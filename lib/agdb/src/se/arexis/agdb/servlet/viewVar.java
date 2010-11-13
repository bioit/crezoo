/*
  $Log$
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

  Revision 1.2  2002/10/18 11:41:12  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.8  2001/05/31 07:07:14  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.7  2001/05/29 14:32:03  frob
  Replaced getStatus with commitOrRollback. New keys added to Errors.properties.

  Revision 1.6  2001/05/22 06:17:07  roca
  Backfuncktionality fixed for all (?) servlets/subpages

  Revision 1.5  2001/05/15 05:41:18  frob
  Changed all methods that used to call writeError() to write an error page. These
  methods now calls the general method commitOrRollback() which does the commit or
  rollback operation as well as handle any errors. writeError() was removed.

  Revision 1.4  2001/05/03 07:57:51  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.3  2001/05/03 06:48:25  frob
  Calls to removeOper changed to use the general removeQSParameter.
  The previously called method is removed.

  Revision 1.2  2001/05/03 06:45:36  frob
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

public class viewVar extends SecureArexisServlet
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

      /*	HttpSession session = req.getSession(true);

		String UserID = (String) session.getValue("UserID");
		Boolean bLoginOk = (Boolean) session.getValue("LoginOk");
		if (UserID == null || UserID.equalsIgnoreCase("") ||
                bLoginOk == null || bLoginOk.booleanValue() != true) {
                res.sendRedirect("/servlets/redirectClass");
                return;
		}
      */
      String extPath = req.getPathInfo();
      //System.err.println("path:"+extPath);
      //System.err.println(req.getQueryString());

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
                     + " <TITLE>View variables frane</TITLE>"
                     + "</HEAD>"
                     + "<frameset rows=\"185,35,*\" framespacing=\"0\" border=\"true\">" //
                     + "<frame name=\"viewtop\" "
                     + "src=\"" +getServletPath("viewVar/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewmiddle\" "
                     + "src=\""+getServletPath("viewVar/middle?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewbottom\""
                     + "src=\""+getServletPath("viewVar/bottom?") + bottomQS + "\" "
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
         type = null,
         unit = null,
         orderby = null;
      String item; // Tells which paramater that has changed.
      String pid = (String) session.getValue("PID");
      suid = req.getParameter("suid");
      item = req.getParameter("item");
      if (item == null) {
         //	sid = findSid(conn, pid);
      } else if (item.equals("sid")) {
         ; //cid = findCid(conn, pid, sid);
      } else if (item.equals("no")) {
         // No changes in the selection
      }
      //	session.putValue("SID", sid);
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

      name = req.getParameter("name");
      if (name != null)
         output.append("&name=").append(name);
      type = req.getParameter("type");
      if (type != null)
         output.append("&type=").append(type);
      unit = req.getParameter("unit");
      if (unit != null)
         output.append("&unit=").append(unit);
      // Set the parameters STARTINDEX and ROWS
      if (!action.equals("NOP"))
         output.append(setIndecis(pid, suid, item, action, req, session));
      output.append("&suid=").append(suid);
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
         rset = stmt.executeQuery("SELECT SID FROM sysadm.V_SAMPLING_UNITS_PRJ WHERE PID=" + 
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
   private String findCid(Connection conn, String pid, String sid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID FROM sysadm.V_CHROMOSOMES_PRJ WHERE " +
                                  "PID=" + pid + " AND SID=" + sid + " " +
                                  "ORDER BY NAME");
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
   private String findMid(Connection conn, String cid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT MID FROM sysadm.MARKERS WHERE CID=" + 
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
   private String setIndecis(String pid, String suid, String item_changed, String action, HttpServletRequest req, HttpSession session) {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(pid, suid, req, session);
      maxRows = getMaxRows(session);//Integer.parseInt( (String) session.getValue("MaxRows"));
      if (req.getParameter("STARTINDEX") != null && 
          item_changed != null && 
          item_changed.equals("no") )
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

   private int countRows(String pid, String suid, HttpServletRequest req, HttpSession session) {
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer(512);
      try {
         sbSQL.append("SELECT count(*) " +
                      "FROM gdbadm.V_VARIABLES_3 WHERE " +
                      "PID=" + pid + " AND SUID=" + suid + " ");
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
   
   /**
    * This is the normal buildFilter. This adds the order by clause.
    * If no order by clause is possible call buildFilter(req,false);
    */
   private String buildFilter(HttpServletRequest req) 
   {
       return buildFilter(req,true);
   }
   
   private String buildFilter(HttpServletRequest req, boolean order) {
      String name = null,
         type = null,
         unit = null,
         orderby = null;
      StringBuffer filter = new StringBuffer(256);
      name = req.getParameter("name");
      type = req.getParameter("type");
      unit = req.getParameter("unit");
      orderby = req.getParameter("ORDERBY");

      if (name != null && !"".equalsIgnoreCase(name))
         filter.append("and NAME like '" + name + "'");
      // possible values for type are *, E, N
      if (type != null && (
                           type.equals("*") ||
                           type.equals("E") ||
                           type.equals("N")
                           ))
         filter.append(" and TYPE like'" + type + "'");
      if (unit != null && !"".equalsIgnoreCase(unit))
         filter.append(" and UNIT like'" + unit + "'");
      
      /** Cannot be used then counting */
      if (order)
      {
          if (orderby != null && !"".equalsIgnoreCase(orderby))
             filter.append(" order by " + orderby);
          else
             filter.append(" order by NAME");
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
      HttpSession session = req.getSession(true);
      Connection conn = null;
      res.setContentType("text/html");
      PrintWriter out = res.getWriter();
      String oper;
      Statement stmt = null;
      ResultSet rset = null;
      int startIndex = 0, rows = 0, maxRows = 0;
      String suid, name, type, unit,  orderby, oldQS, newQS, action, pid;
      //sid
      oper = req.getParameter("oper");
      try {
         conn = (Connection) session.getValue("conn");
         if (oper == null || "".equals(oper))
            oper = "SELECT";

         if (oper.equals("NEW")) {
            if (!createVar(req, res, conn))
               ; //return;
         }
         int currentPrivs[] = (int [])session.getValue("PRIVILEGES");
         pid = (String) session.getValue("PID");
         //sid = req.getParameter("sid");
         suid = req.getParameter("suid");
         //System.err.println("topSUID="+suid);

         name = req.getParameter("name");
         type = req.getParameter("type");
         unit = req.getParameter("unit");
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
         if (suid == null) suid = "-1";
         if (name == null) name = "";
         if (type == null ||
             !(type.equals("*") || type.equals("N") || type.equals("E")))
            type = "*";
         if (unit == null) unit = "";
         if (orderby == null) orderby = "NAME";
         if (action == null) action = "NOP";
         if (pid == null || "".equalsIgnoreCase(pid))
            pid = "-1";

         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+getURL("style/view.css")+"\">");
         out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out.println("<base target=\"content\">");

         writeTopScript(out);
         out.println("<title>View variables</title>");
         out.println("</head>");

         out.println("<body bgcolor=\"#ffffd0\">"
                     +"<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
                     +"<tr>"
                     + "<td width=\"14\" rowspan=\"3\">"
                     +"</td>"
                     +"<td width=\"736\" colspan=\"2\" height=\"15\">"
                     +"<form method=get action=\"" +getServletPath("viewVar") +"\">"
                     +"<p align=\"center\"><font size=\"2\"><b  style=\"font-size: 15pt\">Phenotypes - Variables - View & Edit</b>"
                     +"</font></td></tr>"
                     +"<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>"
                     +"</tr><tr><td width=\"517\">");

         out.println("<table width=488 height=\"92\">"
                     +"<td><b>Sampling unit</b><br><select name=suid "
                     +"name=select onChange='document.forms[0].submit()'  style=\"HEIGHT: 22px; WIDTH: 126px\">");

         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SUID FROM gdbadm.V_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid + " AND STATUS='E'"+ " order by NAME");
         while (rset.next()) {
            if (suid != null && suid.equalsIgnoreCase(rset.getString("SUID")))
               out.println("<OPTION selected value=\"" + rset.getString("SUID") + "\">" +
                           rset.getString("NAME"));
            else
               out.println("<OPTION value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME"));
         }
         rset.close();
         stmt.close();
         out.println("</SELECT>");


         out.println("</td><td><b>Name</b><br>"
                     +"<input id=name name=name value=\"" + name + "\" style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\">");


         out.println("<td><b>Type</b><br><select name=type "
                     +"name=select onChange='document.forms[0].submit()'  style=\"HEIGHT: 22px; WIDTH: 126px\">");

         out.println("<option" +
                     (type.equals("*") ? " selected " : " ") +
                     "value=\"*\">*");
         out.println("<option" +
                     (type.equals("N") ? " selected " : " ") +
                     "value=\"N\">N");
         out.println("<option" +
                     (type.equals("E") ? " selected " : " ") +
                     "value=\"E\">E");
         out.println("</select>");


         out.println("<td><b>Unit</b><br>"
                     + "<input id=unit name=unit value=\"" + unit + "\"style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\">"

                     +"</td></table></td>");

         //Buttons
         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");


         out.println(privDependentString(currentPrivs,VAR_W,
                                         /*if true*/"<input type=button value=\"New Variable\""
                                         + " onClick='parent.location.href=\"" +getServletPath("viewVar/new?") + newQS + "\"' "
                                         +"height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">"
                                         +"</td>",
                                         /*if false*/"<input type=button disabled value=\"New Variable\""
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
         out.println("<input type=\"hidden\" name=item value=no>");

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

         /*
           out.println("<form method=get action=\"../viewVar\">");
           out.println("<div style=\"POSITION: absolute; TOP: 5px; LEFT: 360px\"><H3>Variables</H3></div>");
           out.println("<div style=\"POSITION: absolute; TOP: 50px; LEFT: 40px\">Species</div>");
           out.println("<SELECT name=sid " +
           "onChange='selChanged(\"sid\")' " +
           "style=\"POSITION: absolute; TOP: 65px; LEFT: 40px; HEIGHT: 25px; WIDTH: 150px\">");

           stmt = conn.createStatement();
           rset = stmt.executeQuery("SELECT NAME, SID FROM sysadm.V_SPECIES_PRJ " +
           "WHERE PID=" + pid + " order by NAME");
           while (rset.next()) {
           if (sid != null && sid.equalsIgnoreCase(rset.getString("SID")))
           out.println("<OPTION selected value=\"" + rset.getString("SID") + "\">" + rset.getString("NAME"));
           else
           out.println("<OPTION value=\"" + rset.getString("SID") + "\">" + rset.getString("NAME"));
           }
           out.println("</SELECT>");
           out.println("<div style=\"POSITION: absolute; TOP: 50px; LEFT: 200px\">Name</div>");
           out.println("<INPUT name=name value=\"" + name + "\" " +

           "style=\"POSITION: absolute; TOP: 65px; LEFT: 200px; HEIGHT: 25px; WIDTH: 150px\">");

           out.println("<div style=\"POSITION: absolute; TOP: 50px; LEFT: 360px\">Type</div>");
           out.println("<select name=type " +
           "style=\"POSITION: absolute; TOP: 65px; LEFT: 360px; HEIGHT: 25px; WIDTH: 150px\">");
           out.println("<option" +
           (type.equals("*") ? " selected " : " ") +
           "value=\"*\">*");
           out.println("<option" +
           (type.equals("N") ? " selected " : " ") +
           "value=\"N\">N");
           out.println("<option" +
           (type.equals("E") ? " selected " : " ") +
           "value=\"E\">E");
           out.println("</select>");

           out.println("<div style=\"POSITION: absolute; TOP: 50px; LEFT: 520px\">Unit</div>");
           out.println("<INPUT name=unit value=\"" + unit + "\" " +
           "style=\"POSITION: absolute; TOP: 65px; LEFT: 520px; HEIGHT: 25px; WIDTH: 150px\">");

           out.println("<INPUT type=button value=\"New variable\" "
           + "style=\"POSITION: absolute; TOP: 5px; LEFT: 700px; "
           + "HEIGHT: 25px; WIDTH: 100px\" "
           + "onClick='parent.location.href=\"/servlets/viewVar/new?sid=" + sid + "&" + newQS + "\"'>");
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
           out.println("<input type=\"hidden\" name=item value=no>");
           out.println("</form>");

           out.println("<DIV style=\"POSITION: absolute; TOP: 165px; LEFT: 10px\">"); //margin-left: 10px\">");

           out.println(buildInfoLine(action, startIndex, rows, maxRows));
           out.println("</DIV>");

           out.println("<TABLE align=left bgColor=lightskyblue border=0 cellPadding=1");
           out.println("cellSpacing=0 style=\"POSITION: absolute; LEFT: 10px; TOP: 185px; WIDTH: 800px\">");

           out.println("<TR>");
           out.println("<TD nowrap STYLE=\"WIDTH: 150px\">"
           + "<div onClick='parent.location.href=\"/servlets/viewVar?"
           + "ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\"'>");
           if (orderby.equalsIgnoreCase("NAME"))
           out.println("<b>Name</b>");
           else
           out.println("Name");
           out.println("</div></TD>");
           out.println("<TD STYLE=\"WIDTH: 50px\">"
           + "<div onClick='parent.location.href=\"/servlets/viewVar?"
           + "ACTION=DISPLAY&" + newQS + "&ORDERBY=TYPE\"'>");
           if (orderby.equalsIgnoreCase("TYPE"))
           out.println("<b>Type</b>");
           else
           out.println("Type");
           out.println("</div></TD>");
           out.println("<TD STYLE=\"WIDTH: 150px\">"
           + "<div onClick='parent.location.href=\"/servlets/viewVar?"
           + "ACTION=DISPLAY&" + newQS + "&ORDERBY=UNIT\"'>");
           if (orderby.equalsIgnoreCase("UNIT"))
           out.println("<b>Unit</b>");
           else
           out.println("Unit");
           out.println("</div></TD>");
           out.println("<TD STYLE=\"WIDTH: 180px\">"
           + "<div onClick='parent.location.href=\"/servlets/viewVar?"
           + "ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\"'>");
           if (orderby.equalsIgnoreCase("COMM"))
           out.println("<b>Comment</b>");
           else
           out.println("Comment");
           out.println("</div></TD>");
           out.println("<TD STYLE=\"WIDTH: 50px\">"
           + "<div onClick='parent.location.href=\"/servlets/viewVar?"
           + "ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\"'>");
           if (orderby.equalsIgnoreCase("USR"))
           out.println("<b>User</b>");
           else
           out.println("User");
           out.println("</div></TD>");
           out.println("<TD STYLE=\"WIDTH: 120px\">"
           + "<div onClick='parent.location.href=\"/servlets/viewVar?"
           + "ACTION=DISPLAY&" + newQS + "&ORDERBY=TS\"'>");
           if (orderby.equalsIgnoreCase("TS"))
           out.println("<b>Last updated</b>");
           else
           out.println("Last updated");
           out.println("</div></TD>");
           out.println("<TD STYLE=\"WIDTH: 50px\"></TD>");
           out.println("<TD STYLE=\"WIDTH: 50px\"></TD></TR></TABLE>");
         */
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
      out.println("function newMark() {");
      out.println("  alert('popup new window for creation of marker!');");
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
      out.println("");
      out.println("function selChanged(item) {");
      out.println("//	document.forms[0].oper.value='SEL_CHANGED';");
      out.println("	document.forms[0].item.value=item;");
      out.println("	document.forms[0].submit();");
      out.println("}");
      out.println("");
      out.println("");
      out.println("");
      out.println("//-->");
      out.println("</script>");	

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
         out.println("<td width=100><a href=\"" + getServletPath("viewVar")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\">");
         if(choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown ><b>Name</b></FONT></a></td>\n");
         else out.println("Name</a></td>\n");
         //Type
         out.println("<td width=100><a href=\"" + getServletPath("viewVar")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TYPE\">");
         if(choosen.equals("TYPE"))
            out.println("<FONT color=saddlebrown ><b>Type</b></FONT></a></td>\n");
         else out.println("Type</a></td>\n");

         //Unit
         out.println("<td width=100><a href=\"" + getServletPath("viewVar")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=UNIT\">");
         if(choosen.equals("UNIT"))
            out.println("<FONT color=saddlebrown ><b>Unit</b></FONT></a></td>\n");
         else out.println("Unit</a></td>\n");

         //Comment
         out.println("<td width=150><a href=\"" + getServletPath("viewVar")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\">");
         if(choosen.equals("COMM"))
            out.println("<FONT color=saddlebrown><b>Comment</b></FONT></a></td>\n");
         else out.println("Comment</a></td>\n");

         //USER
         out.println("<td width=100><a href=\"" + getServletPath("viewVar")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
         else out.println("User</a></td>\n");

         //Updated
         out.println("<td width=150><a href=\"" + getServletPath("viewVar")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TS\">");
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
         String pid = null, suid = null, action = null;
         String oldQS = req.getQueryString();
         action = req.getParameter("ACTION");
         //sid = req.getParameter("sid");
         suid=req.getParameter("suid");
         int currentPrivs[] = (int [])session.getValue("PRIVILEGES");
         //System.err.println("suid="+suid);
         //System.err.println("qs="+req.getQueryString());
         pid = (String) session.getValue("PID");
         if (pid == null) pid = "-1";
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
                     + "<title></title>\n"
                     + "</head>\n"
                     + "<body>\n");

         conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();
         StringBuffer sbSQL = new StringBuffer(512);
         sbSQL.append("SELECT NAME, TYPE, UNIT, substr(COMM, 0, 15) as COMM, " +
                      "USR, to_char(TS, '" + getDateFormat(session) + "') as TC_TS, VID " +
                      "FROM gdbadm.V_VARIABLES_3 WHERE SUID=" + suid + " AND PID=" + pid + " ");
         String qs = req.getQueryString();
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);
         // out.println(sbSQL.toString());
         rset = stmt.executeQuery(sbSQL.toString());
         out.println("<TABLE align=left border=0 cellPadding=1");
         out.println("cellSpacing=0 STYLE=\"WIDTH: 800px;\">");
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
            out.println("<td width=100>" + formatOutput(session,rset.getString("NAME"),10) + "</TD>");
            out.println("<td width=100>" + rset.getString("TYPE") + "</TD>");
            out.println("<td width=100>" + rset.getString("UNIT") + "</TD>");
            out.println("<td width=150>" + formatOutput(session,rset.getString("COMM"),12) + "</TD>");
            out.println("<td width=100>" + rset.getString("USR") + "</TD>");
            out.println("<td width=150>" + rset.getString("TC_TS") + "</TD>");

            out.println("<td width=50><A HREF=\""+getServletPath("viewVar/details?vid=") +
                        rset.getString("VID") +	"&" +
                        oldQS + "\" target=\"content\">Details</A></TD>");

            out.println("<td width=50>");

            out.println(privDependentString(currentPrivs,VAR_W,
                                            /*if true*/"<A HREF=\""+getServletPath("viewVar/edit?vid=")+
                                            rset.getString("VID") +	"&" +
                                            oldQS + "\" target=\"content\">Edit</A></TD></TR>",
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
      Connection conn =  null;
      Statement stmt_curr = null, stmt_hist = null;
      ResultSet rset_curr = null, rset_hist = null;
      String prev_type = null;
      String prev_unit = null; 
      String prev_name = null;
      String prev_comm = null;
      String prev_usr = null;
      String prev_ts = null;
      String curr_type = null;
      String curr_unit = null;
      String curr_name = null;
      String curr_comm = null;
      String curr_usr = null;
      String curr_ts = null;
      boolean has_history = false;

      try {
         String oldQS = buildQS(req);
         String vid = req.getParameter("vid");
         if (vid == null || vid.trim().equals("")) vid = "-1";
			
         conn = (Connection) session.getValue("conn");
         // Get the current data of the chromosome
         stmt_curr = conn.createStatement();
         String strSQL = "SELECT SNAME, VID, NAME, TYPE, UNIT, COMM, " +
            "USR, to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS " +
            "FROM gdbadm.V_VARIABLES_3 WHERE " +
            "VID = " + vid;
         rset_curr = stmt_curr.executeQuery(strSQL);

         // Get the history
         stmt_hist = conn.createStatement();
         strSQL = "SELECT NAME, TYPE, UNIT, COMM, " +
            "USR, to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS, TS as dummy " +
            "FROM gdbadm.V_VARIABLES_LOG WHERE " +
            "VID = " + vid + " order by dummy desc";

         rset_hist = stmt_hist.executeQuery(strSQL);

         if (rset_curr.next()) {
            curr_name = rset_curr.getString("NAME");
            curr_type = rset_curr.getString("TYPE");
            curr_unit = rset_curr.getString("UNIT");
            curr_comm = rset_curr.getString("COMM");
            curr_usr = rset_curr.getString("USR");
            curr_ts = rset_curr.getString("TC_TS"); // Time stamp
            if (curr_name == null) curr_name = "";
            if (curr_unit == null) curr_unit = "";
            if (curr_comm == null) curr_comm = "";
         }
         if (rset_hist.next()) {
            prev_name = rset_hist.getString("NAME");
            prev_type = rset_hist.getString("TYPE");
            prev_unit = rset_hist.getString("UNIT");
            prev_comm = rset_hist.getString("COMM");
            prev_usr = rset_hist.getString("USR");
            prev_ts = rset_hist.getString("TC_TS");
            has_history = true;
         }
         out.println("<html>\n"
                     + "<head>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Details</title>\n"
                     + "<META HTTP_EQUIV=\"Pragma\" CONTENT=\"no-cache\">"
                     + "</head>\n"
                     + "<body>\n");
         // new look
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Phenotypes - Variables - View & Edit - Details</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         /*
           out.println("<table width=800px cellPadding=0 cellSpacing=0 align=center border=0 style=\"PADDING-LEFT: 0\">");
           out.println("<tr><td align=left width=100px>");
           out.println("<input type=button style=\"WIDTH: 100px\" value=\"Back\" " +
           "onClick='location.href=\""+getServletPath("viewVar?") + oldQS + "\"'>");
           out.println("<td width=700px align=center><h3>Variable details</h3></td></tr>");
           out.println("<tr><td>&nbsp;</td></tr>");
           out.println("</table>");
         */

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

         out.println("<table nowrap align=center border=0 cellSpacing=0 width=740px>");

         out.println("<tr bgcolor=Black><td align=center colspan=9><b>");
         out.println("<font color=\"#ffffff\">Current Data</font></b></td></tr>");
         out.println("<tr bgcolor=\"#008B8B\"><td nowrap WIDTH=50 style=\"WIDTH: 50px\">Name</td>");
         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">Type</td>");
         out.println("<td nowrap WIDTH=100 style=\"WIDTH: 100px\">Unit</td>");
         out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">Comment</td>");
         out.println("<td nowrap WIDTH=120 style=\"WIDTH: 120px\">Last updated by</td>");
         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 120px\">Last updated</td></tr>");

         out.println("<tr bgcolor=white>");
         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" +
                     (curr_name.equalsIgnoreCase(prev_name) ? "": "<font color=red>") + curr_name);
         out.println((curr_name.equals(prev_name) ? "</td>" : "</font></td>"));
         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" +
                     (curr_type.equalsIgnoreCase(prev_type) ? "": "<font color=red>") + curr_type);
         out.println((curr_type.equals(prev_type) ? "</td>" : "</font></td>"));
			
         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" + 
                     (curr_unit.equalsIgnoreCase(prev_unit) ? "": "<font color=red>") + curr_unit);
         out.println((curr_unit.equals(prev_unit) ? "</td>" : "</font></td>"));

         out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" + 
                     (curr_comm.equals(prev_comm) ? "" : "<font color=red>") + curr_comm);
         out.println((curr_comm.equals(prev_comm) ? "</td>": "</font></td>")); 

         out.println("<td nowrap WIDTH=120 style=\"WIDTH: 120px\">" + curr_usr + "</td>");
         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 120px\">" + curr_ts + "</td>"); // Last updated
				
         out.println("<tr bgcolor=Black>");
         out.println("<td align=center colspan=9><b><font color=\"#ffffff\">History</font></b></td></tr>");
			
         curr_name = prev_name;
         curr_type = prev_type;
         curr_unit = prev_unit;
         curr_comm = prev_comm;
         curr_usr = prev_usr;
         curr_ts = prev_ts;
				
         if (curr_name == null) curr_name = "";
         if (curr_unit == null) curr_unit = "";
         if (curr_comm == null) curr_comm = "";
         boolean odd = true;
         while (rset_hist.next()) {
            prev_name = rset_hist.getString("NAME");
            prev_type = rset_hist.getString("TYPE");
            prev_unit = rset_hist.getString("UNIT");
            prev_comm = rset_hist.getString("COMM");
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
            out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" +
                        (curr_type.equalsIgnoreCase(prev_type) ? "": "<font color=red>") + curr_type);
            out.println((curr_type.equals(prev_type) ? "</td>": "</font></td>"));
            out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" +
                        (curr_unit.equalsIgnoreCase(prev_unit) ? "": "<font color=red>") + curr_unit);
            out.println((curr_unit.equals(prev_unit) ? "</td>": "</font></td>"));
            out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" + 
                        (curr_comm.equals(prev_comm) ? "": "<font color=red>") + curr_comm); 
            out.println((curr_comm.equals(prev_comm) ? "</td>": "</font></td>")); 
            out.println("<td nowrap WIDTH=120 style=\"WIDTH: 120px\">" + curr_usr + "</td>");
            out.println("<td nowrap WIDTH=50 style=\"WIDTH: 120px\">" + curr_ts + "</td>"); // Last updated
            curr_name = prev_name;
            curr_type = prev_type;
            curr_unit = prev_unit;
            curr_comm = prev_comm;
            curr_usr = prev_usr;
            curr_ts = prev_ts;

            if (curr_name == null) curr_name = "";
            if (curr_unit == null) curr_unit = "";
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
            out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" +
                        (curr_type.equals(prev_type) ? "" : "<font color=red>") + curr_type);
            out.println((curr_type.equals(prev_type) ? "</td>": "</font></td>"));
            out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" +
                        (curr_unit.equals(prev_unit) ? "" : "<font color=red>") + curr_unit);
            out.println((curr_unit.equals(prev_unit) ? "</td>": "</font></td>"));
            out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" +
                        (curr_comm.equals(prev_comm) ? "" : "<font color=red>") + curr_comm);
            out.println((curr_comm.equals(prev_comm) ? "</td>": "</font></td>"));
            out.println("<td nowrap WIDTH=120 style=\"WIDTH: 120px\">" + curr_usr + "</td>");
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

         out.println("<input type=button style=\"WIDTH: 100px\" value=\"Back\" " +
         //            "onClick='location.href=\""+getServletPath("viewVar?") + oldQS + "\"'>"+"&nbsp;");
            "onClick='location.href=\""+getServletPath("viewVar?&RETURNING=YES") + "\"'>"+"&nbsp;");

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
         if (createVar(req, res, conn))
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
      String suid = null, newQS, pid, oper, item;
      try {
         conn = (Connection) session.getValue("conn");

         pid = (String) session.getValue("PID");
         suid = req.getParameter("suid");
         newQS = removeQSParameterOper(buildQS(req));
         oper = req.getParameter("oper");

         if (oper == null || oper.trim().equals("")) oper = "SEL_CHANGED";

         if (pid == null || "".equalsIgnoreCase(pid))
            pid = "-1";
         //	item = req.getParameter("item");
         //	if (item == null) {
         //		sid = findSid(conn, pid);
         //	} else if (item.equals("sid")) {
         //		; //cid = findCid(conn, pid, sid);
         //	} else if (item.equals("no")) {
         //		// No changes in the selection
         //	}
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<base target=\"content\">");

         writeNewScript(out);
         out.println("<title>New variable</title>");
         out.println("</head>");
         out.println("<body>");


         // new "look"
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Phenotypes - Variables - New</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         //old			out.println("<center><H3>New variable</H3></center>");
         //			out.println("<br><br>");

         out.println("<form method=get action=\""+getServletPath("viewVar/new?") + newQS + "\">");
         out.println("<table>");
         out.println("<tr><td>");

         out.println("Sampling Unit<br>");
         out.println("<SELECT name=suid WIDTH=150 height=25 " +
                     "style=\"HEIGHT: 25px; WIDTH: 150px\" " +
                     "onChange='selChanged(\"sid\")'>");
         stmt = conn.createStatement();
         /*
           rset = stmt.executeQuery("SELECT NAME, SID FROM gdbadm.V_SPECIES_2 " +
           "WHERE PID=" + pid + " ORDER BY NAME");
         */

         rset = stmt.executeQuery("SELECT NAME, SUID FROM gdbadm.V_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid + " order by NAME");

         while (rset.next()) {
            if (suid != null && suid.equalsIgnoreCase(rset.getString("SUID")))
               out.println("<OPTION selected value=\"" + rset.getString("SUID") + "\">" +
                           rset.getString("NAME"));
            else
               out.println("<OPTION value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME"));
         }

         rset.close();
         stmt.close();
         out.println("</SELECT>");



         out.println("<td>Name<br>");
         out.println("<input type=text name=n width=150 height=25 " +
                     "style=\"WIDTH: 150px; HEIGHT: 25px\" maxlength=20>");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<td>Type<br>");
         out.println("<select name=t maxlength=20 width=50 height=25 " +
                     "style=\"WIDTH: 50px; HEIGHT: 25px\">");
         out.println("<option value=E>E");
         out.println("<option value=N>N");
         out.println("</select>");
         out.println("</td>");
         out.println("<td>Unit<br>");
         out.println("<input type=text name=u maxlength=10 " +
                     "width=150 height=25 " +
                     "style=\"WIDTHY: 150px; HEIGHT: 25px\">");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<td colspan=2 align=center>");
         out.println("Comment<br>");
         out.println("<textarea name=c maxlength=256 cols=40 rows=15 width=340 height=150 " +
                     "style=\"WIDTH: 340px; HEIGHT: 150px\">");
         out.println("</textarea>");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<tr><td COLSPAN=2>");
         out.println("<input type=button value=Cancel onClick='document.location.href=\""+getServletPath("viewVar?") + newQS + "\"'>");
         out.println("&nbsp;");
         out.println("<input type=button value=Create onClick='valForm()'>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=item value=\"no\">");
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
      out.println("	if ( (\"\" + document.forms[0].n.value) != \"\") {");
      out.println("		if (document.forms[0].n.value.length > 20) {");
      out.println("			alert('Name must be less than 20 characters!');");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("		if (document.forms[0].u.value.length > 10) {");
      out.println("			alert('Unit must be less than 10 characters!');");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	}");
      out.println("	");
      out.println("	");
      out.println("	if (rc) {");
      out.println("		if (confirm('Are you sure that you want to create the variable?')) {");
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
         if (deleteVar(req, res, conn))
         {
            writeFrame(req, res);
         }
      }
      else if (oper.equals("UPDATE"))
      {
         if (updateVar(req, res, conn))
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
         String oldQS = buildQS(req);
         String pid = (String) session.getValue("PID");
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         String vid = req.getParameter("vid");
         String sql = "SELECT SNAME, NAME, TYPE, UNIT, COMM, USR, " +
            "to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS " +
            "FROM gdbadm.V_VARIABLES_3 WHERE " +
            "VID = " + vid;
         rset = stmt.executeQuery(sql);

         rset.next();
         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);
         out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out.println("<title>Edit variable</title>");
         out.println("</head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<body>\n");


         // new look
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Phenotypes - Variables - View & Edit - Edit</b></font></center>");
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
           + "<tr><td align=left width=100px><input type=button style=\"WIDTH: 100px\" value=\"Back\" " +
           "onClick='location.href=\""+getServletPath("viewVar?") + oldQS + "\"'>"
           + "<td width=700px align=center><h3>Edit variable</h3></td></tr>"
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

         out.println("<FORM action=\""+getServletPath("viewVar/edit?vid=") + vid + "&" + oldQS + "\" method=\"post\" name=\"FORM1\">");

         // dynamic data table
         out.println("<tr><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0 width=300>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Changable data</font></td></tr>");

         out.println("<tr><td width=200 align=left>Name</td>");
         out.println("<td width=200 align=left>Type</td></tr>");

         out.println("<tr><td width=200><input name=n type=text maxlength=20 "
                     + "style=\"WIDTH: 200px\" value=\"" + rset.getString("NAME") + "\"></td>");

         out.println("<td width=200><select name=t width=50 height=25 style=\"WIDTH: 50px HEIGHT: 25px\">");
         if (rset.getString("TYPE").equals("E")) {
            out.println("<option selected value=E>E");
            out.println("<option value=N>N");
         } else {
            out.println("<option value=E>E");
            out.println("<option selected value=N>N");
         } out.println("</select>");
         out.println("<tr>");

         out.println("<tr><td width=200 align=left>Unit</td></tr>");
         out.println("<tr><td width=200><input type=text name=u value=\"" +
                     rset.getString("UNIT") + "\" maxlength=10></td></tr>");

         out.println("<tr><td width=200 align=left>Comment</td></tr>" +
                     "<tr><td width=200><textarea rows=10 name=c "
                     + "style=\"HIGHT: 60px; WIDTH: 200px\">");
         out.print(rset.getString("COMM"));
         out.println("</textarea></td></tr>");

         out.println("</table></td></tr>");




         /* old
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
                     "<input type=button style=\"WIDTH: 100px\" value=\"Back\" " +
           //          "onClick='location.href=\""+getServletPath("viewVar?") + oldQS + "\"'>&nbsp;"+
                     "onClick='location.href=\""+getServletPath("viewVar?&RETURNING=YES") + "\"'>&nbsp;"+
                     "<input type=reset value=Reset style=\"WIDTH: 100px\">&nbsp;"+
                     "<input type=button id=DELETE name=DELETE value=Delete style=\"WIDTH: 100px\" onClick='valForm(\"DELETE\")'>&nbsp;"+
                     "<input type=button id=UPDATE name=UPDATE value=Update style=\"WIDTH: 100px\" onClick='valForm(\"UPDATE\")'>&nbsp;");


         out.println("</td></tr>");
         out.println("</table></td></tr>");


         // Store some extra information needed by doPost()
         out.println("<input type=hidden  name=oper value=\"\">");
         out.println("<input type=hidden  name=RETURNING value=YES>");

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


   private boolean createVar(HttpServletRequest request,
                             HttpServletResponse response,
                             Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int suid, id;
         String name = null, type = null, unit = null, comm = null;
         connection.setAutoCommit(false);
         id = Integer.parseInt((String) session.getValue("UserID"));
         suid = Integer.parseInt(request.getParameter("suid"));
         name = request.getParameter("n");
         type = request.getParameter("t");
         unit = request.getParameter("u");
         comm = request.getParameter("c");

         if (name == null || name.trim().equals("") || name.length() > 20
             || type == null || type.trim().equals("") ||
             (comm != null && comm.length() > 256))
         {
            throw new Exception();
         }
         DbVariable dbv = new DbVariable();
         dbv.CreateVariable(connection, suid, name, type, unit, comm, id);

         errMessage = dbv.getErrorMessage();
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
                       "Variables.New.Create", errMessage, "viewVar",
                       isOk);
      return isOk;
   }

   
   /**
    * Deletes a variable.
    *
    * @param request The request from client.
    * @param response The response to client.
    * @param connection The current connection.
    * @return True if variable deleted.
    *         False if variable not deleted.
    */
   private boolean deleteVar(HttpServletRequest request,
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
         int variableId = Integer.parseInt(request.getParameter("vid"));
         DbVariable dbVariable = new DbVariable();
         dbVariable.DeleteVariable(connection, variableId, userId);
         errMessage = dbVariable.getErrorMessage();
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
                       "Variables.Edit.Delete", errMessage, "viewVar",
                       isOk);
      return isOk;
   }

   
   /**
    * Updates a variable.
    *
    * @param request The request from client.
    * @param response The response to client.
    * @param connection The current connection.
    * @return True if variable updated.
    *         False if variable not updated
    */
   private boolean updateVar(HttpServletRequest request,
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
         int variableId = Integer.parseInt(request.getParameter("vid"));
         String name = request.getParameter("n");
         String type = request.getParameter("t");
         String unit = request.getParameter("u");
         String comment = request.getParameter("c");
         DbVariable dbVariable = new DbVariable();
         dbVariable.UpdateVariable(connection, variableId, name, type,
                                   unit, comment, userId); 
         errMessage = dbVariable.getErrorMessage();
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
                       "Variables.Edit.Update", errMessage, "viewVar",
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
      out.println("		if (confirm('Are you sure you want to delete the variable?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the variable?')) {");
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
}

