package org.tgdb.webapp.action.model;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.form.FormDataManager;
import org.tgdb.webapp.action.TgDbAction;

public class UnassignStrainAction extends TgDbAction {

    public UnassignStrainAction() {}

    public String getName() {
        return "UnassignStrainAction";
    }

    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");

            if(exists(req.getParameter("strainid"))) {
                FormDataManager formDataManager = getFormDataManager(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, req);
                int eid = Integer.parseInt(formDataManager.getValue("eid"));
                int strain = Integer.parseInt(req.getParameter("strainid"));
                modelManager.unassignStrainFromModel(eid, strain, _caller);
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
