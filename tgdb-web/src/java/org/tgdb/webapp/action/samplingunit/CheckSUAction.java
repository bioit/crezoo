/*
 *
 * Created on August 2, 2005, 3:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.samplingunit;

/**
 *
 * @author lami
 */

import org.tgdb.frame.ActionException;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;

/**
 * TgDbAction class for checking the values for a sampling unit
 */
public class CheckSUAction extends TgDbAction {
    
    /** Creates a new instance of CheckSUAction */
    public CheckSUAction() {
    }
    
    /**
     * Returns the name of the action
     * @return The name of the action
     */
    public String getName() {
        return "CheckSUAction";
    }
    
    /**
     * Performs the action
     * @param request The http request
     * @param context The servlet context
     * @throws org.tgdb.exceptions.ApplicationException If the action could not be performed
     * @return True, if the action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ActionException {
        try {
            // Get the caller
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");    
            int suid = Integer.parseInt(request.getParameter("suid"));
            //request.setAttribute("sucheckdto", checked);
            //request.setAttribute("indsOK", ""+(numInds-checked.size()));
            //request.setAttribute("indsERR", ""+checked.size());
            return true;
      
        } catch (Exception e) {
            throw new ApplicationException("Checking sampling unit failed.");
        }
    } 
}
