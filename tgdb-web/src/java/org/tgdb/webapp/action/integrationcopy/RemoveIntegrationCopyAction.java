package org.tgdb.webapp.action.integrationcopy;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RemoveIntegrationCopyAction extends TgDbAction {
    
    public RemoveIntegrationCopyAction() {
    }
    
    public String getName() {
        return "RemoveIntegrationCopyAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();
            TgDbCaller caller = (TgDbCaller)session.getAttribute("caller");
            
            String iscmid = request.getParameter("iscmid");
            
            modelManager.removeIntegrationCopy(new Integer(iscmid).intValue(), caller);
            
            //modelManager.removeExpressionModel(new Integer(exid).intValue(), caller);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof ApplicationException)
                throw new ApplicationException(e.getMessage());                
        }
        
        return false; 
    }    
}
