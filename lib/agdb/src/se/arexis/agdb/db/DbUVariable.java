/*
  Copyright (C) 2000 by Prevas AB. All rights reserved.
 
  $Log$
  Revision 1.5  2005/03/04 15:36:15  heto
  Converting for using PostgreSQL

  Revision 1.4  2005/02/25 15:08:23  heto
  Converted Db*Variable.java to PostgreSQL

  Revision 1.3  2004/04/23 09:48:52  wali
  added loudUVariables
 
  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.
 
  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson
 
 
  Revision 1.3  2001/04/24 09:34:09  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.
 
  Revision 1.2  2001/04/24 06:31:43  frob
  Checkin after merging frob_fileparser branch.
 
  Revision 1.1.1.1.2.3  2001/04/19 06:54:32  frob
  Added static initializer which registers known file type definitions.
 
  Revision 1.1.1.1.2.2  2001/03/29 11:12:52  frob
  Changed calls to buildErrorString. All calls now passes the result
  from the dataRow2FileRow method as the row parameter.
  Added header and fixed indentation.
 
  Revision 1.1.1.1.2.1  2001/03/28 12:52:02  frob
  Changed calls to FileParser.getTitles() to FileParser.columnTitles() and
  FileParser.getRows() to FileParser.dataRows().
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
public class DbUVariable extends DbAbstractVariable {
    
    static
    {
        try {
            // Register known FileTypeDefinitions
            FileTypeDefinitionList.add(FileTypeDefinition.UVARIABLE,
                    FileTypeDefinition.LIST, 1);
            FileTypeDefinitionList.add(FileTypeDefinition.UVARIABLESET,
                    FileTypeDefinition.LIST, 1);
            FileTypeDefinitionList.add(FileTypeDefinition.UVARIABLE,
                    FileTypeDefinition.MAPPING, 1);
        } catch (FileTypeDefinitionException e) {
            System.err.println("Construction of new FileTypeDefinition " +
                    "failed: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public DbUVariable() {
    }
    
    
    public void CreateUVariables(FileParser fp, Connection conn, int sid, 
            int pid, int id) throws DbException
    
    {
        String name, type, unit, comm;
        String[] titles;
        try 
        {
            // check the fileformat:
            titles = fp.columnTitles();
            checkVariableFileFormat(titles);
       
            
            for (int i = 0; i < fp.dataRows(); i++) 
            {
                name = fp.getValue("VARIABLE", i);
                type = fp.getValue("TYPE", i);
                unit = fp.getValue("UNIT", i);
                comm = fp.getValue("COMMENT", i);

                CreateUVariable(conn, sid, name, type, unit, comm, id, pid);   
            }
            
        } 
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to create unified variables\n(" +
                    sqle.getMessage() + ")");
        } 
    }
    
   
    
    
    public void DeleteUVariable(Connection conn, int uvid, int id) 
        throws DbException
    {
        String sql = "";
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            
            sql = "delete from u_variables_log where uvid = "+uvid+"; " +
                    "delete from U_Variables where uvid = "+uvid;
            stmt.execute(sql);
            
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to delete unified variable\n(" +
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
    
    public void UpdateUVariable(Connection conn, int uvid, String name,
            String type, String unit, String comm,
            int id) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        String sql_log = "";
        try 
        {
            stmt = conn.createStatement();
            
            sql_log = "insert into U_Variables_Log (uvid, name, type, unit, comm, id, ts) "+
                    "select uvid, name, type, unit, comm, id, ts " +
                    "FROM U_Variables " +
                    "where uvid = "+uvid;
            stmt.execute(sql_log);
            
            sql = "update U_Variables set name = "+sqlString(name)+", type = "+sqlString(type)+"," +
                    "unit = "+sqlString(unit)+", comm = "+sqlString(comm)+", id = "+id+", ts = "+getSQLDate()+" " +
                    "where uvid = "+uvid;
            stmt.execute(sql);
            
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to update unified variable\n(" +
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
    
    
   /*
     dbv.CreateUVariable(conn, sid, name, type, unit, comm, id, pid);
    
    */
    public int CreateUVariable(Connection conn, int sid, String name,
            String type, String unit, String comm,
            int id, int pid) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        int uvid = 0;
        try 
        {
            stmt = conn.createStatement();
            
            uvid = getNextID(conn,"U_Variables_Seq");
            
            sql = "insert into U_Variables (uvid,name,type,unit, comm, pid, " +
                    "sid, id, ts) " +
                    "Values ("+uvid+", "+sqlString(name)+", "+sqlString(type)+", " +
                    sqlString(unit)+", "+sqlString(comm)+", "+pid+", "+sid+", "+id+", "+getSQLDate()+")";
            stmt.execute(sql);
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to create unified variable\n(" +
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
        return uvid;
    }
    
    /**
     * Delete the membership.
     * @param conn the database connection
     * @param uvid the unified variable id
     * @param uvsid the unified variable set id
     */
    public void DeleteUVariableSetLink(Connection conn, int uvid, int uvsid)
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try
        {
            stmt = conn.createStatement();
            sql = "DELETE FROM gdbadm.R_U_VAR_SET WHERE "
                    + "UVSID=" + uvsid + " AND UVID=" + uvid;

            stmt.execute(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            throw new DbException("Internal error. Failed to delete variable set link\n"+e.getMessage());
        }
    }
    
    /** 
     * Delete a list of variable set links.
     */
    public void DeleteUVariableSetLinks(Connection conn, String[] uvids, 
            int uvsid)
            throws DbException
    {
        for (int i = 0; i < uvids.length; i++) 
        {
            DeleteUVariableSetLink(conn,Integer.valueOf(uvids[i]),uvsid);
        }
    }
    
    public void CreateUVariableSetLinks(Connection conn, int uvsid, 
            String[] uvids, int pid, int id)
            throws DbException
    
    {
        for (int i = 0; i < uvids.length; i++) 
        {
            Errors.logDebug("ADD [uvsid="+uvsid+", var="+uvids[i]+", pid="+pid+", id="+id+"]");
            
            CreateUVariableSetLink(conn,uvsid,Integer.valueOf(uvids[i]),pid,id);
        }
    }
    
    private void CreateUVariableSetLink(Connection conn, int uvsid, int uvid, 
            int pid, int id) throws DbException
    {
        String sql = "";
        Statement stmt = null;
        try
        {
            stmt = conn.createStatement();
            
            sql = "insert into R_U_Var_Set values("+uvsid+", "+uvid+", "+pid+", "+id+", "+getSQLDate()+")";
            stmt.execute(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            throw new DbException("Internal error. Failed to create unified variable set link\n"+e.getMessage());
        }
        finally
        {
            try
            {
                if (stmt!=null)
                    stmt.close();
            }
            catch (Exception e)
            {}
        }
    }
    
    
    public void CreateUVariableSets(FileParser fp, Connection conn, 
            String uvarsetName, String comm, int sid ,int pid,  int id) 
            throws DbException
    {
        //Statement stmt = null;
        int uvsid;
        String uvariable;
        String message=null;
        //boolean ok = true;
        
        try 
        {
            // check fileformat
            String titles[]=fp.columnTitles();
            checkUVariableFileFormat(titles);
            
           
          
            uvsid = CreateUVariableSet(conn, sid, pid, uvarsetName, comm, id);  

            for (int i = 0; i < fp.dataRows(); i++) 
            {
                uvariable = fp.getValue("VARIABLE", i);

                int uvid = getUVID(conn,uvariable,pid);
                CreateUVariableSetLink(conn, uvsid, uvid, pid, id);
            }
            
        } 
        catch (Exception e) {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to create unified variable sets\n(" +
                    e.getMessage() + ")");
        } 
    }
    
      /**
     * Get the species id given the chromosome id
     * @param conn The database connection
     * @param cid the chromosome id
     * @throws se.arexis.agdb.db.DbException In case of errors, the message is thrown towards the UI
     * @return the species id (sid)
     */
    public int getUVID(Connection conn, String name, int pid) throws DbException {
        Statement stmt = null;
        String sql = "";
        int uvid = 0;
        try 
        {
            stmt = conn.createStatement();
            sql = "select uvid from u_Variables where pid="+pid+" and name = '"+name+"'";
            
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                uvid = rs.getInt("uvid");
            }
            
            if (uvid == 0) {
                throw new DbException("Unable to find unified variable by that name for this project.");
            }
        } catch (DbException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
        }
        return uvid;
    }
    
    
    public int CreateUVariableSet(Connection conn, int sid,  int pid, 
            String name, String comm, int id) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        int uvsid = 0;
        try 
        {
            stmt = conn.createStatement();
            
            uvsid = getNextID(conn,"U_Variable_Sets_Seq");
            
            sql = "INSERT INTO U_Variable_SETS (uvsid,name,comm,pid,sid,id,ts) " +
                    "VALUES ("+uvsid+", "+sqlString(name)+", "+sqlString(comm)+", " +
                    pid +", "+sid+", "+id+", "+getSQLDate()+")";
                   
            stmt.execute(sql);
            
            
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to create unified variable set\n(" +
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
        return uvsid;
    }
    
    public void UpdateUVariableSet(Connection conn, String name, String comm, 
            int uvsid, int id) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        String sql_log = "";
        try 
        {
            stmt = conn.createStatement();
            
            sql_log = "insert into U_Variable_Sets_Log (uvsid, name, comm, id, ts)" +
                    "select uvsid, name, comm, id, ts " +
                    "FROM U_Variable_Sets " +
                    "where uvsid = "+uvsid;
            stmt.execute(sql_log);
            
            sql = "update U_Variable_Sets set name = "+sqlString(name)+", " +
                    "comm = "+sqlString(comm)+", id = "+id+", " +
                    "ts = "+getSQLDate()+" " +
                    "where uvsid = "+uvsid;
            stmt.execute(sql);
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to update unified variable set\n(" +
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
    
    public void DeleteUVariableSet(Connection conn, int uvsid, int id) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            stmt = conn.createStatement();
            
            sql = "delete from u_variable_sets_log where uvsid = "+uvsid+"; " +
                    "delete from U_Variable_Sets where uvsid = "+uvsid;
            stmt.execute(sql);
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to delete unified variable set\n(" +
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
     * Creates a unified variable mapping
     */
    public void CreateUVariableMapping(Connection conn, int uvid, int vid) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            stmt = conn.createStatement();
            sql = "insert into R_UVID_VID Values("+uvid+", "+vid+", "+getSQLDate()+");";
            stmt.execute(sql);
            
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to create unified variable mapping\n(" +
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
     * deletes a unified variable mapping
     */
    public void DeleteUVariableMapping(Connection conn, int uvid, 
            int vid) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            stmt = conn.createStatement();
            sql = "delete from r_uvid_vid where uvid="+uvid+" and vid="+vid;
            stmt.execute(sql);
            
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to call PL/SQL procedure\n(" +
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
    
    
    public void CreateUVariableMappings(FileParser fp, Connection conn, int pid) 
        throws DbException
    {
        String[] titles;
        boolean ok = true;
        String suName, uvName, vName;
        int numberOfSU;
        try 
        {
            titles = fp.columnTitles();
            checkUVarMappingFileFormat(titles);
            
            if(ok)
            {
                numberOfSU = titles.length;
                int suid = 0;
                DbSamplingUnit dbsu = new DbSamplingUnit();
                
                // loop through columns
                for (int column = 1; column < numberOfSU && ok; column++) 
                {
                    suName = titles[column]; // su_name
                    suid = dbsu.getSUID(conn, suName);
                    
                    //loop all rows
                    for (int i = 0; i < fp.dataRows() && ok; i++) 
                    {
                        uvName = fp.getValue("MAPPING", i);//UV_NAME
                        vName = fp.getValue(titles[column], i);//VNAME
                        
                       
                        if(vName != null && !"".equalsIgnoreCase(vName)) 
                        {
                       
                            
                            int uvid = getUVID(conn, uvName, pid);
                            
                            DbVariable vars = new DbVariable();
                            int vid = vars.getVID(conn, vName, suid);
                            
                            CreateUVariableMapping(conn, uvid, vid);
                        }
                    }// end for rows
                }// end for col's
            }
        } catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to create unified variable mappings\n(" +
                    e.getMessage() + ")");
        } 
    }
    
    public void loadUVariables(Connection conn, DataObject db, int sid) {
        Statement stmt;
        
        try {
            stmt = conn.createStatement();
            
            /*
             *SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele1+"'" +
                                " AND SUID=" +"'"+suid+"'");
             */
            
            String sql = "select name from V_U_VARIABLES_1 where sid="+sid;
            Errors.logDebug(sql);
            ResultSet rs = stmt.executeQuery(sql);
            
            stmt = conn.createStatement();
            
            String uvariable;
            //Adds the uvariables in the variable data object. The same methods are used for both.
            while (rs.next() ) {
                uvariable = rs.getString("name").trim();
                db.setVariable(uvariable);
            }
        } catch (Exception e) {
            Errors.logError(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
