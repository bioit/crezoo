
package org.tgdb.genome.integrationcopy;


/**
 * This is the business interface for IntegrationCopy enterprise bean.
 */
public interface IntegrationCopyRemoteBusiness {
    
    int getIscnid() throws java.rmi.RemoteException;

    java.lang.String getIsite() throws java.rmi.RemoteException;

    void setIsite(String isite) throws java.rmi.RemoteException;

    java.lang.String getCnumber() throws java.rmi.RemoteException;

    void setCnumber(String cnumber) throws java.rmi.RemoteException;
    
}
