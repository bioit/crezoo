
package org.tgdb.resource.file;

import org.tgdb.TgDbCaller;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.user.UserRemote;



/**
 * This is the business interface for FileManager enterprise bean.
 */
public interface FileRemoteBusiness {
    byte[] getData() throws java.rmi.RemoteException;

    void setData(byte[] data) throws java.rmi.RemoteException;

    void setComm(java.lang.String comm) throws java.rmi.RemoteException;

    String getComm() throws java.rmi.RemoteException;

    void setName(java.lang.String name) throws java.rmi.RemoteException;

    String getName() throws java.rmi.RemoteException;

    int getFileId() throws java.rmi.RemoteException;

    int getSize() throws java.rmi.RemoteException;

    java.sql.Date getTs() throws java.rmi.RemoteException;

    void setFileType(java.lang.String fileType) throws java.rmi.RemoteException;

    String getFileType() throws java.rmi.RemoteException;

    void setMimeType(java.lang.String mimeType) throws java.rmi.RemoteException;

    String getMimeType() throws java.rmi.RemoteException;

    UserRemote getUser() throws java.rmi.RemoteException;

    ProjectRemote getProject() throws java.rmi.RemoteException;

    void setCaller(TgDbCaller caller) throws java.rmi.RemoteException;
    
    
}
