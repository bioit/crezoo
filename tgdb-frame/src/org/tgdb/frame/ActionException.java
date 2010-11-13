package org.tgdb.frame;

import org.tgdb.frame.advanced.Workflow;

public class ActionException extends ArxFrameException {
    
    private String alt;
    private Workflow workflow;
    
    public ActionException(String msg) {
        super(msg);
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }
    
    
    
    
    
}
