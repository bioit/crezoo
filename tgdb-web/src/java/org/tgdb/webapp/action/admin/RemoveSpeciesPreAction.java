/*
 * RemoveSpeciesPreAction.java
 *
 * Created on January 17, 2006, 8:39 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.admin;

import org.tgdb.frame.advanced.Workflow;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author heto
 */
public class RemoveSpeciesPreAction extends TgDbAction {
    
    public String getName() {
        return "RemoveSpeciesPreAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            Workflow wf = (Workflow)request.getAttribute("workflow");
            String sid = request.getParameter("sid");
            wf.setAttribute("sid", sid);
            
            request.setAttribute("description", "Are you sure you want to delete this species?");
            
            return true;
        } catch (Exception e) {
            throw new ApplicationException("Failed to perform action", e);
        }
    }
}
