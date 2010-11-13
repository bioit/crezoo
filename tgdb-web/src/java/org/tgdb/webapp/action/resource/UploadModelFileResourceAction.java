/*
 * UploadFileResourceAction.java
 *
 * Created on December 20, 2005, 12:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.resource;

import org.tgdb.frame.io.FileDataObject;
import org.tgdb.frame.io.WebFileUpload;
import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lami
 */
public class UploadModelFileResourceAction extends TgDbAction {
    
    /** Creates a new instance of UploadFileResourceAction */
    public UploadModelFileResourceAction() {
    }
    
    public String getName() {
        return "UploadFileResourceAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();            
            WebFileUpload webFile = new WebFileUpload(request, 100000000);
            FileDataObject fileData = webFile.getFile("file");                        
            String upload = webFile.getFormParameter("upload");
            String name = webFile.getFormParameter("name");
            String comm = webFile.getFormParameter("comm");
            int catid = new Integer(webFile.getFormParameter("catid")).intValue();
            
            if(exists(upload)) {                
                TgDbCaller caller = (TgDbCaller)session.getAttribute("caller");

                FormDataManager formDataManager = getFormDataManager(
                        TgDbFormDataManagerFactory.EXPMODEL, 
                        TgDbFormDataManagerFactory.WEB_FORM, 
                        request);             

                String eid = formDataManager.getValue("eid");
                modelManager.addFileResource(Integer.parseInt(eid), fileData.getFileName(), comm, fileData, catid, caller);
            }
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }      
}
