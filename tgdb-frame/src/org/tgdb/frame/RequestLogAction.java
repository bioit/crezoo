package org.tgdb.frame;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;


public class RequestLogAction extends Action {
    
    private static Logger logger = Logger.getLogger(RequestLogAction.class);
    
    public RequestLogAction() {}
    
    public String getName() {
        return "RequestLogAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) {
        logger.debug("------------------------------>RequestLogAction#performAction: Request query = '"+req.getQueryString() + "'");
        return true;
    }
    
}
