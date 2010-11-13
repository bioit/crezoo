package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class SaveStrainAction extends TgDbAction {
    
    public SaveStrainAction() {}

    public String getName() {
        return "SaveStrainAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");

            if(isSubmit(req, "create")) {
                String designation = req.getParameter("designation");
                modelManager.createStrain(designation, _caller);
            }
            else if(isSubmit(req, "save")) {
                FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.STRAIN, TgDbFormDataManagerFactory.WEB_FORM, req);
                String designation = req.getParameter("designation");
                modelManager.updateStrain(Integer.parseInt(fdm.getValue("strain_id")), designation, _caller);
            }
            else if(isSubmit(req, "assign")) {
                int strain = Integer.parseInt(req.getParameter("strain"));
                FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, req);
                int eid = Integer.parseInt(fdm.getValue("eid"));
                modelManager.assignStrainToModel(eid, strain, _caller);
            }
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add strains and types.", e);
        }
    }     
}
