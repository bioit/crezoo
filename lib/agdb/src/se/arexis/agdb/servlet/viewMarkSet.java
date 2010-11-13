/*
  $Log$
  Revision 1.9  2005/03/22 16:22:59  heto
  Removing CallableStatement.
  Fixed bugs in GUI

  Revision 1.8  2005/02/04 15:58:40  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.7  2005/01/31 16:16:41  heto
  Changing database to PostgreSQL. Problems with counts and selection buttons...

  Revision 1.6  2004/12/14 08:56:15  heto
  swedish characters changed in comment.

  Revision 1.5  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.4  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.3  2002/10/22 06:08:11  heto
  rebuilt the "back-buttons".
  Dont save the request object, save the URL instead.
  New function.

  Revision 1.2  2002/10/18 11:41:10  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:05  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.14  2002/01/31 15:54:09  roca
  Additional fixes of javascript for mac, Genotype import mm

  Revision 1.13  2001/09/06 13:01:02  roca
  Major changes to Genotype import handling.
  modified Linkage output format for Post makeped and allele numbering.
  Bug when deleting markersets fixed

  Revision 1.12  2001/05/31 11:53:07  roca
  More structured page and added cromosome choice

  Revision 1.11  2001/05/31 07:07:04  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.10  2001/05/23 15:20:56  roca
  Better layout on membership page

  Revision 1.9  2001/05/23 12:32:33  roca
  fixed choice of chromosome for markerset membership
  changed order of links in adminProj. "Order by name" added it adm species & SU's

  Revision 1.8  2001/05/22 06:16:57  roca
  Backfuncktionality fixed for all (?) servlets/subpages

  Revision 1.7  2001/05/11 11:49:44  frob
  Changed all methods that used to call writeError() to write an error page. These
  methods now calls the general method commitOrRollback() which does the commit or
  rollback operation as well as handle any errors. writeError() was removed.

  Revision 1.6  2001/05/03 14:21:02  frob
  Implemented local version of errorQueryString and changed writeError to use this method.

  Revision 1.5  2001/05/03 07:57:42  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.4  2001/05/03 05:59:37  frob
  Calls to removeOper changed to use the general removeQSParameter.
  The previously called method is removed.

  Revision 1.3  2001/04/24 09:33:57  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:29  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.4  2001/04/18 09:26:46  frob
  Removed the size of the main table used on the webpages.

  Revision 1.1.1.1.2.3  2001/04/12 07:15:52  frob
  createFile: Changed call to Parse() to pass valid file type definitions.
              No longer uses delimiters from request object.
              Layout fixes.
  writeImpFilePage: No longer displays delimiters.
                    Length of file name field changed.
                    HTML validated.

  Revision 1.1.1.1.2.2  2001/03/28 13:47:56  frob
  Added catch() for InputDataFileException which can be raised
  from the parse()-method.
  Added check that the connection object is created before doing
  rollback on it (to avoid null pointer exception).

  Revision 1.1.1.1.2.1  2001/03/28 12:37:48  frob
  Changed the call to the FileParser constructor.
  Indeted the file and added the log header.

*/

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;
import se.arexis.agdb.util.FileImport.*;

import com.oreilly.servlet.MultipartRequest;

