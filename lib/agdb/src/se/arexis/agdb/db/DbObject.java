/*
  Copyright (C) 2000 by Prevas AB. All rights reserved.

  $Log$
  Revision 1.13  2005/02/22 12:47:48  heto
  Converting *Marker files. Created the DbAbstractMarker to handle common functionallity

  Revision 1.12  2005/02/17 16:18:58  heto
  Converted DbUMarker to PostgreSQL
  Redesigned relations: r_uvar_var, r_umid_mid and r_uaid_aid due to errors in the design (redundant data in relations)
  This design change affected some views!

  Revision 1.11  2005/02/08 16:03:21  heto
  DbIndividual is now complete. Some bug tests are done.
  DbSamplingunit is converted. No bugtest.
  All transactions should now be handled in the GUI (yuck..)

  Revision 1.10  2005/02/04 15:58:40  heto
  Converting from Oracle to PostgreSQL or somewhat more SQL server independence.

  Revision 1.9  2004/04/02 14:29:10  heto
  Stream not closed correctly

  Revision 1.8  2004/03/25 15:00:24  wali
  bug fixed

  Revision 1.7  2004/03/01 08:19:06  wali
  Changed the name of replaceApostrophe to replaceSym and escapeString to replaceSymbol. Added some more symbols that should be parser out.

  Revision 1.6  2004/02/13 14:36:11  heto
  Merged a conflict.
  Method for creating files from Blob added

  Revision 1.5  2004/02/13 13:28:42  wali
  Looked at escapeString for ' handling.

  Revision 1.4  2004/02/13 08:28:08  heto
  Added function to parse strings before insertion to the db

  Revision 1.3  2004/01/27 08:02:07  heto
  Debug flag added

  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.3  2001/05/29 14:51:39  frob
  Removed getStatus method and related member variable m_errNumber.

  Revision 1.2  2001/04/24 06:31:42  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.1.1.2.3  2001/04/05 09:03:48  frob
  Un-deprecated the DB_SCHEMA field and added an explanation of it.

  Revision 1.1.1.1.2.2  2001/04/04 07:48:05  frob
  Removed some unused fields and methods.
  Deprecated some fields and methods that I think are unused.
  Commented the file.

  Revision 1.1.1.1.2.1  2001/03/29 11:10:55  frob
  Removed re-calculation of row number i buildErrorString. Now it uses the given row number,
  so it's up to the callee to pass a correct value

*/

package se.arexis.agdb.db;

import java.sql.*;
import java.io.*;
import se.arexis.agdb.util.Errors;

/**
 * The baseclass for all database classes. 
 *
 * @author Thomas Bjorklund, Prevas
 * @see Object
 */
public class DbObject extends Object
{
   /**
    * Holds the name of a package (gdbp) where the stored procedures are
    * defined. The package is stored in the file api_gdbp.sql
    */
   protected static final String DB_SCHEMA = "GdbAdm.gdbp.";
   
   /**
    * The name of the class used in error messages
    */
   String CLASS_NAME = "DbObject()";
   /** Debug variable. True displays debug info, false doesn't */   
   boolean DEBUG = false;

   /**
    * A description of the latest error. Is empty if no error has
    * occoured. 
    */
   private String m_errMessage = "";


   //////////////////////////////////////////////////////////////////////
   //
   // Constructors
   //
   //////////////////////////////////////////////////////////////////////
   

   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////
   

   /**
    * Returns a string describing the error if something went wrong.
    *
    * @return A string describing the error if something went wrong.
    *         An empty string if nothing went wrong.
    */
   public String getErrorMessage()
   {
      if (m_errMessage != null)
         return m_errMessage;
      else
         return "";
   }


   //////////////////////////////////////////////////////////////////////
   //
   // Protected section
   //
   //////////////////////////////////////////////////////////////////////

   
   /**
    * Sets the error message by concatenating the errorText, some default
    * text and the rowNumber.
    *
    * @param errorText Text to be included in the error message.
    * @param rowNumber The row where the error occured. 
    */
   protected void buildErrorString(String errorText, int rowNumber)
   {
      m_errMessage = errorText + " (Row " + rowNumber + ")";
   }
   
   
   /**
    * Sets the error message by contatenating the given errorText with some
    * default text.
    *
    * @param errorText Text to be included in the error message
    */
   protected void buildErrorString(String errorText)
   {
      m_errMessage = errorText ;
   } 
   
