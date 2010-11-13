/*
 * NotImplementedException.java
 *
 * Created on June 22, 2005, 10:48 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.exceptions;

/**
 * Exception thrown when attempt to use features which are not implemented are made
 * @author heto
 */
public class NotImplementedException extends ApplicationException {
    
    /**
     * Creates a new instance of NotImplementedException
     * @param message The exception message
     */
    public NotImplementedException(String message) 
    {
        super(message);
    }
    
    /**
     * Constructor for NotImplementedException
     * @param message The exception message
     * @param e The underlying exception thats propageted to this exception
     */
    public NotImplementedException(String message, Exception e) 
    {
        super(message);
        initCause(e);
    }
    
}
