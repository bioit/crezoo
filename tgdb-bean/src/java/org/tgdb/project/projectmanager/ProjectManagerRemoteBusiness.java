
package org.tgdb.project.projectmanager;

import org.tgdb.frame.io.FileDataObject;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.LoginException;
import org.tgdb.TgDbCaller;
import java.util.Collection;
import org.tgdb.project.user.UserRemote;



/**
 * This is the business interface for ProjectManager enterprise bean.
 */
public interface ProjectManagerRemoteBusiness {

    TgDbCaller login(java.lang.String usr, java.lang.String pwd) throws java.rmi.RemoteException, LoginException;

    //boolean hasUserRole(UserRemote user, ProjectRemote prj, java.lang.String roleName) throws java.rmi.RemoteException;

    //boolean hasUserPrivilege(UserRemote user, ProjectRemote project, java.lang.String privilegeName) throws java.rmi.RemoteException;

    Collection getProjects(TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getRolesByUser(TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    Collection getPrivileges(int rid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    /**
     * Get all privileges a role does not have
     */
    Collection getOtherPrivileges(int rid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void addPrivilegesToRole(java.lang.String[] privIds, int rid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void removePrivilegesFromRole(java.lang.String[] privIds, int rid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    /**
     * Writes the statistics page using the writeStatisticsPage in
     * ServletUtil.
     * 
     * @param request The request from client.
     * @param response The response to client.
     * @exception ServletException If error when handling request.
     * @exception IOException If I/O error when handling request.
     */
    

    int createRole(java.lang.String name, String comm, int pid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getRolesByProject(int pid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void setPassword(java.lang.String old, java.lang.String p1, java.lang.String p2, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void removeRole(int rid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    /**
     * Get all projects that a user is a member of
     * 
     * @param caller the caller object
     * @throws org.tgdb.exceptions.ApplicationException throws error messages to display for the user
     * @return collection of ProjectDTO
     */
    Collection getProjectsByUser(org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    /**
     * 
     * Get a collection of RoleDTO for all roles in a project
     * 
     * Privilege PROJECT_ADM is required
     * 
     * @param pid the project id
     * @param caller the caller of the method.
     * @throws org.tgdb.exceptions.ApplicationException 
     * @return a collection of RoleDTO
     */
    Collection getUsersByProject(int pid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    /**
     * Get a collection of ProjectUserDTO
     * @param pid 
     * @param caller 
     * @throws org.tgdb.exceptions.ApplicationException 
     * @return 
     */
    Collection getProjectUsers(int pid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getUsersByCategory(String distinguish, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void updateRole(int rid, java.lang.String name, java.lang.String comm, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    UserDTO getUser(int id, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;
    
    UserRemote getPublicUser(int id) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void updateUser(int id, int role, java.lang.String name, java.lang.String email, java.lang.String userLink, java.lang.String groupName, java.lang.String groupAddress, java.lang.String groupPhone, java.lang.String groupLink, TgDbCaller caller, String usr, String pwd) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    int createUser(java.lang.String name, java.lang.String email, java.lang.String userLink, java.lang.String groupName, java.lang.String groupAddress, java.lang.String groupPhone, java.lang.String groupLink, String usr, String pwd, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void removeUser(int id, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    /**
     * Get information about one project
     * 
     * @return a ProjectDTO for a project
     * @param pid the project id for the project to get
     * @param caller the caller object
     * @throws org.tgdb.exceptions.ApplicationException throws error messages to display for the user
     */
    ProjectDTO getProject(int pid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void assignUserToProject(int id, int role, int pid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void unAssignUserFromProject(int id, int pid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void assignSpeciesToProject(int sid, int pid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    /**
     * Get information about one project
     * 
     * @return a ProjectDTO for a project
     * @param pid the project id for the project to get
     * @param caller the caller object
     * @throws org.tgdb.exceptions.ApplicationException throws error messages to display for the user
     */
    ProjectDTO getDefaultProject(org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getNonProjectUsers(int pid, org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getFiles(int pid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    
    Collection getLinks(int pid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    Collection getCategoriesAndResources(int pid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void addResource(java.lang.String type, int category, int project, java.lang.String name, java.lang.String comm, FileDataObject fileData, TgDbCaller caller, String url) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void unAssignSpeciesFromProject(int pid, int sid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void unAssignSamplingUnitFromProject(int pid, int suid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void log(String txt) throws java.rmi.RemoteException;
    
}
