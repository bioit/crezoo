/*
 * Verifier.java
 *
 * Created on June 15, 2005, 8:46 AM
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
 * Verifier class connects to the database and checks that a value is that it is
 * expected to be. This is used with standard JDBC call direct to the db. This 
 * is for ensuring that an EJB sets the correct values to the database.
 * @author heto
 */
public class Verifier {
    
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
    public Verifier(String tableName, String idName, String idValue) throws Exception
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
    
    public void verifiy(String atrName, String idValue, String compValue) throws Exception
    {
        verify(atrName,idValue,compValue);
    }
    
    public void verifiy(String atrName, String idValue, int compValue) throws Exception
    {
        verify(atrName,idValue,compValue);
    }
    
    /**
     * Verify the atrValue value against compValue. If mismatch an exception is 
     * thrown.
     * @param atrName The field in the db to check
     * @param idValue The identifier (PK) value.
     * @param compValue The expected value. (String)
     * @throws java.lang.Exception If mismatch an exception is thrown.
     */
    public void verify(String atrName, String idValue, String compValue) throws Exception
    {
        String str = getString(atrName, idName, idValue, tableName);
        if (!str.equals(compValue))
            throw new Exception("Value mismatch "+str+"!="+compValue+" for attribute "+atrName);
    }
    
    /**
     * Verify the atrValue value against compValue. If mismatch an exception is 
     * thrown.
     * @param atrName The field in the db to check
     * @param idValue The identifier (PK) value.
     * @param compValue The expected value. (int)
     * @throws java.lang.Exception If mismatch an exception is thrown.
     */
    public void verify(String atrName, String idValue, int compValue) throws Exception
    {
        int val = getInt(atrName, idName, idValue, tableName);
        if (val != compValue)
            throw new Exception("Value mismatch "+val+"!="+compValue+" for attribute "+atrName);
    }
    
    /**
     * Get the string with this value
     * @param atrName The atribute name
     * @param idName the id name (PK)
     * @param idValue the id value (PK)
     * @param table The table name
     * @throws java.lang.Exception Throws exception if db communication fails.
     * @return A string with the value in the db.
     */
    public String getString(String atrName, String idName, String idValue, String table) throws Exception
    {
        String out = "";
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select "+atrName+" from "+table+" where "+idName+"="+idValue);
            if (rs.next())
            {
                out = rs.getString(atrName);
            }
        }
        catch (Exception e)
        {
            throw new Exception("Unable to get info from db: "+e.getMessage());
        }
        
        return out;
    }
    
    /**
     * Get the string with this value
     * @param atrName The atribute name
     * @param idName the id name (PK)
     * @param idValue the id value (PK)
     * @param table The table name
     * @throws java.lang.Exception Throws exception if db communication fails.
     * @return An int with the value in the db.
     */
    public int getInt(String atrName, String idName, String idValue, String table) throws Exception
    {
        int out = -1;
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select "+atrName+" from "+table+" where "+idName+"="+idValue);
            if (rs.next())
            {
                out = rs.getInt(atrName);
            }
        }
        catch (Exception e)
        {
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
    
    public void assertEquals(String columnName, String expected) throws Exception
    {
        String str = getString(columnName, idName, idValue, tableName);
        if (!str.equals(expected))
            throw new Exception("Value mismatch "+str+"!="+expected+" for attribute "+columnName);
    }
    
}
