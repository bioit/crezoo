/*
 * ViewAvailableGeneticBackgroundsAction.java
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
public class ViewAvailableGeneticBackgroundsAction extends TgDbAction {
    
    public String getName() {
        return "ViewAvailableGeneticBackgroundsAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = request.getSession();
            TgDbCaller caller = (TgDbCaller)se.getAttribute("caller");
            int pid = caller.getPid();
            Collection avgenbacks = modelManager.getAvailableGeneticBackgroundsByProject(pid);
            request.setAttribute("avgenbacks", avgenbacks);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("ViewAvailableGeneticBackgroundsAction failed to perform action", e);
        }
    }
}
