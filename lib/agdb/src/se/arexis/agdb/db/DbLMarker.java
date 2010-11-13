/*
  Copyright (C) 2000 by Prevas AB. All rights reserved.

  $Log$
  Revision 1.5  2005/02/25 15:08:23  heto
  Converted Db*Variable.java to PostgreSQL

  Revision 1.4  2005/02/22 12:47:48  heto
  Converting *Marker files. Created the DbAbstractMarker to handle common functionallity

  Revision 1.3  2004/12/14 08:58:55  heto
  swedish characters changed in comment.

  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.4  2001/06/15 07:21:25  roca
  First attemp at LINKAGE post Makeped
  A family view avaliable in viewGeno

  Revision 1.3  2001/04/24 09:34:07  frob
  Moved file import classes to new package se.arexis.agdb.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:40  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.3  2001/04/18 06:40:49  frob
  Reverted the previous change, the class now inherits DbObject and implements
  its own version of the static initializer.

  Revision 1.1.1.1.2.2  2001/04/12 05:31:37  frob
  Changed the super class to AbstractMarker. The new superclass is common for
  all Marker classes and registers the known file type definitions.
  Changed the calls to checkMarkerValues to use the method dataRow2FileRow in
  the file parser to calculate the correct line number errors occours on.

  Revision 1.1.1.1.2.1  2001/03/28 12:52:00  frob
  Changed calls to FileParser.getTitles() to FileParser.columnTitles()
  and FileParser.getRows() to FileParser.dataRows().
  Indeted the file and added the log header.


*/

package se.arexis.agdb.db;

import se.arexis.agdb.util.*;
import se.arexis.agdb.util.FileImport.*;
import java.sql.*;
import java.util.ArrayList;
import se.arexis.agdb.db.TableClasses.AlleleDO;
import se.arexis.agdb.db.TableClasses.ChromosomeDO;
import se.arexis.agdb.db.TableClasses.LAlleleDO;
import se.arexis.agdb.db.TableClasses.MarkerDO;

/**
 * This class provides methods for objects in the database
 * that has something to do with markers and library markers.
 *
 * @author <b>Tomas Björklund, Prevas AB</b>, Copyright &#169; 2000
 * @version 1.0, 2000-12-11
 */
public class DbLMarker extends DbAbstractMarker
{

