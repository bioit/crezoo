/*
 * LAlleleDO.java
 *
 * Created on February 24, 2005, 8:29 AM
 */

package se.arexis.agdb.db.TableClasses;

/**
 *
 * @author heto
 */
public class LAlleleDO extends AlleleDO
{
    private int lmid;
    
    /** Creates a new instance of LAlleleDO */
    public LAlleleDO(int aid, String name, String comment, int lmid) 
    {
        this.aid = aid;
        this.name = name;
        this.comment = comment;
        this.lmid = lmid;
    }
    
    public int getLMID()
    {
        return lmid;
    }
    
}
