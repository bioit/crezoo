package org.tgdb.genome.integrationcopy;

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
import org.tgdb.species.chromosome.ChromosomeRemote;
import org.tgdb.species.chromosome.ChromosomeRemoteHome;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;

public class IntegrationCopyBean extends AbstractTgDbBean implements javax.ejb.EntityBean, IntegrationCopyRemoteBusiness {
    private javax.ejb.EntityContext context;
    
    private int iscmid;
    private String isite, cnumber;
    
    private boolean dirty;
    private UserRemoteHome userHome;
    private ExpModelRemoteHome modelHome;
    private ProjectRemoteHome projectHome;
    private ChromosomeRemoteHome chromosomeHome;
    
    //ejb methods
    // <editor-fold>
    
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
        modelHome = (ExpModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPMODEL);
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);
        chromosomeHome = (ChromosomeRemoteHome)locator.getHome(ServiceLocator.Services.CHROMOSOME);
    }
    
    public void ejbActivate() {
        
    }
    
    public void ejbPassivate() {
        
    }
    
    public void ejbRemove() {
        makeConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("delete from is_cm where iscmid=?");
            ps.setInt(1, iscmid);
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("IntegrationCopyBean#ejbRemove: Unable to delete integration copy data set.\n"+e.getMessage());
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
            
            ps = conn.prepareStatement("select iscmid, isite, cnumber from is_cm where iscmid=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                iscmid = rs.getInt("iscmid");
                isite = rs.getString("isite");
                cnumber = rs.getString("cnumber");
                dirty = false;
            } else
                throw new EJBException("IntegrationCopyBean#ejbLoad: Error loading integration copy data set");
        } catch (Exception e) {
            throw new EJBException("IntegrationCopyBean#ejbLoad: error loading integration copy data set. \n"+e.getMessage());
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
                
                ps = conn.prepareStatement("update is_cm set isite=?,cnumber=? where iscmid=?");

                ps.setString(1, isite);
                ps.setString(2, cnumber);
                ps.setInt(3, iscmid);
               
                ps.execute();
            } catch (Exception e) {
                throw new EJBException("IntegrationCopyBean#ejbStore: error storing integration copy data set. \n"+e.getMessage());
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
            ps = conn.prepareStatement("select iscmid from is_cm where iscmid = ?");
            ps.setInt(1,key.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("IntegrationCopyBean#ejbFindByPrimaryKey: Cannot find integration copy data set. No next in resultset");
            }
        } catch (SQLException se) {
            throw new FinderException("IntegrationCopyBean#ejbFindByPrimaryKey: Cannot find integration copy data set. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return key;
    }
    
    public java.util.Collection ejbFindByModel(int eid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select g.iscmid from is_cm g, is_cm_model_r r where g.iscmid=r.iscmid and r.eid = ?");
            ps.setInt(1,eid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("iscmid")));
            }
        } catch (SQLException se) {
            throw new FinderException("IntegrationCopyBean#ejbFindByModel: Cannot find integration copy data set by model "+eid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    //</editor-fold>
    
    //setter+getter methods
    //<editor-fold>
    public int getIscnid() {
        return iscmid;
    }

    public String getIsite() {
        return isite;
    }

    public void setIsite(String isite) {
        this.isite = isite;
        dirty = true;
    }
    
    public String getCnumber() {
        return cnumber;
    }

    public void setCnumber(String cnumber) {
        this.cnumber = cnumber;
        dirty = true;
    }
    
    //</editor-fold>
    
    //create+postcreate methods
    //<editor-fold>
    public java.lang.Integer ejbCreate(int iscmid, java.lang.String isite, java.lang.String cnumber) throws javax.ejb.CreateException {
        makeConnection();
        Integer pk = null;
        try {
            
            this.iscmid = iscmid;
            this.isite = isite;
            this.cnumber = cnumber;
            
            pk = new Integer(iscmid);
            
            PreparedStatement ps = conn.prepareStatement("insert into is_cm (iscmid,isite,cnumber) values (?,?,?)");
            ps.setInt(1, iscmid);
            ps.setString(2, isite);
            ps.setString(3, cnumber);
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("IntegrationCopyBean#ejbCreate: Unable to create integration copy data set. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }
    
    public void ejbPostCreate(int iscmid, java.lang.String isite, java.lang.String cnumber) throws javax.ejb.CreateException {}

    //</editor-fold>
}
