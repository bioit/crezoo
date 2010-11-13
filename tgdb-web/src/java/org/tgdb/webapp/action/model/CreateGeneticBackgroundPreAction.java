package org.tgdb.webapp.action.model;

import org.tgdb.frame.advanced.Workflow;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class CreateGeneticBackgroundPreAction extends TgDbAction {
    
    public String getName() {
        return "CreateGeneticBackgroundPreAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            Workflow wf = (Workflow)request.getAttribute("workflow");
            Collection genbackValues = modelManager.getGeneticBackgroundsByProject(caller.getPid(), caller);
            request.setAttribute("genBacks", genbackValues);
            request.setAttribute("backcrosseCollection", modelManager.getBackcrossesCollection());
            
            //if gen. back. returns from gen. back. value creation there will be no parameter...
            String eid = "";
            if(request.getParameter("eid")!=null){
                eid = request.getParameter("eid");
            }
            else {
                //...but a workflow attribute instead.
                eid = wf.getAttribute("eid");
            }
            
            
            
            wf.setAttribute("eid", eid);
            request.setAttribute("eid", eid);

            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("CreateGeneticBackgroundPreAction Failed to perform action", e);
        }
    }
}
