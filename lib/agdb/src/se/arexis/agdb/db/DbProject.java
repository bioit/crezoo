// Copyright (C) 2000 by Prevas AB. All rights reserved.
package se.arexis.agdb.db;

import se.arexis.agdb.db.*;
import java.io.*;
import java.sql.*;

/**
 * This class provides methods for objects in the database
 * that has something to do with setting up and administration
 * of projects in the application.
 *
 * @author <b>Tomas Björklund, Prevas AB</b>, Copyright &#169; 2000
 * @version 1.0, 2000-11-28
 */
public class DbProject extends DbObject {
    
    /**
     * Default constructor.
     * This constructor doesn't do anything.
     */
    public DbProject() {
    }
    
    /**
     * This method links one or more existing species to an existing
     * project. The method calls the AddSpecies() method
     */
    public void AddSpecies(Connection conn, int pid, String[] sids) 
        throws DbException
    {
        for (int i=0;i<sids.length;i++)
        {
            AddSpecies(conn,pid,sids[i]);
        }
    }
    
    /**
     * This method links one existing species to an existing
     * project. 
     */
    public void AddSpecies(Connection conn, int pid, String sid) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            sql = "insert into R_PRJ_SPC values("+pid+", "+sid+")";
            stmt = conn.createStatement();
            stmt.execute(sql);
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to add the species to a project\n(" +
                    sqle.getMessage() + ")");
        } 
        finally 
        {
            try {
                if (stmt != null) stmt.close();
            } 
            catch (SQLException ignored) 
            {}
        }
        
    }
    
    /**
     * This method removes one or more links to species from a project.
     * The method calls the RemoveSpecies() method
     */
    public void RemoveSpecies(Connection conn, int pid, String[] sids)      
        throws DbException
    {
        for (int i=0;i<sids.length;i++)
        {
            RemoveSpecies(conn, pid, sids[i]);
        }
    }
    
    /**
     * This method removes the link to a species from a project.
     */
    public void RemoveSpecies(Connection conn, int pid, String sid) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            
            DbSpecies spec = new DbSpecies();
            
            
            // Check if there are at least one sampling
            sql = "select count(r.suid) as num " +
                    "from R_Prj_Su r, Sampling_Units su " +
                    "where r.pid = "+pid+" and " +
                    "r.suid = su.suid and " +
                    "su.sid = "+sid;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            int num = 0;
            if (rs.next())
            {
                num = rs.getInt("num");
                
                if (num > 0)
                {
                    String sname = spec.getSpeciesName(conn, sid);
                    throw new DbException("There are " + num +
                            " sampling unit(s) of the species " +  sname + " linked into this project.");
                }
            }
            rs.close();
            
            sql = "delete from R_Prj_Spc where pid = p_pid and sid = "+sid;
            stmt.execute(sql);
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            throw new DbException("Internal error. Failed to remove species from project\n(" +
                    sqle.getMessage() + ")");
        } 
        finally 
        {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException ignored) {}
        }
    }
    
    /**
     * This method removes one or more users from a project.
     * The method calls the RemoveUser() method
     */
    public void RemoveUsers(Connection conn, int pid, String[] ids) 
        throws DbException
    {
        for (int i=0;i<ids.length;i++)
        {
            RemoveUser(conn, pid, ids[i]);
        }
    }
    
    /**
     * This method removes a user from a project.
     */
    public void RemoveUser(Connection conn, int pid, String id) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            sql = "delete from R_PRJ_ROL where pid = "+pid+" and id = "+id;
            stmt = conn.createStatement();
            stmt.execute(sql);
            
            
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to remove the user\n(" +
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
     * This method adds one ore more users to an existing project and
     * an existing role within the project. The method calls the
     * AddUser() method.
     */
    public void AddUsers(Connection conn, int pid, int rid, String[] ids) 
        throws DbException
    {
        for (int i=0;i<ids.length;i++)
        {
            AddUser(conn, pid, rid, ids[i]);
        }
    }
    
    /**
     * This method adds a user to an existing project and
     * an existing role within the project. 
     */
    public void AddUser(Connection conn, int pid, int rid, String id) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            sql = "insert into R_PRJ_ROL values("+pid+", "+id+", "+rid+")";
            stmt = conn.createStatement();
            stmt.execute(sql);
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to add user to a role in a project\n(" +
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
     * This method updates the users role in an existing project to
     * an existing role within the project. 
     */
    public void UpdateUser(Connection conn, int pid, int rid, String id) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            sql = " Update R_PRJ_ROL set rid="+rid+" " +
                    "where pid = "+pid+" and id = "+id;
            stmt = conn.createStatement();
            stmt.execute(sql);
            
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            throw new DbException("Internal error. Failed to update user link\n(" +
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
     * This method removes one or more sampling units from a project.
     * For each suid in suids[] the mothed calls the RemoveSU method
     */
    public void RemoveSU(Connection conn, int pid, String[] suids) 
        throws DbException
    {
        for (int i=0;i<suids.length;i++)
        {
            RemoveSU(conn, pid, suids[i]);
        }
    }
    
    /**
     * This method removes one sampling units from a project.
     */
    public void RemoveSU(Connection conn, int pid, String suid) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            sql = "delete from R_PRj_Su where pid = "+pid+" and suid = "+suid;
            stmt = conn.createStatement();
            stmt.execute(sql);
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            throw new DbException("Internal error. Failed to remove the sampling unit link\n(" +
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
     * This method adds one or more sampling units to a project. For each
     * sampling unit id in suids[], the method calls the AddSU(conn,pid,suid) 
     * method.     
     */
    public void AddSU(Connection conn, int pid, String[] suids) 
        throws DbException
    {
        for (int i=0;i<suids.length;i++)
        {
            AddSU(conn,pid,suids[i]);
        }
    }
    
    /**
     * This method adds a sampling units to a project. 
     */
    public void AddSU(Connection conn, int pid, String suid) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            stmt = conn.createStatement();
            
            DbSamplingUnit dbSU = new DbSamplingUnit();
            int su_sid = dbSU.getSID(conn, Integer.valueOf(suid).intValue());
            
            sql = "select count(sid) as num " +
                    "from R_Prj_Spc " +
                    "where pid = "+pid+" and " +
                    "sid = "+su_sid;
            ResultSet rs = stmt.executeQuery(sql);
            
            int num = 0;
            if (rs.next())
            {
                num = rs.getInt("num");
                
                if (num<1)
                {
                    throw new DbException("The sampling unit is not of a correct species.");
                }
            }
            rs.close();
            
            sql = "insert into R_Prj_Su values("+pid+", "+suid+")";
            stmt.execute(sql);
        } 
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            throw new DbException("Internal error. Failed to add sampling unit\n(" +
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
     * This method updates an existing project. The method calls
     * the PL/SQL procedure <code>Update_Project</code>. If there
     * is a problem updateting the project, the error message can
     * be retrieved by the method <code>getMessage</code>.
     * @author <I>Tomas Bjorklund, Prevas AB</I>
     */
    public void UpdateProject(Connection conn, int pid,
            String new_name, String new_comm, String new_status) 
            throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            if (!new_status.contains("E") && !new_status.contains("D"))
                throw new DbException("status must be either 'E' or 'D'");
            
            sql = "update projects set name = "+sqlString(new_name)+", comm = "+
                    sqlString(new_comm)+", status = "+sqlString(new_status)+ " "+
                    "where pid = "+pid;
            stmt = conn.createStatement();
            stmt.execute(sql);
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            throw new DbException("Internal error. Failed to update project\n(" +
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
     * This method deletes an existing project. 
     * @author <I>Tomas Bjorklund, Prevas AB</I>
     */
    public void DeleteProject(Connection conn, int pid) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            stmt = conn.createStatement();
            
            // Delete all unified allele logs
            sql = "delete from U_Alleles_Log where uaid in (select uaid from V_U_Alleles_3 where pid = "+pid+")";
            stmt.execute(sql);
            
            
            sql = "delete from U_Alleles where umid in " +
                    "(select umid from U_Markers where pid = "+pid+")";
            stmt.execute(sql);
            
            
            // Delete all unified marker logs
            sql = "delete from U_Markers_Log where umid in " +
                    "(select umid from U_Markers where pid = "+pid+")";
            stmt.execute(sql);
            
            // Delete all unified markers
            sql = "delete from u_markers where pid = "+pid;
            stmt.execute(sql);
            //p_message := 'Failed to delete all the unified markers for this project. ' ||        
            
                    
            // Delete all unified marker set logs
            sql = "delete from U_Marker_Sets_Log where umsid in " +
                    "(select umsid from U_Marker_Sets where pid = "+pid+")";
            stmt.execute(sql);
	    //p_message := 'Failed to delete all the unified marker set logs, for this project. ' 
                    
            // Delete all unified marker sets
            sql = "delete from U_Marker_Sets where pid = "+pid;
            stmt.execute(sql);
            //p_message := 'Failed to delete all the unified marker sets for this project. ' ||
            
            
            // Delete all unified variable log
            sql = "delete from U_Variables_Log where uvid in " +
                    "(select uvid from U_Variables where pid = "+pid+")";
            stmt.execute(sql);
            //p_message := 'Failed to delete all the unified variable logs for this project. ' ||
            
            // Delete all unified variables
            sql = "delete from U_Variables where pid = "+pid;
            stmt.execute(sql);
            //p_message := 'Failed to delete all the unified variables for this project. ' ||
            
            // Delete all unified variable set logs
            sql = "delete from U_Variable_Sets_Log where uvsid in " +
                    "(select uvsid from U_Variable_Sets where pid = "+pid+")";
            stmt.execute(sql);
            //p_message := 'Failed to delete all the unified variable set logs for this project. ' ||
            
            // Delete all unified variable sets
            sql = "delete from U_Variable_Sets where pid = "+pid;
            stmt.execute(sql);
            //p_message := 'Failed to delete all the unified variable sets for this project. ' 
            
            // Delete all roles
            sql = "delete from roles_ where pid = "+pid;
            stmt.execute(sql);

            // Delete the project
            sql = "delete from Projects where pid = "+pid;
            stmt.execute(sql);
            // p_message := 'Failed to delete project. ' ||
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            throw new DbException("Internal error. Failed to delete project\n(" +
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
     * This method creates a new project. 
     */
    public int CreateProject(Connection conn, String name, String comm) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";        
        int pid = 0;
        try 
        {
            pid = getNextID(conn,"Projects_seq");
            
            sql = "insert into projects (pid,name,comm,status) " +
                    "values("+pid+", "+sqlString(name)+", "+sqlString(comm)+", 'E')";
            stmt = conn.createStatement();
            stmt.execute(sql);
        } 
        catch (SQLException sqle) {
            sqle.printStackTrace(System.err);
            throw new DbException("Internal error. Failed to call PL/SQL procedure\n(" +
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
        return pid;
    }
}
