package org.tgdb.webapp.action.admin;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class GetProjectUserDbAdminAction extends TgDbAction {
    
    public GetProjectUserDbAdminAction() {}
    
    public String getName() {
        return "GetProjectUserDbAdminAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");
            String tmpId = request.getParameter("id");
            
            if(exists(tmpId)) {
                int id = Integer.parseInt(tmpId);
                request.setAttribute("user", projectManager.getUser(id, _caller));
            }
            
            //Collection roles = projectManager.getRolesByProject(caller.getPid(), caller);
            //request.setAttribute("roles", roles);            
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }    
}