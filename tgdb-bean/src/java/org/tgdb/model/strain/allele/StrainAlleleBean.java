package org.tgdb.model.strain.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.model.strain.mutationtype.MutationTypeRemote;
import org.tgdb.model.strain.mutationtype.MutationTypeRemoteHome;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.servicelocator.ServiceLocator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.*;
import org.tgdb.form.FormDataManager;
import org.tgdb.model.expmodel.ExpModelRemote;
import org.tgdb.model.expmodel.ExpModelRemoteHome;
import org.tgdb.species.gene.GeneRemote;
import org.tgdb.species.gene.GeneRemoteHome;

public class StrainAlleleBean extends AbstractTgDbBean implements EntityBean, StrainAlleleRemoteBusiness
{
    private EntityContext context;
    
    private int id;
    private String mgiid, mgi_url,symbol,name,attributes,made_by,origin_strain;
    
    private boolean dirty;
    
    private MutationTypeRemoteHome mutationTypeHome;
    private ExpModelRemoteHome modelHome;
    private GeneRemoteHome geneHome;
    
    //ejb methods
    // <editor-fold defaultstate="collapsed">
    
    public void setEntityContext(EntityContext aContext){
        context = aContext;
        mutationTypeHome = (MutationTypeRemoteHome)locator.getHome(ServiceLocator.Services.MUTATION_TYPE);
        modelHome = (ExpModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPMODEL); 
        geneHome = (GeneRemoteHome)locator.getHome(ServiceLocator.Services.GENE);
    }
    
    public void ejbActivate(){}
    
    public void ejbPassivate(){}
    
