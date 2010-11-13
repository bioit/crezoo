package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GetStrainTypesAction extends TgDbAction {
    
    public GetStrainTypesAction() {}

    public String getName() {
        return "GetStrainTypesAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            
            req.setAttribute("straintypes", modelManager.getStrainTypes(_caller));
            
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return true;
    }     
}
