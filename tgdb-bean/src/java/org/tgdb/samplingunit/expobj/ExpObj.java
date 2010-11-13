package org.tgdb.samplingunit.expobj;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.user.UserRemote;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemote;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.*;

/**
 * This is the bean class for the ExpObj enterprise bean.
 * Created Nov 23, 2005 2:30:20 PM
 * @author heto
 */
public class ExpObj extends AbstractTgDbBean {
    private javax.ejb.EntityContext context;
    
    protected int eid;
    protected String identity;
    protected String alias;
    protected int suid;
    
    protected String status;
    protected String comm;
    protected java.sql.Date ts;
    protected int id;
    protected int type;
    
    protected boolean dirty;
    
//    private TgDbCaller _caller;
    
    private SamplingUnitRemoteHome samplingUnitHome;
    
    /**
     * Creates a new instance of ExpObj
     * @param eid The experimental object id
     * @param id The id of the creator
     * @param ts The date of the creation
     */
    public ExpObj(int eid, int id, java.sql.Date ts) {
        super();
        this.ts = ts;
        this.id = id;
        this.eid = eid;
        samplingUnitHome = (SamplingUnitRemoteHome)ServiceLocator.getInstance().getHome(ServiceLocator.Services.SAMPLINGUNIT);
    }
    
    /**
     * Creates a new instance of ExpObj
     */
    public ExpObj() { 
        super(); 
        samplingUnitHome = (SamplingUnitRemoteHome)ServiceLocator.getInstance().getHome(ServiceLocator.Services.SAMPLINGUNIT);
    }
    
    /**
     * Sets what type of object this is
     * @param type The type of object
     */
    public void setType(int type) {
        this.type = type;
    }
    
    /**
     * Returns what type of object this is
     * @return The type of object
     */
    public int getType() {
        return type;
    }
    
    /**
     * Remove the common object.
     */
    public void remove() throws Exception {
        PreparedStatement ps = null;
      
        ps = conn.prepareStatement("delete from expobj where eid=?");
        ps.setInt(1, eid);

        ps.execute();
    }
        
    /**
     * @see javax.ejb.EntityBean#ejbLoad()
     */
    public void load(int eid) throws Exception {

        PreparedStatement ps = null;
     
        ps = conn.prepareStatement("select eid,suid,identity,alias,ts,status,comm,id " +
                "from expobj where eid=?");
        ps.setInt(1, eid);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            this.eid = rs.getInt("eid");
            suid = rs.getInt("suid");
            identity = rs.getString("identity");
            alias = rs.getString("alias");

            ts = rs.getDate("ts");
            status = rs.getString("status");

            comm = rs.getString("comm");
            id = rs.getInt("id");
            
            dirty = false;

        } else
            throw new EJBException("ExpObjBean#load: Error loading ExpObj");
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void store() throws Exception {
        if (dirty)
        {
            PreparedStatement ps = null;

            ps = conn.prepareStatement("update expobj set identity=?,alias=?,comm=?,status=?,id=?,ts=? " +
                    "where eid=?");

            int i=0;
            ps.setString(++i, identity);
            ps.setString(++i, alias);
            ps.setString(++i, comm);
            ps.setString(++i, status);
            ps.setInt(++i, id);
            ps.setDate(++i, ts);
            ps.setInt(++i, eid);

            int rows = ps.executeUpdate();

            if (rows!=1) {
                throw new EJBException("ExpObjBean#store: Error saving ExpObj. Rows affected "+rows);
            }
        }
    }    
    
    /**
     * Creates a new experimental object
     * @param eid 
     * @param identity 
     * @param su 
     * @param caller 
     * @throws java.lang.Exception 
     */
    public void create(int eid, String identity, SamplingUnitRemote su, TgDbCaller caller) throws CreateException
    {
        try
        {
            PreparedStatement ps = null;

            ps = conn.prepareStatement("insert into expobj (eid,identity,suid,id,status,ts) values (?,?,?,?,'E',timestamp 'now') ");

            int i=0;
            ps.setInt(++i, eid);
            ps.setString(++i, identity);
            ps.setInt(++i, su.getSuid());
            ps.setInt(++i, caller.getId());
            
            

            int rows = ps.executeUpdate();
            dirty = false;

            if (rows!=1) {
                throw new CreateException("ExpObjBean#create: Error creating ExpObj. Rows affected "+rows);
            }
        } 
        catch (SQLException se)
        {
            if (se.getSQLState().equals("23503"))
                throw new DuplicateKeyException("Object already exists");
            else
                throw new CreateException("Cannot create object");
        }
        catch (CreateException ex)
        {
            //ex.printStackTrace();
            throw ex;
        }
        catch (Exception e)
        {
            throw new CreateException("Could not create ExpObj object.");
        }
    }
   

    /**
     * Returns the experimental object id
     * @return The id of the experimental object
     */
    public int getEid() {
        return eid;
    }
    
    /**
     * Returns the identity of the experimental object
     * @return The identity of the experimental object
     */
    public String getIdentity() {
        return identity;
    }
    
    /**
     * Sets the caller
     * @param caller The caller
     */
    public void setCaller(TgDbCaller caller) {
        this.caller = caller;
    }

    /**
     * Sets the identity of the experimental object
     * @param identity The identity of the experimental object
     */
    public void setIdentity(String identity) {
        this.identity = identity;
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());        
        dirty = true;
    }

    /**
     * Returns the alias of the experimental object
     * @return The alias of the experimental object
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias of the experimental object
     * @param alias The alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());        
        dirty = true;
    }

    /**
     * Returns the sampling unit id for the experimental object
     * @return The sampling unit id
     */
    public int getSuid() {
        return suid;
    }

    /**
     * Sets the sampling unit id for the experimental object
     * @param suid The sampling unit id
     */
    public void setSuid(int suid) {
        this.suid = suid;
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());        
        dirty = true;
    }

    /**
     * Returns the status of the experimental object
     * @return The status of the experimental object
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the experimental object
     * @param status The status of the experimental object
     */
    public void setStatus(String status) {
        this.status = status;
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());        
        dirty = true;
    }
    
    /**
     * Returns the user which made the last update on the allele
     * @return The user
     */
    public UserRemote getUser() {
        try {
            return userHome.findByPrimaryKey(new Integer(id));
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Returns the date for when the latest changes was made to the individual
     * @return The date for the last changes on the individual
     */
    public java.sql.Date getTs() {
        return ts;
    }
    
    /**
     * Returns the comment for the individual
     * @return The comment
     */
    public String getComm() {
        return comm;
    }
    
    /**
     * Sets the comment for the individual
     * @param comm The comment
     */
    public void setComm(String comm) {
        this.comm = comm;
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;
    }
    
    /**
     * Returns the sampling unit that the experimental object belongs to
     * @return The sampling unit that the experimental object belongs to
     */
    public SamplingUnitRemote getSamplingUnit() throws ApplicationException {
        SamplingUnitRemote sur = null;
        try {
            sur = samplingUnitHome.findByPrimaryKey(new Integer(getSuid()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("ExpObjBean#getSamplingUnit: Failed to get sampling unit for expobj with id="+getEid()+", identity="+getIdentity()+"\n"+e.getMessage()+", Suid="+getSuid(),e);
        }
        return sur;        
    }
}
