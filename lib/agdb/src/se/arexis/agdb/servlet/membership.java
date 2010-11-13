/*
  $Log$
  Revision 1.7  2005/02/07 15:54:01  heto
  Converted DbIndividual to PostgreSQL
  Now some transaction problem occures with Groupings (update)

  Revision 1.6  2005/02/04 15:58:40  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.5  2004/03/08 10:23:58  wali
  took away some resultsets and statements

  Revision 1.4  2004/03/02 14:03:24  wali
  Membership in groups changed so the individuals that are included in the group is not shown in the "available individuals" window.

  Revision 1.3  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.2  2003/04/25 12:14:46  heto
  Changed all references to axDefault.css
  Source layout fixes.

  Revision 1.1.1.1  2002/10/16 18:14:04  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.4  2002/01/31 15:54:05  roca
  Additional fixes of javascript for mac, Genotype import mm

  Revision 1.3  2001/05/09 06:37:02  frob
  Indented the file and added log header.

*/

package se.arexis.agdb.servlet;


import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.util.*;
import se.arexis.agdb.db.*;

public class membership extends SecureArexisServlet
{
   public void doGet(HttpServletRequest req, HttpServletResponse res) {
      doPost(req, res);
   }
   public void doPost(HttpServletRequest req, HttpServletResponse res) {
      try {
         /*
           HttpSession session = req.getSession(true);
           String strUser = (String) session.getValue("UserID");
           Boolean bLoginOk = (Boolean) session.getValue("LoginOk");
           if (strUser == null || strUser.equalsIgnoreCase("") ||
           bLoginOk == null || bLoginOk.booleanValue() != true) {
           res.sendRedirect("/servlets/redirectClass");
           }
         */		
         dispatchPage(req, res);
         return;
      } catch (Exception e) {
         e.printStackTrace(System.err);
      }		
		
   }
   private void dispatchPage(HttpServletRequest req, HttpServletResponse res) 
      throws IOException	{
      String page = req.getParameter("page");
      if (page == null || page.trim().equals("")) {
         page = "MEMBERSHIP";
      }
		
      if (page.equalsIgnoreCase("MEMBERSHIP"))
         writeMember(req, res);
      else
         //			writeError(req, res);
         ;
   }
   private void writeMember(HttpServletRequest req, HttpServletResponse res)
      throws IOException 
   {
      String oper = null;
      HttpSession session = req.getSession(true);
      //Statement stmt = null;
      //		ResultSet rset = null;
      Connection conn = (Connection) session.getValue("conn");
      String suid, gsid, gid, pid, UserId;
      suid = req.getParameter("suid");
      gsid = req.getParameter("gsid");
      gid = req.getParameter("gid");
      pid = (String) session.getValue("PID");
      oper = req.getParameter("oper");
      UserId = (String) session.getValue("UserID");

      res.setContentType("text/html");
      res.setHeader("Pragme", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      if (pid == null) pid = "-1";

      if (suid == null) {
         suid = (String) session.getValue("SUID");
         gsid = null;
         gid = null;
         session.removeValue("GSID");
         session.removeValue("GID");
      } else if (!suid.equals((String) session.getValue("SUID"))) {
         // sampling unit has changed!
         session.putValue("SUID", suid);
         gsid = null;
         gid = null;
         session.removeValue("GSID");
         session.removeValue("GID");
      } else {
         // Sampling units hasn't changed
         if (gsid == null) {
            gsid = (String) session.getValue("GSID");
            gid = null;
         } else if (!gsid.equals((String) session.getValue("GSID"))) {
				// Grouping has changed
            session.putValue("GSID", gsid);
            gid = null;
         }
      }		

      if (suid == null) {
         suid = findSamplingUnit(conn, pid);
         session.putValue("SUID", suid);
         gsid = findGrouping(conn, suid);
         session.putValue("GSID", gsid);
         gid = findGroup(conn, gsid);
      } else if (gsid == null) {
         gsid = findGrouping(conn, suid);
         gid = findGroup(conn, gsid);
      } else if (gid == null)
         gid = findGroup(conn, gsid);

      if (oper == null)
         oper = "SELECT";

      out.println("<html>");
      out.println("<head><title>Group membership</title>");
      HTMLWriter.css(out,getURL("style/axDefault.css"));
      writeScript(out);
      out.println("</head>");
      out.println("<body>");

      // new look
      out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
      out.println("<tr>");
      out.println("<td width=14 rowspan=3></td>");
      out.println("<td width=736 colspan=2 height=15>");
      out.println("<center>" +
                  "<b style=\"font-size: 15pt\">Groups - Membership </b></center>");
      out.println("</td></tr>");
      out.println("<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>");
      out.println("</tr></table>");
      ///

      /* old look
         out.println("<table width=\"100%\" border=0 cellspacing=0 style=\"LEFT-MARGIN: 0px; RIGHT-MARGIN: 0px; TOP-MARGIN: 0px; WIDTH: 100%\">");
         out.println("<tr><td width=100%><center><h2>Group membership</h2></center></td></tr>");
         out.println("</table>");
      */
      if ( (oper.equalsIgnoreCase("ADD_INDIVIDUALS") || oper.equalsIgnoreCase("REM_INDIVIDUALS")) &&
          !gid.equals("-1") && !gsid.equals("-1")) 
      {
         try 
         {
            //boolean ok = true;
            conn.setAutoCommit(false);
            DbIndividual dbInd = new DbIndividual();
                    
            if (oper.equalsIgnoreCase("ADD_INDIVIDUALS")) 
            {
               // Add individuals to a grouping
               String[] inds = req.getParameterValues("avail_inds");
               if (inds != null) 
               {
                  for (int i = 0; i < inds.length; i++) 
                  {
                      dbInd.createGroupLink(conn, Integer.valueOf(inds[i]).intValue(),
                                Integer.valueOf(gid).intValue(), 
                                Integer.valueOf(UserId).intValue());
                  }
               }
            }
            else if (oper.equalsIgnoreCase("REM_INDIVIDUALS")) 
            {
               // Remove individuals from a grouping
               String[] inds = req.getParameterValues("incl_inds");
               if (inds != null) 
               {
                  for (int i = 0; i < inds.length; i++) 
                  {
                      dbInd.DeleteGroupLink(conn, Integer.valueOf(inds[i]).intValue(), 
                              Integer.valueOf(gid).intValue());
                  }
               }
            }
            writeMemberPage(conn, req, oper, pid, suid, gsid, gid, out);
         } 
         catch (Exception e) 
         {
            try 
            {
               conn.rollback();
               out.println("<pre>Error:\nUnhandled database exception!\n"
                           + e.getMessage()
                           +"\nEnd error.</pre>");
               e.printStackTrace(System.err);
            } 
            catch (SQLException ignored) 
            {} 
         }
      } else if (oper.equalsIgnoreCase("SELECT") ||
                 oper.equalsIgnoreCase("DISPLAY")) {
         writeMemberPage(conn, req, oper, pid, suid, gsid, gid, out);
      } else {
         writeMemberPage(conn, req, oper, pid, suid, gsid, gid, out);
      }

      out.println("</body>");
      out.println("</html>");
   }


   private void writeMemberPage(Connection conn,
                                HttpServletRequest req,
                                String oper,
                                String pid,
                                String suid,
                                String gsid,
                                String gid,
                                PrintWriter out) {
      Statement stmt = null;
      ResultSet rset = null;
      HttpSession session = req.getSession(true);
      String identity = req.getParameter("identity");
      String alias = req.getParameter("alias");
      String sex = req.getParameter("sex");
     // System.err.println(req.getQueryString());
      String father = req.getParameter("father");
      String mother = req.getParameter("mother");

      if (identity == null) identity = "";
      if (alias == null) alias = "";
      if (sex == null) sex = "";
      if (father == null) father = "";
      if (mother == null) mother = "";

      // Set method to post to prevent that someone sees the paramaters through the url
      out.println("<form name=\"form1\" action=\""+getServletPath("membership")+"\" method=\"post\">");
      out.println("<table width=750 border=0 cellspacing=0 cellspacing=1>");
      try {
         // ********************************************************************************
         // Available sampling units
         out.println("<tr><td>Sampling unit<br>");
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID, NAME FROM gdbadm.V_SAMPLING_UNITS_2 " +
                                  "WHERE PID=" + pid + " order by NAME");
         out.println("<select name=suid id=suid onChange=\"selectionChanged()\">");
         boolean first=true;
         while (rset.next()) {
            if (first && suid.equals("-1")) {
               // It's the first time -> set suid
               first = false;
               suid = rset.getString("SUID");
            }
            if (suid.equals(rset.getString("SUID")))
               out.println("<option selected value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME"));
            else
               out.println("<option value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");
         // ********************************************************************************
         // Available groupings
         out.println("<td>Grouping<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT GSID, NAME FROM gdbadm.V_GROUPINGS_1 " +
                                  "WHERE SUID=" + suid + " order by NAME");
         out.println("<select name=gsid id=gsid onChange=\"selectionChanged()\">");
         first=true;
         while (rset.next()) {
            if (first && gsid.equals("-1")) {
               // It's the first time -> set gsid
               first = false;
               gsid = rset.getString("GSID");
            }
            if (gsid.equals(rset.getString("GSID")))
               out.println("<option selected value=\"" + rset.getString("GSID") + "\">" + rset.getString("NAME"));
            else
               out.println("<option value=\"" + rset.getString("GSID") + "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td>");
         // ********************************************************************************
         // Available groups
         out.println("<td>Group<br>");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT GID, NAME FROM gdbadm.V_GROUPS_1 " +
                                  "WHERE GSID=" + gsid + " order by NAME");
         out.println("<select name=gid id=gid onChange=\"selectionChanged()\">");
         
         
         first=true;
         while (rset.next()) {
            if (first && gid.equals("-1")) {
               // It's the first time -> set gid
               first = false;
               gid = rset.getString("GID");
            }
            if (gid.equals(rset.getString("gid")))
               out.println("<option selected value=\"" + rset.getString("GID") + "\">" + rset.getString("NAME"));
            else
               out.println("<option value=\"" + rset.getString("GID") + "\">" + rset.getString("NAME"));
         }
         out.println("</select>");
         out.println("</td></tr>");
         out.println("<tr><td>Identity<br><input type=text name=identity value=\"" + identity + "\"></td>");
         out.println("<td>Alias<br><input type=text name=alias value=\"" + alias + "\"></td>");
         out.println("<td>Sex<br><select name=sex>");
         if (sex.equals("M")) {
            out.println("<option value=\"*\">*");
            out.println("<option value=\"F\">Female");
            out.println("<option selected value=\"M\">Male");
         } else if (sex.equals("F")) {
            out.println("<option value=\"*\">*");
            out.println("<option selected value=\"F\">Female");
            out.println("<option value=\"M\">Male");
         } else {
            out.println("<option selected value=\"*\">*");
            out.println("<option value=\"F\">Female");
            out.println("<option value=\"M\">Male");
         }
         out.println("</select></td></tr>");
         out.println("<tr><td>Father<br><input type=text name=father value=\"" + father + "\"></td>");
         out.println("<td>Mother<br><input type=text name=mother value=\"" + mother + "\"></td>");
         out.println("<td><input type=\"button\" onClick=\"selectionChanged()\" value=\"Display\"></td></tr>");
         out.println("</table>");
         //			out.println("<td width=\"*\">&nbsp;</td></tr></table>");
         out.println("<hr>");
         // ********************************************************************************
         // Availible individuals not included in ths group
         out.println("<table><tr><td valign=middle align=right>");
         out.println("Available individuals<br>");
         out.println("<select name=\"avail_inds\" width=200px multiple size=15 "
                     + "style=\"WIDTH: 200px\">");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         StringBuffer sbSQL = new StringBuffer();
        
         sbSQL.append("SELECT I.IID, I.IDENTITY, I.ALIAS FROM gdbadm.V_INDIVIDUALS_2 I WHERE I.IID not in(select iid from r_ind_grp where GID="+gid +") "); 
         sbSQL.append(buildFilter(suid, identity, alias, sex, father, mother));
         out.println(sbSQL.toString());

         rset = stmt.executeQuery(sbSQL.toString());
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("IID") + "\">"
                        + rset.getString("IDENTITY") + " (" + formatOutput(session,rset.getString("ALIAS"),20) + ")");
         }
         out.println("</select>");
         out.println("</td><td valign=middle align=middle>");
         out.println("<input type=\"button\" name=\"add_inds\" value=\">\" onClick='addIndividuals()'>");
         out.println("<br>");
         out.println("<input type=\"button\" name=\"rem_inds\" value=\"<\" onClick='remIndividuals()'>");
         out.println("</td>");
         // ********************************************************************************
         // Included Individuals
         out.println("<td valign=middle align=left>");
         out.println("Included individuals<br>");
         out.println("<select name=\"incl_inds\" width=200px multiple size=15 "
                     + "style=\"WIDTH: 200px\">");
         rset.close();
         stmt.close();
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT IID, IDENTITY , ALIAS FROM gdbadm.V_INDIVIDUALS_1 WHERE IID "
                                  +  "IN(SELECT IID FROM gdbadm.V_SETS_GQL WHERE "
                                  + "SUID=" + suid + " AND GSID=" + gsid + " AND GID=" + gid + ")"
                                  + " order by identity");
         while (rset.next()) {
            out.println("<option value=\"" + rset.getString("IID") + "\">"
                        + rset.getString("IDENTITY") + " (" + formatOutput(session, rset.getString("ALIAS"),20) + ")");
         }
         out.println("</select>");
         out.println("</td>");
         out.println("</tr>");
         out.println("</table>");

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


      out.println("<input type=\"hidden\" name=\"page\" value=\"MEMBERSHIP\">");
      out.println("<input type=\"hidden\" name=\"oper\" value=\"\">");
      out.println("</form>"); 
   }
   
   
   private String buildFilter(String suid, String identity, String alias, String sex, String father, String mother) 
   {
      StringBuffer filter = new StringBuffer();
      filter.append(" AND SUID=" + suid); 
      if (identity != null && !"".equals(identity)) {
         filter.append(" AND IDENTITY like '").append(identity).append("'");
      }
      if (alias != null && !"".equals(alias)) {
         filter.append(" AND ALIAS like '").append(alias).append("'");
      }
      if (sex != null && !"".equals(sex) && !"*".equals(sex)) {
         filter.append(" AND SEX = '" + sex + "'");
      }
      if (father != null && !"".equals(father)) {
         filter.append(" AND FIDENTITY like '").append(father).append("'");
      }
      if (mother != null && !"".equals(mother)) {
         filter.append(" AND MIDENTITY like '").append(mother).append("'");
      }
      
      filter.append(" order by IDENTITY");
      
      return filter.toString().replace('*', '%');
   }

   private void writeScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function selectionChanged() {");
      out.println("	document.forms[0].oper.value='SELECT';");
      out.println("	document.forms[0].submit();");
      out.println("	return (true);");
      out.println("}");
      out.println("function remIndividuals() {");
      out.println("	document.forms[0].oper.value='REM_INDIVIDUALS';");
      out.println("	document.forms[0].submit();");
      out.println("	return (true);");
      out.println("}");
      out.println("");
      out.println("function addIndividuals() {");
      out.println("	document.forms[0].oper.value='ADD_INDIVIDUALS';");
      out.println("	document.forms[0].submit();");
      out.println("	return (true);");
      out.println("}");
      out.println("");
      out.println("");
      out.println("//-->");
      out.println("</script");	
					
   }

   private String findSamplingUnit(Connection conn, String pid) {
      String ret = null;
      Statement stmt = null;
      ResultSet rset = null;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT SUID FROM sysadm.SAMPLING_UNITS WHERE " +
                                  "PID=" + pid + " order by NAME");
         if (rset.next())
            ret = rset.getString("SUID");
         else
            ret = "-1";
      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
         ret = "-1";
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
      return ret;
   }
   private String findGrouping(Connection conn, String suid) {
      String ret = null;
      Statement stmt = null;
      ResultSet rset = null;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT GSID FROM gdbadm.V_GROUPINGS_1 WHERE " +
                                  "SUID=" + suid + " order by NAME");
         if (rset.next())
            ret = rset.getString("GSID");
         else
            ret = "-1";
      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
         ret = "-1";
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
      return ret;
   }
   private String findGroup(Connection conn, String gsid) {
      String ret = null;
      Statement stmt = null;
      ResultSet rset = null;
      try {
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT GID FROM gdbadm.V_GROUPS_1 WHERE " +
                                  "GSID=" + gsid + " order by NAME");
         if (rset.next())
            ret = rset.getString("GID");
         else
            ret = "-1";
      } catch (SQLException sqle) {
         sqle.printStackTrace(System.err);
         ret = "-1";
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         } catch (SQLException ignored) {}
      }
      return ret;
   }
}
