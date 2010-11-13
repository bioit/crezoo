package org.tgdb.frame;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class NavigatorAction extends Action {
    
    public NavigatorAction() {}
          
    public String getName() {
        return "NavigatorAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) throws ActionException {
        return false;
    }
    
    public boolean performAction(HttpServletRequest req, HttpServletResponse res, ServletContext context) {
        try
        {
            HttpSession session = req.getSession();
        
            /** The navigator handles list behavior. Display a number of 
             * entries in a table. Not all at once.
             */
            Navigator nav = (Navigator)session.getAttribute("navigator");
            if (nav == null)
            {
                nav = new Navigator();
            }
            nav.setNavigator(req);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }
}
