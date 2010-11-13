/*
 * ParamDataObject.java
 *
 * Created on October 6, 2005, 3:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package org.tgdb.project;

import java.io.Serializable;
import java.util.ArrayList;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttributes;

/**
 * Class for storing param names and values.
 * @author lami
 */
public class ParamDataObject implements Serializable {
    private BasicAttributes attrs;
    private boolean hasValues;
    
    /**
     * Creates a new instance of ParamDataObject
     * @param ignoreCase Set to false for case sensitivity
     */
    public ParamDataObject(boolean ignoreCase) {
        attrs = new BasicAttributes(ignoreCase);
        hasValues = false;
    }
    
    /**
     * Get a new ParamDataObject with only the keys specified. Other params
     * are not included in the new ParamDataObject. The Original ParamDataObject
     * is not changed.
     * @param pdo is the original PDO object
     * @param keys is the keys to be included in the new PDO
     * @return a new PDO object
     */
    public static ParamDataObject filter(ParamDataObject pdo, String[] keys) {
        ParamDataObject out = new ParamDataObject(true);
        for (int i=0;i<keys.length;i++) {
            out.put(keys[i], pdo.getValue(keys[i]));
        }
        return out;
    }
    
    /**
     * Returns true if the object holds any parameters
     * @return True if the object holds any params, false otherwise
     */
    public boolean hasValues() {
        return hasValues;
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
        if(key == null)
            return null;
        Attribute a = attrs.get(key); 
        String o = null;
        if(a == null)
            return null;
        try {
            o = (String)a.get();            
        }
        catch(NamingException ne) {
            ne.printStackTrace();
        }
        if(o == null || o.equalsIgnoreCase("null"))
            return null;
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
    public void remove(String key)
    {
        attrs.remove(key);
    }
    
    public String toString()
    {
        String out = "<param-data-object>\n";        
        ArrayList keys = getKeys();
        for (int i=0;i<keys.size();i++)
            out += "\t<param><key>"+keys.get(i)+"</key><value>"+getValue((String)keys.get(i))+"</value></param>\n";
        out += "</param-data-object>\n";
        return out;
    }
}
