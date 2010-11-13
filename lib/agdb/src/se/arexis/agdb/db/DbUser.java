
package se.arexis.agdb.db;

import se.arexis.agdb.db.*;
import java.sql.*;

/**
 * This class provides methods for objects in the database
 * that has something to do with users of the application.
 *
 */
 public class DbUser extends DbObject {
	private int m_pid = -1;

  /**
   * Default constructor.
   * This constructor doesn't do anything.
   */
	public DbUser() {
	}

  /**
   * This method updates an existing apllication user. 
   */
	public void UpdateUser(Connection conn, int id, String new_name,
                         String new_uid, String new_pwd, String new_status) 
                         throws DbException
        {
            Statement stmt = null;
            String sql = "";
            try 
            {
                sql = "Update Users set usr = "+sqlString(new_uid)+", pwd = "+sqlString(new_pwd)+",  name = "+sqlString(new_name)+", status = "+sqlString(new_status)+" " +
                        "where id = "+id;
                stmt = conn.createStatement();
                stmt.execute(sql);
            } 
            catch (SQLException sqle) 
            {
                sqle.printStackTrace(System.err);
                
                throw new DbException("Internal error. Failed to update user\n(" +
                sqle.getMessage() + ")");
            } 
            finally 
            {
                try 
                {
                    if (stmt != null) stmt.close();
                } 
                catch (SQLException ignored) 
                {}
            }
	}
  /**
   * Deletes the user with the user id = id. 
   * This method actually DISABLES the user, and does not removes the user!!!
   * 
   * This is NOT a good idea
   * There are many views that depend on that the
   * user exist. Perhaps it's better to 
   * disable a user.
   *
   */
    public void DeleteUser(Connection conn, int id) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            sql = "update users set status='D' where id = "+id;
            stmt = conn.createStatement();
            stmt.execute(sql);
            
            
        } 
        catch (SQLException sqle) 
        {
                sqle.printStackTrace(System.err);
                
                throw new DbException("Internal error. Failed to delete user\n(" +
              sqle.getMessage() + ")");
        } 
        finally 
        {
            try 
            {
                if (stmt != null) stmt.close();
            } 
            catch (SQLException ignored) 
            {}
        }
    }
    
  /**
   * Creates a new user of the application. 
   */
    public void CreateUser(Connection conn, String user_name,
                         String user_uid, String user_pwd, String status) 
                         throws DbException
    {
        Statement stmt = null;
        String sql = "";
        int id = 0;
        try 
        {
            id = getNextID(conn, "Users_seq");
            
            sql = "insert into Users values("+id+", "+sqlString(user_uid)+", "+sqlString(user_pwd)+", "+sqlString(user_name)+", "+sqlString(status)+");";
            stmt = conn.createStatement();
            stmt.execute(sql);
        } 
        catch (SQLException sqle) 
        {        
            sqle.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to call PL/SQL procedure\n(" +
                      sqle.getMessage() + ")");
        } 
        finally 
        {
            try 
            {
                if (stmt != null) stmt.close();
            } 
            catch (SQLException ignored) 
            {}
        }
    } // End of public void CreateUser(...
}
