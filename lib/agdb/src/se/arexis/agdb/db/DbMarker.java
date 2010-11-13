/*
  Copyright (C) 2000 by Prevas AB. All rights reserved.

  $Log$
  Revision 1.11  2005/03/24 15:12:44  heto
  Working with removing oracle dep.

  Revision 1.10  2005/03/22 16:22:59  heto
  Removing CallableStatement.
  Fixed bugs in GUI

  Revision 1.9  2005/02/22 16:23:43  heto
  Converted DbProject to use PostgreSQL

  Revision 1.8  2005/02/22 12:47:48  heto
  Converting *Marker files. Created the DbAbstractMarker to handle common functionallity

  Revision 1.7  2005/02/21 11:55:42  heto
  Converting Genotypes to PostgreSQL

  Revision 1.6  2004/05/11 08:57:27  wali
  Added logInfo

  Revision 1.5  2004/03/09 14:19:21  heto
  Fixed alot of bugs in else if clauses then checking syntax for values

  Revision 1.4  2003/11/05 07:41:53  heto
  Added debug message
  Trimmed values

  Revision 1.3  2003/05/08 13:00:39  heto
  Added functions to support the check-system for file import.

  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.3  2001/04/24 09:34:07  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:41  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.4  2001/04/18 06:39:58  frob
  Reverted the previous change, this class now inherits DbObject and implements
  its own version of the static initializer.

  Revision 1.1.1.1.2.3  2001/04/12 07:17:49  frob
  Changed super class to DbAbstractMarker to inherit static initialiser (for
  registering valid file type definitions)

  Revision 1.1.1.1.2.2  2001/03/29 11:12:50  frob
  Changed calls to buildErrorString. All calls now passes the result from
  the dataRow2FileRow method as the row parameter.
  Added header and fixed indentation.

  Revision 1.1.1.1.2.1  2001/03/28 12:52:00  frob
  Changed calls to FileParser.getTitles() to FileParser.columnTitles()
  and FileParser.getRows() to FileParser.dataRows().
  Indeted the file and added the log header.


*/
package se.arexis.agdb.db;

import se.arexis.agdb.util.*;
import se.arexis.agdb.util.FileImport.*;
import java.sql.*;
import se.arexis.agdb.db.TableClasses.AlleleDO;
import se.arexis.agdb.db.TableClasses.MarkerDO;

/**
 * This class provides methods for objects in the database
 * that has something to do with markers and library markers.
 *
 * @author <b>Tomas Bj�rklund, Prevas AB</b>, Copyright &#169; 2000
 * @version 1.0, 2000-11-28
 */
public class DbMarker extends DbAbstractMarker
{


   static
   {
      try
      {
         // Register known FileTypeDefinitions
         FileTypeDefinitionList.add(FileTypeDefinition.MARKER,
                                    FileTypeDefinition.LIST, 1);
         FileTypeDefinitionList.add(FileTypeDefinition.MARKERSET,
                                    FileTypeDefinition.LIST, 1);
      }
      catch (FileTypeDefinitionException e)
      {
         System.err.println("Construction of new FileTypeDefinition " +
                            "failed: " + e.getMessage());
         System.exit(1);
      }
   }


   /**
    * Default constructoor
    */
   public DbMarker() {
   }

