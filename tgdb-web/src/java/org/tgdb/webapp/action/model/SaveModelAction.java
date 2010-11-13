package org.tgdb.webapp.action.model;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SaveModelAction extends TgDbAction {
    
    public SaveModelAction() {}
    
    public String getName() {
        return "SaveModelAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();
            TgDbCaller _caller = (TgDbCaller)session.getAttribute("caller");                        
            
            FormDataManager formDataManager = getFormDataManager(
                    TgDbFormDataManagerFactory.EXPMODEL, 
                    TgDbFormDataManagerFactory.WEB_FORM, 
                    request); 
            
            String eid = formDataManager.getValue("eid");
            
            logger.debug("---------------------------------------->SaveModelAction#performAction: eid = " + eid + " & create = "+request.getParameter("create"));
            
            String alias = request.getParameter("alias");
            String availability = request.getParameter("availability");
            String raid = request.getParameter("raid");
            String geneticBackground = request.getParameter("geneticBackground");
            String researchAppsText = request.getParameter("researchAppsText");
            String contactId = request.getParameter("contactId");               
            String comm = request.getParameter("comm");
            String level = "";
            if(exists(request.getParameter("level_"))){
                level = request.getParameter("level_");
            } else {
                level = request.getParameter("level");
            }
            String desired_level = request.getParameter("desired_level");
            String donating_investigator = request.getParameter("donating_investigator");
            String inducible = request.getParameter("inducible");
            String former_names = request.getParameter("former_names");

            modelManager.updateModel(_caller.getSuid(), Integer.parseInt(eid), alias, geneticBackground, availability, Integer.parseInt(raid), researchAppsText, Integer.parseInt(contactId), _caller, comm, level, desired_level, donating_investigator, inducible, former_names);

            
            return true;
        } catch (ApplicationException ae) {
             throw ae;     
        } catch (Exception e) {    
            throw new ApplicationException("SaveModel failed",e);
        }
    }     
}
