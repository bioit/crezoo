package org.tgdb.webapp.action.model;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GetSimpleModelAction extends TgDbAction {
    
    public GetSimpleModelAction() {}

    public String getName() {
        return "GetSimpleModelAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            
            String tmpEid = req.getParameter("eid");
            
            FormDataManager formDataManager = getFormDataManager(
                    TgDbFormDataManagerFactory.EXPMODEL, 
                    TgDbFormDataManagerFactory.WEB_FORM, 
                    req);  
                    
            
            if(exists(tmpEid)) {
                collectFormData(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, req);                                                                                   
                if(exists(formDataManager.getValue("eid"))) {                   
                    if(!tmpEid.equals(formDataManager.getValue("eid"))) {
                        resetFormData(TgDbFormDataManagerFactory.EXPMODEL, req);
                    }              
                }
                
                formDataManager.put("eid", tmpEid);                                              
            }
            
            int eid = Integer.parseInt(formDataManager.getValue("eid")); 
            
            req.setAttribute("modeldto", modelManager.getExperimentalModel(eid, _caller, "superscript"));

            // These are used in the model details section
//            req.setAttribute("users", projectManager.getProjectUsers(_caller.getPid(), _caller));
            req.setAttribute("users", adminManager.getCorrespondents("correspondent", _caller));
            
            req.setAttribute("samplingunits", samplingUnitManager.getSamplingUnits(_caller.getPid(), _caller));
            req.setAttribute("researchApps", modelManager.getResearchApplications(_caller));
            req.setAttribute("levels", modelManager.getLevelsForModel());
            req.setAttribute("inducibles", modelManager.getInducibleValues());
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not retrieve model.", e);
        }
    }     
}
