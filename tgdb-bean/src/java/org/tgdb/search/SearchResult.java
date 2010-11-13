/*
 * SearchResult.java
 *
 * Created on January 24, 2006, 12:52 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.search;

import java.io.Serializable;

/**
 *
 * @author heto
 */
public abstract class SearchResult implements Serializable, Comparable {
    
    protected String workflow;
    protected String name;
    protected String comment;
    protected String type;
    protected String project;
    
    
    public String getWorkflow()
    {
        return workflow;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getComment()
    {
        return comment;
    }
    
    public String getType()
    {
        return type;
    }
    
    public String getProject()
    {
        return project;
    }
    
    public int compareTo(Object other)
    {
        return name.compareTo(((SearchResult)other).getName());
    }
    
    public boolean equals(Object other)
    {
        String tmp = workflow+name+comment;
        String tmp2 = ((SearchResult)other).getWorkflow()+
                ((SearchResult)other).getName()+
                ((SearchResult)other).getComment();
        return tmp.equals(tmp2);
    }
    
}
