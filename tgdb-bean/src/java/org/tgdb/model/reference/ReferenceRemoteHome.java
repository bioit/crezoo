package org.tgdb.model.reference;

import org.tgdb.TgDbCaller;

public interface ReferenceRemoteHome extends javax.ejb.EJBHome {
    
    org.tgdb.model.reference.ReferenceRemote findByPrimaryKey(java.lang.Integer key)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.model.reference.ReferenceRemote create(int refid, int pid, java.lang.String name, String comm, TgDbCaller caller) throws javax.ejb.CreateException, java.rmi.RemoteException;

    java.util.Collection findByModel(int eid) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByModelAndPrimary(int eid, boolean primary) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
}
