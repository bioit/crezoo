/*
 * GetIntegrationCopyAction.java
 *
 * Created on December 20, 2005, 1:25 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.integrationcopy;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GetIntegrationCopyAction extends TgDbAction {
    
    /**
     * Creates a new instance of GetIntegrationCopyAction
     */
    public GetIntegrationCopyAction() {
    }

    /**
     * Returns the name of this action
     * @return The action name
     */
    public String getName() {
        return "GetIntegrationCopyAction";
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
            
            int iscmid = Integer.parseInt(req.getParameter("iscmid")); 
            
            req.setAttribute("integrationcopy", modelManager.getIntegrationCopy(iscmid, caller));     

            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not retrieve model.", e);
        }
    }     
}
