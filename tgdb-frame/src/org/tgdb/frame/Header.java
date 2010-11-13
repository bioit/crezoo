package org.tgdb.frame;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Header extends HttpServlet {
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        PrintWriter out = response.getWriter();
        
        //metas
        out.println("<meta name=\"robots\" content=\"index,follow\"/>");
        out.println("<meta name=\"description\" content=\"CreZOO &mdash; Transgenic Mice Database\"/>");
        out.println("<meta name=\"keywords\" content=\"crezoo, transgenic, mice, transgenic mice, tgdb, tg, cre, biological resources, cre8, mouse resources, databases\"/>");
        out.println("<meta name=\"revisit-after\" content=\"7 days\"/>");
        out.println("<meta name=\"author\" content=\"Michael Zouberakis\"/>");
        
        //css
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/crezoo-divs.css\" />");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/crezoo-con.css\" />");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/superfish.css\" />");
//        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"jquery.treeview.css\" />");
        
        //javascripts
//        out.println("<script type=\"text/javascript\" src=\"javascripts/jquery.min.js\"></script>");
        out.println("<script type=\"text/javascript\" src=\"javascripts/jquery-1.3.2.min.js\"></script>");
        out.println("<script type=\"text/javascript\" src=\"javascripts/hoverIntent.js\"></script>");
        out.println("<script type=\"text/javascript\" src=\"javascripts/superfish.js\"></script>");
        out.println("<script type=\"text/javascript\" src=\"javascripts/supersubs.js\"></script>");
        out.println("<script type=\"text/javascript\" src=\"javascripts/jquery-ui-1.7.3.custom.min.js\"></script>");
        out.println("<script type=\"text/javascript\" src=\"javascripts/crezoo.js\"></script>");
//        out.println("<script src=\"javascripts/jquery.cookie.js\" type=\"text/javascript\"></script>");
//        out.println("<script src=\"javascripts/jquery.treeview.js\" type=\"text/javascript\"></script>");
//        out.println("<script type=\"text/javascript\" src=\"javascripts/demo.js\"></script>");

//        out.println("<!--[if lt IE 7.]>");
//        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/pngfix.css\" />");
//        out.println("<![endif]-->");
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
        return "Header";
    }
    // </editor-fold>
}