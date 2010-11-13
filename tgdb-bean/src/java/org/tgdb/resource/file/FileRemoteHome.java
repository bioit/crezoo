
package org.tgdb.resource.file;

import org.tgdb.TgDbCaller;


/**
 * This is the home interface for FileManager enterprise bean.
 */
public interface FileRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.resource.file.FileRemote findByPrimaryKey(java.lang.Integer fileid)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.resource.file.FileRemote create(Integer fileid, java.lang.String name, String comm, String mimeType, String fileType, TgDbCaller caller) throws javax.ejb.CreateException, java.rmi.RemoteException;

    java.util.Collection findAll() throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByProject(int pid) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findBySamplingUnit(int suid) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByGeneticModification(int gmid) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    java.util.Collection findByExpressionModel(int exid) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    
}
