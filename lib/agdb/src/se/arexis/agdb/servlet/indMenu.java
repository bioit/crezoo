/*
$Log$
Revision 1.3  2003/05/02 07:58:45  heto
Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
Modified configuration and source files according to package change.

Revision 1.2  2003/04/25 12:14:46  heto
Changed all references to axDefault.css
Source layout fixes.


*/

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.util.*;

public class indMenu extends SecureArexisServlet 
{
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException 
    {
        HttpSession session = req.getSession(false);
        int currentPrivs[] = (int[]) session.getValue("PRIVILEGES");

	PrintWriter out = res.getWriter();

	// set content type and other response header fields first
        res.setContentType("text/html");
        out.println("<html>\n"
            + "<head>\n");
        HTMLWriter.css(out,getURL("style/axDefault.css"));
        out.println("<base target=\"content\">\n"
            + "<title>meny</title>\n"
            + "</head>\n"
            + "<body background=\"" + getURL("images/menuback7.gif") + "\"> \n");
        out.println("<table align=left border=0 cellPadding=1 cellSpacing=0 width=144>");

        out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
            "<p>Sampling units</td>" + "<tr>" + "<td class=menuItem>"
            // check privileges on these
            +privDependentString(currentPrivs,SU_R,
            /*if true*/"<a href=\""+getServletPath("viewSU")+"\">View & Edit</a><br>",
            /*if false*/"&nbsp&nbsp<font color = tan> View & Edit</font><br>"));


        out.println("<br></font></td><tr>");



        // Groupings
        out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
            "<p>Groupings</td>" + "<tr>" + "<td class=menuItem>"

            +privDependentString(currentPrivs,GRP_R,
            /*if true*/"<a href=\""+getServletPath("viewGrouping")+"\">View & Edit</a><br>",
            /*if false*/"&nbsp&nbsp<font color = tan> View & Edit</font><br>")

            +privDependentString(currentPrivs,GRP_W,
            /*if true*/"<a href=\""+getServletPath("viewGrouping/impFile")+"\">File Import</a><br><font size=1><br></font>",
            /*if false*/"&nbsp&nbsp<font color = tan> File Import</font><br>")
            +"</td><tr>");


        // Groups
        out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
            "<p>Groups</td>" + "<tr>" + "<td class=menuItem>"

            +privDependentString(currentPrivs,GRP_R,
            /*if true*/"<a href=\""+getServletPath("viewGroup")+"\">View & Edit</a><br>",
            /*if false*/"&nbsp&nbsp<font color = tan> View & Edit</font><br>")

            +privDependentString(currentPrivs,GRP_W,
            /*if true*/"<a href=\""+getServletPath("membership")+"\">Membership</a><br><font size=1<br></font>",
            /*if false*/"&nbsp&nbsp<font color = tan>Membership</font><br>")
            +"</td><tr>");


        //Inds
        out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
            "<p>Individuals</td>" + "<tr>" + "<td class=menuItem>"

            +privDependentString(currentPrivs,IND_R,
            /*if true*/"<a href=\""+getServletPath("viewInd")+"\">View & Edit</a><br>",
            /*if false*/"&nbsp&nbsp<font color = tan> View & Edit</font><br>")

            +privDependentString(currentPrivs,IND_W,
            /*if true*/"<a href=\""+getServletPath("impInd")+"\">File Import</a><br><font size=1><br></font>",
            /*if false*/"&nbsp&nbsp<font color = tan> File Import</font><br>")

            +"</td><tr>");

        //Samples
        out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
            "<p>Samples</td>" + "<tr>" + "<td class=menuItem>"

            +privDependentString(currentPrivs,IND_R,
            /*if true*/"<a href=\""+getServletPath("viewSamples")+"\">View & Edit</a><br>",
            /*if false*/"&nbsp&nbsp<font color = tan> View & Edit</font><br>")


            +privDependentString(currentPrivs,IND_W,
            /*if true*/"<a href=\""+getServletPath("impSamples")+"\">File Import</a><br><font size=1><br></font>",
            /*if false*/"&nbsp&nbsp<font color = tan> File Import</font><br>")
            +"</td><tr>");

        out.println("<td bgcolor=\"#008B8B\" height=3>&nbsp;</td>");
        out.println("</table>");
	out.println("</body></html>");
    }
}


