/*
 * ProjectDTO.java
 *
 * Created on July 14, 2005, 2:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.project.projectmanager;
import org.tgdb.project.project.ProjectRemote;
import java.io.Serializable;

/**
 * Data transfer object for project information
 * @author heto
 */
public class ProjectDTO implements Serializable {
    
    private int pid;
    private String name;
    private String comm;
    private String status;
    
    /**
     * Creates a new instance of ProjectDTO
     * @param pid The project id
     * @param name The project name
     */
    public ProjectDTO(int pid, String name) {
        this.pid=pid;
        this.name = name;
    }
    
    public ProjectDTO(ProjectRemote prj) {
        try {
            pid = prj.getPid();
            name = prj.getName();
            comm = prj.getComm();
            status = prj.getStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Returns the project id
     * @return The project id
     */
    public int getPid() {
        return pid;
    }
    
    /**
     * sets the project id
     * @param pid The project id
     */
    public void setPid(int pid) {
        this.pid = pid;
    }
    
    /**
     * Returns the name of the project
     * @return The name of the project
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the project
     * @param name The name of the project
     */
    public void setName(String name) {
        this.name = name;
    }
    public String getComm() {
        return comm;
    }
    public String getStatus() {
        return status;
    }    
}
