package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class AddGeneAction extends TgDbAction {
    
    public AddGeneAction() {}

    public String getName() {
        return "AddGeneAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");

            if(isSubmit(req, "save") && exists(req.getParameter("promoter"))) {
                FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.ALLELE, TgDbFormDataManagerFactory.WEB_FORM, req);
                modelManager.addGeneToStrainAllele(Integer.parseInt(fdm.getValue("aid")), Integer.parseInt(req.getParameter("promoter")), _caller);
            }
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not retrieve model.", e);
        }
    }     
}
