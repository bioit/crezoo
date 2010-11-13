/*
 
  $Log$
  Revision 1.4  2005/02/23 13:31:26  heto
  Converted database classes to PostgreSQL

  Revision 1.3  2004/12/14 08:58:55  heto
  swedish characters changed in comment.
 
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
 
  Revision 1.1.1.1.2.6  2001/04/11 06:35:34  frob
  Update of constant names in FileTypeDefinition caused changes here.
 
  Revision 1.1.1.1.2.5  2001/04/10 13:11:11  frob
  Changed static initialiser to use constants instead of strings.
 
  Revision 1.1.1.1.2.4  2001/04/10 09:55:54  frob
  Added a static initializer which registers the known file type definitions.
 
  Revision 1.1.1.1.2.3  2001/04/06 12:36:29  frob
  Removed constructor and fixed the layout of AddPrivileges.
 
  Revision 1.1.1.1.2.2  2001/03/29 11:12:51  frob
  Changed calls to buildErrorString. All calls now passes the result
  from the dataRow2FileRow method as the row parameter.
  Added header and fixed indentation.
 
  Revision 1.1.1.1.2.1  2001/03/28 12:52:01  frob
  Changed calls to FileParser.getTitles() to FileParser.columnTitles()
  and FileParser.getRows() to FileParser.dataRows().
  Indeted the file and added the log header.
 
 
 */
package se.arexis.agdb.db;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;
import se.arexis.agdb.util.FileImport.*;

import java.io.*;
import java.sql.*;

/**
 * This class provides methods for objects in the database
 * that has something to do with setting up and administration
 * of roles in the application.
 *
 * @author <b>Tomas Björklund, Prevas AB</b>, Copyright &#169; 2000
 * @version 1.0, 2000-11-28
 */
public class DbRole extends DbObject {
    
    static
    {
        try {
            // Register known FileTypeDefinitions
            FileTypeDefinitionList.add(FileTypeDefinition.ROLE,
                    FileTypeDefinition.LIST, 1);
        } catch (FileTypeDefinitionException e) {
            System.err.println("Construction of new FileTypeDefinition " +
                    "failed: " + e.getMessage());
            System.exit(1);
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////
    //
    // Public section
    //
    //////////////////////////////////////////////////////////////////////
    
    public void CreateRole(Connection conn, int pid, String name, String comm) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        int rid = 0;
        try {
            
            rid = getNextID(conn,"Roles_Seq");
            
            sql = "insert into Roles_ values("+rid+", "+pid+", "+sqlString(name)+", "+sqlString(comm)+")";
            stmt = conn.createStatement();
            stmt.execute(sql);
            
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to create role\n(" +
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
    
    public void UpdateRole(Connection conn, int rid, String new_name, String new_comm) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try {
            sql = " Update Roles_ set name = "+sqlString(new_name)+", comm = "+sqlString(new_comm)+"  where rid = "+rid;
            stmt = conn.createStatement();
            stmt.execute(sql);
            
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to update role\n(" +
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
    
    public void DeleteRole(Connection conn, int rid)
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try {
            // Delete from R_PRJ_RID (on delete cascade pid, rid)
            sql = "delete from roles_ where rid="+rid;
            stmt = conn.createStatement();
            stmt.execute(sql);
            
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to delete role\n(" +
                    sqle.getMessage() + ")");
        } 
        finally 
        {    
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException ignored) {
            }
        }
    }
    
    public void AddPrivilege(Connection conn, int rid, int prid) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try {
            sql = "insert into r_rol_pri values(p_rid, p_prid)";
            stmt = conn.createStatement();
            stmt.execute(sql);
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to add privilege\n(" +
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
     * Remove all privileges for a role.
     */
    private void RemoveAllPrivileges(Connection conn, int rid)
        throws DbException
    {
       Statement stmt = null;
       String sql = "";
       try
       {
           sql = "delete from r_rol_pri where rid = "+rid;
           stmt = conn.createStatement();
           stmt.execute(sql);
       }
       catch (Exception e)
       {
           e.printStackTrace();
           
           throw new DbException("Internal error. Failed to delete all privileges\n"+
                   e.getMessage());           
       }
    }
    
    public void SetPrivileges(Connection conn, int rid, int[] prids) 
        throws DbException
    {
        RemoveAllPrivileges(conn, rid);
        
        for (int i=0;i<prids.length;i++)
        {
            AddPrivilege(conn, rid, prids[i]);
        }        
    }
    
    public void DeletePrivilege(Connection conn, int rid, int prid) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            sql = " delete from r_rol_pri where rid = p_rid and prid = "+prid;
            stmt = conn.createStatement();
            stmt.execute(sql);
            
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to delete privilege\n(" +
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
     * Adds privileges read from a file to a given role id.
     *
     * @param fileParser The file parser to read roles from.
     * @param connection The connection to use.
     * @param roleId The role id to add privileges to.
     */
    public void AddPrivileges(FileParser fileParser, Connection connection,
            int roleId) 
            throws DbException
    {
        try 
        {
            // Get titles from parser and verify them
            String[] titles = fileParser.columnTitles();
            if (titles.length != 1 || !titles[0].equals("PRIVILEGE")) 
            {
                throw new DbException("Unknown file format. The first row in the file " +
                        "must consist of the word [PRIVILEGE]");
            } 
            
            // The currently read privelege
            String aPrivilegeName;        
            
            // Loop all data rows read from file until end of rows and while
            // there are no errors.
            for (int row = 0; row < fileParser.dataRows(); row++) 
            {
                // Get the privilige from the current row and add it to the
                // statement.
                aPrivilegeName = fileParser.getValue("PRIVILEGE", row);
                
                int prid = getPRID(connection, aPrivilegeName);
                AddPrivilege(connection, roleId, prid);
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            throw new DbException("Internal error. Failed to add privileges.");
        }
    }
    
    private int getPRID(Connection conn, String aPrivilegeName)
        throws DbException
    {
        int prid = 0;
        Statement stmt = null;
        String sql = "";
        try
        {
            sql = "select prid from privileges_ " +
                    "where name="+aPrivilegeName;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next())
            {
                prid = rs.getInt("prid");
            }
            rs.close();
            if (prid == 0)
            {
                throw new DbException("Prid not found for privilege="+aPrivilegeName);
            }
        }
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            throw new DbException("Internal error. Failed to get prid");
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
        return prid;
    }
   
    
}


