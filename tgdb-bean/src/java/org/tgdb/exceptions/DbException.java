/**
 * Exception class to handle error signaling between the new db library and
 * the Servlets. This transports messages to the user. All messages must make sense 
 * for the user!
 *
 * @author heto
 */

package org.tgdb.exceptions;


/**
 * Handles database related exceptions (legacy???)
 */
public class DbException extends ApplicationException
{
    
    /**
     * Creates a new instance of DbException
     * @param message The exception message
     */
    public DbException(String message) 
    {
        super(message);
    }
    
    public DbException(String message, Exception e) 
    {
        super(message);
        this.initCause(e);
    }
    
}
