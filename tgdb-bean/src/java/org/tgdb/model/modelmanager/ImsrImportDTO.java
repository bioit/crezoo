package org.tgdb.model.modelmanager;

import org.tgdb.model.availability.AvailabilityRemote;
import java.io.Serializable;

public class ImsrImportDTO implements Serializable {
    private int strain_id;
    
    private int eid, rid, aid, stateid, typeid;
    private String reponame, avbackname, statename, typename;
    private String stateabbr, typeabbr;
    
    
    /** Creates a new instance of AvailabilityDTO */
public ImsrImportDTO(AvailabilityRemote availability) {
        try{
            eid = availability.getEid();
            rid = availability.getRid();
            aid = availability.getAid();
            stateid = availability.getStateid();
            typeid = availability.getTypeid();
            reponame = availability.getRepositoryName();
            avbackname = availability.getAvailableGeneticBackgroundName();
            statename = availability.getStateName();
            typename = availability.getTypeName();
            stateabbr = availability.getStateAbbr();
            typeabbr = availability.getTypeAbbr();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getEid(){
        return eid;
    }
    
    public int getRid(){
        return rid;
    }

    public int getAid(){
        return aid;
    }
    
    public int getStateid(){
        return stateid;
    }
    
    public int getTypeid(){
        return typeid;
    }
    
    public java.lang.String getReponame(){
        return reponame;
    }
    
    public java.lang.String getAvbackname(){
        return avbackname;
    }
    
    public java.lang.String getStatename(){
        return statename;
    }
    
    public java.lang.String getTypename(){
        return typename;
    }
    
    public java.lang.String getStateabbr(){
        return stateabbr;
    }
    
    public java.lang.String getTypeabbr(){
        return typeabbr;
    }
}
