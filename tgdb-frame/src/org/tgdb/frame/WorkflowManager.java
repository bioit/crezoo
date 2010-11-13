/*
 * WorkflowManager.java
 *
 * Created on July 7, 2005, 4:57 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.frame;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 *
 * @author heto
 */
public class WorkflowManager implements IWorkFlowManager {
    
    private static final String WORKFLOWS_TAG = "workflows";
    private static final String WORKFLOW_TAG = "workflow";
    private static final String STATE_TAG = "state";
    private static final String NAME_ATTR = "name";
    private static final String ACTION_ATTR = "action";
    private static final String PRE_ACTION_ATTR = "preaction";
    private static final String VIEW_ATTR = "viewURI";
    private static final String NEW_WORKFLOW_ATTR = "workflow";
    
    private static final String ACTION_PREFIX = "org.tgdb.webapp.action.";
    
    private Caller caller;
    
    
    /**
     * Handles a workflow.
     */
    class WorkflowObject
    {
        private String name;
        protected State[] states;
        
        public WorkflowObject(String name)
        {
            this.name = name;
        }
        
        public String getName()
        {
            return name;
        }
    }
    
    class State
    {
        protected String name;
        protected Action action;
        protected Action preAction;
        protected String viewURI;
        protected String newWorkflow;
    }
            
    private String currentWorkflowName;
    private int currentState;
    private boolean firstDone = false;
    private HashMap workflows;
    
    private int get;
    
    
    
    /**
     * Called by the controller
     */
    public void setContext(ServletContext context) throws IOException
    {
        InputStream is = context.getResourceAsStream("xml/workflow.xml");
        try
        {
            if (context == null)
                throw new Exception("Context is null!");
            
            // Init the HashMap
            workflows = new HashMap();
            
            // Parse XML and populate HashMap.
            parseXML(is);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new IOException("WorkflowManager#setContext: "+e.getMessage());
        }
        currentState=0;
    }
    
    public void setCaller(Caller caller)
    {
        this.caller = caller;
    }
    
    /** 
     * Get the action class for an action name
     */
    private Action getAction(String name) throws Exception
    {
        if (name != null && name.length() > 0)
        {
            Class c = Class.forName(ACTION_PREFIX + name);
            return (Action)c.newInstance();
        }
        return null;
    }
    
    private void reset()
    {
        currentState = 0;
        firstDone = false;
        //currentWorkflowName = null;
    }
    
    /** 
     * Get the workflow object from the request parameter "workflow". 
     * If workflow not found, return null.
     *
     */
    private WorkflowObject getWorkflow(HttpServletRequest req)
    {
        if (req.getParameter("workflow") != null)
        {
            reset();
            currentWorkflowName = req.getParameter("workflow");
        }
        if (currentWorkflowName == null)
        {
            reset();
            return null;
        }
        
        /** 
         * Get the right workflow object by the currentWorkflowName
         */
        return (WorkflowObject)workflows.get(currentWorkflowName);
    }
    
    private boolean executePreAction(State s, HttpServletRequest req, ServletContext context) throws ActionException
    {
        if (s.preAction != null)
        {
            System.err.println("PreAction: State="+s.name+", PreAction="+s.preAction.getName());
            return s.preAction.performAction(req, context);
        }
        else
            return true;
    }
    
    
    private String next(State s1, State s2, HttpServletRequest req, ServletContext context) throws ActionException
    {
        // Action
        if (s1==null || s1.action==null || s1.action.performAction(req, context))
        {
            // Action ok
        }
        
        // PreAction
        if (s2==null || s2.preAction==null || s2.preAction.performAction(req, context))
        {
            // PreAction ok
        }
        
        // Next Page
        if (s2!=null)
            return s2.viewURI;
        else
            return null;
    }
    
