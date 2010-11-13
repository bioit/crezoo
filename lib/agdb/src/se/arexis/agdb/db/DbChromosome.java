/*
  Copyright (C) 2000 by Prevas AB. All rights reserved.

  $Log$
  Revision 1.8  2005/02/22 12:47:48  heto
  Converting *Marker files. Created the DbAbstractMarker to handle common functionallity

  Revision 1.7  2005/02/21 11:55:42  heto
  Converting Genotypes to PostgreSQL

  Revision 1.6  2005/02/17 16:18:58  heto
  Converted DbUMarker to PostgreSQL
  Redesigned relations: r_uvar_var, r_umid_mid and r_uaid_aid due to errors in the design (redundant data in relations)
  This design change affected some views!

  Revision 1.5  2005/02/08 16:03:21  heto
  DbIndividual is now complete. Some bug tests are done.
  DbSamplingunit is converted. No bugtest.
  All transactions should now be handled in the GUI (yuck..)

  Revision 1.4  2005/02/04 15:58:40  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.3  2004/12/14 08:57:46  heto
  swedish characters changed in comment.

  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.3  2001/04/24 09:34:04  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:38  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.5  2001/04/11 06:35:34  frob
  Update of constant names in FileTypeDefinition caused changes here.

  Revision 1.1.1.1.2.4  2001/04/10 13:11:11  frob
  Changed static initialiser to use constants instead of strings.

  Revision 1.1.1.1.2.3  2001/04/10 11:31:02  frob
  Added a static initilizer which registers the known file type definitions.

  Revision 1.1.1.1.2.2  2001/03/29 11:12:48  frob
  Changed calls to buildErrorString. All calls now passes the result from
  the dataRow2FileRow method
  as the row parameter.
  Added header and fixed indentation.

  Revision 1.1.1.1.2.1  2001/03/28 12:51:58  frob
  Changed calls to FileParser.getTitles() to FileParser.columnTitles()
  and FileParser.getRows() to FileParser.dataRows().
  Indeted the file and added the log header.

*/


package se.arexis.agdb.db;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;
import se.arexis.agdb.util.FileImport.*;
import java.sql.*;
import se.arexis.agdb.db.TableClasses.ChromosomeDO;
import java.util.ArrayList;

/**
 * This class provides methods for objects in the database
 * that has something to do with chromosomes.
 *
 * @author <b>Tomas Björklund, Prevas AB</b>, Copyright &#169; 2000
 * @version 1.0, 2000-11-28
 */
public class DbChromosome extends DbObject
{

