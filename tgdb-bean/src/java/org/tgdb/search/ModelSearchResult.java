/*
 * ModelSearchResult.java
 *
 * Created on January 24, 2006, 12:29 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.search;

import org.tgdb.model.expmodel.ExpModelRemote;
import org.tgdb.project.project.ProjectRemote;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;


/**
 *
 * @author heto
 */
public class ModelSearchResult extends SearchResult implements Serializable {
    
    
    /**
     * Creates a new instance of ModelSearchResult 
     */
    public ModelSearchResult(ExpModelRemote model, String workflow) 
    {
        try
        {
            this.workflow = workflow;
            this.name = model.getAlias().replaceAll("<","&lt;").replaceAll(">","&gt;");
            this.name = this.name.replaceAll("&lt;","<sup>").replaceAll("&gt;","</sup>");
            this.comment = model.getComm();
            type = "mouse";
            
            Collection projects = model.getSamplingUnit().getProjects();
            Iterator i = projects.iterator();
            int j = 0;
            project = "";
            while (i.hasNext())
            {
                if (j!=0)
                    project += ", ";
                project += ((ProjectRemote)i.next()).getName();
                j++;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }    
}
