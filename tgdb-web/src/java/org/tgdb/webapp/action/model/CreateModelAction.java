package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class CreateModelAction extends TgDbAction {
    
    public CreateModelAction() {}

    public String getName() {
        return "CreateModelAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
                       
//            req.setAttribute("users", projectManager.getProjectUsers(_caller.getPid(), _caller));
            req.setAttribute("users", adminManager.getCorrespondents("correspondent", _caller));
            
            req.setAttribute("samplingunits", samplingUnitManager.getSamplingUnits(_caller.getPid(), _caller));
            req.setAttribute("researchApps", modelManager.getResearchApplications(_caller));
            req.setAttribute("userid", new Integer(_caller.getId()).toString());
            req.setAttribute("desired_levels", modelManager.getLevelsForModel());
            req.setAttribute("inducibles", modelManager.getInducibleValues());
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not retrieve users.");
        }
    }      
}
