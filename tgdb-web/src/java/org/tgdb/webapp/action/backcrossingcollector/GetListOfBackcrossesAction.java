package org.tgdb.webapp.action.backcrossingcollector;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GetListOfBackcrossesAction extends TgDbAction {
    
    public GetListOfBackcrossesAction() {}

    public String getName() {
        return "GetListOfBackcrossesAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");            
            Collection models = modelManager.getExperimentalModelsForBackcrossingListGeneration(_caller);
            
            req.setAttribute("modelsdto", models);
            
            req.setAttribute("disseminationlevels", modelManager.getLevelsForModel());
            
            return true;
        } catch (ApplicationException e) {
            logger.error("---------------------------------------->GetListOfBackcrossesAction#performAction: Failed", e);
            throw e;
        } catch (Exception e) {
            logger.error("---------------------------------------->GetListOfBackcrossesAction#performAction: Failed", e);
            throw new ApplicationException("GetListOfBackcrossesAction#performAction");
        }
    }     
}
