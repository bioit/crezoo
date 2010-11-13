/*
  $Log$
  Revision 1.59  2005/04/04 13:58:09  heto
  Commit before merging ant-scripts

  Revision 1.58  2005/03/03 15:41:37  heto
  Converting for using PostgreSQL

  Revision 1.57  2005/02/04 15:58:40  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.56  2005/01/31 16:16:40  heto
  Changing database to PostgreSQL. Problems with counts and selection buttons...

  Revision 1.55  2005/01/31 12:59:20  heto
  Making stronger separation of the import modules.

  Revision 1.54  2004/05/11 08:58:22  wali
  Bug fix

  Revision 1.53  2004/04/30 12:02:39  wali
  bugfix

  Revision 1.52  2004/04/28 08:52:26  wali
  Extended with species name in the import and check views.

  Revision 1.51  2004/04/27 06:18:41  wali
  Added functionality for uvariableset

  Revision 1.50  2004/04/23 09:52:53  wali
  Adopted for Species manipulations, mainly in impPropPost, which is splitted to impPropPostSU and inpPropPostSpecies, and impCommitPost.

  Revision 1.49  2004/04/20 08:41:27  wali
  Changed the "Import - Check - Check all files" view to be object name dependent. Name and species were added. The StructureFile method was implemented to sort the files according to features to display the right information.

  Revision 1.48  2004/04/02 14:29:48  heto
  Transaction was incorrect

  Revision 1.47  2004/04/02 07:12:58  wali
  bugfix

  Revision 1.46  2004/03/29 13:48:52  wali
  Bugfix, comments added on the commit page.

  Revision 1.45  2004/03/26 15:08:40  wali
  small bugfix

  Revision 1.44  2004/03/26 10:28:24  wali
  Changed the writeErrorPage in impPropPost. Size displays b and Kb, changed "Import Files - Files - Check" to "Import - Check - Check All files"

  Revision 1.42  2004/03/25 13:47:05  wali
  bug fix

  Revision 1.41  2004/03/25 13:40:30  wali
  conflict solved

  Revision 1.40  2004/03/25 13:31:35  heto
  Fixed conflict. Lock removed

  Revision 1.39  2004/03/25 13:21:34  wali
  details taken away

  Revision 1.38  2004/03/24 07:42:10  wali
  writeImport displays the files now, not the file set. The back buttons works now. Some information added in some views.

  Revision 1.37  2004/03/22 12:20:58  wali
  Displays files in the import view. Some other bug fixes.

  Revision 1.36  2004/03/19 13:08:32  wali
  Looked at a popup window as help function.

  Revision 1.35  2004/03/18 13:23:01  heto
  Added debug message

  Revision 1.34  2004/03/18 12:16:41  wali
  Corrected query string path

  Revision 1.33  2004/03/18 11:42:32  wali
  Fixed buggs regarding displaying comment on the Import - Details - Files page and Query string parameters.

  Revision 1.32  2004/03/18 10:36:21  heto
  Changed status message

  Revision 1.31  2004/03/18 08:26:32  wali
  New trial

  Revision 1.30  2004/03/18 08:19:54  wali
  Fixed comeFrom debug in importFile, writeFiles and changed the layout in upload files.

  Revision 1.29  2004/03/17 16:01:25  wali
  Debugged the check, details and import views.

  Revision 1.28  2004/03/17 13:16:49  wali
  stupid conflict

  Revision 1.27  2004/03/17 13:12:33  heto
  Removed session data

  Revision 1.26  2004/03/17 11:17:55  wali
  Added writeImport, changed the interface a bit.

  Revision 1.25  2004/03/17 07:27:21  heto
  Changed table name to import_set

  Revision 1.24  2004/03/16 10:25:10  wali
  Added "Date from" and "Date to" in writeTop, buildFilter and buildQS, writeTopScript was implemented to chech the dates. Changed "Name" to "File set". Implemented writeDetails. Added Created (creation date) in writeMiddle and added a back button in the Upload view.

  Revision 1.23  2004/03/09 14:20:22  heto
  Removed the "confirm" window
  Messages in "pre" clauses in html

  Revision 1.22  2004/03/09 09:59:36  heto
  Remade the function to return the message. HTML separated to servlet

  Revision 1.21  2004/03/03 08:50:56  wali
  Added a back button on the "check files" page. Changed the path to the back button that occurs when the files cannot be found when download is clicked.

  Revision 1.20  2004/03/01 12:23:10  wali
  Changed analyses to Import/export

  Revision 1.19  2004/02/18 14:20:24  heto
  Create the datadirs before import

  Revision 1.18  2004/02/16 15:53:27  heto
  Commented file.
  Refined the UI for Check Properties.

  Revision 1.17  2004/02/13 14:40:07  heto
  Added column in query for the Checked file

  Revision 1.16  2004/02/12 10:24:49  heto
  Comments fixed
  Adding support for blob operation

  Revision 1.15  2003/11/05 07:43:27  heto
  Added debug text

  Revision 1.14  2003/05/15 06:36:28  heto
  Added abort method.

  Revision 1.13  2003/05/09 14:50:56  heto
  Check process is integrated to the importProcess

  Revision 1.12  2003/05/09 08:46:21  heto
  Working on a status message system.

  Revision 1.11  2003/05/08 13:01:31  heto
  Pages for check-system added

  Revision 1.10  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.9  2003/04/29 11:43:11  heto
  Modified the user interface. Fexed the tables, added new stylesheet.

  Revision 1.8  2003/04/28 15:15:05  heto
  Removed sampling unit box from upload page

  Revision 1.7  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.6  2003/04/25 09:01:01  heto
  Fixes in html.
  Adding messages to log.
  Adding status message to gui.

  Revision 1.5  2003/01/15 09:53:49  heto
  *** empty log message ***

  Revision 1.4  2002/12/20 09:15:46  heto
  Adding support for importing files to the db in the background.

  Revision 1.3  2002/12/13 15:02:14  heto
  Added comments
  Building on paralel import.

  Revision 1.2  2002/11/27 09:38:11  heto
  Added comment field

  Revision 1.1  2002/11/21 10:51:11  heto
  New framework for importing / uploading files

  Revision 1.1  2002/11/13 09:03:06  heto
  File created from viewGeno/impFile
  New paralell version.

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
import se.arexis.agdb.db.TableClasses.SamplingUnit;
import se.arexis.agdb.db.TableClasses.Species;

/**
 * This class handles the basics of file imports to the database.
 * A file should be uploaded to the database, then an user should start a
 * process of importing the data from the file to the database.
 */
public class importFile extends SecureArexisServlet
{
    /** This string is printed to the webpage. */
    private static String line = "------------------------------------------------------------------------------------------------------------------------------";
    
    /** Prints the page used for importing genotypes from a file.
     *
     * @param request The request object to use.
     * @param response The response object to use.
     * @throws ServletException Throws ServletException
     *
     * @exception IOException If a writer could not be created.
     */
    public void doGet(HttpServletRequest request,
                     HttpServletResponse response)
        throws IOException, ServletException
    {
        HttpSession session = request.getSession(true);
        response.setContentType("text/html");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");

        //PrintWriter out = response.getWriter();
        String extPath = request.getPathInfo();
      
          
        if (extPath == null || extPath.equals("") || extPath.equals("/")) 
        {
            writeFrame(request,response);
        }
        else if (extPath.equals("/top")) 
        {
            writeTop(request, response);
        } 
        else if (extPath.equals("/edit"))
        {
            writeEdit(request,response);
        }
        else if (extPath.equals("/files")) 
        {
            writeFiles(request, response);
        }
        else if (extPath.equals("/details")) 
        {
            writeDetails(request, response);
        }
        else if (extPath.equals("/bottom")) 
        {
            writeBottom(request, response);
        } 
        else if (extPath.equals("/middle")) 
        {
            writeMiddle(request, response);
        }
        else if (extPath.equals("/upload"))
        {
            writePage(request,response);
        }
         else if (extPath.equals("/import"))
        {
            writeImport(request,response);
        }
        else if (extPath.equals("/impMultipart")) 
        {
            getUploadFile(request,response);
        }
        else if (extPath.equals("/download")) 
        {
            sendFile(request, response);
        }
        else if (extPath.equals("/impProp")) 
        {
            impProp(request,response);
        }
        else if (extPath.equals("/impPropPost"))
        {
            impPropPost(request,response);
        }
        else if (extPath.equals("/impCommit")) 
        {
            //propertiesPage(request, response);
            impCommit(request,response);
        }
        else if (extPath.equals("/impCommitPost")) 
        {
            //propertiesPage(request, response);
            impCommitPost(request,response);
        }
        
        
        else if (extPath.equals("/viewMessage"))
        {
            viewMessagePage(request, response);
        }
        else if (extPath.equals("/help"))
        {
            writeHelp(request, response);
        }
        else if (extPath.equals("/abort"))
            abort(request,response);
        /*
        else if (extPath.equals("/checkPage"))
        {
            writeCheckPage(request, response);
        }
        else if (extPath.equals("/startCheck"))
        {
            startCheck(request, response);
        }
         */
        
    }

    /** The post method should be calling the get method.
     * @param request The request object to use.
     * @param response The response object to use.
     * @throws IOException Throws IOException
     * @throws ServletException Throws ServletException
     */
   public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
                      throws IOException, ServletException
   {
       doGet(request,response);
   }
   
   /*
   public int NoOfSU()
   {
        return SU;   
   }
    */
   
