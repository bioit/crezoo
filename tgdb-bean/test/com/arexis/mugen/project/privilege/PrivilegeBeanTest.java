/*
 * PrivilegeBeanTest.java
 * JUnit based test
 *
 * Created on June 13, 2005, 12:57 PM
 */

package org.tgdb.project.privilege;
import java.rmi.RemoteException;
import java.util.Properties;
import junit.framework.*;
import javax.ejb.*;
import javax.naming.Context;


/**
 *
 * @author heto
 */
public class PrivilegeBeanTest extends TestCase {
    
    public PrivilegeBeanTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PrivilegeBeanTest.class);
        
        return suite;
    }

    
    
    public void testCreateAndDelete()
    {
        try
        {
            PrivilegeRemoteHome priHome = lookupPrivilegeBean();
            PrivilegeRemote pri = null;

            pri = priHome.create(100, "priv", "comment");
            pri.remove();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        /*
        catch (CreateException e)
        {
            e.printStackTrace();
        }
        catch (RemoveException e)
        {
            e.printStackTrace();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        */
        
        fail("the test case is empty");
    }

    public static void main(java.lang.String[] argList) 
    {
        System.setProperty("org.omg.CORBA.ORBInitialHost", "192.168.1.60");
        System.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
        
        junit.textui.TestRunner.run(suite());
    }

    private org.tgdb.project.privilege.PrivilegeRemoteHome lookupPrivilegeBean() {
        try {
              
            //System.setProperty("org.omg.CORBA.ORBInitialHost", "192.168.1.60");
            //System.setProperty(InitialContext.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.cosnaming.CNCtxFactory");
            //System.setProperty(InitialContext.PROVIDER_URL,"iiop://192.168.1.60:3700");
            
            
            //properties.put(InitialContext.INITIAL_CONTEXT_FACTORY, "com.sun.appserv.naming.S1ASCtxFactory");
            
            /*
            Properties properties = new Properties();
            properties.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.cosnaming.CNCtxFactory");
            properties.put("org.omg.CORBA.ORBInitialHost", "192.168.1.60");
            properties.put("org.omg.CORBA.ORBInitialPort", "3700");
            properties.put(Context.PROVIDER_URL, "iiop://192.168.1.60:3700");
            */
            
            /*
            System.setProperty("org.omg.CORBA.ORBInitialHost", "192.168.1.60");
            System.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.cosnaming.CNCtxFactory");
            */
            
            javax.naming.Context c = new javax.naming.InitialContext();
            Object remote = c.lookup("ejb/CreZOOPrivilegeBean"); //java:comp/env/ejb/PrivilegeBean
            org.tgdb.project.privilege.PrivilegeRemoteHome rv = (org.tgdb.project.privilege.PrivilegeRemoteHome) javax.rmi.PortableRemoteObject.narrow(remote, org.tgdb.project.privilege.PrivilegeRemoteHome.class);
            return rv;
        }
        catch(javax.naming.NamingException ne) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE,"exception caught" ,ne);
            throw new RuntimeException(ne);
        }
    }
    
}
