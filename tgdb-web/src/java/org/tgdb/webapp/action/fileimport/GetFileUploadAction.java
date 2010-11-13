package org.tgdb.webapp.action.fileimport;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.frame.io.FileDataObject;
import org.tgdb.frame.io.WebFileUpload;

public class GetFileUploadAction extends TgDbAction {
    
    public GetFileUploadAction() {}
    
    public String getName() {
        return "GetFileUploadAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
//             HttpSession session = request.getSession();
             TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");
             WebFileUpload webFile = new WebFileUpload(request, 100000000);
             FileDataObject fileData = webFile.getFile("file");                        
             String upload = webFile.getFormParameter("upload");
//             int repo = Integer.parseInt(webFile.getFormParameter("repo"));
            
             if(exists(upload)) {
//                 modelManager.loadTgs(repo, fileData.getData(), _caller);
                 modelManager.loadMiceFromExcel(fileData.getData(), _caller);
            }
            
            return true;
        
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
