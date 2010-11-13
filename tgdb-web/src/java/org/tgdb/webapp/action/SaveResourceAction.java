/*
 * SaveResourceAction.java
 *
 * Created on January 20, 2006, 8:36 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author lami
 */
public class SaveResourceAction extends TgDbAction {
    
    /**
     * Creates a new instance of SaveResourceAction 
     */
    public SaveResourceAction() {
    }

    
    
    /**
     * Returns the name of this action
     * @return The name of this action
     */
    public String getName() {
        return "SaveResourceAction";
    }
    
    /**
     * Performs this action
     * @param request The http request object
     * @param context The servlet context
     * @return True of this action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            String resourceId = request.getParameter("resourceId");
            String name = request.getParameter("name");
            String comm = request.getParameter("comm");
            String url = request.getParameter("url");
            int catid = new Integer(request.getParameter("catid")).intValue();
            
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            resourceManager.updateResource(Integer.parseInt(resourceId), name, comm, url, catid, caller);                                                
            request.setAttribute("id", request.getParameter("id"));
           
            
            
            return true;
        } catch (ApplicationException ae) {
            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to update resource information.",e);
        }
    }    
}
