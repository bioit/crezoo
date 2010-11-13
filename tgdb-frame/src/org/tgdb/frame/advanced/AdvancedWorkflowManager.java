package org.tgdb.frame.advanced;

import org.tgdb.frame.Action;
import org.tgdb.frame.ActionException;
import org.tgdb.frame.ArxFrameException;
import org.tgdb.frame.Caller;
import org.tgdb.frame.IWorkFlowManager;
import org.tgdb.frame.WorkflowException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AdvancedWorkflowManager implements IWorkFlowManager {
    
    private static Logger logger = Logger.getLogger(AdvancedWorkflowManager.class);
    
    private static final String WORKFLOWS_TAG = "workflows";
    private static final String WORKFLOW_TAG = "workflow";
    private static final String STATE_TAG = "state";
    private static final String NAME_ATTR = "name";
    private static final String ACTION_ATTR = "action";
    private static final String PRE_ACTION_ATTR = "preaction";
    private static final String VIEW_ATTR = "viewURI";
    private static final String NEW_WORKFLOW_ATTR = "workflow";
    
    private String ACTION_PREFIX = "action.";    
    private String DEFAULT_WORKFLOW;
    
    private HashMap workflows;
    
    private Workflow currentWorkflow;
    
    private WorkflowHistory history;
    
    private static final int MAX_HISTORY = 5;
    
    private Caller caller;
    
    private ArrayList workflowStack;
   
    public AdvancedWorkflowManager() {}   
    
    public void setContext(ServletContext context) throws IOException {
        InputStream is = context.getResourceAsStream("xml/workflow.xml");
        try
        {
            if (context == null)
                throw new Exception("Context is null!");
            
            // Init the HashMap
            workflows = new HashMap();
            
            history = new WorkflowHistory(MAX_HISTORY);
            workflowStack = new ArrayList();
            
            // Parse XML and populate HashMap.
            parseXML(is);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("WorkflowManager#setContext: "+e.getMessage());
        }
    }
    
    public void setCaller(Caller caller) {
        
        if (caller==null) 
            logger.warn("----------------------------------->AdvancedWorkflowManager#setCaller: Caller is null");
        
        this.caller = caller;
    }
    
    private Action getAction(String name) throws ArxFrameException {
        try {
            if (name != null && name.length() > 0)
            {
                Class c = Class.forName(ACTION_PREFIX + name);
                return (Action)c.newInstance();
            }
            return null;
        } catch (Exception e) {
            throw new ArxFrameException("Failed to get action "+ACTION_PREFIX+name, e);
        }
    }
    
    private boolean historyWorkflow(String workflow) {
        boolean worthHistory = false;
        String [] validWorkflows = {"ViewModels", "ViewModel", "ViewStrains", "ViewStrain", "ViewUser", "ViewGenes", "ViewGene", "ViewGeneticBackgroundValues", "ViewAvailableGeneticBackgrounds", "ViewRepositories", "DisseminationUpdate", "SearchKeywordFast", "ViewSimpleLogs"};
        for(int i=0; i < validWorkflows.length; i++){
            if(workflow.compareTo(validWorkflows[i])==0){
                worthHistory = true;
                return worthHistory;
            }
        }
        return worthHistory;
    }
    
    private boolean repeatedWorkflow(String workflow, int depth) {
        boolean repeatWorkflow = false;
        if(history.elements()<depth){
            return repeatWorkflow;
        } else {
            Workflow tmp = history.get(depth);
            if(workflow.compareTo(tmp.getName())==0){
                repeatWorkflow = true;
                return repeatWorkflow;
            }
        }
        return repeatWorkflow;
    }
    
    private boolean circleWorkflow(String workflow) {
        boolean circleHistory = false;
        String [] validWorkflows = {"ViewModels", "PhenoAssign", "ViewStrains", "DisseminationUpdate", "ViewSimpleLogs"};
        for(int i=0; i < validWorkflows.length; i++){
            if(workflow.compareTo(validWorkflows[i])==0){
                circleHistory = true;
                return circleHistory;
            }
        }
        return circleHistory;
    }
    
    private Workflow getWorkflow(HttpServletRequest req) throws WorkflowException { 
        if (req.getParameter("workflow") != null || workflowStack.size()==0) {
            String newWorkflow;
            if (req.getParameter("workflow")==null && workflowStack.size()==0) {
                //logger.debug("----------------------------------->AdvancedWorkflowManager#getWorkflow: Setting default workflow '"+DEFAULT_WORKFLOW+"'");
                newWorkflow = DEFAULT_WORKFLOW;
            } else {
                newWorkflow = req.getParameter("workflow");
            }
            
            logger.debug("----------------------------------->AdvancedWorkflowManager#getWorkflow: Workflow is '"+newWorkflow+"'");
            
            workflowStack = new ArrayList();
            Workflow tmp = (Workflow)workflows.get(newWorkflow);
            if (tmp==null) {
                throw new WorkflowException("Workflow \""+newWorkflow+"\" not found");
            }
            
            // Do not add the same workflow twice in history. 
            // This helps the back buttons then using hide-windows.
            if (currentWorkflow!=null && !currentWorkflow.getName().equals(newWorkflow) && historyWorkflow(currentWorkflow.getName()) && !repeatedWorkflow(currentWorkflow.getName(), 1)) {
                history.add(currentWorkflow);
                logger.debug("----------------------------------->AdvancedWorkflowManager#getWorkflow: Workflow '"+currentWorkflow.getName()+"' was added to history");
            }
            
            // Set the new workflow. 
            currentWorkflow = new Workflow(tmp);
            workflowStack.add(0, currentWorkflow);
            
            if (currentWorkflow==null)
                throw new WorkflowException("Workflow "+newWorkflow+" not found");            
            
            currentWorkflow.setStart();
        } else {
            logger.debug("----------------------------------->AdvancedWorkflowManager#getWorkflow: Workflow is undefined");
        }
        
        for(int i=1; i < history.elements()+1;i++){
            logger.debug("----------------------------------->AdvancedWorkflowManager#getWorkflow: Workflow history element "+i+" is '"+history.get(i).getName()+"'");
        }
        //logger.debug("AdvancedWorkflowManager#getWorkflow#FirstInHistory="+history.get(1).getName());
        
        /** Get the right workflow object by the currentWorkflowName */
        return (Workflow)workflowStack.get(0);
    }
     
    public void removeMaliciousWorkflows(String maliciousWorkflow) {
        history.massRemove(maliciousWorkflow);
    }
    
    public String getNextPage(HttpServletRequest req, ServletContext context) throws ArxFrameException {
        String page = null;
        try {
            currentWorkflow = getWorkflow(req);
            
            //logger.debug("----------------------------------->AdvancedWorkflowManager#getNextPage: Current workflow '"+currentWorkflow.getName()+"'");
            
            if (req.getParameter("back")!=null && req.getParameter("back").equals("state")) {
                currentWorkflow.setPrevState();
            } else if (req.getParameter("back")!=null || req.getParameter("back.x")!=null) {
                Workflow tmp = history.get(1);
                if(tmp.getName().compareTo("begin")==0){
//                    currentWorkflow = tmp;
//                    currentWorkflow.setStart();
//                    currentWorkflow.setCaller(caller);
//                    req.setAttribute("workflow", currentWorkflow);
//                    page = "welcome.html";
//                    return page;
                } else if(!repeatedWorkflow(currentWorkflow.getName(), 1)){
                    if(circleWorkflow(currentWorkflow.getName())){
                        history.remove(0);
                    }
                    currentWorkflow = tmp;
                    currentWorkflow.setStart();
                } else {
                    history.remove(0);
                    currentWorkflow = history.get(1);
                    currentWorkflow.setStart();
                    
                    if(currentWorkflow.getName().compareTo("begin")==0){
//                        currentWorkflow.setCaller(caller);
//                        req.setAttribute("workflow", currentWorkflow);
//                        page = "welcome.html";
//                        return page;
                    }
                    
                    if(currentWorkflow.getName().compareTo("ViewUser")==0){
                        currentWorkflow.setCaller(caller);
                        req.setAttribute("workflow", currentWorkflow);
                        page = "ViewUser";
                        return page;
                    }
                }
            }
            
            if(req.getParameter("workflow")==null){
                workflowStack.remove(0);
                workflowStack.add(0,currentWorkflow);
            }
            
            currentWorkflow.setCaller(caller);
            
            /* Save the workflow in the request object */
            req.setAttribute("workflow", currentWorkflow);
            
            page = currentWorkflow.getNextPage(req, context);
            
            logger.debug("----------------------------------->AdvancedWorkflowManager#getNextPage: Page is '"+page+"'");
        } catch (ForwardWorkflowException fwe) {
            if (currentWorkflow!=null && historyWorkflow(currentWorkflow.getName()) && !repeatedWorkflow(currentWorkflow.getName(), 1)) {
                history.add(currentWorkflow);
                logger.debug("----------------------------------->AdvancedWorkflowManager#getNextPage: Workflow '"+currentWorkflow.getName()+"' was added to history via forward");
            }

            currentWorkflow = new Workflow((Workflow)workflows.get(currentWorkflow.getCurrentState().getNewWorkflow()));
            
            workflowStack.remove(0);
            workflowStack.add(0,currentWorkflow);
            
            logger.debug("----------------------------------->AdvancedWorkflowManager#getNextPage: Workflow is '"+((Workflow)workflowStack.get(0)).getName()+"'");
            
            /* Save the workflow in the request object */
            req.setAttribute("workflow", currentWorkflow);
            currentWorkflow.setCaller(caller);
            
            page = currentWorkflow.getNextPage(req, context);
        } catch (BackWorkflowException back) {
            if(repeatedWorkflow(currentWorkflow.getName(), 1)){
                history.remove(0);
            }
            currentWorkflow = history.get(1);
            
            workflowStack.remove(0);
            workflowStack.add(0,currentWorkflow);
            logger.debug("----------------------------------->AdvancedWorkflowManager#getNextPage: Workflow is '"+((Workflow)workflowStack.get(0)).getName()+"'");
            
            currentWorkflow.setStart();

            currentWorkflow.setCaller(caller);
            
            req.setAttribute("workflow", currentWorkflow);
            
            page = currentWorkflow.getNextPage(req, context);
        } catch (AlterWorkflowException alt) {
            AltState a = currentWorkflow.getCurrentState().getAltState(alt.getName());
            String wfName = a.getNewWorkflow();
            
            Workflow tmp = new Workflow((Workflow)workflows.get(wfName));
            workflowStack.add(0, tmp);
            currentWorkflow = tmp;
            
            /* Save the workflow in the request object */
            req.setAttribute("workflow", currentWorkflow);
            
            page = currentWorkflow.getNextPage(req, context);
        } catch (ActionException ae) {
            req.setAttribute("exception", ae);
            page="error/GeneralError.jsp";
            logger.error("----------------------------------->AdvancedWorkflowManager#getNextPage: Action exception. Page is '"+page+"' \n", ae);
            throw ae;
        } catch (Exception e) {
            logger.error("General error in workflow logic.",e);
            throw new ArxFrameException("Workflow failure",e);
        }
        
        for(int i=1; i<history.elements()+1;i++){
            logger.debug("----------------------------------->AdvancedWorkflowManager#getNextPage: Workflow history element "+i+" is '"+history.get(i).getName()+"'");
        }
        
        return page;
    }
    
    private void debugWorkflowStack() {
        if (workflowStack==null)
            return;
        
        String out = "----------------------------------->AdvancedWorkflowManager#debugWorkflowStack: \n";
        
        out += "<workflow-stack>\n";
        for (int i=0;i<workflowStack.size();i++) {
            out += "\t<workflow id=\""+i+"\" name=\""+((Workflow)workflowStack.get(i)).getName()+"\"/>\n";
        }
        out += "</workflow-stack>\n";
        logger.debug(out);
    }
   
    private void parseXML(InputStream is) throws ArxFrameException {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            // Find the workflows element
            NodeList wrkflws = doc.getElementsByTagName(WORKFLOWS_TAG);
            Element e_wrkflws = (Element)wrkflws.item(0);
            
            if (e_wrkflws.getAttribute("action-prefix")!=null && 
                    !e_wrkflws.getAttribute("action-prefix").equals(""))
                ACTION_PREFIX = e_wrkflws.getAttribute("action-prefix");
            
            if (e_wrkflws.getAttribute("default-workflow")!=null &&
                    !e_wrkflws.getAttribute("default-workflow").equals(""))
                DEFAULT_WORKFLOW = e_wrkflws.getAttribute("default-workflow");
            

            // Find the workflow element
            NodeList workflows = doc.getElementsByTagName(WORKFLOW_TAG);
            //Element workflow = (Element)workflows.item(0);

            for (int j=0;j<workflows.getLength();j++)
            {
                Element workflow  = (Element)workflows.item(j);

                Workflow w = new Workflow((String)workflow.getAttribute(NAME_ATTR));
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

                        state.setName(curState.getAttribute(NAME_ATTR));
                        state.setView(curState.getAttribute(VIEW_ATTR));
                        state.setNewWorkflow(curState.getAttribute(NEW_WORKFLOW_ATTR));

                        // Convert Action names into class instances
                        state.setPostAction(getAction(curState.getAttribute(ACTION_ATTR)));
                        state.setPreAction(getAction(curState.getAttribute(PRE_ACTION_ATTR)));
                        
                        AltState[] alts = getAltStates(curState);
                        state.altStates = alts;
                       
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
            logger.error("----------------------------------->AdvancedWorkflowManager#parseXML: Error parsing workflow.xml", e);
            throw new ArxFrameException("Application error, problem reading workflow data", e);
        }
    }
    
    private AltState[] getAltStates(Element curState) throws Exception {
        NodeList alts = curState.getChildNodes();
        ArrayList arr = new ArrayList();
        for (int i=0;i<alts.getLength();i++)
        {
            // Get element
            Node node = alts.item(i);
            Element alt = null;
            if (node.getNodeType()==node.ELEMENT_NODE)
            {
                alt = (Element)node;
                
                AltState state = new AltState();

                state.setName(alt.getAttribute(NAME_ATTR));
                state.setView(alt.getAttribute(VIEW_ATTR));
                state.setNewWorkflow(alt.getAttribute(NEW_WORKFLOW_ATTR));

                // Convert Action names into class instances
                state.setPostAction(getAction(alt.getAttribute(ACTION_ATTR)));
                state.setPreAction(getAction(alt.getAttribute(PRE_ACTION_ATTR)));
                
                arr.add(state);
            }
        }
        
        AltState[] out = new AltState[arr.size()];
        for (int i=0;i<arr.size();i++)
        {
            out[i] = (AltState)arr.get(i);
        }
        return out;
    }
}
