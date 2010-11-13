/*
 * IllegalDateException.java
 *
 * Created on November 1, 2006, 10:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tgdb.exceptions;

/**
 *
 * @author se22519
 */
public class IllegalDateException extends IllegalValueException
{
    
    /** Creates a new instance of IllegalDateException */
    public IllegalDateException(String msg, Exception e)
    {
        super(msg, e);
    }
    
    public IllegalDateException(String msg)
    {
        super(msg);
    }
    
}
