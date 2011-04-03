package org.tgdb.model.expmodel;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.PermissionDeniedException;
import org.tgdb.model.modelmanager.StrainAlleleDTO;
import org.tgdb.species.gene.GeneRemoteHome;
import org.tgdb.model.reference.ReferenceRemote;
import org.tgdb.model.reference.ReferenceRemoteHome;
import org.tgdb.model.researchapplication.ResearchApplicationRemote;
import org.tgdb.model.researchapplication.ResearchApplicationRemoteHome;

import org.tgdb.model.strain.allele.StrainAlleleRemote;
import org.tgdb.model.strain.allele.StrainAlleleRemoteHome;
import org.tgdb.model.strain.mutationtype.MutationTypeRemote;

import org.tgdb.model.availability.AvailabilityRemote;
import org.tgdb.model.availability.AvailabilityRemoteHome;
import org.tgdb.model.modelmanager.AvailabilityDTO;

import org.tgdb.model.geneticbackground.GeneticBackgroundRemote;
import org.tgdb.model.geneticbackground.GeneticBackgroundRemoteHome;
import org.tgdb.model.modelmanager.GeneticBackgroundDTO;

import org.tgdb.expression.expressionmodel.ExpressionModelRemote;
import org.tgdb.expression.expressionmodel.ExpressionModelRemoteHome;

import org.tgdb.genome.integrationcopy.IntegrationCopyRemote;
import org.tgdb.genome.integrationcopy.IntegrationCopyRemoteHome;

import org.tgdb.project.user.UserRemote;
import org.tgdb.project.user.UserRemoteHome;
import org.tgdb.resource.file.FileRemoteHome;
import org.tgdb.resource.resource.ResourceRemote;
import org.tgdb.resource.resource.ResourceRemoteHome;
import org.tgdb.samplingunit.expobj.ExpObj;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemote;
import org.tgdb.search.Keyword;
import org.tgdb.servicelocator.ServiceLocator;
import org.tgdb.species.gene.GeneRemote;
import java.rmi.RemoteException;
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

public class ExpModelBean extends ExpObj implements javax.ejb.EntityBean, org.tgdb.model.expmodel.ExpModelRemoteBusiness {
    
    //all params
    //<editor-fold defaultstate="collapsed">
    private javax.ejb.EntityContext context;
    private String researchApplicationText, availability, geneticBackground, donating_investigator, inducible, former_names;
    
    private int level;
    
    private int desired_level;
    
    private int MutationDistinctionParameter=0;
    
    private int researchApplication, contact;//, genotyping, handling, strain;
    
    
    private ResearchApplicationRemoteHome researchApplicationHome;
    
    private GeneticBackgroundRemoteHome genbackHome;
    
    private FileRemoteHome fileHome;
    private GeneRemoteHome geneHome;
    private ReferenceRemoteHome referenceHome;
//    private StrainRemoteHome strainHome;
    private StrainAlleleRemoteHome alleleHome;
//    private MutationTypeRemoteHome mutationHome;
//    private UserRemoteHome userHome;
    private ResourceRemoteHome resourceHome;
    private AvailabilityRemoteHome availabilityHome;
    private ExpressionModelRemoteHome expressionHome;
    private IntegrationCopyRemoteHome icHome;
    
    private TgDbCaller _caller;
    
    //</editor-fold>
    
