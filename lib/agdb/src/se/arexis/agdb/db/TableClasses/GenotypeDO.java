/*
 * GenotypeDO.java
 *
 * Created on February 18, 2005, 1:59 PM
 */

package se.arexis.agdb.db.TableClasses;

/**
 *
 * @author heto
 */
public class GenotypeDO 
{
    private int mid;
    private int iid;
    private int aid1;
    private int aid2;
    private int suid;
    private int level;
    private String raw1;
    private String raw2;
    private String ref;
    private int id;
    private String ts;
    private String comm;
    
    
    /** Creates a new instance of GenotypeDO */
    public GenotypeDO(int mid, int iid, int aid1, int aid2,int suid, int level,String raw1,String raw2,String ref,int id, String ts, String comm)
    {
        this.mid = mid;
        this.iid = iid;
        this.aid1 = aid1;
        this.aid2 = aid2;
        this.suid = suid;
        this.level = level;
        this.raw1 = raw1;
        this.raw2 = raw2;
        this.ref = ref;
        this.id = id;
        this.ts = ts;
        this.comm = comm;
        
    }
    
    
    public int getMid()
    {
        return mid;
    }
    
    public int getIid()
    {
        return iid;
    }
    
    public int getAid1()
    {
        return aid1;
    }
    
    public int getAid2()
    {
        return aid2;
    }
    
    public int getSuid()
    {
        return suid;
    }
    
    public int getLevel()
    {
        return level;
    }
    
    public String getRaw1()
    {
        return raw1;
    }
    
    public String getRaw2()
    {
        return raw2;
    }
    
    public String getRef()
    {
        return ref;
    }
    
    public int getId()
    {
        return id;
    }
    
    public String getTs()
    {
        return ts;
    }
    
    public String getComment()
    {
        return comm;
    }
}
