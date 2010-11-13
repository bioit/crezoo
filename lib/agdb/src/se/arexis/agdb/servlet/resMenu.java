/*
 * resMenu.java
 *
 * Created on den 7 november 2003, 14:14
 */

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.util.*;

/**
 *
 * @author  wali
 */
public class resMenu extends SecureArexisServlet{
    
    /** Creates a new instance of resMenu */
    // public resMenu() {
    //}
    
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

        // Results
        out.println("<table nowrap align=left border=0 cellPadding=1 cellSpacing=0 width=144>");
        out.println("<tr><td bgcolor=\"#008B8B\" class=menuTitle>");
        out.println("Results</td><td bgcolor=\"#008B8B\"><a href=\""+getServletPath("help.html#result")+"\" target=_help> "+
                    "<img border=0 src=\"images/i.png\"></a></td></tr>");//
        
        out.println("<tr><td >");//class=menuItem
        out.println( privDependentString(currentPrivs, RES_W,
            "<a href=\"" + getServletPath("viewRes") + "\">View & Edit</a><br>",
            "&nbsp;&nbsp;&nbsp;<font color=tan>View & Edit</font><br>") );
        out.println("<br>");
        out.println("</td><td >&nbsp;</td></tr>");//class=menuItem
        
      
        // Category
        out.println("<tr><td bgcolor=\"#008B8B\" class=menuTitle>");
        out.println("<p>Category</td><td bgcolor=\"#008B8B\"><a href=\""+getServletPath("help.html#category")+"\" target=_help>"+
                    "<img border=0 src=\"images/i.png\"></a></td><tr>");
        
        out.println("<tr><td class=menuItem>");
        out.println( privDependentString(currentPrivs, CTG_W,
            "<a href=\"" + getServletPath("viewCTG/new") + "\">New Category</a><br>",
            "&nbsp;&nbsp;&nbsp;<font color=tan>New Category</font><br></td><td class=menuItem>&nbsp;</td></tr><tr><td>") );
        
        out.println( privDependentString(currentPrivs, CTG_W,
            "<a href=\"" + getServletPath("viewCTG/edit") + "\">Edit Category</a><br>",
            "&nbsp;&nbsp;&nbsp;<font color=tan>Edit Category</font><br>") );
        //out.println("<br>");
        out.println("</td><td class=menuItem>&nbsp;</td></tr>");
        
        
        //Result Type
        out.println("<tr><td bgcolor=\"#008B8B\" class=menuTitle>");
        out.println("<p>Result Type</td><td bgcolor=\"#008B8B\"><a href=\""+getServletPath("help.html#rtype")+"\" target=_help>"+
                    "<img border=0 src=\"images/i.png\"></a></td><tr>");
        
        out.println("<tr><td class=menuItem>");
        out.println( privDependentString(currentPrivs, RTYPE_W,
            "<a href=\"" + getServletPath("viewRType/new") + "\">New&nbsp;result&nbsp;type</a><br>",
            "&nbsp;&nbsp;&nbsp;<font color=tan>ew&nbsp;result&nbsp;type</font><br></td><td class=menuItem>&nbsp;</td></tr><tr><td>") );
        
        out.println( privDependentString(currentPrivs, RTYPE_W,
            "<a href=\"" + getServletPath("viewRType/edit") + "\">Edit&nbsp;result&nbsp;type</a><br>",
            "&nbsp;&nbsp;&nbsp;<font color=tan>Edit type</font><br>") );
        //out.println("<br>");
        out.println("</td><td class=menuItem>&nbsp;</td></tr>");

    
        // Export result, ex. LOD score
     //   out.println("<tr><td bgcolor=\"#008B8B\" width=144 class=menuTitle>");
     //   out.println("<p>Export Results</td></tr>");
     //   out.println("<tr><td class=menuItem>");
     //   out.println( privDependentString(currentPrivs, RES_W,
     //       "<a href=\"" + getServletPath("viewRes") + "\">New Export</a><br>",
     //       "&nbsp;&nbsp;&nbsp;<font color=tan>New Wxport</font><br>") );   
     //   out.println("</td></tr>");
    
        out.println("<td bgcolor=\"#008B8B\" height=3>&nbsp;</td><td bgcolor=\"#008B8B\">&nbsp;</td></tr>");
       // out.println("<td bgcolor=\"#008B8B\" height=3>&nbsp;</td>");
        out.println("</table>");
	out.println("</body></html>");
    }
    
}
