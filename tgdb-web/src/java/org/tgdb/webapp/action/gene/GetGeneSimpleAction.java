package org.tgdb.webapp.action.gene;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GetGeneSimpleAction extends TgDbAction {
    
    public String getName() {
        return "GetGeneSimpleAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = request.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            
            if(exists(request.getParameter("gaid"))) {
                int gaid = Integer.parseInt(request.getParameter("gaid"));
                request.setAttribute("gene", modelManager.getGene(gaid, _caller));
                request.setAttribute("chromosomes", modelManager.getChromosomesForSpecies(_caller.getSid(), _caller));
            }
            else {
                throw new ApplicationException("Missing required request parameter (gaid).");
            }
            
            return true;
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }
}
