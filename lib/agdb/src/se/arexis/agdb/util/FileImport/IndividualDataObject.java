/*
 * IndividualDataObject.java
 *
 * Created on May 5, 2003, 3:21 PM
 */

package se.arexis.agdb.util.FileImport;

import java.util.*;

/**
 *
 * @author  heto
 */
public class IndividualDataObject 
{

    private ArrayList ind   = new ArrayList();
        
    private ArrayList alias = new ArrayList();
        
    public void add(String iind, String ialias)
    {
        ind.add(iind);
        alias.add(ialias);
    }
    
    public String aliasToIdentity(String tmp_alias)
    {
        int pos = alias.indexOf(tmp_alias);
        
        return (String)ind.get(pos);
    }
    
    public int indexOf(String identity)
    {
        return ind.indexOf(identity);
    }
    
    public boolean isUnique(String identity)
    {
        int pos = ind.indexOf(identity);
        
        if (pos >= 0)
            return false;
        else
            return true;
    }
    
    
    /** Creates a new instance of IndividualDataObject */
    public IndividualDataObject() 
    {
    }
    
}