    public void ejbRemove(){
        makeConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("delete from strain_allele where id=?");
            ps.setInt(1, id);
            ps.execute();
        }
        catch (Exception e) {
            throw new EJBException("StrainAlleleBean#ejbRemove: Unable to delete strain allele. \n"+e.getMessage());
        }
        finally {
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
            ps = conn.prepareStatement("select id,mgiid,name,symbol,made_by,origin_strain,mgi_url from strain_allele where id=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                id = rs.getInt("id");
                mgiid = rs.getString("mgiid");
                name = rs.getString("name");
                symbol = rs.getString("symbol");
//                gene = rs.getInt("gene");
//                strainid = rs.getInt("strainid");
//                attributes = rs.getString("attributes");
                made_by = rs.getString("made_by");
                origin_strain = rs.getString("origin_strain");
                mgi_url = rs.getString("mgi_url");
                dirty = false;
            }
            else
                throw new EJBException("StrainAlleleBean#ejbLoad: Error loading strain allele");
        }
        catch (Exception e) {
            throw new EJBException("StrainAlleleBean#ejbLoad: error loading strain allele. \n"+e.getMessage());
        }
        finally {
            releaseConnection();
        }
    }
    
    public void ejbStore(){
        if (dirty)
        {
            makeConnection();
            PreparedStatement ps = null;
            try
            {
                ps = conn.prepareStatement("update strain_allele set name=?,symbol=?,mgiid=?,made_by=?,origin_strain=?,mgi_url=? where id=?");
                
                ps.setString(1, name);
                ps.setString(2, symbol);
                ps.setString(3, mgiid);
//                if (gene!=0)
//                    ps.setInt(4, gene);
//                else
//                    ps.setNull(4, java.sql.Types.INTEGER);
//                ps.setString(5, attributes);
                ps.setString(4, made_by);
                ps.setString(5, origin_strain);
                ps.setString(6, mgi_url);
                ps.setInt(7, id);
                
                ps.execute();
            }
            catch (Exception e)
            {
                throw new EJBException("StrainAlleleBean#ejbStore: error storing strain allele. \n"+e.getMessage());
            }
            finally
            {
                releaseConnection();
                dirty = false;
            }
        }
    }
    
    // </editor-fold>
    
    //finder methods
    // <editor-fold defaultstate="collapsed">
    
    public Integer ejbFindByPrimaryKey(Integer aKey) throws FinderException{
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select id from strain_allele where id = ?");
            ps.setInt(1,aKey.intValue());
            result = ps.executeQuery();
            
            if (!result.next())
            {
                throw new ObjectNotFoundException("StrainAlleleBean#ejbFindByPrimaryKey: Cannot find strain allele. No next in resultset");
            }
        }
        catch (SQLException se) {
            throw new FinderException("StrainAlleleBean#ejbFindByPrimaryKey: Cannot find strain allele. \n"+se.getMessage());
        }
        finally {
            releaseConnection();
        }
        return aKey;
    }

    public Integer ejbFindByMGI(String mgiid) throws FinderException{
        Integer key = null;
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select id from strain_allele where mgiid = ? limit 1");
            ps.setString(1, mgiid);
            result = ps.executeQuery();

            if (!result.next()) {
                throw new ObjectNotFoundException("StrainAlleleBean#ejbFindByPrimaryKey: Cannot find strain allele. No next in resultset");
            }
            else {
                key = new Integer(result.getInt("id"));
            }
        }
        catch (SQLException se) {
            throw new FinderException("StrainAlleleBean#ejbFindByPrimaryKey: Cannot find strain allele. \n"+se.getMessage());
        }
        finally {
            releaseConnection();
        }
        return key;
    }

    /*
     * actually its by symbol and not name. FIXME!!! - Must rename this
     */
    public Integer ejbFindByNAME(String name) throws FinderException{
        Integer key = null;
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select id from strain_allele where symbol = ? limit 1");
            ps.setString(1, name);
            result = ps.executeQuery();

            if (!result.next()) {
                throw new ObjectNotFoundException("StrainAlleleBean#ejbFindByPrimaryKey: Cannot find strain allele. No next in resultset");
            }
            else {
                key = new Integer(result.getInt("id"));
            }
        }
        catch (SQLException se) {
            throw new FinderException("StrainAlleleBean#ejbFindByPrimaryKey: Cannot find strain allele. \n"+se.getMessage());
        }
        finally {
            releaseConnection();
        }
        return key;
    }
    
    public Collection ejbFindByStrain(int eid, TgDbCaller caller) throws javax.ejb.FinderException{
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            //FIXME!!! - Just remove the commented out line once everything is completed and rename method
//            ps = conn.prepareStatement("select id from strain_allele where strainid = ? order by id");
//            ps = conn.prepareStatement("select distinct(strain_allele) as id from r_strain_strain_allele_mutation_type where strain = ? order by id");
            ps = conn.prepareStatement("select distinct(strain_allele) as id from r_model_strain_allele_mutation_type where model = ? order by id");
            ps.setInt(1,eid);

            
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("id")));
            }
        } catch (SQLException se) {
            throw new FinderException("StrainAlleleBean#ejbFindByStrain: unable to find Strain Alleles. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    public Collection ejbFindByMgiid(String mgiid, TgDbCaller caller) throws javax.ejb.FinderException{
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = conn.prepareStatement("select id from strain_allele where mgiid like ? ");
            ps.setString(1,mgiid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("id")));
            }
        } catch (SQLException se) {
            throw new FinderException("StrainAlleleBean#ejbFindByMgiid: unable to find strain alleles by mgiid. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    public Collection ejbFindByName(String name, TgDbCaller caller) throws javax.ejb.FinderException{
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = conn.prepareStatement("select id from strain_allele where name like ? ");
            ps.setString(1,name);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("id")));
            }
        } catch (SQLException se) {
            throw new FinderException("StrainAlleleBean#ejbFindByName: unable to find strain alleles by name. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    public Collection ejbFindBySymbol(String symbol, TgDbCaller caller) throws javax.ejb.FinderException{
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = conn.prepareStatement("select id from strain_allele where symbol like ? ");
            ps.setString(1,symbol);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("id")));
            }
        } catch (SQLException se) {
            throw new FinderException("StrainAlleleBean#ejbFindBySymbol: unable to find strain alleles by symbol. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public Collection ejbFindUnassignedAlleles(int model, TgDbCaller caller) throws javax.ejb.FinderException{
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();

        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select id from strain_allele where id not in (select strain_allele from r_model_strain_allele_mutation_type where model = ?) order by symbol");
            ps.setInt(1, model);
            result = ps.executeQuery();

            while (result.next()) {
                arr.add(new Integer(result.getInt("id")));
            }
        } catch (SQLException se) {
            throw new FinderException("StrainAlleleBean#ejbFindUnassignedAlleles: unable to find strain alleles not assigned to model " + model+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public Collection ejbFindAll(TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();

        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select id from strain_allele");
            result = ps.executeQuery();

            while (result.next()) {
                arr.add(new Integer(result.getInt("id")));
            }
        } catch (SQLException se) {
            logger.error(getStackTrace(se));
//            throw new FinderException("StrainAlleleBean#ejbFindUnassignedAlleles: unable to find strain alleles not assigned to model " + model+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    public java.util.Collection ejbFindByFDM(FormDataManager fdm, org.tgdb.TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        
        //sets the _caller
        setCaller(caller);
        
        Collection strain_alleles = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        
        int promoter = 0;
        String tmp = fdm.getValue("promoter");
        if (tmp!=null && !tmp.equals("") && !tmp.equals("*"))
            promoter = new Integer(tmp).intValue();
        
        String made_by ="";
        tmp = fdm.getValue("made_by");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            made_by = tmp;
        
        String inducible ="";
        tmp = fdm.getValue("inducible");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            inducible = tmp;
        
        String orderby ="";
        tmp = fdm.getValue("ordertype");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            orderby = tmp;
        
        String sql = "";
        
        try {
            sql = "select sa.id from strain_allele sa where sa.id > 0 ";
            
            if (promoter!=0) {
                sql += " and sa.id in (select aid from r_strain_allele_gene where gid = ?) ";
            }
            
            if (!made_by.equals("")) {
                sql += " and sa.made_by = ? ";
            }
            
            if (!inducible.equals("")) {
                sql += " and sa.id in (select distinct(rms.strain_allele) as said from model m join r_model_strain_allele_mutation_type rms on m.eid = rms.model where m.inducible = ?) ";
            }
            
            if (!orderby.equals("")) {
                if(orderby.equals("NAME")){
                    sql += " order by sa.name";
                }
                
                if(orderby.equals("SYMBOL")){
                    sql += " order by sa.symbol";
                }
                
                if(orderby.equals("ID")){
                    sql += " order by sa.id";
                }
                
            }
            else {
                //default by symbol
                sql += " order by sa.symbol";
            }
                    
            ps = conn.prepareStatement(sql);
            
            int i = 1;
            
            if (promoter!=0)
                ps.setInt(i++, promoter);
            if (!made_by.equals(""))
                ps.setString(i++, made_by);
            if (!inducible.equals(""))
                ps.setString(i++, inducible);
            
            result = ps.executeQuery();
            
            while(result.next()) {
                strain_alleles.add(new Integer(result.getInt("id")));
            }
        } catch (SQLException se) {
            logger.error(getStackTrace(se));
        } finally {
            releaseConnection();
        }
        
        return strain_alleles;
    }
    
    //</editor-fold>
    
    //setter+getter methods
    // <editor-fold defaultstate="collapsed">
    
    public int getId(){
        return id;
    }
    
    public String getSymbol(){
        return symbol;
    }
    
    public void setSymbol(String symbol){
        this.symbol = symbol;
        dirty = true;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
        dirty = true;
    }

    public String getMgiId(){
        return mgiid;
    }

    public void setMgiId(String imsrid){
        this.mgiid = imsrid;
        dirty = true;
    }

    public String getMgi_url() {
        return mgi_url;
    }

    public void setMgi_url(String mgi_url) {
        this.mgi_url = mgi_url;
        dirty = true;
    }
    
    public Collection getMutationTypes(int eid){
        try {
            return mutationTypeHome.findByStrainAllele(id, eid, caller);
        }
        catch (Exception e) {
            throw new EJBException("StrainAlleleBean#getMutationType: Unable to get mutation types. \n"+e.getMessage());
        }
    }

    public String getMade_by(){
        return made_by;
    }

    public void setMade_by(String made_by){
        this.made_by = made_by;
        dirty = true;
    }

    public String getOrigin_strain(){
        return origin_strain;
    }

    public void setOrigin_strain(String origin_strain){
        this.origin_strain = origin_strain;
        dirty = true;
    }
    
    //</editor-fold>
    
    //relational methods
    // <editor-fold defaultstate="collapsed">

    /* gene = promoter for alleles */
    public void addGene(int gid) throws ApplicationException {
        makeConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("insert into r_strain_allele_gene (gid, aid) values (?,?) ");
            ps.setInt(1, gid);
            ps.setInt(2, id);

            ps.execute();
        } catch (Exception e) {
            throw new ApplicationException("StrainAlleleBean#addGene: Unable to add gene to strain allele "+id,e);
        } finally {
            releaseConnection();
        }
    }

    public void deleteGene(int gid) throws ApplicationException {
        makeConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("delete from r_strain_allele_gene where gid = ? and aid = ?");
            ps.setInt(1, gid);
            ps.setInt(2, id);

            ps.execute();
        } catch (Exception e) {
            throw new ApplicationException("StrainAlleleBean#deleteGene: Unable to delete gene from strain allele "+id,e);
        } finally {
            releaseConnection();
        }
    }

    public void addMutationTypeAndAttribute(int eid, int mutation_type, String attribute) throws ApplicationException{
        makeConnection();
        try {
            //FIXME!!! - Remove commented line. (strain-eid switch)
//            PreparedStatement ps = conn.prepareStatement("insert into r_strain_strain_allele_mutation_type (strain,strain_allele, mutation_type, attribute) values (?,?,?,?) ");
            PreparedStatement ps = conn.prepareStatement("insert into r_model_strain_allele_mutation_type (model,strain_allele, mutation_type, attribute) values (?,?,?,?) ");
            ps.setInt(1, eid);
            ps.setInt(2, id);
            ps.setInt(3, mutation_type);
            ps.setString(4, attribute);
            ps.execute();
        } catch (Exception e) {
            throw new ApplicationException("StrainAlleleBean#addMutationTypeAndAttribute: Unable to add type and attribute to strain allele "+id,e);
        } finally {
            releaseConnection();
        }
    }

    public boolean isAssigned(int eid, int mutation_type, String attribute) throws ApplicationException{
        boolean to_return = true;
        makeConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("select * from r_model_strain_allele_mutation_type where model = ? and strain_allele = ? and mutation_type= ? and attribute = ?");
            ps.setInt(1, eid);
            ps.setInt(2, id);
            ps.setInt(3, mutation_type);
            ps.setString(4, attribute);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) to_return = false;
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        } finally {
            releaseConnection();
        }

        return to_return;
    }

    //FIXME!!! - This must be removed completely or updated accordingly
    public void addMutationType(MutationTypeRemote mutationType) throws ApplicationException{
        makeConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("insert into R_MUTATION_TYPE_STRAIN_ALLELE (mutationtype,strainallele) values (?,?) ");
            ps.setInt(1, mutationType.getMutantid());
            ps.setInt(2, id);
            
            ps.execute();
        } catch (Exception e) {
            throw new ApplicationException("StrainAlleleBean#addMutationType: Unable to add type to strain allele "+id,e);
        } finally {
            releaseConnection();
        }
    }
    
    public void removeMutationType(int model, int mutation_type) throws ApplicationException{
        makeConnection();
        try {
//            PreparedStatement ps = conn.prepareStatement("delete from R_MUTATION_TYPE_STRAIN_ALLELE where mutationtype=? and strainallele=?");
            PreparedStatement ps = conn.prepareStatement("delete from r_model_strain_allele_mutation_type where mutation_type = ? and strain_allele = ? and model = ?");
            ps.setInt(1, mutation_type);
            ps.setInt(2, id);
            ps.setInt(3, model);
            ps.execute();
        } catch (Exception e) {
            throw new ApplicationException("StrainAlleleBean#removeMutationType: Unable to remove type from strain allele "+id,e);
        } finally {
            releaseConnection();
        }
    }

    public void unassign(int model) throws ApplicationException {
        makeConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("delete from r_model_strain_allele_mutation_type where strain_allele = ? and model = ?");
            ps.setInt(1, id);
            ps.setInt(2, model);
            ps.execute();
        } catch (Exception e) {
            throw new ApplicationException("StrainAlleleBean#unassign: Unable to unassign strain allele "+id + "from model " + model,e);
        } finally {
            releaseConnection();
        }
    }
    
