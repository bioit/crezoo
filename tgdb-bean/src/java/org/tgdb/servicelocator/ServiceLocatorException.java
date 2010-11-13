/*
 * ServiceLocatorException.java
 *
 * Created on October 21, 2005, 8:36 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.servicelocator;

import javax.ejb.EJBException;

/**
 *
 * @author heto
 */
public class ServiceLocatorException extends EJBException {
    
    /** Creates a new instance of ServiceLocatorException */
    public ServiceLocatorException(String err) {
        super(err);
    }
    
}
