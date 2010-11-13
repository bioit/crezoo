/*
 * UserDTO.java
 *
 * Created on July 29, 2005, 4:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.project.projectmanager;

import org.tgdb.project.user.UserRemote;
import org.tgdb.resource.link.LinkRemote;
import java.io.Serializable;

/**
 * Data transfer object for information about a user
 * @author heto
 */

public class UserDTO implements Serializable, Comparable {
    
    private int id;
    private String name;
    private String usr;
    private String pwd;
    private String email;
    private String status;
    private String userLinkUrl;
    private String userLinkName;
    
    // Research group info
    private String groupName;
    private String groupAddress;
    private String groupPhone;
    private String groupLinkName;
    private String groupLinkUrl;
    
    
    /**
     * Creates a new instance of UserDTO
     */
    public UserDTO()
    {
        
    }
    
    /**
     * Creates a new instance of UserDTO
     * @param id The id of the user
     * @param name The name of the user
     * @param usr The username of the user
     */
    public UserDTO(int id, String name, String usr) 
    {
        this.id=id;
        this.name=name;
        this.usr=usr;
    }
    
    /**
     * Creates a new instance of UserDTO
     * @param user The user bean
     */
    public UserDTO(UserRemote user)
    {
        try
        {            
            id = user.getId();
            name = user.getName();
            usr = user.getUsr();
            pwd = user.getPwd();
            email = user.getEmail();
            groupName = user.getGroupName();
            groupAddress = user.getGroupAddress();
            groupPhone = user.getGroupPhone();                                 
            
            // Link specific stuff
            LinkRemote groupLink = user.getGroupLink();
            LinkRemote userLink = user.getUserLink();
             
            if(groupLink != null) {
                groupLinkName = groupLink.getName();
                groupLinkUrl = groupLink.getUrl();
            }
            if(userLink != null) {
                userLinkName = userLink.getName();
                userLinkUrl = userLink.getUrl();            
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Returns the id of the user
     * @return The id of the user
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the user
     * @param id The id of the user
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the name of the user
     * @return The name of the user
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the password for the user
     * @return The password for the user
     */
    public String getPwd() {
        return pwd;
    }    
    
    /**
     * Returns the status of the user
     * @return The status of the user
     */
    public String getStatus() {
        return status;
    }    

    /**
     * Sets the name of the user
     * @param name The name of the user
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns the email for the user
     * @return The email for the user
     */
    public String getEmail() {
        if(email == null || email.equals("null"))
            return "-";          
        return email;
    }

    /**
     * Sets the email for the user
     * @param email The email for the user
     */
    public void setEmail(String email) {
        this.email = email;
    }    

    /**
     * Returns the username of the user
     * @return The username of the user
     */
    public String getUsr() {
        return usr;
    }

    /**
     * Sets the username of the user
     * @param usr The username of the user
     */
    public void setUsr(String usr) {
        this.usr = usr;
    }

    /**
     * The URL to the users webpage
     * @return The URL to the users webpage
     */
    public String getUserLinkUrl() {
        if(userLinkUrl == null || userLinkUrl.equals("null"))
            return "http://#";             
        return userLinkUrl;
    }

    /**
     * Returns the name of the weblink
     * @return The name of the weblink
     */
    public String getUserLinkName() {
        if(userLinkName == null || userLinkName.equals("null"))
            return "http://#";         
        return userLinkName;
    }

    /**
     * Returns the name of the research group
     * @return The name of the research group
     */
    public String getGroupName() {
        if(groupName == null || groupName.equals("null"))
            return "-";          
        return groupName;
    }

    /**
     * Returns the address for the research group
     * @return The name of the research group
     */
    public String getGroupAddress() {
        if(groupAddress == null || groupAddress.equals("null"))
            return "-";             
        return groupAddress;
    }

    /**
     * The phonenumber for the research group
     * @return The phone number for the research group
     */
    public String getGroupPhone() {
        if(groupPhone == null || groupPhone.equals("null"))
            return "-";           
        return groupPhone;
    }

    /**
     * Returns the name of the weblink to the groups webpage
     * @return The linkname for the groups webpage
     */
    public String getGroupLinkName() {
        if(groupLinkName == null || groupLinkName.equals("null"))
            return "http://#";
        return groupLinkName;
    }

    /**
     * Returns the URL to the groups webpage
     * @return The URL to the groups webpage
     */
    public String getGroupLinkUrl() {
        if(groupLinkUrl == null || groupLinkUrl.equals("null"))
            return "http://#";        
        return groupLinkUrl;
    }
    
    /**
     * Compares this group dto to another object
     * @param anotherObj The object to compare with
     * @throws java.lang.ClassCastException If the object to compare with is not of correct type
     * @return Wether or not the objects are equal
     */
    public int compareTo(Object anotherObj) throws ClassCastException {
        if(!(anotherObj instanceof UserDTO))
            throw new ClassCastException("Object is of wrong class. UserDTO object expected but not found.");
        return getName().compareTo(((UserDTO)anotherObj).getName());
    }
    
    public boolean equals(Object o)
    {
        if(!(o instanceof UserDTO))
            throw new ClassCastException("Object is of wrong class. UserDTO object expected but not found.");
        if (id==((UserDTO)o).getId())
            return true;
        return false;
    }
}
