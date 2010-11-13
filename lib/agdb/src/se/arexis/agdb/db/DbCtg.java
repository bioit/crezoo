/*
 * DbCtg.java
 *
 * Created on den 26 november 2003, 20:12
 *
 * $Log:
 */
package se.arexis.agdb.db;

import java.util.*;
import java.sql.*;
import java.sql.ResultSet.*;
import java.sql.Types.*;
import java.io.InputStream;
import se.arexis.agdb.db.*;

/**
 *
 * @author  wali
 * @version
 */
public class DbCtg extends DbObject {
    
    /** Creates a new instance of DbResult */
    public DbCtg() {
    }
    
    public void CreateCtg(Connection conn, String c_name, String Comment, int id) 
        throws DbException 
    {
        int ctgid = 0;
        ResultSet rset = null;
        Statement stmt = null;
        
        try {
            if(Comment == null) Comment = new String("");
            if(c_name == null) c_name = new String("");
            int l = 0;
            l=Comment.length();
            if(l>256) throw new SQLException("Comment");
            l=c_name.length();
            if(l>38) throw new SQLException("c_name");
            
            String comm = replaceSymbol(Comment);
            stmt = conn.createStatement();
            
            ctgid = getNextID(conn,"Category_Seq");
        /*
        rset = stmt.executeQuery("SELECT Category_Seq.Nextval FROM Dual");
        if (rset.next()){
          ctgid = rset.getInt(1);
        }
         */
            
            String insertStmt = "insert into CATEGORY " +
                    "(CTGID, NAME, COMM, ID, TS) VALUES (?, ?, ?, ?, "+getSQLDate()+")";
            
            PreparedStatement ps = conn.prepareStatement(insertStmt);
            
            int ix = 1;
            ps.setInt(ix++, ctgid);
            ps.setString(ix++, c_name);
            ps.setString(ix++, comm);
            ps.setInt(ix++, id);
            ps.executeUpdate();   
        }
        catch (SQLException e) 
        {
            e.printStackTrace();
            
            throw new DbException("Internal error. Failed to insert values into Category \n(" +
                    e.getMessage() + ")");
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (rset != null) rset.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace(System.err);
            }
        }
    }
    
    public void UpdateCtg(Connection conn, int ctgid, String c_name, String Comment, int id) 
    throws DbException
    {
        
        ResultSet rset = null;
        Statement stmt = null;
        int count = 0;
        try {
            if(Comment == null) Comment = new String("");
            if(c_name == null) c_name = new String("");
            int l = 0;
            l=Comment.length();
            if(l>256) throw new SQLException("Comment");
            l=c_name.length();
            if(l>38) throw new SQLException("c_name");
            
            
            String comm = replaceSymbol(Comment);
            stmt = conn.createStatement();
            
            String updateSql = "update CATEGORY set ";
            if(c_name!=null){
                updateSql += "NAME='" + c_name + "'";
                count++;
            }
            if(comm!=null){
                if(count>0)
                    updateSql += ", ";
                updateSql += "COMM='" + comm + "'";
            }
            
            updateSql += " where CTGID=" + ctgid;
            int res = stmt.executeUpdate(updateSql);
            
            stmt.close();
            stmt = null;
            
        }
        catch (SQLException e) 
        {
            e.printStackTrace();
            throw new DbException("Internal error. Failed to update Category \n(" +
                    e.getMessage() + ")");
            
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (rset != null) rset.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace(System.err);
            }
        }
    }
    
    public void DeleteCtg(Connection conn, int ctgid) throws Exception {
        Statement stmt_res = null;
        ResultSet rset_res = null;
        Statement stmt_del = null;
        ResultSet rset_del = null;
        try{
            stmt_res = conn.createStatement();
            String resSQL = "select RESID from results where ctg=" + ctgid;
            rset_res = stmt_res.executeQuery(resSQL);
            if(rset_res.next())
                throw new Exception();
            
            
            stmt_del = conn.createStatement();
            String delSQL = "delete from category where ctgid = " + ctgid;
            rset_del = stmt_del.executeQuery(delSQL);
            
        }   catch (SQLException e) {
            e.printStackTrace();
        }
        
        catch (Exception e) {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to delete categoty(" +
                    e.getMessage() + ")");
        } finally {
            try {
                if (stmt_del != null) stmt_del.close();
                if (rset_del != null) rset_del.close();
                if (stmt_res != null) stmt_res.close();
                if (rset_res != null) rset_res.close();
            } catch (SQLException ignored) {}
        }
    }
}
