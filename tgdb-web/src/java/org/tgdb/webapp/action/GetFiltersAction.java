/*
 * GetFiltersAction.java
 *
 * Created on November 16, 2005, 9:57 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action;

import org.tgdb.frame.Navigator;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.project.ParamCollector;
import org.tgdb.project.ParamDataObject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lami
 */
public class GetFiltersAction extends TgDbAction {
    
    /**
     * Creates a new instance of GetFiltersAction 
     */
    public GetFiltersAction() {
        super();
    }

    /**
     * Returns the name of this action
     * @return The action name
     */
    public String getName() {
        return "GetFilterAction";
    }
    
    /**
     * Peroforms the action
     * @param req The http request object
     * @param context The servlet context
     * @return True of this action could be performed
     */
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException{
        try {
            TgDbCaller caller = (TgDbCaller)req.getSession().getAttribute("caller");
            Navigator nav = (Navigator)req.getSession().getAttribute("navigator");
            
            HttpSession se = req.getSession();
            
            int sid = caller.getSid();
            String tmpSid = req.getParameter("sid");
            
            if(tmpSid != null) {
                sid = Integer.parseInt(tmpSid);
                caller.setSid(sid);
            }
            
            ParamCollector pc = new ParamCollector(true);
            pc.putDefault("name", "");
            pc.putDefault("expression", "");
            pc.putDefault("sid", ""+sid);            
            pc.putDefault("pid", ""+caller.getPid()); 
            
            ParamDataObject pdo = pc.collectParams(req, "getfiltersaction", nav.getPageManager());            
            
            nav.getPageManager().setMax(exportManager.getNumberOfFilters(pdo)); 
            
            req.setAttribute("species", samplingUnitManager.getSpeciesForProject(caller.getPid(), caller));
            req.setAttribute("filters", exportManager.getFilters(pdo, caller, nav.getPageManager()));
            req.setAttribute("paramdataobject", pdo);
            req.setAttribute("currentSpecies", ""+sid);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not retrieve filters.");
        }
    }     
}
