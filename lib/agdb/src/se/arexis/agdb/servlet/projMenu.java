/*
 * @(#)projMenu.java	1.0 2000-10-09
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
import se.arexis.agdb.util.*;

/**
 *
 * Servlet writing the HTML-code for the menu in Arexis mainPage.
 *
 * @version 1.0, 2000-10-09
 */

public class projMenu extends SecureArexisServlet{


 /**
   * generates HTML-code to sent to client.
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
        Boolean bLoginOk;
	HttpSession session = req.getSession(false);
        int currentPrivs[] = (int[]) session.getValue("PRIVILEGES");
	String strUser = (String) session.getValue("UserID");
	bLoginOk = (Boolean) session.getValue("LoginOk");
	PrintWriter out = res.getWriter();

	// set content type and other response header fields first
        res.setContentType("text/html");
	out.println("<html>");
	out.println("<head>");
        HTMLWriter.css(out,getURL("style/axDefault.css"));
	out.println("<base target=\"content\">");
	out.println("</head>");
	out.println("<body background=\"" + getURL("images/menuback7.gif") + "\">");

        out.println("<table align=left border=0 cellPadding=1 cellSpacing=0 width=144>");
        out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>");
        out.println("<p>Projects</td>");
        out.println("<tr><td class=menuItem>");
        out.println("<a href=\"" + getServletPath("viewProj/options") + "\">Set</a><br>");

        out.println( privDependentString(currentPrivs, PROJECT_ADM,
            "<a href=\"" + getServletPath("viewProj/roles") + "\">Roles</a><br>",
            "&nbsp;&nbsp;&nbsp;<font color=tan>Roles</font><br>") );

        out.println( privDependentString(currentPrivs, PROJECT_ADM,
            "<a href=\"" + getServletPath("viewProj/users") + "\">Users</a><br>",
            "&nbsp;&nbsp;&nbsp;<font color=tan>Users</font><br>") );

        out.println( privDependentString(currentPrivs, PROJECT_STA,
            "<a href=\"" + getServletPath("viewProj/statistics") + "\">Statistics</a><br>",
            "&nbsp;&nbsp;&nbsp;<font color=tan>Statistics</font><br>") );
    
        out.println("<a href=\"" + getServletPath("viewProj/myaccount") + "\">My Account</a><br>");

        out.println("</td></tr>");


        out.println("<td bgcolor=\"#008B8B\" height=3>&nbsp;</td>");
        out.println("</table>");
	out.println("</body></html>");  
    }
}