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
public class UnAssignUserPreAction extends TgDbAction {
    
    /** Creates a new instance of AssignUserPreAction */
    public UnAssignUserPreAction() {
    }
    
    public String getName() {
        return "UnAssignUserPreAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ActionException {
        try {
            
            HttpSession session = request.getSession();
            TgDbCaller caller = (TgDbCaller)session.getAttribute("caller");
            
            Workflow w = (Workflow)request.getAttribute("workflow");
            
            String id = request.getParameter("id");
            String pid = request.getParameter("pid");
            
            if (id==null||id.equals(""))
                id = w.getAttribute("id");
            else
                w.setAttribute("id", id);
                
            if (pid==null||pid.equals(""))
                pid = w.getAttribute("pid");
            else
                w.setAttribute("pid", pid);
            
                        
            return true;
       
        } catch (Exception e) {
            throw new ApplicationException("Failed to assign user", e);
        }
    }
}
