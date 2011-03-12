package org.tgdb.webapp.action.model;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ModelsPostAction extends TgDbAction {
    
    public ModelsPostAction() {}

    public String getName() {
        return "ModelsPostAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            String suid = req.getParameter("suid");
            if(exists(suid))
                _caller.setSuid(Integer.parseInt(suid));
            
            if(isSubmit(req, "reset")) {                
                resetFormData(TgDbFormDataManagerFactory.EXPMODELS, req);
            }
            else{
                collectFormData(TgDbFormDataManagerFactory.EXPMODELS, TgDbFormDataManagerFactory.WEB_FORM, req);
                //once all data is collected get the FDM again
                FormDataManager formDataManager = getFormDataManager(
                    TgDbFormDataManagerFactory.EXPMODELS, 
                    TgDbFormDataManagerFactory.WEB_FORM, 
                    req);
                
                if(isSubmit(req, "byID"))
                    formDataManager.put("ordertype", "MMMDb ID");
                
                if(isSubmit(req, "byNAME"))
                    formDataManager.put("ordertype", "LINE NAME");
                
                if(isSubmit(req, "byINDUCIBILITY"))
                    formDataManager.put("ordertype", "INDUCIBILITY");
                
                if(isSubmit(req, "byDATE"))
                    formDataManager.put("ordertype", "DATE");
            }
                
            
            return true;

        } catch (Exception e) {
//            e.printStackTrace();
            throw new ApplicationException("Failed in models post action.");
        }
    }      
}
