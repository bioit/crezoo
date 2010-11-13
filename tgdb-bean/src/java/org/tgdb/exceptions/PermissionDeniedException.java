/*
 * PermissionDeniedException.java
 *
 * Created on June 30, 2005, 2:49 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.exceptions;

/**
 * Exception thrown when the user does not have sufficient privileges for some operation on the system
 * @author heto
 */
public class PermissionDeniedException extends ApplicationException{
    
    /**
     * Creates a new instance of PermissionDeniedException
     * @param message The exception message
     */
    public PermissionDeniedException(String message) {
        super(message);
    }
    
    /**
     * Constructor for PermissionDeniedException
     * @param message The exception message
     * @param e The underlying exception thats propagated to this exception
     */
    public PermissionDeniedException(String message, Exception e) {
        super(message);
        initCause(e);
    }
    
}
