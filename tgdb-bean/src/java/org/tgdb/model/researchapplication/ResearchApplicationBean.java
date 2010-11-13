package org.tgdb.model.researchapplication;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.model.expmodel.ExpModelRemoteHome;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.project.user.UserRemote;
import org.tgdb.project.user.UserRemoteHome;
import org.tgdb.search.Keyword;
import org.tgdb.servicelocator.ServiceLocator;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.*;


/**
 * This is the bean class for the ResearchApplicationBean enterprise bean.
 * Created Dec 13, 2005 2:23:45 PM
 * @author heto
 */
public class ResearchApplicationBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.model.researchapplication.ResearchApplicationRemoteBusiness {
    private javax.ejb.EntityContext context;
    
    private int raid, pid, id;
    private String name;
    private String comm;
    private java.sql.Date ts;
    
    private boolean dirty;
    
    private UserRemoteHome userHome;    
    private ProjectRemoteHome projectHome; 
    private ExpModelRemoteHome modelHome;
    
    
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
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);         
        modelHome = (ExpModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPMODEL);
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
            ps = conn.prepareStatement("delete from research_application where raid=?");
            ps.setInt(1, raid);
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("ResearchApplicationBean#ejbRemove: Unable to delete research application. \n"+e.getMessage());
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
            ps = conn.prepareStatement("select raid,name,comm, pid, ts, id " +
                    "from research_application where raid=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                raid = rs.getInt("raid");
                name = rs.getString("name");
                comm = rs.getString("comm");
                pid = rs.getInt("pid");
                ts = rs.getDate("ts");
                id = rs.getInt("id");
                dirty = false;
            } else
                throw new EJBException("ResearchApplicationBean#ejbLoad: Error loading ResearchApplication");
        } catch (Exception e) {
            throw new EJBException("ResearchApplicationBean#ejbLoad: error loading ResearchApplicationBean. \n"+e.getMessage());
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
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("update research_application set name=?,comm=?,pid=?,id=?,ts=?" +
                        "where raid=?");

                ps.setString(1, name);
                ps.setString(2, comm);
                ps.setInt(3, pid);
                ps.setInt(4, caller.getId());
                ps.setDate(5, new Date(System.currentTimeMillis()));
                
                ps.setInt(6, raid);

                ps.execute();
            } catch (Exception e) {
                throw new EJBException("ResearchApplicationBean#ejbStore: error storing research application. \n"+e.getMessage());
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
    public java.lang.Integer ejbFindByPrimaryKey(Integer key) throws javax.ejb.FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select raid from research_application where raid = ?");
            ps.setInt(1,key.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("ResearchApplicationBean#ejbFindByPrimaryKey: Cannot find ResearchApplication. No next in resultset");
            }
        } catch (SQLException se) {
            throw new FinderException("ResearchApplicationBean#ejbFindByPrimaryKey: Cannot find ResearchApplication. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return key;
    }

    /**
     * Returns the id of the research application
     * @return The research application id
     */
    public int getRaid() {
        return raid;
    }

    /**
     * Returns the name of the research application
     * @return The name of the research application
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the resarch application
     * @param name The name of the research application
     */
    public void setName(String name) {
        this.name = name;
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());         
        dirty = true;
    }

    /**
     * Returns the comment for the research application
     * @return The comment
     */
    public String getComm() {
        return comm;
    }
    
    /**
     * Sets the caller
     * @param caller The caller
     */
    public void setCaller(TgDbCaller caller) {
        this.caller = caller;
        dirty = true;
    }  
    
    public TgDbCaller getCaller()
    {
        return caller;
    }

    /**
     * Finds all research applications for a project
     * @param pid The project id
     * @throws javax.ejb.FinderException If the research applications could not be retrieved
     * @return The research applications for the project
     */
    public java.util.Collection ejbFindByProject(int pid) throws javax.ejb.FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection apps = new ArrayList();
        try {
            ps = conn.prepareStatement("select raid from research_application where pid = ? order by raid");
            ps.setInt(1, pid);
            result = ps.executeQuery();
            
            while (result.next()) {
                apps.add(new Integer(result.getInt("raid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResearchApplicationBean#ejbFindByProject: Cannot find ResearchApplication. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        
        return apps;
    }

    /**
     * 
     * @param name 
     * @param comm 
     * @param pid 
     * @param raid 
     * @param caller 
     * @throws javax.ejb.CreateException 
     * @return 
     */
    public java.lang.Integer ejbCreate(java.lang.String name, java.lang.String comm, int pid, int raid, TgDbCaller caller) throws javax.ejb.CreateException {
        try {
            this.caller = caller;
            this.name = name;
            this.comm = comm;
            this.pid = pid;
            this.raid = raid;
            
            ts = new Date(System.currentTimeMillis());
            
            makeConnection();
                        
            String sql = "insert into research_application (raid, name, comm, id, ts, pid) values (?,?,?,?,?,?);";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, raid);
            stmt.setString(2, name);
            stmt.setString(3, comm);
            stmt.setInt(4, caller.getId());
            stmt.setDate(5, ts);
            stmt.setInt(6, pid);
            
            stmt.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("Unable to insert research application");
        } finally {
            releaseConnection();
        }
        return new Integer(raid);
    }

    /**
     * 
     * @param name 
     * @param comm 
     * @param pid 
     * @param raid 
     * @param caller 
     * @throws javax.ejb.CreateException 
     */
    public void ejbPostCreate(java.lang.String name, java.lang.String comm, int pid, int raid, TgDbCaller caller) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }

    /**
     * Sets the comment for the research application
     * @param comm The comment
     */
    public void setComm(String comm) {
        this.comm = comm;
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());         
        dirty = true;
    }

    /**
     * Returns the username of the user which made the latest changes on the research application
     * @return The username of the user that made the latest changes on the research application
     */
    public UserRemote getUser() {
        try {
            return userHome.findByPrimaryKey(new Integer(id));
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Returns the date for when the latest changes were made on the research application
     * @return The date for the latest changes
     */
    public java.sql.Date getTs() {
        return ts;
    }

    public Collection ejbFindByName(String name) throws javax.ejb.FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection apps = new ArrayList();
        try {
            ps = conn.prepareStatement("select raid from research_application where lower(name) like lower(?)");
            ps.setString(1, name);
            result = ps.executeQuery();
            
            while (result.next()) {
                apps.add(new Integer(result.getInt("raid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResearchApplicationBean#ejbFindByName: Cannot find ResearchApplication. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }        
        return apps;
    }

    /**
     * Returns the project for the research application
     * @return The project that the research application belongs to
     */
    public ProjectRemote getProject() {
        try {
            return projectHome.findByPrimaryKey(new Integer(pid));
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Collection getModels() throws ApplicationException
    {
        try
        {
            Collection models = modelHome.findByResearchApplication(raid, caller);
            return models;
            //return modelHome.findByResearchApplication(raid, caller);
        }
        catch (Exception e)
        {
            throw new ApplicationException("failed to get models", e);
        }
    }

    public Collection ejbFindByKeyword(Keyword keyword, TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        int key = 0;
        Collection arr = new ArrayList();
        try {
            ps = conn.prepareStatement("select raid from research_application where lower(name) like ? or lower(comm) like ?");
            
            String search = "%"+keyword.getKeyword()+"%";
            
            ps.setString(1, search);
            ps.setString(2, search);
            result = ps.executeQuery();
            
            while (result.next())
            {
                arr.add(new Integer(result.getInt("raid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ResearchApplicationBean#ejbFindByKeyword: Cannot find research application by keyword. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
}
