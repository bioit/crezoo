/*
 * MarkerNotFoundException.java
 *
 * Created on November 3, 2006, 11:14 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tgdb.exceptions;

/**
 *
 * @author se22519
 */
public class MarkerNotFoundException extends ApplicationException
{
    
    /** Creates a new instance of ChromosomeNotFoundException */
    public MarkerNotFoundException(String msg, Exception e)
    {
        super(msg,e);
    }
    
    public MarkerNotFoundException(String msg)
    {
        super(msg);
    }
    
}
