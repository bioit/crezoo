/*
 * FileDTO.java
 *
 * Created on September 15, 2005, 5:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.resource.resourcemanager;

import org.tgdb.resource.file.FileRemote;


/**
 * Data transfer object for a file
 * @author heto
 */
public class FileDTO {
    
    private int fileId;
    private String name;
    private String comm;
    private String mimetype;
    private String filetype;
    private String user;
    private java.sql.Date ts;
    private int size;
    
    public FileDTO()
    {
    }
    
    /**
     * Creates a new instance of FileDTO
     * @param file The file bean
     */
    public FileDTO(FileRemote file) 
    {
        try
        {
            fileId = file.getFileId();
            name = file.getName();
            comm = file.getComm();
            size = file.getSize();
            mimetype = file.getMimeType();
            filetype = file.getFileType();
            ts  = file.getTs();
            user = file.getUser().getUsr();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Returns the file id
     * @return The file id
     */
    public int getFileId() {
        return fileId;
    }

    /**
     * Returns the file name
     * @return The file name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the comment for the file
     * @return The comment for the file
     */
    public String getComm() {
        return comm;
    }
    
    /**
     * Returns the username of the user that made the last modifications on the file
     * @return The username of the user that made the last modifications on the file
     */
    public String getUser() {
        return user;
    }
    
    /**
     * Returns the size of the file
     * @return The size of the file
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the mime type for the file
     * @return The mime type for the file
     */
    public String getMimeType() {
        return mimetype;
    }

    /**
     * Returns the file type
     * @return The file type
     */
    public String getFileType() {
        return filetype;
    }

    /**
     * Returns the date for the last modification of the file
     * @return The date for the last modification of the file
     */
    public String getTs() {
        return ts.toString();
    }
    
}
