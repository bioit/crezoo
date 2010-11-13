/*
 * IllegalValueException.java
 *
 * Created on June 21, 2005, 3:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.exceptions;

/**
 * Exception thrown when wrong parameter values are used as input to the application
 * @author heto
 */
public class IllegalValueException extends ApplicationException{
    
    /**
     * Creates a new instance of IllegalValueException
     * @param message The exception message
     */
    public IllegalValueException(String message) 
    {
        super(message);
    }
    
    /**
     * Constructor for IllegalValueException
     * @param message The exception message
     * @param e The underlying exception thats propageted to this exception
     */
    public IllegalValueException(String message, Exception e) 
    {
        super(message);
        initCause(e);
    }
}
