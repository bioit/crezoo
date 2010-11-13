/*
  $Log$
  Revision 1.9  2005/03/24 15:12:45  heto
  Working with removing oracle dep.

  Revision 1.8  2005/03/22 16:22:59  heto
  Removing CallableStatement.
  Fixed bugs in GUI

  Revision 1.7  2005/02/04 15:58:41  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.6  2005/01/31 16:16:41  heto
  Changing database to PostgreSQL. Problems with counts and selection buttons...

  Revision 1.5  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.4  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.3  2002/10/22 06:08:12  heto
  rebuilt the "back-buttons".
  Dont save the request object, save the URL instead.
  New function.

  Revision 1.2  2002/10/18 11:41:11  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:05  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.12  2001/05/31 07:07:07  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.11  2001/05/30 13:28:52  frob
  Debug printouts removed.

  Revision 1.10  2001/05/29 14:47:21  frob
  Removed unused method.

  Revision 1.9  2001/05/22 06:17:00  roca
  Backfuncktionality fixed for all (?) servlets/subpages

  Revision 1.8  2001/05/15 13:36:28  roca
  After merge problems from last checkin..

  Revision 1.7  2001/05/14 07:13:40  frob
  Changed all methods that used to call writeError() to write an error page. These
  methods now calls the general method commitOrRollback() which does the commit or
  rollback operation as well as handle any errors. writeError() was removed.

  Revision 1.6  2001/05/03 07:57:46  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.5  2001/05/03 06:19:17  frob
  Calls to removeSid changed to use the general removeQSParameter.
  The previously called method is removed.

  Revision 1.4  2001/05/03 06:17:26  frob
  Calls to removeOper changed to use the general removeQSParameter.
  The previously called method is removed.

  Revision 1.3  2001/05/03 06:15:17  frob
  Added log header and indented the file.

*/

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;