   /** Replace all symbols in the Strings, used for the comment.
    * @param searchString The text to parse
    * @return Returns a parsed string
    */   
   private String replaceSym(String searchString)
   { 
       int index = 0;
       int sym = 0;
       String StrOut = ""; 
     
       String symbol[] = {"#","'", "&"};
       String replace[] = {"¤!", "¤%","¤£"};
       int noSym = symbol.length; //number of symbols
       //account for any apostrophes in the parameter 
       for (sym=0; sym < noSym; sym++){
            for (index = searchString.indexOf(symbol[sym]); index != -1; index = searchString.indexOf(symbol[sym])) {           
                // Copy up to the apostrophe 
                StrOut += searchString.substring(0, index);
                StrOut += replace[sym];        
                searchString = searchString.substring(index + 1); 
            } 
             // Add the left over part. (Whole thing, if there were no symbols) 
            StrOut += searchString; 
            searchString = "";
            searchString = StrOut;
            StrOut = "";
            index=0;     
       }
 
       System.err.println("replaceSym after: " + searchString);
      
       return searchString; 
   }
     

   /**
    * Fix strings before stored in the database, changes "'" to "¤"
    * retrieveString in ArexisServlet converts it bac
    * @param txt The text to parse and fi
    * @return Returns a string that is good to import to the databas
    */
   protected String replaceSymbol(String txt)
   {
       txt.trim();
       return replaceSym(txt);
    }
   
   /** Create a file from a blob in the database
    * @param blob The blob to store to file.
    * @param filename The file is stored on file system with the following name. The path? How to
    * handle?
    */
    protected void createFileFromBLOB(Blob blob, String filename)
    {
        String out = "";
        byte[] buf = null;
        InputStream is = null;
       
        try
        {
            is = blob.getBinaryStream();
       
            buf = new byte[256 * 1024]; // 256 KB
            int bytesRead;
       
            FileOutputStream file = new FileOutputStream(filename);
               
            while ((bytesRead = is.read(buf)) != -1)
            {
                file.write(buf, 0, bytesRead);
            }
            file.flush();
            file.close();
        }
        catch (Exception e)
        {
            Errors.logError("createFileFromBlob: " + e.getMessage());
        }
   }
    
    public int getNextID(Connection conn, String sequence_name)
        throws DbException
    {
        String sql = "";
        Statement stmt = null;
        ResultSet rs = null;
        int id = 0;
        try
        {
            stmt = conn.createStatement();
            
            // For PostgreSQL
            sql = "select nextval('"+sequence_name+"') as new_id";
            
            // For Oracle
            //sql = "select "+sequence+".nextval as new_id from dual";
            
            rs = stmt.executeQuery(sql);
            
            if (rs.next())
            {
                id = rs.getInt("new_id");
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
            Errors.logError("Could not get next id: "+e.getMessage());
            throw new DbException("Database error: Next ID could not be retrieved.");
        }
        finally
        {
            try
            {
                if (stmt!=null)
                    stmt.close();
                if (rs!=null)
                    rs.close();
            }
            catch (Exception ignore)
            {}
        }
        Errors.logDebug(id+"=getNextID("+sequence_name+")");
        return id;
    }    
    
    public String getSQLDate()
    {
        // For Oracle
        // return "SYSDATE";
        
        // for PostgreSQL
        return "timestamp 'now'";
    }
    
   protected String sqlString(String str)
   {
       String out = "";
       
       if (str == null || str.trim().equals("") || str.equals("null"))
           return "null";
       else
           return "'"+str.trim()+"'";
   }
   
   
   protected String sqlNumber(String str)
   {
       return sqlInteger(str);
   }
   
   protected String sqlInteger(String str)
   {
       String out = "";
       
       if (str == null || str.equals("") || str.equals("null"))
           return "null";
       else
           return str;
   }
   
  

}

 