//    public void setGeneToNULL(int strainalleleid) throws ApplicationException{
//        makeConnection();
//        try {
//            PreparedStatement ps = conn.prepareStatement("update strain_allele set gene=? where id=?");
//            ps.setObject(1,null);
//            ps.setInt(2, strainalleleid);
//            ps.execute();
//
//            logger.debug("---------------------------------------->StrainAlleleBean#setGeneToNULL: Executed query with strainalleleid = "+strainalleleid);
//
//        } catch (Exception e) {
//            throw new ApplicationException("StrainAlleleBean#setGeneToNULL: Unable to set gene to NULL for this allele ",e);
//        } finally {
//            releaseConnection();
//        }
//    }

    public String getAttributes(int eid){
        makeConnection();
        try {
            //FIXME!!! - Remove this (strain-eid switch)
//            PreparedStatement ps = conn.prepareStatement("select attribute from r_strain_strain_allele_mutation_type where strain = ? and strain_allele = ? limit 1");
            PreparedStatement ps = conn.prepareStatement("select attribute from r_model_strain_allele_mutation_type where model = ? and strain_allele = ? limit 1");
            ps.setInt(1, eid);
            ps.setInt(2, id);
            ResultSet result = ps.executeQuery();

            if (result.next()) attributes = result.getString("attribute");

        } catch (Exception e) {
            logger.error("StrainAlleleBean#getAttributes: Failed to perform due to: " + e.getMessage());
        } finally {
            releaseConnection();
        }
        return attributes;
    }
