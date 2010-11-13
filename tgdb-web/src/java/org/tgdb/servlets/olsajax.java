package org.tgdb.servlets;

import java.io.*;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class olsajax extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/xml;charset=utf-8");

        PrintWriter out = response.getWriter();
//        out.flush();
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        
        try {
            String uri = request.getParameter("uri") + "&termname=" + request.getParameter("termname") + "&ontologyname=" + request.getParameter("ontologyname");
//            out.println("<uri>"+uri+"</uri>");
            InputStream nptstr = new URL(uri).openStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(nptstr);
            out.println("<items>");
            NodeList nl = doc.getElementsByTagName("item");
            for(int nli = 0; nli < nl.getLength(); nli++) {
                Element node = (Element) nl.item(nli);
                out.println("<item>");
                NodeList nl1 = node.getElementsByTagName("name");
                for(int nl1i = 0; nl1i < nl1.getLength(); nl1i++) {
                    out.println("<name>");
                    out.println(nl1.item(nl1i).getTextContent());
                    out.println("</name>");
                }
                NodeList nl2 = node.getElementsByTagName("value");
                for(int nl2i = 0; nl2i < nl2.getLength(); nl2i++) {
                    out.println("<value>");
                    out.println(nl2.item(nl2i).getTextContent());
                    out.println("</value>");
                }
                out.println("</item>");
            }
            out.print("</items>");
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.close();
    }

    // <editor-fold defaultstate="collapsed">
    
//    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

//    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
    public String getServletInfo() {
        return "";
    }
    // </editor-fold>
}
