package org.tgdb.webapp.action.samplingunit;

import org.tgdb.frame.Navigator;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.project.ParamCollector;
import org.tgdb.project.ParamDataObject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;

public class SUPostAction extends TgDbAction {
    
    public SUPostAction() {}
    
    public String getName() {
        return "SUPostAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            String tmpSid = request.getParameter("sid");
            
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");
            Navigator nav = (Navigator)request.getSession().getAttribute("navigator");
            
            int sid = 0;
            if(tmpSid != null) {
                sid = Integer.parseInt(tmpSid);
                _caller.setSid(sid);
            }
            
            ParamCollector pc = new ParamCollector(true);
            pc.putDefault("status", "E");
            pc.putDefault("sid", ""+sid);
            
            ParamDataObject pdo = pc.collectParams(request, "getsufullaction", nav.getPageManager());
            request.getSession().setAttribute("samplingunits.pdo", pdo);
            
            logger.debug("---------------------------------------->SUPostAction#performAction: pdo = "+pdo);
            
            return true;
       } catch (Exception e) {
            logger.error("---------------------------------------->SUPostAction#performAction: Failed");
            throw new ApplicationException("SUPostAction",e);
        }
    }
}
