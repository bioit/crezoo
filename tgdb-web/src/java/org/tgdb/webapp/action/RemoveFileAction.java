package org.tgdb.webapp.action;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class RemoveFileAction extends TgDbAction {
    
    public RemoveFileAction() {}
    
    public String getName() {
        return "RemoveFileAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            
            ConfirmAction c = new ConfirmAction();
            c.performAction(request, context);
            
            String tmp = (String)request.getSession().getAttribute("tmp.preconfirmremoveaction.fileid");
            
            logger.debug("---------------------------------------->RemoveFileAction#performAction: fileid = "+tmp);
            
            int fileId = new Integer(tmp).intValue();

            resourceManager.removeFile(fileId, (TgDbCaller)caller);
            
            request.getSession().removeAttribute("tmp.preconfirmremoveaction.fileid");
            
            return true;
        } catch (ApplicationException e) {
            logger.error("---------------------------------------->RemoveFileAction#performAction: Failed");
            throw e;
        } catch (Exception e) {
            logger.error("---------------------------------------->RemoveFileAction#performAction: Failed");
            throw new ApplicationException("RemoveFileAction", e);
        }
    }
}
