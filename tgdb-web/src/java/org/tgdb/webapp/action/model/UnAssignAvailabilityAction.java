package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


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
            int strainid = new Integer(request.getParameter("strainid")).intValue();
            
            modelManager.removeAvailabilityFromModel(eid, rid, aid, stateid, typeid, strainid, caller);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("UnAssignAvailabilityAction failed to perform action", e);
        }
    }
}
