/*
 * CreateSamplingUnitAction.java
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

import org.tgdb.frame.advanced.Workflow;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;

/**
 * TgDbAction class for creation of sampling units.
 * This action class can either get the project id from the caller object
 * or as a parameter "pid" in query string.
 * @author lami
 */
public class CreateSamplingUnitAction extends TgDbAction {
    
    /** Creates a new instance of CreateSamplingUnitAction */
    public CreateSamplingUnitAction() {
    }
    
    /**
     * Returns the name of the action
     * @return The name of the action
     */
    public String getName() {
        return "CreateSamplingUnitAction";
    }
    
    /**
     * Performs the action
     * @param request The http request
     * @param context The servlet context
     * @throws org.tgdb.exceptions.ApplicationException If the action could not be performed
     * @return True, if the action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            // Get the caller
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller"); 
            
            int pid = caller.getPid();
            if (request.getParameter("pid")!=null)
            {
                pid = new Integer(request.getParameter("pid")).intValue();
            }
            Workflow wf = (Workflow)request.getAttribute("workflow");
            wf.setAttribute("pid", new Integer(pid).toString());

            Collection species = samplingUnitManager.getSpeciesForProject(pid, caller);
            // Get all species so we can use it to fill up the combobox...
            request.setAttribute("speciesInterfaces", species);
            request.setAttribute("pid", ""+pid);
                
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Create sampling unit failed.", e);
        }
    } 
}
