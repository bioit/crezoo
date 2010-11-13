package org.tgdb.search;

import org.tgdb.model.researchapplication.ResearchApplicationRemote;

public class ResearchApplicationSearchResult extends SearchResult {
    
    public ResearchApplicationSearchResult() {}
    
    public ResearchApplicationSearchResult(ResearchApplicationRemote ra, String workflow) {
        
        try {
            this.workflow = workflow;
            this.name = ra.getName();
            this.comment = ra.getComm();
            type = "Research Application";            
            project = ra.getProject().getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
