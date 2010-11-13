/*
 * $Log$
 * Revision 1.4  2003/12/09 08:12:36  wali
 * Added result in the navigation bar.
 *
 * Revision 1.3  2003/05/02 07:58:45  heto
 * Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
 * Modified configuration and source files according to package change.
 *
 * Revision 1.2  2002/11/18 14:38:20  heto
 * Changed Text in menu
 *
 * Revision 1.1.1.1  2002/10/16 18:14:04  heto
 * Import of aGDB 1.5 L3 from Prevas CVS-tree.
 * This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson
 *
 *
 * Revision 1.6  2001/06/13 09:30:19  frob
 * Modified interfact of comment method in HTMLWriter, caused updates in several files.
 *
 * Revision 1.5  2001/05/28 06:34:08  frob
 * Adoption to changes in HTMLWriter.
 *
 * Revision 1.4  2001/05/18 06:17:23  frob
 * Now reads applicaton version from super class.
 *
 * Revision 1.3  2001/05/17 07:59:33  frob
 * Some modifications, restructured page, rewrote comments.
 *
 * Revision 1.2  2001/05/15 10:54:48  frob
 * Added log header and indented the file.
 *
 *
 * @(#)navigator.java	1.0 2000-10-09
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
 * This servlet writes the page in the topmost frame, the navigator
 * frame. The page in structured in three tables. The first table contains
 * the project-, user- and rolerinformation. The second table is used just
 * to add some spacing to the page. The third table contains an inner
 * table. This inner table contains the navigation texts.
 *
 * <P><PRE>
 * +---------+--------+-------+---------+---------+---------+
 * | empty   | proj   | user  | role    |  empty  |  Arexis |
 * +---------+--------+-------+---------+---------+---------+
 *
 * +--------------------------------------------------------+
 * | Empty table                                            |
 * +--------------------------------------------------------+
 *
 * +---------+---------------------------------------------+------+
 * | Empty   | +------+----+------+-----+-----+-----+-----+ | Ver |
 * |         | | Proj | SU | Phen | Gen | Ana | Res | Log | |     |
 * |         | +------+----+------+-----+-----+-----+-----+ |     |
 * +---------+---------------------------------------------+------+
 *  
 * @author frob
 * @see SecureArexisServlet
 */
public class navigator extends SecureArexisServlet
{

