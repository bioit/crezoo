package org.tgdb.samplingunit.samplingunitmanager;

import org.tgdb.frame.PageManager;
import org.tgdb.frame.io.FileDataObject;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.CallerIsNullException;
import org.tgdb.exceptions.ExceptionLogUtil;
import org.tgdb.exceptions.PermissionDeniedException;
import org.tgdb.exceptions.SamplingUnitNotFoundException;
import org.tgdb.model.expmodel.ExpModelRemote;
import org.tgdb.model.modelmanager.ModelManagerRemote;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.ParamDataObject;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.project.user.UserRemoteHome;
import org.tgdb.resource.link.LinkRemote;
import org.tgdb.resource.resource.ResourceRemote;
import org.tgdb.resource.resource.ResourceRemoteHome;
import org.tgdb.resource.resourcemanager.ResourceManagerRemote;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemote;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import org.tgdb.species.species.SpeciesRemote;
import org.tgdb.species.species.SpeciesRemoteHome;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator; 
import javax.ejb.FinderException;

public class SamplingUnitManagerBean extends AbstractTgDbBean implements javax.ejb.SessionBean, org.tgdb.samplingunit.samplingunitmanager.SamplingUnitManagerRemoteBusiness {
    
    private javax.ejb.SessionContext context;
    private SamplingUnitRemoteHome surh;
    private SpeciesRemoteHome sprh;
    private UserRemoteHome urh;
    private ProjectRemoteHome projectHome;
    private ResourceRemoteHome resourceHome;
    
    private ModelManagerRemote modelManager;
    private ResourceManagerRemote resourceManager;

