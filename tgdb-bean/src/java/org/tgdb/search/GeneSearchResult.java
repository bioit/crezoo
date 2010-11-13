/*
 * GeneSearchResult.java
 *
 * Created on January 24, 2006, 3:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.search;

import org.tgdb.species.gene.GeneRemote;

/**
 *
 * @author heto
 */
public class GeneSearchResult extends SearchResult {
    
    /**
     * Creates a new instance of GeneSearchResult 
     */
    public GeneSearchResult(GeneRemote gene, String workflow) 
    {
        try
        {
            this.workflow = workflow;
            this.name = gene.getName().replaceAll("<","&lt;").replaceAll(">","&gt;");
            this.name = this.name.replaceAll("&lt;","<sup>").replaceAll("&gt;","</sup>");
            this.comment = gene.getComm();
            this.type = gene.getDistinguish().replace("_", " ");
            project = gene.getProject().getName();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }    
    
}
