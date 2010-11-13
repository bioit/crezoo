/*
  Copyright (c) Prevas AB. All Rights Reserved.

  $Log$
  Revision 1.17  2005/02/25 15:08:23  heto
  Converted Db*Variable.java to PostgreSQL

  Revision 1.16  2005/02/04 15:58:40  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.15  2005/01/31 16:16:40  heto
  Changing database to PostgreSQL. Problems with counts and selection buttons...

  Revision 1.14  2004/03/31 08:47:38  heto
  Added message. Moved initialization code

  Revision 1.13  2003/05/08 13:02:23  heto
  Fixes in source code.
  Added comments.

  Revision 1.12  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.11  2003/05/02 06:36:04  heto
  Added comments

  Revision 1.10  2003/04/28 15:16:10  heto
  Fixed output of "NULL" to ""
  Code layout changes.

  Revision 1.9  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.8  2003/04/25 09:05:25  heto
  Added alias in inheritance check.
  Fixed bug then uploading files with no extension. File upload failed with filename "testfile"

  Revision 1.7  2003/01/15 09:54:20  heto
  Comments added

  Revision 1.6  2002/12/20 09:11:52  heto
  No change

  Revision 1.5  2002/11/18 14:37:52  heto
  Fixed spelling error

  Revision 1.4  2002/11/13 09:05:08  heto
  Fixed source

  Revision 1.3  2002/10/22 06:08:09  heto
  rebuilt the "back-buttons".
  Dont save the request object, save the URL instead.
  New function.

  Revision 1.2  2002/10/18 11:41:09  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:05  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.22  2002/02/28 12:29:58  roca
  Bugfixes to inheritance check

  Revision 1.21  2002/02/11 17:20:18  roca
  optimized inheritcheck

  Revision 1.20  2002/01/31 15:54:05  roca
  Additional fixes of javascript for mac, Genotype import mm

  Revision 1.19  2002/01/30 19:33:01  roca
  Fixes of the inheritance check pages

  Revision 1.18  2002/01/29 18:03:11  roca
  Changes by roca (se funktionsbskrivnig for LF025)

  Revision 1.17  2001/09/06 13:01:00  roca
  Major changes to Genotype import handling.
  modified Linkage output format for Post makeped and allele numbering.
  Bug when deleting markersets fixed

  Revision 1.16  2001/06/26 12:08:22  roca
  Changed names on buttons (finish/cancel) in alnalyse pages
  Generations changed to Export format
  Corrected counters in Filter/File views
  Bugfix in GenChrimap

  Revision 1.15  2001/06/20 13:18:14  roca
  Temporarily removed postmakeped format in linkage
  Added special views in genoMenu
  Added parental,child and group view for genotypes

  Revision 1.14  2001/06/15 07:21:23  roca
  First attemp at LINKAGE post Makeped
  A family view avaliable in viewGeno

  Revision 1.13  2001/05/31 07:06:57  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.12  2001/05/22 15:27:57  roca
  Fixed Uppercase comparison when reading genotypes from file
  New Pages for admin/su and admin/species

  Revision 1.11  2001/05/22 06:16:51  roca
  Backfuncktionality fixed for all (?) servlets/subpages

  Revision 1.10  2001/05/21 13:12:27  frob
  Minor bugfix in createGenotype().

  Revision 1.9  2001/05/10 13:58:00  frob
  Changed all methods that used to call writeError() to write an error page. These
  methods now calls the general method commitOrRollback() which handles any errors.
  writeError() was removed.

  Revision 1.8  2001/05/03 14:20:59  frob
  Implemented local version of errorQueryString and changed writeError to use this method.

  Revision 1.7  2001/05/03 07:57:38  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.6  2001/05/02 13:52:27  frob
  Calls to removeOper and removePar changed to use the general removeQSParameter.
  The previously called methods are removed.

  Revision 1.5  2001/04/24 09:33:53  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

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

/**
 * Handles genotype information, the GUI
 */
public class viewGeno extends SecureArexisServlet
{
   /**
    * This class provides the functionality necessary to handle
    * genotypes.
    *
    * NOTE:
    * The output from an object of this class depends on the
    * privileges that deal with genotypes, i.e. GENO_R and GENO_W0 to
    * GENO_W9, where GENO_W# controls the client access rights to
    * genotypes of different level. If this feature is to work as
    * expected, this class takes for granted that the privileges
    * GENO_W0 to GENO_W9 are defined as integers in a serie where
    * GENO_W0 has the lowest number, GENO_W9 the highest and there are
    * no holes in the serie. This feature does not inflict any limitations
    * in any other part of the application, but in this servlet. However,
    * to make sure that the above critera is fullfilled, the privileges must
    * be created correctly in the sql-script responsible for the creation
    * of privileges and the mapping to the constants GENO_W# must match.
    *
    * @version 1.0, 2000-10-05
    */
   class parentData {
      public String father_identity;
      public String mother_identity;
      public String father_iid;
      public String mother_iid;

      public parentData() {
         this.father_identity=null;
         this.mother_identity=null;
         this.father_iid=null;
         this.mother_iid=null;
      }
   }

   class indData {
      public String father_identity;
      public String mother_identity;
      public String father_iid;
      public String mother_iid;
      public String identity;
      public String iid;
      public String sex;


      public indData() {
         this.father_identity=null;
         this.mother_identity=null;
         this.father_iid=null;
         this.mother_iid=null;
         this.iid=null;
         this.identity=null;
         this.sex=null;
      }
   }


   class genoData {
      public String mid;
      public String mname;
      public String iid;
      public String identity;
      public String alias;
      public String a1;
      public String a2;
      public String ref;
      public String raw1;
      public String raw2;
      public String com;
      public String f_iid;
      public String f_identity;
      public String a1_f;
      public String a2_f;
      public String ref_f;
      public String m_iid;
      public String m_identity;
      public String a1_m;
      public String a2_m;
      public String ref_m;

      public genoData() 
      {
          this.mid=null;
          this.mname=null;
          this.iid=null;
          this.identity=null;
          this.alias=null;
          this.a1=null;
          this.a2=null;
          this.ref=null;
          this.raw1=null;
          this.raw2=null;
          this.com=null;
          this.f_identity=null;
          this.f_iid=null;

          this.a1_f=null;
          this.a2_f=null;
          this.ref_f=null;
          this.m_iid=null;
          this.m_identity=null;
          this.a1_m=null;
          this.a2_m=null;
          this.ref_m=null;
      }
   }

      class parentComb{
      public String m1;
      public String m2;
      public String f1;
      public String f2;

      public parentComb() {
         this.f1=null;
         this.f2=null;
          this.m1=null;
         this.m2=null;
      }
   }
    class childComb{
      public String a1;
      public String a2;

      public childComb() {
         this.a1=null;
         this.a2=null;
      }
   }




