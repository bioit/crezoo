package org.tgdb.ws;

import org.tgdb.dtos.*;

public interface tgdbwsSEI extends java.rmi.Remote {
    
    public java.lang.String getProjectName() throws java.rmi.RemoteException;

    public TgDbModelDTO[] getTgDbMiceDTO() throws java.rmi.RemoteException;
    
    public TgDbModelDTO[] getTgDbMiceDTOByKey(String key) throws java.rmi.RemoteException;

    public TgDbGeneDTO[] getTgDbGenesByModel(int eid) throws java.rmi.RemoteException;

    public TgDbAvailabilityDTO[] getTgDbAvailabilityByModel(int eid) throws java.rmi.RemoteException;

    public TgDbBackgroundDTO[] getTgDbBackgroundByModel(int eid) throws java.rmi.RemoteException;
    
}