   /**
    * Creates a batch of markers from file. The file should be
    * wrapped by a FileParser object and the file praser's <code>
    * Parse</code> method should already have been called.
    */
   public void CreateMarkers(FileParser fp, Connection conn, int suid, int id) 
        throws DbException
   {
      String[] titles;
      titles = fp.columnTitles();
      String allele;
      String comm;
      int allelePos = -1;
      boolean isNewFormat = true;
      boolean ok = true;

      // The default format of the file is the following format:
      // CHROM MARKER ALIAS COMMENT P1 P2 POSITION ALLELE1 ALLELE2 ALLELE3 etc
      // The new format is as follows:
      // CHROM MARKER ALIAS COMMENT P1 P2 POSITION ALLELE1 ALLELE1COMM ALLELE2 ALLELE2COMM ...

      // check fileformat
      if (titles.length < 7)
         ok = false;

      if (ok) {
         if( !titles[0].equals("CHROMOSOME") ||
            !titles[1].equals("MARKER") ||
            !titles[2].equals("ALIAS") ||
            !titles[3].equals("POSITION") ||
            !titles[4].equals("PRIMER1") ||
            !titles[5].equals("PRIMER2") ||
            !titles[6].equals("COMMENT"))
            ok = false;
      }
      if (ok) {
         for (int i = 7; i < titles.length && ok; i++)
            if (!titles[i].startsWith("ALLELE"))
               ok = false;
      }
      if (!ok) {
         String errStr="Illegal headers.<BR>"+
            "Required file headers: CHROMOSOME MARKER ALIAS POSITION PRIMER1 PRIMER2 COMMENT [ALLELE#...]<BR>"+
            "Headers found in file:";
         for (int j=0; j<titles.length;j++) {
            errStr = errStr+ " " + titles[j];
         }
         throw new DbException(errStr);
      }

      // Find the position in the titles where alleles start
      // According to the fileformat described above the position should be 7,
      // but since it's possible to ommit p1, p2 and position, we shouldn't
      // count ot it.
      for (int i = 0; i < titles.length && allelePos < 0 && ok; i++) {
         if (titles[i].toUpperCase().startsWith("ALLELE"))
            allelePos = i ;
      }
      // There must be an even number of titles for the alleles, if the file
      // includes allele comments
      if (allelePos > 0 && ((titles.length - allelePos) % 2 == 0 && ok)) {
         for (int i = allelePos; i < titles.length && isNewFormat; i+=2) {
            allele = titles[i];
            comm = titles[i+1];
            if ( !( comm.indexOf(allele) == 0 &&
                   comm.substring(allele.length()).equals("COMM")) )
               isNewFormat = false;
         }
      } else {
         isNewFormat = false;
      }
      if (ok) {
         if (isNewFormat)
            createMarkersEx2(fp, conn, suid, id);
         else
            createMarkersEx(fp, conn, suid, id);
      }
   }

