/*
 * SaveProjectUsersAction.java
 *
 * Created on July 29, 2005, 3:41 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.project;

import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.NotImplementedException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;


/**
 * TgDbAction for saving information about projects and users
 * @author heto
 */
public class SaveProjectUsersAction extends TgDbAction {
    
    /** Creates a new instance of SaveProjectUsersAction */
    public SaveProjectUsersAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of this action
     */
    public String getName() {
        return "SaveProjectUsersAction";
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
            if (1==1)
                throw new NotImplementedException("The function of saving is not implemented in SaveProjectUsersAction");
            
            return true;
        } 
        catch (ApplicationException e) {
            throw e;
        }
          
         catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
