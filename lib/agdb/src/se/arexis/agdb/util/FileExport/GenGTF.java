

package se.arexis.agdb.util.FileExport;
import java.io.*;
import java.util.*;
import java.sql.*;

import se.arexis.agdb.db.DbDataFile;
import se.arexis.agdb.db.DbException;
import se.arexis.agdb.util.*;

public class GenGTF extends Thread {

    class IndDataRec {

	public String iid;
	public String identity;
	public String alias;
	public String father;
	public String mother;
	public String sex;
	public String birth_date;

	public IndDataRec () {
	    this.iid = null;
	    this.identity = null;;
	    this.alias = null;;
	    this.father = null;
	    this.mother = null;
	    this.sex = null;
	    this.birth_date = null;
	}
    }

    class RelDataRec {

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

    class MidDataRec {

	public String mid;
	public String mname;

	public MidDataRec () {
	    this.mid = null;;
	    this.mname = null;
	}
    }

    class VidDataRec {

	public String vid;
	public String vname;

	public VidDataRec () {
	    this.vid = null;;
	    this.vname = null;
	}
    }

    // Constructor parameters
    private static int fgid = 0;
    private static String directory = null;
    private static String DB_URL = null;
    private static String DB_UID = null;
    private static String DB_PWD = null;
    private static String NULL_CHAR = null;
    private static int includedFields = 0;
    private static boolean includeRef = true;
    private static boolean includeRaw = true;


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
    private Vector inds = null;
    private Vector mids = null;
    private Vector vids = null;

    // Files
    private int data_dfid = 0;
    private int log_dfid = 0;

    private FileWriter data_file = null;
    private FileWriter log_file = null;

   // private String data_name = "data.txt";
   // private String log_name = "log.txt";
   private String data_name = "";
   private String log_name = "";
    
    // SQL
    private Connection conn = null;
    
    // This should be used to commit changes visible to the user.
    private Connection conn_viss = null;
    

    private PreparedStatement mstmt = null;
    private PreparedStatement umstmt = null;
    private PreparedStatement uastmt = null;
    private PreparedStatement vstmt = null;
    private PreparedStatement uvstmt = null;
    private PreparedStatement astmt = null;

    // Constants
    public final static int SAMPLING_UNIT = 1;
    public final static int IDENTITY = 2;
    public final static int ALIAS = 4;
    public final static int SEX = 8;
    public final static int BIRTH_DATE = 16;
    public final static int FATHER = 32;
    public final static int MOTHER = 64;

    private static int UPDATE_INTERVAL = 10;
    private static int PROGRESS_ERROR = -1;

