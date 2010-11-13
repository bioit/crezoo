/*
 * ArgumentList.java
 *
 * Created on November 10, 2005, 12:51 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author heto
 */
public class ArgumentList {
    
    private HashMap props;
    
    private ArrayList option;
    
    /** Creates a new instance of ArgumentList */
    public ArgumentList(String args[]) {
        
        props = new HashMap();
        option = new ArrayList();
        
        for (int i=0;i<args.length;i++)
        {
            String arg = args[i];
            if (i+1<args.length && arg.startsWith("-") && !args[i+1].startsWith("-"))
            {
                props.put(arg.substring(1), args[i+1]);
                i++;
            }
            else if (arg.startsWith("-"))
            {
                option.add(arg.substring(1));
            }
        }
    }
    
    public String getValue(String name)
    {
        return (String)props.get(name);
    }
        
    public boolean isOption(String name)    
    {
        if (option.contains(name))
            return true;
        else
            return false;
    }
    
    
}
