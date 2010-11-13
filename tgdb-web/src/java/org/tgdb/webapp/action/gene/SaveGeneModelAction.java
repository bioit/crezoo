package org.tgdb.webapp.action.gene;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class SaveGeneModelAction extends TgDbAction {
    
    public String getName() {
        return "SaveGeneModelAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {            
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");
            /* FIXME!!! - Kill this class??? */
            // Get variables
//            String name = request.getParameter("name");
//            String comm = request.getParameter("comm");
//            String mgiid = request.getParameter("mgiid");
//            String genesymbol = request.getParameter("genesymbol");
//            String geneexpress = request.getParameter("geneexpress");
//            String idgene = request.getParameter("idgene");
//            String idensembl = request.getParameter("idensembl");
//            String cid = request.getParameter("cid");
//            int i_cid = new Integer(cid).intValue();
            
//            int gaid = modelManager.createTransgene(name, comm, mgiid, genesymbol, geneexpress, idgene, idensembl, i_cid, _caller);

            
            return true;
        } catch (Exception e) {
            throw new ApplicationException("Failed to perform action", e);
        }
    }
}
