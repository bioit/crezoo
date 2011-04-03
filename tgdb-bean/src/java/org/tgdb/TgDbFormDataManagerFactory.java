package org.tgdb;

import org.tgdb.form.AbstractFormDataManagerFactory;
import org.tgdb.form.FormDataException;
import org.tgdb.form.FormDataManager;
import org.tgdb.frame.WebFormDataManager;
import java.util.ArrayList;

public class TgDbFormDataManagerFactory extends AbstractFormDataManagerFactory {
    /**
     * Constant field for the typical genotype default values
     */
    public final static int GENOTYPE = 0;
    public final static int MARKER_FILTER = 1;
    public final static int MARKER = 2;
    public final static int MARKER_SET_FILTER = 3;
    public final static int MARKER_SET_MEMBERSHIP_FILTER = 4;
    public final static int MARKER_SET_POSITION_FILTER = 5;
    public final static int U_MARKER_FILTER = 6;
    public final static int U_MARKER_MAPPING = 7;
    public final static int U_MARKER = 8;
    public final static int U_MARKER_SET_FILTER = 9;
    public final static int U_MARKER_SET_MEMBERSHIP_FILTER = 10;
    public final static int U_MARKER_SET_POSITION_FILTER = 11;
    public final static int INDIVIDUALS_FILTER = 12;
    public final static int FAMILY_GROUPING = 13;
    public final static int GROUPING_FILTER = 14;
    public final static int GROUP = 15; 
    public final static int GROUP_MEMBERSHIP_FILTER = 16; 
    public final static int SAMPLE = 17; 
    public final static int SAMPLE_FILTER = 18; 
    public final static int PHENOTYPE = 19;
    public final static int VARIABLE_FILTER = 20;
    public final static int VARIABLE_SET_FILTER = 21;
    public final static int PHENOTYPE_FILTER = 22;
    public final static int U_VARIABLE = 23;
    public final static int VARIABLE_SET_MEMBERSHIP_FILTER = 24;
    public final static int U_VARIABLE_SET_MEMBERSHIP_FILTER = 25;
    public final static int U_VARIABLE_SET_FILTER = 26;
    public final static int SAMPLINGUNIT_DETAILS = 27;
    public final static int EXPMODEL = 28;
    public final static int EXPMODELS = 29;
    public final static int PHENOTREE = 30;
    public final static int FAST_SEARCH = 31;
    public final static int ALLELE = 32;
    public final static int STRAIN = 33;
    public final static int MUTATION_TYPE = 34;
    public final static int EXPRESSION_MODEL = 35;
    public final static int STRAIN_TYPE = 36;
    public final static int STRAIN_STATE = 37;
    public final static int GENE = 38;
    public final static int STRAIN_ALLELES = 39;
    public final static int GENES = 40;
    public final static int STRAINS = 41;
    
    private TgDbCaller caller;
    
    /** Creates a new instance of TgDbFormDataManagerFactory */
    public TgDbFormDataManagerFactory(TgDbCaller caller) {
        super();
        this.caller = caller;
        initNames();
    }
    
    
    // inits the names of all managers
    private static void initNames() {
        names = new ArrayList();
        names.add("genotype");
        names.add("marker_filter");
        names.add("marker");
        names.add("marker_set_filter");
        names.add("marker_set_membership_filter");
        names.add("marker_set_position_filter");
        names.add("u_marker_filter");
        names.add("u_marker_mapping");
        names.add("u_marker");
        names.add("u_marker_set_filter");
        names.add("u_marker_set_membership_filter");
        names.add("u_marker_set_position_filter");
        names.add("individuals_filter");
        names.add("family_grouping");
        names.add("grouping_filter");
        names.add("group");
        names.add("group_membership_filter");
        names.add("sample");
        names.add("sample_filter");
        names.add("phenotype");
        names.add("variable_filter");
        names.add("variable_set_filter");
        names.add("phenotype_filter");
        names.add("u_variable");
        names.add("variable_set_membership_filter");
        names.add("u_variable_set_membership_filter");
        names.add("u_variable_set_filter");
        names.add("samplingunit_details");
        names.add("expmodel");
        names.add("expmodels");
        names.add("phenotree");
        names.add("fast_search");
        names.add("allele");
        names.add("strain");
        names.add("mutation_type");
        names.add("expression_model");
        names.add("strain_type");
        names.add("strain_state");
        names.add("gene");
        names.add("strain_alleles");
        names.add("genes");
        names.add("strains");
    }    
    
