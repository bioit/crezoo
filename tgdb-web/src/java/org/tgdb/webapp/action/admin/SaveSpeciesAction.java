/*
 * SaveSpeciesAction.java
 *
 * Created on January 16, 2006, 9:05 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.admin;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author heto
 */
public class SaveSpeciesAction extends TgDbAction {
    
    public String getName() {
        return "SaveSpeciesAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            
            String name = request.getParameter("name");
            String comm = request.getParameter("comm");
            
            String tmp = request.getParameter("sid");
            if (tmp!=null)
            {
                // Update
                int sid = new Integer(tmp).intValue();
                adminManager.updateSpecies(sid, name, comm, caller);
            }
            else
            {
                // Create
                adminManager.createSpecies(name, comm, caller);
            }            
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Failed to perform action", e);
        }
    }
}
