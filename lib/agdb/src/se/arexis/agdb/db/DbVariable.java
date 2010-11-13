/*

  $Log$
  Revision 1.5  2005/02/25 15:08:23  heto
  Converted Db*Variable.java to PostgreSQL

  Revision 1.4  2005/02/23 13:31:26  heto
  Converted database classes to PostgreSQL

  Revision 1.3  2004/02/25 13:55:26  heto
  Added import of variable data file

  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.3  2001/04/24 09:34:10  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:44  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.3  2001/04/18 13:00:38  frob
  Added static initializer which registers known file type definitions.

  Revision 1.1.1.1.2.2  2001/03/29 11:12:52  frob
  Changed calls to buildErrorString. All calls now passes the result
  from the dataRow2FileRow method as the row parameter.
  Added header and fixed indentation.

  Revision 1.1.1.1.2.1  2001/03/28 12:52:02  frob
  Changed calls to FileParser.getTitles() to FileParser.columnTitles()
  and FileParser.getRows() to FileParser.dataRows().
  Indeted the file and added the log header.

*/

package se.arexis.agdb.db;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;
import se.arexis.agdb.util.FileImport.*;

import java.sql.*;

/**
 * This class provides methods for objects in the database
 * that in some way or another
 *
 * @author <b>Tomas Bj�rklund, Prevas AB</b>, Copyright &#169; 2000
 * @version 1.0, 2000-10-12
 */
public class DbVariable extends DbAbstractVariable
{

   static
   {
      try
      {
         // Register known FileTypeDefinitions
         FileTypeDefinitionList.add(FileTypeDefinition.VARIABLE,
                                    FileTypeDefinition.LIST, 1);
         FileTypeDefinitionList.add(FileTypeDefinition.VARIABLESET,
                                    FileTypeDefinition.LIST, 1);
      }
      catch (FileTypeDefinitionException e)
      {
         System.err.println("Construction of new FileTypeDefinition " +
                            "failed: " + e.getMessage());
         System.exit(1);
      }
   }


   public DbVariable() 
   {
   }
   
   /**
    * The file should be in the format:
    * NAME | TYPE | UNIT | Comment |
    */
   private void checkVariableHeader(String[] titles)
        throws DbException
   {
       boolean ok = true;
       
       if(titles.length != 4)
         {
            ok = false;
         }
         if (ok)
         {
            if (!titles[0].equals("VARIABLE") ||
                !titles[1].equals("TYPE") ||
                !titles[2].equals("UNIT") ||
                !titles[3].equals("COMMENT"))
            {
               ok = false;
            }
         }

         if(!ok){
            String errStr="Illegal headers.<BR>"+
               "Required file headers: VARIABLE TYPE UNIT COMMENT<BR>"+
               "Headers found in file:";
            for (int j=0; j<titles.length;j++)
            {
               errStr = errStr+ " " + titles[j];
            }
            throw new DbException(errStr);
         }
   }

   public void CreateVariables(FileParser fp, Connection conn, int suid, int id)
        throws DbException
   {
      String name, type, unit, comm;
      String[] titles;
      try {

         // check the fileformat:
         titles = fp.columnTitles();
         checkVariableHeader(titles);
         
        for (int i = 0; i < fp.dataRows(); i++) 
        {
           name = fp.getValue("VARIABLE", i);
           type = fp.getValue("TYPE", i);
           unit = fp.getValue("UNIT", i);
           comm = fp.getValue("COMMENT", i);

           CreateVariable(conn, suid, name, type, unit, comm, id);
        }
         
      } 
      catch (Exception e) {
         e.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to create variables\n(" +
                          e.getMessage() + ")");
      } 
   }
   
   public int CreateVariable(Connection conn, int suid, String name,
                              String type, String unit, String comm,
                              int id) 
                              throws DbException
   {
      Statement stmt = null;
      int vid = 0;
      String sql = "";
      try 
      {
          checkVariableValues(name, type, unit, comm);
          
          vid = getNextID(conn,"Variables_seq");
          
          sql = "insert into Variables Values " +
                  "("+vid+", "+sqlString(name)+", "+sqlString(type)+", "+
                  sqlString(unit)+"," + sqlString(comm)+", "+suid+", "+id+", "+
                  getSQLDate()+")";
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to create variable\n(" +
                          sqle.getMessage() + ")");
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
      return vid;
   }
   
   public void DeleteVariable(Connection conn, int vid, int id) 
        throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          /*
           -- Delete Phenotype logs
           -- delete Phenotypes
           -- Delete variables logs
           -- Delete R_UVid_Vid (delete cascade uvid, vid)
           -- Delete R_Var_Set (delete cascade vsid, vid)
           */

          sql = "delete from phenotypes_log where vid = "+vid+"; " +
                   "delete from phenotypes where vid = "+vid+"; " +
                   "delete from variables_log where vid = "+vid+"; " +
                   "delete from Variables where vid = "+vid;
           
           stmt = conn.createStatement();
           stmt.execute(sql);
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to delete variable\n(" +
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
   
   
   public void UpdateVariable(Connection conn, int vid, String name,
                              String type, String unit, String comm,
                              int id) throws DbException
   {
      Statement stmt = null;
      String sql = "";
      String sql_log = "";
      try 
      {
          checkVariableValues(name, type, unit, comm);
          
          sql_log = "insert into Variables_Log (vid, name, type, unit, comm, id, ts) " +
                  "select vid, name, type, unit, comm, id, ts " +
                  "FROM Variables " +
                  "where vid = "+vid;
          stmt = conn.createStatement();
          stmt.execute(sql_log);
          
          sql = "update Variables set name = "+sqlString(name)+", type = "+sqlString(type)+", " +
                  "unit = "+sqlString(unit)+", comm = "+sqlString(comm)+", id = "+id+", ts = "+getSQLDate()+" " +
                  "where vid = "+vid;
          stmt.execute(sql);
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         Errors.logError("SQL_LOG="+sql_log);
         Errors.logError("SQL="+sql);
         
         throw new DbException("Internal error. Failed to update variable\n(" +
                          sqle.getMessage() + ")");
      } finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
         } 
         catch (SQLException ignored) 
         {}
      }
   }
   
