/*
 * GetProjectStatisticsAction.java
 *
 * Created on July 26, 2005, 4:25 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.project;

import org.tgdb.frame.Navigator;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import org.tgdb.project.projectmanager.ProjectStatisticsDTO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;

/**
 * TgDbAction to get the statistics for a project.
 * 
 * Uses pid (project id) from Navigator object in session
 * 
 * @author heto
 */
public class GetProjectStatisticsAction extends TgDbAction {
    
    /** Creates a new instance of GetProjectStatisticsAction */
    public GetProjectStatisticsAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "GetProjectStatisticsAction";
    }
    
    /**
     * Performs the action
     * @param request The http request object
     * @param context The servlet context
     * @throws org.tgdb.exceptions.ApplicationException If the action could not be performed
     * @return True if this action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            Navigator nav = (Navigator)request.getSession().getAttribute("navigator");
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            
//            ProjectStatisticsDTO stats = projectManager.getStatistics(caller.getPid(), caller);
            
//            request.setAttribute("stats", stats);
            
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            throw new ApplicationException("Failed to get project statistics", e);
        }
    }
}
