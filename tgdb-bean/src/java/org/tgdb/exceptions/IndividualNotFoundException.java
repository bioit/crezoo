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
public class IndividualNotFoundException extends ApplicationException
{
    
    /** Creates a new instance of SamplingUnitNotFoundException */
    public IndividualNotFoundException(String txt, Exception e)
    {
        super(txt, e);
    }
    
    public IndividualNotFoundException(String txt)
    {
        super(txt);
    }
    
}
