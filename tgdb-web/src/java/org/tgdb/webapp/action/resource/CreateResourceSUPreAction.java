package org.tgdb.webapp.action.resource;

import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class CreateResourceSUPreAction extends TgDbAction {
    
    public CreateResourceSUPreAction() {}
    
    public String getName() {
        return "CreateResourceSUPreAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();            
            
            String suid = request.getParameter("suid");
            workflow.setAttribute("suid", suid);
            
            logger.debug("---------------------------------------->CreateResourceSUPreAction#performAction: suid = "+suid);
            
            return true;
        } catch (Exception e) {
            logger.error("---------------------------------------->CreateResourceSUPreAction#performAction: Failed");
            throw new ApplicationException("CreateResourceSUPreAction",e);
        }
    }      
}
