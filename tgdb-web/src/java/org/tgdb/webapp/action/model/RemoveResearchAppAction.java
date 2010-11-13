/*
 * RemoveResearchAppAction.java
 *
 * Created on February 23, 2006, 10:36 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author heto
 */
public class RemoveResearchAppAction extends TgDbAction {
    
    public String getName() {
        return "RemoveResearchAppAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            
            int raid = new Integer(request.getParameter("raid")).intValue();
            modelManager.removeResearchApplication(raid, caller);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Failed to perform action", e);
        }
    }
}
