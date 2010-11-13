/*
 * SamplingunitPropertiesAction.java
 *
 * Created on July 14, 2005, 4:11 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.project;

import org.tgdb.frame.Navigator;
import org.tgdb.TgDbCaller;
import org.tgdb.samplingunit.samplingunitmanager.SamplingUnitDTO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.webapp.action.*;




/**
 * TgDbAction class for handling the sampling unit properties
 * @author heto
 */
public class SamplingunitPropertiesAction extends TgDbAction {
    
    /** Creates a new instance of SamplingunitPropertiesAction */
    public SamplingunitPropertiesAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of this action
     */
    public String getName() {
        return "SamplingunitPropertiesAction";
    }
    
    /**
     * Performs the action
     * @param request The http request object
     * @param context The servlet context
     * @return True if the action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) {
        try {
            HttpSession session = request.getSession();
            Navigator nav = (Navigator)session.getAttribute("navigator");
            TgDbCaller c = (TgDbCaller)session.getAttribute("caller");

            /** Set the selected Sampling unit id in navigator */
            String su = request.getParameter("suid");

            SamplingUnitDTO home = samplingUnitManager.getSamplingUnit(c, Integer.parseInt(su));
            c.setSuidName(home.getName());
            c.setSid(home.getSid());
            c.setSuid(Integer.parseInt(su));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
