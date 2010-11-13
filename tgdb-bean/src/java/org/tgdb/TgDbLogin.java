package org.tgdb;

import org.tgdb.frame.ArxLoginException;
import org.tgdb.frame.ArxLoginForward;
import org.tgdb.frame.Caller;
import org.tgdb.frame.ILogin;
import org.tgdb.project.projectmanager.ProjectManagerRemote;
import org.tgdb.servicelocator.ServiceLocator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TgDbLogin implements ILogin {
    
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TgDbLogin.class);
    
    public TgDbLogin() {}
    
    public String getLoginView() {
//        return "/tgdb.jsp";
        return "/welcome.jsp";
    }
    
    public String getFirstWorkflow() {
        return "Controller?workflow=begin";
    }
    
    public boolean validUP(HttpServletRequest request){
        boolean isvalid = true;
        
        if (request.getParameter("usr")==null || request.getParameter("pwd")==null
            || request.getParameter("usr").length()==0 || request.getParameter("pwd").length()==0
            || request.getParameter("usr").trim().length()==0 || request.getParameter("pwd").trim().length()==0
            || request.getParameter("usr").trim()==null || request.getParameter("pwd").trim()==null){
            isvalid = false;
            return isvalid;
        }
        
        return isvalid;
    }
    
    public Caller doLogin(HttpServletRequest request, HttpServletResponse response) throws ArxLoginException, ArxLoginForward {
        //logger.debug("---------------------------------------->TgDbLogin#doLogin()");
        TgDbCaller caller = new TgDbCaller();
        try 
        {
            if(!validUP(request))
            {
                if(request.getSession().getAttribute("caller")==null){
                    ServiceLocator locator = ServiceLocator.getInstance();
                    ProjectManagerRemote prjManager = (ProjectManagerRemote)locator.getManager(ServiceLocator.Services.PROJECTMANAGER);
                
                    caller = prjManager.login("public", "notknown");
                    caller.updatePrivileges();
                    prjManager.log("user:"+request.getParameter("usr")+" & pwd:"+request.getParameter("pwd")+". Bad Login"+" "+request.getRemoteAddr()+" "+request.getRemoteHost());
                    return caller;
                } else {
                    return (Caller) request.getSession().getAttribute("caller");
                }
            } else {
                String usr = request.getParameter("usr");
                String pwd = request.getParameter("pwd");

                ServiceLocator locator = ServiceLocator.getInstance();
                ProjectManagerRemote prjManager = (ProjectManagerRemote)locator.getManager(ServiceLocator.Services.PROJECTMANAGER);

                caller = prjManager.login(usr, pwd);
                
                if(caller!=null){
                    caller.updatePrivileges();
                    prjManager.log("user "+caller.getName()+" logged in."+" Login "+caller.getName()+" "+request.getRemoteAddr()+" "+request.getRemoteHost());
                    logger.debug("---------------------------------------->TgDbLogin#doLogin(): Correct input for "+caller.getName());
                }else{
                    if(request.getSession().getAttribute("caller")==null){
                        caller = prjManager.login("public", "notknown");
                        caller.updatePrivileges();
                        logger.debug("---------------------------------------->TgDbLogin#doLogin(): Incorrect login input");
                    }else{
                        return (Caller) request.getSession().getAttribute("caller");
                    }
                }
                return caller;
            }
        }
        catch (Exception e) 
        {
           throw new ArxLoginException("TgDbLogin#doLogin(...): Login method failed", e);
        }
    }
}
