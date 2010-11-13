/*
  $Log$
  Revision 1.8  2005/02/08 16:03:21  heto
  DbIndividual is now complete. Some bug tests are done.
  DbSamplingunit is converted. No bugtest.
  All transactions should now be handled in the GUI (yuck..)

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


  Revision 1.8  2001/05/31 07:07:08  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.7  2001/05/22 06:17:01  roca
  Backfuncktionality fixed for all (?) servlets/subpages

  Revision 1.6  2001/05/15 13:36:29  roca
  After merge problems from last checkin..

  Revision 1.5  2001/05/14 06:13:03  frob
  Changed all methods that used to call writeError() to write an error page. These
  methods now calls the general method commitOrRollback() which does the commit or
  rollback operation as well as handle any errors. writeError() was removed.

  Revision 1.4  2001/05/03 07:57:46  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.3  2001/05/03 06:12:43  frob
  Calls to removeOper changed to use the general removeQSParameter.
  The previously called method is removed.

  Revision 1.2  2001/05/03 06:07:19  frob
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

public class viewSamples extends SecureArexisServlet
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
      } else if (extPath.equals("/bottom")) {
         writeBottom(req, res);
      } else if (extPath.equals("/middle")) {
         writeMiddle(req, res);
      } else if (extPath.equals("/details")) {
         writeDetails(req, res);
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
                     + " <TITLE>View Samples</TITLE>"
                     + "</HEAD>"
                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" //
                     + "<frame name=\"viewtop\" "
                     + "src=\""+ getServletPath("viewSamples/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewmiddle\" "
                     + "src=\""+ getServletPath("viewSamples/middle?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewbottom\""
                     + "src=\"" +getServletPath("viewSamples/bottom?") + bottomQS + "\" "
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



      String action = null,
         suid = null,
         old_suid = null,
         identity = null,
         sname = null,
         tissue = null,
         storage = null,
         orderby = null,
         status = null;

      boolean suid_changed = false;
      String pid = (String) session.getValue("PID");
      old_suid = (String) session.getValue("SUID");
      suid = req.getParameter("suid");
      if (suid == null) {
         suid = old_suid;
         suid_changed = true;
      } else if (old_suid != null && !old_suid.equals(suid)) {
         suid_changed = true;
      }
      if (suid == null) {
         suid = findSuid(conn, pid);
         suid_changed = true;
      }
      //session.putValue("SUID", suid);

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


      output.append("ACTION=")
         .append(action);

      tissue = req.getParameter("TISSUE");
      if (tissue != null)
         output.append("&TISSUE=").append(tissue);
      identity = req.getParameter("IDENTITY");
      if (identity != null)
         output.append("&IDENTITY=").append(identity);
      sname = req.getParameter("SNAME");
      if (sname != null)
         output.append("&SNAME=").append(sname);
      storage = req.getParameter("STORAGE");
      if (storage != null)
         output.append("&STORAGE=").append(storage);


      // Set the parameters STARTINDEX and ROWS
      if (!action.equals("NOP"))
         output.append(setIndecis(suid, old_suid, action, req, session));
      output.append("&suid=").append(suid);
      if (req.getParameter("oper") != null)
         output.append("&oper=").append(req.getParameter("oper"));
      if (req.getParameter("new_sample_name") != null)
         output.append("&new_sample_name=").append(req.getParameter("new_sample_name"));
/*
      orderby = req.getParameter("ORDERBY");
      if (orderby != null)
         output.append("&ORDERBY=").append(orderby);
      else
         output.append("&ORDERBY=IDENTITY");
*/

      // Orderby must be the last parameter in the query string
      orderby = req.getParameter("ORDERBY");
      if (orderby != null)
         output.append("&ORDERBY=").append(orderby);
      else
         output.append("&ORDERBY=IDENTITY");

      return output.toString().replace('%', '*');
   }

   private String findSuid(Connection conn, String pid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID FROM gdbadm.V_SAMPLING_UNITS_2 WHERE PID=" +
                                  pid + " ORDER BY NAME");
         if (rset.next()) {
            ret = rset.getString("SUID");
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
   private String findCid(Connection conn, String suid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID FROM gdbadm.CHROMOSOMES_1 WHERE SID=" +
                                  "(SELECT SID FROM gdbadm.V_SAMPLING_UNITS_1 WHERE SUID=" + suid + ")" +
                                  " ORDER BY NAME");
         if (rset.next()) {
            ret = rset.getString("CID");
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
   /*
     private String findAllele(Connection conn, String aid) {
     Statement stmt = null;
     ResultSet rset = null;
     String ret;
     try {
     stmt = conn.createStatement();
     rset = stmt.executeQuery("SELECT NAME FROM gdbadm.ALLELES_1 WHERE AID=" + aid);
     if (rset.next()) {
     ret = rset.getString("NAME");
     } else {
     ret = "(None)";
     }
     } catch (SQLException e) {
     ret = "(Error)";
     } finally {
     try {
     if (rset != null) rset.close();
     if (stmt != null) stmt.close();
     } catch (SQLException ignored) {
     }
     }
     return ret;
     }
     private String findMid(Connection conn, String cid) {
     Statement stmt = null;
     ResultSet rset = null;
     String ret;
     try {
     stmt = conn.createStatement();
     rset = stmt.executeQuery("SELECT MID FROM gdbadm.MARKERS_1 WHERE CID=" +
     cid + " ORDER BY NAME");
     if (rset.next()) {
     ret = rset.getString("MID");
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

   */
   private String setIndecis(String suid, String old_suid, String action, HttpServletRequest req, HttpSession session) {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(suid, req, session);
      maxRows = getMaxRows(session);
      if (req.getParameter("STARTINDEX") != null && old_suid.equalsIgnoreCase(suid))
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


   private int countRows(String suid, HttpServletRequest req, HttpSession session) {
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer(512);
      try {
         sbSQL.append("SELECT count(*) "
                      + "FROM gdbadm.V_SAMPLES_2 WHERE SUID=" + suid + " ");
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

   private String buildFilter(HttpServletRequest req, boolean order) {
      String identity = null,
         tissue = null,
         storage = null,
         sname = null;
      StringBuffer filter = new StringBuffer(256);
      identity = req.getParameter("IDENTITY");
      tissue = req.getParameter("TISSUE");
      storage = req.getParameter("STORAGE");
      sname = req.getParameter("SNAME");

      if (identity != null && !"".equalsIgnoreCase(identity))
         filter.append("and IDENTITY like '" + identity + "'");
      if (tissue != null && !"".equalsIgnoreCase(tissue))
         filter.append(" and TISSUE_TYPE like '" + tissue + "'");
      if (storage != null && !"".equalsIgnoreCase(storage))
         filter.append(" and STORAGE like '" + storage + "'");
      if (sname != null && !"".equalsIgnoreCase(sname))
         filter.append(" and NAME like '" + sname + "'");
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
      PrintWriter out = res.getWriter();
      String oper;
      oper = req.getParameter("oper");
      if (oper == null || "".equals(oper))
         oper = "SELECT";

      HttpSession session = req.getSession(true);
      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null;
      String suid, identity, sname, tissue, storage , orderby, action, pid, status;
      String oldQS, newQS;
      int testC=0;
      int startIndex = 0, rows = 0, maxRows = 0;
      int currentPrivs[];

      try {

         // Read all availible sampling units from database
         conn = (Connection) session.getValue("conn");
         currentPrivs = (int [])session.getValue("PRIVILEGES");
         stmt = conn.createStatement();
         oldQS = req.getQueryString();
         newQS = buildTopQS(oldQS);
         suid = req.getParameter("suid");


         //suid = (String) session.getValue("SUID");
         pid = (String) session.getValue("PID");
         //pid = req.getParameter("pid");

         if (pid == null) pid = new String("-1"); // Hope this will select nothing
         maxRows= getMaxRows(session);
         tissue = req.getParameter("TISSUE");
         identity = req.getParameter("IDENTITY");
         sname = req.getParameter("SNAME");
         storage = req.getParameter("STORAGE");
         orderby = req.getParameter("ORDERBY");
         action = req.getParameter("ACTION");
         status = req.getParameter("STATUS");
         if (req.getParameter("STARTINDEX") != null)
            startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         else
            startIndex = 0;
         if (req.getParameter("ROWS") != null)
            rows = Integer.parseInt(req.getParameter("ROWS"));
         else
            rows = 0;
         if (suid == null) suid = "";
         if (tissue == null) tissue = "";
         if (identity == null) identity = "";
         if (storage == null) storage = "";
         if (sname == null) sname = "";
         if (orderby == null) orderby = "IDENTITY";
         if (action == null) action = "NOP";
         if (status == null) status = "E";


         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+ getURL("style/view.css") +"\">");
         out.println("<base target=\"content\">");

         writeTopScript(out);
         out.println("<title>Samples</title>");
         out.println("</head>");


         out.println("<body bgcolor=\"#ffffd0\">"
                     +"<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
                     +"<tr>"
                     + "<td width=\"14\" rowspan=\"3\">"
                     +"</td>"
                     +"<td width=\"736\" colspan=\"2\" height=\"15\">"
                     +"<form method=get action=\"" +getServletPath("viewSamples") +"\">"
                     +"<p align=\"center\"><font size=\"2\"><b  style=\"font-size: 15pt\">Individuals - Samples - View & Edit</b>"
                     +"</font></td></tr>"
                     +"<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>"
                     +"</tr><tr><td width=\"517\">");

         out.println("<table width=488 height=\"92\">"

                     +"<td><b>Sampling unit</b><br><select name=suid "
                     +"name=select onChange='document.forms[0].submit()'  style=\"HEIGHT: 22px; WIDTH: 126px\">");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SUID FROM gdbadm.V_SAMPLING_UNITS_2 " +
                                  "WHERE PID="+ pid +" AND STATUS='E'"+" ORDER BY NAME");
         while (rset.next()) {
            if (suid != null && suid.equalsIgnoreCase(rset.getString("SUID")))
               out.println("<OPTION selected value=\"" + rset.getString("SUID") + "\">" +
                           rset.getString("NAME")+ "</option>\n");
            else
               out.println("<OPTION value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME")+"</option>\n");
         }
         rset.close();
         stmt.close();
         out.println("</SELECT></td>");

         out.println("<td><b>Individual</b><br>"
                     + "<input id=IDENTITY name=IDENTITY value=\"" + identity + "\" style=\"HEIGHT: 22px; WIDTH: 127px\" size=\"12\"></td>");


         out.println("<td><b>Sample Name</b><br>"
                     //alias
                     + "<input id=sname name=SNAME value=\"" +sname+"\" style=\"HEIGHT: 22px; WIDTH: 127px\" size=\"12\"></td>");
         out.println("<tr>");

         out.println("<td><b>Tissue</b><br>"
                     + "<input id=tissue name=TISSUE value=\"" +tissue+"\" style=\"HEIGHT: 22px; WIDTH: 127px\" size=\"12\"></td>");
         out.println("<td><b>Storage</b><br>"
                     + "<input id=storage name=STORAGE value=\"" +storage+"\" style=\"HEIGHT: 22px; WIDTH: 127px\" size=\"12\"></td>");

         out.println("</td></table></td>");


         //Buttons
         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");


         out.println(privDependentString(currentPrivs,IND_W,
                                         /*if true*/"<input type=button value=\"New Sample\""
                                         + " onClick='parent.location.href=\"" +getServletPath("viewSamples/new?") + oldQS + "\"' "
                                         +"height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">"
                                         +"</td>",
                                         /*if false*/"<input type=button disabled value=\"New Sample\""
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
         output = "Displaying " + startIndex + "-" + upperLimit +
            " of " + rows + " rows.";
      } else if (action.regionMatches(true, 0, "PREV", 0, "PREV".length())) {
         // Print the current row intervall
         output = "Displaying " + startIndex + "-" + upperLimit +
            " of " + rows + " rows.";
      } else if (action.regionMatches(true, 0, "TOP", 0, "TOP".length())) {
         // Print the current row intervall
         output = "Displaying " + startIndex + "-" + upperLimit +
            " of " + rows + " rows.";
      } else if (action.regionMatches(true, 0, "END", 0, "END".length())) {
         // Print the current row intervall
         output = "Displaying " + startIndex + "-" + upperLimit +
            " of " + rows + " rows.";
      } else if ("COUNT".equalsIgnoreCase(action)) {
         // Count the number of rows this filter will return
         output = "Query will return " + rows + " rows.";
      } else if ("DISPLAY".equalsIgnoreCase(action)) {
         // print the current row intervall
         output = "Displaying " + startIndex + "-" + upperLimit +
            " of " + rows + " rows.";
      } else if ("NOP".equalsIgnoreCase(action)) {
         // Print something that isn't visible (to make the frame as big as it would be in the cases above
         output = "&nbsp;";
      } else {
         // ???
         output = "?" + action + "?";
      }

      return output;
   }
   private void writeTopScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("//-->");
      out.println("</script>");

   }

   /***********************************************************
                                                               /* The middle frame (contains header for the result-table)
                                                                */
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

      //System.err.println("action=" + action);
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
           out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 height=20 width=800 style=\"margin-left:2px\">"
           + "<td width=5></td>");
         */


         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=800 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");

         //Identity
         out.println("<td width=100><a href=\"" + getServletPath("viewSamples")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=IDENTITY\">");
         if(choosen.equals("IDENTITY"))
            out.println("<FONT color=saddlebrown><b>Identity</b></FONT></a></td>\n");
         else out.println("Identity</a></td>\n");

         // sample name
         out.println("<td width=100><a href=\"" + getServletPath("viewSamples")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=SNAME\">");
         if(choosen.equals("SNAME"))
            out.println("<FONT color=saddlebrown><b>Name</b></FONT></a></td>\n");
         else out.println("Name</a></td>\n");

         //Tissue
         out.println("<td width=100><a href=\"" + getServletPath("viewSamples")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TISSUE\">");
         if(choosen.equals("TISSUE"))
            out.println("<FONT color=saddlebrown><b>Tissue</b></FONT></a></td>\n");
         else out.println("Tissue</a></td>\n");
         //Storage
         out.println("<td width=100><a href=\"" + getServletPath("viewSamples")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=STORAGE\">");
         if(choosen.equals("STORAGE"))
            out.println("<FONT color=saddlebrown><b>Storage</b></FONT></a></td>\n");
         else out.println("Storage</a></td>\n");
         //USER
         out.println("<td width=100><a href=\"" + getServletPath("viewSamples")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
         else out.println("User</a></td>\n");
         //Updated
         out.println("<td width=200><a href=\"" + getServletPath("viewSamples")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TS\">");
         if(choosen.equals("TS"))
            out.println("<FONT color=saddlebrown><b>Updated</b></FONT></a></td>\n");
         else out.println("Updated</a></td>\n");

         out.println("<td width=50>&nbsp;</td>"
                     + "<td width=50>&nbsp;</td>\n"
                     + "</table></table>\n"
                     + "</body></html>");


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
      PrintWriter out = res.getWriter();
      Statement stmt = null;
      ResultSet rset = null;
      Connection conn = null;
      int currentPrivs[];

      try
      {
         String suid = null, cid = null, action = null, status=null, orderby = null;

         String oldQS = req.getQueryString();
         action = req.getParameter("ACTION");
         suid = req.getParameter("suid");
         status = req.getParameter("STATUS");
         orderby = req.getParameter("orderby");
         currentPrivs = (int [])session.getValue("PRIVILEGES");

         if (action == null || action.equalsIgnoreCase("NOP") ||
             action.equalsIgnoreCase("COUNT") ||
             suid == null )
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
                     + "<title>bottomFrame</title>\n"
                     + "</head>\n"
                     + "<body>\n");

         conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();
         StringBuffer sbSQL = new StringBuffer(512);

         sbSQL.append("SELECT SAID, IDENTITY, NAME, TISSUE_TYPE, " +
                      " STORAGE, USR, to_char(TS, '" + getDateFormat(session) + "') as TC_TS " +
                      "FROM gdbadm.V_SAMPLES_2 WHERE SUID=" + suid + " ");

         String qs = req.getQueryString();
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);
         if (orderby != null && !"".equalsIgnoreCase(orderby))
            sbSQL.append(" order by " + orderby);
         else
            sbSQL.append(" order by IDENTITY");

         rset = stmt.executeQuery(sbSQL.toString());
         out.println("<TABLE align=left border=0 cellPadding=0");
         out.println("cellSpacing=0 width=800 style=\"margin-left:2px\">");

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

            out.println("<TD WIDTH=5></TD>\n");
            out.println("<TD WIDTH=100>" + formatOutput(session,rset.getString("IDENTITY"),9)+"</TD>");
            out.println("<TD WIDTH=100>" + formatOutput(session,rset.getString("NAME"),9)+ "</TD>");
            out.println("<TD WIDTH=100>" + formatOutput(session,rset.getString("TISSUE_TYPE"),9) + "</TD>");
            out.println("<TD WIDTH=100>" + formatOutput(session,rset.getString("STORAGE"),9) + "</TD>");
            out.println("<TD WIDTH=100>" + formatOutput(session,rset.getString("USR"),8)  +"</TD>");
            out.println("<TD WIDTH=200>" + formatOutput(session,rset.getString("TC_TS"),16) + "</TD>");


            out.println("<TD WIDTH=50><A HREF=\"" +getServletPath("viewSamples/details?said=")
                        + rset.getString("SAID")
                        + "&" + oldQS + "\" target=\"content\">Details</A></TD>");


            out.println("<TD WIDTH=50>");


            out.println(privDependentString(currentPrivs,IND_W,
                                            /*if true*/"<A HREF=\"" +getServletPath("viewSamples/edit?said=")
                                            + rset.getString("SAID")
                                            + "&" + oldQS + "\" target=\"content\">Edit</A></TD></TR>",
                                            /*if false*/"<font color=tan>&nbsp Edit</font></TD>") );

            rowCount++;
         }

         out.println("<TR align=left bgcolor=oldlace><TD WIDTH=5>&nbsp&nbsp</TD></TR>");
         out.println("</TABLE>");
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
    * The sample detail page
    */
   private void writeDetails(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");

      String said =req.getParameter("said");
      Connection conn =  null;
      Statement stmt_curr = null, stmt_hist = null;
      ResultSet rset_curr = null, rset_hist = null;

      String curr_identity = null;
      String curr_storage = null;
      String curr_treatment = null;
      String curr_date = null;
      String curr_comm = null;
      String curr_exp = null;
      String curr_name= null;
      String curr_usr = null;
      String curr_ts = null;
      String curr_tissue = null;

      String prev_identity = null;
      String prev_storage = null;
      String prev_treatment = null;
      String prev_date = null;
      String prev_comm = null;
      String prev_exp = null;
      String prev_name= null;
      String prev_usr = null;
      String prev_ts = null;
      String prev_tissue = null;

      //String curr_ts = null;

      boolean has_history = false;


      try {
         String oldQS = buildQS(req);

         conn = (Connection) session.getValue("conn");
         // Get the current data of the individual
         stmt_curr = conn.createStatement();
         String strSQL = "SELECT SAID, NAME, TISSUE_TYPE, EXPERIMENTER, IDENTITY, "
            + "to_char(DATE_, 'YYYY-MM-DD') as TC_DATE, COMM, USR, "
            + "TREATMENT, STORAGE, to_char(TS, '" + getDateFormat(session) + "') as TC_TS "
            + "FROM gdbadm.V_SAMPLES_3 WHERE "
            + "SAID = " + said;

         rset_curr = stmt_curr.executeQuery(strSQL);

         // Get the history
         strSQL = "SELECT SAID, NAME, TISSUE_TYPE, EXPERIMENTER, "
            + "to_char(DATE_, 'YYYY-MM-DD') as TC_DATE, COMM, USR, "
            + "TREATMENT, STORAGE, to_char(TS, '" + getDateFormat(session) + "') as TC_TS, TS as dummy "
            + "FROM gdbadm.V_SAMPLES_LOG WHERE "
            + "SAID = " + said + " ORDER BY dummy desc";

         stmt_hist = conn.createStatement();
         rset_hist = stmt_hist.executeQuery(strSQL);

         if (rset_curr.next()) {
            curr_name = rset_curr.getString("NAME");
				//curr_identity = rset_curr.getString("IDENTITY");
            curr_tissue = rset_curr.getString("TISSUE_TYPE");
            curr_exp = rset_curr.getString("EXPERIMENTER");
            curr_date = rset_curr.getString("TC_DATE");
            curr_comm = rset_curr.getString("COMM");
            curr_usr = rset_curr.getString("USR");
            curr_treatment = rset_curr.getString("TREATMENT");
            curr_storage = rset_curr.getString("STORAGE");
            curr_ts = rset_curr.getString("TC_TS"); // Time stamp


         }
         if (rset_hist.next()) {
            prev_name = rset_hist.getString("NAME");
				//prev_identity = rset_hist.getString("IDENTITY");
            prev_tissue = rset_hist.getString("TISSUE_TYPE");
            prev_exp = rset_hist.getString("EXPERIMENTER");
            prev_date = rset_hist.getString("TC_DATE");
            prev_comm = rset_hist.getString("COMM");
            prev_usr = rset_hist.getString("USR");
            prev_treatment = rset_hist.getString("TREATMENT");
            prev_storage = rset_hist.getString("STORAGE");
            prev_ts = rset_hist.getString("TC_TS"); // Time stamp
            has_history = true;
         }
         out.println("<html>\n"
                     + "<head>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Details</title>\n"
                     + "<META HTTP_EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out.println("</head>\n"
                     + "<body>\n");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Individuals - Samples - View & Edit - Details</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td></td></tr>");
         out.println("<tr><td></td><td>");

         // static
         out.println("<table nowrap border=0 cellSpacing=0>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
         out.println("<tr><td>Individual</td><td>" + rset_curr.getString("IDENTITY") + "</td></tr>");
         out.println("</table><br><br>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");


         out.println("<table nowrap align=center border=0 cellSpacing=0 width=800px>");

         out.println("<tr bgcolor=Black><td align=center colspan=9><b><font color=\"#ffffff\">Current Data</font></b></td></tr>" +
                     "<tr bgcolor= \"#008B8B\"><td nowrap>Name</td>" +
                     //"<td nowrap>Individual</td>
                     "<td nowrap>Tissue</td>" +
                     "<td nowrap>Treatment</td><td nowrap>Experimenter</td>" +
                     "<td nowrap>Sample date</td>" +
                     "<td nowrap>Storage</td>" +
                     "<td nowrap>Comment</td><td nowrap>Last updated by</td>" +
                     "<td nowrap>Last updated</td></tr>");

         out.println("<tr bgcolor=white>");
         // name
         out.println("<td nowrap>");
         if (("" + curr_name).equals("" + prev_name))
            out.println(formatOutput(session, curr_name, 20));
         else
            out.println("<font color=red>" + formatOutput(session, curr_name, 20) + "</font>");
         out.println("</td>");
         // tissue
         out.println("<td>");
         if (("" + curr_tissue).equals("" + prev_tissue))
            out.println(formatOutput(session, curr_tissue, 20));
         else
            out.println("<font color=red>" + formatOutput(session, curr_tissue, 20) + "</font>");
         out.println("</td>");
         // Treatment
         out.println("<td nowrap>");
         if (("" + curr_treatment).equals("" + prev_treatment))
            out.println(formatOutput(session, curr_treatment, 20));
         else
            out.println("<font color=red>" + formatOutput(session, curr_treatment, 20) + "</font>");
         out.println("</td>");
         // Experimenter
         out.println("<td nowrap>");
         if (("" + curr_exp).equals("" + prev_exp))
            out.println(formatOutput(session, curr_exp, 15));
         else
            out.println("<font color=red>" + curr_exp + "</font>");
         out.println("</td>");
         // Date
         out.println("<td nowrap>");
         if (("" + curr_date).equals("" + prev_date))
            out.println(formatOutput(session, curr_date, 12));
         else
            out.println("<font color=red>" + formatOutput(session , curr_date, 12) + "</font>");
         out.println("</td>");
         // Storage
         out.println("<td nowrap>");
         if (("" + curr_storage).equals("" + prev_storage))
            out.println(formatOutput(session, curr_storage, 12));
         else
            out.println("<font color=red>" + formatOutput(session, curr_storage, 12) + "</font>");
         out.println("</td>");
         // Comment
         out.println("<td nowrap>");
         if (("" + curr_comm).equals("" + prev_comm))
            out.println(formatOutput(session, curr_comm, 12));
         else
            out.println("<font color=red>" + formatOutput(session, curr_comm, 12) + "</font>");
         out.println("</td>");
         out.println("<td nowrap>" + formatOutput(session, curr_usr, 10) + "</td>");
         out.println("<td nowrap>" + formatOutput(session, curr_ts, 16) + "</td>"); // Last updated
         out.println("</tr>");
         out.println("<tr bgcolor=Black><td align=center colspan=9><b><font color=\"#ffffff\">History</font></b></td></tr>");

         curr_name = prev_name;
         curr_tissue = prev_tissue;
         curr_treatment = prev_treatment;
         curr_date = prev_date;
         curr_comm = prev_comm;
         curr_exp = prev_exp;
         curr_storage = prev_storage;
         curr_usr = prev_usr;
         curr_ts = prev_ts;


         boolean odd = true;
         while (rset_hist.next()) {
            prev_name = rset_hist.getString("NAME");
				//prev_identity = rset_hist.getString("IDENTITY");
            prev_tissue = rset_hist.getString("TISSUE_TYPE");
            prev_exp = rset_hist.getString("EXPERIMENTER");
            prev_date = rset_hist.getString("TC_DATE");
            prev_comm = rset_hist.getString("COMM");
            prev_usr = rset_hist.getString("USR");
            prev_treatment = rset_hist.getString("TREATMENT");
            prev_storage = rset_hist.getString("STORAGE");
            prev_ts = rset_hist.getString("TC_TS"); // Time stamp
            if (odd) {
               out.println("<tr bgcolor=white>");
               odd = false;
            } else {
               out.println("<tr bgcolor=lightgrey>");
               odd = true;
            }

            // name
            out.println("<td nowrap>");
            if (("" + curr_name).equals("" + prev_name))
               out.println(formatOutput(session, curr_name, 20));
            else
               out.println("<font color=red>" + formatOutput(session, curr_name, 20) + "</font>");
            out.println("</td>");
            // tissue
            out.println("<td>");
            if (("" + curr_tissue).equals("" + prev_tissue))
               out.println(formatOutput(session, curr_tissue, 20));
            else
               out.println("<font color=red>" + formatOutput(session, curr_tissue, 20) + "</font>");
            out.println("</td>");
            // Treatment
            out.println("<td nowrap>");
            if (("" + curr_treatment).equals("" + prev_treatment))
               out.println(formatOutput(session, curr_treatment, 20));
            else
               out.println("<font color=red>" + formatOutput(session, curr_treatment, 20) + "</font>");
            out.println("</td>");
            // Experimenter
            out.println("<td nowrap>");
            if (("" + curr_exp).equals("" + prev_exp))
               out.println(formatOutput(session, curr_exp, 15));
            else
               out.println("<font color=red>" + curr_exp + "</font>");
            out.println("</td>");
            // Date
            out.println("<td nowrap>");
            if (("" + curr_date).equals("" + prev_date))
               out.println(formatOutput(session, curr_date, 12));
            else
               out.println("<font color=red>" + formatOutput(session , curr_date, 12) + "</font>");
            out.println("</td>");
            // Storage
            out.println("<td nowrap>");
            if (("" + curr_storage).equals("" + prev_storage))
               out.println(formatOutput(session, curr_storage, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_storage, 12) + "</font>");
            out.println("</td>");
            // Commet
            out.println("<td nowrap>");
            if (("" + curr_comm).equals("" + prev_comm))
               out.println(formatOutput(session, curr_comm, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_comm, 12) + "</font>");
            out.println("</td>");
            out.println("<td nowrap>" + formatOutput(session, curr_usr, 10) + "</td>");
            out.println("<td nowrap>" + formatOutput(session, curr_ts, 16) + "</td>"); // Last updated
            out.println("</tr>");

            curr_name = prev_name;
            curr_tissue = prev_tissue;
            curr_treatment = prev_treatment;
            curr_date = prev_date;
            curr_comm = prev_comm;
            curr_exp = prev_exp;
            curr_storage = prev_storage;
            curr_usr = prev_usr;
            curr_ts = prev_ts;
         }

         if (has_history) {

            if (odd) {
               out.println("<tr bgcolor=white>");
               odd = false;
            } else {
               out.println("<tr bgcolor=lightgrey>");
               odd = true;
            }

            // name
            out.println("<td nowrap>");
            if (("" + curr_name).equals("" + prev_name))
               out.println(formatOutput(session, curr_name, 20));
            else
               out.println("<font color=red>" + formatOutput(session, curr_name, 20) + "</font>");
            out.println("</td>");
            // tissue
            out.println("<td>");
            if (("" + curr_tissue).equals("" + prev_tissue))
               out.println(formatOutput(session, curr_tissue, 20));
            else
               out.println("<font color=red>" + formatOutput(session, curr_tissue, 20) + "</font>");
            out.println("</td>");
            // Treatment
            out.println("<td nowrap>");
            if (("" + curr_treatment).equals("" + prev_treatment))
               out.println(formatOutput(session, curr_treatment, 20));
            else
               out.println("<font color=red>" + formatOutput(session, curr_treatment, 20) + "</font>");
            out.println("</td>");
            // Experimenter
            out.println("<td nowrap>");
            if (("" + curr_exp).equals("" + prev_exp))
               out.println(formatOutput(session, curr_exp, 15));
            else
               out.println("<font color=red>" + curr_exp + "</font>");
            out.println("</td>");
            // Date
            out.println("<td nowrap>");
            if (("" + curr_date).equals("" + prev_date))
               out.println(formatOutput(session, curr_date, 12));
            else
               out.println("<font color=red>" + formatOutput(session , curr_date, 12) + "</font>");
            out.println("</td>");
            // Storage
            out.println("<td nowrap>");
            if (("" + curr_storage).equals("" + prev_storage))
               out.println(formatOutput(session, curr_storage, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_storage, 12) + "</font>");
            out.println("</td>");
            // Commet
            out.println("<td nowrap>");
            if (("" + curr_comm).equals("" + prev_comm))
               out.println(formatOutput(session, curr_comm, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_comm, 12) + "</font>");
            out.println("</td>");
            out.println("<td nowrap>" + formatOutput(session, curr_usr, 10) + "</td>");
            out.println("<td nowrap>" + formatOutput(session, curr_ts, 16) + "</td>"); // Last updated
         }

         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");

         // buttons
         out.println("<form>");
         out.println("<table cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td>");

         out.println("<input type=button width=100 style=\"WIDTH: 100px\" value=\"Back\""+
                     "onClick='location.href=\""+getServletPath("viewSamples?&RETURNING=YES") + "\"'>"+"&nbsp;");

//  "onClick='location.href=\""+getServletPath("viewSamples?") + oldQS + "\"'>"+"&nbsp;");

         out.println("</td></tr></table>");

         out.println("</form>");

         out.println("</tr></table>");
         out.println("</body></html>");


      } catch (Exception e)
      {
         out.println("<PRE>");
         e.printStackTrace(out);
         out.println("</PRE>");
      }
      finally {
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
    * The new sample page
    */
   private void writeNew(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("SAVE")) {
         if (createSample(req, res, conn))
            writeFrame(req, res);
      } else {
         writeNewPage(req, res);
      }
   }
   private void writeNewPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);

      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
      Connection conn =  null;
      Statement stmt = null;
      ResultSet rset = null;

      try {
         conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();
         String suname, suid, tissue, storage, experimenter, sname, date;

         String iid, id;
         String pid = (String)session.getValue("PID");
         String oldQS = buildQS(req);
         suid = req.getParameter("suid");
         iid = req.getParameter("iid");
         id = req.getParameter("id");
         tissue = req.getParameter("TISS");
         storage = req.getParameter("STOR");

         out.println("<html>\n"
                     + "<head>\n"
                     + "<title>New SAMPLE</title>");
         printScript(out);
         out.println(getDateValidationScript());

         out.println("</head>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<body>\n");

         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Individuals - Samples - New</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         out.println("<table border=0 cellpadding=0 cellsapcing=0>");
         out.println("<tr><td width=15>&nbsp;</td><td>");

         out.println("<form action=\"" +
                     getServletPath("viewSamples/new?") + oldQS + "\" method=get>");

         out.println("<table border=0 cellspacing=0 cellpadding=0>");
         out.println("<tr>");
         // Sampling unit
         out.println("<td>Sampling unit<br>");
         out.println("<select name=suid onChange='document.forms[0].submit();' " +
                     "width=200 style=\"WIDTH: 200px\">");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SUID FROM gdbadm.V_SAMPLING_UNITS_2 " +
                                  "WHERE PID="+ pid + " AND STATUS='E'"+ " ORDER BY NAME");
         while (rset.next()) {
            // we have no suid choosen yet
            if (suid == null || suid.equals("")) {
               out.println("<option selected value=\"" + rset.getString("SUID") + "\">" +
                           rset.getString("NAME")+ "</option>\n");
               suid = rset.getString("SUID");
            } else {
               if (suid != null && suid.equals(rset.getString("SUID")) )
                  out.println("<option selected value=\"" + rset.getString("SUID") + "\">" +
                              rset.getString("NAME")+ "</option>\n");
               else
                  out.println("<option value=\"" + rset.getString("SUID") + "\">" +
                              rset.getString("NAME")+"</option>\n");
            }
         }
         rset.close();
         stmt.close();
         out.println("</select></td>");
         // Identity
         out.println("<td>Identity<br>");
         out.println("<select name=iid width=200 style=\"WIDTH: 200px\">");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT IDENTITY, IID FROM gdbadm.V_INDIVIDUALS_1 " +
                                  "WHERE SUID="+ suid + " ORDER BY IDENTITY");
         while (rset.next()) {
            if (iid != null && iid.equalsIgnoreCase(rset.getString("IID")))
               out.println("<option selected value=\"" + rset.getString("IID") + "\">" +
                           rset.getString("IDENTITY")+ "</option>\n");
            else
               out.println("<option value=\"" + rset.getString("IID") + "\">" + rset.getString("IDENTITY")+"</option>\n");
         }
         rset.close();
         stmt.close();
         out.println("</select></td>");
         out.println("</tr>");
         // Name
         out.println("<tr>");
         out.println("<td>Name<br>");
         out.println("<input type = text name=NAME maxlength=20 " +
                     "width=200 style=\"WIDTH: 200px\" value=\"\">");
         out.println("</td>");
         // Tissue
         out.println("<td>Tissue<br>");
         out.println("<input type=text name=TISS maxlength=20 " +
                     "width=200 style=\"WIDTH: 200px\" value=\"\">");
         out.println("</td>");
         out.println("</tr>");
         // Treatment
         out.println("<tr>");
         out.println("<td>Treatment<br>");
         out.println("<input type=text name=TREATMENT maxlength=20 " +
                     "width=200 style=\"WIDTH: 200px\" value=\"\">");
         out.println("</td>");
         // Experimenter
         out.println("<td>Experimenter<br>");
         out.println("<input type=text name=EXPERIMENTER maxlength=32 " +
                     "width=200 style=\"WIDTH: 200px\" value=\"\">");
         out.println("</td>");
         out.println("</tr>");
         // Storage
         out.println("<tr>");
         out.println("<td>Sample date<br>");
         out.println("<input type=text name=DATE maxlength=16 onBlur='valDate(this, true);' " +
                     "width=200 style=\"WIDTH: 200px\" value=\"\">");
         out.println("</td>");
         // Sample date
         out.println("<td>Storage<br>");
         out.println("<input type=text name=STOR maxlength=20 " +
                     "width=200 style=\"WIDTH: 200px\" value=\"\">");
         out.println("</td>");
         out.println("</tr>");
         // Comment
         out.println("<tr>");
         out.println("<td colspan=2>Comment<br>");
         out.println("<textarea name=COMM cols=60 rows=5></textarea>");
         out.println("</td>");
         out.println("</tr>");
         out.println("</table>");

         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         // Buttons
         out.println("<table border=0 cellspacing=0 cellpadding=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button value=Back onClick='location.href=\"" +
                     //getServletPath("viewSamples?") + oldQS + "\";' " +
                     getServletPath("viewSamples?&RETURNING=YES")+ "\";' " +
                     "width=100 style=\"WIDTH: 100px\">&nbsp;");
         out.println("</td>");
         out.println("<td>");
         out.println("<input type=button value=Create onClick='valForm(\"SAVE\");' " +
                     "width=100 style=\"WIDTH: 100px\">&nbsp;");
         out.println("</td>");
         out.println("</tr>");
         out.println("</table>");

         out.println("<input type=hidden  name=oper value=\"\">");
         out.println("<input type=\"hidden\"  NAME=RETURNING value=YES>");

/*
         out.println("<input type=\"hidden\" ID=ACTION NAME=ACTION value=\""+req.getParameter("ACTION")+"\">");
         out.println("<input type=\"hidden\" ID=STARTINDEX NAME=STARTINDEX value=\""+req.getParameter("STARTINDEX")+"\">");
         out.println("<input type=\"hidden\" ID=ROWS NAME=ROWS value=\""+req.getParameter("ROWS")+"\">");
         out.println("<input type=\"hidden\" ID=ORDERBY NAME=ORDERBY value=\""+req.getParameter("ORDERBY")+"\">");

*/
         out.println("</form>");
         out.println("</td></tr></table>");
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
      out.println("	if ( (\"\" + document.forms[0].r1.value) != \"\") {");
      out.println("		if (document.forms[0].r1.value.length > 20) {");
      out.println("			alert('Raw 1 must be less than 20 characters!');");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	}");
      out.println("	if ( (\"\" + document.forms[0].r2.value) != \"\") {");
      out.println("		if (document.forms[0].r1.value.length > 20) {");
      out.println("			alert('Raw 2 must be less than 20 characters!');");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	}");
      out.println("	");
      out.println("	");
      out.println("	if (rc) {");
      out.println("		if (confirm('Are you sure that you want to create the genotype?')) {");
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
    * The sample edit page
    */
   private void writeEdit(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";
      if (oper.equals("DELETE")) {
         if (deleteSample(req, res, conn))
            writeFrame(req, res);
      } else if (oper.equals("UPDATE")) {
         if (updateSample(req, res, conn))
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
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
      Connection conn =  null;
      Statement stmt = null;
      ResultSet rset = null;
      String said = req.getParameter("said");
      String tissue=null, storage=null ,sname=null,ind=null, suid=null;
      try {
         conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();
//         String oldQS = buildQS(req);
          String oldQS = removeQSParameter(req.getQueryString(),"oper");

         String strSQL = "SELECT SAID, IID, IDENTITY, NAME,TISSUE_TYPE, TREATMENT, EXPERIMENTER, SUID, "
            + "to_char(DATE_, 'YYYY-MM-DD') as TC_DATE, COMM, "
            + "STORAGE,USR, "
            + "to_char(TS, '" + getDateFormat(session) + "') as TC_TS "
            + "FROM gdbadm.V_SAMPLES_3 WHERE "
            + "SAID = " + said;
         rset = stmt.executeQuery(strSQL);

         rset.next();
         out.println("<html>\n"
                     + "<head>\n"
                     + "<title>Sample Edit</title>");
         printScript(out);
         out.println(getDateValidationScript());
         out.println("</head>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<body bgcolor=\"fafad2\">\n");

         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Samples - View & Edit - Edit</b></font></center>");
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
         out.println("<tr><td>Individual</td><td>" + rset.getString("IDENTITY") + "</td></tr>");
         out.println("<tr><td>Last updated by</td><td>" + rset.getString("USR") + "</td></tr>");
         out.println("<tr><td>Last updated</td><td>" + rset.getString("TC_TS") + "</td></tr>");
         out.println("</table></tr></td>");



         out.println("<form action=\""+getServletPath("viewSamples/edit?") + oldQS + "\" method=\"get\" name=\"FORM1\">");

         // dynamic data table
         out.println("<tr><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0 width=600>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Changable data</font></td></tr>");

         out.println("<tr><td width=200 align=left>Sample Name</td>");
         out.println("<td width=200 align=left>Tissue</td></tr>");
         out.println("<tr><td width=200><input name=NAME type=\"text\" maxlength=12 style=\"WIDTH: 200px\" value=\"" +
                     replaceNull(rset.getString("NAME"), "") + "\"></td>");
         out.println("<td width=200 ><input name=TISSUE type=\"text\" maxlength=12 style=\"WIDTH: 200px\" value=\"" +
                     replaceNull(rset.getString("TISSUE_TYPE"), "") + "\"></td></tr>");

         out.println("<tr><td width=200 align=left>Treatment</td>");
         out.println("<td width=200 align=left>Experimenter</td></tr>");
         out.println("<tr><td width=200><input name=TREATMENT type=\"text\" maxlength=12 style=\"WIDTH: 200px\" value=\"" +
                     replaceNull(rset.getString("TREATMENT"), "") + "\"></td>");
         out.println("<td width=200 ><input name=EXPERIMENTER type=\"text\" maxlength=12 style=\"WIDTH: 200px\" value=\"" +
                     replaceNull(rset.getString("EXPERIMENTER"), "") + "\"></td></tr>");

         out.println("<tr><td width=200 align=left>Sample date</td>");
         out.println("<td width=200 align=left>Storage</td></tr>");
         out.println("<tr><td width=200><input type=text maxlength=10 name=DATE style=\"WIDTH: 200px\" value=\"" +
                     replaceNull( rset.getString("TC_DATE"), "") + "\"" +
                     "onBlur='valDate(this, true);'></td>");
         out.println("<td width=200><input type=text maxlength=10 name=STORAGE style=\"WIDTH: 200px\" value=\"" +
                     replaceNull( rset.getString("STORAGE"), "") + "\"></td></tr>");

         out.println("<tr><td width=200 align=left>Comment</td></tr>");
         out.println("<tr><td colspan=3>");
         out.println("<textarea name=comment cols=60 rows=5>" +
                     replaceNull(rset.getString("COMM"), "") + "</textarea>");
         out.println("</td>");
         out.println("</tr>");

         out.println("</table></td></tr>");


         // buttons table
         out.println("<tr><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0 width=300>");
         out.println("<tr><td width=200>&nbsp;</td>");
         out.println("<td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td></tr>");
         out.println("<tr>");
         out.println("<td><input type=button " +
                     "width=100 style=\"WIDTH: 100px\" value=\"Back\" onClick='location.href=\"" +
                   //  getServletPath("viewSamples?") + oldQS +"\" '>&nbsp;</td>");
                        getServletPath("viewSamples?&RETURNING=YES") +"\" '>&nbsp;</td>");

                     /*
                     "&suid=" + rset.getString("SUID") +
                     "&ACTION=DISPLAY\"'>&nbsp;</td>");
                     */
         out.println("<td><input type=reset value=Reset " +
                     "width=100 style=\"WIDTH: 100px\">&nbsp;</td>");
         out.println("<td><input type=button name=DELETE value=Delete " +
                     "width=100 style=\"WIDTH: 100px\" onClick='valForm(\"DELETE\")'>&nbsp;</td>");
         out.println("<td><input type=button name=UPDATE value=Update " +
                     "width=100 style=\"WIDTH: 100px\" onClick='valForm(\"UPDATE\")'>&nbsp;</td>");

         out.println("</tr>");
         out.println("</table></td></tr>");

         // Store some extra information needed by doPost()
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("<input type=hidden name=RETURNING value=YES>");

         out.println("<input type=hidden name=iid value="+rset.getString("IID")+">");
         out.println("<input type=hidden name=said value="+said+">");
//        System.err.println("samples/edit:"+oldQS);


        //tissue, storage ,sname,ind;
        tissue= req.getParameter("TISSUE");
        storage= req.getParameter("STORAGE");
        sname = req.getParameter("SNAME");
        ind = req.getParameter("IDENTITY");
        suid = req.getParameter("suid");


        if (tissue != null && !tissue.trim().equalsIgnoreCase(""))
        {
          out.println("<input type=\"hidden\" ID=TISSUE NAME=TISSUE value=\""+tissue+"\">");
        }
        if (ind != null && !ind.trim().equalsIgnoreCase(""))
        {
          out.println("<input type=\"hidden\" ID=IDENTITY NAME=IDENTITY value=\""+ind+"\">");
        }
        if (sname != null && !sname.trim().equalsIgnoreCase(""))
        {
          out.println("<input type=\"hidden\" ID=SNAME NAME=SNAME value=\""+sname+"\">");
        }
        if (storage != null && !storage.trim().equalsIgnoreCase(""))
        {
          out.println("<input type=\"hidden\" ID=STORAGE NAME=STORAGE value=\""+storage+"\">");
        }
        if (suid != null && !suid.trim().equalsIgnoreCase(""))
        {
          out.println("<input type=\"hidden\" ID=suid NAME=suid value=\""+req.getParameter("suid")+"\">");
        }
/*
         out.println("<input type=\"hidden\" ID=ACTION NAME=ACTION value=\""+req.getParameter("ACTION")+"\">");
         out.println("<input type=\"hidden\" ID=STARTINDEX NAME=STARTINDEX value=\""+req.getParameter("STARTINDEX")+"\">");
         out.println("<input type=\"hidden\" ID=ROWS NAME=ROWS value=\""+req.getParameter("ROWS")+"\">");
         out.println("<input type=\"hidden\" ID=ORDERBY NAME=ORDERBY value=\""+req.getParameter("ORDERBY")+"\">");
*/

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
    * Creates a new sample.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if sample created.
    *         False if sample not created.
    */
   private boolean createSample(HttpServletRequest request,
                                HttpServletResponse response,
                                Connection connection)
   {
      boolean ret = true;
      boolean isOk = true;
      String errMessage = null;

      try
      {
         HttpSession session = request.getSession(true);
         int iid, id, suid;
         String identity, sname, storage, tissue, experimenter,
            date, treatment, comm;

         connection.setAutoCommit(false);
         id = Integer.parseInt((String) session.getValue("UserID"));
         suid = Integer.parseInt(request.getParameter("suid"));
         iid = Integer.parseInt(request.getParameter("iid"));

         identity= request.getParameter("identity");
         storage = request.getParameter("STOR");
         tissue= request.getParameter("TISS");
         sname= request.getParameter("NAME");
         comm= request.getParameter("COMM");
         date= request.getParameter("DATE");
         treatment= request.getParameter("TREATMENT");
         experimenter= request.getParameter("EXPERIMENTER");

         DbIndividual dbi = new DbIndividual();
         dbi.CreateSample(connection, id, iid, sname, tissue, storage, experimenter,
                          date, treatment, comm);

         errMessage = dbi.getErrorMessage();
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

      commitOrRollback(connection, request, response, "Samples.New.Create",
                       errMessage, "viewSamples", isOk);
      return isOk;
   }


   /**
    * Deletes a sample.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if sample deleted.
    *         False if sample not deleted.
    */
   private boolean deleteSample(HttpServletRequest request,
                                HttpServletResponse response,
                                Connection connection)
   {
      String errMessage = null;
      boolean isOk = true;
      try
      {
         HttpSession session = request.getSession(true);
         int said;
         connection.setAutoCommit(false);

         String UserID = (String) session.getValue("UserID");
         said = Integer.parseInt(request.getParameter("said"));
         DbIndividual dbi = new DbIndividual();
         dbi.DeleteSample(connection, said);
         errMessage = dbi.getErrorMessage();
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
                       "Samples.Edit.Delete", errMessage, "viewSamples",
                       isOk); 
      return isOk;
   }

   
   /**
    * Updates a sample.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if sample updated.
    *         False if sample not updated.
    */
   private boolean updateSample(HttpServletRequest request,
                                HttpServletResponse response,
                                Connection connection)
   {
      String errMessage = null;
      boolean isOk = true;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         String UserID = (String) session.getValue("UserID");
         String name, tissue, storage, experimenter, date, treatment, comm;
         String oldQS = request.getQueryString();
         int  said;

         said = Integer.parseInt(request.getParameter("said"));
         name = request.getParameter("NAME");
         tissue = request.getParameter("TISSUE");
         storage = request.getParameter("STORAGE");
         experimenter = request.getParameter("EXPERIMENTER");
         date = request.getParameter("DATE");
         treatment = request.getParameter("TREATMENT");
         comm = request.getParameter("comment");

         DbIndividual dbi = new DbIndividual();
         dbi.UpdateSample(connection, Integer.parseInt(UserID),said,
                          name, tissue,
                          storage, experimenter, date, treatment,
                          comm );

         errMessage = dbi.getErrorMessage();
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
                       "Samples.Edit.Update", errMessage, "viewSamples",
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
      out.println("		if (confirm('Are you sure you want to delete the genotype?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the genotype?')) {");
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
	

   private void printScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('SAVE' == action.toUpperCase()) {");
      out.println("   if (!valDate(document.forms[0].DATE, true))");
      out.println("     return false;");
      out.println("		if (confirm('Are you sure you want to create the sample?')) {");
      out.println("			document.forms[0].oper.value='SAVE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	}");

      out.println("	else if ('UPDATE' == action.toUpperCase()) {");
      out.println("   if (!valDate(document.forms[0].DATE, true))");
      out.println("     return false;");
      out.println("		if (confirm('Are you sure you want to update sample?')) {");
      out.println("			document.forms[0].oper.value='UPDATE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	}");

      out.println("	else if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to delete the sample?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
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
            // We neew the privilege IND_R for all these
            title = "Individuals - Samples - View & Edit";
            if ( privDependentString(privileges, IND_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/edit") ) {
            // We neew the privilege IND_W
            title = "Individuals - Samples - Edit";
            if ( privDependentString(privileges, IND_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/new") ) {
            // We neew the privilege IND_W
            title = "Individuals - Samples - New";
            if ( privDependentString(privileges, IND_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/impFile") ) {
            // We need the privilege IND_W
            title = "Individuals - Samples - File Import";
            if ( privDependentString(privileges, IND_W, "", null) == null)
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

