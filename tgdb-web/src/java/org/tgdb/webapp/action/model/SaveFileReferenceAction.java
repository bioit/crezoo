package org.tgdb.webapp.action.model;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SaveFileReferenceAction extends TgDbAction {
    
    public SaveFileReferenceAction() {}
    
    public String getName() {
        return "SaveFileReferenceAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = req.getSession();
            TgDbCaller _caller = (TgDbCaller)session.getAttribute("caller");
            
            FormDataManager formDataManager = getFormDataManager(
                    TgDbFormDataManagerFactory.EXPMODEL, 
                    TgDbFormDataManagerFactory.WEB_FORM, 
                    req);
            
            String eid = formDataManager.getValue("eid");
            
            if (isSubmit(req, "save")) {
                String name = req.getParameter("name");
                String comm = req.getParameter("comm");
                resourceManager.updateFile(Integer.parseInt(formDataManager.getValue("fileid")), name, comm, _caller);

                int refid = Integer.parseInt(req.getParameter("refid"));
                String pubmed = req.getParameter("pubmed");
                boolean primary = false;
                if(req.getParameter("primary").equalsIgnoreCase("true")) primary = true;
                
                modelManager.updateReference(refid, name, comm, pubmed, primary, _caller);
            } else if(isSubmit(req, "remove")) {
                resourceManager.removeFile(Integer.parseInt(formDataManager.getValue("fileid")), _caller);
            }

            return true;
        } catch (Exception e) {
            if(e instanceof ApplicationException)
                throw new ApplicationException(e.getMessage());
            else
                e.printStackTrace();
        }
        
        return false;
    }         
}
