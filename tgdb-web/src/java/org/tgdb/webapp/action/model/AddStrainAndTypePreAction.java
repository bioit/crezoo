/*
 * GetStrainTypesAction.java
 *
 * Created on July 10, 2006, 3:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author heto
 */
public class AddStrainAndTypePreAction extends TgDbAction
{
    
    /** Creates a new instance of GetStrainTypesAction */
    public AddStrainAndTypePreAction()
    {
    }

    /**
     * Returns the name of this action
     * @return The action name
     */
    public String getName() {
        return "AddStrainAndTypePreAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller caller = (TgDbCaller)se.getAttribute("caller");                                                              
            
            int id = new Integer(req.getParameter("strainid")).intValue();
            workflow.setAttribute("strainid", req.getParameter("strainid"));
            
            
            //req.setAttribute("states", modelManager.getStrainStatesForStrain(id, caller));
            //req.setAttribute("types", modelManager.getStrainTypesForStrain(id, caller));
            
            req.setAttribute("availablestates", modelManager.getAvailableStrainStatesForStrain(id,caller));
            req.setAttribute("availabletypes", modelManager.getAvailableStrainTypesForStrain(id,caller));
            
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not retrieve model.", e);
        }
    }     
}
