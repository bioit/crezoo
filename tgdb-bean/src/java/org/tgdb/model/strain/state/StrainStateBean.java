package org.tgdb.model.strain.state;

import org.tgdb.TgDbCaller;
import org.tgdb.project.AbstractTgDbBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.*;

public class StrainStateBean extends AbstractTgDbBean implements EntityBean, StrainStateRemoteBusiness
{
    private EntityContext context;
    
    private int id;
    private String name;
    private String abbreviation;
    private int pid;
    
    private boolean dirty;
    
    //ejb methods
    //<editor-fold defaultstate="collapsed">
    public void setEntityContext(EntityContext aContext){
        context = aContext;
    }
    
    public void ejbActivate(){}
    
    public void ejbPassivate(){}
    
    public void ejbRemove(){
        makeConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("delete from strain_state where id=?");
            ps.setInt(1, id);
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("StrainStateBean#ejbRemove: Unable to delete strain type. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    public void unsetEntityContext(){
        context = null;
    }
    
    public void ejbLoad(){
        makeConnection();
        Integer pk = (Integer)context.getPrimaryKey();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select id,name,abbreviation, pid " +
                    "from strain_state where id=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                id = rs.getInt("id");
                name = rs.getString("name");
                abbreviation = rs.getString("abbreviation");
                pid = rs.getInt("pid");
                dirty = false;
            } else
                throw new EJBException("StrainStateBean#ejbLoad: Error loading strain type");
        } catch (Exception e) {
            throw new EJBException("StrainStateBean#ejbLoad: error loading strain type. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    public void ejbStore(){
        if (dirty)
        {
            makeConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("update strain_state set name=?,abbreviation=? where id=?");

                ps.setString(1, name);
                ps.setString(2, abbreviation);
                ps.setInt(3, id);

                ps.execute();
            } catch (Exception e) {
                throw new EJBException("StrainStateBean#ejbStore: error storing strain state. \n"+e.getMessage());
            } finally {
                releaseConnection();
                dirty = false;
            }
        }
    }
    
    // </editor-fold>
    
    //finder methods
    //<editor-fold defaultstate="collapsed">
    
    public Integer ejbFindByPrimaryKey(Integer aKey) throws FinderException{
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select id from strain_state where id = ?");
            ps.setInt(1,aKey.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("StrainStateBean#ejbFindByPrimaryKey: Cannot find strain state. No next in resultset");
            }
        } catch (SQLException se) {
            throw new FinderException("StrainStateBean#ejbFindByPrimaryKey: Cannot find strain state. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return aKey;
    }
    
    public Collection ejbFindByProject(int pid, TgDbCaller caller) throws javax.ejb.FinderException{
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = conn.prepareStatement("select id from strain_state where pid = ? order by id");
            ps.setInt(1,pid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("id")));
            }
        } catch (SQLException se) {
            throw new FinderException("StrainStateBean#ejbFindByProject: unable to find strain states for project. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public Collection ejbFindByStrain(int strainid, TgDbCaller caller) throws javax.ejb.FinderException{
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = conn.prepareStatement("select id from strain_state ss, r_strain_strain_state r where ss.id = r.stateid and r.strainid = ? order by id");
            ps.setInt(1,strainid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("id")));
            }
        } catch (SQLException se) {
            throw new FinderException("StrainStateBean#ejbFindByStrain: unable to find strain states for strain. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    public Collection ejbFindByAbbreviation(String abbreviation, TgDbCaller caller) throws javax.ejb.FinderException{
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = conn.prepareStatement("select id from strain_state where abbreviation like ?");
            ps.setString(1,abbreviation);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("id")));
            }
        } catch (SQLException se) {
            throw new FinderException("StrainStateBean#ejbFindByAbbreviation: unable to find strain states by abbreviation. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    //</editor-fold>
    
    //setter+getter methods
    // <editor-fold defaultstate="collapsed">
    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
        dirty = true;
    }

    public String getAbbreviation(){
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation){
        this.abbreviation = abbreviation;
        dirty = true;
    }
    
    public void setCaller(TgDbCaller caller){
        this.caller = caller;
    }
    //</editor-fold>

    //create+postcreate methods
    // <editor-fold defaultstate="collapsed">
    public Integer ejbCreate(int id, String name, String abbreviation, TgDbCaller caller) throws javax.ejb.CreateException{
        makeConnection();
        Integer pk = null;
        try {
            
            this.caller = caller;
            
            this.id = id;
            this.name = name;
            this.abbreviation = abbreviation;
            
            pk = new Integer(id);
            
            PreparedStatement ps = conn.prepareStatement("insert into strain_state (id, name, abbreviation, pid) values (?, ?, ?, ?)");
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, abbreviation);
            ps.setInt(4, caller.getPid());
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("StrainStateBean#ejbCreate: Unable to create strain state. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }

    public void ejbPostCreate(int id, String name, String abbreviation, TgDbCaller caller) throws javax.ejb.CreateException{}
    
    //</editor-fold>

}
