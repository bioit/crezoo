package org.tgdb.webapp.action.resource;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GetCategoriesAction extends TgDbAction {
    
    public GetCategoriesAction() {}
    
    public String getName() {
        return "GetCategoriesAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();            
            TgDbCaller _caller = (TgDbCaller)session.getAttribute("caller");
            request.setAttribute("categories", resourceManager.getResourceCategories(_caller.getPid(), _caller));
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }      
}
