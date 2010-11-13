package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class AssignExpressedGenePreAction extends TgDbAction {
    
    public String getName() {
        return "AssignExpressedGenePreAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");
            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, request);
            String eid = fdm.getValue("eid");
            
//            request.setAttribute("chromosomes", modelManager.getChromosomesForSpecies(_caller.getSid(), _caller));
            
            request.setAttribute("genes", modelManager.getUnassignedGenes(Integer.parseInt(eid), _caller.getPid(), "expressed_gene", _caller));

            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Failed to perform action", e);
        }
    }
}
