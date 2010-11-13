/*
$Log$
Revision 1.4  2003/05/02 07:58:45  heto
Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
Modified configuration and source files according to package change.

Revision 1.3  2003/04/25 12:14:46  heto
Changed all references to axDefault.css
Source layout fixes.

 
*/

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.util.*;

public class loginError extends ArexisServlet
{

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException 
    {
        res.setContentType("text/html");
	res.setHeader("Pragma", "no-cache");
	res.setHeader("Cache-Control", "no-cache");
	PrintWriter out = res.getWriter();
	try 
        {
            // System.err.println(req.getQueryString());
            String errorType =req.getParameter("error");
            String message;
            if (errorType.equals("dbase"))
            {
                message = "No connection to the database available";
            }
            else if (errorType.equals("noUSR"))
            {
                message = "No matching set of credentials found (username or password incorrect)";
            }
            else //(errorType.equals("undef"))
            {
                message = "Unknown error";
            }


            out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Arexis GDB - Login error</title>");
            HTMLWriter.css(out,getURL("style/axDefault.css"));
            out.println("</head>");
            out.println("<body>");
            out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                "<tr>" +
                "<td width=\"14\" rowspan=\"3\"></td>" +
                "<td width=\"736\" colspan=\"2\" height=\"15\">");
            out.println("<center><b style=\"font-size: 15pt\">");
            out.println("Arexis GDB - Login Error");
            out.println("</b></center></font></td></tr>" +
                "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                "</tr></table>");
            out.println("<table cellspacing=0 cellpadding=0><tr>" +
                "<td width=15></td><td>");
            out.println("<p>");
            out.println("<p>Error message:<br><br>" + message);
            out.println("</td></tr>");

            out.println("<tr><td></td><td></td></tr><td></td><td>");
            out.println("<form>");
      
      
            //getURL("login.html")
      
            out.println("<input type=button value=Back width=100 " +
                "style=\"WIDTH: 100px\" " +
                "onClick='JavaScript:location.href=\"" + getServletContext().getInitParameter("loginPath") + "?" +
                "\";'>&nbsp;");
            out.println("</form>");
            out.println("</td></tr></table>");
            out.println("</body>");
            out.println("</html>");
        } 
        catch (Exception e)	
        {
            e.printStackTrace(System.err);
        }
    }
}

