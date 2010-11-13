package org.tgdb.project.privilege;
import org.tgdb.project.AbstractTgDbBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.*;

/**
 * This is the bean class for the PrivilegeBean enterprise bean.
 * Created May 23, 2005 10:31:33 AM
 * @author heto
 */
public class PrivilegeBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.project.privilege.PrivilegeRemoteBusiness {
    private javax.ejb.EntityContext context;
    
    private boolean dirty;
    
    /**
     * The privilege id. This is the primary key
     */
    private int prid;
    /**
     * The name of the privilege
     */
    private String name;
    /**
     * A comment
     */
    private String comm;
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise beans, Web services)
    // TODO Add business methods
    // TODO Add create methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbActivate()
     */
    public void ejbActivate() {
        
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbPassivate()
     */
    public void ejbPassivate() {
        
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbRemove()
     */
    public void ejbRemove() throws RemoveException {
        makeConnection();
        Statement stmt = null;
        String sql = "";
        try {
            // Delete from R_PRJ_RID (on delete cascade pid, rid)
            sql = "delete from privileges_ where prid="+prid;
            stmt = conn.createStatement();
            stmt.execute(sql);
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new EJBException("PrivilegeBean#ejbRemove: Failed to delete role\n(" +
                    sqle.getMessage() + ")");
        } 
        finally 
        {    
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException ignored) {
            }
            releaseConnection();
        }
    }
    
    /**
     * @see javax.ejb.EntityBean#unsetEntityContext()
     */
    public void unsetEntityContext() {
        context = null;
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbLoad()
     */
    public void ejbLoad() {
        makeConnection();
        PreparedStatement ps = null;
        Integer pk = (Integer)context.getPrimaryKey();
        try
        {
            ps = conn.prepareStatement("select prid,name,comm from privileges_ where prid = ?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next())
            {
                prid = rs.getInt("prid");
                name = rs.getString("name");
                comm = rs.getString("comm");
                dirty = false;
            }
            else
                throw new EJBException("PrivilegeBean#ejbLoad: load failed. No result found in db");
        }
        catch (Exception e)
        {
           
            throw new EJBException("PrivilegeBean#ejbLoad: error loading privilege. \n"+e);
        }
        finally
        {
            releaseConnection();
        }
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() {
        if (dirty)
        {
            makeConnection();

            PreparedStatement ps = null;
            String sql = "";
            try {
                ps = conn.prepareStatement("update privileges_ set name=?, comm=? where prid = ?");
                ps.setString(1, name);
                ps.setString(2, comm);
                ps.setInt(3, prid);
                ps.execute();
            } 
            catch (SQLException sqle) 
            {
                sqle.printStackTrace(System.err);
                throw new EJBException("PrivilegeBean#ejbStore: Failed to update privilege\n" +
                        sqle.getMessage());
            } 
            finally 
            {
                try 
                {
                    if (ps != null) ps.close();
                } 
                catch (SQLException ignored) 
                {}
                releaseConnection();
                dirty=false;
            }
        }
    }
    
    // </editor-fold>
    
    /**
     * See EJB 2.0 and EJB 2.1 section 12.2.5
     */
    public java.lang.Integer ejbFindByPrimaryKey(java.lang.Integer aKey) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try
        {
            ps = conn.prepareStatement("select prid from privileges_ where prid = ?");
            ps.setInt(1,aKey.intValue());
            result = ps.executeQuery();
            
            if (!result.next())
            {
                throw new ObjectNotFoundException("PrivilegeBean#ejbFindByPrimaryKey: Cannot find privilege.");
            }
        }
        catch (SQLException se)
        {
            throw new FinderException("PrivilegeBean#ejbFindByPrimaryKey: SQL error: \n"+se.getMessage());
        }
        
        releaseConnection();
        return aKey;
    }

    /**
     * Get the privilege id
     * @return The privilege id
     */
    public int getPrid() {
        return prid;
    }

    /**
     * Get the privilege name
     * @return The privilege name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the privilege name
     * @param name The new name to this privilege
     */
    public void setName(String name) {
        this.name = name;
        dirty=true;    
    }

    /**
     * Get the comment for the privilege
     * @return The comment string
     */
    public String getComm() {
        return comm;
    }

    /**
     * Set the comment to a privilege
     * @param comm The comment to set
     */
    public void setComm(String comm) {
        this.comm = comm;
        dirty=true;
    }

    /**
     * 
     * @param prid 
     * @param name 
     * @param comm 
     * @throws javax.ejb.CreateException 
     * @return 
     */
    public java.lang.Integer ejbCreate(int prid, java.lang.String name, java.lang.String comm) throws javax.ejb.CreateException {
        this.prid = prid;
        this.name = name;
        this.comm = comm;
        
        
        makeConnection();
        PreparedStatement ps = null;
        try
        {
            ps = conn.prepareStatement("insert into privileges_ (prid,name,comm) values (?,?,?)");
            ps.setInt(1, prid);
            ps.setString(2, name);
            ps.setString(3, comm);
            ps.execute();
            dirty = false;
        }
        catch (Exception e)
        {
            throw new CreateException("PrivilegeBean#ejbCreate: Privilege could not be created. \n"+e.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return new Integer(this.prid);
    }

    /**
     * 
     * @param prid 
     * @param name 
     * @param comm 
     * @throws javax.ejb.CreateException 
     */
    public void ejbPostCreate(int prid, java.lang.String name, java.lang.String comm) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
        
    }

    /**
     * Finds all privileges
     * @throws javax.ejb.FinderException If the privileges could not be found
     * @return A collection of privilges
     */
    public java.util.Collection ejbFindAll() throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try
        {
            ps = conn.prepareStatement("select prid from privileges_ order by name");            
            result = ps.executeQuery();
            while (result.next())
            {
                arr.add(new Integer(result.getInt("prid")));
            }
        }
        catch (SQLException se)
        {
            throw new FinderException("PrivilegeBean#ejbFindAll: SQL error: \n"+se.getMessage());
        }
        
        releaseConnection();
        return arr;
    }
}
