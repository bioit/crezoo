/*
 * GetProjectUsersAction.java
 *
 * Created on July 29, 2005, 3:41 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.project;

import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import org.tgdb.project.projectmanager.ProjectDTO;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;


/**
 * TgDbAction class for retrieving info about projects and users
 * @author heto
 */
public class GetProjectUsersAction extends TgDbAction {
    
    /** Creates a new instance of GetProjectUsersAction */
    public GetProjectUsersAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "GetProjectUsersAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            int pid = caller.getPid();
            
            String sort = null;
            if (request.getParameter("sort")!=null)
            {
                sort = request.getParameter("sort");
                caller.setAttribute("sort", sort);
            }
            
            Collection users = projectManager.getProjectUsers(pid, caller);
            request.setAttribute("users", users);
            
            ProjectDTO project = projectManager.getProject(caller.getPid(), caller);
            request.setAttribute("project", project);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
