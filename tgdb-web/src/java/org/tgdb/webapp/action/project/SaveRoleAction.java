package org.tgdb.webapp.action.project;

import org.tgdb.frame.advanced.Workflow;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.webapp.action.*;

public class SaveRoleAction extends TgDbAction {
    
    public SaveRoleAction() {}
    
    public String getName() {
        return "SaveRoleAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();
            
            String ass[] = request.getParameterValues("ass");
            String other[] = request.getParameterValues("other");
            
            String rid = (String)request.getParameter("rid");
            TgDbCaller _caller = (TgDbCaller)session.getAttribute("caller");
            
            
            if (isSubmit(request, "add")) {
                projectManager.addPrivilegesToRole(other, new Integer(rid).intValue(), _caller);
            } else if (isSubmit(request, "remove")) {
                projectManager.removePrivilegesFromRole(ass, new Integer(rid).intValue(), _caller);
            } else if (isSubmit(request, "submit")) {
                String name = request.getParameter("name");
                String comm = request.getParameter("comm");

                projectManager.updateRole(Integer.parseInt(rid), name, comm, _caller);
            } else if(isSubmit(request, "create")) {
                String name = request.getParameter("name");
                String comm = request.getParameter("comm");
                
                int pid = _caller.getPid();
                
                Workflow w = (Workflow)request.getAttribute("workflow");
                
                if (w.getAttribute("pid")!=null)
                    pid = new Integer((String)w.getAttribute("pid")).intValue();
                
                logger.debug("---------------------------------------->SaveRoleAction#performAction: workflow pid = "+w.getAttribute("pid")+" & request pid = "+request.getParameter("pid"));
                
                int roleID = projectManager.createRole(name, comm, pid, _caller);                
            }
                    
            return true;
        } catch (ApplicationException ae) {
            logger.error("---------------------------------------->SaveRoleAction#performAction: Failed");
            throw ae;
        } catch (Exception e) {
            logger.error("---------------------------------------->SaveRoleAction#performAction: Failed");
            throw new ApplicationException("SaveRoleAction", e);
        }
    }
}
