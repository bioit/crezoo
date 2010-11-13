/*
 * GetFilesAction.java
 *
 * Created on September 15, 2005, 5:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author heto
 */
public class GetFilesAction extends TgDbAction {
    
    /** Creates a new instance of GetFilesAction */
    public GetFilesAction() {
    }
    
    public String getName() {
        return "GetFilesAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            // Do action code here:
            
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            Collection files = resourceManager.getAllFiles(caller);
            request.setAttribute("tmp.files", files);
            
            
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Unable to get file list");
        }
    }
}
