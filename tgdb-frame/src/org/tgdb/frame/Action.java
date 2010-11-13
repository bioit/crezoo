/*
 * AbstractAction.java
 *
 * Created on July 7, 2005, 10:41 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.frame;
import org.tgdb.frame.advanced.Workflow;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Abstract class for an action
 * @author heto
 */
public abstract class Action 
{
    
    protected Caller caller;
    protected Workflow workflow;
    
    /** Creates a new instance of AbstractAction */
    public Action() 
    {
        caller = null;
    }
    
    /**
     * Returns the name of the action
     * @return The name of the action
     */
    public abstract String getName();
    //public abstract Object perform(HttpServletRequest req);
    
    /**
     * Performs the action
     * @param req The http request
     * @param context The servlet context
     * @throws org.tgdb.exceptions.ApplicationException If the action could not be performed
     * @return True, if the action could be performed
     */
    public abstract boolean performAction(HttpServletRequest req, ServletContext context) throws ActionException;
    
    public boolean performAction(HttpServletRequest req, HttpServletResponse res, ServletContext context) throws ActionException
    {
        throw new ActionException("Not implemented");
    }
    
    public void setCaller(Caller caller)
    {
        this.caller = caller;
    }
    public void setWorkflow(Workflow workflow)
    {
        this.workflow = workflow;
    }
 
}
