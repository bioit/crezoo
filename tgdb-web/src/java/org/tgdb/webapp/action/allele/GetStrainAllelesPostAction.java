package org.tgdb.webapp.action.allele;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.frame.Navigator;

public class GetStrainAllelesPostAction extends TgDbAction {
    
    public GetStrainAllelesPostAction() {}

    public String getName() {
        return "GetStrainAllelesPostAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
//            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            String suid = req.getParameter("suid");
            
            if(isSubmit(req, "reset")) {                
                resetFormData(TgDbFormDataManagerFactory.STRAIN_ALLELES, req);
            }
            else{
                collectFormData(TgDbFormDataManagerFactory.STRAIN_ALLELES, TgDbFormDataManagerFactory.WEB_FORM, req);
                //once all data is collected get the FDM again
                FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.STRAIN_ALLELES, TgDbFormDataManagerFactory.WEB_FORM, req);
                
                if(isSubmit(req, "byID"))
                    fdm.put("ordertype", "ID");
                
                if(isSubmit(req, "byNAME"))
                    fdm.put("ordertype", "LINE NAME");
                
                if(isSubmit(req, "bySYMBOL"))
                    fdm.put("ordertype", "SYMBOL");
                
                if(isSubmit(req, "page")) {
                    Navigator nav = (Navigator)se.getAttribute("navigator");
                    nav.getPageManager().setCurrentPage(new Integer(fdm.getValue("page")).intValue());
                }
            }
                
            
            return true;

        } catch (Exception e) {
//            e.printStackTrace();
            throw new ApplicationException("Failed in models post action.");
        }
    }      
}
