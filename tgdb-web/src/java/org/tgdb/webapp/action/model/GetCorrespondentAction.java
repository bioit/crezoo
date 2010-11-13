package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class GetCorrespondentAction extends TgDbAction {
    
    public GetCorrespondentAction() {}

    public String getName() {
        return "GetCorrespondentAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller _caller = (TgDbCaller)req.getSession().getAttribute("caller");
            if(exists(req.getParameter("cid"))) {
                req.setAttribute("correspondent", adminManager.getCorrespondent(Integer.parseInt(req.getParameter("cid")), _caller));
            }
            else {
                throw new ApplicationException("Missing required request parameter");
            }
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return true;
    }      
}
