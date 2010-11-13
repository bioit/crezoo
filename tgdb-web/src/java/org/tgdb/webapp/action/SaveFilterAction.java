/*
 * SaveFilterAction.java
 *
 * Created on November 17, 2005, 8:30 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action;

import org.tgdb.frame.Navigator;
import org.tgdb.TgDbCaller;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;

/**
 *
 * @author lami
 */
public class SaveFilterAction extends TgDbAction {
    
    /** Creates a new instance of SaveFilterAction */
    public SaveFilterAction() {
        super();
    }
    
    /**
     * Returns the name of this action
     * @return The name of this action
     */
    public String getName() {
        return "SaveFilterAction";
    }
    
    /**
     * Performs the action
     * @param req The http request object
     * @param context The servlet context
     * @return True if the action could be performed
     */
    public boolean performAction(HttpServletRequest req, ServletContext context) {
        try {
            TgDbCaller caller = (TgDbCaller)req.getSession().getAttribute("caller");
            
            Navigator nav = (Navigator)req.getSession().getAttribute("navigator");
            String expression = req.getParameter("expression");
            String name = req.getParameter("name");
            String comm = req.getParameter("comm");
            String tmpSid = req.getParameter("sid");
            String tmpFid = req.getParameter("fid");
            
            if(tmpSid != null)
                caller.setSid(Integer.parseInt(tmpSid));            
            
            
            int fid = 0;
            if(tmpFid != null)
                fid = Integer.parseInt(tmpFid);
            
            if(isSubmit(req, "update")) {
                exportManager.updateFilter(fid, caller.getSid(), name, comm, expression, caller);
            }
            else if(isSubmit(req, "create")) {
                exportManager.createFilter(name, comm, expression, caller.getSid(), caller.getPid(), caller);
            }     
            else if(isSubmit(req, "remove")) {
                exportManager.removeFilter(fid, caller);
            }               
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    } 
}
