/*
 * ViewRepositoriesAction.java
 *
 * Created on July 21, 2006, 2:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.model;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 *
 * @author zouberakis
 */
public class ViewRepositoriesAction extends TgDbAction {
    
    public String getName() {
        return "ViewRepositoriesAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = request.getSession();
            TgDbCaller caller = (TgDbCaller)se.getAttribute("caller");
            Collection repositories = modelManager.getRepositoriesByProject(caller.getPid());
            request.setAttribute("repositories", repositories);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("ViewRepositoriesAction failed to perform action", e);
        }
    }
}
