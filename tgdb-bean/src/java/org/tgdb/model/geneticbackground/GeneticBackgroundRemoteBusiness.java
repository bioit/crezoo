
package org.tgdb.model.geneticbackground;


/**
 * This is the business interface for GeneticBackground enterprise bean.
 */
public interface GeneticBackgroundRemoteBusiness {
    
    //interfaces for the get+set methods.
    int getGbid() throws java.rmi.RemoteException;
    
    int getEid() throws java.rmi.RemoteException;

    void setEid(int eid) throws java.rmi.RemoteException;

    int getDna_origin() throws java.rmi.RemoteException;

    void setDna_origin(int dna_origin) throws java.rmi.RemoteException;

    int getTargeted_back() throws java.rmi.RemoteException;

    void setTargeted_back(int targeted_back) throws java.rmi.RemoteException;

    int getHost_back() throws java.rmi.RemoteException;

    void setHost_back(int host_back) throws java.rmi.RemoteException;

    int getBackcrossing_strain() throws java.rmi.RemoteException;

    void setBackcrossing_strain(int backcrossing_strain) throws java.rmi.RemoteException;

    java.lang.String getBackcrosses() throws java.rmi.RemoteException;

    void setBackcrosses(String backcrosses) throws java.rmi.RemoteException;

    java.lang.String getBackNameFromBackId(int backId) throws java.rmi.RemoteException;
    
}
