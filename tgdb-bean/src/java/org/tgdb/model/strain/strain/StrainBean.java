package org.tgdb.model.strain.strain;

import org.tgdb.TgDbCaller;
import org.tgdb.model.strain.state.StrainStateRemote;
import org.tgdb.model.strain.state.StrainStateRemoteHome;
import org.tgdb.model.strain.type.StrainTypeRemote;
import org.tgdb.model.strain.type.StrainTypeRemoteHome;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.servicelocator.ServiceLocator;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.*;
import org.tgdb.model.expmodel.ExpModelRemoteHome;
import org.tgdb.model.modelmanager.StrainLinkDTO;

public class StrainBean extends AbstractTgDbBean implements EntityBean, StrainRemoteBusiness
{
    private EntityContext context;
    
    private int strainid;
    private String designation;
    //private int mgiid;
//    private String mgiid;
    // Relational object
    private int pid;    // Project ID

    private Collection strain_links;
    
    private boolean dirty;
    
    private StrainTypeRemoteHome strainTypeHome;
    private StrainStateRemoteHome strainStateHome;
    private ExpModelRemoteHome modelHome;
//    private StrainAlleleRemoteHome strainAlleleHome;
    
    //ejb methods
    // <editor-fold defaultstate="collapsed">
    public void setEntityContext(EntityContext aContext){
        context = aContext;
        strainTypeHome = (StrainTypeRemoteHome)locator.getHome(ServiceLocator.Services.STRAIN_TYPE);
        strainStateHome = (StrainStateRemoteHome)locator.getHome(ServiceLocator.Services.STRAIN_STATE);
        modelHome = (ExpModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPMODEL);
//        strainAlleleHome = (StrainAlleleRemoteHome)locator.getHome(ServiceLocator.Services.STRAIN_ALLELE);
    }
    
    public void ejbActivate(){}
    
    public void ejbPassivate(){}
    
    public void ejbRemove(){
        makeConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("delete from strain where strainid=?");
            ps.setInt(1, strainid);
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("StrainBean#ejbRemove: Unable to delete strain. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    public void unsetEntityContext(){
        context = null;
    }
    
    public void ejbLoad(){
        makeConnection();
        Integer pk = (Integer)context.getPrimaryKey();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select strainid,designation, pid " +
                    "from strain where strainid=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                strainid = rs.getInt("strainid");
                designation = rs.getString("designation");
//                mgiid = rs.getString("mgiid");
                pid = rs.getInt("pid");
                dirty = false;
            } else
                throw new EJBException("StrainBean#ejbLoad: Error loading strain");
        } catch (Exception e) {
            throw new EJBException("StrainBean#ejbLoad: error loading strain. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    public void ejbStore(){
        if (dirty)
        {
            makeConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("update strain set designation=?, pid=? where strainid=?");

                ps.setString(1, designation);
//                ps.setString(2, mgiid);
                ps.setInt(2, pid);
                ps.setInt(3, strainid);

                ps.execute();
            } catch (Exception e) {
                throw new EJBException("StrainBean#ejbStore: error storing strain. \n"+e.getMessage());
            } finally {
                releaseConnection();
                dirty = false;
            }
        }
    }
    
    // </editor-fold>
    
