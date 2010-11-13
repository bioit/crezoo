package org.tgdb.resource.link;
import org.tgdb.TgDbCaller;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.project.user.UserRemote;
import org.tgdb.project.user.UserRemoteHome;
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
 * This is the bean class for the LinkBean enterprise bean.
 * Created Dec 5, 2005 10:53:02 AM
 * @author lami
 */
public class LinkBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.resource.link.LinkRemoteBusiness {
    private javax.ejb.EntityContext context;
    private String name, url, comm;
    private int linkId, userId, projectId;
    private java.sql.Date ts;
    private UserRemoteHome userHome;  
    private ProjectRemoteHome projectHome;     
    private boolean dirty;
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click on the + sign on the left to edit the code.">
    // TODO Consider creating Transfer Object to encapsulate data
    // TODO Review finder methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);      
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
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("delete from link where linkid = ?");
            ps.setInt(1, linkId);
            
            int res = ps.executeUpdate();
            
            if (res!=1) {
                throw new EJBException("Cannot remove link");
            }
        } catch (SQLException se) {
            throw new EJBException(se);
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
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Integer pk = (Integer)context.getPrimaryKey();
        try {
            ps = conn.prepareStatement("select name, comm, url, id, ts from link where linkid = ?");
            ps.setInt(1, pk.intValue());
            
            result = ps.executeQuery();
            
            if (result.next()) {
                linkId = pk.intValue();
                name = result.getString("name");
                comm = result.getString("comm");
                url = result.getString("url");
                userId = result.getInt("id");
                ts = result.getDate("ts");
                dirty = false;
            } else
                throw new EJBException();
        } catch (SQLException se) {
            throw new EJBException(se);
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
            try {
                PreparedStatement ps = null;
                ps = conn.prepareStatement("update link set name = ? , comm = ?," +
                        " url = ?, id  = ?, ts = ? where linkid = ?");
                
                ps.setString(1, name);
                ps.setString(2, comm);
                ps.setString(3, url);
                ps.setInt(4, userId);
                ps.setDate(5, ts);
                ps.setInt(6, linkId);
                
                ps.execute();
            } catch (Exception e) {
                e.printStackTrace();
                throw new EJBException("Error updating link: "+linkId);
            } finally {
                releaseConnection();
                dirty = false;
            }
        }      
    }
    // </editor-fold>
    

    /**
     * Returns the name of the link
     * @return The name of the link
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the url for the link
     * @return The link url
     */
    public String getUrl() {
        if(!url.startsWith("http://"))
            return "http://"+url;
        return url;
    }

    /**
     * Returns the comment for the link
     * @return The comment for the link
     */
    public String getComment() {
        return comm;
    }

    /**
     * Returns the link id
     * @return The link id
     */
    public int getLinkId() {
        return linkId;
    }

    /**
     * Returns the user that made the last modifications on the link
     * @return The user that made the last modifications on the link
     */
    public UserRemote getUser() {
        try {
            return userHome.findByPrimaryKey(new Integer(userId));
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * The date for when the last modifications were made on the link
     * @return The date for when the last modifications were made on the link
     */
    public java.sql.Date getTs() {
        return ts;
    }

    /**
     * Sets the name of the link
     * @param name The name of the link
     */
    public void setName(java.lang.String name) {
        this.name = name;
        userId = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;
    }

    /**
     * Sets the comment for the link
     * @param comm The comment
     */
    public void setComment(java.lang.String comm) {
        this.comm = comm;
        userId = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;
    }

    /**
     * Sets the URL for the link
     * @param url The url for the link
     */
    public void setUrl(java.lang.String url) {
        this.url = url;
        userId = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;
    }

    /**
     * Returns the project that the link belongs to
     * @return The project that the link belongs to
     */
    public ProjectRemote getProject() {
        try {
            return projectHome.findByPrimaryKey(new Integer(projectId));
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Finds all links for a certain project
     * @param pid The project id
     * @throws javax.ejb.FinderException If the links could not be retrieved
     * @return The links for a project
     */
    public java.util.Collection ejbFindByProject(int pid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select linkid from link where pid = ?");
            ps.setInt(1, pid);

            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("linkid")));
            }
        } catch (SQLException se) {
            throw new FinderException("LinkBean#ejbFindByProject: Cannot find links by project: "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    /**
     * Finds all links for a sampling unit
     * @param suid The sampling unit id
     * @throws javax.ejb.FinderException If the links could not be retrived
     * @return The links for a sampling unit
     */
    public java.util.Collection ejbFindBySamplingUnit(int suid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select r.linkid from r_link_su r, link l where r.linkid = l.linkid and r.suid = ? order by l.name");
            ps.setInt(1, suid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("linkid")));
            }
        } catch (SQLException se) {
            throw new EJBException("LinkBean#findBySamplingUnit: Cannot find links for sampling unit.\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;        
    }
    
    
    /**
     * Creates a new link
     * @param linkId 
     * @param name 
     * @param url 
     * @param comm 
     * @param caller 
     * @throws javax.ejb.CreateException 
     * @return 
     */
    public java.lang.Integer ejbCreate(int linkId, String name, String url, String comm, TgDbCaller caller)  throws javax.ejb.CreateException {
        this.name = name;
        this.comm = comm;
        this.userId = caller.getId(); 
        this.ts = new java.sql.Date(System.currentTimeMillis());
        this.url = url;
        if(!url.startsWith("http://"))
            url = "http://"+url;
        this.linkId = linkId;
        this.projectId = caller.getPid();
        
        makeConnection();
        
        try {
            PreparedStatement ps = null;
            ps = conn.prepareStatement("insert into link (linkid, name, comm, url, pid, id, ts) values (?,?,?,?,?,?,?)");
            ps.setInt(1, linkId);
            ps.setString(2, name);
            ps.setString(3, comm);
            ps.setString(4, url);
            if (projectId==0)
                ps.setNull(5, java.sql.Types.INTEGER);
            else
                ps.setInt(5, projectId);
            ps.setInt(6, userId);
            ps.setDate(7, ts);
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            throw new CreateException("Unable to create link: "+e.getMessage());
        } finally {
            releaseConnection();
        }
        
        return new Integer(linkId);
    }

    /**
     * Sets the caller
     * @param caller The caller
     */
    public void setCaller(TgDbCaller caller) {
        this.caller = caller;
    }
    
    /**
     * 
     * @param linkId 
     * @param name 
     * @param url 
     * @param comm 
     * @param caller 
     */
    public void ejbPostCreate(int linkId, String name, String url, String comm, TgDbCaller caller) {
        // TODO populate relationships here if appropriate
        
    }
    
    /**
     * 
     * @param linkid 
     * @throws javax.ejb.FinderException 
     * @return 
     */
    public java.lang.Integer ejbFindByPrimaryKey(java.lang.Integer linkid) throws javax.ejb.FinderException {                
        makeConnection();        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select linkid from link where linkid = ?");
            ps.setInt(1, linkid.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("LinkBean#ejbFindByPrimaryKey: Cannot find link");
            }
        } catch (SQLException se) {
            throw new FinderException("LinkBean#ejbFindByPrimaryKey: Cannot find link");
        } finally {
            releaseConnection();
        }
        return linkid;
    }    
}
