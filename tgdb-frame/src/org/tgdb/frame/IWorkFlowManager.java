/*
 * IWorkFlowManager.java
 *
 * Created on July 7, 2005, 4:55 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.frame;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author heto
 */
public interface IWorkFlowManager 
{
    /**
     * Called after initalization
     */
    public void setContext(ServletContext context) throws IOException;
    
    /**
     * Called for each request
     */
    public String getNextPage(HttpServletRequest req, ServletContext context) throws ArxFrameException;
    
    /** Set the caller object to the manager*/
    public void setCaller(Caller caller);
    
}
