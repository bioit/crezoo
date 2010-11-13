package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SaveCreateModelAction extends TgDbAction {
    
    public SaveCreateModelAction() {}
    
    public String getName() {
        return "SaveCreateModelAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();
            TgDbCaller _caller = (TgDbCaller)session.getAttribute("caller");                        
            
            
            String suid = request.getParameter("suid");
            if (suid!=null)
                _caller.setSuid(Integer.parseInt(suid));
            
//            FormDataManager formDataManager = getFormDataManager(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, request);
            
//            String eid = formDataManager.getValue("eid");
            
            String alias = request.getParameter("alias");
            String availability = request.getParameter("availability");
            String raid = request.getParameter("raid");
            String geneticBackground = request.getParameter("geneticBackground");
            String researchAppsText = request.getParameter("researchAppsText");
            String contactId = request.getParameter("contactId");
            String comm = request.getParameter("comm");
            String desired_level = request.getParameter("desired_level");
            String donating_investigator = request.getParameter("donating_investigator");
            String inducible = request.getParameter("inducible");
            String former_names = request.getParameter("former_names");
            modelManager.createModel(_caller.getSuid(), alias, geneticBackground, availability, Integer.parseInt(raid), researchAppsText, Integer.parseInt(contactId), _caller, comm, desired_level, donating_investigator, inducible, former_names);
            
            return true;
        } catch (ApplicationException ae) {
             throw ae;     
        } catch (Exception e) {    
            throw new ApplicationException("SaveModel failed",e);
        }
    }     
}
