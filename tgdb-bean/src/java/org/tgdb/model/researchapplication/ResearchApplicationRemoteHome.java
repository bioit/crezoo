
package org.tgdb.model.researchapplication;

import org.tgdb.TgDbCaller;
import org.tgdb.search.Keyword;
import java.util.Collection;


/**
 * This is the home interface for ResearchApplication enterprise bean.
 */
public interface ResearchApplicationRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.model.researchapplication.ResearchApplicationRemote findByPrimaryKey(Integer key)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByProject(int pid) throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.model.researchapplication.ResearchApplicationRemote create(java.lang.String name, java.lang.String comm, int pid, int raid, TgDbCaller caller) throws javax.ejb.CreateException, java.rmi.RemoteException;

    Collection findByName(String name) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findByKeyword(Keyword keyword, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
}
