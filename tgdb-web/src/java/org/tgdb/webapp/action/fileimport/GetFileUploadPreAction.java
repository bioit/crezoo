package org.tgdb.webapp.action.fileimport;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.Part;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.frame.io.FileDataObject;
import org.tgdb.frame.io.WebFileUpload;

public class GetFileUploadPreAction extends TgDbAction {
    
    public GetFileUploadPreAction() {}
    
    public String getName() {
        return "GetFileUploadPreAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            request.setAttribute("repos", modelManager.getRepositoriesByDB());
            return true;
        
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
