/*
 * PhenotypeDataObject.java
 *
 * Created on May 5, 2003, 4:36 PM
 */

package se.arexis.agdb.util.FileImport;

import java.util.*;


/** Store the genotype data for check
 * @author heto
 */
public class PhenotypeDataObject 
{
    /** An arraylist of the variables */    
    private ArrayList d_varname = new ArrayList();
    /** An arraylist of identities */    
    private ArrayList d_identity = new ArrayList();
    
    /** Creates a new instance of PhenotypeDataObject */
    public PhenotypeDataObject() 
    {
    }
    
    /** Add a new Phenotype to TestObjects
     * @param varname The string of the variable
     * @param identity The string of the individuals identity (name)
     */    
    public void add(String varname, String identity)
    {
        d_varname.add(varname);
        d_identity.add(identity);
    }
    
    /** Get the index value of the given phenotype
     * @param varname The variable name
     * @param identity The individuals name
     * @return Returns the individuals index number in the testObjects
     */    
    public int getIndexOf(String varname, String identity)
    {
        ArrayList set1 = new ArrayList();
        for (int i=0;i<d_varname.size();i++)
        {
            if (varname.equals(d_varname.get(i)))
            {
                set1.add(new Integer(i));
            }
        }
        //System.out.println("set1="+set1);
        
        ArrayList set2 = new ArrayList();
        for (int i=0;i<d_identity.size();i++)
        {
            if (identity.equals(d_identity.get(i)))
            {
                set2.add(new Integer(i));
            }
        }
        //System.out.println("set2="+set2);
        
        // Create the intersection of the two sets.
        ArrayList intersect = new ArrayList(set1);
        intersect.retainAll(set2);
        //System.out.println("intersect:  " + intersect);

        if (intersect.size()>0)
        {
            Integer tmp = (Integer)intersect.get(0);
            return tmp.intValue();
        }
        else
        {
            return -1;
        }   
    }
    
    /**
     * Check if the Phenotype exists in the dataObjects
     * @param variable The variable name
     * @param identity The individuals name
     */
    public boolean isPhenotypeUnique(String variable, String identity)
    {
        boolean res = true;
        int pos = getIndexOf(variable, identity);
        if (pos >=0 )
            res = false;
        else
            res = true;
        return res;
    }
    
    /** Returns the variable from the dataobjects
     * @param index The index value
     * @return Returns the Variable name
     */    
    public String getVariable(int index)
    {
        return (String)d_varname.get(index);
    }
    
    /** Returns the identity from the dataobjects
     * @param index The index value
     * @return Returns the identity of the individual
     */    
    public String getIdentity(int index)
    {
        return (String)d_identity.get(index);
    }
    
}
