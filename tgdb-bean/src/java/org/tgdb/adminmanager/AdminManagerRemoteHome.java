
package org.tgdb.adminmanager;


/**
 * This is the home interface for AdminManager enterprise bean.
 */
public interface AdminManagerRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.adminmanager.AdminManagerRemote create()  throws javax.ejb.CreateException, java.rmi.RemoteException;
    
    
}
