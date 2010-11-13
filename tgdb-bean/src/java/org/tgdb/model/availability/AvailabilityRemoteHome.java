
package org.tgdb.model.availability;

import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;


/**
 * This is the home interface for Availability enterprise bean.
 */
public interface AvailabilityRemoteHome extends EJBHome {
    
    AvailabilityRemote findByPrimaryKey(AvailabilityPk key)  throws FinderException, RemoteException;

    AvailabilityRemote create(org.tgdb.model.expmodel.ExpModelRemote model, org.tgdb.model.repositories.RepositoriesRemote repository, org.tgdb.model.availablegeneticbackgrounds.AvailableGeneticBackgroundRemote avgenback, org.tgdb.model.strain.state.StrainStateRemote state, org.tgdb.model.strain.type.StrainTypeRemote type) throws javax.ejb.CreateException, java.rmi.RemoteException;

    Collection findByModel(int eid) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findByRepository(int rid) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    
}
