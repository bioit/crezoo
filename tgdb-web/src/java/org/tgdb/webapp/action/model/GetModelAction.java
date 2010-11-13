package org.tgdb.webapp.action.model;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GetModelAction extends TgDbAction {
    
    public GetModelAction() {}

    public String getName() {
        return "GetModelAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");                                                              
            
            String tmpEid = req.getParameter("eid");
            
            FormDataManager formDataManager = getFormDataManager(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, req);
                    
            
            if(exists(tmpEid)) {
                collectFormData(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, req);                                                                                   
                if(exists(formDataManager.getValue("eid"))) {                   
                    if(!tmpEid.equals(formDataManager.getValue("eid"))) {
                        resetFormData(TgDbFormDataManagerFactory.EXPMODEL, req);
                    }              
                }
                
                formDataManager.put("eid", tmpEid);
                //FIXME!!! - Add a nice generic method in TgDbAction class to refresh the formdatamanager
                se.setAttribute(TgDbFormDataManagerFactory.getInstanceName(TgDbFormDataManagerFactory.EXPMODEL), formDataManager);
            }
            
            int eid = Integer.parseInt(formDataManager.getValue("eid")); 
            
            req.setAttribute("modeldto", modelManager.getExperimentalModel(eid, _caller));              
            req.setAttribute("researchApps", modelManager.getResearchApplications(_caller)); 
            req.setAttribute("transgenes", modelManager.getGenesByModelAndDistinguish(eid, "transgene", _caller));
            req.setAttribute("promoters", modelManager.getPromotersForModel(eid, _caller));
            req.setAttribute("expressed_genes", modelManager.getGenesByModelAndDistinguish(eid, "expressed_gene", _caller));
            req.setAttribute("expressions", modelManager.getExpressionModelsByModel(eid, _caller));
            req.setAttribute("ics", modelManager.getIntegrationCopiesByModel(eid, _caller));
            req.setAttribute("references_primary", modelManager.getReferencesByModelAndPrimary(eid, true, _caller));
            req.setAttribute("references", modelManager.getReferencesByModelAndPrimary(eid, false, _caller));
            
            req.setAttribute("geneticBackground", modelManager.getGeneticBackground(eid, _caller));
            req.setAttribute("strains", modelManager.getStrainsByModel(eid, _caller));


            req.setAttribute("availability", modelManager.getAvailabilityForModel(eid));
            req.setAttribute("strainalleles", modelManager.getStrainAllelesFromStrain(eid, _caller));
            req.setAttribute("resourceTree", modelManager.getResourceTreeCollection(eid, _caller));
            //testingDude...
            req.setAttribute("curruser", caller.getName());
            
            //---DirectView issues...
            
            if(req.getParameter("workflow")!=null){
                String workflowCheck = req.getParameter("workflow");
                if(workflowCheck.compareTo("ViewModelDirect")==0){
                    req.setAttribute("DirectViewLevel", modelManager.getExperimentalModel(eid, _caller).getLevel());
                }
            }
            
            return true;
        } catch (ApplicationException e) {
            logger.error("---------------------------------------->GetModelAction#performAction: Failed",e);
            throw e;
        } catch (Exception e) {
            logger.error("---------------------------------------->GetModelAction#performAction: Failed",e);
            throw new ApplicationException("Could not retrieve model.");
        }
    }     
}
