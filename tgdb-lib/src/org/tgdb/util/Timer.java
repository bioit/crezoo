/*
 * Timer.java
 *
 * Created on October 31, 2005, 9:40 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.util;

/**
 *
 * @author heto
 */
public class Timer {
    
    long t1;
    long t2;
    
    /** Creates a new instance of Timer */
    public Timer() {
        t1 = System.currentTimeMillis();
    }
    
    public void stop()
    {
        t2 = System.currentTimeMillis();
    }
    
    public String toString()
    {
        return "Time="+(t2-t1);
    }
}
