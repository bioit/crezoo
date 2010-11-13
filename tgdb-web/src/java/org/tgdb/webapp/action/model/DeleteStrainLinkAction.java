package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class DeleteStrainLinkAction extends TgDbAction {
    
    public DeleteStrainLinkAction() {}

    public String getName() {
        return "DeleteStrainLinkAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.STRAIN, TgDbFormDataManagerFactory.WEB_FORM, req);

            if(exists(req.getParameter("slid")) && exists(fdm.getValue("strain_id"))) {
                int strainid = Integer.parseInt(fdm.getValue("strain_id"));
                int strain_link_id = Integer.parseInt(req.getParameter("slid"));
                modelManager.deleteStrainLink(strainid, strain_link_id, _caller);
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
