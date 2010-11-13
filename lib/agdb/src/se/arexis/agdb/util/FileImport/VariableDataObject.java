/*
 * VariableDataObject.java
 *
 * $Log$
 * Revision 1.1  2004/02/25 13:54:35  heto
 * Added import of variable data file
 *
 *
 * Created on February 25, 2004, 10:33 AM
 */

package se.arexis.agdb.util.FileImport;

import java.util.*;
import se.arexis.agdb.util.*;

/** DataObject to store information about variables.
 * @author heto
 */
public class VariableDataObject
{
    /** Store the variables in memory */
    private ArrayList d_variable = new ArrayList();
    
    /** Creates a new instance of VariableDataObject */
    public VariableDataObject() 
    {
    }
    
    
    /** Check if the variable is unique in the DataObject
     * @param variable A String of the variables name
     * @return True if the variable is unique, false otherwise
     */    
    public boolean isVariableUnique(String variable)
    {
        int pos = d_variable.indexOf(variable);
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
    public int indexOf(String variable)
    {
        return d_variable.indexOf(variable);
    }
    
    /** Get the name of the variable in the DataObject given the index.
     * @param index The integer index
     * @return Returns the name of the variable
     */    
    public String getVariable(int index)
    {
        return (String)d_variable.get(index);
    }
    
    /** Add a new variable to the DataObject
     * @param variable The name of the variable
     */    
    public void add(String variable)
    {
        d_variable.add(variable);
    }
    
    /** Get the number of rows in the DataObject
     * @return The number of rows
     */    
    public int numOfRows()
    {
        return d_variable.size();
    }
   
    
}
