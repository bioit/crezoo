/*
 * StatusTag.java
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
 * Custom tag for creating an enabled/disabled option combobox
 * @author heto
 */
public class StatusTag extends BodyTagSupport {
    
    private String status;
    private String onChange;
    
    /** Creates a new instance of StatusTag */
    public StatusTag() {
    }
    
    /**
     * Sets the action to perform on change
     * @param onChange The action to perform (javascript)
     */    
    public void setOnChange(String onChange){
        this.onChange = onChange;
    }    
    
    /**
     * Sets the selected value for this combobox.
     * @param status The status value ("E" or "D")
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
            if(onChange != null)
                data += "<select name=\"status\" onChange=\""+onChange+"\">";
            else
                data += "<select name=\"status\">";
            
            if (status.equals("E"))
                data += "<option selected=\"selected\" value=\"E\">Enabled</option>";
            else
                data += "<option selected=\"selected\" value=\"E\">Enabled</option>";
            
            if (status.equals("D"))
                data += "<option selected=\"selected\" value=\"D\">Disabled</option>";
            else
                data += "<option value=\"D\">Disabled</option>";
                
            
            
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
