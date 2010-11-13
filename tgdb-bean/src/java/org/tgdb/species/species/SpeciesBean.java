package org.tgdb.species.species;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.servicelocator.ServiceLocator;
import org.tgdb.species.chromosome.ChromosomeRemoteHome;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.*;

/**
 * This is the bean class for the SpeciesBean enterprise bean.
 * Created May 31, 2005 10:02:03 AM
 * @author heto
 */
public class SpeciesBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.species.species.SpeciesRemoteBusiness {
    private javax.ejb.EntityContext context;
    
    private int sid;
    private String name;
    private String comm;
    
    private boolean dirty;
    
    private ChromosomeRemoteHome chromosomeHome;
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise beans, Web services)
    // TODO Add business methods
    // TODO Add create methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
        chromosomeHome = (ChromosomeRemoteHome)locator.getHome(ServiceLocator.Services.CHROMOSOME);
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
            ps = conn.prepareStatement("delete from species where sid=?");
            ps.setInt(1, sid);
            
            ps.execute();
        } catch(SQLException sqle) {
            throw new EJBException("SQLException: Unable to remove species: "+sqle.getMessage(), sqle);
        } catch (Exception e) {
            throw new EJBException("EJBExecption: Unable to remove species: "+e.getMessage(), e);
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
        // TODO add code to retrieve data
        makeConnection();
        Integer pk = (Integer)context.getPrimaryKey();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select sid,name,comm " +
                    "from species where sid=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                this.sid = rs.getInt("sid");
                setName(rs.getString("name"));
                setComm(rs.getString("comm"));
                
                dirty = false;
            } else
                throw new EJBException("Error loading species");
        } catch (NoSuchEntityException nsee) {
            throw new EJBException("NoSuchEntityException: Could not load Species!", nsee);
        } catch (SQLException sqle) {
            throw new EJBException("SQLException: Could not load Species!", sqle);
        } catch(Exception e) {
            throw new EJBException("Exception: Could not load Species!", e);
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
                ps = conn.prepareStatement("update species set name=?,comm=? " +
                        "where sid=?");

                ps.setString(1, name);
                ps.setString(2, comm);
                ps.setInt(3, sid);

                ps.execute();
            } catch (Exception e) {
                throw new EJBException("species store ["+sid+","+name+","+comm+"]:"+e, e);
            } finally {
                releaseConnection();
                dirty = false;
            }
        }
    }
    
    // </editor-fold>
    
    /**
     * Finds a species by the primary key
     * @param pk The primary key
     * @throws FinderException If no species was found
     * @return The primary key for the species
     */
    public java.lang.Integer ejbFindByPrimaryKey(java.lang.Integer pk) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Integer res = null;
        
        try {
            ps = conn.prepareStatement("select sid from species where sid = ?");
            ps.setInt(1,pk.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("SpeciesBean#ejbFindByPrimaryKey: Cannot find species by id "+pk.intValue());
            }
            
            res = new Integer(result.getInt("sid"));
        } catch (SQLException se) {
            throw new FinderException("SpeciesBean#ejbFindByPrimaryKey: "+se.getMessage());
        } finally {
            releaseConnection();
        }
        
        
        return res;
    }
    
    /**
     * Finds a species by its name
     * @param name The name to look for
     * @return The primary key of the species with the specified name
     * @throws FinderException If SQL-query went wrong
     */
    public java.lang.Integer ejbFindByName(java.lang.String name) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Integer res = null;
        try {
            ps = conn.prepareStatement("select sid from species where name = ?");
            ps.setString(1,name);
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("SpeciesBean#ejbFindByName: Cannot find species by name");
            }
            
            res = new Integer(result.getInt("sid"));
        } catch (SQLException se) {
            throw new FinderException("SpeciesBean#ejbFindByName: "+se.getMessage());
        } finally {
            releaseConnection();
        }
        
        
        // Only reaches this if SQL-query is OK and an object has been found
        return res;
    }
    
    /**
     * Creates a new species
     * @param sid The species id
     * @param name The species name
     * @param comm The comment for the species
     * @return The primary key for the species
     * @throws CreateException If the species could not be created
     */
    public java.lang.Integer ejbCreate(int sid, java.lang.String name, java.lang.String comm) throws javax.ejb.CreateException {
        makeConnection();
        
        this.sid = sid;
        this.setName(name);
        this.setComm(comm);
        
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("insert into species (sid,name,comm) values (?,?,?)");
            ps.setInt(1, sid);
            ps.setString(2, name);
            ps.setString(3, comm);
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            throw new CreateException("SpeciesBean#ejbCreate: Unable to create species\n"+e.getMessage());
        } finally {
            releaseConnection();
        }

        return new Integer(sid);
    }
    
    /**
     * Does nothing
     * @param sid The species id
     * @param name The name
     * @param comm The comment
     * @throws javax.ejb.CreateException Well, never as of now...
     */
    public void ejbPostCreate(int sid, java.lang.String name, java.lang.String comm) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }
    
    /**
     * Returns the species id
     * @return The species id
     */
    public int getSid() {
        return sid;
    }
    
    /**
     * Returns the name for the species
     * @return The species name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the species name
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
        dirty = true;
    }
    
    /**
     * Returns the comment for the species
     * @return The species comment
     */
    public String getComm() {
        return comm;
    }
    
    /**
     * Sets the comment for the species
     * @param comm The new comment
     */
    public void setComm(String comm) {
        this.comm = comm;
        dirty = true;
    }

    /**
     * Retrieves the primary keys of all species in the database
     * @return A collection of primary keys of the species found in the database
     * @param pid The project id
     * @throws javax.ejb.FinderException If something went wrong when trying to find the species
     */
    public java.util.Collection ejbFindAll() throws javax.ejb.FinderException {
        Collection species = new ArrayList();

        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select s.sid from species s order by name");
            result = ps.executeQuery();
            
            while (result.next()) {
                species.add(new Integer(result.getInt("sid")));
            }
        } catch (SQLException se) {
            throw new FinderException("SpeciesBean#findAll: unable to find species. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return species;
    }    

    /**
     * Returns all chromosomes for the species
     * @return A collection of chromosomes
     */
    public Collection getChromosomes() throws ApplicationException {
        try
        {
            return chromosomeHome.findBySpecies(sid, caller);
        } 
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new ApplicationException("Failed to get chromosomes");
        } 
        
    }

    /**
     * Retrieves the primary keys of all species in the database
     * @return A collection of primary keys of the species found in the database
     * @param pid The project id
     * @throws javax.ejb.FinderException If something went wrong when trying to find the species
     */
    public java.util.Collection ejbFindByProject(int pid) throws javax.ejb.FinderException {
        Collection species = new ArrayList();

        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select s.sid from species s, r_prj_spc r where s.sid=r.sid and pid = ?");
            
            ps.setInt(1, pid);
            result = ps.executeQuery();
            
            while (result.next()) {
                species.add(new Integer(result.getInt("sid")));
            }
        } catch (SQLException se) {
            throw new FinderException("SpeciesBean#findAllSpecies: unable to find species. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
      
        return species;
    }
}
