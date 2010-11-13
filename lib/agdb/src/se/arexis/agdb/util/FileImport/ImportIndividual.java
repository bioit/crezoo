/*
 * ImportIndividual.java
 *
 * Created on December 16, 2002, 5:37 PM
 */

package se.arexis.agdb.util.FileImport;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;
import java.util.*;
import java.io.*;
import java.sql.*;

import java.text.*;


/**
 *
 * @author  heto
 */
public class ImportIndividual extends ImportData
{
    public ImportIndividual()
    {
        CREATE = true;
        UPDATE = true;
        CREATE_OR_UPDATE = true;
        SUID = true;
        
        dependency.add(new Dependency("INDIVIDUAL", new String[] {} ));
        
        try
        {
            headers = new ArrayList<FileHeader>();
            headers.add(new FileHeader("INDIVIDUAL","LIST",1,'\t'));
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
        return "INDIVIDUAL";
    }
    
    /** An list of error messages passed between methods in this class */
    private ArrayList errorList;
        
   /**
    * Import Individual data to the database 
    *
    * Modes = CREATE, UPDATE, CREATE_OR_UPDATE
    *
    * @return result of the import. True = ok, false = not ok
    */   
    public boolean imp() 
    {
        boolean res = false;
        String errMessage = null;
        DbImportFile dbInFile = new DbImportFile();
      
        try
        {
            Errors.logDebug("IMP!!! ifid="+ifid);
            
            String filename = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            dbInFile.setStatus(conn_viss,ifid,"0%");
            
            // Create the individual 
            DbIndividual dbIndividual = new DbIndividual();
            //FileParser fileParser = new FileParser(upPath + "/" +  systemFileName);
            FileParser fileParser = new FileParser(filename);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.INDIVIDUAL,
                                                                            FileTypeDefinition.LIST));
            // If add updateMethod
            if (updateMethod == null || updateMethod.equalsIgnoreCase("CREATE"))
            {
                dbIndividual.CreateIndividuals(fileParser, connection,
                                                  sampleUnitId,
                                                  Integer.parseInt(userId));
            }

            // If update updateMethod
            else if (updateMethod.equalsIgnoreCase("UPDATE"))
            {
                dbIndividual.UpdateIndividuals(fileParser, connection,
                                                  sampleUnitId,
                                                  Integer.parseInt(userId)); 
            }

            // if both update and add
            else if (updateMethod.equalsIgnoreCase("CREATE_OR_UPDATE"))
            {
                dbIndividual.CreateOrUpdateIndividuals(fileParser,
                                                          connection,
                                                          sampleUnitId,
                                                          Integer.parseInt(userId));
            }

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
     * Check for valid strings and values. Constraints are enforced here.
     *
     * @param identity
     * @param alias
     * @param father
     * @param mother
     * @param sex
     * @param birth_date
     * @param comment
     * @return  */    
    private void checkValues(String identity, String alias, String father, String mother, String sex, String birth_date, String comment)
    {
        boolean ret = true;
        if(birth_date.trim().equals(""))
            birth_date = null;
        
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
        
        if (alias == null || alias.trim().equals(""))
        {
            alias = "";
        }
        else if (alias.length() > 11)
        {
            errorList.add("Alias [" + alias + "] exceeds 11 characters.");
            ret = false;
        }
        
        if (father == null)
        {
            father = "";
        }
        else if (father != null && father.length() > 11)
        {
            errorList.add("Father [" + father + "] exceeds 11 characters.");
            ret = false;
        }
        
        if (mother == null)
        {
            mother = "";
        }
        else if (mother != null && mother.length() > 11)
        {
            errorList.add("Mother [" + mother + "] exceeds 11 characters.");
            ret = false;
        }
        
        if (sex == null)
        {
            errorList.add("Sex is not valid [ U | M | F ].");
            ret = false;
        }
        else if (!sex.equals("U") && !sex.equals("M") && !sex.equals("F"))
        {
            errorList.add("Sex [" + sex + "] is not valid [ U | M | F ].");
            ret = false;
        }
        
        else if (birth_date != null) {
            try 
            {
                java.util.Date temp = java.sql.Date.valueOf(birth_date);
            }
            catch (Exception e)
            {
                errorList.add("The birth date [ "+birth_date+" ] is not valid, should have YYYY-MM-DD format.");
                ret = false;
            }
           
        }
       
        
        if (comment != null && comment.length() > 256)
        {
            errorList.add("Comment exceeds 256 characters.");
            ret = false;
        }
    }
    
    /**
     * TestObjects, testing data against the database.
     *
     * Check so individuals are unique so they can be inserted to the 
     * database in the given samplingUnit.
     *
     * Check that father and mother exists.
     *
     * @param identity
     * @param alias
     * @param father
     * @param mother
     * @return  */    
    private void checkCreate(String identity, String alias, String father, String mother)
    {
        if (!db.isIndividualUnique(identity))
        {
            errorList.add("Individual ["+ identity+"] already exists");
        }
        else
        {
            // Does the father exists?
            if (father != null && !father.trim().equals("") && db.isIndividualUnique(father))
            {
                errorList.add("The father ["+father+"] is not found");
            }
            
            // Does the mother exists?
            if (mother != null && !mother.trim().equals("") && db.isIndividualUnique(mother))
            {
                errorList.add("The mother ["+mother+"] is not found");
            }
        }
        
        // Add object to test db.
        db.setIndividual(identity,alias);
    }
    
    
    /**
     * TestObjects, testing data against the database.
     *
     * Check so individuals are exists so they can be updated
     *
     * Check that father and mother exists.
     *
     * @param identity
     * @param alias
     * @param father
     * @param mother
     * @return  */    
    private void checkUpdate(String identity, String alias, String father, String mother)
    {
        if (db.isIndividualUnique(identity))
        {
            errorList.add("Individual ["+identity+"] does not exist");
        }
        else
        {
            // Does the father exists?
            if (father != null && !father.trim().equals("") && db.isIndividualUnique(father))
            {
                errorList.add("The father ["+father+"] is not found");
            }
            
            // Does the mother exists?
            if (mother != null && !mother.trim().equals("") && db.isIndividualUnique(mother))
            {
                errorList.add("The mother ["+mother+"] is not found");
            }
        }
    }
    
    /**
     * TestObjects, testing data against the database.
     *
     * Check so create or update method will work.
     *
     * @param identity
     * @param alias
     * @param father
     * @param mother
     * @return  */    
    private void checkCreateOrUpdate(String identity, String alias, String father, String mother)
    {
        if (db.isIndividualUnique(identity))
        {
            checkCreate(identity,alias,father,mother);
        }
        else
        {
            checkUpdate(identity,alias, father, mother);
        }
    }
    
    /**
     * Check the LIST format
     *
     * Loop all rows in the fileParser, check all values 
     *
     * @param fp
     * @param fileOut
     * @param delim
     * @return  */    
    private String checkList(FileParser fp , FileWriter fileOut, char delim)
    {
        String identity, alias, father, mother, sex, birth_date, comment;
        String errorMsg = "";
        
        int dataRows = fp.dataRows();        
        DbImportFile dbInFile = new DbImportFile();
        
        int numOfErrors = 0;
        String statusStr;
        double status;
        double status_last = 0.0;
        
        for (int i=0;i<dataRows;i++)
        {
            errorList = new ArrayList();
            
            identity    = fp.getValue("IDENTITY",i).trim();
            alias       = fp.getValue("ALIAS",i).trim();
            father      = fp.getValue("FATHER",i).trim();
            mother      = fp.getValue("MOTHER",i).trim();
            sex         = fp.getValue("SEX",i).trim();
            birth_date  = fp.getValue("BIRTH_DATE",i).trim();
            comment     = fp.getValue("COMMENT",i).trim();

            // Check for valid data values.
            // Check for length, remove null and so on.
            // Syntax check.
            checkValues(identity, alias, father, mother, sex, birth_date, comment);

            // If add updateMethod
            if (updateMethod == null || updateMethod.equalsIgnoreCase("CREATE"))
            {
                checkCreate(identity, alias, father, mother);
            }

            // If update updateMethod
            else if (updateMethod.equalsIgnoreCase("UPDATE"))
            {
                checkUpdate(identity, alias, father, mother);
            }

            // if both update and add
            else if (updateMethod.equalsIgnoreCase("CREATE_OR_UPDATE"))
            {
                checkCreateOrUpdate(identity, alias, father, mother);
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
                errorList = null;
                //fatalErrors = new ArrayList();
                fileOut.write(identity+delim+alias+delim+father+delim+mother+delim+sex+delim+birth_date+delim+comment+"\n");
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
        Errors.logDebug("Errors="+numOfErrors);
        if (numOfErrors>0)
        {
            errorMsg = "Import of the individuals failed. <br>Errors: "+numOfErrors;
        }
        return errorMsg;
    }
    
    /**
     * The classes must implement the check method
     * @return  */
    public boolean check()
    {
        Errors.logDebug("CheckIndividual started");
        boolean res = false;
        DbImportFile dbInFile = new DbImportFile();
        String filename = "";
        try
        {
            dbInFile.setStatus(conn_viss,ifid,"0%");
            
            
            
            // Create the individual 
            DbIndividual dbIndividual = new DbIndividual();
            
            //String fullFileName = upPath + "/" + pid + "/" + isid + "/" + systemFileName;
            filename = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            String checkFileName = filename + "_checked";
            
            Errors.logDebug("filename="+filename);
            
            //FileHeader header = FileParser.scanFileHeader(fullFileName);
            DbImportFile dbin = new DbImportFile();
            FileHeader header = AbstractFileParser.parseHeader(dbin.getImportFileHeader(conn_viss,ifid));
            
            String type = header.formatTypeName().toUpperCase();
            char delimiter = header.delimiter().charValue();
            
            FileParser fileParser = new FileParser(filename);
            fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.INDIVIDUAL,
                                                                            FileTypeDefinition.LIST));
            
            //FileHeader header = new FileHeader().
            
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
            
            /* 
             * Check all values 
             */
            String errorMsg = "";
            errorMsg = checkList(fileParser, fileOut, delimiter);
            fileOut.close();
            
            /* 
             * Save the file to database
             */
            dbInFile.saveCheckedFile(conn_viss, ifid, checkFileName);
            
            /*
             * Delete files uploaded
             */
            File tmp = new File(checkFileName);
            tmp.delete();
            
            tmp = new File(filename);
            tmp.delete();
                      
            /*
             * Set an message to the user of the result of the check
             */
            if (errorMsg.length()>0)
            {
                res = false;
                dbInFile.setStatus(conn_viss,ifid,"ERROR");
                
                // Add a message to the log
                dbInFile.addErrMsg(conn_viss,ifid,"File checked failed for sampling unit "+DbSamplingUnit.getSUName(conn_viss,Integer.toString(sampleUnitId)) +". <br>"+errorMsg );
                Errors.logDebug("ErrorMsg="+errorMsg);
            }
            else
            {
                res = true;
                dbInFile.setStatus(conn_viss,ifid,"CHECKED");
                
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
        }
        Errors.logDebug("CheckIndividual completed");
        return res;
    }
}
