/*
 * ImportIndividual.java
 *
 * Created on December 16, 2002, 5:37 PM
 *
 * $Log$
 * Revision 1.13  2005/05/17 08:09:00  heto
 * *** empty log message ***
 *
 * Revision 1.12  2005/04/04 13:58:09  heto
 * Commit before merging ant-scripts
 *
 * Revision 1.11  2005/01/31 12:59:04  heto
 * Making stronger separation of the import modules.
 *
 * Revision 1.10  2004/12/14 08:31:07  heto
 * Changed interface for transfering information between the servlet, importprocess and import modules.
 *
 * Comments added
 *
 * Modules are now more independent from the import process.
 *
 * Revision 1.9  2004/04/23 09:58:59  wali
 * another setParameter added.
 *
 * Revision 1.8  2004/03/25 17:06:06  heto
 * Fixing debug messages.
 *
 * Revision 1.7  2004/03/19 10:37:13  heto
 * Added debug messages
 *
 * Revision 1.6  2003/11/05 07:45:31  heto
 * Renamed variable
 *
 * Revision 1.5  2003/05/15 06:37:53  heto
 * Changed the return type from void to boolean for check and imp
 *
 * Revision 1.4  2003/05/09 14:49:48  heto
 * Check process is integrated to the importProcess
 *
 * Revision 1.3  2003/05/02 07:58:45  heto
 * Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
 * Modified configuration and source files according to package change.
 *
 * Revision 1.2  2003/01/15 09:55:36  heto
 * Comments added
 *
 */

package se.arexis.agdb.util.FileImport;

import java.io.File;
import se.arexis.agdb.util.*;
import java.sql.*;
import java.util.*;

/**
 * An abstract class for all "Import" objects. This sets the relevant 
 * parameters to the framework for importing files. 
 *
 * All import objects must advertise its capabilities in the constructor. 
 * Valid combinations are:
 * (( CREATE | UPDATE | CREATE_OR_UPDATE ) | CREATE & UPDATE & CREATE_OR_UPDATE) 
 * ( SUID | SPECIESID ) [ LEVEL ] [ NAME ]
 *
 * Either one of CREATE,UPDATE or CREATE_OR_UPDATE or all of the update methods
 * must be supported!!
 *
 * NAME is designed to sets that don't have the name in the import file, but it 
 * is possible that this could be used in other formats (not confirmed)
 *
 * LEVEL is the user level (then writing this, only implemented in genotypes)
 *
 * The capabilties are used to present the files and format under the properties
 * page before the check. It is up to the import module to actually do what it
 * is saying it is capable of. 
 *
 *
 * @author  heto
 */
public abstract class ImportData
{
    /** 
     * Tells the import process the order to import files.
     */
    protected ArrayList<Dependency> dependency = new ArrayList();
    
    public ArrayList<Dependency> getDependency()
    {
        return dependency;
    }
    
    // Capabilities of import objects
    protected boolean CREATE = false;
    protected boolean UPDATE = false;
    protected boolean CREATE_OR_UPDATE = false;
    protected boolean LEVEL = false;
    protected boolean NAME = false;
    protected boolean SUID = false;
    protected boolean SPECIESID = false;
    
    /** Return the compability of the object. This is used by the
     * import process.
     */
    public boolean compability(String comp)
    {
        if (comp.equals("CREATE"))
            return CREATE;
        else if (comp.equals("UPDATE"))
            return UPDATE;
        else if (comp.equals("CREATE_OR_UPDATE"))
            return CREATE_OR_UPDATE;
        else if (comp.equals("LEVEL"))
            return LEVEL;
        else if (comp.equals("NAME"))
            return NAME;
        else if (comp.equals("SUID"))
            return SUID;
        else if (comp.equals("SPECIESID"))
            return SPECIESID;
        else
            return false;
    }
            
    /**
     * Test database object used for testing existing data and dependencies.
     */
    protected DataObject db;
   
    /**
     * Set the preferences for this import object.
     */
    public void setPrefs(Prefs prefs,DataObject db) throws Exception
    {
        Errors.logInfo("ImportData.setPrefs(...) started");
        this.pid            = Integer.valueOf(prefs.pid).toString();
        this.isid           = Integer.valueOf(prefs.isid).toString();
        this.ifid           = Integer.valueOf(prefs.ifid).toString();
        this.upPath         = prefs.upPath;
        this.systemFileName = prefs.fileName;
        this.updateMethod   = prefs.updateMethod;
        this.connection     = prefs.connection;
        this.conn_viss      = prefs.connViss;
        this.sampleUnitId   = prefs.sampleUnitId;
        this.speciesId      = prefs.speciesId;
        this.userId         = prefs.userId;
        
        this.db=db;
        
        debug();
        
        if (!updateMethod.equals("CREATE") 
            && !updateMethod.equals("UPDATE") 
            && !updateMethod.equals("CREATE_OR_UPDATE"))
            throw new Exception("UpdateMethod is not CREATE, UPDATE, CREATE_OR_UPDATE");
        
        Errors.logInfo("ImportData.setPrefs(...) ended");
    }
    
   
    
