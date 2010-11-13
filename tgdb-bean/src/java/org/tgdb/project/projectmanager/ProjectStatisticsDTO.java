/*
 * ProjectStatisticsDTO.java
 *
 * Created on July 26, 2005, 3:53 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.project.projectmanager;

import java.io.Serializable;

/**
 * A data transfer object for statistics information about projects
 * @author heto
 */
public class ProjectStatisticsDTO implements Serializable {
    
    private int pid;
    private String name;
    private String comm;
    
    private int users;
    private int species;
    private int samplingunits;
    private int individuals;
    private int samples;
    private int variables;
    private int phenotypes;
    private int markers;
    private int genotypes;
            
    
           
    
    /**
     * Creates a new instance of ProjectStatisticsDTO 
     */
    public ProjectStatisticsDTO() {
    }

    /**
     * Returns the project id
     * @return The project id
     */
    public int getPid() {
        return pid;
    }

    /**
     * Sets the project id
     * @param pid The project id
     */
    public void setPid(int pid) {
        this.pid = pid;
    }

    /**
     * Returns the name of the project
     * @return The project name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the project
     * @param name The name of the project
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the comment for the project
     * @return The comment for the project
     */
    public String getComm() {
        return comm;
    }

    /**
     * Sets the comment for the project
     * @param comm The comment for the project
     */
    public void setComm(String comm) {
        this.comm = comm;
    }

    /**
     * Returns the number of users for the project
     * @return The number of users for the project
     */
    public int getUsers() {
        return users;
    }

    /**
     * Sets the number of users for the project
     * @param users The number of users
     */
    public void setUsers(int users) {
        this.users = users;
    }

    /**
     * Returns the number of species in the project
     * @return The number of species in the project
     */
    public int getSpecies() {
        return species;
    }

    /**
     * Sets the number of species in the project
     * @param species The number of species in the project
     */
    public void setSpecies(int species) {
        this.species = species;
    }

    /**
     * Returns the number of sampling units in the project
     * @return The number of sampling units in the project
     */
    public int getSamplingunits() {
        return samplingunits;
    }

    /**
     * Sets the number of sampling units in the project
     * @param samplingunits The number of sampling units in the project
     */
    public void setSamplingunits(int samplingunits) {
        this.samplingunits = samplingunits;
    }

    /**
     * Returns the number of individuals in the project
     * @return The number of individuals in the project
     */
    public int getIndividuals() {
        return individuals;
    }

    /**
     * Sets the number of individuals in the project
     * @param individuals The number of individuals in the project
     */
    public void setIndividuals(int individuals) {
        this.individuals = individuals;
    }

    /**
     * Returns the number of samples in the project
     * @return The number of samples in the project
     */
    public int getSamples() {
        return samples;
    }

    /**
     * Sets the number of samples in the project
     * @param samples The number of samples in the project
     */
    public void setSamples(int samples) {
        this.samples = samples;
    }

    /**
     * Returns the number of variables in the project
     * @return The number of variables in the project
     */
    public int getVariables() {
        return variables;
    }

    /**
     * Sets the number of variables in the project
     * @param variables The number of variables in the project
     */
    public void setVariables(int variables) {
        this.variables = variables;
    }

    /**
     * Returns the number of phenotypes in the project
     * @return The number of phenotypes in the project
     */
    public int getPhenotypes() {
        return phenotypes;
    }

    /**
     * Sets the number of phenotypes in the project
     * @param phenotypes The number of phenotypes in the project
     */
    public void setPhenotypes(int phenotypes) {
        this.phenotypes = phenotypes;
    }

    /**
     * Returns number of markers in the project
     * @return The number of markers in the project
     */
    public int getMarkers() {
        return markers;
    }

    /**
     * Sets the number of markers in the project
     * @param markers The number of markers in the project
     */
    public void setMarkers(int markers) {
        this.markers = markers;
    }

    /**
     * Returns the number of genotypes in the project
     * @return The number of genotypes in the project
     */
    public int getGenotypes() {
        return genotypes;
    }

    /**
     * Sets the number of genotypes in the project
     * @param genotypes The number of genotypes in the project
     */
    public void setGenotypes(int genotypes) {
        this.genotypes = genotypes;
    }
    
}
