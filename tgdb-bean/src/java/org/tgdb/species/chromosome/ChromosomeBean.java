package org.tgdb.species.chromosome;

import org.tgdb.TgDbCaller;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.user.UserRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import org.tgdb.species.species.SpeciesRemote;
import org.tgdb.species.species.SpeciesRemoteHome;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.*;

/**
 * This is the bean class for the ChromosomeBean enterprise bean.
 * Created Jun 13, 2005 10:05:10 AM
 * @author lami
 * @todo Additional findBy's() required? Database relations?
 */
public class ChromosomeBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.species.chromosome.ChromosomeRemoteBusiness {
    private javax.ejb.EntityContext context;
    private int cid, sid;
    private String abbr;
    private String name;
    private String comm;
    private boolean dirty;
    
    private SpeciesRemoteHome speciesHome;
    private UserRemoteHome userHome;    
    private ChromosomeRemoteHome chromosomeHome;
    
    //ejb methods
    // <editor-fold defaultstate="collapsed">
    
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);        
        speciesHome = (SpeciesRemoteHome)locator.getHome(ServiceLocator.Services.SPECIES);
        chromosomeHome = (ChromosomeRemoteHome)locator.getHome(ServiceLocator.Services.CHROMOSOME);
    }
    
    public void ejbActivate() {}
    
    public void ejbPassivate() {}
    
    public void ejbRemove() {
        makeConnection();
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("delete from chromosomes where cid = ?");
            ps.setInt(1, cid);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            
            throw new EJBException("ChromosomeBean#ejbRemove: Internal error. Failed to delete chromsome\n(" +
                    e.getMessage() + ")");
        } finally {
            releaseConnection();
        }     
    }
    
    public void unsetEntityContext() {
        context = null;
    }
    
    public void ejbLoad() {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        Integer pk = (Integer)context.getPrimaryKey();
        try {
            ps = conn.prepareStatement("select name,abbr,comm,sid from chromosomes where cid = ?");
            ps.setInt(1,pk.intValue());
            
            result = ps.executeQuery();
            
            if (result.next()) {
                cid = pk.intValue();
                sid = result.getInt("sid");
                name = result.getString("name");
                abbr = result.getString("abbr");
                comm = result.getString("comm");
                dirty = false;
            } else
                throw new EJBException("ChromosomeBean#ejbLoad: Failed to load bean (no resultset?)");
        } catch (SQLException se) {
            throw new EJBException("ChromosomeBean#ejbLoad: Failed to load bean", se);
        } finally {
            releaseConnection();
        }
    }
    
    public void ejbStore() {
        if (dirty)
        {
            makeConnection();
            try {
                PreparedStatement ps = null;
                ps = conn.prepareStatement("update chromosomes set name = ?, abbr = ?, comm = ?, sid = ? where cid = ?");

                ps.setString(1, name);
                ps.setString(2, abbr);
                ps.setString(3, comm); 
                ps.setInt(4, sid);
                ps.setInt(5, cid);

                ps.execute();
            } catch (Exception e) {
                e.printStackTrace();
                throw new EJBException("ChromosomeBean#ejbStore: Error updating Chromosome ["+cid+"]");
            } finally {
                releaseConnection();
                dirty = false;
            }        
        }
    }
    
    // </editor-fold>
    
    //finder methods
    // <editor-fold defaultstate="collapsed">
    
    public java.lang.Integer ejbFindByPrimaryKey(Integer key) throws javax.ejb.FinderException, java.rmi.RemoteException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select cid from chromosomes where cid = ?");
            ps.setInt(1,key.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("ChromosomeBean#ejbFindByPrimaryKey: Cannot find chromosome");
            }
        } catch (SQLException se) {
            throw new EJBException("ChromosomeBean#ejbFindByPrimaryKey: SQL exception thrown", se);
        } finally {
            releaseConnection();
        }                
        
        return key;     
    }
    
    public java.lang.Integer ejbFindBySpeciesAndName(java.lang.String name, int sid) throws javax.ejb.FinderException {
        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select cid from chromosomes where sid = ? and name = ?");
            ps.setString(2, name);
            ps.setInt(1,sid);
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("ChromosomeBean#ejbFindBySpeciesAndName: Cannot find chromosome by name and species id");
            }
            else
            {
                cid = result.getInt("cid");
            }
        } catch (SQLException se) {
            se.printStackTrace();
            throw new FinderException("ChromosomeBean#ejbFindBySpeciesAndName: Cannot find chromosome by name and species id");
        } finally {
            releaseConnection();
        }

        return new Integer(cid);
    } 
    
    public Collection ejbFindByAbbreviation(java.lang.String abbreviation) throws javax.ejb.FinderException {
        
        Collection chromosomes = new ArrayList();

        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select cid from chromosomes where name like ?");
            ps.setString(1, abbreviation);
            result = ps.executeQuery();
            
            while (result.next()) {
                chromosomes.add(new Integer(result.getInt("cid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ChromosomeBean#ejbFindByAbbreviation: Cannot find chromosome by abbreviation.\n"+se.getMessage());
        } finally {
            releaseConnection();
        }
      
        return chromosomes;
    }
    
    public Collection ejbFindBySpecies(int sid, TgDbCaller caller) throws javax.ejb.FinderException{
        Collection chromosomes = new ArrayList();

        makeConnection();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select cid from chromosomes where sid = ? order by cid");
            ps.setInt(1, sid);
            result = ps.executeQuery();
            
            while (result.next()) {
                chromosomes.add(new Integer(result.getInt("cid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ChromosomeBean#ejbFindChromosomes: unable to find chromosomes. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
      
        return chromosomes;
    }
    
    //</editor-fold>
   
    //create+postcreate methods
    // <editor-fold defaultstate="collapsed">
    
    public java.lang.Integer ejbCreate(int cid, String name, String abbr, String comm, int sid) throws javax.ejb.CreateException, java.rmi.RemoteException {
        this.cid = cid;
        this.name = name;
        this.comm = comm;
        this.sid = sid;
        makeConnection();
        
        try {    
            PreparedStatement ps = null;
            ps = conn.prepareStatement("insert into chromosomes (cid,name,comm,sid) values (?,?,?,?)");
            ps.setInt(1, cid);
            ps.setString(2, name);
            ps.setString(3, comm);
            ps.setInt(4, sid);
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {                           
            throw new CreateException("ChromosomeBean#ejbCreate: Unable to create chromosome: "+e.getMessage());
        } finally {
            releaseConnection();    
        }

        return new Integer(cid);        
    }
    
    public void ejbPostCreate(int cid, String name, String abbr, String comm, int sid) throws javax.ejb.CreateException, java.rmi.RemoteException{}
    
    //</editor-fold>
    
    //setter+getter methods
    // <editor-fold defaultstate="collapsed">
   
    public String getName(){
        return name;
    }
    
    public void setName(java.lang.String name) {
        this.name = name;
        dirty = true;
    }
    
    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
        dirty = true;
    }
    
    public int getCid(){
        return cid;
    }
    
    public String getComm(){
        return comm;
    }

    public SpeciesRemote getSpecies() {
        SpeciesRemote s = null;
        try {
                        
            s = speciesHome.findByPrimaryKey(new Integer(sid));
        } catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        return s;
    }
    
    public void setComm(java.lang.String comm){
        this.comm = comm;
        dirty = true;
    }
    
    //</editor-fold>

}
