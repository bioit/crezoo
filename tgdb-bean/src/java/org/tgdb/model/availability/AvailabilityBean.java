package org.tgdb.model.availability;

import javax.ejb.*;
import org.tgdb.model.expmodel.ExpModelRemote;
import org.tgdb.model.expmodel.ExpModelRemoteHome;
import org.tgdb.model.availablegeneticbackgrounds.AvailableGeneticBackgroundRemote;
import org.tgdb.model.availablegeneticbackgrounds.AvailableGeneticBackgroundRemoteHome;
import org.tgdb.model.repositories.RepositoriesRemote;
import org.tgdb.model.repositories.RepositoriesRemoteHome;

import org.tgdb.model.strain.state.StrainStateRemote;
import org.tgdb.model.strain.state.StrainStateRemoteHome;
import org.tgdb.model.strain.type.StrainTypeRemote;
import org.tgdb.model.strain.type.StrainTypeRemoteHome;

import org.tgdb.project.AbstractTgDbBean;
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

public class AvailabilityBean extends AbstractTgDbBean implements EntityBean, AvailabilityRemoteBusiness {
    private EntityContext context;
    
    private int eid, rid, aid, stateid, typeid;
    
    private boolean dirty;
    private UserRemoteHome userHome;
    private ExpModelRemoteHome modelHome;
    private AvailableGeneticBackgroundRemoteHome avgenbackHome;
    private RepositoriesRemoteHome repoHome;
    private StrainStateRemoteHome stateHome;
    private StrainTypeRemoteHome typeHome;
    private ProjectRemoteHome projectHome;
    
