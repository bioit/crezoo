/*
 * GetProjectUserAction.java
 *
 * Created on December 13, 2005, 10:18 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.project;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.project.projectmanager.ProjectDTO;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author lami
 */
public class GetProjectUserAction extends TgDbAction {
    
    /** Creates a new instance of GetProjectUserAction */
    public GetProjectUserAction() {
    }
    
    
    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "GetProjectUserAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            String tmpId = request.getParameter("id");
            
            if(exists(tmpId)) {
                int id = Integer.parseInt(tmpId);
                request.setAttribute("user", projectManager.getUser(id, caller));
            }
            
            //Collection roles = projectManager.getRolesByProject(caller.getPid(), caller);
            //request.setAttribute("roles", roles);   
            
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
