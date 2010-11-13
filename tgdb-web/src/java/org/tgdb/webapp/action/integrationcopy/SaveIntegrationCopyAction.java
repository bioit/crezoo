package org.tgdb.webapp.action.integrationcopy;

import org.tgdb.frame.io.FileDataObject;
import org.tgdb.frame.io.WebFileUpload;
import org.tgdb.frame.advanced.Workflow;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SaveIntegrationCopyAction extends TgDbAction {
    
    public String getName() {
        return "SaveIntegrationCopyAction";
    }
    
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = request.getSession();            
            //WebFileUpload webFile = new WebFileUpload(request, 100000000);
            String upload = request.getParameter("upload");
            //String upload = webFile.getFormParameter("upload");
            
            if(exists(upload)) {
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            
            if (request.getParameter("iscmid")!=null)
            {
                int iscmid = new Integer(request.getParameter("iscmid")).intValue();
                String isite = request.getParameter("isite");
                String cnumber = request.getParameter("cnumber"); 
                    
                modelManager.updateIntegrationCopy(iscmid, isite, cnumber, caller);
            }
            else
            {
                String isite = request.getParameter("isite");
                String cnumber = request.getParameter("cnumber"); 
                
                String eid = request.getParameter("eid");
                
                int iscmid = modelManager.createIntegrationCopy(isite, cnumber);
                
                modelManager.addIntegrationCopyToModel(new Integer(eid).intValue(), iscmid, caller);
            }
            
            }
            
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("SaveIntegrationCopyAction Failed to perform action", e);
        }
    }
}
