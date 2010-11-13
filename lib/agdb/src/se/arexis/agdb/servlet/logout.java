/*
 * @(#)logout.java	1.0 2000-10-09
 *
 * Copyright (c) Prevas AB. All Rights Reserved.
 *
 * CopyrightVersion 1.0
 */

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import se.arexis.agdb.util.Errors;
/**
 *
 * Servlet constructing the logout.html page for Arexis project.
 *
 * @version 1.0, 2000-10-09
 */

public class logout extends HttpServlet {

/**
   * Method for constructing HTML-code for the logout page.
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
            HttpSession session = req.getSession(true);
            String type = req.getParameter("type");
            Errors.log("User ["+session.getAttribute("UserSign")+"] logged out");
            String loginPath = getServletContext().getInitParameter("loginPath");
            session.invalidate();
            res.setContentType("text/html");
            PrintWriter out = res.getWriter();
            out.println("<HTML><HEAD>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + 
                getServletContext().getInitParameter("rootPath") + "\">");
            out.println("<TITLE>Logout</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<h3 align=center> You have been logged out from the application!<h3>");
            out.println("<br><br>");
            if (loginPath != null && !loginPath.trim().equals(""))
                out.println("<h3 align=center><a  href=\"" + loginPath + "\">Login</a><h3>");

            out.println("</BODY></HTML>");
	}

}

