/*
 * SampleDateObject.java
 *
 * Created on den 1 april 2004, 15:53
 */

package se.arexis.agdb.util.FileImport;

import java.util.*;

/**
 *
 * @author  wali
 */
public class SampleDataObject 
{
    private ArrayList samples   = new ArrayList();
    
    private ArrayList identities   = new ArrayList();
 
    public void add(String iind, String sampleName)
    {
        identities.add(iind);
        samples.add(sampleName);
    }
    
    
    public int indexOf(String sampleName, String iden)
    {
        ArrayList set1 = new ArrayList();
        for (int i=0;i<samples.size();i++)
        {
            if (sampleName.equals(samples.get(i)))
            {
                set1.add(new Integer(i));
            }
        }
        //System.out.println("set1="+set1);
        
        ArrayList set2 = new ArrayList();
        for (int i=0;i<identities.size();i++)
        {
            if (iden.equals(identities.get(i)))
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
    
    public boolean isUnique(String iden, String sampleName)
    { 
        boolean res = true;
        int pos = indexOf(sampleName, iden);
        
        if (pos >= 0)
            res = false;
        else
            res = true;
        return res;
    }
    
    
    
    /** Creates a new instance of SampleDateObject */
    public SampleDataObject() {
    }
    
}
