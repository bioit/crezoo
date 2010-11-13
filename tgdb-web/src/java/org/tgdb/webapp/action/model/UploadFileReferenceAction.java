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

public class UploadFileReferenceAction extends TgDbAction {
    
    public UploadFileReferenceAction() {}
    
    public String getName() {
        return "UploadFileReferenceAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();            
            WebFileUpload webFile = new WebFileUpload(request, 100000000);
            FileDataObject fileData = webFile.getFile("file");                        
            String upload = webFile.getFormParameter("upload");
            String name = webFile.getFormParameter("name");
            String comm = webFile.getFormParameter("comm");
            String pubmed = webFile.getFormParameter("pubmed");
            boolean primary = false;

            if(webFile.getFormParameter("primary").equalsIgnoreCase("true")) primary = true;
            
            if(exists(upload)) {                
                TgDbCaller _caller = (TgDbCaller)session.getAttribute("caller");

                FormDataManager formDataManager = getFormDataManager(
                        TgDbFormDataManagerFactory.EXPMODEL, 
                        TgDbFormDataManagerFactory.WEB_FORM, 
                        request);             

                String eid = formDataManager.getValue("eid");
//                modelManager.addFileReference(Integer.parseInt(eid), fileData.getFileName(), comm, fileData, pubmed, primary, _caller);
                modelManager.addFileReference(Integer.parseInt(eid), name, comm, fileData, pubmed, primary, _caller);
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
