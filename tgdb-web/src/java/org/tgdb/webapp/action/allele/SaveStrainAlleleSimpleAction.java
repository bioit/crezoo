package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class SaveStrainAlleleSimpleAction extends TgDbAction {
    
    public SaveStrainAlleleSimpleAction() {}

    public String getName() {
        return "SaveStrainAlleleAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            FormDataManager fdm_allele = getFormDataManager(TgDbFormDataManagerFactory.ALLELE, TgDbFormDataManagerFactory.WEB_FORM, req);
            
            int strain_allele = Integer.parseInt(fdm_allele.getValue("aid"));//new Integer((String)se.getAttribute("strainalleleid")).intValue();
            
            String symbol = req.getParameter("symbol");
            String name = req.getParameter("name");
            String attributes = req.getParameter("attributes");
            String made_by = req.getParameter("made_by");
            String origin_strain = req.getParameter("origin_strain");String mgi_id = req.getParameter("mgi_id");
            String mgi_url = req.getParameter("mgi_url");
            
            modelManager.updateStrainAllele(0, strain_allele, symbol, name, attributes, mgi_id, mgi_url, made_by, origin_strain, true, _caller);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not save strains allele", e);
        }
    }
}
