/*
 * PreConfirmRemoveAction.java
 *
 * Created on October 17, 2005, 9:50 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.gene;

import org.tgdb.frame.advanced.Workflow;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author heto
 */
public class PreConfirmRemoveAction extends TgDbAction {
    
    /** Creates a new instance of PreConfirmRemoveAction */
    public PreConfirmRemoveAction() {
    }
    
    public String getName() {
        return "PreConfirmRemoveAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            // Do action code here:
            request.setAttribute("description", new String("Are you sure you want to remove this gene?"));
            
            Workflow wf = (Workflow)request.getAttribute("workflow");
            wf.setAttribute("gaid", request.getParameter("gaid"));
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
