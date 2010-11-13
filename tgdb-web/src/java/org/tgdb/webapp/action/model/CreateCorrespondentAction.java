package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class CreateCorrespondentAction extends TgDbAction {
    
    public CreateCorrespondentAction() {}
    
    public String getName() {
        return "CreateCorrespondentAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = req.getSession();
            TgDbCaller _caller = (TgDbCaller)session.getAttribute("caller");
            
            if (isSubmit(req, "create")) {
                String name = req.getParameter("name");
                String email = req.getParameter("email");
                adminManager.createCorrespondent(name, email, "correspondent", _caller);
            } else if (isSubmit(req, "save")) {
                int cid = Integer.parseInt(req.getParameter("cid"));
                String name = req.getParameter("name");
                String email = req.getParameter("email");
                adminManager.updateCorrespondent(cid, name, email, _caller);
            } else if(isSubmit(req, "remove")) {
                //will do something in the future
            }

            return true;
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        
        return false;
    }      
}
