package org.tgdb.webapp.action;

import org.tgdb.TgDbCaller;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.project.BeginAction;

public class LogoutAction extends TgDbAction {

    public LogoutAction() {}
    
    public String getName() {
        return "LogoutAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) {
        try {
           
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");
            String callerStr = _caller.getId()+", "+_caller.getName();
            
            projectManager.log("Logout"+
                        "\nQuery String:"+request.getQueryString()+
                        "\nPath: "+request.getPathInfo()+
                        "\nCaller:"+callerStr+
                        "\nRemote adress: "+request.getRemoteAddr()+
                        "\nRemote host: "+request.getRemoteHost());   
            
            request.getSession().invalidate();
            
            //begin again
            BeginAction _begin = new BeginAction();
            _begin.performAction(request, context);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
