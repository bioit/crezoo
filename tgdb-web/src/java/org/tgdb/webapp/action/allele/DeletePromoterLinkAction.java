package org.tgdb.webapp.action.allele;

import org.tgdb.webapp.action.model.*;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class DeletePromoterLinkAction extends TgDbAction {
    
    public DeletePromoterLinkAction() {}

    public String getName() {
        return "DeletePromoterLinkAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.GENE, TgDbFormDataManagerFactory.WEB_FORM, req);

            if(exists(req.getParameter("plid")) && exists(fdm.getValue("gaid"))) {
                int gaid = Integer.parseInt(fdm.getValue("gaid"));
                int promoter_link_id = Integer.parseInt(req.getParameter("plid"));
                modelManager.deletePromoterLink(gaid, promoter_link_id, _caller);
            }
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not delete promoter link.", e);
        }
    }     
}
