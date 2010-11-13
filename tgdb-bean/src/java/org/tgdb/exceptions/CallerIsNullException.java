/*
 * CallerIsNullException.java
 *
 * Created on November 2, 2006, 9:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tgdb.exceptions;

/**
 *
 * @author se22519
 */
public class CallerIsNullException extends ApplicationException
{
    
    /** Creates a new instance of CallerIsNullException */
    public CallerIsNullException(String msg, Exception e)
    {
        super(msg, e);
    }
    
    public CallerIsNullException(String msg)
    {
        super(msg);
    }
    
}
