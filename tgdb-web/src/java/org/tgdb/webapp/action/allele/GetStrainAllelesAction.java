package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;
import org.tgdb.frame.Navigator;

public class GetStrainAllelesAction extends TgDbAction {
    
    public String getName() {
        return "GetStrainAllelesAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.STRAIN_ALLELES, TgDbFormDataManagerFactory.WEB_FORM, req);
            
            Navigator nav = (Navigator)se.getAttribute("navigator");
            nav.getPageManager().setMax(modelManager.getStrainAllelesByFDM(fdm, _caller).size());
            nav.getPageManager().setDelta(new Integer(fdm.getValue("delta")).intValue());
            
            if(!exists_without_value(req.getParameter("next")) && !exists_without_value(req.getParameter("last")) && !exists_without_value(req.getParameter("prev")) && !exists_without_value(req.getParameter("first"))) {
                nav.getPageManager().setCurrentPage(new Integer(fdm.getValue("page")).intValue());
            }
          
            req.setAttribute("fdm_alleles", fdm);

//            req.setAttribute("strain_alleles", modelManager.getStrainAlleles( _caller));
            req.setAttribute("strain_alleles", modelManager.getStrainAllelesByPGMFDM(nav.getPageManager(), fdm,  _caller));
            
            req.setAttribute("promoters", modelManager.getGenesByDistinguish("promoter", _caller));
            req.setAttribute("inducibilities", modelManager.getInducibility());
            req.setAttribute("madebys", modelManager.getMadeBy());
            return true;
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }
}
