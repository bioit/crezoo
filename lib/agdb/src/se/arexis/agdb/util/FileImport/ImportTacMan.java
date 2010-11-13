/*
 * ImportTacMan.java
 *
 * Created on April 1, 2005, 3:16 PM
 */

package se.arexis.agdb.util.FileImport;

import java.sql.Connection;
import java.util.ArrayList;
import se.arexis.agdb.db.DbImportFile;

/**
 *
 * @author heto
 */
public class ImportTacMan extends ImportData
{
    
    /** Creates a new instance of ImportTacMan */
    public ImportTacMan() 
    {
        CREATE = true;
        SUID = true;
        
        dependency.add(new Dependency("TACMAN", new String[] {"INDIVIDUAL"} ));
        
        try
        {
            headers = new ArrayList<FileHeader>();
            headers.add(new FileHeader("TACMAN","LIST",1,'\t'));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    public String getFormat()
    { 
        return "TACMAN";
    }
    
    public FileHeader examineFile(Connection conn, int ifid)
    {
        FileHeader hdr = null;
        try
        {
            DbImportFile dbfile = new DbImportFile();
            String row = dbfile.getImportFileHeader(conn, String.valueOf(ifid));

            String str = "Well,Sample Name,Detector,Task,CT,Std. Dev. CT,Quantity,Mean Quantity,Std. Dev. Quantity,Filtered";
            
            row = row.trim();
            str = str.trim();
            
            System.out.println(row);
            System.out.println(str);
            
            System.out.println(row.equals(str));

            if (row.equals(str))
            {
                hdr = new FileHeader("TACMAN","LIST",1,',');
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return hdr;
    }
    
    /**
     * 
     * The method that imports the data 
     *  Must be declared in all classes
     * @return Return the status of the method. True means ok, otherwise false.
     */
    public boolean imp()
    {
        return false;
    }
    
    /**
     * The check method implements syntax and semantic control of the import file.
     * This method is called in the first stage of the import process.
     * @return Return the status of the method. True means ok, otherwise false.
     */
    public boolean check()
    {
        return false;
    }
    
}
