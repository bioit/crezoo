/*
  $Log$
  Revision 1.8  2005/02/23 13:31:26  heto
  Converted database classes to PostgreSQL

  Revision 1.7  2005/01/31 16:16:41  heto
  Changing database to PostgreSQL. Problems with counts and selection buttons...

  Revision 1.6  2004/02/12 10:20:53  heto
  Fixed output of the function to change the users passwords.

  Revision 1.5  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.4  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.3  2003/04/25 08:57:46  heto
  Added functions for changing password.
  Hide disabled users in usersbox.

  Revision 1.2  2002/10/18 11:41:10  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:05  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.12  2001/05/30 13:45:08  frob
  Moved writeStatisticsPage to ArexisServlet.

  Revision 1.11  2001/05/30 13:35:37  frob
  Rewrote writeStatistics methods. Move most funktionality to ServletUtil.

  Revision 1.10  2001/05/30 09:19:21  frob
  Rewrote the statistics part of viewProj. Fixed some CSS stuff in HTMLWriter.
  Some keys added to Defaults.properties and Errors.properties

  Revision 1.9  2001/05/22 06:54:32  roca
  backfunktionality for administrator pages and privileges removed from roles (user mode)

  Revision 1.8  2001/05/21 12:05:37  frob
  Modified statistics section to decrease load time.

  Revision 1.7  2001/05/11 12:59:54  frob
  Changed all methods that used to call writeError() to write an error page. These
  methods now calls the general method commitOrRollback() which does the commit or
  rollback operation as well as handle any errors. writeError() was removed.

  Revision 1.6  2001/05/03 14:21:03  frob
  Implemented local version of errorQueryString and changed writeError to use this method.

  Revision 1.5  2001/05/03 07:57:45  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.4  2001/05/03 06:04:27  frob
  Calls to removeOper changed to use the general removeQSParameter.
  The previously called method is removed.

  Revision 1.3  2001/04/24 09:33:59  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:32  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.10  2001/04/18 09:26:48  frob
  Removed the size of the main table used on the webpages.

  Revision 1.1.1.1.2.9  2001/04/11 06:34:18  frob
  Update of FileTypeDefinition constant names caused updates here too.

  Revision 1.1.1.1.2.8  2001/04/10 13:08:44  frob
  Changed call to Parse to use constants instead of strings.

  Revision 1.1.1.1.2.7  2001/04/10 09:57:08  frob
  Removed static initializer and moved it to the database class.

  Revision 1.1.1.1.2.6  2001/04/10 08:25:04  frob
  Changed errormessage when importing role privileges fails.

  Revision 1.1.1.1.2.5  2001/04/09 07:18:11  frob
  Reverted some changes in the HTML code in writeImplRolePage.

  Revision 1.1.1.1.2.4  2001/04/06 12:58:31  frob
  Removed some unused variables.

  Revision 1.1.1.1.2.3  2001/04/06 12:41:12  frob
  Added static initializer for registring file definition types.
  Changed call to parse(), now passes parameters.
  No delimiter filed is added on the HTML-page.
  No parameters are retrieved from the request-object.
  Length of file-name field is resized.
  HTML in writeImpRolePage is validated.
  writeImpRolePage and createRoleFile are restructured.

  Revision 1.1.1.1.2.2  2001/03/28 13:47:58  frob
  Added catch() for InputDataFileException which can be raised
  from the parse()-method.
  Added check that the connection object is created before doing
  rollback on it (to avoid null pointer exception).

  Revision 1.1.1.1.2.1  2001/03/28 12:37:50  frob
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

/**
 * 
 */
