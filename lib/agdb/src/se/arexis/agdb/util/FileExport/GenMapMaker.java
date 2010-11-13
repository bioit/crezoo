/**
 *
 * MapMaker file generation
 *
 * $Log$
 * Revision 1.1  2005/03/22 12:50:17  heto
 * Working with moving all export files to a new package: se.arexis.agdb.util.FileExport
 *
 * Revision 1.4  2003/12/09 09:19:55  wali
 * fgid added to the output filename.
 *
 * Revision 1.3  2003/05/02 07:58:45  heto
 * Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
 * Modified configuration and source files according to package change.
 *
 * Revision 1.2  2002/12/13 15:00:13  heto
 * Comments added
 * 
 * 
 */




package se.arexis.agdb.util.FileExport;
import java.io.*;
import java.util.*;
import java.sql.*;
import se.arexis.agdb.util.*;

/** MapMaker file export
 * Create the files to export from the database.
 * This class extends the Thread class and is running in a new thread
 * to enable the user to contiunue to work during long data exports.
 *
 */
public class GenMapMaker extends Thread 
{

    /**
     * A class for holding information about individuals.
     * Public access to all variables gives easy access.
     */
    class IndDataRec 
    {
	public int su;
  	public String iid;
	public String identity;
  	public String sex;

  	public IndDataRec () {
	    this.su = 0;
	    this.iid = null;
	    this.identity = null;;
	    this.sex = null;
	}
    }

    class RelDataRec 
    {
	public String sid;
	public String suid;
	public String suname;
	public String fid;
	public String fname;
	public String gql;
	public String filter;

	public RelDataRec () {
	    this.sid = null;;
	    this.suid = null;
	    this.suname = null;;
	    this.fid = null;
	    this.fname = null;
	    this.gql = null;
	    this.filter = null;
	}
    }

    /**
     * A structure to hold informaton about marker data
     */
    class MidDataRec 
    {
	public String cid;
	public String cname;
	public String mid;
	public String mname;
	
	public MidDataRec () {
	    this.cid = null;
	    this.cname = null;
	    this.mid = null;
	    this.mname = null;
	}
    }
    
    /**
     * A structure to hold variable data
     */
    class VidDataRec 
    {
	public String vid;
	public String vname;
	
	public VidDataRec () {
	    this.vid = null;
	    this.vname = null;
	}
    }
    
  // Constructor parameters
  private int fgid = 0;
  private String dataType = null;
  private String aName = null;
  private String bName = null;
  private String directory = null;
  private String DB_URL = null;
  private String DB_UID = null;
  private String DB_PWD = null;
  private String NULL_CHAR = null;

  // File generation parameters
  private String fgname = null;
  private String pid = null;
  private String pname = null;
  private String id = null;
  private String uname = null;
  private String mode = null;
  private String msid = null;
  private String msname = null;
  private String vsid = null;
  private String vsname = null;

  // Vectors
  private Vector rels = null;
  private Vector mids = null;
  private Vector vids = null;
  private Vector inds = null;

  // Files
  private int raw_dfid = 0;
  private FileWriter raw_file = null;
 // private String raw_name = "gen_raw.txt";
  private String raw_name = "";
  
  private int prep_dfid = 0;
  private FileWriter prep_file = null;
  //private String prep_name = "gen_prep.txt";
  private String prep_name = "";
  
  private int log_dfid = 0;
  private FileWriter log_file = null;
 // private String log_name = "log.txt";
  private String log_name = "";
  
   // SQL
  private Connection conn = null;

  private PreparedStatement vstmt = null;
  private PreparedStatement uvstmt = null;
  private PreparedStatement mstmt = null;
  private PreparedStatement umstmt = null;
  private PreparedStatement uastmt = null;
  private PreparedStatement astmt = null;
  private CallableStatement pstmt = null;

  // Constants
  protected static final int MAX_MARKER_LEN = 8;
  protected static final int UPDATE_INTERVAL = 10;
  protected static final int PROGRESS_ERROR = -1;


  //-------------------------------------------------------------------------------
  /** A constructor to initiate the GenMapMaker class.
   * @param fgid FileGenerationID
   * @param dataType 
   * @param aName
   * @param bName
   * @param directory
   * @param dburl The URL to the database (connection string)
   * @param uid The userid to connect to the database
   * @param pwd The password used to connect to the database.
   * @throws SQLException Throws SQLException that needs to be catched
   *
   */
  public GenMapMaker(int fgid,
		     String dataType, String aName, String bName,
		     String directory, String dburl, String uid, String pwd)
	  throws SQLException	
  {
	  this.fgid = fgid;
	  this.dataType = dataType;
	  this.aName = aName;
	  this.bName = bName;
	  this.directory = directory;
	  this.DB_URL = dburl;
	  this.DB_UID = uid;
	  this.DB_PWD = pwd;
         
          // The output file names are extended with the fgid for result import.
          this.raw_name = "gen_raw_" + fgid + ".txt";
          this.prep_name = "gen_prep_" + fgid + ".txt";
          this.log_name = "log_name_" + fgid + ".txt";
          
	  this.pid = null;
	  this.pname = null;
	  this.id = null;
	  this.uname = null;
	  this.mode = null;
	  this.msid = null;
	  this.msname = null;
	  this.vsid = null;
	  this.vsname = null;

	  this.rels = new Vector(1);
	  this.mids = new Vector(100);
	  this.vids = new Vector(100);
	  this.inds = new Vector(1000);

          try
          {
              Class.forName("oracle.jdbc.driver.OracleDriver");
              //DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
              this.conn = DriverManager.getConnection(DB_URL, DB_UID, DB_PWD);
              this.conn.setAutoCommit(false);
          }
          catch (Exception e)
          {}
    }

