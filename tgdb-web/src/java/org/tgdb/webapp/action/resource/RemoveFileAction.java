package org.tgdb.webapp.action.resource;

import org.tgdb.frame.advanced.Workflow;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class RemoveFileAction extends TgDbAction {
    
    public String getName() {
        return "RemoveFileAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            String exid = request.getParameter("exid");
            request.getSession().setAttribute("exid", exid);
            int fileid = new Integer(request.getParameter("fileid")).intValue();
            
            modelManager.removeFile(fileid, caller);
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("RemoveFileAction failed to perform action", e);
        }
    }
}
