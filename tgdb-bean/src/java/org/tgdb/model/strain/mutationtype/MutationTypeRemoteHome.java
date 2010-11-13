
package org.tgdb.model.strain.mutationtype;

import org.tgdb.TgDbCaller;
import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;


/**
 * This is the home interface for MutationType enterprise bean.
 */
public interface MutationTypeRemoteHome extends EJBHome
{
    MutationTypeRemote create(int id, String name, TgDbCaller caller) throws javax.ejb.CreateException, java.rmi.RemoteException;
    
    MutationTypeRemote findByPrimaryKey(Integer key)  throws FinderException, RemoteException;

    Collection findByProject(int pid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findByStrainAllele(int stain_allele_id, int strain_id, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    Collection findByStrainAlleleUnassignment(int strainalleleid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    Collection findByAbbreviation(String abbreviation, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
}
