/*
 * AssignAvailabilityAction.java
 *
 * Created on July 18, 2006, 12:10 PM
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
public class AssignAvailabilityAction extends TgDbAction {
    
    public String getName() {
        return "AssignAvailabilityAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            Workflow wf = (Workflow)request.getAttribute("workflow");
            
            int eid = new Integer(wf.getAttribute("eid")).intValue();
            int rid = new Integer(request.getParameter("repositories")).intValue();
            int aid = new Integer(request.getParameter("avgenbacks")).intValue();
            int stateid = new Integer(request.getParameter("state")).intValue();
            int typeid = new Integer(request.getParameter("type")).intValue();
                 
            modelManager.addAvailabilityToModel(eid, rid, aid, stateid, typeid);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Availability information already exists. Please press return to enter different availability information.");
        }
    }
}