    /**
     * The post method should execute the get method instead.
     *
     * @param req The Servlet request
     * @param res The servlet response
     * @throws ServletException -
     * @throws IOException   -
     */    
   public void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      doGet(req, res);
   }
   
   /** This method dispatches the request to the corresponding
    * method. The servlet handles the surrounding frameset,
    * the top frame, the bottom frame and methods for creation of
    * new groupings.
    * @param req The servlet request
    * @param res The servlet response object
    * @throws ServletException If error ServletExceptions can be thrown
    * @throws IOException May throw IOException
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
      } else if (extPath.equals("/bottom")) {
         writeBottom(req, res);
      } else if (extPath.equals("/middle")) {
         writeMiddle(req, res);
      } else if (extPath.equals("/details")) {
         writeDetails(req, res);
      } else if (extPath.equals("/edit")) {
         writeEdit(req, res);
      } else if (extPath.equals("/new")) {
         writeNew(req, res);
      } else if (extPath.equals("/impFile")) {
         writeImpFile(req, res);
      } else if (extPath.equals("/impMultipart")) {
         compareFile(req, res);
      } else if (extPath.equals("/commitMultipart")) {
         createFile(req, res);
      } else if (extPath.equals("/completion")) {
         writeCompletion(req, res);
      } else if (extPath.equals("/customView")) {
        chooseView(req,res);
      } else if (extPath.equals("/download")) {
        sendFile(req,res);
      } else if (extPath.equals("/compare")) {
        writeCompareResult(req,res);
      } else if (extPath.equals("/inheritCheck")) {
        inheritCheck(req,res);
      } else if (extPath.equals("/inheritResults")) {
        writeInheritResults(req,res);
      }

      /*else if (extPath.equals("/updateDev")) {
         if (updateDeviatingGenotypes(req, res))
            writeFrame(req, res);
      }
      */
   }

   /** 
    * Write the frame for the page.
    * @param req The servlet request object
    * @param res Thre servlet resonse object
    */
   private void writeFrame(HttpServletRequest req, HttpServletResponse res)
   {
     try 
     {
         HttpSession session = req.getSession(true);
         // set content type and other response header fields first

         // First we need to check if the user has requested a batch update of genotype levels.
         // In that case we should perform the updates and, in case of success, proceed
         // write the frame data. In case of a failure, the method batchUpdate has already
         // written an error message to the output stream.
         String action = req.getParameter("action");
         if (action != null && action.equals("BATCH_UPDATE")) 
         {
             if (!batchUpdateGenotype(req, res))
             return;
         }
         res.setContentType("text/html");
         res.setHeader("Pragma", "no-cache");
         res.setHeader("Cache-Control", "no-cache");
         PrintWriter out = res.getWriter();
         out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
      
         // Check if redirection is needed
         res = checkRedirectStatus(req,res); 
         //req= getServletState(req,session);

         String topQS = buildQS(req);
         // we don't want the oper parameter anaywhere but in the "edit" and "new" page!
         topQS = removeQSParameterOper(topQS);
         String bottomQS = topQS.toString();

         out.println("<html>"
                     + "<HEAD>"
                     + " <TITLE>View genotypes units</TITLE>"
                     + "</HEAD>"
                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" //
                     + "<frame name=\"viewgenotop\" "
                     + "src=\""+ getServletPath("viewGeno/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewgenomiddle\" "
                     + "src=\""+ getServletPath("viewGeno/middle?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewgenobottom\""
                     + "src=\"" +getServletPath("viewGeno/bottom?") + bottomQS + "\" "
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
   
   /** Build the Query String for the next request.
    * @param req The request object
    * @return Returns a String with the new QS additions.
    */   
   private String buildQS(HttpServletRequest req) 
   {
      StringBuffer output = new StringBuffer(512);
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      String action = null, // For instance COUNT, DISPLAY, NEXT etc
         suid = null,  // Sampling unit id
         old_suid = null, // Previous sampling unit id
         old_cid = null, // Previous chromosome id
         identity = null, // individual identity
         cid = null, // Chromosome id
         marker = null,
         allele1 = null,
         allele2 = null,
         orderby = null,
         mid = null,
         iid = null,
         reference = null,
         usr = null,
         level = null,
         u_level = null,
         t_date = null,
         f_date = null;
      boolean suid_changed = false;
      String pid = (String) session.getValue("PID");
      old_suid = (String) session.getValue("SUID");
      suid = req.getParameter("suid");
      if (suid == null) {
         suid = old_suid;
         suid_changed = true;
      } else if (old_suid != null && !old_suid.equals(suid)) {
         suid_changed = true;
      }
      if (suid == null) {
         suid = findSuid(conn, pid);
         suid_changed = true;
      }
      session.putValue("SUID", suid);

      old_cid = (String) session.getValue("CID");
      cid = req.getParameter("cid");
      if (suid_changed)
         cid = "*";//findCid(conn, suid);
      else {
         if (cid == null)
            cid = old_cid;
      }
      if (cid == null)
         //cid = findCid(conn, suid);
         cid="*";
      session.putValue("CID", cid);

      // Find the requested action
      action = req.getParameter("action");
      if (action == null || action.trim().equals("")) action = "NOP";

      output.append("ACTION=").append(action);
      identity = req.getParameter("identity");
      if (identity != null)
         output.append("&identity=").append(identity);
      marker = req.getParameter("marker");
      if (marker != null)
         output.append("&marker=").append(marker);
      allele1 = req.getParameter("allele1");
      if (allele1 != null)
         output.append("&allele1=").append(allele1);
      allele2 = req.getParameter("allele2");
      if (allele2 != null)
         output.append("&allele2=").append(allele2);
      mid = req.getParameter("mid");
      if (mid != null)
         output.append("&mid=").append(mid);
      iid = req.getParameter("iid");
      if (iid != null)
         output.append("&iid=").append(iid);
      reference=req.getParameter("reference");
      if(reference != null)
         output.append("&reference=").append(reference);

      usr = req.getParameter("usr");
      if (usr != null && !usr.trim().equals(""))
         output.append("&usr=").append(usr);
      level = req.getParameter("level");
      if (level != null && !level.trim().equals(""))
         output.append("&level=").append(level);
      u_level = req.getParameter("u_level");
      if (u_level != null && !u_level.trim().equals(""))
         output.append("&u_level=").append(u_level);
      f_date = req.getParameter("f_date");
      if (f_date != null && !f_date.trim().equals(""))
         output.append("&f_date=").append(f_date);
      t_date = req.getParameter("t_date");
      if (t_date != null && !t_date.trim().equals(""))
         output.append("&t_date=").append(t_date);

      // Set the parameters STARTINDEX and ROWS
      if (!action.equals("NOP"))
         output.append(setIndecis(suid, old_suid, cid, old_cid, action, req, session));
      output.append("&suid=").append(suid);
      output.append("&cid=").append(cid);
      if (req.getParameter("oper") != null)
         output.append("&oper=").append(req.getParameter("oper"));
      //		if (req.getParameter("new_geno_name") != null)
      //			output.append("&new_geno_name=").append(req.getParameter("new_geno_name"));

      orderby = req.getParameter("ORDERBY");
      if (orderby != null)
         output.append("&ORDERBY=").append(orderby);
      else
         output.append("&ORDERBY=IDENTITY");

      return output.toString().replace('%', '*');
   }
   
   /** Get the sampling unit id from the project ID
    *
    * @param conn The connection to use then executing to the database.
    * @param pid The project ID
    * @return Returns a String of the SUID
    */
   private String findSuid(Connection conn, String pid)
   {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try 
      {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 WHERE PID=" +
                                  pid + " ORDER BY NAME");
         if (rset.next()) 
         {
            ret = rset.getString("SUID");
         } 
         else 
         {
            ret = "-1";
         }
      } 
      catch (SQLException e) 
      {
         ret = "-1";
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
      return ret;
   }
   
   /** 
    * Get the Chromosome ID from the SUID
    * @param conn The database connection
    * @param suid The Sampling Unit Id for finding the CID
    * @return Returns a string of the Chromosome ID
    */   
   private String findCid(Connection conn, String suid) 
   {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try 
      {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID FROM gdbadm.V_CHROMOSOMES_1 WHERE SID=" +
                                  "(SELECT SID FROM gdbadm.V_ENABLED_SAMPLING_UNITS_1 WHERE SUID=" + suid + ")" +
                                  " ORDER BY NAME");
         if (rset.next()) 
         {
            ret = rset.getString("CID");
         } 
         else 
         {
            ret = "-1";
         }
      } 
      catch (SQLException e) 
      {    
         ret = "-1";
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
      return ret;
   }
   
   /** 
    * Get the name of an allele given the Allele ID (AID)
    * @param conn The connection to use for the database
    * @param aid The Allele ID as a String
    * @return Returns a string of the Allele name
    */   
   private String findAllele(Connection conn, String aid) 
   {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try 
      {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME FROM gdbadm.V_ALLELES_1 WHERE AID=" + aid);
         if (rset.next()) 
         {
            ret = rset.getString("NAME");
         } 
         else 
         {
            ret = "(None)";
         }
      } 
      catch (SQLException e) 
      {
         ret = "(Error)";
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
      return ret;
   }
   
   /** 
    * Find the Marker ID given the SUID and CID values
    * @param conn The connection to use
    * @param suid The Sampling Unit ID
    * @param cid The Chromosome ID
    * @return Returns a String of the Marker ID
    */   
   private String findMid(Connection conn, String suid, String cid) 
   {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try 
      {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_MARKERS_1 WHERE " +
                                  "SUID=" + suid + " AND CID=" + cid + " ORDER BY NAME");
         if (rset.next()) 
         {
            ret = rset.getString("MID");
         } 
         else 
         {
            ret = "-1";
         }
      } 
      catch (SQLException e) 
      {
         ret = "-1";
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
      return ret;
   }
   
   /** 
    * Get the Marker Set ID given the Sampling Unit ID
    * @param conn The connection to the db
    * @param suid The sampling unit to search for
    * @return Returns a String of the Marker Set ID
    */   
   private String findMsid(Connection conn, String suid) 
   {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try 
      {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT MSID FROM gdbadm.V_MARKER_SETS_1 WHERE " +
                                  "SUID=" + suid + " ORDER BY NAME");
         if (rset.next()) {
            ret = rset.getString("MSID");
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
   
   /** Handle the buttons for filtering (next, previous, ...)
    * Dont list all data in one single page, display data in intervalls.
    *
    * @param suid The Sampling Unit ID
    * @param old_suid The old Sampling Unit ID
    * @param cid The Chromosome ID
    * @param old_cid The old Chromosome ID
    * @param action Get what button that was pressed in the GUI
    * @param req The Servlet request object
    * @param session Get the session for the user
    * @return Returns parameters to the Query String
    */   
   private String setIndecis(   String suid, 
                                String old_suid, 
                                String cid, 
                                String old_cid, 
                                String action, 
                                HttpServletRequest req, 
                                HttpSession session) 
   {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(suid, cid, req, session);

      maxRows = getMaxRows(session);
      if (req.getParameter("STARTINDEX") != null &&
          old_suid.equalsIgnoreCase(suid) &&
          old_cid.equalsIgnoreCase(cid)) 
      {
         startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         if (rows > 0 && startIndex == 0) startIndex = 1;
      }
      else
         startIndex = 1;
      
      if ("COUNT".equalsIgnoreCase(action) ||
          "DISPLAY".equalsIgnoreCase(action)) 
      {
         if (startIndex >= rows)
            startIndex = 1;
      } 
      else if ("TOP".equalsIgnoreCase(action)) 
      {
         startIndex = 1;
      } 
      else if ("PREV".equalsIgnoreCase(action)) 
      {
         // decrement startindex with maxRows, if possible
         startIndex -= maxRows;
         if (startIndex < 1) startIndex = 1;
      } 
      else if ("NEXT".equalsIgnoreCase(action)) 
      {
         // Increment startindex with maxrows, if possible
         startIndex += maxRows;
         if (startIndex >= rows)
            startIndex -= maxRows;
      } 
      else if ("END".equalsIgnoreCase(action)) 
      {
         int mult = (int) rows / maxRows;
         if (rows % maxRows == 0) mult--;
         startIndex = (mult > 0 ? mult : 0) * maxRows + 1;
      } 
      else 
      {
         // action = NOP, i guess
      }
      output.append("&STARTINDEX=").append(startIndex)
         .append("&ROWS=").append(rows);
      return output.toString();
   }

   /** Get the number of rows that are affected given the SUID and CID
    * @param suid The Sampling Unit ID
    * @param cid The Chromosome ID
    * @param req The Servlet request object
    * @param session The session object for the user
    * @return Returns an int with the number of affected rows
    */   
   private int countRows(String suid, String cid, HttpServletRequest req, HttpSession session) 
   {
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;

      StringBuffer sbSQL = new StringBuffer(512);
      try 
      {
         sbSQL.append("SELECT count(*) "
                      + "FROM gdbadm.V_GENOTYPES_3 WHERE SUID=" + suid +" ");
        /*
         if(cid != null && !cid.trim().equals("") && !cid.equals("*"));
         {
          sbSQL.append(" AND CID=" + cid + " ");
         }
         */
         
         sbSQL.append(buildFilter(req,false));
         stmt = conn.createStatement();
         rset = stmt.executeQuery(sbSQL.toString());
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
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         }
         catch (SQLException ignored) 
         {}
      }
   }
   
   private String buildFilter(HttpServletRequest req)
   {
       return buildFilter(req,true);
   }
   
   /** Build a filter for filtering within the data result. This adds parameters to
    * the query string.
    * @param req The request object
    * $param order If true then order statement can be added else order should not be added to the filter.
    * @return Return the additions to the QS to filter the result
    */   
   private String buildFilter(HttpServletRequest req, boolean order) 
   {
      String identity = null,
         chromosome = null,
         marker = null,
         allele1 = null,
         allele2 = null,
         reference = null,
         usr = null,
         level = null,
         f_date = null,
         t_date = null;

      StringBuffer filter = new StringBuffer(256);
      identity = req.getParameter("identity");
      chromosome = req.getParameter("cid");
      marker = req.getParameter("marker");
      allele1 = req.getParameter("allele1");
      allele2 = req.getParameter("allele2");
      reference = req.getParameter("reference");
      usr = req.getParameter("usr");
      level = req.getParameter("level");
      f_date = req.getParameter("f_date");
      t_date = req.getParameter("t_date");

      if (identity != null && !"".equalsIgnoreCase(identity))
         filter.append("and IDENTITY like '" + identity + "'");
/*
      if (chromosome != null && !"".equalsIgnoreCase(chromosome)&& !"*".equals(chromosome))
         filter.append(" and CNAME like'" + chromosome + "'");
*/
    if (chromosome != null && !"".equalsIgnoreCase(chromosome)&& !"*".equals(chromosome))
         filter.append(" and CID like'" + chromosome + "'");
      if (marker != null && !"".equalsIgnoreCase(marker))
         filter.append(" and MNAME like'" + marker + "'");
      if (allele1 != null && !"".equalsIgnoreCase(allele1))
         filter.append(" and A1NAME like'" + allele1 + "'");
      if (allele2 != null && !"".equalsIgnoreCase(allele2))
         filter.append(" and A2NAME like'" + allele2 + "'");
      if(reference != null && !"".equalsIgnoreCase(reference))
         filter.append(" and REFERENCE like'" + reference + "'");
      if (usr != null && !usr.trim().equals(""))
         filter.append(" and upper(USR) like'" + usr.toUpperCase() + "'");
      if (level != null && !"*".equals(level))
         filter.append(" and LEVEL_=" + level);
      if (f_date != null && !f_date.trim().equals(""))
         filter.append(" and TS > to_date('" + f_date + "', 'YYYY-MM-DD')");
      if (t_date != null && !t_date.trim().equals(""))
         filter.append(" and TS < to_date('" + t_date + "', 'YYYY-MM-DD')");

      // temp-commented!
      // Replace every occurence of '*' with '%' and return the string
      // (Oracel uses '%' as wildcard while '%' demands some specail treatment
      // when passed in the query string)
      return filter.toString().replace('*', '%');

   }


   /** 
    * The top frame
    * @param req The request object
    * @param res The response object 
    */
   public void writeTop(HttpServletRequest req, HttpServletResponse res)
   {
      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null; 
      PrintWriter out = null;
      
      int startIndex = 0, rows = 0, maxRows = 0;
      String suid, cid, marker, identity, allele1, allele2, orderby, level,
         u_level, usr, f_date, t_date, oldQS, newQS, action, pid ,reference;
      
      try 
      {
         // set content type and other response header fields first

         res.setContentType("text/html");
         out = res.getWriter();
         String oper;
         oper = req.getParameter("oper");
         if (oper == null || "".equals(oper))
            oper = "SELECT";
         HttpSession session = req.getSession(true);
      
         conn = (Connection) session.getValue("conn");

         pid = (String) session.getValue("PID");
         suid = req.getParameter("suid");
         cid = req.getParameter("cid");

         maxRows = getMaxRows(session);
         action = req.getParameter("ACTION");
         oldQS = req.getQueryString();
         newQS = buildTopQS(oldQS);
         orderby = req.getParameter("ORDERBY");
         identity = req.getParameter("identity");
         marker = req.getParameter("marker");
         allele1 = req.getParameter("allele1");
         allele2 = req.getParameter("allele2");
         reference = req.getParameter("reference");

         level = req.getParameter("level");
         u_level = req.getParameter("u_level");
         usr = req.getParameter("usr");
         f_date = req.getParameter("f_date");
         t_date = req.getParameter("t_date");

         if (req.getParameter("STARTINDEX") != null)
            startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         else
            startIndex = 0;
         if (req.getParameter("ROWS") != null)
            rows = Integer.parseInt(req.getParameter("ROWS"));
         else
            rows = 0;
         if (suid == null) suid = "-1";
         if (cid == null) cid = "-1";
         if (level == null) level = "*";
         if (orderby == null) orderby = "NAME";
         if (action == null) action = "NOP";
         if (pid == null || "".equalsIgnoreCase(pid))
            pid = "-1";

         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+ getURL("style/view.css") +"\">");
         out.println("<base target=\"content\">");

         writeTopScript(out);
         out.println(getDateValidationScript());
         out.println("<title>View genotypes</title>");
         out.println("</head>");


         out.println("<body bgcolor=\"#ffffd0\">"
                     +"<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
                     +"<tr>"
                     + "<td width=\"14\" rowspan=\"3\"></td>"
                     +"<td width=\"736\" colspan=\"2\" height=\"15\">"
                     +"<form method=get action=\"" +getServletPath("viewGeno") +"\">"
                     +"<p align=\"center\"><font size=\"2\"><b  style=\"font-size: 15pt\">Genotypes</b>"
                     +"</font></td></tr>"
                     +"<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>"
                     +"</tr><tr><td width=\"517\">");

         out.println("<table width=488 height=\"92\">");
         // Suid
         out.println("<td><b>Sampling unit</b><br>");
         out.println("<select name=suid onChange='document.forms[0].submit()' " +
                     "width=100 style=\"WIDTH: 100px\">");

         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME, SUID FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID="+ pid + " ORDER BY NAME");

         while (rset.next()) {
            out.println("<option " +
                        (rset.getString("SUID").equals(suid) ? "selected " : "") +
                        "value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME")+ "</option>");
         }
         rset.close();
         stmt.close();
         out.println("</select></td>");
         // Identity
         out.println("<td><b>Identity</b><br>");
         out.println("<input name=identity value=\"" + replaceNull(identity, "") +
                     "\" width=100 style=\"WIDTH: 100px\"></td>");
         // Chromosome
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM gdbadm.V_CHROMOSOMES_1 " +
                                  "WHERE SID=("+
                                  "SELECT SID FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE SUID=" + suid + "AND PID="+ pid + ") order by " +
                                  "gdbadm.TO_NUMBER_ELSE_NULL(NAME), NAME");


         out.println("<td><b>Chromosome</b><br>");
         out.println("<select name=cid width=100 style=\"WIDTH: 100px\">");
         if(cid.equals("*"))
            out.println("<option selected value=\"*\" > *  </option>");
         else
            out.println("<option selected value=\"*\" > * </option>");

         while (rset.next()) {
            out.println("<option " +
                        (rset.getString("CID").equals(cid) ? "selected " : "" ) +
                        "value=\"" + rset.getString("CID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }

          out.println("</select></td>");
         // Reference
         out.println("<td><b>Reference</b><br>");
         out.println("<input name=reference value=\"" + replaceNull(reference, "") +
                     "\" style=\"WIDTH: 100px\" width=100>");
         out.println("</td>");
         // Level
         out.println("<td><b>Level</b><br>");
         out.println("<select name=level width=100 style=\"WIDTH: 100px\">");
         out.println("<option value=\"*\">*</option>");
         for (int i = 0; i <= 9; i++)
            out.println("<option " +
                        (level.equals("" + i) ? "selected " : "" ) +
                        "value=\"" + i + "\">" + i + "</option>");
         out.println("</select>");
         out.println("</td>");
         // Usr
         out.println("<td><b>User</b><br>");
         out.println("<input type=text name=usr width=100 style=\"WIDTH: 100px\" " +
                     "value=\"" + replaceNull(usr, "") + "\">");
         out.println("</td>");
         out.println("</tr>");
         // New row
         out.println("<tr>");
         // Marker
         out.println("<td><b>Marker</b><br>");
         out.println("<input name=marker value=\"" + replaceNull(marker, "") +
                     "\" style=\"WIDTH: 100px\" width=100>");
         out.println("</td>");
         // Allele 1
         out.println("<td><b>Allele name 1</b><br>");
         out.println("<input name=allele1 value=\"" + replaceNull(allele1, "") +
                     "\" style=\"WIDTH: 100px\" width=100>");
         out.println("</td>");
         // Allele 2
         out.println("<td><b>Allele name 2</b><br>");
         out.println("<input name=allele2 value=\"" + replaceNull(allele2, "") +
                     "\" style=\"WIDTH: 100px\" width=100>");
         out.println("</td>");
         // From date
         out.println("<td><b>Date from</b><br>");
         out.println("<input type=text name=f_date width=100 style=\"WIDTH: 100px\" " +
                     "value=\"" + replaceNull(f_date, "") + "\">");
         out.println("</td>");
         // To date
         out.println("<td><b>Date to</b><br>");
         out.println("<input type=text name=t_date width=100 style=\"WIDTH: 100px\" " +
                     "value=\"" + replaceNull(t_date, "") + "\">");
         out.println("</td>");
         out.println("</tr>");
         out.println("</table>"); // End of table for input fields

         out.println("</td>");


         //Buttons
         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");
         out.println("<input type=button value=\"New Genotype\""
                     + " onClick='parent.location.href=\"" +getServletPath("viewGeno/new?") + newQS + "\"' "
                     +"height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">"
                     +"</td>");
         out.println("<tr><td width=68 colspan=2>"
                     +"<input name=COUNT type=button value=\"Count\" width=\"69\""
                     +" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\" "
                     +"onClick='valForm(\"COUNT\");'>"
                     +"</td>"
                     +"<td width=68 colspan=2>"
                     +"<input name=DISPLAY type=button value=\"Display\""
                     +" width=\"70\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\" "
                     +"onClick='valForm(\"DISPLAY\")'>"
                     +"</td></tr>");


         // some hidden values
         out.println("<input type=hidden name=STARTINDEX value=\"" + startIndex + "\">");
         out.println("<input type=hidden name=ORDERBY value=\"" + orderby + "\">");
         out.println("<input type=hidden name=oper value=\"\">");
         out.println("<input type=hidden name=action value=\"\">");

         out.println("<td width=34 colspan=1><input name=TOP type=button value=\"<<\""
                     +"width=\"35\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 31px\" "
                     +"onClick='valForm(\"TOP\")'>"
                     +"</td>");
         out.println("<td width=34 colspan=1><input name=PREV type=submit value=\"<\""
                     +"width=\"34\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 32px\" "
                     +"onClick='valForm(\"PREV\")'>"
                     +"</td>");
         out.println("<td width=34 colspan=1><input name=NEXT type=submit value=\">\""
                     +"width=\"35\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 32px\" "
                     +"onClick='valForm(\"NEXT\")'>"
                     +"</td>");
         out.println("<td width=34 colspan=1><input name=END type=submit value=\">>\""
                     +"width=\"35\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 31px\" "
                     +"onClick='valForm(\"END\")'>"
                     +"</td>");

         out.println("</tr>");
         out.println("<tr>");
         out.println("<td colspan=4>");

         out.println("<table border=1 celspacing== cellpadding=0>");
         out.println("<tr><td>");
         out.println("<table border=0 cellpadding=0 cellspacing=0>");
         out.println("<tr>");
         out.println("<td>To level<br>");
         out.println("<select name=u_level width=60 style=\"WIDTH: 60px\">");
         int privileges[] = (int[]) session.getValue("PRIVILEGES");
         int highestLevel = 0;
         for (int i = 0; i < privileges.length; i++) {
            if (GENO_W9 - privileges[i]  >= 0 &&
                privileges[i] - GENO_W0 >= 0 &&
                privileges[i] - GENO_W0 > highestLevel) {
               // This is a geno_w# privilege and it's higher than
               // the previous highest level
               highestLevel = privileges[i] - GENO_W0;
            }
         }
         if (u_level == null || u_level.trim().equals(""))
            //u_level = "" + highestLevel;
            u_level = "" + 0;

         for (int i = 0; i <= highestLevel; i++) {
            out.println("<option " +
                        (Integer.parseInt(u_level) == i ? "selected " : "" ) +
                        "value=\"" + i + "\">" + i + "</option>");
         }
         out.println("</select>");
         out.println("</td>");
         out.println("<td valign=bottom align=center>");
         out.println("<input type=button width=60 style=\"WIDTH: 60px\" value=\"Update\" " +
                     writeGenoPrivDependentString(privileges, req, "onClick='valForm(\"BATCH_UPDATE\")'",
                                                  "disabled") + ">");
         out.println("</td></tr>");
         out.println("</table>"); // End of table for level update controls
         out.println("</td></tr>");
         out.println("</table>"); // End of table with border

         out.println("</td></tr>");
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


   /** Rebuild the Query String used on the top frame:
    * remove the ORDERBY parameter
    * remove the ACTION parameter
    * @param oldQS The old Query String
    * @return Return the new QS
    */   
   private String buildTopQS(String oldQS) 
   {
      StringBuffer sb = new StringBuffer(256);
      // First we remove the ORDERBY parameter (Must be at the end)
      int i1 = 0, i2 = 0;
      i1 = oldQS.indexOf("&ORDERBY=");
      if (i1 >= 0)
         oldQS = oldQS.substring(0, i1);

      // Now let's remove the parameter ACTION
      i1 = oldQS.indexOf("ACTION=");
      if (i1 >= 0) 
      {
         i2 = oldQS.indexOf("&", i1 + 1);
         if (12 > i1) 
         {
            sb.append(oldQS.substring(0, i1));
            sb.append(oldQS.substring(i2 + 1));
         } 
         else 
         {
            // There was no parameter after ACTION
         }
      } 
      else 
      { 
          // The query string didn't contain a ACTION-parameter
          sb.append(oldQS);
      }
      return sb.toString();
   }

   /**
    * @param action
    * @param startIndex
    * @param rows
    * @param maxRows
    * @return  
    */   
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
      } else if ("BATCH_UPDATE".equalsIgnoreCase(action)) {
         // Print something that isn't visible (to make the frame as big as it would be in the cases above
         output = new String("&nbsp;");
      } else {
         // ???
         output = new String("?" + action + "?");
      }

      return output;
   }
   
   /**
    * Write JavaScript to the browser for the top frame
    *
    * @param out   The printwriter object to write to
    */   
   private void writeTopScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("//	var rc = 1;");
      out.println("//	if ('DISPLAY' == action.toUpperCase() || 'COUNT' == action.toUppercase()) {");
      out.println("   if (document.forms[0].f_date.value != '') {");
      out.println("     if (!valDate(document.forms[0].f_date))");
      out.println("       return false;");
      out.println("   }");
      out.println("   if (document.forms[0].t_date.value != '') {");
      out.println("     if (!valDate(document.forms[0].t_date))");
      out.println("       return false;");
      out.println("   }");
      out.println("   if ('BATCH_UPDATE' == action.toUpperCase()) {");
      out.println("     if (!confirm('Are you sure you want to update all the\\n' + ");
      out.println("                  'genotypes selected by this filter?'))");
      out.println("       return false;");
      out.println("   }");
      out.println("   document.forms[0].action.value=action;");
      out.println("   document.forms[0].submit();");
      out.println("//	} else if ('BATCH_UPDATE' == action.toUpperCase()) {");
      out.println("//		if (confirm('Are you sure you want to update the genotype?')) {");
      out.println("//			document.forms[0].oper.value='UPDATE';");
      out.println("//			rc = 0;");
      out.println("//		}");
      out.println("//	} else {");
      out.println("//		document.forms[0].action.value='';");
      out.println("//	}");
      out.println("	");
      out.println("	return false;");
      out.println("	");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
   }
   
   /** 
    * The middle frame (contains header for the result-table)
    */
   private void writeMiddle(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException 
   {
      HttpSession session = req.getSession(true);
      // set content type and other response header fields first
      res.setContentType("text/html");

      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
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
         out.println("<html>\n<head>\n<link rel=\"stylesheet\" type=\"text/css\" href=\""+getURL("style/tableBar.css")+"\">");
         out.println("<base target=\"content\">");
         out.println("</head><body>");

         if(action != null) {
            out.println("&nbsp;" +buildInfoLine(action, startIndex, rows, maxRows) );
         }

         String oldQS, newQS;
         oldQS = req.getQueryString();
         String choosen= req.getParameter("ORDERBY");
         newQS = buildTopQS(oldQS);

         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=845 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");
         // the menu choices
         //Marker
         out.println("<td width=120><a href=\"" + getServletPath("viewGeno")+"?action=DISPLAY&" + newQS + "&ORDERBY=MNAME\">");
         if(choosen.equals("MNAME"))
            out.println("<FONT color=saddlebrown><b>Marker</b></FONT></a></td>\n");
         else out.println("Marker</a></td>\n");
         //Identity
         out.println("<td width=120><a href=\"" + getServletPath("viewGeno")+"?action=DISPLAY&" + newQS + "&ORDERBY=IDENTITY\">");
         if(choosen.equals("IDENTITY"))
            out.println("<FONT color=saddlebrown><b>Identity</b></FONT></a></td>\n");
         else out.println("Identity</a></td>\n");
         //allele 1
         out.println("<td width=100><a href=\"" + getServletPath("viewGeno")+"?action=DISPLAY&" + newQS + "&ORDERBY=A1NAME\">");
         if(choosen.equals("A1NAME"))
            out.println("<FONT color=saddlebrown><b>Allele 1</b></FONT></a></td>\n");
         else out.println("Allele 1</a></td>\n");
         // allele2
         out.println("<td width=100><a href=\"" + getServletPath("viewGeno")+"?action=DISPLAY&" + newQS + "&ORDERBY=A2NAME\">");
         if(choosen.equals("A2NAME"))
            out.println("<FONT color=saddlebrown><b>Allele 2</b></FONT></a></td>\n");
         else out.println("Allele 2</a></td>\n");

         // Level
         out.println("<td width=20><a href=\"" + getServletPath("viewGeno") + "?action=DISPLAY&" + newQS + "&ORDERBY=LEVEL_\">");
         if (choosen.equals("LEVEL_"))
            out.println("<FTON color=saddlebrown><b>L</b></FONT></a></td>");
         else
            out.println("L</q></td>");
         //Reference
         out.println("<td width=80><a href=\"" + getServletPath("viewGeno")+"?action=DISPLAY&" + newQS + "&ORDERBY=REFERENCE\">");
         if(choosen.equals("REFERENCE"))
            out.println("<FONT color=saddlebrown><b>Ref.</b></FONT></a></td>");
         else out.println("Ref.</a></td>");

         //USER
         out.println("<td width=80><a href=\"" + getServletPath("viewGeno")+"?action=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
         else out.println("User</a></td>\n");

         //Updated
         out.println("<td width=120><a href=\"" + getServletPath("viewGeno")+"?action=DISPLAY&" + newQS + "&ORDERBY=TS\">");
         if(choosen.equals("TS"))
            out.println("<FONT color=saddlebrown><b>Updated</b></FONT></a></td>\n");
         else out.println("Updated</a></td>\n");

         out.println("<td width=50>&nbsp;</td>");
         out.println("<td width=50>&nbsp;</td>");
         out.println("</table></table>");
         out.println("</body></html>");

      } catch (Exception e)	{
      }
      finally {
         try {
            //				if (rset != null) rset.close();
            //				if (stmt != null) stmt.close();
         } catch (Exception ignored) {}
      }
   }


   /**
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
      try {
         String suid = null, cid = null, action = null, identity,marker,orderby;
         int privileges[] = (int[]) session.getValue("PRIVILEGES");
         String oldQS = req.getQueryString();
         // We don't want the parameters iid and item in the query string,
         // Ssince we add them manually belove!
         oldQS = removeQSParameterMid(oldQS);
         oldQS = removeQSParameterIid(oldQS);
         action = req.getParameter("ACTION");
         suid = req.getParameter("suid");
         cid = req.getParameter("cid");
         identity = req.getParameter("identity");
         marker = req.getParameter("marker");

         orderby = req.getParameter("ORDERBY");
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
                     + "<title>bottomFrame</title>\n"
                     + "</head>\n"
                     + "<body>\n");

         conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();
         StringBuffer sbSQL = new StringBuffer(512);

/*
         sbSQL.append("SELECT SUNAME, IDENTITY, MNAME, " +
                      "A1NAME, A2NAME, LEVEL_, REFERENCE, " +
                      "USR, to_char(TS, '" + getDateFormat(session) + "') as TC_TS, MID, IID " +
                      "FROM gdbadm.V_GENOTYPES_3 WHERE " +
                      "SUID=" + suid + " AND CID=" + cid +
                      " ");
*/
       sbSQL.append("SELECT SUNAME, IDENTITY, MNAME, " +
                      "A1NAME, A2NAME, LEVEL_, REFERENCE, " +
                      "USR, to_char(TS, '" + getDateFormat(session) + "') as TC_TS, MID, IID " +
                      "FROM gdbadm.V_GENOTYPES_3 WHERE " +
                      "SUID=" + suid +" ");

         String qs = req.getQueryString();
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);

         if (orderby != null && !"".equalsIgnoreCase(orderby))
            sbSQL.append(" order by " + orderby);
         else
            sbSQL.append(" order by IDENTITY");


         rset = stmt.executeQuery(sbSQL.toString());

         out.println("<TABLE align=left border=0 cellPadding=0");
         out.println("cellSpacing=0 width=845 style=\"margin-left:2px\">");//STYLE=\"WIDTH: 790px;\">");
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

            out.println("<TD WIDTH=5></TD>");

            out.println("<TD WIDTH=120>" + formatOutput(session,rset.getString("MNAME"),15) +"</TD>");
            out.println("<TD WIDTH=120>" + formatOutput(session,rset.getString("IDENTITY"),15) + "</TD>");
            out.println("<TD WIDTH=100>" + formatOutput(session,rset.getString("A1NAME"),12) + "</TD>");
            out.println("<TD WIDTH=100>" + formatOutput(session,rset.getString("A2NAME"),12) + "</TD>");
            out.println("<TD WIDTH=20>" + formatOutput(session,rset.getString("LEVEL_"), 2) + "</TD>");
            out.println("<TD WIDTH=80>" + formatOutput(session,rset.getString("REFERENCE"),10)+"</TD>");
            out.println("<TD WIDTH=80>" + formatOutput(session,rset.getString("USR"),10) + "</TD>");
            out.println("<TD WIDTH=120>" + rset.getString("TC_TS") + "</TD>");


            out.println("<TD WIDTH=50><A HREF=\"" +getServletPath("viewGeno/details?iid=")
                        + rset.getString("IID")
                        + "&mid=" + rset.getString("MID")
                        + "&" + oldQS + "\" target=\"content\">Det.</A></TD>");
            out.println("<TD WIDTH=50>");
            out.println(writeGenoPrivDependentString(privileges, rset.getInt("LEVEL_"),
                                                     "<A HREF=\"" +getServletPath("viewGeno/edit?iid=") + rset.getString("IID") +
                                                     "&mid=" + rset.getString("MID") + "&" + oldQS + "\" target=\"content\">Edit</A>", "&nbsp;") );
            out.println("</TD></TR>");
            rowCount++;
         }
         out.println("<TR align=left bgcolor=oldlace><TD WIDTH=5>&nbsp&nbsp</TD></TR>");
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


   /**
    * The genotype's detail page
    */
   private void writeDetails(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException 
   {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");

      Connection conn = null;
      Statement stmt_curr = null, stmt_hist = null;
      ResultSet rset_curr = null, rset_hist = null;
      String prev_a1 = null;
      String prev_a2 = null;
      String prev_r1 = null;
      String prev_r2 = null;
      String prev_level = null;
      String prev_comm = null;
      String prev_ref = null;
      String prev_usr = null;
      String prev_ts = null;
      String curr_a1 = null;
      String curr_a2 = null;
      String curr_r1 = null;
      String curr_r2 = null;
      String curr_level = null;
      String curr_comm = null;
      String curr_ref = null;
      String curr_usr = null;
      String curr_ts = null;
      boolean has_history = false;

      try {
         String oldQS = buildQS(req);
         String iid = req.getParameter("iid");
         String mid = req.getParameter("mid");
         if (iid == null || iid.trim().equals("")) iid = "-1";
         if (mid == null || mid.trim().equals("")) mid = "-1";

         conn = (Connection) session.getValue("conn");
         // Get the current data
         stmt_curr = conn.createStatement();
         String strSQL = "SELECT SNAME, CNAME, MNAME, SUNAME, IDENTITY, AID1, AID2, "
            + "A1NAME, A2NAME, RAW1, RAW2, LEVEL_, COMM, REFERENCE, "
            + "USR, to_char(TS, '" + getDateFormat(session) + "') as TC_TS "
            + "FROM gdbadm.V_GENOTYPES_4 WHERE "
            + "MID=" + mid + " AND IID=" + iid;
         rset_curr = stmt_curr.executeQuery(strSQL);

         // Get the history
         stmt_hist = conn.createStatement();
         strSQL = "SELECT RAW1, RAW2, AID1, AID2, LEVEL_, " +
            "REFERENCE, COMM, USR, to_char(TS, '" + getDateFormat(session) + "') as TC_TS, TS as dummy " +
            "FROM gdbadm.V_GENOTYPES_LOG " +
            "WHERE MID=" + mid + " AND IID=" + iid + " " +
            "ORDER BY dummy desc";
         rset_hist = stmt_hist.executeQuery(strSQL);

         if (rset_curr.next()) {
            curr_a1 = rset_curr.getString("A1NAME");
            curr_a2 = rset_curr.getString("A2NAME");
            curr_r1 = rset_curr.getString("RAW1");
            curr_r2 = rset_curr.getString("RAW2");
            curr_ref = rset_curr.getString("REFERENCE");
            curr_level = rset_curr.getString("LEVEL_");
            curr_comm = rset_curr.getString("COMM");
            curr_usr = rset_curr.getString("USR");
            curr_ts = rset_curr.getString("TC_TS"); // Time stamp
         }
         if (rset_hist.next()) {
            if (rset_hist.getString("AID1") != null)
               prev_a1 = findAllele(conn, rset_hist.getString("AID1"));
            else
               prev_a1 = "";
            if (rset_hist.getString("AID2") != null)
               prev_a2 = findAllele(conn, rset_hist.getString("AID2"));
            else
               prev_a2 = "";
            prev_r1 = rset_hist.getString("RAW1");
            prev_r2 = rset_hist.getString("RAW2");
            prev_ref = rset_hist.getString("REFERENCE");
            prev_level = rset_hist.getString("LEVEL_");
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
                     "<b style=\"font-size: 15pt\">Genotypes - Details</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td></td></tr>");
         out.println("<tr><td></td><td>");

         out.println("<table nowrap border=0 cellSpacing=0>");
         out.println("<tr><td width=200 colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
         out.println("<tr><td>Species<td>" + rset_curr.getString("SNAME") + "</td></tr>");
         out.println("<tr><td>Chromosome<td>" + rset_curr.getString("CNAME") + "</td></tr>");
         out.println("<tr><td>Marker<td>" + rset_curr.getString("MNAME") + "</td></tr>");
         out.println("<tr><td>Sampling unit<td>" + rset_curr.getString("SUNAME") + "</td></tr>");
         out.println("<tr><td>Identity<td>" + rset_curr.getString("IDENTITY") + "</td></tr>");
         out.println("</table><br><br>");

         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");

         out.println("<table nowrap align=left border=0 cellSpacing=0 width=840px>");
         out.println("<tr bgcolor=Black><td align=center colspan=9><b><font color=\"#ffffff\" >Current Data</font></b></td></tr>");
         out.println("<tr bgcolor= \"#008B8B\" >");
         out.println("<td nowrap WIDTH=100>Allele1</td>");
         out.println("<td nowrap WIDTH=100>Allele2</td>");
         out.println("<td nowrap WIDTH=100>Raw1</td>");
         out.println("<td nowrap WIDTH=100>Raw2</td>");
         out.println("<td nowrap WIDTH=20>L</td>");
         out.println("<td nowrap WIDTH=150>Comment</td>");
         out.println("<td nowrap WIDTH=100>Reference</td>");
         out.println("<td nowrap WIDTH=50>User</td>");
         out.println("<td nowrap WIDTH=120>Last updated</td></tr>");

         out.println("<tr bgcolor=white>");
         // Allele1
         out.println("<td>");
         if (("" + curr_a1).equals("" + prev_a1))
            out.println(formatOutput(session, curr_a1, 12));
         else
            out.println("<font color=red>" + formatOutput(session, curr_a1, 12));
         out.println("</td>");
         // Allele2
         out.println("<td>");
         if (("" + curr_a2).equals("" + prev_a2))
            out.println(formatOutput(session, curr_a2, 12));
         else
            out.println("<font color=red>" + formatOutput(session, curr_a2, 12) + "</font>");
         out.println("</td>");
         // Raw 1
         out.println("<td>");
         if (("" + curr_r1).equals("" + prev_r1))
            out.println(formatOutput(session, curr_r1, 12));
         else
            out.println("<font color=red>" + formatOutput(session, curr_r1, 12) + "</font>");
         out.println("</td>");
         // Raw 2
         out.println("<td>");
         if (("" + curr_r2).equals("" + prev_r2))
            out.println(formatOutput(session, curr_r2, 12) );
         else
            out.println("<font color=red>" + formatOutput(session, curr_r2, 12) + "</font>");
         out.println("</td>");
         // Level
         out.println("<td>");
         if (("" + curr_level).equals("" + prev_level))
            out.println(formatOutput(session,  curr_level, 2));
         else
            out.println("<font color=red>" + formatOutput(session, curr_level, 2) + "</font>");
         out.println("</td>");
         // Comment
         out.println("<td>");
         if (("" + curr_comm).equals("" + prev_comm))
            out.println(formatOutput(session, curr_comm, 20) );
         else
            out.println("<font color=red>" + formatOutput(session, curr_comm, 20) + "</font>");
         out.println("</td>");
         // Reference
         out.println("<td>");
         if (("" + curr_ref).equals("" + prev_ref))
            out.println(formatOutput(session, curr_ref, 12));
         else
            out.println("<font color=red>" + formatOutput(session, curr_ref, 12) + "</font>");
         out.println("<td nowrap WIDTH=50>" + formatOutput(session, curr_usr, 8) + "</td>");
         out.println("<td nowrap WIDTH=120>" + formatOutput(session, curr_ts, 17) + "</td>"); // Last updated
         out.println("<tr bgcolor=Black>");
         out.println("<td align=center colspan=9><b><font color=\"#ffffff\">History</font></b></td></tr>");

         curr_a1 = prev_a1;
         curr_a2 = prev_a2;
         curr_r1 = prev_r1;
         curr_r2 = prev_r2;
         curr_level = prev_level;
         curr_ref = prev_ref;
         curr_comm = prev_comm;
         curr_usr = prev_usr;
         curr_ts = prev_ts;
         boolean odd = true;
         while (rset_hist.next()) {
            if (rset_hist.getString("AID1") != null)
               prev_a1 = findAllele(conn, rset_hist.getString("AID1") );
            else
               prev_a1 = "";
            if (rset_hist.getString("AID2") != null)
               prev_a2 = findAllele(conn, rset_hist.getString("AID2"));
            else
               prev_a2 = "";
            prev_r1 = rset_hist.getString("RAW1");
            prev_r2 = rset_hist.getString("RAW2");
            prev_ref = rset_hist.getString("REFERENCE");
            prev_level = rset_hist.getString("LEVEL_");
            prev_comm = rset_hist.getString("COMM");
            prev_usr = rset_hist.getString("USR");
            prev_ts = rset_hist.getString("TC_TS");

            if (odd)
               out.println("<tr bgcolor=white>");
            else
               out.println("<tr bgcolor=lightgrey>");
            odd = !odd;
            // Allele1
            out.println("<td>");
            if (("" + curr_a1).equals("" + prev_a1))
               out.println(formatOutput(session, curr_a1, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_a1, 12));
            out.println("</td>");
            // Allele2
            out.println("<td>");
            if (("" + curr_a2).equals("" + prev_a2))
               out.println(formatOutput(session, curr_a2, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_a2, 12) + "</font>");
            out.println("</td>");
            // Raw 1
            out.println("<td>");
            if (("" + curr_r1).equals("" + prev_r1))
               out.println(formatOutput(session, curr_r1, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_r1, 12) + "</font>");
            out.println("</td>");
            // Raw 2
            out.println("<td>");
            if (("" + curr_r2).equals("" + prev_r2))
               out.println(formatOutput(session, curr_r2, 12) );
            else
               out.println("<font color=red>" + formatOutput(session, curr_r2, 12) + "</font>");
            out.println("</td>");
            // Level
            out.println("<td>");
            if (("" + curr_level).equals("" + prev_level))
               out.println(formatOutput(session,  curr_level, 2));
            else
               out.println("<font color=red>" + formatOutput(session, curr_level, 2) + "</font>");
            out.println("</td>");
            // Comment
            out.println("<td>");
            if (("" + curr_comm).equals("" + prev_comm))
               out.println(formatOutput(session, curr_comm, 20) );
            else
               out.println("<font color=red>" + formatOutput(session, curr_comm, 20) + "</font>");
            out.println("</td>");
            // Reference
            out.println("<td>");
            if (("" + curr_ref).equals("" + prev_ref))
               out.println(formatOutput(session, curr_ref, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_ref, 12) + "</font>");
            out.println("<td nowrap WIDTH=50>" + formatOutput(session, curr_usr, 8) + "</td>");
            out.println("<td nowrap WIDTH=120>" + formatOutput(session, curr_ts, 17) + "</td></tr>"); // Last updated

            curr_a1 = prev_a1;
            curr_a2 = prev_a2;
            curr_r1 = prev_r1;
            curr_r2 = prev_r2;
            curr_level = prev_level;
            curr_ref = prev_ref;
            curr_comm = prev_comm;
            curr_usr = prev_usr;
            curr_ts = prev_ts;
         }
         if (has_history) {
            if (odd)
               out.println("<tr bgcolor=white>");
            else
               out.println("<tr bgcolor=lightgrey>");
            odd = !odd;
            // Allele1
            out.println("<td>");
            if (("" + curr_a1).equals("" + prev_a1))
               out.println(formatOutput(session, curr_a1, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_a1, 12));
            out.println("</td>");
            // Allele2
            out.println("<td>");
            if (("" + curr_a2).equals("" + prev_a2))
               out.println(formatOutput(session, curr_a2, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_a2, 12) + "</font>");
            out.println("</td>");
            // Raw 1
            out.println("<td>");
            if (("" + curr_r1).equals("" + prev_r1))
               out.println(formatOutput(session, curr_r1, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_r1, 12) + "</font>");
            out.println("</td>");
            // Raw 2
            out.println("<td>");
            if (("" + curr_r2).equals("" + prev_r2))
               out.println(formatOutput(session, curr_r2, 12) );
            else
               out.println("<font color=red>" + formatOutput(session, curr_r2, 12) + "</font>");
            out.println("</td>");
            // Level
            out.println("<td>");
            if (("" + curr_level).equals("" + prev_level))
               out.println(formatOutput(session,  curr_level, 2));
            else
               out.println("<font color=red>" + formatOutput(session, curr_level, 2) + "</font>");
            out.println("</td>");
            // Comment
            out.println("<td>");
            if (("" + curr_comm).equals("" + prev_comm))
               out.println(formatOutput(session, curr_comm, 20) );
            else
               out.println("<font color=red>" + formatOutput(session, curr_comm, 20) + "</font>");
            out.println("</td>");
            // Reference
            out.println("<td>");
            if (("" + curr_ref).equals("" + prev_ref))
               out.println(formatOutput(session, curr_ref, 12));
            else
               out.println("<font color=red>" + formatOutput(session, curr_ref, 12) + "</font>");
            out.println("<td nowrap WIDTH=50>" + formatOutput(session, curr_usr, 8) + "</td>");
            out.println("<td nowrap WIDTH=120>" + formatOutput(session, curr_ts, 17) + "</td></tr>"); // Last updated
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
                  //   getServletPath("viewGeno?&RETURNING=YES") + oldQS + "\"'>&nbsp;");
                     getServletPath("viewGeno?&RETURNING=YES")+ "\"'>&nbsp;");
         out.println("</td></tr></table>");

         out.println("</form>");

         out.println("</td></tr></table>");
         out.println("</body></html>");
      } catch (SQLException e) {
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
   
   /**
    * The new genotype page
    */
   private void writeNew(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("CREATE")) {
         if (createGenotype(req, res, conn))
            writeFrame(req, res);
      } else {
         writeNewPage(req, res);
      }
   }
   
   /**
    * @param req
    * @param res
    * @throws ServletException
    * @throws IOException  
    */   
   private void writeNewPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException 
   {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      String suid, cid, mid, newQS, pid, oper, item;
      try {
         conn = (Connection) session.getValue("conn");

         pid = (String) session.getValue("PID");
         suid = req.getParameter("suid");
         cid = req.getParameter("cid");
         mid = req.getParameter("mid");
         newQS = removeQSParameterOper(req.getQueryString());
         oper = req.getParameter("oper");

         if (oper == null || oper.trim().equals("")) oper = "SEL_CHANGED";
         item = req.getParameter("item");
         if (item == null || item.trim().equals("")) item = ""; // make sure that all of suid, cid, mid, aid are updated
         if (pid == null || "".equalsIgnoreCase(pid))
            pid = "-1";
         if (oper.equals("SEL_CHANGED")) {
            if (item.equals("suid")) {
               cid = findCid(conn, suid);
               mid = findMid(conn, suid, cid);
            } else if (item.equals("cid")) {
               mid = findMid(conn, suid, cid);
            } else if (item.equals("mid")) {
               ;
            } else {
               suid = findSuid(conn, pid);
               cid = findCid(conn, suid);
               mid = findMid(conn, suid, cid);
            }
         } else {
            suid = findSuid(conn, pid);
            cid = findCid(conn, suid);
            mid = findMid(conn, suid, cid);
         }

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<base target=\"content\">");

         writeNewScript(out);
         out.println("<title>New genotype</title>");
         
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Genotypes - New</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=get action=\"" +getServletPath("viewGeno/new?") + newQS + "\">");
         out.println("<br><br>");
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
         out.println("<td>Chromosome<br>");
         out.println("<SELECT name=cid WIDTH=150 height=25 " +
                     "style=\"HEIGHT: 25px; WIDTH: 150px\" " +
                     "onChange='selChanged(\"cid\")'>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CID, NAME FROM " +
                                  "gdbadm.V_CHROMOSOMES_1 " +
                                  "WHERE SID=(SELECT SID FROM V_ENABLED_SAMPLING_UNITS_1 " +
                                  "WHERE SUID=" + suid + ") ORDER BY " +
                                  "TO_NUMBER_ELSE_NULL(NAME), NAME");
         while (rset.next()) {
            if (cid != null && cid.equalsIgnoreCase(rset.getString("CID")))
               out.println("<OPTION selected value=\"" + rset.getString("CID") + "\">" +
                           rset.getString("NAME"));
            else
               out.println("<OPTION value=\"" + rset.getString("CID") + "\">" + rset.getString("NAME"));
         }
         out.println("</SELECT>");
         rset.close();
         stmt.close();
         out.println("<td>Marker<br>");
         out.println("<SELECT name=mid WIDTH=150 height=25 " +
                     "style=\"HEIGHT: 25px; WIDTH: 150px\" " +
                     "onChange='selChanged(\"mid\")'>");
         stmt = conn.createStatement();

         rset = stmt.executeQuery("SELECT MID, NAME FROM gdbadm.V_MARKERS_1" +
                                  " WHERE SUID=" + suid + " AND CID=" + cid + " ORDER BY NAME");

         while (rset.next()) {
            if (mid != null && mid.equalsIgnoreCase(rset.getString("MID")))
               out.println("<OPTION selected value=\"" + rset.getString("MID") + "\">" +
                           rset.getString("NAME"));
            else
               out.println("<OPTION value=\"" + rset.getString("MID") + "\">" + rset.getString("NAME"));

         }
         out.println("</select>");
         out.println("</td></tr>");
         out.println("<tr><td>Identity<br>");
         out.println("<select name=iid width=150 height=25 " +
                     "style=\"WIDTH: 150px; HEIGHT: 25px\">");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT IID, IDENTITY FROM " +
                                  "gdbadm.V_ENABLED_INDIVIDUALS_1 WHERE " +
                                  "SUID=" + suid + " ORDER BY IDENTITY");
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("IID") + "\">" +
                        rset.getString("IDENTITY"));
         }
         out.println("</select>");
         out.println("</td>");
         out.println("<td>Allele 1<br>");
         out.println("<select name=aid1 width=150 height=25 " +
                     "style=\"WIDTH: 150px; HEIGHT: 25px\">");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT AID, NAME FROM " +
                                  "gdbadm.V_ALLELES_1 WHERE " +
                                  "MID=" + mid + " ORDER BY NAME");
         out.println("<option value=\"\">(None)");
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("AID") + "\">" +
                        rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");
         out.println("<td>Allele 2<br>");
         out.println("<select name=aid2 width=150 height=25 " +
                     "style=\"WIDTH: 150px; HEIGHT: 25px\">");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT AID, NAME FROM " +
                                  "gdbadm.V_ALLELES_1 WHERE " +
                                  "MID=" + mid + " ORDER BY NAME");
         out.println("<option value=\"\">(None)");
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("AID") + "\">" +
                        rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td>Raw 1<br>");
         out.println("<input type=text name=r1 width=150 height=25 " +
                     "style=\"WIDTH: 150px; HEIGHT: 25px\">");
         out.println("</td>");
         out.println("<td>Raw 2<br>");
         out.println("<input type=text name=r2 width=150 height=25 " +
                     "style=\"WIDTH: 150px; HEIGHT: 25px\">");
         out.println("</td>");
         // Level
         out.println("<td>Level<br>");
         out.println("<select name=c_level width=150 style=\"WIDTH: 150px\">");
         int privileges[] = (int[]) session.getValue("PRIVILEGES");
         int highestLevel = 0;
         for (int i = 0; i < privileges.length; i++) {
            if (GENO_W9 - privileges[i]  >= 0 &&
                privileges[i] - GENO_W0 >= 0 &&
                privileges[i] - GENO_W0 > highestLevel) {
               // This is a geno_w# privilege and it's higher than
               // the previous highest level
               highestLevel = privileges[i] - GENO_W0;
            }
         }
         for (int i = 0; i < highestLevel; i++) {
            out.println("<option value=\"" + i + "\">" + i + "</option>");
         }
         out.println("<option selected value=\"" + highestLevel + "\">" +
                     highestLevel + "</option>");
         out.println("</select>");
         out.println("</td></tr>");
         out.println("<td COLSPAN=3>Comment<br>");
         out.println("<textarea rows=10 cols=40 name=comm>");
         out.println("</textarea>");
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<tr><td COLSPAN=3>");
         out.println("<table border=0 celpadding=0 cellspacing=0>");
         out.println("<tr>");
         out.println("<td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='document.location.href=\"" +
                  // getServletPath("viewGeno?") + newQS + "\"'>");
                   getServletPath("viewGeno?&RETURNING=YES") + "\"'>");
         out.println("&nbsp;");
         out.println("</td><td>");
         out.println("<input type=button value=Create width=100 " +
                     "style=\"WIDTH: 100px\" onClick='valForm()'>");
         out.println("&nbsp;");
         out.println("</td></tr></table>");
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
   
   /**
    * Write a JavaScript to the browser. This checks input parameters
    * and length of strings.
    * @param out  
    */   
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
      out.println("	if ( (\"\" + document.forms[0].r1.value) != \"\") {");
      out.println("		if (document.forms[0].r1.value.length > 20) {");
      out.println("			alert('Raw 1 must be less than 20 characters!');");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	}");
      out.println("	if ( (\"\" + document.forms[0].r2.value) != \"\") {");
      out.println("		if (document.forms[0].r1.value.length > 20) {");
      out.println("			alert('Raw 2 must be less than 20 characters!');");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	}");
      out.println("	");
      out.println("	");
      out.println("	if (rc) {");
      out.println("		if (confirm('Are you sure that you want to create the genotype?')) {");
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

   /**
    * The genotype's edit page
    */
   private void writeImpFile(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      //HttpSession session = req.getSession(true);
      //Connection conn =  null;
      //conn = (Connection) session.getValue("conn");
      writeImpFilePage(req, res);
   }


   /**
    * Writes the page used for importing genotypes.
    *
    * @param request The request object to use.
    * @param response The response object to use
    * @exception IOException If no writer can be retrieved from the
    *            response object.
    */
   private void writeImpFilePage(HttpServletRequest request,
                                 HttpServletResponse response)
      throws IOException
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

         out.println("<title>Import genotypes</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=0>" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Genotypes - File import</b></center>" +
                     "</td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post enctype=\"multipart/form-data\" action=\"" +
                     getServletPath("viewGeno/impMultipart") + "\">");
         out.println("<table border=0>");
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
         out.println("<input type=file name=filename " +
                     "style=\"WIDTH: 350px\">");
         out.println("</td></tr>");
         // Level
         out.println("<tr><td></td><td>Level<br>");
         out.println("<select name=level style=\"WIDTH: 200px\">");
         int[] privileges = (int[]) session.getValue("PRIVILEGES");
         int myHighestLevel = -1;
         for (int i = 0; i < privileges.length; i++) {
            if (GENO_W9 - privileges[i] >= 0 &&
                privileges[i] - GENO_W0 >= 0 &&
                (privileges[i] - GENO_W0) > myHighestLevel) {
               myHighestLevel = privileges[i] - GENO_W0;
            }
         }

         out.println("<option selected value=\"" + 0 + "\">" +
                     0 + "</option>");

         for (int i = 1; i <= myHighestLevel; i++) {
            out.println("<option value=\"" + i + "\">" + i + "</option>");
         }

         out.println("</select>");
         out.println("</td></tr>");

         // type of upload
         out.println("<tr><td></td><td>Mode<br>");
         out.println("<select name=type width=200 style=\"WIDTH: 200px\">");
         out.println("<option value=\"CREATE\">Create</option>");
         out.println("<option value=\"CREATE_OR_UPDATE\">Create or update</option>");
         out.println("<option value=\"UPDATE\">Update</option>");
         out.println("</select>");
         out.println("</td></tr>");

         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td></td></tr>");
         out.println("<tr><td width=10 style=\"WIDTH: 10px\">&nbsp;</td><td>");
         out.println("<table border=0><tr>");
         out.println("<td>");

/*         out.println("<input type=button value=Send " +
                     "style=\"WIDTH: 100px\" onClick='valForm()'>");
*/
         out.println("<input type=button value=Send " +
                     "style=\"WIDTH: 100px\" onClick='document.forms[0].submit()'>");

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


   /**
    * @param req
    * @param res
    * @throws ServletException
    * @throws IOException  */   
   private void writeEdit(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      if (oper == null) oper = "";
      if (oper.equals("DELETE")) {
         if (deleteGenotype(req, res, conn))
            writeFrame(req, res);
      } else if (oper.equals("UPDATE")) {
         if(updateGenotype(req, res, conn))
            writeEditPage(req, res);
      } else
         writeEditPage(req, res);
   }
   
   /**
    * Write an edit page for the genotype selected
    *
    * @param req
    * @param res
    * @throws ServletException
    * @throws IOException  
    */   
   private void writeEditPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException 
   {
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
         String oldQS = buildQS(req);
         // Since oper might be included in the old query string, we better remove it.
         // If we don't, the application won't be able to retrive the correct value because
         // servlets gets the parameters included in the url before the ones posted.
         oldQS = removeQSParameterOper(oldQS);
         String iid = req.getParameter("iid");
         String mid = req.getParameter("mid");
         String sql = "SELECT A1NAME, A2NAME, RAW1, RAW2, "
            + "SNAME, SUNAME, CNAME, MNAME, AID1, AID2, IDENTITY, "
            + "REFERENCE, COMM, USR, to_char(TS, '" + getDateFormat(session)
            + "') as TC_TS, LEVEL_ "
            + "FROM gdbadm.V_GENOTYPES_4 WHERE "
            + "MID=" + mid + " AND IID=" + iid;
         rset = stmt.executeQuery(sql);

         rset.next();
         out.println("<html>");
         out.println("<head>");
         writeEditScript(out);
         out.println("<title>Edit genotype</title>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Genotypes - Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         out.println("<form method=post action=\"" + getServletPath("viewGeno/edit?") +
                     oldQS + "\">");
         out.println("<table cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td>");

         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr>");
         out.println("<td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td>");
         out.println("</tr>");
         out.println("<tr><td>Species</td><td>" + rset.getString("SNAME") + "</td></tr>");
         out.println("<tr><td>Chromosome</td><td>" + rset.getString("CNAME") + "</td></tr>");
         out.println("<tr><td>Sampling unit</td><td>" + rset.getString("SUNAME") + "</td></tr>");
         out.println("<tr><td>Marker</td><td>" + rset.getString("MNAME") + "</td></tr>");
         out.println("<tr><td>Identity</td><td>" + rset.getString("IDENTITY") + "</td></tr>");
         out.println("<tr><td>Last updated by</td><td>" + rset.getString("USR") + "</td></tr>");
         out.println("<tr><td>Last updated</td><td>" + rset.getString("TC_TS") + "</td></tr>");
         out.println("</table>");
         out.println("</td></tr><tr><td></td><td>");



         // The changable data
         String aid1 = rset.getString("AID1");
         String aid2 = rset.getString("AID2");
         String raw1 = rset.getString("RAW1");
         String raw2 = rset.getString("RAW2");
         String ref = rset.getString("REFERENCE");
         String comm = rset.getString("COMM");
         String level = rset.getString("LEVEL_");
         if (raw1 == null) raw1 = "";
         if (raw2 == null) raw2 = "";
         if (ref == null) ref = "";
         if (comm == null) comm = "";
         rset.close();
         stmt.close();
         // Belowe we use rather cryptic names for the form data. We do this to prevent that
         // the data in the form won't collide with the data in the old query string

         out.println("<table border=0 cellpading=0 cellspacing=0>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Changable data</font></td></tr>");
         // Allele1
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT AID, NAME FROM gdbadm.V_ALLELES_1 WHERE MID=" + mid + " ORDER BY NAME");
         out.println("<tr><td width=200>Allele 1<br>");
         out.println("<select name=a1 width=200 style=\"WIDTH: 200px\">");
         out.println("<option value=\"\">(None)");
         while (rset.next()) {
            out.println("<option " +
                        (aid1 != null && aid1.equals(rset.getString("AID")) ? "selected " : "") +
                        "value=\"" + rset.getString("AID") + "\">" +
                        rset.getString("NAME") +"</option>");
         }
         out.println("</select>");
         rset.close();
         stmt.close();
         out.println("</td>");
         // Raw 1
         out.println("<td width=200>Raw 1<br>");
         out.println("<input type=text name=r1 value=\"" + raw1 + "\">");
         out.println("</td></tr>");
         // Allele2
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT AID, NAME FROM gdbadm.V_ALLELES_1 WHERE MID=" + mid + " ORDER BY NAME");
         out.println("<tr><td width=200>Allele 2<br>");
         out.println("<select name=a2 width=200 style=\"WIDTH: 200px\">");
         out.println("<option value=\"\">(None)");
         while (rset.next()) {
            out.println("<option " +
                        (aid2 != null && aid2.equals(rset.getString("AID")) ? "selected " : "") +
                        "value=\"" + rset.getString("AID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         // Raw 2
         out.println("<td width=200>Raw 2<br>");
         out.println("<input type=text name=r2 value=\"" + raw2 + "\">");
         out.println("</td></tr>");
         // Reference
         out.println("<tr><td>Reference<br>");
         out.println("<input type=text name=r value=\"" + replaceNull(ref, "") + "\">");
         out.println("</td>");
         // Level
         out.println("<td>Level<br>");
         int privileges[] = (int[]) session.getValue("PRIVILEGES");
         int highestLevel = 0;
         for (int i = 0; i < privileges.length; i++) {
            if (GENO_W9 - privileges[i]  >= 0 &&
                privileges[i] - GENO_W0 >= 0 &&
                privileges[i] - GENO_W0 > highestLevel) {
               // This is a geno_w# privilege and it's higher than
               // the previous highest level
               highestLevel = privileges[i] - GENO_W0;
            }
         }
         out.println("<select name=n_level width=150 style=\"WIDTH: 150px\">");
         for (int i = 0; i <= highestLevel; i++) {
            out.println("<option " +
                        (Integer.parseInt(level) == i ? "selected " : "" ) +
                        "value=\"" + i + "\">" + i + "</option>");
         }
         out.println("</select>");
         out.println("</td></tr>");
         // Comment
         out.println("<tr><td colspan=0 align=left>Comment<br>");
         out.println("<textarea name=c rows=8 cols=45>" +
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
                     //getServletPath("viewGeno?") + oldQS + "&item=no\";'>&nbsp;");
                     getServletPath("viewGeno?&RETURNING=YES") + "\";'>&nbsp;");

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


   /**
    * Updates the level of all genotypes selected by the current filter.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if all genotypes was updated.
    *         False if any genotype was not updated.
    */
   private boolean batchUpdateGenotype(HttpServletRequest request,
                                       HttpServletResponse response)
   {
      boolean isOk = true;
      String errMessage = null;
      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer();
      try
      {
         int iid, mid, id, level, myHighestLevel = 0;
         Integer aid1, aid2;
         String suid, cid, identity, mname, raw1, raw2, ref, comm;
         HttpSession session = request.getSession(true);

         // First we find the level at which I'm allowed to update genotypes.
         int privileges[] = (int[]) session.getValue("PRIVILEGES");
         for (int i = 0; i < privileges.length; i++)
         {
            if (GENO_W9 - privileges[i]  >= 0 &&
                privileges[i] - GENO_W0 >= 0 &&
                privileges[i] - GENO_W0 > myHighestLevel)
            {
               // This is a geno_w# privilege and it's higher than
               // the previous highest level
               myHighestLevel = privileges[i] - GENO_W0;
            }
         }
         suid = request.getParameter("suid");
         cid = request.getParameter("cid");

         level = Integer.parseInt(request.getParameter("u_level"));
         id = Integer.parseInt((String) session.getValue("UserID"));

         conn = (Connection) session.getValue("conn");
         conn.setAutoCommit(false);
         stmt = conn.createStatement();
         sbSQL.append("SELECT IDENTITY, IID, MNAME, MID, AID1, AID2, RAW1, RAW2, REFERENCE, COMM " +
                      "FROM V_GENOTYPES_3 WHERE SUID=" + suid + " AND CID="
                      + cid + " AND LEVEL_ <= " + myHighestLevel);
         sbSQL.append(buildFilter(request));
         rset = stmt.executeQuery(sbSQL.toString());
         DbGenotype dbg = new DbGenotype();

         while (rset.next() && isOk)
         {
            iid = rset.getInt("IID");
            identity = rset.getString("IDENTITY");
            mid = rset.getInt("MID");
            mname = rset.getString("MNAME");
            if (rset.getString("AID1") != null)
               aid1 = new Integer(rset.getInt("AID1"));
            else
               aid1 = null;
            if (rset.getString("AID2") != null)
               aid2 = new Integer(rset.getInt("AID2"));
            else
               aid2 = null;
            raw1 = rset.getString("RAW1");
            raw2 = rset.getString("RAW2");
            ref = rset.getString("REFERENCE");
            comm = rset.getString("COMM");

            dbg.UpdateGenotype(conn, iid, mid, aid1, aid2,
                               raw1, raw2, ref, comm, level, id);
            errMessage = dbg.getErrorMessage();
            if (errMessage != null && !errMessage.trim().equals(""))
            {
               errMessage = "Failed to update the genotype for individual [" + identity +
                  "] and marker [" + mname + "]. Error: " + errMessage;
               throw new Exception(errMessage);
            }
         }
      }
      catch (Exception e)
      {
         // Flag for error and set the errMessage if it has not been set
         isOk = false;
         e.printStackTrace(System.err);
         Errors.logError("viewGeno.bachUpdateGenotype(...) exception");
         Errors.logDebug("SQL="+sbSQL);
         if (errMessage == null)
         {
            errMessage = e.getMessage();
         }
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

      commitOrRollback(conn, request, response, "Genotypes.UpdateLevel", errMessage,
                       "viewGeno", isOk);
      return isOk;
   }



  /**
   * This function simply determines which viewPage should be displayed
   *
   */
  private void chooseView(HttpServletRequest req, HttpServletResponse res)
  throws IOException 
  {

     String type=null;
     type=req.getParameter("viewType");

     if(type != null && !type.trim().equals(""))
     {

       if(type.equals("parental"))
       {
        writeParentalView(req,res); // default

       }
       else if(type.equals("child"))
       {
        writeChildView(req,res); // default
       }
       else if(type.equals("group"))
       {
        writeGroupView(req,res); // default
       }
       else // default
       {
        writeParentalView(req,res); // default
       }

     }
     else
     {
        writeParentalView(req,res); // default
     }

  }


   /**
    *  The parental view page
    */
   private void writeParentalView(HttpServletRequest req, HttpServletResponse res)
        throws IOException 
   {
      String suid=null;
      String pid = null;
      String oper = null;
      String m1=null,m2=null,m3=null;
      String m1_name="None" ,m2_name="None",m3_name="None";
      String ind_id=null;
      String item; // This variable tells which of the parameter that has changed
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      String ind=null;

      suid = req.getParameter("suid");

      if(suid == null)
      {
         suid = "-1";
      }
      ind = req.getParameter("ind");
      pid = (String) session.getValue("PID");
      oper = req.getParameter("oper");
      m1=req.getParameter("m1");
      m2=req.getParameter("m2");
      m3=req.getParameter("m3");
      //oper = req.getParameter("oper");
      if(m1==null || m1.trim().equals(""))
      {
       m1="-1";
      }
      if(m2==null || m2.trim().equals(""))
      {
       m2="-1";
      }
      if(m3==null || m3.trim().equals(""))
      {
       m3="-1";
      }

      res.setContentType("text/html");
      res.setHeader("Pragme", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      if (pid == null) pid = "-1";
      if (ind == null) ind = "-1";


      out.println("<html>");
      out.println("<head><title>Genotype FamilyView</title>");
      HTMLWriter.css(out,getURL("style/axDefault.css"));
      // new look
      writeWiewScript(out);
      out.println("</head>");
      out.println("<body>");

      out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
      out.println("<tr>");
      out.println("<td width=14 rowspan=3></td>");
      out.println("<td width=736 colspan=2 height=15>");
      out.println("<center>" +
                  "<b style=\"font-size: 15pt\">Genotypes - Parental View </b></center>");
      out.println("</td></tr>");
      out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
      out.println("</tr></table>");


      Statement stmt = null;
      ResultSet rset = null;

      int privileges[] = (int[]) session.getValue("PRIVILEGES");
      out.println("<form name=\"form1\" action=\"" +getServletPath("viewGeno/customView")+ "\" method=\"get\">");
      out.println("<table width=750 border=0 cellspacing=0 cellspacing=1>");


      try {
         // ********************************************************************************


        //first a box where we can change views
        // must make sure we post the correct type here when submit is pressed etc
         out.println("<tr><td>View Type<br>");
         out.println("<select name=viewType onChange='document.forms[0].submit()' " +
                     "width=100 style=\"WIDTH: 100px\">");
         out.println("<option selected value=\"parental\" > parental");
         out.println("<option value=\"child\" > child ");
         out.println("<option value=\"group\" > group ");

         out.println("</select>");
         out.println("</td></tr>");
         out.println("<tr><td width=110><hr></td></tr>");
         out.println("<tr><td>&nbsp;</td></tr>");


         // Available sampling units
         out.println("<tr><td>Sampling Unit<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid + " order by NAME");
         out.println("<select name=suid onChange='document.forms[0].submit()' " +
                     "width=100 style=\"WIDTH: 100px\">");

         boolean first=true;
         while (rset.next()) {
            if (first && suid.equals("-1")) {
               // It's the first time -> set suid
               first = false;
               suid = rset.getString("SUID");
            }
            if (suid.equals(rset.getString("SUID")))
               out.println("<option selected value=\"" + rset.getString("SUID") +
                           "\">" + rset.getString("NAME"));


            else
               out.println("<option value=\"" + rset.getString("SUID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");
         // ********************************************************************************
         // Available Individuals
         out.println("<td>Individual<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();

         rset = stmt.executeQuery("SELECT IID, IDENTITY FROM gdbadm.V_INDIVIDUALS_1 " +
                                  "WHERE SUID=" + suid + " order by IDENTITY");

         out.println("<select name=ind " +
                     "width=100 style=\"WIDTH: 100px\">");

         first=true;
         while (rset.next()) {
           if (ind.equals(rset.getString("IID")))//the already chhosen one
           {
               out.println("<option selected value=\"" + rset.getString("IID") +
                           "\">" + rset.getString("IDENTITY"));
                           ind_id=rset.getString("IDENTITY");
           }
            else
               out.println("<option value=\"" + rset.getString("IID") +
                           "\">" + rset.getString("IDENTITY"));
         }
         out.println("</select>");
         out.println("</td>");

         // ********************************************************************************
         // First marker
         out.println("<td>Marker 1<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();

         rset = stmt.executeQuery("SELECT MID, NAME FROM gdbadm.V_MARKERS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");

         out.println("<select name=m1 " +
                     "style=\"WIDTH: 120px\">");

         if(m1.equals("None") || m1.equals("-1"))
          out.println("<option selected value=\"-1\">None");
         else
          out.println("<option  value=\"-1\">None");

         while (rset.next()) {
           if (m1.equals(rset.getString("MID")))
           {
               out.println("<option selected value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
               m1_name=rset.getString("NAME");
            }
            else
               out.println("<option value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");


         // ********************************************************************************
         // Second marker
         out.println("<td>Marker 2<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();

         rset = stmt.executeQuery("SELECT MID, NAME FROM gdbadm.V_MARKERS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");

         out.println("<select name=m2 " +
                     "style=\"WIDTH: 120px\">");

         if(m2.equals("None") || m2.equals("-1"))
          out.println("<option selected value=\"-1\">None");
         else
          out.println("<option  value=\"-1\">None");

         while (rset.next()) {
           if (m2.equals(rset.getString("MID")))
           {
               out.println("<option selected value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
                           m2_name=rset.getString("NAME");
           }
            else
               out.println("<option value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");

         // ********************************************************************************
         // Third marker
         out.println("<td>Marker 3<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();

         rset = stmt.executeQuery("SELECT MID, NAME FROM gdbadm.V_MARKERS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");

         out.println("<select name=m3 " +
                     "style=\"WIDTH: 120px\">");

         if(m2.equals("None") || m2.equals("-1"))
          out.println("<option selected value=\"-1\">None");
         else
          out.println("<option  value=\"-1\">None");

         while (rset.next()) {
           if (m3.equals(rset.getString("MID")))
           {
               out.println("<option selected value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
                           m3_name=rset.getString("NAME");
           }

            else
               out.println("<option value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");

//----------------------
         out.println("</tr>");

// submit button
        out.println("<tr>");
        out.println("<td>");
        out.println("&nbsp;<br><input type=button value=Display " +
                     "onClick='document.forms[0].oper.value=\"DISPLAY\";document.forms[0].submit();'>");
        out.println("</td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("<hr>");
        out.println("<br><br>");
//---------------------------------
// The display part of the page

if (oper != null && oper.equals("DISPLAY"))
{
          String ind_name=null,father=null;
          String[] alleles;//= new String [2];
          parentData parents, grandParents, ggrandParents;
          String bgcolor;
          // a "main" table
          out.println("<table>");
          out.println("<td>"); // TD to contain for the rest of tables

          // the table header
          out.println("<table bgcolor=\"#008B8B\" cellSpacing=0> ");
//          out.println("<table>");

          out.println("<tr></tr>");
          out.println("<tr>");

//          out.println("<tr bgcolor=\"#008B8B\" >");
          out.println("<td width=100><FONT color=oldlace>Identity</FONT></td>");
          out.println("<td width=100><FONT color=oldlace>Father</FONT></td>");
          out.println("<td width=100><FONT color=oldlace>Mother</FONT></td>");
          out.println("<td width=166><FONT color=oldlace>"+m1_name+"</FONT></td>");
          out.println("<td width=166><FONT color=oldlace>"+m2_name+"</FONT></td>");
          out.println("<td width=166><FONT color=oldlace>"+m3_name+"</FONT></td>");
          out.println("</tr>");
          out.println("</table>"); // end of header

          out.println("<table cellSpacing=0 >"); // the whole data table
//         out.println("<table>");

          bgcolor = "white";
//          out.println("<tr bgcolor=white>");
          out.println("<tr bgcolor="+bgcolor+">");

          out.println("<td width=100>"+formatOutput(session,ind_id,11)+"</td>");
          parents=getParents(req,suid,ind);
          out.println("<td width=100>"+formatOutput(session,parents.father_identity,11)+"</td>");
          out.println("<td width=100>"+formatOutput(session,parents.mother_identity,11)+"</td>");
           // write individual's data
          alleles=getAlleles(req,suid,ind,m1);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,ind,m2);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,ind,m3);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          out.println("</tr>");

          // write parent's data
          // the father
        if(parents.father_identity != null && !parents.father_identity.trim().equals(""))
        {


          if(bgcolor.equals("lightgrey"))
              bgcolor = "white";
            else
              bgcolor = "lightgrey";
          out.println("<tr bgcolor="+bgcolor+">");

          out.println("<td width=100>"+
                      formatOutput(session,parents.father_identity,11)+"</td>");
          grandParents=getParents(req,suid,parents.father_iid);
          out.println("<td width=100>"+formatOutput(session,grandParents.father_identity,11)+"</td>");
          out.println("<td width=100>"+formatOutput(session,grandParents.mother_identity,11)+"</td>");

          alleles=getAlleles(req,suid,parents.father_iid,m1);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,parents.father_iid,m2);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,parents.father_iid,m3);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");

          out.println("</tr>"); // end father
          }
          // the mother
           if(parents.mother_identity != null && !parents.mother_identity.trim().equals(""))
           {

          if(bgcolor.equals("lightgrey"))
              bgcolor = "white";
            else
              bgcolor = "lightgrey";
          out.println("<tr bgcolor="+bgcolor+">");
//            out.println("<tr bgcolor=white>");
            out.println("<td width=100>"
                       +formatOutput(session,parents.mother_identity,11)+"</td>");
            grandParents=getParents(req,suid,parents.mother_iid);
            out.println("<td width=100>"+formatOutput(session,grandParents.father_identity,11)+"</td>");
            out.println("<td width=100>"+formatOutput(session,grandParents.mother_identity,11)+"</td>");
            alleles=getAlleles(req,suid,parents.mother_iid,m1);
            out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");

            alleles=getAlleles(req,suid,parents.mother_iid,m2);
            out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");

            alleles=getAlleles(req,suid,parents.mother_iid,m3);
            out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");

            out.println("</tr>");
           }

          // fathers parents
          grandParents=getParents(req,suid,parents.father_iid);
          if(grandParents.father_identity != null && !grandParents.father_identity.trim().equals(""))
           {
          if(bgcolor.equals("lightgrey"))
              bgcolor = "white";
            else
              bgcolor = "lightgrey";
          out.println("<tr bgcolor="+bgcolor+">");

//          out.println("<tr bgcolor=lightgrey>");
          out.println("<td width=100>"+
                      formatOutput(session,grandParents.father_identity,11)+"</td>");
          ggrandParents=getParents(req,suid,grandParents.father_iid);
          out.println("<td width=100>"+formatOutput(session,ggrandParents.father_identity,11)+"</td>");
          out.println("<td width=100>"+formatOutput(session,ggrandParents.mother_identity,11)+"</td>");

          alleles=getAlleles(req,suid,grandParents.father_iid,m1);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,grandParents.father_iid,m2);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,grandParents.father_iid,m3);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          out.println("</tr>");
          }

          if(grandParents.mother_identity != null && !grandParents.mother_identity.trim().equals(""))
           {
          if(bgcolor.equals("lightgrey"))
              bgcolor = "white";
            else
              bgcolor = "lightgrey";
          out.println("<tr bgcolor="+bgcolor+">");

           //          out.println("<tr bgcolor=white>");
          out.println("<td width=100>"+
                       formatOutput(session,grandParents.mother_identity,11)+"</td>");
          ggrandParents=getParents(req,suid,grandParents.mother_iid);
          out.println("<td width=100>"+formatOutput(session,ggrandParents.father_identity,11)+"</td>");
          out.println("<td width=100>"+formatOutput(session,ggrandParents.mother_identity,11)+"</td>");
          alleles=getAlleles(req,suid,grandParents.mother_iid,m1);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,grandParents.mother_iid,m2);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,grandParents.mother_iid,m3);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          out.println("</tr>");
          }
          // mothers parents
          grandParents=getParents(req,suid,parents.mother_iid);
          if(grandParents.father_identity != null && !grandParents.father_identity.trim().equals(""))
           {

          if(bgcolor.equals("lightgrey"))
              bgcolor = "white";
            else
              bgcolor = "lightgrey";
          out.println("<tr bgcolor="+bgcolor+">");
//          out.println("<tr bgcolor=lightgrey>");
          out.println("<td width=100>"
                       +formatOutput(session,grandParents.father_identity,11)+"</td>");
          ggrandParents=getParents(req,suid,grandParents.father_iid);
          out.println("<td width=100>"+formatOutput(session,ggrandParents.father_identity,11)+"</td>");
          out.println("<td width=100>"+formatOutput(session,ggrandParents.mother_identity,11)+"</td>");
          alleles=getAlleles(req,suid,grandParents.father_iid,m1);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,grandParents.father_iid,m2);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,grandParents.father_iid,m3);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          out.println("</tr>");
          }
          if(grandParents.mother_identity != null && !grandParents.mother_identity.trim().equals(""))
          {

          if(bgcolor.equals("lightgrey"))
              bgcolor = "white";
            else
              bgcolor = "lightgrey";
          out.println("<tr bgcolor="+bgcolor+">");
//          out.println("<tr bgcolor=white>");
          out.println("<td width=100>"
                       +formatOutput(session,grandParents.mother_identity,11)+"</td>");
          ggrandParents=getParents(req,suid,grandParents.mother_iid);
          out.println("<td width=100>"+formatOutput(session,ggrandParents.father_identity,11)+"</td>");
          out.println("<td width=100>"+formatOutput(session,ggrandParents.mother_identity,11)+"</td>");
          alleles=getAlleles(req,suid,grandParents.mother_iid,m1);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,grandParents.mother_iid,m2);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,grandParents.mother_iid,m3);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          out.println("</tr>");
          }
          out.println("</table>"); // end of data table
          out.println("</td></table>"); // end of "MAIN" table
}
      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
      } catch (Exception e) {
         e.printStackTrace(System.err);
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
      out.println("<input type=hidden name=oper value=\"\">");
      out.println("<input type=hidden name=item value=\"\">");
      out.println("</form>");
      out.println("</body>");
      out.println("</html>");

   }
//

    /**
    *  The child view page
    *
    *
    */
  /***************************************************************************************
    */

   private void writeChildView(HttpServletRequest req, HttpServletResponse res)
        throws IOException {


      String suid=null;
      String pid = null;
      String oper = null;
      String m1=null,m2=null,m3=null;
      String m1_name="None" ,m2_name="None",m3_name="None";
      String mother_iid=null,father_iid=null,mother_identity=null,father_identity=null;


      String item; // This variable tells which of the parameter that has changed
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      String ind=null;

      suid = req.getParameter("suid");
      if(suid == null)
      {
         suid = "-1";
      }
      ind = req.getParameter("ind");
      father_iid = req.getParameter("father");
      mother_iid = req.getParameter("mother");

      pid = (String) session.getValue("PID");
      oper = req.getParameter("oper");
      m1=req.getParameter("m1");
      m2=req.getParameter("m2");
      m3=req.getParameter("m3");

      if(m1==null || m1.trim().equals("")) m1="-1";
      if(m2==null || m2.trim().equals("")) m2="-1";
      if(m3==null || m3.trim().equals("")) m3="-1";

      res.setContentType("text/html");
      res.setHeader("Pragme", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      if (pid == null) pid = "-1";
      if (ind == null) ind = "-1";
      if (father_iid == null) father_iid = "-1";
      if (mother_iid == null) mother_iid = "-1";
      if (father_identity == null) father_identity = "-1";
      if (mother_identity == null) mother_identity = "-1";


      out.println("<html>");
      out.println("<head><title>Genotype Child View</title>");
      HTMLWriter.css(out,getURL("style/axDefault.css"));
      out.println("</head>");
      out.println("<body>");

      writeWiewScript(out);
      // new look
      out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
      out.println("<tr>");
      out.println("<td width=14 rowspan=3></td>");
      out.println("<td width=736 colspan=2 height=15>");
      out.println("<center>" +
                  "<b style=\"font-size: 15pt\">Genotypes - Child View </b></center>");
      out.println("</td></tr>");
      out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
      out.println("</tr></table>");

      Statement stmt = null;
      ResultSet rset = null;
      int privileges[] = (int[]) session.getValue("PRIVILEGES");
      out.println("<form name=\"form1\" action=\"" +getServletPath("viewGeno/customView")+ "\" method=\"get\">");
      out.println("<table width=750 border=0 cellspacing=0 cellspacing=1>");


      try {
         // ********************************************************************************


        //first a box where we can change views
        // must make sure we post the correct type here when submit is pressed etc
         out.println("<tr><td>View Type<br>");
         out.println("<select name=viewType onChange='document.forms[0].submit()' " +
                     "width=100 style=\"WIDTH: 100px\">");
         out.println("<option value=\"parental\" > parental ");
         out.println("<option selected value=\"child\" > child");
         out.println("<option value=\"group\" > group ");

         out.println("</select>");
        out.println("</td></tr>");
        out.println("<tr><td width=110><hr></td></tr>");
        out.println("<tr><td>&nbsp;</td></tr>");

         // Available sampling units
         out.println("<tr><td>Sampling Unit<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid + " order by NAME");
         out.println("<select name=suid onChange='document.forms[0].submit()' " +
                     "width=100 style=\"WIDTH: 100px\">");

         boolean first=true;
         while (rset.next()) {
            if (first && suid.equals("-1")) {
               // It's the first time -> set suid
               first = false;
               suid = rset.getString("SUID");
            }
            if (suid.equals(rset.getString("SUID")))
               out.println("<option selected value=\"" + rset.getString("SUID") +
                           "\">" + rset.getString("NAME"));

            else
               out.println("<option value=\"" + rset.getString("SUID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");

         // ********************************************************************************
         // Available Fathers
         out.println("<td>Father<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();

         rset = stmt.executeQuery("SELECT IID, IDENTITY FROM gdbadm.V_INDIVIDUALS_1 " +
                                  "WHERE SUID=" + suid +" AND SEX='M'"+ " order by IDENTITY");

         out.println("<select name=father " +
                     "width=100 style=\"WIDTH: 100px\">");

         if(father_iid.equals("*"))
         {
          out.println("<option selected value=\"*\">*");
          father_identity="*";
         }
         else
         {
          out.println("<option value=\"*\">*");
         }

         first=true;
         while (rset.next()) {
           if (father_iid.equals(rset.getString("IID")))//the already choosen one
           {
               out.println("<option selected value=\"" + rset.getString("IID") +
                           "\">" + rset.getString("IDENTITY"));
                           father_identity=rset.getString("IDENTITY");
           }
            else
               out.println("<option value=\"" + rset.getString("IID") +
                           "\">" + rset.getString("IDENTITY"));
         }
         out.println("</select>");
         out.println("</td>");


         // ********************************************************************************
         // Available Mothers
         out.println("<td>Mother<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();

         rset = stmt.executeQuery("SELECT IID, IDENTITY FROM gdbadm.V_INDIVIDUALS_1 " +
                                  "WHERE SUID=" + suid +" AND SEX='F'"+ " order by IDENTITY");

         out.println("<select name=mother " +
                     "width=100 style=\"WIDTH: 100px\">");

         first=true;
         if(mother_iid.equals("*"))
         {
          out.println("<option selected value=\"*\">*");
          mother_identity="*";
         }
         else
         {
          out.println("<option value=\"*\">*");
         }

         while (rset.next()) {
           if (mother_iid.equals(rset.getString("IID")))//the already chhosen one
           {
               out.println("<option selected value=\"" + rset.getString("IID") +
                           "\">" + rset.getString("IDENTITY"));
                           mother_identity=rset.getString("IDENTITY");
           }
            else
               out.println("<option value=\"" + rset.getString("IID") +
                           "\">" + rset.getString("IDENTITY"));
         }
         out.println("</select>");
         out.println("</td>");


         // ********************************************************************************
         // First marker
         out.println("<td>Marker 1<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();

         rset = stmt.executeQuery("SELECT MID, NAME FROM gdbadm.V_MARKERS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");

         out.println("<select name=m1 " +
                     "style=\"WIDTH: 120px\">");

         if(m1.equals("None") || m1.equals("-1"))
          out.println("<option selected value=\"-1\">None");
         else
          out.println("<option  value=\"-1\">None");

         while (rset.next()) {
           if (m1.equals(rset.getString("MID")))
           {
               out.println("<option selected value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
               m1_name=rset.getString("NAME");
            }
            else
               out.println("<option value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");


         // ********************************************************************************
         // Second marker
         out.println("<td>Marker 2<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();

         rset = stmt.executeQuery("SELECT MID, NAME FROM gdbadm.V_MARKERS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");

         out.println("<select name=m2 " +
                     "style=\"WIDTH: 120px\">");

         if(m2.equals("None") || m2.equals("-1"))
          out.println("<option selected value=\"-1\">None");
         else
          out.println("<option  value=\"-1\">None");
         while (rset.next()) {
           if (m2.equals(rset.getString("MID")))
           {
               out.println("<option selected value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
                           m2_name=rset.getString("NAME");
           }
            else
               out.println("<option value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");

         // ********************************************************************************
         // Third marker
         out.println("<td>Marker 3<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();

         rset = stmt.executeQuery("SELECT MID, NAME FROM gdbadm.V_MARKERS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");

         out.println("<select name=m3 " +
                     "style=\"WIDTH: 120px\">");

        if(m3.equals("None") || m3.equals("-1"))
          out.println("<option selected value=\"-1\">None");
         else
          out.println("<option  value=\"-1\">None");

         while (rset.next()) {
           if (m3.equals(rset.getString("MID")))
           {
               out.println("<option selected value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
                           m3_name=rset.getString("NAME");
           }
            else
               out.println("<option value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");

         out.println("</tr>");

// submit button
        out.println("<tr>");
        out.println("<td>");
        out.println("&nbsp;<br><input type=button value=Display " +
                     "onClick='document.forms[0].oper.value=\"DISPLAY\";document.forms[0].submit();'>");
        out.println("</td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("<hr>");
        out.println("<br><br>");
//---------------------------------
// The display part of the page


if (oper != null && oper.equals("DISPLAY"))
{
          String ind_name=null,father=null;
          String[] alleles;
          parentData parents, grandParents;

          // a "main" table
          out.println("<table>");
          out.println("<td>"); // TD to contain for the rest of tables

          // the table header
          out.println("<table bgcolor=\"#008B8B\" cellSpacing=0> ");
          out.println("<tr></tr>");
          out.println("<tr>");
          //out.println("<td width=10>&nbsp</td>");
          out.println("<td width=100><FONT color=oldlace>Identity</FONT></td>");
          out.println("<td width=100><FONT color=oldlace>Father</FONT></td>");
          out.println("<td width=100><FONT color=oldlace>Mother</FONT></td>");
          out.println("<td width=166><FONT color=oldlace>"+m1_name+"</FONT></td>");
          out.println("<td width=166><FONT color=oldlace>"+m2_name+"</FONT></td>");
          out.println("<td width=166><FONT color=oldlace>"+m3_name+"</FONT></td>");

          out.println("</tr>");
          out.println("</table>"); // end of header

          // this id done sice we might have one or two parents here
          Vector inds=new Vector();
//          Vector inds2=new Vector();

         // get all children to this parent(s)
         stmt = conn.createStatement();

         /*
         if(mother_identity.equals("*") &&  father_identity.equals("*"))
         {
           out.println("At least one parent must be specified!");
         }

         */
         if(mother_identity.equals("*"))
         {
          rset = stmt.executeQuery(" SELECT FIDENTITY, MIDENTITY, IID, IDENTITY, SEX FROM gdbadm.V_INDIVIDUALS_2 WHERE SUID="
                    + suid +" AND FIDENTITY='"+ father_identity+"'" );
         }
         else if (father_identity.equals("*"))
         {
          rset = stmt.executeQuery(" SELECT FIDENTITY, MIDENTITY, IID, IDENTITY, SEX FROM gdbadm.V_INDIVIDUALS_2 WHERE SUID="
                    + suid +" AND MIDENTITY='"+ mother_identity+"'");
         }
         else // we have both parents
         {
          rset = stmt.executeQuery(" SELECT FIDENTITY, MIDENTITY, IID, IDENTITY, SEX FROM gdbadm.V_INDIVIDUALS_2 WHERE SUID="
                    + suid +" AND MIDENTITY='"+ mother_identity +"' AND FIDENTITY='"+father_identity+"'");

         }

         // for every child, get all the subsequent offsring
          while(rset.next())
          {
            indData tmpInd=new indData();
            tmpInd.father_identity=rset.getString("FIDENTITY");
            tmpInd.mother_identity=rset.getString("MIDENTITY");
            tmpInd.sex =rset.getString("SEX");
            tmpInd.iid =rset.getString("IID");
            tmpInd.identity =rset.getString("IDENTITY");

            if(!indExists(inds,tmpInd.identity))
            {
              inds.addElement(tmpInd);
              addUnrelatedParent(req,inds,tmpInd,suid);
              getAllGenerations(req,suid,rset.getString("IDENTITY"),rset.getString("SEX"),inds);
            }
          }


          out.println("<table cellSpacing=0 >"); // the whole data table
          String bgcolor = "lightgrey";
          for (int i=0;i<inds.size();i++)
          {
            if(bgcolor.equals("lightgrey"))
              bgcolor = "white";
            else
              bgcolor = "lightgrey";

           indData tmp;
           tmp = (indData) inds.elementAt(i);

           out.println("<tr bgcolor="+bgcolor+">");
           out.println("<td width=100>"+formatOutput(session,tmp.identity,11)+"</td>");
           out.println("<td width=100>"+formatOutput(session,tmp.father_identity,11)+"</td>");
           out.println("<td width=100>"+formatOutput(session,tmp.mother_identity,11)+"</td>");

          // collect data
          alleles=getAlleles(req,suid,tmp.iid,m1);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,tmp.iid,m2);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,tmp.iid,m3);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          }

          out.println("</table>"); // end of data table
          out.println("</td></table>"); // end of "MAIN" table
}
      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
      } catch (Exception e) {
         e.printStackTrace(System.err);
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
      out.println("<input type=hidden name=oper value=\"\">");
      out.println("<input type=hidden name=item value=\"\">");
      out.println("</form>");
      out.println("</body>");
      out.println("</html>");

   }

//-----------------------------

  /**
    *  The Group view page
    *
    *
    */
  /***************************************************************************************
    */

   private void writeGroupView(HttpServletRequest req, HttpServletResponse res)
        throws IOException {


      String suid=null;
      String gid = null,gsid=null;
      String pid = null;
      String oper = null;
      String m1=null,m2=null,m3=null;
      String m1_name="None" ,m2_name="None",m3_name="None";
      String iid=null,identity=null;


      String item; // This variable tells which of the parameter that has changed
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");

      suid = req.getParameter("suid");
      if(suid == null)
      {
         suid = "-1";
      }

      pid = (String) session.getValue("PID");
      oper = req.getParameter("oper");
      m1=req.getParameter("m1");
      m2=req.getParameter("m2");
      m3=req.getParameter("m3");
      gid = req.getParameter("gid");
      gsid = req.getParameter("gsid");
      if(m1==null || m1.trim().equals("")) m1="-1";
      if(m2==null || m2.trim().equals("")) m2="-1";
      if(m3==null || m3.trim().equals("")) m3="-1";
      if(gid==null || gid.trim().equals("")) gid= "-1";
      if(gsid==null || gsid.trim().equals("")) gsid= "-1";

      res.setContentType("text/html");
      res.setHeader("Pragme", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      if (pid == null) pid = "-1";

      out.println("<html>");
      out.println("<head><title>Genotype Group View</title>");
      HTMLWriter.css(out,getURL("style/axDefault.css"));
      out.println("</head>");
      out.println("<body>");
      writeWiewScript(out);
      // new look
      out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
      out.println("<tr>");
      out.println("<td width=14 rowspan=3></td>");
      out.println("<td width=736 colspan=2 height=15>");
      out.println("<center>" +
                  "<b style=\"font-size: 15pt\">Genotypes - Group View </b></center>");
      out.println("</td></tr>");
      out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
      out.println("</tr></table>");

      Statement stmt = null;
      ResultSet rset = null;
      int privileges[] = (int[]) session.getValue("PRIVILEGES");
      out.println("<form name=\"form1\" action=\"" +getServletPath("viewGeno/customView")+ "\" method=\"get\">");
      out.println("<table width=750 border=0 cellspacing=0 cellspacing=1>");


      try {
         // ********************************************************************************


        //first a box where we can change views
        // must make sure we post the correct type here when submit is pressed etc
         out.println("<tr><td>View Type<br>");
         out.println("<select name=viewType onChange='selectionChanged(\"viewType\")' " +
                     "width=100 style=\"WIDTH: 100px\">");


         out.println("<option value=\"parental\" > parental ");
         out.println("<option value=\"child\" > child ");
         out.println("<option selected value=\"group\" > group");

         out.println("</select>");
         out.println("</td></tr>");
         out.println("<tr><td width=110><hr></td></tr>");
         out.println("<tr><td>&nbsp;</td></tr>");

         // Available sampling units
         out.println("<tr><td>Sampling Unit<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid + " order by NAME");

         out.println("<select name=suid onChange='selectionChanged(\"suid\")' " +
                     "width=100 style=\"WIDTH: 100px\">");


         boolean first=true;
         while (rset.next()) {
            if (first && suid.equals("-1")) {
               // It's the first time -> set suid
               first = false;
               suid = rset.getString("SUID");
            }
            if (suid.equals(rset.getString("SUID")))
               out.println("<option selected value=\"" + rset.getString("SUID") +
                           "\">" + rset.getString("NAME"));

            else
               out.println("<option value=\"" + rset.getString("SUID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");


         // Available Groupings
         out.println("<td>Grouping<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT GSID, NAME FROM gdbadm.V_GROUPINGS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");

       out.println("<select name=gsid onChange='selectionChanged(\"gsid\")' " +
                     "width=100 style=\"WIDTH: 100px\">");

         while (rset.next()) {
            if (gsid.equals("-1")) {
               // It's the first time -> set gsid
               gsid = rset.getString("GSID");
            }
            if (gsid.equals(rset.getString("GSID")))
            {
               out.println("<option selected value=\"" + rset.getString("GSID") +
                           "\">" + rset.getString("NAME"));
                            gsid = rset.getString("GSID");
            }
            else
               out.println("<option value=\"" + rset.getString("GSID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");

         // ********************************************************************************
         // Available Groups
         out.println("<td>Group<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();


         rset = stmt.executeQuery("SELECT GID, NAME FROM gdbadm.V_GROUPS_1 " +
                                  "WHERE GSID="+gsid+" order by NAME");

         out.println("<select name=gid " +
                     "width=100 style=\"WIDTH: 100px\">");

         first=true;
         while (rset.next()) {
           if (gid.equals(rset.getString("GID")))//the already choosen one
           {
               out.println("<option selected value=\"" + rset.getString("GID") +
                           "\">" + rset.getString("NAME"));
           }
            else
               out.println("<option value=\"" + rset.getString("GID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");


         // ********************************************************************************
         // First marker
         out.println("<td>Marker 1<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();

         rset = stmt.executeQuery("SELECT MID, NAME FROM gdbadm.V_MARKERS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");

         out.println("<select name=m1 " +
                     "style=\"WIDTH: 120px\">");

        if(m1.equals("None") || m1.equals("-1"))
          out.println("<option selected value=\"-1\">None");
         else
          out.println("<option  value=\"-1\">None");

         while (rset.next()) {
           if (m1.equals(rset.getString("MID")))
           {
               out.println("<option selected value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
               m1_name=rset.getString("NAME");
            }
            else
               out.println("<option value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");


         // ********************************************************************************
         // Second marker
         out.println("<td>Marker 2<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();

         rset = stmt.executeQuery("SELECT MID, NAME FROM gdbadm.V_MARKERS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");

         out.println("<select name=m2 " +
                     "style=\"WIDTH: 120px\">");

         if(m2.equals("None") || m2.equals("-1"))
          out.println("<option selected value=\"-1\">None");
         else
          out.println("<option  value=\"-1\">None");

         while (rset.next()) {
           if (m2.equals(rset.getString("MID")))
           {
               out.println("<option selected value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
                           m2_name=rset.getString("NAME");
           }
            else
               out.println("<option value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");

         // ********************************************************************************
         // Third marker
         out.println("<td>Marker 3<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();

         rset = stmt.executeQuery("SELECT MID, NAME FROM gdbadm.V_MARKERS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");

         out.println("<select name=m3 " +
                     "style=\"WIDTH: 120px\">");

        if(m3.equals("None") || m3.equals("-1"))
          out.println("<option selected value=\"-1\">None");
         else
          out.println("<option  value=\"-1\">None");

         while (rset.next()) {
           if (m3.equals(rset.getString("MID")))
           {
               out.println("<option selected value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
                           m3_name=rset.getString("NAME");
           }
            else
               out.println("<option value=\"" + rset.getString("MID") +
                           "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");

         out.println("</tr>");

// submit button
        out.println("<tr>");
        out.println("<td>");
        out.println("&nbsp;<br><input type=button value=Display " +
                     "onClick='document.forms[0].oper.value=\"DISPLAY\";document.forms[0].submit();'>");
        out.println("</td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("<hr>");
        out.println("<br><br>");
//---------------------------------
// The display part of the page

if (oper != null && oper.equals("DISPLAY"))
{
          String[] alleles;
          parentData parents;

          // a "main" table
          out.println("<table>");
          // adjust left side
         //  out.println("<td width=1>&nbsp</td>");
          out.println("<td>"); // TD to contain for the rest of tables

          // the table header
          out.println("<table bgcolor=\"#008B8B\" cellSpacing=0> ");
          out.println("<tr></tr>");
          out.println("<tr>");
          out.println("<td width=100><FONT color=oldlace>Identity</FONT></td>");
          out.println("<td width=100><FONT color=oldlace>Father</FONT></td>");
          out.println("<td width=100><FONT color=oldlace>Mother</FONT></td>");
          out.println("<td width=166><FONT color=oldlace>"+m1_name+"</FONT></td>");
          out.println("<td width=166><FONT color=oldlace>"+m2_name+"</FONT></td>");
          out.println("<td width=166><FONT color=oldlace>"+m3_name+"</FONT></td>");
          out.println("</tr>");
          out.println("</table>"); // end of header

          out.println("<table cellSpacing=0 >"); // the whole data table
          //Find all individuals in this group
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT IID, IDENTITY FROM gdbadm.V_INDIVIDUALS_1 WHERE IID "
                                  +  "IN(SELECT IID FROM gdbadm.V_SETS_GQL WHERE "
                                  + "SUID=" + suid + " AND GID=" + gid + ")"
                                  + " order by identity");

        String bgcolor = "lightgrey";
         while(rset.next())
         {
            if(bgcolor.equals("lightgrey"))
              bgcolor = "white";
            else
              bgcolor = "lightgrey";

          iid=rset.getString("IID");
          identity=rset.getString("IDENTITY");
          parents=getParents(req,suid,iid);

          out.println("<tr bgcolor="+bgcolor+">");
          out.println("<td width=100>"+formatOutput(session,identity,11)+"</td>");
          out.println("<td width=100>"+formatOutput(session,parents.father_identity,11)+"</td>");
          out.println("<td width=100>"+formatOutput(session,parents.mother_identity,11)+"</td>");
           // collect data
          alleles=getAlleles(req,suid,iid,m1);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,iid,m2);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          alleles=getAlleles(req,suid,iid,m3);
          out.println("<td width=166>"+formatOutput(session,alleles[0],10)+", "
                      +formatOutput(session,alleles[1],10)+"</td>");
          out.println("</tr>");

        }//while rset.next()


          out.println("</table>"); // end of data table
          out.println("</td></table>"); // end of "MAIN" table
}
      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
      } catch (Exception e) {
         e.printStackTrace(System.err);
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
      out.println("<input type=hidden name=oper value=\"\">");
      out.println("<input type=hidden name=item value=\"\">");
      out.println("</form>");
      out.println("</body>");
      out.println("</html>");

   }
//

   /**
    * Creates a new genotype.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if genotype was created.
    *         False if genotype was not created.
    */
   private boolean createGenotype(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int mid, iid, id, c_level;
         Integer aid1 = null,
            aid2 = null;
         String r1 = null, r2 = null, comm = null;
         connection.setAutoCommit(false);
         id = Integer.parseInt((String) session.getValue("UserID"));
         mid = Integer.parseInt(request.getParameter("mid"));
         iid = Integer.parseInt(request.getParameter("iid"));
         c_level = Integer.parseInt(request.getParameter("c_level"));
         if (request.getParameter("aid1") == null ||
             request.getParameter("aid1").trim().equals(""))
         {
            aid1 = null;
         }
         else
         {
            aid1 = new Integer(request.getParameter("aid1"));
         }
         if (request.getParameter("aid2") == null ||
             request.getParameter("aid2").trim().equals(""))
         {
            aid2 = null;
         }
         else
         {
            aid2 = new Integer(request.getParameter("aid2"));
         }
         r1 = request.getParameter("r1");
         r2 = request.getParameter("r2");
         comm = request.getParameter("comm");

         DbGenotype dbg = new DbGenotype();
         dbg.CreateGenotype(connection, iid, mid, aid1, aid2, r1, r2, null, comm, c_level, id);
         errMessage = dbg.getErrorMessage();
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
                       "Genotypes.New.Create", errMessage, "viewGeno?", isOk);
      return isOk;
   }


   /**
    * Updates deviating genotypes.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if genotypes was updated.
    *         False if genotypes was not updated.
    */
/*   private boolean updateDeviatingGenotypes(HttpServletRequest request,
                                            HttpServletResponse response)
   {
      boolean isOk = true;
      String errMessage = null;
      Connection connection = null;
      try
      {
         HttpSession session = request.getSession(true);
         connection = (Connection) session.getValue("conn");
         int id, level, suid;
         String marker, identity, extension;
         String a1name, a2name, raw1, raw2, ref, comm;
         DbGenotype dbg = new DbGenotype();
         connection.setAutoCommit(false);
         id = Integer.parseInt(request.getParameter("id"));
         level = Integer.parseInt(request.getParameter("level"));
         suid = Integer.parseInt(request.getParameter("suid"));

         Enumeration e = request.getParameterNames();
         String pn;
         String value;
         while (e.hasMoreElements())
         {
            pn = (String) e.nextElement();
            if (pn.startsWith("upd_"))
            {
               marker = pn.substring("upd_".length(), pn.indexOf("_", "upd_".length()));
               identity = pn.substring("upd_".length() + marker.length() + 1);
               extension = "_" + marker + "_" + identity;
               a1name = request.getParameter("a1" + extension);
               a2name = request.getParameter("a2" + extension);
               raw1 = request.getParameter("raw1" + extension);
               raw2 = request.getParameter("raw2" + extension);
               ref = request.getParameter("ref" + extension);
               comm = request.getParameter("comm" + extension);
               dbg.UpdateGenotype(connection, suid, identity, marker,
                                  a1name, a2name, raw1, raw2, ref, comm,
                                  level, id);
               errMessage = dbg.getErrorMessage();
               Assertion.assertMsg(errMessage == null ||
                                errMessage.trim().equals(""), errMessage);
            }
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

      commitOrRollback(connection, request, response,
                       "Genotypes.Import.UpdateMode.Send.Deviation.Update",
                       errMessage, "viewGeno", isOk);
      return isOk;
   }

*/
   /**
    * Creates or updates genotypes from a file.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if everything was ok.
    *         False if any errors.
    * @exception IOException If writing any of the pages fails.
    * @exception ServletException If writing any of the pages fails.
    */
   private boolean createFile(HttpServletRequest request,
                              HttpServletResponse response)
      throws IOException, ServletException
   {
      boolean isOk = true;
      String errMessage = null;
      String uploadMode = "";

      Connection connection = null;
      Vector genoDiffs = null;
      FileParser fileParser = null;
      MatrixFileParser matrixParser = null;

      try
      {
         HttpSession session = request.getSession(true);
         connection = (Connection) session.getValue("conn");
         connection.setAutoCommit(false);

         String upPath = getUpFilePath();


         int samplingUnitId = Integer.parseInt(request.getParameter("suid"));
         int userId = Integer.parseInt((String) session.getValue("UserID"));
         int level = Integer.parseInt(request.getParameter("level"));
         uploadMode = request.getParameter("type");

         DbGenotype dbGenotype = new DbGenotype();
         String systemFileName = request.getParameter("fileName");
         int dotPlace=systemFileName.indexOf(".");
         
         // Filenames without a dot generated errors. 
         // 
         if (dotPlace <= 0 )
         {
             dotPlace = systemFileName.length();
         }
          
         // get rid of strange endings...
         String newFileName=systemFileName.substring(0,dotPlace)+".txt";
         systemFileName=newFileName;


          if(systemFileName!=null && !systemFileName.trim().equalsIgnoreCase(""))
          {
            // Get the header information from the file to determine if we
            // are reading a list or a matrix file.
            FileHeader header = FileParser.scanFileHeader(upPath + "/" +
                                                          systemFileName);
            // Ensure file format is list or matrix
            Assertion.assertMsg(header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.LIST) ||
                             header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.MATRIX),
                             "Format type name should be list or matrix " +
                             "but found found " + header.formatTypeName());
            // If file is list
            if (header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.LIST))
            {
               fileParser = new FileParser(upPath + "/" +  systemFileName);
               fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.GENOTYPE,
                                                                           FileTypeDefinition.LIST));


                // first, we create any missing alleles in the database
                dbGenotype.CreateAllelesList(fileParser, connection,
                                                  samplingUnitId,
                                                 userId);


               if (uploadMode.equals("CREATE"))
               {
                  dbGenotype.CreateGenotypesList(fileParser, connection,
                                                 level, samplingUnitId,
                                                 userId);
               }
               else if (uploadMode.equals("UPDATE"))
               {
                  dbGenotype.UpdateGenotypesList(fileParser,
                                                 connection,level,
                                                 samplingUnitId,  userId,
                                                 getMaxDeviations());
               }
               else if (uploadMode.equals("CREATE_OR_UPDATE"))
               {
                  dbGenotype.CreateOrUpdateGenotypesList(fileParser,
                                                         connection, level,
                                                         samplingUnitId,
                                                         userId,
                                                         getMaxDeviations());
               }
            }

            // if file is a matrix
            else if (header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.MATRIX))
            {
               matrixParser = new MatrixFileParser(upPath + "/" +  systemFileName);
               matrixParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.GENOTYPE,
                                                                           FileTypeDefinition.MATRIX));

             
                // first, we create any missing alleles in the database
                dbGenotype.CreateAllelesMatrix(matrixParser, connection,
                                                  samplingUnitId,
                                                 userId);



               if (uploadMode.equals("CREATE"))
               {
               
                  dbGenotype.CreateGenotypesMatrix(matrixParser,
                                                   connection, level,
                                                   samplingUnitId, userId);
               }
               else if (uploadMode.equals("UPDATE"))
               {
 

                  dbGenotype.UpdateGenotypesMatrix(matrixParser,
                                                   connection, level,
                                                   samplingUnitId,
                                                   userId,getMaxDeviations());
               }
               else if (uploadMode.equals("CREATE_OR_UPDATE"))
               {


                  dbGenotype.CreateOrUpdateGenotypesMatrix(matrixParser,
                                                           connection,
                                                           level,
                                                           samplingUnitId,
                                                           userId,
                                                           getMaxDeviations());
               }
            }

            errMessage = dbGenotype.getErrorMessage();
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
      commitOrRollback(connection, request, response,
                           "Genotypes.Import.CreateOrCreateUpdate.Send",
                           errMessage, "viewGeno/impFile", isOk);
/*
      // if commit/rollback was ok and if the database operation was
      // successful, decide what to do.
      if (commitOrRollback(connection, request, response,
                           "Genotypes.Import.CreateOrCreateUpdate.Send",
                           errMessage, "viewGeno/impFile", isOk)
          && isOk)
      {
         if (uploadMode.equals("CREATE_OR_UPDATE") && genoDiffs.size() > 0)
         {
            // We need to display a page where the user can choose which
            // genotypes to update and which to discard!
            writeGenoDiffs(request, response, genoDiffs);
         }
         else if (uploadMode.equals("UPDATE") && genoDiffs.size() > 0)
         {
            // We need to display a page where the user can choose which
            // genotypes to update and which to discard!
               writeGenoDiffs(request, response, genoDiffs);
         }
         else
         {
            writeFrame(request, response);
         }
      }
      */
      writeFrame(request, response);

      return isOk;
   }



   /**
    * Compares genotypes from a file with those in database.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @return True if everything was ok.
    *         False if any errors.
    * @exception IOException If writing any of the pages fails.
    * @exception ServletException If writing any of the pages fails.
    */
  private boolean compareFile(HttpServletRequest request,
                              HttpServletResponse response)
  throws IOException, ServletException
  {
    HttpSession session = request.getSession(true);
    boolean isOk = true;
    boolean errorFound = false;
    boolean knownIdentity=false;
    String errorMessage = null;
    String uploadMode = "";
    FileWriter fileOut=null;
    Connection connection = null;
    Vector genoDiffs = null;
    Vector fatalErrors= new Vector();
    FileParser fp = null;
    MatrixFileParser mfp = null;
    ResultSet rset=null;
    Statement stmt=null;
    String titles[];
    char delimeter;
    int nrErrors = 0;
    int nrWarnings = 0;

    int nrDeviations=0;
    int rows=0;
    String givenFileName = null;
    String systemFileName = null;
    String markers[];
    // to store genotype data
    String marker=null;
    String allele1=null;
    String allele2=null;
    String raw1=null;
    String raw2=null;
    String ref=null;
    String indId=null;
    String ind=null;
    String comm=null;
    FileHeader header=null;

    int samplingUnitId=-1;
    int userId=-1;
    int level=-1;
    try
    {
      connection = (Connection) session.getValue("conn");
      connection.setAutoCommit(false);
      String upPath = getUpFilePath();
      
      // File size is limited to 6 Megabyte
      MultipartRequest multiRequest =
            new MultipartRequest(request, upPath, 6 * 1024 * 1024);
      samplingUnitId = Integer.parseInt(multiRequest.getParameter("suid"));
      userId = Integer.parseInt((String) session.getValue("UserID"));
      level = Integer.parseInt(multiRequest.getParameter("level"));
      uploadMode = multiRequest.getParameter("type");

      Enumeration files = multiRequest.getFileNames();
      // not needed here, but we need to register filetypedefinitions...
      DbGenotype dbg=new DbGenotype();

       if (files.hasMoreElements())
       {
          stmt = connection.createStatement();
          givenFileName = (String) files.nextElement();
          systemFileName = multiRequest.getFilesystemName(givenFileName);
       }
       if(systemFileName == null || systemFileName.trim().equals(""))
       {
            errorFound=true;
            fatalErrors.addElement(" Unable to get filename.");
       }
       else
       {
            // Get the header information from the file to determine if we
            // are reading a list or a matrix file.
            header = FileParser.scanFileHeader(upPath + "/" +systemFileName);

            int dotPlace=systemFileName.indexOf(".");
            // get rid of strange endings...
            String newFileName=systemFileName.substring(0,dotPlace)+".txt";


            fileOut = new FileWriter(upPath+ "/" + "checked_"+newFileName);

          if(!header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.LIST) &&
             !header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.MATRIX))
          {
              fatalErrors.addElement("Format type name should be LIST or MATRIX " +
                                            "but found found " + header.formatTypeName());
              errorFound=true;
          }

          // If file is list
          if (header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.LIST)
              && !errorFound)
          {
              fp = new FileParser(upPath + "/" +  systemFileName);
              fp.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.GENOTYPE,
                                                                  FileTypeDefinition.LIST));
              rows=fp.dataRows();
              titles = fp.columnTitles();
              errorFound=checkListTitles(titles,fatalErrors);
              if (titles[0].equals("IDENTITY"))
                 indId="IDENTITY";
              else
                 indId="ALIAS";

              delimeter=header.delimiter().charValue();

              if(!errorFound)
              {  //write to new file
                fileOut.write(header.objectTypeName()+
                              "/"+header.formatTypeName()+"/"+
                              header.version()+"/"+delimeter+"\n");

                for (int j=0;j<titles.length;j++)
                {
                   fileOut.write(titles[j]+delimeter);
                }
                fileOut.write("\n");

                Vector errorMessages=new Vector();
                Vector deviationMessages = new Vector();
                Vector warningMessages = new Vector();
                Vector databaseValues = new Vector();

              // check all rows in file
               for (int i = 0; i < fp.dataRows(); i++)
               {
                // get values
                ind = fp.getValue(indId, i).trim();
                marker = fp.getValue("MARKER", i).trim();
                allele1 = fp.getValue("ALLELE1", i).trim();
                allele2 = fp.getValue("ALLELE2", i).trim();

                raw1 = fp.getValue("RAW1", i).trim();
                raw2 = fp.getValue("RAW2", i).trim();
                ref = fp.getValue("REFERENCE", i).trim();
                comm = fp.getValue("COMMENT", i).trim();

                // check that values exist, have correct length etc
                checkValues(ind, marker,allele1,allele2,raw1,
                              raw2,ref,comm, fatalErrors);

                if (uploadMode.equals("CREATE"))
                {
                      checkListCreate(titles[0],ind, marker,allele1,allele2,raw1,
                              raw2,ref,comm,samplingUnitId,errorMessages,warningMessages,stmt);
                  //{
                  nrErrors+=errorMessages.size();
                  nrDeviations+=deviationMessages.size();
                  nrWarnings+=warningMessages.size();

                  // write row + all errors encountered to file
                  writeListErrors(fileOut,errorMessages,warningMessages,deviationMessages,databaseValues,ind,delimeter,marker,allele1,
                              allele2,raw1,raw2,ref,comm);
                  //}
                  errorMessages=new Vector();
                  deviationMessages=new Vector();
                  warningMessages=new Vector();
                  databaseValues = new Vector();

                }//if create

                else if (uploadMode.equals("UPDATE"))
                {
                  checkListUpdate(titles[0],ind, marker,allele1,allele2,raw1,
                              raw2,ref,comm,samplingUnitId,errorMessages,deviationMessages,warningMessages,
                              databaseValues,delimeter, stmt);

                  nrErrors+=errorMessages.size();
                  nrDeviations+=deviationMessages.size();
                  nrWarnings+=warningMessages.size();

                  // write all errors encountered to file

                  writeListErrors(fileOut,errorMessages,warningMessages,deviationMessages,databaseValues,ind,delimeter,marker,allele1,
                              allele2,raw1,raw2,ref,comm);
                  // clear old errors
                  errorMessages=new Vector();
                  deviationMessages=new Vector();
                  warningMessages=new Vector();
                  databaseValues = new Vector();

                }//if update

                else if (uploadMode.equals("CREATE_OR_UPDATE"))
                {


                  checkListCreateOrUpdate(titles[0],ind, marker,allele1,allele2,raw1,
                              raw2,ref,comm,samplingUnitId,errorMessages,warningMessages,deviationMessages,
                              databaseValues,delimeter, stmt);


                  nrErrors+=errorMessages.size();
                  nrDeviations+=deviationMessages.size();
                  nrWarnings+=warningMessages.size();

                  // write all errors encountered to file
                    writeListErrors(fileOut,errorMessages,warningMessages, deviationMessages,databaseValues,ind,delimeter,marker,allele1,
                              allele2,raw1,raw2,ref,comm);
                  // clear old errors
                  errorMessages=new Vector();
                  deviationMessages=new Vector();
                  warningMessages=new Vector();
                  databaseValues = new Vector();

                }//if createoruopdate
            }//for datarows
           }// not errorfound
          }//if LIST

          // If file is Matrix
            else if (!errorFound && header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.MATRIX))
            {

               mfp = new MatrixFileParser(upPath + "/" +  systemFileName);
               mfp.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.GENOTYPE,
                                                                    FileTypeDefinition.MATRIX));
              rows=mfp.dataRows();
              titles=mfp.columnTitles();
              errorFound=checkMatrixTitles(titles,fatalErrors,samplingUnitId,stmt);
              if (titles[0].equals("IDENTITY"))
                 indId="IDENTITY";
              else
                 indId="ALIAS";

              delimeter=header.delimiter().charValue();
              markers = new String[titles.length-1];
              for (int i = 0; i < markers.length; i++)
               markers[i] = titles[i+1];


              if(!errorFound)
              {  //write to new file
                fileOut.write(header.objectTypeName()+
                              "/"+header.formatTypeName()+"/"+
                              header.version()+"/"+delimeter+"\n");

                for (int j=0;j<titles.length;j++)
                {
                   fileOut.write(titles[j]+delimeter);
                }
                fileOut.write("\n");
                stmt = connection.createStatement();
                String alleles[];
                Vector errorMessages=new Vector();
                Vector warningMessages=new Vector();
                Vector databaseValues=new Vector();
                Vector deviationMessages = new Vector();
                Vector newAlleles=new Vector();
              // check all rows in file
               for (int row = 0; row < mfp.dataRows(); row++)
               {
                ind = mfp.getValue(indId, row)[0];
                // check the whole row
                for (int mNum = 0; mNum < markers.length; mNum++)
                {
                  String old_alleles[]=null;
                  marker = markers[mNum];
                  alleles = mfp.getValue(marker, row);
                  allele1 = alleles[0].trim();
                  allele2 = alleles[1].trim();
                  // store all alleles on this row
                  newAlleles.addElement(allele1);
                  newAlleles.addElement(allele2);
                // check that values exist, have correct length etc
                checkValues(ind, marker,allele1,allele2,null,
                              null,null,null, errorMessages);

                if (uploadMode.equals("CREATE"))
                {

                    checkMatrixCreate(titles[0],ind, marker,allele1,allele2,
                                      samplingUnitId,errorMessages,warningMessages,stmt);

                }

                else if(uploadMode.equals("UPDATE"))
                {

                  checkMatrixUpdate(titles[0],ind, marker,allele1,allele2,
                                              samplingUnitId,errorMessages,warningMessages,deviationMessages,databaseValues,stmt);
                }


                else if(uploadMode.equals("CREATE_OR_UPDATE"))
                {

                  checkMatrixCreateOrUpdate(titles[0],ind, marker,allele1,allele2,
                                              samplingUnitId,errorMessages,warningMessages,deviationMessages,databaseValues,stmt);
                 }// if create or update
               }//for markers


                 nrErrors+=errorMessages.size();
                 nrDeviations+=deviationMessages.size();
                 nrWarnings+=warningMessages.size();

                writeMatrixErrors(fileOut,errorMessages,warningMessages,deviationMessages,databaseValues,
                                    newAlleles,ind,delimeter,marker,allele1,allele2);

               newAlleles= new Vector();
               databaseValues = new Vector();
               errorMessages=new Vector();
               warningMessages=new Vector();
               deviationMessages=new Vector();
            }// for rows
        }// if matrix
       }// if no error found
      }// IF file has more elements (filename !=null);
       if(stmt!=null)
       {
        stmt.close();
       }
    }//try
    catch (Exception e)
      {
         // Flag for error and set the errMessage if it has not been set
         isOk = false;
         e.printStackTrace(System.err);
         errorFound=true;

         if (errorMessage == null)
         {
            errorMessage = e.getMessage();
            fatalErrors.addElement(e.getMessage());
         }
      }

      if(fileOut!=null)
      {
        fileOut.flush();
        fileOut.close();
      }

      if(!errorFound)
      {
        // store values for resultpage
        session.putValue("GENO_nrErrors",new Integer(nrErrors).toString());
        session.putValue("GENO_nrDeviations",new Integer(nrDeviations).toString());
        session.putValue("GENO_nrWarnings",new Integer(nrWarnings).toString());
        session.putValue("GENO_FILE",systemFileName);
        session.putValue("GENO_SUID",new Integer(samplingUnitId).toString());
        session.putValue("GENO_TYPE",uploadMode);
        session.putValue("GENO_LEVEL",new Integer(level).toString());
        session.putValue("GENO_ROWS",new Integer(rows).toString());
        response.sendRedirect(getServletPath("viewGeno/compare"));
       }
       else // Fatal error, we do not let this file pass
       {
        if(fatalErrors.size()==0)
        {
          errorMessage="Unknown";
        }
        else
        {
          errorMessage=(String)fatalErrors.elementAt(0);
        }
        commitOrRollback(connection, request, response,
                           "Genotypes.Import.CreateOrCreateUpdate.Send",
                           errorMessage, "viewGeno/impFile", false);
       }
      return isOk;
}

  /**
   * @param fileOut
   * @param deviationMessages
   * @param oldAlleles
   * @param ind
   * @param delim  */  
private void writeMatrixDeviations(FileWriter fileOut,Vector deviationMessages,
            Vector oldAlleles,String ind,char delim)
{
  try
  {
      if(deviationMessages.size()>0)
      {
         for(int k=0;k<deviationMessages.size();k++)
         {
            fileOut.write("#"+ (String)deviationMessages.elementAt(k)+"\n");
         }
         // write old values
         fileOut.write("#"+ind);
         for (int k=0;k<oldAlleles.size();k++)
         {
           fileOut.write(delim+(String)oldAlleles.elementAt(k));
         }
        fileOut.write("\n");
      }
  }//try
  catch (Exception e)
  {
    e.printStackTrace(System.err);
  }

}

/**
 * @param fileOut
 * @param original_values
 * @param ind
 * @param delimeter
 * @param marker
 * @param allele1
 * @param allele2
 * @param raw1
 * @param raw2
 * @param ref
 * @param comm  
 */
private void writeDeviations(FileWriter fileOut,String original_values,String ind,
            char delimeter,String marker, String allele1,
            String allele2,String raw1,String raw2,String ref,String comm)
{
  try
  {
     if(!original_values.equalsIgnoreCase("SAME"))
     {
        fileOut.write("#--------------------------------------------------\n");
        fileOut.write("#Genotype differs from database, se below (old top, new bottom)\n");
        fileOut.write("#"+ original_values +"\n");

        // write new string
        fileOut.write(ind+delimeter+marker+delimeter+allele1+delimeter+
                      allele2+delimeter+raw1+delimeter+raw2+delimeter+
                      ref+delimeter+comm+"\n");
        fileOut.write("#--------------------------------------------------\n");
     }
   }//try
  catch (Exception e)
  {
    e.printStackTrace(System.err);
  }
}


/**
 * @param fileOut
 * @param errorMessages
 * @param warningMessages
 * @param deviationMessages
 * @param databaseValues
 * @param newAlleles
 * @param ind
 * @param delim
 * @param marker
 * @param allele1
 * @param allele2  
 */
private void writeMatrixErrors(FileWriter fileOut,Vector errorMessages,Vector warningMessages,
            Vector deviationMessages,Vector databaseValues, Vector newAlleles, String ind,
            char delim,String marker, String allele1,
            String allele2)
{
  try
  {
      // if row contains comments
      if(errorMessages.size()>0 || deviationMessages.size()>0 || warningMessages.size()>0)
      {
        fileOut.write("#--------------------------------------------------------\n");
      }
      if(errorMessages.size()>0)
      {
         for(int i=0;i<errorMessages.size();i++)
         {
            fileOut.write("#"+ (String)errorMessages.elementAt(i)+"\n");
         }
      }

      if(warningMessages.size()>0)
      {
         for(int i=0;i<warningMessages.size();i++)
         {
            fileOut.write("#"+ (String)warningMessages.elementAt(i)+"\n");
         }
      }

      if(deviationMessages.size()>0)
      {
         for(int i=0;i<deviationMessages.size();i++)
         {
            fileOut.write("#"+ (String)deviationMessages.elementAt(i)+"\n");
         }
         //write database values
        fileOut.write("#"+ind);
        for (int i=0;i<databaseValues.size();i++)
        {
          fileOut.write(delim+ (String)databaseValues.elementAt(i));
        }
        fileOut.write("\n");
      }
      // write row from file:
      if(errorMessages.size() > 0)
      {
        fileOut.write("#");
      }
      fileOut.write(ind);
      for (int i=0;i<newAlleles.size();i++)
      {
        fileOut.write(delim+(String)newAlleles.elementAt(i));
      }
      fileOut.write("\n");
      if(errorMessages.size()>0 || deviationMessages.size()>0 || warningMessages.size()>0)
      {
        fileOut.write("#--------------------------------------------------------\n");
      }

  }//try
  catch (Exception e)
  {
    e.printStackTrace(System.err);
  }
}


/**
 * @param fileOut
 * @param errorMessages
 * @param ind
 * @param delimeter
 * @param marker
 * @param allele1
 * @param allele2
 * @param raw1
 * @param raw2
 * @param ref
 * @param comm  */
/*
private void writeErrors(FileWriter fileOut,Vector errorMessages,String ind,
            char delimeter,String marker, String allele1,
            String allele2,String raw1,String raw2,String ref,String comm)
{
  try
  {
      if(errorMessages.size()>0)
      {
         fileOut.write("#--------------------------------------------------\n");
         for(int k=0;k<errorMessages.size();k++)
         {
            fileOut.write("#"+ (String)errorMessages.elementAt(k)+"\n");
         }
         fileOut.write("#"+ind+delimeter+marker+delimeter+allele1+
                        delimeter+allele2+delimeter+raw1+delimeter+
                        raw2+delimeter+ref+delimeter+comm+"\n");
         fileOut.write("#--------------------------------------------------\n");
      }

  }//try
  catch (Exception e)
  {
    e.printStackTrace(System.err);
  }

}
 */



/**
 * @param fileOut
 * @param errorMessages
 * @param warningMessages
 * @param deviationMessages
 * @param databaseValues
 * @param ind
 * @param delimeter
 * @param marker
 * @param allele1
 * @param allele2
 * @param raw1
 * @param raw2
 * @param ref
 * @param comm  
 */
private void writeListErrors(FileWriter fileOut,Vector errorMessages,Vector warningMessages,
                            Vector deviationMessages, Vector databaseValues,
                            String ind,char delimeter,
                            String marker, String allele1,String allele2,
                            String raw1,String raw2,String ref,String comm)
{
  try
  {

    if(errorMessages.size()>0 || deviationMessages.size()>0 || warningMessages.size()>0)
    {
      fileOut.write("#--------------------------------------------------\n");
    }
    if(errorMessages.size()>0)
    {
         for(int i=0;i<errorMessages.size();i++)
         {
            fileOut.write("#"+ (String)errorMessages.elementAt(i)+"\n");
         }
    }
    if(warningMessages.size()>0)
    {
        for (int i=0;i<warningMessages.size();i++)
        {
          fileOut.write("#"+ (String)warningMessages.elementAt(i)+"\n");
        }
    }

    if(deviationMessages.size()>0)
    {
        for (int i=0;i<deviationMessages.size();i++)
        {
          fileOut.write("#"+ (String)deviationMessages.elementAt(i)+"\n");
        }
        // write old values
        fileOut.write("#"+databaseValues.elementAt(0)+"\n");
    }
    // if there are errors, the string is "Outcommented"
    if(errorMessages.size()>0)
    {
          fileOut.write("#");
    }

    // write original string
    fileOut.write(ind+delimeter+marker+delimeter+allele1+
                  delimeter+allele2+delimeter+raw1+delimeter+
                  raw2+delimeter+ref+delimeter+comm+"\n");

    if(errorMessages.size()>0 || deviationMessages.size()>0 || warningMessages.size()>0)
    {
      fileOut.write("#--------------------------------------------------\n");
    }

  }//try
  catch (Exception e)
  {
    e.printStackTrace(System.err);
  }

}


/**
 * Checks if the genotype exists, that alleles exists etc.
 * Makes certain the genotype can be updated
 */
private void checkListCreateOrUpdate(  String id_or_alias,
                          String ind,
                          String marker,
                          String allele1,
                          String allele2,
                          String raw1,
                          String raw2,
                          String reference,
                          String comment,
                          int suid,
                          Vector errorMessages,
                          Vector warningMessages,
                          Vector deviationMessages,
                          Vector databaseValues,
                          char delim,
                         Statement stmt)
{
  ResultSet rset;
  String identity=null;
  boolean match=false;

  try
  {
  String mname=null;
  String a1=null;
  String a2=null;
  String r1=null;
  String r2=null;
  String ref=null;
  String comm =null;


      //First if no IDENTITY was sent, we need to get it through alias!
      if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
         && !ind.trim().equalsIgnoreCase(""))
      {
        rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                              " ALIAS=" + "'"+ind +"'"+
                              " AND SUID=" +"'"+suid+"'");

        if(rset.next())
          identity=rset.getString("IDENTITY");
        else
          identity="-1";
      }
      else
        identity=ind;

      //compare to database, does genotype exist? update or create?
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                              "MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY=" + "'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
      if (rset.next())
      {
          // Genotype exists -- check for update
          // do the alleles exist for these markers??
          //Only check if the new desired value is not null
           if(allele1 != null && !allele1.trim().equalsIgnoreCase(""))
          {
            rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele1+"'" +
                                " AND SUID=" +"'"+suid+"'");
            if (!rset.next())
            {
              // the allele does not exist
              String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker +". It will be created if genotype is imported!";
              warningMessages.addElement(Message);
            }
          }
          if(allele2 != null && !allele2.trim().equalsIgnoreCase(""))
          {

            // do the alleles exist for these markers??
            rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                  " MNAME=" + "'"+marker+"'"+
                                  " AND NAME=" +"'"+allele2+"'"+
                                  " AND SUID=" +"'"+suid+"'");
            if (!rset.next())
            {
            // the allele does not exist
            String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker;
            warningMessages.addElement(Message);
            }
          }

          // compare to database
          rset = stmt.executeQuery("SELECT IDENTITY,MNAME, A1NAME, A2NAME,"+
                              " RAW1, RAW2,REFERENCE,COMM FROM gdbadm.V_GENOTYPES_3 WHERE "+
                              " MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY="+"'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
          if (rset.next())
          {
          // get database values
            mname=rset.getString("MNAME");
            a1=rset.getString("A1NAME");
            a2=rset.getString("A2NAME");
            r1=rset.getString("RAW1");
            r2=rset.getString("RAW1");
            ref=rset.getString("REFERENCE");
            comm =rset.getString("COMM");
            //System.err.println("mname1="+mname+"\n");
            //if any of these are null, we need to set nothing.
            if(a1==null)
            a1="";
            if(a2==null)
             a2="";
            if(r1==null)
            r1="";
            if(r2==null)
             r2="";
            if(ref==null)
            ref="";
            if(comm==null)
            comm="";
          // compare allele values
            if(a1.equalsIgnoreCase(allele1) && a2.equalsIgnoreCase(allele2))
            {
              match = true;
            }
            else if(a1.equalsIgnoreCase(allele2) && a2.equalsIgnoreCase(allele1))
            {
              match = true;
            }
            if(!match)
            {
            // genotype differs
            deviationMessages.addElement("#Genotype differs from database, se below (old top, new bottom)");

            //System.err.println("mname="+mname+"\n");
            databaseValues.addElement(ind+delim+mname+delim+a1+delim+a2+delim+r1+delim+r2+delim
                        + ref+delim+comm);

            }// match
        }// rset
      }
      else // genotype does not exist, create
      {

        // Does the individual exist?
        rset = stmt.executeQuery("SELECT IID FROM gdbadm.INDIVIDUALS WHERE " +
                                  " IDENTITY=" + "'"+identity +"'"+
                                  " AND SUID=" +"'"+suid+"'");
        if (!rset.next())
        {
          // the Individual does not exist
          String Message =" The Individual with "+ id_or_alias+" "+ind+" does not exist.";
          errorMessages.addElement(Message);
        }
        // does marker exist?
        rset = stmt.executeQuery("SELECT MID FROM gdbadm.MARKERS WHERE " +
                                  " NAME=" + "'"+marker+"'"+
                                  " AND SUID=" +"'"+suid+"'");

        if (!rset.next())
        {
          // the marker does not exist
          String Message =" Marker "+marker+" does not exist";
          errorMessages.addElement(Message);
        }
        else
        {
         if(allele1!=null && !allele1.equals(""))
         {
            // do the alleles exist for these markers??
            rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                  " MNAME=" + "'"+marker+"'"+
                                  " AND NAME=" +"'"+allele1+"'" +
                                  " AND SUID=" +"'"+suid+"'");
            if (!rset.next())
            {
              // the allele does not exist
              String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker +". It will be created if genotype is created!";

              warningMessages.addElement(Message);
            }
          }

         if(allele2!=null && !allele2.equals(""))
         {
           // do the alleles exist for these markers??
            rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                   " MNAME=" + "'"+marker+"'"+
                                   " AND NAME=" +"'"+allele2+"'"+
                                   " AND SUID=" +"'"+suid+"'");

          if (!rset.next())
          {
            // the allele does not exist
            String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is created!";


            warningMessages.addElement(Message);
          }
        }
      }
  }
    rset.close();
  }// try
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }
}

/**
* Compares all values to whats already in the database
* returns the number of errors found.
**/
private int checkCreateOrUpdate( String id_or_alias,
                          String ind,
                          String marker,
                          String allele1,
                          String allele2,
                          String raw1,
                          String raw2,
                          String ref,
                          String comm,
                          int suid,
                          Vector errorMessages,
                         Statement stmt)
{
  ResultSet rset;
  int nrErrors=0;
  String identity=null;
  try
  {
   //First if no IDENTITY was sent, we need to get it through alias!
   if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
       && !ind.trim().equalsIgnoreCase(""))
   {
   /*
    System.err.println("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                            " ALIAS=" + "'"+ind +"'"+
                            " AND SUID=" +"'"+suid+"'");
*/
    rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                            " ALIAS=" + "'"+ind +"'"+
                            " AND SUID=" +"'"+suid+"'");

      if(rset.next())
      {
        identity=rset.getString("IDENTITY");
      }
      else
      {
        identity="-1";
      }
   }
   else
   {
      identity=ind;
   }

    //compare to database, is this unique? Shall it be created or updated?
    rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                            "MNAME=" +"'"+ marker+"'" +
                            " AND IDENTITY=" + "'"+identity +"'"+
                            " AND SUID=" +"'"+suid+"'");

    if (rset.next())
    {
      // the genotype exists - we will try to update it!
      // do the alleles exist for these markers??
        rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele1+"'" +
                                " AND SUID=" +"'"+suid+"'");
        if (!rset.next())
        {
          // the allele does not exist
          String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker+". It will be created if genotype is imported!";
          errorMessages.addElement(Message);
          nrErrors ++;
        }

        // do the alleles exist for these markers??
        rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele2+"'"+
                                " AND SUID=" +"'"+suid+"'");
        if (!rset.next())
        {
          // the marker does not exist
          String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is imported!";
          errorMessages.addElement(Message);
          nrErrors ++;
        }

      }
      else
      {
        //Genotype does not exist, we must make certaion it can be created
        // Does the individual exist?
        rset = stmt.executeQuery("SELECT IID FROM gdbadm.INDIVIDUALS WHERE " +
                                " IDENTITY=" + "'"+identity +"'"+
                                " AND SUID=" +"'"+suid+"'");
        if (!rset.next())
        {
          // the Individual does not exist
          String Message =" The Individual with "+ id_or_alias+" "+ind+" does not exist.";
          errorMessages.addElement(Message);
          nrErrors ++;
        }
        // does marker exist?
        rset = stmt.executeQuery("SELECT MID FROM gdbadm.MARKERS WHERE " +
                                  " NAME=" + "'"+marker+"'"+
                                  " AND SUID=" +"'"+suid+"'");

        if (!rset.next())
        {
          // the marker does not exist
          String Message =" Marker "+marker+" does not exist";
          errorMessages.addElement(Message);
          nrErrors ++;
        }
        else
        {
          // do the alleles exist for these markers??
          rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                    " MNAME=" + "'"+marker+"'"+
                                    " AND NAME=" +"'"+allele1+"'" +
                                    " AND SUID=" +"'"+suid+"'");

        if (!rset.next())
        {
          // the allele does not exist
          String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker+". It will be created if genotype is imported!";;
          errorMessages.addElement(Message);
          nrErrors ++;
        }

        // do the alleles exist for these markers??
        rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                 " MNAME=" + "'"+marker+"'"+
                                 " AND NAME=" +"'"+allele2+"'"+
                                 " AND SUID=" +"'"+suid+"'");

        if (!rset.next())
        {
          // the marker does not exist
          String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is imported!";
          errorMessages.addElement(Message);
          nrErrors ++;
        }
      }

      rset.close();
    }
  }
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }
        return nrErrors;

}

/**
 * @param id_or_alias
 * @param ind
 * @param marker
 * @param allele1
 * @param allele2
 * @param suid
 * @param errorMessages
 * @param warningMessages
 * @param deviationMessages
 * @param databaseValues
 * @param stmt  */
private void checkMatrixUpdate(  String id_or_alias,String ind,String marker,
                              String allele1,String allele2,int suid,
                              Vector errorMessages,Vector warningMessages,Vector deviationMessages,
                              Vector databaseValues,Statement stmt)
{
  ResultSet rset;
  int nrErrors=0;
  String identity=null;
  boolean match=false;
  try
  {
      //First if no IDENTITY was sent, we need to get it through alias!
      if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
         && !ind.trim().equalsIgnoreCase(""))
      {
        rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                              " ALIAS=" + "'"+ind +"'"+
                              " AND SUID=" +"'"+suid+"'");

        if(rset.next())
          identity=rset.getString("IDENTITY");
        else
          identity="-1";
      }
      else
        identity=ind;
      //compare to database, does genotype exist? can it be updated..
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                              "MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY=" + "'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");



      if (rset.next())
      {// Genotype exists
        // do the alleles exist for these markers??
        if(allele1 != null && !allele1.trim().equalsIgnoreCase(""))
        {

          rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                  " MNAME=" + "'"+marker+"'"+
                                  " AND NAME=" +"'"+allele1+"'" +
                                  " AND SUID=" +"'"+suid+"'");
          if (!rset.next())
          {
            // the allele does not exist
            String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker+". It will be created if genotype is imported!";
            errorMessages.addElement(Message);
            nrErrors ++;
          }
        }


      if(allele2 != null && !allele2.trim().equalsIgnoreCase(""))
      {
        // do the alleles exist for these markers??
        rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele2+"'"+
                                " AND SUID=" +"'"+suid+"'");
        if (!rset.next())
        {
          // the marker does not exist
          String Message =" Warning Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is imported!";
          errorMessages.addElement(Message);
          nrErrors ++;
        }
      }


        rset = stmt.executeQuery("SELECT IDENTITY,MNAME, A1NAME, A2NAME"+
                              " FROM gdbadm.V_GENOTYPES_3 WHERE "+
                              " MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY="+"'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");

        if (rset.next())
        {
          // get database values
          String mname=rset.getString("MNAME");
          String a1=rset.getString("A1NAME");
          String a2=rset.getString("A2NAME");
          //if any of these are null, we need to set nothing.
          if(a1==null)
            a1="";
          if(a2==null)
            a2="";

          // compare allele values
          if(a1.equalsIgnoreCase(allele1) && a2.equalsIgnoreCase(allele2))
          {
            match = true;
          }
          else if(a1.equalsIgnoreCase(allele2) && a2.equalsIgnoreCase(allele1))
          {
            match = true;
          }
          if(!match)
          {
            deviationMessages.addElement(marker+": Genotype differs from database.");
          }
          databaseValues.addElement(a1);
          databaseValues.addElement(a2);
        }
      }
      else // genotype does not exist
      {
            errorMessages.addElement(marker+": The Genotype does not exist in database.");
      }
    rset.close();
  }// try
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }
}

/**
 * This method is not used???
 *
 * Checks if the genotype exists, that alleles exists etc.
 * Makes certain the genotype can be updated
 */
private int checkUpdate(  String id_or_alias,
                          String ind,
                          String marker,
                          String allele1,
                          String allele2,
                          String raw1,
                          String raw2,
                          String ref,
                          String comm,
                          int suid,
                          Vector errorMessages,
                         Statement stmt)
{
  ResultSet rset;
  int nrErrors=0;
  String identity=null;
  try
  {
      //First if no IDENTITY was sent, we need to get it through alias!
      if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
         && !ind.trim().equalsIgnoreCase(""))
      {
        rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                              " ALIAS=" + "'"+ind +"'"+
                              " AND SUID=" +"'"+suid+"'");

        if(rset.next())
          identity=rset.getString("IDENTITY");
        else
          identity="-1";
      }
      else
        identity=ind;

      //compare to database, does genotype exist? can it be updated..
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                              "MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY=" + "'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
      if (rset.next())
      {// Genotype exists
        // do the alleles exist for these markers??
        rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele1+"'" +
                                " AND SUID=" +"'"+suid+"'");
        if (!rset.next())
        {
          // the allele does not exist
          String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker+". It will be created if genotype is imported!";
          errorMessages.addElement(Message);
          nrErrors ++;
        }

        // do the alleles exist for these markers??
        rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele2+"'"+
                                " AND SUID=" +"'"+suid+"'");
        if (!rset.next())
        {
          // the marker does not exist
          String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is imported!";
          errorMessages.addElement(Message);
          nrErrors ++;
        }
      }
      else // genotype does not exist
      {
            String Message ="The Genotype does not exist, cannot be updated.";
            errorMessages.addElement(Message);
            nrErrors ++;
      }
    rset.close();
  }// try
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }
  return nrErrors;
}

/**
 * Checks if the genotype exists, that alleles exists etc.
 * Makes certain the genotype can be updated
 */
private void checkListUpdate(  String id_or_alias,
                          String ind,
                          String marker,
                          String allele1,
                          String allele2,
                          String raw1,
                          String raw2,
                          String reference,
                          String comment,
                          int suid,
                          Vector errorMessages,
                          Vector deviationMessages,
                          Vector warningMessages,
                          Vector databaseValues,
                          char delim,
                         Statement stmt)
{
  ResultSet rset;
  String identity=null;
  boolean match=false;

  try
  {
  String mname=null;
  String a1=null;
  String a2=null;
  String r1=null;
  String r2=null;
  String ref=null;
  String comm =null;


      //First if no IDENTITY was sent, we need to get it through alias!
      if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
         && !ind.trim().equalsIgnoreCase(""))
      {
        rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                              " ALIAS=" + "'"+ind +"'"+
                              " AND SUID=" +"'"+suid+"'");

        if(rset.next())
          identity=rset.getString("IDENTITY");
        else
          identity="-1";
      }
      else
        identity=ind;

      //compare to database, does genotype exist? can it be updated..
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                              "MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY=" + "'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
      if (rset.next())
      {// Genotype exists
        // do the alleles exist for these markers??
        // only check if allele not null
        if(allele1 != null && !allele1.trim().equalsIgnoreCase(""))
        {

          rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                  " MNAME=" + "'"+marker+"'"+
                                  " AND NAME=" +"'"+allele1+"'" +
                                  " AND SUID=" +"'"+suid+"'");
          if (!rset.next())
          {
            // the allele does not exist
            String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker+". It will be created if genotype is imported!";
            warningMessages.addElement(Message);
          }
        }

        if(allele2 != null && !allele2.trim().equalsIgnoreCase(""))
        {

          // do the alleles exist for these markers??
          rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                  " MNAME=" + "'"+marker+"'"+
                                  " AND NAME=" +"'"+allele2+"'"+
                                  " AND SUID=" +"'"+suid+"'");
          if (!rset.next())
          {
           // the marker does not exist
           String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is imported!";
           warningMessages.addElement(Message);
          }
        }
        //----------------
        // compare to database
        rset = stmt.executeQuery("SELECT IDENTITY,MNAME, A1NAME, A2NAME,"+
                              " RAW1, RAW2,REFERENCE,COMM FROM gdbadm.V_GENOTYPES_3 WHERE "+
                              " MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY="+"'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
        if (rset.next())
        {
          // get database values
          mname=rset.getString("MNAME");
          a1=rset.getString("A1NAME");
          a2=rset.getString("A2NAME");
          r1=rset.getString("RAW1");
          r2=rset.getString("RAW1");
          ref=rset.getString("REFERENCE");
          comm =rset.getString("COMM");
          //System.err.println("mname1="+mname+"\n");
          //if any of these are null, we need to set nothing.
          if(a1==null)
           a1="";
          if(a2==null)
             a2="";
          if(r1==null)
           r1="";
          if(r2==null)
            r2="";
          if(ref==null)
            ref="";
          if(comm==null)
            comm="";
          // compare allele values
          if(a1.equalsIgnoreCase(allele1) && a2.equalsIgnoreCase(allele2))
          {
            match = true;
          }
          else if(a1.equalsIgnoreCase(allele2) && a2.equalsIgnoreCase(allele1))
          {
            match = true;
          }
          if(!match)
          {
            // genotype differs
            deviationMessages.addElement("#Genotype differs from database, se below (old top, new bottom)");

             //System.err.println("mname="+mname+"\n");
            databaseValues.addElement(ind+delim+mname+delim+a1+delim+a2+delim+r1+delim+r2+delim
                        + ref+delim+comm);

          }
        }
      }
      else // genotype does not exist
      {
            String Message ="The Genotype does not exist, cannot be updated.";
            errorMessages.addElement(Message);
      }
    rset.close();
  }// try
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }
}