   private void createMarkersEx(FileParser fp, Connection conn, int suid, int id)
        throws DbException
   {
      String chrom, markname, alias, comm, p1, p2, position;
      String[] allele;
      int maxNoOfAlleles;
      int mid;
      String[] titles;
      try 
      {
         titles = fp.columnTitles();
         // The file should be in the format:
         //
         // Chrom. name | Marker name | Alias | Comment | P1 | P2 | POSITION | Allele1 | Allele2 | Allele3 | ... | Allele#
         maxNoOfAlleles = titles.length - 7;
         allele = new String[maxNoOfAlleles];
         
         for (int i = 0; i < fp.dataRows(); i++)
         {
            chrom = fp.getValue("CHROMOSOME", i);
            markname = fp.getValue("MARKER", i);
            alias = fp.getValue("ALIAS", i);
            comm = fp.getValue("COMMENT", i);
            p1 = fp.getValue("P1", i);
            p2 = fp.getValue("P2", i);
            position = fp.getValue("POSITION", i);
            
            for (int n = 0; n < allele.length; n++){
               allele[n] = fp.getValue(titles[n + 7], i);
            }
            
            checkMarkerValues(chrom, markname, alias, p1, p2, position,
                                  comm, fp.dataRow2FileRow(i) + 1);
               
            DbSamplingUnit s = new DbSamplingUnit();
            int sid = s.getSID(conn, suid);

            DbChromosome c = new DbChromosome();
            int cid = c.getCID(conn,chrom,sid);

            mid = CreateMarker(conn, markname, alias, comm, p1, p2, position, suid, cid, id);

            DbAllele a = new DbAllele();            
            for (int n = 0; n < allele.length; n++) 
            {
               if (allele[n].trim().equals("")) 
               {
                  continue;
               } 
               
               checkAlleleValues(allele[n], i, n);
               a.CreateAllele(conn, allele[n], comm, mid, id);
            }
         }
      } 
      catch (DbException e)
      {
          throw e;
      }
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         throw new DbException("Internal error. Failed to create markers\n(" +
                          e.getMessage() + ")");
      }
   }

   private void createMarkersEx2(FileParser fp, Connection conn, int suid, int id)
        throws DbException
   {
      String chrom, markname, alias, comm, p1, p2, position;
      String[] allele;
      String[] alleleComm;
      int maxNoOfAlleles;
      int mid;
      String[] titles;
      try 
      {
         titles = fp.columnTitles();
         // The file should be in the format:
         //
         // Chrom Mark Alias Comm P1 P2 POSITION ALLELE1 ALLELE1COMM
         maxNoOfAlleles = (titles.length - 7) / 2;
         allele = new String[maxNoOfAlleles];
         alleleComm = new String[maxNoOfAlleles];

         for (int i = 0; i < fp.dataRows(); i++)
         {
            chrom = fp.getValue("CHROMOSOME", i);
            markname = fp.getValue("MARKER", i);
            alias = fp.getValue("ALIAS", i);
            comm = fp.getValue("COMMENT", i);
            p1 = fp.getValue("P1", i);
            p2 = fp.getValue("P2", i);
            position = fp.getValue("POSITION", i);
            int k = 0;
            for (int n = 7; n < titles.length; n += 2) {
               allele[k] = fp.getValue(titles[n], i);
               alleleComm[k] = fp.getValue(titles[n+1], i);
               k++;
            }

            checkMarkerValues(chrom, markname, alias, p1, p2, position,
                                  comm, fp.dataRow2FileRow(i) + 1);
               
               
            DbSamplingUnit s = new DbSamplingUnit();
            int sid = s.getSID(conn, suid);

            DbChromosome c = new DbChromosome();
            int cid = c.getCID(conn,chrom,sid);

            mid = CreateMarker(conn, markname, alias, comm, p1, p2, position, suid, cid, id);           
            
            DbAllele a = new DbAllele();
            for (int n = 0; n < allele.length; n++) 
            {
               if (allele[n].trim().equals("")) 
               {
                  continue;
               } 
               
               checkAlleleValues(allele[n], i, n);
               a.CreateAllele(conn, allele[n], comm, mid, id);
            }
         }
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         throw new DbException("Internal error. Failed to create markers\n(" +
                          e.getMessage() + ")");
      } 
   }

   /**
    * Creates a marker
    */
   public int CreateMarker(Connection conn, String name, String alias,
                            String comm, String p1, String p2, String position,
                            int suid, int cid, int id) 
                            throws DbException
   {
      Statement stmt = null;
      int mid = 0;
      String sql = "";
      try 
      {
          checkMarkerValues(name, alias, p1, p2, position, comm, 0);
          
          mid = getNextID(conn,"Markers_seq");
          
          sql = "insert into Markers Values("+mid+", "+sqlString(name.toUpperCase())+", "+
                  sqlString(alias)+", "+sqlString(comm)+", "+suid+", "+cid+", "+
                  sqlString(p1)+", "+sqlString(p2)+", "+sqlNumber(position)+", "+id+", "+getSQLDate()+")";
          
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         buildErrorString("Internal error. Failed to create marker\n(" +
                          sqle.getMessage() + ")");
      } 
      finally 
      {
         try {
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
      return mid;
   }
   
   /**
    * Updates a marker
    */
   public void UpdateMarker(Connection conn, int mid, String name, String alias, String comm,
                            String p1, String p2, String position, int cid, int id)
                            throws DbException
   {
      Statement stmt = null;
      String sql_log = "";
      String sql = "";
      try 
      {
          sql_log = "insert into Markers_Log (name, alias, comm, p1, p2," +
                  "position, cid, id, ts) select name, alias, comm, p1, p2," +
                  "position, cid, id, ts FROM Markers where mid = "+mid;
          
          stmt = conn.createStatement();
          stmt.execute(sql_log);
          
          sql = "update Markers set name = "+sqlString(name.toUpperCase())+", " +
                  "alias = "+sqlString(alias)+", comm = "+sqlString(comm)+", " +
                  "id = "+id+", ts = "+getSQLDate()+", p1 = "+sqlString(p1)+", " +
                  "p2 = "+sqlString(p2)+", position = "+sqlNumber(position)+", " +
                  "cid = "+cid+" where mid = "+mid;
          
          stmt.execute(sql);
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         buildErrorString("Internal error. Failed to call PL/SQL procedure\n(" +
                          sqle.getMessage() + ")");
      } finally {
         try {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) {}
      }
   }
   
   /**
    * Delete a marker
    */
   public void DeleteMarker(Connection conn, int mid) 
        throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          sql = "delete from alleles_log where aid in (select aid from alleles where mid=p_mid);" +
                  "delete from alleles where mid = p_mid; " +
                  "delete from genotypes_log where mid = p_mid; " +
                  "delete from genotypes where mid = p_mid; " +
                  "delete from Markers where mid = p_mid";
          
          stmt = conn.createStatement();
          stmt.execute(sql);
          
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to delete marker\n(" +
                          sqle.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) {}
      }
   }
   
   /**
    * Copy a library marker and all its alleles to a new marker
    */
   public void CopyLibMarker(Connection conn, int lmid, int suid, int id) 
        throws DbException
   {
      try 
      {
          //  Read data from the library marker
          DbLMarker lmarkers = new DbLMarker();
          MarkerDO lm = lmarkers.getLMarker(conn, lmid);
                  
          // Get the unique marker id for the new marker
          // Create the new marker
          int mid = CreateMarker(conn, lm.getName(), lm.getAlias(), 
                  lm.getComment(), lm.getPrimer1(), lm.getPrimer2(), 
                  Float.toString(lm.getPosition()), suid, lm.getCID(), id);
          
          AlleleDO[] la = lmarkers.getLAlleles(conn, lmid);
          
          DbAllele alleles = new DbAllele();
          
          // Copy all library alleles to the new marker
          for (int i=0;i<la.length;i++)        
          {
                alleles.CreateAllele(conn, la[i].getName(), la[i].getComment(), mid, id);
          }
      }
      catch (DbException e)
      {
          throw e;
      }
      catch (Exception e) 
      {    
         e.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to copy library marker\n(" +
                          e.getMessage() + ")");
      }
   }
   
   /**
    * Update a marker set
    */
   public void UpdateMarkerSet(Connection conn, String markersetName,
                               String comm, int msid, int id) 
                               throws DbException
   {
      Statement stmt = null;
      String sql_log = "";
      String sql = "";
      try 
      {
          sql_log = "insert into Marker_Sets_Log (name, comm, id, ts) " +
                  "select name, comm, id, ts " +
                  "from Marker_Sets " +
                  "where msid = "+msid;
          stmt = conn.createStatement();
          stmt.execute(sql_log);
          
          sql = "update Marker_Sets set name = "+sqlString(markersetName)+", " +
                  "comm = "+sqlString(comm)+", " +
                  "id = "+id+", ts = "+getSQLDate()+" " +
                  "where msid = " + msid;
          stmt.execute(sql);
          
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to update marker set\n(" +
                          sqle.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) {}
      }
   }
   
   /**
    * Delete a marker set
    */
   public void DeleteMarkerSet(Connection conn, int msid, int id) 
        throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          sql = "delete from Marker_sets_log where msid = p_msid; " +
                  "delete from Marker_Sets  where msid = p_msid";
          
          stmt = conn.createStatement();
          stmt.execute(sql);
          
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         throw new DbException("Internal error. Failed to delete marker set\n(" +
                          sqle.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) {}
      }
   }
   
   /**
    * Creates a marker set
    */
   public int CreateMarkerSet(Connection conn, String name, String comm,
                               int suid, int id) 
                               throws DbException
   {
      Statement stmt = null;
      int msid = 0;
      String sql = "";
      try 
      {
          msid = getNextID(conn,"Marker_Sets_Seq");
          
          sql = "insert into MARKER_SETS values("+msid+", "+sqlString(name)+", "+sqlString(comm)+", "+suid+", "+id+", "+getSQLDate()+")";
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to create marker set\n(" +
                          sqle.getMessage() + ")");
      } 
      finally 
      {
         try {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) {}
      }
      return msid;
   }
   
   public void CreateMarkerSetLinks(Connection conn, int msid,  
           String[] mrk_ids, int id)
           throws DbException
   {
       for (int i = 0; i < mrk_ids.length; i++) 
       {
           CreateMarkerSetLink(conn, msid, Integer.valueOf(mrk_ids[i]), 0.0, id);
       } 
   }
   
   public void CreateMarkerSetLink(Connection conn,int msid, int mid, double position, int id)
        throws DbException
   {
       Statement stmt = null;
       String sql = "";
       try
       {
           sql = "insert into Positions values("+msid+", "+mid+", "+position+", "+id+", "+getSQLDate()+")";
           stmt = conn.createStatement();
           stmt.execute(sql);
       }
       catch (Exception e)
       {
           e.printStackTrace();
           
           throw new DbException("Internal error. Failed to create marker set link\n(" +
                          e.getMessage() + ")");
       }
   }
   
   public void DeleteMarkerSetLinks(Connection conn, int msid,  
           String[] mrk_ids, int id)
           throws DbException
   {
       for (int i = 0; i < mrk_ids.length; i++) 
       {
           DeleteMarkerSetLink(conn, msid, Integer.valueOf(mrk_ids[i]), id);
       } 
   }
   
   public void DeleteMarkerSetLink(Connection conn,int msid, int mid, int id)
           throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          
          sql = "delete from Positions where msid = "+msid+" and mid = "+mid;
             
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to delete marker set link.\n(" +
                          e.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored)
         {}
      }
   }
   
   public void UpdateMarkerSetLink(Connection conn, int msid, int mid, 
           double pos, int id)
           throws DbException
   {
       Statement stmt = null;
       String sql = "";
       try
       {
           stmt = conn.createStatement();
           sql = "update Positions set value = "+pos+", id = "+id+", ts = "+getSQLDate()+" where msid = "+msid+" and mid = " +mid;
           stmt.execute(sql);
       }
       catch (Exception e)
       {
           e.printStackTrace();
           throw new DbException("Internal error. Failed to update marker set link\n"+e.getMessage());
       }
   }

   
   /**
    * Creates a marker set and a batch of markers that are included in this set.
    */
   public void CreateMarkerSets(FileParser fp, Connection conn,
                                String markersetName, String comm,
                                int suid, int id) 
                                throws DbException
   {
      int msid;
      String marker, order;
      String[] titles;
      Errors.logInfo("DbMarker.createMarkerSets() started");
      boolean ok = true;
      try 
      {
         //check fileformat
         titles = fp.columnTitles();
         if (titles.length != 2){
            ok = false;
         }
         if(ok)
         {
            if (!titles[0].equals("MARKER") ||
                !titles[1].equals("POSITION"))
            {
               ok = false;
            }
         }

         if(!ok){
            String errStr="Illegal headers.<BR>"+
               "Required file headers: MARKER POSITION<BR>"+
               "Headers found in file:";
            for (int j=0; j<titles.length;j++)
            {
               errStr = errStr+ " " + titles[j];
            }
            throw new DbException(errStr);
         }
         
         
        msid = CreateMarkerSet(conn, markersetName, comm, suid, id);

        // The marker names should be stored in the file below the headline MARKER
        // and the position number below ORDER
        DbMarker dbMarker = new DbMarker();
        for (int i = 0; i < fp.dataRows() && ok; i++) 
        {
           marker = fp.getValue("MARKER", i);
           order = fp.getValue("POSITION", i);

           int mid = dbMarker.getMID(conn, marker, suid);        
           try
           {
                CreateMarkerSetLink(conn, msid, mid, Float.valueOf(order).floatValue(), id);
           }
           catch (Exception e)
           {
               throw new DbException("Failed to create and/or link marker [" + marker +
                               "] to marker set. PL/SQL error [" +
                               e.getMessage() + "] at row "+ fp.dataRow2FileRow(i) + 1);
           }
        }

         Errors.logInfo("DbMarker.createMarkerSets() ended");
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to create marker sets\n(" +
                          e.getMessage() + ")");
      } 
   }
   
   /**
    * Deletes an allele
    */
   public void DeleteAllele(Connection conn, int aid) throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
            sql = "update genotypes set aid1 = null where aid1 = "+aid+"; " +
                    "update genotypes set aid2 = null where aid2 = "+aid;
                    
            stmt = conn.createStatement();
            stmt.execute(sql);
            
            sql = "delete from alleles_log where aid = "+aid; 
            stmt.execute(sql);
            
            sql = "delete from alleles where aid = "+aid;
            stmt.execute(sql);
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to delete allele\n(" +
                          sqle.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) {}
      }
   }
   
   /**
    * Updates an allele.
    */
   public void UpdateAllele(Connection conn, int id, int aid,
                            String name, String comm ) 
                            throws DbException
   {
      Statement stmt = null;
      String sql_log = "";
      String sql = "";
      try 
      {
          sql_log = "insert into Alleles_Log (aid, name, comm, id, ts) " +
                  "select aid, name, comm, id, ts " +
                  "from Alleles " +
                  "where aid = "+aid;
          
          stmt = conn.createStatement();
          stmt.execute(sql_log);
          
          sql = "update Alleles set name = "+sqlString(name)+", " +
                  "comm = "+sqlString(comm)+", id = "+id+", ts = "+getSQLDate()+" " +
                  "where aid = "+aid;
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to update allele\n(" +
                          e.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) 
         {}
      }
   }
   
 
  
  
   
   /**
     * Load the individuals to the test-objects
     */
    public void loadAlleles(Connection conn, DataObject db, int suid)
    {
        Statement stmt;
       
        try
        {
            stmt = conn.createStatement();
            
            /*
             *SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele1+"'" +
                                " AND SUID=" +"'"+suid+"'");
            */
            
            String sql = "select mname, name from v_alleles_3 where suid="+suid;
            ResultSet rs = stmt.executeQuery(sql);
           
            stmt = conn.createStatement();
            
            String marker, allele;
            while (rs.next() )
            {
                marker = rs.getString("mname");
                allele = rs.getString("name");
                
                db.setAllele(marker,allele);
            }
        }
        catch (Exception e)
        {
            Errors.logError(e.getMessage());
            e.printStackTrace(System.err);
        }
   }
    
    /**
     * Load the individuals to the test-objects
     */
    public void loadMarkers(Connection conn, DataObject db, int suid)
    {
        Statement stmt;
       
        try
        {
            stmt = conn.createStatement();
            
            /*
             *SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele1+"'" +
                                " AND SUID=" +"'"+suid+"'");
            */
            
            String sql = "select cname, name from V_MARKERS_3 where suid="+suid;
            Errors.logDebug(sql);
            ResultSet rs = stmt.executeQuery(sql);
           
            stmt = conn.createStatement();
            
            String marker, chromosome;
            while (rs.next() )
            {
                chromosome = rs.getString("cname").trim();
                marker = rs.getString("name").trim();
                
                db.setMarker(marker,chromosome);
            }
        }
        catch (Exception e)
        {
            Errors.logError(e.getMessage());
            e.printStackTrace(System.err);
        }
   }
    
    
    public int getMID(Connection conn, String name, int suid)
        throws DbException
   {
       Statement stmt = null;
       String sql = "";
       int mid = 0;
       try
       {
           stmt = conn.createStatement();
           sql = "select mid from Markers where name="+sqlString(name)+ " and suid="+suid;
	  
           ResultSet rs = stmt.executeQuery(sql);
           
           if (rs.next())
           {
               mid = rs.getInt("mid");
           }
           
           if (mid == 0)
           {
               throw new DbException("Unable to read from markers.");
           }
       }
       catch (DbException e)
       {
           throw e;
       }
       catch (Exception e)
       {
           e.printStackTrace();
           Errors.logError("SQL="+sql);
       }
       return mid;
   }
    
    public MarkerDO getMarker(Connection conn, int mid) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        MarkerDO out = null;
        try
        {
            stmt = conn.createStatement();
            sql = "select * from markers where mid = "+mid;
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next())
            {                   
                out = new MarkerDO(rs.getInt("mid"),
                        rs.getString("name"),
                        rs.getString("alias"),
                        rs.getFloat("position"),
                        rs.getString("p1"),
                        rs.getString("p2"),
                        rs.getString("comm"),
                        rs.getInt("cid"));
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
            
            throw new DbException("Unable to get marker ["+mid+"]");
        }       
        return out;
    }
}
