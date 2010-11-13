/*
 * SqlHelper.java
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
public class SqlHelper {
    
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
    String idName;
    
    String idValue;
    
    /**
     * Creates a new instance of Verifier
     * @param tableName Set the table name to verify information from.
     * @param idName The id name that is the primary key to check for.
     * @throws java.lang.Exception May throw exception if connection fails.
     */
    public SqlHelper(String tableName, String idName, String idValue) throws Exception
    {
        this.tableName = tableName;
        this.idName = idName;
        this.idValue = idValue;
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
    
    public SqlHelper(String tableName, String idName, String idValue, String host, String db) throws Exception
    {
        this.tableName = tableName;
        this.idName = idName;
        this.idValue = idValue;
        try
        {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://"+host+"/"+db;
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
    
    public void createDependentObject(String sql) throws Exception{
         try
        {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception("Unable to create dependent object: "+e.getMessage());
        }       
    }
    
    public double getDouble(String columnName)
    {
        double out = 0;
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select "+columnName+" from "+tableName+" where "+idName+"="+idValue);
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
            ResultSet rs = stmt.executeQuery("select "+columnName+" from "+tableName+" where "+idName+"="+idValue);
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
            ResultSet rs = stmt.executeQuery("select "+columnName+" from "+tableName+" where "+idName+"="+idValue);
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
    {
        String out = null;
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select "+columnName+" from "+tableName+" where "+idName+"="+idValue);
            if (rs.next())
            {
                out = rs.getString(columnName);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //throw new Exception("Unable to get info from db: "+e.getMessage());
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
    public void delete(String idName, String idValue) throws Exception
    {
        int out = -1;
        try
        {
            Statement stmt = conn.createStatement();
            stmt.execute("delete from "+tableName+" where "+idName+"="+idValue);
        }
        catch (Exception e)
        {
            throw new Exception("Unable to get info from db: "+e.getMessage());
        }        
    }
    
    
}