/**
 * Compares all values to whats already in the database
 * returns the number of errors found.
 **/
private void checkMatrixCreateOrUpdate( String id_or_alias,String ind,
                                      String marker,String allele1,String allele2,
                                      int suid,Vector errorMessages,Vector warningMessages,
                                      Vector deviationMessages, Vector databaseValues,Statement stmt)
{
  ResultSet rset;
  int nrErrors=0;
  String identity=null;
  int nrDeviations=0;
  boolean match=false;
 try
 {
   //First if no IDENTITY was sent, we need to get it through alias!
   if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
       && !ind.trim().equalsIgnoreCase(""))
   {

    rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                            " ALIAS=" + "'"+ind +"'"+
                            " AND SUID=" +"'"+suid+"'");

      if(rset.next())
      {
        identity=rset.getString("IDENTITY");
      }
      else
      {
        identity="-1";
      }
   }
   else
   {
      identity=ind;
   }

  //compare to database, should this be created?
  rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                            "MNAME=" +"'"+ marker+"'" +
                            " AND IDENTITY=" + "'"+identity +"'"+
                            " AND SUID=" +"'"+suid+"'");


  if (rset.next())
  {
      // Genotype exists
        // do the alleles exist for these markers??
     if(allele1 != null && !allele1.trim().equalsIgnoreCase(""))
     {

        rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele1+"'" +
                                " AND SUID=" +"'"+suid+"'");
        if (!rset.next())
        {
          // the allele does not exist
          String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker+". It will be created if genotype is imported!";
          warningMessages.addElement(Message);
          //nrErrors ++;
        }
     }
     if(allele2 != null && !allele2.trim().equalsIgnoreCase(""))
     {

        // do the alleles exist for these markers??
        rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele2+"'"+
                                " AND SUID=" +"'"+suid+"'");
        if (!rset.next())
        {
          // the marker does not exist
          String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is imported!";
          warningMessages.addElement(Message);
          //nrErrors ++;
        }
     }


      rset = stmt.executeQuery("SELECT IDENTITY,MNAME, A1NAME, A2NAME"+
                              " FROM gdbadm.V_GENOTYPES_3 WHERE "+
                              " MNAME=" +"'"+ marker+"'" +
                              " AND IDENTITY="+"'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");

        rset.next();
        // get database values
        String mname=rset.getString("MNAME");
        String a1=rset.getString("A1NAME");
        String a2=rset.getString("A2NAME");
        //if any of these are null, we need to set nothing.
        if(a1==null)
          a1="";
        if(a2==null)
          a2="";

        // compare allele values
        if(a1.equalsIgnoreCase(allele1) && a2.equalsIgnoreCase(allele2))
        {
          match = true;
        }
        else if(a1.equalsIgnoreCase(allele2) && a2.equalsIgnoreCase(allele1))
        {
          match = true;
        }
        if(!match)
        {

          deviationMessages.addElement(marker+": Genotype differs from database.");
          nrDeviations++;

        }
        databaseValues.addElement(a1);
        databaseValues.addElement(a2);
    }
    else
    {
      // the gontype does not exist-must be created
      // Does the individual exist?
      rset = stmt.executeQuery("SELECT IID FROM gdbadm.INDIVIDUALS WHERE " +
                              " IDENTITY=" + "'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
      if (!rset.next())
      {
        // the Individual does not exist
        String Message =" The Individual with "+ id_or_alias+" "+ind+" does not exist.";
        errorMessages.addElement(Message);
        nrErrors ++;
      }
      // does marker exist?
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.MARKERS WHERE " +
                                " NAME=" + "'"+marker+"'"+
                                " AND SUID=" +"'"+suid+"'");

     if (!rset.next())
     {
       // the marker does not exist
        String Message =" Marker "+marker+" does not exist";
        errorMessages.addElement(Message);
        nrErrors ++;
      }
      else
      {
        if(allele1 !=null && !allele1.equals(""))
        {
          // do the alleles exist for these markers??
          rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                  " MNAME=" + "'"+marker+"'"+
                                  " AND NAME=" +"'"+allele1+"'" +
                                  " AND SUID=" +"'"+suid+"'");

          if (!rset.next())
          {
            // the allele does not exist
            String Message = " Warning!"+marker+": Allele "+allele1+" does not exist. Will be created if genotype is created!";
            warningMessages.addElement(Message);
            //nrErrors ++;
          }
        }

        if(allele2!=null && !allele2.equals(""))
        {
          // do the alleles exist for these markers??
          rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                 " MNAME=" + "'"+marker+"'"+
                                 " AND NAME=" +"'"+allele2+"'"+
                                 " AND SUID=" +"'"+suid+"'");

          if (!rset.next())
          {
            // the allele does not exist
            String Message = " Warning!"+marker+": Allele "+allele2+" does not exist. Will be created if genotype is imported created!";
           warningMessages.addElement(Message);
         //nrErrors ++;
        }
      }
     }
    }
        rset.close();
  }
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }
     //   int []errAndDev = new int[2];
     //   errAndDev[0]=nrErrors;
     //   errAndDev[1]=nrDeviations;
        //return nrErrors;
      //  return errAndDev;

}

