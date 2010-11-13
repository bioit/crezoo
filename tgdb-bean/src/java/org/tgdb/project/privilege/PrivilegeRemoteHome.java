
package org.tgdb.project.privilege;


/**
 * This is the home interface for Privilege enterprise bean.
 */
public interface PrivilegeRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.project.privilege.PrivilegeRemote findByPrimaryKey(java.lang.Integer key)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.project.privilege.PrivilegeRemote create(int prid, java.lang.String name, java.lang.String comm) throws javax.ejb.CreateException, java.rmi.RemoteException;

    java.util.Collection findAll() throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    
}
