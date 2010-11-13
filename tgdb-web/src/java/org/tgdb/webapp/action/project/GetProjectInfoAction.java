/*
 * GetUsersAction.java
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
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;
import java.util.Collection;


/**
 * TgDbAction class for retrieving info about projects and users
 * @author heto
 */
public class GetProjectInfoAction extends TgDbAction {
    
    /**
     * Creates a new instance of GetProjectInfoAction 
     */
    public GetProjectInfoAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "GetProjectInfoAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            int pid = caller.getPid();
            if (pid == 0)
                throw new ApplicationException("Project id is missing");
            
            ProjectDTO prj = projectManager.getProject(pid, caller);
            Collection users = projectManager.getProjectUsers(prj.getPid(), caller);
            Collection species = adminManager.getSpeciesForProject(pid, caller);
            Collection categoriesAndResources = projectManager.getCategoriesAndResources(pid, caller);
            request.setAttribute("project", prj);
            request.setAttribute("pid", ""+prj.getPid());
            request.setAttribute("users", users);
            request.setAttribute("species", species);
            request.setAttribute("categoriesAndResources", categoriesAndResources);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
