/*
  $Log$
  Revision 1.12  2005/02/08 16:03:21  heto
  DbIndividual is now complete. Some bug tests are done.
  DbSamplingunit is converted. No bugtest.
  All transactions should now be handled in the GUI (yuck..)

  Revision 1.11  2005/02/04 15:58:40  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.10  2005/01/31 16:16:41  heto
  Changing database to PostgreSQL. Problems with counts and selection buttons...

  Revision 1.9  2004/03/31 08:48:30  heto
  Fixed debug messages

  Revision 1.8  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.7  2003/04/28 15:16:36  heto
  Code layout changes.

  Revision 1.6  2003/04/25 15:06:37  heto
  Added debug message
  code layout changes

  Revision 1.5  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.4  2002/10/22 06:08:09  heto
  rebuilt the "back-buttons".
  Dont save the request object, save the URL instead.
  New function.

  Revision 1.3  2002/10/18 14:31:48  heto
  Updated session support.

  Revision 1.2  2002/10/18 11:41:10  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:05  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.12  2001/06/13 09:30:21  frob
  Modified interfact of comment method in HTMLWriter, caused updates in several files.

  Revision 1.11  2001/06/13 06:06:24  frob
  Changed the structure of the header table produced in HTMLWriter. From now, the table
  has only two rows. Any other stuff has to be placed within a content table (also
  produced byt the HTMLWriter). This modification caused updates in several servlets.

  Revision 1.10  2001/05/31 07:07:01  frob
  Implemented the writeBottomDefault method in HTMLWriter and removed it from all
  servlets. The servlets now uses the method in HTMLWriter.

  Revision 1.9  2001/05/28 06:34:09  frob
  Adoption to changes in HTMLWriter.

  Revision 1.8  2001/05/22 06:16:55  roca
  Backfuncktionality fixed for all (?) servlets/subpages

  Revision 1.7  2001/05/21 07:58:50  frob
  Totaly rewrote writeDetails method.

  Revision 1.6  2001/05/15 13:36:25  roca
  After merge problems from last checkin..

  Revision 1.5  2001/05/11 08:30:31  frob
  Changed all methods that used to call writeError() to write an error page. These
  methods now calls the general method commitOrRollback() which does the commit or
  rollback operation as well as handle any errors. writeError() was removed.

  Revision 1.4  2001/05/03 07:57:40  frob
  Replaced calls to removeQSParameter to calls to dedicated methods eg removeQSParameterSid

  Revision 1.3  2001/05/02 14:12:35  frob
  Calls to removeOper changed to use the general removeQSParameter.
  The previously called method is removed.

  Revision 1.2  2001/05/02 14:09:21  frob
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
import se.arexis.agdb.db.TableClasses.Individual;

public class viewInd extends SecureArexisServlet
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

      //HttpSession session = req.getSession(true);


      if ( !authorized(req, res) ) {
         // The user does not have the privileges to view the requested page.
         // The method pageLocked has already written an error message
         // to the output stream, and that's why we safely can return here.
         return;
      }
      
      

      String extPath = req.getPathInfo();
      Errors.logDebug("viewInd.doGet(...) QS="+req.getQueryString());
      Errors.logDebug("viewInd.doGet(...) path="+extPath);

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
      } else if (extPath.equals("/completion")) {
         writeCompletion(req, res);
      }
   }
   
   private void writeFrame(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException 
   {
      HttpSession session = req.getSession(true);
      // set content type and other response header fields first
      res.setContentType("text/html");
      PrintWriter out = res.getWriter();
      try 
      {
          // If a redirection is needed, do it here...
          res = checkRedirectStatus(req,res);
          
          
         //req = getServletState(req,session);
          

         String topQS = buildQS(req);
         // we don't want the oper parameter anaywhere but in the "edit" and "new" page!
         topQS = removeQSParameterOper(topQS);
         String bottomQS = topQS.toString();

         out.println("<html>"
                     + "<HEAD>"
                     + " <TITLE>View Individuals</TITLE>"
                     + "</HEAD>"
                     + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" //
                     + "<frame name=\"viewtop\" "
                     + "src=\""+ getServletPath("viewInd/top?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewmiddle\" "
                     + "src=\""+ getServletPath("viewInd/middle?") + topQS + "\""
                     + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
                     + "</frame>\n"

                     + "<frame name=\"viewbottom\""
                     + "src=\"" +getServletPath("viewInd/bottom?") + bottomQS + "\" "
                     + " scrolling=\"auto\" marginheight=\"0\" frameborder=\"0\"></frameset>"
                     + "<noframes><body><p>"
                     + "This page uses frames, but your browser doesn't support them."
                     + "</p></body></noframes></frameset>"
                     + "</HTML>");
      } catch (Exception e)
      {
         e.printStackTrace(System.err);
      }
      finally 
      {
      }
   }
   
   private String buildQS(HttpServletRequest req) 
   {
       Errors.logInfo("viewInd.buildQS(...) started");
      StringBuffer output = new StringBuffer(512);
      
      //HttpSession session = req.getSession(true);
      HttpSession session = null;
      if (req != null) 
          session = req.getSession();
      else
          Errors.logError("Request error!!");
      
      Connection conn = (Connection) session.getAttribute("conn");


      /*	String action = null, // For instance COUNT, DISPLAY, NEXT etc
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
                reference = null;

		boolean suid_changed = false;
		String pid = (String) session.getAttribute("PID");
		old_suid = (String) session.getAttribute("SUID");
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
		old_cid = (String) session.getAttribute("CID");
		cid = req.getParameter("cid");
		if (suid_changed)
                cid = findCid(conn, suid);
		else {
                if (cid == null)
                cid = old_cid;
		}
		if (cid == null)
                cid = findCid(conn, suid);
		session.putValue("CID", cid);

      */


      String action = null,
      suid = null,
      old_suid = null,
      alias = null,
      identity = null,
      sex = null,
      bd_from = null,
      bd_to = null,
      father = null,
      mother = null,
      orderby = null,
      status = null;

      boolean suid_changed = false;
      String pid = (String) session.getAttribute("PID");
      old_suid = (String) session.getAttribute("SUID");
      suid = req.getParameter("suid");
      if (suid == null) 
      {
         suid = old_suid;
         suid_changed = true;
      } 
      else if (old_suid != null && !old_suid.equals(suid)) 
      {
         suid_changed = true;
      }
      if (suid == null) 
      {
         suid = findSuid(conn, pid);
         suid_changed = true;
      }
      session.setAttribute("SUID", suid);

      /*
        old_suid = (String) session.getAttribute("SUID");
        if (old_suid == null) old_suid = new String("");
        suid = req.getParameter("SUID");
        if (suid != null) {
        session.putValue("SUID", new String(suid));
        } else {

        suid = old_suid.toString();
        }
      */
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


      output.append("ACTION=")
         .append(action);

      alias = req.getParameter("ALIAS");
      if (alias != null)
         output.append("&ALIAS=").append(alias);
      identity = req.getParameter("IDENTITY");
      if (identity != null)
         output.append("&IDENTITY=").append(identity);
      sex = req.getParameter("SEX");
      if (sex != null)
         output.append("&SEX=").append(sex);
      bd_from = req.getParameter("BD_FROM");
      if (bd_from != null)
         output.append("&BD_FROM=").append(bd_from);
      bd_to = req.getParameter("BD_TO");
      if (bd_to != null)
         output.append("&BD_TO=").append(bd_to);
      father = req.getParameter("FATHER");
      if (father != null)
         output.append("&FATHER=").append(father);
      mother = req.getParameter("MOTHER");
      if (mother != null)
         output.append("&MOTHER=").append(mother);
      status = req.getParameter("STATUS");
      if (status != null)
         output.append("&STATUS=").append(status);



      // Set the parameters STARTINDEX and ROWS
      if (!action.equals("NOP"))
         output.append(setIndecis(suid, old_suid, action, req, session));
      output.append("&suid=").append(suid);
      if (req.getParameter("oper") != null)
         output.append("&oper=").append(req.getParameter("oper"));
      if (req.getParameter("new_geno_name") != null)
         output.append("&new_geno_name=").append(req.getParameter("new_geno_name"));
