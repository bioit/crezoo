/*
 * DbResult.java
 *
 * Created on den 4 november 2003, 11:30
 */

package se.arexis.agdb.db;
import java.sql.*;
import java.io.*;

import se.arexis.agdb.util.Errors;
/**
 *
 * @author  wali
 */
public class DbResult extends DbObject{
    
    /** Creates a new instance of DbResult
     * To insert values to LOB, they first needs to be initialized as LOB_empty(),
     * thereafter the locator should be retrieved (by the select statement) and
     * then the result can be stored.
     */
    public DbResult() 
    {
    }
    
    /**
     * Save a result file to the database results table
     */
    public void saveResultFile(Connection conn, int rid, String filename)
    throws DbException {
        try 
        {    
            File file = new File(filename);
            FileInputStream fis = new FileInputStream(file);
            
            //PreparedStatement ps = conn.prepareStatement("INSERT INTO testbinary VALUES (?, ?)");
            PreparedStatement ps = conn.prepareStatement("update results set r_file = ? where resid = ?");
            
            ps.setInt(2, rid);
            int length = Long.valueOf(file.length()).intValue();
            
            ps.setBinaryStream(1, fis, length);
            
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
    
    public InputStream getResultFileStream(Connection conn, int resid)
    {
        InputStream out = null;
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery 
                     ("select r_file from results where resid="+resid);

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
    
    /*
    public File getResultFileObject(Connection conn, int resid, String filename)
    {
        File file = new File(filename);
        FileOutputStream fos = FileOutputStream(file);
        
        
        
        InputStream is = null;
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery 
                     ("select r_file from results where resid="+resid);

            // get first row
            if (rset.next())
            {
               is = rset.getBinaryStream(1);
            }    
            
            byte c; 
            while ((c = is.read())!=-1)
            {
                fos.write(c);
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
     */
    
    public void setResultFileStream(Connection conn, int rid, InputStream is, int length)
        throws DbException
    {
        try 
        {    
            //File file = new File(filename);
            //FileInputStream fis = new FileInputStream(file);
            
            //PreparedStatement ps = conn.prepareStatement("INSERT INTO testbinary VALUES (?, ?)");
            PreparedStatement ps = conn.prepareStatement("update results set r_file = ? where resid = ?");
            
            Errors.logDebug("length="+length);
            
            ps.setInt(2, rid);
            //int length = Long.valueOf(file.length()).intValue();
            
            //ps.setBinaryStream(1, fis, length);
            ps.setBinaryStream(1, is, length);
            
            ps.executeUpdate();
            ps.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            
            throw new DbException("Internal error. Failed to set database file\n"+e.getMessage());
        }
    }
           
    
    /**
     * Save a result file to the database results table
     */
    public void saveBatchFile(Connection conn, int rid, String filename)
        throws DbException 
    {
        try 
        {    
            File file = new File(filename);
            FileInputStream fis = new FileInputStream(file);
            
            //PreparedStatement ps = conn.prepareStatement("INSERT INTO testbinary VALUES (?, ?)");
            PreparedStatement ps = conn.prepareStatement("update results set b_file = ? where resid = ?");
            
            ps.setInt(2, rid);
            int length = Long.valueOf(file.length()).intValue();
            
            ps.setBinaryStream(1, fis, length);           
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
    
    public void printResultFile(Connection conn, int resid, OutputStream out)
    {
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("select r_file from results where resid="+resid);
            InputStream is = null;
            // get first row
            if (rset.next())
            {
                is = rset.getBinaryStream(1);
                byte[] buf = new byte[256 * 1024]; // 256 KB
                int bytesRead;
                
                while ((bytesRead = is.read(buf)) != -1)
                {
                    out.write(buf, 0, bytesRead);
                }
            }    
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    /**
     * This returns the file as a byte array
     * This must only be used with text files!
     */
    public byte[] getResultFile(Connection conn, int resid)
    {
        FileOutputStream file = null;
        
        byte[] out = null;
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery 
                     ("select r_file from results where resid="+resid);

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
    public byte[] getBatchFile(Connection conn, int resid)
    {
        FileOutputStream file = null;
        
        byte[] out = null;
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery 
                     ("select b_file from results where resid="+resid);

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
    
    public void CreateResult(Connection conn, int fgid, String RName, int RType,
            String BName, int Ctg, String Comm, int id,
            String resPath, String batPath, int pid)
            throws FileNotFoundException, DbException
    {
        //user id
        //IOException
        int rid = 0;
        System.err.println("DB_CREATE_RESULT");
        //BLOB blob = null;
        //CLOB clob = null;
        //FileInputStream resultImportStream = null; //The result file
        //FileInputStream batchImportStream = null;    //The  batch file
        Statement stmt = null;
        ResultSet rset = null;
        Statement LOBstmt = null;
        ResultSet LOBrset = null;
        
        try {
            if (Comm == null) Comm = new String("");
            if (RName == null) RName = new String("");
            if (BName == null) BName = new String("");
            //Check if the parameter values are within the given limits:
            int l = Comm.length();
            System.err.println("COMMENT LENGTH DBRESULT CreateResult: " + l);
            if(l>2000)
                throw new SQLException("comment");
            
            l = RName.length();
            if(l>80) throw new SQLException("RName");
            
            l = BName.length();
            if(l>80) throw new SQLException("BName");
            
            
            
            
            
            conn.setAutoCommit(false);
            
            if (Comm == null) Comm = new String("");
            Comm = replaceSymbol(Comm);
            
            stmt = conn.createStatement();
            
            rid = getNextID(conn, "Results_Seq");
            
            String insertStmt = "insert into Results " +
                    "(RESID, FGID, R_NAME, R_TYPE, B_NAME, CTG, COMM, PID, C_TS, ID, TS ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, "+getSQLDate()+", ?, "+getSQLDate()+")";
            
            PreparedStatement ps = conn.prepareStatement(insertStmt);
            
            int ix = 1;
            ps.setInt(ix++, rid);
            if(fgid != 0)
                ps.setInt(ix++, fgid);
            else
                ps.setString(ix++, null);
            ps.setString(ix++, RName);
            ps.setInt(ix++, RType);
            ps.setString(ix++, BName);
            ps.setInt(ix++, Ctg);
            ps.setString(ix++, Comm);
            ps.setInt(ix++, pid);
            ps.setInt(ix++, id);
            ps.execute();
            
            ps.close();
            stmt.close();
            stmt = null;
            
            
            
            // Save the result file
            saveResultFile(conn, rid, resPath);
            
            // Save the batch file
            saveBatchFile(conn, rid, batPath);
            
            
            /*
            LOBstmt = conn.createStatement();
            String cmd = "SELECT * from RESULTS where RESID=" + rid;
            LOBrset = LOBstmt.executeQuery(cmd);
            if(LOBrset.next()){
                blob = ((OracleResultSet)LOBrset).getBLOB(4);
                OutputStream Boutstream = blob.getBinaryOutputStream(); //Write to the BLOB from a stream
                
                int Bchunk = blob.getChunkSize(); //Get database LOB storage chunk size in database.
                byte[] Bbuffer = new byte[Bchunk];
                
                int length = -1;
                while ((length = BLOBimportStream.read(Bbuffer)) != -1)
                    Boutstream.write(Bbuffer,0,length);
                Boutstream.close();
                
                if(BName != null) {
                    
                    clob = ((OracleResultSet)LOBrset).getCLOB(7);
                    OutputStream Coutstream = clob.getAsciiOutputStream();
                    
                    int Cchunk = clob.getChunkSize();
                    byte[] Cbuffer = new byte[Cchunk];
                    
                    length = -1;
                    while ((length = CimportStream.read(Cbuffer)) != -1)
                        Coutstream.write(Cbuffer,0,length);
                    Coutstream.close();
                }
                
                LOBstmt.close();
                LOBstmt = null;
            }
             */
            
        }
        catch (DbException e)
        {
            throw e;
        }
        catch (SQLException e) 
        {
            e.printStackTrace();
            
            throw new DbException("Internal error. Failed to update Result\n(" +
                    e.getMessage() + ")");
        }
        finally 
        {
            try {
                if (stmt != null) stmt.close();
                if (rset != null) rset.close();
                if (LOBstmt != null) LOBstmt.close();
                if (LOBrset != null) LOBrset.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace(System.err);
            }
        }
        
    }
    
    
    public void UpdateResults(Connection conn, int resid, int ctg, int rtype,
            String Comment, int id, int pid) 
            throws DbException
    {
        
        System.err.println("DB_UPDATE_Result");
        ResultSet rset_log = null;
        Statement stmt_log = null;
        ResultSet rset_new = null;
        Statement stmt_new = null;
        
        try {
            if (Comment == null) Comment = new String("");
            
            //Check if the parameter values are within the given limits:
            int l = Comment.length();
            System.err.println("COMMENT LENGTH DBRESULT: " + l);
            if(l>2000)
                throw new SQLException("comment");
            
            String comm = replaceSymbol(Comment);
            // Get the old values, if they have changed, insert them into the log table
            conn.setAutoCommit(false);
            stmt_log = conn.createStatement();
            String logSQL = "INSERT into RESULTS_LOG (RESID, fgid, r_name, r_type, b_name, ctg, comm, id, ts, pid) " +
                    "select RESID, fgid, r_name, r_type, b_name, ctg, comm, id, ts, pid from results " +
                    "where RESID =" + resid;
            stmt_log.execute(logSQL);
            
            //save the new variable values. We can save all of them, even if
            //some values haven't changed, since the values are compared when
            //the history is displayed.
            stmt_new = conn.createStatement();
            String updateSQL = "update Results set " +
                    "CTG='" + ctg + "'" + "," + "R_TYPE='" + rtype + "'" + "," +
                    "COMM='" + comm + "'" + "," + "ID='" + id + "'" + "," + "TS=" +getSQLDate()+ " where RESID=" + resid;
            
            int res = stmt_new.executeUpdate(updateSQL);
            System.err.println("The result should have been updated... res: " + res +
                    "the result should have been logged");
            
            stmt_new.close();
            stmt_log.close();
            stmt_new = null;
            stmt_new = null;
            
        }
        catch (SQLException e) 
        {
            e.printStackTrace();
            throw new DbException("Internal error. Failed to update Result\n(" +
                    e.getMessage() + ")");
        } finally {
            try {
                if (stmt_new != null) stmt_new.close();
                if (stmt_log != null) stmt_log.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace(System.err);
            }
        }
    }
    
    public void DeleteResults(Connection conn, int resid, String UserId) 
        throws DbException
    {
        Statement stmt_log = null;
        Statement stmt_del = null;
       
        try {
            conn.setAutoCommit(false);
            stmt_log = conn.createStatement();
            String logSQL = "INSERT into RESULTS_LOG (RESID, r_name, r_type, ctg, comm, id, ts, pid) " +
                    "select RESID, r_name, r_type, ctg, comm, id, ts, pid from results " +
                    "where RESID =" + resid;
            stmt_log.execute(logSQL);
            
            stmt_del = conn.createStatement();
            String delSQL = "delete from results where RESID = " + resid;
            stmt_del.execute(delSQL);
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to delete result(" +
                    e.getMessage() + ")");
        } finally {
            try {
                if (stmt_log != null) stmt_log.close();
                if (stmt_del != null) stmt_del.close();
            } catch (SQLException ignored) {}
        }
    }
}
