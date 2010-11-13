/*
  $Log$
  Revision 1.8  2005/02/17 16:18:58  heto
  Converted DbUMarker to PostgreSQL
  Redesigned relations: r_uvar_var, r_umid_mid and r_uaid_aid due to errors in the design (redundant data in relations)
  This design change affected some views!

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


  Revision 1.9  2001/05/31 07:07:09  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.8  2001/05/22 06:17:02  roca
  Backfuncktionality fixed for all (?) servlets/subpages

  Revision 1.7  2001/05/14 11:22:12  frob
  Changed all methods that used to call writeError() to write an error page. These
  methods now calls the general method commitOrRollback() which does the commit or
  rollback operation as well as handle any errors. writeError() was removed.

  Revision 1.6  2001/05/03 14:21:04  frob
  Implemented local version of errorQueryString and changed writeError to use this method.

  Revision 1.5  2001/05/03 07:57:47  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.4  2001/05/03 06:34:22  frob
  Calls to removeOper, removeMid, removeSid, removeSuid and removeCid changed
  to use the general removeQSParameter.
  The previously called method is removed.

  Revision 1.3  2001/04/24 09:34:00  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:34  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.4  2001/04/18 09:32:02  frob
  createMarkFile(), createMapping(): Changed call to Parse() method to pass valid
                                     file type definitions.
                                     Removed useage of delimiter.
                                     Layout fixes.
  writeImpFilePage(), writeImpMapping(): Removed delimiter section.
                                         Resized filename field.
                                         Validated HTML.

  Revision 1.1.1.1.2.3  2001/04/02 14:17:23  frob
  Class now uses MapFileParser instead of MappingFileParser which has been
  removed.
  Exceptions during Parser() are now catched.
  Rollback will not be done if connection is not created.

  Revision 1.1.1.1.2.2  2001/03/28 13:47:59  frob
  Added catch() for InputDataFileException which can be raised
  from the parse()-method.
  Added check that the connection object is created before doing
  rollback on it (to avoid null pointer exception).

  Revision 1.1.1.1.2.1  2001/03/28 12:37:51  frob
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

public class viewUMark extends SecureArexisServlet
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
      } else if (extPath.equals("/new")) {
         writeNew(req, res);
      } else if (extPath.equals("/impFile")) {
         writeImpFile(req, res);
      } else if (extPath.equals("/impMapping")) {
         writeImpMapping(req, res);
      } else if (extPath.equals("/alleles")) {
         writeAlleles(req, res);
      } else if (extPath.equals("/newAllele")) {
         writeNewAllele(req, res);
      } else if (extPath.equals("/editAllele")) {
         writeEditAllele(req, res);
      } else if (extPath.equals("/detailsAllele")) {
         writeAlleleDetails(req, res);
      } else if (extPath.equals("/impMultipart")) {
         createMarkFile(req, res);
      } else if (extPath.equals("/impMultipartMapping")) {
         createMappings(req, res);
      } else if (extPath.equals("/mapping")) {
         writeMapping(req, res);
      } else if (extPath.equals("/newMapp")) {
         writeNewMapping(req, res);
      } else if (extPath.equals("/alleleMapp")) {
         writeAlleleMapping(req, res);
         //    } else if (extPath.equals("/newAlleleMapp")) {
         //      writeNewAlleleMapping(req, res);
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

//         String incoming = req.getQueryString();
        // Check if redirection is needed
        res = checkRedirectStatus(req,res); 
        //req=getServletState(req,session);

         String topQS = buildQS(req);
         // we don't want the oper parameter anaywhere but in the "edit" and "new" page!
         topQS = removeQSParameterOper(topQS);

         String bottomQS = topQS.toString();
         out.println("<html>"
                     + "<HEAD>"
                     + " <TITLE>View Unified Markers units</TITLE>"
                     + "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\"></HEAD>"
                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" //

                     + "<frame name=\"viewtop\" "
                     + "src=\"" +getServletPath("viewUMark/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewMiddle\" "
                     + "src=\""+ getServletPath("viewUMark/middle?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewbottom\""
                     + "src=\"" +getServletPath("viewUMark/bottom?") + bottomQS + "\" "
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
         sid = null,
         cid = null, // Chromosome id
         umid = null,
         orderby = null;
      String item; // Tells which paramater that has changed.
      String pid = (String) session.getValue("PID");
      sid = req.getParameter("sid");
      cid = req.getParameter("cid");
      umid = req.getParameter("umid");
      item = req.getParameter("item");

      if (item == null || sid == null) {
         sid = findSid(conn, pid);
         cid = "*" ; //findCid(conn, sid);
      } else if (item.equals("sid")) {
         cid = "*"; //findCid(conn, sid);
      } else if (item.equals("no")) {
         // No changes in the selection
      }

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
         output.append(setIndecis(sid, cid, item, action, req, session));
      output.append("&cid=").append(cid);
      if(sid != null)
      {
         output.append("&sid=").append(sid);
      }
      if (umid != null)
         output.append("&umid=").append(umid);
      if (req.getParameter("oper") != null)
         output.append("&oper=").append(req.getParameter("oper"));

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
      try 
      {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID FROM V_SPECIES_2 WHERE " +
                                  "PID=" + pid + " ORDER BY NAME");
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
         rset = stmt.executeQuery("SELECT SUID FROM V_ENABLED_SAMPLING_UNITS_2 WHERE " +
                                  "PID=" + pid + " ORDER BY NAME");
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

   private String findCid(Connection conn, String sid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID FROM V_CHROMOSOMES_1 WHERE " +
                                  "SID=" + sid + " ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME");
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
   private String findCidFromSuid(Connection conn, String suid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID FROM V_CHROMOSOMES_1 WHERE " +
                                  "SID=(SELECT SID FROM V_ENABLED_SAMPLING_UNITS_1 WHERE SUID=" +
                                  suid + ") ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME");
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

   private String setIndecis(String sid, String cid, String item_changed, String action, HttpServletRequest req, HttpSession session) {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(sid, cid, req, session);
      maxRows = getMaxRows(session);
      if (req.getParameter("STARTINDEX") != null &&
          item_changed != null &&
          item_changed.equals("no") )
         startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
      if (rows > 0 && startIndex == 0) startIndex = 1;
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

   private int countRows(String sid, String cid, HttpServletRequest req, HttpSession session) {
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer(512);
      String pid = (String) session.getValue("PID");
      try {
         sbSQL.append("SELECT count(*) " +
                      "FROM gdbadm.V_U_MARKERS_1 WHERE " +
                      "PID=" + pid + " AND SID=" + sid + " ");
         if (!"*".equals(cid))
            sbSQL.append("AND CID=" + cid + " ");
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
      String orderby = null;

      StringBuffer filter = new StringBuffer(256);
      //                orderby = req.getParameter("ORDERBY");
      //
      //                if (orderby != null && !"".equalsIgnoreCase(orderby)) {
      //                        if (orderby.equals("CNAME"))
      //                                filter.append(" order by gdbadm.TO_NUMBER_ELSE_NULL(CNAME), CNAME");
      //                        else
      //                                filter.append(" order by " + orderby);
      //                } else
      //                        filter.append(" order by NAME");

      // Replace every occurence of '*' with '%' and return the string
      // (Oracle uses '%' as wildcard while '%' demands some specail treatment
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
      HttpSession session = req.getSession(true);
      Connection conn = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");

      String oper;
      Statement stmt = null;
      ResultSet rset = null;
      int startIndex = 0, rows = 0, maxRows = 0;
      String cid, marker, orderby, oldQS, newQS, action, pid, sid;
      oper = req.getParameter("oper");
      int privileges[] = (int[]) session.getValue("PRIVILEGES");
      try {
         conn = (Connection) session.getValue("conn");
         if (oper == null || "".equals(oper))
            oper = "SELECT";

         pid = (String) session.getValue("PID");
         sid= req.getParameter("sid");
         cid = req.getParameter("cid");
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
         if (cid == null) cid = "-1";
         if (orderby == null) orderby = "NAME";
         if (action == null) action = "NOP";
         if (pid == null || "".equalsIgnoreCase(pid))
            pid = "-1";

         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+ getURL("style/view.css") +"\">");
         out.println("<base target=\"content\">");

         writeTopScript(out);
         out.println("<title>View Unified Markers</title>");
         out.println("</head>");

         out.println("<body bgcolor=\"#ffffd0\">"
                     +"<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
                     +"<tr>"
                     +"<td width=\"14\" rowspan=\"3\"></td>"
                     +"<td width=\"736\" colspan=\"2\" height=\"15\">"
                     +"<form method=get action=\"" +getServletPath("viewUMark")+"\">"
                     +"<p align=\"center\"><font size=\"2\"><b  style=\"font-size: 15pt\">Unified Markers</b>"
                     +"</font></td></tr>"
                     +"<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>"
                     +"</tr><tr><td width=\"517\">");

         out.println("<table width=488 height=\"92\">"
                     +"<td><b>Species</b><br><select name=sid "
                     +"name=select onChange='selChanged(\"sid\");'  style=\"HEIGHT: 22px; WIDTH: 126px\">");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SID FROM V_SPECIES_2 " +
                                  "WHERE PID="+ pid + " ORDER BY NAME");
         while (rset.next()) {
            if (sid != null && sid.equals(rset.getString("SID")))        {
               out.println("<OPTION selected value=\"" + rset.getString("SID") + "\">" +
                           rset.getString("NAME")+ "</option>\n");
            } else {
               out.println("<OPTION value=\"" + rset.getString("SID") + "\">" + rset.getString("NAME")+"</option>\n");
               // first value read
               if(sid == null)
                  sid = rset.getString("SID");
            }
         }
         rset.close();
         stmt.close();
         out.println("</SELECT></td>");

         out.println("<td><b>Chromosome</b><br><select name=cid " +
                     "width=126 style=\"HEIGHT: 22px; WIDTH: 126px\">");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM V_CHROMOSOMES_1 WHERE " +
                                  "SID=" + sid + " ORDER BY " +
                                  "TO_NUMBER_ELSE_NULL(NAME), NAME");
         out.println("<option " +
                     ("*".equals(cid) ? "selected " : "") +
                     "value=\"*\">*</option>");
         while (rset.next()) {
            if (cid != null && cid.equals(rset.getString("CID")))
               out.println("<OPTION selected value=\"" + rset.getString("CID") + "\">" +
                           rset.getString("NAME"));
            else
               out.println("<OPTION value=\"" + rset.getString("CID") + "\">" + rset.getString("NAME"));
         }
         out.println("</SELECT></td>");
         out.println("</td></table></td>");

         //Buttons
         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");
         out.println("<input type=button value=\"New Unified Marker\" " +
                     privDependentString(privileges, UMRK_W,
                                         " onClick='parent.location.href=\"" +getServletPath("viewUMark/new?") + newQS + "\"' ",
                                         "disabled ") +
                     "height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">");
         out.println("</td>");

         out.println("<tr><td width=68 colspan=2>"
                     +"<input id=COUNT name=COUNT type=submit value=\"Count\" width=\"69\""
                     +" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">"
                     +"</td>"
                     +"<td width=68 colspan=2>"
                     +"<input id=DISPLAY name=DISPLAY type=submit value=\"Display\""
                     +" width=\"70\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">"
                     +"</td></tr>");


         // some hidden values
         // THESE ARE THE ORIINAL FOR VIEW MARK..
         out.println("<input type=\"hidden\" id=\"STARTINDEX\" name=\"STARTINDEX\" value=\"" + startIndex + "\">");
         out.println("<input type=\"hidden\" id=\"ORDERBY\" name=\"ORDERBY\" value=\"" + orderby + "\">");
         out.println("<input type=\"hidden\" id=\"oper\" name=\"oper\" value=\"\">");
         out.println("<input type=hidden name=item value=no>");

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

      } catch (Exception e)     {
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
      out.println("var MAX_LENGTH = 20;");
      out.println("var MIN_LENGTH = 1;");
      out.println("function selChanged(item) {");
      out.println("     document.forms[0].oper.value='SEL_CHANGED';");
      out.println("     document.forms[0].item.value=item;");
      out.println("     document.forms[0].submit();");
      out.println("}");
      out.println("");
      out.println("");
      out.println("");
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

         if(action != null)
         {
            out.println("&nbsp;" + buildInfoLine(action, startIndex, rows, maxRows));
         }

         String oldQS, newQS;
         oldQS = req.getQueryString();
         String choosen= req.getParameter("ORDERBY");
         newQS = buildTopQS(oldQS);
         /*
           out.println("<table bgcolor=\"#008B8B\" border=0 cellpading=0 cellspacing=0 height=20 width=805 style=\"margin-left:2px\">");
           out.println("<td width=5>&nbsp;</td>");
         */
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=805 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");

         // the menu choices
         //chromosome
         out.println("<td width=100><a href=\"" + getServletPath("viewUMark?item=no&ACTION=DISPLAY&") + newQS + "&ORDERBY=CNAME\">");
         if(choosen.equals("CNAME"))
            out.println("<FONT color=saddlebrown ><b>Chrom</b></FONT></a></td>\n");
         else out.println("Chrom</a></td>\n");
         //Unified Marker
         out.println("<td width=100><a href=\"" + getServletPath("viewUMark?item=no&ACTION=DISPLAY&") + newQS + "&ORDERBY=NAME\">");
         if(choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown><b>U. Marker</b></FONT></a></td>\n");
         else out.println("U. Marker</a></td>\n");
         //Comment
         out.println("<td width=100><a href=\"" + getServletPath("viewUMark?item-=no&ACTION=DISPLAY&") + newQS + "&ORDERBY=COMM\">");
         if(choosen.equals("COMM"))
            out.println("<FONT color=saddlebrown><b>Comment</b></FONT></a></td>\n");
         else out.println("Comment</a></td>\n");
         //USER
         out.println("<td width=100><a href=\"" + getServletPath("viewUMark?item=no&ACTION=DISPLAY&") + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
         else out.println("User</a></td>\n");

         //Updated
         out.println("<td width=120><a href=\"" + getServletPath("viewUMark?item=no&ACTION=DISPLAY&") + newQS + "&ORDERBY=TS\">");
         if(choosen.equals("TS"))
            out.println("<FONT color=saddlebrown><b>Updated</b></FONT></a></td>\n");
         else out.println("Updated</a></td>\n");
         /*
           out.println("<td width=60>&nbsp;</td>"); // Mapping link
           out.println("<td width=60>&nbsp;</td>"); // Alleles link
           out.println("<td width=80>&nbsp;</td>"); // Details
           out.println("<td width=80>&nbsp;</td>"); // Edit
           out.println("</table>");
           out.println("</body></html>");
         */
         out.println("<td width=60>&nbsp;</td>"); // Mapping link
         out.println("<td width=60>&nbsp;</td>"); // Alleles link
         out.println("<td width=80>&nbsp;</td>"); // Details
         out.println("<td width=80>&nbsp;</td>"); // Edit
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
         String pid = null, cid = null, sid = null,  action = null;
         String oldQS = req.getQueryString();
         action = req.getParameter("ACTION");
         sid = req.getParameter("sid");
         cid = req.getParameter("cid");
         pid = (String) session.getValue("PID");
         if (pid == null) pid = "-1";
         if (action == null || action.equalsIgnoreCase("NOP") ||
             action.equalsIgnoreCase("COUNT") ||
             sid == null || cid == null)
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

         sbSQL.append("SELECT CNAME, NAME, COMM, " +
                      "USR, to_char(TS, '" + getDateFormat(session) +"') as TC_TS, UMID " +
                      "FROM gdbadm.V_U_MARKERS_3 WHERE " +
                      "PID=" + pid + " AND SID=" + sid + " ");
         if (!"*".equals(cid))
            sbSQL.append("AND CID=" + cid+ " ");

         String qs = req.getQueryString();
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);
         String orderby = req.getParameter("ORDERBY");
         if (orderby != null && !"".equalsIgnoreCase(orderby)) {
            if (orderby.equals("CNAME"))
               sbSQL.append(" order by TO_NUMBER_ELSE_NULL(CNAME), CNAME");
            else
               sbSQL.append(" order by " + orderby);
         } else
            sbSQL.append(" order by NAME");
         rset = stmt.executeQuery(sbSQL.toString());
         out.println("<TABLE align=left border=0 cellpading=0");
         out.println("cellspacing=0 width=805 style=\"margin-left: 2px\">");
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
            out.println("<TD width=5>&nbsp</TD>");
            out.println("<TD width=100>" + rset.getString("CNAME") + "</TD>");
            out.println("<TD width=100>" + rset.getString("NAME") + "</TD>");
            out.println("<TD width=100>" + formatOutput(session, rset.getString("COMM"),12) + "</TD>");
            out.println("<TD width=100>"+ rset.getString("USR") + "</TD>");
            out.println("<TD width=120>"+ rset.getString("TC_TS") + "</TD>");

            out.println("<td width=70>");
            out.println(privDependentString(privileges, UMRK_W,
                                            "<a href=\"" + getServletPath("viewUMark/mapping?") + "umid=" +
                                            rset.getString("UMID") + "&" + oldQS + "\" target=\"content\">Mapping</a>",
                                            "<font color=tan>Mapping</font>"));
            out.println("</td>");


            out.println("<td width=70>");
            out.println(privDependentString(privileges, UMRK_R,
                                            "<a href=\"" + getServletPath("viewUMark/alleles?") + "umid=" +
                                            rset.getString("UMID") + "&" + oldQS + "\" target=\"content\">Alleles</a>",
                                            "<font color=tan>Alleles</font>"));
            out.println("</td>");

            out.println("<TD WIDTH=70>");
            out.println(privDependentString(privileges, UMRK_R,
                                            "<A HREF=\"" +getServletPath("viewUMark/details?")+"umid="
                                            + rset.getString("UMID") + "&" + oldQS + "\" target=\"content\">Details</A>",
                                            "<font color=tan>Details</font>"));
            out.println("</TD>");

            out.println("<TD WIDTH=70>");
            out.println(privDependentString(privileges, UMRK_W,
                                            "<A HREF=\""+getServletPath("viewUMark/edit?")+"umid="
                                            + rset.getString("UMID") + "&" + oldQS + "\" target=\"content\">Edit</A>",
                                            "<font color=tan>Edit</font>"));
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
    * The marker's detail page
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
      String prev_chrom = null;
      String prev_position = null;
      String prev_name = null;
      String prev_alias = null;
      String prev_comm = null;
      String prev_usr = null;
      String prev_ts = null;
      String curr_chrom = null;
      String curr_position = null;
      String curr_name = null;
      String curr_alias = null;
      String curr_comm = null;
      String curr_usr = null;
      String curr_ts = null;
      boolean has_history = false;

      try {

         String oldQS = buildQS(req);
         String umid = req.getParameter("umid");
         if (umid == null) umid = "-1";

         conn = (Connection) session.getValue("conn");
         // Get the current data
         stmt_curr = conn.createStatement();
         String strSQL = "SELECT SNAME, CNAME, NAME, ALIAS, COMM, " +
            "POSITION, USR, " +
            "to_char(TS, '"+ getDateFormat(session) + "') as TC_TS "
            + "FROM V_U_MARKERS_3 WHERE "
            + "UMID=" + umid ;
         rset_curr = stmt_curr.executeQuery(strSQL);

         // Get the history
         stmt_hist = conn.createStatement();
         strSQL = "SELECT NAME, ALIAS, POSITION, CNAME, COMM, USR, " +
            "to_char(TS , '" + getDateFormat(session) + "') as TC_TS, TS as dummy " +
            "FROM V_U_MARKERS_LOG " +
            "WHERE UMID=" + umid + " " +
            "ORDER BY dummy desc";


         rset_hist = stmt_hist.executeQuery(strSQL);

         // We baldly assume that this is alright (why shouldn't we?)
         rset_curr.next();
         curr_name = rset_curr.getString("NAME");
         curr_alias = rset_curr.getString("ALIAS");
         curr_position = rset_curr.getString("POSITION");
         curr_chrom = rset_curr.getString("CNAME");
         curr_comm = rset_curr.getString("COMM");
         curr_usr = rset_curr.getString("USR");
         curr_ts = rset_curr.getString("TC_TS"); // Time stamp

         if (rset_hist.next()) {
            prev_name = rset_hist.getString("NAME");
            prev_alias = rset_hist.getString("ALIAS");
            prev_position = rset_hist.getString("POSITION");
            prev_chrom = rset_hist.getString("CNAME");
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
                     "<b style=\"font-size: 15pt\">Unified Markers - Details</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td></td></tr>");
         out.println("<tr><td></td><td>");

         out.println("<table nowrap border=0 cellSpacing=0>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
         out.println("<tr><td>Species</td><td>" + rset_curr.getString("SNAME") + "</td></tr>");
         out.println("</table><br><br>");

         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");

         out.println("<table nowrap align=center border=0 cellSpacing=0 width=600px>");
         out.println("<tr bgcolor=black><td align=center colspan=9><b><font color=\"#ffffff\">Current Data</font></b></td></tr>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td nowrap WIDTH=100>Name</td>");
         out.println("<td nowrap WIDTH=100>Alias</td>");
         out.println("<td nowrap width=40>Position</td>");
         out.println("<td width=50>Chrom</td>");
         out.println("<td nowrap WIDTH=140>Comment</td>");
         out.println("<td nowrap WIDTH=50>User</td>");
         out.println("<td nowrap WIDTH=120>Last updated</td></tr>");

         out.println("<tr bgcolor=white>");
         // Name
         out.println("<td>");
         if (("" + curr_name).equals("" + prev_name))
            out.println(formatOutput(session, curr_name, 12));
         else
            out.println("<font color=red>" + formatOutput(session, curr_name, 12) + "</font>");
         out.println("</td>");
         // Alias
         out.println("<td>");
         if (("" + curr_alias).equals("" + prev_alias))
            out.println(formatOutput(session, curr_alias, 12));
         else
            out.println("<font color=red>" + formatOutput(session, curr_alias, 12) + "</font>");
         out.println("</td>");
         // Position
         out.println("<td>");
         if (("" + curr_position).equals("" + prev_position))
            out.println(formatOutput(session, curr_position, 4));
         else
            out.println("<font color=red>" + formatOutput(session, curr_position, 4) + "</font>");
         out.println("</td>");
         // Chromosome
         out.println("<td>");
         if (("" + curr_chrom).equals("" + prev_chrom))
            out.println(formatOutput(session, curr_chrom, 3));
         else
            out.println("<font color=red>" + formatOutput(session, curr_chrom, 4) + "</font>");
         out.println("</td>");
         // Comment
         out.println("<td>");
         if (("" + curr_comm).equals("" + prev_comm))
            out.println(formatOutput(session, curr_comm, 15));
         else
            out.println("<font color=red>" + formatOutput(session, curr_comm, 14) + "</font>");
         out.println("</td>");
         // User
         out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
         out.println("<td>" + formatOutput(session, curr_ts, 16) + "</td>"); // Last updated
         out.println("</tr>");
         out.println("<tr bgcolor=Black>");
         out.println("<td align=center colspan=9><b><font color=\"#ffffff\">History</font></b></td></tr>");

         curr_name = prev_name;
         curr_alias = prev_alias;
         curr_position = prev_position;
         curr_chrom = prev_chrom;
         curr_comm = prev_comm;
         curr_usr = prev_usr;
         curr_ts = prev_ts;

         boolean odd = true;
         while (rset_hist.next()) {
            prev_name = rset_hist.getString("NAME");
            prev_alias = rset_hist.getString("ALIAS");
            prev_position = rset_hist.getString("POSITION");
            prev_chrom = rset_hist.getString("CNAME");
            prev_comm = rset_hist.getString("COMM");
            prev_usr = rset_hist.getString("USR");
            prev_ts = rset_hist.getString("TC_TS");
            if (odd)
               out.println("<tr bgcolor=white>");
            else
               out.println("<tr bgcolor=lightgrey>");
            odd = !odd;
            // Name
            out.println("<td>");
            if (("" + curr_name).equals("" + prev_name))
               out.println(formatOutput(session, curr_name, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_name, 12) + "</font>");
            out.println("</td>");
            // Alias
            out.println("<td>");
            if (("" + curr_alias).equals("" + prev_alias))
               out.println(formatOutput(session, curr_alias, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_alias, 12) + "</font>");
            out.println("</td>");
            // Position
            out.println("<td>");
            if (("" + curr_position).equals("" + prev_position))
               out.println(formatOutput(session, curr_position, 4));
            else
               out.println("<font color=red>" + formatOutput(session, curr_position, 4) + "</font>");
            out.println("</td>");
            // Chromosome
            out.println("<td>");
            if (("" + curr_chrom).equals("" + prev_chrom))
               out.println(formatOutput(session, curr_chrom, 3));
            else
               out.println("<font color=red>" + formatOutput(session, curr_chrom, 4) + "</font>");
            out.println("</td>");
            // Comment
            out.println("<td>");
            if (("" + curr_comm).equals("" + prev_comm))
               out.println(formatOutput(session, curr_comm, 14));
            else
               out.println("<font color=red>" + formatOutput(session, curr_comm, 15) + "</font>");
            out.println("</td>");
            // User
            out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
            out.println("<td>" + formatOutput(session, curr_ts, 16) + "</td>"); // Last updated
            out.println("</tr>");

            curr_name = prev_name;
            curr_alias = prev_alias;
            curr_position = prev_position;
            curr_chrom = prev_chrom;
            curr_comm = prev_comm;
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
            // Name
            out.println("<td>");
            if (("" + curr_name).equals("" + prev_name))
               out.println(formatOutput(session, curr_name, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_name, 12) + "</font>");
            out.println("</td>");
            // Alias
            out.println("<td>");
            if (("" + curr_alias).equals("" + prev_alias))
               out.println(formatOutput(session, curr_alias, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_alias, 12) + "</font>");
            out.println("</td>");
            // Position
            out.println("<td>");
            if (("" + curr_position).equals("" + prev_position))
               out.println(formatOutput(session, curr_position, 4));
            else
               out.println("<font color=red>" + formatOutput(session, curr_position, 4) + "</font>");
            out.println("</td>");
            // Chromosome
            out.println("<td>");
            if (("" + curr_chrom).equals("" + prev_chrom))
               out.println(formatOutput(session, curr_chrom, 3));
            else
               out.println("<font color=red>" + formatOutput(session, curr_chrom, 4) + "</font>");
            out.println("</td>");
            // Comment
            out.println("<td>");
            if (("" + curr_comm).equals("" + prev_comm))
               out.println(formatOutput(session, curr_comm, 14));
            else
               out.println("<font color=red>" + formatOutput(session, curr_comm, 15) + "</font>");
            out.println("</td>");
            // User
            out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
            out.println("<td>" + formatOutput(session, curr_ts, 16) + "</td>"); // Last updated
            out.println("</tr>");
         }

         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         // Back button
         out.println("<form>");
         out.println("<table cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                //     getServletPath("viewUMark?item=no&ACTION=DISPLAY&") + oldQS + "\"'>&nbsp;");
                    getServletPath("viewUMark?&RETURNING=YES")+ "\"'>&nbsp;");


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

   private void writeAlleleDetails(HttpServletRequest req, HttpServletResponse res)
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
         String uaid = req.getParameter("uaid");
         if (uaid == null) uaid = "-1";

         conn = (Connection) session.getValue("conn");
         // Get the current data
         stmt_curr = conn.createStatement();
         String strSQL = "SELECT SNAME, CNAME, UMNAME, NAME, COMM, " +
            "USR, to_char(TS, '"+ getDateFormat(session) + "') as TC_TS "
            + "FROM V_U_ALLELES_4 WHERE "
            + "UAID=" + uaid ;
         rset_curr = stmt_curr.executeQuery(strSQL);

         // Get the history
         stmt_hist = conn.createStatement();
         strSQL = "SELECT NAME, COMM, USR, " +
            "to_char(TS , '" + getDateFormat(session) + "') as TC_TS, TS as dummy " +
            "FROM V_U_ALLELES_LOG " +
            "WHERE UAID=" + uaid + " " +
            "ORDER BY dummy desc";

         rset_hist = stmt_hist.executeQuery(strSQL);

         // We baldly assume that this is alright (why shouldn't we?)
         rset_curr.next();
         curr_name = rset_curr.getString("NAME");
         curr_comm = rset_curr.getString("COMM");
         curr_usr = rset_curr.getString("USR");
         curr_ts = rset_curr.getString("TC_TS"); // Time stamp

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
                     "<b style=\"font-size: 15pt\">Unified Markers - Alleles - Details</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td></td></tr>");
         out.println("<tr><td></td><td>");

         out.println("<table nowrap border=0 cellSpacing=0>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
         out.println("<tr><td>Species</td><td>" + rset_curr.getString("SNAME") + "</td></tr>");
         out.println("<tr><td>Chromosome</td><td>" + rset_curr.getString("CNAME") + "</td></tr>");
         out.println("<tr><td>Unified Marker</td><td>" + rset_curr.getString("UMNAME") + "</td></tr>");
         out.println("</table><br><br>");

         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");

         out.println("<table nowrap align=center border=0 cellSpacing=0 width=650px>");
         out.println("<tr bgcolor=black><td align=center colspan=4><b><font color=\"#ffffff\">Current Data</font></b></td></tr>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td nowrap WIDTH=150>Name</td>");
         out.println("<td nowrap WIDTH=250>Comment</td>");
         out.println("<td nowrap WIDTH=100>User</td>");
         out.println("<td nowrap WIDTH=150>Last updated</td></tr>");

         out.println("<tr bgcolor=white>");
         // Name
         out.println("<td>");
         if (("" + curr_name).equals("" + prev_name))
            out.println(formatOutput(session, curr_name, 16));
         else
            out.println("<font color=red>" + formatOutput(session, curr_name, 16) + "</font>");
         out.println("</td>");
         // Comment
         out.println("<td>");
         if (("" + curr_comm).equals("" + prev_comm))
            out.println(formatOutput(session, curr_comm, 25));
         else
            out.println("<font color=red>" + formatOutput(session, curr_comm, 25) + "</font>");
         out.println("</td>");
         // User
         out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
         out.println("<td>" + formatOutput(session, curr_ts, 18) + "</td>"); // Last updated
         out.println("</tr>");
         out.println("<tr bgcolor=Black>");
         out.println("<td align=center colspan=4><b><font color=\"#ffffff\">History</font></b></td></tr>");

         curr_name = prev_name;
         curr_comm = prev_comm;
         curr_usr = prev_usr;
         curr_ts = prev_ts;

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
            // Name
            out.println("<td>");
            if (("" + curr_name).equals("" + prev_name))
               out.println(formatOutput(session, curr_name, 16));
            else
               out.println("<font color=red>" + formatOutput(session, curr_name, 16) + "</font>");
            out.println("</td>");
            // Comment
            out.println("<td>");
            if (("" + curr_comm).equals("" + prev_comm))
               out.println(formatOutput(session, curr_comm, 25));
            else
               out.println("<font color=red>" + formatOutput(session, curr_comm, 25) + "</font>");
            out.println("</td>");
            // User
            out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
            out.println("<td>" + formatOutput(session, curr_ts, 18) + "</td>"); // Last updated
            out.println("</tr>");

            curr_name = prev_name;
            curr_comm = prev_comm;
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
            // Name
            out.println("<td>");
            if (("" + curr_name).equals("" + prev_name))
               out.println(formatOutput(session, curr_name, 16));
            else
               out.println("<font color=red>" + formatOutput(session, curr_name, 16) + "</font>");
            out.println("</td>");
            // Comment
            out.println("<td>");
            if (("" + curr_comm).equals("" + prev_comm))
               out.println(formatOutput(session, curr_comm, 25));
            else
               out.println("<font color=red>" + formatOutput(session, curr_comm, 25) + "</font>");
            out.println("</td>");
            // User
            out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
            out.println("<td>" + formatOutput(session, curr_ts, 18) + "</td>"); // Last updated
            out.println("</tr>");
         }

         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         // Back button
         out.println("<form>");
         out.println("<table cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("viewUMark/alleles?item=no&") + oldQS + "\"'>&nbsp;");
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

   private void writeAlleles(HttpServletRequest req, HttpServletResponse res)
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
      String umname;
      String newQS, umid;
      int privileges[] = (int[]) session.getValue("PRIVILEGES");
      try {
         conn = (Connection) session.getValue("conn");
         umid = req.getParameter("umid");
         newQS = removeQSParameterOper(req.getQueryString());

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Unified Alleles</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Unified Markers - Alleles</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr><td width=5></td><td>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         // Find unified marker name
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME FROM V_U_MARKERS_1 WHERE UMID=" + umid);
         if (rset.next())
            umname = rset.getString("NAME");
         else
            umname = "(unkown)";
         rset.close();
         stmt.close();
         out.println("<p>Alleles for the unified marker " + umname + "</p>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         // Allele table
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td width=100><font color=white>Name</font></td>");
         out.println("<td width=150><font color=white>Comment</font></td>");
         out.println("<td width=80><font color=white>&nbsp;</font></td>"); // Details
         out.println("<td width=80><font color=white>&nbsp;</font></td>"); // edit
         out.println("</tr>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, COMM, UAID FROM V_U_ALLELES_1 WHERE " +
                                  "UMID=" + umid + " ORDER BY NAME");
         boolean odd = true;
         while (rset.next() ) {
            if (odd)
               out.println("<tr bgcolor=white>");
            else
               out.println("<tr bgcolor=lightgrey>");
            odd = !odd;
            out.println("<td>" + formatOutput(session, rset.getString("NAME"), 12) + "</td>");
            out.println("<td>" + formatOutput(session, rset.getString("COMM"), 14) + "</td>");
            out.println("<td><a href=\"" + getServletPath("viewUMark/detailsAllele?") +
                        "uaid=" + rset.getString("UAID") + "&" + newQS + "\">Details</a></td>");
            out.println("<td>" +
                        privDependentString(privileges, UMRK_W,
                                            "<a href=\"" + getServletPath("viewUMark/editAllele?") +
                                            "uaid=" + rset.getString("UAID") + "&" + newQS + "\">Edit</a>",
                                            "<font color=tan>Edit</font>") + "</td>");
            out.println("</tr>");
         }

         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         out.println("<form>");
         out.println("<table cellspacing=0 cellpading=0 border=0><tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='document.location.href=\"" +
                     getServletPath("viewUMark?") + newQS + "\"'>");
         out.println("&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Create width=100 " +
                     "style=\"WIDTH: 100px\" " +
                     privDependentString(privileges, UMRK_W,
                                         "onClick='location.href=\"" + getServletPath("viewUMark/newAllele?") + newQS + "\";'",
                                         "disabled") + ">");
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

   private void writeNewAllele(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createAllele(req, res, conn)) {
            writeAlleles(req, res);
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
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");

      String newQS, umid;
      try {
         conn = (Connection) session.getValue("conn");
         umid = req.getParameter("umid");
         newQS = removeQSParameterOper(req.getQueryString());

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeNewAlleleScript(out);
         out.println("<title>New allele</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Genotypes - Unified Markers - Edit - Create Allele</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" +
                     getServletPath("viewUMark/newAllele?") + newQS + "\">");
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
                     getServletPath("viewUMark/edit?") + newQS + "\"'>");
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


   /***************************************************************************************
    * *************************************************************************************
    * The new marker page
    */
   private void writeNew(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createUMarker(req, res, conn))
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

      String cid = null,
         sid = null,
         newQS,
         pid,
         oper,
         item;
      try {
         conn = (Connection) session.getValue("conn");

         pid = (String) session.getValue("PID");
         sid = req.getParameter("sid");
         cid = req.getParameter("cid");
         newQS = removeQSParameterOper(buildQS(req));
         newQS = removeQSParameterSid(newQS);
         newQS = removeQSParameterCid(newQS);
         oper = req.getParameter("oper");

         if (oper == null) oper = "SEL_CHANGED";

         if (pid == null) pid = "-1";
         item = req.getParameter("item");
         if (item == null) {
            sid = findSid(conn, pid);
            cid = findCid(conn, sid);
         } else if (item.equals("sid")) {
            cid = findCid(conn, sid);
         } else if (item.equals("no")) {
                                // No changes in the selection
         }
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<base target=\"content\">");

         writeNewScript(out);

         out.println("<title>New unified marker</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Genotypes - Unified Markers - New</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("viewUMark/new?") + newQS + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         out.println("<table border=0 cellpading=0 celspaing=0>");
         out.println("<tr>");
         out.println("<td>Sampling unit<br>");
         out.println("<select name=sid WIDTH=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"sid\")'>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SID FROM gdbadm.V_SPECIES_2 " +
                                  "WHERE PID=" + pid + " ORDER BY NAME");
         while (rset.next()) {
            out.println("<option " + (sid.equals(rset.getString("SID")) ? "selected " : "") +
                        "value=\"" + rset.getString("SID") + "\">" + rset.getString("NAME") +
                        "</option>");
         }
         rset.close();
         stmt.close();
         out.println("</select>");
         out.println("</td>");
         out.println("<td>Chromosome<br>");
         out.println("<select name=cid WIDTH=200 style=\"WIDTH: 150px\">");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM V_CHROMOSOMES_1 WHERE " +
                                  "SID=" + sid + " ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME");
         while (rset.next()) {
            out.println("<option " + (cid.equals(rset.getString("CID")) ? "selected " : "") +
                        "value=\"" + rset.getString("CID") + "\">" + rset.getString("NAME") +
                        "</option>");
         }
         rset.close();
         stmt.close();
         out.println("</select>");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<td>Name<br>");
         out.println("<input type=text name=n maxlength=20 width=200 style=\"WIDTH: 200px\">");
         out.println("</td>");
         out.println("<td>Alias<br>");
         out.println("<input type=text name=a maxlength=20 width=200 style=\"WIDTH: 200px\">");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<td>Position<br>");
         out.println("<input type=text name=position width=200 style=\"WIDTH: 200px\">");
         out.println("</td>");
         out.println("<td></td></tr>");
         out.println("<tr>");
         out.println("<td colspan=2>Comment<br>");
         out.println("<textarea rows=8 cols=45 name=c></textarea>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("</td></tr><tr><td></td><td></td></tr>");
         out.println("<tr><td></td><td>");
         out.println("<table cellspacing=0 cellpading=0 border=0><tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='document.location.href=\"" +
                    // getServletPath("viewUMark?") + newQS + "\"'>");
                     getServletPath("viewUMark?&RETURNING=YES")  + "\"'>");
                     
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
      out.println("     if ( (\"\" + document.forms[0].n.value) != \"\") {");
      out.println("             if (document.forms[0].n.value.length > 20) {");
      out.println("                     alert('Name must be less than 20 characters!');");
      out.println("                     rc = 0;");
      out.println("             }");
      out.println("     }");
      out.println("     ");
      out.println("     ");
      out.println("     if (rc) {");
      out.println("             if (confirm('Are you sure that you want to create the unified marker?')) {");
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
   private void writeNewMappingScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function selChanged(item) {");
      out.println("  document.forms[0].item.value = \"\" + item;");
      out.println("  document.forms[0].oper.value = \"SEL_CHANGED\";");
      out.println("  document.forms[0].submit();");
      out.println("}");
      out.println("function valForm() {");
      out.println("             document.forms[0].oper.value = 'CREATE'");
      out.println("             document.forms[0].submit();");
      out.println("     ");
      out.println("     ");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
   }

   private void writeEditAllele(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";
      if (oper.equals("DELETE")) {
         if (deleteAllele(req, res, conn))
            writeAlleles(req, res);
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
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");

      Connection conn =  (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      try {
         String oldQS = buildQS(req);
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         String uaid = req.getParameter("uaid");

         out.println("<html>");
         out.println("<head>");
         writeAlleleEditScript(out);
         out.println("<title>Edit Allele</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Genotypes - Unified Markers - Edit - Alleles</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("viewUMark/editAllele?") +
                     oldQS + "\">");

         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, COMM FROM " +
                                  "V_U_ALLELES_1 WHERE UAID=" + uaid);
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
                     getServletPath("viewUMark/alleles?") + oldQS + "\"'>&nbsp;");
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
         out.println("<input type=\"hidden\" NAME=uaid value=\"" + uaid + "\">");
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

   private void writeImpFile(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      conn = (Connection) session.getValue("conn");
      writeImpFilePage(req, res);
   }

   
   /**
    * Writes the page used to import unified markers.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @exception IOException If no PrintWriter can be created.
    */
   private void writeImpFilePage(HttpServletRequest request,
                                 HttpServletResponse response)
      throws IOException
   {
      HttpSession session = request.getSession(true);
      Connection connection = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");

      PrintWriter out = response.getWriter();
      try
      {

         String projectId = (String) session.getValue("PID");

         out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                     "Transitional//EN\"");
         out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");
          
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         // Validation script
         out.println("<script type=\"text/JavaScript\">");
         out.println("<!--");
         out.println("function valForm() {");
         out.println("  ");
         out.println("  var rc = 1;");
         out.println("  ");
         out.println("  if (rc) {");
         out.println("          if (confirm('Are you sure that you want to create the unified markers?')) {");
         out.println("                  document.forms[0].oper.value = 'UPLOAD';");
         out.println("                  document.forms[0].submit();");
         out.println("          }");
         out.println("  }");
         out.println("  ");
         out.println("  ");
         out.println("}");
         out.println("//-->");
         out.println("</script>");
         out.println("<title>Import markers</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=0>" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">" +
                     "Genotypes - Unified Markers - File import</b></center>" +
                     "</td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post enctype=\"multipart/form-data\" action=\"" +
                     getServletPath("viewUMark/impMultipart") + "\">");
         out.println("<table  border=0 >");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         stmt = connection.createStatement();
         rset = stmt.executeQuery("SELECT SID, NAME FROM V_SPECIES_2 WHERE " +
                                  "PID=" + projectId + " ORDER BY NAME");
         out.println("<td>Species<br>");
         out.println("<select name=sid style=\"WIDTH: 200px\">");
         while (rset.next() ) {
            out.println("<option value=\"" + rset.getString("SID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         rset.close();
         stmt.close();
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


   /**
    * Writes the page used for importing mappings.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @exception IOException If no PrintWriter could be created.
    */
   private void writeImpMapping(HttpServletRequest request,
                                HttpServletResponse response)
      throws IOException
   {
      HttpSession session = request.getSession(true);

      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");

      PrintWriter out = response.getWriter();
      try
      {
         
         out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                     "Transitional//EN\"");
         out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");
          
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         // Validation script
         out.println("<script type=\"text/JavaScript\">");
         out.println("<!--");
         out.println("function valForm() {");
         out.println("  ");
         out.println("  var rc = 1;");
         out.println("  ");
         out.println("  if (rc) {");
         out.println("  //      if (confirm('Are you sure that you want to create the unified markers?')) {");
         out.println("                  document.forms[0].oper.value = 'UPLOAD';");
         out.println("                  document.forms[0].submit();");
         out.println("  //      }");
         out.println("  }");
         out.println("  ");
         out.println("  ");
         out.println("}");
         out.println("//-->");
         out.println("</script>");
         out.println("<title>Import marker mappings</title>");
         out.println("</head>");

         out.println("<body>");
         out.println("<table width=\"846\" border=0>" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Genotypes - Unified Markers - Import Mapping</b></center>" +
                     "</td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post enctype=\"multipart/form-data\" action=\"" +
                     getServletPath("viewUMark/impMultipartMapping") + "\">");
         out.println("<table border=0>");

         // File
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td>File<br>");
         out.println("<input type=file name=filename " +
                     "style=\"WIDTH: 350px\">");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td></td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table border=0><tr>");
         out.println("<td>");
         out.println("<input type=button value=Send  " +
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



   /***************************************************************************************
    * *************************************************************************************
    * The marker's edit page
    */
   private void writeEdit(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";
      if (oper.equals("DELETE")) {
         if (deleteUMarker(req, res, conn))
            writeFrame(req, res);
      } else if (oper.equals("UPDATE")) {
         if(updateUMarker(req, res, conn))
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
      String sid, sname, cid, cname, umid,
         name, alias, position, comm, usr, tc_ts;
      try {
         stmt = conn.createStatement();
         String oldQS = buildQS(req);
         String pid = (String) session.getValue("PID");
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         umid = req.getParameter("umid");
         String sql = "SELECT SID, SNAME, CID, CNAME, " +
            "UMID, NAME, ALIAS, POSITION, COMM, USR, " +
            "to_char(TS, '" + getDateFormat(session) + "') as TC_TS " +
            "FROM V_U_MARKERS_3 WHERE " +
            "UMID=" + umid ;
         rset = stmt.executeQuery(sql);

         rset.next();
         sid = rset.getString("SID");
         sname = rset.getString("SNAME");
         cid = rset.getString("CID");
         cname = rset.getString("CNAME");
         name = rset.getString("NAME");
         alias = rset.getString("ALIAS");
         position = rset.getString("POSITION");
         comm = rset.getString("COMM");
         usr = rset.getString("USR");
         tc_ts = rset.getString("TC_TS");
         rset.close();
         stmt.close();

         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);

         out.println("<title>Edit marker</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Unified Markers - Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("viewUMark/edit?") +
                     oldQS + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr>");
         out.println("<td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td>");
         out.println("</tr>");
         out.println("<tr><td>Species</td><td>" + sname + "</td></tr>");
         out.println("<tr><td>Last updated by</td><td>" + usr + "</td></tr>");
         out.println("<tr><td>Last updated</td><td>" + tc_ts + "</td></tr>");
         out.println("</table>");
         out.println("</td></tr><tr><td></td><td>");

         // Changable data
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Changable data</font></td></tr>");
         out.println("<tr><td>Chromosome<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM V_CHROMOSOMES_1 WHERE SID=" +
                                  sid + " ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME");
         out.println("<select name=n_cid width=200 style=\"WIDTH: 200px\">");
         while (rset.next()) {
            out.println("<option " + (cid.equals(rset.getString("CID")) ? "selected " : "") +
                        "value=\"" + rset.getString("CID") + "\">" + rset.getString("NAME") +
                        "</option>");
         }
         out.println("</select>");
         out.println("</td>");
         rset.close();
         stmt.close();
         out.println("<td>Name<br>");
         out.println("<input type=text name=n_name width=200 style=\"WIDTH: 200px\" " +
                     "value=\"" + replaceNull(name, "") + "\" maxlength=20>");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<td>Alias<br>");
         out.println("<input type=text name=n_alias width=200 style=\"WIDTH: 200px\" " +
                     "value=\"" + replaceNull(alias, "") + "\" maxlength=20>");
         out.println("</td>");
         out.println("<td>Position<br>");
         out.println("<input name=n_position width=200 style=\"WIDTH: 200px\" " +
                     "value=\"" + replaceNull(position, "") + "\">");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<td colspan=2>Comment<br>");
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
                //     getServletPath("viewUMark?item=no&ACTION=DISPLAY&") + "sid=" + sid + "&cid=" + cid + "&item=no\"'>&nbsp;");
                     getServletPath("viewUMark?&RETURNING=YES") + "\"'>&nbsp;");

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
   private void writeMapping(HttpServletRequest req, HttpServletResponse res)
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
      String umid, oper, mid, umname;
      try {
         // first we check if the user has pressed the delete button
         oper = req.getParameter("oper");
         if (oper == null) oper = "SEL_CHANGED";
         if (oper.equals("DELETE")) {
            mid = req.getParameter("item");
            if (!deleteMapping(req, res, conn))
               return;
         }
         umid = req.getParameter("umid");
         // Find the name of the unified marker
         stmt  =conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME FROM V_U_MARKERS_1 WHERE UMID=" + umid);
         if (rset.next())
            umname = rset.getString("NAME");
         else
            umname = "(Unknown)";
         rset.close();
         stmt.close();

         stmt = conn.createStatement();
         String oldQS = buildQS(req);
         String pid = (String) session.getValue("PID");
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         String sql = "SELECT SUNAME, MNAME, UMID, MID " +
            "FROM V_R_UMID_MID_2 WHERE " +
            "UMID=" + umid ;
         rset = stmt.executeQuery(sql);

         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);

         out.println("<title>Unified marker mapping</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Unified Markers - Mapping</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("viewUMark/mapping?") +
                     oldQS + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         out.println("</td></tr><tr><td></td><td>");
         out.println("<p>Mapping for the unified marker " + replaceNull(umname, "(unknown)") + "</p>");
         out.println("</td></tr><tr><td></td><td>");
         out.println("</td></tr><tr><td></td><td>");

         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td><font color=white>Sampling unit</font></td>");
         out.println("<td><font color=white>Marker</font></td>");
         out.println("<td width=110>&nbsp;</td>"); // Delete
         out.println("<td>&nbsp;</td>"); // Allele mapping link
         out.println("</tr>");

         boolean odd = true;
         while (rset.next()) {
            if (odd)
               out.println("<tr bgcolor=white>");
            else
               out.println("<tr bgcolor=lightgrey>");
            odd = !odd;
            out.println("<td>" + formatOutput(session, rset.getString("SUNAME"), 12) + "</td>");
            out.println("<td>" + formatOutput(session, rset.getString("MNAME"), 20) + "</td>");
            out.println("<td><input type=button value=\"Delete\" width=100 style=\"WIDTH: 100px\" " +
                        "onClick='document.forms[0].oper.value=\"DELETE\";document.forms[0].item.value=\"" +
                        rset.getString("MID") + "\";document.forms[0].submit();'></td>");
            out.println("<td width=100><a href=\"" + getServletPath("viewUMark/alleleMapp?") +
                        "umid=" + umid + "&mid=" + rset.getString("MID") + "\">Allele</a></td>");
            out.println("</tr>");
         }

         out.println("</td></tr>");
         out.println("</table>");

         out.println("<tr><td></td><td></td></tr>");
         out.println("<tr><td></td><td>");
         out.println("<table cellspacing=0 cellpading=0>");

         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("viewUMark?") + oldQS + "&item=no\"'>&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Create width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("viewUMark/newMapp?") + oldQS + "\";'>&nbsp;");
         out.println("</td></tr></table>");
         // Store some extra information needed by doPost()
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("<input type=hidden name=item value=\"\">");
         out.println("</td></tr></table>");
         out.println("</form>");
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
   private void writeNewMapping(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createMapping(req, res, conn))
            writeMapping(req, res);
      } else {
         writeNewMappingPage(req, res);
      }

   }

   private void writeNewMappingPage(HttpServletRequest req, HttpServletResponse res)
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
      String umid;
      String suid, cid, pid, oper, item, newQS;
      try {
         stmt = conn.createStatement();
         pid = (String) session.getValue("PID");
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         umid = req.getParameter("umid");
         pid = (String) session.getValue("PID");
         suid = req.getParameter("suid");
         cid = req.getParameter("cid");
         newQS = removeQSParameterOper(buildQS(req));
         newQS = removeQSParameterSuid(newQS);
         newQS = removeQSParameterCid(newQS);
         oper = req.getParameter("oper");

         if (oper == null) oper = "SEL_CHANGED";

         if (pid == null) pid = "-1";
         item = req.getParameter("item");
         if (item == null) {
            suid = findSuid(conn, pid);
            cid = findCidFromSuid(conn, suid);
         } else if (item.equals("suid")) {
            cid = findCidFromSuid(conn, suid);
         } else if (item.equals("no")) {
                                // No changes in the selection
         }
         out.println("<html>");
         out.println("<head>");
         writeNewMappingScript(out);

         out.println("<title>Unified marker mapping</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Genotypes - Unified Markers - Mapping - New</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("viewUMark/newMapp?") +
                     newQS + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         out.println("</td></tr><tr><td></td><td>");

         out.println("<table border=0 cellpading=0 cellspacing=0>");
         // Sampling unit
         out.println("<tr>");
         out.println("<td>Sampling unit<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid + " ORDER BY NAME");
         out.println("<select name=suid width=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"suid\");'>");
         while (rset.next()) {
            out.println("<option " +
                        (suid.equals(rset.getString("SUID")) ? "selected " : "") +
                        "value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         rset.close();
         stmt.close();
         out.println("</select>");
         out.println("</td></tr>");
         // Chromosome
         out.println("<tr>");
         out.println("<td>Chromosome<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM V_CHROMOSOMES_1 WHERE " +
                                  "SID=(SELECT SID FROM V_ENABLED_SAMPLING_UNITS_1 WHERE SUID=" +
                                  suid + ") ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME");
         out.println("<select name=cid width=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"cid\");'>");
         while (rset.next()) {
            out.println("<option " +
                        (cid.equals(rset.getString("CID")) ? "selected " : "" ) +
                        "value=\"" + rset.getString("CID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         rset.close();
         stmt.close();
         out.println("</select>");
         out.println("</td></tr>");
         // Marker
         out.println("<tr>");
         out.println("<td>Marker<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT MID, NAME FROM V_MARKERS_1 WHERE " +
                                  "SUID=" + suid + " AND CID=" + cid + " ORDER BY NAME");
         out.println("<select name=mid width=200 style=\"WIDTH: 200px\">");
         while (rset.next() ) {
            out.println("<option value=\"" + rset.getString("MID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         rset.close();
         stmt.close();
         out.println("</select>");
         out.println("</td></tr>");
         out.println("</table>");

         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         out.println("<table cellspacing=0 cellpading=0>");

         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("viewUMark/mapping?") + newQS + "&item=no\"'>&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Create width=100 " +
                     "style=\"WIDTH: 100px\" onClick='valForm();'>&nbsp;");
         out.println("</td></tr></table>");
         // Store some extra information needed by doPost()
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("<input type=hidden name=item value=\"\">");
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
   /*************************
   **************************/
   private void writeAlleleMapping(HttpServletRequest req, HttpServletResponse res)
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
      String umid, oper, mid;
      String aid, name, uaid, avail_uaid, avail_uname;
      String mname, umname;
      Vector ualleles;
      Vector alleles;
      try {
         // first we check if the user has pressed the delete button
         oper = req.getParameter("oper");
         if (oper == null) oper = "SEL_CHANGED";
         if (oper.equals("UPDATE")) {
            aid = req.getParameter("item");
            if (!updateAlleleMapping(req, res, conn))
               return;
         }

         String oldQS = buildQS(req);
         String pid = (String) session.getValue("PID");
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         umid = req.getParameter("umid");
         mid = req.getParameter("mid");
         // Find the names of the marker and the unified marker
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT u.NAME UMNAME, m.NAME MNAME FROM " +
                                  "V_U_MARKERS_1 u, V_MARKERS_1 m WHERE " +
                                  "u.UMID=" + umid + " AND " +
                                  "m.MID=" + mid);
         if (rset.next()) {
            umname = rset.getString("UMNAME");
            mname = rset.getString("MNAME");
         } else {
            umname = "(unknown)";
            mname = "(unknown)";
         }
         rset.close();
         stmt.close();

         // Read and store all the unified alleles for this unified marker in
         // a vector. We need this data several times belove.
         String sql = "SELECT UAID, NAME FROM V_U_ALLELES_1 WHERE UMID=" +
            umid + " ORDER BY NAME";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(sql);
         ValueHolder vh;
         ualleles = new Vector();
         while (rset.next()) {
            vh = new ValueHolder();
            vh.o1 = rset.getString("UAID");
            vh.o2 = rset.getString("NAME");
            ualleles.addElement(vh);
         }
         rset.close();
         stmt.close();

         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);

         out.println("<title>Unified alleles mapping</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Unified Markers - Mapping - Alleles</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("viewUMark/alleleMapp?") +
                     oldQS + "&umid=" + umid + "&mid=" + mid + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         out.println("</td></tr><tr><td></td><td>");
         out.println("<p>Allele mapping for unified marker " + umname +
                     " and marker " + mname + "</p>");
         out.println("</td></tr><tr><td></td><td>");
         out.println("</td></tr><tr><td></td><td>");

         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td>Allele</td>");
         out.println("<td>Unified allele</td>");
         out.println("</tr>");

         sql = "SELECT NAME, AID FROM V_ALLELES_1 WHERE MID=" + mid +
            " ORDER BY NAME";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(sql);
         alleles = new Vector();
         while (rset.next()) {
            vh = new ValueHolder();
            vh.o1 = rset.getString("AID");
            vh.o2 = rset.getString("NAME");
            alleles.addElement(vh);
         }
         rset.close();
         stmt.close();

         boolean odd = true;
         sql = "SELECT UAID FROM V_R_UAID_AID_1 WHERE PID=" + pid +
            " AND UMID=" + umid + " AND AID=?";
         PreparedStatement pstmt = conn.prepareStatement(sql);
         for (int i = 0; i < alleles.size(); i++) {
            if (odd)
               out.println("<tr bgcolor=white>");
            else
               out.println("<tr bgcolor=lightgrey>");
            odd = !odd;
            aid = (String) ((ValueHolder) alleles.elementAt(i) ).o1;
            name = (String) ((ValueHolder) alleles.elementAt(i) ).o2;
            pstmt.clearParameters();
            pstmt.setInt(1, Integer.parseInt(aid));
            rset = pstmt.executeQuery();
            if (rset.next())
               uaid = rset.getString("UAID");
            else
               uaid = "-1";
            rset.close();

            out.println("<td>" + formatOutput(session, name, 12) + "</td>");
            out.println("<td><select name=aid_" + aid + " width=200 style=\"WIDTH: 200px\">");
            out.println("<option " + (uaid.equals("-1") ? "selected " : "") +
                        "value=\"-1\">(not mapped)</option>");
            for (int j = 0; j < ualleles.size(); j++) {
               avail_uaid = (String) ((ValueHolder) ualleles.elementAt(j) ).o1;
               avail_uname = (String) ((ValueHolder) ualleles.elementAt(j) ).o2;
               out.println("<option " + (uaid.equals(avail_uaid) ? "selected " : "") +
                           "value=\"" + avail_uaid + "\">" + avail_uname + "</option>");
            }
            out.println("</select>");
            out.println("</td>");
            out.println("</tr>");
         }
         pstmt.close();

         out.println("</td></tr>");
         out.println("</table>");

         out.println("<tr><td></td><td></td></tr>");
         out.println("<tr><td></td><td>");
         out.println("<table cellspacing=0 cellpading=0>");

         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("viewUMark/mapping?item=no&") + oldQS + "&item=no\"'>&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Update width=100 " +
                     "style=\"WIDTH: 100px\" onClick='document.forms[0].oper.value=\"UPDATE\"; " +
                     "document.forms[0].submit();'>&nbsp;");
         out.println("</td></tr></table>");
         // Store some extra information needed by doPost()
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("<input type=hidden name=item value=\"\">");
         out.println("</td></tr></table>");
         out.println("</form>");
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
    * Creates unified markers from file.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if unified markers could be created.
    *         False if markers could not be created.
    * @exception IOException If writing any page fails.
    * @exception ServletException If writing any page fails.
    */
   private boolean createMarkFile(HttpServletRequest request,
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

         // Get parameters
         int speciesId = Integer.parseInt(multiRequest.getParameter("sid"));
         int userId = Integer.parseInt((String) session.getValue("UserID"));
         int projectId = Integer.parseInt((String) session.getValue("PID"));

         DbUMarker dbUMarker = new DbUMarker();
         Enumeration fileEnum = multiRequest.getFileNames();
         if (fileEnum.hasMoreElements())
         {
            String givenFileName = (String) fileEnum.nextElement();
            String systemFileName = multiRequest.getFilesystemName(givenFileName);
            FileParser fileParser = new FileParser(upPath + "/" +  systemFileName); 

            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.UMARKER,
                                                                        FileTypeDefinition.LIST));
            dbUMarker.CreateUMarkers(fileParser, connection, projectId,
                                     speciesId, userId); 

            errMessage = dbUMarker.getErrorMessage();
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

      // if commit/rollback ok and database operation ok, write the frame
      if (commitOrRollback(connection, request, response,
                           "UnifiedMarkers.FileImport.Send", errMessage,
                           "viewUMark/impFile", isOk)
          && isOk)
      {
         writeFrame(request, response);
      }
      return isOk;
   }

   
   /**
    * Creates mappings from a file.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if mappings could be created.
    *         Fals if mappings could not be created.
    * @exception IOException If any page can not be written.
    * @exception ServletException If any page can not be written.
    */
   private boolean createMappings(HttpServletRequest request,
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

         int projectId = Integer.parseInt((String) session.getValue("PID"));

         Enumeration fileEnum = multiRequest.getFileNames();
         if (fileEnum.hasMoreElements())
         {
            DbUMarker dbUMarker = new DbUMarker();
            
            String givenFileName = (String) fileEnum.nextElement();
            String systemFileName = multiRequest.getFilesystemName(givenFileName);
            MapFileParser mapFileParser = new MapFileParser(upPath + "/" + systemFileName);
            mapFileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.UMARKER,
                                                                           FileTypeDefinition.MAPPING));
            Mapper[] mappers = mapFileParser.asMapper();
            dbUMarker.CreateMappings(mappers, connection, projectId);
            errMessage = dbUMarker.getErrorMessage();
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

      // if commit/rollback ok and database operation ok, write the frame.
      if (commitOrRollback(connection, request, response,
                           "UnifiedMarkers.MappingImport.Send", errMessage,
                           "viewUMark/impMapping", isOk)
          && isOk)
      {
         writeFrame(request, response);
      }
      return isOk;
   }

   
   /**
    * Creates a new unified marker.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if unified marker created.
    *         False if unified marker not created.
    */
   private boolean createUMarker(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int pid, sid, cid, id;
         String name = null, alias = null, comm = null;
         String position=null;

         connection.setAutoCommit(false);
         // problem to parse ints if = null !!!
         id = Integer.parseInt((String) session.getValue("UserID"));
         pid = Integer.parseInt((String) session.getValue("PID"));
         sid = Integer.parseInt(request.getParameter("sid"));
         cid = Integer.parseInt(request.getParameter("cid"));
         name = request.getParameter("n");
         alias = request.getParameter("a");
         comm = request.getParameter("c");
         position= request.getParameter("position");

         DbUMarker dbum = new DbUMarker();
         dbum.CreateUMarker(connection, name, alias, comm, position, pid, cid, id);
         errMessage = dbum.getErrorMessage();
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
                       "UnifiedMarkers.New.Create", errMessage,
                       "viewUMark", isOk);
      return isOk;
   }

   
   /**
    * Deletes a unified marker.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if unified marker deleted.
    *         False if unified marker not deleted.
    */
   private boolean deleteUMarker(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int umid;
         connection.setAutoCommit(false);
         String UserID = (String) session.getValue("UserID");
         umid = Integer.parseInt(request.getParameter("umid"));
         DbUMarker dbum = new DbUMarker();
         dbum.DeleteUMarker(connection, umid);
         errMessage = dbum.getErrorMessage();
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
                       "UnifiedMarkers.Edit.Delete", errMessage,
                       "viewUMark", isOk);
      return isOk;
   }


   /**
    * Updates a unified marker.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if unified marker updated.
    *         False if unified marker not updated.
    */
   private boolean updateUMarker(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         String UserID = (String) session.getValue("UserID");
         String name, alias, position, comm;
         int cid, umid;
         String oldQS = request.getQueryString();
         name = request.getParameter("n_name");
         alias = request.getParameter("n_alias");
         position = request.getParameter("n_position");
         comm = request.getParameter("n_comm");
         cid = Integer.parseInt(request.getParameter("n_cid"));
         umid = Integer.parseInt(request.getParameter("umid"));

         // Check if position is a valid float.
         try
         {
            float test = Float.parseFloat(position);
         }
         catch (NumberFormatException nfe)
         {
            position = null;
         }

         DbUMarker dbum = new DbUMarker();
         dbum.UpdateUMarker(connection, umid, name, alias, comm, position,
                            cid, Integer.parseInt(UserID)); 
         errMessage = dbum.getErrorMessage();
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
                       "UnifiedMarkers.Edit.Update", errMessage,
                       "viewUMark", isOk);
      return isOk;
   }


   /**
    * Deletes a marker mapping.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if mapping created.
    *         False if mapping not created.
    */
   private boolean createMapping(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int pid, umid, mid, id;
         String name = null, alias = null, comm = null;
         String position=null;

         connection.setAutoCommit(false);
         // problem to parse ints if = null !!!
         id = Integer.parseInt((String) session.getValue("UserID"));
         pid = Integer.parseInt((String) session.getValue("PID"));
         umid = Integer.parseInt(request.getParameter("umid"));
         mid = Integer.parseInt(request.getParameter("mid"));

         DbUMarker dbum = new DbUMarker();
         dbum.CreateUMarkerMapping(connection, umid, mid);
         errMessage = dbum.getErrorMessage();
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
                       "UnifiedMarkers.Mapping.Create", errMessage,
                       "viewUMark/mapping", isOk);
      return isOk;
   }


   /**
    * Deletes a marker mapping.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if mapping deleted.
    *         False if mapping not deleted.
    */
   private boolean deleteMapping(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int umid, mid;
         connection.setAutoCommit(false);
         umid = Integer.parseInt(request.getParameter("umid"));
         mid = Integer.parseInt(request.getParameter("item"));

         DbUMarker dbum = new DbUMarker();
         dbum.DeleteUMarkerMapping(connection, umid, mid);
         errMessage = dbum.getErrorMessage();
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
                       "UnifiedMarkers.Mapping.Delete", errMessage, 
                       "viewUMark", isOk);
      return isOk;
   }


   /**
    * Creates an allele in a unified marker.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if allele created.
    *         False if allele not created.
    */
   private boolean createAllele(HttpServletRequest request,
                                HttpServletResponse response,
                                Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         String name, comm;
         int umid, id;
         connection.setAutoCommit(false);
         name = request.getParameter("n");
         comm = request.getParameter("c");
         umid = Integer.parseInt(request.getParameter("umid"));
         id = Integer.parseInt((String) session.getValue("UserID"));
         DbUMarker dbum = new DbUMarker();
         dbum.CreateUAllele(connection, name, comm, umid, id);
         errMessage = dbum.getErrorMessage();
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
                       "UnifiedMarkers.Alleles.Create.Create", errMessage,
                       "viewUMark/alleles", isOk);
      return isOk;
   }


   /**
    * Updates alleles in a unified marker.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if allele updated.
    *         False if allele not updated.
    */
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
         int uaid, id;
         String oldQS = request.getQueryString();
         uaid = Integer.parseInt(request.getParameter("uaid"));
         id = Integer.parseInt((String) session.getValue("UserID"));
         name = request.getParameter("n");
         comm = request.getParameter("c");
         DbUMarker dbum = new DbUMarker();
         dbum.UpdateUAllele(connection, id, uaid, name, comm);
         errMessage = dbum.getErrorMessage();
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
                       "UnifiedMarkers.Alleles.Edit.Update", errMessage,
                       "viewUMark/alleles", isOk);
      return isOk;
   }


   /**
    * Deletes an allele from a unified marker.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if allele deleted.
    *         False if allele not deleted.
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
         int uaid;
         connection.setAutoCommit(false);
         uaid = Integer.parseInt(request.getParameter("uaid"));
         DbUMarker dbum = new DbUMarker();
         dbum.DeleteUAllele(connection, uaid);
         errMessage = dbum.getErrorMessage();
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
                       "UnifiedMarkers.Alleles.Edit.Delete", errMessage,
                       "viewUMark/alleles", isOk);
      return isOk;
   }


   /**
    * Updates the mapping of alleles in the unified marker to unified
    * alleles. 
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if mapping updated.
    *         False if mapping not updated.
    */
   private boolean updateAlleleMapping(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      HttpSession session = request.getSession(true);
      try
      {
         Enumeration e = request.getParameterNames();
         String pn;
         String uaid;
         String aid;
         String pid = (String) session.getValue("PID");
         DbUMarker dbum = new DbUMarker();
         while (e.hasMoreElements() && isOk)
         {
            pn = (String) e.nextElement();
            if (pn.startsWith("aid_"))
            {
               // Alright, this parameter contains information about
               // the allele mappings
               aid = pn.substring("aid_".length() );
               uaid = request.getParameter(pn);
               dbum.DeleteUAlleleMapping(connection, Integer.parseInt(pid),
                                         Integer.parseInt(aid)); 
               if (uaid.equals("-1"))
               {
                  // This allele should not be mapped
               } 
               else
               {
                  dbum.CreateUAlleleMapping(connection, Integer.parseInt(pid),
                                            Integer.parseInt(uaid),
                                            Integer.parseInt(aid));
               }
               errMessage = dbum.getErrorMessage();
               Assertion.assertMsg(errMessage == null ||
                                errMessage.trim().equals(""), errMessage);
            }
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
                       "UnifiedMarkers.Mapping.Allele.Update", errMessage,
                       "viewUMark", isOk); 
      return isOk;
   }
   

   private void writeAlleleEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("     ");
      out.println("     var rc = 1;");
      out.println("     if ('DELETE' == action.toUpperCase()) {");
      out.println("             if (confirm('Are you sure you want to delete the unified allele?')) {");
      out.println("                     document.forms[0].oper.value='DELETE';");
      out.println("                     rc = 0;");
      out.println("             }");
      out.println("     ");
      out.println("     } else if ('UPDATE' == action.toUpperCase()) {");
      out.println("             if (confirm('Are you sure you want to update the unified allele?')) {");
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
   private void writeNewAlleleScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm() {");
      out.println("     ");
      out.println("     var rc = 1;");
      out.println("     if ( (\"\" + document.forms[0].c.value) != \"\" &&");
      out.println("       document.forms[0].c.value.length > 255) {");
      out.println("                     alert('Comment must be less than 255 characters!');");
      out.println("                     rc = 0;");
      out.println("     }");
      out.println("     if ( (\"\" + document.forms[0].n.value) == \"\") {");
      out.println("                     rc = 0;");
      out.println("     }");
      out.println("     ");
      out.println("     ");
      out.println("     if (rc) {");
      out.println("             if (confirm('Are you sure that you want to create the unified allele?')) {");
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
   private void writeEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("     ");
      out.println("     var rc = 1;");
      out.println("     if ('DELETE' == action.toUpperCase()) {");
      out.println("             if (confirm('Are you really sure you want to delete the unified marker?\\n' +");
      out.println("               'This operation also deletes all unified alleles for the marker!')) {");
      out.println("                     document.forms[0].oper.value='DELETE';");
      out.println("                     rc = 0;");
      out.println("             }");
      out.println("     ");
      out.println("     } else if ('UPDATE' == action.toUpperCase()) {");
      out.println("             if (confirm('Are you sure you want to update the unified marker?')) {");
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
            // We neew the privilege UMRK_R for all these
            title = "Unified Markers - View & Edit";
            if ( privDependentString(privileges, UMRK_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/edit") ) {
            // We neew the privilege UMRK_W
            title = "Unified Markers - Edit";
            if ( privDependentString(privileges, UMRK_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/new") ) {
            // We neew the privilege UMRK_W
            title = "Unified Markers - New";
            if ( privDependentString(privileges, UMRK_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/impFile") ) {
            // We need the privilege UMRK_W
            title = "Genotypes - Unified Markers - File Import";
            if ( privDependentString(privileges, UMRK_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/alleles") ) {
            // We need the privilege UMRK_R
            title = "Unified Markers - Alleles";
            if ( privDependentString(privileges, UMRK_R, "", null) == null)
               ok = false;
         } else if (extPath.equals("/newAllele") ) {
            // We need the privilege UMRK_W
            title = "Unified Markers - New Allele";
            if ( privDependentString(privileges, UMRK_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/editAllele") ) {
            // We need the privilege UMRK_W
            title = "Unified Markers - Edit Allele";
            if ( privDependentString(privileges, UMRK_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/detailsAllele") ) {
            // We need the privilege UMRK_R
            title = "Unified Markers - Details Allele";
            if ( privDependentString(privileges, UMRK_R, "", null) == null)
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

