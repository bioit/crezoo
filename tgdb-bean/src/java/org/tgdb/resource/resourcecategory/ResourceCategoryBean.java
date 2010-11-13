package org.tgdb.resource.resourcecategory;
import org.tgdb.TgDbCaller;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.project.user.UserRemote;
import org.tgdb.project.user.UserRemoteHome;
import org.tgdb.resource.resource.ResourceRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;

/**
 * This is the bean class for the ResourceCategoryBean enterprise bean.
 * Created Jan 17, 2006 4:22:41 PM
 * @author lami
 */
public class ResourceCategoryBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.resource.resourcecategory.ResourceCategoryRemoteBusiness {
    private javax.ejb.EntityContext context;
    private int parentId, userId, categoryId, projectId;
    private String name, comm;
    private java.sql.Date ts;
    private boolean dirty;
    
    private ProjectRemoteHome projectHome;
    private ResourceCategoryRemoteHome resourceCategoryHome;
    private UserRemoteHome userHome;  
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
        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
        resourceCategoryHome = (ResourceCategoryRemoteHome)locator.getHome(ServiceLocator.Services.RESOURCECATEGORY);
        resourceHome = (ResourceRemoteHome)locator.getHome(ServiceLocator.Services.RESOURCE);
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);        
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
            ps = conn.prepareStatement("delete from resource_category where category_id=?");
            ps.setInt(1, categoryId);
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("ResourceCategoryBean#ebjRemove: Unable to remove resource category. \n"+e.getMessage());
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
            ps = conn.prepareStatement("select category_id, name, comm, parent_id, user_id, ts, project_id " +
                    "from resource_category where category_id = ?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                categoryId = rs.getInt("category_id");
                name = rs.getString("name");
                comm = rs.getString("comm");
                userId = rs.getInt("user_id");
                parentId = rs.getInt("parent_id");
                ts = rs.getDate("ts");
                projectId = rs.getInt("project_id");
                dirty = false;
            } else
                throw new EJBException("ResourceCategoryBean#ejbLoad: Error loading resource category");
        } catch (Exception e) {
            throw new EJBException("ResourceCategoryBean#ejbLoad: "+e.getMessage());
        } finally {
            releaseConnection();
        }
    }

    public java.sql.Date getTimestamp() {
        return ts;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comm;
    }

    public int getResourceCategoryId() {
        return categoryId;
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

    public UserRemote getUser() {
        UserRemote user = null;
        try {
            user = userHome.findByPrimaryKey(new Integer(userId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public ResourceCategoryRemote getParentCategory() {
        ResourceCategoryRemote category = null;
        try {
            category = resourceCategoryHome.findByPrimaryKey(new Integer(parentId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return category;
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

    public void setParentCategory(int parentId) {
        this.parentId = parentId;
        userId = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());   
        dirty = true;        
    }

    public void setCaller(TgDbCaller caller) {
        this.caller = caller;
    }

    public Collection getResources() {        
        try {
            return resourceHome.findByCategory(categoryId);
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public java.util.Collection ejbFindByProject(int project) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection categories = new ArrayList();
        try {
            ps = conn.prepareStatement("select category_id from resource_category where project_id = ? order by name");
            ps.setInt(1, project);
            result = ps.executeQuery();
            
            while (result.next()) {
                categories.add(new Integer(result.getInt("category_id")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceCategoryBean#ejbFindByProject: Unable to find resource categories "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return categories;
    }

    public java.util.Collection ejbFindByParent(int parent) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection categories = new ArrayList();
        try {
            ps = conn.prepareStatement("select category_id from resource_category where parent_id = ? order by name");
            ps.setInt(1, parent);
            result = ps.executeQuery();
            
            while (result.next()) {
                categories.add(new Integer(result.getInt("category_id")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceCategoryBean#ejbFindByParent: Unable to find child resource categories "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return categories;
    }

    public boolean isRoot() {
        return parentId == 0;
    }

    public java.lang.Integer ejbCreate(java.lang.String name, java.lang.String comm, int project, int user, int category, int parent) throws javax.ejb.CreateException {
        makeConnection();

        PreparedStatement ps = null;
        this.name = name;
        this.ts = new java.sql.Date(System.currentTimeMillis());
        this.comm = comm;
        this.categoryId = category;
        this.parentId = parent;
        this.userId = user;
        this.projectId = project;
        
        try {
            ps = conn.prepareStatement("insert into resource_category (" +
                    "category_id, name, comm, parent_id, user_id, project_id, ts) values " +
                    "(?, ?, ?, ?, ?, ?, ?)");

            int i = 0;
            ps.setInt(++i, category);                
            ps.setString(++i, name);
            ps.setString(++i, comm);                
            if(parentId == 0)
                ps.setNull(++i, java.sql.Types.INTEGER);
            else
                ps.setInt(++i, parentId);
            ps.setInt(++i, user);
            ps.setInt(++i, project);
            ps.setDate(++i, ts);

            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new EJBException("ResourceCategoryBean#ejbCreate: Error creating resource category. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        
        return new Integer(category);
    }

    public void ejbPostCreate(java.lang.String name, java.lang.String comm, int project, int user, int category, int parent) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }

    public int getNumberOfResources() {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        int num = 0;
        try {
            ps = conn.prepareStatement("select count(resource_id) as num from resource where category_id = ?");
            ps.setInt(1, categoryId);
            result = ps.executeQuery();
            
            if (result.next()) {
                num = result.getInt("num");
            }
        } catch (SQLException se) {
            throw new EJBException("ResourceCategoryBean#getNumberOfResources: Unable to count resources in category "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return num;
    }

    public java.util.Collection ejbFindByProcess(int processId) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection categories = new ArrayList();
        try {
            ps = conn.prepareStatement("select rc.category_id from resource_category rc, r_resource_category_process rcp where rc.category_id = rcp.category_id and rcp.process_id = ? order by rc.name");
            ps.setInt(1, processId);
            result = ps.executeQuery();
            while (result.next()) {
                categories.add(new Integer(result.getInt("category_id")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceCategoryBean#ejbFindByProcess: Unable to find resource categories "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return categories;
    }

    public java.util.Collection ejbFindByPathway(int pathwayId) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection categories = new ArrayList();
        try {
            ps = conn.prepareStatement("select rc.category_id from resource_category rc, r_resource_category_pathway rcp where rc.category_id = rcp.category_id and rcp.pathway_id = ? order by rc.name");
            ps.setInt(1, pathwayId);
            result = ps.executeQuery();
            while (result.next()) {
                categories.add(new Integer(result.getInt("category_id")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceCategoryBean#ejbFindByPathway: Unable to find resource categories "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return categories;
    }

    public java.util.Collection ejbFindByProtein(int proteinId) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection categories = new ArrayList();
        try {
            ps = conn.prepareStatement("select rc.category_id from resource_category rc, r_resource_category_protein rcp where rc.category_id = rcp.category_id and rcp.protein_id = ? order by rc.name");
            ps.setInt(1, proteinId);
            result = ps.executeQuery();
            while (result.next()) {
                categories.add(new Integer(result.getInt("category_id")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceCategoryBean#ejbFindByProtein: Unable to find resource categories "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return categories;
    }

    public java.util.Collection ejbFindByComplex(int complexId) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection categories = new ArrayList();
        try {
            ps = conn.prepareStatement("select rc.category_id from resource_category rc, r_resource_category_complex rcp where rc.category_id = rcp.category_id and rcp.complex_id = ? order by rc.name");
            ps.setInt(1, complexId);
            result = ps.executeQuery();
            while (result.next()) {
                categories.add(new Integer(result.getInt("category_id")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceCategoryBean#ejbFindByComplex: Unable to find resource categories "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return categories;
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() {
        if (dirty) {
            makeConnection();
            
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("update resource_category set name = ?, comm = ?, " +
                        "parent_id = ?, user_id = ?, ts = ? where category_id = ?");
                
                int i = 0;
                ps.setString(++i, name);
                ps.setString(++i, comm);  
                if(parentId == 0)
                    ps.setNull(++i, java.sql.Types.INTEGER);
                else
                    ps.setInt(++i, parentId);
                ps.setInt(++i, userId);
                ps.setDate(++i, ts);
                ps.setInt(++i, categoryId);
                
                int rows = ps.executeUpdate();
                if (rows!=1) {
                    throw new EJBException("ResourceCategoryBean#ejbStore: Error saving resource category. Rows affected "+rows);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new EJBException("ResourceCategoryBean#ejbStore: Error saving resource category. \n"+e.getMessage());
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
            ps = conn.prepareStatement("select category_id from resource_category where category_id = ?");
            ps.setInt(1,aKey.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("ResourceCategoryBean#ejbFindByPrimaryKey: Cannot find resource category");
            }
        } catch (SQLException se) {
            throw new FinderException("ResourceCategoryBean#ejbFindByPrimaryKey: Unable to find resource category "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return aKey;
    }
}
