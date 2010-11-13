package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class GetCorrespondentsAction extends TgDbAction {
    
    public GetCorrespondentsAction() {}

    public String getName() {
        return "GetCorrespondentsAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller _caller = (TgDbCaller)req.getSession().getAttribute("caller");
            req.setAttribute("correspondents", adminManager.getCorrespondents("correspondent", _caller));
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not retrieve users.");
        }
    }      
}
