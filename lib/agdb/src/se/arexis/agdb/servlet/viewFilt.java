/*
  $Log$
  Revision 1.12  2005/03/24 15:12:45  heto
  Working with removing oracle dep.

  Revision 1.11  2005/02/04 15:58:40  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.10  2005/01/31 16:16:40  heto
  Changing database to PostgreSQL. Problems with counts and selection buttons...

  Revision 1.9  2004/03/31 07:31:06  wali
  bug fix

  Revision 1.8  2004/03/30 14:21:51  wali
  Changed Analyses to export.

  Revision 1.7  2004/03/05 14:38:31  wali
  Changed header name

  Revision 1.6  2004/03/01 12:23:25  wali
  Changed analyses to Import/export

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

  Revision 1.1.1.1  2002/10/16 18:14:05  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.13  2001/06/26 12:08:21  roca
  Changed names on buttons (finish/cancel) in alnalyse pages
  Generations changed to Export format
  Corrected counters in Filter/File views
  Bugfix in GenChrimap

  Revision 1.12  2001/05/31 07:06:56  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.11  2001/05/22 06:16:49  roca
  Backfuncktionality fixed for all (?) servlets/subpages

  Revision 1.10  2001/05/10 09:21:02  frob
  Implemented own version of errorQueryString(). create(), update() and delete() rewritten
  to use commitOrRollback(). writeError() was removed.

  Revision 1.9  2001/05/04 11:27:46  frob
  Calls to removeOper and removePid changed to call methods in the superclass.
  The previously called methods are removed.

  Revision 1.8  2001/05/04 11:25:15  frob
  Indented the file and added log header.

*/

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.oreilly.servlet.MultipartRequest; // For file uploads
import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;

