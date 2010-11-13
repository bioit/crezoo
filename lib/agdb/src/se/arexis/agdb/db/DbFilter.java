// Copyright (C) 2000 by Prevas AB. All rights reserved.
package se.arexis.agdb.db;
import se.arexis.agdb.util.Errors;

/**
 * This class provides methods for objects in the database
 * that has something to do with filters.
 *
 * @author <b>Tomas Björklund, Prevas AB</b>, Copyright &#169; 2000
 * @version 1.0, 2000-11-28
 */
public class DbFilter extends DbObject {
    /**
     * The constructor for this class. No arguments.
     */
    public DbFilter() {
    }
    
    /**
     * Creates a new filter.
     * @param conn the connection object
     * @param pid the project id to create the filter in
     * @param name the name of the filter
     * @param expression the expression of the filter.
     * @param comm the comment of the filter
     * @param sid the species id
     * @param id the id of the person creating this filter.
     * @throws se.arexis.agdb.db.DbException throws DbException if an error occurs.
     */
    public void CreateFilter( Connection conn,
            String pid,
            String name,
            String expression,
            String comm,
            int sid,
            int id)
            throws DbException {
        Statement stmt = null;
        int fid = 0;
        String sql = "";
        try {
            stmt = conn.createStatement();
            
            fid = getNextID(conn, "Filters_Seq");
            sql = "insert into Filters (fid,name,expression,comm,pid,sid,id,ts) " +
                    "values ("+fid+", "+sqlString(name)+", "+sqlString(expression)+", "+sqlString(comm)+", "+pid+", "+sid+", "+id+", "+getSQLDate()+")";
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to create the filter\n(" +
                    e.getMessage() + ")");
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException sqle) {
            }
        }
    }
    
    /**
     * Updates an existing filter.
     * @param conn the connection object
     * @param fid the filter id to update
     * @param name the new name
     * @param expression the new expression
     * @param comm The new comment for the filter
     * @param sid the species id this filter operates on
     * @param id the id of the person changing the values.
     * @throws se.arexis.agdb.db.DbException Throws this with messages to the UI
     */
    public void UpdateFilter(Connection conn,
            String fid,
            String name,
            String expression,
            String comm,
            int sid,
            int id)
            throws DbException {
        Statement stmt = null;
        String sql_log = "";
        String sql = "";
        try {
            sql_log = "insert into filters_log select fid, name, expression, comm, sid, id, ts " +
                    "from Filters " +
                    "where fid = "+fid;
            
            stmt = conn.createStatement();
            stmt.execute(sql_log);
            
            sql = "update Filters set name = "+sqlString(name)+", " +
                    "expression = "+sqlString(expression)+", " +
                    "comm = "+sqlString(comm)+", sid = "+sid+", id = "+id+", " +
                    "ts = "+getSQLDate()+" where fid = "+fid;
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            Errors.logError("SQL_LOG="+sql_log);
            Errors.logError("SQL="+sql);
            
            throw new DbException("Internal error. Failed to update filter\n(" +
                    e.getMessage() + ")");
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException sqle) {}
        }
    }
    
    /**
     * Deletes a filter.
     * @param conn the connection
     * @param fid the filter id
     * @throws se.arexis.agdb.db.DbException Throws this with a message to the UI
     */
    public void DeleteFilter(Connection conn, String fid)
    throws DbException {
        Statement stmt = null;
        String sql = "";
        try {
            sql = "delete from filters_log where fid = "+fid+"; " +
                    "delete from Filters where fid = "+fid;
            stmt = conn.createStatement();
            stmt.execute(sql);
            
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to delete filter\n(" +
                    e.getMessage() + ")");
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException sqle) {
            }
        }
    }
}
