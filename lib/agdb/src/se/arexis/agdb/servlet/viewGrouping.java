/*
  $Log$
  Revision 1.9  2005/02/08 16:03:21  heto
  DbIndividual is now complete. Some bug tests are done.
  DbSamplingunit is converted. No bugtest.
  All transactions should now be handled in the GUI (yuck..)

  Revision 1.8  2005/02/07 15:54:01  heto
  Converted DbIndividual to PostgreSQL
  Now some transaction problem occures with Groupings (update)

  Revision 1.7  2005/02/04 15:58:40  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.6  2005/01/31 16:16:41  heto
  Changing database to PostgreSQL. Problems with counts and selection buttons...

  Revision 1.5  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.4  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.3  2002/10/22 06:08:09  heto
  rebuilt the "back-buttons".
  Dont save the request object, save the URL instead.
  New function.

  Revision 1.2  2002/10/18 11:41:10  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:05  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.9  2001/05/31 07:07:00  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.8  2001/05/22 06:16:54  roca
  Backfuncktionality fixed for all (?) servlets/subpages

  Revision 1.7  2001/05/15 13:36:24  roca
  After merge problems from last checkin..

  Revision 1.6  2001/05/11 06:34:54  frob
  Changed all methods that used to call writeError() to write an error page. These
  methods now calls the general method commitOrRollback() which does the commit or
  rollback operation as well as handle any errors. writeError() was removed.

  Revision 1.5  2001/05/03 07:57:39  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.4  2001/05/02 13:54:33  frob
  Calls to removeOper changed to use the general removeQSParameter.
  The previously called method is removed.

  Revision 1.3  2001/04/24 09:33:54  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:27  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.5  2001/04/19 10:57:49  frob
  Changed some comments.

  Revision 1.1.1.1.2.4  2001/04/18 09:26:44  frob
  Removed the size of the main table used on the webpages.

  Revision 1.1.1.1.2.3  2001/04/11 13:15:03  frob
  createGroupingFile: Changed call to Parse() to pass valid file type definitions.
                      No longer uses delimiters in request object.
                      Layout changes.
  writeImpFilePage: Removed field for delimiter.
                    Changed length of filename field.
                    HTML validated.

  Revision 1.1.1.1.2.2  2001/03/28 13:47:54  frob
  Added catch() for InputDataFileException which can be
  raised from the parse()-method.
  Added check that the connection object is created before doing
  rollback on it (to avoid null pointer exception).

  Revision 1.1.1.1.2.1  2001/03/28 12:37:46  frob
  Changed the call to the FileParser constructor.
  Indeted the file and added the log header.

*/

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.oreilly.servlet.MultipartRequest;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;
import se.arexis.agdb.util.FileImport.*;

