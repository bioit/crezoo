
package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.util.*;

public class phenMenu extends SecureArexisServlet 
{
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException 
    {
        HttpSession session = req.getSession(true);
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
            + "<body background=\"" + getURL("images/menuback7.gif") + "\">");
        out.println("<table align=left border=0 cellPadding=1 cellSpacing=0 width=144>");


        // Variable sets
        out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
            "<p>Variable sets</td>" + "<tr>" + "<td class=menuItem>"

            +privDependentString(currentPrivs,VARS_R,
            /*if true*/"<a href=\"" + getServletPath("viewVarSet")+"\">View & Edit</a><br>",
            /*if false*/"&nbsp&nbsp<font color = tan> View & Edit</font><br>")

            +privDependentString(currentPrivs,VARS_W,
            /*if true*/"<a href=\"" + getServletPath("viewVarSet/membership") + "\">Membership</a><br>",
            /*if false*/"&nbsp&nbsp<font color = tan> Membership</font><br>")

            +privDependentString(currentPrivs,VARS_W,
            /*if true*/"<a href=\""+getServletPath("impVarSet")+"\">File Import</a><br><font size=1><br></font>",
            /*if false*/"&nbsp&nbsp<font color = tan> File Import</font><br>")
            +"</td><tr>");


        // Variables
        out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
            "<p>Variables</td>" + "<tr>" + "<td class=menuItem>"

            +privDependentString(currentPrivs,VAR_R,
            /*if true*/"<a href=\""+getServletPath("viewVar")+"\">View & Edit</a><br>",
            /*if false*/"&nbsp&nbsp<font color = tan> View & Edit</font><br>")

            +privDependentString(currentPrivs,VAR_W,
            /*if true*/"<a href=\""+getServletPath("impVar")+"\">File Import</a><br><font size=1><br></font>",
            /*if false*/"&nbsp&nbsp<font color = tan> File Import</font><br>")
            +"</td><tr>");


        // Phenotypes
        out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
            "<p>Phenotypes</td>" + "<tr>" + "<td class=menuItem>"

            +privDependentString(currentPrivs,PHENO_R,
            /*if true*/"<a href=\"" + getServletPath("viewPheno")+"\">View & Edit</a><br>",
            /*if false*/"&nbsp&nbsp<font color = tan> View & Edit</font><br>")

            +privDependentString(currentPrivs,PHENO_W,
            /*if true*/"<a href=\"" + getServletPath("viewPheno/impFile")+"\">File Import</a><br>",
            /*if false*/"&nbsp&nbsp<font color = tan> File Import</font><br>")

            +privDependentString(currentPrivs,PHENO_R,
            /*if true*/"<a href=\"" + getServletPath("viewPheno/completion") + "\">Status</a></br><font size=1><br></font>",
            /*if false*/"&nbsp&nbsp<font color = tan> Status</font><br>")
            +"</td></tr>");

        // U-Variable sets
        out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
            "<p>Unified Variable sets</td>" + "<tr>" + "<td class=menuItem>"

            +privDependentString(currentPrivs,UVARS_R,
            /*if true*/"<a href=\"" + getServletPath("viewUVarSet")+"\">View & Edit</a><br>",
            /*if false*/"&nbsp&nbsp<font color = tan> View & Edit</font><br>")

            +privDependentString(currentPrivs,UVARS_W,
            /*if true*/"<a href=\"" + getServletPath("viewUVarSet/membership") + "\">Membership</a><br>",
            /*if false*/"&nbsp&nbsp<font color = tan> Membership</font><br>")

            +privDependentString(currentPrivs,UVARS_W,
            /*if true*/"<a href=\""+getServletPath("impUVarSet")+"\">File Import</a><br><font size=1><br></font>",
            /*if false*/"&nbsp&nbsp<font color = tan> File Import</font><br>")
            +"</td><tr>");

            // U-variables
            out.println("<td bgcolor=\"#008B8B\" width=144 class=menuTitle>" +
            "<p>Unified Variables</td>" + "<tr>" + "<td class=menuItem>"

            +privDependentString(currentPrivs,UVAR_R,
            /*if true*/"<a href=\""+getServletPath("viewUVar")+"\">View & Edit</a><br>",
            /*if false*/"&nbsp&nbsp<font color = tan> View & Edit</font><br>")

            +privDependentString(currentPrivs,UVAR_W,
            /*if true*/"<a href=\""+getServletPath("impUVar")+"\">File Import</a><br>",
            /*if false*/"&nbsp&nbsp<font color = tan> File Import</font><br>")


            +privDependentString(currentPrivs,VAR_W,
            /*if true*/"<a href=\""+getServletPath("viewUVar/impMapping")+"\">Import Mapping</a><br><font size=1><br></font>",
            /*if false*/"&nbsp&nbsp<font color = tan> Import Mapping</font><br>")
            +"</td><tr>");

	out.println("</table></body></html>");
    }
}


