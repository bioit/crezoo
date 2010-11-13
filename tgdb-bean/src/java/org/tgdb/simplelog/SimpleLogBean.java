package org.tgdb.simplelog;

import org.tgdb.project.AbstractTgDbBean;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;



/**
 * This is the bean class for the SimpleLogBean enterprise bean.
 * Created Dec 22, 2005 10:42:25 AM
 * @author heto
 */
public class SimpleLogBean extends AbstractTgDbBean implements javax.ejb.EntityBean, org.tgdb.simplelog.SimpleLogRemoteBusiness {
    private javax.ejb.EntityContext context;
    
    private int logid;
    private java.sql.Timestamp ts;
    private String txt;
    
    /*
     create table simplelog (
       logid   int   not null,
       ts      timestamp null,
       txt     varchar   null,
       primary key (logid)
     );
     */
     
    private boolean dirty;
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise beans, Web services)
    // TODO Add business methods
    // TODO Add create methods
    /**
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(javax.ejb.EntityContext aContext) {
        context = aContext;
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
            ps = conn.prepareStatement("select logid,ts,txt " +
                    "from simplelog where logid=?");
            ps.setInt(1, pk.intValue());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                logid = rs.getInt("logid");
                ts = rs.getTimestamp("ts");
                txt = rs.getString("txt");
                dirty = false;
            } else
                throw new EJBException("SimpleLogBean#ejbLoad: Error loading log");
        } catch (Exception e) {
            throw new EJBException("SimpleLogBean#ejbLoad: error loading log. \n"+e.getMessage());
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
                ps = conn.prepareStatement("update simplelog set txt=?,ts=? where logid=?");

                ps.setString(1, txt);
                ps.setTimestamp(2, ts);                       
                ps.setInt(3, logid);

                ps.execute();
            } catch (Exception e) {
                throw new EJBException("SimpleLogBean#ejbStore: error storing log. \n"+e.getMessage());
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
    public java.lang.Integer ejbFindByPrimaryKey(java.lang.Integer aKey) throws javax.ejb.FinderException {
        makeConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = conn.prepareStatement("select logid from simplelog where logid = ?");
            ps.setInt(1,aKey.intValue());
            result = ps.executeQuery();
            
            if (!result.next()) {
                throw new ObjectNotFoundException("SimpleLogBean#ejbFindByPrimaryKey: Cannot find log. No next in resultset");
            }
        } catch (SQLException se) {
            throw new FinderException("SimpleLogBean#ejbFindByPrimaryKey: Cannot find log. \n"+se.getMessage());
        } finally {
            releaseConnection();
        }
        return aKey;
    }

    public int getLogid() {
        return logid;
    }

    public java.sql.Timestamp getTs() {
        return ts;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public java.lang.Integer ejbCreate(java.lang.String txt) throws javax.ejb.CreateException {
        makeConnection();
        Integer pk = null;
        try {
            
            PreparedStatement ps = conn.prepareStatement("select nextval('simplelog_seq') as logid");
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                logid=rs.getInt("logid");
            }
            
            
            
            
            this.txt = txt;
            
            ts = new java.sql.Timestamp(System.currentTimeMillis());
            
            pk = new Integer(logid);
            
            PreparedStatement ps2 = conn.prepareStatement("insert into simplelog (logid,txt,ts) values (?,?,?)");
            ps2.setInt(1, logid);
            ps2.setString(2, txt);
            ps2.setTimestamp(3, ts);
            
            ps2.execute();
            
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateException("SimpleLogBean#ejbCreate: Unable to create log. \n"+e.getMessage());
        } finally {
            releaseConnection();
        }
        return pk;
    }

    public void ejbPostCreate(java.lang.String txt) throws javax.ejb.CreateException {
        //TODO implement ejbPostCreate
    }
}
