/*
 * ChromosomeDTO.java
 *
 * Created on August 22, 2005, 10:23 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.model.modelmanager;

import org.tgdb.species.chromosome.ChromosomeRemote;
import java.io.Serializable;

/**
 * Data transfer object for chromosome data
 * @author lami
 */
public class ChromosomeDTO implements Serializable, Comparable {  
    private String name, abbr, comm, speciesname;
    private int cid;
    
    /**
     * Creates a new instance of ChromosomeDTO
     * @param cr The chromosome bean
     */
    public ChromosomeDTO(ChromosomeRemote cr) {
        try{
            this.name = cr.getName();
            this.abbr = cr.getAbbr();
            this.comm = cr.getComm();
            this.cid = cr.getCid();
            this.speciesname = cr.getSpecies().getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a new instance of ChromosomeDTO
     */
    public ChromosomeDTO() {
        this.name = "*";
        this.cid = 0;
        this.comm = "";
        this.speciesname = "";
    }
    
    // Constructor used for making minimal objects used for comparison when only id is available
    /**
     * Creates a new instance of ChromosomeDTO
     * @param cid The chromsome id
     */
    public ChromosomeDTO(int cid) {
        this.cid = cid;
    }     

    /**
     * Returns the name of the chromosome
     * @return The name
     */
    public String getName() {
        if(name == null)
            return "";
        return name;
    }
    
   /**
     * Returns the abbreviation of the chromosome
     * @return The abbreviation
     */
    public String getAbbr() {
        if(abbr == null)
            return "";
        return abbr;
    }

    /**
     * Returns the comment for the chromosome
     * @return The comment
     */
    public String getComm() {
        if(comm == null)
            return "";        
        return comm;
    }

    /**
     * Returns the speciesname for the chromsome
     * @return The species name
     */
    public String getSpeciesname() {
        if(speciesname == null)
            return "";        
        return speciesname;
    }

    /**
     * Returns the id for the chromosome
     * @return The chromosome id
     */
    public int getCid() {
        return cid;
    }
    
    /**
     * Compares this object to another object
     * @param anotherObj The object to compare with
     * @throws java.lang.ClassCastException If the object to compare with is of another type than ChromosomeDTO
     * @return Wether or not the objects are identical, see comparable interface for details
     */
    public int compareTo(Object anotherObj) throws ClassCastException {
        if(!(anotherObj instanceof ChromosomeDTO))
            throw new ClassCastException("Object is of wrong class. ChromosomeDTO object expected but not found.");
        return cid - ((ChromosomeDTO)anotherObj).getCid();
    }      
}
