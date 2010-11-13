
package org.tgdb.simplelog;


/**
 * This is the home interface for SimpleLog enterprise bean.
 */
public interface SimpleLogRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.simplelog.SimpleLogRemote findByPrimaryKey(java.lang.Integer key)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.simplelog.SimpleLogRemote create(java.lang.String txt) throws javax.ejb.CreateException, java.rmi.RemoteException;
    
    
}
