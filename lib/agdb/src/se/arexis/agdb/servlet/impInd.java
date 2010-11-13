/*
  $Log$
  Revision 1.7  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.6  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.5  2002/12/20 09:17:48  heto
  Added code to handle two types of import. This servlet will not be used eventually

  Revision 1.4  2002/12/13 15:01:32  heto
  Fixed graphical problem in UI

  Revision 1.3  2002/11/13 09:02:27  heto
  Fixed upload size

  Revision 1.2  2002/10/18 11:41:09  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:04  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.10  2001/05/31 07:23:38  frob
  Unused method printSUInfo.

  Revision 1.9  2001/05/08 14:23:12  frob
  Minor fix.

  Revision 1.8  2001/05/08 14:03:06  frob
  Modified the commit/rollback section of doPost() to use the general commitOrRollback().

  Revision 1.7  2001/05/04 11:17:08  frob
  Changed the way database operations are handled.

  Revision 1.6  2001/04/27 14:14:38  frob
  Changed parameters in call to writeErrorPage.

  Revision 1.5  2001/04/27 13:52:23  frob
  Changed call to writeErrorPage.

  Revision 1.4  2001/04/27 13:20:09  frob
  Removed private method writeError, class now calls writeError in super class.

  Revision 1.3  2001/04/24 09:33:51  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:24  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.4  2001/04/18 09:26:42  frob
  Removed the size of the main table used on the webpages.

  Revision 1.1.1.1.2.3  2001/04/11 09:01:30  frob
  doPost: Added parameters to the Parse() call.
          No longer uses the delimiters in the request object.
          Layout fixes.
  doGet: Field for delimiter is removed.
         Field for file name is resized.
         HTML is validated.

  Revision 1.1.1.1.2.2  2001/03/28 13:47:50  frob
  Added catch() for InputDataFileException which can be
  raised from the parse()-method.
  Added check that the connection object is created before doing
  rollback on it (to avoid null pointer exception).

  Revision 1.1.1.1.2.1  2001/03/28 12:58:40  frob
  Changed call to constructor to use new constructor format.
  Indented the file and added the log header.

*/

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.oreilly.servlet.MultipartRequest;
import se.arexis.agdb.util.*;
import se.arexis.agdb.db.*;
import se.arexis.agdb.util.FileImport.*;

public class impInd extends SecureArexisServlet
{

    /**
     * Prints the page used for importing individuals from a file.
     *
     * @param request The request object to use.
     * @param response The response object to use.
     * @exception IOException If a writer could not be created.
     */
    public void doGet(HttpServletRequest request,
                     HttpServletResponse response)
      throws IOException
      
    {
       
        HttpSession session = request.getSession(true);
        
      
        String extPath = request.getPathInfo();

        if (extPath == null || extPath.equals("") || extPath.equals("/")) 
        {
            // The frame is requested
            importFile(request, response);
        } 
        else if (extPath.equals("/import")) 
        {
            propertiesPage(request,response);
        }
       
   }
    
    /**
     * Handle the post requests for importing individuals. 
     * 
     *
     * @param request The request object to use.
     * @param response The response object to use.
     */
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
    {
        HttpSession session = request.getSession(true);
        String extPath = request.getPathInfo();
        if (extPath == null || extPath.equals("") || extPath.equals("/")) 
        {
            // The frame is requested
            uploadFile(request, response);
        } 
        else if (extPath.equals("/import")) 
        {
            propertiesPagePost(request,response);
        }
       
    }
                      
    
   /**
     * Import individuals from a file.
     * This is the old method then uploading and importing was done 
     * in the same step
     *
     * @param request The request object to use.
     * @param response The response object to use.
     */
   private void importFile(HttpServletRequest request,
                           HttpServletResponse response)
        throws IOException        
   {
      HttpSession session = request.getSession(true);
      Connection connection;
      Statement sqlStatement = null;
      ResultSet resultSet = null;
      String projectId = null, samplingUnitId = null;

      String strUser = (String) session.getValue("UserID");
      projectId = (String) session.getValue("PID");

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
                                               " NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 WHERE PID=" + 
                                               projectId + " order by NAME");
         
