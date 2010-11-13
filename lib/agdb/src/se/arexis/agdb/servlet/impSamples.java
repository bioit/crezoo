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


  Revision 1.5  2001/05/31 07:23:38  frob
  Unused method printSUInfo.

  Revision 1.4  2001/05/08 14:26:03  frob
  Modified the commit/rollback section of doPost() to use the general commitOrRollback().
  The writeError method is removed.

  Revision 1.3  2001/04/24 09:33:51  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:24  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.4  2001/04/18 09:26:42  frob
  Removed the size of the main table used on the webpages.

  Revision 1.1.1.1.2.3  2001/04/11 09:48:56  frob
  doGet: Call to parse() now passes file type definitions.
         No delimiter is read from the request object.
         Layout changes.
  doPost: Delimiter field is removed.
          Length of file name field is changed.
          HTML is validated.

  Revision 1.1.1.1.2.2  2001/03/28 13:47:50  frob
  Added catch() for InputDataFileException which can be
  raised from the parse()-method.
  Added check that the connection object is created before doing
  rollback on it (to avoid null pointer exception).

  Revision 1.1.1.1.2.1  2001/03/28 12:11:28  frob
  Changed the call to the FileParser constructor.
  Changed how class gets data from the FileParser.
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

public class impSamples extends SecureArexisServlet
{

   /**
    * Prints the page used for importing samples to a sampling unit. 
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @exception IOException If PrintWriter could not be created.
    */
   public void doGet(HttpServletRequest request,
                     HttpServletResponse response)
      throws IOException
   {

      HttpSession session = request.getSession(true);
      Connection connection;
      Statement sqlStatement = null;
      ResultSet resultSet = null;
      String samplingUnitId = null;

      String strUser = (String) session.getValue("UserID");
      String projectId = (String) session.getValue("PID");

      // set content type and other response header fields first
      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");
      PrintWriter out = response.getWriter();
      try
      {
         connection = (Connection) session.getValue("conn");
         sqlStatement = connection.createStatement();
         resultSet = sqlStatement.executeQuery("SELECT SUID, " +
                                               " NAME FROM gdbadm.V_SAMPLING_UNITS_2 WHERE PID=" + 
                                               projectId + " order by NAME");

         out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                     "Transitional//EN\"");
         out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");
         
         out.println("<html>\n"
                     + "<head>\n");
         writeScript(out);
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Import Samples</title>\n"
                     + "</head>\n"
                     + "<body>\n");

         out.println("<table width=846 border=0");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Samples - File Import</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         out.println("<form name=\"FORM1\" action=\"" +
                     getServletPath("impSamples") + 
                     "\" method=\"post\" enctype=\"multipart/form-data\">"
                     + "<table border=0>");
         out.println("<tr><td nowrap align=right>Sampling Unit</td>");

         out.println("<td><select name=\"suid\" style=\"HEIGHT: 24px; WIDTH: 240px\" size=1>");

         boolean first_round = true;
         while (resultSet.next())
         {
            if (first_round)
            {
               samplingUnitId = new String(resultSet.getString("SUID"));
               first_round = false;
            }

            if (samplingUnitId != null &&
                samplingUnitId.equalsIgnoreCase(resultSet.getString("SUID")) )
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

         out.println("<tr><td nowrap align=right>File</td>"
                     + "<td><input name=\"userfile\" type=\"file\"" +
                     "style=\"WIDTH: 350px\"></td></tr>");

         out.println("<tr><td>&nbsp;<td>&nbsp;");

         out.println("<tr><td><input type=\"button\" value=\"Send\" " +
                     "style=\"HEIGHT: 24px; WIDTH: 100px\" " +
                     "onClick='confirmSubmit()'></td>");
         out.println("<td>&nbsp;</td></tr>" +
                     "</table>" +
                     "</form>" +
                     "</body>\n" +
                     "</html>");

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
    * Imports individuals from a file
    *
    * @param request The request object to use.
    * @param response The response object to use.
    */
   public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
   {
      Connection connection = null;
      HttpSession session = request.getSession(true);

      response.setContentType("text/html");
      response.setHeader("Pragme", "no-cache");
      response.setHeader("Cache-Control", "no-cache");

      boolean isOk = true;
      String errMessage = null;
      String samplingUnitAsStr = null;
      try
      {
         // Blindly take it on faith this is a multipart/form-data request

         // Construct a MultipartRequest to help read the information.
         // Pass in the request, a directory to saves files to, and the
         // maximum POST size we should attempt to handle.
         MultipartRequest multiRequest =
            new MultipartRequest(request, getUpFilePath(), 5 * 1024 * 1024);
      
         Enumeration fileEnum = multiRequest.getFileNames();
         if (fileEnum.hasMoreElements())
         {
            // Get given filename and convert it to this system
            String givenFileName = (String) fileEnum.nextElement();
            String systemFileName = multiRequest.getFilesystemName(givenFileName);

            String upPath = getUpFilePath();

            // Get parameters
            String userId = (String) session.getValue("UserID");
            int samplingUnitId = Integer.parseInt(multiRequest.getParameter("suid"));
            samplingUnitAsStr = multiRequest.getParameter("samplingUnitId");

            
            DbIndividual dbIndividual = new DbIndividual();
            FileParser fileParser = new FileParser(upPath + "/" +  systemFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.SAMPLE,
                                                                        FileTypeDefinition.LIST));

            connection = (Connection) session.getValue("conn");
            // Turn off auto commit to enable transactions
            connection.setAutoCommit(false);

            dbIndividual.CreateOrUpdateSamples(fileParser, connection,
                                               samplingUnitId,
                                               Integer.parseInt(userId)); 

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

      // If commit/rollback was ok and if database operation was ok, set
      // the redirect page.
      if (commitOrRollback(connection, request, response,
                           "Samples.Import.Send", errMessage, "impSamples",
                           isOk)  
          && isOk)
      {
         try
         {
            response.sendRedirect("viewSamples?&suid=" + samplingUnitAsStr +
                                  "&ACTION=DISPLAY");
         }
         catch (Exception e)
         {
            e.printStackTrace(System.err);
         }
      }
   }




   private void writeScript(PrintWriter out) {
      out.println("<script type=\"text/javascript\">");
      out.println("<!--");
      out.println("function confirmSubmit() {");
      out.println("  var doSubmit = 1;");
      //	out.println("  if (document.forms[0].update[1].checked) {");
      out.println("    if (confirm('Are you sure you want to import the samples?')) {");
      out.println("      ;");
      out.println("    } else {");
      out.println("      doSubmit = 0;");
      out.println("    }");
      //out.println("  }");
      out.println("  if (doSubmit != 0)");
      out.println("    document.forms[0].submit();");
      out.println("}");
      out.println("// -->");
      out.println("</script>");
   }


}
