/*
 * PostgresId.java
 *
 * Created on June 29, 2005, 9:59 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.id;

import org.tgdb.exceptions.DbException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Class for generation of unique id's
 * @author heto
 */
public class PostgresId implements IdGenerator {
    
    /** Creates a new instance of PostgresId */
    public PostgresId() 
    {
    }
    
    /**
     * Returns the next id to be used
     * @param conn The database connection
     * @param sequenceName The name of the sequence that the id should be generated for
     * @throws org.tgdb.exceptions.DbException If the id could not be generated
     * @return A new id
     */
    public synchronized int getNextId(Connection conn, String sequenceName) throws DbException
    {
        String sql = "";
        Statement stmt = null;
        ResultSet rs = null;
        int id = 0;
        try
        {
            stmt = conn.createStatement();
            
            // For PostgreSQL
            sql = "select nextval('"+sequenceName+"') as new_id";
            
            // For Oracle
            //sql = "select "+sequence+".nextval as new_id from dual";
            
            rs = stmt.executeQuery(sql);
            
            if (rs.next())
            {
                id = rs.getInt("new_id");
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            throw new DbException("Database error: Next ID could not be retrieved.");
        }
        finally
        {
            try
            {
                if (stmt!=null)
                    stmt.close();
                if (rs!=null)
                    rs.close();
            }
            catch (Exception ignore)
            {}
        }        
        return id;
    }
}
