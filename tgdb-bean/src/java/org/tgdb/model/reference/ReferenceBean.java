package org.tgdb.model.reference;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.user.UserRemote;
import org.tgdb.resource.file.FileRemote;
import org.tgdb.resource.file.FileRemoteHome;
import org.tgdb.resource.link.LinkRemote;
import org.tgdb.resource.link.LinkRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.*;

/**
 * This is the bean class for the ReferenceBean enterprise bean.
 * <CODE>
 *    CREATE TABLE REFERENCE
 *    (
 *          refid        INT         NOT NULL,
 *          name         VARCHAR     NOT NULL,
 *          comm         VARCHAR     NULL,
 *          linkid       INT         NULL,
 *          fileid       INT         NULL,
 *          id           INT         NOT NULL,
 *          ts           TIMESTAMP   NULL,
 *          pid          INT         NOT NULL,
 *          PRIMARY KEY (refid),
 *          FOREIGN KEY (linkid) REFERENCES LINK (linkid) ON DELETE SET NULL,
 *          FOREIGN KEY (fileid) REFERENCES FILE (fileid) ON DELETE SET NULL,
 *          FOREIGN KEY (id) REFERENCES USERS (ID),
 *          FOREIGN KEY (pid) REFERENCES PROJECTS (pid) ON DELETE CASCADE
 *    );
 * </CODE>
 * Created Dec 15, 2005 11:27:50 AM
 * @author heto
 */
public class ReferenceBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.model.reference.ReferenceRemoteBusiness {
    private javax.ejb.EntityContext context;
    
    private int refid, id, linkid, fileid, pid;
    private String name, comm, pubmed;
    private boolean primary;
    private java.sql.Date ts;
    
    private boolean dirty;
    
    private FileRemoteHome fileHome;
    private LinkRemoteHome linkHome;
//    private UserRemoteHome userHome;
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise beans, Web services)
    // TODO Add business methods
    // TODO Add create methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
        fileHome = (FileRemoteHome)locator.getHome(ServiceLocator.Services.FILE);
        linkHome = (LinkRemoteHome)locator.getHome(ServiceLocator.Services.LINK);