    /**
     * Prints a debug information about the knowledge of this import 
     * module.
     */
    public void debug()
    {
        Errors.logInfo("ImportData.debug() started");
        
        if (connection == null)
            Errors.logDebug("connection is null");
        else
            Errors.logDebug("connection is not null");
        
        if (conn_viss == null)
            Errors.logDebug("conn_viss is null");
        else
            Errors.logDebug("conn_viss is not null");
        
        if (pid == null)
            Errors.logDebug("pid is null");
        else
            Errors.logDebug("pid="+pid);
        
        if (isid == null)
            Errors.logDebug("isid is null");
        else
            Errors.logDebug("isid="+isid);
        
        if (upPath == null)
            Errors.logDebug("upPath is null");
        else
            Errors.logDebug("upPath="+upPath);
        
        Errors.logDebug("sampleUnitId="+sampleUnitId);
        
        Errors.logDebug("speciesId="+speciesId);
        
        if (userId == null)
            Errors.logDebug("userId is null");
        else
            Errors.logDebug("userId="+userId);
        
        Errors.logDebug("level="+level);
        
        Errors.logDebug("maxDev="+maxDev);
        
        if (updateMethod == null)
            Errors.logDebug("updateMethod is null");
        else
            Errors.logDebug("updateMethod="+updateMethod);
        
        Errors.logInfo("ImportData.debug() ended");
    }
    
    //protected FileTypeDefinition fileType = null;
    
    protected String pid;
    protected String isid;
    protected String ifid;
    protected String upPath;
    protected String systemFileName;
    protected String updateMethod;
    
    protected int sampleUnitId;
    protected String userId;
    
    protected int level;
    protected int maxDev;
    
    protected int speciesId;
    
    /** Database connection */
    protected Connection connection;
    
    /** Visible database connection */
    protected Connection conn_viss;
    
    /**
     * 
     * The method that imports the data 
     *  Must be declared in all classes
     * @return Return the status of the method. True means ok, otherwise false.
     */
    public abstract boolean imp();
    
    /**
     * The check method implements syntax and semantic control of the import file.
     * This method is called in the first stage of the import process.
     * @return Return the status of the method. True means ok, otherwise false.
     */
    public abstract boolean check();
    
    /**
     * This method is used to identify which object to take care of an uploaded file. 
     * ImportData objects are registered in the ImportProcess init function.
     * @return Returns the format name like INDIVIDUAL for the individual format.
     */
    public abstract String getFormat();
    
    protected ArrayList<FileHeader> headers;
    
    /**
     * Return all headers this object can handle
     */
    public ArrayList<FileHeader> getHeaders()
    {
        return headers;
    }
    
    
    
    /**
     * Check if object support this format. 
     * If a match is found return true else return false
     */
    /*
    public boolean supportFormat(String objectType,String formatType, int version)
    {
        for (int i=0;i<headers.size();i++)
        {
            if ((headers.get(i).formatTypeName().equals(formatType)) 
                && (headers.get(i).objectTypeName().equals(objectType))
                && (headers.get(i).version() == version))
            {
                return true;
            }
        }
        return false;
    }
     */
    
    /**
     * Check if object support this format. 
     * If a match is found return true else return false
     */
    public boolean supportFormat(FileHeader hdr)
    {
        boolean out = false;
        try
        {
            for (int i=0;i<headers.size();i++)
            {
                if ((headers.get(i).formatTypeName().equals(hdr.formatTypeName())) 
                    && (headers.get(i).objectTypeName().equals(hdr.objectTypeName()))
                    && (headers.get(i).version() == hdr.version()))
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Examine a file and if the import module recognize the file return 
     * a header of the file. Otherwise null shall be returned.
     *
     * This method helps to identify files that dont have the headerrow and
     * the identification of such a file.
     *
     * Implement this if a fileformat shall have a virtual header row.
     */
    public FileHeader examineFile(Connection conn, int ifid)
    {
        return null;
    }
}