    private State getState(WorkflowObject wfo, int index)
    {   
        State s = null;
        try
        {
            s = wfo.states[index];
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return s;
    }
    
    public String getNextPage3(HttpServletRequest req, ServletContext context)
    {
        String page = "";
        try
        {
            HttpSession session = req.getSession();

            /** Get workflow */
            WorkflowObject wfo = getWorkflow(req);
            if (wfo==null)
            {
                reset();
                throw new Exception("Workflow \""+req.getParameter("workflow")+"\" not found");
            }
            
            // Get previous state
            State s0 = getState(wfo, get-1);

            /** Get current state */
            State s1 = getState(wfo, get);
            //State s1 = wfo.states[currentState];
            
            
            // Action (last state)
            if (s0==null || s0.action==null || s0.action.performAction(req, context))
            {
                // Action ok
                
                
                if (s0!=null)
                {
                    //Previous Action ok, get next state
                    //currentState++;
                }
            }

            // PreAction
            if (s1==null || s1.preAction==null || s1.preAction.performAction(req, context))
            {
                // PreAction ok
            }
            
            
            // View
            if (s1!=null)
            {
                page = s1.viewURI;
            }
            
            
            
            
            if (get>wfo.states.length)
            {
                get=0;
            }
            else
                get++;
            
        }
        catch (Exception e)
        {
            req.setAttribute("exception", e);
            e.printStackTrace();
            page="error/GeneralError.jsp";
        }

        return page;
    }
    
    
    /**
     * Get the next page in workflow.
     */
    public String getNextPage(HttpServletRequest req, ServletContext context)
    {
        String page = "";
        try
        {
            HttpSession session = req.getSession();

            /** Get workflow */
            WorkflowObject wfo = getWorkflow(req);
            if (wfo==null)
            {
                reset();
                throw new Exception("Workflow \""+req.getParameter("workflow")+"\" not found");
            }

            /** Get current state */
            State s = wfo.states[currentState];

            /** Is this the first state? */
            if (currentState==0 && !firstDone)
            {
                /** Set current workflow */
                Navigator nav = (Navigator)session.getAttribute("navigator");
                nav.setCurrentWorkflow(wfo.getName());

                firstDone = true;
            }
            else if (s.action==null || s.action.performAction(req, context))
            {   // Action completed successfully or there were no action.
                if (s.action!=null)
                    System.err.println("Action: State="+s.name+", Action="+s.action.getName());

                // Check the states number.
                if (currentState < wfo.states.length-1)
                {   // Some state (not the last one)
                    /** Go to next state */
                    s = wfo.states[++currentState];
                }
                else
                {   // Last State. Reset all variables.
                    reset();
                }
            }
            else
            {   // Action failed
                throw new Exception("Action \""+s.action.getName()+"\" failed");
            }  

            /** Execute pre-action */
            if (!executePreAction(s, req, context))
            {
                throw new Exception("PreAction \""+s.preAction.getName()+"\"failed");
            }
            
            // Check a reference to another workflow.
            // Begin the new workflow instead of current one.
            if (s.newWorkflow!=null && !s.newWorkflow.equals(""))
            {

                currentWorkflowName = s.newWorkflow;
                currentState=0;
                
                wfo = getWorkflow(req);
                if (wfo==null)
                {
                    reset();
                    throw new Exception("Workflow \""+req.getParameter("workflow")+"\" not found");
                }

                /** Get current state */
                s = wfo.states[currentState];
                /** Set current workflow */
                Navigator nav = (Navigator)session.getAttribute("navigator");
                nav.setCurrentWorkflow(wfo.getName());
                
                /** Execute pre-action */
                if (!executePreAction(s, req, context))
                {
                    throw new Exception("PreAction \""+s.preAction.getName()+"\"failed");
                }
                page = s.viewURI;
            }
            else
            {
                /** Get view */
                page = s.viewURI;
            }
        }
        catch (Exception e)
        {
            req.setAttribute("exception", e);
            e.printStackTrace();
            page="error/GeneralError.jsp";
        }

        return page;
    }
    
  
    
    public void printWorkflow()
    {
        String out = "";
        
        Collection flows = workflows.values();
       
        Iterator itr = flows.iterator();

        out += "<workflows>\n";
        while (itr.hasNext())
        {
            WorkflowObject wf = (WorkflowObject)itr.next();
            out += "<workflow name="+wf.name+">\n";

            for (int i=0;i<wf.states.length;i++)
            {
                String actionName = "";
                if (wf.states[i].action != null)
                    actionName = wf.states[i].action.getName();
                
                String viewURI = "";
                if (wf.states[i].viewURI != null)
                    viewURI = wf.states[i].viewURI;
                
                out += "<state name="+wf.states[i].name+" action="+actionName+" viewURI="+viewURI+"/>\n";
            }
            out += "</workflow>\n";
        }
        out += "</workflows>\n";
        System.err.println(out);
    }
    
    
   
    
    private void parseXML(InputStream is) throws Exception
    {
        try
        {
            
        
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            // Find the workflows element
            NodeList wrkflws = doc.getElementsByTagName(WORKFLOWS_TAG);
            Element e_wrkflws = (Element)wrkflws.item(0);
            
            

            // Find the workflow element
            NodeList workflows = doc.getElementsByTagName(WORKFLOW_TAG);
            //Element workflow = (Element)workflows.item(0);

            for (int j=0;j<workflows.getLength();j++)
            {
                Element workflow  = (Element)workflows.item(j);

                WorkflowObject w = new WorkflowObject((String)workflow.getAttribute(NAME_ATTR));
                
                // Find all states
                NodeList states = workflow.getChildNodes();
                //NodeList states = doc.getElementsByTagNameNS(workflow.getPrefix(), STATE_TAG);
                
                // Create an array for states
                ArrayList arr = new ArrayList();
                
                // Read state info
                for (int i=0;i<states.getLength();i++)
                {
                    // Get element
                    Node node = states.item(i);
                    
                    Element curState = null;
                    if (node.getNodeType()==node.ELEMENT_NODE)
                    {
                        curState = (Element)node;
                        State state = new State();

                        state.name = curState.getAttribute(NAME_ATTR);
                        state.viewURI = curState.getAttribute(VIEW_ATTR);
                        state.newWorkflow = curState.getAttribute(NEW_WORKFLOW_ATTR);

                        // Convert Action names into class instances
                        state.action = getAction(curState.getAttribute(ACTION_ATTR));
                        state.preAction = getAction(curState.getAttribute(PRE_ACTION_ATTR));
                       
                        arr.add(state);
                    }
                }
                
                State[] stateList = new State[arr.size()];
                for (int k=0;k<arr.size();k++)
                {
                    stateList[k] = (State)arr.get(k);
                }
                w.states = stateList;

                // Add the workflow object to the hashmap.
                this.workflows.put(w.getName(), w);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception("WorkflowManager#parseXML: Error parsing XML. "+e.getMessage());
        }
    }
    
    /** Creates a new instance of WorkflowManager */
    public WorkflowManager() 
    {
        //logger.debug("WorkflowManager#WorkflowManager: New WorkflowManager");
    }    
}
