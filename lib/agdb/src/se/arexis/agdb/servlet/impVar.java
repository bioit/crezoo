/*
  $Log$
  Revision 1.5  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.4  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.3  2002/10/18 12:18:50  heto
  Changed method to get the path to uploads. Static -> function.

  Revision 1.2  2002/10/18 11:41:09  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:04  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.4  2001/05/09 06:03:48  frob
  Modified the commit/rollback section of doPost() to use the general commitOrRollback().
  The writeError method is removed.

  Revision 1.3  2001/04/24 09:33:52  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:25  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.3  2001/04/18 12:15:28  frob
  doPost: Added parameters in Parse() call
          Removed usage of delimiter.
          Layout changes.
  doGet: Removed delimiter field.
         Resized file name field.
         HTML validated.

  Revision 1.1.1.1.2.2  2001/03/28 13:47:52  frob
  Added catch() for InputDataFileException which can be
  raised from the parse()-method.
  Added check that the connection object is created before
  doing rollback on it (to avoid null pointer exception).

  Revision 1.1.1.1.2.1  2001/03/28 12:16:20  frob
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
import se.arexis.agdb.util.*;
import se.arexis.agdb.util.FileImport.*;

import se.arexis.agdb.db.*;


public class impVar extends SecureArexisServlet
{


   /**
    * Writes the page used to import variables.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @exception IOException If no PrintWriter can be created.
    */
   public void doGet(HttpServletRequest request,
                     HttpServletResponse response)
      throws IOException {
		
      HttpSession session = request.getSession(false);
      Connection connection;
      Statement sqlStatement = null;
      ResultSet resultSet = null;

      // set content type and other response header fields first
      response.setContentType("text/html");

      PrintWriter out = response.getWriter();
      try
      {
         String projectId = (String) session.getValue("PID");
         if (projectId == null)
         {
            projectId = new String("-1");
         }
         
         connection = (Connection) session.getValue("conn");
         sqlStatement = connection.createStatement();
         resultSet = sqlStatement.executeQuery("SELECT SUID, NAME FROM "
                                               + "gdbadm.V_SAMPLING_UNITS_2 "
                                               + "WHERE PID=" + projectId
                                               + " order by NAME");

         out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                     "Transitional//EN\"");
         out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");

         out.println("<html>\n"
                     + "<head>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Import Variable</title>\n"
                     + "</head>\n"
                     + "<body>\n");

         out.println("<table width=846 border=0 >");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Variables - File Import</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         out.println("<form name=\"FORM1\" action=\""
                     + getServletPath("impVar")
                     + "\" method=\"post\" enctype=\"multipart/form-data\">");
         out.println("<table border=0>");
         out.println("<tr><td nowrap align=right>File</td>"
                     + "<td><input name=\"filename\" type=\"file\" "
                     + "style=\"WIDTH: 350px\"></td></tr>");
         out.println("<tr><td nowrap align=right>Sampling Units</td>");
         out.println("<td><select name=\"suid\" style=\"HEIGHT: 24px; WIDTH: 240px\" size=1>");


         String samplingUnitId = request.getParameter("SUID");

         if (samplingUnitId == null)
         {
            samplingUnitId = "-1";
         }
         

         while (resultSet.next())
         {
            if (samplingUnitId.equals(resultSet.getString("SUID")))
            {
               out.println("<option selected value=\"" +
                           resultSet.getString("SUID") + "\">" +
                           resultSet.getString("NAME"));
            }
            else
            {
               out.println("<option value=\"" +
                           resultSet.getString("SUID") + "\">" +
                           resultSet.getString("NAME"));
            }
         }
         out.println("</select></td></tr>");

         out.println("<tr><td><input type=\"submit\" value=\"Send\"" +
                     "style=\"HEIGHT: 24px; WIDTH: 100px\"></td>");
         out.println("<td>&nbsp;</td></tr>");
         out.println("</table>");
         out.println("</form>");
         out.println("</body>");
         out.println("</html>");


      } catch (SQLException e) {
         e.printStackTrace(out);
      } finally {
         try {
            if (resultSet != null) resultSet.close();
            if (sqlStatement != null) sqlStatement.close();
         } catch (SQLException ignored) {}
      }

   }


   
   /**
    * Imports variables from a file.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @exception ServletException If any page could not be written.
    * @exception IOException If any page could not be written.
    */
   public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
      throws ServletException, IOException
   {
      Connection connection = null;
      HttpSession session = request.getSession(true);

      String userId = (String) session.getValue("UserID");
      if (userId == null || userId.equalsIgnoreCase(""))
      {
         response.sendRedirect("/servlets/redirectClass");
      }
      response.setContentType("text/html");

      boolean isOk = true;
      String errMessage = null;
      String samplingUnitIdStr = null;
      try
      {
         MultipartRequest multiRequest =
            new MultipartRequest(request, getUpFilePath(), 5 * 1024 * 1024);

         Enumeration fileEnum = multiRequest.getFileNames();
         if (fileEnum.hasMoreElements())
         {
            DbVariable dbVariable = null;

            String givenFileName = (String) fileEnum.nextElement();
            String systemFileName = multiRequest.getFilesystemName(givenFileName);
            int samplingUnitId = Integer.parseInt(multiRequest.getParameter("suid"));
            samplingUnitIdStr = multiRequest.getParameter("suid");
            String upPath = getUpFilePath();

            dbVariable = new DbVariable();
            FileParser fileParser = new FileParser(upPath + "/" +  systemFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.VARIABLE,
                                                                        FileTypeDefinition.LIST));
            connection = (Connection) session.getValue("conn");

            // Rollback and turn off auto commit to enable transactions
            connection.rollback();
            connection.setAutoCommit(false);

            dbVariable.CreateVariables(fileParser, connection,
                                       samplingUnitId,
                                       Integer.parseInt(userId)); 

            errMessage = dbVariable.getErrorMessage();
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

      // If commit/rollback ok and if database operation was ok, set the
      // redirect page.
      if (commitOrRollback(connection, request, response,
                           "Variables.Import.Send", 
                           errMessage, "impVar", isOk)
          && isOk)
      {
         response.sendRedirect("viewVar?&suid=" + samplingUnitIdStr +
                               "&ACTION=DISPLAY");
      }
   }

}
