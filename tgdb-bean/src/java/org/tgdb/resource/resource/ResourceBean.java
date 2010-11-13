package org.tgdb.resource.resource;
import org.tgdb.TgDbCaller;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.project.user.UserRemote;
import org.tgdb.project.user.UserRemoteHome;
import org.tgdb.resource.file.FileRemote;
import org.tgdb.resource.file.FileRemoteHome;
import org.tgdb.resource.link.LinkRemote;
import org.tgdb.resource.link.LinkRemoteHome;
import org.tgdb.resource.resourcecategory.ResourceCategoryRemote;
import org.tgdb.resource.resourcecategory.ResourceCategoryRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;

/**
 * This is the bean class for the ResourceBean enterprise bean.
 * Created Jan 17, 2006 4:11:47 PM
 * @author lami
 */
public class ResourceBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.resource.resource.ResourceRemoteBusiness {
    private javax.ejb.EntityContext context;
    private int resourceId, userId, projectId, categoryId, fileId, linkId;
    private String name, comm;
    private java.sql.Date ts;
    private boolean dirty;
    
    private ProjectRemoteHome projectHome;
    private ResourceCategoryRemoteHome resourceCategoryHome;
    private UserRemoteHome userHome;
    private FileRemoteHome fileHome;
    private LinkRemoteHome linkHome;    
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise beans, Web services)
    // TODO Add business methods
    // TODO Add create methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
        resourceCategoryHome = (ResourceCategoryRemoteHome)locator.getHome(ServiceLocator.Services.RESOURCECATEGORY);
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);
        fileHome = (FileRemoteHome)locator.getHome(ServiceLocator.Services.FILE);
        linkHome = (LinkRemoteHome)locator.getHome(ServiceLocator.Services.LINK);        
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
    public void ejbRemove() {
        makeConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("delete from resource where resource_id=?");
            ps.setInt(1, resourceId);
            ps.execute();
            if(fileId != 0) {
                ps = conn.prepareStatement("delete from file where fileid=?");
                ps.setInt(1, fileId);
                ps.execute();                
            }
            else if(linkId != 0) {
                ps = conn.prepareStatement("delete from link where linkid=?");
                ps.setInt(1, linkId);
                ps.execute();                
            }            
        } catch (Exception e) {
            throw new EJBException("ResourceBean#ebjRemove: Unable to remove resource. \n"+e.getMessage());
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
            ps = conn.prepareStatement("select resource_id, name, comm, link_id, category_id, file_id, user_id, ts, project_id " +
                    "from resource where resource_id = ?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                resourceId = rs.getInt("resource_id");
                name = rs.getString("name");
                comm = rs.getString("comm");
                linkId = rs.getInt("link_id");
                fileId = rs.getInt("file_id");
                userId = rs.getInt("user_id");
                categoryId = rs.getInt("category_id");
                ts = rs.getDate("ts");
                projectId = rs.getInt("project_id");
                dirty = false;
            } else
                throw new EJBException("ResourceBean#ejbLoad: Error loading resource");
        } catch (Exception e) {
            throw new EJBException("ResourceBean#ejbLoad: "+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    
    /**
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() {
        if (dirty) {
            makeConnection();
            
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("update resource set name = ?, comm = ?, " +
                        "link_id = ?, file_id = ?, user_id = ?, ts = ?, " +
                        "category_id = ? where resource_id = ?");
                
                int i = 0;
                ps.setString(++i, name);
                ps.setString(++i, comm);
                
                if (linkId != 0)
                    ps.setInt(++i, linkId);
                else
                    ps.setNull(++i, java.sql.Types.INTEGER);
                
                if (fileId != 0)
                    ps.setInt(++i, fileId);
                else
                    ps.setNull(++i, java.sql.Types.INTEGER);
                
                ps.setInt(++i, userId);
                ps.setDate(++i, ts);
                ps.setInt(++i, categoryId);
                ps.setInt(++i, resourceId);
                
                int rows = ps.executeUpdate();
                if (rows!=1) {
                    throw new EJBException("ResourceBean#ejbStore: Error saving resource. Rows affected "+rows);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new EJBException("ResourceBean#ejbStore: Error saving resource. \n"+e.getMessage());
            } finally {
                releaseConnection();
                dirty = false;
            }
        }
    }    

    public void setCaller(TgDbCaller caller) {
        this.caller = caller;
    }

    public ProjectRemote getProject() {
        ProjectRemote project = null;
        try {
            project = projectHome.findByPrimaryKey(new Integer(projectId));
            project.setCaller(caller);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return project;
    }

    public ResourceCategoryRemote getResourceCategory() {
        ResourceCategoryRemote category = null;
        try {
            category = resourceCategoryHome.findByPrimaryKey(new Integer(categoryId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return category;
    }

    public UserRemote getUser() {
        UserRemote user = null;
        try {
            user = userHome.findByPrimaryKey(new Integer(userId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comm;
    }

    public void setName(java.lang.String name) {
        this.name = name;
        userId = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());   
        dirty = true;
    }

    public void setComment(java.lang.String comm) {
        this.comm = comm;
        userId = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());   
        dirty = true;
    }

    public FileRemote getFile() {
        try {
            FileRemote file = fileHome.findByPrimaryKey(new Integer(fileId));
            return file;
        } catch (Exception e) {
        }
        return null;
    }
    
    public LinkRemote getLink() {
        try {
            LinkRemote link = linkHome.findByPrimaryKey(new Integer(linkId));
            return link;
        } catch (Exception e) {
        }
        return null;
    }

    public java.sql.Date getTimestamp() {
        return ts;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setFile(int fileId) {
        this.fileId = fileId;
        userId = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());   
        dirty = true;        
    }

    public void setLink(int linkId) {
        this.linkId = linkId;
        userId = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());   
        dirty = true;        
    }

    public java.util.Collection ejbFindByCategory(int category) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection resources = new ArrayList();
        try {
            ps = conn.prepareStatement("select resource_id from resource where category_id = ? order by name");
            ps.setInt(1, category);
            result = ps.executeQuery();
            
            while (result.next()) {
                resources.add(new Integer(result.getInt("resource_id")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceBean#ejbFindByCategory: Unable to find resources "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return resources;
    }

    public java.util.Collection ejbFindByProject(int project) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection resources = new ArrayList();
        try {
            ps = conn.prepareStatement("select resource_id from resource where project_id = ? order by name");
            ps.setInt(1, project);
            result = ps.executeQuery();
            
            while (result.next()) {
                resources.add(new Integer(result.getInt("resource_id")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceBean#ejbFindByCategory: Unable to find resources "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return resources;
    }

    public String getResourceLink() {
        String link = "";
        try {
            if(fileId == 0)
                link = getLink().getUrl();
            else
                link = "Controller?workflow=ViewFile&fileid="+fileId;            
        } catch (Exception e) { }

        return link;
    }

    public String getResourceType() {
        String type = "Weblink";
        if(linkId == 0)
            type = "File";
        return type;
    }

    public java.lang.Integer ejbCreate(int resourceId, int projectId, int fileId, int linkId, int categoryId, java.lang.String name, java.lang.String comm, TgDbCaller caller) throws javax.ejb.CreateException {
        makeConnection();
        Integer pk = null;
        try {
            
            this.caller = caller;            
            this.resourceId = resourceId;                        
            this.comm = comm;
            this.name = name;
            this.userId = caller.getId();
            this.projectId = projectId;
            this.linkId = linkId;
            this.fileId = fileId;
            this.categoryId = categoryId;
            
            ts = new java.sql.Date(System.currentTimeMillis());            
            
            pk = new Integer(resourceId);
            
            PreparedStatement ps = conn.prepareStatement("insert into resource (resource_id, name, comm, file_id, link_id, category_id, user_id, ts, project_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int i = 0;
            ps.setInt(++i, resourceId);
            ps.setString(++i, name);
            ps.setString(++i, comm);
            if (fileId != 0)
                ps.setInt(++i, fileId); 
            else
                ps.setNull(++i, java.sql.Types.INTEGER);    
            if (linkId != 0)
                ps.setInt(++i, linkId); 
            else
                ps.setNull(++i, java.sql.Types.INTEGER);             
            ps.setInt(++i, categoryId);
            ps.setInt(++i, userId);
            ps.setDate(++i, ts);
            ps.setInt(++i, projectId);
            
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("ResourceBean#ejbCreate: Unable to create resource. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }

    public void ejbPostCreate(int resourceId, int projectId, int fileId, int linkId, int categoryId, java.lang.String name, java.lang.String comm, TgDbCaller caller) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }

    public java.util.Collection ejbFindByProcess(int processId) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection resources = new ArrayList();
        try {
            ps = conn.prepareStatement("select resource_id from r_resource_process where process_id = ?");
            ps.setInt(1, processId);
            result = ps.executeQuery();
            
            while (result.next()) {
                resources.add(new Integer(result.getInt("resource_id")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceBean#ejbFindByProcess: Unable to find resources "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return resources;
    }

    public java.util.Collection ejbFindByPathway(int pathwayId) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection resources = new ArrayList();
        try {
            ps = conn.prepareStatement("select resource_id from r_resource_pathway where pathway_id = ?");
            ps.setInt(1, pathwayId);
            result = ps.executeQuery();
            
            while (result.next()) {
                resources.add(new Integer(result.getInt("resource_id")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceBean#ejbFindByPathway: Unable to find resources "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return resources;
    }

    public java.util.Collection ejbFindByProtein(int proteinId) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection resources = new ArrayList();
        try {
            ps = conn.prepareStatement("select resource_id from r_resource_protein where protein_id = ?");
            ps.setInt(1, proteinId);
            result = ps.executeQuery();
            
            while (result.next()) {
                resources.add(new Integer(result.getInt("resource_id")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceBean#ejbFindByProtein: Unable to find resources "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return resources;
    }

    public java.util.Collection ejbFindByComplex(int complexId) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection resources = new ArrayList();
        try {
            ps = conn.prepareStatement("select resource_id from r_resource_complex where complex_id = ?");
            ps.setInt(1, complexId);
            result = ps.executeQuery();
            
            while (result.next()) {
                resources.add(new Integer(result.getInt("resource_id")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceBean#ejbFindByComplex: Unable to find resources "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return resources;
    }

    public void setResourceCategory(int categoryId) {
        this.categoryId = categoryId;
        userId = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());   
        dirty = true; 
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
            ps = conn.prepareStatement("select resource_id from resource where resource_id = ?");
            ps.setInt(1,aKey.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("ResourceBean#ejbFindByPrimaryKey: Cannot find resource");
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceBean#ejbFindByPrimaryKey: Unable to find resource"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return aKey;
    }

    public Collection ejbFindBySamplingUnit(int suid, TgDbCaller caller) throws javax.ejb.FinderException
    {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection resources = new ArrayList();
        try {
            ps = conn.prepareStatement("select resourceid from r_resource_su where suid = ?");
            ps.setInt(1, suid);
            result = ps.executeQuery();
            
            while (result.next()) {
                resources.add(new Integer(result.getInt("resourceid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceBean#ejbFindBySamplingUnit: Unable to find resources "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return resources;
    }

    public Collection ejbFindByModel(int eid, TgDbCaller caller) throws javax.ejb.FinderException
    {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection resources = new ArrayList();
        try {
            ps = conn.prepareStatement("select resourceid from r_resource_model where eid = ?");
            ps.setInt(1, eid);
            result = ps.executeQuery();
            
            while (result.next()) {
                resources.add(new Integer(result.getInt("resourceid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceBean#ejbFindBySamplingUnit: Unable to find resources "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return resources;
    }
    
    
}
