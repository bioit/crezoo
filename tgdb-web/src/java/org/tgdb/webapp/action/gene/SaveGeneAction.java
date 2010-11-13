package org.tgdb.webapp.action.gene;

import org.tgdb.frame.advanced.Workflow;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class SaveGeneAction extends TgDbAction {
    
    public String getName() {
        return "SaveGeneAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");

            String name = request.getParameter("name");
            String genesymbol = request.getParameter("genesymbol");
            int cromosome = Integer.parseInt(request.getParameter("chromosome"));
            String mgiid = request.getParameter("mgiid");

            if (isSubmit(request,"save") && exists(request.getParameter("gaid"))) {
                int gaid = new Integer(request.getParameter("gaid")).intValue();
                String comm = request.getParameter("comm");
                String geneexpress = request.getParameter("geneexpress");
                String idgene = request.getParameter("idgene");
                String idensembl = request.getParameter("idensembl");
                String molecular_note = request.getParameter("molecular_note");
                String molecular_note_url = request.getParameter("molecular_note_url");
            
                modelManager.updateGene(gaid, name, comm, mgiid, genesymbol, geneexpress, idgene, idensembl, cromosome, molecular_note, molecular_note_url, _caller);
            }
            else if(isSubmit(request,"create_transgene")) {
                String comm = request.getParameter("comm");
                String geneexpress = request.getParameter("geneexpress");
                String idgene = request.getParameter("idgene");
                String idensembl = request.getParameter("idensembl");
                String molecular_note = request.getParameter("molecular_note");
                String molecular_note_url = request.getParameter("molecular_note_url");
            
                int gaid = modelManager.createTransgene(name, comm, mgiid, genesymbol, geneexpress, idgene, idensembl, cromosome, molecular_note, molecular_note_url, _caller);
                Workflow wf = (Workflow)request.getAttribute("workflow");
                wf.setAttribute("gaid", new Integer(gaid).toString());
                
                request.setAttribute("gaid", new Integer(gaid).toString());
            }
            else if(isSubmit(request, "create_promoter")) {
                String driver_note = request.getParameter("driver_note");
                String common_name = request.getParameter("common_name");
                modelManager.createPromoter(name, genesymbol, cromosome, mgiid, driver_note, common_name, _caller);
            }
            else if(isSubmit(request, "create_expressed_gene")) {
                String comm = request.getParameter("comm");
                modelManager.createExpressedGene(name, genesymbol, cromosome, mgiid, comm, _caller);
            }
            else if(isSubmit(request, "save_promoter")) {
                int gaid = new Integer(request.getParameter("gaid")).intValue();
                String driver_note = request.getParameter("driver_note");
                String common_name = request.getParameter("common_name");
                modelManager.updatePromoter(gaid, name, genesymbol, mgiid, cromosome, driver_note, common_name, _caller);
            }
            else if(isSubmit(request, "save_expressed_gene")) {
                int gaid = new Integer(request.getParameter("gaid")).intValue();
                String comm = request.getParameter("comm");
                modelManager.updateExpressedGene(gaid, name, genesymbol, mgiid, cromosome, comm, _caller);
            }
            
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("SaveGeneAction Failed to perform action", e);
        }
    }
}
