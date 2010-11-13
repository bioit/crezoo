/*
 * DbAllele.java
 *
 * Created on February 18, 2005, 12:46 PM
 */

package se.arexis.agdb.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import se.arexis.agdb.db.TableClasses.AlleleDO;
import se.arexis.agdb.util.Errors;

/**
 *
 * @author heto
 */
public class DbAllele extends DbObject
{
    
    /** Creates a new instance of DbAllele */
    public DbAllele() 
    {
    }
    
  /**
    * Creates an allele
    */
   public void CreateAllele(Connection conn,
                            String name, String comm,
                            String marker, int suid, int id) 
                            throws DbException
   {
       DbMarker dbMark = new DbMarker();
       int mid = dbMark.getMID(conn, name, suid);
       
       CreateAllele(conn,name,comm,mid,id);
   }
   
   
   /**
    * Creates an allele
    */
   public int CreateAllele(Connection conn, String name,
                            String comm, int mid, int id)  throws DbException
   {
      Statement stmt = null;
      String sql = "";
      int aid = 0;
      try 
      {
          aid = getNextID(conn, "Alleles_seq");
          
          sql = "insert into Alleles (aid,name,comm,mid,id,ts) values ( "+
                  aid+", "+sqlString(name)+", "+sqlString(comm)+", "+mid+", "+
                  id+", "+getSQLDate()+")";
          
          stmt = conn.createStatement();
          stmt.execute(sql);
      } 
      catch (Exception e) 
      {
         e.printStackTrace(System.err);
         
         throw new DbException("Internal error. Failed to create allele\n(" +
                          e.getMessage() + ")");
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
      return aid;
   }
   
   public int getAID(Connection conn, String name, int mid)
        throws DbException
   {
       Statement stmt = null;
       String sql = "";
       int aid = 0;
       try
       {
           stmt = conn.createStatement();
           sql = "select aid from alleles where name="+sqlString(name)+ " and mid="+mid;
	  
           ResultSet rs = stmt.executeQuery(sql);
           
           if (rs.next())
           {
               aid = rs.getInt("aid");
           }
           
           if (aid == 0)
           {
               throw new DbException("Unable to find allele.");
           }
       }
       catch (DbException e)
       {
           throw e;
       }
       catch (Exception e)
       {
           e.printStackTrace();
           Errors.logError("SQL="+sql);
       }
       return aid;
   }
   
   /**
     * Get the allele data from the database.
     * @param conn the database connection
     * @param aid the unique allele id
     * @throws se.arexis.agdb.db.DbException throws error messages to the UI
     * @return returns the allele dependent object.
     */
    public AlleleDO getAllele(Connection conn, int aid) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        AlleleDO out = null;
        try
        {
            stmt = conn.createStatement();
            sql = "select * from Alleles where aid = "+aid;
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next())
            {                   
                out = new AlleleDO(rs.getInt("aid"),rs.getString("name"),rs.getString("comm"));
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
            
            throw new DbException("Unable to get allele ["+aid+"]");
        }
            
        return out;
    }
    
    //cursor c_aids is select aid from alleles where mid=p_mid;
    
    /**
     * Get the allele data from the database.
     * @param conn the database connection
     * @param aid the unique allele id
     * @throws se.arexis.agdb.db.DbException throws error messages to the UI
     * @return returns the allele dependent object.
     */
    public AlleleDO[] getAlleles(Connection conn, int mid) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        ArrayList out = null;
        try
        {
            out = new ArrayList();
            
            stmt = conn.createStatement();
            
            sql = "select * from alleles where mid="+mid;
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next())
            {                   
                out.add(new AlleleDO(rs.getInt("aid"),rs.getString("name"),rs.getString("comment")));
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
            
            throw new DbException("Unable to get alleles for mid=["+mid+"]");
        }
            
        return (AlleleDO[])out.toArray();
    }
    
}
