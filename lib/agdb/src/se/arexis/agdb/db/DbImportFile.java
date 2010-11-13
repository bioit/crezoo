
package se.arexis.agdb.db;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.FileImport.*;
import se.arexis.agdb.util.Errors;

import java.util.*;
import java.sql.*;
import java.io.*;

//import javax.servlet.ServletContext;


/** Class for handling all communications to the database, and using the import_file and import_file_msg tables.
 *
 */
public class DbImportFile extends DbObject 
{
    
    /** The class name used for debug text */
    private String CLASS_NAME = "DbImportFile";

    /** Create an ImportSession in the database.
     * @return
     * @param conn
     * @param isid
     * @param name
     * @param comm
     * @param uid  */
    public int CreateImportFile( Connection conn,
        int isid,
        String name,
        String comm,        
        //String pid,
        String uid)	
    {
        String METHOD_NAME = "CreateImportFile(...)";
        Statement stmt = null;
        String sql = "";
        
        int ifid = 0;
        
        try 
        {    
            sql = "select max(ifid) + 1 as max from Import_File";
            
            
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            
            // Connect to the database
            stmt = conn.createStatement();
            
            // Get the highest id + 1
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
                ifid = rs.getInt("max");
            else
                ifid = 0;
            
            rs.close();
            stmt.close();
            stmt = conn.createStatement();
            
            sql = "insert into IMPORT_FILE (IFID,ISID,NAME,STATUS, ERRMSG,COMM,ID,TS) VALUES ("+String.valueOf(ifid)+","+String.valueOf(isid)+",'"+name+"','UPLOADED','File uploaded.','"+comm+"',"+uid+",SYSDATE)";
            //sql = "insert into IMPORT_SESSION (ISID,NAME,COMM,PID,ID) VALUES ("+String.valueOf(isid)+",'"+name+"','"+comm+"',"+String.valueOf(pid)+","+String.valueOf(id)+")";
            
            System.err.println("SQL="+sql);
            int res = stmt.executeUpdate(sql);
            
            System.err.println("RES="+res);
            conn.commit();
            
            // Check for errors!
            if (res <= 0) 
            {
                buildErrorString("");
                throw new Exception();
            }
            stmt.close();
	} 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Error in "+CLASS_NAME+"."+METHOD_NAME+"\n(" +
                e.getMessage() + ")");
        }
        finally
        {
            try 
            {
                if (stmt != null) stmt.close();
            }
            catch (SQLException sqle) 
            {
                sqle.printStackTrace(System.err);
            }
        }
        
