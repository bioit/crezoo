
package org.tgdb.resource.link;

import org.tgdb.TgDbCaller;


/**
 * This is the home interface for Link enterprise bean.
 */
public interface LinkRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.resource.link.LinkRemote findByPrimaryKey(java.lang.Integer linkid)  throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    
    
    /**
     *
     */
    org.tgdb.resource.link.LinkRemote create(int linkId, String name, String url, String comm, TgDbCaller caller)  throws javax.ejb.CreateException, java.rmi.RemoteException;

    java.util.Collection findByProject(int pid) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findBySamplingUnit(int suid) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    
}
