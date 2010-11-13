/*
  $Log$
  Revision 1.8  2005/03/04 15:36:15  heto
  Converting for using PostgreSQL

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

  Revision 1.3  2002/10/22 06:08:13  heto
  rebuilt the "back-buttons".
  Dont save the request object, save the URL instead.
  New function.

  Revision 1.2  2002/10/18 11:41:11  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.9  2001/05/31 07:07:13  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.8  2001/05/29 14:00:10  frob
  Replace old design with getStatus with new design commitOrRollback.
  UnifiedVariableSet keys added to Errors.properties.

  Revision 1.7  2001/05/22 06:17:06  roca
  Backfuncktionality fixed for all (?) servlets/subpages

  Revision 1.6  2001/05/14 13:41:42  frob
  Removed writeError(), forgot that in last checkin.

  Revision 1.5  2001/05/14 13:40:07  frob
  Changed all methods that used to call writeError() to write an error page. These
  methods now calls the general method commitOrRollback() which does the commit or
  rollback operation as well as handle any errors. writeError() was removed.

  Revision 1.4  2001/05/03 07:57:51  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.3  2001/05/03 06:43:17  frob
  Calls to removeOper changed to use the general removeQSParameter.
  The previously called method is removed.

  Revision 1.2  2001/05/03 06:41:38  frob
  Indented the file and added log header.

*/

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;

