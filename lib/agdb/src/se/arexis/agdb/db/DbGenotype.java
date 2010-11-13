/*
 
  $Log$
  Revision 1.7  2005/03/24 15:12:44  heto
  Working with removing oracle dep.

  Revision 1.6  2005/02/22 16:23:43  heto
  Converted DbProject to use PostgreSQL

  Revision 1.5  2005/02/21 11:55:42  heto
  Converting Genotypes to PostgreSQL

  Revision 1.4  2004/03/09 14:19:21  heto
  Fixed alot of bugs in else if clauses then checking syntax for values
 
  Revision 1.3  2003/11/05 07:42:16  heto
  Added function to the test system
 
  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.
 
  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson
 
 
  Revision 1.8  2002/01/29 18:03:39  roca
  Changes by roca (se funktionsbskrivnig for LF025)
 
  Revision 1.7  2001/09/06 13:01:03  roca
  Major changes to Genotype import handling.
  modified Linkage output format for Post makeped and allele numbering.
  Bug when deleting markersets fixed
 
  Revision 1.6  2001/05/22 15:27:59  roca
  Fixed Uppercase comparison when reading genotypes from file
  New Pages for admin/su and admin/species
 
  Revision 1.5  2001/04/24 09:34:05  frob
  Moved file import classes to new package se.arexis.agdb.util.FileImport,
  caused updates in several files.
 
  Revision 1.4  2001/04/24 06:31:39  frob
  Checkin after merging frob_fileparser branch.
 
 
 */

package se.arexis.agdb.db;

import java.util.*;
import java.sql.*;
import se.arexis.agdb.db.TableClasses.AlleleDO;
import se.arexis.agdb.db.TableClasses.GenotypeDO;
import se.arexis.agdb.util.*;
import se.arexis.agdb.util.FileImport.*;


/**
 * This class provides an api of methods to
 * handle genotypes in the database.
 * @author Tomas Bjorklund, Prevas AB
 */
public class DbGenotype extends DbObject {
    
