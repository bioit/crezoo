/*
  $Log$
  Revision 1.8  2005/03/04 15:36:15  heto
  Converting for using PostgreSQL

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

  Revision 1.3  2002/10/22 06:08:12  heto
  rebuilt the "back-buttons".
  Dont save the request object, save the URL instead.
  New function.

  Revision 1.2  2002/10/18 11:41:10  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:05  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.11  2002/01/30 19:33:03  roca
  Fixes of the inheritance check pages

  Revision 1.10  2001/05/31 07:07:06  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.9  2001/05/29 13:03:16  frob
  Removed old construction with getStatus in viewPheno. Added keys in Errors.properties.

  Revision 1.8  2001/05/22 06:16:59  roca
  Backfuncktionality fixed for all (?) servlets/subpages

  Revision 1.7  2001/05/15 13:36:26  roca
  After merge problems from last checkin..

  Revision 1.6  2001/05/11 12:17:32  frob
  Changed all methods that used to call writeError() to write an error page. These
  methods now calls the general method commitOrRollback() which does the commit or
  rollback operation as well as handle any errors. writeError() was removed.

  Revision 1.5  2001/05/03 07:57:43  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.4  2001/05/03 06:01:32  frob
  Calls to removeOper changed to use the general removeQSParameter.
  The previously called method is removed.

  Revision 1.3  2001/04/24 09:33:58  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:30  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.5  2001/04/19 10:57:02  frob
  Changed call readFileHeader() => scanFileHeader()

  Revision 1.1.1.1.2.4  2001/04/18 09:26:48  frob
  Removed the size of the main table used on the webpages.

  Revision 1.1.1.1.2.3  2001/04/12 08:46:59  frob
  createFile: Changed call to Parse() to pass valid file type definitions.
              No longer uses the delimiter from the request object.
              No longer uses the format from the request object.
              Pre-reads the format from the file and takes appropriate action.
  writeImpFilePage: No longer displays the delimiter field.
                    No longer displays the format field.
                    File name field resized.
                    HTML validated.

  Revision 1.1.1.1.2.2  2001/03/28 13:47:57  frob
  Added catch() for InputDataFileException which can be
  raised from the parse()-method.
  Added check that the connection object is created before doing
  rollback on it (to avoid null pointer exception).

  Revision 1.1.1.1.2.1  2001/03/28 12:37:49  frob
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

public class viewPheno extends SecureArexisServlet
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
      } else if (extPath.equals("/completion")) {
         writeCompletion(req, res);
      } else if (extPath.equals("/impFile")) {
         writeImpFile(req, res);
      } else if (extPath.equals("/impMultipart")) {
         createFile(req, res);
      }
   }

   
   /**
    * Creates or updates phenotypes from file.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if everything was ok.
    *         False if any errors.
    * @exception IOException If writing any page fails.
    * @exception ServletException If writing any page fails.
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
            new MultipartRequest(request, upPath, 6 * 1024 * 1024);

         // Get parameters from request
         int samplingUnitId = Integer.parseInt(multiRequest.getParameter("suid"));
         int userId = Integer.parseInt((String) session.getValue("UserID"));
         String uploadMode = multiRequest.getParameter("type");

         Enumeration files = multiRequest.getFileNames();
         if (files.hasMoreElements())
         {
            String givenFileName = (String) files.nextElement();
            String systemFileName = multiRequest.getFilesystemName(givenFileName);

            DbPhenotype dbp = new DbPhenotype();

            FileParser fileParser = new FileParser(upPath + "/" +  systemFileName);
            FileHeader header = FileParser.scanFileHeader(upPath + "/" +
                                                          systemFileName);
            
            // Ensure file format is list or matrix
            Assertion.assertMsg(header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.LIST) ||
                             header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.MATRIX),
                             "Format type name should be list or matrix " +
                             "but found found " + header.formatTypeName());

            // If file is a list
            if (header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.LIST))
            {
               fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.PHENOTYPE,
                                                                           FileTypeDefinition.LIST));
               if (uploadMode.equals("CREATE"))
               {
                  dbp.CreatePhenotypesList(fileParser, connection,
                                           samplingUnitId, userId);
               }
               else if (uploadMode.equals("UPDATE"))
               {
                  dbp.UpdatePhenotypesList(fileParser, connection,
                                           samplingUnitId, userId); 
               }
               else if (uploadMode.equals("CREATE_OR_UPDATE"))
               {
                  dbp.CreateOrUpdatePhenotypesList(fileParser, connection,
                                                   samplingUnitId, userId); 
               }
            }

            // If file is a matrix
            else if (header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.MATRIX))
            {
               fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.PHENOTYPE,
                                                                           FileTypeDefinition.MATRIX));
               if (uploadMode.equals("CREATE"))
               {
                  dbp.CreatePhenotypesMatrix(fileParser, connection,
                                             samplingUnitId, userId); 
               }
               else if (uploadMode.equals("UPDATE"))
               {
                  dbp.UpdatePhenotypesMatrix(fileParser, connection,
                                             samplingUnitId, userId); 
               }
               else if (uploadMode.equals("CREATE_OR_UPDATE"))
               {
                  dbp.CreateOrUpdatePhenotypesMatrix(fileParser,
                                                     connection,
                                                     samplingUnitId,
                                                     userId); 
               }
            }
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

      // if commit/rollback ok and database operation ok, write the frame 
      if (commitOrRollback(connection, request, response,
                           "Phenotypes.Import.Send", errMessage,
                           "viewPheno/impFile", isOk)
          && isOk)
      {
         writeFrame(request, response);
      }
      return isOk;
   }


   private void writeFrame(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException 
   {
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
         //req = getServletState(req,session);
         
         String topQS = buildQS(req);
         // we don't want the oper parameter anaywhere but in the "edit" and "new" page!
         topQS = removeQSParameterOper(topQS);
         String bottomQS = topQS.toString();
         String middleQS = topQS.toString();
         out.println("<html>"



                     + "<HEAD>"
                     + " <TITLE>View phenotypes</TITLE>"
                     + "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">"
                     + "</HEAD>"
                     + "<frameset rows=\"185,35,*\" framespacing=\"0\" border=\"true\">" //
                     + "<frame name=\"viewphenotop\" "
                     + "src=\"" + getServletPath("viewPheno/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"
                     + "<frame name=\"viewphenomiddle\" "
                     + "src=\"" + getServletPath("viewPheno/middle?") + middleQS + "\" "
                     + "scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"\">"
                     + "</frame>\n"
                     + "<frame name=\"viewphenobottom\""
                     + "src=\"" + getServletPath("viewPheno/bottom?") + bottomQS + "\" "
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
         suid = null,  // Sampling unit id
         old_suid = null, // Previous sampling unit id
         identity = null, // individual identity
         variable = null,
         orderby = null;
      String pid = (String) session.getValue("PID");
      old_suid = (String) session.getValue("SUID");
      suid = req.getParameter("suid");
      if (suid != null) {
         session.putValue("SUID", new String(suid));
      } else {
         suid = old_suid;
      }
      if (suid == null) {
         suid = findSuid(conn, pid);
      }
      session.putValue("SUID", suid);
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
      identity = req.getParameter("identity");
      if (identity != null)
         output.append("&identity=").append(identity);
      variable = req.getParameter("variable");
      if (variable != null)
         output.append("&variable=").append(variable);
      // Set the parameters STARTINDEX and ROWS
      if (!action.equals("NOP"))
         output.append(setIndecis(suid, old_suid, action, req, session));
      output.append("&suid=").append(suid);

      if (req.getParameter("oper") != null)
         output.append("&oper=").append(req.getParameter("oper"));
      if (req.getParameter("new_name") != null)
         output.append("&new_name=").append(req.getParameter("new_name"));

      orderby = req.getParameter("ORDERBY");
      if (orderby != null)
         output.append("&ORDERBY=").append(orderby);
      else
         output.append("&ORDERBY=IDENTITY");

      return output.toString().replace('%', '*');
   }
   private String findSuid(Connection conn, String pid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 WHERE PID=" +
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
   private String findVid(Connection conn, String suid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT VID FROM gdbadm.V_VARIABLES_1 WHERE SUID=" +
                                  suid + " ORDER BY NAME");
         if (rset.next()) {
            ret = rset.getString("VID");
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
   private String setIndecis(String suid, String old_suid, String action, HttpServletRequest req, HttpSession session) {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(suid, req, session);
      maxRows = getMaxRows(session);// Integer.parseInt( (String) session.getValue("MaxRows"));
      if (req.getParameter("STARTINDEX") != null && 
          old_suid != null && suid != null &&
          old_suid.equalsIgnoreCase(suid) ) {
         startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         if (rows > 0 && startIndex == 0) startIndex = 1;
      }
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
      String pid = (String) session.getValue("PID");
      try {
         sbSQL.append("SELECT count(*) "
                      + "FROM gdbadm.V_PHENOTYPES_2 WHERE SUID=" + suid + " AND PID="+pid +" ");
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
      String identity = null,
         variable = null,
         orderby = null;        

      StringBuffer filter = new StringBuffer(256);
      identity = req.getParameter("identity");
      variable = req.getParameter("variable");
      orderby = req.getParameter("ORDERBY");
                
      if (identity != null && !"".equalsIgnoreCase(identity)) 
         filter.append("and IDENTITY like '" + identity + "'");
      if (variable != null && !"".equalsIgnoreCase(variable))
         filter.append(" and NAME like'" + variable + "'");
      
      if (order)
      {
          if (orderby != null && !"".equalsIgnoreCase(orderby))
             filter.append(" order by " + orderby);
          else
             filter.append(" order by IDENTITY");
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
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
      String oper;
      oper = req.getParameter("oper");
      if (oper == null || "".equals(oper))
         oper = "SELECT";

      HttpSession session = req.getSession(true);
      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null;
      int startIndex = 0, rows = 0, maxRows = 0;
      String suid, vid, variable, identity, orderby, oldQS, newQS, action, pid;
      try {
         conn = (Connection) session.getValue("conn");
         int currentPrivs[] = (int [])session.getValue("PRIVILEGES");
         pid = (String) session.getValue("PID");
         suid = req.getParameter("suid");
         maxRows = getMaxRows(session);
         action = req.getParameter("ACTION");
         oldQS = req.getQueryString();
         newQS = buildTopQS(oldQS);
         orderby = req.getParameter("ORDERBY");
         identity = req.getParameter("identity");
         variable = req.getParameter("variable");
         if (req.getParameter("STARTINDEX") != null)
            startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         else
            startIndex = 0;
         if (req.getParameter("ROWS") != null)
            rows = Integer.parseInt(req.getParameter("ROWS"));
         else
            rows = 0;
         if (identity == null) identity = "";
         if (variable == null) variable = "";
         if (orderby == null) orderby = "NAME";
         if (action == null) action = "NOP";
         if (pid == null || "".equalsIgnoreCase(pid))
            pid = "-1";
         if (suid == null) suid = findSuid(conn, pid);

         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""
                     +getURL("style/view.css") + "\">");
         out.println("<base target=\"content\">");

         writeTopScript(out);
         out.println("<title>View phenotypes</title>");
         out.println("</head>");
         out.println("<body>");

         out.println("<body bgcolor=\"#ffffd0\">"
                     +"<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
                     +"<tr>"
                     + "<td width=\"14\" rowspan=\"3\">"+"</td>"
                     +"<td width=\"736\" colspan=\"2\" height=\"15\">"
                     +"<form method=get action=\"" +getServletPath("viewPheno") +"\">"
                     +"<p align=\"center\"><font size=\"2\"><b  style=\"font-size: 15pt\">Phenotypes</b>"
                     +"</font></td></tr>"
                     +"<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>"
                     +"</tr><tr><td width=\"517\">");



         out.println("<table width=488 height=\"92\">"
                     +"<td><b>Sampling unit</b><br><select name=suid "
                     +"name=select onChange='document.forms[0].submit()'  style=\"HEIGHT: 22px; WIDTH: 126px\">");


         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SUID FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID="+ pid + " ORDER BY NAME");
         while (rset.next()) {
            if (suid != null && suid.equalsIgnoreCase(rset.getString("SUID")))
               out.println("<OPTION selected value=\"" + rset.getString("SUID") + "\">" +
                           rset.getString("NAME")+ "</option>\n");
            else
               out.println("<OPTION value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME")+"</option>\n");
         }

         out.println("<td><b>Identity</b><br>"
                     + "<input id=identity name=identity value=\"" +identity+"\" style=\"HEIGHT: 22px; WIDTH: 127px\" size=\"12\"></td>");


         out.println("<td><b>Variable</b><br>"
                     + "<input id=variable name=variable value=\"" +variable+"\" style=\"HEIGHT: 22px; WIDTH: 127px\" size=\"12\"></td>");

         out.println("</td></table></td>");



         // buttons

         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");

         out.println(privDependentString(currentPrivs,PHENO_W,
                                         /*if true*/"<input type=button value=\"New Phenotype\""
                                         + " onClick='parent.location.href=\"" +getServletPath("viewPheno/new?") + newQS + "\"' "
                                         +"height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">"
                                         +"</td>",
                                         /*if false*/"<input type=button disabled value=\"New Phenotype\""
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
         out.println("<base target=\"content\">");
         out.println("</head><body>");

         /*
           if(action != null)
           {
           //        out.println(buildInfoLine(action, startIndex, rows, maxRows));

           out.println("<tr>"
           + "<td width=\"750\" colspan=\"3\">"
           + "<p align=left>&nbsp;&nbsp;"
           + buildInfoLine(action, startIndex, rows, maxRows)
           + "</td>"
           + "</tr>");
           }
         */
         if(action != null) {
            out.println("&nbsp;" +buildInfoLine(action, startIndex, rows, maxRows) );
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
           + "<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 height=20 width=800 style=\"margin-left:2px\">"
           + "<td width=5></td>");
         */


         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=800 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");




         // the menu choices
         //Sampling Unit
         //  out.println("<td width=100><a href=\"" + getServletPath("viewPheno")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=SUNAME\">");
         // if(choosen.equals("SUNAME"))
         //  out.println("<FONT color=saddlebrown ><b>Sampling Unit</b></FONT></a></td>\n");
         //  else out.println("Sampling Unit</a></td>\n");

         //Identity
         out.println("<td width=100><a href=\"" + getServletPath("viewPheno")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=IDENTITY\">");
         if(choosen.equals("IDENTITY"))
            out.println("<FONT color=saddlebrown><b>Identity</b></FONT></a></td>\n");
         else out.println("Identity</a></td>\n");

         //Variable
         out.println("<td width=180><a href=\"" + getServletPath("viewPheno")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\">");
         if(choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown><b>Variable</b></FONT></a></td>\n");
         else out.println("Variable</a></td>\n");

         //Value
         out.println("<td width=50><a href=\"" + getServletPath("viewPheno")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=VALUE\">");
         if(choosen.equals("VALUE"))
            out.println("<FONT color=saddlebrown><b>Value</b></FONT></a></td>\n");
         else out.println("Value</a></td>\n");

         //Value
         out.println("<td width=100><a href=\"" + getServletPath("viewPheno")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=REFERENCE\">");
         if(choosen.equals("REFERENCE"))
            out.println("<FONT color=saddlebrown><b>Reference</b></FONT></a></td>\n");
         else out.println("Reference</a></td>\n");

         //USER
         out.println("<td width=100><a href=\"" + getServletPath("viewPheno")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
         else out.println("User</a></td>\n");

         //Updated
         out.println("<td width=170><a href=\"" + getServletPath("viewPheno")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TS\">");
         if(choosen.equals("TS"))
            out.println("<FONT color=saddlebrown><b>Updated</b></FONT></a></td>\n");
         else out.println("Updated</a></td>\n");


         out.println("<td width=50>&nbsp;</td>");
         out.println("<td width=50>&nbsp;</td>");
         out.println("</table></table>");
         out.println("</body></html>");
         /*
           out.println("<td width=50>&nbsp;</td>"
           + "<td width=50>&nbsp;</td>"
           + "</table>"
           + "</td>"
           + "</tr>"
           + "</table>"
           + "</body></html>");
         */

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
      try
      {
         String suid = null, action = null, pid =null;
         String oldQS = req.getQueryString();
         action = req.getParameter("ACTION");
         suid = req.getParameter("suid");
         pid = (String) session.getValue("PID");
         int currentPrivs[] = (int [])session.getValue("PRIVILEGES");
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
         sbSQL.append("SELECT SUNAME, IDENTITY, NAME, REFERENCE, " +
                      "VALUE, " +
                      "USR, to_char(TS, '" + getDateFormat(session) + "') as TC_TS, VID, IID " +
                      "FROM gdbadm.V_PHENOTYPES_2 WHERE SUID=" + suid + " AND PID="+pid+" ");

         String qs = req.getQueryString();
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);
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
            out.println("<TD width=5></td>");
                                //out.println("<TD width=100>" + formatOutput(session,rset.getString("SUNAME"),12) + "</TD>");
            out.println("<TD width=100>" + formatOutput(session,rset.getString("IDENTITY"),9) + "</TD>");
            out.println("<TD width=180>" + formatOutput(session,rset.getString("NAME"),16) + "</TD>");
            out.println("<TD width=50>" + formatOutput(session,rset.getString("VALUE"),5) + "</TD>");
            out.println("<TD width=100>" + formatOutput(session,rset.getString("REFERENCE"),8) + "</TD>");
            out.println("<TD width=100>" + formatOutput(session,rset.getString("USR"),9) + "</TD>");
            out.println("<TD width=170>" + formatOutput(session,rset.getString("TC_TS"),16) + "</TD>");
            out.println("<TD width=50><A HREF=\""+getServletPath("viewPheno/details?iid=")
                        + rset.getString("IID")
                        + "&vid=" + rset.getString("VID")
                        + "&" + oldQS + "\" target=\"content\">Details</A></TD>");


            out.println("<TD width=50>");
            out.println(privDependentString(currentPrivs,PHENO_W,
                                            /*if true*/"<A HREF=\""+getServletPath("viewPheno/edit?iid=")
                                            + rset.getString("IID")
                                            + "&vid=" + rset.getString("VID")
                                            + "&" + oldQS + "\" target=\"content\">Edit</A></TD></TR>",
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
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
      Connection conn = null;
      Statement stmt_curr = null, stmt_hist = null;
      ResultSet rset_curr = null, rset_hist = null;
      String prev_value = null;
      String prev_type = null;
      String prev_date = null;
      String prev_ref = null;
      String prev_comm = null;
      String prev_usr = null;
      String prev_ts = null;
      String curr_value = null;
      String curr_type = null;
      String curr_date = null;
      String curr_ref = null;
      String curr_comm = null;
      String curr_usr = null;
      String curr_ts = null;
      boolean has_history = false;

      try {
         String oldQS = buildQS(req);
         String iid = req.getParameter("iid");
         String vid = req.getParameter("vid");
         if (iid == null || iid.trim().equals("")) iid = "-1";
         if (vid == null || vid.trim().equals("")) vid = "-1";

         conn = (Connection) session.getValue("conn");
         // Get the current data
         stmt_curr = conn.createStatement();
         String strSQL = "SELECT SNAME, NAME, SUNAME, IDENTITY, "
            + "VALUE, TYPE, to_char(DATE_, 'YYYY-MM-DD HH24:MI') as DATE_, REFERENCE, "
            + "COMM, USR, to_char(TS, '" + getDateFormat(session) + "') as TC_TS "
            + "FROM gdbadm.V_PHENOTYPES_3 WHERE "
            + "VID=" + vid + " AND IID=" + iid;
         rset_curr = stmt_curr.executeQuery(strSQL);

         // Get the history
         stmt_hist = conn.createStatement();
         strSQL = "SELECT l.VALUE, to_char(l.DATE_, 'YYYY-MM-DD HH24:MI') as DATE_, l.REFERENCE, " +
            "l.COMM, u.USR, to_char(l.TS, '" + getDateFormat(session) + "') as TC_TS, l.TS as dummy " +
            "FROM gdbadm.PHENOTYPES_LOG l, " +
            "gdbadm.USERS u " +
            "WHERE u.ID = l.ID AND " +
            "l.VID=" + vid + " AND l.IID=" + iid + " " +
            "ORDER BY dummy desc";
         rset_hist = stmt_hist.executeQuery(strSQL);

         if (rset_curr.next()) {
            curr_value = formatOutput(session,rset_curr.getString("VALUE"),5);
            curr_date = formatOutput(session,rset_curr.getString("DATE_"),12);
            curr_ref = rset_curr.getString("REFERENCE");
            curr_comm = rset_curr.getString("COMM");
            curr_usr = rset_curr.getString("USR");
            curr_ts = rset_curr.getString("TC_TS"); // Time stamp
            if (curr_ref == null) curr_ref = "";
            if (curr_comm == null) curr_comm = "";
         }
         if (rset_hist.next()) {
            prev_value = rset_hist.getString("VALUE");
            prev_date = formatOutput(session,rset_hist.getString("DATE_"),12);
            prev_ref = rset_hist.getString("REFERENCE");
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
                     "<b style=\"font-size: 15pt\">Phenotypes - View & Edit- Details</b></center>");
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
         out.println("<tr><td>Sampling Unit</td><td>" + rset_curr.getString("SUNAME") + "</td></tr>");
         out.println("<tr><td>Variable</td><td>" + rset_curr.getString("NAME") + "</td></tr>");
         out.println("<tr><td>Type</td><td>" + rset_curr.getString("TYPE") + "</td></tr>");
         out.println("<tr><td>Identity</td><td>" + rset_curr.getString("IDENTITY") + "</td></tr>");
         out.println("</table><br><br>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");

         /*
           out.println("<table width=800px cellPadding=0 cellSpacing=0 align=center border=0 style=\"PADDING-LEFT: 0\">"
           + "<tr><td align=left width=100px><input type=button style=\"WIDTH: 100px\" "
           + "value=\"Back\" onClick='location.href=\""+getServletPath("viewPheno?") + oldQS + "\"'>"
           + "<td width=700px align=center><h3>Phenotype details</tr>"
           + "<tr><td>&nbsp;</td></tr>"
           + "</table>");
           out.println("<table nowrap border=0 cellSpacing=0>");
           out.println("<tr><td colspan=2 bgcolor=black><font color=\"#ffffff\">Static data</font>");
           out.println("<tr><td>Species<td>" + rset_curr.getString("SNAME"));
           out.println("<tr><td>Sampling unit<td>" + rset_curr.getString("SUNAME"));
           out.println("<tr><td>Variable<td>" + rset_curr.getString("NAME"));
           out.println("<tr><td>Type<td>" + rset_curr.getString("TYPE"));
           out.println("<tr><td>Identity<td>" + rset_curr.getString("IDENTITY"));
           out.println("</table><br><br>");

         */


         out.println("<table nowrap align=center border=0 cellSpacing=0 width=740px>");
         out.println("<tr bgcolor=Black>" +
                     "<td align=center colspan=7><b><font color=\"#ffffff\">Current Data</font></b></td>");
         out.println("<tr bgcolor=\"#008B8B\"><td nowrap WIDTH=50 style=\"WIDTH: 50px\">Value");
         out.println("<td nowrap WIDTH=120 style=\"WIDTH: 120px\">Date");
         out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">Comment");
         out.println("<td nowrap WIDTH=100 style=\"WIDTH: 100px\">Reference");
         out.println("<td nowrap WIDTH=120 style=\"WIDTH: 50px\">User</td>");
         out.println("<td nowrap WIDTH=120 style=\"WIDTH: 120px\">Last updated</td></tr>");

         out.println("<tr bgcolor=white>");
         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" +
                     (curr_value.equals(prev_value) ? "" + curr_value : "<font color=red>" + curr_value + "</font>"));
         out.println("<td nowrap WIDTH=100 style=\"WIDTH: 120px\">" + 
                     (curr_date.equals(prev_date) ? "" + curr_date : "<font color=red>" + curr_date + "</font>"));
         out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" +
                     (curr_comm.equals(prev_comm) ? "" + curr_comm : "<font color=red>" + curr_comm + "</font>"));
         out.println("<td nowrap WIDTH=100 style=\"WIDTH: 100px\">" +
                     (curr_ref.equals(prev_ref) ? "" + curr_ref : "<font color=red>" + curr_ref + "</font>"));

         out.println("<td nowrap WIDTH=50 style=\"WIDTH: 50px\">" + curr_usr + "</td>");
         out.println("<td nowrap WIDTH=120 style=\"WIDTH: 120px\">" + curr_ts + "</td>"); // Last updated
                                
         out.println("<tr bgcolor=Black>");
         out.println("<td align=center colspan=7><b><font color=\"#ffffff\">History</font></b></td></tr>");
                        
         curr_value = prev_value;
         curr_date = prev_date;
         curr_ref = prev_ref;
         curr_comm = prev_comm;
         curr_usr = prev_usr;
         curr_ts = prev_ts;
         if (curr_value == null) curr_value = "";
         if (curr_date == null) curr_date = "";
         if (curr_ref == null) curr_ref = "";
         if (curr_comm == null) curr_comm = "";
                                
         boolean odd = true;
         while (rset_hist.next()) {
            prev_value = rset_hist.getString("VALUE");
            prev_date = formatOutput(session,rset_hist.getString("DATE_"),12);
            prev_ref = rset_hist.getString("REFERENCE");
            prev_comm = rset_hist.getString("COMM");
            prev_usr = rset_hist.getString("USR");
            prev_ts = rset_hist.getString("TC_TS");
            if (odd)
               out.println("<tr bgcolor=white>");
            else 
               out.println("<tr bgcolor=lightgrey>");
            odd = !odd;
            out.println("<td nowrap WIDTH=100 style=\"WIDTH: 50px\">" +
                        (curr_value.equals(prev_value) ? "" + curr_value : "<font color=red>" + curr_value + "</font>"));
            out.println("<td nowrap WIDTH=100 style=\"WIDTH: 120px\">" +
                        (curr_date.equals(prev_date) ? "" + curr_date : "<font color=red>" + curr_date + "</font>"));
            out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" + 
                        (curr_comm.equals(prev_comm) ? "" + curr_comm : "<font color=red>" + curr_comm + "</font>")); 
            out.println("<td nowrap WIDTH=100 style=\"WIDTH: 100px\">" +
                        (curr_ref.equals(prev_ref) ? "" + curr_ref : "<font color=red>" + curr_ref + "</font>"));
            out.println("<td nowrap WIDTH=120 style=\"WIDTH: 50px\">" + curr_usr + "</td>");
            out.println("<td nowrap WIDTH=50 style=\"WIDTH: 120px\">" + curr_ts + "</td>"); // Last updated
            curr_value = prev_value;
            curr_date = prev_date;
            curr_ref = prev_ref;
            curr_comm = prev_comm;
            curr_usr = prev_usr;
            curr_ts = prev_ts;
            if (curr_value == null) curr_value = "";
            if (curr_date == null) curr_date = "";
            if (curr_ref == null) curr_ref = "";
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
                        (curr_value.equals(prev_value) ? "" + curr_value : "<font color=red>" + curr_value + "</font>"));
            out.println("<td nowrap WIDTH=100 style=\"WIDTH: 120px\">" +
                        (curr_date.equals(prev_date) ? "" + curr_date : "<font color=red>" + curr_date + "</font>"));
            out.println("<td nowrap WIDTH=250 style=\"WIDTH: 250px\">" + 
                        (curr_comm.equals(prev_comm) ? "" + curr_comm : "<font color=red>" + curr_comm + "</font>")); 
            out.println("<td nowrap WIDTH=100 style=\"WIDTH: 100px\">" +
                        (curr_ref.equals(prev_ref) ? "" + curr_ref : "<font color=red>" + curr_ref + "</font>"));                               
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
                   //  +getServletPath("viewPheno?") + oldQS + "\"'>"+"&nbsp;");
                      +getServletPath("viewPheno?&RETURNING=YES")  + "\"'>"+"&nbsp;");


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
    * The new phenotype page
    */
   private void writeNew(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
                
      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createPhenotype(req, res, conn))
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
      String suid = null, vid = null, newQS, pid, oper, item, vname, identity;
      try {
         conn = (Connection) session.getValue("conn");
                        
         pid = (String) session.getValue("PID");
         suid = req.getParameter("suid");
         vid = req.getParameter("vid");
         newQS = removeQSParameterOper(req.getQueryString());
         oper = req.getParameter("oper");
         vname = req.getParameter("vname");
         identity= req.getParameter("id");

         if (oper == null || oper.trim().equals("")) oper = "SEL_CHANGED";
         item = req.getParameter("item");
         if (item == null || item.trim().equals("")) item = ""; // make sure that all of suid, cid, mid, aid are updated
         if (pid == null || "".equalsIgnoreCase(pid))
            pid = "-1";
         if (item.equals("no")) {
            ;
         } else if (item.equals("suid")) {
            vid = findVid(conn, suid);
         } else {
            suid = findSuid(conn, pid);
            vid = findVid(conn, suid);
         }

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<base target=\"content\">");

         writeNewScript(out);
         out.println("<title>New Phenotype</title>");
         out.println("</head>");
         out.println("<body>");
         out.println(getDateValidationScript());
         // new look
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Phenotypes - New</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         out.println("<form method=get action=\""+getServletPath("viewPheno/new?") + newQS + "\">");
         //     out.println("<center><H3>New phenotype</H3></center>");
         //     out.println("<br><br>");
         out.println("<table>");
         out.println("<tr><td>");
         out.println("Sampling unit<br>");
         out.println("<SELECT name=suid WIDTH=150 height=25 " +
                     "style=\"HEIGHT: 25px; WIDTH: 150px\" " +
                     "onChange='selChanged(\"suid\")'>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SUID FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid + " ORDER BY NAME");
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

         out.println("<td>Variable<br>");
         out.println("<SELECT name=vname WIDTH=250 height=25 " +
                     "style=\"HEIGHT: 25px; WIDTH: 250px\">");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT VID, NAME, TYPE FROM " +
                                  "gdbadm.V_VARIABLES_3 v "+
                                  "WHERE SUID=" + suid +" order by " +
                                  " NAME");


         while (rset.next()) {
            if (vid != null && vid.equalsIgnoreCase(rset.getString("NAME")))
               out.println("<OPTION selected value=\"" + rset.getString("NAME") + "\">" +
                           rset.getString("NAME") + " [" + rset.getString("TYPE") + "]");
            else
               out.println("<OPTION value=\"" + rset.getString("NAME") + "\">" +
                           rset.getString("NAME") + " [" + rset.getString("TYPE") + "]");
         }
         out.println("</SELECT>");
         out.println("<td>Identity<br>");
         out.println("<select name=id width=150 height=25 " +
                     "style=\"WIDTH: 150px; HEIGHT: 25px\">");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT IID, IDENTITY FROM " +
                                  "gdbadm.V_ENABLED_INDIVIDUALS_1 WHERE " +
                                  "SUID=" + suid + " ORDER BY IDENTITY");
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("IDENTITY") + "\">" +
                        rset.getString("IDENTITY"));
         }
         out.println("</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td>Reference<br>");
         out.println("<input type=text name=ref maxlength=32 width=150 heght=25 " +
                     "style=\"WIDTH: 150px HEIGHT: 25px\">");
         out.println("</td>");
         out.println("<td>Date<br>");
         out.println("<input type=text name=date maxlength=10 wdth=150 height=25 " +
                     "size=\"WIDTH: 150px; HEIGHT: 25px\" onBlur='valDate(this, true);'>");
         out.println("</td>");
         out.println("<td>Value<br>");
         out.println("<input type=text name=value maxlength=20 width=150 height=25 " +
                     "style=\"WIDTH: 150px; HEIGHT: 25 px\">");
         out.println("</td></tr>");
         out.println("<tr><td COLSPAN=4>Comment<br>");
         out.println("<textarea rows=10 cols=40 name=comm>");
         out.println("</textarea>");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<tr><td COLSPAN=4>");
         out.println("<input type=button width=100 style=\"WIDTH: 100px\" value=Back " +
                   //  "onClick='document.location.href=\""+getServletPath("viewPheno?") + newQS + "\"'>");
                     "onClick='document.location.href=\""+getServletPath("viewPheno?&RETURNING=YES") + "\"'>");

         out.println("&nbsp;");
         out.println("<input type=button width=100 style=\"WIDTH: 100px\" value=Create " +
                     "onClick='valForm()'>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=item value=\"\">");
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

   private void writeImpFile(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      conn = (Connection) session.getValue("conn");
      writeImpFilePage(req, res);
   }

   private void writeImpFilePage(HttpServletRequest request,
                                 HttpServletResponse response)
      throws ServletException, IOException
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
         out.println("<script type=\"text/JavaScript\">");
         out.println("<!--");
         out.println("function valForm() {");
         out.println("  ");
         out.println("  var rc = 1;");
         out.println("  ");
         out.println("  if (rc) {");
         out.println("          if (confirm('Are you sure that you want to create/update the phenotypes?')) {");
         out.println("                  document.forms[0].oper.value = 'UPLOAD';");
         out.println("                  document.forms[0].submit();");
         out.println("          }");
         out.println("  }");
         out.println("  ");
         out.println("  ");
         out.println("}");
         out.println("//-->");
         out.println("</script>");
         out.println("<title>Import phenotypes</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=0>" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Phenotypes - File import</b></center>" +
                     "</td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post enctype=\"multipart/form-data\" action=\"" +
                     getServletPath("viewPheno/impMultipart") + "\">");
         out.println("<table border=0 >");
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

         // File
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td>File<br>");
         out.println("<input type=file name=filename  " +
                     "style=\"WIDTH: 350px\">");
         out.println("</td></tr>");

         // type of upload
         out.println("<tr><td></td><td>Mode<br>");
         out.println("<select name=type style=\"WIDTH: 200px\">");
         out.println("<option value=\"CREATE\">Create</option>");
         out.println("<option value=\"CREATE_OR_UPDATE\">Create or update</option>");
         out.println("<option value=\"UPDATE\">Update</option>");
         out.println("</select>");
         out.println("</td></tr>");

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
      //                out.println("   if ( (\"\" + document.forms[0].r1.value) != \"\") {");
      //                out.println("           if (document.forms[0].r1.value.length > 20) {");
      //                out.println("                   alert('Raw 1 must be less than 20 characters!');");
      //                out.println("                   rc = 0;");
      //                out.println("           }");
      //                out.println("   }");
      //                out.println("   if ( (\"\" + document.forms[0].r2.value) != \"\") {");
      //                out.println("           if (document.forms[0].r1.value.length > 20) {");
      //                out.println("                   alert('Raw 2 must be less than 20 characters!');");
      //                out.println("                   rc = 0;");
      //                out.println("           }");
      //                out.println("   }");
      //                out.println("   ");
      //                out.println("   ");
      out.println("     if (rc) {");
      //
      out.println(" if (!valDate(document.forms[0].date, true) ) {");
      out.println("return false;");
      out.println(" }");
      //
      out.println("             if (confirm('Are you sure that you want to create the phenotype?')) {");
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
        
   /***************************************************************************************
    * *************************************************************************************
    * The edit page
    */
   private void writeEdit(HttpServletRequest req, HttpServletResponse res) 
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;


      String oper = req.getParameter("oper");
      // System.err.println("new:oper="+oper);
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";
      if (oper.equals("DELETE"))
      {
         if (deletePheno(req, res, conn))
         {
            writeFrame(req, res);
         }
      }
      else if (oper.equals("UPDATE"))
      {
         if (updatePheno(req, res, conn))
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
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
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
         String iid = req.getParameter("iid");
         String vid = req.getParameter("vid");
         String sql = "SELECT TYPE, NAME, "
            + "SNAME, SUNAME, VALUE, to_char(DATE_, 'YYYY-MM-DD') as DATE_, IDENTITY, "
            + "REFERENCE, COMM, USR, to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS "
            + "FROM gdbadm.V_PHENOTYPES_3 WHERE "
            + "VID=" + vid + " AND IID=" + iid;
         rset = stmt.executeQuery(sql);

         rset.next();
         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);
         out.println("<title>Edit phenotypes</title>");
         out.println("</head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<body>\n");



         // new look
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Phenotypes  - View & Edit - Edit</b></font></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         out.println("<table><td></td></table>");
         // the whole information table
         out.println("<table width=800>");
         out.println("<tr><td>");

         // static data table
         out.println("<table border=0 cellpading=0 cellspacing=0 align=left width=300");
         out.println("<tr>");
         out.println("<td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td>");
         out.println("</tr>");
         out.println("<tr><td>Species</td><td>" + rset.getString("SNAME") + "</td></tr>");
         out.println("<tr><td>Variable</td><td>" + rset.getString("NAME") + "</td></tr>");
         out.println("<tr><td>Sampling Unit</td><td>" + rset.getString("SUNAME") + "</td></tr>");
         out.println("<tr><td>Identity</td><td>" + rset.getString("IDENTITY") + "</td></tr>");
         out.println("<tr><td>Last updated by</td><td>" + rset.getString("USR") + "</td></tr>");
         out.println("<tr><td>Last updated</td><td>" + rset.getString("TC_TS") + "</td></tr>");
         out.println("</table></tr></td>");

         // The changable data
         String value = rset.getString("VALUE");
         String date = rset.getString("DATE_");
         String ref = rset.getString("REFERENCE");
         String comm = rset.getString("COMM");
         rset.close();
         stmt.close();
         // oldQS contains iid and vid!
         // Belowe we use rather cryptic names for the form data. We do this to prevent that
         // the data in the form won't collide with the data in the old query string
         out.println("<FORM action=\"edit?" + oldQS + "\" method=\"post\" name=\"FORM1\">");


         // dynamic data table
         out.println("<tr><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0 width=300>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Changable data</font></td></tr>");

         out.println("<tr><td width=200 align=left>Value</td>");
         out.println("<td width=200 align=left>Date</td></tr>");

         out.println("<tr><td width=200><input type=text name=v value=\"" + replaceNull(value, "") + "\"></td>");
         out.println("<td width=200><input type=text name=d value=\"" + replaceNull(date, "") + "\"></td></tr>");

         out.println("<tr><td width=200 align=left>Reference</td></tr>");
         out.println("<tr><td width=200><input type=text name=r value=\"" + replaceNull(ref, "") + "\"></td></tr>");

         out.println("<tr><td width=200 align=left>Comment</td></tr>" +
                     "<tr><td width=200><textarea name=c  "
                     + "HEIGHT=80 width=200 style=\"HEIGHT: 80px; WIDTH: 200px\">");
         out.print( replaceNull(comm, "") );
         out.println("</textarea></td></tr>");

         out.println("</table></td></tr>");



         // buttons table
         out.println("<tr><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0 width=300>");
         out.println("<tr><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td></tr>" +
                     "<tr><td colspan=4 align=center>" +
                     "<input type=button style=\"WIDTH: 100px\" "
                     + "value=\"Back\" onClick='location.href=\""
                //     +getServletPath("viewPheno?") + oldQS + "\"'>&nbsp"+
                      +getServletPath("viewPheno?&RETURNING=YES") + "\"'>&nbsp"+

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
    * Creates a new phenotype.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if phenotype created.
    *         False if phenotype not created.
    */
   private boolean createPhenotype(HttpServletRequest request,
                                   HttpServletResponse response,
                                   Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int suid, iid, id;
         String varName = null, identity=null, ref = null, date = null, value = null, comm = null;
         connection.setAutoCommit(false);

         //System.err.println(request.getQueryString());
         id = Integer.parseInt((String) session.getValue("UserID"));
         suid = Integer.parseInt(request.getParameter("suid"));
         identity = request.getParameter("id");
         value = request.getParameter("value");
         ref = request.getParameter("ref");
         date = request.getParameter("date");
         comm = request.getParameter("comm");
         varName = request.getParameter("vname");

         DbPhenotype dbp = new DbPhenotype();

         dbp.CreatePhenotype(connection, suid, identity, ref, varName,
                             date, value, comm, id); 
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
                       "Phenotypes.New.Create", errMessage, "viewPheno",
                       isOk);
      return isOk;
   }


   /**
    * Deletes a phenotype.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @param connection The current connection.
    * @return True if phenotype deleted.
    *         False if phenotype not deleted.
    */
   private boolean deletePheno(HttpServletRequest request,
                               HttpServletResponse response,
                               Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         int variableId = Integer.parseInt(request.getParameter("vid"));
         int individualId = Integer.parseInt(request.getParameter("iid"));
         DbPhenotype dbPhenotype = new DbPhenotype();
         dbPhenotype.DeletePhenotype(connection, variableId, individualId); 
         errMessage = dbPhenotype.getErrorMessage();
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
                       "Phenotypes.Edit.Delete", errMessage, "viewPheno",
                       isOk);
      return isOk;
   }
   

   /**
    * Updates a phenotype.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @param connection The current connection object.
    * @return True if phenotype updated.
    *         False if phenotype not updated.
    */
   private boolean updatePheno(HttpServletRequest request,
                            HttpServletResponse response,
                            Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         String userID = (String) session.getValue("UserID");
         int variableId = Integer.parseInt(request.getParameter("vid"));
         int individualId = Integer.parseInt(request.getParameter("iid"));
         String value = request.getParameter("v");
         String date = request.getParameter("d");
         String reference = request.getParameter("r");
         String comment = request.getParameter("c");
         DbPhenotype dbPhenotype = new DbPhenotype();
         dbPhenotype.UpdatePhenotype(connection, variableId, individualId,
                                     reference, date, value, comment,
                                     Integer.parseInt(userID)); 
         errMessage = dbPhenotype.getErrorMessage();
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
                       "Phenotypes.Edit.Update", errMessage, "viewPheno",
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
      out.println("             if (confirm('Are you sure you want to delete the phenotype?')) {");
      out.println("                     document.forms[0].oper.value='DELETE';");
      out.println("                     rc = 0;");
      out.println("             }");
      out.println("     ");
      out.println("     } else if ('UPDATE' == action.toUpperCase()) {");
      out.println("             if (confirm('Are you sure you want to update the phenotype?')) {");
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
    * The completion page
    */
   private void writeCompletion(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
      HttpSession session = req.getSession(true);
      String suid, fid, opt_suid_fid, vsid, oldQS, pid, sql;
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
      try {
         Connection conn = (Connection) session.getValue("conn");
         fid = req.getParameter("fid");
         vsid = req.getParameter("vsid");
         pid = (String) session.getValue("PID");
         suid = req.getParameter("suid");
         if (fid == null) fid = "-1";
         if (vsid == null) vsid = "-1";
         res.setContentType("text/html");
         out = res.getWriter();
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Phenotype completion</title>");
         out.println("</head>");
         out.println("<body>");


         // new look
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Phenotypes - Status </b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         //                     out.println("<div style=\"POSITION: absolute; TOP: 5px; LEFT: 360px\"><H3>Phenotype completion</H3></div>");

         out.println("<FORM method=get action=\""+getServletPath("viewPheno/completion")+"\" name=\"FORM1\">");
         out.println("<br><br><table border=0 width=400>");
         out.println("<tr>");
         // Filters
         sql = "SELECT FID, NAME FROM gdbadm.V_FILTERS_1 WHERE " +
            "PID=" + pid + " ORDER BY NAME ";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(sql);
         out.println("<td width=120 style=\"WIDTH: 120px\">" +
                     "Filter<br><select name=\"fid\" width=120 style=\"WIDTH: 120px\">");
         while (rset.next()) {
            if (rset.getString("FID").equals(fid))
               out.println("<option selected value=\"" + rset.getString("FID") + "\">");
            else
               out.println("<option value=\"" + rset.getString("FID") + "\">");
            out.println(rset.getString("NAME"));
         }
         out.println("</select>");
         rset.close();
         stmt.close();

         // sampling unit
         sql = "SELECT SUID, NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 WHERE " +
            "PID=" + pid + " ORDER BY NAME ";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(sql);

         out.println("<td width=120 style=\"WIDTH: 120px\">" +
                     "Sampling Unit<br><select name=\"suid\" name=select onChange='document.forms[0].submit()'width=120 style=\"WIDTH: 120px\">");

         while (rset.next()) {

            // a value has been choosen
            if (suid !=null && rset.getString("SUID").equals(suid))
            {
               out.println("<option selected value=\"" + rset.getString("SUID") + "\">");
               out.println(rset.getString("NAME"));

            }
            else
            {
               // first time. No sampling unit selected
               if (suid ==null || "".equalsIgnoreCase(suid))
               {
                  out.println("<option selected value=\"" + rset.getString("SUID") + "\">");
                  out.println(rset.getString("NAME"));
                  suid=rset.getString("SUID");
               }
               else
               {
                  // non-choosen values
                  out.println("<option value=\"" + rset.getString("SUID") + "\">");
                  out.println(rset.getString("NAME"));
               }
            }
         }
         out.println("</select>");
         rset.close();
         stmt.close();


         // variable Set
         stmt = conn.createStatement();
         sql = "SELECT VSID, NAME FROM gdbadm.V_VARIABLE_SETS_3 WHERE " +
            "PID=" + pid + " AND SUID="+suid + " ORDER BY NAME ";
         rset = stmt.executeQuery(sql);
         out.println("<td width=120 style=\"WIDTH: 120px\">" +
                     "Variable set<br><select name=vsid width=120 style=\"WIDTH: 120px\">");
         while (rset.next()) {
            if (rset.getString("VSID").equals(vsid))
               out.println("<option selected value=\"" + rset.getString("VSID") + "\">");
            else
               out.println("<option value=\"" + rset.getString("VSID") + "\">");
            out.println(rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("<td>");
         out.println("&nbsp;<br><input type=submit name=\"submit1\" value=\"Display\">");
         out.println("</table>");

         out.println("</form>");
         out.println("<hr>");

         // The completion data
         if (vsid != null && fid != null && vsid != "-1" && fid != "-1") {
            writeCompletionTable(conn, out, pid, fid, vsid, suid);
         }

         out.println("</body>");
         out.println("</html>");
      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {
         }
      }
   }
   private void writeCompletionTable(Connection conn,
                                     PrintWriter out,
                                     String pid,
                                     String fid,
                                     String vsid,
                                     String suid) {

      //System.err.println("params:"+pid+":"+fid+":"+vsid+":"+suid);
      Statement stmt = null;
      ResultSet rset = null;
      int inds = 0;
      int variables = 0;
      String gqlExpr = null, filter = null;
      Vector vids = new Vector(100);
      //String sid = null;
      try {
         stmt = conn.createStatement();
         /*
           rset = stmt.executeQuery("SELECT SID FROM gdbadm.V_VARIABLE_SETS_4 WHERE VSID=" + vsid);
           rset.next();
           sid = rset.getString("SID");
         */
         // The number of variables and individuals:

         rset = stmt.executeQuery("SELECT EXPRESSION FROM " +
                                  "gdbadm.V_FILTERS_1 WHERE FID=" + fid);



         rset.next();

         gqlExpr = rset.getString("EXPRESSION");

         GqlTranslator gqlt = new GqlTranslator(pid, suid, gqlExpr, conn);

         gqlt.translate();
         filter = gqlt.getFilter();
         rset = stmt.executeQuery("SELECT COUNT(*) as INDS " + filter);
         rset.next();
         inds = rset.getInt("INDS");


         out.println("<table border=0 cellspacing=0 cellpadding=0 width=100%>");
         out.println("<tr><td width=5></td><td>");

         out.println("<p>Filter returns " + inds + " individuals.</p>");

         rset = stmt.executeQuery("SELECT VID FROM gdbadm.R_VAR_SET WHERE VSID=" + vsid);
         while (rset.next()) {
            vids.addElement(rset.getString("VID"));
            variables++;
         }



         out.println("<p>Variable set contains " + variables + " variables</p>");

         out.println("<table cellPadding=0 cellSpacing=0 width=500 style=\"WIDTH: 460px\">");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td width=120 style=\"WIDTH: 120px\">Variable");
         out.println("<td width=120 style=\"WIDTH: 120px\">Percentage");
         out.println("<td width=120 style=\"WIDTH: 120px\">individuals");
         out.println("<td width=120 style=\"WIDTH: 120px\">&nbsp;"); // for hyperlinks
         boolean odd = true;
         for (int i = 0; i < vids.size(); i++) {
            out.println("<tr bgcolor=" + (odd ? "white" : "lightgrey") + ">");
            odd = !odd;
            out.println("<td width=120 style=\"WIDTH: 120px\">");
            rset = stmt.executeQuery("SELECT NAME FROM gdbadm.V_VARIABLES_1 WHERE VID=" +
                                     (String) vids.elementAt(i));
            if(rset.next())
            {
               out.println(rset.getString("NAME"));
               rset = stmt.executeQuery("SELECT COUNT(*) as PHENOS FROM gdbadm.V_PHENOTYPES_1 WHERE " +
                                        "VID=" + (String) vids.elementAt(i) + " AND " +
                                        "IID IN(SELECT ind.IID " + filter + ")");
            }
            if(rset.next())
            {
               if(inds > 0 )
               {
                  out.println("<td width=120 style=\"WIDTH: 120px\">" + (int) 100*rset.getInt("PHENOS")/inds + "%");
               }
               else
               {
                  out.println("<td width=120 style=\"WIDTH: 120px\">" +"-");
               }

               out.println("<td width=120 style=\"WIDTH: 120px\">" + rset.getInt("PHENOS") + "");
               out.println("<td width=120 style=\"WIDTH: 120px\">&nbsp;");

            }
         }
         out.println("</table>");
         out.println("</td></tr>");

         out.println("</table>");

      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {
         }
      }
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
            // We neew the privilege ind_R for all these
            title = "Phenotypes -  View & Edit";
            if ( privDependentString(privileges, PHENO_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/edit") ) {
            // We neew the privilege IND_W
            title = "Phenotypes - View & Edit - Edit";
            if ( privDependentString(privileges, PHENO_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/new") ) {
            // We neew the privilege IND_W
            title = "Phenotypes - View & Edit - New";
            if ( privDependentString(privileges, PHENO_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/impFile") ) {
            // We need the privilege IND_W
            title = "Phenotypes -  File Import";
            if ( privDependentString(privileges, IND_W, "", null) == null)
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

