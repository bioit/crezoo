package org.tgdb.search;

import org.tgdb.model.strain.allele.StrainAlleleRemote;

public class StrainAlleleSearchResult extends SearchResult {
    
    public StrainAlleleSearchResult(StrainAlleleRemote strain_allele, String workflow) {
        try {
            this.workflow = workflow;
            this.name = strain_allele.getName().replaceAll("<","&lt;").replaceAll(">","&gt;");
            this.name = this.name.replaceAll("&lt;","<sup>").replaceAll("&gt;","</sup>");
            this.comment = "";
            this.type = "allele";
            project = "";
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
}
