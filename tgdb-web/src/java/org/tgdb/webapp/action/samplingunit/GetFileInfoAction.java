/*
 * GetFileInfoAction.java
 *
 * Created on December 7, 2005, 9:56 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.samplingunit;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author lami
 */
public class GetFileInfoAction extends TgDbAction {
    
    /**
     * Creates a new instance of GetFileInfoAction 
     */
    public GetFileInfoAction() {
    }


    /**
     * Returns the name of this action
     * @return The name of the action
     */
    public String getName() {
        return "GetFileInfoAction";
    }
    
    /**
     * Performs this action
     * @param request The http request object
     * @param context The servlet context
     * @return True if this action could be performed
     */
    public boolean performAction(HttpServletRequest req, ServletContext context) {
        try {
            TgDbCaller caller = (TgDbCaller)req.getSession().getAttribute("caller");            
            
            FormDataManager formDataManager = getFormDataManager(
                    TgDbFormDataManagerFactory.SAMPLINGUNIT_DETAILS, 
                    TgDbFormDataManagerFactory.WEB_FORM, 
                    req); 
            
            String tmpFileid = req.getParameter("fileid");            
            if(exists(tmpFileid))
                formDataManager.put("fileid", tmpFileid);
            
            req.setAttribute("filedto", resourceManager.getFile(Integer.parseInt(formDataManager.getValue("fileid")), caller));
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }      
}
