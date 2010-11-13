/*
 * $Log$
 * Revision 1.4  2003/12/09 07:56:39  wali
 * Extended with the servletspaths to resMenu and viewRes
 *
 * Revision 1.3  2003/05/02 07:58:45  heto
 * Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
 * Modified configuration and source files according to package change.
 *
 * Revision 1.2  2002/10/18 11:41:09  heto
 * Replaced Assertion.assert with Assertion.assertMsg
 *
 * Java 1.4 have a keyword "assert".
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
 * Revision 1.4  2001/05/17 08:03:34  frob
 * Restructured HTML, rewrote comments.
 *
 * Revision 1.3  2001/05/15 10:52:23  frob
 * Modified layout and restructuring.
 *
 * Revision 1.2  2001/05/15 08:06:49  frob
 * Added log header and indented the file.
 *
 *
 * @(#)mainPage.java	1.0 2000-10-09
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
 * Writes the main page used in the application. The page contains three
 * framesets. The first one splits the page in two rows. The second one
 * splits the first row in two columns, one for the navigation page and one
 * for the arexis logo. The third frameset splits the second row in two
 * columns, one for the menu and one for the content page:
 * <P>
 * <PRE>
 * +--------------------------------------+
 * | +--------------------------+-------+ |
 * | | navigator                | logo  | |
 * | +--------------------------+-------+ |
 * +------+-------------------------------+
 * | +------+---------------------------+ |
 * | | menu |  content                  | |
 * | |      |                           | |
 * | |      |                           | |
 * | |      |                           | |
 * | |      |                           | |
 * | |      |                           | |
 * | |      |                           | |
 * | |      |                           | |
 * | |      |                           | |
 * | +------+---------------------------+ |
 * +--------------------------------------+
 * </PRE>
 *
 * @author frob
 * @see SecureArexisServlet
 */
public class mainPage extends SecureArexisServlet
{

   /**
    * Builds the HTML for the page.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @exception ServletException If request could not be handled.
    * @exception IOException If error handling request.
    */
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
   {
      HttpSession session = request.getSession(false);
      PrintWriter out = response.getWriter();
      String requestedPage = request.getParameter("PAGE");
      if (requestedPage == null || "".equalsIgnoreCase(requestedPage) )
      {
         requestedPage = new String("SESSION");
      }
      // set content type and other response header fields first
      response.setContentType("text/html");

      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 "
                  + "Frameset//EN\"\n"
                  + "  \"http://www.w3.org/TR/REC-html40/frameset.dtd\">\n");
      
      HTMLWriter.openHEAD(out, "Arexis Main Page");
      HTMLWriter.closeHEAD(out);
      HTMLWriter.comment(out, "Page is split horizontally in two framesets, "
                         + "one that contains the navigator and a logo and "
                         + "another which contains the left menu as well as the "
                         + "page content. Non-standart tags FRAMESPACING and "
                         + "BORDER has to be used to remove the frame "
                         + "borders in all browsers :-P", true, false);
      
      out.println("<FRAMESET rows=\"50,*\" framespacing=0 border=0>");
      HTMLWriter.comment(out, "Create the topmost frameset with the navigator "
                         + " and the logo in separate frames.", true, false);
      
      out.println("  <FRAMESET cols=\"*,20\" framespacing=0 border=0>");

      // Call the navigator servlet with the name of the requested page
      out.println("    <FRAME name=\"navigator\" "
                  + " src=\"" + getServletPath("navigator") + "?PAGE="
                  + requestedPage + "\"" + " marginheight=0  "
                  + " frameborder=0 noresize scrolling=no>");
      out.println("    <FRAME name=\"logo\" src=\"" + getURL("logo.jsp")+ "\""
                  + " marginheight=0 frameborder=0  noresize scrolling=no>\n"
                  + "  </FRAMESET>");
      
      HTMLWriter.comment(out, "Create the second frameset with the menu and the "
                         + "contents in separate frames.", true, false);
      out.println("  <FRAMESET cols=\"150,*\" framespacing=0 border=0>");

      // Write the contents of menu frame. This is done by calling a
      // servlet. Which servlet to call is decided by the name of the 
      // requested page.
      out.print("    <FRAME name=\"menu\" ");
      if ("SESSION".equalsIgnoreCase(requestedPage))
      {
         out.print("src=\"" + getServletPath("projMenu")+ "\" ");
      }
      else if ("INDIVIDUALS".equalsIgnoreCase(requestedPage))
      {
         out.print("src=\"" + getServletPath("indMenu")+ "\" ");
      }
      else if ("GENOTYPES".equalsIgnoreCase(requestedPage))
      {
         out.print("src=\"" + getServletPath("genoMenu")+"\" ");
      }
      else if ("PHENOTYPES".equalsIgnoreCase(requestedPage))
      {
         out.print("src=\"" + getServletPath("phenMenu")+"\" ");
      }
      else if ("ANALYSES".equalsIgnoreCase(requestedPage))
      {
         out.print("src=\"" + getServletPath("anaMenu")+"\" ");
      }
      else if ("RESULTS".equalsIgnoreCase(requestedPage))
      {
         out.print("src=\"" + getServletPath("resMenu")+"\" ");
      }
      else
      {
         System.err.println("Unknown page requested for " +
                            "menu frame: " + requestedPage);
      }
      
      out.print(" marginheight=0 frameborder=0 scrolling=\"auto\">\n");

      // Write the contents of content frame. This is done by calling a
      // servlet. Which servlet to call is decided by the name of the 
      // requested page. 
     out.print("    <FRAME name=\"content\" ");
      if ("SESSION".equalsIgnoreCase(requestedPage))
      {
         out.print("src=\"" + getServletPath("viewProj/options")+ "\"");
      }
      else if ("INDIVIDUALS".equalsIgnoreCase(requestedPage))
      {
         out.print("src=\"" + getServletPath("viewInd")+"\"");
      }
      else if ("GENOTYPES".equalsIgnoreCase(requestedPage))
      {
         out.print("src=\"" + getServletPath("viewGeno") + "\"");
      }
      else if ("PHENOTYPES".equalsIgnoreCase(requestedPage))
      {
         out.print("src=\"" + getServletPath("viewPheno") + "\"");
      }
      else if ("ANALYSES".equalsIgnoreCase(requestedPage))
      {
         out.print("src=\"" + getServletPath("viewFilt") + "\"");
      }
      else if ("RESULTS".equalsIgnoreCase(requestedPage))
      {
         out.print("src=\"" + getServletPath("viewRes") + "\"");
      }
      else
      {
         System.err.println("Unknown page requested for " +
                            "menu frame: " + requestedPage);
      }

      out.print(" marginheight=6 frameborder=0 "
                + "scrolling=\"auto\">\n");
      out.println("  </FRAMESET>");
      
      HTMLWriter.comment(out, "Write the noframe part", true, false);
      out.println("  <NOFRAMES>\n"
                  + "    <BODY>\n"
                  + "      <P>"
                  + "This page uses frames, but your browser doesn't support them."
                  + "</P>\n"
                  + "    </BODY>\n"
                  + "  </NOFRAMES>\n"
                  + "</FRAMESET>\n"
                  + "</HTML>");
   }
}
