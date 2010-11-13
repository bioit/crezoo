/*
 * CreateGeneOntologyAction.java
 *
 * Created on December 15, 2005, 4:42 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lami
 */
public class CreateGeneOntologyAction extends TgDbAction {
    
    /** Creates a new instance of CreateGeneOntologyAction */
    public CreateGeneOntologyAction() {
    }

    /**
     * Returns the name of this action
     * @return The action name
     */
    public String getName() {
        return "CreateGeneOntologyAction";
    }
    
    /**
     * Peroforms the action
     * @param req The http request object
     * @param context The servlet context
     * @return True of this action could be performed
     */
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller caller = (TgDbCaller)se.getAttribute("caller");     
            
            req.setAttribute("gmid", req.getParameter("gmid"));
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed in preaction for creation of genetic modification.");
        }
    }       
}
