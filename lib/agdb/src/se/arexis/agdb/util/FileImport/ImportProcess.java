/*
 * ImportProcess.java
 *
 * Created on December 18, 2002, 1:50 PM
 *
 * $Log$
 * Revision 1.37  2005/05/17 08:09:00  heto
 * *** empty log message ***
 *
 * Revision 1.36  2005/04/04 14:29:44  heto
 * Resolved conflict
 *
 * Revision 1.35  2005/04/04 13:58:09  heto
 * Commit before merging ant-scripts
 *
 * Revision 1.34  2005/03/03 15:41:37  heto
 * Converting for using PostgreSQL
 *
 * Revision 1.33  2005/01/31 16:16:41  heto
 * Changing database to PostgreSQL. Problems with counts and selection buttons...
 *
 * Revision 1.32  2005/01/31 12:59:04  heto
 * Making stronger separation of the import modules.
 *
 * Revision 1.31  2004/12/14 08:50:24  heto
 * Changed the import process to make the modules more independant. Every module must advertise its capabilities to let the import process know what a module can do.
 *
 * Revision 1.30  2004/04/27 13:47:39  wali
 * Added marker set abd unified marker set functionality
 *
 * Revision 1.29  2004/04/27 06:20:39  wali
 * Added variabelset and uvariableset functionality
 *
 * Revision 1.28  2004/04/23 10:02:38  wali
 * Adopted for Species manipulations, mainly in run, which is splitted to runSamplinUnit and runSpecies. Some other methods added.
 *
 * Revision 1.27  2004/04/05 14:05:44  heto
 * Added fileformats
 *
 * Revision 1.26  2004/04/02 12:18:38  heto
 * Store the blob on disk and then check format. Some weird error has occured sometimes...
 *
 * Revision 1.25  2004/04/02 08:12:26  heto
 * Loading data to test objects from db
 *
 * Revision 1.24  2004/04/01 15:01:24  heto
 * Added Grouping and Sample format
 *
 * Revision 1.23  2004/03/31 08:53:00  heto
 * Fixed debug messages
 *
 * Revision 1.22  2004/03/26 15:00:06  heto
 * Fixed log messages
 *
 * Revision 1.21  2004/03/26 13:46:23  heto
 * Fixing debug messages.
 * Removed dead code
 * Fixed if-clause
 *
 * Revision 1.20  2004/03/26 07:27:21  heto
 * Fixing debug messages.
 * Removed dead code
 *
 * Revision 1.19  2004/03/25 13:33:42  heto
 * Changed locking to sampling unit and not project
 * Fixed error in error status. One error in a file means that the import set will fail
 *
 * Revision 1.18  2004/03/19 10:38:01  heto
 * Fixed debug messages
 * FIxed update flag
 *
 * Revision 1.17  2004/03/18 13:23:36  heto
 * Check for invalid import files
 *
 * Revision 1.16  2004/03/17 13:12:05  heto
 * Removed session data
 *
 * Revision 1.15  2004/03/17 07:28:40  heto
 * Changed table name to import_set
 *
 * Revision 1.14  2004/03/09 09:58:17  heto
 * Removed dead code.
 * Change status setting method
 *
 * Revision 1.13  2004/03/08 12:08:16  heto
 * Changed connection to conn_viss for all queries that should be vissible outside the import process transaction. This is important for error handling, can rollback the database
 *
 * Revision 1.12  2004/03/02 09:28:37  heto
 * Added Phenotype file importing in the new system
 *
 * Revision 1.11  2004/02/25 13:54:35  heto
 * Added import of variable data file
 *
 * Revision 1.10  2004/02/12 10:26:06  heto
 * Adding support for blob operation. This affects the import system
 *
 * Revision 1.9  2003/11/05 07:47:24  heto
 * Refined the import system
 *
 * Revision 1.8  2003/05/15 06:41:18  heto
 * Changed the return type from void to boolean for check and imp.
 * Working with getting messages to the user
 *
 * Revision 1.7  2003/05/09 14:49:48  heto
 * Check process is integrated to the importProcess
 *
 * Revision 1.6  2003/05/02 07:58:45  heto
 * Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
 * Modified configuration and source files according to package change.
 *
 * Revision 1.5  2003/04/29 15:26:31  heto
 * Documenting code
 * removed parameter on private method (sharing in the object was user)
 *
 *
 */

