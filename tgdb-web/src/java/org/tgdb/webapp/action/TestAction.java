/*
 * TestAction.java
 *
 * Created on July 11, 2005, 10:07 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.webapp.action;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author heto
 */
public class TestAction extends TgDbAction {
    
    /** Creates a new instance of TestAction */
    public TestAction() {
    }
    
    public String getName()
    {
        return "TestAction";
    }
    
    public Object perform(HttpServletRequest req)
    {
        
        return null;
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context)
    {
        System.err.println("TestAction has been performed");
        
        return true;
    }
}
