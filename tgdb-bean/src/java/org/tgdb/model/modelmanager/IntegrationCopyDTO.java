package org.tgdb.model.modelmanager;

import org.tgdb.genome.integrationcopy.IntegrationCopyRemote;
import java.util.Collection;
import java.util.Iterator;
import java.io.Serializable;

public class IntegrationCopyDTO implements Serializable {
    
    private int iscmid;
    private String isite, cnumber;
    
    public IntegrationCopyDTO(IntegrationCopyRemote ic) {
        try {
            iscmid = ic.getIscnid();
            isite = ic.getIsite();
            cnumber = ic.getCnumber();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int getIscnid()
    {
        return iscmid;
    }
    
    public int getIscmid()
    {
        return iscmid;
    }
    
    public String getIsite()
    {
        return isite;
    }
    
    public String getCnumber(){
        return cnumber;
    }
}
