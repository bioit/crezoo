/*
 * IteratorTag.java
 *
 * Created on July 6, 2005, 3:12 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.tags;

/**
 *
 * @author lami
 */
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.util.*;


public class IteratorTag extends TagSupport {
    private Iterator iterator;
    private String type;
    
    public IteratorTag() {
        super();
    }
    
    public void setCollection(Collection collection) {
        if(collection.size()>0)
            iterator = collection.iterator();
    }
    
    public void setType(String type) {
        this.type=type;
    }
    
    public int doStartTag() throws JspException {
        if (iterator != null) {
            if (hasNext())
                return EVAL_BODY_INCLUDE;
        }
        return SKIP_BODY;
    }
    
    public int doAfterBody() throws JspException {
        if (hasNext())
            return EVAL_BODY_AGAIN;
        else
            return SKIP_BODY;
    }
    
    private boolean hasNext() {
        if (iterator.hasNext()) {
            this.pageContext.setAttribute(id, iterator.next(), PageContext.PAGE_SCOPE);
            return true;
        }
        return false;
    }
}
