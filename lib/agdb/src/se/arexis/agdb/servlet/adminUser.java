/*
  $Log$
  Revision 1.6  2005/02/04 15:58:40  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.5  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.4  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.3  2002/10/22 06:08:08  heto
  rebuilt the "back-buttons".
  Dont save the request object, save the URL instead.
  New function.

  Revision 1.2  2002/10/18 11:41:09  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:04  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.9  2001/05/31 07:06:55  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.8  2001/05/22 06:54:31  roca
  backfunktionality for administrator pages and privileges removed from roles (user mode)

  Revision 1.7  2001/05/08 12:03:59  frob
  Changed all methods that used to call writeError to write an error page. These
  methods now calls the general method commitOrRollback which handles any errors.
  The writeError method is removed.

  Revision 1.6  2001/05/03 14:20:58  frob
  Implemented local version of errorQueryString and changed writeError to use this method.

  Revision 1.5  2001/05/03 07:57:34  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.4  2001/05/02 11:49:54  frob
  Calls to removeOper and removeId modified to use the general removeQSParameter.
  The previously called methods are removed.

  Revision 1.3  2001/05/02 11:45:53  frob
  Indented the file, added header.

*/
package se.arexis.agdb.servlet;


import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
//import com.oreilly.servlet.MultipartRequest; // For file uploads
import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;

