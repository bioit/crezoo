package org.tgdb.frame;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Foot extends HttpServlet {
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        PrintWriter out = response.getWriter();
        
        out.println("<div id=\"foot\">");
            out.println("<p>&copy;&nbsp;<a href=\"http://www.fleming.gr\" target=\"_blank\" title=\"fleming.gr\">&nbsp;fleming&nbsp;</a>2009</p>");
        out.println("</div>");
        
        
        //out.close();
    }
    
    // <editor-fold defaultstate="collapsed">
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
    public String getServletInfo() {
        return "PanelLeftTop";
    }
    
    // </editor-fold>
}