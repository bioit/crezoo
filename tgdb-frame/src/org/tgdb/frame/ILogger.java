/*
 * ILogger.java
 *
 * Created on December 22, 2005, 11:07 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.frame;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author heto
 */
public interface ILogger {
    
    public void log(HttpServletRequest request, 
            HttpServletResponse response);
}
