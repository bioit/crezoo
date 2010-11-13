package org.tgdb.project.securityprinciple;
import org.tgdb.project.AbstractTgDbBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.*;
import org.tgdb.project.user.UserRemote;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.project.role.RoleRemote;
import org.tgdb.project.role.RoleRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This is the bean class for the SecurityPrincipleBean enterprise bean.
 * Created May 23, 2005 12:10:37 PM
 * 
 * A Security Principle is composed of project, user and a role.
 * @author heto
 */
public class SecurityPrincipleBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.project.securityprinciple.SecurityPrincipleRemoteBusiness {
    private javax.ejb.EntityContext context;
    
    /**
     * The project id
     */
    private int pid;
    /**
     * the user id
     */
    private int id;
    /**
     * the role id
     */
    private int rid;
    
    private boolean dirty;
    
    private RoleRemoteHome roleHome;
    private ProjectRemoteHome projectHome;
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise beans, Web services)
    // TODO Add business methods
    // TODO Add create methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
        roleHome = (RoleRemoteHome)locator.getHome(ServiceLocator.Services.ROLE);
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);
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
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try
        {
            ps = conn.prepareStatement("delete from r_prj_rol where pid = ? and id=? and rid=?");
            ps.setInt(1, getPid());
            ps.setInt(2, getId());
            ps.setInt(3, getRid());
            
            //ps.execute();
            
            int res = ps.executeUpdate();
            
            if (res!=1)
            {
                throw new EJBException("SecurityPrincipleBean#ejbRemove: Cannot remove security Principle. Db returned error status -1");
            }
        }
        catch (SQLException se)
        {
            throw new EJBException("SecurityPrincipleBean#ejbRemove: Cannot remove security Principle. \n"+se.getMessage());
        }
        finally
        {
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
        SecurityPrinciplePk pk = (SecurityPrinciplePk)context.getPrimaryKey();
        
        setPid(pk.getPid().intValue());
        setId(pk.getId().intValue());
        setRid(pk.getRid().intValue());
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbStore()
     *
     * 
     */
    public void ejbStore() {      
    }
    
    // </editor-fold>
    
    /**
     * See EJB 2.0 and EJB 2.1 section 12.2.5
     */
    public org.tgdb.project.securityprinciple.SecurityPrinciplePk ejbFindByPrimaryKey(org.tgdb.project.securityprinciple.SecurityPrinciplePk pk) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try
        {
            ps = conn.prepareStatement("select pid from r_prj_rol where pid = ? and id=? and rid=?");
            ps.setInt(1, pk.getPid().intValue());
            ps.setInt(2, pk.getId().intValue());
            ps.setInt(3, pk.getRid().intValue());
            
            result = ps.executeQuery();
            
            if (!result.next())
            {
                throw new ObjectNotFoundException("SecurityPrincipleBean#ejbFindByPrimaryKey: Cannot find security Principle");
            }
        }
        catch (SQLException se)
        {
            throw new FinderException("SecurityPrincipleBean#ejbRemove: Cannot find security principle. \n"+se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return pk;
    }

    /**
     * 
     * @param project 
     * @param user 
     * @param role 
     * @throws javax.ejb.CreateException 
     * @return 
     */
    public SecurityPrinciplePk ejbCreate(ProjectRemote project, UserRemote user, RoleRemote role) throws javax.ejb.CreateException {
        makeConnection();
        PreparedStatement ps = null;
        SecurityPrinciplePk pk = null;
        try
        {
            setPid(project.getPid());
            setId(user.getId());
            setRid(role.getRid());
            
            ps = conn.prepareStatement("insert into r_prj_rol (pid,id,rid) values (?,?,?)");
            ps.setInt(1, project.getPid());
            ps.setInt(2, user.getId());
            ps.setInt(3, role.getRid());
            ps.execute();
            
            pk = new SecurityPrinciplePk(project.getPid(),
                user.getId(),
                role.getRid());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new CreateException("SecurityPrincipleBean#ejbCreate: Cannot create security Principle. \n"+e.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return pk;
    }

    /**
     * 
     * @param project 
     * @param user 
     * @param role 
     * @throws javax.ejb.CreateException 
     */
    public void ejbPostCreate(ProjectRemote project, UserRemote user, RoleRemote role) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }

    /**
     * Returns the project id
     * @return The project id
     */
    public int getPid() {
        return pid;
    }

    /**
     * Returns the id of the user which performed the latest changes to the information
     * @return The id of the user
     */
    public int getId() {
        return id;
    }    

    /**
     * Returns the role id
     * @return The role id
     */
    public int getRid() {
        return rid;
    }

    /**
     * Finds the security information by using the project id
     * @param pid The project id
     * @throws javax.ejb.FinderException If the information could not be retrieved
     * @return A security principle
     */
    public java.util.Collection ejbFindByProject(int pid) throws javax.ejb.FinderException {
        Collection arr = new ArrayList();
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            ps = conn.prepareStatement("select id,rid from r_prj_rol where pid = ?");
            ps.setInt(1, pid);
            
            int id = 0;
            int rid = 0;
            
            SecurityPrinciplePk pk = null;
            
            rs = ps.executeQuery();
            while (rs.next())
            {
                id = rs.getInt("id");
                rid = rs.getInt("rid");
                pk = new SecurityPrinciplePk(pid, id, rid);
                arr.add(pk);
            }
        }
        catch (Exception se)
        {
            throw new FinderException("SecurityPrincipleBean#ejbFindByProject: Cannot find project. \n" + se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return arr;
    }

    /**
     * Finds the security principle for a certain user
     * @param id The id of the user
     * @throws javax.ejb.FinderException If the information could not be retrieved
     * @return A security principle
     */
    public java.util.Collection ejbFindByUser(int id) throws javax.ejb.FinderException {
        Collection arr = new ArrayList();
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            ps = conn.prepareStatement("select rid, pid from r_prj_rol where id = ?");
            ps.setInt(1, id);
            
            int rid = 0;
            int pid = 0;
            
            SecurityPrinciplePk pk = null;
            
            rs = ps.executeQuery();
            while (rs.next())
            {
                rid = rs.getInt("rid");
                pid = rs.getInt("pid");
                pk = new SecurityPrinciplePk(pid, id, rid);
                arr.add(pk);
            }
        }
        catch (Exception se)
        {
            throw new FinderException("SecurityPrincipleBean#ejbFindByProject: Cannot find project. \n" + se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return arr;
    }

    /**
     * Returns the role for the user
     * @return The role
     */
    public RoleRemote getRole() {
        RoleRemote role = null;
        try
        {
            role = roleHome.findByPrimaryKey(new Integer(getRid()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return role;
    }

    /**
     * Returns the user of the security principle
     * @return The user id
     */
    public UserRemote getUser() {
        UserRemote user = null;
        try
        {
            user = userHome.findByPrimaryKey(new Integer(id));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Returns the project for the security principle
     * @return The project id
     */
    public ProjectRemote getProject() {
        ProjectRemote project = null;
        try
        {
            project = projectHome.findByPrimaryKey(new Integer(pid), null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return project;
    }

    /**
     * Sets the project id
     * @param pid The project id
     */
    public void setPid(int pid) {
        this.pid = pid;
        dirty = true;
    }

    /**
     * Sets the user id
     * @param id The user id
     */
    public void setId(int id) {
        this.id = id;
        dirty = true;
    }

    /**
     * Sets the role id
     * @param rid The role id
     */
    public void setRid(int rid) {
        this.rid = rid;
        dirty = true;
    }

    /**
     * Finds all roles for a user and project
     * @param id The id of the user
     * @param pid The project id
     * @throws javax.ejb.FinderException If the roles could not be retrieved
     * @return All roles for a project and user
     */
    public org.tgdb.project.securityprinciple.SecurityPrinciplePk ejbFindByUserProject(int id, int pid) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            ps = conn.prepareStatement("select rid from r_prj_rol where id = ? and pid = ?");
            ps.setInt(1, id);
            ps.setInt(2, pid);
            
            int rid = 0;
            
            SecurityPrinciplePk pk = null;
            
            rs = ps.executeQuery();
            while (rs.next())
            {
                rid = rs.getInt("rid");
                pk = new SecurityPrinciplePk(pid, id, rid);
                return pk;
            }
        }
        catch (Exception se)
        {
            throw new FinderException("SecurityPrincipleBean#ejbFindByProject: Cannot find project. \n" + se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        
        return null;
    }
}
