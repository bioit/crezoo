/*
 * ImportIndividual.java
 *
 * Created on December 16, 2002, 5:37 PM
 *
 * $Log$
 * Revision 1.19  2005/01/31 12:59:04  heto
 * Making stronger separation of the import modules.
 *
 * Revision 1.18  2004/12/14 08:36:45  heto
 * Added capabilities
 *
 * Revision 1.17  2004/12/08 09:26:36  heto
 * Javadoc added
 *
 * Revision 1.16  2004/04/28 08:53:54  wali
 * Bug fix
 *
 * Revision 1.15  2004/04/05 11:30:04  heto
 * Added abstract class as a common ground
 *
 * Revision 1.14  2004/03/26 15:00:06  heto
 * Fixed log messages
 *
 * Revision 1.13  2004/03/26 07:26:42  heto
 * Fixing debug messages.
 *
 * Revision 1.12  2004/03/22 14:02:55  heto
 * Error in import file set status
 *
 * Revision 1.11  2004/03/18 10:36:46  heto
 * Changed status message
 *
 * Revision 1.10  2004/03/09 14:21:23  heto
 * Fixed alot of bugs in else if clauses then checking syntax for values
 *
 * Revision 1.9  2004/03/08 12:08:16  heto
 * Changed connection to conn_viss for all queries that should be vissible outside the import process transaction. This is important for error handling, can rollback the database
 *
 * Revision 1.8  2004/02/16 15:57:42  heto
 * Adding support for Blob instead of file operation.
 * Completed the function for checks in Create&Update mode
 *
 * Revision 1.7  2003/11/05 07:47:02  heto
 * Refined the import system
 *
 * Revision 1.6  2003/05/15 06:40:37  heto
 * Changed the return type from void to boolean for check and imp.
 * Added a detailed status report of the progress for check.
 * Working with getting messages to the user
 *
 * Revision 1.5  2003/05/09 14:49:48  heto
 * Check process is integrated to the importProcess
 *
 * Revision 1.4  2003/05/02 07:58:45  heto
 * Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
 * Modified configuration and source files according to package change.
 *
 * Revision 1.3  2003/04/25 09:16:18  heto
 * Changed the message type of import files.
 *
 * Revision 1.2  2003/01/15 09:57:19  heto
 * Comments added
 * Check method added (not finished)
 *
 */

package se.arexis.agdb.util.FileImport;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;

import java.util.*;
import java.io.*;

/**
 * Class for handling Import of Markers. 
 *
 * @author  heto
 */
public class ImportMarkers extends AbstractImportMarker
{  
    public ImportMarkers()
    {
        CREATE = true;
        UPDATE = true;
        CREATE_OR_UPDATE = true;
        SUID = true;
        try
        {
            headers = new ArrayList<FileHeader>();
            headers.add(new FileHeader("MARKER","LIST",1,'\t'));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Return the format name
     */
    public String getFormat()
    { 
        return "MARKER";
    }
    
    
    /**
     * Import the data
     * @return boolean value true if result is ok, otherwise false
     */
    public boolean imp() 
    {
        Errors.logInfo("ImportMarkers.imp() started");
        boolean res = false;
        //Errors.log("SUID="+sampleUnitId+", CONN="+connection);
        String errMessage = null; 
        DbImportFile dbInFile = new DbImportFile();
        try
        {
            String fullFileName = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            
            dbInFile.setStatus(conn_viss,ifid,"0%");
        
            DbMarker dbMarker = new DbMarker();
            
            FileParser fileParser = new FileParser(fullFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.MARKER,
                                                                        FileTypeDefinition.LIST));
                                                                        
            /*
             * Note! Changed the mode to default to create instead of checking it!
             */
            /*
            if (mode == null || mode.equals("CREATE"))
            {
             */
                dbMarker.CreateMarkers(fileParser, connection, sampleUnitId, Integer.parseInt(userId));
                errMessage = dbMarker.getErrorMessage();
                Assertion.assertMsg(errMessage == null ||
                                 errMessage.trim().equals(""), errMessage);

                dbInFile.setStatus(conn_viss,ifid,"IMPORTED");
                //dbInFile.UpdateImportFile(connection,null,null,"Done",Integer.parseInt(ifid),Integer.parseInt(userId));

                // Add a message to the log
                dbInFile.addErrMsg(conn_viss,ifid,"File imported to sampling unit "+DbSamplingUnit.getSUName(conn_viss,Integer.toString(sampleUnitId)) +"\nNote: Markers are always imported in Create mode.");
                res = true;
            /*
            }
            
            else if (mode.equals("UPDATE"))
            {
                dbInFile.setStatus(connection,ifid,"DONE");
                dbInFile.addErrMsg(connection,ifid,"File NOT imported to sampling unit "+DbSamplingUnit.getSUName(connection,Integer.toString(sampleUnitId))+". Marker file only support CREATE.");
            }
            
            else if (mode.equals("CREATE_OR_UPDATE"))
            {
                dbInFile.setStatus(connection,ifid,"DONE");
                dbInFile.addErrMsg(connection,ifid,"File NOT imported to sampling unit "+DbSamplingUnit.getSUName(connection,Integer.toString(sampleUnitId))+". Marker file only support CREATE.");
            }
            */
            
        }
        catch (Exception e) 
        {
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
        Errors.logInfo("ImportMarkers.imp() ended");
        return res;
    }
    
    

    
    
    
    /**
     * Check the data for syntax and symantics
     * @return boolean value true if all ok, otherwise false
     */
    public boolean check()
    {
        Errors.logInfo("CheckMarkers started");
        Errors.logInfo("CheckMarkers started.....[]");
        boolean res = false;
        String errMessage = null;
        //String sampleUnitIdAsStr = null;
        DbImportFile dbInFile = new DbImportFile();
        
        String fullFileName = "";
        String checkFileName = "";
        
        try
        {
            dbInFile.setStatus(conn_viss,ifid,"0%");
            
            // Store the file on server filesystem
            fullFileName = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            checkFileName = fullFileName + "_checked";
            
            FileHeader header = FileParser.scanFileHeader(fullFileName);
            String type = header.formatTypeName().toUpperCase();
            char delimiter = header.delimiter().charValue();
            
            
            // Create the individual 
            //DbIndividual dbIndividual = new DbIndividual();
            //FileParser fileParser = new FileParser(upPath + "/" +  systemFileName);
            FileParser fileParser = new FileParser(fullFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.MARKER,
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
            
            /**
             * Run the checks for all rows
             */
            String errorMsg = "";
            errorMsg = checkList(fileParser,fileOut,delimiter);
            fileOut.close();
            
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
            e.printStackTrace();
            // Flag for error and set the errMessage if it has not been set
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
                File tmp = new File(checkFileName);
                tmp.delete();

                tmp = new File(fullFileName);
                tmp.delete();
            }
            catch (Exception ignore)
            {
            }
        }
        Errors.logInfo("CheckMarkers completed");
        return res;
    }
    
    
}