    static
    {
        try {
            // Register known FileTypeDefinitions
            FileTypeDefinitionList.add(FileTypeDefinition.GENOTYPE,
                    FileTypeDefinition.LIST, 1);
            FileTypeDefinitionList.add(FileTypeDefinition.GENOTYPE,
                    FileTypeDefinition.MATRIX, 1);
        } catch (FileTypeDefinitionException e) {
            System.err.println("Construction of new FileTypeDefinition " +
                    "failed: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private Vector genoDiffs = null;
    
    /**
     * Default cobstructor.
     */
    public DbGenotype() {
    }
    
    public void checkGenotypeMatrixHeader(String[] titles) throws DbException
    {
        boolean validHeader = true;
        
        // Check the file header
        if (titles.length < 2)
            validHeader = false;
        else if (!titles[0].equals("IDENTITY") && !titles[0].equals("ALIAS"))
            validHeader = false;
        if (!validHeader) {
            String errStr="Illegal headers.<BR>"+
                    "Required file headers: IDENTITY/ALIAS MARKER1 MARKER2 ...<BR>"+
                    "Headers found in file:";
            for (int j=0; j<titles.length;j++) {
                errStr = errStr+ " " + titles[j];
            }
            
            throw new DbException(errStr);
        }
    }
    
    public void checkGenotypeListHeader(String[] titles) throws DbException
    {
        boolean validHeader = true;
        
        // Check the file header
        if (titles.length != 8)
            validHeader = false;
        else if (!(titles[0].equals("IDENTITY") || titles[0].equals("ALIAS")) ||
                !titles[1].equals("MARKER") ||
                !titles[2].equals("ALLELE1") ||
                !titles[3].equals("ALLELE2") ||
                !titles[4].equals("RAW1") ||
                !titles[5].equals("RAW2") ||
                !titles[6].equals("REFERENCE") ||
                !titles[7].equals("COMMENT") )
            validHeader = false;
        
        if (!validHeader) 
        {
            String errStr="Illegal headers.<BR>"+
                    "Required file headers: IDENTITY/ALIAS MARKER ALLELE1 ALLELE2 RAW1 RAW2 REFERENCE COMMENT<BR>"+
                    "Headers found in file:";
            for (int j=0; j<titles.length;j++) {
                errStr = errStr+ " " + titles[j];
            }

            throw new DbException(errStr);
        }
    }
    
    /**
     * Creates a batch of genotypes from file. This method takes a FileParse
     * as an argumant. The FileParser should be prepared, that is the method
     * parse shoul already have been called by the calling procedure. 
     */
    public void CreateGenotypesList(FileParser fp, Connection conn, int level, 
            int suid, int id) throws DbException
    {
        String ind, name, marker,
        allele1, allele2,
        raw1, raw2,
        ref, comm,
        indId;
        String[] titles;
        boolean validHeader = true;
        boolean knownIdentity;
        try 
        {
            titles = fp.columnTitles();
            checkGenotypeListHeader(titles);
            
            if (titles[0].equals("IDENTITY"))
            {
                knownIdentity = true;
                indId = "IDENTITY";
            }
            else
            {
                knownIdentity = false;
                indId = "ALIAS";
            }           
            
            DbIndividual dbInd = new DbIndividual();
            DbMarker dbMark = new DbMarker();
            DbAllele dbAllele = new DbAllele();

            int iid = 0;
            int mid = 0;
            int aid1 = 0;
            int aid2 = 0;
            for (int i = 0; i < fp.dataRows() ; i++) 
            {
                ind = fp.getValue(indId, i);
                marker = fp.getValue("MARKER", i);
                allele1 = fp.getValue("ALLELE1", i);
                allele2 = fp.getValue("ALLELE2", i);
                raw1 = fp.getValue("RAW1", i);
                raw2 = fp.getValue("RAW2", i);
                ref = fp.getValue("REFERENCE", i);
                comm = fp.getValue("COMMENT", i);
                
                checkValues(ind, marker, allele1, allele2, raw1, raw2,
                        ref, comm, fp.dataRow2FileRow(i) + 1);
                
                // Get the iid from either the identity or alias
                if (knownIdentity)
                {
                    iid = dbInd.getIID(conn, ind, null, suid);
                }
                else
                {
                    iid = dbInd.getIID(conn, null, ind, suid);
                }
                
                mid = dbMark.getMID(conn, marker, suid);
                
                aid1 = dbAllele.getAID(conn, allele1, mid);
                aid1 = dbAllele.getAID(conn, allele2, mid);
                
                CreateGenotype(conn, iid, mid, aid1, aid2, raw1, raw2, ref, comm, level, id);
            }
        }
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            buildErrorString("Internal error. Failed to create genotype list\n(" +
                    e.getMessage() + ")");
        }
    }
    
    
    private void checkValues(String identity, String marker,
            String allele1, String allele2,
            String raw1, String raw2,
            String ref, String comm, int row) 
            throws DbException
    {
        if (identity == null || identity.trim().equals("")) 
        {
            throw new DbException("Unable to read Identity/Alias at row "+row);
            
        } 
        else if (identity.length() > 11) 
        {
            throw new DbException("Identity/Alias [" + identity + "] exceeds 11 characters at row "+ row);
        }
        
        if (marker == null || marker.trim().equals("")) 
        {
            throw new DbException("Unable to read marker at row "+ row);
            
        } 
        else if (marker.length() > 20) 
        {
            throw new DbException("Marker [" + marker + "] exceeds 20 characters at row"+  row);
        }
        
        if (allele1 != null && allele1.length() > 20) 
        {
            throw new DbException("Allele1 [" + allele1 + "] exceeds 20 characters at row "+ row);
        }
        
        if (allele2 != null && allele2.length() > 20) 
        {
            throw new DbException("Allele2 [" + allele2 + "] exceeds 20 characters at row "+ row);
        }
        
        if (raw1 != null && raw1.length() > 20) 
        {
            throw new DbException("Raw1 [" + raw1 + "] exceeds 20 characters at row "+ row);
        }
        
        if (raw2 != null && raw2.length() > 20) 
        {
            throw new DbException("Raw2 [" + raw2 + "] exceeds 20 characters at row "+ row);
        }
        
        if (ref != null && ref.length() > 32) 
        {
            throw new DbException("Reference [" + ref + "] exceeds 32 characters at row "+ row);
        }
        
        if (comm != null && comm.length() > 256) 
        {
            throw new DbException("Comment exceeds 256 characters at row "+ row);
        }
    }
    
    /**
     * Updates a batch of genotypes from file. This method takes a FileParse
     * as an argumant. The FileParser should be prepared, that is the method
     * parse should already have been called by the calling procedure. 
     */
    public void UpdateGenotypesList(FileParser fp, Connection conn, int level,
            int suid, int id, int maxDiff) throws DbException	
    {
        String ind, name, marker,
        allele1, allele2,
        raw1, raw2,
        ref, comm, indId;
        String message = null;
        String[] titles;
        
        boolean knownIdentity;
        try 
        {
            titles = fp.columnTitles();
            
            checkGenotypeListHeader(titles);
            
            if (titles[0].equals("IDENTITY"))
            {
                knownIdentity = true;
                indId = "IDENTITY";
            }
            else
            {
                knownIdentity = false;
                indId = "ALIAS";
            }
            for (int i = 0; i < fp.dataRows(); i++) 
            {
                ind = fp.getValue(indId, i);
                marker = fp.getValue("MARKER", i);
                allele1 = fp.getValue("ALLELE1", i);
                allele2 = fp.getValue("ALLELE2", i);
                raw1 = fp.getValue("RAW1", i);
                raw2 = fp.getValue("RAW2", i);
                ref = fp.getValue("REFERENCE", i);
                comm = fp.getValue("COMMENT", i);
                
                checkValues(ind, marker, allele1, allele2, raw1, raw2,
                        ref, comm, fp.dataRow2FileRow(i) + 1);
                
                DbMarker dbMark = new DbMarker();
                int mid = dbMark.getMID(conn, marker, suid);
                
                
                DbIndividual dbInd = new DbIndividual();
                int iid=0;
                if (knownIdentity)
                    iid = dbInd.getIID(conn, ind, null, suid);
                else
                    iid = dbInd.getIID(conn, null, ind, suid);
                
                GenotypeDO gen = getGenotype(conn,mid,iid);
                
                DbAllele dbAllele = new DbAllele();
                AlleleDO a1 = dbAllele.getAllele(conn, gen.getAid1());
                
                // Switch A1 & A2!
                if (a1.getName() != allele1)
                {
                    UpdateGenotype(conn, iid, mid, allele2, allele1, raw1, raw2, ref, comm, level, id);
                }
                else
                {
                    UpdateGenotype(conn, iid, mid, allele1, allele2, raw1, raw2, ref, comm, level, id);
                }
            }
        }
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to update genotype list\n(" +
                    e.getMessage() + ")");
        } 
    }
    
     /**
     * Get the allele data from the database.
     * @param conn the database connection
     * @param aid the unique allele id
     * @throws se.arexis.agdb.db.DbException throws error messages to the UI
     * @return returns the allele dependent object.
     */
    public GenotypeDO getGenotype(Connection conn, int mid, int iid) throws DbException
    {
        Statement stmt = null;
        String sql = "";
        GenotypeDO out = null;
        try
        {
            stmt = conn.createStatement();
            sql = "select * from Genotype where mid = "+mid+" and iid="+iid;
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next())
            {                   
                out = new GenotypeDO(rs.getInt("mid"),
                        rs.getInt("iid"),
                        rs.getInt("aid1"), 
                        rs.getInt("aid2"), 
                        rs.getInt("suid"), 
                        rs.getInt("level"), 
                        rs.getString("raw1"),
                        rs.getString("raw2"),
                        rs.getString("reference"),
                        rs.getInt("id"), 
                        rs.getString("ts"),
                        rs.getString("comm"));
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Errors.logError("SQL="+sql);
            
            throw new DbException("Unable to get genotype ["+mid+","+iid+"]");
        }
        return out;
    }
    
    
    public void CreateOrUpdateGenotypesList(FileParser fp, Connection conn, int level,
            int suid, int id, int maxDiff) throws DbException
    {
        String ind, name, marker,
        allele1, allele2, indId,
        raw1, raw2,
        ref, comm;
           
        String[] titles;
        boolean knownIdentity;
        try 
        {
            titles = fp.columnTitles();
            checkGenotypeListHeader(titles);
            
            if (titles[0].equals("IDENTITY"))
            {
                knownIdentity = true;
                indId = "IDENTITY";
            }
            else
            {
                knownIdentity = false;
                indId = "ALIAS";
            }
            
            DbMarker dbMark = new DbMarker();
            DbIndividual dbInd = new DbIndividual();
            DbAllele dbAllele = new DbAllele();
                        
            for (int i = 0; i < fp.dataRows(); i++) 
            {
                ind = fp.getValue(indId, i);
                marker = fp.getValue("MARKER", i);
                allele1 = fp.getValue("ALLELE1", i);
                allele2 = fp.getValue("ALLELE2", i);
                raw1 = fp.getValue("RAW1", i);
                raw2 = fp.getValue("RAW2", i);
                ref = fp.getValue("REFERENCE", i);
                comm = fp.getValue("COMMENT", i);
                
                CommonCreateOrUpdate(conn, ind, marker, allele1, allele2, raw1, raw2, ref, comm, 
                            suid, dbMark , dbInd, dbAllele, knownIdentity, 
                            fp.dataRow2FileRow(i), level, id );
            }
        } 
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to create or update genotype list\n(" +
                    e.getMessage() + ")");
        } 
    }
    
