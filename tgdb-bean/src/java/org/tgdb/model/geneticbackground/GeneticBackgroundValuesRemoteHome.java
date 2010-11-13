
package org.tgdb.model.geneticbackground;

import org.tgdb.project.project.ProjectRemote;
import java.rmi.RemoteException;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;


/**
 * This is the home interface for GeneticBackgroundValues enterprise bean.
 */
public interface GeneticBackgroundValuesRemoteHome extends EJBHome {
    
    GeneticBackgroundValuesRemote findByPrimaryKey(Integer key)  throws FinderException, RemoteException;

    GeneticBackgroundValuesRemote create(int bid, String backname, ProjectRemote project) throws javax.ejb.CreateException, java.rmi.RemoteException;
    
    java.util.Collection findByProject(int pid) throws javax.ejb.FinderException, java.rmi.RemoteException;
}
