
package org.tgdb.model.geneticbackground;

import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;


/**
 * This is the home interface for GeneticBackground enterprise bean.
 */
public interface GeneticBackgroundRemoteHome extends EJBHome {
    
    GeneticBackgroundRemote findByPrimaryKey(Integer key)  throws FinderException, RemoteException;

    GeneticBackgroundRemote create(int gbid, int eid, int dna_origin, int targeted_back, int host_back, int backcrossing_strain, String backcrosses) throws javax.ejb.CreateException, java.rmi.RemoteException;

    Collection findByGeneticBackgroundModel(int eid) throws javax.ejb.FinderException, java.rmi.RemoteException;

    //org.tgdb.model.geneticbackground.GeneticBackgroundRemoteBusiness create(int gbid, int eid, int dna_origin, int targeted_back, int host_back, int backcrossing_strain, String backcrosses) throws javax.ejb.CreateException, java.rmi.RemoteException;
    
}
