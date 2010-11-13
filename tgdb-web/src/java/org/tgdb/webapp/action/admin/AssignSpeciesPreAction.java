/*
 * AssignUserPreAction.java
 *
 * Created on January 11, 2006, 9:24 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.admin;
import org.tgdb.frame.ActionException;
import org.tgdb.frame.advanced.Workflow;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 *
 * @author heto
 */
public class AssignSpeciesPreAction extends TgDbAction {
    
    /** Creates a new instance of AssignUserPreAction */
    public AssignSpeciesPreAction() {
    }
    
    public String getName() {
        return "AssignSpeciesPreAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ActionException {
        try {
            String pid = request.getParameter("pid");
            HttpSession session = request.getSession();
            TgDbCaller caller = (TgDbCaller)session.getAttribute("caller");
            
            Workflow w = (Workflow)request.getAttribute("workflow");
            w.setAttribute("pid", pid);
            
            //Collection species = adminManager.getAllSpecies(caller);
            Collection species = adminManager.getOtherSpecies(new Integer(pid).intValue(), caller);
            request.setAttribute("species", species);
            request.setAttribute("pid", pid);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Failed to assign user", e);
        }
    }
}
