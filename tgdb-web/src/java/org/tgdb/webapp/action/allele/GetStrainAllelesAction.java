package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GetStrainAllelesAction extends TgDbAction {
    
    public String getName() {
        return "GetStrainAllelesAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");

            req.setAttribute("strain_alleles", modelManager.getStrainAlleles( _caller));
            return true;
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }
}
