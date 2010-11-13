
package org.tgdb.resource.resource;

import org.tgdb.TgDbCaller;
import java.util.Collection;


/**
 * This is the home interface for Resource enterprise bean.
 */
public interface ResourceRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.resource.resource.ResourceRemote findByPrimaryKey(java.lang.Integer key)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByCategory(int category) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByProject(int project) throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.resource.resource.ResourceRemote create(int resourceId, int projectId, int fileId, int linkId, int categoryId, java.lang.String name, java.lang.String comm, TgDbCaller caller) throws javax.ejb.CreateException, java.rmi.RemoteException;

    java.util.Collection findByProcess(int processId) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByPathway(int pathwayId) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByProtein(int proteinId) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByComplex(int complexId) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findBySamplingUnit(int suid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findByModel(int eid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    
}
