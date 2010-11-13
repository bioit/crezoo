/*
 * RemoveAvailableGeneticBackgroundAction.java
 *
 * Created on July 23, 2006, 2:46 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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
 * @author zouberakis
 */
public class RemoveAvailableGeneticBackgroundAction extends TgDbAction {
    
    /** Creates a new instance of RemoveAvailableGeneticBackgroundAction */
    public RemoveAvailableGeneticBackgroundAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "RemoveAvailableGeneticBackgroundAction";
    }
    
    /**
     * Performs the action
     * @param request The http request object
     * @param context The servlet context
     * @throws org.tgdb.exceptions.ApplicationException If the action could not be performed
     * @return True if the action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();
            TgDbCaller caller = (TgDbCaller)session.getAttribute("caller");
            
            int aid = new Integer(request.getParameter("aid")).intValue();
            modelManager.removeAvailableGeneticBackground(aid, caller);
            ViewAvailableGeneticBackgroundsAction avgenbackaction = new ViewAvailableGeneticBackgroundsAction();
            avgenbackaction.performAction(request, context);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof ApplicationException)
                throw new ApplicationException(e.getMessage());                
        }
        
        return false; 
    }    
}
