package org.tgdb.webapp.action.gene;
import org.tgdb.frame.Navigator;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GetGenesAction extends TgDbAction {
    
    public String getName() {
        return "GetGenesAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = request.getSession();
            TgDbCaller caller = (TgDbCaller)se.getAttribute("caller");
            Navigator nav = (Navigator)se.getAttribute("navigator");
            
            nav.getPageManager().setMax(modelManager.getGenes(caller.getPid(), caller));
            Collection genes = modelManager.getGenesByPGM(nav.getPageManager());
            request.setAttribute("genes", genes);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("Failed to perform action", e);
        }
    }
}
