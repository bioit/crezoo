package org.tgdb.resource.resourcemanager;
import org.tgdb.frame.io.FileDataObject;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.ExceptionLogUtil;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.resource.file.FileRemote;
import org.tgdb.resource.file.FileRemoteHome;
import org.tgdb.resource.link.LinkRemote;
import org.tgdb.resource.link.LinkRemoteHome;
import org.tgdb.resource.resource.ResourceRemote;
import org.tgdb.resource.resource.ResourceRemoteHome;
import org.tgdb.resource.resourcecategory.ResourceCategoryRemote;
import org.tgdb.resource.resourcecategory.ResourceCategoryRemoteHome;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemote;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

/**
 * This is the bean class for the ResourceManagerBean enterprise bean.
 * Created Dec 5, 2005 2:53:48 PM
 * @author lami
 */
public class ResourceManagerBean extends AbstractTgDbBean implements javax.ejb.SessionBean, org.tgdb.resource.resourcemanager.ResourceManagerRemoteBusiness {
    private javax.ejb.SessionContext context;
    private LinkRemoteHome linkHome;
    private FileRemoteHome fileHome;
    private ProjectRemoteHome projectHome;
    private SamplingUnitRemoteHome samplingUnitHome;
    private ResourceCategoryRemoteHome resourceCategoryHome;    
    private ResourceRemoteHome resourceHome;    
    
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise bean, Web services)
    // TODO Add business methods or web service operations
    /**
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(javax.ejb.SessionContext aContext) {
        context = aContext;
        linkHome = (LinkRemoteHome)locator.getHome(ServiceLocator.Services.LINK);
        fileHome = (FileRemoteHome)locator.getHome(ServiceLocator.Services.FILE);
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);
        samplingUnitHome = (SamplingUnitRemoteHome)locator.getHome(ServiceLocator.Services.SAMPLINGUNIT);
        resourceCategoryHome = (ResourceCategoryRemoteHome)locator.getHome(ServiceLocator.Services.RESOURCECATEGORY);        
        resourceHome = (ResourceRemoteHome)locator.getHome(ServiceLocator.Services.RESOURCE);                
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
    
    // <editor-fold defaultstate="collapsed" desc="Link implementation">
    // All link methods. This is basic methods that create, modifies, or deletes.

    /**
     * Creates a new link
     * @param name The name of the link
     * @param comm The comment for the link
     * @param url The url of the link
     * @param caller The caller
     * @throws org.tgdb.exceptions.ApplicationException If the link could not be created
     * @return The id of the new link
     */
    public LinkRemote createLink(java.lang.String name, java.lang.String comm, java.lang.String url, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("LINK_W", caller);
        validate("URL", url, 1000);
        LinkRemote link = null;
        try{ 
            makeConnection();
            int linkid = getIIdGenerator().getNextId(conn, "link_seq");
            link = linkHome.create(linkid, name, url, comm, caller);                      
        }
        catch(Exception e){
            logger.error("---------------------------------------->ResourceManagerBean#createLink: Link creation failed", e);
            throw new ApplicationException("Could not create link", e);
        } finally {
            releaseConnection();
        }
        return link;
    }

    /**
     * Returns a link
     * @param linkid The link id of the link to fetch
     * @param caller The caller
     * @throws org.tgdb.exceptions.ApplicationException If the link could not be retrieved
     * @return A link
     */
    public LinkDTO getLink(int linkid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("LINK_W", caller);
        
        try {
            LinkRemote link = linkHome.findByPrimaryKey(new Integer(linkid));
        
            return new LinkDTO(link);
        } catch(Exception e){
            logger.error("---------------------------------------->ResourceManagerBean#getLink: Failed to get link "+linkid, e);
            throw new ApplicationException("Could not get link", e);
        }
    }        

    /**
     * Updates a link
     * @param linkid The id of the link to update
     * @param name The name of the link
     * @param url The URL of the link
     * @param comm The comment for the link
     * @param caller The caller
     * @throws org.tgdb.exceptions.ApplicationException If the link could not be updated
     */
    public void updateLink(int linkid, java.lang.String name, java.lang.String url, java.lang.String comm, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("LINK_W", caller);
        validate("URL", url, 1000);        
        try{
            LinkRemote link  = linkHome.findByPrimaryKey(new Integer(linkid));
            link.setCaller(caller);
            link.setName(name);
            link.setComment(comm);
            link.setUrl(url);
        }
        catch (RemoteException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#updateLink: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch(FinderException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#updateLink: Failed to find link", e);
            throw new ApplicationException("Could not update link. Link not found", e);
        }
    }
    
    /**
     * Removes a link
     * @param linkid The id of the link to remove
     * @param caller The caller
     * @throws org.tgdb.exceptions.ApplicationException If the link could not be removed
     */
    public void removeLink(int linkid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("LINK_W", caller);       
        try{
            LinkRemote link = linkHome.findByPrimaryKey(new Integer(linkid));
            link.setCaller(caller);
            link.remove();
        }
        catch (RemoteException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#removeLink: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (FinderException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#removeLink: Finder error", e);
            throw new ApplicationException("Could not remove link. Link not found", e);
        }
        catch (RemoveException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#removeLink: Failed to remove link "+linkid, e);
            throw new ApplicationException("Could not remove link", e);
        }
    }
    
    // </editor-fold>
    

    // <editor-fold defaultstate="collapsed" desc="File implementation">

    /**
     * Saves a file in the database
     * @param name The name of the file to save
     * @param comm The comment for the file
     * @param caller The caller
     * @param fileData The file data
     * @throws org.tgdb.exceptions.ApplicationException If the file could not be saved
     * @return The file id
     */
    public FileRemote saveFile(String name, String comm, org.tgdb.TgDbCaller caller, FileDataObject fileData) throws ApplicationException {
        validate("FILE_W", caller);
        int fileid = -1;
        FileRemote file = null;
        try {
            makeConnection();
            fileid = getIIdGenerator().getNextId(conn, "file_seq");  
            releaseConnection();

            file = fileHome.create(new Integer(fileid), name, comm, fileData.getMimeType(), null, caller);
            
            // Store the file
            file.setData(fileData.getData());
            
            return file;
        } 
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#saveFile: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }  
        catch (CreateException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#saveFile: Failed to create file", e);
            throw new ApplicationException("Unable to save file in database.");
        } 
    }
    
    /**
     * Saves a file in the database
     * @param name The name of the file
     * @param contentType The content type for the file
     * @param data The data in the file
     * @throws org.tgdb.exceptions.ApplicationException If the file could not be stored
     * @return The id of the saved file
     */
    public int saveFile(String name, String contentType, byte[] data, TgDbCaller caller) throws ApplicationException
    {
        validate("FILE_W", caller);
        int fileid = -1;
        try
        {
            makeConnection();
            fileid = getIIdGenerator().getNextId(conn, "file_seq");
            releaseConnection();
            
            // Create info in database.
            FileRemote file = fileHome.create(new Integer(fileid), name, null, null, null, caller);
            
            // Store the file
            file.setData(data);
            
            // Store mimetype
            file.setMimeType(contentType);
            
            return file.getFileId();
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#saveFile: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        } 
        catch (CreateException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#saveFile: Failed to save file. Create exception", e);
            throw new ApplicationException("Unable to save file in database.", e);
        }
    }
    
    /**
     * Retrieves a file
     * FILE_R is required
     * @param fileid The id of the file to retrieve
     * @param caller The caller
     * @throws org.tgdb.exceptions.ApplicationException If the file could not be retrieved
     * @return A file
     */
    public FileDTO getFile(int fileid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("FILE_R", caller);
        
        try {
            FileRemote file = fileHome.findByPrimaryKey(new Integer(fileid));
        
            return new FileDTO(file);
        } 
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#getFile: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        } 
        catch(FinderException e){
            logger.error("---------------------------------------->ResourceManagerBean#getFile: Failed to find file", e);
            throw new ApplicationException("Could not get file. File not found", e);
        }
    }
    
    public File getDiskFile(int fileid, TgDbCaller caller) throws ApplicationException
    {
        validate("FILE_R", caller);
        try
        {
            FileRemote file = fileHome.findByPrimaryKey(new Integer(fileid));
            
            File tmp = File.createTempFile("agdbtemp",".tmp");
            //FileWriter w = new FileWriter(tmp);
            
            BufferedOutputStream o = new BufferedOutputStream(new FileOutputStream(tmp));
            
            o.write(file.getData());
            o.flush();
            o.close();

            return tmp;    
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#getDiskFile: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        } 
        catch (FinderException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#getDiskFile: Failed to find file", e);
            throw new ApplicationException("Failed to get file to disk. File not found", e);
        }
        catch (FileNotFoundException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#getDiskFile: Temp file not found", e);
            throw new ApplicationException("Failed to store temp file");
        }
        catch (IOException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#getDiskFile: IOException", e);
            throw new ApplicationException("Failed to store temp file. Read / Write error", e);
        }
    }
    
    /**
     * Get the file Remote object for this file id.
     * FILE_R is required
     * 
     * @return valid FileRemote object.
     * @param fileid 
     * @param caller 
     * @throws org.tgdb.exceptions.ApplicationException if user is not permitted, or if file object could not be retrieved,
     *
     */
    public FileRemote getFileObject(int fileid, TgDbCaller caller) throws ApplicationException
    {
        validate("FILE_R", caller);
        try {
            return fileHome.findByPrimaryKey(new Integer(fileid));
        } 
        catch (RemoteException ex) {
            ex.printStackTrace();
            throw ExceptionLogUtil.createLoggableEJBException(ex);
        } 
        catch (FinderException ex) {
            logger.error("---------------------------------------->ResourceManagerBean#getFileObject: Could not find file", ex);
            throw new ApplicationException("Could not find file object.",ex);
        }
    }

    /**
     * Updates a file
     * @param fileid The id of the file to update
     * @param name The name of the file
     * @param comm The comment for the file
     * @param caller The caller
     * @throws org.tgdb.exceptions.ApplicationException If the file could not be updated
     */
    public void updateFile(int fileid, java.lang.String name, java.lang.String comm, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("FILE_W", caller);
        validate("Name", name, 55);   
        try{
            FileRemote file = fileHome.findByPrimaryKey(new Integer(fileid));
            file.setName(name);
            file.setComm(comm);
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#updateFile: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        } 
        catch (FinderException e) {
            logger.error("---------------------------------------->ResourceManagerBean#updateFile: Failed to find file", e);
            throw new ApplicationException("Could not update file. File not found", e);
        }
    }

    /**
     * Removes a file from the database
     * @param fileid The id of the file to remove
     * @param caller The caller
     * @throws org.tgdb.exceptions.ApplicationException If the file could not be removed
     */
    public void removeFile(int fileid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("FILE_W", caller);       
        try{
            FileRemote file = fileHome.findByPrimaryKey(new Integer(fileid));
            file.remove();
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#removeFile: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        } 
        catch (FinderException e) {
            logger.error("---------------------------------------->ResourceManagerBean#removeFile: Failed to find file", e);
            throw new ApplicationException("Could not remove file. File not found");
        }
        catch (RemoveException e) {
            logger.error("---------------------------------------->ResourceManagerBean#removeFile: failed to remove file", e);
            throw new ApplicationException("Could not remove file");
        }
    }
    
    /**
     * Get the first row of a file
     */
    public String getFirstRow(int fileid, TgDbCaller caller) throws ApplicationException
    {
        validate("FILE_R", caller);
        String row = "";
        try
        {
            FileRemote file = fileHome.findByPrimaryKey(new Integer(fileid));
            byte[] data = file.getData();
            int i=0;
            while (data[i]!='\n')
            {
                row += (char)data[i];
                i++;
            }
            return row;
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#getFirstRow: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        } 
        catch (FinderException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#getFirstRow: Error", e);
            throw new ApplicationException("Failed to get header row. File not found");
        }
        catch (Exception e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#getFirstRow: Error", e);
            throw new ApplicationException("Failed to get header row", e);
        }
    }
    
    /**
     * Returns all files in the database
     * @throws org.tgdb.exceptions.ApplicationException If the files could not be retrieved
     * @return The files in the database
     */
    public Collection getAllFiles(TgDbCaller caller) throws ApplicationException
    {
        validate("FILE_R", caller);
        Collection arr = new ArrayList();
        try
        {
            Collection files = fileHome.findAll();
            
            Iterator i = files.iterator();
            while (i.hasNext())
            {
                FileRemote f = (FileRemote)i.next();
                arr.add(new FileDTO(f));
            }
            return arr;
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#getAllFiles: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        } 
        catch (FinderException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#getAllFiles: Failed to find files", e);
            throw new ApplicationException("Failed to get files");
        }
    }
    
    // </editor-fold>
    

    // <editor-fold defaultstate="collapsed" desc="Resources">


    public Collection getResources(java.util.Collection categories, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("RESOURCE_R", caller);
        try {          
            Collection dtos = new ArrayList();
            
            Iterator i = categories.iterator();
            
            while(i.hasNext()) {
                ResourceCategoryRemote category = (ResourceCategoryRemote)i.next();
                dtos.add(new ResourceCategoryDTO(category));
            }                  
            
            return dtos;
        } 
        catch (Exception e) {
            logger.error("---------------------------------------->ResourceManagerBean#getResources: Failed to get resources", e);
            throw new ApplicationException("Failed to get resources as levels.");
        }        
    }

    public void createResourceCategory(int project, java.lang.String name, java.lang.String comm, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("PROJECT_ADM", caller);
        try{ 
            makeConnection();
            int categoryId = getIIdGenerator().getNextId(conn, "resource_category_seq");

            ResourceCategoryRemote category = resourceCategoryHome.create(name, comm, project, caller.getId(), categoryId, 0);
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#createResourceCategory: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        } 
        catch (CreateException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#createResourceCategory: Failed to create resource category", e);
            throw new ApplicationException("Could not create resource category", e);
        } 
        finally 
        {
            releaseConnection();
        }
    }

    public Collection getResourceCategories(int pid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try{ 
            Collection dtos = new ArrayList();
            Collection categories = resourceCategoryHome.findByProject(pid);
            Iterator i = categories.iterator();
            
            while(i.hasNext()) {
                ResourceCategoryRemote category = (ResourceCategoryRemote)i.next();
                dtos.add(new ResourceCategoryDTO(category));
            }  

            return dtos;
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#getResourceCategories: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        } 
        catch (FinderException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#getResourceCategories: Remote exception", e);
            throw new ApplicationException("Failed to find resouce categories.", e);
        }
        catch (Exception e){
            logger.error("---------------------------------------->ResourceManagerBean#getResourceCategories: General error, failed to get resource categories", e);
            throw new ApplicationException("Could not get resource categories", e);
        }
    }

    public ResourceCategoryDTO getResourceCategory(int categoryId, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("PROJECT_ADM", caller);
        try
        { 
            return new ResourceCategoryDTO(resourceCategoryHome.findByPrimaryKey(new Integer(categoryId)));
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#getResourceCategory: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        } 
        catch(FinderException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#getResourceCategory: Failed to find resource category", e);
            throw new ApplicationException("Could not get resource category", e);
        }
    }

    public void updateResourceCategory(int categoryId, String name, String comm, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("PROJECT_ADM", caller);
        try
        { 
            ResourceCategoryRemote category = resourceCategoryHome.findByPrimaryKey(new Integer(categoryId));
            category.setCaller(caller);
            category.setName(name);
            category.setComment(comm);
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#updateResourceCategory: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch(FinderException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#updateResourceCategory: Failed to update resource category, not found", e);
            throw new ApplicationException("Failed to update, could not find resource category", e);
        }
        catch (Exception e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#updateResourceCategory: Could not update resource category", e);
            throw new ApplicationException("Could not get update resource category", e);
        }
    }

    public void removeResourceCategory(int categoryId, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("PROJECT_ADM", caller);
        try{ 
            resourceCategoryHome.findByPrimaryKey(new Integer(categoryId)).remove();
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#removeResourceCategory: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch(FinderException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#removeResourceCategory: Failed to find resource category", e);
            throw new ApplicationException("Could not remove resource category, not found", e);
        }
        catch (RemoveException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#removeResourceCategory: Failed to remove ", e);
            throw new ApplicationException("Could not get remove resource category", e);
        }
    }
    
    public ResourceRemote createResource(LinkRemote link, int catId, TgDbCaller caller) throws ApplicationException
    {
        validate("RESOURCE_W", caller);
        int resourceId = 0;
        ResourceRemote resource = null;
        try{ 
            makeConnection();
            resourceId = getIIdGenerator().getNextId(conn, "resource_seq");
            releaseConnection();
            
            
            resource = resourceHome.create(resourceId, link.getProject().getPid(), 0, link.getLinkId(), catId, link.getName(), link.getComment(), caller);
            
            
            return resource;
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#createResource: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (CreateException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#createResource: Failed to create resource", e);
            throw new ApplicationException("Could not create resource.", e);
        }
        catch (Exception e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#createResource: Unknown error", e);
            throw new ApplicationException("Could not create resource", e);   
        }
    }

    public ResourceRemote createResource(int pid, java.lang.String name, java.lang.String comm, int fileId, int linkId, int catId, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("RESOURCE_W", caller);
        int resourceId = 0;
        ResourceRemote resource = null;
        try{ 
            makeConnection();
            resourceId = getIIdGenerator().getNextId(conn, "resource_seq");
            if(name == null || name.length() == 0)
                name = "New resource";
            resource = resourceHome.create(resourceId, pid, fileId, linkId, catId, name, comm, caller);
            logger.debug("---------------------------------------->ResourceManagerBean#createResource: Resource created. Resourceid="+resourceId);
            
            resource.setComment("123");
            
            return resource;
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#createResource: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (CreateException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#createResource: Failed to create resource", e);
            throw new ApplicationException("Could not create resource.", e);
        }
        catch (Exception e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#createResource: Unknown error", e);
            throw new ApplicationException("Could not create resource", e);   
        }
        finally 
        {
            releaseConnection();
        }
    }

    public ResourceDTO getResource(int resourceId, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("RESOURCE_R", caller);
        try{ 
            return new ResourceDTO(resourceHome.findByPrimaryKey(new Integer(resourceId)));
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#getResource: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (FinderException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#getResource: Failed to find resource", e);
            throw new ApplicationException("Could not get resource", e);
        }
    }

    public void updateResource(int resourceId, java.lang.String name, java.lang.String comm, String url, int catid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("RESOURCE_W", caller);
        try{
            if(name == null || name.length() == 0)
                name = "New resource";            
            ResourceRemote resource = resourceHome.findByPrimaryKey(new Integer(resourceId));
            resource.setCaller(caller);
            resource.setName(name);
            resource.setComment(comm);
            resource.setResourceCategory(catid);
            
            FileRemote file = resource.getFile();
            LinkRemote link = resource.getLink();
            
            if(file != null) {
                file.setCaller(caller);
                file.setComm(comm);
            }
            else if(link != null) {
                link.setCaller(caller);
                link.setComment(comm);
                link.setUrl(url);
            }
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#updateResource: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (FinderException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#updateResource: Failed to find resource", e);
            throw new ApplicationException("Could not update resource, resource not found", e);
        }
    }

    public void removeResource(int resourceId, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("RESOURCE_W", caller);
        try
        { 
            resourceHome.findByPrimaryKey(new Integer(resourceId)).remove();
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#removeResource: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (FinderException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#removeResource: Failed to find resource", e);
            throw new ApplicationException("Could not update resource, resource not found", e);
        }
        catch (RemoveException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#removeResource: Failed to remove", e);
            throw new ApplicationException("Could not get remove resource", e);
        }
    }
    
    
    
    /** 
     * Get all categories from the ResourceRemote collection
     * Returns a collection of ResourceCategoryRemote
     */
    public Collection getCategoriesFromResources(Collection resources) throws ApplicationException
    {
        Collection categories = new ArrayList();
        try
        {
            Iterator i = resources.iterator();
            while (i.hasNext())
            {
                ResourceRemote r = (ResourceRemote)i.next();
                ResourceCategoryRemote rc = r.getResourceCategory();
                if (!categories.contains(rc))
                    categories.add(rc);
            }    
            return categories;
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#getCategoriesFromResources: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (Exception ex)
        {
            logger.error("---------------------------------------->ResourceManagerBean#getCategoriesFromResources: Unknown error", ex);
            throw new ApplicationException("Failed to extract categories from resources", ex);
        }
    }
    
    /**
     * Get the resource tree from a collection of resources.
     * @param resources is a collection of ResourceRemote objects connected
     * to an object.
     * @param caller is the caller of the method
     * @throws org.tgdb.exceptions.ApplicationException 
     * @return 
     */
    public Collection getResourceTreeCollection(Collection resources, TgDbCaller caller) throws ApplicationException
    {
        //logger.debug("---------------------------------------->ResourceManagerBean#getResourceTreeCollection: Started");
        Collection resourceTree = new ArrayList();
        try
        {
            Collection tempRes = null;
            
            Collection categories = getCategoriesFromResources(resources);
            Iterator i = categories.iterator();
            while (i.hasNext())
            {
                ResourceCategoryRemote c = (ResourceCategoryRemote)i.next();
                
                tempRes = new ArrayList(resources);
                
                // Get all resource in this category (intersection)
                tempRes.retainAll(c.getResources());
                
                // Initilaize the dto
                ResourceBranchDTO dto = new ResourceBranchDTO(c, tempRes);
                
                // Add to collection
                resourceTree.add(dto);
            }
            return resourceTree;
        }   
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#getResourceTreeCollection: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to get resources", e);
        }
    }
    
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="SamplingUnit implementations">
    
    /**
     * Returns the files for a sampling unit
     * @param suid The sampling unit id
     * @param caller The caller
     * @throws org.tgdb.exceptions.ApplicationException If the files could not be retrieved
     * @return The files for a sampling unit
     */
    public Collection getSamplingUnitFiles(int suid, TgDbCaller caller) throws ApplicationException {
        validate("FILE_R", caller);         
        
        try {
            SamplingUnitRemote samplingUnit = samplingUnitHome.findByPrimaryKey(new Integer(suid));
            Collection arr = samplingUnit.getFiles();            
            Collection files = new ArrayList();
            Iterator i = arr.iterator();
            while(i.hasNext()) {
                FileRemote file = (FileRemote)i.next();
                files.add(new FileDTO(file));
            }            
            return files;
        } 
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#getSamplingUnitFiles: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (FinderException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#getSamplingUnitFiles: Failed to find su", e);
            throw new ApplicationException("Could not get files for sampling unit. Sampling unit not found", e);
        }
        catch (Exception e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#getSamplingUnitFiles: Failed to get files for SU", e);
            throw new ApplicationException("Failed to get files for sampling unit.", e);
        }
    }    

    /**
     * Returns all links for a sampling unit
     * @param suid The sampling unit id
     * @param caller The caller
     * @throws org.tgdb.exceptions.ApplicationException If the links could not be retrieved
     * @return The links for a sampling unit
     */
    public Collection getSamplingUnitLinks(int suid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("LINK_R", caller);         
        
        try {
            SamplingUnitRemote samplingUnit = samplingUnitHome.findByPrimaryKey(new Integer(suid));
            Collection arr = samplingUnit.getLinks();
            Collection links = new ArrayList();
            Iterator i = arr.iterator();

            while(i.hasNext()) {               
                LinkRemote link = (LinkRemote)i.next();
                links.add(new LinkDTO(link));
            }
            return links;
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#getSamplingUnitLinks: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (FinderException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#getSamplingUnitLinks: Failed to find su", e);
            throw new ApplicationException("Could not get links for sampling unit. Sampling unit not found", e);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get links for sampling unit.");
        }
    }
    
    /**
     * Adds a link to a sampling unit
     * @param suid The sampling unit id
     * @param caller The caller
     * @param name The name of the link
     * @param url The link url
     * @param comm The comment for the link
     * @throws org.tgdb.exceptions.ApplicationException If the link could not be added
     * @return The link id
     */
    public int addLinkToSamplingUnit(int suid, org.tgdb.TgDbCaller caller, String name, String url, String comm) throws ApplicationException {
        validate("LINK_W", caller);
        try {
            LinkRemote link = createLink(name, comm, url, caller);            
            SamplingUnitRemote samplingUnit = samplingUnitHome.findByPrimaryKey(new Integer(suid));
            samplingUnit.addLink(link);
            
            return link.getLinkId();
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#addLinkToSamplingUnit: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (FinderException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#addLinkToSamplingUnit: Failed to find su", e);
            throw new ApplicationException("Could not add link to sampling unit. Sampling unit not found", e);
        } 
        catch (Exception e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#addLinkToSamplingUnit:Failed in add link to su", e);
            throw new ApplicationException("Could not add link", e);            
        }
    }
    
    /**
     * Adds a file to a sampling unit
     * @param suid The sampling unit id
     * @param caller The caller
     * @param fileData The file data
     * @param name The name of the file
     * @param comm The comment for the file
     * @throws org.tgdb.exceptions.ApplicationException If the file could not be added
     * @return The file id
     */
    public int addFileToSamplingUnit(int suid, org.tgdb.TgDbCaller caller, FileDataObject fileData, String name, String comm) throws ApplicationException {
        validate("FILE_W", caller);
        try {
            FileRemote file = saveFile(name, comm, caller, fileData);
            SamplingUnitRemote samplingUnit = samplingUnitHome.findByPrimaryKey(new Integer(suid));
            samplingUnit.addFile(file);
            
            return file.getFileId();
        }
        catch (RemoteException e) 
        {
            logger.error("---------------------------------------->ResourceManagerBean#addFileToSamplingUnit: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (FinderException e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#addFileToSamplingUnit: Failed to find su", e);
            throw new ApplicationException("Could not add file to sampling unit. Sampling unit not found", e);
        } 
        catch (Exception e)
        {
            logger.error("---------------------------------------->ResourceManagerBean#addFileToSamplingUnit: Failed in add file to su", e);
            throw new ApplicationException("Could not add file", e);            
        }
    }
    
    // </editor-fold>
    
    
}
