
package org.tgdb.model.strain.strain;

import org.tgdb.model.strain.state.StrainStateRemote;
import org.tgdb.model.strain.type.StrainTypeRemote;
import java.rmi.RemoteException;
import java.util.Collection;


/**
 * This is the business interface for Strain enterprise bean.
 */
public interface StrainRemoteBusiness
{
    int getStrainid() throws java.rmi.RemoteException;

    java.lang.String getDesignation() throws java.rmi.RemoteException;

    void setDesignation(String designation) throws java.rmi.RemoteException;

//    java.util.Collection getStrainAlleles() throws java.rmi.RemoteException;

    void addType(StrainTypeRemote type) throws RemoteException, java.rmi.RemoteException;

    void removeType(StrainTypeRemote type) throws RemoteException, java.rmi.RemoteException;

    java.util.Collection getTypes() throws java.rmi.RemoteException;

    java.util.Collection getStates() throws java.rmi.RemoteException;

    void addState(StrainStateRemote state) throws RemoteException, java.rmi.RemoteException;

    void removeState(StrainStateRemote state) throws RemoteException, java.rmi.RemoteException;

//    String getMgiId() throws java.rmi.RemoteException;
//
//    void setMgiId(String mgiid) throws java.rmi.RemoteException;

    void insertStrain_link(String repository, String externalid, String strainurl) throws java.rmi.RemoteException;

    void deleteStrain_link(int id) throws java.rmi.RemoteException;

    Collection getStrain_links() throws java.rmi.RemoteException;

    String getStrain_links_string() throws java.rmi.RemoteException;
    
}