    //ejb infrastructure methods
    //<editor-fold>
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(EntityContext aContext) {
        context = aContext;
        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
        modelHome = (ExpModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPMODEL);
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);
        repoHome = (RepositoriesRemoteHome)locator.getHome(ServiceLocator.Services.REPOSITORIES);
        avgenbackHome = (AvailableGeneticBackgroundRemoteHome)locator.getHome(ServiceLocator.Services.AVAILABLE_GENETIC_BACKGROUNDS);
        stateHome = (StrainStateRemoteHome)locator.getHome(ServiceLocator.Services.STRAIN_STATE);
        typeHome = (StrainTypeRemoteHome)locator.getHome(ServiceLocator.Services.STRAIN_TYPE);
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
        try
        {
            ps = conn.prepareStatement("delete from r_model_repositories_avgenback where eid = ? and rid=? and aid=? and stateid=? and typeid=?");
            ps.setInt(1, getEid());
            ps.setInt(2, getRid());
            ps.setInt(3, getAid());
            ps.setInt(4, getStateid());
            ps.setInt(5, getTypeid());
            
            //ps.execute();
            
            int res = ps.executeUpdate();
            
            if (res!=1)
            {
                throw new EJBException("AvailabilityBean#ejbRemove: Cannot remove Availability. Db returned error status -1");
            }
        }
        catch (SQLException se)
        {
            throw new EJBException("AvailabilityBean#ejbRemove: Cannot remove Availability. \n"+se.getMessage());
        }
        finally
        {
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
        AvailabilityPk pk = (AvailabilityPk)context.getPrimaryKey();
        
        eid = pk.getEid().intValue();
        rid = pk.getRid().intValue();
        aid = pk.getAid().intValue();
        stateid = pk.getStateid().intValue();
        typeid = pk.getTypeid().intValue();
        dirty = false;
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() {
        // TODO add code to persist data
    }
    
    // </editor-fold>
    
    //finder methods
    //<editor-fold>
    public AvailabilityPk ejbFindByPrimaryKey(AvailabilityPk pk) throws FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try
        {
            ps = conn.prepareStatement("select eid from r_model_repositories_avgenback where eid = ? and rid=? and aid=? and stateid=? and typeid=?");
            ps.setInt(1, pk.getEid().intValue());
            ps.setInt(2, pk.getRid().intValue());
            ps.setInt(3, pk.getAid().intValue());
            ps.setInt(4, pk.getStateid().intValue());
            ps.setInt(5, pk.getTypeid().intValue());
            
            result = ps.executeQuery();
            
            if (!result.next())
            {
                throw new ObjectNotFoundException("AvailabilityBean#ejbFindByPrimaryKey: Cannot find Availability");
            }
        }
        catch (SQLException se)
        {
            throw new FinderException("AvailabilityBean#ejbRemove: Cannot find Availability. \n"+se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return pk;
    }
    
    public Collection ejbFindByModel(int eid) throws javax.ejb.FinderException {
        Collection arr = new ArrayList();
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            ps = conn.prepareStatement("select rid, aid, stateid, typeid from r_model_repositories_avgenback where eid = ?");
            ps.setInt(1, eid);
            
            int rid = 0;
            int aid = 0;
            int stateid = 0;
            int typeid = 0;
            
            AvailabilityPk pk = null;
            
            rs = ps.executeQuery();
            while (rs.next())
            {
                rid = rs.getInt("rid");
                aid = rs.getInt("aid");
                stateid = rs.getInt("stateid");
                typeid = rs.getInt("typeid");
                pk = new AvailabilityPk(eid, rid, aid, stateid, typeid);
                arr.add(pk);
            }
        }
        catch (Exception se)
        {
            throw new FinderException("AvailabilityBean#ejbFindByModel: Cannot find Availability by model. \n" + se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return arr;
    }
    
    public Collection ejbFindByRepository(int rid) throws javax.ejb.FinderException {
        Collection arr = new ArrayList();
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            ps = conn.prepareStatement("select eid, aid, stateid, typeid from r_model_repositories_avgenback where rid = ?");
            ps.setInt(1, rid);
            
            int eid = 0;
            int aid = 0;
            int stateid = 0;
            int typeid = 0;
            
            AvailabilityPk pk = null;
            
            rs = ps.executeQuery();
            while (rs.next())
            {
                eid = rs.getInt("eid");
                aid = rs.getInt("aid");
                stateid = rs.getInt("stateid");
                typeid = rs.getInt("typeid");
                pk = new AvailabilityPk(eid, rid, aid, stateid, typeid);
                arr.add(pk);
            }
        }
        catch (Exception se)
        {
            throw new FinderException("AvailabilityBean#ejbFindByProject: Cannot find Availability by repository. \n" + se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return arr;
    }
    
    //</editor-fold>

    //set+get methods
    //<editor-fold>
    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
        dirty = true;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
        dirty = true;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
        dirty = true;
    }
    
    public int getStateid() {
        return stateid;
    }

    public void setStateid(int stateid) {
        this.stateid = stateid;
        dirty = true;
    }

    public int getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
        dirty = true;
    }
    
    public ExpModelRemote getModel() {
        ExpModelRemote model = null;
        try
        {
            model = modelHome.findByPrimaryKey(new Integer(eid));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return model;
    }
    
    public RepositoriesRemote getRepository() {
        RepositoriesRemote repository = null;
        try
        {
            repository = repoHome.findByPrimaryKey(new Integer(rid));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return repository;
    }
    
    public AvailableGeneticBackgroundRemote getAvailableGeneticBackground() {
        AvailableGeneticBackgroundRemote avgenback = null;
        try
        {
            avgenback = avgenbackHome.findByPrimaryKey(new Integer(aid));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return avgenback;
    }
    
    public StrainStateRemote getState() {
        StrainStateRemote state = null;
        try
        {
            state = stateHome.findByPrimaryKey(new Integer(stateid));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return state;
    }
    
    public StrainTypeRemote getType() {
        StrainTypeRemote type = null;
        try
        {
            type = typeHome.findByPrimaryKey(new Integer(typeid));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return type;
    }
    
    public String getRepositoryName() {
        String reponame = "";
        try
        {
           RepositoriesRemote repository = repoHome.findByPrimaryKey(new Integer(rid));
           reponame = repository.getReponame();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return reponame;
    }
    
    public String getAvailableGeneticBackgroundName() {
        String avgenbackname = "";
        try
        {
            AvailableGeneticBackgroundRemote avgenback = avgenbackHome.findByPrimaryKey(new Integer(aid));
            avgenbackname = avgenback.getAvbackname();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return avgenbackname;
    }
    
    public String getStateName() {
        String statename = "";
        try
        {
           StrainStateRemote state = stateHome.findByPrimaryKey(new Integer(stateid));
           statename = state.getName();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return statename;
    }
    
    public String getStateAbbr() {
        String stateabbr = "";
        try
        {
           StrainStateRemote state = stateHome.findByPrimaryKey(new Integer(stateid));
           stateabbr = state.getAbbreviation();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return stateabbr;
    }
    
    public String getTypeName() {
        String typename = "";
        try
        {
           StrainTypeRemote type = typeHome.findByPrimaryKey(new Integer(typeid));
           typename = type.getName();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return typename;
    }
    
    public String getTypeAbbr() {
        String typeabbr = "";
        try
        {
           StrainTypeRemote type = typeHome.findByPrimaryKey(new Integer(typeid));
           typeabbr = type.getAbbreviation();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return typeabbr;
    }
    
    //</editor-fold>
    
    //create+postcreate methods
    //<editor-fold>
    public AvailabilityPk ejbCreate(org.tgdb.model.expmodel.ExpModelRemote model, org.tgdb.model.repositories.RepositoriesRemote repository, org.tgdb.model.availablegeneticbackgrounds.AvailableGeneticBackgroundRemote avgenback, org.tgdb.model.strain.state.StrainStateRemote state, org.tgdb.model.strain.type.StrainTypeRemote type) throws javax.ejb.CreateException {
        makeConnection();
        PreparedStatement ps = null;
        AvailabilityPk pk = null;
        try
        {
            setEid(model.getEid());
            setRid(repository.getRid());
            setAid(avgenback.getAid());
            setStateid(state.getId());
            setTypeid(type.getId());
            
            ps = conn.prepareStatement("insert into r_model_repositories_avgenback (eid,rid,aid,stateid,typeid) values (?,?,?,?,?)");
            ps.setInt(1, model.getEid());
            ps.setInt(2, repository.getRid());
            ps.setInt(3, avgenback.getAid());
            ps.setInt(4, state.getId());
            ps.setInt(5, type.getId());
            ps.execute();
            
            pk = new AvailabilityPk(model.getEid(),
                repository.getRid(),
                avgenback.getAid(), state.getId(), type.getId());
            dirty = false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new CreateException("AvailabilityBean#ejbCreate: Cannot create Availability. \n"+e.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return pk;
    }

    public void ejbPostCreate(org.tgdb.model.expmodel.ExpModelRemote model, org.tgdb.model.repositories.RepositoriesRemote repository, org.tgdb.model.availablegeneticbackgrounds.AvailableGeneticBackgroundRemote avgenback, org.tgdb.model.strain.state.StrainStateRemote state, org.tgdb.model.strain.type.StrainTypeRemote type) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }
    
    //</editor-fold>

    
}
