/*
 * QueryTag.java
 *
 * Created on November 4, 2005, 12:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 *
 * @author lami
 */
public class QueryTag extends BodyTagSupport {
    private String type;
    
    /** Creates a new instance of QueryTag */
    public QueryTag() {
    }
    
    public void setType(String type) {
        this.type = type;
    }    
    
    /**
     * Builds the HTML code for the tag
     * @throws javax.servlet.jsp.JspException If the HTML code could not be created
     * @return The BodyTagSupport.SKIP_BODY integer value if everything went fine
     */
    public int doEndTag() throws JspException {
        String performType = (String)pageContext.getRequest().getAttribute("querytype");
        try {
            String bodyText = bodyContent.getString();
            // Choosen view
            if(type.equalsIgnoreCase(performType))                            
                pageContext.getOut().print(bodyText);
            // Default view
            else if(type.equalsIgnoreCase("simple") && performType == null)
                pageContext.getOut().print(bodyText);
        } catch (IOException ioe) {}

        return EVAL_PAGE;
    }    
}
