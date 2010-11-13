package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class CreateStrainAlleleAction extends TgDbAction {
    
    public CreateStrainAlleleAction() {}

    public String getName() {
        return "CreateStrainAlleleAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");

            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, req);
            
            if(isSubmit(req, "create")) {
                String symbol = req.getParameter("symbol");
                String name = req.getParameter("name");
                String made_by = req.getParameter("made_by");
                String origin_strain = req.getParameter("origin_strain");
                String mgi_id = req.getParameter("mgi_id");
                String mgi_url = req.getParameter("mgi_url");

                modelManager.createStrainAllele(symbol,name, mgi_id, mgi_url, made_by,origin_strain,_caller);
            }
            else if(isSubmit(req, "assign")) {
                int eid = Integer.parseInt(fdm.getValue("eid"));
                String type = req.getParameter("type");
                String attribute = req.getParameter("attribute");
                String strain_allele = req.getParameter("allele_id");
                modelManager.addMutationTypeAndAttributeToStrainAllele(eid, Integer.parseInt(strain_allele), Integer.parseInt(type), attribute, _caller);
            }
            else {
                logger.debug(getName() + " was called but no submit button was clicked!!!");
            }
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not create strains allele", e);
        }
    }     
}
