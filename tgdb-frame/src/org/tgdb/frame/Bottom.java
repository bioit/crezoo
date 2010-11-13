package org.tgdb.frame;
import org.tgdb.util.Timer;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.Logger;

public class Bottom extends HttpServlet {
    
    private static Logger logger = Logger.getLogger(Menu.class);
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Set to expire far in the past.
        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html>");
        out.println("<head>");
        out.println("<title>tgdb bottom frame</title>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"test.css\" />");
        out.println("</head>");
        out.println("<body>");
        out.println("<table width=\"100%\"  border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
        out.println("<tr>");
        
        Caller caller = (Caller)request.getSession().getAttribute("caller");
        
        if (caller==null || caller.getUsr().compareTo("public")==0){
            out.println("<td height=\"30px\" bgcolor=\"#000000\" style=\"color:#FFFFFF\" colspan=\"2\" align=\"left\">&nbsp;&nbsp;<b>visitor</b></td>");
        } else{
            out.println("<td height=\"30px\" bgcolor=\"#000000\" style=\"color:#FFFFFF\" colspan=\"2\" align=\"left\">&nbsp;&nbsp;<b>"+caller.getName()+"</b></td>");
        }
        
        out.println("<td height=\"30px\" bgcolor=\"#000000\" style=\"color:#FFFFFF\" colspan=\"2\" align=\"right\">");
        
        if (caller!=null && caller.hasPrivilege("MODEL_W")){
            out.println("<a target=\"page\" href=\"Controller?workflow=CreateModel\" class=\"tgdblink\"> add transgenic mouse</a>|");
            out.println("<a target=\"page\" href=\"Controller?workflow=CreateGene\" class=\"tgdblink\"> add transgene </a>|");
        }
        
        out.println("contact |");
	out.println("<a target=\"page\" href=\"Controller?workflow=logout\" class=\"tgdblink\"> <b>logout</b> </a>&nbsp;&nbsp;");
        out.println("</td></tr></table>");
        out.println("</body>");
        out.println("</html>");
         
        out.close();
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
