/*
  $Log$
  Revision 1.5  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.4  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.3  2002/10/18 12:18:51  heto
  Changed method to get the path to uploads. Static -> function.

  Revision 1.2  2002/10/18 11:41:09  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:04  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.4  2001/05/09 06:32:30  frob
  Modified the commit/rollback section of doPost() to use the general commitOrRollback().
  The writeError method is removed.

  Revision 1.3  2001/04/24 09:33:52  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:26  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.3  2001/04/18 12:59:03  frob
  doPost: Added parameters to Parse() call.
          Removed usage of delimiter.
          Layout fixes.
  doGet: Delimiter field removed.
         File name field resized.
         HTML validated.

  Revision 1.1.1.1.2.2  2001/03/28 13:47:52  frob
  Added catch() for InputDataFileException which can be raised
  from the parse()-method.
  Added check that the connection object is created before doing
  rollback on it (to avoid null pointer exception).

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


public class impVarSet extends SecureArexisServlet
{

   
   /**
    * Writes the page used to import variable sets.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @exception IOException If no PrintWriter could be created. 
    */
   public void doGet(HttpServletRequest request,
                     HttpServletResponse response)
      throws IOException
   {
      HttpSession session = request.getSession(false);
      Connection connection;

      Statement sqlStatement = null;
      ResultSet resultSet = null;

      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      try
      {
         String projectId = (String) session.getValue("PID");
         if (projectId == null)
         {
            projectId = new String("-1");
         }
         
         String samplingUnitId = request.getParameter("suid");
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
         out.println("<title>Import Variable set</title>\n"
                     + "</head>\n"
                     + "<body>\n");

         // new look
         out.println("<table width=846 border=0 >");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">" +
                     "Variable Sets - File Import</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         out.println("<form name=\"FORM1\" action=\"" +
                     getServletPath("impVarSet")+"\" method=\"post\"" +
                     "enctype=\"multipart/form-data\">");
         out.println("<table border=0>");
         out.println("<tr><td nowrap align=right>Name</td>"
                     + "<td><input type=\"text\" name=\"name\" "
                     + "maxlength=20 style=\"WIDTH: 240px\"></td></tr>");

         out.println("<tr><td nowrap align=right>Comment</td>"
                     + "<td><input type=\"text\" name=\"comm\" " +
                     "maxlength=256 style=\"WIDTH: 240px\"></td></tr>");
         
         out.println("<tr><td nowrap align=right>File</td>"
                     + "<td><input name=\"filename\" type=\"file\" "
                     + "style=\"WIDTH: 350px\"></td></tr>");
         out.println("<tr><td nowrap align=right>Sampling Units</td>");
         out.println("<td><select name=\"suid\" style=\"HEIGHT: 24px; WIDTH: 240px\" size=1>");

         while (resultSet.next())
         {
            if (samplingUnitId != null &&
                samplingUnitId.equals(resultSet.getString("SUID")))
            {
               out.println("<option selected value=\"" +
                           resultSet.getString("SUID") + "\">" +
                           resultSet.getString("NAME")); 
            }
            else
            {
               if(samplingUnitId == null ||
                  "".equalsIgnoreCase(samplingUnitId) ||
                  samplingUnitId.equalsIgnoreCase("-1")) 
               {
                  out.println("<option selected value=\"" +
                              resultSet.getString("SUID") + "\">" +
                              resultSet.getString("NAME")); 
                  samplingUnitId = resultSet.getString("SUID");
               }
               else
               {
                  out.println("<option value=\"" +
                              resultSet.getString("SUID") + "\">" +
                              resultSet.getString("NAME")); 
               }
            }
         }
         out.println("</select></td></tr>");

         out.println("<tr><td><input type=\"submit\" value=\"Send\" "
                     + "style=\"HEIGHT: 24px; WIDTH: 100px\"></td>" +
                     "<td>&nbsp;</td></tr>");
         out.println("</table>");
         out.println("</form>");
         out.println("</body>");
         out.println("</html>");


      }
      catch (SQLException e)
      {
         e.printStackTrace(out);
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


   /**
    * Imports variable sets from a file.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @exception ServletException If any page can not be written.
    * @exception IOException If any page can not be written.
    */
   public void doPost(HttpServletRequest request,
                      HttpServletResponse response) 
      throws ServletException, IOException
   {
      Connection connection = null;
      HttpSession session = request.getSession(false);
      String userId = (String) session.getValue("UserID");

      response.setContentType("text/html");

      PrintWriter out = response.getWriter();
      boolean isOk =true;
      String errMessage = null;
      String samplingUnitIdStr = null;
      try
      {
         MultipartRequest multiRequest =
            new MultipartRequest(request, getUpFilePath(), 5 * 1024 * 1024);

         Enumeration fileEnum = multiRequest.getFileNames();
         if (fileEnum.hasMoreElements())
         {
            String givenFileName = (String) fileEnum.nextElement();
            String systemFileName = multiRequest.getFilesystemName(givenFileName);
            String variableSetName = multiRequest.getParameter("name");
            String comment = multiRequest.getParameter("comm");
            int samplingUnitId = Integer.parseInt(multiRequest.getParameter("suid"));
            String projectId = (String) session.getValue("PID");
       
            String upPath = getUpFilePath();

            DbVariable dbVariable = new DbVariable();

            FileParser fileParser = new FileParser(upPath + "/" +  systemFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.VARIABLESET,
                                                                        FileTypeDefinition.LIST));
            
            connection = (Connection) session.getValue("conn");
            
            // Rollback and turn off auto commit to enable transactions
            connection.rollback();
            connection.setAutoCommit(false);

            dbVariable.CreateVariableSets(fileParser, connection,
                                          variableSetName, comment,
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

      if (commitOrRollback(connection, request, response,
                           "VariableSets.Import.Send", errMessage,
                           "impVarSet", isOk)
          && isOk)
      {
         response.sendRedirect("viewVarSet?&suid=" + samplingUnitIdStr +
                               "&ACTION=DISPLAY");
      }
   }

}
