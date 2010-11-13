// Copyright (C) 2000 by Prevas AB. All rights reserved.
package se.arexis.agdb.db;

import se.arexis.agdb.db.*;
import java.sql.*;
import se.arexis.agdb.util.Errors;
import java.util.ArrayList;
import se.arexis.agdb.db.TableClasses.SamplingUnit;

/**
 * This class provides an api of methods to
 * handle sampling units in the database.
 * @author Tomas Bjorklund, Prevas AB
 */
public class DbSamplingUnit extends DbObject 
{
    /**
     * The sampling unit id
     */
    private int m_suid;
  
    /**
     * Constructor
     */
    public DbSamplingUnit() 
    {
        m_suid = -1;
    }
    
    /**
     * Check for valid input data. If something is invalid, this throws DbException
     * @param name The sampling unit name
     * @param comment The sampling unit comment
     * @param status The status ('E' or 'D')
     * @throws se.arexis.agdb.db.DbException Throws a DbException with a message to the user.
     */
    public void checkSUValues(String name,String comment,String status) throws DbException
    {
        if (name == null)
            throw new DbException("Name is null");
        if (name!=null && name.length() > 20)
	    throw new DbException("Name ("  + name + ") exceeds 20 characters");
        else if (comment!=null && comment.length() > 256)
            throw new DbException("Comment exceeds 256 characters");
        else if (status!=null && !status.equals("E") && !status.equals("D"))
            throw new DbException("Illegal value of status ("+status+")");
    }
    
