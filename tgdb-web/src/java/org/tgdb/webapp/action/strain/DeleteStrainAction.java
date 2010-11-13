package org.tgdb.webapp.action.strain;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class DeleteStrainAction extends TgDbAction {
    
    public DeleteStrainAction() {}

    public String getName() {
        return "DeleteStrainAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");

            if(exists(req.getParameter("strainid"))) {
                int strainid = Integer.parseInt(req.getParameter("strainid"));
                modelManager.deleteStrain(strainid, _caller);
            }
            
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return true;
    }     
}
