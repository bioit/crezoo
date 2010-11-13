package org.tgdb.model.strain.allele;

import org.tgdb.TgDbCaller;
import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;

public interface StrainAlleleRemoteHome extends EJBHome {
    
    StrainAlleleRemote findByPrimaryKey(Integer key)  throws FinderException, RemoteException;

    StrainAlleleRemote findByMGI(String mgiid)  throws FinderException, RemoteException;

    StrainAlleleRemote findByNAME(String name)  throws FinderException, RemoteException;

    Collection findByStrain(int strainid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    Collection findByMgiid(String mgiid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    Collection findByName(String name, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    Collection findBySymbol(String symbol, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findUnassignedAlleles(int model, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findAll(TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    StrainAlleleRemote create(int id, String symbol, String name, TgDbCaller caller) throws javax.ejb.CreateException, java.rmi.RemoteException;
        
}
