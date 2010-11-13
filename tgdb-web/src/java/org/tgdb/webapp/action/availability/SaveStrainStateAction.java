package org.tgdb.webapp.action.availability;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class SaveStrainStateAction extends TgDbAction {
    
    public String getName() {
        return "SaveStrainStateAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");

            String name = request.getParameter("name");
            String abbreviation = request.getParameter("abbreviation");

            if (isSubmit(request,"create")) {
                modelManager.createStrainState(name, abbreviation, _caller);
            }
            else if(isSubmit(request, "save")) {
                int ssid = Integer.parseInt(request.getParameter("ssid"));
                modelManager.updateStrainState(ssid, name, abbreviation, _caller);
            }
            
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return true;
    }
}
