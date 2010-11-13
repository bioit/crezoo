/*
 * Role.java
 *
 * Created on June 30, 2005, 9:00 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Class for handling information about a role
 * @author heto
 */
public class Role 
{
    private int rid;
    private String name;
    
    Collection privileges;
    
    /**
     * Creates a new instance of Role
     * @param rid The role id
     * @param name The name of the role
     */
    public Role(int rid, String name) 
    {
        this.rid = rid;
        this.name = name;
        
        privileges = new ArrayList();
    }
    
    /**
     * Adds a privilege to the role
     * @param pri The privilege to add
     */
    public void addPrivilege(Privilege pri)
    {
        privileges.add(pri);
    }
    
    /**
     * Returns the privileges assosciated with the role
     * @return The privileges assosciated with the role
     */
    public Collection getPrivileges()
    {
        return privileges;
    }

    /**
     * Returns the id of the role
     * @return The id of the role
     */
    public int getRid() {
        return rid;
    }

    /**
     * Returns the name of the role
     * @return The name of the role
     */
    public String getName() {
        return name;
    }
    
}
