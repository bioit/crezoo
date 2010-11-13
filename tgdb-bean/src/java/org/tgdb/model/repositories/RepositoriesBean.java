package org.tgdb.model.repositories;

import javax.ejb.*;
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
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;

public class RepositoriesBean extends AbstractTgDbBean implements EntityBean, RepositoriesRemoteBusiness {
    private EntityContext context;
    
    private int rid, pid, hasdb;
    private String reponame, repourl, mouseurl;
    
    private boolean dirty;
    private UserRemoteHome userHome;
    private ExpModelRemoteHome modelHome;
    private ProjectRemoteHome projectHome;
    
    //ejb methods
    // <editor-fold defaultstate="collapsed">
    
    public void setEntityContext(EntityContext aContext) {
        context = aContext;
        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
        modelHome = (ExpModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPMODEL);
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);
    }
    
    public void ejbActivate() {}
    
    public void ejbPassivate() {}
    
    public void ejbRemove() {
        makeConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("delete from repositories where rid=?");
            ps.setInt(1, rid);
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("RepositoriesBean#ejbRemove: Unable to delete Repository.\n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    public void unsetEntityContext() {
        context = null;
    }
    
    public void ejbLoad() {
        makeConnection();
        Integer pk = (Integer)context.getPrimaryKey();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select rid, reponame, pid, hasdb, mouseurl, repourl " +
                    "from repositories where rid=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                rid = rs.getInt("rid");
                reponame = rs.getString("reponame");   
                pid = rs.getInt("pid");
                hasdb = rs.getInt("hasdb");
                mouseurl = rs.getString("mouseurl");
                repourl = rs.getString("repourl");
                dirty = false;
            } else
                throw new EJBException("RepositoriesBean#ejbLoad: Error loading Repository");
        } catch (Exception e) {
            throw new EJBException("RepositoriesBean#ejbLoad: error loading Repository. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    public void ejbStore() {
        if (dirty)
        {
            makeConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("update repositories set reponame=?,hasdb=?,mouseurl=?,repourl=? " +
                        "where rid=?");

                ps.setString(1, reponame);
                ps.setInt(2, hasdb);
                ps.setString(3, mouseurl);
                ps.setString(4, repourl);
                ps.setInt(5, rid);
                ps.execute();
            } catch (Exception e) {
                throw new EJBException("RepositoriesBean#ejbStore: error storing Repository. \n"+e.getMessage());
            } finally {
                releaseConnection();
                dirty = false;
            }
        }
    }
    
    // </editor-fold>
    
    //finder methods
    // <editor-fold defaultstate="collapsed">
    public Integer ejbFindByPrimaryKey(Integer key) throws FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select rid from repositories where rid = ?");
            ps.setInt(1,key.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("RepositoriesBean#ejbFindByPrimaryKey: Cannot find Repository. No next in resultset");
            }
        } catch (SQLException se) {
            throw new FinderException("RepositoriesBean#ejbFindByPrimaryKey: Cannot find Repository. \n"+se.getMessage());
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
            ps = conn.prepareStatement("select rid from repositories where pid = ? order by reponame");
            ps.setInt(1,pid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("rid")));
            }
        } catch (SQLException se) {
            throw new FinderException("RepositoriesBean#ejbFindByProject: Cannot find repositories by project "+pid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    public Collection ejbFindByDB() throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select rid from repositories where hasdb = 1 order by reponame");
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("rid")));
            }
        } catch (SQLException se) {
            throw new FinderException("RepositoriesBean#ejbFindByDB: Cannot find repositories that have a database \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    //</editor-fold>
    
    //get+set methods.
    // <editor-fold defaultstate="collapsed">

    public int getRid() {
        return rid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
        dirty = true;
    }

    public String getReponame() {
        return reponame;
    }

    public void setReponame(String reponame) {
        this.reponame = reponame;
        dirty = true;
    }
    
    public int getHasdb(){
        return hasdb;
    }
    
    public void setHasdb(int hasdb){
        this.hasdb = hasdb;
        dirty = true;
    }
    
    public String getMouseurl(){
        return mouseurl;
    }
    
    public void setMouseurl(String mouseurl){
        this.mouseurl = mouseurl;
        dirty = true;
    }
    
    public String getRepourl(){
        return repourl;
    }
    
    public void setRepourl(String repourl){
        this.repourl = repourl;
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
    // <editor-fold defaultstate="collapsed">
    
    public Integer ejbCreate(int rid, String reponame, int hasdb, String mouseurl, String repourl, ProjectRemote project) throws javax.ejb.CreateException {
        makeConnection();
        Integer pk = null;
        try {
            this.rid = rid;
            this.reponame = reponame;
            this.pid = project.getPid();
            
            pk = new Integer(rid);
            
            PreparedStatement ps = conn.prepareStatement("insert into repositories (rid, reponame, pid, hasdb, mouseurl, repourl) values (?,?,?,?,?,?)");
            ps.setInt(1, rid);
            ps.setString(2, reponame);
            ps.setInt(3, pid);
            ps.setInt(4, hasdb);
            ps.setString(5, mouseurl);
            ps.setString(6, repourl);
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("RepositoriesBean#ejbCreate: Unable to create Repository. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }

    public void ejbPostCreate(int rid, String reponame, int hasdb, String mouseurl, String repourl, ProjectRemote project) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }
    
    //</editor-fold>

    
    
    
}
