/*
  $Log$
  Revision 1.5  2005/01/31 16:16:41  heto
  Changing database to PostgreSQL. Problems with counts and selection buttons...

  Revision 1.4  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.3  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.2  2002/10/18 11:41:10  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:05  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.23  2001/06/18 09:10:54  frob
  Totally rewritten.

  Revision 1.19  2001/06/11 07:02:00  frob
  Fixed some HTML style.

  Revision 1.18  2001/06/01 09:53:18  frob
  Added some comments.

  Revision 1.17  2001/06/01 06:41:31  frob
  HTML layout fixes.

  Revision 1.16  2001/05/31 07:06:59  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.15  2001/05/29 11:57:04  frob
  All methods in editGroup moved to viewGroup. Old construction with getStatus removed.

  Revision 1.14  2001/05/29 06:42:29  frob
  All code from detailsGroup moved to this class.

  Revision 1.13  2001/05/28 13:50:52  frob
  Modified as logError was moved from ArexisServlet to Errors.

  Revision 1.12  2001/05/23 11:56:51  frob
  Added some log statements.

  Revision 1.11  2001/05/23 11:05:39  frob
  Finished restructuring/commenting.

  Revision 1.10  2001/05/23 07:59:16  frob
  Commenting, restructuring.

  Revision 1.9  2001/05/22 12:37:26  frob
  Various changes.

  Revision 1.8  2001/05/22 10:27:58  frob
  Restructuring, commenting, etc.

  Revision 1.7  2001/05/22 06:16:53  roca
  Backfuncktionality fixed for all (?) servlets/subpages

  Revision 1.6  2001/05/16 09:30:56  frob
  writeError() removed (again!)

  Revision 1.5  2001/05/16 09:28:19  frob
  Indented the file as they were lost in the merge.

  Revision 1.4  2001/05/15 13:36:24  roca
  After merge problems from last checkin..

  Revision 1.3  2001/05/11 05:39:15  frob
  Changed all methods that used to call writeError() to write an error page. These
  methods now calls the general method commitOrRollback() which does the commit or
  rollback operation as well as handle any errors. writeError() was removed.

  Revision 1.2  2001/05/11 05:18:46  frob
  Added log header and indented the file.

*/

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;

public class viewGroup extends SecureArexisServlet
{

   
   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////
   

   /**
    * Calls the doGet method.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @exception ServletException If request could not be handled.
    * @exception IOException If I/O error when handling request.
    */
   public void doPost(HttpServletRequest request,
                      HttpServletResponse response) 
      throws ServletException, IOException
   {
      doGet(request, response);
   }
   

   /**
    * Dispatches the request by calling a private method depending on the
    * path given in the request object. 
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @exception ServletException If request could not be handled.
    * @exception IOException If I/O error when handling request
    */
   public void doGet(HttpServletRequest request,
                     HttpServletResponse response)
      throws ServletException, IOException
   {
      // Get the path varaible which is used to determine which method to 
      // call 
      String extPath = request.getPathInfo();
      
      // If path varaible is not given, just write the frame itself.
      if (extPath == null || extPath.equals("") || extPath.equals("/"))
      {
         writeFrame(request, response);
      }

      // Write the top section of the page
      else if (extPath.equals("/top"))
      {
         writeTop(request, response);
      }

      // Write the middle section of the page
      else if (extPath.equals("/middle"))
      {
         writeMiddle(request, response);
      }
      
      // Writes the bottom part of the page
      else if (extPath.equals("/bottom"))
      {
         writeBottom(request, response);
      }

      // Writes the page for creating a new group or creates a new group 
      else if (extPath.equals("/new"))
      {
         writeNew(request, response);
      }

      // Writes the page for copying a group or copies a group
      else if (extPath.equals("/copy"))
      {
         writeCopy(request, response);
      }

      // Writes the details page
      else if (extPath.equals("/details"))
      {
         writeDetails(request, response);
      }
      
      // Writes the edit page or updates a group or deletes a group
      else if (extPath.equals("/edit"))
      {
         writeEdit(request, response);
      }
      
      // Unhandled path
      else
      {
         Errors.logError(getClass().getName() +
                         ".doGet: Unknown path : " + extPath);
      }
         
   }


   //////////////////////////////////////////////////////////////////////
   //
   // Protected section
   //
   //////////////////////////////////////////////////////////////////////


   
   //////////////////////////////////////////////////////////////////////
   //
   // Private section
   //
   //////////////////////////////////////////////////////////////////////
   

   /**
    * Writes the default frameset for groups. The frameset contains three
    * frames: top, middle and bottom.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @exception ServletException If request could not be handled.
    * @exception IOException If I/O error when handling request.
    */
   private void writeFrame(HttpServletRequest request,
                           HttpServletResponse response)
      throws ServletException, IOException
   {
      HttpSession session = request.getSession(true);

      // set content type and other response header fields first
      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");

      PrintWriter out = response.getWriter();
      try
      {
         // First, determine whether suid has changed or not. Then get the
         // gsid and determine whether it has changed or not.
         boolean suidHasChanged = setSessionSuid(request);
         String gsid = getGsid(request, suidHasChanged);
         boolean gsidHasChanged = hasNewGsid(request, gsid);

         // Get necessary parameters for the query string, then build the 
         // string 
         String action = getAction(request, suidHasChanged);
         String orderBy = getOrderBy(request, suidHasChanged);
         int groups = countGroups(gsid, session);
         String startIndex = getStartIndex(request, groups, suidHasChanged, 
                                           gsidHasChanged, action);
         String newQS = "gsid=" + gsid + "&" + "ORDERBY=" + orderBy + "&" +
            "ACTION=" + action + "&" + "STARTINDEX=" + startIndex;
         // System.err.println("New qs: " + newQS);
         
         // Write the page.
         HTMLWriter.framesetDoctype(out);
         HTMLWriter.openHEAD(out, "");
         HTMLWriter.closeHEAD(out);

         HTMLWriter.comment(out, "Page is divided into three frames: " +
                            "a top, middle and a bottom. Non-standard\n" +
                            "parameters FRAMESPACING and BORDER are " +
                            "necessary to remove frameborders in all browsers",
                            true, false);
         
         out.println("\n<FRAMESET rows=\"180,35,*\" framespacing=0 border=0>\n");
         out.println("  <FRAME name=\"viewgroupptop\" " +
                     "src=\"" + getServletPath("viewGroup/top?") + newQS + "\" " +
                     "scrolling=no marginheight=0 noresize frameborder=0>\n" );
         out.println("  <FRAME name=\"viewgroupmiddle\" " +
                     "src=\"" + getServletPath("viewGroup/middle?") + newQS + "\" " +
                     "scrolling=no marginheight=0 noresize frameborder=0>\n");
         out.println("  <FRAME name=\"viewgroupbottom\" " +
                     "src=\"" + getServletPath("viewGroup/bottom?") + newQS + "\" " +
                     "scrolling=auto marginheight=0 frameborder=0>\n");
         out.println("  <NOFRAMES>\n" +
                     "    <BODY>\n" +
                     "      <P>This page uses frames, but your browser doesn't support them.\n" +
                     "    </BODY>\n" +
                     "  </NOFRAMES>\n" +
                     "</FRAMESET>\n" +
                     "</HTML>");
      }
      catch (Exception e)
      {
         out.println("<PRE>");
         e.printStackTrace(out);
         out.println("</PRE>");
      }
   }


