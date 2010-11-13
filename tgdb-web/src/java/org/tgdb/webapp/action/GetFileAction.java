package org.tgdb.webapp.action;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.PermissionDeniedException;
import org.tgdb.resource.file.FileRemote;
import org.tgdb.resource.file.FileRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


public class GetFileAction extends TgDbAction {
    
    public GetFileAction() {}
    
    public String getName() {
        return "GetFileAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            if (!caller.hasPrivilege("FILE_R"))
                throw new PermissionDeniedException("User "+caller.getName()+" is not allowed to view files. Privilege FILE_R is required.");
            
            
            Integer fileid = new Integer(workflow.getParameter("fileid"));
            FileRemoteHome fh = (FileRemoteHome)locator.getHome(ServiceLocator.Services.FILE);
            FileRemote file = fh.findByPrimaryKey(fileid);
            
            // Send the bean. This handles the file data array smarter.
            request.setAttribute("tmp.bean.file", file);
            
            return true;
        
        } 
        catch (ApplicationException e)
        {
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Unable to get file");
        }
    }
}