public class viewUVarSet extends SecureArexisServlet
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
      } else if (extPath.equals("/membership")) {
         writeMember(req, res);
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
                     + " <TITLE>View UVar Set</TITLE>"
                     + "</HEAD>"
                     + "<frameset rows=\"185,35,*\" framespacing=\"0\" border=\"true\">" //
                     + "<frame name=\"viewtop\" "
                     + "src=\""+ getServletPath("viewUVarSet/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewmiddle\" "
                     + "src=\"" + getServletPath("viewUVarSet/middle?") + topQS + "\" "
                     + "scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"\">"
                     + "</frame>\n"


                     + "<frame name=\"viewbottom\""
                     + "src=\"" +getServletPath("viewUVarSet/bottom?") + bottomQS + "\" "
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
         old_sid = null, 
         name = null, 
         uvar = null, // A name of a variable that must be a member of this variable set
         orderby = null;
      String pid = (String) session.getValue("PID");
      old_sid = (String) session.getValue("SID");
      sid = req.getParameter("sid");
      if (sid != null) {
         session.putValue("SID", new String(sid));
      } else {
         sid = old_sid;
      }
      if (sid == null) {
         sid = findSid(conn, pid);
      }
      session.putValue("SID", sid);
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
      uvar = req.getParameter("UVAR");
      if (uvar != null)
         output.append("&UVAR=").append(uvar);
      // Set the parameters STARTINDEX and ROWS
      if (!action.equals("NOP"))
         output.append(setIndecis(sid, old_sid, action, req, session));
      output.append("&sid=").append(sid);
		
      if (req.getParameter("oper") != null) 
         output.append("&oper=").append(req.getParameter("oper"));
      if (req.getParameter("new_name") != null) 
         output.append("&new_name=").append(req.getParameter("new_name"));

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
      } 
      catch (SQLException e) 
      {
          e.printStackTrace();
         ret = "-1";
      } 
      finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {
         }
      }
      return ret;
   }
   
   private String findUVsid(Connection conn, String pid, String sid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT UVSID FROM gdbadm.V_U_VARIABLE_SETS_3 WHERE " +
                                  "PID=" + pid + " AND SID=" +
                                  sid + " ORDER BY NAME");
         if (rset.next()) {
            ret = rset.getString("UVSID");
         } else {
            ret = "-1";
         }
      } 
      catch (SQLException e) 
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
   
   private String setIndecis(String sid, String old_sid, String action, HttpServletRequest req, HttpSession session) {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(sid, req, session);
      maxRows = getMaxRows(session);//Integer.parseInt( (String) session.getValue("MaxRows"));
      if (req.getParameter("STARTINDEX") != null &&
          old_sid != null && sid != null &&
          old_sid.equalsIgnoreCase(sid) )
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

   private int countRows(String sid, HttpServletRequest req, HttpSession session) 
   {
      Connection conn = (Connection) session.getValue("conn");
      String pid = (String) session.getValue("PID");
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer(512);
      try 
      {
          //SID=" + sid + " " + "AND 
         sbSQL.append("SELECT count(*) "
                      + "FROM gdbadm.V_U_VARIABLE_SETS_3 WHERE SID="+sid+" AND PID=" + pid + " ");
         sbSQL.append(buildFilter(req,false));
         stmt = conn.createStatement();
         rset = stmt.executeQuery(sbSQL.toString());
         rset.next();
         return rset.getInt(1);
      } 
      catch (SQLException e) 
      {
          e.printStackTrace();
         return 0;
      } 
      finally 
      {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         }
         catch (SQLException ignored) {}
      }
   }
   
   private String buildFilter(HttpServletRequest req)
   {
       return buildFilter(req,true);
   }
   
   private String buildFilter(HttpServletRequest req, boolean order) 
   {
      String name = null,
         uvar = null,
         orderby = null;

      StringBuffer filter = new StringBuffer(256);
      name = req.getParameter("NAME");
      uvar = req.getParameter("UVAR");
      orderby = req.getParameter("ORDERBY");

      if (name != null && !"".equalsIgnoreCase(name))
         filter.append("and NAME like '" + name + "'");
      if (uvar != null && !"".equalsIgnoreCase(uvar))
         filter.append(" and UVSID IN "
                       + "(SELECT DISTINCT UVSID FROM gdbadm.R_U_VAR_SET_1 "
                       + "WHERE UVID IN "
                       + "(SELECT UVID FROM gdbadm.VARIABLES WHERE NAME like '" + uvar + "')"
                       + ")");
      
      if (order)
      {
          if (orderby != null && !"".equalsIgnoreCase(orderby))
             filter.append(" order by " + orderby);
          else
             filter.append(" order by name");
      }

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

      if (oper.equals("NEW_GENO")) {
         if (!createGeno(req, res))
            ; //return;
      }
      HttpSession session = req.getSession(true);
      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null;
      int startIndex = 0, rows = 0, maxRows = 0;
      String sid, uvar, name, orderby, oldQS, newQS, action, pid;
      try {
         conn = (Connection) session.getValue("conn");
         int currentPrivs[] = (int [])session.getValue("PRIVILEGES");
         pid = (String) session.getValue("PID");
         sid = req.getParameter("sid");
         maxRows = getMaxRows(session);
         action = req.getParameter("ACTION");
         oldQS = req.getQueryString();
         newQS = buildTopQS(oldQS);
         orderby = req.getParameter("ORDERBY");
         name = req.getParameter("NAME");
         uvar = req.getParameter("UVAR");
         if (req.getParameter("STARTINDEX") != null)
            startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         else
            startIndex = 0;
         if (req.getParameter("ROWS") != null)
            rows = Integer.parseInt(req.getParameter("ROWS"));
         else
            rows = 0;
         if (sid == null) sid = "-1";
         if (name == null) name = "";
         if (uvar == null) uvar = "";
         if (orderby == null) orderby = "NAME";
         if (action == null) action = "NOP";
         if (pid == null || "".equalsIgnoreCase(pid))
            pid = "-1";

         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\"" +getURL("style/view.css")+"\">");
         out.println("<base target=\"content\">");

         writeTopScript(out);
         out.println("<title>View U-variable set</title>");
         out.println("</head>");

         out.println("<body bgcolor=\"#ffffd0\">"
                     +"<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
                     +"<tr>"
                     + "<td width=\"14\" rowspan=\"3\">"
                     +"</td>"
                     +"<td width=\"736\" colspan=\"2\" height=\"15\">"
                     +"<form method=get action=\"" +getServletPath("viewUVarSet") +"\">"
                     +"<p align=\"center\"><font size=\"2\"><b  style=\"font-size: 15pt\">Unified Variable Sets</b>"
                     +"</font></td></tr>"
                     +"<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>"
                     +"</tr><tr><td width=\"517\">");

         out.println("<table width=488 height=\"92\">"
                     +"<td><b>Species</b><br><select name=sid "
                     +"name=select onChange='document.forms[0].submit()'  style=\"HEIGHT: 22px; WIDTH: 126px\">");

         /*	out.println("<body>");
          */
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SID FROM gdbadm.V_SPECIES_2 " +
                                  "WHERE PID=" + pid + " order by NAME");
         while (rset.next()) {
            if (sid != null && sid.equalsIgnoreCase(rset.getString("SID")))
               out.println("<OPTION selected value=\"" + rset.getString("SID") + "\">" +
                           rset.getString("NAME"));
            else
               out.println("<OPTION value=\"" + rset.getString("SID") + "\">" + rset.getString("NAME"));
         }
         rset.close();
         stmt.close();
         out.println("</SELECT>");


         out.println("</td><td><b>Name</b><br>"
                     +"<input id=name name=NAME value=\"" + name + "\" style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\">");

         out.println("<td><b>Member</b><br>"
                     + "<input id=uvar name=UVAR value=\"" + uvar + "\"style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\">"
                     +"</td></table></td>");


         //Buttons
         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");

         out.println(privDependentString(currentPrivs,UVARS_W,
                                         /*if true*/"<input type=button value=\"New U-Variable set\""
                                         + " onClick='parent.location.href=\"" +getServletPath("viewUVarSet/new?") + newQS + "\"' "
                                         +"height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">"
                                         +"</td>",
                                         /*if false*/"<input type=button disabled value=\"New U-Variable set\""
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
         out.println("<input type=\"hidden\" name=\"new_name\" value=\"\">");

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

      } 
      catch (Exception e)
      {
          e.printStackTrace();
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
      out.println("var MAX_GENO_LENGTH = 20;");		
      out.println("var MIN_GENO_LENGTH = 1;");
      out.println("function newGeno() {");
      out.println("  alert('popup new window for creation of genotype!');");
      out.println("//	var geno_name = prompt('Genotype name', '');");
      out.println("//	if (geno_name == null || '' == geno_name)");
      out.println("//		return (false);");
      out.println("//	else if (su_name.length > MAX_SU_LENGTH) {");		
      out.println("//		alert('The name must be in the range 1-20 characters.');");
      out.println("//		return (false);");
      out.println("//	}");
      out.println("//	if (!confirm('Are you sure you want to create a\\n'");
      out.println("//		+ 'new sampling units with the name \\'' + su_name + '\\'')) {");
      out.println("//		return (false);");
      out.println("//	}");
      out.println("");
      out.println("");
      out.println("//	document.forms[0].oper.value='NEW_GENO';");
      out.println("//	document.forms[0].new_geno_name.value=geno_name;");
      out.println("//	document.forms[0].submit();");
      out.println("}");
      out.println("//-->");
      out.println("</script>");	

   }
   private boolean createGeno(HttpServletRequest req, HttpServletResponse res) {	
      /*		HttpSession session = req.getSession(true);
                        Connection conn = (Connection) session.getValue("conn");
                        String name, sid, pid;
                        try {
			conn.setAutoCommit(false);
			name = req.getParameter("new_su_name");
			sid = req.getParameter("sid");
			pid = (String) session.getValue("PID");

			if (name == null) {
				// Well, nothing much to do really.
				return true;
                                }		
		
                                DBSamplingUnit dbSU = new DBSamplingUnit();
                                dbSU.CreateSamplingUnit(conn,
                                pid, 
                                name, 
                                "", // Comment
                                Integer.parseInt(sid), 
                                Integer.parseInt((String) session.getValue("UserID")));
                                if (dbSU.getStatus() != 0) {
				conn.rollback();
				return false;
                                }		
                                } catch (Exception e) {
                                try {
				conn.rollback();
                                } catch (SQLException ignored) {
                                }
                                return false;
                                }
                                try {
                                conn.commit();
                                } catch (SQLException ignored) {
                                }
      */
      return true;
   }

   /*  ***********************************************************
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
         out.println("<base target=\"content\"></head><body>");

         if(action != null)
         {
            out.println("&nbsp;" +buildInfoLine(action, startIndex, rows, maxRows));
            /*
              out.println("<tr>"
              + "<td width=\"750\" colspan=\"3\">"
              + "<p align=left>&nbsp;&nbsp;"
              + buildInfoLine(action, startIndex, rows, maxRows)
              + "</td>"
              + "</tr>");
            */
         }

         String oldQS, newQS;
         oldQS = req.getQueryString();
         String choosen= req.getParameter("ORDERBY");
         //System.err.println("old=" +oldQS);
         newQS = buildTopQS(oldQS);
         // System.err.println("new=" +newQS);
         /*
           out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
           + "</tr>"
           + "<tr>"
           + "<td width=\"750\" colspan=\"3\">"
           //style=\"margin-left:5px
           + "<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 height=20 width=800 style=\"margin-left:2px\">"
           + "<td width=5></td>");
         */
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=800 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");

         // the menu choices
         //Name
         out.println("<td width=100><a href=\"" + getServletPath("viewUVarSet")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\">");
         if(choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown ><b>Name</b></FONT></a></td>\n");
         else out.println("Name</a></td>\n");

         //Comment
         out.println("<td width=300><a href=\"" + getServletPath("viewUVarSet")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\">");
         if(choosen.equals("COMM"))
            out.println("<FONT color=saddlebrown><b>Comment</b></FONT></a></td>\n");
         else out.println("Comment</a></td>\n");

         //USER
         out.println("<td width=100><a href=\"" + getServletPath("viewUVarSet")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
         else out.println("User</a></td>\n");

         //Updated
         out.println("<td width=200><a href=\"" + getServletPath("viewUVarSet")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TS\">");
         if(choosen.equals("TS"))
            out.println("<FONT color=saddlebrown><b>Updated</b></FONT></a></td>\n");
         else out.println("Updated</a></td>\n");
         /*
           out.println("<td width=50>&nbsp;</td>"
           + "<td width=50>&nbsp;</td>"
           + "</table>"
           + "</td>"
           + "</tr>"
           + "</table>"
           + "</body></html>");
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
      PrintWriter out = res.getWriter();
      Statement stmt = null;
      ResultSet rset = null;
      Connection conn = null;
      try
      {
         String sid = null, action = null, pid = null, oldQS = null;
         pid = (String) session.getValue("PID");
         oldQS = req.getQueryString();
         action = req.getParameter("ACTION");
         sid = req.getParameter("sid");
         int currentPrivs[] = (int [])session.getValue("PRIVILEGES");
         if (action == null || action.equalsIgnoreCase("NOP") ||
             action.equalsIgnoreCase("COUNT") ||
             sid == null )
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
         sbSQL.append("SELECT NAME, substr(COMM, 0, 50) as COMM, " +
                      "USR, to_char(TS, '" + getDateFormat(session) + "') as TC_TS, UVSID " +
                      "FROM gdbadm.V_U_VARIABLE_SETS_3 WHERE SID=" + sid + " AND PID=" + pid + " ");

         String qs = req.getQueryString();
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);
         // out.println(sbSQL.toString());
         rset = stmt.executeQuery(sbSQL.toString());
         out.println("<TABLE align=left border=0 cellPadding=1");
         out.println("cellSpacing=0 width=800>");



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
            out.println("<td width=100>" + rset.getString("NAME") + "</TD>");
            out.println("<td width=300>" + formatOutput(session,rset.getString("COMM"),25) + "</TD>");
            out.println("<td width=100>" + rset.getString("USR") + "</TD>");
            out.println("<td width=200>" + rset.getString("TC_TS") + "</TD>");
            out.println("<td width=50><A HREF=\""+getServletPath("viewUVarSet/details?")+"uvsid="
                        + rset.getString("UVSID") + "&"
                        + oldQS + "\" target=\"content\">Details</A></TD>");

            out.println("<td width=50>");
            out.println(privDependentString(currentPrivs,UVARS_W,
                                            /*if true*/"<A HREF=\""+getServletPath("viewUVarSet/edit?")+"uvsid="
                                            + rset.getString("UVSID") + "&"
                                            + oldQS + "\" target=\"content\">Edit</A></TD></TR>",
                                            /*if false*/"<font color=tan>&nbsp Edit</font></TD>") );

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
      PrintWriter out = res.getWriter();
		
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
         String uvsid = req.getParameter("uvsid");
         if (uvsid == null || uvsid.trim().equals("")) uvsid = "-1";

         conn = (Connection) session.getValue("conn");
         // Get the current data
         stmt_curr = conn.createStatement();
         String strSQL = "SELECT SNAME, NAME, COMM, USR, " +
            "to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS " +
            "FROM gdbadm.V_U_VARIABLE_SETS_3 WHERE " +
            "UVSID=" + uvsid;
         rset_curr = stmt_curr.executeQuery(strSQL);

         // Get the history
         stmt_hist = conn.createStatement();
         strSQL = "SELECT NAME, COMM, USR, to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS, TS as dummy " +
            "FROM gdbadm.V_U_VARIABLE_SETS_LOG " +
            "WHERE UVSID=" + uvsid + " " +
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

         // new look
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Individuals - View & Edit- Details</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td></td></tr>");
         out.println("<tr><td></td><td>");

         // static
         out.println("<table nowrap border=0 cellSpacing=0>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
         out.println("<tr><td>Species</td><td>" + rset_curr.getString("SNAME") + "</td></tr>");
         out.println("</table><br><br>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         /*
           out.println("<table width=800px cellPadding=0 cellSpacing=0 align=center border=0 style=\"PADDING-LEFT: 0\">"
           + "<tr><td align=left width=100px><input type=button style=\"WIDTH: 100px\" "
           + "value=\"Back\" onClick='location.href=\""+getServletPath("viewUVarSet?") + oldQS + "\"'>"
           + "<td width=700px align=center><h3>Variable set details</tr>"
           + "<tr><td>&nbsp;</td></tr>"
           + "</table>");
           out.println("<table nowrap border=0 cellSpacing=0>");
           out.println("<tr><td colspan=2 bgcolor=black><font color=\"#ffffff\">Static data</font>");
           out.println("<tr><td>Species<td>" + rset_curr.getString("SNAME"));
           out.println("</table><br><br>");
         */


         out.println("<table nowrap align=center border=0 cellSpacing=0 width=740px>");
         out.println("<tr bgcolor=Black>" +
                     "<td align=center colspan=7><b><font color=\"#ffffff\">Current Data</font></b></td>");

         out.println("<tr bgcolor=\"#008B8B\"><td nowrap WIDTH=50 style=\"WIDTH: 50px\">Name");
         out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">Comment");
         out.println("<td nowrap WIDTH=120 style=\"WIDTH: 50px\">User</td>");
         out.println("<td nowrap WIDTH=120 style=\"WIDTH: 120px\">Last updated</td></tr>");

         out.println("<tr bgcolor=white>");
         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" + 
                     (curr_name.equals(prev_name) ? "" + curr_name : "<font color=red>" + curr_name + "</font>"));
         out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" +
                     (curr_comm.equals(prev_comm) ? "" + curr_comm : "<font color=red>" + curr_comm + "</font>"));

         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" + curr_usr + "</td>");
         out.println("<td nowrap WIDTH=120 style=\"WIDTH: 120px\">" + curr_ts + "</td>"); // Last updated
				
         out.println("<tr bgcolor=Black>");
         out.println("<td align=center colspan=7><b><font color=\"#ffffff\">History</font></b></td></tr>");
			
         curr_name = prev_name;
         curr_comm = prev_comm;
         curr_usr = prev_usr;
         curr_ts = prev_ts;
         if (curr_name == null) curr_name = "";
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
            out.println("<td nowrap WIDTH=100 style=\"WIDTH: 50px\">" +
                        (curr_name.equals(prev_name) ? "" + curr_name : "<font color=red>" + curr_name + "</font>"));
            out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" + 
                        (curr_comm.equals(prev_comm) ? "" + curr_comm : "<font color=red>" + curr_comm + "</font>")); 
            out.println("<td nowrap WIDTH=120 style=\"WIDTH: 50px\">" + curr_usr + "</td>");
            out.println("<td nowrap WIDTH=50 style=\"WIDTH: 120px\">" + curr_ts + "</td>"); // Last updated
            curr_name = prev_name;
            curr_comm = prev_comm;
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
            out.println("<td nowrap WIDTH=100 style=\"WIDTH: 50px\">" +
                        (curr_name.equals(prev_name) ? "" + curr_name : "<font color=red>" + curr_name + "</font>"));
            out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" + 
                        (curr_comm.equals(prev_comm) ? "" + curr_comm : "<font color=red>" + curr_comm + "</font>")); 
            out.println("<td nowrap WIDTH=120 style=\"WIDTH: 50px\">" + curr_usr + "</td>");
            out.println("<td nowrap WIDTH=50 style=\"WIDTH: 120px\">" + curr_ts + "</td>"); // Last updated
         }
         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");

         // buttons
         out.println("<form>");
         out.println("<table cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td>");

         out.println("<input type=button style=\"WIDTH: 100px\" "
                     + "value=\"Back\" onClick='location.href=\""
            //         +getServletPath("viewUVarSet?") + oldQS + "\"'>"+"&nbsp;");
         +getServletPath("viewUVarSet?&RETURNING=YES")  + "\"'>"+"&nbsp;");

         out.println("</td></tr></table>");

         out.println("</form>");

         out.println("</tr></table>");
         out.println("</body></html>");

      } 
      catch (SQLException e)
      {
          e.printStackTrace();
          
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
    * The new page
    */
   private void writeNew(HttpServletRequest req, HttpServletResponse res) 
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
		
      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createUVarSet(req, res, conn))
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
         if (item == null || item.trim().equals("")) item = ""; // make sure that all of suid, cid, mid, aid are updated
         if (pid == null || "".equalsIgnoreCase(pid))
            pid = "-1";
         if (item.equals("no")) {
            ;
         } else if (item.equals("sid")) {
            //				vid = findVid(conn, suid);
         } else {
            sid = findSid(conn, pid);
            //				vid = findVid(conn, suid);
         }
			
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<base target=\"content\">");

         writeNewScript(out);
         out.println("<title>New variable set</title>");
         out.println("</head>");
         out.println("<body>");

         // new look
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Phenotypes - Unified Variable Sets - New</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         out.println("<form method=get action=\""+getServletPath("viewUVarSet/new?") + newQS + "\">");

         //		out.println("<center><H3>New variable set</H3></center>");
         //		out.println("<br><br>");
         out.println("<table>");
         out.println("<tr><td>");
         out.println("Species<br>");
         out.println("<SELECT name=sid WIDTH=150 height=25 " +
                     "style=\"HEIGHT: 25px; WIDTH: 150px\" " +
                     "onChange='selChanged(\"sid\")'>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SID FROM gdbadm.V_SPECIES_2 " +
                                  "WHERE PID=" + pid + " ORDER BY NAME");
         while (rset.next()) {
            if (sid != null && sid.equalsIgnoreCase(rset.getString("SID")))
               out.println("<OPTION selected value=\"" + rset.getString("SID") + "\">" +
                           rset.getString("NAME"));
            else
               out.println("<OPTION value=\"" + rset.getString("SID") + "\">" + rset.getString("NAME"));
         }
         rset.close();
         stmt.close();
         out.println("</SELECT>");
         out.println("<td>Name<br>");
         out.println("<input type=text name=name maxlength=20 " +
                     "width=150 height=25 " +
                     "style=\"WIDTH: 150px; HEIGHT: 25px\">");

         out.println("</tr>");
         out.println("<tr><td COLSPAN=2>Comment<br>");
         out.println("<textarea rows=10 cols=40 name=comm>");
         out.println("</textarea>");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<tr><td COLSPAN=2>");
         out.println("<input type=button value=Cancel onClick='document.location.href=\""+getServletPath("viewUVarSet?") + newQS + "\"'>");
         out.println("&nbsp;");
         out.println("<input type=button value=Create onClick='valForm()'>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=item value=\"\">");
         out.println("<input type=hidden name=oper value=\"\">");
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
      out.println("	");
      out.println("	var rc = 1;");
      //		out.println("	if ( (\"\" + document.forms[0].r1.value) != \"\") {");
      //		out.println("		if (document.forms[0].r1.value.length > 20) {");
      //		out.println("			alert('Raw 1 must be less than 20 characters!');");
      //		out.println("			rc = 0;");
      //		out.println("		}");
      //		out.println("	}");
      //		out.println("	if ( (\"\" + document.forms[0].r2.value) != \"\") {");
      //		out.println("		if (document.forms[0].r1.value.length > 20) {");
      //		out.println("			alert('Raw 2 must be less than 20 characters!');");
      //		out.println("			rc = 0;");
      //		out.println("		}");
      //		out.println("	}");
      //		out.println("	");
      //		out.println("	");
      out.println("	if (rc) {");
      out.println("		if (confirm('Are you sure that you want to create the variable set?')) {");
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
         if (deleteUVarSet(req, res, conn))
         {
            writeFrame(req, res);
         }
      }
      else if (oper.equals("UPDATE"))
      {
         if (updateUVarSet(req, res, conn))
         {
            writeEditPage(req, res);
         }
      }
      else
      {
         writeEditPage(req, res);
      }
   }

   private void writeEditPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      PrintWriter out = res.getWriter();
      Connection conn =  (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      try {
         stmt = conn.createStatement();
         String oldQS = req.getQueryString(); //buildQS(req);
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         String uvsid = req.getParameter("uvsid");
         String sql = "SELECT SNAME, NAME, COMM, USR, " +
            "to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS " +
            "FROM gdbadm.V_U_VARIABLE_SETS_3 WHERE " +
            "UVSID = " + uvsid;
         rset = stmt.executeQuery(sql);

         rset.next();
         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);
         out.println("<title>Edit U-variable set</title>");
         out.println("</head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<body>\n");

         // new look
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Phenotypes - Variable sets - View & Edit - Edit</b></font></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         // just a "newline"
         out.println("<table><td></td></table>");
         // the whole information table
         out.println("<table width=800>");
         out.println("<tr><td>");
         /*
           out.println("<table width=800px cellPadding=0 cellSpacing=0 align=center border=0 style=\"PADDING-LEFT: 0\">"
           + "<tr><td align=left width=100px><input type=button style=\"WIDTH: 100px\" "
           + "value=\"Back\" onClick='location.href=\""+getServletPath("viewUVarSet?") + oldQS + "\"'>"
           + "<td width=700px align=center><h3>Edit Unified variable set</h3></td></tr>"
           + "<tr><td>&nbsp;</td></tr>"
           + "</table>");
         */

         // static data table
         out.println("<table border=0 cellpading=0 cellspacing=0 align=left width=300");
         out.println("<tr>");
         out.println("<td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td>");
         out.println("</tr>");
         out.println("<tr><td>Species</td><td>" + rset.getString("SNAME") + "</td></tr>");
         out.println("<tr><td>Last updated by</td><td>" + rset.getString("USR") + "</td></tr>");
         out.println("<tr><td>Last updated</td><td>" + rset.getString("TC_TS") + "</td></tr>");
         out.println("</table></tr></td>");

			
         // The changable data
         String name = rset.getString("NAME");
         String comm = rset.getString("COMM");
         rset.close();
         stmt.close();
         // oldQS contains vsid!
         // Belowe we use rather cryptic names for the form data. We do this to prevent that
         // the data in the form won't collide with the data in the old query string
         out.println("<FORM action=\"edit?" + oldQS + "\" method=\"post\" name=\"FORM1\">");


         // dynamic data table
         out.println("<tr><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0 width=300>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Changable data</font></td></tr>");


         out.println("<tr><td width=200 align=right>Name</td></tr>");
         out.println("<tr><td width=200><input type=text name=n value=\"" + name + "\"></td></tr>");
         //			out.println("<td width=200 align=right>&nbsp;");
         //			out.println("<td width=200>&nbsp;");

         out.println("<tr><td width=200 align=right>Comment</td></tr>");

         out.println("<tr><td width=200><textarea name=c  "
                     + "HEIGHT=80 width=200 style=\"HEIGHT: 80px; WIDTH: 200px\">");
         out.print(comm);
         out.println("</textarea></td></tr>");
         out.println("</table></td></tr>");


         /*
           out.println("<td width=200 align=right>&nbsp;");
           out.println("<td width=200>&nbsp;");

           out.println("<tr><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td></tr>" +
           "<tr><td colspan=4 align=center>" +
           "<input type=button id=UPDATE name=UPDATE value=Update style=\"WIDTH: 100px\" onClick='valForm(\"UPDATE\")'>&nbsp;" +
           "<input type=button id=DELETE name=DELETE value=Delete style=\"WIDTH: 100px\" onClick='valForm(\"DELETE\")'>&nbsp;" +
           "<input type=reset value=Reset style=\"WIDTH: 100px\"></td></tr>");
           out.println("</table>");
         */



         // buttons table
         out.println("<tr><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0 width=300>");
         out.println("<tr><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td></tr>" +
                     "<tr><td colspan=4 align=center>" +
                     "<input type=button style=\"WIDTH: 100px\" "
                     + "value=\"Back\" onClick='location.href=\""
                     //+getServletPath("viewUVarSet?") + oldQS + "\"'>&nbsp;"+
                     +getServletPath("viewUVarSet?&RETURNING=YES")  + "\"'>&nbsp;"+

                     "<input type=reset value=Reset style=\"WIDTH: 100px\">&nbsp;"+
                     "<input type=button id=DELETE name=DELETE value=Delete style=\"WIDTH: 100px\" onClick='valForm(\"DELETE\")'>&nbsp;"+
                     "<input type=button id=UPDATE name=UPDATE value=Update style=\"WIDTH: 100px\" onClick='valForm(\"UPDATE\")'>&nbsp;");

         out.println("</td></tr>");
         out.println("</table></td></tr>");
         //

         // Store some extra information needed by doPost()
         out.println("<input type=\"hidden\" NAME=oper value=\"\">");
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
    * Creates a new unified variable set.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if uvarset created.
    *         False if uvarset not created.
    */
   private boolean createUVarSet(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int sid, id, pid;
         String name = null, comm = null;
         connection.setAutoCommit(false);
         System.err.println("create!");
         id = Integer.parseInt((String) session.getValue("UserID"));
         pid = Integer.parseInt((String) session.getValue("PID"));
         sid = Integer.parseInt(request.getParameter("sid"));
         name = request.getParameter("name");
         comm = request.getParameter("comm");

         DbUVariable dbuv = new DbUVariable();
         dbuv.CreateUVariableSet(connection, sid, pid, name, comm, id);
         errMessage = dbuv.getErrorMessage();
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
                       "UnifiedVariableSets.New.Create", errMessage,
                       "viewUVarSet", isOk);
      return isOk;
   }


   /**
    * Deletes a unified variable set.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @param connection The current connection.
    * @return True if unified variable set deleted.
    *         False if unified variable set not deleted.
    */
   private boolean deleteUVarSet(HttpServletRequest request,
                              HttpServletResponse response,
                              Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);

         int userId = Integer.parseInt((String) session.getValue("UserID"));
         int uvarSetId = Integer.parseInt(request.getParameter("uvsid"));
         DbUVariable dbUVariable = new DbUVariable();
         dbUVariable.DeleteUVariableSet(connection, uvarSetId, userId);
         errMessage = dbUVariable.getErrorMessage();
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
                       "UnifiedVariableSets.Edit.Delete", errMessage, "viewUVarSet",
                       isOk);
      return isOk;
   }


   /**
    * Updates a unified variable set.
    *
    * @param request The request from client.
    * @param response The response to client.
    * @param connection The current connection.
    * @return True if unified variable set updated.
    *         False if unified variable set not updated.
    */
   private boolean updateUVarSet(HttpServletRequest request,
                              HttpServletResponse response,
                              Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
      
         int uvarSetId = Integer.parseInt(request.getParameter("uvsid"));
         int userId = Integer.parseInt((String) session.getValue("UserID"));
         String name = request.getParameter("n");
         String comment = request.getParameter("c");
         DbUVariable dbUVariable = new DbUVariable();
         dbUVariable.UpdateUVariableSet(connection, name, comment,
                                        uvarSetId, userId); 
         errMessage = dbUVariable.getErrorMessage();
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
                       "UnifiedVariableSets.Edit.Update", errMessage, "viewUVarSet",
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
      out.println("		if (confirm('Are you sure you want to delete the variable set?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the variable set?')) {");
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
	


   /***************************************************************************************
    * *************************************************************************************
    * The Membership page
    */
   private void writeMember(HttpServletRequest req, HttpServletResponse res)
      throws IOException 
   {
      String oper = null;
      HttpSession session = req.getSession(true);
      Statement stmt = null;
      Connection conn = (Connection) session.getValue("conn");
      String sid, uvsid, pid, UserId;
      String item; // This variable tells which of the parameter that has changed
      sid = req.getParameter("sid");
      uvsid = req.getParameter("uvsid");
      
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
         sid = findSid(conn, pid);
         uvsid = findUVsid(conn, pid, sid);
      } else if (item.trim().equals("sid")) {
         uvsid = findUVsid(conn, pid, sid);
      } else {
      }

      if (oper == null)
         oper = "SELECT";

      out.println("<html>");
      out.println("<head><title>Variable set membership</title>");
      HTMLWriter.css(out,getURL("style/axDefault.css"));
      writeMemberScript(out);
      out.println("</head>");
      out.println("<body>");



      // new look
      out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
      out.println("<tr>");
      out.println("<td width=14 rowspan=3></td>");
      out.println("<td width=736 colspan=2 height=15>");
      out.println("<center>" +
                  "<b style=\"font-size: 15pt\">Phenotypes - Unified Variable Sets - Membership</b></center>");
      out.println("</td></tr>");
      out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
      out.println("</tr></table>");

      if ( (oper.equalsIgnoreCase("ADD") || oper.equalsIgnoreCase("REM")) ) 
      {
         try 
         {
            conn.setAutoCommit(false);
            
            DbUVariable uvars = new DbUVariable();

            if (oper.equalsIgnoreCase("ADD")) 
            {
               // Add individuals to a grouping
               String[] vars = req.getParameterValues("avail");
               if (vars != null) 
               {
                   uvars.CreateUVariableSetLinks(conn, Integer.valueOf(uvsid),vars, Integer.valueOf(pid), Integer.valueOf(UserId));
               }
            } 
            else if (oper.equalsIgnoreCase("REM")) 
            {
               // Remove individuals from a grouping
               String[] uvids = req.getParameterValues("incl");
               if (uvids != null) 
               {
                   uvars.DeleteUVariableSetLinks(conn, uvids, Integer.valueOf(uvsid));
               }
            }
            
            conn.commit();
            
            writeMemberPage(conn, req, oper, pid, sid, uvsid, out);
         }
         catch (Exception e) 
         {
            try 
            {
               conn.rollback();
               out.println("<pre>Error:\nUnhandled database exception!\n"
                           + e.getMessage()
                           +"\nEnd error.</pre>");
               e.printStackTrace(System.err);
            } 
            catch (SQLException ignored) 
            {} 
            finally 
            {
               try {
                  if (stmt != null) stmt.close();
               } catch (SQLException ignored) {}
            }

         }
      } 
      else if (oper.equalsIgnoreCase("SELECT") ||
                 oper.equalsIgnoreCase("DISPLAY")) 
      {
         writeMemberPage(conn, req, oper, pid, sid, uvsid, out);
      } 
      else 
      {
         writeMemberPage(conn, req, oper, pid, sid, uvsid, out);
      }

      out.println("</body>");
      out.println("</html>");
   }


   private void writeMemberPage(Connection conn,
                                HttpServletRequest req,
                                String oper,
                                String pid,
                                String sid,
                                String uvsid,
                                PrintWriter out) {
      Statement stmt = null;
      ResultSet rset = null;

      // Set method to post to prevent that someone sees the paramaters through the url
      out.println("<form name=\"form1\" action=\""+getServletPath("viewUVarSet/membership")+"\" method=\"post\">");
      out.println("<table width=750 border=0 cellspacing=0 cellspacing=1>");
      try {
         // ********************************************************************************
         // Available species units
         out.println("<tr><td>Species<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID, NAME FROM gdbadm.V_SPECIES_2 " +
                                  "WHERE PID=" + pid + " order by NAME");
         out.println("<select name=sid onChange='selChanged(\"sid\")'>");
         boolean first=true;
         while (rset.next()) {
            if (first && sid.equals("-1")) {
               // It's the first time -> set sid
               first = false;
               sid = rset.getString("SID");
            }
            if (sid.equals(rset.getString("SID")))
               out.println("<option selected value=\"" + rset.getString("SID") +
                           "\">" + rset.getString("NAME"));
            else
               out.println("<option value=\"" + rset.getString("SID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");
         // ********************************************************************************
         // Available unified variable sets
         out.println("<td>Unified Variable set<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         System.err.println("sid="+sid+" pid="+pid);
         rset = stmt.executeQuery("SELECT UVSID, NAME FROM gdbadm.V_U_VARIABLE_SETS_3 " +
                                  "WHERE SID=" + sid + " AND PID=" + pid + " order by NAME");
         out.println("<select name=uvsid onChange='selChanged(\"uvsid\")'>");
         first=true;
         while (rset.next()) {
            if (first && uvsid.equals("-1")) {
               // It's the first time -> set msid
               first = false;
               uvsid = rset.getString("UVSID");
            }
            if (uvsid.equals(rset.getString("UVSID")))
               out.println("<option selected value=\"" + rset.getString("UVSID") +
                           "\">" + rset.getString("NAME"));
            else
               out.println("<option value=\"" + rset.getString("UVSID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<hr>");
         // ********************************************************************************
         // Availible unified variables
         out.println("<table><tr><td valign=middle align=right>");
         out.println("Available unified variables<br>");
         out.println("<select name=\"avail\" width=200px multiple size=15 "
                     + "style=\"WIDTH: 200px\">");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         StringBuffer sbSQL = new StringBuffer();
         sbSQL.append("SELECT UVID, NAME FROM gdbadm.V_U_VARIABLES_3 ");
         sbSQL.append("WHERE PID=" + pid + " AND SID=" + sid);
         rset = stmt.executeQuery(sbSQL.toString());
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("UVID") + "\">"
                        + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td><td valign=middle align=middle>");
         out.println("<input type=\"button\" name=\"add\" value=\">\" onClick='add_var()'>");
         out.println("<br>");
         out.println("<input type=\"button\" name=\"rem\" value=\"<\" onClick='rem_var()'>");
         out.println("</td>");
         // ********************************************************************************
         // Included unified variables
         out.println("<td valign=middle align=left>");
         out.println("Included unified variables<br>");
         out.println("<select name=\"incl\" width=200px multiple size=15 "
                     + "style=\"WIDTH: 200px\">");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT v.UVID, v.NAME " +
                                  "FROM gdbadm.V_U_VARIABLES_1 v, gdbadm.V_R_U_VAR_SET_1 r "+
                                  "WHERE v.UVID=r.UVID AND " +
                                  "r.UVSID=" + uvsid + " " +
                                  "order by v.NAME");
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("UVID") + "\">"
                        + rset.getString("NAME"));
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
      out.println("	document.forms[0].oper.value='SELECT';");
      out.println("	document.forms[0].item.value = item;");
      out.println("	document.forms[0].submit();");
      out.println("	return (true);");
      out.println("}");
      out.println("function rem_var() {");
      out.println("	document.forms[0].oper.value='REM';");
      out.println("	document.forms[0].submit();");
      out.println("	return (true);");
      out.println("}");
      out.println("");
      out.println("function add_var() {");
      out.println("	document.forms[0].oper.value='ADD';");
      out.println("	document.forms[0].submit();");
      out.println("	return (true);");
      out.println("}");
      out.println("");
      out.println("");
      out.println("//-->");
      out.println("</script");	
					
   }

}

