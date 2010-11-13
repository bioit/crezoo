
package org.tgdb.genome.integrationcopy;

import java.rmi.RemoteException;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;


/**
 * This is the home interface for IntegrationCopy enterprise bean.
 */
public interface IntegrationCopyRemoteHome extends EJBHome {
    
    IntegrationCopyRemote findByPrimaryKey(Integer key)  throws FinderException, RemoteException;
    
    IntegrationCopyRemote create(int iscmid, java.lang.String isite, java.lang.String cnumber) throws javax.ejb.CreateException, java.rmi.RemoteException;
    
    java.util.Collection findByModel(int eid) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
}
