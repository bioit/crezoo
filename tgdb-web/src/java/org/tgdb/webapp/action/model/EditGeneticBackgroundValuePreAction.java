/*
 * EditGeneticBackgroundValuePreAction.java
 *
 * Created on July, 2006, 22:18 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.model;

import org.tgdb.frame.advanced.Workflow;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author zouberakis
 */
public class EditGeneticBackgroundValuePreAction extends TgDbAction {
    
    public String getName() {
        return "EditGeneticBackgroundValuePreAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            
            if (request.getParameter("bid")!=null)
            {
                int bid = new Integer(request.getParameter("bid")).intValue();
                String backname = modelManager.getGeneBackValueName(bid,caller);
                request.setAttribute("backname", backname);
                request.setAttribute("bid", new Integer(bid).toString());
                
                Workflow wf = (Workflow)request.getAttribute("workflow");
                wf.setAttribute("bid", new Integer(bid).toString());
            }
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("EditGeneticBackgroundValuePreAction Failed to perform action", e);
        }
    }
}