   static
   {
      try
      {
         // Register known FileTypeDefinitions
         FileTypeDefinitionList.add(FileTypeDefinition.LMARKER,
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
   public DbLMarker() {
   }

   /**
    * Creates a batch of library markers from file. The file should be
    * wrapped by a FileParser object and the file praser's <code>
    * Parse</code> method should already have been called.
    */
   public void CreateLMarkers(FileParser fp, Connection conn, int sid) 
        throws DbException
   {
      String[] titles;
      titles = fp.columnTitles();
      String allele;
      String comm;
      int allelePos = -1;
      boolean isNewFormat = true;

      // The default format of the file is the following format:
      // CHROM MARKER ALIAS COMMENT P1 P2 POSITION ALLELE1 ALLELE2 ALLELE3 etc
      // The new format is as follows:
      // CHROM MARKER ALIAS COMMENT P1 P2 POSITION ALLELE1 ALLELE1COMM ALLELE2 ALLELE2COMM ...


      // Find the position in the titles where alleles start
      // According to the fileformat described above the position should be 7,
      // but since it's possible to ommit p1, p2 and position, we shouldn't
      // count ot it.
      for (int i = 0; i < titles.length && allelePos < 0; i++) 
      {
         if (titles[i].toUpperCase().startsWith("ALLELE"))
            allelePos = i;
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
      } else 
      {
         isNewFormat = false;
      }
      if (allelePos < 0) 
      {
         throw new DbException("Unknown file format");
      } 
      else 
      {
         if (isNewFormat)
            createMarkersEx2(fp, conn, sid);
         else
            createMarkersEx(fp, conn, sid);
      }
   }

   private void createMarkersEx(FileParser fp, Connection conn, int sid)
        throws DbException
   {
      String chrom, markname, alias, comm, p1, p2, position;
      String[] allele;
      int maxNoOfAlleles;
      int lmid;
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
            
            for (int n = 0; n < allele.length; n++)
            {
               allele[n] = fp.getValue(titles[n + 7], i);
            }
            
            checkMarkerValues(chrom, markname, alias, comm, position,
                                  fp.dataRow2FileRow(i) + 1);
            
            DbChromosome chr = new DbChromosome();
            int cid = chr.getCID(conn, chrom,sid);
               
            lmid = CreateLMarker(conn, markname, alias, comm, p1, p2, position, sid, cid);
            
            // Create alleles
            for (int n = 0; n < allele.length; n++) 
            {
               if (allele[n].trim().equals("")) 
               {
                  continue;
               } 
               
               checkAlleleValues(allele[n], i, n); 
               CreateLAllele(conn, allele[n], null, lmid);
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

   private void createMarkersEx2(FileParser fp, Connection conn, int sid)
        throws DbException
   {
      String chrom, markname, alias, comm, p1, p2, position;
      String[] allele;
      String[] alleleComm;
      int maxNoOfAlleles;
      int lmid;
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
            
            for (int n = 7; n < titles.length; n += 2) 
            {
               allele[k] = fp.getValue(titles[n], i);
               alleleComm[k] = fp.getValue(titles[n+1], i);
               k++;
            }
            
            checkMarkerValues(chrom, markname, alias, comm, position,
                                  fp.dataRow2FileRow(i) + 1);
               
            DbChromosome chr = new DbChromosome();
            int cid = chr.getCID(conn, chrom,sid);

            lmid = CreateLMarker(conn, markname, alias, comm, p1, p2, position, sid, cid);

            // Create alleles
            for (int n = 0; n < allele.length; n++) 
            {
               if (allele[n].trim().equals("")) 
               {
                  continue;
               } 
               
               checkAlleleValues(allele[n], i, n);                  
               CreateLAllele(conn, allele[n], alleleComm[n], lmid);
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
   
   public void CopySUMarker(Connection conn, int mid) throws DbException
   {
      int lmid;
      try 
      {
          DbMarker mark = new DbMarker();
          MarkerDO m = mark.getMarker(conn, mid);
          
          DbChromosome chr = new DbChromosome();
          ChromosomeDO c = chr.getChromosome(conn, m.getCID());
          
          // Create the library marker
          lmid = CreateLMarker(conn, m.getName(), m.getAlias(), m.getComment(), 
                  m.getPrimer1(), m.getPrimer2(), Float.toString(m.getPosition()), c.getSID(), m.getCID());
                    
          DbAllele a = new DbAllele();
          AlleleDO[] alleles = a.getAlleles(conn, mid);
          
          for (int i=0;i<alleles.length;i++)
          {
              CreateLAllele(conn, alleles[i].getName(), 
                      alleles[i].getComment(), 
                      lmid);
          }
      } 
      catch (DbException e)
      {
          throw e;
      }
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to copy sampling unit marker\n(" +
                          e.getMessage() + ")");
      } 
   }

   public int CreateLMarker(Connection conn, String name, String alias,
                             String comm, String p1, String p2, String position,
                             int sid, int cid) 
                             throws DbException
   {
      Statement stmt = null;
      int lmid = 0;
      String sql = "";
      try 
      {
          checkMarkerValues(name, alias, position, comm, 0);
          
          lmid = getNextID(conn, "L_Markers_Seq");
          
          sql = "insert into L_Markers(lmid, name, alias, comm, sid, cid, p1, " +
                  "p2, position)  Values(" +
                  lmid+", "+sqlString(name.toUpperCase())+", "+sqlString(alias)+", " +
                  sqlString(comm)+", "+sid+",  "+cid+", "+sqlString(p1)+", "+sqlString(p2)+", "+sqlString(position)+")";
                  
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (DbException e)
      {
          throw e;
      }
      catch (Exception e)
      {
          e.printStackTrace();
          Errors.logError("SQL="+sql);
          
          throw new DbException("Failed to create Library Marker: \n"+e.getMessage());
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
      return lmid;
   }
   
   /**
    * Updates a library marker
    */
   public void UpdateLMarker(Connection conn, int lmid, String name, String alias, String comm,
                             String p1, String p2, String position, int cid)
                             throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          sql = "update L_Markers set name = "+sqlString(name.toUpperCase())+", " +
                  "alias = "+sqlString(alias)+", comm = "+sqlString(comm)+", " +
                  "p1 = "+sqlString(p1)+", p2 = "+sqlString(p2)+", position = "+sqlNumber(position)+", cid = "+cid+" " +
                  "where lmid = "+lmid;
          
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to update library marker\n(" +
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
   public void DeleteLMarker(Connection conn, int lmid) 
    throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          sql = "delete from l_alleles where lmid = "+lmid+"; " +
                  "delete from l_markers where lmid = "+lmid;
         
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to delete library marker.\n(" +
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
    * Deletes a library allele
    */
   public void DeleteLAllele(Connection conn, int laid) 
        throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          sql = "delete from l_alleles where laid = "+laid;
          stmt = conn.createStatement();
          stmt.execute(sql);
          
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to delete library allele\n(" +
                          sqle.getMessage() + ")");
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
   public void UpdateLAllele(Connection conn, int laid,
                             String name, String comm ) 
                             throws DbException
   {
      Statement stmt = null;
      String sql = "";
      
      try 
      {
          sql = "update L_Alleles set name = "+sqlString(name)+", comm = "+sqlString(comm)+" where laid = "+laid;
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to update library allele\n(" +
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
   
   public int getLMID(Connection conn, String name, int sid)
        throws DbException
   {
       Statement stmt = null;
       String sql = "";
       int lmid = 0;
       try
       {
           stmt = conn.createStatement();
           sql = "select lmid from L_Markers where name="+sqlString(name)+ " and sid="+sid;
	  
           ResultSet rs = stmt.executeQuery(sql);
           
           if (rs.next())
           {
               lmid = rs.getInt("lmid");
           }
           
           if (lmid == 0)
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
       return lmid;
   }
    
   
   
   /**
    * Creates a library allele
    */
   public void CreateLAllele(Connection conn,
                             String name, String comm,
                             String marker, int sid) 
                             throws DbException
   {
       DbLMarker dbLMarker = new DbLMarker();
       
       int lmid = dbLMarker.getLMID(conn, name, sid);
       
       CreateLAllele(conn,name,comm,lmid);
   
   }
   
   /**
    * Creates a library allele
    */
   public void CreateLAllele(Connection conn,
                             String name, String comm,
                             int lmid) 
                             throws DbException
   {
      int laid = 0;
      Statement stmt = null;
      String sql = "";
      try 
      {
          laid = getNextID(conn,"L_Alleles_Seq");
          sql = "insert into L_Alleles (laid, name, comm, lmid) values("+laid+", "+sqlString(name)+", "+sqlString(comm)+", "+lmid+")";
          stmt = conn.createStatement();
          stmt.execute(sql);
          
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to create library allele\n(" +
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
     * Get the library allele data from the database.
     * @param conn the database connection
    
     * @throws se.arexis.agdb.db.DbException throws error messages to the UI
     * @return returns the allele dependent object.
     */
    public LAlleleDO[] getLAlleles(Connection conn, int lmid) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        ArrayList out = null;
        try
        {
            out = new ArrayList();
            
            stmt = conn.createStatement();
            
            sql = "select * from l_alleles where lmid="+lmid;
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next())
            {                   
                out.add(new LAlleleDO(rs.getInt("aid"),rs.getString("name"),rs.getString("comment"), rs.getInt("lmid")));
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
            
            throw new DbException("Unable to get alleles for lmid=["+lmid+"]");
        }
            
        return (LAlleleDO[])out.toArray();
    }
   
   
   public MarkerDO getLMarker(Connection conn, int lmid) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        MarkerDO out = null;
        try
        {
            stmt = conn.createStatement();
            sql = "select * from l_markers where lmid = "+lmid;
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next())
            {                   
                out = new MarkerDO(rs.getInt("lmid"),
                        rs.getString("name"),
                        rs.getString("alias"),
                        rs.getFloat("position"),
                        rs.getString("p1"),
                        rs.getString("p2"),
                        rs.getString("comment"),
                        rs.getInt("cid"));
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
            
            throw new DbException("Unable to get library marker ["+lmid+"]");
        }
            
        return out;
    }  
}
