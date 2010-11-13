package org.tgdb.webapp.action.model;

import org.tgdb.frame.advanced.Workflow;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class EditGeneticBackgroundPreAction extends TgDbAction {
    
    public EditGeneticBackgroundPreAction() {}

    public String getName() {
        return "EditGeneticBackgroundPreAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            Workflow wf = (Workflow)req.getAttribute("workflow");
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            Collection genbackValues = modelManager.getGeneticBackgroundsByProject(_caller.getPid(), _caller);
            
            int eid = new Integer(req.getParameter("eid")).intValue();

            req.setAttribute("genBacks", genbackValues);
            req.setAttribute("modeldto", modelManager.getExperimentalModel(eid, _caller));
            req.setAttribute("genbackdto", modelManager.getGeneticBackgroundDTO(eid,_caller));
            req.setAttribute("backcrosseCollection", modelManager.getBackcrossesCollection());
            
            wf.setAttribute("eid",req.getParameter("eid"));
                
            return true;
        } catch (ApplicationException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("EditGeneticBackgroundPreAction Failed to perform action.");
        }
    }     
}
