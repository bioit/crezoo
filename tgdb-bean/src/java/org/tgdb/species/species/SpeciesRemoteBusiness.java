
package org.tgdb.species.species;

import org.tgdb.exceptions.ApplicationException;
import java.util.Collection;


/**
 * This is the business interface for Species enterprise bean.
 */
public interface SpeciesRemoteBusiness {
    int getSid() throws java.rmi.RemoteException;

    String getName() throws java.rmi.RemoteException;

    void setName(java.lang.String name) throws java.rmi.RemoteException;

    String getComm() throws java.rmi.RemoteException;

    void setComm(java.lang.String comm) throws java.rmi.RemoteException;

    Collection getChromosomes() throws java.rmi.RemoteException, ApplicationException;
}
