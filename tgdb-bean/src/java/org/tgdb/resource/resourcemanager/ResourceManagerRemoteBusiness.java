
package org.tgdb.resource.resourcemanager;

import org.tgdb.frame.io.FileDataObject;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.resource.file.FileRemote;
import org.tgdb.resource.link.LinkRemote;
import org.tgdb.resource.resource.ResourceRemote;
import java.util.Collection;


/**
 * This is the business interface for ResourceManager enterprise bean.
 */
public interface ResourceManagerRemoteBusiness {
    Collection getSamplingUnitFiles(int suid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    Collection getSamplingUnitLinks(int suid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    LinkRemote createLink(java.lang.String name, java.lang.String comm, java.lang.String url, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    LinkDTO getLink(int linkid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void updateLink(int linkid, java.lang.String name, java.lang.String url, java.lang.String comm, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    int addLinkToSamplingUnit(int suid, TgDbCaller caller, String name, String url, String comm) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void removeLink(int linkid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    FileRemote saveFile(String name, String comm, org.tgdb.TgDbCaller caller, FileDataObject fileData) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;        

    int addFileToSamplingUnit(int suid, TgDbCaller caller, FileDataObject fileData, String name, String comm) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    FileDTO getFile(int fileid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void updateFile(int fileid, java.lang.String name, java.lang.String comm, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void removeFile(int fileid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    Collection getResources(java.util.Collection categories, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void createResourceCategory(int project, java.lang.String name, java.lang.String comm, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    Collection getResourceCategories(int pid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    ResourceCategoryDTO getResourceCategory(int categoryId, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void updateResourceCategory(int categoryId, String name, String comm, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void removeResourceCategory(int categoryId, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    ResourceRemote createResource(int pid, java.lang.String name, java.lang.String comm, int fileId, int linkId, int catId, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    ResourceDTO getResource(int resourceId, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void updateResource(int resourceId, java.lang.String name, java.lang.String comm, String url, int catid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void removeResource(int resourceId, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    /**
     * Get the first row of a file
     */
    java.lang.String getFirstRow(int fileid, TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    /**
     * Get the file Remote object for this file id
     * 
     * @param fileid
     * @param caller
     * @return 
     */
    org.tgdb.resource.file.FileRemote getFileObject(int fileid, TgDbCaller caller) throws java.rmi.RemoteException, ApplicationException;

    /**
     * Returns all files in the database
     * 
     * @return The files in the database
     * @throws org.tgdb.exceptions.ApplicationException If the files could not be retrieved
     */
    java.util.Collection getAllFiles(TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    /**
     * Saves a file in the database
     * 
     * @param name The name of the file
     * @param contentType The content type for the file
     * @param data The data in the file
     * @return The id of the saved file
     * @throws org.tgdb.exceptions.ApplicationException If the file could not be stored
     */
    int saveFile(String name, String contentType, byte[] data, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    /**
     * 
     * Get all categories from the ResourceRemote collection
     * Returns a collection of ResourceCategoryRemote
     */
    java.util.Collection getCategoriesFromResources(Collection resources) throws java.rmi.RemoteException, ApplicationException;

    /**
     * Get the resource tree from a collection of resources.
     * 
     * @param resources is a collection of ResourceRemote objects connected
     * to an object.
     * @param caller is the caller of the method
     * @return 
     * @throws org.tgdb.exceptions.ApplicationException
     */
    java.util.Collection getResourceTreeCollection(Collection resources, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    java.io.File getDiskFile(int fileid, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;

    org.tgdb.resource.resource.ResourceRemote createResource(LinkRemote link, int catId, TgDbCaller caller) throws ApplicationException, java.rmi.RemoteException;
    
    
}
