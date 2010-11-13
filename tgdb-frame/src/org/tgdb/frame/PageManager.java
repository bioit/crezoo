/*
 * PageManager.java
 *
 * Created on July 27, 2005, 1:03 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.frame;
import java.io.Serializable;

/**
 * Class for handling display and navigation for pages of data
 * @author heto
 */
public class PageManager implements Serializable {
   
    private int start;
    private int delta;
    private int max;
    private int viewed;
    
    private boolean last;
    
    
    /** Creates a new instance of PageManager */
    public PageManager() 
    {
        start=1;
        delta=20;
        max = -1;
    }

    /**
     * Returns the number of what to display
     * @return The start number
     */
    public int getStart() {
        if(start > max) {
            setFirst();
        }
        return start;
    }

    /**
     * Returns the value of how much to jump when browsing pages
     * @return The value of how many objects to jump when browsing pages
     */
    public int getDelta() {
        return delta;
    }

    /**
     * Sets the value of how many objects to jump when browsing pages
     * @param delta The value stating how many objects to jump when browsing pages
     */
    public void setDelta(int delta) {
        this.delta = delta;
    }

    /**
     * Returns the maximum number of objects to display
     * @return The maximum number of objects, i.e. the size of the collection
     */
    public int getMax() {
        return max;
    }

    /**
     * Sets the maximum number of objects to display
     * @param max The maximum number of objects to display
     */
    public void setMax(int max) {
        this.max = max;
        
        if (last)
        {
            int from = max % delta;
            //start = max - from + 1;
            if(from != 0){
                start = max - from + 1;
            }
            else{
                start = max - delta + 1;
            }
        }
    }
    
    /**
     * Returns the stop value, i.e. the max index to use in the collection of objects to display
     * @return The stop value
     */
    public int getStop() {
        return start+delta-1;
    }
    
    /**
     * Sets the next interval to display
     */
    public void setNext()
    {
        if(start + delta < max) {
            start += delta;
            if(start + delta < max) {
                last = false;
            } else {
                last = true;
            }
        }
    }
    
    /**
     * Sets the previous interval to display
     */
    public void setPrev()
    {
        if (start-delta>0)
        {
            start -= delta;
            last = false;
        }
    }
    
    /**
     * Sets the first interval to display
     */
    public void setFirst()
    {
        start = 1;
        last = false;
    }
    
    /**
     * Sets the last interval to display
     */
    public void setLast()
    {
        /*
        if (max==-1)
            throw new ApplicationException("Last button unsupported if setMax is not called. ");
          */  
        int from = max % delta;
        if(from != 0){
            start = max - from + 1;
        }
        else{
            start = max - delta + 1;
        }
        
        last = true;
        
    }
    
    /**
     * Returns if the current interval is the last to display
     * @return If the interval is the last to display
     */
    public boolean isLast()
    {
        return last;
    }
    
    public int getViewed()
    {
        if (last){
           return max-start+1;
        }else{
            if(start==1 && max < delta){
                return max;
            }else{
                return delta; 
            }
        }
    }
}
