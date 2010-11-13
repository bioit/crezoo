/*
 * GenFormat.java
 *
 * Created on March 16, 2005, 12:51 PM
 */

package se.arexis.agdb.util.FileExport;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import se.arexis.agdb.db.DbDataFile;
import se.arexis.agdb.db.DbException;

/**
 *
 * @author heto
 */
public abstract class GenFormat extends Thread
{
    
    protected static int PROGRESS_ERROR = -1;
   
    // SQL
    protected Connection conn = null;
    
    protected FileWriter log_file = null;
    
     //-------------------------------------------------------------------------------
    protected void setProgress(int dfid, int su, int sut, int n, int t) 
	throws DbException, IOException 
    {
	String message = null;
	String progress = null;

	if (n == PROGRESS_ERROR)
	    progress = "ERROR";
	else if (su+1 == sut && n == t)
	    progress = "DONE";
	else 
	    progress = (su*100/sut) + (n*100/sut/t) + " %";

	try 
        {
            DbDataFile df = new DbDataFile();
            df.setDataFileStatus(conn, dfid, progress);
	} 
        catch (Exception e) 
        {
            e.printStackTrace();
            
	    log_file.write("setProgress [" + message + "]\n");
	    throw new DbException("Internal Error. Failed to set progress");
	}
    }
    
}
