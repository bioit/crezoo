
package org.tgdb.model.availablegeneticbackgrounds;

import org.tgdb.exceptions.ApplicationException;


/**
 * This is the business interface for AvailableGeneticBackground enterprise bean.
 */
public interface AvailableGeneticBackgroundRemoteBusiness {
    int getAid() throws java.rmi.RemoteException;

    int getPid() throws java.rmi.RemoteException;

    void setPid(int pid) throws java.rmi.RemoteException;

    java.lang.String getAvbackname() throws java.rmi.RemoteException;

    void setAvbackname(String avbackname) throws java.rmi.RemoteException;

    org.tgdb.project.project.ProjectRemote getProject() throws ApplicationException, java.rmi.RemoteException;
    
}
