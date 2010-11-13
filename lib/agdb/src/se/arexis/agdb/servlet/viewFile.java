/**
  $Log$
  Revision 1.16  2005/02/04 15:58:40  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.15  2005/01/31 16:16:40  heto
  Changing database to PostgreSQL. Problems with counts and selection buttons...

  Revision 1.14  2004/04/02 07:09:27  wali
  Bug fix

  Revision 1.13  2004/03/31 07:30:47  wali
  Bug fix

  Revision 1.12  2004/03/30 14:21:28  wali
  Changed Analyses to export.

  Revision 1.11  2004/03/16 10:29:21  wali
  Added "Date from" and "Date to" in writeTop, buildFilter and buildQS, writeTopScript was implemented to chech the dates. Implemented writeDetails.

  Revision 1.10  2004/03/05 14:38:20  wali
  Changed the header name

  Revision 1.9  2004/03/01 12:23:21  wali
  Changed analyses to Import/export

  Revision 1.8  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.7  2003/04/29 11:43:11  heto
  Modified the user interface. Fexed the tables, added new stylesheet.

  Revision 1.6  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.5  2003/04/25 09:06:16  heto
  Replacing concept "generation" to "file export"

  Revision 1.4  2002/12/13 15:03:58  heto
  Comments added

  Revision 1.3  2002/11/21 10:48:17  heto
  Changed to specification 1.3 or 1.4.
  session attributes has changed.

  Revision 1.2  2002/10/18 11:41:09  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:05  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.14  2001/06/26 12:08:19  roca
  Changed names on buttons (finish/cancel) in alnalyse pages
  Generations changed to Export format
  Corrected counters in Filter/File views
  Bugfix in GenChrimap

  Revision 1.13  2001/05/31 07:06:55  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.12  2001/05/10 08:42:49  frob
  Changed update() to call general commitOrRollback(). writeError() was removed.

  Revision 1.11  2001/05/09 14:20:15  frob
  Modification of delete() and update() methods. Added errorQueryString(), rewrote and
  renamed removeDir() to removeFileObject().

  Revision 1.10  2001/05/04 11:23:22  frob
  Calls to removeOper and removePid changed to call methods in the superclass.
  The previously called methods are removed.

  Revision 1.9  2001/05/04 11:20:21  frob
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

public class viewFile extends SecureArexisServlet
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
      } else if (extPath.equals("/edit")) {
         writeEdit(req, res);
      } else if (extPath.equals("/files")) {
         writeFiles(req, res);
      } else if (extPath.equals("/download")) {
         sendFile(req, res);
      } else if (extPath.equals("/start")) {
         writeStart(req, res);
      } else if (extPath.equals("/details")) {
         writeDetails(req, res);   
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
    * Returns the query string to be used when going back from the error
    * page. 
    *
    * @param request The request object to be used when building the string.
    * @return The error query string.
    */
   protected String errorQueryString(HttpServletRequest request)
   {
      String errorQS = buildQS(request);
      return removeQSParameterOper(errorQS);
   }
   

   /** Write the frame for all other pages to be visible in.
    *
    * The frame is built according to the following
    * schema.
    *
    * ************************
    * * TOP                  *
    * ************************
    * * MIDDLE               *
    * *                      *
    * ************************
    * * BOTTOM               *
    * *                      *
    * ************************
    *
    * The top is the menu frame
    *
    * The middle part is the header of the data fields.
    *
    * The bottom part is the data table.
    *
    * @param req The request object
    * @param res The response object
    * @throws ServletException Throws ServletException
    * @throws IOException Throws IOException
    */
   private void writeFrame(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException 
   {
      HttpSession session = req.getSession(true);
      // set content type and other response header fields first
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");

      PrintWriter out = res.getWriter();
      try {
         String topQS = buildQS(req);
        
         // we don't want the oper parameter anaywhere but in the "edit" and "new" page!
         topQS = removeQSParameterOper(topQS);
         String bottomQS = topQS.toString();
         res = checkRedirectStatus(req,res);
         out.println("<html>"
                     + "<HEAD>"
                     + " <TITLE>Filters</TITLE>"
                     + "</HEAD>"
                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" //
                     + "<frame name=\"filetop\" "
                     + "src=\""+ getServletPath("viewFile/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"filemiddle\" "
                     + "src=\""+ getServletPath("viewFile/middle?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"filebottom\""
                     + "src=\"" +getServletPath("viewFile/bottom?") + bottomQS + "\" "
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
      Connection conn = (Connection) session.getAttribute("conn");
      String action = null, // For instance COUNT, DISPLAY, NEXT etc
//         sid,
//         old_sid,
         fgid,
         name,
         type,
         mode,
         status,
         orderby = null, 
         dfrom=null,
         dto=null;
      boolean sid_changed = false;
     /* old_sid = (String) session.getAttribute("SID");
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
      */
      fgid = req.getParameter("fgid");
      name = req.getParameter("name");
      type= req.getParameter("type");
      mode = req.getParameter("mode");
      status = req.getParameter("status");
      dfrom = req.getParameter("D_FROM");
      dto = req.getParameter("D_TO");
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
      if (fgid != null && !fgid.trim().equals(""))
         output.append("&fgid=").append(fgid);
      if (name != null && !name.trim().equals(""))
         output.append("&name=").append(name);
      if (type != null && !type.trim().equals(""))
         output.append("&type=").append(type);
      if (mode != null && !mode.trim().equals(""))
         output.append("&mode=").append(mode);
      if (status != null && !status.trim().equals(""))
         output.append("&status=").append(status);
      if (dfrom != null && !dfrom.trim().equals(""))
         output.append("&D_FROM=").append(dfrom);
      if (dto != null && !dto.trim().equals(""))
         output.append("&D_TO=").append(dto);
      if (req.getParameter("oper") != null) {
          output.append("&oper=").append(req.getParameter("oper"));
      }
      
    //  output.append("&sid=").append(sid);
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
      Connection conn = (Connection) session.getAttribute("conn");
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer(512);
      try {
         sbSQL.append("SELECT count(distinct fgid) " +
                      "FROM gdbadm.V_FILE_GENERATIONS_2 WHERE ");
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
      String pid = null,
         name = null,
         mode = null,
         type = null,
         status = null,
         dfrom = null,
         dto = null;
      StringBuffer filter = new StringBuffer(256);
      HttpSession session = req.getSession(true);
      pid = (String) session.getAttribute("PID");
      name = req.getParameter("name");
      mode = req.getParameter("mode");
      type = req.getParameter("type");
      status = req.getParameter("status");
      dfrom = req.getParameter("D_FROM");
      dto = req.getParameter("D_TO"); 
    
      filter.append(" pid=" + pid);
      if (name != null && !name.trim().equals(""))
         filter.append(" and name like '" + name + "'");

      if (mode != null && !mode.trim().equals("") && !mode.equals("*"))
         filter.append(" and mode_='" + mode + "'");

      if (type != null && !type.trim().equals("") && !type.equals("*"))
         filter.append(" and type='" + type + "'");

      if (dfrom!=null)  
            if(dfrom.compareTo("") != 0)   
                filter.append(" and TS >= to_date('" + dfrom + "', 'YYYY-MM-DD')");
      
      if (dto!=null && !dto.trim().equals(""))
             filter.append(" and TS < to_date('" + dto + "', 'YYYY-MM-DD')");
      
      if (status != null && !status.trim().equals("") && !status.equals("*")) {
         if (status.equals("D")) {
            // All analyses in which all the data files has been generated
            filter.append(" and FGID NOT IN(SELECT FGID FROM V_DATA_FILES_1 WHERE " +
                          " V_FILE_GENERATIONS_2.FGID=FGID and upper(STATUS) != 'DONE')");
         } else if (status.equals("I")) {
            // All analyses that are still in progress. The criterium is that at least
            // one of the data files are still being generated, that is, the data file
            // status is not one of ("DONE", "ERROR")
            filter.append(" and FGID IN (SELECT FGID FROM V_DATA_FILES_1 WHERE " +
                          " V_FILE_GENERATIONS_2.FGID=FGID AND upper(STATUS) NOT IN ('DONE', 'ERROR') )");
         } else if (status.equals("E")) {
            // All analyses where at least one of the data file generation failed.
            filter.append(" and FGID IN (SELECT FGID FROM V_DATA_FILES_1 WHERE " +
                          " V_FILE_GENERATIONS_2.FGID=FGID AND upper(STATUS)='ERROR')");
         } 
         
      }
      
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


   /**
    *
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
      String pid, name, mode, type, status, orderby, oldQS, newQS, action, d_from, d_to;
      try {
         conn = (Connection) session.getAttribute("conn");
         pid = (String) session.getAttribute("PID");
         name = req.getParameter("name");
         mode = req.getParameter("mode");
         type = req.getParameter("type");
         status = req.getParameter("status");
         action = req.getParameter("ACTION");
         oldQS = req.getQueryString();
         newQS = buildTopQS(oldQS);
         orderby = req.getParameter("ORDERBY");
         d_from = req.getParameter("D_FROM");
         d_to = req.getParameter("D_TO");
         if (req.getParameter("STARTINDEX") != null)
            startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         else
            startIndex = 1;
         if (req.getParameter("ROWS") != null)
            rows = Integer.parseInt(req.getParameter("ROWS"));
         else
            rows = 0;
         if (name == null) name = "";

         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+ getURL("style/view.css") +"\">");
         out.println("<base target=\"content\">");
         
         //to check if the date is given correctly
         writeTopScript(out);
         out.println(getDateValidationScript());
         
         out.println("<title>Files</title>");
         out.println("</head>");
         
         out.println("<body bgcolor=\"#ffffd0\">");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<form method=get action=\"" + getServletPath("viewFile") +"\">");
         out.println("<p align=\"center\">" + // <center>" +
                     "<b style=\"font-size: 15pt\">Export - View & Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr><tr><td width=\"517\">");
         // Name
         out.println("<table width=488 height=\"92\">" +
                     "<td><b>Name</b><br><input name=name width=100 style=\"WIDTH: 100px\" " +
                     "value=\"" + name + "\">");
         out.println("</td>");
        
         
          // Format
         out.println("<td><b>Format</b><br>");
         //  out.println("<select name=type width=130 style=\"WIDTH: 100px\">");
         out.println("<select name=type width=130 >");

         // The alternetives for type should be dynamicely retrieved in some way
         out.println(getTypeOptions(req, res));
         out.println("</select>");
         out.println("</td>");
         
         // Mode
         out.println("<td><b>Mode</b><br>");
         out.println("<select name=mode width=100 style=\"WIDTH: 100px\">");
         if ("S".equals(mode)) {
            out.println("<option selected value=\"S\">Single</option>");
            out.println("<option value=\"M\">Multi</option>");
            out.println("<option value=\"*\">*</option>");
         } else if ("M".equals(mode)) {
            out.println("<option value=\"S\">Single</option>");
            out.println("<option selected value=\"M\">Multi</option>");
            out.println("<option value=\"*\">*</option>");
         } else {
            out.println("<option value=\"S\">Single</option>");
            out.println("<option value=\"M\">Multi</option>");
            out.println("<option selected value=\"*\">*</option>");
         }
         out.println("</select>");
         out.println("</td>");
        

         // Status
         out.println("<td><b>Status</b><br>");
         out.println("<select name=status width=100 style=\"WIDTH: 100px\">");
         if ("D".equals(status)) {
            out.println("<option selected value=\"D\">Done</option>");
            out.println("<option value=\"I\">In progress</option>");
            out.println("<option value=\"E\">Error</option>");
            out.println("<option value=\"*\">*</option>");
         } else if ("I".equals(status)) {
            out.println("<option value=\"D\">Done</option>");
            out.println("<option selected value=\"I\">In progress</option>");
            out.println("<option value=\"E\">Error</option>");
            out.println("<option value=\"*\">*</option>");
         } else if ("E".equals(status)) {
            out.println("<option value=\"D\">Done</option>");
            out.println("<option value=\"I\">In progress</option>");
            out.println("<option selected value=\"E\">Error</option>");
            out.println("<option value=\"*\">*</option>");
         } else {
            out.println("<option value=\"D\">Done</option>");
            out.println("<option value=\"I\">In progress</option>");
            out.println("<option value=\"E\">Error</option>");
            out.println("<option selected value=\"*\">*</option>");
         }
         out.println("</select>");
         out.println("</td></tr>");

         // 

         //Date
         out.println("<tr>");
         out.println("<td><b>Date from:</b><br>"
            +"<input id=D_FROM name=D_FROM value=\"" + replaceNull(d_from, "") + "\" style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\"></td>");
            
         out.println("<td><b>Date to:</b><br>"
            + "<input id=D_TO name=D_TO value=\"" + replaceNull(d_to, "") + "\"style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\"></td></tr>");
            
         out.println("<tr>");
         out.println("<td><b>&nbsp;</b><br>");
         out.println("&nbsp;");
         out.println("</td>");
         out.println("<td><b>&nbsp;</b><br></td>");
         out.println("<td><b>&nbsp;</b><br></td></table></td>");

         //Buttons
         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>");
         out.println("&nbsp;");
         //      out.println("<input type=button value=\"New filter\"" +
         //        " onClick='parent.location.href=\"" + getServletPath("viewFilt/new?") + newQS + "\"' " +
         //        "height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">" +
         //        "</td>");
         out.println("</td>");
         /*out.println("<tr><td width=68 colspan=2>" +
                     "<input id=COUNT name=COUNT type=submit value=\"Count\" width=\"69\"" +
                     " height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">" +
                     "</td>");
         out.println("<td width=68 colspan=2>" +
                     "<input id=DISPLAY name=DISPLAY type=submit value=\"Display\"" +
                     " width=\"70\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">" +
                     "</td></tr>");*/
         out.println("<tr><td width=68 colspan=2>" +
                     "<input id=COUNT name=COUNT type=button value=\"Count\" width=\"69\"" +
                     " onClick='valForm(\"COUNT\")' height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">" +
                     "</td>");
         out.println("<td width=68 colspan=2>" +
                     "<input id=DISPLAY name=DISPLAY type=button value=\"Display\"" +
                     " onClick ='valForm(\"DISPLAY\")' width=\"70\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">" +
                     "</td></tr>");
  /*
         out.println("<tr><td width=68 colspan=2>"//type=submit
            +"<input id=COUNT name=COUNT type=button value=\"Count\" width=\"69\""
            +" onClick='valForm(\"COUNT\")' height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">"
            +"</td>"
            +"<td width=68 colspan=2>"
            +"<input id=DISPLAY name=DISPLAY type=button value=\"Display\""
            +" width=\"70\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\" " //>
            +"onClick='valForm(\"DISPLAY\")'>"
            +"</td></tr>");
    */     
         
         // some hidden values
         out.println("<input type=\"hidden\" id=\"STARTINDEX\" name=\"STARTINDEX\" value=\"" + startIndex + "\">");
         out.println("<input type=\"hidden\" id=\"ORDERBY\" name=\"ORDERBY\" value=\"" + orderby + "\">");
         out.println("<input type=\"hidden\" id=\"oper\" name=\"oper\" value=\"\">");
         out.println("<input type=\"hidden\" id=\"ACTION\" name=ACTION value=\"NOP\">");
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

   
   private void writeTopScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 0;");
      out.println("	if ('DISPLAY' == action.toUpperCase() || 'COUNT' == action.toUpperCase()) {");
      out.println("         if (document.forms[0].D_FROM.value != '') {");
      out.println("               if (!valDate(document.forms[0].D_FROM)) {");
      out.println("			rc = 1;");
      out.println("	          }");  
      out.println("           }"); 
      out.println("         if (document.forms[0].D_TO.value != '') {");
      out.println("               if (!valDate(document.forms[0].D_TO)) {");
      out.println("			rc = 1;");
      out.println("	          }");  
      out.println("           }"); 
      out.println("	");
      out.println("	}");
      out.println("	if (rc == 0) {");
      out.println("             document.forms[0].ACTION.value=action;");
      out.println("		document.forms[0].submit();");
      out.println("		return true;");
      out.println("	}");
      out.println("	return false;");
      out.println("	");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
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


   
   /** 
    * The middle frame (contains header for the result-table)
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
        maxRows = getMaxRows(session);


      try {
         out.println("<html>\n<head>\n<link rel=\"stylesheet\" " +
                     "type=\"text/css\" href=\""+getURL("style/tableBar.css")+"\">");
         out.println("<base target=\"content\">");
         out.println("</head>");
         out.println("<body>");
         if(action != null)
         {
            //        out.println("<p align=left>&nbsp;&nbsp;");
            out.println("&nbsp;" +buildInfoLine(action, startIndex, rows, maxRows));
         }

         String oldQS, newQS;
         oldQS = req.getQueryString();
         String choosen = req.getParameter("ORDERBY");
         newQS = buildTopQS(oldQS);

         /*
           out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 " +
           "height=20 width=750 style=\"margin-left:2px\">" +
           "<td width=5></td>");
         */

         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=750 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");




         // the menu choices
         // Name
         out.println("<td width=150><a href=\"" +
                     getServletPath("viewFile")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\">");
         if(choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown><b>Name</b></FONT></a></td>\n");
         else
            out.println("Name</a></td>\n");
         // Mode
         out.println("<td width=50><a href=\"" + getServletPath("viewFile")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=MODE_\">");
         if(choosen.equals("MODE"))
            out.println("<FONT color=saddlebrown><b>Mode</b></FONT></a></td>\n");
         else out.println("Mode</a></td>\n");
         // Type
         out.println("<td width=130><a href=\"" + getServletPath("viewFile")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TYPE\">");
         if(choosen.equals("TYPE"))
            out.println("<FONT color=saddlebrown><b>Format</b></FONT></a></td>\n");
         else out.println("Format</a></td>\n");
         //      // Status
         //      out.println("<td width=100><a href=\"" + getServletPath("viewFile")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=STATUS\">");
         //      if(choosen.equals("STATUS"))
         //        out.println("<FONT color=saddlebrown><b>Status</b></FONT></a></td>\n");
         //      else out.println("Status</a></td>\n");
         // Comment
         out.println("<td width=150><a href=\"" + getServletPath("viewFile")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\">");
         if(choosen.equals("COMM"))
            out.println("<FONT color=saddlebrown><b>Comments</b></FONT></a></td>\n");
         else out.println("Comment</a></td>\n");
         // USER
         out.println("<td width=50><a href=\"" + getServletPath("viewFile")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
         else out.println("User</a></td>\n");
         // Updated
         out.println("<td width=120><a href=\"" + getServletPath("viewFile")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TS\">");
         if(choosen.equals("TS"))
            out.println("<FONT color=saddlebrown><b>Updated</b></FONT></a></td>\n");
         else out.println("Updated</a></td>\n");
         /*
           out.println("<td width=60>&nbsp;</td>"); // Details
           out.println("<td width=40>&nbsp;</td>"); // Edit
           out.println("</table>");
           out.println("</body></html>");
         */



         out.println("<td width=60>&nbsp;</td>");
         out.println("<td width=40>&nbsp;</td>");
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
   
   
   /**
    * 
    * The bottom frame
    */
   private void writeBottom(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException 
   {
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
         pid = (String) session.getAttribute("PID");
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

         conn = (Connection) session.getAttribute("conn");
         stmt = conn.createStatement();
         StringBuffer sbSQL = new StringBuffer(512);
         sbSQL.append("SELECT PID, FGID, NAME, MODE_, TYPE, XMSID, XVSID, " +
                      "COMM, USR, to_char(TS, '" + getDateFormat(session) + "') as TC_TS " +
                      "FROM V_FILE_GENERATIONS_2 WHERE ");
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);

         sbSQL.append(" ORDER BY ").append(orderby);
         //out.println(sbSQL.toString());
         rset = stmt.executeQuery(sbSQL.toString());
       
         out.println("<TABLE align=left border=0 cellPadding=0");
         out.println("cellSpacing=0 width=753 style=\"margin-left:2px\">");
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
            out.println("<TD WIDTH=150>" + formatOutput(session, rset.getString("NAME"),18) +"</TD>");
            out.println("<TD WIDTH=50>" + formatOutput(session, rset.getString("MODE_"),2)+"</TD>");
            out.println("<TD WIDTH=130>" + formatOutput(session, rset.getString("TYPE"),15)+"</TD>");
            //				out.println("<TD WIDTH=100>" + formatOutput(session, rset.getString("NAME"), 6) + "</TD>"); // STATUS
            out.println("<TD WIDTH=150>" + formatOutput(session, rset.getString("COMM"), 18) + "</TD>"); // SIZE
            out.println("<TD WIDTH=50>" + formatOutput(session, rset.getString("USR"), 6) + "</TD>");
            out.println("<TD WIDTH=120>" + formatOutput(session, rset.getString("TC_TS"), 18) + "</TD>");
            //out.println("<TD WIDTH=60><A HREF=\"" + getServletPath("viewFile/files?fgid=") +
              //          rset.getString("FGID") + "&" +
                //        oldQS + "\" target=\"content\">Files</A></TD>");
            out.println("<TD WIDTH=40><A HREF=\"" + getServletPath("viewFile/edit?fgid=") +
                        rset.getString("FGID") + "&" +
                        oldQS + "\" target=\"content\">Edit</A></TD>");
           
            out.println("<TD WIDTH=60><A HREF=\"" + getServletPath("viewFile/details?fgid=") +
                        rset.getString("FGID") + 
                        "&" + oldQS + "\" target=\"content\">Details</A></TD>");
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

   /**
    * Startting a new file export.
    */
   private void writeStart(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      //		HttpSession session = req.getSession(true);
      //		Connection conn =  null;
      //		String  oper = req.getParameter("oper");
      //    String type = req.getParameter("t");
      //		conn = (Connection) session.getAttribute("conn");
      //		if (oper == null) oper = "SEL_CHANGED";
      //		if (oper.equals("CREATE")) {
      //			if (create(req, res, conn)) {
      // Modify request to fit the edit page and call writeEditPage
      //				writeFrame(req, res);
      //      } else {
      //        ; // We have already displayed an error message!
      //      }
      //		} else {
      //      if (type != null && type.equals("TAB_DEL")) {
      //         writeStartTabDel(req, res);
      //      } else if (type != null && type.equals("LINKAGE")) {
      //        res.sendError(res.SC_NOT_IMPLEMENTED);
      //      } else if (type != null && type.equals("CRIMAP")) {
      //        res.sendError(res.SC_NOT_IMPLEMENTED);
      //      } else {
      writeStartPage(req, res);
      //      }
      //		}
   }
   
   /**
    * Write the HTML page to display then a new file export should be 
    * started.
    *
    */
   private void writeStartPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException 
   {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getAttribute("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String sid = null, newQS, pid, oper, item;
      try 
      {
         conn = (Connection) session.getAttribute("conn");
         pid = (String) session.getAttribute("PID");
         sid = req.getParameter("sid");
         newQS = req.getQueryString(); // removeOper(req.getQueryString());
         oper = req.getParameter("oper");

         if (oper == null || oper.trim().equals("")) oper = "SEL_CHANGED";
         item = req.getParameter("item");
         if (item == null || item.trim().equals("")) item = "";
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeStartScript(out);
         out.println("<title>Start file export</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<p align=\"center\">" +
         //out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Export file - New</b>");//</center>
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<form>"); // method=post action=\"" +
         //        getServletPath("viewFile/start?") + newQS + "\">");

         out.println("<table width=400 border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table>");
         // Species
         out.println("<tr>");
         out.println("<td>Species<br>");
         out.println("<select name=s width=200 " +
                     "style=\"WIDTH: 200px\">");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID, NAME FROM V_SPECIES_2 WHERE " +
                                  "PID=" + pid + " ORDER BY NAME");
         while (rset.next() ) {
            out.println("<option value=\"" + rset.getString("SID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td>");
         out.println("</tr>");
         // Type of generation
         // this should be dynamicly loaded in some way
         out.println("<tr>");
         out.println("<td nowrap>Fileformat<br>");
         out.println("<select name=t width=200 " +
                     "style=\"WIDTH: 200px\">");
         out.println("<option selected value=\"TAB_DEL\">" +
                     "General Table Format</option>");
         out.println("<option value=\"LINKAGE\">Linkage</option>");
         out.println("<option value=\"CRIMAP\">Crimap</option>");
         out.println("<option value=\"MAPMAKER\">MapMaker</option>");

         out.println("</select>");
         out.println("</td>");
         out.println("</tr>");
         // Name
         out.println("<tr>");
         out.println("<td>Name<br>");
         out.println("<input type=text name=n value=\"\" width=200 " +
                     "style=\"WIDTH: 200px\" maxlength=20>");
         out.println("</td>");
         out.println("</tr>");
         // Comment
         out.println("<tr>");
         out.println("<td>Comment<br>");
         out.println("<input type=text name=c value=\"\" width=200 " +
                     "style=\"WIDTH: 200px\">");
         out.println("</td>");
         out.println("</tr>");
         // Mode
         out.println("<tr>");
         out.println("<td colspan=3>Mode<br>");
         out.println("<input name=m type=radio checked value=\"S\">Single Sampling Unit<br>");
         out.println("<input name=m type=radio value=\"M\">Multiple Sampling Units<br>");
         out.println("</td>");
         out.println("</tr>");
         // Some buttons
         out.println("<tr>");
         out.println("<td colspan=3>");
         out.println("<table border=0 cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td><input type=button value=\"Next\" " +
                     "width=100 style=\"WIDTH: 100px\" " +
                     "onClick='startGen();'>");
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


   private void writeStartScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function selChanged(item) {");
      out.println("  document.forms[0].item.value = \"\" + item;");
      out.println("  document.forms[0].oper.value = \"SEL_CHANGED\";");
      out.println("  document.forms[0].submit();");
      out.println("}");
      out.println("function startGen() {");
      out.println("  var uri;");
      out.println("  var selInd=document.forms[0].t.selectedIndex;");
      out.println("  if ( (\"\" + document.forms[0].n.value).length == 0) {");
      out.println("    alert('You must supply a name!');");
      out.println("    return(false);");
      out.println("  }");
      out.println("  if (document.forms[0].t[selInd].value == 'TAB_DEL') {");
      out.println("    uri=\"" + getServletPath("startTabDel") + "\";");
      out.println("  } else if (document.forms[0].t[selInd].value == 'CRIMAP') {");
      out.println("    uri=\"" + getServletPath("startCrimap") + "\";");
      ///
      out.println("  } else if (document.forms[0].t[selInd].value == 'LINKAGE') {");
      out.println("    uri=\"" + getServletPath("startLinkage") + "\";");
      ///
      out.println("  } else if (document.forms[0].t[selInd].value == 'MAPMAKER') {");
      out.println("    uri=\"" + getServletPath("startMapMaker") + "\";");

      out.println("  }");
      out.println("");
      out.println("  uri = uri + \"?n=\" + document.forms[0].n.value + \"&c=\" +");
      out.println("        document.forms[0].c.value + \"&s=\" + document.forms[0].s[document.forms[0].s.selectedIndex].value +");
      out.println("        \"&t=\" + document.forms[0].t[selInd].value + \"&m=\";");
      out.println("  if(document.forms[0].m[0].checked == true)");
      out.println("    uri = uri + \"S\";");
      out.println("  else");
      out.println("    uri = uri + \"M\";");
      out.println("  document.location=uri;");
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
      out.println("//		if (confirm('Are you sure that you want to create the filter?')) {");
      out.println("			document.forms[0].oper.value = ''");
      out.println("			document.forms[0].submit();");
      out.println("//		}");
      out.println("	}");
      out.println("	");
      out.println("	");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
   }

   
    /** Displays the Details page where the file are retrieved.
     */
   
    private void writeDetails(HttpServletRequest req,
                             HttpServletResponse res) 
      throws ServletException, IOException
   {
         
         String fgid = req.getParameter("fgid");
         String name = null;
         String comm = null;
         String format = null;
         String mode = null;
         String usr = null;
         String update = null;
         Statement stmt = null;
         ResultSet rset = null;
         Connection conn = null;
         HttpSession session = req.getSession(true);
         checkRedirectStatus(req, res);
         String oldQS = req.getQueryString();
         try { 
            conn = (Connection) session.getAttribute("conn");
            stmt = conn.createStatement();
            String SQL = "SELECT PID, FGID, NAME, MODE_, TYPE, XMSID, XVSID, " +
                      "COMM, USR, to_char(TS, '" + getDateFormat(session) + "') as TC_TS " +
                      "FROM V_FILE_GENERATIONS_2 WHERE FGID=" + fgid;
            rset = stmt.executeQuery(SQL);
            if (rset.next()){
                name = rset.getString("NAME");
                comm = rset.getString("COMM");
                format = rset.getString("TYPE");
                mode = rset.getString("MODE_");
                usr = rset.getString ("USR");
                update = rset.getString("TC_TS");
            }    
        
         res.setContentType("text/html");
         res.setHeader("Pragma", "no-cache");
         res.setHeader("Cache-Control", "no-cache");
         PrintWriter out = res.getWriter();
         out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out.println("<html>\n"
                     + "<head>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
        
         
         //title
         out.println("<title>Details</title>\n"
                     + "<META HTTP_EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out.println("</head>\n<body>\n");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Export - Details</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         // the whole information table    
         out.println("<table border=0 cellspacing=0 cellpadding=0><tr>" +
                    "<td width=15></td><td></td></tr>");
        
         out.println("<tr><td></td><td>");
         
         // static data table
         out.println("<table nowrap border=0 cellSpacing=0>"); 
         out.println("<tr><td width=300 colspan=3 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
         
         //Format
         out.println("<tr><td>Format</td><td>"+formatOutput(session, format, 25)+"</td>");
         //out.println("<td><a href=\"" + getServletPath("viewRes/download?") +
          //          "rid=" + rid + "&file=res\">Download</a></td></tr>");
         
         //Mode
         if(mode.trim().compareTo("S")==0)
             mode = "Single";
         if(mode.trim().compareTo("M")==0)
             mode = "Multi";
         out.println("<tr><td>Mode</td><td>"+formatOutput(session, mode, 25)+"</td>"); 
        // if(curr_b_name!=null)
         //   out.println("<td><a href=\"" + getServletPath("viewRes/download?") + "rid=" + rid + "&file=batch\">Download</a></td></tr>");
         
         //Exported files     
         out.println("<tr><td>Exported files</td>");
         if(fgid!=null)
            out.println("<td><a href=\"" + getServletPath("viewFile/files?") + "fgid=" + fgid + "\">Download</a></td></tr>");
         
         //Creation date
         out.println("<tr><td>Created</td><td>"); // + formatOutput(session, curr_c_ts, 25) + "</td></tr>");
         
         
         out.println("<tr><td></td><td></td></tr><tr><td></td><td></tr>"); 
         out.println("</table>"); 
    
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         
         // current data table
         out.println("<table nowrap align=left border=0 cellSpacing=0 width=840px>");
         out.println("<tr bgcolor=Black><td align=center colspan=5><b><font color=\"#ffffff\" >Current Data</font></b></td></tr>");
         out.println("<tr bgcolor= \"#008B8B\" >");
         out.println("<td nowrap WIDTH=110px>Name</td>");
         out.println("<td nowrap WIDTH=110px>Comment</td>");   
         out.println("<td nowrap WIDTH=50px>User</td>");
         out.println("<td nowrap WIDTH=120px>Last updated</td></tr>");
         out.println("<tr><td></td><td></td><td></td><td></td>"); 
         out.println("<tr bgcolor=white>");
         out.println("<td>" + formatOutput(session, name, 12) + "</td>");
         out.println("<td>" + formatOutput(session, comm, 25) + "</td>");
         out.println("<td>" + formatOutput(session, usr, 12) + "</td>");
         out.println("<td>" + formatOutput(session, update, 18) + "</td></tr>");
         
         // Back button
         out.println("<tr><td></td><td></td></tr><tr><td></td><td></td></tr>");
         out.println("<form>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("viewFile?&")+ oldQS +"\"'>&nbsp;");
         
         out.println("</form>");
         out.println("</td></tr></table>"); //current data
         out.println("</td></tr></table>");  //information table
         out.println("</body></html>");
        
         }catch (SQLException e) {
            ;
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
      conn = (Connection) session.getAttribute("conn");
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
      Connection conn = (Connection) session.getAttribute("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String newQS, pid, fgid, name, comm, oper, item;
      try {
         conn = (Connection) session.getAttribute("conn");
         pid = (String) session.getAttribute("PID");
         fgid = req.getParameter("fgid");
         newQS = removeQSParameterOper(req.getQueryString());
         oper = req.getParameter("oper");

         if (oper == null || oper.trim().equals("")) oper = "SEL_CHANGED";
         item = req.getParameter("item");
         if (item == null || item.trim().equals("")) item = "";

         // Retrieve the data for this filter
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT FGID, NAME, COMM, MODE_, TYPE, " +
                                  "XMSNAME, XVSNAME FROM V_FILE_GENERATIONS_3 WHERE FGID=" + fgid);
         if (!rset.next() ) {
            // This should almost be impossible
            throw new Exception("Analysis query didn't return any data.");
         }
         name = rset.getString("NAME");
         comm = rset.getString("COMM");
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeEditScript(out);

         out.println("<title>Edit analysis</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Export - Edit</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<form method=post action=\"" +
                     getServletPath("viewFile/edit?") + newQS + "\">");

         out.println("<table width=400 border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table>");
         out.println("<tr>");

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
         // Some buttons
         out.println("<tr>");
         out.println("<td colspan=3>");
         out.println("<table border=0 cellspacing=0 cellpading=0>");
         out.println("<tr>");
         // Back button
         out.println("<td><input type=button value=\"Back\" " +
                     "width=70 style=\"WIDTH: 70px\" " +
                     "onClick='Javascript:location.href=\"" +
                     getServletPath("viewFile?") + newQS + "\";'>&nbsp;</td>");
         out.println("<td><input type=button width=70 style=\"WIDTH: 70px\" " +
                     "value=\"Update\" onClick='valForm(\"UPDATE\");'>&nbsp;</td>");
         out.println("<td><input type=button value=\"Delete\" " +
                     "width=70 style=\"WIDTH: 70px\" " +
                     "onClick='valForm(\"DELETE\");'>&nbsp;</td>");
         out.println("</tr>");
         out.println("</table>");
         out.println("</td>");
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
   private void writeFiles(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getAttribute("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String newQS, pid, fgid, name, comm, oper, item, rid; 
      //the rid is used when this method is called from Results - details
      String status;
      boolean allDone=true;
      boolean oneError=false;
      boolean noFiles = true;

      try {
         conn = (Connection) session.getAttribute("conn");
         pid = (String) session.getAttribute("PID");
         fgid = req.getParameter("fgid");
         rid = req.getParameter("rid");
        
         newQS = removeQSParameterOper(req.getQueryString());
         oper = req.getParameter("oper");

         if (oper == null || oper.trim().equals("")) oper = "SEL_CHANGED";
         item = req.getParameter("item");
         if (item == null || item.trim().equals("")) item = "";

         // check if we need to refresh
         // if all files are done or if one is in error, we do not

         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT STATUS " +
                                  "FROM V_DATA_FILES_2 WHERE FGID=" + fgid);

         while (rset.next())
         {
            noFiles = false;
            status = rset.getString("STATUS");
            if (!status.equalsIgnoreCase("DONE"))
            {
               allDone=false;
            }
            if (status.equalsIgnoreCase("ERROR"))
            {
               oneError = true;
            }
         }
         if (rset != null) rset.close();
         if (stmt != null) stmt.close();

         // Retrieve the data for this filter
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT FGID, DFID, NAME, STATUS, COMM, USR, TS " +
                                  "FROM V_DATA_FILES_2 WHERE FGID=" + fgid);
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/fileList.css")); //axDefault

         //refresh if not all are done and there is no error
         if(allDone==false && oneError==false)
         {
            out.println("<META HTTP-EQUIV=\"REFRESH\" CONTENT=\"3; URL="+getServletPath("viewFile/files?MODE=AUTO&fgid="+fgid)+"\">");
         }
         // refresh also if no files were found ..(yet)
         else if (noFiles==true)
         {
            out.println("<META HTTP-EQUIV=\"REFRESH\" CONTENT=\"1; URL="+getServletPath("viewFile/files?MODE=AUTO&fgid="+fgid)+"\">");
         }

         out.println("<title>Files - selfupdating</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Export - Files</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<form method=post action=\"" +
                     getServletPath("viewFile/file?") + newQS + "\">");

         out.println("<table width=400 border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr bgcolor=\"008B8B\">");
         out.println("<td width=200>File</td>");
         //out.println("<td>&nbsp;</td>");
         out.println("<td width=75>Status</td>");
         //out.println("<td>&nbsp;</td>");
         out.println("<td width=75>User</td>");
         //out.println("<td width=100>&nbsp;</td>");
         out.println("<td>&nbsp;</td>");
         //out.println("<td>&nbsp;</td>");
            
         out.println("</tr>");

         boolean odd = true;
         while (rset.next()) {
            if (odd)
               out.println("<tr bgcolor=white>");
            else
               out.println("<tr bgcolor=lightgrey>");
            odd = !odd;
            out.println("<td>" + formatOutput(session, rset.getString("NAME"), 25) + "</td>");
            //out.println("<td>&nbsp;</td>");
            out.println("<td>" + formatOutput(session, rset.getString("STATUS"), 10) + "</td>");
            //out.println("<td>&nbsp;</td>");
            out.println("<td>" + formatOutput(session, rset.getString("USR"), 10) + "</td>");
            //out.println("<td>&nbsp;</td>");
            out.println("<td width=100><a href=\"" + getServletPath("viewFile/download?") +
                        "dfid=" + rset.getString("dfid") + "&" + newQS + "\">Download</a></td>");
            out.println("</tr>");
         }
         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         // Some buttons
         out.println("<table border=0 cellspacing=0 cellpading=0>");
         out.println("<tr>");
         // Back button
         // This method can be called either from Results - details or Export - details.
         if(rid!=null){
            out.println("<td><input type=button value=\"Back\" " +
                     "width=70 style=\"WIDTH: 70px\" " +
                     "onClick='Javascript:location.href=\"" +  
                     getServletPath("viewRes/details?rid=") + rid + "\";'>&nbsp;</td>");
         }
         else {    
            out.println("<td><input type=button value=\"Back\" " +
                     "width=70 style=\"WIDTH: 70px\" " +
                     "onClick='Javascript:location.href=\"" +  
                     getServletPath("viewFile?RETURNING=YES&") + newQS + "\";'>&nbsp;</td>");//details
         }
         out.println("</tr>");
         out.println("</table>");

         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=item value=\"\">");
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

   private void sendFile(HttpServletRequest req, HttpServletResponse res) {
      String dfid;
      String filename;
      String absPath;
      String contentType;
      String pid, fgid;
      FileInputStream fis = null;
      OutputStream out = null;
      byte[] buf = null;
      Connection conn = null;
      HttpSession session = req.getSession(false);
      Statement stmt = null;
      ResultSet rset = null;
      try {
         dfid = req.getParameter("dfid");
         pid = (String) session.getAttribute("PID");
         conn = (Connection) session.getAttribute("conn");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT FGID, NAME FROM V_DATA_FILES_1 " +
                                  "WHERE DFID=" + dfid);
         if (rset.next()) {
            fgid = rset.getString("FGID");
            filename = rset.getString("NAME");
            absPath = getFileGeneratePath() + "/" + pid + "/" + fgid;
            contentType = getServletContext().getMimeType(absPath + "/" + filename);
            if (contentType == null)
               contentType = new String("text/plain");
            res.setContentType(contentType);
            out = res.getOutputStream();
            fis = new FileInputStream(absPath + "/" + filename);
            buf = new byte[256 * 1024]; // 256 KB
            int bytesRead;
            while ((bytesRead = fis.read(buf)) != -1) {
               out.write(buf, 0, bytesRead);
            }
         }
      } catch (Exception e) {
         e.printStackTrace(System.err);
      }  finally {
         try {
            if (fis != null) fis.close();
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (Exception ignored) {}
      }
   }

   
   /**
    * Deletes a file generation
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if file generation was removed.
    *         False if generation was not removed.
    */
   private boolean delete(HttpServletRequest request,
                          HttpServletResponse response,
                          Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      String absPath;
      int fgid;
      int pid;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         absPath = getFileGeneratePath();

         pid = Integer.parseInt((String) session.getAttribute("PID"));
         fgid = Integer.parseInt(request.getParameter("fgid"));

         if (!deleteFileObject(absPath + "/" + pid + "/" + fgid))
         {
            errMessage = "Failed to delete file object " + absPath + "/" +
               pid + "/" + fgid;
            throw new Exception();
         }
         DbFileGeneration dbfg = new DbFileGeneration();
         dbfg.DeleteFileGeneration(connection, fgid);
         errMessage = dbfg.getErrorMessage();
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
                       "Generations.Edit.Delete", errMessage,
                       "viewFile/edit", isOk);
      return isOk;
   }


   /**
    * Updates a generation.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if generation was updated.
    *         False if generation was not updated.
    */
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
         String comm;
         String pid;
         int fgid;
         int id;
         String oldQS = request.getQueryString();
         name = request.getParameter("n");
         fgid = Integer.parseInt(request.getParameter("fgid"));
         comm = request.getParameter("c");
         id = Integer.parseInt((String) session.getAttribute("UserID"));
         DbFileGeneration dbfg = new DbFileGeneration();
         dbfg.UpdateFileGeneration(connection, name, comm, fgid, id);
         errMessage = dbfg.getErrorMessage();
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
                       "Generations.Edit.Update", errMessage, 
                       "viewFile/edit", isOk);
      return isOk;
   }


   private void writeEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to delete the generated files?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the generated files?')) {");
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
      int privileges[] = (int[]) session.getAttribute("PRIVILEGES");
      try {
         if (extPath == null || extPath.trim().equals("") ) extPath = "/";
         if (extPath.equals("/") ||
             extPath.equals("/bottom") ||
             extPath.equals("/middle") ||
             extPath.equals("/top") ) {
            // We neew the privilege FLT_R for all these
            title = "Export - Filters - View & Edit";
            if ( privDependentString(privileges, ANA_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/details") ) {
            // We neew the privilege FLT_R for all these
            title = "Export - Filters - Details";
            if ( privDependentString(privileges, FLT_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/edit") ) {
            // We neew the privilege FLT_W
            title = "Export - Filters - Edit";
            if ( privDependentString(privileges, FLT_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/new") ) {
            // We neew the privilege FLT_W
            title = "Export - Filters - New";
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

   private String getTypeOptions(HttpServletRequest req, HttpServletResponse res) 
   {
      StringBuffer out = new StringBuffer();
      String type;
      type = req.getParameter("type");
      if ("Linkage".equals(type)) {
         out.append("<option selected value=\"Linkage\">Linkage</option>\n");
         out.append("<option value=\"MapMaker\">MapMaker</option>\n");
         out.append("<option value=\"General Table Format\">General Table Format</option>\n");
         out.append("<option value=\"Crimap\">Crimap</option>\n");
         out.append("<option value=\"*\">*</option>\n");

      } else if ("Crimap".equals(type)) {
         out.append("<option value=\"Linkage\">Linkage</option>\n");
         out.append("<option selected value=\"Crimap\">Crimap</option>\n");
         out.append("<option value=\"General Table Format\">General Table Format</option>\n");
         out.append("<option value=\"MapMaker\">MapMaker</option>\n");
         out.append("<option value=\"*\">*</option>\n");

      }else if ("MapMaker".equals(type)) {
         out.append("<option value=\"Linkage\">Linkage</option>\n");
         out.append("<option selected value=\"MapMaker\">MapMaker</option>\n");
         out.append("<option value=\"General Table Format\">General Table Format</option>\n");
         out.append("<option value=\"Crimap\">Crimap</option>\n");
         out.append("<option value=\"*\">*</option>\n");
      }else if ("General Table Format".equals(type)) {
         out.append("<option value=\"Linkage\">Linkage</option>\n");
         out.append("<option value=\"MapMaker\">MapMaker</option>\n");
         out.append("<option selected value=\"General Table Format\">General Table Format</option>\n");
         out.append("<option value=\"Crimap\">Crimap</option>\n");
         out.append("<option value=\"*\">*</option>\n");
      }
      else {
         out.append("<option value=\"Linkage\">Linkage</option>\n");
         out.append("<option value=\"Crimap\">Crimap</option>\n");
         out.append("<option value=\"MapMaker\">MapMaker</option>\n");
         out.append("<option value=\"General Table Format\">General Table Format</option>\n");
         out.append("<option selected value=\"*\">*</option>\n");
      }
      return out.toString();
   }


   /**
    * Deletes the file system object pointed out by the given string. 
    *
    * @param deletePath The path to the object to remove.
    * @return True if object was removed.
    *         False if object was not removed.
    */
   /*
   private boolean deleteFileObject(String deletePath)
   {
      try
      {
         // Ensure that the path to object to delete contains the path to
         // the directory with generated files. If not, we are trying to
         // delete something which is not a generated file which is an
         // error. 
         if (deletePath.indexOf(getFileGeneratePath()) < 0)
         {
            throw new Exception("Delete path (" + deletePath +
                                ") does not contain the path to the " +
                                "directory with generated files (" +
                                getFileGeneratePath() + ").");
         }
      
         // Build a file object based on the delete path and check its
         // existence. If it does not exist, exit with true to indicate the
         // item is deleted.
         File fileSystemObject = new File(deletePath);
         if (!fileSystemObject.exists())
         {
            return true;
         }
         
         // If file object is a directory
         if (fileSystemObject.isDirectory())
         {
            // Get the contents of the directory and call deleteFileObject
            // recursively for each item in the directory. If any item can
            // not be deleted, we will bail out.
            String[] directoryItems = fileSystemObject.list();
            for (int i = 0; i < directoryItems.length; i++) 
            {
               Assertion.assertMsg(deleteFileObject(deletePath + "/" +
                                          directoryItems[i]),
                                "Failed to delete directory item " +
                                deletePath + "/" + directoryItems[i]);
            }
         }
         
         // Try to delete the file system object itself
         Assertion.assertMsg(fileSystemObject.delete(),
                          "Failed to delete the file system object " +
                          fileSystemObject.getPath());
      }
      catch (Exception e)
      {
         e.printStackTrace(System.err);
         return false;
      }
      return true;
   }
    */
}