/**
* Compares all values to whats already in the database
* returns the number of errors found.
**/
private void checkMatrixCreate( String id_or_alias,
                          String ind,
                          String marker,
                          String allele1,
                          String allele2,
                          int suid,
                          Vector errorMessages,
                          Vector warningMessages,
                          Statement stmt)
{

  ResultSet rset;
  int nrErrors=0;
  String identity=null;
 try
 {
   //First if no IDENTITY was sent, we need to get it through alias!
   if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
       && !ind.trim().equalsIgnoreCase(""))
   {

    rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                            " ALIAS=" + "'"+ind +"'"+
                            " AND SUID=" +"'"+suid+"'");

      if(rset.next())
      {
        identity=rset.getString("IDENTITY");
      }
      else
      {
        identity="-1";
      }
   }
   else
   {
      identity=ind;
   }

  //compare to database, is this unique? (can it be inserted?)
  rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                            "MNAME=" +"'"+ marker+"'" +
                            " AND IDENTITY=" + "'"+identity +"'"+
                            " AND SUID=" +"'"+suid+"'");

  if (rset.next())
  {
    // the genotype exists

    String Message = marker +": The Genotype exists, cannot be created.";
    errorMessages.addElement(Message);
    nrErrors ++;
    }

    // Does the individual exist?
    rset = stmt.executeQuery("SELECT IID FROM gdbadm.INDIVIDUALS WHERE " +
                              " IDENTITY=" + "'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
    if (!rset.next())
    {
      // the Individual does not exist
      String Message =" The Individual with "+ id_or_alias+" "+ind+" does not exist.";
      errorMessages.addElement(Message);
      nrErrors ++;
    }
    // does marker exist?
    rset = stmt.executeQuery("SELECT MID FROM gdbadm.MARKERS WHERE " +
                              " NAME=" + "'"+marker+"'"+
                              " AND SUID=" +"'"+suid+"'");

    if (!rset.next())
    {
      // the marker does not exist
      String Message =" Marker "+marker+" does not exist";
      errorMessages.addElement(Message);
      nrErrors ++;
    }
    else
    {
      // do the alleles exist for these markers??
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele1+"'" +
                                " AND SUID=" +"'"+suid+"'");

      if (!rset.next())
      {
        // the allele does not exist
        String Message = " Warning!"+ marker+": Allele "+allele1+" does not exist. Will be created if genotype is imported!";
        warningMessages.addElement(Message);
        //nrErrors ++;
      }

      // do the alleles exist for these markers??
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                               " MNAME=" + "'"+marker+"'"+
                               " AND NAME=" +"'"+allele2+"'"+
                               " AND SUID=" +"'"+suid+"'");

      if (!rset.next())
      {
        // the marker does not exist
        String Message = " Warning!"+marker+": Allele "+allele2+" does not exist. Will be created if genotype is imported!";
        warningMessages.addElement(Message);
        //nrErrors ++;
      }
      rset.close();
    }
  }
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }
//        return nrErrors;

}




