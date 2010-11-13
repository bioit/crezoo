/*
 * ChromosomeNotFoundException.java
 *
 * Created on November 2, 2006, 3:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tgdb.exceptions;

/**
 *
 * @author se22519
 */
public class ChromosomeNotFoundException extends ApplicationException
{
    
    /** Creates a new instance of ChromosomeNotFoundException */
    public ChromosomeNotFoundException(String msg, Exception e)
    {
        super(msg,e);
    }
    
    public ChromosomeNotFoundException(String msg)
    {
        super(msg);
    }
    
}
