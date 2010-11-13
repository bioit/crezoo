package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class AssignGeneAction extends TgDbAction {
    
    public String getName() {
        return "AssignGeneAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");
            
            if (exists(request.getParameter("gene"))) {
                int gaid = Integer.parseInt(request.getParameter("gene"));

                FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, request);
                int eid = Integer.parseInt(fdm.getValue("eid"));
            
                modelManager.addGeneToModel(gaid, eid, _caller);
            }
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Failed to perform action", e);
        }
    }
}
