
package org.tgdb.samplingunit.samplingunit;

import org.tgdb.TgDbCaller;
import org.tgdb.species.species.SpeciesRemote;


/**
 * This is the home interface for SamplingUnit enterprise bean.
 */
public interface SamplingUnitRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.samplingunit.samplingunit.SamplingUnitRemote findByPrimaryKey(java.lang.Integer key)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.samplingunit.samplingunit.SamplingUnitRemote create(Integer suid, java.lang.String name, java.lang.String comm, SpeciesRemote species, TgDbCaller caller) throws javax.ejb.CreateException, java.rmi.RemoteException;

    java.util.Collection findByProject(int pid) throws javax.ejb.FinderException, java.rmi.RemoteException;        

    java.util.Collection findByProjectSpecies(int pid, int sid) throws javax.ejb.FinderException, java.rmi.RemoteException;

    SamplingUnitRemote findByName(String name, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
}
