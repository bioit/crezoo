
package org.tgdb.model.availability;


/**
 * This is the business interface for Availability enterprise bean.
 */
public interface AvailabilityRemoteBusiness {
    int getEid() throws java.rmi.RemoteException;

    void setEid(int eid) throws java.rmi.RemoteException;

    int getRid() throws java.rmi.RemoteException;

    void setRid(int rid) throws java.rmi.RemoteException;

    int getAid() throws java.rmi.RemoteException;

    void setAid(int aid) throws java.rmi.RemoteException;

    org.tgdb.model.expmodel.ExpModelRemote getModel() throws java.rmi.RemoteException;

    org.tgdb.model.repositories.RepositoriesRemote getRepository() throws java.rmi.RemoteException;

    org.tgdb.model.availablegeneticbackgrounds.AvailableGeneticBackgroundRemote getAvailableGeneticBackground() throws java.rmi.RemoteException;

    java.lang.String getRepositoryName() throws java.rmi.RemoteException;

    java.lang.String getAvailableGeneticBackgroundName() throws java.rmi.RemoteException;

    int getStateid() throws java.rmi.RemoteException;

    void setStateid(int stateid) throws java.rmi.RemoteException;

    int getTypeid() throws java.rmi.RemoteException;

    void setTypeid(int typeid) throws java.rmi.RemoteException;

    java.lang.String getStateName() throws java.rmi.RemoteException;

    java.lang.String getTypeName() throws java.rmi.RemoteException;

    java.lang.String getStateAbbr() throws java.rmi.RemoteException;

    java.lang.String getTypeAbbr() throws java.rmi.RemoteException;
    
}
