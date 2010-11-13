package org.tgdb.project.project;

import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.IllegalValueException;
import org.tgdb.TgDbCaller;
import org.tgdb.resource.file.FileRemoteHome;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.resource.link.LinkRemoteHome;
import org.tgdb.project.role.RoleRemoteHome;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.ejb.*;
import org.tgdb.project.securityprinciple.SecurityPrincipleRemoteHome;
import org.tgdb.resource.resource.ResourceRemoteHome;
import org.tgdb.resource.resourcecategory.ResourceCategoryRemoteHome;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemote;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import org.tgdb.species.species.SpeciesRemote;
import org.tgdb.species.species.SpeciesRemoteHome;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;


/**
 * This is the bean class for the ProjectBean enterprise bean.
 * Created May 20, 2005 2:09:27 PM
 * @author heto
 */
public class ProjectBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.project.project.ProjectRemoteBusiness {
    private javax.ejb.EntityContext context;
    
    /**
     * The project id (pid)
     */
    private int pid;
    /**
     * Project name
     */
    private String name;
    /**
     * Comment
     */
    private String comm;
    /**
     * Project status. E | D (Enable/Disable)
     */
    private String status;
    
    private TgDbCaller caller;
    
    private boolean dirty;
   
    private SecurityPrincipleRemoteHome secHome;
    private LinkRemoteHome linkHome;
    private FileRemoteHome fileHome;
    private SpeciesRemoteHome speciesHome;
    private SamplingUnitRemoteHome samplingUnitHome;
    private ResourceRemoteHome resourceHome;
    private ResourceCategoryRemoteHome resourceCategoryHome;
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise beans, Web services)
    // TODO Add business methods
    // TODO Add create methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
        secHome = (SecurityPrincipleRemoteHome)locator.getHome(ServiceLocator.Services.SECURITYPRINCIPLE);
        fileHome = (FileRemoteHome)locator.getHome(ServiceLocator.Services.FILE);
        linkHome = (LinkRemoteHome)locator.getHome(ServiceLocator.Services.LINK);
        speciesHome = (SpeciesRemoteHome)locator.getHome(ServiceLocator.Services.SPECIES);
        samplingUnitHome = (SamplingUnitRemoteHome)locator.getHome(ServiceLocator.Services.SAMPLINGUNIT);
        resourceCategoryHome = (ResourceCategoryRemoteHome)locator.getHome(ServiceLocator.Services.RESOURCECATEGORY);
        resourceHome = (ResourceRemoteHome)locator.getHome(ServiceLocator.Services.RESOURCE);
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbActivate()
     */
    public void ejbActivate() {
        
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbPassivate()
     */
    public void ejbPassivate() {
        
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbRemove()
     */
    public void ejbRemove() throws RemoveException
    {
        makeConnection();
        Statement stmt = null;
        String sql = "";
        try 
        {
            
            sql = "delete from Projects where pid = "+pid;
            
            System.err.println("SQL="+sql);
            stmt = conn.createStatement();
            stmt.execute(sql);
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            throw new RemoveException("ProjectBean#ejbRemove: Failed to remove: \n"+sqle.getMessage());
        } 
        finally 
        {
            try 
            {
                if (stmt != null) stmt.close();
            } 
            catch (SQLException ignored) 
            {}
            releaseConnection();
        }
    }
    
    /**
     * @see javax.ejb.EntityBean#unsetEntityContext()
     */
    public void unsetEntityContext() {
        context = null;
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbLoad()
     */
    public void ejbLoad() {
        // TODO add code to retrieve data
        
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Integer pk = (Integer)context.getPrimaryKey();
        try
        {
            ps = conn.prepareStatement("select name,comm,status from projects where pid = ?");
            ps.setInt(1,pk.intValue());
                     
            result = ps.executeQuery();
            
            if (result.next())
            {
                pid = pk.intValue();
                name = result.getString("name");
                comm = result.getString("comm");
                status  = result.getString("status");
                
                dirty = false;
            }
            else
                throw new EJBException();
        }
        catch (SQLException se)
        {
            throw new EJBException("ProjectBean#ejbLoad: Failed to load project, pid="+pid+"\n"+se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() 
    {    
        if (dirty)
        {
            makeConnection();
            Statement stmt = null;
            PreparedStatement ps = null;
            String sql = "";
            try 
            {


                ps = conn.prepareStatement("update projects set name = ?, comm = ?, status = ? where pid = ?");

                /*
                sql = "update projects set name = "+sqlString(new_name)+", comm = "+
                        sqlString(new_comm)+", status = "+sqlString(new_status)+ " "+
                        "where pid = "+pid;
                 */

                ps.setString(1, name);
                ps.setString(2, comm);
                ps.setString(3, status);
                ps.setInt(4, pid);

                ps.execute();


                /*
                stmt = conn.createStatement();
                stmt.execute(sql);
                 */
            } 
            catch (SQLException sqle) 
            {
                sqle.printStackTrace(System.err);
                throw new EJBException("ProjectBean#ejbStore: Failed to update project. \n" +
                        sqle.getMessage());
            } 
            finally 
            {
                try 
                {
                    if (stmt != null) stmt.close();
                } 
                catch (SQLException ignored) {}
                releaseConnection();
                dirty = false;
            }
        }
    }
    
    // </editor-fold>
    
    
    /**
     * Finds a project using the primary key
     * @param pid The project id
     * @param caller The current caller object
     * @throws javax.ejb.FinderException If the project could not be found
     * @return The project
     */
    public java.lang.Integer ejbFindByPrimaryKey(java.lang.Integer pid, TgDbCaller caller) throws javax.ejb.FinderException {
        setCaller(caller);
        return ejbFindByPrimaryKey(pid);
    }
    
    /**
     * Finds a project using the primary key
     * @param pid The project id
     * @param caller The current caller object
     * @throws javax.ejb.FinderException If the project could not be found
     * @return The project
     */
    public java.lang.Integer ejbFindByPrimaryKey(java.lang.Integer pid) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try
        {
            ps = conn.prepareStatement("select pid from projects where pid = ?");
            ps.setInt(1,pid.intValue());
            result = ps.executeQuery();
            
            if (!result.next())
            {
                throw new ObjectNotFoundException("ProjectBean#ejbFindByPrimaryKey: Cannot find project. \n");
            }
        }
        catch (SQLException se)
        {
            throw new FinderException("ProjectBean#ejbFindByPrimaryKey: Cannot find project. \n"+se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return pid;
    }

    /**
     * Get the Project id
     * @return the project id
     */
    public int getPid() {
        return pid;
    }

    /**
     * Get the name of the project
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the project
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
        dirty = true;
    }

    /**
     * get comment
     * @return the comment
     */
    public String getComm() {
        return comm;
    }

    /**
     * Set the comment to the project
     * @param comm comment
     */
    public void setComm(String comm) {
        this.comm = comm;
        dirty = true;
    }

    /**
     * Get the status of the project. E - Enabled, D - Disabled
     * @return the project status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the status
     * E | D
     * @param status The status of the project
     * @throws org.tgdb.exceptions.IllegalValueException If the status value was non supported
     * @throws org.tgdb.exceptions.ApplicationException If the status could not be set
     */
    public void setStatus(String status) throws IllegalValueException, ApplicationException
    {
        if (status!=null && !status.equals("E") && !status.equals("D"))
                throw new IllegalValueException("status must be either 'E' or 'D'");
        
        if (!caller.hasPrivilege("PROJECT_ADM") && !caller.isAdmin())
            throw new ApplicationException("Permission denied. Unable to set status.");
        
        this.status = status;
        dirty = true;
    }

    /**
     * 
     * @param pid 
     * @param name 
     * @param comm 
     * @param status 
     * @param usr 
     * @throws javax.ejb.CreateException 
     * @return 
     */
    public Integer ejbCreate(int pid, java.lang.String name, java.lang.String comm, java.lang.String status, TgDbCaller usr) throws javax.ejb.CreateException {
        this.pid = pid;
        this.name = name;
        this.comm = comm;
        this.status = status;
        
        caller = usr;
        
        if (!caller.hasPrivilege("PROJECT_ADM") && !caller.isAdmin())
            throw new CreateException("User "+caller.getName()+" dont have privilege PROJECT_ADM. Create failed. ");
        
        makeConnection();
                
        PreparedStatement ps = null;
        String sql = "";        
        //int pid = 0;
        try 
        {
            //pid = getNextID(conn,"Projects_seq");
            
            sql = "insert into projects (pid,name,comm,status) values (?,?,?,?)";
            
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1,pid);
            ps.setString(2, name);
            ps.setString(3, comm);
            ps.setString(4, status);
            ps.execute();
            dirty = false;
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace();
            throw new CreateException("ProjectBean#ejbCreate: Failed to create project\n"+sqle.getMessage());
        }
        finally 
        {
            try 
            {
                if (ps != null) ps.close();
            } 
            catch (SQLException ignored) 
            {}
            releaseConnection();
        }
        return new Integer(this.pid);
    }

    /**
     * 
     * @param pid 
     * @param name 
     * @param comm 
     * @param status 
     * @param usr 
     * @throws javax.ejb.CreateException 
     */
    public void ejbPostCreate(int pid, java.lang.String name, java.lang.String comm, java.lang.String status, TgDbCaller usr) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }

    /**
     * Same as setStatus("E")
     */
    public void enable() {
        try
        {
            this.setStatus("E");
        }
        catch (ApplicationException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * same as setStatus("D")
     */
    public void disable() {
        try
        {
            this.setStatus("D");
        }
        catch (ApplicationException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Finds all projects in the database
     * @param caller The current caller object
     * @throws javax.ejb.FinderException If the projects could not be retrieved
     * @return A collection of projects
     */
    public java.util.Collection ejbFindByAll(TgDbCaller caller) throws javax.ejb.FinderException {
        
        Vector keys = new Vector();
        
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try
        {
            String sort = "name";
            if (caller.getAttribute("sort")!=null)
                sort = caller.getAttribute("sort");
            
            ps = conn.prepareStatement("select pid from projects order by "+sort);
            
            result = ps.executeQuery();
            
            while (result.next())
            {
                keys.addElement(result.getObject("pid"));
            }
        }
        catch (SQLException se)
        {
            throw new FinderException("ProjectBean#ejbFindByAll: cannot find objects. \n"+se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return keys;
    }

    /**
     * Method for retrieval of all names.
     * @return String[] with all names, null if no names can be found.
     * @throws java.rmi.RemoteException If errors occur this thows remote exception
     */
    
    public String[] getAllNames() throws java.rmi.RemoteException {
        String[] names = null;
        
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try
        {
            ps = conn.prepareStatement("select count(*) as numNames from users");
            result = ps.executeQuery();
            
            // Get number of users for creation of array with sufficient size
            if (result.next())
            {
                names = new String[result.getInt("numNames")];
            }   
            else
            {
                // No names in the database...
                return null;
            }
            
            ps = conn.prepareStatement("select name from users");
            result = ps.executeQuery();
            
            int i = 0;
            while (result.next())
            {
                names[i++] = (String)result.getObject("name");
            }
        }
        catch (SQLException se)
        {
            throw new EJBException("ProjectBean#getAllNames: failed to get all names. \n"+se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return names;      
    }

    /**
     * Get all SecurityPrinciples for this project
     * @return returns a Collection of SecurityPrinciples
     */
    public Collection getSecurityPrinciples() 
    {
        Collection arr = new ArrayList();
        try
        {
            arr = secHome.findByProject(pid);
        }
        catch (Exception e)
        {
            throw new EJBException(e);
        }
        return arr;
    }

    /**
     * Returns the caller object
     * @return The caller object
     */
    public String getCaller() {
        return context.getCallerPrincipal().getName();
    }

    /**
     * Sets the current caller object
     * @param usr The current caller
     */
    public void setCaller(TgDbCaller usr) {
        caller = usr;
        dirty = true;
    }
    
    /**
     * Returns the roles for the project
     * @return The roles
     */
    public Collection getRoles()
    {
        Collection arr = new ArrayList();
        try
        {
            RoleRemoteHome rHome = (RoleRemoteHome)locator.getHome(ServiceLocator.Services.ROLE);
            arr = rHome.findByProject(pid);
        }
        catch (Exception e)
        {
            throw new EJBException(e);
        }
        return arr;
    }

    /**
     * Returns all links for a project
     * @return The links for a project
     */
    public Collection getLinks() {
        Collection arr = null;
        try {
            arr = linkHome.findByProject(pid);
        } catch (FinderException fe) {
            throw new EJBException(fe);
        } catch (RemoteException re) {
            throw new EJBException(re);
        }
        return arr;
    }

    /**
     * Returns all files for a project
     * @return The files for a project
     */
    public Collection getFiles() {      
        Collection arr = null;
        try {
            arr = fileHome.findByProject(pid);
        } catch (FinderException fe) {
            throw new EJBException(fe);
        } catch (RemoteException re) {
            throw new EJBException(re);
        }
        return arr;
    }
    
    /**
     * Get all species connected to this project
     * @throws org.tgdb.exceptions.ApplicationException if errors occur
     * @return a collection of species beans
     */
    public Collection getSpecies() throws ApplicationException
    {
        try
        {
            Collection spc = speciesHome.findByProject(pid); // findAllSpecies(pid);
            return spc;
        }
        catch (Exception e)
        {
            throw new ApplicationException("Unable to get species", e);
        }
    }
    
    public Collection getSamplingUnits() throws ApplicationException
    {
        try
        {
            Collection sus = samplingUnitHome.findByProject(pid);
            return sus;
        }
        catch (Exception e)
        {
            throw new ApplicationException("Unable to get species", e);
        }
    }
    
    public void addSamplingUnit(SamplingUnitRemote su) throws ApplicationException
    {
        makeConnection();
        PreparedStatement ps = null;
        try
        {
            ps = conn.prepareStatement("insert into r_prj_su (pid, suid) values (?,?)");
            ps.setInt(1, pid);
            ps.setInt(2, su.getSuid());
            ps.execute();
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to add sampling unit to project",e);
        }
        finally
        {
            releaseConnection();
        }
    }
    
    public void addSpecies(SpeciesRemote spc) throws ApplicationException
    {
        makeConnection();
        PreparedStatement ps = null;
        try
        {
            ps = conn.prepareStatement("insert into r_prj_spc (pid, sid) values (?,?)");
            ps.setInt(1, pid);
            ps.setInt(2, spc.getSid());
            ps.execute();
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to add species to project",e);
        }
        finally
        {
            releaseConnection();
        }
    }

    public java.util.Collection ejbFindBySamplingUnit(int suid) throws javax.ejb.FinderException {
              
        Collection keys = new ArrayList();
        
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try
        {
            ps = conn.prepareStatement("select pid from r_prj_su where suid=?");
            ps.setInt(1, suid);
            result = ps.executeQuery();
            
            while (result.next())
            {
                keys.add(new Integer(result.getInt("pid")));
            }
        }
        catch (SQLException se)
        {
            throw new FinderException("ProjectBean#ejbFindByAll: cannot find objects. \n"+se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return keys;
    }
    
    public Collection getResources() {
        try {
            return resourceHome.findByProject(pid);
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Collection getResourceCategories() {
        try {
            return resourceCategoryHome.findByProject(pid);
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }    

    public java.util.Collection ejbFindByName(java.lang.String name, org.tgdb.TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection projects = new ArrayList();
        try {
            ps = conn.prepareStatement("select pid from projects where lower(name) like lower(?)");
            ps.setString(1, name);
            result = ps.executeQuery();
            
            while (result.next()) {
                projects.add(new Integer(result.getInt("pid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ProjectBean#ejbFindByName: Cannot find project. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }        
        return projects;
    }

    public void removeSpecies(SpeciesRemote species) {
        makeConnection();
        PreparedStatement ps = null;
        try
        {
            ps = conn.prepareStatement("delete from r_prj_spc where pid = ? and sid = ?");
            ps.setInt(1, pid);
            ps.setInt(2, species.getSid());
            ps.execute();
        }
        catch (Exception e)
        {
            throw new EJBException("Failed to remove species from project",e);
        }
        finally
        {
            releaseConnection();
        }
    }

    public void removeSamplingUnit(SamplingUnitRemote su) {
        makeConnection();
        PreparedStatement ps = null;
        try
        {
            ps = conn.prepareStatement("delete from r_prj_su where pid = ? and suid = ?");
            ps.setInt(1, pid);
            ps.setInt(2, su.getSuid());
            ps.execute();
        }
        catch (Exception e)
        {
            throw new EJBException("Failed to remove sampling unit from project",e);
        }
        finally
        {
            releaseConnection();
        }
    }
}