    //-------------------------------------------------------------------------------
    public GenGTF(int fgid, String directory,
		  String driver, String dburl, String uid, String pwd,
		  String null_char, int fieldMask, boolean incRef, boolean incRaw)
	throws SQLException	{

	this.fgid = fgid;
	this.directory = directory;
	this.DB_URL = dburl;
	this.DB_UID = uid;
	this.DB_PWD = pwd;
	this.NULL_CHAR = null_char;
	this.includedFields = fieldMask;
  this.includeRef = incRef;
	this.includeRaw = incRaw;
        
        // The output file names are extended with the fgid for result import.
        this.data_name = "data_" + fgid + ".txt";
        this.log_name = "log_" + fgid + ".txt";
        
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
	this.inds = new Vector(1000);
	this.mids = new Vector(100);
	this.vids = new Vector(100);

        try
        {
            //DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            //Class.forName("oracle.jdbc.driver.OracleDriver");
            Class.forName(driver);
            this.conn = DriverManager.getConnection(DB_URL, DB_UID, DB_PWD);
            //this.conn.setAutoCommit(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //-------------------------------------------------------------------------------
    public void run() {
	try 
        {
            Errors.logDebug("GenGTF.run() started");
            DbDataFile df = new DbDataFile();
            
	    readFileGenerationParameters();
	    readRelationParameters();
	    log_file  = createPhysicalFile(directory, log_name);
	    data_file = createPhysicalFile(directory, data_name);
	    log_dfid  = df.createDataFile(conn, fgid, log_name, id);
	    data_dfid = df.createDataFile(conn, fgid, data_name, id);
	    prepareSQL();

	    if (mode.equals("S"))
		singleMode();
	    else
		multiMode();

	} catch (Exception e) {
	    e.printStackTrace(System.err);
	} finally {
	    closePhysicalFile(log_file);
	    closePhysicalFile(data_file);
	}
        Errors.logDebug("GenGTF.run() ended");
    }

    //-------------------------------------------------------------------------------
    public void singleMode()
	throws Exception 
    {
        Errors.logDebug("GenGTF.singleMode() started");
	int i = 0;
	long t0 = System.currentTimeMillis();
	long t1 = 0;

	try {
	    writeLogFileHeader();
	    setProgress(log_dfid, 0, 1, 50, 100);
	    readMids();
	    readVids();
	    writeDataFileHeader();

	    writeLogFileSU(0);
	    readInds(0);
            
            Errors.logDebug("inds.size()="+inds.size());

	    for (i=0; i < inds.size(); i++) 
            {
		checkForMissingParents(i);
		writeIndData(0, i);
		writeGenotypes(i);
		writePhenotypes(i);
		data_file.write("\n");
		if ((i % UPDATE_INTERVAL) == 0) {
		    setProgress(data_dfid, 0, 1, i, inds.size());
		    if (aborted()) {
			writeLogFileError("Generation aborted by user");
			break;
		    }
		}
		yield(); // Give other threads a chance to run
	    }
	    
	    setProgress(data_dfid, 0, 1, 100, 100);

	} catch (Exception e) {
	    e.printStackTrace(System.err);
	    setProgress(data_dfid, 0, 1, PROGRESS_ERROR, 100);
	} finally {
	    t1 = System.currentTimeMillis();
	    writeLogFileFooter((int) (t1-t0)/1000);
	    setProgress(log_dfid, 0, 1, 100, 100);
	}
        Errors.logDebug("GenGTF.singleMode() ended");
    }

    //-------------------------------------------------------------------------------
    public void multiMode()
	throws Exception {

	int su = 0;
	int i = 0;
	long t0 = System.currentTimeMillis();
	long t1 = 0;

	try {
	    writeLogFileHeader();
	    setProgress(log_dfid, 0, 1, 50, 100);
	    readUMids();
	    readUVids();
	    writeDataFileHeader();

	    for (su=0; su < rels.size(); su++) {
		writeLogFileSU(su);
		checkForMissingMarkerMappings(su);
		checkForMissingVariableMappings(su);
		readInds(su);

		for (i=0; i < inds.size(); i++) {
		    checkForMissingParents(i);
		    writeIndData(su, i);
		    writeUGenotypes(su, i);
		    writeUPhenotypes(su, i);
		    data_file.write("\n");
		    if ((i % UPDATE_INTERVAL) == 0) { 
			setProgress(data_dfid, su, rels.size(), i, inds.size());
			if (aborted()) {
			    writeLogFileError("Generation aborted by user");
			    break;
			}
		    }
		    yield(); // Give other threads a chance to run
		}
	    }
	    
	    setProgress(data_dfid, 0, 1, 100, 100);

	} catch (Exception e) {
	    e.printStackTrace(System.err);
	    setProgress(data_dfid, 0, 1, PROGRESS_ERROR, 100);
	} finally {
	    t1 = System.currentTimeMillis();
	    writeLogFileFooter((int) (t1-t0)/1000);
	    setProgress(log_dfid, 0, 1, 100, 100);
	}
    }

    //--------------------------------------------------------------------------------
    private FileWriter createPhysicalFile(String directory, String file_name)
	throws IOException {
	return  new FileWriter(new File(directory + "/" + file_name));
    }
	
    //--------------------------------------------------------------------------------
    private void closePhysicalFile(FileWriter fw) {
	try {
	    if (fw != null) fw.close();
	} catch (IOException ioe) {}
    }
	
   
	
    //--------------------------------------------------------------------------------
    private void readFileGenerationParameters()
	throws SQLException, IOException {
	Statement s = null;
	ResultSet r = null;
	String q = null;

	try {
	    q = "SELECT FG.NAME as FGNAME, P.PID, P.NAME as PNAME, U.ID, U.NAME as UNAME, FG.MODE_,"
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

	} 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace();
            Errors.logError("SQL Error="+q);
            
	    writeLogFileError("SQL ERROR [" + q + "]");
            throw sqle;
	} 
        finally 
        {
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
                sqle.printStackTrace();
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
                sqle.printStackTrace();
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
	    q = "SELECT R.SID, R.SUID, R.NAME as SUNAME, R.FID, F.NAME as FNAME, R.EXPRESSION"
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
		relData.gql = r.getString("EXPRESSION").replace("\"","'");
		trans = new GqlTranslator(pid, relData.suid, relData.gql, conn);
		trans.translate();
		relData.filter = trans.getFilter();
		rels.addElement(relData);
	    }

	} catch (SQLException sqle) {
            sqle.printStackTrace();
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
	    r = s.executeQuery("SELECT ind.IID, "
			       + "ind.IDENTITY, "
			       + "ind.ALIAS, " 
			       + "ind.FIDENTITY, "
			       + "ind.MIDENTITY, "
			       + "ind.SEX, "
			       + "to_char(ind.BIRTH_DATE, 'YYYY-MM-DD') as BD "
			       + rel.filter);

	    inds.removeAllElements();

	    while (r.next()) {
		ind = new IndDataRec();
		ind.iid = r.getString("IID");
		ind.identity = r.getString("IDENTITY");
		ind.alias = r.getString("ALIAS");      if (r.wasNull()) ind.alias = null;
		ind.father = r.getString("FIDENTITY"); if (r.wasNull()) ind.father = null;
		ind.mother = r.getString("MIDENTITY"); if (r.wasNull()) ind.mother = null;
		ind.sex = r.getString("SEX");          if (r.wasNull()) ind.sex = null;
		ind.birth_date = r.getString("BD");    if (r.wasNull()) ind.birth_date = null;
		inds.addElement(ind);

		yield(); // Give other threads a chance to run
	    }
	    
	} 
        catch (SQLException sqle) 
        {
            sqle.printStackTrace();
	    writeLogFileError("SQL ERROR [" + rel.filter + "]");
	    throw sqle;
	} finally {
	    if (r != null) r.close();
	    if (s != null) s.close();
	}
    }

