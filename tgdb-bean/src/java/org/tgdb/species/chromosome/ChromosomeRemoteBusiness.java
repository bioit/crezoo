
package org.tgdb.species.chromosome;

import org.tgdb.species.species.SpeciesRemote;


/**
 * This is the business interface for Chromosome enterprise bean.
 */
public interface ChromosomeRemoteBusiness {

    String getName() throws java.rmi.RemoteException;

    void setName(java.lang.String name) throws java.rmi.RemoteException;

    String getComm() throws java.rmi.RemoteException;

    void setComm(java.lang.String comm) throws java.rmi.RemoteException; 

    SpeciesRemote getSpecies() throws java.rmi.RemoteException;

    /**
     * Method for getting the chromosome id.
     * @return the chromosome id (cid)
     */
    int getCid() throws java.rmi.RemoteException;

    java.lang.String getAbbr() throws java.rmi.RemoteException;

    void setAbbr(String abbr) throws java.rmi.RemoteException;
}
