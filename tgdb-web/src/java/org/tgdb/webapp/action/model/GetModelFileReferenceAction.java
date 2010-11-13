package org.tgdb.webapp.action.model;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.exceptions.ApplicationException;

public class GetModelFileReferenceAction extends TgDbAction {
    
    public GetModelFileReferenceAction() {}

    public String getName() {
        return "GetModelFileReferenceAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) {
        try {
            TgDbCaller _caller = (TgDbCaller)req.getSession().getAttribute("caller");
            
            FormDataManager formDataManager = getFormDataManager(
                    TgDbFormDataManagerFactory.EXPMODEL, 
                    TgDbFormDataManagerFactory.WEB_FORM, 
                    req); 
            
            String tmpFileid = req.getParameter("fileid");

            if(exists(tmpFileid)) formDataManager.put("fileid", tmpFileid);
            
            req.setAttribute("filedto", resourceManager.getFile(Integer.parseInt(formDataManager.getValue("fileid")), _caller));

            if(exists(req.getParameter("refid"))) {
                req.setAttribute("refdto", modelManager.getReference(Integer.parseInt(req.getParameter("refid")), _caller));
            }
            else {
                throw new ApplicationException("missing required request parameter");
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }       
}
