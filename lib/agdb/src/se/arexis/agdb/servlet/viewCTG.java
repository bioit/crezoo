/*
 * viewCTG.java
 *
 * Created on den 26 november 2003, 16:50
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
public class viewCTG extends SecureArexisServlet{
    
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
             writeNewCTGPage(req, res);  
           } else if (extPath.equals("/edit")) {
             writeEditCTGPage(req, res);
            } else if (extPath.equals("/writeNew")) {
             writeNew(req, res); 
           }else if (extPath.equals("/createCTG")) {
             createCTG(req, res);
           }
        
        
       
     }
 
  private void writeNew(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
      HttpSession session = req.getSession(true);
      Connection conn =  null;

      String oper = req.getParameter("oper");
      conn = (Connection) session.getValue("conn");
      
      if (oper.equals("UPDATE")) {
         if (updateCTG(req, res, conn))
          writeEditCTGPage(req, res);
      }
      else if (oper.equals("DELETE")) {
         if(deleteCTG(req, res, conn))
             writeEditCTGPage(req, res);
      }
      else {
         writeEditCTGPage(req, res);
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
       
    private void writeNewCTGPage(HttpServletRequest req, HttpServletResponse res)
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
         out.println("<title>New Category</title>");
         
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Category - New</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
    
         out.println("<form method=post action=\"" + getServletPath("viewCTG/createCTG") + "\">");
         out.println("<br><br>");
         out.println("<table border=0>");
         
         //Name
         out.println("<tr><td>");
         out.println("Category Name:");
         out.println("</label><br></font><input type=text name=ctg_name>");
         out.println("</td>");
         //some distance between the text fields
         out.println("<td>&nbsp</td>");
         //Comment
         out.println("<td>"); 
         out.println("Comment:");
         out.println("</label><br></font><input type=text name=ctg_comm>");
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
         
    //     out.println("<input type=hidden name=item value=\"\">");
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
        
    
    /** Displayes the Edit Category page and when the "save changes" is clicked,
     * the writeNew function is called (by getServletPath...) which calls the 
     * UpdateCTG (because the oper=UPDATE) followed by the writeNewCTGPage. 
     */
    
     private void writeEditCTGPage(HttpServletRequest req, HttpServletResponse res)
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
      String oper, c_name, tname, tcomm; //item
      c_name = null;
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
         c_name = req.getParameter("NAME");
         
         
         out.println("<html>");
         out.println("<head>");
         HTMLWriter.css(out,getURL("style/axDefault.css"));
         out.println("<base target=\"content\">");
         
         writeEditScript(out);
         out.println("<title>Edit Category</title>");
         
         out.println("</head>");
         out.println("<body>");
         out.println("<table width=\"846\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" valign=\"top\">" +
                     "<tr>" +
                     "<td width=\"14\" rowspan=\"3\"></td>" +
                     "<td width=\"736\" colspan=\"2\" height=\"15\">");
         out.println("<center>" +
                     "<b style=\"font-size: 15pt\">Category - Edit</b></center>" +
                     "</font></td></tr>" +
                     "<tr><td width=\"736\" colspan=\"2\" height=\"2\" bgcolor=\"#008B8B\">&nbsp;</td>" +
                     "</tr></table>");
         out.println("<form method=post action=\"" + getServletPath("viewCTG/writeNew") + "\">");
         out.println("<br><br>");
         out.println("<table border=0>");
         out.println("<td><font face=arial,geneva,helvetica size=1>Choose the category that you want to edit: ");
         out.println("</td>");
         
         out.println("<tr><td>Category name<br>");
         out.println("<SELECT id=NAME name=NAME WIDTH=150 height=25 " +
                     "style=\"HEIGHT: 25px; WIDTH: 150px\" " +
                     "onChange='selChanged()'>"); 
         stmt = conn.createStatement();
         rset = stmt.executeQuery("SELECT CTGID, NAME, COMM FROM Category" +
         " ORDER BY NAME");
       
         out.println("<OPTION value=no_value>*</option>");
         while (rset.next()) {
            if (c_name != null && c_name.equalsIgnoreCase(rset.getString("CTGID")))
               out.println("<OPTION selected value=\"" + rset.getString("CTGID") + "\">" +
                           rset.getString("NAME"));
            else
               out.println("<OPTION value=\"" + rset.getString("CTGID") + "\">" + 
                            rset.getString("NAME"));

         }
      
         out.println("</td></tr>");
         out.println("</select> </table>");
         rset.close();
         stmt.close();
     
        
        if(c_name!=null)
        if(!c_name.equalsIgnoreCase("no_value")){
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT NAME, COMM FROM Category WHERE " +
            "CTGID=" + c_name + " ");
            if (rset.next()) {  //if data exist
              tname = rset.getString("NAME");
              tcomm = rset.getString("COMM");
            }
         }
         
         
         if(tcomm==null) //if the comment from the db was null, still write a *
             tcomm="*";
         String ctgcomm = retrieveSymbol(tcomm);
         
         out.println("<table><tr><td>");
         out.println("Edit Category name:");
         out.println("</label><br></font><input type=text name=ctg_name value='" + tname + "'");
         out.println("</td>"); 
         
         out.println("<td>&nbsp</td>");
        
         out.println("<td COLSPAN=1>Edit Comment<br>");
         out.println("<textarea rows=1 cols=30 name=ctg_comm value=>"+ctgcomm+"</textarea>");
         out.println("</td></tr>");
         
         
         out.println("</td></tr><br><br><br></table>");
         rset.close();
         stmt.close();
      
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
      out.println("         if ( (\"\" + document.forms[0].ctg_name.value) == '*' || (\"\" + document.forms[0].ctg_name.value) == \"\") {");
      out.println("			alert('You must give a category name!');");
      out.println("			rc = 0;");
      out.println("      	}");
      out.println("         else if (confirm('Are you sure you want to delete the Category?')) {");
      out.println("			document.forms[0].oper.value='DELETE';");
      out.println("			rc = 0;");
      out.println("             }");
      out.println("                                                                 ");
      out.println("	} else if ('UPDATE' == action.toUpperCase()) {");
      out.println("             if ( (\"\" + document.forms[0].ctg_name.value) == '*' || (\"\" + document.forms[0].ctg_name.value) == \"\") {");
      out.println("			alert('You must give a category name!');");
      out.println("			rc = 0;");
      out.println("             }");   
      out.println("             else if ( (\"\" + document.forms[0].ctg_comm.value) != \"\") {");
      out.println("                 if (document.forms[0].ctg_comm.value.length > 256) {");
      out.println("			alert('The comment must be less than 256 characters!');");
      out.println("			rc = 0;");
      out.println("                  }");
      out.println("             }");
      out.println("             else if ( (\"\" + document.forms[0].ctg_name.value) != \"\") {");
      out.println("                  if (document.forms[0].ctg_name.value.length > 38) {");
      out.println("			alert('The category name must be less than 38 characters!');");
      out.println("			rc = 0;"); 
      out.println("                  }"); 
      out.println("             }");
      out.println("             if (rc == 1) {");
      out.println("                  if (confirm('Are you sure you want to update the category?')) {");
      out.println("			document.forms[0].oper.value='UPDATE';");
      out.println("			rc = 0;"); 
      out.println("                  }"); 
      out.println("             }"); 
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
      out.println("	if ( (\"\" + document.forms[0].ctg_name.value) == \"\") {");
      out.println("			alert('You must give a Category name!');");
      out.println("			rc = 0;");
      out.println("    	}");
      
      out.println("     if (document.forms[0].ctg_name.value.length > 38) {");
      out.println("			alert('The Category name must be less than 38 characters!');");
      out.println("			rc = 0;");
      out.println("	}");
     
      out.println("	if ( (\"\" + document.forms[0].ctg_comm.value) != \"\") {");
      out.println("		if (document.forms[0].ctg_comm.value.length > 256) {");
      out.println("			alert('The comment must be less than 256 characters!');");
      out.println("			rc = 0;");
      out.println("		}");
      out.println("	}");
      out.println("	");  
      out.println("	");
      out.println("	if (rc) {");
      out.println("		if (confirm('Are you sure that you want to save this Category?')) {");
      out.println("			document.forms[0].oper.value = 'UPDATE'");
     //out.println("alert('In function, create: ' + document.forms[0].oper.value) ");
      out.println("			document.forms[0].submit();");
      out.println("		}");
      out.println("	}");
      out.println("	");
      out.println("	");
      out.println("}");
      out.println("//-->");
      out.println("</script>");
   }
   
      private boolean createCTG(HttpServletRequest request,
                                  HttpServletResponse response)
                           throws IOException, ServletException       
   {
      System.err.println("ViewCtg_CREATE_CTG"); 
      boolean isOk = true;
      String errMessage = null;
      Connection connection = null;
      
      HttpSession session = request.getSession(true);
      connection = (Connection) session.getValue("conn");
      try 
      {
         connection.setAutoCommit(false);
      
         int id;
         int ctgid;
         String name = null, comm = null; 
         id = Integer.parseInt((String) session.getValue("UserID"));
      
         name = request.getParameter("ctg_name");
         comm = request.getParameter("ctg_comm");

         DbCtg dbctg = new DbCtg(); 
         dbctg.CreateCtg(connection, name, comm, id);  
          
      } 
      catch (SQLException sqle)
      {
           sqle.printStackTrace(System.err);
           isOk = false;
           try
           {
                if(sqle.getMessage()=="Comment")
                    writeErrorPage(request, response, "Category.New", 
                    Errors.keyValue("Category.New.Comment.Error.Msg"), "viewCTG/new" );
               if(sqle.getMessage()=="c_name")
                    writeErrorPage(request, response, "Category.New", 
                    Errors.keyValue("Category.New.Name.Error.Msg"), "viewCTG/new" );
           } 
           catch (IOException ioe) 
           {
                ioe.printStackTrace(System.err);
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
                           "Category.New", errMessage,
                           "viewCTG/new", isOk);
         
      // if commit/rollback ok and database operation ok, write the frame
     if (isOk)
            writeNewCTGPage(request, response);
      
      return isOk;
   }   
   
      /**
       * Calls the updateCTG in the DbCtg servlet. Reads the parametervalues from the screen.
       */
      
      
     private boolean updateCTG(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Connection connection) throws IOException, ServletException
   {
      System.err.println("ViewCtg_Update_CTG"); 
      boolean isOk = true;
      String errMessage = null;
      
      HttpSession session = request.getSession(true); 
      int id;
      int ctgid;
      String name = null, comm = null; 
      try {
        connection.setAutoCommit(false);
        id = Integer.parseInt((String) session.getValue("UserID"));  
        ctgid = Integer.parseInt((String) request.getParameter("NAME"));
        name = request.getParameter("ctg_name");
        comm = request.getParameter("ctg_comm");
        
        DbCtg dbctg = new DbCtg(); 
        dbctg.UpdateCtg(connection, ctgid, name, comm, id);  
        errMessage = dbctg.getErrorMessage(); 
      
      }catch (SQLException sqle){
           try{
                if(sqle.getMessage()=="Comment")
                    writeErrorPage(request, response, "Category.New", 
                    Errors.keyValue("Category.New.Comment.Error.Msg"), "viewCTG/edit" );
               if(sqle.getMessage()=="c_name")
                    writeErrorPage(request, response, "Category.New", 
                    Errors.keyValue("Category.New.Name.Error.Msg"), "viewCTG/edit" );
               
                isOk = false;
                sqle.printStackTrace(System.err);
               } catch (IOException ioe) {
                    ioe.printStackTrace(System.err);
               }
        } catch (Exception e) {
         // Flag for error and set the errMessage if it has not been set
         isOk = false;
         e.printStackTrace(System.err);
         if (errMessage == null)
         {
            errMessage = e.getMessage();
         }
       }  
        
      commitOrRollback(connection, request, response,
                           "Category.New.Update", errMessage,
                           "viewRes", isOk); 
    
      return isOk;
     
    }
     
  private boolean deleteCTG(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Connection connection) throws IOException
   {
      String errMessage = null;
      boolean isOk = true;
      PrintWriter out = response.getWriter(); 
     
      
      try
      {
         //HttpSession session = request.getSession(true);
         int ctgid;
          
         connection.setAutoCommit(false);
         ctgid = Integer.parseInt((String) request.getParameter("NAME"));
         DbCtg dbctg = new DbCtg(); 
         dbctg.DeleteCtg(connection, ctgid);  
         errMessage = dbctg.getErrorMessage(); 
         
         Assertion.assertMsg(errMessage == null ||
                          errMessage.trim().equals(""), errMessage);
      }
      
      catch (Exception e) 
      {
         // Flag for error and set the errMessage if it has not been set
         isOk = false;
         e.printStackTrace(System.err);
         HTMLWriter.writeErrorPage(out,"A result uses the category, only \"empty\" categories can be deleted." , e.getMessage());
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
