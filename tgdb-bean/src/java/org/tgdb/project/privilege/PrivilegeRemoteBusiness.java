
package org.tgdb.project.privilege;


/**
 * This is the business interface for Privilege enterprise bean.
 */
public interface PrivilegeRemoteBusiness {
    int getPrid() throws java.rmi.RemoteException;

    String getName() throws java.rmi.RemoteException;

    void setName(java.lang.String name) throws java.rmi.RemoteException;

    String getComm() throws java.rmi.RemoteException;

    void setComm(java.lang.String comm) throws java.rmi.RemoteException;
    
}
