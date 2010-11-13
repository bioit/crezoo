/*
 * AbstractImportMarkerSet.java
 *
 * Created on den 27 april 2004, 08:50
 */

package se.arexis.agdb.util.FileImport;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;

import java.util.*;
import java.io.*;

/**
 *
 * @author  wali
 */
abstract class AbstractImportMarkerSet extends ImportData {
    
    protected ArrayList errorList;
    
     /** Warning list */
    protected ArrayList warningList;
    /** Creates a new instance of AbstractImportVariableSet */
    
    /** Creates a new instance of AbstractImportMarkerSet */
    public AbstractImportMarkerSet() {
    }
   
    protected void checkValues(String marker_name)
    {
        if (marker_name == null || marker_name.trim().equals(""))
        {
            errorList.add("Unable to read marker name.");
        }
        else if (marker_name.length() > 20)
        {
            errorList.add("Marker name exceeds 20 characters.");
        }
        
        //The position can be any number, so, no need to check.
        
       
    }
    
    protected void checkCreate(String marker)
    {
        //The variables should exist!
        if (db.isMarkerUnique(marker))
        {
            errorList.add("Marker ["+marker+"] in marker set does not exist.");
        }
        // Add object to test db.
        db.setMarker(marker);
    }
    
    protected void checkUpdate(String marker)
    {
        
    }
    
    protected void checkCreateOrUpdate(String marker)
    {
        
    }
    
     protected String checkList(FileParser fp, FileWriter fileOut, char delim)
    {
        Errors.logDebug("ImportMarkerSet.checkList(...)");
        String errorMsg = "";
        int dataRows = fp.dataRows();
        int numOfErrors = 0;
        
        String marker, position;
        
        DbImportFile dbInFile = new DbImportFile();
        String statusStr;
        double status;
        double status_last = 0.0;
        
        Errors.logDebug(db.getDebug());
        Errors.logDebug("DataRows="+dataRows);
        warningList = new ArrayList();
        
       
        for (int i=0;i<dataRows;i++)
        {
            errorList = new ArrayList();
            
            marker    = fp.getValue("MARKER",i);
            position  = fp.getValue("POSITION",i);
            // Check for valid data values.
            // Check for length, remove null and so on.
            // Syntax check.
            checkValues(marker);

            // If add updateMethod
            if (updateMethod == null || updateMethod.equalsIgnoreCase("CREATE"))
            {
                checkCreate(marker);
            }

            // If update updateMethod
            else if (updateMethod.equalsIgnoreCase("UPDATE"))
            {   
                warningList.add("Update updateMethod was chosen, marker sets only supports create updateMethod.");
                Errors.logWarn("AbstractImportMarkerSet.checkList(...) "+
                "UPDATE updateMethod was chosen but it is not supported by import marker set. CREATE updateMethod is used instead.");
                checkCreate(marker);
            }

            // if both update and add
            else if (updateMethod.equalsIgnoreCase("CREATE_OR_UPDATE"))
            {   
                warningList.add("Create or update updateMethod was chosen, marker sets only supports create updateMethod.");
                Errors.logWarn("AbstractImportMarkerSet.checkList(...) "+
                "CREATE OR UPDATE updateMethod was chosen but it is not supported by import marker set. CREATE updateMethod is used instead.");
                checkCreate(marker);
            }
            
            /**
             * Write errors to a commented file
             */
            try
            {
                //Errors.logDebug(errList.toString());
                numOfErrors += errorList.size();
                for (int j=0;j<errorList.size();j++)
                {
                    fileOut.write("# " + (String)errorList.get(j)+"\n");
                }
               
                //fatalErrors = new ArrayList();
                fileOut.write(marker+delim+position+"\n");
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
                e.printStackTrace(System.err);
            }
            
            /*
             * Set the status of the import, visible to the user
             */
            status = (new Double(i*100/(1.0*dataRows))).doubleValue();
            if (status_last + 5 < status)
            {
                status_last = status;                
                statusStr = Integer.toString((new Double(status)).intValue()) + "%";
                dbInFile.setStatus(conn_viss,ifid,statusStr);
            }
        }
        
        writeListErrors(fileOut);
        
        /* Write a meaningful message to the user. */
      
        Errors.logDebug("Errors="+numOfErrors);
       
        if (errorList.size()>0)
            errorMsg = "ERROR: Import of the marker set file failed.";
        else if (warningList.size()>0)
            errorMsg = "WARNING: Some warnings exist in the marker set file";
        else 
            errorMsg = "Marker set file is correct";
        errorMsg += "<br>\nWarnings:"+warningList.size()+"<br>\nErrors:"+errorList.size();

        return errorMsg;
    }
     
  private void writeListErrors(FileWriter fileOut)
    {
        try
        {
            if(errorList.size()>0 ||  warningList.size()>0)
            {
                fileOut.write("#--------------------------------------------------\n");
            }
            if(errorList.size()>0)
            {
                for(int i=0;i<errorList.size();i++)
                {
                    fileOut.write("#"+ (String)errorList.get(i)+"\n");
                }
            }
            if(warningList.size()>0)
            {
                for (int i=0;i<warningList.size();i++)
                {
                    fileOut.write("#"+ (String)warningList.get(i)+"\n");
                }
            }

            
            // if there are errors, the string is "Outcommented"
            if(errorList.size()>0)
            {
                fileOut.write("#");
            }

            // write original string
            //fileOut.write(delimeter+group);

            if(errorList.size()>0 || warningList.size()>0)
            {
                fileOut.write("#--------------------------------------------------\n");
            }

        }//try
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }
}
