package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GetGeneAction extends TgDbAction {
    
    public String getName() {
        return "GetGeneAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");

            if(exists(req.getParameter("gid"))) {
                req.setAttribute("gene", modelManager.getGene(Integer.parseInt(req.getParameter("gid")), _caller));
            }
            else {
                throw new ApplicationException("Missing required request parameter (gid).");
            }
            return true;
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }
}
