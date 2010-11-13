/*
 * Privilege.java
 *
 * Created on June 30, 2005, 9:02 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb;

import org.tgdb.project.privilege.PrivilegeRemote;
import java.io.Serializable;

/**
 * Class for handling information about a privilege
 * @author heto
 */
public class Privilege implements Serializable 
{
    private int prid;
    private String name;
    
    public Privilege(PrivilegeRemote priv)
    {
        try
        {
            prid = priv.getPrid();
            name = priv.getName();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a new instance of Privilege
     * @param prid The id of the privilege
     * @param name The name of the privilege
     */
    public Privilege(int prid, String name) 
    {
        this.prid = prid;
        this.name = name;
    }

    /**
     * Returns the id of the privilege
     * @return The id of the privilege
     */
    public int getPrid() {
        return prid;
    }

    /**
     * Returns the name of the privilege
     * @return The name of the privilege
     */
    public String getName() {
        return name;
    }
    
    
}
