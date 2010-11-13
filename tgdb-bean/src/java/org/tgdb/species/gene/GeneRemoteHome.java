
package org.tgdb.species.gene;

import org.tgdb.TgDbCaller;
import org.tgdb.search.Keyword;
import java.util.Collection;

public interface GeneRemoteHome extends javax.ejb.EJBHome {
    
    org.tgdb.species.gene.GeneRemote findByPrimaryKey(java.lang.Integer key)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.species.gene.GeneRemote findByNAME(String name)  throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    java.util.Collection findByMgiid(java.lang.String mgiid)  throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    java.util.Collection findBySymbol(java.lang.String symbol)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    //org.tgdb.species.gene.GeneRemote create(int gaid, java.lang.String name, String comm, ProjectRemote project, TgDbCaller caller) throws javax.ejb.CreateException, java.rmi.RemoteException;

    java.util.Collection findByModel(int eid) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByDistinguish(String distinguish) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByModelAndDistinguish(int eid, String distinguish) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findGenesNotAssignedToAllele(int aid, String distinguish) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findPromoters(int eid) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByAllele(int aid) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findByName(java.lang.String name) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    Collection findByNameCaseSensitive(java.lang.String name) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByKeyword(Keyword keyword) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByProject(int pid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.species.gene.GeneRemote create(int gaid, java.lang.String name, String genesymbol, int cid, TgDbCaller caller) throws javax.ejb.CreateException, java.rmi.RemoteException;
    
    java.util.Collection findUnassignedGenes(int eid, int pid, String distinguish) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    java.util.Collection findUnassignedGenesForTransgenic(int eid, int strainid, int pid) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
}
