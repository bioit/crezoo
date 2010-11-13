/*
 * PreConfirmRemoveAction.java
 *
 * Created on October 17, 2005, 9:50 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action;

import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
            request.setAttribute("description", new String("Are you sure you want to remove the object?"));
            request.getSession().setAttribute("tmp.preconfirmremoveaction.fileid", request.getParameter("fileid"));
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
