/*
 * StartExportAction.java
 *
 * Created on November 17, 2005, 2:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lami
 */
public class StartExportAction extends TgDbAction {
    
    /** Creates a new instance of StartExportAction */
    public StartExportAction() {
        super();
    }
    
    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "StartExportAction";
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

            
            return true;
        } catch (Exception e) {
            if(e instanceof ApplicationException)
                throw new ApplicationException(e.getMessage());
            else
                e.printStackTrace();
        }
        
        return false;
    }
}
