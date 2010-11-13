/*
 * $Log$
 * Revision 1.2  2003/05/02 07:58:45  heto
 * Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
 * Modified configuration and source files according to package change.
 *
 * Revision 1.1.1.1  2002/10/16 18:14:04  heto
 * Import of aGDB 1.5 L3 from Prevas CVS-tree.
 * This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson
 *
 *
 * Revision 1.5  2001/06/13 09:30:19  frob
 * Modified interfact of comment method in HTMLWriter, caused updates in several files.
 *
 * Revision 1.4  2001/05/28 06:34:08  frob
 * Adoption to changes in HTMLWriter.
 *
 * Revision 1.3  2001/05/17 11:23:46  frob
 * Removed parameter to menu- and mainframe servlets, also removed buildQueryString
 * as it no loger were used. Fixed HTML.
 *
 * Revision 1.2  2001/05/17 10:32:40  frob
 * Indented the file and added log header.
 *
 */

package se.arexis.agdb.servlet;
 
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.util.*;


/**
 * Builds the main page for administrators. The page is divided into two
 * frames, adminmenu and adminmainframe:
 *
 * <P><PRE>
 * +-----------+----------------------------+
 * | adminmenu | adminmainframe             |
 * |           |                            |
 * |           |                            |
 * |           |                            |
 * |           |                            |
 * |           |                            |
 * +-----------+----------------------------+
 * </PRE>
 * <P>
 * @author frob
 * @see AdminArexisServlet
 */
public class adminMain extends AdminArexisServlet
{


   /**
    * Writes the HTML for the page.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @exception IOException if an error occurs.
    * @exception ServletException if an error occurs
    */
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException
   {
      HttpSession session = request.getSession(true);

      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
		
      HTMLWriter.framesetDoctype(out);
      HTMLWriter.openHTML(out);
      HTMLWriter.openHEAD(out, "Administrators main frame");
      HTMLWriter.closeHEAD(out);
            
      HTMLWriter.comment(out, "Page contains a frameset with two frames",
                         true, false);
      out.println("<FRAMESET cols=\"150,*\" framespacing=0 border=0>");
      out.println("  <FRAME name=\"adminmenu\" src=\"" +
                  getServletPath("adminMenu") + 
                  "\" scrolling=\"auto\" marginheight=0 noresize frameborder=0>");
      out.println("  <FRAME name=\"adminmainframe\" " +
                  "src=\"" + getServletPath("adminProj") +
                  "\" scrolling=\"auto\" marginheight=0 frameborder=0>");
      out.println("  <NOFRAMES>\n" +
                  "    <BODY>\n" +
                  "      <P>This page uses frames, but your browser " +
                  "doesn't support them.</P>\n" +
                  "    </BODY>\n" +
                  "  </NOFRAMES>\n" +
                  "</FRAMESET>");
      HTMLWriter.closeHTML(out);
   }
}
