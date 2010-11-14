package org.tgdb.model.availability;

import java.io.Serializable;

public class AvailabilityPk implements Serializable {
    
    private Integer eid;
    private Integer rid;
    private Integer aid;
    private Integer stateid;
    private Integer typeid;
    private Integer strainid;
    
    
    /** Creates a new instance of AvailabilityPk */
    public AvailabilityPk() {
    }
    
    /** Creates a new instance of AvailabilityPk */
    public AvailabilityPk(int eid, int rid, int aid, int stateid, int typeid, int strainid) {
        this.eid = new Integer(eid);
        this.rid = new Integer(rid);
        this.aid = new Integer(aid);
        this.stateid = new Integer(stateid);
        this.typeid = new Integer(typeid);
        this.strainid = new Integer(strainid);
    }
    
    /**
     * Checks if the keys are equal
     * @param pk The primary key
     * @return A boolean value depending on if the keys match
     */
    public boolean equals(AvailabilityPk pk)
    {
        if (pk.getRid().equals(rid) && 
                pk.getEid().equals(eid) &&
                pk.getAid().equals(aid) &&
                pk.getStateid().equals(stateid) &&
                pk.getTypeid().equals(typeid) &&
                pk.getStrainid().equals(strainid))
            return true;
        else 
            return false;
    }
    
    /**
     * Returns a hashcode for the key
     * @return A hashcode for the key
     */
    public int hashCode()
    {
        String txt = new String();
        txt += eid +" "+ rid +" "+ aid +" "+ stateid +" "+ typeid +" "+ strainid;
        return txt.hashCode();
    }

    /**
     * Returns the model id
     * @return The model id
     */
    public Integer getEid() {
        return eid;
    }

    /**
     * Returns the repository id
     * @return The repository id
     */
    public Integer getRid() {
        return rid;
    }

    /**
     * Returns the available genetic background id
     * @return The available genetic background id
     */
    public Integer getAid() {
        return aid;
    }
    
    /**
     * Returns the strain state id
     * @return The strain state id
     */
    public Integer getStateid() {
        return stateid;
    }
    
    /**
     * Returns the strain type id
     * @return The strain type id
     */
    public Integer getTypeid() {
        return typeid;
    }

    public Integer getStrainid() {
        return strainid;
    }
    
}
