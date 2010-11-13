
package org.tgdb.project.project;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.IllegalValueException;
import org.tgdb.TgDbCaller;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemote;
import org.tgdb.species.species.SpeciesRemote;
import java.util.Collection;


/**
 * This is the business interface for Project enterprise bean.
 */
public interface ProjectRemoteBusiness {
    int getPid() throws java.rmi.RemoteException;

    String getName() throws java.rmi.RemoteException;

    void setName(java.lang.String name) throws java.rmi.RemoteException;

    String getComm() throws java.rmi.RemoteException;

    void setComm(java.lang.String comm) throws java.rmi.RemoteException;

    String getStatus() throws java.rmi.RemoteException;

    void setStatus(java.lang.String status) throws java.rmi.RemoteException, IllegalValueException, ApplicationException;

    void enable() throws java.rmi.RemoteException;

    void disable() throws java.rmi.RemoteException;

    String[] getAllNames() throws java.rmi.RemoteException;

    

    Collection getSecurityPrinciples() throws java.rmi.RemoteException;

    String getCaller() throws java.rmi.RemoteException;

    void setCaller(TgDbCaller usr) throws java.rmi.RemoteException;

    Collection getRoles() throws java.rmi.RemoteException;

    Collection getLinks() throws java.rmi.RemoteException;

    Collection getFiles() throws java.rmi.RemoteException;

    Collection getSpecies() throws ApplicationException, java.rmi.RemoteException;

    void addSamplingUnit(org.tgdb.samplingunit.samplingunit.SamplingUnitRemote su) throws ApplicationException, java.rmi.RemoteException;

    void addSpecies(org.tgdb.species.species.SpeciesRemote spc) throws ApplicationException, java.rmi.RemoteException;

    Collection getResources() throws java.rmi.RemoteException;

    Collection getResourceCategories() throws java.rmi.RemoteException;    

    Collection getSamplingUnits() throws ApplicationException, java.rmi.RemoteException;

    void removeSpecies(SpeciesRemote species) throws java.rmi.RemoteException;

    void removeSamplingUnit(SamplingUnitRemote su) throws java.rmi.RemoteException;
}
