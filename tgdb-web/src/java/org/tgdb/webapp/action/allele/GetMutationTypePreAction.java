package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class GetMutationTypePreAction extends TgDbAction {
    
    public GetMutationTypePreAction() {}

    public String getName() {
        return "GetMutationTypePreAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");   
            
            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.MUTATION_TYPE, TgDbFormDataManagerFactory.WEB_FORM, req);

            if(exists(req.getParameter("mtid"))) {
              fdm.put("mtid", req.getParameter("mtid"));
              //FIXME!!! - Add a nice generic method in TgDbAction class to refresh the formdatamanager
              se.setAttribute(TgDbFormDataManagerFactory.getInstanceName(TgDbFormDataManagerFactory.MUTATION_TYPE), fdm);
            }

            req.setAttribute("mutation_type", modelManager.getMutationType(Integer.parseInt(fdm.getValue("mtid")), _caller));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not get mutation/allele type", e);
        }
    }     
}
