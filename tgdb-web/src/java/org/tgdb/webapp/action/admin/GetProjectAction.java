/*
 * GetUsersAction.java
 *
 * Created on July 29, 2005, 3:41 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.admin;

import org.tgdb.frame.advanced.Workflow;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import org.tgdb.project.projectmanager.ProjectDTO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;
import java.util.Collection;
import javax.servlet.http.HttpSession;


/**
 * TgDbAction class for retrieving info about projects and users
 * @author heto
 */
public class GetProjectAction extends TgDbAction {
    
    /**
     * Creates a new instance of GetUsersAction 
     */
    public GetProjectAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "GetProjectAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            Workflow wf = (Workflow)request.getAttribute("workflow");
            HttpSession session = request.getSession();
            int pid = 0;
            if (request.getParameter("pid")!=null)
            {
                pid = new Integer(request.getParameter("pid")).intValue();
                request.setAttribute("pid", new Integer(pid));
                session.setAttribute("admin.pid", new Integer(pid));
            }
            else if (request.getAttribute("pid")!=null)
            {
                Integer tmp = new Integer((String)request.getAttribute("pid"));
                if (tmp!=null)
                    pid = tmp.intValue();
            }
            else if (wf.getAttribute("pid")!=null)
            {
                Integer tmp = new Integer(wf.getAttribute("pid"));
                if (tmp!=null)
                    pid = tmp.intValue();
            }
            else if (session.getAttribute("admin.pid")!=null)
            {
                Integer tmp = (Integer)session.getAttribute("admin.pid");
                if (tmp!=null)
                    pid = tmp.intValue();
            }    
            else
                throw new ApplicationException("Project id is missing");
            
            ProjectDTO prj = projectManager.getProject(pid, caller);
            Collection users = projectManager.getProjectUsers(prj.getPid(), caller);
            Collection roles = projectManager.getRolesByProject(pid, caller);
            Collection categories = resourceManager.getResourceCategories(pid, caller);
            Collection species = adminManager.getSpeciesForProject(pid, caller);
            Collection sus = samplingUnitManager.getSamplingUnits(pid, caller);
            
            request.setAttribute("project", prj);
            request.setAttribute("users", users);
            request.setAttribute("roles", roles);
            request.setAttribute("species", species);
            request.setAttribute("sus", sus);
            request.setAttribute("categories", categories);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
