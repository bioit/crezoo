package org.tgdb.expression.expressionmodel;

import org.tgdb.exceptions.ApplicationException;
import org.tgdb.model.expmodel.ExpModelRemoteHome;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.project.ProjectRemoteHome;
import org.tgdb.resource.file.FileRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import org.tgdb.species.chromosome.ChromosomeRemoteHome;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import org.tgdb.dto.OlsDTO;

public class ExpressionModelBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.expression.expressionmodel.ExpressionModelRemoteBusiness {
    private javax.ejb.EntityContext context;
    
    private int exid;
    private String exanatomy, excomm;
    
    private boolean dirty;
    
//    private UserRemoteHome userHome;
    private ExpModelRemoteHome modelHome;
    private ProjectRemoteHome projectHome;
    private ChromosomeRemoteHome chromosomeHome;
    private FileRemoteHome fileHome;
    
    //ejb-methods
    // <editor-fold>
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
//        userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
        modelHome = (ExpModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPMODEL);
        projectHome = (ProjectRemoteHome)locator.getHome(ServiceLocator.Services.PROJECT);
        chromosomeHome = (ChromosomeRemoteHome)locator.getHome(ServiceLocator.Services.CHROMOSOME);
        fileHome = (FileRemoteHome)locator.getHome(ServiceLocator.Services.FILE);
    }
    
    public void ejbActivate() {}
    
    public void ejbPassivate() {}
    
    public void ejbRemove() {
        makeConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("delete from expression where exid=?");
            ps.setInt(1, exid);
            ps.execute();
        } catch (Exception e) {
            throw new EJBException("ExpressionModel#ejbRemove: Unable to delete Expression Model.\n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    public void unsetEntityContext() {
        context = null;
    }
    
    public void ejbLoad() {
        makeConnection();
        Integer pk = (Integer)context.getPrimaryKey();
        PreparedStatement ps = null;
        try {
            
            ps = conn.prepareStatement("select exid, exanatomy, excomm from expression where exid=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                exid = rs.getInt("exid");
                exanatomy = rs.getString("exanatomy");
                excomm = rs.getString("excomm");
                dirty = false;
            } else
                throw new EJBException("ExpressionModelBean#ejbLoad: Error loading Expression Model");
        } catch (Exception e) {
            throw new EJBException("ExpressionModelBean#ejbLoad: error loading Expression Model. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }
    
    public void ejbStore() {
        if (dirty)
        {
            makeConnection();
            PreparedStatement ps = null;
            try {
                
                ps = conn.prepareStatement("update expression set exanatomy=?,excomm=? where exid=?");

                ps.setString(1, exanatomy);
                ps.setString(2, excomm);
                ps.setInt(3, exid);
                
                ps.execute();
            } catch (Exception e) {
                throw new EJBException("ExpressionModelBean#ejbStore: error storing Expressino Model. \n"+e.getMessage());
            } finally {
                releaseConnection();
                dirty = false;
            }
        }
    }
    
    // </editor-fold>
    
    //finder methods
    //<editor-fold>
    public Integer ejbFindByPrimaryKey(Integer key) throws FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select exid from expression where exid = ?");
            ps.setInt(1,key.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("ExpressionModelBean#ejbFindByPrimaryKey: Cannot find Expression Model. No next in resultset");
            }
        } catch (SQLException se) {
            throw new FinderException("ExpressionModelBean#ejbFindByPrimaryKey: Cannot find Expression Model. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return key;
    }
    
    public java.util.Collection ejbFindByModel(int eid) throws javax.ejb.FinderException {
        makeConnection();
        
        Collection arr = new ArrayList();
        
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select g.exid from expression g, expression_model_r r where g.exid=r.exid and r.eid = ? order by g.exanatomy");
            ps.setInt(1,eid);
            result = ps.executeQuery();
            
            while (result.next()) {
                arr.add(new Integer(result.getInt("exid")));
            }
        } catch (SQLException se) {
            throw new FinderException("ExpressionModelBean#ejbFindByModel: Cannot find expresion model by model "+eid+" \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return arr;
    }
    //</editor-fold>
    
    //setter+getter methods
    //<editor-fold>
    public int getExid() {
        return exid;
    }
    
    public String getExanatomy(){
        return exanatomy;
    }
    
    public void setExanatomy(String exanatomy) {
        this.exanatomy = exanatomy;
        dirty = true;
    }
    
    public String getExcomm(){
        return excomm;
    }
    
    public void setExcomm(String excomm){
        this.excomm = excomm;
        dirty = true;
    }
    
    public Collection getFiles(){
        Collection arr = null;
        try {
            arr = fileHome.findByExpressionModel(exid);
        } catch (FinderException fe) {
            throw new EJBException(fe);
        } catch (RemoteException re) {
            throw new EJBException(re);
        }
        return arr;
    }
    
    //</editor-fold>
    
    //create+postcreate methods
    //<editor-fold>
    public java.lang.Integer ejbCreate(int exid, java.lang.String exanatomy, java.lang.String excomm) throws javax.ejb.CreateException {
        makeConnection();
        Integer pk = null;
        try {
            
            this.exid = exid;
            this.exanatomy = exanatomy;
            this.excomm = excomm;
            
            pk = new Integer(exid);
            
            PreparedStatement ps = conn.prepareStatement("insert into expression (exid,exanatomy,excomm) values (?,?,?)");
            ps.setInt(1, exid);
            ps.setString(2, exanatomy);
            ps.setString(3, excomm);
            
            ps.execute();
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("ExpressionModelBean#ejbCreate: Unable to create Expression Model. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }
    
    public void ejbPostCreate(int exid, java.lang.String exanatomy, java.lang.String excomm) throws javax.ejb.CreateException {}

    //</editor-fold>
    
    //relational methods
    //<editor-fold>
    public void addFile(int fileid) throws ApplicationException {
        makeConnection();
        try {
            
            PreparedStatement ps = conn.prepareStatement("insert into expression_file_r (exid,fileid) values (?,?)");
            ps.setInt(1, exid);
            ps.setInt(2, fileid);
            
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("ExpressionModelBean#addFileToExpressionModel: Unable to add file to expression model. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
    }

    public void addOntology(String oid, String namespace) throws ApplicationException {
        makeConnection();
        try {

            PreparedStatement ps = conn.prepareStatement("insert into r_expression_ontology (exid, oid, namespace) values (?,?,?)");
            ps.setInt(1, exid);
            ps.setString(2, oid);
            ps.setString(3, namespace);

            ps.execute();
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        } finally {
            releaseConnection();
        }
    }

    public void deleteOntology(String oid, String namespace) throws ApplicationException {
        makeConnection();
        try {

            PreparedStatement ps = conn.prepareStatement("delete from r_expression_ontology where exid = ? and oid = ? and namespace = ?");
            ps.setInt(1, exid);
            ps.setString(2, oid);
            ps.setString(3, namespace);

            ps.execute();
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        } finally {
            releaseConnection();
        }
    }

    public Collection getOntologyTerms(String namespace) throws ApplicationException {
        Collection terms = new ArrayList();
        makeConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("select * from r_expression_ontology where exid= ? and namespace = ?");
            ps.setInt(1, exid);
            ps.setString(2, namespace);
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                OlsDTO tmp = new OlsDTO();
                tmp.setOid(result.getString("oid"));
                tmp.setNamespace(namespace);
                terms.add(tmp);
            }
        } catch (Exception e) {
            logger.error(getStackTrace(e));
        } finally {
            releaseConnection();
        }
        return terms;
    }
    //</editor-fold>
}
