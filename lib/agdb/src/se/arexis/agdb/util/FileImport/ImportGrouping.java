/*
 * ImportGrouping.java
 *
 * Created on April 1, 2004, 12:48 PM
 */

package se.arexis.agdb.util.FileImport;

import java.io.*;
import java.util.*;

import se.arexis.agdb.*;
import se.arexis.agdb.util.*;
import se.arexis.agdb.db.*;


/**
 *
 * @author  heto
 */
public class ImportGrouping extends ImportData 
{
    
    /**
     * Return the format name
     */
    public String getFormat()
    { 
        return "GROUPING";
    }

    /** An list of error messages passed between methods in this class */
    private ArrayList errorList;
    
    /** Warning list */
    private ArrayList warningList;
    
    /** Creates a new instance of ImportGrouping */
    public ImportGrouping() 
    {
        CREATE=true;
        SUID=true;
        try
        {
            headers = new ArrayList<FileHeader>();
            headers.add(new FileHeader("GROUPING","LIST",1,'\t'));
            
            // Individual data is needed before this is imported
            dependency.add(new Dependency("GROUPING", new String[] {"INDIVIDUAL","TACMAN"} ));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public boolean check() 
    {
        this.updateMethod="CREATE";
        
        // ImportGrouping.check()
        Errors.logInfo("ImportGrouping.check() started");
        boolean res = false;
        DbImportFile dbInFile = new DbImportFile();
        String errorMsg = "";
        
        FileWriter fileOut=null;
        
        try
        {
            String filename = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            String checkFileName = filename + "_checked";
            
            // Create the individual data object
            DbIndividual dbIndividual = new DbIndividual();
            
            FileParser fileParser = new FileParser(filename);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.GROUPING,
                                                                            FileTypeDefinition.LIST));
            
            FileHeader header = FileParser.scanFileHeader(filename);
            char delimiter = header.delimiter().charValue();
            
            // Write out the result to a new file
            fileOut = new FileWriter(checkFileName);
            fileOut.write(header.objectTypeName()
                +"/"+header.formatTypeName()
                +"/"+header.version()
                +"/"+header.delimiter()+"\n");
            
            String titles[] = fileParser.columnTitles();
            for (int j=0;j<titles.length;j++)
            {
                fileOut.write(titles[j] + delimiter);
            }
            fileOut.write("\n");
            
            // Garbage collect the unused variables
            header = null;
            
            
            // Check list file
            errorMsg = checkList(fileParser, fileOut, delimiter);
            
            // Close the file
            fileOut.close();
            
            /* 
             * Save the file to database
             */
            dbInFile.saveCheckedFile(conn_viss, ifid, checkFileName);
            
            /*
             * Delete files uploaded
             */
            File tmp = new File(filename);
            tmp.delete();
            
            tmp = new File(checkFileName);
            tmp.delete();
            
            if (errorMsg.startsWith("ERROR:"))
            {
                res = false;
                dbInFile.setStatus(conn_viss,ifid,"ERROR");
                
                // Add a message to the log
                dbInFile.addErrMsg(conn_viss,ifid,"File checked failed for sampling unit "+DbSamplingUnit.getSUName(conn_viss,Integer.toString(sampleUnitId)) +". <br>"+errorMsg );
                Errors.logDebug("ErrorMsg="+errorMsg);
            }
            else if (errorMsg.startsWith("WARNING:"))
            {
                dbInFile.setStatus(conn_viss,ifid,"WARNING");
                res = true;
                
                // Add a message to the log
                dbInFile.addErrMsg(conn_viss,ifid,"Warnings exists: "+errorMsg );
            }
            else
            {
                dbInFile.setStatus(conn_viss,ifid,"CHECKED");
                res = true;
                
                // Add a message to the log
                dbInFile.addErrMsg(conn_viss,ifid,"File checked for sampling unit "+DbSamplingUnit.getSUName(conn_viss,Integer.toString(sampleUnitId)) + ".<br>"+errorMsg);

            }
            
           
          
            
        }
        catch (Exception e)
        {
            Errors.logError("ImportGrouping.check() Exception: "+e.getMessage());
        }
        
        
        Errors.logInfo("ImportGrouping.check() ended");
        return res;
    }
    
