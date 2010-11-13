/*
 * FileUploadPreAction.java
 *
 * Created on December 15, 2005, 9:34 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action;

import org.tgdb.exceptions.ApplicationException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author lami
 */
public class FileUploadPreAction extends TgDbAction {
    
    /**
     * Creates a new instance of FileUploadPreAction 
     */
    public FileUploadPreAction() {
    }
    
    public String getName() {
        return "FileUploadPreAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {            
            
            // Fetches the id of the object that the file should be attached to
            request.setAttribute("id", request.getParameter("id"));
            
            return true;
        
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failure in fileupload preaction");
        }
    }    
}
