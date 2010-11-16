package org.tgdb.species.gene;

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
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import org.tgdb.model.modelmanager.PromoterLinkDTO;

public class GeneBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.species.gene.GeneRemoteBusiness {
    private javax.ejb.EntityContext context;
    
    private int gaid, id, pid, cid;
    private String name, comm, mgiid, genesymbol, geneexpress, idgene, idensembl, driver_note, molecular_note, molecular_note_link, common_name, distinguish;
    private java.sql.Date ts;
    
    private boolean dirty;
//    private UserRemoteHome userHome;
    private ExpModelRemoteHome model_home;
    private ProjectRemoteHome project_home;
    private ChromosomeRemoteHome chromosome_home;
    
    private TgDbCaller _caller;
    
    //ejb methods
    // <editor-fold defaultstate="collapsed">
    
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
        model_home = (ExpModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPMODEL);
        project_home = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);
        chromosome_home = (ChromosomeRemoteHome)locator.getHome(ServiceLocator.Services.CHROMOSOME);
    }
    
    public void ejbActivate() {}
    
    public void ejbPassivate() {}
    
    public void ejbRemove() {
        makeConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("delete from gene where gaid=?");
            ps.setInt(1, gaid);
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("GeneBean#ejbRemove: Unable to delete Gene.\n"+e.getMessage());
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
            
            ps = conn.prepareStatement("select gaid, pid, name,comm, id, ts, mgiid, genesymbol, geneexpress, idgene, idensembl, cid, driver_note, molecular_note, molecular_note_link, common_name, distinguish " +
                    "from gene where gaid=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                gaid = rs.getInt("gaid");
                pid = rs.getInt("pid");
                name = rs.getString("name");
                comm = rs.getString("comm");
                id = rs.getInt("id");
                ts = rs.getDate("ts");
                mgiid = rs.getString("mgiid");
                genesymbol = rs.getString("genesymbol");
                geneexpress = rs.getString("geneexpress");
                idgene = rs.getString("idgene");
                idensembl = rs.getString("idensembl");
                cid = rs.getInt("cid");
                driver_note = rs.getString("driver_note");
                molecular_note = rs.getString("molecular_note");
                molecular_note_link = rs.getString("molecular_note_link");
                common_name = rs.getString("common_name");
                distinguish = rs.getString("distinguish");
                dirty = false;
            } else
                throw new EJBException("GeneBean#ejbLoad: Error loading Gene");
        } catch (Exception e) {
            throw new EJBException("GeneBean#ejbLoad: error loading Gene. \n"+e.getMessage());
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
                
                ps = conn.prepareStatement("update gene set name=?,comm=?,id=?, ts=?, mgiid=?, genesymbol=?, geneexpress=?, idgene=?, idensembl=?, cid=?, driver_note=?, molecular_note=?, molecular_note_link=?, common_name=?, distinguish=? " +
                        "where gaid=?");

                ps.setString(1, name);
                ps.setString(2, comm);
                ps.setInt(3, id);
                ps.setDate(4, new Date(System.currentTimeMillis()));
                //ps.setInt(5, gaid);
                ps.setString(5, mgiid);
                ps.setString(6, genesymbol);
                ps.setString(7, geneexpress);
                ps.setString(8, idgene);
                ps.setString(9, idensembl);
                ps.setInt(10, cid);
                ps.setString(11, driver_note);
                ps.setString(12, molecular_note);
                ps.setString(13, molecular_note_link);
                ps.setString(14, common_name);
                ps.setString(15, distinguish);
                ps.setInt(16, gaid);
               
                ps.execute();
            } catch (Exception e) {
                throw new EJBException("GeneBean#ejbStore: error storing Gene. \n"+e.getMessage());
            } finally {
                releaseConnection();
                dirty = false;
            }
        }
    }
    
    // </editor-fold>
    
    //finder methods
    // <editor-fold defaultstate="collapsed">
    
    public java.lang.Integer ejbFindByPrimaryKey(java.lang.Integer key) throws javax.ejb.FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select gaid from gene where gaid = ?");
            ps.setInt(1,key.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("GeneBean#ejbFindByPrimaryKey: Cannot find Gene. No next in resultset");
            }
        } catch (SQLException se) {
            throw new FinderException("GeneBean#ejbFindByPrimaryKey: Cannot find Gene. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return key;
    }

    public java.lang.Integer ejbFindByNAME(String name) throws javax.ejb.FinderException {
        Integer key = null;
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select gaid from gene where name = ? limit 1");
            ps.setString(1,name);
            result = ps.executeQuery();

            if (!result.next()) {
                throw new ObjectNotFoundException("GeneBean#ejbFindByPrimaryKey: Cannot find Gene. No next in resultset");
            }
            else {
                key = new Integer(result.getInt("gaid"));
            }
        } catch (SQLException se) {
            throw new FinderException("GeneBean#ejbFindByPrimaryKey: Cannot find Gene. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return key;
    }
    
    public java.util.Collection ejbFindByMgiid(java.lang.String mgiid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select gaid from gene where mgiid like ?");
            ps.setString(1, mgiid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("gaid")));
            }
            
        } catch (SQLException se) {
            se.printStackTrace();
            throw new FinderException("GeneBean#ejbFindByMgiid: Cannot find gene by mgiid");
        } finally {
            releaseConnection();
        }

        return arr;
    }
    
    public java.util.Collection ejbFindBySymbol(java.lang.String symbol) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select gaid from gene where genesymbol like ?");
            ps.setString(1, symbol);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("gaid")));
            }
            
        } catch (SQLException se) {
            se.printStackTrace();
            throw new FinderException("GeneBean#ejbFindBySymbol: Cannot find gene by symbol");
        } finally {
            releaseConnection();
        }

        return arr;
    }
    
    public Collection ejbFindByNameCaseSensitive(java.lang.String name) throws javax.ejb.FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        int key = 0;
        Collection arr = new ArrayList();
        try {
            
            ps = conn.prepareStatement("select gaid from gene where name like ?");
            ps.setString(1, name);
            result = ps.executeQuery();
            
            while (result.next())
            {
                arr.add(new Integer(result.getInt("gaid")));
            }
        } catch (SQLException se) {
            throw new FinderException("GeneBean#ejbFindByNameCaseSensitive: Cannot find Gene. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    public java.util.Collection ejbFindByModel(int eid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select g.gaid from gene g, r_gene_model r where g.gaid=r.gaid and r.eid = ? order by g.name");
            ps.setInt(1,eid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("gaid")));
            }
        } catch (SQLException se) {
            throw new FinderException("GeneBean#ejbFindByModel: Cannot find gene by model "+eid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public java.util.Collection ejbFindByDistinguish(String distinguish) throws javax.ejb.FinderException {
        makeConnection();

        Collection arr = new ArrayList();

        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select g.gaid from gene g where g.distinguish = ? order by g.name");
            ps.setString(1,distinguish);
            result = ps.executeQuery();

            while (result.next()) {
                arr.add(new Integer(result.getInt("gaid")));
            }
        } catch (SQLException se) {
            logger.error(getStackTrace(se));
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public java.util.Collection ejbFindByModelAndDistinguish(int eid, String distinguish) throws javax.ejb.FinderException {
        makeConnection();

        Collection arr = new ArrayList();

        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select g.gaid from gene g, r_gene_model r where g.gaid=r.gaid and r.eid = ? and g.distinguish = ? order by g.name");
            ps.setInt(1,eid);
            ps.setString(2,distinguish);
            result = ps.executeQuery();

            while (result.next()) {
                arr.add(new Integer(result.getInt("gaid")));
            }
        } catch (SQLException se) {
            throw new FinderException("GeneBean#ejbFindByModelAndDistinguish: Cannot find gene by model "+eid+" and distinguish " + distinguish +" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    /*
     * this gets the gene that are assigned to an allele based on the model id.
     */
    public java.util.Collection ejbFindPromoters(int eid) throws javax.ejb.FinderException {
        makeConnection();
        Collection arr = new ArrayList();
        try {
            PreparedStatement ps = conn.prepareStatement("select distinct(gid) as gaid from r_model_strain_allele_mutation_type mst join r_strain_allele_gene sg on sg.aid = mst.strain_allele where mst.model = ?");
            ps.setInt(1,eid);
//            ps.setString(2,distinguish);
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                arr.add(new Integer(result.getInt("gaid")));
            }
        } catch (SQLException se) {
            throw new FinderException("GeneBean#ejbFindByModelAndDistinguish: Cannot find gene by model "+eid+" and distinguish " + distinguish +" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public java.util.Collection ejbFindByAllele(int aid) throws javax.ejb.FinderException {
        makeConnection();

        Collection arr = new ArrayList();

        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select g.gaid from gene g, r_strain_allele_gene r where g.gaid=r.gid and r.aid = ? order by g.name");
            ps.setInt(1,aid);
            result = ps.executeQuery();

            while (result.next()) {
                arr.add(new Integer(result.getInt("gaid")));
            }
        } catch (SQLException se) {
            throw new FinderException("GeneBean#ejbFindByAllele: Cannot find gene by allele "+aid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public java.util.Collection ejbFindGenesNotAssignedToAllele(int aid, String distinguish) throws javax.ejb.FinderException {
        makeConnection();

        Collection arr = new ArrayList();

        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select g.gaid from gene g where g.gaid not in (select gid from r_strain_allele_gene where aid = ?) and distinguish = ? order by g.name");
            ps.setInt(1,aid);
            ps.setString(2, distinguish);
            result = ps.executeQuery();

            while (result.next()) {
                arr.add(new Integer(result.getInt("gaid")));
            }
        } catch (SQLException se) {
            throw new FinderException("GeneBean#ejbFindGenesNotAssignedToAllele: Cannot find genes not assigned allele "+aid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    public java.util.Collection ejbFindUnassignedGenes(int eid, int pid, String distinguish) throws javax.ejb.FinderException {
        makeConnection();
        Collection arr = new ArrayList();
        try {
            if(exists(distinguish)) {
                distinguish = " and distinguish = '" + distinguish + "' ";
            }
            else {
                distinguish = "";
            }
            PreparedStatement ps = conn.prepareStatement("(select g.gaid from gene g where g.pid=? "+distinguish+") except (select g.gaid from gene g, r_gene_model r where r.eid=? and g.gaid=r.gaid)");
            ps.setInt(1,pid);
            ps.setInt(2,eid);
            ResultSet result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("gaid")));
            }
        } catch (SQLException se) {
            throw new FinderException("GeneBean#ejbFindByModel: Cannot find genes not assigned to model "+eid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public java.util.Collection ejbFindUnassignedGenesForTransgenic(int eid, int strainid, int pid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            String sql = "select g.gaid from gene g where g.pid=? "
                        +"and g.gaid not in (select g2.gaid from gene g2, r_gene_model r2 where r2.eid=? and g2.gaid=r2.gaid) "
                        +"and g.gaid not in (select gene from strain_allele where strainid=? and gene is not NULL)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,pid);
            ps.setInt(2,eid);
            ps.setInt(3,strainid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("gaid")));
            }
        } catch (SQLException se) {
            throw new FinderException("GeneBean#ejbFindUnassignedGenesForTransgenic: Cannot find genes not assigned to transgenic model "+eid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    public Collection ejbFindByName(java.lang.String name) throws javax.ejb.FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        int key = 0;
        Collection arr = new ArrayList();
        try {
            
            ps = conn.prepareStatement("select gaid from gene where lower(name) like ?");
            //ps = conn.prepareStatement("select gaid from gene where lower(name) like lower(?)");
            ps.setString(1, name);
            result = ps.executeQuery();
            
            while (result.next())
            {
                arr.add(new Integer(result.getInt("gaid")));
            }
        } catch (SQLException se) {
            throw new FinderException("GeneBean#ejbFindByName: Cannot find Gene. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public java.util.Collection ejbFindByKeyword(Keyword keyword) throws javax.ejb.FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        int key = 0;
        Collection arr = new ArrayList();
        try {
            
            ps = conn.prepareStatement("select gaid from gene where lower(name) like ? or lower(comm) like ? or lower(genesymbol) like ? or lower(geneexpress) like ?");
            
            String search = "%"+keyword.getKeyword()+"%";
            
            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);
            ps.setString(4, search);
            result = ps.executeQuery();
            
            while (result.next())
            {
                arr.add(new Integer(result.getInt("gaid")));
            }
        } catch (SQLException se) {
            throw new FinderException("GeneBean#ejbFindByName: Cannot find Gene. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public java.util.Collection ejbFindByProject(int pid, TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        
        //set the caller in the super class
        this._caller = caller;
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select gaid from gene where pid = ? order by name");
            ps.setInt(1,pid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("gaid")));
            }
        } catch (SQLException se) {
            throw new FinderException("GeneBean#ejbFindByProject: Cannot find gene by project "+pid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    //</editor-fold>
    
    //setter+getter methods
    // <editor-fold defaultstate="collapsed">

    public int getGaid() {
        return gaid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        dirty = true;
        id = _caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());         
    }

    public String getDriver_note() {
        return driver_note;
    }

    public void setDriver_note(String driver_note) {
        this.driver_note = driver_note;
        dirty = true;
        id = _caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());
    }

    public String getMolecular_note() {
        return molecular_note;
    }

    public void setMolecular_note(String molecular_note) {
        this.molecular_note = molecular_note;
        dirty = true;
        id = _caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());
    }

    public String getMolecular_note_link() {
        return molecular_note_link;
    }

    public void setMolecular_note_link(String molecular_note_link) {
        this.molecular_note_link = molecular_note_link;
        dirty = true;
        id = _caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());
    }

    public String getCommon_name() {
        return common_name;
    }

    public void setCommon_name(String common_name) {
        this.common_name = common_name;
        dirty = true;
        id = _caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());
    }

    public String getDistinguish() {
        return distinguish;
    }

    public void setDistinguish(String distinguish) {
        this.distinguish = distinguish;
        dirty = true;
        id = _caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis()); 
    }

    public String getComm() {
        return comm;
    }

    public void setComm(String comm) {
        this.comm = comm;
        dirty = true;
        id = _caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());         
    }
    
    public String getMgiid() {
        return mgiid;
    }

    public void setMgiid(String mgiid) {
        this.mgiid = mgiid;
        dirty = true;
        id = _caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());         
    }
    
    public String getGenesymbol() {
        return genesymbol;
    }

    public void setGenesymbol(String genesymbol) {
        this.genesymbol = genesymbol;
        dirty = true;
        id = _caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());         
    }
    
    public String getGeneexpress() {
        return geneexpress;
    }

    public void setGeneexpress(String geneexpress) {
        this.geneexpress = geneexpress;
        dirty = true;
        id = _caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());         
    }

    public String getIdgene() {
        return idgene;
    }

    public void setIdgene(String idgene) {
        this.idgene = idgene;
        dirty = true;
        id = _caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());         
    }
    
    public String getIdensembl() {
        return idensembl;
    }

    public void setIdensembl(String idensembl) {
        this.idensembl = idensembl;
        dirty = true;
        id = _caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());         
    }
    
    
    @Override
    public void setCaller(TgDbCaller caller) {
        this._caller = caller;
    }

    public Collection getModels() {
        Collection models = new ArrayList();
        try {
            if(distinguish.equalsIgnoreCase("promoter")) {
                models = model_home.findByPromoter(gaid, caller);
            }
            else {
                models = model_home.findByGene(gaid, _caller);
            }
        }
        catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return models;
    }
    
    public int getModelsNum() {
        try {
            return model_home.findByGene(gaid, _caller).size();
        } catch (Exception e) {
            e.printStackTrace();
            throw new EJBException("Could not get models");
        }
    }

    public Date getTs() {
        return ts;
    }

    public UserRemote getUser() {
        try
        {
            UserRemote usr = userHome.findByPrimaryKey(new Integer(id));
            return usr;
        }
        catch (Exception e)
        {
            throw new EJBException("Could not get user");
        }
    }
    
    public ProjectRemote getProject() throws ApplicationException{
        ProjectRemote prj = null;
        try {
            prj = project_home.findByPrimaryKey(new Integer(pid));
            return prj;
        } catch (Exception e) {
//            throw new ApplicationException("Could not get project");
        }
        return prj;
    }
    
    public ChromosomeRemote getChromosome(){
        try
        {
            return chromosome_home.findByPrimaryKey(new Integer(cid));
        }   
        catch (Exception e)
        {
            throw new EJBException("Failed to get chromosome",e);
        }
    }
    
    public void setChromosome(ChromosomeRemote chromosome){
        try
        {
            this.cid = chromosome.getCid();
            dirty = true;
        }
        catch (RemoteException ex)
        {
            ex.printStackTrace();
            throw new EJBException("Failed to set chromosome on gene", ex);
        }
    }
    
    //</editor-fold>
    
    //create+postcreate
    // <editor-fold defaultstate="collapsed">
    
    public java.lang.Integer ejbCreate(int gaid, java.lang.String name, String genesymbol, int cid, TgDbCaller caller) throws javax.ejb.CreateException {
        makeConnection();
        Integer pk = null;
        try {
            
            this.gaid = gaid;
            this.name = name;
//            this.comm = comm;
//            this.mgiid = mgiid;
            this.genesymbol = genesymbol;
//            this.geneexpress = geneexpress;
//            this.idgene = idgene;
//            this.idensembl = idensembl;
            this.cid = cid;//chr.getCid();
            
            
            this.id = caller.getId();
            this.ts = new Date(System.currentTimeMillis());
            
            this._caller = caller;
            
            this.pid = caller.getPid();//project.getPid();
            
            pk = new Integer(gaid);
            
            PreparedStatement ps = conn.prepareStatement("insert into gene (gaid, name, pid, id, ts, genesymbol, cid) values (?,?,?,?,?,?,?)");
            ps.setInt(1, gaid);
            ps.setString(2, name);
//            ps.setString(3, comm);
            ps.setInt(3, pid);
            ps.setInt(4, id);
            ps.setDate(5, ts);
//            ps.setString(7, mgiid);
            ps.setString(6, genesymbol);
//            ps.setString(9, geneexpress);
//            ps.setString(10, idgene);
//            ps.setString(11, idensembl);
            ps.setInt(7, cid);
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("GeneBean#ejbCreate: Unable to create Gene. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }

    public void ejbPostCreate(int gaid, java.lang.String name, String genesymbol, int cid, TgDbCaller caller) throws javax.ejb.CreateException {}

    //</editor-fold>
    
    public boolean isAssigned(int eid, String distinguish) {
        boolean to_return = true;
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select g.gaid from gene g, r_gene_model r where g.gaid=r.gaid and r.eid = ? and g.distinguish = ? order by g.name");
            ps.setInt(1,eid);
            ps.setString(2,distinguish);
            result = ps.executeQuery();

            if (!result.next()) to_return = false;
            
        } catch (Exception se) {
            logger.error(getStackTrace(se));
        } finally {
            releaseConnection();
        }
        return to_return;
    }

    //methods for promoter links
    public void insertPromoter_link(String repository, String externalid, String strainurl) {
        try {
            makeConnection();
            int id = getIIdGenerator().getNextId(conn, "promoter_link_seq");
            PreparedStatement ps = conn.prepareStatement("insert into promoter_links (id, pid,repository,externalid,strainurl) values (?,?,?,?,?) ");
            ps.setInt(1, id);
            ps.setInt(2, gaid);
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

    public void deletePromoter_link(int id) {
        try {
            makeConnection();
            PreparedStatement ps = conn.prepareStatement("delete from promoter_links where id = ?");
            ps.setInt(1, id);

            ps.execute();
        } catch (Exception se) {
            logger.error(se.getMessage());
        } finally {
            releaseConnection();
        }
    }

    public Collection getPromoter_links() {
        Collection promoter_links = new ArrayList();

        try {
            makeConnection();
            PreparedStatement ps = conn.prepareStatement("select id, repository,externalid,strainurl from promoter_links where pid = ?");
            ps.setInt(1,gaid);

            ResultSet result = ps.executeQuery();

            while (result.next()) {
                promoter_links.add(new PromoterLinkDTO(result.getInt("id"), gaid, result.getString("repository"), result.getString("externalid"), result.getString("strainurl")));
            }
        } catch (Exception se) {
            logger.error(se.getMessage());
        } finally {
            releaseConnection();
        }
        return promoter_links;
    }

    public String getPromoter_links_string() {
        String promoter_link_string = "";

        try {
            makeConnection();
            PreparedStatement ps = conn.prepareStatement("select repository,externalid,strainurl from promoter_links where pid = ?");
            ps.setInt(1,gaid);

            ResultSet result = ps.executeQuery();

            while (result.next()) {

                String strain_url = "#";
                if(result.getString("strainurl") != null && result.getString("strainurl").trim().length() > 0) strain_url = result.getString("strainurl");

                promoter_link_string += "&nbsp;<a href='" + strain_url +"' title = '" + result.getString("repository") + " ID' target='_blank'>" + result.getString("repository") + " ID: " + result.getString("externalid") + "</a>&nbsp;";
            }
        } catch (Exception se) {
            logger.error(se.getMessage());
        } finally {
            releaseConnection();
        }
        return promoter_link_string;
    }
}