    //finder methods
    // <editor-fold defaultstate="collapsed">
    public Integer ejbFindByNAME(String name) throws FinderException{
        Integer aKey = null;
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select strainid from strain where designation = ? limit 1");
            ps.setString(1,name);
            result = ps.executeQuery();

            if (!result.next()) {
                throw new ObjectNotFoundException("StrainBean#ejbFindByPrimaryKey: Cannot find strain [id="+strainid+"]. No next in resultset");
            }
            else {
                aKey = new Integer(result.getInt("strainid"));
            }
        } catch (SQLException se) {
            throw new FinderException("StrainBean#ejbFindByPrimaryKey: Cannot find strain [id="+strainid+"]. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return aKey;
    }


    public Integer ejbFindByPrimaryKey(Integer aKey) throws FinderException{
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select strainid from strain where strainid = ?");
            ps.setInt(1,aKey.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("StrainBean#ejbFindByPrimaryKey: Cannot find strain [id="+strainid+"]. No next in resultset");
            }
        } catch (SQLException se) {
            throw new FinderException("StrainBean#ejbFindByPrimaryKey: Cannot find strain [id="+strainid+"]. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return aKey;
    }
    
    public Collection ejbFindByProject(int pid, TgDbCaller caller) throws javax.ejb.FinderException{
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = conn.prepareStatement("select strainid from strain where pid = ? order by strainid");
            ps.setInt(1,pid);

            
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("strainid")));
            }
        } catch (SQLException se) {
            throw new FinderException("StrainBean#ejbFindByProject: unable to find strains for project [id="+strainid+"]. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public Collection ejbFindByModel(int model, TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();

        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = conn.prepareStatement("select strain as strainid from r_model_strain where model = ?");
            ps.setInt(1,model);
            result = ps.executeQuery();

            while (result.next()) {
                arr.add(new Integer(result.getInt("strainid")));
            }
        } catch (SQLException se) {
            throw new FinderException("StrainBean#ejbFindByModel: unable to find strains for model "+model+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public Collection ejbFindConnectedToModels(TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();

        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = conn.prepareStatement("select distinct(strain) as strainid from r_model_strain");
            result = ps.executeQuery();

            while (result.next()) {
                arr.add(new Integer(result.getInt("strainid")));
            }
        } catch (SQLException se) {
            throw new FinderException("StrainBean#ejbFindByModel: unable to find strains connected to models \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public Collection ejbFindUnassigned(int model, TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();

        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = conn.prepareStatement("select strainid from strain where strainid not in (select strain from r_model_strain where model = ?) and designation not like '' order by designation");
            ps.setInt(1,model);
            result = ps.executeQuery();

            while (result.next()) {
                arr.add(new Integer(result.getInt("strainid")));
            }
        } catch (SQLException se) {
            throw new FinderException("StrainBean#ejbFindUnassigned: unable to find unassigned strains for model "+model+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
//    public Collection ejbFindByMgiid(String mgiid, TgDbCaller caller) throws javax.ejb.FinderException{
//        makeConnection();
//        this.caller = caller;
//        Collection arr = new ArrayList();
//
//        PreparedStatement ps = null;
//        ResultSet result = null;
//        try {
//
//            ps = conn.prepareStatement("select strainid from strain where mgiid like ?");
//            ps.setString(1,mgiid);
//
//            result = ps.executeQuery();
//
//            while (result.next()) {
//                arr.add(new Integer(result.getInt("strainid")));
//            }
//        } catch (SQLException se) {
//            throw new FinderException("StrainBean#ejbFindByMgiid: unable to find strains by mgiid. \n"+se.getMessage());
//        } finally {
//            releaseConnection();
//        }
//        return arr;
//    }
    //</editor-fold>

    
    //setter+getter methods
    // <editor-fold defaultstate="collapsed">
    public int getStrainid(){
        return strainid;
    }

    public String getDesignation(){
        return designation;
    }

    public void setDesignation(String designation){
        this.designation = designation;
        dirty = true;
    }
    
//    public String getMgiId(){
//        return mgiid;
//    }
//
//    public void setMgiId(String mgiid){
//        this.mgiid = mgiid;
//        dirty = true;
//    }

    public int getModels(){
        int models = 0;
        try {
            models = modelHome.findByStrain(strainid,caller).size();
        }
        catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return models;
    }
    
    public Collection getTypes(){
        Collection arr = null;
        try {
            arr = strainTypeHome.findByStrain(strainid,caller);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }
    
    public Collection getStates(){
        Collection arr = null;
        try {
            arr = strainStateHome.findByStrain(strainid,caller);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return arr;
    }

    public void insertStrain_link(String repository, String externalid, String strainurl) {
        this.strain_links = new ArrayList();

        try {
            makeConnection();
            int id = getIIdGenerator().getNextId(conn, "strain_link_seq");
            PreparedStatement ps = conn.prepareStatement("insert into strain_links (id, strainid,repository,externalid,strainurl) values (?,?,?,?,?) ");
            ps.setInt(1, id);
            ps.setInt(2, strainid);
            ps.setString(3, repository);
            ps.setString(4, externalid);
            ps.setString(5, strainurl);

            ps.execute();
        } catch (Exception se) {
            logger.error(se.getMessage());
        } finally {
            releaseConnection();
        }
    }

    public void deleteStrain_link(int id) {
        this.strain_links = new ArrayList();

        try {
            makeConnection();
//            int id = getIIdGenerator().getNextId(conn, "strain_link_seq");
            PreparedStatement ps = conn.prepareStatement("delete from strain_links where id = ?");
            ps.setInt(1, id);

            ps.execute();
        } catch (Exception se) {
            logger.error(se.getMessage());
        } finally {
            releaseConnection();
        }
    }

    public Collection getStrain_links() {
        this.strain_links = new ArrayList();
        
        try {
            makeConnection();
            PreparedStatement ps = conn.prepareStatement("select id, repository,externalid,strainurl from strain_links where strainid = ?");
            ps.setInt(1,strainid);

            ResultSet result = ps.executeQuery();

            while (result.next()) {
                strain_links.add(new StrainLinkDTO(result.getInt("id"), this.strainid, result.getString("repository"), result.getString("externalid"), result.getString("strainurl")));
            }
        } catch (Exception se) {
            logger.error(se.getMessage());
        } finally {
            releaseConnection();
        }
        return this.strain_links;
    }

    public String getStrain_links_string() {
        String strain_link_string = "";

        try {
            makeConnection();
            PreparedStatement ps = conn.prepareStatement("select repository,externalid,strainurl from strain_links where strainid = ?");
            ps.setInt(1,strainid);

            ResultSet result = ps.executeQuery();

            while (result.next()) {
//                strain_links.add(new StrainLinkDTO(this.strainid, result.getString("repository"), result.getString("externalid"), result.getString("strainurl")));

                String strain_url = "#";
                if(result.getString("strainurl") != null && result.getString("strainurl").trim().length() > 0) strain_url = result.getString("strainurl");

                strain_link_string += "&nbsp;<a href='" + strain_url +"' title = '" + result.getString("repository") + " ID' target='_blank'>" + result.getString("repository") + " ID: " + result.getString("externalid") + "</a>&nbsp;";
            }
        } catch (Exception se) {
            logger.error(se.getMessage());
        } finally {
            releaseConnection();
        }
        return strain_link_string;
    }
    
//    public Collection getStrainAlleles(){
//        Collection arr = new ArrayList();
//        try
//        {
//            //FIXME!!! - This is not valid anymore!!! Allele will be accessible via model only.
//            arr = strainAlleleHome.findByStrain(strainid, caller);
//        }
//        catch (Exception e)
//        {
//            throw new EJBException("StrainBean#getStrainAlleles: Unable to get strain alleles. \n"+e.getMessage());
//        }
//        return arr;
//    }
    
    //</editor-fold>
    
    //relational methods
    //<editor-fold defaultstate="collapsed">
    public void addType(StrainTypeRemote type) throws RemoteException{
        makeConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("insert into r_strain_strain_type (strainid,typeid) values (?,?) ");
            ps.setInt(1, strainid);
            ps.setInt(2, type.getId());
            
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("StrainBean#addType: Unable to add type "+type.getId()+" to strain "+strainid);
        } finally {
            releaseConnection();
        }
    }
    
    public void removeType(StrainTypeRemote type) throws RemoteException{
        makeConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("delete from r_strain_strain_type where strainid=? and typeid=? ");
            ps.setInt(1, strainid);
            ps.setInt(2, type.getId());
            
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("StrainBean#removeType: Unable to remove type "+type.getId()+" from strain "+strainid);
        } finally {
            releaseConnection();
        }
    }
    
    public void addState(StrainStateRemote state) throws RemoteException{
        makeConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("insert into r_strain_strain_state (strainid,stateid) values (?,?) ");
            ps.setInt(1, strainid);
            ps.setInt(2, state.getId());
            
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("StrainBean#addState: Unable to add type "+state.getId()+" to strain "+strainid);
        } finally {
            releaseConnection();
        }
    }
    
    public void removeState(StrainStateRemote state) throws RemoteException{
        makeConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("delete from r_strain_strain_state where strainid=? and stateid=? ");
            ps.setInt(1, strainid);
            ps.setInt(2, state.getId());
            
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("StrainBean#removeState: Unable to remove state "+state.getId()+" from strain "+strainid);
        } finally {
            releaseConnection();
        }
    }
    //</editor-fold>
    
    //create+postcreate methods
    //<editor-fold defaultstate="collapsed">
    public Integer ejbCreate(int strainid, String designation, TgDbCaller caller) throws javax.ejb.CreateException{
        makeConnection();
        Integer pk = null;
        try {
            
            this.caller = caller;
            
            this.strainid = strainid;
            this.designation = designation;
            this.pid = caller.getPid();
            
            pk = new Integer(strainid);
            
            PreparedStatement ps = conn.prepareStatement("insert into strain (strainid, designation, pid) values (?, ?, ?)");
            ps.setInt(1, strainid);
            ps.setString(2, designation);
            ps.setInt(3, pid);
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("StrainBean#ejbCreate: Unable to create strain alleles. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }

    public void ejbPostCreate(int strainid, String designation, TgDbCaller caller) throws javax.ejb.CreateException{}
    //</editor-fold>
    
}
