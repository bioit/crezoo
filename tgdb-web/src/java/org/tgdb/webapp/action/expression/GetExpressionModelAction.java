package org.tgdb.webapp.action.expression;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class GetExpressionModelAction extends TgDbAction {
    
    public String getName() {
        return "GetExpressionModelAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.EXPRESSION_MODEL, TgDbFormDataManagerFactory.WEB_FORM, req);
            
            String exid = req.getParameter("exid");
            
            if(exists(exid)){
                fdm.put("exid", exid);
                se.setAttribute(TgDbFormDataManagerFactory.getInstanceName(TgDbFormDataManagerFactory.EXPRESSION_MODEL), fdm);
            }
            else{
                exid = fdm.getValue("exid");
            }
            
            req.setAttribute("expression", modelManager.getExpressionModel(new Integer(exid).intValue(), _caller));
            
            req.setAttribute("exfiles", modelManager.getExpressionModelFiles(new Integer(exid).intValue(), _caller));

            req.setAttribute("emap_terms", modelManager.getOntologyTerms(new Integer(exid).intValue(), "EMAP", _caller));
            req.setAttribute("ma_terms", modelManager.getOntologyTerms(new Integer(exid).intValue(), "MA", _caller));

//            req.setAttribute("gross_anatomy_terms", modelManager.getTermsByOntology("EMAP"));
            
        } catch (Exception e) {
//            throw new ApplicationException("GetExpressionModelAction Failed to perform action", e);
            logger.error(getStackTrace(e));
        }
        return true;
    }
}