        return ifid;
    }
    
    
    
    
    
    /** 
     * Save a file to the database import_file table
     */
    public void saveImportFile(Connection conn, int ifid, File file)
        throws DbException
    {
        try
        {
            FileInputStream fis = new FileInputStream(file);
            
            //PreparedStatement ps = conn.prepareStatement("INSERT INTO testbinary VALUES (?, ?)");
            PreparedStatement ps = conn.prepareStatement("update import_file set import_file = ?, len = ? where ifid = ?");
            
            ps.setInt(3, ifid);
            
            int length = Long.valueOf(file.length()).intValue();
            
            ps.setBinaryStream(1, fis, length);
            ps.setInt(2, length);
            
            ps.executeUpdate();
            ps.close();
            fis.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            throw new DbException("Internal error. Failed to set database file\n"+e.getMessage());
        }
    }
    
    public InputStream getCheckedFileStream(Connection conn, int ifid)
    {
        InputStream out = null;
        
        
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery 
                     ("select checked_file from import_file where ifid="+ifid);

            // get first row
            if (rset.next())
            {
               out = rset.getBinaryStream(1);
            }    
            rset.close();
            stmt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return out;
    }
    
    /**
     * This returns the file as a byte array
     * This must only be used with text files!
     */
    public byte[] getCheckedFile(Connection conn, int ifid)
    {
        FileOutputStream file = null;
        
        byte[] out = null;
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery 
                     ("select checked_file from import_file where ifid="+ifid);

            // get first row
            if (rset.next())
            {
               out = rset.getBytes(1);
               
               //rset.get
            }    
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return out;
    }

    
    /**
     * This returns the file as a byte array
     * This must only be used with text files!
     */
    public byte[] getImportFile(Connection conn, int ifid)
    {
        FileOutputStream file = null;
        
        byte[] out = null;
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery 
                     ("select import_file from import_file where ifid="+ifid);

            // get first row
            if (rset.next())
            {
               out = rset.getBytes(1);
               
               //rset.get
            }    
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return out;
    }
    
    
    public void getImportFile(Connection conn, int ifid, String filename)
    {
        FileOutputStream file = null;
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery 
                     ("select import_file from import_file where ifid="+ifid);

            // get first row
            if (rset.next())
            {
               InputStream is = rset.getBinaryStream (1);
               try
               {
                  file = new FileOutputStream (filename);
                  
                  int chunk;
                  int i = 0;
                  while ((chunk = is.read()) != -1)
                  {
                     file.write(chunk);
                     i++;
                     
                     if (i>1000)
                     {
                         file.flush();
                         i=0;
                     }
                  }
                  is.close();
                  file.close();
               }
               catch (Exception e)
               {
                  String err = e.toString();
                  System.out.println(err);
               }
            }    
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    /** Create an ImportFile in the database. This method is a test-method
     * to evaluate the use of BLOBs in the database. 
     *
     * @return returns the file id created.
     * @param conn The connection used to contact the database.
     * @param isid Import session id to connect the file to.
     * @param name The file name
     * @param comm A comment of the file
     * @param uid  the user performing the operation*/
    public int CreateImportFile2( Connection conn,
        int isid,
        String path,
        String name,
        String mimeType,
        String comm,
        //String pid,
        String uid,
        String objectName
        )
            throws FileNotFoundException, DbException
    {
        String METHOD_NAME = "CreateImportFile2(...)";
        
        DEBUG = true;
        
        
        Statement stmt = null;
        String sql = "";
        
        int ifid = 0;
        int res = 0;
        PreparedStatement pstmt = null;
        try 
        {    
            sql = "select max(ifid) + 1 as max from Import_File";
            
            if (conn.getAutoCommit() == true)
                throw new DbException("No current transaction");        
            
            // Connect to the database
            stmt = conn.createStatement();
            
            // Get the highest id + 1
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
                ifid = rs.getInt("max");
            else
                ifid = 0;
            
            rs.close();
            stmt.close();
            stmt = null;
            
            String filepath=path + "/" + name;
            Errors.logDebug("Filepath="+filepath);
            
            String insertStmt = "insert into IMPORT_FILE (IFID,ISID,NAME, " +
                    "IMPORT_TYPE, ID,TS,STATUS,OBJECT_NAME) " +
                    "VALUES (?,?,?,?,?,"+getSQLDate()+",'UPLOADED',?)";
            
            //ERRMSG,COMM,,STATUS
            pstmt = conn.prepareStatement(insertStmt);
            int ix = 1;
            pstmt.setInt(ix++, ifid);  
            if (DEBUG) System.err.println("ifid="+ifid);
            pstmt.setInt(ix++, isid);
            if (DEBUG) System.err.println("isid="+isid);
            pstmt.setString(ix++, name);
            if (DEBUG) System.err.println("name="+name);
            
            pstmt.setString(ix++,mimeType);
            //the length of the file
            //pstmt.setInt(ix++,size);
            
            if (DEBUG) System.err.println("uid="+uid);
            pstmt.setString(ix++, uid);
            pstmt.setString(ix++, objectName);
           
            res = pstmt.executeUpdate(); // Update
            System.err.println("Res="+res);
            
            if (DEBUG) System.err.println("Timeout=" + pstmt.getQueryTimeout());
            
           
            
            // Check for errors!
            if (res <= 0) 
            {
                throw new DbException("Update failed: result <= 0");
            }
           
            
            pstmt.close();
            
            
            /*
             * Upload the file 
             */
            
            File importFile = new File(filepath);
            if (importFile.length()==0) throw new Exception("File size is null");
            
            // Set the file
            saveImportFile(conn, ifid, importFile);
            
            // Delete the uploaded file. Now in db.
            importFile.delete();
	} 
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            throw new DbException("Error in "+CLASS_NAME+"."+METHOD_NAME+"\n(" +
                e.getMessage() + ")");
        }
        finally
        {
            try 
            {
                if (stmt != null) stmt.close();
            }
            catch (SQLException sqle) 
            {
                sqle.printStackTrace(System.err);
            }
        }
        
        return ifid;
    }
    
    
    public void saveCheckedFile(Connection conn, String s_ifid, String filename)
        throws DbException
    {
        try
        {
            File file = new File(filename);
            FileInputStream fis = new FileInputStream(file);
            
            int ifid = Integer.valueOf(s_ifid).intValue();
            
            //PreparedStatement ps = conn.prepareStatement("INSERT INTO testbinary VALUES (?, ?)");
            PreparedStatement ps = conn.prepareStatement("update import_file set checked_file = ?, len = ? where ifid = ?");
            
            ps.setInt(3, ifid);
            
            int length = Long.valueOf(file.length()).intValue();
            
            ps.setBinaryStream(1, fis, length);
            ps.setInt(2, length);
            
            ps.executeUpdate();
            ps.close();
            fis.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            throw new DbException("Internal error. Failed to set checked file\n"+e.getMessage());
        }
    }
    
   
    
    public void insert_chk_name(Connection conn, String name, int ifid){
        Statement stmt = null;
        String sql = "";
        try 
        {
            sql = "update import_file set chk_set_name='"+name+"' where ifid="+ifid;            
            int num = 0;
 
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            
            // Connect to the database
            stmt = conn.createStatement();
            
            int res = stmt.executeUpdate(sql);
            
            if (res <= 0)
            {
                Errors.log("DbImportFile.Insert_chk_name affected zero rows!");   
            }
            conn.commit();
            stmt.close();
            stmt = null;
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to call PL/SQL procedure\n(" +
            e.getMessage() + ")");
            
            try
            {
                conn.rollback();
            }
            catch (Exception e2)
            {
            }
        } 
        finally 
        {
            try 
            {
                //conn.commit();
                if (stmt != null) stmt.close();
            } 
            catch (SQLException sqle) 
            {
                Errors.log(sqle.getMessage());
            }
        }

    }
    
    
    public String get_chk_name(Connection conn, int ifid){
        Statement stmt = null;
        ResultSet rset = null;
        String sql = "";
        String name = "";
        try 
        {
            sql = "select chk_set_name from import_file where ifid="+ifid;            
 
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            
            // Connect to the database
            stmt = conn.createStatement();
            
            rset=stmt.executeQuery(sql);
            
            if (rset.next())
            {
                name = rset.getString("chk_set_name");
            }
            else
                Errors.log("DbImportFile.get_chk_name did not return chk_set_name!");   
            conn.commit();
            stmt.close();
            stmt = null;
        } 
                
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to call PL/SQL procedure\n(" +
            e.getMessage() + ")");
            
            try
            {
                conn.rollback();
            }
            catch (Exception e2)
            {
            }
        } 
        finally 
        {
            try 
            {
                //conn.commit();
                if (stmt != null) stmt.close();
            } 
            catch (SQLException sqle) 
            {
                Errors.log(sqle.getMessage());
            }
        }
        return name;
    }
    
    /** Update the information of a import file.
     * If name, comm or errmsg is set to null then no change will
     * occur for those variables.
     * @param conn
     * @param name
     * @param comm
     * @param errmsg
     * @param ifid
     * @param id  */
    public void UpdateImportFile( Connection conn,
        String name,
        String comm,
        String errmsg,
        int ifid,
        int id)	
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            sql = "update import_file set ";            
            int num = 0;
            
            if (name != null)
            {
                sql += "name='"+name+"'";
                num++;
            }   
            if (comm != null)
            {
                if (num>0)
                    sql += ",";
                sql += "comm='"+comm+"'";
                num++;
            }
            if (errmsg != null)
            {
                if (num>0)
                    sql += ",";
                sql += "errmsg='"+errmsg+"'";
            }
                
              
            sql += " where ifid="+ifid;
            
            
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            
            // Connect to the database
            stmt = conn.createStatement();
            
            
            int res = stmt.executeUpdate(sql);
            
            if (res <= 0)
            {
                Errors.log("DbImportFile.UpdateImportFile affected zero rows!");   
            }
            conn.commit();
            stmt.close();
            stmt = null;
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to call PL/SQL procedure\n(" +
            e.getMessage() + ")");
            
            try
            {
                conn.rollback();
            }
            catch (Exception e2)
            {
            }
        } 
        finally 
        {
            try 
            {
                //conn.commit();
                if (stmt != null) stmt.close();
            } 
            catch (SQLException sqle) 
            {
                Errors.log(sqle.getMessage());
            }
        }
    }
  
    /**
     * @param conn
     * @param isid  */    
    public void DeleteImportFile(Connection conn, int isid) 
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            sql = "delete from IMPORT_SET where isid = "+isid;
            
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            
            // Connect to the database
            stmt = conn.createStatement();
           
            
            stmt.executeUpdate(sql);
            
            stmt.close();
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to call PL/SQL procedure\n(" +
            e.getMessage() + ")");
        } 
        finally 
        {
            try
            {
                if (stmt != null) stmt.close();
            } 
            catch (SQLException sqle) 
            {}
        }
    }
    
    /** Get the status
     * @param conn The connection object
     * @param ifid Input file id
     * @return ?
     */    
    public String getStatus(Connection conn, String ifid)
    {
        Statement stmt = null;
        String sql = "";
        String out = "";
        try 
        {
            sql = "select status from IMPORT_FILE where ifid = "+ifid;
            
            
            
            
            // Connect to the database
            stmt = conn.createStatement();
            
            
            ResultSet rset = stmt.executeQuery(sql);
            
            if (rset.next())
            {
                out = rset.getString("status");
                
            }
           
            
           
            stmt.close();
            
            return out;
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to call PL/SQL procedure\n(" +
            e.getMessage() + ")");
            Errors.log("DbImportFile.setStatus failed, rollback db: "+e.getMessage());
            
            try
            {
                conn.rollback();
            }
            catch (Exception e2)
            {
                Errors.log("Rollback failed: "+e2.getMessage());
            }
        }
        finally 
        {
            try
            {
                if (stmt != null) 
                {
                    stmt.close();
                }
                
                return out;
                
            } 
            catch (SQLException sqle) 
            {}
        }
        
        return out;
    }
    
    
    
    /**
     * @param conn
     * @param ifid
     * @param status  */    
    public void setStatus(Connection conn, String ifid,String status)
    {
        Errors.logInfo("DbImportFile.setStatus(...) started");
        Errors.logDebug("Setting status to file ifid="+ifid+" to status="+status);
        Statement stmt = null;
        String sql = "";
        try 
        {
            sql = "update IMPORT_FILE set status='"+status+"' where ifid = "+ifid;
            
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            
            // Connect to the database
            stmt = conn.createStatement();
           
            
            stmt.executeUpdate(sql);
            
            stmt.close();
            
            conn.commit();
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to call PL/SQL procedure\n(" +
            e.getMessage() + ")");
            Errors.logError("DbImportFile.setStatus failed, rollback db: "+e.getMessage());
            
            try
            {
                conn.rollback();
                Errors.logWarn("DbImportFile.setStatus(...) Rollback");
            }
            catch (Exception e2)
            {
                Errors.logError("Rollback failed: "+e2.getMessage());
            }
        } 
        finally 
        {
            try
            {
                if (stmt != null) 
                {
                    stmt.close();
                }
                
            } 
            catch (SQLException sqle) 
            {}
        }
        Errors.logInfo("DbImportFile.setStatus(...) ended");
    }
    
    /** Delete all error messages
     * @param conn The connection to use for db operations.
     * @param ifid The import file id for the error messages
     */
    public void deleteAllErrMsg(Connection conn, String ifid)
    {
        //Errors.log("Adding message to file ifid="+ifid+" to errmsg="+txt);
        Statement stmt = null;
        String sql = "";
        try 
        {
            // Connect to the database
            stmt = conn.createStatement();
            sql = "delete from import_file_msg where ifid="+ifid;
            stmt.executeUpdate(sql);
            stmt.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to call PL/SQL procedure\n(" +
            e.getMessage() + ")");
            Errors.log("DbImportFile.setErrMsg failed, rollback db: "+e.getMessage());
        } 
        finally 
        {
            try
            {
                if (stmt != null) 
                {
                    stmt.close();
                }
            } 
            catch (SQLException sqle) 
            {}
        }   
    }
    
   
    
    /**
     * @param conn
     * @param ifid
     * @param txt  */    
    public void addErrMsg(Connection conn, String ifid, String txt)
    {
        Errors.log("Adding message to file ifid="+ifid+" to errmsg="+txt);
        Statement stmt = null;
        String sql = "";
        try 
        {
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            // Connect to the database
            stmt = conn.createStatement();
            
            sql = "select max(msgid) as new_msgid from import_file_msg";
            ResultSet rs = stmt.executeQuery(sql);
            
            int id = 0;
            if (rs.next())
            {
                id = rs.getInt("new_msgid") + 1;
            }
            rs.close();
            sql = "insert into IMPORT_FILE_MSG (msgid,msg,ifid,ts) VALUES ("+id+",'"+txt+"',"+ifid+","+getSQLDate()+")";
            
            stmt.executeUpdate(sql);
            
            stmt.close();
            conn.commit();            
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to call PL/SQL procedure\n(" +
            e.getMessage() + ")");
            Errors.log("DbImportFile.setErrMsg failed, rollback db: "+e.getMessage());
            
            try
            {
                conn.rollback();
            }
            catch (Exception e2)
            {
                Errors.log("Rollback failed: "+e2.getMessage());
            }
        } 
        finally 
        {
            try
            {
                if (stmt != null) 
                {
                    stmt.close();
                }
                conn.setAutoCommit(true);
            } 
            catch (SQLException sqle) 
            {}
        }   
    }
    
    /**
     * This is a test to provide better separation between html and db layers.
     */
    public void getErrInfo(Connection conn, String ifid, ArrayList msg, ArrayList ts)
    {
        Statement stmt = null;
        String sql = "";
        //String msg = "";
        try 
        {
            sql = "SELECT msg,ts FROM import_file_msg WHERE ifid = "+ifid + " order by msgid";
            Errors.logDebug(sql);
            
            // Connect to the database
            stmt = conn.createStatement();
           
            ResultSet rs = stmt.executeQuery(sql);
            
            //msg = new ArrayList();
            //ts  = new ArrayList();
            
            while (rs.next())
            {
                ts.add(rs.getTimestamp("ts"));
                msg.add(rs.getString("msg"));
            }            
            stmt.close();
            conn.commit();
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Query failed\n(" +
            e.getMessage() + ")");
        } 
        finally
        {
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                }
            } 
            catch (SQLException sqle) 
            {}
        }   
    }
    
    
    
    
    /** Get the file name of an import file from the ifid
     * @param conn The database connection to use for query
     * @param ifid The file id to search the name for
     * @return Returns the file name
     */
    public String getFileName(Connection conn, String ifid)
    {
        Statement stmt = null;
        String sql = "";
        String out = "";
        try 
        {
            sql = "SELECT name FROM import_file WHERE ifid = "+ifid;
            Errors.logDebug(sql);
            
            // Connect to the database
            stmt = conn.createStatement();
           
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
            {
                out = rs.getString("name");
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Query failed\n(" +
            e.getMessage() + ")");
        } 
        finally
        {
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                }
            } 
            catch (SQLException sqle) 
            {}
        }   

        return out;
    }
    
    /**
     * Load the header information from the database table. 
     * If this information is not available return a null object!
     */
    public FileHeader getFileHeader(Connection conn, String ifid)
    {
        Statement stmt = null;
        String sql = "";
        FileHeader hdr = null;
        try
        {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery 
                     ("select object_type,format_type,version,delimiter from import_file where ifid="+ifid);

            // get first row
            if (rs.next())
            {
                String object_type  = rs.getString("object_type");
                String format_type  = rs.getString("format_type");
                int version         = rs.getInt("version");
                char delimiter    = rs.getString("delimiter").charAt(0);
                
                if (object_type.equals("") ||
                        format_type.equals(""))
                    hdr = null;
                else
                    hdr = new FileHeader(object_type,format_type,version,delimiter);
            }
            rs.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return hdr;
    }
    
    /**
     * Save a file header to the database.
     */
    public void saveFileHeader(Connection conn, int ifid, FileHeader hdr)
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try
        {
            stmt = conn.createStatement();
            sql = "update import_file set " +
                    "object_type="+sqlString(hdr.objectTypeName())+", " +
                    "format_type="+sqlString(hdr.formatTypeName())+", " +
                    "version="+hdr.version()+", " +
                    "delimiter='"+hdr.delimiter()+"' " +
                    "where ifid="+ifid;
            
            stmt.execute(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new DbException("Internal error. Failed to save file header\n"+e.getMessage());
        }
    }
   
    
    /**
     * Get the header of the file stored in the database. Read the first line 
     * of the file.
     */
    public String getImportFileHeader(Connection conn, String ifid)
    {
        Statement stmt = null;
        String sql = "";
        String tmp = "";
        try
        {
            stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery 
                     ("select import_file from import_file where ifid="+ifid);

            // get first row
            if (rset.next())
            {
               
               InputStream is = rset.getBinaryStream(1);
               //InputStream is = rset.getAsciiStream(1);
               try
               {
                  char c;
                  
                  while (((c = (char)is.read()) !=-1) && (c != '\n'))
                  {
                      tmp += c;
                  }
               }
               catch (Exception e)
               {
                  String err = e.toString();
                  System.out.println(err);
               }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return tmp;
    }
    
    /** Store an input file (BLOB) on the filesystem
     * @return Returns the name and path of the file
     * @param conn The connection to the database
     * @param ifid The input file id to store on disk
     */
    public String storeImportFileBLOB(Connection conn, String s_ifid)
    {
        String filename = "";
        
        // Get the blob from db
        //Blob blob = getImportFileBlob(conn, s_ifid);
        
        // Get the filename
        filename = "/tmp/" + getFileName(conn, s_ifid);
        
        int ifid = Integer.valueOf(s_ifid).intValue();
        
        getImportFile(conn,ifid,filename);
        
        // Create the file
        //createFileFromBLOB(blob, filename);
        
        return filename;
    }
    
}
