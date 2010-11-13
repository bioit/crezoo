package org.tgdb.frame;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ILogin {
    
     /**
      * Handle login. 
      * 
      * Fist call:
      * If user has not logged in yet ArxLoginForward is used to 
      * forward user to the login view. 
      *
      * Second call: 
      * The user and password values are entered. If login is correct, Caller object is returned.
      * If it fails, the ArxLoginException is thrown to show an error page.
      *
      * @return Caller object with user info.
      * @param request 
      * @param response 
      * @throws org.tgdb.frame.ArxLoginForward is thrown then controller should redirect to the login page. Login page should be entered with the url message on exception object.
      * @throws org.tgdb.frame.ArxLoginException is thrown if something goes wrong, login failed, or denied
      */
     public Caller doLogin(HttpServletRequest request, 
            HttpServletResponse response) throws ArxLoginForward, ArxLoginException;
     
     public String getLoginView();
     
     public String getFirstWorkflow();
    
    
}
