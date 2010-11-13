/*
 * ImportParams.java
 *
 * Created on December 9, 2004, 9:39 AM
 */

package se.arexis.agdb.util.FileImport;

import java.sql.Connection;

import se.arexis.agdb.util.Errors;

/**
 *
 * @author heto
 */
public class Prefs 
{
    
    public int pid;
    public int isid;
    public String upPath;
    public String updateMethod;
    public String userId;
    public int level;
    public int maxDev;
    
    
    public int ifid;                // Only used by ImportProcess class
    public String fileName;         // Only used by ImportProcess class
    
    
    public int speciesId;
    public int sampleUnitId;
    
    /** The mode of the process: CHECK, IMPORT */
    public String mode;
    
    
    /** Database connection */
    public Connection connection;
    
    /** Visible database connection */
    public Connection connViss;
    
    /** Creates a new instance of ImportParams */
    public Prefs() 
    {
    }
    
    public void setSUId(int suid)
    {
        this.sampleUnitId=suid;
    }
    
    public void setSpeciesId(int speciesid)
    {
        this.speciesId=speciesid;
    }
    
    public void debug()
    {
        Errors.logInfo("Prefs.debug() "+isid+" started");
        
        if (connection == null)
            Errors.logDebug("connection is null");
        else
            Errors.logDebug("connection is not null");
        
        if (connViss == null)
            Errors.logDebug("connViss is null");
        else
            Errors.logDebug("conn_viss is not null");
        
        Errors.logDebug("pid="+pid);
        Errors.logDebug("isid="+isid);
        Errors.logDebug("ifid="+ifid);
        /*
        if (pid == null)
            Errors.logDebug("pid is null");
        else
            Errors.logDebug("pid="+pid);
        
        if (isid == null)
            Errors.logDebug("isid is null");
        else
            Errors.logDebug("isid="+isid);
        */
        
        if (upPath == null)
            Errors.logDebug("upPath is null");
        else
            Errors.logDebug("upPath="+upPath);
        
        
        if (fileName == null)
            Errors.logDebug("fileName is null");
        else
            Errors.logDebug("fileName="+fileName);
        
        
        if (updateMethod == null)
            Errors.logDebug("updateMethod is null");
        else
            Errors.logDebug("updateMethod="+updateMethod);
        
        Errors.logDebug("sampleUnitId="+sampleUnitId);
        Errors.logDebug("speciesId="+speciesId);
        
        if (userId == null)
            Errors.logDebug("userId is null");
        else
            Errors.logDebug("userId="+userId);
        
        Errors.logDebug("level="+level);
        
        Errors.logDebug("maxDev="+maxDev);
        
        if (mode == null)
            Errors.logDebug("mode is null");
        else
            Errors.logDebug("mode="+mode);
        
       
        
        Errors.logInfo("Prefs.debug() "+isid+" ended");
    }
}
