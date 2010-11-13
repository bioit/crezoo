/*
 * SaveResearchAppAction.java
 *
 * Created on February 23, 2006, 10:05 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.model;

import org.tgdb.frame.advanced.Workflow;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author heto
 */
public class SaveResearchAppAction extends TgDbAction {
    
    public String getName() {
        return "SaveResearchAppAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            Workflow wf = (Workflow)request.getAttribute("workflow");
            
            String name = request.getParameter("name");
            String comm = request.getParameter("comm");
            
            if (wf.getAttribute("raid")==null)
            {
                // Create
                int raid = modelManager.createResearchApplication(name, comm, caller);
                wf.setAttribute("raid", new Integer(raid).toString());
            }
            else
            {
                // Update
                int raid = new Integer(wf.getAttribute("raid")).intValue();
                modelManager.updateResearchApplication(raid, name, comm, caller);
            }
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Failed to perform action", e);
        }
    }
}
