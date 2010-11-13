/*
 * DbException.java
 *
 * Created on February 2, 2005, 3:26 PM
 */

package se.arexis.agdb.db;

/**
 * Exception class to handle error signaling between the new db library and
 * the Servlets. This transports messages to the user. All messages must make sense 
 * for the user!
 *
 * @author heto
 */
public class DbException extends Exception
{
    
    /** Creates a new instance of DbException */
    public DbException(String message) 
    {
        super(message);
    }
    
}
