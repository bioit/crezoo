/*
  $Log$
  Revision 1.7  2005/02/23 13:31:26  heto
  Converted database classes to PostgreSQL

  Revision 1.6  2005/02/04 15:58:40  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.5  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.4  2003/04/25 12:14:45  heto
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


  Revision 1.19  2001/05/31 07:06:52  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.18  2001/05/30 13:45:07  frob
  Moved writeStatisticsPage to ArexisServlet.

  Revision 1.17  2001/05/30 13:35:36  frob
  Rewrote writeStatistics methods. Move most funktionality to ServletUtil.

  Revision 1.16  2001/05/23 12:32:32  roca
  fixed choice of chromosome for markerset membership
  changed order of links in adminProj. "Order by name" added it adm species & SU's

  Revision 1.15  2001/05/23 10:38:08  roca
  Modified htmlcode for proj/edit sampling units & species to work on linux

  Revision 1.14  2001/05/22 15:27:56  roca
  Fixed Uppercase comparison when reading genotypes from file
  New Pages for admin/su and admin/species

  Revision 1.13  2001/05/22 06:54:29  roca
  backfunktionality for administrator pages and privileges removed from roles (user mode)

  Revision 1.12  2001/05/21 12:05:36  frob
  Modified statistics section to decrease load time.

  Revision 1.11  2001/05/21 10:40:56  roca
  removed privileges from role view

  Revision 1.10  2001/05/08 06:28:52  frob
  Minor fix.

  Revision 1.9  2001/05/07 12:01:12  frob
  Changed all methods that used to call writeError method to write the error page. These
  methods noew calls the inherites method commitOrRollback which handles the errors.

  Revision 1.8  2001/05/03 14:20:56  frob
  Implemented local version of errorQueryString and changed writeError to use this method.

  Revision 1.7  2001/05/03 07:57:32  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.6  2001/05/02 11:19:58  frob
  Calls to removeOper and removePid modified to use the general removeQSParameter.
  The previously called methods are removed.

  Revision 1.5  2001/04/27 13:54:45  frob
  Updated HTML in method writeTop().

  Revision 1.4  2001/04/24 09:33:47  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.3  2001/04/24 06:31:20  frob
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

public class adminProj extends AdminArexisServlet
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
      } else if (extPath.equals("/statistics")) {
         writeStatistics(req, res);
      } else if (extPath.equals("/species")) {
         writeEditSpecies(req, res);
      } else if (extPath.equals("/su")) {
         writeEditSU(req, res);
      } else if (extPath.equals("/edit")) {
         writeEdit(req, res);
      } else if (extPath.equals("/new")) {
         writeNew(req, res);
      } else if (extPath.equals("/users")) {
         writeUser(req, res);
      } else if (extPath.equals("/roles")) {
         writeRole(req, res);
      } else if (extPath.equals("/editRole")) {
         writeEditRole(req, res);
      } else if (extPath.equals("/newRole")) {
         writeNewRole(req, res);
      } else if (extPath.equals("/impRole")) {
         writeImpRole(req, res);
      } else if (extPath.equals("/impRoleMultipart")) {
         createRoleFile(req, res);
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
         // Check if redirections is needed. 
         res = checkRedirectStatus(req,res);
         //req=getServletState(req,session);
         
         String topQS = buildQS(req);
         // we don't want the oper parameter anaywhere but in the "edit" and "new" page!
         topQS = removeQSParameterOper(topQS);
         
         String bottomQS = topQS.toString();

         out.println("<html>"
                     + "<HEAD>"
                     + " <TITLE>Administrate projects</TITLE>"
                     + "</HEAD>"
                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" //
                     + "<frame name=\"projtop\" "
                     + "src=\""+ getServletPath("adminProj/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"projmiddle\" "
                     + "src=\""+ getServletPath("adminProj/middle?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"projbottom\""
                     + "src=\"" +getServletPath("adminProj/bottom?") + bottomQS + "\" "
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
         old_sid = null, // Previous species id
         sid = null, // species id
         suid = null,
         id = null,
         name = null,
         pid = null,
         status = null,
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
         sid = "*"; //findSid(conn);
         sid_changed = true;
      }
      session.putValue("SID", sid);

      pid = req.getParameter("pid");
      suid = req.getParameter("suid");
      if (suid == null || suid.trim().equals("") || sid_changed) {
         suid = "*";
      }
      id = req.getParameter("id");
      if (id == null || id.trim().equals(""))
         id = "*";
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
         output.append(setIndecis(sid, old_sid, action, req, session));
      output.append("&sid=").append(sid);
      output.append("&pid=").append(pid);
      output.append("&suid=").append(suid);
      output.append("&id=").append(id);
      output.append("&status=").append(status);
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

   private String setIndecis(String sid, String old_sid, String action, HttpServletRequest req, HttpSession session) {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(sid, req, session);
      maxRows = getMaxRows(session);
      if (req.getParameter("STARTINDEX") != null &&
          old_sid != null && old_sid.equals(sid)) {
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

   private int countRows(String sid, HttpServletRequest req, HttpSession session) {
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer(512);
      try {
         sbSQL.append("SELECT count(distinct pid) " +
                      "FROM gdbadm.V_PROJECTS_3 WHERE 1=1 ");
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
         suid = null,
         id = null,
         name = null,
         status = null;
      StringBuffer filter = new StringBuffer(256);
      sid = req.getParameter("sid");
      suid = req.getParameter("suid");
      id = req.getParameter("id");
      name = req.getParameter("name");
      status = req.getParameter("status");

      if (id != null && !id.trim().equals("") && !id.equals("*"))
         filter.append(" and ID=" + id);
      if (sid != null && !sid.trim().equals("") && !sid.equals("*"))
         filter.append(" and SID=" + sid);
      if (suid != null && !suid.trim().equals("") && !suid.equals("*"))
         filter.append(" and SUID=" + suid);
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
      String sid, suid, status, name, id, orderby, oldQS, newQS, action;
      try {
         conn = (Connection) session.getValue("conn");

         sid = req.getParameter("sid");
         suid = req.getParameter("suid");
         status = req.getParameter("status");
         id = req.getParameter("id");
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
         if (sid == null || sid.trim().equals(""))
            sid = "*"; //findSid(conn);
         if (suid == null || suid.trim().equals(""))
            suid = "*";
         if (status == null || status.trim().equals(""))
            status = "*";
         if (id == null || id.trim().equals(""))
            id = "*";
         if (name == null || name.trim().equals(""))
            name = "";

         out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                     "Transitional//EN\"");
         out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");

         out.println("<html>");
         out.println("<head>\n" +
                     "  <link rel=\"stylesheet\" type=\"text/css\" href=\"" +
                     getURL("style/view.css") +"\">"); 
         out.println("  <base target=\"adminmainframe\">");
         out.println("  <title>Project</title>");
         out.println("</head>");

         out.println("\n<body bgcolor=\"#ffffd0\">");
         out.println("<form method=get action=\"" +
                     getServletPath("adminProj") +"\">");

         // Create main table, will contain 3 rows x 3 columns
         out.println("\n<! ====== Main table, start ====== -->");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0>");

         out.println("\n  <!-- ====== Main table, r1 ====== -->");
         out.println("  <tr>\n" +
                     "    <td width=14 rowspan=3></td>\n" +
                     "    <td width=736 colspan=2 height=15>\n" +
                     "      <center>\n" +
                     "        <b style=\"font-size: 15pt\">Projects</b>\n" +
                     "      </center>\n" +
                     "    </td>\n" +
                     "  </tr>");

         // Second row: c1 = joined with r1, c2+c3 = colored line
         out.println("\n  <!-- ====== Main table, r2 ====== -->");
         out.println("  <tr>\n" +
                     "    <td width=736 colspan=2 height=2 bgcolor=\"#008B8B\">&nbsp;</td>\n" +
                     "  </tr>");

         // Third row: c1= joined with r1, c2 = selection field table,
         // c3 = button table
         out.println("\n  <!-- ====== Main table, r3 ====== -->");
         out.println("  <tr>\n" +
                     "    <td width=517>");

         out.println("\n      <!-- ====== Selection field table, start ====== -->");
         out.println("      <table width=488 border=0>");
         out.println("\n        <!-- ====== Selection field table, r1 ====== -->");
         out.println("        <TR>");

         // Build first row in table with selection fields
         out.println("          <td>\n" +
                     "            <b>Species</b><br>\n" +
                     "            <select name=sid " +
                     "onChange='document.forms[0].submit()' " +
                     "style=\"HEIGHT: 22px; WIDTH: 126px\">");

         // Get all species from the database and add them to the selection field
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SID FROM gdbadm.V_SPECIES_1 " +
                                  "ORDER BY NAME");
         while (rset.next())
         {
            if (sid != null && sid.equalsIgnoreCase(rset.getString("SID")))
            {
               out.println("              <option selected value=\"" +
                           rset.getString("SID") + "\">" +
                           rset.getString("NAME")+ "</option>");
            }
            else
            {
               out.println("              <option value=\"" + rset.getString("SID") +
                           "\">" + rset.getString("NAME")+"</option>"); 
            }
         }
         if ("*".equals(sid) )
         {
            out.println("              <option selected value=\"*\">*</option>");
         }
         else 
         {
            out.println("              <option value=\"*\">*</option>");
         }
         
         rset.close();
         stmt.close();
         out.println("            </SELECT>\n" +
                     "          </td>");


         // Get all sampling units from the database and add them to the
         // selection filed
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         if ("*".equals(sid))
         {
            rset = stmt.executeQuery("SELECT NAME, SUID FROM " +
                                     "V_SAMPLING_UNITS_1 ORDER BY NAME");
         }
         else
         {
            rset = stmt.executeQuery("SELECT NAME, SUID FROM " +
                                     "V_SAMPLING_UNITS_1 WHERE SID=" + sid
                                     + " ORDER BY NAME"); 
         }
         
         out.println("          <td>\n" +
                     "            <b>Sampling unit</b><br>\n" + 
                     "            <select name=suid style=\"WIDTH: 100px\">");

         while (rset.next())
         {
            out.println("              <option " +
                        (rset.getString("SUID").equals(suid) ? " SELECTED " : " ") +
                        "value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("              <option " +
                     ("*".equals(suid) ? "SELECTED" : "") +
                     ">*</option>");
         out.println("            </select>");
         out.println("          </td>");

         // Get all users from the database and add them to the selection field
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT USR, ID FROM " +
                                  "V_USERS_1 ORDER BY USR");

         out.println("          <td>\n" +
                     "            <b>User</b><br>\n" +
                     "            <select name=id style=\"WIDTH: 100px\">");
         
         while (rset.next())
         {
            out.println("              <option " +
                        (rset.getString("ID").equals(id) ? " SELECTED " : " ") +
                        "value=\"" + rset.getString("ID") + "\">" +
                        rset.getString("USR") + "</option>");
         }
         out.println("              <option " +
                     ("*".equals(id) ? "SELECTED" : "") +
                     ">*</option>");
         out.println("            </select>");
         out.println("          </td>");

         // Get all statuses from the database and add them to the
         // selection field
         out.println("          <td>\n" +
                     "            <b>Status</b><br>\n" +
                     "            <select name=status style=\"WIDTH: 100px\">");
         
         if ("E".equals(status))
         {
            out.println("              <option selected value=\"E\">E</option>");
            out.println("              <option value=\"D\">D</option>");
            out.println("              <option>*</option>");
         }
         else if ("D".equals(status))
         {
            out.println("              <option value=\"E\">E</option>");
            out.println("              <option selected value=\"D\">D</option>");
            out.println("              <option>*</option>");
         }
         else
         {
            out.println("              <option value=\"E\">E</option>");
            out.println("              <option value=\"D\">D</option>");
            out.println("              <option selected>*</option>");
         }
         out.println("            </select>");
         out.println("          </td>");
         out.println("        </tr>");
         
         out.println("\n        <!-- ======= Selection field table, r2 ====== -->");
         out.println("        <tr>");

         // Create input field for project name
         out.println("          <td colspan=4 align=left>\n" +
                     "            <b>Name</b><br>\n" +
                     "              <input type=text name=name " +
                     "style=\"WIDTH: 100px\" value=\"" + name + "\">");
         out.println("          </td>");
         out.println("        </TR>");
         out.println("      </table>");
         out.println("      <!-- ====== Selection field table, end ====== -->\n");
         out.println("    </td>");

         out.println("    <td width=219>");
         out.println("\n      <!-- ====== Button table, start ====== -->");
         out.println("      <table border=0 cellpadding=0 cellspacing=0 " +
                     "width=135 align=right>");

         // First row in table contains one button and spans four columns
         out.println("\n        <!-- ====== Button table, r1 ====== -->");
         out.println("        <TR>\n" +
                     "          <td colspan=4>\n" +
                     "            <input type=button value=\"New Project\"" +
                     " onClick='parent.location.href=\"" +
                      getServletPath("adminProj/new?") + newQS + "\"' " +
                     " style=\"font-size: 9pt; HEIGHT: 24px; " +
                     "WIDTH: 133px\" name=\"button\">\n" + 
                     "          </td>\n" +
                     "        </TR>");

         // Second row contains two buttons, each span two columns
         out.println("\n        <!-- ====== Button table, r2 ====== -->");
         out.println("        <tr>\n" +
                     "          <td width=68 colspan=2>\n" +
                     "            <input id=COUNT name=COUNT " +
                     "type=submit value=\"Count\" style=\"font-size: 9pt; " +
                     "HEIGHT: 24px; WIDTH: 66px\">\n" +
                     "          </td>");
         out.println("          <td width=68 colspan=2>\n" +
                     "            <input id=DISPLAY name=DISPLAY type=submit value=\"Display\"" +
                     " style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">\n" +
                     "          </td>\n" +
                     "        </tr>");

         // Third row contains four buttons, one column each
         out.println("\n        <!-- ====== Button table, r3 ====== -->");
         out.println("        <TR>\n" +
                     "          <td width=34 colspan=1>\n" +
                     "            <input id=TOP name=TOP type=submit value=\"<<\"" +
                     "style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 31px\">\n" +
                     "          </td>");
         out.println("          <td width=34 colspan=1>\n" +
                     "            <input id=PREV name=PREV type=submit value=\"<\"" +
                     "style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 32px\">\n" +
                     "          </td>");
         out.println("          <td width=34 colspan=1>\n" +
                     "            <input id=NEXT name=NEXT type=submit value=\">\"" +
                     "style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 32px\">\n" +
                     "          </td>");
         out.println("          <td width=34 colspan=1>\n" +
                     "            <input id=END name=END type=submit value=\">>\"" +
                     "style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 31px\">\n" +
                     "          </td>");
         out.println("        </TR>");

         // Fourth row contains some hiden fields
         out.println("\n        <!-- ====== Button table, r4 ====== -->");
         out.println("        <TR>\n" +
                     "          <TD width=0 height=0>\n" +
                     "            <input type=\"hidden\" id=\"STARTINDEX\" " +
                     " name=\"STARTINDEX\" value=\"" + startIndex + "\">\n" +
                     "            <input type=\"hidden\" id=\"ORDERBY\" " +
                     " name=\"ORDERBY\" value=\"" + orderby + "\">\n" +
                     "            <input type=\"hidden\" id=\"oper\" " +
                     " name=\"oper\" value=\"\">\n" +
                     "          </TD>\n" +
                     "        </TR>");
         out.println("      </table>");
         out.println("      <!-- ====== End button table ====== -->\n");
         out.println("    </td>\n" +
                     "  </tr>\n" +
                     "</table>\n" +
                     "<!-- ====== End main table ====== -->\n" +
                     "</form>");

         out.println("</body></html>");

      }
      catch (Exception e)
      {
         out.println("<PRE>");
         e.printStackTrace(out);
         out.println("</PRE>");
      }
      finally
      {
         try
         {
            if (rset != null)
            {
               rset.close();
            }
            if (stmt != null)
            {
               stmt.close();
            }
         }
         catch (SQLException ignored)
         {
         }
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

         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                    // " height=20 width=710 style=\"margin-left:2px\">");
                    " height=20 width=840 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");

         /*
           out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 " +
           "height=20 width=710 style=\"margin-left:2px\">" +
           "<td width=5></td>");
         */
         // the menu choices
         // Name
         out.println("<td width=150><a href=\"" +
                     getServletPath("adminProj")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\">");
         if(choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown><b>Name</b></FONT></a></td>\n");
         else
            out.println("Name</a></td>\n");
         // Comment
         out.println("<td width=200><a href=\"" +
                     getServletPath("adminProj")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\">");
         if(choosen.equals("COMM"))
            out.println("<FONT color=saddlebrown><b>Comment</b></FONT></a></td>\n");
         else
            out.println("Comment</a></td>\n");
         out.println("<td width=50><a href=\"" +
                     getServletPath("adminProj") + "?ACTION=DISPLAY&" + newQS + "&ORDERBY=STATUS\">");
         if (choosen.equals("STATUS"))
            out.println("<FONT color=saddlebrown><b>Status</b></FONT></a></td>\n");
         else
            out.println("Status</a></td>\n");
         /*
           out.println("<td width=60>&nbsp;</td>");
           out.println("<td width=60>&nbsp;</td>");
           out.println("<td width=60>&nbsp;</td>");
           out.println("<td width=60>&nbsp;</td>");
           out.println("</table></table>");
           out.println("</body></html>");
         */
         out.println("<td width=72>&nbsp;</td>");
         out.println("<td width=72>&nbsp;</td>");
         out.println("<td width=72>&nbsp;</td>");
         out.println("<td width=72>&nbsp;</td>");
         out.println("<td width=72>&nbsp;</td>");
         out.println("<td width=72>&nbsp;</td>");
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
         String suid = null, action = null, orderby;
         String oldQS = req.getQueryString();
         oldQS = removeQSParameterPid(oldQS);

         action = req.getParameter("ACTION");
         suid = req.getParameter("suid");
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
         } else if (action.equalsIgnoreCase("PREV"))
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
         sbSQL.append("SELECT distinct NAME, PID, COMM, STATUS FROM gdbadm.V_PROJECTS_3 WHERE 1=1");
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);
         sbSQL.append(" ORDER BY ").append(orderby);
         rset = stmt.executeQuery(sbSQL.toString());

         out.println("<TABLE align=left border=0 cellPadding=0");
