/*
 * TableIndividualTag.java
 *
 * Created on July 7, 2005, 11:08 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.tags;
import java.lang.reflect.Method;
import java.util.Collection;
import javax.servlet.jsp.*;
import java.util.Iterator;


/**
 * Custom tag to iterate a collection and replace keywords inside the 
 * body of the tag to values from the objects in the collection.
 *
 * The keywords are #:methodName# where the methodName is a method in the
 * object inside the array. The keywords get substituted to the values that
 * the method returns. It is also possible to use multiple methods as keyword
 * and separate the names by using '.' (ex. #:getPerson.getName#).
 *
 * Use #?alt# for alternating row color when iterating a table.
 *
 * Example: 
 *
 *     <table>
       <tr>
            <th>Role</th><th>Comment</th><th>Edit</th>
       </tr>
       <m:iterate-collection collection="<%=roles%>">
       <tr class="#?alt#">
            <td>#:getName#</td>
            <td>#:getComm#</td>
            <td><a href="Controller?workflow=EditRole&rid=#:getRid#">Edit</a></td>
       </tr>
       </m:iterate-collection>
       </table>
 *
 *
 * @author heto
 */
public class CollectionIteratorTag extends TableTag
{
    
    /** Creates a new instance of TableIndividualTag */
    public CollectionIteratorTag() {
        super();
    }
    
    /**
     * Creates the iterative display code for the collection
     * @throws javax.servlet.jsp.JspException If the iterative display could not be created
     * @return The BodyTagSupport.EVAL_PAGE integer value if everything went OK
     */
    public int doEndTag() throws JspException
    {
    
        try
        {
            String data = "";            
                
            Iterator i = dto.iterator();

            /* Get body */
            String str = bodyContent.getString();
            String[] tokenized = str.split("#");
            
            boolean isColored = false;
            
            // For each of the objects in dto
            while (i.hasNext()) 
            {
                Object obj = i.next();
                    
                for (int j=0;j<tokenized.length;j++)
                {
                    if (tokenized[j].charAt(0) == ':')
                    {
                        String tmp = tokenized[j].substring(1);
                        // Get all method names
                        String[] names = tmp.split("\\.");                                              
                        Method method = null;
                        
                        Object out = obj;
                        
                        // For each of the method names
                        for(int k=0;k<names.length;k++)
                        {
                            // If the class exists
                            if(out != null)
                            {
                                // Get the method in the class...
                                method = out.getClass().getMethod(names[k], null); 
                                
                                // ...and invoke the method in the class
                                out = method.invoke(out, null);
                            }
                        }                        
                        
                        if (out != null)
                            data += out;
                        else
                            data += "&nbsp;";
                         
                    }   
                    // Special cases...
                    else if(tokenized[j].charAt(0) == '?')
                    {
                        String name = tokenized[j].substring(1);
                        // Keyword for using alternating row color
                        if(name.equals("alt"))
                        {
                            // If white background we should color the
                            // next row differently
                            if(!isColored)
                            {
                                data += "alternatingOne";
                                isColored = true;
                            }
                            // If colored row, don't color the next row
                            else
                            {
                                data += "alternatingTwo";
                                isColored = false;                            
                            }
                        }
                    }
                    else
                    {
                        data += tokenized[j];
                    }
                }                
            }
            
            // Print the table on the page
            pageContext.getOut().print(data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return EVAL_PAGE;
    }
     
}
