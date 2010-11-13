package org.tgdb.webapp.action.model;

import org.tgdb.frame.Navigator;
import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GetModelsAction extends TgDbAction {
    
    public GetModelsAction() {}

    public String getName() {
        return "GetModelsAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            Navigator nav = (Navigator)se.getAttribute("navigator");
            
            FormDataManager formDataManager = getFormDataManager(TgDbFormDataManagerFactory.EXPMODELS, TgDbFormDataManagerFactory.WEB_FORM, req);
            
            if (req.getParameter("_raid")!=null || req.getParameter("_gaid")!=null || req.getParameter("_fstid")!=null) {
                formDataManager.reset();
                logger.debug("---------------------------------------->GetModelsAction#performAction: Reset form data manager");
            }   
                
            
            String tmp = req.getParameter("_raid");
            if (tmp!=null)
                formDataManager.put("raid", tmp);
            
            tmp = req.getParameter("_gaid");
            if (tmp!=null)
                formDataManager.put("gaid", tmp);
            
            tmp = req.getParameter("_fstid");
            if (tmp!=null)
                formDataManager.put("fstid", tmp);
            
            req.setAttribute("formdata", formDataManager);
            
            nav.getPageManager().setMax(modelManager.getExperimentalModelsByForm(formDataManager, _caller));
            
            Collection models = modelManager.getExperimentalModelsByPGM(nav.getPageManager());
            
            req.setAttribute("samplingunits", samplingUnitManager.getSamplingUnits(_caller.getPid(), _caller));
            req.setAttribute("modelsdto", models);
            
            req.setAttribute("participants", modelManager.getParticipants());
            req.setAttribute("researchers", modelManager.getParticipantNames());
            req.setAttribute("mutations", modelManager.getMutationTypes(_caller.getPid(), _caller));
            
            req.setAttribute("rapps", modelManager.getResearchApplications(_caller));
//            req.setAttribute("genes", modelManager.getGenesByProject(_caller.getPid(), _caller));
            req.setAttribute("genes", modelManager.getGenesByDistinguish("transgenes", _caller));
            
            req.setAttribute("sortby", modelManager.getOrderByTypes());
            req.setAttribute("disseminationlevels", modelManager.getLevelsForModel());
            
            req.setAttribute("samplingUnit", new Integer(_caller.getSuid()).toString());
            
            return true;
        } catch (ApplicationException e) {
            logger.error("---------------------------------------->GetModelsAction#performAction: Action failed", e);
            throw e;
        } catch (Exception e) {
            logger.error("---------------------------------------->GetModelsAction#performAction: Action failed", e);
            throw new ApplicationException("Could not retrieve models.");
        }
    }     
}
