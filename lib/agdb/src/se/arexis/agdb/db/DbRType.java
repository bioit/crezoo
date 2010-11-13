/*
 * DbRType.java
 *
 * Created on den 4 december 2003, 09:20
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
public class DbRType extends DbObject {
    
    public DbRType() {
    }
    
    public void CreateRType(Connection conn, String RT_name, String Comment, int id)
    throws DbException {
        
        int RTid = 0;
        ResultSet rset = null;
        Statement stmt = null;
        
        try {
            if(Comment == null) Comment = new String("");
            if(RT_name == null) RT_name = new String("");
            int l = 0;
            l=Comment.length();
            if(l>256) throw new SQLException("Comment");
            l=RT_name.length();
            if(l>38) throw new SQLException("RT_name");
            
            String comm = replaceSymbol(Comment);
            stmt = conn.createStatement();
            
            RTid = getNextID(conn,"RType_Seq");
            /*
            rset = stmt.executeQuery("SELECT RType_Seq.Nextval FROM Dual");
            if (rset.next()){
                //Assertion.assertMsg(rset.next(), "New role was not created.");
                RTid = rset.getInt(1);
            }
             */
            System.out.println("RTid in Dbresult: " + RTid);
            
            String insertStmt = "insert into RTYPE " +
                    "(RTID, NAME, COMM, ID, TS) VALUES (?, ?, ?, ?, "+getSQLDate()+")";
            
            PreparedStatement ps = conn.prepareStatement(insertStmt);
            
            int ix = 1;
            ps.setInt(ix++, RTid);
            ps.setString(ix++, RT_name);
            ps.setString(ix++, comm);
            ps.setInt(ix++, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DbException("Internal error. Failed to insert values into Rtype \n(" +
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
    
    public void UpdateRType(Connection conn, int RTid, String RT_name, 
            String Comment, int id) 
            throws DbException
    {
        System.err.println("DB_UPDATE_RType");
        ResultSet rset = null;
        Statement stmt = null;
        int count = 0;
        try {
            String comm = replaceSymbol(Comment);
            stmt = conn.createStatement();
            
            String updateSql = "update RTYPE set ";
            if(RT_name!=null){
                updateSql += "NAME='" + RT_name + "'";
                count++;
            }
            if(comm!=null){
                if(count>0)
                    updateSql += ", ";
                updateSql += "COMM='" + comm + "'";
            }
            
            updateSql += " where RTID=" + RTid;
            
            
            int res = stmt.executeUpdate(updateSql);
            
            stmt.close();
            stmt = null;
        }
        
        catch (SQLException e) 
        {
            e.printStackTrace();
            throw new DbException("Internal error. Failed to update RType \n(" +
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
    
    public void DeleteRType(Connection conn, int rtid) 
    throws DbException 
    {
        Statement stmt_res = null;
        ResultSet rset_res = null;
        Statement stmt = null;
        
        try{
            
            stmt = conn.createStatement();
            String resSQL = "select RESID from results where r_type=" + rtid;
            rset_res = stmt.executeQuery(resSQL);
            if(rset_res.next())
                throw new Exception();
            
            
            String delSQL = "delete from rtype where rtid = " + rtid;
            stmt.execute(delSQL);
            
        } 
        catch (Exception e) {
            e.printStackTrace(System.err);
            throw new DbException("Internal error. Failed to delete result type(" +
                    e.getMessage() + ")");
        } 
        finally 
        {
            try {
                if (stmt != null) stmt.close();
                
                
                if (rset_res != null) rset_res.close();
            } catch (SQLException ignored) {}
        }
    }
    
}


