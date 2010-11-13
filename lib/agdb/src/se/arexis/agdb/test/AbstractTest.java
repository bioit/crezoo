/*
 * AbstractTest.java
 *
 * Created on April 5, 2005, 9:09 AM
 */

package se.arexis.agdb.test;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author heto
 */
public class AbstractTest 
{
    
    public Connection pg_conn;
    public Connection pg_conn_viss;
    
    public Connection ora_conn;
    
    /** Creates a new instance of AbstractTest */
    public AbstractTest() 
    {
        
        
       
    }
    
    public void init()
    {
        String dburl="jdbc:oracle:thin:@192.168.1.32:1521:agdbtest";
        String uid="gdbadm";
        String pwd="gdbadm";
        
        
        try
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            ora_conn = DriverManager.getConnection(dburl,uid,pwd);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        
        String classprop = "org.postgresql.Driver";
        dburl = "jdbc:postgresql://192.168.1.32/agdb";
        try
        {
            Class.forName(classprop);
            pg_conn = DriverManager.getConnection(dburl,uid,pwd);
            pg_conn_viss = DriverManager.getConnection(dburl,uid,pwd);
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
