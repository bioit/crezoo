
package org.tgdb.model.geneticbackground;

import org.tgdb.exceptions.ApplicationException;


/**
 * This is the business interface for GeneticBackgroundValues enterprise bean.
 */
public interface GeneticBackgroundValuesRemoteBusiness {
    int getBid() throws java.rmi.RemoteException;

    java.lang.String getBackname() throws java.rmi.RemoteException;

    void setBackname(String backname) throws java.rmi.RemoteException;

    void setPid(int pid) throws java.rmi.RemoteException;

    int getPid() throws java.rmi.RemoteException;

    org.tgdb.project.project.ProjectRemote getProject() throws ApplicationException, java.rmi.RemoteException;
    
}