    //-------------------------------------------------------------------------------
    /**
     * Start the process of generate a MapMaker file
     */
    public void run() {
	int i = 0;
	try {
	    readFileGenerationParameters();
	    readRelationParameters();

	    log_file = createPhysicalFile(directory, log_name);
	    log_dfid = createDataFile(fgid, log_name, id);
	    prep_file = createPhysicalFile(directory, prep_name);
	    prep_dfid = createDataFile(fgid, prep_name, id);
	    raw_file = createPhysicalFile(directory, raw_name);
	    raw_dfid = createDataFile(fgid, raw_name, id);
	    prepareSQL();

	    if (mode.equals("S"))
		singleMode();
	    else
 		multiMode();

	} catch (Exception e) {
	    e.printStackTrace(System.err);
	} finally {
	    closePhysicalFile(log_file);
	    closePhysicalFile(prep_file);
	    closePhysicalFile(raw_file);
	}
    }

    //-------------------------------------------------------------------------------
    /**
     * Single mode operation means builing the datafiles from only one 
     * sampling unit.
     *
     * @param Exception Throws Exception if an error occurs.
     */
    public void singleMode()
	throws Exception {
	
  	int i = 0;
	int j = 0;
	long t0 = System.currentTimeMillis();
	long t1 = 0;
	
  	try {
	    writeLogFileHeader();
	    setProgress(log_dfid, 50, 100);

	    readInds(0);
	    readMids();
	    readVids();

	    writeRawFileHeader();

	    writeLogFileMids();
	    for (i=0; i < mids.size(); i++) {
		writeGenotypes(i);
		
		if ((i % UPDATE_INTERVAL) == 0) {
		    setProgress(raw_dfid, i, mids.size() + vids.size()) ;
		    
		    if (aborted()) {
			writeLogFileError("Generation aborted by user");
			break;
		    }
		}
		yield(); // Give other threads a chance to run
	    }
	    
	    writeLogFileVids();
	    for (i=0; i < vids.size(); i++) {
		writePhenotypes(i);
		
		if ((i % UPDATE_INTERVAL) == 0) {
		    setProgress(raw_dfid, mids.size() + i, mids.size() + vids.size()) ;
		    
		    if (aborted()) {
			writeLogFileError("Generation aborted by user");
			break;
		    }
		}
		yield(); // Give other threads a chance to run
	    }
	    
	    setProgress(raw_dfid, 100, 100);
	    writePrepFile();
	    setProgress(prep_dfid, 100, 100);
	    
	} catch (Exception e) {
	    e.printStackTrace(System.err);
	    setProgress(raw_dfid, PROGRESS_ERROR, 100);
	} finally {
	    t1 = System.currentTimeMillis();
	    writeLogFileFooter((int) (t1-t0)/1000);
	    setProgress(log_dfid, 100, 100);
	}
    }
    
    //-------------------------------------------------------------------------------
    /**
     * Exporting in multimode means that data is collected from more than one 
     * samplingunit at once. 
     *
     * @throws Exception Throws Exception if an error occurs.
     */
    public void multiMode()
	throws Exception {
	
	int su = 0;
	int i = 0;
	int j = 0;
	long t0 = System.currentTimeMillis();
	long t1 = 0;
	
	try {
  	    writeLogFileHeader();
  	    setProgress(log_dfid, 50, 100);

  	    for (su=0; su < rels.size(); su++)
  		readInds(su);
  	    readUMids();
  	    readUVids();

  	    for (su=0; su < rels.size(); su++) {
		checkForMissingMarkerMappings(su);
		checkForMissingVariableMappings(su);
	    }

	    writeRawFileHeader();

	    writeLogFileMids();
	    for (i=0; i < mids.size(); i++) {
		writeUGenotypes(i);
		
		if ((i % UPDATE_INTERVAL) == 0) {
		    setProgress(raw_dfid, i, mids.size() + vids.size()) ;
		    
		    if (aborted()) {
			writeLogFileError("Generation aborted by user");
			break;
		    }
		}
		yield(); // Give other threads a chance to run
	    }
	    
	    writeLogFileVids();
	    for (i=0; i < vids.size(); i++) {
		writeUPhenotypes(i);
		
		if ((i % UPDATE_INTERVAL) == 0) {
		    setProgress(raw_dfid, mids.size() + i, mids.size() + vids.size()) ;
		    
		    if (aborted()) {
			writeLogFileError("Generation aborted by user");
			break;
		    }
		}
		yield(); // Give other threads a chance to run
	    }
	    
	    setProgress(raw_dfid, 100, 100);
	    writePrepFile();
	    setProgress(prep_dfid, 100, 100);
	    
	} catch (Exception e) {
	    e.printStackTrace(System.err);
	    setProgress(raw_dfid, PROGRESS_ERROR, 100);
	} finally {
	    t1 = System.currentTimeMillis();
	    writeLogFileFooter((int) (t1-t0)/1000);
	    setProgress(log_dfid, 100, 100);
	}
    }

