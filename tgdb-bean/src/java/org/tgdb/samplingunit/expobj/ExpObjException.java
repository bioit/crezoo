/*
 * ExpObjException.java
 *
 * Created on November 15, 2006, 2:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tgdb.samplingunit.expobj;

/**
 *
 * @author se22519
 */
public class ExpObjException extends Exception
{
    
    /** Creates a new instance of ExpObjException */
    public ExpObjException(String msg)
    {
        super(msg);
    }
    
    public ExpObjException(String msg, Exception e)
    {
        super(msg, e);
    }
}
