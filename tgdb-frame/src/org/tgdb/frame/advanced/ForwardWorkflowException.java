/*
 * ForwardWorkflowException.java
 *
 * Created on February 1, 2006, 8:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.frame.advanced;

import org.tgdb.frame.WorkflowException;

/**
 *
 * @author heto
 */
public class ForwardWorkflowException extends WorkflowException {
    
    /**
     * Creates a new instance of ForwardWorkflowException
     * @param msg is a message
     * @param wfName is the new workflow name that should be currentWorkflow
     */
    public ForwardWorkflowException(String msg, String wfName) {
        super(msg);
    }
    
}
