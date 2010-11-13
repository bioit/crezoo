/*
 * EditRepositoryAction.java
 *
 * Created on July 20, 2006, 22:18 PM
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
import org.tgdb.model.modelmanager.RepositoriesDTO;


/**
 *
 * @author zouberakis
 */
public class EditRepositoryAction extends TgDbAction {
    
    public String getName() {
        return "EditRepositoryAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            Workflow wf = (Workflow)request.getAttribute("workflow");
            int rid = new Integer(wf.getAttribute("rid")).intValue();
            String reponame = request.getParameter("reponame");
            int hasdb = Integer.parseInt(request.getParameter("hasdb"));
            String mouseurl = request.getParameter("mouseurl");
            String repourl = request.getParameter("repourl");
            modelManager.updateRepositoryName(rid, reponame, hasdb, mouseurl, repourl);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("EditRepositoryAction failed to perform action", e);
        }
    }
}
