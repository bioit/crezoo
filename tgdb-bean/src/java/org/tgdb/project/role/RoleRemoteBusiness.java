
package org.tgdb.project.role;

import org.tgdb.exceptions.ApplicationException;
import java.util.Collection;
import org.tgdb.project.privilege.PrivilegeRemote;


/**
 * This is the business interface for Role enterprise bean.
 */
public interface RoleRemoteBusiness {
    int getRid() throws java.rmi.RemoteException;


    int getPid() throws java.rmi.RemoteException;

    void setPid(int pid) throws java.rmi.RemoteException;

    String getName() throws java.rmi.RemoteException;

    void setName(java.lang.String name) throws java.rmi.RemoteException;

    String getComm() throws java.rmi.RemoteException;

    void setComm(java.lang.String comm) throws java.rmi.RemoteException;

    void setPrivileges(Collection roles) throws java.rmi.RemoteException, ApplicationException;

    Collection getPrivileges() throws java.rmi.RemoteException;

    void addPrivilege(PrivilegeRemote privilege) throws java.rmi.RemoteException;

    void removePrivilege(org.tgdb.project.privilege.PrivilegeRemote privilege) throws java.rmi.RemoteException;
    
}