/**
* Compares all values to whats already in the database
* returns the number of errors found.
**/
private void checkListCreate( String id_or_alias,
                          String ind,
                          String marker,
                          String allele1,
                          String allele2,
                          String raw1,
                          String raw2,
                          String ref,
                          String comm,
                          int suid,
                          Vector errorMessages,
                          Vector warningMessages,
                         Statement stmt)
{

  ResultSet rset;
  int nrErrors=0;
  String identity=null;
 try
 {
   //First if no IDENTITY was sent, we need to get it through alias!
   if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
       && !ind.trim().equalsIgnoreCase(""))
   {

    rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                            " ALIAS=" + "'"+ind +"'"+
                            " AND SUID=" +"'"+suid+"'");

      if(rset.next())
      {
        identity=rset.getString("IDENTITY");
      }
      else
      {
        identity="-1";
      }
   }
   else
   {
      identity=ind;
   }

  //compare to database, is this unique? (can it be inserted?)
  rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                            "MNAME=" +"'"+ marker+"'" +
                            " AND IDENTITY=" + "'"+identity +"'"+
                            " AND SUID=" +"'"+suid+"'");


  if (rset.next())
  {
     // the genotype exists
    String Message =" The Genotype already exists, cannot be created.";
    errorMessages.addElement(Message);
    nrErrors ++;
    }

    // Does the individual exist?
    rset = stmt.executeQuery("SELECT IID FROM gdbadm.INDIVIDUALS WHERE " +
                              " IDENTITY=" + "'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
    if (!rset.next())
    {
      // the Individual does not exist
      String Message =" The Individual with "+ id_or_alias+" "+ind+" does not exist.";
      errorMessages.addElement(Message);
    }
    // does marker exist?
    rset = stmt.executeQuery("SELECT MID FROM gdbadm.MARKERS WHERE " +
                              " NAME=" + "'"+marker+"'"+
                              " AND SUID=" +"'"+suid+"'");

    if (!rset.next())
    {
      // the marker does not exist
      String Message =" Marker "+marker+" does not exist";
      errorMessages.addElement(Message);
    }
    else
    {
      // do the alleles exist for these markers??
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele1+"'" +
                                " AND SUID=" +"'"+suid+"'");
      if (!rset.next())
      {
        // the allele does not exist
        String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker+". It will be created if genotype is imported!";
        warningMessages.addElement(Message);
      }

      // do the alleles exist for these markers??
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                               " MNAME=" + "'"+marker+"'"+
                               " AND NAME=" +"'"+allele2+"'"+
                               " AND SUID=" +"'"+suid+"'");

      if (!rset.next())
      {
        // the marker does not exist
        String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is imported!";
        warningMessages.addElement(Message);
      }
      rset.close();
    }
  }
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }

}

