package org.tgdb.webapp.action.model;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SaveLinkReferenceAction extends TgDbAction {
    
    public SaveLinkReferenceAction() {}
    
    public String getName() {
        return "SaveLinkReferenceAction";
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
            String name = request.getParameter("name");
            String pubmed = request.getParameter("pubmed");
            boolean primary = false;
            if(request.getParameter("primary").equalsIgnoreCase("true")) primary = true;
            String comm = request.getParameter("comm");
            String url = request.getParameter("url");
            
            if (isSubmit(request, "create")) {
                modelManager.addLinkReference(Integer.parseInt(eid), name, pubmed, primary, comm, url, _caller);
            } else if (isSubmit(request, "save")) {
                int refid = Integer.parseInt(request.getParameter("refid"));
                modelManager.updateReference(refid, name, comm, pubmed, primary, _caller);
                resourceManager.updateLink(Integer.parseInt(formDataManager.getValue("linkid")), name, url, comm, _caller);
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
