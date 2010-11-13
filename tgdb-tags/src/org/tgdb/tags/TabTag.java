package org.tgdb.tags;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class TabTag extends BodyTagSupport {
    
    private String title, name, workflow;
    private ArrayList menu;
    
    private String state, startState;
    
    public TabTag() {}
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setWorkflow(String workflow) {
        this.workflow = workflow;
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
            
            String bodyString = getBody(bodyText);
            
            String currenTab = (String)req.getParameter("tab");
            
            if(currenTab==null){
                currenTab = (String) se.getAttribute("currenTab");
            }
            
            if(currenTab != null && currenTab.equalsIgnoreCase(name)){
                out.println(bodyString);
            } else if (name.equalsIgnoreCase("general") && currenTab == null){
                out.println(bodyString);
                currenTab = "general";
            }
            
            se.setAttribute("currenTab", currenTab);
                   
            out.flush();   
        } catch (IOException ioe) {}

        return EVAL_PAGE;
    }       
}