public class viewFilt extends SecureArexisServlet
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
      if ( !authorized(req, res) ) {
         // The user does not have the privileges to view the requested page.
         // The method pageLocked has already written an error message
         // to the output stream, and that's why we safely can return here.
         return;
      }

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
      } else if (extPath.equals("/statistics")) {
         //			writeStatistics(req, res);
      } else if (extPath.equals("/edit")) {
         writeEdit(req, res);
      } else if (extPath.equals("/new")) {
         writeNew(req, res);
      } else if (extPath.equals("/users")) {
         //      writeUser(req, res);
      } else if (extPath.equals("/roles")) {
         //      writeRole(req, res);
      } else if (extPath.equals("/editRole")) {
         //      writeEditRole(req, res);
      } else if (extPath.equals("/newRole")) {
         //      writeNewRole(req, res);
      } else if (extPath.equals("/impRole")) {
         //      writeImpRole(req, res);
      } else if (extPath.equals("/impRoleMultipart")) {
         //      createRoleFile(req, res);
      }
   }


   /**
    * Returns the query string that should be used when going back from the
    * error page. 
    *
    * @param request The request object to use when building the string.
    * @return The query string.
    */
   protected String errorQueryString(HttpServletRequest request)
   {
      String errorQueryString = buildQS(request);
      return removeQSParameterOper(errorQueryString);
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
                     + " <TITLE>Filters</TITLE>"
                     + "</HEAD>"
                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" //
                     + "<frame name=\"filttop\" "
                     + "src=\""+ getServletPath("viewFilt/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"filtmiddle\" "
                     + "src=\""+ getServletPath("viewFilt/middle?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"filtbottom\""
                     + "src=\"" +getServletPath("viewFilt/bottom?") + bottomQS + "\" "
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
         sid,
         old_sid,
         name = null,
         fid,
         express = null,
         orderby = null;
      boolean sid_changed = false;
      old_sid = (String) session.getValue("SID");
      sid = req.getParameter("sid");
      if (sid == null) {
         sid = old_sid;
         sid_changed = true;
      } else if (old_sid != null && !old_sid.equals(sid)) {
         sid_changed = true;
      }
      if (sid == null) {
         sid = findSid(conn);
         sid_changed = true;
      }
      fid = req.getParameter("fid");
      session.putValue("SID", sid);
      name = req.getParameter("name");
      express = req.getParameter("express");
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
         output.append(setIndecis(sid_changed, action, req, session));
      if (fid != null && !fid.trim().equals(""))
         output.append("&fid=").append(fid);
      if (name != null && !name.trim().equals(""))
         output.append("&name=").append(name);
      if (express != null && !express.trim().equals(""))
         output.append("&express=").append(express);
      if (req.getParameter("oper") != null) {
         output.append("&oper=").append(req.getParameter("oper"));
      }
      output.append("&sid=").append(sid);
      orderby = req.getParameter("ORDERBY");
      if (orderby != null)
         output.append("&ORDERBY=").append(orderby);
      else
         output.append("&ORDERBY=NAME");
      return output.toString().replace('%', '*');
   }
   private String findSid(Connection conn) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID FROM gdbadm.V_SPECIES_1 ORDER BY NAME");
         if (rset.next()) {
            ret = rset.getString("SID");
         } else {
            ret = "-1";
         }
      } catch (SQLException e) 
      {
          e.printStackTrace();
         ret = "-1";
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {
         }
      }
      return ret;
   }

   private String setIndecis(boolean sid_changed, String action, HttpServletRequest req, HttpSession session) {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(req, session);
      maxRows = getMaxRows(session);
      if (req.getParameter("STARTINDEX") != null && !sid_changed) {
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
      return output.toString();
   }

   private int countRows(HttpServletRequest req, HttpSession session) {
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer(512);
      String pid;
      pid = (String) session.getValue("PID");
      // System.err.println("PID="+pid);

      try {
         sbSQL.append("SELECT count(distinct fid) " +
                      //"FROM gdbadm.V_FILTERS_1 WHERE 1=1 ");
                      "FROM gdbadm.V_FILTERS_1 WHERE PID=" +pid+" ");

         sbSQL.append(buildFilter(req,false));
         stmt = conn.createStatement();
         rset = stmt.executeQuery(sbSQL.toString());
         rset.next();
         //      int test= rset.getInt(1);
         //      System.err.println("COunt="+test);
         //      return test;
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
      String sid = null,
         name = null,
         express = null;
      StringBuffer filter = new StringBuffer(256);
      sid = req.getParameter("sid");
      name = req.getParameter("name");
      express = req.getParameter("express");
      filter.append(" and sid=" + sid);
      if (name != null && !name.trim().equals(""))
         filter.append(" and name like '" + name + "'");

      if (express != null && !express.trim().equals(""))
         filter.append(" and expression like '" + express + "'");

      /*
      if (order)
      {
          //Do Something
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
      String pid, sid, name, express, orderby, oldQS, newQS, action;
      try {
         conn = (Connection) session.getValue("conn");
         pid = (String) session.getValue("PID");
         sid = req.getParameter("sid");
         name = req.getParameter("name");
         express = req.getParameter("express");
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
         if (sid == null || sid.trim().equals(""))
            sid = findSid(conn);

         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+ getURL("style/view.css") +"\">");
         out.println("<base target=\"content\">");

         out.println("<title>Filters</title>");
         out.println("</head>");

         out.println("<body bgcolor=\"#ffffd0\">");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<form method=get action=\"" +getServletPath("viewFilt") +"\">");
         out.println("<p align=\"center\">" +
         //out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Filters - View & Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr><tr><td width=\"517\">");

         out.println("<table width=488 height=\"92\">" +
                     "<td><b>Species</b><br><select name=sid " +
                     //        "onChange='document.forms[0].submit()' " +
                     "style=\"HEIGHT: 22px; WIDTH: 126px\">");

         // Species
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SID FROM gdbadm.V_SPECIES_2 WHERE PID=" +
                                  pid + " ORDER BY NAME");
         while (rset.next()) {
            if (sid != null && sid.equalsIgnoreCase(rset.getString("SID")))
               out.println("<OPTION selected value=\"" + rset.getString("SID") + "\">" +
                           rset.getString("NAME")+ "</option>\n");
            else
               out.println("<OPTION value=\"" + rset.getString("SID") + "\">" + rset.getString("NAME")+"</option>\n");
         }
         rset.close();
         stmt.close();
         out.println("</SELECT></td>");
         // Name
         stmt = conn.createStatement();
         out.println("<td><b>Name</b><br>");
         out.println("<input type=text name=name width=100 " +
                     "style=\"WIDTH: 100px\" value=\"" + replaceNull(name, "") + "\">");
         out.println("</td>");
         // Expression
         out.println("<td><b>Expression</b><br>");
         out.println("<input type=text name=express width=100 " +
                     "style=\"WIDTH: 100px\" value=\"" + replaceNull(express, "") + "\">");
         out.println("</td>");

         out.println("<td><b>&nbsp;</b><br>");
         out.println("&nbsp;</td>");

         out.println("<tr>");

         out.println("<td><b>&nbsp;</b><br>");
         out.println("&nbsp;");
         out.println("</td>");
         out.println("<td><b>&nbsp;</b><br></td>");
         out.println("<td><b>&nbsp;</b><br></td></table></td>");

         //Buttons
         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");
         out.println("<input type=button value=\"New filter\"" +
                     " onClick='parent.location.href=\"" + getServletPath("viewFilt/new?") + newQS + "\"' " +
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
          e.printStackTrace();
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
      } else { // The query string didn't contain an ACTION-parameter
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
      //maxRows = 50;
        maxRows=getMaxRows(session);
      try {
         out.println("<html>\n<head>\n<link rel=\"stylesheet\" " +
                     "type=\"text/css\" href=\""+getURL("style/tableBar.css")+"\">");
         out.println("<base target=\"content\">");
         out.println("</head>");
         out.println("<body>");
         if(action != null)
         {
            //  out.println("<p align=left>&nbsp;&nbsp;");
            out.println("&nbsp;" +buildInfoLine(action, startIndex, rows, maxRows));
         }

         String oldQS, newQS;
         oldQS = req.getQueryString();
         String choosen= req.getParameter("ORDERBY");
         newQS = buildTopQS(oldQS);



         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");


         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 " +
                     "height=20 width=770 style=\"margin-left:2px\">" +
                     "<td width=5></td>");
         // the menu choices
         // Name
         out.println("<td width=100><a href=\"" +
                     getServletPath("viewFilt")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\">");
         if(choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown><b>Name</b></FONT></a></td>\n");
         else
            out.println("Name</a></td>\n");
         // Expression
         out.println("<td width=200><a href=\"" + getServletPath("viewFilt")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=EXPRESSION\">");
         if(choosen.equals("EXPRESSION"))
            out.println("<FONT color=saddlebrown><b>Expression</b></FONT></a></td>\n");
         else out.println("Expression</a></td>\n");

         // Comment
         out.println("<td width=200><a href=\"" +
                     getServletPath("viewFilt")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\">");
         if(choosen.equals("COMM"))
            out.println("<FONT color=saddlebrown><b>Comment</b></FONT></a></td>\n");
         else
            out.println("Comment</a></td>\n");

         // USER
         out.println("<td width=50><a href=\"" + getServletPath("viewFilt")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
         else out.println("User</a></td>\n");

         // Updated
         out.println("<td width=120><a href=\"" + getServletPath("viewFilt")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TS\">");
         if(choosen.equals("TS"))
            out.println("<FONT color=saddlebrown><b>Updated</b></FONT></a></td>\n");
         else out.println("Updated</a></td>\n");

         out.println("<td width=60>&nbsp;</td>"); // Details
         out.println("<td width=40>&nbsp;</td>"); // Edit
         out.println("</table>");
         out.println("</body></html>");

      } catch (Exception e)
      {
          e.printStackTrace();
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
      try {
         String pid, action, orderby;
         String oldQS = req.getQueryString();
         oldQS = removeQSParameterPid(oldQS);
         action = req.getParameter("ACTION");
         pid = (String) session.getValue("PID");
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
         sbSQL.append("SELECT FID, NAME, COMM, EXPRESSION, USR, to_char(TS, '" +
                      getDateFormat(session) + "') as TC_TS FROM V_FILTERS_2 WHERE PID=" + pid);
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);
         sbSQL.append(" ORDER BY ").append(orderby);
         rset = stmt.executeQuery(sbSQL.toString());

         out.println("<TABLE align=left border=0 cellPadding=0");
         out.println("cellSpacing=0 width=773 style=\"margin-left:2px\">");
         boolean odd = true;
         // First we spawn rows!

         int rowCount = 0;
         int startIndex = Integer.parseInt( req.getParameter("STARTINDEX"));
         if (startIndex > 1) {
            while ((rowCount++ < startIndex - 1) && rset.next())
               ;
         }
         rowCount = 0;
         int maxRows = getMaxRows(session);
         while (rset.next() && rowCount < maxRows) {
            out.println("<TR align=left ");
            if (odd) {
               out.println("bgcolor=white>");
               odd = false;
            } else {
               out.println("bgcolor=lightgrey>");
               odd = true;
            }
            out.println("<td width=3></td>");
            out.println("<TD WIDTH=100>" + formatOutput(session, rset.getString("NAME"),12) +"</TD>");
            out.println("<TD WIDTH=200>" + formatOutput(session, rset.getString("EXPRESSION"),20)+"</TD>");
            out.println("<TD WIDTH=200>" + formatOutput(session, rset.getString("COMM"),20)+"</TD>");
            out.println("<TD WIDTH=50>" + formatOutput(session, rset.getString("USR"), 6) + "</TD>");
            out.println("<TD WIDTH=120>" + formatOutput(session, rset.getString("TC_TS"), 18) + "</TD>");
            out.println("<TD WIDTH=60>&nbsp;</TD>");
				/*out.println("<TD WIDTH=60><A HREF=\"" + getServletPath("viewFilt/details?fid=") +
                                  rset.getString("FID") + "&" +
                                  oldQS + "\" target=\"content\">Details</A></TD>");*/
            out.println("<TD WIDTH=40><A HREF=\"" + getServletPath("viewFilt/edit?fid=") +
                        rset.getString("FID") + "&" +
                        oldQS + "\" target=\"content\">Edit</A></TD>");

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
    * The  detail page
    */
   private void writeStatistics(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();

      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null;
      String name = null;
      String comm = null;
      String status = null;
      boolean odd = true;
      try {
         String oldQS = buildQS(req);
         String pid = req.getParameter("pid");
         conn = (Connection) session.getValue("conn");
         if (pid == null || pid.trim().equals("")) pid = "-1";

         out.println("<html>");
         out.println("<head>");
         out.println("<title>Details Projects</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Projects - Statistics</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         // General Data
         stmt = conn.createStatement();
         String strSQL = "SELECT NAME, COMM, STATUS " +
            "FROM gdbadm.V_PROJECTS_1 WHERE " +
            "PID=" + pid ;
         rset = stmt.executeQuery(strSQL);
         rset.next();
         name = rset.getString("NAME");
         comm = rset.getString("COMM");
         status = rset.getString("STATUS");
         out.println("</tr><tr><td></td><td></td><tr><td></td><td>");
         out.println("<table><tr>");
         out.println("<td width=15 style=\"WIDTH: 15px\">");
         out.println("</td><td>");
         out.println("<table cellspacing=0 cellpadding=0 border=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<b>General</b>");
         out.println("</td><td>&nbsp;</td></tr>");
         out.println("<tr>");
         out.println("<td>Name</td><td>" + formatOutput(session, name, 20) + "</td></tr>");
         out.println("<tr><td>Comment</td><td>" + formatOutput(session, comm, 256) + "</td></tr>");
         out.println("<tr><td>Status</td><td>" + status + "</td></tr>");
         out.println("</table>");

         out.println("</tr><tr><td></td><td></td><tr><td></td><td>");

         // Species
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME FROM " +
                                  "GdbAdm.V_Species_2 where pid=" + pid);
         out.println("<table cellspacing=0 cellpadding=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<b>Species</b>");
         out.println("</td><td>&nbsp;</td></tr>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td width=200 style=\"WIDTH: 200px\">Name</td></tr>");
         odd = true;
         while (rset.next()) {
            out.println("<TR align=left ");
            if (odd) {
               out.println("bgcolor=white>");
            } else {
               out.println("bgcolor=lightgrey>");
            }
            odd = !odd;
            out.println("<td>");
            out.println(formatOutput(session, rset.getString("NAME"), 20));
            out.println("</td></tr>");
         }
         out.println("</table>");

         out.println("</tr><tr><td></td><td></td><tr><td></td><td>");

         // Sampling units
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SNAME, COMM, STATUS FROM " +
                                  "GdbAdm.V_Sampling_units_2 where pid=" + pid);
         out.println("<table cellspacing=0 cellpadding=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<b>Sampling units</b>");
         out.println("</td><td>&nbsp;</td></tr>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td width=200 style=\"WIDTH: 200px\">Name</td>" +
                     "<td width=100 style=\"WIDTH: 100px\">Species</td>" +
                     "<td width=200 style=\"WIDTH: 200px\">Comment</td>" +
                     "<td width=70 style=\"WIDTH: 70px\">Status</td></tr>");
         odd = true;
         while (rset.next()) {
            out.println("<TR align=left ");
            if (odd) {
               out.println("bgcolor=white>");
            } else {
               out.println("bgcolor=lightgrey>");
            }
            odd = !odd;
            out.println("<td>");
            out.println(formatOutput(session, rset.getString("NAME"), 20));
            out.println("</td>");
            out.println("<td>");
            out.println(formatOutput(session, rset.getString("SNAME"), 20));
            out.println("</td>");
            out.println("<td>");
            out.println(formatOutput(session, rset.getString("COMM"), 20));
            out.println("</td>");
            out.println("<td>");
            out.println(rset.getString("STATUS"));
            out.println("</td>");
            out.println("</tr>");
         }
         out.println("</table>");

         out.println("</tr><tr><td></td><td></td><tr><td></td><td>");

         // Roles
         out.println("<table cellspacing=0 cellpadding=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<b>Roles</b>");
         out.println("</td><td>&nbsp;</td></tr>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td width=200 style=\"WIDTH: 200px\">Name</td>" +
                     "<td width=200 style=\"WIDTH: 200px\">Comment</td></tr>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, COMM FROM " +
                                  "GdbAdm.V_Roles_1 where pid=" + pid);
         odd = true;
         while (rset.next()) {
            out.println("<TR align=left ");
            if (odd) {
               out.println("bgcolor=white>");
            } else {
               out.println("bgcolor=lightgrey>");
            }
            odd = !odd;
            out.println("<td>");
            out.println(formatOutput(session, rset.getString("NAME"), 20));
            out.println("</td><td>");
            out.println( formatOutput(session, rset.getString("COMM"), 256));
            out.println("</td></tr>");
         }
         out.println("</table>");

         out.println("</tr><tr><td></td><td></td><tr><td></td><td>");

         // Users
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, RNAME FROM " +
                                  "GdbAdm.V_USERS_2 where pid=" + pid);
         out.println("<table cellspacing=0 cellpadding=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<b>Users</b>");
         out.println("</td><td>&nbsp;</td></tr>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td width=200 style=\"WIDTH: 200px\">Name</td>" +
                     "<td width=200 style=\"WIDTH: 200px\">Role</td></tr>");
         odd = true;
         while (rset.next()) {
            out.println("<TR align=left ");
            if (odd) {
               out.println("bgcolor=white>");
            } else {
               out.println("bgcolor=lightgrey>");
            }
            odd = !odd;
            out.println("<td>");
            out.println(formatOutput(session, rset.getString("NAME"), 32));
            out.println("</td><td>");
            out.println(formatOutput(session, rset.getString("RNAME"), 20));
            out.println("</td></tr>");
         }
         out.println("</table>");

         out.println("</tr><tr><td></td><td></td><tr><td></td><td>");
         out.println("</tr><tr><td></td><td></td><tr><td></td><td>");
         out.println("</td></tr><tr><td></td><td>");
         out.println("<form>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='history.go(-1);'>");
         out.println("</form>");
         out.println("</td>");
         out.println("</tr></table>");

         out.println("</body></html>");
      } catch (SQLException e)
      {
          e.printStackTrace();
         out.println("<PRE>");
         e.printStackTrace(out);
         out.println("</PRE>");
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
   }
   /***************************************************************************************
    * *************************************************************************************
    * The new project page
    */
   private void writeNew(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createFilter(req, res, conn)) {
            // Modify request to fit the edit page and call writeEditPage
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
      String sid = null, newQS, pid, oper, item;
      try {
         conn = (Connection) session.getValue("conn");
         pid = (String) session.getValue("PID");
         sid = req.getParameter("sid");
         newQS = removeQSParameterOper(req.getQueryString());
         oper = req.getParameter("oper");

         if (oper == null || oper.trim().equals("")) oper = "SEL_CHANGED";
         item = req.getParameter("item");
         if (item == null || item.trim().equals("")) item = "";
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeNewScript(out);
         out.println("<title>New Project</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Filters - New</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<form method=post action=\"" +
                     getServletPath("viewFilt/new?") + newQS + "\">");

         out.println("<table width=400 border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table>");
         out.println("<tr>");
         // Species
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID, NAME FROM V_SPECIES_2 WHERE PID=" +
                                  pid + " ORDER BY NAME");
         out.println("<td>Species<br>");
         out.println("<select name=s width=100 " +
                     "style=\"WIDTH: 100px\">");
         while (rset.next()) {
            if (rset.getString("SID").equals(sid))
               out.println("<option selected value=\"" + rset.getString("SID") + "\">" +
                           rset.getString("NAME") + "</option>");
            else
               out.println("<option value=\"" + rset.getString("SID") + "\">" +
                           rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td>");

         // Name
         out.println("<td>Name<br>");
         out.println("<input type=text name=n value=\"\" width=100 " +
                     "style=\"WIDTH: 100px\" maxlength=20>");
         out.println("</td>");
         // Comment
         out.println("<td>Comment<br>");
         out.println("<input type=text name=c value=\"\" width=200 " +
                     "style=\"WIDTH: 200px\">");
         out.println("</td>");
         out.println("</tr>");
         // Expression
         out.println("<tr>");
         out.println("<td colspan=3>GQL Expression<br>");
         out.print("<textarea name=e cols=80 rows=15></textarea>");
         out.println("</td>");
         out.println("</tr>");
         // Some buttons
         out.println("<tr>");
         out.println("<td colspan=3>");
         out.println("<table border=0 cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td><input type=button value=\"Cancel\" " +
                     "width=100 style=\"WIDTH: 100px\" " +
                     "onClick='Javascript:location.href=\"" +
                     getServletPath("viewFilt?") + newQS + "\";'>");
         out.println("&nbsp;</td>");
         out.println("<td><input type=button value=\"Create\" " +
                     "width=100 style=\"WIDTH: 100px\" " +
                     "onClick='valForm();'>");
         out.println("&nbsp;</td>");
         out.println("</tr>");
         out.println("</table>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=item value=\"\">");
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("</td></tr></table>");
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
      out.println("	if ( (\"\" + document.forms[0].c.value) != \"\" &&");
      out.println("       document.forms[0].c.value.length > 255) {");
      out.println("			alert('Comment must be less than 255 characters!');");
      out.println("			rc = 0;");
      out.println("	}");
      out.println("	if ( (\"\" + document.forms[0].n.value) == \"\") {");
      out.println("			rc = 0;");
      out.println("	}");
      out.println("	");
      out.println("	");
      out.println("	if (rc) {");
      out.println("		if (confirm('Are you sure that you want to create the filter?')) {");
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

   private void writeUserScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm() {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if (rc) {");
      out.println("		if (confirm('Are you sure that you want to update the user(s) roles?')) {");
      out.println("			document.forms[0].oper.value = 'UPDATE'");
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
      // writeeditPage handles oper=TEST
      if (oper == null) oper = "";
      if (oper.equals("DELETE")) {
         if (deleteFilter(req, res, conn))
            writeFrame(req, res);

      } else if (oper.equals("UPDATE")) {
         if (updateFilter(req, res, conn))
            writeEditPage(req, res, "", "", makeEditScript("", ""));
      } else if (oper.equals("TEST") ) {
         // testFilter calls writeEditPage when done
         if (updateFilter(req, res, conn) )
            testFilter(req, res);
      } else
         writeEditPage(req, res, "", "", makeEditScript("", "") );
   }
   private void writeEditPage(HttpServletRequest req, HttpServletResponse res,
                              String message, String result, String script)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String sid, suid, newQS, pid, fid, name, comm, express, oper, item;
      try {
         conn = (Connection) session.getValue("conn");
         pid = (String) session.getValue("PID");
         suid = req.getParameter("su");
         if (suid == null) suid = "-1";
         fid = req.getParameter("fid");
         newQS = removeQSParameterOper(req.getQueryString());
         oper = req.getParameter("oper");

         if (oper == null || oper.trim().equals("")) oper = "SEL_CHANGED";
         item = req.getParameter("item");
         if (item == null || item.trim().equals("")) item = "";

         // Retrieve the data for this filter
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID, NAME, COMM, EXPRESSION FROM V_FILTERS_1 " +
                                  "WHERE FID=" + fid);
         if (!rset.next() ) {
            // This should almost be impossible
            throw new Exception("Filter query didn't return any data.");
         }
         sid = rset.getString("SID");
         name = rset.getString("NAME");
         comm = rset.getString("COMM");
         express = rset.getString("EXPRESSION");
         rset.close();
         stmt.close();
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println(script);

         out.println("<title>Edit filter</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Filters - Edit</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<form method=post action=\"" +
                     getServletPath("viewFilt/edit?") + newQS + "\">");

         out.println("<table width=400 border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table>");
         out.println("<tr>");
         // Species
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID, NAME FROM V_SPECIES_2 WHERE PID=" +
                                  pid + " ORDER BY NAME");
         out.println("<td>Species<br>");
         out.println("<select name=s width=100 " +
                     "style=\"WIDTH: 100px\">");
         while (rset.next()) {
            if (rset.getString("SID").equals(sid))
               out.println("<option selected value=\"" + rset.getString("SID") + "\">" +
                           rset.getString("NAME") + "</option>");
            else
               out.println("<option value=\"" + rset.getString("SID") + "\">" +
                           rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td>");

         // Name
         out.println("<td>Name<br>");
         out.println("<input type=text name=n value=\"" + name + "\" width=100 " +
                     "style=\"WIDTH: 100px\" maxlength=20>");
         out.println("</td>");
         // Comment
         out.println("<td>Comment<br>");
         out.println("<input type=text name=c value=\"" + replaceNull(comm, "") + "\" width=200 " +
                     "style=\"WIDTH: 200px\">");
         out.println("</td>");
         out.println("</tr>");
         // Expression
         out.println("<tr>");
         out.println("<td colspan=3>GQL Expression<br>");
         out.print("<textarea name=e cols=85 rows=10 onchange='JavaScrip:exprHasChanged=true;'>");
         out.print( replaceNull(express, "") );
         out.print("</textarea>");
         out.println("</td>");
         out.println("</tr>");
         // Status and result fields
         out.println("<tr>");
         out.println("<td colspan=3>");
         out.println("<table border=0 cellspacing=0 cellpading=0>");
         out.println("<td width=300>");
         out.println("Status<br>");
         out.println("<table border=0 cellpadding=0 cellspaing=0>"); //1
         out.println("<tr><td width=290>");
         out.println(message);
         out.println("</td></tr></table>");
         out.println("</td>");
         out.println("<td width=300>");
         out.println("Result<br>");
         out.println("<table border=0 cellpading=0 cellspacing=0>");//1
         out.println("<tr><td width=290>");
         out.println(result);
         out.println("</td></tr></table>");
         out.println("</td></tr></table>");
         out.println("</td></tr>");
         // Some buttons
         out.println("<tr>");
         out.println("<td colspan=3>");
         out.println("<table border=0 cellpading=0 cellspacing=0>");//1
         out.println("<tr><td>");
         out.println("<table border=0 cellspacing=0 cellpading=0>");
         out.println("<tr>");
         // Back button
         out.println("<td><input type=button value=\"Back\" " +
                     "width=70 style=\"WIDTH: 70px\" " +
                     "onClick='Javascript:location.href=\"" +
                //     getServletPath("viewFilt?") + newQS + "\";'>&nbsp;</td>");
                      getServletPath("viewFilt?&RETURNING=YES")  + "\";'>&nbsp;</td>");

         out.println("</tr></table>");
         out.println("</td>");
         out.println("<td>");  // beginning of cell with border=1
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr>");
         // Sampling units
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid + " AND SID=" + sid + " ORDER BY NAME");
         out.println("<td><select name=su width=100 style=\"WIDTH: 100px\">");
         while (rset.next()) {
            if (suid.equals(rset.getString("SUID")))
               out.println("<option selected value=\"" + rset.getString("SUID") + "\">" +
                           rset.getString("NAME") + "</option>");
            else
               out.println("<option value=\"" + rset.getString("SUID") + "\">" +
                           rset.getString("NAME") + "</option>");
         }
         out.println("</select></td>");
         rset.close();
         stmt.close();
         // Update
         //   out.println("<td><input type=button width=70 style=\"WIDTH: 70px\" " +
         out.println("<td><input type=button style=\"WIDTH: 100px\"" +
                     "value=\"Update & Test\" onClick='condSubmit(\"TEST\");'>&nbsp;</td>");

         // Display
         /*
           out.println("<td><input type=button width=70 style=\"WIDTH: 70px\" value=\"Display\" " +
           "onClick='popupDisplay();'>&nbsp;</td>");
         */
         out.println("</tr></table>");
         out.println("</td>");
         out.println("<td>");
         out.println("<table border=0 cellpading=0 callspacing=0>");
         out.println("<tr>");
         // Delete
         out.println("<td><input type=button value=\"Delete\" " +
                     "width=70 style=\"WIDTH: 70px\" " +
                     "onClick='condSubmit(\"DELETE\");'>&nbsp;</td>");
         // Errors
         out.println("<td><input type=button value=\"Errors\" " +
                     "width=70 style=\"WIDTH: 70px\" " +
                     "onClick='popupErr();'>&nbsp;</td>");
         // SQL
         out.println("<td><input type=button value=\"SQL\" " +
                     "width=70 style=\"WIDTH: 70px\" " +
                     "onClick='popupSQL();'>&nbsp;</td>");
         out.println("</tr></table>");
         out.println("</td>"); // end of cell with border=1

         out.println("</tr>");
         out.println("</table>");
         out.println("</td>");
         out.println("</tr>");
         out.println("</table>"); // End of table with border=1
         out.println("</td>");
         out.println("</tr>");
         out.println("</table>");

         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=item value=\"\">");
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("<input type=hidden name=RETURNING value=YES>");
         out.println("</td></tr></table>");
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


   /**
    * Creates a new filter.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if filter was created.
    *         False if filter was not created.
    */
   private boolean createFilter(HttpServletRequest request,
                                HttpServletResponse response,
                                Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         String name, comm, express, sid, pid, id;
         connection.setAutoCommit(false);
         pid = (String) session.getValue("PID");
         id = (String) session.getValue("UserID");
         name = request.getParameter("n");
         comm = request.getParameter("c");
         express = request.getParameter("e");
         sid = request.getParameter("s");
         DbFilter dbf = new DbFilter();
         dbf.CreateFilter(connection, pid, name, express, comm,
                          Integer.parseInt(sid), Integer.parseInt(id) ); 
         errMessage = dbf.getErrorMessage();
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
      
      commitOrRollback(connection, request, response, "Filters.New.Create",
                       errMessage, "viewFilt/new", isOk); 
      return isOk;
   }

   
   /**
    * Deletes a filter.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if filter was removed.
    *         False if filter was not removed.
    */
   private boolean deleteFilter(HttpServletRequest request,
                                HttpServletResponse response, 
                                Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         String fid;
         connection.setAutoCommit(false);
         fid = request.getParameter("fid");
         DbFilter dbf = new DbFilter();
         dbf.DeleteFilter(connection, fid);
         errMessage = dbf.getErrorMessage();
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

      commitOrRollback(connection, request, response,
                       "Filters.Edit.Delete", errMessage, "viewFilt/edit", isOk);
      return isOk;
   }


   private boolean updateFilter(HttpServletRequest request,
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
         String comm;
         String sid;
         String express;
         String pid;
         String fid;
         String id;
         String oldQS = request.getQueryString();
         name = request.getParameter("n");
         sid = request.getParameter("s");
         fid = request.getParameter("fid");
         comm = request.getParameter("c");
         express = request.getParameter("e");
         id = (String) session.getValue("UserID");
         DbFilter dbf = new DbFilter();
         dbf.UpdateFilter(connection, fid, name, express, comm,
                          Integer.parseInt(sid), Integer.parseInt(id) );
         errMessage = dbf.getErrorMessage();
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

      commitOrRollback(connection, request, response,
                       "Filters.Edit.Update", errMessage, "viewFilt/edit",
                       isOk); 
      return isOk;
   }


   private void writeEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to delete the project?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the project?')) {");
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

   
 

   private boolean authorized(HttpServletRequest req, HttpServletResponse res) {
      HttpSession session = req.getSession(true);
      String extPath = req.getPathInfo();
      boolean ok = true;
      String title = "";
      int privileges[] = (int[]) session.getValue("PRIVILEGES");
      try {
         if (extPath == null || extPath.trim().equals("") ) extPath = "/";
         if (extPath.equals("/") ||
             extPath.equals("/bottom") ||
             extPath.equals("/middle") ||
             extPath.equals("/top") ) {
            // We neew the privilege FLT_R for all these
            title = "Filters - View & Edit";
            if ( privDependentString(privileges, FLT_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/details") ) {
            // We neew the privilege FLT_R for all these
            title = "Filters - Details";
            if ( privDependentString(privileges, FLT_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/edit") ) {
            // We neew the privilege FLT_W
            title = "Filters - Edit";
            if ( privDependentString(privileges, FLT_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/new") ) {
            // We neew the privilege FLT_W
            title = "Filters - New";
            if ( privDependentString(privileges, FLT_W, "", null) == null)
               ok = false;
         }

         if (!ok)
            writeUnauthorizedPage(res, title);
      } catch (Exception e) {
         e.printStackTrace(System.err);
         ok = false;
      }
      return ok;
   }

   private void writeUnauthorizedPage(HttpServletResponse res, String title) {
      try {
         res.setContentType("text/html");
         res.setHeader("Pragma", "no-cache");
         res.setHeader("Cache-Control", "no-cache");
         PrintWriter out = res.getWriter();
         out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out.println("<html>");
         out.println("<head>");
         out.println("<title>Unauthorized user</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">");
         out.println(title);
         out.println("</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");
         out.println("<p>");
         out.println("<b>");
         out.println("You are not authorized to view this page.");
         out.println("</b>");
         out.println("</td></tr>");

         out.println("<tr><td></td><td></td></tr><td></td><td>");
         out.println("</td></tr></table>");
         out.println("</body>");
         out.println("</html>");
      } catch (Exception e)	{
         e.printStackTrace(System.err);
      } finally {
         ;
      }

   }

   private void testFilter(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
      HttpSession session = req.getSession(true);
      //		PrintWriter out = res.getWriter();
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      String pid = (String) session.getValue("PID");
      String sid = req.getParameter("s");
      String suid = req.getParameter("su");
      String result = "&nbsp;";
      String status = "&nbsp;";
      String script = "";
      if (sid == null) sid =  "-1";
      if (suid == null) suid = "-1";
      String gqlExpression = req.getParameter("e");
      if (gqlExpression == null) gqlExpression = "";
      GqlTranslator gqltr = new GqlTranslator(pid, suid, gqlExpression, conn);
      gqltr.translate();
      String filter = "SELECT count(ind.iid) " + gqltr.getFilter();
      Vector comments = gqltr.getComments();
      Vector code = gqltr.getCode();
      if (comments.size() != 0) {
         // We encountered comments in the gql-expression
         script = makeEditScript(convFilter(filter), convComments(comments, code) );
         result = "";
         status = "GQL code contains errors.";
      } else {
         try {
            stmt = conn.createStatement();
            rset = stmt.executeQuery(filter);
            if (rset.next()) {
               script = makeEditScript(convFilter(filter), "");
               result = "Filter will return " + rset.getString(1) + " rows.";
               status = "OK";
            } else {
               result = "";
               status = "Error";
               comments.removeAllElements();
               code.removeAllElements();
               comments.addElement("Application:");
               code.addElement("DB query didn't return any rows.");
               script = makeEditScript(convFilter(filter), convComments(comments, code) );

            }
         } catch (SQLException e) {
             e.printStackTrace();
            result = "";
            status = "Error";
            comments.removeAllElements();
            code.removeAllElements();
            comments.addElement("Application:");
            code.addElement("Unexpected exception, "); // + e.getMessage() );
            script = makeEditScript(convFilter(filter), convComments(comments, code) );
         } catch (Exception ge) {
            ge.printStackTrace(System.err);
         } finally {
            try {
               if (rset != null) rset.close();
               if (stmt != null) stmt.close();
            } catch (SQLException ignored) {}
         }
      }
      writeEditPage(req, res, status, result, script);
   }
   private String convComments(Vector comments, Vector code) {
      StringBuffer output = new StringBuffer(256);
      if (comments.size() == 0 ) {
         output.append(new String("Success!"));
      } else {
         for (int i = 0; i < comments.size() && i < code.size(); i++) {
            output.append((String) comments.elementAt(i) + " [" + (String) code.elementAt(i) + "]");
            output.append(new String("\\n"));
         }
      }
      return output.toString();
   }
   private String convFilter(String filter) {
      StringBuffer output = new StringBuffer(2000);
      // Damn this is uggly
      for (int i = 0; i < filter.length(); i++) {
         if(filter.charAt(i) == '\n')
            output.append("\\n");
         else if (filter.charAt(i) == '\r')
            output.append("\\r");
         else if (filter.charAt(i) == '\t')
            output.append("\\t");
         else
            output.append(filter.charAt(i));
      }
      return output.toString();
   }
   private String makeEditScript(String sql, String comments) {
      StringBuffer out = new StringBuffer(1024);
      out.append("<SCRIPT language=\"JavaScript\">\n<!--\n");
      out.append("var exprHasChanged = false;\n");
      out.append("var SqlWin = null;\n");
      out.append("var ErrWin = null;\n");
      out.append("var DisplayWin = null;\n");
      out.append("function condSubmit(oper) {\n");
      out.append("	if ('DELETE' == oper.toUpperCase()) {\n");
      out.append("		if (confirm('Are you sure you want to delete this filter?')) {\n");
      out.append("			document.forms[0].oper.value='DELETE';\n");
      out.append("			document.forms[0].submit();\n");
      out.append("		}\n");
      out.append("	} else {\n");
      out.append("		document.forms[0].oper.value=oper;\n");
      out.append("//		if (SqlWin) {\n");
      out.append("//			SqlWin.close();\n");
      out.append("//			SqlWin = null;\n");
      out.append("//		}\n");
      out.append("//		if (ErrWin) {\n");
      out.append("//			ErrWin.close();\n");
      out.append("//			ErrWin = null;\n");
      out.append("//		}\n");
      out.append("		document.forms[0].submit();\n");
      out.append("	}\n");
      out.append("}\n");
      out.append("function popupSQL() {\n");
      out.append("	if (exprHasChanged) {\n");
      out.append("		alert(\"The GQL-expression has changed since last time it was compiled.\\nPlease test the expression first!\");\n");
      out.append("		return;\n");
      out.append("	} else { //if (!SqlWin) {\n");
      out.append("		\n");
      out.append("//		var gt = unescape(\"%3E\");\n");
      out.append("		var command = \"window.open('', 'sql', \";\n");
      out.append("		command += \"'toolbar=no,location=no,directories=no,status=no,\";\n");
      out.append("		command += \"menubar=no,scrollbars=yes,resizable=yes,\";\n");
      out.append("		command += \"width=600,height=400')\";\n");
      out.append("		SqlWin = eval(command);\n");
      out.append("		SqlWin = eval(command);\n");
      out.append("		SqlWin.document.open();\n");
      out.append("		SqlWin.document.writeln(\"<html><head><title>\");\n");
      out.append("		SqlWin.document.writeln(\"SQL code\");\n");
      out.append("		SqlWin.document.writeln(\"</title></head>\");\n");
      out.append("		SqlWin.document.writeln(\"<body BGCOLOR='#008B8B' ONBLUR='self.focus()'>\");\n");
      out.append("    SqlWin.document.writeln(\"<table height=90% width=100% border=0 cellpadding=6>\");\n");
      out.append("    SqlWin.document.writeln(\"<tr><td height=80%>\");\n");
      out.append("    SqlWin.document.writeln(\"<FONT COLOR=WHITE FACE='Arial, Helvetica' size='3'><b>\");\n");
      out.append("		SqlWin.document.writeln(\"<code><PRE>\");\n");
      out.append("		SqlWin.document.writeln(\"" + sql + "\");\n");
      out.append("		SqlWin.document.writeln(\"</PRE><br>\");\n");
      out.append("    SqlWin.document.writeln(\"<tr><td height=20%>\");\n");
      out.append("		SqlWin.document.writeln(\"<form><input type=button value=Close onClick='self.close()'></form>\");\n");
      out.append("    SqlWin.document.writeln(\"</table>\");\n");
      out.append("		SqlWin.document.writeln(\"</body></html>\");\n");
      out.append("    SqlWin.document.close();\n");
      out.append("	}\n");
      out.append("}\n");
      out.append("\n");
      out.append("function popupErr() {\n");
      out.append("	if (exprHasChanged) {\n");
      out.append("		alert(\"The GQL-expression has changed since last time it was compiled.\\nPlease Test or save the expression first!\");\n");
      out.append("		return;\n");
      out.append("	} else if (!ErrWin) {\n");
      out.append("		\n");
      out.append("		var gt = unescape(\"%3E\");\n");
      out.append("		var command = \"window.open('', 'comm', \";\n");
      out.append("		command += \"'toolbar=no,location=no,directories=no,status=no,\";\n");
      out.append("		command += \"menubar=no,scrollbars=yes,resizable=yes,\";\n");
      out.append("		command += \"width=600,height=400')\";\n");
      out.append("		ErrWin = eval(command);\n");
      out.append("		ErrWin = eval(command);\n");
      out.append("		ErrWin.document.open();\n");
      out.append("		ErrWin.document.writeln(\"<html><head><title>\");\n");
      out.append("		ErrWin.document.writeln(\"Errors and warnings\");\n");
      out.append("		ErrWin.document.writeln(\"</title></head>\");\n");
      out.append("		ErrWin.document.writeln(\"<body BGCOLOR='#008B8B' ONBLUR='self.focus()'>\");\n");
      out.append("    ErrWin.document.writeln(\"<table height=90% width=100% border=0 cellpadding=6>\");\n");
      out.append("    ErrWin.document.writeln(\"<tr><td height=80%>\");\n");
      out.append("    ErrWin.document.writeln(\"<FONT COLOR=WHITE FACE='Arial, Helvetica' size='3'><b>\");\n");
      out.append("		ErrWin.document.writeln(\"<code><PRE>\");\n");
      out.append("		ErrWin.document.writeln(\"" + comments + "\");\n");
      out.append("		ErrWin.document.writeln(\"</PRE><br>\");\n");
      out.append("    ErrWin.document.writeln(\"<tr><td height=20%>\");\n");
      out.append("		ErrWin.document.writeln(\"<form><input type=button value=Close onClick='self.close()'></form>\");\n");
      out.append("    ErrWin.document.writeln(\"</table>\");\n");
      out.append("		ErrWin.document.writeln(\"</body></html>\");\n");
      out.append("	}\n");
      out.append("}\n");
      out.append("function popupDisplay() {\n");
      out.append("	if (exprHasChanged) {\n");
      out.append("		alert(\"The GQL-expression has changed since last time it was compiled.\\nPlease update the expression first!\");\n");
      out.append("		return;\n");
      out.append("	} //else if (!ErrWin) {\n");
      out.append("		\n");
      out.append("		var gt = unescape(\"%3E\");\n");
      out.append("		var command = \"window.open(\";\n"); //'', 'comm', \";\n");
      out.append("		command += \"'" + getServletPath("viewFilt") + "')\";\n"); //toolbar=no,location=no,directories=no,status=no,\";\n");
      out.append("//		command += \"menubar=no,scrollbars=yes,resizable=yes,\";\n");
      out.append("//		command += \"width=600,height=400')\";\n");
      out.append("		DisplayWin = eval(command);\n");
      out.append("//		ErrWin = eval(command);\n");
      out.append("//		ErrWin.document.open();\n");
      out.append("//		ErrWin.document.writeln(\"<html><head><title>\");\n");
      out.append("//		ErrWin.document.writeln(\"Errors and warnings\");\n");
      out.append("//		ErrWin.document.writeln(\"</title></head>\");\n");
      out.append("//		ErrWin.document.writeln(\"<body BGCOLOR='#008B8B' ONBLUR='self.focus()'>\");\n");
      out.append("//    ErrWin.document.writeln(\"<table height=90% width=100% border=0 cellpadding=6>\");\n");
      out.append("//    ErrWin.document.writeln(\"<tr><td height=80%>\");\n");
      out.append("//    ErrWin.document.writeln(\"<FONT COLOR=WHITE FACE='Arial, Helvetica' size='3'><b>\");\n");
      out.append("//		ErrWin.document.writeln(\"<code><PRE>\");\n");
      out.append("//		ErrWin.document.writeln(\"" + comments + "\");\n");
      out.append("//		ErrWin.document.writeln(\"</PRE><br>\");\n");
      out.append("//    ErrWin.document.writeln(\"<tr><td height=20%>\");\n");
      out.append("//		ErrWin.document.writeln(\"<form><input type=button value=Close onClick='self.close()'></form>\");\n");
      out.append("//    ErrWin.document.writeln(\"</table>\");\n");
      out.append("//		ErrWin.document.writeln(\"</body></html>\");\n");
      out.append("//	}\n");
      out.append("}\n");
      out.append("\n");

      out.append("// -->\n</SCRIPT>");
      return out.toString();
   }

}
