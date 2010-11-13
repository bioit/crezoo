
package org.tgdb.project.project;

import org.tgdb.TgDbCaller;


/**
 * This is the home interface for Project enterprise bean.
 */
public interface ProjectRemoteHome extends javax.ejb.EJBHome {
    

    org.tgdb.project.project.ProjectRemote findByPrimaryKey(java.lang.Integer pid)  throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    /**
     *
     */
    org.tgdb.project.project.ProjectRemote findByPrimaryKey(java.lang.Integer key, TgDbCaller caller)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.project.project.ProjectRemote create(int pid, java.lang.String name, java.lang.String comm, java.lang.String status,TgDbCaller usr) throws javax.ejb.CreateException, java.rmi.RemoteException;

    java.util.Collection findByAll(TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findBySamplingUnit(int suid) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByName(java.lang.String name, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
}
