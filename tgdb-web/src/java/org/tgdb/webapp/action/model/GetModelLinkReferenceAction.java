package org.tgdb.webapp.action.model;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.exceptions.ApplicationException;

public class GetModelLinkReferenceAction extends TgDbAction {
    
    public GetModelLinkReferenceAction() {}

    public String getName() {
        return "GetModelLinkReferenceAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) {
        try {
            TgDbCaller _caller = (TgDbCaller)req.getSession().getAttribute("caller");
            
            FormDataManager fdm = getFormDataManager(
                    TgDbFormDataManagerFactory.EXPMODEL, 
                    TgDbFormDataManagerFactory.WEB_FORM, 
                    req); 
            
            String tmpLinkid = req.getParameter("linkid");            
            if(exists(tmpLinkid))
                fdm.put("linkid", tmpLinkid);
            
            req.setAttribute("linkdto", resourceManager.getLink(Integer.parseInt(fdm.getValue("linkid")), _caller));

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
