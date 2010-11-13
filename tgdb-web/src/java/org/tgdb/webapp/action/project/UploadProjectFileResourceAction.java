package org.tgdb.webapp.action.project;

import org.tgdb.frame.io.FileDataObject;
import org.tgdb.frame.io.WebFileUpload;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UploadProjectFileResourceAction extends TgDbAction {
    
    public UploadProjectFileResourceAction() {}
    
    public String getName() {
        return "UploadProjectFileResourceAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            
            logger.debug("---------------------------------------->UploadProjectFileResourceAction#performAction: Started");
            
            HttpSession session = request.getSession();            
            WebFileUpload webFile = new WebFileUpload(request, 100000000);
            
            FileDataObject fileData = webFile.getFile("file"); 
            String upload = webFile.getFormParameter("upload");
            String comm = webFile.getFormParameter("comm");            
            String name = webFile.getFormParameter("name");
                                    
            String pidId = webFile.getFormParameter("id");            
            String catId = webFile.getFormParameter("catId");
            
            if(exists(upload)) {                
                TgDbCaller _caller = (TgDbCaller)session.getAttribute("caller");        
                projectManager.addResource("file", Integer.parseInt(catId), Integer.parseInt(pidId), name, comm, fileData, _caller, null);
            }
            
            logger.debug("---------------------------------------->UploadProjectFileResourceAction#performAction: Ended");
            
            return true;
        } catch (ApplicationException e) {
            logger.error("---------------------------------------->UploadProjectFileResourceAction#performAction: Failed");
            throw e;
        } catch (Exception e) {
            logger.error("---------------------------------------->UploadProjectFileResourceAction#performAction: Failed");
            throw new ApplicationException("UploadProjectFileResourceAction",e);
        }
    }     
}
