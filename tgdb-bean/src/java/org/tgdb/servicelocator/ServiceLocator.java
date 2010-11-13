package org.tgdb.servicelocator;

import org.tgdb.adminmanager.AdminManagerRemoteHome;
import org.tgdb.resource.file.FileRemoteHome;
import org.tgdb.export.ExportManagerRemoteHome;
import org.tgdb.export.filter.GQLFilterRemoteHome;
import org.tgdb.model.expmodel.ExpModelRemoteHome;
import org.tgdb.species.gene.GeneRemoteHome;
import org.tgdb.model.modelmanager.ModelManagerRemoteHome;
import org.tgdb.model.reference.ReferenceRemoteHome;
import org.tgdb.model.researchapplication.ResearchApplicationRemoteHome;
import org.tgdb.model.strain.allele.StrainAlleleRemoteHome;
import org.tgdb.model.strain.mutationtype.MutationTypeRemoteHome;
import org.tgdb.model.strain.state.StrainStateRemoteHome;
import org.tgdb.model.strain.strain.StrainRemoteHome;
import org.tgdb.model.strain.type.StrainTypeRemoteHome;
import org.tgdb.project.privilege.PrivilegeRemoteHome;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.project.projectmanager.ProjectManagerRemoteHome;
import org.tgdb.project.role.RoleRemoteHome;
import org.tgdb.project.securityprinciple.SecurityPrincipleRemoteHome;
import org.tgdb.project.user.UserRemoteHome;
import org.tgdb.resource.link.LinkRemoteHome;
import org.tgdb.resource.resource.ResourceRemoteHome;
import org.tgdb.resource.resourcecategory.ResourceCategoryRemoteHome;
import org.tgdb.resource.resourcemanager.ResourceManagerRemoteHome;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteHome;
import org.tgdb.samplingunit.samplingunitmanager.SamplingUnitManagerRemoteHome;
import org.tgdb.simplelog.SimpleLogRemoteHome;
import org.tgdb.species.chromosome.ChromosomeRemoteHome;
import org.tgdb.species.species.SpeciesRemoteHome;
import java.lang.reflect.Method;
import java.util.HashMap;
import javax.ejb.EJBHome;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import org.tgdb.model.geneticbackground.GeneticBackgroundRemoteHome;
import org.tgdb.model.geneticbackground.GeneticBackgroundValuesRemoteHome;
import org.tgdb.model.availability.AvailabilityRemoteHome;
import org.tgdb.model.availablegeneticbackgrounds.AvailableGeneticBackgroundRemoteHome;
import org.tgdb.model.repositories.RepositoriesRemoteHome;
import org.tgdb.expression.expressionmodel.ExpressionModelRemoteHome;

import org.tgdb.genome.integrationcopy.IntegrationCopyRemoteHome;


public class ServiceLocator {
    // singleton's private instance
    private static ServiceLocator me;
    private static int totalTime;
    
    static {
        me = new ServiceLocator();
        totalTime=0;
        cache = new HashMap();
    }
    
    private ServiceLocator() {}
    
    // returns the Service Locator instance
    static public ServiceLocator getInstance() {
        return me;
    }
    
    
    // Services Constants Inner Class - service objects
    public class Services {
        final public static int PROJECT  = 0;
        final public static int SAMPLINGUNIT = 1;
        final public static int USER = 2;
        final public static int INDIVIDUAL = 3;
        final public static int GENOTYPE = 4;
        final public static int ALLELE = 5;
        final public static int MARKER = 6;
        final public static int MARKERSET = 7;
        final public static int UMARKERSET = 8;
        final public static int UMARKER = 9;
        final public static int UALLELE = 10;
        final public static int CHROMOSOME = 11;
        final public static int SPECIES = 12;
        final public static int FILE = 13;
        final public static int GROUPING = 14;
        final public static int PHENOTYPE = 15;
        final public static int PHENOTYPEMANAGER = 16;
        final public static int SAMPLINGUNITMANAGER = 17;
        final public static int GENOTYPEMANAGER = 18;
        final public static int PROJECTMANAGER = 19;
        