         out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                     "Transitional//EN\"");
         out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");
         
         out.println("<html>\n"
                     + "<head>\n");
         writeScript(out);
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Import Individuals</title>\n"
                     + "</head>\n"
                     + "<body>\n");

         out.println("<table width=846 border=0>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Individuals - File Import</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         out.println("<form name=\"FORM1\" action=\"" +
                     getServletPath("impInd")+"\" method=\"post\" enctype=\"multipart/form-data\">" +
                     "<table border=0>");

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
                           resultSet.getString("SUID") + "\">" + resultSet.getString("NAME"));
            }
            else
            {
               out.println("<option value=\"" + resultSet.getString("SUID") + 
                           "\">" + resultSet.getString("NAME"));
            }
         }

         out.println("</select></td></tr>");
         out.println("<tr><td nowrap align=right>File</td>"
                     + "<td><input name=\"userfile\" type=\"file\"" +
                     "></td></tr>");
                     //style=\"WIDTH: 350px\"

         out.println("<tr><td align=right>Create new");
         out.println("<td><input type=radio value=no name=update CHECKED>");
         out.println("<tr><td align=right>Update existing");
         out.println("<td><input type=radio value=yes name=update>");

         out.println("<tr><td align=right>Create & Update ");
         out.println("<td><input type=radio value=both name=update>");

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


  
                     
   public void uploadFile(HttpServletRequest request,
                          HttpServletResponse response)
   {

      Connection connection = null;
      HttpSession session = request.getSession(true);

      response.setContentType("text/html");
      response.setHeader("Pragme", "no-cache");
      response.setHeader("Cache-Control", "no-cache");

      boolean isOk = true;
      String errMessage = null;
      String sampleUnitIdAsStr = null;
      try
      {
         // Blindly take it on faith this is a multipart/form-data request

         // Construct a MultipartRequest to help read the information.
         // Pass in the request, a directory to saves files to, and the
         // maximum POST size we should attempt to handle.
         MultipartRequest multiRequest =
            new MultipartRequest(request, getUpFilePath(), 6 * 1024 * 1024);
         //"/arexis/uploads" (by heto)
         
         Enumeration fileEnum = multiRequest.getFileNames();
         if (fileEnum.hasMoreElements())
         {
            // Get filename and convert it to this system
            String givenFileName = (String) fileEnum.nextElement();
            String systemFileName = multiRequest.getFilesystemName(givenFileName);
            String upPath = getUpFilePath();
            String update = multiRequest.getParameter("update");
            
            // Get parameters
            String userId = (String) session.getValue("UserID");
            int sampleUnitId = Integer.parseInt(multiRequest.getParameter("suid"));
            sampleUnitIdAsStr = multiRequest.getParameter("suid");
            connection = (Connection) session.getValue("conn");
            connection.setAutoCommit(false);

            // Create the individual 
            DbIndividual dbIndividual = new DbIndividual();
            FileParser fileParser = new FileParser(upPath + "/" +  systemFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.INDIVIDUAL,
                                                                        FileTypeDefinition.LIST));

            // If add mode
            if (update == null || update.equalsIgnoreCase("no"))
            {
               dbIndividual.CreateIndividuals(fileParser, connection,
                                              sampleUnitId,
                                              Integer.parseInt(userId));
            }

            // If update mode
            else if (update.equalsIgnoreCase("yes"))
            {
               dbIndividual.UpdateIndividuals(fileParser, connection,
                                              sampleUnitId,
                                              Integer.parseInt(userId)); 
            }

            // if both update and add
            else if (update.equalsIgnoreCase("both"))
            {
               dbIndividual.CreateOrUpdateIndividuals(fileParser,
                                                      connection,
                                                      sampleUnitId,
                                                      Integer.parseInt(userId));
            }

            // Get the error message from the database object. If it is set an
            // error occured during the operation so an error is thrown.
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

      // If commit/rollback was ok and if the database operation was ok,
      // set the redirect page.
      if (commitOrRollback(connection, request, response,
                           "Individuals.FileImport.Send", errMessage,
                           "impInd", isOk)
          && isOk)
      {
         try
         {
            response.sendRedirect("viewInd?&suid=" + sampleUnitIdAsStr +
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


      out.println("  if (document.forms[0].update[1].checked) {");
      out.println("    if (confirm('Are you sure you want to update the Individuals?')) {");
      out.println("      ;");
      out.println("    } else {");
      out.println("      doSubmit = 0;");
      out.println("    }");
      out.println("  }");

      out.println("  if (document.forms[0].update[0].checked) {");
      out.println("    if (confirm('Are you sure you want to create the Individuals?')) {");
      out.println("      ;");
      out.println("    } else {");
      out.println("      doSubmit = 0;");
      out.println("    }");
      out.println("  }");

      out.println("  if (document.forms[0].update[2].checked) {");
      out.println("    if (confirm('Are you sure you want to create and/or update the Individuals?')) {");
      out.println("      ;");
      out.println("    } else {");
      out.println("      doSubmit = 0;");
      out.println("    }");
      out.println("  }");




      out.println("  if (doSubmit != 0)");
      out.println("    document.forms[0].submit();");
      out.println("}");
      out.println("// -->");
      out.println("</script>");
   }

   
   
    /**
     * 
     *
     * @param request The request object to use.
     * @param response The response object to use.
     */
    private void propertiesPage(HttpServletRequest request,
                           HttpServletResponse response)
        throws IOException        
    {
        
        HttpSession session = request.getSession(true);
        Connection connection;
        Statement sqlStatement = null;
        ResultSet resultSet = null;
        String projectId = null, samplingUnitId = null;

        String strUser = (String) session.getValue("UserID");
        projectId = (String) session.getValue("PID");
        
        String isid = request.getParameter("isid");
        String ifid = request.getParameter("ifid");
        String fname = request.getParameter("fname");

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
                                               " NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 WHERE PID=" + 
                                               projectId + " order by NAME");
         
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                     "Transitional//EN\"");
            out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");
         
            out.println("<html>\n"
                     + "<head>\n");
            writeScript(out);
            HTMLWriter.css(out,getURL("style/axDefault.css"));
            out.println("<title>Import Individuals</title>\n"
                     + "</head>\n"
                     + "<body>\n");

            out.println("<table width=846 border=0>");
            out.println("<tr>");
            out.println("<td width=14 rowspan=3></td>");
            out.println("<td width=736 colspan=2 height=15>");
            out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Individuals - File Import</b></center>");
            out.println("</td></tr>");
            out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
            out.println("</tr></table>");

            out.println("<form name=\"FORM1\" action=\"" +
                     getServletPath("impInd/import?fname="+fname+"&isid="+isid+"&ifid="+ifid)+
                     "\" method=\"post\"><table border=0>");

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
                           resultSet.getString("SUID") + "\">" + resultSet.getString("NAME"));
                }
                else
                {
                    out.println("<option value=\"" + resultSet.getString("SUID") + 
                           "\">" + resultSet.getString("NAME"));
                }
            }
            out.println("</select></td></tr>");
            
            out.println("<tr><td nowrap align=right>File</td>"
                     + "<td>"+fname+"</td></tr>");
                     //style=\"WIDTH: 350px\"

            out.println("<tr><td align=right>Create new");
            out.println("<td><input type=radio value=no name=update CHECKED>");
            out.println("<tr><td align=right>Update existing");
            out.println("<td><input type=radio value=yes name=update>");

            out.println("<tr><td align=right>Create & Update ");
            out.println("<td><input type=radio value=both name=update>");

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
     * Take the form data from propertiesPage and start a process of 
     * importing the new file.
     *
     * @param request The request object to use.
     * @param response The response object to use.
     */
    private void propertiesPagePost(HttpServletRequest request,
                           HttpServletResponse response)
    {
        Connection connection = null;
        HttpSession session = request.getSession(true);

        response.setContentType("text/html");
        response.setHeader("Pragme", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        
        String pid = (String)session.getAttribute("PID");
        String isid = request.getParameter("isid");

        boolean isOk = true;
        String errMessage = null;
        String sampleUnitIdAsStr = null;
        try
        {
            // Get parameters
            String userId = (String) session.getValue("UserID");
            int sampleUnitId = Integer.parseInt(request.getParameter("suid"));
            sampleUnitIdAsStr = request.getParameter("suid");
            connection = (Connection) session.getValue("conn");
            connection.setAutoCommit(false);
         
            String systemFileName = request.getParameter("fname");
            String upPath = getUpFilePath();
            String update = request.getParameter("update");
            
            

            // Create the individual 
            DbIndividual dbIndividual = new DbIndividual();
            FileParser fileParser = new FileParser(upPath + "/" + pid + "/" + isid + "/" + systemFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.INDIVIDUAL,
                                                                        FileTypeDefinition.LIST));

            // If add mode
            if (update == null || update.equalsIgnoreCase("no"))
            {
               dbIndividual.CreateIndividuals(fileParser, connection,
                                              sampleUnitId,
                                              Integer.parseInt(userId));
            }

            // If update mode
            else if (update.equalsIgnoreCase("yes"))
            {
               dbIndividual.UpdateIndividuals(fileParser, connection,
                                              sampleUnitId,
                                              Integer.parseInt(userId)); 
            }

            // if both update and add
            else if (update.equalsIgnoreCase("both"))
            {
               dbIndividual.CreateOrUpdateIndividuals(fileParser,
                                                      connection,
                                                      sampleUnitId,
                                                      Integer.parseInt(userId));
            }

            // Get the error message from the database object. If it is set an
            // error occured during the operation so an error is thrown.
            errMessage = dbIndividual.getErrorMessage();
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

        // If commit/rollback was ok and if the database operation was ok,
        // set the redirect page.
        if (commitOrRollback(connection, request, response,
                           "Individuals.FileImport.Send", errMessage,
                           "impInd", isOk)
            && isOk)
        {
            try
            {
                response.sendRedirect(getServletPath("viewInd?&suid=") + sampleUnitIdAsStr +
                                  "&ACTION=DISPLAY"); 
            }
            catch (Exception e)
            {
                e.printStackTrace(System.err);
            }
        }        
    }
}

