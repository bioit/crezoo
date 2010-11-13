package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class DeleteStrainAlleleAction extends TgDbAction {
    
    public DeleteStrainAlleleAction() {}

    public String getName() {
        return "DeleteStrainAlleleAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");

            if(exists(req.getParameter("strainalleleid"))) {
                modelManager.deleteStrainAllele(Integer.parseInt(req.getParameter("strainalleleid")), _caller);
            }
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return true;
    }     
}
