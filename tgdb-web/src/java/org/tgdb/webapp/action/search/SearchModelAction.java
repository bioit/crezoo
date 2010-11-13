package org.tgdb.webapp.action.search;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import java.util.Collection;
import java.util.TreeSet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.TgDbAction;

public class SearchModelAction extends TgDbAction {
    
    public String getName() {
        return "SearchModelAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            
            String geneName = request.getParameter("gene");
            String vsName = request.getParameter("vsname");
            String raName = request.getParameter("raname");
            String project = request.getParameter("project");
            
            // Fix wildcards
            if (geneName!=null) {
                geneName = geneName.replace('*', '%');
                geneName = geneName.replace('?', '_');     
                geneName = "%"+geneName+"%";
            }
            
            TgDbCaller searchCaller = modelManager.getSearchCaller();
            
            Collection models_gene = modelManager.searchByGene(geneName, searchCaller);
            Collection models_ra = modelManager.searchByResearchApplication(raName, searchCaller);
            Collection models_prj  = modelManager.searchByProject(project, searchCaller);
            
            Collection models = new TreeSet();
            models.addAll(models_gene);
            models.addAll(models_ra);
            models.addAll(models_prj);
            
            
            logger.debug("---------------------------------------->SearchModelAction#performAction: models = "+models.size());
            
            request.setAttribute("models", models);
            
            return true;
        } catch (ApplicationException e) {
            logger.error("---------------------------------------->SearchModelAction#performAction: Failed");
            throw e;
        } catch (Exception e) {
            logger.error("---------------------------------------->SearchModelAction#performAction: Failed");
            throw new ApplicationException("SearchModelAction", e);
        }
    }
}
