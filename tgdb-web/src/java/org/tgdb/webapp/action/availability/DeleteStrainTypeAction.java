package org.tgdb.webapp.action.availability;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class DeleteStrainTypeAction extends TgDbAction {
    
    public String getName() {
        return "DeleteStrainTypeAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");

            if(exists(request.getParameter("stid"))) {
                modelManager.deleteStrainType(Integer.parseInt(request.getParameter("stid")), _caller);
            }
            
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return true;
    }
}
