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


  Revision 1.11  2001/05/31 07:06:53  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.10  2001/05/22 06:54:30  roca
  backfunktionality for administrator pages and privileges removed from roles (user mode)

  Revision 1.9  2001/05/08 11:00:52  frob
  Removed unused methods: writeUser, writeUserPage, updateUser, writeUserScript
  and writeStatistics.

  Revision 1.8  2001/05/08 10:53:21  frob
  Changed all methods that used to call writeError to write an error page. These
  methods now calls the general method commitOrRollback which handles any errors.
  The writeError method is removed.

  Revision 1.7  2001/05/03 14:20:57  frob
  Implemented local version of errorQueryString and changed writeError to use this method.

  Revision 1.6  2001/05/03 07:57:33  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.5  2001/05/02 11:37:30  frob
  Calls to removeOper and removeSid modified to use the general removeQSParameter.
  The previously called methods are removed.

  Revision 1.4  2001/04/24 09:33:49  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.3  2001/04/24 06:31:22  frob
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

public class adminSpecies extends AdminArexisServlet
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
      } else if (extPath.equals("/chrom")) {
         writeChrom(req, res);
      } else if (extPath.equals("/editChrom")) {
         writeEditChrom(req, res);
      } else if (extPath.equals("/newChrom")) {
         writeNewChrom(req, res);
      } else if (extPath.equals("/impChrom")) {
         writeImpChrom(req, res);
      } else if (extPath.equals("/impChromMultipart")) {
         if (createChromFile(req, res))
            writeChrom(req, res);
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
         res =  checkRedirectStatus(req,res);
         //req=getServletState(req,session);
         
         String topQS = buildQS(req);
         // we don't want the oper parameter anaywhere but in the "edit" and "new" page!
         topQS = removeQSParameterOper(topQS);

         String bottomQS = topQS.toString();

         out.println("<html>"
                     + "<HEAD>"
                     + " <TITLE>Administrate Species</TITLE>"
                     + "</HEAD>"
                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" //
                     + "<frame name=\"spectop\" "
                     + "src=\""+ getServletPath("adminSpecies/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"specmiddle\" "
                     + "src=\""+ getServletPath("adminSpecies/middle?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"specbottom\""
                     + "src=\"" +getServletPath("adminSpecies/bottom?") + bottomQS + "\" "
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
         sid = null, // species id
         name = null,
         orderby = null;
      sid = req.getParameter("sid");
      name = req.getParameter("name");

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
      if (sid != null && !sid.trim().equals(""))
         output.append("&sid=").append(sid);
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
      rows = countRows( req, session);
      maxRows = getMaxRows(session);
      if (req.getParameter("STARTINDEX") != null) {
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
         sbSQL.append("SELECT count(sid) " +
                      "FROM V_SPECIES_1 WHERE 1=1 ");
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
      String name = null;
      //			     orderby = null;
      StringBuffer filter = new StringBuffer(256);
      /*    sid = req.getParameter("sid");
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
      */
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
      //		Connection conn = null;
      //		Statement stmt = null;
      //		ResultSet rset = null;
      int startIndex = 0, rows = 0, maxRows = 0;
      String name, oldQS, newQS, action;
      try {
         //			conn = (Connection) session.getValue("conn");

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

         out.println("<title>Species</title>");
         out.println("</head>");

         out.println("<body bgcolor=\"#ffffd0\">");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<form method=get action=\"" +getServletPath("adminSpecies") +"\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Species</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr><tr><td width=\"517\">");

         out.println("<table width=488 height=\"92\">" +
                     "<td><b>&nbsp;</td>");
         out.println("<td><b>&nbsp;</td>");
         out.println("<td><b>&nbsp;</td>");
         out.println("<td><b>&nbsp;</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td><b>&nbsp;</td>");
         out.println("<td><b>&nbsp;</b><br></td>");
         out.println("<td><b>&nbsp;</b><br></td></table></td>");

         //Buttons
         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");
         out.println("<input type=button value=\"New Species\"" +
                     " onClick='parent.location.href=\"" +getServletPath("adminSpecies/new?") + newQS + "\"' " +
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
         ;
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
            //out.println("<p align=left>&nbsp;&nbsp;");
            out.println("&nbsp;"+buildInfoLine(action, startIndex, rows, maxRows));
         }

         String oldQS, newQS;
         oldQS = req.getQueryString();
         String choosen= req.getParameter("ORDERBY");
         newQS = buildTopQS(oldQS);

         /*
           out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 " +
           "height=20 width=600 style=\"margin-left:2px\">" +
           "<td width=5></td>");
         */
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=600 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");



         // the menu choices
         // Name
         out.println("<td width=150><a href=\"" +
                     getServletPath("adminSpecies")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\">");
         if(choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown><b>Name</b></FONT></a></td>\n");
         else
            out.println("Name</a></td>\n");
         // Comment
         out.println("<td width=250><a href=\"" +
                     getServletPath("adminSpecies")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\">");
         if(choosen.equals("COMM"))
            out.println("<FONT color=saddlebrown><b>Comment</b></FONT></a></td>\n");
         else
            out.println("Comment</a></td>\n");
         /*
           out.println("<td width=100>&nbsp;</td>"); // Chromosomes
           //      out.println("<td width=100>&nbsp;</td>"); // Details
           out.println("<td width=100>&nbsp;</td>"); // Edit
           out.println("</tr></table>");
           //      out.println("</td></tr></table>");
           out.println("</body></html>");
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
         sbSQL.append("SELECT NAME, SID, COMM FROM V_SPECIES_1 WHERE 1=1");
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);
         sbSQL.append(" ORDER BY ").append(orderby);
         rset = stmt.executeQuery(sbSQL.toString());

         out.println("<TABLE align=left border=0 cellPadding=0");
         out.println("cellSpacing=0 width=600 style=\"margin-left:2px\">");//STYLE=\"WIDTH: 790px;\">");
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
            out.println("<TD WIDTH=250>" + formatOutput(session, rset.getString("COMM"),30)+"</TD>");

            out.println("<TD WIDTH=100><A HREF=\"" +getServletPath("adminSpecies/chrom?sid=")
                        + rset.getString("SID")
                        + "&" + oldQS + "\" target=\"adminmainframe\">Chromosomes</A></TD>");
            //				out.println("<TD WIDTH=100><A HREF=\"" +getServletPath("adminSpecies/details?sid=")
            //							+ rset.getString("SID")
            //							+ "&" + oldQS + "\" target=\"adminmainframe\">Details</A></TD>");
            out.println("<TD WIDTH=95><A HREF=\"" +getServletPath("adminSpecies/edit?sid=")
                        + rset.getString("SID")
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
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String newQS, oper;
      try {
         conn = (Connection) session.getValue("conn");
         newQS = removeQSParameterOper(req.getQueryString());
         oper = req.getParameter("oper");

         //			if (oper == null || oper.trim().equals("")) oper = "SEL_CHANGED";
         //			item = req.getParameter("item");
         //			if (item == null || item.trim().equals("")) item = "";
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeNewScript(out);
         out.println("<title>New Species</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Species - New</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=get action=\"" +
                     getServletPath("adminSpecies/new?") + newQS + "\">");
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
                    // getServletPath("adminSpecies?") + newQS + "\"'>");
                    getServletPath("adminSpecies?&RETURNING=YES")  + "\"'>");
         out.println("&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Create width=100 " +
                     "style=\"WIDTH: 100px\" onClick='valForm()'>");
         out.println("&nbsp;</td></tr></table>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("<input type=hidden name=RETURNIG value=YES>");

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
    * The new chromosome page
    */
   private void writeNewChrom(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createChrom(req, res, conn)) {
            writeChrom(req, res);
         } else {
            ; // We have already displayed an error message!
         }
      } else {
         writeNewChromPage(req, res);
      }
   }
   private void writeNewChromPage(HttpServletRequest req, HttpServletResponse res)
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
         out.println("<title>New Chromosome</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Species - Chromosome - New</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" +
                     getServletPath("adminSpecies/newChrom?") + newQS + "\">");
         out.println("<table width=400 border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("Name<br>");
         out.println("<input name=n maxlength=2 width=200 " +
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
                     getServletPath("adminSpecies/chrom?") + newQS + "\"'>");
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
   private void writeImpChrom(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      conn = (Connection) session.getValue("conn");
      writeImpChromPage(req, res);
   }

   /**
    * Creates the page used for importing chromosomes to species.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @exception IOException If no PrintWriter can be retrieved from the
    *            response object.
    */
   private void writeImpChromPage(HttpServletRequest request,
                                  HttpServletResponse response)
      throws IOException
   {
      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");

      PrintWriter out = response.getWriter();
      try
      {
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
         out.println("	if (rc) {");
         out.println("		if (confirm('Are you sure that you want to create the chromosomes?')) {");
         out.println("			document.forms[0].oper.value = 'UPLOAD';");
         out.println("			document.forms[0].submit();");
         out.println("		}");
         out.println("	}");
         out.println("	");
         out.println("	");
         out.println("}");
         out.println("//-->");
         out.println("</script>");
         out.println("<title>Import chromosomes</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=0>" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Species - Chromosomes - Import</b></center>" +
                     "</td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post enctype=\"multipart/form-data\" action=\"" +
                     getServletPath("adminSpecies/impChromMultipart?") + newQS + "\">");
         out.println("<table border=0>");

         // File
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td>File<br>");
         out.println("<input type=file name=filename style=\"WIDTH: 350px\">");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td></td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table border=0><tr>");
         out.println("<td>");
         out.println("<input type=button value=Back " +
                     "style=\"WIDTH: 100px\" onClick='document.location.href=\"" +
                     getServletPath("adminSpecies/chrom?") + newQS + "\"'>");
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
      out.println("		if (confirm('Are you sure that you want to create the species?')) {");
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
      if (oper.equals("DELETE"))
      {
         if (delete(req, res, conn))
         {
            writeFrame(req, res);
         }
      }
      else if (oper.equals("UPDATE"))
      {
         if (update(req, res, conn))
         {
            writeEditPage(req, res);
         }
      }
      else
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
         String sid = req.getParameter("sid");

         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);
         out.println("<title>Edit Species</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Species - Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         stmt = conn.createStatement();
         String sql = "SELECT NAME, COMM " +
            "FROM V_Species_1 WHERE " +
            "SID=" + sid;
         rset = stmt.executeQuery(sql);
         rset.next();
         String name = rset.getString("NAME");
         String comm = rset.getString("COMM");
         rset.close();
         stmt.close();
         // oldQS contains iid and mid!
         // Belowe we use rather cryptic names for the form data. We do this to prevent that
         // the data in the form won't collide with the data in the old query string
         out.println("<FORM action=\"" + getServletPath("adminSpecies/edit?") +
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
         out.println("</tr>");
         out.println("<tr><td>Comment<br>");
         out.println("<textarea name=c  cols=40 rows=10>");
         out.print(formatOutput(session, comm, 256));
         out.println("</textarea>");
         out.println("</tr>");
         out.println("<tr><td>&nbsp;</td></tr>");
         out.println("<tr><td>");
         out.println("<table cellspacing=0 cellpadding=0 border=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                    // getServletPath("adminSpecies?") + oldQS + "\"'>&nbsp;");
                    getServletPath("adminSpecies?&RETURNING=YES")  + "\"'>&nbsp;");
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
   /***************************************************************************************
    * *************************************************************************************
    * The edit chromosome page
    */
   private void writeEditChrom(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";
      if (oper.equals("DELETE")) {
         if (deleteChrom(req, res, conn))
            writeChrom(req, res);
      } else if (oper.equals("UPDATE")) {
         if(updateChrom(req, res, conn))
            writeEditChromPage(req, res);
      } else
         writeEditChromPage(req, res);
   }
   private void writeEditChromPage(HttpServletRequest req, HttpServletResponse res)
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
         String cid = req.getParameter("cid");

         out.println("<html>");
         out.println("<head>");
         writeChromEditScript(out);
         out.println("<title>Edit Chromosome</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Species - Chromosomes - Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("adminSpecies/editChrom?") +
                     oldQS + "\">");

         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, COMM FROM " +
                                  "V_CHROMOSOMES_1 WHERE CID=" + cid);
         rset.next();
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");
         out.println("<table border=0 cellpading= cellspacing=0></tr>");
         out.println("<td width=300 style=\"WIDTH: 300px\">");
         out.println("Name<br>");
         out.println("<input type=text name=n maxlength=2 width=250 " +
                     "style=\"WIDTH: 250px\" " +
                     "value=\"" + formatOutput(session, rset.getString("NAME"), 3) + "\"></td></tr>");
         out.println("<tr><td>Comment<br>");
         out.println("<textarea name=c cols=40 rows=10>");
         out.println(formatOutput(session, rset.getString("COMM"), 256));
         out.println("</textarea>");
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
                     getServletPath("adminSpecies/chrom?") + oldQS + "\"'>&nbsp;");
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
         out.println("<input type=\"hidden\" NAME=cid value=\"" + cid + "\">");
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
    * The chromosome page
    */
   private void writeChrom(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      Connection conn =  (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      boolean odd;
      try {
         String oldQS = buildQS(req);
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrieve the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         String sid = req.getParameter("sid");
         String sql;
         out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out.println("<html>");
         out.println("<head>");
         //			writeUserScript(out);
         out.println("<title>Chromosomes</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Species - Chromosomes</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
         out.println("<form method=post action=\"" + getServletPath("adminSpecies/chrom?") +
                     oldQS + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         stmt = conn.createStatement();
         String sql_ = "SELECT CID, NAME, COMM FROM " +
                                  "v_chromosomes_1 where sid=" + sid + " ORDER BY to_number_else_null(NAME), NAME";
         
         System.out.println("SQL="+sql_);
         
         rset = stmt.executeQuery(sql_);
         out.println("<table cellpading=0 cellspacing=0>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td width=200 style=\"WIDTH: 50px\">Name</td>");
         out.println("<td width=200 style=\"WIDTH: 300px\">Comment</td>");
         out.println("<td width=300 style=\"WIDTH: 50px\">&nbsp;</td>");
         out.println("<td width=50 style=\"WIDTH: 50px\">&nbsp;</td>");
         out.println("</tr>");
         String bgcolor = "white";
         odd = true;
         while (rset.next() ) {
            if (odd)
               out.println("<tr bgcolor=white>");
            else
               out.println("<tr bgcolor=lightgrey>");
            out.println("<td>");
            out.println(formatOutput(session, rset.getString("NAME"), 3));
            out.println("</td>");
            out.println("<td>" + formatOutput(session, rset.getString("COMM"), 30) + "</td>");
            out.println("<td>&nbsp;</td>");
            out.println("<td><a href=\"" + getServletPath("adminSpecies/editChrom?") +
                        oldQS + "&cid=" + rset.getString("CID") + "\">edit</td></tr>");
            odd = !odd;
         }

         //      out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr>");
         out.println("<tr><td colspan=2>");
         out.println("<table><tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" " +
                     "onClick='JavaScript:location.href=\""
                     //+ getServletPath("adminSpecies?") +oldQS + "\";'>&nbsp;");
                     + getServletPath("adminSpecies?&RETURNING=YES")  + "\";'>&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Import width=100 " +
                     "style=\"WIDTH: 100px\" " +
                     "onClick='JavaScript:location.href=\"" + getServletPath("adminSpecies/impChrom?") +
                     oldQS + "\";'>&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=\"Create new\" width=100 " +
                     "style=\"WIDTH: 100px\" " +
                     "onClick='JavaScript:location.href=\"" + getServletPath("adminSpecies/newChrom?") +
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
      } 
      catch (Exception e)	
      {
            e.printStackTrace(); // Write to server log.

            out.println("<PRE>");
            e.printStackTrace(out);
            out.println("</PRE>");
      } 
      finally 
      {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
   }

   
   /**
    * Creates a new species.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if species was created.
    *         False if species was not created.
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
         DbSpecies dbs = new DbSpecies();
         dbs.CreateSpecies(connection, name, comm);
         errMessage = dbs.getErrorMessage();
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

      commitOrRollback(connection, request, response, "Species.New.Create",
                       errMessage, "adminSpecies/new", isOk);
      return isOk;
   }


   /**
    * Creates a chromosome in a species.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if chromosome was created.
    *         False if chromosome was not created.
    */
   private boolean createChrom(HttpServletRequest request,
                               HttpServletResponse response,
                               Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         String name = null, comm = null, sid= null;
         connection.setAutoCommit(false);
         name = request.getParameter("n");
         comm = request.getParameter("c");
         sid = request.getParameter("sid");
         DbChromosome dbc = new DbChromosome();
         dbc.CreateChromosome(connection, name, comm, Integer.parseInt(sid));
         errMessage = dbc.getErrorMessage();
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
                       "Species.Chromosomes.New.Create", errMessage,
                       "adminSpecies/chrom", isOk);
      return isOk;
   }
   

   /**
    * Creates chromosomes read from a given file.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if chromosomes was created.
    *         False if any error occours.
    * @exception IOException If writing error page fails.
    * @exception ServletException If writing error page fails.
    */
   private boolean createChromFile(HttpServletRequest request,
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
         String speciesId = request.getParameter("sid");

         FileParser fileParser = null;
         Enumeration fileEnum = multiRequest.getFileNames();
         if (fileEnum.hasMoreElements())
         {
            String givenFileName = (String) fileEnum.nextElement();
            String systemFileName =
               multiRequest.getFilesystemName(givenFileName);

            DbChromosome dbChromosome = new DbChromosome();
            
            fileParser = new FileParser(upPath + "/" +  systemFileName); // (byte)';');
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.CHROMOSOME,
                                                                        FileTypeDefinition.LIST));
                             
            dbChromosome.CreateChromosomes(fileParser, connection, Integer.parseInt(speciesId) );
            errMessage = dbChromosome.getErrorMessage();
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
                       "Species.Chromosomes.Import.Send", errMessage,
                       "adminSpecies/chrom", isOk);
      return isOk;
   }


   /**
    * Deletes a species.
    *
    * @param request The request object to use.
    * @param response The respone object to use.
    * @param connection The connection object to use.
    * @return True if the species was deleted.
    *         False if the species was not deleted.
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
         int sid;
         connection.setAutoCommit(false);
         sid = Integer.parseInt(request.getParameter("sid"));
         DbSpecies dbs = new DbSpecies();
         dbs.DeleteSpecies(connection, sid);
         errMessage = dbs.getErrorMessage();
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
                       "Species.Edit.Delete", errMessage,
                       "adminSpecies/edit", isOk);
      return isOk;
   }

   
   /**
    * Deletes a chromosome from a species.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if chromosome was deleted.
    *         False if chromosome was not deleted.
    */
   private boolean deleteChrom(HttpServletRequest request,
                               HttpServletResponse response,
                               Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int cid;
         connection.setAutoCommit(false);
         cid = Integer.parseInt(request.getParameter("cid"));
         DbChromosome dbc = new DbChromosome();
         dbc.DeleteChromosome(connection, cid);
         errMessage = dbc.getErrorMessage();
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
                       "Species.Chromosomes.Edit.Delete", errMessage,
                       "adminSpecies/chrom", isOk);
      return isOk;
   }


   /**
    * Updates a species.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if species was updated.
    *         False if species was not updated.
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
         int sid;
         String oldQS = request.getQueryString();
         name = request.getParameter("n");
         comm = request.getParameter("c");
         sid = Integer.parseInt(request.getParameter("sid"));
         DbSpecies dbs = new DbSpecies();
         dbs.UpdateSpecies(connection, sid, name, comm);
         errMessage = dbs.getErrorMessage();
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
                       "Species.Edit.Update", errMessage,
                       "adminSpecies/edit", isOk); 
      return isOk;
   }




   /**
    * Updates a chromosome in a species.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if the chromosome was updated.
    *         False if the chromosome was not updated.
    */
   private boolean updateChrom(HttpServletRequest request,
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
         int cid;
         String oldQS = request.getQueryString();
         cid = Integer.parseInt(request.getParameter("cid"));
         name = request.getParameter("n");
         comm = request.getParameter("c");
         DbChromosome dbc = new DbChromosome();
         dbc.UpdateChromosome(connection, cid, name, comm);
         errMessage = dbc.getErrorMessage();
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
                       "Species.Chromosomes.Edit.Update", errMessage,  
                       "adminSpecies/chrom", isOk);
      return isOk;
   }

   private void writeEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Deleting a species has the following consequenses:\\n' + ");
      out.println("       '  All chromosomes for the species will be deleted\\n' + ");
      out.println("       '  All library markers for the species will be deleted\\n' + ");
      out.println("       '  All unified markers for the species will be deleted\\n' + ");
      out.println("       '  All unified marker sets for the species will be deleted\\n' + ");
      out.println("       '  All unified variables for the species will be deleted\\n' + ");
      out.println("       '  All unified variable sets for the species will be deleted\\n' + ");
      out.println("       '  All sampling units of the species will be deleted\\n' + ");
      out.println("       '  All individuals of the species will be deleted\\n' + ");
      out.println("       '  All markers for the species will be deleted\\n' + ");
      out.println("       '  All marker sets for the species will be deleted\\n' + ");
      out.println("       '  All variables for the species will be deleted\\n' + ");
      out.println("       '  All variable sets for thespecies will be deleted\\n' + ");
      out.println("       '  All Geno- and phenotype data will be deleted\\n' + ");
      out.println("       '  etc.' + ");
      out.println("       'In other words, everything that in any why is related to\\n' + ");
      out.println("       'this species will be deleted.\\n\\n' + ");
      out.println("       'ARE YOU ABSOLUTELY SURE OF THAT THIS IS WHAT YOU WISH TO DO?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the species?')) {");
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

   private void writeChromEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Deleting a chromosome has the following consequenses:\\n' + ");
      out.println("       '  All library markers for the chromosome will be deleted\\n' + ");
      out.println("       '  All unified markers for the chromosome will be deleted\\n' + ");
      out.println("       '  All unified variables for the chromosome will be deleted\\n' + ");
      out.println("       '  All markers for the chromosome will be deleted\\n' + ");
      out.println("       '  All Genotype data will be deleted\\n' + ");
      out.println("       '  etc.' + ");
      out.println("       'In other words, everything that in any why is related to\\n' + ");
      out.println("       'this chromosome will be deleted.\\n\\n' + ");
      out.println("       'ARE YOU ABSOLUTELY SURE OF THAT THIS IS WHAT YOU WISH TO DO?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the chromosome?')) {");
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
    * page. Builds the original query string and removes the "oper"
    * parameter. 
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
