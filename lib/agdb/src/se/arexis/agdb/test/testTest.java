/*
 * testTest.java
 * JUnit based test
 *
 * Created on March 22, 2005, 1:28 PM
 */

package se.arexis.agdb.test;

import junit.framework.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import se.arexis.agdb.db.DbImportFile;
import se.arexis.agdb.db.DbResult;

/**
 *
 * @author heto
 */
public class testTest extends TestCase {
    
    public testTest(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(testTest.class);
        
        return suite;
    }

    /**
     * Test of main method, of class se.arexis.agdb.test.test.
     */
    public void testMain() {
        System.out.println("testMain");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    
}
