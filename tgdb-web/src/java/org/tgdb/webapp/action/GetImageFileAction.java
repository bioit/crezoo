package org.tgdb.webapp.action;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.PermissionDeniedException;
import org.tgdb.resource.file.FileRemote;
import org.tgdb.resource.file.FileRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import java.io.ByteArrayInputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class GetImageFileAction extends TgDbAction {
    
    public GetImageFileAction() {}
    
    public String getName() {
        return "GetImageFileAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            if (!caller.hasPrivilege("FILE_R"))
                throw new PermissionDeniedException("User "+caller.getName()+" is not allowed to view files. Privilege FILE_R is required.");
            
            
            //Integer fileid = new Integer(workflow.getParameter("fileid"));
            //get the parameter
            Integer fileid = new Integer(request.getParameter("fileid"));
            //get the file bean
            FileRemoteHome fh = (FileRemoteHome)locator.getHome(ServiceLocator.Services.FILE);
            //get the specific file bean
            FileRemote file = fh.findByPrimaryKey(fileid);
            
            JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(new ByteArrayInputStream(file.getData()));
            
            
            // Send the bean. This handles the file data array smarter.
            //request.setAttribute("tmp.bean.file", file);
            
            return true;
        
        } 
        catch (ApplicationException e)
        {
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Unable to get image file");
        }
    }
}
