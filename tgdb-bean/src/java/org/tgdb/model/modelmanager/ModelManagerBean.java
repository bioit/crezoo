package org.tgdb.model.modelmanager;

import org.tgdb.frame.PageManager;
import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.adminmanager.SpeciesDTO;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.ExceptionLogUtil;
import org.tgdb.exceptions.PermissionDeniedException;
import org.tgdb.model.expmodel.ExpModelRemote;
import org.tgdb.model.expmodel.ExpModelRemoteHome;
import org.tgdb.resource.resourcemanager.FileDTO;
import org.tgdb.search.ModelSearchResult;
import org.tgdb.species.gene.GeneRemote;
import org.tgdb.species.gene.GeneRemoteHome;

import org.tgdb.model.geneticbackground.GeneticBackgroundRemote;
import org.tgdb.model.geneticbackground.GeneticBackgroundRemoteHome;
import org.tgdb.model.geneticbackground.GeneticBackgroundValuesRemote;
import org.tgdb.model.geneticbackground.GeneticBackgroundValuesRemoteHome;

import org.tgdb.model.availability.AvailabilityPk;
import org.tgdb.model.availability.AvailabilityRemote;
import org.tgdb.model.availability.AvailabilityRemoteHome;
import org.tgdb.model.availablegeneticbackgrounds.AvailableGeneticBackgroundRemote;
import org.tgdb.model.availablegeneticbackgrounds.AvailableGeneticBackgroundRemoteHome;
import org.tgdb.model.repositories.RepositoriesRemote;
import org.tgdb.model.repositories.RepositoriesRemoteHome;

import org.tgdb.model.reference.ReferenceRemote;
import org.tgdb.model.reference.ReferenceRemoteHome;
import org.tgdb.model.researchapplication.ResearchApplicationRemote;
import org.tgdb.model.researchapplication.ResearchApplicationRemoteHome;
import org.tgdb.model.strain.allele.StrainAlleleRemote;
import org.tgdb.model.strain.allele.StrainAlleleRemoteHome;
import org.tgdb.model.strain.mutationtype.MutationTypeRemote;
import org.tgdb.model.strain.mutationtype.MutationTypeRemoteHome;
import org.tgdb.model.strain.state.StrainStateRemote;
import org.tgdb.model.strain.state.StrainStateRemoteHome;
import org.tgdb.model.strain.strain.StrainRemote;
import org.tgdb.model.strain.strain.StrainRemoteHome;
import org.tgdb.model.strain.type.StrainTypeRemote;
import org.tgdb.model.strain.type.StrainTypeRemoteHome;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.project.user.UserRemote;
import org.tgdb.resource.file.FileRemote;
import org.tgdb.resource.file.FileRemoteHome;
import org.tgdb.resource.link.LinkRemote;
import org.tgdb.resource.link.LinkRemoteHome;
import org.tgdb.resource.resource.ResourceRemote;
import org.tgdb.resource.resourcemanager.ResourceManagerRemote;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemote;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteHome;
import org.tgdb.search.GeneSearchResult;
import org.tgdb.search.Keyword;
import org.tgdb.search.ResearchApplicationSearchResult;
import org.tgdb.servicelocator.ServiceLocator;
import org.tgdb.species.chromosome.ChromosomeRemote;
import org.tgdb.species.chromosome.ChromosomeRemoteHome;
import org.tgdb.species.species.SpeciesRemote;
import org.tgdb.species.species.SpeciesRemoteHome;
import org.tgdb.expression.expressionmodel.ExpressionModelRemote;
import org.tgdb.expression.expressionmodel.ExpressionModelRemoteHome;
import org.tgdb.genome.integrationcopy.IntegrationCopyRemote;
import org.tgdb.genome.integrationcopy.IntegrationCopyRemoteHome;

import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

//import java.lang.Character;

import java.util.Map;
import org.tgdb.dto.OlsDTO;

import org.tgdb.dtos.*;
import uk.ac.ebi.ook.web.services.Query;
import uk.ac.ebi.ook.web.services.QueryService;
import uk.ac.ebi.ook.web.services.QueryServiceLocator;

public class ModelManagerBean extends AbstractTgDbBean implements javax.ejb.SessionBean, org.tgdb.model.modelmanager.ModelManagerRemoteBusiness {
    private javax.ejb.SessionContext context;
    
    private ExpModelRemoteHome modelHome;
    private SamplingUnitRemoteHome samplingUnitHome;
    private ResearchApplicationRemoteHome researchAppHome;
    private ResourceManagerRemote resourceManager;
    private FileRemoteHome fileHome;
    private LinkRemoteHome linkHome;
    private GeneRemoteHome geneHome;
    private ReferenceRemoteHome referenceHome;
    //private UserRemoteHome userHome;
    private ProjectRemoteHome projectHome;
    private StrainRemoteHome strainHome;
    private StrainTypeRemoteHome strainTypeHome;
    private StrainStateRemoteHome strainStateHome;
    private GeneticBackgroundRemoteHome genbackHome;
    private MutationTypeRemoteHome mutationTypeHome;
    private GeneticBackgroundValuesRemoteHome genbackValuesHome;
    private StrainAlleleRemoteHome strainAlleleHome;
    private AvailabilityRemoteHome availabilityHome;
    private AvailableGeneticBackgroundRemoteHome avgenbackHome;
    private RepositoriesRemoteHome repositoriesHome;
    private ChromosomeRemoteHome chromosomeHome;
    private SpeciesRemoteHome speciesHome;
    private ExpressionModelRemoteHome expressionHome;
    private IntegrationCopyRemoteHome icHome;
   
    private Collection modelsTMP;
    private int modelsTMPsize;
    
    private Collection genesTMP;
    private int genesTMPsize;

//    private Query ols_query;
//    private Map term_maps = new HashMap();
//    private String[] ontologies = {"EMAP", "MA"};

    // <editor-fold  defaultstate="collapsed">
    /**
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(javax.ejb.SessionContext aContext) {
        this.context = aContext;
    }
    
    /**
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() {
        
    }
    
    /**
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    public void ejbPassivate() {
        
    }
    
    /**
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    public void ejbRemove() {
        
    }
    // </editor-fold>
    
    public void ejbCreate() {
        modelHome = (ExpModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPMODEL);
        fileHome = (FileRemoteHome)locator.getHome(ServiceLocator.Services.FILE);
        //userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
        linkHome = (LinkRemoteHome)locator.getHome(ServiceLocator.Services.LINK);
        samplingUnitHome = (SamplingUnitRemoteHome)locator.getHome(ServiceLocator.Services.SAMPLINGUNIT);
        researchAppHome = (ResearchApplicationRemoteHome)locator.getHome(ServiceLocator.Services.RESEARCHAPPLICATION);
        geneHome = (GeneRemoteHome)locator.getHome(ServiceLocator.Services.GENE);
        referenceHome = (ReferenceRemoteHome)locator.getHome(ServiceLocator.Services.REFERENCE);
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);
        strainHome = (StrainRemoteHome)locator.getHome(ServiceLocator.Services.STRAIN);
        strainTypeHome = (StrainTypeRemoteHome)locator.getHome(ServiceLocator.Services.STRAIN_TYPE);
        strainStateHome = (StrainStateRemoteHome)locator.getHome(ServiceLocator.Services.STRAIN_STATE);
        genbackHome = (GeneticBackgroundRemoteHome)locator.getHome(ServiceLocator.Services.GENETIC_BACKGROUND);
        mutationTypeHome = (MutationTypeRemoteHome)locator.getHome(ServiceLocator.Services.MUTATION_TYPE);
        genbackValuesHome = (GeneticBackgroundValuesRemoteHome)locator.getHome(ServiceLocator.Services.GENETIC_BACKGROUND_VALUES);
        strainAlleleHome = (StrainAlleleRemoteHome)locator.getHome(ServiceLocator.Services.STRAIN_ALLELE);
        availabilityHome = (AvailabilityRemoteHome)locator.getHome(ServiceLocator.Services.AVAILABILITY);
        avgenbackHome = (AvailableGeneticBackgroundRemoteHome)locator.getHome(ServiceLocator.Services.AVAILABLE_GENETIC_BACKGROUNDS);
        repositoriesHome = (RepositoriesRemoteHome)locator.getHome(ServiceLocator.Services.REPOSITORIES);
        chromosomeHome = (ChromosomeRemoteHome)locator.getHome(ServiceLocator.Services.CHROMOSOME);
        resourceManager = (ResourceManagerRemote)locator.getManager(ServiceLocator.Services.RESOURCEMANAGER);         
        speciesHome = (SpeciesRemoteHome)locator.getHome(ServiceLocator.Services.SPECIES);
        expressionHome = (ExpressionModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPRESSION_MODEL);
        icHome = (IntegrationCopyRemoteHome)locator.getHome(ServiceLocator.Services.INTEGRATION_COPY);
    }

    //ols functions
    //<editor-fold defaultstate="collapsed">
    
    public Collection getTermsByOntology(String ontology_name) throws ApplicationException {
        Collection arr = new ArrayList();
        try {
            QueryService ols = (QueryService) new QueryServiceLocator();
            Query ols_query = ols.getOntologyQuery();
            Map map = ols_query.getOntologyNames();
//            Map map = ols_query.getRootTerms(ontology_name);
//            Map map = ols_query.getAllTermsFromOntology(ontology_name);
            Iterator i = map.keySet().iterator();
            int oid = 1;
            while(i.hasNext()) {
                String key = (String) i.next();
                String name =(String) map.get(key);
                OlsDTO dto = new  OlsDTO();
//                dto.setOid(oid);
                dto.setNamespace(key);
                dto.setName(name);
                arr.add(dto);
                oid++;
            }
        } catch(Exception e){
            logger.error(e.getMessage());
        }
        return arr;
    }

    private String getTermById(String term_id, String namespace) throws ApplicationException {
        String term_name = "undefined";
        try {
            QueryService ols = (QueryService) new QueryServiceLocator();
            Query ols_query = ols.getOntologyQuery();
            term_name = ols_query.getTermById(term_id, namespace);
        }
        catch(Exception e) {
            logger.error(getStackTrace(e));
        }
        return term_name;
    }
    //</editor-fold>

    //model(s) functions
    //<editor-fold defaultstate="collapsed">
    public Collection getExperimentalModels(int suid, TgDbCaller caller, PageManager pageManager) throws ApplicationException {
        try {                     
            SamplingUnitRemote samplingUnit = samplingUnitHome.findByPrimaryKey(new Integer(suid));
            
            // Fetch the remote interfaces
            Collection models = samplingUnit.getExperimentalModels();
            Collection modelsDTOs = new ArrayList();
            Iterator i = models.iterator();
            
            int start = pageManager.getStart();
            int stop = pageManager.getStop();
            int index = 0;
            
            // Create data transfer objects
            while (i.hasNext()) {
                index++;
                
                if (index>=start && index<=stop) {
                    modelsDTOs.add(new ExpModelDTO((ExpModelRemote)i.next()));
                } else {
                    // Return if we have enough data
                    if(modelsDTOs.size() == pageManager.getDelta())
                        return modelsDTOs;       
                    // Skip this object. This is outside the interval
                    i.next();
                }              
            }                        
            
            // Return the data transfer objects
            return modelsDTOs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get models.");
        }
    }

    /*
     * must be removed
     */
    public Collection getExperimentalModelsForBackcrossingListGeneration(TgDbCaller caller) throws ApplicationException {
        try {
            Collection models = modelHome.findByBackrossingListGeneration(caller);
            Collection modelsDTOs = new ArrayList();
            Iterator i = models.iterator();
           
            while (i.hasNext()) {
                modelsDTOs.add(new ExpModelDTO((ExpModelRemote)i.next()));
            }
            
            // Return the data transfer objects
            return modelsDTOs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get models for backcrossing list generation.");
        }
    }
    /*
     * must be removed
     */
    public Collection getExperimentalModelsForIMSR(int suid, TgDbCaller caller) throws ApplicationException {
        try {
            Collection models = modelHome.findByIMSRSubmission(suid, caller);
            Collection modelsDTOs = new ArrayList();
            Iterator i = models.iterator();
            
            while (i.hasNext()) {
                modelsDTOs.add(new ExpModelDTO((ExpModelRemote)i.next()));              
            }                        
            
            // Return the data transfer objects
            return modelsDTOs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get models.");
        }
    }
    
    public Collection getExperimentalModelsToIMSRTable(Collection models,int suid) throws ApplicationException {
        try {
            int tmpInt = 0;
            Collection modelsDTOs = new ArrayList();
            Iterator i = models.iterator();
            ExpModelRemote tmpModel = null;
            ExpModelDTO tmpDTO = null;
            while (i.hasNext()) {
                tmpDTO = (ExpModelDTO)i.next();
                tmpModel = modelHome.findByPrimaryKey(new Integer(tmpDTO.getEid()));
                tmpInt = tmpModel.IMSRSubmit(tmpDTO.getEid());
                //if (tmpInt==1){
                    modelsDTOs.add(tmpDTO);
                //}
            }                        
            
            // Return the data transfer objects
            return modelsDTOs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("getExperimentalModelsToIMSRTable failed dramatically.");
        }
    }
    
    public Collection getExperimentalModelsByForm(FormDataManager fdm, TgDbCaller caller, PageManager pageManager) throws ApplicationException {
        try {
            Collection models = modelHome.findByFormDataManager(fdm, caller);
            Collection modelsDTOs = new ArrayList();
            Iterator i = models.iterator();
            
            int start = pageManager.getStart();
            int stop = pageManager.getStop();
            int index = 0;
            
            while (i.hasNext()) {
                index++;
                
                if (index>=start && index<=stop) {
                    modelsDTOs.add(new ExpModelDTO((ExpModelRemote)i.next()));
                } else {
                    // Return if we have enough data
                    if(modelsDTOs.size() == pageManager.getDelta())
                        return modelsDTOs;
                    // Skip this object. This is outside the interval
                    i.next();
                }
            }
            
            // Return the data transfer objects
            return modelsDTOs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get models.");
        }
    }
    
    public int getExperimentalModelsByForm(FormDataManager fdm, TgDbCaller caller) throws ApplicationException {
        try {
            Collection models = modelHome.findByFormDataManager(fdm, caller);
            this.modelsTMP = models;
            this.modelsTMPsize = modelsTMP.size();
            return modelsTMPsize;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("getExperimentalModelsByForm: Failed to get models.");
        }
    }
    
    public Collection getExperimentalModelsByPGM(PageManager pageManager) throws ApplicationException {
        try {
            Collection modelsDTOs = new ArrayList();
            Iterator i = this.modelsTMP.iterator();
            
            int start = pageManager.getStart();
            int stop = pageManager.getStop();
            int index = 0;
            
            while (i.hasNext()) {
                
                index++;
                
                if (index>=start && index<=stop) {
                    modelsDTOs.add(new ExpModelDTO((ExpModelRemote)i.next()));
                } else {
                    if(modelsDTOs.size() == pageManager.getDelta())
                        return modelsDTOs;
                    i.next();
                }
            }
            
            // Return the data transfer objects
            return modelsDTOs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("getExperimentalModelsByPGM: Failed to get models.");
        }
    }
    
    public Collection getExperimentalModelsByFormNoDelta(FormDataManager fdm, TgDbCaller caller, PageManager pageManager) throws ApplicationException {
        try {
            Collection models = modelHome.findByFormDataManager(fdm, caller);
            Collection modelsDTOs = new ArrayList();
            Iterator i = models.iterator();
            
            int start = pageManager.getStart();
            int stop = pageManager.getStop();
            int index = 0;
            
            while (i.hasNext()) {
                modelsDTOs.add(new ExpModelDTO((ExpModelRemote)i.next()));
            }
            
            // Return the data transfer objects
            return modelsDTOs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get models.");
        }
    }
 
    public Collection getExperimentalModelsByFormForDissUpdate(FormDataManager fdm, TgDbCaller caller, PageManager pageManager) throws ApplicationException {
        try {
            Collection models = modelHome.findByFormDataManagerForDissUpdate(fdm, caller);
            Collection modelsDTOs = new ArrayList();
            Iterator i = models.iterator();
            
            int start = pageManager.getStart();
            int stop = pageManager.getStop();
            int index = 0;
            
            while (i.hasNext()) {
                index++;
                
                if (index>=start && index<=stop) {
                    modelsDTOs.add(new ExpModelDTO((ExpModelRemote)i.next()));
                } else {
                    // Return if we have enough data
                    if(modelsDTOs.size() == pageManager.getDelta())
                        return modelsDTOs;
                    // Skip this object. This is outside the interval
                    i.next();
                }
            }
            
            // Return the data transfer objects
            return modelsDTOs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get models.");
        }
    }
    
    public Collection getExperimentalModelsByFormForDissUpdateNoDelta(FormDataManager fdm, TgDbCaller caller, PageManager pageManager) throws ApplicationException {
        try {
            Collection models = modelHome.findByFormDataManagerForDissUpdate(fdm, caller);
            Collection modelsDTOs = new ArrayList();
            Iterator i = models.iterator();
            
            int start = pageManager.getStart();
            int stop = pageManager.getStop();
            int index = 0;
            
            while (i.hasNext()) {
                modelsDTOs.add(new ExpModelDTO((ExpModelRemote)i.next()));
            }
            
            // Return the data transfer objects
            return modelsDTOs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get models.");
        }
    }
    
    public ExpModelDTO getExperimentalModel(int eid, TgDbCaller caller, String superscript) throws ApplicationException {
        try {                                
            // Fetch the remote interface
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            
            // Create data transfer object
            ExpModelDTO dto = new ExpModelDTO(model);
            
            // Return the data transfer object
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get model.");
        }
    }
    
    public ExpModelDTO getExperimentalModel(int eid, TgDbCaller caller) throws ApplicationException {
        try {                                
            // Fetch the remote interface
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            
            // Create data transfer object
            ExpModelDTO dto = new ExpModelDTO(model);
            
            // Return the data transfer object
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get model.");
        }
    }

    public int getNumberOfExperimentalModels(int suid, TgDbCaller caller) throws ApplicationException {
        try {
            SamplingUnitRemote samplingUnit = samplingUnitHome.findByPrimaryKey(new Integer(suid));
            samplingUnit.setCaller(caller);
            return samplingUnit.getNumberOfExperimentalModels();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get number of models.");
        }
    }
    
