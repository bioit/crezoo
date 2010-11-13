package org.tgdb.webapp.action.gene;

import org.tgdb.frame.advanced.Workflow;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.model.modelmanager.GeneDTO;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GetGeneAction extends TgDbAction {
    
    public String getName() {
        return "GetGeneAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = request.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            Workflow wf = (Workflow)request.getAttribute("workflow");
            
            
            int gaid = 0;
            if (request.getParameter("gaid")!=null)
            {
                gaid = new Integer(request.getParameter("gaid")).intValue();
                wf.setAttribute("gaid", new Integer(gaid).toString());  
            }
            else
            {
                String request_gaid = (String) se.getAttribute("currGene");//request.getAttribute("gaid");
                gaid = new Integer(request_gaid).intValue();
            }
            GeneDTO gene = null;
//            if(wf.getName().compareTo("EditGene")==0){
//                gene = modelManager.getGene(gaid, _caller);
//            }else{
//                gene = modelManager.getGene(gaid, _caller, "superscript");
//            }
            gene = modelManager.getGene(gaid, _caller);
            
            //GeneDTO gene = modelManager.getGene(gaid, caller, "superscript");
            request.setAttribute("gene", gene);
            
            Collection models = modelManager.getModelsByGene(gaid, _caller);
            request.setAttribute("models", models);
            
            Collection chromosomes = modelManager.getChromosomesForSpecies(_caller.getSid(), _caller);
            request.setAttribute("chromosomes", chromosomes);
            
            se.setAttribute("currGene", new Integer(gaid).toString());
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("GetGeneAction Failed to perform action", e);
            //throw new ApplicationException("GetGeneAction Failed to perform action -> "+request_gaid, e);
        }
    }
}
