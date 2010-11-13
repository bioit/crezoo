package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class SaveMutationTypeAction extends TgDbAction {
    
    public SaveMutationTypeAction() {}

    public String getName() {
        return "SaveMutationTypeAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");   

            if(isSubmit(req, "save")) {
                FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.MUTATION_TYPE, TgDbFormDataManagerFactory.WEB_FORM, req);
                String name = req.getParameter("name");
                String abbreviation = req.getParameter("abbreviation");

                modelManager.updateMutationType(Integer.parseInt(fdm.getValue("mtid")), name, abbreviation, _caller);
            }
            else if(isSubmit(req, "create")) {
                String name = req.getParameter("name");
                String abbreviation = req.getParameter("abbreviation");

                modelManager.createMutationType(name, abbreviation, _caller);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not get mutation/allele type", e);
        }
    }     
}
