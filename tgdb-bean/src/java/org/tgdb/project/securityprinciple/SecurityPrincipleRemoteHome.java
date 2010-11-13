
package org.tgdb.project.securityprinciple;

import org.tgdb.project.user.UserRemote;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.role.RoleRemote;


/**
 * This is the home interface for SecurityPrinciple enterprise bean.
 */
public interface SecurityPrincipleRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.project.securityprinciple.SecurityPrincipleRemote findByPrimaryKey(SecurityPrinciplePk pk)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.project.securityprinciple.SecurityPrincipleRemote create(ProjectRemote project, UserRemote user, RoleRemote role) throws javax.ejb.CreateException, java.rmi.RemoteException;

    java.util.Collection findByProject(int pid) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByUser(int id) throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.project.securityprinciple.SecurityPrincipleRemote findByUserProject(int id, int pid) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    
}