   static
   {
      try
      {
         // Register known FileTypeDefinitions
         FileTypeDefinitionList.add(FileTypeDefinition.CHROMOSOME,
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
     * Deletes a chromosome. If an error
     * occurs, an exception is thrown.
     *
     * @param conn a valid connection to the database
     * @param cid the chromosome id to be deleted
     */
    public void DeleteChromosome(Connection conn, int cid) 
        throws DbException
    {
        Errors.logDebug("DeleteChromosome");
        
        PreparedStatement pstmt = null;
    
        String sql = "";
        String sql_l_markers = "";
        String sql_u_markers = "";
        String sql_markers = "";
        try 
        {
            // Delete_Chromosome
            //sql += "select * from gdbadm.l_markers where cid=1001";

            // Delete_L_Marker lmid	
            sql_l_markers = "select lmid from gdbadm.l_markers where cid="+cid;
            
            sql += "delete from l_alleles where lmid in ("+sql_l_markers+");";
            sql += "delete from l_markers where lmid in ("+sql_l_markers+");";

            // Delete_U_Marker umid	
            sql_u_markers = "select umid from gdbadm.u_markers where cid="+cid;
            sql += "delete from gdbadm.u_alleles_log where uaid in (select uaid from u_alleles where umid in ("+sql_u_markers+"));";
            sql += "delete from gdbadm.u_alleles where umid in ("+sql_u_markers+");";
            sql += "delete from gdbadm.u_markers where umid in ("+sql_u_markers+");";

            // Delete_Marker mid	
            sql_markers = "select mid from gdbadm.markers where cid="+cid;
            sql += "delete from gdbadm.alleles_log where aid in (select aid from alleles where mid in ("+sql_markers+"));";
            sql += "delete from gdbadm.alleles where mid in ("+sql_markers+");";
            sql += "delete from gdbadm.genotypes_log where mid in ("+sql_markers+");";
            sql += "delete from gdbadm.genotypes where mid in ("+sql_markers+");";
            sql += "delete from gdbadm.markers where cid="+cid+";";
            
            sql += "delete from chromosomes where cid="+cid+";";

            pstmt = conn.prepareStatement(sql);
            
            //pstmt.addBatch(sql);
            //pstmt.execute();
            pstmt.execute();
            
            Errors.logDebug("Affected rows = "+pstmt.getUpdateCount());
            

            
            //System.err.println(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("SQL="+sql);
            buildErrorString("Server error: "+e.getMessage());
            Errors.logError(e.toString());
            
            throw new DbException("Unable to delete chromosome and dependent information");
        }
        finally 
        {
            try 
            {
                if (pstmt != null) pstmt.close();
             } 
             catch (SQLException ignored) {}
        }
  }
   
   /**
    * Updates a chromosome.
    *
    * @param conn a valid connection to the database
    * @param cid chromosome id to be updated
    * @param new_name the new name of the chromosome
    * @param new_comm the new comment of the chromosome
    * @parame id the user id
    */
   public void UpdateChromosome(Connection conn, int cid, String new_name, String new_comm) 
        throws DbException
   {
       Statement stmt = null;
       String sql = "";
       try
       {
           stmt = conn.createStatement();
           
           checkValues(new_name, new_comm);
           sql = "update Chromosomes set name = '"+new_name+"', comm = '"+new_comm+"' where cid = "+cid;
       }
       catch (DbException e)
       {
           throw e;
       }
       catch (Exception e)
       {
           e.printStackTrace();
           Errors.logError("Error: "+sql);
           
           throw new DbException("Error updating Chromosome ["+cid+"]");
       }
       finally 
       {
           try
           {
                stmt.close();
           }
           catch (Exception ignore)
           {}
       }
   }


   /**
    * Creates a new chromosome.
    *
    * @param conn a valid connection to the database
    * @param name the name of the created chromosome
    * @param comm the new comment of the chromosome
    * @param sid the species id
    * @parame id the user id
    */
   public void CreateChromosome(Connection conn, String name, String comm, int sid) 
        throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          checkValues(name,comm);
          stmt = conn.createStatement();
          
          sql = "insert into Chromosomes Values ((nextval('Chromosomes_Seq')) , '"+name+"', '"+comm+"', '"+sid+"')";
          stmt.execute(sql);
    
      }
      catch (Exception e)
      {
          e.printStackTrace();
          Errors.logError("SQL="+sql);
          
          try
          {
              Errors.logWarn("Setting Sequence at current max value");
              stmt.execute("SELECT setval('Chromosomes_Seq', max(cid)) FROM Chromosomes");              
          }
          catch (Exception seq)
          {
              seq.printStackTrace();
              Errors.logError("Setting sequence failed: "+seq.getMessage());
          }
          
          throw new DbException("Error storing data to the database");
      }
      finally
      {
          try
          {
              stmt.close();
          }
          catch (Exception ignore)
          {}
      }       
   }


   
   /**
    * Creates a batch of chromosomes
    *
    * @param fp a FileParser object that has been initialized
    * with the file containing the chromosomes. That is the method
    * <code>parse</code> has alredy been called.
    * @param conn a valid connection to the database
    * @param cid chromosome id to be updated
    * @parame id the user id
    */
   public void CreateChromosomes(FileParser fp, Connection conn, int sid) 
        throws DbException
   {
       try
       {
            String name, comm;
            String titles[];
       
            titles = fp.columnTitles();
            int rows = fp.dataRows();
            
            
            // Check file format
            if (titles.length != 2) 
            {
                throw new DbException("Unknown file format. The title row must be in the form " +
                             "[CHROMOSOME (delimeter) COMMENT]");
            } 
            else if (!titles[0].equals("CHROMOSOME") || !titles[1].equals("COMMENT")) 
            {
                throw new DbException("Unknown file format. The title row must be in the form " +
                             "[CHROMOSOME (delimeter) COMMENT]");
            }
            for (int i = 0; i < rows ; i++) 
            {
                name = fp.getValue("CHROMOSOME", i);
                comm = fp.getValue("COMMENT", i);
                
                checkValues(name, comm, fp.dataRow2FileRow(i) + 1);                
                CreateChromosome(conn,name,comm,sid);
            }
       }
       catch (DbException e)
       {
           e.printStackTrace();
           throw e;
       }
       catch (Exception e)
       {
           e.printStackTrace();
           throw new DbException("Error creating chromosomes. Please contact the system administrator");
       }
   }
   
   /**************************************************************
    * This method checks the parameters 'name' and 'comm' before
    * the callable statement is executed. If the method detects
    * an error it builds up the message attribute and returns
    * false.
    */
   private void checkValues(String name, String comm) 
        throws DbException
   {
      //boolean ret = true;
      if (name == null || name.trim().equals("")) 
      {
         throw new DbException("Unable to read the chromosome name");
      } 
      else if (name.length() > 2) 
      {
          throw new DbException("Name [" + name + "] exceeds 2 character");
      } 
      else if (comm == null) 
      {
          throw new DbException("Unable to read the comment");         
      } 
      else if (comm.length() > 256) 
      {
          throw new DbException("The comment [" + comm.substring(0, 16) + "...] " +
                          "exceeds 255 characters");
      }
   }

   
   
   /**************************************************************
    * This method checks the parameters 'name' and 'comm' before
    * the callable statement is executed. If the method detects
    * an error it builds up the message attribute and returns
    * false.
    */
   private void checkValues(String name, String comm, int row)
        throws DbException
   {
      
      if (name == null || name.trim().equals(""))
      {
          throw new DbException("Unable to read the name at row " + row);
      }
      else if (name.length() > 2)
      {
          throw new DbException("Name [" + name + "] exceeds 2 character at row " + row);
      }
      else if (comm == null)
      {
          throw new DbException("Unable to read the comment at row " + row);
      }
      else if (comm.length() > 256)
      {
          throw new DbException("The comment [" + comm.substring(0, 16) + "...] exceeds 256 characters at row " + row);
      }
   }
   
   public int getCID(Connection conn, String name, int sid)
        throws DbException
   {
       Statement stmt = null;
       String sql = "";
       int cid = 0;
       try
       {
           stmt = conn.createStatement();
           sql = "select cid from chromosomes where name="+sqlString(name)+ " and sid="+sid;
	  
           ResultSet rs = stmt.executeQuery(sql);
           
           if (rs.next())
           {
               cid = rs.getInt("cid");
           }
           
           if (cid == 0)
           {
               throw new DbException("Unable to find chromosome.");
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
       return cid;
   }
   
   /**
     * Get all chromosomes for a species
     * @param conn The database connection
     * @param speciesid The species id of interest.
     * @return Returns an array of ChromosomeDO objects.
     */
    public ChromosomeDO getChromosome(Connection conn, int cid)
    {
        
        
        ChromosomeDO out = null;
        try
        {
            Statement sqlStatement = null;
            ResultSet rs = null;

            sqlStatement = conn.createStatement();
            rs = sqlStatement.executeQuery("SELECT cid, NAME, comment, sid " +
                    "FROM gdbadm.Chromosomes WHERE cid=" + cid);

            while (rs.next())
            {
                out = new ChromosomeDO(rs.getInt("cid"), rs.getString("NAME"),
                        rs.getString("comment"), rs.getInt("sid"));
            }
            
            rs.close();
            sqlStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return out;
    }
   
   
   /**
     * Get all chromosomes for a species
     * @param conn The database connection
     * @param speciesid The species id of interest.
     * @return Returns an array of ChromosomeDO objects.
     */
    static public ChromosomeDO[] getChromosomes(Connection conn, int speciesid)
    {
        ArrayList out = null;
        try
        {
            out = new ArrayList();
        
            Statement sqlStatement = null;
            ResultSet rs = null;

            sqlStatement = conn.createStatement();
            rs = sqlStatement.executeQuery("SELECT cid, NAME, comment, sid " +
                    "FROM gdbadm.Chromosomes WHERE sid=" + 
                    speciesid + " order by NAME");

            while (rs.next())
            {
                out.add(new ChromosomeDO(rs.getInt("cid"), rs.getString("NAME"),
                        rs.getString("comment"), rs.getInt("sid")));
            }
            
            rs.close();
            sqlStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return (ChromosomeDO[])out.toArray();
    }
}
