/*
 * importSamples.java
 *
 * Created on den 1 april 2004, 13:35
 */

package se.arexis.agdb.util.FileImport;

import java.io.*;
import java.util.*;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;


/**
 *
 * @author  wali
 * @version
 */
public class ImportSamples extends ImportData
{
    /**
     * Return the format name
     */
    public String getFormat()
    { 
        return "SAMPLE";
    }
    
    private ArrayList errorList;
    private ArrayList warningList;
    /** Creates a new instance of ImportVariables */
    public ImportSamples() 
    {
        CREATE=true;
        UPDATE=true;
        CREATE_OR_UPDATE=true;
        SUID=true;
        try
        {
            headers = new ArrayList<FileHeader>();
            headers.add(new FileHeader("SAMPLE","LIST",1,'\t'));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public boolean check() 
    {
        Errors.logInfo("ImportSamples.check() started");
        boolean res = false;
        DbImportFile dbInFile = new DbImportFile();
        String fullFileName = "";
        String checkFileName = "";
        String errMessage = null;
        
        try
        {
            dbInFile.setStatus(conn_viss,ifid,"0%");
            
            // Store the file on server filesystem
            fullFileName = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            checkFileName = fullFileName + "_checked";
            
            FileHeader header = FileParser.scanFileHeader(fullFileName);
            String type = header.formatTypeName().toUpperCase();
            char delimiter = header.delimiter().charValue();
            
            FileParser fileParser = new FileParser(fullFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.SAMPLE,
                                                                            FileTypeDefinition.LIST));
            
            // Write out the result to a new file
            FileWriter fileOut = new FileWriter(checkFileName);
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
            
            String indId;
            
            if (titles[0].equals("IDENTITY"))
                indId="IDENTITY";
            else
                indId="Alias";
            
            String errorMsg = "";
            errorMsg = checkList(fileParser,fileOut,delimiter, indId);
            fileOut.close();
            
            /* 
             * Save the file to database
             */
            dbInFile.saveCheckedFile(conn_viss, ifid, checkFileName);
            
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
            dbInFile.setStatus(conn_viss,ifid,"ERROR");
                        
            // Add a message to the log
            dbInFile.addErrMsg(conn_viss,ifid,e.getMessage());
           
            e.printStackTrace(System.err);
            if (errMessage == null)
            {
                errMessage = e.getMessage();
            }
        }
        finally
        {
            try
            {
                /*
                 * Delete files uploaded
                 */
                File tmp = new File(checkFileName);
                tmp.delete();

                tmp = new File(fullFileName);
                tmp.delete();
            }
            catch (Exception ignore)
            {
            }
        }
        
        Errors.logInfo("ImportSamples.check() ended");
        return res;
    }
  
    public boolean imp() 
    {
        boolean res = false;
        //boolean isOk = true;
        String errMessage = null;
        //String sampleUnitIdAsStr = null;
        DbImportFile dbInFile = new DbImportFile();
      
        try
        {
            String filename = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            
            
            dbInFile.setStatus(conn_viss,ifid,"0%");
            
            // Create the individual 
            DbIndividual dbIndividual = new DbIndividual();
            //FileParser fileParser = new FileParser(upPath + "/" +  systemFileName);
            FileParser fileParser = new FileParser(filename);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.SAMPLE,
            FileTypeDefinition.LIST));
            
            /*
            // If add mode
            if (mode == null || mode.equalsIgnoreCase("CREATE"))
            {
                dbIndividual.CreateIndividuals(fileParser, connection,
                                                  sampleUnitId,
                                                  Integer.parseInt(userId));
            }

            // If update mode
            else if (mode.equalsIgnoreCase("UPDATE"))
            {
                dbIndividual.UpdateIndividuals(fileParser, connection,
                                                  sampleUnitId,
                                                  Integer.parseInt(userId)); 
            }
            */
            
            //sample only support CREATE OR UPDATE!!
            // if both update and add
            if (updateMethod == null || updateMethod.equalsIgnoreCase("CREATE_OR_UPDATE") || updateMethod.equalsIgnoreCase("UPDATE") || updateMethod.equalsIgnoreCase("CREATE"))
            {
                dbIndividual.CreateOrUpdateSamples(fileParser,
                                                          connection,
                                                          sampleUnitId,
                                                          Integer.parseInt(userId));
            }
            errMessage = dbIndividual.getErrorMessage();
            
            // Get the error message from the database object. If it is set an
            // error occured during the operation so an error is thrown.
            errMessage = dbIndividual.getErrorMessage();
            Assertion.assertMsg(errMessage == null ||
                                 errMessage.trim().equals(""), errMessage);
            
            dbInFile.setStatus(conn_viss,ifid,"IMPORTED");
            //dbInFile.UpdateImportFile(connection,null,null,"Done",Integer.parseInt(ifid),Integer.parseInt(userId));
            
            // Add a message to the log
            dbInFile.addErrMsg(conn_viss,ifid,"File imported to sampling unit "+DbSamplingUnit.getSUName(conn_viss,Integer.toString(sampleUnitId)) );
            res = true;
        }
        catch (Exception e)
        {
            // Flag for error and set the errMessage if it has not been set
            //isOk = false;
            dbInFile.setStatus(conn_viss,ifid,"ERROR");
            //dbInFile.UpdateImportFile(connection,null,null,e.getMessage(),Integer.parseInt(ifid),Integer.parseInt(userId));
            
            // Add a message to the log
            dbInFile.addErrMsg(conn_viss,ifid,e.getMessage());
           
            e.printStackTrace(System.err);
            if (errMessage == null)
            {
                errMessage = e.getMessage();
            }
        }
        return res;
    }
    
    
        /**
     * @param fp
     * @param fileOut
     * @param delim
     * @return  */    
    private String checkList(FileParser fp , FileWriter fileOut, char delim, String indId)
    {
        String identity, name, tissue, experimenter, date, treatment, storage, comment, value;
        String errorMsg = "";
        Errors.logInfo("ImportSamples.checkList(...) started");
        int dataRows = fp.dataRows();        
        DbImportFile dbInFile = new DbImportFile();
        
        int numOfErrors = 0;
        String statusStr;
        double status;
        double status_last = 0.0;
     
        errorList = new ArrayList();
        warningList = new ArrayList();
        for (int i=0;i<dataRows;i++)
        {
            identity        = fp.getValue(indId, i).trim();
            name            = fp.getValue("NAME",i).trim();
            tissue          = fp.getValue("TISSUE",i).trim();
            experimenter    = fp.getValue("EXPERIMENTER",i).trim();
            date            = fp.getValue("DATE",i).trim();
            treatment       = fp.getValue("TREATMENT",i).trim();
            storage         = fp.getValue("STORAGE",i).trim();
            comment         = fp.getValue("COMMENT",i).trim();

            // Check for valid data values.
            // Check for length, remove null and so on.
            // Syntax check.
       
            checkValues(identity, name, tissue, experimenter, date, treatment, storage, comment);
   
            // If add updateMethod
            if (updateMethod == null || updateMethod.equalsIgnoreCase("CREATE"))
            {
                warningList.add("Create updateMethod was chosen but is not supported by samples. CREATE OR UPDATE updateMethod is used instead.");
                Errors.logWarn("ImportSamples.checkList(...) "+
                "CREATE updateMethod was chosen but it is not supported by samples. CREATE OR UPDATE updateMethod is used instead.");
                checkCreateOrUpdate(identity, name);
                //checkCreate(name);
            }

            // If update updateMethod
            else if (updateMethod.equalsIgnoreCase("UPDATE"))
            {
                warningList.add("Update updateMethod was chosen, variable sets only supports create updateMethod.");
                Errors.logWarn("ImportSamples.checkList(...) "+
                "UPDATE updateMethod was chosen but it is not supported by samples. CREATE OR UPDATE updateMethod is used instead.");
                checkCreateOrUpdate(identity, name);
                //checkUpdate(name);
            }

           
            // if both update and add
            else if (updateMethod.equalsIgnoreCase("CREATE_OR_UPDATE"))
            {
                checkCreateOrUpdate(identity, name);
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
                fileOut.write(identity+delim+name+delim+tissue+delim+experimenter+delim+date+delim+treatment+delim+storage+delim+comment+"\n");
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
            // write row + all errors encountered to file
            writeListErrors(fileOut);
        }
        Errors.logDebug("ImportSamples.checkList() Errors="+numOfErrors);
        /*if (numOfErrors>0)
        {null
            errorMsg = "Import of the sample failed. <br>Errors: "+numOfErrors;
        }
        else if (warningList.size()>0)
            errorMsg = "WARNING: Some warnings exist in the grouping file";
        */
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
    
    private void checkValues(String identity, String name, String tissue, String experimenter, String date, String treatment, String storage, String comment)
    {
        boolean ret = true;
            
        //Identity    
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
        
        //name
        if (name == null || name.trim().equals(""))
        {
            errorList.add("Unable to read sample name.");
            ret = false;
        }
        else if (name.length() > 20)
        {
            errorList.add("Sample [" + name + "] exceeds 20 characters.");
            ret = false;
        }
        
        
        //tissue    
        if (tissue == null || tissue.trim().equals(""))
        {
            tissue = "";
        }
        else if (tissue.length() > 20)
        {
            errorList.add("Tissue [" + tissue + "] exceeds 20 characters.");
            ret = false;
        }
        
            
        //Experimenter    
        if (experimenter == null)
        {
            experimenter = "";
        }
        else if (experimenter != null && experimenter.length() > 32)
        {
            errorList.add("Experimenter [" + experimenter + "] exceeds 32 characters.");
            ret = false;
        }
        
        if (date.length() != 0) 
            if (date != null && date.length() != 10)
            {
                errorList.add("Date not in the format 'YYYY-MM-DD'.");
                ret = false;
            }
        else if (date != null && date.length() == 10)
        {
            try 
            {
                java.util.Date temp = java.sql.Date.valueOf(date);
            }
            catch (Exception e)
            {  
                errorList.add("Date not in the format 'YYYY-MM-DD'");
                ret = false;
            }
        }
     
        if (treatment == null)
        {
            treatment = "";
        }
        else if (treatment != null && treatment.length() > 20)
        {
            errorList.add("Treatment [" + treatment + "] exceeds 20 characters.");
            ret = false;
        }
        
        if (storage == null)
        {
            storage = "";
        }
        else if (storage != null && storage.length() > 20)
        {
            errorList.add("Storage [" + treatment + "] exceeds 20 characters.");
            ret = false;
        }
        
        if (comment != null && comment.length() > 256)
        {
            errorList.add("Comment exceeds 256 characters.");
            ret = false;
        }
    }
    
    /**
     * @param name
     * @return  */    
    
    private void checkCreate(String identity, String name)
    {   
        if(db.isIndividualUnique(identity)){
            errorList.add("Identity ["+identity+"] does not exist.");
        }
        
        if (!db.isSampleUnique(identity, name))
        {
            errorList.add("Sample ["+ name +"] already exists");
        }
       
        // Add object to test db.
        db.setSample(identity, name);
    }
    
     /**
     * @param name
     * @return  */    
    private void checkUpdate(String identity, String name)
    {
        if(db.isIndividualUnique(identity)){
            errorList.add("Identity ["+identity+"] ["+name+"] does not exist.");
        }
        
        if (db.isSampleUnique(identity, name))
        {
            errorList.add("Sample ["+name+"] does not exist");
        }
    }
    
     /**
     * @param name
     * @return  */    
    private void checkCreateOrUpdate(String identity, String name)
    {
      
        if (db.isSampleUnique(identity, name))
        {
            checkCreate(identity, name);
        }
        else
        {
            checkUpdate(identity, name);
        }
    }
    
    
}
