
package org.tgdb.project.projectmanager;


/**
 * This is the home interface for ProjectManager enterprise bean.
 */
public interface ProjectManagerRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.project.projectmanager.ProjectManagerRemote create()  throws javax.ejb.CreateException, java.rmi.RemoteException;
    
    
}
