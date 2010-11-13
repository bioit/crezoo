/*
 * AbstractImportVariableSet.java
 *
 * Created on den 6 april 2004, 10:25
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
public abstract class AbstractImportVariableSet extends ImportData {
    
    protected ArrayList errorList;
    
     /** Warning list */
    protected ArrayList warningList;
    /** Creates a new instance of AbstractImportVariableSet */
    public AbstractImportVariableSet() {
    }
    
    protected void checkValues(String variable_name)
    {
        if (variable_name == null || variable_name.trim().equals(""))
        {
            errorList.add("Unable to read variable name.");
        }
        else if (variable_name.length() > 20)
        {
            errorList.add("Variable set name exceeds 20 characters.");
        }
        
       
    }
    
    protected void checkCreate(String variableSet)
    {
        //The variables should exist!
        if (db.isVariableUnique(variableSet))
        {
            errorList.add("Variable in variable set ["+ variableSet+"] does not exist.");
        }
        // Add object to test db.
        db.setVariable(variableSet);
    }
    
    protected void checkUpdate(String variableSet)
    {
        
    }
    
    protected void checkCreateOrUpdate(String variableSet)
    {
        
    }
    
     protected String checkList(FileParser fp, FileWriter fileOut, char delim)
    {
        Errors.logDebug("ImportVariablesSet.checkList(...)");
        String errorMsg = "";
        int dataRows = fp.dataRows();
        int numOfErrors = 0;
        int numOfWarnings = 0;
        
        String variable;
        //String chr, marker, alias, position, primer1, primer2, comment;
        DbImportFile dbInFile = new DbImportFile();
        String statusStr;
        double status;
        double status_last = 0.0;
        
        Errors.logDebug(db.getDebug());
        Errors.logDebug("DataRows="+dataRows);
        
        
       
        for (int i=0;i<dataRows;i++)
        {
           errorList = new ArrayList(); 
           warningList = new ArrayList(); 
            variable    = fp.getValue("VARIABLE",i);
            
            // Check for valid data values.
            // Check for length, remove null and so on.
            // Syntax check.
            checkValues(variable);

            // If add updateMethod
            if (updateMethod == null || updateMethod.equalsIgnoreCase("CREATE"))
            {
                checkCreate(variable);
            }

            // If update updateMethod
            else if (updateMethod.equalsIgnoreCase("UPDATE"))
            {   
                warningList.add("Update updateMethod was chosen, variable sets only supports create updateMethod.");
                Errors.logWarn("AbstractImportVariableSet.checkList(...) "+
                "UPDATE updateMethod was chosen but it is not supported by import variable set. CREATE updateMethod is used instead.");
                checkCreate(variable);
            }

            // if both update and add
            else if (updateMethod.equalsIgnoreCase("CREATE_OR_UPDATE"))
            {   
                warningList.add("Create or update updateMethod was chosen, variable sets only supports create updateMethod.");
                Errors.logWarn("AbstractImportVariableSet.checkList(...) "+
                "CREATE OR UPDATE updateMethod was chosen but it is not supported by import variable set. CREATE updateMethod is used instead.");
                checkCreate(variable);
            }
            
            /**
             * Write errors to a commented file
             */
            try
            {
                //Errors.logDebug(errList.toString());
                numOfErrors += errorList.size();
                numOfWarnings += warningList.size();
                if(numOfErrors > numOfWarnings)
                for (int j=0;j<errorList.size();j++)
                {
                    fileOut.write("# " + (String)errorList.get(j)+"\n");
                }
              
                //fatalErrors = new ArrayList();
                fileOut.write(variable+"\n");
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
        
        writeListErrors(fileOut);
        
        /* Write a meaningful message to the user. */
      
        Errors.logDebug("Errors="+numOfErrors);
       
        if (numOfErrors>0)
            errorMsg = "ERROR: Import of the variable set file failed.";
        else if (numOfWarnings>0)
            errorMsg = "WARNING: Some warnings exist in the variable set file";
        else 
            errorMsg = "Variable set file is correct";
        errorMsg += "<br>\nWarnings:"+numOfWarnings+"<br>\nErrors:"+numOfErrors;

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
