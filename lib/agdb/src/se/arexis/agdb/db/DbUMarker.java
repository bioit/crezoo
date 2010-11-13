/*
  $Log$
  Revision 1.9  2005/03/22 16:22:59  heto
  Removing CallableStatement.
  Fixed bugs in GUI

  Revision 1.8  2005/02/23 13:31:26  heto
  Converted database classes to PostgreSQL

  Revision 1.7  2005/02/22 16:23:43  heto
  Converted DbProject to use PostgreSQL

  Revision 1.6  2005/02/22 12:47:48  heto
  Converting *Marker files. Created the DbAbstractMarker to handle common functionallity

  Revision 1.5  2005/02/21 11:55:42  heto
  Converting Genotypes to PostgreSQL

  Revision 1.4  2005/02/17 16:18:58  heto
  Converted DbUMarker to PostgreSQL
  Redesigned relations: r_uvar_var, r_umid_mid and r_uaid_aid due to errors in the design (redundant data in relations)
  This design change affected some views!

  Revision 1.3  2004/04/23 09:48:33  wali
  Added loadMarker

  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.3  2001/04/24 09:34:08  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:43  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.5  2001/04/18 06:41:42  frob
  Changed the superclass to DbObject and added some file type definitions.

  Revision 1.1.1.1.2.4  2001/04/12 09:50:59  frob
  Made the class a subclass to DbAbstractMarker to inherit the file type
  definitions defined in that class.
  Added a static initializer to register a file type definition.

  Revision 1.1.1.1.2.3  2001/03/29 11:12:51  frob
  Changed calls to buildErrorString. All calls now passes the result from
  the dataRow2FileRow method as the row parameter.
  Added header and fixed indentation.

  Revision 1.1.1.1.2.2  2001/03/28 12:52:01  frob
  Changed calls to FileParser.getTitles() to FileParser.columnTitles() and
  FileParser.getRows() to FileParser.dataRows(). 
  Indeted the file and added the log header.

  Revision 1.1.1.1.2.1  2001/03/27 13:00:54  frob
  Modified the file to reflect a change in Mapper.java.
  Added log in header and indented the file correctly.
*/

package se.arexis.agdb.db;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;
import se.arexis.agdb.util.FileImport.*;

import java.util.*;
import java.sql.*;
import se.arexis.agdb.db.DbException;

/**
 * A Class class.
 * <P>
 * @author  <b>Tomas Bj�rklund, Prevas AB</b>, Copyright &#169; 2000
 */
public class DbUMarker extends DbAbstractMarker
{

   static
   {
      try
      {
         // Register known FileTypeDefinitions
         FileTypeDefinitionList.add(FileTypeDefinition.UMARKER,
                                    FileTypeDefinition.LIST, 1);
         FileTypeDefinitionList.add(FileTypeDefinition.UMARKERSET,
                                    FileTypeDefinition.LIST, 1);
         FileTypeDefinitionList.add(FileTypeDefinition.UMARKER,
                                    FileTypeDefinition.MAPPING, 1);
      }
      catch (FileTypeDefinitionException e)
      {
         System.err.println("Construction of new FileTypeDefinition " +
                            "failed: " + e.getMessage());
         System.exit(1);
      }
   }

   /**
    * Constructor
    */
   public DbUMarker() {
   }


