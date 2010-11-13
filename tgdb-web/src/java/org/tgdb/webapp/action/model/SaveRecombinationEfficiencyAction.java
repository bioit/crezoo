package org.tgdb.webapp.action.model;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SaveRecombinationEfficiencyAction extends TgDbAction {
    
    public SaveRecombinationEfficiencyAction() {
    }
    
    public String getName() {
        return "SaveRecombinationEfficiencyAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();
            TgDbCaller caller = (TgDbCaller)session.getAttribute("caller");                        
            
            FormDataManager formDataManager = getFormDataManager(
                    TgDbFormDataManagerFactory.EXPMODEL, 
                    TgDbFormDataManagerFactory.WEB_FORM, 
                    request); 
            
            String eid = formDataManager.getValue("eid");
            
            String researchAppsText = request.getParameter("researchAppsText");
            
            modelManager.updateRecombinationEfficiencyModel(Integer.parseInt(eid), researchAppsText, caller);

            
            return true;
        }
        catch (ApplicationException ae)
        {
             throw ae;     
        } 
        catch (Exception e) 
        {    
            throw new ApplicationException("SaveRecombinationEfficiency failed",e);
        }
    }     
}
