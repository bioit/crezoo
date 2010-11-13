/*
 * SaveLinkResourceAction.java
 *
 * Created on December 20, 2005, 12:25 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.resource;

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
public class SaveModelLinkResourceAction extends TgDbAction {
    
    /** Creates a new instance of SaveLinkResourceAction */
    public SaveModelLinkResourceAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "SaveLinkResourceAction";
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
            
            FormDataManager formDataManager = getFormDataManager(
                    TgDbFormDataManagerFactory.EXPMODEL, 
                    TgDbFormDataManagerFactory.WEB_FORM, 
                    request);             
            
            String eid = formDataManager.getValue("eid");
            
            if (isSubmit(request, "create")) {
                String name = request.getParameter("name");
                String comm = request.getParameter("comm");
                String url = request.getParameter("url");
                int catid = new Integer(request.getParameter("catid")).intValue();
                
                modelManager.addLinkResource(Integer.parseInt(eid), name, comm, url, catid, caller);
            } else if (isSubmit(request, "save")) {
                String name = request.getParameter("name");
                String comm = request.getParameter("comm");
                String url = request.getParameter("url");                

                resourceManager.updateLink(Integer.parseInt(formDataManager.getValue("linkid")), name, url, comm, caller);
            } 

            return true;
        } catch (Exception e) {
            if(e instanceof ApplicationException)
                throw new ApplicationException(e.getMessage());
            else
                e.printStackTrace();
        }
        
        return false;
    }      
}
