
package se.arexis.agdb.db;

import java.sql.*;
import se.arexis.agdb.util.Errors;
import se.arexis.agdb.db.TableClasses.Species;
import java.util.ArrayList;
import se.arexis.agdb.db.TableClasses.ChromosomeDO;

/**
 * This class provides methods for objects in the database
 * that has something to do with species of the application.
 *
 */
public class DbSpecies extends DbObject
{
    
    /**
     * Default constructor.
     * This constructor doesn't do anything.
     */
    public DbSpecies() {
    }
    
    /**
     * Updates an existing species.
     * @param conn The database connection
     * @param sid the species id for the species to update
     * @param new_name the new name of the species
     * @param new_comm the new comment
     * @throws se.arexis.agdb.db.DbException Throws messages to the UI
     */
    public void UpdateSpecies(Connection conn, int sid,
            String new_name, String new_comm) 
            throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            sql = "update Species set name = "+sqlString(new_name)+", comm = "+sqlString(new_comm)+" where sid = "+sid;
            stmt = conn.createStatement();
            stmt.execute(sql);
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to update species\n(" +
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
    }
    
    /**
     * Deletes an existing species.
     * @param conn The database connection
     * @param sid the species id to delete
     * @throws se.arexis.agdb.db.DbException Throws messages to the UI
     */
    public void DeleteSpecies(Connection conn, int sid) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            
            DbChromosome c = new DbChromosome();
            ChromosomeDO[] chrs = c.getChromosomes(conn, sid);
            
            for (int i=0;i<chrs.length;i++)
            {
                c.DeleteChromosome(conn, chrs[i].getCID());
            }
            sql = "delete from species where sid = "+sid;
            stmt = conn.createStatement();
            stmt.execute(sql);
        } 
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            Errors.logError("SQL="+sql);
            
            throw new DbException("Internal error. Failed to delete species\n(" +
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
    }
    
    /**
     * Creates a new species.
     * @param conn The database connection
     * @param name the name of the new species
     * @param comm the comment for the new species
     * @throws se.arexis.agdb.db.DbException Throws messages to the UI in case of errors.
     */
    public void CreateSpecies(Connection conn, String name, String comm) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        int sid = 0;
        try 
        {
            sid = getNextID(conn, "Species_seq");
            sql = "insert into Species values("+sid+", "+sqlString(name)+", "+sqlString(comm)+")";
            stmt = conn.createStatement();
            stmt.execute(sql);
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            Errors.logError("SQL="+sql);
            
            throw new DbException("Internal error. Failed to create species\n(" +
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
    }
    
    
    
    
    /**
     * Get the species id given the name
     * @param conn The database connection
     * @param name the name of the species
     * @throws se.arexis.agdb.db.DbException In case of errors, the message is thrown towards the UI
     * @return returns the species id (sid)
     */
    public int getSID(Connection conn, String name) throws DbException {
        Statement stmt = null;
        String sql = "";
        int sid = 0;
        try {
            stmt = conn.createStatement();
            sql = "select sid from Species where name="+sqlString(name);
            
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                sid = rs.getInt("sid");
            }
            
            if (sid == 0) {
                throw new DbException("Unable to find species.");
            }
        } catch (DbException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
        }
        return sid;
    }
    
    /**
     * Get the species id given the chromosome id
     * @param conn The database connection
     * @param cid the chromosome id
     * @throws se.arexis.agdb.db.DbException In case of errors, the message is thrown towards the UI
     * @return the species id (sid)
     */
    public int getSID(Connection conn, int cid) throws DbException {
        Statement stmt = null;
        String sql = "";
        int sid = 0;
        try {
            stmt = conn.createStatement();
            sql = "select sid from chromosomes where cid="+cid;
            
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                sid = rs.getInt("sid");
            }
            
            if (sid == 0) {
                throw new DbException("Unable to find species.");
            }
        } catch (DbException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
        }
        return sid;
    }
    
    
    
    
    /**
     * Get the name of a sampling unit from the id
     * @param conn The database connection
     * @param speciesId the species id to lookup the sampling unit
     * @return the name of the sampling unit
     */
    static public String getSpeciesName(Connection conn, String speciesId) {
        String out = "";
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("select name from species where sid="+speciesId);
            
            if (rset.next()) {
                out = rset.getString("name");
            }
        } catch (Exception e) {
            Errors.logError("DbSpecies.getSpeciesName(...) "+ e.getMessage());
        }
        
        return out;
    }
    
    /**
     * Get a list of all species in a project
     * @param conn The database connection
     * @param projectId The project id for the project containing the species.
     * @return A list of species.
     */
    static public ArrayList<Species> getSpecies(Connection conn, int projectId) {
        ArrayList<Species> out = null;
        
        try {
            
            out = new ArrayList<Species>();
            
            Statement sqlStatement = null;
            ResultSet resultSet = null;
            
            sqlStatement = conn.createStatement();
            
            resultSet = sqlStatement.executeQuery("SELECT sid, name from V_Species_2 where pid="+projectId+" ORDER BY NAME");
            
            while (resultSet.next()) {
                out.add(new Species(resultSet.getInt("sid"),
                        resultSet.getString("NAME")));
            }
            resultSet.close();
            sqlStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return out;
    }
}
