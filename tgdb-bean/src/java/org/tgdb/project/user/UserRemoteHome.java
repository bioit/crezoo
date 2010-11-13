
package org.tgdb.project.user;

import java.util.Collection;
import org.tgdb.TgDbCaller;


/**
 * This is the home interface for User enterprise bean.
 */
public interface UserRemoteHome extends javax.ejb.EJBHome {
    
    Collection findByDistinguish(String distinguish, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    UserRemote findByPrimaryKey(Integer key)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    UserRemote create(int id, java.lang.String usr, java.lang.String pwd, java.lang.String name, java.lang.String status) throws javax.ejb.CreateException, java.rmi.RemoteException;

    UserRemote findByUsr(java.lang.String usr) throws javax.ejb.FinderException, java.rmi.RemoteException;

    int getNumberOfUsers() throws java.rmi.RemoteException;

    UserRemote findByUserAndPwd(java.lang.String usr, java.lang.String pwd) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findAll(TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;    
}
