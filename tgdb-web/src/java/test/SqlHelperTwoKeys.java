/*
 * SqlHelperTwoKeys.java
 *
 * Created on June 21, 2005, 2:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package test;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 * @author heto
 */
public class SqlHelperTwoKeys {
    
    /**
     * Database connection object
     */
    java.sql.Connection conn;
    
    /**
     * The database table name
     */
    String tableName;
    /**
     * The id name for the table.
     */
    String idName1, idName2;
    
    String idValue1, idValue2;
    
    /**
     * Creates a new instance of Verifier
     * @param tableName Set the table name to verify information from.
     * @param idName The id name that is the primary key to check for.
     * @throws java.lang.Exception May throw exception if connection fails.
     */
    public SqlHelperTwoKeys(String tableName, String idName1, String idName2, String idValue1, String idValue2) throws Exception
    {
        this.tableName = tableName;
        this.idName1 = idName1;
        this.idValue1 = idValue1;
        this.idValue2 = idValue2;
        this.idName2 = idName2;
        try
        {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://10.0.1.30/agdb";
            Properties props = new Properties();
            props.setProperty("user","gdbadm");
            props.setProperty("password","gdbadm");
            //props.setProperty("ssl","true");
            conn = DriverManager.getConnection(url, props); 
        }
        catch (Exception e)
        {
            throw new Exception("Unable to connect to db: "+e.getMessage());
        }
    }   
    
    public void createDependentObject(String sql) {
         try
        {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //throw new Exception("Unable to get info from db: "+e.getMessage());
        }       
    }
    
    public double getDouble(String columnName)
    {
        double out = 0;
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select "+columnName+" from "+tableName+" where "+idName1+"="+idValue1+" and "+idName2+"="+idValue2);
            if (rs.next())
            {
                out = rs.getDouble(columnName);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //throw new Exception("Unable to get info from db: "+e.getMessage());
        }
        
        return out;
    }
    
    public java.sql.Date getDate(String columnName)
    {
        java.sql.Date out = null;
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select "+columnName+" from "+tableName+" where "+idName1+"="+idValue1+" and "+idName2+"="+idValue2);
            if (rs.next())
            {
                out = rs.getDate(columnName);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //throw new Exception("Unable to get info from db: "+e.getMessage());
        }
        
        return out;
    }    
    
    public int getInt(String columnName)
    {
        int out = 0;
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select "+columnName+" from "+tableName+" where "+idName1+"="+idValue1+" and "+idName2+"="+idValue2);
            if (rs.next())
            {
                out = rs.getInt(columnName);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //throw new Exception("Unable to get info from db: "+e.getMessage());
        }
        
        return out;
    }    
    
    public String getString(String columnName)
    throws Exception{
        String out = null;
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select "+columnName+" from "+tableName+" where "+idName1+"="+idValue1+" and "+idName2+"="+idValue2);
            if (rs.next())
            {
                out = rs.getString(columnName);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception("Unable to get info from db: "+e.getMessage());
        }
        
        return out;
    }    
    
    /**
     * Delete a tuple from a table. 
     *
     * delete from <table> where <idName> = <idValues 
     *
     * @param idName the name to delete
     * @param idValue the value for the tuples to be deleted
     * @throws java.lang.Exception throws exception if a database error occurs.
     */
    public void delete(String idName1, String idName2, String idValue1, String idValue2) throws Exception
    {
        int out = -1;
        try
        {
            Statement stmt = conn.createStatement();
            stmt.execute("delete from "+tableName+" where "+idName1+"="+idValue1+" and "+idName2+"="+idValue2);
        }
        catch (Exception e)
        {
            throw new Exception("Unable to get info from db: "+e.getMessage());
        }        
    }
    
    
}