    //--------------------------------------------------------------------------------
    /**
     * Creates a new file on the server.
     *
     * @param directory The directory to store the file in. 
     * @param file_name The file name. Must be a valid filename.
     */
    private FileWriter createPhysicalFile(String directory, String file_name)
	throws IOException {
	return  new FileWriter(new File(directory + "/" + file_name));
    }
	
    //--------------------------------------------------------------------------------
    /**
     * Close a file and ignore all errors.
     *
     * @param fw FileWriter object to close.
     */
    private void closePhysicalFile(FileWriter fw) {
	try {
	    if (fw != null) fw.close();
	} catch (IOException ioe) {}
    }
	
    //--------------------------------------------------------------------------------
    /**
     * Store the information about a data file in the database. This 
     * information is shown in the GUI then selecting a "filegeneration".
     *
     * @param fgid FileGenerationID
     * @param name The filename to store in the db? 
     * @param id   
     * @throws SQLException Throws SQLException if an error occurs in the db.
     * @throws IOException Throws IOException.
     * @return  The DataFileID, the id of the file stored in the db.
     */
    private int createDataFile(int fgid, String name, String id)
	throws SQLException, IOException 
    {
	CallableStatement s = null;
	String message = null;
	int dfid = 0;

	try 
        {
	    s = conn.prepareCall("{CALL GDBP.CREATE_DATA_FILE(?,?,?,?,?,?,?)}");
	    s.registerOutParameter(1, java.sql.Types.NUMERIC);
	    s.registerOutParameter(7, java.sql.Types.VARCHAR);
	    s.setNull(1, java.sql.Types.NUMERIC);
	    s.setInt(2, fgid);
	    s.setString(3, name);
	    s.setString(4, "0 %");
	    s.setNull(5, java.sql.Types.VARCHAR);
	    s.setInt(6, Integer.parseInt(id));
	    s.setNull(7, java.sql.Types.VARCHAR);
	    s.execute();
	    message = s.getString(7);
	    dfid = s.getInt(1);
	    s.close();
	    if (message != null && !message.equals(""))
		throw new SQLException(name);

	} catch (SQLException sqle) {
	    writeLogFileError("SQL ERROR [" + message + "]");
	    throw sqle;
	}

	conn.commit();

	return dfid;
    }
	
    //--------------------------------------------------------------------------------
    private void readFileGenerationParameters()
	throws SQLException, IOException {
	Statement s = null;
	ResultSet r = null;
	String q = null;

	try {
	    q = "SELECT FG.NAME FGNAME, P.PID, P.NAME PNAME, U.ID, U.NAME UNAME, FG.MODE_,"
		+ " FG.XMSID, FG.XVSID"
		+ " FROM V_PROJECTS_1 P, V_USERS_1 U, V_FILE_GENERATIONS_1 FG"
		+ " WHERE FG.PID = P.PID AND FG.ID = U.ID AND FGID = " + fgid;
	    s = conn.createStatement();
	    r = s.executeQuery(q);
	
	    if (r.next()) {
		fgname = r.getString("FGNAME");
		pid = r.getString("PID");
		pname = r.getString("PNAME");
		id = r.getString("ID");
		uname = r.getString("UNAME");
		mode = r.getString("MODE_");
		msid = r.getString("XMSID"); if (r.wasNull()) msid = null;
		vsid = r.getString("XVSID"); if (r.wasNull()) vsid = null;
	    } else
		throw new SQLException("No file generation found with fgid = " + fgid);

	} catch (SQLException sqle) {
	    writeLogFileError("SQL ERROR [" + q + "]");
	    throw sqle;
	} finally {
	    if (r != null) r.close();
	    if (s != null) s.close();
	}

	if (msid != null) {
	    try {
		if (mode.equals("S"))
		    q = "SELECT NAME FROM V_MARKER_SETS_1"
			+ " WHERE MSID = " + msid;
		else
		    q = "SELECT NAME FROM V_U_MARKER_SETS_1"
			+ " WHERE UMSID = " + msid;
		
		s = conn.createStatement();
		r = s.executeQuery(q);
	
		if (r.next()) {
		    msname = r.getString("NAME");
		} else
		    throw new SQLException("No (unified) marker set found with (u)msid = " + msid);
		
	    } catch (SQLException sqle) {
		writeLogFileError("SQL ERROR [" + q + "]");
		throw sqle;
	    } finally {
		if (r != null) r.close();
		if (s != null) s.close();
	    }
	}

	if (vsid != null) {
	    try {
		if (mode.equals("S"))
		    q = "SELECT NAME FROM V_VARIABLE_SETS_1"
			+ " WHERE VSID = " + vsid;
		else
		    q = "SELECT NAME FROM V_U_VARIABLE_SETS_1"
			+ " WHERE UVSID = " + vsid;
		
		s = conn.createStatement();
		r = s.executeQuery(q);
	
		if (r.next()) {
		    vsname = r.getString("NAME");
		} else
		    throw new SQLException("No (unified) variable set found with (u)vsid = " + vsid);
		
	    } catch (SQLException sqle) {
		writeLogFileError("SQL ERROR [" + q + "]");
		throw sqle;
	    } finally {
		if (r != null) r.close();
		if (s != null) s.close();
	    }
	}
    }
	
