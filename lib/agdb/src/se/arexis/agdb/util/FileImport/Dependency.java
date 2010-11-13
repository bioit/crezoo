/*
 * Dependency.java
 *
 * Created on April 5, 2005, 8:35 AM
 */

package se.arexis.agdb.util.FileImport;

/**
 *
 * @author heto
 */
public class Dependency {
    
    /** Creates a new instance of Dependency */
    public Dependency() 
    {
    }
    
    
    public String name;
    public String[] dep;
        
    public Dependency(String name, String[] dep)
    {
        this.name = name;
        this.dep = dep;
    }

    
}
