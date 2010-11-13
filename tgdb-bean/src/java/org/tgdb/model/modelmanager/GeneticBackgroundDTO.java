package org.tgdb.model.modelmanager;

import org.tgdb.model.geneticbackground.GeneticBackgroundRemote;
import java.io.Serializable;

public class GeneticBackgroundDTO implements Serializable {
    private int gbid, eid, dna_origin, targeted_back, host_back, backcrossing_strain;
    private String backcrosses, dna_origin_name, targeted_back_name, host_back_name, backcrossing_strain_name;
    //protected ServiceLocator locator;
    //private GeneticBackgroundValuesRemoteHome genbackValuesHome;
    
    /** Creates a new instance of GeneticBackgroundDTO */
public GeneticBackgroundDTO(GeneticBackgroundRemote genback) {
        try{
            gbid = genback.getGbid();
            eid = genback.getEid();
            dna_origin = genback.getDna_origin();
            targeted_back = genback.getTargeted_back();
            host_back = genback.getHost_back();
            backcrossing_strain = genback.getBackcrossing_strain();
            backcrosses = genback.getBackcrosses();
            dna_origin_name = genback.getBackNameFromBackId(dna_origin);
            targeted_back_name = genback.getBackNameFromBackId(targeted_back);
            host_back_name = genback.getBackNameFromBackId(host_back);
            backcrossing_strain_name = genback.getBackNameFromBackId(backcrossing_strain);
                  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getGbid(){
        return gbid;
    }
    
    public int getEid(){
        return eid;
    }
    
    public int getDna_origin(){
        return dna_origin;
    }
    
    public int getTargeted_back(){
        return targeted_back;
    }
    
    public int getHost_back(){
        return host_back;
    }
    
    public int getBackcrossing_strain(){
        return backcrossing_strain;
    }
    
    public java.lang.String getBackcrosses(){
        return backcrosses;
    }
    
    public String getDna_origin_name(){
        return dna_origin_name;
    }
    
    public String getTargeted_back_name(){
        return targeted_back_name;
    }
    
    public String getHost_back_name(){
        return host_back_name;
    }
    
    public String getBackcrossing_strain_name(){
        return backcrossing_strain_name;
    }
}
