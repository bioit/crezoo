package org.tgdb.samplingunit.samplingunit;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.IllegalValueException;
import org.tgdb.exceptions.PermissionDeniedException;
import org.tgdb.TgDbCaller;
import org.tgdb.model.expmodel.ExpModelRemoteHome;
import org.tgdb.resource.file.FileRemote;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.resource.link.LinkRemote;
import org.tgdb.project.user.UserRemote;
import org.tgdb.resource.file.FileRemoteHome;
import org.tgdb.resource.link.LinkRemoteHome;
import org.tgdb.resource.resource.ResourceRemote;
import org.tgdb.resource.resource.ResourceRemoteHome;
import org.tgdb.samplingunit.samplingunitmanager.SamplingUnitDTO;
import org.tgdb.servicelocator.ServiceLocator;
import org.tgdb.species.species.SpeciesRemote;
import org.tgdb.species.species.SpeciesRemoteHome;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.RemoveException;

/**
 * This is the bean class for the SamplingUnitBean enterprise bean.
 * Created May 23, 2005 2:58:16 PM
 * @author heto
 *
 * @todo the sql query in ejbCreate have a id value of 1001 static. CHANGE THIS!!!
 */
public class SamplingUnitBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteBusiness {
    private javax.ejb.EntityContext context;
    
    private int suid;
    /**
     * The sampling unit name
     */
    private String name;
    /**
     * The comment
     */
    private String comm;
    /**
     * Status for the sampling unit. E - Enabled, D - Disabled
     */
    private String status;
    /**
     * The timestamp for latest changes
     */
    private java.sql.Date ts;
    /**
     * The species id
     */
    private int sid;
    /**
     * The id of the user
     */
    private int id;
    
    private boolean dirty;
    
//    private UserRemoteHome userHome;
    private SpeciesRemoteHome speciesHome;
    private LinkRemoteHome linkHome;
    private FileRemoteHome fileHome;
    private ExpModelRemoteHome modelHome;
    private ProjectRemoteHome projectHome;
    private ResourceRemoteHome resourceHome;
    
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise beans, Web services)
    // TODO Add business methods
    // TODO Add create methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
