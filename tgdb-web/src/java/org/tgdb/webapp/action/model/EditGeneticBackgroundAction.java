/*
 * EditGeneticBackgroundAction.java
 *
 * Created on July 18, 2006, 12:10 PM
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
public class EditGeneticBackgroundAction extends TgDbAction {
    
    public String getName() {
        return "EditGeneticBackgroundAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            Workflow wf = (Workflow)request.getAttribute("workflow");
            
            //if (request.getParameter("dna_origin")!=null){
                int eid = new Integer(wf.getAttribute("eid")).intValue();
                int dna_origin = new Integer(request.getParameter("dna_origin")).intValue();
                int targeted_back = new Integer(request.getParameter("targeted_back")).intValue();
                int host_back = new Integer(request.getParameter("host_back")).intValue();
                int backcrossing_strain = new Integer(request.getParameter("backcrossing_strain")).intValue();
                String backcrosses = request.getParameter("backcrosses");
                
                modelManager.updateGeneticBackgroundForModel(eid, dna_origin, targeted_back, host_back, backcrossing_strain, backcrosses, caller);
            //}
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("EditGeneticBackgroundAction Failed to perform action", e);
        }
    }
}
