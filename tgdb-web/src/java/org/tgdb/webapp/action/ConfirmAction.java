/*
 * ConfirmAction.java
 *
 * Created on July 28, 2005, 3:26 PM
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
 * TgDbAction class for confirmation questions
 * @author heto
 */
public class ConfirmAction extends TgDbAction {
    
    /** Creates a new instance of ConfirmAction */
    public ConfirmAction() {
    }
    
    /**
     * Returns the name of the action
     * @return The name of the action
     */
    public String getName() {
        return "ConfirmAction";
    }
    
    /**
     * Performs the action
     * @param request The http request
     * @param context The servlet context
     * @throws org.tgdb.exceptions.ApplicationException If the action could not be performed
     * @return True, if the action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            if (request.getParameter("yes")!=null)
                return true;
            else
                throw new ApplicationException("Operation was aborted");
            
            
            
        } 
        catch (ApplicationException e)
        {
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
