package org.tgdb.webapp.action.availability;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class DeleteStrainStateAction extends TgDbAction {
    
    public String getName() {
        return "DeleteStrainStateAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");

            if(exists(request.getParameter("ssid"))) {
                modelManager.deleteStrainState(Integer.parseInt(request.getParameter("ssid")), _caller);
            }
            
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return true;
    }
}