        final public static int VARIABLE= 22;
        final public static int ROLE= 23;
        final public static int SECURITYPRINCIPLE = 24;
        final public static int PRIVILEGE = 25;
        final public static int VARIABLESET = 26;
        final public static int UVARIABLE = 27;
        final public static int UVARIABLESET = 28;

        
        
        final public static int GROUP = 30;
        final public static int SAMPLE = 31;
        final public static int EXPORTMANAGER = 32;
        final public static int GQLFILTER = 33;
        final public static int LINK = 34;
        final public static int RESOURCEMANAGER = 35;
        final public static int EXPMODEL = 36;
        final public static int GENETICMODIFICATION = 37;
        final public static int RESEARCHAPPLICATION = 38;
        final public static int MODELMANAGER = 39;
        final public static int GENEONTOLOGY = 40;
        final public static int GENE = 43;
        final public static int REFERENCE = 44;
        final public static int SIMPLELOG = 45;
        final public static int ADMINMANAGER = 46;
        final public static int RESOURCE = 47;
        final public static int RESOURCECATEGORY = 48;
        final public static int PROCESS = 50;
        final public static int PROTEIN = 53;
        final public static int PROTEINFAMILY = 54;
        final public static int PROTEINCOMPLEX = 55;
        final public static int STRAIN = 56;
        final public static int STRAIN_TYPE = 57;
        final public static int STRAIN_STATE = 58;
        final public static int STRAIN_ALLELE = 59;
        final public static int MUTATION_TYPE = 60;
        final public static int GENETIC_BACKGROUND = 61;
        final public static int GENETIC_BACKGROUND_VALUES = 62;
        final public static int REPOSITORIES = 63;
        final public static int AVAILABLE_GENETIC_BACKGROUNDS = 64;
        final public static int AVAILABILITY = 65;
        
        final public static int EXPRESSION_MODEL = 71;
        final public static int INTEGRATION_COPY = 72;
        
    }
    
    // Project EJB related constants
    final static Class  PROJECT_CLASS = ProjectRemoteHome.class;
    final static String PROJECT_NAME  = "ejb/CreZOOProjectBean";
    
    final static Class  SU_CLASS = SamplingUnitRemoteHome.class;
    final static String SU_NAME  = "ejb/CreZOOSamplingUnitBean";
    
    final static Class  SUMANAGER_CLASS = SamplingUnitManagerRemoteHome.class;
    final static String SUMANAGER_NAME  = "ejb/CreZOOSamplingUnitManagerBean";
    
    final static Class USER_CLASS = UserRemoteHome.class;
    final static String USER_NAME = "ejb/CreZOOUserBean";
    
    final static Class CHROMOSOME_CLASS = ChromosomeRemoteHome.class;
    final static String CHROMOSOME_NAME = "ejb/CreZOOChromosomeBean";
    
    final static Class SPECIES_CLASS = SpeciesRemoteHome.class;
    final static String SPECIES_NAME = "ejb/CreZOOSpeciesBean";
            
    final static Class FILE_CLASS = FileRemoteHome.class;
    final static String FILE_NAME = "ejb/CreZOOFileBean";
    
    final static Class PROJECTMANAGER_CLASS = ProjectManagerRemoteHome.class;
    final static String PROJECTMANAGER_NAME = "ejb/CreZOOProjectManagerBean";
    
    final static Class ROLE_CLASS = RoleRemoteHome.class;
    final static String ROLE_NAME = "ejb/CreZOORoleBean";
    
    final static Class SECURITYPRINCIPLE_CLASS = SecurityPrincipleRemoteHome.class;
    final static String SECURITYPRINCIPLE_NAME = "ejb/CreZOOSecurityPrincipleBean";
    
    final static Class PRIVILEGE_CLASS = PrivilegeRemoteHome.class;
    final static String PRIVILEGE_NAME = "ejb/CreZOOPrivilegeBean";
    
    final static Class EXPORTMANAGER_CLASS = ExportManagerRemoteHome.class;
    final static String EXPORTMANAGER_NAME = "ejb/CreZOOExportManagerBean";  
    
    final static Class GQLFILTER_CLASS = GQLFilterRemoteHome.class;
    final static String GQLFILTER_NAME = "ejb/CreZOOGQLFilterBean";      
    
