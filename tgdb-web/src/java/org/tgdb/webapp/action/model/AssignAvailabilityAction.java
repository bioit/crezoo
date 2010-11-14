package org.tgdb.webapp.action.model;

import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;


public class AssignAvailabilityAction extends TgDbAction {
    
    public String getName() {
        return "AssignAvailabilityAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            
//            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");
//            Workflow wf = (Workflow)request.getAttribute("workflow");
//
//            int eid = new Integer(wf.getAttribute("eid")).intValue();
            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, request);
            int eid = new Integer(fdm.getValue("eid")).intValue();
            int rid = new Integer(request.getParameter("repositories")).intValue();
            int aid = new Integer(request.getParameter("avgenbacks")).intValue();
            int stateid = new Integer(request.getParameter("state")).intValue();
            int typeid = new Integer(request.getParameter("type")).intValue();
            int strainid = new Integer(request.getParameter("strainid")).intValue();
                 
            modelManager.addAvailabilityToModel(eid, rid, aid, stateid, typeid, strainid);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Availability information already exists. Please press return to enter different availability information.");
        }
    }
}
