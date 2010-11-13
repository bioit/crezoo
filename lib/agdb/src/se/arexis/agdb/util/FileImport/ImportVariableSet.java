/*
 * ImportVariableSet.java
 *
 * Created on den 7 april 2004, 07:45
 */

package se.arexis.agdb.util.FileImport;

import java.io.*;
import java.util.*;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;

/**
 *
 * @author  wali
 */
public class ImportVariableSet extends AbstractImportVariableSet 
{
    /**
     * Return the format name
     */
    public String getFormat()
    { 
        return "VARIABLESET";
    }
    
    /** Creates a new instance of ImportVariableSet */
    public ImportVariableSet() 
    {
        CREATE=true;
        SUID=true;
        NAME=true;
        
        try
        {
            headers = new ArrayList<FileHeader>();
            headers.add(new FileHeader("VARIABLESET","LIST",1,'\t'));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
  
    public boolean check() 
    {
        Errors.logDebug("CheckVariableSet started");
        boolean res = false;
        DbImportFile dbInFile = new DbImportFile();
        String fullFileName = "";
        String checkFileName = "";
        String errMessage = null;
        
        try
        {
            // Create the variableset 
            DbVariable dbVariable = new DbVariable();
            
            dbInFile.setStatus(conn_viss,ifid,"0%");
            
      
            
            // Store the file on server filesystem
            fullFileName = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            checkFileName = fullFileName + "_checked";
            
            FileHeader header = FileParser.scanFileHeader(fullFileName);
            String type = header.formatTypeName().toUpperCase();
            char delimiter = header.delimiter().charValue();
            
            FileParser fileParser = new FileParser(fullFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.VARIABLESET,
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
       
        Errors.logDebug("CheckVariableSet completed");
        return res;
    }
    
    
    public boolean imp() 
    {
        boolean res = false;
        
        DbVariable dbVariable = null;
        String fullFileName = "";
        String errMessage = null; 
        DbImportFile dbInFile = new DbImportFile();
        try
        {
            dbVariable = new DbVariable();
            
            // Store the file on server filesystem
            fullFileName = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            
            FileParser fileParser = new FileParser(fullFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.VARIABLESET,
                                                                        FileTypeDefinition.LIST));
            
            String variablesetName="";            
            String comm = "";
            
            //get the name of the variable set that was given by the user
            variablesetName=dbInFile.get_chk_name(connection, Integer.parseInt(ifid));
            
            dbVariable.CreateVariableSets(fileParser, connection,
                                       variablesetName, comm, 
                                       sampleUnitId,
                                       Integer.parseInt(userId)); 

            errMessage = dbVariable.getErrorMessage();
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
            Errors.logError("ImportVariables.imp(...)" + e.getMessage());   
            
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
