
package org.tgdb.resource.resourcemanager;


/**
 * This is the home interface for ResourceManager enterprise bean.
 */
public interface ResourceManagerRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.resource.resourcemanager.ResourceManagerRemote create()  throws javax.ejb.CreateException, java.rmi.RemoteException;
    
    
}
