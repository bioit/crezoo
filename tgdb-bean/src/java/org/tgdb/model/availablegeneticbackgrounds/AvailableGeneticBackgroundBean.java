package org.tgdb.model.availablegeneticbackgrounds;

import javax.ejb.*;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.model.expmodel.ExpModelRemoteHome;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.project.ProjectRemoteHome;
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

public class AvailableGeneticBackgroundBean extends AbstractTgDbBean implements EntityBean, AvailableGeneticBackgroundRemoteBusiness {
    private EntityContext context;
    
    private int aid, pid;
    private String avbackname;
    
    private boolean dirty;
    private UserRemoteHome userHome;
    private ExpModelRemoteHome modelHome;
    private ProjectRemoteHome projectHome;
    
    //ejb infrastructure methods
    // <editor-fold>
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(EntityContext aContext) {
        context = aContext;
        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
        modelHome = (ExpModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPMODEL);
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
            ps = conn.prepareStatement("delete from available_genetic_back where aid=?");
            ps.setInt(1, aid);
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("AvailableGeneticBackgroundBean#ejbRemove: Unable to delete Available Genetic Background.\n"+e.getMessage());
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
            ps = conn.prepareStatement("select aid, avbackname, pid " +
                    "from available_genetic_back where aid=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                aid = rs.getInt("aid");
                avbackname = rs.getString("avbackname");   
                pid = rs.getInt("pid");
                dirty = false;
            } else
                throw new EJBException("AvailableGeneticBackgroundBean#ejbLoad: Error loading Available Genetic Background");
        } catch (Exception e) {
            throw new EJBException("AvailableGeneticBackgroundBean#ejbLoad: error loading Available Genetic Background. \n"+e.getMessage());
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
                ps = conn.prepareStatement("update available_genetic_back set avbackname=? " +
                        "where aid=?");

                ps.setString(1, avbackname);
                ps.setInt(2, aid);
                ps.execute();
            } catch (Exception e) {
                throw new EJBException("AvailableGeneticBackgroundBean#ejbStore: error storing Available Genetic Background. \n"+e.getMessage());
            } finally {
                releaseConnection();
                dirty = false;
            }
        }
    }
    
    // </editor-fold>
    
    //finder methods
    //<editor-fold>
    public Integer ejbFindByPrimaryKey(Integer key) throws FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select aid from available_genetic_back where aid = ?");
            ps.setInt(1,key.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("AvailableGeneticBackgroundBean#ejbFindByPrimaryKey: Cannot find Available Genetic Background. No next in resultset");
            }
        } catch (SQLException se) {
            throw new FinderException("AvailableGeneticBackgroundBean#ejbFindByPrimaryKey: Cannot find Available Genetic Background. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return key;
    }

    public Collection ejbFindByProject(int pid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select aid from available_genetic_back where pid = ? order by avbackname");
            ps.setInt(1,pid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("aid")));
            }
        } catch (SQLException se) {
            throw new FinderException("AvailableGeneticBackgroundBean#ejbFindByProject: Cannot find repositories by project "+pid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    //</editor-fold>
    
    //set+get methods
    //<editor-fold>
    public int getAid() {
        return aid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
        dirty = true;
    }

    public String getAvbackname() {
        return avbackname;
    }

    public void setAvbackname(String avbackname) {
        this.avbackname = avbackname;
        dirty = true;
    }
    
    public ProjectRemote getProject() throws ApplicationException {
        try
        {
            ProjectRemote prj = projectHome.findByPrimaryKey(new Integer(pid));
            return prj;
        }
        catch (Exception e)
        {
            throw new ApplicationException("Could not get project");
        }
    }
    
    //</editor-fold>
    
    //create+postcreate methods
    //<editor-fold>
    public Integer ejbCreate(int aid, String avbackname, int pid, org.tgdb.project.project.ProjectRemote project) throws javax.ejb.CreateException {
        makeConnection();
        Integer pk = null;
        try {
            this.aid = aid;
            this.avbackname = avbackname;
            this.pid = project.getPid();
            
            pk = new Integer(aid);
            
            PreparedStatement ps = conn.prepareStatement("insert into available_genetic_back (aid, avbackname, pid) values (?,?,?)");
            ps.setInt(1, aid);
            ps.setString(2, avbackname);
            ps.setInt(3, pid);
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("AvailableGeneticBackgroundBean#ejbCreate: Unable to create Available Genetic Background. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }

    public void ejbPostCreate(int aid, String avbackname, int pid, org.tgdb.project.project.ProjectRemote project) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }
    
    //</editor-fold>
    
}
