package org.tgdb.webapp.action.gene;
import org.tgdb.frame.Navigator;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class GetGenesAction extends TgDbAction {
    
    public String getName() {
        return "GetGenesAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = request.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
//            Navigator nav = (Navigator)se.getAttribute("navigator");
            
            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.GENES, TgDbFormDataManagerFactory.WEB_FORM, request);
            
            Navigator nav = (Navigator)se.getAttribute("navigator");
            nav.getPageManager().setMax(modelManager.getStrainAllelesByFDM(fdm, _caller).size());
            nav.getPageManager().setDelta(new Integer(fdm.getValue("delta")).intValue());
            
            if(!exists_without_value(request.getParameter("next")) && !exists_without_value(request.getParameter("last")) && !exists_without_value(request.getParameter("prev")) && !exists_without_value(request.getParameter("first"))) {
                nav.getPageManager().setCurrentPage(new Integer(fdm.getValue("page")).intValue());
            }
          
//            request.setAttribute("fdm_genes", fdm);
            
            
            nav.getPageManager().setMax(modelManager.getGenes(_caller.getPid(), _caller));
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
