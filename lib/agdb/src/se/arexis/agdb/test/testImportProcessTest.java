/*
 * testImportProcessTest.java
 * JUnit based test
 *
 * Created on December 10, 2004, 1:20 PM
 */

package se.arexis.agdb.test;

import junit.framework.*;
import java.util.ArrayList;
import se.arexis.agdb.util.FileImport.*;
import se.arexis.agdb.db.*;
import se.arexis.agdb.db.TableClasses.*;
import java.sql.*;

/**
 *
 * @author heto
 */
public class testImportProcessTest extends TestCase {
    
    public testImportProcessTest(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(testImportProcessTest.class);
        
        return suite;
    }

    /**
     * Test of main method, of class se.arexis.agdb.test.testImportProcess.
     */
    public void testMain() {
        System.out.println("testMain");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of test method, of class se.arexis.agdb.test.testImportProcess.
     */
    public void testTest() {
        System.out.println("testTest");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    
}
