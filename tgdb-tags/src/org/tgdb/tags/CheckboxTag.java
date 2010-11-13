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
public class CheckboxTag extends BodyTagSupport {
    
    //<m:checkbox collection='' name='father' id='iid' value='name'/>
    
    private String name;
    private Collection collection;
    private String collectionName;
    private String idName;
    private String textName;
    private String onChange;
    private boolean wildcardOption;
    private String selected;
    
    private boolean emptyOption;
    
    /** Creates a new instance of CheckboxTag */
    public CheckboxTag() 
    {
        emptyOption=false;
    }
    
    /**
     * Sets the name of the HTML select tag
     * @param name The name
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Sets the collection to iterate
     * @param col The collection to iterate
     */
    /*
    public void setCollection(Collection col)
    {
        collection = col;
    }
     */
    
    /* Set the collection name */
    public void setCollection(String col)
    {
        collection = (java.util.Collection)pageContext.getRequest().getAttribute(col);
        collectionName = col;
    }    
    
    /**
     * Sets the action to perform on change
     * @param onChange The action to perform (javascript)
     */    
    public void setOnChange(String onChange){
        this.onChange = onChange;
    }
    
    /**
     * Sets the id for the HTML options
     * @param idName The option id name
     */
    public void setIdGetter(String idName)
    {
        this.idName=idName;
    }
    
    
    /**
     * Sets the text value for the HTML option object
     * @param textName The text name
     */
    public void setTextGetter(String textName)
    {
        this.textName = textName;
    }
    
    /**
     * Sets if the combox should start with an empty option
     * @param empty The selector for this option. Accepted values are "yes" or "true" if first row should be empty
     */
    public void setEmptyOption(String empty)
    {
        if (empty.equalsIgnoreCase("yes") || empty.equalsIgnoreCase("true") || 
                empty.equalsIgnoreCase("1"))
            emptyOption = true;
        else
            emptyOption = false;
    }
    
    public void setWildcardOption(String wildCard)
    {
        if (wildCard.equalsIgnoreCase("yes") || wildCard.equalsIgnoreCase("true") || 
                wildCard.equalsIgnoreCase("1"))
            wildcardOption = true;
        else
            wildcardOption = false;
    }    
    
    /**
     * Sets the selected option for this combobox
     * @param selected The identifier for the selected option
     */
    public void setSelected(String selected)
    {
        this.selected = selected;
    }
    
    /**
     * Sets the selected option for the combobox
     * @param selected The selected option id
     */
    public void setSelected(int selected)
    {
        this.selected = ""+selected;
    }
    
    /**
     * Builds the combobox
     * @throws javax.servlet.jsp.JspException If the combobox could not be created
     * @return The BodyTagSupport.SKIP_BODY integer value when done
     */
    public int doStartTag() throws JspException 
    {
        String data = "";
        try
        {
            if (collection == null && collectionName==null)    
                throw new Exception("Collection is null");
            else if (collection==null)
                throw new Exception("Collection "+collectionName+" is null");
            
            if(onChange != null)
                data += "<select name=\""+name+"\" onchange=\""+onChange+"\">";
            else
                data += "<select name=\""+name+"\">";
            
            if (emptyOption)
                data += "<option value=\"\"></option>";
            
            if (wildcardOption)
                data += "<option value=\"*\">*</option>";            
            
            
            Iterator i = collection.iterator();
            while (i.hasNext())
            {
                // Get the next object
                Object o = i.next();
                
                /** Get the id value from the object */
                Method getId = o.getClass().getMethod(idName, null);
                Object id = getId.invoke(o, null);
                
                /** Get the text value from the object */
                Method getValue = o.getClass().getMethod(textName, null);
                Object txt = getValue.invoke(o, null);
                
                String tmp = "" + id;
                if (tmp.equals(selected))
                    data += "<option selected=\"selected\" value=\""+id+"\">"+txt+"</option>";
                else
                    data += "<option value=\""+id+"\">"+txt+"</option>";
            }
            data += "</select>";
            
            // Print the table on the page
            pageContext.getOut().print(data);
            
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new JspTagException(e.getMessage());
        }
        
        return SKIP_BODY;
    }
}
