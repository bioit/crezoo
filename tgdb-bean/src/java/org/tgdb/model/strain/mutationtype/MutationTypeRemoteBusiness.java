
package org.tgdb.model.strain.mutationtype;


/**
 * This is the business interface for MutationType enterprise bean.
 */
public interface MutationTypeRemoteBusiness
{
    int getMutantid() throws java.rmi.RemoteException;

    java.lang.String getAbbreviation() throws java.rmi.RemoteException;

    void setAbbreviation(String abbreviation) throws java.rmi.RemoteException;

    java.lang.String getName() throws java.rmi.RemoteException;

    void setName(String name) throws java.rmi.RemoteException;
    
}
