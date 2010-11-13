/*
 * TypeTag.java
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
 * Custom tag for creating an E/N option combobox
 * @author lami
 */
public class TypeTag extends BodyTagSupport {
    private String status;
    private String name;
    private boolean wildcard;    
    
    /** Creates a new instance of TypeTag */
    public TypeTag() {
        name = "type";
    }
    
    public void setWildcard(boolean wildcard) {
        this.wildcard = wildcard;
    }    
    
    public void setName(String name) {
        this.name = name;
    }    
    
    /**
     * Sets the selected value for this combobox.
     * @param status The status value ("E", N")
     */
    public void setValue(String status)
    {
        this.status = status;
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
            
            data += "<select name=\""+name+"\">";
            
            if (status.equals("E"))
                data += "<option selected=\"selected\" value=\"E\">E</option>";
            else
                data += "<option >E</option>";
            
            if (status.equals("N"))
                data += "<option selected=\"selected\" value=\"N\">N</option>";
            else
                data += "<option value=\"N\">N</option>";            
                
            
            if(wildcard && status.equals("*"))
                data += "<option selected=\"selected\" value=\"*\">*</option>";
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
