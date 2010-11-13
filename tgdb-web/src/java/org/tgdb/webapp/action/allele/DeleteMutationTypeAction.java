package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class DeleteMutationTypeAction extends TgDbAction {
    
    public DeleteMutationTypeAction() {}

    public String getName() {
        return "DeleteMutationTypeAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");   
            
            if(exists(req.getParameter("mtid"))) {
                modelManager.deleteMutationType(Integer.parseInt(req.getParameter("mtid")), _caller);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not get mutation/allele type", e);
        }
    }     
}