    /**
     * Creates a new FormDataManager instance with suitable default values.
     * @param name The type of WebFormDataManager to create
     * @throws org.tgdb.frame.ActionException If the the name could not be recognized
     * @return A WebFormDataManager object with default values as specified by the type of WebFormDataManager to build
     */
    public FormDataManager createInstance(int name, int type) throws FormDataException {
        // Name that will be used when storing session variables,
        // eg. tmp.actionName.varName
        // Mainly made this way for easy clean-up if desired later on...
        
        if(name > names.size())
            throw new FormDataException("FormDataManager name does not exits");
        
        FormDataManager formDataManager = null;
        
        if(type == WEB_FORM)
            formDataManager = new WebFormDataManager(false);
        else
            formDataManager = new FormDataManager(false);
        
        // Default values for the genotype 'create' view
        if(name == GENOTYPE){
            formDataManager.putDefault("mid", "");   
            formDataManager.putDefault("iid", "");
            formDataManager.putDefault("cid", "");
            formDataManager.putDefault("raw1", "");
            formDataManager.putDefault("raw2", "");
            formDataManager.putDefault("reference", "");
            formDataManager.putDefault("comm", "");
            formDataManager.putDefault("aid1", "");
            formDataManager.putDefault("aid2", "");
            formDataManager.putDefault("level", ""); 
        }
        
        // Default values for the marker view filter
        else if(name == MARKER_FILTER) {
            formDataManager.putDefault("suid", ""+caller.getSuid());   
            formDataManager.putDefault("cname", "*");
            formDataManager.putDefault("name", "");            
        }
        
        // Default values for the marker create/edit filter
        else if(name == MARKER) {
            formDataManager.putDefault("suid", ""+caller.getSuid());   
            formDataManager.putDefault("mid", "");   
            formDataManager.putDefault("p1", "");
            formDataManager.putDefault("p2", "");
            formDataManager.putDefault("name", "");
            formDataManager.putDefault("comm", "");
            formDataManager.putDefault("position", "");
            formDataManager.putDefault("alias", "");
            formDataManager.putDefault("cname", "*");        
        }   
        
        // Default values for the marker set view filter
        else if(name == MARKER_SET_FILTER) {
            formDataManager.putDefault("v.suid", ""+caller.getSuid());   
            formDataManager.putDefault("v.name", "");
            formDataManager.putDefault("v.comm", "");
            formDataManager.putDefault("member", "");          
        }     
        
        // Default values for the marker set membership view filter
        else if(name == MARKER_SET_MEMBERSHIP_FILTER) {
            formDataManager.putDefault("suid", ""+caller.getSuid());   
            formDataManager.putDefault("cid", "");
            formDataManager.putDefault("msid", "");          
        }      
        
        // Default values for the marker set position view filter
        else if(name == MARKER_SET_POSITION_FILTER) {
            formDataManager.putDefault("suid", ""+caller.getSuid());   
            formDataManager.putDefault("msid", "");          
        }       
        
        // Default values for the unified marker view filter
        else if(name == U_MARKER_FILTER) {
            formDataManager.putDefault("sid", ""+caller.getSid());   
            formDataManager.putDefault("cname", "*");
            formDataManager.putDefault("pid", ""+caller.getPid());       
        }       
        
        // Default values for the unified marker mapping
        else if(name == U_MARKER_MAPPING) {
            formDataManager.putDefault("umid", "");         
            formDataManager.putDefault("mid", ""); 
            formDataManager.putDefault("cid", ""); 
        }  
        
        // Default values for the unified marker create/edit
        else if(name == U_MARKER) {
            formDataManager.putDefault("name", "");         
            formDataManager.putDefault("comm", ""); 
            formDataManager.putDefault("cid", ""); 
            formDataManager.putDefault("position", ""); 
            formDataManager.putDefault("alias", ""); 
            formDataManager.putDefault("sid", ""+caller.getSid()); 
        }   
        
        // Default values for the unified marker set view filter
        else if(name == U_MARKER_SET_FILTER) {
            formDataManager.putDefault("v.sid", ""+caller.getSid()); 
            formDataManager.putDefault("v.name", ""); 
            formDataManager.putDefault("v.comm", ""); 
            formDataManager.putDefault("v.pid", ""+caller.getPid()); 
            formDataManager.putDefault("member", ""); 
        }         
        
        // Default values for the unified marker set membership view filter
        else if(name == U_MARKER_SET_MEMBERSHIP_FILTER) {
            formDataManager.putDefault("sid", ""+caller.getSid()); 
            formDataManager.putDefault("suid", ""+caller.getSuid()); 
            formDataManager.putDefault("umsid", ""); 
        } 
        
        // Default values for the unified marker set position view filter
        else if(name == U_MARKER_SET_POSITION_FILTER) {
            formDataManager.putDefault("sid", ""+caller.getSid());   
            formDataManager.putDefault("umsid", "");          
            formDataManager.putDefault("umid", "");          
        }        
        
        // Default values for the individuals view filter
        else if(name == INDIVIDUALS_FILTER) {
            formDataManager.putDefault("suid", ""+caller.getSuid());
            formDataManager.putDefault("sex", "*");
            formDataManager.putDefault("father", "");
            formDataManager.putDefault("mother", "");
            formDataManager.putDefault("alias", "");
            formDataManager.putDefault("identity", "");
            formDataManager.putDefault("bdatefrom", "");
            formDataManager.putDefault("bdateto", "");
            formDataManager.putDefault("status", "E");
            formDataManager.putDefault("gid", "*");
            formDataManager.putDefault("gsid", "*");                  
            formDataManager.putDefault("querytype", "simple");   
        }    
        
        // Default values for the family grouping
        else if(name == FAMILY_GROUPING) {
            formDataManager.putDefault("suid", ""+caller.getSuid());   
            formDataManager.putDefault("mother", "");          
            formDataManager.putDefault("father", "");       
            formDataManager.putDefault("comm", "Auto created from family information. ");
            formDataManager.putDefault("name", "");       
        }    
        
        // Default values for the grouping filter
        else if(name == GROUPING_FILTER) {
            formDataManager.putDefault("suid", ""+caller.getSuid());   
            formDataManager.putDefault("gsid", "");               
        }      
        
        // Default values for the group
        else if(name == GROUP) {
            formDataManager.putDefault("suid", ""+caller.getSuid());   
            formDataManager.putDefault("comm", "");
            formDataManager.putDefault("name", "");       
            formDataManager.putDefault("gsid", "");               
        }    
                
        // Default values for the group membership filter
        else if(name == GROUP_MEMBERSHIP_FILTER) {
            formDataManager.putDefault("suid", ""+caller.getSuid());
            formDataManager.putDefault("sex", "*");
            formDataManager.putDefault("father", "");
            formDataManager.putDefault("mother", "");
            formDataManager.putDefault("alias", "");
            formDataManager.putDefault("identity", "");
            formDataManager.putDefault("bdatefrom", "");
            formDataManager.putDefault("bdateto", "");
            formDataManager.putDefault("status", "E");
            formDataManager.putDefault("gid", "");
            formDataManager.putDefault("gsid", "");             
        }     
        
        // Default values for the sample create/update
        else if(name == SAMPLE) {
            formDataManager.putDefault("suid", ""+caller.getSuid());
            formDataManager.putDefault("name", "");
            formDataManager.putDefault("comm", "");
            formDataManager.putDefault("experimenter", "");
            formDataManager.putDefault("tissue", "");
            formDataManager.putDefault("iid", "");
            formDataManager.putDefault("storage", "");
            formDataManager.putDefault("date", "");
            formDataManager.putDefault("treatment", "");           
        }   
        
        // Default values for the sample view/edit filter
        else if(name == SAMPLE_FILTER) {
            formDataManager.putDefault("suid", ""+caller.getSuid());
            formDataManager.putDefault("name", "");
            formDataManager.putDefault("tissue_type", "");
            formDataManager.putDefault("identity", "");
            formDataManager.putDefault("storage", "");          
        }          
        
        // Default values for the phenotype create/edit filter
        else if(name == PHENOTYPE) {
            formDataManager.putDefault("suid", ""+caller.getSuid());
            formDataManager.putDefault("date", "");
            formDataManager.putDefault("value", "");
            formDataManager.putDefault("comm", "");
            formDataManager.putDefault("reference", "");
            formDataManager.putDefault("identity", "");
            formDataManager.putDefault("variable", "");          
        }      
        
        // Default values for the variable create/edit filter
        else if(name == VARIABLE_FILTER) {
            formDataManager.putDefault("v.suid", ""+caller.getSuid());
            formDataManager.putDefault("v.type", "*");
            formDataManager.putDefault("v.name", "");
            formDataManager.putDefault("v.unit", "");
            formDataManager.putDefault("vs.vsid", "*");        
        }      
        
        // Default values for the variable set create/edit filter
        else if(name == VARIABLE_SET_FILTER) {
            formDataManager.putDefault("suid", ""+caller.getSuid());
            formDataManager.putDefault("vsname", "");
            formDataManager.putDefault("vname", "");       
        }   
        
        // Default values for the phenotype view/edit filter
        else if(name == PHENOTYPE_FILTER) {
            formDataManager.putDefault("suid", ""+caller.getSuid());
            formDataManager.putDefault("name", "");
            formDataManager.putDefault("identity", "");
        }       
        
        // Default values for the unified variable view/edit filter
        else if(name == U_VARIABLE) {
            formDataManager.putDefault("pid", ""+caller.getPid());
            formDataManager.putDefault("sid", ""+caller.getSid());
            formDataManager.putDefault("type", "*");
            formDataManager.putDefault("name", "");
            formDataManager.putDefault("unit", "");
        }  
        
        // Default values for the variable set membership 
        else if(name == VARIABLE_SET_MEMBERSHIP_FILTER) {
            formDataManager.putDefault("suid", ""+caller.getSuid());
            formDataManager.putDefault("vsid", "");
        }          
        
        // Default values for the unified variable set membership 
        else if(name == U_VARIABLE_SET_MEMBERSHIP_FILTER) {
            formDataManager.putDefault("sid", ""+caller.getSid());
            formDataManager.putDefault("uvsid", "");
        }     
        
        // Default values for the unified variable set view/edit filter 
        else if(name == U_VARIABLE_SET_FILTER) {
            formDataManager.putDefault("sid", ""+caller.getSid());
            formDataManager.putDefault("pid", ""+caller.getPid());
            formDataManager.putDefault("vsname", "");
            formDataManager.putDefault("vname", "");            
        }   
        
        // Default values for the samplingunit details page
        else if(name == SAMPLINGUNIT_DETAILS) {
            formDataManager.putDefault("suid", ""+caller.getSuid());
            formDataManager.putDefault("linkid", "");           
            formDataManager.putDefault("fileid", "");             
        }    
        
        else if(name == EXPMODEL) {
            formDataManager.putDefault("suid", ""+caller.getSuid());
            formDataManager.putDefault("eid", "");                       
            formDataManager.putDefault("gmid", "");  
            formDataManager.putDefault("goid", ""); 
        }  
        
        else if (name == EXPMODELS) {
            formDataManager.putDefault("suid", ""+caller.getSuid());
            formDataManager.putDefault("raid","");
            formDataManager.putDefault("fstid","");
            formDataManager.putDefault("gaid","");
            formDataManager.putDefault("emap","");
            formDataManager.putDefault("ma","");
            formDataManager.putDefault("inducible","");
            formDataManager.putDefault("participantname","");
            formDataManager.putDefault("groupname", "");
            formDataManager.putDefault("mutationtypes","");
            formDataManager.putDefault("ordertype","");
            formDataManager.putDefault("disslevel","");
            formDataManager.putDefault("strain","");
            formDataManager.putDefault("delta","20");
            formDataManager.putDefault("page","1");
        }
        
        else if (name == PHENOTREE) {
            formDataManager.putDefault("levelOne", "");
            formDataManager.putDefault("levelTwo", "");
            formDataManager.putDefault("levelThree", "");
            formDataManager.putDefault("levelFour", "");
            formDataManager.putDefault("levelFive", "");
            formDataManager.putDefault("levelSix", "");
            formDataManager.putDefault("levelSeven", "");
            formDataManager.putDefault("levelEight", "");
            formDataManager.putDefault("levelNine", "");
        }
        
        else if (name == FAST_SEARCH) {
            formDataManager.putDefault("fast_search_key", "");
        }

        else if (name == ALLELE) {
            formDataManager.putDefault("aid", "");
            formDataManager.putDefault("attributes", "");
        }

        else if (name == STRAIN) {
            formDataManager.putDefault("strain_id", "");
        }

        else if (name == MUTATION_TYPE) {
            formDataManager.putDefault("mtid", "");
        }

        else if (name == EXPRESSION_MODEL) {
            formDataManager.putDefault("exid", "");
        }

        else if (name == STRAIN_TYPE) {
            formDataManager.putDefault("stid", "");
        }

        else if (name == STRAIN_STATE) {
            formDataManager.putDefault("ssid", "");
        }

        else if (name == GENE) {
            formDataManager.putDefault("gaid", "");
        }
        
        else if (name == STRAIN_ALLELES) {
            formDataManager.putDefault("delta","20");
            formDataManager.putDefault("promoter","");
            formDataManager.putDefault("inducible","");
            formDataManager.putDefault("made_by","");
            formDataManager.putDefault("ordertype","");
            formDataManager.putDefault("page","1");
        }
        
        else if (name == GENES) {
            formDataManager.putDefault("delta","20");
            formDataManager.putDefault("page","1");
        }
        
        else if (name == STRAINS) {
            formDataManager.putDefault("delta","20");
            formDataManager.putDefault("page","1");
        }

        return formDataManager;
    }   
}