package se.arexis.agdb.util.FileImport;

import java.sql.*;
import java.util.*;
import java.io.File;

import se.arexis.agdb.db.*;
import se.arexis.agdb.util.Errors;
import se.arexis.agdb.util.FileImport.Dependency;

/**
 * This class is the framework for handling importing data to aGDB.
 * This framework provides parallell support for upload and importing data
 * to the database. 
 *
 * This class implements the Thread superclass to provide parallell threads.
 * 
 * Then adding new import modules to aGDB, the modules must be a subclass of 
 * DataObject and implement the advertiesed capabilities. Also, the must be 
 * added to initImportObjects() method to be included in the imports. The
 * sorting mechanism must also be changed to sort the new file type.
 *
 * @author  heto
 *
 */
public class ImportProcess extends Thread 
{
    /** Test implementation for all ImportData objects*/
    private ArrayList<ImportData> impObjs;
    
    /** The two modes "CHECK" or "IMPORT" determins
     * which state the import process is on
     */
    private String mode;
    
    /** The connection to use all long running tasks with */
    private Connection conn;
    
    /** The connection for doing short and visible tasks (status messages) */
    private Connection conn_viss;
    
    /** The import session id */
    private String isid;
        
    /** The sample unit id the data will be imported to */
    private int sampleUnitId;
        
    /** 
     * The lock for importing data.
     * Only one import session is allowed in one sampling unit at the time 
     */
    private boolean haveLock;
    
    /** All preferences for the import process */
    private Prefs prefs;

    
    /** Create an import process then asking underlying modules.
     * This constructor should not be used if the import process will be 
     * started, just for queries about the file formats.
     */
    public ImportProcess()
    {
        initImportObjects();
    }
    
    /** Create an import process, the unified version
     */
    public ImportProcess(Prefs prefs)
    {
        this.prefs = prefs;
        
        prefs.debug();
        
        // These are used in this class
        // The rest are passed on the the next object level.
        //updateMethod = prefs.updateMethod;
        isid = Integer.valueOf(prefs.isid).toString();
        conn = prefs.connection;
        conn_viss = prefs.connViss;
        sampleUnitId = prefs.sampleUnitId;
        mode = prefs.mode;
    }    
    
    /**
     * Check if it is possible to create a lock for import.
     * 
     * @return will return true if a lock was successful, otherwise returing false
     */
    public synchronized boolean lock()
    {
        Errors.logInfo("ImportProcess.lock() "+isid+" started");
        boolean out = false;
        try
        {
            DbImportSet dbSet = new DbImportSet();
            conn.setAutoCommit(false);
 
            if (dbSet.isLocked(conn,Integer.valueOf(isid).intValue()))
            {
                throw new Exception("Import is locked for this sampling unit: "+sampleUnitId);
            }

            if (mode.equals("CHECK"))
                dbSet.setStatus(conn,isid,"CHECKING");
            else if (mode.equals("IMPORT"))
                dbSet.setStatus(conn,isid,"IMPORTING");
            else
                throw new Exception("Mode is invalid. CHECK | IMPORT : "+mode);
         
            conn.commit();
           
            haveLock = true;
            out = true;
        }
        
        catch (Exception e)
        {
            e.printStackTrace();
            Errors.logWarn("ImportProcess.lock() "+isid+" Failed to lock for import: "+e.getMessage());
            out = false;
            haveLock = false;
            
            try
            {
                conn.rollback();
                Errors.logWarn("ImportProcess.lock() "+isid+" Rollback");
            }
            catch (Exception e2)
            {
            }
        }
        Errors.logInfo("ImportProcess.lock() "+isid+" ended");
        return out;
    }
    
