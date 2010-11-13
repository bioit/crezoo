/*
 * SamplingUnit.java
 *
 * Created on December 13, 2004, 12:19 PM
 */

package se.arexis.agdb.db.TableClasses;

/**
 *
 * @author heto
 */
public class SamplingUnit 
{
    private int suid;
    private String name;
    
    /** Creates a new instance of SamplingUnit */
    public SamplingUnit(int suid, String name) 
    {
        this.suid=suid;
        this.name=name;
    }
    
    
    public void suid(int suid)
    {
        this.suid = suid;
    }
    
    public void name(String name)
    {
        this.name = name;
    }
    
    
    public int suid()
    {
        return suid;
    }
    
    public String name()
    {
        return name;
    }
    
    
}
