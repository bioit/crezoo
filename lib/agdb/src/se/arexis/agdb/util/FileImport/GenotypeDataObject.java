/*
 * GenotypeDataObject.java
 *
 * Created on May 5, 2003, 3:22 PM
 */

package se.arexis.agdb.util.FileImport;

import java.util.*;

/**
 *
 * @author  heto
 */
public class GenotypeDataObject 
{

    public ArrayList d_identity = new ArrayList();
    public ArrayList d_marker   = new ArrayList();
    public ArrayList d_a1       = new ArrayList();
    public ArrayList d_a2       = new ArrayList();
    
    
    /** Creates a new instance of GenotypeDataObject */
    public GenotypeDataObject() 
    {
    }
    
    public void add(String identity, String marker, String a1, String a2)
    {
        d_identity.add(identity);
        d_marker.add(marker);
        d_a1.add(a1);
        d_a2.add(a2);
    }
    
    public int getIndexOf(String identity, String marker)
    {
        ArrayList set1 = new ArrayList();
        for (int i=0;i<d_identity.size();i++)
        {
            if (identity.equals(d_identity.get(i)))
            {
                set1.add(new Integer(i));
            }
        }
        //System.out.println("set1="+set1);
        
        ArrayList set2 = new ArrayList();
        for (int i=0;i<d_marker.size();i++)
        {
            if (marker.equals(d_marker.get(i)))
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
    
    public String getIdentity(int index)
    {
        return (String)d_identity.get(index);
    }
    
    public String getMarker(int index)
    {
        return (String)d_marker.get(index);
    }
   
    public String getA1(int index)
    {
        return (String)d_a1.get(index);
    }
    
    public String getA2(int index)
    {
        return (String)d_a2.get(index);
    }
    
    /*
    public boolean isMarkerIndividualUnique(String marker, String identity)
    {
        int pos = d_mname.indexOf(marker);
        
        boolean res = false;
        
        if (pos > 0)
        {
            if (identity.equals((String)d_identity.get(pos)))
                res = false;
            else
                res = true;
        }
        else
            res = true;
        
        return res;
    }
     */

}
