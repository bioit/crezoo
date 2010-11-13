
package org.tgdb.model.reference;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.project.user.UserRemote;
import org.tgdb.resource.file.FileRemote;
import org.tgdb.resource.link.LinkRemote;
import java.sql.Date;


/**
 * This is the business interface for Reference enterprise bean.
 */
public interface ReferenceRemoteBusiness {
    int getRefid() throws java.rmi.RemoteException;

    String getPubmed() throws java.rmi.RemoteException;

    void setPubmed(String pubmed) throws java.rmi.RemoteException;

    boolean isPrimary() throws java.rmi.RemoteException;

    void setPrimary(boolean primary) throws java.rmi.RemoteException;

    String getName() throws java.rmi.RemoteException;

    void setName(java.lang.String name) throws java.rmi.RemoteException;

    String getComm() throws java.rmi.RemoteException;

    void setComm(java.lang.String comm) throws java.rmi.RemoteException;

    Date getTs() throws java.rmi.RemoteException;
    
    FileRemote getFile()  throws java.rmi.RemoteException;
    
    LinkRemote getLink()  throws java.rmi.RemoteException;
    
    void setFile(FileRemote file)  throws ApplicationException, java.rmi.RemoteException;
    
    void setLink(LinkRemote link)  throws ApplicationException, java.rmi.RemoteException;    

    void setCaller(TgDbCaller caller) throws java.rmi.RemoteException;

    UserRemote getUser() throws ApplicationException, java.rmi.RemoteException;
}