//        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
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
        try {
            ps = conn.prepareStatement("delete from reference where refid=?");
            ps.setInt(1, refid);
            ps.execute();
            
            if(fileid != 0) {
                ps = conn.prepareStatement("delete from file where fileid=?");
                ps.setInt(1, fileid);
                ps.execute();                
            }
            else if(linkid != 0) {
                ps = conn.prepareStatement("delete from link where linkid=?");
                ps.setInt(1, linkid);
                ps.execute();                
            }
            
        } catch (Exception e) {
            throw new EJBException("ReferenceBean#ebjRemove: Unable to remove reference. \n"+e.getMessage());
        } finally {
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
        Integer pk = (Integer)context.getPrimaryKey();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select * from reference where refid=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                refid = rs.getInt("refid");
                name = rs.getString("name");
                comm = rs.getString("comm");
                linkid = rs.getInt("linkid");
                fileid = rs.getInt("fileid");
                id = rs.getInt("id");
                ts = rs.getDate("ts");
                pid = rs.getInt("pid");
                pubmed = rs.getString("pubmed");
                primary = rs.getBoolean("primary");
                dirty = false;
            } else
                throw new EJBException("ReferenceBean#ejbLoad: Error loading reference");
        } catch (Exception e) {
            throw new EJBException("ReferenceBean#ejbLoad: "+e.getMessage());
        } finally {
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
            try {
                ps = conn.prepareStatement("update reference set name=?,comm=?,linkid=?,fileid=?,id=?, pubmed=?, \"primary\"=?,ts=? " +
                        "where refid=?");

                int i=0;
                ps.setString(++i, name);
                ps.setString(++i, comm);
                if (linkid != 0)
                    ps.setInt(++i, linkid); 
                else
                    ps.setNull(++i, java.sql.Types.INTEGER);    
                if (fileid != 0)
                    ps.setInt(++i, fileid); 
                else
                    ps.setNull(++i, java.sql.Types.INTEGER);                   
                ps.setInt(++i, id);
                ps.setString(++i, pubmed);
                ps.setBoolean(++i, primary);
                ps.setDate(++i, new java.sql.Date(System.currentTimeMillis()));
                ps.setInt(++i, refid);
                
                int rows = ps.executeUpdate();
                if (rows!=1) {
                    throw new EJBException("ReferenceBean#ejbLoad: Error saving reference. Rows affected "+rows);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new EJBException("ReferenceBean#ejbLoad: Error saving reference. \n"+e.getMessage());
            } finally {
                releaseConnection();
                dirty = false;
            }
        }
    }
    
    // </editor-fold>
    
    /**
     * See EJB 2.0 and EJB 2.1 section 12.2.5
     */
    public java.lang.Integer ejbFindByPrimaryKey(java.lang.Integer key) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select refid from reference where refid = ?");
            ps.setInt(1,key.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("ReferenceBean#ejbFindByPrimaryKey: Cannot find reference");
            }
        } catch (SQLException se) {
            throw new FinderException("ReferenceBean#ejbFindByPrimaryKey: Cannot find reference "+refid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return key;
    }
    
    private void setCalled()
    {
        ts = new java.sql.Date(System.currentTimeMillis());
//        id = caller.getId();
        dirty = true;
    }

    /**
     * The id of the reference
     * @return The reference id
     */
    public int getRefid() {
        return refid;
    }

    /**
     * @return the pubmed
     */
    public String getPubmed() {
        return pubmed;
    }

    /**
     * @param pubmed the pubmed to set
     */
    public void setPubmed(String pubmed) {
        this.pubmed = pubmed;
        setCalled();
    }

    /**
     * @return the primary
     */
    public boolean isPrimary() {
        return primary;
    }

    /**
     * @param primary the primary to set
     */
    public void setPrimary(boolean primary) {
        this.primary = primary;
        setCalled();
    }

    /**
     * The name of the reference
     * @return The name of the reference
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the reference
     * @param name The name of the reference
     */
    public void setName(String name) {
        this.name = name;
        setCalled();
    }

    /**
     * Returns the comment for the reference
     * @return The comment for the reference
     */
    public String getComm() {
        return comm;
    }

    /**
     * Sets the comment for the reference
     * @param comm The comment for the reference
     */
    public void setComm(String comm) {
        this.comm = comm;
        setCalled();
    }

    /**
     * Returns the date for when the last modifications where made on the reference
     * @return The date for when the last modifications where made on the reference
     */
    public Date getTs() {
        return ts;
    }
    
    /**
     * Sets the file for the reference
     * @param file The file
     * @throws org.tgdb.exceptions.ApplicationException If the file could not be set
     */
    public void setFile(FileRemote file) throws ApplicationException
    {
        try
        {
            fileid = file.getFileId();
            setCalled();
        }
        catch (Exception e)
        {
            ApplicationException e2 = new ApplicationException("Failed to set file");
            e2.initCause(e);
            throw e2;
        }
    }
    
    /**
     * Returns the file for the reference
     * @return The file for the reference
     */
    public FileRemote getFile()
    {
        try
        {
            FileRemote file = fileHome.findByPrimaryKey(new Integer(fileid));
            return file;
        }
        catch (Exception e)
        {}
        return null;
    }

    /**
     * 
     * @param refid 
     * @param pid 
     * @param name 
     * @param comm 
     * @param caller 
     * @throws javax.ejb.CreateException 
     * @return 
     */
    public java.lang.Integer ejbCreate(int refid, int pid, String name, String comm, TgDbCaller caller) throws javax.ejb.CreateException {
        makeConnection();
        Integer pk = null;
        try {
            
            this.caller = caller;
            
            this.refid = refid;
            
            
            this.comm = comm;
            this.name = name;
            this.id = caller.getId();
            ts = new java.sql.Date(System.currentTimeMillis());
            
            
            pk = new Integer(refid);
            
            PreparedStatement ps = conn.prepareStatement("insert into reference (refid, name, id, ts, pid) values (?, ?, ?, ?, ?)");
            ps.setInt(1, refid);
            ps.setString(2, name);
            ps.setInt(3, id);
            ps.setDate(4, ts);
            ps.setInt(5, pid);
            
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("ReferenceBean#ejbCreate: Unable to create reference. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }

    /**
     * 
     * @param refid 
     * @param pid 
     * @param name 
     * @param comm 
     * @param caller 
     * @throws javax.ejb.CreateException 
     */
    public void ejbPostCreate(int refid, int pid, java.lang.String name, String comm, TgDbCaller caller) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }

    /**
     * Finds all references for the model
     * @param eid The model id
     * @throws javax.ejb.FinderException If the references could not be retrieved
     * @return The references for the model
     */
    public java.util.Collection ejbFindByModel(int eid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select refid from r_ref_model where eid = ? order by refid");
            ps.setInt(1,eid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("refid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ReferenceBean#ejbFindByModel: Cannot find reference by model "+eid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public java.util.Collection ejbFindByModelAndPrimary(int eid, boolean primary) throws javax.ejb.FinderException {
        makeConnection();

        Collection arr = new ArrayList();

        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select ref.refid from reference ref join r_ref_model r on r.refid = ref.refid where r.eid = ? and ref.primary = ? order by ref.refid");
            ps.setInt(1,eid);
            ps.setBoolean(2, primary);
            result = ps.executeQuery();

            while (result.next()) {
                arr.add(new Integer(result.getInt("refid")));
            }
        } catch (SQLException se) {
            logger.error(getStackTrace(se));
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public java.util.Collection ejbFindByExpression(int exid) throws javax.ejb.FinderException {
        makeConnection();

        Collection arr = new ArrayList();

        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select refid from r_ref_exp where exid = ?");
            ps.setInt(1,exid);
            result = ps.executeQuery();

            while (result.next()) {
                arr.add(new Integer(result.getInt("refid")));
            }
        } catch (SQLException se) {
            logger.error(getStackTrace(se));
        } finally {
            releaseConnection();
        }
        return arr;
    }

    /**
     * Sets the caller
     * @param caller The caller
     */
    @Override
    public void setCaller(TgDbCaller caller) {
        this.caller = caller;
    }

    /**
     * Returns the username of the user that made the latest changes on the reference
     * @throws org.tgdb.exceptions.ApplicationException If the user could not be retrieved
     * @return The username of the user that made the latest changes on the reference
     */
    public UserRemote getUser() throws ApplicationException {
        try
        {
            UserRemote user = userHome.findByPrimaryKey(new Integer(id));
            return user;
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to get user");
        }
    }
    
    /**
     * Sets the link for the reference
     * @param link The link
     * @throws org.tgdb.exceptions.ApplicationException If the link could not be set
     */
    public void setLink(LinkRemote link) throws ApplicationException
    {
        try
        {
            linkid = link.getLinkId();
            setCalled();
        }
        catch (Exception e)
        {
            ApplicationException e2 = new ApplicationException("Failed to set link");
            e2.initCause(e);
            throw e2;
        }
    }
    
    /**
     * Returns the link reference
     * @return The link reference
     */
    public LinkRemote getLink()
    {
        try
        {
            LinkRemote link = linkHome.findByPrimaryKey(new Integer(linkid));
            return link;
        }
        catch (Exception e)
        {}
        return null;
    }
}
