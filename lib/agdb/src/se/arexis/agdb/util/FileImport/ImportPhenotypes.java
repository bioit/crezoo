/*
 * ImportPhenotypes.java
 *
 * $Log$
 * Revision 1.11  2005/01/31 12:59:04  heto
 * Making stronger separation of the import modules.
 *
 * Revision 1.10  2004/12/14 08:37:12  heto
 * Added capabilities
 * Renamed variable
 *
 * Revision 1.9  2004/05/11 09:03:06  wali
 * The error and warning messages are bug fixed. DeviationMessages and DatabaseValues are not taken away, since they might be used in the future.
 *
 * Revision 1.8  2004/04/30 12:01:22  wali
 * bug fix
 *
 * Revision 1.7  2004/03/26 15:00:06  heto
 * Fixed log messages
 *
 * Revision 1.6  2004/03/26 13:45:32  heto
 * Fixed return status
 *
 * Revision 1.5  2004/03/18 10:36:46  heto
 * Changed status message
 *
 * Revision 1.4  2004/03/09 14:21:23  heto
 * Fixed alot of bugs in else if clauses then checking syntax for values
 *
 * Revision 1.3  2004/03/09 09:57:16  heto
 * add message after import
 *
 * Revision 1.2  2004/03/08 12:08:16  heto
 * Changed connection to conn_viss for all queries that should be vissible outside the import process transaction. This is important for error handling, can rollback the database
 *
 * Revision 1.1  2004/03/02 09:28:18  heto
 * Added Phenotype file importing in the new system
 *
 *
 * Created on February 27, 2004, 1:16 PM
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
public class ImportPhenotypes extends ImportData
{
    /**
     * Return the format name
     */
    public String getFormat()
    { 
        return "PHENOTYPE";
    }
    
    /** An list of error messages passed between methods in this class */
    private ArrayList errorList;
     
    /** Warning list */
    private ArrayList warningList;
    
     
    /** Creates a new instance of ImportPhenotypes */
    public ImportPhenotypes() 
    {
        CREATE=true;
        UPDATE=true;
        CREATE_OR_UPDATE=true;
        SUID=true;
        LEVEL=true;
        try
        {
            headers = new ArrayList<FileHeader>();
            headers.add(new FileHeader("PHENOTYPE","LIST",1,'\t'));
            headers.add(new FileHeader("PHENOTYPE","MATRIX",1,'\t'));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public boolean check() 
    {
        Errors.logDebug("CheckPhenotype started");
        
        boolean res = false;
        
        String errMessage = null;
        FileWriter fileOut=null;
        DbImportFile dbInFile = new DbImportFile();  
        
        String fullFileName = "";
        String checkFileName = "";
        try
        {            
            dbInFile.setStatus(conn_viss,ifid,"0%");
            
            fullFileName = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            checkFileName = fullFileName + "_checked";
            
            // Create the Phenotype 
            DbPhenotype dbPhenotype = new DbPhenotype();
            
            FileHeader header = FileParser.scanFileHeader(fullFileName);
            String type = header.formatTypeName().toUpperCase();
            char delimiter = header.delimiter().charValue();
            
            //AbstractValueFileParser fp = null;
            FileParser fp = null;
            
            if (type.equals("LIST"))
            {
                fp = new FileParser(fullFileName);                
                fp.Parse(FileTypeDefinitionList.matchingDefinitions(
                    FileTypeDefinition.PHENOTYPE,FileTypeDefinition.LIST));
            }
            else if (type.equals("MATRIX"))
            {
                fp = new FileParser(fullFileName);
                fp.Parse(FileTypeDefinitionList.matchingDefinitions(
                    FileTypeDefinition.PHENOTYPE,FileTypeDefinition.MATRIX));
            }
            
                        
            // Write out the result to a new file
            fileOut = new FileWriter(checkFileName);
            fileOut.write(header.objectTypeName()
                +"/"+header.formatTypeName()
                +"/"+header.version()
                +"/"+header.delimiter()+"\n");
            
            String titles[] = fp.columnTitles();
            for (int j=0;j<titles.length;j++)
            {
                fileOut.write(titles[j] + delimiter);
            }
            fileOut.write("\n");
            
            // Garbage collect the unused variables
            header = null;
            //fullFileName = null;
            //checkFileName = null;
            
            // Fix to upper case
            updateMethod = updateMethod.toUpperCase();
            
            Vector fatalErrors = new Vector();
            
            if (type.equals("LIST"))
                checkListTitles(titles,fatalErrors);
            else
                checkMatrixTitles(titles,fatalErrors);
            
            writeTitleErrors(fileOut,fatalErrors);
            
            String indId;
            
            if (titles[0].equals("IDENTITY"))
                indId="IDENTITY";
            else
                indId="Alias";
            
            String errMsg = "";
            if (type.equals("LIST"))
                errMsg = checkList((FileParser)fp, fileOut,delimiter,indId);
                //errMsg = checkList((FileParser)fp, fatalErrors,fileOut,delimiter,indId);
            else if (type.equals("MATRIX"))
                errMsg = checkMatrix((FileParser)fp, fatalErrors, fileOut, delimiter,indId);
            
            // Close the file
            fileOut.close();
            
            /* 
             * Save the file to database
             */
            dbInFile.saveCheckedFile(conn_viss, ifid, checkFileName);
            
            
                
            // Get the error message from the database object. If it is set an
            // error occured during the operation so an error is thrown.
            //errMessage = dbIndividual.getErrorMessage();
            //Assertion.assertMsg(errMessage == null ||
            //                     errMessage.trim().equals(""), errMessage);
            
           
            if (errMsg.startsWith("ERROR:"))
            {
                dbInFile.setStatus(conn_viss,ifid,"ERROR");
                res = false;
            }
            else if (errMsg.startsWith("WARNING:"))
            {
                dbInFile.setStatus(conn_viss,ifid,"WARNING");
                res = true;
            }
            else
            {
                dbInFile.setStatus(conn_viss,ifid,"CHECKED");
                res = true;
            }
            
            // Add a message to the log
            dbInFile.addErrMsg(conn_viss,ifid,"File checked for sampling unit "+DbSamplingUnit.getSUName(conn_viss,Integer.toString(sampleUnitId))+"<br>\n"+errMsg);

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
                 * Delete temporary file
                 */
                File tmp = new File(fullFileName);
                tmp.delete();
                tmp = null;
                
                tmp = new File(checkFileName);
                tmp.delete();
                tmp = null;
            }
            catch (Exception ignore)
            {
            }
        }
        
        Errors.logDebug("CheckPhenotype completed");
        
        return res;
    }
    
    /**
     * Check the titles if they are valid.
     * 
     * There should be 8 columns
     * { {IDENTITY | ALIAS} , VARIABLE , VALUE, DATE, REFERENCE, COMMIT }
     * @param titles
     * @param errorMessages
     * @return  
    */  
    private boolean checkListTitles(String [] titles, Vector errorMessages)
    {
        // Check the file header
        boolean errorFound=false;
        String errorStr=null;
        
        if (titles.length != 6) //8
            errorFound = true;
        else if (!(titles[0].equals("IDENTITY") || titles[0].equals("ALIAS")) ||
                !titles[1].equals("VARIABLE") ||
                !titles[2].equals("VALUE") ||
                !titles[3].equals("DATE") ||
                !titles[4].equals("REFERENCE") ||
                !titles[5].equals("COMMENT") )
            errorFound = true; //ref=6, comm=7
 
        if (errorFound)
        {
            errorStr=" Illegal headers.\n"+ //<BR>
                "# Required file headers: IDENTITY/ALIAS VARIABLE VALUE DATE REFERENCE COMMENT \n"+
                "# Headers found in file: ";

            for (int j=0; j<titles.length;j++)
            {
                errorStr = errorStr+ " " + titles[j];
            }
                  errorMessages.addElement(errorStr);
        }
        return errorFound;
    }
    
    /**
     * Check the headers of the Phenotype matrix file
     * @param titles
     * @param errorMessages
     * @param suid
     * @return  
     */
    private boolean checkMatrixTitles(String [] titles, Vector errorMessages)
    {
        boolean errorFound=false;
        String errorStr=null;
        try
        {

            if (titles.length < 2)
                errorFound = true;
            else if (!(titles[0].equals("IDENTITY") || titles[0].equals("ALIAS")))
            {
                errorFound = true;
            }
            if (errorFound)
            {
                errorStr=" Illegal headers.\n"+
                    "# Required file headers: IDENTITY/ALIAS VARIABLE1 VARIABLE2 ...\n"+
                    "# Headers found in file: ";
                for (int j=0; j<titles.length;j++)
                {
                    errorStr = errorStr+ " " + titles[j];
                }
                errorMessages.addElement(errorStr);
            }

            // now we check that the variables in header exists
            for (int i=1;i<titles.length;i++)
            {
                if (db.isVariableUnique(titles[i]))
                {
                    errorFound=true;
                    errorStr="Variable "+titles[i]+" does not exist.";
                    errorMessages.addElement(errorStr);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
        return errorFound;
    }
    
    //public String checkList(FileParser fp, Vector errorMessages, FileWriter fileOut, char delimiter, String indId)
    public String checkList(FileParser fp, FileWriter fileOut, char delimiter, String indId)
    {
        //String ind, marker, allele1, allele2, raw1, raw2, ref, comm;
        String ind,variable,value,date,ref,comm;
        
        String errMsg = "";

        int nrErrors = 0;
        int nrWarnings = 0;
        int nrDeviations=0;
            
        int dataRows = fp.dataRows();
        String titles[] = fp.columnTitles();
        
        DbImportFile dbInFile = new DbImportFile();
        String statusStr;
        double status;
        double status_last = 0.0;
        
        warningList = new ArrayList();
        errorList = new ArrayList();
        
        for (int i=0;i<dataRows;i++)
        {
          //  Vector errorMessages = new Vector();
     //      Vector warningMessages = new Vector();
            Vector deviationMessages = new Vector();
            Vector databaseValues = new Vector();
            
            ind     = ((FileParser)fp).getValue(indId,i).trim();
            variable= ((FileParser)fp).getValue("VARIABLE",i).trim();
            value   = ((FileParser)fp).getValue("VALUE",i).trim();
            date    = ((FileParser)fp).getValue("DATE",i).trim();
            ref     = ((FileParser)fp).getValue("REF",i).trim();
            comm    = ((FileParser)fp).getValue("COMMENT",i).trim();
                
            // Check for valid data values.
            // Check for length, remove null and so on.
            // Syntax check.
         
            checkValues(ind, variable, value, date, ref, comm); 
          //  checkValues(ind, variable, value, date, ref, comm, fatalErrors);

            // If create updateMethod
            if (updateMethod == null || updateMethod.equals("CREATE"))
                checkCreate(titles[0],ind, variable,value,date,ref,comm, sampleUnitId);

            
            
            // If update updateMethod
            else if (updateMethod.equals("UPDATE"))
                checkUpdate(titles[0],ind, variable,value,date,ref,comm,
                        sampleUnitId, deviationMessages,
                        databaseValues,delimiter);

            // if both update and add
            else if (updateMethod.equals("CREATE_OR_UPDATE"))
                checkCreateOrUpdate(titles[0],ind, variable,value,
                        date,ref,comm,sampleUnitId,deviationMessages,
                        databaseValues,delimiter);
            
           
            nrErrors+=errorList.size();
            nrDeviations+=deviationMessages.size();
            nrWarnings+=warningList.size();
                
            // write row + all errors encountered to file
            
            writeListErrors(fileOut,deviationMessages,
                databaseValues,ind,delimiter,
                variable,value,date,ref,comm);
      
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
            
            errorList.clear();  
            warningList.clear();
        }
       
        if (nrErrors>0)
            errMsg = "ERROR: Import of the Phenotypes failed.";
        else if (nrWarnings>0)
            errMsg = "WARNING: Some warnings exist in the import file";
        else 
            errMsg = "Phenotype file is correct";
        errMsg += "\nDeviations:"+nrDeviations+"\nWarnings:"+nrWarnings+"\nErrors:"+nrErrors;
        
        return errMsg;
    }
    
    private boolean checkValues(String identity, String variable, String value, String date, String ref, String comm)
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
        
        if (variable == null || variable.trim().equals(""))
        {
            errorList.add("Unable to read variable.");
            ret = false;
        }
        else if (variable.length() > 20)
        {
            errorList.add("Variable [" + variable + "] exceeds 20 characters.");
            ret = false;
        }
     
        if (value == null || value.trim().equals(""))
        {
            errorList.add("Unable to read value.");
            ret = false;
        }
        
        if (value != null && value.length() > 20)
        {
            errorList.add("Value [" + value + "] exceeds 20 characters.");
            ret = false;
        }
        
      
        if(!(date==null || date.trim().equals(""))){
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
        
        if (ref != null && ref.length() > 32)
        {
            errorList.add("Reference [" + ref + "] exceeds 32 characters.");
            ret = false;
        }
        
        if (comm != null && comm.length() > 256)
        {
            errorList.add("Comment exceeds 256 characters.");
            ret = false;
        }
        return ret;
    }
    
       
    
    /**
     * Compares all values to whats already in the database
     * returns the number of errors found.
     *
     */
    
    private void checkCreate( String id_or_alias,
                          String ind,
                          String variable,
                          String value,
                          String date,
                          String ref,
                          String comm,
                          int suid)
    {

        //ResultSet rset;
        int nrErrors=0;
        String identity=null;
        try
        {  
            //First if no IDENTITY was sent, we need to get it through alias!
            
            if (id_or_alias.equalsIgnoreCase("ALIAS") 
                && (ind!=null)
                && (!ind.trim().equalsIgnoreCase("")))
            {
                identity = db.getIdentity(ind);
            }
            else
            {
                identity=ind;
            }
            
            //compare to database, is this unique? (can it be inserted?)
            if (!db.isPhenotypeUnique(variable,identity))
            {
                // the phenotype exists
                String Message =" The Phenotype [V="+variable+", I="+identity+"] already exists, cannot be created.";
                errorList.add(Message);
                nrErrors ++;
            }
          
            // Does the individual exist?
            if (db.isIndividualUnique(identity))
            {
                // the Individual does not exist
                String Message =" The Individual with "+ id_or_alias+" "+ind+" does not exist.";
                errorList.add(Message);
            }
            
            
            // does variable exist?
            if (db.isVariableUnique(variable))
            {
                // the variable does not exist
                String Message =" Variable "+variable+" does not exist";
                errorList.add(Message);
            }
        }
        catch (Exception e)
        {
             // Flag for error and set the errMessage if it has not been set
             e.printStackTrace(System.err);
        }
    }
    

    /**
     * Checks if the phenotype exists
     * Makes certain the genotype can be updated
     */
    private void checkUpdate(  String id_or_alias,
                              String ind,
                              String variable,
                              String value,
                              String date,
                              String ref,
                              String comm,
                              int suid,
                              Vector deviationMessages,
                              Vector databaseValues,
                              char delim)
    {
        //ResultSet rset;
        String identity=null;
        boolean match=false;

        try
        {
           
            //First if no IDENTITY was sent, we need to get it through alias!
            if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
                && !ind.trim().equalsIgnoreCase(""))
            {
                identity = db.getIdentity(ind);
            }
            else   
                identity = ind;
                
            //compare to database, does genotype exist? can it be updated..
            if (db.isPhenotypeUnique(variable,identity))
            {
                String Message ="The Phenotype does not exist, cannot be updated.";
                errorList.add(Message);
            }
            //rset.close();
        }// try
        catch (Exception e)
        {
            // Flag for error and set the errMessage if it has not been set
            e.printStackTrace(System.err);
        }
    }
    
    /**
     * Checks if the genotype exists, that alleles exists etc.
     * Makes certain the genotype can be updated
     */
    private void checkCreateOrUpdate(  String id_or_alias,
                              String ind,
                              String variable,
                              String value,
                              String date,
                              String ref,
                              String comm,
                              int suid,
                              Vector deviationMessages,
                              Vector databaseValues,
                              char delim)
    {
        //ResultSet rset;
        String identity=null;
        boolean match=false;

        try
        {
           

            
            //First if no IDENTITY was sent, we need to get it through alias!
            if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
                && !ind.trim().equalsIgnoreCase(""))
            {
                identity = db.getIdentity(ind);
            }
            else   
                identity = ind;
            

            //compare to database, does genotype exist? can it be updated..
            if (!db.isPhenotypeUnique(variable,identity))
            {
                // Genotype exists -- check for update
                
                checkUpdate(id_or_alias, ind, variable, value, date, ref, comm, suid,
                            deviationMessages, databaseValues, delim);
            }
            else
            {
                // Genotype doesn't exist
                checkCreate(id_or_alias, ind, variable, value, date, ref, comm, suid);  
              //  checkCreate(id_or_alias, ind, variable, value, date, ref, comm, suid,
                //    errorMessages,warningMessages); 
            }
        }// try
        catch (Exception e)
        {
             // Flag for error and set the errMessage if it has not been set
             e.printStackTrace(System.err);
        }
    }
    
    /**
     * @param fileOut
     * @param errorMessages
     * @param warningMessages
     * @param deviationMessages
     * @param databaseValues
     * @param ind
     * @param delimeter
     * @param marker
     * @param allele1
     * @param allele2
     * @param raw1
     * @param raw2
     * @param ref
     * @param comm  
     */
    
    private void writeTitleErrors(FileWriter fileOut,
                                Vector titleError)
                                
    {
        try
        {
            
            if(titleError.size()>0)
            {
                fileOut.write("#--------------------------------------------------\n");
                for(int i=0;i<titleError.size();i++)
                {
                   fileOut.write("#"+ (String)titleError.elementAt(i)+"\n"); 
                }
                fileOut.write("#--------------------------------------------------\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }
    
   
    private void writeListErrors(FileWriter fileOut,
                                Vector deviationMessages, Vector databaseValues,
                                String ind,char delimeter,
                                String variable, String value,String date,
                                String ref,String comm)
                                
    {
        try
        {

            if(errorList.size()>0 || deviationMessages.size()>0 || warningList.size()>0)
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

            if(deviationMessages.size()>0)
            {
                for (int i=0;i<deviationMessages.size();i++)
                {
                    fileOut.write("#"+ (String)deviationMessages.elementAt(i)+"\n");
                }
                // write old values
                fileOut.write("#"+databaseValues.elementAt(0)+"\n");
            }
            // if there are errors, the string is "Outcommented"
            if(errorList.size()>0)
            {
                fileOut.write("#");
            }

            // write original string
            fileOut.write(ind+delimeter+variable+delimeter+value+
                      delimeter+date+delimeter+ref+delimeter+comm+"\n");

            if(errorList.size()>0 || deviationMessages.size()>0 || warningList.size()>0)
            {
                fileOut.write("#--------------------------------------------------\n");
            }

        }//try
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }
  
    public String checkMatrix(FileParser fp, 
        Vector fatalErrors, FileWriter fileOut, char delimiter, String indId)
    {
        
        String errMsg = "";
        //String ind, marker = "", allele1 = "", allele2 = ""; //, raw1, raw2; //, //ref; //, comm;
        String ind = "", variable = "", value = "";
        //String alleles[];
        
        int nrErrors = 0;
        int nrWarnings = 0;
        int nrDeviations=0;
        
        /*
        Vector errorMessages = new Vector();
        Vector warningMessages = new Vector();
        Vector deviationMessages = new Vector();
        Vector databaseValues = new Vector();
        */ 
        
        
        DbImportFile dbInFile = new DbImportFile();
        String statusStr;
        double status;
        double status_last = 0.0;
        
        int dataRows = fp.dataRows();
        String titles[]  = fp.columnTitles();
        String variables[] = new String[titles.length-1];
        for (int i = 0; i < variables.length; i++)
            variables[i] = titles[i+1];
        
        Vector deviationMessages    = null;
        Vector databaseValues       = null;
        Vector newAlleles           = null;
        Vector values               = null;
        
        warningList = new ArrayList();
        errorList = new ArrayList();
        
        for (int row = 0; row < fp.dataRows(); row++)
        {
            deviationMessages   = new Vector();
            databaseValues      = new Vector();
            values              = new Vector();
            
            ind = fp.getValue(indId, row);
            
          
            //newAlleles = new Vector();
            
            // check the whole row
            for (int mNum = 0; mNum < variables.length; mNum++)
            {
                //String old_alleles[]=null;
                variable = variables[mNum];
                value = fp.getValue(variable, row);
                
                // Add the values for error writing
                values.add(value);
                
                // check that values exist, have correct length etc
                checkValues(ind, variable, value, null, null, null);
                
                if (updateMethod.equals("CREATE"))
                    checkCreate(titles[0],ind, variable,value,null,null,null, 
                        sampleUnitId);
                
                
                else if (updateMethod.equals("UPDATE"))
                    checkUpdate(titles[0],ind, variable,value,null,null,null,
                        sampleUnitId,deviationMessages,
                        databaseValues,delimiter);                
                
                else if (updateMethod.equals("CREATE_OR_UPDATE"))
                    checkCreateOrUpdate(titles[0],ind, variable,value,
                        null,null,null,sampleUnitId,deviationMessages,
                        databaseValues,delimiter);
               
            }//for markers

            nrErrors+=errorList.size();
            nrDeviations+=deviationMessages.size();
            nrWarnings+=warningList.size();

            writeMatrixErrors(fileOut,deviationMessages,
                    databaseValues,values, ind,delimiter,
                    variable,value);

            
            
            /*
            //newAlleles= new Vector();
            databaseValues = new Vector();
            errorMessages=new Vector();
            warningMessages=new Vector();
            deviationMessages=new Vector();
            */
            /*
             * Set the status of the import, visible to the user
             */
            status = (new Double(row*100/(1.0*dataRows))).doubleValue();            
            if (status_last + 5 < status)
            {
                status_last = status;                
                statusStr = Integer.toString((new Double(status)).intValue()) + "%";
                dbInFile.setStatus(conn_viss,ifid,statusStr);
            }
            errorList.clear();  
            warningList.clear();
        }// for rows

        if (nrErrors>0)
            errMsg = "ERROR: Import of the genotypes failed.";
        else if (nrWarnings>0)
            errMsg = "WARNING: Some warnings exist in the import file";
        else 
            errMsg = "Genotype file is correct";        
        errMsg += "\nDeviations:"+nrDeviations+"\nWarnings:"+nrWarnings+"\nErrors:"+nrErrors;

        return errMsg;
        
        
    }
    
    /**
     * @param fileOut
     * @param errorMessages
     * @param warningMessages
     * @param deviationMessages
     * @param databaseValues
     * @param newAlleles
     * @param ind
     * @param delim
     * @param marker
     * @param allele1
     * @param allele2  
     */
    private void writeMatrixErrors(FileWriter fileOut,Vector deviationMessages,
                Vector databaseValues, Vector values,String ind, char delim,
                String variable, String value)
    {
        try
        {
            // if row contains comments
            if(errorList.size()>0 || deviationMessages.size()>0 || warningList.size()>0)
            {
                fileOut.write("#--------------------------------------------------------\n");
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
                for(int i=0;i<warningList.size();i++)
                {
                    fileOut.write("#"+ (String)warningList.get(i)+"\n");
                }
            }

            if(deviationMessages.size()>0)
            {
                for(int i=0;i<deviationMessages.size();i++)
                {
                    fileOut.write("#"+ (String)deviationMessages.elementAt(i)+"\n");
                }
                //write database values
                fileOut.write("#"+ind);
                for (int i=0;i<databaseValues.size();i++)
                {
                    fileOut.write(delim+ (String)databaseValues.elementAt(i));
                }
                fileOut.write("\n");
            }
            // write row from file:
            if(errorList.size() > 0)
            {
                fileOut.write("#");
            }
            fileOut.write(ind);
            
            for (int i=0;i<values.size();i++)
            {
                fileOut.write(delim+(String)values.elementAt(i));
            }
            fileOut.write("\n");
            
           
            
            
            /*
            for (int i=0;i<newAlleles.size();i++)
            {
                fileOut.write(delim+(String)newAlleles.elementAt(i));
            }
            fileOut.write("\n");
            */

            //fileOut.write(delim+allele1);
            //fileOut.write(delim+allele2);
            //fileOut.write("\n");
            
            if(errorList.size()>0 || deviationMessages.size()>0 || warningList.size()>0)
            {
                fileOut.write("#--------------------------------------------------------\n");
            }
        }//try
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }

    
    public boolean imp() 
    {
        boolean res = false;
        String errMessage = null;

        DbImportFile dbInFile = new DbImportFile();
        DbPhenotype dbp = new DbPhenotype();
        String fullFileName = null;
        
        try
        {
            Errors.logInfo("CheckPhenotype started");
            //connection.setAutoCommit(false);            
            dbInFile.setStatus(conn_viss,ifid,"0%");
            
            fullFileName = dbInFile.storeImportFileBLOB(conn_viss, ifid);
         
            FileHeader header = FileParser.scanFileHeader(fullFileName);
            FileParser fileParser = new FileParser(fullFileName);
            
            // Set status
            dbInFile.setStatus(conn_viss,ifid,"10%");
            
            // Ensure file format is list or matrix
            Assertion.assertMsg(header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.LIST) ||
                             header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.MATRIX),
                             "Format type name should be list or matrix " +
                             "but found found " + header.formatTypeName());

            // If file is a list
            if (header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.LIST))
            {
               fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.PHENOTYPE,
                                                                           FileTypeDefinition.LIST));
               dbInFile.setStatus(conn_viss,ifid,"20%");
               
               if (updateMethod.equals("CREATE"))
               {
                  dbp.CreatePhenotypesList(fileParser, connection,
                                           sampleUnitId, Integer.valueOf(userId).intValue());
               }
               else if (updateMethod.equals("UPDATE"))
               {
                  dbp.UpdatePhenotypesList(fileParser, connection,
                                           sampleUnitId, Integer.valueOf(userId).intValue()); 
               }
               else if (updateMethod.equals("CREATE_OR_UPDATE"))
               {
                  dbp.CreateOrUpdatePhenotypesList(fileParser, connection,
                                                   sampleUnitId, Integer.valueOf(userId).intValue()); 
               }
            }

            // If file is a matrix
            else if (header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.MATRIX))
            {
               fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.PHENOTYPE,
                                                                           FileTypeDefinition.MATRIX));
               dbInFile.setStatus(conn_viss,ifid,"20%");
               
               if (updateMethod.equals("CREATE"))
               {
                  dbp.CreatePhenotypesMatrix(fileParser, connection,
                                             sampleUnitId, Integer.valueOf(userId).intValue()); 
               }
               else if (updateMethod.equals("UPDATE"))
               {
                  dbp.UpdatePhenotypesMatrix(fileParser, connection,
                                             sampleUnitId, Integer.valueOf(userId).intValue());
               }
               else if (updateMethod.equals("CREATE_OR_UPDATE"))
               {
                  dbp.CreateOrUpdatePhenotypesMatrix(fileParser,
                                                     connection,
                                                     sampleUnitId,
                                                     Integer.valueOf(userId).intValue()); 
               }
            }
            errMessage = dbp.getErrorMessage();
         
            Assertion.assertMsg(errMessage == null ||
                             errMessage.trim().equals(""), errMessage);
            
            dbInFile.setStatus(conn_viss,ifid,"IMPORTED");

            // Add a message to the log
            dbInFile.addErrMsg(conn_viss,ifid,"File imported for sampling unit "+DbSamplingUnit.getSUName(conn_viss,Integer.toString(sampleUnitId)));
            res = true;
            
            Errors.logInfo("Check Phenotype ended");
        }
        catch (Exception e) 
        {
            // Flag for error and set the errMessage if it has not been set
            //isOk = false;
            dbInFile.setStatus(conn_viss,ifid,"ERROR");
            
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
    
}
