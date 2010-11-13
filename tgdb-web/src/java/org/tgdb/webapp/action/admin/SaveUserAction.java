package org.tgdb.webapp.action.admin;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SaveUserAction extends TgDbAction {
    
    public SaveUserAction() {}
    
    public String getName() {
        return "SaveUserAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ApplicationException {
        try {
            HttpSession session = req.getSession();
            TgDbCaller _caller = (TgDbCaller)session.getAttribute("caller");
            
            if (isSubmit(req, "create")) {
                String name = req.getParameter("name");
                String email = req.getParameter("email");
                String userLink = req.getParameter("userLink");
                
                /*
                String role = req.getParameter("rid");
                if(!exists(role))
                    throw new ApplicationException("Cannot create a project user without a role");
                 */
                
                String groupPhone = req.getParameter("groupPhone");
                String groupAddress = req.getParameter("groupAddress");
                String groupLink = req.getParameter("groupLink");
                String groupName = req.getParameter("groupName");
                
                String pwd = req.getParameter("pwd");
                String usr = req.getParameter("usr");
                adminManager.createUser(name, email, userLink, groupName, groupAddress, groupPhone, groupLink, usr, pwd, _caller);
            } else if (isSubmit(req, "save")) {
                String id = req.getParameter("id");
                String name = req.getParameter("name");
                String email = req.getParameter("email");
                /*
                String role = req.getParameter("rid");
                if(!exists(role))
                    throw new ApplicationException("Cannot store a project user without a role");                
                 **/
                String userLink = req.getParameter("userLink");
                
                String groupPhone = req.getParameter("groupPhone");
                String groupAddress = req.getParameter("groupAddress");
                String groupLink = req.getParameter("groupLink");                              
                
                String groupName = req.getParameter("groupName"); 
                String pwd = req.getParameter("pwd");
                String usr = req.getParameter("usr");                  
                adminManager.updateUser(Integer.parseInt(id), name, email, userLink, groupName, groupAddress, groupPhone, groupLink, _caller, usr, pwd);
                
                req.setAttribute("id", id);
            } else if(isSubmit(req, "remove")) {
                String id = req.getParameter("id");
                projectManager.removeUser(Integer.parseInt("id"), _caller);
            }

            return true;
        } catch (Exception e) {
            if(e instanceof ApplicationException)
                throw new ApplicationException(e.getMessage());
            else
                e.printStackTrace();
        }
        
        return false;
    }      
}
