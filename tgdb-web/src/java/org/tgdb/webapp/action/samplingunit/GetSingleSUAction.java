/*
 * GetSingleSUAction.java
 *
 * Created on July 14, 2005, 4:43 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.samplingunit;

import org.tgdb.frame.Navigator;
import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.samplingunit.samplingunitmanager.SamplingUnitDTO;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;



/**
 * TgDbAction class for the retrieval of sampling units
 * @author lami
 */
public class GetSingleSUAction extends TgDbAction {
    
    /** Creates a new instance of GetSUAction */
    public GetSingleSUAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "GetSingleSUAction";
    }
    
    /**
     * Performs this action
     * @param request The http request object
     * @param context The servlet context
     * @return True if this action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");                        
            Navigator nav = (Navigator)request.getSession().getAttribute("navigator");
            
            FormDataManager formDataManager = getFormDataManager(
                    TgDbFormDataManagerFactory.SAMPLINGUNIT_DETAILS, 
                    TgDbFormDataManagerFactory.WEB_FORM, 
                    request);   
            
            String tmpSuid = request.getParameter("suid");            
            
            if(exists(tmpSuid)) {
                if(exists(formDataManager.getValue("suid"))) {                   
                    if(!tmpSuid.equals(formDataManager.getValue("suid"))) {
                        resetFormData(TgDbFormDataManagerFactory.SAMPLINGUNIT_DETAILS, request);
                    }              
                }
                
                formDataManager.put("suid", tmpSuid);
            }
            
            int suid = Integer.parseInt(formDataManager.getValue("suid"));
            
            collectFormData(TgDbFormDataManagerFactory.SAMPLINGUNIT_DETAILS, TgDbFormDataManagerFactory.WEB_FORM, request);                                                                                              
            
            SamplingUnitDTO samplingunit = samplingUnitManager.getSamplingUnit(caller, suid);
            Collection log = samplingUnitManager.getSamplingUnitHistory(caller, suid);                        
                
            
            request.setAttribute("samplingunitsingle", samplingunit);            
            request.setAttribute("samplingunitlog", log);
            request.setAttribute("files", resourceManager.getSamplingUnitFiles(suid, caller));
            request.setAttribute("links", resourceManager.getSamplingUnitLinks(suid, caller));
            request.setAttribute("resourceTree", samplingUnitManager.getResourceTreeCollection(suid, caller));
            
            return true;
        } 
        catch (ApplicationException ae)
        {
            throw ae;
        }
        catch (Exception e) {
            //e.printStackTrace();
            throw new ApplicationException("Failed to get sampling unit", e);
        }
    }
}