public class viewProj extends SecureArexisServlet
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
      throws ServletException, IOException 
   {

      HttpSession session = req.getSession(true);
      //check if project has been choosen
      Boolean projSet= (Boolean) session.getValue("projSet");
      if(projSet == null || projSet.booleanValue() != true)
      {
         redirect(res);
      }


      String extPath = req.getPathInfo();
      if (extPath == null || extPath.equals("") || extPath.equals("/")) {
         // The frame is requested
         writeOptions(req, res);
      } else if (extPath.equals("/options")) {
         writeOptions(req, res);
      } else if (extPath.equals("/roles")) {
         writeRole(req, res);
      } else if (extPath.equals("/statistics")) {
         writeStatistics(req, res);
      } else if (extPath.equals("/editRole")) {
         writeEditRole(req, res);
      } else if (extPath.equals("/impRole")) {
         writeImpRole(req, res);
      } else if (extPath.equals("/newRole")) {
         writeNewRole(req, res);
      } else if (extPath.equals("/impRoleMultipart")) {
         createRoleFile(req, res);
      } else if (extPath.equals("/users")) {
         writeUser(req, res);
      } else if (extPath.equals("/myaccount")) {
         writeMyAccount(req,res); 
      } else if (extPath.equals("/changepwd")) {
         changePwd(req,res);
      }
      
   }

   private String buildQS(HttpServletRequest req) 
   {
      StringBuffer output = new StringBuffer(512);
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      String rid;

      String pid = (String) session.getValue("PID");
      rid = req.getParameter("rid");
      if (rid != null)
         output.append("&rid=").append(rid);

      return output.toString().replace('%', '*');
   }

   private boolean updateSession(HttpServletRequest req, HttpServletResponse res) 
   {
      boolean ok = true;
      try {
         HttpSession session = req.getSession(true);
         String nr = req.getParameter("nr");
         String mnr = req.getParameter("mnr");
         String dr = req.getParameter("dr");
         String pid = req.getParameter("pid");
         if (!pid.equals((String) session.getValue("PID")) ) {
            // Project has changed
            // In this case we need to update the upper frame (navigator)
            // to display the correct project. How do we accomplish this ?
            updateProjectSettings(req, res);
         }
         setNullReplacement(session, nr);
         setDateFormat(session, dr);
         setMaxRows(session, Integer.parseInt(mnr));
      } catch (Exception e) {
         // Something went wrong
      }
      return true;
   }
   
   private void writeUserScript(PrintWriter out) 
   {    
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm() {");
      out.println("     ");
      out.println("     var rc = 1;");
      out.println("     if (rc) {");
      out.println("             if (confirm('Are you sure that you want to update the user(s) roles?')) {");
      out.println("                     document.forms[0].oper.value = 'UPDATE'");
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
      out.println("             if (confirm('Are you sure you want to delete the genotype?')) {");
      out.println("                     document.forms[0].oper.value='DELETE';");
      out.println("                     rc = 0;");
      out.println("             }");
      out.println("     ");
      out.println("     } else if ('UPDATE' == action.toUpperCase()) {");
      out.println("             if (confirm('Are you sure you want to update the genotype?')) {");
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
   
   private void writeNewRoleScript(PrintWriter out) 
   {
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
      out.println("             if (confirm('Are you sure that you want to create the role?')) {");
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

   private void writeRoleEditScript(PrintWriter out) 
   {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("     ");
      out.println("     var rc = 1;");
      out.println("     if ('DELETE' == action.toUpperCase()) {");
      out.println("             if (confirm('Are you sure you want to delete this role?')) {");
      out.println("                     document.forms[0].oper.value='DELETE';");
      out.println("                     rc = 0;");
      out.println("             }");
      out.println("     ");
      out.println("     } else if ('UPDATE' == action.toUpperCase()) {");
      out.println("   if (!('' + document.forms[0].n.value)=='') {");
      out.println("               if (confirm('Are you sure you want to update this role?')) {");
      out.println("                       document.forms[0].oper.value='UPDATE';");
      out.println("                       rc = 0;");
      out.println("               }");
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
    *
    * The Role page
    *
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
         String pid = (String) session.getValue("PID"); //req.getParameter("pid");
         String rname;
         String sql;
         out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out.println("<html>");
         out.println("<head>");
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
         out.println("<form method=post action=\"" + getServletPath("viewProj/roles?") +
                     oldQS + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         stmt_role = conn.createStatement();
         rset_role = stmt_role.executeQuery("SELECT RID, NAME, COMM FROM " +
                                            "v_roles_1 where pid=" + pid + " ORDER BY NAME");
         out.println("<table cellpading=0 cellspacing=0>");
         out.println("<tr bgcolor=\"#oo8B8B\">");
         out.println("<td width=200 style=\"WIDTH: 200px\">Role</td>");
         //out.println("<td width=200 style=\"WIDTH: 200px\">Privilege</td>");
         out.println("<td width=200 style=\"WIDTH: 200px\"></td>");
         out.println("<td width=300 style=\"WIDTH: 300px\">Comment</td>");
         out.println("<td width=50 style=\"WIDTH: 50px\">&nbsp;</td>");
         out.println("</tr>");

         // display the roles
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
            out.println("<tr bgcolor="+ bgcolor +"><td>");
            //out.println("<tr bgcolor=white><td>");
            out.println(formatOutput(session, rset_role.getString("NAME"), 21));
            out.println("</td><td>&nbsp;</td>");
            out.println("<td>" + formatOutput(session, rset_role.getString("COMM"), 30) + "</td>");
            out.println("<td><a href=\"" + getServletPath("viewProj/editRole?") +
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
         out.println("<input type=button value=Import width=100 " +
                     "style=\"WIDTH: 100px\" " +
                     "onClick='JavaScript:location.href=\"" + getServletPath("viewProj/impRole?") +
                     oldQS + "\";'>&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=\"Create new\" width=100 " +
                     "style=\"WIDTH: 100px\" " +
                     "onClick='JavaScript:location.href=\"" + getServletPath("viewProj/newRole?") +
                     oldQS + "\";'>&nbsp;");
         out.println("</td></tr></table>");
         out.println("</td></tr>");
         out.println("</table>");
         // Store some extra information needed by doPost()
         out.println("<input type=\"hidden\" NAME=oper value=\"\">");

         out.println("</td></tr></table>");

         out.println("</FORM>");
         out.println("</body>");
         out.println("</html>");
      } catch (Exception e)     {
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


   private void writeEditRole(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException 
   {
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
      throws ServletException, IOException 
   {
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

         out.println("<form method=post action=\"" + getServletPath("viewProj/editRole?") +
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
                     getServletPath("viewProj/roles?") + oldQS + "\"'>&nbsp;");
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

   
   /**
    * Writes the statistics page using the writeStatisticsPage in
    * ServletUtil. 
    *
    * @param request The request from client.
    * @param response The response to client.
    * @exception ServletException If error when handling request.
    * @exception IOException If I/O error when handling request.
    */
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
         String projectId = (String) session.getValue("PID");
         Connection connection =  (Connection) session.getValue("conn");
         
         // Get general project data 
         stmt = connection.createStatement();
         rset = stmt.executeQuery("SELECT NAME, COMM, STATUS FROM V_PROJECTS_1 " +
                                  "WHERE PID=" + projectId);
         
         // Check that project found, otherwise bail out
         Assertion.assertMsg(rset.next(), "Project not found!");

         // Copy general data to vector
         String[][] generalData = new String[2][2];
         generalData[0][0] = "Name";
         generalData[0][1] = rset.getString("NAME");
         generalData[1][0] = "Comment";
         generalData[1][1] = rset.getString("COMM");
         
         // Define vector for statisticData 
         String[][] statisticData = new String[9][2];
         String counter;

         // Query for all users of the project and add result to vector. If
         // no users found, add a blank string.
         rset = stmt.executeQuery("SELECT COUNT (*) FROM " +
                                  "V_USERS_2 WHERE PID=" + projectId); 
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[0][0] = "Users";
         statisticData[0][1] = counter;
         
         // Query for species in project
         rset = stmt.executeQuery("SELECT COUNT (*) FROM V_PROJECTS_2 WHERE " +
                                  "PID=" + projectId);
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[1][0] = "Species";
         statisticData[1][1] = counter;
         
         // Query for sampling units
         rset = stmt.executeQuery("SELECT COUNT (*) FROM V_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + projectId);
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[2][0] = "Sampling units";
         statisticData[2][1] = counter;
         
         // Query for individuals
         rset = stmt.executeQuery("SELECT sum(INDS) "
                                  + "FROM gdbadm.V_SAMPLING_UNITS_3 WHERE PID="
                                  + projectId );
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[3][0] = "Individuals";
         statisticData[3][1] = counter;
                  
         // Query for samples
         rset = stmt.executeQuery("SELECT count(*) "
                                  + "FROM gdbadm.V_SAMPLES_2 s, V_SAMPLING_UNITS_2 su"+
                                  " WHERE s.SUID=su.SUID AND su.PID=" + projectId + " ");
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[4][0] = "Samples";
         statisticData[4][1] = counter;

         // Query for variables
         rset = stmt.executeQuery("SELECT count(*) "
                                  + "FROM gdbadm.V_VARIABLES_3 WHERE PID="
                                  + projectId + " "); 
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[5][0] = "Variables";
         statisticData[5][1] = counter;

         // Query for phenotypes
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
         statisticData[6][0] = "Phenotypes";
         statisticData[6][1] = counter;

         // Query for markers
         rset = stmt.executeQuery("SELECT count(*) "
                                  + "FROM gdbadm.V_MARKERS_1 m, gdbadm.V_SAMPLING_UNITS_2 s "+
                                  "WHERE m.SUID=s.SUID AND s.PID=" +
                                  projectId + " ");
         counter = "";
         if (rset.next())
         {
            counter = Integer.toString(rset.getInt(1));
         }
         statisticData[7][0] = "Markers";
         statisticData[7][1] = counter;

         // Query for genotypes
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
         statisticData[8][0] = "Genotypes";
         statisticData[8][1] = counter;

         // Now write the page
         writeStatisticsPage(out, generalData, statisticData,  null);  
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
    * The session opptions page
    */
   private void writeOptions(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
      HttpSession session = req.getSession(true);
      String oper = req.getParameter("oper");
      if (oper == null) oper = "";
      if (oper.equals("UPDATE")) {
         String pid = (String) session.getValue("PID");
         if (updateSession(req, res)) {
            // We need to check if the project has changed. If so
            // we have to update the whole window, since the top frame
            // displays the name of the current project.
            if (!pid.equals((String) session.getValue("PID"))) {
               // Current project has changed!
               PrintWriter out = res.getWriter();
               res.setContentType("text/html");
               out.println("<html><script language=\"JavaScript\">");
               out.println("<!--");
               out.println("top.location=\"" + getServletPath("mainPage?page=SESSION") + "\"");
               out.println("// -->");
               out.println("</script></html>");
            } else {
               writeOptionsPage(req, res);
            }
         }
      } else {
         writeOptionsPage(req, res);
      }
   }
   private void writeOptionsPage(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
      HttpSession session = req.getSession(true);
      Connection conn;
      Statement stmt = null;
      ResultSet rset = null;
      String maxRows, nullReplacement, dateFormat, pid, userID;
      userID = (String) session.getValue("UserID");
      pid = (String) session.getValue("PID");
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = null;
      try {
         conn = (Connection) session.getValue("conn");
         out = res.getWriter();
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Projects Options</title>");
         // Validation script
         out.println("<script language=\"JavaScript\">");
         out.println("<!--");
         out.println("function isNumeric(val) {");
         out.println("  var numeric = 1;");
         out.println("  var ch;");
         out.println("  for (i=0; i < ('' + val).length && i<3; i++) {");
         out.println("    ch = val.substr(i, 1);");
         out.println("    if (!(ch == '0' || ch == '1' ||");
         out.println("          ch == '2' || ch == '3' ||");
         out.println("          ch == '4' || ch == '5' ||");
         out.println("          ch == '6' || ch == '7' ||");
         out.println("          ch == '8' || ch == '9') ) {");
         out.println("      numeric = 0;");
         out.println("    }");
         out.println("  }");
         out.println("  if (numeric) {");
         out.println("    if (0 == val || 100 < val)");
         out.println("      numeric = 0;");
         out.println("  }");
         out.println("");
         out.println("  return numeric;");
         out.println("}");
         out.println("");
         out.println("");
         out.println("");
         out.println("// -->");
         out.println("</script>");
         out.println("<script language=\"JavaScript\">");
         out.println("<!--");
         out.println("function valForm() {");
         out.println("  var rc = 1;");
         out.println("  if (!isNumeric(document.forms[0].mnr.value)) {");
         out.println("    rc = 0;");
         out.println("    alert('Number of rows must be in the range 1 to 99');");
         out.println("  } else {");
         out.println("    ; //alert('Not numeric!');");
         out.println("  }");
         out.println("");
         out.println("  if (rc) {");
         out.println("    document.forms[0].oper.value='UPDATE';");
         out.println("    document.forms[0].submit();");
         out.println("  }");
         out.println("");
         out.println("");
         out.println("");
         out.println("}");
         out.println("// -->");
         out.println("</script>");
         out.println("</head>");
         out.println("<body>");

         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Projects - Set</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<FORM method=get action=\"" + getServletPath("viewProj/options") + "\">");
         out.println("<table>");
         // Project
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT PID, PNAME FROM gdbadm.V_USERS_2 WHERE ID=" + userID
                                  + " AND PID IN(SELECT PID from gdbadm.V_PROJECTS_1 WHERE STATUS='E') order by PNAME");
         out.println("<tr>");
         out.println("<td>Project<br>");
         out.println("<select name=pid width=200 style=\"WIDTH: 200px\">");
         while(rset.next()) {
            out.println("<option " + (pid.equals(rset.getString("PID")) ? "selected" : "" ) +
                        " value=\"" + rset.getString("PID") + "\">" +
                        rset.getString("PNAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td></tr>");
         // Null replacement
         out.println("<td>Null replacement<br>");
         out.println("<input type=text name=nr width=200 style=\"WIDTH: 200px\" " +
                     "value=\"" + getNullReplacement(session) + "\">");
         out.println("</td></tr>");
         // Date representation
         out.println("<tr>");
         out.println("<td>Date representation<br>");
         out.println("<select name=dr width=200 style=\"WIDTH: 200px\">");
         dateFormat = getDateFormat(session);
         if (dateFormat.equals("YYMMDD")) {
            out.println("<option selected value=\"YYMMDD\">YYMMDD</option>");
            out.println("<option value=\"MMDDYY\">MMDDYY</option>");
            out.println("<option value=\"YYYY-MM-DD HH24:MI\">YYYY-MM-DD HH24:MI</option>");
         } else if (dateFormat.equals("MMDDYY")) {
            out.println("<option value=\"YYMMDD\">YYMMDD</option>");
            out.println("<option selected value=\"MMDDYY\">MMDDYY</option>");
            out.println("<option value=\"YYYY-MM-DD HH24:MI\">YYYY-MM-DD HH24:MI</option>");
         } else if (dateFormat.equals("YYYY-MM-DD HH24:MI")) {
            out.println("<option value=\"YYMMDD\">YYMMDD</option>");
            out.println("<option value=\"MMDDYY\">MMDDYY</option>");
            out.println("<option selected value=\"YYYY-MM-DD HH24:MI\">YYYY-MM-DD HH24:MI</option>");
         } else {
            out.println("<option value=\"YYMMDD\">YYMMDD</option>");
            out.println("<option value=\"MMDDYY\">MMDDYY</option>");
            out.println("<option value=\"YYYY-MM-DD HH24:MI\">YYYY-MM-DD HH24:MI</option>");
         }
         out.println("</select>");
         out.println("</td></tr>");
         // Number of rows to display in each page
         out.println("<tr>");
         out.println("<td>Maximum number of rows to display<br>");
         out.println("<input type=text name=mnr width=200 style=\"WIDTH: 200px\" " +
                     "value=\"" + getMaxRows(session) + "\">");
         out.println("</td></tr>");

         out.println("<tr><td>&nbsp;</td></tr>");
         // Some buttons
         out.println("<tr>");
         out.println("<td>");
         out.println("<table>");
         out.println("<tr>");
         out.println("<td><input type=reset value=Reset width=100 style=\"WIDTH: 100px\">&nbsp</td>");
         out.println("<td><input type=button width=100 style=\"WIDTH: 100px\" " +
                     "onClick='valForm();' value=\"Update\">&nbsp;</td>");
         out.println("</tr></table>");

         out.println("</td></tr>");
         out.println("</table>");
         // Store some extra information
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
         } catch (SQLException ignored) {}
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
      String errorQueryString = buildQS(request);
      return removeQSParameterOper(errorQueryString);
   }



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
         pid = (String) session.getValue("PID");
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
                     getServletPath("viewProj/newRole?") + newQS + "\">");
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
                     getServletPath("viewProj/roles?") + newQS + "\"'>");
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
    * Creates the page used to import roles
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

         out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                     "Transitional//EN\"");
         out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");
         
         out.println("<html>");
         out.println("<head>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         
         // Validation script
         out.println("<script type=\"text/javascript\">");
         out.println("<!--");
         out.println("function valForm() {");
         out.println("  ");
         out.println("  var rc = 1;");
         out.println("  if ( (\"\" + document.forms[0].c.value) != \"\" &&");
         out.println("       document.forms[0].c.value.length > 255) {");
         out.println("                  alert('Comment must be less than 255 characters!');");
         out.println("                  rc = 0;");
         out.println("  }");
         out.println("  if ( (\"\" + document.forms[0].n.value) == \"\") {");
         out.println("                  rc = 0;");
         out.println("  }");
         out.println("  ");
         out.println("  ");
         out.println("  if (rc) {");
         out.println("          if (confirm('Are you sure that you want to create the role?')) {");
         out.println("                  document.forms[0].oper.value = 'UPLOAD';");
         out.println("                  document.forms[0].submit();");
         out.println("          }");
         out.println("  }");
         out.println("  ");
         out.println("  ");
         out.println("}");
         out.println("//-->");
         out.println("</script>");
         out.println("<title>New Role</title>");
         out.println("</head>");

         out.println("<body>");

         // Header table
         out.println("\n<!--  The header table -->");
         out.println("<table width=846 border=0>");
         out.println("<tr>\n" +
                     "  <td width=14 rowspan=3></td>\n" +
                     "  <td width=736 colspan=2 height=15> " + 
                     "<center><b style=\"font-size: 15pt \">" +
                     "Projects - Roles - File import</b></center></td>\n" +
                     "</tr>");
         
         out.println("<tr>\n" +
                     "  <td width=736 colspan=2 height=2 " +
                     "bgcolor=\"#008B8B\">&nbsp;</td>\n" +
                     "</tr>\n</table>");

         out.println("<form method=post enctype=\"multipart/form-data\" action=\"" +
                     getServletPath("viewProj/impRoleMultipart") + "\">");

         // Main table
         out.println("\n<!-- The main table -->");
         out.println("<table border=0>");

         // First row is an empty column + the name of the role to create 
         out.println("<tr>\n" +
                     "  <td width=10 ></td>\n" +
                     "  <td>Name<br><input name=n maxlength=20 " +
                     "style=\"WIDTH: 200px; HEIGHT: 22px\"></td>\n" +
                     "</tr>");

         // Second row is an empty column + the comment field
         out.println("<tr>\n" +
                     "  <td></td>\n" +
                     "  <td>Comment<br><textarea rows=10 cols=40 name=c> " +
                     "</textarea></td>\n" +
                     "</tr>");
         
         // Third row is an empty column + the file import field
         out.println("<tr>\n" +
                     "  <td></td>\n" +
                     "  <td>File<br><input type=file name=filename " +
                     "style=\"WIDTH: 350px\"></td>\n" +
                     "</tr>");

         // Fourth row in an emtpy colum + an emtpy column
         out.println("<tr>\n" +
                     "  <td></td>\n" +
                     "  <td></td>\n" +
                     "</tr>");

         // Fifth row is an empty colum + a table with buttons
         out.println("<tr>\n" +
                     "  <td></td>\n" +
                     "  <td>");
         out.println("    <table border=0>\n" +
                     "    <tr>\n");
         // First column in the table is the "Back" button
         out.println("      <td><input type=button value=Back " +
                     "style=\"WIDTH: 100px\" onClick='document.location.href=\"" +
                     getServletPath("viewProj/roles") + "\"'>\n" +
                     "&nbsp;</td>");

         // Second column in the table is the "Send" button
         out.println("      <td><input type=button value=Send " +
                     "style=\"WIDTH: 100px\" onClick='valForm()'>" +
                     "&nbsp;</td>\n" +
                     "    </tr>\n" +
                     "    </table>");
         out.println("  </td>\n" +
                     "</tr>\n" +
                     "</table>");
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
         String pid = (String) session.getValue("PID");
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
         out.println("<form method=post action=\"" + getServletPath("viewProj/users?") +
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
         sql = "SELECT NAME, ID, RID FROM V_USERS_2 WHERE " +
            "PID=" + pid + " ORDER BY NAME";
         
         Errors.logDebug(sql);
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
         rset_user = stmt_user.executeQuery("SELECT NAME, ID FROM V_USERS_1 " +
                 "where status='E' and id not in " +
                 "(SELECT ID FROM V_USERS_2 WHERE PID=" + pid +")");
         
         out.println("<tr><td>");
         out.println("<select name=add_us width=200 " +
                     "style=\"WIDTH: 200px\">");
         out.println("<option value=\"\" selected>(New user)</option>");
         while (rset_user.next()) {
            out.println("<option value=\"" + rset_user.getString("ID") + "\">" +
                        formatOutput(session, rset_user.getString("NAME"), 15) + "</option>");
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


   /**
    * Creates a new role.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if role created.
    *         False if role not created.
    */
   private boolean createRole(HttpServletRequest request,
                              HttpServletResponse response,
                              Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         String name, comm, pid;
         connection.setAutoCommit(false);
         name = request.getParameter("n");
         comm = request.getParameter("c");
         pid = (String) session.getValue("PID");
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

      commitOrRollback(connection, request, response, "Roles.New.Create",
                       errMessage, "viewProj/newRole", isOk);
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
         // Create a session and get the connection from it
         HttpSession session = request.getSession(true);
         connection = (Connection) session.getValue("conn");
         connection.setAutoCommit(false);

         String upPath = getUpFilePath();
         MultipartRequest multiRequest =
            new MultipartRequest(request, upPath, 5 * 1024 * 1024);

         // Get parameters from the request
         String roleName, comment, projectId;
         roleName = multiRequest.getParameter("n");
         comment = multiRequest.getParameter("c");
         projectId = (String) session.getValue("PID");

         // Create a new role. 
         DbRole dbRole = new DbRole();
         dbRole.CreateRole(connection, Integer.parseInt(projectId), roleName, comment);
         errMessage = dbRole.getErrorMessage();

         // Ensure no error message was created during role creation
         Assertion.assertMsg(errMessage == null ||
                           errMessage.trim().equals(""),
                          "Role creation caused error: " + errMessage); 
         
         // Find the new role's id
         sqlStatement = connection.createStatement();
         resultSet = sqlStatement.executeQuery("SELECT RID FROM V_ROLES_1 WHERE NAME='" +
                                               roleName + "' AND PID=" + projectId);

         // Ensure there is a new role and get its id
         Assertion.assertMsg(resultSet.next(), "New role was not created.");
         int roleId = resultSet.getInt("RID");


         FileParser fileParser = null;
         Enumeration fileEnum = null;
         String givenFileName;  // The file name retrieved from the request
                                // object 
         String systemFileName; // The file name converted to the current
                                // file system
         
         // Get all the found file names
         fileEnum = multiRequest.getFileNames();
         if (fileEnum.hasMoreElements())
         {
            // Get the file name and convert it
            givenFileName = (String) fileEnum.nextElement();
            systemFileName = multiRequest.getFilesystemName(givenFileName);
            
            // Create the parser and parse the file
            fileParser = new FileParser(upPath + "/" +  systemFileName);  
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.ROLE,
                                                                        FileTypeDefinition.LIST));
            
            // Add privileges to the role and ensure no error was generated
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

      // if commit/rollback ok and database operation ok, write the role
      // page. 
      if (commitOrRollback(connection, request, response,
                           "Roles.Import.Send", errMessage,
                           "viewProj/impRole", isOk)
          && isOk)
      {
         writeRole(request, response);
      }
      return isOk;
   }

   
   /**
    * Updates the roles of a user.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if user updated.
    *         False if user not updated.
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
         pid = Integer.parseInt((String) session.getValue("PID"));
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

      commitOrRollback(connection, request, response, "Users.Update",
                       errMessage, "viewProj/users", isOk);
      return isOk;
   }
   

   /**
    * Updates a role.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if role updated.
    *         False if role not updated.
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
         int rid;
         String prid;
         String oldQS = request.getQueryString();
         //      pid = (String) session.getValue("PID");
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
      
      commitOrRollback(connection, request, response, "Roles.Edit.Update",
                       errMessage, "viewProj/roles", isOk);
      return isOk;
   }


   /**
    * Deletes a role.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if role deleted.
    *         False if role not deleted.
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

      commitOrRollback(connection, request, response, "Roles.Edit.Delete",
                       errMessage, "viewProj/editRole", isOk);
      return isOk;
   }


   private void updateProjectSettings(HttpServletRequest req, HttpServletResponse  res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      String pid, id, pname, rname, rid;
      int privileges[] = null;
      int index;
      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null;
      pid = req.getParameter("pid");
      id = (String) session.getValue("UserID");
      try {
         conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT RNAME, PNAME FROM V_ENABLED_USERS_2 WHERE " +
                                  "PID=" + pid + " AND ID=" + id);
         if (rset.next()) {
            session.putValue("PID", pid);
            session.putValue("PNAME", rset.getString("PNAME"));
            session.putValue("ROLE", rset.getString("RNAME"));

            // read and store the privileges associated with this role in the
            // session object privileges.
            rset.close();
            stmt.close();
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT COUNT(*) FROM V_USER_PRIV " +
                                     "WHERE PID=" +pid + " AND ID=" +id);
            if(rset.next())
               privileges = new int[rset.getInt(1)];
            else
               throw new Exception("");

            rset.close();
            stmt.close();

            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT RNAME, PRID FROM V_USER_PRIV " +
                                     "WHERE PID=" +pid+" AND ID=" +id);
            index = 0;
            while (rset.next())
            {
               privileges[index]=rset.getInt("PRID");
               index++;
            }
            session.putValue("PRIVILEGES",privileges);
            rset.close();
            stmt.close();

         } else {
            Boolean bLoginOk = new Boolean(false);
            session.putValue("LoginOk", bLoginOk);
            throw new Exception("");
         }
      } catch (Exception e) {
         e.printStackTrace(System.err);
         redirect(res);
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
   }
   
   /** Write the page for changing the password.
    * @param req The request object
    * @param res The response object
    *
    * servlet url: /myaccount
    */
   private void writeMyAccount(HttpServletRequest req,HttpServletResponse res) 
   {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      
      PrintWriter out = null;
      
      try {
         out = res.getWriter();
         
         out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out.println("<html>");
         out.println("<head>");
         out.println("<title>My Account</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Projects - My Account</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         
         out.println("<table><tr><td>");
         out.println("<p>The password should be both characters and numbers combined, at least of lenght 5. Dictionary words are always bad to use in passwords.</p>");
         out.println("<form method=post action=\"" + getServletPath("viewProj/changepwd") + "\">");         
         out.println("Passwd1 : <input type=\"password\" name=\"passwd1\"><br>");
         out.println("Passwd2 : <input type=\"password\" name=\"passwd2\"><br><br>");
         out.println("<input type=\"submit\" name=\"submit\" value=\"Change Password\"><br>");
         out.println("</td></tr></table>");
         
         
         
         out.println("</form>");
         out.println("</body>");
         out.println("</html>");
      }
      catch (Exception e)
      {}
   }
   
   /** Receive the request to change password. This method calls the DbUser
    * object to change the password.
    *
    * All actions performed to this function is logged. UserID, Username and
    * Fullname is not changeable in this method, the values is picked from
    * the session variables. The user can not tamper these values (right?)
    * @param req The Request object in use
    * @param res The response object
    *
    * Servlet url: /changepwd
    */
   private void changePwd(HttpServletRequest req,HttpServletResponse res)
   {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      
      PrintWriter out = null;
      
      Errors.log("changePWD");
      String user = "";
      String UserName = "";
      String pw1 = "";
      String pw2 = "";
      String sign = "";
      
      try 
      {
          out = res.getWriter();
          
          user = (String)session.getAttribute("UserID");
          sign = (String)session.getAttribute("UserSign");
          pw1 = req.getParameter("passwd1");
          pw2 = req.getParameter("passwd2");
          
          UserName = (String)session.getAttribute("UserName");
          
          if (pw1.length()<=4)
              throw new Exception("Password to short. Password must have more than 4 characters");
          
          if (pw1.equals(pw2))
          {
              Errors.log("User ["+sign+"] changed password.");
              
              Connection conn = (Connection) session.getAttribute("conn");
              conn.setAutoCommit(false);
              
              DbUser usr = new DbUser();
              usr.UpdateUser(conn, Integer.parseInt(user), UserName,
                         sign, pw1, "E");
              
              conn.commit();
                           
                out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>My Account</title>");
                HTMLWriter.css(out,getURL("style/axDefault.css"));
                out.println("</head>");
                out.println("<body>");

                out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                         "<tr>" +
                         "<td width=\"14\" rowspan=\"3\"></td>" +
                         "<td width=\"736\" colspan=\"2\" height=\"15\">");
                out.println("<center>" +
                         "<b style=\"font-size: 15pt\">Projects - My Account</b></center>" +
                         "</font></td></tr>" +
                         "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                         "</tr></table>");


                out.println("<table><tr><td>");
                out.println("<p></p><p>The password was changed</p>");
                out.println("</td></tr></table>");



                out.println("</form>");
                out.println("</body>");
                out.println("</html>");
          }
          else
              throw new Exception("Password mismatch");                         
      }
      catch (Exception e)
      {
          Errors.log("Password not changed: " + e.getMessage());
          try
          {
            writeErrorPage(req,res,"Password not changed",e.getMessage());
          }
          catch (Exception e2)
          {}
      }
   }
}