//         out.println("cellSpacing=0 width=712 style=\"margin-left:2px\">");
         out.println("cellSpacing=0 width=840 style=\"margin-left:2px\">");

         boolean odd = true;
         // First we spawn rows!

         int rowCount = 0;
         int startIndex = Integer.parseInt( req.getParameter("STARTINDEX"));
         if (startIndex > 1) {
            while ((rowCount++ < startIndex - 1) && rset.next())
               ;
         }
         rowCount = 0;
         int maxRows = 50; //Integer.parseInt((String) session.getValue("MaxRows"));
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
            out.println("<TD WIDTH=150>" + formatOutput(session, rset.getString("NAME"),15) +"</TD>");
            out.println("<TD WIDTH=200>" + formatOutput(session, rset.getString("COMM"),20)+"</TD>");
            out.println("<TD WIDTH=50>" + rset.getString("STATUS") + "</TD>");
/*
            out.println("<TD WIDTH=60><A HREF=\"" +getServletPath("adminProj/roles?pid=")
                        + rset.getString("PID")
                        + "&" + oldQS + "\" target=\"adminmainframe\">Roles</A></TD>");
            out.println("<TD WIDTH=50><A HREF=\"" +getServletPath("adminProj/users?pid=")
                        + rset.getString("PID")
                        + "&" + oldQS + "\" target=\"adminmainframe\">Users</A></TD>");
            out.println("<TD WIDTH=80><A HREF=\"" +getServletPath("adminProj/statistics?pid=")
                        + rset.getString("PID")
                        + "&" + oldQS + "\" target=\"adminmainframe\">Statistics</A></TD>");
            out.println("<TD WIDTH=50><A HREF=\"" +getServletPath("adminProj/edit?pid=")
                        + rset.getString("PID")
                        + "&" + oldQS + "\" target=\"adminmainframe\">Edit</A></TD></TR>");
*/

            out.println("<TD WIDTH=60><A HREF=\"" +getServletPath("adminProj/roles?pid=")
                        + rset.getString("PID")
                        + "&" + oldQS + "\" target=\"adminmainframe\">Roles</A></TD>");
            out.println("<TD WIDTH=60><A HREF=\"" +getServletPath("adminProj/users?pid=")
                        + rset.getString("PID")
                        + "&" + oldQS + "\" target=\"adminmainframe\">Users</A></TD>");
             out.println("<TD WIDTH=72><A HREF=\"" +getServletPath("adminProj/species?pid=")
                        + rset.getString("PID")
                        + "&" + oldQS + "\" target=\"adminmainframe\">Species</A></TD>");
