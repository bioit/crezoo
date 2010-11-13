/*
 * AlleleDataObject.java
 *
 * Created on May 5, 2003, 4:09 PM
 */

package se.arexis.agdb.util.FileImport;

import java.util.*;
import se.arexis.agdb.util.*;



/**
 *
 * @author  heto
 */
public class MarkerDataObject 
{
    
    private ArrayList d_marker = new ArrayList();
    private ArrayList d_chromosome = new ArrayList();
    
    /** Creates a new instance of MakerDataObject */
    public MarkerDataObject() 
    {
    }
    
    public boolean isMarkerUnique(String marker)
    {
        int pos = d_marker.indexOf(marker);
        boolean res = false;
        
        if (pos >= 0)
            return false;
        else
            return true;
    }
    
    public boolean isMarkerUnique(String marker, String chromosome)
    {
        int pos = indexOf(marker,chromosome);
        boolean res = false;
        //Errors.logDebug("Pos="+Integer.toString(pos)+", Marker="+marker + ", chr="+chromosome);
        if (pos >= 0)
            res = false;
        else
            res = true;
        return res;
    }
    
    public int indexOf(String marker)
    {
        return d_marker.indexOf(marker);
    } 
    
    public int indexOf(String marker,String chromosome)
    {
        //System.err.println("M=:"+marker+":,C=:"+chromosome+":");
        ArrayList set1 = new ArrayList();
        for (int i=0;i<d_chromosome.size();i++)
        {
            //System.err.println("I="+i+"db.chr="+(String)d_chromosome.get(i));
            if (chromosome.equals((String)d_chromosome.get(i)))
            {
                set1.add(new Integer(i));
            }
        }
        //System.out.println("set1="+set1);
        
        ArrayList set2 = new ArrayList();
        for (int i=0;i<d_marker.size();i++)
        {
            if (marker.equals((String)d_marker.get(i)))
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
    
    public String getChromosome(int index)
    {
        return (String)d_chromosome.get(index);
    }
    
    public void add(String marker, String chromosome)
    {
        d_marker.add(marker);
        d_chromosome.add(chromosome);
    }
    
    public void add(String marker)
    {
        d_marker.add(marker);
    }
    
    public int numOfRows()
    {
        return d_marker.size();
    }
}