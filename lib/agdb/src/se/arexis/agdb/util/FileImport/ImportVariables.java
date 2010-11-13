/*
 * ImportVariables.java
 *
 * $Log$
 * Revision 1.10  2005/01/31 12:59:04  heto
 * Making stronger separation of the import modules.
 *
 * Revision 1.9  2004/12/14 08:37:34  heto
 * Added capabilities
 *
 * Revision 1.8  2004/04/13 09:00:26  wali
 * Bugfix
 *
 * Revision 1.7  2004/04/06 14:37:32  wali
 * Added warning text
 *
 * Revision 1.6  2004/04/05 13:11:40  wali
 * Extracted common classes. Inherits abstractImportVariable.
 *
 * Revision 1.5  2004/03/26 15:00:12  heto
 * Fixed log messages
 *
 * Revision 1.4  2004/03/18 10:36:46  heto
 * Changed status message
 *
 * Revision 1.3  2004/03/09 14:21:23  heto
 * Fixed alot of bugs in else if clauses then checking syntax for values
 *
 * Revision 1.2  2004/03/08 12:08:16  heto
 * Changed connection to conn_viss for all queries that should be vissible outside the import process transaction. This is important for error handling, can rollback the database
 *
 * Revision 1.1  2004/02/25 13:54:35  heto
 * Added import of variable data file
 *
 *
 * Created on February 25, 2004, 10:53 AM
 */

package se.arexis.agdb.util.FileImport;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;

import java.util.*;
import java.io.*;

/**
 *
 * @author  heto
 */
public class ImportVariables extends AbstractImportVariable
{
    /**
     * Return the format name
     */
    public String getFormat()
    { 
        return "VARIABLE";
    }
    
    private ArrayList errorList;
    
    /** Creates a new instance of ImportVariables */
    public ImportVariables() 
    {
        CREATE=true;
        SUID=true;
        try
        {
            headers = new ArrayList<FileHeader>();
            headers.add(new FileHeader("VARIABLE","LIST",1,'\t'));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public boolean check() 
    {
        Errors.logDebug("CheckVariables started");
        boolean res = false;
        DbImportFile dbInFile = new DbImportFile();
        String fullFileName = "";
        String checkFileName = "";
        String errMessage = null;
        
        try
        {
            // Create the variable 
            DbVariable dbVariable = new DbVariable();
            
            dbInFile.setStatus(conn_viss,ifid,"0%");
            
            // Store the file on server filesystem
            fullFileName = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            checkFileName = fullFileName + "_checked";
            
            FileHeader header = FileParser.scanFileHeader(fullFileName);
            String type = header.formatTypeName().toUpperCase();
            char delimiter = header.delimiter().charValue();
            
            FileParser fileParser = new FileParser(fullFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.VARIABLE,
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
            }*/
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
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.VARIABLE,
                                                                        FileTypeDefinition.LIST));
            

            dbVariable.CreateVariables(fileParser, connection,
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
