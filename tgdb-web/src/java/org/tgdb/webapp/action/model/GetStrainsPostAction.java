package org.tgdb.webapp.action.model;
import org.tgdb.frame.Navigator;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class GetStrainsPostAction extends TgDbAction {
    
    public String getName() {
        return "GetStrainsPostAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = request.getSession();
//            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            collectFormData(TgDbFormDataManagerFactory.STRAINS, TgDbFormDataManagerFactory.WEB_FORM, request);
            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.STRAINS, TgDbFormDataManagerFactory.WEB_FORM, request);
                
            
            if(isSubmit(request, "page")) {
                Navigator nav = (Navigator)se.getAttribute("navigator");
                nav.getPageManager().setCurrentPage(new Integer(fdm.getValue("page")).intValue());
            }
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Failed to perform action", e);
        }
    }
}
