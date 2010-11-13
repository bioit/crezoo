/*
 * VerifierTest.java
 * JUnit based test
 *
 * Created on June 15, 2005, 5:14 PM
 */

package test;

import junit.framework.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 * @author heto
 */
public class VerifierTest extends TestCase {
    
    public VerifierTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(VerifierTest.class);
        
        return suite;
    }

    /**
     * Test of verifiy method, of class test.Verifier.
     */
    public void testVerifiy() {
        System.out.println("testVerifiy");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of getString method, of class test.Verifier.
     */
    public void testGetString() {
        System.out.println("testGetString");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of getInt method, of class test.Verifier.
     */
    public void testGetInt() {
        System.out.println("testGetInt");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    public static void main(java.lang.String[] argList) {

        junit.textui.TestRunner.run(suite());
    }
    
}