   private void CreateVariableSetLink(Connection conn, int vsid, int vid, int id)
    throws DbException
   {
       Statement stmt = null;
       String sql = "";
       try
       {
           sql = "insert into R_Var_Set values("+vsid+", "+vid+", "+id+", "+getSQLDate()+")";
           stmt = conn.createStatement();
           stmt.execute(sql);
       }
       catch (Exception e)
       {
           e.printStackTrace();
           
           throw new DbException("Internal error. Failed to create variable set link\n" +
                   e.getMessage());
       }
       finally
       {
           try
           {
               if (stmt!=null)
                   stmt.close();
           }
           catch (Exception ignore)
           {}
       }
   }
   
   
    public void CreateVariableSets(FileParser fp, Connection conn, 
           String variablesetName, String comm, int suid, int id) 
           throws DbException
    {
        int vsid;
        String variable;
        try 
        {
            String titles[] = fp.columnTitles();
            if(titles.length != 1 || !titles[0].equals("VARIABLE"))
            {
               String errStr="Illegal headers.<BR>"+
                  "Required file headers: VARIABLE <BR>"+
                  "Headers found in file:";
               for (int j=0; j<titles.length;j++)
               {
                  errStr = errStr+ " " + titles[j];
               }
               throw new DbException(errStr);
            }
            vsid = CreateVariableSet(conn, suid, variablesetName, comm, id);
         
            for (int i = 0; i < fp.dataRows(); i++) 
            {
                variable = fp.getValue("VARIABLE", i);

                int vid = getVID(conn, variable, suid);
                CreateVariableSetLink(conn, vsid, vid, id);
            }         
        }  
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to create variable sets\n(" +
                          e.getMessage() + ")");
        } 
    }
   
   public int CreateVariableSet(Connection conn, int suid, String name, 
           String comm, int id) throws DbException
   {
      Statement stmt = null;
      int vsid = 0;
      String sql = "";
      try 
      {
          vsid = getNextID(conn, "Variable_Sets_Seq");
          
          sql = "INSERT INTO Variable_SETS (vsid,name,comm,suid,id,ts) " +
                  "VALUES ("+vsid+", "+sqlString(name)+", "+sqlString(comm)+", "+suid+", "+id+", "+getSQLDate()+")";
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to create variable set\n(" +
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
      return vsid;
   }
   
   public void UpdateVariableSet(Connection conn, String variablesetName, 
           String comm, int vsid, int id) throws DbException
   {
      Statement stmt = null;
      String sql = "";
      String sql_log = "";
      try 
      {
          sql_log = "insert into Variable_Sets_Log (vsid, name, comm, id, ts) " +
                  "select vsid, name, comm, id, ts " +
                  "FROM Variable_Sets " +
                  "where vsid = "+vsid;
          stmt = conn.createStatement();
          stmt.execute(sql_log);
          
          sql = "update Variable_Sets set name = "+sqlString(variablesetName)+", " +
                  "comm = "+sqlString(comm)+", id = "+id+", ts = "+getSQLDate()+"" +
                  "where vsid = "+ vsid;
          stmt.execute(sql);
          
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to update variable set\n(" +
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
   
   public void DeleteVariableSet(Connection conn, int vsid, int id) 
        throws DbException
   {
      Statement stmt = null;
      String sql = "";
      try 
      {
          // Delete R_Var_Set (delete cascade vsid, vid)
          // Delete Variable set logs
                  
          sql = "delete from variable_sets_log where vsid = "+vsid+"; " +
                  "delete from Variable_Sets where vsid = "+vsid;
          stmt = conn.createStatement();
          stmt.execute(sql);
          
      } 
      catch (SQLException sqle) 
      {
         sqle.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to delete variable set\n(" +
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
     * Load the variables to the test-objects
     */
    public void loadVariables(Connection conn, DataObject db, int suid)
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
            
            String sql = "select name from V_VARIABLES_1 where suid="+suid;
            Errors.logDebug(sql);
            ResultSet rs = stmt.executeQuery(sql);
           
            stmt = conn.createStatement();
            
            String variable;
            while (rs.next() )
            {
                variable = rs.getString("name").trim();
                db.setVariable(variable);
            }
        }
        catch (Exception e)
        {
            Errors.logError(e.getMessage());
            e.printStackTrace(System.err);
        }
   }
    
     /**
     * Get the species id given the chromosome id
     * @param conn The database connection
     * @param cid the chromosome id
     * @throws se.arexis.agdb.db.DbException In case of errors, the message is thrown towards the UI
     * @return the species id (sid)
     */
    public int getVID(Connection conn, String name, int suid) throws DbException {
        Statement stmt = null;
        String sql = "";
        int vid = 0;
        try 
        {
            stmt = conn.createStatement();
            sql = "select vid from Variables where suid="+suid+" and name = '"+name+"'";
            
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                vid = rs.getInt("vid");
            }
            
            if (vid == 0) {
                throw new DbException("Unable to find variable by that name for this sampling unit.");
            }
        } catch (DbException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
        }
        return vid;
    }

    
}
