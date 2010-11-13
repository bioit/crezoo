package org.tgdb.adminmanager;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.PermissionDeniedException;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.privilege.PrivilegeRemoteHome;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.project.projectmanager.ProjectUserDTO;
import org.tgdb.project.role.RoleRemoteHome;
import org.tgdb.project.securityprinciple.SecurityPrincipleRemoteHome;
import org.tgdb.project.user.UserRemote;
import org.tgdb.resource.link.LinkRemote;
import org.tgdb.resource.resourcemanager.ResourceManagerRemote;
import org.tgdb.servicelocator.ServiceLocator;
import org.tgdb.species.species.SpeciesRemote;
import org.tgdb.species.species.SpeciesRemoteHome;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.tgdb.project.projectmanager.UserDTO;
import org.tgdb.project.user.UserRemoteHome;

/**
 * This is the bean class for the AdminManagerBean enterprise bean.
 * Created Jan 10, 2006 10:31:21 AM
 * @author heto
 */
public class AdminManagerBean extends AbstractTgDbBean implements javax.ejb.SessionBean, org.tgdb.adminmanager.AdminManagerRemoteBusiness {
    private javax.ejb.SessionContext context;
    
    private ProjectRemoteHome projectHome;
    private PrivilegeRemoteHome privHome;
    private RoleRemoteHome roleHome;
    private UserRemoteHome userHome;
    private SecurityPrincipleRemoteHome securityHome;
    private ResourceManagerRemote resourceManager;
    private SpeciesRemoteHome speciesHome;
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise bean, Web services)
    // TODO Add business methods or web service operations
    /**
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(javax.ejb.SessionContext aContext) {
        context = aContext;
        
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);
        privHome = (PrivilegeRemoteHome)locator.getHome(ServiceLocator.Services.PRIVILEGE);
        roleHome = (RoleRemoteHome)locator.getHome(ServiceLocator.Services.ROLE);
        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
        securityHome = (SecurityPrincipleRemoteHome)locator.getHome(ServiceLocator.Services.SECURITYPRINCIPLE);
        resourceManager = (ResourceManagerRemote)locator.getManager(ServiceLocator.Services.RESOURCEMANAGER);
        speciesHome = (SpeciesRemoteHome)locator.getHome(ServiceLocator.Services.SPECIES);
    }
    
    /**
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() {
        
    }
    
    /**
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    public void ejbPassivate() {
        
    }
    
    /**
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    public void ejbRemove() {
        
    }
    // </editor-fold>
    
    /**
     * See section 7.10.3 of the EJB 2.0 specification
     * See section 7.11.3 of the EJB 2.1 specification
     */
    public void ejbCreate() {
        // TODO implement ejbCreate if necessary, acquire resources
        // This method has access to the JNDI context so resource aquisition
        // spanning all methods can be performed here such as home interfaces
        // and data sources.
    }
    
    
    
    // Add business logic below. (Right-click in editor and choose
    // "EJB Methods > Add Business Method" or "Web Service > Add Operation")
    
    /**
     * Get a collection of all users. Wraps data in ProjectUserDTO
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If the information could not be retrieved
     * @return A collection of ProjectUser DTO's
     */
    public Collection getUsers(TgDbCaller caller) throws ApplicationException
    {
        //if (!caller.hasPrivilege("PROJECT_ADM"))
        //        throw new PermissionDeniedException("User is not allowed to get roles");
        Collection users = new ArrayList();
        try
        {
            Collection arr = userHome.findAll(caller);
            Iterator i = arr.iterator();
            while (i.hasNext())
            {
                UserRemote user = (UserRemote)i.next();
                ProjectUserDTO prjUsr = new ProjectUserDTO(user);
                users.add(prjUsr);
            }
        }
        catch (Exception e)
        {
            throw new ApplicationException("Unable to get all users", e);
        }
        return users;
    }

    public Collection getCorrespondents(String distinguish, TgDbCaller caller) throws ApplicationException {
        Collection users = new ArrayList();
        try {
            Collection arr = userHome.findByDistinguish(distinguish, caller);
            Iterator i = arr.iterator();
            while (i.hasNext()) {
                UserDTO usr = new UserDTO((UserRemote)i.next());
                users.add(usr);
            }
        }
        catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return users;
    }
    
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
    public void updateUser(int id, java.lang.String name, java.lang.String email, 
            java.lang.String userLink, java.lang.String groupName, java.lang.String groupAddress,
            java.lang.String groupPhone, java.lang.String groupLink, 
            org.tgdb.TgDbCaller caller, String usr, String pwd) throws ApplicationException {
        validate("PROJECT_ADM", caller);
        
        try {
            UserRemote user  = userHome.findByPrimaryKey(new Integer(id));
            user.setName(name);
            user.setEmail(email);
            user.setGroupName(groupName);
            user.setGroupAddress(groupAddress);
            user.setGroupPhone(groupPhone);
            user.setPwd(pwd);
            
            if(user.getUserLink() != null) {
                LinkRemote link = user.getUserLink();
                link.setCaller(caller);
                link.setName(userLink);
                link.setUrl(userLink);
            }
            else {
                int linkid = resourceManager.createLink(userLink, "User link for user id="+id, userLink, caller).getLinkId();
                user.setUserLink(linkid);
            }
            
            if(user.getGroupLink() != null) {
                LinkRemote link = user.getGroupLink();
                link.setCaller(caller);
                link.setName(groupLink);
                link.setUrl(groupLink);
            }
            else {
                int linkid = resourceManager.createLink(groupLink, "User link for research group id="+id, groupLink, caller).getLinkId();
                user.setGroupLink(linkid);
            }            
            
            /*
            SecurityPrincipleRemote sec = securityHome.findByUserProject(id, caller.getPid());
            if(role != sec.getRid()) {
                sec.remove();

                ProjectRemote projectRem = projectHome.findByPrimaryKey(new Integer(caller.getPid()), caller);
                UserRemote userRem = userHome.findByPrimaryKey(new Integer(id));
                RoleRemote roleRem = roleHome.findByPrimaryKey(new Integer(role));

                securityHome.create(projectRem, userRem, roleRem);        
            }
             */
        } catch (Exception e) {
            throw new ApplicationException("Unable to update user.", e);
        }
    }
    
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
    public void createUser(java.lang.String name, java.lang.String email, java.lang.String userLink, java.lang.String groupName, java.lang.String groupAddress, java.lang.String groupPhone, java.lang.String groupLink, String usr, String pwd, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("PROJECT_ADM", caller);
        
        try {
            makeConnection();
            int id = getIIdGenerator().getNextId(conn, "users_seq");
            UserRemote user  = userHome.create(id, usr, pwd, name, "E");
            
            user.setEmail(email);
            user.setGroupName(groupName);
            user.setGroupAddress(groupAddress);
            user.setGroupPhone(groupPhone);
            /* only for regular users */
            user.setDistinguish("regular");
            
            
            if(user.getUserLink() != null) {
                LinkRemote link = user.getUserLink();
                link.setUrl(userLink);
            }
            else if(userLink != null && userLink.length() > 0){
                int linkid = resourceManager.createLink(userLink, "User link for user id="+id, userLink, caller).getLinkId();
                user.setUserLink(linkid);
            }
            
            if(user.getGroupLink() != null) {
                LinkRemote link = user.getGroupLink();
                link.setUrl(groupLink);
            }
            else if(groupLink != null && groupLink.length() > 0){
                int linkid = resourceManager.createLink(groupLink, "User link for research group id="+id, groupLink, caller).getLinkId();
                user.setGroupLink(linkid);
            }            
            
            /*
            ProjectRemote projectRem = projectHome.findByPrimaryKey(new Integer(caller.getPid()), caller);
            UserRemote userRem = userHome.findByPrimaryKey(new Integer(id));
            RoleRemote roleRem = roleHome.findByPrimaryKey(new Integer(role));

            securityHome.create(projectRem, userRem, roleRem);        
             */
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Unable to create user.", e);
        }
    }

    public UserDTO getCorrespondent(int cid, TgDbCaller caller) throws ApplicationException {
        UserDTO dto = new UserDTO();
        try {
            UserRemote user  = userHome.findByPrimaryKey(new Integer(cid));
            dto = new UserDTO(user);
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return dto;
    }

    public void createCorrespondent(String name, String email, String distinguish, TgDbCaller caller) throws ApplicationException {
        try {
            makeConnection();
            int id = getIIdGenerator().getNextId(conn, "users_seq");
            
//            String _name = name; if(_name.length() > 10) _name = _name.substring(0, 9);
//            UserRemote user  = userHome.create(id, _name, _name, name, "D");

            //_random is used for username and password
            String _random = getRandomString(10);
            UserRemote user  = userHome.create(id, _random, _random, name, "D");
            user.setEmail(email);
            user.setDistinguish(distinguish);
            user.setStatus("D");
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        finally {
            releaseConnection();
        }
    }

    private String getRandomString(int n) {
        char[] pw = new char[n];
        int c  = 'A';
        int  r1 = 0;

        for (int i=0; i < n; i++) {
            r1 = (int)(Math.random() * 3);

            switch(r1) {
                case 0: c = '0' +  (int)(Math.random() * 10); break;
                case 1: c = 'a' +  (int)(Math.random() * 26); break;
                case 2: c = 'A' +  (int)(Math.random() * 26); break;
            }
            pw[i] = (char)c;
        }
        return new String(pw);
    }

    public void updateCorrespondent(int cid, String name, String email, TgDbCaller caller) throws ApplicationException {
        try {
            UserRemote user  = userHome.findByPrimaryKey(new Integer(cid));
            user.setName(name);
            user.setEmail(email);
            user.setStatus("D");
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
    }

    public void createProject(String name, String comm, TgDbCaller caller) throws ApplicationException
    {
        validate("PROJECT_ADM", caller);
        try {
            makeConnection();
            int pid = getIIdGenerator().getNextId(conn, "projects_seq");
            ProjectRemote project  = projectHome.create(pid, name, comm, "E", caller);
            
        } catch (Exception e) {
            throw new ApplicationException("Unable to create project.", e);
        }
        finally
        {
            releaseConnection();
        }
    }
    
    
    
    /**
     * Updates a project
     * @param pid 
     * @param comm 
     * @param status 
     * @param name The name of the user
     * @param caller The caller
     * @throws org.tgdb.exceptions.ApplicationException If the user could not be updated
     */
    public void updateProject(int pid, String name, String comm, String status, 
            TgDbCaller caller) throws ApplicationException {
        validate("PROJECT_ADM", caller);
        
        try {
            ProjectRemote project  = projectHome.findByPrimaryKey(new Integer(pid));
            
            project.setCaller(caller);
            project.setName(name);
            project.setComm(comm);
            project.setStatus(status);
            
        } catch (Exception e) {
            throw new ApplicationException("Unable to update project.", e);
        }
    }
    
    public void removeProject(int pid, TgDbCaller caller) throws ApplicationException
    {
        validate("PROJECT_ADM", caller);
        try {
            
            ProjectRemote project  = projectHome.findByPrimaryKey(new Integer(pid));
            project.remove();
            
        } catch (Exception e) {
            throw new ApplicationException("Unable to delete project.", e);
        }
    }
    
    public Collection getSpeciesForProject(int pid, TgDbCaller caller) throws ApplicationException
    {
        Collection arr = new ArrayList();
        try
        {
            ProjectRemote prj = projectHome.findByPrimaryKey(new Integer(pid));
            Collection species = prj.getSpecies();
            Iterator i = species.iterator();
            while (i.hasNext())
            {
                SpeciesRemote s = (SpeciesRemote)i.next();
                arr.add(new SpeciesDTO(s));
            }
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to get species for project [pid="+pid+"]",e);
        }
        return arr;
    }
    
    /**
     * Method for retrieval of all species in the database
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If something went wrong during the retrieval
     * @return A collection of species remote interfaces
     */
    public Collection getAllSpecies(TgDbCaller caller) throws ApplicationException
    {
        Collection arr = new ArrayList();
        try
        {
            Collection species = speciesHome.findAll();
            Iterator i = species.iterator();
            while (i.hasNext())
            {
                SpeciesRemote s = (SpeciesRemote)i.next();
                arr.add(new SpeciesDTO(s));
            }
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to get all species",e);
        }
        return arr;
    }
    
    public Collection getOtherSpecies(int pid, TgDbCaller caller) throws ApplicationException
    {
        try
        {
            Collection specPrj = getSpeciesForProject(pid, caller);
            Collection allSpc = getAllSpecies(caller);
            
            allSpc.removeAll(specPrj);
            
            return allSpc;
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to get non project species",e);
        }
    }
    
    /**
     * Get a species from the db
     * @return A collection of species remote interfaces
     * @param sid the species id
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If something went wrong during the retrieval
     */
    public SpeciesDTO getSpecies(int sid, TgDbCaller caller) throws ApplicationException {        
        SpeciesRemote sr;
        SpeciesDTO species;
        try{
            sr = speciesHome.findByPrimaryKey(new Integer(sid));
            species  = new SpeciesDTO(sr);
        } 
        catch(Exception e) 
        {
            e.printStackTrace();
            throw new ApplicationException("Could not get species", e);
        }
        return species;
    }
    
    public void updateSpecies(int sid, String name, String comm, TgDbCaller caller) throws ApplicationException
    {
        if (!caller.isAdmin())
            throw new PermissionDeniedException("Permission denied. User is not admin.");
        
        try
        {
            SpeciesRemote spc = speciesHome.findByPrimaryKey(new Integer(sid));
            spc.setName(name);
            spc.setComm(comm);
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to update species");
        }
    }
    
    /**
     * Create a new species. 
     * Admin status is required.
     * @param name is the name of the species
     * @param comm is a comment
     * @param caller is the admin creating the species.
     * @throws org.tgdb.exceptions.ApplicationException if something goes wrong
     */
    public void createSpecies(String name, String comm, TgDbCaller caller) throws ApplicationException
    {
        if (!caller.isAdmin())
            throw new PermissionDeniedException("Permission denied. User is not admin.");
        
        try
        {
            makeConnection();
            int sid = getIIdGenerator().getNextId(conn, "species_seq");
            
            SpeciesRemote spc = speciesHome.create(sid, name, comm);
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to create species");
        }
    }
    
    public void removeSpecies(int sid, TgDbCaller caller) throws ApplicationException
    {
        if (!caller.isAdmin())
            throw new PermissionDeniedException("Permission denied. User is not admin.");
        
        try
        {
            SpeciesRemote spc = speciesHome.findByPrimaryKey(new Integer(sid));
            spc.remove();
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to remove species", e);
        }
    }
}
