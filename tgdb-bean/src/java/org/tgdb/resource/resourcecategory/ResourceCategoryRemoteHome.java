
package org.tgdb.resource.resourcecategory;


/**
 * This is the home interface for ResourceCategory enterprise bean.
 */
public interface ResourceCategoryRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.resource.resourcecategory.ResourceCategoryRemote findByPrimaryKey(java.lang.Integer key)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByProject(int project) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByParent(int parent) throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.resource.resourcecategory.ResourceCategoryRemote create(java.lang.String name, java.lang.String comm, int project, int user, int category, int parent) throws javax.ejb.CreateException, java.rmi.RemoteException;

    java.util.Collection findByProcess(int processId) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByPathway(int pathwayId) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByProtein(int proteinId) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByComplex(int complexId) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    
}