/**
 * This is not used????
 *
 * Compares all values to whats already in the database
 * returns the number of errors found.
 */
private int checkCreate( String id_or_alias,
                          String ind,
                          String marker,
                          String allele1,
                          String allele2,
                          String raw1,
                          String raw2,
                          String ref,
                          String comm,
                          int suid,
                          Vector errorMessages,
                         Statement stmt)
{

  ResultSet rset;
  int nrErrors=0;
  String identity=null;
 try
 {
   //First if no IDENTITY was sent, we need to get it through alias!
   if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
       && !ind.trim().equalsIgnoreCase(""))
   {

    rset = stmt.executeQuery("SELECT IDENTITY FROM gdbadm.INDIVIDUALS WHERE " +
                            " ALIAS=" + "'"+ind +"'"+
                            " AND SUID=" +"'"+suid+"'");

      if(rset.next())
      {
        identity=rset.getString("IDENTITY");
      }
      else
      {
        identity="-1";
      }
   }
   else
   {
      identity=ind;
   }

  //compare to database, is this unique? (can it be inserted?)
  rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_GENOTYPES_3 WHERE " +
                            "MNAME=" +"'"+ marker+"'" +
                            " AND IDENTITY=" + "'"+identity +"'"+
                            " AND SUID=" +"'"+suid+"'");

  if (rset.next())
  {
    // the genotype exists
    String Message =" The Genotype already exists, cannot be created.";
    errorMessages.addElement(Message);
    nrErrors ++;
    }

    // Does the individual exist?
    rset = stmt.executeQuery("SELECT IID FROM gdbadm.INDIVIDUALS WHERE " +
                              " IDENTITY=" + "'"+identity +"'"+
                              " AND SUID=" +"'"+suid+"'");
    if (!rset.next())
    {
      // the Individual does not exist
      String Message =" The Individual with "+ id_or_alias+" "+ind+" does not exist.";
      errorMessages.addElement(Message);
      nrErrors ++;
    }
    // does marker exist?
    rset = stmt.executeQuery("SELECT MID FROM gdbadm.MARKERS WHERE " +
                              " NAME=" + "'"+marker+"'"+
                              " AND SUID=" +"'"+suid+"'");

    if (!rset.next())
    {
      // the marker does not exist
      String Message =" Marker "+marker+" does not exist";
      errorMessages.addElement(Message);
      nrErrors ++;
    }
    else
    {
      // do the alleles exist for these markers??
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                                " MNAME=" + "'"+marker+"'"+
                                " AND NAME=" +"'"+allele1+"'" +
                                " AND SUID=" +"'"+suid+"'");

      if (!rset.next())
      {
        // the allele does not exist
        String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker+". It will be created if genotype is imported!";
        errorMessages.addElement(Message);
        nrErrors ++;
      }

      // do the alleles exist for these markers??
      rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_ALLELES_3 WHERE " +
                               " MNAME=" + "'"+marker+"'"+
                               " AND NAME=" +"'"+allele2+"'"+
                               " AND SUID=" +"'"+suid+"'");

      if (!rset.next())
      {
        // the allele does not exist
        String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is imported!";
        errorMessages.addElement(Message);
        nrErrors ++;
      }
      rset.close();
    }
  }
  catch (Exception e)
  {
         // Flag for error and set the errMessage if it has not been set
         e.printStackTrace(System.err);
  }
        return nrErrors;

}

    /**
     * Send the file to the browser. This creates a download box if 
     * the filetype is unkown to the browser.
     *
     * @param req The request object
     * @param res  The response object
     */
    private void sendFile(HttpServletRequest req, HttpServletResponse res) 
    {
      String dfid;
      String filename;
      String absPath;
      String contentType;
      String pid, fgid;
      FileInputStream fis = null;
      OutputStream out = null;
      byte[] buf = null;
      Connection conn = null;
      HttpSession session = req.getSession(false);
      Statement stmt = null;
      ResultSet rset = null;
      try {
            filename = req.getParameter("FILENAME");

            absPath = getUpFilePath();
            contentType = getServletContext().getMimeType(absPath + "/" + filename);
            if (contentType == null)
               contentType = new String("text/plain");
            res.setContentType(contentType);
            out = res.getOutputStream();
            fis = new FileInputStream(absPath + "/" + filename);
            buf = new byte[256 * 1024]; // 256 KB
            int bytesRead;
            while ((bytesRead = fis.read(buf)) != -1) {
               out.write(buf, 0, bytesRead);

         }
      } catch (Exception e) {
         e.printStackTrace(System.err);
      }  finally {
         try {
            if (fis != null) fis.close();
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (Exception ignored) {}
      }
   }


    /**
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException  */    
  private void writeCompareResult(HttpServletRequest req,
                               HttpServletResponse res)

      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
     res.setHeader("Cache-Control", "no-cache");

      PrintWriter out = res.getWriter();
      String suname, sname;
      int nrDeviations;
      int nrErrors;
      int nrWarnings;
      int rows;
      String fileName=null;
      String level=null;
      String suid=null;
      String type=null;

      try {
      // Validation script
         out.println("<script type=\"text/JavaScript\">");
         out.println("<!--");
         out.println("function valForm() {");
         out.println("	");
         out.println("	var rc = 1;");
         out.println("	");
         out.println("	if (rc) {");
         out.println("		if (confirm('Are you sure that you want to create/update the genotypes?')) {");
         out.println("			document.forms[0].submit();");
         out.println("		}");
         out.println("	}");
         out.println("	");
         out.println("	");
         out.println("}");
         out.println("//-->");
         out.println("</script>");

          nrDeviations=Integer.parseInt((String)session.getValue("GENO_nrDeviations"));
          nrErrors=Integer.parseInt((String)session.getValue("GENO_nrErrors"));
          nrWarnings=Integer.parseInt((String)session.getValue("GENO_nrWarnings"));
          fileName=(String)session.getValue("GENO_FILE");
          level=(String)session.getValue("GENO_LEVEL");
          suid=(String)session.getValue("GENO_SUID");
          type=(String)session.getValue("GENO_TYPE");
          rows=Integer.parseInt((String)session.getValue("GENO_ROWS"));

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Compare Results</title>");
         out.println("</head>");
         out.println("<body>");


         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<form method=post action=\"" +
                     getServletPath("viewGeno/commitMultipart") + "\">");

         out.println("<tr>");
          out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Genotype - File Import - Results</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");


         out.println("<table width=500 border=0 cellSpacing=0 cellPading=5>");
         out.println("<tr><td style=\"WIDTH: 30px\">&nbsp;</td><td>");

         out.println("<table border=0 cellpading=0 cellspacing=0>");
          out.println("<tr>");
         out.println("<td width=200>FileName</td>");
         out.println("<td width=300>" + fileName + "</td>");
         out.println("</tr>");
          out.println("<tr>");
         out.println("<td width=200>Mode</td>");
         out.println("<td width=300>" + type + "</td>");
         out.println("</tr>");
          out.println("<tr>");
         out.println("<td width=200>Data rows in file</td>");
         out.println("<td width=300>" + rows + "</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Deviations found in file</td>");
         out.println("<td width=300>" + nrDeviations + "</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Warnings found in file</td>");
         out.println("<td width=300>"+nrWarnings+"</td>");
         out.println("</tr>");
         out.println("<tr>");
         out.println("<td width=200>Errors Found in file</td>");
         out.println("<td width=300>"+nrErrors+"</td>");
         out.println("</tr>");
         out.println("<tr><td></td><td></td></tr>");
//         out.println("</table>");

        out.println("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");

        out.println("<tr>");
        int dotPlace=fileName.indexOf(".");
        // get rid of strange endings...
        String newFileName=fileName.substring(0,dotPlace)+".txt";

        out.println("<td>Download commented file</td>");
        out.println("<td><a href=\""
         +getServletPath("viewGeno/download?&FILENAME="+"checked_"+newFileName)
         +"\">here</a>");
        out.println("</td>");
        out.println("</tr>");

         out.println("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");

         // Some buttons
         out.println("<tr><td></td><td></td></tr>");
         out.println("<tr>");

//         out.println("<tr>");
         out.println("<td><input type=button value=\"Cancel\" " +
                     "width=100 style=\"WIDTH: 100px\" "+
                     "onClick='JavaScript:location.href=\"" + getServletPath("viewGeno/impFile") + "\"';>");
          out.println("&nbsp;</td>");

         out.println("<td><input type=button value=\"Commit\" " +
                     "width=100 style=\"WIDTH: 100px\" "
                     +"onClick='valForm()'>");
         out.println("&nbsp;</td>");
         out.println("</tr>");
         out.println("</table>");

         out.println("<input type=hidden name=fileName value="+"checked_"+fileName+">");
         out.println("<input type=hidden name=suid value="+suid+">");
         out.println("<input type=hidden name=level value="+level+">");
         out.println("<input type=hidden name=type value="+type+">");
         out.println("</table>");
          out.println("</form>");

         out.println("</body>");
         out.println("</html>");

         } catch (Exception e) {
         e.printStackTrace(System.err);
      }
  }

/**
 * Check the titles if they are valid.
 * 
 * There should be 8 columns
 * { {IDENTITY | ALIAS} , MARKER , ALLELE1, ALLELE2, RAW1, RAW2, REFERENCE, COMMIT }
 * @param titles
 * @param errorMessages
 * @return  
 */  
private boolean checkListTitles(String [] titles, Vector errorMessages)
{
    // Check the file header
    boolean errorFound=false;
    String errorStr=null;
    if (titles.length != 8)
        errorFound = true;
    else if (!(titles[0].equals("IDENTITY") || titles[0].equals("ALIAS")) ||
            !titles[1].equals("MARKER") ||
            !titles[2].equals("ALLELE1") ||
            !titles[3].equals("ALLELE2") ||
            !titles[4].equals("RAW1") ||
            !titles[5].equals("RAW2") ||
            !titles[6].equals("REFERENCE") ||
            !titles[7].equals("COMMENT") )
        errorFound = true;
    
    if (errorFound)
    {
        errorStr="Illegal headers.<BR>"+
            "Required file headers: IDENTITY/ALIAS MARKER ALLELE1 ALLELE2 RAW1 RAW2 REFERENCE COMMENT<BR>"+
            "Headers found in file:";
        
        for (int j=0; j<titles.length;j++)
        {
            errorStr = errorStr+ " " + titles[j];
        }
              errorMessages.addElement(errorStr);
    }
    return errorFound;
}


/**
 * @param titles
 * @param errorMessages
 * @param suid
 * @param stmt
 * @return  
 */
private boolean checkMatrixTitles(String [] titles, Vector errorMessages,
                                  int suid,Statement stmt)
{
  boolean errorFound=false;
  String errorStr=null;
  ResultSet rset =null;
  try
    {
   // System.err.println("checkMatrixtitles..");
    if (titles.length < 2)
      errorFound = true;
    else if (!(titles[0].equals("IDENTITY") || titles[0].equals("ALIAS")))
    {
      errorFound = true;
    }
    if (errorFound)
    {
      errorStr="Illegal headers.<BR>"+
      "Required file headers: IDENTITY/ALIAS MARKER1 MARKER2 ...<BR>"+
      "Headers found in file:";
      for (int j=0; j<titles.length;j++)
      {
        errorStr = errorStr+ " " + titles[j];
      }
      errorMessages.addElement(errorStr);
    }

    // now we check that the markers in header exists
    for (int i=1;i<titles.length;i++)
    {

      rset = stmt.executeQuery("SELECT MID FROM gdbadm.MARKERS WHERE " +
                               " NAME=" + "'"+titles[i]+"'"+
                                " AND SUID=" +"'"+suid+"'");
      if(!rset.next())
      {
        errorFound=true;
        errorStr="Marker "+titles[i]+" does not exist.";
        errorMessages.addElement(errorStr);
      }
    }


    if(rset!=null)
    rset.close();
    }
    catch (Exception e)
    {
      e.printStackTrace(System.err);
    }
    return errorFound;
}

/**
 * Check the genotype values. Check for existance and length of the fields. 
 *
 * @param identity      The identity (/ alias?)
 * @param marker        The marker 
 * @param allele1       The allele value
 * @param allele2       The allele value 
 * @param raw1          The raw value
 * @param raw2          The raw value
 * @param ref           The reference field
 * @param comm          The comment field
 * @param errMessages   Returns a Vector of error messages (String)
 * @return              Boolean, true if values is ok, or false if something is not ok.
 */