    //-------------------------------------------------------------------------------
    public void readRelationParameters()
	throws SQLException, IOException {
	Statement s = null;
	ResultSet r = null;
	String q = null;
	RelDataRec relData = null;
	GqlTranslator trans = null;

	try {
	    q = "SELECT R.SID, R.SUID, R.NAME SUNAME, R.FID, F.NAME FNAME, R.EXPRESSION"
		+ " FROM V_R_FG_FLT_1 R, V_FILTERS_1 F"
		+ " WHERE R.FID = F.FID AND FGID = " + fgid;
	    s = conn.createStatement();
	    r = s.executeQuery(q);
	    
	    while (r.next()) {
		relData = new RelDataRec();
		relData.sid = r.getString("SID");
		relData.suid = r.getString("SUID");
		relData.suname = r.getString("SUNAME");
		relData.fid = r.getString("FID");
		relData.fname = r.getString("FNAME");
		relData.gql = r.getString("EXPRESSION");
		trans = new GqlTranslator(pid, relData.suid, relData.gql, conn);
		trans.translate();
		relData.filter = trans.getFilter();
		rels.addElement(relData);
	    }

	} catch (SQLException sqle) {
	    writeLogFileError("SQL ERROR [" + q + "]");
	    throw sqle;
	} finally {
	    if (r != null) r.close();
	    if (s != null) s.close();
	}
    }

    //-------------------------------------------------------------------------------
    public void readInds(int su)
	throws SQLException, IOException {
	Statement s = null;
	ResultSet r = null;
	IndDataRec ind = null;
	RelDataRec rel = (RelDataRec) rels.elementAt(su);

	try {
	    s = conn.createStatement();
	    r = s.executeQuery("SELECT "
			       + "ind.IID, "
			       + "ind.IDENTITY, "
			       + "ind.SEX "
			       + rel.filter);

	    while (r.next()) {
		ind = new IndDataRec();
		ind.su = su;
		ind.iid = r.getString("IID");
		ind.identity = r.getString("IDENTITY");
		ind.sex = r.getString("SEX");          
                if (r.wasNull()) ind.sex = null;
		inds.addElement(ind);

		yield(); // Give other threads a chance to run
	    }
	    
	} catch (SQLException sqle) {
	    writeLogFileError("SQL ERROR [" + rel.filter + "]");
	    throw sqle;
	} finally {
	    if (r != null) r.close();
	    if (s != null) s.close();
	}
    }

    //-------------------------------------------------------------------------------
    public void checkForMissingMarkerMappings(int su)
	throws SQLException {
	Statement s = null;
	ResultSet r = null;
	RelDataRec rel = (RelDataRec) rels.elementAt(su);
	int i = 0;

	for (i=0; i < mids.size(); i++) {
	    String umid = ((MidDataRec) mids.elementAt(i)).mid;
	    String umname = ((MidDataRec) mids.elementAt(i)).mname;
	    s = conn.createStatement();
	    r = s.executeQuery("SELECT MID "
			       + " FROM V_R_UMID_MID_1 "
			       + " WHERE SUID = " + rel.suid
			       + "   AND UMID = " + umid);
	    
	    if  (! r.next())
		writeLogFileWarning("No mapping for unified marker " + umname);
	    
	    if (r != null) r.close();
	    if (s != null) s.close();
	}
    }

    //-------------------------------------------------------------------------------
    public void checkForMissingVariableMappings(int su)
	throws SQLException {
	Statement s = null;
	ResultSet r = null;
	RelDataRec rel = (RelDataRec) rels.elementAt(su);
	int i = 0;

	for (i=0; i < vids.size(); i++) {
	    String uvid = ((VidDataRec) vids.elementAt(i)).vid;
	    String uvname = ((VidDataRec) vids.elementAt(i)).vname;
	    s = conn.createStatement();
	    r = s.executeQuery("SELECT VID "
			       + " FROM V_R_UVID_VID_1 "
			       + " WHERE SUID = " + rel.suid
			       + "   AND UVID = " + uvid);
	    
	    if  (! r.next())
		writeLogFileWarning("No mapping for unified variable " + uvname);
	    
	    if (r != null) r.close();
	    if (s != null) s.close();
	}
    }

