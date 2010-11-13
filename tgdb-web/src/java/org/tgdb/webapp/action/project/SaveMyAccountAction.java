/*
 * SaveMyAccountAction.java
 *
 * Created on July 29, 2005, 9:46 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.project;

import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;


/**
 * This action changes the password of the user
 * @author heto
 */
public class SaveMyAccountAction extends TgDbAction {
    
    /** Creates a new instance of SaveMyAccountAction */
    public SaveMyAccountAction() {
    }
    
    /**
     * The name of the action
     * @return string of action name
     */
    public String getName() {
        return "SaveMyAccountAction";
    }
    
    /**
     * Perform the action of setting password
     * @param request the request object from client
     * @param context The servlet context
     * @throws org.tgdb.exceptions.ApplicationException If the action could not be performed
     * @return True if the action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            
            String old = request.getParameter("old");
            String p1 = request.getParameter("p1");
            String p2 = request.getParameter("p2");
            
            projectManager.setPassword(old, p1, p2, caller);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to change password");
        }
    }
}
