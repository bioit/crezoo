package org.tgdb.project.role;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.NotImplementedException;
import org.tgdb.project.AbstractTgDbBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.*;
import org.tgdb.project.privilege.PrivilegeRemote;
import org.tgdb.project.privilege.PrivilegeRemoteHome;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.servicelocator.ServiceLocator;

/**
 * This is the bean class for the RoleBean enterprise bean.
 * Created May 23, 2005 9:11:27 AM
 * @author heto
 */
public class RoleBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.project.role.RoleRemoteBusiness {
    private javax.ejb.EntityContext context;
    
    private boolean dirty;
    
    private int rid;
    private int pid;
    private String name;
    private String comm;
    
    PrivilegeRemoteHome privilegeHome;
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise beans, Web services)
    // TODO Add business methods
    // TODO Add create methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
        privilegeHome = (PrivilegeRemoteHome)locator.getHome(ServiceLocator.Services.PRIVILEGE);
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
    public void ejbRemove() {
        makeConnection();
        Statement stmt = null;
        String sql = "";
        try {
            // Delete from R_PRJ_RID (on delete cascade pid, rid)
            sql = "delete from roles_ where rid="+rid;
            stmt = conn.createStatement();
            stmt.execute(sql);
            
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new EJBException("RoleBean#ejbRemove: Failed to delete role\n" +
                    sqle.getMessage());
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
    public void ejbLoad() 
    {
        makeConnection();
        PreparedStatement ps = null;
        Integer pk = (Integer)context.getPrimaryKey();
        try
        {
            ps = conn.prepareStatement("select rid,pid,name,comm from roles_ where rid = ?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next())
            {
                rid = rs.getInt("rid");
                pid = rs.getInt("pid");
                name = rs.getString("name");
                comm = rs.getString("comm");
                dirty = false;
            }
            else
                throw new EJBException("RoleBean#ejbLoad: load failed of "+pk);
            
        }
        catch (Exception e)
        {
            throw new EJBException("RoleBean#ejbLoad: ejbLoad Exception: "+e);
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
                ps = conn.prepareStatement("update roles_ set name=?, comm=? where rid = ?");
                ps.setString(1, name);
                ps.setString(2, comm);
                ps.setInt(3, rid);
                ps.execute();
            } 
            catch (SQLException sqle) 
            {
                sqle.printStackTrace(System.err);

                throw new EJBException("RoleBean#ejbStore: Failed to update role \n" +
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
            ps = conn.prepareStatement("select rid from roles_ where rid = ?");
            ps.setInt(1,aKey.intValue());
            result = ps.executeQuery();
            
            if (!result.next())
            {
                throw new ObjectNotFoundException("RoleBean#ejbFindByPrimaryKey: Cannot find project."+aKey.intValue());
            }
        }
        catch (SQLException se)
        {
            throw new FinderException("RoleBean#ejbFindByPrimaryKey: failed to find role. \n"+se);
        }
        finally
        {
            releaseConnection();
        }
        return aKey;
    }

    /**
     * Returns the role id
     * @return The role id
     */
    public int getRid() {
        return rid;
    }

    /**
     * Sets the role id
     * @param rid The role id
     */
    public void setRid(int rid) {
        this.rid = rid;
        dirty=true;
    }

    /**
     * Returns the project id
     * @return The project id
     */
    public int getPid() {
        return pid;
    }

    /**
     * Sets the project id
     * @param pid The project id
     */
    public void setPid(int pid) {
        this.pid = pid;
        dirty=true;
    }

    /**
     * Returns the name of the role
     * @return The name of the role
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the role
     * @param name The name of the role
     */
    public void setName(String name) {
        this.name = name;
        dirty=true;
    }

    /**
     * Returns the comment for the role
     * @return The comment
     */
    public String getComm() {
        return comm;
    }

    /**
     * Sets the comment for the role
     * @param comm The comment
     */
    public void setComm(String comm) {
        this.comm = comm;
        dirty=true;
    }

    /**
     * 
     * @param rid 
     * @param project 
     * @param name 
     * @param comm 
     * @throws javax.ejb.CreateException 
     * @return 
     */
    public java.lang.Integer ejbCreate(int rid, ProjectRemote project, java.lang.String name, java.lang.String comm) throws javax.ejb.CreateException {
        makeConnection();
        
        PreparedStatement ps = null;
        //String sql = "";
        //int rid = 0;
        try {
            
            this.rid=rid;
            this.pid=project.getPid();
            this.name=name;
            this.comm=comm;
            
            ps = conn.prepareStatement("insert into Roles_ (rid,pid,name,comm) values(?,?,?,?)");
            ps.setInt(1, rid);
            ps.setInt(2, project.getPid());
            ps.setString(3, name);
            ps.setString(4, comm);
            ps.execute();
            dirty = false;
        } 
        catch (Exception sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new CreateException("RoleBean#ejbCreate: Failed to create role \n" +
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
        }
        return new Integer(this.rid);
    }

    /**
     * 
     * @param rid 
     * @param project 
     * @param name 
     * @param comm 
     * @throws javax.ejb.CreateException 
     */
    public void ejbPostCreate(int rid, ProjectRemote project, java.lang.String name, java.lang.String comm) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }

    /**
     * Sets the privileges for the role
     * @param privileges The privileges
     * @throws org.tgdb.exceptions.ApplicationException If the privileges could not be set
     */
    public void setPrivileges(Collection privileges) throws ApplicationException
    {
        throw new NotImplementedException("RoleBean#setPrivileges: Not implemented");
    }

    /**
     * Returns the privileges for the role
     * @return The privileges for the role
     */
    public Collection getPrivileges() 
    {
        PreparedStatement ps = null;
        Collection privs = new ArrayList();
        PrivilegeRemote priv = null;
        
        makeConnection();
        try
        {
            ps = conn.prepareStatement("select prid from r_rol_pri where rid = ?");
            ps.setInt(1, rid);
            ResultSet rs = ps.executeQuery();
            int pk = 0;
            while (rs.next())
            {
                pk = rs.getInt("prid");
                priv = privilegeHome.findByPrimaryKey(new Integer(pk));
                privs.add(priv);
            }
        }
        catch (Exception e)
        {
            throw new EJBException("RoleBean#getPrivileges: Error getting privileges.\n"+e.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return privs;
    }

    /**
     * Adds a privilege to the role
     * @param privilege The privilege to add
     */
    public void addPrivilege(PrivilegeRemote privilege) 
    {
        makeConnection();
        PreparedStatement ps = null;
        try 
        {
            ps = conn.prepareStatement("insert into r_rol_pri (rid,prid) values(?,?)");
            ps.setInt(1, rid);
            ps.setInt(2, privilege.getPrid());
            ps.execute();
        } 
        catch (Exception sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new EJBException("RoleBean#addPrivilege: Failed to add privilege\n" +
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
        }
    }
    
    /**
     * Removes a privilege from the role
     * @param privilege The privilege to remove
     */
    public void removePrivilege(PrivilegeRemote privilege) 
    {
        makeConnection();
        PreparedStatement ps = null;
        try 
        {
            ps = conn.prepareStatement("delete from r_rol_pri where rid=? and prid=?");
            ps.setInt(1, rid);
            ps.setInt(2, privilege.getPrid());
            ps.execute();
        } 
        catch (Exception sqle) 
        {
            sqle.printStackTrace(System.err);
            
            throw new EJBException("RoleBean#removePrivilege: Failed to remove privilege\n" +
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
        }
    }

    /**
     * Finds a role using the project id
     * @param pid The project id
     * @throws javax.ejb.FinderException If the role could not be found
     * @return The role
     */
    public java.util.Collection ejbFindByProject(int pid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try
        {
            ps = conn.prepareStatement("select rid from roles_ where pid = ?");
            ps.setInt(1, pid);
            result = ps.executeQuery();
            
            while (result.next())
            {
                arr.add(new Integer(result.getInt("rid")));
            }
        }
        catch (SQLException se)
        {
            throw new FinderException("RoleBean#ejbFindByProject: failed to find role. \n"+se);
        }
        finally
        {
            releaseConnection();
        }
        return arr;
    }
}
