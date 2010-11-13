/*
 * DbDataFile.java
 *
 * Created on March 15, 2005, 9:02 AM
 */

package se.arexis.agdb.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author heto
 */
public class DbDataFile extends DbObject
{
    
    /** Creates a new instance of DbDataFile */
    public DbDataFile() 
    {
    }
    
     //--------------------------------------------------------------------------------
    public int createDataFile(Connection conn, int fgid, String name, String id)
	throws DbException
    {
	Statement stmt = null;
	int dfid = 0;
        String sql = "";
	try 
        {
            stmt = conn.createStatement();
            // Check parameters
	    if (name.length() >  20)
                throw new DbException("Name exceeds 20 characters");
            
            dfid = getNextID(conn, "Data_Files_Seq");
            
            sql = "insert into Data_Files (dfid, fgid, name, status,id,ts) " +
                    "Values("+dfid+", "+fgid+", "+sqlString(name)+", '0%', "+
                    id+", "+getSQLDate()+")";
                    
            stmt.execute(sql);
            stmt.close();        
	} 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace();
            
	    //writeLogFileError("SQL ERROR [" + message + "]");
	    throw new DbException("Internal error. Failed to create data file\n"+sqle.getMessage());
	}
	return dfid;
    }
    
    
    public void setDataFileStatus(Connection conn, int dfid, String status)
        throws DbException
    {
        // Check parameters
        if (status.length() > 8)
            throw new DbException("Status exceeds 8 charcters");
        
        Statement stmt = null;
        String sql = "";
        try
        {
            // Update the data file row
            stmt = conn.createStatement();
            sql = "update Data_Files set status = "+sqlString(status)+", " +
                    "ts = "+getSQLDate()+" where dfid = "+dfid;
            stmt.execute(sql);
        }
        catch (Exception e)
        {
            throw new DbException("Internal error. Failed to set data file status\n"+e.getMessage());
        }
    }
    
}
