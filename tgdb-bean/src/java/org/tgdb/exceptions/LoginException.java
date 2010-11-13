/*
 * LoginException.java
 *
 * Created on June 29, 2005, 4:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.exceptions;

/**
 * Exception thrown when login fails for some reasen
 * @author heto
 */
public class LoginException extends ApplicationException {
    
    /**
     * Creates a new instance of LoginException
     * @param message The exception message
     */
    public LoginException(String message) 
    {
        super(message);
    }
    
    /**
     * Constructor for LoginException
     * @param message The exception message
     * @param e The underlying exception that is propagated to this exception
     */
    public LoginException(String message, Exception e) 
    {
        super(message);
        this.initCause(e);
    }
    
}
