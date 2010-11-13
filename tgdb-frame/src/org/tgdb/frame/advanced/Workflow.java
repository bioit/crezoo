/*
 * Workflow.java
 *
 * Created on November 14, 2005, 12:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.frame.advanced;

import org.tgdb.frame.ActionException;
import org.tgdb.frame.Caller;
import org.tgdb.frame.WorkflowException;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Handle a workflow in a web application.
 *
 * A workflow is a process that have multiple states, each state can have an
 * alternative state if exceptions are thrown in a state. A new workflow can 
 * be started from within a state either from an alternative state, and then 
 * new workflow will be run imidiate and the rest of the first workflow will 
 * continue. Otherwise a new workflow can be started and the old discarded.
 *
 
 *
 * Workflow:
 * State1 -> State2 -> State3
 *                       -> AltState
 *                              -> New Workflow
 *
 * All workflows are handled by a WorkflowManager that is available to each 
 * user (an own instance of the manager) that is saved in the users session.
 *
 * @author tobias
 */
public class Workflow {
    
    private static Logger logger = Logger.getLogger(Workflow.class);
    
    private String name;
    public State[] states;
    private int currentState;
    private String test;
    private HashMap attributes;
    
    private boolean preDone;
    
    private Caller caller;
    
        
    
    /**
     * Creates a new instance of Workflow
     * @param name is the name of the new workflow. Workflow name must be 
     * unique!
     */
    public Workflow(String name) {
        this.name = name;
        attributes = new HashMap();
    }
    
    /**
     * Create a new copy of a workflow. This create an independent copy, no
     * references. Also, properties are NOT copied at all, both workflow properties 
     * and state properties!
     * @param w is the workflow to copy.
     */
    public Workflow(Workflow w)
    {
        name = w.getName();
        
        attributes = new HashMap();
        
        states = new State[w.getStateCount()];
        
        for (int i=0;i<w.getStateCount();i++)
        {
            State s = new State();
            s.setName(w.getState(i).getName());
            s.setNewWorkflow(w.getState(i).getNewWorkflow());
            s.setPostAction(w.getState(i).getPostAction());
            s.setPreAction(w.getState(i).getPreAction());
            s.setView(w.getState(i).getView());
            
            s.altStates = new AltState[w.getState(i).altStates.length];
            for (int j=0;j<w.getState(i).altStates.length;j++)
            {
                s.altStates[j] = w.getState(i).altStates[j];
            }
            
            states[i]=s;
        }
    }
    
    /**
     * Get the name of the workflow. The name is unique.
     * @return the name of the workflow.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Set the workflow to a starting position. Set the state to the first state.
     */
    public void setStart()
    {
        currentState = 0;
        preDone = false;
    }
    
    /**
     * Get the current state in this workflow. 
     * @return the state object 
     */
    public State getCurrentState()
    {
        return states[currentState];
    }
    
    
    

    
    /**
     * Set the previous state for this workflow. This checks if we already are 
     * at first state and silently accepts such calls.
     */
    public void setPrevState()
    {
        if (currentState>0)
            currentState--;
        else
            setStart();
    }
    
    /**
     * Get the state with an index.
     * @param index of the states
     * @return the state object
     */
    public State getState(int index)
    {
        return states[index];
    }
    
    /**
     * Get the number of states available in this workflow.
     * @return the number of states.
     */
    public int getStateCount()
    {
        return states.length;
    }
    
    /**
     * Get the workflow object in a text representation.
     * @return the data in an xml format.
     */
    public String toString()
    {
        String out = "<workflow name=\""+name+"\">\n";
        for (int i=0;i<states.length;i++)
        {
            out += "\t"+states[i].toString();
        }
        out += "</workflow>\n";
        return out;
    }
    
    /**
     * Set a string attribute on the workflow. 
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
     * Get a string attribute on the workflow. 
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
     * Remove all attributes available in this workflow.
     */
    public void removeAllAttributes()
    {
        attributes = new HashMap();
    }
    
    private HashMap params;
    
    /**
     * Collect all parameters inside the workflow 
     * in local variables. Only unique identifiers are valid!
     */
    private void collectParameters(HttpServletRequest req)
    {
        if (params==null)
            params = new HashMap();
        
        Enumeration pNames = req.getParameterNames();
        while (pNames.hasMoreElements())
        {
            String key = (String)pNames.nextElement();
            String value = req.getParameter(key);
            params.put(key, value);
            //logger.debug("added param: "+key);
        }
    }
    
    /**
     * Get a collected parameter 
     */
    public String getParameter(String name)
    {
        //logger.debug("Parameters available: "+params.size());
        return (String)params.get(name);
    }
    
    /**
     * Get the next page in this workflow. This method keeps track of current state and increase states when necesary.
     * 
     * @param req 
     * @param context 
     * @throws org.tgdb.frame.advanced.ForwardWorkflowException is thrown if a workflow contains a forwarder to another workflow. The new workflow will run instead of current one.
     * @throws org.tgdb.frame.ActionException 
     * @throws org.tgdb.frame.WorkflowException 
     * @return a view url (page)
     */
    public String getNextPage(HttpServletRequest req, ServletContext context) throws ForwardWorkflowException, ActionException, WorkflowException
    {
        String page = null;
        State state = null;
        
        // Get the state
        state = states[currentState];
        state.setCaller(caller);
        state.setWorkflow(this);
        
        // Collect parameters
        collectParameters(req);
        try
        {
            while (currentState<states.length && page == null)
            {
                // The first state has no prev action
                if (currentState!=0 || preDone)
                {
                    // Do post action
                    state.performPostAction(req, context);

                    // increase index
                    currentState++;
                    if (currentState==states.length)
                        throw new WorkflowException("Workflow '"+this.name+"' ended without view");
                    preDone = false;
                }

                // Get the state
                state = states[currentState];
                state.setCaller(caller);
                state.setWorkflow(this);

                // Do pre action
                state.performPreAction(req, context);
                preDone = true;
                
                // Forward to previous workflow
                if (state.getNewWorkflow()!=null && state.getNewWorkflow().equalsIgnoreCase("back"))
                    throw new BackWorkflowException("Back");

                // Forward to new workflow if wanted.
                if (state.getNewWorkflow()!=null)
                    throw new ForwardWorkflowException("Forward", state.getNewWorkflow());

                // Get page
                page = state.getView();
                
                if (page!=null && page.equals(""))
                    page = null;
            }
        }
        catch (ActionException ae)
        {
            throw ae;
        }
        catch (WorkflowException we)
        {
            //we.printStackTrace();
            throw we;
        }
        catch (Exception e)
        {
            logger.error("Swallow exception", e);
            //e.printStackTrace();
            
        }
        return page;
    }
    
    public void setCaller(Caller caller)
    {
        if (caller==null) 
            logger.error("Workflow#setCaller is null!!!");
        this.caller = caller;
    }
}