    //-------------------------------------------------------------------------------
    public void readMids()
	throws SQLException, IOException {
	Statement s = null;
	ResultSet r = null;
	String q = null;
	MidDataRec mid = null;

	if (msid != null)
	    try {
		q = "SELECT CID, CNAME, MID, MNAME"
		    + " FROM V_POSITIONS_2"
		    + " WHERE MSID = " + msid
		    + " ORDER BY CNAME, POSITION, MNAME";
		
		s = conn.createStatement();
		r = s.executeQuery(q);
		while (r.next()) {
		    mid = new MidDataRec();
		    mid.cid = r.getString("CID");
		    mid.cname = r.getString("CNAME");
		    mid.mid = r.getString("MID");
		    mid.mname = r.getString("MNAME");
		    mids.addElement(mid);
		}
		
	    } catch (SQLException sqle) {
		writeLogFileError("SQL ERROR [" + q + "]");
		throw sqle;
	    } finally {
		if (r != null) r.close();
		if (s != null) s.close();
	    }
    }

    //-------------------------------------------------------------------------------
    public void readUMids()
	throws SQLException, IOException {
	Statement s = null;
	ResultSet r = null;
	String q = null;
	MidDataRec mid = null;

	if (msid != null)
	    try {
		q = "SELECT CID, CNAME, UMID, UMNAME"
		    + " FROM V_U_POSITIONS_2"
		    + " WHERE UMSID = " + msid
		    + " ORDER BY CNAME, POSITION, UMNAME";
		
		s = conn.createStatement();
		r = s.executeQuery(q);
		while (r.next()) {
		    mid = new MidDataRec();
		    mid.cid = r.getString("CID");
		    mid.cname = r.getString("CNAME");
		    mid.mid = r.getString("UMID");
		    mid.mname = r.getString("UMNAME");
		    mids.addElement(mid);
		}
		
	    } catch (SQLException sqle) {
		writeLogFileError("SQL ERROR [" + q + "]");
		throw sqle;
	    } finally {
		if (r != null) r.close();
		if (s != null) s.close();
	    }
    }

    //-------------------------------------------------------------------------------
    public void readVids()
	throws SQLException, IOException {
	Statement s = null;
	ResultSet r = null;
	String q = null;
	VidDataRec vid = null;

	if (vsid != null)
	    try {
		q = "SELECT VID, VNAME"
		    + " FROM V_R_VAR_SET_2"
		    + " WHERE VSID=" + vsid
		    + " ORDER BY VNAME";
		s = conn.createStatement();
		r = s.executeQuery(q);
		while (r.next()) {
		    vid = new VidDataRec();
		    vid.vid = r.getString("VID");
		    vid.vname = r.getString("VNAME");
		    vids.addElement(vid);
		}
		
	    } catch (SQLException sqle) {
		writeLogFileError("SQL ERROR [" + q + "]");
		throw sqle;
	    } finally {
		if (r != null) r.close();
		if (s != null) s.close();
	    }
    }

    //-------------------------------------------------------------------------------
    public void readUVids()
	throws SQLException, IOException {
	Statement s = null;
	ResultSet r = null;
	String q = null;
	VidDataRec vid = null;

	if (vsid != null)
	    try {
		q = "SELECT UVID, UVNAME"
		    + " FROM V_R_U_VAR_SET_2"
		    + " WHERE UVSID=" + vsid
		    + " ORDER BY UVNAME";
		s = conn.createStatement();
		r = s.executeQuery(q);
		while (r.next()) {
		    vid = new VidDataRec();
		    vid.vid = r.getString("UVID");
		    vid.vname = r.getString("UVNAME");
		    vids.addElement(vid);
		}
		
	    } catch (SQLException sqle) {
		writeLogFileError("SQL ERROR [" + q + "]");
		throw sqle;
	    } finally {
		if (r != null) r.close();
		if (s != null) s.close();
	    }
    }

    //-------------------------------------------------------------------------------
    private void writeGenotypes(int midnr)
  	throws SQLException, IOException {
	ResultSet rset = null;
	MidDataRec md = (MidDataRec) mids.elementAt(midnr);
	String a1name = null;
	String a2name = null;
	int i = 0;
	int acount = 0;
	int bcount = 0;

	try {
	    raw_file.write("*" + truncIf2Long(md.mname, true));

	    for (i=0; i<inds.size(); i++) {
		IndDataRec ind = (IndDataRec) inds.elementAt(i);
		acount = 0;
		bcount = 0;
		mstmt.clearParameters();
		mstmt.setInt(1, Integer.parseInt(md.mid));
		mstmt.setInt(2, Integer.parseInt(ind.iid));
		rset = mstmt.executeQuery();
		if (rset.next()) {
		    a1name = rset.getString("A1NAME");
		    a2name = rset.getString("A2NAME");
		    if (aName.equals(a1name)) acount++;
		    if (aName.equals(a2name)) acount++;
		    if (bName.equals(a1name)) bcount++;
		    if (bName.equals(a2name)) bcount++;
		}
		else {
		    a1name = null;
		    a2name = null;
		}
		if (acount == 2 && bcount == 0)
		    raw_file.write("\ta");
		else if (acount == 0 && bcount == 2)
		    raw_file.write("\tb");
		else if (acount == 1 && bcount == 1)
		    raw_file.write("\th");
		else {
		    raw_file.write("\t-");
		    if (a1name != null)
			if (!a1name.equals(aName) && !a1name.equals(bName))
			    writeLogFileError("Read illegal allele (" + a1name + ") for individual "
					      + ind.identity + " and marker " + md.mname);
		    if (a2name != null) 
			if (!a2name.equals(aName) && !a2name.equals(bName))
			    writeLogFileError("Read illegal allele (" + a2name + ") for individual "
					      + ind.identity + " and marker " + md.mname);
		}
	    }
	    
	    raw_file.write("\n");
	    
	} catch (SQLException sqle) {
	    writeLogFileError("writeGenotypes: Fatal SQL Error");
	    throw sqle;
	} catch (IOException ioe) {
	    writeLogFileError("writeGenotypes: Fatal IO Error");
	    ioe.printStackTrace(System.err);
	    throw ioe;
	}
    }
    
