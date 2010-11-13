/*
 * FormDataManager.java
 *
 * Created on November 24, 2005, 12:34 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttributes;

/**
 * The FormDataManager is an object responsible for collecting, and maintaining form data (key/value). The FormDataManager can have a set of default keys/values to use.
 * @author lami
 */
public class FormDataManager implements Serializable {
    protected BasicAttributes attrs;
    protected boolean hasValues;
    protected ArrayList defaultKeys;
    protected ArrayList defaultValues;
    private boolean ignoreCase;
    
    /**
     * Creates a new instance of FormDataManager
     * @param ignoreCase Ignore case for parameters
     */
    public FormDataManager(boolean ignoreCase) {
        attrs = new BasicAttributes(ignoreCase);
        hasValues = false;
        defaultKeys = new ArrayList();
        defaultValues = new ArrayList();   
        this.ignoreCase = ignoreCase;
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
    
    /**
     * Returns true if the object holds any parameters
     * @return True if the object holds any params, false otherwise
     */
    public boolean hasValues() {
        return hasValues;
    }   
    
    /**
     * Resets this object to default keys only
     */
    public void reset() {
        attrs = new BasicAttributes(ignoreCase);        
    }
    
    /**
     * Get a new FormDataManager with only the keys specified. Other params
     * are not included in the new ParamDataObject. The Original ParamDataObject
     * is not changed.
     * @param pdo is the original PDO object
     * @param keys is the keys to be included in the new PDO
     * @return A new cleaned-up FormDataManager containing only the specified keys (if existing)
     */
    public static FormDataManager requestFormData(FormDataManager pdo, String[] keys) {                
        FormDataManager out = new FormDataManager(true);
        for (int i=0;i<keys.length;i++) {
            out.put(keys[i], pdo.getValue(keys[i]));
        }
        return out;
    }    
    
    /**
     * Fetches the parameter values
     * @return The parameter values
     */
    public ArrayList getValues() {
        NamingEnumeration enumeration = attrs.getAll();
        
        ArrayList tmp = new ArrayList();
        
        try {
            while(enumeration.hasMore())
                tmp.add(enumeration.next());
        } catch(NamingException ne) {
            ne.printStackTrace();
        }
        
        return tmp;
    }
    
    /**
     * Fetches a specific parameter value
     * @param key The name of the param to fetch
     * @return The requested param
     */
    public String getValue(String key) {
        // This makes sure that the FormData is at least
        // the default values...
        checkDefaults();
        
        if(key == null)
            return "";
        Attribute a = attrs.get(key); 
        String o = "";
        if(a == null)
            return "";
        try {
            o = (String)a.get();            
        }
        catch(NamingException ne) {
            ne.printStackTrace();
        }
        if(o == null || o.equalsIgnoreCase("null"))
            return "";
        else
            return o;
    }
    
    /**
     * Returns all parameter names
     * @return The parameter names
     */
    public ArrayList getKeys() {
        NamingEnumeration enumeration = attrs.getIDs();
        
        ArrayList tmp = new ArrayList();
        try {
            while(enumeration.hasMore()) {
                tmp.add((String)enumeration.next());
            }
        } catch(NamingException ne) {
            ne.printStackTrace();
        }    
        
        return tmp;
    }
    
    /**
     * Puts a new parameter name and value
     * @param key The param name
     * @param value The param value
     */
    public void put(String key, String value) {
        attrs.put(key, value);
        hasValues = true;
    }
    
    
    /**
     * Remove a parameter 
     * @param key is the key to remove
     */
    public void remove(String key) {
        attrs.remove(key);
    }
    
    /**
     * A string representation of the object describing the current state of the form parameter collection
     * @return A string representation of the state of the object
     */
    public String toString() {
        String out = "<form-data>\n";
        ArrayList keys = getKeys();
        for (int i=0;i<keys.size();i++)
            out += "\t<parameter><key>"+keys.get(i)+"</key><value>"+getValue((String)keys.get(i))+"</value></parameter>\n";
        out += "</form-data>\n";
        return out;
    }   
    
    
    /**
     * Makes sure that the minimum (default values) requirements are satisfied
     */
    protected void checkDefaults() {
        // Get all inserted keys
        ArrayList keys = getKeys();
        Iterator i = defaultKeys.iterator();
        Iterator j = defaultValues.iterator();
        String key = "";
        String value = "";
        
        while(i.hasNext()) {
            key = (String)i.next();
            value = (String)j.next();
            // Check if default keys are present
            if(!keys.contains(key)) {
                put(key, value);
            }
        }                
    }  
    
    /**
     * This method does nothing. Create a specialized FormDataManager and override this method. Use the put(key, value) method to insert parameters in the specialized collectParams(). Use the checkDefaults() to make sure that default values are used as minimum.
     * @param o The object to collect data from. A specialized FormDataManager can as example be a webform and then it might be suitable to pass along the request object as parameter.
     */
    protected void collectParams(Object o) {        
    }
}
