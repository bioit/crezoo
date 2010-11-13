/*
 * Project.java
 *
 * Created on January 20, 2006, 9:23 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb;

import org.tgdb.project.project.ProjectRemote;
import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author heto
 */
public class Project implements Serializable {
    
    private HashMap privs;
    
    private String name;
    private int pid;
          
    
    
    /** Creates a new instance of Project */
    public Project(ProjectRemote project) {
        try
        {
            name = project.getName();
            pid = project.getPid();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }

    public HashMap getPrivs() {
        return privs;
    }

    public String getName() {
        return name;
    }

    public int getPid() {
        return pid;
    }
    
    public void setPrivs(HashMap privHash)
    {
        privs = privHash;
    }
}