    //-------------------------------------------------------------------------------
    public boolean indExist(String identity) {
	int i = 0;

	while (i < inds.size()) {
	    IndDataRec ind = (IndDataRec) inds.elementAt(i);
	    if (identity.equals(ind.identity))
		return true;
	    i++;
	}

	return false;
    }

    //-------------------------------------------------------------------------------
    public void checkForMissingParents(int indnr) {
	IndDataRec ind = (IndDataRec) inds.elementAt(indnr);
	
	if ((ind.father != null) && ((includedFields & FATHER) == FATHER)) {
	    if (!indExist(ind.father)) {
		ind.father = null;
		writeLogFileWarning("Father for " + ind.identity
				    + " not selected by filter");
	    }
	}

	if ((ind.mother != null) && ((includedFields & MOTHER) == MOTHER)) {
	    if (!indExist(ind.mother)) {
		ind.mother = null;
		writeLogFileWarning("Mother for " + ind.identity
				    + " not selected by filter");
	    }
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
		q = "SELECT MID, MNAME"
		    + " FROM V_POSITIONS_2"
		    + " WHERE MSID = " + msid
		    + " ORDER BY CNAME, POSITION, MNAME";

		s = conn.createStatement();
		r = s.executeQuery(q);
		while (r.next()) {
		    mid = new MidDataRec();
		    mid.mid = r.getString("MID");
		    mid.mname = r.getString("MNAME");
		    mids.addElement(mid);
		}

	    } catch (SQLException sqle) 
            {
                sqle.printStackTrace();
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
		q = "SELECT UMID, UMNAME"
		    + " FROM V_U_POSITIONS_2"
		    + " WHERE UMSID = " + msid
		    + " ORDER BY CNAME, POSITION, UMNAME";

		s = conn.createStatement();
		r = s.executeQuery(q);
		while (r.next()) {
		    mid = new MidDataRec();
		    mid.mid = r.getString("UMID");
		    mid.mname = r.getString("UMNAME");
		    mids.addElement(mid);
		}

	    } catch (SQLException sqle) {
                sqle.printStackTrace();
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
                sqle.printStackTrace();
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
                sqle.printStackTrace();
		writeLogFileError("SQL ERROR [" + q + "]");
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
    private void writeDataFileHeader()
	throws IOException	{
	StringBuffer header = new StringBuffer();
	if ((includedFields & SAMPLING_UNIT) == SAMPLING_UNIT) {
	    header.append("SU.NAME");
	    if (includedFields > SAMPLING_UNIT)
		header.append("\t");
	}
	if ((includedFields & IDENTITY) == IDENTITY) {
	    header.append("IDENTITY");
	    if (includedFields > IDENTITY)
		header.append("\t");
	}
	if ((includedFields & ALIAS) == ALIAS) {
	    header.append("ALIAS");
	    if (includedFields > ALIAS)
		header.append("\t");
	}
	if ((includedFields & SEX) == SEX) {
	    header.append("SEX");
	    if (includedFields > SEX)
		header.append("\t");
	}
	if ((includedFields & BIRTH_DATE) == BIRTH_DATE) {
	    header.append("BIRTH_DATE");
	    if (includedFields > BIRTH_DATE)
		header.append("\t");
	}
	if ((includedFields & FATHER) == FATHER) {
	    header.append("FATHER");
	    if (includedFields > FATHER)
		header.append("\t");
	}
	if ((includedFields & MOTHER) == MOTHER)
	    header.append("MOTHER");

	data_file.write(header.toString());
	writeMarkers();
	writeVariables();
	data_file.write("\n");
    }

    //-------------------------------------------------------------------------------
    private void writeMarkers()
	throws IOException {

	int i = 0;
	while (i < mids.size()) {
	    MidDataRec mid = (MidDataRec) mids.elementAt(i);

		    data_file.write("\t" + mid.mname + ".A1\t" + mid.mname + ".A2");

	     if (includeRaw)
		       data_file.write("\t" +  mid.mname + ".R1\t" + mid.mname + ".R2");
       if (includeRef)
		       data_file.write("\t" + "REFERENCE");

	      i++;
      }

    }

    //-------------------------------------------------------------------------------
    private void writeVariables()
	throws IOException {

	int i = 0;
	while (i < vids.size()) {
	    VidDataRec vid = (VidDataRec) vids.elementAt(i);
	    data_file.write("\t" + vid.vname);
	    i++;
	}
    }

    //-------------------------------------------------------------------------------
    private void writeIndData(int su, int i)
	throws IOException {
	RelDataRec rel = (RelDataRec) rels.elementAt(su);
	IndDataRec ind = (IndDataRec) inds.elementAt(i);
	StringBuffer line = new StringBuffer(512);

	if ((includedFields & SAMPLING_UNIT) == SAMPLING_UNIT) {
	    line.append(replaceIfNull(rel.suname));
	    if (includedFields > SAMPLING_UNIT)
		line.append("\t");
	}
	if ((includedFields & IDENTITY) == IDENTITY) {
	    line.append(replaceIfNull(ind.identity));
	    if (includedFields > IDENTITY)
		line.append("\t");
	}
	if ((includedFields & ALIAS) == ALIAS) {
	    line.append(replaceIfNull(ind.alias));
	    if (includedFields > ALIAS)
		line.append("\t");
	}
	if ((includedFields & SEX) == SEX) {
	    line.append(replaceIfNull(ind.sex));
	    if (includedFields > SEX)
		line.append("\t");
	}
	if ((includedFields & BIRTH_DATE) == BIRTH_DATE) {
	    line.append(replaceIfNull(ind.birth_date));
	    if (includedFields > BIRTH_DATE)
		line.append("\t");
	}
	if ((includedFields & FATHER) == FATHER) {
	    line.append(replaceIfNull(ind.father));
	    if (includedFields > FATHER)
		line.append("\t");
	}
	if ((includedFields & MOTHER) == MOTHER) {
	    line.append(replaceIfNull(ind.mother));
	}

	data_file.write(line.toString() );
    }

    //-------------------------------------------------------------------------------
    private void writeGenotypes(int indnr)
	throws SQLException, IOException {
	ResultSet rset = null;
	IndDataRec ind = (IndDataRec) inds.elementAt(indnr);
	String a1name = null;
	String a2name = null;
	String raw1 = null;
	String raw2 = null;
  String ref =null;
	int i = 0;
	try {
	    mstmt.clearParameters();
	    mstmt.setInt(1, Integer.parseInt(ind.iid));
	    rset = mstmt.executeQuery();
	    while (rset.next()) {
		String mid = rset.getString("MID");
		while (! mid.equals(((MidDataRec) mids.elementAt(i)).mid)) {
			data_file.write("\t" + NULL_CHAR + "\t" + NULL_CHAR);

		    if (includeRaw)
			data_file.write("\t" + NULL_CHAR + "\t" + NULL_CHAR);
      if (includeRef)
      data_file.write("\t" + NULL_CHAR);
		    i++;
		}
		a1name = replaceIfNull(rset.getString("A1NAME"));
		a2name = replaceIfNull(rset.getString("A2NAME"));
		raw1 = replaceIfNull(rset.getString("RAW1"));
		raw2 = replaceIfNull(rset.getString("RAW2"));
    ref = replaceIfNull(rset.getString("REFERENCE"));

		    data_file.write("\t" + a1name + "\t" + a2name);

		if (includeRaw)
		    data_file.write("\t" + raw1 + "\t" + raw2);
    if (includeRef)
		    data_file.write("\t" + ref );
		i++;
	    }
	    while (i < mids.size()) {
		    data_file.write("\t" + NULL_CHAR + "\t" + NULL_CHAR);

		if (includeRaw)
		    data_file.write("\t" + NULL_CHAR + "\t" + NULL_CHAR);

    if (includeRef)
		    data_file.write("\t" + NULL_CHAR);

		i++;
	    }

	} catch (SQLException sqle) {
            sqle.printStackTrace();
	    writeLogFileError("writeGenotypes: Fatal SQL Error (iid=" + ind.iid + ")");
	    throw sqle;
	} catch (IOException ioe) {
            ioe.printStackTrace();
	    writeLogFileError("writeGenotypes: Fatal IO Error (iid=" + ind.iid + ")");
	    ioe.printStackTrace(System.err);
	    throw ioe;
	}
    }

    //-------------------------------------------------------------------------------
    private void writeUGenotypes(int sunr, int indnr)
	throws SQLException, IOException {
	ResultSet rset = null;
	RelDataRec rel = (RelDataRec) rels.elementAt(sunr);
	IndDataRec ind = (IndDataRec) inds.elementAt(indnr);
	String a1name = null;
	String a2name = null;
	String raw1 = null;
	String raw2 = null;
  String ref =null;
	int i = 0;
	try {
	    umstmt.clearParameters();
	    umstmt.setInt(1, Integer.parseInt(rel.suid));
	    umstmt.setInt(2, Integer.parseInt(ind.iid));
	    rset = umstmt.executeQuery();
	    while (rset.next()) {
		String umid = rset.getString("UMID");
		while (! umid.equals(((MidDataRec) mids.elementAt(i)).mid)) {

    data_file.write("\t" + NULL_CHAR + "\t" + NULL_CHAR);

		    if (includeRaw)
			  data_file.write("\t" + NULL_CHAR + "\t" + NULL_CHAR);
        if (includeRef)
			  data_file.write("\t" + NULL_CHAR);
		    i++;
		}
		a1name = replaceIfNull(translateAllele(i, rset.getString("AID1"), ind.identity));
		a2name = replaceIfNull(translateAllele(i, rset.getString("AID2"), ind.identity));
		raw1 = replaceIfNull(rset.getString("RAW1"));
		raw2 = replaceIfNull(rset.getString("RAW2"));
		ref = replaceIfNull(rset.getString("REFERENCE"));

    data_file.write("\t" + a1name + "\t" + a2name);

		if (includeRaw)
		    data_file.write("\t" + raw1 + "\t" + raw2);
	if (includeRef)
		    data_file.write("\t" + ref);

		i++;
	    }
	    while (i < mids.size()) {

    data_file.write("\t" + NULL_CHAR + "\t" + NULL_CHAR);

		if (includeRaw)
		    data_file.write("\t" + NULL_CHAR + "\t" + NULL_CHAR);
    if (includeRef)
		    data_file.write("\t" + NULL_CHAR + "\t" + NULL_CHAR);

		i++;
	    }

	} catch (SQLException sqle) {
            sqle.printStackTrace();
	    writeLogFileError("writeUGenotypes: Fatal SQL Error (iid=" + ind.iid + ")");
	    throw sqle;
	} catch (IOException ioe) {
	    writeLogFileError("writeUGenotypes: Fatal IO Error (iid=" + ind.iid + ")");
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
    private void writePhenotypes(int indnr)
	throws SQLException, IOException {
	ResultSet rset = null;
	IndDataRec ind = (IndDataRec) inds.elementAt(indnr);
	int i = 0;
	try {
	    vstmt.clearParameters();
	    vstmt.setInt(1, Integer.parseInt(ind.iid));
	    rset = vstmt.executeQuery();
	    while (rset.next()) {
		String vid = rset.getString("VID");
		while (! vid.equals(((VidDataRec) vids.elementAt(i)).vid)) {
		    data_file.write("\t" + NULL_CHAR);
		    i++;
		}
		data_file.write("\t" + replaceIfNull(rset.getString("VALUE")));
		i++;
	    }
	    while (i < vids.size()) {
		data_file.write("\t" + NULL_CHAR);
		i++;
	    }

	} catch (SQLException sqle) {
            sqle.printStackTrace();
	    writeLogFileError("writePhenotypes: Fatal SQL Error (iid=" + ind.iid + ")");
	    throw sqle;
	} catch (IOException ioe) {
            ioe.printStackTrace();
	    writeLogFileError("writePhenotypes: Fatal IO Error (iid=" + ind.iid + ")");
	    throw ioe;
	}
    }
	
    //-------------------------------------------------------------------------------
    private void writeUPhenotypes(int sunr, int indnr)
	throws SQLException, IOException {
	ResultSet rset = null;
	RelDataRec rel = (RelDataRec) rels.elementAt(sunr);
	IndDataRec ind = (IndDataRec) inds.elementAt(indnr);
	int i = 0;
	try {
	    uvstmt.clearParameters();
	    uvstmt.setInt(1, Integer.parseInt(rel.suid));
	    uvstmt.setInt(2, Integer.parseInt(ind.iid));
	    rset = uvstmt.executeQuery();
	    while (rset.next()) {
		String uvid = rset.getString("UVID");
		while (! uvid.equals(((VidDataRec) vids.elementAt(i)).vid)) {
		    data_file.write("\t" + NULL_CHAR);
		    i++;
		}
		data_file.write("\t" + replaceIfNull(rset.getString("VALUE")));
		i++;
	    }
	    while (i < vids.size()) {
		data_file.write("\t" + NULL_CHAR);
		i++;
	    }

	} catch (SQLException sqle) {
            sqle.printStackTrace();
	    writeLogFileError("writeUPhenotypes: Fatal SQL Error (iid=" + ind.iid + ")");
	    throw sqle;
	} catch (IOException ioe) {
            ioe.printStackTrace();
	    writeLogFileError("writeUPhenotypes: Fatal IO Error (iid=" + ind.iid + ")");
	    throw ioe;
	}
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
	    log_file.write("Format:     General Table Format\n");
	    log_file.write("Null char:  " + NULL_CHAR + "\n");
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
    private void writeLogFileSU(int su)
	throws IOException {
	RelDataRec rel = (RelDataRec) rels.elementAt(su);
	try {
	    log_file.write("---------------------------------------------\n");
	    log_file.write("Sampling unit: " + rel.suname + "\n");
	    log_file.write("Filter:        " + rel.fname + "\n");
	    log_file.write("Expression:    " + rel.gql + "\n");
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
	}
    }

    //-------------------------------------------------------------------------------
    private void prepareSQL()
	throws SQLException {

	mstmt = conn.prepareStatement("SELECT P.MID, G.A1NAME, G.A2NAME, G.RAW1, G.RAW2, G.REFERENCE"
				      + " FROM V_POSITIONS_2 P, V_GENOTYPES_3 G"
				      + " WHERE P.MSID=" + msid
				      + "   AND P.MID=G.MID"
				      + "   AND G.IID=?"
				      + " ORDER BY P.CNAME, P.POSITION, P.MNAME");

	umstmt = conn.prepareStatement("SELECT P.UMID, G.AID1, G.AID2, G.A1NAME, G.A2NAME, G.RAW1, G.RAW2, G.REFERENCE "
				       + " FROM V_U_POSITIONS_2 P, V_R_UMID_MID_1 M, V_GENOTYPES_3 G"
				       + " WHERE P.UMSID=" + msid
				       + "   AND P.UMID=M.UMID"
				       + "   AND M.SUID=?"
				       + "   AND M.MID=G.MID"
				       + "   AND G.IID=?"
				       + " ORDER BY P.CNAME, P.POSITION, P.UMNAME");

	uastmt = conn.prepareStatement("SELECT A.NAME"
				       + " FROM V_R_UAID_AID_1 M, V_U_ALLELES_1 A"
				       + " WHERE M.UMID=?"
				       + "   AND M.AID=?"
				       + "   AND M.UAID=A.UAID");

	vstmt = conn.prepareStatement("SELECT R.VID, P.VALUE"
				      + " FROM V_R_VAR_SET_2 R, V_PHENOTYPES_1 P"
				      + " WHERE R.VSID=" + vsid
				      + "   AND R.VID=P.VID"
				      + "   AND P.IID=?"
				      + " ORDER BY R.VNAME");

	uvstmt = conn.prepareStatement("SELECT R.UVID, P.VALUE"
				       + " FROM V_R_U_VAR_SET_2 R, V_R_UVID_VID_1 M, V_PHENOTYPES_1 P"
				       + " WHERE R.UVSID=" + vsid
				       + "   AND R.UVID=M.UVID"
				       + "   AND M.SUID=?"
				       + "   AND M.VID=P.VID"
				       + "   AND P.IID=?"
				       + " ORDER BY R.UVNAME");

	astmt = conn.prepareStatement("SELECT ABORT_"
				      + " FROM V_FILE_GENERATIONS_1"
				      + " WHERE FGID = " + fgid);
    }

    //-------------------------------------------------------------------------------
    private void setProgress(int dfid, int su, int sut, int n, int t)
	throws DbException, IOException 
    {
	String message = null;
	String progress = null;

	if (n == PROGRESS_ERROR)
	    progress = "ERROR";
	else if (su+1 == sut && n == t)
	    progress = "DONE";
	else
	    progress = (su*100/sut) + (n*100/sut/t) + " %";

	try 
        {
            DbDataFile df = new DbDataFile();
            df.setDataFileStatus(conn, dfid, progress);
	} 
        catch (Exception e) 
        {
            e.printStackTrace();
            
	    log_file.write("setProgress [" + message + "]\n");
	    throw new DbException("Internal Error. Failed to set progress");
	}
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

    //-------------------------------------------------------------------------------
    private String replaceIfNull(String val) {
	if (val != null)
	    return val;
	else
	    return NULL_CHAR;
	}

}


