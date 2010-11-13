package org.tgdb.frame;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class Navigator {    
    private static Logger logger = Logger.getLogger(Navigator.class);
    
    private PageManager pageManager;
    
    private String currentWorkflow;
    
    private String prevWorkflow;
    
    private boolean back;
    
    public Navigator() {
        pageManager = new PageManager();
        pageManager.setFirst();
        pageManager.setDelta(20);
    }
    
    public void debug() {}
    
    public PageManager getPageManager() {
        return pageManager;
    }
    
    public void setNavigator(HttpServletRequest request)
    {
        if (request.getParameter("next")!=null)
        {
            logger.debug("Setting next in navigator");
            pageManager.setNext();
        }
        else if (request.getParameter("prev")!=null)
        {   
            pageManager.setPrev();
        }
        else if (request.getParameter("first")!=null)
        {
            pageManager.setFirst();
        }
        else if (request.getParameter("last")!=null)
        {
            pageManager.setLast();
        }
        else if (request.getParameter("last") == null && request.getParameter("first") == null && request.getParameter("prev") == null && request.getParameter("next") == null){
            pageManager.setFirst();
        }
        
        request.getSession().setAttribute("navigator", this);
    }

    public String getCurrentWorkflow() {
        return currentWorkflow;
    }

    public void setCurrentWorkflow(String currentWorkflow)    
    {
        if (this.currentWorkflow==null || !this.currentWorkflow.equals(currentWorkflow))
        {
            this.prevWorkflow = this.currentWorkflow;
            this.currentWorkflow = currentWorkflow;
        }
        
    }

//    public String getPrevWorkflow() {
//        return prevWorkflow;
//    }
    
//    public boolean goBack()
//    {
//        if (back)
//        {
//            back = false;
//            return true;
//        }
//        return false;
//    }    
}
