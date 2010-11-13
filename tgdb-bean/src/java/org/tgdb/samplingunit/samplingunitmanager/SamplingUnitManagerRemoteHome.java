
package org.tgdb.samplingunit.samplingunitmanager;


/**
 * This is the home interface for SamplingUnitManager enterprise bean.
 */
public interface SamplingUnitManagerRemoteHome extends javax.ejb.EJBHome {
    
    
    
    /**
     *
     */
    org.tgdb.samplingunit.samplingunitmanager.SamplingUnitManagerRemote create()  throws javax.ejb.CreateException, java.rmi.RemoteException;
    
    
}