//96
            out.println("<TD WIDTH=106><A HREF=\"" +getServletPath("adminProj/su?pid=")
                        + rset.getString("PID")
                        + "&" + oldQS + "\" target=\"adminmainframe\">Sampling Units</A></TD>");
//72
            out.println("<TD WIDTH=52><A HREF=\"" +getServletPath("adminProj/edit?pid=")
                        + rset.getString("PID")
                        + "&" + oldQS + "\" target=\"adminmainframe\">Edit</A></TD>");
//72
     out.println("<TD WIDTH=82><A HREF=\"" +getServletPath("adminProj/statistics?pid=")
                        + rset.getString("PID")
                        + "&" + oldQS + "\" target=\"adminmainframe\">Statistics</A></TD></TR>");
      
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



   private void writeStatistics(HttpServletRequest request,
                                HttpServletResponse response) 
      throws ServletException, IOException
   {
      
      HttpSession session = request.getSession(true);
      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");
      PrintWriter out = response.getWriter();
      Statement stmt = null;
      ResultSet rset = null;
      try
      {
         String projectId = request.getParameter("pid");
         Connection connection = (Connection) session.getValue("conn");

         // Get general project data
         stmt = connection.createStatement();
         rset = stmt.executeQuery("SELECT NAME, COMM, STATUS " +
                                  "FROM gdbadm.V_PROJECTS_1 WHERE " +
                                  "PID=" + projectId);
         
         // Check that project found, otherwise bail out
         Assertion.assertMsg(rset.next(), "Project not found: " + projectId);

         // Copy general data to vector
         String[][] generalData = new String[3][2];
         generalData[0][0] = "Name";
         generalData[0][1] = rset.getString("NAME");
         generalData[1][0] = "Comment";
         generalData[1][1] = rset.getString("COMM");
         generalData[2][0] = "Status";
         generalData[2][1] = rset.getString("STATUS");
         
         // Define vector for statisticData
         String[][] statisticData = new String[10][2];
         String counter;
         
         // Query for all roles in the project and add the result to
         // vector. If no roles found, add a blank string.
         rset = stmt.executeQuery("SELECT COUNT(*) FROM " +
                                  "GdbAdm.V_Roles_1 where pid=" + projectId);
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[0][0] = "Roles";
         statisticData[0][1] = counter;
         
         // Users
         rset = stmt.executeQuery("SELECT COUNT(*) FROM " +
                                  "GdbAdm.V_USERS_2 where pid=" + projectId);
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[1][0] = "Users";
         statisticData[1][1] = counter;
         
         // Species
         rset = stmt.executeQuery("SELECT COUNT(*) FROM " +
                                  "GdbAdm.V_Species_2 where pid=" + projectId);
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[2][0] = "Species";
         statisticData[2][1] = counter;

         // Sampling units
         rset = stmt.executeQuery("SELECT COUNT(*) FROM " +
                                  "GdbAdm.V_Sampling_units_2 where pid=" + projectId);
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[3][0] = "Sampling units";
         statisticData[3][1] = counter;

         // Individuals
         rset = stmt.executeQuery("SELECT sum(INDS) "
                                  + "FROM gdbadm.V_SAMPLING_UNITS_3 WHERE PID="
                                  + projectId );
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[4][0] = "Individuals";
         statisticData[4][1] = counter;

         // Samples
         rset = stmt.executeQuery("SELECT count(*) "
                                  + "FROM gdbadm.V_SAMPLES_2 s, V_SAMPLING_UNITS_2 su"+
                                  " WHERE s.SUID=su.SUID AND su.PID=" + projectId + " ");
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[5][0] = "Samples";
         statisticData[5][1] = counter;         

         // Variables
         rset = stmt.executeQuery("SELECT count(*) "
                                  + "FROM gdbadm.V_VARIABLES_3 WHERE PID="
                                  + projectId + " "); 
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[6][0] = "Variables";
         statisticData[6][1] = counter;

         // Phenotypes
         rset = stmt.executeQuery("SELECT COUNT(*) "
                                  + "FROM gdbadm.V_VARIABLES_1 V, " +
                                  " PHENOTYPES P WHERE V.SUID IN " +
                                  "(SELECT SUID FROM R_PRJ_SU WHERE PID=" +
                                  projectId + ") AND V.VID = P.VID");
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[7][0] = "Phenotypes";
         statisticData[7][1] = counter;

         // Markers
         rset = stmt.executeQuery("SELECT count(*) "
                                  + "FROM gdbadm.V_MARKERS_1 m, " +
                                  "gdbadm.V_SAMPLING_UNITS_2 s "+
                                  "WHERE m.SUID=s.SUID AND s.PID=" +
                                  projectId + " "); 
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[8][0] = "Markers";
         statisticData[8][1] = counter;

         // Genotypes
         rset = stmt.executeQuery("SELECT COUNT(*) " +
                                  "FROM gdbadm.V_MARKERS_1 M, " +
                                  "GENOTYPES G WHERE M.SUID IN " +
                                  "(SELECT SUID FROM R_PRJ_SU WHERE PID="  +
                                  projectId + ") AND M.MID = G.MID");
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[9][0] = "Genotypes";
         statisticData[9][1] = counter;
         
         writeStatisticsPage(out, generalData, statisticData,
                             "history.go(-1)");  
      }
      catch (Exception e)
      {
         out.println("<PRE>");
         e.printStackTrace(out);
         out.println("</PRE>");
      }
      finally
      {
         try
         {
            if (rset != null)
            {
               rset.close();
            }
            if (stmt != null)
            {
               stmt.close();
            }
            
         }
         catch (SQLException ignored)
         {
         }
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
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String sid = null, newQS, pid, oper, item;
      try {
         conn = (Connection) session.getValue("conn");
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
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Projects - New</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=get action=\"" +
                     getServletPath("adminProj/new?") + newQS + "\">");
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
                     getServletPath("adminProj?&RETURNING=YES")  + "\"'>");
                     //getServletPath("adminProj?") + newQS + "\"'>");
         out.println("&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Create width=100 " +
                     "style=\"WIDTH: 100px\" onClick='valForm()'>");
         out.println("&nbsp;</td></tr></table>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=item value=\"\">");
         out.println("<input type=hidden name=RETURNING value=YES>");

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

   /***************************************************************************************
    * *************************************************************************************
    * The new role page
    */
   private void writeNewRole(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createRole(req, res, conn)) {
            writeRole(req, res);
         } else {
            ; // We have already displayed an error message!
         }
      } else {
         writeNewRolePage(req, res);
      }
   }
   private void writeNewRolePage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String newQS, pid;
      try {
         conn = (Connection) session.getValue("conn");
         pid = req.getParameter("pid");
         newQS = removeQSParameterOper(req.getQueryString());

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeNewRoleScript(out);
         out.println("<title>New Project</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Projects - Roles - New</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" +
                     getServletPath("adminProj/newRole?") + newQS + "\">");
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
                     getServletPath("adminProj/roles?") + newQS + "\"'>");
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
   private void writeImpRole(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      conn = (Connection) session.getValue("conn");
      writeImpRolePage(req, res);
   }

   /**
    * This method build the page used for importing privileges to a role.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @exception IOException If no writer can be retrieved from the
    *            response object.
    */
   private void writeImpRolePage(HttpServletRequest request,
                                 HttpServletResponse response)
      throws IOException
   {
      HttpSession session = request.getSession(true);
      Connection connection = (Connection) session.getValue("conn");

      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");
      PrintWriter out = response.getWriter();

      try
      {
         connection = (Connection) session.getValue("conn");

         String newQS = removeQSParameterOper(request.getQueryString());

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
         out.println("		if (confirm('Are you sure that you want to create the role?')) {");
         out.println("			document.forms[0].oper.value = 'UPLOAD';");
         out.println("			document.forms[0].submit();");
         out.println("		}");
         out.println("	}");
         out.println("	");
         out.println("	");
         out.println("}");
         out.println("//-->");
         out.println("</script>");
         out.println("<title>New Role</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=846 border=0>" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Projects - Roles - File import</b></center>" +
                     "</td></tr>" +
                     "<tr><td width=736 colspan=2 height=2 bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post enctype=\"multipart/form-data\" action=\"" +
                     getServletPath("adminProj/impRoleMultipart?") + newQS
                     + "\">");
         
         out.println("<table border=0>");
         out.println("<tr><td width=10>&nbsp;</td><td>");
         out.println("Name<br>");
         out.println("<input name=n maxlength=20 " +
                     "style=\"WIDTH: 200px; HEIGHT: 22px\">");
         out.println("</td></tr>");
         out.println("<tr><td></td>");
         out.println("<td>Comment<br>");
         out.println("<textarea rows=10 cols=40 name=c>");
         out.println("</textarea>");
         out.println("</td></tr>");

         // File
         out.println("<tr><td></td>");
         out.println("<td>File<br>");
         out.println("<input type=file name=filename " +
                     "style=\"WIDTH: 350px\">");
         
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td></td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table border=0><tr>");
         out.println("<td>");
         out.println("<input type=button value=Back " +
                     "style=\"WIDTH: 100px\" onClick='document.location.href=\"" +
                     getServletPath("adminProj/roles?") + newQS + "\"'>");
         out.println("&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Send " + 
                     "style=\"WIDTH: 100px\" onClick='valForm()'>");
         out.println("&nbsp;</td></tr></table>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("</form>");
         out.println("</body>");
         out.println("</html>");
      }
      catch (Exception e)
      {
         e.printStackTrace(System.err);
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
      out.println("		if (confirm('Are you sure that you want to create the project?')) {");
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
   private void writeNewRoleScript(PrintWriter out) {
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
      out.println("		if (confirm('Are you sure that you want to create the role?')) {");
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
         if (update(req, res, conn))
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
         
         String pid = req.getParameter("pid");

         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);
         out.println("<title>Edit Projects</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Projects - Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         stmt = conn.createStatement();
         String sql = "SELECT NAME, COMM, STATUS " +
            "FROM gdbadm.V_PROJECTS_1 WHERE " +
            "PID=" + pid;
         rset = stmt.executeQuery(sql);
         rset.next();
         String name = rset.getString("NAME");
         String comm = rset.getString("COMM");
         String status = rset.getString("STATUS");
         if (comm == null) comm = "";
         rset.close();
         stmt.close();
         // oldQS contains iid and mid!
         // Belowe we use rather cryptic names for the form data. We do this to prevent that
         // the data in the form won't collide with the data in the old query string
         out.println("<FORM action=\"" + getServletPath("adminProj/edit?") +
                     oldQS + "\" method=\"post\" name=\"FORM1\">");
         out.println("<table border=0 cellpading=0 cellspacing=0><tr>");
         out.println("<td width=10 style=\"WIDTH: 15px\">");
         out.println("</td><td>");
         out.println("<table width=400 cellspacing=0 cellpading=0 border=0>");
         out.println("<tr>");
         out.println("<td width=200>Name<br>");
         out.println("<input type=text name=n maxlength=20 width=195 " +
                     "style=\"WIDTH: 195px; HEIGHT: 22px\" value=\"" +
                     formatOutput(session, name, 20) + "\">");
         out.println("</td>");

         out.println("<td width=200>Status<br>");
         out.println("<select name=s width=100 style=\"WIDTH: 100px\">");
         if ("E".equals(status)) {
            out.println("<option selected value=\"E\">E</option>");
            out.println("<option value=\"D\">D</option>");
         } else {
            out.println("<option value=\"E\">E</option>");
            out.println("<option selected value=\"D\">D</option>");
         }
         out.println("</select>");
         out.println("</td></tr>");
         rset.close();
         stmt.close();
         out.println("<tr><td colspan=2>Comment<br>");
         out.println("<textarea name=c  cols=40 rows=10>");
         out.print(formatOutput(session, comm, 256));
         out.println("</textarea>");
         out.println("</tr>");
/*
         // Find available species
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID, NAME FROM GdbAdm.V_SPECIES_1 " +
                                  "MINUS SELECT SID, SNAME FROM GdbAdm.V_PROJECTS_2 " +
                                  "WHERE PID=" + pid );
         out.println("<tr><td>Add species<br>");
         out.println("<select name=add_sp width=100 style=\"WIDTH: 100px\">");
         out.println("<option selected value=\"\">(Nothing)</option>");
         while (rset.next() )
            out.println("<option value=\"" + rset.getString("SID") + "\">" +
                        rset.getString("NAME") + "</option>");
         out.println("</select>");
         out.println("</td>");
         rset.close();
         stmt.close();

         // Find included species
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID, SNAME FROM GdbAdm.V_PROJECTS_2 " +
                                  "WHERE PID=" + pid );
         out.println("<td>Remove species<br>");
         out.println("<select name=rem_sp width=100 style=\"WIDTH: 100px\">");
         out.println("<option selected value=\"\">(Nothing)</option>");
         while (rset.next() && rset.getString("SID") != null )
            out.println("<option value=\"" + rset.getString("SID") + "\">" +
                        rset.getString("SNAME") + "</option>");
         out.println("</select>");
         out.println("</td></tr>");

         // Find available sampling units
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM GdbAdm.V_Sampling_Units_1 " +
                                  "WHERE SID IN (SELECT SID FROM V_PROJECTS_2 WHERE PID="+ pid + ") " +
                                  "AND SUID NOT IN (SELECT SUID FROM V_SAMPLING_UNITS_2 WHERE PID=" + pid + ")");
         out.println("<tr><td>Add Sampling u.<br>");
         out.println("<select name=add_su width=100 style=\"WIDTH: 100px\">");
         out.println("<option selected value=\"\">(Nothing)</option>");
         while (rset.next() )
            out.println("<option value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</option>");
         out.println("</select>");
         out.println("</td>");
         rset.close();
         stmt.close();

         // Find included sampling units
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID, SUID, NAME FROM GdbAdm.V_Sampling_Units_2 " +
                                  "WHERE PID=" + pid );
         out.println("<td>Remove Sampling u.<br>");
         out.println("<select name=rem_su width=100 style=\"WIDTH: 100px\">");
         out.println("<option selected value=\"\">(Nothing)</option>");
         while (rset.next() && rset.getString("SID") != null )
            out.println("<option value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</option>");
         out.println("</select>");
         out.println("</td></tr>");
*/
         out.println("<tr><td  colspan=2 nowrap align=center>");
         out.println("&nbsp;</td></tr>");
         out.println("<tr><td  colspan=2 nowrap align=center>");
         out.println("<table cellspacing=0 cellpadding=0 border=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                  //   getServletPath("adminProj?") + oldQS + "\"'>&nbsp;");
                      getServletPath("adminProj?&RETURNING=YES")  + "\"'>&nbsp;");

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

/****************************************************************************************
    * The edit su page
    */

    private void writeEditSU(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      writeEditSUScript(out);
      Connection conn =  (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.

       String oper= req.getParameter("oper");
        if (oper == null)
        {
          oper = "-1";
        }
       String pid = req.getParameter("pid");

       if (oper.equalsIgnoreCase("ADD_SU") || oper.equalsIgnoreCase("REM_SU"))
       {
         try {
            boolean ok = true;
            conn.setAutoCommit(false);
            conn.rollback();
            String[] samplingUnits = null;
            if (oper.equalsIgnoreCase("ADD_SU"))
            {   // Add su to project
                samplingUnits = req.getParameterValues("avail_su");

               if (samplingUnits != null)
               {
                  stmt = conn.createStatement();
                  for (int i = 0; i < samplingUnits.length && ok; i++)
                  {
                     String sql = "INSERT INTO gdbadm.R_PRJ_SU(PID,SUID) "
                        + "VALUES(" +pid +", "+ samplingUnits[i]+" "+")";
                     try
                     {
                        stmt.executeUpdate(sql);
                     }
                     catch (SQLException sqle)
                     {
                        // Catch any exception that might be thrown due to
                        // that this species exists in the project

                        if (sqle.getMessage().indexOf("SYSADM.SYS_C008953") >= 0)
                           ;
                        else
                        {
                           throw sqle;
                        }
                     }//end catch
                   }// end for species.lenght
                  }// end if species = null
               }// end if oper = ADD

               else if (oper.equalsIgnoreCase("REM_SU"))
               {
                  // Remove su from project
                  samplingUnits = req.getParameterValues("incl_su");
                  if (samplingUnits != null)
                  {
                    stmt = conn.createStatement();
                    for (int i = 0; i < samplingUnits.length && ok; i++)
                    {
                       String sql = "DELETE FROM gdbadm.R_PRJ_SU WHERE "
                        + "SUID=" + samplingUnits[i] + " AND PID=" + pid;
                       int rows = stmt.executeUpdate(sql);
                       if (rows != 1) ok = false;
                    }
                  }
              }//end else if

            if (ok)
               conn.commit();
            else
               conn.rollback();

           } // end catch
           catch (Exception e) {
            try {
               conn.rollback();
               out.println("<pre>Error:\nUnhandled database exception!\n"
                           + e.getMessage()
                           +"\nEnd error.</pre>");
               e.printStackTrace(System.err);
            } catch (SQLException ignored) {
            }
            finally {
               try {
                  if (stmt != null) stmt.close();
               } catch (SQLException ignored) {}
            }

         }
      }

       try {
         String oldQS = buildQS(req);
         oldQS = removeQSParameterOper(oldQS);

         out.println("<html>");
         out.println("<head>");
         out.println("<title>Edit Projects</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");

      // get project name
         stmt = conn.createStatement();
         String sql = "SELECT NAME " +
            "FROM gdbadm.V_PROJECTS_1 WHERE " +
            "PID=" + pid;
         rset = stmt.executeQuery(sql);
         rset.next();
         String name = rset.getString("NAME");
         rset.close();
         stmt.close();

         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Edit Sampling Units for "
                     + name +"</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");


      out.println("<form name=\"form1\" action=\""
      +getServletPath("adminProj/su")+"\" method=\"post\">");
         out.println("<table border=0 cellpading=0 cellspacing=0><tr>");
         out.println("<td width=10 style=\"WIDTH: 15px\">");
         out.println("</td><td>");
         out.println("<table width=400 cellspacing=0 cellpading=0 border=0>");


         // Find available sampling Units
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM GdbAdm.V_Sampling_Units_1 " +
                                  "WHERE SID IN (SELECT SID FROM V_PROJECTS_2 WHERE PID="+ pid + ") " +
                                  "AND SUID NOT IN (SELECT SUID FROM V_SAMPLING_UNITS_2 WHERE PID=" + pid + ") ORDER BY NAME");

//         out.println("<table><tr><td valign=middle align=right>");
         out.println("<tr><td>Available Sampling Units<br>");

         out.println("<select name=\"avail_su\" width=200px multiple size=15 "
                     + "style=\"WIDTH: 200px\">");


         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("SUID") + "\">"
                        + rset.getString("NAME"));
         }
         out.println("</select>");
         rset.close();
         stmt.close();

         // buttons
         out.println("</td><td valign=middle align=middle>");
         out.println("<input type=\"button\" name=\"add_su\" value=\">\" onClick='addSU()'>");
         out.println("<br>");
         out.println("<input type=\"button\" name=\"rem_su\" value=\"<\" onClick='remSU()'>");
         out.println("</td>");

         // Find included sampling units
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID, SUID, NAME FROM GdbAdm.V_Sampling_Units_2 " +
                                  "WHERE PID=" + pid + " ORDER BY NAME" );

//         out.println("<td valign=middle align=left>");
         out.println("<td>Included Sampling Units<br>");


         out.println("<select name=\"incl_su\" width=200px multiple size=15 "
                     + "style=\"WIDTH: 200px\">");

        while (rset.next() && rset.getString("SID") != null )
            out.println("<option value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</option>");
         out.println("</select>");

         rset.close();
         stmt.close();

      out.println("</td></tr>");
         out.println("<tr><td  colspan=2 nowrap align=center>");
         out.println("&nbsp;</td></tr>");
         out.println("<tr><td  colspan=2 nowrap align=center>");
//         out.println("<table cellspacing=0 cellpadding=0 border=0>");
         out.println("<tr>");
         out.println("<td align=left>");
         out.println("<input type=button value=Back  width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                      getServletPath("adminProj?&RETURNING=YES")  + "\"'>&nbsp;");

         out.println("</td></tr>");
         out.println("</table>");
         // Store some extra information needed by doPost()
         out.println("<input type=\"hidden\" NAME=oper value=\"\">");
         out.println("<input type=\"hidden\" NAME=RETURNING value=YES>");
         out.println("<input type=\"hidden\" NAME=pid value="+pid+">");


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
    * The edit species page
    */

    private void writeEditSpecies(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      writeEditSpeciesScript(out);
      Connection conn =  (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.

       String oper= req.getParameter("oper");
        if (oper == null)
        {
          oper = "-1";
        }
       String pid = req.getParameter("pid");

       if (oper.equalsIgnoreCase("ADD_SPECIES") || oper.equalsIgnoreCase("REM_SPECIES"))
       {
         try {
            boolean ok = true;
            conn.setAutoCommit(false);
            conn.rollback();
            String[] species = null;
            System.err.println("oper3="+oper);
            if (oper.equalsIgnoreCase("ADD_SPECIES"))
            {   // Add species to project
                species = req.getParameterValues("avail_species");
               System.err.println("avail_sp="+species[0]);

               if (species != null)
               {
                  stmt = conn.createStatement();
                  for (int i = 0; i < species.length && ok; i++)
                  {
                     String sql = "INSERT INTO gdbadm.R_PRJ_SPC(PID,SID) "
                        + "VALUES(" +pid +", "+ species[i]+" "+")";
                     try
                     {
                        stmt.executeUpdate(sql);
                     }
                     catch (SQLException sqle)
                     {
                        // Catch any exception that might be thrown due to
                        // that this species exists in the project

                        if (sqle.getMessage().indexOf("SYSADM.SYS_C008953") >= 0)
                           ;
                        else
                        {
                           throw sqle;
                        }
                     }//end catch
                   }// end for species.lenght
                  }// end if species = null
               }// end if oper = ADD

               else if (oper.equalsIgnoreCase("REM_SPECIES"))
               {
                  // Remove species from project
                  species = req.getParameterValues("incl_species");

                  if (species != null)
                  {
                    stmt = conn.createStatement();
                    for (int i = 0; i < species.length && ok; i++)
                    {
                       String sql = "DELETE FROM gdbadm.R_PRJ_SPC WHERE "
                        + "SID=" + species[i] + " AND PID=" + pid;
                       int rows = stmt.executeUpdate(sql);
                       if (rows != 1) ok = false;
                    }
                  }
              }//end else if

            if (ok)
               conn.commit();
            else
               conn.rollback();

           } // end cath
           catch (Exception e) {
            try {
               conn.rollback();
               out.println("<pre>Error:\nUnhandled database exception!\n"
                           + e.getMessage()
                           +"\nEnd error.</pre>");
               e.printStackTrace(System.err);
            } catch (SQLException ignored) {
            }
            finally {
               try {
                  if (stmt != null) stmt.close();
               } catch (SQLException ignored) {}
            }

         }

      }

       try {
         String oldQS = buildQS(req);
         oldQS = removeQSParameterOper(oldQS);
         out.println("<html>");
         out.println("<head>");
         out.println("<title>Edit Projects</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");

      // get project name
         stmt = conn.createStatement();
         String sql = "SELECT NAME " +
            "FROM gdbadm.V_PROJECTS_1 WHERE " +
            "PID=" + pid;
         rset = stmt.executeQuery(sql);
         rset.next();
         String name = rset.getString("NAME");
         rset.close();
         stmt.close();

         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Edit Species for "
                     + name +"</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

      out.println("<form name=\"form1\" action=\""
      +getServletPath("adminProj/species")+"\" method=\"post\">");
         out.println("<table border=0 cellpading=0 cellspacing=0><tr>");
         out.println("<td width=10 style=\"WIDTH: 15px\">");
         out.println("</td><td>");
         out.println("<table width=400 cellspacing=0 cellpading=0 border=0>");

         // Find available species
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID, NAME FROM GdbAdm.V_SPECIES_1 " +
                 "where sid not in " +
                    "(SELECT SID FROM GdbAdm.V_PROJECTS_2 " +
                          "WHERE PID=" + pid + ") " +
                 "ORDER BY NAME");
                          

        // out.println("<table><tr><td valign=middle align=right>");
         out.println("<tr><td>Available species<br>");

         out.println("<select name=\"avail_species\" width=200px multiple size=15 "
                     + "style=\"WIDTH: 200px\">");


         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("SID") + "\">"
                        + rset.getString("NAME"));
         }
         out.println("</select>");
         rset.close();
         stmt.close();

         // buttons
         out.println("</td><td valign=middle align=middle>");
         out.println("<input type=\"button\" name=\"add_sp\" value=\">\" onClick='addSpecies()'>");
         out.println("<br>");
         out.println("<input type=\"button\" name=\"rem_sp\" value=\"<\" onClick='remSpecies()'>");
         out.println("</td>");

         // Find included species
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID, SNAME FROM GdbAdm.V_PROJECTS_2 " +
                                  "WHERE PID=" + pid + " ORDER BY SNAME" );