    final static Class LINK_CLASS = LinkRemoteHome.class;
    final static String LINK_NAME = "ejb/CreZOOLinkBean";      
    
    final static Class RESOURCEMANAGER_CLASS = ResourceManagerRemoteHome.class;
    final static String RESOURCEMANAGER_NAME = "ejb/CreZOOResourceManagerBean";       

    final static Class EXPMODEL_CLASS = ExpModelRemoteHome.class;
    final static String EXPMODEL_NAME = "ejb/CreZOOExpModelBean";     
    
    final static Class RESEARCHAPPLICATION_CLASS = ResearchApplicationRemoteHome.class;
    final static String RESEARCHAPPLICATION_NAME = "ejb/CreZOOResearchApplicationBean";
    
    final static Class MODELMANAGER_CLASS = ModelManagerRemoteHome.class;
    final static String MODELMANAGER_NAME = "ejb/CreZOOModelManagerBean";    
    
    final static Class GENE_CLASS = GeneRemoteHome.class;
    final static String GENE_NAME = "ejb/CreZOOGeneBean";
    
    final static Class REFERENCE_CLASS = ReferenceRemoteHome.class;
    final static String REFERENCE_NAME = "ejb/CreZOOReferenceBean";
    
    final static Class SIMPLELOG_CLASS = SimpleLogRemoteHome.class;
    final static String SIMPLELOG_NAME = "ejb/CreZOOSimpleLogBean";
    
    final static Class ADMINMANAGER_CLASS = AdminManagerRemoteHome.class;
    final static String ADMINMANAGER_NAME = "ejb/CreZOOAdminManagerBean";
    
    final static Class RESOURCE_CLASS = ResourceRemoteHome.class;
    final static String RESOURCE_NAME = "ejb/CreZOOResourceBean";
    
    
    final static Class RESOURCECATEGORY_CLASS = ResourceCategoryRemoteHome.class;
    final static String RESOURCECATEGORY_NAME = "ejb/CreZOOResourceCategoryBean";   
    
    final static Class STRAIN_CLASS = StrainRemoteHome.class;
    final static String STRAIN_NAME = "ejb/CreZOOStrainBean"; 
    
    final static Class STRAIN_TYPE_CLASS = StrainTypeRemoteHome.class;
    final static String STRAIN_TYPE_NAME = "ejb/CreZOOStrainTypeBean"; 
    
    final static Class STRAIN_STATE_CLASS = StrainStateRemoteHome.class;
    final static String STRAIN_STATE_NAME = "ejb/CreZOOStrainStateBean"; 
    
    final static Class STRAIN_ALLELE_CLASS = StrainAlleleRemoteHome.class;
    final static String STRAIN_ALLELE_NAME = "ejb/CreZOOStrainAlleleBean"; 
    
    final static Class MUTATION_TYPE_CLASS = MutationTypeRemoteHome.class;
    final static String MUTATION_TYPE_NAME = "ejb/CreZOOMutationTypeBean"; 
    
    final static Class GENETIC_BACKGROUND_CLASS = GeneticBackgroundRemoteHome.class;
    final static String GENETIC_BACKGROUND_NAME = "ejb/CreZOOGeneticBackgroundBean";
    
    final static Class GENETIC_BACKGROUND_VALUES_CLASS = GeneticBackgroundValuesRemoteHome.class;
    final static String GENETIC_BACKGROUND_VALUES_NAME = "ejb/CreZOOGeneticBackgroundValuesBean";
    
    final static Class REPOSITORIES_CLASS = RepositoriesRemoteHome.class;
    final static String REPOSITORIES_NAME = "ejb/CreZOORepositoriesBean";
    
    final static Class AVAILABLE_GENETIC_BACKGROUNDS_CLASS = AvailableGeneticBackgroundRemoteHome.class;
    final static String AVAILABLE_GENETIC_BACKGROUNDS_NAME = "ejb/CreZOOAvailableGeneticBackgroundBean";
   
    final static Class AVAILABILITY_CLASS = AvailabilityRemoteHome.class;
    final static String AVAILABILITY_NAME = "ejb/CreZOOAvailabilityBean";
    
