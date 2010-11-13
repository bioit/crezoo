/*
 * RemoveResourceCategoryAction.java
 *
 * Created on January 19, 2006, 8:59 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.admin;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lami
 */
public class RemoveResourceCategoryAction extends TgDbAction {
    
    /** Creates a new instance of RemoveResourceCategoryAction */
    public RemoveResourceCategoryAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "RemoveResourceCategoryAction";
    }
    
    /**
     * Performs the action
     * @param request The http request object
     * @param context The servlet context
     * @throws org.tgdb.exceptions.ApplicationException If the action could not be performed
     * @return True if the action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();
            TgDbCaller caller = (TgDbCaller)session.getAttribute("caller");                        

            String pid = request.getParameter("pid");
            String catId = request.getParameter("catId");
            resourceManager.removeResourceCategory(Integer.parseInt(catId), caller);
            request.setAttribute("pid", pid); 

            GetProjectAction project = new GetProjectAction();
            project.performAction(request, context);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof ApplicationException)
                throw new ApplicationException(e.getMessage());                
        }
        
        return false;
    }     
}