   /** Write the frame for all other pages to be visible in.
    *
    * The frame is built according to the following
    * schema.
    *
    * ************************
    * * TOP                  *
    * ************************
    * * MIDDLE               *
    * *                      *
    * ************************
    * * BOTTOM               *
    * *                      *
    * ************************
    *
    * The top is the menu frame
    *
    * The middle part is the header of the data fields.
    *
    * The bottom part is the data table.
    *
    * @param req The request object
    * @param res The response object
    * @throws ServletException Throws ServletException
    * @throws IOException Throws IOException
    */
    private void writeFrame(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException 
    {
        HttpSession session = req.getSession(true);
        // set content type and other response header fields first
        res.setContentType("text/html");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Cache-Control", "no-cache");

        PrintWriter out = res.getWriter();
        try 
        {
            checkRedirectStatus(req,res);
            
            String topQS = buildQS(req);
            // we don't want the oper parameter anaywhere but in the "edit" and "new" page!
            topQS = removeQSParameterOper(topQS);
            String bottomQS = topQS.toString();

            out.println("<html>"
                     + "<HEAD>"
                     + " <TITLE>Import Files - Upload</TITLE>"
                     + "</HEAD>"
                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" //
                     + "<frame name=\"filetop\" "
                     + "src=\""+ getServletPath("importFile/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"filemiddle\" "
                     + "src=\""+ getServletPath("importFile/middle?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"filebottom\""
                     + "src=\"" +getServletPath("importFile/bottom?") + bottomQS + "\" "
                     + " scrolling=\"auto\" marginheight=\"0\" frameborder=\"0\"></frameset>"
                     + "<noframes><body><p>"
                     + "This page uses frames, but your browser doesn't support them."
                     + "</p></body></noframes></frameset>"
                     + "</HTML>");
        } 
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
        finally 
        {
        }
    }
    
    /** 
     * Write upload HTML page.
     *
     * @param request The request object
     * @param response The response object
     *
     * @throws IOException Trows IOException
     *
     * Servlet path: /upload
     *
     */
   private void writePage(HttpServletRequest request,
                     HttpServletResponse response)
      throws IOException
   {
      HttpSession session = request.getSession(true);
      PrintWriter out = response.getWriter();
      Connection connection = (Connection) session.getAttribute("conn");

      Statement sqlStatement = null;
      ResultSet resultSet = null;

      
      try
      {
         //connection = (Connection) session.getAttribute("conn");
         String projectId = (String) session.getAttribute("PID");

         out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                     "Transitional//EN\"");
         out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");

         out.println("<html>");
         
        // writeImportStatus(out);
         
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));

         out.println("<title>Import - Upload File</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=0>" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Import - Upload File</b></center>" +
                     "</td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post enctype=\"multipart/form-data\" action=\"" +
                     getServletPath("importFile/impMultipart") + "\">");
         out.println("<table border=0>");
         //out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         //out.println("</tr>");
         /*
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

          */
         
         
         // Import Session Name
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td>File set name<br>");
         out.println("<input type=text name=isname " +
                     "style=\"WIDTH: 350px\">");
         out.println("</td></tr>");
         
         // Comment field
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td>Comment<br>");
         out.println("<textarea name=comment rows=6 cols=40></textarea>");
         out.println("</td></tr>");
         
         // File 1
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td>File<br>");
         out.println("<input type=file name=filename size=40>"); // +
         out.println("</td></tr>");

         // File 2
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td><input type=file name=filename2 size=40>"); // +
         out.println("</td></tr>");

         // File 3
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td><input type=file name=filename3 size=40>"); // +
         out.println("</td></tr>");
         
         // File 4
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td><input type=file name=filename4 size=40>"); // +
         out.println("</td></tr>");
         
         // File 5
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td><input type=file name=filename5 size=40>"); // +
         out.println("</td></tr>");
         
         // File 6
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td><input type=file name=filename6 size=40>"); // +
         out.println("</td></tr>");
         
         // File 7
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td><input type=file name=filename7 size=40>"); // +
         out.println("</td></tr>");
         
         // File 8
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td><input type=file name=filename8 size=40>"); // +
         out.println("</td></tr>");
         
         // File 9
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td><input type=file name=filename9 size=40>"); // +
         out.println("</td></tr>");
         
         // File 10
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         out.println("<td><input type=file name=filename10 size=40>"); // +
         out.println("</td></tr>");

         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td></td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         
         
         out.println("<table border=0><tr>");
         out.println("<td>");


         out.println("<input type=button value=\"Upload Files\" " +
                     "style=\"WIDTH: 100px\" onClick='document.forms[0].submit()'>");
         out.println("</td></tr>");
         
         out.println("<tr><td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("importFile?&RETURNING=YES")+ "\"'>&nbsp;");
         
         out.println("&nbsp;</td></tr></table>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("</form>");
         
         out.println("<p>The uploaded files will be assigned to " 
            +"your current project. The files will be readable " 
            +"and importable to all with permission to the project.</p>");
         
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
   
   /** Write the top HTML page for this servlet.
    * @param req The request object
    * @param res The response object
    * @throws ServletException Throws a ServletException
    * @throws IOException Throws an IOException
    */   
    public void writeTop(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException 
    {
        
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
        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        String status_array[] = {"*","UPLOADED","CHECKING", "CHECKED", "IMPORTING", "IMPORTED", "WARNING", "ERROR"};
        int noOfStatus = status_array.length;
      
        int startIndex = 0, rows = 0, maxRows = 0;
        String pid, name, mode, type, status, orderby, oldQS, newQS, action,
        d_from, d_to;
        try 
        {
            conn = (Connection) session.getAttribute("conn");
            pid = (String) session.getAttribute("PID");
            name = req.getParameter("name");
            //mode = req.getParameter("mode");
            //type = req.getParameter("type");
            status = req.getParameter("status");
            action = req.getParameter("ACTION");
            oldQS = req.getQueryString();
            newQS = buildTopQS(oldQS);
            orderby = req.getParameter("ORDERBY");
            d_from = req.getParameter("D_FROM");
            d_to = req.getParameter("D_TO");
            if (req.getParameter("STARTINDEX") != null)
                startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
            else
                startIndex = 1;
            if (req.getParameter("ROWS") != null)
                rows = Integer.parseInt(req.getParameter("ROWS"));
            else
                rows = 0;
            
            if (name == null) name = "";
            if(status==null) status="";
            
            out.println("<html>");
            out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+ getURL("style/view.css") +"\">");
            out.println("<base target=\"content\">");

            //to check if the date is given correctly
            writeTopScript(out);
            out.println(getDateValidationScript());
            
            out.println("<title>Files</title>");
            out.println("</head>");

            out.println("<body bgcolor=\"#ffffd0\">");
            out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
            out.println("<form method=get action=\"" + getServletPath("importFile") +"\">");
            out.println("<p align=\"center\">" +
            //out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Import - View & Import</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr><tr><td width=\"517\">");
            // Name
            out.println("<table width=488 height=\"92\">" +
                     "<td><b>File set</b><br><input name=name width=100 style=\"WIDTH: 126px\" " +
                     "value=\"" + name + "\">");
            out.println("</td>");

           //Status 
           out.println("<td><b>Status</b><br><select name=status "
            +" onChange='selChanged(\"status\")' style=\"HEIGHT: 22px; WIDTH: 126px\">");
     
           for(int i=0; i<noOfStatus; i++){
              if(status.compareTo(status_array[i])==0)
                  out.println("<option selected value=\""+status+"\">"+ status + "</option>");  
              else 
                  out.println("<option value=\""+status_array[i]+"\">"+status_array[i]+"</option>");
            }
            out.println("</select></td>"); 
            
            //Date from
             out.println("<td><b>Date from (updated):</b><br>"
            +"<input id=D_FROM name=D_FROM value=\"" + replaceNull(d_from, "") + "\" style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\"></td>");
            //Date to
             out.println("<td><b>Date to (updated):</b><br>"
            + "<input id=D_TO name=D_TO value=\"" + replaceNull(d_to, "") + "\"style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\"></td></tr>");     
            //Help button
          //  out.println("<tr><td><a href='#' onClick=\"window.open('help?', "
            //        +"'Title','toolbar=no,scrollbars=yes,resizable=yes,width=500,height=200');\"><font color=black>HELP</font></a></td></td></tr>");
            
            out.println("<tr>");
            out.println("<td><b>&nbsp;</b><br>");
            out.println("&nbsp;");
            out.println("</td>");
            out.println("<td><b>&nbsp;</b><br></td>");
            out.println("<td><b>&nbsp;</b><br></td></table></td>");

            //Buttons
            out.println("<td width=219>");
            out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
            out.println("<td colspan=4>");
            out.println("&nbsp;");
            //      out.println("<input type=button value=\"New filter\"" +
            //        " onClick='parent.location.href=\"" + getServletPath("viewFilt/new?") + newQS + "\"' " +
            //        "height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">" +
            //        "</td>");
            out.println("</td>");

            out.println("<tr><td width=68 colspan=2>" +
                     "<input id=COUNT name=COUNT type=button value=\"Count\" width=\"69\"" +
                     " onClick ='valForm(\"COUNT\")' height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">" +
                     "</td>");
            out.println("<td width=68 colspan=2>" +
                     "<input id=DISPLAY name=DISPLAY type=button value=\"Display\"" +
                     " onClick ='valForm(\"DISPLAY\")' width=\"70\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">" +
                     "</td></tr>");


            // some hidden values
            out.println("<input type=\"hidden\" id=\"STARTINDEX\" name=\"STARTINDEX\" value=\"" + startIndex + "\">");
            out.println("<input type=\"hidden\" id=\"ORDERBY\" name=\"ORDERBY\" value=\"" + orderby + "\">");
            out.println("<input type=\"hidden\" id=\"oper\" name=\"oper\" value=\"\">");
            out.println("<input type=\"hidden\" id=\"ACTION\" name=ACTION value=\"NOP\">");
            
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
                if (rset != null) rset.close();
                if (stmt != null) stmt.close();
            } 
            catch (SQLException ignored) 
            {}
        }
    }
    
    /**
     * 
     * @param request 
     * @param response 
     * @throws java.io.IOException 
     */
    private void writeHelp(HttpServletRequest request,
                             HttpServletResponse response) 
                             throws IOException
   {
    
      HttpSession session = request.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      response.setContentType("text/html");
      response.setHeader("Cache-Control", "no-cache");
      response.setHeader("Pragma", "no-cache");
      PrintWriter out = response.getWriter();
  
     
      
          String titleString = "HELP!";
          HTMLWriter.doctype(out);
          HTMLWriter.openHTML(out);
          HTMLWriter.openHEAD(out, titleString);
          HTMLWriter.closeHEAD(out);
          HTMLWriter.openBODY(out,"");
         
          out.println("<TABLE cellSpacing=0 cellPadding=4 border=0 >");
          out.println("<TR><TD><textarea rows=10 cols=60 value=>First the files has to be uploaded and "+
          "all the files that are uploaded at the same time are stored togehter in a file set."+
          " Before the file set can be imported to the database, it has to be checked and all the files "+
          "in the file set has to pass the check to enable the file set to be imported."+
          " Only one file set can be checked and imported at the time, that is, "+
          " the checked file set has to be imported before any other file set can be "+
          " checked or imported.</textarea></TD></TR>");
          out.println("</TABLE>");
          HTMLWriter.closeBODY(out);
          HTMLWriter.closeHTML(out);
     
      
      
      }
    
    
    /**
     * 
     * @param out 
     */
    private void writeTopScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 0;");
      out.println("	if ('DISPLAY' == action.toUpperCase() || 'COUNT' == action.toUpperCase()) {");
      out.println("         if (document.forms[0].D_FROM.value != '') {");
      out.println("               if (!valDate(document.forms[0].D_FROM)) {");
      out.println("			rc = 1;");
      out.println("	          }");  
      out.println("           }"); 
      out.println("         if (document.forms[0].D_TO.value != '') {");
      out.println("               if (!valDate(document.forms[0].D_TO)) {");
      out.println("			rc = 1;");
      out.println("	          }");  
      out.println("           }"); 
      out.println("	");
      out.println("	}");
      out.println("	if (rc == 0) {");
      out.println("             document.forms[0].ACTION.value=action;");
      out.println("		document.forms[0].submit();");
      out.println("		return true;");
      out.println("	}");
      out.println("	return false;");
      out.println("	");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
   }
    
    /** The middle frame (contains header for the result-table)
     * @param req The request object
     *
     * @param res The response object
     * @throws ServletException Throws ServletException
     *
     * @throws IOException Throws IOException
     *
     */
   private void writeMiddle(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException 
   {
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

        maxRows = getMaxRows(session);


      try { 
         out.println("<html>\n<head>\n<link rel=\"stylesheet\" " +
                     "type=\"text/css\" href=\""+getURL("style/tableBar.css")+"\">");
         out.println("<base target=\"content\">");
         out.println("</head>");
         out.println("<body>");
         if(action != null)
         {
            out.println("&nbsp;" +buildInfoLine(action, startIndex, rows, maxRows));
         }
         
         String oldQS, newQS;
         oldQS = req.getQueryString();
         String choosen = req.getParameter("ORDERBY");
         newQS = buildTopQS(oldQS);

         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=830 style=\"margin-left:2px\">");
         
         out.println("<td width=5></td>");

         // the menu choices
         // Name
         out.println("<td width=150><a href=\"" +
                     getServletPath("importFile")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\">");
         if(choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown><b>File set</b></FONT></a></td>\n");
         else
            out.println("File set</a></td>\n");
         
       
          // Comment
         out.println("<td width=150><a href=\"" + getServletPath("importFile")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\">");
         if(choosen.equals("COMM"))
            out.println("<FONT color=saddlebrown><b>Comments</b></FONT></a></td>\n");
         else out.println("Comment</a></td>\n");
         // Status
         out.println("<td width=85><a href=\"" + getServletPath("importFile")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=STATUS\">");
         if(choosen.equals("STATUS"))
            out.println("<FONT color=saddlebrown><b>Status</b></FONT></a></td>\n");
         else out.println("Status</a></td>\n");
        //Updated 
         out.println("<td width=140><a href=\"" + getServletPath("importFile")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TS\">");
         if(choosen.equals("TS"))
            out.println("<FONT color=saddlebrown><b>Updated</b></FONT></a></td>\n");
         else out.println("Updated</a></td>\n");
         // USER
         out.println("<td width=50><a href=\"" + getServletPath("importFile")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
         else out.println("User</a></td>\n");
         //Uploaded
         out.println("<td width=140><a href=\"" + getServletPath("importFile")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("C_TS"))
            out.println("<FONT color=saddlebrown><b>Uploaded</b></FONT></a></td>\n");
         else out.println("Uploaded</a></td>\n");
        
         out.println("<td width=30>&nbsp;</td>");
         out.println("<td width=40>&nbsp;</td>");
         out.println("<td width=40>&nbsp;</td>");
         out.println("</table></table>");
         out.println("</body></html>");

      } 
      catch (Exception e)
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
   
   /**
    * Displayes the detailed page.
    * @param req 
    * @param res 
    * @throws ServletException 
    * @throws java.io.IOException 
    */
   
  
   private void writeDetails(HttpServletRequest req,
                             HttpServletResponse res) 
      throws ServletException, IOException
   {
         
         String isid = req.getParameter("isid");
         String comeFrom = req.getParameter("comeFrom");
         String fileSet = null;
         String comm = null;
         String status = null;
         String usr = null;
         String update = null;
         String uploaded = null;
         Statement stmt = null;
         ResultSet rset = null;
         Connection conn = null;
         HttpSession session = req.getSession(true);
  
         checkRedirectStatus(req, res);
         String oldQS = req.getQueryString();
       
         try { 
             
            conn = (Connection) session.getAttribute("conn");
            stmt = conn.createStatement();
            String SQL = "SELECT PID, ISID, STATUS, NAME, " +
                      "COMM, USR, to_char(TS, '" + getDateFormat(session) + "') as TC_TS, " +
                      " to_char(C_TS, '" + getDateFormat(session) + "') as C_TS " +
                      "FROM V_IMPORT_SET_2 WHERE ISID=" + isid;
          
            rset = stmt.executeQuery(SQL);

            if (rset.next()){
                fileSet = rset.getString("NAME");
                comm = rset.getString("COMM");
                status = rset.getString("STATUS");
                usr = rset.getString ("USR");
                uploaded = rset.getString("C_TS");
                update = rset.getString("TC_TS");
                status = rset.getString("STATUS");
            }    

         res.setContentType("text/html");
         res.setHeader("Pragma", "no-cache");
         res.setHeader("Cache-Control", "no-cache");
         PrintWriter out = res.getWriter();
         out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out.println("<html>\n"
                     + "<head>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
        
         
         //title
         out.println("<title>Details</title>\n"
                     + "<META HTTP_EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out.println("</head>\n<body>\n");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Import - Details</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         // the whole information table    
         out.println("<table border=0 cellspacing=0 cellpadding=0><tr>" +
                    "<td width=15></td><td></td></tr>");
        
         out.println("<tr><td></td><td>");
         
         // static data table
         out.println("<table nowrap border=0 cellSpacing=0>"); 
         out.println("<tr><td width=300 colspan=3 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
       
         //Imported files     
         out.println("<tr><td>File set</td><td>"+ fileSet+"</td>");
         if(isid!=null) // isid
            out.println("<td><a href=\"" + getServletPath("importFile/files?") + "comeFrom=details&" + oldQS + "\">Download</a></td></tr>");
         
         //Uploaded date
         out.println("<tr><td>Uploaded</td><td>" + formatOutput(session, uploaded, 25) + "</td></tr>");
         
         
         out.println("<tr><td></td><td></td></tr><tr><td></td><td></tr>"); 
         out.println("</table>"); 
    
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         
         // current data table
         out.println("<table nowrap align=left border=0 cellSpacing=0 width=840px>");
         out.println("<tr bgcolor=Black><td align=center colspan=5><b><font color=\"#ffffff\" >Current Data</font></b></td></tr>");
         out.println("<tr bgcolor= \"#008B8B\" >");
         out.println("<td nowrap WIDTH=110px>File set</td>");
         out.println("<td nowrap WIDTH=110px>Comment</td>");
         out.println("<td nowrap WIDTH=110px>Status</td>");
         out.println("<td nowrap WIDTH=50px>User</td>");
         out.println("<td nowrap WIDTH=120px>Updated</td>");
         
         out.println("<tr><td></td><td></td><td></td><td></td>"); 
         out.println("<tr bgcolor=white>");
         out.println("<td>" + formatOutput(session, fileSet, 12) + "</td>");
         out.println("<td>" + formatOutput(session, comm, 25) + "</td>");
         out.println("<td>" + formatOutput(session, status, 25) + "</td>");
         out.println("<td>" + formatOutput(session, usr, 12) + "</td>");
         out.println("<td>" + formatOutput(session, update, 18) + "</td></tr>");
         
         // Back button
         out.println("<tr><td></td><td></td></tr><tr><td></td><td></td></tr>");
         out.println("<form>");
         out.println("<tr>");
         out.println("<td>");
     
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("importFile?")+oldQS+"\"'>&nbsp;"); 
         out.println("</form>");
         out.println("</td></tr></table>"); //current data
         out.println("</td></tr></table>");  //information table
         out.println("</body></html>");
        
         }catch (SQLException e) {
            ;
        }
    }
   
   
   
   /** Build the Query String for the calls.
    *
    * @param req The request object
    *
    * @return Returns the new query string to be used.
    *
    */   
    private String buildQS(HttpServletRequest req) 
    {
        StringBuffer output = new StringBuffer(512);
        HttpSession session = req.getSession(true);
        Connection conn = (Connection) session.getAttribute("conn");
        String action = null, // For instance COUNT, DISPLAY, NEXT etc
        fgid,
        name,
        type,
        mode,
        status,
        orderby = null,
        dfrom = null,
        dto = null;
        boolean sid_changed = false;   
        fgid = req.getParameter("fgid");
        name = req.getParameter("name");
        type= req.getParameter("type");
        mode = req.getParameter("mode");
        status = req.getParameter("status");
        dfrom = req.getParameter("D_FROM");
        dto = req.getParameter("D_TO");
        // Find the requested action
        if ("DISPLAY".equalsIgnoreCase(req.getParameter("DISPLAY"))) 
        {
            action = "DISPLAY";
        } 
        else if ("COUNT".equalsIgnoreCase(req.getParameter("COUNT"))) 
        {
            action = "COUNT";
        } 
        else if ("<<".equalsIgnoreCase(req.getParameter("TOP"))) 
        {
            action = "TOP";
        } 
        else if ("<".equalsIgnoreCase(req.getParameter("PREV"))) 
        {
            action = "PREV";
        } 
        else if (">".equalsIgnoreCase(req.getParameter("NEXT"))) 
        {
            action = "NEXT";
        } 
        else if (">>".equalsIgnoreCase(req.getParameter("END"))) 
        {
            action = "END";
        } 
        else 
        {
            action = req.getParameter("ACTION");
            if (action == null) action = "NOP";
        }

        output.append("ACTION=").append(action);

        // Set the parameters STARTINDEX and ROWS
        if (!action.equals("NOP"))
            output.append(setIndecis(sid_changed, action, req, session));
        if (fgid != null && !fgid.trim().equals(""))
            output.append("&fgid=").append(fgid);
        if (name != null && !name.trim().equals(""))
            output.append("&name=").append(name);
        if (type != null && !type.trim().equals(""))
            output.append("&type=").append(type);
        if (mode != null && !mode.trim().equals(""))
            output.append("&mode=").append(mode);
        if (status != null && !status.trim().equals(""))
            output.append("&status=").append(status);
        if (dfrom != null && !dfrom.trim().equals(""))
            output.append("&D_FROM=").append(dfrom);
        if (dto != null && !dto.trim().equals(""))
            output.append("&D_TO=").append(dto);
        if (req.getParameter("oper") != null) {
            output.append("&oper=").append(req.getParameter("oper"));
    }
    //  output.append("&sid=").append(sid);
      orderby = req.getParameter("ORDERBY");
      if (orderby != null)
         output.append("&ORDERBY=").append(orderby);
      else
         output.append("&ORDERBY=NAME");
      return output.toString().replace('%', '*');
   }
   
    /** Adds parameters to the request string
     *
     * @param sid_changed ?
     * @param action The input of action could be < COUNT | DISPLAY | TOP | PREV | NEXT | END >
     * @param req The servlet request object
     * @param session The session object
     * @return Returns a query string with "startindex" and "rows" added.
     */    
  private String setIndecis(boolean sid_changed, String action, HttpServletRequest req, HttpSession session) 
  {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(req, session);
      maxRows = getMaxRows(session);

      if (req.getParameter("STARTINDEX") != null && !sid_changed) {
         startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
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
  
  /** Count the number of import sessions found with the filter in use.
   *
   * @param req The request object
   * @param session a session object
   *
   * @return Returns the number of rows found.
   */  
    private int countRows(HttpServletRequest req, HttpSession session) 
    {
      Connection conn = (Connection) session.getAttribute("conn");
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer(512);
      try 
      {
         sbSQL.append("SELECT count(distinct isid) " +
                      "FROM gdbadm.V_IMPORT_SET_2 WHERE ");
         sbSQL.append(buildFilter(req,false));
         stmt = conn.createStatement();
         rset = stmt.executeQuery(sbSQL.toString());
         rset.next();
         return rset.getInt(1);
      } catch (SQLException e) {
         e.printStackTrace(System.err);
         Errors.log("Error: SQL="+sbSQL);
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
    
    /** Filter request in view. This is entered by the user.
     * @param req The request parameter
     * @return a filter in SQL (a where clause)
     */
    private String buildFilter(HttpServletRequest req, boolean order) 
    {
        String pid,
         name,
         mode,
         type,
         status,
         dfrom,
         dto;
      StringBuffer filter = new StringBuffer(256);
      HttpSession session = req.getSession(true);
      pid = (String) session.getAttribute("PID");
      name = req.getParameter("name");
      //mode = req.getParameter("mode");
      //type = req.getParameter("type");
      status = req.getParameter("status");
      filter.append(" pid=" + pid);
      dfrom = req.getParameter("D_FROM");
      dto = req.getParameter("D_TO"); 
      if (name != null && !name.trim().equals(""))
         filter.append(" and name like '" + name + "'");
      
      if (dfrom!=null)
          if(dfrom.compareTo("") != 0)
             filter.append("and TS >= to_date('" + dfrom + "', 'YYYY-MM-DD')");
      
      if (dto!=null && !dto.trim().equals(""))
             filter.append("and TS < to_date('" + dto + "', 'YYYY-MM-DD')");
      
     //if (mode != null && !mode.trim().equals("") && !mode.equals("*"))
      //   filter.append(" and mode_='" + mode + "'");

      //if (type != null && !type.trim().equals("") && !type.equals("*"))
      //   filter.append(" and type='" + type + "'");

      
      if (status != null && !status.trim().equals("") && !status.equals("*") && !status.equals("no_value")) 
      {
          filter.append(" and status='"+status+"'");
      }
          
        /* if (status.equals("D")) 
         {
            // All analyses in which all the data files has been generated
            //filter.append(" and ISID NOT IN(SELECT ISID FROM V_IMPORT_FILES_1 WHERE " +
            //              " V_IMPORT_SESSION_2.ISID=ISID and upper(STATUS) != 'DONE')");
            filter.append(" and status='DONE'");
         } 
         else if (status.equals("I")) 
         {
            // All analyses that are still in progress. The criterium is that at least
            // one of the data files are still being generated, that is, the data file
            // status is not one of ("DONE", "ERROR")
            //filter.append(" and ISID IN (SELECT ISID FROM V_IMPORT_FILES_1 WHERE " +
            //              " V_IMPORT_SESSION_2.ISID=ISID AND upper(STATUS) NOT IN ('DONE', 'ERROR') )");
            filter.append(" and status!= 'DONE' and status!='ERROR'");
         } 
         else if (status.equals("E")) 
         {
            // All analyses where at least one of the data file generation failed.
            //filter.append(" and ISID IN (SELECT ISID FROM V_IMPORT_FILES_1 WHERE " +
            //              " V_IMPORT_SESSION_2.ISID=ISID AND upper(STATUS)='ERROR')");
            filter.append(" and status='ERROR'");
         }
      }*/
      
      /*
      if (order)
      {
          // Do something?
      }
       */
       
      // Replace every occurence of '*' with '%' and return the string
      // (Oracel uses '%' as wildcard while '%' demands some specail treatment
      // when passed in the query string)
      return filter.toString().replace('*', '%');
   }
   
    /** Build the Query string for the top frame
     *
     * @param oldQS The old Query String to be rebuilt
     *
     * @return Return the new Query string for the top frame
     *
     */    
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
      } else { // The query string didn't contain an ACTION-parameter
         sb.append(oldQS);
      }
      return sb.toString();
   }

   
   /**WARNING! This method is currently not implemented! What is the purpose 
    *of this??
    *
    * @param req The request object
    * @param res The response object
    * @return  returns an empty string.
    */   
   private String getTypeOptions(HttpServletRequest req, HttpServletResponse res) 
   {
       return "";
   }
   
   /** The infoline is the text describing how many hits a query gives.
    * Gives a readable string, a message to the user.
    * @param action What action was performed: < NEXT | PREV | TOP | END | COUNT | DISPLAY | NOP >
    * @param startIndex The start value of the rows displayed.
    * @param rows The number of rows that is beeing displayed.
    * @param maxRows The maximum number of rows to display.
    * @return Return a readable text to display in the middle frame.
    */   
   private String buildInfoLine(String action, int startIndex, int rows, int maxRows) 
   {
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
   
   /** Writes the list of data
    * @param req The request parameter
    * @param res The response parameter
    * @throws ServletException Throws ServletException
    * @throws IOException Throws IOException
    */
   private void writeBottom(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException 
   {
      HttpSession session = req.getSession(true);
      // set content type and other response header fields first
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      Statement stmt = null;
      ResultSet rset = null;
      Connection conn = null;
      StringBuffer sbSQL = new StringBuffer(512);
      try {
         String pid, action, orderby, fileSet="";
         int is_pid = 0;
         String oldQS = req.getQueryString();
         oldQS = removeQSParameterPid(oldQS);
         action = req.getParameter("ACTION");
         pid = (String) session.getAttribute("PID");
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
         } else if (action.equalsIgnoreCase("PREV"))
         {
            ;
         }

         out.println("<html>\n"
                     + "<head><link rel=\"stylesheet\" type=\"text/css\" href=\""
                     +getURL("style/bottom.css")+"\">\n"
                     + "<title>bottomFrame</title>\n"
                     + "</head>\n"
                     + "<body>\n");

         conn = (Connection) session.getAttribute("conn");
         stmt = conn.createStatement();
         
         sbSQL.append("SELECT PID, ISID, STATUS, NAME,    " +
                      "COMM, USR, to_char(TS, '" + getDateFormat(session) + "') as TS, " +
                      "to_char(C_TS, '" + getDateFormat(session) + "') as C_TS " +
                      "FROM V_IMPORT_SET_2 WHERE ");
        
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);

         sbSQL.append(" ORDER BY ").append(orderby);
         //out.println(sbSQL.toString());
         rset = stmt.executeQuery(sbSQL.toString());

         
         
         out.println("<TABLE align=left border=0 cellPadding=0");
         out.println("cellSpacing=0 width=830 style=\"margin-left:2px\">");
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
            out.println("<TD WIDTH=150>" + formatOutput(session, rset.getString("NAME"),18) +"</TD>");
            out.println("<TD WIDTH=150>" + formatOutput(session, rset.getString("COMM"), 25) + "</TD>"); // SIZE
            out.println("<TD WIDTH=85>" + formatOutput(session, rset.getString("STATUS"),10)+"</TD>");
            if("CHECKED".compareTo(rset.getString("STATUS").trim())==0){
                fileSet=rset.getString("NAME");
            }
         
            out.println("<TD WIDTH=140>" + formatOutput(session, rset.getString("TS"), 18) + "</TD>");
            out.println("<TD WIDTH=50>" + formatOutput(session, rset.getString("USR"), 6) + "</TD>");
            out.println("<td width=140>" + formatOutput(session, rset.getString("C_TS"), 18) + "</td>");
            
            out.println("<TD WIDTH=30><A HREF=\"" + getServletPath("importFile/edit?isid=") +
                        rset.getString("ISID") + "&" +
                        oldQS + "\" target=\"content\">Edit</A></TD>");
           
            out.println("<TD WIDTH=40><A HREF=\"" + getServletPath("importFile/files?isid=") +
                        rset.getString("ISID") + "&" +
                        oldQS + "\" target=\"content\">Check</A></TD>");
            String status= rset.getString("STATUS").trim();
            if(status.compareTo("CHECKED")==0 || status.compareTo("IMPORTING")==0 || status.compareTo("IMPORTED")==0)
                out.println("<TD WIDTH=40><A HREF=\"" + getServletPath("importFile/import?isid=") +
                        rset.getString("ISID") + "&" +
                        oldQS + "\" target=\"content\">Import</A></TD>");
            else
                out.println("<TD><p><font color=black>Import</font></p></TD>");
            out.println("</TR>");

            rowCount++;
         }
         stmt.close();
         rset.close();
         
         out.println("</TABLE>");
         out.println("<table><tr><td>&nbsp;</td></tr></table>");
        
         out.println("</body></html>");


      } 
      catch (Exception e)
      {
         out.println("<strong>Error in filter!</strong><br>");
         out.println("Error message: " + e.getMessage());
         out.println("<br>Modify filter according to message!</body></html>");
         e.printStackTrace(System.err);
         System.out.println(sbSQL);
      }
      finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (Exception ignored) {}
      }
   }
   
    /** Take an uploaded file. Store it on the webserver for later
     * import of data to the database.
     * @param request The request object for the servlet
     * @param response The response object
     * @throws IOException Throws an IOException
     * 
     * Servlet path: /impMultipart
     */
    private void getUploadFile(HttpServletRequest request,
        HttpServletResponse response)
        throws IOException
    {
       
        HttpSession session = request.getSession(true);
        PrintWriter out = response.getWriter();
        
        Connection connection = null;
       
        DbImportSet dbis = new DbImportSet();
        DbImportFile dbif = new DbImportFile();
        
        Vector fatalErrors= new Vector();
        //boolean errorFound = false;

        FileHeader header=null;
        FileWriter fileOut=null;
        String givenFileName = null;
        String systemFileName = null;
        String isname = null;
        String comment = null;
        
        int samplingUnitId=-1;
        int userId=-1;
       
        try
        {
            // Write a message to the user.
            HTMLWriter.writeErrorPage(out,"File upload started","");
            
            // Set the connection.
            connection = (Connection) session.getAttribute("conn");
            connection.setAutoCommit(false);
            Errors.logInfo("importFile.getUploadFile(...) setAutoCommit");
            
            // Get user and project info from the session.
            String pid = String.valueOf(session.getAttribute("PID"));
            String uid = String.valueOf(session.getAttribute("UserID"));
            
            
            
            /** Return the path to the files. 
             *  The upPath is the base of the upload
             */
            String upPath = getUpFilePath();
            
            // Create path if it does not exist
            createPath(upPath);
            
            // File size is limited to 6 Megabyte
            MultipartRequest multiRequest =
                new MultipartRequest(request, upPath, 6 * 1024 * 1024);
            
            //samplingUnitId = Integer.parseInt(multiRequest.getParameter("suid"));
            userId = Integer.parseInt((String) session.getAttribute("UserID"));
            
            // Get the ImportSetName
            isname = multiRequest.getParameter("isname");
            
            if ((isname == null) || (isname.trim().equals("")))
                throw new Exception("Import session name is invalid: "+isname);
            
            // Get the comment field
            comment = multiRequest.getParameter("comment");

            Enumeration files = multiRequest.getFileNames();
            
            // Create a new Import Session in the database.
            int isid = dbis.CreateImportSet(connection,isname,comment,pid,uid); 
            //out.println("ISID="+isid);
            
            // Return the path to the files. 
            // <base>/<pid>/<isid>/
            //String storePath = getUpFilePath()+"/"+pid+"/"+isid;
            
            // Create path if it does not exist
            //createPath(storePath);
            
            int ifid=0;
            String objectName="noObjectName";
            
            boolean unifiedOnly = false;
            boolean notUnified = false; 
            String [] Files = new String [10];
            int noOfFiles = 0;
            
            while (files.hasMoreElements())
            {

                givenFileName = (String) files.nextElement();
                systemFileName = multiRequest.getFilesystemName(givenFileName);
                
                if ((systemFileName == null) || (systemFileName.trim().equals("")))
                {
                    Errors.logError("importFile.getUploadFile(...): Filename is null");
                    //errorFound=true;
                    //-->fatalErrors.addElement(" Unable to get filename.");
                    //throw new Exception("File name is not OK.");
                }
                else
                {
                   
                   // out.println("File "+systemFileName+" is uploaded.<br>");
                    Files[noOfFiles] = systemFileName;
                    noOfFiles++;
                    
                    try
                    {
                        header = FileParser.scanFileHeader(upPath+"/"+systemFileName);
                        objectName = header.objectTypeName();
                    }
                    catch (Exception e)
                    {
                        objectName = "unknown";
                    }
                    
                  
                    File tempFile = new File(upPath+"/"+systemFileName);
                    String mimeType = this.getServletContext().getMimeType(upPath + "/" + systemFileName);
                  
                    // Store the file in the database
                    ifid = dbif.CreateImportFile2(connection,isid,upPath,systemFileName,mimeType,"Comment",uid,objectName);
                    
                    // Store the file header to the database.
                    ImportProcess ip = new ImportProcess();
                    ip.createHeaders(connection, ifid);

                    if(objectName.trim().compareTo("UVARIABLE")==0 || objectName.trim().compareTo("UMARKER")==0 || 
                       objectName.trim().compareTo("UVARIABLESET")==0 || objectName.trim().compareTo("UMARKERSET")==0 )
                        unifiedOnly=true;
                    else
                        notUnified=true;
                        
                    // Delete the temporary file. Check the status of the delete.
                    if (!tempFile.delete())
                    {
                        Errors.log("Delete failed: "+systemFileName);
                    }
                     
                }
            }
            
            if(unifiedOnly && notUnified){
                String errMessage = null;
                connection.rollback();
                dbis.DeleteImportSet(connection, isid);
                 
                errMessage = dbis.getErrorMessage();
                Assertion.assertMsg(errMessage == null ||
                          errMessage.trim().equals(""), errMessage);
                
                Errors.logInfo("importFile.getUploadFile(...) Rollback");
                HTMLWriter.writeErrorPage(out,"The files are not uploaded.<br> Unified markers, "+
                   "unified marker mapping, unified variable and unified variable mapping <br>" +
                   "cannot be uploaded in the same file set as other file formats.","");
                
            }
            else 
            {
                connection.commit();
                
                for(int j=0; j<noOfFiles; j++)
                    out.println("File "+Files[j]+" is uploaded.<br>");
                Errors.logInfo("importFile.getUploadFile(...) Commit");
                //out.println("Files uploaded");
                HTMLWriter.writeErrorPage(out,"All Files Uploaded","");
            }
           
        }
        catch (Exception e)
        {
            Errors.logDebug(e.getMessage());
            
            Errors.logWarn("Rollback");
            try
            {
                connection.rollback();
            }
            catch (Exception ignore)
            {}
            
            //out.println(e.getMessage());
            HTMLWriter.writeErrorPage(out,"File upload failed",e.getMessage());
              
        }
    }


  
   /**
    * Choose which type of edit page to write. < DELETE | UPDATE > else the same as UPDATE is selected.
    *
    * @param req The request object
    * @param res The response object 
    * @throws ServletException Throws Servlet Exception
    * @throws IOException Throws IOException
    */   
    private void writeEdit(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException 
    {
        HttpSession session = req.getSession(true);
        Connection conn =  null;

        String oper = req.getParameter("oper");
        conn = (Connection) session.getAttribute("conn");
        if (oper == null) oper = "";
        if (oper.equals("DELETE")) 
        {
            if (delete(req, res, conn))
                writeFrame(req, res);
        } 
        else if (oper.equals("UPDATE")) 
        {
            if (update(req, res, conn))
                writeEditPage(req, res);
        } 
        else
            writeEditPage(req, res);
    }
  
    /**
     * Write the edit page for importFiles.
     *
     * @param req The request object
     * @param res The response object
     * @throws ServletException Throws ServletExceptions
     * @throws IOException Throws IOExceptions.
     */    
    private void writeEditPage(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException 
    {
        HttpSession session = req.getSession(true);
        Connection conn = (Connection) session.getAttribute("conn");
        Statement stmt = null;
        ResultSet rset = null;
        res.setContentType("text/html");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Cache-Control", "no-cache");
        PrintWriter out = res.getWriter();
        String newQS, pid, isid, name, comm, oper, item;
        try 
        {
            conn = (Connection) session.getAttribute("conn");
            pid = (String) session.getAttribute("PID");
            isid = req.getParameter("isid");
            newQS = removeQSParameterOper(req.getQueryString());
            oper = req.getParameter("oper");

            if (oper == null || oper.trim().equals("")) oper = "SEL_CHANGED";
            item = req.getParameter("item");
            if (item == null || item.trim().equals("")) item = "";

            // Retrieve the data for this filter
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT ISID, NAME, COMM  " +
                                  "FROM V_IMPORT_SET_2 WHERE ISID=" + isid);
            if (!rset.next() ) 
            {
                // This should almost be impossible
                throw new Exception("Analysis query didn't return any data.");
            }
            name = rset.getString("NAME");
            comm = rset.getString("COMM");
            out.println("<html>");
            out.println("<head>");
            HTMLWriter.css(out,getURL("style/axDefault.css"));
            writeEditScript(out);

            out.println("<title>Edit analysis</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
            out.println("<tr>");
            out.println("<td width=14 rowspan=3></td>");
            out.println("<td width=736 colspan=2 height=15>");
            out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Import - Edit</b></center>");
            out.println("</td></tr>");
            out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
            out.println("</tr></table>");
            out.println("<form method=post action=\"" +
                getServletPath("importFile/edit?") + newQS + "\">");

            out.println("<table width=400 border=0 cellSpacing=0 cellPading=5>");
            out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
            out.println("<table>");
            out.println("<tr>");

            // Name
            out.println("<td>File set name<br>");
            out.println("<input type=text name=n value=\"" + name + "\" width=100 " +
                "style=\"WIDTH: 100px\" maxlength=20>");
            out.println("</td>");
            // Comment
            out.println("<td>Comment<br>");
            out.println("<input type=text name=c value=\"" + replaceNull(comm, "") + "\" width=200 " +
                "style=\"WIDTH: 200px\">");
            out.println("</td>");
            out.println("</tr>");
            // Some buttons
            out.println("<tr>");
            out.println("<td colspan=3>");
            out.println("<table border=0 cellspacing=0 cellpading=0>");
            out.println("<tr>");
            // Back button
            out.println("<td><input type=button value=\"Back\" " +
                     "width=70 style=\"WIDTH: 70px\" " +
                     "onClick='Javascript:location.href=\"" +
                     getServletPath("importFile?") + newQS + "\";'>&nbsp;</td>");
            out.println("<td><input type=button width=70 style=\"WIDTH: 70px\" " +
                     "value=\"Update\" onClick='valForm(\"UPDATE\");'>&nbsp;</td>");
            out.println("<td><input type=button value=\"Delete\" " +
                     "width=70 style=\"WIDTH: 70px\" " +
                     "onClick='valForm(\"DELETE\");'>&nbsp;</td>");
            out.println("</tr>");
            out.println("</table>");
            out.println("</td>");
            out.println("</tr>");
            out.println("</table>");

            out.println("</td></tr>");
            out.println("</table>");
            out.println("<input type=hidden name=item value=\"\">");
            out.println("<input type=hidden name=oper value=\"\">");
            out.println("</td></tr></table>");
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
                if (rset != null) rset.close();
                if (stmt != null) stmt.close();
            } 
            catch (SQLException ignored) 
            {
            }
        }
    }
   
  
   /**
    * Deletes a import session and the files associated.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if import session was removed.
    *         False if import session was not removed.
    */
   private boolean delete(HttpServletRequest request,
                          HttpServletResponse response,
                          Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      String absPath;
      int isid;
      int pid;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         //absPath = getFileGeneratePath();
         absPath = getUpFilePath();

         pid = Integer.parseInt((String) session.getAttribute("PID"));
         isid = Integer.parseInt(request.getParameter("isid"));

         if (!deleteFileObject(absPath + "/" + pid + "/" + isid))
         {
            errMessage = "Failed to delete file object " + absPath + "/" +
               pid + "/" + isid;
            throw new Exception();
         }
         DbImportSet dbis = new DbImportSet();
         dbis.DeleteImportSet(connection, isid);
         errMessage = dbis.getErrorMessage();
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
                       "Generations.Edit.Delete", errMessage,
                       "importFile/edit", isOk);
      return isOk;
   }

   /**
    * Updates a generation.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if generation was updated.
    *         False if generation was not updated.
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
         String pid;
         int isid;
         int id;
         String oldQS = request.getQueryString();
         name = request.getParameter("n");
         isid = Integer.parseInt(request.getParameter("isid"));
         comm = request.getParameter("c");
         id = Integer.parseInt((String) session.getAttribute("UserID"));
         DbImportSet dbis = new DbImportSet();
         dbis.UpdateImportSet(connection, name, comm, isid, id);
         errMessage = dbis.getErrorMessage();
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
                       "Generations.Edit.Update", errMessage, 
                       "importFile/edit", isOk);
      return isOk;
   }

   
   /**
    * Writes java-scripts to the client to handle reminders and confirmations
    * before deleting or updating values.
    *
    * @param out void
    */   
    private void writeEditScript(PrintWriter out) 
    {
        out.println("<script language=\"JavaScript\">");
        out.println("<!--");
        out.println("function valForm(action) {");
        out.println("	");
        out.println("	var rc = 1;");
        out.println("	if ('DELETE' == action.toUpperCase()) {");
        out.println("		if (confirm('Are you sure you want to delete the import session and all associated files?')) {");
        out.println("			document.forms[0].oper.value='DELETE';");
        out.println("			rc = 0;");
        out.println("		}");
        out.println("	");
        out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
        out.println("		if (confirm('Are you sure you want to update the import session?')) {");
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
    
    /** Write the page that displays the files available for a import session.
     * @param req The request object.
     * @param res The response object
     * @throws ServletException Throws ServletException
     * @throws IOException  Throws IOException
     *
     * Servlet Url: /files?isid=<isid>
     */
    private void writeFiles(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException 
    {
        HttpSession session = req.getSession(true);
        Connection conn = (Connection) session.getAttribute("conn");
        Statement stmt = null;
        ResultSet rset = null;
        res.setContentType("text/html");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Cache-Control", "no-cache");
        
        String path = req.getPathInfo();
      
        PrintWriter out = res.getWriter();
        String newQS, pid, isid, oper, item; 
     
        boolean allDone=true;
        boolean oneError=false;
        boolean noFiles = true;
        
        String isid_status = "";
        //boolean locked = true;
       
        try 
        {
            conn = (Connection) session.getAttribute("conn");
            pid = (String) session.getAttribute("PID");
            isid = req.getParameter("isid");
            newQS = removeQSParameterOper(req.getQueryString());
            newQS = removeQSParameter(newQS, "ifid");
            newQS = removeQSParameter(newQS, "isid");
            checkRedirectStatus(req, res);
        
            DbImportSet dbImp = new DbImportSet();
            
            // Check if a new import can be started.
            //locked      = dbImp.isLocked(conn, Integer.valueOf(isid).intValue());
            isid_status = dbImp.getStatus(conn,isid).trim();

            // Retrieve the data for this filter
            stmt = conn.createStatement();
            
            rset = stmt.executeQuery("SELECT V.ISID, V.IFID, V.NAME as F_NAME, V.STATUS as F_STATUS, "+ 
                                  "V.COMM, V.LEN, V.USR, to_char(V.TS, '" + getDateFormat(session) + "') as TS, " +  
                                  "I.NAME as IS_NAME, I.STATUS as IS_STATUS, to_char(I.CHK_TS, '" + getDateFormat(session) + "') as CHK_TS, " +
                                  "I.CHK_LEVEL, I.CHK_MODE, I.USR, I.SU_NAME, I.SP_NAME " +
                                  "FROM V_IMPORT_FILES_2 V, V_IMPORT_SET_3 I WHERE V.ISID=I.ISID AND V.ISID=" + isid);
            
            out.println("<html>");
            out.println("<head>");
            HTMLWriter.css(out,getURL("style/fileList.css"));
            out.println("<title>Files - selfupdating</title>");       
            
            Errors.log("isid_status=\""+isid_status+"\"");
            
            // Refresh
            if (isid_status.equals("CHECKING") || isid_status.equals("IMPORTING"))
                out.println("<META HTTP-EQUIV=\"REFRESH\" CONTENT=\"3; URL="+getServletPath("importFile/files?MODE=AUTO&isid="+isid)+"\">");
            
            out.println("</head>");
            out.println("<body>");
    
            out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
            out.println("<tr>");
            out.println("<td width=14 rowspan=3></td>");
            out.println("<td width=736 colspan=2 height=15>");
         
            out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Import - Check</b></center>");
          
            out.println("</td></tr>");
            out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
            out.println("</tr></table>");
   
            out.println("<form method=post action=\"" +
                     getServletPath("importFile/file?") + newQS + "\">");

            boolean odd = true;
            
            String name, status, usr, uploaded, updated, size;
            String is_name, is_status, chk_date, su, mode, level, spName;
            status = "";
            if(rset.next()){
                is_name = rset.getString("IS_NAME");
                is_status = rset.getString("IS_STATUS");
                chk_date = rset.getString("CHK_TS");
                su = rset.getString("SU_NAME");
                spName = rset.getString("SP_NAME");
                mode = rset.getString("CHK_MODE");
                level = rset.getString("CHK_LEVEL");
                if(chk_date==null) chk_date = "NOT CHECKED!";
                out.println("<table border=0 cellSpacing=0 cellPading=5>");
                out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
                
                //static data
                out.println("<table nowrap border=0 cellSpacing=0>"); 
                out.println("<tr><td width=300 colspan=3 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
       
                //File set     
                out.println("<tr><td>File set</td><td>"+ is_name+"</td></tr>");
                out.println("<tr><td>Status</td><td>"+ is_status+"</td>");
                out.println("<tr><td>Checked</td><td>"+ chk_date+"</td>");
                if(su!=null){
                    if(mode==null) mode = "";
                    if (mode.trim().equals("C"))
                        mode = "CREATE";
                    else if (mode.trim().equals("U"))
                        mode = "UPDATE";
                    else if (mode.trim().equals("CU"))
                        mode = "CREATE OR UPDATE";
                    out.println("<tr><td>Sampling unit</td><td>"+ su +"</td>");
                    out.println("<tr><td>Mode</td><td>"+ mode +"</td>");
                    if(level!=null)
                        out.println("<tr><td>Level</td><td>"+ level +"</td>");
                }
                if(spName!=null){
                    mode = "CREATE";
                    out.println("<tr><td>Species</td><td>"+ spName +"</td>");
                    out.println("<tr><td>Mode</td><td>"+ mode +"</td>");
                }
                    
                out.println("</table>"); 
                //static data
          
                out.println("</td></tr><tr><td></td></tr><tr><td></td></tr><tr><td></td><td>");
                //file data table
                out.println("<table border=0 cellpading=0 cellspacing=0>");
                out.println("<tr bgcolor=\"008B8B\">");
                out.println("<td width=200>File</td>");//th
                out.println("<td width=125>Status</td>");
                out.println("<td width=75>User</td>");
                out.println("<td width=75>Size</td>");
                out.println("<td width=150>Uploaded</td>");
                out.println("<td width=75>&nbsp;</td>");
                out.println("<td width=75>&nbsp;</td>");
                out.println("</tr>");

                
                do {
                    name    = rset.getString("F_NAME");
                    status  = rset.getString("F_STATUS");
                    usr     = rset.getString("USR");
                    uploaded = rset.getString("TS");
                    int len = rset.getInt("LEN");
                   
                    if(len>1024){
                        len = len/1024;
                        size = len + "Kb";
                    }
                    else
                        size = "" + len + "b";
                    
                    
                    if (status == null) status="";
                
                    if (odd)
                        out.println("<tr bgcolor=white>");
                    else
                        out.println("<tr bgcolor=lightgrey>");
                    odd = !odd;
                
                    //name
                    out.println("<td>" + formatOutput(session, name, 25) + "</td>");
                    
                
                    //status, the ERROR or WARNING can be clicked, if any
                    if((status.trim().compareTo("ERROR")==0 || status.trim().compareTo("WARNING")==0) )
                        out.println("<td><a href=\"" + getServletPath("importFile/download?") +
                        "ifid=" + rset.getString("ifid") + "&check=" + "true&" + newQS + "\">"+status+"</a></td>");
                    else    
                        out.println("<td>" + formatOutput(session, status, 10) + "</td>");
                
                    //user name
                    out.println("<td>" + formatOutput(session, usr, 10) + "</td>");
                
                    //size
                    out.println("<td>" + formatOutput(session, size, 10) + "</td>");
                    //uploaded
                    out.println("<td>"  + formatOutput(session, uploaded, 18)+ "</td>");
                
                    //download file
                    out.println("<td><a href=\"" + getServletPath("importFile/download?") +
                    "ifid=" + rset.getString("ifid") + "&" + newQS + "\">Download</a></td>");
                
                    out.println("<td><a href=\"" + getServletPath("importFile/viewMessage?") +
                           "ifid=" + rset.getString("ifid") + "&" + newQS + "\">Log</a></td>");         
                    out.println("<td>&nbsp;</td>");
                    out.println("</tr>");
                } while(rset.next());
            }
            out.println("</table>"); //file data table
            out.println("</td></tr>");
            
            out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
            
           
            // button table
           out.println("<table border=0 cellspacing=0 cellpading=0>");
          
           if (isid_status.equals("UPLOADED") || isid_status.equals("IMPORTED") || isid_status.equals("ERROR") || isid_status.equals("CHECKED"))
                {
                    out.println("<tr><td><input type=button value=\"Check all files\" " 
                        +" onClick='Javascript:location.href=\""
                        + getServletPath("importFile/impProp?isid=") + isid + "&" + newQS +"\";'></td></tr>");
                }

            // Back button
           out.println("<tr>");
           out.println("<td><input type=button value=\"Back\" " +
                        "width=70 style=\"WIDTH: 70px\" " +
                        "onClick='Javascript:location.href=\"" +
                        getServletPath("importFile/?") + newQS + "\";'>&nbsp;</td>");
         
            out.println("</tr>");
           
            out.println("</table>"); //button table
            
            out.println("</td></tr>");
            out.println("</table>");
    
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
                if (rset != null) rset.close();
                if (stmt != null) stmt.close();
            } 
            catch (SQLException ignored) 
            {
            }
        }
    }
    
    
    
        /**
         * 
         * @param req 
         * @param res 
         * @throws ServletException 
         * @throws java.io.IOException 
         */
        private void writeImport(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException 
    {
        HttpSession session = req.getSession(true);
        Connection conn = (Connection) session.getAttribute("conn");
        Statement stmt = null;
        ResultSet rset = null;
        res.setContentType("text/html");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Cache-Control", "no-cache");
        
        PrintWriter out = res.getWriter();
        String newQS, pid, isid, oper, item; 
       
        boolean allDone=true;
        boolean oneError=false;
        boolean noFiles = true;
        
        String isid_status = "";
        //boolean locked = true;
      
        try 
        {
            conn = (Connection) session.getAttribute("conn");
            pid = (String) session.getAttribute("PID");
            isid = req.getParameter("isid");
            newQS = req.getQueryString();
            //newQS = removeQSParameterOper(req.getQueryString());
            checkRedirectStatus(req, res);
    
            DbImportSet dbImp = new DbImportSet();
      
            // Check if a new import can be started.
            //locked      = dbImp.isLocked(conn, Integer.valueOf(isid).intValue());
            isid_status = dbImp.getStatus(conn,isid);

            String fileSet = null,
                   status = null,
                   comm = null,
                   usr = null,
                   edited = null,
                   uploaded = null,
                   suid = null,
                   spName =null,
                   level = null,
                   mode = null,
                   checked_date = null;
            
            // Retrieve the data for this filter 
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT NAME, STATUS , COMM, to_char(TS, '" + getDateFormat(session) + "') as TS, "+
                                  "to_char(C_TS, '" + getDateFormat(session) + "') as C_TS, CHK_SUID, SP_NAME, "+
                                  "CHK_LEVEL, CHK_MODE, to_char(CHK_TS, '" + getDateFormat(session) + "'), " +
                                  "to_char(CHK_TS, '" + getDateFormat(session) + "') as CHK_TS, USR, SU_NAME " +  
                                  "FROM V_IMPORT_SET_3 WHERE ISID=" + isid);
          
            if(rset.next()){
                fileSet = rset.getString("NAME");
                status = rset.getString("STATUS");
                comm = rset.getString("COMM");
                usr = rset.getString("USR");
                uploaded = rset.getString("C_TS");
                suid = rset.getString("SU_NAME");
                spName = rset.getString("SP_NAME");
                level = rset.getString("CHK_LEVEL");
                mode = rset.getString("CHK_MODE");
                checked_date = rset.getString("CHK_TS");
            }
        
            if(fileSet==null) fileSet = "-";
            if(status==null) status= "-";
            if(comm==null) comm = "-";
            if(usr==null) usr = "-";
            if(uploaded==null) uploaded = "-";
            //if(suid==null) suid = "-";
            //if(spName==null) spName = "-";
            if(level==null) level = "-";
            if(mode==null) mode = "-";
            if(checked_date==null) checked_date = "-";
          
            if(mode==null) mode="";
            if (mode.trim().equals("C"))
                mode = "CREATE";
            else if (mode.trim().equals("U"))
                mode = "UPDATE";
            else if (mode.trim().equals("CU"))
                mode = "CREATE OR UPDATE";
          
            out.println("<html>");
            out.println("<head>");
            HTMLWriter.css(out,getURL("style/fileList.css"));
            out.println("<title>Files - selfupdating</title>");       
            
            // Refresh
            if (isid_status.equals("CHECKING") || isid_status.equals("IMPORTING"))
                out.println("<META HTTP-EQUIV=\"REFRESH\" CONTENT=\"3; URL="+getServletPath("importFile/import?MODE=AUTO&isid="+isid)+"\">");
          
            out.println("</head>");
            out.println("<body>");
            
            // 846
            out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
            out.println("<tr>");
            out.println("<td width=14 rowspan=3></td>");
            out.println("<td width=736 colspan=2 height=15>");
            out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Import - Checked files</b></center>");
            out.println("</td></tr>");
            out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
            out.println("</tr></table>");
            
    
            out.println("<method=post action=\"" +
                     getServletPath("importFile/file?") + newQS + "\">");

            
             // the whole information table    
            out.println("<table border=0 cellspacing=0 cellpadding=0><tr>" +
                    "<td width=15></td><td></td></tr>"); 
            out.println("<tr><td></td><td>");
         
            // static data table
            out.println("<table nowrap border=0 cellSpacing=0>"); 
            out.println("<tr><td width=300 colspan=3 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
            
            if(fileSet.compareTo("-")==0)
                out.println("<tr><td width=300 colspan=2><font color=red>The files have not been checked!</font></td></tr>"); 
            out.println("<tr><td></td><td></td></tr>");
            out.println("<tr><td>File set</td><td>" + formatOutput(session, fileSet, 25) + "</td></tr>");
            out.println("<tr><td>Status</td><td>" + formatOutput(session, status, 25) + "</td></tr>");
            if(suid!=null)
            {
                out.println("<tr><td>Sampling unit</td><td>" + formatOutput(session, suid, 25) + "</td></tr>");
                
                if(level!="")
                    out.println("<tr><td>Level</td><td>" + formatOutput(session, level, 25) + "</td></tr>");
            }
            if(spName!=null){
                 out.println("<tr><td>Species</td><td>" + formatOutput(session, spName, 25) + "</td></tr>");  
                 mode = "CREATE";
            }
            out.println("<tr><td>Mode</td><td>" + formatOutput(session, mode, 25) + "</td></tr>");
            out.println("<tr><td>User</td><td>" + formatOutput(session, usr, 25) + "</td></tr>");
            out.println("<tr><td>Checked date</td><td>" + formatOutput(session, checked_date, 25) + "</td></tr>");
            
             
            out.println("<tr><td></td><td></td></tr><tr><td></td><td></tr>"); 
            out.println("</table>"); 
            //static data table
           
            stmt.close();
            rset.close();
            
            stmt = conn.createStatement(); 
            rset = stmt.executeQuery("SELECT IFID, NAME, STATUS , COMM, LEN, USR " + 
                                  "FROM V_IMPORT_FILES_2 WHERE ISID=" + isid);
           
            //file table
            out.println("<table border=0 cellSpacing=0 cellPading=5>");
            out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
            
            out.println("</td></tr><tr><td></td></tr><tr><td></td></tr><tr><td></td><td></td></tr>");
            out.println("<table border=0 cellpading=0 cellspacing=0>");
            out.println("<tr bgcolor=\"008B8B\">");
            out.println("<td width=150>Files</td>");
            out.println("<td width=125>File status</td>");
            out.println("<td width=125>Size</td>");
            out.println("<td width=100>User</td>");
            out.println("<td width=75>&nbsp;</td>");
            out.println("</tr>");
            
            int len;
            String size="";
            boolean odd = true;
            while (rset.next()) {
                    len = rset.getInt("LEN");
                   
                    if(len>1024){
                        len = len/1024;
                        size = len + "Kb";
                    }
                    else
                        size = "" + len + "b";
                if (odd)
                    out.println("<tr bgcolor=white>");
                else
                    out.println("<tr bgcolor=lightgrey>");
                
                out.println("<td>" + formatOutput(session, rset.getString("NAME"), 25) + "</td>");
                out.println("<td>" + formatOutput(session, rset.getString("STATUS"), 10) + "</td>");
                out.println("<td>" + formatOutput(session, size, 10) + "</td>");
                out.println("<td>" + formatOutput(session, rset.getString("USR"), 30) + "</td>");
                out.println("<td><a href=\"" + getServletPath("importFile/viewMessage?") +
                           "ifid=" + rset.getString("ifid") + "&" + newQS + "\">Log</a></td>");
               // out.println("<td></td>");
                odd = !odd;
           } 
            
            out.println("</table>");
            //file table
            out.println("</td></tr>"); 
            out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
           
            // Button table
            out.println("<table border=0 cellspacing=0 cellpading=0>");
            out.println("<tr><td></td></tr><tr><td></td></tr><tr><td></td></tr>"); 
            out.println("<tr><td></td></tr><tr><td></td></tr><tr><td></td></tr>");
           
            if (isid_status.trim().equals("CHECKED"))
            {
                out.println("<tr><td><input type=button value=\"Import all files\" " 
                    +" onClick='Javascript:location.href=\""
                    + getServletPath("importFile/impCommit?isid=") + isid + "&" + newQS+"\";'></td></tr>");
                
                out.println("<tr><td><input type=button value=\"Abort import\" " 
                    +" onClick='Javascript:location.href=\""
                    + getServletPath("importFile/abort?isid=") + isid +"\";'></td></tr>");
            }    
            
            // Back button
            out.println("<tr>");
            out.println("<td><input type=button value=\"Back\" " +
                     "width=70 style=\"WIDTH: 70px\" " +
                     "onClick='Javascript:location.href=\"" +
                 getServletPath("importFile/?&") + newQS+"\";'>&nbsp;</td>");  
          
            out.println("</tr>");
     
            out.println("</table>");  //button table
            out.println("</td></tr></table>");  //information table
            out.println("</td></tr>");
            out.println("</table>");
         
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
                if (rset != null) rset.close();
                if (stmt != null) stmt.close();
            } 
            catch (SQLException ignored) 
            {
            }
        }
    }
    
    
    
    /**
     * Send the file for download to the user that requested the file.
     * The file must be located in the database and written to 
     * the output stream.
     *
     * 
     * @param req The servlet request object
     * @param res The servlet response object
     *
     * Sevlet path: /download
     */    
    private void sendFile(HttpServletRequest req, HttpServletResponse res) 
    {
        Errors.logInfo("ImportFile.sendFile(...) started");
        String filename = "";
        String ifid;
        
        String contentType = null;
        String pid, isid = "";
        
        OutputStream out = null;
        
        Connection conn = null;
        HttpSession session = req.getSession(false);
        
        
        boolean check = false;
        String QS = ""; //used for error page
        try 
        {
            QS = req.getQueryString();
            ifid = req.getParameter("ifid");
            pid = (String) session.getAttribute("PID");
         
            String checkStr = req.getParameter("check");
            if (checkStr != null && checkStr.equals("true"))
                check = true;
            
            conn = (Connection) session.getAttribute("conn");
            
            // Test
            DbImportFile impfile = new DbImportFile();
            Errors.log(impfile.getImportFileHeader(conn, ifid));
            
            
            
            /* Print the file to webpage
             */
            if (contentType == null)
                    contentType = new String("text/plain");
            
            res.setContentType(contentType);

            // Set the header to get correct filename
            filename = impfile.getFileName(conn, ifid);
            res.setHeader("Content-Disposition", "inline; filename=" + filename);
            
            byte[] test;

            if (check)
                test = impfile.getCheckedFile(conn,Integer.valueOf(ifid).intValue());
            else
                test = impfile.getImportFile(conn,Integer.valueOf(ifid).intValue());
            
            out = res.getOutputStream();
            out.write(test);
                        
            Errors.logInfo("ImportFile.sendFile(...) ended.");
        }
        catch (Exception e) 
        {
            Errors.logError("importFile.sendFile: Blob not found in db: isid="+isid);
            e.printStackTrace(System.err);
            
            try
            {  
                writeErrorPage(req,res,"Import.FileNotFound","Error, file not found: "+ filename,"importFile/files?" + QS);                
                //writeErrorPage(req,res,"Import.FileNotFound","Error, file not found: "+ filename,"importFile");                
            }
            catch (Exception dontcare)
            {
                System.err.println(dontcare.getMessage());
            }
        }     
    }
    
  
    /**
     * Print the options for the import. 
     * This information will be used for how to import the data.
     * @param request The request object to use.
     * @param response The response object to use.
     * 
     * Old name : propertiesPage
     * @throws java.io.IOException 
     */
    private void impProp(HttpServletRequest request,
                           HttpServletResponse response)
        throws IOException        
    {
        ArrayList<ImportFileStruct> files = null;
        HttpSession session = request.getSession(true);
        Connection connection;        
        String projectId = null, samplingUnitId = null, sid = null;
        String QS = request.getQueryString();
        String strUser = (String) session.getValue("UserID");
        projectId = (String) session.getValue("PID");
        
        String isid = request.getParameter("isid");
        
        
        // set content type and other response header fields first
        response.setContentType("text/html");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        try
        {
            ImportProcess imp = new ImportProcess();
            
            connection = (Connection) session.getValue("conn");
            writeImpPropScript(out);
            
            // Get the import files from db
            DbImportSet dbis = new DbImportSet();
            files = dbis.getImportFiles(connection, Integer.valueOf(isid).intValue());
            
            Errors.logDebug("isid "+isid+" size="+files.size());
        
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                     "Transitional//EN\"");
            out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");
         
            out.println("<html>\n"
                     + "<head>\n");
            writeScriptModes(out);
            HTMLWriter.css(out,getURL("style/axDefault.css"));
            out.println("<title>Import Individuals</title>\n"
                     + "</head>\n"
                     + "<body>\n");
            
           // writeImportStatus(out);

            out.println("<table width=846 border=0>");
            out.println("<tr>");
            out.println("<td width=14 rowspan=3></td>");
            out.println("<td width=736 colspan=2 height=15>");
            out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Import - Check - Check all files</b></center>");
            out.println("</td></tr>");
            out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
            out.println("</tr></table>");
            
            /*
            out.println("<form name=\"FORM1\" action=\"" +
                     getServletPath("importFile/impPropPost?fname="+fname+"&isid="+isid+"&ifid="+ifid +"&" + QS)+
                     "\" method=\"post\"><table border=0>");
            */
            out.println("<form name=\"FORM1\" action=\"" +
                     getServletPath("importFile/impPropPost?isid="+isid+"&" + QS)+
                     "\" method=\"post\"><table border=0>");
            
             // out.println("<tr><td colspan=3>&nbsp;</td></tr>"); 
            out.println("<tr><td colspan=2>&nbsp;</td><td>Files:</td></tr>");
           
            
            ArrayList<ImportFileStruct> filenames = null;
            
            // Create, Update or Create_or_Update
            filenames = imp.listOfFiles(files, "C_U_CU");
            impPropWrite_C_U_CU(filenames,out);
            
            //impPropWrite_C_U(imp,files,out);
            //impPropWrite_C_CU(imp,files,out);
            //impPropWrite_U_CU(imp,files,out);
            
            filenames = imp.listOfFiles(files, "C");
            impPropWrite_C(filenames,out);
            
            //impPropWrite_U(imp,files,out);
            
            //impPropWrite_CU(imp,files,out);
            
            filenames = imp.listOfFiles(files, "SUID");
            ArrayList<SamplingUnit> samplingunits = DbSamplingUnit.getSamplingUnits(connection, Integer.valueOf(projectId).intValue());
            impPropWrite_SamplingUnit(filenames,out,samplingunits);
            
            filenames = imp.listOfFiles(files, "LEVEL");
            impPropWrite_Level(filenames,out,session);
            
            
            filenames = imp.listOfFiles(files, "SPECIESID");
            ArrayList<Species> species = DbSpecies.getSpecies(connection, Integer.valueOf(projectId).intValue());
            impPropWrite_Species(filenames,out,species);
            
            filenames = imp.listOfFiles(files, "NAME");
            impPropWrite_Name(filenames,out);
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
                /*
            if(CorU>0){
                out.println("<tr><td colspan=2>Note: The samples are always checked in Create or Update mode.</td><td>"+fileStructure[2][1]+"</td></tr>");
                if(CorU>2){
                    for(int i=3; i<CorU;i+=2)
                        out.println("<tr><td colspan=2>&nbsp;</td><td>"+fileStructure[2][i]+"</td></tr>");
                }
            }
            */
         
            
            
            
            
           
            
           
            out.println("<tr><td>&nbsp;</td><td></td></tr>");
            
            if(imp.listOfFiles(files, "NAME").size()>0)
                out.println("<tr><td><input type=\"button\" value=\"Start Check\" " +
                     "style=\"HEIGHT: 24px; WIDTH: 100px\" " +
                     "onClick='valForm()'></td></tr>");
            else 
                out.println("<tr><td><input type=\"submit\" value=\"Start Check\" " +
                     "style=\"HEIGHT: 24px; WIDTH: 100px\" " +
                     "onClick='document.forms[0].submit()'></td></tr>");
            
         
            
         out.println("<tr><td><p><input type=button value=\"Back\" " +
                 "width=70 style=\"WIDTH: 70px\" " +
                 "onClick='Javascript:location.href=\"" +
                 getServletPath("importFile/files?") + QS + "\";'>&nbsp;</p>");
            out.println("<td>&nbsp;</td></tr>" +
                     "</table>" +
                     "</form>" +
                     "</body>\n" +
                     "</html>");

        }
        catch (Exception e)
        {
            e.printStackTrace(out);
        }
    }    
    
    /**
     * Write the import properties for files with create,update or create_or_update capabilities.
     * This method is used by impProp
     *
     * @param filenames the import files that supports this mode.  
     * @param out The printwriter to use for writing the page 
     */
    private void impPropWrite_C_U_CU(ArrayList<ImportFileStruct> filenames, PrintWriter out)
    {
        // Create, Update or Create_or_Update
        //ArrayList<ImportFileStruct> filenames = null;
        //filenames = imp.listOfFiles(files, "C_U_CU");
        
        Errors.logDebug("impPropWrite_C_U_CU, filenamessize = "+filenames.size());

        if (filenames.size()>0)
        {
            String text="Select the mode for this formats: ";

            for (int i=0;i<filenames.size();i++)
            {
                if (i!=0)
                    text += ", ";
                else if (i==filenames.size())
                    text += " and ";

                text += filenames.get(i).type;
            }



            //The text and the first file name    
            out.println("<tr><td colspan=2>"+text+"</td><td>"+filenames.get(0).name+"</td></tr>");

            // Print the rest of the file names
            for (int i=1;i<filenames.size();i++)
                out.println("<tr><td colspan=2>&nbsp;</td><td>"+filenames.get(i).name+"</td></tr>");


            out.println("<tr><td align=right>Create new");
            out.println("<td><input type=radio value=C name=update CHECKED>");
            out.println("<tr><td align=right>Update existing");
            out.println("<td><input type=radio value=U name=update>");

            out.println("<tr><td align=right>Create or Update ");
            out.println("<td><input type=radio value=CU name=update>");
            
            out.println("<tr><td colspan=3>"+line+"</td></tr>");
        }
            
    }
   
    /**
     * Write the import properties for files with level capabilities.
     * This method is used by impProp
     *
     * @param filenames the import files that only supports create mode.  
     * @param out The printwriter to use for writing the page 
     * @param session The session variable, privilege levels are stored in the session.
     */
    private void impPropWrite_Level(ArrayList<ImportFileStruct> filenames, PrintWriter out, HttpSession session)
    {   
        if (filenames.size()>0)
        {
            for (int i=0;i<filenames.size();i++)
            {
                
            }
            
            out.println("<tr><td colspan=2>Select the level:</td>"
                +"<td>"+filenames.get(0).name+"</td></tr>");
            
            // the rest of the file names
            
            for(int i=1; i<filenames.size();i++)
                out.println("<tr><td colspan=2>&nbsp;</td><td>"+filenames.get(i).name+"</td></tr>");
            

            out.println("<tr><td>Level</td>");
            out.println("<td><select name=level style=\"WIDTH: 200px\">");
            int[] privileges = (int[]) session.getValue("PRIVILEGES");
            int myHighestLevel = -1;
            for (int i = 0; i < privileges.length; i++) 
            {
                if (GENO_W9 - privileges[i] >= 0 &&
                    privileges[i] - GENO_W0 >= 0 &&
                    (privileges[i] - GENO_W0) > myHighestLevel) 
                {
                    myHighestLevel = privileges[i] - GENO_W0;
                }
            }

            out.println("<option selected value=\"" + 0 + "\">" +
                 0 + "</option>");

            for (int i = 1; i <= myHighestLevel; i++) 
            {
                out.println("<option value=\"" + i + "\">" + i + "</option>");
            }

            out.println("</select>");
            out.println("</td></tr>");
            out.println("<tr><td colspan=3>"+line+"</td></tr>");
        }
    }
    
    /**
     * Write the import properties for files with only create mode.
     * This method is used by impProp
     *
     * @param filenames the import files that only supports create mode.  
     * @param out The printwriter to use for writing the page
     */
    private void impPropWrite_C(ArrayList<ImportFileStruct> filenames, PrintWriter out)
    {
        if (filenames.size()>0)
        {
            String text="Note: The ";
            if (filenames.size()>2)
            {
                for(int i=0; i<filenames.size();i++)
                {
                    if  (i == filenames.size())
                        text+=" and the ";
                    else if (i>1)
                        text+=", ";
                    text += filenames.get(i).type;
                }
                text+=" are always checked in Create New mode.";  
            }
            else 
                text += filenames.get(0).type + " is always checked in Create New mode.";

            out.println("<tr><td colspan=2>"+text+"</td><td>"+filenames.get(0).name+"</td></tr>");
            if(filenames.size()>2)
            {
                for(int i=1; i<filenames.size();i++)
                    out.println("<tr><td colspan=2>&nbsp;</td><td>"+filenames.get(i).name+"</td></tr>");
            }            
            out.println("<tr><td colspan=3>"+line+"</td></tr>");
        }
    }
    
    /**
     *  Write the import properties for files with uses Samplingunitid
     * This method is used by impProp
     * @param filenames The files that uses suid
     * @param out The printwriter to send output to
     * @param samplingunits The sampling units to display as options on the page
     */
    private void impPropWrite_SamplingUnit(ArrayList<ImportFileStruct> filenames, PrintWriter out, ArrayList<SamplingUnit> samplingunits)
    {
        if (filenames.size()>0)
        {
            out.println("<tr><td colspan=2>Select the sampling unit to which the file should be imported:</td>"
                +"<td>"+filenames.get(0).name+"</td></tr>");

            // the rest of the file names
            
            for(int i=1; i<filenames.size();i++)
                out.println("<tr><td colspan=2>&nbsp;</td><td>"+filenames.get(i).name+"</td></tr>");
            
            out.println("<tr><td>Sampling Unit</td>");

            out.println("<td><select name=\"suid\" style=\"HEIGHT: 24px; WIDTH: 200px\" size=1>");
            
            
            boolean first_round = true;
            
            for (int i=0;i<samplingunits.size();i++)
            {
                out.println("<option value=\"" + samplingunits.get(i).suid() + 
                          "\">" + samplingunits.get(i).name());
                
            }

            

            out.println("</select></td></tr>");
            out.println("<tr><td colspan=3>"+line+"</td></tr>"); 
        }
        
    }
    
    /**
     *  Write the import properties for files with uses speciesid
     * This method is used by impProp
     * @param filenames The files that uses speciesid
     * @param out The printwriter to send output to
     * @param species The species to display as options on the page
     */
    private void impPropWrite_Species(ArrayList<ImportFileStruct> filenames, PrintWriter out, ArrayList<Species> species)
    {
        //Species
        if(filenames.size()>0)
        {
            out.println("<tr><td colspan=2>Select the species to which the files should be imported:</td>"
                +"<td>"+filenames.get(0).name+"</td></tr>");

            // the rest of the file names
            
            for(int i=1; i<filenames.size();i++)
                out.println("<tr><td colspan=2>&nbsp;</td><td>"+filenames.get(i).name+"</td></tr>");


            out.println("<tr><td>Species</td>");
            out.println("<td><select name=species style=\"WIDTH: 200px\">");

            for (int i=0;i<species.size();i++)
            {
                out.println("<option value=\"" + species.get(i).speciesId() + 
                       "\">" + species.get(i).name());
            }
            out.println("</select>");
            out.println("</td></tr>"); 
            out.println("<tr><td colspan=3>"+line+"</td></tr>");
        }
    }
    
    /**
     * Write the import properties for files with uses name
     * This method is used by impProp
     * @param filenames the import files that uses this feature
     * @param out the printwriter to write output to
     */
    private void impPropWrite_Name(ArrayList<ImportFileStruct> filenames, PrintWriter out)
    {
        //The name of the sets of variables and markers.
        if(filenames.size()>0)
        {
            for(int i=0; i<filenames.size();i++)
            {
                out.println("<tr><td colspan=3>Give the name for the "+filenames.get(i).type+":</td></tr>");
                out.println("<tr><td>Name</td><td><input id=setName name=setName_"+filenames.get(i).ifid+" style=\"HEIGHT: 22px; WIDTH: 200px\" size=\"12\"></td><td>"+filenames.get(i).name+"</td></tr>");
            }
            out.println("<tr><td colspan=3>"+line+"</td></tr>"); 
        }
    }
    
     
 private void writeImpPropScript(PrintWriter out) 
    {
        out.println("<script language=\"JavaScript\">");
        out.println("<!--");
        out.println("function valForm(action) {");
        out.println("	");
        out.println("	var rc = 1;");
        out.println("	if (document.forms[0].setName.value==\"\") {");
        out.println("             alert('A Name has to be provided!'); ");
        out.println("             rc = 0;");
        out.println("	}");
        out.println("	if (rc == 1) {");
     
        out.println("		  document.forms[0].submit();");
        out.println("		  return true;");
        out.println("	}");
        out.println("	");
        out.println("}");
        out.println("//-->");
        out.println("</script>");
    }
 
 
 
   /**
    * The post section, all values for the impProp must be processed and stored.
    * @param request 
    * @param response 
    * @throws java.io.IOException 
    */
   private void impPropPost(HttpServletRequest request,
                           HttpServletResponse response)
                           throws IOException
   {
       try
        {
            HttpSession session = request.getSession(true);
            DbImportSet is = new DbImportSet();
            
            /*
             * Get parameters
             */
            String pid          = (String)session.getAttribute("PID");
            String userId       = (String)session.getAttribute("UserID");
            String isid         = request.getParameter("isid");
            String upPath       = getUpFilePath();
            Prefs prefs         = new Prefs();            
            prefs.pid           = Integer.valueOf(pid).intValue();
            prefs.userId        = userId;
            prefs.isid          = Integer.valueOf(isid).intValue();
            prefs.upPath        = upPath;
            prefs.maxDev        = getMaxDeviations();
            
            // Create path if it does not exist
            createPath(upPath);
            
            // Get parameters from web.xml 
            ServletContext conf = this.getServletContext();

            // Create a new connection to the db 
            // for the imput session!
            //Class.forName("oracle.jdbc.driver.OracleDriver");
            Class.forName(conf.getInitParameter("driver"));
            Connection conn = DriverManager.getConnection(conf.getInitParameter("dburl"), conf.getInitParameter("uid"), conf.getInitParameter("pwd"));
            Connection conn_viss = DriverManager.getConnection(conf.getInitParameter("dburl"), conf.getInitParameter("uid"), conf.getInitParameter("pwd"));
            
            prefs.connViss=conn_viss;
            prefs.connection=conn;
            
            prefs.mode="CHECK";
            
            // Sampling unit import
            if (request.getParameter("suid")!=null)
            {
                // Regular import, suid is available
                prefs.setSUId(Integer.parseInt(request.getParameter("suid")));
                is.setChkSuid(conn, isid, prefs.sampleUnitId);
                
                
                String updateMethod = request.getParameter("update");
                    
                if (updateMethod!=null)
                {
                    if (updateMethod.equals("C"))
                        prefs.updateMethod = "CREATE";
                    else if (updateMethod.equals("U"))
                        prefs.updateMethod = "UPDATE";
                    else if (updateMethod.equals("CU"))
                        prefs.updateMethod = "CREATE_OR_UPDATE";
                    else
                        prefs.updateMethod = "CREATE";

                    is.setChkUpdateMethod(conn, isid,  updateMethod);
                }
                
                if (request.getParameter("level") != null)
                {
                    prefs.level = Integer.parseInt(request.getParameter("level"));
                    is.setChkLevel(conn, isid, prefs.level);
                }
            }
            // Species import
            else
            {
                // Unifies import, speciesid is available
                
                prefs.setSpeciesId(Integer.parseInt(request.getParameter("species")));
                
                prefs.debug();
                
                //int speciesId    = Integer.parseInt(request.getParameter("species"));  
                //imp.setSpeciesId(speciesId);
                is.setChkSpeciesId(conn, isid, prefs.speciesId);
            }
            
            ImportProcess tmp = new ImportProcess();
            ArrayList<ImportFileStruct> files = is.getImportFiles(conn, Integer.valueOf(isid).intValue());
            ArrayList<ImportFileStruct> filenames = tmp.listOfFiles(files, "NAME");
            tmp=null;
            
            String setName = null;
            DbImportFile dbif = new DbImportFile();
            for (int i=0;i<filenames.size();i++)
            {
                setName = request.getParameter("setName_"+filenames.get(i).ifid).trim();
                String sql = "select count(name) as num from VARIABLE_SETS where suid="+ prefs.sampleUnitId +"AND name='"+setName+"'";

                Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(sql);

                if(rset.next())
                {
                    int num = rset.getInt("num");
                    
                    if (num != 0)   
                        throw new Exception("Set name exists. Please set another name");
                }
                dbif.insert_chk_name(conn, setName, Integer.valueOf(filenames.get(i).ifid).intValue());
            }
            ImportProcess imp = new ImportProcess(prefs);

            if(request.getParameter("suid")!="")
            {
                if (imp.lock() == true)
                {
                    imp.setPriority(Thread.NORM_PRIORITY - 2);
                    imp.start();
                    response.sendRedirect(getServletPath("importFile/files?isid="+isid));
                }
                else
                {
                    PrintWriter out = response.getWriter();    
                    try
                    {  
                        writeErrorPage(request, response, "ImportFile.Check", //"viewRes/edit?rid="+rid+"&"+oldQS
                        Errors.keyValue("ImportFile.Check.FileSet.Error.Msg"), 
                        "importFile/files?isid="+isid);
                        //writeErrorPage(request,response,"Error importing","A lock could not be established. Only one import session for each project is allowed at the same time. Please try later");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace(System.err);
                    }
                }
            }
            else    // Species
            {
                imp.setPriority(Thread.NORM_PRIORITY - 2);
                imp.start();
                response.sendRedirect(getServletPath("importFile/files?isid="+isid));

                //DbImportSet dbSet = new DbImportSet();
                is.setStatus(conn,isid,"CHECKING");
            }    
        }
        catch (Exception e)
        {
            Errors.log("Error in import file: "+e.getMessage());
            e.printStackTrace();
        }
   }
    
  /**
     * @param out
     */    
    private void writeScriptModes(PrintWriter out) {
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
     * /viewMessage
     * @param request 
     * @param response 
     * @throws ServletException 
     * @throws java.io.IOException 
     */
    private void viewMessagePage(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        HttpSession session = request.getSession(true);
        Connection connection;
        
        String ifid = request.getParameter("ifid");
      //  String imp = request.getParameter("IMPORT");
        String newQS = removeQSParameterOper(request.getQueryString());
        
        // set content type and other response header fields first
        response.setContentType("text/html");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        try
        {
            connection = (Connection) session.getValue("conn");
            
            // Get the message id
            DbImportFile dbFile = new DbImportFile();
            //String msg = dbFile.getErrMsg(connection,ifid);
            
            ArrayList msg = new ArrayList();
            ArrayList ts  = new ArrayList();
            dbFile.getErrInfo(connection, ifid, msg, ts);
            
            
            
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                     "Transitional//EN\"");
            out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");
         
            out.println("<html>\n"
                     + "<head>\n");
            HTMLWriter.css(out,getURL("style/axDefault.css"));
            out.println("<title>View Message</title>\n"
                     + "</head>\n"
                     + "<body>\n");            
            out.println("<h1>Messages</h1>");
            //\n<p>\n"+msg+"\n</p>\n");
            
            Timestamp tmp_time = null;
            for (int i=0;i<msg.size();i++)
            {
                tmp_time = (Timestamp)ts.get(i);
                
                if (tmp_time != null)
                    out.println("<p>" + tmp_time + "</p>");
                
                out.println("<p><pre>" + (String)msg.get(i) + "</pre></p>");
                out.println("<hr");
            }
               
     
                out.println("<p><input type=button value=\"Back\" " +
                    "width=70 style=\"WIDTH: 70px\" " +
                    "onClick='Javascript:location.href=\"" +
                    getServletPath("importFile?RETURNING=YES&") + newQS + "\";'>&nbsp;</p>");
        
             /*   out.println("<p><input type=button value=\"Back\" " +
                    "width=70 style=\"WIDTH: 70px\" " +
                    "onClick='Javascript:location.href=\"" +
                    getServletPath("importFile/files?") + newQS + "\";'>&nbsp;</p>");    
            */
            out.println("</body>\n</html>");

        }
        catch (Exception e)
        {
            e.printStackTrace(out);
        }
    }
    
    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */    
    private void abort(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        DbImportSet dbImp = new DbImportSet();
        
        HttpSession session = request.getSession();
        
        Connection conn = (Connection)session.getAttribute("conn");
        
        String isid = request.getParameter("isid");
        
        
        dbImp.setStatus(conn,isid,"ERROR");
        
        // Redirect to the files view again.
        response.sendRedirect(getServletPath("importFile/files?isid="+isid));        
    }
    
    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */    
    private void impCommit(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        HttpSession session = request.getSession(true);
        
        Connection connection;
        String ifid = request.getParameter("ifid");
        String isid = request.getParameter("isid");
        String newQS = request.getQueryString();
        
        
        // set content type and other response header fields first
        response.setContentType("text/html");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        try
        {
            connection = (Connection) session.getValue("conn");
            
            // Get the message id
            DbImportFile dbFile = new DbImportFile();
            //String msg = dbFile.getErrMsg(connection,ifid);
            
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 " +
                     "Transitional//EN\"");
            out.println(" \"http://www.w3.org/TR/html4/loose.dtd\">");
         
            out.println("<html>\n"
                     + "<head>\n");
            HTMLWriter.css(out,getURL("style/axDefault.css"));
            out.println("<title>Commit</title>\n"
                     + "</head>\n"
                     + "<body>\n");            
            out.println("<h1>Import all files</h1>\n");
            
            out.println("<p>If the database has been updated since the check was performed, "+
            "the import will may not succeed.<br> In that case, perform a new check.</p>");
            out.println("<p>When the import is confirmed it will be impossible to undo "+
            "the operation, <br> unless it is aborted during the import.<p>");
            
            
            out.println("<form name=\"FORM1\" action=\"" +
                     getServletPath("importFile/impCommitPost?isid="+isid+"&ifid="+ifid +"&"+newQS)+
                     "\" method=\"post\">");
            
            out.println("<p><input type=submit value=\"Confirm import\"></p>");
            
            // Back button
            out.println("<p><input type=button value=\"Back\" " +
                 "width=70 style=\"WIDTH: 70px\" " +
                 "onClick='Javascript:location.href=\"" +
                 getServletPath("importFile/import?isid="+ isid +"&" + newQS) + "\";'>&nbsp;</p>");
            
            out.println("</body>\n</html>");

        }
        catch (Exception e)
        {
            e.printStackTrace(out);
        }
    }
    
    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */    
    private void impCommitPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        try
        {
            HttpSession session = request.getSession(true);
            String newQS = request.getQueryString();
            
            String pid          = (String)session.getAttribute("PID");
            String isid         = request.getParameter("isid");
            String upPath       = getUpFilePath();            
            ServletContext conf = this.getServletContext();
            String userId       = (String)session.getAttribute("UserID");

            // Create a new connection to the db 
            // for the imput session!
            //Class.forName("oracle.jdbc.driver.OracleDriver");
            Class.forName(conf.getInitParameter("driver"));
            Connection conn = DriverManager.getConnection(conf.getInitParameter("dburl"), conf.getInitParameter("uid"), conf.getInitParameter("pwd"));
            Connection conn_viss = DriverManager.getConnection(conf.getInitParameter("dburl"), conf.getInitParameter("uid"), conf.getInitParameter("pwd"));

            DbImportSet is = new DbImportSet();
            ImportProperties ip = is.getChkValues(conn, isid);
            
            ImportProcess imp;
            
            
            Prefs prefs = new Prefs();
            prefs.connection    =   conn;
            prefs.connViss      =   conn_viss;
            prefs.pid           =   Integer.valueOf(pid).intValue();
            prefs.isid          =   Integer.valueOf(isid).intValue();
            prefs.upPath        =   upPath;
            prefs.sampleUnitId  =   ip.suid;
            prefs.userId        =   userId;
            prefs.level         =   ip.level;
            prefs.maxDev        =   getMaxDeviations();
            prefs.mode          =   "IMPORT";
            
            Errors.logDebug("Test:updateMethod="+ip.updateMethod);
            
            
            
            if (ip.updateMethod.equals("C"))
                prefs.updateMethod="CREATE";
            else if (ip.updateMethod.equals("U"))
                prefs.updateMethod="UPDATE";
            else if (ip.updateMethod.equals("CU"))
                prefs.updateMethod="CREATE_OR_UPDATE";
            
            prefs.debug();
            
            
            //Sampling unit
            if(prefs.sampleUnitId!=0)
            {
                
                
                imp = new ImportProcess(prefs);
                
                if (imp.lock() == true)
                {
                    imp.setPriority(Thread.NORM_PRIORITY - 2);
                    imp.start();
                    response.sendRedirect(getServletPath("importFile/import?isid="+isid+"&"+newQS)); //files
                }
            
                else
                {
                    PrintWriter out = response.getWriter();    
                    try
                    {
                        writeErrorPage(request,response,"Error importing","A lock could not be established. Only one import session for each project is allowed at the same time. Please try later");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace(System.err);
                    }   
                }
            }
            
            //Species
            else
            {
                //int sid = is.getSpeciesValue(conn, isid);
                
                prefs.speciesId = is.getSpeciesValue(conn, isid);
                imp = new ImportProcess(prefs);
                
                imp.setPriority(Thread.NORM_PRIORITY - 2);
                imp.start();
                response.sendRedirect(getServletPath("importFile/import?isid="+isid+"&"+newQS)); //files
                
                DbImportSet dbSet = new DbImportSet();
                dbSet.setStatus(conn,isid,"IMPORTING");
            }
        }
        catch (Exception e)
        {
            Errors.log("Error in import file: "+e.getMessage());
            e.printStackTrace();
        }
    }
}

