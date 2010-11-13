package org.tgdb.model.geneticbackground;

import javax.ejb.*;
//added
import org.tgdb.model.expmodel.ExpModelRemoteHome;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.project.ProjectRemoteHome;
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

//the makeconnection+closeconnection functions are found in the AbstractTgDbBean class.

public class GeneticBackgroundBean extends AbstractTgDbBean implements EntityBean, GeneticBackgroundRemoteBusiness {
    private EntityContext context;
    
    private int gbid, eid, dna_origin, targeted_back, host_back, backcrossing_strain;//, backcrosses;
    private String backcrosses;
    
    private boolean dirty;
    private UserRemoteHome userHome;
    private ExpModelRemoteHome modelHome;
    private ProjectRemoteHome projectHome;
    private GeneticBackgroundValuesRemoteHome genbackValuesHome;
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise beans, Web services)
    // TODO Add business methods
    // TODO Add create methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(EntityContext aContext) {
        context = aContext;
        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
        modelHome = (ExpModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPMODEL);
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);
        genbackValuesHome = (GeneticBackgroundValuesRemoteHome)locator.getHome(ServiceLocator.Services.GENETIC_BACKGROUND_VALUES);
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
        //open connection.
        makeConnection();
        //declare ps.
        PreparedStatement ps = null;
        try {
            //prepare the query
            ps = conn.prepareStatement("delete from genetic_back where gbid=?");
            //replace the questionmark
            ps.setInt(1, gbid);
            //execute the query.
            ps.execute();
        } catch (Exception e) {
            //if something goes wrong tell me about it.
            throw new EJBException("GeneticBackgroundBean#ejbRemove: Unable to delete Genetic Background Info for model. "+eid+" \n"+e.getMessage());
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
        //open connection.
        makeConnection();
        //get the primary key+store to Integer pk
        Integer pk = (Integer)context.getPrimaryKey();
        //declare ps.
        PreparedStatement ps = null;
        try {
            //prepare query
            ps = conn.prepareStatement("select gbid, eid, dna_origin, targeted_back, host_back, backcrossing_strain, backcrosses " +
                    "from genetic_back where gbid=?");
            //replace ? with the primary key's int value (pk is an Integer).
            ps.setInt(1, pk.intValue());
            //store the result of the query in rs.
            ResultSet rs = ps.executeQuery();

            //if the cursor of the resultset can be moved to the next position (row)
            //than we assume that all required data from the query have successfully returned(???).
            if (rs.next()) {
                gbid = rs.getInt("gbid");
                eid = rs.getInt("eid");
                dna_origin = rs.getInt("dna_origin");
                targeted_back = rs.getInt("targeted_back");
                host_back = rs.getInt("host_back");
                backcrossing_strain = rs.getInt("backcrossing_strain");
                //backcrosses = rs.getInt("backcrosses");
                backcrosses = rs.getString("backcrosses");
                dirty = false;
            } else
                throw new EJBException("GeneticBackgroundBean#ejbLoad: Error loading Genetic Background Info tuple");
        } catch (Exception e) {
            throw new EJBException("GeneticBackgroundBean#ejbLoad: error loading Genetic Background Info tuple for model. "+eid+" \n"+e.getMessage());
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
                //prepare the query.
                ps = conn.prepareStatement("update genetic_back set eid=?, dna_origin=?,targeted_back=?,host_back=?, backcrossing_strain=?, backcrosses=? " +
                        "where gbid=?");
                //replace the ? with the following order.
                ps.setInt(1, eid);
                ps.setInt(2, dna_origin);
                ps.setInt(3, targeted_back);
                ps.setInt(4, host_back);
                ps.setInt(5, backcrossing_strain);
                //ps.setInt(6, backcrosses);
                ps.setString(6, backcrosses);
                ps.setInt(7, gbid);
               
                ps.execute();
            } catch (Exception e) {
                throw new EJBException("GeneticBackgroundBean#ejbStore: error storing Genetin Background Information for model. "+eid+" \n"+e.getMessage());
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
    public Integer ejbFindByPrimaryKey(Integer aKey) throws FinderException {
        // TODO add code to locate aKey from persistent storage
        // throw javax.ejb.ObjectNotFoundException if aKey is not in
        // persistent storage.
       //return null;
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select gbid from genetic_back where gbid = ?");
            ps.setInt(1, aKey.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("GeneticBackgroundBean#ejbFindByPrimaryKey: Cannot find Genetic Background Information. No next in resultset");
            }
        } catch (SQLException se) {
            throw new FinderException("GeneticBackgroundBean#ejbFindByPrimaryKey: Cannot find Genetic Background Information. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return aKey;
    }

    //set+get methods for genetic_back fields
    
    public int getGbid() {
        return gbid;
    }
    
    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
        dirty = true;
    }

    public int getDna_origin() {
        return dna_origin;
    }

    public void setDna_origin(int dna_origin) {
        this.dna_origin = dna_origin;
        dirty = true;
    }

    public int getTargeted_back() {
        return targeted_back;
    }

    public void setTargeted_back(int targeted_back) {
        this.targeted_back = targeted_back;
        dirty = true;
    }

    public int getHost_back() {
        return host_back;
    }

    public void setHost_back(int host_back) {
        this.host_back = host_back;
        dirty = true;
    }

    public int getBackcrossing_strain() {
        return backcrossing_strain;
    }

    public void setBackcrossing_strain(int backcrossing_strain) {
        this.backcrossing_strain = backcrossing_strain;
        dirty = true;
    }

    public String getBackcrosses() {
        return backcrosses;
    }

    public void setBackcrosses(String backcrosses) {
        this.backcrosses = backcrosses;
        dirty = true;
    }
    
    public String getBackNameFromBackId(int backId){
        try
        {
            GeneticBackgroundValuesRemote genBackValues = genbackValuesHome.findByPrimaryKey(new Integer(backId));
            String backId_name = genBackValues.getBackname();
            return backId_name;
            
        }
        catch (Exception e)
        {
            throw new EJBException("Could not get background name");
        }
        //return null;
    }

    //create+postcreate methods.
    
    public Integer ejbCreate(int gbid, int eid, int dna_origin, int targeted_back, int host_back, int backcrossing_strain, String backcrosses) throws javax.ejb.CreateException {
        makeConnection();
        //pk=the Integer that the method will return.
        Integer pk = null;
        try {
            this.gbid = gbid;
            this.eid = eid;
            this.dna_origin = dna_origin;
            this.targeted_back = targeted_back;
            this.host_back = host_back;
            this.backcrossing_strain = backcrossing_strain;
            this.backcrosses = backcrosses;
            
            //pk is the eid=primary key for genetic_back table.
            pk = new Integer(gbid);
            //prepare the SQL query of the create method.
            PreparedStatement ps = conn.prepareStatement("insert into genetic_back (gbid,eid,dna_origin,targeted_back,host_back,backcrossing_strain,backcrosses) values (?,?,?,?,?,?,?)");
            //replace the questionmarks with the following data...
            ps.setInt(1, gbid);
            ps.setInt(2, eid);
            ps.setInt(3, dna_origin);
            ps.setInt(4, targeted_back);
            ps.setInt(5, host_back);
            ps.setInt(6, backcrossing_strain);
            //ps.setInt(7, backcrosses);
            ps.setString(7, backcrosses);
            //execute the statement.
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("GeneticBackgroundBean#ejbCreate: Unable to create Genetic Background Tuble for model."+eid+" \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }

    public void ejbPostCreate(int gbid,int eid, int dna_origin, int targeted_back, int host_back, int backcrossing_strain, String backcrosses) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }

    //custom finder method
    public Collection ejbFindByGeneticBackgroundModel(int eid) throws javax.ejb.FinderException {
        makeConnection();
        Collection arr = new ArrayList();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select gbid from genetic_back where eid = ? order by eid");
            ps.setInt(1, eid);
            result = ps.executeQuery();
            while (result.next()) {
                //add the gbid of 'every tuple' to collection arr
                arr.add(new Integer(result.getInt("gbid")));
            }
        } catch (SQLException se) {
            throw new FinderException("GeneticBackgroundBean#ejbFindByProject: Cannot find genetic background information for model "+eid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
}
