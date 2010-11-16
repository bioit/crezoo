package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class SavePromoterLinkAction extends TgDbAction {
    
    public SavePromoterLinkAction() {}

    public String getName() {
        return "SavePromoterLinkAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");

            if(isSubmit(req, "create")) {
                FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.GENE, TgDbFormDataManagerFactory.WEB_FORM, req);
                int gaid = Integer.parseInt(fdm.getValue("gaid"));
                String repository = req.getParameter("repository");
                String externalid = req.getParameter("externalid");
                String strainurl = req.getParameter("strainurl");
                modelManager.createPromoterLink(gaid, repository, externalid, strainurl, _caller);
            }
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add promoter.", e);
        }
    }     
}
