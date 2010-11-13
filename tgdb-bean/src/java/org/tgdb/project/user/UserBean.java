package org.tgdb.project.user;
import org.tgdb.TgDbCaller;
import org.tgdb.encryption.StringEncrypter;
import org.tgdb.encryption.StringEncrypter.EncryptionException;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.PermissionDeniedException;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.securityprinciple.SecurityPrincipleRemoteHome;
import org.tgdb.resource.link.LinkRemote;
import org.tgdb.resource.link.LinkRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;

public class UserBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.project.user.UserRemoteBusiness {
    private javax.ejb.EntityContext context;

    private int id;
    private String usr, pwd, email, name, status, groupName, groupAddress, groupPhone, distinguish;
    private int groupLink, userLink;
    private boolean admin;
    
    private boolean dirty;
    
    /**
     * Encryption key for the password field.
     * This MUST NOT be changed without consideration. Old values will be 
     * completely lost! 
     * Setting crypt field to null and setting a clear text password will 
     * force creation of the new crypto value.
     */
    private String encryptionKey = "dskj38sh2hdh3//hdhj21ndn8n hdshdg";
    
    
    private SecurityPrincipleRemoteHome secHome;
    private LinkRemoteHome linkHome;
    
    // <editor-fold defaultstate="collapsed" desc="ejb methods">
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
        secHome = (SecurityPrincipleRemoteHome)locator.getHome(ServiceLocator.Services.SECURITYPRINCIPLE);
        linkHome = (LinkRemoteHome)locator.getHome(ServiceLocator.Services.LINK);
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
    public void ejbRemove() 
    {
        makeConnection();
        Statement stmt = null;
        String sql = "";
        try 
        {
            sql = "update users set status='D' where id = "+id;
            
            System.err.println("SQL="+sql);
            stmt = conn.createStatement();
            stmt.execute(sql);
        } 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace(System.err);
                
            throw new EJBException("UserBean#ejbRemove: Failed to delete user\n" +
              sqle.getMessage());
        } 
        finally 
        {
            try 
            {
                if (stmt != null) stmt.close();
            } 
            catch (SQLException ignored) 
            {}
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
        ResultSet result = null;
        Integer pk = (Integer)context.getPrimaryKey();
        try
        {
            ps = conn.prepareStatement("select * from users where id = ?");
            ps.setInt(1,pk.intValue());

            result = ps.executeQuery();

            if (result.next())
            {
                usr = result.getString("usr");
                pwd = result.getString("pwd");
                String crypt = result.getString("crypt");
                
                // Check if encryption is used.
                if (pwd == null && crypt!=null && !crypt.equals(""))
                {
                    // Create the encrypter
                    StringEncrypter encrypter =
                            new StringEncrypter( "DES", encryptionKey );
                    
                    // Decrypt
                    pwd = encrypter.decrypt( crypt );
                }
                
                    
                
                name = result.getString("name");
                status  = result.getString("status");
                id = result.getInt("id");
                groupLink = result.getInt("group_link");
                userLink = result.getInt("user_link");
                email  = result.getString("email");
                groupName  = result.getString("group_name");
                groupAddress  = result.getString("group_addr");
                groupPhone  = result.getString("group_phone");
                admin = result.getBoolean("admin");
                distinguish = result.getString("distinguish");
                dirty = false;
            }
            else
                throw new EJBException("UserBean#ejbLoad: could not find user "+pk);
        }
        catch (EncryptionException e)
        {
            e.printStackTrace();
            throw new EJBException("Failed to decrypt password", e);
        }
        catch (SQLException se)
        {
            throw new EJBException(se);
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
            PreparedStatement stmt = null;
            try 
            {
                // Make the encrypter
                StringEncrypter encrypter = 
                    new StringEncrypter( "DES", encryptionKey );
                
                // Encrypt the cleartext password
                String pwdEncrypted = encrypter.encrypt( pwd );
                
                
                stmt = conn.prepareStatement("update users set usr=?, crypt=?, pwd=?, name=?, status=?, group_name=?, group_phone=?, group_addr=?, email=?, group_link=?, user_link=?, admin=?, distinguish=? where id=?");
                stmt.setString(1, usr);
                stmt.setString(2, pwdEncrypted);            // Set the encrypted password    
                stmt.setNull(3, java.sql.Types.VARCHAR);    // Set cleartext to null!
                stmt.setString(4, name);
                stmt.setString(5, status);
                stmt.setString(6, groupName);
                stmt.setString(7, groupPhone);
                stmt.setString(8, groupAddress);
                stmt.setString(9, email);
                stmt.setInt(10, groupLink);
                stmt.setInt(11, userLink);
                stmt.setBoolean(12, admin);
                stmt.setString(13, distinguish);
                stmt.setInt(14, id);
                
                stmt.execute();
            } 
            catch (EncryptionException e) {
                e.printStackTrace();
                throw new EJBException("UserBean#ejbStore: Failed to encrypt password");
            }
            catch (SQLException sqle) {
                sqle.printStackTrace(System.err);

                throw new EJBException("UserBean#ejbStore: Failed to update user\n" +
                    sqle.getMessage());
            } 
            finally {
                try {
                    if (stmt != null) stmt.close();
                } 
                catch (SQLException ignored) {}
                releaseConnection();
                dirty = false;
            }
        }
    }
    