    //-------------------------------------------------------------------------------
    private void writeUGenotypes(int midnr)
	throws SQLException, IOException {
	ResultSet rset = null;
	MidDataRec md = (MidDataRec) mids.elementAt(midnr);
	String a1name = null;
	String a2name = null;
	int i = 0;
	int acount = 0;
	int bcount = 0;

	try {
	    raw_file.write("*" + truncIf2Long(md.mname, true));

	    for (i=0; i<inds.size(); i++) {
		IndDataRec ind = (IndDataRec) inds.elementAt(i);
		acount = 0;
		bcount = 0;
		umstmt.clearParameters();
		umstmt.setInt(1, Integer.parseInt(md.mid));
		umstmt.setInt(2, Integer.parseInt(ind.iid));
		rset = umstmt.executeQuery();
		if (rset.next()) {
		    a1name = translateAllele(midnr, rset.getString("AID1"), ind.identity);
		    a2name = translateAllele(midnr, rset.getString("AID2"), ind.identity);
		    if (aName.equals(a1name)) acount++;
		    if (aName.equals(a2name)) acount++;
		    if (bName.equals(a1name)) bcount++;
		    if (bName.equals(a2name)) bcount++;
		}
		else {
		    a1name = null;
		    a2name = null;
		}
		if (acount == 2 && bcount == 0)
		    raw_file.write("\ta");
		else if (acount == 0 && bcount == 2)
		    raw_file.write("\tb");
		else if (acount == 1 && bcount == 1)
		    raw_file.write("\th");
		else {
		    raw_file.write("\t-");
		    if (a1name != null)
			if (!a1name.equals(aName) && !a1name.equals(bName))
			    writeLogFileError("Read illegal allele (" + a1name + ") for individual "
					      + ind.identity + " and marker " + md.mname);
		    if (a2name != null) 
			if (!a2name.equals(aName) && !a2name.equals(bName))
			    writeLogFileError("Read illegal allele (" + a2name + ") for individual "
					      + ind.identity + " and marker " + md.mname);
		}
	    }
	    
	    raw_file.write("\n");
	    
	} catch (SQLException sqle) {
	    writeLogFileError("writeUGenotypes: Fatal SQL Error");
	    throw sqle;
	} catch (IOException ioe) {
	    writeLogFileError("writeUGenotypes: Fatal IO Error");
	    ioe.printStackTrace(System.err);			  
	    throw ioe;
	}
    }

    //-------------------------------------------------------------------------------
    private void writePhenotypes(int vidnr)
  	throws SQLException, IOException {
	ResultSet rset = null;
	VidDataRec vd = (VidDataRec) vids.elementAt(vidnr);
	String val = null;
	int i = 0;

	try {
	    raw_file.write("*" + vd.vname);

	    for (i=0; i<inds.size(); i++) {
		IndDataRec ind = (IndDataRec) inds.elementAt(i);
		vstmt.clearParameters();
		vstmt.setInt(1, Integer.parseInt(vd.vid));
		vstmt.setInt(2, Integer.parseInt(ind.iid));
		rset = vstmt.executeQuery();
		if (rset.next())
		    val = rset.getString("VALUE");
		else
		    val = null;
		if (val != null)
		    raw_file.write("\t" + val);
		else
		    raw_file.write("\t-");
	    }

	    raw_file.write("\n");

  	} catch (SQLException sqle) {
	    writeLogFileError("writePhenotypes: Fatal SQL Error");
	    throw sqle;
	} catch (IOException ioe) {
	    writeLogFileError("writePhenotypes: Fatal IO Error");
	    ioe.printStackTrace(System.err);
	    throw ioe;
	}
    }