    public void createModel(int suid, String alias, String geneticBackground, String availability, int type, String researchApplications, int contact, org.tgdb.TgDbCaller caller, String comm, String desired_level, String donating_investigator, String inducible, String former_names) throws ApplicationException {
        ExpModelRemote model = null;
        SamplingUnitRemote samplingUnit = null;
        ResearchApplicationRemote researchApplication = null;
        UserRemote contactUser = null;
        StrainRemote strain = null;
        try 
        {
            makeConnection();
            int eid = getIIdGenerator().getNextId(conn, "expobj_seq");
            samplingUnit = samplingUnitHome.findByPrimaryKey(new Integer(suid));
            
            researchApplication = researchAppHome.findByPrimaryKey(new Integer(type));
            
            contactUser = userHome.findByPrimaryKey(new Integer(contact));
            
            model = modelHome.create(eid, ""+eid, samplingUnit, caller);
            model.setCaller(caller);
            model.setAlias(alias);
            model.setComm(comm);
            model.setContact(contactUser);
            model.setAvailability(availability);
            model.setResearchApplicationText(researchApplications);
            model.setResearchApplication(researchApplication);
            model.setGeneticBackground(geneticBackground);
            model.setDonating_investigator(donating_investigator);
            model.setInducible(inducible);
            model.setFormer_names(former_names);
            
            if (desired_level.equals("Public"))
                model.setDesiredLevel(0);
            else if (desired_level.equals("Mugen"))
                model.setDesiredLevel(1);
            else if (desired_level.equals("Admin"))
                model.setDesiredLevel(2);
            
//            int strainId = getIIdGenerator().getNextId(conn, "strain_seq");
//            strain = strainHome.create(strainId, "", caller);
//            model.setStrain(strain);
            
        }
        catch (FinderException e)
        {
            logger.error("---------------------------------------->ModelManagerBean#createModel: FinderException then creating model", e);
            
            if (samplingUnit == null)
                throw new ApplicationException("Could not create model: Sampling Unit "+suid+" not found");
            if (researchApplication == null)
                throw new ApplicationException("Could not create model: Research Application "+type+" not found");
            if (contactUser == null)
                throw new ApplicationException("Could not create model: Contact user not found");
        }
        catch (CreateException e)
        {
            logger.error("---------------------------------------->ModelManagerBean#createModel: CreateException then creating model", e);
            
            if (model==null)
                throw new ApplicationException("Could not create model");
            if (strain == null)
                throw new ApplicationException("Could not create strain");
        }
        catch (RemoteException e)
        {
            logger.error("---------------------------------------->ModelManagerBean#createModel: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (ApplicationException e) 
        {
            logger.error("---------------------------------------->ModelManagerBean#createModel: Failed to create new model", e);
            throw new ApplicationException("Could not create new model: "+e.getMessage());            
        } 
        finally 
        {
            releaseConnection();
        } 
    }
    
    public int createModelAutomatic(String strain_id, String strain_designation, org.tgdb.TgDbCaller caller) throws ApplicationException {
        ExpModelRemote model = null;
        SamplingUnitRemote samplingUnit = null;
        ResearchApplicationRemote researchApplication = null;
        UserRemote contactUser = null;
        StrainRemote strain = null;
        
        //default sampling unit id
        int suid = 1003;
        String geneticBackground = "n.a.";
        String availability = "n.a.";
        String researchApplications = "";
        //research application type is transgenic tool
        int type = 9001; 
        //default contact person is admin
        int contact = 1001;
        String comm = "automatically imported model from the imsr vaults";
        int to_return = 0;
        
        try {
            makeConnection();
            //get the id
            int eid = getIIdGenerator().getNextId(conn, "expobj_seq");
            //get sampling unit
            samplingUnit = samplingUnitHome.findByPrimaryKey(new Integer(suid));
            //get research app
            researchApplication = researchAppHome.findByPrimaryKey(new Integer(type));
            //get user
            contactUser = userHome.findByPrimaryKey(new Integer(contact));
            //create model
            model = modelHome.create(eid, strain_id, samplingUnit, caller);
            //add to model...
            model.setCaller(caller);
            model.setAlias(strain_designation);
            model.setComm(comm);
            model.setContact(contactUser);
            model.setAvailability(availability);
            model.setResearchApplicationText(researchApplications);
            model.setResearchApplication(researchApplication);
            model.setGeneticBackground(geneticBackground);
            
            model.setDesiredLevel(0);
            model.setLevel(0);
            
            //create the strain
            int strainId = getIIdGenerator().getNextId(conn, "strain_seq");
            strain = strainHome.create(strainId, strain_designation, caller);

            //ZOUB FIX - Find workaround for this
//            strain.setMgiId(strain_id);
            
            to_return = eid;
        }
        catch (FinderException e)
        {
            logger.error("---------------------------------------->ModelManagerBean#createModelAutomatic: FinderException when creating model", e);
            
            if (samplingUnit == null)
                throw new ApplicationException("Could not create model: Sampling Unit "+suid+" not found");
            if (researchApplication == null)
                throw new ApplicationException("Could not create model: Research Application "+type+" not found");
            if (contactUser == null)
                throw new ApplicationException("Could not create model: Contact user not found");
        }
        catch (CreateException e)
        {
            logger.error("---------------------------------------->ModelManagerBean#createModelAutomatic: CreateException then creating model", e);
            
            if (model==null)
                throw new ApplicationException("Could not create model");
            if (strain == null)
                throw new ApplicationException("Could not create strain");
        }
        catch (RemoteException e)
        {
            logger.error("---------------------------------------->ModelManagerBean#createModelAutomatic: Remote exception", e);
            throw ExceptionLogUtil.createLoggableEJBException(e);
        }
        catch (ApplicationException e) 
        {
            logger.error("---------------------------------------->ModelManagerBean#createModelAutomatic: Failed to create new model", e);
            throw new ApplicationException("Could not create new model: "+e.getMessage());            
        } 
        finally 
        {
            releaseConnection();
        }
        return to_return;
    }

    public void updateModel(int suid, int eid, java.lang.String alias, java.lang.String geneticBackground, java.lang.String availability, int type, java.lang.String researchApplications, int contact, org.tgdb.TgDbCaller caller, String comm, String level, String desired_level, String donating_investigator, String inducible, String former_names) throws ApplicationException {
        validate("MODEL_W", caller);
        try {
            makeConnection();

            SamplingUnitRemote samplingUnit = samplingUnitHome.findByPrimaryKey(new Integer(suid));
            ResearchApplicationRemote researchApplication = researchAppHome.findByPrimaryKey(new Integer(type));            
            UserRemote contactUser = userHome.findByPrimaryKey(new Integer(contact));            
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            
            
            /** Check for the models sampling unit */
            Collection projects = model.getSamplingUnit().getProjects();
            validate("MODEL_W", caller, projects);
            
            model.setCaller(caller);
            model.setAlias(alias);
            model.setComm(comm);
            model.setSuid(suid);
            model.setContact(contactUser);
            model.setAvailability(availability);
            model.setResearchApplicationText(researchApplications);
            model.setResearchApplication(researchApplication);
            model.setGeneticBackground(geneticBackground);
            model.setDonating_investigator(donating_investigator);
            model.setInducible(inducible);
            model.setFormer_names(former_names);
            
            if (level.equals("Public"))
                model.setLevel(0);
            else if (level.equals("Mugen"))
                model.setLevel(1);
            else if (level.equals("Admin"))
                model.setLevel(2);
            
            if (desired_level.equals("Public"))
                model.setDesiredLevel(0);
            else if (desired_level.equals("Mugen"))
                model.setDesiredLevel(1);
            else if (desired_level.equals("Admin"))
                model.setDesiredLevel(2);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not update model\n"+e.getMessage(),e);            
        } finally {
            releaseConnection();
        } 
    }

    public void removeModel(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {           
            validate("MODEL_W", caller);
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));

            //FIXME!!! - Is this fix ok???
//            Iterator i_strainAlleles = model.getStrain().getStrainAlleles().iterator();
            Iterator i_strainAlleles = strainAlleleHome.findByStrain(eid, caller).iterator();
            while (i_strainAlleles.hasNext())
            {
                StrainAlleleRemote sta = (StrainAlleleRemote)i_strainAlleles.next();
//                sta.remove();
                sta.unassign(eid);
            }    
            
            Iterator i = model.getGeneAffected().iterator();
            while (i.hasNext())
            {
                GeneRemote ga = (GeneRemote)i.next();
                model.removeGene(ga);
            }    
//            model.getStrain().remove();
            model.remove();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove model\n"+e.getMessage());            
        }
    }
    
    public void updateRecombinationEfficiencyModel(int eid, java.lang.String researchApplications, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("MODEL_W", caller);
        try {
            makeConnection();

            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            
            
            validate("MODEL_W", caller);
            
            model.setCaller(caller);
            model.setResearchApplicationText(researchApplications);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not update model's recombination efficiency. \n"+e.getMessage(),e);            
        } finally {
            releaseConnection();
        } 
    }
    
    public void updateAuthorsCommentModel(int eid, java.lang.String comm, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("MODEL_W", caller);
        try {
            makeConnection();

            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            
            
            validate("MODEL_W", caller);
            
            model.setCaller(caller);
            model.setComm(comm);
            //model.setResearchApplicationText(comm);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not update model's author's coment. \n"+e.getMessage(),e);            
        } finally {
            releaseConnection();
        } 
    }
    
    public Collection searchModelByKeyword(Keyword keyword, TgDbCaller caller) throws ApplicationException{
        Collection arr = new TreeSet();
        try
        {
            Collection models = modelHome.findByKeyword(keyword, caller);
            Iterator i = models.iterator();
            while (i.hasNext())
            {
                ExpModelRemote model = (ExpModelRemote)i.next();
                arr.add(new ModelSearchResult(model,"Controller?workflow=ViewModel&amp;expand_all=true&amp;name_begins=model.block&amp;general=true&amp;eid="+model.getEid()));
            }
        }
        catch (FinderException fe)
        {
            fe.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("failed to search by research application",e);
        }
        return arr;
    }
    
    public Collection getModelsByGene(int gid, TgDbCaller caller) throws ApplicationException{
        try {                                            
            GeneRemote gene = geneHome.findByPrimaryKey(new Integer(gid));
            gene.setCaller(caller);
            Collection models = gene.getModels();
            
            Collection dtos = new ArrayList();
            Iterator i = models.iterator();
            while(i.hasNext()) {                
                dtos.add(new ExpModelDTO((ExpModelRemote)i.next()));
            }
            
            logger.debug("---------------------------------------->ModelManagerBean#getModelsByGene: Models size = "+models.size());
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genes affected.", e);
        }
    }
    
