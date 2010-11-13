
package org.tgdb.export.filter;

import org.tgdb.frame.PageManager;
import org.tgdb.TgDbCaller;
import org.tgdb.project.ParamDataObject;



/**
 * This is the home interface for GQLFilter enterprise bean.
 */
public interface GQLFilterRemoteHome extends javax.ejb.EJBHome {    
    org.tgdb.export.filter.GQLFilterRemote create(int fid, String name, String comm, String expression, int sid, int pid, TgDbCaller caller)  throws javax.ejb.CreateException, java.rmi.RemoteException;

    org.tgdb.export.filter.GQLFilterRemote findByPrimaryKey(Integer key) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByQuery(ParamDataObject pdo, PageManager pageManager) throws javax.ejb.FinderException, java.rmi.RemoteException;

    int getNumberOfFilters(ParamDataObject pdo) throws java.rmi.RemoteException;
    
    
}
