package org.tgdb.webapp.action.gene;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;
import org.tgdb.model.modelmanager.GeneDTO;

public class GetGeneSimpleAction extends TgDbAction {
    
    public String getName() {
        return "GetGeneSimpleAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = request.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");

            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.GENE, TgDbFormDataManagerFactory.WEB_FORM, request);
            
            if(exists(request.getParameter("gaid"))) {
                fdm.put("gaid", request.getParameter("gaid"));
                //FIXME!!! - Add a nice generic method in TgDbAction class to refresh the formdatamanager
                se.setAttribute(TgDbFormDataManagerFactory.getInstanceName(TgDbFormDataManagerFactory.GENE), fdm);
            }
//                int gaid = Integer.parseInt(request.getParameter("gaid"));
                int gaid = Integer.parseInt(fdm.getValue("gaid"));
                GeneDTO gene = modelManager.getGene(gaid, _caller);
                request.setAttribute("gene", gene);
                request.setAttribute("chromosomes", modelManager.getChromosomesForSpecies(_caller.getSid(), _caller));
                //for promoters only!
                if(gene.getDistinguish().equalsIgnoreCase("promoter")) {
                    request.setAttribute("promoter_links", modelManager.getPromoterLinks(gaid, _caller));
                }
//            }
//            else {
//                throw new ApplicationException("Missing required request parameter (gaid).");
//            }
            
            return true;
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }
}
