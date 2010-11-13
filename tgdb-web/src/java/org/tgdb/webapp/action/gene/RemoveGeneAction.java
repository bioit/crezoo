package org.tgdb.webapp.action.gene;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class RemoveGeneAction extends TgDbAction {
    
    public String getName() {
        return "RemoveGeneAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");

            if(exists(request.getParameter("gaid"))) {
                int gaid = Integer.parseInt(request.getParameter("gaid"));
                modelManager.removeGene(gaid, _caller);
            }
            else {
                throw new ApplicationException("Missing required request parameter (gaid)");
            }
//            else if (request.getParameter("yes")!=null) {
//                Workflow wf = (Workflow)request.getAttribute("workflow");
//                int gaid = new Integer(wf.getAttribute("gaid")).intValue();
//                modelManager.removeGene(gaid, _caller);
//            }
            
            return true;
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }
}
