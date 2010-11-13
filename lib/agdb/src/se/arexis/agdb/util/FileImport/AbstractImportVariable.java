/*
 * AbstractImportVariable.java
 *
 * Created on den 5 april 2004, 14:14
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
public abstract class AbstractImportVariable extends ImportData 
{
    protected ArrayList errorList;
    
       /** Warning list */
    protected ArrayList warningList;
    /** Creates a new instance of AbstractImportVariable */
    public AbstractImportVariable() {
    }
    
   
    protected void checkValues(String variable, String type, String unit, String comment)
    {
        if (variable == null || variable.trim().equals(""))
        {
            errorList.add("Unable to read variable name.");
        }
        else if (variable.length() > 20)
        {
            errorList.add("Variable exceeds 20 characters.");
        }
        
        if(type == null || type.trim().equals(""))
        {
            errorList.add("Unable to read variable type.");
        }
        else if (type.length() > 1)
        {
            errorList.add("Type exceeds 1 character.");
        }
        else if(!type.trim().equals("E") && !type.trim().equals("N"))
        {
            errorList.add("The type has to be E or N.");
        }    
       
        
        if (unit.length() > 10)
        {
            errorList.add("Unit exceeds 10 characters");
        }
        
        if (comment.length() > 256)
        {
            errorList.add("Comment exceeds 10 characters");
        }
    }
    
     protected void checkCreate(String variable)
    {
        if (!db.isVariableUnique(variable))
        {
            errorList.add("Variable ["+ variable+"] already exists");
        }
        // Add object to test db.
        db.setVariable(variable);
    }
    
    protected void checkUpdate(String variable)
    {
        
    }
    
    protected void checkCreateOrUpdate(String variable)
    {
        
    }
    
    /** Checks that the values in the files have the right format and
     * that the variables does not exist if the it is create mode. 
     * The values are checked for each row in the file and the the errors are
     * added to the errorList (which is protected). The errors are written
     * to the outFile (the file where the errors are stored, declared in check())
     * for each row and is also "cleared" for each row so that the same errors 
     * are not written more than on time. 
     */
    
     protected String checkList(FileParser fp, FileWriter fileOut, char delim)
    {
        Errors.logDebug("ImportVariables.checkList(...)");
        String errorMsg = "";
        int dataRows = fp.dataRows();
        int numOfErrors = 0;
        int numOfWarnings = 0;
        
        String variable, type, unit, comment;
        //String chr, marker, alias, position, primer1, primer2, comment;
        DbImportFile dbInFile = new DbImportFile();
        String statusStr;
        double status;
        double status_last = 0.0;
        
        Errors.logDebug(db.getDebug());
        Errors.logDebug("DataRows="+dataRows);
        warningList = new ArrayList();
        errorList = new ArrayList();
        for (int i=0;i<dataRows;i++)
        {    
            variable    = fp.getValue("VARIABLE",i);
            type        = fp.getValue("TYPE",i);
            unit        = fp.getValue("UNIT",i);           
            comment     = fp.getValue("COMMENT",i);
            
            // Check for valid data values.
            // Check for length, remove null and so on.
            // Syntax check.
            checkValues(variable,type,unit,comment);

            // If add updateMethod
            if (updateMethod == null || updateMethod.equalsIgnoreCase("CREATE"))
            {
                checkCreate(variable);
            }

            // If update updateMethod
            else if (updateMethod.equalsIgnoreCase("UPDATE"))
            {
                warningList.add("Update updateMethod was chosen but create updateMethod was executed.");
                Errors.logWarn("AbstractImportVariable.checkList(...) "+
                "UPDATE updateMethod was chosen but it is not supported by import variables. CREATE updateMethod is used instead.");
                checkCreate(variable);
            }

            // if both update and add
            else if (updateMethod.equalsIgnoreCase("CREATE_OR_UPDATE"))
            {
                warningList.add("Create or update updateMethod was chosen but create updateMethod was executed.");
                Errors.logWarn("AbstractImportVariable.checkList(...) "+
                "CREATE OR UPDATE updateMethod was chosen but it is not supported by import variables. CREATE updateMethod is used instead.");
                checkCreate(variable);
            }
            
            /**
             * Write errors to a commented file
             */
            try
            {
                fileOut.write(variable+delim+type+delim+unit+delim+comment+"\n");
                //Errors.logDebug(errList.toString());
                numOfErrors += errorList.size();
                for (int j=0;j<errorList.size();j++)
                {
                    fileOut.write("#Error: " + (String)errorList.get(j)+"\n");
                }
                
                numOfWarnings += warningList.size();
                for (int j=0;j<warningList.size();j++)
                {
                    fileOut.write("#Warning: " + (String)warningList.get(j)+"\n");
                }
                
                //fatalErrors = new ArrayList();
                
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
          //  writeListErrors(fileOut);
            errorList.clear();  //
            warningList.clear();
            
        }
        /* Write a meaningful message to the user. */
        Errors.logDebug("Errors="+numOfErrors);
       
        if (numOfErrors>0)
            errorMsg = "ERROR: Import of the variable set file failed.";
        else if (numOfWarnings>0)
            errorMsg = "WARNING: Some warnings exist in the variable set file";
        else 
            errorMsg = "Variable set file is correct";
        errorMsg += "<br>\nWarnings:"+numOfWarnings+"<br>\nErrors:"+numOfErrors;
        /*  if (errorList.size()>0)
            errorMsg = "ERROR: Import of the variable file failed.";
        else if (warningList.size()>0)
            errorMsg = "WARNING: Some warnings exist in the variable file";
        else 
            errorMsg = "Variable file is correct";
        errorMsg += "<br>\nWarnings:"+warningList.size()+"<br>\nErrors:"+errorList.size();
*/
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
    
}
