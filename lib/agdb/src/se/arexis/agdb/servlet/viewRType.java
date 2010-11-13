/*
 * viewRType.java
 *
 * Created on den 4 december 2003, 09:38
 */
package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;

/**
 *
 * @author  wali
 * @version
 */
public class viewRType extends SecureArexisServlet{
    
      
   public void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      doGet(req, res);
   }
   /**
    * This method dispatches the request to the corresponding
    * method. The servlet handles the surrounding frameset.
    * Two pages are displayed, one where the user can create 
    * a new category, writeNewCTGPage, and one where he can 
    * change the name and comment to the category, writeEditCTGPage. 
    */
   public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {

      HttpSession session = req.getSession(true);
     
        String extPath = req.getPathInfo();
        System.err.println("QS="+req.getQueryString());
        System.err.println("path="+extPath);
 
         if (extPath == null || extPath.equals("") || extPath.equals("/") || extPath.equals("/new")) {
             writeNewRTypePage(req, res);  
           } else if (extPath.equals("/edit")) {
             writeEditRTypePage(req, res);
            } else if (extPath.equals("/writeNew")) {
             writeNew(req, res); 
           }else if (extPath.equals("/createRType")) {
             createRType(req, res);
           }
        
        
       
     }
 
  private void writeNew(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;
      
      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      
      if (oper.equals("UPDATE")) {
         if (updateRType(req, res, conn))
          writeEditRTypePage(req, res);
      }
      else if (oper.equals("DELETE")) {
         if(deleteRType(req, res, conn))
             writeEditRTypePage(req, res);
      } else {
         writeEditRTypePage(req, res);
      }
   }
   
  /** Displays the "New Category" page. When the "Save" button is clicked, the 
   * form executes its action and the program is navigated to the createCtg function.
   * Some checks are made in the writeScript function, see below.
   * The page consists of two tables, one upper which contains "Category - new"
   * and one that contains the textfields and buttons. 
   * The hidden operator, oper, is not really used in this method, but the writeScript
   * is used for both writeNewCTGPage and writeEditCTGPage and must therefore be declared. 
   */
       
    private void writeNewRTypePage(HttpServletRequest req, HttpServletResponse res)
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
      String fgid, oper, newQS, suid, pid;
      pid = "NULL";
      int currentPrivs[];
      currentPrivs = (int [])session.getAttribute("PRIVILEGES");
      /* When the client makes a GET request to the server a HTML form is created and the 
       hidden fields contain the name submitted for that GET request plus hidden fields 
       containing each of the names submitted by each previous GET request.*/
      
      try {
         conn = (Connection) session.getValue("conn");
  
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<base target=\"content\">");

         writeScript(out);
         out.println("<title>New Result Type</title>");
         
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Result Type - New</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
    
         out.println("<form method=post action=\"" + getServletPath("viewRType/createRType") + "\">");
         out.println("<br><br>");
         out.println("<table border=0>");
         
         //Name
         out.println("<tr><td>");
         out.println("Result Type Name:");
         out.println("</label><br></font><input type=text name=RT_name>");
         out.println("</td>");
         //some distance between the text fields
         out.println("<td>&nbsp</td>");
         //Comment
         out.println("<td>"); 
         out.println("Comment:");
         out.println("</label><br></font><input type=text name=RT_comm>");
         out.println("</td></tr>");
         
         //the back button
         out.println("<tr><td>");
         out.println("<input type=button value=Back width=100 " +
                     "style=\"WIDTH: 100px\" onClick='document.location.href=\"" +
                   getServletPath("viewRes?&RETURNING=YES") + "\"'>");
         out.println("&nbsp;");        
         
         out.println("<input type=button value=Save width=100 " +
                    "style=\"WIDTH: 100px\" onClick='valForm()'>");
         out.println("</td></tr></table>");
        
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
            } catch (SQLException ignored) {}
        }
    }
    
    
    /** Displayes the Edit Category page and when the "save changes" is clicked,
     * the writeNew function is called (by getServletPath...) which calls the 
     * UpdateCTG (because the oper=UPDATE) followed by the writeNewCTGPage. 
     */
    
     private void writeEditRTypePage(HttpServletRequest req, HttpServletResponse res)
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
      String oper, RT_name, tname, tcomm; //item
      RT_name = null;
      tname = "*";
      tcomm = "*";
      int currentPrivs[];
      currentPrivs = (int [])session.getAttribute("PRIVILEGES");
      /* When the client makes a GET request to the server a HTML form is created and the 
       hidden fields contain the name submitted for that GET request plus hidden fields 
       containing each of the names submitted by each previous GET request.*/
      
      try {
         conn = (Connection) session.getValue("conn");
      
         oper = req.getParameter("oper");
         System.err.println("OPER: " + oper);
         RT_name = req.getParameter("NAME");
         
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<base target=\"content\">");

         writeEditScript(out);
         out.println("<title>Edit RType</title>");
         
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Result Type - Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
       
         out.println("<form method=post action=\"" + getServletPath("viewRType/writeNew") + "\">");
         out.println("<br><br>");
         out.println("<table border=0>");
         out.println("<td><font face=arial,geneva,helvetica size=1>Choose the result type that you want to edit: ");
         out.println("</td>");
         
         out.println("<tr><td>Result Type name<br>");
         out.println("<SELECT name=NAME WIDTH=150 height=25 " +
                     "style=\"HEIGHT: 25px; WIDTH: 150px\" " +
                     "onChange='selChanged()'>"); 
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT RTID, NAME, COMM FROM RTYPE" +
         " ORDER BY NAME");
       
         out.println("<OPTION value=no_value>*</option>");
         while (rset.next()) {
            if (RT_name != null && RT_name.equalsIgnoreCase(rset.getString("RTID")))
               out.println("<OPTION selected value=\"" + rset.getString("RTID") + "\">" +
                           rset.getString("NAME"));
            else
               out.println("<OPTION value=\"" + rset.getString("RTID") + "\">" + 
                            rset.getString("NAME"));

         }
      
         out.println("</td></tr>");
         out.println("</select> </table>");
         rset.close();
         stmt.close();
     
        
        if(RT_name!=null)
        if(!RT_name.equalsIgnoreCase("no_value")){
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT NAME, COMM FROM RTYPE WHERE " +
            "RTID=" + RT_name + " ");
            if (rset.next()) {
              tname = rset.getString("NAME");
              tcomm = rset.getString("COMM");
              System.err.println("tname: ++++" + tname);
              System.err.println("tcomm: ++++" + tcomm);
              
            }
         }
         
         if (tcomm == null) tcomm = new String("");
         String RTcomm = retrieveSymbol(tcomm);
         if(RTcomm==null)
             RTcomm="*";
         out.println("<table><tr><td>");
         out.println("Edit Result Type name:");
         out.println("</label><br></font><input type=text name=RT_name value='" + tname + "'");
         out.println("</td>"); 
         
         out.println("<td>&nbsp</td>");
        
         out.println("<td COLSPAN=1>Edit Comment<br>");
         out.println("<textarea rows=1 cols=30 name=RT_comm value=>"+RTcomm+"</textarea>");
         out.println("</td></tr>");
        
         out.println("</td></tr><br><br><br></table>");
         
         out.println("<tr><td>");
         out.println("<table border=0 cellpading=0 cellspacing=0 width=300>");
         out.println("<tr><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td><td width=200>&nbsp;</td></tr>" +
                     "<tr><td colspan=4 align=center>" +
                     "<input type=button style=\"WIDTH: 100px\" value=\"Back\" onClick='location.href=\""
                     +getServletPath("viewRes?&RETURNING=YES") + "\"'>&nbsp;"+
                     "<input type=reset value=Reset style=\"WIDTH: 100px\">&nbsp;"+
                     "<input type=button id=DELETE name=DELETE value=Delete style=\"WIDTH: 100px\" onClick='valForm(\"DELETE\")'>&nbsp;"+
                     "<input type=button id=UPDATE name=UPDATE value=Update style=\"WIDTH: 100px\" onClick='valForm(\"UPDATE\")'>&nbsp;");

         out.println("</td></tr>");
         out.println("</table></td></tr>");//end of button table
        
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
            } catch (SQLException ignored) {}
        }
    }
  
      private void writeEditScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("	");
      out.println("function selChanged() {");
      out.println("  document.forms[0].submit();");
      out.println("}");
      out.println("function valForm(action) {");
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ('DELETE' == action.toUpperCase()) {");
      out.println("             if ( (\"\" + document.forms[0].RT_name.value) == '*' || (\"\" + document.forms[0].RT_name.value) == \"\") {");
      out.println("			alert('You must give a Result Type name!');");
      out.println("			rc = 0;");
      out.println("             }");   
      out.println("		else if (confirm('Are you sure you want to delete the Result Type?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("             }");
      out.println("	");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("             if ( (\"\" + document.forms[0].RT_name.value) == '*' || (\"\" + document.forms[0].RT_name.value) == \"\") {");
      out.println("			alert('You must give a Result Type name!');");
      out.println("			rc = 0;");
      out.println("             }");   
      out.println("             else if ( (\"\" + document.forms[0].RT_comm.value) != \"\") {");
      out.println("                 if (document.forms[0].RT_comm.value.length > 256) {");
      out.println("			alert('The comment must be less than 256 characters!');");
      out.println("			rc = 0;");
      out.println("                  }");
      out.println("             }");
      out.println("             else if ( (\"\" + document.forms[0].RT_name.value) != \"\") {");
      out.println("                  if (document.forms[0].RT_name.value.length > 38) {");
      out.println("			alert('The Result type name must be less than 38 characters!');");
      out.println("			rc = 0;"); 
      out.println("                  }"); 
      out.println("             }");
      out.println("             if (rc == 1) {");
      out.println("                  if (confirm('Are you sure you want to update the Result Type?')) {");
      out.println("			document.forms[0].oper.value='UPDATE';");
      out.println("			rc = 0;"); 
      out.println("                  }"); 
      out.println("             }"); 
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
     
     /** valForm makes some checks on the comment and name parameters.
      * selChanged submits the form.
      */
     
      private void writeScript(PrintWriter out) {
      out.println("<script language=\"JavaScript\">");
      out.println("<!--");
      out.println("	");
      out.println("function selChanged() {");
      out.println("  document.forms[0].submit();");
      out.println("}");
      out.println("function valForm() {");
     
      out.println("	");
      out.println("	var rc = 1;");
      out.println("	if ( (\"\" + document.forms[0].RT_name.value) == \"\") {");
      out.println("			alert('You must give a Result Type name!');");
      out.println("			rc = 0;");
      out.println("    	}");
      out.println("     if (document.forms[0].RT_name.value.length > 38) {");
      out.println("			alert('The Result Type name must be less than 38 characters!');");
      out.println("			rc = 0;");
      out.println("	}");
      out.println("	if ( (\"\" + document.forms[0].RT_comm.value) != \"\") {");
      out.println("		if (document.forms[0].RT_comm.value.length > 256) {");
      out.println("			alert('The comment must be less than 256 characters!');");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	}");
      out.println("	");
      out.println("	");
      out.println("	if (rc) {");
      out.println("		if (confirm('Are you sure that you want to save this Result Type?')) {");
      out.println("			document.forms[0].oper.value = 'UPDATE'");
      out.println("			document.forms[0].submit();");
      out.println("		}");
      out.println("	}");
      out.println("	");
      out.println("	");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
   }
   
      private boolean createRType(HttpServletRequest request,
                                  HttpServletResponse response)
                           throws IOException, ServletException       
   {
      System.err.println("ViewRType_CREATE_RType"); 
      boolean isOk = true;
      String errMessage = null;
      Connection connection = null;
      
      HttpSession session = request.getSession(true);
      connection = (Connection) session.getValue("conn");
      
      try {
         connection.setAutoCommit(false);
         int id;
         int RTid;
         String name = null, comm = null; 
         id = Integer.parseInt((String) session.getValue("UserID"));
      
         name = request.getParameter("RT_name");
         comm = request.getParameter("RT_comm");
                
         DbRType dbRType = new DbRType(); 
         dbRType.CreateRType(connection, name, comm, id);  
         errMessage = dbRType.getErrorMessage(); 
 
      }  catch (SQLException sqle){
           try{
                if(sqle.getMessage()=="Comment")
                    writeErrorPage(request, response, "RType.New", 
                    Errors.keyValue("RType.New.Comment.Error.Msg"), "viewRType/new" );
               if(sqle.getMessage()=="RT_name")
                    writeErrorPage(request, response, "RType.New", 
                    Errors.keyValue("RType.New.Name.Error.Msg"), "viewRType/new" );
               
                isOk = false;
                sqle.printStackTrace(System.err);
               } catch (IOException ioe) {
                    ioe.printStackTrace(System.err);
               }
        }catch (Exception e) 
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
                           "RType.New", errMessage,
                           "viewRes", isOk);
      
     // if commit/rollback ok and database operation ok, write the frame
      if (isOk)
             writeNewRTypePage(request, response);
      
      return isOk;
   }   
   
      /**
       * Calls the updateCTG in the DbCtg servlet. Reads the parametervalues from the screen.
       */
      
      
     private boolean updateRType(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Connection connection)
   {
      System.err.println("ViewRType_Update_RType"); 
      boolean isOk = true;
      String errMessage = null;
      
      HttpSession session = request.getSession(true); 
      int id;
      int RTid;
      String name = null, comm = null; 
      try{
        connection.setAutoCommit(false); 
        id = Integer.parseInt((String) session.getValue("UserID")); 
        RTid = Integer.parseInt((String) request.getParameter("NAME"));
        name = request.getParameter("RT_name");
        comm = request.getParameter("RT_comm");
    
        DbRType dbRType = new DbRType(); 
        dbRType.UpdateRType(connection, RTid, name, comm, id);  
        errMessage = dbRType.getErrorMessage(); 
        Assertion.assertMsg(errMessage == null ||
                          errMessage.trim().equals(""), errMessage);
      }  catch (Exception e) 
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
                           "Rtype.New.Update", errMessage,
                           "viewRes", isOk); 
    
      return isOk;
     
    }

     private boolean deleteRType(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Connection connection) throws IOException
   {
      String errMessage = null;
      boolean isOk = true;
      PrintWriter out = response.getWriter(); 
    
      try
      {
         int RTid;
         connection.setAutoCommit(false);
         RTid = Integer.parseInt((String) request.getParameter("NAME"));
         DbRType dbRType = new DbRType(); 
         dbRType.DeleteRType(connection, RTid);  
        
      }
      catch (Exception e) 
      {
         // Flag for error and set the errMessage if it has not been set
         isOk = false;
         e.printStackTrace(System.err);
         HTMLWriter.writeErrorPage(out,"A result uses the result type, only \"empty\" result types can be deleted." , e.getMessage());
         if (errMessage == null)
         {
            errMessage = e.getMessage();
         }
      }
         
      commitOrRollback(connection, request, response,
                       "Category.Edit.Delete", errMessage, "viewCTG",
                       isOk); 
      return isOk;
   } 
}