    final static Class EXPRESSION_MODEL_CLASS = ExpressionModelRemoteHome.class;
    final static String EXPRESSION_MODEL_NAME = "ejb/CreZOOExpressionModelBean";
    
    final static Class INTEGRATION_COPY_CLASS = IntegrationCopyRemoteHome.class;
    final static String INTEGRATION_COPY_NAME = "ejb/CreZOOIntegrationCopyBean";

    private static EJBHome cacheProject;
    
    // Returns the Class for the required service
    static private Class getServiceClass(int service){
        switch( service ) {
            case Services.PROJECT:
                return PROJECT_CLASS;
            case Services.SAMPLINGUNIT:
                return SU_CLASS;
            case Services.USER:
                return USER_CLASS;
            case Services.CHROMOSOME:
                return CHROMOSOME_CLASS;
            case Services.SPECIES:
                return SPECIES_CLASS;
            case Services.FILE:
                return FILE_CLASS;
            case Services.SAMPLINGUNITMANAGER:
                return SUMANAGER_CLASS;
            case Services.PROJECTMANAGER:
                return PROJECTMANAGER_CLASS;                     
            case Services.ROLE:
                return ROLE_CLASS;
            case Services.SECURITYPRINCIPLE:
                return SECURITYPRINCIPLE_CLASS;
            case Services.PRIVILEGE:
                return PRIVILEGE_CLASS;
            case Services.EXPORTMANAGER:
                return EXPORTMANAGER_CLASS;   
            case Services.GQLFILTER:
                return GQLFILTER_CLASS;       
            case Services.LINK:
                return LINK_CLASS;    
            case Services.RESOURCEMANAGER:
                return RESOURCEMANAGER_CLASS;
            case Services.EXPMODEL:
                return EXPMODEL_CLASS;    
            case Services.RESEARCHAPPLICATION:
                return RESEARCHAPPLICATION_CLASS;
            case Services.MODELMANAGER:
                return MODELMANAGER_CLASS;     
            case Services.GENE:
                return GENE_CLASS;
            case Services.REFERENCE:
                return REFERENCE_CLASS;    
            case Services.SIMPLELOG:
                return SIMPLELOG_CLASS;
            case Services.ADMINMANAGER:
                return ADMINMANAGER_CLASS;
            case Services.RESOURCE:
                return RESOURCE_CLASS;
            case Services.RESOURCECATEGORY:
                return RESOURCECATEGORY_CLASS;     
            case Services.STRAIN:
                return STRAIN_CLASS;
            case Services.STRAIN_TYPE:
                return STRAIN_TYPE_CLASS;    
            case Services.STRAIN_STATE:
                return STRAIN_STATE_CLASS; 
            case Services.STRAIN_ALLELE:
                return STRAIN_ALLELE_CLASS;
            case Services.MUTATION_TYPE:
                return MUTATION_TYPE_CLASS;
            case Services.GENETIC_BACKGROUND:
                return GENETIC_BACKGROUND_CLASS;
            case Services.GENETIC_BACKGROUND_VALUES:
                return GENETIC_BACKGROUND_VALUES_CLASS;
            case Services.REPOSITORIES:
                return REPOSITORIES_CLASS;
            case Services.AVAILABLE_GENETIC_BACKGROUNDS:
                return AVAILABLE_GENETIC_BACKGROUNDS_CLASS;
            case Services.AVAILABILITY:
                return AVAILABILITY_CLASS;
            case Services.EXPRESSION_MODEL:
                return EXPRESSION_MODEL_CLASS;
            case Services.INTEGRATION_COPY:
                return INTEGRATION_COPY_CLASS;
        }
        return null;
    }
    
