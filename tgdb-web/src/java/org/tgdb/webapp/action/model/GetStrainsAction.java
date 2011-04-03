package org.tgdb.webapp.action.model;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.frame.Navigator;

/**
 *
 * @author heto
 */
public class GetStrainsAction extends TgDbAction
{
    
    /** Creates a new instance of GetStrainTypesAction */
    public GetStrainsAction()
    {
    }

    /**
     * Returns the name of this action
     * @return The action name
     */
    public String getName() {
        return "GetStrainsAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");                                                              
            
            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.STRAINS, TgDbFormDataManagerFactory.WEB_FORM, req);
            
            Navigator nav = (Navigator)se.getAttribute("navigator");
            nav.getPageManager().setMax(modelManager.getStrainAllelesByFDM(fdm, _caller).size());
            nav.getPageManager().setDelta(new Integer(fdm.getValue("delta")).intValue());
            
            if(!exists_without_value(req.getParameter("next")) && !exists_without_value(req.getParameter("last")) && !exists_without_value(req.getParameter("prev")) && !exists_without_value(req.getParameter("first"))) {
                nav.getPageManager().setCurrentPage(new Integer(fdm.getValue("page")).intValue());
            }
            
            req.setAttribute("strains", modelManager.getStrainsPGM(_caller, nav.getPageManager()));
//            req.setAttribute("strains", modelManager.getStrains(_caller));
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not retrieve strains.", e);
        }
    }     
}
