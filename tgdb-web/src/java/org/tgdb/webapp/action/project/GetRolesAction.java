/*
 * GetRolesAction.java
 *
 * Created on July 19, 2005, 9:36 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.project;

import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;


/**
 * TgDbAction class for retrieval of roles
 * @author heto
 */
public class GetRolesAction extends TgDbAction {
    
    /** Creates a new instance of GetRolesAction */
    public GetRolesAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of this action
     */
    public String getName() {
        return "GetRolesAction";
    }
    
    /**
     * Performs this action
     * @param request The http request object
     * @param context The servlet context
     * @throws org.tgdb.exceptions.ApplicationException If the actin could not be performed
     * @return True if this action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            
            if (caller.getPid()==0)
                throw new ApplicationException("A project must be selected before roles are viewed for a project");
            
            Collection roles = projectManager.getRolesByProject(caller.getPid(), caller);
            request.setAttribute("roles", roles);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get roles");
        }
    }
}
