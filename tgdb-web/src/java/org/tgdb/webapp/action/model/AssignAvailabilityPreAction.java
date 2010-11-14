package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;


public class AssignAvailabilityPreAction extends TgDbAction {
    
    public String getName() {
        return "AssignAvailabilityPreAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");
            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, request);

            int eid = 0;

            if(exists(request.getParameter("eid"))) {
                eid = new Integer(request.getParameter("eid")).intValue();
                fdm.put("eid", request.getParameter("eid"));
                //FIXME!!! - Add a nice generic method in TgDbAction class to refresh the formdatamanager
                request.getSession().setAttribute(TgDbFormDataManagerFactory.getInstanceName(TgDbFormDataManagerFactory.EXPMODEL), fdm);
            }
            else {
                eid = new Integer(fdm.getValue("eid")).intValue();
            }

            request.setAttribute("strains", modelManager.getStrainsConnectedToModel(eid, _caller));
            request.setAttribute("repositories", modelManager.getRepositoriesByProject(_caller.getPid()));
            request.setAttribute("avgenbacks", modelManager.getAvailableGeneticBackgroundsByProject(_caller.getPid()));
            request.setAttribute("states", modelManager.getStrainStates(_caller));
            request.setAttribute("types", modelManager.getStrainTypes(_caller));
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("AssignAvailabilityPreAction failed to perform action", e);
        }
    }
}