   /**
    * Creates a batch of markers from file. The file should be
    * wrapped by a FileParser object and the file praser's <code>
    * Parse</code> method should already have been called.
    */
   public void CreateUMarkers(FileParser fp, Connection conn, int pid, int sid, int id) 
        throws DbException
   {
      String[] titles;
      titles = fp.columnTitles();
      String allele;
      String comm;
      int allelePos = -1;
      boolean isNewFormat = true;
      boolean validHeader = true;

      // The default format of the file is the following format:
      // CHROM MARKER ALIAS COMMENT P1 P2 POSITION ALLELE1 ALLELE2 ALLELE3 etc
      // The new format is as follows:
      // CHROM MARKER ALIAS COMMENT P1 P2 POSITION ALLELE1 ALLELE1COMM ALLELE2 ALLELE2COMM ...

      // Note!!! Although the fields P1 and P2 should be included in the file,
      // we don't store this data for unified markers!

      // Check file format
      if (titles.length < 5)
         validHeader = false;
      else if (!titles[0].equals("CHROMOSOME") ||
               !titles[1].equals("MARKER") ||
               !titles[2].equals("ALIAS") ||
               !titles[3].equals("POSITION") )
         validHeader = false;


      // Find the position in the titles where alleles start
      // According to the fileformat described above the position should be 7,
      // but since it's possible to ommit p1, p2 we shouldn't
      // count ot it.
      for (int i = 0; i < titles.length && allelePos < 0; i++) {
         if (titles[i].toUpperCase().startsWith("ALLELE"))
            allelePos = i;
      }
      if (allelePos < 0)
         validHeader = false;

      if (!validHeader) {
         String errStr="Illegal headers.<BR>"+
            "Required file headers: CHROMOSOME MARKER ALIAS POSITION [PRIMER1] [PRIMER2] COMMENT [ALLELE..]<BR>"+
            "Headers found in file:";
         for (int j=0; j<titles.length;j++) {
            errStr = errStr+ " " + titles[j];
         }
         
         throw new DbException(errStr);
      }

      // There must be an even number of titles for the alleles, if the file
      // includes allele comments
      if (allelePos > 0 && ((titles.length - allelePos) % 2 == 0)) {
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
      if (isNewFormat)
         createMarkersEx2(fp, conn, pid, sid, id);
      else
         createMarkersEx(fp, conn, pid, sid, id);
   }


   private void createMarkersEx(FileParser fp, Connection conn, int pid, int sid, int id) 
        throws DbException
   {
      String chrom, markname, alias, comm, position;
      String[] allele;
      int maxNoOfAlleles;
      int umid;
      String[] titles;
      try 
      {
         titles = fp.columnTitles();
         // The file should be in the format:
         //
         // Chrom. name | Marker name | Alias | Comment | P1 | P2 | POSITION | Allele1 | Allele2 | Allele3 | ... | Allele#
         maxNoOfAlleles = titles.length - 7;
         allele = new String[maxNoOfAlleles];
        

         boolean ok = true;
         for (int i = 0; i < fp.dataRows() && ok; i++)
         {
            chrom = fp.getValue("CHROMOSOME", i);
            markname = fp.getValue("MARKER", i);
            alias = fp.getValue("ALIAS", i);
            comm = fp.getValue("COMMENT", i);
            position = fp.getValue("POSITION", i);
            for (int n = 0; n < allele.length; n++)
            {
               allele[n] = fp.getValue(titles[n + 7], i);
            }
            if (checkMarkerValues(chrom, markname, alias, comm, position, i)) 
            {
               DbChromosome c = new DbChromosome();
               int cid = c.getCID(conn,chrom,sid);
               umid = CreateUMarker(conn, markname, alias, comm, position, pid, cid, id);
            } 
            else 
            {
               ok = false;
               continue;
            }
            
            // Creation of the marker went fine!
            for (int n = 0; n < allele.length && ok; n++) 
            {
               if (allele[n].trim().equals("")) 
               {
                  continue;
               } 
               else if (checkAlleleValues(allele[n], i, n)) 
               {
                  CreateUAllele(conn, allele[n], comm, umid, id);
               } 
               else 
               {
                  ok = false;
                  continue;
               }
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


   private void createMarkersEx2(FileParser fp, Connection conn, int pid, int sid, int id)
        throws DbException
   {
      String chrom, markname, alias, comm, position;
      String[] allele;
      String[] alleleComm;
      int maxNoOfAlleles;
      int umid;
      String[] titles;
      String message = null;
      try 
      {
         titles = fp.columnTitles();
         // The file should be in the format:
         //
         // Chrom Mark Alias Comm P1 P2 POSITION ALLELE1 ALLELE1COMM
         maxNoOfAlleles = (titles.length - 7) / 2;
         allele = new String[maxNoOfAlleles];
         alleleComm = new String[maxNoOfAlleles];
         

         boolean ok = true;
         for (int i = 0; i < fp.dataRows() && ok; i++)
         {
            chrom = fp.getValue("CHROMOSOME", i);
            markname = fp.getValue("MARKER", i);
            alias = fp.getValue("ALIAS", i);
            comm = fp.getValue("COMMENT", i);
            position = fp.getValue("POSITION", i);
            int k = 0;
            for (int n = 7; n < titles.length; n += 2) 
            {
               allele[k] = fp.getValue(titles[n], i);
               alleleComm[k] = fp.getValue(titles[n+1], i);
               k++;
            }
            if (checkMarkerValues(chrom, markname, alias, comm, position, i)) 
            {
               DbChromosome c = new DbChromosome();
               int cid = c.getCID(conn,chrom,sid);
               
               umid = CreateUMarker(conn, markname, alias, comm, position, pid, cid, id);
            } 
            else 
            {
               ok = false;
               continue;
            }
            
            // Creation of the marker went fine!
            for (int n = 0; n < allele.length && ok; n++) 
            {
               if (allele[n].trim().equals("")) 
               {
                  continue;
               } 
               else if (checkAlleleValues(allele[n], i, n)) 
               {
                  CreateUAllele(conn,allele[n], alleleComm[n], umid, id);                  
               } 
               else 
               {
                  ok = false;
                  continue;
               }
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

   
   
   public void loadUMarkers(Connection conn, DataObject db, int speciesId)
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
            
            String sql = "select cname, name from V_U_Markers_3 where sid="+speciesId;
            Errors.logDebug(sql);
            ResultSet rs = stmt.executeQuery(sql);
           
            stmt = conn.createStatement();
            
            String umarker, chromosome;
            while (rs.next() )
            {
                chromosome = rs.getString("cname").trim();
                umarker = rs.getString("name").trim();
                db.setMarker(umarker,chromosome);
            }
        }
        catch (Exception e)
        {
            Errors.logError(e.getMessage());
            e.printStackTrace(System.err);
        }
   }
   
   
   /**
    * Creates a marker
    */
   public int CreateUMarker(Connection conn, String name, String alias,
                             String comm, String position,
                             int pid, int cid, int id) 
                             throws DbException
   {
      Statement stmt = null;
      int umid = 0;
      String sql = "";
      try 
      {
          DbSpecies s = new DbSpecies();
          int sid = s.getSID(conn, cid);
          
          umid = getNextID(conn,"U_Markers_Seq");
          
          sql = "insert into U_Markers (umid,name,alias,comm,position,pid,sid,cid,id,ts) " +
                  "Values("+umid+", "+sqlString(name.toUpperCase())+", "+
                  sqlString(alias)+", "+sqlString(comm)+", "+sqlNumber(position)+", " +
                  pid + ", "+sid+", "+cid+", "+id+", "+getSQLDate()+")";
  
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to create unified marker\n(" +
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
      return umid;
   }
   
   
   /**
    * Updates a marker
    */
   public void UpdateUMarker(Connection conn, int umid, String name, String alias, String comm,
                             String position, int cid, int id)
                             throws DbException
   {
      Statement stmt = null;
      String sql = "";
      String sql_log = "";
      try 
      {
          sql_log = "insert into U_Markers_Log (umid, name, alias, comm, position, cid, id, ts) " +
                  "select umid, name, alias, comm, position, cid, id, ts " +
                  "FROM U_Markers " +
                  "where umid = "+umid;
          stmt = conn.createStatement();
          stmt.execute(sql_log);
          
          sql = "update U_Markers set name = "+sqlString(name.toUpperCase())+", " +
                  "alias = "+sqlString(alias)+", comm = "+sqlString(comm)+", " +
                  "position = "+position+", cid = "+cid+", id = "+id+", ts = "+getSQLDate() +
                  "where umid = "+umid;
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL_LOG="+sql_log);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to update unified marker ["+umid+"]\n(" +
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
    * Delete a marker
    */
   public void DeleteUMarker(Connection conn, int umid) throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          sql = "delete from u_alleles_log where uaid in (select uaid from u_alleles where umid="+umid+"); " +
                  "delete from u_alleles where umid="+umid+"; " +
                  "delete from u_Markers where umid = "+umid;
          
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to delete unified marker\n(" +
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
    * Update a marker set
    */
   public void UpdateUMarkerSet(Connection conn, String markersetName,
                                String comm, int umsid, int id) 
                                throws DbException
   {
      Statement stmt = null;
      String sql_log = "";
      String sql = "";
      try 
      {
          sql_log = "insert into U_Marker_Sets_Log (umsid,name,comm,id,ts) " +
                  "select umsid,name,comm,id,ts " +
                  "FROM U_Marker_Sets " +
                  "where umsid = "+umsid;
         
          stmt = conn.createStatement();
          stmt.execute(sql_log);
          
          sql = "update U_Marker_Sets set name = "+sqlString(markersetName)+", " +
                  "comm = "+sqlString(comm)+", id = "+id+", ts = "+getSQLDate()+" " +
                  "where umsid = "+umsid;
                 
          stmt.execute(sql);
          
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL_LOG="+sql_log);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to update the unified marker set\n(" +
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
    * Delete a marker set
    */
   public void DeleteUMarkerSet(Connection conn, int umsid, int id) 
        throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          sql = "delete from u_marker_sets_log where umsid = "+umsid+" ;" +
                  "delete from u_marker_sets where umsid = "+umsid;
          
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to delete unified marker set\n(" +
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
    * Creates a marker set
    */
   public int CreateUMarkerSet(Connection conn, String name, String comm,
                                int pid, int sid, int id) throws DbException
   {
      Statement stmt = null;
      //int msid;
      
      int umsid = 0;
      String sql = "";
      try 
      {
          umsid = getNextID(conn,"U_Marker_sets_seq");   
          sql = "INSERT INTO U_MARKER_SETS (umsid,name,comm,pid,sid,id,ts) " +
                  "VALUES ("+umsid+", "+sqlString(name)+", "+sqlString(comm)+", "+pid+"," +
                  sid+ ", "+id+", "+getSQLDate()+")";
             
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to create marker set.\n(" +
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
      return umsid;
   }
   
   public void CreateUMarkerSetLinks(Connection conn, int umsid,  
           String[] mrk_ids, int id)
           throws DbException
   {
       for (int i = 0; i < mrk_ids.length; i++) 
       {
           CreateUMarkerSetLink(conn, umsid, Integer.valueOf(mrk_ids[i]), 0.0, id);
       } 
   }
  
   public void CreateUMarkerSetLink(Connection conn,int umsid, int umid, 
           double u_position, int id)
           throws DbException
   {
       Statement stmt = null;
      //int msid;
      
      //int umsid = 0;
      String sql = "";
      try 
      {
          
          sql = "insert into U_Positions values( " +
                  umsid+", "+umid+", "+u_position+", "+id+", "+getSQLDate()+")";
             
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to create marker set link.\n(" +
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
   
    public void DeleteUMarkerSetLinks(Connection conn, int umsid,  
           String[] mrk_ids, int id)
           throws DbException
   {
       for (int i = 0; i < mrk_ids.length; i++) 
       {
           DeleteUMarkerSetLink(conn, umsid, Integer.valueOf(mrk_ids[i]), id);
       } 
   }
   
   public void DeleteUMarkerSetLink(Connection conn,int umsid, int umid, int id)
           throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          
          sql = "delete from U_Positions where umsid = "+umsid+" and umid = "+umid;
             
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
   
   public void UpdateUMarkerSetLink(Connection conn, int umsid, int umid, 
           double pos, int id)
           throws DbException
   {
       Statement stmt = null;
       String sql = "";
       try
       {
           stmt = conn.createStatement();
           sql = "update U_Positions set value = "+pos+", id = "+id+", ts = "+getSQLDate()+" where umsid = "+umsid+" and umid = " +umid;
           stmt.execute(sql);
       }
       catch (Exception e)
       {
           e.printStackTrace();
           throw new DbException("Internal error. Failed to update unified marker set link\n"+e.getMessage());
       }
   }


   /**
    * Creates a marker set and a batch of markers that are included in this set.
    */
   public void CreateUMarkerSets(FileParser fp, Connection conn,
                                 String markersetName, String comm,
                                 int pid, int sid, int id) 
                                 throws DbException
   {
      int umsid;
      String titles[];
      String marker, order;
      boolean ok = true;
      boolean validHeader = true;

      try 
      {
         // Check header
         titles = fp.columnTitles();
         
         if (titles.length != 2)
            validHeader = false;
         else if (!titles[0].equals("MARKER") ||
                  !titles[1].equals("POSITION"))
            validHeader = false;

         if (!validHeader) {
            String errStr="Illegal headers.<BR>"+
               "Required file headers: MARKER POSITION<BR>"+
               "Headers found in file:";
            for (int j=0; j<titles.length;j++) {
               errStr = errStr+ " " + titles[j];
            }
            
            throw new DbException(errStr);
         }
         
         umsid = CreateUMarkerSet(conn, markersetName, comm, pid, sid, id);

         // The marker names should be stored in the file below the headline MARKER
         // and the position number below ORDER
         for (int i = 0; i < fp.dataRows() && ok; i++) 
         {
            marker = fp.getValue("MARKER", i);
            order = fp.getValue("POSITION", i);
            
            double position = 0;
            if (order == null || order.trim().equals(""))
               position = 0;
            else
               position = new Double(order);
            
            
            // get the unified marker id
            int umid = getUMID(conn, marker, pid);
            
            try
            {
                CreateUMarkerSetLink(conn, umsid, umid, position, id);
            }
            catch (Exception e)
            {
                throw new DbException("Failed to create and/or link marker [" + marker +
                                "] to marker set. PL/SQL error [" + e.getMessage()
                                + "] at row "+fp.dataRow2FileRow(i) + 1);
            }
         }
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to create unified marker sets\n(" +
                          e.getMessage() + ")");
      } 
   }


   /**
    * Deletes an allele
    */
   public void DeleteUAllele(Connection conn, int uaid) throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          sql = "delete from u_alleles_log where uaid = "+uaid+" ; " +
                  "delete from U_Alleles where uaid = "+uaid;
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to delete unified allele\n(" +
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
    * Updates an allele.
    */
   public void UpdateUAllele(Connection conn, int id, int uaid,
                             String name, String comm ) 
                             throws DbException
   {
      Statement stmt = null;
      String sql_log ="";
      String sql = "";
      try 
      {
          sql_log = "insert into U_Alleles_log (uaid,name,comm,id,ts)" +
                  "select uaid, name, comm, id, ts " +
                  "from U_Alleles where uaid = "+uaid;
          stmt = conn.createStatement();
          stmt.execute(sql_log);
          
          sql = "update U_Alleles set " +
                  "name = "+sqlString(name)+", comm = "+sqlString(comm)+", " +
                  "id = "+id+", ts = "+getSQLDate()+" " +
                  "where uaid = "+uaid;       
                 
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL_LOG="+sql_log);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to update unified allele\n(" +
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
    * Creates an allele
    */
   public int CreateUAllele(Connection conn, String name,
                             String comm, int umid, int id) 
                             throws DbException
   {
      Statement stmt = null;
      String sql = "";
      int uaid = 0;
      try 
      {
          uaid = getNextID(conn,"U_Alleles_Seq");
          
          sql = "insert into U_Alleles values(" + uaid +
                  ", "+sqlString(name)+", "+sqlString(comm)+", "+umid+", "+id+", "+getSQLDate()+")";
	  
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to create Unified allele\n(" +
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
      return uaid;
   }



   public void CreateMappings(Mapper[] mappers, Connection conn, int pid) 
        throws DbException
   {
      String suname;
      String mname;
      String umname;
      String[] alleles;
      String[] ualleles;
      String aname;
      String uaname;
     
      try 
      {
         int suid = 0;
         int umid = 0;
         int mid = 0;
         
         DbMarker dm = new DbMarker();
         DbGenotype dg = new DbGenotype();
         DbAllele da = new DbAllele();
         
         for (int mappIndex = 0; mappIndex < mappers.length; mappIndex++) 
         {
            suname = mappers[mappIndex].getSUName();
            mname = mappers[mappIndex].getMarker();
            umname = mappers[mappIndex].getUMarker();
            
            
            
            DbSamplingUnit db = new DbSamplingUnit();
            suid = db.getSUID(conn, suname);
            
            umid = getUMID(conn, umname, pid);
            mid = dm.getMID(conn,mname,suid);
            
            CreateUMarkerMapping(conn,umid, mid);
            
            
            
            // Alright, so far everything went fine
            
            
            alleles = mappers[mappIndex].getAlleles();
            ualleles = mappers[mappIndex].getUAlleles();
            int maxAlleles = mappers[mappIndex].alleles();
            
            int aid = 0;
            int uaid = 0;
            for (int alleleIndex = 0; alleleIndex < maxAlleles; alleleIndex++) 
            {
               aname = alleles[alleleIndex];
               uaname = ualleles[alleleIndex];
               if (aname != null && !aname.trim().equals("") &&
                   uaname != null && !uaname.trim().equals("")) 
               {
                  aid  = da.getAID(conn,aname,mid);
                  uaid = getUAID(conn, uaname, umid);
                  
                  try
                  { 
                      CreateUAlleleMapping(conn,pid, uaid, aid);
                  }
                  catch (Exception e)
                  {
                      throw new DbException("Error in block " + (mappIndex + 1) +
                                      ", Mapping [" + umname + "] sampling unit [" + suname + "] marker [" + mname +
                                      "] unified allele [" + uaname + "] allele [" + aname + "].");
                  }
               }
            } // End of for (int alleleIndex...
         } // End of for (int mappIndex...

      }
      catch (DbException e)
      {
          throw e;
      }
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         throw new DbException("Internal error. Failed to create mappings\n(" +
                          e.getMessage() + ")");
      } 
   }


   /**
    * Creates a unified marker mapping
    */
   public void CreateUMarkerMapping(Connection conn, int umid, int mid) 
        throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          sql = " insert into R_UMID_MID (umid,mid,ts) Values( " +
                  umid+", "+mid+", "+getSQLDate()+")";
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to create the unified marker mapping\n(" +
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


   public void DeleteUMarkerMapping(Connection conn, int umid, int mid) 
        throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          sql = "delete from r_uaid_aid where aid = (select aid from alleles where mid="+mid+") and uaid=(select uaid from u_alleles where umid="+umid+") ; ";
          sql += "delete from r_umid_mid where umid="+umid+" and mid="+mid;
          
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to delete Unified Marker mapping\n(" +
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
   
   private int getUAID(Connection conn, String name, int umid)
        throws DbException
   {
       Statement stmt = null;
       String sql = "";
       int uaid = 0;
       try
       {
           stmt = conn.createStatement();
           sql = "select uaid from u_alleles where name="+sqlString(name)+ " and umid="+umid;
	  
           ResultSet rs = stmt.executeQuery(sql);
           
           if (rs.next())
           {
               uaid = rs.getInt("uaid");
           }
           
           if (uaid == 0)
           {
               throw new DbException("Unable to find allele.");
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
       return uaid;
   }
   
   
   
   
   
   private int getUMID(Connection conn, String name, int pid)
        throws DbException
   {
       Statement stmt = null;
       String sql = "";
       int umid = 0;
       try
       {
           stmt = conn.createStatement();
           sql = "select umid from U_Markers where name="+sqlString(name)+ " and pid="+pid;
	  
           ResultSet rs = stmt.executeQuery(sql);
           
           if (rs.next())
           {
               umid = rs.getInt("umid");
           }
           
           if (umid == 0)
           {
               throw new DbException("Unable to read from unified alleles.");
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
       return umid;
   }
   
   /**
    * Get the Unified Marker id given a unified allele id
    */
   private int getUMID(Connection conn, int uaid) throws DbException
   {
       Statement stmt = null;
       String sql = "";
       int umid = 0;
       try
       {
           stmt = conn.createStatement();
           sql = "select umid from U_Alleles where uaid="+uaid;
	  
           ResultSet rs = stmt.executeQuery(sql);
           
           if (rs.next())
           {
               umid = rs.getInt("umid");
           }
           
           if (umid == 0)
           {
               throw new DbException("Unable to read from unified alleles.");
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
       return umid;
   }


   public void CreateUAlleleMapping(Connection conn, int pid, int uaid, int aid) {
      Statement stmt = null;
      String sql = "";
      try 
      {
          int umid = getUMID(conn,uaid);
          sql = "insert into R_UAID_AID Values(" + pid +
                  ", "+umid+", "+aid+", "+uaid+", "+getSQLDate()+")";
	  
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         buildErrorString("Internal error. Failed to Failed to create the mapping link.\n(" +
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


   public void DeleteUAlleleMapping(Connection conn, int pid, int aid) 
        throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          stmt = conn.createStatement();
          sql = "delete from r_uaid_aid " +
                  "where pid="+pid+" and " +
                  "aid="+aid;
          
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to delete Unified Allele mapping\n(" +
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
}
