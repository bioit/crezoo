package org.tgdb.webapp.action;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class RequestLogAction extends TgDbAction {
    
    public RequestLogAction() {}
    
    public String getName() {
        return "RequestLogAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) {
        
        logger.debug("---------------------------------------->RequestLogAction#performAction: Request query = '"+req.getQueryString() + "'");
        
        return true;
    }
    
}
