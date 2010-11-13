package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class CreateStrainAllelePreAction extends TgDbAction {
    
    public CreateStrainAllelePreAction() {}

    public String getName() {
        return "CreateStrainAllelePreAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");

            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, req);
            int eid = Integer.parseInt(fdm.getValue("eid"));
            
//            String id = req.getParameter("strainid");
//            workflow.setAttribute("strainid", id);
            
            req.setAttribute("types", modelManager.getMutationTypes(_caller.getPid(), _caller));
            req.setAttribute("alleles_unassigned", modelManager.getUnassignedAlleles(eid, _caller));
//            req.setAttribute("attributes", modelManager.getMutationTypeAttributes());
            
            return true;
        
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not create strains allele", e);
        }
    }     
}
