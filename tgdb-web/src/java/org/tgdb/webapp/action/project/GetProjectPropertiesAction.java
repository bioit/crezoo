/*
 * GetProjectPropertiesAction.java
 *
 * Created on July 14, 2005, 2:16 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.project;

import org.tgdb.TgDbCaller;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;



/**
 * TgDbAction class for retrieval of the project properties
 * @author heto
 */
public class GetProjectPropertiesAction extends TgDbAction {
    
    /** Creates a new instance of GetProjectPropertiesAction */
    public GetProjectPropertiesAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "GetProjectPropertiesAction";
    }
    
    /**
     * Performs this action
     * @param request The http request object
     * @param context The servlet context
     * @return True if this action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");

            Collection projects = projectManager.getProjectsByUser(caller);
            
            request.setAttribute("projects", projects);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
