package org.tgdb.webapp.action.admin;

import org.tgdb.frame.ActionException;
import org.tgdb.frame.advanced.Workflow;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class AssignUserAction extends TgDbAction {
    
    public AssignUserAction() {}
    
    public String getName() {
        return "AssignUserAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ActionException {
        try {
            HttpSession session = request.getSession();
            TgDbCaller _caller = (TgDbCaller)session.getAttribute("caller");
            
            Workflow w = (Workflow)request.getAttribute("workflow");
            String pid = w.getAttribute("pid");
            
            String rid = request.getParameter("role");
            
            String id = null;
            if (request.getParameter("user")!=null)
                id = request.getParameter("user");
            if (w.getAttribute("id")!=null)
                id = w.getAttribute("id");
            
            projectManager.assignUserToProject(new Integer(id).intValue(), 
                    new Integer(rid).intValue(),
                    new Integer(pid).intValue(), _caller);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Failed to assign user", e);
        }
    }
}
