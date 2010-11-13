package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class SaveStrainAlleleAction extends TgDbAction {
    
    public SaveStrainAlleleAction() {}

    public String getName() {
        return "SaveStrainAlleleAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");

            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, req);
            int eid = Integer.parseInt(fdm.getValue("eid"));
            
            int strain_allele = new Integer((String)se.getAttribute("strainalleleid")).intValue();
            
            String symbol = req.getParameter("symbol");
            String name = req.getParameter("name");
            String attributes = req.getParameter("attributes");
            String made_by = req.getParameter("made_by");
            String origin_strain = req.getParameter("origin_strain");
            
            String mgi_id = req.getParameter("mgi_id");
            String mgi_url = req.getParameter("mgi_url");
            
            modelManager.updateStrainAllele(eid, strain_allele, symbol, name, attributes, mgi_id, mgi_url, made_by, origin_strain, false, _caller);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not save strains allele", e);
        }
    }
}
