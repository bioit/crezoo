/*
  $Log$
  Revision 1.8  2005/02/21 11:55:42  heto
  Converting Genotypes to PostgreSQL

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

  Revision 1.3  2002/10/22 06:08:11  heto
  rebuilt the "back-buttons".
  Dont save the request object, save the URL instead.
  New function.

  Revision 1.2  2002/10/18 11:41:10  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:05  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.13  2002/01/31 15:54:08  roca
  Additional fixes of javascript for mac, Genotype import mm

  Revision 1.12  2002/01/29 18:03:13  roca
  Changes by roca (se funktionsbskrivnig for LF025)

  Revision 1.11  2001/05/31 07:07:03  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.10  2001/05/29 13:04:37  frob
  Removed unused method.

  Revision 1.9  2001/05/22 06:16:56  roca
  Backfuncktionality fixed for all (?) servlets/subpages

  Revision 1.8  2001/05/14 11:26:00  frob
  Removed writeError(), forgot that in last checkin :-P

  Revision 1.7  2001/05/11 11:06:25  frob
  Changed all methods that used to call writeError() to write an error page. These
  methods now calls the general method commitOrRollback() which does the commit or
  rollback operation as well as handle any errors. writeError() was removed.

  Revision 1.6  2001/05/03 14:21:00  frob
  Implemented local version of errorQueryString and changed writeError to use this method.

  Revision 1.5  2001/05/03 07:57:41  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.4  2001/05/03 05:56:23  frob
  Calls to removeOper, removeSuid and removeCid changed to use the general removeQSParameter.
  The previously called methods are removed.

  Revision 1.3  2001/04/24 09:33:55  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:28  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.4  2001/04/18 09:26:45  frob
  Removed the size of the main table used on the webpages.

  Revision 1.1.1.1.2.3  2001/04/12 06:35:15  frob
  createMarkFile: Call to Parse() now passes valid file type definitions.
                  No longer uses the delimiters from the request.
                  Layout fix.
  writeImpFilePage: No longer displays the delimiter field.
                    File name field resized.
                    HTML validated.

  Revision 1.1.1.1.2.2  2001/03/28 13:47:55  frob
  Added catch() for InputDataFileException which can be raised from
  the parse()-method.
  Added check that the connection object is created before doing
  rollback on it (to avoid null pointer exception).

  Revision 1.1.1.1.2.1  2001/03/28 12:37:47  frob
  Changed the call to the FileParser constructor.
  Indeted the file and added the log header.

*/

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;
import se.arexis.agdb.util.FileImport.*;

import com.oreilly.servlet.MultipartRequest;

