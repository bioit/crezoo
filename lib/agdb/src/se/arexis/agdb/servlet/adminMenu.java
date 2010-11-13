/*
  $Log$
  Revision 1.3  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.2  2003/04/25 12:14:45  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.1.1.1  2002/10/16 18:14:04  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.2  2001/05/21 06:41:34  frob
  Indented the file, added log header and removed large comment.

*/

package se.arexis.agdb.servlet;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.util.*;

public class adminMenu extends AdminArexisServlet
{
   public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException
   {

      PrintWriter out = res.getWriter();

      // set content type and other response header fields first
      res.setContentType("text/html");
      out.println("<html>\n"
                  + "<head>\n");
      HTMLWriter.css(out,getURL("style/axDefault.css"));
      out.println(""  //<base target=\"content\">\n"
                  + "<title>meny</title>\n"
                  + "</head>\n"
                  + "<body background=\"" + getURL("images/menuback7.gif") + "\"> \n");
      out.println("<table align=left border=0 cellPadding=1 cellSpacing=0 width=144>");

      out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
                  "<p>Projects</td>" + "<tr>" + "<td class=menuItem>" +
                  "<a href=\""+getServletPath("adminProj")+"\" target=\"adminmainframe\">View & Edit</a><br>" +
                  "<br></font></td><tr>");


      out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
                  "<p>Users</td>" + "<tr>" + "<td class=menuItem>" +
                  "<a href=\""+getServletPath("adminUser")+"\" target=\"adminmainframe\">View & Edit</a><br>" +
                  "<br></font></td><tr>");

      out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
                  "<p>Species</td>" + "<tr>" + "<td class=menuItem>" +
                  "<a href=\""+getServletPath("adminSpecies")+"\" target=\"adminmainframe\">View & Edit</a><br>");
      //       out.println("<a href=\""+getServletPath("adminSpecies/impChrom")+"\" target=\"adminmainframe\">Import Chrom.</a><br><font size=1>");
      out.println("<br></font></td><tr>");



      out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
                  "<p>Library Markers</td>" + "<tr>" + "<td class=menuItem>" +
                  "<a href=\""+getServletPath("adminLMark")+"\" target=\"adminmainframe\">View & Edit</a><br>" +
                  "<a href=\""+getServletPath("adminLMark/impLMarkFile")+"\" target=\"adminmainframe\">Import from file</a><br><font size=1>" +
                  "<a href=\""+getServletPath("adminLMark/impLMarkSU")+"\" target=\"adminmainframe\">Import from s.u.</a><br><font size=1>" +
                  "<br></font></td><tr>");


      out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
                  "<p>Session</td>" + "<tr>" + "<td class=menuItem>" +

                  "<a href=\""+getServletPath("logout?type=admin")+"\" target=\"_top\">Logout</a><br><font size=1><br></font></td>\n<tr>\n");

      out.println("<td bgcolor=\"#008B8B\" height=3>&nbsp;</td>");
      out.println("</table>");
      out.println("</body></html>");
   }

}