private boolean checkValues(String identity, String marker,
                               String allele1, String allele2,
                               String raw1, String raw2,
                               String ref, String comm, Vector errMessages) {
      boolean ret = true;

      if (identity == null || identity.trim().equals(""))
      {

         errMessages.addElement("Unable to read Identity/Alias.");
         ret = false;
      }
      else if (identity.length() > 11)
      {
         errMessages.addElement("Identity/Alias [" + identity + "] exceeds 11 characters.");
         ret = false;
      }
      else if (marker == null || marker.trim().equals(""))
      {
         errMessages.addElement("Unable to read marker.");
         ret = false;
      }
      else if (marker.length() > 20)
      {
         errMessages.addElement("Marker [" + marker + "] exceeds 20 characters.");
         ret = false;
      }
      else if (allele1 != null && allele1.length() > 20)
      {
         errMessages.addElement("Allele1 [" + allele1 + "] exceeds 20 characters.");
         ret = false;
      }
      else if (allele2 != null && allele2.length() > 20)
      {
         errMessages.addElement("Allele2 [" + allele2 + "] exceeds 20 characters.");
         ret = false;
      }
      else if (raw1 != null && raw1.length() > 20)
      {
         errMessages.addElement("Raw1 [" + raw1 + "] exceeds 20 characters.");
         ret = false;
      }
      else if (raw2 != null && raw2.length() > 20)
      {
         errMessages.addElement("Raw2 [" + raw2 + "] exceeds 20 characters.");
         ret = false;
      }
      else if (ref != null && ref.length() > 32)
      {
         errMessages.addElement("Reference [" + ref + "] exceeds 32 characters.");
         ret = false;
      }
      else if (comm != null && comm.length() > 256)
      {
         errMessages.addElement("Comment exceeds 256 characters.");
         ret = false;
      }
      return ret;
   }


   /**
    * Deletes a genotype.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if genotype was deleted
    *         False if genotype was not deleted.
    */
   private boolean deleteGenotype(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int mid, iid;
         connection.setAutoCommit(false);
         String UserID = (String) session.getValue("UserID");
         mid = Integer.parseInt(request.getParameter("mid"));
         iid = Integer.parseInt(request.getParameter("iid"));
         DbGenotype dbg = new DbGenotype();
         dbg.DeleteGenotype(connection, iid, mid);
         errMessage = dbg.getErrorMessage();
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
                       "Genotypes.Edit.Delete", errMessage, "viewGeno",
                       isOk);
      return isOk;
   }


   /**
    * @param request
    * @param response
    * @param connection
    * @return  */   
   private boolean updateGenotype(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Connection connection)
   {
      String errMessage = null;
      boolean isOk = true;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         String UserID = (String) session.getValue("UserID");
         String raw1, raw2, ref, comm;
         Integer aid1 = null,
            aid2 = null;
         String oldQS = request.getQueryString();
         int mid, iid, level;
         mid = Integer.parseInt(request.getParameter("mid"));
         iid = Integer.parseInt(request.getParameter("iid"));
         if (request.getParameter("a1") != null &&
             !request.getParameter("a1").trim().equals(""))
         {
            aid1 = new Integer(request.getParameter("a1"));
         }
         else
         {
            aid1 = null;
         }
         
         if (request.getParameter("a2") != null &&
             !request.getParameter("a2").trim().equals(""))
         {
            aid2 = new Integer(request.getParameter("a2"));
         }
         else
         {
            aid2 = null;
         }

         raw1 = request.getParameter("r1");
         raw2 = request.getParameter("r2");
         ref = request.getParameter("r");
         comm = request.getParameter("c");
         level = Integer.parseInt(request.getParameter("n_level"));
         DbGenotype dbg = new DbGenotype();
         dbg.UpdateGenotype(connection, iid, mid, aid1, aid2, raw1, raw2,
                            ref, comm, level, Integer.parseInt(UserID));
         errMessage = dbg.getErrorMessage();
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
                       "Genotypes.Edit.Update", errMessage, "viewGeno",
                       isOk);
      return isOk;
   }


   /**
    * @param out  
    */   
   private void writeEditScript(PrintWriter out) 
   {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to delete the genotype?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the genotype?')) {");
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



   /***************************************************************************************
    * *************************************************************************************
    * The completion page
    */
   private void writeCompletion(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
      HttpSession session = req.getSession(true);
      String suid, fid, msid, oldQS, pid, sql, item, oper;
      Statement stmt = null;
      ResultSet rset = null;
      PrintWriter out = null;
      try {
         Connection conn = (Connection) session.getValue("conn");
         fid = req.getParameter("fid");
         suid = req.getParameter("suid");
         msid = req.getParameter("msid");
         pid = (String) session.getValue("PID");
         item = req.getParameter("item");
         oper = req.getParameter("oper");
         if (item == null) item = "";
         if (oper == null) oper = "";
         if (fid == null) fid = "-1";
         if (item.equals("")) {
            suid = findSuid(conn, pid);
            msid = findMsid(conn, suid);
         } else if (item.equals("suid")) {
            msid = findMsid(conn, suid);
         } else if (item.equals("no")) {
            ;
         }
         res.setContentType("text/html");
         res.setHeader("Pragma", "no-cache");
         res.setHeader("Cache-Control", "no-cache");
         out = res.getWriter();
         out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out = res.getWriter();

         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Genotype completion</title>");
         out.println("</head>");
         out.println("<body>");

         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center><b style=\"font-size: 15pt\">Genotypes - Status </b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         out.println("<table border=0 cellspacing=0 cellpadding=0 width=100%>");
         out.println("<tr><td width=15>&nbsp;</td><td>");
         out.println("</td></tr>");
         out.println("<tr><td></td><td></td></tr><tr><td></td><td>");

         out.println("<form method=get action=\"" + getServletPath("viewGeno/completion") + "\">");

         out.println("<table border=0 cellspacing=0 cellpadding=0>");
         out.println("<tr>");
         // Filters
         out.println("<td>Filter<br>");
         sql = "SELECT FID, NAME FROM gdbadm.V_FILTERS_1 WHERE " +
            "PID=" + pid + " ORDER BY NAME";
         stmt = conn.createStatement();
         rset = stmt.executeQuery(sql);
         out.println("<select name=fid width=150 style=\"WIDTH: 150px\">");
         while (rset.next()) {
            out.println("<option " +
                        (rset.getString("FID").equals(fid) ? "selected " : "" ) +
                        "value=\"" + rset.getString("FID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         rset.close();
         stmt.close();
         out.println("</td>");
         // Sampling unit
         out.println("<td>Sampling unit<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM V_ENABLED_SAMPLING_UNITS_2 WHERE PID=" +
                                  pid + " ORDER BY NAME");
         out.println("<select name=suid width=150 style=\"WIDTH: 150px\" " +
                     "onChange='document.forms[0].item.value=\"suid\";document.forms[0].submit();'>");
         while (rset.next()) {
            out.println("<option " +
                        (rset.getString("SUID").equals(suid) ? "selected " : "" ) +
                        "value=\"" + rset.getString("SUID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         stmt.close();
         rset.close();
         out.println("</td>");
         // Marker Set
         out.println("<td>Marker set<br>");
         stmt = conn.createStatement();
         sql = "SELECT MSID, NAME FROM gdbadm.V_MARKER_SETS_1 WHERE " +
            "SUID=" + suid + " ORDER BY NAME";
         rset = stmt.executeQuery(sql);
         out.println("<select name=msid width=150 style=\"WIDTH: 150px\">");
         while (rset.next()) {
            out.println("<option " +
                        (rset.getString("MSID").equals(msid) ? "selected " : "") +
                        "value=\"" + rset.getString("MSID") + "\">" +
                        rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         rset.close();
         stmt.close();
         out.println("</td>");
         // Button
         out.println("<td>");
         out.println("&nbsp;<br><input type=button value=Display " +
                     "onClick='document.forms[0].oper.value=\"DISPLAY\";document.forms[0].submit();'>");
         out.println("</td></tr>");
         out.println("</table>");
         out.println("<input type=hidden name=item value=no>");
         out.println("<input type=hidden name=oper value=\"SEL_CHANGED\">");
         out.println("</form>");
         out.println("</td></tr>");
         out.println("<tr><td colspan=2>");
         out.println("<hr>");
         out.println("</td></tr>");
         out.println("</tr><td></td><td>");

         // The completion data
         if (fid != null && suid != null && msid != null && oper.equals("DISPLAY")) {
            writeCompletionTable(conn, out, pid, suid, fid, msid);
         }

         out.println("</td></tr></table>");
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
   /**
    * @param conn
    * @param out
    * @param pid
    * @param suid
    * @param fid
    * @param msid  */   
   private void writeCompletionTable(Connection conn,
                                     PrintWriter out,
                                     String pid,
                                     String suid,
                                     String fid,
                                     String msid) {

      Statement stmt = null;
      ResultSet rset = null;
      Statement stmt_markers = null;
      ResultSet rset_markers = null;
      int inds = 0;
      int markers = 0;
      String gqlExpr = null, filter = null;
      Vector mids = new Vector(100);
      try {

         // The number of markers and individuals:
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT EXPRESSION FROM " +
                                  "gdbadm.V_FILTERS_1 WHERE FID=" + fid);
         rset.next();

         gqlExpr = rset.getString("EXPRESSION");
         GqlTranslator gqlt = new GqlTranslator(pid, suid, gqlExpr, conn);

         gqlt.translate();
         filter = gqlt.getFilter();
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT COUNT(*) INDS " + filter);
         rset.next();
         inds = rset.getInt("INDS");
         out.println("<p>Filter returns " + inds + " individuals.</p>");

         rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_POSITIONS_1 WHERE MSID=" + msid);
         while (rset.next()) {
            mids.addElement(rset.getString("MID"));
            markers++;
         }

         out.println("<p>Markerset contains " + markers + " markers</p>");

         out.println("<table cellPadding=0 cellSpacing=0 width=500 style=\"WIDTH: 460px\">");
         out.println("<tr bgcolor=\"#008B8B\">");
         out.println("<td width=120><font color=white>Marker</font>");
         out.println("<td width=120><font color=white>Percentage</font>");
         out.println("<td width=120><font color=white>individuals</font>");
         out.println("<td width=120>&nbsp;"); // for hyperlinks
         boolean odd = true;
         for (int i = 0; i < mids.size(); i++) {
            out.println("<tr bgcolor=" + (odd ? "white" : "lightgrey") + ">");
            odd = !odd;
            out.println("<td width=120 style=\"WIDTH: 120px\">");
            rset = stmt.executeQuery("SELECT NAME FROM gdbadm.V_MARKERS_1 WHERE MID=" +
                                     (String) mids.elementAt(i));
            rset.next();
            out.println(rset.getString("NAME"));
            rset = stmt.executeQuery("SELECT COUNT(*) GENOS FROM gdbadm.V_GENOTYPES_1 WHERE " +
                                     "MID=" + (String) mids.elementAt(i) + " AND " +
                                     "IID IN(SELECT ind.IID " + filter + ")");
            rset.next();
            out.println("<td width=120 style=\"WIDTH: 120px\">" +
                        (inds == 0 ? "0" : "" + (int) 100*rset.getInt("GENOS")/inds + "%" ));
            out.println("<td width=120 style=\"WIDTH: 120px\">" + rset.getInt("GENOS") + "");
            out.println("<td width=120 style=\"WIDTH: 120px\">&nbsp;");

         }
         out.println("</table>");
      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
      } catch (ArithmeticException ae) {
         ae.printStackTrace(System.err);
      } finally {
         try {
            if (rset_markers != null) rset_markers.close();
            if (stmt_markers !=null) stmt_markers.close();
         } catch (SQLException ignored) {
         }
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



   /**
    * @param req
    * @param res
    * @return  */   
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
            // We neew the privilege UMRKS_R for all these
            title = "Genotypes - View & Edit";
            if ( privDependentString(privileges, GENO_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/edit") ) {
            // We neew the privilege GENO_W#
            // This one is a lttle bit special. The user is only allowed
            // to view this page if he/she has at least the same level of
            // GTENO_W# as the genotype was last updetat to.
            title = "Genotypes - Edit";
            if ( writeGenoPrivDependentString(privileges, req, "", null, true) == null)
               ok = false;
         } else if (extPath.equals("/new") ) {
            // We neew the privilege GENO_W#
            title = "Genotypes - New";
            if ( writeGenoPrivDependentString(privileges, req, "", null, false) == null)
               ok = false;
         } else if (extPath.equals("/impFile") ) {
            // We need the privilege GENO_W#
            title = "Genotypes - File Import";
            if ( writeGenoPrivDependentString(privileges, req, "", null, false) == null)
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

   /**
    * @param res
    * @param title  */   
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
   
   /**
    * @param privileges
    * @param req
    * @param ifTrue
    * @param ifFalse
    * @param update
    * @return  */   
   private String writeGenoPrivDependentString(int[] privileges,
                                               HttpServletRequest req, String ifTrue, String ifFalse, boolean update) {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      String iid, mid;
      int genoLevel = 0;
      int myHighestLevel = -1;
      boolean authorized = false;
      try {
         // Check if this is a valid request
         if (update) {
            iid = req.getParameter("iid");
            mid = req.getParameter("mid");
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT LEVEL_ FROM V_GENOTYPES_1 WHERE " +
                                     "MID=" + mid + " AND IID=" + iid);
            rset.next();
            genoLevel = rset.getInt("LEVEL_");
         } else {
            genoLevel = 0;
         }
         for (int i = 0; i < privileges.length; i++) {
            if (privileges[i] - GENO_W0 >= 0 &&
                GENO_W9 - privileges[i] >= 0 &&
                privileges[i] - GENO_W0 > myHighestLevel)
               myHighestLevel = privileges[i] - GENO_W0;

         }
         if (myHighestLevel >= genoLevel)
            authorized = true;
      } catch (Exception e) {
         authorized = false;
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {
         }
      }
      if (authorized)
         return ifTrue;
      else
         return ifFalse;
   }
   
   /**
    * @param privileges
    * @param genoLevel
    * @param ifTrue
    * @param ifFalse
    * @return  */   
   private String writeGenoPrivDependentString(int[] privileges,
                                               int genoLevel, String ifTrue, String ifFalse) {
      int myHighestLevel = -1;
      boolean authorized = false;
      try {
         for (int i = 0; i < privileges.length; i++) {
            if (privileges[i] - GENO_W0 >= 0 &&
                GENO_W9 - privileges[i] >= 0 &&
                privileges[i] - GENO_W0 > myHighestLevel)
               myHighestLevel = privileges[i] - GENO_W0;
         }
         if (myHighestLevel >= genoLevel)
            authorized = true;
      } catch (Exception e) {
         authorized = false;
      }
      if (authorized)
         return ifTrue;
      else
         return ifFalse;
   }
   /**
    * @param privileges
    * @param req
    * @param ifTrue
    * @param ifFalse
    * @return  */   
   private String writeGenoPrivDependentString(int[] privileges,
                                               HttpServletRequest req, String ifTrue, String ifFalse) {
      HttpSession session = req.getSession(true);
      int myHighestLevel = -1;
      boolean authorized = false;
      try {
         // Check if this is a valid request
         for (int i = 0; i < privileges.length; i++) {
            if (privileges[i] - GENO_W0 >= 0 &&
                GENO_W9 - privileges[i] >= 0 &&
                privileges[i] - GENO_W0 > myHighestLevel)
               myHighestLevel = privileges[i] - GENO_W0;

         }
         if (myHighestLevel >= 0)
            authorized = true;
      } catch (Exception e) {
         authorized = false;
      }
      if (authorized)
         return ifTrue;
      else
         return ifFalse;
   }
   
   
    /**
     * Returns the names of both alleles for a genotype, given suid,iid and mid
     */
    private String[] getAlleles(HttpServletRequest req, String suid, String iid, String mid)
    {
      String alleles [] = new String[2];
      String sql = null;

      HttpSession session = req.getSession(true);
      String pid= (String)session.getValue("PID");
      Statement stmt = null;
      ResultSet rset = null;
      alleles[0]=null;
      alleles[1]=null;

      if(suid==null ||iid==null || mid==null)
      {
        return alleles;
      }

      try {
         Connection conn = (Connection) session.getValue("conn");

         stmt = conn.createStatement();

         sql = " SELECT A1NAME, A2NAME FROM gdbadm.V_GENOTYPES_4 WHERE PID="
                 + pid + " AND SUID="+suid+" AND IID="+ iid +" AND MID="+ mid;
         rset = stmt.executeQuery(sql);

         if(rset.next())
         {
              alleles[0]=rset.getString("A1NAME");
              alleles[1]=rset.getString("A2NAME");
         }
/*
         if(alleles[0]==null)
          alleles[0]=getNullReplacement(session);
         if(alleles[1]==null)
          alleles[1]=getNullReplacement(session);
*/
        if (rset != null) rset.close();
        if (stmt != null) stmt.close();


    } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
             }

       return alleles;
     }


/**
* Returns the identities of both parents for an individual, given suid and iid
* The father is the first string, the mother the second.
**/
private parentData getParents(HttpServletRequest req, String suid, String iid)
    {
      parentData parents= new parentData(); // note the first=father, second= mother
      String sql = null;

      HttpSession session = req.getSession(true);
      String pid= (String)session.getValue("PID");
      Statement stmt = null;
      ResultSet rset = null;
      if(suid==null ||iid==null)
      {
        return parents;
      }

      try {
         Connection conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();

         // get parents iid's
         sql = " SELECT FATHER, MOTHER FROM gdbadm.V_INDIVIDUALS_1 WHERE SUID="
                + suid +" AND IID="+ iid;
         rset = stmt.executeQuery(sql);
         if(rset.next())
         {
              parents.father_iid=rset.getString("FATHER");
              parents.mother_iid=rset.getString("MOTHER");
         }
         // get parents identities

         sql = " SELECT IDENTITY FROM gdbadm.V_INDIVIDUALS_1 WHERE SUID="
                + suid +" AND IID="+ parents.father_iid;


         rset = stmt.executeQuery(sql);
         if(rset.next())
         {
              parents.father_identity=rset.getString("IDENTITY");
         }
        sql = " SELECT IDENTITY FROM gdbadm.V_INDIVIDUALS_1 WHERE SUID="
                + suid +" AND IID="+ parents.mother_iid;
         rset = stmt.executeQuery(sql);
         if(rset.next())
         {
              parents.mother_identity=rset.getString("IDENTITY");
         }
           if (rset != null) rset.close();
          if (stmt != null) stmt.close();


    } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
         }

      return parents;
     }

/**
 * Returns a vector containing all offsring found starting from parent (parent_identity)
 */
private Vector getAllGenerations(HttpServletRequest req, String suid, String parent_identity, String parent_sex, Vector allInds)
    {
      //System.err.println("params:"+parent_identity+":"+parent_sex+":"+ allInds.size());


      String sql = null;
      HttpSession session = req.getSession(true);
      String pid= (String)session.getValue("PID");
      Statement stmt = null;
      ResultSet rset = null;
      if(suid==null ||parent_identity==null)
      {
        return null;
      }

      try {
         Connection conn = (Connection) session.getValue("conn");
         stmt = conn.createStatement();


         // get all children with this parent
         if(parent_sex.equals("M"))
         {
            sql = " SELECT FIDENTITY, MIDENTITY, IID, IDENTITY, SEX FROM gdbadm.V_INDIVIDUALS_2 WHERE SUID="
                    + suid +" AND FIDENTITY='"+ parent_identity+"'";
         }
         else
         {
            sql = " SELECT FIDENTITY, MIDENTITY, IID, IDENTITY, SEX FROM gdbadm.V_INDIVIDUALS_2 WHERE SUID="
                    + suid +" AND MIDENTITY='"+ parent_identity+"'";
         }
         rset = stmt.executeQuery(sql);
         // for all children found, save and repeat
         while(rset.next())
         {
          // spara undan
          indData tmpInd = new indData();
          tmpInd.father_identity=rset.getString("FIDENTITY");
          tmpInd.mother_identity=rset.getString("MIDENTITY");
          tmpInd.sex =rset.getString("SEX");
          tmpInd.iid =rset.getString("IID");
          tmpInd.identity =rset.getString("IDENTITY");


          if(!indExists(allInds,tmpInd.identity))// individual not allready accounted for
          {
          //  System.err.println("adding " +tmpInd.identity +" to allInds.");
            allInds.addElement(tmpInd);

            // add any parent not in the list allready (EG THE OTHER PARENT)
            addUnrelatedParent(req,allInds,tmpInd,suid);
/*
            // add any parent not in the list allready (EG THE OTHER PARENT)
            // Since we want the other parent, but not its  other children
            if(!indExists(allInds,tmpInd.father_identity))
            {
              sql = " SELECT FIDENTITY, MIDENTITY, IID, IDENTITY FROM gdbadm.V_INDIVIDUALS_2 WHERE SUID="
                    + suid +" AND IDENTITY='"+tmpInd.father_identity+"'";

              parent_rset = parent_stmt.executeQuery(sql);
              if(parent_rset.next())
              {
               indData tmpParent=new indData();
               tmpParent.father_identity=parent_rset.getString("FIDENTITY");
               tmpParent.mother_identity=parent_rset.getString("MIDENTITY");
               tmpParent.iid =parent_rset.getString("IID");
               tmpParent.identity =parent_rset.getString("IDENTITY");
               allInds.addElement(tmpParent);
              }

            }
            if(!indExists(allInds,tmpInd.mother_identity))
            {
               sql = " SELECT FIDENTITY, MIDENTITY, IID, IDENTITY FROM gdbadm.V_INDIVIDUALS_2 WHERE SUID="
                    + suid +" AND IDENTITY='"+tmpInd.mother_identity+"'";
               parent_rset = parent_stmt.executeQuery(sql);
              if(parent_rset.next())
              {
               indData tmpParent=new indData();
               tmpParent.father_identity=parent_rset.getString("FIDENTITY");
               tmpParent.mother_identity=parent_rset.getString("MIDENTITY");
               tmpParent.iid =parent_rset.getString("IID");
               tmpParent.identity =parent_rset.getString("IDENTITY");
               allInds.addElement(tmpParent);
              }

            }
*/
             // call myself to repeat this for all children
             getAllGenerations(req,suid,tmpInd.identity,tmpInd.sex,allInds);

          }

         }

          if (rset != null) rset.close();
         if (stmt != null) stmt.close();


    } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
         }

      return allInds;
     }



    /**
     * Nice method name :)
     * @param out  
     */
     private void writeWiewScript(PrintWriter out) 
     {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function selectionChanged(object) {");
      out.println("	");

      out.println("		if (object=='viewType') {");

  //     out.println("if (!confirm(object))");
  //    out.println("       return false;");

      out.println("			document.forms[0].suid.value='';");
      out.println("			document.forms[0].gsid.value='';");
      out.println("			document.forms[0].gid.value='';");
      out.println("		}");
      out.println("		if (object=='suid') {");
      out.println("			document.forms[0].gsid.value='';");
      out.println("			document.forms[0].gid.value='';");
      out.println("		}");
      out.println("		if (object=='gsid') {");
      out.println("			document.forms[0].gid.value='';");
      out.println("		}");
      out.println("		document.forms[0].submit();");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
   }

 /**
  * Returns true if the individual (identity) already exists within the vector
  */
 private boolean indExists(Vector inds, String identity)
 {
  boolean exists= false;
  for(int i=0; i<inds.size();i++)
  {
    indData ind=(indData)inds.elementAt(i);
    if(ind.identity.equals(identity))
    {
     exists=true;
    }
  }
  return exists;
 }

/**
 * Adds mother or fater of individual (ind) to the list of individuals (inds)
 */
 private void addUnrelatedParent(HttpServletRequest req,Vector allInds, indData tmpInd, String suid)
 {

    String sql = null;
    Statement parent_stmt=null;
    ResultSet parent_rset = null;
    HttpSession session = req.getSession(true);

 try {
         Connection conn = (Connection) session.getValue("conn");
         parent_stmt = conn.createStatement();

 
            // add any parent not in the list allready (EG THE OTHER PARENT)
            // Since we want the other parent, but not its  other children
            if(!indExists(allInds,tmpInd.father_identity))
            {
              sql = " SELECT FIDENTITY, MIDENTITY, IID, IDENTITY FROM gdbadm.V_INDIVIDUALS_2 WHERE SUID="
                    + suid +" AND IDENTITY='"+tmpInd.father_identity+"'";

              parent_rset = parent_stmt.executeQuery(sql);
              if(parent_rset.next())
              {
               indData tmpParent=new indData();
               tmpParent.father_identity=parent_rset.getString("FIDENTITY");
               tmpParent.mother_identity=parent_rset.getString("MIDENTITY");
               tmpParent.iid =parent_rset.getString("IID");
               tmpParent.identity =parent_rset.getString("IDENTITY");
               allInds.addElement(tmpParent);
              }

            }
            if(!indExists(allInds,tmpInd.mother_identity))
            {
               sql = " SELECT FIDENTITY, MIDENTITY, IID, IDENTITY FROM gdbadm.V_INDIVIDUALS_2 WHERE SUID="
                    + suid +" AND IDENTITY='"+tmpInd.mother_identity+"'";
               parent_rset = parent_stmt.executeQuery(sql);
              if(parent_rset.next())
              {
               indData tmpParent=new indData();
               tmpParent.father_identity=parent_rset.getString("FIDENTITY");
               tmpParent.mother_identity=parent_rset.getString("MIDENTITY");
               tmpParent.iid =parent_rset.getString("IID");
               tmpParent.identity =parent_rset.getString("IDENTITY");
               allInds.addElement(tmpParent);
              }

            }
              if (parent_rset != null) parent_rset.close();
              if (parent_stmt != null) parent_stmt.close();

             } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
         }
     }


 /**
  * @param req
  * @param res
  * @throws ServletException
  * @throws IOException  */ 
 private void inheritCheck(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException 
 {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      String item = req.getParameter("item");

      conn = (Connection) session.getValue("conn");

     // System.err.println("inher:"+oper+":"+item);
      if (oper == null) oper = "";

      if (oper.equals("DO_CHECK"))
      {
        doInheritCheck(req,res);
      }
      else
      {
        writeInheritCheck(req, res);
      }
   }


 /**
  * @param req
  * @param res  */ 
private void writeInheritCheck(HttpServletRequest req, HttpServletResponse res) 
{
      Statement stmt = null;
      ResultSet rset = null;
      HttpSession session = req.getSession(true);
      Connection conn=null;
      String old_suid=null,m_choiche=null, suid=null, sid=null, pid=null, msid=null, cid=null,gsid=null ;

  try {
      conn = (Connection) session.getValue("conn");
      PrintWriter out = res.getWriter();
      pid = (String)session.getValue("PID");
      String[] add_mrks = req.getParameterValues("avail_mark");
      String[] rem_mrks = req.getParameterValues("rem_mark");
//      String[] tmp_mrks = req.getParameterValues("choosen_mark");

      String[] add_grps = req.getParameterValues("avail_grps");
      String[] rem_grps = req.getParameterValues("rem_grps");
//    String[] tmp_grps = req.getParameterValues("choosen_grps");

      String oper=req.getParameter("oper");
      suid=req.getParameter("suid");
      old_suid = (String) session.getValue("old_suid");
        msid=req.getParameter("msid");
      gsid=req.getParameter("gsid");
      m_choiche=req.getParameter("m_choiche");
      // the markers
      Vector choosen_mrks= (Vector)session.getValue("choosen_mrks");
      if(choosen_mrks == null)
      {
        choosen_mrks =new Vector();
      }
      // add new choosen markers
      if(oper !=null && oper.equals("ADD_MRK") && add_mrks !=null)
      {
        for(int i=0; i<add_mrks.length;i++)
        {
           if(!choosen_mrks.contains(add_mrks[i]))
           {
             choosen_mrks.addElement(add_mrks[i]);
           }
        }
      }
      // remove the desired markers
      else if (oper != null && oper.equals("REM_MRK") && rem_mrks!=null)
      {
        for(int i=0; i<rem_mrks.length;i++)
        {
          choosen_mrks.remove(rem_mrks[i]);
        }
      }

// the groups
      Vector choosen_grps= (Vector)session.getValue("choosen_grps");
      if(choosen_grps == null)
      {
        choosen_grps =new Vector();
      }
      // add new choosen markers
      if(oper !=null && oper.equals("ADD_GRP") && add_grps!=null)
      {
        for(int i=0; i<add_grps.length;i++)
        {
           if(!choosen_grps.contains(add_grps[i]))
           {
             choosen_grps.addElement(add_grps[i]);
           }
        }
      }
      // remove the desired markers
      else if (oper != null && oper.equals("REM_GRP") && rem_grps!=null)
      {
        for(int i=0; i<rem_grps.length;i++)
        {
          choosen_grps.remove(rem_grps[i]);
        }
      }
           res.setContentType("text/html");

               out.println("<html>");
      out.println("<head>");

 out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function selChanged(item) {");
      out.println("     document.forms[0].oper.value='NOTHING';");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("");
      out.println("function delete_mark() {");
      out.println("     document.forms[0].oper.value='REM_MRK';");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("");
      out.println("function delete_grp() {");
      out.println("     document.forms[0].oper.value='REM_GRP';");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("");
      out.println("function include_mark() {");
      out.println("     document.forms[0].oper.value='ADD_MRK';");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("");
      out.println("function include_grp() {");
      out.println("     document.forms[0].oper.value='ADD_GRP';");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("");
      out.println("function do_chk() {");
      out.println("     document.forms[0].oper.value='DO_CHECK';");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("");
      out.println("//-->");
      out.println("</script>");

/*
   out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function selChanged(item) {");
      out.println("     document.forms[0].oper.value='SELECT';");
      out.println("     document.forms[0].item.value = item;");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("function rem_mark() {");
      out.println("     document.forms[0].oper.value='REM';");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("");
      out.println("function add_mark() {");
      out.println("     document.forms[0].oper.value='ADD';");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("");
      out.println("");
      out.println("//-->");
      out.println("</script>");

*/
//      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
/*
      out.println("<html>");
      out.println("<head>");
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function relPage(choice) {");
      out.println("     document.forms[0].oper.value = choice;");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("function doCheck() {");
      out.println("     document.forms[0].oper.value='DO_CHECK';");
      out.println("     document.forms[0].submit();");
      out.println("     return (true);");
      out.println("}");
      out.println("");
      out.println("//-->");
      out.println("</script>");

*/
      out.println("<title>Inheritance Check</title>");


     
      HTMLWriter.css(out,getURL("style/axDefault.css"));
      out.println("</head>");
      out.println("<body>");


      out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                  "<tr>" +
                  "<td width=\"14\" rowspan=\"3\"></td>" +
                  "<td width=\"736\" colspan=\"2\" height=\"15\">");
      out.println("<center>" +
                  "<b style=\"font-size: 15pt\">Genotypes - Inheritance Check</b></center>" +
                  "</font></td></tr>" +
                  "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                  "</tr></table>");

      out.println("<table cellspacing=0 cellpadding=0><tr>" +
                  "<td width=15></td><td>");


        out.println("<form name=form1 action=\""+getServletPath("viewGeno/inheritCheck")+"\" method=\"post\">");
      out.println("<table width=750 border=0 cellspacing=0>");
         out.println("<tr>");

         // Available sampling units
         out.println("<td>Sampling unit<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, SID, NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid + " order by NAME");
         out.println("<select name=suid style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"RELOAD\")'>");

            while (rset.next()) {
            if (suid != null && suid.equals(rset.getString("SUID")))        {
               out.println("<OPTION selected value=\"" + rset.getString("SUID") + "\">" +
                           rset.getString("NAME")+ "</option>\n");
                           suid=rset.getString("SUID");

            } else {
               out.println("<OPTION value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME")+"</option>\n");
               // first value read
               if(suid == null)
               {
                  suid = rset.getString("SUID");
               }
            }
         }
         if(suid!=null)
          {
            session.putValue("old_suid",suid);//for next "round"..
          }

         rset.close();
         stmt.close();
         out.println("</select>");
         out.println("</td>");

         out.println("</tr>");
        out.println("<tr><td style=\"WIDTH: 200px\"><hr></td></tr>");


        out.println("<tr>");
        out.println("<td width=\"400\">Check Inheritance for the following markers:</td>");
        out.println("</tr>");
         out.println("<tr>");
        out.println("<td>&nbsp</td>");
        out.println("</tr>");

        out.println("<tr>");
        if (m_choiche != null && m_choiche.equals("markers"))
        {
          out.println("<td style=\"WIDTH: 250px\"><input type=\"radio\" name=\"m_choiche\" value=\"marker_set\">Markerset<br></td>");
          out.println("<td style=\"WIDTH: 200px\"><input type=\"radio\" name=\"m_choiche\" value=\"markers\" checked>Markers<br></td>");//</tr>");
        }
        else
        {
          out.println("<td style=\"WIDTH: 250px\"><input type=\"radio\" name=\"m_choiche\" value=\"marker_set\" checked>Markerset<br></td>");
          out.println("<td style=\"WIDTH: 200px\"><input type=\"radio\" name=\"m_choiche\" value=\"markers\" >Markers<br></td>");//</tr>");

        }
        out.println("<td style=\"WIDTH: 50px\">&nbsp</td>");
        out.println("<td style=\"WIDTH: 200px\">Choosen markers<br></td></tr>");
out.println("<tr>");
        //out.println("<td></td><td style=\"WIDTH: 200px\">Markers to include <br></td></tr>");

         // ********************************************************************************
         // Available Marker sets

         out.println("<td valign=top style=\"WIDTH: 250px\">");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT MSID, NAME FROM gdbadm.V_MARKER_SETS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");
         out.println("<select name=msid width=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"RELOAD\")'>");

         while (rset.next())
         {

           if(msid==null || msid.trim().equals(""))
           {
            msid = rset.getString("MSID");
                  out.println("<option selected " +
                        "value=\"" + rset.getString("MSID") + "\">" +
                        rset.getString("NAME") + "</option>");

           }
            else
            {
              out.println("<option " +
                        (msid.equals(rset.getString("MSID")) ? "selected " : "" ) +
                        "value=\"" + rset.getString("MSID") + "\">" +
                        rset.getString("NAME") + "</option>");

           }
         }
         rset.close();
         stmt.close();
         out.println("</select>");
         out.println("</td>");//</tr>");//slut kolumn 2 samt rad

         // ********************************************************************************
         // Available markers
         out.println("<td>");
         out.println("<select name=avail_mark multiple size=10 "
                     + "style=\"WIDTH: 180px\">");
         stmt = conn.createStatement();
         StringBuffer sbSQL = new StringBuffer();
         sbSQL.append("SELECT MID, NAME FROM gdbadm.V_MARKERS_1 ");
         sbSQL.append("WHERE SUID=" + suid +" order by NAME ");

         rset = stmt.executeQuery(sbSQL.toString());
         while (rset.next())
         {
//           if(value ! in choosen)
//           {

             out.println("<option value=\"" + rset.getString("MID") + "\">"
                         + rset.getString("NAME") + "</option>");
//           }
         }
         out.println("</select>");
         out.println("</td><td valign=middle align=middle style=\"WIDTH: 50px\">");


         int privileges[] = (int[]) session.getValue("PRIVILEGES");


         // buttons
         out.println("<input type=button style=\"WIDTH: 30px\" name=m_add value=\">\" " +
                    // privDependentString(privileges, MRKS_W, "onClick='relPage(\"ADD_MRK\")'", "disabled") +
                    privDependentString(privileges, MRKS_W, "onClick='include_mark()'", "disabled") +
                     ">");



         out.println("<br><br>");
         out.println("<input type=button style=\"WIDTH: 30px\" name=m_del value=\"<\" " +
                     privDependentString(privileges, MRKS_W, "onClick='delete_mark()'", "disabled") +
                     ">");

         out.println("</td>");

         // ********************************************************************************
         // choosen Markers
         out.println("<td valign=middle align=left>");
         out.println("<select name=rem_mark multiple size=10 "
                     + "style=\"WIDTH: 180px\">");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();

         if(choosen_mrks !=null && !choosen_mrks.isEmpty())
         {
          for(int i=0; i<choosen_mrks.size();i++)
          {
           String marker = (String)choosen_mrks.elementAt(i);
           rset = stmt.executeQuery("SELECT MID, NAME " +
                                    "FROM V_MARKERS_1 "+
                                    "WHERE SUID=" + suid + " " +
                                    " AND MID="+marker+
                                    " order by NAME");
             if(rset.next())
            {
                out.println("<option value=\"" + rset.getString("MID") + "\">"
                          + rset.getString("NAME") + "</option>");
             }
          }
         }
         out.println("</select>");
         out.println("</td>");
         out.println("</tr>");
         out.println("</table>");

         if(choosen_mrks !=null)
         {
          session.putValue("choosen_mrks",choosen_mrks);
         }
        out.println("<hr>");

         // ********************************************************************************
         // Groups & Grouping

       out.println("<table width=750 border=0 cellspacing=0>");

        out.println("<tr>");
        out.println("<td width=\"400\">Calculate segregation for the following groups:</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td>&nbsp</td>");
        out.println("</tr>");


      out.println("<tr>");
      out.println("<td style=\"WIDTH: 250px\">Grouping<br></td>");
      out.println("<td style=\"WIDTH: 200px\">Available Groups<br></td>");
      out.println("<td style=\"WIDTH: 50px\">&nbsp</td>");
      out.println("<td style=\"WIDTH: 200px\">Choosen Groups<br></td></tr>");
      out.println("<tr>");

      out.println("<td valign=top style=\"WIDTH: 250px\">");

         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT GSID, NAME FROM gdbadm.V_GROUPINGS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");
         out.println("<select name=gsid width=200 style=\"WIDTH: 200px\" " +
                     "onChange='selChanged(\"RELOAD\")'>");

         while (rset.next())
         {
            if (suid !=null && old_suid != null && (!suid.equals(old_suid)))
            {
                gsid = rset.getString("GSID");
            }

           if(gsid==null || gsid.trim().equals(""))
           {
            gsid = rset.getString("GSID");
                  out.println("<option selected " +
                        "value=\"" + rset.getString("GSID") + "\">" +
                        rset.getString("NAME") + "</option>");

           }
            else
            {
              out.println("<option " +
                        (gsid.equals(rset.getString("GSID")) ? "selected " : "" ) +
                        "value=\"" + rset.getString("GSID") + "\">" +
                        rset.getString("NAME") + "</option>");
           }
         }
         if(rset != null && stmt != null)
         {
            rset.close();
            stmt.close();
          }
         out.println("</select>");
         out.println("</td>");//</tr>");//slut kolumn 2 samt rad

         // Available groups
         out.println("<td>");
         out.println("<select name=avail_grps multiple size=5 "
                     + "style=\"WIDTH: 180px\">");
         stmt = conn.createStatement();
         sbSQL = new StringBuffer();
         sbSQL.append("SELECT GID, NAME FROM gdbadm.V_GROUPS_1 ");
         sbSQL.append("WHERE GSID=" + gsid );

         rset = stmt.executeQuery(sbSQL.toString());
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("GID") + "\">"
                        + rset.getString("NAME") + "</option>");
         }
         out.println("</select>");
         out.println("</td><td valign=middle align=middle style=\"WIDTH: 50px\">");


         privileges = (int[]) session.getValue("PRIVILEGES");
         // buttons
         out.println("<input type=button style=\"WIDTH: 30px\" name=g_add value=\">\" " +
                     //privDependentString(privileges, MRKS_W, "onClick='relPage(\"ADD_GRP\")'", "disabled") +
                    privDependentString(privileges, MRKS_W, "onClick='include_grp()'", "disabled") +


                     ">");
         out.println("<br><br>");
         out.println("<input type=button style=\"WIDTH: 30px\" name=g_rem value=\"<\" " +
                    // privDependentString(privileges, MRKS_W, "onClick='relPage(\"REM_GRP\")'", "disabled") +
                    privDependentString(privileges, MRKS_W, "onClick='delete_grp()'", "disabled") +

                     ">");
         out.println("</td>");

         // ********************************************************************************
         // Included groups
         out.println("<td valign=middle align=left>");
         out.println("<select name=rem_grps multiple size=5 "
                     + "style=\"WIDTH: 180px\">");

         rset.close();
         stmt.close();
         stmt = conn.createStatement();

//------------------------------------------

         if(choosen_grps !=null && !choosen_grps.isEmpty())
         {
          for(int i=0; i<choosen_grps.size();i++)
          {
           String group = (String)choosen_grps.elementAt(i);
           rset = stmt.executeQuery("SELECT GID, NAME " +
                                    "FROM V_GROUPS_2 "+
                                    "WHERE SUID=" + suid + " " +
                                    " AND GID="+group+
                                    " order by NAME");
             if(rset.next())
            {
                out.println("<option value=\"" + rset.getString("GID") + "\">"
                          + rset.getString("NAME") + "</option>");
             }
          }
         }
         out.println("</select>");
         out.println("</td>");
         out.println("</tr>");
         out.println("</table>");

         if(choosen_grps !=null)
         {
          session.putValue("choosen_grps",choosen_grps);
         }
        out.println("<hr>");

        out.println("<table width=750 border=0 cellspacing=0>");
        out.println("<tr>");
        out.println("<td>");
        out.println("<input type=button value=\"Check\" width=100 " +
                     "style=\"WIDTH: 100px\" onClick='do_chk()'>&nbsp;");
        out.println("<input type=hidden name=oper value=\"\">");
        out.println("<input type=hidden name=item value=\"\">");
        out.println("</td>");
        out.println("</tr>");
        out.println("</table>");
         out.println("</form></td>");
        out.println("</tr>");

         out.println("</table>");
         out.println("</body></html>");

      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
      } catch (Exception e) {
         e.printStackTrace(System.err);
      }finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}

      }



/*--------------------- bort
      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
      } catch (Exception e) {
         e.printStackTrace(System.err);


 ------------------*/
   }



/**
 * @param req
 * @param res
 * @throws IOException
 * @throws ServletException
 * @return  */
 private boolean doInheritCheck(HttpServletRequest req,
                              HttpServletResponse res)
  throws IOException, ServletException
  {
    HttpSession session = req.getSession(true);
    PreparedStatement pstmt=null;
    PreparedStatement pstmtGrps=null;
     // to save names for presentation page
    Vector fileNames= new Vector();

    boolean isOk = true;
    boolean errorFound = false;
    FileWriter errFile=null;
    FileWriter corrFile=null;
    FileWriter segFile=null;

    Connection conn = null;
    Statement stmt =null;
    ResultSet rset = null;
    ResultSet rset2 = null;
    String[] mids;
    String[] gids;
    Vector markers = null;
    Vector groups = null;
    Vector genotypes =null;
    String gsid = null;
    String msid = null;
    String suid = null;
    String use_msid=null;
    String a1=null, a2=null,a1_f=null,a2_f=null,a1_m=null,a2_m=null;
    String ref=null, m_ref=null , f_ref=null;
    String m_choiche=null;

    try
    {
      String upPath = getUpFilePath();
      conn = (Connection) session.getValue("conn");
      errFile = new FileWriter(upPath+ "/" + "Errors.txt");
      corrFile= new FileWriter(upPath+ "/" + "Corrections.txt");
      suid=req.getParameter("suid");
      m_choiche=req.getParameter("m_choiche");
      msid=req.getParameter("msid");
      errFile.write("Encountered errors:\n");
      errFile.write("Child\tAlias\tMarker\tA1\tA2\tRef\tFather\tA1(f)\tA2(f)\tRef(f)\tMother\tA1(m)\tA2(m)\tRef(m)\n");
      errFile.write("======================================================================================================\n");
      corrFile.write("GENOTYPE/LIST/1/;\n");
      corrFile.write("IDENTITY;ALIAS;MARKER;ALLELE1;ALLELE2;RAW1;RAW2;REFERENCE;COMMENT\n");
      groups = (Vector) session.getValue("choosen_grps");

       if(groups != null || !groups.isEmpty())
      {
            pstmtGrps = conn.prepareStatement("SELECT IID "
                                  +" FROM gdbadm.V_R_IND_GRP_1 "
                                  +" WHERE IID=? AND GID=? ");
      }

      if (m_choiche.equals("markers"))
      {
       markers= (Vector)session.getValue("choosen_mrks");
      }
      else // we use markers from the markerset
      {
        markers = new Vector();
        stmt = conn.createStatement();
        rset = stmt.executeQuery("SELECT MID" +
                                  " FROM V_POSITIONS_2 "+
                                  " WHERE MSID=" + msid + " ");
       while(rset.next())
       {
          markers.addElement(rset.getString("MID"));
       }
      }
      session.removeValue("choosen_mrks");
      session.removeValue("choosen_grps");
      session.removeValue("old_suid");

        // we get all matching genotypes for each marker.
        genotypes = new Vector();

        pstmt = conn.prepareStatement("SELECT MID, IID, IDENTITY, ALIAS, NAME, A1NAME, A2NAME, "+
                                  "  FATHER, MOTHER, REFERENCE, RAW1, RAW2, COMM "
                                  +" FROM gdbadm.V_GENOTYPES_6 "
                                  +" WHERE MID=?"
                                  + " order by " +
                                  "NAME, IDENTITY");

        for (int i=0; i<markers.size();i++)
        {
          String mid = (String) markers.elementAt(i);
          genotypes = new Vector();
          pstmt.clearParameters();
	        pstmt.setString(1,mid);
          rset=pstmt.executeQuery();
        // save all genotypes found
          while (rset.next())
          {
            genoData tmp = new genoData();
            String tmpAl1 =null;
            String tmpAl2 = null;

            tmp.identity=rset.getString("IDENTITY");
            tmp.alias=rset.getString("ALIAS");
            tmp.iid=rset.getString("IID");
            tmp.mname=rset.getString("NAME");
            tmp.mid=rset.getString("MID");

            // we wish to sort these...
            tmpAl1=rset.getString("A1NAME");
            tmpAl2=rset.getString("A2NAME");
            // we count allele 0 as null..........
            if ((tmpAl1==null) || (tmpAl1.equals("0")))
            {
                tmpAl1="";
            }
            if ((tmpAl2==null) || (tmpAl2.equals("0")))
            {
                tmpAl2="";
            }
            if ((tmp.alias==null) || (tmp.alias.equalsIgnoreCase("null")))
            {
                tmp.alias="";
            }

            if(tmpAl1.compareTo(tmpAl2) < 0)
            {
              tmp.a1=rset.getString("A1NAME");
              tmp.a2=rset.getString("A2NAME");
            }
            else
            {
              tmp.a1=rset.getString("A2NAME");
              tmp.a2=rset.getString("A1NAME");
            }
            // we count allele 0 as null..........

            if(tmp.a1!=null && tmp.a1.equals("0"))
            {
                tmp.a1=null;
            }
            if(tmp.a2!=null && tmp.a2.equals("0"))
            {
               tmp.a2=null;
            }

            tmp.f_iid=rset.getString("FATHER");
            tmp.m_iid=rset.getString("MOTHER");
            tmp.raw1=rset.getString("RAW1");
            tmp.raw2=rset.getString("RAW2");
            tmp.ref=rset.getString("REFERENCE");
            tmp.com=rset.getString("COMM");
            genotypes.addElement(tmp);
          }//rset.next
          rset.close();

         // we find parental data
         for (int j=0 ; j<genotypes.size();j++)
         {
            genoData ind = (genoData) genotypes.elementAt(j);
            for(int k=0; k<genotypes.size();k++)
            {
              genoData ind2  = (genoData) genotypes.elementAt(k);
              // father

              if (ind2.iid.equals(ind.f_iid))
              {
                ind.a1_f=ind2.a1;
                ind.a2_f=ind2.a2;
                ind.f_identity=ind2.identity;
                ind.ref_f = ind2.ref;
              }
               //mother
              if (ind2.iid.equals(ind.m_iid))
              {
                ind.a1_m=ind2.a1;
                ind.a2_m=ind2.a2;
                ind.m_identity=ind2.identity;
                ind.ref_m = ind2.ref;

              }
            }// for genosize (k)
            genotypes.removeElementAt(j);
            genotypes.insertElementAt(ind, j);
         }//for genosize (j)

          // now, we write the error and correctional files..
         for (int j=0 ; j<genotypes.size();j++)
         {
            genoData geno = (genoData)genotypes.elementAt(j);
              if(!correctInheritance(geno))
              {
                  // errorofile
                errFile.write(geno.identity+"\t"+geno.alias+"\t"+geno.mname+"\t");
                
                errFile.write(replaceNull(geno.a1,"*")+"\t"+replaceNull(geno.a2,"*")+"\t"+ replaceNull(geno.ref,"*")+"\t");
                errFile.write(replaceNull(geno.f_identity,"*")+"\t");
                errFile.write(replaceNull(geno.a1_f,"*")+"\t"+replaceNull(geno.a2_f,"*")+"\t"+replaceNull(geno.ref_f,"*")+"\t");
                errFile.write(replaceNull(geno.m_identity,"*")+"\t");
                errFile.write(replaceNull(geno.a1_m,"*")+"\t"+ replaceNull(geno.a2_m,"*") +"\t"+replaceNull(geno.ref_m,"*")+"\n");

                //correctional file
                corrFile.write(geno.identity +";"+geno.alias+";"+geno.mname+";"+replaceNull(geno.a1," ")+
                                ";"+replaceNull(geno.a2," ")+";"+replaceNull(geno.raw1," ")+
                                ";"+replaceNull(geno.raw2," ")+";"+replaceNull(geno.ref," ")
                                +";"+replaceNull(geno.com," ")+"\n");
              }//if !correct

          } // for genotypes.size

                // for each marker, we make a file for each group..
               for(int j=0; j<groups.size();j++)
               {
                    // get all groupmemebers
                    String group=(String) groups.elementAt(j);
                    Vector members=new Vector();
                    getMembers(members,group,genotypes,conn);
                    Vector parentCombinations = new Vector();
                    Vector childCombinations = new Vector();

                    for(int k=0; k<members.size();k++)
                    {
                      genoData tmpElement= (genoData) members.elementAt(k);
                      addParentComb(parentCombinations, tmpElement);
                    }// for genotypes

                    // segregational data
                    getChildCombs(members,childCombinations);

                    int [][] allFreq;
                    allFreq= new int[parentCombinations.size()][childCombinations.size()];

                    for (int y=0;y<parentCombinations.size();y++)
                    {
                      for (int l=0;l<childCombinations.size();l++)
                      {
                          allFreq[y][l]=0;
                      }
                    }
                    countFrequencies(allFreq,parentCombinations,childCombinations,members);
                    FileWriter sgFile=null;
                    if(parentCombinations.size()>0 && childCombinations.size()>0)
                    {
                      String suname=getSUName(suid,conn);
                      String mname=getMarkName(mid,conn);
                      String gname=getGroupName((String)groups.elementAt(j),conn);
                      String fileName=mname+"_"+ gname+ "_Segregation";
                      fileNames.addElement(fileName);

                      sgFile=new FileWriter(upPath+ "/" + fileName);
                      sgFile.write("Segregation data for sampling unit ");
                      sgFile.write(suname);
                      sgFile.write(", marker ");
                      sgFile.write(mname);
                      sgFile.write(" and group ");
                      sgFile.write(gname);
                      sgFile.write(".\n");
                      sgFile.write("==========================================================================");
                      sgFile.write("\n\n\n");

                      sgFile.write("Father\t\tMother\t\tChild genotype frequency\n");
                      sgFile.write("genotype\tgenotype\t");

                      for(int y=0;y<childCombinations.size();y++)
                      {
                        childComb tmpCh=(childComb)childCombinations.elementAt(y);
                        sgFile.write(replaceNull(tmpCh.a1,"*")+","+replaceNull(tmpCh.a2,"*")+"\t\t");
                      }
                      sgFile.write("Total\n");

                      sgFile.write("============================================");
                      for (int q=0;q<childCombinations.size();q++)
                      {
                        sgFile.write("===============");
                      }
                      sgFile.write("\n");

                      for(int y=0;y<parentCombinations.size();y++)
                      {
                        parentComb tmpPa=(parentComb)parentCombinations.elementAt(y);
                        sgFile.write(replaceNull(tmpPa.f1,"*")+","+replaceNull(tmpPa.f2,"*")+"\t\t"
                        +replaceNull(tmpPa.m1,"*")+","+replaceNull(tmpPa.m2,"*")+"\t\t");
                        int sumFreq=0;
                        for(int l=0;l<childCombinations.size();l++)
                        {
                          sumFreq = sumFreq +allFreq[y][l];
                          sgFile.write(allFreq[y][l]+"\t\t");
                        }
                        sgFile.write(sumFreq+"\n");
                      }

                      sgFile.write("============================================");
                      for (int q=0;q<childCombinations.size();q++)
                      {
                        sgFile.write("===============");
                      }
                      sgFile.write("\n");


                      int [] sumCols= new int[childCombinations.size()];
                      int tmpVal=0;
                      //all columns
                      for (int l=0;l<childCombinations.size();l++)
                      { //all rows
                        for(int k=0;k<parentCombinations.size();k++)
                        {
                          tmpVal+= allFreq[k][l];
                        }
                        sumCols [l]=tmpVal;
                        tmpVal=0;
                        }// for childcombinations
                      sgFile.write("Total:\t\t\t\t");
                      int totCount=0;
                      for(int l=0;l<childCombinations.size();l++)
                      {
                        sgFile.write(sumCols[l]+"\t\t");
                        totCount+= sumCols[l];
                      }
                      sgFile.write(totCount+"\n");
                      sgFile.flush();
                      sgFile.close();

                    } // if combs not 0
                }
        }// for markers....

        errFile.flush();
        errFile.close();
        corrFile.flush();
        corrFile.close();
        // add filename to vector and save in sesseion...
        session.putValue("segFiles",fileNames);
        if(pstmtGrps!=null)
          pstmtGrps.close();
        if(stmt!=null)
          stmt.close();
        if(pstmt!=null)
          pstmt.close();
        if(rset!=null)
          rset.close();

    } // try
    catch (Exception e)
      {
         e.printStackTrace(System.err);
     }
        res.sendRedirect(getServletPath("viewGeno/inheritResults"));
        return true;
}

 /**
  * @param members
  * @param gid
  * @param genotypes
  * @param conn  */ 
private void getMembers(Vector members,String gid,Vector genotypes,Connection conn)
{
  //
  try{
  int index;
  ResultSet rset;

  Statement stmt=conn.createStatement();

  rset = stmt.executeQuery("SELECT IID "
                                  +" FROM gdbadm.V_R_IND_GRP_1 "
                                  +" WHERE GID="
                                  + gid+" ");

  //loop through found members
  // copy these from read genotypes to new vector
  while(rset.next())
  {
    boolean found=false;
    genoData tmpGeno= null;
    String tmpIID=rset.getString("IID");
    index=0;
    while (index < genotypes.size()&& found== false)
    {
      tmpGeno=(genoData)genotypes.elementAt(index);

      if (tmpIID.equals(tmpGeno.iid))
      {
       found=true;
       //System.err.println("::"+tmpGeno.a1 +":"+tmpGeno.a2);
       members.addElement((genoData)genotypes.elementAt(index));
      }
      index++;
    }
  }
      }catch (Exception e) {
  e.printStackTrace(System.err);
  }

}


/**
 * @param gid
 * @param conn
 * @return  */
private String getGroupName(String gid,Connection conn)
{
    String group=null;
 try{
    ResultSet rset =null;
    Statement stmt = conn.createStatement();

    rset = stmt.executeQuery("SELECT NAME FROM gdbadm.V_GROUPS_1 "
                                  +" WHERE GID="+gid+" ");
    if(rset.next())
    {
      group=rset.getString("NAME");
    }

    stmt.close();
    rset.close();

    }catch (Exception e) {
  e.printStackTrace(System.err);
  }

  return group;
}
/**
 * @param mid
 * @param conn
 * @return  */
private String getMarkName(String mid,Connection conn)
{
    String marker=null;
 try{
    ResultSet rset =null;
    Statement stmt = conn.createStatement();

    rset = stmt.executeQuery("SELECT NAME FROM gdbadm.V_MARKERS_1 "
                                  +" WHERE MID="+mid+" ");
    if(rset.next())
    {
      marker=rset.getString("NAME");
    }

    stmt.close();
    rset.close();

    }catch (Exception e) {
  e.printStackTrace(System.err);
  }

  return marker;
}
/**
 * @param suid
 * @param conn
 * @return  */
private String getSUName(String suid,Connection conn)
{
    String samplingUnit=null;
 try{
    ResultSet rset =null;
    Statement stmt = conn.createStatement();

    rset = stmt.executeQuery("SELECT NAME FROM gdbadm.V_ENABLED_SAMPLING_UNITS_1 "
                                  +" WHERE SUID="+suid+" ");
    if(rset.next())
    {
      samplingUnit=rset.getString("NAME");
    }

    stmt.close();
    rset.close();

    }catch (Exception e) {
  e.printStackTrace(System.err);
  }

  return samplingUnit;
}


/**
 * @param allFreq
 * @param parentCombinations
 * @param childCombinations
 * @param genotypes  */
private void countFrequencies(int [][]allFreq,//int[]parentFreq,int[]childFreq,
                            Vector parentCombinations,Vector childCombinations,
                            Vector genotypes)
{

 //for all parentcombinations:
  for(int i=0;i<parentCombinations.size();i++)
  {
   // System.err.println("parent nr "+i);

    parentComb currentParent= (parentComb)parentCombinations.elementAt(i);
    //make copy to avoid ruining original..
    parentComb tmpParent = new parentComb();
    tmpParent.f1=currentParent.f1;
    tmpParent.f2=currentParent.f2;
    tmpParent.m1=currentParent.m1;
    tmpParent.m2=currentParent.m2;

        if(tmpParent.f1 == null)
          tmpParent.f1="";
        if(tmpParent.f2 == null)
          tmpParent.f2="";
        if(tmpParent.m1 == null)
          tmpParent.m1="";
        if(tmpParent.m2 == null)
          tmpParent.m2="";

    // for all childcombinations
    for(int j=0;j<childCombinations.size();j++)
    {
   // System.err.println("child nr "+j);

      childComb currentChild = (childComb) childCombinations.elementAt(j);
      childComb tmpChild = new childComb();
      tmpChild.a1=currentChild.a1;
      tmpChild.a2=currentChild.a2;
      if(tmpChild.a1 == null)
        tmpChild.a1="";
      if(tmpChild.a2 == null)
        tmpChild.a2="";

      // search + count all occurances
      for(int k=0;k<genotypes.size();k++)
      {
        genoData currentGeno = (genoData)genotypes.elementAt(k);
        genoData tmpGeno = new genoData();


        tmpGeno.a1=currentGeno.a1;
        tmpGeno.a2=currentGeno.a2;
        tmpGeno.a1_f=currentGeno.a1_f;
        tmpGeno.a2_f=currentGeno.a2_f;
        tmpGeno.a1_m=currentGeno.a1_m;
        tmpGeno.a2_m=currentGeno.a2_m;


        if(tmpGeno.a1 == null)
          tmpGeno.a1="";
        if(tmpGeno.a2 == null)
          tmpGeno.a2="";
        if(tmpGeno.a1_f == null)
          tmpGeno.a1_f="";
        if(tmpGeno.a2_f == null)
          tmpGeno.a2_f="";
        if(tmpGeno.a1_m == null)
          tmpGeno.a1_m="";
        if(tmpGeno.a2_m == null)
          tmpGeno.a2_m="";

         //System.err.println("genotype nr "+k+"  "+tmpGeno.a1+":"+tmpGeno.a2);
         //System.err.println("father  "+tmpGeno.a1_f+":"+tmpGeno.a2_f);
         //System.err.println("mother  "+tmpGeno.a1_m+":"+tmpGeno.a2_m);

//         System.err.println("compare:"+ tmpParent.f1 +":"+tmpParent.f2+":"+tmpParent.m1+":"+tmpParent.m2);
        // parentmatch?
        if(tmpGeno.a1_f.equals(tmpParent.f1) && tmpGeno.a2_f.equals(tmpParent.f2)
            && tmpGeno.a1_m.equals(tmpParent.m1) && tmpGeno.a2_m.equals(tmpParent.m2))
        {
        //   System.err.println("parentmatch "+i+":"+j);

        //childmatch??
/*           System.err.println("genotype:");
           System.err.println(tmpGeno.a1 + ":" + tmpGeno.a2 );
           System.err.println("child:");
           System.err.println( tmpChild.a1+ ":" + tmpChild.a2 );

           System.err.println("in child:");
            for (int f=0;f<childCombinations.size();f++)
            {
              childComb tmpCh1 = (childComb)childCombinations.elementAt(f);
              System.err.println(tmpCh1.a1+":"+tmpCh1.a2);
            }
*/
          if(tmpGeno.a1.equals(tmpChild.a1) && tmpGeno.a2.equals(tmpChild.a2))
          {
//              System.err.println("childmatch, adding for "+i+":"+j);
              allFreq[i][j]++;
          }//childmatch
        }//parentMatch
      }// fo all genotypes
    }// for children.size
  }// for parents.size


 //serach + count......



}



/**
 * @param parentCombs
 * @param entry  */
private void addParentComb(Vector parentCombs,genoData entry)
{
        boolean found = false;
        parentComb tmpComb;
     //   System.err.println("entering AddPArent:"+entry.a1_f+":"+entry.a2_f+":"+entry.a1_m+":"+entry.a2_m);


        for (int j=0; j<parentCombs.size();j++)
        {
            tmpComb= (parentComb) parentCombs.elementAt(j);
              if (sameAlleles(tmpComb.m1,tmpComb.m2,entry.a1_m,entry.a2_m))
              {
                if (sameAlleles(tmpComb.f1,tmpComb.f2,entry.a1_f,entry.a2_f))
                {
                 found = true;
                }
              }
        }
        if(found == false)
        {
          parentComb newComb = new parentComb();
          newComb.f1=entry.a1_f;
          newComb.f2=entry.a2_f;
          newComb.m1=entry.a1_m;
          newComb.m2=entry.a2_m;
          parentCombs.addElement(newComb);
        }
}

/**
 * @param genos
 * @param childCombs  */
private void getChildCombs(Vector genos, Vector childCombs)
{
        String a=null,b=null,c=null,d=null;
        childComb tmpChild;


          for (int i=0; i<genos.size();i++)
          {
            genoData tmpGeno=(genoData)genos.elementAt(i);

              tmpChild= new childComb();
              tmpChild.a1=tmpGeno.a1;
              tmpChild.a2=tmpGeno.a2;
              addChildComb(childCombs, tmpChild);
          }
}

/**
 * @param childCombs
 * @param entry  */
private void addChildComb(Vector childCombs,childComb entry)
{
        boolean found = false;
        childComb tmpComb;
        childComb newComb =new childComb();
      //  System.err.println("entering Addchild:"+entry.a1+":"+entry.a2);
        for (int i=0; i<childCombs.size();i++)
        {
            tmpComb= (childComb) childCombs.elementAt(i);
              if (sameAlleles(tmpComb.a1,tmpComb.a2,entry.a1,entry.a2))
              {
                 found = true;
                 i=childCombs.size();
              }
        }
        if(found == false)
        {
          newComb.a1=entry.a1;
          newComb.a2=entry.a2;
          childCombs.addElement(newComb);
       //   System.err.println("added comb: Vector=");
           for (int i=0; i<childCombs.size();i++)
           {
            childComb tmpComb2= (childComb) childCombs.elementAt(i);
       //     System.err.println(tmpComb2.a1+":"+tmpComb2.a2);
           }

        }
}

/**
 * @param a1
 * @param a2
 * @param b1
 * @param b2
 * @return  */
private boolean sameAlleles(String a1, String a2, String b1, String b2)
{
    boolean same = false;
    String a_1, a_2, b_1,b_2; // make copies to cope with null strings
    a_1=a1;
    a_2=a2;
    b_1=b1;
    b_2=b2;

    // if the genovalue is null, we accept it as valid in any case.
    // thereforre it should be set to the same as a parental value??

    if(a_1== null)
    {
      a_1="";
    }
     if(a_2== null)
    {
      a_2="";
    }
    if(b_1==null)
    {
      b_1="";
    }
    if(b_2==null)
    {
      b_2="";
    }


    if(a_1.equals(b_1) && a_2.equals(b_2))
    {
      same = true;
    }
    if(a_2.equals(b_1) && a_1.equals(b_2))
    {
      same = true;
    }
  //  System.err.println("sameAllele returning:"+same +" for"+ a_1+":"+a_2+"-"+b_1+":"+b_2);
    return same;
}


/**
 * @param ca1
 * @param pa1
 * @param pa2
 * @return  */
private boolean possibleParent(String ca1, String pa1, String pa2)
{
    if (ca1== null || ca1.trim().equals(""))
        return true;

    if (pa1== null || pa1.trim().equals(""))
        return true;

    if (pa2== null || pa2.trim().equals(""))
        return true;

    if (ca1.equals(pa1) || ca1.equals(pa2))
    {
        return true;
    }
//System.err.println("possible=false");
    return false;
}

/**
 * @param ind
 * @return  */
private boolean correctInheritance(genoData ind)
{
      boolean m_a1_p, m_a2_p, m_a1_m, m_a2_m;
      m_a1_p = false;
      m_a2_p = false;
      m_a1_m = false;
      m_a2_m = false;
  /*    System.err.println("correct inheritance..");
      System.err.println(ind.a1+":"+ind.a2);
      System.err.println(ind.a1_f+":"+ind.a2_f);
      System.err.println(ind.a1_m+":"+ind.a2_m);

*/
      m_a1_p = possibleParent(ind.a1,ind.a1_f,ind.a2_f);
      m_a2_p = possibleParent(ind.a2,ind.a1_f,ind.a2_f);
      m_a1_m = possibleParent(ind.a1,ind.a1_m,ind.a2_m);
      m_a2_m = possibleParent(ind.a2,ind.a1_m,ind.a2_m);


      if(m_a1_p && m_a2_m)
      {
        return true;
      }
      if(m_a2_p && m_a1_m)
      {
        return true;
      }
 // System.err.println("returning false inherit..");
 return false;
}



/**
 * @param req
 * @param res
 * @throws ServletException
 * @throws IOException  */
 private void writeInheritResults(HttpServletRequest req,
                               HttpServletResponse res)

      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      Statement stmt = null;
      ResultSet rset = null;
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
     res.setHeader("Cache-Control", "no-cache");

      PrintWriter out = res.getWriter();
      String suname, sname;

      try {
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Inherit Results</title>");
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
          out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Genotype - Inheritance Check - Results</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
/*
         out.println("<table width=500 border=0 cellSpacing=0 cellPading=5>");
        out.println("<tr><td style=\"WIDTH: 30px\">&nbsp;</td><td>");
        out.println("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");
        out.println("<tr>");
        out.println("<td colspan=2>");

*/
        out.println("<table border=0 cellspacing=0 cellpading=5>");
        out.println("<tr><td>&nbsp</td></tr>");
        out.println("<tr><td>&nbsp</td></tr>");

        out.println("<tr>");
        out.println("<td style=\"WIDTH: 60px\">&nbsp</td>");
        out.println("<td>");
        out.println("<table border=0 cellspacing=0 cellpadding=0 >");
        out.println("<TR><td>Generated files:<br><hr></td></TR>");
        //out.println("<TR><td><hr></td></TR>");

        out.println("<TR><td><a href=\""
         +getServletPath("viewGeno/download?&FILENAME="+"Errors.txt")
         +"\">Errors</a>");
        out.println("</td>");
        out.println("</tr>");

        out.println("<TR><td><a href=\""
         +getServletPath("viewGeno/download?&FILENAME="+"Corrections.txt")
         +"\">Corrections</a>");
        out.println("</td>");
        out.println("</tr>");

        Vector files= (Vector) session.getValue("segFiles");
        for(int i=0;i<files.size();i++)
        {
         String fileName=(String) files.elementAt(i);
         out.println("<TR><td><a href=\""
         +getServletPath("viewGeno/download?&FILENAME="+fileName)
         +"\">"+fileName+"</a>");
        out.println("</td>");
        out.println("</tr>");

        }
         out.println("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");

         // Some buttons
         out.println("<tr><td><hr></td><td></td></tr>");
         out.println("<tr>");
         out.println("<td><input type=button value=\"Done\" " +
                     "width=100 style=\"WIDTH: 100px\" "+
                     "onClick='JavaScript:location.href=\"" + getServletPath("viewGeno/inheritCheck") + "\"';>");
          out.println("&nbsp;</td>");
         out.println("</tr>");

         out.println("</table>");
         out.println("</table>");

         out.println("</body>");
         out.println("</html>");

         } catch (Exception e) {
         e.printStackTrace(System.err);
      }
  }
}

