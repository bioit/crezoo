/*
 * CreateFilterAction.java
 *
 * Created on November 17, 2005, 8:29 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action;

import org.tgdb.frame.Navigator;
import org.tgdb.TgDbCaller;
import org.tgdb.export.GQLFilterDTO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author lami
 */
public class CreateFilterAction extends TgDbAction {
    
    /** Creates a new instance of CreateFilterAction */
    public CreateFilterAction() {
        super();
    }
    
    /**
     * Returns the name of this action
     * @return The name of this action
     */
    public String getName() {
        return "CreateFilterAction";
    }
    
    /**
     * Performs the action
     * @param req The http request object
     * @param context The servlet context
     * @return True if the action could be performed
     */
    public boolean performAction(HttpServletRequest req, ServletContext context) {
        try {
            TgDbCaller caller = (TgDbCaller)req.getSession().getAttribute("caller");
            String status = "";
            String result = "";            
            Navigator nav = (Navigator)req.getSession().getAttribute("navigator");
            String mess = (String)req.getAttribute("mess");
            if(mess == null)
                mess = "";

            req.setAttribute("sid", ""+caller.getSid());
            req.setAttribute("species", samplingUnitManager.getSpeciesForProject(caller.getPid(), caller));
            req.setAttribute("samplingunits", samplingUnitManager.getSamplingUnits(caller.getPid(), caller.getSid(), caller));
            req.setAttribute("status", status);
            req.setAttribute("result", result);
            
            req.setAttribute("mess", mess);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }  
}
