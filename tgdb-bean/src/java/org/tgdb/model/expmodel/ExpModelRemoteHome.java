
package org.tgdb.model.expmodel;

import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbCaller;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemote;
import org.tgdb.search.Keyword;
import java.util.Collection;


public interface ExpModelRemoteHome extends javax.ejb.EJBHome {

    org.tgdb.model.expmodel.ExpModelRemote findByPrimaryKey(java.lang.Integer eid)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.model.expmodel.ExpModelRemote findByNAME(String name)  throws javax.ejb.FinderException, java.rmi.RemoteException;

    org.tgdb.model.expmodel.ExpModelRemote create(int eid, java.lang.String identity, SamplingUnitRemote samplingUnit, TgDbCaller caller) throws javax.ejb.CreateException, java.rmi.RemoteException;

    java.util.Collection findBySamplingUnit(int suid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByStrain(int strain, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findByGene(int gaid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findByPromoter(int gaid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    Collection findByResearchApplication(int raid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByKeyword(Keyword keyword, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByFormDataManager(FormDataManager fdm, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;

    java.util.Collection findByIMSRSubmission(int suid, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    java.util.Collection findByModelsThatNeedDissUpdate(TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    java.util.Collection findByFormDataManagerForDissUpdate(FormDataManager fdm, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    java.util.Collection findByBackrossingListGeneration(TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    java.util.Collection findByWebServiceRequest() throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    java.util.Collection findByWebServiceKeywordRequest(String keyword) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
    Collection findByStrainAllele(int strain_allele, TgDbCaller caller) throws javax.ejb.FinderException, java.rmi.RemoteException;
    
}