   /**
    * Generates the HTML-code to be loaded in the frame. The currently
    * selected navigation choice is highlighted.
    *
    * @param request The request object to read from.
    * @param response The respons object to write to.
    * @exception ServletException if an error occurs.
    * @exception IOException if an error occurs.
    */
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
   {

      HttpSession session = request.getSession(true);

      // It should be possible to get here eventhough the user hasn't
      // choosen a specific project.
      String projectName = (String) session.getValue("PNAME");
      String userRole = null;
      String requestedPage = request.getParameter("PAGE");
      if (requestedPage == null || "".equalsIgnoreCase(requestedPage))
      {
         requestedPage = new String("PROJECT");
      }
      
      if (projectName!= null)
      {
         userRole = (String) session.getValue("ROLE");
      }

      String userName = (String) session.getValue("UserName");

      // set content type and other response header fields first
      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");
      PrintWriter out = response.getWriter();

      HTMLWriter.comment(out, "This page can not have a doctype as " +
                         "Netscape 6 on Mac screws up then :-P", true, false);

      // Write the start of the header, including CSS part
      HTMLWriter.openHTML(out);
      HTMLWriter.openHEAD(out, "navigator");
      HTMLWriter.navigatorCSS(out);
      out.println("  <BASE target=\"contents\">");
      HTMLWriter.closeHEAD(out);
      
      HTMLWriter.openBODY(out, "background=\"" +
                          getURL("images/menuback6.gif") + "\"");

      // Page is structured in tree tables, this is the first one. Contains
      // six columns
      HTMLWriter.comment(out, "First table, contains project- "+
                         "user- and role information", true, false);
      out.println("<TABLE border=0 width=\"100%\" cellspacing=0 " +
                  "cellpadding=0>\n");
      out.println("  <TR>");

      HTMLWriter.comment(out, "Col 1 is emtpy", true, false);
      out.println("    <TD width=150 height=25></TD>");

      HTMLWriter.comment(out, "Col 2 contains project name", true, false);
      out.print("    <TD width=200 align=center>\n" +
                "      <FONT color=\"Black\" size=2>\n" +
                "        <B>\n" +
                "          Project: \n" +
                "          <FONT color=\"#008B8B\" size=2>");
      
      
      // Write the project name if set, else write default text 
      if (projectName != null)
      {
         out.print(projectName);
      }
      else
      {
         out.print("None selected");
      }

      // Write the end of the column
      out.println("</FONT>\n" +
                  "        </B>\n" +
                  "      </FONT>\n" +
                  "    </TD>");

      HTMLWriter.comment(out, "Col 3 contains the user name", true, false);
      out.println("    <TD width=300 align=center>\n" +
                  "      <FONT color=\"Black\" size=2>\n" +
                  "        <B>\n" +
                  "          User: \n" +
                  "          <FONT color=\"#008B8B\" size=2>" +
                  userName + "</FONT>\n" +
                  "        </B>\n" +
                  "      </FONT>\n" +
                  "    </TD>");
      
      HTMLWriter.comment(out, "Col 4 contains role name", true, false);
      out.print("    <TD width=200 align=center>\n" +
                "      <FONT color=\"Black\" size=2>\n" +
                "        <B>\n" +
                "          Role: \n" +
                "          <FONT color=\"#008B8B\" size=2>");
      
      // If project is selected, write the role
      if (projectName != null)
      {
         out.print(userRole);
      }

      // Write the end of the colum
      out.println("</FONT>\n" +
                  "        </B>\n" +
                  "      </FONT>\n" +
                  "    </TD>");

      HTMLWriter.comment(out, "Col 5 is empty", true, false);
      out.println("    <TD width=50 align=center>\n" +
                  "      <FONT color=\"Black\" size=2>\n" +
                  "        <B>&nbsp;</B>\n" +
                  "      </FONT>\n" +
                  "    </TD>");

      HTMLWriter.comment(out, "Col 6 contains Arexis link", true, false);
      out.println("    <TD width=30 align=center>\n" +
                  "      <A HREF=\"http://www.arexis.se\">\n" +
                  "        <FONT color=\"Black\" size=1>\n" +
                  "          <B>Arexis</B>\n" +
                  "        </FONT>\n" +
                  "      </A>\n" +
                  "    </TD>\n" +
                  "  </TR>\n" +
                  "</TABLE>");

      // Second table, just to get some space and make row as high as the
      // logo gif.
      HTMLWriter.comment(out, "Second table, just to get some space", true,
                         false);
      out.println("<TABLE border=0 width=\"100%\" " +
                  "cellspacing=0 cellpadding=0>");
      out.println("  <TR bgcolor=\"black\">\n " +
                  "    <TD width=\"*\" height=2></TD>\n" +
                  "  </TR>\n" +
                  "</TABLE>");

      // Third table, contains the navigation section text
      HTMLWriter.comment(out, "Third table, contains the navigation texts",
                         true, false);
      out.println("<TABLE border=0 width=\"100%\" " +
                  "cellspacing=\"0\" cellpadding=\"0\">\n" +
                  "  <TR bgcolor=\"Black\">");

      HTMLWriter.comment(out, "Col 1 is empty", true, false);
      out.println("    <TD width=150 height=23>&nbsp;</TD>"); 

      HTMLWriter.comment(out, "Col 2 contains a table", true, false);
      out.println("    <TD width=\"*\" colspan=\"0\">");

      HTMLWriter.comment(out, "Start of inner table", true, false);
      out.println("      <TABLE border=0 width=\"100%\" " +
                  "cellspacing=0 cellpadding=0 >\n"+
                  "        <TR>");

      // Write the columns in the table. Each column contains one of the
      // navigation texts.
      HTMLWriter.comment(out,"Col 1 contains Project", true, false);
      if ("SESSION".equalsIgnoreCase(requestedPage))
      {
         HTMLWriter.navigatorSelected(out, "SESSION", "Projects");
      }
      else
      {
         HTMLWriter.navigatorNotSelected(out, "SESSION", "Projects");
      }
      
      HTMLWriter.comment(out,"Col 2 contains Individuals", true, false);
      if ("INDIVIDUALS".equalsIgnoreCase(requestedPage))
      {
         HTMLWriter.navigatorSelected(out, "INDIVIDUALS",
                                      "Sampling units");
      }
      else
      {
         HTMLWriter.navigatorNotSelected(out, "INDIVIDUALS",
                                         "Sampling units");
      }

      HTMLWriter.comment(out,"Col 3 contains Phenotypes", true, false);
      if ("PHENOTYPES".equalsIgnoreCase(requestedPage))
      {
         HTMLWriter.navigatorSelected(out, "PHENOTYPES",
                                      "Phenotypes");
      }
      else
      {
         HTMLWriter.navigatorNotSelected(out, "PHENOTYPES",
                                         "Phenotypes");
      }

      HTMLWriter.comment(out,"Col 4 contains Genotypes", true, false);
      if ("GENOTYPES".equalsIgnoreCase(requestedPage))
      {
         HTMLWriter.navigatorSelected(out, "GENOTYPES",
                                      "Genotypes");
      }
      else
      {
         HTMLWriter.navigatorNotSelected(out, "GENOTYPES",
                                         "Genotypes");
      }
      
      HTMLWriter.comment(out,"Col 5 contains Analyses", true, false);
      if ("ANALYSES".equalsIgnoreCase(requestedPage))
      {
         //HTMLWriter.navigatorSelected(out, "Analyses","Analyses");
         HTMLWriter.navigatorSelected(out, "Analyses","Import/Export"); 
      }
      else
      {
         //HTMLWriter.navigatorNotSelected(out, "Analyses","Analyses");
         HTMLWriter.navigatorNotSelected(out, "Analyses","Import/Export"); 
      }
      
      
      HTMLWriter.comment(out,"Col 6 contains Results", true, false);
      if ("RESULTS".equalsIgnoreCase(requestedPage))
      {
         HTMLWriter.navigatorSelected(out, "Results","Results"); 
      }
      else
      {
         HTMLWriter.navigatorNotSelected(out, "Results","Results"); 
      }
      HTMLWriter.comment(out, "Col 7 contains Logout", true, false);
      out.println("          <TD width=\"11%\">\n"+
                  "            <A HREF=\"" + getServletPath("logout") +
                  "\" target=_top>\n" +
                  "              <B>Logout</B>\n" +
                  "            </A>\n" +
                  "          </TD>\n" +
                  "        </TR>\n" +
                  "      </TABLE>" );
      HTMLWriter.comment(out, "End of inner table", false, true);
      out.println("    </TD>");

      HTMLWriter.comment(out, "Col 3 contains version number", true, false);
      out.println("    <TD width=30 align=center>\n" +
                  "      <FONT size=1>" + applicationVersion() + "</FONT>\n" +
                  "    </TD>\n" +
                  "  </TR>\n" +
                  "</TABLE>\n" +
                  "</BODY>\n" +
                  "</HTML>");
   }
}
