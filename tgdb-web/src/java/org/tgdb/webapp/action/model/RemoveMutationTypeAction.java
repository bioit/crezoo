package org.tgdb.webapp.action.model;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RemoveMutationTypeAction extends TgDbAction {
    
    public RemoveMutationTypeAction() {}
    
    public String getName() {
        return "RemoveMutationTypeAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();
            TgDbCaller _caller = (TgDbCaller)session.getAttribute("caller");
            
            String strainalleleid = request.getParameter("strainalleleid");
            String mutationid = request.getParameter("mutationtypeid");

            String eid = "";
            eid = (String)session.getAttribute("eid");
            session.setAttribute("eid", eid);

            modelManager.removeMutationTypeFromStrainAllele(Integer.parseInt(eid),Integer.parseInt(mutationid), Integer.parseInt(strainalleleid), _caller);
            
            session.setAttribute("strainalleleid", strainalleleid);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof ApplicationException)
                throw new ApplicationException(e.getMessage());                
        }
        
        return false;
    }        
}
