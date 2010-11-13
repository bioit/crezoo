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
 * the method returns.
 *
 * Example: 
 *
 *     <table>
       <tr>
            <th>Role</th><th>Comment</th><th>Edit</th>
       </tr>
       <m:iterate-collection collection="<%=roles%>">
       <tr>
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
public class SelectViewTag extends TableTag
{
    
    private Collection list1;
    private Collection list2;
    
    /** Creates a new instance of TableIndividualTag */
    public SelectViewTag() {
        super();
    }
    
    /**
     * Sets the first list to display
     * @param l1 The list
     */
    public void setList1(Collection l1)
    {
        list1 = l1;
    }
    
    /**
     * Sets the second list to display
     * @param l2 The second list
     */
    public void setList2(Collection l2)
    {
        list2 = l2;
    }
    
    
    
    /**
     * Builds the HTML code for the display
     * @throws javax.servlet.jsp.JspException If something went wrong when building the view
     * @return The BodyTagSupport.EVAL_PAGE if everything went ok
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

            // For each of the objects in dao
            while (i.hasNext()) 
            {
                Object obj = i.next();
                for (int j=0;j<tokenized.length;j++)
                {
                    if (tokenized[j].charAt(0) == ':')
                    {
                        String name = tokenized[j].substring(1);

                        Method method = obj.getClass().getMethod(name, null);
                        
                        Object out = method.invoke(obj, null);
                        if (out!=null)
                            data += out;
                        else
                            data += "&nbsp;";
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
