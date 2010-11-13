/*
 * ParamCollector.java
 *
 * Created on October 6, 2005, 4:41 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.project;
import org.tgdb.frame.PageManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Class for collecting a number of params and their values
 * @author lami
 */
public class ParamCollector implements Serializable{
    private ArrayList reserved;
    private ArrayList defaultKeys;
    private ArrayList defaultValues;
    private ParamDataObject qdo;
    private boolean bypassSessionOnly;
    
    /**
     * Creates a new instance of ParamCollector
     * @param ignoreCase Parameter for case sensitive param name check
     */
    public ParamCollector(boolean ignoreCase) {
        qdo = new ParamDataObject(ignoreCase);
        reserved = new ArrayList();
        defaultKeys = new ArrayList();
        defaultValues = new ArrayList();
        bypassSessionOnly = false;
        
        // Add reserved words
        reserved.add("workflow");
        reserved.add("next");
        reserved.add("prev");
        reserved.add("first");
        reserved.add("last");
        reserved.add("reset");
        reserved.add("display");
        reserved.add("querytype");
        reserved.add("set");
        reserved.add("remove");
        reserved.add("save");
    }
    
    /**
     * Puts default values for params
     * @param key The param name
     * @param value The param value
     */
    public void putDefault(String key, String value) {
        defaultKeys.add(key);
        defaultValues.add(value);
    }
    
    public void bypassSessionOnly() {
        bypassSessionOnly = true;
    }
    
    /**
     * Collects all parameters except those that are reserved. The params are collected from the HttpServletrequest object.
     * @param req The HttpServletRequest object to collect params from
     * @return An object with the parameters used when defining conditions in SQL-queries.
     */
    public ParamDataObject collectParams(HttpServletRequest req, String actionName, PageManager pm) { 
        HttpSession se = req.getSession();
        boolean sessionOnly = false;
        String action = req.getParameter("collect."+actionName+".display");
        
        // Need to add a new reserved parameter...the button param
        reserved.add("collect."+actionName+".display");
        
//        if(action == null && !bypassSessionOnly)
//            sessionOnly = true;
        // Special case...reset button pressed
        if(req.getParameter("reset") != null) {
            return fillDefaults(se, actionName, pm);
        }
        
                        
        // Get all params and their values
        Enumeration names = req.getParameterNames();
        Map values = req.getParameterMap();
        
        Object key = null;
        String k;
        
        while(names.hasMoreElements() && values != null) {
            key = names.nextElement();
            
            // If the param name is not a reserved attribute
            if(!reserved.contains((String)key)) {  
                // Get the key...an array is returned from the get() since values for
                // a html-select can exist
                String[] tmp = (String[])values.get(key);
                
                if(!sessionOnly) {
                    // but we only want the first right now
                    if(tmp[0] != null) {
                        qdo.put((String)key, tmp[0]);
                        se.setAttribute("tmp."+actionName+"."+(String)key, tmp[0]);
                    }
                }
                
                // No parameter...use session
                else {
                    qdo.put((String)key, (String)se.getAttribute("tmp."+actionName+"."+(String)key));
                }
            }
        }       
        
        // Finally check if all default fields contain values
        checkDefaults(req, actionName);
        return qdo;
    }  
    
    // Removes session params that should no longer be used, i.e. all for the
    // specific action class
    private void cleanUpSession(HttpSession se, String actionName, PageManager pm) {
        Enumeration e = se.getAttributeNames();
        String name = "";

        pm.setFirst();
        while(e.hasMoreElements()) {
            name = (String)e.nextElement();
            if(name.startsWith("tmp."+actionName)) {
                se.removeAttribute(name);
            }            
        }
    }
    
    // Fills the qdo with default values only
    public ParamDataObject fillDefaults(HttpSession se, String actionName, PageManager pm) {
        cleanUpSession(se, actionName, pm);
        Iterator i = defaultKeys.iterator();
        Iterator j = defaultValues.iterator();
        
        String key = "";
        String value = "";
        while(i.hasNext()) {
            key = (String)i.next();
            value = (String)j.next();
            se.setAttribute("tmp."+actionName+"."+key, value);
            qdo.put(key, value);
        }         
        
        return qdo;
    }
    
    private void checkDefaults(HttpServletRequest req, String actionName) {
        // Get all inserted keys
        ArrayList keys = qdo.getKeys();
        Iterator i = defaultKeys.iterator();
        Iterator j = defaultValues.iterator();
        String key = "";
        String value = "";
        
        HttpSession se = req.getSession();
        
        while(i.hasNext()) {
            key = (String)i.next();
            value = (String)j.next();
            // Check if default keys are present
            if(!keys.contains(key)) {
                // If value is in session...insert it
                String tmpValue = (String)req.getSession().getAttribute("tmp."+actionName+"."+key);
                if(tmpValue != null && tmpValue.length() > 0)
                    qdo.put(key, tmpValue);
                // If not...insert default value
                else
                    qdo.put(key, value);
                se.setAttribute("tmp."+actionName+"."+key, qdo.getValue(key));
            }
        }                
    }
}
