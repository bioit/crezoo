/*
 * RoleDTO.java
 *
 * Created on July 19, 2005, 10:34 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.project.projectmanager;

import org.tgdb.project.role.RoleRemote;
import java.io.Serializable;

/**
 * Data transfer object for information about a role
 * @author heto
 */
public class RoleDTO implements Serializable {
    
    private int rid;
    private String name;
    private String comm;
    
    /**
     * Creates a new instance of RoleDTO
     * @param rid The role id
     * @param name The role name
     * @param comm The comment for the role
     */
    public RoleDTO(int rid, String name, String comm) 
    {
        this.rid = rid;
        this.name = name;
        this.comm = comm;
    }
    /**
     * Creates a new instance of RoleDTO
     * @param role The role bean
     */
    public RoleDTO(RoleRemote role) 
    {
        try
        {
            this.rid = role.getRid();
            this.name = role.getName();
            this.comm = role.getComm();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    

    /**
     * Returns the id of the role
     * @return The role id
     */
    public int getRid() {
        return rid;
    }

    /**
     * Sets the id of the role
     * @param rid The role id
     */
    public void setRid(int rid) {
        this.rid = rid;
    }

    /**
     * Returns the name of the role
     * @return The name of the role
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the role
     * @param name The name of the role
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the comment for the role
     * @return The comment for the role
     */
    public String getComm() {
        return comm;
    }

    /**
     * Sets the comment for the role
     * @param comm The commment for the role
     */
    public void setComm(String comm) {
        this.comm = comm;
    }
    
}
