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
import org.tgdb.samplingunit.samplingunitmanager.SamplingUnitDTO;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Custom tag class for building a combobox
 * @author heto
 */
public class SUComboBoxTag extends BodyTagSupport {
    private String onChange;
    private boolean wildcardOption;
    private boolean emptyOption;
    private boolean hideEmptyOption;
    private String name;    
    
    /** Creates a new instance of SUComboBoxTag */
    public SUComboBoxTag() {
        emptyOption=false;
        name = "suid";
    }
    
    public void setHideEmpty(String hideEmpty)
    {
        if (hideEmpty.equalsIgnoreCase("yes") || hideEmpty.equalsIgnoreCase("true") ||
                hideEmpty.equalsIgnoreCase("1"))
            hideEmptyOption = true;
        else
            hideEmptyOption = false;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets the action to perform on change
     * @param onChange The action to perform (javascript)
     */
    public void setOnChange(String onChange){
        this.onChange = onChange;
    }
    
    /**
     * Sets if the combox should start with an empty option
     * @param empty The selector for this option. Accepted values are "yes" or "true" if first row should be empty
     */
    public void setEmptyOption(String empty) {
        if (empty.equalsIgnoreCase("yes") || empty.equalsIgnoreCase("true") ||
                empty.equalsIgnoreCase("1"))
            emptyOption = true;
        else
            emptyOption = false;
    }
    
    public void setWildcardOption(String wildCard) {
        if (wildCard.equalsIgnoreCase("yes") || wildCard.equalsIgnoreCase("true") ||
                wildCard.equalsIgnoreCase("1"))
            wildcardOption = true;
        else
            wildcardOption = false;
    }
    
    /**
     * Builds the combobox
     * @throws javax.servlet.jsp.JspException If the combobox could not be created
     * @return The BodyTagSupport.SKIP_BODY integer value when done
     */
    public int doStartTag() throws JspException {
        TgDbCaller caller = (TgDbCaller)pageContext.getSession().getAttribute("caller");
        String data = "";
        Collection collection = (java.util.Collection)pageContext.getRequest().getAttribute("samplingunits");
        try {
            if (collection == null)
                throw new Exception("Collection is null");
            
            if(onChange != null)
                data += "<select name=\""+name+"\" onChange=\""+onChange+"\">";
            else
                data += "<select name=\""+name+"\">";
            
            if (emptyOption)
                data += "<option value=\"\"></option>";
            
            if (wildcardOption)
                data += "<option value=\"*\">*</option>";
            
            
            Iterator i = collection.iterator();
            while (i.hasNext()) {
                // Get the next object
                Object o = i.next();
                
                /** Get the id value from the object */
                Method getId = o.getClass().getMethod("getSuid", null);
                Object id = getId.invoke(o, null);
                
                /** Get the text value from the object */
                Method getValue = o.getClass().getMethod("getName", null);
                Object txt = getValue.invoke(o, null);
                
                String tmp = "" + id;
                if (tmp.equals(""+caller.getSuid()))
                    data += "<option selected=\"selected\" value=\""+id+"\">"+txt+"</option>";
                else
                    data += "<option value=\""+id+"\">"+txt+"</option>";
            }
            data += "</select>";
            
            if (collection.size()==1 && hideEmptyOption)
            {
                Iterator it = collection.iterator();
                SamplingUnitDTO sudto = (SamplingUnitDTO)it.next();
                String tmp = "<input type=\"hidden\" name=\"suid\" value=\""+sudto.getSuid()+"\"/>";
                pageContext.getOut().print(tmp);
            }
            else
            {    
                // Print the table on the page
                pageContext.getOut().print(data);
            }    
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new JspTagException(e.getMessage());
        }
        
        return SKIP_BODY;
    }
}
