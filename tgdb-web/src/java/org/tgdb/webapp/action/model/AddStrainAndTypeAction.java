package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class AddStrainAndTypeAction extends TgDbAction
{
    
    public AddStrainAndTypeAction() {}

    public String getName() {
        return "AddStrainAndTypeAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");                                                              
            
            int id = new Integer(workflow.getAttribute("strainid")).intValue();
            
            String stateString = req.getParameter("availablestates").trim();
            int state = 0;
            if (stateString.equalsIgnoreCase("") && stateString!=null) 
                state = new Integer(stateString).intValue();
            
            
            String typeString = req.getParameter("availabletypes").trim();
            
            logger.debug("---------------------------------------->AddStrainAndTypeAction#performAction: state = "+stateString+", type = "+typeString);
            
            int type = 0;
            if (typeString.equalsIgnoreCase("") && typeString!=null)
                type = new Integer(typeString).intValue();
            
            modelManager.addStrainAndTypeToStrain(id,type,state,_caller);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not add strains and types.", e);
        }
    }     
}
