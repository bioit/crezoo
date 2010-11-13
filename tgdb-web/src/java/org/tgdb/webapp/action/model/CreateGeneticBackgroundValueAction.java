/*
 * CreateGeneticBackgroundValueAction.java
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
import java.lang.Integer;
//import java.io.*;


/**
 *
 * @author zouberakis
 */
public class CreateGeneticBackgroundValueAction extends TgDbAction {
    
    public String getName() {
        return "CreateGeneticBackgroundValueAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            Workflow wf = (Workflow)request.getAttribute("workflow");
            
            //if gen. back. value creation was triggered by gen. back. info assignment from model view
            //we need the following if statement+the passed attribute.
            if (request.getParameter("eid")!=null){
                wf.setAttribute("eid", request.getParameter("eid"));
            }
            
            if (request.getParameter("bid")!=null)
            {
                String backname = request.getParameter("backname");
                int bid = new Integer(request.getParameter("bid")).intValue();
                modelManager.updateGeneBackValue(bid,backname,caller);
            }
            else if (wf.getAttribute("bid")!=null)
            {
                
                String backname = request.getParameter("backname");
                int bid = Integer.parseInt((String)wf.getAttribute("bid"));
                modelManager.updateGeneBackValue(bid,backname,caller);
                
            }
            else{
                String backname = request.getParameter("backname");
                int bid = modelManager.createGeneBackValue(backname, caller);
                wf.setAttribute("bid", new Integer(bid).toString());
                
                request.setAttribute("bid", new Integer(bid).toString());
            }
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("CreateGeneticBackgroundValueAction Failed to perform action", e);
        }
    }
}
