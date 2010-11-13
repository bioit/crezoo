/*
 * CheckboxTag.java
 *
 * Created on July 14, 2005, 10:23 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.tags;
import org.tgdb.TgDbCaller;
import java.util.Collection;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Custom tag class for hiding data then a collection is null or empty
 * @author heto
 */
public class HideCollectionTag extends BodyTagSupport {
    
    
    private String collectionName;
    private int limit;
    
    
    /** Creates a new instance of SUComboBoxTag */
    public HideCollectionTag() {
    }
    
    public void setCollection(String collectionName)
    {
        this.collectionName = collectionName;
    }
    
    public void setLimit(String limit)
    {
        this.limit = new Integer(limit).intValue();
    }
    
   
    /**
     * Builds the combobox
     * @throws javax.servlet.jsp.JspException If the combobox could not be created
     * @return The BodyTagSupport.SKIP_BODY integer value when done
     */
    public int doEndTag() throws JspException {
        TgDbCaller caller = (TgDbCaller)pageContext.getSession().getAttribute("caller");
        String data = "";
        Collection collection = (java.util.Collection)pageContext.getRequest().getAttribute(collectionName);
        try {
            if (collection != null && collection.size()>limit)
            {    
                String bodyText = bodyContent.getString();
                pageContext.getOut().print(bodyText);
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            //throw new JspTagException(e.getMessage());
        }
        return EVAL_PAGE;
    }
}