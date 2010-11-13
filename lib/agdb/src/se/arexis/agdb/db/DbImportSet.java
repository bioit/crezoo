/**
 *
 * $Log$
 * Revision 1.14  2005/05/17 08:11:06  heto
 * *** empty log message ***
 *
 * Revision 1.13  2005/03/03 15:41:37  heto
 * Converting for using PostgreSQL
 *
 * Revision 1.12  2005/02/23 13:31:26  heto
 * Converted database classes to PostgreSQL
 *
 * Revision 1.11  2005/01/31 12:55:23  heto
 * Trimmed a string
 *
 * Revision 1.10  2004/12/14 08:57:33  heto
 * Renamed variable
 *
 * Revision 1.9  2004/04/23 09:47:41  wali
 * Added setChkSpecies, setChkSuid, setChkLevel, setChk;ode, getSpeciesValue
 *
 * Revision 1.8  2004/04/02 08:10:51  heto
 * Fixed debug messages
 *
 * Revision 1.7  2004/03/26 13:44:38  heto
 * Fixing debug messages.
 *
 * Revision 1.6  2004/03/25 13:32:18  heto
 * Changed locking to sampling unit and not project
 *
 * Revision 1.5  2004/03/22 09:51:04  heto
 * Added sysdate
 *
 * Revision 1.4  2004/03/18 13:22:30  heto
 * Added trim and debug message
 *
 * Revision 1.3  2004/03/18 10:37:35  heto
 * wrong variable type in sql query
 *
 * Revision 1.2  2004/03/17 09:28:20  heto
 * Changed to integer to properly handle sample id
 *
 * Revision 1.1  2004/03/17 07:26:24  heto
 * File renamed from DbImportSession
 *
 * Revision 1.10  2003/11/05 07:41:15  heto
 * Added a table to delete information from then deleting an import session
 *
 * Revision 1.9  2003/05/15 06:32:03  heto
 * Added parameter to isLocked. The session that has the lock must be able to relock.
 *
 * Revision 1.8  2003/05/09 14:50:29  heto
 * Added function getStatus
 *
 * Revision 1.7  2003/05/02 07:58:45  heto
 * Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
 * Modified configuration and source files according to package change.
 *
 * Revision 1.6  2003/04/25 09:10:05  heto
 * Added default status to a new session
 *
 * Revision 1.5  2003/01/15 09:54:53  heto
 * Log statement added in file
 *
 */

package se.arexis.agdb.db;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.FileImport.*;
import se.arexis.agdb.util.Errors;

import java.util.*;
import java.sql.*;

public class DbImportSet extends DbObject 
{
    private int m_fgid;

