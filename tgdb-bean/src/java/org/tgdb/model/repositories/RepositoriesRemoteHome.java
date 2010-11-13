
package org.tgdb.model.repositories;

import org.tgdb.TgDbCaller;
import org.tgdb.project.project.ProjectRemote;
import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;


/**
 * This is the home interface for Repositories enterprise bean.
 */
public interface RepositoriesRemoteHome extends EJBHome {
    
    RepositoriesRemote findByPrimaryKey(Integer key)  throws FinderException, RemoteException;

    RepositoriesRemote create(int rid, String reponame, int hasdb, String mouseurl, String repourl, ProjectRemote project) throws javax.ejb.CreateException, java.rmi.RemoteException;

    Collection findByProject(int pid) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    Collection findByDB() throws javax.ejb.FinderException, java.rmi.RemoteException;
}
