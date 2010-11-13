
package org.tgdb.species.chromosome;

import org.tgdb.TgDbCaller;
import java.util.Collection;


/**
 * This is the home interface for Chromosome enterprise bean.
 */
public interface ChromosomeRemoteHome extends javax.ejb.EJBHome {
    
    org.tgdb.species.chromosome.ChromosomeRemote findByPrimaryKey(Integer key)  throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    org.tgdb.species.chromosome.ChromosomeRemote create(int cid, String name, String abbr, String comm, int sid) throws javax.ejb.CreateException, java.rmi.RemoteException;

    org.tgdb.species.chromosome.ChromosomeRemote findBySpeciesAndName(java.lang.String name, int sid) throws javax.ejb.FinderException, java.rmi.RemoteException;   
    
    Collection findByAbbreviation(java.lang.String abbreviation) throws javax.ejb.FinderException, java.rmi.RemoteException;   

    Collection findBySpecies(int sid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
}
