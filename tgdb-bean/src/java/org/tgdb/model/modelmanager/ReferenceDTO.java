/*
 * ReferenceDTO.java
 *
 * Created on December 19, 2005, 9:54 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.model.modelmanager;

import org.tgdb.model.reference.ReferenceRemote;
import org.tgdb.project.user.UserRemote;
import org.tgdb.resource.file.FileRemote;
import org.tgdb.resource.link.LinkRemote;
import java.io.Serializable;
import org.tgdb.frame.DTO;

/**
 * Data transfer object for a reference
 * @author lami
 */
public class ReferenceDTO extends DTO implements Serializable {
    private String name, comm, resource, userName, ts, edit, type, target, pubmed;
    private boolean primary;
    private int linkid, fileid, refid, userId;
    
    /**
     * Creates a new instance of ReferenceDTO
     * @param reference The reference
     */
    public ReferenceDTO(ReferenceRemote ref) {
        try {
            FileRemote file = ref.getFile();
            LinkRemote link = ref.getLink();
            
            if(file != null) {
                this.name = ref.getName(); //file.getName();
                this.comm = ref.getComm();//file.getComm();
                this.fileid = file.getFileId();
                linkid = 0;
                this.edit = processURl("Controller?workflow=EditModelFileReference&fileid="+fileid);
                this.resource = processURl("Controller?workflow=ViewFile&fileid="+fileid);
                type = "Document";
                target = "_blank";
                
            } else if(link != null) {
                this.name = ref.getName();//link.getName();
                this.comm = ref.getComm();//link.getComment();
                this.linkid = link.getLinkId();
                fileid = 0;
                this.resource = processURl(link.getUrl());                
                this.edit = processURl("Controller?workflow=EditModelLinkReference&linkid="+linkid);
                type = "Webblink";
                target = "_blank";
            }

            this.pubmed = ref.getPubmed();
            this.primary = ref.isPrimary();
            
            UserRemote user = ref.getUser();
            this.userName = user.getUsr();
            this.userId = user.getId();
                        
            this.ts = ref.getTs().toString();
            this.refid = ref.getRefid();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int hashCode() {
        String txt = new String();
        txt += refid +" ";
        return txt.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReferenceDTO other = (ReferenceDTO) obj;
        if (this.refid != other.refid) {
            return false;
        }
        return true;
    }

    public String getPubmed() {
        return pubmed;
    }

    public boolean getPrimary() {
        return primary;
    }
    
    /**
     * Returns an URL to the edit action
     * @return An URL linking to the edit action
     */
    public String getEdit() {
        return edit;
    }
    
    /**
     * Returns the target frame to open the reference in
     * @return The target frame to open the reference in
     */
    public String getTarget() {
        return target;
    }
    
    /**
     * The reference link URL
     * @return The reference URL
     */
    public String getResource() {
        return resource;
    }
    
    /**
     * Returns the type of reference, either 'Document' or 'Weblink'
     * @return The type of reference, either 'Document' or 'Weblink'
     */
    public String getType() {
        return type;        
    }
    
    /**
     * The name of the reference
     * @return The name of the reference
     */
    public String getName() {
        return name;
    }
    
    /**
     * The comment for the reference
     * @return The comment for the reference
     */
    public String getComm() {
        return comm;
    }
    
    /**
     * The id of the link
     * @return The id of the link
     */
    public int getLinkId() {
        return linkid;
    }
    
    /**
     * The id of the file
     * @return The id of the file
     */
    public int getFileId() {
        return fileid;
    }
    
    /**
     * The id of the reference
     * @return The id of the reference
     */
    public int getRefid() {
        return refid;
    }
    
    /**
     * The username of the user that made the last changes on the reference
     * @return The username of the user that made the last changes on the reference
     */
    public String getUserName() {
        return userName;
    }
    
    /**
     * Returns the id of the user that made the last changes
     * @return The id of the user that made the last changes on the reference
     */
    public int getUserId() {
        return userId;
    }
    
    /**
     * Returns the date for when the reference was last modified
     * @return The date for the last modification of the reference
     */
    public String getTs() {
        return ts;
    }
}
