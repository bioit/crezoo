/*
 * PhenotypeDO.java
 *
 * Created on March 3, 2005, 2:29 PM
 */

package se.arexis.agdb.db.TableClasses;

/**
 *
 * @author heto
 */
public class PhenotypeDO 
{
    
    int iid;
    int vid;
    String value;
    String type;
    String unit;
    String comment;
    
    /** Creates a new instance of PhenotypeDO */
    public PhenotypeDO(int iid, int vid, String value, String type, String unit, String comment) 
    {
        this.iid = iid;
        this.vid = vid;
        this.value = value;
        this.type = type;
        this.unit = unit;
        this.comment = comment;
    }
    
    public PhenotypeDO()
    {
    }
    
    public int getIID()
    {
        return iid;
    }
    
    public int getVID()
    {
        return vid;
    }
    
    public String getValue()
    {
        return value;
    }
    public String getUnit()
    {
        return unit;
    }
    public String getComment()
    {
        return comment;
    }
    
    public String getType()
    {
        return type;
    }
    
}
