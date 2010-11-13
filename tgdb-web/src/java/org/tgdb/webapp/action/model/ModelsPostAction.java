/*
 * ModelsPostAction.java
 *
 * Created on December 21, 2005, 10:32 AM
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
 * @author lami
 */
public class ModelsPostAction extends TgDbAction {
    
    /** Creates a new instance of ModelsPostAction */
    public ModelsPostAction() {
    }

    /**
     * Returns the name of this action
     * @return The action name
     */
    public String getName() {
        return "ModelsPostAction";
    }
    
    /**
     * Performs the action
     * @param req The http request object
     * @param context The servlet context
     * @return True of this action could be performed
     */
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller caller = (TgDbCaller)se.getAttribute("caller");            
            String suid = req.getParameter("suid");
            if(exists(suid))
                caller.setSuid(Integer.parseInt(suid));
            
            if(isSubmit(req, "reset")) {                
                resetFormData(TgDbFormDataManagerFactory.EXPMODELS, req);
            }
            else{
                collectFormData(TgDbFormDataManagerFactory.EXPMODELS, TgDbFormDataManagerFactory.WEB_FORM, req);
                //once all data is collected get the FDM again
                FormDataManager formDataManager = getFormDataManager(
                    TgDbFormDataManagerFactory.EXPMODELS, 
                    TgDbFormDataManagerFactory.WEB_FORM, 
                    req);
                
                if(isSubmit(req, "byID"))
                    formDataManager.put("ordertype", "MMMDb ID");
                
                if(isSubmit(req, "byNAME"))
                    formDataManager.put("ordertype", "LINE NAME");
                
                if(isSubmit(req, "byDATE"))
                    formDataManager.put("ordertype", "DATE");
            }
                
            
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed in models post action.");
        }
    }      
}
