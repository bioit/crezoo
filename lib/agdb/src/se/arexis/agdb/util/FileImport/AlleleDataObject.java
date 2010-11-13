/*
 * AlleleDataObject.java
 *
 * Created on May 5, 2003, 4:09 PM
 */

package se.arexis.agdb.util.FileImport;

import java.util.*;



/**
 *
 * @author  heto
 */
public class AlleleDataObject 
{
    
    private ArrayList d_marker = new ArrayList();
    private ArrayList d_allele = new ArrayList();
    
    /** Creates a new instance of AlleleDataObject */
    public AlleleDataObject() 
    {
    }
    
    public boolean isMarkerUnique(String marker)
    {
        int pos = d_marker.indexOf(marker);
        boolean res = false;
        
        if (pos > 0)
            return false;
        else
            return true;
    }
    
    public boolean isMarkerAlleleUnique(String marker, String allele)
    {
        int pos = d_marker.indexOf(marker);
        
        boolean res = false;
        
        if (pos > 0)
        {
            if (allele.equals((String)d_allele.get(pos)))
                res = false;
            else
                res = true;
        }
        else
            res = true;
        
        return res;
    }
    
    public int indexOf(String marker)
    {
        return d_marker.indexOf(marker);
    } 
    
    public int indexOf(String allele, String marker)
    {
        ArrayList set1 = new ArrayList();
        for (int i=0;i<d_allele.size();i++)
        {
            if (allele.equals(d_allele.get(i)))
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
   
    public String getMarker(int index)
    {
        return (String)d_marker.get(index);
    }
    
    public String getAllele(int index)
    {
        return (String)d_allele.get(index);
    }
    
    public void add(String marker, String allele)
    {
        d_marker.add(marker);
        d_allele.add(allele);
    }
    
}
