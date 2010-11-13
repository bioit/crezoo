
package org.tgdb.model.strain.type;

import org.tgdb.TgDbCaller;


/**
 * This is the business interface for StrainType enterprise bean.
 */
public interface StrainTypeRemoteBusiness
{
    void setCaller(TgDbCaller caller) throws java.rmi.RemoteException;

    void setAbbreviation(String abbreviation) throws java.rmi.RemoteException;

    java.lang.String getAbbreviation() throws java.rmi.RemoteException;

    void setName(String name) throws java.rmi.RemoteException;

    java.lang.String getName() throws java.rmi.RemoteException;

    int getId() throws java.rmi.RemoteException;
    
}