    //-------------------------------------------------------------------------------
    private void writeUPhenotypes(int vidnr)
  	throws SQLException, IOException {
	ResultSet rset = null;
	VidDataRec vd = (VidDataRec) vids.elementAt(vidnr);
	String val = null;
	int i = 0;

	try {
	    raw_file.write("*" + vd.vname);

	    for (i=0; i<inds.size(); i++) {
		IndDataRec ind = (IndDataRec) inds.elementAt(i);
		uvstmt.clearParameters();
		uvstmt.setInt(1, Integer.parseInt(vd.vid));
		uvstmt.setInt(2, Integer.parseInt(ind.iid));
		rset = uvstmt.executeQuery();
		if (rset.next())
		    val = rset.getString("VALUE");
		else
		    val = null;
		if (val != null)
		    raw_file.write("\t" + val);
		else
		    raw_file.write("\t-");
	    }

	    raw_file.write("\n");

  	} catch (SQLException sqle) {
	    writeLogFileError("writeUPhenotypes: Fatal SQL Error");
	    throw sqle;
	} catch (IOException ioe) {
	    writeLogFileError("writeUPhenotypes: Fatal IO Error");
	    ioe.printStackTrace(System.err);
	    throw ioe;
	}
    }

    //-------------------------------------------------------------------------------
    private String translateAllele(int midnr, String aid, String identity)
	throws SQLException, IOException {
	ResultSet rset = null;
	String name = null;
	String umid = ((MidDataRec) mids.elementAt(midnr)).mid;
	String umname = ((MidDataRec) mids.elementAt(midnr)).mname;

	if (aid == null)
	    return aid;
	else {
	    uastmt.clearParameters();
	    uastmt.setInt(1, Integer.parseInt(umid));
	    uastmt.setInt(2, Integer.parseInt(aid));
	    rset = uastmt.executeQuery();
	    if (rset.next())
		name = rset.getString("NAME");
	    else
		writeTranslationError(umname, aid, identity);

	    return name;
	}
    }

    //-------------------------------------------------------------------------------
    private void writeTranslationError(String umname, String aid, String identity) {
	Statement s = null;
	ResultSet r = null;
	String q = null;
	String mname = null;
	String aname = null;

	try {
	    q = "SELECT MNAME, NAME"
		+ " FROM V_ALLELES_3"
		+ " WHERE AID = " + aid;
	    s = conn.createStatement();
	    r = s.executeQuery(q);
	
	    if (r.next()) {
		mname = r.getString("MNAME");
		aname = r.getString("NAME");
	    }

	    if (r != null) r.close();
	    if (s != null) s.close();

	} catch (Exception e) {
	    e.printStackTrace(System.err);
	}

	writeLogFileError("Individual " + identity
			  + ", no mapping for allele " + aname
			  + " (marker: " + mname 
			  + ", unified marker: " + umname + ")");
    }

    //-------------------------------------------------------------------------------
    private void writeLogFileHeader()
	throws IOException {
	try {
	    log_file.write("#\n");
	    log_file.write("# File generation log file\n");
	    log_file.write("#\n");
	    log_file.write("Project:    " + pname + "\n");
	    log_file.write("User:       " + uname + "\n");
	    log_file.write("Generation: " + fgname + "\n");
	    log_file.write("Format:     MapMaker, version 3.0\n");
	    if (mode.equals("S")) {
		log_file.write("Mode:       Single\n");
		log_file.write("Marker Set:   " + msname + "\n");
		log_file.write("Variable Set: " + vsname + "\n");
	    }
	    else {
		log_file.write("Mode:       Multi\n");
		log_file.write("Unified Marker Set:   " + msname + "\n");
		log_file.write("Unified Variable Set: " + vsname + "\n");
	    }
	} catch (IOException e) {
	    e.printStackTrace(System.err);
	    throw e;
	}
    }

    //-------------------------------------------------------------------------------
    private void writeLogFileMids()
	throws IOException {
	try {
	    log_file.write("---------------------------------------------\n");
	    log_file.write("Writing genotypes...\n");
	} catch (IOException e) {
	    e.printStackTrace(System.err);
	    throw e;
	}
    }

    //-------------------------------------------------------------------------------
    private void writeLogFileVids()
	throws IOException {
	try {
	    log_file.write("---------------------------------------------\n");
	    log_file.write("Writing phenotypes...\n");
	} catch (IOException e) {
	    e.printStackTrace(System.err);
	    throw e;
	}
    }

    //-------------------------------------------------------------------------------
    private void writeLogFileFooter(int secs) {
	try {
	    log_file.write("---------------------------------------------\n");
	    log_file.write("Total generation time:\t" + secs + " seconds.\n");
	} catch (IOException e) {
	    e.printStackTrace(System.err);
	}
    }

    //-------------------------------------------------------------------------------
    public void writeLogFileWarning(String message) {
	try {
	    log_file.write("WARNING: " + message + "\n");
	} catch (IOException e) {
	    e.printStackTrace(System.err);
	}
    }
		
    //-------------------------------------------------------------------------------
    public void writeLogFileError(String message) {
	try {
	    log_file.write("ERROR: " + message + "\n");
	} catch (IOException e) {
	    e.printStackTrace(System.err);
      System.err.println("message='" + message + "'");
	}
    }
		
