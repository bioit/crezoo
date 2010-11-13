package org.tgdb.webapp.action.allele;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.model.modelmanager.StrainAlleleDTO;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class GetStrainAlleleAction extends TgDbAction {
    
    public GetStrainAlleleAction() {}

    public String getName() {
        return "GetStrainAlleleAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            FormDataManager fdm_allele = getFormDataManager(TgDbFormDataManagerFactory.ALLELE, TgDbFormDataManagerFactory.WEB_FORM, req);
//            FormDataManager fdm_model = getFormDataManager(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, req);
            
            String strain_allele_id = "";
            
            if (exists(req.getParameter("strainalleleid"))) {
                strain_allele_id = req.getParameter("strainalleleid");
                fdm_allele.put("aid", req.getParameter("strainalleleid"));
            }
            else {
                strain_allele_id = (String)se.getAttribute("strainalleleid");
            }
            
            se.setAttribute("strainalleleid", strain_allele_id);
            
            String eid = "";
            if(req.getParameter("eid")!=null){
                eid = req.getParameter("eid");
            }else{
                eid = (String)se.getAttribute("eid");
            }
            
            StrainAlleleDTO strain_allele = modelManager.getStrainAllele(Integer.parseInt(eid),Integer.parseInt(strain_allele_id), false,_caller);

            fdm_allele.put("attributes", strain_allele.getAttributes());
            //FIXME!!! - Add a nice generic method in TgDbAction class to refresh the formdatamanager
            se.setAttribute(TgDbFormDataManagerFactory.getInstanceName(TgDbFormDataManagerFactory.ALLELE), fdm_allele);

//            logger.debug("attributes from strain_allele object: " + strain_allele.getAttributes() + " + from formdatamanager: " + fdm.getValue("attributes"));

            //FIXME!!! - There must be a better way to do this (finding out the eid value)
            Collection mutationTypes = modelManager.getMutationTypesFromStrainAllele(strain_allele.getId(), Integer.parseInt(eid),_caller);
//            Collection mutationTypeAttributes = modelManager.getMutationTypeAttributes();
            
            req.setAttribute("strainallele", strain_allele);
            req.setAttribute("mutationtypes", mutationTypes);
            /* gene connected to an allele is called promoter/locus */
            req.setAttribute("promoters", modelManager.getGenesByAllele(strain_allele.getId(), _caller));
//            req.setAttribute("attributes", mutationTypeAttributes);
            
            se.setAttribute("eid", eid);
            return true;
        
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not create strains allele", e);
        }
    }     
}
