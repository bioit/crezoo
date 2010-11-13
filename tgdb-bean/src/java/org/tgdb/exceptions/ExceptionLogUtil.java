/*
 * ExceptionLogUtil.java
 *
 * Created on November 1, 2006, 9:27 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tgdb.exceptions;

import java.rmi.RemoteException;

/**
 *
 * @author se22519
 */
public class ExceptionLogUtil
{
    
    /** Creates a new instance of ExceptionLogUtil */
    public ExceptionLogUtil()
    {
    }
    
    public static LoggableEJBException createLoggableEJBException(RemoteException re) 
    {
        Throwable t = re.detail;
        if (t!=null && t instanceof Exception)
        {
            return new LoggableEJBException((Exception)re.detail);
        }
        else
        {
            return new LoggableEJBException(re);
        }
    }
    
    
}
