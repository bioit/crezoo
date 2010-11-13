package org.tgdb.model.strain.mutationtype;

import org.tgdb.TgDbCaller;
import org.tgdb.project.AbstractTgDbBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.*;

public class MutationTypeBean extends AbstractTgDbBean implements EntityBean, MutationTypeRemoteBusiness
{
    private EntityContext context;
    
    private int id;
    private String abbreviation;
    private String name;
    
    private boolean dirty;
    
    //ejb methods
    // <editor-fold defaultstate="collapsed">
    public void setEntityContext(EntityContext aContext){
        context = aContext;
    }
    
    public void ejbActivate(){}
    
    public void ejbPassivate(){}
    
    public void ejbRemove(){
        makeConnection();
        PreparedStatement ps = null;
        try
        {
            ps = conn.prepareStatement("delete from mutation_type where id=?");
            ps.setInt(1, id);
            ps.execute();
        }
        catch (Exception e)
        {
            throw new EJBException("MutationTypeBean#ejbRemove: Unable to delete mutation type. \n"+e.getMessage());
        }
        finally
        {
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
        try
        {
            ps = conn.prepareStatement("select id,abbreviation,name " +
                    "from mutation_type where id=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next())
            {
                id = rs.getInt("id");
                abbreviation = rs.getString("abbreviation");
                name = rs.getString("name");
                dirty = false;
            }
            else
                throw new EJBException("MutationTypeBean#ejbLoad: Error loading mutation type");
        }
        catch (Exception e)
        {
            throw new EJBException("MutationTypeBean#ejbLoad: error loading mutation type. \n"+e.getMessage());
        }
        finally
        {
            releaseConnection();
        }
    }
    
    public void ejbStore(){
        if (dirty)
        {
            makeConnection();
            PreparedStatement ps = null;
            try
            {
                ps = conn.prepareStatement("update mutation_type set name=?,abbreviation=? where id=?");
                
                ps.setString(1, name);
                ps.setString(2, abbreviation);
                
                ps.setInt(3, id);
                
                ps.execute();
            }
            catch (Exception e)
            {
                throw new EJBException("MutationTypeBean#ejbStore: error storing mutation type. \n"+e.getMessage());
            }
            finally
            {
                releaseConnection();
                dirty = false;
            }
        }
    }
    
    // </editor-fold>
    
    //finder methods
    //<editor-fold>
    
    public Integer ejbFindByPrimaryKey(Integer aKey) throws FinderException{
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try
        {
            ps = conn.prepareStatement("select id from mutation_type where id = ?");
            ps.setInt(1,aKey.intValue());
            result = ps.executeQuery();
            
            if (!result.next())
            {
                throw new ObjectNotFoundException("MutationTypeBean#ejbFindByPrimaryKey: Cannot find mutation type. No next in resultset");
            }
        }
        catch (SQLException se)
        {
            throw new FinderException("MutationTypeBean#ejbFindByPrimaryKey: Cannot find mutation type. \n"+se.getMessage());
        }
        finally
        {
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

            ps = conn.prepareStatement("select id from mutation_type where pid = ? order by id");
            ps.setInt(1,pid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("id")));
            }
        } catch (SQLException se) {
            throw new FinderException("MutationTypeBean#ejbFindByProject: unable to find mutation types for project. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }

    public Collection ejbFindByStrainAllele(int stain_allele_id, int eid, TgDbCaller caller) throws javax.ejb.FinderException{
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            //FIXME!!! - Deleted commented out line once everything is done
//            ps = conn.prepareStatement("select mutationtype from R_MUTATION_TYPE_STRAIN_ALLELE where strainallele = ? order by mutationtype");
//            ps = conn.prepareStatement("select mutation_type as mutationtype from r_strain_strain_allele_mutation_type where strain = ? and strain_allele = ? order by mutationtype");
            ps = conn.prepareStatement("select mutation_type as mutationtype from r_model_strain_allele_mutation_type where model = ? and strain_allele = ? order by mutationtype");
            ps.setInt(1,eid);
            ps.setInt(2,stain_allele_id);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("mutationtype")));
            }
        } catch (SQLException se) {
            throw new FinderException("MutationTypeBean#ejbFindByStrainAllele: unable to find mutation types for strain allele. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    public Collection ejbFindByStrainAlleleUnassignment(int strainalleleid, TgDbCaller caller) throws javax.ejb.FinderException{
        makeConnection();
        this.caller = caller;
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            //FIXME!!! - Remove commented line (kill r_mutation_type_strain_allele)
//            ps = conn.prepareStatement("select id from mutation_type where id not in (select mutationtype from r_mutation_type_strain_allele where strainallele=?)");
            ps = conn.prepareStatement("select id from mutation_type where id not in (select mutation_type from r_model_strain_allele_mutation_type where strain_allele=?)");
            ps.setInt(1,strainalleleid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("id")));
            }
        } catch (SQLException se) {
            throw new FinderException("MutationTypeBean#ejbFindByStrainAlleleUnassignment: unable to find mutation types for this strainallele. \n"+se.getMessage());
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

            ps = conn.prepareStatement("elect id from mutation_type where abbreviation like ? ");
            ps.setString(1,abbreviation);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("id")));
            }
        } catch (SQLException se) {
            throw new FinderException("MutationTypeBean#ejbFindByAbbreviation: unable to find mutation types by abbreviation. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    
    //</editor-fold>

    //setter+getter methods
    //<editor-fold>
    /**
     * 
     * @deprecated Use getId() instead.
     */
    public int getMutantid()
    {
        return id;
    }
    
    public int getId()
    {
        return id;
    }

    public String getAbbreviation()
    {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation)
    {
        this.abbreviation = abbreviation;
        dirty = true;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
        dirty = true;
    }
    
    //</editor-fold>

    //create+postcreate methods
    // <editor-fold defaultstate="collapsed">

    public Integer ejbCreate(int id, String name, TgDbCaller caller) throws javax.ejb.CreateException{
        makeConnection();
        Integer pk = null;
        try {

            this.caller = caller;

            this.id = id;
            this.name = name;

            pk = new Integer(id);

            PreparedStatement ps = conn.prepareStatement("insert into mutation_type (id, name, pid) values (?, ?, ?)");
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setInt(3, caller.getPid());

            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("Unable to create mutation type. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }

    public void ejbPostCreate(int id, String name, TgDbCaller caller) throws javax.ejb.CreateException{}

    //</editor-fold>
}