    //-------------------------------------------------------------------------------
    private void writeRawFileHeader()
	throws IOException {

	raw_file.write("Data type " + dataType + "\n");
	raw_file.write(inds.size() + " " + mids.size() + " " + vids.size() + "\n");
    }

    //-------------------------------------------------------------------------------
    private void writePrepFile()
	throws IOException {
	int i;
	int j;
	String pcn = null;
	Vector chrs = new Vector(25);

	//Collect chromosomes...
	for (i=0; i<mids.size(); i++) {
	    MidDataRec md = (MidDataRec) mids.elementAt(i);
	    if (!md.cname.equals(pcn)) {
		chrs.addElement(md.cname);
		pcn = md.cname;
	    }
	}

	prep_file.write("print names on\n");
	prep_file.write("er de on\n");
	prep_file.write("cent func kosambi\n");
	prep_file.write("units cm\n");
	
	prep_file.write("\n");
	for (i=0; i<chrs.size(); i++)
	    prep_file.write("make chromosome " + ((String) chrs.elementAt(i)) + "\n");

	pcn = null;
	for (i=0; i<chrs.size(); i++) {
	    String cname = (String) chrs.elementAt(i);
	    prep_file.write("\n");
	    prep_file.write("sequence");
	    for (j=0; j<mids.size(); j++) {
		MidDataRec md = (MidDataRec) mids.elementAt(j);
		if (md.cname.equals(cname))
		    prep_file.write(" " + truncIf2Long(md.mname, false));
	    }
	    prep_file.write("\n");
	    prep_file.write("attach " + cname + "\n");
	    prep_file.write("framework " + cname + "\n");
	}

	prep_file.write("\n");
	prep_file.write("save\n");
    }

    //-------------------------------------------------------------------------------
    private String truncIf2Long(String mname, boolean log)
	throws IOException {

	if (mname.length() <= MAX_MARKER_LEN)
	    return mname;
	else {
	    if (log) {
		writeLogFileWarning("Marker " + mname + " truncated to "
				    + mname.substring(0, MAX_MARKER_LEN-1));
	    }
	    return mname.substring(0, MAX_MARKER_LEN-1);
	}
    }
    
    //-------------------------------------------------------------------------------
    private void prepareSQL()
	throws SQLException {

	mstmt = conn.prepareStatement("SELECT G.A1NAME, G.A2NAME"
				      + " FROM V_GENOTYPES_3 G"
				      + " WHERE G.MID=?"
				      + "   AND G.IID=?");

	umstmt = conn.prepareStatement("SELECT G.AID1, G.AID2"
				       + " FROM V_R_UMID_MID_1 R, V_GENOTYPES_3 G"
				       + " WHERE R.UMID=?"
				       + "   AND R.MID=G.MID"
				       + "   AND G.IID=?");

  	vstmt = conn.prepareStatement("SELECT P.VALUE"
				      + " FROM V_PHENOTYPES_1 P"
  				      + " WHERE P.VID=?"
  				      + "   AND P.IID=?");

  	uvstmt = conn.prepareStatement("SELECT P.VALUE"
  				       + " FROM V_R_UVID_VID_1 R, V_PHENOTYPES_1 P"
  				       + " WHERE R.UVID=?"
  				       + "   AND R.VID=P.VID"
  				       + "   AND P.IID=?");

	uastmt = conn.prepareStatement("SELECT A.NAME"
				       + " FROM V_R_UAID_AID_1 M, V_U_ALLELES_1 A"
				       + " WHERE M.UMID=?"
				       + "   AND M.AID=?"
				       + "   AND M.UAID=A.UAID");

	astmt = conn.prepareStatement("SELECT ABORT_"
				      + " FROM V_FILE_GENERATIONS_1"
				      + " WHERE FGID = " + fgid);

	pstmt = conn.prepareCall("{CALL GDBP.SET_DATA_FILE_STATUS(?,?,?)}");
    }

    //-------------------------------------------------------------------------------
    private void setProgress(int dfid, int n, int t) 
	throws SQLException, IOException {
	String message = null;
	String progress = null;

	if (n == PROGRESS_ERROR)
	    progress = "ERROR";
	else if (n == t)
	    progress = "DONE";
	else 
	    progress = (n*100/t) + " %";

	try {
	    pstmt.clearParameters();
	    pstmt.registerOutParameter(3, java.sql.Types.VARCHAR);
	    pstmt.setInt(1, dfid);
	    pstmt.setString(2, progress);
	    pstmt.setNull(3, java.sql.Types.VARCHAR);
	    pstmt.execute();
	    message = pstmt.getString(3);

	    if (message != null && !message.equals(""))
		throw new SQLException("setProgress");

	} catch (SQLException sqle) {
	    log_file.write("setProgress [" + message + "]\n");
	    throw sqle;
	}

	conn.commit();
    }

    //-------------------------------------------------------------------------------
    public boolean aborted() 
	throws SQLException {
	ResultSet rset = null;
	boolean a = false;

	astmt.clearParameters();
	rset = astmt.executeQuery();
	rset.next();

	a = (rset.getInt("ABORT_") != 0);
	rset.close();

	return a;
    }

}