//
    public void setAttributes(int model, String attributes){
//        this.attributes = attributes;
//        dirty = true;
        //FIXME!!! - insert attribute in r_strain_strain_allele_mutation_type table
        makeConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("update r_model_strain_allele_mutation_type set attribute = ? where model = ? and strain_allele = ?");
            ps.setString(1, attributes);
            ps.setInt(2, model);
            ps.setInt(3, id);
            ps.execute();

        } catch (Exception e) {
            logger.error("StrainAlleleBean#setAttributes: Failed to perform due to: " + e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    /*
     * FIXME!!! - Following methods are supposed to merge strain allele and transgene information. Really crappy way to do it! Biologists are obviously bad analysts!!!
     */
    public String getTransgeneExpression() {
        String transgene_expression = "";
        
        try {
            Collection models = modelHome.findByStrainAllele(id, null);
            Iterator models_it = models.iterator();
            while(models_it.hasNext()) {
                ExpModelRemote model_tmp = (ExpModelRemote)models_it.next();
                Collection transgenes = geneHome.findByModelAndDistinguish(model_tmp.getEid(), "transgene");
                Iterator transgenes_it = transgenes.iterator();
                while(transgenes_it.hasNext()) {
                    GeneRemote transgene_tmp = (GeneRemote)transgenes_it.next();
                    transgene_expression += transgene_tmp.getGeneexpress(); //+ " / ";
                    
                    if(transgenes_it.hasNext()) transgene_expression += " / ";
                }
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
        return transgene_expression;
    }
    
    public String getTransgeneMolecular() {
        String transgene_molecular = "";
        
        try {
            Collection models = modelHome.findByStrainAllele(id, null);
            Iterator models_it = models.iterator();
            while(models_it.hasNext()) {
                ExpModelRemote model_tmp = (ExpModelRemote)models_it.next();
                Collection transgenes = geneHome.findByModelAndDistinguish(model_tmp.getEid(), "transgene");
                Iterator transgenes_it = transgenes.iterator();
                while(transgenes_it.hasNext()) {
                    GeneRemote transgene_tmp = (GeneRemote)transgenes_it.next();
                    
                    transgene_molecular += "<a href=\"" + transgene_tmp.getMolecular_note_link() + "\" target=\"_blank\">"+ transgene_tmp.getMolecular_note() + "</a>";
                    
                    if(transgenes_it.hasNext()) transgene_molecular += " / ";
                }
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
        return transgene_molecular;
    }
    
    public String getTransgeneChromosome() {
        String transgene_chromosome = "";
        
        try {
            Collection models = modelHome.findByStrainAllele(id, null);
            Iterator models_it = models.iterator();
            while(models_it.hasNext()) {
                ExpModelRemote model_tmp = (ExpModelRemote)models_it.next();
                Collection transgenes = geneHome.findByModelAndDistinguish(model_tmp.getEid(), "transgene");
                Iterator transgenes_it = transgenes.iterator();
                while(transgenes_it.hasNext()) {
                    GeneRemote transgene_tmp = (GeneRemote)transgenes_it.next();
                    
                    transgene_chromosome += transgene_tmp.getChromosome().getName();
                }
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
        return transgene_chromosome;
    }
    
    //</editor-fold>
    
    //create+postcreate methods
    // <editor-fold defaultstate="collapsed">
    
    public Integer ejbCreate(int id, String symbol, String name, TgDbCaller caller) throws javax.ejb.CreateException{
        makeConnection();
        Integer pk = null;
        try {
            
            this.caller = caller;
            
            this.id = id;
            this.name = name;
            this.symbol = symbol;
            
            pk = new Integer(id);
            
            PreparedStatement ps = conn.prepareStatement("insert into strain_allele (id, name, symbol) values (?, ?, ?)");
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, symbol);
//            ps.setInt(4, strain.getStrainid());
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("StrainAlleleBean#ejbCreate: Unable to create strain allele. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }

    public void ejbPostCreate(int id, String symbol, String name, TgDbCaller caller) throws javax.ejb.CreateException{}
    
    //</editor-fold>

}
