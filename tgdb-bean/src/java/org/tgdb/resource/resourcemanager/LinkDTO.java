/*
 * LinkDTO.java
 *
 * Created on December 5, 2005, 2:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.resource.resourcemanager;

import org.tgdb.resource.link.LinkRemote;

/**
 * Data transfer object for a link
 * @author lami
 */
public class LinkDTO {
    private int linkId, pid;
    private String name, url, comm, user;
    private java.sql.Date ts;
    
    public LinkDTO()
    {
    }
    
    /**
     * Creates a new instance of LinkDTO
     * @param link The link bean
     */
    public LinkDTO(LinkRemote link) {
        try {
            linkId = link.getLinkId();
            name = link.getName();
            comm = link.getComment();
            ts  = link.getTs();
            url = link.getUrl();
            user = link.getUser().getUsr();
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }

    /**
     * Returns the id of the link
     * @return The link id
     */
    public int getLinkId() {
        return linkId;
    }

    /**
     * Returns the project id for the link
     * @return The project id
     */
    public int getPid() {
        return pid;
    }

    /**
     * Returns the name of the link
     * @return The name of the link
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the URL of the link
     * @return The link URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the comment for the link
     * @return The comment for the link
     */
    public String getComm() {
        return comm;
    }

    /**
     * Returns the username of the user that made the last changes on the link
     * @return The username of the user that made the last changes on the link
     */
    public String getUser() {
        return user;
    }

    /**
     * Returns the date for when the link was last modified
     * @return The date for when the last modifications were made on the link
     */
    public String getTs() {
        return ts.toString();
    }
    
}
