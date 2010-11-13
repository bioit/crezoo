
package org.tgdb.model.strain.state;

import org.tgdb.TgDbCaller;


/**
 * This is the business interface for StrainState enterprise bean.
 */
public interface StrainStateRemoteBusiness
{
    void setCaller(TgDbCaller caller) throws java.rmi.RemoteException;

    void setAbbreviation(String abbreviation) throws java.rmi.RemoteException;

    java.lang.String getAbbreviation() throws java.rmi.RemoteException;

    void setName(String name) throws java.rmi.RemoteException;

    java.lang.String getName() throws java.rmi.RemoteException;

    int getId() throws java.rmi.RemoteException;
    
}
