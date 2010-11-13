package org.tgdb.model.modelmanager;

import org.tgdb.model.geneticbackground.GeneticBackgroundValuesRemote;
import java.io.Serializable;

public class GeneticBackgroundValuesDTO implements Serializable {
    private int bid, pid;
    private String backname;
    
    
    /** Creates a new instance of GeneticBackgroundDTO */
public GeneticBackgroundValuesDTO(GeneticBackgroundValuesRemote genbackValues) {
        try{
            bid = genbackValues.getBid();
            backname = genbackValues.getBackname();
            pid = genbackValues.getPid();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getBid(){
        return bid;
    }
    
    public java.lang.String getBackname(){
        return backname;
    }
    
    public int getPid(){
        return pid;
    }
}
