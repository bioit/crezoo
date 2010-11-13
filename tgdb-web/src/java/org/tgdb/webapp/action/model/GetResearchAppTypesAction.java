/*
 * GetResearchAppTypesAction.java
 *
 * Created on February 23, 2006, 9:16 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.model;

import org.tgdb.frame.Navigator;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 *
 * @author heto
 */
public class GetResearchAppTypesAction extends TgDbAction {
    
    public String getName() {
        return "GetResearchAppTypesAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = request.getSession();
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            Navigator nav = (Navigator)se.getAttribute("navigator");
            
            Collection resapptypes = modelManager.getResearchApplications(caller);
            
            nav.getPageManager().setMax(modelManager.getResearchApplications(caller).size());
            
            request.setAttribute("resapptypes", resapptypes);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Failed to perform action", e);
        }
    }
}