   /**
    * Returns the most accurate groupings id (gsid). If the sampling unit
    * did change, the returned value is always a default value. If the
    * sampling unit did not change, the gsid parameter is read from the
    * request. If the parameter was found and is not blank, the found value
    * is returned, otherwise the default value is used.
    *
    * @param request The request from the client.
    * @param suidHasChanged Value indicating whether the sampling unit did
    *                       change or not.
    * @return A correct groupings id.
    */
   private String getGsid(HttpServletRequest request,
                          boolean suidHasChanged)
   {
      // The groupings id to use. Initialize with default value
      String gsid = Defaults.DEFAULT_GSID;

      // If suid has not changed, try to read gsid from request.
      if (!suidHasChanged)
      {
         // Get gsid from request. If found in request, use that value 
         String requestGsid = request.getParameter("gsid");
         if (Utils.assigned(requestGsid) && !Utils.blank(requestGsid))
         {
            gsid = requestGsid;
         }
      }
      
      return gsid;
   }

   
   /**
    * Returns a value indicating whether the gsid has changed or not. The
    * result is defined by looking for the parameter old_gsid in the
    * request. If this parameter is given, is not blank and is different
    * from the currentGsid parameter, this method returns true. Otherwise
    * false is returned. 
    *
    * @param request a HttpServletRequest value
    * @param currentGsid a String value
    * @return True if currentGsid is different from the value of the
    *         old_gsid parameter.
    *         False otherwise.
    */
   private boolean hasNewGsid(HttpServletRequest request,
                              String currentGsid) 
   {
      // Get the old_gsid parameter from the request
      String oldGsid = request.getParameter("old_gsid");

      // If parameter is set, is not blank and is different from the
      // currentGsid, return true
      if (Utils.assigned(oldGsid) && !Utils.blank(oldGsid) &&
          !currentGsid.equals(oldGsid)) 
      {
         return true;
      }

      // The gsid is not new.
      return false;
   }
   
   
   /**
    * Count number of groups with the given grouping id.
    *
    * @param groupingsId The grouping id to look for.
    * @param session The session value to use.
    * @return Number of groups within the given grouping.
    */
   private int countGroups(String groupingsId, HttpSession session)
   {
      Connection connection = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      try
      {
         // Create the statement, execute the query and return result.
         stmt = connection.createStatement();
         rset = stmt.executeQuery("SELECT count(*) "
                                  + "FROM gdbadm.V_GROUPS_1 WHERE GSID=" +
                                  groupingsId + " "); 
         rset.next();
         return rset.getInt(1);
      }
      catch (SQLException e)
      {
         return 0;
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

   
   /**
    * Writes the contents of the topframe.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @exception ServletException If request could not be handled.
    * @exception IOException If I/O error when handling request
    */
   private void writeTop(HttpServletRequest request,
                         HttpServletResponse response) 
      throws ServletException, IOException
   {
      // set content type and other response header fields first
      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");
      
      HttpSession session = request.getSession(true);
      Connection connection = null;
      PrintWriter out = response.getWriter();
      
      Statement stmt_su = null; // Statement and resultset for sampling units
      ResultSet rset_su = null;
      Statement stmt_grps = null; // Statement and result set for groupings
      ResultSet rset_grps = null;
      
      try
      {
         connection = (Connection) session.getValue("conn");

         // Get parameters
         final String pid = (String) session.getValue("PID");
         final String suid = (String) session.getValue("SUID");
         final int [] currentPrivs = (int [])session.getValue("PRIVILEGES");

         // These values are set in writeFrame, so we don't need to check
         // for null values
         final String gsid = request.getParameter("gsid");
         final String orderby = request.getParameter("ORDERBY");
         final String action = request.getParameter("ACTION");
         final int startIndex =
            Integer.parseInt(request.getParameter("STARTINDEX")); 

         // Write the header, open body and open the FORM tag
         HTMLWriter.startTopPage(out, getServletPath("viewGroup"));
         
         // Write the header table and start the content table
         HTMLWriter.headerTable(out, 0, Errors.keyValue("Groups.Main"));
         HTMLWriter.contentTableStart(out, 0);

         // The top page is structured as a table with three
         // columns. First column contains sampling units, second column
         // contains groupings while the third column contains the buttons
         HTMLWriter.topTableStart(out, 0);
         
         out.println("  <!-- First column: sampling units -->\n" +
                     "  <TD height=92>\n" +
                     "    <B>Sampling unit</B><BR>");
         
         // Add the sampling units selection field. This field submits the
         // page each time a new suid is selected.
         out.println("    <SELECT name=suid "
                     + "onChange='document.forms[0].submit()'  "
                     + "style=\"WIDTH: 126px\">");

         // Create a statement and execute a query for all sampling units
         // within the project
         stmt_su = connection.createStatement();
         rset_su = stmt_su.executeQuery("SELECT NAME, SUID FROM "
                                        + "gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                        " WHERE PID=" + pid +
                                        " order by NAME");

         // While there are sampling units in the result set, add them to
         // the selection field.
         while (rset_su.next())
         {
            
            // If looped suid is the same as we read from the session
            // object, use this sampling unit as the pre-selected one. 
            if (suid.equalsIgnoreCase(rset_su.getString("SUID")))
            {
               out.print("      <OPTION selected value=\"");
            }

            // This is not the same suid as in the session object, just add
            // the suid.
            else
            {
               out.print("      <OPTION value=\"");
            }
            out.println(rset_su.getString("SUID") + "\">" +
                        rset_su.getString("NAME"));   
         }
         
         out.println("    </SELECT>");

         // Close the first column of the table, open the second one and
         // add the groupings selection field
         out.println("  </TD>\n\n" +
                     "  <!-- Second column: groupings -->\n" +
                     "  <TD>\n" +
                     "    <B>Grouping</B><br>\n" +
                     "    <SELECT name=gsid style=\"HEIGHT: 22px; WIDTH: 126px\">");

         // Create a statement and execute a query for all groupings within
         // the sampling unit.
         stmt_grps = connection.createStatement();
         rset_grps = stmt_grps.executeQuery("SELECT NAME, GSID FROM gdbadm.V_GROUPINGS_1 " +
                                            "WHERE SUID=" + suid + " ORDER BY NAME");

         // While there are goupings, add them to the selection field
         while (rset_grps.next())
         {
            // If looped grouping is the same as the one in the request
            // object, use this grouping as the pre-selected one
            if (gsid.equalsIgnoreCase(rset_grps.getString("GSID")))
            {
               out.print("      <OPTION selected value=\"");
            }

            // Not the same guid as in the request object, just add it
            else
            {
               out.print("      <OPTION value=\"");
            }
            out.println(rset_grps.getString("GSID") + "\">" +
                        rset_grps.getString("NAME"));
         }
         out.println("    </SELECT>");

         // Close the second column, open the third one and add the buttons.
         out.println("  </TD>\n\n" +
                     "  <!-- Third column: buttons -->\n" +
                     "  <TD>");

         // Buttons are structured in a table
         HTMLWriter.comment(out, "Button table, start", true, false);
         out.println("<TABLE border=0 width=132 align=right " +
                     "cellspacing=0 cellpadding=0>");
         out.println("<TR>\n" +
                     "  <TD width=66 colspan=2>");

         // New-button
         out.println(privDependentString(currentPrivs, GRP_W, /*if true*/
                                         "    <INPUT type=button value=\"New\""  
                                         + " onClick='parent.location.href=\"" 
                                         + getServletPath("viewGroup/new?")  
                                         + "gsid=" + gsid + "\"' "
                                         + " style=\"font-size: 9pt; "
                                         + "HEIGHT: 24px; WIDTH: 66px\">\n"
                                         + "  </TD>",
                                         /*if false*/
                                         "    INPUT type=button disabled value=\"New\""
                                         + " style=\"font-size: 9pt; "
                                         + " HEIGHT: 24px; WIDTH: 66px\">"
                                         + "  </TD>"));
         out.println("  <TD width=66 colspan=2>");

         // Copy-button
         out.println(privDependentString(currentPrivs, GRP_W, /*if true*/
                                         "    <INPUT type=button value=\"Copy\"" 
                                         + " onClick='parent.location.href=\"" 
                                         + getServletPath("viewGroup/copy?") 
                                         + "gsid=" + gsid + "\"' "
                                         + " style=\"font-size: 9pt; "
                                         + "HEIGHT: 24px; WIDTH: 66px\">\n"
                                         + "  </TD>",
                                         /*if false*/
                                         "    <INPUT type=button disabled value=\"Copy\""
                                         + " style=\"font-size: 9pt; "
                                         + " HEIGHT: 24px; WIDTH: 66px\">"
                                         + "  </TD>"));

         out.println("</TR>");

         out.println("<TR>\n" +
                     "  <TD width=68 colspan=2>\n" +
                     "    <INPUT name=ACTION type=submit value=\"" +
                     Defaults.ACTION_COUNT + "\" " +
                     " style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">\n" +
                     "  </TD>\n" +
                     "  <TD width=68 colspan=2>\n" +
                     "    <INPUT name=ACTION type=submit value=\"" +
                     Defaults.ACTION_DISPLAY + "\" " +
                     " style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">\n"+
                     "  </TD>\n" +
                     "</TR>");

         out.println("<TR>");
         out.println("  <TD width=34>\n" +
                     "    <INPUT name=ACTION type=submit value=\"<<\" " +
                     "style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 33px\">\n" +
                     "  </TD>");
         out.println("  <TD width=34>\n" +
                     "    <INPUT name=ACTION type=submit value=\"<\" " +
                     "style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 33px\">\n" +
                     "  </TD>");
         out.println("  <TD width=34>\n" +
                     "    <INPUT name=ACTION type=submit value=\">\" " +
                     "style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 33px\">\n" +
                     "  </TD>");
         out.println("  <TD width=34>\n" +
                     "    <INPUT name=ACTION type=submit value=\">>\" " +
                     "style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 33px\">\n" +
                     "  </TD>");
         out.println("</TR>\n" +
                     "</TABLE>");
         HTMLWriter.comment(out, "Button table, end", false, true);

         out.println("  </TD>");
         HTMLWriter.topTableEnd(out);
         
         // some hidden values
         out.println("<INPUT type=hidden name=\"STARTINDEX\" value=\"" 
                     + startIndex + "\">");
         out.println("<INPUT type=hidden name=\"ORDERBY\" value=\"" 
                     + orderby + "\">");

         // old_gsid holds the value of the gsid that we used when the page
         // was displayed. This value can later be compared to the gsid
         // value, which holds the value of the selected grouping in the
         // selection field. This makes it possible to determine if the
         // user did change grouping or not.
         out.println("<INPUT type=hidden name=\"old_gsid\" value=\"" + gsid
                     + "\">");
         
         HTMLWriter.contentTableEnd(out);
         out.println("</FORM>");
         
         HTMLWriter.closeBODY(out);
         HTMLWriter.closeHTML(out);
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
            if (rset_su != null) rset_su.close();
            if (stmt_su != null) stmt_su.close();
            if (rset_grps != null) rset_grps.close();
            if (stmt_grps != null) stmt_grps.close();
         }
         catch (SQLException ignored)
         {
         }
      }
   }

   
   /**
    * Prints the middle frame. Contains the info line and the header of the
    * table with the matching group data.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @exception ServletException If request could not be handled.
    * @exception IOException If I/O error handling request.
    * @exception AssertionException If parameter could not be read.
    */
   private void writeMiddle(HttpServletRequest request,
                            HttpServletResponse response)
      throws ServletException, IOException
   {
      HttpSession session = request.getSession(true);
            
      // set content type and other response header fields first
      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");
      PrintWriter out = response.getWriter();

      // Get necessary parameters. These are set in writeFrame so we
      // doesn't need to check for nulls.
      final String gsid = request.getParameter("gsid");
      final String orderBy = request.getParameter("ORDERBY");
      final String action = request.getParameter("ACTION");
      final int startIndex =
         Integer.parseInt(request.getParameter("STARTINDEX")); 
      final int maxRows = getMaxRows(session);
      final int rows = countGroups(gsid, session);
                  
      // Start the new query string. The rest will be added in 
      // writeMiddleDetailsHeader 
      String queryString = "gsid=" + gsid + "&STARTINDEX=" + startIndex;
      
      try
      {
         // Start the page and start the table
         HTMLWriter.startMiddlePage(out, action, startIndex, rows, maxRows);
         HTMLWriter.startMiddlePageTable(out, 0, 750);
         
         // Get the servlet path
         final String servletPath = getServletPath("viewGroup");

         // Write the headers.
         HTMLWriter.writeMiddleDetailsHeader(out, 100, servletPath,
                                             queryString, orderBy, 
                                             Defaults.ORDERBY_NAME_TEXT,
                                             Defaults.ORDERBY_NAME_SQL); 
         
         HTMLWriter.writeMiddleDetailsHeader(out, 100, servletPath,
                                             queryString, orderBy,  
                                             Defaults.ORDERBY_COMMENT_TEXT,   
                                             Defaults.ORDERBY_COMMENT_SQL);
         
         HTMLWriter.writeMiddleDetailsHeader(out, 100, servletPath,
                                             queryString, orderBy, 
                                             Defaults.ORDERBY_INDIVIDUALS_TEXT,  
                                             Defaults.ORDERBY_INDIVIDUALS_SQL); 
         
         HTMLWriter.writeMiddleDetailsHeader(out, 100, servletPath,
                                             queryString, orderBy,  
                                             Defaults.ORDERBY_USER_TEXT,    
                                             Defaults.ORDERBY_USER_SQL); 

         HTMLWriter.writeMiddleDetailsHeader(out, 145, servletPath,
                                             queryString, orderBy, 
                                             Defaults.ORDERBY_TS_TEXT,  
                                             Defaults.ORDERBY_TS_SQL);

         out.println("  <TD width=100>&nbsp;</TD>");
         out.println("  <TD width=100>&nbsp;</TD>");
         out.println("</TR>\n" +
                     "</TABLE>");
         HTMLWriter.closeBODY(out);
         HTMLWriter.closeHTML(out);
      }
      catch (Exception e)
      {
         out.println("<strong>Error in filter!</strong><br>");
         out.println("Error message: " + e.getMessage());
         out.println("<br>Modify filter according to message!</body></html>");
      }
   }
   
   
   /**
    * Writes the bottom page. Contains a table with all matching groups.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @exception ServletException If error when handling request.
    * @exception IOException If I/O error when handling request.
    */
   private void writeBottom(HttpServletRequest request,
                            HttpServletResponse response)
      throws ServletException, IOException
   {
      HttpSession session = request.getSession(true);

      // set content type and other response header fields first
      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");

      PrintWriter out = response.getWriter();
      Statement stmt = null;
      ResultSet rset = null;
      Connection connection = null;
      try
      {
         final int [] currentPrivs = (int [])session.getValue("PRIVILEGES");
         final String pid = (String)session.getValue("PID"); // Project id
         final int maxRows = getMaxRows(session);

         // Get values from request, are set in writeFrame so we doesn't
         // need to check for nulls
         final String orderby = request.getParameter("ORDERBY");
         final String gsid = request.getParameter("gsid");
         final String action = request.getParameter("ACTION");
         final int startIndex =
            Integer.parseInt(request.getParameter("STARTINDEX"));
                  
         // If no action or action is count, just write the default bottom
         // and exit
         if (action == null || 
             action.equalsIgnoreCase(Defaults.ACTION_COUNT))
         {
            // Nothing to do!
            HTMLWriter.writeBottomDefault(out);
            return;
         }

         // Create the connection and start building the SQL string
         connection = (Connection) session.getValue("conn");
         StringBuffer sbSQL = new StringBuffer(512);
         sbSQL.append("SELECT NAME, COMM, INDS, USR, GID, " +
                      "to_char(TS, '" + getDateFormat(session) + "') as TC_TS " +
                      "FROM gdbadm.V_GROUPS_3 WHERE GSID=" + gsid + " " +
                      "AND PID=" + pid +" ");

         // End the SQL string depending on the ORDERBY parameter
         if (orderby == null)
         {
            sbSQL.append(" order by NAME");
         }
         else
         {
            sbSQL.append(" order by " + orderby);
         }

         // Create the statement and execute the query
         stmt = connection.createStatement();
         rset = stmt.executeQuery(sbSQL.toString());

         // If startIndex is not 1 we must step forward in the result set
         // until the desired index if found
         int currentRow = 0;
         if (startIndex > 1)
         {
            while ((currentRow++ < startIndex - 1) && rset.next() )
               ;
         }

         boolean odd = true;    // Are we printing an odd row or not?
         currentRow = 0;

         // Write the start of the page and open the table
         HTMLWriter.startBottomPage(out, 750);
         
         // Loop all items in the result set and print them
         while (rset.next() && currentRow < maxRows)
         {
            out.print("<TR align=left ");
            if (odd)
            {
               out.println("bgcolor=white>");
               odd = false;
            }
            else
            {
               out.println("bgcolor=lightgrey>");
               odd = true;
            }

            // Write the columns
            out.println("  <TD width=5></TD>");
            out.println("  <TD width=100>" +
                        formatOutput(session, rset.getString("NAME"), 12) +
                        "</TD>"); 
            out.println("  <TD width=100>" +
                        formatOutput(session, rset.getString("COMM"), 15) +
                        "</TD>");
            
            out.println("  <TD width=100>" +
                        replaceNull(rset.getString("INDS"), "0") +
                        "</TD>"); 
            out.println("  <TD width=100>" +
                        formatOutput(session, rset.getString("USR"), 10) +
                        "</TD>");
            
            out.println("  <TD width=145>" +
                        formatOutput(session, rset.getString("TC_TS"), 18) +
                        "</TD>");

            // For details and edit, get the group id and us it together
            // with the original query string as parameters in the link.
            out.println("  <TD width=100>\n" +
                        "    <A HREF=\"" +
                        getServletPath("viewGroup/details?gid=") +
                        rset.getString("GID") +
                        "\" target=\"content\">\n" +
                        "      Details\n" +
                        "    </A>\n" +
                        "  </TD>"); 


            out.println("  <TD width=100>");
            out.println(privDependentString(currentPrivs,GRP_W,
                                            "    <A HREF=\"" +
                                            getServletPath("viewGroup/edit?gid=") +
                                            rset.getString("GID") +
                                            "\" target=\"content\">\n" +
                                            "      Edit\n" +
                                            "    </A>",
                                            "    <font color=tan>&nbsp Edit</font>"));
            out.println("  </TD>\n" +
                        "</TR>\n");
            currentRow++;
         }
         out.println("</TABLE>");
         HTMLWriter.closeBODY(out);
         HTMLWriter.closeHTML(out);
      }
      catch (Exception e)
      {
         out.println("<strong>Error in filter!</strong><br>");
         out.println("Error message: " + e.getMessage());
         out.println("<br>Modify filter according to message!</body></html>");
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
         catch (Exception ignored)
         {
         }
      }
   }


   /**
    * Either writes the page used for creating new individuals or creates a
    * new individual and rewrites the frame.What is done is determined by
    * the oper parameter in the request.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @exception ServletException If error when handling the request.
    * @exception IOException If I/O error when handling request.
    */
   private void writeNew(HttpServletRequest request,
                         HttpServletResponse response)
      throws ServletException, IOException
   {
      HttpSession session = request.getSession(true);
      Connection connection = (Connection) session.getValue("conn");

      // Get the oper parameter and check if it is set to create. If it is,
      // create the group and rewrite the frame
      String oper = request.getParameter("oper");
      if (Utils.assigned(oper) && oper.equals("CREATE")) 
      {
         // Create a group and rewrite the frame
         if (createGroup(request, response))
         {
            writeFrame(request, response);
         }
      }
      
      // if not create, write the page for creating new groups
      else
      {
         writeNewPage(request, response);
      }
   }


   /**
    * Writes the page used for creating new grops. When the page is
    * submitted, it is posted to the servlet itself with the extended path
    * set to /new.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @exception ServletException If request could not be handled.
    * @exception IOException If I/O error when handling request.
    */
   private void writeNewPage(HttpServletRequest request,
                             HttpServletResponse response)
      throws ServletException, IOException
   {
      HttpSession session = request.getSession(true);
      boolean suidHasChanged = setSessionSuid(request);

      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");

      PrintWriter out = response.getWriter();
      Connection connection =  null;
      Statement stmt = null;
      ResultSet rset = null;

      try
      {
         connection = (Connection) session.getValue("conn");
         stmt = connection.createStatement();

         final String pid = (String) session.getValue("PID"); // Project id
         final String suid = (String) session.getValue("SUID");

         // Get the gsid from the request. By using this method, we don't
         // need to check for nulls as it always return a valid value.
         String gsid = getGsid(request, suidHasChanged);
         
         HTMLWriter.doctype(out);
         out.println("<HTML>\n"
                     + "<HEAD>\n"
                     + "  <TITLE>New Group</title>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
                     
         // Print the script to be used when creating new groups.
         printCreateGroupScript(out);
         out.println("</HEAD>\n"
                     + "<BODY>\n");

         // new look
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Groups - New</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" " +
                     " bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<FORM method=\"post\" action=\"" +
                     getServletPath("viewGroup/new?") + "ACTION=" +
                     Defaults.ACTION_DISPLAY + "\">"); 

         out.println("<table width=800 align=center border=0>");

         // Create statement and build query for getting all sampling units
         // within this project
         stmt = connection.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM " +
                                  "gdbadm.V_ENABLED_SAMPLING_UNITS_2 WHERE PID="
                                  + pid + "ORDER BY NAME");
         out.println("<tr><td width=200>Sampling Units</td></tr>" +
                     "<tr><td width=200>");
         
         out.println("<SELECT style=\"WIDTH: 200px\" name=\"suid\" " +
                     "onChange='document.forms[0].submit()'>");

         // Loop all found sampling units
         while (rset.next())
         {
            // If currently looped suid is the selected one, select the su
            // in the selection field
            if (suid.equalsIgnoreCase(rset.getString("SUID")))
            {
               out.print("  <OPTION selected value=\"");
            }
            
            // Current suid is not the selected one
            else
            {
               out.print("  <OPTION value=\"");
            }
            
            out.println(rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</OPTION>");  
         }
         out.println("</SELECT></td></tr>");

         // Create statement and build query to get all groupings for
         // current sampling unit and current project
         stmt = connection.createStatement();
         rset = stmt.executeQuery("SELECT GSID, NAME FROM " +
                                  "gdbadm.V_GROUPINGS_2 WHERE PID=" + pid +
                                  " AND SUID=" + suid + " ORDER BY NAME");

         out.println("<tr><td width=200 >Grouping</td></tr>" +
                     "<tr><td width=200>");

         out.println("<SELECT style=\"WIDTH: 200px\" name=\"gsid\">");
         
         // While there are groupings
         while (rset.next())
         {
            // If currently looped gsid is the previously selected one,
            // select the grouping in the field
            if (gsid.equalsIgnoreCase(rset.getString("GSID")))
            {
               out.print("  <OPTION selected value=\"");
            }

            // This is not the selecte grouping, just add it
            else
            {
               out.print("  <OPTION value=\"");
            }
            out.println(rset.getString("GSID") + "\">" +
                        rset.getString("NAME") + "</OPTION>");
         }
         out.println("</SELECT></td></tr>");


         out.println("<tr><td width=200>Name</td></tr>" +
                     "<tr><td width=200>" +
                     "<input type=text maxlength=20 " +
                     "name=\"new_group_name\" style=\"WIDTH: 200px\" value=\"\"></td></tr>");
         out.println("<tr><td width=200 >Comment</td></tr>" +
                     "<tr><td colspan=3 width=600>"
                     +"<textarea rows=8 cols=45 name=COMM></textarea>"
                     +"</td></tr>");



         out.println("<tr>\n" +
                     "  <td width=200>&nbsp;</td>\n" +
                     "  <td width=200>&nbsp;</td>\n" +
                     "  <td width=200>&nbsp;</td>\n" +
                     "  <td width=200>&nbsp;</td>\n" +
                     "</tr>" +
                     "<tr><td colspan=4 >" +
                     "<input type=button value=Back onClick='location.href=\"" +
                     
                     getServletPath("viewGroup?&RETURNING=YES") + "\";' " +
                     "style=\"WIDTH: 100px\">&nbsp;"+
                     "<input type=button id=SAVE name=SAVE " +
                     "value=Create style=\"WIDTH: 100px\" " +
                     "onClick='valForm(\"CREATE\")'>&nbsp;");

         out.println("</td></tr>");
         out.println("</table>");

         out.println("<INPUT type=\"hidden\" name=RETURNING value=YES>");
         out.println("<INPUT type=\"hidden\" name=oper value=\"\">");
         
         out.println("</FORM>");
         out.println("</body>\n</html>");
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


   /**
    * Print the Java script to be shown when creating new group. If user
    * selects yes in the shown dialog, the hidden parameter oper is set to
    * CREATE and then the form is submitted.
    *
    * @param out The PrintWriter to write to.
    */
   private void printCreateGroupScript(PrintWriter out)
   {
      out.println("<script type=\"text/javascript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('CREATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to create the group?')) {");
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


   /**
    * Creates a new group.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if group was created.
    *         False if group was not created.
    */
   private boolean createGroup(HttpServletRequest request,
                               HttpServletResponse response)
   {
      HttpSession session = request.getSession(true);
      Connection connection = (Connection) session.getValue("conn");
      String errMessage = null;
      boolean isOk = true;
      try
      {
         connection.setAutoCommit(false);
         String groupName = request.getParameter("new_group_name");
         boolean suidHasChanged = setSessionSuid(request);
         String gsid = getGsid(request, suidHasChanged);
         String comment = request.getParameter("COMM");

         if (groupName == null)
         {
            // Well, nothing much to do really.
            return true;
         }
         DbIndividual dbInd = new DbIndividual();
         dbInd.CreateGroup(connection,
                           Integer.parseInt((String)
                                            session.getValue("UserID")), 
                           groupName, comment, Integer.parseInt(gsid)); 

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

      commitOrRollback(connection, request, response, "Groups.New.Create",
                       errMessage, "viewGroup", isOk);
      return isOk;
   }

   
   /**
    * Either writes the page used when copying groups or copies a
    * group. What action to take is determined by the paramter oper found
    * in the request object.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @exception IOException If I/O error when handling request.
    * @exception ServletException If request could not be handled.
    */
   private void writeCopy(HttpServletRequest request,
                          HttpServletResponse response)
      throws IOException, ServletException
   {
      HttpSession session = request.getSession(true);
      Connection conn = (Connection) session.getValue("conn");

      // Get the oper parameter and check if it is set to COPY. If it is,
      // copy the group and rewrite the frame
      String oper = request.getParameter("oper");
      if (Utils.assigned(oper) && oper.equals("COPY"))
      {
         if (copyGroup(request, response))
         {
            writeFrame(request, response);
         }
      }

      // Not copy, write the copy page
      else
      {
         writeCopyPage(request, response);
      }
   }


   /**
    * Writes the page used for copying groups.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @exception ServletException If request could not be handled.
    * @exception IOException If I/O error when handling request.
    */
   private void writeCopyPage(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
   {
      HttpSession session = request.getSession(true);
      boolean suidHasChanged = setSessionSuid(request);

      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");

      PrintWriter out = response.getWriter();
      Connection conn =  null;
      Statement stmt = null;
      ResultSet rset = null;

      try
      {
         conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();

         final String pid = (String) session.getValue("PID"); // Project id
         final String suid = (String) session.getValue("SUID"); // Sampling unit id

         //
         // Try get parameters. If not found, set to default values
         //

         // The grouping selected in the main frame.
         final String gsid = getGsid(request, suidHasChanged);
         
         // Grouping to copy from
         String from_gsid = request.getParameter("from_gsid");
         if (!Utils.assigned(from_gsid))
         {
            // If no from grouping found in request, use the grouping
            // selected in the main frame
            from_gsid = gsid;
         }
         
         // Grouping to copy to
         String to_gsid = request.getParameter("to_gsid");
         if (!Utils.assigned(to_gsid))
         {
            to_gsid = Defaults.DEFAULT_GSID;
         }

         // Group to copy from
         String from_gid = request.getParameter("from_gid");
         if (!Utils.assigned(from_gid))
         {
            from_gid = Defaults.DEFAULT_GID;
         }

         // The name of the new group
         String to_gname = request.getParameter("to_gname"); 

         // Holds the name of the component that caused the update of the
         // page. May be "suid" or "from_gsid"
         String item = request.getParameter("item"); 

         // If item not set, this is the first time the page is loaded.
         // Use gsid from request and default gid
         if (item == null)
         {
            from_gsid = gsid;
            to_gsid = gsid;
            from_gid = Defaults.DEFAULT_GID;
         }

         // If the sampling unit id changed, set grouping/groups to default values
         else if (item.equals("suid"))
         {
            from_gsid = Defaults.DEFAULT_GSID;
            to_gsid = Defaults.DEFAULT_GSID;
            from_gid = Defaults.DEFAULT_GID;
         }

         // The groupings did change, set from group to default
         else if (item.equals("from_gsid"))
         {
            from_gid = Defaults.DEFAULT_GID;
         }

         // If no group name found in request, use blank string
         if (to_gname == null)
         {
            to_gname ="";
         }

         // Write the start of the page
         HTMLWriter.doctype(out);
         out.println("<html>\n"
                     + "<head>\n"
                     + "<title>Copy Group</title>");
         printCopyGroupScript(out);
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>\n"
                     + "<body>\n");

         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Groups - Copy</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" " + 
                     "bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<FORM method=\"get\" action=\"" +
                     getServletPath("viewGroup/copy?") + "\">"); 
         
         // Create a statement and query for all sampling units in the
         // current project
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, " +
                                  "NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 "
                                  + "WHERE PID=" + pid +
                                  " ORDER BY NAME ");

         // Create a table and put the sampling units in a selection field
         out.println("<TABLE>\n" +
                     "  <TR>\n" +
                     "    <TD width= 12></TD>\n" +
                     "    <TD width=100 align=left>Sampling Unit</TD>\n" +
                     "  </TR>" +
                     "  <TR>\n" +
                     "    <TD width=12></TD>\n" +
                     "    <TD width=200>\n" +
                     "      <SELECT style=\"WIDTH: 200px\" name=\"suid\" " +
                     "onChange='document.forms[0].item.value=\"suid\";" +
                     "document.forms[0].submit()'>");
         
         // Loop found sampling units and add them to list
         while (rset.next())
         {
            out.print("        <OPTION ");

            // If currently looped SU is the selected one, select this su
            // in the list
            if (suid.equals(rset.getString("SUID")))
            {
               out.print("selected ");
            }
            out.println("value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         rset.close();
         stmt.close();

         out.println("      </SELECT>\n" +
                     "    </TD>\n" +
                     "  </TR>\n" +
                     "  <TR>\n" +
                     "    <TD></TD>\n" +
                     "    <TD>&nbsp;</TD>\n" +
                     "  </TR>\n" +
                     "</TABLE>");

         // The "bar" table
         out.println( "<table bgcolor=\"#008B8B\" border=0 " +
                     "cellpadding=0 cellspacing=0 width=800 >");
         out.println("<tr><td bgcolor=oldlace width=12 height=15>\n" +
                     "</td><td width=338 bgcolor=\"#008B8B\">"
                     +"<b>Copy From</b></td>"
                     +"<td width=100 bgcolor=oldlace></td>"
                     +"<td width=350><b>Copy To</b></td></tr></table>\n");

         out.println( "<table  border=0 cellpadding=0 cellspacing=0 width=800 >");
         out.println("<tr><td width=5 height=15></td></tr>");
         out.println("<tr><td width=5></td></tr>");
         out.println("<tr><td width=5></td></tr>");

         // from-grouping selection list
         out.println("<tr><td width=350 align=left>Grouping</td>"
                     + "<td width=100>&nbsp&nbsp</td>"
                     +"<td width=350>Grouping</td></tr>");
         out.println("<tr><td width=350><select style=\"WIDTH: 200px\" name=\"from_gsid\" " +
                     "onChange='document.forms[0].item.value=\"from_gsid\";" +
                     "document.forms[0].submit()'>");

         // Create statement and query for all groupings within the
         // sampling unit
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT GSID, " +
                                  "NAME FROM gdbadm.V_GROUPINGS_2 WHERE SUID="
                                  + suid + " ORDER BY NAME");

         // Loop all found groupings and add them to the list of "from
         // groupings" 
         while (rset.next())
         {
            out.print("<option ");
            
            // If from_gsid is not set, set variable to the first found
            // grouping 
            if (from_gsid.equals(Defaults.DEFAULT_GSID))
            {
               from_gsid = rset.getString("GSID");
            }

            // If currently looped grouping is the selected one, select it
            // in list
            if (from_gsid.equals(rset.getString("GSID")))
            {
               out.print("selected ");
            }
            
            out.println("value=\"" + rset.getString("GSID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         rset.close();
         stmt.close();
         out.println("</select></td>");

         // Create a statement and query for all groupings wihtin the
         // sampling unit
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT GSID, NAME "
                                  + "FROM gdbadm.V_GROUPINGS_2 WHERE SUID="
                                  + suid + " AND SUID=" + suid + " ORDER BY "
                                  + "NAME"); 

         // to-gouping selection list
         out.println("<td width=75></td><td width=350>\n" +
                     "<select style=\"WIDTH: 200px\" name=\"to_gsid\">");


         // Loop all found groupings and add them to the list
         while (rset.next())
         {
            out.print("<option ");

            // If currently looped grouping is the selected one, select it in
            // the list
            if (to_gsid.equals(rset.getString("GSID")))
            {
               out.print("selected ");
            }
            out.println("value=\"" + rset.getString("GSID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         rset.close();
         stmt.close();
         out.println("</select></td></tr>");

         out.println("<tr><td width=5></td></tr>");

         // group-selection list
         out.println("<tr><td width=350 align=left>Group</td>"
                     +"<td width=75></td><td width=350 align=left>New Group Name</td></tr>");

         // Create statement and query for all groups within current
         // groupings and project
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT GID, NAME FROM gdbadm.V_GROUPS_2 WHERE GSID="
                                  +from_gsid+ " AND SUID="+suid+" ORDER BY NAME");

         //from-group selection field
         out.println("<tr><td width=350><select style=\"WIDTH: 200px\" name=\"from_gid\">");

         // Print all groups.
         while (rset.next())
         {
            out.print("<option ");

            // If currently looped group is the selected one, select it in
            // the list
            if (from_gid.equals(rset.getString("GID")))
            {
               out.print("selected ");
            }
            
            out.println("value=\"" + rset.getString("GID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }

         rset.close();
         stmt.close();

         out.println("</select></td>");

         // to group
         out.println("<td width=100>&nbsp&nbsp</td><td width=350>"+
                     "<input type=text maxlength=20 name=\"to_gname\" value="
                     +  "\"" + to_gname + "\">\n" +
                     "</td>");

         out.println("</tr>");

         out.println("<tr><td width=5></td></tr>");
         out.println("<tr><td width=5></td></tr>");

         out.println("<tr><td>");
         out.println("<input type=button name=Back value=Back style=\"WIDTH: 100px\""
                     +"onClick='location.href=\"" +
                     getServletPath("viewGroup?RETURNING=YES") +
                     "\"'>");
         out.println("<input type=button name=Copy " +
                     "value=Copy style=\"WIDTH: 100px\" " +
                     "onClick='valForm(\"COPY\")'>&nbsp;");

         out.println("</td></tr>");
         out.println("</table>");
         out.println("<INPUT type=hidden name=item value=no>");
         out.println("<INPUT type=hidden name=oper value=\"\">");

         out.println("</FORM>");
         out.println("</body>\n</html>");
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


   /**
    * Writes the script to be used when creating new groups.
    *
    * @param out The PrintWriter to write to.
    */
   private void printCopyGroupScript(PrintWriter out)
   {
      out.println("<script type=\"text/javascript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('COPY' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to copy the group?')) {");
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


   /**
    * Copies a group, ie creates a new group based on an allready existing one.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if group was copied.
    *         False if group was not copied.
    */
   private boolean copyGroup(HttpServletRequest request,
                             HttpServletResponse response)
   {
      String errMessage = null;
      boolean isOk = true;
      HttpSession session = request.getSession(true);
      Connection connection = (Connection) session.getValue("conn");
      String from_gsid, to_gsid, from_gid, g_name, comm;

      try
      {
         connection.setAutoCommit(false);
         g_name = request.getParameter("to_gname");
         from_gsid = request.getParameter("from_gsid");
         to_gsid = request.getParameter("to_gsid");
         from_gid = request.getParameter("from_gid");
         comm = request.getParameter("COMM");

         if (g_name == null || from_gid==null || from_gsid==null ||
             to_gsid==null)
         {
            // Well, nothing much to do really.
            return true;
         }
         DbIndividual dbInd = new DbIndividual();
         dbInd.CopyGroup(connection,
                         Integer.parseInt((String) session.getValue("UserID")),
                         Integer.parseInt(from_gsid),
                         Integer.parseInt(from_gid),
                         Integer.parseInt(to_gsid),
                         g_name,
                         comm // Comment
                         );

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

      commitOrRollback(connection, request, response, "Groups.Copy.Copy",
                       errMessage, "viewGroup", isOk);
      return isOk;
   }


   private void writeDetails(HttpServletRequest request,
                             HttpServletResponse response)
      throws IOException
   {
      // Get the page paramter. If not given, use default value. 
      String page = request.getParameter("page");
      if (page == null)
      {
         page = "DETAILS";
      }

      // Call the correct method 
      if (page.equals("DETAILS"))
      {
         printGroupDetailsPage(request, response);
      }
      else if (page.equals("VIEW_MEMBERS"))
      {
         printMembers(request, response);
      }
      else
      {
         printGroupDetailsPage(request, response);
      }
   }

   
   /**
    * Prints the members of a given group.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @exception IOException If PrintWriter not created.
    */
   private void printMembers(HttpServletRequest request,
                             HttpServletResponse response)  
      throws IOException
   {
      HttpSession session = request.getSession(true);
      Statement stmt = null;
      ResultSet rset = null;
      Connection conn = (Connection) session.getValue("conn");
      response.setContentType("text/html");
      response.setHeader("Cache-Control", "no-cache");
      response.setHeader("Pragma", "no-cache");
      PrintWriter out = response.getWriter();
      try
      {
         // Get the group id, create a statement and query for sampling
         // unit name, grouping name and group name for current group
         String reqGid = request.getParameter("gid");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUNAME, GSNAME, NAME FROM gdbadm.V_GROUPS_2 "
                                  + "WHERE GID=" + reqGid);

         // If data found, construct a title string
         String titleString = "";
         if (rset.next()) 
         {
            titleString = "Members of the group " + rset.getString("NAME") + 
               " in the grouping " + 
               rset.getString("GSNAME") + " (sampling unit: " +
               rset.getString("SUNAME") + ")";
         }

         // Write start of page: DOCTYPE, HTML, open/close HEAD and open
         // BODY
         HTMLWriter.doctype(out);
         HTMLWriter.openHTML(out);
         HTMLWriter.openHEAD(out, titleString);
         HTMLWriter.closeHEAD(out);
         HTMLWriter.openBODY(out,"");

         // Start the table
         out.println("<TABLE cellSpacing=0 cellPadding=4 border=0>");
         out.println("<TR bgcolor=lightskyblue>\n" +
                     "  <TD><B>Identity</B></TD>\n" +
                     "  <TD><B>Alias</B></TD>\n" + 
                     "  <TD><B>Sex</B></TD>\n" +
                     "  <TD><B>Father</B></TD>\n" +
                     "  <TD><B>Mother</B></TD>\n" +
                     "  <TD><B>Birth date</B></TD>\n" +
                     "</TR>");
			
         // Query for all indivinduals within current group
         rset = stmt.executeQuery("SELECT IDENTITY, ALIAS, SEX, FIDENTITY, MIDENTITY, "
                                  + "to_char(BIRTH_DATE, 'YYYY-MM-DD HH24:MI') as TC_BIRTH_DATE "
                                  + "FROM gdbadm.V_INDIVIDUALS_2 WHERE IID "
                                  + "IN(SELECT IID FROM V_SETS_GQL WHERE "
                                  + "GID=" + reqGid + ") order by IDENTITY"); 

         boolean oddRow = true;

         // Loop all found individuals and print them
         while (rset.next())
         {
            if (oddRow)
            {
               out.println("<TR bgcolor=white>");
            }
            else
            {
               out.println("<TR bgcolor=lightgrey>");
            }
            oddRow = !oddRow;
            out.println("  <TD>" + rset.getString("IDENTITY") + "</TD>\n" +
                        "  <TD>" + rset.getString("ALIAS") + "</TD>\n" +
                        "  <TD>" + rset.getString("SEX") + "</TD>\n" +
                        "  <TD>" + rset.getString("FIDENTITY") + "</TD>\n" +
                        "  <TD>" + rset.getString("MIDENTITY") + "</TD>\n" +
                        "  <TD>" + rset.getString("TC_BIRTH_DATE") + "</TD>\n" +
                        "</TR>");
         }
         out.println("</TABLE>");
         HTMLWriter.closeBODY(out);
         HTMLWriter.closeHTML(out);
      }
      catch (SQLException sqle)
      {
         sqle.printStackTrace(System.err);
         out.println("<pre>Unexpected database error!</pre>");
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

   
   /**
    * Prints the page with group details.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @exception IOException If PrintWriter not created.
    */
   private void printGroupDetailsPage(HttpServletRequest request,
                                      HttpServletResponse response)
      
      throws IOException
   {
      HttpSession session = request.getSession(true);

      // set content type and other response header fields first
      response.setContentType("text/html");

      Connection connection =  null;

      // Current and history statement/result set
      Statement currStatement = null;
      ResultSet currResultSet = null;
      Statement histStatement = null;
      ResultSet histResultSet = null;
      
      PrintWriter out = response.getWriter();
      try
      {
         // Get the group id from the request. If not found, use default
         String reqGid = request.getParameter("gid");
         if (reqGid == null || reqGid.trim().equals(""))
         {
            reqGid = "-1";
         }
         			
         connection = (Connection) session.getValue("conn");

         // Create a statement and get all groups with the requested id
         // (should be only one)
         currStatement = connection.createStatement();
         String strSQL = "SELECT GID, NAME, COMM, GSNAME, SUNAME, "
            + "USR, to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS "
            + "FROM gdbadm.V_GROUPS_2 WHERE "
            + "GID = " + reqGid;
         currResultSet = currStatement.executeQuery(strSQL);

         // Create the statement and get all log history for the requested group.
         histStatement = connection.createStatement();
         strSQL = "SELECT NAME, COMM, "
            + "USR, to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS, TS as dummy "
            + "FROM gdbadm.V_GROUPS_LOG WHERE "
            + "GID = " + reqGid + " order by dummy desc";
         histResultSet = histStatement.executeQuery(strSQL);

         String [] data;        // Temporary used when getting data from
                                // result set
         Vector dataRows = new Vector();// All data regarding the group.

         // If current data found, copy data from result set to
         // variable. Then add the variable to the vector
         if (currResultSet.next())
         {
            data = new String[4];
            data[0] = currResultSet.getString("NAME");
            data[1] = currResultSet.getString("COMM");
            data[2] = currResultSet.getString("USR");
            data[3] = currResultSet.getString("TC_TS");
            dataRows.add(data);
         }

         // While history data found, copy data from result set to
         // variable. Then add the variable to the vector
         while (histResultSet.next())
         {
            data = new String[4];
            data[0] = histResultSet.getString("NAME");
            data[1] = histResultSet.getString("COMM");
            data[2] = histResultSet.getString("USR");
            data[3] = histResultSet.getString("TC_TS");
            dataRows.add(data);
         }

         // Start the page by writing doctype, opening head, CSS and
         // scripts 
         HTMLWriter.doctype(out);
         HTMLWriter.openHEAD(out, "");
         HTMLWriter.defaultCSS(out);
         printMemberScript(out);

         // Close the head and open the body
         HTMLWriter.closeHEAD(out);
         HTMLWriter.openBODY(out, "");

         // Now write the header table and open the content table
         HTMLWriter.headerTable(out, 0, Errors.keyValue("Groups.Details"));
         HTMLWriter.contentTableStart(out, 0);

         // Open the details table
         HTMLWriter.openDetailsTable(out, 0);

         // Get static data and write the static data table followed by an
         // empty row.
         String[][] staticData = { { "Sampling Unit", currResultSet.getString("SUNAME") },
                                   { "Grouping", currResultSet.getString("GSNAME")}};
         HTMLWriter.detailsStaticDataTable(out, staticData, 0);
         HTMLWriter.emptyTableRow(out);
         
         // Define column titles and write the data table followed by an
         // empty row.
         String[] dataColumnTitles = new String[] { "Name", "Comment",
                                                    "Last updated by",
                                                    "Last updated"};
         HTMLWriter.detailsDataTable(out, dataColumnTitles, dataRows, 0);
         HTMLWriter.emptyTableRow(out);

         // Write the button table with back button + members button
         HTMLWriter.detailsButtonTable(out, "location.href=\"" +
                                       getServletPath("viewGroup?&RETURNING=YES\""), 
                                       "&nbsp;\n" +
                                       HTMLWriter.button("Members",
                                                         "viewMembers(\"" +
                                                         reqGid + "\")",
                                                         Integer.parseInt(Defaults.DEFAULT_BUTTONWIDTH)), 
                                       0);  
                                       
         // Close the details table, the content table, the body and the
         // html
         out.println("</TABLE>");
         HTMLWriter.comment(out, "++++++++++ Details table, end ++++++++++",
                            false, true); 
         HTMLWriter.contentTableEnd(out);
         HTMLWriter.closeBODY(out);
         HTMLWriter.closeHTML(out);
      }
      catch (SQLException e)
      {
         out.println("<PRE>");
         e.printStackTrace(out);
         out.println("</PRE>");
      }

      finally
      {
         try
         {
            if (currResultSet != null)
            {
               currResultSet.close();
            }
            if (histResultSet != null)
            {
               histResultSet.close();
            }
            if (currStatement != null)
            {
               currStatement.close();
            }
            if (histStatement != null)
            {
               histStatement.close();
            }
         }
         catch (SQLException ignored)
         {
         }
      }	
   }

   
   /**
    * Writes the script used to write member details.
    *
    * @param out The PrintWriter to write to.
    */
   private void printMemberScript(PrintWriter out)
   {
      out.println("<SCRIPT type=\"text/JavaScript\">");
      out.println("<!--");	
      out.println("var memberWindow;"); 
      out.println("function viewMembers(gid) ");
      out.println("{");	
      out.println("  // Define a string with command for opening the member window");
      out.println("  var windowOpenCommand = \"window.open('', 'Members', \";");	
      out.println("  windowOpenCommand += \"'toolbar=no, location=no,\";");
      out.println("  windowOpenCommand += \"directories=no,status=no,\";");	
      out.println("  windowOpenCommand += \"menubar=no, scrollbars=yes, resizable=yes,\";");	
      out.println("  windowOpenCommand += \"width=600,height=400')\";");
      out.println("  //alert(windowOpenCommand);\n");

      out.println("  // Build the URL for the memberWindow. Is done by " +
                  "getting the current URL, stripping of the end and\n" +
                  "  // adding the gid id. First get current ULR");
      out.println("  memberWindowURL = \"\" + self.location;\n");	

      out.println("  // Now get the position of the ?-char");
      out.println("  qPos = memberWindowURL.indexOf('?');");	
      out.println("  //alert('qPos=' + qPos);\n");
      
      out.println("  // If no ?-char found, exit");
      out.println("  if (qPos < 0) return;\n");
      
      out.println("  // The ?-char was found. Strip of the end and " +
                  "build a new URL. Will call this servlet with the \n" +
                  "  // page attribute set to VIEW_MEMBERS and the gid " +
                  "attribute set to the current gid");
      out.println("  memberWindowURL = memberWindowURL.substring(0, qPos + 1);");	
      out.println("  memberWindowURL += 'page=VIEW_MEMBERS&gid=' + gid;");	
      out.println("");	
      out.println("  // alert('memberWindowURL=\"' + memberWindowURL);");
      out.println("  // Evaluate the open command and run it to " +
                  "display the window.");
      out.println("  memberWindow = eval(windowOpenCommand);");	
      out.println("  memberWindow = eval(windowOpenCommand);\n");
      out.println("  // Open the window for writing ");
      out.println("  memberWindow.document.open();\n");	

      out.println("  memberWindow.location = memberWindowURL;");	
      out.println("}");	
      out.println("//-->");	
      out.println("</SCRIPT>");	
      out.println("");	
   }


   /**
    * Main method for handling edit actions. Reads the oper parameter from
    * the request to determine the current edit action. If parameter not
    * set, the edit page is written. If parameter set to delete, the group
    * is deleted and the frame is rewritten. If parameter set to update,
    * the group is updated and the edit page is rewritten
    *
    * @param request The request from client.
    * @param response The response to client.
    * @exception ServletException If request can not be handled.
    * @exception IOException If I/O error when handling request.
    */
   private void writeEdit(HttpServletRequest request,
                          HttpServletResponse response)
      throws ServletException, IOException
   {
      HttpSession session = request.getSession(true);
      Connection connection = (Connection) session.getValue("conn");

      // Get the oper parameter. The paramter is set in the script on the
      // edit page. If not set, use default value.
      String oper = request.getParameter("oper");

      if (oper == null)
      {
         oper = "";
      }

      // User is deleting a group. Delete group and rewrite frame 
      if (oper.equals("DELETE"))
      {
         if (deleteGroup(request, response, connection))
         {
            writeFrame(request, response);
         }
      }

      // User is updating group. Update group and write edit page
      else if (oper.equals("UPDATE"))
      {
         if (updateGroup(request, response, connection))
         {
            writeEditPage(request, response);
         }
      }

      // No action given, write edit page
      else
      {
         writeEditPage(request, response);
      }
   }


   /**
    * Writes the page used for editing groups.
    *
    * @param request The client request.
    * @param response The client response.
    * @exception ServletException If request could not be handled.
    * @exception IOException If I/O error when handling request.
    */
   private void writeEditPage(HttpServletRequest request,
                              HttpServletResponse response) 
      throws ServletException, IOException
   {
      HttpSession session = request.getSession(true);

      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      Statement stmt = null;
      ResultSet rset = null;
		
      try
      {
         // Get the group id from the request. Set it to default if not
         // found in request object.
         String reqGid = request.getParameter("gid");
         if (reqGid == null)
         {
            reqGid = "-1";
         }
         
         Connection connection = (Connection) session.getValue("conn");

         // Create a statement and query for group with given id
         stmt = connection.createStatement();
         String strSQL = "SELECT SUNAME, GSNAME, NAME, COMM, USR, "
            + "to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS " // HIST is obsolute
            + "FROM gdbadm.V_GROUPS_2 WHERE "
            + "GID = " + reqGid;
         rset = stmt.executeQuery(strSQL);
         
         // If group found
         if (rset.next())
         {
            // Write doctype, header, css, script
            HTMLWriter.doctype(out);
            HTMLWriter.openHTML(out);
            HTMLWriter.openHEAD(out, "");
            HTMLWriter.defaultCSS(out);
            writeEditScript(out);
            HTMLWriter.closeHEAD(out);

            // Write the body, the header table and start the content table
            HTMLWriter.openBODY(out, "");
            HTMLWriter.headerTable(out, 0, Errors.keyValue("Groups.Edit"));
            HTMLWriter.contentTableStart(out, 0);
            
            out.println("<table><td></td></table>");
            // the whole information table
	    out.println("<table width=800>" + "<tr><td>");


            // static data table
            out.println("<table border=0 cellpading=0 cellspacing=0 align=left width=300");
            out.println("<tr>");
            out.println("  <td colspan=2 bgcolor=lightgrey>\n" +
                        "    <font size=\"+1\">Static data</font>\n" +
                        "  </td>\n" +
                        "</tr>");

            out.println("<tr><td>Sampling Unit</td><td>" +
                        rset.getString("SUNAME") + "</td></tr>");
            out.println("<tr><td>Grouping</td><td>" +
                        rset.getString("GSNAME") + "</td></tr>");
            out.println("<tr><td>Last updated by</td><td>" +
                        rset.getString("USR") + "</td></tr>");
            out.println("<tr><td>Last updated</td><td>" +
                        rset.getString("TC_TS") + "</td></tr>");
            out.println("</table>");
            // End static table

            out.println("</tr></td>");

            // Create a form and set action. Should call the POST method of
            // this servlet with extended path set to /edit. Also add the
            // modified query string to pass on the original parameters.
            out.println("<FORM action=\"" +
                        getServletPath("viewGroup/edit?") +
                        "gid=" + reqGid +
                        "\" method=\"post\" name=\"FORM1\">");

            // dynamic data table
            out.println("<tr><td>");
            out.println("<table border=0 cellpading=0 cellspacing=0 width=300>");
            out.println("<tr>\n" +
                        "  <td colspan=2 bgcolor=lightgrey>\n" +
                        "    <font size=\"+1\">Changable data</font>\n" +
                        "  </td>\n" +
                        "</tr>");

            out.println("<tr><td width=200>Name</td></tr>" +
                        "<tr><td width=200><input name=\"name\" type=\"text\" maxlength=20 "
                        + "style=\"WIDTH: 200px\" value=\"" +
                        formatOutput(session, rset.getString("NAME"), 20) + "\"></td></tr>");
            out.println("<tr><td width=200>Comment</td></tr>" +
                        "<tr><td width=200><textarea name=\"comm\" id=\"comm\" "
                        + "style=\"HIGHT: 60px; WIDTH: 200px\">");
            out.print( replaceNull(rset.getString("COMM"), "") );
            out.println("</textarea></td></tr>");
            out.println("</table></td></tr>");

            // buttons table
            out.println("<tr><td>");
            out.println("<table border=0 cellpading=0 cellspacing=0 width=300>");
            out.println("<tr>\n" +
                        "  <td width=200>&nbsp;</td>\n" +
                        "  <td width=200>&nbsp;</td>\n" +
                        "  <td width=200>&nbsp;</td>\n" +
                        "  <td width=200>&nbsp;</td>\n" +
                        "</tr>\n" +
                        "<tr>\n" +
                        "  <td colspan=4 align=center>\n" +
                        "    <input type=button name=BACK value=Back " +
                        "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                        getServletPath("viewGroup?&RETURNING=YES") +
                        "\"'>&nbsp;\n" +
                        "    <input type=reset value=Reset " +
                        "style=\"WIDTH: 100px\">&nbsp;\n"  +
                        "    <input type=button id=DELETE " +
                        "name=DELETE value=Delete style=\"WIDTH: 100px\" " +
                        "onClick='valForm(\"DELETE\")'>&nbsp;\n" +
	          	"    <input type=button id=UPDATE name=UPDATE " +
                        "value=Update style=\"WIDTH: 100px\" " +
                        "onClick='valForm(\"UPDATE\")'>&nbsp;");

            out.println("  </td>\n" +
                        "</tr>");
            out.println("</table></td></tr>");

            // Store some extra information needed by doPost()
            out.println("<input type=\"hidden\" ID=\"oper\" NAME=\"oper\" value=\"\">");
            out.println("<input type=\"hidden\" ID=RETURNING NAME=RETURNING value=YES>");

            out.println("</table>");/*end of data table*/
            out.println("</FORM>");
            HTMLWriter.contentTableEnd(out);
            
            out.println("</body>\n</html>");
         } // End of if (rset.next()) {
         else
         {
            out.println("<pre>Unexpected error!\nThere is no group with this group id.</pre>");
         }
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


   /**
    * Writes the script used when confirming an update/delete
    * operation. The script in this method sets the oper value which is
    * used to determine the action in the writeEdit.
    *
    * @param out The PrintWriter to write to.
    */
   private void writeEditScript(PrintWriter out)
   {
      out.println("<script type=\"text/JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to delete the group?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the group?')) {");
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


   /**
    * Updates a group.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @param connection The connection to use.
    * @return True if group updated.
    *         False if group not updated.
    */
   private boolean updateGroup(HttpServletRequest request,
                               HttpServletResponse response,
                               Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         String userId = (String) session.getValue("UserID");
         int groupId = Integer.parseInt(request.getParameter("gid"));
         String groupName = request.getParameter("name");
         String comment = request.getParameter("comm");
         DbIndividual dbIndividual = new DbIndividual();
         dbIndividual.UpdateGroup(connection, groupId, groupName, comment,
                         Integer.parseInt(userId));
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

      commitOrRollback(connection, request, response,
                       "Groups.Edit.Update", errMessage, "viewGroup",
                       isOk);
      return isOk;

   }


   /**
    * Deletes a group.
    *
    * @param request The request from the client.
    * @param response The response to the client.
    * @param connection The connection to use.
    * @return True if group deleted.
    *         False if group not deleted.
    */
   private boolean deleteGroup(HttpServletRequest request,
                               HttpServletResponse response,
                               Connection connection)  
   { 
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         int groupId = Integer.parseInt(request.getParameter("gid"));
         DbIndividual dbIndividual = new DbIndividual();
         dbIndividual.DeleteGroup(connection, groupId);
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

      commitOrRollback(connection, request, response,
                       "Groups.Edit.Delete", errMessage, "viewGroup",
                       isOk);
      return isOk;
   }


}