    //ejb methods
    // <editor-fold defaultstate="collapsed">
    
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
        researchApplicationHome = (ResearchApplicationRemoteHome)locator.getHome(ServiceLocator.Services.RESEARCHAPPLICATION);
        fileHome = (FileRemoteHome)locator.getHome(ServiceLocator.Services.FILE);
        geneHome = (GeneRemoteHome)locator.getHome(ServiceLocator.Services.GENE);
        referenceHome = (ReferenceRemoteHome)locator.getHome(ServiceLocator.Services.REFERENCE);
//        strainHome = (StrainRemoteHome)locator.getHome(ServiceLocator.Services.STRAIN);
        alleleHome = (StrainAlleleRemoteHome)locator.getHome(ServiceLocator.Services.STRAIN_ALLELE);
//        mutationHome = (MutationTypeRemoteHome)locator.getHome(ServiceLocator.Services.MUTATION_TYPE);
        
        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
        resourceHome = (ResourceRemoteHome)locator.getHome(ServiceLocator.Services.RESOURCE);
        availabilityHome = (AvailabilityRemoteHome)locator.getHome(ServiceLocator.Services.AVAILABILITY);
        genbackHome = (GeneticBackgroundRemoteHome)locator.getHome(ServiceLocator.Services.GENETIC_BACKGROUND);
        expressionHome = (ExpressionModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPRESSION_MODEL);
        icHome = (IntegrationCopyRemoteHome)locator.getHome(ServiceLocator.Services.INTEGRATION_COPY);
    }
    
    public void ejbActivate() {}
    
    public void ejbPassivate() {}
    
    public void ejbRemove() {
        makeConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("delete from model where eid=?");
            ps.setInt(1, eid);
            ps.execute();
            super.remove();
        } catch (Exception e) {
            throw new EJBException("ExpModelBean#ebjRemove: Unable to remove exp model. \n"+e.getMessage());
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
            
            /** Load common things */
            super.load(pk.intValue());
            
            
            ps = conn.prepareStatement("select eid,background,availability,contact, application, apptext, level, desired_level, donating_investigator, inducible, former_names from model where eid=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                eid = rs.getInt("eid");
                geneticBackground = rs.getString("background");
                availability = rs.getString("availability");
                researchApplication = rs.getInt("application");
                researchApplicationText = rs.getString("appText");
                contact = rs.getInt("contact");
//                genotyping = rs.getInt("genotyping");
//                handling = rs.getInt("handling");
//                strain = rs.getInt("strain");
                level = rs.getInt("level");
                desired_level = rs.getInt("desired_level");
                donating_investigator = rs.getString("donating_investigator");
                inducible = rs.getString("inducible");
                former_names = rs.getString("former_names");
                dirty = false;
            } else
                throw new EJBException("ExpModelBean#ejbLoad: Error loading ExpModel");
        } catch (Exception e) {
            logger.error("---------------------------------------->ExpModelBean#ejbLoad: Failed to load model", e);
            throw new EJBException("ExpModelBean#ejbLoad: "+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    public void ejbStore() {
        if(dirty) {
            makeConnection();
            
            PreparedStatement ps = null;
            try {
                super.store();
                
                ps = conn.prepareStatement("update model set background=?,availability=?,contact=?,application=?, appText=? " +
                        ", level=?, desired_level=?, donating_investigator = ?, inducible = ?, former_names = ? where eid=?");

                int i=0;
                ps.setString(++i, geneticBackground);
                ps.setString(++i, availability);
                if (contact != 0)
                    ps.setInt(++i, contact); 
                else
                    ps.setNull(++i, java.sql.Types.INTEGER);
                if(researchApplication != 0)
                    ps.setInt(++i, researchApplication);
                else
                    ps.setNull(++i, java.sql.Types.INTEGER);
                ps.setString(++i, researchApplicationText);
                
//                if(genotyping != 0)
//                {
//                    ps.setInt(++i, genotyping);
//                }
//                else
//                {
//                    ps.setNull(++i, java.sql.Types.INTEGER);
//                }
//
//                if(handling != 0)
//                    ps.setInt(++i, handling);
//                else
//                    ps.setNull(++i, java.sql.Types.INTEGER);
                
//                if (strain != 0)
//                    ps.setInt(++i, strain);
//                else
//                    ps.setNull(++i, java.sql.Types.INTEGER);
                            

                ps.setInt(++i, level);
                
                ps.setInt(++i, desired_level);

                ps.setString(++i, getDonating_investigator());

                ps.setString(++i, getInducible());

                ps.setString(++i, getFormer_names());
                
                ps.setInt(++i, eid);
                
                int rows = ps.executeUpdate();
                if (rows!=1) {
                    throw new EJBException("ExpModelBean#ejbStore: Error saving ExpModel. Rows affected "+rows);
                }
            } catch (Exception e) {
                logger.error("---------------------------------------->ExpModelBean#ejbStore: Failed to store model", e);
                throw new EJBException("ExpModelBean#ejbStore: Error saving ExpModel. \n"+e.getMessage());
            } finally {
                releaseConnection();
                dirty = false;
            }
        }
    }    
    
    // </editor-fold>
    
    //finder methods
    // <editor-fold defaultstate="collapsed">
    
    public java.lang.Integer ejbFindByPrimaryKey(java.lang.Integer eid) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            //String levelString = getModelLevelSql(_caller);  // +levelString
            ps = conn.prepareStatement("select eid from model where eid = ? ");
            ps.setInt(1, eid.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("ExpModelBean#ejbFindByPrimaryKey: Cannot find model");
            }
        } catch (SQLException se) {
            throw new FinderException("ExpModelBean#ejbFindByPrimaryKey: Cannot find model "+eid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return eid;
    }

    public java.lang.Integer ejbFindByNAME(String name) throws javax.ejb.FinderException {
        makeConnection();
        Integer to_return;
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select eid from expobj where alias = ? ");
            ps.setString(1, name);
            result = ps.executeQuery();

            if (!result.next()) {
                throw new ObjectNotFoundException("ExpModelBean#ejbFindByCommonName: Cannot find model");
            }
            else {
                to_return = new Integer(result.getInt("eid"));
            }
        } catch (SQLException se) {
            throw new FinderException("ExpModelBean#ejbFindByCommonName: Cannot find model with name"+name+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return to_return;
    }
    
    public java.util.Collection ejbFindBySamplingUnit(int suid, TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        Collection models = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        
        try {
            String levelString = getModelLevelSql(caller);
            ps = conn.prepareStatement("select m.eid from model m, expobj e where m.eid = e.eid and e.suid = ? "+levelString);
            ps.setInt(1, suid);
            result = ps.executeQuery();
            
            while(result.next()) {
                models.add(new Integer(result.getInt("eid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ExpModelBean#ejbFindBySamplingUnit: Cannot find models\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        
        return models;
    }
    
    public java.util.Collection ejbFindByStrainAllele(int strain_allele, TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        Collection models = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;

        try {
//            String levelString = getModelLevelSql(caller);
            ps = conn.prepareStatement("select distinct(model) as eid from r_model_strain_allele_mutation_type where strain_allele = ?");
            ps.setInt(1, strain_allele);
            result = ps.executeQuery();

            while(result.next()) {
                models.add(new Integer(result.getInt("eid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ExpModelBean#ejbFindByStrainAllele: Cannot find models by strain allele " + strain_allele + " \n"+se.getMessage());
        } finally {
            releaseConnection();
        }

        return models;
    }

    public java.util.Collection ejbFindByStrain(int strain, TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        Collection models = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;

        try {
//            String levelString = getModelLevelSql(caller);
            ps = conn.prepareStatement("select model as eid from r_model_strain where strain = ?");
            ps.setInt(1, strain);
            result = ps.executeQuery();

            while(result.next()) {
                models.add(new Integer(result.getInt("eid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ExpModelBean#ejbFindByStrain: Cannot find models by strain " + strain + " \n"+se.getMessage());
        } finally {
            releaseConnection();
        }

        return models;
    }
    
    public java.util.Collection ejbFindByBackrossingListGeneration(TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        Collection models = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        
        try {
            ps = conn.prepareStatement("select m.eid from model m, expobj e, users u, research_application r where m.eid = e.eid and m.contact=u.id and m.application=r.raid order by r.name, u.group_name");
            result = ps.executeQuery();
            
            while(result.next()) {
                models.add(new Integer(result.getInt("eid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ExpModelBean#ejbFindByBackrossingListGeneration: Cannot get models for backrossing list generation\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        
        return models;
    }
    
    public java.util.Collection ejbFindByModelsThatNeedDissUpdate(TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        Collection models = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        
        try {
            ps = conn.prepareStatement("select eid from model where level != desired_level");
            result = ps.executeQuery();
            
            while(result.next()) {
                models.add(new Integer(result.getInt("eid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ExpModelBean#ejbModelsThatNeedDissUpdate: Cannot find models that need dissemination level update.\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        
        return models;
    }
    
    public java.util.Collection ejbFindByIMSRSubmission(int suid, TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        Collection models = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        
        try {
            //String levelString = getModelLevelSql(_caller);
            //get only public models that are not entered in the r_model_imsr table
            String levelString = "and level = 0";
            ps = conn.prepareStatement("select m.eid from model m, expobj e where m.eid = e.eid and e.suid = ? and m.eid not in (select eid from r_model_imsr) "+levelString);
            ps.setInt(1, suid);
            result = ps.executeQuery();
            
            while(result.next()) {
                models.add(new Integer(result.getInt("eid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ExpModelBean#ejbFindByIMSRSubmission: Cannot find models for IMSR Submission\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        
        return models;
    }
    
    public Collection ejbFindByResearchApplication(int raid, TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        Collection arr = new ArrayList();        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            String levelString = getModelLevelSql(caller);
            ps = conn.prepareStatement("select eid from model where application = ? "+levelString);
            ps.setInt(1, raid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("eid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ExpModelBean#ejbFindByResearchApplication: Cannot find model \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public java.util.Collection ejbFindByKeyword(Keyword keyword, TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        Collection arr = new ArrayList();        
        PreparedStatement ps = null;
        ResultSet result = null;
        //validate("MODEL_R",_caller);
        try {
            String levelString = getModelLevelSql(caller);
            ps = conn.prepareStatement("select m.eid, e.suid from model m, expobj e where m.eid=e.eid and "
                    +"(lower(background) like ? "+
                    "or lower(availability) like ? "+
                    "or lower(apptext) like ? "+
                    "or lower(identity) like ? "+
                    "or lower(alias) like ? "+
                    "or lower(comm) like ?)" +levelString);
            
            String search = "%"+keyword.getKeyword()+"%";
            
            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);
            ps.setString(4, search);
            ps.setString(5, search);
            ps.setString(6, search);
            
            result = ps.executeQuery();
            
            
            while (result.next()) 
            {
                try
                {
                    validateSU("MODEL_R", caller, result.getInt("suid"));
                    arr.add(new Integer(result.getInt("eid")));
                }
                catch (PermissionDeniedException pde)
                {
                }
            }
        } catch (SQLException se) {
            throw new FinderException("ExpModelBean#ejbFindByKeyword: Cannot find model by keyword\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public java.util.Collection ejbFindByFormDataManager(FormDataManager fdm, org.tgdb.TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        
        //sets the _caller
        setCaller(caller);
        
        Collection models = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        
        // Get Research application checkbox value (raid)
        int raid = 0;
        String tmp = fdm.getValue("raid");
        if (tmp!=null && !tmp.equals("") && !tmp.equals("*"))
            raid = new Integer(tmp).intValue();
        
        // Get gene checkbox value (gaid)
        int gaid = 0;
        tmp = fdm.getValue("gaid");
        if (tmp!=null && !tmp.equals("") && !tmp.equals("*"))
            gaid = new Integer(tmp).intValue();
        
        // Get the sampling unit value (suid)
        int suid = 0;
        tmp = fdm.getValue("suid");
        if (tmp!=null && !tmp.equals(""))
             suid = new Integer(tmp).intValue();
        else
            suid = caller.getSuid();
        
        String groupname ="";
        tmp = fdm.getValue("groupname");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            groupname = tmp;
        
        String participant ="";
        tmp = fdm.getValue("participantname");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            participant = tmp;
        
        int mutationtypes = 0;
        tmp = fdm.getValue("mutationtypes");
        if (tmp!=null && !tmp.equals("") && !tmp.equals("*"))
            mutationtypes = new Integer(tmp).intValue();

        int strain = 0;
        tmp = fdm.getValue("strain");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            strain = new Integer(tmp).intValue();
        
        String emap ="";
        tmp = fdm.getValue("emap");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            emap = tmp;
        
        String ma ="";
        tmp = fdm.getValue("ma");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            ma = tmp;
        
        String inducible ="";
        tmp = fdm.getValue("inducible");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            inducible = tmp;
        
        String orderby ="";
        tmp = fdm.getValue("ordertype");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            orderby = tmp;
        
        String disslevel ="";
        tmp = fdm.getValue("disslevel");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            disslevel = tmp;
        
        try {
            String sql = "select m.eid from model m, expobj e where m.eid=e.eid and suid = ? ";
            
            if (raid!=0)
            {
                sql += " and application = ? ";
            }
            
            if (gaid!=0)
            {
                /*
                 * Use this for regular genes...
                 */
//                sql += " and m.eid in (select eid from r_gene_model where gaid=?) ";
                /*
                 * ...and this for promoters
                 */
                sql += " and m.eid in (select distinct(mst.model) as eid from r_model_strain_allele_mutation_type mst join r_strain_allele_gene sg on sg.aid = mst.strain_allele where sg.gid = ?) ";
            }
            
            if (!groupname.equals(""))
            {
                sql += " and m.eid in (select eid from model where contact in (select id from users where group_name like ?)) ";
            }
            
            if (!participant.equals(""))
            {
                sql += " and m.eid in (select eid from model where contact in (select id from users where name like ?)) ";
            }
            
            if (mutationtypes!=0)
            {
                sql += " and m.eid in (select eid from model where strain in (select strainid from strain where strainid in (select strainid from strain_allele where id in (select strainallele from r_mutation_type_strain_allele where mutationtype in (select id from mutation_type where id=?))))) ";
            }

            if (strain!=0)
            {
                sql += " and m.eid in (select model from r_model_strain where strain=? ) ";
            }
            
            if (!emap.equals(""))
            {
                sql += " and m.eid in (select distinct(emr.eid) as eid from expression_model_r  emr join r_expression_ontology reo on emr.exid = reo.exid where reo.oid = ?) ";
            }
            
            if (!ma.equals(""))
            {
                sql += " and m.eid in (select distinct(emr.eid) as eid from expression_model_r  emr join r_expression_ontology reo on emr.exid = reo.exid where reo.oid = ?) ";
            }
            
            if (!inducible.equals(""))
            {
                sql += " and m.inducible = ? ";
            }
            
            if(!disslevel.equals("")){
                if(disslevel.equals("Admin")){
                    sql += " and m.eid in (select eid from model where level=2) ";
                }
                
                if(disslevel.equals("Mugen")){
                    sql += " and m.eid in (select eid from model where level=1) ";
                }
                
                if(disslevel.equals("Public")){
                    sql += " and m.eid in (select eid from model where level=0) ";
                }
            }
            
            String levelString = getModelLevelSql(caller);
            sql += levelString;
            
            if (!orderby.equals(""))
            {
                if(orderby.equals("MMMDb ID")){
                    sql += " order by eid";
                }
                
                if(orderby.equals("LINE NAME")){
                    sql += " order by alias";
                }
                
                if(orderby.equals("DATE")){
                    sql += " order by e.ts desc";
                }
                
                if(orderby.equals("INDUCIBILITY")){
                    sql += " order by inducible";
                }
                
            }
            else {
                //default by date
                sql += " order by e.ts desc";
            }
                    
            ps = conn.prepareStatement(sql);
            
            int i = 1;
            ps.setInt(i++, suid);
            
            if (raid!=0)
                ps.setInt(i++, raid);
            if (gaid!=0)
                ps.setInt(i++, gaid);
            if (!groupname.equals(""))
                ps.setString(i++, groupname);
            if (!participant.equals(""))
                ps.setString(i++, participant);
            if (mutationtypes!=0)
                ps.setInt(i++, mutationtypes);
            if (strain!=0)
                ps.setInt(i++, strain);
            if (!emap.equals(""))
                ps.setString(i++, emap);
            if (!ma.equals(""))
                ps.setString(i++, ma);
            if (!inducible.equals(""))
                ps.setString(i++, inducible);
            
            result = ps.executeQuery();
            
            while(result.next()) {
                models.add(new Integer(result.getInt("eid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ExpModelBean#ejbFindBySamplingUnit: Cannot find models\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        
        return models;
    }
    
    public java.util.Collection ejbFindByFormDataManagerForDissUpdate(FormDataManager fdm, org.tgdb.TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        Collection models = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        
        // Get Research application checkbox value (raid)
        int raid = 0;
        String tmp = fdm.getValue("raid");
        if (tmp!=null && !tmp.equals("") && !tmp.equals("*"))
            raid = new Integer(tmp).intValue();
        
        String partcipantname = "";
        tmp = fdm.getValue("participantname");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            partcipantname = tmp;
        
        String groupname ="";
        tmp = fdm.getValue("groupname");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            groupname = tmp;
        
        int mutationtypes = 0;
        tmp = fdm.getValue("mutationtypes");
        if (tmp!=null && !tmp.equals("") && !tmp.equals("*"))
            mutationtypes = new Integer(tmp).intValue();
        
        String orderby ="";
        tmp = fdm.getValue("ordertype");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            orderby = tmp;
        
        String disslevel ="";
        tmp = fdm.getValue("disslevel");
        if (tmp!=null && !tmp.equals("") && tmp.compareTo("*")!=0)
            disslevel = tmp;
        
        try {
            String sql = "select eid from model where level != desired_level";
            
            if (raid!=0)
            {
                sql += " and application = ? ";
            }
            
            if (!partcipantname.equals(""))
            {
                sql += " and eid in (select eid from model where contact in (select id from users where name like ?)) ";
            }
                        
            if (!groupname.equals(""))
            {
                sql += " and eid in (select eid from model where contact in (select id from users where group_name like ?)) ";
            }
            
            if (mutationtypes!=0)
            {
                sql += " and eid in (select eid from model where strain in (select strainid from strain where strainid in (select strainid from strain_allele where id in (select strainallele from r_mutation_type_strain_allele where mutationtype in (select id from mutation_type where id=?))))) ";
            }
            
            if(!disslevel.equals("")){
                if(disslevel.equals("Admin")){
                    sql += " and eid in (select eid from model where level=2) ";
                }
                
                if(disslevel.equals("Mugen")){
                    sql += " and eid in (select eid from model where level=1) ";
                }
                
                if(disslevel.equals("Public")){
                    sql += " and eid in (select eid from model where level=0) ";
                }
            }
            
            String levelString = getModelLevelSql(caller);
            sql += levelString;
            
            if (!orderby.equals(""))
            {
                if(orderby.equals("MMMDb ID")){
                    sql += " order by eid";
                }
                
                if(orderby.equals("DATE")){
                    sql += " order by ts";
                }
                
                if(orderby.equals("INDUCIBILITY")){
                    sql += " order by inducible";
                }
                
            }
                    
            ps = conn.prepareStatement(sql);
            
            int i = 1;
            
            if (raid!=0)
                ps.setInt(i++, raid);
            if (!partcipantname.equals(""))
                ps.setString(i++, partcipantname);
            if (!groupname.equals(""))
                ps.setString(i++, groupname);
            if (mutationtypes!=0)
                ps.setInt(i++, mutationtypes);
            
            result = ps.executeQuery();
            
            while(result.next()) {
                models.add(new Integer(result.getInt("eid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ExpModelBean#ejbFindByFormDataManagerForDissUpdate: Cannot find models\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        
        return models;
    }
    
    public java.util.Collection ejbFindByGene(int gaid, TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        Collection models = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            String levelString = "";//getModelLevelSql(_caller);
            ps = conn.prepareStatement("select m.eid from r_gene_model r, model m where r.eid = m.eid and gaid = ? "+levelString);
            ps.setInt(1, gaid);
            result = ps.executeQuery();
            
            while(result.next()) {
                models.add(new Integer(result.getInt("eid")));
            }
        } catch (SQLException se) {
            se.printStackTrace();
            throw new FinderException("ExpModelBean#ejbFindByGene: Cannot find models by gene\n"+se.getMessage());
        } finally {
            releaseConnection();
        }        
        return models;
    }

    public java.util.Collection ejbFindByPromoter(int gaid, TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        Collection models = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            String levelString = "";//getModelLevelSql(_caller);
            ps = conn.prepareStatement("select distinct(m.model) as eid from r_model_strain_allele_mutation_type m join r_strain_allele_gene s on m.strain_allele = s.aid where s.gid = ?"+levelString);
            ps.setInt(1, gaid);
            result = ps.executeQuery();

            while(result.next()) {
                models.add(new Integer(result.getInt("eid")));
            }
        } catch (SQLException se) {
            logger.error(getStackTrace(se));
        } finally {
            releaseConnection();
        }
        return models;
    }
    
    //</editor-fold>
    
    //setter+getter methods
    // <editor-fold defaultstate="collapsed">
    
    /**
     * @return the donating_investigator
     */
    public String getDonating_investigator() {
        return donating_investigator;
    }

    /**
     * @param donating_investigator the donating_investigator to set
     */
    public void setDonating_investigator(String donating_investigator) {
        this.donating_investigator = donating_investigator;
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;        
    }

    /**
     * @return the inducible
     */
    public String getInducible() {
        return inducible;
    }

    /**
     * @param inducible the inducible to set
     */
    public void setInducible(String inducible) {
        this.inducible = inducible;
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;
    }

    /**
     * @return the former_names
     */
    public String getFormer_names() {
        return former_names;
    }

    /**
     * @param former_names the former_names to set
     */
    public void setFormer_names(String former_names) {
        this.former_names = former_names;
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;
    }

    public String getResearchApplicationText() {
        return researchApplicationText;
    }

    public void setResearchApplicationText(String researchApplicationText) {
        this.researchApplicationText = researchApplicationText;
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;        
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;        
    }

    public String getGeneticBackground() {
        return geneticBackground;
    }

    public void setGeneticBackground(String geneticBackground) {
        this.geneticBackground = geneticBackground;
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;        
    }
    
    public Collection getGeneticBackgroundInfo() throws ApplicationException{
        Collection arr = null;
        try {
            arr = genbackHome.findByGeneticBackgroundModel(eid);
            Collection dtos = new ArrayList();
            Iterator i = arr.iterator();
            
            while(i.hasNext()) {                
                dtos.add(new GeneticBackgroundDTO((GeneticBackgroundRemote)i.next()));
            }
            
            return dtos;
            
            } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genetic background (backcrosses etc.) information for specific model.", e);
        }
    }

    public ResearchApplicationRemote getResearchApplication() {
        try
        {
            if(researchApplication > 0) {
                ResearchApplicationRemote ra = researchApplicationHome.findByPrimaryKey(new Integer(researchApplication));
                return ra;
            } else
                return null;
        }
        catch (Exception e)
        {
            throw new EJBException("ExpModelBean#getResearchApplication: Failed to get researchApplication");
        }
    }

    public void setResearchApplication(ResearchApplicationRemote ra) throws RemoteException {
        this.researchApplication = ra.getRaid();
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;        
    }
    
    public UserRemote getContact() {
        try
        {
            UserRemote usr = userHome.findByPrimaryKey(new Integer(contact));
            return usr;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

//    public FileRemote getGenotypingFile() {
//        try {
//            FileRemote file = fileHome.findByPrimaryKey(new Integer(genotyping));
//            return file;
//        } catch (FinderException fe) {
//            return null;
//        } catch (Exception e) {
//            throw new EJBException("Could not get genotyping file");
//        }
//    }
//
//    public FileRemote getHandlingFile() {
//        try {
//            FileRemote file = fileHome.findByPrimaryKey(new Integer(handling));
//            return file;
//        } catch (FinderException fe) {
//            return null;
//        } catch (Exception e) {
//            throw new EJBException("Could not get handling file");
//        }
//    }
//
//    public void setGenotypingFile(int fileid) {
//        genotyping = fileid;
//        ts = new java.sql.Date(System.currentTimeMillis());
//        dirty = true;
//    }
//
//    public void setHandlingFile(int fileid) {
//        handling = fileid;
//        ts = new java.sql.Date(System.currentTimeMillis());
//        dirty = true;
//    }

    public void setContact(UserRemote usr) throws RemoteException {
        this.contact = usr.getId();
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;
    }

    @Override
    public void setCaller(TgDbCaller caller) {
        this._caller = caller;
        super.setCaller(caller);
        dirty = true;
    }
    
    public Collection getResources() throws ApplicationException {
        try {
            return resourceHome.findByModel(eid, _caller);
        } catch (Exception e) {
            throw new ApplicationException("Could not get resources");
        }
    }
    
    public Collection getAvailabilityForModel(int eid) throws ApplicationException{
        try {
            Collection _availability = availabilityHome.findByModel(eid);
            Collection dtos = new ArrayList();
            Iterator i = _availability.iterator();
            while(i.hasNext()) {                
                dtos.add(new AvailabilityDTO((AvailabilityRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get availability information for specific model.", e);
        }
    }

    public int getNumberOfPhenotypes() throws ApplicationException {
        makeConnection();
        try {
            
            PreparedStatement ps = conn.prepareStatement("select count(iid) as num from phenotypes where iid = ? and suid = ?");
            ps.setInt(1, eid);
            ps.setInt(2, suid);
            
            ResultSet result = ps.executeQuery();
            int num = 0;
            if(result.next())
                num = result.getInt("num");
            
            return num;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("ExpModelBean#getNumberOfPhenotypes Unable to count phenotypes. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }

    public Collection getReferences(){
        Collection arr = null;
        try {
            arr = referenceHome.findByModel(eid);
        } catch (FinderException fe) {
            throw new EJBException(fe);
        } catch (RemoteException re) {
            throw new EJBException(re);
        }
        return arr;
    }

    public int getLevel(){
        return level;
    }

    public void setLevel(int level){
        this.level = level;
        dirty = true;
    }
    
    public int getDesiredLevel(){
        return desired_level;
    }
    
    public void setDesiredLevel(int desired_level){
        this.desired_level = desired_level;
        dirty = true;
    }
    
    public String getMutationTypesForModel () throws ApplicationException {
        try{
//            StrainRemote _strain = getStrain();
//            Collection strainAlleles = _strain.getStrainAlleles();
            Collection alleles = alleleHome.findByStrain(eid, caller);
            Iterator i = alleles.iterator();
            String mutations = "";
            MutationDistinctionParameter = 0;
            int j=0;
            while (i.hasNext()) {
                StrainAlleleRemote sar = (StrainAlleleRemote)i.next();
                //FIXME!!! - Remove this (strain-eid switch)
//                Collection mutationTypes = sar.getMutationTypes(this.strain);
                Collection mutationTypes = sar.getMutationTypes(this.eid);
                Iterator k = mutationTypes.iterator();
                while (k.hasNext()) {
                    MutationTypeRemote m = (MutationTypeRemote)k.next();
                    
                    if (!mutations.contains(m.getName())){
                        if (j!=0)
                            mutations += ", ";
                        mutations += m.getName();
                        
                        //Mutation Distinction Factor
                        if (m.getName().compareTo("transgenic")==0){
                            MutationDistinctionParameter = 1;
                        }
                    }
                    j++;
                }//nested while
            }//while
            return mutations;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new ApplicationException("ExpModelBean#getMutationTypesForModel: Failed to get mutation types for model. ",ex);
        }
    }
    
    public Collection getStrainAlleleInfo() throws ApplicationException{
        try {
//            StrainRemote _strain = getStrain();
//            Collection strainAlleles = _strain.getStrainAlleles();
            Collection alleles = alleleHome.findByStrain(eid, caller);
            Collection dtos = new ArrayList();
            
            Iterator i = alleles.iterator();
            
            while(i.hasNext()) {                
                dtos.add(new StrainAlleleDTO((StrainAlleleRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain allele information for specific model.", e);
        }
    }
    
    public int getMutationDistinctionParameter() throws ApplicationException {
        return MutationDistinctionParameter;
    }
    
    public Collection getIntegrationCopies(){
        Collection arr = null;
        try {
            arr = icHome.findByModel(eid);
        } catch(ObjectNotFoundException oe) {
            // Nothing..return empty list
            return new ArrayList();             
        } catch (FinderException fe) {
            // Nothing..return empty list
            return new ArrayList();
        } catch (RemoteException re) {
            throw new EJBException(re);
        }
        return arr;
    }
    
    //</editor-fold>
    
    //relational methods
    // <editor-fold defaultstate="collapsed">
    
    public String getModelLevelSql(TgDbCaller clr){
        
        String levelString = "";
        if (clr.hasPrivilege("MODEL_ADM"))
        {
            levelString = " and level <= 2 ";
        }
        else if (clr.hasPrivilege("MODEL_MUGEN"))
        {
            levelString = " and level <= 1 ";
        }
        else if (clr.hasPrivilege("MODEL_PUB"))
        {
            levelString = " and level <= 0 ";
        }
        return levelString;
    }
    
    public void addExpressionModel(ExpressionModelRemote expression) throws ApplicationException {
        makeConnection();
        try {
            
            PreparedStatement ps = conn.prepareStatement("insert into expression_model_r (eid, exid) values (?,?)");
            ps.setInt(1, eid);
            ps.setInt(2, expression.getExid());
            
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("ExpModelBean#addExpressionModel: Unable to add expression model. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    public void addIntegrationCopy(IntegrationCopyRemote ic) throws ApplicationException {
        makeConnection();
        try {
            
            PreparedStatement ps = conn.prepareStatement("insert into is_cm_model_r (eid, iscmid) values (?,?)");
            ps.setInt(1, eid);
            ps.setInt(2, ic.getIscnid());
            
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("ExpModelBean#addIntegrationCopy: Unable to add integration copy data set. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    public int IMSRSubmit(int eid) throws ApplicationException{
        try
        {
            makeConnection();
            PreparedStatement ps = conn.prepareStatement("insert into r_model_imsr (eid,imsr,ts) values (?,?,?)");
            ps.setInt(1, eid);
            ps.setInt(2, 1);
            ps.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.execute();
            return 1;
        }
        catch (Exception e)
        {
            throw new ApplicationException("ExpModelBean#addGene: Failed to add this model to the r_model_imsr table",e);
        }
        finally 
        {
            releaseConnection();
        }
    }
    
    public Collection getGeneAffected(){
        Collection arr = null;
        try {
            arr = geneHome.findByModel(eid);
        } catch(ObjectNotFoundException oe) {
            // Nothing..return empty list
            return new ArrayList();             
        } catch (FinderException fe) {
            // Nothing..return empty list
            return new ArrayList();
        } catch (RemoteException re) {
            throw new EJBException(re);
        }
        return arr;
    }
    
    public String getPromotersString(){
        String promoters_string = "";
        try {
            Collection arr = geneHome.findPromoters(eid);
            Iterator i = arr.iterator();
            while(i.hasNext()) {                
                GeneRemote promoter = (GeneRemote)i.next();
                promoters_string += promoter.getGenesymbol();// + ", ";
                if(i.hasNext()) promoters_string += ", ";
            }
            
        } catch(ObjectNotFoundException oe) {          
        } catch (FinderException fe) {
        } catch (RemoteException re) {
            throw new EJBException(re);
        }
        return promoters_string;
    }
    
    public void removeReference(ReferenceRemote reference) throws ApplicationException {
        makeConnection();
        try {
            
            PreparedStatement ps = conn.prepareStatement("delete from r_ref_model where refid = ? and eid = ?");
            ps.setInt(1, reference.getRefid());
            ps.setInt(2, eid);
            
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("ExpModelBean#removeReference: Unable to remove reference. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }

    public void addResource(ResourceRemote res) throws ApplicationException {
        makeConnection();
        try {
            
            PreparedStatement ps = conn.prepareStatement("insert into r_resource_model (resourceid,eid) values (?,?)");
            ps.setInt(1, res.getResourceId());
            ps.setInt(2, eid);
            
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("ExpModelBean#addResource: Unable to add resource. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    public void addReference(ReferenceRemote ref) throws ApplicationException{
        makeConnection();
        try {
            
            PreparedStatement ps = conn.prepareStatement("insert into r_ref_model (refid,eid) values (?,?)");
            ps.setInt(1, ref.getRefid());
            ps.setInt(2, eid);
            
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("ExpModelBean#addReference: Unable to add reference. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    public void addGene(GeneRemote gene) throws ApplicationException{
        try
        {
            makeConnection();
            PreparedStatement ps = conn.prepareStatement("insert into r_gene_model (gaid,eid) values (?,?)");
            ps.setInt(1, gene.getGaid());
            ps.setInt(2, eid);
            
            ps.execute();
        }
        catch (Exception e)
        {
//            throw new ApplicationException("ExpModelBean#addGene: Failed to add gene to this model",e);
            logger.error(getStackTrace(e));
        }
        finally 
        {
            releaseConnection();
        }
    }
    
    public void removeGene(GeneRemote gene) throws ApplicationException{
        try
        {
            makeConnection();
            PreparedStatement ps = conn.prepareStatement("delete from r_gene_model where gaid=? and eid=?");
            ps.setInt(1, gene.getGaid());
            ps.setInt(2, eid);
            ps.execute();
        }
        catch (Exception e)
        {
            throw new ApplicationException("ExpModelBean#removeGene: Failed to remove gene from this model",e);
        }
        finally 
        {
            releaseConnection();
        }
    }
    
    public void unassignGeneFromStrainAlleles(int strainId, int geneId) throws ApplicationException{
        try
        {
            makeConnection();
            PreparedStatement ps = conn.prepareStatement("update strain_allele set gene=null where strainid=? and gene=?");
            ps.setInt(1, strainId);
            ps.setInt(2, geneId);
            ps.execute();
        }
        catch (Exception e)
        {
            throw new ApplicationException("ExpModelBean#unassignGeneFromStrainAlleles: Failed to unassign genes from alleles of this model",e);
        }
        finally 
        {
            releaseConnection();
        }
    }
    
    public void unassignStrainAllelesFromGene(int eid, int strainid) throws ApplicationException{
        try
        {
            makeConnection();
            PreparedStatement ps = conn.prepareStatement("delete from r_gene_model where eid=? and gaid not in (select gene from strain_allele where strainid=? and gene is not null)");
            ps.setInt(1, eid);
            ps.setInt(2, strainid);
            ps.execute();
        }
        catch (Exception e)
        {
            throw new ApplicationException("ExpModelBean#unassignStrainAllelesFromGene: Failed to unassign genes from allele gene reassignment",e);
        }
        finally 
        {
            releaseConnection();
        }
    }

    public void addStrain(int strain) throws ApplicationException{
        try
        {
            makeConnection();
            PreparedStatement ps = conn.prepareStatement("insert into r_model_strain (strain,model) values (?,?)");
            ps.setInt(1, strain);
            ps.setInt(2, eid);

            ps.execute();
        }
        catch (Exception e) {
            throw new ApplicationException("ExpModelBean#addStrain: Failed to add strain to model " + eid + " name " + alias,e);
        }
        finally {
            releaseConnection();
        }
    }

    public void clearStrain(int strain) throws ApplicationException {
        try
        {
            makeConnection();
            PreparedStatement ps = conn.prepareStatement("delete from r_model_strain where strain = ? and model = ?");
            ps.setInt(1, strain);
            ps.setInt(2, eid);

            ps.execute();
        }
        catch (Exception e) {
            throw new ApplicationException("ExpModelBean#addStrain: Failed to remove strain from model " + eid,e);
        }
        finally {
            releaseConnection();
        }
    }
    // </editor-fold>
    
    //get expression models for model
    // <editor-fold defaultstate="collapsed">
    public Collection getExpressionModels(){
        Collection arr = null;
        try {
            arr = expressionHome.findByModel(eid);
        } catch(ObjectNotFoundException oe) {
            // Nothing..return empty list
            return new ArrayList();             
        } catch (FinderException fe) {
            // Nothing..return empty list
            return new ArrayList();
        } catch (RemoteException re) {
            throw new EJBException(re);
        }
        return arr;
    }
    //</editor-fold>

    //create+postcreate methods
    // <editor-fold defaultstate="collapsed">
    public java.lang.Integer ejbCreate(int eid, java.lang.String identity, SamplingUnitRemote samplingUnit, TgDbCaller caller) throws javax.ejb.CreateException {
        makeConnection();
        Integer pk = null;
        try {
            this.eid = eid;
            this.identity = identity;
            
            create(eid, identity, samplingUnit, caller);
            
            geneticBackground = "";
            availability = "";
            contact = 0;
            researchApplication = 0;
            researchApplicationText = "";
            status = "E";
            alias = "Alias";
            level = 1;
            ts = new java.sql.Date(System.currentTimeMillis());
            id = caller.getId();
            
            pk = new Integer(eid);
            
            PreparedStatement ps = conn.prepareStatement("insert into model (eid) values (?)");
            ps.setInt(1, eid);
            
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("ExpModelBean#ejbCreate: Unable to create model. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }

    public void ejbPostCreate(int eid, java.lang.String identity, SamplingUnitRemote samplingUnit, TgDbCaller caller) throws javax.ejb.CreateException {}
    
    //</editor-fold>
    
    //web services methods
    //<editor-fold defaultstate="collapsed">
    
    //simple model finder
    public java.util.Collection ejbFindByWebServiceRequest() throws javax.ejb.FinderException {
        makeConnection();
        Collection models = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        
        try {
            ps = conn.prepareStatement("select m.eid from model m, expobj e where m.eid = e.eid and level <= 0 ");
            result = ps.executeQuery();
            
            while(result.next()) {
                models.add(new Integer(result.getInt("eid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ExpModelBean#ejbFindByWebServiceRequest: Cannot find models\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        
        return models;
    }
    
    //key model finder
    
    public java.util.Collection ejbFindByWebServiceKeywordRequest(String keyword) throws javax.ejb.FinderException {
        makeConnection();
        Collection arr = new ArrayList();        
        PreparedStatement ps = null;
        ResultSet result = null;
        
        try {
            ps = conn.prepareStatement("select m.eid, e.suid from model m, expobj e where m.eid=e.eid and level <= 0 and "
                    +"(lower(background) like ? "+
                    "or lower(availability) like ? "+
                    "or lower(apptext) like ? "+
                    "or lower(identity) like ? "+
                    "or lower(alias) like ? "+
                    "or lower(comm) like ? " +
                    "or strain in (select strainid from strain where lower(designation) like ? ) " +
                    //model search based on gene
                    "or m.eid in (select eid from r_gene_model where gaid in (select distinct gaid from gene where lower(name) like ? or lower(comm) like ? or lower(mgiid) like ? or lower(genesymbol) like ? or lower(idensembl) like ?)) " +
                    //model search bansed on phenotype
                    //"or m.eid in (select eid from getPaths(?) as (eid int4)) " +
                    //model search based on availability
                    "or m.eid in (select distinct eid from r_model_repositories_avgenback where " +
                    "rid in (select distinct rid from repositories where lower(reponame) like ?) or " +
                    "aid in (select distinct aid from available_genetic_back where lower(avbackname) like ?) or " +
                    "stateid in (select distinct id from strain_state where lower(name) like ?) or " +
                    "typeid in (select distinct id from strain_type where lower(name) like ?)) " +
                    //model search based on genetic background
                    "or m.eid in (select distinct eid from genetic_back where " +
                    "dna_origin in (select bid from genetic_back_values where lower(backname) like ?) " +
                    "or targeted_back in (select bid from genetic_back_values where lower(backname) like ?) " +
                    "or host_back in (select bid from genetic_back_values where lower(backname) like ?) " +
                    "or backcrossing_strain in (select bid from genetic_back_values where lower(backname) like ?) " +
                    "or lower(backcrosses) like ?) " +
                    
                    ")");
            
            String search = "%"+keyword.toLowerCase()+"%";
            
            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);
            ps.setString(4, search);
            ps.setString(5, search);
            ps.setString(6, search);
            ps.setString(7, search);
            ps.setString(8, search);
            ps.setString(9, search);
            ps.setString(10, search);
            ps.setString(11, search);
            ps.setString(12, search);
            ps.setString(13, search);
            ps.setString(14, search);
            ps.setString(15, search);
            ps.setString(16, search);
            ps.setString(17, search);
            ps.setString(18, search);
            ps.setString(19, search);
            ps.setString(20, search);
            ps.setString(21, search);
            //ps.setString(22, search);
            
            result = ps.executeQuery();
            
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("eid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ExpModelBean#ejbFindByWebServiceKeywordRequest: Cannot find model by keyword\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    //</editor-fold>
    
    
}
