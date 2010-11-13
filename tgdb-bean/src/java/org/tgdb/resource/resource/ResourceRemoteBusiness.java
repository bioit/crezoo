
package org.tgdb.resource.resource;

import org.tgdb.TgDbCaller;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.user.UserRemote;
import org.tgdb.resource.file.FileRemote;
import org.tgdb.resource.link.LinkRemote;
import org.tgdb.resource.resourcecategory.ResourceCategoryRemote;



/**
 * This is the business interface for Resource enterprise bean.
 */
public interface ResourceRemoteBusiness {
    void setCaller(TgDbCaller caller) throws java.rmi.RemoteException;

    ProjectRemote getProject() throws java.rmi.RemoteException;

    ResourceCategoryRemote getResourceCategory() throws java.rmi.RemoteException;

    UserRemote getUser() throws java.rmi.RemoteException;

    String getName() throws java.rmi.RemoteException;

    String getComment() throws java.rmi.RemoteException;

    void setName(java.lang.String name) throws java.rmi.RemoteException;

    void setComment(java.lang.String comm) throws java.rmi.RemoteException;

    FileRemote getFile() throws java.rmi.RemoteException;

    LinkRemote getLink() throws java.rmi.RemoteException;

    java.sql.Date getTimestamp() throws java.rmi.RemoteException;

    int getResourceId() throws java.rmi.RemoteException;

    void setFile(int fileId) throws java.rmi.RemoteException;

    void setLink(int linkId) throws java.rmi.RemoteException;

    void setResourceCategory(int categoryId) throws java.rmi.RemoteException;

    String getResourceLink() throws java.rmi.RemoteException;

    String getResourceType() throws java.rmi.RemoteException;
    
}