public class adminUser extends AdminArexisServlet
{
   public void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      doGet(req, res);
   }
   /**
    * This method dispatches the request to the corresponding
    * method. The servlet handles the surrounding frameset,
    * the top frame, the bottom frame and methods for creation of
    * new groupings.
    */
   public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {

      HttpSession session = req.getSession(true);

      String extPath = req.getPathInfo();
      if (extPath == null || extPath.equals("") || extPath.equals("/")) {
         // The frame is requested
         writeFrame(req, res);
      } else if (extPath.equals("/top")) {
         writeTop(req, res);
      } else if (extPath.equals("/bottom")) {
         writeBottom(req, res);
      } else if (extPath.equals("/middle")) {
         writeMiddle(req, res);
      } else if (extPath.equals("/edit")) {
         writeEdit(req, res);
      } else if (extPath.equals("/new")) {
         writeNew(req, res);
      }
   }
   private void writeFrame(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      // set content type and other response header fields first
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");

      PrintWriter out = res.getWriter();
      try 
      {
         // Check if redirection is needed 
         res = checkRedirectStatus(req,res); 
         //req=getServletState(req,session);
         
         String topQS = buildQS(req);
         // we don't want the oper parameter anaywhere but in the "edit" and "new" page!
         topQS = removeQSParameterOper(topQS);
         String bottomQS = topQS.toString();

         out.println("<html>"
                     + "<HEAD>"
                     + " <TITLE>Administrate Users</TITLE>"
                     + "</HEAD>"
                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" //
                     + "<frame name=\"usertop\" "
                     + "src=\""+ getServletPath("adminUser/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"usermiddle\" "
                     + "src=\""+ getServletPath("adminUser/middle?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"userbottom\""
                     + "src=\"" +getServletPath("adminUser/bottom?") + bottomQS + "\" "
                     + " scrolling=\"auto\" marginheight=\"0\" frameborder=\"0\"></frameset>"
                     + "<noframes><body><p>"
                     + "This page uses frames, but your browser doesn't support them."
                     + "</p></body></noframes></frameset>"
                     + "</HTML>");
      } catch (Exception e)
      {
         e.printStackTrace(System.err);
      }
      finally {
      }
   }
   private String buildQS(HttpServletRequest req) {
      StringBuffer output = new StringBuffer(512);
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      String action = null, // For instance COUNT, DISPLAY, NEXT etc
         id = null,
         name = null,
         status = null,
         orderby = null;

      id = req.getParameter("id");
      name = req.getParameter("name");
      status = req.getParameter("status");
      if (status == null || status.trim().equals(""))
         status = "*";

      // Find the requested action
      if ("DISPLAY".equalsIgnoreCase(req.getParameter("DISPLAY"))) {
         action = "DISPLAY";
      } else if ("COUNT".equalsIgnoreCase(req.getParameter("COUNT"))) {
         action = "COUNT";
      } else if ("<<".equalsIgnoreCase(req.getParameter("TOP"))) {
         action = "TOP";
      } else if ("<".equalsIgnoreCase(req.getParameter("PREV"))) {
         action = "PREV";
      } else if (">".equalsIgnoreCase(req.getParameter("NEXT"))) {
         action = "NEXT";
      } else if (">>".equalsIgnoreCase(req.getParameter("END"))) {
         action = "END";
      } else {
         action = req.getParameter("ACTION");
         if (action == null) action = "NOP";
      }

      output.append("ACTION=").append(action);

      // Set the parameters STARTINDEX and ROWS
      if (!action.equals("NOP"))
         output.append(setIndecis(action, req, session));
      output.append("&status=").append(status);
      if (id != null && !id.trim().equals(""))
         output.append("&id=").append(id);
      if (name != null && !name.trim().equals(""))
         output.append("&name=").append(name);
      if (req.getParameter("oper") != null) {
         output.append("&oper=").append(req.getParameter("oper"));
      }

      orderby = req.getParameter("ORDERBY");
      if (orderby != null)
         output.append("&ORDERBY=").append(orderby);
      else
         output.append("&ORDERBY=NAME");
      return output.toString().replace('%', '*');
   }

   private String setIndecis(String action, HttpServletRequest req, HttpSession session) {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(req, session);
      maxRows = getMaxRows(session);
      if (req.getParameter("STARTINDEX") != null ) {
         startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
      }	else
         startIndex = 1;
      if ("COUNT".equalsIgnoreCase(action) ||
          "DISPLAY".equalsIgnoreCase(action)) {
         if (startIndex >= rows)
            startIndex = 1;
      } else if ("TOP".equalsIgnoreCase(action)) {
         startIndex = 1;
      } else if ("PREV".equalsIgnoreCase(action)) {
         // decrement startindex with maxRows, if possible
         startIndex -= maxRows;
         if (startIndex < 1) startIndex = 1;
      } else if ("NEXT".equalsIgnoreCase(action)) {
         // Increment startindex with maxrows, if possible
         startIndex += maxRows;
         //System.err.println("maxrows="+maxRows+" incremented=" +startIndex);
         if (startIndex >= rows)
            startIndex -= maxRows;
      } else if ("END".equalsIgnoreCase(action)) {
         int mult = (int) rows / maxRows;
         if (rows % maxRows == 0) mult--;
         startIndex = (mult > 0 ? mult : 0) * maxRows + 1;
      } else {
         // action = NOP, i guess
      }
      output.append("&STARTINDEX=").append(startIndex)
         .append("&ROWS=").append(rows);
      //System.err.println("out="+output.toString());
      return output.toString();
   }

   private int countRows(HttpServletRequest req, HttpSession session) {
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer(512);
      try {
         sbSQL.append("SELECT count(*) " +
                      "FROM V_USERS_1 WHERE 1=1 ");
         sbSQL.append(buildFilter(req,false));
         stmt = conn.createStatement();
         rset = stmt.executeQuery(sbSQL.toString());
         rset.next();
         return rset.getInt(1);
      } catch (SQLException e) {
         e.printStackTrace(System.err);
         return 0;
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         }catch (SQLException ignored) {}
      }
   }
   
   private String buildFilter(HttpServletRequest req)
   {
       return buildFilter(req,true);
   }
   
   private String buildFilter(HttpServletRequest req, boolean order) 
   {
      String name = null,
         status = null;
      //			     orderby = null;
      StringBuffer filter = new StringBuffer(256);
      name = req.getParameter("name");
      status = req.getParameter("status");

      if ("E".equals(status) || "D".equals(status))
         filter.append(" and status='" + status + "'");
      if (name != null && !name.trim().equals(""))
         filter.append(" and name like '" + name + "'");
      
      /*
      if (order)
      {
          // Do something?
      }
       */

      // Replace every occurence of '*' with '%' and return the string
      // (Oracel uses '%' as wildcard while '%' demands some specail treatment
      // when passed in the query string)
      return filter.toString().replace('*', '%');
   }


   /***************************************************************************************
    * *************************************************************************************
    * The top frame
    */

   public void writeTop(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      // set content type and other response header fields first
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String oper;
      oper = req.getParameter("oper");
      if (oper == null || "".equals(oper))
         oper = "SELECT";

      HttpSession session = req.getSession(true);
      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null;
      int startIndex = 0, rows = 0, maxRows = 0;
      String status, name, orderby, oldQS, newQS, action;
      try {
         conn = (Connection) session.getValue("conn");

         status = req.getParameter("status");
         name = req.getParameter("name");
         action = req.getParameter("ACTION");
         oldQS = req.getQueryString();
         newQS = buildTopQS(oldQS);
         orderby = req.getParameter("ORDERBY");
         if (req.getParameter("STARTINDEX") != null)
            startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         else
            startIndex = 0;
         if (req.getParameter("ROWS") != null)
            rows = Integer.parseInt(req.getParameter("ROWS"));
         else
            rows = 0;
         if (status == null || status.trim().equals(""))
            status = "*";
         if (name == null || name.trim().equals(""))
            name = "";

         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+ getURL("style/view.css") +"\">");
         out.println("<base target=\"adminmainframe\">");

         out.println("<title>Project</title>");
         out.println("</head>");

         out.println("<body bgcolor=\"#ffffd0\">");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<form method=get action=\"" +getServletPath("adminUser") +"\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Users</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr><tr><td width=\"517\">");

         // Name
         out.println("<table width=488 height=\"92\">");

         out.println("<td>");
         out.println("<td><b>Name</b><br>");
         out.println("<input type=text name=name width=100 " +
                     "style=\"WIDTH: 100px\" value=\"" + name + "\">");
         out.println("</td>");

         // Status
         out.println("<td><b>Status</b><br>");
         out.println("<select name=status width=100 " +
                     "style=\"WIDTH: 100px\">");
         if ("E".equals(status)) {
            out.println("<option selected value=\"E\">E</option>");
            out.println("<option value=\"D\">D</option>");
            out.println("<option>*</option>");
         } else if ("D".equals(status)) {
            out.println("<option value=\"E\">E</option>");
            out.println("<option selected value=\"D\">D</option>");
            out.println("<option>*</option>");
         } else {
            out.println("<option value=\"E\">E</option>");
            out.println("<option value=\"D\">D</option>");
            out.println("<option selected>*</option>");
         }
         out.println("</select>");
         out.println("</td>");
         out.println("<td>&nbsp;</td>");
         out.println("<td>&nbsp;</td>");

         out.println("<tr>");
         out.println("<td>&nbsp;</td>");
         out.println("<td><b>&nbsp;</b><br></td>");
         out.println("<td><b>&nbsp;</b><br></td></table></td>");

         //Buttons
         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");
         out.println("<input type=button value=\"New User\"" +
                     " onClick='parent.location.href=\"" +getServletPath("adminUser/new?") + newQS + "\"' " +
                     "height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">" +
                     "</td>");

         out.println("<tr><td width=68 colspan=2>" +
                     "<input id=COUNT name=COUNT type=submit value=\"Count\" width=\"69\"" +
                     " height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">" +
                     "</td>");
         out.println("<td width=68 colspan=2>" +
                     "<input id=DISPLAY name=DISPLAY type=submit value=\"Display\"" +
                     " width=\"70\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">" +
                     "</td></tr>");


         // some hidden values
         out.println("<input type=\"hidden\" id=\"STARTINDEX\" name=\"STARTINDEX\" value=\"" + startIndex + "\">");
         out.println("<input type=\"hidden\" id=\"ORDERBY\" name=\"ORDERBY\" value=\"" + orderby + "\">");
         out.println("<input type=\"hidden\" id=\"oper\" name=\"oper\" value=\"\">");

         out.println("<td width=34 colspan=1><input id=TOP name=TOP type=submit value=\"<<\"" +
                     "width=\"35\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 31px\">" +
                     "</td>");
         out.println("<td width=34 colspan=1><input id=PREV name=PREV type=submit value=\"<\"" +
                     "width=\"34\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 32px\">" +
                     "</td>");
         out.println("<td width=34 colspan=1><input id=NEXT name=NEXT type=submit value=\">\"" +
                     "width=\"35\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 32px\">" +
                     "</td>");
         out.println("<td width=34 colspan=1><input id=END name=END type=submit value=\">>\"" +
                     "width=\"35\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 31px\">" +
                     "</td>");
         out.println("</table></form></td></tr></table>");

         out.println("</body></html>");

      } catch (Exception e)		{
         out.println("<PRE>");
         e.printStackTrace(out);
         out.println("</PRE>");
      }	finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
   }

   private String buildTopQS(String oldQS) {
      StringBuffer sb = new StringBuffer(256);
      // First we remove the ORDERBY parameter (Must be at the end)
      int i1 = 0, i2 = 0;
      i1 = oldQS.indexOf("&ORDERBY=");
      if (i1 >= 0)
         oldQS = oldQS.substring(0, i1);

      // Now let's remove the parameter ACTION
      i1 = oldQS.indexOf("ACTION=");
      if (i1 >= 0) {
         i2 = oldQS.indexOf("&", i1 + 1);
         if (12 > i1) {
            sb.append(oldQS.substring(0, i1));
            sb.append(oldQS.substring(i2 + 1));
         } else {
				// There was no parameter after ACTAION
         }
      } else { // The query string didn't contain a ACTION-parameter
         sb.append(oldQS);
      }
      return sb.toString();
   }

   private String buildInfoLine(String action, int startIndex, int rows, int maxRows) {
      String output = null;
      int upperLimit = startIndex + maxRows - 1;
      if (upperLimit > rows) upperLimit = rows;
      if (rows == 0) startIndex = 0;
      if (action.regionMatches(true, 0, "NEXT", 0, "NEXT".length())) {
         // Print the current row intervall
         output = new String("Displaying " + startIndex + "-" + upperLimit +
                             " of " + rows + " rows.");
      } else if (action.regionMatches(true, 0, "PREV", 0, "PREV".length())) {
         // Print the current row intervall
         output = new String("Displaying " + startIndex + "-" + upperLimit +
                             " of " + rows + " rows.");
      } else if (action.regionMatches(true, 0, "TOP", 0, "TOP".length())) {
         // Print the current row intervall
         output = new String("Displaying " + startIndex + "-" + upperLimit +
                             " of " + rows + " rows.");
      } else if (action.regionMatches(true, 0, "END", 0, "END".length())) {
         // Print the current row intervall
         output = new String("Displaying " + startIndex + "-" + upperLimit +
                             " of " + rows + " rows.");
      } else if ("COUNT".equalsIgnoreCase(action)) {
         // Count the number of rows this filter will return
         output = new String("Query will return " + rows + " rows.");
      } else if ("DISPLAY".equalsIgnoreCase(action)) {
         // print the current row intervall
         output = new String("Displaying " + startIndex + "-" + upperLimit +
                             " of " + rows + " rows.");
      } else if ("NOP".equalsIgnoreCase(action)) {
         // Print something that isn't visible (to make the frame as big as it would be in the cases above
         output = new String("&nbsp;");
      } else {
         // ???
         output = new String("?" + action + "?");
      }

      return output;
   }


   /***********************************************************
                                                               /* The middle frame (contains header for the result-table)
                                                                */
   private void writeMiddle(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      // set content type and other response header fields first
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");

      PrintWriter out = res.getWriter();
      Statement stmt = null;
      ResultSet rset = null;
      Connection conn = null;
      String action;
      int startIndex, rows, maxRows;
      action = req.getParameter("ACTION");
      if (req.getParameter("STARTINDEX") != null)
         startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
      else
         startIndex = 0;
      if (req.getParameter("ROWS") != null)
         rows = Integer.parseInt(req.getParameter("ROWS"));
      else
         rows = 0;
      maxRows = 50;

      try {
         out.println("<html>\n<head>\n<link rel=\"stylesheet\" " +
                     "type=\"text/css\" href=\""+getURL("style/tableBar.css")+"\">");
         out.println("<base target=\"adminmainframe\">");
         out.println("</head>");
         out.println("<body>");
         if(action != null)
         {
            // out.println("<p align=left>&nbsp;&nbsp;");
            out.println("&nbsp;"+buildInfoLine(action, startIndex, rows, maxRows));
         }

         String oldQS, newQS;
         oldQS = req.getQueryString();
         String choosen= req.getParameter("ORDERBY");
         newQS = buildTopQS(oldQS);
         /*      out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 " +
                 "height=20 width=550 style=\"margin-left:2px\">" +
                 "<td width=5></td>");
         */
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=550 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");



         // the menu choices
         // Name
         out.println("<td width=200><a href=\"" +
                     getServletPath("adminUser")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\">");
         if(choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown><b>Name</b></FONT></a></td>\n");
         else
            out.println("Name</a></td>\n");
         // USR
         out.println("<td width=150><a href=\"" +
                     getServletPath("adminUser")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>Usr</b></FONT></a></td>\n");
         else
            out.println("Usr</a></td>\n");
         out.println("<td width=100><a href=\"" +
                     getServletPath("adminUser") + "?ACTION=DISPLAY&" + newQS + "&ORDERBY=STATUS\">");
         if (choosen.equals("STATUS"))
            out.println("<FONT color=saddlebrown><b>Status</b></FONT></a></td>\n");
         else
            out.println("Status</a></td>\n");
         /*
           out.println("<td width=100>&nbsp;</td>");
           out.println("</tr>");
           out.println("</table>");
           out.println("</body></html>");
         */
         out.println("<td width=100>&nbsp;</td>");
         out.println("</table></table>");
         out.println("</body></html>");

      } catch (Exception e)
      {
         out.println("<strong>Error in filter!</strong><br>");
         out.println("Error message: " + e.getMessage());
         out.println("<br>Modify filter according to message!</body></html>");
      }
      finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (Exception ignored) {}
      }
   }
   /***************************************************************************************
    * *************************************************************************************
    * The bottom frame
    */
   private void writeBottom(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      // set content type and other response header fields first
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      Statement stmt = null;
      ResultSet rset = null;
      Connection conn = null;
      try
      {
         String action = null, orderby;
         String oldQS = req.getQueryString();
         oldQS = removeQSParameterId(oldQS);
         action = req.getParameter("ACTION");
         orderby = req.getParameter("ORDERBY");
         if (action == null || action.equalsIgnoreCase("NOP") ||
             action.equalsIgnoreCase("COUNT") )
         {
            // Nothing to do!
            HTMLWriter.writeBottomDefault(out);
            return;
         }
         else if (action.equalsIgnoreCase("NEXT"))
         {
            ;
         }
         else if (action.equalsIgnoreCase("PREV"))
         {
            ;
         }

         out.println("<html>\n"
                     + "<head><link rel=\"stylesheet\" type=\"text/css\" href=\""
                     +getURL("style/bottom.css")+"\">\n"
                     + "<title>bottomFrame</title>\n"
                     + "</head>\n"
                     + "<body>\n");

         conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();
         StringBuffer sbSQL = new StringBuffer(512);
         sbSQL.append("SELECT ID, NAME, USR, STATUS FROM gdbadm.V_USERS_1 WHERE 1=1");
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);
         sbSQL.append(" ORDER BY ").append(orderby);
         rset = stmt.executeQuery(sbSQL.toString());

         out.println("<TABLE align=left border=0 cellPadding=0");
         out.println("cellSpacing=0 width=555 style=\"margin-left:2px\">");//STYLE=\"WIDTH: 790px;\">");
         boolean odd = true;
         // First we spawn rows!

         int rowCount = 0;
         int startIndex = Integer.parseInt( req.getParameter("STARTINDEX"));
         if (startIndex > 1) {
            while ((rowCount++ < startIndex - 1) && rset.next())
               ;
         }
         rowCount = 0;
         int maxRows = 50;
         while (rset.next() && rowCount < maxRows) {
            out.println("<TR align=left ");
            if (odd) {
               out.println("bgcolor=white>");
               odd = false;
            } else {
               out.println("bgcolor=lightgrey>");
               odd = true;
            }
            out.println("<td width=5></td>");
            out.println("<TD WIDTH=200>" + formatOutput(session, rset.getString("NAME"),33) +"</TD>");
            out.println("<TD WIDTH=150>" + formatOutput(session, rset.getString("USR"),10)+"</TD>");
            out.println("<TD WIDTH=100>" + rset.getString("STATUS") + "</TD>");

            out.println("<TD WIDTH=100><A HREF=\"" +getServletPath("adminUser/edit?id=")
                        + rset.getString("ID")
                        + "&" + oldQS + "\" target=\"adminmainframe\">Edit</A></TD>");
            out.println("</TR>");

            rowCount++;
         }
         out.println("</TABLE>");
         out.println("<table><tr><td>&nbsp;</td></tr></table>");
         out.println("</body></html>");


      } catch (Exception e)
      {
         out.println("<strong>Error in filter!</strong><br>");
         out.println("Error message: " + e.getMessage());
         out.println("<br>Modify filter according to message!</body></html>");
         e.printStackTrace(System.err);
      }
      finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (Exception ignored) {}
      }
   }


   /***************************************************************************************
    * *************************************************************************************
    * The new user page
    */
   private void writeNew(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createUser(req, res, conn)) {
            writeFrame(req, res);
         } else {
            ; // We have already displayed an error message!
         }
      } else {
         writeNewPage(req, res);
      }
   }
   private void writeNewPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String newQS;
      try {
         conn = (Connection) session.getValue("conn");
         newQS = removeQSParameterId(req.getQueryString());
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeNewScript(out);
         out.println("<title>New User</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Users - New</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=get action=\"" +
                     getServletPath("adminUser/new?") + newQS + "\">");

         out.println("<table border=0 cellpading=0 cellspacing=0><tr>");
         out.println("<td width=10 style=\"WIDTH: 15px\">");
         out.println("</td><td>");
         out.println("<table width=400 cellspacing=0 cellpading=0 border=0>");
         out.println("<tr>");
         out.println("<td width=200>Name<br>");
         out.println("<input type=text name=n maxlength=32 width=195 " +
                     "style=\"WIDTH: 195px; HEIGHT: 22px\" value=\"\">");
         out.println("</td>");
         out.println("</tr><tr>");
         out.println("<td width=200>Status<br>");
         out.println("<select name=s width=195 style=\"WIDTH: 195px\">");
         out.println("<option selected value=\"E\">E</option>");
         out.println("<option value=\"D\">D</option>");
         out.println("</select>");
         out.println("</td></tr>");
         out.println("<tr><td>");
         out.println("Usr<br>");
         out.println("<input type=text name=u width=195 " +
                     "style=\"WIDTH: 195px\" value=\"\">");
         out.println("</td></tr>");
         out.println("<tr><td>Password<br>");
         out.println("<input type=password name=p width=195 " +
                     "STYLE=\"WIDTH: 195px\" value=\"\">");
         out.println("</td>");
         out.println("</tr>");
         out.println("</table>");
         out.println("</td></tr><tr><td></td><td>");
         out.println("<table>");
         out.println("<tr><td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='document.location.href=\"" +
                     //getServletPath("adminUser?") + newQS + "\"'>");
                     getServletPath("adminUser?&RETURNING=YES")  + "\"'>");
         out.println("&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Create width=100 " +
                     "style=\"WIDTH: 100px\" onClick='valForm()'>");
         out.println("&nbsp;</td></tr></table>");
         // Some extra information...
         out.println("<input type=\"hidden\" NAME=oper value=\"\">");
         out.println("<input type=\"hidden\" NAME=RETURNING value=YES>");

         out.println("</td></tr>");
         out.println("</table>");
         out.println("</form>");
         out.println("</body>");
         out.println("</html>");
      } catch (Exception e) {
         e.printStackTrace(System.err);
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {
         }
      }

   }

   private void writeNewScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function selChanged(item) {");
      out.println("  document.forms[0].item.value = \"\" + item;");
      out.println("  document.forms[0].oper.value = \"SEL_CHANGED\";");
      out.println("  document.forms[0].submit();");
      out.println("}");
      out.println("function valForm() {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ( (\"\" + document.forms[0].u.value).length < 1 ||");
      out.println("	     (\"\" + document.forms[0].u.value).length > 10) {");
      out.println("	  alert('Usr must be in the range 1 - 10 characters!');");
      out.println("	  return ;");
      out.println("	}");
      out.println("	if ( (\"\" + document.forms[0].p.value).length < 1 ||");
      out.println("	     (\"\" + document.forms[0].p.value).length > 10) {");
      out.println("	  alert('Password must be in the range 1 - 10 characters!');");
      out.println("	  return ;");
      out.println("	}");
      out.println("	if ( (\"\" + document.forms[0].n.value) != \"\" &&");
      out.println("       document.forms[0].n.value.length > 32) {");
      out.println("			alert('Name must be in the range 1 - 32 characters!');");
      out.println("			return ;");
      out.println("	}");
      out.println("	if ( (\"\" + document.forms[0].n.value) == \"\") {");
      out.println("			rc = 0;");
      out.println("	}");
      out.println("	");
      out.println("	");
      out.println("	if (rc) {");
      out.println("		if (confirm('Are you sure that you want to create the user?')) {");
      out.println("			document.forms[0].oper.value = 'CREATE'");
      out.println("			document.forms[0].submit();");
      out.println("		}");
      out.println("	}");
      out.println("	");
      out.println("	");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
   }

   /***************************************************************************************
    * *************************************************************************************
    * The edit page
    */
   private void writeEdit(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";
      if (oper.equals("DELETE")) {
         if (deleteUser(req, res, conn))
            writeFrame(req, res);
      } else if (oper.equals("UPDATE")) {
         if (updateUser(req, res, conn))
            writeEditPage(req, res);
      } else
         writeEditPage(req, res);
   }
   private void writeEditPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      Connection conn =  (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      try {
         String oldQS = buildQS(req);
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         String id = req.getParameter("id");

         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);
         out.println("<title>Edit User</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Users - Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         stmt = conn.createStatement();
         String sql = "SELECT NAME, USR, PWD, STATUS " +
            "FROM V_USERS_1 WHERE " +
            "ID=" + id;
         rset = stmt.executeQuery(sql);
         rset.next();
         String name = rset.getString("NAME");
         String usr = rset.getString("USR");
         String pwd = rset.getString("PWD");
         String status = rset.getString("STATUS");
         rset.close();
         stmt.close();
         // Belowe we use rather cryptic names for the form data. We do this to prevent that
         // the data in the form won't collide with the data in the old query string
         out.println("<FORM action=\"" + getServletPath("adminUser/edit?") +
                     oldQS + "\" method=\"post\" name=\"FORM1\">");
         out.println("<table border=0 cellpading=0 cellspacing=0><tr>");
         out.println("<td width=10 style=\"WIDTH: 15px\">");
         out.println("</td><td>");
         out.println("<table width=400 cellspacing=0 cellpading=0 border=0>");
         out.println("<tr>");
         out.println("<td width=200>Name<br>");
         out.println("<input type=text name=n maxlength=32 width=195 " +
                     "style=\"WIDTH: 195px; HEIGHT: 22px\" value=\"" +
                     formatOutput(session, name, 33) + "\">");
         out.println("</td>");
         out.println("</tr><tr>");
         out.println("<td width=200>Status<br>");
         out.println("<select name=s width=195 style=\"WIDTH: 195px\">");
         if ("E".equals(status)) {
            out.println("<option selected value=\"E\">E</option>");
            out.println("<option value=\"D\">D</option>");
         } else {
            out.println("<option value=\"E\">E</option>");
            out.println("<option selected value=\"D\">D</option>");
         }
         out.println("</select>");
         out.println("</td></tr>");
         out.println("<tr><td>");
         out.println("Usr<br>");
         out.println("<input type=text name=u width=195 " +
                     "style=\"WIDTH: 195px\" value=\"" + usr + "\">");
         out.println("</td></tr>");
         out.println("<tr><td>Password<br>");
         out.println("<input type=password name=p width=195 " +
                     "STYLE=\"WIDTH: 195px\" value=\"" + pwd + "\">");
         out.println("</td>");
         out.println("</tr>");

         out.println("<tr><td  colspan=2 nowrap align=center>");
         out.println("&nbsp;</td></tr>");
         out.println("<tr><td  colspan=2 nowrap align=center>");
         out.println("<table cellspacing=0 cellpadding=0 border=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                    // getServletPath("adminUser?") + oldQS + "\"'>&nbsp;");
                     getServletPath("adminUser?&RETURNING=YES") + "\"'>&nbsp;");
         out.println("</td><td>");
         out.println("<input type=reset value=Reset width=100 " +
                     "style=\"WIDTH: 100px\">&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button name=DELETE value=Delete width=100 " +
                     "style=\"WIDTH: 100px\" onClick='valForm(\"DELETE\")'>&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button name=UPDATE value=Update width=100 " +
                     "style=\"WIDTH: 100px\" onClick='valForm(\"UPDATE\")'>&nbsp;");
         out.println("</td></tr></table>");
         out.println("</td></tr>");
         out.println("</table>");
         // Store some extra information needed by doPost()
         out.println("<input type=\"hidden\" NAME=oper value=\"\">");
         out.println("<input type=\"hidden\" NAME=RETURNING value=YES>");

         out.println("</td></tr></table>");
         out.println("</FORM>");
         out.println("</body>");
         out.println("</html>");
      } catch (Exception e)
      {
         out.println("<PRE>");
         e.printStackTrace(out);
         out.println("</PRE>");
      }
      finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
   }

   
   /**
    * Creates a new user.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if user was created.
    *         False if user was not created.
    */
   private boolean createUser(HttpServletRequest request,
                              HttpServletResponse response,
                              Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         String name, usr, pwd, status;
         connection.setAutoCommit(false);
         name = request.getParameter("n");
         usr = request.getParameter("u");
         pwd = request.getParameter("p");
         status = request.getParameter("s");
         DbUser dbu = new DbUser();
         dbu.CreateUser(connection, name, usr, pwd, status);
         errMessage = dbu.getErrorMessage();
         Assertion.assertMsg(errMessage == null ||
                          errMessage.trim().equals(""), errMessage);
      }
      catch (Exception e) 
      {
         // Flag for error and set the errMessage if it has not been set
         isOk = false;
         e.printStackTrace(System.err);
         if (errMessage == null)
         {
            errMessage = e.getMessage();
         }
      }

      commitOrRollback(connection, request, response, "Users.New.Create",
                       errMessage, "adminUser/new", isOk);
      return isOk;
   }

    
   /**
    * Deletes a user.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if user was delete.
    *         False if user was not deleted.
    */
   private boolean deleteUser(HttpServletRequest request,
                              HttpServletResponse response,
                              Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int id;
         connection.setAutoCommit(false);
         id = Integer.parseInt(request.getParameter("id"));
         DbUser dbu = new DbUser();
         dbu.DeleteUser(connection, id);
         errMessage = dbu.getErrorMessage();
         Assertion.assertMsg(errMessage == null ||
                          errMessage.trim().equals(""), errMessage);
      }
      catch (Exception e) 
      {
         // Flag for error and set the errMessage if it has not been set
         isOk = false;
         e.printStackTrace(System.err);
         if (errMessage == null)
         {
            errMessage = e.getMessage();
         }
      }
      commitOrRollback(connection, request, response, "Users.Edit.Delete",
                       errMessage, "adminUser/edit", isOk);
      return isOk;
   }


   private boolean updateUser(HttpServletRequest request,
                              HttpServletResponse response,
                              Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         String name;
         String status;
         String usr;
         String pwd;
         int id;
         String oldQS = request.getQueryString();
         name = request.getParameter("n");
         status = request.getParameter("s");
         usr = request.getParameter("u");
         pwd = request.getParameter("p");
         id = Integer.parseInt(request.getParameter("id"));

         DbUser dbu = new DbUser();
         dbu.UpdateUser(connection, id, name, usr, pwd, status);
         errMessage = dbu.getErrorMessage();
         Assertion.assertMsg(errMessage == null ||
                          errMessage.trim().equals(""), errMessage);
      }
      catch (Exception e) 
      {
         // Flag for error and set the errMessage if it has not been set
         isOk = false;
         e.printStackTrace(System.err);
         if (errMessage == null)
         {
            errMessage = e.getMessage();
         }
      }

      commitOrRollback(connection, request, response, "Users.Edit.Update",
                       errMessage, "adminUser/edit", isOk);
      return isOk;
   }


   private void writeEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Since it\\'s possible that a very large amount of data\\n' + ");
      out.println("       'in the database refers to a user, and the fact that deleting\\n' + ");
      out.println("       'a user also causes all this data to be deleted, this operation\\n' + ");
      out.println("       'isn\\'t allowed. Instead of deleting the user this command will\\n' + ");
      out.println("       'disable him/her, which means that this user is not able to log on\\n' + ");
      out.println("       'to any projects. The user can be enabled again by updating his/hers\\n' + ");
      out.println("       'status to \\'E\\'. Do you wish to disable this user?') ) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the user?')) {");
      out.println("			document.forms[0].oper.value='UPDATE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	} else {");
      out.println("		document.forms[0].oper.value='';");
      out.println("	}");
      out.println("	");
      out.println("	if (rc == 0) {");
      out.println("		document.forms[0].submit();");
      out.println("		return true;");
      out.println("	}");
      out.println("	return false;");
      out.println("	");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
   }


   /**
    * Returns the query string to be used when going back from the error
    * page. 
    *
    * @param request The request object to be used when building the string.
    * @return The error query string.
    */
   protected String errorQueryString(HttpServletRequest request)
   {
      String errorQueryString = buildQS(request);
      return removeQSParameterOper(errorQueryString);
   }




}
