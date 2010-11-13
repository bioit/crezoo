/*
 * GetStrainTypesAction.java
 *
 * Created on July 10, 2006, 3:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tgdb.webapp.action.model;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author heto
 */
public class GetStrainStatesAction extends TgDbAction
{
    
    /** Creates a new instance of GetStrainTypesAction */
    public GetStrainStatesAction()
    {
    }

    /**
     * Returns the name of this action
     * @return The action name
     */
    public String getName() {
        return "GetStrainStatesAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller caller = (TgDbCaller)se.getAttribute("caller");                                                              
            
            
            req.setAttribute("strainstates", modelManager.getStrainStates(caller));
            
            
            
            
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not retrieve model.", e);
        }
    }     
}
