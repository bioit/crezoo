/*
 * SexTag.java
 *
 * Created on July 13, 2005, 7:47 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.tags;

import org.tgdb.frame.advanced.Workflow;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Custom tag for getting a parameter from the workflow object
 * @author heto
 */
public class ParameterTag extends BodyTagSupport {
    
    private String name;
    
    
    /** Creates a new instance of ParameterTag */
    public ParameterTag() {
        name = "";
    }
    
    
    
    /**
     * Set the parameter name to get
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
  
    
    /**
     * Builds the HTML code for the tag
     * @throws javax.servlet.jsp.JspException If the HTML code could not be created
     * @return The BodyTagSupport.SKIP_BODY integer value if everything went fine
     */
    public int doStartTag() throws JspException 
    {
        try
        {
            Workflow workflow = (Workflow)pageContext.getRequest().getAttribute("workflow");
            
            String value = workflow.getParameter(name);
            if (value == null)
                value = "";
            // Print the table on the page
            pageContext.getOut().print(value);
            
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new JspTagException(e.getMessage());
        }
        
        return SKIP_BODY;
    }
}
