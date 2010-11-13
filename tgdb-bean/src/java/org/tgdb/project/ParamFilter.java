/*
 * ParamFilter.java
 *
 * Created on November 18, 2005, 11:04 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.project;

/**
 * Filter the ParamDataObject to only contain the specified keys.
 * @author heto
 */
public class ParamFilter {
    
    /**
     * Creates a new instance of ParamFilter 
     */
    public ParamFilter() {
    }
    
    
    /**
     * Get a new ParamDataObject with only the keys specified. Other params
     * are not included in the new ParamDataObject. The Original ParamDataObject
     * is not changed.
     * @param pdo is the original PDO object
     * @param keys is the keys to be included in the new PDO
     * @return a new PDO object
     */
    public ParamDataObject filter(ParamDataObject pdo, String[] keys)
    {
        ParamDataObject out = new ParamDataObject(true);
        for (int i=0;i<keys.length;i++)
        {
            out.put(keys[i], pdo.getValue(keys[i]));
        }
        return out;
    }
    
  
}