    private ExpModelRemote o;
    
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise bean, Web services)
    // TODO Add business methods or web service operations
    /**
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(javax.ejb.SessionContext aContext) {
        context = aContext;
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
        surh = (SamplingUnitRemoteHome)locator.getHome(ServiceLocator.Services.SAMPLINGUNIT);
        sprh = (SpeciesRemoteHome)locator.getHome(ServiceLocator.Services.SPECIES);
        urh = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);
        resourceHome = (ResourceRemoteHome)locator.getHome(ServiceLocator.Services.RESOURCE);
        
        modelManager = (ModelManagerRemote)locator.getManager(ServiceLocator.Services.MODELMANAGER);
        resourceManager = (ResourceManagerRemote)locator.getManager(ServiceLocator.Services.RESOURCEMANAGER);
    }
    
    
    
    // Add business logic below. (Right-click in editor and choose
    // "EJB Methods > Add Business Method" or "Web Service > Add Operation")
    
    /**
     * Returns a collection of sampling units.
     * @param pid The project id
     * @param caller The current caller object for the work session
     * @throws org.tgdb.exceptions.ApplicationException If the sampling units could not be retrieved.
     * @return A collection of sampling units for the project
     */
    public Collection getSamplingUnits(int pid, TgDbCaller caller) throws ApplicationException {
        validate("SU_R", caller);
        Collection arr = new ArrayList();
        try {
            Collection samplingunits = surh.findByProject(pid);
            Iterator i = samplingunits.iterator();
            SamplingUnitRemote su;
            while (i.hasNext()) {
                su = (SamplingUnitRemote)i.next();
                arr.add(new SamplingUnitDTO(su.getSuid(), su.getName(), su.getSpecies().getSid()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Error getting sampling units", e);
        }
        return arr;
    }
    
    /**
     * Returns the number of sampling units for the specified species
     * @return The number of sampling units for the specified species
     * @param qdo The parameters to filter on
     * @param sid The species id
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If the operation failed
     */
    public int getNumberOfSamplingUnits(int sid, org.tgdb.TgDbCaller caller, ParamDataObject qdo) throws ApplicationException {
        validate("SU_R", caller);
        makeConnection();
        
        Statement st = null;    
        int num = 0;
        try {
            String sql = "select count(suid) as num from v_sampling_units_3 where pid = '"+caller.getPid()+"' ";

            String extendSQL = buildQueryConditions(qdo);
            
            logger.debug("---------------------------------------->SamplingUnitManagerBean#getNumberOfSamplingUnits: Query = " + sql + extendSQL);
            
            st = conn.createStatement();
            ResultSet result = st.executeQuery(sql+extendSQL);         
            
            if (result.next()) {
                num = result.getInt("num");               
            }           
        } catch (Exception se) {
            se.printStackTrace();
            throw new ApplicationException("Unable to count sampling units."+se.getMessage(), se);
        } finally {
            releaseConnection();
        }
        
        return num;
    }    
    
    public int getNumberOfGroupings(int suid) throws ApplicationException {
        try {           
            SamplingUnitRemote su = surh.findByPrimaryKey(new Integer(suid));

            return 0;//su.getNumberOfGroupings();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not count groupings.", e);
        }
    }    
    
    /**
     * Retrieves the samplingunits that exist for the current species and project
     * @return A collection of sampling unit dto's
     * @param qdo Filtering parameters
     * @param sid The species id
     * @param pageManager The pagemanager object handling data page browsing info
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If the sampling units could not be found
     */
    public Collection getSamplingUnits(TgDbCaller caller, PageManager pageManager, ParamDataObject qdo, int sid) throws ApplicationException {
        validate("SU_R", caller);
        makeConnection();
        
        Collection arr = new ArrayList();
        
        Statement st = null;    
        
        try {
            String sql = "select * from v_sampling_units_3 where pid = '"+caller.getPid()+"' and sid = "+sid;

            String extendSQL = buildQueryConditions(qdo);

            st = conn.createStatement();
            ResultSet result = st.executeQuery(sql+extendSQL); 
            
            SamplingUnitDTO dto = null;
            
            int start = pageManager.getStart();
            int stop = pageManager.getStop();
            int index = 0;         
            
            while (result.next()) {
                index++;
                
                if (index>=start && index<=stop) {
                    
                    SamplingUnitRemote su = surh.findByPrimaryKey(new Integer(result.getInt("suid")));
                    su.setCaller(caller);
                    dto = new SamplingUnitDTO(su);
                    
                    /*
                    dto = new SamplingUnitDTO(result.getInt("suid"), result.getString("name"));
                    dto.setComm(result.getString("comm"));
                    dto.setStatus(result.getString("status"));
                    dto.setTs(result.getDate("ts"));
                    dto.setUsr(result.getString("usr"));
                    dto.setInds(result.getInt("inds"));
                    */

                    arr.add(dto);
                } else {                    
                    result.next();
                }                
            }           
        } catch (Exception se) {
            se.printStackTrace();
            throw new ApplicationException("Unable to find sampling units."+se.getMessage(), se);
        } finally {
            releaseConnection();
        }
        return arr;
    } 
    
    /**
     * Retrieves the samplingunits history
     * @return A collection of sampling unit dto's
     * @param suid The sampling unit id
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If the sampling units could not be found
     */
    public Collection getSamplingUnitHistory(TgDbCaller caller, int suid) throws ApplicationException {
        validate("SU_R", caller);         
        try {
            SamplingUnitRemote sur = surh.findByPrimaryKey(new Integer(suid));
            sur.setCaller(caller);
            return sur.getHistory();            
        } catch (Exception se) {
            se.printStackTrace();
            throw new ApplicationException("Unable to find sampling units history.", se);
        }
    } 
    
    /**
     * Creates a new sampling unit
     * @return The id of the new sampling unit
     * @param pid the project the sampling unit should be connected to
     * @param name The name of the sampling unit
     * @param comm The comment for the sampling unit
     * @param status The status of the sampling unit
     * @param caller The current caller object
     * @param sid The species id for which this sampling unit belongs to
     * @throws org.tgdb.exceptions.ApplicationException If the sampling unit could not be created
     */
    public int createSamplingUnit(String name, String comm, String status, int sid, int pid, TgDbCaller caller) throws ApplicationException {
        int suid = 0;
        validate("SU_W", caller);        
        validate("Name", name, 20);
        validate("Comment", comm, 256);            
        
        try {
            SamplingUnitRemote tmp = null;
            try
            {
                tmp = surh.findByName(name, caller);
            } catch (FinderException ex)
            {
                //ex.printStackTrace();
                tmp = null;
            }
            if (tmp!=null)
                throw new ApplicationException("Sampling unit name is not unique. Please find another name.");
            
            
            
            
            makeConnection();
            suid = getIIdGenerator().getNextId(conn, "sampling_units_seq");
            
            SpeciesRemote sr = sprh.findByPrimaryKey(new Integer(sid));
            SamplingUnitRemote sur  = surh.create(new Integer(suid), name, comm, sr, caller);
            
            ProjectRemote p = projectHome.findByPrimaryKey(new Integer(pid));
            p.addSamplingUnit(sur);
            
            
            
        } 
        catch (ApplicationException e)
        {
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not create a new sampling unit.", e);
        } finally {
            releaseConnection();
        }
        
        return suid;
    }
    
    
    
    /**
     * Returns the default sampling unit. This depends on the pid in the caller 
     * object.
     * @param caller The caller
     * @throws org.tgdb.exceptions.ApplicationException If the default sampling unit could not be retrieved
     * @return The default sampling unit
     */
    public SamplingUnitDTO getDefaultSamplingUnit(TgDbCaller caller) throws ApplicationException
    {
        if (caller==null)
            throw new CallerIsNullException("Caller is not set");
        validate("SU_R", caller);        
        SamplingUnitDTO dto = null;                
        try {
            SamplingUnitRemote su;
            Collection arr = surh.findByProject(caller.getPid());
            Iterator i = arr.iterator();
            if (i.hasNext())
            {
                su = (SamplingUnitRemote)i.next();
                su.setCaller(caller);
                dto = new SamplingUnitDTO(su);
            }
            else
            {
                ProjectRemote prj = projectHome.findByPrimaryKey(new Integer(caller.getPid()));
                Collection species = prj.getSpecies();
                Iterator is = species.iterator();
                SpeciesRemote s = null;
                if (is.hasNext())
                    s = (SpeciesRemote)is.next();
                else
                    throw new ApplicationException("No species assigned to this project.");
                
                
                caller.setSid(s.getSid());
                    
                logger.debug("---------------------------------------->SamplingUnitManagerBean#getDefaultSamplingUnit: Default sampling unit not found. Auto creating default sampling unit");
                int suid = createSamplingUnit(prj.getName()+".default", "", "E", caller.getSid(), caller.getPid(), caller);
                su = surh.findByPrimaryKey(new Integer(suid));
                dto = new SamplingUnitDTO(su);
            }    
        }
        catch (RemoteException e)
        {
            logger.error("---------------------------------------->SamplingUnitManagerBean#getDefaultSamplingUnit: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (FinderException e)
        {
            logger.error("---------------------------------------->SamplingUnitManagerBean#getDefaultSamplingUnit: Failed to find sampling unit", e);
            throw new ApplicationException("Unable to get default sampling unit.", e);
        }
        return dto;
    }

    /**
     * Method for retrieval of a single sample unit (DTO)
     * @param caller The current caller object
     * @param suid The sampling unit id of the unit to retrieve
     * @throws org.tgdb.exceptions.ApplicationException If the sampling unit could not be retrieved
     * @return A Sampling Unit Data transfer object (DTO)
     */
    public SamplingUnitDTO getSamplingUnit(org.tgdb.TgDbCaller caller, int suid) throws ApplicationException {
        validate("SU_R", caller);        
        SamplingUnitDTO dto = null;                
        try {
            SamplingUnitRemote su  = surh.findByPrimaryKey(new Integer(suid));
            dto = new SamplingUnitDTO(su);
        } catch (Exception se) {
            se.printStackTrace();
            throw new ApplicationException("Unable to find sampling unit", se);
        }
        return dto;
    }

    /**
     * Updates the information about a sampling unit
     * @param suid The id of the sampling unit to update
     * @param name The (new?) name of the sampling unit
     * @param comm The (new?) comment for the sampling unit
     * @param status The (new?) status of the sampling unit
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If the sampling unit could not be updated
     */
    public void updateSamplingUnit(int suid, java.lang.String name, java.lang.String comm, java.lang.String status, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("SU_W", caller);        
        validate("Name", name, 20);
        validate("Comment", comm, 256);
        try{
            SamplingUnitRemote su  = surh.findByPrimaryKey(new Integer(suid));
            
            su.setCaller(caller);
            
            su.addHistory();

            su.setName(name);
            su.setComm(comm);
            su.setStatus(status);
        }
        catch (FinderException e)
        {
            logger.error("---------------------------------------->SamplingUnitManagerBean#updateSamplingUnit: Update failed, Sampling unit was not found.",e);
            throw new SamplingUnitNotFoundException("Update failed, Sampling unit was not found.", e);
        }
        catch (RemoteException e)
        {
            logger.error("---------------------------------------->SamplingUnitManagerBean#updateSamplingUnit: Update failed. Remote exception",e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
    }

    /**
     * Removes a sampling unit
     * @param suid The id of the sampling unit to remove
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If the sampling unit could not be retrieved
     */
    public void removeSamplingUnit(int suid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("SU_W", caller);     
        validate("IND_W", caller);
        validate("GRP_W", caller);
        validate("MODEL_W", caller);
        try{
            
            SamplingUnitRemote su  = surh.findByPrimaryKey(new Integer(suid));
            
            Iterator models = su.getExperimentalModels().iterator();
            while (models.hasNext())
            {
                ExpModelRemote m = (ExpModelRemote)models.next();
                modelManager.removeModel(m.getEid(), caller);
                //m.remove();
            }
            
            
            
            
            su.remove();
        }catch(Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove sampling unit with suid="+suid, e);
        }
    }

    /**
     * Method for retrieval of all species for a project
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If something went wrong during the retrieval
     * @return A collection of species remote interfaces
     */
    public Collection getSpeciesForProject(int pid, TgDbCaller caller) throws ApplicationException {
        
        if (pid!=caller.getPid() && !caller.isAdmin())
            throw new PermissionDeniedException("User is not allowed to get species.");
        
        validate("SU_R", caller);        
        
        Collection species = new ArrayList();
        try{
            SpeciesRemote sr;
            Collection keys = sprh.findByProject(pid); //findAllSpecies(pid);

            Iterator itr = keys.iterator();            
            
            while(itr.hasNext()){      

                 sr = (SpeciesRemote)itr.next();

                 species.add(sr);
            }
        }catch(Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not get species", e);
        }
        
        return species;
    }
    
    /**
     * Returns the sampling units for a species and project
     * @param pid The project id
     * @param sid The species id
     * @param caller The caller
     * @throws org.tgdb.exceptions.ApplicationException If the sampling units could not be retrieved
     * @return The sampling units for the species and project
     */
    public Collection getSamplingUnits(int pid, int sid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("SU_R", caller);
        Collection arr = new ArrayList();
        try {
            Collection samplingunits = surh.findByProjectSpecies(pid, sid);
            Iterator i = samplingunits.iterator();
            SamplingUnitRemote su;
            while (i.hasNext()) {
                su = (SamplingUnitRemote)i.next();
                arr.add(new SamplingUnitDTO(su.getSuid(), su.getName(), su.getSpecies().getSid()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Error getting sampling units", e);
        }
        return arr;
    }
    
    /**
     * Returns the experimental objects for a sampling unit. This returns all
     * individuals and all models.
     * @param suid The sampling unit id
     * @param caller The caller
     * @throws org.tgdb.exceptions.ApplicationException If the experimental objects could not be retrieved
     * @return The experimental objects for the sampling unit
     */
    public Collection getExperimentalObjects(int suid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        
        if (!caller.hasPrivilege("IND_R") && !caller.hasPrivilege("MODEL_R"))
            throw new PermissionDeniedException("Permission denied then getting objects. Privilege IND_R or MODEL_R are needed");
        
        Collection dto = new ArrayList();
        try {
            SamplingUnitRemote samplingUnit = surh.findByPrimaryKey(new Integer(suid));
            samplingUnit.setCaller(caller);
            Collection expObjects = samplingUnit.getExperimentalObjects();
            Iterator itr = expObjects.iterator();

            while (itr.hasNext()) {                
                // Add to array
                Object o = itr.next();

                if(o instanceof ExpModelRemote) 
                {
                    ExpModelRemote expObj = (ExpModelRemote)o;
                    dto.add(new ExperimentalObjectDTO(expObj.getEid(), expObj.getIdentity(), expObj.getUser().getUsr(), expObj.getUser().getId(), expObj.getAlias(), expObj.getComm(), expObj.getTs().toString()));
                }               
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not get experimental objects. suid="+suid, e);
        }
        return dto;
    }
    
    /**
     * Get the resource tree for all resources connected to the sampling unit
     * with the suid provided.
     * @param suid is the sampling unit id
     * @param caller is the caller of this method.
     * @throws org.tgdb.exceptions.ApplicationException 
     * @return a collection of ResourceBranchDTO
     */
    public Collection getResourceTreeCollection(int suid, TgDbCaller caller) throws ApplicationException
    {
        logger.debug("---------------------------------------->SamplingUnitManagerBean#getResourceTreeCollection");
        Collection resourceTree = new ArrayList();
        try
        {
            SamplingUnitRemote su = surh.findByPrimaryKey(new Integer(suid));
            Collection resources = su.getResources();
            
            return resourceManager.getResourceTreeCollection(resources, caller);
        }   
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to get resources", e);
        }
    }
    
    public void addLinkResource(String name, String comm, String url, int category, int suid, TgDbCaller caller) throws ApplicationException
    {
        validate("RESOURCE_W", caller);
        
        logger.debug("---------------------------------------->SamplingUnitManagerBean#addLinkResource: Started");
        
        try {
            int resourceId = 0;
            ResourceRemote res = null;
            SamplingUnitRemote su = surh.findByPrimaryKey(new Integer(suid));
           
            // Store the link
            LinkRemote link = resourceManager.createLink(name, comm, url, caller);
            
            // Register the link as a resource
            res = resourceManager.createResource(link, category, caller);  
            su.addResource(res);
        } catch (Exception e) {
            logger.error("---------------------------------------->SamplingUnitManagerBean#addLinkResource: Failed");
            throw new ApplicationException("SamplingUnitManagerBean#addLinkResource", e);            
        }
        
        logger.debug("---------------------------------------->SamplingUnitManagerBean#addLinkResource: Ended");
    }
    
    public void addResource(java.lang.String type, int category, int project, java.lang.String name, java.lang.String comm, FileDataObject fileData, org.tgdb.TgDbCaller caller, String url, int suid) throws ApplicationException {
        validate("RESOURCE_W", caller);
        
        logger.debug("---------------------------------------->SamplingUnitManagerBean#addResource: Started");
        
        try {
            int resourceId = 0;
            ResourceRemote res = null;
            SamplingUnitRemote su = surh.findByPrimaryKey(new Integer(suid));
            if(type.equalsIgnoreCase("file")) {
                // Store the file
                int fileId = resourceManager.saveFile(fileData.getFileName(), comm, caller, fileData).getFileId();
                // Register the file as a resource
                res = resourceManager.createResource(project, name, comm, fileId, 0, category, caller);
                su.addResource(res);
            } else if(type.equalsIgnoreCase("weblink")) {
                // Store the link
                int linkid = resourceManager.createLink(name, comm, url, caller).getLinkId();
                // Register the link as a resource
                res = resourceManager.createResource(project, name, comm, 0, linkid, category, caller);  
                su.addResource(res);
            } else {
                throw new ApplicationException("Unknown type");
            }
      
            
        } 
        catch (Exception e) 
        {
            logger.error("---------------------------------------->SamplingUnitManagerBean#addResource: Failed to add resource", e);
            throw new ApplicationException("Could not add resource to sampling unit", e);            
        }
        logger.debug("---------------------------------------->SamplingUnitManagerBean#addResource: Ended");
    }
}
