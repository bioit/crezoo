package org.tgdb.model.modelmanager;

import org.tgdb.model.availablegeneticbackgrounds.AvailableGeneticBackgroundRemote;
import java.io.Serializable;

public class AvailableGeneticBackgroundDTO implements Serializable {
    private int aid, pid;
    private String avbackname;
    
    
    /** Creates a new instance of AvailableGeneticBackgroundDTO */
public AvailableGeneticBackgroundDTO(AvailableGeneticBackgroundRemote avback) {
        try{
            aid = avback.getAid();
            avbackname = avback.getAvbackname();
            pid = avback.getPid();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getAid(){
        return aid;
    }
    
    public java.lang.String getAvbackname(){
        return avbackname;
    }
    
    public int getPid(){
        return pid;
    }
}
