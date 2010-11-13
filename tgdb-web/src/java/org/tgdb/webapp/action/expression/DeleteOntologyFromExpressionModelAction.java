package org.tgdb.webapp.action.expression;

import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class DeleteOntologyFromExpressionModelAction extends TgDbAction {
    
    public String getName() {
        return "DeleteOntologyFromExpressionModelAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller _caller = (TgDbCaller)req.getSession().getAttribute("caller");
            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.EXPRESSION_MODEL, TgDbFormDataManagerFactory.WEB_FORM, req);
            int exid = Integer.parseInt(fdm.getValue("exid"));
            
            String ma_id = req.getParameter("ma_id");
            if(exists(ma_id)){
                modelManager.deleteOntologyFromExpressionModel(exid, ma_id, "MA", _caller);
            }
            
            String emap_id = req.getParameter("emap_id");
            if(exists(emap_id)){
                modelManager.deleteOntologyFromExpressionModel(exid, emap_id, "EMAP", _caller);
            }
            
            
            return true;
        } catch (Exception e) {
            throw new ApplicationException("SaveExpressionModelAction Failed to perform action", e);
        }
    }
}
