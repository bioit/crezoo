package org.tgdb.webapp.action.model;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GetStrainAction extends TgDbAction {
    
    public GetStrainAction() {}

    public String getName() {
        return "GetStrainAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession se = req.getSession();
            TgDbCaller _caller = (TgDbCaller)se.getAttribute("caller");
            FormDataManager fdm = getFormDataManager(TgDbFormDataManagerFactory.STRAIN, TgDbFormDataManagerFactory.WEB_FORM, req);

            String strain_id = req.getParameter("strainid");

            if(exists(strain_id)) {
                fdm.put("strain_id", strain_id);
                //FIXME!!! - Add a nice generic method in TgDbAction class to refresh the formdatamanager
                se.setAttribute(TgDbFormDataManagerFactory.getInstanceName(TgDbFormDataManagerFactory.STRAIN), fdm);
            }
            else {
                strain_id = fdm.getValue("strain_id");
            }
            
            req.setAttribute("straindto", modelManager.getStrain(Integer.parseInt(strain_id), _caller, "superscript"));
            req.setAttribute("strainlinks", modelManager.getStrainLinks(Integer.parseInt(strain_id), _caller));
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not retrieve model.", e);
        }
    }     
}
