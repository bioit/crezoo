package org.tgdb.frame.advanced;

import org.tgdb.frame.Action;
import org.tgdb.frame.ActionException;
import org.tgdb.frame.Caller;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class State {
    private String name;
    private Action preAction;
    private Action postAction;
    private String view;
    private String newWorkflow;
    private HashMap attributes;
    protected AltState[] altStates;
    
    private Caller caller;
    private Workflow workflow;
    
    
    public State() {
        attributes = new HashMap();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPreAction(Action preAction) {
        this.preAction = preAction;
    }
    
    public Action getPreAction() {
        return preAction;
    }
    
    public void performPreAction(HttpServletRequest req, ServletContext context) throws ActionException {
        if (preAction!=null)
        {
            preAction.setCaller(caller);
            preAction.setWorkflow(workflow);
            boolean res = preAction.performAction(req, context);
            if (!res)
                throw new ActionException("Action \""+postAction.getName()+"\" failed");
        }
    }

    public void setPostAction(Action postAction) {
        this.postAction = postAction;
    }
    
    public Action getPostAction() {
        return postAction;
    }
    
    public void performPostAction(HttpServletRequest req, ServletContext context) throws ActionException {
        if (postAction!=null)
        {
            postAction.setCaller(caller);
            postAction.setWorkflow(workflow);
            boolean res = postAction.performAction(req, context);
            if (!res)
                throw new ActionException("Action \""+postAction.getName()+"\" failed");
        }
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getNewWorkflow() {
        if (newWorkflow!=null && newWorkflow.equals(""))
            return null;
        return newWorkflow;
    }

    public void setNewWorkflow(String newWorkflow) {
        this.newWorkflow = newWorkflow;
    }
    
    /**
     * Set a string attribute on the state available during current workflow. 
     * This could be used to save posted data to be able to go back in states
     * and yet have posted data available. 
     * @param name is the name of the attribute.
     * @param value is the value of the attribute.
     */
    public void setAttribute(String name, String value)
    {
        attributes.put(name, value);
    }
    
    /**
     * Get a string attribute on the state available during current workflow. 
     * This could be used to save posted data to be able to go back in states
     * and yet have posted data available. 
     * @param name is the name of the attribute 
     * @return the value of the given name. Null is returned if not found.
     */
    public String getAttribute(String name)
    {
        return (String)attributes.get(name);
    }
    
    /**
     * Remove all attributes available in this state.
     */
    public void removeAllAttributes()
    {
        attributes = new HashMap();
    }
    
    /**
     * Get an alternative state if any actions in this state throws an 
     * ActionException with a message setAlt(...). The name available in the 
     * exception message and the name of the altState must match.
     * @param name the name of the alternative state to fetch
     * @return an altState object with the alternative state.
     */
    public AltState getAltState(String name)
    {
        if (altStates == null)
            return null;
        for (int i=0;i<altStates.length;i++)
        {
            if (name.equals(altStates[i].getName()))
            {
                return altStates[i];
            }
        }
        return null;
    }
    
    /**
     * Return this object to a string representation.
     * @return a string of the representation.
     */
    public String toString()
    {
        String preActionString = "";
        if (preAction==null)
            preActionString="null";
        else
            preActionString = preAction.toString();
        
        String out = "<state name=\""+name+"\" preaction=\""+preActionString+"\">\n";
        for (int i=0;i<altStates.length;i++)
        {
            out += "\t"+altStates[i].toString();
        }
        out += "</state>\n";
        return out;
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
