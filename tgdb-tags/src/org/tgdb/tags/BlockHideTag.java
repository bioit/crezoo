/*
 * BlockHideTag.java
 *
 * Created on December 5, 2005, 1:56 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.tags;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;


/**
 *
 * @author lami
 */
public class BlockHideTag extends BodyTagSupport {
    private String name, state, startState;
    
    /** Creates a new instance of BlockHideTag */
    public BlockHideTag() {
        name = "";
        startState = "collapsed";
    }
    
    public void setState(String state) {
        this.state = state;
        startState = state;
    }   
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int doEndTag() throws JspException {        
        try {
            String bodyText = bodyContent.getString();
            HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
            HttpSession se = req.getSession();
            
            String performAction = (String)req.getParameter(name);
            /*
            if (performAction == null){
                performAction = (String)req.getAttribute(name);
            }
             */
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
               

            if(state != null && state.equalsIgnoreCase("expanded")) {                           
                pageContext.getOut().print(bodyText);
            }            

            // Choosen view
//            if(state.equalsIgnoreCase("collapse")) {                           
//                pageContext.getOut().print(bodyText);
//            }
            
        } catch (IOException ioe) {}

        return EVAL_PAGE;
    }       
}
