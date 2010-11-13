/*

 $Log$
 Revision 1.6  2003/05/02 07:58:45  heto
 Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
 Modified configuration and source files according to package change.

 Revision 1.5  2003/04/25 12:14:46  heto
 Changed all references to axDefault.css
 Source layout fixes.

 
*/

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.util.*;

public class genoMenu extends SecureArexisServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Boolean bLoginOk;
		HttpSession session = req.getSession(false);
    int currentPrivs[] = (int[]) session.getValue("PRIVILEGES");

		// set content type and other response header fields first
    res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		out.println("<html>\n"
			+ "<head>\n");
                HTMLWriter.css(out,getURL("style/axDefault.css"));
		out.println("<base target=\"content\">\n"
			+ "<title>meny</title>\n"
			+ "</head>\n"
      + "<body background=\"" + getURL("images/menuback7.gif") + "\"> \n");
     out.println("<table align=left border=0 cellPadding=1 cellSpacing=0 width=144>");

      //Marker Sets
    out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
    "<p>Marker sets</td>" + "<tr>");
    out.println("<td class=menuItem>");
    out.println(privDependentString(currentPrivs, MRKS_R,
      "<a href=\"" + getServletPath("viewMarkSet") + "\">View & Edit</a><br>",
      "&nbsp;&nbsp;&nbsp;<font color=tan>View & Edit</font><br>") );
    out.println(privDependentString(currentPrivs, MRKS_R,
      "<a href=\"" + getServletPath("viewMarkSet/membership") + "\">Membership</a><br>",
       "&nbsp;&nbsp;&nbsp<font color=tan>Membership</font><br>") );
    out.println(privDependentString(currentPrivs, MRKS_R,
      "<a href=\"" + getServletPath("viewMarkSet/position") + "\">Positions</a><br>",
      "&nbsp;&nbsp;&nbsp;<font color=tan>Positions</font><br>") );
    out.println(privDependentString(currentPrivs, MRKS_W,
      "<a href=\"" + getServletPath("viewMarkSet/impFile") + "\">File Import</a><br>",
      "&nbsp;&nbsp;&nbsp;<font color=tan>File Import</font><br>") );
    out.println("<br></td><tr>");

    //Markers
    out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
    "<p>Markers</td>" + "<tr>");
    out.println("<td class=menuItem>");
    out.println(privDependentString(currentPrivs, MRK_R,
      "<a href=\"" + getServletPath("viewMark") + "\">View & Edit</a><br>",
      "&nbsp;&nbsp;&nbsp;<font color=tan>View & Edit</font><br>") );
    out.println(privDependentString(currentPrivs, LMRK_R,
      "<a href=\"" + getServletPath("viewMark/impLib") + "\">Library Import</a><br>",
      "&nbsp;&nbsp;&nbsp;<font color=tan>Library Import</font><br>") );
    out.println(privDependentString(currentPrivs, MRK_W,
      "<a href=\"" + getServletPath("viewMark/impFile") + "\">File Import</a><br>",
      "&nbsp;&nbsp;&nbsp;<font color=tan>File Import</font><br>") );
    out.println("<br></td><tr>");

    
    //Genotypes
    out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
    "<p>Genotypes</td>" + "<tr>");
    out.println("<td class=menuItem>");
    out.println(privDependentString(currentPrivs, GENO_R,
      "<a href=\"" + getServletPath("viewGeno") + "\">View & Edit</a><br>",
      "&nbsp;&nbsp;&nbsp;<font color=tan>View & Edit</font><br>") );
    out.println(writeGenoPrivDependentString(currentPrivs, req,
      "<a href=\"" + getServletPath("viewGeno/impFile") + "\">File Import</a><br>" ,
      "&nbsp;&nbsp;&nbsp;<font color=tan>File Import</font><br>") );
    out.println(privDependentString(currentPrivs, GENO_R,
      "<a href=\"" + getServletPath("viewGeno/completion") + "\">Status</a><br>",
      "&nbsp;&nbsp;&nbsp;<font color=tan>Status</font><br>") );
    out.println(privDependentString(currentPrivs, GENO_R,
      "<a href=\"" + getServletPath("viewGeno/inheritCheck") + "\">Inheritance Check</a><br>",
      "&nbsp;&nbsp;&nbsp;<font color=tan>Inheritance Check</font><br>") );
    out.println(privDependentString(currentPrivs, GENO_R,
      "<a href=\"" + getServletPath("viewGeno/customView") + "\">Special Views</a><br>",
      "&nbsp;&nbsp;&nbsp;<font color=tan>Status</font><br>") );

    out.println("<br></td><tr>");

    
     //U-Marker Sets
    out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
    "<p>Unified Marker sets</td>" + "<tr>");
    out.println("<td class=menuItem>");
    out.println(privDependentString(currentPrivs, UMRKS_R,
      "<a href=\"" + getServletPath("viewUMarkSet") + "\">View & Edit</a><br>",
      "&nbsp;&nbsp;&nbsp<font color=tan>View & Edit</font><br>") );
    out.println(privDependentString(currentPrivs, UMRKS_R,
      "<a href=\"" + getServletPath("viewUMarkSet/membership") + "\">Membership</a><br>",
      "&nbsp;&nbsp;&nbsp<font color=tan>Membership</font><br>") );
    out.println(privDependentString(currentPrivs, UMRKS_R,
      "<a href=\"" + getServletPath("viewUMarkSet/position") + "\">Positions</a><br>",
      "&nbsp;&nbsp;&nbsp;<font color=tan>Positions</font><br>") );
    out.println(privDependentString(currentPrivs, UMRKS_W,
      "<a href=\"" + getServletPath("viewUMarkSet/impFile") + "\">File Import</a><br>",
      "&nbsp;&nbsp;&nbsp;<font color=tan>File Import</font><br>") );
    out.println("<br></td><tr>");

    // U-markers
    out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
    "<p>Unified Markers</td>" + "<tr>");
    out.println("<td class=menuItem>");
    out.println(privDependentString(currentPrivs, UMRK_R,
      "<a href=\"" + getServletPath("viewUMark") + "\">View & Edit</a><br>",
      "&nbsp;&nbsp;&nbsp;<font color=tan>View & Edit</font><br>") );
    out.println(privDependentString(currentPrivs, UMRK_W,
      "<a href=\"" + getServletPath("viewUMark/impFile") + "\">File Import</a><br>",
      "&nbsp;&nbsp;&nbsp;<font color=tan>File Import</font><br>") );
    out.println(privDependentString(currentPrivs, UMRK_W,
      "<a href=\"" + getServletPath("viewUMark/impMapping") + "\">Import Mapping</a><br>",
      "&nbsp;&nbsp;&nbsp<font color=tan>Import Mapping</font><br") );
    out.println("<br></td>");



		out.println("</table></body></html>");
	}
  private String writeGenoPrivDependentString(int[] privileges,
    HttpServletRequest req, String ifTrue, String ifFalse) {
    HttpSession session = req.getSession(true);
    int myHighestLevel = -1;
    boolean authorized = false;
    try {
      // Check if this is a valid request
      for (int i = 0; i < privileges.length; i++) {
        if (privileges[i] - GENO_W0 >= 0 &&
            GENO_W9 - privileges[i] >= 0 &&
            privileges[i] - GENO_W0 > myHighestLevel)
          myHighestLevel = privileges[i] - GENO_W0;

      }
      if (myHighestLevel >= 0)
        authorized = true;
    } catch (Exception e) {
      authorized = false;
    }
    if (authorized)
      return ifTrue;
    else
      return ifFalse;
  }
}
