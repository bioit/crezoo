
package org.tgdb.resource.link;

import org.tgdb.TgDbCaller;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.user.UserRemote;


/**
 * This is the business interface for Link enterprise bean.
 */
public interface LinkRemoteBusiness {
    String getName() throws java.rmi.RemoteException;

    String getUrl() throws java.rmi.RemoteException;

    String getComment() throws java.rmi.RemoteException;

    int getLinkId() throws java.rmi.RemoteException;

    UserRemote getUser() throws java.rmi.RemoteException;

    java.sql.Date getTs() throws java.rmi.RemoteException;

    void setName(java.lang.String name) throws java.rmi.RemoteException;

    void setComment(java.lang.String comm) throws java.rmi.RemoteException;

    void setUrl(java.lang.String url) throws java.rmi.RemoteException;

    ProjectRemote getProject() throws java.rmi.RemoteException;

    void setCaller(TgDbCaller caller) throws java.rmi.RemoteException;
    
}
