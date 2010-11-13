
package org.tgdb.model.availablegeneticbackgrounds;

import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;


/**
 * This is the home interface for AvailableGeneticBackground enterprise bean.
 */
public interface AvailableGeneticBackgroundRemoteHome extends EJBHome {
    
    AvailableGeneticBackgroundRemote findByPrimaryKey(Integer key)  throws FinderException, RemoteException;

    AvailableGeneticBackgroundRemote create(int aid, String avbackname, int pid, org.tgdb.project.project.ProjectRemote project) throws javax.ejb.CreateException, java.rmi.RemoteException;

    Collection findByProject(int pid) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    
}
