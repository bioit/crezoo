/*
 * AbstractImportMarker.java
 *
 * Created on April 5, 2004, 1:04 PM
 *
 * $Log$
 * Revision 1.6  2004/12/14 08:51:08  heto
 * Renamed variable
 *
 * Revision 1.5  2004/12/08 09:17:44  heto
 * Code audit
 * Removed unnecessary code
 * added comments and javadoc.
 *
 *
 */

package se.arexis.agdb.util.FileImport;
import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;

import java.util.*;
import java.io.*;

/**
 * This an abstract class to handle common functionallity of 
 * ImportMarkers and ImportUMarkers.
 *
 * @author  heto
 */
public abstract class AbstractImportMarker extends ImportData 
{
    /** Error list for errors encountered */
    protected ArrayList errorList;
    
    /** Warning list */
    protected ArrayList warningList;
    
    /** Creates a new instance of AbstractImportMarker */
    public AbstractImportMarker() 
    {
    }
    
   /**
     * Check values for constraints.
     * @param chr The cromosome
     * @param marker The marker data
     * @param alias The alias of the marker
     * @param position The position of the marker
     * @param primer1 The first primer
     * @param primer2 the second primer
     * @param comment A comment
     */
    protected void checkValues(String chr,String marker,String alias,
        String position,String primer1,String primer2,String comment)
    {
        if (chr == null || chr.trim().equals(""))
        {
            errorList.add("Unable to read Chromosome.");
        }
        else if (chr.length() > 2)
        {
            errorList.add("Chromosome [" + chr + "] exceeds 2 characters.");
        }
        
        if (marker == null || marker.trim().equals(""))
        {
            errorList.add("Unable to read Marker ["+marker+"]");
        }
        else if (marker.length() > 20)
        {
            errorList.add("Marker [" + marker + "] exceeds 20 characters.");
        }
        
        if (alias == null)
        {
            alias = "";
        }
        else if (alias.length() > 20)
        {
            errorList.add("Alias [] exceeds 20 characters.");
        }
        
        
        if (position == null)
        {
            position = "";
        }
        
        if (primer1 == null)
        {
            primer1 = "";
        }
        
        if (primer2 == null)
        {
            primer2 = "";
        }
        
        if (primer1.length() > 40)
        {
            errorList.add("Primer1 [" + primer1 + "] exceeds 40 characters.");
        }
        
        if (primer2.length() > 40)
        {
            errorList.add("Primer1 [" + primer1 + "] exceeds 40 characters.");
        }
        
     
        if (comment != null && comment.length() > 256)
        {
            errorList.add("Comment exceeds 256 characters.");
        }
    }
        
    /**
     * Check data for create mode. Check so the information
     * is uniq
     * @param chr The cromosome to check
     * @param marker the marker to check
     */
    protected void checkCreate(String chr,String marker)
    {
        if (!db.isMarkerUnique(marker,chr))
        { 
            errorList.add("Marker ["+ marker+"] for this chromosome ["+chr+"] already exists");
        }
      
        // Add object to test db.
        db.setMarker(marker,chr);
    }
    
   
    /**
     * Check for update mode. This is not implemented!
     */
    /*
    protected void checkUpdate(String chr,String marker)
    {        
        //if (db.isMarkerUnique(marker,chr))
        //    errorList.add("Marker ["+ marker+"] for this chromosome ["+chr+"] does not exists. Therefore update fails.");
    }*/
    
    /*
     
     *Not implemented!!!
    protected void checkCreateOrUpdate(String chr,String marker)
    {
        if (db.isMarkerUnique(marker,chr))
        {
            checkCreate(chr,marker);
        }
        else
        {
            checkUpdate(chr,marker);
        }
    }
    */
    
    /**
     * Check the LIST format
     * 
     * Loop all rows in the fileParser, check all values
     * @param fp The fileparser object. The data to be examined.
     * @param fileOut The commented file handler
     * @param delim The delimiter, for example ";"
     * @return the status message of the method. This message is aimed for the user
     */
    protected String checkList(FileParser fp, FileWriter fileOut, char delim)
    {
        Errors.logInfo("ImportMarkers.checkList(...) started");
        String errorMsg = "";
        int dataRows = fp.dataRows();
        int numOfErrors = 0;
        
        String chr, marker, alias, position, primer1, primer2, comment;
        DbImportFile dbInFile = new DbImportFile();
        DbMarker dbMark = new DbMarker(); 
        
        String statusStr;
        double status;
        double status_last = 0.0;
        
       
        Errors.logDebug(db.getDebug());
        Errors.logDebug("DataRows="+dataRows);
        
        for (int i=0;i<dataRows;i++)
        {
            warningList = new ArrayList();
            errorList = new ArrayList();
            
            chr         = fp.getValue("CHROMOSOME",i);
            marker      = fp.getValue("MARKER",i);
            alias       = fp.getValue("ALIAS",i);
            position    = fp.getValue("POSITION",i);
            primer1     = fp.getValue("PRIMER1",i);
            primer2     = fp.getValue("PRIMER2",i);
            comment     = fp.getValue("COMMENT",i);
            
            // Check for valid data values.
            // Check for length, remove null and so on.
            // Syntax check.
            
            checkValues(chr,marker,alias,position,primer1,primer2,comment);
            
            if (updateMethod == null || updateMethod.equalsIgnoreCase("CREATE"))
            {
                checkCreate(chr,marker);

            }

            // If update updateMethod
            else if (updateMethod.equalsIgnoreCase("UPDATE"))
            {
                warningList.add("Update updateMethod was chosen but create updateMethod was executed for the markers.");
                Errors.logWarn("AbstractImportMarker.checkList(...) "+
                "UPDATE updateMethod was chosen but it is not supported by import markers. CREATE updateMethod is used instead.");
                checkCreate(chr, marker);
            }

            // if both update and add
            else if (updateMethod.equalsIgnoreCase("CREATE_OR_UPDATE"))
            {
                warningList.add("Create or update updateMethod was chosen but create updateMethod was executed for the markers.");
                Errors.logWarn("AbstractImportMarker.checkList(...) "+
                "CREATE OR UPDATE updateMethod was chosen but it is not supported by import markers. CREATE updateMethod is used instead.");
                checkCreate(chr,marker);
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
                fileOut.write(chr+delim+marker+delim+alias+delim+position+delim+primer1+delim+primer2+delim+comment+"\n");
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
            
            //writeListErrors(fileOut);
        }
        /* Write a meaningful message to the user. */
        Errors.logDebug("Errors="+numOfErrors);
        
        if (errorList.size()>0)
            errorMsg = "ERROR: Import of the marker file failed.";
        else if (warningList.size()>0)
            errorMsg = "WARNING: Some warnings exist in the marker file";
        else 
            errorMsg = "Marker file is correct";
        errorMsg += "<br>\nWarnings:"+warningList.size()+"<br>\nErrors:"+errorList.size();
 
        return errorMsg;
    }
 
    /*
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
     */
    
}
