/*
 * ImportIndividual.java
 *
 * Created on December 16, 2002, 5:37 PM
 *
 * $Log$
 * Revision 1.17  2005/01/31 12:59:04  heto
 * Making stronger separation of the import modules.
 *
 * Revision 1.16  2004/12/14 08:34:44  heto
 * Added capabilities
 * Renamed variable
 *
 * Revision 1.15  2004/05/11 09:00:11  wali
 * logInfo bug fix
 *
 * Revision 1.14  2004/04/06 09:13:59  heto
 * Deviations results in warning messages
 *
 * Revision 1.13  2004/03/26 15:00:06  heto
 * Fixed log messages
 *
 * Revision 1.12  2004/03/26 13:45:15  heto
 * Fixed return status
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
 * Revision 1.8  2004/02/16 15:56:09  heto
 * Adding support for Blob instead of file operation.
 *
 * Revision 1.7  2003/11/05 07:46:33  heto
 * Refined the import system
 *
 * Revision 1.6  2003/05/15 06:39:01  heto
 * Changed the return type from void to boolean for check and imp.
 * Added a detailed status report of the progress for check.
 *
 * Revision 1.5  2003/05/09 14:49:48  heto
 * Check process is integrated to the importProcess
 *
 * Revision 1.4  2003/05/02 07:58:45  heto
 * Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
 * Modified configuration and source files according to package change.
 *
 * Revision 1.3  2003/04/25 09:17:23  heto
 * Changed the message type of import files.
 * Experiment with checkSyntax functions.
 *
 * Revision 1.2  2003/01/15 09:56:16  heto
 * Comments added
 * Check method added (not finished)
 *
 */

package se.arexis.agdb.util.FileImport;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.*;
import java.sql.*;

import java.util.*;
import java.io.*;

/**
 *
 * @author  heto
 */
public class ImportGenotypes extends ImportData
{
    public ImportGenotypes()
    {
        CREATE=true;
        UPDATE=true;
        CREATE_OR_UPDATE=true;
        SUID=true;
        
        try
        {
            headers = new ArrayList<FileHeader>();
            headers.add(new FileHeader("GENOTYPE","LIST",1,'\t'));
            headers.add(new FileHeader("GENOTYPE","MATRIX",1,'\t'));
            //This is a test!!!
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
        return "GENOTYPE";
    }
    
    public boolean imp()
    {
        boolean res = false;
        String errMessage = null;
        String uploadMode = updateMethod;

        
        Vector genoDiffs = null;
        FileParser fileParser = null;
        MatrixFileParser matrixParser = null;
        
        DbGenotype dbGenotype = new DbGenotype();
        DbImportFile dbInFile = new DbImportFile();

        String fullFileName = "";
        try
        {
            //connection.setAutoCommit(false);            
            dbInFile.setStatus(conn_viss,ifid,"0%");
            
            fullFileName = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            
            if(systemFileName!=null && !systemFileName.trim().equalsIgnoreCase(""))
            {
                // Get the header information from the file to determine if we
                // are reading a list or a matrix file.
                FileHeader header = FileParser.scanFileHeader(fullFileName);
                
                dbInFile.setStatus(conn_viss,ifid,"10%");
                
                // Ensure file format is list or matrix
                Assertion.assertMsg(header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.LIST) ||
                             header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.MATRIX),
                             "Format type name should be list or matrix " +
                             "but found found " + header.formatTypeName());
                
                
                Errors.log("Format=" + header.formatTypeName());
                Errors.log("uploadMode="+uploadMode);
                
                
                // If file is list
                if (header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.LIST))
                {
                    fileParser = new FileParser(fullFileName);
                    fileParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.GENOTYPE,
                                                                           FileTypeDefinition.LIST));
                    
                    dbInFile.setStatus(conn_viss,ifid,"25%");

                    // first, we create any missing alleles in the database
                    dbGenotype.CreateAllelesList(fileParser, connection,
                                                  sampleUnitId,
                                                 Integer.parseInt(userId));
                    
                    dbInFile.setStatus(conn_viss,ifid,"50%");

