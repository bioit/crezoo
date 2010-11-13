/*
 * VariableSetDataObject.java
 *
 * Created on April 6, 2004, 11:01 AM
 */

package se.arexis.agdb.util.FileImport;

import java.util.*;
import se.arexis.agdb.util.*;

/**
 *
 * @author  heto
 */
public class VariableSetDataObject 
{
    
    private ArrayList d_variable_set = new ArrayList();
    
    /** Creates a new instance of VariableSetDataObject */
    public VariableSetDataObject() 
    {
    }
    
    /** Check if the variable is unique in the DataObject
     * @param variable A String of the variables name
     * @return True if the variable is unique, false otherwise
     */    
    public boolean isVariableSetUnique(String variableset)
    {
        int pos = d_variable_set.indexOf(variableset);
        boolean res = false;
        
        if (pos >= 0)
            return false;
        else
            return true;
    }
    
    /** Get the index value of the variable
     * @param variable The name of the variable (String)
     * @return Return the index value
     */    
    public int indexOf(String variableset)
    {
        return d_variable_set.indexOf(variableset);
    }
    
    /** Add a new variable to the DataObject
     * @param variable The name of the variable
     */    
    public void add(String variableset)
    {
        d_variable_set.add(variableset);
    }
}
