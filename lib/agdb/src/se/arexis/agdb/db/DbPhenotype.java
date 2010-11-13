/*
  Copyright (C) 2000 by Prevas AB. All rights reserved.

  $Log$
  Revision 1.10  2005/03/22 16:22:59  heto
  Removing CallableStatement.
  Fixed bugs in GUI

  Revision 1.9  2005/03/03 15:41:37  heto
  Converting for using PostgreSQL

  Revision 1.8  2005/02/23 13:31:26  heto
  Converted database classes to PostgreSQL

  Revision 1.7  2005/02/22 16:23:43  heto
  Converted DbProject to use PostgreSQL

  Revision 1.6  2005/02/08 16:03:21  heto
  DbIndividual is now complete. Some bug tests are done.
  DbSamplingunit is converted. No bugtest.
  All transactions should now be handled in the GUI (yuck..)

  Revision 1.5  2004/05/11 08:57:46  wali
  Added logInfo

  Revision 1.4  2004/03/09 14:19:21  heto
  Fixed alot of bugs in else if clauses then checking syntax for values

  Revision 1.3  2004/03/02 09:27:22  heto
  Added method for loading data to testobjects

  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.3  2001/04/24 09:34:08  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:42  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.3  2001/04/12 08:29:46  frob
  Added static initializer which registers file type definitions.

  Revision 1.1.1.1.2.2  2001/03/29 11:12:50  frob
  Changed calls to buildErrorString. All calls now passes the result
  from the dataRow2FileRow method as the row parameter.
  Added header and fixed indentation.

  Revision 1.1.1.1.2.1  2001/03/28 12:52:01  frob
  Changed calls to FileParser.getTitles() to FileParser.columnTitles()
  and FileParser.getRows() to FileParser.dataRows().
  Indeted the file and added the log header.


*/
package se.arexis.agdb.db;

import se.arexis.agdb.util.*;
import java.sql.*;
import se.arexis.agdb.db.TableClasses.PhenotypeDO;
import se.arexis.agdb.util.FileImport.*;

/**
 * This class provides methods for objects in the database
 * that have something to do with phenotypes.
 *
 * @author <b>Tomas Bj�rklund, Prevas AB</b>, Copyright &#169; 2000
 * @version 1.0, 2000-10-06
 */
public class DbPhenotype extends DbObject
{

   static
   {
      try
      {
         // Register known FileTypeDefinitions
         FileTypeDefinitionList.add(FileTypeDefinition.PHENOTYPE,
                                    FileTypeDefinition.LIST, 1);
         FileTypeDefinitionList.add(FileTypeDefinition.PHENOTYPE,
                                    FileTypeDefinition.MATRIX, 1);
      }
      catch (FileTypeDefinitionException e)
      {
         System.err.println("Construction of new FileTypeDefinition " +
                            "failed: " + e.getMessage());
         System.exit(1);
      }
   }

   /**
    * Default constructor
    */
   public DbPhenotype() 
   {
   }
   
   private void checkPhenotypeMatrixFileFormat(String[] titles)
        throws DbException
   {
       boolean validHeader = true;
       
       // check fileformat
         if (titles.length < 2) {
            validHeader = false;
         } else if(!titles[0].equals("IDENTITY") && !titles[0].equals("ALIAS") ) {
            validHeader = false;
         }
         if(!validHeader){
            String errStr="Illegal headers.<BR>"+
               "Required file headers: IDENTITY/ALIAS VARIABLE1 VARIABLE2 ...<BR>"+
               "Headers found in file:";
            for (int j=0; j<titles.length;j++)
            {
               errStr = errStr+ " " + titles[j];
            }
            throw new DbException(errStr);
         }
   }
   
   private void checkPhenotypeListFileFormat(String[] titles)
    throws DbException
   {
       boolean validHeader = true;
       
       // check fileformat
         if (titles.length != 6){
            validHeader = false;
         } else if (!(titles[0].equals("IDENTITY") || titles[0].equals("ALIAS")) ||
                    !titles[1].equals("VARIABLE") ||
                    !titles[2].equals("VALUE") ||
                    !titles[3].equals("DATE") ||
                    !titles[4].equals("REFERENCE") ||
                    !titles[5].equals("COMMENT")) {
            validHeader = false;
         }
         if(!validHeader){
            String errStr="Illegal headers.<BR>"+
               "Required file headers: IDENTITY VARIABLE VALUE DATE REFERENCE COMMENT<BR>"+
               "Headers found in file:";
            for (int j=0; j<titles.length;j++)
            {
               errStr = errStr+ " " + titles[j];
            }
            throw new DbException(errStr);
         }
   }
   