    public void addExpressionModelToModel(int eid, int exid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            ExpressionModelRemote expression = expressionHome.findByPrimaryKey(new Integer(exid));
            model.addExpressionModel(expression);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add expression model to model number "+eid+" \n"+e.getMessage(),e);            
        }
    }
    
    public void addIntegrationCopyToModel(int eid, int iscmid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            IntegrationCopyRemote ic = icHome.findByPrimaryKey(new Integer(iscmid));
            model.addIntegrationCopy(ic);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add integration copy data set to model number "+eid+" \n"+e.getMessage(),e);            
        }
    }
    
    public Collection getModelsByStrainAllele(int strain_allele, TgDbCaller caller) throws ApplicationException{
        Collection dtos = new ArrayList();
        try {                                            
//            GeneRemote gene = geneHome.findByPrimaryKey(new Integer(strain_allele));
//            gene.setCaller(caller);
            Collection models = modelHome.findByStrainAllele(strain_allele, caller);
            
            Iterator i = models.iterator();
            while(i.hasNext()) {                
                dtos.add(new ExpModelDTO((ExpModelRemote)i.next()));
            }
            
//            logger.debug("---------------------------------------->ModelManagerBean#getModelsByGene: Models size = "+models.size());
            
        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ApplicationException("Failed to get genes affected.", e);
            logger.error(e);
        }
        return dtos;
    }
    //</editor-fold>

    //research application(s) functions
    //<editor-fold defaultstate="collapsed">
    public Collection getResearchApplications(org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {                                
            Collection resapps = researchAppHome.findByProject(caller.getPid());
            Collection resappDTOs = new ArrayList();
            Iterator i = resapps.iterator();
            while(i.hasNext()) {
                resappDTOs.add(new ResearchAppDTO((ResearchApplicationRemote)i.next()));
            }
            
            return resappDTOs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get research applications.");
        }
    }
    
    public ResearchAppDTO getResearchApplication(int raid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {                                
            return new ResearchAppDTO(researchAppHome.findByPrimaryKey(new Integer(raid)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get research application (raid="+raid+").");
        }
    }
    
    public Collection getAllResearchApplications(TgDbCaller caller) throws ApplicationException{
        try {                                
            Collection resapps = researchAppHome.findByName("%");
            Collection resappDTOs = new ArrayList();
            Iterator i = resapps.iterator();
            while(i.hasNext()) {
                ResearchApplicationRemote ra = (ResearchApplicationRemote)i.next();
                resappDTOs.add(new ResearchAppDTO(ra));
            }
            
            return resappDTOs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get research applications.");
        }
    }
    
    public Collection searchByResearchApplication(String name, TgDbCaller caller) throws ApplicationException{
        Collection arr = new ArrayList();
        try
        {
            Collection apps = researchAppHome.findByName(name);
            Iterator ia = apps.iterator();
            while (ia.hasNext())
            {
                try
                {
                    ResearchApplicationRemote ra = (ResearchApplicationRemote)ia.next();

                    validatePid("MODEL_R", caller, ra.getProject().getPid());

                    Collection models = ra.getModels();
                    Iterator im = models.iterator();
                    while (im.hasNext())
                    {
                        ExpModelRemote model = (ExpModelRemote)im.next();
                        arr.add(new ExpModelDTO(model));
                    }
                }
                catch (PermissionDeniedException pde)
                {
                    logger.error("---------------------------------------->ModelManagerBean#searchByResearchApplication: Permission denied");
                }
            }
        }
        catch (FinderException fe)
        {
            fe.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("failed to search by research application",e);
        }
        return arr;
    }
    
    public Collection searchResearchApplicationByKeyword(Keyword keyword, TgDbCaller caller) throws ApplicationException{
        Collection arr = new TreeSet();
        try
        {
            Collection rapps = researchAppHome.findByKeyword(keyword, caller);
            Iterator i = rapps.iterator();
            while (i.hasNext())
            {
                ResearchApplicationRemote ra = (ResearchApplicationRemote)i.next();
                arr.add(new ResearchApplicationSearchResult(ra,"Controller?workflow=ViewResearchApp&raid="+ra.getRaid()));
            }            
        }
        catch (FinderException fe)
        {
            fe.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("failed to search keyword",e);
        }
        return arr;
    }
    
    public int createResearchApplication(String name, String comment, TgDbCaller caller) throws ApplicationException{
        try
        {
            makeConnection();
            int raid = getIIdGenerator().getNextId(conn, "research_application_seq");
            researchAppHome.create(name, comment, caller.getPid() ,raid,  caller);
            return raid;
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to create research application",e);
        }
        finally
        {
            releaseConnection();
        }
    }
    
    public void updateResearchApplication(int raid, String name, String comment, TgDbCaller caller) throws ApplicationException{
        try
        {
            ResearchApplicationRemote rapp = researchAppHome.findByPrimaryKey(new Integer(raid));
            rapp.setCaller(caller);
            rapp.setName(name);
            rapp.setComm(comment);
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to update research application",e);
        }
    }
    
    public void removeResearchApplication(int raid, TgDbCaller caller) throws ApplicationException{
        try
        {
            validate("MODEL_W", caller);
            ResearchApplicationRemote rapp = researchAppHome.findByPrimaryKey(new Integer(raid));
            rapp.remove();
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to remove research application",e);
        }
    }

    //</editor-fold>
   
    //handling+genotyping functions
    //<editor-fold defaultstate="collapsed">
//    public void addHandlingFile(int eid, FileDataObject fileData, org.tgdb.TgDbCaller caller) throws ApplicationException {
//        validate("FILE_W", caller);
//        try {
//            int fileid = resourceManager.saveFile(fileData.getFileName(), "Handling instructions for eid="+eid, caller, fileData).getFileId();
//            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
//            model.setHandlingFile(fileid);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ApplicationException("Could not add file\n"+e.getMessage());
//        }
//    }

//    public void addGenotypingFile(int eid, FileDataObject fileData, org.tgdb.TgDbCaller caller) throws ApplicationException {
//        validate("FILE_W", caller);
//        try {
//            int fileid = resourceManager.saveFile(fileData.getFileName(), "Genotyping instructions for eid="+eid, caller, fileData).getFileId();
//            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
//            model.setGenotypingFile(fileid);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ApplicationException("Could not add file\n"+e.getMessage());
//        }
//    }

//    public void removeGenotypingFile(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException {
//        validate("FILE_W", caller);
//        try {
//            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
//            //model.setCaller(caller);
//            FileRemote file = model.getGenotypingFile();
//            int fileId = file.getFileId();
//            //model.setGenotypingFile(0);
//            resourceManager.removeFile(fileId, caller);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ApplicationException("Could not remove genotyping file\n"+e.getMessage());
//        }
//    }

//    public void removeHandlingFile(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException {
//        validate("FILE_W", caller);
//        try {
//            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
//            FileRemote file = model.getHandlingFile();
//            //model.setHandlingFile(0);
//            resourceManager.removeFile(file.getFileId(), caller);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ApplicationException("Could not remove handling file\n"+e.getMessage());
//        }
//    }
    //</editor-fold>

    //gene(s) functions
    //<editor-fold defaultstate="collapsed">
    public Collection getGenesByModel(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {                                            
            Collection genes = geneHome.findByModel(eid);
            Collection dtos = new ArrayList();
            Iterator i = genes.iterator();
            while(i.hasNext()) {                
                dtos.add(new GeneDTO((GeneRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genes affected.", e);
        }
    }

    public Collection getGenesByModelAndDistinguish(int eid, String distinguish, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            Collection genes = geneHome.findByModelAndDistinguish(eid, distinguish);
            Collection dtos = new ArrayList();
            Iterator i = genes.iterator();
            while(i.hasNext()) {
                dtos.add(new GeneDTO((GeneRemote)i.next()));
            }

            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genes affected.", e);
        }
    }

    public Collection getGenesByDistinguish(String distinguish, TgDbCaller caller) throws ApplicationException{
        Collection dtos = new ArrayList();
        try {
            Collection genes = geneHome.findByDistinguish(distinguish);
            Iterator i = genes.iterator();
            while(i.hasNext()) {
                dtos.add(new GeneDTO((GeneRemote)i.next()));
            }
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return dtos;
    }
   
    public Collection getGenesByProject(int pid, TgDbCaller caller) throws ApplicationException{
        try {                                            
            Collection genes = geneHome.findByProject(pid, caller);
            Collection dtos = new ArrayList();
            GeneDTO tmpGene = null;
            Iterator i = genes.iterator();
            while(i.hasNext()) {
                tmpGene = new GeneDTO((GeneRemote)i.next());
                if (tmpGene.getName().compareTo("Unknown")!=0){
                    dtos.add(tmpGene);
                }
                //dtos.add(new GeneDTO((GeneRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genes affected.", e);
        }
    }
    
    public int getGenes(int pid, TgDbCaller caller) throws ApplicationException{
        try {                                            
            Collection genes = geneHome.findByProject(pid, caller);
            this.genesTMP = genes;
            this.genesTMPsize = this.genesTMP.size();
            return genesTMPsize;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("getGenes: Failed to get genes.", e);
        }
    }
    
    public Collection getGenesByPGM(PageManager pageManager) throws ApplicationException{
        try {                                            
            Collection dtos = new ArrayList();
            Iterator i = this.genesTMP.iterator();
            
            int start = pageManager.getStart();
            int stop = pageManager.getStop();
            int index = 0;
            
            while(i.hasNext()) {
                index++;
                
                if (index>=start && index<=stop) {
                dtos.add(new GeneDTO((GeneRemote)i.next()));
                }
                else {
                    if(dtos.size() == pageManager.getDelta())
                        return dtos;
                    i.next();
                }
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("getGenesByPGM: Failed to get genes.", e);
        }
    }
    
    public Collection getGenesForTransgenicMice(int pid, TgDbCaller caller) throws ApplicationException{
        try {                                            
            Collection genes = geneHome.findByProject(pid, caller);
            Collection dtos = new ArrayList();
            GeneDTO tmpGene = null;
            Iterator i = genes.iterator();
            while(i.hasNext()) { 
                tmpGene = new GeneDTO((GeneRemote)i.next());
                if (tmpGene.getGenesymbol().compareTo("cre")==0 || tmpGene.getGenesymbol().compareTo("rtTA")==0 || tmpGene.getGenesymbol().compareTo("flp")==0){
                    dtos.add(tmpGene);
                }
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genes affected.", e);
        }
    }
    
    public int getGeneAssignmentForTransgenicModel(int eid, int gaid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {                                            
            Collection genes = geneHome.findByModel(eid);
            Collection dtos = new ArrayList();
            Iterator i = genes.iterator();
            while(i.hasNext()) {                
                GeneRemote tmp = (GeneRemote)i.next();
                if(tmp.getGaid()==gaid){
                    return 1;
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to check if gene for transgenic model is already assigned to model.", e);
        }
    }
    
    public Collection getGenesByProjectForNavTag(int pid, TgDbCaller caller, PageManager pageManager) throws ApplicationException{
        try {      
            
            Collection genes = geneHome.findByProject(pid, caller);
            Collection dtos = new ArrayList();
            Iterator i = genes.iterator();
            
            int start = pageManager.getStart();
            int stop = pageManager.getStop();
            int index = 0;
            
            while(i.hasNext()) {
                index++;
                
                if (index>=start && index<=stop) {
                dtos.add(new GeneDTO((GeneRemote)i.next()));
                }
                else {
                    // Return if we have enough data
                    if(dtos.size() == pageManager.getDelta())
                        return dtos;
                    // Skip this object. This is outside the interval
                    i.next();
                }
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genes affected.", e);
        }
    }
    
    public Collection getUnassignedGenes(int eid,int pid, String distinguish, TgDbCaller caller) throws ApplicationException{
        try {                                            
            Collection genes = geneHome.findUnassignedGenes(eid,pid, distinguish);
            Collection dtos = new ArrayList();
            Iterator i = genes.iterator();
            while(i.hasNext()) {                
                dtos.add(new GeneDTO((GeneRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get the genes not affected to the model.", e);
        }
    }
    
    public Collection getUnassignedGenesForTransgenic(int eid,int strainid, int pid, TgDbCaller caller) throws ApplicationException{
        try {                                            
            //Collection genes = geneHome.findUnassignedGenes(eid,pid);
            Collection genes = geneHome.findUnassignedGenesForTransgenic(eid, strainid, pid);
            Collection dtos = new ArrayList();
            Iterator i = genes.iterator();
            while(i.hasNext()) {                
                dtos.add(new GeneDTO((GeneRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get the genes not affected to the model.", e);
        }
    }

    public void addGeneToModel(int gaid, int eid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            GeneRemote gene = geneHome.findByPrimaryKey(new Integer(gaid));            
            model.addGene(gene);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add gene to this model\n"+e.getMessage(),e);            
        }
    }
    
    public void removeGeneFromModel(int gaid, int eid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            validate("MODEL_W", caller);
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            GeneRemote gene = geneHome.findByPrimaryKey(new Integer(gaid));            
            model.removeGene(gene);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove gene affected\n"+e.getMessage(),e);            
        }        
    }

    public Collection getPromotersForModel(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            Collection genes = geneHome.findPromoters(eid);
            Collection dtos = new ArrayList();
            Iterator i = genes.iterator();
            while(i.hasNext()) {
                dtos.add(new GeneDTO((GeneRemote)i.next()));
            }

            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get promoters for eid " + eid, e);
        }
    }

    public int createPromoter(String name, String symbol, int cid, String mgiid, String driver_note, String common_name, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            makeConnection();
            int gaid = getIIdGenerator().getNextId(conn, "gene_seq");
            GeneRemote gene = geneHome.create(gaid, name, symbol, cid, caller);
            gene.setMgiid(mgiid);
            gene.setDriver_note(driver_note);
            gene.setCommon_name(common_name);
            gene.setDistinguish("promoter");
            return gene.getGaid();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not create promoter. \n"+e.getMessage(),e);
        } finally {
            releaseConnection();
        }
    }

    public void updatePromoter(int gid, String name, String symbol, String mgiid, int cid, String driver_note, String common_name, TgDbCaller caller) throws ApplicationException {
        try {
            GeneRemote gene = geneHome.findByPrimaryKey(new Integer(gid));
            gene.setCaller(caller);
            gene.setName(name);
            gene.setGenesymbol(symbol);
            gene.setMgiid(mgiid);
            gene.setChromosome(chromosomeHome.findByPrimaryKey(new Integer(cid)));
            gene.setDriver_note(driver_note);
            gene.setCommon_name(common_name);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    //more promoter stuff
    public Collection getPromoterLinks(int pid, TgDbCaller caller) throws ApplicationException {
        try {
            GeneRemote promoter = geneHome.findByPrimaryKey(new Integer(pid));
            return promoter.getPromoter_links();
        }
        catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public void createPromoterLink(int pid, String repository, String externalid, String strainurl, TgDbCaller caller) throws ApplicationException {
        try {
            GeneRemote gene = geneHome.findByPrimaryKey(new Integer(pid));
            gene.insertPromoter_link(repository, externalid, strainurl);
        }
        catch(Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public void deletePromoterLink(int pid, int promoter_link_id, TgDbCaller caller) throws ApplicationException {
        try {
            GeneRemote promoter = geneHome.findByPrimaryKey(new Integer(pid));
            promoter.deletePromoter_link(promoter_link_id);
        }
        catch(Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public int createExpressedGene(String name, String symbol, int cid, String mgiid, String comm, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            makeConnection();
            int gaid = getIIdGenerator().getNextId(conn, "gene_seq");
            GeneRemote gene = geneHome.create(gaid, name, symbol, cid, caller);
            gene.setMgiid(mgiid);
            gene.setComm(comm);
            gene.setDistinguish("expressed_gene");
            return gene.getGaid();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not create promoter. \n"+e.getMessage(),e);
        } finally {
            releaseConnection();
        }
    }

    public void updateExpressedGene(int gid, String name, String symbol, String mgiid, int cid, String comm, TgDbCaller caller) throws ApplicationException {
        try {
            GeneRemote gene = geneHome.findByPrimaryKey(new Integer(gid));
            gene.setCaller(caller);
            gene.setName(name);
            gene.setGenesymbol(symbol);
            gene.setMgiid(mgiid);
            gene.setChromosome(chromosomeHome.findByPrimaryKey(new Integer(cid)));
            gene.setComm(comm);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    
    public int createTransgene(String name, String comm, String mgiid, String genesymbol, String geneexpress, String idgene, String idensembl, int cid, String molecular_note, String molecular_note_url, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            makeConnection();
            int gaid = getIIdGenerator().getNextId(conn, "gene_seq");
            GeneRemote gene = geneHome.create(gaid, name, genesymbol, cid, caller);
            gene.setComm(comm);
            gene.setMgiid(mgiid);
            gene.setGeneexpress(geneexpress);
            gene.setIdgene(idgene);
            gene.setIdensembl(idensembl);
            gene.setDistinguish("transgene");
            gene.setMolecular_note(molecular_note);
            gene.setMolecular_note_link(molecular_note_url);
            return gene.getGaid();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not create gene affected\n"+e.getMessage(),e);            
        } finally {
            releaseConnection();
        }        
    }

    public void updateGene(int gaid, String name, String comm, String mgiid, String genesymbol, String geneexpress, String idgene, String idensembl, int cid, String molecular_note, String molecular_note_url, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            GeneRemote gene = geneHome.findByPrimaryKey(new Integer(gaid));
            gene.setCaller(caller);
            
            gene.setName(name);
            gene.setComm(comm);
            gene.setMgiid(mgiid);
            gene.setGenesymbol(genesymbol);
            gene.setGeneexpress(geneexpress);
            gene.setIdgene(idgene);
            gene.setIdensembl(idensembl);
            ChromosomeRemote chr = chromosomeHome.findByPrimaryKey(new Integer(cid));
            gene.setChromosome(chr);
            gene.setMolecular_note(molecular_note);
            gene.setMolecular_note_link(molecular_note_url);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not update gene affected\n"+e.getMessage());            
        }
    }

    public void removeGene(int gaid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            validate("MODEL_W", caller);
            GeneRemote geneAffected = geneHome.findByPrimaryKey(new Integer(gaid));
            geneAffected.remove();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove gene affected.", e);            
        }
    }


    public GeneDTO getGene(int gaid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            GeneRemote gene = geneHome.findByPrimaryKey(new Integer(gaid));
            return new GeneDTO(gene);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not get gene affected\n"+e.getMessage());            
        }
    }
    
    public Collection searchByGene(String geneName, TgDbCaller caller) throws ApplicationException{
        Collection arr = new ArrayList();
        try
        {
            Collection genes = geneHome.findByName(geneName); 
            Iterator gi = genes.iterator();
            while (gi.hasNext())
            {
                try
                {
                    GeneRemote gene = (GeneRemote)gi.next();

                    validatePid("MODEL_R", caller, gene.getProject().getPid());

                    Collection models = gene.getModels();
                    Iterator i = models.iterator();
                    while (i.hasNext())
                    {
                        ExpModelRemote model = (ExpModelRemote)i.next();
                        arr.add(new ExpModelDTO(model));
                    }
                }
                catch (PermissionDeniedException pde)
                {
                    logger.error("---------------------------------------->ModelManagerBean#searchByGene: Permission denied");
                }
            }
        }
        catch (FinderException fe)
        {
            fe.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("failed to search by gene",e);
        }
        return arr;
    }
    
    public Collection searchGeneByKeyword(Keyword keyword, TgDbCaller caller) throws ApplicationException{
        Collection arr = new TreeSet();
        try
        {
            Collection genes = geneHome.findByKeyword(keyword);
            logger.debug("---------------------------------------->ModelManagerBean#searchGeneByKeyword: Keyword '"+keyword.getKeyword()+"' return "+genes.size()+" hits");
            Iterator i = genes.iterator();
            while (i.hasNext())
            {
                GeneRemote gene = (GeneRemote)i.next();
                arr.add(new GeneSearchResult(gene,"Controller?workflow=ViewGene&amp;gaid="+gene.getGaid()));
            }            
        }
        catch (FinderException fe)
        {
            fe.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("failed to search keyword",e);
        }
        return arr;
    }
    
    public Collection getGeneByMgiid(String mgiid, TgDbCaller caller) throws ApplicationException{
        try {                                            
            Collection genes = geneHome.findByMgiid(mgiid);
            Collection dtos = new ArrayList();
            Iterator i = genes.iterator();
            while(i.hasNext()) {                
                dtos.add(new GeneDTO((GeneRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genes by mgiid.", e);
        }
    }
    
    public Collection getGeneBySymbol(String symbol, TgDbCaller caller) throws ApplicationException{
        try {                                            
            Collection genes = geneHome.findBySymbol(symbol);
            Collection dtos = new ArrayList();
            Iterator i = genes.iterator();
            while(i.hasNext()) {                
                dtos.add(new GeneDTO((GeneRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genes by symbol.", e);
        }
    }

    public Collection getGenesByAllele(int aid, TgDbCaller caller) throws ApplicationException {
        try {
            Collection genes = geneHome.findByAllele(aid);
            Collection dtos = new ArrayList();
            Iterator i = genes.iterator();
            while(i.hasNext()) {
                dtos.add(new GeneDTO((GeneRemote)i.next()));
            }

            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genes by allele " + aid, e);
        }
    }

    public Collection getGenesUnassignedToAllele(int aid, String distinguish, TgDbCaller caller) throws ApplicationException {
        try {
            Collection genes = geneHome.findGenesNotAssignedToAllele(aid, distinguish);
            Collection dtos = new ArrayList();
            Iterator i = genes.iterator();
            while(i.hasNext()) {
                dtos.add(new GeneDTO((GeneRemote)i.next()));
            }

            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genes not assigned to allele " + aid, e);
        }
    }
    
    public Collection getGeneByNameCaseSensitive(String name, TgDbCaller caller) throws ApplicationException{
        try {                                            
            Collection genes = geneHome.findByNameCaseSensitive(name);
            Collection dtos = new ArrayList();
            Iterator i = genes.iterator();
            while(i.hasNext()) {                
                dtos.add(new GeneDTO((GeneRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genes by case sensitive name.", e);
        }
    }
    
    //</editor-fold>
    
    //integration + copy
    //<editor-fold defaultstate="collapsed">
    public int createIntegrationCopy(java.lang.String isite, java.lang.String cnumber) throws ApplicationException {
        try {
            makeConnection();
            int iscmid = getIIdGenerator().getNextId(conn, "is_cn_seq");            
            IntegrationCopyRemote ic = icHome.create(iscmid, isite, cnumber);
            return ic.getIscnid();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not create integration copy data set\n"+e.getMessage(),e);            
        } finally {
            releaseConnection();
        }        
    }
    
    public Collection getIntegrationCopiesByModel(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {                                            
            Collection ics = icHome.findByModel(eid);
            Collection dtos = new ArrayList();
            Iterator i = ics.iterator();
            while(i.hasNext()) {                
                dtos.add(new IntegrationCopyDTO((IntegrationCopyRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get integration copy data sets.", e);
        }
    }
    
    public void removeIntegrationCopy(int iscmid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {           
            validate("MODEL_W", caller);
            IntegrationCopyRemote ic = icHome.findByPrimaryKey(new Integer(iscmid));
            ic.remove();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove integration copy data set.\n"+e.getMessage());            
        }
    }
    
    public IntegrationCopyDTO getIntegrationCopy(int iscmid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            IntegrationCopyRemote ic = icHome.findByPrimaryKey(new Integer(iscmid));
            return new IntegrationCopyDTO(ic);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not get integration copy data set.\n"+e.getMessage());            
        }
    }
    
    public void updateIntegrationCopy(int iscmid, java.lang.String isite, java.lang.String cnumber, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            IntegrationCopyRemote ic = icHome.findByPrimaryKey(new Integer(iscmid));
            
            ic.setIsite(isite);
            ic.setCnumber(cnumber);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not update integration copy data set. \n"+e.getMessage());            
        }
    }
    //</editor-fold>
    
    //expression model functions
    //<editor-fold defaultstate="collapsed">
    public int createExpressionModel(java.lang.String exanatomy, java.lang.String excomm) throws ApplicationException {
        try {
            makeConnection();
            int exid = getIIdGenerator().getNextId(conn, "expression_id_seq");            
            ExpressionModelRemote expression = expressionHome.create(exid, exanatomy, excomm);
            return expression.getExid();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not create expression model affected\n"+e.getMessage(),e);            
        } finally {
            releaseConnection();
        }        
    }
    
    public Collection getExpressionModelsByModel(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {                                            
            Collection expressions = expressionHome.findByModel(eid);
            Collection dtos = new ArrayList();
            Iterator i = expressions.iterator();
            while(i.hasNext()) {
                ExpressionModelRemote expression = (ExpressionModelRemote)i.next();
                ExpressionModelDTO expression_dto = new ExpressionModelDTO(expression);

                //get the emaps first
                String emap_terms = "";
                Iterator j = expression.getOntologyTerms("EMAP").iterator();
                while(j.hasNext()) {
                    OlsDTO tmp = (OlsDTO)j.next();
                    emap_terms += "[" + tmp.getOid() + "] " + getTermById(tmp.getOid(), tmp.getNamespace()) + " &bull; ";
                }
                expression_dto.setEmap_terms(emap_terms);

                //now get the mas
                String ma_terms = "";
                Iterator k = expression.getOntologyTerms("MA").iterator();
                while(k.hasNext()) {
                    OlsDTO tmp = (OlsDTO)k.next();
                    ma_terms += "<a href='http://www.informatics.jax.org/searches/AMA.cgi?id="+tmp.getOid()+"' target='_blank'>[" + tmp.getOid() + "] " + getTermById(tmp.getOid(), tmp.getNamespace()) + "</a> &bull; ";
                }
                expression_dto.setMa_terms(ma_terms);

                dtos.add(expression_dto);
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get expresion models affected.", e);
        }
    }
    
    public Collection getExpressionModelFiles(int exid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {                                            
            ExpressionModelRemote expressions = expressionHome.findByPrimaryKey(new Integer(exid));
            Collection dtos = new ArrayList();
            
            Iterator i = expressions.getFiles().iterator();
            while(i.hasNext()) {                
                dtos.add(new FileDTO((FileRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get expresion models affected.", e);
        }
    }
    
    public void addFileToExpressionModel(int exid, int fileid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            ExpressionModelRemote expression = expressionHome.findByPrimaryKey(new Integer(exid));
            expression.addFile(fileid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add file to expression model numer "+exid+" \n"+e.getMessage(),e);            
        }
    }

    public void addReferenceToExpressionModel(int exid, int refid, TgDbCaller caller) throws ApplicationException {
        try {
            ExpressionModelRemote expression = expressionHome.findByPrimaryKey(new Integer(exid));
            expression.addReference(refid);
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
    }

    public void deleteReferenceFromExpressionModel(int exid, int refid, TgDbCaller caller) throws ApplicationException {
        try {
            ExpressionModelRemote expression = expressionHome.findByPrimaryKey(new Integer(exid));
            expression.deleteReference(refid);
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
    }

    public void addOntologyToExpressionModel(int exid, String oid, String namespace, TgDbCaller caller) throws ApplicationException {
        try {
            ExpressionModelRemote expression = expressionHome.findByPrimaryKey(new Integer(exid));
            expression.addOntology(oid, namespace);
        } catch (Exception e) {
            logger.error(getStackTrace(e));
//            e.printStackTrace();
//            throw new ApplicationException("Could not add file to expression model numer "+exid+" \n"+e.getMessage(),e);
        }
    }

    public void deleteOntologyFromExpressionModel(int exid, String oid, String namespace, TgDbCaller caller) throws ApplicationException {
        try {
            ExpressionModelRemote expression = expressionHome.findByPrimaryKey(new Integer(exid));
            expression.deleteOntology(oid, namespace);
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
    }

    public Collection getOntologyTerms(int exid, String namespace, TgDbCaller caller) throws ApplicationException {
        Collection terms = new ArrayList();
        try {
            ExpressionModelRemote expression = expressionHome.findByPrimaryKey(new Integer(exid));
//            expression.addOntology(oid, namespace);
            Iterator i = expression.getOntologyTerms(namespace).iterator();
            while(i.hasNext()) {
                OlsDTO tmp = (OlsDTO)i.next();
                tmp.setName(getTermById(tmp.getOid(), tmp.getNamespace()));
                terms.add(tmp);
            }
        } catch (Exception e) {
            logger.error(getStackTrace(e));
//            e.printStackTrace();
//            throw new ApplicationException("Could not add file to expression model numer "+exid+" \n"+e.getMessage(),e);
        }
        return terms;
    }
    
    public void removeExpressionModel(int exid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {           
            validate("MODEL_W", caller);
            ExpressionModelRemote expression = expressionHome.findByPrimaryKey(new Integer(exid));
            
            Iterator files = expression.getFiles().iterator();
            while (files.hasNext())
            {
                FileRemote file = (FileRemote)files.next();
                file.remove();
            }    
            
            expression.remove();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove expression model\n"+e.getMessage());            
        }
    }
    
    public ExpressionModelDTO getExpressionModel(int exid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            ExpressionModelRemote expression = expressionHome.findByPrimaryKey(new Integer(exid));
            return new ExpressionModelDTO(expression);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not get expression model\n"+e.getMessage());            
        }
    }
    
    public void updateExpressionModel(int exid, java.lang.String exanatomy, java.lang.String excomm, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            ExpressionModelRemote expression = expressionHome.findByPrimaryKey(new Integer(exid));
            
            expression.setExanatomy(exanatomy);
            expression.setExcomm(excomm);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not update expression model\n"+e.getMessage());            
        }
    }
    
    //</editor-fold>
    
    //stand-alone ontology methods
    //<editor-fold defaultstate="collapsed">
    public Collection getOntologyTerms(String namespace) throws ApplicationException {
        Collection terms = new ArrayList();
        makeConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("select distinct(oid) from r_expression_ontology where namespace = ?");
            ps.setString(1, namespace);
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                OlsDTO tmp = new OlsDTO();
                tmp.setOid(result.getString("oid"));
                tmp.setNamespace(namespace);
                terms.add(tmp);
            }
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        } finally {
            releaseConnection();
        }
        return terms;
    }
    //</editor-fold>
    
    //strain-allele functions
    //<editor-fold defaultstate="collapsed">
    public Collection getStrainAlleles(TgDbCaller caller) throws ApplicationException {
        Collection dtos = new ArrayList();
        try {
            Collection sas = strainAlleleHome.findAll(caller);
            Iterator i = sas.iterator();
            while(i.hasNext()) {
                dtos.add(new StrainAlleleDTO((StrainAlleleRemote)i.next()));
            }
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return dtos;
    }
    
    public Collection getStrainAllelesByFDM(FormDataManager fdm, TgDbCaller caller) throws ApplicationException {
        Collection strain_alleles = new ArrayList();
        try {
            Iterator i = strainAlleleHome.findByFDM(fdm, caller).iterator();
            
            while (i.hasNext()) {
                
                strain_alleles.add(new StrainAlleleDTO((StrainAlleleRemote)i.next()));
                
            }
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return strain_alleles;
    }
    
    public Collection getStrainAllelesByPGMFDM(PageManager pageManager, FormDataManager fdm, TgDbCaller caller) throws ApplicationException {
        Collection strain_alleles = new ArrayList();
        try {
            Iterator i = strainAlleleHome.findByFDM(fdm, caller).iterator();//this.modelsTMP.iterator();
            
            int start = pageManager.getStart();
            int stop = pageManager.getStop();
            int index = 0;
            
            while (i.hasNext()) {
                
                index++;
                
                if (index>=start && index<=stop) {
                    strain_alleles.add(new StrainAlleleDTO((StrainAlleleRemote)i.next()));
                } else {
                    if(strain_alleles.size() == pageManager.getDelta())
                        return strain_alleles;
                    i.next();
                }
            }
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return strain_alleles;
    }

    public Collection getStrainAllelesByMgiid(String mgiid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {                                            
            Collection sas = strainAlleleHome.findByMgiid(mgiid, caller);
            Collection dtos = new ArrayList();
            Iterator i = sas.iterator();
            while(i.hasNext()) {                
                dtos.add(new StrainAlleleDTO((StrainAlleleRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain alleles by mgiid.", e);
        }
    }
    
    public Collection getStrainAllelesByName(String name, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {                                            
            Collection sas = strainAlleleHome.findByName(name, caller);
            Collection dtos = new ArrayList();
            Iterator i = sas.iterator();
            while(i.hasNext()) {                
                dtos.add(new StrainAlleleDTO((StrainAlleleRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain alleles by name.", e);
        }
    }
    
    public Collection getStrainAllelesBySymbol(String symbol, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {                                            
            Collection sas = strainAlleleHome.findBySymbol(symbol, caller);
            Collection dtos = new ArrayList();
            Iterator i = sas.iterator();
            while(i.hasNext()) {                
                dtos.add(new StrainAlleleDTO((StrainAlleleRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain alleles by symbol.", e);
        }
    }

    public Collection getUnassignedAlleles(int model, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            Collection sas = strainAlleleHome.findUnassignedAlleles(model, caller);
            Collection dtos = new ArrayList();
            Iterator i = sas.iterator();
            while(i.hasNext()) {
                dtos.add(new StrainAlleleDTO((StrainAlleleRemote)i.next()));
            }

            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain alleles not assigned to model " + model, e);
        }
    }

    public void addGeneToStrainAllele(int aid, int gid, TgDbCaller caller) throws ApplicationException {
        try {
            StrainAlleleRemote allele = strainAlleleHome.findByPrimaryKey(new Integer(aid));
            allele.addGene(gid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add gene to this strain allele\n"+e.getMessage(),e);
        }
    }

    public void removeGeneFromStrainAllele(int aid, int gid, TgDbCaller caller) throws ApplicationException {
        try {
            StrainAlleleRemote allele = strainAlleleHome.findByPrimaryKey(new Integer(aid));
            allele.deleteGene(gid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove gene from this strain allele\n"+e.getMessage(),e);
        }
    }

    public void addMutationTypeAndAttributeToStrainAllele(int eid, int strain_allele, int mutation_type, String attribute, TgDbCaller caller) throws ApplicationException{
        try {
            StrainAlleleRemote allele = strainAlleleHome.findByPrimaryKey(new Integer(strain_allele));
            allele.addMutationTypeAndAttribute(eid, mutation_type, attribute);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add mutation type and attribute to this strain allele\n"+e.getMessage(),e);
        }
    }

    public void addMutationTypeToStrainAllele(int id, int strainalleleid, TgDbCaller caller) throws ApplicationException{
        try {
            StrainAlleleRemote allele = strainAlleleHome.findByPrimaryKey(new Integer(strainalleleid));
            MutationTypeRemote mut = mutationTypeHome.findByPrimaryKey(new Integer(id));
            
            allele.addMutationType(mut);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add mutation type to this strain allele\n"+e.getMessage(),e);            
        }
    }
    
    public void removeMutationTypeFromStrainAllele(int model, int mutation_type, int strain_allele, TgDbCaller caller) throws ApplicationException{
        try {
            StrainAlleleRemote allele = strainAlleleHome.findByPrimaryKey(new Integer(strain_allele));
            allele.removeMutationType(model, mutation_type);
            
//            return mutName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add mutation type to this strain allele\n"+e.getMessage(),e);            
        }
    }

    //FIXME!!! - Needs a big big fix
//    public void removeGeneFromStrainAlleles(int gaid, int eid, org.tgdb.TgDbCaller caller) throws ApplicationException {
//        try {
//            validate("MODEL_W", caller);
//            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
//            int strainId = model.getStrain().getStrainid();
//            model.unassignGeneFromStrainAlleles(strainId, gaid);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ApplicationException("Could not remove gene from strain's alleles\n"+e.getMessage(),e);
//        }
//    }

    //FIXME!!! - Needs a big big fix
//    public void removeStrainAllelesFromGene(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException {
//        try {
//            validate("MODEL_W", caller);
//            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
//            int strainId = model.getStrain().getStrainid();
//            model.unassignStrainAllelesFromGene(eid, strainId);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ApplicationException("Could not remove gene from strain's alleles via strain allele's gene reassignment\n"+e.getMessage(),e);
//        }
//    }
    
    public StrainAlleleDTO getStrainAllele(int model,int strain_allele, boolean simple, TgDbCaller caller) throws ApplicationException{
        try {
            StrainAlleleRemote allele = strainAlleleHome.findByPrimaryKey(new Integer(strain_allele));
            StrainAlleleDTO allele_dto = new StrainAlleleDTO(allele);
            if(!simple) allele_dto.setAttributes(allele.getAttributes(model));
            return allele_dto;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain alleles");
        }
    }
    
    public Collection getMutationTypesFromStrainAllele(int strainalleleid, int eid, TgDbCaller caller) throws ApplicationException{
        try
        {
            Collection arr = new ArrayList();
            //FIXME!!! - Is this not to resource intensive???
            StrainAlleleRemote sa = strainAlleleHome.findByPrimaryKey(new Integer(strainalleleid));
//            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
//            Collection mtArr = sa.getMutationTypes(model.getStrain().getStrainid());
            //FIXME!!! - Remove this (strain-eid switch)
            Collection mtArr = sa.getMutationTypes(eid);
            logger.debug("---------------------------------------->ModelManagerBean#getMutationTypesFromStrainAllele: Mutation types "+mtArr.size());
            Iterator i = mtArr.iterator();
            while (i.hasNext())
            {
                arr.add(new MutationTypeDTO((MutationTypeRemote)i.next()));
            }    
            return arr;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain alleles");
        }
    }
    
    public int createStrainAllele(String symbol, String name, String mgi_id, String mgi_url, String made_by, String origin_strain, TgDbCaller caller) throws ApplicationException{
        try {
            makeConnection();
            int id = getIIdGenerator().getNextId(conn, "strain_allele_seq");
//            StrainRemote strain = strainHome.findByPrimaryKey(new Integer(strainid));
            StrainAlleleRemote strain_allele = strainAlleleHome.create(id, symbol, name, caller);
            strain_allele.setMgiId(mgi_id);
            strain_allele.setMgi_url(mgi_url);
            strain_allele.setMade_by(made_by);
            strain_allele.setOrigin_strain(origin_strain);
            return strain_allele.getId();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to add a new strain allele. ");
        }
        finally
        {
            releaseConnection();
        }
    }
    
    public int createStrainAlleleAdvanced(String symbol, String name, String mgiid, TgDbCaller caller) throws ApplicationException{
        try
        {
            makeConnection();
            int id = getIIdGenerator().getNextId(conn, "strain_allele_seq");
//            StrainRemote strain = modelHome.findByPrimaryKey(new Integer(eid)).getStrain();
            StrainAlleleRemote strainAllele = strainAlleleHome.create(id, symbol, name, caller);
            strainAllele.setMgiId(mgiid);
            return strainAllele.getId();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to add a new strain allele. ");
        }
        finally
        {
            releaseConnection();
        }
    }

    public void deleteStrainAllele(int strain_allele, TgDbCaller caller) throws ApplicationException {
        try {
            StrainAlleleRemote allele = strainAlleleHome.findByPrimaryKey(new Integer(strain_allele));
            allele.remove();
        }
        catch(Exception e){
            logger.error(getStackTrace(e));
        }
    }
    
    public void removeStrainAllele (int model, int strain_allele, TgDbCaller caller) throws ApplicationException {
        try {
            validate("MODEL_W", caller);
            //GeneRemote geneAffected = geneHome.findByPrimaryKey(new Integer(gaid));
            //geneAffected.remove();
            StrainAlleleRemote allele = strainAlleleHome.findByPrimaryKey(new Integer(strain_allele));
//            strainAllele.remove();
            allele.unassign(model);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove strain allele.", e);            
        }
    }
    
    public void updateStrainAllele(int eid, int strainallele, String symbol, String name, String attributes, String mgi_id, String mgi_url, String made_by, String origin_strain, boolean simple, TgDbCaller caller) throws ApplicationException{
        try {
            StrainAlleleRemote allele = strainAlleleHome.findByPrimaryKey(new Integer(strainallele));
            
            allele.setSymbol(symbol);
            allele.setName(name);
            allele.setMgiId(mgi_id);
            allele.setMgi_url(mgi_url);

            if(!simple) allele.setAttributes(eid, attributes);

            allele.setMade_by(made_by);
            allele.setOrigin_strain(origin_strain);
            
//            if (geneid!=0){
//                allele.setGene(geneHome.findByPrimaryKey(new Integer(geneid)));
//            }
            /*else{
                allele.setGeneToNULL(strainallele);
            }*/
                
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to add a new strain allele. ");
        }
      
    }
    
//    public void clearGeneFromStrainAllele(int strainallele) throws ApplicationException{
//        try
//        {
//            StrainAlleleRemote allele = strainAlleleHome.findByPrimaryKey(new Integer(strainallele));
//            allele.setGeneToNULL(strainallele);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            throw new ApplicationException("Failed to set strain allele's gene to null. ");
//        }
//
//    }
    //</editor-fold>
    
    //references
    //<editor-fold defaultstate="collapsed">
    public Collection getReferences(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {                                            
            Collection references = referenceHome.findByModel(eid);
            Collection dtos = new ArrayList();
            Iterator i = references.iterator();
            while(i.hasNext()) {   
                dtos.add(new ReferenceDTO((ReferenceRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get references.");
        }
    }

    public Collection getReferencesByModelAndPrimary(int eid, boolean primary, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            Collection references = referenceHome.findByModelAndPrimary(eid, primary);
            Collection dtos = new ArrayList();
            Iterator i = references.iterator();
            while(i.hasNext()) {
                dtos.add(new ReferenceDTO((ReferenceRemote)i.next()));
            }

            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get references.");
        }
    }

    public void addLinkReference(int eid, String name, String pubmed, boolean primary, String comm, String url, TgDbCaller caller) throws ApplicationException {
        try {
            makeConnection();
            int refid = getIIdGenerator().getNextId(conn, "reference_seq");
            
            ReferenceRemote reference = referenceHome.create(refid, caller.getPid(), name, comm, caller);
            LinkRemote link = resourceManager.createLink(name, comm, url, caller);
            reference.setCaller(caller);
            reference.setLink(link);
            reference.setPubmed(pubmed);
            reference.setPrimary(primary);
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            model.addReference(reference);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add link reference\n"+e.getMessage());            
        } finally {
            releaseConnection();
        }     
    }

    public void addFileReference(int eid, java.lang.String name, java.lang.String comm, org.tgdb.frame.io.FileDataObject fileData, String pubmed, boolean primary, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            makeConnection();
            int refid = getIIdGenerator().getNextId(conn, "reference_seq");
            
            ReferenceRemote reference = referenceHome.create(refid, caller.getPid(), name, comm, caller);
            FileRemote file = resourceManager.saveFile(fileData.getFileName(), comm, caller, fileData);
            reference.setCaller(caller);
            reference.setFile(file);
            reference.setPubmed(pubmed);
            reference.setPrimary(primary);
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            model.addReference(reference);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add file reference\n"+e.getMessage());            
        } finally {
            releaseConnection();
        } 
    }

    public ReferenceDTO getReference(int refid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            return new ReferenceDTO(referenceHome.findByPrimaryKey(new Integer(refid)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove reference\n"+e.getMessage());
        }
    }

    public void removeReference(int eid, int refid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {            
            ReferenceRemote reference = referenceHome.findByPrimaryKey(new Integer(refid));
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            model.removeReference(reference);
            reference.remove();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove reference\n"+e.getMessage());            
        }
    }

    public void updateReference(int refid, String name, String comm, String pubmed, boolean primary, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            ReferenceRemote ref = referenceHome.findByPrimaryKey(new Integer(refid));
            ref.setName(name);
            ref.setComm(comm);
            ref.setPubmed(pubmed);
            ref.setPrimary(primary);
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
    }
    //</editor-fold>

    //resources
    //<editor-fold defaultstate="collapsed">
    public void addFileResource(int eid, java.lang.String name, java.lang.String comm, org.tgdb.frame.io.FileDataObject fileData, int catid, TgDbCaller caller) throws ApplicationException {
        try {
            FileRemote file = resourceManager.saveFile(name, comm, caller, fileData);
            ResourceRemote resource = resourceManager.createResource(caller.getPid(), name, comm, file.getFileId(), 0, catid, caller);
      
            // Find and add resource to model.
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            model.addResource(resource);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add file resource\n"+e.getMessage());            
        }
    }
    
    public void removeFileResource(int refid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {           
            //validate("MODEL_W", caller);
            ReferenceRemote reference = referenceHome.findByPrimaryKey(new Integer(refid));
            if(reference.getLink()!=null){
                reference.getLink().remove();
            }
            if(reference.getFile()!=null){
                reference.getFile().remove();
            }
            reference.remove(); 
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove reference\n"+e.getMessage());            
        }
    }

    public void addLinkResource(int eid, java.lang.String name, java.lang.String comm, java.lang.String url, int catid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            
            LinkRemote link = resourceManager.createLink(name, comm, url, caller);
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            
            ResourceRemote resource = resourceManager.createResource(caller.getPid(), name, comm, 0, link.getLinkId(), catid, caller);
            model.addResource(resource);
    
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add link resource\n"+e.getMessage());            
        }
    }
    
    public int addFile(org.tgdb.frame.io.FileDataObject exfile, String exfilecomm, TgDbCaller caller) throws ApplicationException {
        try {
            FileRemote file = resourceManager.saveFile(exfile.getFileName(), exfilecomm, caller, exfile);
            
            return file.getFileId();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add file resource [addFile function] \n"+e.getMessage());            
        }
    }
    
    public void removeFile(int fileid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            validate("MODEL_W", caller);
            FileRemote file = fileHome.findByPrimaryKey(new Integer(fileid));
            file.remove();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove file "+fileid+" from database.", e);            
        }
    }
    //</editor-fold>
    
    //strain(s) functions
    //<editor-fold defaultstate="collapsed">
    public Collection getStrains(TgDbCaller caller) throws ApplicationException{
        try {                                
            Collection strains = strainHome.findByProject(caller.getPid(),caller);
                    
                    
            Collection strainDTOs = new ArrayList();
            Iterator i = strains.iterator();
            while(i.hasNext()) {                
                strainDTOs.add(new StrainDTO((StrainRemote)i.next()));
            }
            
            return strainDTOs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strains.");
        }
    }
    
    public Collection getStrainsPGM(TgDbCaller caller, PageManager page_manager) throws ApplicationException {
        Collection strain_dtos = new ArrayList();
        try {
            Iterator i = strainHome.findByProject(caller.getPid(), caller).iterator();
            
            int start = page_manager.getStart();
            int stop = page_manager.getStop();
            int index = 0;
            
            while(i.hasNext()) {
                index++;
                
                if (index>=start && index<=stop) {
                    strain_dtos.add(new StrainDTO((StrainRemote)i.next()));
                } else {
                    if(strain_dtos.size() == page_manager.getDelta()) return strain_dtos;
                    i.next();
                }
                
            }//while
            
        } catch (Exception e) {
            logger.error(e);
        }
        return strain_dtos;
    }

    public Collection getStrainsConnectedToModels(TgDbCaller caller) throws ApplicationException{
        try {
            Collection strains = strainHome.findConnectedToModels(caller);


            Collection strainDTOs = new ArrayList();
            Iterator i = strains.iterator();
            while(i.hasNext()) {
                strainDTOs.add(new StrainDTO((StrainRemote)i.next()));
            }

            return strainDTOs;
        } catch (Exception e) {
//            logger.error(getStackTrace(e));
            throw new ApplicationException("Failed to get strains.");
        }
    }

    public Collection getStrainsConnectedToModel(int eid, TgDbCaller caller) throws ApplicationException{
        try {
            Collection strains = strainHome.findByModel(eid, caller);


            Collection strainDTOs = new ArrayList();
            Iterator i = strains.iterator();
            while(i.hasNext()) {
                strainDTOs.add(new StrainDTO((StrainRemote)i.next()));
            }

            return strainDTOs;
        } catch (Exception e) {
//            logger.error(getStackTrace(e));
            throw new ApplicationException("Failed to get strains.");
        }
    }
    
    public StrainDTO getStrain(int strainid, TgDbCaller caller, String superscript) throws ApplicationException{
        try {
            StrainRemote strain = strainHome.findByPrimaryKey(new Integer(strainid));
            return new StrainDTO(strain);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain.");
        }
    }

    public void createStrain(String designation, TgDbCaller caller) throws ApplicationException {
       try {
            makeConnection();
            int strain_id = getIIdGenerator().getNextId(conn, "strain_seq");
            strainHome.create(strain_id, designation, caller);
//            StrainRemote strain = strainHome.create(strain_id, designation, caller);
        }
        catch (Exception e) {
            throw new ApplicationException("Failed to create strain \n",e);
        }
        finally {
            releaseConnection();
        }

   }
     
    public void updateStrain(int strainid, String designation, TgDbCaller caller) throws ApplicationException{
        try {
            StrainRemote strain = strainHome.findByPrimaryKey(new Integer(strainid));
            strain.setDesignation(designation);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain.");
        }
    }

    public void deleteStrain(int strainid, TgDbCaller caller) throws ApplicationException {
        try {
            StrainRemote strain = strainHome.findByPrimaryKey(new Integer(strainid));
            strain.remove();
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
    }

    public void createStrainType(String name, String abbreviation, TgDbCaller caller) throws ApplicationException {
        try {
            makeConnection();
            int stid = getIIdGenerator().getNextId(conn, "strain_type_seq");
            strainTypeHome.create(stid, name, abbreviation, caller);
        }
        catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        finally {
            releaseConnection();
        }
    }

    public void updateStrainType(int stid, String name, String abbreviation, TgDbCaller caller) throws ApplicationException {
        try {
            StrainTypeRemote strain_type = strainTypeHome.findByPrimaryKey(new Integer(stid));
            strain_type.setName(name);
            strain_type.setAbbreviation(abbreviation);
        }
        catch(Exception e){
            logger.error(getStackTrace(e));
        }
    }

    public void deleteStrainType(int stid, TgDbCaller caller) throws ApplicationException {
        try {
            StrainTypeRemote strain_type = strainTypeHome.findByPrimaryKey(new Integer(stid));
            strain_type.remove();
        }
        catch(Exception e){
            logger.error(getStackTrace(e));
        }
    }

    public StrainTypeDTO getStrainType(int stid, TgDbCaller caller) throws ApplicationException {
        StrainTypeDTO strain_type = null;
        try {
            strain_type = new StrainTypeDTO(strainTypeHome.findByPrimaryKey(new Integer(stid)));
        }
        catch(Exception e) {
            logger.error(getStackTrace(e));
        }
        return strain_type;
    }
    
    public Collection getStrainTypes(TgDbCaller caller) throws ApplicationException{
        try {
            Collection strainTypes = strainTypeHome.findByProject(caller.getPid(), caller);
            Collection strainTypeDTOs = new ArrayList();
            Iterator i = strainTypes.iterator();
            while (i.hasNext()) {
                StrainTypeRemote type = (StrainTypeRemote)i.next();
                strainTypeDTOs.add(new StrainTypeDTO(type));
            }
            return strainTypeDTOs;
            
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain types");
        }
    }
    
    public Collection getStrainTypesByAbbreviation(String abbreviation, TgDbCaller caller) throws ApplicationException{
        try
        {
            Collection strainTypes = strainTypeHome.findByAbbreviation(abbreviation, caller);
            Collection strainTypeDTOs = new ArrayList();
            Iterator i = strainTypes.iterator();
            while (i.hasNext())
            {
                StrainTypeRemote type = (StrainTypeRemote)i.next();
                strainTypeDTOs.add(new StrainTypeDTO(type));
            }
            return strainTypeDTOs;
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain types by abbreviation");
        }
    }
    
    public Collection getStrainTypesForStrain(int strainId, TgDbCaller caller) throws ApplicationException{
        try
        {
            StrainRemote strain = strainHome.findByPrimaryKey(new Integer(strainId));
            Collection strainTypes = strain.getTypes();
            Collection strainTypeDTOs = new ArrayList();
            Iterator i = strainTypes.iterator();
            while (i.hasNext())
            {
                StrainTypeRemote type = (StrainTypeRemote)i.next();
                strainTypeDTOs.add(new StrainTypeDTO(type));
            }
            return strainTypeDTOs;
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain types for the strain ["+strainId+"]");
        }
    }
    
    public Collection getAvailableStrainTypesForStrain(int strainId, TgDbCaller caller) throws ApplicationException{
        try
        {
            Collection strainTypes = getStrainTypesForStrain(strainId, caller);
            Collection allTypes = getStrainTypes(caller); 
            
            // Get all available types
            allTypes.removeAll(strainTypes);
            
            // Return available types
            return allTypes;    
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to get available strain types for the strain ["+strainId+"]");
        }
    }

    public void createStrainState(String name, String abbreviation, TgDbCaller caller) throws ApplicationException {
        try {
            makeConnection();
            int ssid = getIIdGenerator().getNextId(conn, "strain_state_seq");
            strainStateHome.create(ssid, name, abbreviation, caller);
        }
        catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        finally {
            releaseConnection();
        }
    }

    public void updateStrainState(int ssid, String name, String abbreviation, TgDbCaller caller) throws ApplicationException {
        try {
            StrainStateRemote strain_state = strainStateHome.findByPrimaryKey(new Integer(ssid));
            strain_state.setName(name);
            strain_state.setAbbreviation(abbreviation);
        }
        catch(Exception e){
            logger.error(getStackTrace(e));
        }
    }

    public void deleteStrainState(int ssid, TgDbCaller caller) throws ApplicationException {
        try {
            StrainStateRemote strain_state = strainStateHome.findByPrimaryKey(new Integer(ssid));
            strain_state.remove();
        }
        catch(Exception e){
            logger.error(getStackTrace(e));
        }
    }

    public StrainStateDTO getStrainState(int ssid, TgDbCaller caller) throws ApplicationException {
        StrainStateDTO strain_state = null;
        try {
            strain_state = new StrainStateDTO(strainStateHome.findByPrimaryKey(new Integer(ssid)));
        }
        catch(Exception e) {
            logger.error(getStackTrace(e));
        }
        return strain_state;
    }
    
    public Collection getStrainStates(TgDbCaller caller) throws ApplicationException{
        try
        {
            Collection strainStates = strainStateHome.findByProject(caller.getPid(), caller);
            Collection strainStateDTOs = new ArrayList();
            Iterator i = strainStates.iterator();
            while (i.hasNext())
            {
                StrainStateRemote state = (StrainStateRemote)i.next();
                strainStateDTOs.add(new StrainStateDTO(state));
            }
            
            logger.debug("---------------------------------------->ModelManagerBean#getStrainStates: Strain states = "+strainStates.size());
            
            return strainStateDTOs;
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain types");
        }
    }
    
    public Collection getStrainStatesByAbbreviation(String abbreviation, TgDbCaller caller) throws ApplicationException{
        try
        {
            Collection strainStates = strainStateHome.findByAbbreviation(abbreviation, caller);
            Collection strainStateDTOs = new ArrayList();
            Iterator i = strainStates.iterator();
            while (i.hasNext())
            {
                StrainStateRemote state = (StrainStateRemote)i.next();
                strainStateDTOs.add(new StrainStateDTO(state));
            }
            
            logger.debug("---------------------------------------->ModelManagerBean#getStrainStatesByAbbreviation: Strain states = "+strainStates.size());
            
            return strainStateDTOs;
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain types by abbreviation");
        }
    }
    
    public Collection getStrainStatesForStrain(int strainId, TgDbCaller caller) throws ApplicationException{
        try
        {
            StrainRemote strain = strainHome.findByPrimaryKey(new Integer(strainId));
            Collection strainStates = strain.getStates();
            Collection strainStateDTOs = new ArrayList();
            Iterator i = strainStates.iterator();
            while (i.hasNext())
            {
                StrainStateRemote state = (StrainStateRemote)i.next();
                strainStateDTOs.add(new StrainStateDTO(state));
            }
 
            return strainStateDTOs;           
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain types for strain ["+strainId+"]");
        }
    }
    
    public Collection getAvailableStrainStatesForStrain(int strainId, TgDbCaller caller) throws ApplicationException{
        try
        {
            Collection strainStates = getStrainStatesForStrain(strainId, caller);
            Collection allStates = getStrainStates(caller); 
            
            // Get all available states
            allStates.removeAll(strainStates);
            
            // Return available states
            return allStates;           
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to get available strain states for strain ["+strainId+"]");
        }
    }
    
    public void addStrainAndTypeToStrain(int strainid, int typeid, int stateid, TgDbCaller caller) throws ApplicationException{
        try
        {
            
            logger.debug("---------------------------------------->ModelManagerBean#addStrainAndTypeToStrain: strainid = "+strainid+", typeid = "+typeid+", stateid = "+stateid);
            
            StrainRemote strain = strainHome.findByPrimaryKey(new Integer(strainid));
            if (stateid!=0)
                strain.addState(strainStateHome.findByPrimaryKey(new Integer(stateid)));
            if (typeid!=0)
                strain.addType(strainTypeHome.findByPrimaryKey(new Integer(typeid)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Could not add the state ["+stateid+"] and type ["+typeid+"] to this strain ["+strainid+"]");
        }
    }
    
    public void removeTypeFromStrain(int strainid, int typeid, TgDbCaller caller) throws ApplicationException{
        try
        {
            validate("MUGEN_W", caller);
            StrainRemote strain = strainHome.findByPrimaryKey(new Integer(strainid));
            strain.removeType(strainTypeHome.findByPrimaryKey(new Integer(typeid)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Could not remove the type ["+typeid+"] from this strain ["+strainid+"]");
        }
    }
    
    public void removeStateFromStrain(int strainid, int stateid, TgDbCaller caller) throws ApplicationException{
        try
        {
            StrainRemote strain = strainHome.findByPrimaryKey(new Integer(strainid));
            strain.removeState(strainStateHome.findByPrimaryKey(new Integer(stateid)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Could not remove the state ["+stateid+"] from this strain ["+strainid+"]");
        }
    }

    //FIXME!!! - Rename this. Must be getStrainAllelesFromModel
    public Collection getStrainAllelesFromStrain(int eid, TgDbCaller caller) throws ApplicationException{
        try
        {
            Collection strainAlleles = strainAlleleHome.findByStrain(eid, caller);
            Collection strainAlleleDTOs = new ArrayList();
            Iterator i = strainAlleles.iterator();
            while (i.hasNext())
            {
                StrainAlleleRemote allele = (StrainAlleleRemote)i.next();
                StrainAlleleDTO sa_tmp = new StrainAlleleDTO(allele);
                sa_tmp.setAttributes(allele.getAttributes(eid));

                //fetch matutation, mutation abbreviations & IsStrainAlleleTransgenic
                Collection mut_arr = allele.getMutationTypes(eid);
                Iterator k = mut_arr.iterator();
                String mutations = "";
                String mutationabbrs = "";
                int IsStrainAlleleTransgenic = 0;
                int j=0;
                while (k.hasNext()) {
                    MutationTypeRemote m = (MutationTypeRemote)k.next();
                    if (j!=0){
                        mutationabbrs += ", ";
                        mutations += ", ";
                    }
                    mutations += m.getName();

                    if (m.getName().compareTo("transgenic")==0)
                        IsStrainAlleleTransgenic = 1;

                    mutationabbrs += m.getAbbreviation();
                    j++;
                }

                sa_tmp.setMutations(mutations);
                sa_tmp.setMutationabbrs(mutationabbrs);
//                sa_tmp.setIsStrainAlleleTransgenic(IsStrainAlleleTransgenic);

                strainAlleleDTOs.add(sa_tmp);
            }
            
            //logger.debug("---------------------------------------->ModelManagerBean#getStrainAllelesFromStrain: size = "+strainAlleleDTOs.size());
            
            return strainAlleleDTOs;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strain alleles");
        }
    }

    //FIXME!!! - Needs a big big fix. Possibly complete removal.
//    public StrainDTO getStrainFromModel(int eid, TgDbCaller caller) throws ApplicationException{
//       try
//       {
//           ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
//           StrainDTO strain = new StrainDTO(model.getStrain());
//           return strain;
//       }
//       catch (Exception e)
//       {
//           e.printStackTrace();
//           throw new ApplicationException("Failed to get strain from the model ["+eid+"]. Model does not have a strain. Contact system administrator");
//       }
//   }

    public Collection getStrainsByModel(int eid, TgDbCaller caller) throws ApplicationException {
       try
       {
           Collection strain_dtos = new ArrayList();
           Collection strains = strainHome.findByModel(eid, caller);
           Iterator i = strains.iterator();

           while(i.hasNext()) strain_dtos.add(new StrainDTO((StrainRemote)i.next()));

           return strain_dtos;

//           StrainRemote strain = strainHome.findByPrimaryKey(new Integer(strainId));
//            Collection strainStates = strain.getStates();
//            Collection strainStateDTOs = new ArrayList();
//            Iterator i = strainStates.iterator();
//            while (i.hasNext())
//            {
//                StrainStateRemote state = (StrainStateRemote)i.next();
//                strainStateDTOs.add(new StrainStateDTO(state));
//            }
//
//            return strainStateDTOs;
       }
       catch (Exception e) {
           e.printStackTrace();
           throw new ApplicationException("Failed to get strains for model "+eid+"\n");
       }
   }

   public Collection getUnassignedStrains(int eid, TgDbCaller caller) throws ApplicationException {
       try
       {
           Collection strain_dtos = new ArrayList();
           Collection strains = strainHome.findUnassigned(eid, caller);
           Iterator i = strains.iterator();

           while(i.hasNext()) strain_dtos.add(new StrainDTO((StrainRemote)i.next()));

           return strain_dtos;
       }
       catch (Exception e) {
           e.printStackTrace();
           throw new ApplicationException("Failed to get strains for model "+eid+"\n");
       }
   }

   public void assignStrainToModel(int eid, int strain, TgDbCaller caller) throws ApplicationException {
       try {
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            model.addStrain(strain);
       }
       catch (Exception e) {
           e.printStackTrace();
           throw new ApplicationException("Failed to assign strain to model "+eid+"\n");
       }

   }

   public void unassignStrainFromModel(int eid, int strain, TgDbCaller caller) throws ApplicationException {
       try {
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            model.clearStrain(strain);
       }
       catch (Exception e) {
           e.printStackTrace();
           throw new ApplicationException("Failed to assign strain to model "+eid+"\n");
       }

   }
    
    public Collection getStrainsFromMgiid(String strainid, TgDbCaller caller) throws ApplicationException{
        try
        {
            //ZOUB FIX - Find usages of the method and find workaround
//            Collection strainz = strainHome.findByMgiid(strainid, caller);
            Collection strainz = new ArrayList();
            
            Collection strainzDTOs = new ArrayList();
            Iterator i = strainz.iterator();
            while (i.hasNext())
            {
                StrainRemote some_strain = (StrainRemote)i.next();
                strainzDTOs.add(new StrainDTO(some_strain));
            }
            
            logger.debug("---------------------------------------->ModelManagerBean#getStrainFromMgiid: size = "+strainzDTOs.size());
            
            return strainzDTOs;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to get strainz by mgiid");
        }
    }

    public Collection getStrainLinks(int strainid, TgDbCaller caller) throws ApplicationException {
        try {
            StrainRemote strain = strainHome.findByPrimaryKey(new Integer(strainid));
            return strain.getStrain_links();
        }
        catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public void createStrainLink(int strainid, String repository, String externalid, String strainurl, TgDbCaller caller) throws ApplicationException {
        try {
            StrainRemote strain = strainHome.findByPrimaryKey(new Integer(strainid));
            strain.insertStrain_link(repository, externalid, strainurl);
        }
        catch(Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public void deleteStrainLink(int strainid, int strain_link_id, TgDbCaller caller) throws ApplicationException {
        try {
            StrainRemote strain = strainHome.findByPrimaryKey(new Integer(strainid));
            strain.deleteStrain_link(strain_link_id);
        }
        catch(Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }
    //</editor-fold>
    
    //genetic background functions
    //<editor-fold defaultstate="collapsed">
   public Collection getGeneticBackground(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {                                            
            Collection genBack = genbackHome.findByGeneticBackgroundModel(eid);
            Collection dtos = new ArrayList();
            Iterator i = genBack.iterator();
            while(i.hasNext()) {                
                dtos.add(new GeneticBackgroundDTO((GeneticBackgroundRemote)i.next()));
            }
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genetic back*** for model "+eid, e);
        }
    }
   
   public GeneticBackgroundDTO getGeneticBackgroundDTO(int eid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {                                            
            Collection genBack = genbackHome.findByGeneticBackgroundModel(eid);
            GeneticBackgroundDTO dto = null;
            Iterator i = genBack.iterator();
            if(i.hasNext()) {                
                dto = new GeneticBackgroundDTO((GeneticBackgroundRemote)i.next());
            }
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genetic back*** for model "+eid, e);
        }
    }
   
   public Collection getGeneticBackgroundsByProject(int pid, TgDbCaller caller) throws ApplicationException{
        try {                                            
            Collection genbacks = genbackValuesHome.findByProject(pid);
            Collection dtos = new ArrayList();
            Iterator i = genbacks.iterator();
            while(i.hasNext()) {                
                dtos.add(new GeneticBackgroundValuesDTO((GeneticBackgroundValuesRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get genetic background information.", e);
        }
    }
    
    public void updateGeneticBackgroundForModel(int eid, int dna_origin, int targeted_back, int host_back, int backcrossing_strain, String backcrosses, org.tgdb.TgDbCaller caller) throws ApplicationException {        
        try{                       
            //the collection returned contains only one interface. should it contain more than one then a while loop needs to be implemented.
            Collection genBack = genbackHome.findByGeneticBackgroundModel(eid);
            Iterator i = genBack.iterator();
            //if(i.hasNext()) {                
                GeneticBackgroundRemote genBackRemote = (GeneticBackgroundRemote)i.next();
            //}
            genBackRemote.setDna_origin(dna_origin);
            genBackRemote.setTargeted_back(targeted_back);
            genBackRemote.setHost_back(host_back);
            genBackRemote.setBackcrossing_strain(backcrossing_strain);
            genBackRemote.setBackcrosses(backcrosses);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new ApplicationException("Could not update genetic background information");
        }
    }//updateGeneBackValue
    
    public void setGeneticBackgroundForModel (int eid, int dna_origin, int targeted_back, int host_back, int backcrossing_strain, String backcrosses) throws ApplicationException{
        try
        {
            makeConnection();
            int gbid = getIIdGenerator().getNextId(conn, "genetic_back_seq");
            genbackHome.create(gbid, eid, dna_origin, targeted_back, host_back, backcrossing_strain, backcrosses);
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to create genetic background for model ",e);
        }
        finally
        {
            releaseConnection();
        }
    }//setGeneticBackgroundForModel
    
    public int createGeneBackValue(java.lang.String backname, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            makeConnection();
            ProjectRemote project = projectHome.findByPrimaryKey(new Integer(caller.getPid()));
            int bid = getIIdGenerator().getNextId(conn, "genetic_back_values_seq");            
            //GeneRemote gene = geneHome.create(gaid, name, comm, mgiid, genesymbol, geneexpress, idgene, idensembl, project, caller);
            GeneticBackgroundValuesRemote genbackValues = genbackValuesHome.create(bid,backname,project);
            return genbackValues.getBid();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not create genetic background strain\n"+e.getMessage(),e);            
        } finally {
            releaseConnection();
        }        
    }//createGeneBackValue
    
    public void updateGeneBackValue(int bid, String backname, org.tgdb.TgDbCaller caller) throws ApplicationException {        
        try{ 
            GeneticBackgroundValuesRemote genbackValues = genbackValuesHome.findByPrimaryKey(new Integer(bid));                      
            genbackValues.setBackname(backname);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new ApplicationException("Could not update genetic background strain");
        }
    }//updateGeneBackValue
    
    public String getGeneBackValueName(int bid, org.tgdb.TgDbCaller caller) throws ApplicationException {        
        try{ 
            GeneticBackgroundValuesRemote genbackValues = genbackValuesHome.findByPrimaryKey(new Integer(bid));                      
            String backname = genbackValues.getBackname();
            return backname;
        }
        catch(Exception e){
            e.printStackTrace();
            throw new ApplicationException("Could not get genetic background strain name for id "+bid+"\n");
        }
    }
    
    //</editor-fold>
   
    //mutation types functions
    //<editor-fold defaultstate="collapsed">

   public void createMutationType(String name, String abbreviation, TgDbCaller caller) throws ApplicationException {
       try {
            makeConnection();
            int id = getIIdGenerator().getNextId(conn, "mutation_type_seq");
            MutationTypeRemote mutation_type = mutationTypeHome.create(id, name, caller);
            mutation_type.setAbbreviation(abbreviation);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to add a new mutation type. ");
        }
        finally {
            releaseConnection();
        }
   }

   public void updateMutationType(int mtid, String name, String abbreviation, TgDbCaller caller) throws ApplicationException {
       try {
           MutationTypeRemote mutation_type = mutationTypeHome.findByPrimaryKey(new Integer(mtid));
           mutation_type.setName(name);
           mutation_type.setAbbreviation(abbreviation);
       }
       catch (Exception e) {
           e.printStackTrace();
           throw new ApplicationException("Failed to update mutation types", e);
       }
   }

   public void deleteMutationType(int mtid, TgDbCaller caller) throws ApplicationException {
       try {
           MutationTypeRemote mutation_type = mutationTypeHome.findByPrimaryKey(new Integer(mtid));
           mutation_type.remove();
       }
       catch (Exception e) {
           e.printStackTrace();
           throw new ApplicationException("Failed to delete mutation types", e);
       }
   }

   public MutationTypeDTO getMutationType(int mtid, TgDbCaller caller) throws ApplicationException {
       try {
           MutationTypeRemote mutation_type = mutationTypeHome.findByPrimaryKey(new Integer(mtid));
           return new MutationTypeDTO(mutation_type);
       }
       catch (Exception e) {
           e.printStackTrace();
           throw new ApplicationException("Failed to get mutation types", e);
       }
   }

   public Collection getMutationTypes(int pid, TgDbCaller caller) throws ApplicationException{
       try
       {
           Collection dtos = new ArrayList();
           Collection mutationTypes = mutationTypeHome.findByProject(pid, caller);
           Iterator i = mutationTypes.iterator();
           while (i.hasNext())
           {
               MutationTypeRemote mutationType = (MutationTypeRemote)i.next();
               dtos.add(new MutationTypeDTO(mutationType));
           }
           return dtos;
       }
       catch (Exception e)
       {
           e.printStackTrace();
           throw new ApplicationException("Failed to get mutation types", e);
       }
   }
   
   public Collection getMutationTypesByAbbreviation(String abbreviation, TgDbCaller caller) throws ApplicationException{
       try
       {
           Collection dtos = new ArrayList();
           Collection mutationTypes = mutationTypeHome.findByAbbreviation(abbreviation, caller);
           Iterator i = mutationTypes.iterator();
           while (i.hasNext())
           {
               MutationTypeRemote mutationType = (MutationTypeRemote)i.next();
               dtos.add(new MutationTypeDTO(mutationType));
           }
           return dtos;
       }
       catch (Exception e)
       {
           e.printStackTrace();
           throw new ApplicationException("Failed to get mutation types by abbreviation", e);
       }
   }
   
   public Collection getUnassignedMutationTypes(int strainalleleid, TgDbCaller caller) throws ApplicationException{
       try
       {
           Collection dtos = new ArrayList();
           //Collection mutationTypes = mutationTypeHome.findByProject(pid, caller);
           Collection mutationTypes = mutationTypeHome.findByStrainAlleleUnassignment(strainalleleid, caller);
           Iterator i = mutationTypes.iterator();
           while (i.hasNext())
           {
               MutationTypeRemote mutationType = (MutationTypeRemote)i.next();
               dtos.add(new MutationTypeDTO(mutationType));
           }
           return dtos;
       }
       catch (Exception e)
       {
           e.printStackTrace();
           throw new ApplicationException("Failed to get mutation types", e);
       }
   }
   //</editor-fold>
  
    //availability functions
    //<editor-fold defaultstate="collapsed">
    public Collection getBackcrossesCollection(){
        Collection backcrossesCollection = new ArrayList();
        for(int i=1; i<10; i++){
            backcrossesCollection.add("0"+(new Integer(i).toString()));
        }//for
        backcrossesCollection.add(">=10");
        backcrossesCollection.add("n/a");
        
        return backcrossesCollection;
    }
    
    public Collection getAvailableGeneticBackgroundsByProject(int pid) throws ApplicationException {
        try {                                            
            Collection avgenbacks = avgenbackHome.findByProject(pid);
            Collection dtos = new ArrayList();
            Iterator i = avgenbacks.iterator();
            while(i.hasNext()) {                
                dtos.add(new AvailableGeneticBackgroundDTO((AvailableGeneticBackgroundRemote)i.next()));
            }
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get the available genetic backgrounds for the current project.", e);
        }
    }
    
    public Collection getAvailabilityForModel(int eid) throws ApplicationException{
        try {
            Collection availability = availabilityHome.findByModel(eid);
            Collection dtos = new ArrayList();
            Iterator i = availability.iterator();
            while(i.hasNext()) {                
                dtos.add(new AvailabilityDTO((AvailabilityRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get availability information for specific model.", e);
        }
    }
    
    public void addAvailabilityToModel(int eid, int rid, int aid, int stateid, int typeid, int strainid) throws ApplicationException {
        try {
            ExpModelRemote model = modelHome.findByPrimaryKey(new Integer(eid));
            RepositoriesRemote repository = repositoriesHome.findByPrimaryKey(new Integer(rid));
            AvailableGeneticBackgroundRemote avgenback = avgenbackHome.findByPrimaryKey(new Integer(aid));
            StrainStateRemote state = strainStateHome.findByPrimaryKey(new Integer(stateid));
            StrainTypeRemote type = strainTypeHome.findByPrimaryKey(new Integer(typeid));
            StrainRemote strain = strainHome.findByPrimaryKey(new Integer(strainid));
            
            AvailabilityRemote availability = availabilityHome.create(model, repository, avgenback, state, type, strain);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to assign availability information to model");            
        }        
    }
    
    public void removeAvailabilityFromModel(int eid, int rid, int aid, int stateid, int typeid, int strainid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            validate("MODEL_W", caller);
            AvailabilityRemote availability = availabilityHome.findByPrimaryKey(new AvailabilityPk(eid, rid, aid, stateid, typeid, strainid));
            availability.remove();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove availability information\n"+e.getMessage(),e);            
        }        
    }
    //</editor-fold>
    
    //repository functions
    //<editor-fold defaultstate="collapsed">
    public Collection getRepositoriesByProject(int pid) throws ApplicationException {
        try {                                            
            Collection repositories = repositoriesHome.findByProject(pid);
            Collection dtos = new ArrayList();
            Iterator i = repositories.iterator();
            while(i.hasNext()) {                
                dtos.add(new RepositoriesDTO((RepositoriesRemote)i.next()));
            }
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get the repositories for the current project.", e);
        }
    }
    
    public Collection getRepositoriesByDB() throws ApplicationException {
        try {                                            
            Collection repositories = repositoriesHome.findByDB();
            Collection dtos = new ArrayList();
            Iterator i = repositories.iterator();
            while(i.hasNext()) {                
                dtos.add(new RepositoriesDTO((RepositoriesRemote)i.next()));
            }
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get the repositories that have a database.", e);
        }
    }
    
    public RepositoriesDTO returnRepositoryById (int rid) throws ApplicationException {
        try{
            RepositoriesRemote repository = repositoriesHome.findByPrimaryKey(new Integer(rid));
            return new RepositoriesDTO(repository);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not return repository\n"+e.getMessage(),e);            
        }  
    }
    
    public void updateRepositoryName (int rid, String reponame, int hasdb, String mouseurl, String repourl) throws ApplicationException {
        try{
            RepositoriesRemote repository = repositoriesHome.findByPrimaryKey(new Integer(rid));
            repository.setReponame(reponame);
            repository.setHasdb(hasdb);
            repository.setMouseurl(mouseurl);
            repository.setRepourl(repourl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not update repository's name\n"+e.getMessage(),e);            
        }
    }
    
    public int addRepository (String reponame, int hasdb, String mouseurl, String repourl, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            makeConnection();
            int rid = getIIdGenerator().getNextId(conn, "repositories_seq");            
            ProjectRemote project = projectHome.findByPrimaryKey(new Integer(caller.getPid()));
            RepositoriesRemote repository = repositoriesHome.create(rid, reponame, hasdb, mouseurl, repourl, project);
            return repository.getRid();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not create repository. \n"+e.getMessage(),e);            
        } finally {
            releaseConnection();
        }  
    }
    
    public void removeRepository(int rid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {           
            validate("MODEL_W", caller);
            RepositoriesRemote repository = repositoriesHome.findByPrimaryKey(new Integer(rid));
            repository.remove();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove repository \n"+e.getMessage());            
        }
    }
    //</editor-fold>
    
    //available genetic background functions
    //<editor-fold defaultstate="collapsed">
    public AvailableGeneticBackgroundDTO returnAvailableGeneticBackgroundById (int aid) throws ApplicationException {
        try{
            AvailableGeneticBackgroundRemote repository = avgenbackHome.findByPrimaryKey(new Integer(aid));
            return new AvailableGeneticBackgroundDTO(repository);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not return available genetic background. \n"+e.getMessage(),e);            
        }  
    }
    
    public void updateAvailableGeneticBackgroundName (int aid, String avgenbackname) throws ApplicationException {
        try{
            AvailableGeneticBackgroundRemote avgenback = avgenbackHome.findByPrimaryKey(new Integer(aid));
            avgenback.setAvbackname(avgenbackname);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not update repository's name\n"+e.getMessage(),e);            
        }
    }
    
    public int addAvailableGeneticBackground (String avgenbackname, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {
            makeConnection();
            int aid = getIIdGenerator().getNextId(conn, "available_genetic_back_seq");
            ProjectRemote project = projectHome.findByPrimaryKey(new Integer(caller.getPid()));
            AvailableGeneticBackgroundRemote avgenback = avgenbackHome.create(aid, avgenbackname, caller.getPid(), project);
            return avgenback.getAid();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not create repository. \n"+e.getMessage(),e);            
        } finally {
            releaseConnection();
        }  
    }
    
    public void removeAvailableGeneticBackground(int aid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        try {           
            validate("MODEL_W", caller);
            AvailableGeneticBackgroundRemote avgenback = avgenbackHome.findByPrimaryKey(new Integer(aid));
            avgenback.remove();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove available genetic background. \n"+e.getMessage());            
        }
    }
    //</editor-fold>
    
    //participant related methods
    //<editor-fold defaultstate="collapsed">
    public Collection getParticipants() throws ApplicationException{
        makeConnection();
        Collection participants = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select distinct group_name from users where group_name is not null");
            result = ps.executeQuery();

            while(result.next()) {
                if (result.getString("group_name").compareTo("PUBLIC") != 0){
                    participants.add(result.getString("group_name"));
                }
            }
        } catch (SQLException se) {
            logger.error("---------------------------------------->ModelManagerBean#getParticipants: Cannot get group names", se);
            throw new ApplicationException("Cannot get group names \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        
        return participants;
    }
    
    public Collection getParticipantNames() throws ApplicationException{
        makeConnection();
        Collection participants = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
//            ps = conn.prepareStatement("select distinct name from users where group_name is not null");
            ps = conn.prepareStatement("select distinct u.name as name from users u join model m on m.contact = u.id order by u.name");
            result = ps.executeQuery();

            while(result.next()) {
                //if (result.getString("group_name").compareTo("PUBLIC") != 0){
                    participants.add(result.getString("name"));
                //}
            }
        } catch (SQLException se) {
            logger.error("---------------------------------------->ModelManagerBean#getParticipantNames: Cannot get partincipant names", se);
            throw new ApplicationException("Cannot get participant names \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        
        return participants;
    }
    //</editor-fold>
    
    //chromosome methods
    //<editor-fold defaultstate="collapsed">
    
    public Collection getChromosomesForSpecies(int sid, TgDbCaller caller) throws ApplicationException{
        try {
            logger.debug("---------------------------------------->ModelManagerBean#getChromosomesForSpecies: sid = "+sid);
            SpeciesRemote species = speciesHome.findByPrimaryKey(new Integer(sid));
            Collection chromosomes = species.getChromosomes();
            
            //Collection chromosomes = chromoHome.findAllChromosomes();
            Collection dtos = new ArrayList();
            Iterator i = chromosomes.iterator();
            while(i.hasNext()) {                
                dtos.add(new ChromosomeDTO((ChromosomeRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get chromosomes.", e);
        }
    }
    
    public Collection getChromosomesByAbbreviation(String abbreviation, TgDbCaller caller) throws ApplicationException{
        try {
            logger.debug("---------------------------------------->ModelManagerBean#getChromosomesByAbbreviation: abbreviation = "+abbreviation);
            Collection chromosomes = chromosomeHome.findByAbbreviation(abbreviation);
            
            Collection dtos = new ArrayList();
            Iterator i = chromosomes.iterator();
            while(i.hasNext()) {                
                dtos.add(new ChromosomeDTO((ChromosomeRemote)i.next()));
            }
            
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get chromosomes.", e);
        }
    }
    
    //</editor-fold>
    
    //misc+crucial functions
    //<editor-fold defaultstate="collapsed">
    public SpeciesDTO getSpecies(int sid, TgDbCaller caller) throws ApplicationException{
        try
        {
            SpeciesRemote spc = speciesHome.findByPrimaryKey(new Integer(sid));
            return new SpeciesDTO(spc);
        } 
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new ApplicationException("Failed to get species");
        } 
    }
    
    public Collection getResourceTreeCollection(int eid, TgDbCaller caller) throws ApplicationException{
        //logger.debug("---------------------------------------->ModelManagerBean#getResourceTreeCollection");
        Collection resourceTree = new ArrayList();
        try
        {
            ExpModelRemote m = modelHome.findByPrimaryKey(new Integer(eid));
            Collection resources = m.getResources();
            
            return resourceManager.getResourceTreeCollection(resources, caller);
        }   
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("Failed to get resources", e);
        }
    }
    
    public Collection searchByProject(String name, TgDbCaller caller) throws ApplicationException{
        Collection arr = new TreeSet();
        try
        {
            Collection projects = projectHome.findByName(name, caller);
            
            Iterator iPrj = projects.iterator();
            while (iPrj.hasNext())
            {
                try
                {
                    ProjectRemote prj = (ProjectRemote)iPrj.next();

                    validatePid("MODEL_R", caller, prj.getPid());

                    Collection samplingUnits = prj.getSamplingUnits();
                    Iterator iSu = samplingUnits.iterator();
                    while (iSu.hasNext())
                    {
                        SamplingUnitRemote su = (SamplingUnitRemote)iSu.next();
                        Collection models = su.getExperimentalModels();
                        Iterator iModels = models.iterator();
                        while (iModels.hasNext())
                        {
                            ExpModelRemote model = (ExpModelRemote)iModels.next();
                            arr.add(new ExpModelDTO(model));
                        }
                    }
                }
                catch (PermissionDeniedException pde)
                {
                    logger.error("---------------------------------------->ModelManagerBean#searchByProject: Permission denied");
                    pde.printStackTrace();
                }
            }
        }
        catch (FinderException fe)
        {
            fe.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("failed to search by research application",e);
        }
        return arr;
    }
   
    public Collection searchByKeyword(String keyword, TgDbCaller caller) throws ApplicationException{
        Collection arr = new ArrayList();
        try
        {
            //TgDbCaller searchCaller = getSearchCaller();
            TgDbCaller searchCaller = caller;
            Keyword key = new Keyword(keyword);
            arr.addAll(searchModelByKeyword(key, searchCaller));
            arr.addAll(searchGeneByKeyword(key, searchCaller)); 
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ApplicationException("failed to search by research application",e);
        }
        return arr;
    }
    
    /**
     * Get a caller object used for searching. 
     * This returns user "public" that must exist in the database.
     *s
     * @throws org.tgdb.exceptions.ApplicationException if the public user is missing
     * @return a TgDbCaller object
     */
    public TgDbCaller getSearchCaller() throws ApplicationException{
        try
        {
            UserRemote user = userHome.findByUsr("public");
            return new TgDbCaller(user);
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to get search caller. Is user public created?");
        }
    }
    
    public Collection getInducibility() throws ApplicationException {
        makeConnection();
        Collection inducibility = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select distinct(inducible) from model where inducible !=''");
            result = ps.executeQuery();

            while(result.next()) {
                    inducibility.add(result.getString("inducible"));
            }
            
        } catch (SQLException se) {
            logger.error("---------------------------------------->ModelManagerBean#getInducibility: Cannot get inducibility values", se);
            throw new ApplicationException("Cannot get inducibility \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        
        return inducibility;
    }
    
    public Collection getMadeBy() throws ApplicationException {
        makeConnection();
        Collection inducibility = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select distinct(made_by) from strain_allele where made_by != ''");
            result = ps.executeQuery();

            while(result.next()) {
                    inducibility.add(result.getString("made_by"));
            }
            
        } catch (SQLException se) {
            logger.error(se);
//            throw new ApplicationException("Cannot get inducibility \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        
        return inducibility;
    }
    //</editor-fold>
    
    //simple methods with no db connectivity
    //<editor-fold defaultstate="collapsed">
    public Collection getOrderByTypes(){
        Collection orderByCollection = new ArrayList();
        
        orderByCollection.add("MMMDb ID");
        orderByCollection.add("LINE NAME");
        orderByCollection.add("DATE");
        
        return orderByCollection;
    }
    
    public Collection getOrderByTypes2(){
        Collection orderByCollection = new ArrayList();
        
        orderByCollection.add("MMMDb ID");
        orderByCollection.add("DATE");
        
        return orderByCollection;
    }
    
    public Collection getMutationTypeAttributes(){
        Collection mtaCollection = new ArrayList();
        
        mtaCollection.add("CONDITIONAL");
        mtaCollection.add("INDUCIBLE");
        mtaCollection.add("CONDITIONAL+INDUCIBLE");
        mtaCollection.add("N/A");
        mtaCollection.add("OTHER");
        
        return mtaCollection;
    }
    
    public Collection getLevelsForModel(){
        Collection arr = new ArrayList();
        arr.add(new String("Public"));
        arr.add(new String("Mugen"));
        arr.add(new String("Admin"));
        return arr;
    }
    
    public Collection getHasdbValues(){
        Collection arr = new ArrayList();
        arr.add(new String("0"));
        arr.add(new String("1"));
        return arr;
    }

    public Collection getInducibleValues(){
        Collection orderByCollection = new ArrayList();

        orderByCollection.add("YES");
        orderByCollection.add("NO");
        orderByCollection.add("N/A");

        return orderByCollection;
    }
    //</editor-fold>
    
    //load file data methods
    //<editor-fold defaultstate="collapsed">
    public boolean loadMiceFromExcel(byte[] data, TgDbCaller caller) throws ApplicationException {
        boolean completed = false;
        try{
            FileOutputStream tmp = new FileOutputStream("C:/tmp/data/tgmice.dat");
            tmp.write(data);
            tmp.close();
            data = null;

            File re_file = new File("C:/tmp/data/tgmice.dat");

            BufferedReader input = null;
            input = new BufferedReader(new FileReader(re_file));

            String line = null;
            int line_type = 0;

            String [] tg_mouse = null;

            while ((line = input.readLine()) != null){
                tg_mouse = line.split("\t", -2);

//                System.out.println("--------------MODEL DATA--------------");
//                System.out.println("Line Name: \t" + tg_mouse[4]);
//                System.out.println("Inducible: \t" + tg_mouse[7]);
//                System.out.println("--------------ALLELE DATA--------------");
//                System.out.println("Allele MGI ID: \t" + tg_mouse[0]);
//                System.out.println("Allele Symbol: \t" + tg_mouse[1]);
//                System.out.println("Allele Name: \t" + tg_mouse[2]);
//                System.out.println("--------------STRAIN DATA--------------");
//                System.out.println("Strain: \t" + tg_mouse[3]);
//                System.out.println("MGI ID: \t" + tg_mouse[11]);
//                System.out.println("EMMA ID: \t" + tg_mouse[12]);
//                System.out.println("MMRRC ID: \t" + tg_mouse[13]);
//                System.out.println("--------------PROMOTER DATA--------------");
//                System.out.println("Promoter Name: \t" + tg_mouse[5]);
//                System.out.println("--------------PUBMED--------------");
//                System.out.println("Pubmed: \t" + tg_mouse[6]);

                //try to locate model then try to create it

                int eid = 0;
                try {
                    eid = modelHome.findByNAME(tg_mouse[4]).getEid();
                }
                catch (Exception e) {
                    eid = createModelAuto(tg_mouse[4], tg_mouse[7], caller);
                }

                if(eid == 0) throw new Exception("Model with name " + tg_mouse[4] + " could not be found and not be created either.");

                ExpModelRemote model_remote = modelHome.findByPrimaryKey(new Integer(eid));

                logger.debug("-----------------------------------------------------------------------------------------");
                logger.debug("Created model with name " + model_remote.getAlias() + " and id " + model_remote.getEid());

                int allele = 0;

                try {
                    allele = strainAlleleHome.findByNAME(tg_mouse[1]).getId();
                }
                catch (Exception e) {
                    allele = createAlleleAuto(tg_mouse[1], tg_mouse[2], tg_mouse[0], caller);
                }

                if(allele == 0) throw new Exception("Allele with name " + tg_mouse[2] + " cound not be found and not be created either.");

                //add the allele to the model (mutation_type = 0)
                StrainAlleleRemote allele_remote = strainAlleleHome.findByPrimaryKey(new Integer(allele));
                logger.debug("Created allele with name " + allele_remote.getName() + " and id " + allele_remote.getId());
                if(!allele_remote.isAssigned(eid, 0, null)) allele_remote.addMutationTypeAndAttribute(eid, 0, null);
                logger.debug("Added allele with name "+allele_remote.getName() + " to model with name " + model_remote.getAlias());

                int strain = 0;

                try {
                    strain = strainHome.findByNAME(tg_mouse[3]).getStrainid();
                } catch(Exception e) {
                    strain = createStrainAuto(tg_mouse[3], caller);
                }

                if(strain==0) throw new Exception("Strain with name " + tg_mouse[3] + " cound not be found and not be created either.");
                
                StrainRemote strain_remote = strainHome.findByPrimaryKey(new Integer(strain));


                logger.debug("Created strain with name "+strain_remote.getDesignation() + " and id " + strain_remote.getStrainid());
                strain_remote.insertStrain_link("MGI", tg_mouse[11], "#");
                strain_remote.insertStrain_link("EMMA", tg_mouse[12], "#");
                strain_remote.insertStrain_link("MMRRC", tg_mouse[13], "#");

                try {
                    model_remote.addStrain(strain);
                } catch(Exception e) {
                    logger.error("Failed to add strain with name "+strain_remote.getDesignation() + " and id " + strain_remote.getStrainid() + " to model with name " + model_remote.getAlias() + " and id " + model_remote.getEid() + ".\n"+getStackTrace(e));
                }

                int promoter = 0;

                try {
                    promoter = geneHome.findByNAME(tg_mouse[5]).getGaid();
                } catch(Exception e) {
                    promoter = createPromoterAuto(tg_mouse[5], 24, caller);
                }

                if(promoter == 0) throw new Exception("Promoter with name " + tg_mouse[5] + " cound not be found and not be created either.");

                try {
                    GeneRemote promoter_remote = geneHome.findByPrimaryKey(new Integer(promoter));
                    
                    if(!promoter_remote.isAssigned(eid, "promoter")) addGeneToModel(promoter, eid, caller);
                }
                catch(Exception e) {
                    logger.error("Failed to add promoter with name " + tg_mouse[5] + " and id "+ promoter + " to model with name " + model_remote.getAlias() + " and id " + model_remote.getEid());
                }

                if(tg_mouse[6] != null && tg_mouse[6].length() > 0) addLinkReference(eid, "Primary publication", tg_mouse[6], true, "Primary publication", "http://", caller);

            }
            completed = true;
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return completed;
    }

    public int createModelAuto(String model_name, String inducible, org.tgdb.TgDbCaller caller) throws ApplicationException {
        ExpModelRemote model = null;
        SamplingUnitRemote samplingUnit = null;
        ResearchApplicationRemote researchApplication = null;
        UserRemote contactUser = null;

        //default sampling unit id
        int suid = 1003;
        String geneticBackground = "n.a.";
        String availability = "n.a.";
        String researchApplications = "";
        //research application type is transgenic tool
        int type = 9001;
        //default contact person is admin
        int contact = 1001;
        String comm = "automatically imported model";
        int to_return = 0;

        try {
            makeConnection();
            //get the id
            int eid = getIIdGenerator().getNextId(conn, "expobj_seq");
            //get sampling unit
            samplingUnit = samplingUnitHome.findByPrimaryKey(new Integer(suid));
            //get research app
            researchApplication = researchAppHome.findByPrimaryKey(new Integer(type));
            //get user
            contactUser = userHome.findByPrimaryKey(new Integer(contact));
            //create model
            model = modelHome.create(eid, model_name, samplingUnit, caller);
            //add to model...
            model.setCaller(caller);
            model.setAlias(model_name);
            model.setComm(comm);
            model.setContact(contactUser);
            model.setAvailability(availability);
            model.setResearchApplicationText(researchApplications);
            model.setResearchApplication(researchApplication);
            model.setGeneticBackground(geneticBackground);

            model.setDesiredLevel(0);
            model.setLevel(0);

            if (inducible.equalsIgnoreCase("Y")) {
                inducible = "YES";
            }
            else if(inducible.equalsIgnoreCase("N")) {
                inducible = "NO";
            }
            model.setInducible(inducible);

            to_return = eid;
        }
        catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        finally {
            releaseConnection();
        }
        return to_return;
    }

    public int createAlleleAuto(String symbol, String name, String mgiid, TgDbCaller caller) throws ApplicationException{
        int to_return = 0;
        try {
            makeConnection();
            int id = getIIdGenerator().getNextId(conn, "strain_allele_seq");
            StrainAlleleRemote strainAllele = strainAlleleHome.create(id, symbol, name, caller);
            strainAllele.setMgiId(mgiid);
            to_return = strainAllele.getId();
        }
        catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        finally {
            releaseConnection();
        }
        return to_return;
    }

    public int createStrainAuto(String designation, TgDbCaller caller) throws ApplicationException {
        int to_return = 0;
       try {
            makeConnection();
            int strain_id = getIIdGenerator().getNextId(conn, "strain_seq");
            to_return = strainHome.create(strain_id, designation, caller).getStrainid();
        }
        catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        finally {
            releaseConnection();
        }
        return to_return;
   }

    public int createPromoterAuto(String name, int cid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        int to_return = 0;
        try {
            makeConnection();
            int gaid = getIIdGenerator().getNextId(conn, "gene_seq");
            GeneRemote gene = geneHome.create(gaid, name, name, cid, caller);
            gene.setDistinguish("promoter");
            to_return = gene.getGaid();
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        } finally {
            releaseConnection();
        }
        return to_return;
    }
    private int getStrainStateId(String abbreviation, TgDbCaller caller) throws ApplicationException{
        int state_id = 0;
        try{
            Collection tmp_strain_states = getStrainStatesByAbbreviation(abbreviation.trim(), caller);
            
            Iterator tmp_state_it = tmp_strain_states.iterator();
            
            while(tmp_state_it.hasNext()){
                StrainStateDTO tmp_state = (StrainStateDTO)tmp_state_it.next();
                state_id = tmp_state.getId();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not get strain state id by abbeviation.\n"+e.getMessage());            
        }
        return state_id;
    }
    
    private int getStrainTypeId(String abbreviation, TgDbCaller caller) throws ApplicationException{
        int type_id = 0;
        try{
            Collection tmp_strain_types = getStrainTypesByAbbreviation(abbreviation.trim(), caller);
            
            Iterator tmp_type_it = tmp_strain_types.iterator();
            
            while(tmp_type_it.hasNext()){
                StrainTypeDTO tmp_type = (StrainTypeDTO)tmp_type_it.next();
                type_id = tmp_type.getId();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not get strain type id by abbeviation.\n"+e.getMessage());            
        }
        return type_id;
    }
    
    private int returnMax(int one, int two){
        int toReturn = 0;
        
        if(one > two)
            toReturn = one;
        
        if(one < two)
            toReturn = two;
        
        return toReturn;
    }
    
    private int returnMin(int one, int two){
        int toReturn = 0;
        
        if(one < two)
            toReturn = one;
        
        if(one > two)
            toReturn = two;
        
        return toReturn;
    }
    
    private boolean checkTg(String designation){
        boolean is_tg = false;
        String [] tg_key = {"cre", "Cre", "rtTA", "FLP", "rtta", "flp"};
        for(int i=0; i < tg_key.length; i++){
            if(designation.contains(tg_key[i])){
                is_tg = true;
                return is_tg;
            }
        }
        //if(designation.con)
        return is_tg;
    }
    
    public boolean loadTgs(int repo, byte[] data, TgDbCaller caller) throws ApplicationException {
        boolean completed = false;
        try{
            FileOutputStream tmp = new FileOutputStream("C:/tmp/data/tgmice.dat");
            tmp.write(data);
            tmp.close();
            data = null;
            
            File re_file = new File("C:/tmp/data/tgmice.dat");
            
            BufferedReader input = null;
            input = new BufferedReader(new FileReader(re_file));
            
            String line = null;
            int line_type = 0;
            
            String [] tg_mouse = null;
            
            while ((line = input.readLine()) != null){
                tg_mouse = line.split("\t", -2);
                if(tg_mouse.length >= 8 && tg_mouse[7].compareTo("TG")==0 && checkTg(tg_mouse[1])){
                    
                    if(tg_mouse.length < 11){
                        doLineTypeOne(repo, tg_mouse, caller);
                    }else if(tg_mouse.length > 10 && tg_mouse.length < 12){
                        doLineTypeTwo(repo, tg_mouse, caller);
                    }else if(tg_mouse.length > 11 && tg_mouse.length < 13){
                        doLineTypeThree(repo, tg_mouse, caller);
                    }
                }
            }
            completed = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not parse uploaded file.\n"+e.getMessage());            
        }
        return completed;
    }
    
    public void doLineTypeOne(int repo, String [] tg_mouse, TgDbCaller caller) throws ApplicationException{
        try{
                
                    logger.debug("---------------------------------------->ModelManagerBean#doLineTypeOne: Processing "+tg_mouse[0]);
                    if(getStrainsFromMgiid(tg_mouse[0], caller).isEmpty()){
                        
                        //create the model+strain
                        int tmp_eid = createModelAutomatic(tg_mouse[0], tg_mouse[1], caller);
                        
                        //start working on the availability of the tg mouse
                        int av_gen_back_id = 57000;
                        if(tg_mouse[2].indexOf(",")==-1 && tg_mouse[3].indexOf(",")==-1){
                            //FIX ME - Find the strain id properly!!!
                            addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tg_mouse[3].trim(), caller), getStrainTypeId(tg_mouse[2].trim(), caller), 666);
                        }else{
                            String [] tmp_types = null;
                            String [] tmp_states = null;
                            
                            if(tg_mouse[2].indexOf(",")!=-1){
                                tmp_types = tg_mouse[2].split(",");
                            }else{
                                tmp_types = new String[1];
                                tmp_types[0] = tg_mouse[2].trim();
                            }
                            
                            if(tg_mouse[3].indexOf(",")!=-1){
                                tmp_states = tg_mouse[3].split(",");
                            }else{
                                tmp_states = new String[1];
                                tmp_states[0] = tg_mouse[3].trim();
                            }
                            
                            if(tmp_types.length == tmp_states.length){
                                for(int some_i=0; some_i < tmp_states.length; some_i++){
                                    //FIX ME - Find the strain id properly!!!
                                    addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tmp_states[some_i].trim(), caller), getStrainTypeId(tmp_types[some_i].trim(), caller), 666);
                                }
                                
                            }else{
                                int max_tmp = returnMax(tmp_states.length, tmp_types.length);
                                int min_tmp = returnMin(tmp_states.length, tmp_types.length);
                                
                                for(int some_ii=0; some_ii < min_tmp; some_ii++){
                                    //FIX ME - Find the strain id properly!!!
                                    addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tmp_states[some_ii].trim(), caller), getStrainTypeId(tmp_types[some_ii].trim(), caller), 666);
                                }
                                
                                if(tmp_states.length == max_tmp){
                                    for(int some_iii=min_tmp; some_iii < max_tmp; some_iii++){
                                        //FIX ME - Find the strain id properly!!!
                                        addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tmp_states[some_iii].trim(), caller), getStrainTypeId(tmp_types[min_tmp-1].trim(), caller), 666);
                                    }
                                }//case states are more than types
                                
                                if(tmp_types.length == max_tmp){
                                   for(int some_iiii=min_tmp; some_iiii < max_tmp; some_iiii++){
                                       //FIX ME - Find the strain id properly!!!
                                        addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tmp_states[min_tmp-1].trim(), caller), getStrainTypeId(tmp_types[some_iiii].trim(), caller), 666);
                                    } 
                                }//case types are more than states
                            }
                        }
                        //end of availability handling section
                        
                        //start working on the allele info of the tg mouse
                        String mgiid_num = tg_mouse[4].substring(tg_mouse[4].indexOf(":")+1).trim();
                        createStrainAlleleAdvanced(tg_mouse[5].trim(),tg_mouse[6].trim(), mgiid_num, caller);
                        
                        //start working on the chromosome
                        int chromo_id_tmp = 24;
                        if(tg_mouse.length > 8){
                            if(tg_mouse[8].compareTo("")!=0){
                                Collection chromos = getChromosomesByAbbreviation(tg_mouse[8], caller);
                                Iterator chromo_it = chromos.iterator();
                                while(chromo_it.hasNext()){
                                    ChromosomeDTO chromo_now = (ChromosomeDTO)chromo_it.next();
                                    chromo_id_tmp = chromo_now.getCid();
                                }
                            }
                        }
                        
                        //start working on the gene info of the tg mouse
                        //tg_mouse.length < 11 => gene mgiid:yes | gene name:no | gene symbol:no
                        if(tg_mouse.length < 11){
                            //if mgi id is not bogus...
                            if(tg_mouse[9].compareTo("")!=0 && tg_mouse[9].compareTo("MGI:")!=0){
                                //if there's no gene with such an mgi id
                                if(getGeneByMgiid(tg_mouse[9].substring(tg_mouse[9].indexOf(":")+1).trim(), caller).isEmpty()){
                                    int new_gaid = createTransgene("transgene for tg mouse "+tmp_eid, "", tg_mouse[9].substring(tg_mouse[9].indexOf(":")+1), "", "", "", "", chromo_id_tmp, "molecular_note", "molecular_note_url", caller);
                                    addGeneToModel(new_gaid, tmp_eid, caller);
                                }else{
                                    int gene_id_tmp = 0;
                                    Collection genes_tmp = getGeneByMgiid(tg_mouse[9].substring(tg_mouse[9].indexOf(":")+1).trim(), caller);
                                    Iterator genes_it = genes_tmp.iterator();
                                    while(genes_it.hasNext()){
                                        GeneDTO gene_tmp = (GeneDTO)genes_it.next();
                                        gene_id_tmp = gene_tmp.getGaid();
                                    }
                                    addGeneToModel(gene_id_tmp, tmp_eid, caller);
                                }
                            }
                        
                        }//if(tg_mouse.length < 11)
                        
                    }//if(getStrainsFromMgiid(tg_mouse[0], caller).isEmpty())
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not parse uploaded file.\n"+e.getMessage());            
        }
    }
    
    public void doLineTypeTwo(int repo, String [] tg_mouse, TgDbCaller caller) throws ApplicationException{
        try{
                    logger.debug("---------------------------------------->ModelManagerBean#doLineTypeTwo: Processing "+tg_mouse[0]);
                    if(getStrainsFromMgiid(tg_mouse[0], caller).isEmpty()){
                        
                        //create the model+strain
                        int tmp_eid = createModelAutomatic(tg_mouse[0], tg_mouse[1], caller);
                        
                        //start working on the availability of the tg mouse
                        int av_gen_back_id = 57000;
                        if(tg_mouse[2].indexOf(",")==-1 && tg_mouse[3].indexOf(",")==-1){
                            //FIX ME - Find the strain id properly!!!
                            addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tg_mouse[3].trim(), caller), getStrainTypeId(tg_mouse[2].trim(), caller), 666);
                        }else{
                            String [] tmp_types = null;
                            String [] tmp_states = null;
                            
                            if(tg_mouse[2].indexOf(",")!=-1){
                                tmp_types = tg_mouse[2].split(",");
                            }else{
                                tmp_types = new String[1];
                                tmp_types[0] = tg_mouse[2].trim();
                            }
                            
                            if(tg_mouse[3].indexOf(",")!=-1){
                                tmp_states = tg_mouse[3].split(",");
                            }else{
                                tmp_states = new String[1];
                                tmp_states[0] = tg_mouse[3].trim();
                            }
                            
                            if(tmp_types.length == tmp_states.length){
                                for(int some_i=0; some_i < tmp_states.length; some_i++){
                                    //FIX ME - Find the strain id properly!!!
                                    addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tmp_states[some_i].trim(), caller), getStrainTypeId(tmp_types[some_i].trim(), caller), 666);
                                }
                                
                            }else{
                                int max_tmp = returnMax(tmp_states.length, tmp_types.length);
                                int min_tmp = returnMin(tmp_states.length, tmp_types.length);
                                
                                for(int some_ii=0; some_ii < min_tmp; some_ii++){
                                    //FIX ME - Find the strain id properly!!!
                                    addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tmp_states[some_ii].trim(), caller), getStrainTypeId(tmp_types[some_ii].trim(), caller), 666);
                                }
                                
                                if(tmp_states.length == max_tmp){
                                    for(int some_iii=min_tmp; some_iii < max_tmp; some_iii++){
                                        //FIX ME - Find the strain id properly!!!
                                        addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tmp_states[some_iii].trim(), caller), getStrainTypeId(tmp_types[min_tmp-1].trim(), caller), 666);
                                    }
                                }//case states are more than types
                                
                                if(tmp_types.length == max_tmp){
                                   for(int some_iiii=min_tmp; some_iiii < max_tmp; some_iiii++){
                                       //FIX ME - Find the strain id properly!!!
                                        addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tmp_states[min_tmp-1].trim(), caller), getStrainTypeId(tmp_types[some_iiii].trim(), caller), 666);
                                    } 
                                }//case types are more than states
                            }
                        }
                        //end of availability handling section
                        
                        //start working on the allele info of the tg mouse
                        String mgiid_num = tg_mouse[4].substring(tg_mouse[4].indexOf(":")+1).trim();
                        createStrainAlleleAdvanced(tg_mouse[5].trim(),tg_mouse[6].trim(), mgiid_num, caller);
                        
                        //start working on the chromosome
                        int chromo_id_tmp = 24;
                        if(tg_mouse.length > 8){
                            if(tg_mouse[8].compareTo("")!=0){
                                Collection chromos = getChromosomesByAbbreviation(tg_mouse[8], caller);
                                Iterator chromo_it = chromos.iterator();
                                while(chromo_it.hasNext()){
                                    ChromosomeDTO chromo_now = (ChromosomeDTO)chromo_it.next();
                                    chromo_id_tmp = chromo_now.getCid();
                                }
                            }
                        }
                        
                        //start working on the gene info of the tg mouse
                        
                        //tg_mouse.length < 12 => gene mgiid:yes | gene name:no | gene symbol:yes
                        if(tg_mouse.length > 10 && tg_mouse.length < 12){
                            //if mgi id is not bogus...
                            if(tg_mouse[9].compareTo("")!=0 && tg_mouse[9].compareTo("MGI:")!=0){
                                //if there's no gene with such an mgi id
                                if(getGeneByMgiid(tg_mouse[9].substring(tg_mouse[9].indexOf(":")+1).trim(), caller).isEmpty()){
                                    //create the new gene
                                    int new_gaid = createTransgene("transgene for tg mouse "+tmp_eid, "", tg_mouse[9].substring(tg_mouse[9].indexOf(":")+1), tg_mouse[10], "", "", "", chromo_id_tmp, "molecular_note", "molecular_note_url", caller);
                                    addGeneToModel(new_gaid, tmp_eid, caller);
                                }else{
                                    int gene_id_tmp = 0;
                                    Collection genes_tmp = getGeneByMgiid(tg_mouse[9].substring(tg_mouse[9].indexOf(":")+1).trim(), caller);
                                    Iterator genes_it = genes_tmp.iterator();
                                    while(genes_it.hasNext()){
                                        GeneDTO gene_tmp = (GeneDTO)genes_it.next();
                                        gene_id_tmp = gene_tmp.getGaid();
                                    }
                                    addGeneToModel(gene_id_tmp, tmp_eid, caller);
                                }
                            }else if(tg_mouse[10].compareTo("")!=0){
                                //case there's no mgiid for the gene
                                
                                //case there are no other genes with the existing symbol 
                                if(getGeneBySymbol(tg_mouse[10], caller).isEmpty()){
                                    int new_gaid = createTransgene("transgene for tg mouse "+tmp_eid, "", "", tg_mouse[10], "", "", "", chromo_id_tmp, "molecular_note", "molecular_note_url", caller);
                                    addGeneToModel(new_gaid, tmp_eid, caller);
                                }else{
                                    int new_gaid = 0;
                                    Collection genes_by_symbol = getGeneBySymbol(tg_mouse[10], caller);
                                    Iterator genes_by_symbol_it = genes_by_symbol.iterator();
                                    while(genes_by_symbol_it.hasNext()){
                                        GeneDTO gene_very_tmp = (GeneDTO)genes_by_symbol_it.next();
                                        new_gaid = gene_very_tmp.getGaid();
                                    }
                                    addGeneToModel(new_gaid, tmp_eid, caller);
                                }
                            }
                        
                        }//if(tg_mouse.length < 12)
                        
                    }//if(getStrainsFromMgiid(tg_mouse[0], caller).isEmpty())
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not parse uploaded file.\n"+e.getMessage());            
        }
    }
    
    public void doLineTypeThree(int repo, String [] tg_mouse, TgDbCaller caller) throws ApplicationException{
        try{
            logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: Processing "+tg_mouse[0]);
                    if(getStrainsFromMgiid(tg_mouse[0], caller).isEmpty()){
                        
                        //create the model+strain
                        int tmp_eid = createModelAutomatic(tg_mouse[0], tg_mouse[1], caller);
                        
                        //start working on the availability of the tg mouse
                        int av_gen_back_id = 57000;
                        if(tg_mouse[2].indexOf(",")==-1 && tg_mouse[3].indexOf(",")==-1){
                            //FIX ME - Find the strain id properly!!!
                            addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tg_mouse[3].trim(), caller), getStrainTypeId(tg_mouse[2].trim(), caller), 666);
                        }else{
                            String [] tmp_types = null;
                            String [] tmp_states = null;
                            
                            if(tg_mouse[2].indexOf(",")!=-1){
                                tmp_types = tg_mouse[2].split(",");
                            }else{
                                tmp_types = new String[1];
                                tmp_types[0] = tg_mouse[2].trim();
                            }
                            
                            if(tg_mouse[3].indexOf(",")!=-1){
                                tmp_states = tg_mouse[3].split(",");
                            }else{
                                tmp_states = new String[1];
                                tmp_states[0] = tg_mouse[3].trim();
                            }
                            
                            if(tmp_types.length == tmp_states.length){
                                for(int some_i=0; some_i < tmp_states.length; some_i++){
                                    //FIX ME - Find the strain id properly!!!
                                    addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tmp_states[some_i].trim(), caller), getStrainTypeId(tmp_types[some_i].trim(), caller), 666);
                                }
                                
                            }else{
                                int max_tmp = returnMax(tmp_states.length, tmp_types.length);
                                int min_tmp = returnMin(tmp_states.length, tmp_types.length);
                                
                                for(int some_ii=0; some_ii < min_tmp; some_ii++){
                                    //FIX ME - Find the strain id properly!!!
                                    addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tmp_states[some_ii].trim(), caller), getStrainTypeId(tmp_types[some_ii].trim(), caller), 666);
                                }
                                
                                if(tmp_states.length == max_tmp){
                                    for(int some_iii=min_tmp; some_iii < max_tmp; some_iii++){
                                        //FIX ME - Find the strain id properly!!!
                                        addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tmp_states[some_iii].trim(), caller), getStrainTypeId(tmp_types[min_tmp-1].trim(), caller), 666);
                                    }
                                }//case states are more than types
                                
                                if(tmp_types.length == max_tmp){
                                   for(int some_iiii=min_tmp; some_iiii < max_tmp; some_iiii++){
                                       //FIX ME - Find the strain id properly!!!
                                        addAvailabilityToModel(tmp_eid, repo, av_gen_back_id, getStrainStateId(tmp_states[min_tmp-1].trim(), caller), getStrainTypeId(tmp_types[some_iiii].trim(), caller), 666);
                                    } 
                                }//case types are more than states
                            }
                        }
                        //end of availability handling section
                        
                        //start working on the allele info of the tg mouse
                        String mgiid_num = tg_mouse[4].substring(tg_mouse[4].indexOf(":")+1).trim();
                        createStrainAlleleAdvanced(tg_mouse[5].trim(),tg_mouse[6].trim(), mgiid_num, caller);
                        
                        //start working on the chromosome
                        int chromo_id_tmp = 24;
                        if(tg_mouse.length > 8){
                            logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: [tg_mouse.length > 8] chromosome is: "+tg_mouse[8]+" model is: "+tg_mouse[0]);
                            if(tg_mouse[8].compareTo("")!=0 && tg_mouse[8]!=null){
                                logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: Working on chromosome: "+tg_mouse[8]+" for model: "+tg_mouse[0]);
                                Collection chromos = getChromosomesByAbbreviation(tg_mouse[8], caller);
                                Iterator chromo_it = chromos.iterator();
                                while(chromo_it.hasNext()){
                                    ChromosomeDTO chromo_now = (ChromosomeDTO)chromo_it.next();
                                    chromo_id_tmp = chromo_now.getCid();
                                }
                                logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: Finished working on chromosome "+tg_mouse[8]+" cid is "+chromo_id_tmp);
                            }
                        }
                        
                        //start working on the gene info of the tg mouse
                        
                        //tg_mouse.length < 13 => gene mgiid:yes | gene name:yes | gene symbol:yes
                        if(tg_mouse.length > 11 && tg_mouse.length < 13){
                            //if mgi id is not bogus...
                            if(tg_mouse[9].compareTo("")!=0 && tg_mouse[9].compareTo("MGI:")!=0){
                                logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: Model "+tg_mouse[0]+" is one step before getGeneByMgiid function with mgiid "+tg_mouse[9].substring(tg_mouse[9].indexOf(":")+1).trim());
                                Collection genes_tmp = getGeneByMgiid(tg_mouse[9].substring(tg_mouse[9].indexOf(":")+1).trim(), caller);
                                //if there's no gene with such an mgi id
                                //if(getGeneByMgiid(tg_mouse[9].substring(tg_mouse[9].indexOf(":")+1).trim(), caller).isEmpty()){
                                if(genes_tmp.isEmpty()){
                                    logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: Model "+tg_mouse[0]+" has unique mgiid.");
                                    //if the name is not blank, check if there's another gene with the same name...
                                    if(tg_mouse[11].compareTo("")!=0){
                                        int new_gaid = 0;
                                        Collection get_the_genes = getGeneByNameCaseSensitive(tg_mouse[11].trim(), caller);
                                        if(get_the_genes.isEmpty()){
                                            //create the new gene
                                            new_gaid = createTransgene(tg_mouse[11].trim(), "", tg_mouse[9].substring(tg_mouse[9].indexOf(":")+1), tg_mouse[10], "", "", "", chromo_id_tmp, "molecular_note", "molecular_note_url", caller);
                                            addGeneToModel(new_gaid, tmp_eid, caller);
                                            logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: Gene name was not blank+no other gene has such a name");
                                            logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: New gene[][][][] name: "+tg_mouse[11].trim());
                                            logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: New gene[][][][] mgiid: "+tg_mouse[9].substring(tg_mouse[9].indexOf(":")+1));
                                            logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: New gene[][][][] cid: "+chromo_id_tmp);
                                            GeneDTO test_gene = getGene(new_gaid, caller);
                                            logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: Gene has been stored and here are the data");
                                            logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: New gene[][][][] name: "+test_gene.getName());
                                            logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: New gene[][][][] mgiid: "+test_gene.getMgiid());
                                            logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: New gene[][][][] cid: "+test_gene.getCid());
                                            
                                        }else{
                                            Iterator get_the_genes_it = get_the_genes.iterator();
                                            while(get_the_genes_it.hasNext()){
                                                GeneDTO some_gene = (GeneDTO)get_the_genes_it.next();
                                                new_gaid = some_gene.getGaid();
                                            }
                                            addGeneToModel(new_gaid, tmp_eid, caller);
                                            logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: Gene name was not blank+but some other gene had the same name");
                                        }
                                    }
                                    
                                }else{//case there's a gene with such an mgi id...
                                    logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: Model "+tg_mouse[0]+" hasn't unique mgiid.");
                                    int gene_id_tmp = 0;
                                    Iterator genes_it = genes_tmp.iterator();
                                    while(genes_it.hasNext()){
                                        GeneDTO gene_tmp = (GeneDTO)genes_it.next();
                                        gene_id_tmp = gene_tmp.getGaid();
                                    }
                                    addGeneToModel(gene_id_tmp, tmp_eid, caller);
                                }
                            }else{//case there's no mgiid for the gene
                                logger.debug("---------------------------------------->ModelManagerBean#doLineTypeThree: Model "+tg_mouse[0]+" has no mgiid.");
                                if(tg_mouse[11].compareTo("")!=0){
                                    
                                    int new_gaid = 0;
                                    Collection genes_by_symbol = getGeneByNameCaseSensitive(tg_mouse[11], caller);
                                    
                                    if(genes_by_symbol.isEmpty()){
                                        new_gaid = createTransgene(tg_mouse[11], "", "", tg_mouse[10], "", "", "", chromo_id_tmp, "molecular_note", "molecular_note_url", caller);
                                        addGeneToModel(new_gaid, tmp_eid, caller);
                                    }else{
                                        Iterator genes_by_symbol_it = genes_by_symbol.iterator();
                                        
                                        while(genes_by_symbol_it.hasNext()){
                                            GeneDTO gene_very_tmp = (GeneDTO)genes_by_symbol_it.next();
                                            new_gaid = gene_very_tmp.getGaid();
                                        }
                                        
                                        addGeneToModel(new_gaid, tmp_eid, caller);
                                    }
                                }
                            }
                        
                        }//if(tg_mouse.length < 13)
                        
                    }//if(getStrainsFromMgiid(tg_mouse[0], caller).isEmpty())
            tg_mouse = null;
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not parse uploaded file.\n"+e.getMessage());            
        }
    }
    //</editor-fold>
    
    
    //web services methods
    //<editor-fold defaultstate="collapsed">
    public java.lang.String getProjectName(){
        String prjname = "TgDb";
        return prjname;
    }
    
    public String [] getTgDbMice() throws ApplicationException {
        try {
            Collection models = modelHome.findByWebServiceRequest();
            String [] modelsws = new String [models.size()];
            Iterator i = models.iterator();
            ExpModelRemote tmp = null;
            int index = 0;
            while (i.hasNext()) {
                tmp = (ExpModelRemote)i.next();
                modelsws[index] = "<tr><td>"+tmp.getEid()+"</td><td>"+tmp.getAlias()+"</td></tr>";
                index++;
            }
            return modelsws;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get models.");
        }
    }

    //FIXME!!! - Needs a big big fix
    public TgDbModelDTO [] getTgDbMiceDTO() throws ApplicationException {
        try {
            Collection models = modelHome.findByWebServiceRequest();
            TgDbModelDTO [] modelsws = new TgDbModelDTO [models.size()];
            Iterator i = models.iterator();
            
            TgDbModelDTO tmp = null;
            int index = 0;
            while (i.hasNext()) {
                ExpModelRemote model = ((ExpModelRemote)i.next());
                tmp = new TgDbModelDTO();
                tmp.setEid(model.getEid());
                tmp.setAcc(model.getIdentity());
                tmp.setLine(model.getAlias());
//                tmp.setDesignation(model.getStrain().getDesignation().replaceAll("<","&lt;").replaceAll(">","&gt;").replaceAll("&lt;","<sup>").replaceAll("&gt;","</sup>"));
                tmp.setMutations(model.getMutationTypesForModel());
                modelsws[index] = tmp;
                index++;
            }
            return modelsws;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("TgDb-WS:failed to get models.");
        }
    }

    //FIXME!!! - Needs a big big fix
    public TgDbModelDTO [] getTgDbMiceDTOByKey(String key) throws ApplicationException {
        try {
            Collection models = modelHome.findByWebServiceKeywordRequest(key);
            TgDbModelDTO [] modelsws = new TgDbModelDTO [models.size()];
            Iterator i = models.iterator();
            
            TgDbModelDTO tmp = null;
            int index = 0;
            while (i.hasNext()) {
                ExpModelRemote model = ((ExpModelRemote)i.next());
                tmp = new TgDbModelDTO();
                tmp.setEid(model.getEid());
                tmp.setAcc(model.getIdentity());
                tmp.setLine(model.getAlias());
//                tmp.setDesignation(model.getStrain().getDesignation().replaceAll("<","&lt;").replaceAll(">","&gt;").replaceAll("&lt;","<sup>").replaceAll("&gt;","</sup>"));
                tmp.setMutations(model.getMutationTypesForModel());
                modelsws[index] = tmp;
                index++;
            }
            return modelsws;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("TgDb-WS:failed to get models.");
        }
    }
    
    public TgDbGeneDTO [] getTgDbGenesByModel(int eid) throws ApplicationException {
        try {
            Collection genes = geneHome.findByModel(eid);
            TgDbGeneDTO [] genesws = new TgDbGeneDTO [genes.size()];
            Iterator i = genes.iterator();
            
            TgDbGeneDTO tmp = null;
            int index = 0;
            while (i.hasNext()) {
                GeneRemote gene = ((GeneRemote)i.next());
                tmp = new TgDbGeneDTO();
                tmp.setGid(gene.getGaid());
                tmp.setName(gene.getName());
                tmp.setSymbol(gene.getGenesymbol());
                tmp.setChromosome(gene.getChromosome().getName());
                tmp.setMgiid(gene.getMgiid().trim());
                genesws[index] = tmp;
                index++;
            }
            return genesws;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("TgDb-WS:failed to get genes.");
        }
    }
    
    public TgDbAvailabilityDTO [] getTgDbAvailabilityByModel(int eid) throws ApplicationException {
        try {
            Collection avs = availabilityHome.findByModel(eid);
            TgDbAvailabilityDTO [] avsws = new TgDbAvailabilityDTO [avs.size()];
            Iterator i = avs.iterator();
            
            TgDbAvailabilityDTO tmp = null;
            int index = 0;
            while (i.hasNext()) {
                AvailabilityRemote av = ((AvailabilityRemote)i.next());
                tmp = new TgDbAvailabilityDTO();
                tmp.setRepository(av.getRepositoryName());
                //tmp.setRepositorylink(av.getRepositoryURL());
                tmp.setBackground(av.getAvailableGeneticBackgroundName());
                tmp.setState(av.getStateName());
                tmp.setType(av.getTypeName());
                avsws[index] = tmp;
                index++;
            }
            return avsws;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("TgDb-WS:failed to get availability info.");
        }
    }
    
    public TgDbBackgroundDTO [] getTgDbBackgroundByModel(int eid) throws ApplicationException {
        try {
            Collection backs = genbackHome.findByGeneticBackgroundModel(eid);
            TgDbBackgroundDTO [] backsws = new TgDbBackgroundDTO [backs.size()];
            Iterator i = backs.iterator();
            
            TgDbBackgroundDTO tmp = null;
            int index = 0;
            while (i.hasNext()) {
                GeneticBackgroundDTO back = new GeneticBackgroundDTO((GeneticBackgroundRemote)i.next());
                tmp = new TgDbBackgroundDTO();
                tmp.setDna(back.getDna_origin_name());
                tmp.setTarget(back.getTargeted_back_name());
                tmp.setHost(back.getHost_back_name());
                tmp.setBackcross(back.getBackcrossing_strain_name());
                tmp.setBackcrosses(back.getBackcrosses());
                backsws[index] = tmp;
                index++;
            }
            return backsws;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("TgDb-WS:failed to get genetic background info.");
        }
    }
    
    //</editor-fold>

}


