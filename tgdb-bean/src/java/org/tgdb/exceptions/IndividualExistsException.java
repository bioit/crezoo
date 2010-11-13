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
public class IndividualExistsException extends ApplicationException
{
    
    /** Creates a new instance of SamplingUnitNotFoundException */
    public IndividualExistsException(String txt, Exception e)
    {
        super(txt, e);
    }
    
}
