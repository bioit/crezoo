
package org.tgdb.model.modelmanager;


/**
 * This is the home interface for ModelManager enterprise bean.
 */
public interface ModelManagerRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.model.modelmanager.ModelManagerRemote create()  throws javax.ejb.CreateException, java.rmi.RemoteException;
    
    
}
