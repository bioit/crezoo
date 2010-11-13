package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class GetStrainAlleleSimpleAction extends TgDbAction {
    
    public GetStrainAlleleSimpleAction() {}

    public String getName() {
        return "GetStrainAlleleSimpleAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            FormDataManager fdm_allele = getFormDataManager(TgDbFormDataManagerFactory.ALLELE, TgDbFormDataManagerFactory.WEB_FORM, req);
            
            if (exists(req.getParameter("strainalleleid"))) {
                fdm_allele.put("aid", req.getParameter("strainalleleid"));
                se.setAttribute(TgDbFormDataManagerFactory.getInstanceName(TgDbFormDataManagerFactory.ALLELE), fdm_allele);
            }

            int strain_allele_id = Integer.parseInt(fdm_allele.getValue("aid"));
           
            req.setAttribute("strainallele", modelManager.getStrainAllele(0,strain_allele_id, true,_caller));
            req.setAttribute("promoters", modelManager.getGenesByAllele(strain_allele_id, _caller));
            
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        }
        return true;
    }     
}
