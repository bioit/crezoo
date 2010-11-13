package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class CreateGeneFromModelAction extends TgDbAction {
    
    public String getName() {
        return "CreateGeneFromModelAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");
            
            request.setAttribute("chromosomes", modelManager.getChromosomesForSpecies(_caller.getSid(), _caller));
            request.setAttribute("speciesname", modelManager.getSpecies(_caller.getSid(), _caller).getName());
            
            return true;
       
        } catch (Exception e) {
            throw new ApplicationException("Failed to perform action", e);
        }
    }
}
