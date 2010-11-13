package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class RemoveGeneAction extends TgDbAction {
    
    public RemoveGeneAction() {}
    
    public String getName() {
        return "RemoveGeneAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = req.getSession();
            TgDbCaller _caller = (TgDbCaller)session.getAttribute("caller");

            if(exists(req.getParameter("gid"))) {
                FormDataManager fdm_allele = getFormDataManager(TgDbFormDataManagerFactory.ALLELE, TgDbFormDataManagerFactory.WEB_FORM, req);
                int aid = Integer.parseInt(fdm_allele.getValue("aid"));
                int gid = Integer.parseInt(req.getParameter("gid"));
                modelManager.removeGeneFromStrainAllele(aid, gid, _caller);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof ApplicationException)
                throw new ApplicationException(e.getMessage());                
        }
        
        return false;
    }        
}