public class viewMark extends SecureArexisServlet
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
      } else if (extPath.equals("/impLib")) {
         writeImpLib(req, res);
      } else if (extPath.equals("/impFile")) {
         writeImpFile(req, res);
      } else if (extPath.equals("/alleles")) {
         writeAlleles(req, res);
      } else if (extPath.equals("/newAllele")) {
         writeNewAllele(req, res);
      } else if (extPath.equals("/editAllele")) {
         writeEditAllele(req, res);
      } else if (extPath.equals("/detailsAllele")) {
         writeAlleleDetails(req, res);
      } else if (extPath.equals("/impMultipart")) {
         createMarkFile(req, res);
      }

   }
   private void writeFrame(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
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
          //req= getServletState(req,session);


         // String incoming = req.getQueryString();
         //      System.err.println("in="+incoming);
         String topQS = buildQS(req);
         // we don't want the oper parameter anaywhere but in the "edit" and "new" page!
         topQS = removeQSParameterOper(topQS);
         //      System.err.println("in2="+topQS);

         String bottomQS = topQS.toString();
         out.println("<html>"
                     + "<HEAD>"
                     + " <TITLE>View Markers units</TITLE>"
                     + "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\"></HEAD>"
                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" //

                     + "<frame name=\"viewtop\" "
                     + "src=\"" +getServletPath("viewMark/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewMiddle\" "
                     + "src=\""+ getServletPath("viewMark/middle?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewbottom\""
                     + "src=\"" +getServletPath("viewMark/bottom?") + bottomQS + "\" "
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
         sid = null,
         cid = null, // Chromosome id
         mid = null,
         mname=null,
         orderby = null;
      String item; // Tells which paramater that has changed.
      String pid = (String) session.getValue("PID");
      cid = req.getParameter("cid");
      mid = req.getParameter("mid");
      suid = req.getParameter("suid");
      item = req.getParameter("item");
      mname= req.getParameter("mname");
      if (item == null || suid == null) {
         suid = findSuid(conn, pid);
         cid = "*"; // findCid(conn, suid);
      } else if (item.equals("suid")) {
         cid = "*"; //findCid(conn, suid);
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

      // Set the parameters STARTINDEX and ROWS
      if (!action.equals("NOP"))
         output.append(setIndecis(suid, cid, mname, item, action, req, session));
      output.append("&cid=").append(cid);
      if(suid != null)
      {
         output.append("&suid=").append(suid);
      }
      if (mid != null)
         output.append("&mid=").append(mid);
      if (mname != null)
         output.append("&mname=").append(mname);

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
         rset = stmt.executeQuery("SELECT SID FROM V_SPECIES_2 WHERE " +
                                  "PID=" + pid + " ORDER BY NAME");
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

   private String findSuid(Connection conn, String pid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID FROM V_ENABLED_SAMPLING_UNITS_2 WHERE PID=" +
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
   private String findCid(Connection conn, String suid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID FROM V_CHROMOSOMES_1 WHERE " +
                                  "SID=(SELECT SID FROM V_ENABLED_SAMPLING_UNITS_1 WHERE SUID=" + suid + ") " +
                                  "ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME");
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
   private String findCidFromSid(Connection conn, String sid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID FROM gdbadm.V_CHROMOSOMES_1 WHERE " +
                                  "SID=" + sid + " " +
                                  "ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME");
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
   private String setIndecis(String suid, String cid,String mname, String item_changed, String action, HttpServletRequest req, HttpSession session) {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(suid, cid, mname, req, session);
      maxRows = getMaxRows(session);
      if (req.getParameter("STARTINDEX") != null && 
          item_changed != null && 
          item_changed.equals("no") ) {
         startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         if (rows > 0 && startIndex == 0)
            startIndex=1;
      } else
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
	
   private int countRows(String suid, String cid, String mname, HttpServletRequest req, HttpSession session) {
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer(512);
      try {
         sbSQL.append("SELECT count(*) " +
                      "FROM gdbadm.V_MARKERS_1 WHERE " +
                      "SUID=" + suid + " ");
         if (!"*".equals(cid))
            sbSQL.append("AND CID=" + cid + " ");
         if(!("").equals(mname.trim())&& mname != null)
         {
              String temp_name =mname;
              temp_name=temp_name.toString().replace('*', '%');
               sbSQL.append("AND NAME like '" + temp_name+ "' ");
         }

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
      String orderby = null;

      StringBuffer filter = new StringBuffer(256);
      //		orderby = req.getParameter("ORDERBY");
      //
      //		if (orderby != null && !"".equalsIgnoreCase(orderby)) {
      //			if (orderby.equals("CNAME"))
      //				filter.append(" order by gdbadm.TO_NUMBER_ELSE_NULL(CNAME), CNAME");
      //			else
      //				filter.append(" order by " + orderby);
      //		} else
      //			filter.append(" order by NAME");

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
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
      Statement stmt = null;
      ResultSet rset = null;
      int startIndex = 0, rows = 0, maxRows = 0;
      String cid, marker, orderby, oldQS, newQS, action, pid, suid, mname;
      int privileges[] = (int[]) session.getValue("PRIVILEGES");
      try {
         conn = (Connection) session.getValue("conn");

         suid= req.getParameter("suid");
         cid = req.getParameter("cid");
        // System.err.println(req.getQueryString());

         mname =req.getParameter("mname");
         maxRows = getMaxRows(session);
         action = req.getParameter("ACTION");
         oldQS = req.getQueryString();
         newQS = buildTopQS(oldQS);

         orderby = req.getParameter("ORDERBY");
         pid = (String) session.getValue("PID");


         if (req.getParameter("STARTINDEX") != null)
            startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         else
            startIndex = 0;
         if (req.getParameter("ROWS") != null)
            rows = Integer.parseInt(req.getParameter("ROWS"));
         else
            rows = 0;
         if (orderby == null) orderby = "NAME";
         if (action == null) action = "NOP";

         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+ getURL("style/view.css") +"\">");
         out.println("<base target=\"content\">");

         writeTopScript(out);
         out.println("<title>View Markers</title>");
         out.println("</head>");

         out.println("<body bgcolor=\"#ffffd0\">"
                     +"<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
                     +"<tr>"
                     +"<td width=\"14\" rowspan=\"3\"></td>"
                     +"<td width=\"736\" colspan=\"2\" height=\"15\">"
                     +"<form method=get action=\"" +getServletPath("viewMark")+"\">"
                     +"<p align=\"center\"><font size=\"2\"><b  style=\"font-size: 15pt\">Markers</b>"
                     +"</font></td></tr>"
                     +"<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>"
                     +"</tr><tr><td width=\"517\">");

         out.println("<table width=488 height=\"92\">"
                     +"<td><b>Sampling Units</b><br><select name=suid "
                     +"name=select onChange='selChanged(\"suid\");'  style=\"HEIGHT: 22px; WIDTH: 126px\">");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SUID, SID FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID="+ pid + " ORDER BY NAME");
         while (rset.next()) {
            if (suid != null && suid.equals(rset.getString("SUID")))        {
               out.println("<OPTION selected value=\"" + rset.getString("SUID") + "\">" +
                           rset.getString("NAME")+ "</option>\n");
            } else {
               out.println("<OPTION value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME")+"</option>\n");
               // first value read
               if(suid == null)
                  suid = rset.getString("SUID");
            }
         }
         rset.close();
         stmt.close();
         out.println("</SELECT></td>");

         out.println("<td><b>Chromosome</b><br><select name=cid " +
                     "width=126 style=\"HEIGHT: 22px; WIDTH: 126px\">");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM V_CHROMOSOMES_1 WHERE " +
                                  "SID=(SELECT SID FROM V_ENABLED_SAMPLING_UNITS_1 WHERE SUID=" +
                                  suid + ") ORDER BY " +
                                  "TO_NUMBER_ELSE_NULL(NAME), NAME");
         out.println("<option " +
                     ("*".equals(cid) ? "selected " : "") +
                     "value=\"*\">*</option>");
         while (rset.next()) {
            if (cid != null && cid.equals(rset.getString("CID")))
               out.println("<OPTION selected value=\"" + rset.getString("CID") + "\">" +
                           rset.getString("NAME"));
            else
               out.println("<OPTION value=\"" + rset.getString("CID") + "\">" + rset.getString("NAME"));
         }
         out.println("</SELECT></td>");

//------------------------------


         out.println("<td><b>Marker name</b><br>");
         out.println("<input name=mname value=\"" + replaceNull(mname, "") +
                     "\" style=\"WIDTH: 100px\" width=100>");
         out.println("</td>");

//-----------------------


         out.println("</td></table></td>");

         //Buttons
         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");
         out.println("<input type=button value=\"New Marker\" " +
                     privDependentString(privileges, MRK_W,
                                         "onClick='parent.location.href=\"" +getServletPath("viewMark/new?item=no&") + newQS + "\"' ",
                                         "disabled ") +
                     "height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">");
         out.println("</td>");

         out.println("<tr><td width=68 colspan=2>"
                     +"<input id=COUNT name=COUNT type=submit value=\"Count\" width=\"69\""
                     +" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">"
                     +"</td>"
                     +"<td width=68 colspan=2>"
                     +"<input id=DISPLAY name=DISPLAY type=submit value=\"Display\""
                     +" width=\"70\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">"
                     +"</td></tr>");


         // some hidden values
         // THESE ARE THE ORIINAL FOR VIEW MARK..
         out.println("<input type=\"hidden\" id=\"STARTINDEX\" name=\"STARTINDEX\" value=\"" + startIndex + "\">");
         out.println("<input type=\"hidden\" id=\"ORDERBY\" name=\"ORDERBY\" value=\"" + orderby + "\">");
         out.println("<input type=\"hidden\" id=\"oper\" name=\"oper\" value=\"\">");
         out.println("<input type=\"hidden\" id=\"new_name\" name=\"new_name\" value=\"\">");
         out.println("<input type=hidden name=item value=no>");

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

      } catch (Exception e)	{
         out.println("<PRE>");
         e.printStackTrace(out);
         out.println("</PRE>");
      } finally {
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
         output = "Displaying " + startIndex + "-" + upperLimit +
            " of " + rows + " rows.";
      } else if (action.regionMatches(true, 0, "PREV", 0, "PREV".length())) {
         // Print the current row intervall
         output = "Displaying " + startIndex + "-" + upperLimit +
            " of " + rows + " rows.";
      } else if (action.regionMatches(true, 0, "TOP", 0, "TOP".length())) {
         // Print the current row intervall
         output = "Displaying " + startIndex + "-" + upperLimit +
            " of " + rows + " rows.";
      } else if (action.regionMatches(true, 0, "END", 0, "END".length())) {
         // Print the current row intervall
         output = "Displaying " + startIndex + "-" + upperLimit +
            " of " + rows + " rows.";
      } else if ("COUNT".equalsIgnoreCase(action)) {
         // Count the number of rows this filter will return
         output = "Query will return " + rows + " rows.";
      } else if ("DISPLAY".equalsIgnoreCase(action)) {
         // print the current row intervall
         output = "Displaying " + startIndex + "-" + upperLimit +
            " of " + rows + " rows.";
      } else if ("NOP".equalsIgnoreCase(action)) {
         // Print something that isn't visible (to make the frame as big as it would be in the cases above
         output = "&nbsp;";
      } else {
         // ???
         output = "?" + action + "?";
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
      if (req.getParameter("STARTINDEX") != null)
         startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
      else
         startIndex = 0;
      if (req.getParameter("ROWS") != null)
         rows = Integer.parseInt(req.getParameter("ROWS"));
      else
         rows = 0;
      maxRows = getMaxRows(session);//Integer.parseInt((String) session.getValue("MaxRows"));


      try {
         out.println("<html>\n<head>\n<link rel=\"stylesheet\" type=\"text/css\" href=\""+getURL("style/tableBar.css")+"\">");
         out.println("<base target=\"content\"></head><body>");

         if(action != null)
         {
            out.println("&nbsp;" + buildInfoLine(action, startIndex, rows, maxRows));
         }

         String oldQS, newQS;
         oldQS = req.getQueryString();
         String choosen= req.getParameter("ORDERBY");
         newQS = buildTopQS(oldQS);
         /*
           out.println("<table bgcolor=\"#008B8B\" border=0 cellpading=0 cellspacing=0 height=20 width=795 style=\"margin-left:2px\">");
           out.println("<td width=5>&nbsp;</td>");
         */

         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=795 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");


         // the menu choices
         //chromosome
         out.println("<td width=100><a href=\"" + getServletPath("viewMark")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=CNAME\">");
         if(choosen.equals("CNAME"))
            out.println("<FONT color=saddlebrown ><b>Chrom</b></FONT></a></td>\n");
         else out.println("Chrom</a></td>\n");
         //Marker
         out.println("<td width=100><a href=\"" + getServletPath("viewMark")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=NAME\">");
         if(choosen.equals("NAME"))
            out.println("<FONT color=saddlebrown><b>Marker</b></FONT></a></td>\n");
         else out.println("Marker</a></td>\n");
         //Comment
         out.println("<td width=150><a href=\"" + getServletPath("viewMark")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\">");
         if(choosen.equals("COMM"))
            out.println("<FONT color=saddlebrown><b>Comment</b></FONT></a></td>\n");
         else out.println("Comment</a></td>\n");
         //USER
         out.println("<td width=100><a href=\"" + getServletPath("viewMark")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
         else out.println("User</a></td>\n");

         //Updated
         out.println("<td width=120><a href=\"" + getServletPath("viewMark")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TS\">");
         if(choosen.equals("TS"))
            out.println("<FONT color=saddlebrown><b>Updated</b></FONT></a></td>\n");
         else out.println("Updated</a></td>\n");

         /*
           out.println("<td width=60>&nbsp;</td>"); // Alleles link
           out.println("<td width=80>&nbsp;</td>");
           out.println("<td width=80>&nbsp;</td>");
           out.println("</table>");
           out.println("</body></html>");
         */

         out.println("<td width=60>&nbsp;</td>");
         out.println("<td width=80>&nbsp;</td>");
         out.println("<td width=80>&nbsp;</td>");
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
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
      Statement stmt = null;
      ResultSet rset = null;
      Connection conn = null;
      int privileges[] = (int[]) session.getValue("PRIVILEGES");
      try
      {
         String pid = null, cid = null, suid = null,  action = null, mname=null;
         String oldQS = req.getQueryString();
         action = req.getParameter("ACTION");
         suid = req.getParameter("suid");
         cid = req.getParameter("cid");
         mname = req.getParameter("mname");
         pid = (String) session.getValue("PID");
         if (pid == null) pid = "-1";
         if (action == null || action.equalsIgnoreCase("NOP") ||
             action.equalsIgnoreCase("COUNT") ||
             suid == null || cid == null)
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

         sbSQL.append("SELECT CNAME, NAME, COMM, " +
                      "USR, to_char(TS, '" + getDateFormat(session) +"') as TC_TS, MID " +
                      "FROM gdbadm.V_MARKERS_3 WHERE " +
                      "SUID=" + suid + " ");
         if (!"*".equals(cid))
            sbSQL.append("AND CID=" + cid+ " ");
         if(!("").equals(mname.trim())&& mname != null)
         {
              String temp_name =mname;
              temp_name=temp_name.toString().replace('*', '%');
               sbSQL.append("AND NAME like '" + temp_name+ "' ");
         }

         String qs = req.getQueryString();
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);
         String orderby = req.getParameter("ORDERBY");
         if (orderby != null && !"".equalsIgnoreCase(orderby)) {
            if (orderby.equals("CNAME"))
               sbSQL.append(" order by gdbadm.TO_NUMBER_ELSE_NULL(CNAME), CNAME");
            else
               sbSQL.append(" order by " + orderby);
         } else
            sbSQL.append(" order by NAME");
           // System.err.println(sbSQL.toString());
         rset = stmt.executeQuery(sbSQL.toString());
         out.println("<TABLE align=left border=0 cellpading=0");
         out.println("cellspacing=0 width=795 style=\"margin-left: 2px\">");
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
            out.println("<TD width=5>&nbsp</TD>");
            out.println("<TD width=100>" + rset.getString("CNAME") + "</TD>");
            out.println("<TD width=100>" + rset.getString("NAME") + "</TD>");
            out.println("<TD width=150>" + formatOutput(session, rset.getString("COMM"),15) + "</TD>");
            out.println("<TD width=100>"+ rset.getString("USR") + "</TD>");
            out.println("<TD width=120>"+ rset.getString("TC_TS") + "</TD>");
            out.println("<td width=70><a href=\"" + getServletPath("viewMark/alleles?") + "mid=" +
                        rset.getString("MID") + "&mname=" + rset.getString("NAME")+"&item=no&" + oldQS + "\" target=\"content\">Alleles</a></td>");
            out.println("<TD WIDTH=70>"+"<A HREF=\"" +getServletPath("viewMark/details?")+"mid="
                        + rset.getString("MID") + "&item=no&" +
                        oldQS + "\" target=\"content\">Details</A></TD>");

            out.println("<TD WIDTH=80>");
            out.println(privDependentString(privileges, MRK_W,
                                            "<A HREF=\""+getServletPath("viewMark/edit?")+"mid="
                                            + rset.getString("MID")
                                            + "&" + oldQS + "\" target=\"content\">Edit</A>",
                                            "<font color=tan>Edit</font>"));
            out.println("</TD></TR>");
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
    * The marker's detail page
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
      String prev_chrom = null;
      String prev_p1 = null;
      String prev_p2 = null;
      String prev_position = null;
      String prev_name = null;
      String prev_alias = null;
      String prev_comm = null;
      String prev_usr = null;
      String prev_ts = null;
      String curr_chrom = null;
      String curr_p1 = null;
      String curr_p2 = null;;
      String curr_position = null;
      String curr_name = null;
      String curr_alias = null;
      String curr_comm = null;
      String curr_usr = null;
      String curr_ts = null;
      boolean has_history = false;

      try {

         String oldQS = buildQS(req);
         String mid = req.getParameter("mid");
         if (mid == null || mid.trim().equals("")) mid = "-1";

         conn = (Connection) session.getValue("conn");
         // Get the current data
         stmt_curr = conn.createStatement();
         String strSQL = "SELECT SNAME, SUNAME, CNAME, NAME, ALIAS, COMM, " +
            "P1, P2, POSITION, USR, " +
            "to_char(TS, '"+ getDateFormat(session) + "') as TC_TS "
            + "FROM gdbadm.V_MARKERS_3 WHERE "
            + "MID=" + mid ;
         rset_curr = stmt_curr.executeQuery(strSQL);

         // Get the history
         stmt_hist = conn.createStatement();
         strSQL = "SELECT NAME, ALIAS, P1, P2, POSITION, CNAME, COMM, USR, " +
            "to_char(TS , '" + getDateFormat(session) + "') as TC_TS, TS as dummy " +
            "FROM gdbadm.V_MARKERS_LOG " +
            "WHERE MID=" + mid + " " +
            "ORDER BY dummy desc";


         rset_hist = stmt_hist.executeQuery(strSQL);

         // We baldly assume that this is alright (why shouldn't we?)
         rset_curr.next();
         curr_name = rset_curr.getString("NAME");
         curr_alias = rset_curr.getString("ALIAS");
         curr_p1 = rset_curr.getString("P1");
         curr_p2 = rset_curr.getString("P2");
         curr_position = rset_curr.getString("POSITION");
         curr_chrom = rset_curr.getString("CNAME");
         curr_comm = rset_curr.getString("COMM");
         curr_usr = rset_curr.getString("USR");
         curr_ts = rset_curr.getString("TC_TS"); // Time stamp

         if (rset_hist.next()) {
            prev_name = rset_hist.getString("NAME");
            prev_alias = rset_hist.getString("ALIAS");
            prev_p1 = rset_hist.getString("P1");
            prev_p2 = rset_hist.getString("P2");
            prev_position = rset_hist.getString("POSITION");
            prev_chrom = rset_hist.getString("CNAME");
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
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Genotypes - Markers - Details</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td></td></tr>");
         out.println("<tr><td></td><td>");

         out.println("<table nowrap border=0 cellSpacing=0>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
         out.println("<tr><td>Species</td><td>" + rset_curr.getString("SNAME") + "</td></tr>");
         out.println("<tr><td>Sampling unit</td><td>" + rset_curr.getString("SUNAME") + "</td></tr>");
         out.println("</table><br><br>");

         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");

         out.println("<table nowrap align=center border=0 cellSpacing=0 width=760px>");
         out.println("<tr bgcolor=black><td align=center colspan=9><b><font color=\"#ffffff\">Current Data</font></b></td></tr>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td nowrap WIDTH=100>Name</td>");
         out.println("<td nowrap WIDTH=100>Alias</td>");
         out.println("<td nowrap width=80>Primer 1</td>");
         out.println("<td nowrap width=80>Primer 2</td>");
         out.println("<td nowrap width=40>Position</td>");
         out.println("<td width=50>Chrom</td>");
         out.println("<td nowrap WIDTH=140>Comment</td>");
         out.println("<td nowrap WIDTH=50>User</td>");
         out.println("<td nowrap WIDTH=120>Last updated</td></tr>");

         out.println("<tr bgcolor=white>");
         // Name
         out.println("<td>");
         if (("" + curr_name).equals("" + prev_name))
            out.println(formatOutput(session, curr_name, 12));
         else
            out.println("<font color=red>" + formatOutput(session, curr_name, 12) + "</font>");
         out.println("</td>");
         // Alias
         out.println("<td>");
         if (("" + curr_alias).equals("" + prev_alias))
            out.println(formatOutput(session, curr_alias, 12));
         else
            out.println("<font color=red>" + formatOutput(session, curr_alias, 12) + "</font>");
         out.println("</td>");
         // Primer 1
         out.println("<td>");
         if (("" + curr_p1).equals("" + prev_p1))
            out.println(formatOutput(session, curr_p1, 10));
         else
            out.println("<font color=red>" + formatOutput(session, curr_p1, 10) + "</font>");
         out.println("</td>");
         // Primer 2
         out.println("<td>");
         if (("" + curr_p2).equals("" + prev_p2))
            out.println(formatOutput(session, curr_p2, 10));
         else
            out.println("<font color=red>" + formatOutput(session, curr_p2, 10) + "</font>");
         out.println("</td>");
         // Position
         out.println("<td>");
         if (("" + curr_position).equals("" + prev_position))
            out.println(formatOutput(session, curr_position, 4));
         else
            out.println("<font color=red>" + formatOutput(session, curr_position, 4) + "</font>");
         out.println("</td>");
         // Chromosome
         out.println("<td>");
         if (("" + curr_chrom).equals("" + prev_chrom))
            out.println(formatOutput(session, curr_chrom, 3));
         else
            out.println("<font color=red>" + formatOutput(session, curr_chrom, 4) + "</font>");
         out.println("</td>");
         // Comment
         out.println("<td>");
         if (("" + curr_comm).equals("" + prev_comm))
            out.println(formatOutput(session, curr_comm, 15));
         else
            out.println("<font color=red>" + formatOutput(session, curr_comm, 14) + "</font>");
         out.println("</td>");
         // User
         out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
         out.println("<td>" + formatOutput(session, curr_ts, 16) + "</td>"); // Last updated
         out.println("</tr>");
         out.println("<tr bgcolor=Black>");
         out.println("<td align=center colspan=9><b><font color=\"#ffffff\">History</font></b></td></tr>");

         curr_name = prev_name;
         curr_alias = prev_alias;
         curr_p1 = prev_p1;
         curr_p2 = prev_p2;
         curr_position = prev_position;
         curr_chrom = prev_chrom;
         curr_comm = prev_comm;
         curr_usr = prev_usr;
         curr_ts = prev_ts;

         boolean odd = true;
         while (rset_hist.next()) {
            prev_name = rset_hist.getString("NAME");
            prev_alias = rset_hist.getString("ALIAS");
            prev_p1 = rset_hist.getString("P1");
            prev_p2 = rset_hist.getString("P2");
            prev_position = rset_hist.getString("POSITION");
            prev_chrom = rset_hist.getString("CNAME");
            prev_comm = rset_hist.getString("COMM");
            prev_usr = rset_hist.getString("USR");
            prev_ts = rset_hist.getString("TC_TS");
            if (odd)
               out.println("<tr bgcolor=white>");
            else
               out.println("<tr bgcolor=lightgrey>");
            odd = !odd;
            // Name
            out.println("<td>");
            if (("" + curr_name).equals("" + prev_name))
               out.println(formatOutput(session, curr_name, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_name, 12) + "</font>");
            out.println("</td>");
            // Alias
            out.println("<td>");
            if (("" + curr_alias).equals("" + prev_alias))
               out.println(formatOutput(session, curr_alias, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_alias, 12) + "</font>");
            out.println("</td>");
            // Primer 1
            out.println("<td>");
            if (("" + curr_p1).equals("" + prev_p1))
               out.println(formatOutput(session, curr_p1, 10));
            else
               out.println("<font color=red>" + formatOutput(session, curr_p1, 10) + "</font>");
            out.println("</td>");
            // Primer 2
            out.println("<td>");
            if (("" + curr_p2).equals("" + prev_p2))
               out.println(formatOutput(session, curr_p2, 10));
            else
               out.println("<font color=red>" + formatOutput(session, curr_p2, 10) + "</font>");
            out.println("</td>");
            // Position
            out.println("<td>");
            if (("" + curr_position).equals("" + prev_position))
               out.println(formatOutput(session, curr_position, 4));
            else
               out.println("<font color=red>" + formatOutput(session, curr_position, 4) + "</font>");
            out.println("</td>");
            // Chromosome
            out.println("<td>");
            if (("" + curr_chrom).equals("" + prev_chrom))
               out.println(formatOutput(session, curr_chrom, 3));
            else
               out.println("<font color=red>" + formatOutput(session, curr_chrom, 4) + "</font>");
            out.println("</td>");
            // Comment
            out.println("<td>");
            if (("" + curr_comm).equals("" + prev_comm))
               out.println(formatOutput(session, curr_comm, 14));
            else
               out.println("<font color=red>" + formatOutput(session, curr_comm, 15) + "</font>");
            out.println("</td>");
            // User
            out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
            out.println("<td>" + formatOutput(session, curr_ts, 16) + "</td>"); // Last updated
            out.println("</tr>");

            curr_name = prev_name;
            curr_alias = prev_alias;
            curr_p1 = prev_p1;
            curr_p2 = prev_p2;
            curr_position = prev_position;
            curr_chrom = prev_chrom;
            curr_comm = prev_comm;
            curr_usr = prev_usr;
            curr_ts = prev_ts;
         }
         if (has_history) {
            if (odd) {
               out.println("<tr bgcolor=white>");
               odd = false;
            } else {
               out.println("<tr bgcolor=lightgrey>");
               odd = true;
            }
            // Name
            out.println("<td>");
            if (("" + curr_name).equals("" + prev_name))
               out.println(formatOutput(session, curr_name, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_name, 12) + "</font>");
            out.println("</td>");
            // Alias
            out.println("<td>");
            if (("" + curr_alias).equals("" + prev_alias))
               out.println(formatOutput(session, curr_alias, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_alias, 12) + "</font>");
            out.println("</td>");
            // Primer 1
            out.println("<td>");
            if (("" + curr_p1).equals("" + prev_p1))
               out.println(formatOutput(session, curr_p1, 10));
            else
               out.println("<font color=red>" + formatOutput(session, curr_p1, 10) + "</font>");
            out.println("</td>");
            // Primer 2
            out.println("<td>");
            if (("" + curr_p2).equals("" + prev_p2))
               out.println(formatOutput(session, curr_p2, 10));
            else
               out.println("<font color=red>" + formatOutput(session, curr_p2, 10) + "</font>");
            out.println("</td>");
            // Position
            out.println("<td>");
            if (("" + curr_position).equals("" + prev_position))
               out.println(formatOutput(session, curr_position, 4));
            else
               out.println("<font color=red>" + formatOutput(session, curr_position, 4) + "</font>");
            out.println("</td>");
            // Chromosome
            out.println("<td>");
            if (("" + curr_chrom).equals("" + prev_chrom))
               out.println(formatOutput(session, curr_chrom, 3));
            else
               out.println("<font color=red>" + formatOutput(session, curr_chrom, 4) + "</font>");
            out.println("</td>");
            // Comment
            out.println("<td>");
            if (("" + curr_comm).equals("" + prev_comm))
               out.println(formatOutput(session, curr_comm, 14));
            else
               out.println("<font color=red>" + formatOutput(session, curr_comm, 15) + "</font>");
            out.println("</td>");
            // User
            out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
            out.println("<td>" + formatOutput(session, curr_ts, 16) + "</td>"); // Last updated
            out.println("</tr>");
         }

         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         // Back button
         out.println("<form>");
         out.println("<table cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("viewMark?item=no&") + oldQS + "\"'>&nbsp;");
         out.println("</td></tr></table>");

         out.println("</form>");

         out.println("</td></tr></table>");
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

   private void writeAlleleDetails(HttpServletRequest req, HttpServletResponse res)
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
         String aid = req.getParameter("aid");
         if (aid == null) aid = "-1";

         conn = (Connection) session.getValue("conn");
         // Get the current data
         stmt_curr = conn.createStatement();
         String strSQL = "SELECT SNAME, SUNAME, CNAME, MNAME, NAME, COMM, " +
            "USR, to_char(TS, '"+ getDateFormat(session) + "') as TC_TS "
            + "FROM V_ALLELES_3 WHERE "
            + "AID=" + aid ;
         rset_curr = stmt_curr.executeQuery(strSQL);

         // Get the history
         stmt_hist = conn.createStatement();
         strSQL = "SELECT NAME, COMM, USR, " +
            "to_char(TS , '" + getDateFormat(session) + "') as TC_TS, TS as dummy " +
            "FROM V_ALLELES_LOG " +
            "WHERE AID=" + aid + " " +
            "ORDER BY dummy desc";


         rset_hist = stmt_hist.executeQuery(strSQL);

         // We baldly assume that this is alright (why shouldn't we?)
         rset_curr.next();
         curr_name = rset_curr.getString("NAME");
         curr_comm = rset_curr.getString("COMM");
         curr_usr = rset_curr.getString("USR");
         curr_ts = rset_curr.getString("TC_TS"); // Time stamp

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
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Genotypes - Markers - Alleles - Details</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td></td></tr>");
         out.println("<tr><td></td><td>");

         out.println("<table nowrap border=0 cellSpacing=0>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
         out.println("<tr><td>Species</td><td>" + rset_curr.getString("SNAME") + "</td></tr>");
         out.println("<tr><td>Chromosome</td><td>" + rset_curr.getString("CNAME") + "</td></tr>");
         out.println("<tr><td>Sampling unit</td><td>" + rset_curr.getString("SUNAME") + "</td></tr>");
         out.println("<tr><td>Marker</td><td>" + rset_curr.getString("MNAME") + "</td></tr>");
         out.println("</table><br><br>");

         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");

         out.println("<table nowrap align=center border=0 cellSpacing=0 width=650px>");
         out.println("<tr bgcolor=black><td align=center colspan=4><b><font color=\"#ffffff\">Current Data</font></b></td></tr>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td nowrap WIDTH=150>Name</td>");
         out.println("<td nowrap WIDTH=250>Comment</td>");
         out.println("<td nowrap WIDTH=100>User</td>");
         out.println("<td nowrap WIDTH=150>Last updated</td></tr>");

         out.println("<tr bgcolor=white>");
         // Name
         out.println("<td>");
         if (("" + curr_name).equals("" + prev_name))
            out.println(formatOutput(session, curr_name, 16));
         else
            out.println("<font color=red>" + formatOutput(session, curr_name, 16) + "</font>");
         out.println("</td>");
         // Comment
         out.println("<td>");
         if (("" + curr_comm).equals("" + prev_comm))
            out.println(formatOutput(session, curr_comm, 25));
         else
            out.println("<font color=red>" + formatOutput(session, curr_comm, 25) + "</font>");
         out.println("</td>");
         // User
         out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
         out.println("<td>" + formatOutput(session, curr_ts, 18) + "</td>"); // Last updated
         out.println("</tr>");
         out.println("<tr bgcolor=Black>");
         out.println("<td align=center colspan=4><b><font color=\"#ffffff\">History</font></b></td></tr>");

         curr_name = prev_name;
         curr_comm = prev_comm;
         curr_usr = prev_usr;
         curr_ts = prev_ts;

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
            // Name
            out.println("<td>");
            if (("" + curr_name).equals("" + prev_name))
               out.println(formatOutput(session, curr_name, 16));
            else
               out.println("<font color=red>" + formatOutput(session, curr_name, 16) + "</font>");
            out.println("</td>");
            // Comment
            out.println("<td>");
            if (("" + curr_comm).equals("" + prev_comm))
               out.println(formatOutput(session, curr_comm, 25));
            else
               out.println("<font color=red>" + formatOutput(session, curr_comm, 25) + "</font>");
            out.println("</td>");
            // User
            out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
            out.println("<td>" + formatOutput(session, curr_ts, 18) + "</td>"); // Last updated
            out.println("</tr>");

            curr_name = prev_name;
            curr_comm = prev_comm;
            curr_usr = prev_usr;
            curr_ts = prev_ts;
         }
         if (has_history) {
            if (odd) {
               out.println("<tr bgcolor=white>");
               odd = false;
            } else {
               out.println("<tr bgcolor=lightgrey>");
               odd = true;
            }
            // Name
            out.println("<td>");
            if (("" + curr_name).equals("" + prev_name))
               out.println(formatOutput(session, curr_name, 16));
            else
               out.println("<font color=red>" + formatOutput(session, curr_name, 16) + "</font>");
            out.println("</td>");
            // Comment
            out.println("<td>");
            if (("" + curr_comm).equals("" + prev_comm))
               out.println(formatOutput(session, curr_comm, 25));
            else
               out.println("<font color=red>" + formatOutput(session, curr_comm, 25) + "</font>");
            out.println("</td>");
            // User
            out.println("<td>" + formatOutput(session, curr_usr, 10) + "</td>");
            out.println("<td>" + formatOutput(session, curr_ts, 18) + "</td>"); // Last updated
            out.println("</tr>");
         }

         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         // Back button
         out.println("<form>");
         out.println("<table cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("viewMark/alleles?item=no&") + oldQS + "\"'>&nbsp;");
         out.println("</td></tr></table>");

         out.println("</form>");

         out.println("</td></tr></table>");
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

   private void writeAlleles(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String newQS, mid, mname;
      int privileges[] = (int[]) session.getValue("PRIVILEGES");
      try {
         conn = (Connection) session.getValue("conn");
         mid = req.getParameter("mid");
         newQS = removeQSParameterOper(req.getQueryString());

        mname = req.getParameter("mname");

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Alleles</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">"+mname+" - Alleles</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");


         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr><td width=5></td><td>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         out.println("<Br>");
         // Allele table
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td width=100>Name</td>");
         out.println("<td width=150>Comment</td>");
         out.println("<td width=80>&nbsp;</td>"); // Details
         out.println("<td width=80>&nbsp;</td>"); // edit
         out.println("</tr>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, COMM, AID FROM V_ALLELES_1 WHERE " +
                                  "MID=" + mid + " ORDER BY NAME");
         boolean odd = true;
         while (rset.next() ) {
            if (odd)
               out.println("<tr bgcolor=white>");
            else
               out.println("<tr bgcolor=lightgrey>");
            odd = !odd;
            out.println("<td>" + formatOutput(session, rset.getString("NAME"), 12) + "</td>");
            out.println("<td>" + formatOutput(session, rset.getString("COMM"), 14) + "</td>");
            out.println("<td><a href=\"" + getServletPath("viewMark/detailsAllele?") +
                        "aid=" + rset.getString("AID") + "&item=no&" + newQS + "\">Details</a></td>");
            out.println("<td>");
            out.println(privDependentString(privileges, MRK_W,
                                            "<a href=\"" + getServletPath("viewMark/editAllele?") +
                                            "aid=" + rset.getString("AID") + "&item=no&" + newQS + "\">Edit</a>",
                                            "<font color=tan>Edit</font>"));
            out.println("</td>");
            out.println("</tr>");
         }

         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         out.println("<form>");
         out.println("<table cellspacing=0 cellpading=0 border=0><tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='document.location.href=\"" +
                  //   getServletPath("viewMark?") + newQS + "\"'>");
                   getServletPath("viewMark?&RETURNING=YES") + "\"'>");

         out.println("&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Create width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("viewMark/newAllele?") + "mname=" +mname+"&"+newQS + "\";''>");
         out.println("&nbsp;</td></tr></table>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=oper value=\"\">");
          out.println("<input type=hidden name=RETURNING value=YES>");
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

   private void writeNewAllele(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createAllele(req, res, conn)) {
            writeAlleles(req, res);
         } else {
            ; // We have already displayed an error message!
         }
      } else {
         writeNewAllelePage(req, res);
      }
   }
   private void writeNewAllelePage(HttpServletRequest req, HttpServletResponse res)
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

      String newQS, mid,mname;
      try {
         conn = (Connection) session.getValue("conn");
         mid = req.getParameter("mid");
         newQS = removeQSParameterOper(req.getQueryString());
          mname =req.getParameter("mname");
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeNewAlleleScript(out);
         out.println("<title>New allele</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">"+mname+" - Edit - Create Allele</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" +
                     getServletPath("viewMark/newAllele?") + newQS + "\">");
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
                     getServletPath("viewMark/edit?") + newQS + "\"'>");
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


   /***************************************************************************************
    * *************************************************************************************
    * The new marker page
    */
   private void writeNew(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createMarker(req, res, conn))
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
      String //sid = null,
         cid = null,
         suid = null,
         newQS,
         pid,
         oper,
         item;
      try {
         conn = (Connection) session.getValue("conn");
         pid = (String) session.getValue("PID");
         //			sid = req.getParameter("sid");
         cid = req.getParameter("cid");
         suid= req.getParameter("suid");
         newQS = removeQSParameterOper(buildQS(req));
         newQS = removeQSParameterSuid(newQS);
         newQS = removeQSParameterCid(newQS);
         oper = req.getParameter("oper");

         if (oper == null || oper.trim().equals("")) oper = "SEL_CHANGED";

         if (pid == null || "".equalsIgnoreCase(pid))
            pid = "-1";
         item = req.getParameter("item");
         if (item == null) {
            suid = findSuid(conn, pid);
            cid = findCid(conn, suid);
         } else if (item.equals("suid")) {
            cid = findCid(conn, suid);
         } else if (item.equals("no")) {
				// No changes in the selection
         }
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<base target=\"content\">");

         writeNewScript(out);

         out.println("<title>New marker</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Markers - New</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("viewMark/new?") + newQS + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         out.println("<table border=0 cellpading=0 celspaing=0>");
         out.println("<tr>");
         out.println("<td>Sampling unit<br>");
         out.println("<select name=suid WIDTH=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"suid\")'>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SUID FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid + " ORDER BY NAME");
         while (rset.next()) {
            out.println("<option " + (suid.equals(rset.getString("SUID")) ? "selected " : "") +
                        "value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME") +
                        "</option>");
         }
         rset.close();
         stmt.close();
         out.println("</select>");
         out.println("</td>");
         out.println("<td>Chromosome<br>");
         out.println("<select name=cid WIDTH=200 style=\"WIDTH: 150px\">");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM V_CHROMOSOMES_1 WHERE " +
                                  "SID=(SELECT SID FROM V_ENABLED_SAMPLING_UNITS_1 WHERE SUID=" + suid + ") " +
                                  "ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME");
         while (rset.next()) {
            out.println("<option " + (cid.equals(rset.getString("CID")) ? "selected " : "") +
                        "value=\"" + rset.getString("CID") + "\">" + rset.getString("NAME") +
                        "</option>");
         }
         rset.close();
         stmt.close();
         out.println("</select>");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<td>Primer 1<br>");
         out.println("<input type=text name=p1 width=200 style=\"WIDTH: 200px\">");
         out.println("</td>");
         out.println("<td>Primer 2<br>");
         out.println("<input type=text name=p2 width=200 style=\"WIDTH: 200px\">");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<td>Name<br>");
         out.println("<input type=text name=n width=200 style=\"WIDTH: 200px\">");
         out.println("</td>");
         out.println("<td>Alias<br>");
         out.println("<input type=text name=a width=200 style=\"WIDTH: 200px\">");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<td>Position<br>");
         out.println("<input type=text name=position width=200 style=\"WIDTH: 200px\">");
         out.println("</td>");
         out.println("<td></td></tr>");
         out.println("<tr>");
         out.println("<td colspan=2>Comment<br>");
         out.println("<textarea rows=8 cols=45 name=c>");
         out.println("</textarea>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("</td></tr><tr><td></td><td></td></tr>");
         out.println("<tr><td></td><td>");
         out.println("<table cellspacing=0 cellpading=0 border=0><tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='document.location.href=\"" +
                     //getServletPath("viewMark?") + newQS + "\"'>");
                     getServletPath("viewMark?&RETURNING=YES") + "\"'>");


         out.println("&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Create width=100 " +
                     "style=\"WIDTH: 100px\" onClick='valForm()'>");
         out.println("&nbsp;</td></tr></table>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=item value=\"no\">");
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
      out.println("	}");
      //	out.println("	if ( (\"\" + document.forms[0].a.value) != \"\") {");
      //	out.println("		if (document.forms[0].a.value.length > 20) {");
      //	out.println("			alert('Alias must be less than 20 characters!');");
      //	out.println("			rc = 0;");
      //	out.println("		}");
      //	out.println("	}");
      out.println("	");
      out.println("	");
      out.println("	if (rc) {");
      out.println("		if (confirm('Are you sure that you want to create the marker?')) {");
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
   private void writeImpLibScript(PrintWriter out) {
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
      out.println("	");
      out.println("	");
      out.println("	if (rc) {");
      out.println("		if (confirm('Are you sure that you want to copy the library marker?')) {");
      out.println("			document.forms[0].oper.value = 'COPY'");
      out.println("			document.forms[0].submit();");
      out.println("		}");
      out.println("	}");
      out.println("	");
      out.println("	");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
   }

   private void writeEditAllele(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";
      if (oper.equals("DELETE")) {
         if (deleteAllele(req, res, conn))
            writeAlleles(req, res);
      } else if (oper.equals("UPDATE")) {
         if(updateAllele(req, res, conn))
            writeEditAllelePage(req, res);
      } else
         writeEditAllelePage(req, res);
   }
   private void writeEditAllelePage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
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
         String aid = req.getParameter("aid");

         out.println("<html>");
         out.println("<head>");
         writeAlleleEditScript(out);
         out.println("<title>Edit Allele</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Markers - Edit - Alleles</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("viewMark/editAllele?item=no&") +
                     oldQS + "\">");

         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, COMM FROM " +
                                  "V_ALLELES_1 WHERE AID=" + aid);
         rset.next();
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");
         out.println("<table border=0 cellpading= cellspacing=0></tr>");
         out.println("<td width=300 style=\"WIDTH: 300px\">");
         out.println("Name<br>");
         out.println("<input type=text name=n maxlength=20 width=250 " +
                     "style=\"WIDTH: 250px\" " +
                     "value=\"" + replaceNull(rset.getString("NAME"), "") + "\"></td></tr>");
         out.println("<tr><td>Comment<br>");
         out.println("<textarea name=c cols=40 rows=10>" +
                     replaceNull(rset.getString("COMM"), "") +
                     "</textarea>");
         out.println("</td></tr>");
         out.println("<tr><td>&nbsp;</td></tr>");
         out.println("</table>");

         out.println("</td></tr>");
         out.println("<tr><td></td><td>");
         out.println("<table cellspacing=0 cellpading=0>");

         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("viewMark/alleles?item=no&") + oldQS + "\"'>&nbsp;");
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
         out.println("<input type=\"hidden\" NAME=aid value=\"" + aid + "\">");
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
         out.println("<script type=\"text/javascript\">");
         out.println("<!--");
         out.println("function valForm() {");
         out.println("	");
         out.println("	var rc = 1;");
         out.println("	");
         out.println("	if (rc) {");
         out.println("		if (confirm('Are you sure that you want to create the markers?')) {");
         out.println("			document.forms[0].oper.value = 'UPLOAD';");
         out.println("			document.forms[0].submit();");
         out.println("		}");
         out.println("	}");
         out.println("	");
         out.println("	");
         out.println("}");
         out.println("//-->");
         out.println("</script>");
         out.println("<title>Import markers</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=0>" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Genotypes - Markers - File import</b></center>" +
                     "</td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post enctype=\"multipart/form-data\" action=\"" +
                     getServletPath("viewMark/impMultipart") + "\">");
         out.println("<table border=0>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td>");
         sqlStatement = connection.createStatement();
         resultSet = sqlStatement.executeQuery("SELECT SUID, NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 WHERE " +
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
         out.println("<input type=file name=filename " +
                     "style=\"WIDTH: 350px\">");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td></td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table border=0><tr>");
         out.println("<td>");
         out.println("<input type=button value=Send " +
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
            if (resultSet != null) resultSet.close();
            if (sqlStatement != null) sqlStatement.close();
         } catch (SQLException ignored) {
         }
      }
   }
   /***************************************************************************************
    * *************************************************************************************
    * The marker's edit page
    */
   private void writeEdit(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";
      if (oper.equals("DELETE")) {
         if (deleteMarker(req, res, conn))
            writeFrame(req, res);
      } else if (oper.equals("UPDATE")) {
         if(updateMarker(req, res, conn))
            writeEditPage(req, res);
      } else
         writeEditPage(req, res);
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
      String sid, sname, cid, cname, suid, suname, mid,
         name, alias, p1, p2, position, comm, usr, tc_ts;
      try {
         stmt = conn.createStatement();
         String oldQS = buildQS(req);
         String pid = (String) session.getValue("PID");
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         //      oldQS = removeMid(oldQS);
         mid = req.getParameter("mid");
         String sql = "SELECT SID, SNAME, CID, CNAME, SUID, SUNAME, " +
            "MID, NAME, ALIAS, P1, P2, POSITION, COMM, USR, " +
            "to_char(TS, '" + getDateFormat(session) + "') as TC_TS " +
            "FROM gdbadm.V_MARKERS_3 WHERE " +
            "MID=" + mid ;
         rset = stmt.executeQuery(sql);

         rset.next();
         sid = rset.getString("SID");
         sname = rset.getString("SNAME");
         cid = rset.getString("CID");
         cname = rset.getString("CNAME");
         suid = rset.getString("SUID");
         suname = rset.getString("SUNAME");
         name = rset.getString("NAME");
         alias = rset.getString("ALIAS");
         p1 = rset.getString("P1");
         p2 = rset.getString("P2");
         position = rset.getString("POSITION");
         comm = rset.getString("COMM");
         usr = rset.getString("USR");
         tc_ts = rset.getString("TC_TS");
         rset.close();
         stmt.close();

         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);

         out.println("<title>Edit marker</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Markers - Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("viewMark/edit?") +
                     oldQS + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr>");
         out.println("<td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td>");
         out.println("</tr>");
         out.println("<tr><td>Species</td><td>" + sname + "</td></tr>");
         out.println("<tr><td>Sampling unit</td><td>" + suname + "</td></tr>");
         out.println("<tr><td>Last updated by</td><td>" + usr + "</td></tr>");
         out.println("<tr><td>Last updated</td><td>" + tc_ts + "</td></tr>");
         out.println("</table>");
         out.println("</td></tr><tr><td></td><td>");

         // Changable data
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Changable data</font></td></tr>");
         out.println("<tr><td>Chromosome<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM V_CHROMOSOMES_1 WHERE SID=" +
                                  sid + " ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME");
         out.println("<select name=n_cid width=200 style=\"WIDTH: 200px\">");
         while (rset.next()) {
            out.println("<option " + (cid.equals(rset.getString("CID")) ? "selected " : "") +
                        "value=\"" + rset.getString("CID") + "\">" + rset.getString("NAME") +
                        "</option>");
         }
         out.println("</select>");
         out.println("</td>");
         rset.close();
         stmt.close();
         out.println("<td>Name<br>");
         out.println("<input type=text name=n_name width=200 style=\"WIDTH: 200px\" " +
                     "value=\"" + replaceNull(name, "") + "\" maxlength=20>");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<td>Alias<br>");
         out.println("<input type=text name=n_alias width=200 style=\"WIDTH: 200px\" " +
                     "value=\"" + replaceNull(alias, "") + "\" maxlength=20>");
         out.println("</td>");
         out.println("<td>Position<br>");
         out.println("<input name=n_position width=200 style=\"WIDTH: 200px\" " +
                     "value=\"" + replaceNull(position, "") + "\">");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<td>Primer 1<br>");
         out.println("<input type=text name=n_p1 width=200 style=\"WIDTH: 200px\" " +
                     "value=\"" + replaceNull(p1, "") + "\" maxlength=40>");
         out.println("</td>");
         out.println("<td>Primer 2<br>");
         out.println("<input type=text name=n_p2 width=200 style=\"WIDTH: 200px\" " +
                     "value=\"" + replaceNull(p2, "") + "\" maxlength=40>");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<td colspan=2>Comment<br>");
         out.println("<textarea name=n_comm cols=45 rows=7>" +
                     replaceNull(comm, "") + "</textarea>");
         out.println("</td></tr>");
         out.println("</table>");


         out.println("</td></tr>");

         out.println("<tr><td></td><td></td></tr>");
         out.println("<tr><td></td><td>");
         out.println("<table cellspacing=0 cellpading=0>");

         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
          //           getServletPath("viewMark?") + "suid=" + suid + "&cid=" + cid + "&item=no\"'>&nbsp;");
                     getServletPath("viewMark?&RETURNING=YES") + "\"'>&nbsp;");
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
         // Store some extra information needed by doPost()
         out.println("<input type=\"hidden\" NAME=oper value=\"\">");
          out.println("<input type=\"hidden\" NAME=RETURNING value=YES>");
         out.println("</form>");
         out.println("</td></tr></table>");
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
   private void writeImpLib(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("COPY")) {
         if (importLib(req, res, conn)) {
            writeImpLibPage(req, res);
         } else {
            ; // We have already displayed an error message!
         }
      } else {
         writeImpLibPage(req, res);
      }
   }

   private void writeImpLibPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String sid, cid, suid, pid, oper, item;
      try {
         conn = (Connection) session.getValue("conn");
         pid = (String) session.getValue("PID");
         suid = req.getParameter("suid");
         sid = req.getParameter("sid");
         cid = req.getParameter("cid");
         oper = req.getParameter("oper");
         if (oper == null) oper = "SEL_CHANGED";
         item = req.getParameter("item");
         if (item == null) item = "";
         if (oper.equals("SEL_CHANGED")) {
            if (item.equals("sid")) {
               cid = findCidFromSid(conn, sid);
            } else if (item.equals("cid")) {
               ;
            } else {
               sid = findSid(conn, pid);
               cid = findCidFromSid(conn, sid);
            }
         }
         if (suid == null) suid = findSuid(conn, pid);
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeImpLibScript(out);
         out.println("<title>Import Library Marker</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Markers - Import Library Marker</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<table border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=10>&nbsp;</td><td>");

         out.println("<form method=post action=\"" +
                     getServletPath("viewMark/impLib") + "\">");
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr>");
         // Species
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID, NAME FROM V_SPECIES_2 WHERE " +
                                  "PID=" + pid + " ORDER BY NAME");
         out.println("<td>Species<br>");
         out.println("<select name=sid width=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"sid\");'>");
         while (rset.next()) {
            out.println("<option " +
                        (sid.equals(rset.getString("SID")) ? "selected " : "") +
                        "value=\"" + rset.getString("SID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td>");
         rset.close();
         stmt.close();
         // To sampling unit
         out.println("<td>Import to Sampling unit<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid + " AND SID=" + sid + " ORDER BY NAME");
         out.println("<select name=suid width=200 style=\"WIDTH: 200px\">");
         while (rset.next()) {
            out.println("<option " +
                        (suid.equals(rset.getString("SUID")) ? "selected " : "") +
                        "value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println ("</select>");
         out.println("</td>");
         rset.close();
         stmt.close();
         out.println("</tr>");
         out.println("<tr>");
         // Chromosome
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM V_CHROMOSOMES_1 " +
                                  "WHERE SID=" + sid + " ORDER BY " +
                                  "TO_NUMBER_ELSE_NULL(NAME), NAME");
         out.println("<td>Chromosome<br>");
         out.println("<select name=cid width=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"cid\");'>");
         while (rset.next() ) {
            out.println("<option " +
                        (cid.equals(rset.getString("CID")) ? "selected " : "") +
                        "value=\"" + rset.getString("CID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td>");
         rset.close();
         stmt.close();

         out.println("<td>&nbsp;</td></tr>");
         out.println("<tr>");
         // Library Marker
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT LMID, NAME FROM V_L_MARKERS_1 WHERE " +
                                  "CID=" + cid + " ORDER BY NAME");
         out.println("<td>Library Marker<br>");
         out.println("<select name=lmid width=200 style=\"WIDTH: 200px\">");
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("LMID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td>");
         rset.close();
         stmt.close();

         out.println("<td></td></tr></table>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");
         // Copy button
         out.println("<table cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button value=Import width=100 " +
                     "style=\"WIDTH: 100px\" onClick='valForm(\"COPY\");'>&nbsp;");
         out.println("</td></tr></table>");
         // Some hidden values
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("<input type=hidden name=item value=\"\">");
         out.println("</form>");
         out.println("</td></tr></table>");

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


   /**
    * Imports library markers as markers to a sampling unit. 
    *
    * @param request The request object to use.
    * @param response The response objet to use.
    * @param connection The connection object to use.
    * @return True if library markers was imported.
    *         False if library markers was not imported.
    */
   private boolean importLib(HttpServletRequest request,
                             HttpServletResponse response,
                             Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int suid, lmid, id;
         connection.setAutoCommit(false);
         // problem to parse ints if = null !!!
         id = Integer.parseInt((String) session.getValue("UserID"));
         suid = Integer.parseInt(request.getParameter("suid"));
         lmid = Integer.parseInt(request.getParameter("lmid"));

         DbMarker dbm = new DbMarker();
         dbm.CopyLibMarker(connection, lmid, suid, id);
         errMessage = dbm.getErrorMessage();
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
                       "Markers.ImportLM.Import", errMessage,
                       "viewMark/impLib", isOk);
      return isOk;
   }


   /**
    * Imports markers from a file to a sampling unit.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if markers could be imported.
    *         False if markers could not be imported.
    * @exception IOException If error writing error page or frame.
    * @exception ServletException If error writing error page or frame. 
    */
   private boolean createMarkFile(HttpServletRequest request,
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
            new MultipartRequest(request, upPath, 5 * 1024 * 1024);

         // Get parameters
         int samplingUnitId = Integer.parseInt(multiRequest.getParameter("suid"));
         int userId = Integer.parseInt((String) session.getValue("UserID"));

         Enumeration fileEnum = multiRequest.getFileNames();
         if (fileEnum.hasMoreElements())
         {
            DbMarker dbMarker = new DbMarker();
            
            // Convert given filename to system file name
            String givenFileName = (String) fileEnum.nextElement();
            String systemFileName = multiRequest.getFilesystemName(givenFileName);

            FileParser fileParser = new FileParser(upPath + "/" +  systemFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.MARKER,
                                                                        FileTypeDefinition.LIST));

            dbMarker.CreateMarkers(fileParser, connection, samplingUnitId, userId);
            errMessage = dbMarker.getErrorMessage();
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

      // If commit/rollback was ok and databas operation was ok, write the frame
      if (commitOrRollback(connection, request, response,
                           "Markers.Import.Send", errMessage,
                           "viewMark/impFile", isOk) 
          && isOk)
      {
         writeFrame(request, response);
      }
      return isOk;
   }


   /**
    * Creates a new marker.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if marker created.
    *         False if marker not created.
    */
   private boolean createMarker(HttpServletRequest request,
                                HttpServletResponse response,
                                Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int suid, cid, id;
         String name = null, alias = null, comm = null;
         String p1=null, p2=null, position=null;

         connection.setAutoCommit(false);
         // problem to parse ints if = null !!!
         id = Integer.parseInt((String) session.getValue("UserID"));
         suid = Integer.parseInt(request.getParameter("suid"));
         cid = Integer.parseInt(request.getParameter("cid"));
         name = request.getParameter("n");
         alias = request.getParameter("a");
         comm = request.getParameter("c");
         p1= request.getParameter("p1");
         p2= request.getParameter("p2");
         position= request.getParameter("position");


         DbMarker dbm = new DbMarker();
         dbm.CreateMarker(connection, name, alias, comm, p1, p2, position, suid, cid, id);
         errMessage = dbm.getErrorMessage();
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

      commitOrRollback(connection, request, response, "Markers.New.Create",
                       errMessage, "viewMark/new", isOk);
      return isOk;
   }


   /**
    * Deletes a marker.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if marker was deleted.
    *         False if marker was not deleted.
    */
   private boolean deleteMarker(HttpServletRequest request,
                                HttpServletResponse response,
                                Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int mid;
         connection.setAutoCommit(false);
         String UserID = (String) session.getValue("UserID");
         mid = Integer.parseInt(request.getParameter("mid"));
         DbMarker dbm = new DbMarker();
         dbm.DeleteMarker(connection, mid);
         errMessage = dbm.getErrorMessage();
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
                       "Markers.Edit.Delete", errMessage, "viewMark",
                       isOk);
      return isOk;
   }

   
   /**
    * Updates a marker.
    *
    * @param request The request object to use.
    * @param response The request object to use.
    * @param connection The connection object to use.
    * @return True if marker updated.
    *         False if marker not updated.
    */
   private boolean updateMarker(HttpServletRequest request,
                                HttpServletResponse response,
                                Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         String UserID = (String) session.getValue("UserID");
         String name, alias, position, p1, p2, comm;
         int cid, mid;
         String oldQS = request.getQueryString();
         name = request.getParameter("n_name");
         alias = request.getParameter("n_alias");
         position = request.getParameter("n_position");
         p1 = request.getParameter("n_p1");
         p2 = request.getParameter("n_p2");
         comm = request.getParameter("n_comm");
         cid = Integer.parseInt(request.getParameter("n_cid"));
         mid = Integer.parseInt(request.getParameter("mid"));

         // Check if position is a valid float.
         try
         {
            float test = Float.parseFloat(position);
         }
         catch (NumberFormatException nfe)
         {
            position = null;
         }

         DbMarker dbm = new DbMarker();
         dbm.UpdateMarker(connection, mid, name, alias, comm,
                          p1,p2,position, cid, Integer.parseInt(UserID)); 
         errMessage = dbm.getErrorMessage();
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
                       "Markers.Edit.Update", errMessage, "viewMark",
                       isOk);
      return isOk;
   }


   /**
    * Creates an allele in a marker.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if allele created.
    *         False if allele not created.
    */
   private boolean createAllele(HttpServletRequest request,
                                HttpServletResponse response,
                                Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         String name, comm;
         int mid, id;
         connection.setAutoCommit(false);
         name = request.getParameter("n");
         comm = request.getParameter("c");
         mid = Integer.parseInt(request.getParameter("mid"));
         id = Integer.parseInt((String) session.getValue("UserID"));
         
         DbAllele dba = new DbAllele();
         
         //DbMarker dbm = new DbMarker();
         dba.CreateAllele(connection, name, comm, mid, id);
         
         errMessage = dba.getErrorMessage();
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
                       "Markers.Alleles.Create.Create", errMessage,
                       "viewMark/alleles", isOk);
      return isOk;
   }

   
   /**
    * Updates an allele in a marker.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if allele updated.
    *         False if allele not updated.
    */
   private boolean updateAllele(HttpServletRequest request,
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
         int aid, id;
         String oldQS = request.getQueryString();
         aid = Integer.parseInt(request.getParameter("aid"));
         id = Integer.parseInt((String) session.getValue("UserID"));
         name = request.getParameter("n");
         comm = request.getParameter("c");
         DbMarker dbm = new DbMarker();
         dbm.UpdateAllele(connection, id, aid, name, comm);
         errMessage = dbm.getErrorMessage();
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
                       "Markers.Alleles.Edit.Delete", 
                       errMessage, "viewMark/alleles", isOk); 
      return isOk;
   }

   
   /**
    * Deletes an allele from the marker.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if allele deleted.
    *         False if allele not deleted.
    */
   private boolean deleteAllele(HttpServletRequest request,
                                HttpServletResponse response,
                                Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int aid;
         connection.setAutoCommit(false);
         aid = Integer.parseInt(request.getParameter("aid"));
         DbMarker dbm = new DbMarker();
         dbm.DeleteAllele(connection, aid);
         errMessage = dbm.getErrorMessage();
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
         
      commitOrRollback(connection, request, response, "Markers.Alleles.Edit.Delete",
                       errMessage, "viewMark/alleles", isOk);
      return isOk;
   }


   private void writeAlleleEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to delete the allele?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the allele?')) {");
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
   private void writeNewAlleleScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm() {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ( (\"\" + document.forms[0].c.value) != \"\" &&");
      out.println("       document.forms[0].c.value.length > 255) {");
      out.println("			alert('Comment must be less than 255 characters!');");
      out.println("			rc = 0;");
      out.println("	}");
      out.println("	if ( (\"\" + document.forms[0].n.value) == \"\") {");
      out.println("			rc = 0;");
      out.println("	}");
      out.println("	");
      out.println("	");
      out.println("	if (rc) {");
      out.println("		if (confirm('Are you sure that you want to create the allele?')) {");
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
   private void writeEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you really sure you want to delete the marker?\\n' +");
      out.println("               'This operation also deletes every genotype that is \\n' + ");
      out.println("               'associated with this marker!')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the marker?')) {");
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
            // We neew the privilege MRK_R for all these
            title = "Genotypes - Markers - View & Edit";
            if ( privDependentString(privileges, MRK_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/edit") ) {
            // We neew the privilege MRK_W
            title = "Genotypes - Markers - Edit";
            if ( privDependentString(privileges, MRK_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/new") ) {
            // We neew the privilege MRK_W
            title = "Genotypes - Markers - New";
            if ( privDependentString(privileges, MRK_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/impFile") ) {
            // We need the privilege MRK_W
            title = "Genotypes - Markers - File Import";
            if ( privDependentString(privileges, MRK_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/impLib") ) {
            // We need the privilege LMRK_R
            title = "Genotypes - Markers - Library Import";
            if ( privDependentString(privileges, LMRK_R, "", null) == null)
               ok = false;
         } else if (extPath.equals("/alleles") ) {
            // We need the privilege MRK_R
            title = "Genotypes - Markers - Alleles";
            if ( privDependentString(privileges, MRK_R, "", null) == null)
               ok = false;
         } else if (extPath.equals("/newAllele") ) {
            // We need the privilege MRK_W
            title = "Genotypes - Markers - New Allele";
            if ( privDependentString(privileges, MRK_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/editAllele") ) {
            // We need the privilege MRK_W
            title = "Genotypes - Markers - Edit Allele";
            if ( privDependentString(privileges, MRK_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/detailsAllele") ) {
            // We need the privilege MRK_R
            title = "Genotypes - Markers - Details Allele";
            if ( privDependentString(privileges, MRK_R, "", null) == null)
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
      } catch (Exception e)	{
         e.printStackTrace(System.err);
      } finally {
         ;
      }
   }

}

