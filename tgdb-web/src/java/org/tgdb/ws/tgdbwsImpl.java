package org.tgdb.ws;

import org.tgdb.model.modelmanager.*;
import org.tgdb.dtos.*;

public class tgdbwsImpl implements tgdbwsSEI {
    
    
    public java.lang.String getProjectName() throws java.rmi.RemoteException {
        ModelManagerRemote modelManager = lookupTgDbModelManagerBean();
        return modelManager.getProjectName();
    }
    
    public TgDbModelDTO[] getTgDbMiceDTO() throws java.rmi.RemoteException {
        ModelManagerRemote modelManager = lookupTgDbModelManagerBean();
        try {
            TgDbModelDTO[] models = modelManager.getTgDbMiceDTO();
            return models;
        } catch (org.tgdb.exceptions.ApplicationException e){
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public TgDbModelDTO[] getTgDbMiceDTOByKey(String key) throws java.rmi.RemoteException {
        ModelManagerRemote modelManager = lookupTgDbModelManagerBean();
        try {
            TgDbModelDTO[] models = modelManager.getTgDbMiceDTOByKey(key);
            return models;
        } catch (org.tgdb.exceptions.ApplicationException e){
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public TgDbGeneDTO[] getTgDbGenesByModel(int eid) throws java.rmi.RemoteException { 
        ModelManagerRemote modelManager = lookupTgDbModelManagerBean();
        try {
            TgDbGeneDTO[] genes = modelManager.getTgDbGenesByModel(eid);
            return genes;
        } catch (org.tgdb.exceptions.ApplicationException e){
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public TgDbAvailabilityDTO[] getTgDbAvailabilityByModel(int eid) throws java.rmi.RemoteException {
        ModelManagerRemote modelManager = lookupTgDbModelManagerBean();
        try {
            TgDbAvailabilityDTO[] avs = modelManager.getTgDbAvailabilityByModel(eid);
            return avs;
        } catch (org.tgdb.exceptions.ApplicationException e){
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public TgDbBackgroundDTO[] getTgDbBackgroundByModel(int eid) throws java.rmi.RemoteException {
        ModelManagerRemote modelManager = lookupTgDbModelManagerBean();
        try {
            TgDbBackgroundDTO[] backs = modelManager.getTgDbBackgroundByModel(eid);
            return backs;
        } catch (org.tgdb.exceptions.ApplicationException e){
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private ModelManagerRemote lookupTgDbModelManagerBean() {
        try {
            javax.naming.Context c = new javax.naming.InitialContext();
            Object remote = c.lookup("java:comp/env/ejb/CreZOOModelManagerBean");
            org.tgdb.model.modelmanager.ModelManagerRemoteHome rv = (org.tgdb.model.modelmanager.ModelManagerRemoteHome) javax.rmi.PortableRemoteObject.narrow(remote, org.tgdb.model.modelmanager.ModelManagerRemoteHome.class);
            return rv.create();
        }
        catch(javax.naming.NamingException ne) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE,"exception caught" ,ne);
            throw new RuntimeException(ne);
        }
        catch(javax.ejb.CreateException ce) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE,"exception caught" ,ce);
            throw new RuntimeException(ce);
        }
        catch(java.rmi.RemoteException re) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE,"exception caught" ,re);
            throw new RuntimeException(re);
        }
    }
    
}
