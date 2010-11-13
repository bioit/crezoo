/*
 * MarkerDO.java
 *
 * Created on February 18, 2005, 10:50 AM
 */

package se.arexis.agdb.db.TableClasses;

/**
 *
 * @author heto
 */
public class MarkerDO 
{
    int mid;
    String name;
    String alias;
    float position;
    String primer1;
    String primer2;
    String comment;
    int cid;
    
    
    /** Creates a new instance of MarkerDO */
    public MarkerDO(int mid, String name, String alias, float position, String prim1, String prim2, String comment,int cid) 
    {
        this.mid = mid;
        this.name = name;
        this.alias = alias;
        this.position = position;
        this.primer1 = prim1;
        this.primer2 = prim2;
        this.comment = comment;
        this.cid = cid;
    }
    
    public int getMID()
    {
        return mid;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getAlias()
    {
        return alias;
    }
    
    public float getPosition()
    {
        return position;
    }
    
    public String getPrimer1()
    {
        return primer1;
    }
    
    public String getPrimer2()
    {
        return primer2;
    }
    
    public String getComment()
    {
        return comment;
    }
    
    public int getCID()
    {
        return cid;
    }
}
