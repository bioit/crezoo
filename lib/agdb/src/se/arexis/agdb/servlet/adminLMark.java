/*
  $Log$
  Revision 1.6  2005/02/04 15:58:40  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.5  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.4  2003/04/25 12:14:45  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.3  2002/10/22 06:08:07  heto
  rebuilt the "back-buttons".
  Dont save the request object, save the URL instead.
  New function.

  Revision 1.2  2002/10/18 11:41:08  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:04  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.13  2001/05/31 07:06:51  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.12  2001/05/22 06:54:28  roca
  backfunktionality for administrator pages and privileges removed from roles (user mode)

  Revision 1.11  2001/05/08 11:11:24  frob
  Removed unused methods: writeChrom, writeUser, writeUserPage, writeUserScript,
  updateUser and findCid.

  Revision 1.10  2001/05/08 06:25:02  frob
  Changed some error key names.

  Revision 1.9  2001/05/07 06:53:05  frob
  Changed all methods that used to call writeError to write an error page. These
  methods now calls the general method commitOrRollback which handles any errors.

  Revision 1.8  2001/05/03 13:23:27  frob
  Implemented local version of errorQueryString and changed writeError to use this
  method when calling writeErrorPage.

  Revision 1.7  2001/05/03 07:57:31  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.6  2001/05/02 11:01:10  frob
  Changed calls to removeCid, removeOper and removeSid to use the general removeQSParameter.

  Revision 1.5  2001/04/27 14:14:02  frob
  Changed writeError, it now calls the general writeErroPage.

  Revision 1.4  2001/04/24 09:33:45  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.3  2001/04/24 06:31:18  frob
  Checkin after merging frob_fileparser branch.

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
import se.arexis.agdb.util.FileImport.*;

public class adminLMark extends AdminArexisServlet
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
      } else if (extPath.equals("/impLMarkFile")) {
         writeImpLMarkFile(req, res);
      } else if (extPath.equals("/impLMarkSU")) {
         writeImpLMarkSU(req, res);
      } else if (extPath.equals("/editAllele")) {
         writeEditAllele(req, res);
      } else if (extPath.equals("/newAllele")) {
         writeNewAllele(req, res);
      } else if (extPath.equals("/details")) {
         writeDetails(req, res);
      } else if (extPath.equals("/impLMarkMultipart")) {
         if (createLMarkFile(req, res))
            writeFrame(req, res);
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
         // Check if redirection is needed and save URL 
         res = checkRedirectStatus(req,res);
          
         // Old method.
         //req= getServletState(req,session);
      
         String topQS = buildQS(req);
         // we don't want the oper parameter anaywhere but in the "edit" and "new" page!
         topQS = removeQSParameterOper(topQS);
         
         String bottomQS = topQS.toString();

         out.println("<html>"
                     + "<HEAD>"
                     + " <TITLE>Administrate Library Markers</TITLE>"
                     + "</HEAD>"
                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" //
                     + "<frame name=\"lmarktop\" "
                     + "src=\""+ getServletPath("adminLMark/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"lmarkmiddle\" "
                     + "src=\""+ getServletPath("adminLMark/middle?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"lmarkbottom\""
                     + "src=\"" +getServletPath("adminLMark/bottom?") + bottomQS + "\" "
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
         old_sid = null,
         sid = null, // species id
         cid = null, // Chromosome id
         name = null,
         lmid = null,
         orderby = null;
      boolean sid_changed = false;
      old_sid = (String) session.getValue("SID");
      // We need to make sure that the old_sid doesn't equal '*', since
      // this page doesn't support that alternetive
      if ("*".equals(old_sid) ) old_sid = null;
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
      session.putValue("SID", sid);

      cid = req.getParameter("cid");
      if (cid == null || cid.trim().equals("") || sid_changed) {
         cid = "*" ; //findCid(conn, sid);
      }

      name = req.getParameter("name");
      lmid = req.getParameter("lmid");

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
      if (sid != null && !sid.trim().equals(""))
         output.append("&sid=").append(sid);
      if (cid != null && !cid.trim().equals(""))
         output.append("&cid=").append(cid);
      if (lmid != null && !lmid.trim().equals(""))
         output.append("&lmid=").append(lmid);
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
   private String findSuid(Connection conn) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID FROM gdbadm.V_SAMPLING_UNITS_1 ORDER BY NAME");
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

   private String findCidFromSuid(Connection conn, String suid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID FROM gdbadm.V_CHROMOSOMES_1 WHERE SID " +
                                  "IN(SELECT SID FROM V_SAMPLING_UNITS_1 WHERE SUID=" + suid + ")" +
                                  " ORDER BY to_number_else_null(NAME), NAME");
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

   private String setIndecis(boolean sid_changed, String action, HttpServletRequest req, HttpSession session) {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(req, session);
      maxRows = getMaxRows(session);
      if (req.getParameter("STARTINDEX") != null && !sid_changed) {
         startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         // System.err.println("start="+ startIndex);
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
         sbSQL.append("SELECT count(lmid) " +
                      "FROM V_L_MARKERS_1 WHERE 1=1 ");
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
      String sid = null,
         cid = null,
         name = null;
      //			     orderby = null;
      StringBuffer filter = new StringBuffer(256);
      sid = req.getParameter("sid");
      cid = req.getParameter("cid");
      name = req.getParameter("name");

      if (sid != null && !sid.trim().equals(""))
         filter.append(" and SID=" + sid);
      if (cid != null && !cid.trim().equals("") && !cid.equals("*"))
         filter.append(" and CID=" + cid);
      if (name != null && !name.trim().equals(""))
         filter.append(" and name like '" + name + "'");

      // if (order)
      // {
      //		if (orderby != null && !"".equalsIgnoreCase(orderby))
      //			filter.append(" order by " + orderby);
      //		else
      //			filter.append(" order by NAME");
      // }

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
      String sid, cid, name, oldQS, newQS, action;
      try {
         conn = (Connection) session.getValue("conn");
      
         // We baldly count on that sid and cid have been set by buildQS(..)
         sid = req.getParameter("sid");
         cid = req.getParameter("cid");
         name = req.getParameter("name");
         action = req.getParameter("ACTION");
         oldQS = req.getQueryString();
         newQS = buildTopQS(oldQS);
         if (req.getParameter("STARTINDEX") != null)
            startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         else
            startIndex = 0;
         if (req.getParameter("ROWS") != null)
            rows = Integer.parseInt(req.getParameter("ROWS"));
         else
            rows = 0;
         if (name == null || name.trim().equals(""))
            name = "";

         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+ getURL("style/view.css") +"\">");
         out.println("<base target=\"adminmainframe\">");

         out.println("<title>Library Markers</title>");
         out.println("</head>");

         out.println("<body bgcolor=\"#ffffd0\">");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<form method=get action=\"" +getServletPath("adminLMark") +"\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Library Markers</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr><tr><td width=\"517\">");

         out.println("<table width=488 height=\"92\">" +
                     "<td><b>&nbsp;</td>");

         // Species
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID, NAME FROM V_SPECIES_1 ORDER BY NAME");
         out.println("<td><b>Species</b><br>");
         out.println("<select name=\"sid\" width=120 style=\"WIDTH: 120px\" " +
                     "onChange='JavaScript:document.forms[0].submit();'>");
         while (rset.next()) {
            if (sid.equals(rset.getString("SID")) ) {
               out.println("<option selected value=\"" + rset.getString("SID") + "\">" +
                           rset.getString("NAME") + "</option>");
            } else {
               out.println("<option value=\"" + rset.getString("SID") + "\">" +
                           rset.getString("NAME") + "</option>");
            }
         }
         out.println("</select></td>");

         // Chromosomes
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM V_CHROMOSOMES_1 " +
                                  "WHERE SID=" + sid + " ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME");
         out.println("<td><b>Chromosome</b><br>");
         out.println("<select name=\"cid\" width=120 " +
                     "style=\"WIDTH: 120px\">");
         out.println("<option ");
         if ("*".equals(cid))
            out.println("selected ");
         out.println("value=\"*\">*</option>");
         while (rset.next() ) {
            if (cid.equals(rset.getString("CID")) ) {
               out.println("<option selected value=\"" + rset.getString("CID") + "\">" +
                           rset.getString("NAME") + "</option>");
            } else {
               out.println("<option value=\"" + rset.getString("CID") + "\">" +
                           rset.getString("NAME") + "</option>");
            }
         }
         out.println("</select></td>");

         // Name
         out.println("<td><b>Name</b><br>");
         out.println("<input name=\"name\" width=120 style=\"WIDTH: 120px\" " +
                     "value=\"" + name + "\"></td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td><b>&nbsp;</td>");
         out.println("<td><b>&nbsp;</b><br></td>");
         out.println("<td><b>&nbsp;</b><br></td></table></td>");

         //Buttons
         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");
         out.println("<input type=button value=\"New L. Marker\"" +
                     " onClick='parent.location.href=\"" +getServletPath("adminLMark/new?") + newQS + "\"' " +
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
         //      out.println("<input type=\"hidden\" id=\"ORDERBY\" name=\"ORDERBY\" value=\"" + orderby + "\">");
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
      maxRows = getMaxRows(session);

      try {
         out.println("<html>\n<head>\n<link rel=\"stylesheet\" " +
                     "type=\"text/css\" href=\""+getURL("style/tableBar.css")+"\">");
         out.println("<base target=\"adminmainframe\">");
         out.println("</head>");
         out.println("<body>");
         if(action != null)
         {
            //out.println("<p align=left>&nbsp;&nbsp;");
            out.println("&nbsp;"+buildInfoLine(action, startIndex, rows, maxRows));
         }

         String oldQS, newQS;
         oldQS = req.getQueryString();
         String choosen= req.getParameter("ORDERBY");
         newQS = buildTopQS(oldQS);
         /*
           out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 " +
           "height=20 width=820 style=\"margin-left:2px\">" +
           "<td width=5></td>");
         */
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=850 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");


         // the menu choices
         // Chromosome
         out.println("<td width=100><a href=\"" +
                     getServletPath("adminLMark")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=CNAME\">");
         if(choosen.equals("CNAME"))
            out.println("<FONT color=saddlebrown><b>Chromosome</b></FONT></a></td>");
         else
            out.println("Chromosome</a></td>");
         // Name
         out.println("<td width=150><a href=\"" +
                     getServletPath("adminLMark") + "?ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\">");
         if (choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown><b>Name</b></FONT></a></td>");
         else
            out.println("Name</a></td>");
         // Comment
         out.println("<td width=250><a href=\"" +
                     getServletPath("adminLMark")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\">");
         if(choosen.equals("COMM"))
            out.println("<FONT color=saddlebrown><b>Comment</b></FONT></a></td>");
         else
            out.println("Comment</a></td>");
         // Primer 1
         out.println("<td width=100><a href=\"" +
                     getServletPath("adminLMark") + "?ACTION=DISPLAY&" + newQS + "&ORDERBY=P1\">");
         if (choosen.equals("P1"))
            out.println("<font color=saddlebrown><b>Primer 1</b></font></a></td>");
         else
            out.println("Primer 1</a></td>");
         // Primer 2
         out.println("<td width=100><a href=\"" +
                     getServletPath("adminLMark") + "?ACTION=DISPLAY&" + newQS + "&ORDERBY=P2\">");
         if (choosen.equals("P2"))
            out.println("<font color=saddlebrown><b>Primer 2</b></font></a></td>");
         else
            out.println("Primer 2</a></td>");
         // Position
         out.println("<td width=50><a href=\"" +
                     getServletPath("adminLMark") + "?ACTION=DISPLAY&" + newQS + "&ORDERBY=POSITION\">");
         if (choosen.equals("POSITION"))
            out.println("<font color=saddlebrown><b>Pos.</b></font></a></td>");
         else
            out.println("Pos.</a></td>");

         /*
           out.println("<td width=80>&nbsp;</td>"); // Details
           out.println("<td width=80>&nbsp;</td>"); // Edit
           out.println("</tr></table>");
           //      out.println("</td></tr></table>");
           out.println("</body></html>");
         */
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
      // set content type and other response header fields first
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      Statement stmt = null;
      ResultSet rset = null;
      Connection conn = null;
      try {
         String action = null, orderby;
         String oldQS = req.getQueryString();
         oldQS = removeQSParameterSid(oldQS);
         
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
         sbSQL.append("SELECT CNAME, LMID, NAME, COMM, P1, P2, POSITION " +
                      "FROM V_L_MARKERS_2 WHERE 1=1 ");
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);
         if ("CNAME".equals(orderby))
            sbSQL.append(" ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME");
         else
            sbSQL.append(" ORDER BY ").append(orderby);
         rset = stmt.executeQuery(sbSQL.toString());

         out.println("<TABLE align=left border=0 cellPadding=0");
         out.println("cellSpacing=0 width=850 style=\"margin-left:2px\">");//STYLE=\"WIDTH: 790px;\">");
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
            out.println("<td width=5></td>");
            out.println("<TD WIDTH=100>" + formatOutput(session, rset.getString("CNAME"), 3) + "</TD>");
            out.println("<TD WIDTH=150>" + formatOutput(session, rset.getString("NAME"),15) +"</TD>");
            out.println("<TD WIDTH=250>" + formatOutput(session, rset.getString("COMM"),30)+"</TD>");
            out.println("<TD WIDTH=100>" + formatOutput(session, rset.getString("P1"),10)+"</TD>");
            out.println("<TD WIDTH=100>" + formatOutput(session, rset.getString("P2"),10)+"</TD>");
            out.println("<TD WIDTH=50>" + formatOutput(session, rset.getString("POSITION"),5)+"</TD>");

            out.println("<TD WIDTH=50><A HREF=\"" +getServletPath("adminLMark/details?lmid=")
                        + rset.getString("LMID")
                        + "&" + oldQS + "\" target=\"adminmainframe\">Details</A></TD>");
            out.println("<TD WIDTH=50><A HREF=\"" +getServletPath("adminLMark/edit?lmid=")
                        + rset.getString("LMID")
                        + "&" + oldQS + "\" target=\"adminmainframe\">Edit</A></TD>");
            out.println("</TD></TR>");

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
   private void writeDetails(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();

      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null;
      String name, comm, cname, sname, p1, p2, position;
      boolean odd = true;
      try {
         String oldQS = buildQS(req);
         String lmid = req.getParameter("lmid");
         conn = (Connection) session.getValue("conn");
         out.println("<html>");
         out.println("<head>");
         out.println("<title>Library Markers - Details</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Library Markers - Details</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         // General Data
         stmt = conn.createStatement();
         String strSQL = "SELECT SNAME, CNAME, CID, NAME, COMM, P1, P2, POSITION " +
            "FROM V_L_MARKERS_2 WHERE LMID=" + lmid ;
         rset = stmt.executeQuery(strSQL);
         rset.next();
         name = rset.getString("NAME");
         comm = rset.getString("COMM");
         sname = rset.getString("SNAME");
         cname = rset.getString("CNAME");
         p1 = rset.getString("P1");
         p2 = rset.getString("P2");
         position = rset.getString("POSITION");
         out.println("</tr><tr><td></td><td></td><tr><td></td><td>");
         out.println("<table><tr>");
         out.println("<td width=15 style=\"WIDTH: 15px\">");
         out.println("</td><td>");
         out.println("<table cellspacing=0 cellpadding=0 border=0>");
         out.println("<tr>");
         out.println("<td colspan=2 align=center>");
         out.println("<b>General</b>");
         out.println("</td></tr>");
         out.println("<tr><td width=200>Species</td><td>" + formatOutput(session, sname, 20) + "</td></tr>");
         out.println("<tr><td>Chromosome</td><td>" + formatOutput(session, cname, 20) + "</td></tr>");
         out.println("<tr><td>Name</td><td>" + formatOutput(session, name, 20) + "</td></tr>");
         out.println("<tr><td>Comment</td><td>" + formatOutput(session, comm, 256) + "</td></tr>");
         out.println("<tr><td>Primer 1</td><td>" + formatOutput(session, p1, 40) + "</td></tr>");
         out.println("<tr><td>Primer 2</td><td>" + formatOutput(session, p2, 40) + "</td></tr>");
         out.println("<tr><td>Position</td><td>" + formatOutput(session, position, 20) + "</td></tr>");
         out.println("</table>");

         out.println("</tr><tr><td></td><td></td><tr><td></td><td>");

         // Alleles
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, COMM FROM V_L_ALLELES_1 " +
                                  "WHERE lmid=" + lmid + " ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME");
         out.println("<table cellspacing=0 cellpadding=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<b>Alleles</b>");
         out.println("</td><td>&nbsp;</td></tr>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td width=200 style=\"WIDTH: 200px\">Name</td>");
         out.println("<td width=250 style=\"WIDTH: 250px\">Comment</td>");
         out.println("</tr>");
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
            out.println(formatOutput(session, rset.getString("COMM"), 30));
            out.println("</td></tr>");
         }
         out.println("</table>");


         out.println("</tr><tr><td></td><td></td><tr><td></td><td>");
         out.println("</tr><tr><td></td><td></td><tr><td></td><td>");
         out.println("</td></tr><tr><td></td><td>");
         out.println("<form>");
   /*
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='history.go(-1);'>");
*/

         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("adminLMark?&RETURNING=YES") + "\"'>&nbsp;");



         out.println("</form>");
         out.println("</td>");
         out.println("</tr></table>");

         out.println("</body></html>");
      } catch (SQLException e)
      {
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
    * The new species page
    */
   private void writeNew(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (create(req, res, conn)) {
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
      ResultSet rset = null;
      Statement stmt = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String newQS, oper, sid, cid, name;

      try {
         conn = (Connection) session.getValue("conn");
         sid = req.getParameter("sid");
         cid = req.getParameter("cid");
         name = req.getParameter("name");
         if (name == null) name = "";
         newQS = "";
         oper = req.getParameter("oper");

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeNewScript(out);
         out.println("<title>New Library Marker</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Library Markers - New</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=get action=\"" +
                     getServletPath("adminLMark/new?") + newQS + "\">");
         out.println("<table width=400 border=0 cellSpacing=0 cellPading=5>");
         // Species
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID, NAME FROM V_SPECIES_1 ORDER BY NAME");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("Species<br>");
         out.println("<select name=sid width=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"SID\");'>");
         while (rset.next()) {
            if (sid.equals(rset.getString("SID")) )
               out.println("<option selected value=\"" + rset.getString("SID") + "\">" +
                           rset.getString("NAME") + "</option>");
            else
               out.println("<option value=\"" + rset.getString("SID") + "\">" +
                           rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td></tr>");
         rset.close();
         stmt.close();

         // Chromosome
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM V_CHROMOSOMES_1 " +
                                  "WHERE SID=" + sid + " ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("Chromosome<br>");
         out.println("<select name=cid width=200 style=\"WIDTH: 200px\"'>");
         while (rset.next()) {
            if (cid.equals(rset.getString("CID")) )
               out.println("<option selected value=\"" + rset.getString("CID") + "\">" +
                           rset.getString("NAME") + "</option>");
            else
               out.println("<option value=\"" + rset.getString("CID") + "\">" +
                           rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td></tr>");

         // Name
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("Name<br>");
         out.println("<input name=n maxlength=20 width=200 " +
                     "style=\"WIDTH: 200px; HEIGHT: 22px\">");
         out.println("</td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         // Alias
         out.println("Alias<br>");
         out.println("<input name=a maxlength=20 width=200 " +
                     "style=\"WIDTH: 200px; HEIGHT: 22px\">");
         out.println("</td></tr>");
         // Primer 1
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("Primer 1<br>");
         out.println("<input name=p1 maxlength=40 width=200 " +
                     "style=\"WIDTH: 200px\">");
         out.println("</td></tr>");
         // Primer 2
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("Primer 2<br>");
         out.println("<input name=p2 maxlength=40 width=200 " +
                     "style=\"WIDTH: 200px\">");
         out.println("</td></tr>");
         // Position
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("Position<br>");
         out.println("<input name=p maxlength=20 width=200 " +
                     "style=\"WIDTH: 200px; HEIGHT: 22px\">");
         out.println("</td></tr>");
         // Comment
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td>Comment<br>");
         out.println("<textarea rows=10 cols=40 name=c>");
         out.println("</textarea>");
         out.println("</td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td></td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table cellspacing=0 cellpading=0 border=0><tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                     /*"style=\"WIDTH: 100px\" onClick='document.location.href=\"" +
                     getServletPath("adminLMark?") + newQS + "\"'>");
                    */
                    "style=\"WIDTH: 100px\" onClick='document.location.href=\"" +
                     getServletPath("adminLMark?&RETURNING=YES")  + "\"'>");


         out.println("&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Create width=100 " +
                     "style=\"WIDTH: 100px\" onClick='valForm()'>");
         out.println("&nbsp;</td></tr></table>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=oper value=\"\">");
          out.println("<input type=hidden name=RETURNING value=YES>");

         out.println("<input type=hidden name=name value=\"" + name + "\">");
         out.println("</form>");
         out.println("</body>");
         out.println("</html>");
      } catch (Exception e) {
         e.printStackTrace(System.err);
      } finally {
         ;
      }
   }

   /***************************************************************************************
    * *************************************************************************************
    * The new library allele page
    */
   private void writeNewAllele(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createAllele(req, res, conn)) {
            writeEditPage(req, res);
         } else {
            ; // We have already displayed an error message!
         }
      } else {
         writeNewAllelePage(req, res);
      }
   }
   private void writeNewAllelePage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String newQS, lmid;
      try {
         conn = (Connection) session.getValue("conn");
         lmid = req.getParameter("lmid");
         newQS = removeQSParameterOper(req.getQueryString());

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeNewAlleleScript(out);
         out.println("<title>New library allele</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Library Markers - Edit - Create Allele</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" +
                     getServletPath("adminLMark/newAllele?") + newQS + "\">");
         out.println("<table width=400 border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("Name<br>");
         out.println("<input name=n maxlength=20 width=200 " +
                     "style=\"WIDTH: 200px; HEIGHT: 22px\">");
         out.println("</td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td>Comment<br>");
         out.println("<textarea rows=10 cols=40 name=c>");
         out.println("</textarea>");
         out.println("</td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td></td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table cellspacing=0 cellpading=0 border=0><tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='document.location.href=\"" +
                     getServletPath("adminLMark/edit?") + newQS + "\"'>");
         out.println("&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Create width=100 " +
                     "style=\"WIDTH: 100px\" onClick='valForm()'>");
         out.println("&nbsp;</td></tr></table>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=oper value=\"\">");
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

   private void writeImpLMarkSU(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("COPY")) {
         if (importSuMark(req, res, conn)) {
            writeImpLMarkSUPage(req, res);
         } else {
            ; // We have already displayed an error message!
         }
      } else {
         writeImpLMarkSUPage(req, res);
      }
   }

   private void writeImpLMarkSUPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String sid, cid, suid, pid, oper, item;
      try {
         conn = (Connection) session.getValue("conn");
         pid = (String) session.getValue("PID");
         suid = req.getParameter("suid");
         cid = req.getParameter("cid");
         oper = req.getParameter("oper");
         if (oper == null) oper = "SEL_CHANGED";
         item = req.getParameter("item");
         if (item == null) item = "";
         if (oper.equals("SEL_CHANGED")) {
            if (item.equals("suid")) {
               cid = findCidFromSuid(conn, suid);
            } else if (item.equals("cid")) {
               ;
            } else {
               suid = findSuid(conn);
               cid = findCidFromSuid(conn, suid);
            }
         }

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeSelectionScript(out);
         out.println("<title>Import Library Marker</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Library Markers - Import from Sampling unit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<table border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=20></td><td>");

         out.println("<form method=post action=\"" +
                     getServletPath("adminLMark/impLMarkSU") + "\">");
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr>");
         // From Sampling unit
         out.println("<td>Sampling unit<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM V_SAMPLING_UNITS_1 " +
                                  " ORDER BY NAME");
         out.println("<select name=suid width=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"suid\");'>");
         while (rset.next()) {
            out.println("<option " +
                        (suid.equals(rset.getString("SUID")) ? "selected " : "") +
                        "value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println ("</select>");
         out.println("</td>");
         rset.close();
         stmt.close();

         out.println("</tr>");
         out.println("<tr>");
         // Chromosome
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM V_CHROMOSOMES_1 " +
                                  "WHERE SID IN(SELECT SID FROM V_SAMPLING_UNITS_1 WHERE SUID=" + suid + ") " +
                                  " ORDER BY " +
                                  "TO_NUMBER_ELSE_NULL(NAME), NAME");
         out.println("<td>Chromosome<br>");
         out.println("<select name=cid width=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"cid\");'>");
         while (rset.next() ) {
            out.println("<option " +
                        (cid.equals(rset.getString("CID")) ? "selected " : "") +
                        "value=\"" + rset.getString("CID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td>");
         rset.close();
         stmt.close();

         out.println("</tr>");
         out.println("<tr>");
         // Marker
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT MID, NAME FROM V_MARKERS_1 WHERE " +
                                  "SUID=" + suid + " AND CID=" + cid + " ORDER BY NAME");
         out.println("<td>Marker<br>");
         out.println("<select name=mid width=200 style=\"WIDTH: 200px\">");
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("MID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td>");
         rset.close();
         stmt.close();

         out.println("</tr></table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         // Copy button
         out.println("<table cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button value=Copy width=100 " +
                     "style=\"WIDTH: 100px\" onClick='valForm(\"COPY\");'>&nbsp;");
         out.println("</td></tr></table>");
         // Some hidden values
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("<input type=hidden name=item value=\"\">");
         out.println("</form>");
         out.println("</td></tr></table>");

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


   private void writeImpLMarkFile(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      conn = (Connection) session.getValue("conn");
      writeImpLMarkFilePage(req, res);
   }

   private void writeImpLMarkFilePage(HttpServletRequest request,
                                      HttpServletResponse response)
      throws IOException
   {
      HttpSession session = request.getSession(true);
      Connection connection = (Connection) session.getValue("conn");
      Statement sqlStatement = null;
      ResultSet resultSet = null;
      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");
      PrintWriter out = response.getWriter();

      try
      {
         connection = (Connection) session.getValue("conn");

         out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                     "Transitional//EN\"");
         out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         // Validation script
         out.println("<script type=\"text/javascript\">");
         out.println("<!--");
         out.println("function valForm() {");
         out.println("	");
         out.println("	var rc = 1;");
         out.println("	if (rc) {");
         out.println("		if (confirm('Are you sure that you want to create the libarary markers?')) {");
         out.println("			document.forms[0].oper.value = 'UPLOAD';");
         out.println("			document.forms[0].submit();");
         out.println("		}");
         out.println("	}");
         out.println("	");
         out.println("	");
         out.println("}");
         out.println("//-->");
         out.println("</script>");
         out.println("<title>Import library markers from file</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=0>" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Library Markers - Import from file</b></center>" +
                     "</td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post enctype=\"multipart/form-data\" action=\"" +
                     getServletPath("adminLMark/impLMarkMultipart?") + "\">");
         out.println("<table border=0>");

         // The species
         sqlStatement = connection.createStatement();
         resultSet = sqlStatement.executeQuery("SELECT NAME, SID FROM V_SPECIES_1 ORDER BY NAME");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td>Species<br>");
         out.println("<select name=sid style=\"WIDTH: 200px\">");
         while (resultSet.next()) {
            out.println("<option value=\"" + resultSet.getString("SID") + "\">" +
                        resultSet.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td></tr>");

         // The file
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td>File<br>");
         out.println("<input type=file name=filename " +
                     "style=\"WIDTH: 350px\"></td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td></td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table border=0><tr>");
         out.println("<td>");
         out.println("<input type=button value=Send " +
                     "style=\"WIDTH: 100px\" onClick='valForm()'>");
         out.println("&nbsp;</td></tr></table>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("</form>");
         out.println("</body>");
         out.println("</html>");
      } catch (Exception e) {
         e.printStackTrace(System.err);
      } finally {
         try {
            if (resultSet != null) resultSet.close();
            if (sqlStatement != null) sqlStatement.close();
         } catch (SQLException ignored) {
         }
      }
   }
   private void writeSelectionScript(PrintWriter out) {
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
      out.println("	");
      out.println("	");
      out.println("	if (rc) {");
      out.println("		if (confirm('Are you sure that you want to copy the marker?')) {");
      out.println("			document.forms[0].oper.value = 'COPY'");
      out.println("			document.forms[0].submit();");
      out.println("		}");
      out.println("	}");
      out.println("	");
      out.println("	");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
   }

   private void writeNewScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function selChanged(item) {");
      out.println("	document.forms[0].oper.value = 'SEL_CHANGED';");
      out.println("	document.forms[0].submit();");
      out.println("	}");
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
      out.println("		if (confirm('Are you sure that you want to create the library marker?')) {");
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

   private void writeNewAlleleScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
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
      out.println("		if (confirm('Are you sure that you want to create the library allele?')) {");
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
         if (delete(req, res, conn))
            writeFrame(req, res);
      } else if (oper.equals("UPDATE")) {
         if (updateLMarker(req, res, conn))
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
         String oldQS = req.getQueryString(); //buildQS(req);
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         oldQS = removeQSParameterCid(oldQS);
         String lmid = req.getParameter("lmid");

         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);
         out.println("<title>Edit Library marker</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Library Markers - Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         stmt = conn.createStatement();
         String sql = "SELECT NAME, ALIAS, CID, P1, P2, POSITION, COMM " +
            "FROM V_L_MARKERS_1 WHERE LMID=" + lmid;
         rset = stmt.executeQuery(sql);
         rset.next();
         String name = rset.getString("NAME");
         String comm = rset.getString("COMM");
         String alias = rset.getString("ALIAS");
         String p1 = rset.getString("P1");
         String p2 = rset.getString("P2");
         String position = rset.getString("POSITION");
         String cid = rset.getString("CID");
         rset.close();
         stmt.close();

         // oldQS contains iid and mid!
         // Belowe we use rather cryptic names for the form data. We do this to prevent that
         // the data in the form won't collide with the data in the old query string
         out.println("<FORM action=\"" + getServletPath("adminLMark/edit?") +
                     oldQS + "\" method=\"post\" name=\"FORM1\">");
         out.println("<table border=0 cellpading=0 cellspacing=0><tr>");
         out.println("<td width=10 style=\"WIDTH: 15px\">");
         out.println("</td><td>");
         out.println("<table width=400 cellspacing=0 cellpading=0 border=0>");
         out.println("<tr>");
         // Chromosome
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM V_CHROMOSOMES_1 WHERE " +
                                  "SID=(SELECT SID FROM V_CHROMOSOMES_1 WHERE CID=" + cid + ")");
         out.println("<td width=200>Chromosome<br>");
         out.println("<select name=cid width=195 " +
                     "style=\"WIDTH: 195px; HEIGHT: 22px\">");
         while (rset.next()) {
            if (cid.equals(rset.getString("CID")))
               out.println("<option selected value=\"" + rset.getString("CID") + "\">" +
                           rset.getString("NAME") + "</option>");
            else
               out.println("<option value=\"" + rset.getString("CID") + "\">" +
                           rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td>");
         out.println("</tr>");
         out.println("<tr>");
         // Name
         out.println("<td width=200>Name<br>");
         out.println("<input type=text name=n maxlength=20 width=195 " +
                     "style=\"WIDTH: 195px; HEIGHT: 22px\" value=\"" +
                     replaceNull(name, "") + "\">");
         out.println("</td>");
         out.println("</tr>");
         out.println("<tr>");
         // Alias
         out.println("<td width=200>Alias<br>");
         out.println("<input type=text name=a maxlength=20 width=195 " +
                     "style=\"WIDTH: 195px; HEIGHT: 22px\" value=\"" +
                     replaceNull(alias, "") + "\">");
         out.println("</td>");
         out.println("</tr>");
         out.println("<tr>");
         // Primer 1
         out.println("<td width=200>Primer 1<br>");
         out.println("<input type=text name=p1 maxlength=40 width=195 " +
                     "style=\"WIDTH: 195px\" value=\"" + replaceNull(p1, "") + "\">");
         out.println("</td>");
         out.println("</tr>");
         // Primer 2
         out.println("<td width=200>Primer 2<br>");
         out.println("<input type=text name=p2 maxlength=40 width=195 " +
                     "style=\"WIDTH: 195px\" value=\"" + replaceNull(p2, "") + "\">");
         out.println("</td>");
         out.println("</tr>");
         out.println("<td width=200>Position<br>");
         out.println("<input type=text name=p maxlength=20 width=195 " +
                     "style=\"WIDTH: 195px; HEIGHT: 22px\" value=\"" +
                     replaceNull(position, "") + "\">");
         out.println("</td>");
         out.println("</tr>");
         out.println("<tr><td>Comment<br>");
         out.println("<textarea name=c  cols=40 rows=10>");
         out.print(replaceNull(comm, ""));
         out.println("</textarea>");
         out.println("</tr>");
         out.println("<tr><td>&nbsp;</td></tr>");
         // Alleles
         out.println("<tr><td>Alleles<br>");
         out.println("<table border=0 cellspacing=0 cellpading=0>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td width=200>Name</td>");
         out.println("<td width=250>Comment</td>");
         out.println("<td width=100>&nbsp</td></tr>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, COMM, LAID FROM V_L_ALLELES_1 WHERE " +
                                  "LMID=" + lmid + " ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME");
         boolean odd = true;
         while (rset.next() ) {
            if (odd) {
               out.println("<tr bgcolor=white>");
            } else {
               out.println("<tr bgcolor=lightgrey>");
            }
            out.println("<td>" + formatOutput(session, rset.getString("NAME"), 21) + "</td>");
            out.println("<td>" + formatOutput(session, rset.getString("COMM"), 30) + "</td>");
            out.println("<td><a href=\"" + getServletPath("adminLMark/editAllele?") +
                        "laid=" + rset.getString("LAID") + "&" + oldQS + "\">Edit</a></td>");
            out.println("</tr>");
            odd = !odd;
         }
         out.println("</table>");
         out.println("</td></tr>");

         // Control buttons
         out.println("<tr><td>&nbsp;</td></tr>");
         out.println("<tr><td>");
         out.println("<table cellspacing=0 cellpadding=0 border=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                    /* "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("adminLMark?") + oldQS + "\"'>&nbsp;");
                    */
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("adminLMark?&RETURNING=YES") + "\"'>&nbsp;");

         out.println("</td><td>");
         out.println("<input type=reset value=Reset width=100 " +
                     "style=\"WIDTH: 100px\">&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button name=CREATE_ALLELE value=\"Create allele\" width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("adminLMark/newAllele?") + oldQS + "\"''>&nbsp;");
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
   /***************************************************************************************
    * *************************************************************************************
    * The edit chromosome page
    */
   private void writeEditAllele(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";
      if (oper.equals("DELETE")) {
         if (deleteAllele(req, res, conn))
            writeEditPage(req, res);
      } else if (oper.equals("UPDATE")) {
         if(updateAllele(req, res, conn))
            writeEditAllelePage(req, res);
      } else
         writeEditAllelePage(req, res);
   }
   private void writeEditAllelePage(HttpServletRequest req, HttpServletResponse res)
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
         String laid = req.getParameter("laid");

         out.println("<html>");
         out.println("<head>");
         writeAlleleEditScript(out);
         out.println("<title>Edit Library Allele</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Libraray Markers - Edit - Alleles</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("adminLMark/editAllele?") +
                     oldQS + "\">");

         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, COMM FROM " +
                                  "V_L_ALLELES_1 WHERE LAID=" + laid);
         rset.next();
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");
         out.println("<table border=0 cellpading= cellspacing=0></tr>");
         out.println("<td width=300 style=\"WIDTH: 300px\">");
         out.println("Name<br>");
         out.println("<input type=text name=n maxlength=20 width=250 " +
                     "style=\"WIDTH: 250px\" " +
                     "value=\"" + replaceNull(rset.getString("NAME"), "") + "\"></td></tr>");
         out.println("<tr><td>Comment<br>");
         out.println("<textarea name=c cols=40 rows=10>" +
                     replaceNull(rset.getString("COMM"), "") +
                     "</textarea>");
         out.println("</td></tr>");
         out.println("<tr><td>&nbsp;</td></tr>");
         out.println("</table>");

         out.println("</td></tr>");
         out.println("<tr><td></td><td>");
         out.println("<table cellspacing=0 cellpading=0>");

         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("adminLMark/edit?") + oldQS + "\"'>&nbsp;");
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
         // Store some extra information
         out.println("<input type=\"hidden\" NAME=laid value=\"" + laid + "\">");
         out.println("<input type=\"hidden\" NAME=oper value=\"\">");
         out.println("</FORM>");
         out.println("</td></tr><table>");
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
    * Creates a new library marker.
    *
    * @param request a HttpServletRequest value.
    * @param response a HttpServletResponse value.
    * @param connection a Connection value.
    * @return True if object was creatd in the database.
    *         False if object not created.
    */
   private boolean create(HttpServletRequest request,
                          HttpServletResponse response,
                          Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         String name, alias, p1, p2, position, comm;
         int sid, cid;
         // The variables sid and cid should be part of the query string
         sid = Integer.parseInt(request.getParameter("sid"));
         cid = Integer.parseInt(request.getParameter("cid"));
         connection.setAutoCommit(false);
         name = request.getParameter("n");
         alias = request.getParameter("a");
         p1 = request.getParameter("p1");
         p2 = request.getParameter("p2");
         position = request.getParameter("p");
         comm = request.getParameter("c");
         DbLMarker dblm = new DbLMarker();
         dblm.CreateLMarker(connection, name, alias, comm, p1, p2, position, sid, cid);

         // Get the error message from the database object. If it is set an
         // error occured during the operation so an exception is thrown.
         errMessage = dblm.getErrorMessage();
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

      commitOrRollback(connection, request, response, "LibraryMarkers.New.Create",
                       errMessage, "adminLMark/new", isOk);
      return isOk;
   }
   

   /**
    * Creates a new allele in a library marker.
    *
    * @param request a HttpServletRequest value.
    * @param response a HttpServletResponse value.
    * @param connection a Connection value.
    * @return True if database object was created.
    *         False if object not created.
    */
   private boolean createAllele(HttpServletRequest request,
                                HttpServletResponse response,
                                Connection  connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         String name, comm;
         int lmid;
         connection.setAutoCommit(false);
         name = request.getParameter("n");
         comm = request.getParameter("c");
         lmid = Integer.parseInt(request.getParameter("lmid"));
         DbLMarker dblm = new DbLMarker();
         dblm.CreateLAllele(connection, name, comm, lmid);
         errMessage = dblm.getErrorMessage();
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
                       "LibraryMarkers.Edit.Allele.Create", errMessage,
                       "adminLMark/edit", isOk);
      return isOk;
   }


   /**
    * Imports library markers for a given species from a file.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if markers could be imported.
    *         False if markers could not be imported.
    * @exception IOException If error page could not be written.
    * @exception ServletException If error page could not be written.
    */
   private boolean createLMarkFile(HttpServletRequest request,
                                   HttpServletResponse response)
      throws IOException, ServletException
   {
      boolean isOk = true;
      String errMessage = null;
      Connection connection = null;
      
      try
      {
         HttpSession session = request.getSession(true);
         connection = (Connection) session.getValue("conn");
         connection.setAutoCommit(false);

         String upPath = getUpFilePath();
         MultipartRequest multiRequest =
            new MultipartRequest(request, upPath, 5 * 1024 * 1024);

         String speciesId = multiRequest.getParameter("sid");

         FileParser fileParser = null;
         Enumeration fileEnum = multiRequest.getFileNames();
         if (fileEnum.hasMoreElements())
         {
            DbLMarker dbLMarker = new DbLMarker();

            String givenFileName = (String) fileEnum.nextElement();
            String systemFileName = multiRequest.getFilesystemName(givenFileName);
            fileParser = new FileParser(upPath + "/" +  systemFileName); 
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.LMARKER,
                                                                        FileTypeDefinition.LIST));
            dbLMarker.CreateLMarkers(fileParser, connection,
                                     Integer.parseInt(speciesId)); 

            errMessage = dbLMarker.getErrorMessage();
            Assertion.assertMsg(errMessage == null ||
                             errMessage.trim().equals(""), errMessage);
         }
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
                       "LibraryMarkers.FileImport.Send", errMessage,
                       "adminLMark/impLMarkFile", isOk);
      return isOk;
   }


   /**
    * Deletes a library marker.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if object was deleted.
    *         False if object not deleted
    */
   private boolean delete(HttpServletRequest request,
                          HttpServletResponse response,
                          Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int lmid;
         connection.setAutoCommit(false);
         lmid = Integer.parseInt(request.getParameter("lmid"));
         DbLMarker dblm = new DbLMarker();
         dblm.DeleteLMarker(connection, lmid);
         errMessage = dblm.getErrorMessage();
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
                       "LibraryMarkers.Edit.Delete", errMessage,
                       "adminLMark/edit", isOk);
      return isOk;
   }

   
   /**
    * Deletes an allele from a library marker.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection to use.
    * @return True if allele was deleted.
    *         False if allele was not deleted.
    */
   private boolean deleteAllele(HttpServletRequest request,
                                HttpServletResponse response,
                                Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int laid;
         connection.setAutoCommit(false);
         laid = Integer.parseInt(request.getParameter("laid"));
         DbLMarker dblm = new DbLMarker();
         dblm.DeleteLAllele(connection, laid);
         errMessage = dblm.getErrorMessage();
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
                       "LibraryMarkers.Edit.Allele.Delete", errMessage,
                       "adminLMark/edit", isOk); 
      return isOk;
   }

   
   /**
    * Imports library markers from a sampling unit.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if markers were imported.
    *         False if import failed.
    */
   private boolean importSuMark(HttpServletRequest request,
                                HttpServletResponse response,
                                Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int mid;
         connection.setAutoCommit(false);
         // problem to parse ints if = null !!!
         mid = Integer.parseInt(request.getParameter("mid"));

         DbLMarker dblm = new DbLMarker();
         dblm.CopySUMarker(connection, mid);
         errMessage = dblm.getErrorMessage();
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
                       "LibraryMarkers.ImportSU.Copy", errMessage,
                       "adminLMark/impLMarkSU", isOk);
      return isOk;
   }

   
   /**
    * Updates a library marker.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if library marker was updated.
    *         False if update fails.
    */
   private boolean updateLMarker(HttpServletRequest request,
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
         String alias;
         String p1;
         String p2;
         String position;
         int cid;
         int lmid;
         String oldQS = request.getQueryString();
         name = request.getParameter("n");
         comm = request.getParameter("c");
         alias = request.getParameter("a");
         p1 = request.getParameter("p1");
         p2 = request.getParameter("p2");
         position = request.getParameter("p");
         lmid = Integer.parseInt(request.getParameter("lmid"));
         cid = Integer.parseInt(request.getParameter("cid"));
         DbLMarker dblm = new DbLMarker();
         dblm.UpdateLMarker(connection, lmid, name, alias, comm, p1, p2, position, cid);
         errMessage = dblm.getErrorMessage();
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
                       "LibraryMarkers.Edit.Update", errMessage,
                       "adminLMark/edit", isOk); 
      return isOk;
   }




   private boolean updateAllele(HttpServletRequest request,
                                HttpServletResponse response,
                                Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         String temp, name, comm;
         int laid;
         String oldQS = request.getQueryString();
         laid = Integer.parseInt(request.getParameter("laid"));
         name = request.getParameter("n");
         comm = request.getParameter("c");
         DbLMarker dblm = new DbLMarker();
         dblm.UpdateLAllele(connection, laid, name, comm);
         errMessage = dblm.getErrorMessage();
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
                       "LibraryMarkers.Edit.Allele.Update", errMessage,
                       "adminLMark/edit", isOk);
      return isOk;
   }

   private void writeEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to delete the library marker.\\n' + ");
      out.println("       'All the library alleles refering to this marker will also be deleted.')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the library marker?')) {");
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

   private void writeAlleleEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure youwant to delete the library allele?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the library allele?')) {");
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
