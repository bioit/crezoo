/*
 * GenModFileUploadAction.java
 *
 * Created on December 16, 2005, 9:49 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.model;

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

public class GenModFileUploadAction extends TgDbAction {
    
    public GenModFileUploadAction() {}
    
    public String getName() {
        return "GenModFileUploadAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();            
            WebFileUpload webFile = new WebFileUpload(request, 100000000);
            FileDataObject fileData = webFile.getFile("file");            
            String comm = webFile.getFormParameter("comm");            
            String upload = webFile.getFormParameter("upload");
            
            if(exists(upload)) {                
                TgDbCaller caller = (TgDbCaller)session.getAttribute("caller");

                FormDataManager formDataManager = getFormDataManager(
                        TgDbFormDataManagerFactory.EXPMODEL, 
                        TgDbFormDataManagerFactory.WEB_FORM, 
                        request);             

                logger.debug("---------------------------------------->GenModFileUploadAction#performAction: \n"+formDataManager.toString());
                
                String gmid = formDataManager.getValue("gmid");

                String name = fileData.getFileName();

                //int fileid = resourceManager.addFileToGeneticModification(Integer.parseInt(gmid), name, comm, fileData, caller);

                //formDataManager.put("fileid", ""+fileid);
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
