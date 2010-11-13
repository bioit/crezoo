/*
 * Menu.java
 *
 * Created on January 9, 2006, 9:56 AM
 */

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

/**
 *
 * @author heto
 * @version
 */
public class Menu extends HttpServlet {
    
    private static Logger logger = Logger.getLogger(Menu.class);
    private String target;
    
    
    private Document doc;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException 
     * @throws java.io.IOException 
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet Menu</title>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"nav.css\" />");
        out.println("</head>");
        out.println("<body>");
        
        try
        {
            /* Read the xml data */
            parseXML();
            
            /* Write logo on top of menu */
            //out.println("<table height=\"100%\"><tr align=\"left\"><td id=\"logo\"></td></tr><tr valign=top><td>");
            
            Caller caller = (Caller)request.getSession().getAttribute("caller");
            if (caller==null)
            {
                response.sendRedirect("Controller");
                return;
            }
                //throw new PermissionDeniedException("Caller is null");
            
            String current = request.getParameter("current");
            if (current!=null && !current.equals(""))
            {
                String expand = caller.getAttribute("menu."+current);
                if (expand!=null && expand.equals("1"))
                    caller.setAttribute("menu."+current, "0");
                else
                    caller.setAttribute("menu."+current, "1");
            }
            
            
            printMenu(out, caller);
            
            out.println("</td></tr></table>");
            
        }
        catch (Exception e)
        {
            logger.error("Menu generation failed, general exception. Swallow!", e);
            out.println("Failed to write menu. "+e.getMessage());
        }
        
        out.println("</body>");
        out.println("</html>");
         
        out.close();
    }
    
    /**
     * Print a nodelist of menus.
     * @param menuItems the nodelist of menu items.
     * @param out is the printwriter to output data to.
     * @param caller is the user making the current call.
     */
    private void printMenuItems(NodeList menuItems, PrintWriter out, Caller caller)
    {
        if (menuItems.getLength()>0)
            out.println("<ul>");
        
        for (int j=0;j<menuItems.getLength();j++)
        {
            
                org.w3c.dom.Node node = menuItems.item(j);
                
                //out.println("nodeType="+node.getNodeType());
                
                if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
                {
                    if (hasPrivilege(node, caller))
                    {
                        //Element e2 = (Element)menuItems.item(j);
                        printMenuItem(node, out, caller);


                        NodeList menuItems2 = node.getChildNodes();//e2.getElementsByTagName("menu-item");
                        printMenuItems(menuItems2, out, caller);
                    }
                    //out.println("</menu-item>");
                }


        }
        if (menuItems.getLength()>0)
            out.println("</ul>");
    }
    
    /**
     * Checks if the user has the right privilege to perform this action.
     * @param node is the object to check for privileges
     * @param caller is the user making the call.
     * @return true if user is granted this menuitem, false otherwise.
     */
    private boolean hasPrivilege(Node node, Caller caller)
    {
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
    
    /**
     * Print a particular menu item 
     * @param node is the part of the xml object to print
     * @param out is the PrintWriter to write the output to.
     * @param caller is the user making this call.
     */
    private void printMenuItem(org.w3c.dom.Node node, PrintWriter out, Caller caller)
    {
        NamedNodeMap attrs = node.getAttributes();
        
        if (attrs!=null)
        {
            org.w3c.dom.Node tmpNode = null;
            
            String name="", altName="";
            
            tmpNode = attrs.getNamedItem("name");
            if (tmpNode!=null) 
                name = tmpNode.getNodeValue();
            

            tmpNode = attrs.getNamedItem("alt-name");
            if (tmpNode!=null)
                altName = tmpNode.getNodeValue();

            String priv = null;
            tmpNode = attrs.getNamedItem("priv");
            if (tmpNode!=null)
                priv = tmpNode.getNodeValue();
            
            String image = null;
            tmpNode = attrs.getNamedItem("image");
            if (tmpNode!=null)
                image = tmpNode.getNodeValue();
            
            String size = null;
            tmpNode = attrs.getNamedItem("size");
            if (tmpNode!=null)
                size = tmpNode.getNodeValue();
            
            if (priv==null || priv.equals("")  || caller.hasPrivilege(priv))
            {
            
                String url;
                String localTarget = null;

                tmpNode = attrs.getNamedItem("workflow");
                if (tmpNode!=null)
                    url = "Controller?workflow="+tmpNode.getNodeValue();
                else
                {
                    url = "Menu";
                    localTarget = "";
                }

                tmpNode = attrs.getNamedItem("url");
                if (tmpNode!=null)
                    url = tmpNode.getNodeValue();

                if (image!=null && !image.equals(""))
                {
                    String tmpSize = "";
                    if (size!=null && !size.equals(""))
                        tmpSize = "width=\""+size+"\"";
                        
                    if (localTarget==null)
                        out.println("<li><a target=\""+target+"\" href=\""+url+"\"><img src=\""+image+"\" "+tmpSize+" title=\""+name+"\"><br>"+name+"</a></li>");
                    else
                        out.println("<li><a target=\""+localTarget+"\" href=\""+url+"\"><img src=\""+image+"\" "+tmpSize+" title=\""+name+"\"><br>"+name+"</a></li>");
                }    
                else
                {    
                    if (localTarget==null)
                        out.println("<li><a target=\""+target+"\" href=\""+url+"\">"+name+"</a></li>");
                    else
                        out.println("<li><a target=\""+localTarget+"\" href=\""+url+"\">"+name+"</a></li>");
                }
            }
        }
    }
    
    
    
    /**
     * Print the menu from the doc object.
     * @param out the PrintWriter object from the servlet.
     * @param caller is the user making the call. This is used for privilege control in the menu.
     */
    private void printMenu(PrintWriter out, Caller caller)
    {
        try
        {
            out.println("<div id=\"navcontainer\">");
            out.println("<ul id=\"navlist\">");

            // Get root node menu
            NodeList menus = doc.getElementsByTagName("menus");
            Element menusElement = (Element)menus.item(0);

            target = menusElement.getAttribute("target");


            NodeList nl = menusElement.getChildNodes();
            //NodeList nl = doc.getElementsByTagName("menu");
            for (int i=0;i<nl.getLength();i++)
            {
                Node node = nl.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {

                    Element e = (Element)nl.item(i);

                    String name = e.getAttribute("name");
                    String workflow = e.getAttribute("workflow");
                    String priv = e.getAttribute("priv");
                    String admin = e.getAttribute("admin");

                    if (caller==null)
                        throw new ArxFrameException("Caller is null");
                    
                    if ((admin==null || admin.equals("") || caller.isAdmin())
                        && (priv==null || priv.equals("") || caller.hasPrivilege(priv)))
                    {
                        if (workflow!=null && !workflow.equals(""))
                            out.println("<li id=\"active\"><a target=\""+target+"\" href=\"Controller?workflow="+workflow+"\">"+name+"</a>");
                        else
                            out.println("<li id=\"active\"><a href=\"Menu?current="+name+"\">"+name+"</a>");
                        //out.println("<ul id=\"subnavlist\">");

                        // Show expanded menu
                        String expand = caller.getAttribute("menu."+name);
                        if (expand!=null && expand.equals("1"))
                        {
                            NodeList menuItems = e.getChildNodes();

                            //NodeList menuItems = e.getElementsByTagName("menu-item");
                            printMenuItems(menuItems, out, caller);
                        }
                    }
                }
            }
            out.println("</ul>");
            out.println("</div>");
        }
        catch (ArxFrameException e)
        {
            logger.error("Menu error", e);
            out.println("Menu error: "+e.getMessage());
        }
    }
    
    /**
     * Parse the xml resource and read the data into the doc object
     * @throws java.lang.Exception if reading fails.
     */
    private void parseXML() throws Exception
    {
        Timer t = new Timer();
        try
        {
            ServletContext context = this.getServletContext();
            InputStream is = context.getResourceAsStream("xml/menu.xml");
            
            
        
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            
            

        }
        catch (Exception e)
        {
            logger.error("Failed to parse menu.xml file (xml/menu.xml)", e);
            throw new Exception("Cannot display menu", e);
        }
        t.stop();
        logger.debug("Menu#parseXML "+t);
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
