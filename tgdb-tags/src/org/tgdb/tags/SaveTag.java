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
public class SaveTag extends BodyTagSupport {
    private String cancelName, saveName, onClick;
    
    /** Creates a new instance of BlockHideTag */
    public SaveTag() throws JspException {
        saveName = "save";
        cancelName = "back";
        onClick = "";
    }
    
    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }
    
    public void setCancelName(String cancelName) {
        this.cancelName = cancelName;
    }   
    
    public void setOnClick(String onClick) {
        this.onClick = onClick;
    }

    public int doStartTag() throws JspException {        
        try {            
            pageContext.getOut().print("<input type=\"image\" src=\"images/icons/disk_blue_24.png\" onClick=\""+onClick+"\" value=\""+saveName+"\" title=\"Save\" name=\""+saveName+"\" alt=\""+saveName+"\" >&nbsp;");
            pageContext.getOut().print("<input type=\"image\" src=\"images/icons/cancel_24.png\" title=\"Cancel\" value=\""+cancelName+"\" name=\""+cancelName+"\">");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return SKIP_BODY;
    }       
}
