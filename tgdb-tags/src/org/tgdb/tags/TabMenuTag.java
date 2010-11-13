package org.tgdb.tags;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class TabMenuTag extends BodyTagSupport {
    
    private String title, name, workflow;
    private String[] names, titles;
    
    private String currenTab;
    
    public TabMenuTag() {}
    
    public void setTitle(String title) {
        this.title = title;
        this.titles = title.split(":");
    }
    
    public void setName(String name) {
        this.name = name;
        this.names = name.split(":");
    }
    
    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }
    
    
    public int doEndTag() throws JspException {        
        try {
            HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
            HttpSession se = req.getSession();
            
            currenTab = (String)req.getParameter("tab");
            
            if(currenTab==null){
                currenTab = (String) se.getAttribute("currenTab");
                if(currenTab==null){
                    currenTab = "general";
                }
            }
            
            JspWriter out = pageContext.getOut();
            
            out.println("<table class=\"blockmenu\" cellspacing=\"0\" cellpadding=\"0\">");
            out.println("<tr>");
            for(int i=0; i<names.length; i++){
                if(names[i].equalsIgnoreCase(currenTab)){
                    out.println("<td class=\"blockmenucellhit\"><span>"+titles[i]+"</span></td>");
                } else {
                    out.println("<td class=\"blockmenucell\">");
                    out.println("<a href=\"Controller?workflow="+workflow+"&amp;tab="+names[i]+"\" title=\"open "+titles[i]+" tab\" class=\"blockmenulink\">"+titles[i]+"</a>");
                    out.println("</td>");
                }
                
            }
            out.println("</tr>"); 
            out.println("</table>");                   
            out.flush();
            
        } catch (IOException ioe) {}

        return EVAL_PAGE;
    }       
}
