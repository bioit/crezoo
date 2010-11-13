/*
 * IndividualDO.java
 *
 * Created on February 18, 2005, 11:04 AM
 */

package se.arexis.agdb.db.TableClasses;

import java.util.Date;

/**
 *
 * @author heto
 */
public class IndividualDO 
{
    
  
   private int iid;
   private String identity = null;
   private String alias = null;
   private int fatherId;
   private int motherId;
   private String sex;
   private Date birthDate = null;
   private String status = null;
   private int samplingUnitId;
   private int userId;
   private String timeStamp = null;
   private String comment = null;
   
   
    
    /** Creates a new instance of IndividualDO */
    public IndividualDO(int iid, String identity, String alias, 
            int fatherId, int motherId, String sex, Date birthDate,
            String status, int suid, int id, String ts, String comment)        
    {
        this.iid = iid;
        this.identity = identity;
        this.alias = alias;
        this.fatherId=fatherId;
        this.motherId=motherId;
        this.sex = sex;
        this.birthDate = birthDate;
        this.status = status;
        this.samplingUnitId = suid;
        this.userId = id;
        this.timeStamp = ts;
        this.comment = comment;
    }
    
    public int getIID()
    {
        return iid;
    }
    
    public String getIdentity()
    {
        return identity;
    }
    
    public String getAlias()
    {
        return alias;
    }
    
    public String getSex()
    {
        return sex;
    }
    
    public Date getBirthDate()
    {
        return birthDate;
    }
    
    public int getFather()
    {
        return fatherId;
    }
    
    public int getMother()
    {
        return motherId;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public int getSUID()
    {
        return samplingUnitId;
    }
    
    
    /**
     * Continue here when its needed!!
     */
            
    
}
