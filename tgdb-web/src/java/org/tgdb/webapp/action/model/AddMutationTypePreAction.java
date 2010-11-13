package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class AddMutationTypePreAction extends TgDbAction {
    
    public AddMutationTypePreAction() {}

    public String getName() {
        return "AddMutationTypePreAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            
            int id = new Integer(req.getParameter("strainalleleid")).intValue();
            workflow.setAttribute("strainalleleid", req.getParameter("strainalleleid"));
           
            //req.setAttribute("types", modelManager.getMutationTypesFromStrainAllele(id, caller));
            //--------------DUDE_TEST_STRAIN_ALLELE
            //req.setAttribute("types", modelManager.getMutationTypes(caller.getPid(), caller));
            req.setAttribute("types", modelManager.getUnassignedMutationTypes(id, _caller));
            
            String eid = "";
            eid = (String)se.getAttribute("eid");
            se.setAttribute("eid", eid);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("AddMutationTypePreAction failed to perform action.", e);
        }
    }     
}