public class viewGrouping extends SecureArexisServlet
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
      } else if (extPath.equals("/new")) {
         writeNew(req, res);
      } else if (extPath.equals("/copy")) {
         writeCopy(req, res);
      } else if (extPath.equals("/impFile")) {
         writeImpFile(req, res);
      } else if (extPath.equals("/impMultipart")) {
         createGroupingFile(req, res);
      } else if (extPath.equals("/edit")) {
         writeEdit(req, res);
      } else if (extPath.equals("/details")) {
         writeDetails(req, res);
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
         //req= getServletState(req,session);

         String topQS = buildQS(req);
         String bottomQS = topQS.toString();
         out.println("<html>"
                     + "<HEAD>"

                     + " <TITLE>View Groupings</TITLE>"
                     + "</HEAD>"
                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" //
                     + "<frame name=\"viewgroupingtop\" "
                     + "src=\""+getServletPath("viewGrouping/top?") + topQS + "\""
                     + "scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"
                     + "<frame name=\"viewgroupingmiddle\" "
                     + "src=\""+getServletPath("viewGrouping/middle?") + topQS + "\""
                     + "scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"
                     + "<frame name=\"viewgroupingbottom\""
                     + "src=\""+getServletPath("viewGrouping/bottom?") + bottomQS + "\" "
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
   private String buildQS(HttpServletRequest req) {
      StringBuffer output = new StringBuffer(512);
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      String pid = (String) session.getValue("PID");
      String action = null;
      String suid = null, old_suid = null, orderby = null;
      // Find suid 
      //old_suid = (String) session.getValue("SUID");

      suid = req.getParameter("suid");
      if (suid == null) 
      {
         suid = old_suid;
      }
      
      /*
       * This code is not used
       * And if old_suid is null (which means that suid becomes null) then
       * the result of the function is not kept. 
      
      if (suid == null)
         findSuid(conn, pid);
       */
      
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
      output.append(setIndecis(suid, old_suid, action, req, session));

      orderby = req.getParameter("ORDERBY");
      if (orderby != null)
         output.append("&ORDERBY=").append(orderby);
      else
         output.append("&ORDERBY=NAME");
      output.append("&suid=").append(suid);
		
      if (req.getParameter("oper") != null) 
         output.append("&oper=").append(req.getParameter("oper"));
      if (req.getParameter("new_grouping_name") != null) 
         output.append("&new_grouping_name=").append(req.getParameter("new_grouping_name"));
							 
      return output.toString().replace('%', '*');
   }
   
   /*
    * This method is commented out. An ignored exception occured that
    * messed up the transaction support. The function was not in use.
    *
   private String findSuid(Connection conn, String pid) 
   {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try 
      {    
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 WHERE PID=" + 
                                  pid + " ORDER BY NAME");
         if (rset.next()) 
         {
            ret = rset.getString("SUID");
         } 
         else 
         {
            ret = "-1";
         }
      } 
      catch (SQLException e) 
      {
          e.printStackTrace();
         ret = "-1";
      } 
      finally 
      {
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
      if (req.getParameter("STARTINDEX") != null && old_suid != null && old_suid.equalsIgnoreCase(suid)) 
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
                      + "FROM gdbadm.V_GROUPINGS_2 WHERE SUID=" + suid + " ");
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
      StringBuffer filter = new StringBuffer(256);
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
      if (oper.equals("NEW_GROUPING")) {
         if (!createGrouping(req, res))
            ; //return;
      }
*/
      HttpSession session = req.getSession(true);
      Connection conn = null;
      Statement stmt_su = null;
      ResultSet rset_su = null;
      int startIndex = 0, rows = 0, maxRows = 0;
      String suid, gsid, name, orderby, oldQS, newQS, action, pid;
      int currentPrivs[];

      try {
         conn = (Connection) session.getValue("conn");
         stmt_su = conn.createStatement();
         currentPrivs = (int [])session.getValue("PRIVILEGES");
         pid = (String) session.getValue("PID");
         suid = req.getParameter("suid");
         maxRows = getMaxRows(session);
         action = req.getParameter("ACTION");
         oldQS = removeQSParameter(req.getQueryString(),"oper");
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
         if (suid == null) suid = new String("-1");
         if (orderby == null) orderby = new String("NAME");
         if (action == null) action = new String("NOP");
         if (pid == null || "".equalsIgnoreCase(pid))
            pid = new String("-1");
         // new
         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+ getURL("style/view.css") +"\">");
         out.println("<base target=\"content\">");

         writeTopScript(out);
         out.println("<title>View Grouping</title>");
         out.println("</head>");



         out.println("<body bgcolor=\"#ffffd0\">"
                     +"<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
                     +"<tr>"
                     + "<td width=\"14\" rowspan=\"3\">" +"</td>"
                     +"<td width=\"736\" colspan=\"2\" height=\"15\">"
                     +"<form method=get action=\"" +getServletPath("viewGrouping") +"\">"
                     +"<p align=\"center\"><font size=\"2\"><b  style=\"font-size: 15pt\">Groupings - View & Edit</b>"
                     +"</font></td></tr>"
                     +"<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>"
                     +"</tr><tr><td width=\"517\">");

         out.println("<table width=488 height=\"92\">"
                     +"<td><b>Sampling Units</b><br><select name=suid "
                     +"name=select onChange='document.forms[0].submit()'  style=\"HEIGHT: 22px; WIDTH: 126px\">");


         // Get all the sampling units for this project
         rset_su = stmt_su.executeQuery("SELECT NAME, SUID FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                        " WHERE PID=" + pid + " order by NAME");
         String s_suid = null; // Selected sampling unit
         boolean first_round = true;
         while (rset_su.next()) {
            if (first_round) {
               s_suid = new String(rset_su.getString("SUID"));
               first_round = false;
            }
            if (suid != null && suid.equalsIgnoreCase(rset_su.getString("SUID"))) {
               s_suid = new String(rset_su.getString("SUID"));
               out.println("<OPTION selected value=\"" + rset_su.getString("SUID") + "\">" + rset_su.getString("NAME"));
            }
            else
               out.println("<OPTION value=\"" + rset_su.getString("SUID") + "\">" + rset_su.getString("NAME"));
         }
         // In case there isn't a suid stored in session-object
         out.println("</SELECT>");
         out.println("</td></table></td>");


         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");
         out.println("<tr><td width=68 colspan=2>");


       System.err.println("top/oldQS="+oldQS);
       System.err.println("top/newQS="+newQS);

         out.println(privDependentString(currentPrivs,GRP_W,
                                         /*if true*/"<input type=button value=\"New\""
                                         + " onClick='parent.location.href=\"" +getServletPath("viewGrouping/new?")  + oldQS+ "\"' "
                                       //  + "onClick='newGroup()'"
                                         +" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">"
                                         +"</td>",
                                         /*if false*/"<input type=button disabled value=\"New\""
                                         +"height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\" name=\"button\">"
                                         +"</td>"));

         out.println("<td width=68 colspan=2>");

         out.println(privDependentString(currentPrivs,GRP_W,
                                         /*if true*/"<input type=button value=\"Copy\""
                                         + " onClick='parent.location.href=\"" +getServletPath("viewGrouping/copy?") + oldQS + "\"' "
                                         +" width=\"70\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">"
                                         +"</td></tr>",
                                         /*if false*/"<input type=button disabled value=\"Copy\""
                                         +"height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\" name=\"button\">"
                                         +"</td>"));

         out.println("<tr><td width=68 colspan=2>"
                     +"<input id=COUNT name=COUNT type=submit value=\"Count\" width=\"69\""
                     +" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">"
                     +"</td>"
                     +"<td width=68 colspan=2>"
                     +"<input id=DISPLAY name=DISPLAY type=submit value=\"Display\""
                     +" width=\"70\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">"
                     +"</td></tr>");

         out.println("<input type=\"hidden\" id=\"STARTINDEX\" name=\"STARTINDEX\" value=\"" + startIndex + "\">");
         out.println("<input type=\"hidden\" id=\"ORDERBY\" name=\"ORDERBY\" value=\"" + orderby + "\">");
         out.println("<input type=\"hidden\" id=\"oper\" name=\"oper\" value=\"\">");
         out.println("<input type=\"hidden\" id=\"new_grouping_name\" name=\"new_grouping_name\" value=\"\">");

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
            if (rset_su != null) rset_su.close();
            if (stmt_su != null) stmt_su.close();
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
      out.println("var MAX_GROUPING_LENGTH = 20;");		
      out.println("var MIN_GROUPING_LENGTH = 1;");
      out.println("function newGrouping() {");
      out.println("	var grouping_name = prompt('Grouping name', '');");
      out.println("	if (grouping_name == null || '' == grouping_name)");
      out.println("		return (false);");
      out.println("	else if (grouping_name.length > MAX_GROUPING_LENGTH) {");		
      out.println("		alert('The name must be in the range 1-20 characters.');");
      out.println("		return (false);");
      out.println("	}");
      out.println("	if (!confirm('Are you sure you want to create a\\n'");
      out.println("		+ 'new grouping with the name \\'' + grouping_name + '\\'')) {");
      out.println("		return (false);");
      out.println("	}");
      out.println("");
      out.println("");
      out.println("	document.forms[0].oper.value='NEW_GROUPING';");
      out.println("	document.forms[0].new_grouping_name.value=grouping_name;");
      out.println("	document.forms[0].submit();");
      out.println("}");
      out.println("//-->");
      out.println("</script>");

   }
   
   
   /**
    * Creates a new grouping
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if grouping was created.
    *         False if grouping was not created.
    */
   private boolean createGrouping(HttpServletRequest request,
                                  HttpServletResponse response)
   {
      HttpSession session = request.getSession(true);
      Connection connection = (Connection) session.getValue("conn");
      String name = null, suid = null, comm = null;
      boolean isOk = true;
      String errMessage = null;

      try
      {
         connection.setAutoCommit(false);
         name = request.getParameter("new_grouping_name");
         suid = request.getParameter("suid");
         comm = request.getParameter("COMM");
         if (name == null)
         {
            // Well, nothing much to do really.
            return true;
         }
         DbIndividual dbInd = new DbIndividual();
         dbInd.CreateGrouping(connection,
                              Integer.parseInt((String) session.getValue("UserID")),
                              name,
                              comm, // Comment
                              Integer.parseInt(suid));


         errMessage = dbInd.getErrorMessage();
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

      commitOrRollback(connection, request, response, "Groupings.New.Create",
                       errMessage, "viewGrouping/new", isOk);
      return isOk;
   }

   
   /**
    * Imports individuals from a file.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if groupings was imported correctly.
    * @exception IOException If frame or error page could not be written.
    * @exception ServletException If frame or error page could not be
    *            written. 
    */
   private boolean createGroupingFile(HttpServletRequest request,
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

         int samplingUnitId = Integer.parseInt(multiRequest.getParameter("suid"));
         int userId = Integer.parseInt((String) session.getValue("UserID"));

         DbIndividual dbIndividual = new DbIndividual();

         Enumeration fileEnum = multiRequest.getFileNames();
         if (fileEnum.hasMoreElements())
         {
            String givenFileName = (String) fileEnum.nextElement();
            String systemFileName = multiRequest.getFilesystemName(givenFileName);
            FileParser fileParser = new FileParser(upPath + "/" +  systemFileName); 
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.GROUPING,
                                                                        FileTypeDefinition.LIST));
            dbIndividual.CreateGroupings(fileParser, connection,
                                         samplingUnitId, userId);
            
            errMessage = dbIndividual.getErrorMessage();
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

      if (commitOrRollback(connection, request, response,
                           "Groupings.FileImport.Send", errMessage,
                           "viewGrouping/impFile", isOk)
          && isOk)
      {
         writeFrame(request, response);
      }
      return isOk;
   }


   private boolean deleteGrouping(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Connection connection)
      throws ServletException, IOException
   {
      String errMessage = null;
      boolean isOk = true;
      int gsid;
      try
      {
         gsid = Integer.parseInt(request.getParameter("gsid"));
         DbIndividual dbi = new DbIndividual();
         dbi.DeleteGrouping(connection, gsid);
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

      // if commit/rollback was ok and if databas operation was ok, write
      // the original frame.
      if (commitOrRollback(connection, request, response,
                           "Groupings.Edit.Delete", errMessage,
                           "viewGrouping", isOk)
          && isOk)
      {
         writeFrame(request, response);
      }
      return isOk;
   }


   /**
    * Updates a grouping
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if grouping was updated.
    *         False if grouping was not updated.
    * @exception ServletException if an error occurs
    * @exception IOException if an error occurs
    */
   private boolean updateGrouping(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Connection connection)
      throws ServletException, IOException
   {
      HttpSession session = request.getSession(true);
      String errMessage = null;
      boolean isOk = true;
      String name, comment, UserID;
      int gsid;
      try
      {
         connection.setAutoCommit(false);
         name = request.getParameter("n_name");
         comment = request.getParameter("n_comm");
         gsid = Integer.parseInt(request.getParameter("gsid"));
         UserID = (String) session.getValue("UserID");
         DbIndividual dbi = new DbIndividual();
         dbi.UpdateGrouping(connection, gsid, name, comment, Integer.parseInt(UserID));
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
                       "Groupings.Edit.Update", errMessage, "viewGrouping",
                       isOk);
 
      return isOk;
   }


   /**
    * Copies a grouping, ie creates a new grouping from an existing one. 
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if the grouping was copied.
    *         False if grouping was not created.
    */
   private boolean copyGrouping(HttpServletRequest request,
                                HttpServletResponse response)
   {
      HttpSession session = request.getSession(true);
      Connection connection = (Connection) session.getValue("conn");
      String from_gsid = null, g_name = null, suid = null, comm = null;
      String errMessage = null;
      boolean isOk = true;

      try
      {
         connection.setAutoCommit(false);
         g_name = request.getParameter("to_gname");
         from_gsid = request.getParameter("from_gsid");
         comm = request.getParameter("COMM");
         suid = request.getParameter("suid");

         DbIndividual dbInd = new DbIndividual();
         dbInd.CopyGrouping(connection,
                            Integer.parseInt((String) session.getValue("UserID")),
                            Integer.parseInt(suid),
                            Integer.parseInt(from_gsid),
                            g_name,
                            comm);
         errMessage = dbInd.getErrorMessage();
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
                       "Groupings.Copy.Copy", errMessage,
                       "viewGrouping/copy", isOk);
      return isOk;
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
         out.println("</head><body>");
         if(action != null)
         {
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
         newQS = buildTopQS(oldQS);
         /*
           out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
           + "</tr>"
           + "<tr>"
           + "<td width=\"750\" colspan=\"3\">"
           + "<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 height=20 width=840 style=\"margin-left:2px\">"
           + "<td width=5></td>");

         */
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=845 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");

         // the menu choices

         //Name
         out.println("<td width=100><a href=\"" + getServletPath("viewGrouping")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\">");
         if(choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown><b>Name</b></FONT></a></td>\n");
         else out.println("Name</a></td>\n");
         //Comment
         out.println("<td width=200><a href=\"" + getServletPath("viewGrouping")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\">");
         if(choosen.equals("COMM"))
            out.println("<FONT color=saddlebrown><b>Comment</b></FONT></a></td>\n");
         else out.println("Comment</a></td>\n");
         //Idividuals
         out.println("<td width=100><a href=\"" + getServletPath("viewGrouping")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=GRPS\">");
         if(choosen.equals("GRPS"))
            out.println("<FONT color=saddlebrown><b>Groups</b></FONT></a></td>\n");
         else out.println("Groups</a></td>\n");

         //USER
         out.println("<td width=100><a href=\"" + getServletPath("viewGrouping")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
         else out.println("User</a></td>\n");

         //Updated
         out.println("<td width=140><a href=\"" + getServletPath("viewGrouping")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TS\">");
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
      PrintWriter out = res.getWriter();
      Statement stmt = null;
      ResultSet rset = null;
      Connection conn = null;
      int currentPrivs[];
      try
      {
         currentPrivs = (int [])session.getValue("PRIVILEGES");
         String suid, action = null, orderby = null, pid=null;
         String oldQS = removeQSParameterOper(req.getQueryString());
         action = req.getParameter("ACTION");
         suid = req.getParameter("suid");
         pid = (String)session.getValue("PID");
         if (suid == null) suid = "-1";
         if (action == null || action.equalsIgnoreCase("NOP") ||
             action.equalsIgnoreCase("COUNT") || suid == null)
         {
            // Nothing to do!
            HTMLWriter.writeBottomDefault(out);
            return;
         }
         else if (action.equalsIgnoreCase("NEXT"))
         {
            // Skip the first 50 rows?!
         } else if (action.equalsIgnoreCase("PREV"))
         {
            // The opposit
         }


         out.println("<html>\n"
                     + "<head><link rel=\"stylesheet\" type=\"text/css\" href=\""
                     +getURL("style/bottom.css")+"\">\n"
                     + "<title>bottom</title>\n"
                     + "</head>\n"
                     + "<body>\n");

         conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();
         StringBuffer sbSQL = new StringBuffer(512);
         sbSQL.append("SELECT GSID, NAME, COMM, USR, " +
                      "to_char(TS, '" + getDateFormat(session) + "') as TC_TS, " +
                      "GRPS FROM gdbadm.V_GROUPINGS_2 WHERE SUID=" + suid + " AND PID="+pid+" ");
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
         out.println("cellSpacing=0 width=840 style=\"margin-left:2px\">");

         boolean odd = true;
         int rowCount = 0;
         int startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         if (startIndex > 1) {
            while ((rowCount++ < startIndex - 1) && rset.next() )
               ;
         }
         rowCount = 0;
         int maxRows = getMaxRows(session);
         while (rset.next() && rowCount < maxRows) 
         {
            out.println("<TR align=left ");
            if (odd) {
               out.println("bgcolor=white>");
               odd = false;
            } else {
               out.println("bgcolor=lightgrey>");
               odd = true;
            }
            out.println("<TD WIDTH=5></TD><TD WIDTH=100>" + rset.getString("NAME")+ "</TD>");
            out.println("<TD WIDTH=200>" + formatOutput(session,rset.getString("COMM"),30) +"</TD>");
            out.println("<TD WIDTH=100>" + formatOutput(session,rset.getString("GRPS"),6) + "</TD>");
            out.println("<TD WIDTH=100>" + formatOutput(session,rset.getString("USR"),8) + "</TD>");
            out.println("<TD WIDTH=140>" + rset.getString("TC_TS") + "</TD>");
            out.println("<TD WIDTH=100><A HREF=\""+getServletPath("viewGrouping/details?gsid=") + rset.getString("GSID") + "&" + oldQS + "\" target=\"content\">Details</A></TD>");
            out.println("<TD WIDTH=100>");

            out.println(privDependentString(currentPrivs,GRP_W,
                                            /*if true*/"<A HREF=\""+getServletPath("viewGrouping/edit?gsid=") + rset.getString("GSID") + "&" + oldQS + "\" target=\"content\">Edit</A></TD></TR>",
                                            /*if false*/"<font color=tan>&nbsp Edit</font></TD>") );
            rowCount++;
         }
         out.println("</TABLE>");
         out.println("</body></html>");

      } 
      catch (Exception e)
      {
         e.printStackTrace();
         out.println("<strong>Error in filter!</strong><br>");
         out.println("Error message: " + e.getMessage());
         out.println("<br>Modify filter according to message!</body></html>");
      } 
      finally 
      {
         try 
         {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } 
         catch (Exception ignored) 
         {}
      }
   }


   private void printScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('CREATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to create the grouping?')) {");
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

   private void writeImpFile(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      conn = (Connection) session.getValue("conn");
      writeImpFilePage(req, res);
   }


   /**
    * Writes the page used to import groupings.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    */
   private void writeImpFilePage(HttpServletRequest request,
                                 HttpServletResponse response)
   {
      HttpSession session = request.getSession(true);
      Connection connection = (Connection) session.getValue("conn");
      Statement sqlStatement = null;
      ResultSet resultSet = null;
   
      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");
      
      try
      {
         connection = (Connection) session.getValue("conn");
         String projectId = (String) session.getValue("PID");
   
         PrintWriter out = response.getWriter();
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
         out.println("	");
         out.println("	if (rc) {");
         out.println("		if (confirm('Are you sure that you want to create the grouping?')) {");
         out.println("			document.forms[0].oper.value = 'UPLOAD';");
         out.println("			document.forms[0].submit();");
         out.println("		}");
         out.println("	}");
         out.println("	");
         out.println("	");
         out.println("}");
         out.println("//-->");
         out.println("</script>");
         out.println("<title>Import grouping</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Groupings - File import</b></center>" +
                     "</td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post enctype=\"multipart/form-data\" action=\"" +
                     getServletPath("viewGrouping/impMultipart") + "\">");
         out.println("<table border=0>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         sqlStatement = connection.createStatement();
         resultSet = sqlStatement.executeQuery("SELECT SUID, NAME FROM V_ENABLED_SAMPLING_UNITS_2 WHERE " +
                                  "PID=" + projectId + " ORDER BY NAME");
         out.println("<td>Sampling units<br>");
         out.println("<select name=suid style=\"WIDTH: 200px\">");
         while (resultSet.next() ) {
            out.println("<option value=\"" + resultSet.getString("SUID") + "\">" +
                        resultSet.getString("NAME") + "</option>");
         }
         out.println("</select>");
         resultSet.close();
         sqlStatement.close();
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
         String gsid = req.getParameter("gsid");
         if (gsid == null || gsid.trim().equals("")) gsid = "-1";

         conn = (Connection) session.getValue("conn");
         // Get the current data
         stmt_curr = conn.createStatement();
         String strSQL = "SELECT SUNAME, NAME, COMM, USR, "
            + "to_char(TS, '"+ getDateFormat(session) + "') as TC_TS "
            + "FROM gdbadm.V_GROUPINGS_2 WHERE "
            + "GSID=" + gsid ;
         rset_curr = stmt_curr.executeQuery(strSQL);

         // Get the history
         stmt_hist = conn.createStatement();
         strSQL = "SELECT NAME, COMM, USR, " +
            "to_char(TS , '" + getDateFormat(session) + "') as TC_TS, TS as dummy " +
            "FROM gdbadm.V_GROUPINGS_LOG " +
            "WHERE GSID=" + gsid + " " +
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
                     "<b style=\"font-size: 15pt\">Groupings - Details</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td></td></tr>");
         out.println("<tr><td></td><td>");

         out.println("<table nowrap border=0 cellSpacing=0>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
         out.println("<tr><td>Sampling unit</td><td>" + rset_curr.getString("SUNAME") + "</td></tr>");
         out.println("</table><br><br>");

         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");

         out.println("<table nowrap align=center border=0 cellSpacing=0 width=600px>");
         out.println("<tr bgcolor=black><td align=center colspan=9><b><font color=\"#ffffff\">Current Data</font></b></td></tr>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td nowrap WIDTH=100>Name</td>");
         out.println("<td nowrap WIDTH=300>Comment</td>");
         out.println("<td nowrap WIDTH=50>User</td>");
         out.println("<td nowrap WIDTH=150>Last updated</td></tr>");

         out.println("<tr bgcolor=white>");
         // Name
         out.println("<td>");
         if (("" + curr_name).equals("" + prev_name))
            out.println(formatOutput(session, curr_name, 12));
         else
            out.println("<font color=red>" + formatOutput(session, curr_name, 12) + "</font>");
         out.println("</td>");
         // Comment
         out.println("<td>");
         if (("" + curr_comm).equals("" + prev_comm))
            out.println(formatOutput(session, curr_comm, 30));
         else
            out.println("<font color=red>" + formatOutput(session, curr_comm, 30) + "</font>");
         out.println("</td>");
         // User
         out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
         out.println("<td>" + formatOutput(session, curr_ts, 16) + "</td>"); // Last updated
         out.println("</tr>");
         out.println("<tr bgcolor=Black>");
         out.println("<td align=center colspan=9><b><font color=\"#ffffff\">History</font></b></td></tr>");

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
               out.println(formatOutput(session, curr_name, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_name, 12) + "</font>");
            out.println("</td>");
            // Comment
            out.println("<td>");
            if (("" + curr_comm).equals("" + prev_comm))
               out.println(formatOutput(session, curr_comm, 30));
            else
               out.println("<font color=red>" + formatOutput(session, curr_comm, 30) + "</font>");
            out.println("</td>");
            // User
            out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
            out.println("<td>" + formatOutput(session, curr_ts, 16) + "</td>"); // Last updated
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
               out.println(formatOutput(session, curr_name, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_name, 12) + "</font>");
            out.println("</td>");
            // Comment
            out.println("<td>");
            if (("" + curr_comm).equals("" + prev_comm))
               out.println(formatOutput(session, curr_comm, 30));
            else
               out.println("<font color=red>" + formatOutput(session, curr_comm, 30) + "</font>");
            out.println("</td>");
            // User
            out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
            out.println("<td>" + formatOutput(session, curr_ts, 16) + "</td>"); // Last updated
            out.println("</tr>");
         }

         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         // Back' button
         out.println("<form>");
         out.println("<table cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                   //  getServletPath("viewGrouping?") + oldQS + "\"'>&nbsp;");
                     getServletPath("viewGrouping?&RETURNING=YES") + "\"'>&nbsp;");
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

   /***************************************************************************************
    * *************************************************************************************
    * The new Grouping Page
    */
   private void writeNew(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createGrouping(req, res))
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
         String suname, suid,pid;
         pid= (String) session.getValue("PID");

         //suid = (String) session.getValue("SUID");

      suid=req.getParameter("suid");
     //  System.err.println ("suid="+suid);

         String oldQS = buildQS(req);
         //String inParameters=req.getQueryString();
         //System.err.println ("new/OLDQS="+oldQS);
         //System.err.println ("new/REQ="+req.getQueryString());

         out.println("<html>\n"
                     + "<head>\n"
                     + "<title>New Grouping</title>\n"
                     + "</head>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<body>\n");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Groupings - New</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         out.println("<FORM method=\"post\" action=\"" + getServletPath("viewGrouping/new?")+/*oldQS +*/ "\">");

         out.println("<table width=800 border=0>");//align=center border=0>");

         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID="+pid+" ORDER BY NAME");

         out.println("<tr><td width=200 >Sampling Units</td></tr>" +
                     "<tr><td width=200><select style=\"WIDTH: 200px\" name=\"suid\">");

         while (rset.next()) {
            // no su choosen yet
            if(suid==null || "".equalsIgnoreCase(suid)||suid.equalsIgnoreCase("null")) {
               out.println("<option selected value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME"));
               suid = rset.getString("SUID");
            } else { // suid has a value
               if(suid.equalsIgnoreCase(rset.getString("SUID")))
               {
                // Found the allready choosen su. "Mark" it as selected
               out.println("<option selected value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME"));
               }
               else
               {
                out.println("<option value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME"));
                }
            }
         }
         out.println("</select></td></tr>");
         out.println("<tr><td width=200 >Name</td></tr>" +
                     "<tr><td width=200><input type=text maxlength=20 name=\"new_grouping_name\" style=\"WIDTH: 200px\" value=\"\"></td></tr>");

         out.println("<tr><td width=200 >Comment</td></tr>" +	"<td colspan=3 width=600>"
                     +"<textarea rows=8 cols=45 name=COMM></textarea>"
                     +"</td></tr>");
         out.println("<tr><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td></tr>" +
                     "<tr><td colspan=4 >" +
                     "<input type=button value=Back onClick='location.href=\"" +
                     getServletPath("viewGrouping?") + oldQS + "\";' " +
                   //   getServletPath("viewGrouping?suid=") + suid + "&action=DISPLAY" +"\";' " +
                    getServletPath("viewGrouping?&RETURNING=YES")  + "\";' " +

                     "width=100 style=\"WIDTH: 100px\">&nbsp;"+
                     "<input type=button id=SAVE name=SAVE value=Create   style=\"WIDTH: 100px\""+
                     "onClick='valForm(\"CREATE\")'>&nbsp;");

         out.println("</td></tr>");
         out.println("</table>");


         out.println("<input type=\"hidden\" ID=\"oper\" NAME=\"oper\" value=\"\">");
         out.println("<input type=\"hidden\"  NAME=RETURNING value=YES>");

         /*
         out.println("<input type=\"hidden\" ID=ACTION NAME=ACTION value=\""+req.getParameter("ACTION")+"\">");
         out.println("<input type=\"hidden\" ID=STARTINDEX NAME=STARTINDEX value=\""+req.getParameter("STARTINDEX")+"\">");
         out.println("<input type=\"hidden\" ID=ROWS NAME=ROWS value=\""+req.getParameter("ROWS")+"\">");
         out.println("<input type=\"hidden\" ID=ORDERBY NAME=ORDERBY value=\""+req.getParameter("ORDERBY")+"\">");

*/
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
   private void writeEdit(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";
      if (oper.equals("DELETE")) {
         if (deleteGrouping(req, res, conn)) {
            writeFrame(req, res);
         }
      } else if (oper.equals("UPDATE")) {
         if(updateGrouping(req, res, conn)) {
            writeEditPage(req, res);
         }
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
      String suid, suname, gsid, name, comm, usr, tc_ts;
      try {
         stmt = conn.createStatement();
         String oldQS = buildQS(req);
         String pid = (String) session.getValue("PID");
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameter(oldQS,"oper");
         suid = req.getParameter("suid");
         gsid = req.getParameter("gsid");

         String sql = "SELECT SUID, SUNAME, NAME, COMM, USR, " +
            "to_char(TS, '" + getDateFormat(session) + "') as TC_TS " +
            "FROM gdbadm.V_GROUPINGS_2 WHERE " +
            "GSID=" + gsid ;
         rset = stmt.executeQuery(sql);

         rset.next();
         suname = rset.getString("SUNAME");
         name = rset.getString("NAME");
         comm = rset.getString("COMM");
         usr = rset.getString("USR");
         tc_ts = rset.getString("TC_TS");
         rset.close();
         stmt.close();

         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);

         out.println("<title>Edit grouping</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Groupings - Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("viewGrouping/edit?") +
                     oldQS + "&gsid=" + gsid + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr>");
         out.println("<td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td>");
         out.println("</tr>");
         out.println("<tr><td>Sampling unit</td><td>" + suname + "</td></tr>");
         out.println("<tr><td>Last updated by</td><td>" + usr + "</td></tr>");
         out.println("<tr><td>Last updated</td><td>" + tc_ts + "</td></tr>");
         out.println("</table>");
         out.println("</td></tr><tr><td></td><td>");

         // Changable data
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Changable data</font></td></tr>");
         out.println("<tr><td>Name<br>");
         out.println("<input type=text name=n_name width=200 style=\"WIDTH: 200px\" " +
                     "value=\"" + replaceNull(name, "") + "\" maxlength=20>");
         out.println("</td></tr>");
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
                    // getServletPath("viewGrouping?") + "suid=" + suid + "&item=no\"'>&nbsp;");
       // getServletPath("viewGrouping?") + oldQS + "\"'>&nbsp;");
           getServletPath("viewGrouping?&RETURNING=YES") + "\"'>&nbsp;");

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
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
   }
   private void writeEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to delete the grouping?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the grouping?')) {");
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

   private void writeCopy(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";
      if (oper.equals("COPY")) {
         if (copyGrouping(req, res)) {
            writeCopyPage(req, res);
         }
      } else
         writeCopyPage(req, res);
   }
   private void writeCopyPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);

      PrintWriter out = res.getWriter();
      // set content type and other response header fields first
      res.setContentType("text/html");
      Connection conn =  null;
      Statement stmt = null;
      ResultSet rset = null;
      printCpScript(out);
      //writeNewScript(out);
      try {
         conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();
         String suname, suid = null ,pid; // gsid=null;
         pid= (String) session.getValue("PID");
         suid = req.getParameter("suid");
         String from_gsid = req.getParameter("from_gsid");
         String to_gname = req.getParameter("to_gname");

         if(to_gname == null || to_gname.equalsIgnoreCase("null"))  {
            to_gname ="";
         }

         String oldQS = buildQS(req);
        //String oldQS = req.getQueryString();
     //   System.err.println("copy/OLDQS="+ oldQS);
     //   System.err.println("copy/REQ="+ req.getQueryString());

         out.println("<html>\n"
                     + "<head>\n"
                     + "<title>Copy Grouping</title>\n"
                     + "</head>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<body>\n");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Groupings - Copy</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<FORM method=\"get\" action=\"" + getServletPath("viewGrouping/copy?")+ oldQS + "\">");


         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 WHERE PID="+pid+" ORDER BY NAME ");

         out.println("<table><td></td></table>");

         out.println("<table><tr><td width= 12></td><td width=100 align=left>Sampling Unit</td></tr>" +
                     "<tr><td width=12></td><td width=200><select style=\"WIDTH: 200px\" name=\"suid\"onChange='document.forms[0].submit()'>");

         while (rset.next())
         {
            // NO SU COOSEN YET
            if( suid==null || suid.equalsIgnoreCase("") || suid.equalsIgnoreCase("null")) {
               out.println("<OPTION selected value=\"" + rset.getString("SUID") + "\">" +
                           rset.getString("NAME")+ "</option>\n");
               suid=rset.getString("SUID");
            } else {
               if (suid != null && suid.equalsIgnoreCase(rset.getString("SUID"))) {
                  out.println("<OPTION selected value=\"" + rset.getString("SUID") + "\">" +
                              rset.getString("NAME")+ "</option>\n");
               } else {
                  out.println("<OPTION value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME")+"</option>\n");
               }
            }
         }
         rset.close();
         stmt.close();

         out.println("</select></td></tr></table>");

         out.println("<table><td></td></table>");
         out.println("<table><td></td></table>");

         // the "bar"
         out.println( "<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 height=15 width=800 >");
         out.println("<tr><td bgcolor=oldlace width=12></td><td width=338 bgcolor=\"#008B8B\">"
                     +"<b>Copy From</b></td>"
                     +"<td width=100 bgcolor=oldlace></td>"
                     +"<td width=350><b>Copy To</b></td></tr></table>\n");

         out.println( "<table  border=0 cellpadding=0 cellspacing=0 height=15 width=800 >");
         out.println("<tr><td width=5></td></tr>");
         out.println("<tr><td width=5></td></tr>");
         out.println("<tr><td width=5></td></tr>");

         // from grouping
         out.println("<tr><td width=350 align=left>Grouping</td>"
                     + "<td width=100>&nbsp&nbsp</td>"
                     +"<td width=350>Grouping</td></tr>");
         out.println("<tr><td width=350>");
         out.println("<select width=200 style=\"WIDTH: 200px\" " +
                     "name=from_gsid>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT GSID, NAME FROM gdbadm.V_GROUPINGS_1 WHERE SUID="+suid+" AND SUID="+suid+" ORDER BY NAME");

         while (rset.next()) {
            // no gsid choosen yet
            if(from_gsid==null ||"".equalsIgnoreCase(from_gsid) || from_gsid.equalsIgnoreCase("null")) {
               out.println("<option selected value=\"" + rset.getString("GSID") + "\">" + rset.getString("NAME"));
               from_gsid = rset.getString("GSID");
            } else {
               // gsid choosen previously
               if(from_gsid.equals(rset.getString("GSID"))) {
                  out.println("<option selected value=\"" + rset.getString("GSID") + "\">" + rset.getString("NAME"));
                  from_gsid = rset.getString("GSID");
               } else {
                  out.println("<option value=\"" + rset.getString("GSID") + "\">" + rset.getString("NAME"));
               }
            }
         }
         rset.close();
         stmt.close();
         out.println("</select></td>");
         // to gouping
         out.println("<td width=100>&nbsp&nbsp</td><td width=350>" +
                     "<input type=text maxlength=20 name=to_gname value="+to_gname+"></td>");
         out.println("</tr>");
         out.println("<input type=\"hidden\" ID=oper NAME=oper value=\"\">");

         out.println("<input type=\"hidden\" ID=ACTION NAME=ACTION value=\""+req.getParameter("ACTION")+"\">");
         out.println("<input type=\"hidden\" ID=STARTINDEX NAME=STARTINDEX value=\""+req.getParameter("STARTINDEX")+"\">");
         out.println("<input type=\"hidden\" ID=ROWS NAME=ROWS value=\""+req.getParameter("ROWS")+"\">");
         out.println("<input type=\"hidden\" ID=ORDERBY NAME=ORDERBY value=\""+req.getParameter("ORDERBY")+"\">");



         out.println("<tr><td width=5></td></tr>");
         out.println("<tr><td width=5></td></tr>");

         out.println("<tr><td>");
       //  out.println("<input type=button id=Cancel name=Cancel value=Back style=\"WIDTH: 100px\""
                 //    +"onClick='location.href=\"\"'>");

       out.println("<input type=button name=Cancel value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("viewGrouping?") + removeQSParameter(oldQS,"oper") + "\"'>&nbsp;");

         out.println("<input type=button name=Copy value=Copy " +
                     "width=100 style=\"WIDTH: 100px\" onClick='valForm(\"COPY\")'>&nbsp;");

         out.println("</td></tr>");
         out.println("</table>");
         out.println("</FORM>");
         out.println("</body>\n</html>");
      } catch (Exception e)	{
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

   private void printCpScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('COPY' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to copy the grouping?')) {");
      out.println("			document.forms[0].oper.value='COPY';");
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
            // We need the privilege GRP_R for all these
            title = "Groupings - View & Edit";
            if ( privDependentString(privileges, GRP_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/edit") ) {
            // We need the privilege GRP_W
            title = "Groupings - Edit";
            if ( privDependentString(privileges, GRP_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/new") ) {
            // We need the privilege GRP_W
            title = "Grouping - New";
            if ( privDependentString(privileges, GRP_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/copy") ) {
            // We need the privilege GRP_W
            title = "Groupings - Copy";
            if ( privDependentString(privileges, GRP_R, "", null) == null)
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
