/*
 * GetResourceAction.java
 *
 * Created on January 20, 2006, 8:30 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.resource.resourcemanager.ResourceDTO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author lami
 */
public class GetResourceAction extends TgDbAction {
    
    /** Creates a new instance of GetResourceAction */
    public GetResourceAction() {
    }
    
    
    /**
     * Returns the name of this action
     * @return The name of this action
     */
    public String getName() {
        return "GetResourceAction";
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
            String id = request.getParameter("id");
            
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            ResourceDTO dto = resourceManager.getResource(Integer.parseInt(resourceId), caller);
                                    
            request.setAttribute("resource", dto);
            request.setAttribute("id", id);
            request.setAttribute("categories", resourceManager.getResourceCategories(caller.getPid(), caller));
            
            return true;
        } catch (ApplicationException ae) {
            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get resource information.",e);
        }
    }
}