/*
      orderby = req.getParameter("ORDERBY");
      if (orderby != null)
         output.append("&ORDERBY=").append(orderby);
      else
         output.append("&ORDERBY=IDENTITY");
*/

      // Orderby must be the last parameter in the query string
      orderby = req.getParameter("ORDERBY");
      if (orderby != null)
         output.append("&ORDERBY=").append(orderby);
      else
         output.append("&ORDERBY=IDENTITY");

      Errors.logInfo("viewInd.buildQS(...) ended");
      return output.toString().replace('%', '*');
   }
   
   /*
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
     {
     output.append("&reference=").append(reference);
     }

     // Set the parameters STARTINDEX and ROWS
     if (!action.equals("NOP"))
     output.append(setIndecis(suid, old_suid, cid, old_cid, action, req, session));
     output.append("&suid=").append(suid);
     output.append("&cid=").append(cid);
     if (req.getParameter("oper") != null)
     output.append("&oper=").append(req.getParameter("oper"));
     if (req.getParameter("new_geno_name") != null)
     output.append("&new_geno_name=").append(req.getParameter("new_geno_name"));

     orderby = req.getParameter("ORDERBY");
     if (orderby != null)
     output.append("&ORDERBY=").append(orderby);
     else
     output.append("&ORDERBY=IDENTITY");
   */

   private String findSuid(Connection conn, String pid) 
   {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID FROM gdbadm.V_SAMPLING_UNITS_2 WHERE PID=" +
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
         rset = stmt.executeQuery("SELECT CID FROM gdbadm.CHROMOSOMES_1 WHERE SID=" +
                                  "(SELECT SID FROM gdbadm.V_SAMPLING_UNITS_1 WHERE SUID=" + suid + ")" +
                                  " ORDER BY NAME");
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
   private String findAllele(Connection conn, String aid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT NAME FROM gdbadm.ALLELES_1 WHERE AID=" + aid);
         if (rset.next()) {
            ret = rset.getString("NAME");
         } else {
            ret = "(None)";
         }
      } catch (SQLException e) {
         ret = "(Error)";
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {
         }
      }
      return ret;
   }
   private String findMid(Connection conn, String cid) {
      Statement stmt = null;
      ResultSet rset = null;
      String ret;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT MID FROM gdbadm.MARKERS_1 WHERE CID=" +
                                  cid + " ORDER BY NAME");
         if (rset.next()) {
            ret = rset.getString("MID");
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


   /*
     private String setIndecis(String suid, String old_suid, String cid, String old_cid, String action, HttpServletRequest req, HttpSession session) {
     StringBuffer output = new StringBuffer(128);
     int rows = 0, startIndex = 0, maxRows = 0;
     rows = countRows(suid, cid, req, session);
     maxRows = Integer.parseInt( (String) session.getAttribute("MaxRows"));
     if (req.getParameter("STARTINDEX") != null &&
     old_suid.equalsIgnoreCase(suid) &&
     old_cid.equalsIgnoreCase(cid))
     {
     startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
     // System.err.println("start="+ startIndex);
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
     //System.err.println("maxrows="+maxRows+" incremented=" +startIndex);
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
     //System.err.println("out="+output.toString());
     return output.toString();
     }
   */
   private String setIndecis(String suid, String old_suid, String action, HttpServletRequest req, HttpSession session) {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(suid, req, session);
      maxRows = getMaxRows(session);
      if (req.getParameter("STARTINDEX") != null && old_suid.equalsIgnoreCase(suid)) {
         startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         if (rows > 0 && startIndex == 0) startIndex = 1;
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

   /*
     private int countRows(String suid, String cid, HttpServletRequest req, HttpSession session) {
     Connection conn = (Connection) session.getAttribute("conn");
     Statement stmt = null;
     ResultSet rset = null;
     StringBuffer sbSQL = new StringBuffer(512);
     try {
     sbSQL.append("SELECT count(*) "
     + "FROM gdbadm.V_GENOTYPES_2 WHERE SUID=" + suid + " AND CID=" + cid + " ");
     sbSQL.append(buildFilter(req));
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
   */

   private int countRows(String suid, HttpServletRequest req, HttpSession session) {
      Connection conn = (Connection) session.getAttribute("conn");
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer(512);
      try {
         sbSQL.append("SELECT count(*) "
                      + "FROM gdbadm.V_INDIVIDUALS_1 WHERE SUID=" + suid + " ");
         sbSQL.append(buildFilter(req,false));
         stmt = conn.createStatement();
         rset = stmt.executeQuery(sbSQL.toString());
         rset.next();
         return rset.getInt(1);		
											
      } catch (SQLException e) 
      {
          e.printStackTrace();
          System.out.println(sbSQL);
         return 0;
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         }catch (SQLException ignored) {}
      }
   }

   /*
     private String buildFilter(HttpServletRequest req) {
     String identity = null,
     chromosome = null,
     marker = null,
     allele1 = null,
     allele2 = null,
     orderby = null,
     reference = null;

     StringBuffer filter = new StringBuffer(256);
     identity = req.getParameter("identity");
     chromosome = req.getParameter("chromosome");
     marker = req.getParameter("marker");
     allele1 = req.getParameter("allele1");
     allele2 = req.getParameter("allele2");
     orderby = req.getParameter("ORDERBY");
     reference = req.getParameter("reference");

     if (identity != null && !"".equalsIgnoreCase(identity))
     filter.append("and IDENTITY like '" + identity + "'");
     if (chromosome != null && !"".equalsIgnoreCase(chromosome))
     filter.append(" and CNAME like'" + chromosome + "'");
     if (marker != null && !"".equalsIgnoreCase(marker))
     filter.append(" and MNAME like'" + marker + "'");
     if (allele1 != null && !"".equalsIgnoreCase(allele1))
     filter.append(" and A1NAME like'" + allele1 + "'");
     if (allele2 != null && !"".equalsIgnoreCase(allele2))
     filter.append(" and A2NAME like'" + allele2 + "'");
     if(reference != null && !"".equalsIgnoreCase(reference))
     filter.append(" and REFERENCE like'" + reference + "'");

     // temp-commented!
     if (orderby != null && !"".equalsIgnoreCase(orderby))
     filter.append(" order by " + orderby);
     else
     filter.append(" order by IDENTITY");

     // Replace every occurence of '*' with '%' and return the string
     // (Oracel uses '%' as wildcard while '%' demands some specail treatment
     // when passed in the query string)
     return filter.toString().replace('*', '%');
     }
   */
   
   private String buildFilter(HttpServletRequest req) 
   {
       return buildFilter(req,true);
   }
   
   private String buildFilter(HttpServletRequest req, boolean order) 
   {
        String identity = null,
        alias = null,
        sex = null,
        bd_from = null,
        bd_to = null,
        father = null,
        mother = null,
        orderby = null,
        status = null;

      StringBuffer filter = new StringBuffer(256);
      identity = req.getParameter("IDENTITY");
      alias = req.getParameter("ALIAS");
      sex = req.getParameter("SEX");
      bd_from = req.getParameter("BD_FROM");
      bd_to = req.getParameter("BD_TO");
      father = req.getParameter("FATHER");
      mother = req.getParameter("MOTHER");
      orderby = req.getParameter("ORDERBY");
      status = req.getParameter("STATUS");

      if (identity != null && !"".equalsIgnoreCase(identity)) 
         filter.append("and IDENTITY like '" + identity + "'");
      if (alias != null && !"".equalsIgnoreCase(alias))
         filter.append(" and ALIAS like '" + alias + "'");
      if ("M".equalsIgnoreCase(sex))
         filter.append(" and SEX='M'");
      else if ("F".equalsIgnoreCase(sex))
         filter.append(" and SEX='F'");
      else if ("U".equalsIgnoreCase(sex))
         filter.append(" and SEX='U'");

      if (bd_from != null && !"".equalsIgnoreCase(bd_from))
         filter.append(" and BIRTH_DATE >= to_date('" + bd_from + "', 'YYYY-MM-DD')");
      if (bd_to != null && !"".equalsIgnoreCase(bd_to))
         filter.append(" and BIRTH_DATE <= to_date('" + bd_to + "', 'YYYY-MM-DD')");
      if (father != null && !"".equalsIgnoreCase(father))
         filter.append(" and FIDENTITY like '" + father + "'");
      if (mother != null && !"".equalsIgnoreCase(mother))
         filter.append(" and MIDENTITY like '" + mother + "'");
      if (status != null && !"".equalsIgnoreCase(status))
         filter.append(" and STATUS ="+ "'" + status + "'");
      
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
   /*
     private String formatOutput(String inputString, int maxLength) {
     String output = null;

     //System.err.println("in="+inputString);
     if(inputString == null || inputString.trim().equalsIgnoreCase(""))
     {
     output=getNullReplacement();
     if (output==null || output.trim().equalsIgnoreCase(""))
     {
     output = "&nbsp;&nbsp;";
     }
     }
     else
     {
     if (inputString.length() <= maxLength)
     {
     output = inputString;
     }
     else
     {
     output = inputString.substring(0,maxLength-2) +"..";
     }
     }
     return output;
     }

   */
   /***************************************************************************************
    * *************************************************************************************
    * The top frame
    */

   public void writeTop(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      // set content type and other response header fields first
      res.setContentType("text/html");
      PrintWriter out = res.getWriter();
      String oper;
      oper = req.getParameter("oper");
      if (oper == null || "".equals(oper))
         oper = "SELECT";
/*
      if (oper.equals("NEW_GENO")) {
         if (!createGeno(req, res))
            ; //return;
      }

      */
      HttpSession session = req.getSession(true);
      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null;
      //		int startIndex = 0, rows = 0, maxRows = 0;
      /* from indtop..
         String suid, alias, identity, sex, bd_from, bd_to, mother, father, orderby, action, pid;
         String oldQS, newQS;

         int startIndex = 0, rows = 0, maxRows = 0;
         try {

         // Read all availible sampling units from database
         conn = (Connection) session.getAttribute("conn");
         stmt = conn.createStatement();
         oldQS = req.getQueryString();
         newQS = buildQueryString(oldQS);
         suid = (String) session.getAttribute("SUID");
         pid = (String) session.getAttribute("PID");
         if (pid == null) pid = new String("-1"); // Hope this will select nothing
         maxRows = getMaxRows(session);
         alias = req.getParameter("ALIAS");
         identity = req.getParameter("IDENTITY");
         sex = req.getParameter("SEX");
         bd_from = req.getParameter("BD_FROM");
         bd_to = req.getParameter("BD_TO");
         father = req.getParameter("FATHER");
         mother = req.getParameter("MOTHER");
         orderby = req.getParameter("ORDERBY");
         action = req.getParameter("ACTION");
         if (req.getParameter("STARTINDEX") != null)
         startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         else
         startIndex = 0;
         if (req.getParameter("ROWS") != null)
         rows = Integer.parseInt(req.getParameter("ROWS"));
         else
         rows = 0;
         if (suid == null) suid = new String("");
         if (alias == null) alias = new String("");
         if (identity == null) identity = new String("");
         if (sex == null) sex = new String("Male");
         if (bd_to == null) bd_to = new String("");
         if (bd_from == null) bd_from = new String("");
         if (father == null) father = new String("");
         if (mother == null) mother = new String("");
         if (orderby == null) orderby = new String("IDENTITY");
         if (action == null) action = new String("NOP");

         rset = stmt.executeQuery("SELECT NAME, SUID FROM gdbadm.V_SAMPLING_UNITS_2 " +


      */
      /*String suid, cid, marker, identity, allele1, allele2, orderby, oldQS, newQS, action, pid ,reference;

        try {
        conn = (Connection) session.getAttribute("conn");

        pid = (String) session.getAttribute("PID");
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
        if (identity == null) identity = "";
        if (reference == null) reference = "";
        if (allele1 == null) allele1 = "";
        if (allele2 == null) allele2 = "";
        if (marker == null) marker = "";
        if (orderby == null) orderby = "NAME";
        if (action == null) action = "NOP";
        if (pid == null || "".equalsIgnoreCase(pid))
        pid = "-1";
      */
      String suid, alias, identity, sex, bd_from, bd_to, mother, father, orderby, action, pid, status;
      String oldQS, newQS;
      int testC=0;
      int startIndex = 0, rows = 0, maxRows = 0;
      int currentPrivs[];

      try {

         // Read all availible sampling units from database
         conn = (Connection) session.getAttribute("conn");
         currentPrivs = (int [])session.getAttribute("PRIVILEGES");
         stmt = conn.createStatement();
//         oldQS = req.getQueryString();
         oldQS = removeQSParameter(req.getQueryString(),"oper");
         newQS = buildTopQS(oldQS);
         suid = req.getParameter("suid");


         //suid = (String) session.getAttribute("SUID");
         pid = (String) session.getAttribute("PID");
         //pid = req.getParameter("pid");

         if (pid == null) pid = new String("-1"); // Hope this will select nothing
         maxRows= getMaxRows(session);
         alias = req.getParameter("ALIAS");
         identity = req.getParameter("IDENTITY");
         sex = req.getParameter("SEX");
         bd_from = req.getParameter("BD_FROM");
         bd_to = req.getParameter("BD_TO");
         father = req.getParameter("FATHER");
         mother = req.getParameter("MOTHER");
         orderby = req.getParameter("ORDERBY");
         action = req.getParameter("ACTION");
         status = req.getParameter("STATUS");
         if (req.getParameter("STARTINDEX") != null)
            startIndex = Integer.parseInt(req.getParameter("STARTINDEX"));
         else
            startIndex = 0;
         if (req.getParameter("ROWS") != null)
            rows = Integer.parseInt(req.getParameter("ROWS"));
         else
            rows = 0;
         if (suid == null) suid = new String("");
         if (alias == null) alias = new String("");
         if (identity == null) identity = new String("");
         if (sex == null) sex = new String("Male");
         if (bd_to == null) bd_to = new String("");
         if (bd_from == null) bd_from = new String("");
         if (father == null) father = new String("");
         if (mother == null) mother = new String("");
         if (orderby == null) orderby = new String("IDENTITY");
         if (action == null) action = new String("NOP");
         if (status == null) status = new String("E");


         out.println("<html>");
         out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+ getURL("style/view.css") +"\">");
         out.println("<base target=\"content\">");

         writeTopScript(out);
         out.println("<title>Individuals</title>");
         out.println("</head>");


         out.println("<body bgcolor=\"#ffffd0\">"
                     +"<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
                     +"<tr>"
                     + "<td width=\"14\" rowspan=\"3\">" /*+ "<img src=\""
                                                           +getURL("images/Image5.gif")+ "\" height=150 width=\"1\">"*/ +"</td>"
                     +"<td width=\"736\" colspan=\"2\" height=\"15\">"
                     +"<form method=get action=\"" +getServletPath("viewInd") +"\">"
                     +"<p align=\"center\"><font size=\"2\"><b  style=\"font-size: 15pt\">Individuals - View & Edit</b>"
                     +"</font></td></tr>"
                     +"<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>"
                     +"</tr><tr><td width=\"517\">");

         out.println("<table width=488 height=\"92\">"

                     +"<td><b>Sampling unit</b><br><select name=suid "
                     +"name=select onChange='document.forms[0].submit()'  style=\"HEIGHT: 22px; WIDTH: 126px\">");


         stmt = conn.createStatement();
         out.println("status:"+status);
         rset = stmt.executeQuery("SELECT NAME, SUID FROM gdbadm.V_SAMPLING_UNITS_2 " +
                                  "WHERE PID="+ pid + " AND STATUS='E'" +" ORDER BY NAME");
         while (rset.next()) {
            if (suid != null && suid.equalsIgnoreCase(rset.getString("SUID")))
               out.println("<OPTION selected value=\"" + rset.getString("SUID") + "\">" +
                           rset.getString("NAME")+ "</option>\n");
            else
               out.println("<OPTION value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME")+"</option>\n");
         }
         rset.close();
         stmt.close();
         out.println("</SELECT></td>");

         out.println("<td><b>Identity</b><br>"
                     + "<input id=IDENTITY name=IDENTITY value=\"" + identity + "\" style=\"HEIGHT: 22px; WIDTH: 127px\" size=\"12\"></td>");
         /*
           stmt = conn.createStatement();
           rset = stmt.executeQuery("SELECT CID, NAME FROM gdbadm.V_CHROMOSOMES_1 " +
           "WHERE SID=("+
           "SELECT SID FROM gdbadm.V_SAMPLING_UNITS_1 " +
           "WHERE SUID=" + suid + "AND PID="+ pid + ") order by " +
           "gdbadm.TO_NUMBER_ELSE_NULL(NAME), NAME");



         */
         out.println("<td><b>Alias</b><br>"
                     + "<input id=alias name=ALIAS value=\"" +alias+"\" style=\"HEIGHT: 22px; WIDTH: 127px\" size=\"12\"></td>");

         out.println("<td><b>Sex</b><br>"

                     +"<select name=SEX  style=\"HEIGHT: 22px; WIDTH: 126px\">");

         if (sex.equalsIgnoreCase("*"))
            out.println("<OPTION selected value=*>*");
         else
            out.println("<OPTION value=*>*");
         if (sex.equalsIgnoreCase("M"))
            out.println("<OPTION selected value=M>Male");
         else
            out.println("<OPTION value=M>Male");
         if (sex.equalsIgnoreCase("F"))
            out.println("<OPTION selected value=F>Female");
         else
            out.println("<OPTION value=F>Female");

         if (sex.equalsIgnoreCase("U"))
            out.println("<OPTION selected value=U>Unknown");
         else
            out.println("<OPTION value=U>Unknown");
         out.println("</SELECT></TD>");

         // enabled/disabled
         out.println("<td><b>Status</b><br>"

                     +"<select name=STATUS  style=\"HEIGHT: 22px; WIDTH: 126px\">");

         if (status.equalsIgnoreCase("E"))
            out.println("<OPTION selected value=E>Enabled");
         else
            out.println("<OPTION value=E>Enabled");
         if (status.equalsIgnoreCase("D"))
            out.println("<OPTION selected value=D>Disabled");
         else
            out.println("<OPTION value=D>Disabled");
         out.println("</SELECT></TD>");
         ////



         /*
           while (rset.next()) {
           if (sex != null && sex.equalsIgnoreCase(rset.getString("SEX")))
           out.println("<OPTION selected value=\"" + rset.getString("SEX") + "\">" +
           rset.getString("NAME"));
           else
           out.println("<OPTION value=\"" + rset.getString("CID") + "\">" + rset.getString("NAME"));
           }
           out.println("</SELECT></td>");
         */
         out.println("<tr>");
         out.println("<td><b>Birth Date from</b><br>"
                     +"<input id=BD_FROM name=BD_FROM value=\"" + bd_from + "\" style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\">"
                     +"</td>");
         out.println("<td><b>Birth Date to</b><br>"
                     + "<input id=BD_TO name=BD_TO value=\"" + bd_to + "\"style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\">"
                     +"</td>");
         out.println("<td><b>Father</b><br>"
                     + "<input id=FATHER name=FATHER value=\"" + father + "\"style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\">"


                     +"</td><td><b>Mother</b><br>"
                     +"<input id=MOTHER name=MOTHER value=\"" + mother + "\" style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\">"
                     +"</td></table></td>");


         //Buttons
         out.println("<td width=219>");
         out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
         out.println("<td colspan=4>\n");


         out.println(privDependentString(currentPrivs,IND_W,

                                         /*if true*/"<input type=button value=\"New Individual\""
                                         + " onClick='parent.location.href=\"" +getServletPath("viewInd/new?") + oldQS + "\"' "
                                         +"height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">"
                                         +"</td>",
                                         /*if false*/"<input type=button disabled value=\"New Individual\""
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
      out.println("var MAX_GENO_LENGTH = 20;");
      out.println("var MIN_GENO_LENGTH = 1;");
      out.println("function newGeno() {");
      out.println("  alert('popup new window for creation of genotype!');");
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
      out.println("//-->");
      out.println("</script>");

   }
   private boolean createGeno(HttpServletRequest req, HttpServletResponse res) {
      /*		HttpSession session = req.getSession(true);
                        Connection conn = (Connection) session.getAttribute("conn");
                        String name, sid, pid;
                        try {
			conn.setAutoCommit(false);
			name = req.getParameter("new_su_name");
			sid = req.getParameter("sid");
			pid = (String) session.getAttribute("PID");

			if (name == null) {
				// Well, nothing much to do really.
				return true;
                                }
                                DBSamplingUnit dbSU = new DBSamplingUnit();
                                dbSU.CreateSamplingUnit(conn,
                                pid,
                                name,
                                "", // Comment
                                Integer.parseInt(sid),
                                Integer.parseInt((String) session.getAttribute("UserID")));
                                if (dbSU.getStatus() != 0) {
				conn.rollback();
				return false;
                                }
                                } catch (Exception e) {
                                try {
				conn.rollback();
                                } catch (SQLException ignored) {
                                }
                                return false;
                                }
                                try {
                                conn.commit();
                                } catch (SQLException ignored) {
                                }
      */
      return true;
   }



   /***********************************************************
                                                               /* The middle frame (contains header for the result-table)
                                                                */
   private void writeMiddle(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      // set content type and other response header fields first
      res.setContentType("text/html");

      PrintWriter out = res.getWriter();
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
         out.println("</head>\n<body>");

         if(action != null)
         {
            out.println("&nbsp;" +buildInfoLine(action, startIndex, rows, maxRows));
         }
         String oldQS, newQS;
         oldQS = req.getQueryString();
         String choosen= req.getParameter("ORDERBY");
         newQS = buildTopQS(oldQS);

         /*
           //                                846?
           //out.println("<table width=\"850\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
           //+ "</tr>"
           //+ "<tr>"
           //+ "<td width=\"750\" colspan=\"3\">"
           //style=\"margin-left:5px

           out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0 height=20 width=840 style=\"margin-left:2px\">"
           //+"<TR align=left>"
           + "<td width=5></td>");

         */
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
                     " height=20 width=840 style=\"margin-left:2px\">");
         out.println("<td width=5></td>");




         // the menu choices
         //Identity
         out.println("<td width=90><a href=\"" + getServletPath("viewInd")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=IDENTITY\">");
         if(choosen.equals("IDENTITY"))
            out.println("<FONT color=saddlebrown><b>Identity</b></FONT></a></td>\n");
         else out.println("Identity</a></td>\n");
         //alias
         out.println("<td width=95><a href=\"" + getServletPath("viewInd")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=ALIAS\">");
         if(choosen.equals("ALIAS"))
            out.println("<FONT color=saddlebrown><b>Alias</b></FONT></a></td>\n");
         else out.println("Alias</a></td>\n");
         //Sex
         out.println("<td width=30><a href=\"" + getServletPath("viewInd")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=SEX\">");
         if(choosen.equals("SEX"))
            out.println("<FONT color=saddlebrown><b>Sex</b></FONT></a></td>\n");
         else out.println("Sex</a></td>\n");
         //Birthdate
         out.println("<td width=105><a href=\"" + getServletPath("viewInd")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=BIRTH_DATE\">");
         if(choosen.equals("BIRTH_DATE"))
            out.println("<FONT color=saddlebrown><b>Birth date</b></FONT></a></td>\n");
         else out.println("Birth date</a></td>\n");
         // Father
         out.println("<td width=95><a href=\"" + getServletPath("viewInd")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=FIDENTITY\">");
         if(choosen.equals("FIDENTITY"))
            out.println("<FONT color=saddlebrown><b>Father</b></FONT></a></td>\n");
         else out.println("Father</a></td>\n");
         // Mother
         out.println("<td width=95><a href=\"" + getServletPath("viewInd")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=MIDENTITY\">");
         if(choosen.equals("MIDENTITY"))
            out.println("<FONT color=saddlebrown><b>Mother</b></FONT></a></td>\n");
         else out.println("Mother</a></td>\n");
         //USER
         out.println("<td width=80><a href=\"" + getServletPath("viewInd")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
         if(choosen.equals("USR"))
            out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
         else out.println("User</a></td>\n");
         //Updated
         out.println("<td width=140><a href=\"" + getServletPath("viewInd")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=TS\">");
         if(choosen.equals("TS"))
            out.println("<FONT color=saddlebrown><b>Updated</b></FONT></a></td>\n");
         else out.println("Updated</a></td>\n");
         /*
           out.println("<td width=55>&nbsp;</td>"
           + "<td width=50>&nbsp;</td>\n"
           //      + "</table>"
           //      + "</td>"
           //      + "</tr>"
           + "</table>\n"
           + "</body></html>");
         */
         out.println("<td width=55>&nbsp;</td>");
         out.println("<td width=50>&nbsp;</td>");
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
      PrintWriter out = res.getWriter();
      Statement stmt = null;
      ResultSet rset = null;
      Connection conn = null;
      int currentPrivs[];
      try
      {
         String suid = null, cid = null, action = null, status=null;
         String oldQS = req.getQueryString();
         //System.err.println(oldQS);
         action = req.getParameter("ACTION");
         suid = req.getParameter("suid");
         status = req.getParameter("STATUS");
         currentPrivs = (int [])session.getAttribute("PRIVILEGES");
         //cid = req.getParameter("cid");
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
         } else if (action.equalsIgnoreCase("PREV"))
         {
            // The opposit
         }

         out.println("<html>\n"
                     + "<head><link rel=\"stylesheet\" type=\"text/css\" href=\""
                     +getURL("style/bottom.css")+"\">\n"
                     + "<title>bottomFrame</title>\n"
                     + "</head>\n"
                     + "<body>\n");

         conn = (Connection) session.getAttribute("conn");
         stmt = conn.createStatement();
         StringBuffer sbSQL = new StringBuffer(512);

         sbSQL.append("SELECT IDENTITY, ALIAS, SEX, " +
                      "to_char(BIRTH_DATE, 'YYYY-MM-DD') as TC_BIRTH_DATE, " +
                      "FIDENTITY, MIDENTITY, IID, USR, to_char(TS, '" + getDateFormat(session) + "') as TC_TS " +
                      "FROM gdbadm.V_INDIVIDUALS_2 WHERE SUID=" + suid + " ");
         String qs = req.getQueryString();
         // System.err.println("bottQS=" + qs);
         // Build filter
         String filter = buildFilter(req);
         sbSQL.append(filter);
         //out.println(sbSQL.toString());
         rset = stmt.executeQuery(sbSQL.toString());
         out.println("<TABLE align=left border=0 cellPadding=0");
         out.println("cellSpacing=0 width=840 style=\"margin-left:2px\">");
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

            out.println("<TD WIDTH=5></TD>\n");
            out.println("<TD WIDTH=90>" + formatOutput(session,rset.getString("IDENTITY"),11)+"</TD>");
            out.println("<TD WIDTH=95>" + formatOutput(session,rset.getString("ALIAS"),11)+ "</TD>");
            out.println("<TD WIDTH=30>" + formatOutput(session,rset.getString("SEX"),3) + "</TD>");
            out.println("<TD WIDTH=105>" + formatOutput(session,rset.getString("TC_BIRTH_DATE"),12) + "</TD>");

            out.println("<TD WIDTH=95>" + formatOutput(session,rset.getString("FIDENTITY"),11) + "</TD>");
            out.println("<TD WIDTH=95>" + formatOutput(session,rset.getString("MIDENTITY"),11) +"</TD>");
            out.println("<TD WIDTH=80>" + formatOutput(session,rset.getString("USR"),10)  +"</TD>");
            out.println("<TD WIDTH=140>" + formatOutput(session,rset.getString("TC_TS"),16) + "</TD>");


            out.println("<TD WIDTH=55><A HREF=\"" +getServletPath("viewInd/details?iid=")
                        + rset.getString("IID")
                        + "&mid=" + rset.getString("IID")
                        + "&" + oldQS + "\" target=\"content\">Details</A></TD>");


            out.println("<TD WIDTH=50>");

            out.println(privDependentString(currentPrivs,IND_W,
                                            /*if true*/"<A HREF=\"" +getServletPath("viewInd/edit?iid=")
                                            + rset.getString("IID")+ "&mid=" + rset.getString("IID")
                                            + "&" + oldQS + "\" target=\"content\">Edit</A></TD></TR>",
                                            /*if false*/"<font color=tan>&nbsp Edit</font></TD>") );


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
    * Writes the details page. The page contains the current individual
    * information as well as all history data.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @exception ServletException if an error occurs.
    * @exception IOException if an error occurs.
    */
   private void writeDetails(HttpServletRequest request,
                             HttpServletResponse response) 
      throws ServletException, IOException
   {
      
      HttpSession session = request.getSession(true);
      PrintWriter out = response.getWriter();

      // The individual to look for and the project to look for
      // the individual in.
      String individualId = request.getParameter("iid");
      String projectId = (String) session.getAttribute("PID");

      // Set content type and other response header fields first
      response.setContentType("text/html");
      Connection connection =  null;

      // Statements for getting current and previous data.
      Statement currentStmt = null;
      Statement previousStatement = null;
      
      // Result sets for storing current/previous data.
      ResultSet currResult = null;
      ResultSet prevResult = null;

      // Indicates that previous data for individual was found.
      boolean hasPrevData = false;

      // Object storing current and previous individual data.
      Individual currentData = new Individual();
      Individual previousData = new Individual();
      
      try
      {
         connection = (Connection) session.getAttribute("conn");

         // Build statement to get current data of individual and store
         // result in result set
         currentStmt = connection.createStatement();
         String strSQL = "SELECT IID, IDENTITY, ALIAS, SEX, SUNAME, "
            + "to_char(BIRTH_DATE, 'YYYY-MM-DD') as TC_BIRTH_DATE, COMM, "
            + "FIDENTITY, MIDENTITY, USR, to_char(TS, '" +
            getDateFormat(session) + "') as TC_TS, STATUS "
            + "FROM gdbadm.V_INDIVIDUALS_3 WHERE "
            + "IID = " + individualId;
         currResult = currentStmt.executeQuery(strSQL);

         // Build statement to get previous data of individual and store
         // result in result set
         previousStatement = connection.createStatement();
         strSQL = "SELECT IID, IDENTITY, ALIAS, SEX, "
            + "to_char(BIRTH_DATE, 'YYYY-MM-DD') as TC_BIRTH_DATE, COMM, "
            + "FIDENTITY, MIDENTITY, USR, to_char(TS, '" +
            getDateFormat(session) + "') as TC_TS, TS as dummy , STATUS "
            + "FROM gdbadm.V_INDIVIDUALS_LOG WHERE "
            + "IID = " + individualId + " order by dummy desc";
         prevResult = previousStatement.executeQuery(strSQL);

         // Copy current data from result set to object
         if (currResult.next())
         {
            currentData = initIndividualFromView3andLog(currentData,
                                                        currResult);
            currentData.replaceNull();
            
         }

         // Copy previous data from result set to object
         if (prevResult.next())
         {
            previousData = initIndividualFromView3andLog(previousData,
                                                         prevResult);
            hasPrevData = true;
         }

         // Write the start of the page, ie doctype, header and CSS
         HTMLWriter.doctype(out);
         HTMLWriter.openHTML(out);
         HTMLWriter.openHEAD(out, "Details");
         HTMLWriter.defaultCSS(out);
         HTMLWriter.closeHEAD(out);

         HTMLWriter.openBODY(out, "");

         // Write the header table
         HTMLWriter.headerTable(out, 0,
                                Errors.keyValue("Individuals.Details"));

         // Write the start of the content table
         HTMLWriter.contentTableStart(out, 0);

         // This page is split in a table with four rows. Write the start
         // of the table.
         HTMLWriter.comment(out, "This page contains one table with " + 
                            "four rows", true, false);
         out.println("<TABLE width=\"100%\">");

         HTMLWriter.comment(out, "Page table, r1: static data table", true,
                            false);
         out.println("<TR>\n" +
                     "  <TD>");
         
         out.println("    <TABLE border=0 cellSpacing=3>\n" +
                     "    <TR>\n" +
                     "      <TD nowrap colspan=2 bgcolor=lightgrey>\n" +
                     "        <FONT size=\"+1\">Static data</FONT>\n" +
                     "      </TD>\n" +
                     "    </TR>\n" +
                     "    <TR>\n" +
                     "      <TD nowrap>Sampling Unit: </TD>\n" +
                     "      <TD nowrap>" +
                     formatOutput(session, currResult.getString("SUNAME"),20) + 
                     "</TD>\n" +
                     "    </TR>\n" +
                     "    </TABLE>");
         
         out.println("  </TD>\n" +
                     "</TR>");

         HTMLWriter.comment(out, "Page table, r2: empty row", true, false);
         out.println("<TR>\n" +
                     "  <TD>&nbsp;</TD>\n" +
                     "</TR>");
         
         HTMLWriter.comment(out, "Page table, r3: data table", true, false);
         out.println("<TR>\n" +
                     "  <TD>");
                           
         out.println("    <TABLE align=center border=0 cellSpacing=0 width=800px>");

         HTMLWriter.comment(out, "Data table, r1: table header", true, false);
         out.println("    <TR bgcolor=Black>\n" +
                     "      <TD align=center colspan=10 nowrap>\n" +
                     "        <FONT color=\"#ffffff\">Current Data</FONT>\n" +
                     "      </TD>\n" +
                     "    </TR>");

         HTMLWriter.comment(out, "Data table, r2: column headers", true, false);
         out.println("    <TR bgcolor= \"#008B8B\">\n" +
                     "      <TD nowrap>Identity</TD>\n" +
                     "      <TD nowrap>Alias</TD>\n" +
                     "      <TD nowrap>Father</TD>\n" +
                     "      <TD nowrap>Mother</TD>\n" +
                     "      <TD nowrap>Sex</TD>\n" +
                     "      <TD nowrap>Birth date</TD>\n" +
                     "      <TD nowrap>Status</TD>\n" +
                     "      <TD nowrap>Comment</TD>\n" +
                     "      <TD nowrap>Last updated by</TD>\n" +
                     "      <TD nowrap>Last updated</TD>\n" +
                     "    </TR>");

         HTMLWriter.comment(out, "Data table, r3: current data", true, false);
         writeIndividualDetailRow(out, currentData, previousData, true);
         
         HTMLWriter.comment(out, "Data table, r4: history header", true, false);
         out.println("    <TR bgcolor=Black>\n" +
                     "      <TD align=center colspan=10>\n" +
                     "        <FONT color=\"#ffffff\">History</FONT>\n" +
                     "      </TD>\n" +
                     "    </TR>");


         HTMLWriter.comment(out, "Start history rows", true, false);
         boolean oddRow = true;
         // Loop all previous data and print each data version on a
         // separate row. 
         while (prevResult.next())
         {
            // Copy previous data to current data
            currentData.copy(previousData);
            currentData.replaceNull();
            
            // Get previous data
            previousData = initIndividualFromView3andLog(previousData,
                                                         prevResult);
            
            // Write the history values of the individual
            writeIndividualDetailRow(out, currentData, previousData, oddRow);

            oddRow = !oddRow;
         }

         // There is one version of data left to print, so print it. First
         // copy it to current object
         currentData.copy(previousData);
         currentData.replaceNull();
         
         // Now print it 
         writeIndividualDetailRow(out, currentData, previousData, oddRow);
            
         HTMLWriter.comment(out, "Data table, end", false, true);
         out.println("    </TABLE>\n" +
                     "  </TD>\n" +
                     "</TR>");

         // Create an individual and get the groupings and groups the
         // individual is a member of.
         DbIndividual dbInd = new DbIndividual();
         ValueHolder valueHolder = dbInd.calcMembership(connection,
                                                       Integer.parseInt(projectId),
                                                       individualId);
         Vector groupings = (Vector) valueHolder.o1;
         Vector group = (Vector) valueHolder.o2; 

         HTMLWriter.comment(out, "Page table, r4: empty row", true, false);
         out.println("<TR>\n" +
                     "  <TD>&nbsp;</TD>\n" +
                     "</TR>");

         HTMLWriter.comment(out, "Page table, r5: membership table", true, false);
         out.println("<TR>\n" +
                     "  <TD>");
         
         out.println("<P>\n" +
                     "    <TABLE align=left border=0 cellSpacing=0 width=400px>");

         HTMLWriter.comment(out, "Membership table, r1: table header",
                            true, false);
         out.println("    <TR bgcolor=black>\n" +
                     "      <TD nowrap colspan=2 align=center>\n" +
                     "        <FONT color=\"#fffff\">Membership</FONT>\n" +
                     "      </TD>\n" +
                     "    </TR>");

         HTMLWriter.comment(out, "Membership table, r2: column header",
                            true, false);
         out.println("    <TR bgcolor=\"#008B8B\">\n" +
                     "      <TD width=\"50%\">Grouping</TD>\n" +
                     "      <TD>Group</TD>\n" +
                     "    </TR>");
         HTMLWriter.comment(out, "Membership rows", true, false);
         oddRow = true;
         for (int i = 0; i < groupings.size() && i < group.size(); i++)
         {
            // Different color on odd/even rows.
            if (oddRow)
            {
               out.println("    <TR bgcolor=white>");
            }
            else {
               out.println("    <TR bgcolor=lightgrey>");
            }
            oddRow = !oddRow;
            
            out.println("      <TD>" + (String) groupings.elementAt(i) + "</TD>\n" +
                        "      <TD>" + (String) group.elementAt(i) + "</TD>\n" + 
                        "    </TR>");
         }
         
         HTMLWriter.comment(out, "Membership table, end", false, true);
         out.println("    </TABLE>\n" +
                     "  </TD>\n" +
                     "</TR>");

         HTMLWriter.comment(out, "Page table, r6: empty row", true, false);
         out.println("<TR>\n" +
                     "  <TD>&nbsp;</TD>\n" +
                     "</TR>");

         HTMLWriter.comment(out, "Page table, r7: buttons table", true, false);
         out.println("<TR>\n" +
                     "  <TD>");
         
         out.println("<P>\n" +
                     "    <TABLE cellspacing=0 cellpadding=0 border=0>\n" +
                     "    <TR>\n" +
                     "      <TD>\n" + 
                     "        <FORM>\n" +
                 /*    HTMLWriter.backButton("location.href=\"" +
                                           getServletPath("viewInd?") +
                                           buildQS(request) + "\"\n") +
                  */
                        HTMLWriter.backButton("location.href=\"" +
                                           //getServletPath("viewInd?&RETURNING=YES") +
                                           getPrevURL(request) +
                                            "\"\n") +

                     "        </FORM>\n" +
                     "      </TD>\n" +
                     "    </TR>\n" +
                     "    </TABLE>");

         HTMLWriter.comment(out, "Page table, end", false, true);
         out.println("  </TD>\n" +
                     "</TR>\n" +
                     "</TABLE>");

         // End content table
         HTMLWriter.contentTableEnd(out);
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
            if (currResult != null)
            {
               currResult.close();
            }
            if (prevResult != null)
            {
               prevResult.close();
            }
            if (currentStmt != null)
            {
               currentStmt.close();
            }
            if (previousStatement != null)
            {
               previousStatement.close();
            }
         }
         catch (SQLException ignored)
         {
         }
      }
   }

   
   /**
    * Inits an Individual object from a resultset based on view
    * V_INDIVIDUALS_3 or V_INDIVIDUALS_LOG.
    *
    * @param individual The Individual object to initialize.
    * @param resultSet The result set to read data from.
    * @return The initialized object.
    */
   private Individual initIndividualFromView3andLog(Individual individual,
                                                    ResultSet resultSet)
   {
      try
      {
         individual.identity(resultSet.getString("IDENTITY"));
         individual.alias(resultSet.getString("ALIAS"));
         individual.sex(resultSet.getString("SEX"));
         individual.birthDate(resultSet.getString("TC_BIRTH_DATE"));
         individual.comment(resultSet.getString("COMM"));
         individual.fatherId(resultSet.getString("FIDENTITY"));
         individual.motherId(resultSet.getString("MIDENTITY"));
         individual.userId(resultSet.getString("USR"));
         individual.timeStamp(resultSet.getString("TC_TS"));
         individual.status(resultSet.getString("STATUS"));
         return individual;
      }
      catch (SQLException e)
      {
         e.printStackTrace(System.err);
         return null;
      }
   }
                                           

   /**
    * Writes one row of current individual data. Before data is written,
    * values are compared to the previous version of the data. Any changed
    * values are written in red to indicate a changed value.
    *
    * @param out The PrintWriter to write to.
    * @param currentData The current version of the Individual.
    * @param previousData The previous version of the individual.
    * @param oddRow Are we writing to an odd row or not?
    */
   private void writeIndividualDetailRow(PrintWriter out,
                                           Individual currentData,
                                           Individual previousData,
                                           boolean oddRow)
   {
      // Use different background for odd/even rows
      if (oddRow)
      {
         out.println("    <TR bgcolor=white>");
      }
      else
      {
         out.println("    <TR bgcolor=lightgrey>");
      }
      
      // Write the data.
      writeIndividualDetailCell(out,
                                currentData.identity().equalsIgnoreCase(previousData.identity()),
                                currentData.identity());
      writeIndividualDetailCell(out,
                                currentData.alias().equalsIgnoreCase(previousData.alias()),
                                currentData.alias());
      writeIndividualDetailCell(out,
                                currentData.fatherId().equalsIgnoreCase(previousData.fatherId()),
                                currentData.fatherId());
      writeIndividualDetailCell(out,
                                currentData.motherId().equalsIgnoreCase(previousData.motherId()),
                                currentData.motherId());
      writeIndividualDetailCell(out, 
                                currentData.sex().equalsIgnoreCase(previousData.sex()),
                                currentData.sex());
      writeIndividualDetailCell(out,
                                currentData.birthDate().equalsIgnoreCase(previousData.birthDate()),
                                currentData.birthDate());
      writeIndividualDetailCell(out,
                                currentData.status().equalsIgnoreCase(previousData.status()),
                                currentData.status());
      writeIndividualDetailCell(out,
                                currentData.comment().equalsIgnoreCase(previousData.comment()),
                                currentData.comment());
      
      out.println("      <TD nowrap>" + currentData.userId() + "</TD>");
      out.println("      <TD nowrap>" + currentData.timeStamp() + "</TD>\n" +
                  "    </TR>"); 
   }


   /**
    * Writes one cell of individual data. If the unmodifiedValue parameter is
    * false, value is written in red to indicate a modified value. Otherwise
    * the value is written in black.
    *
    * @param out The PrintWriter to write to.
    * @param unmodifiedValue Is value of cell unmodified?
    * @param cellValue The value of the cell.
    */
   private void writeIndividualDetailCell(PrintWriter out,
                                          boolean unmodifiedValue,
                                          String cellValue)
   {
      if (cellValue == null || cellValue.equals(""))
      {
         cellValue = "&nbsp;";
      }
    
      // If value is modified, add FONT-tag to the value.
      if (!unmodifiedValue)
      {
         cellValue = "\n        <FONT color=red>" + cellValue +
            "</FONT>\n      ";
      }
      
      out.println("      <TD nowrap>" + cellValue + "</TD>");
   }

   
   /***************************************************************************************
    * *************************************************************************************
    * The new ind page
    */
   private void writeNew(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("OPER");
      //  System.err.println("op="+oper);
      conn = (Connection) session.getAttribute("conn");
      if (oper == null) oper = "SEL_CHANGED";
      if (oper.equals("SAVE")) {
         if (createIndividual(req, res, conn))
            writeFrame(req, res);
      } else {
         writeNewPage(req, res);
      }
   }
   private void writeNewPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);

      PrintWriter out = res.getWriter();
      // set content type and other response header fields first
      res.setContentType("text/html");
      Connection conn =  null;
      Statement stmt = null;
      ResultSet rset = null;
      Statement stmt_father = null;
      ResultSet rset_father = null;
      Statement stmt_mother = null;
      ResultSet rset_mother = null;

      try {
         conn = (Connection) session.getAttribute("conn");
         stmt = conn.createStatement();
         String suname, suid;

         suid=req.getParameter("suid");
         //suid = (String) session.getAttribute("SUID");
         String oldQS = buildQS(req);
         // System.err.println("i_qs="+req.getQueryString());

         String strSQL = "SELECT NAME FROM gdbadm.V_SAMPLING_UNITS_1 WHERE "
            + "SUID = " + suid;
         rset = stmt.executeQuery(strSQL);
         rset.next();
         suname = rset.getString("NAME");
         out.println("<html>\n"
                     + "<head>");
         printScript(out);
         out.println(getDateValidationScript());
         out.println("<title>New individual</title>\n"
                     + "</head>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<body>\n");


         // new look
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Individuals - New Individual (in Sampling Unit \""+suname+"\")</b></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");
         out.println("<FORM action=\"" + getServletPath("viewInd/new?") + oldQS + "\" method=get name=\"FORM1\">");

         out.println("<table width=800  border=0>");//align=center

         out.println("<tr><td width=200 >Identity</td>" +
                     "<td width=200>Alias</td></tr>" +
                     "<tr><td width=200><input name=\"identity\" type=\"text\" maxlength=11 style=\"WIDTH: 200px\" value=\"\"></td>"+
                     "<td width=200 ><input name=\"alias\" type=\"text\" maxlength=11 style=\"WIDTH: 200px\" value=\"\"></td></tr>");

         out.println("<tr><td width=200>Sex</td>");
         out.println("<td width=200 >Birth date</td></tr>");
         out.println("<tr><td width=200><select style=\"WIDTH: 200px\" name=\"sex\">");

         out.println("<option selected value=M>Male\n" +
                     "<option value=F>Female"
                     +"<option value=U>Unknown");

         out.println("</select></td>");
         // Birth date
         out.println("<td width=200>");
         out.println("<input type=text maxlength=16 name=birth_date " +
                     "width=200 style=\"WIDTH: 200px\" value=\"\" onBlur='valDate(this, true);'>");
         out.println("</td></tr>");

         // Find possible fathers
         stmt_father = conn.createStatement();
         rset_father = stmt_father.executeQuery(
                                                "SELECT IDENTITY, IID FROM gdbadm.V_INDIVIDUALS_1 " +
                                                "WHERE SUID=" + (String) session.getAttribute("SUID") +
                                                " AND SEX='M'");

         out.println("<tr><td width=200 >Father</td>");
         out.println("<td width=200 >Mother</td></tr>");

         out.println("<tr><td><select name=father style=\"WIDTH: 200px\">");

         while (rset_father.next()) {
            out.println("<option value=\"" + rset_father.getString("IID") + "\">" + rset_father.getString("IDENTITY"));
         }
         out.println("<option selected value=\"\">");
         out.println("</select></td>");
         // Find possible mothers
         stmt_mother = conn.createStatement();
         rset_mother = stmt_mother.executeQuery(
                                                "SELECT IDENTITY, IID FROM gdbadm.V_INDIVIDUALS_1 " +
                                                "WHERE SUID=" + (String) session.getAttribute("SUID") +
                                                " AND SEX='F'");

         out.println("<td><select name=mother style=\"WIDTH: 200px\">");
         while (rset_mother.next()) {
            out.println("<option value=\"" + rset_mother.getString("IID") + "\">" + rset_mother.getString("IDENTITY"));
         }
         out.println("<option selected value=\"\">");
         out.println("</select></td></tr>");

         out.println("<tr><td width=200 >Comment</td></tr>" +
                     "<tr><td colspan=3 width=600>"
                     +"<textarea rows=8 cols=45 name=comment></textarea>"
                     +"</td></tr>");

         //   "<input type=text name=comment maxlength=256 style=\"WIDTH: 600px\" value=\"\"</td></tr>");

         out.println("<tr><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td></tr>" +
                     "<tr><td colspan=4 >" +
                     "<input type=button value=Back onClick='location.href=\"" +
                  //   getServletPath("viewInd?") + oldQS + "\";' " +
                     getServletPath("viewInd?&RETURNING=YES")  + "\";' " +

                     "width=100 style=\"WIDTH: 100px\">&nbsp;"+
                     "<input type=button name=SAVE value=Create style=\"WIDTH: 100px\" onClick='valForm(\"SAVE\")'>&nbsp;");
         //"<input type=reset value=Reset style=\"WIDTH: 100px\">

         out.println("</td></tr>");
         out.println("</table>");


         /*	"<input type=button value=Back onClick='location.href=\"" +
                getServletPath("viewGrouping?") + oldQS + "\";' " +
                "width=100 style=\"WIDTH: 100px\">&nbsp;"+
                "<input type=button id=SAVE name=SAVE value=Create style=\"WIDTH: 100px\""+
                "onClick='valForm(\"CREATE\")'>&nbsp;");
         */



         out.println("<input type=\"hidden\" ID=suid NAME=suid value="+suid+">");
         out.println("<input type=\"hidden\" ID=\"OPER\" NAME=\"OPER\" value=\"\">");
/*
         out.println("<input type=\"hidden\" ID=ACTION NAME=ACTION value=\""+req.getParameter("ACTION")+"\">");
         out.println("<input type=\"hidden\" ID=STARTINDEX NAME=STARTINDEX value=\""+req.getParameter("STARTINDEX")+"\">");
         out.println("<input type=\"hidden\" ID=ROWS NAME=ROWS value=\""+req.getParameter("ROWS")+"\">");
         out.println("<input type=\"hidden\" ID=ORDERBY NAME=ORDERBY value=\""+req.getParameter("ORDERBY")+"\">");
*/
         out.println("<input type=\"hidden\"  NAME=RETURNING value=YES>");

         out.println("</FORM>");
         out.println("</body>\n</html>");
      } catch (Exception e)
      {
         out.println("<PRE>");
         e.printStackTrace(out);
         out.println("</PRE>");
      } finally {
         try {
            if (rset != null) rset.close();
            if (rset_father != null) rset_father.close();
            if (rset_mother != null) rset_mother.close();
            if (stmt != null) stmt.close();
            if (stmt_father  != null) stmt_father.close();
            if (stmt_mother != null) stmt_mother.close();
         } catch (SQLException ignored) {}
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

   /***************************************************************************************
    * *************************************************************************************
    * The Individuals edit page
    */
   private void writeEdit(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      //    System.err.println("op="+oper);

      conn = (Connection) session.getAttribute("conn");
      
      if (oper == null) oper = "";
      if (oper.equals("DELETE")) {
         if (deleteIndividual(req, res, conn))
            writeFrame(req, res);
      } else if (oper.equals("UPDATE")) {
         if (updateIndividual(req, res, conn))
            writeEditPage(req, res);
      } else
         writeEditPage(req, res);
   }
   private void writeEditPage(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      res.setContentType("text/html");
      String iid = req.getParameter("iid");
      String suid;
      PrintWriter out = res.getWriter();
      // set content type and other response header fields first
      res.setContentType("text/html");
      Connection conn =  null;
      Statement stmt = null;
      ResultSet rset = null;
      Statement stmt_father = null;
      ResultSet rset_father = null;
      Statement stmt_mother = null;
      ResultSet rset_mother = null;
      Errors.logDebug("viewInd.writeEditPage(...) edit/inds="+req.getQueryString());
      try {

         conn = (Connection) session.getAttribute("conn");
         stmt = conn.createStatement();
         String oldQS = buildQS(req);

         String strSQL = "SELECT SUID, SUNAME, STATUS, IID, IDENTITY, ALIAS, SEX, "
            + "to_char(BIRTH_DATE, 'YYYY-MM-DD') as TC_BIRTH_DATE, COMM, "
            + "FIDENTITY, MIDENTITY, USR, "
            + "to_char(TS, 'YYYY-MM-DD HH24:MI') as TC_TS " // HIST is obsolute
            + "FROM gdbadm.V_INDIVIDUALS_3 WHERE "
            + "IID = " + iid;

         rset = stmt.executeQuery(strSQL);

         rset.next();

         out.println("<html>\n"
                     + "<head>\n"
                     + "<title>Individual Edit</title>");
         writeEditScript(out);
         out.println(getDateValidationScript());
         out.println("</head>\n");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<body bgcolor=\"fafad2\">\n");
         // new look
         out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
         out.println("<tr>");
         out.println("<td width=14 rowspan=3></td>");
         out.println("<td width=736 colspan=2 height=15>");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Individuals - View & Edit - Edit</b></font></center>");
         out.println("</td></tr>");
         out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
         out.println("</tr></table>");

         // just a "newline"
         out.println("<table><td></td></table>");



         // the whole information table
         out.println("<table width=800>");
         out.println("<tr><td>");
         // static data table
         out.println("<table border=0 cellpading=0 cellspacing=0 align=left width=300");
         out.println("<tr>");
         out.println("<td colspan=2 bgcolor=lightgrey><font size=\"+1\">Static data</font></td>");
         out.println("</tr>");
         out.println("<tr><td>Sampling Unit</td><td>" + formatOutput(session, rset.getString("SUNAME"), 20) + "</td></tr>");
         out.println("<tr><td>Last updated by</td><td>" + formatOutput(session, rset.getString("USR"), 10) + "</td></tr>");
         out.println("<tr><td>Last updated</td><td>" + formatOutput(session, rset.getString("TC_TS"), 18) + "</td></tr>");
         out.println("</table></tr></td>");

         out.println("<FORM action=\"" + getServletPath("viewInd/edit?") + oldQS + "\" method=\"get\" name=\"FORM1\">");

         // dynamic data table
         out.println("<tr><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0 width=600>");
         out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Changable data</font></td></tr>");

         out.println("<tr><td width=200 align=left>Identity</td>");
         out.println("<td width=200 align=left>Alias</td></tr>");

         out.println("<tr><td width=200><input name=identity type=text maxlength=12 " +
                     "width=200 style=\"WIDTH: 200px\" value=\"" + replaceNull(rset.getString("IDENTITY"), "") +
                     "\"></td>");
         out.println("<td width=200><input name=alias type=text maxlength=12 " +
                     "width=200 style=\"WIDTH: 200px\" value=\"" + replaceNull(rset.getString("ALIAS"), "") +
                     "\"></td></tr>");

         out.println("<tr><td width=200 align=left>Sex</td>" +
                     "<td width=200 align=left>Status</td></tr>" +

                     "<tr><td width=200><select style=\"WIDTH: 200px\" name=\"sex\">");

         if (rset.getString("SEX").equalsIgnoreCase("M")) {
            out.println("<option selected value=M>Male\n" +
                        "<option value=F>Female"
                        +"<option value=U>Unknown");

         }
         if (rset.getString("SEX").equalsIgnoreCase("F")) {

            out.println("<option value=M>Male\n" +
                        "<option selected value=F>Female"
                        +"<option value=U>Unknown");
         }
         if (rset.getString("SEX").equalsIgnoreCase("U")) {

            out.println("<option value=M>Male\n" +
                        "<option selected value=U>Unknown"
                        +"<option value=F>Female");
         }

         out.println("</select></td>");

         // status

         out.println("<td width=200><select style=\"WIDTH: 200px\" name=\"status\">");
         if (rset.getString("STATUS").equalsIgnoreCase("E")) {
            out.println("<option selected value=E>Enabled\n" +
                        "<option value=D>Disabled");
         } else {
            out.println("<option value=E>Enabled\n" +
                        "<option selected value=D>Disabled");
         }
         out.println("</select></td></tr>");
         suid = rset.getString("SUID");
         // Find possible fathers
         stmt_father = conn.createStatement();
         String sql = "SELECT IDENTITY, IID FROM gdbadm.V_INDIVIDUALS_1 " +
                        "WHERE SUID=" + suid + " AND IID!=" + iid + " AND SEX='M' " +
                        (rset.getString("TC_BIRTH_DATE") != null ?
                        "AND BIRTH_DATE < to_date('" + rset.getString("TC_BIRTH_DATE") + "', 'YYYY-MM-DD')" :
                        "" ) +
                        " order by IDENTITY";
         Errors.logDebug("Male sql="+sql);
         rset_father = stmt_father.executeQuery(sql);

         out.println("<tr><td width=200 align=left>Father</td>");
         out.println("<td width=200 align=left>Mother</td></tr>");

         out.println("<tr><td><select name=father style=\"WIDTH: 200px\">");
         String curr_father;
         if (rset.getString("FIDENTITY") == null)  // No current father
            curr_father = new String("");
         else
            curr_father = rset.getString("FIDENTITY");
         while (rset_father.next()) 
         {
            if (curr_father.equalsIgnoreCase(rset_father.getString("IDENTITY"))) 
            {
               out.println("<option selected value=\"" + rset_father.getString("IID") + "\">" + rset_father.getString("IDENTITY"));
            } 
            else 
            {
               out.println("<option value=\"" + rset_father.getString("IID") + "\">" + rset_father.getString("IDENTITY"));
            }
         }
         if (curr_father.equalsIgnoreCase(""))
            out.println("<option selected value=\"\">");
         else
            out.println("<option value=\"\">");

         out.println("</select></td>");
         // Find possible mothers
         out.println("<td><select name=mother style=\"WIDTH: 200px\">");
         stmt_mother = conn.createStatement();
         rset_mother = stmt_mother.executeQuery(
                                                "SELECT IDENTITY, IID FROM gdbadm.V_INDIVIDUALS_1 " +
                                                "WHERE SUID=" + suid + " AND IID!=" + iid + " AND SEX='F' " +
                                                (rset.getString("TC_BIRTH_DATE") != null ?
                                                 "AND BIRTH_DATE < to_date('" + rset.getString("TC_BIRTH_DATE") + "', 'YYYY-MM-DD')" :
                                                 "" ) +
                                                " order by IDENTITY");

         String curr_mother;
         if (rset.getString("MIDENTITY") == null)  // No current mother
            curr_mother = new String("");
         else
            curr_mother = rset.getString("MIDENTITY");
         while (rset_mother.next()) {
            if (curr_mother.equalsIgnoreCase(rset_mother.getString("IDENTITY"))) {
               out.println("<option selected value=\"" + rset_mother.getString("IID") + "\">" + rset_mother.getString("IDENTITY"));
            } else {
               out.println("<option value=\"" + rset_mother.getString("IID") + "\">" + rset_mother.getString("IDENTITY"));
            }
         }
         if (curr_mother.equalsIgnoreCase(""))
            out.println("<option selected value=\"\">");
         else
            out.println("<option value=\"\">");

         out.println("</select></td></tr>");

         // Birth date
         out.println("<tr><td width=200 align=left>Birth date</td></tr>");
         out.println("<tr><td width=200>");
         out.println("<input type=text maxlength=16 name=birth_date " +
                     "width=200 style=\"WIDTH: 200px\" value=\"" +
                     replaceNull(rset.getString("TC_BIRTH_DATE"), "") +
                     "\" onBlur='valDate(this, true);'></td></tr>");

         out.println("<tr><td width=200 align=left>Comment</td></tr>");
         out.println("<tr><td colspan=3>");
         out.println("<input type=text name=comment maxlength=256 " +
                     "width=600 style=\"WIDTH: 600px\" value=\"" +
                     replaceNull(rset.getString("COMM"), "") + "\"</td></tr>");

         out.println("</table></td></tr>");

         // buttons table
         out.println("<tr><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0 width=300>");
         out.println("<tr><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td></tr>" +
                     "<tr><td colspan=4 align=center>" +
                     "<input type=button style=\"WIDTH: 100px\" value=\"Back\" onClick='location.href=\""
//                     +getServletPath("viewInd?") + oldQS + "\"'>&nbsp;"+
                     +getServletPath("viewInd?&RETURNING=YES") + "\"'>&nbsp;"+

                     "<input type=reset value=Reset style=\"WIDTH: 100px\">&nbsp;"+
                     "<input type=button id=DELETE name=DELETE value=Delete style=\"WIDTH: 100px\" onClick='valForm(\"DELETE\")'>&nbsp;"+

                     "<input type=button id=UPDATE name=UPDATE value=Update style=\"WIDTH: 100px\" onClick='valForm(\"UPDATE\")'>&nbsp;");

         out.println("</td></tr>");
         out.println("</table></td></tr>");
         //


         // Store some extra information needed by doPost()
         out.println("<input type=hidden NAME=oper value=\"\">");
         out.println("<input type=hidden NAME=iid value=\"" + iid + "\">");
/*
          out.println("<input type=\"hidden\" ID=ACTION NAME=ACTION value=\""+req.getParameter("ACTION")+"\">");
         out.println("<input type=\"hidden\" ID=STARTINDEX NAME=STARTINDEX value=\""+req.getParameter("STARTINDEX")+"\">");
         out.println("<input type=\"hidden\" ID=ROWS NAME=ROWS value=\""+req.getParameter("ROWS")+"\">");
         out.println("<input type=\"hidden\" ID=ORDERBY NAME=ORDERBY value=\""+req.getParameter("ORDERBY")+"\">");
*/
       out.println("<input type=\"hidden\"  NAME=RETURNING value=YES>");

         out.println("</table>");/*end of data table*/
         out.println("</FORM>");
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
            if (rset_father != null) rset_father.close();
            if (rset_mother != null) rset_mother.close();
            if (stmt != null) stmt.close();
            if (stmt_father  != null) stmt_father.close();
            if (stmt_mother != null) stmt_mother.close();
         } catch (SQLException ignored) {}
      }

   }


   /**
    * Creates a new individual.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if individual was created.
    *         False if individual was not created
    */
   private boolean createIndividual(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Connection connection)
   {
      boolean isOk = true;
      String errMessage = null;
      try
      {
         HttpSession session = request.getSession(true);
         int iid, id, suid;
         String identity=null , alias=null, sex=null, father = null;
         String  mother = null, comm = null, birth_date;

         connection.setAutoCommit(false);
         id = Integer.parseInt((String) session.getAttribute("UserID"));
         suid = Integer.parseInt(request.getParameter("suid"));

         identity= request.getParameter("identity");
         alias = request.getParameter("alias");
         sex= request.getParameter("sex");
         birth_date= request.getParameter("birth_date");
         father = request.getParameter("father");
         mother = request.getParameter("mother");
         comm = request.getParameter("comm");

         DbIndividual dbi = new DbIndividual();
         dbi.CreateIndividual(connection, id, identity, alias, father,
                              mother, sex, birth_date, comm, suid); 

         errMessage = dbi.getErrorMessage();
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
                       "Individuals.New.Create", errMessage, "viewInd",
                       isOk); 
      return isOk;
   }

   
   /**
    * Deletes an individual.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param connection The connection object to use.
    * @return True if individual was deleted.
    *         False if individual was not deleted.
    */
   private boolean deleteIndividual(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Connection connection)
   {
      String errMessage = null;
      boolean isOk = true;
      try
      {
         HttpSession session = request.getSession(true);
         int mid, iid;
         connection.setAutoCommit(false);
         String UserID = (String) session.getAttribute("UserID");
         iid = Integer.parseInt(request.getParameter("iid"));
         DbIndividual dbi = new DbIndividual();
         dbi.DeleteIndividual(connection, iid);
         errMessage = dbi.getErrorMessage();
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
                       "Individuals.Edit.Delete", errMessage, "viewInd",
                       isOk); 
      return isOk;
   }


   private boolean updateIndividual(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Connection connection)
   {
      String errMessage = null;
      boolean isOk = true;
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         String UserID = (String) session.getAttribute("UserID");
         String father, mother, birth_date,identity ,alias , status, sex,comm;
         String oldQS = request.getQueryString();
         int mid, iid, level;
         //mid = Integer.parseInt(request.getParameter("mid"));
         iid = Integer.parseInt(request.getParameter("iid"));
         father = request.getParameter("father");
         mother = request.getParameter("mother");
         birth_date = request.getParameter("birth_date");
         alias = request.getParameter("alias");
         identity = request.getParameter("identity");
         comm = request.getParameter("comment");
         sex = request.getParameter("sex");
         status = request.getParameter("status");

         DbIndividual dbi = new DbIndividual();

         dbi.UpdateIndividual(connection,Integer.parseInt(UserID), iid, identity,
                              alias, father, mother,sex,status, birth_date,comm);
         errMessage = dbi.getErrorMessage();
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
                       "Individuals.Edit.Update", errMessage, "viewInd",
                       isOk);
      return isOk;
   }


   private void writeEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to delete the Individual?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("   if (!valDate(document.forms[0].birth_date, true))");
      out.println("     return false;");
      out.println("		if (confirm('Are you sure you want to update the Individual?')) {");
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
      String fid, msid, oldQS, pid, sql;
      Statement stmt = null;
      ResultSet rset = null;
      PrintWriter out = null;
      try {
         Connection conn = (Connection) session.getAttribute("conn");
         fid = req.getParameter("fid");
         msid = req.getParameter("msid");
         pid = (String) session.getAttribute("PID");
         if (fid == null) fid = "-1";
         if (msid == null) msid = "-1";
         res.setContentType("text/html");
         out = res.getWriter();
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<title>Genotype completion</title>");
         out.println("</head>");
         out.println("<body>");

         out.println("<div style=\"POSITION: absolute; TOP: 5px; LEFT: 360px\"><H3>Genotype completion</H3></div>");
         out.println("<FORM method=get action=\"../viewGeno/completion\" name=\"FORM1\">");
         out.println("<br><br><table border=0 width=400>");
         // Filters
         out.println("<tr>");
         sql = "SELECT FID, NAME FROM gdbadm.V_FILTER_1 WHERE " +
            "PID=" + pid;
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
         // Marker Set
         stmt = conn.createStatement();
         sql = "SELECT MSID, NAME FROM gdbadm.V_MARKER_SETS_1 WHERE " +
            "PID=" + pid;
         rset = stmt.executeQuery(sql);
         out.println("<td width=120 style=\"WIDTH: 120px\">" +
                     "Marker set<br><select name=\"msid\" width=120 style=\"WIDTH: 120px\">");
         while (rset.next()) {
            if (rset.getString("MSID").equals(msid))
               out.println("<option selected value=\"" + rset.getString("MSID") + "\">");
            else
               out.println("<option value=\"" + rset.getString("MSID") + "\">");
            out.println(rset.getString("NAME"));
         }
         out.println("</select>");
         rset.close();
         stmt.close();
         out.println("<td>");
         out.println("&nbsp;<br><input type=submit name=\"submit1\" value=\"Display\">");
         out.println("</table>");

         out.println("</form>");
         out.println("<hr>");

         // The completion data
         if (fid != null && msid != null &&
             req.getParameter("submit1") != null) {
            writeCompletionTable(conn, out, pid, fid, msid);
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
                                     String msid) {

      Statement stmt = null;
      ResultSet rset = null;
      Statement stmt_markers = null;
      ResultSet rset_markers = null;
      int inds = 0;
      int markers = 0;
      String gqlExpr = null, filter = null;
      Vector mids = new Vector(100);
      String sid = null;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SID FROM gdbadm.MARKER_SETS_2 WHERE MSID=" + msid);
         rset.next();
         sid = rset.getString("SID");

         // The number of markers and individuals:
         rset = stmt.executeQuery("SELECT EXPRESSION FROM " +
                                  "gdbadm.FILTER_1 WHERE FID=" + fid);
         rset.next();
         gqlExpr = rset.getString("EXPRESSION");
         //	public GqlTranslator(String pid, String sid, String suid, String gqlstring, Connection conn) {
         /* tar bort detta tillf�lligt /roca
            GqlTranslator gqlt = new GqlTranslator(pid, sid, gqlExpr, conn);
            gqlt.translate();
            filter = gqlt.getFilter();
         */
         rset = stmt.executeQuery("SELECT COUNT(*) INDS " + filter);
         rset.next();
         inds = rset.getInt("INDS");
         out.println("<p>Filter returns " + inds + " individuals.</p>");

         rset = stmt.executeQuery("SELECT MID FROM gdbadm.V_POSITION_1 WHERE MSID=" + msid);
         while (rset.next()) {
            mids.addElement(rset.getString("MID"));
            markers++;
         }

         out.println("<p>Markerset contains " + markers + " markers</p>");

         out.println("<table cellPadding=0 cellSpacing=0 width=500 style=\"WIDTH: 460px\">");
         out.println("<tr bgcolor=\"lightskyblue\">");
         out.println("<td width=120 style=\"WIDTH: 120px\">Marker");
         out.println("<td width=120 style=\"WIDTH: 120px\">Percentage");
         out.println("<td width=120 style=\"WIDTH: 120px\">individuals");
         out.println("<td width=120 style=\"WIDTH: 120px\">&nbsp;"); // for hyperlinks
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
            out.println("<td width=120 style=\"WIDTH: 120px\">" + (int) 100*rset.getInt("GENOS")/inds + "%");
            out.println("<td width=120 style=\"WIDTH: 120px\">" + rset.getInt("GENOS") + "");
            out.println("<td width=120 style=\"WIDTH: 120px\">&nbsp;");

         }
         out.println("</table>");
      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
      } finally {
         try {
            if (rset_markers != null) rset_markers.close();
            if (stmt_markers !=null) stmt_markers.close();
         } catch (SQLException ignored) {
         }
      }
   }


   private void printScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('SAVE' == action.toUpperCase()) {");
      out.println("   if (!valDate(document.forms[0].birth_date, true) ) {");
      out.println("     return false;");
      out.println("   }");
      out.println("		if (confirm('Are you sure you want to create the individual?')) {");
      out.println("			document.forms[0].OPER.value='SAVE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	}");

      out.println("	else if ('UPDATE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the individual?')) {");
      out.println("			document.forms[0].OPER.value='UPDATE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	}");

      out.println("	else if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to update the individual?')) {");
      out.println("			document.forms[0].OPER.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");


      out.println("	} else {");

      out.println("		document.forms[0].OPER.value='';");
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

   private boolean authorized(HttpServletRequest req, HttpServletResponse res) 
   {
      HttpSession session = req.getSession(true);
      String extPath = req.getPathInfo();
      boolean ok = true;
      String title = "";
      int privileges[] = (int[]) session.getAttribute("PRIVILEGES");
      try {
         if (extPath == null || extPath.trim().equals("") ) extPath = "/";
         if (extPath.equals("/") ||
             extPath.equals("/bottom") ||
             extPath.equals("/middle") ||
             extPath.equals("/top") ||
             extPath.equals("/details") ) {
            // We neew the privilege ind_R for all these
            title = "Individuals -  View & Edit";
            if ( privDependentString(privileges, IND_R, "", null) == null )
               ok = false;
         } else if (extPath.equals("/edit") ) {
            // We neew the privilege IND_W
            title = "Individuals - View & Edit - Edit";
            if ( privDependentString(privileges, IND_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/new") ) {
            // We neew the privilege IND_W
            title = "Individuals - View & Edit - New";
            if ( privDependentString(privileges, IND_W, "", null) == null)
               ok = false;
         } else if (extPath.equals("/impFile") ) {
            // We need the privilege IND_W
            title = "Individuals -  File Import";
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
      } catch (Exception e)	{
         e.printStackTrace(System.err);
      } finally {
         ;
      }
   }

   
}

