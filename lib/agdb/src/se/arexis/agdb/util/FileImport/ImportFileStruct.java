/*
 * ImportFileStruct.java
 *
 * Created on December 10, 2004, 1:13 PM
 */

package se.arexis.agdb.util.FileImport;

/**
 * A class that contains information about the import file names.
 *
 * @author heto
 */
public class ImportFileStruct 
{
    /** The name of the file */
    public String name;
    /** The filetype that can be imported */
    public String type;
    /** The file id */
    public String ifid;
    
    public FileHeader hdr;
        
        
    /**
     * Create a new ImportFileStruct object.
     * The attributed will be assigned to the public variables. 
     *
     * @param name The name of the file
     * @param type The file type
     * @param ifid The import file id
     */        
    public ImportFileStruct(String name,String type, String ifid)
    {
        this.name = name;
        this.type = type;
        this.ifid = ifid;
    }   
    
    public ImportFileStruct(String name, String ifid, FileHeader hdr)
    {
        this.name = name;
        this.ifid = ifid;
        this.hdr = hdr;
        
        type = hdr.objectTypeName();
    }
    
    public ImportFileStruct(ImportFileStruct filestruct)
    {
        this.name = filestruct.name;
        this.type = filestruct.type;
        this.ifid = filestruct.ifid;
    }
}