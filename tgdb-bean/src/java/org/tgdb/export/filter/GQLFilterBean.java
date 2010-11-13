package org.tgdb.export.filter;
import org.tgdb.frame.PageManager;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.PermissionDeniedException;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.ParamDataObject;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.project.user.UserRemote;
import org.tgdb.project.user.UserRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import org.tgdb.species.species.SpeciesRemote;
import org.tgdb.species.species.SpeciesRemoteHome;
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

public class GQLFilterBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.export.filter.GQLFilterRemoteBusiness {
    private javax.ejb.EntityContext context;
    private int fid, pid, sid, id;
    private String expression, name, comm;
    private java.sql.Date ts;
    private SpeciesRemoteHome speciesHome;
    private ProjectRemoteHome projectHome;
    private UserRemoteHome userHome;
    private boolean dirty;
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Consider creating Transfer Object to encapsulate data
    // TODO Review finder methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
        speciesHome = (SpeciesRemoteHome)locator.getHome(ServiceLocator.Services.SPECIES);
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);
        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);        
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
            ps = conn.prepareStatement("delete from filters where fid=?");
            ps.setInt(1, fid);
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("FilterBean#ejbRemove: Unable to delete filter. \n"+e.getMessage());
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
            ps = conn.prepareStatement("select * from filters where fid=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                fid = rs.getInt("fid");
                pid = rs.getInt("pid");
                sid = rs.getInt("sid");
                name = rs.getString("name");
                comm = rs.getString("comm");
                id = rs.getInt("id");
                ts = rs.getDate("ts");
                expression = rs.getString("expression");
                dirty = false;
            } else
                throw new EJBException("FilterBean#ejbLoad: Error loading filter");
        } catch (Exception e) {
            throw new EJBException("FilterBean#ejbLoad: error loading filter. \n"+e.getMessage());
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
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("update filters set name=?,comm=?," +
                        "sid=?, pid=?, expression=?, id=?,ts=? where fid=?");
                
                ps.setString(1, name);
                ps.setString(2, comm);
                ps.setInt(3, sid);
                ps.setInt(4, pid);
                ps.setString(5, expression);
                ps.setInt(6, id);
                ps.setDate(7, ts);
                ps.setInt(8, fid);
                
                ps.execute();
            } catch (Exception e) {
                throw new EJBException("FilterBean#ejbStore: error storing filter. \n"+e.getMessage());
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
    
    public java.lang.Integer ejbCreate(int fid, String name, String comm, String expression, int sid, int pid, TgDbCaller caller)  throws javax.ejb.CreateException {
        makeConnection();
        Integer pk = null;
        try {
            this.fid = fid;            
            this.name = name;
            this.comm = comm;
            this.sid = sid;
            this.pid = pid;
            ts = new java.sql.Date(System.currentTimeMillis());
            id = caller.getId();
            pk = new Integer(fid);
            
            PreparedStatement ps = conn.prepareStatement("insert into filters (fid,sid,pid,name,comm,expression,id,ts) values (?,?,?,?,?,?,?,?)");
            ps.setInt(1, fid);
            ps.setInt(2, sid);
            ps.setInt(3, pid);
            ps.setString(4, name);
            ps.setString(5, comm);
            ps.setString(6, expression);
            ps.setInt(7, id);
            ps.setDate(8, ts);
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("FilterBean#ejbCreate: Unable to create filter. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }
    
    /**
     * 
     * @param fid 
     * @param name 
     * @param comm 
     * @param expression 
     * @param sid 
     * @param pid 
     * @param caller 
     */
    public void ejbPostCreate(int fid, String name, String comm, String expression, int sid, int pid, TgDbCaller caller) {
        // TODO populate relationships here if appropriate        
    }

    /**
     * Returns the id for the filter
     * @return The filter id
     */
    public int getFid() {
        return fid;
    }

    /**
     * Returns the project for the filter
     * @return The project remote interface for this filter
     */
    public ProjectRemote getProject() {
        ProjectRemote project = null;
        try {
            project = projectHome.findByPrimaryKey(new Integer(pid), caller);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return project;
    }

    /**
     * Sets the project id for this filter
     * @param pid The new project id
     */
    public void setPid(int pid) {
        this.pid = pid;
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());  
        dirty = true;
    }

    /**
     * Returns the species for this filter
     * @return The species for this filter
     */
    public SpeciesRemote getSpecies() {
        SpeciesRemote species = null;
        try {
            species = speciesHome.findByPrimaryKey(new Integer(sid));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return species;
    }

    /**
     * Sets the species id for this filter
     * @param sid The new species id for this filter
     */
    public void setSid(int sid) {
        this.sid = sid;
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());  
        dirty = true;
    }

    /**
     * Returns the SQL expression for this filter
     * @return The SQL exspression for the filter
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Sets the SQL expression for this filter
     * @param expression The new SQL expression for this filter
     */
    public void setExpression(String expression) {
        this.expression = expression;
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());  
        dirty = true;
    }

    /**
     * Returns the name of the filter
     * @return The name of the filter
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the filter
     * @param name The name of the filter
     */
    public void setName(String name) {
        this.name = name;
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());  
        dirty = true;
    }

    /**
     * Returns the comment for the filter
     * @return The comment for the filter
     */
    public String getComm() {
        return comm;
    }

    /**
     * Finder method for a filter. Finds by the use of filters primary key...
     * @param key The primary key of the filter
     * @throws javax.ejb.FinderException If the filter could not be found
     * @return The found filter
     */
    public java.lang.Integer ejbFindByPrimaryKey(java.lang.Integer key) throws javax.ejb.FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select fid from filters where fid = ?");
            ps.setInt(1, key.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("FilterBean#ejbFindByPrimaryKey: Cannot find filter. No next in resultset");
            }
        } catch (SQLException se) {
            throw new FinderException("FilterBean#ejbFindByPrimaryKey: Cannot find filter. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return key;
    }

    /**
     * Sets the comment for the filter
     * @param comm The comment for the filter
     */
    public void setComm(String comm) {
        this.comm = comm;
        id = caller.getId();
        ts = new java.sql.Date(System.currentTimeMillis());  
        dirty = true;
    }

    /**
     * Returns the date for when the filter was last updated
     * @return The date for when the filter was last updated
     */
    public java.sql.Date getUpdated() {
        return ts;
    }
    
    /**
     * Returns the user that made the latest changes to this filter
     * @return The user that made the latest changes on the filter
     */
    public UserRemote getUser() {
        UserRemote usr = null;
        try {
            usr = userHome.findByPrimaryKey(new Integer(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usr;
    } 

    /**
     * Finds a filter through a restricted query. The restrictions depends on the parameters in the paramdataobject.
     * @param pdo The parameter data object that can contain query restrictions from the mining filter
     * @param pageManager The pagemanager object. It is used for optimizing purposes...
     * @throws javax.ejb.FinderException If something went wrong when searching for the filters
     * @return A collection of filters
     */
    public java.util.Collection ejbFindByQuery(ParamDataObject pdo, PageManager pageManager) throws javax.ejb.FinderException {
        String sql = "";      
        Collection arr = new ArrayList();
        
        if(pdo != null && pdo.hasValues()) {
            ArrayList list = pdo.getKeys();
            String column = "";
            String data = "";
            String term = "";

            for(int i=0;i<list.size();i++) {
                if(sql.length() > 0)
                    term = " AND";
                column = (String)list.get(i);
                
                data = pdo.getValue(column);
                if(data != null && data.length() > 0 && !data.equalsIgnoreCase("null")) {
                    sql += term+" "+column+" LIKE '"+data+"'";
                }
            }
            
            sql += " ORDER BY name";
            
            makeConnection();
            

            Statement st = null;    

            try {
                String sqlFull = "SELECT fid, name FROM filters WHERE ";
                
                st = conn.createStatement();


                st = conn.createStatement();
                ResultSet result = null;                

                result = st.executeQuery(sqlFull+sql);
                while (result.next()) {       
                    arr.add(new Integer(result.getInt("fid")));          
                }                    
            } catch (Exception se) {
                se.printStackTrace();
                throw new FinderException("Unable to find filters."+se.getMessage());
            } finally {
                releaseConnection();
            }                      
        }  
        
        return arr;  
    }

    /**
     * Returns the number of filters in the database
     * @param pdo The parameter data object that might contain mining filter restrictions from the UI
     * @return The number of filters given the restrictions applied on the query
     */
    public int ejbHomeGetNumberOfFilters(org.tgdb.project.ParamDataObject pdo) {
        String sql = "";      
        int num = 0;
       
        if(pdo != null && pdo.hasValues()) {
            // Get the mining attributes from the UI
            ArrayList list = pdo.getKeys();
            String column = "";
            String data = "";
            String term = "";

            // Make SQL restrictions on the query depending on the 
            // keys in the pdo
            for(int i=0;i<list.size();i++) {
                if(sql.length() > 0)
                    term = " AND";
                column = (String)list.get(i);
                
                data = pdo.getValue(column);
                if(data != null && data.length() > 0 && !data.equalsIgnoreCase("null")) {
                    sql += term+" "+column+" LIKE '"+data+"'";
                }
            }
            
            makeConnection();
            

            Statement st = null;    

            try {
                
                String sqlFull = "SELECT count(fid) AS num FROM filters WHERE " + sql;
                
                st = conn.createStatement();
                ResultSet result = st.executeQuery(sqlFull);
                if(result.next())
                    num = result.getInt("num");
            } catch (Exception se) {
                se.printStackTrace();
                throw new EJBException("Unable to count filters."+se.getMessage());
            } finally {
                releaseConnection();
            }                      
        }  
        
        return num; 
    }

    /**
     * Sets the current caller object
     * @param caller The current caller object
     */
    public void setCaller(TgDbCaller caller) {
        this.caller = caller;
    }
    
    /**
     * Writes a log entry to track changes history
     * @throws org.tgdb.exceptions.PermissionDeniedException If the caller does not have GR_W privilege
     */
    public void addHistory() throws PermissionDeniedException {
        if (!caller.hasPrivilege("FLT_W"))
            throw new PermissionDeniedException("Permission denied. Unable to write history."); 
        
        makeConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("insert into groups_log (fid, name, expression, comm, sid, id, ts) values (?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, fid);
            ps.setString(2, name);
            ps.setString(3, expression);
            ps.setString(4, comm);
            ps.setInt(5, sid);
            ps.setInt(6, caller.getId());
            ps.setDate(7, ts);

        } catch (Exception e) {
            throw new EJBException("FilterBean#addHistory: Error writing history for filter. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
      
}
