package org.tgdb.tags;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class WindowTag extends BodyTagSupport {
    
    private String title, name, workflow;
    private ArrayList menu;
    
    private String state, startState;
    
    public WindowTag() {
        menu = new ArrayList();
        startState = "collapsed";
    }
    
    public void setState(String state) {
        this.state = state;
        startState = state;
    }
    
    public void setMenu(String menu) {
        this.menu.add(menu);
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setName(String name) {
        this.name = name+"_display";
    }
    
    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }
    
    private String getMenu(String text) {
        int p1 = text.indexOf("<menu>");
        int p2 = text.indexOf("</menu>");
        return text.substring(p1+6, p2);
    }
    
    private String getBody(String text) {
        int p1 = text.indexOf("<body>");
        int p2 = text.indexOf("</body>");
        return text.substring(p1+6, p2);
    }
    
    public int doEndTag() throws JspException {        
        try {
            String bodyText = bodyContent.getString();
            HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
            HttpSession se = req.getSession();
            
            JspWriter out = pageContext.getOut();
            
            String menuString = getMenu(bodyText);
            String bodyString = getBody(bodyText);
            
            String performAction = (String)req.getParameter(name);
            state = (String)se.getAttribute(name);
            if(state == null)
                state = startState;            
            if(performAction != null) {

                if(state.equalsIgnoreCase("expanded"))
                    se.setAttribute(name, "collapsed");
                else
                    se.setAttribute(name, "expanded");
                state = (String)se.getAttribute(name);    
            }
            String nameBegins = req.getParameter("name_begins");
            if (nameBegins==null)
                nameBegins = "";
            
            if (req.getParameter("expand_all")!=null &&
                    req.getParameter("expand_all").equals("true") &&
                    name.startsWith(nameBegins))
            {
                state = "expanded";
                se.setAttribute(name, "expanded");
            }
            else if (req.getParameter("collapse_all")!=null && 
                    req.getParameter("collapse_all").equals("true") &&
                    name.startsWith(nameBegins))
            {
                state = "collapsed";
                se.setAttribute(name, "collapsed");
            }    
            
            
            //if (state != null && state.equalsIgnoreCase("expanded")) 
            //{
            
            out.println("<table class=\"block3\" cellspacing=\"0\">");
            out.println("   <tr>");
            out.println("   <th class=\"block\">");
            //out.println("       <a href=\"Controller?workflow="+workflow+"&"+name+"=true\" title=\"Expand/Collapse this section\">"+title+"</a>");
            out.println(""+title+"");
            if (!menuString.trim().equals("")) {
                out.println(menuString);
            }
            out.println("   </th>");
            out.println("   </tr>");
                   
            out.println("   <tr>");
            out.println("   <td>");    
            
            
            if (state != null && state.equalsIgnoreCase("expanded")) 
            {
                //out.println("       <m:hide-block name=\""+name+"_display\">");   

                out.println("           <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
                out.println("           <tr>");
                //out.println("           <th valign=top>"); //  class=\"menu\" 
                /*
                if (!menuString.trim().equals(""))
                {    
                    out.println("               <table cellpadding=0 cellspacing=0 border=0>");
                    out.println("               <tr><th><img src='images/menu_gradient_top_20.png'></th></tr>");
                    out.println("               <tr><th class=\"menu\">");
                    out.println(menuString);
                    out.println("               </th></tr>");
                    out.println("               <tr><th><img src='images/menu_gradient_bottom_20.png'></th></tr>");
                    out.println("               </table>");
                }
                 */
                //out.println("           </th>");

                out.println("           <td width=\"100%\">");
                out.println(bodyString);
                out.println("           </td>");
                out.println("           </tr>");    
                out.println("           </table>");

                //out.println("       </m:hide-block>");         
            }
            
            out.println("   </td>");
            out.println("   </tr>"); 
            out.println("</table>");             
                   
            out.flush();   
        } catch (IOException ioe) {}

        return EVAL_PAGE;
    }       
}
