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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 *
 * @author lami
 */
public class BackTag extends BodyTagSupport {
    
    /** Creates a new instance of BlockHideTag */
    public BackTag() throws JspException {
       
    }

    public int doStartTag() throws JspException {        
        try {            
            pageContext.getOut().print("<input type=\"image\" src=\"images/icons/nav_left_blue_24.png\" title=\"Back\" name=\"back\" value=\"back\">");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return SKIP_BODY;
    }       
}
