/*
 * @(#)projMenu.java	1.0 2000-10-09
 *
 * Copyright (c) Prevas AB. All Rights Reserved.
 *
 * CopyrightVersion 1.0
 *
 * $Log$
 * Revision 1.13  2004/04/02 07:13:23  wali
 * bugfix
 *
 * Revision 1.12  2004/03/30 14:20:58  wali
 * Layout improved
 *
 * Revision 1.11  2004/03/25 13:21:07  wali
 * changed the information button
 *
 * Revision 1.10  2004/03/24 07:40:19  wali
 * Added information symbol
 *
 * Revision 1.9  2004/03/19 13:07:20  wali
 * Added help commands.
 *
 * Revision 1.8  2004/03/17 15:59:12  wali
 * Changed View&Edit to View&Import and took Check&Import away
 *
 * Revision 1.7  2004/03/16 10:17:28  wali
 * Changed the menu names.
 *
 * Revision 1.6  2003/05/02 07:58:45  heto
 * Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
 * Modified configuration and source files according to package change.
 *
 * Revision 1.5  2003/04/25 12:14:46  heto
 * Changed all references to axDefault.css
 * Source layout fixes.
 *
 * Revision 1.4  2002/11/21 10:50:08  heto
 * Changed menu alternatives
 *
 * Revision 1.3  2002/11/18 14:39:33  heto
 * changed name of servlet from import to importFile
 *
 * Revision 1.2  2002/11/18 09:33:47  heto
 * Fixed source layout.
 * Added import menu.
 * Added log to the file.
 *
 *
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
public class anaMenu extends SecureArexisServlet
{
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
	PrintWriter out = res.getWriter();

	// set content type and other response header fields first
        res.setContentType("text/html");
	out.println("<html>");
	out.println("<head>");
	HTMLWriter.css(out,getURL("style/axDefault.css"));
	out.println("<base target=\"content\">");
	out.println("</head>");
	out.println("<body background=\"" + getURL("images/menuback7.gif") + "\">");

       //width=144>  bgcolor=\"#008B8B\"
        out.println("<table align=left border=0 cellPadding=1 cellSpacing=0 width=144>");
        // Filters
        out.println("<tr><td bgcolor=\"#008B8B\" class=menuTitle> ");
        out.println("Filters</td><td bgcolor=\"#008B8B\"><a href=\""+getServletPath("help.html#filter")+"\" target=_help> "+
                    "<img border=0 src=\"images/i.png\"></a></td></tr>");
       
        out.println("<tr><td class=menuItem>");
        out.println( privDependentString(currentPrivs, FLT_R,
            "<a href=\"" + getServletPath("viewFilt") + "\">View & Edit</a><br>",
            "&nbsp;&nbsp;&nbsp;<font color=tan>View & Edit</font><br>") );
        out.println("<br>");
        out.println("</td><td class=menuItem>&nbsp;</td></tr>");
        //class=menuTitle
       
        
        // Export 
        out.println("<tr><td bgcolor=\"#008B8B\" class=menuTitle>");
        out.println("Export</td><td bgcolor=\"#008B8B\"><a href=\""+getServletPath("help.html#exporting")+"\" target=_help>"+
                    "<img border=0 src=\"images/i.png\"></a></td><tr>");
       
        out.println("<tr><td class=menuItem>");
        out.println( privDependentString(currentPrivs, ANA_R,
            "<a href=\"" + getServletPath("viewFile") + "\">View & Edit</a><br>",
            "&nbsp;&nbsp;&nbsp;<font color=tan>View & Edit</font><br></td><td class=menuItem>&nbsp;</td></tr><tr><td>") );
        out.println( privDependentString(currentPrivs, ANA_R,
            "<a href=\"" + getServletPath("viewFile/start") + "\">Export file</a><br>",
            "&nbsp;&nbsp;&nbsp;<font color=tan>Start new</font><br>") );
        out.println("</td><td class=menuItem>&nbsp;</td></tr>");
    
    
        // Import
        out.println("<tr><td bgcolor=\"#008B8B\" class=menuTitle>");        
        out.println("Import </td><td bgcolor=\"#008B8B\"><a href=\""+getServletPath("help.html#import")+"\" target=_help>"+
                    "<img border=0 src=\"images/i.png\"></a></td><tr>");
        out.println("<tr><td class=menuItem>");
        
        out.println( privDependentString(currentPrivs, ANA_R,
            "<a href=\"" + getServletPath("importFile") + "\">View&nbsp;&&nbsp;Import</a><br>",
            "&nbsp;&nbsp;&nbsp;<font color=tan>List Files</font><br></td><td class=menuItem>&nbsp;</td></tr><tr><td>") );
        out.println( privDependentString(currentPrivs, ANA_R,
            "<a href=\"" + getServletPath("importFile/upload") + "\">Upload File</a><br>",
            "&nbsp;&nbsp;&nbsp;<font color=tan>Upload File</font><br>") );
        out.println("</td><td class=menuItem>&nbsp;</td></tr>");
    
      //<
        out.println("<td bgcolor=\"#008B8B\" height=3>&nbsp;</td><td bgcolor=\"#008B8B\">&nbsp;</td></tr>");
  
        out.println("</table>");
	out.println("</body></html>");
    }
}