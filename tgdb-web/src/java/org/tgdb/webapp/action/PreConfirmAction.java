/*
 * PreConfirmAction.java
 *
 * Created on July 28, 2005, 4:37 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action;

import org.tgdb.exceptions.ApplicationException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


/**
 * TgDbAction class for confirmation of operations
 * @author heto
 */
public class PreConfirmAction extends TgDbAction {
    
    /** Creates a new instance of PreConfirmAction */
    public PreConfirmAction() {
    }
    
    /**
     * Returns the name of the action
     * @return The name of the action
     */
    public String getName() {
        return "PreConfirmAction";
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
            // Do action code here:
            request.setAttribute("description", new String("Are you sure you want to remove the object?"));
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