    public void CreatePhenotypesList(FileParser fp, Connection conn,
                                    int suid, int id) 
                                    throws DbException
    {
        CommonPhenotypesList(fp, conn, suid, id, "create") ;
    }
   
    /**
     * Creates a batch of phenotypes from file.
     */
    /*
    public void CreatePhenotypesList(FileParser fp, Connection conn,
                                    int suid, int id) 
                                    throws DbException
    {    
        String ind, name, value, ref, comm;
        String temp=null; // A temporary container for the tissue date belove
        String[] titles;
        
        Errors.logInfo("DBPhenotype.CreatePhenotypeList(...) started");
        try 
        {
            titles = fp.columnTitles();
            checkPhenotypeListFileFormat(titles);
         
            DbIndividual inds = new DbIndividual();
            DbVariable vars = new DbVariable();
            int iid = 0;
            int vid = 0;

            for (int i = 0; i < fp.dataRows(); i++) 
            {
               ind = fp.getValue(titles[0], i);
               name = fp.getValue("VARIABLE", i);
               value = fp.getValue("VALUE", i);
               temp = fp.getValue("DATE", i);
               ref = fp.getValue("REFERENCE", i);
               comm = fp.getValue("COMMENT", i);

               checkValues(ind, name, value, temp, ref, comm,
                                fp.dataRow2FileRow(i) + 1);

               if (value == null || value.trim().equals(""))
                  continue;

               if (titles[0].equals("IDENTITY")) 
                    iid = inds.getIID(conn, ind, null, suid);
               else
                    iid = inds.getIID(conn, null, ind, suid);

               vid = vars.getVID(conn, name, suid);

               CreatePhenotype(conn, iid, vid, suid, value, temp, ref, comm, id);
            }

            Errors.logInfo("DBPhenotype.CreatePhenotypeList(...) ended");
        } 
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            throw new DbException("Internal error. Failed to call PL/SQL procedure\n(" +
                          e.getMessage() + ")");
        } 
    }
     */
   
    
   
    
    /**
     * Updates a batch of phenotypes fromfile.
     */
    private void CommonPhenotypesList(FileParser fp, Connection conn, int suid, 
            int id, String mode) 
            throws DbException
    {
        String ind, indId, name, value, ref, comm;
        String temp; // A temporary container for the tissue date belove
        int sid;
        java.sql.Date tissue_date;
        
        String[] titles;
        String message = null;
        boolean validHeader = true;
        boolean knownIdentity = true;
        try 
        {
            titles = fp.columnTitles();
            checkPhenotypeListFileFormat(titles);
            
            
            boolean ok = true;
            
            DbIndividual inds = new DbIndividual();
            DbVariable vars = new DbVariable();
            
            int iid = 0;
            int vid = 0;
            
            PhenotypeDO phenotype = null;
            
            for (int i = 0; i < fp.dataRows() && ok; i++)
            {
                ind = fp.getValue(titles[0], i);
                name = fp.getValue("VARIABLE", i);
                value = fp.getValue("VALUE", i);
                temp = fp.getValue("DATE", i);
                ref = fp.getValue("REFERENCE", i);
                comm = fp.getValue("COMMENT", i);
                
                checkValues(ind, name, value, temp, ref, comm,
                                 fp.dataRow2FileRow(i) + 1);
                
                
                if (titles[0].equals("Identity"))
                    iid = inds.getIID(conn, ind, null, suid);
                else
                    iid = inds.getIID(conn, null, ind, suid);
                
                vid = vars.getVID(conn, name, suid);
                
                if (mode.equals("update"))
                    UpdatePhenotype(conn, vid, iid, ref, temp, value, comm, id);
                else if (mode.equals("create"))
                    CreatePhenotype(conn, iid, vid, suid, value, temp, ref, comm, id);
                
                else if (mode.equals("create_or_update"))
                {
                    phenotype = getPhenotype(conn, vid, iid);
                    
                    if (phenotype==null)
                        UpdatePhenotype(conn, vid, iid, ref, temp, value, comm, id);
                    else
                        CreatePhenotype(conn, iid, vid, suid, value, temp, ref, comm, id);
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
            
            throw new DbException("Internal error. Failed to call PL/SQL procedure\n(" +
                          e.getMessage() + ")");
        } 
   }
    
    
    /**
    * Updates a batch of phenotypes fromfile.
    */
    public void UpdatePhenotypesList(FileParser fp, Connection conn, int suid, 
            int id) throws DbException
    {
        CommonPhenotypesList(fp, conn, suid, id, "update") ;
    }
    
    
   
    
    public void CreateOrUpdatePhenotypesList(FileParser fp, Connection conn,
                                            int suid, int id)
                                            throws DbException
    {
        CommonPhenotypesList(fp, conn, suid, id, "create_or_update") ;
    }
    

 
   
   /**
    * Creates a phenotype
    */
   public void CreatePhenotype(Connection conn, int iid, int vid, int suid,
                               String value, String date, String ref, 
                                String comm, int id) 
                                throws DbException
   {
       Statement stmt = null;
       String sql = "";
       try
       {
           stmt = conn.createStatement();
           sql = "insert into Phenotypes values( " +
                   vid+", "+iid+", "+suid+", "+sqlString(value)+", "+sqlString(date)+", " +
                   sqlString(ref)+", "+id+", "+getSQLDate()+", "+sqlString(comm)+")";
           stmt.execute(sql);
       }
       catch (Exception e)
       {
           e.printStackTrace();
           
           throw new DbException("Internal error. Failed to create phenotype\n" + e.getMessage());
       }
       finally
       {
           try
           {
               if (stmt != null)
                   stmt.close();
           }
           catch (Exception e)
           {}
       }
   }


   /**
    * Creates a phenotype
    */
   public void CreatePhenotype(Connection conn, int suid,
                               String identity, String ref, String name, String date,
                               String value, String comm, int id) 
                               throws DbException
   {
          // Find vid for this variable
          DbVariable var = new DbVariable();
          int vid = var.getVID(conn,name,suid);
                   
          // Find iid for this individual
          DbIndividual inds = new DbIndividual();
          int iid = inds.getIID(conn, identity, null, suid);
          
          // Insert the new phenotype
          CreatePhenotype(conn, iid, vid, suid, value, date, ref, comm, id);
   }
   
   /**
    * Deletes a phenotype.
    */
   public void DeletePhenotype(Connection conn, int vid, int iid) 
        throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          sql = "delete from phenotypes_log where vid = "+vid+" and iid = "+iid+"; " +
                  "delete from Phenotypes where vid = "+vid+" and iid = "+iid;
          stmt = conn.createStatement();
          stmt.execute(sql);
          
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         throw new DbException("Internal error. Failed to delete phenotype\n(" +
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
    * Updates a phenotype
    */
   public void UpdatePhenotype(Connection conn, int vid,
                               int iid, String ref, String date,
                               String value, String comm, int id) 
                               throws DbException
   {
      Statement stmt = null;
      String sql = "";
      String sql_log = "";
      try 
      {
          // Log the old data
          sql_log = "insert into Phenotypes_Log (value, date_, reference, id, ts, comm)" +
                  "select value, date_, reference, id, ts, comm " +
                  "from Phenotypes " +
                  "where vid = "+vid+" and iid = "+iid;
          stmt = conn.createStatement();
          stmt.execute(sql);
          
          
          sql = "update Phenotypes set " +
                    "value = "+sqlString(value)+", " +
                    "date_ = "+sqlString(date)+", " +
                    "reference = "+sqlString(ref)+", " +
                    "id = "+id+", ts = "+getSQLDate()+", " +
                    "comm = "+sqlString(comm)+" " +
                  "where vid = "+vid+" and iid = "+iid;
          stmt.execute(sql);
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to update phenotype\n(" +
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
   
   private boolean checkValues(String identity, String name,
                               String value, String date,
                               String ref, String comm, int row) 
                               throws DbException
   {
      boolean rc = true;
      
      if (identity == null || identity.trim().equals(""))
      {
         throw new DbException("Identity is null at row "+row);
      }
      else if (identity.length() > 11)
      {
         throw new DbException("Identity [" + identity + "] exceeds 11 characters at row "+ row);
      }
      
      if (name == null || name.trim().equals(""))
      {
         throw new DbException("Variable name is null at row "+ row);
      }
      else if (name.length() > 20)
      {
         throw new DbException("Variable name [" + name + "] exceeds 20 characters at row "+ row);
      }
      
      if (value == null || value.trim().equals(""))
      {
         ;// that's ok! buildErrorString("Value is missig.", row);
         ; // rc = false;
      }
      else if (value.length() > 20)
      {
         throw new DbException("Value [" + value + "] exceeds 20 characters at row "+ row);
      }
      
      if (date == null || date.trim().equals(""))
      {
         ; // OK!!! buildErrorString("Date is null at row " + row, row);
      }
      else if (date.length() != 10)
      {
         throw new DbException("Date not in the format 'YYYY-MM-DD' at row "+ row);
      }
      else if (date.length() == 10)
      {
         try {
            java.util.Date temp = java.sql.Date.valueOf(date);
         }
         catch (Exception e)
         {
            throw new DbException("Date not in the format 'YYYY-MM-DD' at row "+ row);
         }
      }
      
      if (ref != null && ref.length() > 32)
      {
         throw new DbException("Reference [" + ref + "] exceeds 32 characters at row " +row);
      }
      if (comm != null && comm.length() > 256) {
         throw new DbException("Comment exceeds 256 characters at row "+ row);
      }
      
      return rc;
   }
   
   public void CreatePhenotypesMatrix(FileParser fp, Connection conn,
                                      int suid, int id) 
                                      throws DbException
   {
       CommonPhenotypesMatrix(fp,conn,suid,id,"create");
   }
   
   private void CommonPhenotypesMatrix(FileParser fp, Connection conn,
                                      int suid, int id, String mode) 
                                      throws DbException
   {
      String ind, value, indId;
      
     
      String[] titles;
      String variables[];
      String variable;
     
      boolean ok = true;

      try 
      {
         titles = fp.columnTitles();
         checkPhenotypeMatrixFileFormat(titles);
         
         variables = new String[titles.length-1];
         for (int i = 0; i < variables.length; i++)
            variables[i] = titles[i+1];

        DbIndividual inds = new DbIndividual();
        DbVariable vars = new DbVariable();

        int iid = 0;
        int vid = 0;
        PhenotypeDO phenotype = null;

        for (int row = 0; row < fp.dataRows() && ok; row++)
        {
           ind = fp.getValue(titles[0], row);
           

           for (int vNum = 0; vNum < variables.length; vNum++) 
           {
              variable = variables[vNum];
              value = fp.getValue(variable, row);

              checkValues(ind, variable, value, null, null, null,
                               fp.dataRow2FileRow(row) + 1);

              if (value == null || value.trim().equals(""))
                 continue;

              if (titles[0].equals("Identity"))
                iid = inds.getIID(conn, ind, null, suid);
              else
                iid = inds.getIID(conn, null, ind, suid);

              vid = vars.getVID(conn, variable, suid);

              if (mode.equals("update"))
                  UpdatePhenotype(conn, vid, iid, null, null, value, null, id);
              else if (mode.equals("create"))
                  CreatePhenotype(conn, iid, vid, suid, value, null, null, null, id);
                
              else if (mode.equals("create_or_update"))
              {
                  phenotype = getPhenotype(conn, vid, iid);
                    
                  if (phenotype==null)
                    UpdatePhenotype(conn, vid, iid, null, null, value, null, id);
                  else
                    CreatePhenotype(conn, iid, vid, suid, value, null, null, null, id);
              }
           }
        }// End of row loop   
      } 
      catch (DbException e)
      {
          throw e;
      }
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to call PL/SQL procedure\n(" +
                          e.getMessage() + ")");
      } 
   }
   
   
   public void UpdatePhenotypesMatrix(FileParser fp, Connection conn, int suid, 
           int id) throws DbException
   {
       CommonPhenotypesMatrix(fp,conn,suid,id,"update");
   }
   
   

   public void CreateOrUpdatePhenotypesMatrix(FileParser fp, Connection conn,
                                              int suid, int id) 
                                              throws DbException
                                              
   {
       CommonPhenotypesMatrix(fp,conn,suid,id,"create_or_update");
   }

  

    /**
     * Load the Phenotypes to the test-objects
     */
    public void loadPhenotype(Connection conn, DataObject db, int suid)
    {
        Statement stmt;
       
        try
        {
            stmt = conn.createStatement();
         
            String sql = "select identity, name as variable from V_Phenotypes_3 where suid="+suid;
            ResultSet rs = stmt.executeQuery(sql);
           
            stmt = conn.createStatement();
            
            String identity, variable;
            while (rs.next() )
            {
                identity = rs.getString("identity");
                variable   = rs.getString("variable");
                
                db.setPhenotype(variable,identity);
            }
        }
        catch (Exception e)
        {
            Errors.logError(e.getMessage());
            e.printStackTrace(System.err);
        }
   }
    
     /**
     * Get the allele data from the database.
     * @param conn the database connection
     * @param aid the unique allele id
     * @throws se.arexis.agdb.db.DbException throws error messages to the UI
     * @return returns the allele dependent object.
     */
    public PhenotypeDO getPhenotype(Connection conn, int vid, int iid) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        PhenotypeDO out = null;
        try
        {
            stmt = conn.createStatement();
            sql = "select * from phenotype where vid = "+vid+" and iid="+iid;
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next())
            {                   
                out = new PhenotypeDO(rs.getInt("iid"),
                        rs.getInt("vid"),
                        rs.getString("value"), 
                        rs.getString("type"), 
                        rs.getString("unit"),
                        rs.getString("comm"));
                
               
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
            
            throw new DbException("Unable to get phenotype ["+vid+","+iid+"]");
        }
        return out;
    }
   
}
