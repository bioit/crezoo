
package org.tgdb.adminmanager;

import org.tgdb.exceptions.ApplicationException;
import java.util.Collection;
import org.tgdb.TgDbCaller;
import org.tgdb.project.projectmanager.UserDTO;


/**
 * This is the business interface for AdminManager enterprise bean.
 */
public interface AdminManagerRemoteBusiness {
    /**
     * Get a collection of all users. Wraps data in ProjectUserDTO
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If the information could not be retrieved
     * @return A collection of ProjectUser DTO's
     */
    Collection getUsers(org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getCorrespondents(String distinguish, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    /**
     * Updates a user
     * @param id The user id
     * @param name The name of the user
     * @param email The email of the user
     * @param userLink The link to the users website
     * @param groupName The name of the research group
     * @param groupAddress The address for the research group
     * @param groupPhone The phonenumber for the research group
     * @param groupLink The link to the research group webpage
     * @param caller The caller
     * @param usr The username
     * @param pwd The password
     * @throws org.tgdb.exceptions.ApplicationException If the user could not be updated
     */
    void updateUser(int id, java.lang.String name, java.lang.String email, java.lang.String userLink, java.lang.String groupName, java.lang.String groupAddress, java.lang.String groupPhone, java.lang.String groupLink, org.tgdb.TgDbCaller caller, java.lang.String usr, java.lang.String pwd) throws ApplicationException, java.rmi.RemoteException;

    /**
     * Creates a new user
     * @param role The role in the project
     * @param name The name
     * @param email The email
     * @param userLink The link to the users webpage
     * @param groupName The name of the researchgroup
     * @param groupAddress The address for the research group
     * @param groupPhone The phonenumber for the research group
     * @param groupLink The link to the groups webbpage
     * @param usr The username
     * @param pwd The password
     * @param caller The caller
     * @throws org.tgdb.exceptions.ApplicationException If the user could not be created
     */
    void createUser(java.lang.String name, java.lang.String email, java.lang.String userLink, java.lang.String groupName, java.lang.String groupAddress, java.lang.String groupPhone, java.lang.String groupLink, java.lang.String usr, java.lang.String pwd, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    UserDTO getCorrespondent(int cid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void createCorrespondent(String name, String email, String distinguish, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void updateCorrespondent(int cid, String name, String email, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
    /**
     * Updates a project
     * @param id The user id
     * @param name The name of the user
     * @param email The email of the user
     * @param userLink The link to the users website
     * @param groupName The name of the research group
     * @param groupAddress The address for the research group
     * @param groupPhone The phonenumber for the research group
     * @param groupLink The link to the research group webpage
     * @param caller The caller
     * @param usr The username
     * @param pwd The password
     * @throws org.tgdb.exceptions.ApplicationException If the user could not be updated
     */
    void updateProject(int pid, java.lang.String name, java.lang.String comm, java.lang.String status, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void createProject(java.lang.String name, java.lang.String comm, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeProject(int pid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getSpeciesForProject(int pid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getAllSpecies(org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    /**
     * Get a species from the db
     * @return A collection of species remote interfaces
     * @param sid the species id
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If something went wrong during the retrieval
     */
    SpeciesDTO getSpecies(int sid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void updateSpecies(int sid, java.lang.String name, java.lang.String comm, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void createSpecies(java.lang.String name, java.lang.String comm, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeSpecies(int sid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getOtherSpecies(int pid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
    
}
