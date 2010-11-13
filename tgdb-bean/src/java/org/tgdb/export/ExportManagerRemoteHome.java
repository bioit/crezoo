
package org.tgdb.export;


/**
 * This is the home interface for ExportManager enterprise bean.
 */
public interface ExportManagerRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.export.ExportManagerRemote create()  throws javax.ejb.CreateException, java.rmi.RemoteException;
    
    
}
