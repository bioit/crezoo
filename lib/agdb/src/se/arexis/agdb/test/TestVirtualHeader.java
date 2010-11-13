/*
 * TestVirtualHeader.java
 *
 * Created on April 1, 2005, 2:20 PM
 */

package se.arexis.agdb.test;

import java.sql.Connection;
import java.sql.DriverManager;
import se.arexis.agdb.util.FileImport.ImportProcess;

/**
 *
 * @author heto
 */
public class TestVirtualHeader {
    
    /** Creates a new instance of TestVirtualHeader */
    public TestVirtualHeader() 
    {
    }
    
    public static void main(String args[])
    {
     
        // TODO code application logic here
        String classprop = "org.postgresql.Driver";
        String cstr = "jdbc:postgresql://192.168.1.32/agdb";
        String usr = "gdbadm";
        String pwd = "gdbadm";
        Connection conn = null;
        try
        {
            Class.forName(classprop);
            conn = DriverManager.getConnection(cstr, usr, pwd);

            int ifid = 3;
            ImportProcess ip = new ImportProcess();
            ip.createHeaders(conn, ifid);
            
            /*
            ip.createHeaders(conn,2);
            ip.createHeaders(conn,6);
            */
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
