/*
 * ImportUMarker.java
 *
 * Created on April 5, 2004, 1:13 PM
 *
 * $Log$
 * Revision 1.8  2005/01/31 12:59:04  heto
 * Making stronger separation of the import modules.
 *
 * Revision 1.7  2004/12/14 08:40:29  heto
 * Added capabilities
 *
 * Revision 1.6  2004/12/08 09:26:59  heto
 * Javadoc added
 *
 *
 */

package se.arexis.agdb.util.FileImport;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;
import java.util.*;
import java.io.*;

/**
 * The class to handle imports of UMarkers
 * @author heto
 */
public class ImportUMarkers extends AbstractImportMarker 
{
    /**
     * Return the format name
     */
    public String getFormat()
    { 
        return "UMARKER";
    }
    
    
    
    /** Creates a new instance of ImportUMarker */
    public ImportUMarkers() 
    {
        CREATE = true;
        SPECIESID = true;
        try
        {
            headers = new ArrayList<FileHeader>();
            headers.add(new FileHeader("UMARKER","LIST",1,'\t'));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Checks for syntax and semantics errors
     * @return True is all is ok, otherwise false
     */
    public boolean check() 
    {
        Errors.logInfo("CheckUMarkers started");
        boolean res = false;
        //boolean isOk = true;
        String errMessage = null;
        //String sampleUnitIdAsStr = null;
        DbImportFile dbInFile = new DbImportFile();
        
        String fullFileName = "";
        String checkFileName = "";
        
        try
        {
            dbInFile.setStatus(conn_viss,ifid,"0%");
            
            DbUMarker dbMarker = new DbUMarker();
            
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
            
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.UMARKER,
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
                dbInFile.addErrMsg(conn_viss,ifid,"File checked failed for species "+DbSpecies.getSpeciesName(conn_viss,Integer.toString(speciesId)) +". <br>"+errorMsg );
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
                dbInFile.addErrMsg(conn_viss,ifid,"File checked for Species "+DbSpecies.getSpeciesName(conn_viss,Integer.toString(speciesId)) + ".<br>"+errorMsg);

            }
            
         
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
        Errors.logInfo("CheckUMarkers completed");
        return res;
    }
    
    /**
     * Import data to the database
     * @return True if ok, otherwise false
     */
    public boolean imp() 
    {
        Errors.logInfo("ImportUMarkers.imp() started");
        boolean res = false;
        //Errors.log("SUID="+sampleUnitId+", CONN="+connection);
        String errMessage = null; 
        DbImportFile dbInFile = new DbImportFile();
        try
        {
            String fullFileName = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            
            dbInFile.setStatus(conn_viss,ifid,"0%");
        
            DbUMarker dbUMarker = new DbUMarker();
            
            FileParser fileParser = new FileParser(fullFileName);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.UMARKER,
                                                                        FileTypeDefinition.LIST));
                                                                        
            /*
             * Note! Changed the mode to default to create instead of checking it!
             */
            /*
            if (mode == null || mode.equals("CREATE"))
            {
             */
                dbUMarker.CreateUMarkers(fileParser, connection, Integer.valueOf(pid).intValue(), speciesId, Integer.parseInt(userId));
                errMessage = dbUMarker.getErrorMessage();
                Assertion.assertMsg(errMessage == null ||
                                 errMessage.trim().equals(""), errMessage);

                dbInFile.setStatus(conn_viss,ifid,"IMPORTED");
                //dbInFile.UpdateImportFile(connection,null,null,"Done",Integer.parseInt(ifid),Integer.parseInt(userId));

                // Add a message to the log
                dbInFile.addErrMsg(conn_viss,ifid,"File imported to Species "+DbSpecies.getSpeciesName(conn_viss,Integer.toString(speciesId)) +"\nNote: UMarkers are always imported in Create mode.");
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
        Errors.logInfo("ImportUMarkers.imp() ended");
        return res;
    }
    
  
}
