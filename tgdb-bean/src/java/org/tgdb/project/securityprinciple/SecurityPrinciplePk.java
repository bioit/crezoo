/*
 * SecurityPrinciplePK.java
 *
 * Created on May 23, 2005, 12:13 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.project.securityprinciple;

import java.io.Serializable;

/**
 * Compound primary key class
 * @author heto
 */
public class SecurityPrinciplePk implements Serializable
{
    private Integer pid;
    private Integer id;
    private Integer rid;
    
    /** Creates a new instance of SecurityPrinciplePK */
    public SecurityPrinciplePk() {
    }
    
    /**
     * Creates a new instance of the primary key
     * @param pid The project id
     * @param id The user id
     * @param rid The role id
     */
    public SecurityPrinciplePk(int pid, int id, int rid)
    {
        this.pid = new Integer(pid);
        this.id = new Integer(id);
        this.rid = new Integer(rid);
    }
    
    /**
     * Checks if the keys are equal
     * @param pk The primary key
     * @return A boolean value depending on if the keys match
     */
    public boolean equals(SecurityPrinciplePk pk)
    {
        if (pk.getId().equals(id) && 
                pk.getPid().equals(pid) &&
                pk.getRid().equals(rid))
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
        txt += pid +" "+ id +" "+ rid;
        return txt.hashCode();
    }

    /**
     * Returns the project id
     * @return The project id
     */
    public Integer getPid() {
        return pid;
    }

    /**
     * Returns the user id
     * @return The user id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Returns the role id
     * @return The role id
     */
    public Integer getRid() {
        return rid;
    }
    
}
