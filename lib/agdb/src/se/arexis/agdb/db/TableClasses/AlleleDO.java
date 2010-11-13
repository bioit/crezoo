/*
 * AlleleDO.java
 *
 * Created on February 18, 2005, 10:16 AM
 */

package se.arexis.agdb.db.TableClasses;

/**
 *
 * @author heto
 */
public class AlleleDO 
{
    
    protected int aid;
    protected String name;
    protected String comment;
    
    /*
     * Don't use this.
     */
    public AlleleDO()
    {
        
    }
    
    /** Creates a new instance of AlleleDO */
    public AlleleDO(int aid, String name, String comment) 
    {
        this.aid = aid;
        this.name = name;
        this.comment = comment;
    }
    
    public int getAID()
    {
        return aid;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getComment()
    {
        return comment;
    }
}
