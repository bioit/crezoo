/*
 * SamplingUnitDTO.java
 *
 * Created on July 14, 2005, 3:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.samplingunit.samplingunitmanager;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemote;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteHome;
import java.io.Serializable;
import java.util.Date;

/**
 * Data transfer object for sampling unit information
 * @author heto
 */
public class SamplingUnitDTO implements Serializable 
{
    /**
     * sampling unit id
     */
    private int suid;
    /**
     * The sampling unit name
     */
    private String name;
    
    /**
     * The sampling unit species name
     */
    private String sname;    
    /**
     * The comment
     */
    private String comm;
    /**
     * Status for the sampling unit. E - Enabled, D - Disabled
     */
    private String status;
    /**
     * The timestamp for latest changes
     */
    private Date ts;
    /**
     * The species id
     */
    private int sid;
    /**
     * The id of the user
     */
    private int id;
    
    /**
     * The number of individuals in this su
     */
    private int inds;
    
    /* 
     * The number of models in this su
     */
    private int numModels;
    
    /**
     * The number of projects this su exists in
     */
    private int numProjects;    
    
    /**
     * The user who last updated this sampling unit
     */
    private String usr;
    
    
    public SamplingUnitDTO()
    {}
    
    /**
     * Creates a new instance of SamplingUnitDTO
     * @param suid The sampling unit id
     * @param name The name of the sampling unit
     */
    public SamplingUnitDTO(int suid, String name) {
        this.suid = suid;
        this.name = name;
        this.usr = "Not inited";
        this.numProjects = -1;
        this.inds = -1;
        this.id = -1;
        this.sid = -1;
        this.ts = null;
        this.comm = "Not inited";
        this.sname = "Not inited";
    }
    
    /**
     * Creates a new instance of sampling unit dto
     * @param suid The sampling unit id
     * @param name The name of the sampling unit
     * @param sid The species id
     */
    public SamplingUnitDTO(int suid, String name, int sid) {
        this.suid = suid;
        this.name = name;
        this.usr = "Not inited";
        this.numProjects = -1;
        this.inds = -1;
        this.id = -1;
        this.sid = sid;
        this.ts = null;
        this.comm = "Not inited";
        this.sname = "Not inited";
    }    
    
    /**
     * Creates a new instance of sampling unit dto
     * @param su The sampling unit bean
     */
    public SamplingUnitDTO(SamplingUnitRemote su)
    {
        try{
            this.suid = su.getSuid();
            name = su.getName();
            sname = su.getSpecies().getName();
            comm = su.getComm();
            status = su.getStatus();
            ts = su.getTs();
            sid = su.getSpecies().getSid();

            id = su.getId();
            usr = su.getUser().getUsr();

            inds = 0;
            numProjects = su.getNumberOfProjects();
            numModels = su.getNumberOfExperimentalModels();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
     /**
     * Returns the number of individuals in this su
     * @return The number of individuals
     */
    public int getInds(){
        return inds;
    }
    
    /**
     * Sets the number of individuals
     * @param inds The number of individuals
     */    
    public void setInds(int inds){
        this.inds = inds;
    }

    /**
     * Returns the sampling unit id
     * @return The sampling unit id
     */
    public int getSuid() {
        return suid;
    }

    /**
     * Sets the sampling unit id
     * @param suid The new sampling unit id
     */
    public void setSuid(int suid) {
        this.suid = suid;
    }

    /**
     * Returns the name for the sampling unit
     * @return The sampling unit name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for the sampling unit data transfer object
     * @param name The sampling unit name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the comment for the sampling unit
     * @return The sampling unit comment
     */
    public String getComm() {
        return comm;
    }

    /**
     * Sets the comment for the sampling unit
     * @param comm The new comment
     */
    public void setComm(String comm) {
        this.comm = comm;
    }

    /**
     * Returns the status of the sampling unit
     * @return The status of the sampling unit
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the sampling unit
     * @param status The new status of the sampling unit
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the timestamp for when this sampling unit was last modified
     * @return The time for the last modification
     */
    public Date getTs() {
        return ts;
    }

    /**
     * Sets the time for when this sampling unit was modified
     * @param ts The new timestamp
     */
    public void setTs(Date ts) {
        this.ts = ts;
    }

    /**
     * Returns the species id for the sampling unit
     * @return The species id for the sampling unit
     */
    public int getSid() {
        return sid;
    }

    /**
     * Sets the species id for the sampling unit
     * @param sid The species id
     */
    public void setSid(int sid) {
        this.sid = sid;
    }

    /**
     * Returns the id for the user who performed the last update on the sampling unit
     * @return The id of the user who last modified the sampling unit
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id for the user who last updated the sampling unit
     * @param id The id of the user
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /** Returns the username who last updated this sampling unit
     * @return The user name
     */
    public String getUsr() {
        return usr;
    }

    /** Sets the username for the one who last updated this sampling unit
     * @param usr The user name
     */
    public void setUsr(String usr) {
        this.usr = usr;
    }

    /** Returns the species name
     * @return The species name
     */
    public String getSname() {
        return sname;
    }

    /** Sets the species name
     * @param sname The species name
     */       
    public void setSname(String sname) {
        this.sname = sname;
    }

    /** Returns the number of projects this su exists in
     * @return The number of projects
     */
    public int getNumProjects() {
        return numProjects;
    }

    /** Sets the number of projects this su exists in
     * @param numProjects The number of projects
     */        
    public void setNumProjects(int numProjects) {
        this.numProjects = numProjects;
    }
    
    public int getNumModels() {
        return numModels;
    }
    
}
