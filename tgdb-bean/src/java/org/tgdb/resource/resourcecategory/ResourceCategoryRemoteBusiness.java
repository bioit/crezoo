
package org.tgdb.resource.resourcecategory;

import org.tgdb.TgDbCaller;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.user.UserRemote;
import java.util.Collection;


/**
 * This is the business interface for ResourceCategory enterprise bean.
 */
public interface ResourceCategoryRemoteBusiness {
    java.sql.Date getTimestamp() throws java.rmi.RemoteException;

    String getName() throws java.rmi.RemoteException;

    String getComment() throws java.rmi.RemoteException;

    int getResourceCategoryId() throws java.rmi.RemoteException;

    ProjectRemote getProject() throws java.rmi.RemoteException;

    UserRemote getUser() throws java.rmi.RemoteException;

    ResourceCategoryRemote getParentCategory() throws java.rmi.RemoteException;

    void setName(java.lang.String name) throws java.rmi.RemoteException;

    void setComment(java.lang.String comm) throws java.rmi.RemoteException;

    void setParentCategory(int parentId) throws java.rmi.RemoteException;

    void setCaller(TgDbCaller caller) throws java.rmi.RemoteException;

    Collection getResources() throws java.rmi.RemoteException;

    boolean isRoot() throws java.rmi.RemoteException;

    int getNumberOfResources() throws java.rmi.RemoteException;
    
}
