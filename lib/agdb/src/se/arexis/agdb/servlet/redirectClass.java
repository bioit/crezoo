/*
 * @(#)redirectClass.java	1.0 2000-10-09
 *
 * Copyright (c) Prevas AB. All Rights Reserved.
 *
 * CopyrightVersion 1.0
 */

package se.arexis.agdb.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;


/**
 *
 * Servlet that handles redirect responses to a client.
 *
 * @version 1.0, 2000-10-09
 */
public class redirectClass extends HttpServlet {

private String m_loginPath;

 public void init() throws ServletException
 {
     m_loginPath = this.getServletContext().getInitParameter("loginPath");
     System.err.println("redirect:path=" + m_loginPath);
     System.err.println("redirectClass.init()");
 }

 /*
  public void init(ServletConfig config)throws ServletException
  {
      m_loginPath = config.getInitParameter("loginPath");
      System.err.println("redirect:path=" + m_loginPath );
      super.init(config);
  }
  */

/**
   * Method for constructing HTML-code and send back to client.
   * A redirect is sent back to the login page if client has not yet
   * successfully logged onto the system.
   * If the client has logged in ( a session exists) the redirect is made to the
   * mainpage.
   *
   * Overrides <code>HttpServlet.doGet</code> method.
   *
   * @param req HttpServletRequest that encapsulates the request to
   * the servlet
   * @param resp HttpServletResponse that encapsulates the response
   * from the servlet
   * @exception IOException if detected when handling the request
   * @exception ServletException if the request could not be handled
   *
   * @see javax.servlet.http.HttpServlet#doGet
   */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
					throws ServletException, IOException
        {
            System.err.println("redirectClass.doGet(req,res)");
		PrintWriter out = res.getWriter();
		HttpSession session = null;
		session = req.getSession(true);
		Boolean bLoginOk = null;
		bLoginOk = (Boolean) session.getAttribute("LoginOk");
		String pid = (String) session.getAttribute("PID");
		res.setContentType("text/html");

		out.println("<HTML>");
		out.println("<HEAD>");
		out.println("<SCRIPT LANGUAGE=\"JavaScript\">");
		out.println("<!--");
                
                
                System.err.println("Bef. redirection");
                
                //redirectPATH???
                //not logged in!
		if (bLoginOk == null || bLoginOk.booleanValue() == false) 
                {
                    System.err.println("bLoginOk == null | false!!! Redirecting to loginpage.");
                    //out.println("top.location.href='http://linlinux/roffeLogin.html';");
                    out.println("top.location.href='"+m_loginPath+"';");
		}
                // logged in but no project selected
                else if (pid == null) 
                {
                    out.println("top.location.href='mainPage?PAGE=SESSION';");
                }
                // logged in and project selected
                else 
                {
                    out.println("top.location.href='mainPage?PAGE=SESSION';");
		}
                
		out.println("// -->");
		out.println("</SCRIPT>");
		out.println("</HEAD");
		out.println("<BODY></BODY>");
		out.println("</HTML>");

	}


}