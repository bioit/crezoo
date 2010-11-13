/*
 * ChromosomeDO.java
 *
 * Created on February 18, 2005, 9:21 AM
 */

package se.arexis.agdb.db.TableClasses;

/**
 *
 * @author heto
 */
public class ChromosomeDO 
{
    int cid;
    private String name;
    private String comment;
    int sid;
    
    /** Creates a new instance of ChromosomeDO */
    public ChromosomeDO(int cid, String name, String comment, int sid) 
    {
        this.cid = cid;
        this.name = name;
        this.comment = comment;
        this.sid = sid;
    }
    
    
    public int getCID()
    {
        return cid;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getComment()
    {
        return comment;
    }
    
    public int getSID()
    {
        return sid;
    }
    
}
