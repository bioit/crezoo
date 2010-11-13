/*
 * GroupNotFoundException.java
 *
 * Created on November 2, 2006, 10:14 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tgdb.exceptions;

/**
 *
 * @author se22519
 */
public class GroupNotFoundException extends ApplicationException
{
    
    /** Creates a new instance of GroupingNotFoundException */
    public GroupNotFoundException(String msg, Exception e)
    {
        super(msg, e);
    }
    
    public GroupNotFoundException(String msg)
    {
        super(msg);
    }
    
}
