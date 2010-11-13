package org.tgdb.frame;

import org.tgdb.util.Timer;
import java.io.*;


import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PanelLeft extends HttpServlet {
    
    private static final String MENU_FILE = "xml/menu.xml";
    private static Logger logger = Logger.getLogger(PanelLeft.class);
    private String target;
    
    
    private Document doc;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
//        out.println("<html>");
//        out.println("<head>");
//        out.println("<title>Servlet Menu</title>");
//        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"nav.css\" />");
//        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"jquery.treeview.css\" />");
//        out.println("<script type=\"text/javascript\" src=\"javascripts/jquery.min.js\"></script>");
//        out.println("<script src=\"javascripts/jquery.cookie.js\" type=\"text/javascript\"></script>");
//        out.println("<script src=\"javascripts/jquery.treeview.js\" type=\"text/javascript\"></script>");
//        out.println("<script type=\"text/javascript\" src=\"javascripts/demo.js\"></script>");
//        out.println("</head>");
//        out.println("<body>");
        
        try {
            /* Read the xml data */
            parseXML();

            Caller caller = (Caller)request.getSession().getAttribute("caller");

            if (caller==null) {
                response.sendRedirect("Controller");
                logger.error("Caller was null says PanelLeft");
                return;
            }
            
            /* Write logo on top of menu */
            out.println("<div id=\"logo\">");
            out.println("<h1>CreZOO</h1>");
            out.println("<div id=\"shortcuts\">");
            if(caller != null && !caller.getUsr().equalsIgnoreCase("public")) {
                out.println("<a href=\"Controller?workflow=Logout\">Logout " + caller.getUsr() + "</a>");
                out.println("<a href=\"Controller?workflow=ViewMyAccount\">Account</a>");
            }
            else {
                out.println("<a href=\"Controller?workflow=Login\">Login</a>");
            }
            out.println("<form method=\"post\" action=\"Controller?workflow=SearchKeywordFast\">");
            out.println("<span>Search</span>");
            out.println("<input type=\"text\" name=\"fast_search_key\" />");
            out.println("</form>");
            out.println("</div>");
            out.println("</div>");
            out.println("<div id=\"under_logo\">");
            out.println("<div id=\"banner\">");
            out.println("<img id=\"banner_image\" src=\"images/under_logo.jpg\" alt=\"CreZOO\"/>");
            out.println("</div>");
            out.println("</div>");
            
            printMenu(out, caller);
            
        }
        catch (Exception e) {
            logger.error("Menu generation failed, general exception. Swallow!", e);
            out.println("Failed to write menu. "+e.getMessage());
        }
        
//        out.println("</body>");
//        out.println("</html>");
         
//        out.close();
    }
    
    private void printMenuItems(NodeList menuItems, PrintWriter out, Caller caller) {
        if (menuItems.getLength()>0) out.println("<ul>");
        
        for (int j=0;j<menuItems.getLength();j++) {
            
                org.w3c.dom.Node node = menuItems.item(j);
                if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    if (hasPrivilege(node, caller)) {
//                        Element e2 = (Element)menuItems.item(j);
                        out.println("<li>");
                        printMenuItem(node, out, caller);
//                        NodeList menuItems2 = node.getChildNodes();//e2.getElementsByTagName("menu-item");
                        printMenuItems(node.getChildNodes(), out, caller);
                        out.println("</li>");
                    }
                }
        }
        if (menuItems.getLength()>0) out.println("</ul>");
    }
    
    private boolean hasPrivilege(Node node, Caller caller) {
        NamedNodeMap attrs = node.getAttributes();
        
        Node tmpNode = null;
        
        String priv = null;
        tmpNode = attrs.getNamedItem("priv");
        if (tmpNode!=null)
            priv = tmpNode.getNodeValue();
            
        if (priv==null || priv.equals("")  || caller.hasPrivilege(priv))
            return true;
        return false;
    }
    
    private void printMenuItem(org.w3c.dom.Node node, PrintWriter out, Caller caller) {
        NamedNodeMap attrs = node.getAttributes();
        
        if (attrs!=null) {
            org.w3c.dom.Node tmpNode = null;
            
            String name="", altName="";
            
            tmpNode = attrs.getNamedItem("name");
            if (tmpNode!=null) name = tmpNode.getNodeValue();
            
            tmpNode = attrs.getNamedItem("alt-name");
            if (tmpNode!=null) altName = tmpNode.getNodeValue();

            String priv = null;
            tmpNode = attrs.getNamedItem("priv");
            if (tmpNode!=null) priv = tmpNode.getNodeValue();
            
            if (priv==null || priv.equals("")  || caller.hasPrivilege(priv)) {
            
                String url = "#";

                tmpNode = attrs.getNamedItem("workflow");
                
                if (tmpNode!=null) url = "Controller?workflow="+tmpNode.getNodeValue();

                tmpNode = attrs.getNamedItem("url");
                if (tmpNode!=null) url = tmpNode.getNodeValue();

                if (!url.equals("#")) {
                    out.println("<a target=\"" + target + "\" href=\"" + url + "\">" + name + "</a>");
                } 
                else {
                    out.println("<span>" + name + "</span>");
                }
            }
        }
    }
    
    private void printMenu(PrintWriter out, Caller caller) {
        try {
            out.println("<div id=\"navigation\">");
//            out.println("<div id=\"panel-left\">");
            //out.println("<ul id=\"navlist\">");
            //out.println("<ul id=\"red\" class=\"treeview-red\">");
            out.println("<ul class=\"sf-menu\" >");

            // Get root node menu
            NodeList menus = doc.getElementsByTagName("menus");
            Element menusElement = (Element)menus.item(0);

            target = menusElement.getAttribute("target");


            NodeList nl = menusElement.getChildNodes();
            //NodeList nl = doc.getElementsByTagName("menu");
            
            for (int i=0;i<nl.getLength();i++) {
                Node node = nl.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element e = (Element)nl.item(i);

                    String name = e.getAttribute("name");
                    String workflow = e.getAttribute("workflow");
                    String priv = e.getAttribute("priv");
                    String admin = e.getAttribute("admin");

                    if (caller==null) throw new ArxFrameException("Caller is null");
                    
                    if ((admin==null || admin.equals("") || caller.isAdmin()) && (priv==null || priv.equals("") || caller.hasPrivilege(priv))) {
                        
                        out.println("<li>");
                        if (workflow!=null && !workflow.equals("")) {
                            out.println("<a target=\""+target+"\" href=\"Controller?workflow="+workflow+"\">"+name+"</a>");
                        }
                        else {
                            out.println("<span>"+name+"</span>");
                        }
                        
                        printMenuItems(e.getChildNodes(), out, caller);
                        
                        out.println("</li>");
                    }
                }
            }
            out.println("</ul>");
            out.println("</div>");
        }
        catch (ArxFrameException e) {
            logger.error("Menu error", e);
            out.println("Menu error: "+e.getMessage());
        }
    }
    
    private void parseXML() throws Exception {
        Timer t = new Timer();
        try {
            ServletContext context = this.getServletContext();
            InputStream is = context.getResourceAsStream(MENU_FILE);
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            
        }
        catch (Exception e) {
            logger.error("Failed to parse menu.xml file ("+MENU_FILE+")", e);
            throw new Exception("Cannot display menu", e);
        }
        t.stop();
        logger.debug("Menu#parseXML "+t);
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    public String getServletInfo() {
        return "Menu Advanced";
    }
    // </editor-fold>
}
