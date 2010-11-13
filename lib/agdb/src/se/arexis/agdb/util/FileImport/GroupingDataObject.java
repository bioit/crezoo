/*
 * GroupingDataObject.java
 *
 * Created on April 1, 2004, 2:16 PM
 */

package se.arexis.agdb.util.FileImport;

import java.util.*;
import se.arexis.agdb.util.*;

/**
 *
 * @author  heto
 */
public class GroupingDataObject 
{
    
    public ArrayList d_identity = new ArrayList();
    
    public ArrayList d_group = new ArrayList();
    
    public ArrayList d_grouping = new ArrayList();
    
    
    
    /** Creates a new instance of GroupingDataObject */
    public GroupingDataObject() 
    {
    }
    
    public void add(String identity, String group, String grouping)
    {
        d_identity.add(identity);
        d_group.add(group);
        d_grouping.add(grouping);
    }
    
     public String getIdentity(int index)
    {
        return (String)d_identity.get(index);
    }
    
    public String getGroup(int index)
    {
        return (String)d_group.get(index);
    }
   
    public String getGrouping(int index)
    {
        return (String)d_grouping.get(index);
    }
    
    public int getIndexOf(String identity, String group, String grouping)
    {
        Errors.logInfo("GroupingDataObject.getIndexOf("+identity+","+group+","+grouping+")");
        int res = -1;
        
        ArrayList set1 = new ArrayList();
        for (int i=0;i<d_identity.size();i++)
        {
            if (identity.equals(d_identity.get(i)))
            {
                set1.add(new Integer(i));
            }
        }
        
        ArrayList set2 = new ArrayList();
        for (int i=0;i<d_group.size();i++)
        {
            if (group.equals(d_group.get(i)))
            {
                set2.add(new Integer(i));
            }
        }
        
        ArrayList set3 = new ArrayList();
        for (int i=0;i<d_grouping.size();i++)
        {
            if (grouping.equals(d_grouping.get(i)))
            {
                set3.add(new Integer(i));
            }
        }
        
        ArrayList intersect = new ArrayList(set1);
        intersect.retainAll(set2);
        intersect.retainAll(set3);
        
        Errors.logDebug("intersect="+intersect.toArray());
        
        
        if (intersect.size()>0)
        {
            Integer tmp = (Integer)intersect.get(0);
            res = tmp.intValue();
        }
        else
        {
            res = -1;
        }   
        return res;
    }
}
