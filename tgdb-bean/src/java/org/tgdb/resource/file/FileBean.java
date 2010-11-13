package org.tgdb.resource.file;
import org.tgdb.TgDbCaller;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.project.user.UserRemote;
import org.tgdb.project.user.UserRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;

/**
 * This is the bean class for the FileBean enterprise bean.
 * Created Sep 13, 2005 2:27:27 PM
 * @author heto
 */
public class FileBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.resource.file.FileRemoteBusiness {
    private javax.ejb.EntityContext context;
    
    private int fileId, pid, id;
    private String name;
    private String comm;
    private int size;
    private String mimeType;
    private String fileType;
    private java.sql.Date ts;
    
    private boolean dirty;
    
    private UserRemoteHome userHome;    
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
        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
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
        try {
            PreparedStatement ps = null;
            ps = conn.prepareStatement("delete from file where fileid = ?");
            ps.setInt(1, fileId);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new EJBException("FileBean#ejbRemove: Error deleting file [fileid="+fileId+"]");
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
        try {
            makeConnection();
            PreparedStatement ps = null;
            ResultSet result = null;
            
            Integer pk = (Integer)context.getPrimaryKey();
            
            ps = conn.prepareStatement("select name,comm, pid, length(file) as size, mimetype, filetype, id, ts from file where fileid = ?");
            ps.setInt(1,pk.intValue());
            result = ps.executeQuery();
            
            if (result.next()) {
                fileId = pk.intValue();
                name = result.getString("name");
                comm = result.getString("comm");
                size = result.getInt("size");
                pid = result.getInt("pid");
                id = result.getInt("id");
                
                mimeType = result.getString("mimetype");
                fileType = result.getString("filetype");
                ts = result.getDate("ts");
                dirty = false;
            } else
                throw new EJBException("FileBean#ejbLoad: Failed to load file");
        } catch (Exception e) {
            e.printStackTrace();
            throw new EJBException("FileBean#ejbLoad: Failed to load file. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    /**
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() {
        if (dirty) {
            makeConnection();
            try {
                PreparedStatement ps = null;
                ps = conn.prepareStatement("update file set " +
                        " name = ?, comm = ?, mimetype = ?, filetype = ?, id = ?, ts = ? where fileid = ?");
                
                ps.setString(1, name);
                ps.setString(2, comm);
                ps.setString(3, mimeType);
                ps.setString(4, fileType);
                ps.setInt(5, id);
                ps.setDate(6, ts);
                ps.setInt(7, fileId);
                
                
                
                ps.execute();
            } catch (Exception e) {
                e.printStackTrace();
                throw new EJBException("FileBean#ejbStore: Error updating file [fileid="+fileId+"]");
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
    public java.lang.Integer ejbFindByPrimaryKey(java.lang.Integer fileid) throws javax.ejb.FinderException {                
        makeConnection();        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select fileid from file where fileid = ?");
            ps.setInt(1,fileid.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("FileManagerBean#ejbFindByPrimaryKey: Cannot find file");
            }
        } catch (SQLException se) {
            throw new FinderException("FileManagerBean#ejbFindByPrimaryKey: Cannot find file "+fileid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return fileid;
    }
    
    /**
     * Returns the file id
     * @return The file id
     */
    public int getFileId() {
        return fileId;
    }
    
    /**
     * Sets the file id
     * @param fileId The file id
     */
    public void setFileId(int fileId) {
        this.fileId = fileId;
        dirty = true;
    }
    
    /**
     * Returns the name of the file
     * @return The name of the file
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the file
     * @param name The name of the file
     */
    public void setName(String name) {
        this.name = name;
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;
    }
    
    /**
     * Returns the comment for the file
     * @return The comment for the file
     */
    public String getComm() {
        return comm;
    }
    
    /**
     * Sets the comment for the file
     * @param comm The comment for the file
     */
    public void setComm(String comm) {
        this.comm = comm;
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;
    }
    
    /**
     * Sets the file data
     * @param data The file data
     */
    public void setData(byte[] data) {
        
        logger.debug("---------------------------------------->FileBean#setData: Data "+data.length);
        
        int res = 0;
        try {
            makeConnection();
            PreparedStatement ps = null;

            ps = conn.prepareStatement("update file set file = ? where fileid = ?");
            ps.setBytes(1, data);
            ps.setInt(2,fileId);            
            
            res = ps.executeUpdate();
            
            ts = new java.sql.Date(System.currentTimeMillis());
            dirty = true;
            ps.close();
            
        } catch (Exception e) {
            logger.error("---------------------------------------->FileBean#setData: Failed");
            throw new EJBException("FileBean#setData", e);
        } finally {
            releaseConnection();
        }
        
        logger.debug("---------------------------------------->FileBean#setData: Completed "+res);
    }
    
    /**
     * Returns the file data
     * @return The file data
     */
    public byte[] getData() {
        byte[] data = null;
        try {
            makeConnection();
            PreparedStatement ps = null;
            ResultSet result = null;
            
            ps = conn.prepareStatement("select file from file where fileid = ?");
            ps.setInt(1,fileId);
            
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                data = rs.getBytes("file");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseConnection();
        }
        return data;
    }
    
    /**
     * Create a new file
     * @param fileid 
     * @param name 
     * @param comm 
     * @param mimeType 
     * @param fileType 
     * @param caller id and pid is used by this method
     * @throws javax.ejb.CreateException 
     * @return 
     */
    public java.lang.Integer ejbCreate(Integer fileid, java.lang.String name, String comm, String mimeType, String fileType, TgDbCaller caller) throws javax.ejb.CreateException {
        try {
            this.ts = new java.sql.Date(System.currentTimeMillis());
            this.fileId = fileid.intValue();
            this.name = name;
            this.comm = comm;
            this.mimeType = mimeType;
            this.fileType = fileType;
            this.pid = caller.getPid();
            this.id = caller.getId();
            
            makeConnection();
            
            
            String sql = "insert into file (fileid, name, comm, mimeType, fileType, pid, id, ts) values (?,?,?,?,?,?,?,?);";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, fileid.intValue());
            stmt.setString(2, name);
            stmt.setString(3, comm);
            stmt.setString(4, mimeType);
            stmt.setString(5, fileType);
            stmt.setInt(6, pid);
            stmt.setInt(7, id);
            stmt.setDate(8, ts);
            
            stmt.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("Unable to create table file");
        } finally {
            releaseConnection();
        }
        return fileid;
    }
    
    /**
     * 
     * @param fileid 
     * @param name 
     * @param comm 
     * @param mimeType 
     * @param fileType 
     * @param caller 
     * @throws javax.ejb.CreateException 
     */
    public void ejbPostCreate(Integer fileid, java.lang.String name, String comm, String mimeType, String fileType, TgDbCaller caller) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }
    
    /**
     * Finds all files
     * @throws javax.ejb.FinderException If the files could not be retrieved
     * @return All files in the database
     */
    public java.util.Collection ejbFindAll() throws javax.ejb.FinderException {
        Collection all = new ArrayList();
        try {
            
            makeConnection();
            PreparedStatement ps = null;
            ResultSet result = null;
            
            ps = conn.prepareStatement("select fileid from file order by name");
            result = ps.executeQuery();
            
            while (result.next()) {
                all.add(new Integer(result.getInt("fileid")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new FinderException("FileBean#ejbFindAll: Unable to find files.");
        } finally {
            releaseConnection();
        }
        return all;
    }
    
    /**
     * Returns the size of the file
     * @return The size of the file
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Returns the mime type for the file
     * @return The mime type for the file
     */
    public String getMimeType() {
        return mimeType;
    }
    
    /**
     * Sets the mime type for the file
     * @param mimeType The mime type for the file
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;
    }
    
    /**
     * Returns the file type
     * @return The file type
     */
    public String getFileType() {
        return fileType;
    }
    
    /**
     * Sets the file type
     * @param fileType The file type
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
        ts = new java.sql.Date(System.currentTimeMillis());
        dirty = true;
    }

    /**
     * Returns the user that last modfied the file
     * @return The user that made the last modficiations on the file
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
     * Returns the project that the file belongs to
     * @return The project that the file belongs to
     */
    public ProjectRemote getProject() {
        try {
            return projectHome.findByPrimaryKey(new Integer(pid));
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Finds all files for a project
     * @param pid The project id
     * @throws javax.ejb.FinderException If the files could not be retrieved
     * @return The files for the project
     */
    public java.util.Collection ejbFindByProject(int pid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select fileid from file where pid = ?");
            ps.setInt(1, pid);

            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("fileid")));
            }
        } catch (SQLException se) {
            throw new FinderException("FileBean#ejbFindByProject: Cannot find files by project: "+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    /**
     * Finds all files for a sampling unit
     * @param suid The sampling unit id
     * @throws javax.ejb.FinderException If the files could not be retrieved
     * @return The files for a sampling unit
     */
    public java.util.Collection ejbFindBySamplingUnit(int suid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select r.fileid from r_file_su r, file f where r.fileid = f.fileid and r.suid = ? order by f.name");
            ps.setInt(1, suid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("fileid")));
            }
        } catch (SQLException se) {
            throw new EJBException("FileBean#findBySamplingUnit: Cannot find files for sampling unit.\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    /**
     * Finds all files for a genetic modification
     * @param gmid The genetic modification id
     * @throws javax.ejb.FinderException If the files could not be retrieved
     * @return The files for the genetic modification
     */
    public java.util.Collection ejbFindByGeneticModification(int gmid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select r.fileid from R_GENMOD_FILE r, file f where r.fileid=f.fileid and gmid = ? order by f.name");
            ps.setInt(1, gmid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("fileid")));
            }
        } catch (Exception se) {
            throw new EJBException("FileBean#findBySamplingUnit: Cannot find files for genetic modification.\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    public java.util.Collection ejbFindByExpressionModel(int exid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select g.fileid from file g, expression_file_r r where g.fileid=r.fileid and r.exid = ? order by g.name");
            ps.setInt(1,exid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("fileid")));
            }
        } catch (SQLException se) {
            throw new FinderException("FileBean#ejbFindByExpressionModel: Cannot find file [image] by expression model "+exid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public void setCaller(TgDbCaller caller) {
        this.caller = caller;
    }
    
    /**
     * Returns the date for when the file was last modified
     * @return The date for when the file was last modified
     */
    public java.sql.Date getTs() {
        return ts;
    }
    
    
    
}