public class viewMarkSet extends SecureArexisServlet
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
      } else if (extPath.equals("/bottom")) {
         writeBottom(req, res);
      } else if (extPath.equals("/details")) {
         writeDetails(req, res);
      } else if (extPath.equals("/edit")) {
         writeEdit(req, res);
      } else if (extPath.equals("/membership")) {
         writeMember(req, res);
      } else if (extPath.equals("/new")) {
         writeNew(req, res);
      } else if (extPath.equals("/position")) {
         writePosition(req, res);
      } else if (extPath.equals("/impFile")) {
         writeImpFile(req, res);
      } else if (extPath.equals("/impMultipart")) {
         createFile(req, res);
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
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
      try 
      {

         // Check if redirection is needed
         res = checkRedirectStatus(req,res); 
         //req= getServletState(req,session);

         String topQS = buildQS(req);
         //                     // we don't want the oper parameter anaywhere but in the edit page!
         //                     topQS = removeOper(topQS);
         String bottomQS = topQS.toString();

         out.println("<html>"
                     + "<HEAD>"
                     + " <TITLE>View Marker sets frame</TITLE>"
                     + "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\"></HEAD>"

                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" //

                     + "<frame name=\"viewtop\" "
                     + "src=\""+getServletPath("viewMarkSet/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewMiddle\" "
                     + "src=\""+ getServletPath("viewMarkSet/middle?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewbottom\""
                     + "src=\"" + getServletPath("viewMarkSet/bottom?") + bottomQS + "\" "
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
         suid = null,
         name = null,
         msid = null,
         comm = null,
         marker = null,
         orderby = null;
      String pid = (String) session.getValue("PID");
      suid = req.getParameter("suid");
      if (suid == null)
         suid = findSuid(conn, pid);

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
      name = req.getParameter("NAME");
      if (name != null)
         output.append("&NAME=").append(name);
      marker = req.getParameter("marker");
      if (marker != null)
         output.append("&marker=").append(marker);
      comm = req.getParameter("COMM");
      if (comm != null)
         output.append("&COMM=").append(comm);
      msid = req.getParameter("msid");
      if (msid != null)
         output.append("&msid=").append(msid);

      // Set the parameters STARTINDEX and ROWS
      if (!action.equals("NOP"))
         output.append(setIndecis(suid, action, req, session));
      output.append("&suid=").append(suid);

      if (req.getParameter("oper") != null)
         output.append("&oper=").append(req.getParameter("oper"));
      if (req.getParameter("new_ms_name") != null)
         output.append("&new_ms_name=").append(req.getParameter("new_ms_name"));

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
         rset = stmt.executeQuery("SELECT SID FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 WHERE PID=" +
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
   private String findSuid(Connection conn, String pid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID FROM V_ENABLED_SAMPLING_UNITS_2 WHERE PID=" +
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
   private String findMsid(Connection conn, String suid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT MSID FROM gdbadm.V_MARKER_SETS_1 WHERE " +
                                  "SUID=" + suid + " ORDER BY NAME");
         if (rset.next()) {
            ret = rset.getString("MSID");
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
   private String setIndecis(String suid, String action, HttpServletRequest req, HttpSession session) {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(suid, req, session);
      maxRows = getMaxRows(session);
      if (req.getParameter("STARTINDEX") != null) {
         startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         if (rows > 0 && startIndex == 0)
            startIndex=1;
      } else
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
      String pid = (String) session.getValue("PID");
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer(512);
      try {
         sbSQL.append("SELECT count(*) "
                      + "FROM V_MARKER_SETS_2 WHERE SUID=" + suid + " ");
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
      String name = null,
         comm = null,
         mark = null,
         orderby = null;
      StringBuffer filter = new StringBuffer(256);
      name = req.getParameter("NAME");
      comm = req.getParameter("COMM");
      mark = req.getParameter("marker");
      //orderby = req.getParameter("ORDERBY");

      if (name != null && !"".equalsIgnoreCase(name))
         filter.append(" and NAME like'" + name + "'");
      if (comm != null && !"".equals(comm))
         filter.append(" AND COMM like '" + comm + "'");
      if (mark != null && !"".equals(mark.trim()))
         filter.append(" AND MSID IN " +
                       "(SELECT DISTINCT MSID FROM gdbadm.V_POSITIONS_1 " +
                       "WHERE MID IN " +
                       "(SELECT MID FROM gdbadm.V_MARKERS_1 WHERE NAME like '" + mark + "')" +
                       ")");
      return filter.toString().replace('*', '%');
   }
        
        
   /***************************************************************************************
    * *************************************************************************************
    * The top frame
    */
                
   public void writeTop(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      // set content type and other response header fields first
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");

      Statement stmt = null;
      ResultSet rset = null;
      int startIndex = 0, rows = 0, maxRows = 0;
      String suid, mark, comm, name, orderby, oldQS, newQS, action, pid;
      int privileges[] = (int[]) session.getValue("PRIVILEGES");
      try {
         conn = (Connection) session.getValue("conn");
         oldQS = req.getQueryString();
         newQS = buildTopQS(oldQS);
         pid = (String) session.getValue("PID");
         suid = req.getParameter("suid");
         maxRows = getMaxRows(session);

         name = req.getParameter("NAME");
         comm = req.getParameter("COMM");
         mark = req.getParameter("marker");
         orderby = req.getParameter("ORDERBY");
         action = req.getParameter("ACTION");
         if (req.getParameter("STARTINDEX") != null)
            startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         else
            startIndex = 0;
         if (req.getParameter("ROWS") != null)
            rows = Integer.parseInt(req.getParameter("ROWS"));
         else
            rows = 0;
         if (pid == null) pid = "-1"; // Hope this will select nothing
         if (suid == null) suid = findSuid(conn, pid);
         if (name == null) name = "";
         if (comm == null) comm = "";
         if (mark == null) mark = "";
         if (orderby == null) orderby = "NAME";
         if (action == null) action = "NOP";

         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+ getURL("style/view.css")+"\">");
         out.println("<base target=\"content\">");

         writeTopScript(out);

         out.println("<title>View Marker Sets</title>");
         // new
         out.println("</head>");
         out.println("<body bgcolor=\"#ffffd0\">"
                     +"<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
                     +"<tr>"
                     + "<td width=\"14\" rowspan=\"3\">"+"</td>"
                     +"<td width=\"736\" colspan=\"2\" height=\"15\">"
                     +"<form method=get action=\"" + getServletPath("viewMarkSet") + "\">"
                     +"<p align=\"center\"><font size=\"2\"><b  style=\"font-size: 15pt\">Marker Sets</b>"
                     +"</font></td></tr>"
                     +"<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>"
                     +"</tr><tr><td width=\"517\">");

         //sampling unit
         out.println("<table width=488 height=\"92\">"
                     +"<td><b>Sampling Unit</b><br><select name=suid "
                     +" style=\"HEIGHT: 22px; WIDTH: 126px\">");

         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SUID FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID="+ pid + " ORDER BY NAME");
         while (rset.next()) {
            out.println("<option " +
                        (suid.equals(rset.getString("SUID")) ? "selected " : "" ) +
                        "value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }

         rset.close();
         stmt.close();
         out.println("</SELECT></td>");

         // inputfields
         out.println("<td><b>Name</b><br>"
                     + "<input id=name name=NAME value=\"" + name + "\"style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\">"
                     +"</td><td><b>Comment</b><br>"
                     +"<input id=comm name=COMM value=\"" + comm + "\" style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\">"
                     +"</td><td><b>Member</b><br>"
                     +"<input id=mark name=marker value=\"" + mark + "\" style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\">");

         out.println("</td></table></td>");

         //Buttons
         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");
         out.println("<input type=button value=\"New Marker Set\" " +
                     privDependentString(privileges, MRKS_W,
                                         " onClick='parent.location.href=\"" +getServletPath("viewMarkSet/new?") + newQS + "\"'",
                                         "disabled ") +
                     "height=20 width=139 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" " +
                     "name=\"button\">");
         out.println("</td>");

         out.println("<tr><td width=68 colspan=2>"
                     +"<input id=COUNT name=COUNT type=submit value=\"Count\" width=\"69\""
                     +" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">"
                     +"</td>"
                     +"<td width=68 colspan=2>"
                     +"<input id=DISPLAY name=DISPLAY type=submit value=\"Display\""
                     +" width=\"70\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">"
                     +"</td></tr>");
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

         out.println("<input type=\"hidden\" id=\"STARTINDEX\" name=\"STARTINDEX\" value=\"" + startIndex + "\">");
         out.println("<input type=\"hidden\" id=\"ORDERBY\" name=\"ORDERBY\" value=\"" + orderby + "\">");
         out.println("<input type=\"hidden\" id=\"oper\" name=\"oper\" value=\"\">");

         out.println("</table></form></td></tr></table>");

         out.println("</td></tr></TABLE>");
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
      out.println("var MAX_LENGTH = 20;");              
      out.println("var MIN_LENGTH = 1;");
      out.println("function newMarkerSet() {");
      out.println("");
      out.println("     var name = prompt('Marker set name', '');");
      out.println("     if (name == null || '' == name)");
      out.println("             return (false);");
      out.println("     else if (name.length > MAX_LENGTH) {");         
      out.println("             alert('The name must be in the range 1-20 characters.');");
      out.println("             return (false);");
      out.println("     }");
      out.println("     if (!confirm('Are you sure you want to create a\\n'");
      out.println("             + 'new marker set with the name \\'' + name + '\\'')) {");
      out.println("             return (false);");
      out.println("     }");
      out.println("");
      out.println("");
      out.println("     document.forms[0].oper.value='NEW';");
      out.println("     document.forms[0].new_ms_name.value=name;");
      out.println("     document.forms[0].submit();");
      out.println("}");
      out.println("//-->");
      out.println("</script>"); 

   }
   
   /**
    * Creates a new marker set.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if marker set created.
    *         False if marker set not created.
    */
   private boolean createMarkerSet(HttpServletRequest request,
                                   HttpServletResponse response,
                                   Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         String name, comm;
         int suid, id;
         connection.setAutoCommit(false);
         name = request.getParameter("n_name");
         comm = request.getParameter("n_comm");
         suid = Integer.parseInt(request.getParameter("suid"));
         id = Integer.parseInt((String) session.getValue("UserID"));

         DbMarker dbm = new DbMarker();
         dbm.CreateMarkerSet(connection,
                             name,
                             comm, //comm,
                             suid,
                             id);
         errMessage = dbm.getErrorMessage();
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
                       "MarkerSets.New.Create", errMessage,
                       "viewMarkSet", isOk); 
      return isOk;
   }


   /**
    * Creates a marker set in a samping unit from a given file.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if marker set could be created.
    *         False if mareker set could not be created.
    * @exception IOException If frame or error page could not be written. 
    * @exception ServletException If frame or error page could not be written.
    */
   private boolean createFile(HttpServletRequest request,
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

         // Get parameters from request
         int samplingUnitId = Integer.parseInt(multiRequest.getParameter("suid"));
         int userId = Integer.parseInt((String) session.getValue("UserID"));
         String setName = multiRequest.getParameter("name");

         DbMarker dbMarker = new DbMarker();
         FileParser fileParser = null;

         Enumeration fileEnum = multiRequest.getFileNames();
         if (fileEnum.hasMoreElements())
         {
            String givenFileName = (String) fileEnum.nextElement();
            String systemFileName =
               multiRequest.getFilesystemName(givenFileName);
            
            fileParser = new FileParser(upPath + "/" +  systemFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.MARKERSET,
                                                                        FileTypeDefinition.LIST));
            
            dbMarker.CreateMarkerSets(fileParser, connection, setName,
                                      null, samplingUnitId, userId);
            
            errMessage = dbMarker.getErrorMessage();
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

      // if commit/rollback is ok and databas operation was ok, write the
      // frame 
      if (commitOrRollback(connection, request, response,
                       "MarkerSets.Import.Send", errMessage,
                       "viewMarkSet/impFile", isOk)
          && isOk)
      {
         writeFrame(request, response);
      }
      return isOk;
   }



   /************************************************************
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
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
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
         out.println("<base target=\"content\"></head><body>");

         if(action != null) {
            /*
              out.println("<tr>"
              + "<td width=\"750\" colspan=\"3\">"
              + "<p align=left>&nbsp;&nbsp;"
              + buildInfoLine(action, startIndex, rows, maxRows)
              + "</td>"
              + "</tr>");
            */
            out.println("&nbsp;" +buildInfoLine(action, startIndex, rows, maxRows));

         }

         String oldQS, newQS;
         oldQS = req.getQueryString();
         String choosen= req.getParameter("ORDERBY");
         //System.err.println("old=" +oldQS);
         newQS = buildTopQS(oldQS);
         /*
           out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
           + "</tr>"
           + "<tr>"
           + "<td width=\"750\" colspan=\"3\">"
           + "<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 height=20 width=800 style=\"margin-left:2px\">"
           + "<td width=5></td>");
         */
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=800 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");


         // the menu choices
         //chromosome
         //Markerset
         out.println("<td width=100><a href=\"" + getServletPath("viewMarkSet")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\">");
         if(choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown><b>Marker Set</b></FONT></a></td>\n");
         else out.println("Marker Set</a></td>\n");
         //Comment
         out.println("<td width=200><a href=\"" + getServletPath("viewMarkSet")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\">");
         if(choosen.equals("COMM"))
            out.println("<FONT color=saddlebrown><b>Comment</b></FONT></a></td>\n");
         else out.println("Comment</a></td>\n");
         //USER
         out.println("<td width=100><a href=\"" + getServletPath("viewMarkSet")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
         else out.println("User</a></td>\n");

         //Updated
         out.println("<td width=200><a href=\"" + getServletPath("viewMarkSet")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TS\">");
         if(choosen.equals("TS"))
            out.println("<FONT color=saddlebrown><b>Updated</b></FONT></a></td>\n");
         else out.println("Updated</a></td>\n");
         /*
           out.println("<td width=100>&nbsp;</td>"
           + "<td width=100>&nbsp;</td>"
           + "</table>"
           + "</td>"
           + "</tr>"
           + "</table>"
           + "</body></html>");
         */
         out.println("<td width=100>&nbsp;</td>");
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
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
      Statement stmt = null;
      ResultSet rset = null;
      Connection conn = null;
      int privileges[] = (int[]) session.getValue("PRIVILEGES");
      try
      {
         String suid = null, pid = null, action = null, orderby;
         String oldQS = removeQSParameterOper(req.getQueryString());
         action = req.getParameter("ACTION");
         suid = req.getParameter("suid");
         pid = (String) session.getValue("PID");
         orderby = req.getParameter("ORDERBY");
         if (orderby == null) orderby = "NAME";
         if (action == null || action.equalsIgnoreCase("NOP") ||
             action.equalsIgnoreCase("COUNT") ||
             suid == null || pid == null)
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
         sbSQL.append("SELECT NAME, COMM, " +
                      "USR, to_char(TS, '" + getDateFormat(session) + "') as TC_TS, MSID " +
                      "FROM gdbadm.V_MARKER_SETS_2 WHERE SUID=" + suid + " ");
         String qs = req.getQueryString();
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter).append(" ORDER BY ").append(orderby);
         rset = stmt.executeQuery(sbSQL.toString());
         out.println("<TABLE align=left border=0 cellPadding=0");
         out.println("cellSpacing=0 width=800 style=\"margin-left:2px\">");//STYLE=\"WIDTH: 790px;\">");
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
            out.println("<TD WIDTH=5></TD>");
            out.println("<TD WIDTH=100>" + formatOutput(session, rset.getString("NAME"), 15) + "</TD>");
            out.println("<TD WIDTH=200>" + formatOutput(session,rset.getString("COMM"),20) + "</TD>");
            out.println("<TD WIDTH=100>" + rset.getString("USR") + "</TD>");
            out.println("<TD WIDTH=200>" + rset.getString("TC_TS") + "</TD>");

            out.println("<TD WIDTH=100>");
            out.println(privDependentString(privileges, MRKS_R,
                                            "<A HREF=\""+getServletPath("viewMarkSet/details?")+ "msid=" + rset.getString("MSID") + "&"
                                            + oldQS + "\" target=\"content\">Details</A>", "<font color=tan>Details</font"));
            out.println("</TD>");

            out.println("<TD WIDTH=100>");
            out.println(privDependentString(privileges, MRKS_W,
                                            "<A HREF=\""+getServletPath("viewMarkSet/edit?")+"msid=" + rset.getString("MSID") + "&"
                                            + oldQS + "\" target=\"content\">Edit</A>", "<font color=tan>Edit</font>"));
            out.println("</TD></TR>");
            rowCount++;
         }
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
    * The detail page
    */
   private void writeDetails(HttpServletRequest req, HttpServletResponse res) 
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");

      Connection conn = null;
      Statement stmt_curr = null, stmt_hist = null;
      ResultSet rset_curr = null, rset_hist = null;
      String prev_name = null;
      String prev_comm = null;
      String prev_usr = null;
      String prev_ts = null;
      String curr_name = null;
      String curr_comm = null;
      String curr_usr = null;
      String curr_ts = null;
      boolean has_history = false;

      try {
         String oldQS = buildQS(req);
         String msid = req.getParameter("msid");
         if (msid == null || msid.trim().equals("")) msid = "-1";
                        
         conn = (Connection) session.getValue("conn");
         // Get the current data 
         stmt_curr = conn.createStatement();
         String strSQL = "SELECT SNAME, SUNAME, NAME, MSID, "
            + "COMM, USR, to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS "
            + "FROM gdbadm.V_MARKER_SETS_3 WHERE "
            + "MSID=" + msid;
         rset_curr = stmt_curr.executeQuery(strSQL);
                        
         // Get the history
         stmt_hist = conn.createStatement();
         strSQL = "SELECT NAME, COMM, USR, to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS, TS as dummy " +
            "FROM gdbadm.V_MARKER_SETS_LOG " +
            "WHERE MSID=" + msid + " " +
            "ORDER BY dummy desc";
         rset_hist = stmt_hist.executeQuery(strSQL);

         if (rset_curr.next()) {
            curr_name = rset_curr.getString("NAME");
            curr_comm = rset_curr.getString("COMM");
            curr_usr = rset_curr.getString("USR");
            curr_ts = rset_curr.getString("TC_TS"); // Time stamp
            if (curr_comm == null) curr_comm = "";
         }
         if (rset_hist.next()) {
            prev_name = rset_hist.getString("NAME");
            prev_comm = rset_hist.getString("COMM");
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
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Marker Sets - Details</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td></td></tr>");

         out.println("<tr><td></td><td>");
         out.println("<table nowrap border=0 cellSpacing=0>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
         out.println("<tr><td>Species</td><td>" + rset_curr.getString("SNAME") + "</td></tr>");
         out.println("<tr><td>Sampling unit</td><td>" + rset_curr.getString("SUNAME") + "</td></tr>");
         out.println("</table><br><br>");

         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");

         out.println("<table nowrap align=center border=0 cellSpacing=0 width=520px>");                 
         out.println("<tr bgcolor=Black>" +
                     "<td align=center colspan=9><b><font color=\"#ffffff\">Current Data</font></b></td>");
         out.println("<tr bgcolor=\"#008B8B\"><td nowrap WIDTH=100 style=\"WIDTH: 100px\">NAME");
         out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">Comment");
         out.println("<td nowrap WIDTH=120 style=\"WIDTH: 50px\">User</td>");
         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 120px\">Last updated</td></tr>");
                        
         out.println("<tr bgcolor=white>");
         out.println("<td nowrap WIDTH=100 style=\"WIDTH: 100px\">" + 
                     (curr_name.equals(prev_name) ? "" + curr_name : "<font color=red>" + curr_name + "</font>"));
         out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" +
                     (curr_comm.equals(prev_comm) ? "" + curr_comm : "<font color=red>" + curr_comm + "</font>"));
         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" + curr_usr + "</td>");
         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 120px\">" + curr_ts + "</td>"); // Last updated
                                
         out.println("<tr bgcolor=Black>");
         out.println("<td align=center colspan=9><b><font color=\"#ffffff\">History</font></b></td></tr>");
                        
         curr_name = prev_name;
         curr_comm = prev_comm;
         curr_usr = prev_usr;
         curr_ts = prev_ts;
         if (curr_comm == null) curr_comm = "";
                                
         boolean odd = true;
         while (rset_hist.next()) {
            prev_name = rset_hist.getString("NAME");
            prev_comm = rset_hist.getString("COMM");
            prev_usr = rset_hist.getString("USR");
            prev_ts = rset_hist.getString("TC_TS");
            if (odd) 
               out.println("<tr bgcolor=white>");
            else 
               out.println("<tr bgcolor=lightgrey>");
            odd = !odd;
            out.println("<td nowrap WIDTH=100 style=\"WIDTH: 100px\">" +
                        (curr_name.equals(prev_name) ? "" + curr_name : "<font color=red>" + curr_name + "</font>"));
            out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" + 
                        (curr_comm.equals(prev_comm) ? "" + curr_comm : "<font color=red>" + curr_comm + "</font>")); 
            out.println("<td nowrap WIDTH=120 style=\"WIDTH: 50px\">" + curr_usr + "</td>");
            out.println("<td nowrap WIDTH=50 style=\"WIDTH: 120px\">" + curr_ts + "</td>"); // Last updated
            curr_name = prev_name;
            curr_comm = prev_comm;
            curr_usr = prev_usr;
            curr_ts = prev_ts;
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
            out.println("<td nowrap WIDTH=100 style=\"WIDTH: 100px\">" +
                        (curr_name.equals(prev_name) ? "" + curr_name : "<font color=red>" + curr_name + "</font>"));
            out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" + 
                        (curr_comm.equals(prev_comm) ? "" + curr_comm : "<font color=red>" + curr_comm + "</font>")); 
            out.println("<td nowrap WIDTH=120 style=\"WIDTH: 50px\">" + curr_usr + "</td>");
            out.println("<td nowrap WIDTH=50 style=\"WIDTH: 120px\">" + curr_ts + "</td>"); // Last updated
         }

         out.println("</tr></table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         // Back button
         out.println("<form>");
         out.println("<table cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("viewMarkSet?") + oldQS + "\"'>&nbsp;");
         out.println("</td></tr></table>");

         out.println("</form>");

         out.println("</td></tr></table>");

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
   private void writeImpFile(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      conn = (Connection) session.getValue("conn");
      writeImpFilePage(req, res);
   }



   /**
    * Writes the page used to import marker sets from.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @exception IOException If no PrintWriter could be created.
    */
   private void writeImpFilePage(HttpServletRequest request,
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
         String projectId = (String) session.getValue("PID");
         
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
         out.println("  ");
         out.println("  var rc = 1;");
         out.println("  ");
         out.println("  if (rc) {");
         out.println("   if ((\"\" + document.forms[0].name.value).length > 0) {");
         out.println("            if (confirm('Are you sure that you want to create the marker set?')) {");
         out.println("                    document.forms[0].oper.value = 'UPLOAD';");
         out.println("                    document.forms[0].submit();");
         out.println("            }");
         out.println("          }");
         out.println("  }");
         out.println("  ");
         out.println("  ");
         out.println("}");
         out.println("//-->");
         out.println("</script>");
         out.println("<title>Import marker set</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=0>" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Marker Sets - File import</b></center>" +
                     "</td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post enctype=\"multipart/form-data\" action=\"" +
                     getServletPath("viewMarkSet/impMultipart") + "\">");
         out.println("<table border=0>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         sqlStatement = connection.createStatement();
         resultSet = sqlStatement.executeQuery("SELECT SUID, NAME FROM V_ENABLED_SAMPLING_UNITS_2 WHERE " +
                                  "PID=" + projectId + " ORDER BY NAME");
         out.println("<td>Sampling unit<br>");
         out.println("<select name=suid style=\"WIDTH: 200px\">");
         while (resultSet.next() ) {
            out.println("<option value=\"" + resultSet.getString("SUID") + "\">" +
                        resultSet.getString("NAME") + "</option>");
         }
         out.println("</select>");
         resultSet.close();
         sqlStatement.close();
         out.println("</td></tr>");
         // Name
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td>Name<br>");
         out.println("<input type=text name=name maxlength=20  " +
                     "style=\"WIDTH: 200px\">");
         out.println("</td></tr>");
         
         // File
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td>File<br>");
         out.println("<input type=file name=filename " +
                     "style=\"WIDTH: 350px\">");
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
      }
      catch (Exception e)
      {
         e.printStackTrace(System.err);
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
         if (deleteMarkerSet(req, res, conn))
            writeFrame(req, res);
      } else if (oper.equals("UPDATE")) {
         if(updateMarkerSet(req, res, conn))
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
      Connection conn =  (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      String sname, suname, name, comm, usr, tc_ts, suid;
      try {
         stmt = conn.createStatement();
         String oldQS = buildQS(req);
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         String msid = req.getParameter("msid");
         String sql = "SELECT SNAME, SUNAME, SUID, NAME, COMM, USR, " +
            "to_char(TS, '" + getDateFormat(session) + "') as TC_TS " +
            "FROM gdbadm.V_MARKER_SETS_3 WHERE " +
            "MSID=" + msid;
         rset = stmt.executeQuery(sql);

         rset.next();
         sname = rset.getString("SNAME");
         suname = rset.getString("SUNAME");
         suid = rset.getString("SUID");
         name = rset.getString("NAME");
         comm = rset.getString("COMM");
         usr = rset.getString("USR");
         tc_ts = rset.getString("TC_TS");

         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);
         out.println("<title>Edit marker set</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Marker Sets - Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("viewMarkSet/edit?") +
                     oldQS + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr>");
         out.println("<td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td>");
         out.println("</tr>");
         out.println("<tr><td>Species</td><td>" + sname + "</td></tr>");
         out.println("<tr><td>Sampling unit</td><td>" + suname + "</td></tr>");
         out.println("<tr><td>Last updated by</td><td>" + usr + "</td></tr>");
         out.println("<tr><td>Last updated</td><td>" + tc_ts + "</td></tr>");
         out.println("</table>");
         out.println("</td></tr><tr><td></td><td>");

         // The changable data
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Changable data</font></td></tr>");
         out.println("<tr><td>Name<br>");
         out.println("<input type=text name=n_name width=200 style=\"WIDTH: 200px\" " +
                     "value=\"" + replaceNull(name, "") + "\" maxlength=20>");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<tr>");
         out.println("<td>Comment<br>");
         out.println("<textarea name=n_comm cols=45 rows=7>" +
                     replaceNull(comm, "") + "</textarea>");
         out.println("</td></tr>");
         out.println("</table>");

         out.println("</td></tr>");

         out.println("<tr><td></td><td></td></tr>");
         out.println("<tr><td></td><td>");
         out.println("<table cellspacing=0 cellpading=0>");

         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                    // getServletPath("viewMarkSet?") + "suid=" + suid + "&item=no\"'>&nbsp;");
                     getServletPath("viewMarkSet?&RETURNING=YES") + "\"'>&nbsp;");
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
         // Store some extra information needed by doPost()
         out.println("<input type=\"hidden\" NAME=oper value=\"\">");
         out.println("<input type=\"hidden\" NAME=RETURNING value=YES>");

         out.println("</form>");
         out.println("</td></tr></table>");
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


   /**
    * Deletes a marker set.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if marker set deleted.
    *         False if marker set not deleted.
    */
   private boolean deleteMarkerSet(HttpServletRequest request,
                                   HttpServletResponse response,
                                   Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int msid;
         connection.setAutoCommit(false);
         String UserID = (String) session.getValue("UserID");
         msid = Integer.parseInt(request.getParameter("msid"));
         DbMarker dbm = new DbMarker();
         dbm.DeleteMarkerSet(connection, msid, Integer.parseInt(UserID));
         errMessage = dbm.getErrorMessage();
         //errMessage = "effo";
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
                       "MarkerSets.Edit.Delete", errMessage, "viewMarkSet",
                       isOk); 
      return isOk;
   }

   
   /**
    * Updates a marker set.
    *
    * @param request a HttpServletRequest value
    * @param response a HttpServletResponse value
    * @param connection a Connection value
    * @return a boolean value
    */
   private boolean updateMarkerSet(HttpServletRequest request,
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
         String name, comm;
         String oldQS = request.getQueryString();
         int msid;
         msid = Integer.parseInt(request.getParameter("msid"));
         name = request.getParameter("n_name");
         comm = request.getParameter("n_comm");

         DbMarker dbm = new DbMarker();
         dbm.UpdateMarkerSet(connection, name, comm, msid, Integer.parseInt(UserID));
         errMessage = dbm.getErrorMessage();
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
                       "MarkerSets.Edit.Update", errMessage, "viewMarkSet",
                       isOk);
      return isOk;
   }


   private void writeEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("     ");
      out.println("     var rc = 1;");
      out.println("     if ('DELETE' == action.toUpperCase()) {");
      out.println("             if (confirm('Are you sure you want to delete the marker set?')) {");
      out.println("                     document.forms[0].oper.value='DELETE';");
      out.println("                     rc = 0;");
      out.println("             }");
      out.println("     ");
      out.println("     } else if ('UPDATE' == action.toUpperCase()) {");
      out.println("             if (confirm('Are you sure you want to update the marker set?')) {");
      out.println("                     document.forms[0].oper.value='UPDATE';");
      out.println("                     rc = 0;");
      out.println("             }");
      out.println("     } else {");
      out.println("             document.forms[0].oper.value='';");
      out.println("     }");
      out.println("     ");
      out.println("     if (rc == 0) {");
      out.println("             document.forms[0].submit();");
      out.println("             return true;");
      out.println("     }");
      out.println("     return false;");
      out.println("     ");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
   }
        

   /***************************************************************************************
    * *************************************************************************************
    * The Membership page
    */
        
   private void writeMember(HttpServletRequest req, HttpServletResponse res)
      throws IOException {
      String oper = null;
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      String suid, msid, pid, UserId;
      String item; // This variable tells which of the parameter that has changed
      suid = req.getParameter("suid");
      msid = req.getParameter("msid");
      pid = (String) session.getValue("PID");
      oper = req.getParameter("oper");
      UserId = (String) session.getValue("UserID");
      item = req.getParameter("item");

      res.setContentType("text/html");
      res.setHeader("Pragme", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      if (pid == null) pid = "-1";

      if (item == null) {
         // First time?
         suid = findSuid(conn, pid);
         msid = findMsid(conn, suid);
      } else if (item.equals("suid")) {
         msid = findMsid(conn, suid);
      }

      if (oper == null)
         oper = "SELECT";

      out.println("<html>");
      out.println("<head>");
      writeMemberScript(out);
      out.println("<title>Edit marker set</title>");
      HTMLWriter.css(out,getURL("style/axDefault.css"));
      out.println("</head>");
      out.println("<body>");
      out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                  "<tr>" +
                  "<td width=\"14\" rowspan=\"3\"></td>" +
                  "<td width=\"736\" colspan=\"2\" height=\"15\">");
      out.println("<center>" +
                  "<b style=\"font-size: 15pt\">Marker Sets - Membership</b></center>" +
                  "</font></td></tr>" +
                  "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                  "</tr></table>");

      out.println("<table cellspacing=0 cellpadding=0><tr>" +
                  "<td width=15></td><td>");


      if ( (oper.equalsIgnoreCase("ADD") || oper.equalsIgnoreCase("REM")) ) 
      {
         try 
         {
            boolean ok = true;
            conn.setAutoCommit(false);

            DbMarker m = new DbMarker();
            if (oper.equalsIgnoreCase("ADD")) 
            {
               // Add individuals to a grouping
               String[] mrks = req.getParameterValues("avail_mark");
               if (mrks != null) 
               {
                   m.CreateMarkerSetLinks(conn, Integer.valueOf(msid), mrks, Integer.valueOf(UserId));
               }
            } 
            else if (oper.equalsIgnoreCase("REM")) 
            {
               // Remove individuals from a grouping
               String[] mrks = req.getParameterValues("incl_mark");
               if (mrks != null) 
               {
                   m.DeleteMarkerSetLinks(conn, Integer.valueOf(msid), mrks, Integer.valueOf(UserId));
               }
            }
            
            conn.commit();
            writeMemberPage(conn, req, oper, pid, suid, msid, out);
            
         } 
         catch (Exception e) 
         {
             e.printStackTrace(System.err);
             try 
             {
               conn.rollback();
               out.println("<pre>Error:\nUnhandled database exception!\n"
                           + e.getMessage()
                           +"\nEnd error.</pre>");
            } 
            catch (SQLException ignored) 
            {} 
         }
      } else if (oper.equalsIgnoreCase("SELECT") ||
                 oper.equalsIgnoreCase("DISPLAY")) {
         writeMemberPage(conn, req, oper, pid, suid, msid, out);
      } else {
         writeMemberPage(conn, req, oper, pid, suid, msid, out);
      }
      out.println("</td></tr></table>");
      out.println("</body>");
      out.println("</html>");
   }


   private void writeMemberPage(Connection conn,
                                HttpServletRequest req,
                                String oper,
                                String pid,
                                String suid,
                                String msid,
                                PrintWriter out) {
      Statement stmt = null;
      ResultSet rset = null;
      HttpSession session = req.getSession(true);
      // Set method to post to prevent that someone sees the paramaters through the url
      out.println("<form name=\"form1\" action=\""+getServletPath("viewMarkSet/membership")+"\" method=\"post\">");
      out.println("<table width=750 border=0 cellspacing=0 cellspacing=1>");
      String sid=null;
      String cid=null;
      try {


         // ********************************************************************************


         out.println("<tr>"); // the first "half" of the page

/*
 // the "bar"
         out.println("<br><br>");
         out.println( "<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 height=15 width=800 >");
         out.println("<tr><td bgcolor=oldlace width=12></td><td width=338 bgcolor=\"#008B8B\">"
                     +"<b>Available Markers </b></td>"
                     +"<td width=100 bgcolor=oldlace></td>"
                     +"<td width=350><b>Included Markers</b></td></tr></table>\n");
*/


       out.println("<tr><td style=\"WIDTH: 200px\">Available Markers<br><hr></td><td style=\"WIDTH: 200px\">Included Markers<br><hr></td></tr>");
    //    out.println("<tr><td bgcolor=\"#008B8B\" style=\"WIDTH: 50px\"></td><td width=50></td><td bgcolor=\"#008B8B\" style=\"WIDTH: 150px\"></td></tr>");


         out.println("<br><br>");
         // Available sampling units
         out.println("<td>Sampling unit<br>");
        // out.println("<tr><td>Sampling unit<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, SID, NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid + " order by NAME");
         out.println("<select name=suid width=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"suid\")'>");

/*
         while (rset.next()) {
            out.println("<option " +
                        (suid.equals(rset.getString("SUID")) ? "selected " : "") +
                        "value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }

*/

            while (rset.next()) {
            if (suid != null && suid.equals(rset.getString("SUID")))        {
               out.println("<OPTION selected value=\"" + rset.getString("SUID") + "\">" +
                           rset.getString("NAME")+ "</option>\n");
                           sid=rset.getString("SID");
            } else {
               out.println("<OPTION value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME")+"</option>\n");
               // first value read
               if(suid == null)
               {
                  suid = rset.getString("SUID");
                  sid=rset.getString("SID");
               }
            }
         }

         rset.close();
         stmt.close();
         out.println("</select>");
         //out.println("</td></TR>");
         out.println("<br>");

         // Chromosomes
//         out.println("<td>Chromosome<br>");
         out.println("Chromosome<br>");

         cid=req.getParameter("cid");
        // System.err.println("cid="+cid);
        // System.err.println("req="+req.getQueryString());

         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM gdbadm.V_CHROMOSOMES_1 " +
                                  "WHERE SID=" + sid + " order by NAME");


         out.println("<select name=cid width=200 style=\"WIDTH: 200px\" "
          + "onChange='selChanged(\"suid\")'>");
        // System.err.println("cid="+cid);
         if(cid==null || cid.equals("*") || cid.trim().equals(""))
         {
          //System.err.println("cid="+cid);
          out.println("<OPTION selected value=*>*");
          cid = "*";
         }
         else
         {
         // System.err.println("cid!=null");
           out.println("<OPTION  value=*>*");
         }

         while (rset.next()) {

            if(rset.getString("CID").equals(cid))
            {
                out.println("<OPTION selected value=\"" + rset.getString("CID") + "\">" +
                        rset.getString("NAME") + "</option>");
                       // System.err.println("cid1="+cid);
                        cid = rset.getString("CID");
                       // System.err.println("cid2="+cid);

            }
            else
            {
            out.println("<option " +
                        "value=\"" + rset.getString("CID") + "\">" +
                        rset.getString("NAME") + "</option>");
                       // System.err.println("cidin="+cid);
                       // System.err.println("ciddb="+rset.getString("CID"));
            }
         }
         rset.close();
         stmt.close();
         out.println("</select>");
//         out.println("</td>"); // slut på chromosome
         out.println("</td>"); // slut hela denna kolumn


         // ********************************************************************************
         // Available Marker sets


         out.println("<td valign=top >Marker set<br>");

         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT MSID, NAME FROM gdbadm.V_MARKER_SETS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");
         out.println("<select name=msid width=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"msid\")'>");
         while (rset.next()) {
            out.println("<option " +
                        (msid.equals(rset.getString("MSID")) ? "selected " : "" ) +
                        "value=\"" + rset.getString("MSID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         rset.close();
         stmt.close();
         out.println("</select>");
         out.println("</td></tr>");//slut kolumn 2 samt rad


         out.println("</table>");//slut tabell
         //out.println("<hr>");
//         out.println("</td></tr>");
         // ********************************************************************************
         // Available markers
         out.println("<tr><td></td><td>");

         out.println("<table><tr><td valign=middle align=left>");
         out.println("Markers<br>");
         out.println("<select name=avail_mark width=200px multiple size=15 "
                     + "style=\"WIDTH: 200px\">");
         stmt = conn.createStatement();
         StringBuffer sbSQL = new StringBuffer();
         sbSQL.append("SELECT MID, NAME FROM gdbadm.V_MARKERS_1 ");
         
         sbSQL.append("WHERE SUID=" + suid +" ");
         sbSQL.append("and mid not in (select mid from v_positions_2 where msid="+msid+") ");
         // "AND MID NOT IN (SELECT MID, NAME FROM V_POSITIONS ??WHERE PID=" + pid + ") ORDER BY NAME");

         if( cid==null || cid.equals("*"))
         {
          sbSQL.append(" ORDER BY NAME");
         }
         else
         {
          sbSQL.append(" AND CID="+cid+" ORDER BY NAME");
         }

         rset = stmt.executeQuery(sbSQL.toString());
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("MID") + "\">"
                        + rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td><td valign=middle align=middle style=\"WIDTH: 150px\">");
         // The update buttons has functionality only for those
         // with the MRSK_W privielege
         int privileges[] = (int[]) session.getValue("PRIVILEGES");


         // buttons
         out.println("<input type=button style=\"WIDTH: 30px\" name=add value=\">\" " +
                     privDependentString(privileges, MRKS_W, "onClick='add_mark()'", "disabled") +
                     ">");
         out.println("<br><br>");
         out.println("<input type=button style=\"WIDTH: 30px\" name=rem value=\"<\" " +
                     privDependentString(privileges, MRKS_W, "onClick='rem_mark()'", "disabled") +
                     ">");
         out.println("</td>");

         // ********************************************************************************
         // Included Markers
         out.println("<td valign=middle align=left>");
         out.println("Markers<br>");
         out.println("<select name=incl_mark width=200px multiple size=15 "
                     + "style=\"WIDTH: 200px\">");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT MID, MNAME " +
                                  "FROM V_POSITIONS_2 "+
                                  "WHERE MSID=" + msid + " " +
                                  "order by TO_NUMBER_ELSE_NULL(CNAME), CNAME, POSITION");
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("MID") + "\">"
                        + rset.getString("MNAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td>");
         out.println("</tr>");
         out.println("</table>");

      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
      } catch (Exception e) {
         e.printStackTrace(System.err);
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
      out.println("<input type=hidden name=oper value=\"\">");
      out.println("<input type=hidden name=item value=\"\">");
      out.println("</form>");
   }
   private void writeMemberScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function selChanged(item) {");
      out.println("     document.forms[0].oper.value='SELECT';");
      out.println("     document.forms[0].item.value = item;");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("function rem_mark() {");
      out.println("     document.forms[0].oper.value='REM';");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("");
      out.println("function add_mark() {");
      out.println("     document.forms[0].oper.value='ADD';");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("");
      out.println("");
      out.println("//-->");
      out.println("</script>");
                                        
   }
        
   /***************************************************************************************
    * *************************************************************************************
    * The distance page
    */
   private void writePosition(HttpServletRequest req, HttpServletResponse res)
      throws IOException 
   {
      String oper = null;
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      String suid, msid, pid, UserId;
      String item; // This variable tells which of the parameter that has changed
      suid = req.getParameter("suid");
      msid = req.getParameter("msid");
      pid = (String) session.getValue("PID");
      oper = req.getParameter("oper");
      UserId = (String) session.getValue("UserID");
      item = req.getParameter("item");


      res.setContentType("text/html");
      res.setHeader("Pragme", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      if (pid == null) pid = "-1";

      if (item == null) {
         // First time?
         suid = findSuid(conn, pid);
         msid = findMsid(conn, suid);
      } else if (item.trim().equals("suid")) {
         msid = findMsid(conn, suid);
      } else {
      }
      if (oper == null)
         oper = "SELECT";
      out.println("<html>");
      out.println("<head>");
      writeDistScript(out);
      out.println("<title>Marker Positions</title>");
      HTMLWriter.css(out,getURL("style/axDefault.css"));
      out.println("</head>");
      out.println("<body>");
      out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                  "<tr>" +
                  "<td width=\"14\" rowspan=\"3\"></td>" +
                  "<td width=\"736\" colspan=\"2\" height=\"15\">");
      out.println("<center>" +
                  "<b style=\"font-size: 15pt\">Marker Sets - Positions</center>" +
                  "</font></td></tr>" +
                  "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                  "</tr></table>");

      out.println("<table cellspacing=0 cellpadding=0><tr>" +
                  "<td width=15></td><td>");

      if ( (oper.equals("SET")) ) 
      {
         try 
         {
            //boolean ok = true;
            conn.setAutoCommit(false);
           
            Enumeration params = req.getParameterNames();
            String param;
            String mid;
            String dist;
            String sql;
            
            DbMarker m = new DbMarker();
            while (params.hasMoreElements()) 
            {
               param = (String) params.nextElement();
               if (param.startsWith("MID_")) 
               {
                  // This is a distance parameter!
                  try 
                  {
                     dist = req.getParameter(param);
                     mid = param.substring("MID_".length());
                     m.UpdateMarkerSetLink(conn, Integer.valueOf(msid), Integer.valueOf(mid), Double.valueOf(dist), Integer.valueOf(UserId));
                  } 
                  catch (NumberFormatException nfe) 
                  {
                      nfe.printStackTrace();
                  }
               }
            }
            
            conn.commit();
            writePositionPage(conn, req, oper, pid, suid, msid, out);
            
         }
         catch (Exception e) 
         {
            e.printStackTrace(System.err);
            try {
               conn.rollback();
               out.println("<pre>Error:\nUnhandled database exception!\n"
                           + e.getMessage()
                           +"\nEnd error.</pre>");
               
            } catch (SQLException ignored) {
            }
         } 
      } else if (oper.equalsIgnoreCase("SELECT")) {
         writePositionPage(conn, req, oper, pid, suid, msid, out);
      } else {
         writePositionPage(conn, req, oper, pid, suid, msid, out);
      }
      out.println("</td></tr></table>");
      out.println("</body>");
      out.println("</html>");
   }
   private void writePositionPage(Connection conn,
                                  HttpServletRequest req,
                                  String oper,
                                  String pid,
                                  String suid,
                                  String msid,
                                  PrintWriter out) {
      HttpSession session = req.getSession(true);
      Statement stmt = null;
      ResultSet rset = null;
      try {
         out.println("<form method=get action=\""+getServletPath("viewMarkSet/position")+"\">");


         out.println("<table border=0><tr>");
         // Available species
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid +" order by NAME");
         out.println("<td>Sampling unit<br>");
         out.println("<select name=suid width=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"suid\")'>");
         while (rset.next()) {
            out.println("<option " +
                        (suid.equals(rset.getString("SUID")) ? "selected " : "" ) +
                        "value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td>");
         rset.close();
         stmt.close();
         // Available marker sets
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, MSID FROM gdbadm.V_MARKER_SETS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");
         out.println("<td>Marker set<br>");
         out.println("<select name=msid width=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"msid\")'>");
         while (rset.next()) {
            out.println("<option " +
                        (msid.equals(rset.getString("MSID")) ? "selected " : "" ) +
                        "value=\"" + rset.getString("MSID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td>");
         rset.close();
         stmt.close();
         // The update button has functionality onlu for those
         // with the MRSK_W privielege
         int privileges[] = (int[]) session.getValue("PRIVILEGES");
         out.println("<td valign=bottom><input type=button value=Update " +
                     "width=100 style=\"WIDTH: 100px\" " +
                     privDependentString(privileges, MRKS_W, "onClick='valForm()'", "disabled") +
                     ">");
         out.println("</td>");
         out.println("</tr>");
         out.println("</table>");
         out.println("<hr><br>");
         out.println("<table cellspacing=0 cellpadding=0 width=500 style=\"WIDTH: 500px\">");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td><font color=white><b>Chromosome</b></font></td>");
         out.println("<td><font color=white><b>Marker</b></font></td>");
         out.println("<td><font color=white><b>Default position</b></font></td>");
         out.println("<td><font color=white><b>Over. position</b></font></td>");
         out.println("<td><font color=white><b>New position</b></font></td></tr>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT MNAME, MID, CNAME, DEF_POSITION, OVER_POSITION, POSITION FROM " +
                                  "V_POSITIONS_2 WHERE MSID=" + msid + " " +
                                  "ORDER BY TO_NUMBER_ELSE_NULL(CNAME), CNAME, POSITION, MNAME");
         boolean odd = true;
         while (rset.next()) {
            if (odd)
               out.println("<tr bgcolor=white>");
            else
               out.println("<tr bgcolor=lightgrey>");
            odd = !odd;
            out.println("<td>");
            out.println(rset.getString("CNAME"));
            out.println("</td>");
            out.println("<td>");
            out.println(rset.getString("MNAME"));
            out.println("</td>");
            out.println("<td>" + formatOutput(session, rset.getString("DEF_POSITION"), 6) + "</td>");
            out.println("<td>" + formatOutput(session, rset.getString("OVER_POSITION"), 6) + "</td>");
            out.println("<td><input type=text width=100 style=\"WIDTH: 100px\" " +
                        "name=\"MID_" + rset.getString("MID") + "\" " +
                        "value=\"" + replaceNull(rset.getString("OVER_POSITION"), "") + "\" " +
                        "onMouseOver='displayHelp()' onMouseOut='hideHelp()'>");
            out.println("</td></tr>");
         }
         out.println("</table>");
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("<input type=hidden name=item value=\"\">");
         out.println("</form>");

      } catch (SQLException sqle) {
         // Do something damn it!
         sqle.printStackTrace(System.err);
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (Exception ignored) {
         }
      }
   }

   private void writeDistScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function selChanged(item) {");
      out.println("     document.forms[0].oper.value='SELECT';");
      out.println("     document.forms[0].item.value = item;");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("");
      out.println("function valForm() {");
      out.println("     if (confirm('Are you sure you want to update the distances?')) {");
      out.println("             document.forms[0].oper.value='SET';");
      out.println("             document.forms[0].submit();");
      out.println("     }");
      out.println("     return (false);");
      out.println("}");
      out.println("");
      out.println("function displayHelp() {");
      out.println("     window.status='Enter the new distance as a dot separated decimal number.';");
      out.println("     return true;");
      out.println("}");
      out.println("");
      out.println("function hideHelp() {");
      out.println("     window.status = '';");
      out.println("     return true;");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
   }
   /***************************************************************************************
    * *************************************************************************************
    * The new markerset page
    */
   private void writeNew(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createMarkerSet(req, res, conn))
            writeFrame(req, res);
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
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
      String //sid = null,
         //cid = null,
         suid, newQS, pid, oper, item;
      try {
         conn = (Connection) session.getValue("conn");

         pid = (String) session.getValue("PID");
         //                     sid = req.getParameter("sid");
         //                     cid = req.getParameter("cid");
         suid= req.getParameter("suid");
         newQS = removeQSParameterOper(buildQS(req));
         oper = req.getParameter("oper");

         if (oper == null || oper.trim().equals("")) oper = "SEL_CHANGED";

         if (pid == null) pid = "-1";
         item = req.getParameter("item");
         suid = req.getParameter("suid");
         if (suid == null) suid = findSuid(conn, pid);
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<base target=\"content\">");

         writeNewScript(out);
         out.println("<title>New marker set</title>");
         //HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Marker Sets - New</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=get action=\""+ getServletPath("viewMarkSet/new?") + newQS + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         out.println("<table border=0 cellpading=0 celspaing=0>");
         out.println("<tr>");
         out.println("<td>Sampling unit<br>");
         out.println("<select name=suid WIDTH=200 style=\"WIDTH: 200px\">");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SUID FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid + " ORDER BY NAME");
         while (rset.next()) {
            out.println("<option " +
                        (suid.equals(rset.getString("SUID")) ? "selected " : "" ) +
                        "value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         rset.close();
         stmt.close();
         out.println("</select>");
         out.println("</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td>Name<br>");
         out.println("<input type=text name=n_name maxlength=20 " +
                     "width=200 style=\"WIDTH: 200px\">");
         out.println("</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td>Comment<br>");
         out.println("<textarea rows=10 cols=40 name=n_comm>");
         out.println("</textarea>");
         out.println("</td></tr>");
         out.println("</table>");

         out.println("</td></tr><tr><td></td><td></td></tr>");
         out.println("<tr><td></td><td>");
         out.println("<table cellspacing=0 cellpading=0 border=0><tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='document.location.href=\"" +
        //             getServletPath("viewMarkSet?") + newQS + "\"'>");
                     getServletPath("viewMarkSet?&RETURNING=YES") +"\"'>");


         out.println("&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Create width=100 " +
                     "style=\"WIDTH: 100px\" onClick='valForm()'>");
         out.println("&nbsp;</td></tr></table>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=item value=\"no\">");
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("<input type=hidden name=RETURNING value=YES>");

         out.println("</form>");
         out.println("</body>");
         out.println("</html>");
      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
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
      out.println("     ");
      out.println("     var rc = 1;");
      out.println("     if ( (\"\" + document.forms[0].n_name.value) != \"\") {");
      out.println("             if (document.forms[0].n_name.value.length > 20) {");
      out.println("                     alert('Name must be less than 20 characters!');");
      out.println("                     rc = 0;");
      out.println("             }");
      out.println("     }");
      //        out.println("   if ( (\"\" + document.forms[0].a.value) != \"\") {");
      //        out.println("           if (document.forms[0].a.value.length > 20) {");
      //        out.println("                   alert('Alias must be less than 20 characters!');");
      //        out.println("                   rc = 0;");
      //        out.println("           }");
      //        out.println("   }");
      out.println("     ");
      out.println("     ");
      out.println("     if (rc) {");
      out.println("             if (confirm('Are you sure that you want to create the marker?')) {");
      out.println("                     document.forms[0].oper.value = 'CREATE'");
      out.println("                     document.forms[0].submit();");
      out.println("             }");
      out.println("     }");
      out.println("     ");
      out.println("     ");
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
            // We neew the privilege MRKS_R for all these
            title = "Marker set - View & Edit";
            if ( privDependentString(privileges, MRKS_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/edit") ) {
            // We neew the privilege MRKS_W
            title = "Marker set - Edit";
            if ( privDependentString(privileges, MRKS_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/new") ) {
            // We neew the privilege MRKS_W
            title = "Marker set - New";
            if ( privDependentString(privileges, MRKS_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/impFile") ) {
            // We need the privilege MRKS_W
            title = "Marker set - File Import";
            if ( privDependentString(privileges, MRKS_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/membership") ) {
            // We need the privilege MRK_R
            title = "Markers set - Memebership";
            if ( privDependentString(privileges, MRKS_R, "", null) == null)
               ok = false;
         } else if (extPath.equals("/position") ) {
            // We need the privilege MRK_R
            title = "Markers set - Position";
            if ( privDependentString(privileges, MRKS_R, "", null) == null)
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
      } catch (Exception e)     {
         e.printStackTrace(System.err);
      } finally {
         ;
      }
   }
}
