/*
 * FileDataObject.java
 *
 * Created on December 6, 2005, 1:48 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.frame.io;

/**
 *
 * @author lami
 */
public class FileDataObject {
    private byte[] data;
    private String mimeType;
    private String fileName;
    
    /** Creates a new instance of FileDataObject */
    public FileDataObject(byte[] data, String fileName, String mimeType) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
    public String getMimeType() {
        return mimeType;
    }    

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public int getDataSize() {
        return data.length;
    }
}
