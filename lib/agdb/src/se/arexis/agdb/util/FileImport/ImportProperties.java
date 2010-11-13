/*
 * ImportProperties.java
 *
 * Created on March 17, 2004, 7:57 AM
 */

package se.arexis.agdb.util.FileImport;

/**
 *
 * @author  heto
 */
public class ImportProperties {
    
    /** Creates a new instance of ImportProperties */
    public ImportProperties() 
    {
    }
    
    public ImportProperties(String updateMethod,int suid, int level)
    {
        this.updateMethod=updateMethod;
        this.suid=suid;
        this.level=level;
    }
    
    public int suid;
    public String updateMethod;
    public int level;
}
