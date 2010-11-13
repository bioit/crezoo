/*
 * GetResearchAppAction.java
 *
 * Created on February 23, 2006, 9:52 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.model;

import org.tgdb.frame.advanced.Workflow;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.model.modelmanager.ResearchAppDTO;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author heto
 */
public class GetResearchAppAction extends TgDbAction {
    
    public String getName() {
        return "GetResearchAppAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            Workflow wf = (Workflow)request.getAttribute("workflow");
            
            int raid = 0;
            String tmp = request.getParameter("raid");
            if (tmp==null || tmp.equals(""))
            {
                tmp = wf.getAttribute("raid");    
            }
            else
            {
                raid = new Integer(tmp).intValue();
                wf.setAttribute("raid", tmp);
            }
            
            ResearchAppDTO rapp = modelManager.getResearchApplication(raid, caller);
            request.setAttribute("rapp", rapp);
            request.setAttribute("projectId", new Integer(caller.getPid()).toString());
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Failed to perform action", e);
        }
    }
}
