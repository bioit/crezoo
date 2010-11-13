package org.tgdb.project.user;

import org.tgdb.exceptions.ApplicationException;
import org.tgdb.resource.link.LinkRemote;
import java.util.Collection;

public interface UserRemoteBusiness {
    String getDistinguish() throws java.rmi.RemoteException;

    void setDistinguish(String distinguish) throws java.rmi.RemoteException;

    void setStatus(java.lang.String status) throws java.rmi.RemoteException;

    String getStatus() throws java.rmi.RemoteException;

    void setName(java.lang.String name) throws java.rmi.RemoteException;

    String getName() throws java.rmi.RemoteException;

    void setPwd(java.lang.String pwd) throws java.rmi.RemoteException;

    String getPwd() throws java.rmi.RemoteException;

    void setUsr(java.lang.String usr) throws java.rmi.RemoteException;

    String getUsr() throws java.rmi.RemoteException;

    int getId() throws java.rmi.RemoteException;

    Collection getSecurityPrinciples() throws java.rmi.RemoteException;

    String getGroupName() throws java.rmi.RemoteException;

    void setGroupName(java.lang.String groupName) throws java.rmi.RemoteException;

    String getGroupAddress() throws java.rmi.RemoteException;

    void setGroupAddress(java.lang.String groupAddress) throws java.rmi.RemoteException;

    String getGroupPhone() throws java.rmi.RemoteException;

    void setGroupPhone(java.lang.String groupPhone) throws java.rmi.RemoteException;

    LinkRemote getGroupLink() throws java.rmi.RemoteException;

    void setGroupLink(int groupLink) throws java.rmi.RemoteException;

    LinkRemote getUserLink() throws java.rmi.RemoteException;

    void setUserLink(int userLink) throws java.rmi.RemoteException;

    String getEmail() throws java.rmi.RemoteException;

    void setEmail(java.lang.String email) throws java.rmi.RemoteException;

    boolean isAdmin() throws java.rmi.RemoteException;

    /**
     * Set the flag to indicate that this user is permitted to 
     * admin the server.
     */
    void setAdmin(boolean admin) throws ApplicationException, java.rmi.RemoteException;    
}
