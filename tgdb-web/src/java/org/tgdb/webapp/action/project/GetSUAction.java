/*
 * GetSUAction.java
 *
 * Created on July 14, 2005, 4:43 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.project;

import org.tgdb.frame.Navigator;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;



/**
 * TgDbAction class for the retrieval of sampling units
 * @author heto
 */
public class GetSUAction extends TgDbAction {
    
    /** Creates a new instance of GetSUAction */
    public GetSUAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "GetSUAction";
    }
    
    /**
     * Performs this action
     * @param request The http request object
     * @param context The servlet context
     * @return True if this action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            
            Navigator nav = (Navigator)request.getSession().getAttribute("navigator");
            
            Collection samplingunits = samplingUnitManager.getSamplingUnits(caller.getPid(), caller);
            
            if (samplingunits==null)
                throw new Exception("Samplingunits obj null");
            
            request.setAttribute("samplingunits", samplingunits);
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not retrieve sampling units.");
        }
    }
}
