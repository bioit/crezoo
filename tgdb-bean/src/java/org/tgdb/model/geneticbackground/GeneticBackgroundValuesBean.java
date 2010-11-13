package org.tgdb.model.geneticbackground;

import javax.ejb.*;
//added
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.model.expmodel.ExpModelRemoteHome;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.project.ProjectRemote;
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

public class GeneticBackgroundValuesBean extends AbstractTgDbBean implements EntityBean, GeneticBackgroundValuesRemoteBusiness {
    private EntityContext context;
    
    private int bid, pid;
    private String backname;
    
    private boolean dirty;
    private UserRemoteHome userHome;
    private ExpModelRemoteHome modelHome;
    private ProjectRemoteHome projectHome;
    
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
            ps = conn.prepareStatement("delete from genetic_back_values where bid=?");
            //replace the questionmark
            ps.setInt(1, getBid());
            //execute the query.
            ps.execute();
        } catch (Exception e) {
            //if something goes wrong tell me about it.
            throw new EJBException("GeneticBackgroundValuesBean#ejbRemove: Unable to delete Genetic Background Strain. \n"+e.getMessage());
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
            ps = conn.prepareStatement("select bid, backname, pid " +
                    "from genetic_back_values where bid=?");
            //replace ? with the primary key's int value (pk is an Integer).
            ps.setInt(1, pk.intValue());
            //store the result of the query in rs.
            ResultSet rs = ps.executeQuery();

            //if the cursor of the resultset can be moved to the next position (row)
            //than we assume that all required data from the query have successfully returned(???).
            if (rs.next()) {
                bid = rs.getInt("bid");
                backname = rs.getString("backname");
                pid = rs.getInt("pid");
                dirty = false;
            } else
                throw new EJBException("GeneticBackgroundValuesBean#ejbLoad: Error loading Genetic Background Value Tuple");
        } catch (Exception e) {
            throw new EJBException("GeneticBackgroundValuesBean#ejbLoad: error loading Genetic Background Value. \n"+e.getMessage());
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
                ps = conn.prepareStatement("update genetic_back_values set backname=?, pid=? " +
                        "where bid=?");
                //replace the ? with the following order.
                ps.setString(1, backname);
                ps.setInt(2, pid);
                ps.setInt(3, bid);
               
                ps.execute();
            } catch (Exception e) {
                throw new EJBException("GeneticBackgroundValuesBean#ejbStore: error storing Genetic Background Values. \n"+e.getMessage());
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
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select bid from genetic_back_values where bid = ?");
            ps.setInt(1, aKey.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("GeneticBackgroundValuesBean#ejbFindByPrimaryKey: Cannot find Genetic Background value. No next in resultset");
            }
        } catch (SQLException se) {
            throw new FinderException("GeneticBackgroundBeanValues#ejbFindByPrimaryKey: Cannot find Genetic Background value. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return aKey;
    }

    public int getBid() {
        return bid;
    }

    public String getBackname() {
        return backname;
    }

    public void setBackname(String backname) {
        this.backname = backname;
        dirty=true;
    }
    
    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
        dirty=true;
    }

        //business method to track down project.
    public ProjectRemote getProject() throws ApplicationException
    {
        try
        {
            ProjectRemote prj = projectHome.findByPrimaryKey(new Integer(pid));
            return prj;
        }
        catch (Exception e)
        {
            throw new ApplicationException("Could not get project");
        }
    }
    
    public Integer ejbCreate(int bid, String backname, ProjectRemote project) throws javax.ejb.CreateException {
        makeConnection();
        //pk=the Integer that the method will return.
        Integer pk = null;
        try {
            this.bid = bid;
            this.backname = backname;
            //this.pid = pid;
            this.pid = project.getPid();
            
            //pk is the eid=primary key for genetic_back table.
            pk = new Integer(bid);
            //prepare the SQL query of the create method.
            PreparedStatement ps = conn.prepareStatement("insert into genetic_back_values (bid,backname,pid) values (?,?,?)");
            //replace the questionmarks with the following data...
            ps.setInt(1, bid);
            ps.setString(2, backname);
            ps.setInt(3, pid);
            //execute the statement.
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("GeneticBackgroundBeanValues#ejbCreate: Unable to create Genetic Background Strain. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }

    public void ejbPostCreate(int bid, String backname, ProjectRemote project) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }
    
    //-------finder methods--------
    public java.util.Collection ejbFindByProject(int pid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select bid from genetic_back_values where pid = ? order by backname");
            ps.setInt(1,pid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("bid")));
            }
        } catch (SQLException se) {
            throw new FinderException("GeneticBackgroundBeanValues#ejbFindByProject: Cannot find genetic backgrounds for project "+pid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

}
