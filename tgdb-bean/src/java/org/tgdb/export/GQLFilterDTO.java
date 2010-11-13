/*
 * GQLFilterDTO.java
 *
 * Created on November 16, 2005, 9:35 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.export;
import org.tgdb.export.filter.GQLFilterRemote;
import org.tgdb.species.species.SpeciesRemote;
import java.io.Serializable;

/**
 * Data transfer object for a GQL-filter
 * @author lami
 */
public class GQLFilterDTO implements Serializable {
    private String name, comm, expression, user, updated, species;
    private int sid, pid, fid;
    
    /**
     * Creates a new instance of GQLFilterDTO
     * @param filter The remote interface for a filter
     */
    public GQLFilterDTO(GQLFilterRemote filter) {
        try {
            this.fid = filter.getFid();
            this.name = filter.getName();
            this.comm = filter.getComm();
            this.expression = filter.getExpression();            
            this.pid = filter.getProject().getPid();
            this.user = filter.getUser().getUsr();
            this.updated = filter.getUpdated().toString();
            
            SpeciesRemote sr = filter.getSpecies();
            this.sid = sr.getSid();            
            this.species = sr.getName();
        } catch(Exception e){
            e.printStackTrace();
        }        
    }
    
    /**
     * Returns the species name for the filter
     * @return The species name
     */
    public String getSpecies() {
        if(species == null)
            return "";
        return species;
    }    

    /**
     * Returns the name for the filter
     * @return The name
     */
    public String getName() {
        if(name == null)
            return "";
        return name;
    }

    /**
     * Return the comment for the filter
     * @return The comment
     */
    public String getComm() {
        if(comm == null)
            return "";        
        return comm;
    }

    /**
     * Return the expression for this filter
     * @return The expression
     */
    public String getExpression() {
        if(expression == null)
            return "";        
        return expression;
    }

    /**
     * Returns the username of the user that made the last changes on the filter
     * @return The username
     */
    public String getUser() {
        if(user == null)
            return "";        
        return user;
    }

    /**
     * Return the date for when the filter was last updated
     * @return The date for when the filter was last updated
     */
    public String getUpdated() {
        if(updated == null)
            return "";        
        return updated;
    }

    /**
     * Returns the species id for the filter
     * @return The species id
     */
    public int getSid() {
        return sid;
    }

    /**
     * Returns the project id for the filter
     * @return The project id
     */
    public int getPid() {
        return pid;
    }

    /**
     * Returns the filter id
     * @return The filter id
     */
    public int getFid() {
        return fid;
    }
    
}
