
package org.tgdb.samplingunit.samplingunitmanager;
import org.tgdb.frame.PageManager;
import org.tgdb.frame.io.FileDataObject;
import org.tgdb.form.FormDataManager;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.IndividualNotFoundException;
import org.tgdb.project.ParamDataObject;
import java.util.Collection;

/**
 * This is the business interface for SamplingUnitManager enterprise bean.
 */
public interface SamplingUnitManagerRemoteBusiness {
    Collection getSamplingUnits(int pid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
    
    int createSamplingUnit(String name, String comm, String status, int sid, int pid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
    
    Collection getSamplingUnits(TgDbCaller caller, PageManager pageManager, ParamDataObject qdo, int sid) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;
    
    Collection getSamplingUnitHistory(TgDbCaller caller, int suid) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    SamplingUnitDTO getSamplingUnit(TgDbCaller caller, int suid) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void updateSamplingUnit(int suid, java.lang.String name, java.lang.String comm, java.lang.String status, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void removeSamplingUnit(int suid, TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException ;

    Collection getSpeciesForProject(int pid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    int getNumberOfSamplingUnits(int sid, TgDbCaller caller, ParamDataObject qdo) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    int getNumberOfGroupings(int suid) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    Collection getSamplingUnits(int pid, int sid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    SamplingUnitDTO getDefaultSamplingUnit(org.tgdb.TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getExperimentalObjects(int suid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    java.util.Collection getResourceTreeCollection(int suid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    void addResource(java.lang.String type, int category, int project, java.lang.String name, java.lang.String comm, FileDataObject fileData, org.tgdb.TgDbCaller caller, String url, int suid) throws ApplicationException, java.rmi.RemoteException;

    /**
     * Create or Update an individual. If the individual does not exist, create 
     * it. If the individual does exists, update the values. 
     * This is used in the import system.
     * 
     * @param suid is the sampling unit id
     * @param caller
     * @param identity
     * @param alias
     * @param sex
     * @param father
     * @param mother
     * @param birthdate
     * @param comm
     * @return iid
     * @throws org.tgdb.exceptions.ApplicationException
     */
    void addLinkResource(String name, String comm, String url, int category, int suid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
}
