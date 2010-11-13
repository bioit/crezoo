/*
 * UnAssignAvailabilityAction.java
 *
 * Created on July 20, 2006, 10:08 AM
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
 * @author zouberakis
 */
public class UnAssignAvailabilityAction extends TgDbAction {
    
    public String getName() {
        return "UnAssignAvailabilityAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            
            int eid = new Integer(request.getParameter("eid")).intValue();
            int rid = new Integer(request.getParameter("rid")).intValue();
            int aid = new Integer(request.getParameter("aid")).intValue();
            int stateid = new Integer(request.getParameter("stateid")).intValue();
            int typeid = new Integer(request.getParameter("typeid")).intValue();
            
            modelManager.removeAvailabilityFromModel(eid, rid, aid, stateid, typeid, caller);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("UnAssignAvailabilityAction failed to perform action", e);
        }
    }
}
