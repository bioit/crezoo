/*
 * GetModelsForIMSRAction.java
 *
 * Created on November 6, 2006, 8:50 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.model;

import org.tgdb.frame.Navigator;
import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author zouberakis
 */
public class GetModelsForIMSRAction extends TgDbAction {
    
    /** Creates a new instance of GetModelsForIMSRAction */
    public GetModelsForIMSRAction() {
    }

    /**
     * Returns the name of this action
     * @return The action name
     */
    public String getName() {
        return "GetModelsForIMSRAction";
    }
    
    /**
     * Peroforms the action
     * @param req The http request object
     * @param context The servlet context
     * @return True of this action could be performed
     */
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller caller = (TgDbCaller)se.getAttribute("caller");            
            
            Collection models = modelManager.getExperimentalModelsForIMSR(caller.getSuid(), caller);
            
            Collection models2 = modelManager.getExperimentalModelsToIMSRTable(models, caller.getSuid());
            
            req.setAttribute("modelsdto", models2);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not retrieve models.");
        }
    }     
}
