
package org.tgdb.model.repositories;

import org.tgdb.exceptions.ApplicationException;


/**
 * This is the business interface for Repositories enterprise bean.
 */
public interface RepositoriesRemoteBusiness {
    int getRid() throws java.rmi.RemoteException;

    int getPid() throws java.rmi.RemoteException;

    void setPid(int pid) throws java.rmi.RemoteException;

    java.lang.String getReponame() throws java.rmi.RemoteException;

    void setReponame(String reponame) throws java.rmi.RemoteException;

    org.tgdb.project.project.ProjectRemote getProject() throws ApplicationException, java.rmi.RemoteException;

    int getHasdb() throws java.rmi.RemoteException;

    void setHasdb(int hasdb) throws java.rmi.RemoteException;

    java.lang.String getMouseurl() throws java.rmi.RemoteException;

    void setMouseurl(String mouseurl) throws java.rmi.RemoteException;

    java.lang.String getRepourl() throws java.rmi.RemoteException;

    void setRepourl(String repourl) throws java.rmi.RemoteException;
    
}
