
package org.tgdb.project.securityprinciple;

import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.role.RoleRemote;
import org.tgdb.project.user.UserRemote;


/**
 * This is the business interface for SecurityPrinciple enterprise bean.
 */
public interface SecurityPrincipleRemoteBusiness {
    int getPid() throws java.rmi.RemoteException;

    int getId() throws java.rmi.RemoteException;

    int getRid() throws java.rmi.RemoteException;

    RoleRemote getRole() throws java.rmi.RemoteException;

    UserRemote getUser() throws java.rmi.RemoteException;

    ProjectRemote getProject() throws java.rmi.RemoteException;
    
}
