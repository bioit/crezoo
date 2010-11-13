package org.tgdb.webapp.action.allele;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.form.FormDataManager;

public class RemoveStrainAlleleAction extends TgDbAction {
    
    public String getName() {
        return "RemoveStrainAlleleAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");

            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.EXPMODEL, TgDbFormDataManagerFactory.WEB_FORM, request);
            int eid = Integer.parseInt(fdm.getValue("eid"));
            
            String strain_allele = request.getParameter("strainalleleid");
            
            modelManager.removeStrainAllele(eid, Integer.parseInt(strain_allele),_caller);
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("RemoveStrainAlleleAction failed to perform action", e);
        }
    }
}