    /**
     * Updates an sampling unit.
     * @param conn The database connection
     * @param name The new name of the sampling unit
     * @param comm The comment of the sampling unit
     * @param status The status of the sampling unit. E - Enabled, D - Disabled
     * @param suid The sampling unit id
     * @param id The user performing the change
     * @throws se.arexis.agdb.db.DbException If errors occurs, this messages the user
     */
    public void UpdateSamplingUnit(Connection conn, String name, String comm, 
                String status, int suid, int id) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        String sql_log ="";
	try 
        {
            checkSUValues(name,comm,status);
            stmt = conn.createStatement();
          
            // Save to log table
            sql_log = "insert into sampling_units_log " 
                   +"(suid, name, comm, status, id, ts) (select suid, name, comm, status, id, ts from sampling_units where suid="+suid+")";
 
            stmt.execute(sql_log);
            Errors.logInfo("Affected rows: "+stmt.getUpdateCount());

            sql = "update Sampling_Units set name = "+sqlString(name)+", " +
                    "comm = "+sqlString(comm)+", status = "+sqlString(status)+
                    ", id = "+id+", ts = "+getSQLDate()+" where suid = "+suid;
            
            
            stmt.execute(sql);
            Errors.logInfo("Affected rows: "+stmt.getUpdateCount());
        } 
        catch(SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            Errors.logError("DbSamplingUnit.UpdateSamplingUnit(...) SQL="+sql+"  SQL exception: "+sqle.getMessage());
            
            throw new DbException("Internal error. Failed to update sampling unit\n(" +
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
     * Create the link between a sampling unit and the project R_PRJ_SU.
     * @param conn The database connection
     * @param suid The sampling unit id
     * @param pid The project id
     * @throws se.arexis.agdb.db.DbException Throws DbException for a message to a user
     */
    public void CreateSamplingUnitLink(Connection conn, int suid, int pid)
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try
        {
            stmt = conn.createStatement();
            sql = "insert into R_Prj_SU values ("+pid+", "+suid+")";
            stmt.execute(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            throw new DbException("Internal error. Failed to create sampling unit link\n(" +
                e.getMessage() + ")");
        }
    }

    /**
     * Creates a new sampling unit
     * @param conn The database connection
     * @param pid The project id for this sampling unit
     * @param name The name of the sampling unit
     * @param comm The comment of the sampling unit
     * @param status The status of the sampling unit. E - Enabled, D - Disabled
     * @param species The species this sampling unit is connected to.
     * @param id The user performing the operation
     * @throws se.arexis.agdb.db.DbException Throws a DbException with a message to the user if errors occur.
     */
    public void CreateSamplingUnit(Connection conn, String pid, String name, 
            String comm, String status, int species, int id) throws DbException
    {
        Statement stmt = null;
        int suid = 0;
        String sql = "";
        try 
        {
            checkSUValues(name,comm,status);
            
            stmt = conn.createStatement();
            
            suid = getNextID(conn,"sampling_units_seq");
            
            // Create the sampling unit
            sql = "insert into Sampling_Units values " +
                    "("+suid+", "+sqlString(name)+", "+sqlString(comm)+", "+sqlString(status)+", "+species+", "+id+", "+getSQLDate()+")";
          
            stmt.execute(sql);
            
            CreateSamplingUnitLink(conn,suid,Integer.valueOf(pid).intValue());
	} 
        catch(Exception e) 
        {
            
            e.printStackTrace(System.out);
            Errors.logError("DbSamplingUnit.UpdateSamplingUnit(...) SQL Exception: "+e.getMessage());
            
            throw new DbException("Internal error. Failed to create sampling unit\n(" +
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
     * return the suid from the recently created sampling unit
     * @return The sampling unit id
     */
    public int getSuid() 
    {
	return m_suid;
    }
    
    public int getSUID(Connection conn, String name) throws DbException
   {
       Statement stmt = null;
       String sql = "";
       int suid = 0;
       try
       {
           stmt = conn.createStatement();
           sql = "select suid from sampling_units where name="+sqlString(name);
	  
           ResultSet rs = stmt.executeQuery(sql);
           
           if (rs.next())
           {
               suid = rs.getInt("suid");
           }
           
           if (suid == 0)
           {
               throw new DbException("Unable to find sampling unit.");
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
       return suid;
   }

    /**
     * Deletes a sampling unit
     * @param conn The database connection
     * @param pid The project id to delete the sampling unit in.
     * @param suid The sampling unit id to delete.
     * @throws se.arexis.agdb.db.DbException Throws a DbException to the user in case of errors.
     */
    public void DeleteSamplingUnit(Connection conn, int pid, int suid) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
	try 
        {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select count(pid) as temp " +
                    "from r_prj_su " +
                    "where suid = "+suid);
            
            int temp = 0;
            if (rs.next())
            {
                temp = rs.getInt("temp");
            }
            
            if (temp==1)
            {
                sql = "delete from genotypes      where suid = "+suid+";";
                sql += "delete from phenotypes     where suid = "+suid+";";
                sql += "delete from variables      where suid = "+suid+";";
                sql += "delete from variable_sets  where suid = "+suid+";";
                sql += "delete from markers        where suid = "+suid+";";
                sql += "delete from marker_sets    where suid = "+suid+";";
                sql += "delete from groupings      where suid = "+suid+";";
                sql += "delete from individuals    where suid = "+suid+";";
                sql += "delete from sampling_units where suid = "+suid+";";
            }
            else
            {
                sql ="delete from R_PRJ_SU where " +
                        "pid = "+pid+" and " +
                        "suid = "+suid;
            }
            
            stmt.execute(sql);
	} 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            Errors.logError("DbSamplingUnit.UpdateSamplingUnit(...) SQL Exception: "+e.getMessage());
            
            throw new DbException("Internal error. Failed to delete sampling unit\n(" +
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
     * Get the species id given the chromosome id
     * @param conn The database connection
     * @param cid the chromosome id
     * @throws se.arexis.agdb.db.DbException In case of errors, the message is thrown towards the UI
     * @return the species id (sid)
     */
    public int getSID(Connection conn, int suid) throws DbException {
        Statement stmt = null;
        String sql = "";
        int sid = 0;
        try {
            stmt = conn.createStatement();
            sql = "select sid from sampling_units where suid="+suid;
            
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                sid = rs.getInt("sid");
            }
            
            if (sid == 0) {
                throw new DbException("Unable to find species for this sampling unit.");
            }
        } catch (DbException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
        }
        return sid;
    }

    /**
     * 
     * Get the name of a sampling unit from the id
     * @param conn The database connection
     * @param suid The sampling unit id to get the name of the sampling unit
     * @return Returns the sampling unit name
     */
    static public String getSUName(Connection conn, String suid)
    {
        String out = "";
      
        try 
        {
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("select name from sampling_units where suid="+suid);
         
            if (rset.next())
            {
                out = rset.getString("name");
            }
        }
        catch (Exception e)
        {
            Errors.logError("DbSamplingUnit.getSUName(...) "+ e.getMessage());
        }
      
        return out;
    }
    
    /**
     * Get all sampling units in a project.
     * @param conn The database connection
     * @param projectId The project id of interest.
     * @return Returns an array (1.5!!) of SamplingUnit objects.
     */
    static public ArrayList<SamplingUnit> getSamplingUnits(Connection conn, int projectId)
    {
        ArrayList<SamplingUnit> out = null;
        
        try
        {
            
            out = new ArrayList<SamplingUnit>();
        
            Statement sqlStatement = null;
            ResultSet resultSet = null;

            sqlStatement = conn.createStatement();
            resultSet = sqlStatement.executeQuery("SELECT SUID, " +
                                               " NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 WHERE PID=" + 
                                               projectId + " order by NAME");

            while (resultSet.next())
            {
                out.add(new SamplingUnit(resultSet.getInt("SUID"),
                                resultSet.getString("NAME")));
            }
            resultSet.close();
            sqlStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return out;
    }
}
