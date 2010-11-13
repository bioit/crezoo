/*
 * AlterWorkflowException.java
 *
 * Created on February 1, 2006, 8:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.frame.advanced;
import org.tgdb.frame.ActionException;

/**
 *
 * @author heto
 */
public class AlterWorkflowException extends ActionException {
    
    private String name;
    
    /**
     * Creates a new instance of AlterWorkflowException
     * @param msg is a message
     * @param name is the name to match in workflow.xml
     */
    public AlterWorkflowException(String name) {
        super("AlterWorkflow name="+name);
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
}
