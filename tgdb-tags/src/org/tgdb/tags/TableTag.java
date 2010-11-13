/*
 * TableTag.java
 *
 * Created on July 5, 2005, 5:57 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.tags;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.util.Collection;

/**
 * Abstract class for table tags.
 * @author lami
 */
public abstract class TableTag extends BodyTagSupport {
    /**
     * The data access object
     */
    protected Collection dto;        
    /**
     * The css class for the table
     */
    protected String css;
    
    private int alt;
    
    // Keeps track of when we have displayed as many rows as the dao is large.
    // Thus, this variable can be used to reset the alt counter so that we 
    // dont have problems with differet row color on first row when reloading page
    private int ctr;
    
    /**
     * Parameter for wether or not rows should have alternating colors.
     */
    protected String alternating;
    
    /**
     * TableTag constructor
     */
    public TableTag() {
        super();
        alt = 0;
        ctr = 0;
    }
    
    /**
     * Sets wether or not alternating row colors should be done. Accepted values is 'yes' for alternating rows and 'no' for non-alternating rows.
     * @param alternating A string for wether or not rows should have alternating colors (values are 'yes' and 'no')
     */
    public void setAlternating(String alternating){
        this.alternating = alternating;
    }
    
    /**
     * Sets the collection of data access objects
     * @param dto The collection to display
     */
    public void setCollection(Collection dto){
        this.dto = dto;
    }
    
    public void setCollection(String dto){
        this.dto = (Collection)pageContext.getRequest().getAttribute(dto);   
    }    
    
    /**
     * Sets the css class for this table tag
     * @param css The css class name for the table tag
     */
    public void setCss(String css) {
        this.css = css;
    }   
    
    // Responisble for displaying a row, used mainly for alternating row colors
    /**
     * Returns the table row to display. Used for alternating row colors
     * @return The HTML code for the table row
     */
    protected String getTableRow(){        
        // We have reloaded page and thus resets the color variable
        if(ctr == dto.size()){
            alt = 0;
            ctr = 0;
        }
        ctr++;
            
        String data = "";
        if(alternating == "yes"){
            if(alt == 0){
                data += "<tr class=\""+css+" alternatingOne\">";
                alt = 1;
            }
            else{
                data += "<tr class=\""+css+" alternatingTwo\">";
                alt = 0;
            }
        }
        else
            data += "<tr class=\""+css+"\">";   
        
        return data;
    }
}
