/*
 * PrivilegeDTO.java
 *
 * Created on July 19, 2005, 1:43 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.project.projectmanager;

import org.tgdb.project.privilege.PrivilegeRemote;
import java.io.Serializable;

/**
 * Data transfer object for information about privileges
 * @author heto
 */
public class PrivilegeDTO implements Serializable {
    
    private int prid;
    private String name;
    private String comm;
    private boolean selected;
    
    /** Creates a new instance of PrivilegeDTO */
    public PrivilegeDTO() {
    }
    
    /**
     * Creates an instance of the data transfer object
     * @param priv The privilege
     */
    public PrivilegeDTO(PrivilegeRemote priv)
    {
        try
        {
            this.prid = priv.getPrid();
            this.name = priv.getName();
            this.comm = priv.getComm();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Returns the id of the privilege
     * @return The id of the privilege
     */
    public int getPrid() {
        return prid;
    }

    /**
     * Sets the id of the privilege
     * @param prid The privilege id
     */
    public void setPrid(int prid) {
        this.prid = prid;
    }

    /**
     * Returns the name of the privilege
     * @return The name of the privilege
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the privilege
     * @param name The name of the privilege
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the comment for the privilege
     * @return The comment for the privilege
     */
    public String getComm() {
        return comm;
    }

    /**
     * Sets the comment for the privilege
     * @param comment The comment
     */
    public void setComm(String comment) {
        this.comm = comment;
    }

    /**
     * Returns whether or not this privilege is selected
     * @return If the privilege is selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets whether or not the privilege is selected
     * @param selected True or false depending on if the project should is selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
}
