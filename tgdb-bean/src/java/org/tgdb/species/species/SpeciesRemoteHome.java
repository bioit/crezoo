
package org.tgdb.species.species;


/**
 * This is the home interface for Species enterprise bean.
 */
public interface SpeciesRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.species.species.SpeciesRemote findByPrimaryKey(java.lang.Integer key)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.species.species.SpeciesRemote create(int sid, java.lang.String name, java.lang.String comm) throws javax.ejb.CreateException, java.rmi.RemoteException;

    org.tgdb.species.species.SpeciesRemote findByName(java.lang.String name) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findAll() throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByProject(int pid) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    
}
