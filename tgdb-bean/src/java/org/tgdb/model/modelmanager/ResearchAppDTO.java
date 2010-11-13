/*
 * ResearchAppDTO.java
 *
 * Created on December 14, 2005, 3:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.model.modelmanager;

import org.tgdb.model.expmodel.ExpModelRemote;
import org.tgdb.model.researchapplication.ResearchApplicationRemote;
import org.tgdb.project.user.UserRemote;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.project.projectmanager.ProjectDTO;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * Data transfer object for a research application
 * @author lami
 */
public class ResearchAppDTO implements Serializable {
    private String name, comm;
    private int raid;
    
    //just for now to overcome bug.
    private int pid;
    
    private int userId;
    private String userName, ts;
    
    private String models;
    private int numModels;
    
    private ProjectDTO prj;
    
    /**
     * Creates a new instance of ResearchAppDTO
     * @param resapp The research application
     */
    public ResearchAppDTO(ResearchApplicationRemote resapp) {
        try {
            //get name from resapp bean
            this.name = resapp.getName();
            //get comment from resapp
            this.comm = resapp.getComm();
            //get id for resapp
            this.raid = resapp.getRaid();
            //get the user id
            userId = resapp.getUser().getId();
            //now get the user's name
            userName = resapp.getUser().getName();
            
            this.pid = resapp.getProject().getPid();
            ts = resapp.getTs().toString();
            
            /*prj = new ProjectDTO(resapp.getProject());
            
            models = "";
            numModels=0;
            Collection modelsarr = resapp.getModels();
            Iterator i = modelsarr.iterator();
            while (i.hasNext())
            {
                ExpModelRemote model = (ExpModelRemote)i.next();
                //if (resapp.getCaller().getSuid() == model.getSamplingUnit().getSuid())
                //{
                    if (numModels!=0)
                        models +=", ";
                    models += model.getAlias();
                    numModels++;
                //}
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the name of the research application
     * @return The name of the research application
     */
    public String getName() {
        return name;
    }

    /**
     * The comment for the research application
     * @return The comment for the research application
     */
    public String getComm() {
        return comm;
    }

    /**
     * The research application id
     * @return The research application id
     */
    public int getRaid() {
        return raid;
    }
    
    public int getId() {
        return raid;
    }
    
    public String getProjectName()
    {
        return prj.getName();
    }
    
    public int getPid() {
        return pid;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getTs() {
        return ts;
    }
    
    public String getModels() {
        return models;
    }
    
    public int getNumberOfModels() {
        return numModels;
    }
}