    // </editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="finder methods">
    
    public Integer ejbFindByPrimaryKey(Integer aKey) throws javax.ejb.FinderException {
        // TODO add code to locate aKey from persistent storage
        // throw javax.ejb.ObjectNotFoundException if aKey is not in
        // persistent storage.
        
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try
        {
            ps = conn.prepareStatement("select id from users where id = ?");
            ps.setInt(1,aKey.intValue());
            result = ps.executeQuery();
           
            if (!result.next())
            {
                throw new ObjectNotFoundException("UserBean#ejbFindByPrimaryKey: Cannot find user. Result set is empty. Id="+id);
            }
        }
        catch (SQLException se)
        {
            throw new FinderException("UserBean#ejbFindByPrimaryKey: unable to find user.\n"+ se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return aKey;
    }

    public java.lang.Integer ejbFindByUsr(java.lang.String usr) throws javax.ejb.FinderException {

        makeConnection();

        PreparedStatement ps = null;
        ResultSet result = null;

        Integer to_return = null;
        try
        {
            ps = conn.prepareStatement("select id from users where usr = ?");
            ps.setString(1,usr);
            result = ps.executeQuery();


            if (result.next())
            {
                to_return = new Integer(result.getInt("id"));
            }
            else
                throw new ObjectNotFoundException("UserBean#ejbFindByUser: Cannot find user "+usr);
        }
        catch (SQLException se)
        {
            throw new FinderException("UserBean#ejbFindByUser: Cannot find user "+usr+" \n"+se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return to_return;
    }

    /**
     * Returns the number of users
     * @return The number of users
     */
    public int ejbHomeGetNumberOfUsers() {
        //TODO implement ejbHomeGetNumberOfUsers

        makeConnection();

        PreparedStatement ps = null;
        ResultSet result = null;

        int num = 0;
        try
        {
            ps = conn.prepareStatement("select count(*) as num from users");
            result = ps.executeQuery();


            if (result.next())
            {
                num = result.getInt("num");
            }
            else
                throw new EJBException();
        }
        catch (SQLException se)
        {
            throw new EJBException("UserBean#ejbHomeGetNumberOfUsers: Unable to get the number of users. \n"+se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return num;
    }

    /**
     * Finds a user using the username and password.
     *
     * Password encryption is enabled by default with fallback option.
     * First the password gets encrypted and the search begun in the db.
     * If no user found with the encrypted password, try with the clear text
     * field instead. If that fails, the user is not valid.
     *
     * @param usr The username
     * @param pwd The password
     * @throws javax.ejb.FinderException If the user could not be found
     * @return A user
     */
    public java.lang.Integer ejbFindByUserAndPwd(java.lang.String usr, java.lang.String pwd) throws javax.ejb.FinderException {
        makeConnection();

        PreparedStatement ps = null;
        ResultSet result = null;

        Integer to_return = null;
        try
        {

            // Encrypted part
            StringEncrypter encrypter =
                    new StringEncrypter( "DES", encryptionKey );

            // Encrypt password and search with the encrypted password.
            String pwdEncrypted = encrypter.encrypt( pwd );

            ps = conn.prepareStatement("select id from users where usr = ? and crypt = ?");
            ps.setString(1,usr);
            ps.setString(2, pwdEncrypted);
            result = ps.executeQuery();

            if (result.next())
            {
                to_return = new Integer(result.getInt("id"));
            }
            else
            {

                // Clear text find.
                ps = conn.prepareStatement("select id from users where usr = ? and pwd = ?");
                ps.setString(1,usr);
                ps.setString(2, pwd);
                result = ps.executeQuery();

                if (result.next())
                {
                    to_return = new Integer(result.getInt("id"));
                }
                /*else
                    throw new ObjectNotFoundException("UserBean#ejbFindByUser: Cannot find user "+usr);*/
                }
        }
        catch (EncryptionException e)
        {
            e.printStackTrace();
            throw new EJBException("Encrypt/Decrypt failed", e);
        }
        catch (SQLException se)
        {
            throw new FinderException("UserBean#ejbFindByUser: Cannot find user "+usr+" \n"+se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return to_return;
    }

    /**
     * Find all users in the database
     * @param caller The caller of this method
     * @throws javax.ejb.FinderException if errors occur.
     * @return a collection of EJBs
     */
    public java.util.Collection ejbFindAll(TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();

        PreparedStatement ps = null;
        ResultSet result = null;

        Collection all = new ArrayList();
        try
        {
            ps = conn.prepareStatement("select id from users order by name");

            result = ps.executeQuery();
            while (result.next()){
                all.add(new Integer(result.getInt("id")));
            }
        }
        catch (SQLException se)
        {
            throw new FinderException("UserBean#ejbFindAll: Cannot find user "+usr+" \n"+se.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return all;
    }

    public Collection ejbFindByDistinguish(String distinguish, TgDbCaller caller) throws javax.ejb.FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        Collection all = new ArrayList();
        try
        {
            ps = conn.prepareStatement("select id from users where distinguish = ? order by name");
            ps.setString(1, distinguish);

            result = ps.executeQuery();
            while (result.next()) all.add(new Integer(result.getInt("id")));
        }
        catch (SQLException se) {
            throw new FinderException("Cannot find user "+usr+" \n"+se.getMessage());
        }
        finally {
            releaseConnection();
        }
        return all;
    }
    
   //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setter+getter methods">
    /**
     * Returns the user id
     * @return The user id
     */
    public int getId() {
        return id;
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
     * @return the distinguish
     */
    public String getDistinguish() {
        return distinguish;
    }

    /**
     * @param distinguish the distinguish to set
     */
    public void setDistinguish(String distinguish) {
        this.distinguish = distinguish;
        dirty = true;
    }

    /**
     * Returns the username of the user
     * @return The username
     */
    public String getUsr() {
        return usr;
    }

    /**
     * Sets the username of the user
     * @param usr The username
     */
    public void setUsr(String usr) {
        this.usr = usr;
        dirty = true;
    }

    /**
     * Returns the password for the user
     * @return The password
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * Sets the password of the user
     * @param pwd The password
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
        dirty = true;
    }

    /**
     * Returns the name of the user (NOTE: This is not the username but the users real name)
     * @return The name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user (NOTE: This is the real name of the user and not the username)
     * @param name The name of the user
     */
    public void setName(String name) {
        this.name = name;
        dirty = true;
    }

    /**
     * Returns the status of the user
     * @return The status of the user
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the user
     * @param status The status of the user
     */
    public void setStatus(String status) {
        this.status = status;
        dirty = true;
    }

    /**
     * Returns the security principles assosciated with a specific user
     * @return The security privilege for the user
     */
    public Collection getSecurityPrinciples() {
        Collection arr = new ArrayList();
        try
        {
            arr = secHome.findByUser(id);
        }
        catch (Exception e)
        {
            throw new EJBException(e);
        }
        return arr;
    }

    /**
     * Returns the name of the research group the user works in
     * @return The name of the research group
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the name of the research group the user works in
     * @param groupName The groupname
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
        dirty = true;
    }

    /**
     * Returns the post address for the research group
     * @return The post address
     */
    public String getGroupAddress() {
        return groupAddress;
    }

    /**
     * Sets the post address for the research group
     * @param groupAddress The post address for the research group
     */
    public void setGroupAddress(String groupAddress) {
        this.groupAddress = groupAddress;
        dirty = true;
    }

    /**
     * Returns the phonenumber for the research group
     * @return The phonenumber for the research group
     */
    public String getGroupPhone() {
        return groupPhone;
    }

    /**
     * Sets the phonenumber for the research group
     * @param groupPhone The phonenumber for the research group
     */
    public void setGroupPhone(String groupPhone) {
        this.groupPhone = groupPhone;
        dirty = true;
    }

    private LinkRemote getLink(int linkId) {
        LinkRemote aLink = null;
        try {
            if (linkId != 0)
                aLink = linkHome.findByPrimaryKey(new Integer(linkId));
        } catch (Exception e) {
            throw new EJBException("UserBean#getLink: Unable to get link. \n"+e.getMessage());
        }
        return aLink;
    }

    /**
     * Returns the link to the groups webpage
     * @return The link to the groups webpage
     */
    public LinkRemote getGroupLink() {
        return getLink(groupLink);
    }

    /**
     * Sets the link to the groups webpage
     * @param groupLink The link to the groups webpage
     */
    public void setGroupLink(int groupLink) {
        this.groupLink = groupLink;
        dirty = true;
    }

    /**
     * The link to the users webpage
     * @return The link to the users webpage
     */
    public LinkRemote getUserLink() {
        return getLink(userLink);
    }

    /**
     * Returns the email address of the user
     * @return The users email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the users email address
     * @param email The email address
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
        dirty = true;
    }

    /**
     * Sets the link to the users webpage
     * @param userLink The link to the users webbpage
     */
    public void setUserLink(int userLink) {
        this.userLink = userLink;
        dirty = true;
    }

    /**
     * Is this user permitted to admin the server.
     * @return True if so...false otherwise
     */
    public boolean isAdmin() {
        return admin;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="create+postcreate methods">

    public java.lang.Integer ejbCreate(int id, java.lang.String usr, java.lang.String pwd, java.lang.String name, java.lang.String status) throws javax.ejb.CreateException {
        
        makeConnection();
        
        this.id = id;
        this.usr = usr;
        this.pwd = pwd;
        this.name = name;
        this.status = status;
        String sql = "insert into users (id,usr,pwd,name,status) values (?,?,?,?,?) ";
        
        try
        {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setString(2, usr);
            stmt.setString(3, pwd);
            stmt.setString(4, name);
            stmt.setString(5, status);
            
            stmt.executeUpdate();
            dirty = false;
        }
        catch (Exception e)
        {
            throw new CreateException("UserBean#ejbCreate: unable to create user.\n"+e.getMessage());
        }
        finally
        {
            releaseConnection();
        }
        return new Integer(id);
    }
    
    public void ejbPostCreate(int id, java.lang.String usr, java.lang.String pwd, java.lang.String name, java.lang.String status) throws javax.ejb.CreateException {}

    //</editor-fold>
    
    /**
     * Set the flag to indicate that this user is permitted to 
     * admin the server.
     * @param admin If the user is admin
     * @throws org.tgdb.exceptions.ApplicationException If the user is not allowed to set as admin
     */
    public void setAdmin(boolean admin) throws ApplicationException {
        
        try
        {
            UserRemote admUser = userHome.findByPrimaryKey(new Integer(caller.getId()));
            if (!admUser.isAdmin())
                throw new PermissionDeniedException("Permission denied setting admin flag.");
            
            this.admin = admin;
            dirty = true;
        }
        catch (PermissionDeniedException pd)
        {
            throw pd;
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to set admin");
        }
    }
}
