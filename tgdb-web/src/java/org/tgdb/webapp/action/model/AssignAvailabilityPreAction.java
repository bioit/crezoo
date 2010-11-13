/*
 * AssignAvailabilityPreAction.java
 *
 * Created on July 20, 2006, 12:49 AM
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
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author zouberakis
 */
public class AssignAvailabilityPreAction extends TgDbAction {
    
    public String getName() {
        return "AssignAvailabilityPreAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            int pid = caller.getPid();
            
            Collection repositories = modelManager.getRepositoriesByProject(pid);
            request.setAttribute("repositories", repositories);
            
            Collection avgenbacks = modelManager.getAvailableGeneticBackgroundsByProject(pid);
            request.setAttribute("avgenbacks", avgenbacks);
            
            request.setAttribute("states", modelManager.getStrainStates(caller));
            request.setAttribute("types", modelManager.getStrainTypes(caller));
            
            Workflow wf = (Workflow)request.getAttribute("workflow");
            
            String eid = "";
            
            if (request.getParameter("eid") == null){
                eid = wf.getAttribute("eid");
            }
            else{
                eid = request.getParameter("eid");
            }
            
            
            
            wf.setAttribute("eid", eid);
            request.setAttribute("eid", eid);

            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("AssignAvailabilityPreAction failed to perform action", e);
        }
    }
}
