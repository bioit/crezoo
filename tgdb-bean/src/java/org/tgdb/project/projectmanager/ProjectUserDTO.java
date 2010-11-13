/*
 * ProjectUserDTO.java
 *
 * Created on July 29, 2005, 4:47 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.project.projectmanager;
import org.tgdb.project.user.UserRemote;
import java.io.Serializable;

/**
 * Data transfer object for information about a project and user
 * @author heto
 */
public class ProjectUserDTO extends UserDTO implements Serializable, Comparable
{
    private String roleName;
    private int rid;
    
    /** Creates a new instance of ProjectUserDTO */
    public ProjectUserDTO() {
    }
    
    /**
     * Creates a new instance of ProjectUserDTO
     * @param user The user bean
     */
    public ProjectUserDTO(UserRemote user)
    {
        super(user);
    }
    
    /**
     * Returns the name of the role
     * @return The name of the role
     */
    public String getRoleName() {
        return roleName;
    }
    
    /**
     * Sets the name of the role
     * @param roleName The new name
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Returns the role id
     * @return The role id
     */
    public int getRid() {
        return rid;
    }

    /**
     * Sets the role id
     * @param rid The role id
     */
    public void setRid(int rid) {
        this.rid = rid;
    }
    
    /**
     * Compares this group dto to another object
     * @param anotherObj The object to compare with
     * @throws java.lang.ClassCastException If the object to compare with is not of correct type
     * @return Wether or not the objects are equal
     */
    public int compareTo(Object anotherObj) throws ClassCastException {
        if(!(anotherObj instanceof ProjectUserDTO))
            throw new ClassCastException("Object is of wrong class. ProjectUserDTO object expected but not found.");
        return getName().compareTo(((ProjectUserDTO)anotherObj).getName());
    }
    
}
