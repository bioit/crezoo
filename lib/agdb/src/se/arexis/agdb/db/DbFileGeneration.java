/**
 * $Log$
 * Revision 1.7  2005/03/24 15:12:44  heto
 * Working with removing oracle dep.
 *
 * Revision 1.6  2005/03/03 15:41:37  heto
 * Converting for using PostgreSQL
 *
 *
 */
package se.arexis.agdb.db;

import se.arexis.agdb.util.GqlTranslator;
import se.arexis.agdb.db.*;
import se.arexis.agdb.util.FileImport.*;

import java.util.*;
import java.sql.*;


public class DbFileGeneration extends DbObject 
{
    
    private String filter;
    
    
    /**
     * Check for valid values
     */
    private void checkFileGenerationValues(String name, String mode, 
            String type, String comm) throws DbException
    {
        if (name==null || name.length() > 20)
            throw new DbException("Name exceeds 20 characters");
        
        if (mode!=null && !mode.equals("S") && !mode.equals("M"))
            throw new DbException("Mode must be one of S, M");
        
        if (type!= null && type.length() > 20)
            throw new DbException("Type exceeds 20 charcters");
        
        if (comm!=null && comm.length() > 256)
            throw new DbException("Comment exceeds 256 charcters");
    }
    
    public void Create_FG_FLT_Link(Connection conn, int fgid, int suid, 
            int fid, Integer gsid) throws DbException
    {
        String sql = "";
        Statement stmt = null;
        try
        {
            stmt = conn.createStatement();
            
            sql = "insert into R_FG_FLT Values("+fgid+", "+suid+", "+fid+", "+sqlNumber(String.valueOf(gsid))+")";
            stmt.execute(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            throw new DbException("Internal error. Failed to create link\n" +
                    e.getMessage());
        }    
        finally
        {
            try
            {
                if (stmt!=null)
                    stmt.close();
            }
            catch (Exception ignore)
            {}
        }
    }
    
    public int CreateSingleFileGeneration( Connection conn,
            
            String msid,
            String vsid,
            String s_gsid,
            String type,
            String name,
            String comm,
            int pid,
            int suid,
            int fid,
            int id)	
            throws DbException
    {
        Statement stmt = null;
        
        checkFileGenerationValues(name,"S",type, comm);
        
        int fgid = 0;
        String sql = "";
        try 
        {
            stmt = conn.createStatement();
            
            fgid = getNextID(conn,"File_Generations_Seq");            
            sql = "insert into File_Generations Values("+fgid+", "+
                    sqlString(name)+", 'S', "+sqlString(type)+
                    ", "+msid+", "+vsid+", "+sqlString(comm)+", "+pid+", 0, " +
                    id+", "+getSQLDate()+")";
            stmt.execute(sql);
            
            Integer gsid = null;
            if (s_gsid == null)
                gsid = null;
            else
                gsid = Integer.valueOf(s_gsid);
            
            Create_FG_FLT_Link(conn,fgid,suid,fid,gsid);
        } 
        catch (DbException e)
        {
            e.printStackTrace();
            
            throw e;
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to create single file generation\n(" +
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
        return fgid;
    }
    
    public void CreateFgIndLink( Connection conn, int fgid, String suid, String pid, String gsid, String fid)
    {
        GqlTranslator trans = null;
        
      /* The fgid and the individuals are stored in the R_FGID_IND table.
       * The individuals are parsed out from the filter.
       */
        Statement stmt = null;  // to get the GQL expression
        ResultSet rset = null;
        Statement s = null;      // to get the individuals
        ResultSet r = null;
        Statement stmtInsert = null; // insert the values into the rfgind table
        ResultSet rsetInsert = null;
        try{
            
            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT EXPRESSION FROM FILTERS WHERE FID=" + fid);
            int icounter = 0;
            //String suid_s =  ""+suid;
            //String pid_s = ""+pid;
            System.err.println("Före GqlTranslator: CreateFgIndLink");
            if(rset.next()){
                
                trans = new GqlTranslator(pid, suid, gsid, rset.getString("EXPRESSION"), conn);
                trans.translate();
                filter = trans.getFilter();
            }
            
            String ind = null;
            String family = null;
            
            s = conn.createStatement();
            r = s.executeQuery("SELECT "
                    + "ind.IID "
                    + filter);
            
            stmtInsert = conn.createStatement();
            while (r.next()) {
                String insertString = "insert into R_FG_IND " +
                        "(FGID, IID, SUID) VALUES ("+ fgid +"," + r.getInt("iid") + "," + suid + ")";
                stmtInsert.executeUpdate(insertString);
            }
            
        } catch (SQLException sqle) {
            ;
            
        }  catch (Exception e) {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to call PL/SQL procedure\n(" +
                    e.getMessage() + ")");
            
            
        }  finally {
            try{
                if (r != null) r.close();
                if (s != null) s.close();
                if (rset != null) rset.close();
                if (stmt != null) stmt.close();
            } catch (SQLException sqle) {}
        }
    }
    
    public void UpdateFileGeneration( Connection conn,
            String name,
            String comm,
            int fgid,
            int id) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            stmt = conn.createStatement();
            
            checkFileGenerationValues(name,null,null, comm);
            
            sql = "update File_Generations set name="+sqlString(name)+", comm="+sqlString(comm)+" where fgid="+fgid;
            stmt.execute(sql);
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to update file generation\n(" +
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
    
    
    public void DeleteFileGeneration(Connection conn, int fgid) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            stmt = conn.createStatement();
            sql = "delete from data_files where fgid = "+fgid+"; " +
                    "delete from file_generations where fgid = "+fgid;
            stmt.execute(sql);
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to delete file generation\n(" +
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
}