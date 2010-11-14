package org.tgdb.model.strain.strain;

import org.tgdb.TgDbCaller;
import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;


public interface StrainRemoteHome extends EJBHome {
    StrainRemote findByPrimaryKey(Integer key)  throws FinderException, RemoteException;

    StrainRemote findByNAME(String name)  throws FinderException, RemoteException;

    StrainRemote create(int strainid, String designation, TgDbCaller caller) throws javax.ejb.CreateException, java.rmi.RemoteException;

    Collection findByProject(int pid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findByModel(int model, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findConnectedToModels(TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findUnassigned(int model, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
//    Collection findByMgiid(String mgiid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
}
