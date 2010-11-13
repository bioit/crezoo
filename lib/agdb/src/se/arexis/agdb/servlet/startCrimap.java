/*
  $Log$
  Revision 1.9  2005/03/24 15:12:44  heto
  Working with removing oracle dep.

  Revision 1.8  2005/03/22 12:50:17  heto
  Working with moving all export files to a new package: se.arexis.agdb.util.FileExport

  Revision 1.7  2004/03/30 14:21:15  wali
  Changed Analyses to export.

  Revision 1.6  2003/12/19 15:32:51  wali
  Extended so that CreateFgIndLink is called in DbFileGeneration.  Used when results are saved.

  Revision 1.5  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.4  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.3  2002/12/13 15:02:38  heto
  Moved function to ArexisServlet

  Revision 1.2  2002/11/13 09:05:46  heto
  Migration to tomcat. ServletContext

  Revision 1.1.1.1  2002/10/16 18:14:04  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.12  2002/02/01 15:47:18  roca
  removed variable set choiche for crimap.

  Revision 1.11  2002/01/29 18:03:07  roca
  Changes by roca (se funktionsbskrivnig for LF025)

  Revision 1.10  2001/06/26 12:08:17  roca
  Changed names on buttons (finish/cancel) in alnalyse pages
  Generations changed to Export format
  Corrected counters in Filter/File views
  Bugfix in GenChrimap

  Revision 1.9  2001/06/07 13:19:45  roca
  added function TO_POSITIVE_NUMBER_ELSE_NULL in api_misc
  changed buttons to Finish/cancel on som generation pages
  Fixed bug (when alelename=0) in Genlinkage
  Tried to implement Linkage without locus

  Revision 1.8  2001/05/31 12:09:04  roca
  changed backbutton to cancelbutton

  Revision 1.7  2001/05/21 08:17:06  roca
  Roca fixed nullreplacement for files, privs not displayed and counter in phenotypes

  Revision 1.6  2001/05/11 09:17:26  frob
  Changed some key-names in Errors.properties which affected these files.

  Revision 1.5  2001/05/09 09:15:35  frob
  Changed all calls to writeError() to the general writeErrorPage(). The original
  writeError is removed.

  Revision 1.4  2001/05/09 06:49:01  frob
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
import se.arexis.agdb.util.FileExport.GenCRIMAP;

public class startCrimap extends SecureArexisServlet
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
         // The first page is requested
         writeStep1(req, res);
      } else if (extPath.equals("/step1")) {
         writeStep1(req, res);
      } else if (extPath.equals("/step2Single")) {
         writeStep2Single(req, res);
      } else if (extPath.equals("/step3Single")) {
         writeStep3Single(req, res);
      } else if (extPath.equals("/startSingle")) {
         startSingle(req, res);
      } else if (extPath.equals("/step2")) {
         ; //writeStep2(req, res);
      } else if (extPath.equals("/step2Multi")) {
         writeStep2Multi(req, res);
      } else if (extPath.equals("/step3Multi")) {
         writeStep3Multi(req, res);
      }  else if (extPath.equals("/startMulti")) {
         startMulti(req, res);
      }

   }
   private void writeStep1(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {



      String name = req.getParameter("n");
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      try {
        stmt = conn.createStatement();

        rset = stmt.executeQuery("SELECT NAME FROM V_FILE_GENERATIONS_1 WHERE NAME='"+name+"'" );
        if(rset.next())
        {
            writeErrorPage(req, res, "Generations.Start.Crimap", "The name you have choosen already exists!",
                           "viewFile/start" + "?");
         return;
        }
      }
      catch (SQLException ignored)
      {
      }
      String mode = req.getParameter("m");
      if ("M".equals(mode)) {
         // Multi sampling unit mode
         writeStep1Multi(req, res);

      } else {
         // Single mode
         writeStep1Single(req, res);
      }
   }

   private void writeStep1Multi(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String sql;
      String params="";
      String pid, oper, item;
      String type, name, comm, sid;
      //    String suid = "";
      String new_suid;
      Vector suids = null; // Sampling units
      try {
         conn = (Connection) session.getValue("conn");
         pid = (String) session.getValue("PID");
         oper = req.getParameter("oper");
         if (oper == null || oper.trim().equals("")) oper = "";
         type = req.getParameter("t");
         name = req.getParameter("n");
         comm = req.getParameter("c");
         sid = req.getParameter("s");
         //System.err.println("step1:"+req.getQueryString());

         new_suid = req.getParameter("new_suid");
         if (sid == null || sid.trim().equals("")) sid = "-1";

         Enumeration e = req.getParameterNames();
         String pn;
         suids = new Vector();
         while (e.hasMoreElements() ) {
            pn = (String) e.nextElement();
            if (pn.startsWith("suid")) {
               suids.addElement(pn.substring("suid".length() ) );
            }
         }
         // Add new sampling unit?
         if (oper.equals("ADD_SU")) {
            if (new_suid != null && !new_suid.trim().equals("")) {
               suids.addElement(new_suid);
            }
         } else if (oper.equals("REMOVE")) {
            // Remove sampling unit?
            String rem_suid = req.getParameter("rem_suid");
            if (rem_suid != null && !rem_suid.trim().equals("")) {
               int index;
               for (index = 0; index < suids.size(); index++)
                  if (rem_suid.equals((String) suids.elementAt(index)))
                     break;
               suids.removeElementAt(index);
            }
         }

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeStartTabDelScript(out);
         out.println("<title>Start Crimap generation</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Export file - Start - Crimap 1(3)</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<form method=post action=\"" +
                     getServletPath("startCrimap/step1") + "\">");
         /*
           out.println("<form method=post action=\"" +
           getServletPath("startTabDel/step2Multi") + "\">");
         */
         out.println("<table width=500 border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         // General data from previously pages
         out.println("<tr>");
         out.println("<td width=200>Name</td>");
         out.println("<td width=300>" + name + "</td>");
         out.println("<tr>");
         out.println("<td width=200>Comment</td>");
         out.println("<td width=300>" + formatOutput(session, comm, 20) + "</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Type</td>");
         out.println("<td width=300>" + type + "</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Mode</td>");
         out.println("<td width=300>Multiple Sampling Units</td>");
         out.println("</tr>");

         out.println("<tr><td></td><td></td></tr>");
         // Sampling units

         out.println("<tr><td colspan=2>");
         out.println("<table width=250 border=0 cellpading=0 cellspacing=0>");
         out.println("<tr bgcolor=\"#008B8B\"><td width=150 nowrap>Sampling unit</td><td width=100>&nbsp;</td></tr>");
         String suname;
         for (int i = 0; i < suids.size(); i++) {
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT NAME FROM V_ENABLED_SAMPLING_UNITS_2 WHERE SUID=" +
                                     (String) suids.elementAt(i) );
            rset.next();
            suname = rset.getString("NAME");
            rset.close();
            stmt.close();
            out.println("<tr>");
            out.println("<td>" + suname + "</td>");
            out.println("<td>");
            out.println("<input type=button value=\"Remove\" width=100 style=\"WIDTH: 100px\"" +
                        " onClick='valForm(\"REMOVE\", \"" + (String) suids.elementAt(i) + "\");'>");
            out.println("</td>");
            out.println("</tr>");
            rset.close();
            stmt.close();
         }
         out.println("</table>");
         out.println("</td>");
         out.println("</tr>");
         // Add sampling unit
         stmt = conn.createStatement();
         sql = "SELECT SUID, NAME FROM V_ENABLED_SAMPLING_UNITS_2 WHERE " +
            "PID=" + pid + " AND SID=" + sid;
         if (suids.size() > 0) {
            sql = sql + " AND SUID NOT IN(";
            for (int i = 0; i < suids.size() - 1; i++) {
               sql = sql + (String) suids.elementAt(i) + ", ";
            }
            sql = sql + (String) suids.lastElement() + ")";
         }
         sql = sql + " ORDER BY NAME";
         rset = stmt.executeQuery(sql);
         out.println("<tr>");
         out.println("<td>Sampling unit<br>");
         out.println("<select name=new_suid width=100 style=\"WIDTH: 100px\">");
         while(rset.next()) {
            out.println("<option value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</td>");
         out.println("<td>");
         out.println("<input type=button value=\"Add\" width=100 style=\"WIDTH: 100px\"" +
                     " onClick='valForm(\"ADD_SU\", \"\");'>");
         out.println("</td>");
         out.println("</tr>");

         // Some buttons
         out.println("<tr><td></td><td></td></tr>");
         out.println("<tr>");
         out.println("<td colspan=3>");
         out.println("<table border=0 cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td><input type=button value=\"Cancel\" " +
                     "width=100 style=\"WIDTH: 100px\" " +
                     "onClick='JavaScript:location.href=\"" + getServletPath("viewFile/start") + "\"';>");
         out.println("&nbsp;</td>");
         //  out.println("<td><input type=button value=\"next\" " +
         //     "width=100 style=\"WIDTH: 100px\" " +
         //     "onClick='next();'>");


         // add a the "static" parameters
         params="&t="+type;
         params+="&n="+name;
         params+="&c="+comm;
         params+="&s="+sid;

         // add all suid's as parameter-names
         for (int i=0; i < suids.size(); i++)
         {
            params += "&suid" + (String) suids.elementAt(i)+"=";
         }
         //        System.err.println("params="+params);


         // must choose sampling -unit first...
         if(suids.size()> 0)
         {
            out.println("<td><input type=button value=\"next\" " +
                        "width=100 style=\"WIDTH: 100px\" " +
                        "onClick='JavaScript:location.href=\"" + getServletPath("startCrimap/step2Multi?") +params +"\"';>");
         }
         else
         {
            out.println("<td><input type=button disabled value=\"next\" " +
                        "width=100 style=\"WIDTH: 100px\" "
                        /*"onClick='JavaScript:location.href=\"" + getServletPath("startTabDel/step2Multi?") +params + "\"'*/+";>");


         }

         out.println("&nbsp;</td>");
         out.println("</tr>");
         out.println("</table>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=rem_suid value=\"\">");
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("<input type=hidden name=t value=\"" + type + "\">");
         out.println("<input type=hidden name=n value=\"" + name + "\">");
         out.println("<input type=hidden name=c value=\"" + comm + "\">");
         out.println("<input type=hidden name=s value=\"" + sid + "\">");
         out.println("<input type=hidden name=m value=\"M\">");
         for (int i=0; i < suids.size(); i++)
            out.println("<input type=hidden name=\"suid" + (String) suids.elementAt(i) + "\" value=\"\">");
         out.println("</td></tr></table>");
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

   private void writeStep2Multi(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      Statement stmt_grps = null;
      Statement stmt_filt = null;


      ResultSet rset = null;
      ResultSet rset_grps = null;
      ResultSet rset_filt = null;

      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String pid, oper, item;
      String name, comm, sid, type;
      String suid;
      String fid=null;
      String gsid=null;
      boolean filtersFound=false;
      boolean allGrpsFound =false;
      boolean oneGrpFound=false;
      Vector suids = null; // Sampling units

      try {
         conn = (Connection) session.getValue("conn");
         pid = (String) session.getValue("PID");
         oper = req.getParameter("oper");

         if (oper == null || oper.trim().equals("")) oper = "";


         name = req.getParameter("n");
         comm = req.getParameter("c");
         sid = req.getParameter("s");
         //nej!!fid = req.getParameter("fid");
         //suid = req.getParameter("suid");
         // nej!!gsid = req.getParameter("gsid");
         type = req.getParameter("t");
         //      System.err.println(req.getQueryString());
         //      System.err.println("st2:params:"+type+name+comm+sid);


         if (sid == null) sid = "-1";
         //if (fid == null) fid = "-1";
         //if (suid == null) suid = "-1";
         Enumeration e = req.getParameterNames();
         String pn;
         suids = new Vector();
         while (e.hasMoreElements() ) {
            pn = (String) e.nextElement();
            if (pn.startsWith("suid")) {
               // System.err.println("suid:"+pn.substring("suid".length()));
               suids.addElement(pn.substring("suid".length() ) );
            }
         }

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         //			writeStartTabDelScript(out);
         out.println("<title>multi delimited generation 2</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Analyses - File Generation (multi) - Start - Crimap 2(3)</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<form method=post action=\"" +
                     //  getServletPath("startTabDel/step3Single") + "\">");
                     getServletPath("startCrimap/step3Multi") + "\">");

         out.println("<table width=700 border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");

         // general data from previous page

         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr>");
         out.println("<td width=200>Name</td>");
         out.println("<td width=300>" + name + "</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Comment</td>");
         out.println("<td width=300>" + formatOutput(session, comm, 20) + "</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Type</td>");
         out.println("<td width=300>Crimap</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Mode</td>");
         out.println("<td width=300>Multiple Sampling Units</td>");
         out.println("</tr>");
         out.println("<tr><td></td><td></td></tr>");
         out.println("</table>");


         out.println("</td></tr>");
         out.println("<tr><td>&nbsp;</td><td>");


         out.println("<table border=0 cellpading=0 cellspacing=0>");

         out.println("<tr><td>Unified Marker set<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT UMSID, NAME FROM V_U_MARKER_SETS_1 WHERE SID=" +
                                  sid + " AND PID="+pid+" ORDER BY NAME");
         out.println("<select name=umsid width=200 style=\"WIDTH: 200px\">");
         out.println("<option selected value=\"-1\">(None)</option>");
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("UMSID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         rset.close();
         stmt.close();
         out.println("</td>");
         // Variable Sets
         /*  out.println("<td>Unified Variable set<br>");
             stmt = conn.createStatement();
             rset = stmt.executeQuery("SELECT UVSID, NAME FROM V_U_VARIABLE_SETS_1 WHERE SID=" +
             sid + " AND PID="+pid+" ORDER BY NAME");
             out.println("<select name=uvsid width=200 style=\"WIDTH: 200px\">");
             out.println("<option selected value=\"-1\">(None)</option>");
             while (rset.next()) {
             out.println("<option value=\"" + rset.getString("UVSID") + "\">" +
             rset.getString("NAME") + "</option>");
             }
             out.println("</select>");
             rset.close();
             stmt.close();
             out.println("</td>");*/
         //out.println("</tr>");
         out.println("</tr></table>");


         // sampling-units from previous page - choose gsid and fid
         out.println("<table border=0 cellpading=0 cellspaing=0>");
         out.println("<tr><td></td></tr>");
         out.println("<tr><td></td></tr>");

         out.println("<tr><td width=200 bgcolor=\"#008B8B\">Sampling units</td>");
         out.println("<td width=200 bgcolor=\"#008B8B\">Grouping</td>");
         out.println("<td width=200 bgcolor=\"#008B8B\">Filter</td></tr>");


         // all choosen su's
         //groupingFound= true;
         allGrpsFound= true;
         for (int i=0; i < suids.size(); i++)
         {
            // take a suid and find name
            String curr_suid=(String) suids.elementAt(i);
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT NAME FROM V_SAMPLING_UNITS_1 WHERE SUID=" + curr_suid);

            if (rset.next())
            {
               oneGrpFound = false;
               out.println("<tr><td width=200>" + rset.getString("NAME") + "</td>");

               // get all available groupings,
               // the select box is named gsid_"suid" to identify the correlation between
               // sampling-unit and gsid.

               stmt_grps = conn.createStatement();
               rset_grps=stmt_grps.executeQuery("SELECT NAME,GSID FROM V_GROUPINGS_1 WHERE SUID=" + curr_suid);
               out.println("<td><select name=\"gsid"+curr_suid+"\" style=\"HEIGHT: 24px; WIDTH: 200px\" size=1>");

               while (rset_grps.next()) {
                  oneGrpFound=true;
                  if (gsid != null && gsid.equals(rset.getString("GSID"))){
                     out.println("<option selected value=\"" + rset_grps.getString("GSID") + "\">" + rset_grps.getString("NAME"));
                  }
                  else{
                     if(gsid == null || "".equalsIgnoreCase(gsid) || gsid.equalsIgnoreCase("-1")){
                        out.println("<option selected value=\"" + rset_grps.getString("GSID") + "\">" + rset_grps.getString("NAME"));
                        suid = rset_grps.getString("GSID");
                     }
                     else{
                        out.println("<option value=\"" + rset_grps.getString("GSID") + "\">" + rset_grps.getString("NAME"));
                     }
                  }
               }

               rset_grps.close();
               stmt_grps.close();
               out.println("</select></td>");
               // check if groups was found for this sampling unit
               if(!oneGrpFound)
               {
                  allGrpsFound=false;
               }


               // all available filters
               stmt_filt = conn.createStatement();
               rset_filt= stmt_filt.executeQuery("SELECT NAME,FID FROM V_FILTERS_1 WHERE SID="
                                                 + sid +" AND PID="+pid);

               out.println("<td><select name=\"fid"+curr_suid+"\" style=\"HEIGHT: 24px; WIDTH: 200px\" size=1>");
               filtersFound = false;
               while (rset_filt.next()) {
                  filtersFound=true;
                  if (fid != null && fid.equalsIgnoreCase(rset_filt.getString("FID")))
                     out.println("<OPTION selected value=\"" + rset_filt.getString("FID") + "\">" +
                                 rset_filt.getString("NAME"));
                  else
                     out.println("<OPTION value=\"" + rset_filt.getString("FID") + "\">" + rset_filt.getString("NAME"));
               }
               out.println("</select></td></tr>");
               rset_filt.close();
               stmt_filt.close();

            }
            //out.println("
            out.println("</tr>");
            rset.close();
            stmt.close();
         }



         //out.println("<td width=300>Error!!!</td>");
         // out.println("</tr>");
         out.println("<tr><td></td><td></td></tr>");
         if(!filtersFound)
         {
            out.println("<tr><td> Error! No filters found. Create filters first.</td></tr>");
         }
         else
         {
            if(!allGrpsFound)
            {
               out.println("<tr><td> Error! Groupings missing.</td></tr>");
            }
         }

         out.println("</table>");

         out.println("</td></tr>");
         out.println("<tr><td>&nbsp;</td><td>");
         out.println("<table border=0 cellpading=0 cellspaing=0>");
         // Marker Sets

         /*
           out.println("<tr><td>Marker set<br>");
           stmt = conn.createStatement();
           rset = stmt.executeQuery("SELECT MSID, NAME FROM V_MARKER_SETS_1 WHERE SUID=" +
           suid + " ORDER BY NAME");
           out.println("<select name=msid width=200 style=\"WIDTH: 200px\">");
           out.println("<option selected value=\"-1\">(None)</option>");
           while (rset.next()) {
           out.println("<option value=\"" + rset.getString("MSID") + "\">" +
           rset.getString("NAME") + "</option>");
           }
           out.println("</select>");
           rset.close();
           stmt.close();
           out.println("</td></tr>");
           // Variable Sets
           out.println("<tr><td>Variable set<br>");
           stmt = conn.createStatement();
           rset = stmt.executeQuery("SELECT VSID, NAME FROM V_VARIABLE_SETS_1 WHERE SUID=" +
           suid + " ORDER BY NAME");
           out.println("<select name=vsid width=200 style=\"WIDTH: 200px\">");
           out.println("<option selected value=\"-1\">(None)</option>");
           while (rset.next()) {
           out.println("<option value=\"" + rset.getString("VSID") + "\">" +
           rset.getString("NAME") + "</option>");
           }
           out.println("</select>");
           rset.close();
           stmt.close();
           out.println("</td>");
           out.println("</tr>");

         */

         // Some buttons
         out.println("<tr><td></td><td></td></tr>");
         out.println("<tr>");
         out.println("<td colspan=3>");
         out.println("<table border=0 cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td><input type=button value=\"Cancel\" " +
                     "width=100 style=\"WIDTH: 100px\" " +
                     "onClick='JavaScript:location.href=\"" + getServletPath("viewFile/start") + "\"';>");
         out.println("&nbsp;</td>");
         // we cannot allow user to proceed unless a filter is choosen
         if (filtersFound && allGrpsFound)
         {
            out.println("<td><input type=button value=\"Next\" " +
                        "width=100 style=\"WIDTH: 100px\" " +
                        "onClick='javascript:document.forms[0].submit();'>");
         }
         else
         {
            out.println("<td><input type=button disabled value=\"Next\" " +
                        "width=100 style=\"WIDTH: 100px\" " +
                        /*"onClick='javascript:document.forms[0].submit();'*/">");


         }
         out.println("&nbsp;</td>");
         out.println("</tr>");
         out.println("</table>");
         out.println("</td></tr>");
         out.println("</table>");


         // System.err.println("size="+suids.size());

         //	out.println("<input type=hidden name=oper value=\"\">");
         // add all suid's as parameter-names
         for (int i=0; i < suids.size(); i++)
         {
            //  System.err.println((String) suids.elementAt(i));
            out.println("<input type=hidden name=suid"+(String) suids.elementAt(i) +" value=\"\">");
         }

         //			out.println("<input type=hidden name=rem_suid value=\"\">");
         out.println("<input type=hidden name=oper value=\"\">");
         // 			out.println("<input type=hidden name=t value=\"" + type + "\">");
         out.println("<input type=hidden name=n value=\"" + name + "\">");
         out.println("<input type=hidden name=c value=\"" + comm + "\">");
         out.println("<input type=hidden name=s value=\"" + sid + "\">");
         //out.println("<input type=hidden name=suid value=\"" + suid + "\">");
         //out.println("<input type=hidden name=fid value=\"" + fid + "\">");
         out.println("<input type=hidden name=m value=\"S\">");
         out.println("</td></tr></table>");
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

   private void writeStep3Multi(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String pid, oper, item;
      String name, comm, sid;
      //String suid;
      //String fid;
      String umsid, uvsid;
      Vector suids = null; // Sampling units

      try {
         conn = (Connection) session.getValue("conn");
         pid = (String) session.getValue("PID");
         oper = req.getParameter("oper");

         // find all choosen suid's
         Enumeration e = req.getParameterNames();
         String pn;
         suids = new Vector();
         while (e.hasMoreElements() ) {
            pn = (String) e.nextElement();
            if (pn.startsWith("suid")) {
               // System.err.println("suid:"+pn.substring("suid".length()));
               suids.addElement(pn.substring("suid".length() ) );
            }
         }

         if (oper == null || oper.trim().equals("")) oper = "";

         name = req.getParameter("n");
         comm = req.getParameter("c");
         sid = req.getParameter("s");
         //System.err.println("p:"+req.getQueryString());
         //     fid = req.getParameter("fid");
         //     suid = req.getParameter("suid");
         umsid = req.getParameter("umsid");
         uvsid = req.getParameter("uvsid");
         if (sid == null) sid = "-1";
         // if (fid == null) fid = "-1";
         // if (suid == null) suid = "-1";
         if (umsid == null) umsid = "-1";
         if (uvsid == null) uvsid = "-1";

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeStartTabDelScript(out);
         out.println("<title>Start Crimap  generation</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Analyses - File Generation (multi) - Start - Crimap 3(3)</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<form method=post action=\"" +
                     getServletPath("startCrimap/startMulti") + "\">");

         out.println("<table width=500 border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         // General data from previous pages
         out.println("<tr>");
         out.println("<td width=200>Name</td>");
         out.println("<td width=300>" + name + "</td>");
         out.println("<tr>");
         out.println("<td width=200>Comment</td>");
         out.println("<td width=300>" + formatOutput(session, comm, 20) + "</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Type</td>");
         out.println("<td width=300>Crimap</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Mode</td>");
         out.println("<td width=300>Multiple Sampling Units</td>");
         out.println("</tr>");

         out.println("<td width=200>Unified Marker set</td>");
         if (umsid.equals("-1")) {
            out.println("<td>None</td>");
         } else {
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT NAME FROM V_U_MARKER_SETS_1 WHERE UMSID="
                                     + umsid );//+ " AND PID="+pid);
            if (rset.next())
               out.println("<td width=300>" + rset.getString("NAME") + "</td>");
            else
               out.println("<td width=300>Error!!!</td>");
            rset.close();
            stmt.close();

         }
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Unified Variable set</td>");
         if (uvsid.equals("-1")) {
            out.println("<td>None</td>");
         } else {
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT NAME FROM V_U_VARIABLE_SETS_1 WHERE UVSID="
                                     + uvsid );// + " AND PID="+pid);
            if (rset.next())
               out.println("<td width=300>" + rset.getString("NAME") + "</td>");
            else
               out.println("<td width=300>Error!!!</td>");
            rset.close();
            stmt.close();

         }
         out.println("</tr>");

         out.println("<tr><td></td><td></td></tr>");
         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td>&nbsp;</td><td>");

         //  out.println("<table border=0 width = 200  cellpading=0 cellspaing=0>");
         //  out.println("<tr><td width=200 bgcolor=\"#008B8B\" height= 2><U>  </U></td></tr>");
         //  out.println("</table>");
         out.println("<hr>");

         /*
           out.println("<table border=0 cellpading=0 cellspaing=0>");
           out.println("<tr>");
           out.println("<td colspan=2>");
           out.println("<U>Included fields</U>");
           out.println("</td></tr>");
           out.println("<tr><td><input type=checkbox name=sampling_unit checked></td><td>Sampling unit</td>");
           out.println("<td><input type=checkbox name=identity checked></td><td>Identity</td>");
           out.println("<td><input type=checkbox name=alias checked></td><td>Alias</td></tr>");
           out.println("<tr><td><input type=checkbox name=sex checked></td><td>Sex</td>");
           out.println("<td><input type=checkbox name=father checked></td><td>Father</td>");
           out.println("<td><input type=checkbox name=mother checked></td><td>Mother</td></tr>");
           out.println("<tr><td><input type=checkbox name=birth_date checked></td><td>Birth date</td>");
           //out.println("<td colspan=2>&nbsp;</td></tr>");
           out.println("<td><input type=checkbox name=raw checked></td><td>Raw data</td></tr>");
           out.println("</table>");
         */
         out.println("<tr><td>&nbsp;</td><td>");

         // out.println("<hr>");

         // choosen data
         out.println("<table border=0 cellpading=0 cellspaing=0>");
         out.println("<tr>");
         out.println("<td colspan=2>");
         out.println("Choosen data");
         out.println("</td></tr>");
         out.println("<tr><td width=200 bgcolor=\"#008B8B\">Sampling units</td>");
         out.println("<td width=200 bgcolor=\"#008B8B\">Grouping</td>");
         out.println("<td width=200 bgcolor=\"#008B8B\">Filter</td></tr>");

         // diplay all choosen sampling-units, with grouping and filter for each

         for (int i=0; i < suids.size(); i++)
         {
            // take a suid and find name
            String curr_suid=(String) suids.elementAt(i);
            String curr_gsid =null;
            String curr_fid=null;
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT NAME FROM V_SAMPLING_UNITS_1 WHERE SUID=" + curr_suid);
            if (rset.next())
            {
               out.println("<tr><td width=200>" + rset.getString("NAME") + "</td>");

               // find corresponding gsid in parameters
               Enumeration sent = req.getParameterNames();
               String tmpName;
               while (sent.hasMoreElements() )
               {
                  tmpName = (String) sent.nextElement();
                  if (tmpName.startsWith("gsid"))
                  {
                     // System.err.println("paramName:"+tmpName);
                     // is this the one corresponding with current suid?
                     if(tmpName.substring("gsid".length()).equals(curr_suid))
                     {
                        curr_gsid=req.getParameter(tmpName);
                        // System.err.println("GSID:"+curr_gsid);
                     }//if
                  }//if
               }//while

               //ok, got gsid, now use it..
               rset = stmt.executeQuery("SELECT NAME FROM V_GROUPINGS_1 WHERE GSID=" + curr_gsid);
               if(rset.next())
               {
                  out.println("<td width=200>" + rset.getString("NAME") + "</td>");
                  out.println("<input type=hidden name=gsid"+curr_suid+" value=\"" + curr_gsid + "\">");
               }

               // find corresponding fid in parameters
               sent = req.getParameterNames();
               tmpName= null;
               while (sent.hasMoreElements() )
               {
                  tmpName = (String) sent.nextElement();
                  if (tmpName.startsWith("fid"))
                  {
                     //              System.err.println("paramName:"+tmpName);
                     // is this the one corresponding with current suid?
                     if(tmpName.substring("fid".length()).equals(curr_suid))
                     {
                        curr_fid=req.getParameter(tmpName);
                        //               System.err.println("FID:"+curr_fid);

                     }
                  }
               }
               //ok, got fid, now use it..
               rset = stmt.executeQuery("SELECT NAME FROM V_FILTERS_1 WHERE FID=" + curr_fid);
               if(rset.next())
               {
                  out.println("<td width=200>" + rset.getString("NAME") + "</td></tr>");
                  out.println("<input type=hidden name=fid"+curr_suid+" value=\"" + curr_fid + "\">");
               }
            }

         }
         rset.close();
         stmt.close();

         out.println("</table>");

         
         out.println("<table border=0 cellpading=0 cellspacing=0>");
           out.println("<tr>");
         out.println("<br>");
         out.println("<td><input type=checkbox name=fileChoice ></td><td>All data in one file</td>");
         out.println("</tr>");
         out.println("</table>");

         // Some buttons
         out.println("<tr><td></td><td></td></tr>");
         out.println("<tr>");
         out.println("<td colspan=3>");
         out.println("<table border=0 cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td><input type=button value=\"Cancel\" " +
                     "width=100 style=\"WIDTH: 100px\" " +
                     "onClick='JavaScript:location.href=\"" + getServletPath("viewFile/start") + "\"';>");
         out.println("&nbsp;</td>");
         out.println("<td><input type=button value=\"Finish\" " +
                     "width=100 style=\"WIDTH: 100px\" " +
                     "onClick='javascript:document.forms[0].submit();'>");
         out.println("&nbsp;</td>");
         out.println("</tr>");
         out.println("</table>");
         out.println("</td></tr>");
         out.println("</table>");

         // send all suid's as hidden....
         for (int i=0; i < suids.size(); i++)
         {
            //  System.err.println((String) suids.elementAt(i));
            out.println("<input type=hidden name=suid"+(String) suids.elementAt(i) +" value=\"\">");
         }

         //			out.println("<input type=hidden name=rem_suid value=\"\">");
         out.println("<input type=hidden name=oper value=\"\">");
         // 			out.println("<input type=hidden name=t value=\"" + type + "\">");
         out.println("<input type=hidden name=n value=\"" + name + "\">");
         out.println("<input type=hidden name=c value=\"" + comm + "\">");
         out.println("<input type=hidden name=s value=\"" + sid + "\">");
         // out.println("<input type=hidden name=suid value=\"" + suid + "\">");
         // out.println("<input type=hidden name=fid value=\"" + fid + "\">");
         out.println("<input type=hidden name=umsid value=\"" + umsid + "\">");
         out.println("<input type=hidden name=uvsid value=\"" + uvsid + "\">");
         out.println("<input type=hidden name=m value=\"M\">");
         out.println("</td></tr></table>");
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


   private void writeStep1Single(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String sql;
      String pid, oper, item;
      String type, name, comm, sid;
      String suid=null,fid=null,gsid=null, old_suid=null;
      boolean filtersFound=false;
      //    String suid = "";
      //    String new_suid;
      //    Vector suids = null; // Sampling units
      //    Vector gsids = null; // Groupings
      try {
         conn = (Connection) session.getValue("conn");
         pid = (String) session.getValue("PID");
         oper = req.getParameter("oper");
         //      if ("START".equals(oper)) {
         //        // Start the generation
         //        return;
         //      }

         if (oper == null || oper.trim().equals("")) oper = "";
         type = req.getParameter("t");
         name = req.getParameter("n");
         comm = req.getParameter("c");
         sid = req.getParameter("s");
         suid = req.getParameter("suid");
         old_suid =req.getParameter("old_suid");
         fid=req.getParameter("fid");
         gsid=req.getParameter("gsid");
//         System.err.println("GSID="+gsid);
//         System.err.println(req.getQueryString());

         //      new_suid = req.getParameter("new_suid");
         if (sid == null || sid.trim().equals("")) sid = "-1";


         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeStartTabDelScript(out);
         out.println("<title>Start Crimap generation</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Export file - Start - Crimap 1(3)</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<form method=get action=\"" +
                     //  getServletPath("startCrimap/step2Single") + "\">");
                     getServletPath("startCrimap/step1") + "\">");

         out.println("<table width=500 border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         // General data from previously pages
         out.println("<tr>");
         out.println("<td width=200>Name</td>");
         out.println("<td width=300>" + name + "</td>");
         out.println("<tr>");
         out.println("<td width=200>Comment</td>");
         out.println("<td width=300>" + formatOutput(session, comm, 20) + "</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Type</td>");
         out.println("<td width=300>Crimap</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Mode</td>");
         out.println("<td width=300>Single sampling Unit</td>");
         out.println("</tr>");

         out.println("<tr><td></td><td></td></tr>");
         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td>&nbsp;</td><td>");
         out.println("<table border=0 cellpading=0 cellspaing=0>");
         // Filters
         out.println("<tr><td>Filter<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT FID, NAME FROM V_FILTERS_2 WHERE PID=" +
                                  pid + " AND SID=" + sid + " ORDER BY NAME");
         filtersFound = false;
//         out.println("<select name=fid onChange='document.forms[0].submit()' width=200 style=\"WIDTH: 200px\">");
         out.println("<select name=fid  onChange='document.forms[0].submit()' width=200 style=\"WIDTH: 200px\">");

         while (rset.next()) {
            filtersFound=true;

            if( fid==null || fid.equalsIgnoreCase("") || fid.equalsIgnoreCase("null"))
            {
               out.println("<OPTION selected value=\"" + rset.getString("FID") + "\">" +
                           rset.getString("NAME")+ "</option>\n");
               fid=rset.getString("FID");
            }
            else
            {
               if (fid != null && fid.equalsIgnoreCase(rset.getString("FID")))
               {

                  out.println("<OPTION selected value=\"" + rset.getString("FID") + "\">" +
                              rset.getString("NAME")+ "</option>\n");
               }
               else
               {
                  out.println("<OPTION value=\"" + rset.getString("FID") + "\">" + rset.getString("NAME")+"</option>\n");
               }
            }
         }

         out.println("</select>");
         rset.close();
         stmt.close();
         out.println("</td></tr>");
         // Sampling units
         out.println("<tr><td>Sampling unit<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM V_ENABLED_SAMPLING_UNITS_2 WHERE PID=" +
                                  pid + " AND SID=" + sid + " ORDER BY NAME");
         out.println("<select name=suid onChange='document.forms[0].submit()'width=200 style=\"WIDTH: 200px\">");
         //name=\"suid\"onChange='document.forms[0].submit()'>");
            // SUID has changed, reset..
          if(suid != null && !suid.equals(old_suid))
          {
            gsid=null;
          }

         while (rset.next())
         {
            // NO SU COOSEN YET
            if( suid==null || suid.equalsIgnoreCase("") || suid.equalsIgnoreCase("null"))
            {
               out.println("<OPTION selected value=\"" + rset.getString("SUID") + "\">" +
                           rset.getString("NAME")+ "</option>\n");
               suid=rset.getString("SUID");
               old_suid=suid;
               gsid=null;
            }
            else
            {
               if (suid != null && suid.equalsIgnoreCase(rset.getString("SUID")))
               {
                  out.println("<OPTION selected value=\"" + rset.getString("SUID") + "\">" +
                              rset.getString("NAME")+ "</option>\n");
                old_suid=rset.getString("SUID");

              // gsid=null;
               }
               else
               {
                  out.println("<OPTION value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME")+"</option>\n");
               }
            }
         }
         out.println("</select></td></tr>");
         // Groupings
         out.println("<tr><td>Grouping<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT GSID, NAME FROM V_GROUPINGS_2 WHERE PID=" +
                                  pid + " AND SUID=" + suid + " ORDER BY NAME");
     //   out.println("<select name=gsid onChange='document.forms[0].submit()' width=200 style=\"WIDTH: 200px\">");
      out.println("<select name=gsid onChange='document.forms[0].submit()' width=200 style=\"WIDTH: 200px\">");

         while (rset.next()) {
            // NO GSID COOSEN YET
            if( gsid==null || gsid.equalsIgnoreCase("") || gsid.equalsIgnoreCase("null"))
            {
               out.println("<OPTION selected value=\"" + rset.getString("GSID") + "\">" +
                           rset.getString("NAME")+ "</option>\n");
               gsid=rset.getString("GSID");
            }
            else // we have gsid choosen
            {

            // find the already choosen option
               if (gsid != null && gsid.equalsIgnoreCase(rset.getString("GSID")))
               {
                  out.println("<OPTION selected value=\"" + rset.getString("GSID") + "\">" +
                              rset.getString("NAME")+ "</option>\n");

               }
               else // write the rest (the non choosen options)
               {
                  out.println("<OPTION value=\"" + rset.getString("GSID") + "\">" + rset.getString("NAME")+"</option>\n");

               }
            }
         }

         out.println("</select>");
         rset.close();
         stmt.close();
         out.println("</td>");
         out.println("</tr>");

         if(!filtersFound)
         {
            out.println("<tr><td>Error! no filters found. Create filters first.</td></tr>");
         }


         // Some buttons
         out.println("<tr><td></td><td></td></tr>");
         out.println("<tr>");
         out.println("<td colspan=3>");
         out.println("<table border=0 cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td><input type=button value=\"Cancel\" " +
                     "width=100 style=\"WIDTH: 100px\" " +
                     "onClick='JavaScript:location.href=\"" + getServletPath("viewFile/start") + "\"';>");
         out.println("&nbsp;</td>");

         if(filtersFound)
         {
            out.println("<td><input type=button value=\"Next\" " +
                        "width=100 style=\"WIDTH: 100px\" " +
                        //"onClick='javascript:document.forms[0].submit();'>");
                        "onClick='JavaScript:location.href=\"" + getServletPath("startCrimap/step2Single?")
                        +"&suid="+suid +"&fid="+fid +"&gsid="+gsid+"&n="+name+"&c="+comm+ "\"';>");
         }
         else
         {
            out.println("<td><input type=button disabled value=\"Next\" " +
                        "width=100 style=\"WIDTH: 100px\" "
                        /*"onClick='javascript:document.forms[0].submit();'*/+">");
         }
         out.println("&nbsp;</td>");
         out.println("</tr>");
         out.println("</table>");
         out.println("</td></tr>");
         out.println("</table>");
         //			out.println("<input type=hidden name=rem_suid value=\"\">");
         out.println("<input type=hidden name=oper value=\"\">");
         // 			out.println("<input type=hidden name=t value=\"" + type + "\">");
         out.println("<input type=hidden name=n value=\"" + name + "\">");
         out.println("<input type=hidden name=c value=\"" + comm + "\">");
         out.println("<input type=hidden name=s value=\"" + sid + "\">");
         out.println("<input type=hidden name=old_suid value=\"" + old_suid + "\">");

         out.println("<input type=hidden name=m value=\"S\">");
         out.println("</td></tr></table>");
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

   private void writeStep2Single(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String pid, oper, item;
      String name, comm, sid;
      String suid;
      String fid;
      String gsid;

      try {
         conn = (Connection) session.getValue("conn");
         pid = (String) session.getValue("PID");
         oper = req.getParameter("oper");

         if (oper == null || oper.trim().equals("")) oper = "";
         name = req.getParameter("n");
         comm = req.getParameter("c");
         sid = req.getParameter("s");
         fid = req.getParameter("fid");
         suid = req.getParameter("suid");
         gsid = req.getParameter("gsid");
         // System.err.println("step1:gsid="+gsid);
         if (sid == null) sid = "-1";
         if (fid == null) fid = "-1";
         if (suid == null) suid = "-1";
         if (gsid == null) gsid = "-1";

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeStartTabDelScript(out);
         out.println("<title>Start Crimap generation</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Export file - Start - Crimap 2(3)</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<form method=post action=\"" +
                     getServletPath("startCrimap/step3Single") + "\">");

         out.println("<table width=500 border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         // General data from previously pages
         out.println("<tr>");
         out.println("<td width=200>Name</td>");
         out.println("<td width=300>" + name + "</td>");
         out.println("<tr>");
         out.println("<td width=200>Comment</td>");
         out.println("<td width=300>" + formatOutput(session, comm, 20) + "</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Type</td>");
         out.println("<td width=300>Crimap</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Mode</td>");
         out.println("<td width=300>Single sampling Unit</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Filter</td>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME FROM V_FILTERS_1 WHERE FID=" + fid);
         if (rset.next())
            out.println("<td width=300>" + rset.getString("NAME") + "</td>");
         else
            out.println("<td width=300>Error!!!</td>");
         rset.close();
         stmt.close();
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Sampling unit</td>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME FROM V_SAMPLING_UNITS_1 WHERE SUID=" + suid);
         if (rset.next())
            out.println("<td width=300>" + rset.getString("NAME") + "</td>");
         else
            out.println("<td width=300>Error!!!</td>");
         rset.close();
         stmt.close();
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Grouping</td>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME FROM V_GROUPINGS_1 WHERE GSID=" + gsid);
         if (rset.next())
            out.println("<td width=300>" + rset.getString("NAME") + "</td>");
         else
            out.println("<td width=300>Error!!!</td>");
         rset.close();
         stmt.close();
         out.println("</tr>");

         out.println("<tr><td></td><td></td></tr>");
         out.println("</table>");
         out.println("</td></tr>");
         out.println("<tr><td>&nbsp;</td><td>");
         out.println("<table border=0 cellpading=0 cellspaing=0>");
         // Marker Sets
         out.println("<tr><td>Marker set<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT MSID, NAME FROM V_MARKER_SETS_1 WHERE SUID=" +
                                  suid + " ORDER BY NAME");
         out.println("<select name=msid width=200 style=\"WIDTH: 200px\">");
         out.println("<option selected value=\"-1\">(None)</option>");
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("MSID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         rset.close();
         stmt.close();
         out.println("</td></tr>");
         // Variable Sets

         /*
         out.println("<tr><td>Variable set<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT VSID, NAME FROM V_VARIABLE_SETS_1 WHERE SUID=" +
                                  suid + " ORDER BY NAME");
         out.println("<select name=vsid width=200 style=\"WIDTH: 200px\">");
         out.println("<option selected value=\"-1\">(None)</option>");
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("VSID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         rset.close();
         stmt.close();
         out.println("</td>");
         out.println("</tr>");
*/
         // Some buttons
         out.println("<tr><td></td><td></td></tr>");
         out.println("<tr>");
         out.println("<td colspan=3>");
         out.println("<table border=0 cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td><input type=button value=\"Cancel\" " +
                     "width=100 style=\"WIDTH: 100px\" " +
                     "onClick='JavaScript:location.href=\"" + getServletPath("viewFile/start") + "\"';>");
         out.println("&nbsp;</td>");
         out.println("<td><input type=button value=\"Next\" " +
                     "width=100 style=\"WIDTH: 100px\" " +
                     "onClick='javascript:document.forms[0].submit();'>");
         out.println("&nbsp;</td>");
         out.println("</tr>");
         out.println("</table>");
         out.println("</td></tr>");
         out.println("</table>");
         //			out.println("<input type=hidden name=rem_suid value=\"\">");
         out.println("<input type=hidden name=oper value=\"\">");
         // 			out.println("<input type=hidden name=t value=\"" + type + "\">");
         out.println("<input type=hidden name=n value=\"" + name + "\">");
         out.println("<input type=hidden name=c value=\"" + comm + "\">");
         out.println("<input type=hidden name=s value=\"" + sid + "\">");
         out.println("<input type=hidden name=suid value=\"" + suid + "\">");
         out.println("<input type=hidden name=fid value=\"" + fid + "\">");
         out.println("<input type=hidden name=gsid value=\"" + gsid + "\">");
         out.println("<input type=hidden name=m value=\"S\">");
         out.println("</td></tr></table>");
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
   private void writeStep3Single(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String pid, oper, item;
      String name, comm, sid, gsid;
      String suid;
      String fid;
      String msid, vsid;

      try {
         conn = (Connection) session.getValue("conn");
         pid = (String) session.getValue("PID");
         oper = req.getParameter("oper");

         if (oper == null || oper.trim().equals("")) oper = "";
         name = req.getParameter("n");
         comm = req.getParameter("c");
         sid = req.getParameter("s");
         fid = req.getParameter("fid");
         suid = req.getParameter("suid");
         msid = req.getParameter("msid");
         vsid = req.getParameter("vsid");
         gsid = req.getParameter("gsid");

         if (sid == null) sid = "-1";
         if (fid == null) fid = "-1";
         if (suid == null) suid = "-1";
         if (msid == null) msid = "-1";
         if (vsid == null) vsid = "-1";
         if (gsid == null) gsid = "-1";

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         writeStartTabDelScript(out);
         out.println("<title>Start Crimap generation</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Export file - Start - Crimap 3(3)</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<form method=post action=\"" +
                     getServletPath("startCrimap/startSingle") + "\">");

         out.println("<table width=500 border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0>");
         // General data from previously pages
         out.println("<tr>");
         out.println("<td width=200>Name</td>");
         out.println("<td width=300>" + name + "</td>");
         out.println("<tr>");
         out.println("<td width=200>Comment</td>");
         out.println("<td width=300>" + formatOutput(session, comm, 20) + "</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Type</td>");
         out.println("<td width=300>Crimap</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Mode</td>");
         out.println("<td width=300>Single sampling Unit</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Filter</td>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME FROM V_FILTERS_1 WHERE FID=" + fid);
         if (rset.next())
            out.println("<td width=300>" + rset.getString("NAME") + "</td>");
         else
            out.println("<td width=300>Error!!!</td>");
         rset.close();
         stmt.close();
         out.println("</tr>");

         out.println("<tr>");
         out.println("<td width=200>Sampling unit</td>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME FROM V_SAMPLING_UNITS_1 WHERE SUID=" + suid);
         if (rset.next())
            out.println("<td width=300>" + rset.getString("NAME") + "</td>");
         else
            out.println("<td width=300>Error!!!</td>");
         rset.close();
         stmt.close();
         out.println("</tr>");

         out.println("<tr>");
         out.println("<td width=200>Grouping</td>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME FROM V_GROUPINGS_1 WHERE GSID=" + gsid);
         if (rset.next())
            out.println("<td width=300>" + rset.getString("NAME") + "</td>");
         else
            out.println("<td width=300>Error!!!</td>");
         rset.close();
         stmt.close();
         out.println("</tr>");


         out.println("<td width=200>Marker set</td>");
         if (msid.equals("-1")) {
            out.println("<td>None</td>");
         } else {
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT NAME FROM V_MARKER_SETS_1 WHERE MSID=" + msid);
            if (rset.next())
               out.println("<td width=300>" + rset.getString("NAME") + "</td>");
            else
               out.println("<td width=300>Error!!!</td>");
         }
         rset.close();
         stmt.close();
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Variable set</td>");
         if (vsid.equals("-1")) {
            out.println("<td>None</td>");
         } else {
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT NAME FROM V_VARIABLE_SETS_1 WHERE VSID=" + vsid);
            if (rset.next())
               out.println("<td width=300>" + rset.getString("NAME") + "</td>");
            else
               out.println("<td width=300>Error!!!</td>");
         }
         rset.close();
         stmt.close();
         out.println("</tr>");

//-----------



         out.println("<tr><td></td><td></td></tr>");
         out.println("</table>");

         out.println("<table border=0 cellpading=0 cellspacing=0>");
           out.println("<tr>");
         out.println("<br>");
         out.println("<td><input type=checkbox name=fileChoice ></td><td>All data in one file</td>");
         out.println("</tr>");
         out.println("</table>");

         out.println("</td></tr>");
         out.println("<tr><td>&nbsp;</td><td>");


         //  out.println("<table border=0 cellpading=0 cellspaing=0>");
         //  out.println("<tr>");
         //  out.println("<td colspan=2>");
         //  out.println("<U>Included fields</U>");
         //  out.println("</td></tr>");
         /* out.println("<tr><td><input type=checkbox name=sampling_unit checked></td><td>Sampling unit</td></tr>");
            out.println("<tr><td><input type=checkbox name=identity checked></td><td>Identity</td></tr>");
            out.println("<tr><td><input type=checkbox name=alias checked></td><td>Alias</td></tr>");
            out.println("<tr><td><input type=checkbox name=sex checked></td><td>Sex</td></tr>");
            out.println("<tr><td><input type=checkbox name=father checked></td><td>Father</td></tr>");
            out.println("<tr><td><input type=checkbox name=mother checked></td><td>Mother</td></tr>");
            out.println("<tr><td><input type=checkbox name=birth_date checked></td><td>Birth date</td></tr>");
            out.println("<tr><td colspan=2>&nbsp;</td></tr>");
            out.println("<tr><td><input type=checkbox name=raw checked></td><td>Raw data</td></tr>");
         */
         //     out.println("</table>");


         // Some buttons
         out.println("<tr><td></td><td></td></tr>");
         out.println("<tr>");
         out.println("<td colspan=3>");
         out.println("<table border=0 cellspacing=0 cellpading=0>");
         out.println("<tr>");
         out.println("<td><input type=button value=\"Cancel\" " +
                     "width=100 style=\"WIDTH: 100px\" " +
                     "onClick='JavaScript:location.href=\"" + getServletPath("viewFile/start") + "\"';>");
         out.println("&nbsp;</td>");
         out.println("<td><input type=button value=\"Finish\" " +
                     "width=100 style=\"WIDTH: 100px\" " +
                     "onClick='javascript:document.forms[0].submit();'>");
         out.println("&nbsp;</td>");
         out.println("</tr>");
         out.println("</table>");
         out.println("</td></tr>");
         out.println("</table>");
         //			out.println("<input type=hidden name=rem_suid value=\"\">");
         out.println("<input type=hidden name=oper value=\"\">");
         // 			out.println("<input type=hidden name=t value=\"" + type + "\">");
         out.println("<input type=hidden name=n value=\"" + name + "\">");
         out.println("<input type=hidden name=c value=\"" + comm + "\">");
         out.println("<input type=hidden name=s value=\"" + sid + "\">");
         out.println("<input type=hidden name=suid value=\"" + suid + "\">");
         out.println("<input type=hidden name=fid value=\"" + fid + "\">");
         out.println("<input type=hidden name=msid value=\"" + msid + "\">");
         out.println("<input type=hidden name=vsid value=\"" + vsid + "\">");
         out.println("<input type=hidden name=gsid value=\"" + gsid + "\">");
         out.println("<input type=hidden name=m value=\"S\">");
         out.println("</td></tr></table>");
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

   void startSingle(HttpServletRequest req, HttpServletResponse res)
      throws IOException 
   {
      HttpSession session = req.getSession(true);
      String absFileame;
      String name;
      String comm;
      String mode;
      String filter;
      String msid;
      String vsid;
      String fid;
      String expression;
      String pid;
      String suid;
      String gsid;
      String sid;
      String UserID;
      boolean multiFiles;
      int field_mask = 0;
      int fgid=0;
      boolean includeRaw ;
      boolean ok = true;
      GqlTranslator gqltr = null;
      GenCRIMAP gCri = null;
      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null;
      msid = req.getParameter("msid");
      if (msid != null && Integer.parseInt(msid) < 0) msid = null;
      vsid = req.getParameter("vsid");
      if (vsid != null && Integer.parseInt(vsid) < 0) vsid = null;
      name = req.getParameter("n");
      comm = req.getParameter("c");
      mode = req.getParameter("m");
      suid = req.getParameter("suid");
      gsid = req.getParameter("gsid");
      fid = req.getParameter("fid");
      pid = (String) session.getValue("PID");
      // if fid, filenname etc...
      if (req.getParameter("sampling_unit") != null)
         field_mask += 1;
      if (req.getParameter("identity") != null)
         field_mask += 2;
      if (req.getParameter("alias") != null)
         field_mask += 4;
      if (req.getParameter("sex") != null)
         field_mask += 8;
      if (req.getParameter("birth_date") != null)
         field_mask += 16;
      if (req.getParameter("father") != null)
         field_mask += 32;
      if (req.getParameter("mother") != null)
         field_mask += 64;
      if (req.getParameter("raw") != null)
         includeRaw = true;
      else
         includeRaw = false;

      if (req.getParameter("fileChoice") != null)
         multiFiles = false;
      else
         multiFiles = true;


      conn = (Connection) session.getValue("conn");
      UserID = (String) session.getValue("UserID");
      try 
      {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT EXPRESSION, PID, SID FROM V_FILTERS_1 WHERE FID=" + fid);
         if (rset.next()) 
         {
            expression = rset.getString("EXPRESSION");
            sid = rset.getString("SID");
         } 
         else 
         {    
            expression = ""; // ???
            sid = "-1";
         }
         gqltr = new GqlTranslator(pid, sid, expression, conn);
         gqltr.translate();
         filter = gqltr.getFilter();

         //ServletConfig conf = super.getServletConfig();
         ServletContext conf = super.getServletContext();
         String nullRepl = getFileNullReplacement(session);
         String absPath = conf.getInitParameter("fileGeneratePath");
         String dbdriver = conf.getInitParameter("driver");
         String dburl = conf.getInitParameter("dburl");
         String uid = conf.getInitParameter("uid");
         String pwd = conf.getInitParameter("pwd");
         
         if (dburl == null || uid == null || pwd == null || absPath == null) 
         {
            System.err.println("Unable to read initialization parameters needed by startCrimap");
            ok = false;
            writeErrorPage(req, res, "Generations.Start.Crimap",
                           Errors.keyValue("Generations.Start.Crimap." +
                                           "ReadInitParams.Error.Msg"), 
                           "viewFile/start");
         }
         else
         {
            // Create the neccesary databas objects!
             
           
            DbFileGeneration dbFG = new DbFileGeneration();
             
            //CreateSingleFileGeneration(conn, msid, vsid,  String s_gsid, String type, String name,  String comm, int pid, int suid, int fid, int id)	
            fgid = dbFG.CreateSingleFileGeneration(conn, msid, vsid, gsid, "CRIMAP", name,  comm, Integer.valueOf(pid), Integer.valueOf(suid), Integer.valueOf(fid), Integer.valueOf(UserID));
            
            //Insert values in the r_fgid_ind table
            dbFG.CreateFgIndLink(conn, fgid, suid, pid, gsid, fid);
            
            // Everything is set -> Let's start the generation !
            absPath = absPath + "/" + pid + "/" + fgid + "/";
            if (!createPath(absPath))
            {
               writeErrorPage(req, res, "Generations.Start.Crimap",
                              Errors.keyValue("Generations.Start.Crimap.CreateDir.Error.Msg"), 
                              "viewFile/start");
               ok = false;
               throw new Exception();
            }



            //  public GenCRIMAP(int fgid, String directory,
            //		     String dburl, String uid, String pwd)
            System.err.println(req.getQueryString());

            gCri = new GenCRIMAP(fgid, absPath, dbdriver, dburl, uid, pwd, multiFiles);// nullRepl, field_mask, includeRaw);
  				// No rush. We don't want to overload the server with time consuming tasks

            gCri.setPriority(Thread.NORM_PRIORITY - 2);
            gCri.start();
         }

      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
      } catch (Exception e) {
         ok=false;
         e.printStackTrace(System.err);
      }

      // Commit or rollback the changes
      try {
         if (ok) {
            conn.commit();
         } else {
            conn.rollback();
         }
      } catch (SQLException ignored) {

      } catch (Exception e) {
         e.printStackTrace(System.err);
      }
      finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
            //if (cstmt != null) cstmt.close();
         } catch (SQLException ignored) {}
      }
      /*
        if (ok)
        res.sendRedirect(getServletPath("viewFile") );
      */
      if (ok && fgid!=0)
         res.sendRedirect(getServletPath("viewFile/files?fgid="+fgid) );

   }

   void startMulti(HttpServletRequest req, HttpServletResponse res)
      throws IOException {
      HttpSession session = req.getSession(true);
      String absFileame;
      String name;
      String comm;
      String mode;
      String filter;
      String umsid;
      String uvsid;
      //	String fid;
      String expression;
      String pid;
      // String suid;
      // String gsid;
      Vector suids = null; // Sampling units
      String sid;
      String UserID;
      boolean multiFiles=false;
      int field_mask = 0;
      int fgid=0;
      boolean includeRaw ;
      boolean ok = true;
      GqlTranslator gqltr = null;
      GenCRIMAP gCri = null;
      Connection conn = null;
      CallableStatement cstmt = null;
      Statement stmt = null;
      ResultSet rset = null;

      // System.err.println("QS="+req.getQueryString());
      umsid = req.getParameter("umsid");
      //      System.err.println("UMSID="+umsid);


      if (umsid != null && Integer.parseInt(umsid) < 0) umsid = null;
      uvsid = req.getParameter("uvsid");
      // System.err.println("UVSID="+uvsid);
      if (uvsid != null && Integer.parseInt(uvsid) < 0) uvsid = null;
      name = req.getParameter("n");
      comm = req.getParameter("c");
      mode = req.getParameter("m");

      //suid = req.getParameter("suid");
      //gsid = req.getParameter("gsid");
      //fid = req.getParameter("fid");

      // System.err.println("Got:"+name+comm+mode);

      pid = (String) session.getValue("PID");
      // if fid, filenname etc...
      /*
        if (req.getParameter("sampling_unit") != null)
        field_mask += 1;
        if (req.getParameter("identity") != null)
        field_mask += 2;
        if (req.getParameter("alias") != null)
        field_mask += 4;
        if (req.getParameter("sex") != null)
        field_mask += 8;
        if (req.getParameter("birth_date") != null)
        field_mask += 16;
        if (req.getParameter("father") != null)
        field_mask += 32;
        if (req.getParameter("mother") != null)
        field_mask += 64;
        if (req.getParameter("raw") != null)
        includeRaw = true;
        else
        includeRaw = false;
      */


      if (req.getParameter("fileChoice") != null)
         multiFiles = false;
      else
         multiFiles = true;

      conn = (Connection) session.getValue("conn");
      UserID = (String) session.getValue("UserID");
      try {

         /* why this??
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT EXPRESSION, PID, SID FROM V_FILTERS_1 WHERE FID=" + fid);
            if (rset.next()) {
            expression = rset.getString("EXPRESSION");
            sid = rset.getString("SID");
            } else {
            expression = ""; // ???
            sid = "-1";
            }
            gqltr = new GqlTranslator(pid, sid, expression, conn);
            gqltr.translate();
            filter = gqltr.getFilter();
         */

         // System.err.println(req.getQueryString());

         // find all choosen suid's
         Enumeration e = req.getParameterNames();
         String pn;
         suids = new Vector();
         while (e.hasMoreElements() ) {
            pn = (String) e.nextElement();
            // System.err.println(pn);
            if (pn.startsWith("suid")) {
               // System.err.println("suid:"+pn.substring("suid".length()));
               suids.addElement(pn.substring("suid".length() ) );
            }
         }
         ServletContext conf = super.getServletContext();
         String nullRepl = getFileNullReplacement(session);
         String absPath = conf.getInitParameter("fileGeneratePath");
         String dbdriver = conf.getInitParameter("driver");
         String dburl = conf.getInitParameter("dburl");
         String uid = conf.getInitParameter("uid");
         String pwd = conf.getInitParameter("pwd");
         /*
           procedure Create_File_Generation(
           p_fgid    in out  FILE_GENERATIONS.FGID%TYPE,
           p_name	  in 	  FILE_GENERATIONS.NAME%TYPE,
           p_mode	  in	  FILE_GENERATIONS.MODE_%TYPE,
           p_type	  in	  FILE_GENERATIONS.TYPE_%TYPE,
           p_msid	  in	  FILE_GENERATIONS.MSID%TYPE,
           p_vsid	  in	  FILE_GENERATIONS.VSID%TYPE,
           p_comm	  in	  FILE_GENERATIONS.COMM%TYPE,
           p_pid	  in	  FILE_GENERATIONS.PID%TYPE,
           p_id	  in	  FILE_GENERATIONS.ID%TYPE,
           p_message in out  varchar2) IS
         */
         if (dburl == null || uid == null || pwd == null || absPath == null) {
            System.err.println("Unable to read initialization parameters needed by startCrimap");
            ok = false;
            writeErrorPage(req, res, "Generations.Start.Crimap",
                           Errors.keyValue("Generations.Start.Crimap.ReadInitParams.Error.Msg"),
                           "viewFile/start");
         } else {
            // Create the neccesary database objects!
            cstmt = conn.prepareCall("{call gdbp.Create_File_Generation(?,?,?,?,?,?,?,?,?,?)}");
            cstmt.registerOutParameter(1, java.sql.Types.NUMERIC); // FGID
            cstmt.registerOutParameter(10, java.sql.Types.VARCHAR); // Message
            cstmt.setString(2, name);
            cstmt.setString(3, mode);
            cstmt.setString(4, "Crimap");
            if (umsid == null)
               cstmt.setNull(5, java.sql.Types.NUMERIC);
            else
               cstmt.setInt(5, Integer.parseInt(umsid) );
            if (uvsid == null)
               cstmt.setNull(6, java.sql.Types.NUMERIC);
            else
               cstmt.setInt(6, Integer.parseInt(uvsid) );
            if (comm != null && !comm.trim().equals(""))
               cstmt.setString(7, comm);
            else
               cstmt.setNull(7, java.sql.Types.VARCHAR);
            cstmt.setInt(8, Integer.parseInt(pid) );
            cstmt.setInt(9, Integer.parseInt(UserID) );
            cstmt.execute();
            String message = cstmt.getString(10);
            if (message != null && !message.trim().equals("")) {
               ok = false;
               writeErrorPage(req, res, "Generations.Start.Crimap", message,
                              "viewFile/start");
               throw new Exception();
            }
            fgid = cstmt.getInt(1);

            //          System.err.println("fgid="+fgid);
            /*
              procedure Create_FG_FLT_Link(
              p_fgid    in      R_FG_FLT.FGID%TYPE,
              p_suid	  in 	  R_FG_FLT.SUID%TYPE,
              p_fid	  in	  R_FG_FLT.FID%TYPE,
              p_gsid	  in	  R_FG_FLT.GSID%TYPE,
              p_message in out  varchar2) IS
            */

            // for all sampling_units:


            //          System.err.println("size="+suids.size());
            
            // Used for insertion into r_fg_ind
            DbFileGeneration DbFG = new DbFileGeneration();
            for (int i=0; i < suids.size(); i++)
            {
               String curr_suid=(String) suids.elementAt(i);
               String curr_gsid =null;
               String curr_fid=null;


               // System.err.println("suid="+curr_suid);
               // find corresponding gsid in parameters
               Enumeration sent = req.getParameterNames();
               String tmpName;
               while (sent.hasMoreElements() )
               {
                  tmpName = (String) sent.nextElement();
                  if (tmpName.startsWith("gsid"))
                  {
                     // System.err.println("paramName:"+tmpName);
                     // is this the one corresponding with current suid?
                     if(tmpName.substring("gsid".length()).equals(curr_suid))
                     {
                        curr_gsid=req.getParameter(tmpName);
                        // System.err.println("SUD:"+curr_suid+"|"+"GSID:"+curr_gsid);
                     }//if
                  }//if
               }//while

               // find corresponding fid
               sent = req.getParameterNames();
               tmpName=null;
               while (sent.hasMoreElements() )
               {
                  tmpName = (String) sent.nextElement();
                  if (tmpName.startsWith("fid"))
                  {
                     //              System.err.println("paramName:"+tmpName);
                     // is this the one corresponding with current suid?
                     if(tmpName.substring("fid".length()).equals(curr_suid))
                     {
                        curr_fid=req.getParameter(tmpName);
                        // System.err.println("SUD:"+curr_suid+"|" +"FID:"+curr_fid);
                     }//if
                  }//if
               }//while

               cstmt.close();


               // System.err.println("gsid:"+curr_gsid);
               // System.err.println("FID:"+curr_fid);

               cstmt = conn.prepareCall("{call gdbp.Create_FG_FLT_Link(?,?,?,?,?)}");
               cstmt.registerOutParameter(5, java.sql.Types.VARCHAR);
               cstmt.setInt(1, fgid);
               cstmt.setInt(2, Integer.parseInt(curr_suid) );
               cstmt.setInt(3, Integer.parseInt(curr_fid) );
               if (curr_gsid == null)
                  cstmt.setNull(4, java.sql.Types.NUMERIC);
               else
                  cstmt.setInt(4, Integer.parseInt(curr_gsid));
               cstmt.execute();
               message = cstmt.getString(5);
               // System.err.println("mess="+message);
               if (message != null && !message.trim().equals("")) {
                  ok = false;
                  writeErrorPage(req, res, "Generations.Start.Crimap",
                                 message, "viewFile/start");
                  throw new Exception();
               }
               
               //Insert values in the r_fgid_ind table        
            DbFG.CreateFgIndLink(conn, fgid, curr_suid, pid, curr_gsid, curr_fid);
               
            }// end for all sampling units

            // ALL SAMPLING UNITS HANDLED

            // Everything is set -> Let's start the generation !
            absPath = absPath + "/" + pid + "/" + fgid + "/";
            if (!createPath(absPath)) {
               writeErrorPage(req, res, "Generations.Start.Crimap",
                              Errors.keyValue("Generations.Start.Crimap.CreateDir.Error.Msg"),
                              "viewFile/start" );
               ok = false;
               throw new Exception();
            }
            // System.err.println("start generation..");
            //  System.err.println(req.getQueryString());
            gCri = new GenCRIMAP(fgid, absPath, dbdriver, dburl, uid, pwd, multiFiles);
  				// No rush. We don't want to overload the server with time consuming tasks

            gCri.setPriority(Thread.NORM_PRIORITY - 2);
            gCri.start();
         }


      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
      } catch (Exception e) {
         ok=false;
         e.printStackTrace(System.err);
      }

      // Commit or rollback the changes
      try {
         if (ok) {
            conn.commit();
         } else {
            conn.rollback();
         }
      } catch (SQLException ignored) {

      } catch (Exception e) {
         e.printStackTrace(System.err);
      }
      finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
            if (cstmt != null) cstmt.close();
         } catch (SQLException ignored) {}
      }
      /*
        if (ok)
        res.sendRedirect(getServletPath("viewFile") );
      */
      if (ok && fgid!=0)
         res.sendRedirect(getServletPath("viewFile/files?fgid="+fgid) );
  	
   }

   /*

     } catch (SQLException sqle) {
     sqle.printStackTrace(System.err);

     } catch (Exception e) {
     ;
     } finally {
     try {
     if (rset != null) rset.close();
     if (stmt != null) stmt.close();
     if (cstmt != null) cstmt.close();
     } catch (SQLException ignored) {}
     }
     if (ok)
     res.sendRedirect(getServletPath("viewFile") );
     }
   */
   private void writeStartScript(PrintWriter out) {
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
      out.println("//		if (confirm('Are you sure that you want to create the filter?')) {");
      out.println("			document.forms[0].oper.value = ''");
      out.println("			document.forms[0].submit();");
      out.println("//		}");
      out.println("	}");
      out.println("	");
      out.println("	");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
   }
   private void writeStartTabDelScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action, suid) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('ADD_SU' == action.toUpperCase()) {");
      out.println("			document.forms[0].oper.value='ADD_SU';");
      out.println("			rc = 0;");
      out.println("	");
      out.println("	} else if ('REMOVE' == action.toUpperCase()) {");
      out.println("			document.forms[0].oper.value='REMOVE';");
      out.println("			document.forms[0].rem_suid.value=suid;");
      out.println("			rc = 0;");
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
      out.println("function start() {");
      out.println("	");
      out.println("	 document.forms[0].oper.value='START';");
      out.println("	 document.forms[0].submit();");
      out.println("	 return true;");
      out.println("	");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
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
             extPath.equals("/top") ) {
            // We neew the privilege FLT_R for all these
            title = "Analyses - Filters - View & Edit";
            if ( privDependentString(privileges, ANA_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/details") ) {
            // We neew the privilege FLT_R for all these
            title = "Analyses - Filters - Details";
            if ( privDependentString(privileges, FLT_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/edit") ) {
            // We neew the privilege FLT_W
            title = "Analyses - Filters - Edit";
            if ( privDependentString(privileges, FLT_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/new") ) {
            // We neew the privilege FLT_W
            title = "Analyses - Filters - New";
            if ( privDependentString(privileges, FLT_W, "", null) == null)
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
