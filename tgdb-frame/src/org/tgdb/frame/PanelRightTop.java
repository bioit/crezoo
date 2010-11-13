package org.tgdb.frame;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class PanelRightTop extends HttpServlet {
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        PrintWriter out = response.getWriter();
        
        out.println("<div id=\"panel-right-top\">");
//            out.println("&nbsp;");
//        out.println("<form method=\"post\" action=\"Controller?workflow=SearchKeywordFast\">");
//        out.println("<span>Search</span>");
//        out.println("<input type=\"text\" name=\"fast_search_key\" />");
//        out.println("</form>");
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