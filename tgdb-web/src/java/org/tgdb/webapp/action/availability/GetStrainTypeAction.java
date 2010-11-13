package org.tgdb.webapp.action.availability;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class GetStrainTypeAction extends TgDbAction {
    
    public String getName() {
        return "GetStrainTypeAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller _caller = (TgDbCaller)req.getSession().getAttribute("caller");
            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.STRAIN_TYPE, TgDbFormDataManagerFactory.WEB_FORM, req);

            if(exists(req.getParameter("stid"))) {
                fdm.put("stid", req.getParameter("stid"));
                req.getSession().setAttribute(TgDbFormDataManagerFactory.getInstanceName(TgDbFormDataManagerFactory.STRAIN_TYPE), fdm);
            }

            req.setAttribute("strain_type", modelManager.getStrainType(Integer.parseInt(fdm.getValue("stid")), _caller));
            
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return true;
    }
}