    /** Create an ImportSet in the database.
     * @param conn
     * @param name
     * @param comm
     * @param pid
     * @param id
     * @return  */
    public int CreateImportSet( Connection conn,
        String name,
        String comm,        
        String pid,
        String id)	
        throws DbException
    {
        Errors.logInfo("DbImportSet.CreateImportSet(...) started");
        Statement stmt = null;
        String sql = "";
        
        int isid = 0;
        
        try 
        {
            // Get a new id
            sql = "select max(isid) + 1 as max from Import_Set";
            
            // Connect to the database
            stmt = conn.createStatement();
            
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
                isid = rs.getInt("max");
            else
                isid = 0;
            
            rs.close();
            stmt.close();
            stmt = conn.createStatement();
            
            sql = "insert into IMPORT_SET (ISID,NAME,STATUS,COMM,PID,ID,TS,C_TS) VALUES ("+String.valueOf(isid)+",'"+name+"','UPLOADED','"+comm+"',"+pid+","+id+","+getSQLDate()+","+getSQLDate()+")";
            //sql = "insert into IMPORT_SESSION (ISID,NAME,COMM,PID,ID) VALUES ("+String.valueOf(isid)+",'"+name+"','"+comm+"',"+String.valueOf(pid)+","+String.valueOf(id)+")";
            
            Errors.logDebug("SQL="+sql);
            int res = stmt.executeUpdate(sql);
            
            Errors.logDebug("RES="+res);
            
            
            // Check for errors!
            if (res <= 0) 
            {
                throw new DbException("Error: res <= 0");
            }
            stmt.close();
	} 
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to create import set\n(" +
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
        Errors.logInfo("DbImportSet.CreateImportSet(...) ended");
        return isid;
    }
    
    /** Update the import set information
     * @param conn A valid database connection to connect to the database.
     * @param name The name of the import set
     * @param comm The comment of the import set
     * @param isid The import set id to update
     * @param id    The id of the person logged in to the system???
     */
    public void UpdateImportSet( Connection conn,
        String name,
        String comm,
        int isid,
        int id)	
    {
        Errors.logInfo("DbImportSet.UpdateImportSet() started");
        Statement stmt = null;
        try 
        {
            // Connect to the database
            stmt = conn.createStatement();
            conn.setAutoCommit(false);
            
            String sql="update import_set set name='"+name+"',comm='"+comm+"',TS="+getSQLDate()+" where isid="+isid;
            
            int res = stmt.executeUpdate(sql);
            
            // res means the number of rows affected by the query.
            if (res <=0)
            {
                Errors.logWarn("DbImportSet.UpdateImportSet() Update failed for isid="+isid);
                conn.rollback();
                Errors.logWarn("DbImportSet.UpdateImportSet() Rollback");
                
                buildErrorString("SQL: Update failed for isid="+isid);
                throw new Exception();                
            }
            else
            {
                conn.commit();
                Errors.logInfo("DbImportSet.UpdateImportSet() Commit");
            }
            
            stmt.close();
        } 
        catch (Exception e) 
        {
            Errors.logError("DbImportSet.UpdateImportSet() update failed for isid="+isid+", exception="+e.getMessage());
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to update database\n(" +
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
        Errors.logInfo("DbImportSet.UpdateImportSet() ended");
    }
  
    /**
     * Delete an import set and all associated files.
     *
     * @param conn
     * @param isid  */    
    public void DeleteImportSet(Connection conn, int isid) 
    {
        Errors.logInfo("DbImportSet.DeleteImportSet(...) started");
        Statement stmt = null;
        String sql = "";
        try 
        {
            int res = 0;
            
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            
            // Connect to the database
            stmt = conn.createStatement();
            
            sql = "delete from IMPORT_FILE_MSG where ifid in (select ifid from IMPORT_FILE where isid = "+isid+")"; 
            res = stmt.executeUpdate(sql);
            
            sql = "delete from IMPORT_FILE where isid = "+isid;
            res = stmt.executeUpdate(sql);
            
            sql = "delete from IMPORT_SET where isid = "+isid;
            res = stmt.executeUpdate(sql);
            
            conn.commit();
            stmt.close();
            Errors.logInfo("DbImportSet.DeleteImportSet() Commit");
            
        } 
        catch (Exception e) 
        {
            try
            {
                Errors.logError("DbImportSet.DeleteImportSet(...) Rollback");
                conn.rollback();
            }
            catch (Exception ignore)
            {}
            
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Execute query\n(" +
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
        Errors.logInfo("DbImportSet.DeleteImportSet(...) ended");
    }
    
    public void setStatus(Connection conn, String isid,String status)
    {
        Errors.logInfo("DbImportSet.setStatus(...) started");
        Errors.logDebug("Setting status to import set isid="+isid+" to status=\""+status+"\"");
        
        Statement stmt = null;
        
        if (status != "UPLOADED" && status != "CHECKING" && status != "CHECKED"
            && status != "IMPORTING" && status != "IMPORTED" && status != "ERROR")
        {
            Errors.logError("DbImportSet.setStatus(...): Invalid status message: "+status);
            status = "ERROR";
        }
        
        String sql = "";
        try 
        {
            sql = "update IMPORT_SET set status='"+status+"',TS="+getSQLDate()+" where isid = "+isid;
            
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            
            // Connect to the database
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            conn.commit();
            Errors.logInfo("DbImportSet.setStatus(...) Commit");     
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to set status\n(" +
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
        Errors.logInfo("DbImportSet.setStatus(...) ended");
    }
    
    public String  getStatus(Connection conn, String isid)
    {
        //Errors.logInfo("DbImportSet.getStatus(...) started");
        String res = "";
        String sql;
        Statement stmt = null;
        try 
        {
            sql = "SELECT status FROM import_set WHERE isid="+isid;
            // Connect to the database
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            int num = 99;
            if (rs.next())
            {
                res = rs.getString("status").trim();
            }
            stmt.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to set status\n(" +
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
        //Errors.logInfo("DbImportSet.getStatus(...) ended");
        return res;
    }
    
    /**
     * Check for other imports in this sampling unit. 
     * If another import is in progress in this SU, then we cant
     * allow another import set.
     *  String pid, String isid
     */
    public boolean isLocked(Connection conn, int isid)
    {
        Errors.logInfo("DbImportSet.isLocked(...) started, isid="+isid);
        Statement stmt = null;
        String sql = "";
        
        boolean out = true;
        try 
        {
            //and not isid="+isid+" and pid="+pid
            sql = "SELECT COUNT(isid) as numOfImp FROM import_set WHERE (status='IMPORTING' or status='CHECKING' or status='CHECKED') and not isid="+isid+" and chk_suid in (select chk_suid from import_set where isid="+isid+")";
            
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            
            // Connect to the database
            stmt = conn.createStatement();
           
            
            ResultSet rs = stmt.executeQuery(sql);
            
            int num = 99;
            if (rs.next())
            {
                num = rs.getInt("numOfImp");
            }
        
            if (num == 0)
                out = false;
            else
                out = true;
                
            
            stmt.close();
            conn.commit();
            Errors.logInfo("DbImportSet.isLocked(...) Commit");
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to set status\n(" +
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
        Errors.logInfo("DbImportSet.isLocked(...) ended, isid="+isid);
        return out;
    }
    
    // String suid, String updateMethod, int level
    public void setChkValues(Connection conn, String isid, ImportProperties ip)
    {
        Errors.logInfo("DbImportSet.setChkValues(...) started");
        Errors.logDebug("isid="+isid);
        Statement stmt = null;
        String sql = "";
        
        try
        {
            sql = "update IMPORT_SET set chk_suid="+ip.suid+",chk_mode='"+ip.updateMethod+"',chk_level="+ip.level+", CHK_TS="+getSQLDate()+" where isid = "+isid;
            Errors.logDebug(sql);
            
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            
            // Connect to the database
            stmt = conn.createStatement();
            
            stmt.executeUpdate(sql);
            
            stmt.close();
            conn.commit();
            Errors.logInfo("DbImportSet.setChkValues(...) Commit");
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to get check values\n(" +
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
        Errors.logInfo("DbImportSet.setChkValues(...) ended");
    }
    
   public void setChkSpeciesId(Connection conn, String isid, int speciesId)
    {
        Errors.logInfo("DbImportSet.setChkSpeciesId(...) started");
        Errors.logDebug("isid="+isid);
        Statement stmt = null;
        String sql = "";
        
        try
        {
            sql = "update IMPORT_SET set chk_species="+speciesId+", CHK_TS="+getSQLDate()+" where isid = "+isid;
            Errors.logDebug(sql);
            
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            
            // Connect to the database
            stmt = conn.createStatement();
            
            stmt.executeUpdate(sql);
            
            stmt.close();
            conn.commit();
            Errors.logInfo("DbImportSet.setChkSpecies(...) Commit");
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to set check values\n(" +
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
        Errors.logInfo("DbImportSet.setChkSpecies(...) ended");
    }
    
  public void setChkSuid(Connection conn, String isid, int suid)
    {
        Errors.logInfo("DbImportSet.setChkSuid(...) started");
        Errors.logDebug("isid="+isid);
        Statement stmt = null;
        String sql = "";
        
        try
        {
            sql = "update IMPORT_SET set chk_suid="+suid+", CHK_TS="+getSQLDate()+" where isid = "+isid;
            Errors.logDebug(sql);
            
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            
            // Connect to the database
            stmt = conn.createStatement();
            
            stmt.executeUpdate(sql);
            
            stmt.close();
            conn.commit();
            Errors.logInfo("DbImportSet.setChkSuid(...) Commit");
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to set check values\n(" +
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
        Errors.logInfo("DbImportSet.setChkSuid(...) ended");
    }
    
  public void setChkLevel(Connection conn, String isid, int level)
    {
        Errors.logInfo("DbImportSet.setChkLevel(...) started");
        Errors.logDebug("isid="+isid);
        Statement stmt = null;
        String sql = "";
        
        try
        {
            sql = "update IMPORT_SET set chk_level="+level+" where isid = "+isid;
            Errors.logDebug(sql);
            
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            
            // Connect to the database
            stmt = conn.createStatement();
            
            stmt.executeUpdate(sql);
            
            stmt.close();
            conn.commit();
            Errors.logInfo("DbImportSet.setChkLevel(...) Commit");
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to set check values\n(" +
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
        Errors.logInfo("DbImportSet.setChkLevel(...) ended");
    }
    
   public void setChkUpdateMethod(Connection conn, String isid, String updateMethod)
    {
        Errors.logInfo("DbImportSet.setChkupdateMethod(...) started");
        Errors.logDebug("isid="+isid);
        Statement stmt = null;
        String sql = "";
        
        try
        {
            sql = "update IMPORT_SET set chk_mode='"+updateMethod+"' where isid = "+isid;
            Errors.logDebug(sql);
            
            // Shut off autoCommit.
            conn.setAutoCommit(false);
            
            // Connect to the database
            stmt = conn.createStatement();
            
            stmt.executeUpdate(sql);
            
            stmt.close();
            conn.commit();
            Errors.logInfo("DbImportSet.setChkUpdateMethod(...) Commit");
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to set check values\n(" +
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
        Errors.logInfo("DbImportSet.setChkupdateMethod(...) ended");
    }
   
  
    public ImportProperties getChkValues(Connection conn, String isid)
    {
        Errors.logInfo("DbImportSet.getChkValues(...) started");
        Statement stmt = null;
        String sql = "";
        ImportProperties ip = null;
        try
        {
            sql = "select chk_suid,chk_mode,chk_level from IMPORT_SET where isid = "+isid;
            
            // Connect to the database
            stmt = conn.createStatement();
           
            ResultSet rs = stmt.executeQuery(sql);
            
            ip = new ImportProperties();
            
            if (rs.next())
            {
                ip.suid = rs.getInt("chk_suid");
                ip.updateMethod = rs.getString("chk_mode").trim();
                ip.level= rs.getInt("chk_level");
            }
            stmt.close();            
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to set check values\n(" +
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
        Errors.logInfo("DbImportSet.getChkValues(...) ended");
        return ip;
    }
     
    public int getSpeciesValue(Connection conn, String isid)
    {
        Errors.logInfo("DbImportSet.getSpeciesValues(...) started");
        Statement stmt = null;
        String sql = "";
        int speciesId = 0;
        try
        {
            sql = "select chk_species from IMPORT_SET where isid = "+isid;
            
            // Connect to the database
            stmt = conn.createStatement();
           
            ResultSet rs = stmt.executeQuery(sql);
          
           
            if (rs.next())
            {
                speciesId = rs.getInt("chk_species");
            }
            stmt.close();            
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to set check speceis values\n(" +
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
        Errors.logInfo("DbImportSet.getSpeciesValues(...) ended");
        return speciesId;
    }
    
    public ArrayList<ImportFileStruct> getImportFiles(Connection connection, int isid)
    {
        ArrayList<ImportFileStruct> out = null;
        try
        {
            out = new ArrayList<ImportFileStruct>();
        
            
            Statement stmt = null;
            ResultSet rset = null;
            stmt = connection.createStatement();


            stmt = connection.createStatement();
            String SQLstmt = "SELECT NAME, OBJECT_TYPE, format_type, version, delimiter, IFID FROM IMPORT_FILE WHERE ISID = "+isid;
            rset = stmt.executeQuery(SQLstmt);



            while(rset.next())
            {
                //out.add(new ImportFileStruct(rset.getString("NAME"),rset.getString("OBJECT_TYPE"),rset.getString("IFID")));
                FileHeader hdr = new FileHeader(rset.getString("Object_type"), rset.getString("format_type"), rset.getInt("version"), rset.getString("delimiter").charAt(0));
                
                ImportFileStruct files = new ImportFileStruct(rset.getString("NAME"),rset.getString("IFID"),hdr);
                
                out.add(files);

                //stores the object names and files in an array.
                //structureFiles(rset.getString("OBJECT_NAME").trim(), rset.getString("NAME").trim(), rset.getString("IFID").trim());
            }    
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
        }
        return out;
    }
    
}
