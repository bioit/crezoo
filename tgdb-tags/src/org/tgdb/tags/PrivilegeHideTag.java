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
import org.tgdb.TgDbCaller;
import java.lang.reflect.Method;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Tag for hiding/showing of HTML based on user privileges. The HTML inside the tag body is shown if the user has the required privileges.
 * @author lami
 */
public class PrivilegeHideTag extends BodyTagSupport {
    private String privilege;
    
    private int suid;
    private int pid;
    
    /**
     * Creates a new instance of PrivilegeHideTag 
     */
    public PrivilegeHideTag() {
    }
    
    /**
     * Sets the privilege that is required for showing the text inside the tag body
     * @param privilege The privilege needed for display of the HTML inside the tag body
     */
    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }    
    
    public void setSuid(int suid)
    {
        this.suid = suid;
    }
    
    public void setPid(int pid)
    {
        this.pid = pid;
    }
    
    /**
     * Prints the HTML inside the tag body given that the caller has sufficient privs
     * @throws javax.servlet.jsp.JspException If the HTML code could not be created
     * @return The BodyTagSupport.SKIP_BODY integer value if everything went fine
     */
    public int doEndTag() throws JspException {
        TgDbCaller caller = (TgDbCaller)pageContext.getSession().getAttribute("caller");
        
        try {
            String bodyText = bodyContent.getString();
            
            if (suid!=0)
            {
                if(caller.hasPrivilegeSU(privilege, suid) || caller.isAdmin())                            
                pageContext.getOut().print(bodyText);
            }
            else if (pid!=0)
            {
                if (caller.hasPrivilege(privilege, pid) || caller.isAdmin())
                pageContext.getOut().print(bodyText);
            }
            else
            {
                // Default project
                if(caller.hasPrivilege(privilege) || caller.isAdmin())                            
                    pageContext.getOut().print(bodyText);
            }
        } catch (Exception ioe) {}

        return EVAL_PAGE;
    }    
}
