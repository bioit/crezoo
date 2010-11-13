package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class AddMutationTypeAction extends TgDbAction {
    
    public AddMutationTypeAction() {}

    public String getName() {
        return "AddMutationTypeAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");

            String eid = "";
            eid = (String)se.getAttribute("eid");
            se.setAttribute("eid", eid);
            
            int id = new Integer(workflow.getAttribute("strainalleleid")).intValue();
            String type = req.getParameter("type");

            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.ALLELE, TgDbFormDataManagerFactory.WEB_FORM, req);
            String attributes = fdm.getValue("attributes");

            modelManager.addMutationTypeAndAttributeToStrainAllele(Integer.parseInt(eid), id, Integer.parseInt(type), attributes, _caller);
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not retrieve model.", e);
        }
    }     
}
