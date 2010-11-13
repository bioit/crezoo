package org.tgdb.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class service extends HttpServlet {
   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            URL			content     = null;
            PrintWriter		out         = response.getWriter();
            URLConnection	contentCon  = null;
            String		wsName      = (String) request.getParameter("ws");
            String		contentType = null;
            String		wsdlUrl     = null;
            String		from        = null;
            String		to          = null;
            BufferedReader	rd          = null;
            String		dataWsdl    = "";
 
 
            if (wsName != null) {
 
 
                wsdlUrl = "http://" + request.getHeader("Host") + "/" + request.getContextPath() + "/" + wsName + "?WSDL";
                from        = InetAddress.getLocalHost().getCanonicalHostName() + ":" + request.getLocalPort() ;
                to          = request.getHeader("Host");
 
 
                content     = new URL(wsdlUrl);
 
                contentCon  = content.openConnection();
 
                contentType = contentCon.getContentType();
 
                response.setContentType(contentType);
 
                rd = new BufferedReader(new InputStreamReader(contentCon.getInputStream()));
 
                String	line;
 
                while ((line = rd.readLine()) != null) {
                    dataWsdl += line;
                }
 
                 dataWsdl = dataWsdl.replaceAll(from, to);
 
 
                out.print(dataWsdl);
            }
            out.close();
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="servlet methods">
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    public String getServletInfo() {
        return "service servlet resolves wsdl soap address issues of glassfish server";
    }
    
    // </editor-fold>

}