    public Vector getGenoDiffs() {
        if (genoDiffs == null)
            genoDiffs = new Vector();
        
        return genoDiffs;
    }
    
    /**
     * Creates a genotype.
     */
    public void CreateGenotype(Connection conn, int iid, int mid, Integer aid1, Integer aid2,
            String raw1, String raw2, String ref, String comm, int level,  int id) 
            throws DbException
    {
        if (level < 0 || level > 10)
            level = 1;

        // Throws an exception if not found
        DbMarker mark = new DbMarker();
        mark.getMarker(conn, mid);

        // Throws an exception if not found
        DbAllele dba = new DbAllele();
        dba.getAllele(conn, aid1);
        dba.getAllele(conn, aid2);
        
        // Throws an exception if not found
        DbIndividual ind = new DbIndividual();
        int suid = ind.getIndividual(conn, iid).getSUID();
  
        Statement stmt = null;
        String sql = "";
        try 
        {
            
            sql = "insert into Genotypes (mid, iid, aid1, aid2, suid, " +
                    "level_, raw1, raw2, reference, id, ts, comm) " +
                    "values("+mid+", "+iid+", "+aid1+", "+aid2+", "+suid+", "+level+"," +
                    sqlString(raw1)+", "+sqlString(raw2)+", "+sqlString(ref)+", "+id+", "+getSQLDate()+", "+sqlString(comm)+")";
            stmt = conn.createStatement();
            stmt.execute(sql);
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            Errors.logError("SQL="+sql);
            
            throw new DbException("Internal error. Failed to create genotype\n(" +
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
     * Updates a genotype
     */
    public void UpdateGenotype(Connection conn, int suid, String identity , String marker,
            String a1name, String a2name, String raw1, String raw2,
            String ref, String comm, int level, int id) throws DbException
    {
        DbIndividual ind = new DbIndividual();
        int iid = ind.getIID(conn, identity, null, suid);
        
        DbMarker mark = new DbMarker();
        int mid = mark.getMID(conn, marker, suid);
        
        UpdateGenotype(conn, iid, mid, a1name, a2name, raw1, raw2, ref, comm, level, id);
    }
    
    /**
     * Updates a genotype.
     */
    public void UpdateGenotype(Connection conn, int iid, int mid,
            String a1name, String a2name, String raw1, String raw2,
            String ref, String comm, int level, int id) 
            throws DbException
    {
        DbAllele dba = new DbAllele();

        int a1id = dba.getAID(conn, a1name, mid);
        int a2id = dba.getAID(conn, a2name, mid);

        UpdateGenotype(conn,iid,mid,a1id,a2id,raw1,raw2,ref,comm,level,id);
    }
    
    
    
   
    
    /**
     * Updates a genotype.
     */
    public void UpdateGenotype(Connection conn, int iid, int mid,
            Integer aid1, Integer aid2, String raw1, String raw2,
            String ref, String comm, int level, int id) 
            throws DbException
    {
        Statement stmt = null;
        String sql = "";
        String sql_log = "";
        
        try 
        {
            // Check the level parameter
            if (level < 0 || level > 10)
                level = 1;
            
            sql_log = "insert into Genotypes_Log (mid, iid, aid1, aid2, level_, " +
                    "raw1, raw2, reference, id, ts, comm) " +
                    "select mid, iid, aid1, aid2, level_, raw1, raw2, reference, " +
                    "id, ts, comm " +
                    "from Genotypes " +
                    "where mid = "+mid+" and iid = "+iid;
            
            stmt = conn.createStatement();
            stmt.execute(sql_log);
            
            // if the alleles does not exists. This will throw an DbException
            DbAllele dba = new DbAllele();
            dba.getAllele(conn,aid1);
            dba.getAllele(conn,aid2);
            
            sql = "update Genotypes set aid1 = "+aid1+", aid2 = "+aid2+", " +
                    "level_ = "+level+", raw1 = "+sqlString(raw1)+", " +
                    "raw2 = "+sqlString(raw2)+", reference = "+sqlString(ref)+", " +
                    "id = "+id+", ts = "+getSQLDate()+", comm = "+sqlString(comm)+" " +
                    "where mid = "+mid+" and iid = "+iid;
            
            stmt.execute(sql);
        } 
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            Errors.logError("SQL_LOG="+sql_log);
            Errors.logError("SQL="+sql);
            
            throw new DbException("Internal error. Failed to update genotype\n(" +
                    e.getMessage() + ")");
        } 
        finally 
        {
            try 
            {
                if (stmt != null) stmt.close();
            } 
            catch (SQLException sqle) {}
        }
    }
    
    /**
     * Delete a genotype.
     */
    public void DeleteGenotype(Connection conn, int iid, int mid) 
        throws DbException
    {
        Statement stmt = null;
        String sql = "";
        try 
        {
            sql = "delete from genotypes_log where mid = p_mid and iid = "+iid+"; " +
                    "delete from Genotypes where mid = p_mid and iid = "+iid;
            stmt = conn.createStatement();
            stmt.execute(sql);
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            Errors.logError("SQL="+sql);
            
            throw new DbException("Internal error. Failed to delete genotype\n(" +
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
    
    
    public void CreateGenotypesMatrix(MatrixFileParser gfp, Connection conn, 
            int level, int suid, int id) throws DbException
    {
        CommonGenotypesMatrix(gfp, conn, level, suid, id, 0, true);
    }
    
    public void UpdateGenotypesMatrix(MatrixFileParser gfp, Connection conn, int level,
            int suid, int id, int maxDiff) throws DbException
    {
        CommonGenotypesMatrix(gfp, conn, level, suid, id, maxDiff, false);
    }
    
    private void CommonGenotypesMatrix(MatrixFileParser gfp, Connection conn, int level,
            int suid, int id, int maxDiff, boolean create) throws DbException
    {
        String ind, name, marker,
        allele1, allele2, indId;
        String[] titles;
        String[] markers;
        String[] alleles;
        boolean knownIdentity;
        try 
        {
            titles = gfp.columnTitles();
            markers = new String[titles.length-1];
            for (int i = 0; i < markers.length; i++)
                markers[i] = titles[i+1];
            
            checkGenotypeMatrixHeader(titles);
            
       
            
            if (titles[0].equals("IDENTITY"))
            {
                knownIdentity = true;
                indId = "IDENTITY";
            }
            else
            {
                knownIdentity = false;
                indId = "ALIAS";
            }
            
            DbIndividual dbInd = new DbIndividual();
            DbMarker dbMark = new DbMarker();
            DbAllele dbAllele = new DbAllele();
            
            boolean ok = true;
            for (int row = 0; row < gfp.dataRows() && ok; row++) 
            {
                ind = gfp.getValue(indId, row)[0];
                
                for (int mNum = 0; mNum < markers.length && ok ; mNum++) 
                {
                    marker = markers[mNum];
                    alleles = gfp.getValue(marker, row);
                    allele1 = alleles[0];
                    allele2 = alleles[1];
                    
                    
                    checkValues(ind, marker, allele1, allele2, null, null,
                            null, null, gfp.dataRow2FileRow(row) + 1);
                                        
                    int iid = 0;
                    if (knownIdentity)
                        iid = dbInd.getIID(conn, ind, null, suid);
                    else
                        iid = dbInd.getIID(conn, null, ind, suid);
                    
                    int mid = dbMark.getMID(conn, marker.toUpperCase(), suid);
                    
                    if (create)
                    {
                        int aid1 = dbAllele.CreateAllele(conn, allele1, "Autocreated", mid, id);
                        int aid2 = dbAllele.CreateAllele(conn, allele2, "Autocreated", mid, id);  
                    
                        CreateGenotype(conn, iid, mid, aid1, aid2, null, null, null, "Autocreated", level, id);
                    }
                    else
                    {
                        int aid1 = dbAllele.getAID(conn, allele1, mid);
                        int aid2 = dbAllele.getAID(conn, allele2, mid);
                        UpdateGenotype(conn, iid, mid, aid1, aid2, null, null, null, null, level, id);
                    }
                } // end of marker loop
            } // End of row loop
        } 
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to call PL/SQL procedure\n(" +
                    e.getMessage() + ")");
        } 
    }
    
    
    
    
    
    public void CreateOrUpdateGenotypesMatrix(MatrixFileParser gfp, Connection conn, int level,
            int suid, int id, int maxDiff)	throws DbException
    {
        String ind, name, marker, allele1, allele2, indId;
        String[] titles;
        String[] markers;
        String[] alleles;
        boolean knownIdentity;
        try 
        {
            titles = gfp.columnTitles();
            markers = new String[titles.length-1];
            for (int i = 0; i < markers.length; i++)
                markers[i] = titles[i+1];
            
            checkGenotypeMatrixHeader(titles);
            
            if (titles[0].equals("IDENTITY"))
            {
                knownIdentity = true;
                indId = "IDENTITY";
            }
            else
            {
                knownIdentity = false;
                indId = "ALIAS";
            }
            
            DbMarker dbMark = new DbMarker();
            DbIndividual dbInd = new DbIndividual();
            DbAllele dbAllele = new DbAllele();
            
            for (int row = 0; row < gfp.dataRows(); row++) 
            {
                ind = gfp.getValue(indId, row)[0];
                
                for (int mNum = 0; mNum < markers.length ; mNum++) 
                {
                    marker = markers[mNum];
                    alleles = gfp.getValue(marker, row);
                    allele1 = alleles[0];
                    allele2 = alleles[1];
                    
                    CommonCreateOrUpdate(conn, ind, marker, allele1, allele2,null,null,null,null,  
                            suid, dbMark , dbInd, dbAllele, knownIdentity, 
                            gfp.dataRow2FileRow(row), level, id );
                } // end of marker loop
            } // End of row loop
        } 
        catch (DbException e)
        {
            throw e;
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to call PL/SQL procedure\n(" +
                    e.getMessage() + ")");
        } 
    }
    
    private void CommonCreateOrUpdate(Connection conn, String ind, 
            String marker, String allele1, String allele2, String raw1, 
            String raw2, String ref, String comm, int suid, 
            DbMarker dbMark , DbIndividual dbInd, DbAllele dbAllele, 
            boolean knownIdentity, int row,  int level, int id )
            throws DbException
    {
        checkValues(ind, marker, allele1, allele2, null, null,
                            null, null, row + 1);
                    
        int mid = dbMark.getMID(conn, marker, suid);

        int iid=0;
        if (knownIdentity)
            iid = dbInd.getIID(conn, ind, null, suid);
        else
            iid = dbInd.getIID(conn, null, ind, suid);

        GenotypeDO gen = getGenotype(conn,mid,iid);

        // Genotype does not exist
        if (gen == null) 
        {
            int aid1 = dbAllele.CreateAllele(conn, allele1, "Autocreated allele in create_or_update", mid, id);
            int aid2 = dbAllele.CreateAllele(conn, allele2, "Autocreated allele in create_or_update", mid, id);

            CreateGenotype(conn, iid, mid, aid1, aid2, raw1, raw2, ref, comm, level, id);
        }
        else
        {
            UpdateGenotype(conn, iid, mid, allele1, allele2, raw1, raw2, ref, comm, level, id);
        }
    }
    
    
    
    
    public void CreateAllelesList(FileParser fp, Connection conn, int samplingUnitId, int userId) 
    {
        String marker, allele1, allele2;
        try 
        {
            
            DbAllele da = new DbAllele();
            DbMarker dm = new DbMarker();
            
            // check if they are missing!!
            // check all rows in file
            for (int i = 0; i < fp.dataRows(); i++) 
            {
                // get values
                marker = fp.getValue("MARKER", i);
                allele1 = fp.getValue("ALLELE1", i);
                allele2 = fp.getValue("ALLELE2", i);
                
                int mid = dm.getMID(conn, marker, samplingUnitId);
                
                try
                {
                    int aid1 = da.getAID(conn, allele1, mid);
                }
                catch (Exception e)
                {
                    da.CreateAllele(conn, allele1, null, mid, userId);
                }
                try
                {
                    int aid2 = da.getAID(conn, allele2, mid);
                }
                catch (Exception e)
                {
                    da.CreateAllele(conn, allele2, null, mid, userId);
                }                
            } 
        }
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            buildErrorString("Internal error. Failed to create alleles list\n(" +
                    e.getMessage() + ")");
        } 
    }
    
    
    public void CreateAllelesMatrix(MatrixFileParser mfp, Connection conn, 
            int samplingUnitId, int userId) throws DbException
    {
        String marker, allele1, allele2;
        String titles[];
        String indId, ind;
        String markers[];
        
        DbAllele da = new DbAllele();
        DbMarker dm = new DbMarker();
        
        try 
        {
            titles=mfp.columnTitles();
            if (titles[0].equals("IDENTITY"))
                indId="IDENTITY";
            else
                indId="ALIAS";
            
            // check all rows in file
            for (int row = 0; row < mfp.dataRows(); row++) {
                ind = mfp.getValue(indId, row)[0];
                markers = new String[titles.length-1];
                for (int i = 0; i < markers.length; i++)
                    markers[i] = titles[i+1];
                
                // check the whole row
                for (int mNum = 0; mNum < markers.length; mNum++) 
                {
                    String old_alleles[]=null;
                    marker = markers[mNum];
                    String alleles[] = mfp.getValue(marker, row);
                    allele1 = alleles[0];
                    allele2 = alleles[1];
                    
                    // do the alleles exist for these markers??
                    int mid = dm.getMID(conn, marker, samplingUnitId);
                    try
                    {
                        da.getAID(conn, allele1, mid);
                    }
                    catch (Exception e)
                    {
                        da.CreateAllele(conn, allele1, "Autocreated by Genotype import", mid, userId);
                    }
                    
                    try
                    {
                        da.getAID(conn, allele2, mid);
                    }
                    catch (Exception e)
                    {
                        da.CreateAllele(conn, allele2, "Autocreated by Genotype import", mid, userId);
                    }
                }//for markers
                
            }// for rows
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            
            throw new DbException("Internal error. Failed to create alleles matrix\n(" +
                    e.getMessage() + ")");
        } 
    }
    
    /**
     * Load the individuals to the test-objects
     */
    public void loadGenotype(Connection conn, DataObject db, int suid) {
        Statement stmt;
        
        try {
            stmt = conn.createStatement();
            
            String sql = "select identity, mname as marker, a1name as a1, a2name as a2 from V_Genotypes_4 where suid="+suid;
            ResultSet rs = stmt.executeQuery(sql);
            
            stmt = conn.createStatement();
            
            String identity, marker, a1, a2;
            while (rs.next() ) {
                identity = rs.getString("identity");
                marker   = rs.getString("marker");
                a1       = rs.getString("a2");
                a2       = rs.getString("a2");
                
                db.setGenotype(identity,marker,a1,a2);
            }
        } catch (Exception e) {
            Errors.logError(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
    
}





