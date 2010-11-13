/*
 * Class.java
 *
 * Created on den 7 november 2003, 11:15
 */

package se.arexis.agdb.servlet;

import java.io.*;
import java.lang.String.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.db.*;
import se.arexis.agdb.db.DbObject.*;
import se.arexis.agdb.util.*;
import com.oreilly.servlet.MultipartRequest;
/**
 *
 * @author  wali
 */
public class viewRes extends SecureArexisServlet{
    
    /** Creates a new instance of Class */
    // public viewRes() {
    //}
    
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
        
        //if ( !authorized(req, res) ) {
        // The user does not have the privileges to view the requested page.
        // The method pageLocked has already written an error message
        // to the output stream, and that's why we safely can return here.
        // return;
        
        String extPath = req.getPathInfo();
        System.err.println("QS="+req.getQueryString());
        System.err.println("path="+extPath);
        
        if (extPath == null || extPath.equals("") || extPath.equals("/")) {
            writeFrame(req, res);
        } else if (extPath.equals("/top")) {
            writeTop(req, res);
        } else if (extPath.equals("/bottom")) {
            writeBottom(req, res);
        } else if (extPath.equals("/middle")) {
            writeMiddle(req, res);
        } else if (extPath.equals("/details")) {
            writeDetails(req, res);
        } else if (extPath.equals("/individuals")) {
            writeIndividuals(req, res);
        } else if (extPath.equals("/comment")) {
            writeComment(req, res);  
        } else if (extPath.equals("/download")) {
            sendFile(req, res);        
        } else if (extPath.equals("/edit")) {
            writeEdit(req, res);
        } else if (extPath.equals("/new")) {
            writeNew(req,res);
        } else if (extPath.equals("/newResult")) {
            createResult(req, res);
            //  } else if (extPath.equals("/impRes")) {
            //    writeNew(req, res);
            //} else if (extPath.equals("/completion")) {
            //writeCompletion(req, res);
        }
    }
    
    private void writeFrame(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        // set content type and other response header fields first
        res.setContentType("text/html");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Cache-Control", "no-cache");
        String action = req.getParameter("ACTION"); //action
        String oper = req.getParameter("OPER");
   
        System.err.println("oper writeFrame: " + oper);
        PrintWriter out = res.getWriter();
        try {
            // Check if redirection is needed
            res = checkRedirectStatus(req,res);
            //req=getServletState(req,session);
            
            String topQS = buildQS(req);
           
            // we don't want the oper parameter anaywhere but in the "edit" and "new" page!
            topQS = removeQSParameterOper(topQS);
            String bottomQS = topQS.toString();
         
            out.println("<html>"
            + "<HEAD>"
            + " <TITLE>Result</TITLE>"
            + "</HEAD>"
            + "<frameset rows=\"180,35,*\" framespacing=\"0\" border=\"true\">" 
            + "<frame name=\"filttop\" "
            + "src=\""+ getServletPath("viewRes/top?") + topQS + "\""
            + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
            + "</frame>\n"
           
            + "<frame name=\"filtmiddle\" "
            + "src=\""+ getServletPath("viewRes/middle?") + topQS + "\""
            + " scrolling=\"no\" marginheight=\"0\" noresize frameborder=\"0\">"
            + "</frame>\n"
            
            + "<frame name=\"filtbottom\""
            + "src=\"" +getServletPath("viewRes/bottom?") + bottomQS + "\" "
            + " scrolling=\"auto\" marginheight=\"0\" frameborder=\"0\">"     
            + "</frameset><noframes><body><p>"
            + "This page uses frames, but your browser doesn't support them."
            + "</p></body></noframes></frameset>"
            + "</HTML>");
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        finally {
        }
    }
    
    /** Keeps track of which attribute the results should be sorted by, and the
     * buttons used for navigation of the results.
     */
    
    private String buildQS(HttpServletRequest req) {
        StringBuffer output = new StringBuffer(512);
        
        HttpSession session = req.getSession(true);
        if (req != null)
            session = req.getSession();
        else
            System.err.println("Request error!!");
        
        Connection conn = (Connection) session.getAttribute("conn");
        
        String action = null,
        suid = null,
        old_suid = null,
        result = null,
        rtype = null,
        fgid = null,
        group = null,
        groupings = null,
        date_from = null,
        date_to = null,
        category = null,
        individual = null,
        orderby = null,
        status = null;

      boolean suid_changed = false;
      String pid = (String) session.getAttribute("PID");
      old_suid = (String) session.getAttribute("SUID");
      suid = req.getParameter("suid");
    
      String oper = req.getParameter("OPER");
      if(oper == null) oper = new String("");
    
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
      
        if (oper.equalsIgnoreCase("SEL_CHANGED"))
            output.append("&OPER=").append(oper);
        result = req.getParameter("RESNAME");
        if (result != null)
            output.append("&RESNAME=").append(result);
        rtype = req.getParameter("RTYPE");
        if (rtype != null)
            output.append("&RTYPE=").append(rtype);
        fgid = req.getParameter("FGID");
        if (fgid != null)
            output.append("&FGID=").append(fgid);
        group = req.getParameter("GROUP");
        if (group != null)
            output.append("&GROUP=").append(group);
        groupings = req.getParameter("GROUPINGS");
        if (group != null)
            output.append("&GROUPINGS=").append(groupings);
        date_from = req.getParameter("R_FROM");
        if (date_from != null)
            output.append("&R_FROM=").append(date_from);
        date_to = req.getParameter("R_TO");
        if (date_to != null)
            output.append("&R_TO=").append(date_to);
        category = req.getParameter("CATEGORY");
        if (category != null)
            output.append("&CATEGORY=").append(category);
        individual = req.getParameter("IND");
        if (individual != null)
            output.append("&IND=").append(individual);    
        if (!action.equals("NOP"))
           output.append(setIndecis(suid, old_suid, action, req, session));
        output.append("&suid=").append(suid);
        if (req.getParameter("oper") != null)
            output.append("&oper=").append(req.getParameter("oper"));
       
        // Orderby must be the last parameter in the query string
        orderby = req.getParameter("ORDERBY");
        if (orderby != null)
            output.append("&ORDERBY=").append(orderby);
        else
            output.append("&ORDERBY=R_NAME");
        
        return output.toString().replace('%', '*');
    }
    
    /** Displays the opper part of the View & Edit page: the search criteria
     * the user can give.
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

        HttpSession session = req.getSession(true);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
      
        String exp_file, result_type, individual, r_to, r_from, group, groupings, ctg, rid, orderby, action;
        String oldQS, newQS, suid, pid, result_name, iid, fgid;
        int testC=0;
        int startIndex = 0, rows = 0, maxRows = 0;
        int currentPrivs[];
        
        try {
            
            conn = (Connection) session.getAttribute("conn");
            currentPrivs = (int [])session.getAttribute("PRIVILEGES");
            pid = (String) session.getAttribute("PID");
            maxRows= getMaxRows(session);
            stmt = conn.createStatement();
              
            oldQS = req.getQueryString();
            oldQS = removeQSParameter(req.getQueryString(),"oper");
            newQS = buildTopQS(oldQS);
           
            suid = req.getParameter("suid");    
            result_name = req.getParameter("RESNAME");
            result_type = req.getParameter("RTYPE");
            individual = req.getParameter("IND");
            fgid = req.getParameter("FGID");
            r_from = req.getParameter("R_FROM");
            r_to = req.getParameter("R_TO");
           // group = req.getParameter("GROUP");
           // groupings = req.getParameter("GROUPINGS");
            ctg = req.getParameter("CATEGORY");
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
            if (result_type == null) result_type = new String("");
            if (r_to == null) r_to = new String("");
            if (r_from == null) r_from = new String("");
          //  if (group == null) group = new String("");
          //  if (groupings == null) groupings = new String("");
            if (individual == null) individual = new String("");
            if (ctg == null) ctg = new String("");
            if (orderby == null) orderby = new String("R_NAME");
            if (action == null) action = new String("NOP");
            if (result_name == null) result_name = new String("");
            if (fgid == null) fgid = new String("");
           
            out.println("<html>");
            out.println("<head><link rel=\"stylesheet\" type=\"text/css\" href=\""+ getURL("style/view.css") +"\">");
            out.println("<base target=\"content\">");
            
            writeTopScript(out);
            out.println(getDateValidationScript()); 
            out.println("<title>Results</title>");
            out.println("</head>");
            
          
            out.println("<body bgcolor=\"#ffffd0\">"
            +"<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">"
            +"<tr>"
            + "<td width=\"14\" rowspan=\"3\">" +"</td>"
            +"<td width=\"736\" colspan=\"2\" height=\"15\">"
            +"<form method=get action=\"" +getServletPath("viewRes") +"\">"
            +"<p align=\"center\"><font size=\"2\"><b  style=\"font-size: 15pt\">Results - View & Edit </b>"
            +"</font></td></tr>"
            +"<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>"
            +"</tr><tr><td width=\"517\">");

            out.println("<table width=488 height=\"92\">");            
            out.println("<td><b>Sampling unit</b><br><select name=suid "
            +" onChange='selChanged(\"suid\")' style=\"HEIGHT: 22px; WIDTH: 126px\">");
                
            //get the sampling unit name
            String suSQL = "SELECT NAME, SUID FROM gdbadm.V_SAMPLING_UNITS_3 " +
                                  "WHERE PID="+ pid + " AND STATUS='E'" +" ORDER BY NAME";
            stmt = conn.createStatement();    
            rset = stmt.executeQuery(suSQL);
           
            out.println("<OPTION value=no_value>*</option>");
            while (rset.next()) {
                if (suid != null && suid.equalsIgnoreCase(rset.getString("SUID")))
                    out.println("<OPTION selected value=\"" + rset.getString("SUID") + "\">" +
                    rset.getString("NAME")+ "</option>");
                else
                    out.println("<OPTION value=\"" + rset.getString("SUID") + "\">" + rset.getString("NAME")+"</option>");
            }
            rset.close();
            stmt.close();
            out.println("</SELECT></td>");
          
            //Result name
            out.println("<td><b>Result Name</b><br><select name=RESNAME "
            +" onChange='document.forms[0].submit' style=\"HEIGHT: 22px; WIDTH: 126px\">");
      
            stmt = conn.createStatement();
            String sql="";
            if(suid.trim().equals("") || suid.trim().equals("no_value"))
                sql="SELECT RESID, R_NAME FROM RESULTS WHERE PID=" +pid+"ORDER BY R_NAME";
            else
                sql = "SELECT R_NAME, RESID FROM RESULTS WHERE PID ="+ pid
                    + " AND FGID IN (SELECT FGID FROM R_FG_IND WHERE SUID="+suid
                    + ") ORDER BY R_NAME";
                
           rset = stmt.executeQuery(sql);
          
            out.println("<OPTION value=no_value>*</option>");
            while (rset.next()) {   //RID
                if (result_name != null && result_name.equalsIgnoreCase(rset.getString("R_NAME")))
                    out.println("<OPTION selected value=\"" + rset.getString("R_NAME") + "\">" +
                    rset.getString("R_NAME")+ "</option>\n");
                else
                    out.println("<OPTION value=\"" + rset.getString("R_NAME") + "\">" + rset.getString("R_NAME")+"</option>\n");
            }
            rset.close();
            stmt.close();
            out.println("</SELECT></td>");
            
            
            // Results_type
            out.println("<td><b>Result Type</b><br><select name=RTYPE "
            +" onChange='document.forms[0].submit' style=\"HEIGHT: 22px; WIDTH: 126px\">");
            
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT RTID, NAME FROM RTYPE" +
            " ORDER BY NAME");
            out.println("<OPTION value=no_value>*</option>");
            while (rset.next()) {   
                if (result_type != null && result_type.equalsIgnoreCase(rset.getString("RTID")))
                    out.println("<OPTION selected value=\"" + rset.getString("RTID") + "\">" +
                    rset.getString("NAME")+ "</option>\n");
                else
                    out.println("<OPTION value=\"" + rset.getString("RTID") + "\">" + rset.getString("NAME")+"</option>\n");
            }
            rset.close();
            stmt.close();
            out.println("</SELECT></td>");  
            
            //File Id
            out.println("<td><b>File id");
            out.println("<SELECT name=FGID " +
            "style=\"HEIGHT: 22px; WIDTH: 126px\" " +
            "onChange='selChanged(\"fgid\")'>");
            stmt = conn.createStatement(); 
            String SQL = "";
          
            if(suid.trim().equals("") || suid.trim().equals("no_value"))
                SQL = "SELECT DISTINCT FGID FROM R_FG_IND WHERE SUID IN "
                    + "(SELECT SUID FROM V_Enabled_Sampling_Units_2 WHERE PID = "+pid+") ORDER BY FGID DESC";
            else 
                SQL = "SELECT DISTINCT FGID FROM RESULTS WHERE PID = "+ pid
                    + " AND FGID IN (SELECT FGID FROM R_FG_IND WHERE SUID = "+suid+") ORDER BY FGID DESC";
              
            rset = stmt.executeQuery(SQL);
          
            out.println("<OPTION value=no_value>*</option>");
            while (rset.next()) {
                if (fgid != null && fgid.equalsIgnoreCase(rset.getString("FGID")))
                    out.println("<OPTION selected value=\"" + rset.getString("FGID") + "\">" +
                    rset.getString("FGID"));
                else
                    out.println("<OPTION value=\"" + rset.getString("FGID") + "\">" +
                    rset.getString("FGID"));
                
            }
            out.println("</td>");
            out.println("</select>");
            rset.close();
            stmt.close();
            
             
            //Result date
            out.println("<tr>");
            out.println("<td><b>Result Date from:</b><br>"
            +"<input id=R_FROM name=R_FROM value=\"" + replaceNull(r_from, "") + "\" style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\"></td>");
            
            out.println("<td><b>Result Date to:</b><br>"
            + "<input id=R_TO name=R_TO value=\"" + replaceNull(r_to, "") + "\"style=\"HEIGHT: 22px; WIDTH: 126px\" size=\"12\"></td>");
            
            /*
            //Individuals group
            out.println("<td><b>Group</b><br>");
            out.println("<SELECT name=GROUP " +
            "style=\"HEIGHT: 22px; WIDTH: 126px\" " +
            "onChange='selChanged(\"group\")'>");
            
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT GID, NAME, COMM FROM Groups" +
            " ORDER BY NAME");
            
            out.println("<OPTION value=no_value>*</option>");
            while (rset.next()) {
                if (group != null && group.equalsIgnoreCase(rset.getString("GID")))
                    out.println("<OPTION selected value=\"" + rset.getString("GID") + "\">" +
                    rset.getString("NAME"));
                else
                    out.println("<OPTION value=\"" + rset.getString("GID") + "\">" +
                    rset.getString("NAME"));
            }
            
            out.println("</td>");
            out.println("</select>");
            rset.close();
            stmt.close();
            
            //Grouping
            out.println("<td><b>Grouping</b><br>");
            out.println("<SELECT name=GROUPINGS " +
            "style=\"HEIGHT: 22px; WIDTH: 126px\" " +
            "onChange='selChanged(\"grouping\")'>");
            
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT GSID, NAME FROM Groupings" +
            " ORDER BY NAME");
            
            out.println("<OPTION value=no_value>*</option>");
            while (rset.next()) {
                if (groupings != null && groupings.equalsIgnoreCase(rset.getString("GSID")))
                    out.println("<OPTION selected value=\"" + rset.getString("GSID") + "\">" +
                    rset.getString("NAME"));
                else
                    out.println("<OPTION value=\"" + rset.getString("GSID") + "\">" +
                    rset.getString("NAME"));
            }
            
            out.println("</td>");
            out.println("</select>");
            rset.close();
            stmt.close();
            */
            
            out.println("<td><b>Identity</b><br>"
                     + "<input id=IND name=IND value=\"" + individual + "\" style=\"HEIGHT: 22px; WIDTH: 127px\" size=\"12\"></td>");
          
            // Category
            out.println("<td><b>Category</b><br>");
            out.println("<SELECT name=CATEGORY " +
            "style=\"HEIGHT: 22px; WIDTH: 126px\" " +
            "onChange='selChanged(\"ctg\")'>");
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT CTGID, NAME, COMM FROM Category" +
            " ORDER BY NAME");
            
            out.println("<OPTION value=no_value>*</option>");
            while (rset.next()) {
                if (ctg != null && ctg.equalsIgnoreCase(rset.getString("CTGID")))
                    out.println("<OPTION selected value=\"" + rset.getString("CTGID") + "\">" +
                    rset.getString("NAME"));
                else
                    out.println("<OPTION value=\"" + rset.getString("CTGID") + "\">" +
                    rset.getString("NAME"));
            }
            
            out.println("</td>");
            out.println("</select></tr></table>");
            rset.close();
            stmt.close();
            
          
            //Buttons
            out.println("<td width=219>");
            out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
            out.println("<td colspan=4>\n");
            
            out.println(privDependentString(currentPrivs,IND_W,
            
            /*if true*/"<input type=button value=\"New Result\""
            + " onClick='parent.location.href=\"" +getServletPath("viewRes/new?") + oldQS + "\"' "
            +"height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">"
            +"</td>",
            /*if false*/"<input type=button disabled value=\"New Result\""
            +"height=20 width=\"139\" style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 133px\" name=\"button\">"
            +"</td>"));
            
            
            out.println("<tr><td width=68 colspan=2>"//type=submit
            +"<input id=COUNT name=COUNT type=button value=\"Count\" width=\"69\""
            +" onClick='valForm(\"COUNT\")' height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">"
            +"</td>"
            +"<td width=68 colspan=2>"
            +"<input id=DISPLAY name=DISPLAY type=button value=\"Display\""
            +" width=\"70\" height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\" " //>
            +"onClick='valForm(\"DISPLAY\")'>"
            +"</td></tr>");
            
            
            // some hidden values
            out.println("<input type=\"hidden\" id=\"STARTINDEX\" name=\"STARTINDEX\" value=\"" + startIndex + "\">");
            out.println("<input type=\"hidden\" id=\"ORDERBY\" name=\"ORDERBY\" value=\"" + orderby + "\">");
            out.println("<input type=\"hidden\" id=\"ACTION\" name=ACTION value=\"NOP\">"); 
            out.println("<input type=\"hidden\" id=\"OPER\" name=OPER value=\"\">");
            out.println("<input type=\"hidden\" id=\"ITEM\" name=ITEM value=\"\">");
            
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
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
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
   /** Checks that the dates are given in the right way.
    *
    */
    
    private void writeTopScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function selChanged(item) {");
      out.println("  document.forms[0].ITEM.value = \"\" + item;");
      out.println("  document.forms[0].OPER.value = \"SEL_CHANGED\";");
      out.println("  document.forms[0].submit();");
      out.println("}");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 0;");
      out.println("	if ('DISPLAY' == action.toUpperCase() || 'COUNT' == action.toUpperCase()) {");
      out.println("         if (document.forms[0].R_FROM.value != '') {");
      out.println("               if (!valDate(document.forms[0].R_FROM)) {");
      out.println("			rc = 1;");
      out.println("	          }");  
      out.println("           }"); 
      out.println("         if (document.forms[0].R_TO.value != '') {");
      out.println("               if (!valDate(document.forms[0].R_TO)) {");
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

    
    /** Displays the middle part of the View & Edit page.
     *
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
        
        String oper = req.getParameter("OPER");
      
        if(action==null) action = "NOP";
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
            
            if(action != null){ 
                out.println("&nbsp;" +buildInfoLine(action, startIndex, rows, maxRows));
            }
            String oldQS, newQS;
            oldQS = req.getQueryString();
            String choosen= req.getParameter("ORDERBY");
            newQS = buildTopQS(oldQS);
        
           //information table
            out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
           // menu table        
            out.println("<table bgcolor=\"#008B8B\" border=0 cellpadding=0 cellspacing=0" +
            " height=20 width=840 style=\"margin-left:2px\">"); 
            out.println("<td width=5></td>");       
           
            // the menu choices
            
            //Result name
            out.println("<td width=90><a href=\"" + getServletPath("viewRes")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=R_NAME\">");
            if(choosen.equals("R_NAME"))
                out.println("<FONT color=saddlebrown><b>Result name</b></FONT></a></td>\n");
            else out.println("Result name</a></td>\n");
            
            //Result type
            out.println("<td width=80><a href=\"" + getServletPath("viewRes")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=RT_NAME\">");
            if(choosen.equals("R_TYPE"))
                out.println("<FONT color=saddlebrown><b>Result Type</b></FONT></a></td>\n");
            else out.println("Result Type</a></td>\n");
            
            //Batch name
            out.println("<td width=90><a href=\"" + getServletPath("viewRes")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=B_NAME\">");
            if(choosen.equals("B_NAME"))
                out.println("<FONT color=saddlebrown><b>Batch name</b></FONT></a></td>\n");
            else out.println("Batch name</a></td>\n");
            
            // Category
            out.println("<td width=80><a href=\"" + getServletPath("viewRes")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=CTG\">");
            if(choosen.equals("CTG"))
                out.println("<FONT color=saddlebrown><b>Category</b></FONT></a></td>\n");
            else out.println("Category</a></td>\n");
         
            // Comment
            out.println("<td width=160><a href=\"" + getServletPath("viewRes")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=COMM\">");
            if(choosen.equals("COMMENT"))
                out.println("<FONT color=saddlebrown><b>Comment</b></FONT></a></td>\n");
            else out.println("Comment</a></td>\n");
           
             //Updated
            out.println("<td width=95><a href=\"" + getServletPath("viewRes")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=R.TS\">");
            if(choosen.equals("R.TS"))
                out.println("<FONT color=saddlebrown><b>Updated</b></FONT></a></td>\n");
            else out.println("Updated</a></td>\n");
         
            //USER
            out.println("<td width=60><a href=\"" + getServletPath("viewRes")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=USR\">");
            if(choosen.equals("USR"))
                out.println("<FONT color=saddlebrown><b>User</b></FONT></a></td>\n");
            else out.println("User</a></td>\n");
           
              //Creation date
            out.println("<td width=95><a href=\"" + getServletPath("viewRes")+"?ACTION=DISPLAY&" + newQS + "&ORDERBY=C_TS\">");
            if(choosen.equals("C_TS"))
                out.println("<FONT color=saddlebrown><b>Created</b></FONT></a></td>\n");
            else out.println("Created</a></td>\n");
         
            out.println("<td width=40>&nbsp;</td>");
            out.println("<td width=30>&nbsp;</td>");
            out.println("</table></table>");//menu/information tables
            out.println("</body></html>");
            
            
        } catch (Exception e) 
        {
            e.printStackTrace();
            out.println("<strong>Middle Error in filter!</strong><br>");
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
    
    /** Displays the results on the View & Edit page
     */
    
    private void writeBottom(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        // set content type and other response header fields first
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        Statement stmt = null;
        ResultSet rset = null;
        Statement stmtName = null;   // to get the name for rtype and category
        ResultSet rsetName = null;
        Connection conn = null;
        int currentPrivs[];
        StringBuffer sbSQL = new StringBuffer(512);
        try {
            String suid = null, action = null, status=null, oper=null;
            String oldQS = req.getQueryString();
            int resid = 0;
          
            action = req.getParameter("ACTION");
            suid = req.getParameter("suid");
            status = req.getParameter("STATUS");
            oper = req.getParameter("OPER");
            currentPrivs = (int [])session.getAttribute("PRIVILEGES");
         
            if (action == null || action.equalsIgnoreCase("NOP") ||
                action.equalsIgnoreCase("COUNT") || oper != null) 
                {
                    HTMLWriter.writeBottomDefault(out);
                    return;
                }
            else if (action.equalsIgnoreCase("NEXT")) {
                // Skip the first 50 rows?!
            } else if (action.equalsIgnoreCase("PREV")) {
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
                                     
            
            sbSQL.append("SELECT RESID, R_NAME, R_TYPE, B_NAME, CTG, R.COMM, to_char(C_TS, '" + getDateFormat(session) + "') as C_TS," + 
            "R.ID, to_char(R.TS, '" + getDateFormat(session) + "') as RTS, " +
            "CATEGORY.NAME as CTG_NAME, RTYPE.NAME as RT_NAME, USR " +
            "FROM RESULTS R, CATEGORY, RTYPE, USERS WHERE " +
            "R_TYPE = RTYPE.RTID AND CTG = CATEGORY.CTGID AND USERS.ID=R.ID ");
             
            String qs = req.getQueryString();
           
            // Build filter
            String filter = buildFilter(req);
            sbSQL.append(filter);
   
            rset = stmt.executeQuery(sbSQL.toString());
            
            out.println("<TABLE align=left border=0 cellPadding=0");
            out.println("cellSpacing=0 height=20 width=840 style=\"margin-left:2px\">");
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
        
            while (rset.next() && rowCount < maxRows ) {
               
                 out.println("<TR align=left ");
                 if (odd) 
                    out.println("bgcolor=white>");
                 else 
                    out.println("bgcolor=lightgrey>");    
                        
                out.println("<TD WIDTH=5></TD>");
                out.println("<TD WIDTH=90>" + formatOutput(session,rset.getString("R_NAME"),15)+"</TD>");
                out.println("<TD WIDTH=80>" + formatOutput(session,rset.getString("RT_NAME"),13)+ "</TD>");
                out.println("<TD WIDTH=90>" + formatOutput(session,rset.getString("B_NAME"),15)+"</TD>");          
                out.println("<TD WIDTH=80>" + formatOutput(session,rset.getString("CTG_NAME"),13) + "</TD>");
                
                String comment = rset.getString("COMM");
                if (comment == null) comment = new String("");
                comment = retrieveSymbol(comment);
                
                out.println("<TD WIDTH=160>" + formatOutput(session,comment,30) + "</TD>");
                out.println("<TD WIDTH=95>" + formatOutput(session,rset.getString("RTS"),17) + "</TD>");
                out.println("<TD WIDTH=60>" + formatOutput(session,rset.getString("USR"),11) + "</TD>");
                out.println("<TD WIDTH=95>" + formatOutput(session,rset.getString("C_TS"),17) + "</TD>");
        
                out.println("<TD WIDTH=40><A HREF=\"" + getServletPath("viewRes/details?rid=")
                + rset.getString("resid")
                + "&" + oldQS + "\" target=\"content\">Details</A></TD>");
                
                out.println("<TD WIDTH=30>");
                
                out.println(privDependentString(currentPrivs,IND_W,
                /*if true*/"<A HREF=\"" +getServletPath("viewRes/edit?rid=")
                + rset.getString("RESID")
                + "&" + oldQS + "\" target=\"content\">Edit</A></TD></TR>",
                /*if false*/"<font color=tan>&nbsp Edit</font></TD>") );
                odd=!odd;
                rowCount++;
            }
            out.println("<TR align=left bgcolor=oldlace><TD WIDTH=5>&nbsp&nbsp</TD></TR>");
            out.println("</TABLE>");
            out.println("</body></html>");
            
        } catch (Exception e) 
        {
            e.printStackTrace();
            Errors.logError("SQL="+sbSQL);
            out.println("<strong>Bottom Error in filter!</strong><br>");
            out.println("Error message: " + e.getMessage());
            out.println("<br>Modify filter according to message!</body></html>");
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (stmt != null) stmt.close();
                if (rsetName != null) rsetName.close();
                if (stmtName != null) stmtName.close();
            } catch (Exception ignored) {}
        }
    }
    
    private String buildFilter(HttpServletRequest req)
    {
        return buildFilter(req,true);
    }
    
    /** Generates SQL statement according to the given search criteria 
     * on the View & Edit page.
     */
    
    private String buildFilter(HttpServletRequest req, boolean order) {
      String rname = null,
         rtype = null,
         fgid = null,
         rfrom = null,
         rto = null,
         //group = null,
         //groupings = null,
         ctg = null,      
         id = null, 
         orderby = null,
         status = null,
         identity = null,
         suid = null,
         pid = null;
        HttpSession session = req.getSession(true);
        StringBuffer filter = new StringBuffer(256);
        suid = req.getParameter("suid");
        rname = req.getParameter("RESNAME");
        rtype = req.getParameter("RTYPE");
        fgid = req.getParameter("FGID");
        ctg = req.getParameter("CATEGORY"); 
        rfrom = req.getParameter("R_FROM");
        rto = req.getParameter("R_TO");
       // group = req.getParameter("GROUP");
       // groupings = req.getParameter("GROUPINGS");
        orderby = req.getParameter("ORDERBY");
        status = req.getParameter("STATUS");
        identity = req.getParameter("IND");
        pid = (String) session.getAttribute("PID");
      
    
        if (suid != null && !"no_value".equalsIgnoreCase(suid) && !suid.trim().equals(""))
            filter.append(" AND R.FGID IN (SELECT FGID FROM R_FG_IND WHERE SUID"
                        + " IN (SELECT SUID FROM SAMPLING_UNITS WHERE SUID = " + suid + "))");
        if (pid != null && !"no_value".equalsIgnoreCase(pid))
         filter.append("and PID like '" + pid + "'");
        if (rname != null && !"no_value".equalsIgnoreCase(rname))
         filter.append("and R_NAME like '" + rname + "'");
        if (fgid != null && !"no_value".equalsIgnoreCase(fgid))
         filter.append(" and FGID like '" + fgid + "'");
        if (rtype != null && !"no_value".equalsIgnoreCase(rtype))
         filter.append(" and R_TYPE like '" + rtype + "'"); 
        if (ctg != null && !"no_value".equalsIgnoreCase(ctg))
         filter.append(" and CTG like '" + ctg + "'");      
        if (rfrom != null && !"".equalsIgnoreCase(rfrom))
         filter.append(" and R.TS >= to_date('" + rfrom + "', 'YYYY-MM-DD')");
         //filter.append(" and to_char(TS, 'YYYYMMDD') >= to_date('" + rfrom + "', 'YYYY-MM-DD')");
        if (rto != null && !"".equalsIgnoreCase(rto))
         filter.append(" and R.TS <= to_date('" + rto + "', 'YYYY-MM-DD')");
         //filter.append(" and to_char(TS, 'YYYYMMDD') <= to_date('" + rto + "', 'YYYY-MM-DD')");
        if (identity != null && !"".equalsIgnoreCase(identity))
         filter.append("and R.FGID IN (SELECT DISTINCT FGID FROM R_FG_IND WHERE IID IN"+
                        "(SELECT IID FROM INDIVIDUALS WHERE IDENTITY like '"+identity+"'))"); 
        
        if (order)
        {
            if (orderby != null && !"".equalsIgnoreCase(orderby))
             filter.append(" order by " + orderby);
            else
                filter.append(" order by R.R_NAME");
        }
       // if (group != null && !"no_value".equalsIgnoreCase(group))
        // filter.append(" and results.fgid in (SELECT DISTINCT FG.FGID FROM FILE_GENERATIONS FG, R_FG_IND RFI WHERE FG.FGID=RFI.FGID AND RFI.IID IN (SELECT DISTINCT iid FROM V_SETS_GQL v, GROUPS, Groupings gs WHERE v.GID = GROUPS.GID AND GS.GSID=V.GSID AND GROUPS.GID='"+group+"'))" );
     //   if (groupings != null && !"no_value".equalsIgnoreCase(groupings))
      //   filter.append(" and CTG like '" + ctg + "'");
        
       
        // Replace every occurence of '*' with '%' and return the string
        // (Oracel uses '%' as wildcard while '%' demands some specail treatment
        // when passed in the query string)
        
        return filter.toString().replace('*', '%');
        
    }
    
    /** Generated the info line that is displayed on the View & Edit page. 
     * Used in writeMiddle
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
    
    private String setIndecis(  String suid, 
                                String old_suid, 
                                String action, 
                                HttpServletRequest req, 
                                HttpSession session) 
   {
      StringBuffer output = new StringBuffer(128);
      int rows = 0, startIndex = 0, maxRows = 0;
      rows = countRows(suid, req, session);
   
      maxRows = getMaxRows(session);
      if (req.getParameter("STARTINDEX") != null &&
          old_suid.equalsIgnoreCase(suid))
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
    
    
       
   private int setIndIndecis(String action,
                                int rows,
                                HttpServletRequest req, 
                                HttpSession session) 
   {
      StringBuffer output = new StringBuffer(128);
      int startIndex = 0, maxRows = 0;
   //   rows = countRows(suid, req, session);
      maxRows = getMaxRows(session);
      if (req.getParameter("StartIndIndex") != null) //&&
         // old_suid.equalsIgnoreCase(suid))
      {
         startIndex = Integer.parseInt(req.getParameter("StartIndIndex"));
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
   
     // output.append("&StartIndIndex=").append(startIndex)
       //  .append("&ROWS=").append(rows);
      return startIndex; //output.toString();
   }
   
    private int countRows(String suid, HttpServletRequest req, HttpSession session) {
      Connection conn = (Connection) session.getAttribute("conn");
      Statement stmt = null;
      ResultSet rset = null;
      StringBuffer sbSQL = new StringBuffer(512);
      try {
         sbSQL.append("SELECT count(*) "
                      + "FROM Results R, RType WHERE R.r_type=rtype.rtid ");
         sbSQL.append(buildFilter(req,false));
        
         stmt = conn.createStatement();
         rset = stmt.executeQuery(sbSQL.toString());
         rset.next();
         return rset.getInt(1);		
											
      } catch (SQLException e) {
          e.printStackTrace();
         return 0;
      } finally {
         try {
            if (rset != null) rset.close();
            if (stmt != null) stmt.close();
         }catch (SQLException ignored) {}
      }
   }    
    
    private String findSuid(Connection conn, String pid) {
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
            e.printStackTrace();
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

    
    /** Displays the Result - New page.
     *
     */
    
    private void writeNew(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        Connection conn = (Connection) session.getValue("conn");
        Statement stmt = null;
        ResultSet rset = null;
        res.setContentType("text/html");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Cache-Control", "no-cache");
        PrintWriter out = res.getWriter();
        String rid, fgid, oper, item, newQS, suid, pid;
        
        pid = "null";
        int currentPrivs[];
        currentPrivs = (int [])session.getAttribute("PRIVILEGES");
        pid = (String) session.getAttribute("PID");
      /* When the client makes a GET request to the server a HTML form is created and the
       hidden fields contain the name submitted for that GET request plus hidden fields
       containing each of the names submitted by each previous GET request.*/
        String ctg = null;
        String s_fgid = null;
        String rtype = null;
        ctg = req.getParameter("CAT");
        s_fgid = req.getParameter("sel_fgid");
        rtype = req.getParameter("RES");
        try {
            conn = (Connection) session.getValue("conn");   
            rid = (String) session.getValue("RID");
           
            oper = req.getParameter("oper");  
            if (oper == null || oper.trim().equals("")) oper = "SEL_CHANGED";
            item = req.getParameter("item");
            if (item == null || item.trim().equals("")) item = ""; // make sure that all of suid, cid, mid, aid are updated
            if (rid == null || "".equalsIgnoreCase(rid))
                rid = "-1";
      
            out.println("<html>");
            out.println("<head>");
            HTMLWriter.css(out,getURL("style/axDefault.css"));
            out.println("<base target=\"content\">");
            
            writeNewScript(out);
            out.println("<title>New Result</title>");
            
            out.println("</head>");
            out.println("<body>");
            out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
            "<tr>" +
            "<td width=\"14\" rowspan=\"3\"></td>" +
            "<td width=\"736\" colspan=\"2\" height=\"15\">");
            out.println("<center>" +
            "<b style=\"font-size: 15pt\">Results - New</b></center>" +
            "</font></td></tr>" +
            "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
            "</tr></table>");
            
            
            out.println("<form method=post enctype=\"multipart/form-data\" action=\"" +
            getServletPath("viewRes/newResult?") + "\">");
           
            out.println("<br><br>");
            out.println("<table border=0>");
            out.println("<td>");
            
            // File ID, from drop down menu    
            out.println("File id<br>");
            out.println("<font face=arial,geneva,helvetica size=1>Choose the file id from the list:<br></font></label>");
            out.println("<SELECT name=sel_fgid WIDTH=150 height=25 " +
            "style=\"HEIGHT: 25px; WIDTH: 150px\"> ");
          
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT DISTINCT FGID FROM R_FG_IND WHERE SUID IN"
                       + " (SELECT SUID FROM V_Enabled_Sampling_Units_2 WHERE PID = "+pid+") ORDER BY FGID DESC");
            out.println("<OPTION value=0>*</option>");
            
            while (rset.next()) {
                if (s_fgid != null && s_fgid.equalsIgnoreCase(rset.getString("FGID")))
                    out.println("<OPTION selected value=\"" + rset.getString("FGID") + "\">" +
                    rset.getString("FGID"));
                else
                    out.println("<OPTION value=\"" + rset.getString("FGID") + "\">" +
                    rset.getString("FGID"));
            }
            
            out.println("</td>");
            out.println("</select>");
            rset.close();
            stmt.close();
                  
            //Category
            out.println("<td><b>Category</b><br>");
            out.println("<SELECT name=CAT " +
            "style=\"HEIGHT: 22px; WIDTH: 126px\" >");

            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT CTGID, NAME, COMM FROM Category" +
            " ORDER BY NAME");
            
           
            while (rset.next()) {
                if (ctg != null && ctg.equalsIgnoreCase(rset.getString("CTGID")))
                    out.println("<OPTION selected value=\"" + rset.getString("CTGID") + "\">" +
                    rset.getString("NAME"));
                else
                    out.println("<OPTION value=\"" + rset.getString("CTGID") + "\">" +
                    rset.getString("NAME"));
            }
            
            out.println("</td>");
            out.println("</select>");
            rset.close();
            stmt.close();
            
            
            // Fgid, written
            out.println("<tr><td>");
            out.println("<font face=arial,geneva,helvetica size=1>or write it here: " +
            "</label><br></font><input type=text name=write_fgid>");
            out.println("</td>");
            
            //Result type
            out.println("<td><b>Result Type</b><br>");
            out.println("<SELECT name=RES " +
            "style=\"HEIGHT: 22px; WIDTH: 126px\"> ");
          
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT RTID, NAME, COMM FROM RTYPE" +
            " ORDER BY NAME");
            
            while (rset.next()) {
                if (rtype != null && rtype.equalsIgnoreCase(rset.getString("RTID")))
                    out.println("<OPTION selected value=\"" + rset.getString("RTID") + "\">" +
                    rset.getString("NAME"));
                else
                    out.println("<OPTION value=\"" + rset.getString("RTID") + "\">" +
                    rset.getString("NAME"));
            }
            
            out.println("</td>");
            out.println("</select>");
            rset.close();
            stmt.close();
              
            //Some space between the File ID and the Batch file
            out.println("<tr>");
            out.println("<td>&nbsp");
            out.println("</td>");
            out.println("</tr>");
            
            //Result File
            out.println("<tr>");
            out.println("<td>Result File<br>");
            out.println("<input type=file name=filename size=30>"); // +
            out.println("</td></tr>");

            //Batch file
            out.println("<tr>");
            out.println("<td>Batch File<br>");
            out.println("<input type=file name=bfilename size=30>"); // +
            out.println("</td></tr>");
            
            //Comment
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
        
            getServletPath("viewRes?&RETURNING=YES") + "\"'>");
            out.println("&nbsp;");
            out.println("</td><td>");
            out.println("<input type=button value=Save width=100 " +
            "style=\"WIDTH: 100px\" onClick='valForm()'>");
            
            out.println("&nbsp;");
            out.println("</td></tr></table>");
            out.println("</td></tr>");
            out.println("</table>");
            
            /* Hidden values
             * item: 
             */
            out.println("<input type=hidden name=item value=\"\">");
            out.println("<input type=hidden name=oper value=\"\">");
            out.println("<input type=hidden name=RETURNING value=YES>");
            
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
            
          
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (rset != null) rset.close();
            } catch (SQLException ignored) {
            }
        }
    }
    /** Used in writeNew to check the user given parameters.
     */
    
    private void writeNewScript(PrintWriter out) {
        out.println("<script language=\"JavaScript\">");
        out.println("<!--");
        out.println("	");
        out.println("function selChanged(item) {");
        out.println("  document.forms[0].item.value = \"\" + item;");
        out.println("  document.forms[0].oper.value = \"SEL_CHANGED\";");
        out.println("  document.forms[0].submit();");
        out.println("     }");
        out.println("function valForm() {");
        out.println("	");
        out.println("	var rc = 1;");
        out.println("	var i = 1;"); //index for sel_fgid
        out.println("     var wfgid = \"\";");
        out.println("     var sfgid = \"\";");
        out.println("     i = document.forms[0].sel_fgid.selectedIndex;");
        out.println("     sfgid = document.forms[0].sel_fgid.options[i].value;");
        out.println("     wfgid = document.forms[0].write_fgid.value;");
        out.println("     if (wfgid != \"\" && i!=0) {");
        out.println("         if ( wfgid != sfgid) {");
        out.println("             alert('The chosen file id is not the same as the written one!'); ");
        out.println("             rc = 0;");
        out.println("         }");
        out.println("     }");
  /*      out.println("     else if ( ( (\"\" + document.forms[0].RES.value) == \"\") {");
        out.println("           alert('You must create a result type!');");
        out.println("		rc = 0;");
        out.println("     }");   
        out.println("     else if ( ( (\"\" + document.forms[0].CAT.value) == \"\") {");
        out.println("           alert('You must create a category!');");
        out.println("		rc = 0;");
        out.println("     }");  */ 
        out.println("	  else if ( (\"\" + document.forms[0].filename.value) == \"\") {");
        out.println("			alert('The result file has to be given!');");
        out.println("			rc = 0;");
        out.println("     }");
        out.println("     else if (document.forms[0].filename.value.length > 80) {");
        out.println("			alert('The result filename and path must be less than 80 characters!');");
        out.println("			rc = 0;");
        out.println("     }");
        out.println("	  else if ( (\"\" + document.forms[0].bfilename.value) != \"\") {");
        out.println("		if (document.forms[0].bfilename.value.length > 80) {");
        out.println("			alert('The batch filename and path must be less than 80 characters!');");
        out.println("			rc = 0;");
        out.println("           }");
        out.println("     }");
        out.println("	  else if ( (\"\" + document.forms[0].comm.value) != \"\") {");
        out.println("		if (document.forms[0].comm.value.length > 2000) {");
        out.println("			alert('The comment must be less than 2000 characters!');");
        out.println("			rc = 0;");
        out.println("		}");
        out.println("	  }");
        out.println("	  ");
        out.println("     ");
        out.println("	 if (rc) {");
        out.println("		if (confirm('Are you sure that you want to save this Result?')) {");
        out.println("			document.forms[0].oper.value = 'CREATE'");
        out.println("			document.forms[0].submit();");
        out.println("		}");
        out.println("	 }");
        out.println("	");
        out.println("	");
        out.println("}");
        out.println("//-->");
        out.println("</script>");
    }
    
    
    private void writeEdit(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        Connection conn =  null;
        
        String oper = req.getParameter("oper");
        System.err.println("OPER i writeEdit: " + oper);
        conn = (Connection) session.getAttribute("conn");
        if (oper == null) oper = "";
        
        if (oper.equals("DELETE")) {
            updateResults(req, res, conn);
            if (deleteResults(req, res, conn)){
                writeFrame(req, res);     
            }
        }
        else if (oper.equals("UPDATE")) {
           if(updateResults(req, res, conn)){
                 writeFrame(req, res);
            }   
           
        }
           
           //        writeEditPage(req, res);
       else
        writeEditPage(req, res);
    }
    
    
    /** Displays the Result - Edit page.
     *
     */
    
    private void writeEditPage(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        res.setContentType("text/html");
        String rid = req.getParameter("rid");
        String suid;
        PrintWriter out = res.getWriter();
        // set content type and other response header fields first
        res.setContentType("text/html");
        Connection conn =  null;
        Statement stmt = null; 
        ResultSet rset = null;
        Statement stmt_ctg = null;
        ResultSet rset_ctg = null;
        Statement stmt_rtype = null;
        ResultSet rset_rtype = null;
        
        try {
            
            conn = (Connection) session.getAttribute("conn");
            stmt = conn.createStatement();
            String oldQS = buildQS(req);
            
     /*    RID         NUMBER (38)   NOT NULL,
  FGID        NUMBER (38)   NOT NULL,
  R_NAME      VARCHAR2 (80)  NOT NULL,
  R_FILE      BLOB           NOT NULL,
  R_TYPE      NUMBER (38)  NOT NULL,
  B_NAME      VARCHAR2 (80),
  B_FILE      CLOB,
  CTG         NUMBER (38)   NOT NULL,
  COMM        VARCHAR2 (2000),
  ID          NUMBER (38)   NOT NULL,
  TS          DATE          NOT NULL
      */
            String strSQL = "SELECT FGID, R_NAME, R_TYPE, B_NAME, CTG, COMM, C_TS, ID, to_char(C_TS,  '" + getDateFormat(session) + "') as C_TS " +
            "FROM RESULTS WHERE RESID=" + rid;       
            rset = stmt.executeQuery(strSQL);

            rset.next();
            
            out.println("<html>\n"
            + "<head>\n"
            + "<title>Results Edit</title>");
            writeEditScript(out);
            out.println(getDateValidationScript());
            out.println("</head>\n");
            HTMLWriter.css(out,getURL("style/axDefault.css"));
            out.println("<body bgcolor=\"fafad2\">\n");
            // title
            out.println("<table width=846 border=0 cellspacing=0 cellpadding=0 valign=top>");
            out.println("<tr>");
            out.println("<td width=14 rowspan=3></td>");
            out.println("<td width=736 colspan=2 height=15>");
            out.println("<center>" +
            "<b style=\"font-size: 15pt\">Results - Edit</b></font></center>");
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
            out.println("<tr><td>Result file name</td><td>" + formatOutput(session, rset.getString("r_name"), 25) + "</td></tr>");
            out.println("<tr><td>Batch file name</td><td>" + formatOutput(session, rset.getString("b_name"), 25) + "</td></tr>");
            out.println("<tr><td>Created</td><td>" + formatOutput(session, rset.getString("C_TS"), 25) + "</td></tr>");
            out.println("<tr><td>File id</td><td>" + formatOutput(session, rset.getString("fgid"), 20) + "</td></tr>");
            
            out.println("<td>");
            out.println("</table></tr></td>");//static table
            
            out.println("<FORM action=\"" + getServletPath("viewRes/edit?") + oldQS + "\" method=\"get\" name=\"FORM1\">");
            // dynamic data table
            out.println("<tr><td>");
            out.println("<table border=0 cellpading=0 cellspacing=0 width=600>");
            out.println("<tr><td colspan=2 bgcolor=lightgrey><font size=\"+1\">Changable data</font></td></tr>");

             // Find the categories
            stmt_ctg = conn.createStatement();
            String sql_ctg = "SELECT CTGID, NAME FROM CATEGORY " +
                        "order by NAME";
            Errors.logDebug("Category sql="+sql_ctg);
            rset_ctg = stmt_ctg.executeQuery(sql_ctg);

            out.println("<tr><td width=200 align=left>Category</td>");
            out.println("<td width=200 align=left>Result Type</td></tr>");
            
            out.println("<tr><td><select name=ctg style=\"WIDTH: 200px\">");
            int curr_ctgid = 0;
            if (rset.getInt("CTG") == 0)  
                System.err.println("Error in writeEditPage, the result should have a category");
            else
               curr_ctgid = rset.getInt("CTG");
            while (rset_ctg.next()) 
            {
               if (curr_ctgid == rset_ctg.getInt("CTGID")) 
               {
                  out.println("<option selected value=\"" + rset_ctg.getInt("CTGID") + "\">" + rset_ctg.getString("NAME"));
               } 
               else 
               {
                  out.println("<option value=\"" + rset_ctg.getInt("CTGID") + "\">" + rset_ctg.getString("NAME"));
               }
            }
            out.println("</select></td>");
          
            
            // Find the result types
            stmt_rtype = conn.createStatement();
          
            String sql_rtype = "SELECT RTID, NAME FROM RTYPE " +
                        "order by NAME";
            //Errors.logDebug("RType sql="+sql_rtype);
            rset_rtype = stmt_ctg.executeQuery(sql_rtype);
           
            out.println("<td><select name=rtype style=\"WIDTH: 200px\">");
            int curr_rtid = 0;
            if (rset.getInt("R_TYPE") == 0)  
                System.err.println("Error in writeEditPage, result should have a result type");
            else 
               curr_rtid = rset.getInt("R_TYPE");
            while (rset_rtype.next()) 
            {
              
               if (curr_rtid == rset_rtype.getInt("RTID")) 
               {
               out.println("<option selected value=\"" + rset_rtype.getInt("RTID") + "\">" + rset_rtype.getString("NAME"));
               } 
               else 
               {
                  out.println("<option value=\"" + rset_rtype.getInt("RTID") + "\">" + rset_rtype.getString("NAME"));
               }
            }
            out.println("</select></td></tr>");
            
            // just a "newline"
            out.println("<td><tr></tr></td>");
            //Parse out the "'" from the comment.
            String comment = rset.getString("comm");
            if (comment == null) comment = new String("");
            comment = retrieveSymbol(comment);
            //The comment
            out.println("<td COLSPAN=3>Comment<br>");
            out.println("<textarea rows=10 cols=65 name=comm value=>"+comment+"</textarea>");
            out.println("</td></tr>");
            out.println("<tr>");
            out.println("<tr><td COLSPAN=3>");
            out.println("</td></tr>");
            
            out.println("</table></td></tr>"); /* end of dynamic data table */
            
             // buttons table
         out.println("<tr><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0 width=300>");
         out.println("<tr><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td></tr>" +
                     "<tr><td colspan=4 align=center>" +
                     "<input type=button style=\"WIDTH: 100px\" value=\"Back\" onClick='location.href=\""
//                     +getServletPath("viewInd?") + oldQS + "\"'>&nbsp;"+
                     +getServletPath("viewRes?&RETURNING=YES") + "\"'>&nbsp;"+
                     "<input type=reset value=Reset style=\"WIDTH: 100px\">&nbsp;"+
                     "<input type=button id=DELETE name=DELETE value=Delete style=\"WIDTH: 100px\" onClick='valForm(\"DELETE\")'>&nbsp;"+
                     "<input type=button id=UPDATE name=UPDATE value=Update style=\"WIDTH: 100px\" onClick='valForm(\"UPDATE\")'>&nbsp;");

         out.println("</td></tr>");
         out.println("</table></td></tr>");//end of button table
            
         // Store some extra information needed by doPost()
         out.println("<input type=hidden NAME=oper value=\"\">");
         out.println("<input type=hidden NAME=rid value=\"" + rid + "\">");
         out.println("<input type=\"hidden\"  NAME=RETURNING value=YES>");
         out.println("</FORM>");
         out.println("</table></td></tr>");/*end of information table*/
         out.println("</body>");
         out.println("</html>");            
        }
        catch (Exception e) {
            e.printStackTrace();
            out.println("<PRE>");
            e.printStackTrace(out);
            out.println("</PRE>");
        }finally {
            try {
                if (stmt != null) stmt.close();
                if (rset != null) rset.close();
                if (stmt_ctg != null) stmt_ctg.close();
                if (rset_ctg != null) rset_ctg.close();
                if (stmt_rtype != null) stmt_rtype.close();
                if (rset_rtype != null) rset_rtype.close();
            } catch (SQLException ignored) {
            }
        }
       
    }
    
    /** Used in writeEditpage to check the user given parameters
     *
     */
    private void writeEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("		if (confirm('Are you sure you want to delete the Result?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("                     if (document.forms[0].comm.value.length > 2000) {");
      out.println("                         alert('The comment must be less than 2000 characters!');");
      out.println("                     }");
      out.println("                     else if (confirm('Are you sure you want to update the Result?')) {");
      out.println("                         document.forms[0].oper.value='UPDATE';");
      out.println("                         rc = 0;");
      out.println("                     }");
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
   
   
    
    /** Extension of the Result - Details view where the individuals are printed out.
     * The page is build as follows:
     *
     *  |----------------------------------------------|
     *  |     Results - Details - Individuals          |
     *  |                                              |
     *  |----------------------------------------------| 
     *
     *      information table
     *  |----------------------------------------------|
     *  |  uppertable                                  |
     *  ||--------------------------------------------||
     *  ||  static data table        navigation table|||
     *  |||------------------------||----------------||| 
     *  |||                        ||                |||
     *  |||------------------------||----------------|||
     *  ||--------------------------------------------||
     *  |                                              |
     *  |  individual header table                     |
     *  ||--------------------------------------------||
     *  ||                                            ||
     *  ||--------------------------------------------||
     *  |                                              |
     *  |  individuals table                           |
     *  ||--------------------------------------------||
     *  ||                                            ||
     *  ||--------------------------------------------||
     *  |   back button table                          |
     *  ||--------------------------------------------||
     *  ||                                            ||
     *  ||--------------------------------------------||
     *  |----------------------------------------------|
     */
    
    private void writeIndividuals(HttpServletRequest req,
                             HttpServletResponse res)
                             throws IOException
    {    
      int curr_fgid = 0; 
      String curr_r_name = "null";
      int curr_r_type = 0;
      String curr_b_name = "null";
      int curr_ctg = 0;
      String curr_comm = "null";
      int curr_id = 0;
      String curr_ts = "null";
      String creation_ts = "null";
      boolean odd = true; //for bgcolor when the ind are displayed.
      String param = "param"; //used for the hidden value, phen or geno can be choosen.
      String chosen = "nothing"; //
      String QS = req.getQueryString();
      PrintWriter out = res.getWriter();
 
      Statement indStmt = null;  
      ResultSet indRset = null;   
      
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");     
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");

      try {
        HttpSession session = req.getSession(true);
     
        // The result that should be shown
        String rid = req.getParameter("rid");
        String fgid = req.getParameter("fgid");
        String r_name = req.getParameter("r_name");
        String b_name = req.getParameter("b_name").trim();
        String c_ts = req.getParameter("c_ts");
        String action = req.getParameter("indAction");
    
        System.err.println("rid: " + rid);
        System.err.println("fgid: " + fgid);
        System.err.println("results_name: " +r_name);
        System.err.println("c_ts" + c_ts);
        //output.append(setIndecis(suid, old_suid, action, req, session));
        
        if (rid==null) rid = new String("");
        if (fgid==null) fgid = new String("");
        if (r_name==null) r_name = new String("");
        if (b_name==null) b_name = new String("");
        if (c_ts==null) c_ts = new String("");
        if (action==null) action = new String("");
        
       System.err.println("indACTION: " + action);
 
       
        // Set content type and other response header fields first
        res.setContentType("text/html");
        Connection connection =  null;
    
        connection = (Connection) session.getAttribute("conn");
        HTMLWriter.css(out,getURL("style/axDefault.css"));        
        //HTMLWriter.css(out,getURL("style/bottom.css"));      
      
         //title
         out.println("<title>Details</title>\n"
                     + "<META HTTP_EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
         out.println("</head>\n<body>\n");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Results - Details - Individuals</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");

         // the information table
         out.println("<table border=0 cellspacing=0 cellpadding=0><tr>" +
                     "<td width=15></td><td></td></tr>");
        
         out.println("<tr><td>");
     
         //upper table
         out.println("<table nowrap border=0 cellSpacing=0>");
         out.println("<tr><td>");  
         // static data table
          // static data table
         out.println("<table nowrap border=0 cellSpacing=0>"); 
         out.println("<tr><td width=250 colspan=3 bgcolor=lightgrey><font size=\"+1\">Static data</font></td></tr>");
         out.println("<tr><td>Result filename   </td><td>" + formatOutput(session, r_name, 30) + "</td></tr>");
         out.println("<tr><td>Batch filename   </td><td>" + formatOutput(session, b_name, 30) + "</td></tr>");
         out.println("<tr><td>Created   </td><td>" + formatOutput(session, c_ts, 20) + "</td></tr>");
         out.println("<tr><td>File id   </td><td>" +formatOutput(session,fgid, 20) + "</td></tr>");
         out.println("</table>"); 
         //static data table
        
   
       /*****************For the navigation buttons*************************/
        
         //Count the rows that are displayed    
        int rows = 0;
        indStmt = connection.createStatement();
        String rowsSQL = "select count(*)"
                        + " from R_FG_IND R, INDIVIDUALS I, SAMPLING_UNITS SU, RESULTS RES "
                        + "where R.IID = I.IID AND R.SUID = I.SUID AND SU.SUID = R.SUID AND "
                        + "R.FGID = " + fgid + " AND RES.RESID = "+rid+" ORDER BY IDENTITY";
        
        indRset = indStmt.executeQuery(rowsSQL);
        if(indRset.next())
            rows = indRset.getInt(1);
       
        indStmt.close();
        indRset.close();
        
        int startIndex = 0;
        int maxRows = 0;
        if (req.getParameter("StartIndIndex") != null)
            startIndex = Integer.parseInt(req.getParameter("StartIndIndex"));
        else
            startIndex = 1;
  
        maxRows = getMaxRows(session);  
        out.println("</td><td width=50></td><td>");
        startIndex = setIndIndecis(action, rows, req, session);
        StringBuffer output = new StringBuffer(512);
        output.append("fgid=").append(fgid);
        output.append("&rid=").append(rid);
        output.append("&r_name=").append(r_name);
        output.append("&b_name=").append(b_name);
        output.append("&c_ts=").append(c_ts);
        output.append("&StartIndIndex").append(startIndex);
        output.append("&ROWS").append(rows);
       
        output.toString();
        
        //navigation button table
        out.println("<table border=0 cellpadding=0 cellspacing=0 width=135 align=\"right\">\n");
        out.println("<td colspan=4>\n");
            
        out.println("<tr><td width=68 colspan=2>"
            +"<input id=COUNT name=COUNT type=button value=\"Count\" width=\"69\""
            +"onClick='location.href=\"" +
                     getServletPath("viewRes/individuals?")
                     + output + "&indAction=COUNT\"' height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">&nbsp;</td>"); 
        out.println("<td width=68 colspan=2>"
            +"<input id=DISPLAY name=DISPLAY type=button value=\"Display\" width=\"69\""
            +"onClick='location.href=\"" +
                     getServletPath("viewRes/individuals?")
                     + output + "&indAction=DISPLAY\"' height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 66px\">&nbsp;</td></tr>");
        
        out.println("<tr><td width=34 colspan=1><input id=TOP name=TOP type=button value=\"<<\""
            +" onClick='location.href=\"" + getServletPath("viewRes/individuals?")
            + output +"&indAction=TOP\"' height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 33px\">&nbsp;</td>"); 
        out.println("<td width=34 colspan=1><input id=PREV name=PREV type=button value=\"<\""
            +" onClick='location.href=\"" + getServletPath("viewRes/individuals?")
            + output +"&indAction=PREV\"' height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 33px\">&nbsp;</td>");   
        out.println("<td width=34 colspan=1><input id=NEXT name=NEXT type=button value=\">\""
            +" onClick='location.href=\"" + getServletPath("viewRes/individuals?")
            + output +"&indAction=NEXT\"' height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 33px\">&nbsp;</td>");   
        out.println("<td width=34 colspan=1><input id=END name=END type=button value=\">>\""
            +" onClick='location.href=\"" + getServletPath("viewRes/individuals?")
            + output +"&indAction=END\"' height=15 style=\"font-size: 9pt; HEIGHT: 24px; WIDTH: 33px\">&nbsp;</td></tr>");   
        out.println("</table>");
        //navigation button table
        
       /***********************end navigation buttons****************************/              
         out.println("</td></tr>"); 
         out.println("</table>");
         //upper table
 
         out.println("</tr></td>");
         out.println("<tr><td></td></tr><tr><td></td></tr><tr><td></td></tr>");
       
         //Info line
         if(!action.equalsIgnoreCase("")) {
               // startIndex = Integer.parseInt(req.getParameter("StartIndIndex"));
                System.err.println("startIndex before bil: " + startIndex);
                out.println("<tr><td>");
                out.println("&nbsp;" +buildInfoLine(action, startIndex, rows, maxRows));
                out.println("</tr></td>");
            }
         out.println("<tr><td></td></tr>");
         out.println("<tr><td></td></tr>");
  
        
         //Identity header table 
         out.println("<tr><td>");
         out.println("<table bgcolor=\"#008B8B\" align=left border=0 cellSpacing=0 width=380>");
         out.println("<tr><td width=150 colspan=1 <font>Identity</font></td>");
         out.println("<td width=150 colspan=1 <font>Sampling unit</font></td>");
         out.println("<td width=80 colspan=1 <font>Changed</font></td></tr>");
         out.println("</table></td></tr>");
         //Identity header table
         out.println("<tr><td>");

            indStmt = connection.createStatement();
            String strSQL = "select I.IDENTITY, I.TS ITS, SU.NAME, RES.TS RESTS"
                        + " from R_FG_IND R, INDIVIDUALS I, SAMPLING_UNITS SU, RESULTS RES "
                        + "where R.IID = I.IID AND R.SUID = I.SUID AND SU.SUID = R.SUID AND "
                        + "R.FGID = " + fgid + " AND RES.RESID = "+rid+" ORDER BY IDENTITY";
            indRset = indStmt.executeQuery(strSQL);
        
        
            //Identity table
            out.println("<table align=left border=0 cellSpacing=0 width=380 style=\"margin-left:2px\">"); 
            java.util.Date cr_ts, ts;  
            // First we spawn rows!
            int rowCount = 0;
                if (startIndex > 1) { 
                    while ((rowCount++ < startIndex - 1) && indRset.next())
                    ;
                }
           boolean displayed = false;     
           rowCount = 0;
            if(indRset.next() && rowCount < maxRows && !"".equalsIgnoreCase(action) &&  !action.equalsIgnoreCase("COUNT")){
              //  cr_ts = indRset.getDate("RESTS");
                
                do{
                //    ts = indRset.getDate("its");
                    if (odd)
                        out.println("<tr bgcolor=white>");
                    else
                        out.println("<tr bgcolor=lightgrey>");
                    odd = !odd;
            
                    out.println("<TD WIDTH=150> " + formatOutput(session,indRset.getString("identity"),20) + "</TD>");        
                    out.println("<TD WIDTH=150> " + formatOutput(session,indRset.getString("name"),20) + "</TD>");
            
                    //if (ts.after(cr_ts))
                    if(indRset.getTimestamp("its").after(indRset.getTimestamp("RESTS")))
                        out.println("<TD WIDTH=80> " + formatOutput(session,"YES",5) + "</TD>");
                    else
                        out.println("<TD WIDTH=80> " + formatOutput(session,"",5) + "</TD>");
                    out.println("</TR>");
                    rowCount++;
                } while (indRset.next() && rowCount < maxRows);
                displayed = true;
            }
            out.println("<tr><td></td></tr>");
            out.println("<tr><td></td></tr>");
            out.println("</table></tr>"); 
            //identity table 
       
            indStmt.close();
            indRset.close();
            
            if(displayed){
                indStmt = connection.createStatement();
                String delSQL = "select COUNT(*) "
                        + " from R_FG_IND WHERE FGID = "+ fgid
                        + " AND IID not IN(select distinct I.IID "
                        + "from R_FG_IND R, INDIVIDUALS I, SAMPLING_UNITS SU, RESULTS RES "
                        + "where R.IID = I.IID AND R.SUID = I.SUID AND SU.SUID = R.SUID AND "
                        + "R.FGID = " + fgid + " AND RES.RESID = "+rid+")";
      
                indRset = indStmt.executeQuery(delSQL);
                int NoOfDel=0;
                String NoOf = "";
                
                if(indRset.next())
                    NoOfDel=indRset.getInt(1);  
                out.println("<tr><td>");
                out.println("<tr><td>Number of deleted individuals:  </td><td>"+NoOfDel+"</td></tr>");
                }
            //back button table
            out.println("<table>");
            out.println("<tr>");
            out.println("<td>");
            out.println("<input type=button name=BACK value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                     getServletPath("viewRes/details?") + QS + "\"'>&nbsp;");
            out.println("</tr></table>"); 
            //button table        
             
            
            // some hidden values
            out.println("<input type=\"hidden\" id=\"PARAM\" name=\"PARAM\" value=\"" + param + "\">");
            out.println("<input type=\"hidden\" id=\"StartIndIndex\" name=\"StartIndIndex\" value=\"" + startIndex + "\">");
            out.println("</td></tr></table>");  
            //information table
         
            out.println("</body></html>");
      } catch (SQLException e) {
          e.printStackTrace();
            out.println("<PRE>");
            e.printStackTrace(out);
            out.println("</PRE>");
   
        //} catch (java.text.ParseException p) {
          //  p.printStackTrace(out);
          //  System.err.println("FEL PÅ PARSERN!!!");
        } finally {
            try {
                if (indStmt != null) indStmt.close();
                if (indRset != null) indRset.close();
            } catch (SQLException ignored) {
            }
        }
     
      }
     
    
    /** Displays the Details page where the history data is shown.
     */
   private void writeDetails(HttpServletRequest req,
                             HttpServletResponse res) 
      throws ServletException, IOException
   {
      String curr_fgid = ""; 
      String curr_r_name = "";
      int curr_r_type = 0;
      String curr_b_name = "";
      int curr_ctg = 0;
      String curr_comm = "";
      int curr_id = 0;
      String curr_ts = "";
      String curr_c_ts = "";
       
      int hist_fgid = 0; 
      String hist_r_name = "";
      int hist_r_type = 0;
      String hist_b_name = "";
      int hist_ctg = 0;
      String hist_comm = "";
      int hist_id = 0;
      String hist_ts = "";
      
      String type = "";  //could be RT, Ctg or ID. Is used by printName.
      String str = ""; // could be comm or TS. Is uder by printString.
      
      String oldQS = req.getQueryString();
      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      PrintWriter out = res.getWriter();
      out.println("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
    
      // Statements for getting current and previous data.
      Statement currentStmt = null;
      Statement historyStatement = null;    
      ResultSet currResult = null;
      ResultSet historyResult = null;
 
      try 
      {
          HttpSession session = req.getSession(true);

          // The result that should be shown
          String rid = req.getParameter("rid");

          // Set content type and other response header fields first
          res.setContentType("text/html");
          Connection connection =  null;

          // Indicates that previous data for individual was found.
          boolean hasPrevData = false;

          connection = (Connection) session.getAttribute("conn");

          //Gets the current result data from the database.
          currentStmt = connection.createStatement();
          String strSQL = "SELECT FGID, R_NAME, R_TYPE, B_NAME, CTG, COMM, to_char(C_TS, '" + getDateFormat(session) + "') as C_TS, ID, to_char(TS, '" + getDateFormat(session) + "') as TS " +
                "FROM RESULTS WHERE RESID=" + rid;
          currResult = currentStmt.executeQuery(strSQL);

          if (currResult.next()) {
              curr_fgid = currResult.getString("fgid");
              curr_r_name = currResult.getString("r_name");
              curr_r_type = currResult.getInt("r_type");
              curr_b_name = currResult.getString("b_name");
              curr_ctg = currResult.getInt("ctg");
              curr_comm = currResult.getString("comm");
              curr_id = currResult.getInt("id");
              curr_ts = currResult.getString("ts");
              curr_c_ts = currResult.getString("C_TS");
          }       

          System.err.println("writeDetails, FGID: " + curr_fgid);
          //Gets the history result data from the results_log table
          historyStatement = connection.createStatement();
          strSQL = "SELECT FGID, R_NAME, R_TYPE, B_NAME, CTG, COMM, ID, to_char(TS, '" + getDateFormat(session) + "') as TS " +
                "FROM RESULTS_LOG WHERE RESID=" + rid + "order by TS desc";
          historyResult = historyStatement.executeQuery(strSQL);
          if (historyResult.next()) {
              hasPrevData=true;

              hist_r_type = historyResult.getInt("r_type");
              hist_ctg = historyResult.getInt("ctg");
              hist_comm = historyResult.getString("comm");
              hist_id = historyResult.getInt("id");
              hist_ts = historyResult.getString("ts");
          }

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
                         "<b style=\"font-size: 15pt\">Results - Details</b></center>" +
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
             //Result file
             out.println("<tr><td>Result file name </td><td>"+formatOutput(session, curr_r_name, 25)+"</td>");
             out.println("<td><a href=\"" + getServletPath("viewRes/download?") +
                        "rid=" + rid + "&file=res\">Download</a></td></tr>");
             //Batch file
             out.println("<tr><td>Batch file name</td><td>"+formatOutput(session, curr_b_name, 25)+"</td>");
             if(curr_b_name!=null)
                out.println("<td><a href=\"" + getServletPath("viewRes/download?") + "rid=" + rid + "&file=batch\">Download</a></td></tr>");
             //Exported files     
             out.println("<tr><td>Exported files</td><td></td>");
             if(curr_fgid!=null)
                out.println("<td><a href=\"" + getServletPath("viewFile/files?") + "fgid=" + curr_fgid + "&rid="+rid + "\">Download</a></td></tr>");
             //Creation date
             out.println("<tr><td>Created</td><td>" + formatOutput(session, curr_c_ts, 25) + "</td></tr>");
             //fgid
             out.println("<tr><td>File id</td><td>" + formatOutput(session, curr_fgid, 20) + "</td></tr>");

             out.println("<tr><td></td><td></td></tr><tr><td></td><td></tr>"); 

             //Individuals button
            // if(curr_fgid.compareTo("0")!=0){ //curr_fgid.compareTo("")!=0 || 
             if(curr_fgid!=null){
             out.println("<tr><td>");
             out.println("<input type=button name=individuals value=Individuals width=100 " +
                         "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                         getServletPath("viewRes/individuals?fgid=")
                         + currResult.getInt("fgid")
                         + "&r_name=" + currResult.getString("r_name")
                         + "&b_name=" + currResult.getString("b_name") 
                         + "&c_ts=" + currResult.getString("C_TS") +"&"
                         + oldQS +"\"'>&nbsp;");
             out.println("</td></tr>");
             }
             out.println("<tr><td></td><td></td></tr><tr><td></td><td></td></tr>");
             out.println("</table>"); 

             out.println("<tr><td></td><td></td></tr><tr><td></td><td>");

             // current data table
             out.println("<table nowrap align=left border=0 cellSpacing=0 width=840px>");
             out.println("<tr bgcolor=Black><td align=center colspan=5><b><font color=\"#ffffff\" >Current Data</font></b></td></tr>");
             out.println("<tr bgcolor= \"#008B8B\" >");
             out.println("<td nowrap WIDTH=110px>Category</td>");
             out.println("<td nowrap WIDTH=110px>Result type</td>");
             out.println("<td nowrap WIDTH=450px>Comment</td>");
             out.println("<td nowrap WIDTH=50px>User</td>");
             out.println("<td nowrap WIDTH=120px>Last updated</td></tr>");

             out.println("<tr bgcolor=white>");

             /* printName changes the color of the output string to red if prev_string
              * and curr_string are different. To avoid that current data that has 
              * no history is displayed in red, the hist_string are set to curr_str
              * so they are equal in the comparison in the printName and printString
              * functions.
              */

             if(!hasPrevData){ 
                hist_ctg=curr_ctg; 
                hist_r_type = curr_r_type;
                hist_comm = curr_comm;
                hist_id = curr_id;
                hist_ts = curr_ts;
             }

              /* The names of the result type, categories and id should be displayed, 
              * therefor their names must be fetched from the database. The function
              * printName does this. 
              */

             // Category
             out.println("<td>");
             printName(curr_ctg, hist_ctg, type="ctg", out, session, connection);
             out.println("</td>");

             //Result type
             out.println("<td>");
             printName(curr_r_type, hist_r_type, type="RT", out, session, connection);
             out.println("</td>");

             //Comment
             out.println("<td>"); 
             printComment(curr_comm, hist_comm, curr_ts, rid, out, session);
             out.println("</td>");

             //User
             out.println("<td>");
             printName(curr_id, hist_id, type="ID", out, session, connection);
             out.println("</td>");
             type="";

             //TS
             out.println("<td>");
             out.println(formatOutput(session, curr_ts, 22));
             out.println("</td>");
             out.println("</tr>");

             out.println("<tr bgcolor=Black>");
             out.println("<td align=center colspan=5><b><font color=\"#ffffff\">History</font></b></td></tr>");


             boolean odd = true; //used for keeping track of bgcolor

             /* Always writes out the current string and checks with one older 
              * string wether it has changed.
              */


             while(historyResult.next()){ 
                if (curr_comm == null) curr_comm = new String("");
                if (hist_comm == null) hist_comm = new String("");
                curr_r_type = hist_r_type; 
                curr_ctg = hist_ctg; 
                curr_comm = hist_comm; 
                curr_id = hist_id; 
                curr_ts = hist_ts; 

                hist_r_type = historyResult.getInt("r_type");
                hist_ctg = historyResult.getInt("ctg");
                hist_comm = historyResult.getString("comm");
                hist_id = historyResult.getInt("id");
                hist_ts = historyResult.getString("ts");

                if(curr_comm==hist_comm)
                     System.err.println("likamed");
                if (curr_comm.equals(hist_comm))
                     System.err.println("japp");

                if (odd)
                   out.println("<tr bgcolor=white>");
                else
                   out.println("<tr bgcolor=lightgrey>");
                odd = !odd;

                // Category
                out.println("<td>");
                printName(curr_ctg, hist_ctg, type="ctg", out, session, connection);
                out.println("</td>");

                //Result type
                out.println("<td>");
                printName(curr_r_type, hist_r_type, type="RT", out, session, connection);
                out.println("</td>");

                //Comment
                out.println("<td>");
                printComment(curr_comm, hist_comm, curr_ts, rid, out, session);
                out.println("</td>");

                //User
                out.println("<td>");
                printName(curr_id, hist_id, type="ID", out, session, connection);
                out.println("</td>");
                str="";

                //TS
                out.println("<td>");
               // out.println(histResult.getString("TS"));
                out.println(formatOutput(session, curr_ts, 22));
                //printString(curr_ts, hist_ts, str = "TS", out, session);
                out.println("</td>");
                out.println("</tr>");
             }
             /* Since we printed out the current value above, the last history
              * value has not been printed. Since it is the original value, we 
              * compare it with itself to be sure that it will be printed in black.
              */
             if(hasPrevData) {

                if (odd)
                   out.println("<tr bgcolor=white>");
                else
                   out.println("<tr bgcolor=lightgrey>");
                odd = !odd;
                 // Category
                out.println("<td>");
                printName(hist_ctg, hist_ctg, type="ctg", out, session, connection);
                out.println("</td>");

                //Result type
                out.println("<td>");
                printName(hist_r_type, hist_r_type, type="RT", out, session, connection);
                out.println("</td>");

                //Comment
                out.println("<td>");
                printComment(hist_comm, hist_comm, hist_ts, rid, out, session);
                out.println("</td>");

                //User
                out.println("<td>");
                printName(hist_id, hist_id, type="ID", out, session, connection);
                out.println("</td>");
                str="";

                //TS
                out.println("<td>");
               // out.println(currResult.getString("TS"));
                //printString(hist_ts, hist_ts, str = "TS", out, session);
                out.println(formatOutput(session, hist_ts, 22));
                out.println("</td>");

                out.println("</tr>");
             }

             out.println("<tr><td></td><td></td></tr><tr><td></td><td>"); //</tr>
             // Back button

             out.println("<tr><td></td><td></td></tr><tr><td></td><td></td></tr>");
             out.println("<form>");
             out.println("<tr>");
             out.println("<td>");
             out.println("<input type=button name=BACK value=Back width=100 " +
                         "style=\"WIDTH: 100px\" onClick='location.href=\"" +
                         getServletPath("viewRes?&RETURNING=YES")+ "\"'>&nbsp;");

             out.println("</form>");
             out.println("</td></tr></table>"); //current data

         //    out.println("<input type=\"hidden\" id=\"wrComm\" name=\"wrComm\" value=\"" + wrComm + "\">");
             out.println("</td></tr></table>");  //information table
             out.println("</body></html>");


        } 
      catch (SQLException e) 
      {
            e.printStackTrace();
            
            this.writeErrorPage(req,res,"Details","Failed to display details");
                    
         
      } 
      finally 
      {
         try 
         {
            if (currentStmt != null) currentStmt.close();
            if (historyStatement != null) historyStatement.close();
            if (currResult != null) currResult.close(); 
            if (historyResult != null) historyResult.close();
           
         }
         catch (SQLException ignored) 
         {} 
      }
    }

    
   /** Taken out the category, result type and user names from the respective 
    * tables when the history is shown, the changed values are hihglighted
    * in red. Used in writeDetails. 
    */
   
    private void printName( int curr, int hist, String str, PrintWriter out, HttpSession session, Connection connection) {
       Statement stmtSQL = null;
       ResultSet resSet = null;
       String sql = "";
       try {
           stmtSQL = connection.createStatement();
           if(str.compareTo("ctg")==0)
                sql = "SELECT NAME FROM CATEGORY WHERE CTGID=" + curr;
           else if (str.compareTo("RT")==0)
                sql = "SELECT NAME FROM RTYPE WHERE RTID=" + curr;
           else if (str.compareTo("ID")==0)
                sql = "SELECT USR FROM USERS WHERE ID=" + curr;

           resSet = stmtSQL.executeQuery(sql);
           resSet.next();       
           
           if(str.compareTo("ctg")==0 || str.compareTo("RT")==0){
               if (curr != hist) {    
                   out.println("<font color=red>" + formatOutput(session, resSet.getString("NAME") + "</font>", 20));
                 }
                else {
                out.println( formatOutput(session, resSet.getString("NAME"), 20));
                }
           }
           else if (str.compareTo("ID")==0){
            if (curr != hist) {    
                   out.println("<font color=red>" + formatOutput(session, resSet.getString("USR") + "</font>", 12));
                 }
                else {
                out.println( formatOutput(session, resSet.getString("USR"), 12));
                }   
           }
           
       } catch (SQLException e) {
          e.printStackTrace(out);
      }
     finally {
         try {
            if (stmtSQL != null) stmtSQL.close();
            if (resSet != null) resSet.close();
         } catch (SQLException ignored) { } 
      }
    }
    
    /** Checks if two strings are the same, if they are the latest value is 
     * highlighted in red. To read the complete comment, the writeComment method
     * is called via doGet(). The resid and the date must ne given.
     */
    
    private void printComment( String current, String history, String ts, String resid, PrintWriter out, HttpSession session) {
        
            if (current == null) current = new String("");
            if (history == null) history = new String("");
            String curr = retrieveSymbol(current);
            String hist = retrieveSymbol(history);
          
            if (("" + current).equals("" + history)){
                out.println(formatOutput(session, curr, 60)); 
                if(curr.compareTo("")!=0)
                    out.println(" <a href='#' onClick=\"window.open('comment?RESID="+resid+"&TS="+ts+"', "
                    +"'Title','toolbar=no,scrollbars=yes,resizable=yes,width=550,height=500');\">view</a>");
            }
            else {
                out.println("<font color=red>" + formatOutput(session, curr, 60) + "</font>");
                if(curr.compareTo("")!=0)
                    out.println("<a href='#' onClick=\"window.open('comment?RESID="+resid+"&TS="+ts+"', "
                    +"'Title','toolbar=no,scrollbars=yes,resizable=yes,width=550,height=500');\">view</a>");
                  
            }
   
    }

    /** Writes the comment in a new window on the results-details page. 
     *
     */
   private void writeComment(HttpServletRequest request,
                             HttpServletResponse response) 
                             throws IOException
   {
      HttpSession session = request.getSession(true);
      Connection conn = (Connection) session.getValue("conn");
      response.setContentType("text/html");
      response.setHeader("Cache-Control", "no-cache");
      response.setHeader("Pragma", "no-cache");
      PrintWriter out = response.getWriter();
      System.err.println("COMMENT");
      String resid = request.getParameter("RESID");
      String ts = request.getParameter("TS").trim();
      System.err.println("Resid, ts: " +resid+", "+ts);
      Statement stmt = null;
      ResultSet rset = null;
      
      try
      {
          //java.util.Date lts, rts;
          String comm = "";
          String dateLog = "";
          String dateRes = "";
          stmt = conn.createStatement();
         
         //  String SQL = " select l.comm lcomm, r.comm rcomm, l.TS LTS, "
         //               + "r.TS RTS "
         //               + "from results_log l, results r where l.RESID=" + resid;
         // 
          String SQL = " select l.comm as lcomm, r.comm as rcomm, to_char(l.TS, '"+ getDateFormat(session) +"') as LTS, "
                        + "to_char(r.TS,'"+ getDateFormat(session) +"') as RTS " 
                        + "from results_log l, results r where r.RESID=" + resid;
       /*  String SQL = " select l.comm lcomm, r.comm rcomm, to_char(l.TS, 'YYYY-MM-DD HH24:MI') as LTS, "
                        + "to_char(r.TS,'YYYY-MM-DD HH24:MI') as RTS "
                        + "from results_log l, results r where l.RESID=" + resid;*/
          rset = stmt.executeQuery(SQL);
         
          while(rset.next()){
           //   lts = rset.getDate("LTS");
           //   rts = rset.getDate("RTS");
           
              dateLog=rset.getString("LTS");
              dateRes=rset.getString("RTS");
              System.err.println("dateLOG: " + dateLog);
              System.err.println("dateRes" + dateRes);
              if(dateLog.compareTo(ts)==0) 
                   comm=rset.getString("lcomm");
               if(dateRes.compareTo(ts)==0)
                   comm=rset.getString("rcomm");
          }
          comm=retrieveSymbol(comm);
          System.err.println("Comment: " + comm);
          String titleString="Results - Details - Comment";
         // Write start of page: DOCTYPE, HTML, open/close HEAD and open
         // BODY
          HTMLWriter.doctype(out);
          HTMLWriter.openHTML(out);
          HTMLWriter.openHEAD(out, titleString);
          HTMLWriter.closeHEAD(out);
          HTMLWriter.openBODY(out,"");
         
          out.println("<TABLE cellSpacing=0 cellPadding=4 border=0>");
          out.println("<TR><TD><textarea rows=30 cols=70 value=>"+comm+"</textarea></TD></TR>");
          out.println("</TABLE>");
          HTMLWriter.closeBODY(out);
          HTMLWriter.closeHTML(out);
     
      }
      catch (SQLException e) 
      {
          e.printStackTrace();
          
          try
          {
            writeErrorPage(request,response,"Write comment","Error writing comment");    
          }
          catch (Exception ignore)
          {}
                  
      
          
          
      } 
      finally 
      {
         try 
         {
            if (stmt != null) stmt.close();
            if (rset != null) rset.close();
         } 
         catch (SQLException ignored) 
         {} 
      } 
   }
    
   /** Writes the results and batch file in a new window.
    *
    */
   
    private void sendFile(HttpServletRequest req, HttpServletResponse res) 
    {
        String rid;
        String file = ""; //res or batch
        String filename = "";
        String contentType;
        //OutputStream out = null;
        byte[] buf = null;
        Connection conn = null;
        HttpSession session = req.getSession(false);
        Statement stmt = null;
        ResultSet rset = null;
        
        PrintWriter out = null;
      
        try 
        {
            rid = req.getParameter("rid");
            file = req.getParameter("file");
            
            conn = (Connection) session.getAttribute("conn");
            DbResult results = new DbResult();
            String str = "";
            
            if (file.equals("res"))
            {
                OutputStream os = res.getOutputStream();
                contentType = new String("text/plain");
                res.setContentType(contentType);

                // Set the header to get correct filename
                res.setHeader("Content-Disposition", "inline; filename=file.txt");
                results.printResultFile(conn, Integer.valueOf(rid), os);
            }
            else if (file.equals("batch"))
            {
                str = new String(results.getBatchFile(conn, Integer.valueOf(rid)));
            }
            
            
                    
                    
                    
                    
            /*
            conn = (Connection) session.getAttribute("conn");
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT r_name, r_file, b_name, b_file FROM results " +
                                  "WHERE RESID=" + rid);
            if (rset.next()) 
            {
                if(file.compareTo("res")==0){
                    Blob blob = null;   
                    filename = rset.getString("R_NAME");  
                    blob = rset.getBlob("R_FILE");
                    // Test for blob file operation 
                              
                    if (blob == null)
                        throw new Exception("Blob is null");
                
                    InputStream is = blob.getBinaryStream();                              
                    contentType = new String("text/plain");
                    res.setContentType(contentType);
                
                    // Set the header to get correct filename
                    res.setHeader("Content-Disposition", "inline; filename=" + filename);                          
                    buf = new byte[256 * 1024]; // 256 KB
                    int bytesRead;
                    out = res.getOutputStream();
                    while ((bytesRead = is.read(buf)) != -1)
                    {
                        out.write(buf, 0, bytesRead);
                    }
                }
                if(file.compareTo("batch")==0){
                    Clob clob = null;   
                    filename = rset.getString("B_NAME");  
                    clob = rset.getClob("B_FILE");
                    // Test for blob file operation 
                              
                    if (clob == null)
                        throw new Exception("Blob is null");
                
                    InputStream is = clob.getAsciiStream();                              
                    contentType = new String("text/plain");
                    res.setContentType(contentType);
                
                    // Set the header to get correct filename
                    res.setHeader("Content-Disposition", "inline; filename=" + filename);                          
                    buf = new byte[256 * 1024]; // 256 KB
                    int bytesRead;
                    out = res.getOutputStream();
                    while ((bytesRead = is.read(buf)) != -1)
                    {
                        out.write(buf, 0, bytesRead);
                    }
                }
             
            }
             */
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            try
            {
                writeErrorPage(req,res,"Import.FileNotFound","Error, file not found: "+ filename,"importFile");                
            }
            catch (Exception dontcare)
            {
                System.err.println(dontcare.getMessage());
            }
        }     
        finally 
        {
            try 
            {
                if (rset != null) rset.close();
                if (stmt != null) stmt.close();
            } 
            catch (Exception ignored) 
            {}
        }
    }
  
 /** Stores a new result by passing the parameter values given by the user to
     * the Db CreateResult.
     */
    private boolean createResult(HttpServletRequest request,
    HttpServletResponse response)
    throws ServletException { 
        System.err.println("ViewRes_CREATE_RESULT");
        boolean isOk = true;
        String errMessage = null;
        Connection connection = null;
        
        Statement stmt = null;
        ResultSet rset = null;
        
        HttpSession session = request.getSession(true);
        connection = (Connection) session.getValue("conn");
        //  connection.setAutoCommit(false);
        
        String upPath = getUpFilePath();
        // Create path if it does not exist
        createPath(upPath);
         PrintWriter out = null;
        try{
            out = response.getWriter();
            MultipartRequest multiRequest =
            new MultipartRequest(request, upPath, 5 * 1024 * 1024);
        
            int id, fgid, pid; // w_fgid is the fgid that the user writes himself
            fgid = 0;
            pid = 0;
            int w_fgid = 0; 
            int s_fgid = 0;
            String rname = null, rfile = null, bname = null, bfile = null, comm = null;
            int rtype = 0, ctg = 0;
    
            id = Integer.parseInt((String) session.getValue("UserID"));
            pid = Integer.parseInt((String) session.getValue("PID"));
        
            // String pid = (String) session.getAttribute("PID");
            //The fgid writer returns null when it is empty and the selected fgid resturns 0
       
            if (multiRequest.getParameter("write_fgid") == null ||
                multiRequest.getParameter("write_fgid").trim().equals("")) {
                w_fgid = 0;
            }
            else {
                w_fgid = Integer.parseInt(multiRequest.getParameter("write_fgid").trim());
            }
        
            s_fgid = Integer.parseInt(multiRequest.getParameter("sel_fgid"));
            //if there was no written fgid, take the selected one.
            if(s_fgid == 0 && w_fgid == 0)
                fgid = 0;
            else if (s_fgid != 0 && w_fgid !=0 && s_fgid != w_fgid)
                throw new Exception("selected fgid not the same as the written");
            else if(s_fgid != 0 && w_fgid == 0)
                fgid = s_fgid;
            else if (s_fgid == 0 && w_fgid != 0)
                fgid = w_fgid;
            else fgid = 0;
           
            if(fgid!=0){
                stmt = connection.createStatement();
                String sql = "select distinct fgid from r_fg_ind where fgid ="+fgid+ "and suid in "
                           + "(select suid from gdbadm.V_SAMPLING_UNITS_3 where STATUS = 'E' and pid ="+pid+")"; 
                rset = stmt.executeQuery(sql);
        
           if(!rset.next()){
                 writeErrorPage(request, response, "Results.New", Errors.keyValue("Results.New.FgidNotFound.Error.Msg"), "viewRes/new" );
                 throw new Exception("The given file id does not exist!");

                }
            }
            Enumeration files = multiRequest.getFileNames();
            while (files.hasMoreElements()) {
                String givenFileName = (String) files.nextElement();
                String systemFileName = multiRequest.getFilesystemName(givenFileName);
                if (givenFileName.compareTo("filename")==0)
                    rname = systemFileName;
                else if (givenFileName.compareTo("bfilename")==0)
                    bname = systemFileName;
            }
        
            rtype = Integer.parseInt(multiRequest.getParameter("RES"));
            comm = multiRequest.getParameter("comm");   
            ctg = Integer.parseInt(multiRequest.getParameter("CAT"));
            
            String resPath = upPath + "/" + rname;
            String batPath = upPath + "/" + bname;
       
            System.err.println("CreateREsult viewRes, result_name:" + rname);
            System.err.println("CreateREsult viewRes, batch_name:" + bname);
            connection.setAutoCommit(false); 
            DbResult dbr = new DbResult();
            dbr.CreateResult(connection, fgid, rname, rtype, bname, ctg, comm, id, resPath, batPath, pid);
            errMessage = dbr.getErrorMessage();
        
        }
        catch (SQLException sqle)
        {
           try{
                if(sqle.getMessage()=="comment")
                    writeErrorPage(request, response, "Results.New", 
                    Errors.keyValue("Results.Edit.Comment.Error.Msg"), "viewRes/new" );
                if(sqle.getMessage()=="RName")
                    writeErrorPage(request, response, "Results.New", 
                    Errors.keyValue("Results.New.RName.Error.Msg"), "viewRes/new" );
                if(sqle.getMessage()=="BName")
                    writeErrorPage(request, response, "Results.New", 
                    Errors.keyValue("Results.New.BName.Error.Msg"), "viewRes/new" );
                isOk = false;
                sqle.printStackTrace(System.err);
               } catch (IOException ioe) {
                    ioe.printStackTrace(System.err);
               }
        }
        catch (FileNotFoundException fnf){
            try{
                if(fnf.getMessage()=="result")
                    writeErrorPage(request, response, "Results.New", Errors.keyValue("Results.New.ResultFileNotFound.Error.Msg"), "viewRes/new" );
                else if(fnf.getMessage()=="batch")
                    writeErrorPage(request, response, "Results.New", Errors.keyValue("Results.New.BatchFileNotFound.Error.Msg"), "viewRes/new" );
                isOk = false;
                fnf.printStackTrace(System.err);
               } catch (IOException ioe) {
                    ioe.printStackTrace(System.err);
               }
        }
        catch (IOException ioe){
          /*  try{
                writeErrorPage(request, response, "Results.New", Errors.keyValue("Results.New.FileLengthExceded.Error.Msg"), "viewRes/new" );
                isOk = false;
                ioe.printStackTrace(System.err);
           } catch (IOException e) {
                e.printStackTrace(System.err);
           }*/
           isOk = false;
           HTMLWriter.writeErrorPage(out,"File error!",ioe.getMessage());
        }
  
        catch (Exception e) {
         // Flag for error and set the errMessage if it has not been set
            isOk = false;
            e.printStackTrace(System.err);
            if (errMessage == null)
            {
                errMessage = e.getMessage();
            }
      }
         finally {
            try {
                if (rset != null) rset.close();
                if (stmt != null) stmt.close();
            } catch (SQLException ignored) {}
        }
       try{
             // if commit/rollback ok and database operation ok, write the frame
            //since the writeErrorPage already has been written, nothing will be
            //executed here.
            if (commitOrRollback(connection, request, response,
                    "Results.New", errMessage,
                    "viewRes/new", isOk) && isOk) {
                writeFrame(request, response);
        }
       }catch (IOException e){
            e.printStackTrace(System.err);
       }
         
        return isOk;
       
    }
    /** Updates the results by passing the given parameters to the Db UpdateResult.
     */
    
    private boolean updateResults(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Connection connection) throws ServletException
   {
      String errMessage = null;
      boolean isOk = true;
      String oldQS = request.getQueryString();
      try
      {
         HttpSession session = request.getSession(true);
         connection.setAutoCommit(false);
         String UserID = (String) session.getAttribute("UserID");
         int pid = Integer.parseInt((String) session.getValue("PID"));
         String comm;
         int rid = 0, cat = 0, rtype = 0;
          
         rid = Integer.parseInt(request.getParameter("rid"));
         cat = Integer.parseInt(request.getParameter("ctg"));
         rtype = Integer.parseInt(request.getParameter("rtype"));
         comm = request.getParameter("comm");

         DbIndividual dbi = new DbIndividual();
         DbResult dbr = new DbResult();
         
         dbr.UpdateResults(connection, rid, cat, rtype, comm, Integer.parseInt(UserID), pid); 
        
         errMessage = dbi.getErrorMessage();
         System.err.println("update Result: " + errMessage);
       //  Assertion.assertMsg(errMessage == null ||
         //                 errMessage.trim().equals(""), errMessage);
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
      
      int rid = Integer.parseInt(request.getParameter("rid"));
      oldQS = removeQSParameterOper("oper");
      
      commitOrRollback(connection, request, response,
                       "Results.Edit", errMessage, "viewRes/edit?rid="+rid+"&"+oldQS,
                       isOk);
      return isOk;
   }
   
    /** Deletes results by calling the Db DeleteResult function.
     */
     private boolean deleteResults(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Connection connection)
   {
      String errMessage = null;
      boolean isOk = true;
      try
      {
         HttpSession session = request.getSession(true);
         int mid, resid;
         connection.setAutoCommit(false);
         String UserID = (String) session.getAttribute("UserID");
         resid = Integer.parseInt(request.getParameter("rid"));
         DbResult dbres = new DbResult();
         dbres.DeleteResults(connection, resid, UserID);
         errMessage = dbres.getErrorMessage();
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
                       "Results.Edit.Delete", errMessage, "viewRes",
                       isOk); 
      return isOk;
   }
    
} 