                    if (uploadMode.equals("CREATE"))
                    {
                        dbGenotype.CreateGenotypesList(fileParser, connection,
                                                 level, sampleUnitId,
                                                 Integer.parseInt(userId));
                    }
                    else if (uploadMode.equals("UPDATE"))
                    {
                        dbGenotype.UpdateGenotypesList(fileParser,
                                                 connection,level,
                                                 sampleUnitId,  Integer.parseInt(userId),
                                                 maxDev);
                    }
                    else if (uploadMode.equals("CREATE_OR_UPDATE"))
                    {
                        dbGenotype.CreateOrUpdateGenotypesList(fileParser,
                                                         connection, level,
                                                         sampleUnitId,
                                                         Integer.parseInt(userId),
                                                         maxDev);
                    }
                    
                    dbInFile.setStatus(conn_viss,ifid,"75%");
                }

                // if file is a matrix
                else if (header.formatTypeName().equalsIgnoreCase(FileTypeDefinition.MATRIX))
                {
                    //dbInFile.setStatus(connection,ifid,"10%");
                    
                    matrixParser = new MatrixFileParser(fullFileName);
                    matrixParser.Parse(FileTypeDefinitionList.matchingDefinitions(FileTypeDefinition.GENOTYPE,
                                                                           FileTypeDefinition.MATRIX));
                    
                    dbInFile.setStatus(conn_viss,ifid,"25%");

             
                    // first, we create any missing alleles in the database
                    dbGenotype.CreateAllelesMatrix(matrixParser, connection,
                                                  sampleUnitId,
                                                 Integer.parseInt(userId));

                    dbInFile.setStatus(connection,ifid,"50%");

                    if (uploadMode.equals("CREATE"))
                    {
               
                        dbGenotype.CreateGenotypesMatrix(matrixParser,
                                                   connection, level,
                                                   sampleUnitId, Integer.parseInt(userId));
                    }
                    else if (uploadMode.equals("UPDATE"))
                    {
 

                        dbGenotype.UpdateGenotypesMatrix(matrixParser,
                                                   connection, level,
                                                   sampleUnitId,
                                                   Integer.parseInt(userId),maxDev);
                    }
                    else if (uploadMode.equals("CREATE_OR_UPDATE"))
                    {


                        dbGenotype.CreateOrUpdateGenotypesMatrix(matrixParser,
                                                           connection,
                                                           level,
                                                           sampleUnitId,
                                                           Integer.parseInt(userId),
                                                           maxDev);
                    }
                    dbInFile.setStatus(conn_viss,ifid,"75%");
                }

                errMessage = dbGenotype.getErrorMessage();
                Errors.log("ImportGenotypes, Import errors: "+errMessage);
                
                Assertion.assertMsg(errMessage == null ||
                             errMessage.trim().equals(""), errMessage);
                
                //connection.commit();
                
                dbInFile.setStatus(conn_viss,ifid,"IMPORTED");
                //dbInFile.UpdateImportFile(connection,null,null,"Done",Integer.parseInt(ifid),Integer.parseInt(userId));
                
                // Add a message to the log
                dbInFile.addErrMsg(conn_viss,ifid,"File imported to sampling unit "+DbSamplingUnit.getSUName(conn_viss,Integer.toString(sampleUnitId)) );
                
                res = true;
            }
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
            }
            catch (Exception ignore)
            {
            }
        }
        return res;
    }
    
    /**************************************************************************
     * Check 
     *************************************************************************/
    
       /**
     * Check the titles if they are valid.
     * 
     * There should be 8 columns
     * { {IDENTITY | ALIAS} , MARKER , ALLELE1, ALLELE2, RAW1, RAW2, REFERENCE, COMMIT }
     * @param titles
     * @param errorMessages
     * @return  
    */  
    private boolean checkListTitles(String [] titles, Vector errorMessages)
    {
        // Check the file header
        boolean errorFound=false;
        String errorStr=null;
        if (titles.length != 8)
            errorFound = true;
        else if (!(titles[0].equals("IDENTITY") || titles[0].equals("ALIAS")) ||
                !titles[1].equals("MARKER") ||
                !titles[2].equals("ALLELE1") ||
                !titles[3].equals("ALLELE2") ||
                !titles[4].equals("RAW1") ||
                !titles[5].equals("RAW2") ||
                !titles[6].equals("REFERENCE") ||
                !titles[7].equals("COMMENT") )
            errorFound = true;

        if (errorFound)
        {
            errorStr="Illegal headers.<BR>"+
                "Required file headers: IDENTITY/ALIAS MARKER ALLELE1 ALLELE2 RAW1 RAW2 REFERENCE COMMENT<BR>"+
                "Headers found in file:";

            for (int j=0; j<titles.length;j++)
            {
                errorStr = errorStr+ " " + titles[j];
            }
                  errorMessages.addElement(errorStr);
        }
        return errorFound;
    }
    
    
    /**
     * Check the headers of the genotype matrix file
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
            // System.err.println("checkMatrixtitles..");
            if (titles.length < 2)
                errorFound = true;
            else if (!(titles[0].equals("IDENTITY") || titles[0].equals("ALIAS")))
            {
                errorFound = true;
            }
            if (errorFound)
            {
                errorStr="Illegal headers.<BR>"+
                    "Required file headers: IDENTITY/ALIAS MARKER1 MARKER2 ...<BR>"+
                    "Headers found in file:";
                for (int j=0; j<titles.length;j++)
                {
                    errorStr = errorStr+ " " + titles[j];
                }
                errorMessages.addElement(errorStr);
            }

            // now we check that the markers in header exists
            for (int i=1;i<titles.length;i++)
            {
                if (db.isMarkerUnique(titles[i]))
                {
                    errorFound=true;
                    errorStr="Marker "+titles[i]+" does not exist.";
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
    
    /**
     * Check the genotype values. Check for existance and length of the fields. 
     *
     * @param identity      The identity (/ alias?)
     * @param marker        The marker 
     * @param allele1       The allele value
     * @param allele2       The allele value 
     * @param raw1          The raw value
     * @param raw2          The raw value
     * @param ref           The reference field
     * @param comm          The comment field
     * @param errMessages   Returns a Vector of error messages (String)
     * @return              Boolean, true if values is ok, or false if something is not ok.
     */
    private boolean checkValues(String identity, String marker,
                                   String allele1, String allele2,
                                   String raw1, String raw2,
                                   String ref, String comm, Vector errMessages) 
    {
        boolean ret = true;

        if (identity == null || identity.trim().equals(""))
        {
             errMessages.addElement("Unable to read Identity/Alias.");
             ret = false;
        }
        else if (identity.length() > 11)
        {
            errMessages.addElement("Identity/Alias [" + identity + "] exceeds 11 characters.");
            ret = false;
        }
        
        if (marker == null || marker.trim().equals(""))
        {
            errMessages.addElement("Unable to read marker.");
            ret = false;
        }
        else if (marker.length() > 20)
        {
            errMessages.addElement("Marker [" + marker + "] exceeds 20 characters.");
            ret = false;
        }
        
        if (allele1 != null && allele1.length() > 20)
        {
            errMessages.addElement("Allele1 [" + allele1 + "] exceeds 20 characters.");
            ret = false;
        }
        
        if (allele2 != null && allele2.length() > 20)
        {
            errMessages.addElement("Allele2 [" + allele2 + "] exceeds 20 characters.");
            ret = false;
        }
        
        if (raw1 != null && raw1.length() > 20)
        {
            errMessages.addElement("Raw1 [" + raw1 + "] exceeds 20 characters.");
            ret = false;
        }
        
        if (raw2 != null && raw2.length() > 20)
        {
            errMessages.addElement("Raw2 [" + raw2 + "] exceeds 20 characters.");
            ret = false;
        }
        
        if (ref != null && ref.length() > 32)
        {
            errMessages.addElement("Reference [" + ref + "] exceeds 32 characters.");
            ret = false;
        }
        
        if (comm != null && comm.length() > 256)
        {
            errMessages.addElement("Comment exceeds 256 characters.");
            ret = false;
        }
        return ret;
    }
    
    /**
     * Compares all values to whats already in the database
     * returns the number of errors found.
     */
    private void checkCreate( String id_or_alias,
                          String ind,
                          String marker,
                          String allele1,
                          String allele2,
                          int suid,
                          Vector errorMessages,
                          Vector warningMessages)
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
            if (!db.isGenotypeUnique(marker,identity))
            {
                // the genotype exists
                String Message =" The Genotype [M="+marker+", I="+identity+"] already exists, cannot be created.";
                errorMessages.addElement(Message);
                nrErrors ++;
            }
          
            // Does the individual exist?
            if (db.isIndividualUnique(identity))
            {
                // the Individual does not exist
                String Message =" The Individual with "+ id_or_alias+" "+ind+" does not exist.";
                errorMessages.addElement(Message);
            }
            
            
            // does marker exist?
            if (db.isMarkerUnique(marker))
            {
                // the marker does not exist
                String Message =" Marker "+marker+" does not exist";
                errorMessages.addElement(Message);
            }
            else
            {
                // do the alleles exist for these markers??
                if (db.isAlleleUnique(allele1,marker))
                {
                    // the allele does not exist
                    String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker+". It will be created if genotype is imported!";
                    warningMessages.addElement(Message);
                }
                
                if (db.isAlleleUnique(allele2,marker))
                {
                    // the marker does not exist
                    String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is imported!";
                    warningMessages.addElement(Message);
                }
            }
        }
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
    private void checkUpdate( String id_or_alias,
                              String ind,
                              String marker,
                              String allele1,
                              String allele2,
                              //String raw1,
                              //String raw2,
                              //String reference,
                              //String comment,
                              int suid,
                              Vector errorMessages,
                              Vector deviationMessages,
                              Vector warningMessages,
                              Vector databaseValues,
                              char delim)
    {
        //ResultSet rset;
        String identity=null;
        boolean match=false;

        try
        {
            /*
            String mname=null;
            String a1=null;
            String a2=null;
            String r1=null;
            String r2=null;
            String ref=null;
            String comm =null;
            */

            //First if no IDENTITY was sent, we need to get it through alias!
            if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
                && !ind.trim().equalsIgnoreCase(""))
            {
                identity = db.getIdentity(ind);
            }
            else   
                identity = ind;
                
            //compare to database, does genotype exist? can it be updated..
            if (!db.isGenotypeUnique(marker,identity))
            {
                // Genotype exists
                
                // do the alleles exist for these markers??
                // only check if allele not null
                if(allele1 != null && !allele1.trim().equalsIgnoreCase(""))
                {
                    if (db.isAlleleUnique(allele1,marker))
                    {
                        // the allele does not exist
                        String Message =" Warning! Allele "+allele1+" does not exist for marker "+marker+". It will be created if genotype is imported!";
                        warningMessages.addElement(Message);
                    }
                }
                if(allele2 != null && !allele2.trim().equalsIgnoreCase(""))
                {   
                    if (db.isAlleleUnique(allele2,marker))
                    {
                        // the marker does not exist
                        String Message =" Warning! Allele "+allele2+" does not exist for marker "+marker+". It will be created if genotype is imported!";
                        warningMessages.addElement(Message);
                    }
                }
                
                // Compare genotypes to the db.
                int pos = db.indexOfGenotype(identity,marker);
                
                if (pos <0 )
                    Errors.logDebug("Genotype not found");
                
                String a1 = db.getGenotypeA1(pos);
                String a2 = db.getGenotypeA2(pos);
                
                if(a1.equalsIgnoreCase(allele1) && a2.equalsIgnoreCase(allele2))
                    match = true;
                else if(a1.equalsIgnoreCase(allele2) && a2.equalsIgnoreCase(allele1))
                    match = true;
             
                if(!match)
                {
                    // genotype differs
                    deviationMessages.addElement("#Genotype differs from database, se below (old top, new bottom)");

                    //System.err.println("mname="+mname+"\n");
                    databaseValues.addElement(ind+delim+delim+a1+delim+a2+delim+delim);                        
                }
            }
            else // genotype does not exist
            {
                String Message ="The Genotype does not exist, cannot be updated.";
                errorMessages.addElement(Message);
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
                              String marker,
                              String allele1,
                              String allele2,
                              //String raw1,
                              //String raw2,
                              //String reference,
                              //String comment,
                              int suid,
                              Vector errorMessages,
                              Vector warningMessages,
                              Vector deviationMessages,
                              Vector databaseValues,
                              char delim)
    {
        //ResultSet rset;
        String identity=null;
        boolean match=false;

        try
        {
            String mname=null;
            String a1=null;
            String a2=null;
            String r1=null;
            String r2=null;
            String ref=null;
            String comm =null;

            
            //First if no IDENTITY was sent, we need to get it through alias!
            if(id_or_alias.equalsIgnoreCase("ALIAS")&& ind!=null
                && !ind.trim().equalsIgnoreCase(""))
            {
                identity = db.getIdentity(ind);
            }
            else   
                identity = ind;
            

            //compare to database, does genotype exist? can it be updated..
            if (!db.isGenotypeUnique(marker,identity))
            {
                // Genotype exists -- check for update
                
                checkUpdate(id_or_alias, ind, marker, allele1, allele2, suid,
                    errorMessages,deviationMessages, warningMessages, databaseValues, delim);
                
                /*
                 *
                 * String id_or_alias,
                              String ind,
                              String marker,
                              String allele1,
                              String allele2,
                              //String raw1,
                              //String raw2,
                              //String reference,
                              //String comment,
                              int suid,
                              Vector errorMessages,
                              Vector deviationMessages,
                              Vector warningMessages,
                              Vector databaseValues,
                              char delim
                 */
            }
            else
            {
                // Genotype doesnt exist
                checkCreate(id_or_alias, ind, marker, allele1, allele2, suid,
                    errorMessages,warningMessages);       
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
    private void writeListErrors(FileWriter fileOut,Vector errorMessages,Vector warningMessages,
                                Vector deviationMessages, Vector databaseValues,
                                String ind,char delimeter,
                                String marker, String allele1,String allele2,
                                String raw1,String raw2,String ref,String comm)
    {
        try
        {

            if(errorMessages.size()>0 || deviationMessages.size()>0 || warningMessages.size()>0)
            {
                fileOut.write("#--------------------------------------------------\n");
            }
            if(errorMessages.size()>0)
            {
                for(int i=0;i<errorMessages.size();i++)
                {
                    fileOut.write("#"+ (String)errorMessages.elementAt(i)+"\n");
                }
            }
            if(warningMessages.size()>0)
            {
                for (int i=0;i<warningMessages.size();i++)
                {
                    fileOut.write("#"+ (String)warningMessages.elementAt(i)+"\n");
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
            if(errorMessages.size()>0)
            {
                fileOut.write("#");
            }

            // write original string
            fileOut.write(ind+delimeter+marker+delimeter+allele1+
                      delimeter+allele2+delimeter+raw1+delimeter+
                      raw2+delimeter+ref+delimeter+comm+"\n");

            if(errorMessages.size()>0 || deviationMessages.size()>0 || warningMessages.size()>0)
            {
                fileOut.write("#--------------------------------------------------\n");
            }

        }//try
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
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
    private void writeMatrixErrors(FileWriter fileOut,Vector errorMessages,Vector warningMessages,
                Vector deviationMessages,Vector databaseValues, Vector newAlleles, 
                String ind, char delim,String marker, String allele1, String allele2)
    {
        try
        {
            // if row contains comments
            if(errorMessages.size()>0 || deviationMessages.size()>0 || warningMessages.size()>0)
            {
                fileOut.write("#--------------------------------------------------------\n");
            }
            if(errorMessages.size()>0)
            {
                for(int i=0;i<errorMessages.size();i++)
                {
                    fileOut.write("#"+ (String)errorMessages.elementAt(i)+"\n");
                }
            }

            if(warningMessages.size()>0)
            {
                for(int i=0;i<warningMessages.size();i++)
                {
                    fileOut.write("#"+ (String)warningMessages.elementAt(i)+"\n");
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
            if(errorMessages.size() > 0)
            {
                fileOut.write("#");
            }
            fileOut.write(ind);
            
            
            for (int i=0;i<newAlleles.size();i++)
            {
                fileOut.write(delim+(String)newAlleles.elementAt(i));
            }
            fileOut.write("\n");
            

            //fileOut.write(delim+allele1);
            //fileOut.write(delim+allele2);
            //fileOut.write("\n");
            
            if(errorMessages.size()>0 || deviationMessages.size()>0 || warningMessages.size()>0)
            {
                fileOut.write("#--------------------------------------------------------\n");
            }
        }//try
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }
    
    public String checkList(FileParser fp, Vector fatalErrors, FileWriter fileOut, char delimiter, String indId)
    {
        String ind, marker, allele1, allele2, raw1, raw2, ref, comm;
        
        String errMsg = "";
        
        int nrErrors = 0;
        int nrWarnings = 0;
        int nrDeviations=0;
            
        int dataRows = fp.dataRows();
        String titles[] = fp.columnTitles();
        
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
        
        for (int i=0;i<dataRows;i++)
        {
            Vector errorMessages = new Vector();
            Vector warningMessages = new Vector();
            Vector deviationMessages = new Vector();
            Vector databaseValues = new Vector();
            
            ind     = ((FileParser)fp).getValue(indId,i).trim();
            marker  = ((FileParser)fp).getValue("MARKER",i).trim();
            allele1 = ((FileParser)fp).getValue("ALLELE1",i).trim();
            allele2 = ((FileParser)fp).getValue("ALLELE2",i).trim();
            raw1    = ((FileParser)fp).getValue("RAW1",i).trim();
            raw2    = ((FileParser)fp).getValue("RAW2",i).trim();
            ref     = ((FileParser)fp).getValue("REF",i).trim();
            comm    = ((FileParser)fp).getValue("COMMENT",i).trim();
                
            // Check for valid data values.
            // Check for length, remove null and so on.
            // Syntax check.
            checkValues(ind, marker, allele1, allele2, raw1,raw2,  ref, comm, fatalErrors);

            // If create updateMethod
            if (updateMethod == null || updateMethod.equals("CREATE"))
                checkCreate(titles[0],ind, marker,allele1,allele2,sampleUnitId,errorMessages,warningMessages);
            //raw1,raw2,ref,comm,

            // If update updateMethod
            else if (updateMethod.equals("UPDATE"))
                checkUpdate(titles[0],ind, marker,allele1,allele2,
                        sampleUnitId,errorMessages, deviationMessages,
                        warningMessages, databaseValues,delimiter);
            
                //checkUpdate();

            // if both update and add
            else if (updateMethod.equals("CREATE_OR_UPDATE"))
                checkCreateOrUpdate(titles[0],ind, marker,allele1,
                        allele2,sampleUnitId,errorMessages,warningMessages,
                        deviationMessages,databaseValues,delimiter);
            
                //checkCreateOrUpdate();
                
            nrErrors+=errorMessages.size();
            nrDeviations+=deviationMessages.size();
            nrWarnings+=warningMessages.size();
                
            // write row + all errors encountered to file
            writeListErrors(fileOut,errorMessages,warningMessages,
                deviationMessages,databaseValues,ind,delimiter,
                marker,allele1,allele2,raw1,raw2,ref,comm);
            
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
        if (nrErrors>0)
            errMsg = "ERROR: Import of the genotypes failed.";
        else if (nrWarnings>0)
            errMsg = "WARNING: Some warnings exist in the import file";
        else if (nrDeviations>0)
            errMsg = "WARNING: Deviations exists in import file";
        else 
            errMsg = "Genotype file is correct";
        errMsg += "<br>\nDeviations:"+nrDeviations+"<br>\nWarnings:"+nrWarnings+"<br>\nErrors:"+nrErrors;
        
        return errMsg;
    }
    
    public String checkMatrix(MatrixFileParser mfp, 
        Vector fatalErrors, FileWriter fileOut, char delimiter, String indId)
    {
        String errMsg = "";
        String ind, marker = "", allele1 = "", allele2 = ""; //, raw1, raw2; //, //ref; //, comm;
        String alleles[];
        
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
        
        int dataRows = mfp.dataRows();
        String titles[]  = mfp.columnTitles();
        String markers[] = new String[titles.length-1];
        for (int i = 0; i < markers.length; i++)
            markers[i] = titles[i+1];
        
        Vector errorMessages        = null;
        Vector warningMessages      = null;
        Vector deviationMessages    = null;
        Vector databaseValues       = null;
        Vector newAlleles           = null;
        
        for (int row = 0; row < mfp.dataRows(); row++)
        {
            errorMessages       = new Vector();
            warningMessages     = new Vector();
            deviationMessages   = new Vector();
            databaseValues      = new Vector();
            
            ind = mfp.getValue(indId, row)[0];
            
            //System.err.println("ind="+ind);
            newAlleles = new Vector();
            // check the whole row
            for (int mNum = 0; mNum < markers.length; mNum++)
            {
                String old_alleles[]=null;
                marker = markers[mNum];
                alleles = mfp.getValue(marker, row);
                allele1 = alleles[0].trim();
                allele2 = alleles[1].trim();
                // store all alleles on this row
                newAlleles.addElement(allele1); // For writing error matrix errors
                newAlleles.addElement(allele2); // -"-
                // check that values exist, have correct length etc
                checkValues(ind, marker,allele1,allele2,null,
                              null,null,null, errorMessages);
                
                //System.err.println("Marker="+marker);

                if (updateMethod.equals("CREATE"))
                    checkCreate(titles[0],ind, marker,allele1,allele2,
                        sampleUnitId,errorMessages, warningMessages);
                
                
                else if (updateMethod.equals("UPDATE"))
                    checkUpdate(titles[0],ind, marker,allele1,allele2,
                        sampleUnitId,errorMessages, deviationMessages,
                        warningMessages, databaseValues,delimiter);                
                
                else if (updateMethod.equals("CREATE_OR_UPDATE"))
                    checkCreateOrUpdate(titles[0],ind, marker,allele1,
                        allele2,sampleUnitId,errorMessages,warningMessages,
                        deviationMessages,databaseValues,delimiter);
               
            }//for markers

            nrErrors+=errorMessages.size();
            nrDeviations+=deviationMessages.size();
            nrWarnings+=warningMessages.size();

            writeMatrixErrors(fileOut,errorMessages,warningMessages,
                deviationMessages,databaseValues,newAlleles, ind,delimiter,
                marker,allele1,allele2);

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
            
        }// for rows

        if (nrErrors>0)
            errMsg = "ERROR: Import of the genotypes failed.";
        else if (nrWarnings>0)
            errMsg = "WARNING: Some warnings exist in the import file";
        else if (nrDeviations>0)
            errMsg = "WARNING: Deviations exists in import file";
        else 
            errMsg = "Genotype file is correct";        
        errMsg += "<br>\nDeviations:"+nrDeviations+"<br>\nWarnings:"+nrWarnings+"<br>\nErrors:"+nrErrors;

        return errMsg;
    }

    /** The classes must implement the check method
     */
    public boolean check() 
    {
        Errors.logDebug("CheckGenotype started");
        
        boolean res = false;
        
        String errMessage = null;
        FileWriter fileOut=null;
        
        //String sampleUnitIdAsStr = null;
        DbImportFile dbInFile = new DbImportFile();  
        
        String fullFileName = "";
        String checkFileName = "";
        try
        {            
            dbInFile.setStatus(conn_viss,ifid,"0%");
            
            fullFileName = dbInFile.storeImportFileBLOB(conn_viss, ifid);
            checkFileName = fullFileName + "_checked";
            
            // Create the individual 
            DbGenotype dbGenotype = new DbGenotype();
            
            FileHeader header = FileParser.scanFileHeader(fullFileName);
            String type = header.formatTypeName().toUpperCase();
            char delimiter = header.delimiter().charValue();
            
            AbstractValueFileParser fp = null;
            
            if (type.equals("LIST"))
            {
                fp = new FileParser(fullFileName);                
                fp.Parse(FileTypeDefinitionList.matchingDefinitions(
                    FileTypeDefinition.GENOTYPE,FileTypeDefinition.LIST));
            }
            else if (type.equals("MATRIX"))
            {
                fp = new MatrixFileParser(fullFileName);
                fp.Parse(FileTypeDefinitionList.matchingDefinitions(
                    FileTypeDefinition.GENOTYPE,FileTypeDefinition.MATRIX));
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
            
            String indId;
            
            if (titles[0].equals("IDENTITY"))
                indId="IDENTITY";
            else
                indId="Alias";
            
            String errMsg = "";
            if (type.equals("LIST"))
                errMsg = checkList((FileParser)fp, fatalErrors,fileOut,delimiter,indId);
            else if (type.equals("MATRIX"))
                errMsg = checkMatrix((MatrixFileParser)fp, fatalErrors, fileOut, delimiter,indId);
            
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
        
        Errors.logDebug("CheckGenotype completed");
        
        return res;
    }
    
    
}