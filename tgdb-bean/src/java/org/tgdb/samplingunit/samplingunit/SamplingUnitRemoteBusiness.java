
package org.tgdb.samplingunit.samplingunit;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.IllegalValueException;
import org.tgdb.exceptions.PermissionDeniedException;
import org.tgdb.TgDbCaller;
import org.tgdb.resource.file.FileRemote;
import org.tgdb.resource.link.LinkRemote;
import org.tgdb.project.user.UserRemote;
import org.tgdb.resource.resource.ResourceRemote;
import org.tgdb.species.species.SpeciesRemote;
import java.rmi.RemoteException;
import java.util.Collection;



/**
 * This is the business interface for SamplingUnit enterprise bean.
 */
public interface SamplingUnitRemoteBusiness {
    String getName() throws java.rmi.RemoteException;

    int getSuid() throws java.rmi.RemoteException;

    void setName(java.lang.String name) throws java.rmi.RemoteException;

    String getComm() throws java.rmi.RemoteException;

    void setComm(java.lang.String comm) throws java.rmi.RemoteException;

    String getStatus() throws java.rmi.RemoteException;

    void setStatus(java.lang.String status) throws java.rmi.RemoteException, IllegalValueException;

    java.sql.Date getTs() throws java.rmi.RemoteException;

    SpeciesRemote getSpecies() throws java.rmi.RemoteException;

    void addHistory() throws java.rmi.RemoteException, PermissionDeniedException;

    void setCaller(TgDbCaller caller) throws java.rmi.RemoteException;

    Collection getHistory() throws java.rmi.RemoteException;

    UserRemote getUser() throws java.rmi.RemoteException;

    int getId() throws java.rmi.RemoteException;

    int getNumberOfProjects() throws ApplicationException, java.rmi.RemoteException;

    /**
     * Returns the number of groups in the grouping
     * @param suid The sampling unit id
     * @throws org.tgdb.exceptions.ApplicationException If the information could not be retrieved
     * @return The number of groups in the grouping
     */
    int getNumberOfGroupings() throws ApplicationException, java.rmi.RemoteException;

    Collection getFiles() throws java.rmi.RemoteException, ApplicationException;

    Collection getLinks() throws java.rmi.RemoteException, ApplicationException;

    void addFile(FileRemote file) throws java.rmi.RemoteException;

    void removeFile(FileRemote file) throws java.rmi.RemoteException;

    void addLink(LinkRemote link) throws java.rmi.RemoteException;

    void removeLink(LinkRemote link) throws java.rmi.RemoteException;

    Collection getExperimentalModels() throws java.rmi.RemoteException;

    int getNumberOfExperimentalModels() throws java.rmi.RemoteException;

    Collection getExperimentalObjects() throws java.rmi.RemoteException;

    Collection getProjects() throws ApplicationException, java.rmi.RemoteException;

//    /**
//     * Returns the experimental models for the sampling unit
//     * 
//     * @return The experimental models for the sampling unit
//     */
//    java.util.Collection getExperimentalModels(int suid, TgDbCaller caller) throws java.rmi.RemoteException;

    /**
     * Adds a resource to the sampling unit
     * 
     * @param file The file to add
     * @throws java.rmi.RemoteException If the file could not be added
     */
    void addResource(ResourceRemote resource) throws RemoteException, java.rmi.RemoteException;

    /**
     * Removes a resource from the sampling unit
     * 
     * @param file The file to remove
     * @throws java.rmi.RemoteException If the file could not be removed
     */
    void removeResource(ResourceRemote resource) throws RemoteException, java.rmi.RemoteException;

    java.util.Collection getResources() throws ApplicationException, java.rmi.RemoteException;
    
}
