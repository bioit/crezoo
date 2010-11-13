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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Custom tag for creating an female/male/unknown option combobox
 * @author heto
 */
public class SexTag extends BodyTagSupport {
    
    private String status;
    private boolean wildcard;
    
    /** Creates a new instance of SexTag */
    public SexTag() {
        wildcard = false;
    }
    
    
    
    /**
     * Sets the selected value for this combobox.
     * @param status The status value ("F", M", "*" or "U")
     */
    public void setValue(String status)
    {
        this.status = status;
    }
    
    public void setWildcard(boolean wildcard) {
        this.wildcard = wildcard;
    }
    
    /**
     * Builds the HTML code for the tag
     * @throws javax.servlet.jsp.JspException If the HTML code could not be created
     * @return The BodyTagSupport.SKIP_BODY integer value if everything went fine
     */
    public int doStartTag() throws JspException 
    {
        String data = "";
        try
        {
            data += "<select name=\"sex\">";
            
            if (status.equals("F"))
                data += "<option selected value=\"F\">Female</option>";
            else
                data += "<option selected value=\"F\">Female</option>";
            
            if (status.equals("M"))
                data += "<option selected value=\"M\">Male</option>";
            else
                data += "<option value=\"M\">Male</option>";            
            
            if (status.equals("U"))
                data += "<option selected value=\"U\">Unknown</option>";
            else
                data += "<option value=\"U\">Unknown</option>";
            
            if(wildcard && status.equals("*"))
                data += "<option selected value=\"*\">*</option>";
            else
                data += "<option value=\"*\">*</option>";                
                
            
            
            data += "</select>";
            
            // Print the table on the page
            pageContext.getOut().print(data);
            
        }
        catch (Exception e) {
            throw new JspTagException(e.getMessage());
        }
        
        return SKIP_BODY;
    }
}