    private String checkList(FileParser fp , FileWriter fileOut, char delim)
    {
        // ImportGrouping.checkList(...)
        Errors.logInfo("ImportGrouping.checkList(...) started");
        
        String errorMsg = "";
        String titles[];
        
        
        
        int dataRows = fp.dataRows();
        DbImportFile dbInFile = new DbImportFile();
        
        int numOfErrors = 0;
        String statusStr;
        double status;
        double status_last = 0.0;
        
        // This first element is "IDENTITY"
        titles = fp.columnTitles();
        
        String identity, group, grouping;
        
        errorList = new ArrayList();
        warningList = new ArrayList();
        
        // Check for valid grouping names in title
        for (int j=1;j<titles.length;j++)
        {
            grouping = titles[j].trim();
            if (grouping.length()>20)
            {
                // Grouping name too long
                errorList.add("Grouping name ["+grouping+"] exceeds 20 characters.");
            }
            if (grouping.length()==0)
            {
                // Grouping name not valid
                errorList.add("Grouping name is invalid.");
            }
            
            /*
            try
            {
                // Write out the group names to the commented file
                fileOut.write(grouping+delim);
            }
            catch (Exception e)
            {
                Errors.logError("Exception: "+e.getMessage());
                e.printStackTrace();
            }
             */
            
        }
        
        
        for (int i=0;i<dataRows;i++)
        {
            identity = fp.getValue("IDENTITY",i).trim();
            Errors.logDebug("ImportGrouping.checkList(...) IDENTITY="+identity);
            
            try
            {
                // Write to comment file
                fileOut.write(identity);
            }
            catch (Exception e)
            {
                Errors.logError("ImportGrouping.checkList(...) Unable to write to comment file");
            }
            
            for (int j=1;j<titles.length;j++)
            {
                
                
                group       = fp.getValue(titles[j], i).trim();
                grouping    = titles[j].trim();
                
                Errors.logDebug("ImportGrouping.checkList(...) GROUP="+group);
                Errors.logDebug("ImportGrouping.checkList(...) GROUPING="+grouping);
                
                checkValues(identity,group);
                
                /*
                 * Add updateMethod is always used. If other updateMethod is choosen, a warning 
                 * message is displayed.
                 */
                
                // If add updateMethod
                if (updateMethod == null || updateMethod.equalsIgnoreCase("CREATE"))
                {
                }
                
                // If update updateMethod
                else if (updateMethod.equalsIgnoreCase("UPDATE"))
                {
                    Errors.logWarn("ImportGrouping.checkList(...) Always create updateMethod for grouping files");
                    warningList.add("Always create updateMethod for grouping files");
                }
                
                else if (updateMethod.equalsIgnoreCase("CREATE_OR_UPDATE"))
                {
                    Errors.logWarn("ImportGrouping.checkList(...) Always create updateMethod for grouping files");
                    warningList.add("Always create updateMethod for grouping files");
                }
            
                // Always in create updateMethod!
                checkCreate(identity, group,grouping);
                
                try
                {
                    // write original string
                    fileOut.write(delim+group);
                }
                catch (Exception e)
                {
                    Errors.logError("ImportGrouping.checkList(...) Unable to write to comment file");
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
            try
            {
                // Write to comment file
                fileOut.write("\n");
            }
            catch (Exception e)
            {
                Errors.logError("ImportGrouping.checkList(...) Unable to write to comment file");
            }
            
            // write row + all errors encountered to file
            writeListErrors(fileOut);
            
        }
            
        if (errorList.size()>0)
            errorMsg = "ERROR: Import of the grouping file failed.";
        else if (warningList.size()>0)
            errorMsg = "WARNING: Some warnings exist in the grouping file";
        else 
            errorMsg = "Grouping file is correct";
        errorMsg += "<br>\nWarnings:"+warningList.size()+"<br>\nErrors:"+errorList.size();
            
        Errors.logInfo("ImportGrouping.checkList(...) ended");
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
    
    private void checkValues(String identity, String group)
    {
        boolean ret = true;
        if (identity == null || identity.trim().equals(""))
        {
            errorList.add("Unable to read Identity/Alias.");
            ret = false;
        }
        else if (identity.length() > 11)
        {
            errorList.add("Identity/Alias [" + identity + "] exceeds 11 characters.");
            ret = false;
        }
        
        if (group == null || group.trim().equals(""))
        {
            group = "";
        }
        else if (group.length() > 20)
        {
            errorList.add("Group [" + group + "] exceeds 20 characters.");
            ret = false;
        }
    }
    
    private void checkCreate(String identity, String group, String grouping)
    {
        if (db.isIndividualUnique(identity))
        {
            errorList.add("Individual ["+ identity+"] does not exist");
        }
        
        if (!db.isGroupingUnique(identity,group,grouping))
        {
            errorList.add("Individual ["+ identity+"] for group ["+group+"] in grouping ["+grouping+"] already exists");
        }
        
        // Add object to test db.
        db.setGrouping(identity,group,grouping);
    }
    
    public boolean imp() 
    {
        // ImportGrouping.imp()
        Errors.logInfo("ImportGrouping.imp() started");
        boolean res = false;
        DbImportFile dbInFile = new DbImportFile();
        String errMessage;
        try
        {
            String filename = dbInFile.storeImportFileBLOB(conn_viss, ifid);
                        
            // Create the individual data object
            DbIndividual dbIndividual = new DbIndividual();
            
            FileParser fileParser = new FileParser(filename); 
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.GROUPING,
                                                                        FileTypeDefinition.LIST));
            dbIndividual.CreateGroupings(fileParser, connection,
                                         sampleUnitId, Integer.valueOf(userId).intValue());
            
            errMessage = dbIndividual.getErrorMessage();
            Assertion.assertMsg(errMessage == null ||
                             errMessage.trim().equals(""), errMessage);
            
            dbInFile.setStatus(conn_viss,ifid,"IMPORTED");
            
            // Add a message to the log
            dbInFile.addErrMsg(conn_viss,ifid,"File imported to sampling unit "+DbSamplingUnit.getSUName(conn_viss,Integer.toString(sampleUnitId)) );
            res = true;
        }
        catch (Exception e)
        {
            Errors.logError("ImportGrouping.imp() Exception: "+e.getMessage());
        }
       
        Errors.logInfo("ImportGrouping.imp() ended");
        return res;
    }
    
}
