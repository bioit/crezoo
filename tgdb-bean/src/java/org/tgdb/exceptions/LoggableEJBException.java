/*
 * LoggableEJBException.java
 *
 * Created on November 1, 2006, 9:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tgdb.exceptions;

import javax.ejb.EJBException;

/**
 *
 * @author se22519
 */
public class LoggableEJBException extends EJBException
{
    protected boolean isLogged;
    protected String uniqueID;
    
    /** Creates a new instance of LoggableEJBException */
    public LoggableEJBException(Exception exc)
    {
        super(exc);
        isLogged = false;
        uniqueID = "xyz123";
    }
    
}
