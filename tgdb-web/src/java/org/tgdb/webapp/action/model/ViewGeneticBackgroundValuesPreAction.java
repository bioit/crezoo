package org.tgdb.webapp.action.model;

import org.tgdb.frame.advanced.Workflow;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class ViewGeneticBackgroundValuesPreAction extends TgDbAction {
    
    public String getName() {
        return "ViewGeneticBackgroundValuesPreAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            Collection genbackValues = modelManager.getGeneticBackgroundsByProject(caller.getPid(), caller);
            request.setAttribute("genBacks", genbackValues);
            
            //String eid = request.getParameter("eid");
            
            Workflow wf = (Workflow)request.getAttribute("workflow");
            //wf.setAttribute("eid", eid);
            //request.setAttribute("eid", eid);

            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Failed to perform action", e);
        }
    }
}
