/*
 * ImportUVariables.java
 *
 * Created on den 5 april 2004, 14:28
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
public class ImportUVariables extends AbstractImportVariable 
{
    /**
     * Return the format name
     */
    public String getFormat()
    { 
        return "UVARIABLE";
    }
    
    /** Creates a new instance of ImportUVariables */
    public ImportUVariables() 
    {
        CREATE=true;
        SPECIESID=true;
        
        try
        {
            headers = new ArrayList<FileHeader>();
            headers.add(new FileHeader("UVARIABLE","LIST",1,'\t'));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public boolean check() 
    {
        Errors.logDebug("CheckUVariables started");
        boolean res = false;
        DbImportFile dbInFile = new DbImportFile();
        String fullFileName = "";
        String checkFileName = "";
        String errMessage = null;
        
        try
        {
            // Create the uvariable 
            DbUVariable dbUVariable = new DbUVariable();
            
            dbInFile.setStatus(conn_viss,ifid,"0%");
            
            // Store the file on server filesystem
            fullFileName = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            checkFileName = fullFileName + "_checked";
            
            FileHeader header = FileParser.scanFileHeader(fullFileName);
            String type = header.formatTypeName().toUpperCase();
            char delimiter = header.delimiter().charValue();
            
            FileParser fileParser = new FileParser(fullFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.UVARIABLE,
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
            
            String errorMsg = "";
            errorMsg = checkList(fileParser,fileOut,delimiter);
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
            
          /*  
            if (errorMsg.length()>0)
            {
                res = false;
                dbInFile.setStatus(conn_viss,ifid,"ERROR");
                // Add a message to the log
                dbInFile.addErrMsg(conn_viss,ifid,"File failed the check for sampling unit "+DbSamplingUnit.getSUName(conn_viss,Integer.toString(sampleUnitId)) +"<br>"+errorMsg);
            }
            else
            {
                res = true;
                dbInFile.setStatus(conn_viss,ifid,"CHECKED");   
                // Add a message to the log
                dbInFile.addErrMsg(conn_viss,ifid,"File checked for sampling unit "+DbSamplingUnit.getSUName(conn_viss,Integer.toString(sampleUnitId)) +"<br>"+errorMsg);
            }
           **/
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
       
        Errors.logDebug("CheckVariables completed");
        return res;
    }
    
   
    
    
    public boolean imp() 
    {
        boolean res = false;
        
        DbUVariable dbUVariable = null;
        String fullFileName = "";
        String errMessage = null; 
        DbImportFile dbInFile = new DbImportFile();
        try
        {
            dbUVariable = new DbUVariable();
            
            // Store the file on server filesystem
            fullFileName = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            
            FileParser fileParser = new FileParser(fullFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.UVARIABLE,
                                                                        FileTypeDefinition.LIST));
            

            dbUVariable.CreateUVariables(fileParser, connection,
                                       speciesId, Integer.valueOf(pid).intValue(), 
                                       Integer.parseInt(userId)); 

            errMessage = dbUVariable.getErrorMessage();
            Assertion.assertMsg(errMessage == null ||
                             errMessage.trim().equals(""), errMessage);
            
      
            dbInFile.setStatus(conn_viss,ifid,"IMPORTED");
                //dbInFile.UpdateImportFile(connection,null,null,"Done",Integer.parseInt(ifid),Integer.parseInt(userId));

            // Add a message to the log
            dbInFile.addErrMsg(conn_viss,ifid,"File imported to sampling unit "+DbSamplingUnit.getSUName(conn_viss,Integer.toString(sampleUnitId)) +"Note: Markers is always imported in Create mode.");
            res = true;
        }
        catch (Exception e)
        {
            Errors.logError("ImportUVariables.imp(...)" + e.getMessage());   
            
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
        finally 
        {
            try
            {
                /*
                 * Delete files uploaded
                 */
                File tmp = new File(fullFileName);
                tmp.delete();
            }
            catch (Exception ignore)
            {
            }
          
        }
        
        return res;
    }
 
    
}