//         out.println("<td valign=middle align=left>");
         out.println("<td>Included species<br>");


         out.println("<select name=\"incl_species\" width=200px multiple size=15 "
                     + "style=\"WIDTH: 200px\">");

        while (rset.next() && rset.getString("SID") != null )
            out.println("<option value=\"" + rset.getString("SID") + "\">" +
                        rset.getString("SNAME") + "</option>");
         out.println("</select>");

         rset.close();
         stmt.close();

      out.println("</td></tr>");
         out.println("<tr><td  colspan=2 nowrap align=center>");
         out.println("&nbsp;</td></tr>");
         out.println("<tr><td  colspan=2 nowrap align=center>");
//         out.println("<table cellspacing=0 cellpadding=0 border=0>");
         out.println("<tr>");
         out.println("<td align=left>");
         out.println("<input type=button value=Back  width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                      getServletPath("adminProj?&RETURNING=YES")  + "\"'>&nbsp;");

         out.println("</td></tr>");
         out.println("</table>");
         // Store some extra information needed by doPost()
         out.println("<input type=\"hidden\" NAME=oper value=\"\">");
         out.println("<input type=\"hidden\" NAME=RETURNING value=YES>");
         out.println("<input type=\"hidden\" NAME=pid value="+pid+">");


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
    * The edit role page
    */
   private void writeEditRole(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";
      if (oper.equals("DELETE")) {
         if (deleteRole(req, res, conn))
            writeRole(req, res);
      } else if (oper.equals("UPDATE")) {
         if (updateRole(req, res, conn))
            writeEditRolePage(req, res);
      } else
         writeEditRolePage(req, res);
   }
   private void writeEditRolePage(HttpServletRequest req, HttpServletResponse res)
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
         String rid = req.getParameter("rid");

         out.println("<html>");
         out.println("<head>");
         writeRoleEditScript(out);
         out.println("<title>Edit Roles</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Projects - Roles - Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("adminProj/editRole?") +
                     oldQS + "\">");

         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, COMM FROM " +
                                  "V_ROLES_1 WHERE RID=" + rid);
         rset.next();
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");
         out.println("<table border=0 cellpading= cellspacing=0></tr>");
         out.println("<td width=300 style=\"WIDTH: 300px\">");
         out.println("Name<br>");
         out.println("<input type=text name=n maxlength=20 width=250 " +
                     "style=\"WIDTH: 250px\" " +
                     "value=\"" + formatOutput(session, rset.getString("NAME"), 20) + "\"></td></tr>");
         out.println("<tr><td>Comment<br>");
         out.println("<textarea name=c cols=40 rows=10>");
         out.println(formatOutput(session, rset.getString("COMM"), 256));
         out.println("</textarea>");
         out.println("</td></tr>");
         out.println("<tr><td>&nbsp;</td></tr>");
         out.println("</table>");

         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT PRID, NAME, COMM, INCL FROM " +
                                  "V_ROLE_PRIV_1 WHERE RID=" + rid + " ORDER BY PRID");
         out.println("<table cellpading=0 cellspacing=0>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td width=200 style=\"WIDTH: 200px\">Privilege</td>");
         out.println("<td width=300 style=\"WIDTH: 300px\">Comment</td>");
         out.println("<td width=100 style=\"WIDTH: 100px\">Included</td>");
         out.println("</tr>");
         boolean odd = true;
         while (rset.next() ) {
            if (odd)
               out.println("<tr bgcolor=white>");
            else
               out.println("<tr bgcolor=lightgrey>");
            out.println("<td>" + formatOutput(session, rset.getString("NAME"), 13) + "</td>");
            out.println("<td>" + formatOutput(session, rset.getString("COMM"), 256) + "</td>");
            out.println("<td>");
            out.println("<input type=checkbox name=\"priv" + rset.getString("PRID") + "\"" +
                        (rset.getString("INCL").equals("1") ? " checked " : "") + "></td>");
            out.println("</tr>");
            odd = !odd;
         }
         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td>");
         out.println("<table cellspacing=0 cellpading=0>");

         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("adminProj/roles?") + oldQS + "\"'>&nbsp;");
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
         out.println("<input type=\"hidden\" NAME=rid value=\"" + rid + "\">");
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
   /***************************************************************************************
    * *************************************************************************************
    * The users page
    */
   private void writeUser(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";
      if (oper.equals("UPDATE")) {
         if (updateUser(req, res, conn))
            writeUserPage(req, res);
      } else
         writeUserPage(req, res);
   }
   private void writeUserPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      Connection conn =  (Connection) session.getValue("conn");
      Statement stmt_user = null;
      ResultSet rset_user = null;
      Statement stmt_role = null;
      ResultSet rset_role = null;
      try {
         String oldQS = buildQS(req);
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrieve the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         
         String pid = req.getParameter("pid");
         String uname, rname;
         String sql;
         Vector roles = new Vector();

         out.println("<html>");
         out.println("<head>");
         writeUserScript(out);
         out.println("<title>User Projects</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Projects - Users</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
         out.println("<form method=post action=\"" + getServletPath("adminProj/users?") +
                     oldQS + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         // read and store all the roles for this project
         stmt_role = conn.createStatement();
         rset_role = stmt_role.executeQuery("SELECT RID, NAME FROM " +
                                            "v_roles_1 where pid=" + pid + " ORDER BY NAME");
         while (rset_role.next() ) {
            ValueHolder vh = new ValueHolder();
            vh.o1 = rset_role.getString("NAME");
            vh.o2 = rset_role.getString("RID");
            roles.addElement(vh);
         }

         stmt_user = conn.createStatement();
         sql = "SELECT NAME, ID, RID FROM GdbAdm.V_USERS_2 WHERE " +
            "PID=" + pid + " ORDER BY NAME";
         rset_user = stmt_user.executeQuery(sql);
         out.println("<table cellspacing=0 cellpadding=0>" +
                     "<tr bgcolor=\"#008B8B\"><td>User name</td><td>Role</td></tr>");
         int rowcount = 0;
         while (rset_user.next()) {
            out.println("<tr><td>" + formatOutput(session, rset_user.getString("NAME"), 31) + "</td>");
            out.println("<td><select name=id" + rowcount + " width=200 style=\"WIDTH: 200px\">");
            for (int i = 0; i < roles.size(); i++) {
               ValueHolder vh = (ValueHolder) roles.elementAt(i);
               out.println("<option ");
               if (rset_user.getString("RID").equals((String) vh.o2))
                  out.println("selected ");
               out.println("value=\"" + rset_user.getString("ID") + "," +
                           (String) vh.o2 + "\">" + (String) vh.o1 + "</option>");
            }
            out.println("<option value=\"" + rset_user.getString("ID") + ",-1\">" +
                        "(Remove)</option>");
            out.println("</select>");
            out.println("</td></tr>");
            rowcount++;
         }

         rset_user.close();
         stmt_user.close();
         stmt_user = conn.createStatement();
         rset_user = stmt_user.executeQuery("SELECT NAME, ID FROM V_USERS_1 where id not in (SELECT ID FROM V_USERS_2 WHERE PID=" + pid+")");
                                            
         out.println("<tr><td>");
         out.println("<select name=add_us width=200 " +
                     "style=\"WIDTH: 200px\">");
         out.println("<option value=\"\" selected>(New user)</option>");
         while (rset_user.next()) {
            out.println("<option value=\"" + rset_user.getString("ID") + "\">" +
                        formatOutput(session, rset_user.getString("NAME"), 11) + "</option>");
         }
         out.println("</select></td>");

         rset_role.close();
         stmt_role.close();
         stmt_role = conn.createStatement();
         rset_role = stmt_role.executeQuery("SELECT RID, NAME FROM V_ROLES_1 " +
                                            "WHERE PID=" + pid + " ORDER BY NAME");
         out.println("<td><select name=\"user_role\" width=200 " +
                     "style=\"WIDTH: 200px\">");
         while (rset_role.next()) {
            out.println("<option value=\"" + rset_role.getString("RID") + "\">" +
                        rset_role.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr>");
         out.println("<tr><td colspan=2>");
         out.println("<table><tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" " +
                     "onClick='JavaScript:location.href=\"" + getServletPath("adminProj?&RETURNING=YES") + "\";'>");
                    //  "onClick='JavaScript:location.href=\"" + getServletPath("adminProj") + "\";'>");
         out.println("</td><td>");
         out.println("<input type=Reset value=\"Reset\" width=100 " +
                     "style=\"WIDTH: 100px\">");
         out.println("</td><td>");
         out.println("<input type=button value=Update width=100 " +
                     "style=\"WIDTH: 100px\" onClick='valForm();'>");
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
            if (rset_user != null) rset_user.close();
            if (stmt_user != null) stmt_user.close();
            if (rset_role != null) rset_role.close();
            if (stmt_role != null) stmt_role.close();
         } catch (SQLException ignored) {}
      }
   }
   /***************************************************************************************
    * *************************************************************************************
    * The Role page
    */
   private void writeRole(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      Connection conn =  (Connection) session.getValue("conn");
      Statement stmt_priv = null;
      ResultSet rset_priv = null;
      Statement stmt_role = null;
      ResultSet rset_role = null;
      try {
         String oldQS = buildQS(req);
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrieve the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         
         String pid = req.getParameter("pid");
         String rname;
         String sql;
         out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out.println("<html>");
         out.println("<head>");
         writeUserScript(out);
         out.println("<title>Projects roles</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Projects - Roles</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
         out.println("<form method=post action=\"" + getServletPath("adminProj/roles?") +
                     oldQS + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         stmt_role = conn.createStatement();
         rset_role = stmt_role.executeQuery("SELECT RID, NAME, COMM FROM " +
                                            "v_roles_1 where pid=" + pid + " ORDER BY NAME");
         out.println("<table cellpading=0 cellspacing=0>");
         out.println("<tr bgcolor=\"#oo8B8B\">");
         out.println("<td width=200 style=\"WIDTH: 200px\">Role</td>");
//         out.println("<td width=200 style=\"WIDTH: 200px\">Privilege</td>");
        out.println("<td width=200 style=\"WIDTH: 200px\"></td>");

         out.println("<td width=300 style=\"WIDTH: 300px\">Comment</td>");
         out.println("<td width=50 style=\"WIDTH: 50px\">&nbsp;</td>");
         out.println("</tr>");
         String bgcolor = "lightgrey";


         while (rset_role.next() ) {
            if (bgcolor.equalsIgnoreCase("lightgrey"))
            {
             bgcolor = "white";
            }
            else
            {
               bgcolor = "lightgrey";
            }

 //           out.println("<tr bgcolor=white><td>");
            out.println("<tr bgcolor="+ bgcolor +"><td>");
            out.println(formatOutput(session, rset_role.getString("NAME"), 21));
            out.println("</td><td>&nbsp;</td>");
            out.println("<td>" + formatOutput(session, rset_role.getString("COMM"), 30) + "</td>");
            out.println("<td><a href=\"" + getServletPath("adminProj/editRole?") +
                        oldQS + "&rid=" + rset_role.getString("RID") + "\">edit</td></tr>");
/*

            stmt_priv = conn.createStatement();
            rset_priv = stmt_priv.executeQuery("SELECT NAME, COMM FROM V_PRIVILEGES_2 " +
                                               "WHERE RID=" + rset_role.getString("RID"));
            while (rset_priv.next() ) {
               out.println("<tr bgcolor=lightgrey><td>&nbsp;</td>");
               out.println("<td>" + formatOutput(session, rset_priv.getString("NAME"), 13) + "</td>");
               out.println("<td>" + formatOutput(session, rset_priv.getString("COMM"), 30) + "</td>");
               out.println("<td>&nbsp;</td></tr>");
            }
            if (bgcolor.equals("white"))
               bgcolor = "lightgrey";
            else
               bgcolor="white";
            rset_priv.close();
            stmt_priv.close();
*/

         }

         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr>");
         out.println("<tr><td colspan=2>");
         out.println("<table><tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" " +
                     /*"onClick='JavaScript:location.href=\"" + getServletPath("adminProj?") +
                     oldQS + "\";'>&nbsp;");
                     */
                       "onClick='JavaScript:location.href=\""
                       + getServletPath("adminProj?&RETURNING=YES")+"\";'>&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Import width=100 " +
                     "style=\"WIDTH: 100px\" " +
                     "onClick='JavaScript:location.href=\"" + getServletPath("adminProj/impRole?") +
                     oldQS + "\";'>&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=\"Create new\" width=100 " +
                     "style=\"WIDTH: 100px\" " +
                     "onClick='JavaScript:location.href=\"" + getServletPath("adminProj/newRole?") +
                     oldQS + "\";'>&nbsp;");
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
      } catch (Exception e)	{
         out.println("<PRE>");
         e.printStackTrace(out);
         out.println("</PRE>");
      } finally {
         try {
            if (rset_priv != null) rset_priv.close();
            if (stmt_priv != null) stmt_priv.close();
            if (rset_role != null) rset_role.close();
            if (stmt_role != null) stmt_role.close();
         } catch (SQLException ignored) {}
      }
   }


   /**
    * Creates a new project.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if project was created.
    *         False if project was not created
    */
   private boolean create(HttpServletRequest request,
                          HttpServletResponse response,
                          Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try {
         HttpSession session = request.getSession(true);
         String name = null, comm = null;
         connection.setAutoCommit(false);
         name = request.getParameter("n");
         comm = request.getParameter("c");
         DbProject dbp = new DbProject();
         dbp.CreateProject(connection, name, comm);
         errMessage = dbp.getErrorMessage();
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

      commitOrRollback(connection, request, response, "Projects.New.Create",
                       errMessage, "adminProj/new", isOk); 
      return isOk;
   }

   
   private boolean createRole(HttpServletRequest request,
                              HttpServletResponse response,
                              Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         String name = null, comm = null, pid= null;
         connection.setAutoCommit(false);
         name = request.getParameter("n");
         comm = request.getParameter("c");
         pid = request.getParameter("pid");
         DbRole dbr = new DbRole();
         dbr.CreateRole(connection, Integer.parseInt(pid), name, comm);
         errMessage = dbr.getErrorMessage();
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

      commitOrRollback(connection, request, response, "Projects.Roles.New.Create",
                       errMessage, "adminProj/newRole", isOk); 
      return isOk;
   }


   /**
    * Creates a new role with privileges read from a file
    *
    * @param request The Request object to use
    * @param response The Response object to use
    * @return True if everything is ok
    *         False if anything fails
    * @exception IOException If writing role- or error-page fails.
    * @exception ServletException If writing role- or error-page fails.
    */
   private boolean createRoleFile(HttpServletRequest request,
                                  HttpServletResponse response)
      throws IOException, ServletException
   {
      boolean isOk = true;
      String errMessage = null;
      Connection connection = null;
      ResultSet resultSet = null;
      Statement sqlStatement = null;
      try
      {
         HttpSession session = request.getSession(true);
         connection = (Connection) session.getValue("conn");
         connection.setAutoCommit(false);

         String upPath = getUpFilePath();
         MultipartRequest multi =
            new MultipartRequest(request, upPath, 5 * 1024 * 1024);

         // Get parameters from request
         String roleName = multi.getParameter("n");
         String comment = multi.getParameter("c");
         String projectId = request.getParameter("pid");

         // First we create the role, than we add the privileges
         DbRole dbRole = new DbRole();
         dbRole.CreateRole(connection, Integer.parseInt(projectId), roleName, comment);
         errMessage = dbRole.getErrorMessage();

         // Ensure no error messge was created during role creation
         Assertion.assertMsg(errMessage == null ||
                          errMessage.trim().equals(""),
                          "Role creation caused error: " + errMessage);
         
         // Find the new role's id
         sqlStatement = connection.createStatement();
         resultSet = sqlStatement.executeQuery("SELECT RID FROM V_ROLES_1 WHERE NAME='" +
                                  roleName + "' AND PID=" + projectId);

         // Ensure there is a new role and get it's id
         Assertion.assertMsg(resultSet.next(), "New role was not created.");
         int roleId = resultSet.getInt("RID");

         FileParser fileParser = null;
         Enumeration fileEnum =  multi.getFileNames();
         if (fileEnum.hasMoreElements())
         {
            String givenFileName = (String) fileEnum.nextElement();
            String systemFileName = multi.getFilesystemName(givenFileName);
            fileParser = new FileParser(upPath + "/" +  systemFileName); //(byte)';');
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.ROLE,
                                                                        FileTypeDefinition.LIST));
            
            dbRole.AddPrivileges(fileParser, connection, roleId);
            errMessage = dbRole.getErrorMessage();
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

      finally
      {
         try
         {
            if (resultSet != null)
            {
               resultSet.close();
            }
            
            if (sqlStatement != null)
            {
               sqlStatement.close();
            }
            
         }
         catch (SQLException ignored)
         {
         }
      }
      
      // If commit/rollback was ok and if the previous db-operation was ok,
      // write the role
      if (commitOrRollback(connection, request, response,
                           "Projects.Roles.Import.Send", errMessage,
                           "adminProj/impRole", isOk)
          && isOk)
      {
         writeRole(request, response);
      }
      return isOk;
   }


   /**
    * Deletes a project.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if project was deleted.
    *         False if project was not deleted.
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
         int pid;
         connection.setAutoCommit(false);
         pid = Integer.parseInt(request.getParameter("pid"));
         DbProject dbp = new DbProject();
         dbp.DeleteProject(connection, pid);
         errMessage = dbp.getErrorMessage();
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
                       "Projects.Edit.Delete", errMessage, "adminProj/new",
                       isOk); 
      return isOk;
   }


   /**
    * Deletes a role from the project.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if the role was removed.
    *         False if the role was not removed.
    */
   private boolean deleteRole(HttpServletRequest request,
                              HttpServletResponse response,
                              Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int rid;
         connection.setAutoCommit(false);
         rid = Integer.parseInt(request.getParameter("rid"));
         DbRole dbr = new DbRole();
         dbr.DeleteRole(connection, rid);
         errMessage = dbr.getErrorMessage();
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
                       "Projects.Roles.Edit.Delete", errMessage,
                       "adminProj/editRole", isOk); 
      return isOk;
   }

   private boolean update(HttpServletRequest request,
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
         String comm;
         String[] add_sp = new String[1];
         String[] rem_sp = new String[1];
         String[] add_su = new String[1];
         String[] rem_su = new String[1];
         int pid;
         String oldQS = request.getQueryString();
         name = request.getParameter("n");
         status = request.getParameter("s");
         comm = request.getParameter("c");
         pid = Integer.parseInt(request.getParameter("pid"));
         add_sp[0] = request.getParameter("add_sp");
         rem_sp[0] = request.getParameter("rem_sp");
         add_su[0] = request.getParameter("add_su");
         rem_su[0] = request.getParameter("rem_su");

         DbProject dbp = new DbProject();

         dbp.UpdateProject(connection, pid, name, comm, status);
         errMessage = dbp.getErrorMessage();
         Assertion.assertMsg(errMessage == null ||
                          errMessage.trim().equals(""), errMessage);

         if (add_sp[0] != null && !add_sp[0].trim().equals(""))
         {
            dbp.AddSpecies(connection, pid, add_sp);
            errMessage = dbp.getErrorMessage();
            Assertion.assertMsg(errMessage == null ||
                             errMessage.trim().equals(""), errMessage);
         }

         if (rem_sp[0] != null && !rem_sp[0].trim().equals(""))
         {
            dbp.RemoveSpecies(connection, pid, rem_sp);
            errMessage = dbp.getErrorMessage();
            Assertion.assertMsg(errMessage == null ||
                             errMessage.trim().equals(""), errMessage);
         }

         if (add_su[0] != null && !add_su[0].trim().equals("")) {
            dbp.AddSU(connection, pid, add_su);
            errMessage = dbp.getErrorMessage();
            Assertion.assertMsg(errMessage == null ||
                             errMessage.trim().equals(""), errMessage);
         }

         if (rem_sp[0] != null && !rem_su[0].trim().equals(""))
         {
            dbp.RemoveSU(connection, pid, rem_su);
            errMessage = dbp.getErrorMessage();
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
                       "Projects.Edit.Update", errMessage,
                       "adminProj/edit", isOk);
      return isOk;
   }

   
   /**
    * Updates a user in the project.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if user was updated.
    *         False if user was not updated.
    */
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
         String temp, id, rid;
         int pid;
         String oldQS = request.getQueryString();
         pid = Integer.parseInt(request.getParameter("pid"));
         int rowcount = 0;
         DbProject dbp = new DbProject();

         while (true)
         {
            temp = request.getParameter("id" + rowcount);
            if (temp == null || temp.trim().equals(""))
            {
               // We're done!
               break;
            }
            id = temp.substring(0, temp.indexOf(","));
            rid = temp.substring(temp.indexOf(",") + 1);
            // This is a rather ugly way of doing this. We update all the users
            // roles even if none has been updated.
            if (Integer.parseInt(rid) > 0)
            {
               dbp.UpdateUser(connection, pid, Integer.parseInt(rid), id);
            }
            else
            {
               dbp.RemoveUser(connection, pid, id);
            }
            errMessage = dbp.getErrorMessage();
            Assertion.assertMsg(errMessage == null ||
                             errMessage.trim().equals(""), errMessage);
            rowcount++;
         }

         // Check if there is a new user who should be included in this project
         String new_user = request.getParameter("add_us");
         if (isOk && new_user != null && !new_user.trim().equals(""))
         {
            // Yes, there is!
            String new_rid = request.getParameter("user_role");
            dbp.AddUser(connection, pid, Integer.parseInt(new_rid), new_user);
            errMessage = dbp.getErrorMessage();
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
                       "Projects.Users.Update", errMessage,
                       "adminProj/users", isOk);
      return isOk;
   }


   /**
    * Updates a role in the project.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if role was updated.
    *         False if role was not updated.
    */
   private boolean updateRole(HttpServletRequest request,
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
         int pid, rid;
         String prid;
         String oldQS = request.getQueryString();
         pid = Integer.parseInt(request.getParameter("pid"));
         rid = Integer.parseInt(request.getParameter("rid"));
         name = request.getParameter("n");
         comm = request.getParameter("c");
         DbRole dbr = new DbRole();

         // First we update the name and the comment!
         dbr.UpdateRole(connection, rid, name, comm);
         errMessage = dbr.getErrorMessage();
         Assertion.assertMsg(errMessage == null ||
                          errMessage.trim().equals(""), errMessage); 

         Enumeration e = request.getParameterNames();
         String pn;
         String value;
         Vector privileges = new Vector();
         while (e.hasMoreElements() )
         {
            pn = (String) e.nextElement();
            if (pn.startsWith("priv"))
            {
               // This is a privilege
               prid = pn.substring("priv".length());
               value = request.getParameter(pn);
               privileges.addElement(prid);
            }
         }
         int[] prids = new int[privileges.size()];
         for (int i = 0; i < privileges.size(); i++)
            prids[i] = Integer.parseInt((String) privileges.elementAt(i));
         dbr.SetPrivileges(connection, rid, prids);
         errMessage = dbr.getErrorMessage();
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
                       "Projects.Roles.Edit.Update", errMessage,
                       "adminProj/roles", isOk); 
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

   private void writeRoleEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to delete this role?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("   if (!('' + document.forms[0].n.value)=='') {");
      out.println("		  if (confirm('Are you sure you want to update this role?')) {");
      out.println("			  document.forms[0].oper.value='UPDATE';");
      out.println("			  rc = 0;");
      out.println("		  }");
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

  private void writeEditSpeciesScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function selectionChanged() {");
      out.println("	document.forms[0].oper.value='SELECT';");
      out.println("	document.forms[0].submit();");
      out.println("	return (true);");
      out.println("}");
      out.println("function remSpecies() {");
      out.println("	document.forms[0].oper.value='REM_SPECIES';");
      out.println("	document.forms[0].submit();");
      out.println("	return (true);");
      out.println("}");
      out.println("");
      out.println("function addSpecies() {");
      out.println("	document.forms[0].oper.value='ADD_SPECIES';");
      out.println("	document.forms[0].submit();");
      out.println("	return (true);");
      out.println("}");
      out.println("");
      out.println("");
      out.println("//-->");
      out.println("</script");

   }
 private void writeEditSUScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function selectionChanged() {");
      out.println("	document.forms[0].oper.value='SELECT';");
      out.println("	document.forms[0].submit();");
      out.println("	return (true);");
      out.println("}");
      out.println("function remSU() {");
      out.println("	document.forms[0].oper.value='REM_SU';");
      out.println("	document.forms[0].submit();");
      out.println("	return (true);");
      out.println("}");
      out.println("");
      out.println("function addSU() {");
      out.println("	document.forms[0].oper.value='ADD_SU';");
      out.println("	document.forms[0].submit();");
      out.println("	return (true);");
      out.println("}");
      out.println("");
      out.println("");
      out.println("//-->");
      out.println("</script");

   }

}
