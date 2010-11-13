/*
 * testSortingAlgoritm.java
 *
 * Created on April 1, 2005, 4:28 PM
 */

package se.arexis.agdb.test;

import java.sql.Connection;
import java.sql.DriverManager;

import java.util.ArrayList;

/**
 *
 * @author heto
 */
public class testSortingAlgoritm 
{
    
    
    
    /** Creates a new instance of testSortingAlgoritm */
    public testSortingAlgoritm() 
    {
    }
    
    
    
    
    
    
    
    public static void main(String args[])
    {
        
        try
        {
            TestSort x = new TestSort();
            //x.testThis();
            x.runSort();
            
        
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
