/*
 * Species.java
 *
 * Created on December 13, 2004, 12:42 PM
 */

package se.arexis.agdb.db.TableClasses;

/**
 *
 * @author heto
 */
public class Species 
{

    private int speciesId;
    
    private String name;
            
    
    /** Creates a new instance of Species */
    public Species(int speciesId, String name) 
    {
        this.speciesId=speciesId;
        this.name=name;
    }
    
    public void speciesId(int speciesId)
    {
        this.speciesId=speciesId;
    }
    
    public void name(String name)
    {
        this.name=name;
    }
    
    public int speciesId()
    {
        return speciesId;
    }
    
    public String name()
    {
        return name;
    }
}
