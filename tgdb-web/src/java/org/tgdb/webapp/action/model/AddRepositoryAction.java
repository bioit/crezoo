/*
 * AddRepositoryAction.java
 *
 * Created on July, 2006, 22:18 PM
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
 * @author zouberakis
 */
public class AddRepositoryAction extends TgDbAction {
    
    public String getName() {
        return "AddRepositoryAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            Workflow wf = (Workflow)request.getAttribute("workflow");
            
            //if repository creation was triggered by av.gen.back. assignment from model view
            //we need the following if statement+the passed attribute.
            if (request.getParameter("eid")!=null){
                wf.setAttribute("eid", request.getParameter("eid"));
            }
            
            String reponame = request.getParameter("reponame");
            int hasdb = Integer.parseInt(request.getParameter("hasdb"));
            String mouseurl = request.getParameter("mouseurl");
            String repourl = request.getParameter("repourl");
            modelManager.addRepository(reponame, hasdb, mouseurl, repourl, caller);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("AddRepositoryAction Failed to perform action", e);
        }
    }
}
