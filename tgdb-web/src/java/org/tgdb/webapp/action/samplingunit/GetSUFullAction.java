/*
 * GetSUFullAction.java
 *
 * Created on July 14, 2005, 4:43 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.samplingunit;

import org.tgdb.frame.Navigator;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import org.tgdb.project.ParamCollector;
import org.tgdb.project.ParamDataObject;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.webapp.action.*;


/**
 * TgDbAction class for the retrieval of sampling units
 * @author heto
 */
public class GetSUFullAction extends TgDbAction {
    
    /** Creates a new instance of GetSUAction */
    public GetSUFullAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "GetSUFullAction";
    }
    
    /**
     * Performs this action
     * @param request The http request object
     * @param context The servlet context
     * @return True if this action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            Navigator nav = (Navigator)request.getSession().getAttribute("navigator");
            
            HttpSession se = request.getSession();
            
            int sid = caller.getSid();
            
            
            
            ParamDataObject pdo = (ParamDataObject)se.getAttribute("samplingunits.pdo");
            if (pdo==null)
            {
                ParamCollector pc = new ParamCollector(true);
                pc.putDefault("status", "E");
                pc.putDefault("sid", ""+sid);
                pdo = pc.fillDefaults(se, "getsufullaction", nav.getPageManager());
            }
            
                                  
            //ParamDataObject pdo = pc.collectParams(request, "getsufullaction", nav.getPageManager());
            
            Collection samplingunits = samplingUnitManager.getSamplingUnits(caller, nav.getPageManager(), pdo, sid);                        
            nav.getPageManager().setMax(samplingUnitManager.getNumberOfSamplingUnits(sid, caller, pdo)); 
            
            request.setAttribute("samplingunits", samplingunits);
            request.setAttribute("species", samplingUnitManager.getSpeciesForProject(caller.getPid(), caller));
            request.setAttribute("paramdataobject", pdo);
            request.setAttribute("currentSpecies", ""+sid);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not retrieve sampling units.");
        }
    }    
}