    /**
     * This function fetches the first row of the file, the header, and
     * stores this information in the import_file table. If the file is
     * unknown to the import system (not a standard aGDB file with a header) 
     * then all import modules must be asked if anyone recognizes the file 
     * and then set the virtual file header
     */
    public void createHeaders(Connection conn, int ifid)
    {
        try
        {
       
            // Get the header of the file.
            // 1. Parse the file header of the file
            // 2. If the parsing generates an exception, then a virtual header 
            //    row are checked by asking all import objects if anyone 
            //    recognizes the file

            //FileHeader hdr = dbimpfile.getFileHeader(conn_viss, tmp_ifid);
            FileHeader hdr = null;
            DbImportFile dbfile = new DbImportFile();
            try
            {
                hdr = AbstractFileParser.parseHeader(dbfile.getImportFileHeader(conn,String.valueOf(ifid)));
            }
            catch (Exception e)
            {
                for (int i=0;i<impObjs.size();i++)
                {
                    
                    hdr = impObjs.get(i).examineFile(conn,ifid);
                    
                    if (hdr!=null)
                        break;
                }
            }
            
            // If the header is not null, Save the header info to the database.
            if (hdr!=null)
                dbfile.saveFileHeader(conn, ifid, hdr);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Start the process of importing one or several files to the database.
     *
     */
    public void run()
    {
        Errors.logDebug("ImportProcess.run() started");
        
        initImportObjects();
        
        Errors.logInfo("ImportProcess.runSamplingUnit() "+isid+" started, isid="+isid);
        Statement stmt = null;
        ResultSet rset = null;
        
        DataObject db = new DataObject();
        
        String systemFileName = "";
        DbImportFile dbFile = null;
        DbImportSet dbSession = null;
        try
        {
            dbSession = new DbImportSet();
            
            if (sampleUnitId != 0 && prefs.speciesId !=0)
                throw new Exception("Importing of files of both sampleUnitId and speciesId dependent data at the same time is not permitted.");
            
            // Check for lock if sampleUnitId is used, Not for unified formats!
            if (sampleUnitId!=0 && haveLock == false)
                throw new Exception("Import is locked in this sampling unit!");
            
            /*
             Start a transaction.
             It forbidden for any part of the program to issue a commit 
             to the "conn" object. If something fails it should be possible
             to rollback!
             */
            conn.setAutoCommit(false);
            
            dbFile = new DbImportFile();
            
            
            /* Write message to the log */
            if (mode.equals("CHECK"))
                Errors.logInfo("Check started");
            else if (mode.equals("IMPORT"))
                Errors.logInfo("Import started");
            else
                throw new Exception("Invalid mode: "+mode+". (CHECK | IMPORT)");
            
            // Get the allowed files in correct order
            ArrayList importFiles = sortImportFiles();
            
            //System.err.println("Names="+importFiles.toString());
            
            
            
            /* Initialize the check objects */
            DbIndividual    db_ind  = null;
            DbMarker        db_mark = null;
            DbGenotype      db_gen  = null;
            DbVariable      db_var  = null;
            DbPhenotype     db_phen = null;
            
            DbUMarker       db_umark = null;
            DbUVariable     db_uvar  = null;
            
            
            /* If check, then load info from the database */
            if (mode.equals("CHECK"))
            {
                if (sampleUnitId!=0)
                {
                    Errors.logDebug("Importing data to test objects");
                    db_ind = new DbIndividual();
                    db_ind.loadIndividual(conn,db,sampleUnitId);
                    db_ind.loadGroupings(conn, db, sampleUnitId);
                    db_ind.loadSamples(conn, db, sampleUnitId);

                    db_mark = new DbMarker();
                    db_mark.loadAlleles(conn,db,sampleUnitId);
                    db_mark.loadMarkers(conn,db,sampleUnitId);

                    db_gen = new DbGenotype();
                    db_gen.loadGenotype(conn,db,sampleUnitId);

                    db_var = new DbVariable();
                    db_var.loadVariables(conn, db, sampleUnitId);

                    db_phen = new DbPhenotype();
                    db_phen.loadPhenotype(conn, db, sampleUnitId);
                }
                else if (prefs.speciesId!=0)
                {
                    db_umark = new DbUMarker();
                    db_umark.loadUMarkers(conn,db,prefs.speciesId);
                    
                    db_uvar = new DbUVariable();
                    db_uvar.loadUVariables(conn, db, prefs.speciesId);
                }                
            }
            
            // Set the status for the session
            // Only one import of a session can be made at a time 
            // in the same sampling unit!
           
            int res_err = 0;
            ImportFileStruct tmp = null;
            boolean res = false;
            for (int i=0;i<importFiles.size();i++)
            {
                tmp = (ImportFileStruct)importFiles.get(i);
                Errors.logDebug("Name="+tmp.name+", Type="+tmp.type+", IFID="+tmp.ifid);
                
                if (tmp.hdr==null)
                    Errors.logDebug("hdr is null!!!!!!!!");
                
                
                // Add file info to prefs object
                prefs.fileName=tmp.name;
                prefs.ifid=Integer.valueOf(tmp.ifid).intValue();
                
                
                //ImportData impObj = getImportModule(getHeader(conn_viss,tmp.ifid));
                ImportData impObj = getImportModule(tmp.hdr);
                if (impObj!=null)
                {
                    impObj.setPrefs(prefs,db);
                        
                    if (mode.equals("CHECK"))
                        res = impObj.check();
                    else if (mode.equals("IMPORT"))
                        res = impObj.imp();
                }
                else
                    throw new Exception("ImportModule not found for file");
                
                /*
                // Itterate all avaliable import format objects and 
                // match the right object responsible for import of the data.
                for (int j=0;j<impObjs.size();j++)
                {    
                    if (impObjs.get(j).getFormat().equals(tmp.type))
                    {
                        //impObjs.get(j).setParameters(pid,isid,tmp.ifid,upPath,tmp.name,update,conn,conn_viss,sampleUnitId,userId,level,maxDev,db);
                        impObjs.get(j).setPrefs(prefs,db);
                        
                        if (mode.equals("CHECK"))
                            res = impObjs.get(j).check();
                        else if (mode.equals("IMPORT"))
                            res = impObjs.get(j).imp();
                    }
                }
                 */
                
                // Count errors
                if (res == false)
                    res_err++;
            }
            
            // Reset the status then done.
            /* Write message to the log */
            if (importFiles.size()==0)
            {
                //dbSession.setStatus(conn_viss,isid,"ERROR");
                Errors.logWarn("ImportProcess.run() "+isid+" No valid import formats in isid="+isid);
                throw new Exception("Rollback");
            }
            else if (mode.equals("CHECK") && res_err == 0)
            {
                dbSession.setStatus(conn_viss,isid,"CHECKED");
                Errors.logInfo("Check completed");
            }
            else if (mode.equals("CHECK") && res_err != 0)
            {
                //dbSession.setStatus(conn_viss,isid,"ERROR");
                Errors.logWarn("Check completed with errors");
                throw new Exception("Rollback");
            }
            else if (mode.equals("IMPORT") && res_err == 0)
            {
                dbSession.setStatus(conn_viss,isid,"IMPORTED");
                Errors.logInfo("Import completed");
            }
            else if (mode.equals("IMPORT") && res_err != 0)
            {
                //dbSession.setStatus(conn_viss,isid,"ERROR");
                Errors.logWarn("Import completed with errors");
                throw new Exception("Rollback");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                // Set the message to error if something went wrong.
                if (dbSession != null)
                    dbSession.setStatus(conn_viss,isid,"Error");
            }
            catch (Exception ignore)
            {
            }
            
            try
            {
                /*
                * Errors ocurred. Full rollback!
                */
                
                conn.rollback();
                Errors.logWarn("ImportProcess.run() "+isid+" Rollback");
            }
            catch (Exception ignore)
            {
            }
            
            e.printStackTrace();
            //buildErrorString(e.getMessage());
        }
        finally
        {
            try
            {
                /* All ok. Commit changes!*/
                conn.commit();
                Errors.logInfo("ImportProcess.run() "+isid+" Commit");
                
                if (stmt != null)
                    stmt.close();
                
                // Redirect to the page to display the files.
                //res.sendRedirect(getServletPath("importFile/files?isid="+isid));
            }
            catch (Exception ignore)
            {}
        }
        
        Errors.logDebug("ImportProcess.run() ended");
    }
    
    /**
     * Get the import module that can handle the import file with 
     * the given header.
     *
     * If no match is found, this returns null
     *
     */
    public ImportData getImportModule(FileHeader hdr)
    {
        // Itterate all avaliable import format objects and 
        // match the right object responsible for import of the data.
        for (int j=0;j<impObjs.size();j++)
        {    
            if (impObjs.get(j).supportFormat(hdr))
            {
                return impObjs.get(j);
            }
        }
        return null;
    }
    
    private FileHeader getHeader(Connection conn_viss,String ifid)
    {
        FileHeader hdr = null;
        try
        {
            DbImportFile dbimpfile = new DbImportFile();
            String filename = dbimpfile.storeImportFileBLOB(conn_viss, ifid);
            hdr = FileParser.scanFileHeader(filename);

            File tmp = new File(filename);
            tmp.delete();
            tmp = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        
        return hdr;
    }
    
    /**
     * Inititate all import formats in the impObjs array
     */
    public void initImportObjects()
    {
        impObjs = new ArrayList<ImportData>();
        
        impObjs.add(new ImportIndividual());
        impObjs.add(new ImportMarkers());
        impObjs.add(new ImportMarkerSet());
        impObjs.add(new ImportUMarkerSet());
        impObjs.add(new ImportUMarkers());
        impObjs.add(new ImportGenotypes());
        impObjs.add(new ImportVariables());
        impObjs.add(new ImportVariableSet());
        impObjs.add(new ImportUVariables());
        impObjs.add(new ImportUVariableSet());
        impObjs.add(new ImportPhenotypes());
        impObjs.add(new ImportGrouping());
        impObjs.add(new ImportSamples());
        impObjs.add(new ImportTacMan());
        
        
    }
    
    /**
     * Get a list of all formats available for import
     */
    public ArrayList<String> listFormats()
    {
        ArrayList<String> formats = new ArrayList<String>();
        
        // Loop all impObjs to find the combabilityCombination
        for (int i=0;i<impObjs.size();i++)
        {
            formats.add(impObjs.get(i).getFormat());
        }
        return formats;
    }
    
    /**
     * Get a list of all format headers available for import. 
     */
    public ArrayList<FileHeader> getHeaders()
    {
        ArrayList<FileHeader> headers = new ArrayList<FileHeader>();
        
        // Loop all impObjs to find the combabilityCombination
        for (int i=0;i<impObjs.size();i++)
        {
            ArrayList<FileHeader> tmp = impObjs.get(i).getHeaders();
            if (tmp!=null)
                headers.addAll(tmp);
        }
        return headers;
    }
    
    /**
     * List all files associated with a capabilityCombination.
     *
     * A capabilityCombination is the possible combinations of the
     * Capabilities (CREATE,UPDATE,LEVEL, ...)
     */
    public ArrayList<ImportFileStruct> listOfFiles(ArrayList<ImportFileStruct> files, 
                                        String capabilityCombination)
    {
        ArrayList<ImportFileStruct> arr = new ArrayList<ImportFileStruct>();
        
        // Loop all files
        for (int iFiles = 0;iFiles<files.size();iFiles++)
        {
            // Get the format of the file (ex: INDIVIDUAL)
            String format = files.get(iFiles).type; 
                    
            // Loop all impObjs to find the combabilityCombination
            for (int i=0;i<impObjs.size();i++)
            {
                if (impObjs.get(i).getFormat().equals(format))
                {
                    // Formats capable of create, update or create_or_update
                    if (capabilityCombination.equals("C_U_CU"))
                    {
                        // Is this import object capable of handling this info?
                        if (impObjs.get(i).compability("CREATE") &&
                                impObjs.get(i).compability("UPDATE") &&
                                impObjs.get(i).compability("CREATE_OR_UPDATE"))
                        {
                            //arr.add(files.get(iFiles).name);
                            arr.add(new ImportFileStruct(files.get(iFiles)));
                        }
                    }
                    // Formats capable of create, update
                    else if (capabilityCombination.equals("C_U"))
                    {
                        // Is this import object capable of handling this info?
                        if (impObjs.get(i).compability("CREATE") &&
                                impObjs.get(i).compability("UPDATE") &&
                                ! impObjs.get(i).compability("CREATE_OR_UPDATE"))
                        {
                            //arr.add(files.get(iFiles).name);
                            arr.add(new ImportFileStruct(files.get(iFiles)));
                        }
                    }
                    // Formats capable of create, create_or_update
                    else if (capabilityCombination.equals("C_CU"))
                    {
                        // Is this import object capable of handling this info?
                        if (impObjs.get(i).compability("CREATE") &&
                                impObjs.get(i).compability("CREATE_OR_UPDATE") && 
                                ! impObjs.get(i).compability("UPDATE"))
                        {
                            //arr.add(files.get(iFiles).name);
                            arr.add(new ImportFileStruct(files.get(iFiles)));
                        }
                    }
                    // Formats capable of update, create_or_update
                    else if (capabilityCombination.equals("U_CU"))
                    {
                        // Is this import object capable of handling this info?
                        if (impObjs.get(i).compability("UPDATE") &&
                                impObjs.get(i).compability("CREATE_OR_UPDATE") && 
                                ! impObjs.get(i).compability("CREATE"))
                        {
                            //arr.add(files.get(iFiles).name);
                            arr.add(new ImportFileStruct(files.get(iFiles)));
                        }
                    }
                    
                    else if (capabilityCombination.equals("C"))
                    {
                        // Is this import object capable of handling this info?
                        if (impObjs.get(i).compability("CREATE") &&
                                !impObjs.get(i).compability("UPDATE") &&
                                !impObjs.get(i).compability("CREATE_OR_UPDATE"))
                        {
                            //arr.add(files.get(iFiles).name);
                            arr.add(new ImportFileStruct(files.get(iFiles)));
                        }
                    }
                    else if (capabilityCombination.equals("U"))
                    {
                        // Is this import object capable of handling this info?
                        if (impObjs.get(i).compability("UPDATE") &&
                                !impObjs.get(i).compability("CREATE") &&
                                !impObjs.get(i).compability("CREATE_OR_UPDATE"))
                           
                        {
                            //arr.add(files.get(iFiles).name);
                            arr.add(new ImportFileStruct(files.get(iFiles)));
                        }
                    }   
                    else if (capabilityCombination.equals("CU"))
                    {
                        // Is this import object capable of handling this info?
                        if (impObjs.get(i).compability("CREATE_OR_UPDATE") &&
                                !impObjs.get(i).compability("UPDATE") &&
                                !impObjs.get(i).compability("CREATE"))
                        {
                            //arr.add(files.get(iFiles).name);
                            arr.add(new ImportFileStruct(files.get(iFiles)));
                        }
                    }
                        
                    // Only single capabilities. No grouping are allowed.
                    // One to one mapping between capabilityCombination and 
                    // Capability
                    else
                    {
                        // Is this import object capable of handling this info?
                        if (impObjs.get(i).compability(capabilityCombination))
                        {
                            //arr.add(files.get(iFiles).name);
                            arr.add(new ImportFileStruct(files.get(iFiles)));
                        }
                    }                    
                }
            }
        }
        
        return arr;
    }
    
    
    /**
     * Get the index of an object in the dependency list
     */
    private int indexOf(ArrayList<Dependency> sorted, String name)
    {
        for (int i=0;i<sorted.size();i++)
        {
            if (sorted.get(i).name.equals(name))
            {
                return i;
            }
        }
        return -1;
    }
    
    public void sort(ArrayList<Dependency> list) throws DbException
    {
        int i=0;
        
        // Loop limiter, detect circular dependencies
        // If the loop cannot correctly sort the list in 100 loops, the 
        // sorting fails
        int k=0; 
        boolean done = false;
        boolean move = false;
        while (i<list.size())
        {
            move = false;
            Dependency tmp = list.get(i);
            
            
            for (int j=0;j<tmp.dep.length;j++)
            {
                String name = tmp.dep[j];
                int index = indexOf(list,name);

                if (index > i)
                {
                    list.add(i,list.get(index));
                    list.remove(index+1);
                    move=true;
                }
            }
            
            
            if (move==false)
                i++;
            
            k++;     
            
            if (k>100)
                throw new DbException("Circular dependencies. 100 tries to sort list exeeded.");
        }
    }
    
    public ArrayList<Dependency> getDependencies()
    {
        ArrayList<Dependency> list = new ArrayList();
        for (int i=0;i<impObjs.size();i++)
        {
            ArrayList<Dependency> tmp = impObjs.get(i).getDependency();
            
            if (tmp!=null)
                list.addAll(tmp);
        }
        return list;
    }
    
    public ArrayList<ImportFileStruct> sortImportFiles2() throws DbException
    {
        // Read files from database with this isid
        DbImportSet is = new DbImportSet();
        ArrayList<ImportFileStruct> files = is.getImportFiles(conn, Integer.valueOf(isid));
        
        
        //ArrayList<FileHeader> hdrs = getHeaders();
        
        
        // Get the dependency tree of all import modules and sort the list
        ArrayList<Dependency> deps = getDependencies();
        sort(deps);
        
        
        
        files.get(1).hdr.objectTypeName();
        //deps.get(2).name;
                
        throw new DbException("Not complete implementation");
        
        //for (int i=0;i<deps)
        
        
        
        //return files;
    }
   
    /**
     * Get a sorted list of valid files for import. 
     * This will be sorted in the import order of the files for hanling 
     * dependencies between files. 
     *
     * If ifid is null this means that the whole import session should be returned.
     * Otherwise a particular import file will be identified with both ifid and isid
     *
     * @return Returns an ArrayList of ImportFileStruct in order of import
     */    
    private ArrayList sortImportFiles()
    {
        Errors.logInfo("importProcess.sortImportFiles() "+isid+" started");
        String sql;
        ArrayList fileNames = new ArrayList();
        
        ArrayList indNames      = new ArrayList();
        ArrayList grpNames      = new ArrayList();
        ArrayList smpNames      = new ArrayList();
        ArrayList varSetNames   = new ArrayList();
        ArrayList uVarSetNames  = new ArrayList();
        ArrayList uVarNames     = new ArrayList();
        ArrayList varNames      = new ArrayList();
        ArrayList pheNames      = new ArrayList();
        ArrayList marSetNames   = new ArrayList();
        ArrayList uMarkSetNames = new ArrayList();
        ArrayList uMarkNames    = new ArrayList();
        ArrayList marNames      = new ArrayList();
        ArrayList genNames      = new ArrayList();
        ArrayList mapNames      = new ArrayList();
        
        sql = "select name,ifid,import_file,import_type from import_file where isid="+isid;
        try
        {
            Errors.logDebug("sql="+sql);
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery(sql);

            String tmp_name = "";
            String objName = "";
            String tmp_ifid = "";
            //Blob tmp_blob = null;
            DbImportFile dbimpfile = new DbImportFile();
            FileHeader hdr = null;
            String mime = "";
            String filename = "";

            while (rset.next())
            {
                tmp_name = rset.getString("name");
                tmp_ifid = rset.getString("ifid");
                mime = rset.getString("import_type");
                //tmp_blob = rset.getBlob("import_file");
                
                
                // Get the header of the file.
                // 1. Load the header from DB
                // 2. Parse the file header of the file
                // 3. If the parsing generates an exception, then a virtual header 
                //    row are checked by asking all import objects if anyone 
                //    recognizes the file
                
                hdr = dbimpfile.getFileHeader(conn_viss, tmp_ifid);
                //hdr = dbimpfile.getImportFileHeader(tmp_blob);
                
                if (hdr == null)
                {
                    dbimpfile.setStatus(conn_viss,tmp_ifid,"ERROR");
                    dbimpfile.addErrMsg(conn_viss, tmp_ifid, "File type is not importable to the database.");
                    Errors.logWarn("ImportProcess.sortImportFiles(...) "+isid+" hdr==null, No header found in file");
                }                    
                else
                {
                    objName = hdr.objectTypeName();
                    Errors.logDebug("ImportProcess.sortImportFiles(...) "+isid+" objName="+objName);
                    
                    /*
                     * Set status to 0%
                     */
                    dbimpfile.setStatus(conn_viss,tmp_ifid,"0%");

                    if (objName.equals("INDIVIDUAL"))
                        indNames.add(new ImportFileStruct(tmp_name,"INDIVIDUAL",tmp_ifid));
                    if (objName.equals("TACMAN"))
                        indNames.add(new ImportFileStruct(tmp_name,"TACMAN",tmp_ifid));
                    else if (objName.equals("GROUPING"))
                        grpNames.add(new ImportFileStruct(tmp_name,"GROUPING",tmp_ifid));
                    else if (objName.equals("SAMPLE"))
                        smpNames.add(new ImportFileStruct(tmp_name,"SAMPLE",tmp_ifid));
                    else if (objName.equals("VARIABLE"))
                        varNames.add(new ImportFileStruct(tmp_name,"VARIABLE",tmp_ifid));
                    else if (objName.equals("UVARIABLE"))
                        varNames.add(new ImportFileStruct(tmp_name,"UVARIABLE",tmp_ifid));
                    else if (objName.equals("VARIABLESET"))
                        varSetNames.add(new ImportFileStruct(tmp_name,"VARIABLESET",tmp_ifid));
                    else if (objName.equals("UVARIABLESET"))
                        uVarSetNames.add(new ImportFileStruct(tmp_name,"UVARIABLESET",tmp_ifid));
                    else if (objName.equals("PHENOTYPE"))
                        pheNames.add(new ImportFileStruct(tmp_name,"PHENOTYPE",tmp_ifid));
                    else if (objName.equals("MARKER"))
                        marNames.add(new ImportFileStruct(tmp_name,"MARKER",tmp_ifid));
                    else if (objName.equals("UMARKER"))
                        marNames.add(new ImportFileStruct(tmp_name,"UMARKER",tmp_ifid));
                    else if (objName.equals("MARKERSET"))
                        marSetNames.add(new ImportFileStruct(tmp_name,"MARKERSET",tmp_ifid));
                    else if (objName.equals("UMARKERSET"))
                        uMarkSetNames.add(new ImportFileStruct(tmp_name,"UMARKERSET",tmp_ifid));
                    else if (objName.equals("GENOTYPE"))
                        genNames.add(new ImportFileStruct(tmp_name,"GENOTYPE",tmp_ifid));
                    else
                    {
                        dbimpfile.setStatus(conn_viss,tmp_ifid,"ERROR");
                        dbimpfile.addErrMsg(conn_viss, tmp_ifid, "File type is not importable to the database.");
                        Errors.logWarn("File type is not importable to the database. ifid="+tmp_ifid);
                    }
                }
            }
            stmt.close();
            
            fileNames.addAll(indNames);
            fileNames.addAll(grpNames);
            fileNames.addAll(smpNames);
            fileNames.addAll(varNames);
            fileNames.addAll(uVarNames);
            fileNames.addAll(varSetNames);
            fileNames.addAll(uVarSetNames);  
            fileNames.addAll(pheNames);
            fileNames.addAll(marNames);
            fileNames.addAll(marSetNames);
            fileNames.addAll(uMarkNames);
            fileNames.addAll(uMarkSetNames);
            fileNames.addAll(genNames);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Errors.logError("ImportProcess.sortImportFile(...) "+isid+" "+e.getMessage());
        }
        Errors.logInfo("importProcess.sortImportFiles() "+isid+" ended");
        return fileNames;
    }
}
