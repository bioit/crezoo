/*
 * GetUsersAction.java
 *
 * Created on July 29, 2005, 3:41 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.admin;

import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;


/**
 * TgDbAction class for retrieving info about projects and users
 * @author heto
 */
public class GetUsersAction extends TgDbAction {
    
    /**
     * Creates a new instance of GetUsersAction 
     */
    public GetUsersAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "GetUsersAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            int pid = caller.getPid();            
            
            Collection users = adminManager.getUsers(caller);
            request.setAttribute("users", users);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