public class viewSU extends SecureArexisServlet
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
      } else if (extPath.equals("/middle")) {
         writeMiddle(req, res);
      } else if (extPath.equals("/new")) {
         writeNew(req, res);
      } else if (extPath.equals("/bottom")) {
         writeBottom(req, res);
      } else if (extPath.equals("/check")) {
         writeCheck(req, res);
      } else if (extPath.equals("/details")) {
         writeDetails(req, res);
      } else if (extPath.equals("/edit")) {
         writeEdit(req, res);
      }
   }
   private void writeFrame(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException 
   {
      HttpSession session = req.getSession(true);
      // set content type and other response header fields first
      res.setContentType("text/html");
      PrintWriter out = res.getWriter();
      try 
      {
         // Check if redirection is needed
         res = checkRedirectStatus(req,res); 
         //req=getServletState(req,session);

         String topQS = buildQS(req);
         //System.err.println("tQS=" +topQS);
         String bottomQS = topQS.toString();
         // System.err.println("bQS=" +bottomQS);

         out.println("<html>"
                     + "<HEAD>"
                     + " <TITLE>View Sampling units</TITLE>"
                     + "</HEAD>"
                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">"
                     + "<frame name=\"viewsutop\" "
                     + "src=\""+getServletPath("viewSU/top?") + topQS + "\""
                     + "scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewsumiddle\" "
                     + "src=\""+getServletPath("viewSU/middle?") + topQS + "\""
                     + "scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewsubottom\""
                     + "src=\""+getServletPath("viewSU/bottom?") + bottomQS + "\" "
                     + "scrolling=\"auto\" marginheight=\"0\" frameborder=\"0\"></frameset>"
                     + "<noframes><body><p>"
                     + "This page uses frames, but your browser doesn't support them."
                     + "</p></body></noframes></frameset>"
                     + "</HTML>");
      } catch (Exception e)
      {
         out.println("<PRE>");
         e.printStackTrace(out);
         out.println("</PRE>");
      }
      finally {
      }
   }
   private String buildQS(HttpServletRequest req) 
   {
      StringBuffer output = new StringBuffer(512);
      HttpSession session = req.getSession(); // true
      Connection conn = (Connection) session.getAttribute("conn");
      String pid = (String) session.getAttribute("PID");
      String sid, old_sid, suid, orderby, status;
      String action = null;
      old_sid = (String) session.getValue("SID");
      sid = req.getParameter("sid");
      if (sid == null)
         sid = old_sid;
      if (sid == null)
         findSid(conn, pid);
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
      output.append(setIndecis(sid, old_sid, action, req, session));

      suid = req.getParameter("suid");
      if (suid != null)
         output.append("&suid=").append(suid);
      status = req.getParameter("STATUS");
      if (status != null)
         output.append("&STATUS=").append(status);
      output.append("&sid=").append(sid);
      if (req.getParameter("oper") != null)
         output.append("&oper=").append(req.getParameter("oper"));
      //		if (req.getParameter("new_su_name") != null)
      //			output.append("&new_su_name=").append(req.getParameter("new_su_name"));
      orderby = req.getParameter("ORDERBY");
      if (orderby != null)
         output.append("&ORDERBY=").append(orderby);
      else
         output.append("&ORDERBY=NAME");

      return output.toString().replace('%', '*');
   }
   private String findSid(Connection conn, String pid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID FROM gdbadm.V_SPECIES_2 WHERE PID=" +
                                  pid + " ORDER BY NAME");
         if (rset.next()) {
            ret = rset.getString("SID");
         } else {
            ret = "-1";
         }
      } catch (SQLException e) {
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
   private String setIndecis(String sid, String old_sid, String action, HttpServletRequest req, HttpSession session) {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(sid, req, session);
      maxRows = getMaxRows(session);
      if (req.getParameter("STARTINDEX") != null && old_sid != null && old_sid.equalsIgnoreCase(sid))
         startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
      else
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


   private int countRows(String sid, HttpServletRequest req, HttpSession session) {
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer(512);
      try {
         sbSQL.append("SELECT count(*) "
                      + "FROM gdbadm.V_SAMPLING_UNITS_3 WHERE SID=" + sid + " ");
         sbSQL.append(buildFilter(req,false));
         stmt = conn.createStatement();
         rset = stmt.executeQuery(sbSQL.toString());
         rset.next();
         return rset.getInt(1);
      } catch (SQLException e) {
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
      HttpSession session = req.getSession(true);
      StringBuffer filter = new StringBuffer(256);
      String pid = (String) session.getValue("PID");
      String status = req.getParameter("STATUS");
      if (status != null)
      {
         filter.append(" AND STATUS='" + status +"' ");
      }
      if (pid == null)
         pid = "-1";
      filter.append(" AND PID=" + pid);
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
      PrintWriter out = res.getWriter();
      String oper;
      oper = req.getParameter("oper");
      if (oper == null || "".equals(oper))
         oper = "SELECT";

/*
      if (oper.equals("NEW_SU")) {
         if (!createSU(req, res))
            System.out.println("fail..");
         ; //return;
      }
*/
      HttpSession session = req.getSession(true);
      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null;
      int currentPrivs[];
      int startIndex = 0, rows = 0, maxRows = 0;
      String sid, orderby, oldQS, newQS, action, pid, status;

      try {
         conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();
         currentPrivs = (int [])session.getValue("PRIVILEGES");
         pid = (String) session.getValue("PID");
         sid = req.getParameter("sid");
         //System.err.println("(top) sid=" +sid);
         status =req.getParameter("STATUS");
         maxRows = getMaxRows(session);
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
         if (sid == null) sid = "-1";
         if (orderby == null) orderby = "NAME";
         if (action == null) action = "NOP";
         if(status == null) status = "E";
         if (pid == null || "".equalsIgnoreCase(pid))
            pid = "-1";

         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+ getURL("style/view.css")+"\">");
         out.println("<base target=\"content\">");


         writeTopScript(out);
         out.println("<title>View Sampling units</title>");
         out.println("</head>");
         out.println("<body bgcolor=\"#ffffd0\">"
                     +"<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
                     +"<tr>"
                     + "<td width=\"14\" rowspan=\"3\">" +"</td>"
                     +"<td width=\"736\" colspan=\"2\" height=\"15\">"
                     +"<form method=get action=\"" +getServletPath("viewSU") +"\">"
                     +"<p align=\"center\"><font size=\"2\"><b  style=\"font-size: 15pt\">Sampling Units - View & Edit</b>"
                     +"</font></td></tr>"
                     +"<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>"
                     +"</tr><tr><td width=\"517\">");



         out.println("<table width=488 height=\"92\">"
                     +"<td><b>Species</b><br><select name=sid "
                     +"name=select onChange='document.forms[0].submit()'  style=\"HEIGHT: 22px; WIDTH: 126px\">");
         // Get all the species for this project
         rset = stmt.executeQuery("SELECT NAME, SID FROM gdbadm.V_SPECIES_2 " +
                                  " WHERE PID=" + pid + " order by NAME");
         String s_sid = null; // Selected sampling unit
         boolean first_round = true;
         while (rset.next()) {
            if (first_round) {
               s_sid = rset.getString("SID");
               first_round = false;
            }
            if (sid != null && sid.equalsIgnoreCase(rset.getString("SID"))) {
               s_sid = rset.getString("SID");
               out.println("<OPTION selected value=\"" + rset.getString("SID") + "\">" + rset.getString("NAME")+"</option>\n");
            }
            else
               out.println("<OPTION value=\"" + rset.getString("SID") + "\">" + rset.getString("NAME")+"</option>\n");
         }
         out.println("</SELECT></TD>");

         // enabled/disabled
         out.println("<td><b>Status</b><br>");
         out.println("<select name=STATUS  style=\"HEIGHT: 22px; WIDTH: 126px\">");
         if (status.equalsIgnoreCase("E"))
            out.println("<OPTION selected value=E>Enabled");
         else
            out.println("<OPTION value=E>Enabled");
         if (status.equalsIgnoreCase("D"))
            out.println("<OPTION selected value=D>Disabled");
         else
            out.println("<OPTION value=D>Disabled");
         out.println("</SELECT></TD></table></td>");


         //Buttons
         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");
         // Only display button  if user has "create" privileges
         out.println(privDependentString(currentPrivs,SU_W,/*if true*/"<input type=button value=\"New Sampling Unit\""
                                         +"onClick='parent.location.href=\"" +getServletPath("viewSU/new?") + oldQS + "\"' "
                                         +"height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">"
                                         +"</td>",/*if false*/"<input type=button disabled value=\"New Sampling Unit\""
                                         //+"onClick='newSU()'"
                                         +"height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">"
                                         +"</td>"));

         out.println("<tr><td width=68 colspan=2>"
                     +"<input id=COUNT name=COUNT type=submit value=\"Count\" width=\"69\""
                     +" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">"
                     +"</td>"
                     +"<td width=68 colspan=2>"
                     +"<input id=DISPLAY name=DISPLAY type=submit value=\"Display\""
                     +" width=\"70\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">"
                     +"</td></tr>");


         // some hidden values
         out.println("<input type=\"hidden\" id=\"STARTINDEX\" name=\"STARTINDEX\" value=\"" + startIndex + "\">");
         out.println("<input type=\"hidden\" id=\"ORDERBY\" name=\"ORDERBY\" value=\"" + orderby + "\">");
         out.println("<input type=\"hidden\" id=\"oper\" name=\"oper\" value=\"\">");
         out.println("<input type=\"hidden\" id=\"new_su_name\" name=\"new_su_name\" value=\"\">");

         out.println("<td width=34 colspan=1><input id=TOP name=TOP type=submit value=\"<<\""
                     +"width=\"35\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 31px\">"
                     +"</td>");
         out.println("<td width=34 colspan=1><input id=PREV name=PREV type=submit value=\"<\""
                     +"width=\"34\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 32px\">"
                     +"</td>");
         out.println("<td width=34 colspan=1><input id=NEXT name=NEXT type=submit value=\">\""
                     +"width=\"35\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 32px\">"
                     +"</td>");
         out.println("<td width=34 colspan=1><input id=END name=END type=submit value=\">>\""
                     +"width=\"35\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 31px\">"
                     +"</td>");
         out.println("</table></form></td></tr></table>");

         out.println("</body></html>");


         /*out.println("<INPUT type=button value=\"New s.u.\" "
           + "style=\"POSITION: absolute; TOP: 5px; LEFT: 700px; "
           + "HEIGHT: 25px; WIDTH: 100px\" "
           + "onClick='newSU()'>");
           out.println("<table border=1 align=left " +
           "style=\"POSITION: absolute; TOP: 40px; LEFT: 694px; " +
           "PADDING-LEFT: 2px; PADDING-RIGHT: 2px; PADDING-TOP: 2px; PADDING-BOTTOM: 2px\"><tr><td>");
           out.println("<INPUT id=COUNT name=COUNT type=submit value=\"Count\" style=\"HEIGHT: 25px; WIDTH: 100px\"><BR>");
           out.println("<INPUT id=DISPLAY name=DISPLAY type=submit value=\"Display\" style=\"HEIGHT: 25px; WIDTH: 100px\"><br>");
           out.println("<INPUT id=TOP name=TOP type=submit value=\"<<\" style=\"HEIGHT: 25px; WIDTH: 25px\">"
           + "<INPUT id=PREV name=PREV type=submit value=\"<\" style=\"HEIGHT: 25px; WIDTH: 25px\">"
           + "<INPUT id=NEXT name=NEXT type=submit value=\">\" style=\"HEIGHT: 25px; WIDTH: 25px\">"
           + "<INPUT id=END name=END type=submit value=\">>\" style=\"HEIGHT: 25px; WIDTH: 25px\">");
           out.println("</td></tr></TABLE>");

           out.println("<input type=\"hidden\" id=\"STARTINDEX\" name=\"STARTINDEX\" value=\"" + startIndex + "\">");
           out.println("<input type=\"hidden\" id=\"ORDERBY\" name=\"ORDERBY\" value=\"" + orderby + "\">");
           out.println("<input type=\"hidden\" id=\"oper\" name=\"oper\" value=\"\">");
           out.println("<input type=\"hidden\" id=\"new_su_name\" name=\"new_su_name\" value=\"\">");
           out.println("</form>");

           out.println("<DIV style=\"POSITION: absolute; TOP: 165px; LEFT: 10px\">"); //margin-left: 10px\">");

           out.println(buildInfoLine(action, startIndex, rows, maxRows));
           out.println("</DIV>");


           out.println("<TABLE align=left bgColor=lightskyblue border=0 cellPadding=1");
           out.println("cellSpacing=0 style=\"POSITION: absolute; LEFT: 10px; TOP: 185px; WIDTH: 720px\">");

           out.println("<TR>");
         */


         /*
           out.println("<TD style=\"WIDTH: 130px\">"
           + "<div onClick='parent.location.href=\"/servlets/viewSU?"
           + "ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\"'>");
           if (orderby.equalsIgnoreCase("NAME"))
           out.println("<b>Name</b>");
           else
           out.println("Name");
           out.println("</div></TD>");
           out.println("<TD style=\"WIDTH: 130px\">"
           + "<div onClick='parent.location.href=\"/servlets/viewSU"
           + "ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\"'>");
           if (orderby.equalsIgnoreCase("COMM"))
           out.println("<b>Comment</b>");
           else
           out.println("Comment");
           out.println("</div></TD>");
           out.println("<TD style=\"WIDTH: 130px\">"
           + "<div onClick='parent.location.href=\"/servlets/viewSU?"
           + "ACTION=DISPLAY&" + newQS + "&ORDERBY=INDS\"'>");
           if (orderby.equalsIgnoreCase("INDS"))
           out.println("<b>Groups</b>");
           else
           out.println("Inds");
           out.println("</div></TD>");
           out.println("<TD style=\"WIDTH: 50px\">"
           + "<div onClick='parent.location.href=\"/servlets/viewSU?"
           + "ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\"'>");
           if (orderby.equalsIgnoreCase("USR"))
           out.println("<b>User</b>");
           else
           out.println("User");
           out.println("</div></TD>");
           out.println("<TD style=\"WIDTH: 130px\">"
           + "<div onClick='parent.location.href=\"/servlets/viewSU?"
           + "ACTION=DISPLAY&" + newQS + "&ORDERBY=TS\"'>");
           if (orderby.equalsIgnoreCase("TS"))
           out.println("<b>Last updated</b>");
           else
           out.println("Last updated");
           out.println("</div></TD>");
           out.println("<TD style=\"WIDTH: 50px\"></TD>");
           out.println("<TD style=\"WIDTH: 50px\"></TD>");
         */
         //out.println("<TD style=\"WIDTH: 50px\"></TD></TR></TABLE></TABLE>");

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
   private void writeTopScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("var MAX_SU_LENGTH = 20;");		
      out.println("var MIN_SU_LENGTH = 1;");
      out.println("function newSU() {");
      out.println("	var su_name = prompt('Sampling unit name', '');");
      out.println("	if (su_name == null || '' == su_name)");
      out.println("		return (false);");
      out.println("	else if (su_name.length > MAX_SU_LENGTH) {");		
      out.println("		alert('The name must be in the range 1-20 characters.');");
      out.println("		return (false);");
      out.println("	}");
      out.println("	if (!confirm('Are you sure you want to create a\\n'");
      out.println("		+ 'new sampling units with the name \\'' + su_name + '\\'')) {");
      out.println("		return (false);");
      out.println("	}");
      out.println("");
      out.println("");
      out.println("	document.forms[0].oper.value='NEW_SU';");
      out.println("	document.forms[0].new_su_name.value=su_name;");
      out.println("	document.forms[0].submit();");
      out.println("}");
      out.println("//-->");
      out.println("</script>");

   }


   /**
    * Creates a new sampling unit.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if sampling unit created.
    *         False if sampling unit not created.
    */
   private boolean createSU(HttpServletRequest request,
                            HttpServletResponse response)
   {
      HttpSession session = request.getSession(true);
      Connection connection = (Connection) session.getValue("conn");
      String name, sid, pid, status,comm;
      boolean isOk = true;
      String errMessage = null;

      try
      {
         connection.setAutoCommit(false);
         name = request.getParameter("new_su_name");
         sid = request.getParameter("sid");
         pid = (String) session.getValue("PID");
         status = request.getParameter("STATUS");
         comm = (String) request.getParameter("COMM");

         if (name == null || sid == null || status == null)
         {
            // Well, nothing much to do really.
            return true;
         }

         DbSamplingUnit dbSU = new DbSamplingUnit();
         dbSU.CreateSamplingUnit(connection,
                                 pid,
                                 name,
                                 comm,
                                 status,
                                 Integer.parseInt(sid),
                                 Integer.parseInt((String) session.getValue("UserID")));
         errMessage = dbSU.getErrorMessage();
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
                       "SamplingUnits.New.Create", errMessage, "viewSU", 
                       isOk);
      return isOk;
   }


   private void writeMiddle(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      // set content type and other response header fields first
      res.setContentType("text/html");

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
      maxRows = getMaxRows(session);


      try {
         out.println("<html>\n<head>\n<link rel=\"stylesheet\" type=\"text/css\" href=\""+getURL("style/tableBar.css")+"\">");
         out.println("<base target=\"content\">");
         out.println("</head>\n<body>");
         if(action != null)
         {
            out.println("&nbsp;" +buildInfoLine(action, startIndex, rows, maxRows));
         }

         String oldQS, newQS;
         oldQS = req.getQueryString();
         String choosen= req.getParameter("ORDERBY");
         newQS = buildTopQS(oldQS);
         /*
           out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
           + "</tr>"
           + "<tr>"
           + "<td width=\"750\" colspan=\"3\">"
           //style=\"margin-left:5px
           + "<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 height=20 width=750 style=\"margin-left:2px\">"
           + "<td width=5></td>");
         */
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=750 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");




         // the menu choices

         //Name
         out.println("<td width=150><a href=\"" + getServletPath("viewSU")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\">");
         if(choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown ><b>Name</b></FONT></a></td>\n");
         else out.println("Name</a></td>\n");
         //Comment
         out.println("<td width=100><a href=\"" + getServletPath("viewSU")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\">");
         if(choosen.equals("COMM"))
            out.println("<FONT color=saddlebrown><b>Comment</b></FONT></a></td>\n");
         else out.println("Comment</a></td>\n");
         //Idividuals
         out.println("<td width=100><a href=\"" + getServletPath("viewSU")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=INDS\">");
         if(choosen.equals("INDS"))
            out.println("<FONT color=saddlebrown><b>Individuals</b></FONT></a></td>\n");
         else out.println("Individuals</a></td>\n");

         //USER
         out.println("<td width=100><a href=\"" + getServletPath("viewSU")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
         else out.println("User</a></td>\n");

         //Updated
         out.println("<td width=145><a href=\"" + getServletPath("viewSU")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TS\">");
         if(choosen.equals("TS"))
            out.println("<FONT color=saddlebrown><b>Updated</b></FONT></a></td>\n");
         else out.println("Updated</a></td>\n");
         /*
           out.println("<td width=50>&nbsp;</td>"
           + "<td width=50>&nbsp;</td>"
           + "<td width=50>&nbsp;</td>"
           + "</table>"
           + "</td>"
           + "</tr>"
           + "</table>"
           + "</body></html>");
         */
         out.println("<td width=50>&nbsp;</td>");
         out.println("<td width=50>&nbsp;</td>");
         out.println("<td width=50>&nbsp;</td>");
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
      int currentPrivs[] = (int [])session.getValue("PRIVILEGES");
      // set content type and other response header fields first
      res.setContentType("text/html");
      PrintWriter out = res.getWriter();
      Statement stmt = null;
      ResultSet rset = null;
      Connection conn = null;
      try
      {
         String sid, action = null, status, orderby = null;
         String oldQS = req.getQueryString();
         String newQS= removeQSParameterOper(oldQS);
         action = req.getParameter("ACTION");
         sid = req.getParameter("sid");
         status = req.getParameter("STATUS");
         if (sid == null) sid = "-1";
         if(status==null) status = "E";
         if (action == null || action.equalsIgnoreCase("NOP") ||
             action.equalsIgnoreCase("COUNT") || sid == null)
         {
            // Nothing to do!
            HTMLWriter.writeBottomDefault(out);
            return;
         }
         else if (action.equalsIgnoreCase("NEXT"))
         {
            // Skip the first 50 rows?!
         }
         else if (action.equalsIgnoreCase("PREV"))
         {
            // The opposit
         }

         out.println("<html>\n"
                     + "<head><link rel=\"stylesheet\" type=\"text/css\" href=\""
                     +getURL("style/bottom.css")+"\">\n"
                     + "<title></title>\n"
                     + "</head>\n"
                     + "<body>\n");

         conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();
         StringBuffer sbSQL = new StringBuffer(512);
         sbSQL.append("SELECT NAME, COMM, USR, SUID, " +
                      "to_char(TS, '" + getDateFormat(session) + "') as TC_TS, " +
                      "INDS FROM gdbadm.V_SAMPLING_UNITS_3 WHERE SID="
                      + sid + " ");
         String qs = req.getQueryString();
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);
         orderby = req.getParameter("ORDERBY");
         if (orderby == null)
            sbSQL.append(" order by NAME");
         else
            sbSQL.append(" order by " + orderby);
         rset = stmt.executeQuery(sbSQL.toString());
         out.println("<TABLE align=left border=0 cellPadding=0");
         out.println("cellSpacing=0 width=750 style=\"margin-left:2px\">");
         boolean odd = true;
         int rowCount = 0;
         int startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         if (startIndex > 1) {
            while ((rowCount++ < startIndex - 1) && rset.next() )
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
            out.println("<TD WIDTH=5></TD>");
            out.println("<TD WIDTH=150>" + formatOutput(session, rset.getString("NAME"), 20) + "</TD>");
            out.println("<TD WIDTH=100>"+ formatOutput(session, rset.getString("COMM"), 10) + "</TD>");
            out.println("<TD WIDTH=100>" + replaceNull(rset.getString("INDS"), "0") + "</TD>");
            out.println("<TD WIDTH=100>" + rset.getString("USR") + "</TD>");
            out.println("<TD WIDTH=145>"+ formatOutput(session, rset.getString("TC_TS"), 18) + "</TD>");
            out.println("<TD WIDTH=50><A HREF=\"details?suid=" + rset.getString("SUID") + "&" +
                        newQS + "\" target=\"content\">Details</A></TD>");
            //check privileges on these links
            out.println("<TD WIDTH=50>"
                        + privDependentString(currentPrivs,SU_W,
                                              /*if true*/"<A HREF=\"edit?suid=" + rset.getString("SUID") + "&" + newQS + "\" target=\"content\">Edit</A></TD>",
                                              /*if false*/"<font color=tan>&nbsp Edit</font></TD>") );

            out.println("<TD WIDTH=50>"
                        + privDependentString(currentPrivs,SU_W,
                                              /*if true*/"<A HREF=\"check?suid=" + rset.getString("SUID") + "&" + oldQS + "\" target=\"content\">Check</A></TD></TR>",
                                              /*if false*/"<font color=tan>Check</font></TD>") );
            out.println("</TR>");
            rowCount++;
         }
         out.println("<TR align=left bgcolor=oldlace><TD WIDTH=5>&nbsp&nbsp</TD></TR>");
         out.println("</TABLE>");
         out.println("</body></html>");


      } catch (Exception e) 
      {
         out.println("<strong>Error in filter!</strong><br>");
         out.println("Error message: " + e.getMessage());
         e.printStackTrace(out);
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
    * The check sampling unit page
    */
   private void writeCheck(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);

      res.setContentType("text/html");
      PrintWriter out = res.getWriter();
      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null;
      String suid;
      String oldQS;
      int status;
      try {
         oldQS = buildQS(req) ; //req.getQueryString();
         suid = req.getParameter("suid");
         conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();
         String sql = "SELECT IID, IDENTITY FROM gdbadm.V_ENABLED_INDIVIDUALS_1 WHERE " +
            "SUID=" + suid;

         rset = stmt.executeQuery(sql);

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Sampling unit check</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Sampling units - Check</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         out.println("<table border=0 cellpadding=0 cellspacing=0>");
         out.println("<tr><td width=5>&nbsp;</td>");
         out.println("<td></td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");

         out.println("<table align=center cellSpacing=0 cellPadding=1>");
         out.println("<tr bgcolor=\"#008B8B\"><b>");
         out.println("<td nowrap WIDTH=100px><font color=white>Identity</font></td>");
         out.println("<td nowrap WIDTH=100px><font color=white>F. not male</font></td>");
         out.println("<td nowrap WIDTH=100px><font color=white>F. disabled</font></td>");
         out.println("<td nowrap WIDTH=100px><font color=white>F. too young</font></td>");
         out.println("<td nowrap WIDTH=100px><font color=white>M. not female</font></td>");
         out.println("<td nowrap WIDTH=100px><font color=white>M. disabled</font></td>");
         out.println("<td nowrap WIDTH=100px><font color=white>M. too young</font></td>");
         out.println("</b></tr>");
         int okInd = 0;
         int errInd = 0;
         boolean odd = true;
         
         DbIndividual ind = new DbIndividual();
         while (rset.next()) 
         {
            status = ind.checkIndividual(conn, rset.getInt("IID"));
            
            Errors.logDebug("Status="+status);
           
            if (status == 0) {// There are no errors!
               okInd++;
               continue;
            } 
            else 
            {
               errInd++;
               if (odd) {
                  out.println("<tr bgcolor=white>");
                  odd = false;
               } 
               else 
               {
                  out.println("<tr bgcolor=lightgrey>");
                  odd = true;
               }
               out.println("<td>" + rset.getString("IDENTITY") + "</td>");
            }
            if ((status & 1) == 1)
               out.println("<td align=center nowrap>x</td>");
            else
               out.println("<td align=center nowrap>&nbsp</td>");

            if ((status & 16) == 16)
               out.println("<td align=center nowrap>x</td>");
            else
               out.println("<td align=center nowrap>&nbsp;</td>");

            if ((status & 2) == 2)
               out.println("<td align=center nowrap>x</td>");
            else
               out.println("<td align=center nowrap>&nbsp;</td>");

            if ((status & 4) == 4)
               out.println("<td align=center nowrap>x</td>");
            else
               out.println("<td align=center nowrap>&nbsp;</td>");

            if ((status & 32) == 32)
               out.println("<td align=center nowrap>x</td>");
            else
               out.println("<td align=center nowrap>&nbsp;</td>");


            if ((status & 8) == 8)
               out.println("<td align=center nowrap>x</td>");
            else
               out.println("<td align=center nowrap>&nbsp;</td>");

            out.println("</tr>");
         }

         out.println("<tr><td colspan=5>&nbsp;</td></tr><td colspan=5>&nbsp;</td><tr>");
         out.println("<td colspan=2 align=left><p><STRONG>Individuals ok:&nbsp;" + String.valueOf(okInd) + "</STRONG></p>");
         out.println("<td colspan=2 align=left><p><STRONG>Individuals whith errors:&nbsp;" + String.valueOf(errInd) + "</STRONG></p></td");
         out.println("</tr></table>");

         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         out.println("<table border=0 cellspacing=0 cellpadding=0>");
         out.println("<tr><td>");
         out.println("<input type=button value=Back width=100 style=\"WIDTH: 100px\" " +
                //     "onClick='location.href=\"" + getServletPath("viewSU?") + oldQS + "\"'>");
                "onClick='location.href=\"" + getServletPath("viewSU?&RETURNING=YES")  + "\"'>");
         out.println("</td></tr></table>");

         out.println("</td></tr></table>");


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
   /***************************************************************************************
    * *************************************************************************************
    * The sampling unit's detail page
    */
   private void writeDetails(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      PrintWriter out = res.getWriter();

      Connection conn = null;
      Statement stmt_curr = null, stmt_hist = null;
      ResultSet rset_curr = null, rset_hist = null;
      String prev_name = null;
      String prev_comm = null;
      String prev_usr = null;
      String prev_ts = null;
      String prev_status = null;
      String curr_name = null;
      String curr_comm = null;
      String curr_usr = null;
      String curr_ts = null;
      String curr_status = null;
      boolean has_history = false;

      try {
         String oldQS = buildQS(req);
         String suid = req.getParameter("suid");
         if (suid == null || suid.trim().equals("")) suid = "-1";

         conn = (Connection) session.getValue("conn");
         // Get the current data
         stmt_curr = conn.createStatement();
         String strSQL = "SELECT SUID, NAME, COMM, SNAME, STATUS, "
            + "USR, to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS "
            + "FROM gdbadm.V_SAMPLING_UNITS_2 WHERE "
            + "SUID = " + suid;

         rset_curr = stmt_curr.executeQuery(strSQL);

         // Get the history
         stmt_hist = conn.createStatement();
         strSQL = "SELECT NAME, COMM, STATUS, "
            + "USR, to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS, TS as dummy "
            + "FROM gdbadm.V_SAMPLING_UNITS_LOG WHERE "
            + "SUID = " + suid + " order by dummy desc";

         rset_hist = stmt_hist.executeQuery(strSQL);

         if (rset_curr.next()) {
            curr_name = rset_curr.getString("NAME");
            curr_comm = rset_curr.getString("COMM");
            curr_status = rset_curr.getString("STATUS");
            curr_usr = rset_curr.getString("USR");
            curr_ts = rset_curr.getString("TC_TS"); // Time stamp
            if (curr_name == null) curr_name = "";
            if (curr_comm == null) curr_comm = "";
         }
         if (rset_hist.next()) {
            prev_name = rset_hist.getString("NAME");
            prev_comm = rset_hist.getString("COMM");
            prev_status = rset_hist.getString("STATUS");
            prev_usr = rset_hist.getString("USR");
            prev_ts = rset_hist.getString("TC_TS");
            has_history = true;
         }
         out.println("<html>\n"
                     + "<head>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Details</title>\n"
                     + "<META HTTP_EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out.println("</head>\n<body>\n");

         // new look
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Sampling Units - Details</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");


         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td></td></tr>");
         out.println("<tr><td></td><td>");

         out.println("<table nowrap border=0 cellSpacing=0>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
         out.println("<tr><td>Species</td><td>" + rset_curr.getString("SNAME") + "</td></tr>");
         out.println("</table><br><br>");

         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");


         out.println("<table nowrap align=center border=0 cellSpacing=0 width=570px>");
         out.println("<tr bgcolor=Black><td align=center colspan=9><b><font color=\"#ffffff\">Current Data</font></b></td></tr>" +
                     "<tr bgcolor=\"#008B8B\"><td nowrap WIDTH=50 style=\"WIDTH: 50px\"><font color=white>Name</font></td>" +
                     "<td nowrap WIDTH=100 style=\"WIDTH: 60px\"><font color=white>Status</font></td>" +
                     "<td nowrap WIDTH=250 style=\"WIDTH: 250px\"><font color=white>Comment</font></td>" +
                     "<td nowrap WIDTH=120 style=\"WIDTH: 120px\"><font color=white>Last updated by</font></td>" +
                     "<td nowrap WIDTH=50 style=\"WIDTH: 120px\"><font color=white>Last updated</font></td></tr>");
			
         out.println("<tr bgcolor=white>");
         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" +
                     (curr_name.equalsIgnoreCase(prev_name) ? "": "<font color=red>") + curr_name);
         out.println((curr_name.equals(prev_name) ? "</td>" : "</font></td>"));
         out.println("<td nowrap WIDTH=100 style=\"WIDTH: 100px\">" +
                     (curr_status.equalsIgnoreCase(prev_status) ? "": "<font color=red>") + curr_status);
         out.println((curr_status.equals(prev_status) ? "</td>" : "</font></td>"));
         out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" +
                     (curr_comm.equals(prev_comm) ? "" : "<font color=red>") + curr_comm);
         out.println((curr_comm.equals(prev_comm) ? "</td>": "</font></td>")); 

         out.println("<td nowrap WIDTH=120 style=\"WIDTH: 120px\">" + curr_usr + "</td>");
         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 120px\">" + curr_ts + "</td>"); // Last updated
				
         out.println("<tr bgcolor=Black>");
         out.println("<td align=center colspan=9><b><font color=\"#ffffff\">History</font></b></td></tr>");
			
         curr_name = prev_name;
         curr_comm = prev_comm;
         curr_status = prev_status;
         curr_usr = prev_usr;
         curr_ts = prev_ts;
				
         if (curr_name == null) curr_name = "";
         if (curr_comm == null) curr_comm = "";
         boolean odd = true;
         while (rset_hist.next()) {
            prev_name = rset_hist.getString("NAME");
            prev_comm = rset_hist.getString("COMM");
            prev_status = rset_hist.getString("STATUS");
            prev_usr = rset_hist.getString("USR");
            prev_ts = rset_hist.getString("TC_TS");
            if (odd) {
               out.println("<tr bgcolor=white>");
               odd = false;
            } else {
               out.println("<tr bgcolor=lightgrey>");
               odd = true;
            }
            out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" +
                        (curr_name.equalsIgnoreCase(prev_name) ? "": "<font color=red>") + curr_name);
            out.println((curr_name.equals(prev_name) ? "</td>": "</font></td>"));
            out.println("<td nowrap WIDTH=100 style=\"WIDTH: 100px\">" +
                        (curr_status.equals(prev_status) ? "" : "<font color=red>") + curr_status);
            out.println((curr_status.equals(prev_status) ? "</td>" : "</font></td>"));
            out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" +
                        (curr_comm.equals(prev_comm) ? "": "<font color=red>") + curr_comm); 
            out.println((curr_comm.equals(prev_comm) ? "</td>": "</font></td>")); 
            out.println("<td nowrap WIDTH=120 style=\"WIDTH: 120px\">" + curr_usr + "</td>");
            out.println("<td nowrap WIDTH=50 style=\"WIDTH: 120px\">" + curr_ts + "</td>"); // Last updated
            curr_name = prev_name;
            curr_comm = prev_comm;
            curr_status = prev_status;
            curr_usr = prev_usr;
            curr_ts = prev_ts;
				
            if (curr_name == null) curr_name = "";
            if (curr_comm == null) curr_comm = "";
         }
         if (has_history) {
            if (odd) {
               out.println("<tr bgcolor=white>");
               odd = false;
            } else {
               out.println("<tr bgcolor=lightgrey>");
               odd = true;
            }
            out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" +
                        (curr_name.equals(prev_name) ? "" : "<font color=red>") + curr_name); 
            out.println((curr_name.equals(prev_name) ? "</td>": "</font></td>"));
            out.println("<td nowrap WIDTH=100 style=\"WIDTH: 100px\">" +
                        (curr_status.equals(prev_status) ? "" : "<font color=red>") + curr_status);
            out.println((curr_status.equals(prev_status) ? "</td>" : "</font></td>"));
            out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" +
                        (curr_comm.equals(prev_comm) ? "" : "<font color=red>") + curr_comm);
            out.println((curr_comm.equals(prev_comm) ? "</td>": "</font></td>")); 
            out.println("<td nowrap WIDTH=120 style=\"WIDTH: 120px\">" + curr_usr + "</td>");
            out.println("<td nowrap WIDTH=50 style=\"WIDTH: 120px\">" + curr_ts + "</td>"); // Last updated
         }
         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");

         // Back button
         out.println("<form>");
         out.println("<table cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td>");
/*
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"\"'>"+"&nbsp;");
*/

       out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\""
                     +getServletPath("viewSU?&RETURNING=YES")  + "\"'>&nbsp;");


         out.println("</td></tr></table>");

         out.println("</form>");


         out.println("</tr></table>");
         out.println("</body></html>");
      } catch (SQLException e)
      {
         out.println("<PRE>");
         e.printStackTrace(out);
         out.println("</PRE>");
      } finally {
         try {
            if (rset_curr != null) rset_curr.close();
            if (rset_hist != null) rset_hist.close();
            if (stmt_curr != null) stmt_curr.close();
            if (stmt_hist != null) stmt_hist.close();
         } catch (SQLException ignored) {}
      }	
   }	
   /***************************************************************************************
    * *************************************************************************************
    * The sampling unit's edit page
    */
   private void writeEdit(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";

      if (oper.equals("DELETE_SU")) {
         if(deleteSU(req, res, conn))
            writeFrame(req, res);
      } else if (oper.equals("UPDATE_SU")) {
         if (updateSU(req, res, conn))
            writeEditPage(req, res);
      } else
         writeEditPage(req, res);
   }
   private void writeEditPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      PrintWriter out = res.getWriter();
      Connection conn =  (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      ResultSet rsetCount = null;
      Statement stmtCount = null;
      int nrOfProjects=0;
      try {
         stmt = conn.createStatement();
         String oldQS = buildQS(req);
         String sid=null;
         sid = req.getParameter("sid");
         String suid = req.getParameter("suid");

         // count how many projects own this SU
         stmtCount= conn.createStatement();
         rsetCount = stmt.executeQuery("SELECT COUNT (*) from R_PRJ_SU where SUID ="+suid);
         if(rsetCount.next()){
            nrOfProjects =rsetCount.getInt(1);
         }
         if (rsetCount != null) rsetCount.close();
         if (stmtCount != null) stmtCount.close();
         // Hidden? kolla detta med script!


         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         String strSQL = "SELECT SUID, SNAME, NAME, STATUS, COMM, USR, "
            + "to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS " // HIST is obsolute
            + "FROM gdbadm.V_SAMPLING_UNITS_2 WHERE "
            + "SUID = " + suid;

         rset = stmt.executeQuery(strSQL);

         rset.next();
         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);
         out.println("<title>Edit sampling unit</title>");
         out.println("</head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<body>\n");


         // new look
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Sampling Units- Edit</b></font></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         // just a "newline"
         out.println("<table><td></td></table>");

         // the whole information table
         out.println("<table width=800>");
         out.println("<tr><td>");


         // static data table
         out.println("<table border=0 cellpading=0 cellspacing=0 align=left width=300");
         out.println("<tr>");
         out.println("<td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td>");
         out.println("</tr>");
         out.println("<tr><td>Species</td><td>" + rset.getString("SNAME") + "</td></tr>");
         out.println("<tr><td>Included in</td><td>" + nrOfProjects +" projects"+ "</td></tr>");
         out.println("<tr><td>Last updated by</td><td>" + rset.getString("USR") + "</td></tr>");
         out.println("<tr><td>Last updated</td><td>" + rset.getString("TC_TS") + "</td></tr>");
         out.println("</table></tr></td>");

         out.println("<FORM action=\"edit?" + oldQS + "\" method=\"post\" name=\"FORM1\">");

         // dynamic data table
         out.println("<tr><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0 width=300>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Changable data</font></td></tr>");
         // Name
         out.println("<tr><td width=200>Name</td></tr>" +
                     "<tr><td width=200><input name=\"name\" type=\"text\" maxlength=20 "
                     + "style=\"WIDTH: 200px\" value=\"" + rset.getString("NAME") + "\"></td></tr>");
         // Status
         out.println("<tr><td width=200>Status</td></tr>");
         out.println("<tr><td width=200>");
         out.println("<select name=s width=200 style=\"WIDTH: 200px\">");
         if (rset.getString("STATUS").equals("E")) {
            out.println("<option selected value=\"E\">Enabled</option>");
            out.println("<option value=\"D\">Disabled</option>");
         } else {
            out.println("<option value=\"E\">Enabled</option>");
            out.println("<option selected value=\"D\">Disabled</option>");
         }
         out.println("</select>");
         out.println("</td></tr>");
         // Comment
         out.println("<tr><td width=200>Comment</td></tr>" +
                     "<tr><td width=200><textarea name=\"comm\" id=\"comm\" "
                     + "style=\"HIGHT: 60px; WIDTH: 200px\">");
         out.print(replaceNull(rset.getString("COMM"), ""));
         out.println("</textarea></td></tr>");
         out.println("</table></td></tr>");

         // buttons table
         out.println("<tr><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0 width=300>");
         out.println("<tr><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td></tr>" +
                     "<tr><td colspan=4 align=center>" +


                     "<input type=button name=BACK value=Back width=100 " +
                     // "style=\"WIDTH: 100px\" onClick='location.href=\"\"'>"+"&nbsp;"+
                     //"style=\"WIDTH: 100px\" onClick='location.href=\""+getServletPath("viewSU?")+ oldQS + "\"'>"+"&nbsp;"+
                      "style=\"WIDTH: 100px\" onClick='location.href=\""+getServletPath("viewSU?&RETURNING=YES") + "\"'>"+"&nbsp;"+

                     "<input type=reset value=Reset style=\"WIDTH: 100px\">&nbsp;"+
                     "<input type=button id=DELETE name=DELETE value=Delete style=\"WIDTH: 100px\" onClick='valForm(\"DELETE\")'>&nbsp;" +
                     "<input type=button id=UPDATE name=UPDATE value=Update style=\"WIDTH: 100px\" onClick='valForm(\"UPDATE\")'>&nbsp;");
         //   "<input type=button name=BACK value=Back " + "style=\"WIDTH: 100px\" onClick='location.href=\""+
         //   getServletPath("viewSU?") + "suid=" + suid + "&item=no\"'>&nbsp;");

         out.println("</td></tr>");
         out.println("</table></td></tr>");

         // Store some extra information needed by doPost()
         out.println("<input type=\"hidden\" NAME=oper value=\"\">");
         out.println("<input type=\"hidden\" NAME=nrProj value="+nrOfProjects+">");
         out.println("<input type=\"hidden\" NAME=RETURNING value=YES>");


         out.println("</table>");/*end of data table*/
         out.println("</FORM>");
         out.println("</body>\n</html>");



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
    * Deletes a sampling unit.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if sampling unit deleted.
    *         False if sampling unit not deleted.
    */
   private boolean deleteSU(HttpServletRequest request,
                            HttpServletResponse response,
                            Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      int suid = -1 ;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         String UserID = (String) session.getValue("UserID");
         String pid = (String) session.getValue("PID");
         String oldQS = request.getQueryString();
         suid = Integer.parseInt(request.getParameter("suid"));
         DbSamplingUnit dbsu = new DbSamplingUnit();
         dbsu.DeleteSamplingUnit(connection, Integer.parseInt(pid), suid);
         errMessage = dbsu.getErrorMessage();
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
                       "SamplingUnits.Edit.Delete", errMessage,
                       "viewSU", isOk);
      return isOk;
   }

   
   private boolean updateSU(HttpServletRequest request,
                            HttpServletResponse response,
                            Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      String oldQS = request.getQueryString();
      int suid = -1;

      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         String UserID = (String) session.getValue("UserID");
         String name, comment, status;
         status = request.getParameter("s");
         name = request.getParameter("name");
         comment = request.getParameter("comm");
         suid = Integer.parseInt(request.getParameter("suid"));
         if (name == null)
         {
            // nothing to do really.
            return isOk;
         }

         DbSamplingUnit dbsu = new DbSamplingUnit();

         dbsu.UpdateSamplingUnit(connection, name, comment, status, suid,
                                 Integer.parseInt(UserID)); 

         errMessage = dbsu.getErrorMessage();
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
                       "SamplingUnits.Edit.Update", errMessage,
                       "viewSU", isOk);
      return isOk;
   }


   private void writeEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      //'Are you sure you want to delete the sampling unit?'

      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("     if (document.forms[0].nrProj.value==1){");
      out.println("		      if (confirm('Are you sure you want to delete the sampling unit?')) {");
      out.println("             if (confirm('Warning! this Sampling Unit is only used by this Project, if deleted it will bee completely removed from the database')) {");
      out.println("			          document.forms[0].oper.value='DELETE_SU';");
      out.println("			          rc = 0;");
      out.println("              }");
      out.println("          }");
      out.println("      }");
      out.println("      else {");
      out.println("		      if (confirm('Are you sure you want to delete the sampling unit?')) {");
      out.println("			       document.forms[0].oper.value='DELETE_SU';");
      out.println("			       rc = 0;");
      out.println("          }");
      out.println("       }");

      /*

        out.println("		if (confirm('Are you sure you want to delete the sampling unit?')) {");
        out.println("       if (document.forms[0].nrProj.value=1){");
        out.println("            if (confirm('Warning! this Sampling Unit is only used by this Project, if deleted it will bee completely removed from the database')) {");
        out.println("			       document.forms[0].oper.value='DELETE_SU';");
        out.println("			       rc = 0;");
        out.println("            }");
        out.println("        }");
        out.println("        else {");
        out.println("			   document.forms[0].oper.value='DELETE_SU';");
        out.println("			   rc = 0;");
        out.println("		    }");
        out.println("		 }");
      */
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the sampling unit?')) {");
      out.println("			document.forms[0].oper.value='UPDATE_SU';");
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

   /***************************************************************************************
    * *************************************************************************************
    * The new SU page
    */
   private void writeNew(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createSU(req, res))
         {
            writeFrame(req, res);
         }
      } else {
         writeNewPage(req, res);
      }
   }
   private void writeNewPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);

      PrintWriter out = res.getWriter();
      // set content type and other response header fields first
      res.setContentType("text/html");
      Connection conn =  null;
      Statement stmt = null;
      ResultSet rset = null;
      printScript(out);
      //writeNewScript(out);
      try {
         conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();
         String suname, suid,pid, sid = null;
         sid = req.getParameter("sid");
         pid= (String) session.getValue("PID");

         suid = (String) session.getValue("SUID");
         String oldQS = buildQS(req);
         String newQS = removeQSParameterOper(oldQS);
         newQS = removeQSParameterSid(newQS);

         out.println("<html>\n"
                     + "<head>\n"
                     + "<title>New Sampling Unit</title>\n"
                     + "</head>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<body>\n");

         // new "look"
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Sampling Units - New</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");



         out.println("<FORM action=\"" + getServletPath("viewSU/new?")+ newQS + "\" method=\"post\" name=\"FORM1\">");

         //out.println("<table width=800 align=center border=0>");
         out.println("<table width=800 border=0>");

         stmt = conn.createStatement();
         rset = stmt.executeQuery(
                                  "SELECT SID, NAME FROM gdbadm.V_SPECIES_2 WHERE PID="+pid+"ORDER BY NAME");




         //	out.println("<tr><td width=200 align=right>Species</td>" +
         out.println("<tr><td width=200 >Species</td></tr>" +

                     "<tr><td width=200><select style=\"WIDTH: 200px\" name=\"sid\">");
         while (rset.next())
         {
            // no sid choosen yet
            if(sid == null || "".equalsIgnoreCase(sid)|| sid.equalsIgnoreCase("NULL"))
            {
               out.println("<option value=\"" + rset.getString("SID") + "\">" + rset.getString("NAME"));
               sid =rset.getString("SID");
            }
            else
            {
               out.println("<option value=\"" + rset.getString("SID") + "\">" + rset.getString("NAME"));
            }
         }
         //	out.println("<option selected value=\"\">");

         out.println("</select></td></tr>");

         //out.println("<tr><td width=200 align=right>Status</td>" +
         out.println("<tr><td width=200 >Status</td></tr>" +

                     "<tr><td width=200><select style=\"WIDTH: 200px\" name=\"STATUS\">");
         out.println("<option selected value=E>Enabled\n" +
                     "<option value=D>Disabled");
         out.println("</select></td></tr>");

         out.println("<tr><td width=200 >Name</td></tr>" +
                     "<tr><td width=200><input type=text maxlength=20 name=\"new_su_name\" style=\"WIDTH: 200px\" value=\"\"></td></tr>");

         out.println("<tr><td width=200>Comment</td></tr>" +	"<tr><td colspan=3 width=600>"
                     //      + "<input type=text name=\"COMM\" maxlength=256 style=\"WIDTH: 600px\" value=\"\"
                     +"<textarea rows=8 cols=45 name=COMM></textarea>"
                     +"</td></tr>");



         //out.println("<td colspan=2>Comment<br>");
         //			out.println("<textarea rows=8 cols=45 name=c>");
         //			out.println("</textarea>");



         /*
           out.println("<tr>");
           out.println("<td><input type=button value=\"Cancel\" " +
           out.println("&nbsp;</td>");

           out.println("<td><input type=button value=\"Create\" " +
           out.println("&nbsp;</td>");

           out.println("</tr>");



         */
         out.println("<tr><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td></tr>" +
                     "<tr><td colspan=4>");// align=center>");
         out.println("<input type=button value=Back onClick='location.href=\"" +
                     getServletPath("viewSU?RETURNING=YES")+ "\";' " +
                     "width=100 style=\"WIDTH: 100px\">&nbsp;");

         out.println("<input type=button id=SAVE name=SAVE value=Create style=\"WIDTH: 100px\" onClick='valForm(\"CREATE\")'>&nbsp; ");
         //	"<input type=reset value=Reset style=\"WIDTH: 100px\"></td></tr>");
         out.println("</td></tr>");
         out.println("</table>");

         out.println("<input type=\"hidden\" ID=\"oper\" NAME=\"oper\" value=\"\">");
          out.println("<input type=\"hidden\" NAME=RETURNING value=YES>");
         out.println("</FORM>");
         out.println("</body>\n</html>");
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

   private void printScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('CREATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to create the sampling units?')) {");
      out.println("			document.forms[0].oper.value='CREATE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
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
             extPath.equals("/top") ||
             extPath.equals("/details") ) {
            // We neew the privilege SU_R for all these
            title = "Sampling units - View & Edit";
            if ( privDependentString(privileges, SU_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/edit") ) {
            // We neew the privilege SU_W
            title = "Sampling units - Edit";
            if ( privDependentString(privileges, SU_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/new") ) {
            // We neew the privilege SU_W
            title = "Sampling units - New";
            if ( privDependentString(privileges, SU_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/check") ) {
            // We need the privilege SU_W
            title = "Sampling units - Check";
            if ( privDependentString(privileges, SU_R, "", null) == null)
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
}

