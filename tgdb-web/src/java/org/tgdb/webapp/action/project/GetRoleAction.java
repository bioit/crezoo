/*
 * GetRoleAction.java
 *
 * Created on July 19, 2005, 1:51 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action.project;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.project.projectmanager.RoleDTO;
import org.tgdb.project.role.RoleRemote;
import org.tgdb.project.role.RoleRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.webapp.action.*;


/**
 * TgDbAction class for retrieval of a role
 * @author heto
 */
public class GetRoleAction extends TgDbAction {
    
    /** Creates a new instance of GetRoleAction */
    public GetRoleAction() {
    }
    
    /**
     * Returns the name of this action
     * @return The name of this action
     */
    public String getName() {
        return "GetRoleAction";
    }
    
    /**
     * Performs this action
     * @param request The http request object
     * @param context The servlet context
     * @return True of this action could be performed
     */
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            
            
            String rid = request.getParameter("rid");
            if (rid!=null)
                request.getSession().setAttribute("rid", rid);
            else
                rid = (String)request.getSession().getAttribute("rid");
            
            
            RoleRemoteHome home = (RoleRemoteHome)locator.getHome(ServiceLocator.Services.ROLE);
            RoleRemote role = home.findByPrimaryKey(new Integer(rid));
            
            RoleDTO r = new RoleDTO(role);
            
            request.setAttribute("role", r);
            
            // Get the caller
            TgDbCaller caller = (TgDbCaller)request.getSession().getAttribute("caller");
            
            Collection privs = projectManager.getPrivileges(r.getRid(), caller);
            
            Collection restPrivs = projectManager.getOtherPrivileges(r.getRid(), caller);
            
            
            
            request.setAttribute("privs", privs);
            request.setAttribute("restPrivs", restPrivs);
            
            return true;
        } 
        catch (ApplicationException ae)
        {
            throw ae;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Failed to get role information.",e);
        }
    }
}
