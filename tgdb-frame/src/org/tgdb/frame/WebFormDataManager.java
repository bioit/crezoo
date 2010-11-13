/*
 * WebFormDataManager.java
 *
 * Created on November 24, 2005, 8:13 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.frame;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.form.*;



/**
 * Class for storing param names and values.
 * @author lami
 */
public class WebFormDataManager extends FormDataManager implements Serializable {    
    /**
     * Creates a new instance of WebFormDataManager
     * @param ignoreCase Set to false for case sensitivity
     */
    public WebFormDataManager(boolean ignoreCase) {
        super(ignoreCase);
    }
          
    /**
     * Collects all parameters except those that are reserved. The params are collected from the HttpServletrequest object.
     * @param req The HttpServletRequest object to collect params from
     * @return An object with the parameters used when defining conditions in SQL-queries.
     */
    public void collectParams(Object o) {             
        HttpServletRequest req = (HttpServletRequest)o;
        // Get all params and their values
        Enumeration names = req.getParameterNames();
        Map values = req.getParameterMap();
        
        Object key = null;
        String k;
        
        while(names.hasMoreElements() && values != null) {
            key = names.nextElement();
            
            // Get the key...an array is returned from the get() since values for
            // a html-select can exist
            String[] tmp = (String[])values.get(key);

            // but we only want the first right now
            if(tmp[0] != null) {
                put((String)key, tmp[0]);
            }
        }       
        
        // Finally check if all default fields contain values
        checkDefaults();
    }   
}
