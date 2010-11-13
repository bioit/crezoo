package org.tgdb.frame.advanced;

import java.util.ArrayList;

/**
 * A workflow history manager.
 * Handles the previous workflows that has been executed. Possible to
 * get back an earlier workflow.
 * @author heto
 */
public class WorkflowHistory {
    
    /**
     * The number of history objects saved.
     */
    private int MAX_HISTORY;
    /**
     * The array to store old workflows in.
     */
    private ArrayList history;
    
    /**
     * Creates a new instance of WorkflowHistory
     * @param MAX_HISTORY The number of old workflows saved
     */
    public WorkflowHistory(int MAX_HISTORY) {
        history = new ArrayList();
        this.MAX_HISTORY = MAX_HISTORY;
    }
    
    /**
     * Add a workflow to the history.
     * @param wfo The workflow object
     */
    public void add(Workflow wfo) {
        //if (!history.contains(wfo))
        if (wfo!=null)
        {
            history.add(wfo);
            
            // Remove the oldest object
            if (history.size()>MAX_HISTORY)
                history.remove(0);
        }
    }
    
    /**
     * Returns the history size
     */
    public int elements() {
        return history.size();
    }
    
    /**
     * Get a history object a number of steps back.
     * @param back the number of steps back in history to get a workflow from.
     * @return a workflow object
     */
    /* old get method
    public Workflow get(int back)
    {
        return (Workflow)history.get(history.size()-back);
    }
     */
    public Workflow get(int back) {
        if(history.size()==0 || back>history.size()){
            Workflow tmp = new Workflow("begin");
            return tmp;
        } else{
            return (Workflow)history.get(history.size()-back);
        }
    }
    
    public void remove(int back) {
        history.remove(history.size()-back-1);
    }
    
    /**
     * Return a string representation of the history list. 
     * This is notated in xml.
     * @return A string of history data.
     */
    public String toString() {
        String out = "";
        out += "<history>\n";
        for (int i=0;i<history.size();i++)
        {
            out += "\t<workflow id=\""+i+"\" name=\""+((Workflow)history.get(i)).getName()+"\"/>\n";
        }
        out += "</history>\n";
        return out;
    }
    
    /**
     * Removes a given workflow from the workflow history.
     * The workflow that is removed may cause problems during back navigation.
     */
    public void massRemove(String maliciousWorkflow){
        for (int i=0;i<history.size();i++){
            if(((Workflow)history.get(i)).getName().compareTo(maliciousWorkflow)==0){
                history.remove(i);
            }
        }
    }
    
}