//        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
        speciesHome = (SpeciesRemoteHome)locator.getHome(ServiceLocator.Services.SPECIES);
        fileHome = (FileRemoteHome)locator.getHome(ServiceLocator.Services.FILE);
        linkHome = (LinkRemoteHome)locator.getHome(ServiceLocator.Services.LINK);
        modelHome = (ExpModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPMODEL);
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);
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
    public void ejbRemove() throws RemoveException {
        makeConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("delete from sampling_units where suid = ?");
            ps.setInt(1, suid);
            int rows = ps.executeUpdate();
            
        } catch (Exception e) {
            throw new RemoveException("SamplingUnitBean#ejbRemove: Error deleting sampling unit. \n"+e.getMessage());
        } finally {
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
        makeConnection();
        
        Integer pk = (Integer)context.getPrimaryKey();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select suid,name,comm,status,sid,id,ts " +
                    "from sampling_units where suid=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                suid = rs.getInt("suid");
                name = rs.getString("name");
                comm = rs.getString("comm");
                status = rs.getString("status");
                sid = rs.getInt("sid");
                id = rs.getInt("id");
                ts = rs.getDate("ts");
                
                dirty = false;
            } else
                throw new EJBException("SamplingUnitBean#ejbLoad: Error loading sampling unit");
        } catch (Exception e) {
            throw new EJBException("SamplingUnitBean#ejbLoad: Error loading sampling unit. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() {  
        if (dirty)
        {
            makeConnection();
            Integer pk = (Integer)context.getPrimaryKey();
            PreparedStatement ps = null;
            try {            
                ps = conn.prepareStatement("update sampling_units set name=?,comm=?,status=?,sid=?,id=?,ts=?" +
                        "where suid=?");

                ps.setString(1, name);
                ps.setString(2, comm);
                ps.setString(3, status);
                ps.setInt(4, sid);
                ps.setInt(5, id);
                ps.setDate(6, ts);
                ps.setInt(7, pk.intValue());



                int res = ps.executeUpdate();

                if (res != 1)
                    throw new EJBException("SamplingUnitBean#ejbStore: Error storing sampling unit");
            } catch (Exception e) {
                throw new EJBException("SamplingUnitBean#ejbStore: Error storing sampling unit. \n"+e.getMessage());
            } finally {
                releaseConnection();
                dirty = false;
            }
        }
    }
    
    // </editor-fold>
    
    /**
     * See EJB 2.0 and EJB 2.1 section 12.2.5
     */
    public java.lang.Integer ejbFindByPrimaryKey(java.lang.Integer aKey) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;

        try {
            ps = conn.prepareStatement("select suid from sampling_units where suid = ?");
            ps.setInt(1,aKey.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("SamplingUnitBean#ejbFindByPrimaryKey: Cannot find sampling unit suid="+aKey);
            }
        } catch (SQLException se) {
            throw new FinderException("SamplingUnitBean#ejbFindByPrimaryKey: could not find sampling unit. suid="+aKey+"\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return aKey;
    }
    
    /**
     * Create a new sampling unit
     * @return the sampling unit id (PK)
     * @privilege SU_W is required to create a new sampling_unit.
     * @param caller 
     * @param suid The sampling unit id
     * @param name sampling unit name
     * @param comm comment
     * @param species A species bean
     * @throws javax.ejb.CreateException Throws this on error creating the sampling unit
     */
    public java.lang.Integer ejbCreate(Integer suid, java.lang.String name, java.lang.String comm, SpeciesRemote species, TgDbCaller caller) throws javax.ejb.CreateException {
        makeConnection();
        
        this.suid = suid.intValue();
        this.caller = caller;
        
        if (!caller.hasPrivilege("SU_W") && !caller.isAdmin())
            throw new CreateException("Permission denied to create new samplingunit.");
        
        try {
            this.status = "E";
            this.name = name;
            this.comm = comm;            
            this.sid = species.getSid();
            id = caller.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        this.ts = new java.sql.Date(System.currentTimeMillis());
        
        
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("insert into sampling_units (suid,name,comm,status,sid,id,ts) values (?,?,?,?,?,?,?)");
            ps.setInt(1, suid.intValue());
            ps.setString(2, this.name);
            ps.setString(3, this.comm);
            ps.setString(4, getStatus());
            ps.setInt(5, species.getSid());
            ps.setInt(6, id);
            ps.setDate(7, ts);
            
            ps.execute();
            dirty = false;
            /*
            ps = conn.prepareStatement("insert into r_prj_su (pid, suid) values (?,?)");
            ps.setInt(1, caller.getPid());
            ps.setInt(2, suid.intValue());
            
            ps.execute();
             */
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("SamplingUnitBean#ejbCreate: Unable to create Sampling Unit: "+e.getMessage());
        } finally {
            releaseConnection();
        }
        return new Integer(this.getSuid());
    }
    
    /**
     * PostCreate method
     * @param caller 
     * @param suid The sampling unit id
     * @param name sampling unit name
     * @param comm comment
     * @param species A species bean
     * @throws javax.ejb.CreateException Throws this on error creating the sampling unit
     */
    public void ejbPostCreate(Integer suid, java.lang.String name, java.lang.String comm, SpeciesRemote species, TgDbCaller caller) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }
    
    /**
     * Get the samping unit id
     * @return the sampling unit id
     */
    public int getSuid() {
        return suid;
    }
    
    /**
     * Get the name of the sampling unit
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the name
     * @param name name of sampling unit
     */
    public void setName(String name) {     
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());          
        this.name = name;
        dirty = true;
    }
    
    /**
     * Get the comment
     * @return the comment
     */
    public String getComm() {
        return comm;
    }
    
    /**
     * Set the comment
     * @param comm the comment     
     */
    public void setComm(String comm)  {
        this.comm = comm;
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());                  
        dirty = true;
    }
    
    /**
     * Get the status of the sampling unit. E - Enabled, D - Disabled.
     * @return a string with [ E | D ]
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Set the status.
     * @param status the status string [ E | D ]
     * @throws org.tgdb.exceptions.IllegalValueException If an unsupported value was passed along as argument
     */
    public void setStatus(String status) throws IllegalValueException {      
        if (status!=null && !status.equals("E") && !status.equals("D"))
            throw new IllegalValueException("status must be either 'E' or 'D'");
        
        this.status = status;
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());                  
        dirty = true;
    }
    
    /**
     * Get the timestamp of this object
     * @return The timestamp.
     */
    public java.sql.Date getTs() {        
        return ts;
    }
    
    
    /**
     * Get the species for this sampling unit
     * @return A collection of species that belongs to the sampling unit
     */
    public SpeciesRemote getSpecies() {
        
        SpeciesRemote species = null;
        try {
            species = speciesHome.findByPrimaryKey(new Integer(sid));
        } 
        catch (Exception e) 
        {
            throw new EJBException(e.getMessage());
        }
        return species;
    }
    
    /**
     * Finds a sampling unit that belongs to a project
     * @param pid The project id
     * @throws javax.ejb.FinderException If the sampling unit could not be found
     * @return The sampling unit
     */
    public java.util.Collection ejbFindByProject(int pid) throws javax.ejb.FinderException {      
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select suid from r_prj_su where pid = ?");
            ps.setInt(1,pid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("suid")));
            }
        } catch (SQLException se) {
            throw new FinderException("SamplingUnitBean#ejbFindByProject: unable to find sampling unit by project. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    
    
    
    /**
     * Writes a log entry to track changes history
     * @throws org.tgdb.exceptions.PermissionDeniedException If the caller does not have SU_W privilege
     */
    public void addHistory() throws PermissionDeniedException {
        if (!caller.hasPrivilege("SU_W"))
            throw new PermissionDeniedException("Permission denied. Unable to write log."); 
        
        makeConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("insert into sampling_units_log (suid, name, comm, status, id, ts) values (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, suid);
            ps.setString(2, name);
            ps.setString(3, comm);
            ps.setString(4, status);
            ps.setInt(5, caller.getId());
            ps.setDate(6, ts);
                                                
            ps.execute();

        } catch (Exception e) {
            throw new EJBException("SamplingUnitBean#getHistory: Error saving history for sampling unit. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }

    /**
     * Sets the caller object
     * @param caller The caller
     */
    public void setCaller(org.tgdb.TgDbCaller caller) {
        this.caller = caller;
        dirty = true;
    }
    
    /**
     * Returns the id of the individual that made the last changes on the sampling unit
     * @return The id of the user that made the last changes on the sampling unit
     */
    public int getId()
    {
        return id;
    }
    
    /**
     * Returns the user that made the last changes on the sampling unit
     * @return The user that made the last changes on the sampling unit
     */
    public UserRemote getUser()
    {
        UserRemote usr = null;
        try 
        {
            usr = userHome.findByPrimaryKey(new Integer(id));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
        }
        return usr;
    }

    /**
     * Returns a collection of history log entries for the sampling unit
     * @return A collection of history log entries
     */
    public Collection getHistory() {               
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        
        ResultSet result = null;
        
        try {
            ps = conn.prepareStatement("select * from sampling_units_log where suid = ? order by ts desc");
            ps.setInt(1, suid);
            result = ps.executeQuery();
            SamplingUnitDTO dto = null;
            
            while (result.next()) {
                dto = new SamplingUnitDTO(result.getInt("suid"), result.getString("name"));
                dto.setComm(result.getString("comm"));
                dto.setStatus(result.getString("status"));
                dto.setTs(result.getDate("ts"));
                dto.setUsr(result.getString("id"));
                
                arr.add(dto);
            }
        } catch (Exception se) {
            throw new EJBException("SamplingUnitBean#getHistory: unable to find sampling units history. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    /**
     * Returns the number of projects that this sampling unit is in
     * @throws org.tgdb.exceptions.ApplicationException If the number of projects could not be retrieved
     * @return The number of projects the sampling unit is in
     */
    public int getNumberOfProjects() throws ApplicationException {
        makeConnection();
        
        int num = 0;
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select count(*) as num from r_prj_su r, sampling_units s where r.suid=s.suid and s.suid=?");
            ps.setInt(1,suid);
            
            result = ps.executeQuery();
            
            if (result.next()) {
                num = result.getInt("num");
            }
        } catch (SQLException se) {
            throw new ApplicationException("SamplingUnitBean#getNumberOfProjects: Cannot count the individuals in sampling unit "+suid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return num;
    }
    
    /**
     * Returns the number of groups in the grouping
     * @return The number of groups in the grouping
     * @throws org.tgdb.exceptions.ApplicationException If the information could not be retrieved
     */
    public int getNumberOfGroupings() throws ApplicationException {
        makeConnection();
        
        int num = 0;
        return num;
    }

    
    /**
     * Finds all sampling units for a species and project
     * @param pid The project id
     * @param sid The species id
     * @throws javax.ejb.FinderException If the sampling units could not be retrieved
     * @return The sampling units for the project and species
     */
    public java.util.Collection ejbFindByProjectSpecies(int pid, int sid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select su.suid from r_prj_su su, sampling_units sp where su.pid = ? AND sp.sid = ? AND sp.suid = su.suid");
            ps.setInt(1,pid);
            ps.setInt(2,sid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("suid")));
            }
        } catch (SQLException se) {
            throw new FinderException("SamplingUnitBean#ejbFindByProjectSpecies: unable to find sampling unit by project and species. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    /**
     * Returns the files that belong to the sampling unit
     * @throws org.tgdb.exceptions.ApplicationException If the files could not be retrieved
     * @return The files for the sampling unit
     */
    public Collection getFiles() throws ApplicationException {
        try {
            return fileHome.findBySamplingUnit(suid);
        } catch (Exception e) {
            throw new ApplicationException("Could not get files");
        }
    }

    /**
     * Returns the links for the sampling unit
     * @throws org.tgdb.exceptions.ApplicationException If the links could not be retrieved
     * @return The links for the sampling unit
     */
    public Collection getLinks() throws ApplicationException {
        try {
            return linkHome.findBySamplingUnit(suid);
        } catch (Exception e) {
            throw new ApplicationException("Could not get links");
        }
    }
    
    
    
    public Collection getResources() throws ApplicationException {
        try {
            return resourceHome.findBySamplingUnit(suid, caller);
        } catch (Exception e) {
            throw new ApplicationException("Could not get files");
        }
    }
    
    /**
     * Adds a resource to the sampling unit
     * @param file The file to add
     * @throws java.rmi.RemoteException If the file could not be added
     */
    public void addResource(ResourceRemote resource) throws RemoteException {
        makeConnection();
        
        try {
            PreparedStatement ps = conn.prepareStatement("insert into r_resource_su (resourceid, suid) values (?,?)");
            ps.setInt(1, resource.getResourceId());
            ps.setInt(2, suid);
            
            ps.execute();
        } catch (SQLException e) {
            throw new EJBException("samplingUnitBean#addResource: Unable to add resource "+resource.getName()+" to samplingunit "+name);
        } finally {
            releaseConnection();
        }
    }
    
    /**
     * Removes a resource from the sampling unit
     * @param file The file to remove
     * @throws java.rmi.RemoteException If the file could not be removed
     */
    public void removeResource(ResourceRemote resource) throws RemoteException {
        makeConnection();
        
        try {
            PreparedStatement ps = conn.prepareStatement("delete from r_resource_su where resourceid = ? and suid = ?");
            ps.setInt(1, resource.getResourceId());
            ps.setInt(2, suid);
            
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("samplingUnitBean#removeResource: Unable to remove "+resource.getName()+" from samplingunit "+name);
        } finally {
            releaseConnection();
        }
    }

    /**
     * Adds a file to the sampling unit
     * @param file The file to add
     * @throws java.rmi.RemoteException If the file could not be added
     */
    public void addFile(FileRemote file) throws RemoteException {
        makeConnection();
        
        try {
            PreparedStatement ps = conn.prepareStatement("insert into r_file_su (fileid, suid) values (?,?)");
            ps.setInt(1, file.getFileId());
            ps.setInt(2, suid);
            
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("samplingUnitBean#addFile: Unable to add file "+file.getName()+" to samplingunit "+name);
        } finally {
            releaseConnection();
        }
    }

    /**
     * Removes a file from the sampling unit
     * @param file The file to remove
     * @throws java.rmi.RemoteException If the file could not be removed
     */
    public void removeFile(FileRemote file) throws RemoteException {
        makeConnection();
        
        try {
            PreparedStatement ps = conn.prepareStatement("delete from r_file_su where fileid = ? and suid = ?");
            ps.setInt(1, file.getFileId());
            ps.setInt(2, suid);
            
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("samplingUnitBean#removeFile: Unable to remove "+file.getName()+" from samplingunit "+name);
        } finally {
            releaseConnection();
        }
    }

    /**
     * Adds a link to the sampling unit
     * @param link The link to add
     * @throws java.rmi.RemoteException If the link could not be added
     */
    public void addLink(LinkRemote link) throws RemoteException {
        makeConnection();
        
        try {
            PreparedStatement ps = conn.prepareStatement("insert into r_link_su (linkid, suid) values (?,?)");
            ps.setInt(1, link.getLinkId());
            ps.setInt(2, suid);
            
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("samplingUnitBean#addLink: Unable to add link "+link.getName()+" to samplingunit "+name);
        } finally {
            releaseConnection();
        }
    }

    /**
     * Removes a link from the sampling unit
     * @param link The link to remove
     * @throws java.rmi.RemoteException If the link could not be removed
     */
    public void removeLink(LinkRemote link) throws RemoteException {
        makeConnection();
        
        try {
            PreparedStatement ps = conn.prepareStatement("delete from r_link_su where linkid = ? and suid = ?");
            ps.setInt(1, link.getLinkId());
            ps.setInt(2, suid);
            
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("samplingUnitBean#removeLink: Unable to remove "+link.getName()+" from samplingunit "+name);
        } finally {
            releaseConnection();
        }
    }

    /**
     * Returns the experimental models for the sampling unit
     * @return The experimental models for the sampling unit
     */
    public Collection getExperimentalModels() {
        try {
            return modelHome.findBySamplingUnit(suid, caller);
        } catch (Exception se) {
            throw new EJBException("samplingUnitBean#getExperimentalModels: Cannot find models "+se.getMessage());
        }
    }
    
//    /**
//     * Returns the experimental models for the sampling unit
//     * @return The experimental models for the sampling unit
//     */
//    public Collection getExperimentalModels(int suid, TgDbCaller caller) {
//        try {
//            return modelHome.findBySamplingUnit(suid, caller);
//        } catch (Exception se) {
//            throw new EJBException("samplingUnitBean#getExperimentalModels: Cannot find models "+se.getMessage());
//        }
//    }

    /**
     * Returns the number of experimental models for the sampling unit
     * @return The number of experimental models for the sampling unit
     */
    public int getNumberOfExperimentalModels() {
        try {
            return modelHome.findBySamplingUnit(suid, caller).size();
        } catch (Exception se) {
            throw new EJBException("samplingUnitBean#getNumberOfExperimentalModels: Cannot get number of models "+se.getMessage());
        }
    }
    
    

    /**
     * Returns the experimental objects for the sampling unit
     * @return The experimental objects for the sampling unit
     */
    public Collection getExperimentalObjects() {
        try {
            Collection expObjects = getExperimentalModels();
            
            return expObjects;
        } catch (Exception se) {
            throw new EJBException("samplingUnitBean#getExperimentalObjects: Cannot find experimental objects "+se.getMessage());
        }
    }

    /**
     * Get the projects that this sampling unit is connected to. May be more than one!
     * @throws org.tgdb.exceptions.ApplicationException if some errors occur
     * @return a collection of ProjectRemote
     */
    public Collection getProjects() throws ApplicationException
    {
        try
        {
            return projectHome.findBySamplingUnit(suid);
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to get projects",e);
        }
    }

    public Integer ejbFindByName(String name, TgDbCaller caller) throws javax.ejb.FinderException
    {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        int suid = 0;
        try {
            ps = conn.prepareStatement("select suid from sampling_units where name = ?");
            ps.setString(1, name);
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("SamplingUnitBean#ejbFindByName: Cannot find sampling unit name="+name);
            }
            else
            {
                suid = result.getInt("suid");
            }
        } catch (SQLException se) {
            throw new FinderException("SamplingUnitBean#ejbFindByName: could not find sampling unit. name="+name+"\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return new Integer(suid);
    }
}