    // returns the JNDI name for the required service
    static private String getServiceName(int service){
        switch( service ) {
            case Services.PROJECT:
                return PROJECT_NAME;
            case Services.SAMPLINGUNIT:
                return SU_NAME;
            case Services.USER:
                return USER_NAME;
            case Services.CHROMOSOME:
                return CHROMOSOME_NAME;
            case Services.SPECIES:
                return SPECIES_NAME;
            case Services.FILE:
                return FILE_NAME;
            case Services.SAMPLINGUNITMANAGER:
                return SUMANAGER_NAME;
            case Services.PROJECTMANAGER:
                return PROJECTMANAGER_NAME;
            case Services.ROLE:
                return ROLE_NAME;
            case Services.SECURITYPRINCIPLE:
                return SECURITYPRINCIPLE_NAME;
            case Services.PRIVILEGE:
                return PRIVILEGE_NAME;
            case Services.EXPORTMANAGER:
                return EXPORTMANAGER_NAME;     
            case Services.GQLFILTER:
                return GQLFILTER_NAME;   
            case Services.LINK:
                return LINK_NAME;   
            case Services.RESOURCEMANAGER:
                return RESOURCEMANAGER_NAME;   
           case Services.EXPMODEL:
                return EXPMODEL_NAME;
            case Services.RESEARCHAPPLICATION:
                return RESEARCHAPPLICATION_NAME;
            case Services.MODELMANAGER:
                return MODELMANAGER_NAME;  
            case Services.GENE:
                return GENE_NAME;
            case Services.REFERENCE:
                return REFERENCE_NAME;
            case Services.SIMPLELOG:
                return SIMPLELOG_NAME;
            case Services.ADMINMANAGER:
                return ADMINMANAGER_NAME;
            case Services.RESOURCE:
                return RESOURCE_NAME;
            case Services.RESOURCECATEGORY:
                return RESOURCECATEGORY_NAME; 
            case Services.STRAIN:
                return STRAIN_NAME;
            case Services.STRAIN_TYPE:
                return STRAIN_TYPE_NAME;    
            case Services.STRAIN_STATE:
                return STRAIN_STATE_NAME;    
            case Services.STRAIN_ALLELE:
                return STRAIN_ALLELE_NAME;
            case Services.MUTATION_TYPE:
                return MUTATION_TYPE_NAME;
            case Services.GENETIC_BACKGROUND:
                return GENETIC_BACKGROUND_NAME;
            case Services.GENETIC_BACKGROUND_VALUES:
                return GENETIC_BACKGROUND_VALUES_NAME;
            case Services.REPOSITORIES:
                return REPOSITORIES_NAME;
            case Services.AVAILABLE_GENETIC_BACKGROUNDS:
                return AVAILABLE_GENETIC_BACKGROUNDS_NAME;
            case Services.AVAILABILITY:
                return AVAILABILITY_NAME;
            case Services.EXPRESSION_MODEL:
                return EXPRESSION_MODEL_NAME;
            case Services.INTEGRATION_COPY:
                return INTEGRATION_COPY_NAME;
        }
        return null;
    }
    
    private static HashMap cache;
    
    private EJBHome getFromCache(int service) {
        EJBHome home = null;
        return (EJBHome)cache.get(new Integer(service));
    }
    
    private void addToCache(int service, EJBHome home)
    {
        if (!cache.containsKey(new Integer(service)))
            cache.put(new Integer(service), home);        
    }
    
    
  /**
   * gets the EJBHome for the given service using the
   * JNDI name and the Class for the EJBHome
   */
    public EJBHome getHome( int s ) throws ServiceLocatorException {
        //long t1 = System.currentTimeMillis();
        EJBHome home = null;
        try {
            Context initial  = new InitialContext();
            
            home = getFromCache(s);
            if (home==null)
            {
                // Look up using the service name from
                // defined constant
                Object objref =
                        initial.lookup(getServiceName(s));

                // Narrow using the EJBHome Class from
                // defined constant
                Object obj = PortableRemoteObject.narrow(
                        objref, getServiceClass(s));
                home = (EJBHome)obj;
                
                // Add this home to cache
                addToCache(s, home);
            }
        } catch( NamingException ex ) {
            ex.printStackTrace();
            throw new ServiceLocatorException("Naming error");
        } catch( Exception ex ) {
            ex.printStackTrace();
            throw new ServiceLocatorException("getHome failed");
        }

        //long t2 = System.currentTimeMillis();
        //totalTime+=t2-t1;
        return home;
    }
    
    public Object getManager(int s) throws ServiceLocatorException {
        Object o = null;
        try
        {
            EJBHome tmp = getHome(s);
            Method m = tmp.getClass().getMethod("create", null);
            o = m.invoke(tmp, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ServiceLocatorException(e.getMessage());
        }
        return o;
    }
}

