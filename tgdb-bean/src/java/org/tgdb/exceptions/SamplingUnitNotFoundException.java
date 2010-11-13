/*
 * SamplingUnitNotFoundException.java
 *
 * Created on November 1, 2006, 9:48 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tgdb.exceptions;

/**
 *
 * @author se22519
 */
public class SamplingUnitNotFoundException extends ApplicationException
{
    
    /** Creates a new instance of SamplingUnitNotFoundException */
    public SamplingUnitNotFoundException(String txt, Exception e)
    {
        super(txt, e);
    }
    
    public SamplingUnitNotFoundException(String txt)
    {
        super(txt);
    }
    
}
