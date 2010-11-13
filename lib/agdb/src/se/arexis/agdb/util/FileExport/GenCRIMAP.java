//
// CRI-MAP file generation
//
// Important notes about the gen files:
//
// 1. A gen file contains data for ONE chromosome.
// 2. Marker names consists of at most 15 characters.
// 3. Family names must be unique, but there are no limit on the number of characters.
// 4. Individual id's must be unique numbers.
// 5. Individuals must have 0 or 2 parents, which means that the unknown parent for an
//    individual with only on known parent must be generated.
// 6. Unknown parents and alleles are coded as 0.
// 7. Allele id's must be numbers.
// 8. All males must have their second (non existing) allele on the X chromosome coded
//    as a dummy allele, e.g. 99. 
//

package se.arexis.agdb.util.FileExport;
import java.io.*;
import java.util.*;
import java.sql.*;
import se.arexis.agdb.db.DbDataFile;
import se.arexis.agdb.db.DbException;
import se.arexis.agdb.util.*;

public class GenCRIMAP extends GenFormat //Thread 
{
 

   /**
    * This class hols information about an individual
    */
   class IndDataRec
   {
      /** The crimap id of the individual */
      public String n;

      /** The id of the SU the individual belong to */
      public int su;

      /** The name of the family the individual belong to */
      public String family;

      /** The id of the individual */
      public String iid;

      /** The identity of the individual */
      public String identity;

      /** The alias of the individual */
      public String alias;

      public String father;
      public String mother;

      /** The sex of the individual */
      public String sex;

      /** The birthdate of the individual */
      public String birth_date;


      /**
       * Creates a new IndDataRec instance.
       *
       */
      public IndDataRec ()
      {
         this.n = null;
         this.su = 0;
         this.family = null;
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
	public String gsid;

	public RelDataRec () {
	    this.sid = null;;
	    this.suid = null;
	    this.suname = null;;
	    this.fid = null;
	    this.fname = null;
	    this.gql = null;
	    this.filter = null;
	    this.gsid = null;
	}
    }

    class AllelesRec {
	public String name;
	public int n;

	public AllelesRec () {
	    this.name = null;
	    this.n = 0;
	}
    }

    class MidDataRec {

	public String cid;
	public String cname;
	public String mid;
	public String mname;
	public Vector alleles;

	public MidDataRec () {
	    this.cid = null;
	    this.cname = null;
	    this.mid = null;
	    this.mname = null;
	    this.alleles = null;
	}
    }

    class GenFileRec {

	public String cid;
	public String file_name;
	public int file_dfid;
	public FileWriter file;

	public GenFileRec () {
	    this.cid = null;
	    this.file_name = null;
	    this.file_dfid = 0;
	    this.file = null;
	}
    }

    // Constructor parameters
    private static int fgid = 0;
    private static String directory = null;
    private static String DB_URL = null;
    private static String DB_UID = null;
    private static String DB_PWD = null;
    private static String NULL_CHAR = null;

    // File generation parameters
    private String fgname = null;
    private String pid = null;
    private String pname = null;
    private String id = null;
    private String uname = null;
    private String mode = null;
    private String msid = null;
    private String msname = null;
    private boolean multiFiles = false;

    // Counters
    private int fcounter = 0;
    private int icounter = 0;
    private int dcounter = 0;
    private int maxInd = 0;

    // Vectors
    private Vector rels = null;

    /** The ids of the selected markers */
    private Vector mids = null;

    /** The ids of the selected individuals */
    private Vector inds = null;

   /** Missing individuals which has been created */
   private Vector dummys = null;

    // Files
    private Vector gens = null;

    private int log_dfid = 0;
    
    //private String log_name = "log.txt";
    private String log_name = "";
    private int map_dfid = 0;
    private FileWriter map_file = null;
   // private String map_name = "mapping.txt";
    private String map_name = "";
    
    

    private PreparedStatement mstmt = null;
    private PreparedStatement umstmt = null;
    private PreparedStatement uastmt = null;
    private PreparedStatement astmt = null;
    //private CallableStatement pstmt = null;

    // Constants
    private static int UPDATE_INTERVAL = 10;
    
    private static int ISTART = 1000;

    //-------------------------------------------------------------------------------
    public GenCRIMAP(int fgid, String directory,
		     String driver, String dburl, String uid, String pwd, boolean multiFiles)
	throws SQLException	{

	this.fgid = fgid;
	this.directory = directory;
	this.DB_URL = dburl;
	this.DB_UID = uid;
	this.DB_PWD = pwd;
        this.multiFiles = multiFiles;
        
        // The output file names are extended with the fgid for result import.
        this.log_name = "log_" + fgid + ".txt";
        this.map_name = "mapping_" + fgid + ".txt";
        
	this.pid = null;
	this.pname = null;
	this.id = null;
	this.uname = null;
	this.mode = null;
	this.msid = null;
	this.msname = null;

	this.rels = new Vector(1);
	this.mids = new Vector(100);
	this.inds = new Vector(1000);
	this.dummys = new Vector(100);
	this.gens = new Vector(25);

	this.fcounter = 0;
	this.icounter = 0;
	this.dcounter = 0;

      //  this.log_name = "data" + fgid + ".txt";
        try
        {
            //DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            //Class.forName("oracle.jdbc.driver.OracleDriver");
            Class.forName(driver);
            this.conn = DriverManager.getConnection(DB_URL, DB_UID, DB_PWD);
            this.conn.setAutoCommit(false);
        }
        catch (Exception e)
        {}
    }

    //-------------------------------------------------------------------------------
    public void run() 
    {
	int i = 0;
	try 
        {
            Errors.logDebug("GenCrimap.run() started");
            DbDataFile df = new DbDataFile();
            
            log_file = createPhysicalFile(directory, log_name);
            Errors.logDebug("before readFileGenParams");
	    readFileGenerationParameters();
            Errors.logDebug("after readFileGenParams");
	    readRelationParameters();
	    log_dfid = df.createDataFile(conn, fgid, log_name, id);
	    map_file = createPhysicalFile(directory, map_name);
	    map_dfid = df.createDataFile(conn, fgid, map_name, id);
	    prepareSQL();
            
            Errors.logDebug("Before single or multi");

	    if (mode.equals("S"))
		singleMode();
	    else
		multiMode();
            
            Errors.logDebug("After single or multi");
            
            conn.commit();
	} 
        catch (Exception e) 
        {
	    e.printStackTrace(System.err);
            try
            {
                conn.rollback();
            }
            catch (Exception ignore)
            {}
	} 
        finally 
        {
	    closePhysicalFile(log_file);
	    closePhysicalFile(map_file);
	    for (i=0; i < gens.size(); i++)
		closePhysicalFile(((GenFileRec) gens.elementAt(i)).file);
	}
        Errors.logDebug("GenCrimap.run() ended");
    }

    //-------------------------------------------------------------------------------
    public void singleMode()
	throws Exception {

	int i = 0;
	int j = 0;
	long t0 = System.currentTimeMillis();
	long t1 = 0;

	try {
            Errors.logDebug("SingleMode started");
	    writeLogFileHeader();
	    setProgress(log_dfid, 0, 1, 50, 100);
	    readMids();
            
            Errors.logDebug("After readMids");

      //
	    createGenFiles();

	    writeLogFileSU(0);
	    readInds(0);
	    writeGenFileHeaders();

	    for (i=0; i < inds.size(); i++)
		checkForMissingParents(i);

	    writeMapFile();
	    setProgress(map_dfid, 0, 1, 100, 100);

	    for (i=0; i < inds.size(); i++) {
                Errors.logDebug("singleMode i="+i);
		if (firstInFamily(i))
		    writeFamilyData(i);
		writeIndData(i);
		writeGenotypes(i);
		if (lastInFamily(i))
		    writeDummys(i);
		if ((i % UPDATE_INTERVAL) == 0) {
		    for (j=0; j < gens.size(); j++)
			setProgress(((GenFileRec) gens.elementAt(j)).file_dfid, 0, 1, i, inds.size());

		    if (aborted()) {
			writeLogFileError("Generation aborted by user");
			break;
		    }
		}
		yield(); // Give other threads a chance to run
	    }
	    
	    for (j=0; j < gens.size(); j++)
		setProgress(((GenFileRec) gens.elementAt(j)).file_dfid, 0, 1, 100, 100);

	} catch (Exception e) {
	    e.printStackTrace(System.err);
	    for (j=0; j < gens.size(); j++)
		setProgress(((GenFileRec) gens.elementAt(j)).file_dfid, 0, 1, PROGRESS_ERROR, 100);
	} finally {
	    t1 = System.currentTimeMillis();
	    writeLogFileFooter((int) (t1-t0)/1000);
	    setProgress(log_dfid, 0, 1, 100, 100);
	}
    }

    //-------------------------------------------------------------------------------
    public void multiMode()
	throws Exception {

	int su = 0;
	int i = 0;
	int j = 0;
	long t0 = System.currentTimeMillis();
	long t1 = 0;

	try {
	    writeLogFileHeader();
	    setProgress(log_dfid, 0, 1, 50, 100);
	    readUMids();
	    createGenFiles();

	    for (su=0; su < rels.size(); su++)
		readInds(su);
	    writeGenFileHeaders();

	    for (i=0; i < inds.size(); i++) {
		if (firstInSamplingUnit(i)) {
		    IndDataRec ind = (IndDataRec) inds.elementAt(i); 
		    writeLogFileSU(ind.su);
		    checkForMissingMarkerMappings(ind.su);
		}
		checkForMissingParents(i);
	    }

	    writeMapFile();
	    setProgress(map_dfid, 0, 1, 100, 100);
	    
	    for (i=0; i < inds.size(); i++) {
		if (firstInFamily(i))
		    writeFamilyData(i);
		writeIndData(i);
		writeUGenotypes(i);
		if (lastInFamily(i))
		    writeDummys(i);
		if ((i % UPDATE_INTERVAL) == 0) { 
		    for (j=0; j < gens.size(); j++)
			setProgress(((GenFileRec) gens.elementAt(j)).file_dfid, 0, 1, i, inds.size());
		    if (aborted()) {
			writeLogFileError("Generation aborted by user");
			break;
		    }
		}
		yield(); // Give other threads a chance to run
	    }
	    
	    for (j=0; j < gens.size(); j++)
		setProgress(((GenFileRec) gens.elementAt(j)).file_dfid, 0, 1, 100, 100);

	} catch (Exception e) {
	    e.printStackTrace(System.err);
	    for (j=0; j < gens.size(); j++)
		setProgress(((GenFileRec) gens.elementAt(j)).file_dfid, 0, 1, PROGRESS_ERROR, 100);
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
    /*
private int createDataFile(int fgid, String name, String id)
	throws SQLException, IOException {
	CallableStatement s = null;
	String message = null;
	int dfid = 0;
        
        if (name.length()>20)
            throw new DbException("Name exceeds 20 characters");
        

	try 
        {
            
            dfid = getNextID(conn,"Data_Files_Seq");
            
            
	-- Find DFID
	  begin
	    if l_ok then
		  select Data_Files_Seq.nextval into p_dfid from dual;
		end if;
	  exception
	    when others then
		  p_message := 'Failed to increment file id. ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end;
	-- Create the data file row
	  begin
	    if l_ok then
		  insert into Data_Files Values(
		    p_dfid, p_fgid, p_name, p_status,
			p_comm, p_id, sysdate);
		end if;
	  exception
	    when others then
		  p_message := 'Failed to create the data file row. ' ||
		  			   'ORACLE error: ' || SQLERRM;
		  l_ok := false;
	  end;
            
            
            
            
            
            
            
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
     */
	
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
	    } else
		throw new SQLException("No file generation found with fgid = " + fgid);

	}
        catch (SQLException sqle) 
        {
            sqle.printStackTrace();
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
	    q = "SELECT R.SID, R.SUID, R.NAME as SUNAME, R.FID, F.NAME as FNAME, R.EXPRESSION, R.GSID"
		+ " FROM V_R_FG_FLT_1 R, V_FILTERS_1 F"
		+ " WHERE R.FID = F.FID AND FGID = " + fgid;
	    s = conn.createStatement();
	    r = s.executeQuery(q);

	    while (r.next()) 
            {
                Errors.logDebug("ReadRelationParameters...");
		relData = new RelDataRec();
		relData.sid = r.getString("SID");
		relData.suid = r.getString("SUID");
		relData.suname = r.getString("SUNAME");
		relData.fid = r.getString("FID");
		relData.fname = r.getString("FNAME");
		relData.gql = r.getString("EXPRESSION");
		relData.gsid = r.getString("GSID");
		trans = new GqlTranslator(pid, relData.suid, relData.gsid, relData.gql, conn);
		trans.translate();
		relData.filter = trans.getFilter();
		relData.gsid = r.getString("GSID");
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
			       + "grp.GNAME, "
			       + "ind.IID, "
			       + "ind.IDENTITY, "
			       + "ind.ALIAS, "
			       + "ind.FIDENTITY, "
			       + "ind.MIDENTITY, "
			       + "ind.SEX, "
			       + "to_char(ind.BIRTH_DATE, 'YYYY-MM-DD') as BD "
			       + rel.filter
			       + " ORDER BY grp.GNAME");

	    while (r.next()) {
		icounter++;
		if (ind == null || !ind.family.equals(r.getString("GNAME")))
		    fcounter++;
		ind = new IndDataRec();


    // annars ta ny siffra, (obs dummys) (Multiple???)


		//ind.n = "" + (ISTART + icounter);
		ind.su = su;
		ind.family = r.getString("GNAME");
		ind.iid = r.getString("IID");
		ind.identity = r.getString("IDENTITY");
    //System.err.println("identity:"+ind.identity);
     // determine if name is an integer or we need to rename
		//ind.n = "" + (ISTART + icounter);
    int newValue = getMappingName(ind.identity);
    if (newValue > 0 && !occupied(newValue,inds))
      ind.n = "" + newValue;
    else
      ind.n = null;

    // Keep track of highest nr found
    if(ind.n != null && ind.n != "")
    {
        if(Integer.parseInt(ind.n) > maxInd)
        {
           maxInd = Integer.parseInt(ind.n);
        }
    }
//---------
		ind.alias = r.getString("ALIAS");      if (r.wasNull()) ind.alias = null;
		ind.father = r.getString("FIDENTITY"); if (r.wasNull()) ind.father = null;
		ind.mother = r.getString("MIDENTITY"); if (r.wasNull()) ind.mother = null;
		ind.sex = r.getString("SEX");          if (r.wasNull()) ind.sex = null;
		ind.birth_date = r.getString("BD");    if (r.wasNull()) ind.birth_date = null;
		inds.addElement(ind);

		yield(); // Give other threads a chance to run
	    }
      // rename all indnames that was not integer value
      for (int i=0; i<icounter;i++)
      {
        IndDataRec tempInd = (IndDataRec)inds.elementAt(i);
        if(tempInd.n == null || tempInd.equals(""))
        {
          maxInd ++;
          tempInd.n= "" + maxInd;
          inds.removeElementAt(i);
          inds.insertElementAt(tempInd,i);
        }
      }
    //Dummys???



	} catch (SQLException sqle) {
	    writeLogFileError("SQL ERROR [" + rel.filter + "]");
	    throw sqle;
	} finally {
	    if (r != null) r.close();
	    if (s != null) s.close();
	}
    }

    //-------------------------------------------------------------------------------

private int getMappingName(String identity)
{
      //Encode integers as integers...
    int identityAsInteger;

    try
    {
        identityAsInteger =Integer.parseInt(identity);
    // Do we catch parse execption if an integer was not "found"??
    } catch (Exception e)
    {
        // we could not retrieve an integer
        identityAsInteger =-1;
    }

    return identityAsInteger;
}
private boolean occupied(int intValue, Vector allInds)
{
      String strValue = ""+intValue;
      //look for value in vector
      for (int i=0; i<allInds.size();i++)
      {
        IndDataRec tempInd = (IndDataRec)inds.elementAt(i);
        String tmp = tempInd.n;
        if(tmp !=null && tmp.equals(strValue))
        {
          return true;
        }
      }
    return false;
}

    public boolean indExist(int su, String identity) {
	int i = 0;

	while (i < inds.size()) {
	    IndDataRec ind = (IndDataRec) inds.elementAt(i);
	    if (su == ind.su && identity.equals(ind.identity))
		return true;
	    i++;
	}

	return false;
    }


   /**
    * Checks if an individual is a member of a given SU and given family.
    *
    * @param samplingUnitId The id of the SU to look for individual in.
    * @param individualId The id of the individual to look for.
    * @param familyName The name of the family to look for individual in.
    * @return True if individual is member of familiy
    *         False if individual is not member of family
    */
   public boolean indInFamily(int samplingUnitId, String individualId,
                              String familyName)
   {
      IndDataRec currentInd;

      // Loop all individuals
      int i = 0;
      while (i < inds.size())
      {
         // Get current individual and check if it is the one we are
         // looking for
         currentInd = (IndDataRec) inds.elementAt(i);
         if (samplingUnitId == currentInd.su &&
             individualId.equals(currentInd.identity) &&
             familyName.equals(currentInd.family))
         {
            return true;
         }
         i++;
      }

      return false;
   }


   /**
    * Look for an individual within a given SU and a given family and
    * return the N value (crimap id) of the individual.
    *
    * @param samplingUnitId The SU to look for individual in.
    * @param individualId The id of the individual to look for.
    * @param familyName The family name to look for individual in.
    * @return The crimap individual id.
    */
   public String getN(int samplingUnitId, String individualId, String familyName)
   {
      if (individualId == null)
      {
         return "0";
      }

      IndDataRec currentInd;

      // Look for individual in inds array
      for (int i = 0; i < inds.size(); i++)
      {
         // if current individual has correct su, family name and
         // individual id, return its N-value
         currentInd = (IndDataRec) inds.elementAt(i);
         if (samplingUnitId == currentInd.su &&
             individualId.equals(currentInd.identity) &&
             familyName.equals(currentInd.family))
         {
            return currentInd.n;
         }
      }

      // Individual not found, look for individual in dummys array
      for (int i = 0; i<dummys.size(); i++)
      {
         // if corrent su, family name and id, return N-value of individual
         currentInd = (IndDataRec) dummys.elementAt(i);
         if (samplingUnitId == currentInd.su &&
             individualId.equals(currentInd.identity) &&
             familyName.equals(currentInd.family)) 
         {
            return currentInd.n;
         }
      }

      return "0";
   }

   
    //-------------------------------------------------------------------------------
    public String createDummy(int su, String fam, String sex) {
	IndDataRec dummy = null;

	dcounter++;
	dummy = new IndDataRec();
	//dummy.n = "" + (ISTART + icounter + dcounter);
  maxInd ++;
  dummy.n = "" + (maxInd);
	dummy.su = su;
	dummy.family = fam;
	dummy.iid = null;
	dummy.identity = "<..." + dummy.n + "...>";
	dummy.alias = null;
	dummy.father = null;
	dummy.mother = null;
	dummy.sex = sex;
	dummy.birth_date = null;
	dummys.addElement(dummy);
	
	return dummy.identity;
    }

    //-------------------------------------------------------------------------------
    public void checkForMissingParents(int indnr) {
	IndDataRec ind = (IndDataRec) inds.elementAt(indnr);
	
	if (ind.father != null) {
	    if (!indExist(ind.su, ind.father)) {
		writeLogFileWarning("Father (" + ind.father + ") for "
				    + "individual ("+ ind.identity + ")"
				    + " is not selected by filter and family grouping");
		ind.father = null;
	    } else if (!indInFamily(ind.su, ind.father, ind.family)) {
		writeLogFileWarning("Father (" + ind.father + ") for "
				    + "individual ("+ ind.identity + ")"
				    + " is not a member of the family");
		ind.father = null;
	    }
	}

	if (ind.mother != null) {
	    if (!indExist(ind.su, ind.mother)) {
		writeLogFileWarning("Mother (" + ind.mother + ") for "
				    + "individual ("+ ind.identity + ")"
				    + " is not selected by filter and family grouping");
		ind.mother = null;
	    } else if (!indInFamily(ind.su, ind.mother, ind.family)) {
		writeLogFileWarning("Mother (" + ind.mother + ") for "
				    + "individual ("+ ind.identity + ")"
				    + " is not a member of the family");
		ind.mother = null;
	    }
	}

	if (ind.father == null && ind.mother != null)
	    ind.father = createDummy(ind.su, ind.family, "M");

	if (ind.father != null && ind.mother == null)
	    ind.mother = createDummy(ind.su, ind.family, "F");
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
    public boolean firstInSamplingUnit(int i) {
	if (i == 0)
	    return true;
	else {
	    IndDataRec curr = (IndDataRec) inds.elementAt(i);
	    IndDataRec prev = (IndDataRec) inds.elementAt(i-1);
	    return curr.su != prev.su;
	}
    }

    //-------------------------------------------------------------------------------
    public boolean firstInFamily(int i) {
	if (i == 0)
	    return true;
	else {
	    IndDataRec curr = (IndDataRec) inds.elementAt(i);
	    IndDataRec prev = (IndDataRec) inds.elementAt(i-1);
	    return !(curr.su == prev.su && curr.family.equals(prev.family));
	}
    }

    //-------------------------------------------------------------------------------
    public boolean lastInFamily(int i) {
	if (i == inds.size()-1)
	    return true;
	else {
	    IndDataRec curr = (IndDataRec) inds.elementAt(i);
	    IndDataRec next = (IndDataRec) inds.elementAt(i+1);
	    return !(curr.su == next.su && curr.family.equals(next.family));
	}
    }

   //-------------------------------------------------------------------------------
   public Vector renumberAlleles(Vector a) {
      int i = 0;
      int imax = 0;

      Errors.logDebug("renumberAlleles, a.size="+a.size());
      //Encode integers as integers...

      for (i=0; i<a.size(); i++){
         try {
            AllelesRec ar = (AllelesRec) a.elementAt(i);

            ar.n = Integer.parseInt(ar.name);

            if (ar.n > imax)
            {
               imax = ar.n;
            }
         } catch (Exception e)
         { }
      }
      
      Errors.logDebug("imax="+imax+", a.size="+a.size());
      
      //Encode non positive integers as positive integers...
      for (i=0; i<a.size(); i++) {
         AllelesRec ar = (AllelesRec) a.elementAt(i);
         if (ar.n <= 0)
            ar.n = ++imax;
      }
      
      Errors.logDebug("imax="+imax+", a.size="+a.size());

      /*
       *This section is outcommented due to a serious bug. Never ending loop.
       *  ... TH
      // Fill out the holes...
      for (i=0; i<a.size(); i++) {
         AllelesRec ar = (AllelesRec) a.elementAt(i);
         Errors.logDebug("Alleles="+ar.name+", i="+i+", ar.n="+ar.n);
         if (ar.n != i + 1) {

            AllelesRec nar = new AllelesRec();
            nar.n = i + 1;
            nar.name = "";
            a.insertElementAt(nar, i);
         }
      }
       */
      Errors.logDebug("renumberAlleles done");
      return a;
   }

    //-------------------------------------------------------------------------------
    public Vector readAlleles(String mid, String mname)
	throws SQLException {
	Statement s = null;
	ResultSet r = null;
	String q = null;
	AllelesRec ar = null;
	Vector alleles = new Vector();
	try {
	    q = "SELECT NAME"
		+ " FROM V_ALLELES_1"
		+ " WHERE MID = " + mid
		+ " ORDER BY TO_POSITIVE_NUMBER_ELSE_NULL(NAME), NAME";
	    
	    s = conn.createStatement();
	    r = s.executeQuery(q);
	    while (r.next()) {
                Errors.logDebug("readAlleles loop [mid="+mid+", mname="+mname+",aname="+r.getString("NAME")+"]");
		ar = new AllelesRec();
		ar.name = r.getString("NAME");
		if (!ar.name.equals("0"))                // Temporary fix for Uppsala! 
		    alleles.addElement(ar);
		else
		    log_file.write("WARNING: Allele 0 for marker " + mname + " treated as NULL\n");
	    }

	} catch (SQLException sqle) {
	    writeLogFileError("SQL ERROR [" + q + "]");
	    throw sqle;
	} catch (IOException ioe) {
	} finally {
	    if (r != null) r.close();
	    if (s != null) s.close();
	}

	return renumberAlleles(alleles);
    }

    //-------------------------------------------------------------------------------
    public Vector readUAlleles(String mid, String mname)
	throws SQLException {
	Statement s = null;
	ResultSet r = null;
	String q = null;
	AllelesRec ar = null;
	Vector alleles = new Vector();
	try {
	    q = "SELECT NAME"
		+ " FROM V_U_ALLELES_1"
		+ " WHERE UMID = " + mid
		+ " ORDER BY TO_POSITIVE_NUMBER_ELSE_NULL(NAME), NAME";
	    
	    s = conn.createStatement();
	    r = s.executeQuery(q);
	    while (r.next()) {
		ar = new AllelesRec();
		ar.name = r.getString("NAME");
		if (!ar.name.equals("0"))                // Temporary fix for Uppsala!  
		    alleles.addElement(ar);
		else
		    log_file.write("WARNING: Allele 0 for marker " + mname + " treated as NULL\n");
	    }

	} catch (SQLException sqle) {
	    writeLogFileError("SQL ERROR [" + q + "]");
	    throw sqle;
	} catch (IOException ioe) {
	} finally {
	    if (r != null) r.close();
	    if (s != null) s.close();
	}

	return renumberAlleles(alleles);
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
                    Errors.logDebug("readMids loop");
		    mid = new MidDataRec();
		    mid.cid = r.getString("CID");
		    mid.cname = r.getString("CNAME");
		    mid.mid = r.getString("MID");
		    mid.mname = r.getString("MNAME");
		    if (mid.mname.length() > 15)
			writeLogFileError("Marker name (" + mid.mname + ") too long!");
		    mid.alleles = readAlleles(mid.mid, mid.mname);
		    mids.addElement(mid);
		}

    // we only want one uotput file.
    // No nead to handle different chromosomes, we name them the same
    if (!multiFiles)
    {
       MidDataRec tmpMid;
      for (int i=0; i<mids.size();i++)
      {
       tmpMid=(MidDataRec) mids.elementAt(i);
       mids.removeElementAt(i);
       tmpMid.cid="_";
       tmpMid.cname="_";
       mids.insertElementAt(tmpMid,i);
      }
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
		    if (mid.mname.length() > 15)
			writeLogFileError("Marker name (" + mid.mname + ") too long!");
		    mid.alleles = readUAlleles(mid.mid, mid.mname);
		    mids.addElement(mid);
		}

      // we only want one uotput file.
    // No nead to handle different chromosomes, we name them the same
    if (!multiFiles)
    {
       MidDataRec tmpMid;
      for (int i=0; i<mids.size();i++)
      {
       tmpMid=(MidDataRec) mids.elementAt(i);
       mids.removeElementAt(i);
       tmpMid.cid="_";
       tmpMid.cname="_";
       mids.insertElementAt(tmpMid,i);
      }
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
    public void createGenFiles()
	throws SQLException, IOException, DbException
    {
	GenFileRec gfr = null;
	GenFileRec tmpGfr = null;

	MidDataRec mid = null;
	String lcname = null;
	int i = 0;
        
        DbDataFile df = new DbDataFile();

        for (i=0; i < mids.size(); i++) 
        {
            mid = (MidDataRec) mids.elementAt(i);
            if (lcname == null || !lcname.equals(mid.cname)) 
            {
                gfr = new GenFileRec();
                gfr.cid = mid.cid;
                gfr.file_name = "chr" + mid.cname + ".gen";
                gfr.file = createPhysicalFile(directory, gfr.file_name);
                gfr.file_dfid = df.createDataFile(conn, fgid, gfr.file_name, id);
                gens.addElement(gfr);
                lcname = mid.cname;
            }
        }
  }

    //-------------------------------------------------------------------------------
    public int countMarkers(String cid) {
	int i = 0;
	int counter = 0;

  	  for (i=0; i < mids.size(); i++)
      {
	      if (cid.equals(((MidDataRec) mids.elementAt(i)).cid))
		  counter++;
      }

	return counter;
    }

    //-------------------------------------------------------------------------------
    private void writeGenFileHeaders()
	throws IOException	{
	int i = 0;
  

	for (i=0; i < gens.size(); i++) {
	    GenFileRec gen = (GenFileRec) gens.elementAt(i);
	    gen.file.write(fcounter + "\n");
	    gen.file.write(countMarkers(gen.cid) + "\n");
	    writeMarkers(gen);
	    gen.file.write("\n");
	}
    }

    //-------------------------------------------------------------------------------
    private void writeMarkers(GenFileRec gen)
	throws IOException {
	int j = 0;

	  for (j=0; j < mids.size(); j++)
	      if (gen.cid.equals(((MidDataRec) mids.elementAt(j)).cid))
		  gen.file.write(((MidDataRec) mids.elementAt(j)).mname + " ");
      }


    //-------------------------------------------------------------------------------
    public int countFamilyMembers(int su, String fam) {
	int i = 0;
	int counter = 0;

	for (i=0; i<inds.size(); i++) {
	    IndDataRec ind = (IndDataRec) inds.elementAt(i);
	    if (su == ind.su && fam.equals(ind.family))
		counter++;
	}

	for (i=0; i<dummys.size(); i++) {
	    IndDataRec ind = (IndDataRec) dummys.elementAt(i);
	    if (su == ind.su && fam.equals(ind.family))
		counter++;
	}
	
	return counter;
    }

    //-------------------------------------------------------------------------------
    private void writeFamilyData(int i)
	throws IOException {
	IndDataRec ind = (IndDataRec) inds.elementAt(i);
	RelDataRec rel = (RelDataRec) rels.elementAt(ind.su);
	int j = 0;
        int members = countFamilyMembers(ind.su, ind.family);

	for (j=0; j < gens.size(); j++) {
	    FileWriter file = ((GenFileRec) gens.elementAt(j)).file;
	    file.write("\n");
	    if (mode.equals("M"))
		file.write(rel.suname + "/");
	    file.write(ind.family + "\n");
	    file.write(members + "\n");
	}
    }

    //-------------------------------------------------------------------------------
    private void writeIndData(int i)
	throws Exception {
	IndDataRec ind = (IndDataRec) inds.elementAt(i);
	int j = 0;

	for (j=0; j < gens.size(); j++) {
	    FileWriter file = ((GenFileRec) gens.elementAt(j)).file;
	    file.write(ind.n + " ");
            file.write(getN(ind.su, ind.mother, ind.family) + " ");
            file.write(getN(ind.su, ind.father, ind.family) + " ");
	    if (ind.sex.equals("F"))
		file.write("0");
	    else if (ind.sex.equals("M"))
		file.write("1");
	    else
		file.write("3");
	    file.write("\n");
	}
    }

    //-------------------------------------------------------------------------------
    private void writeDummys(int indnr)
	throws Exception {
	String fam = ((IndDataRec) inds.elementAt(indnr)).family;
	int su = ((IndDataRec) inds.elementAt(indnr)).su;
	IndDataRec ind = null;;
	MidDataRec mid = null;;
	int i = 0;
	int j = 0;
	int k = 0;

	for (j=0; j < gens.size(); j++) {
	    GenFileRec gen = ((GenFileRec) gens.elementAt(j));
	    for (i=0; i< dummys.size(); i++) {
		ind = (IndDataRec) dummys.elementAt(i);
		if (su == ind.su && fam.equals(ind.family)) {
		    gen.file.write(ind.n + " ");
		    gen.file.write("0 ");
		    gen.file.write("0 ");
		    if (ind.sex.equals("F"))
			gen.file.write("0");
		    else if (ind.sex.equals("M"))
			gen.file.write("1");
		    else
			gen.file.write("3");
		    gen.file.write("\n");
		    for (k=0; k < mids.size(); k++) {
			mid = (MidDataRec) mids.elementAt(k);
			if (gen.cid.equals(mid.cid))
			    gen.file.write("0 " + replaceIfMaleX(ind, mid, "0") + " ");
		    }
		    gen.file.write("\n");
		}
	    }
	}
    }

    //-------------------------------------------------------------------------------
    public FileWriter getFile(String cid) {
	int i = 0;


	for (i=0; i<gens.size(); i++) {
	    GenFileRec gen = (GenFileRec) gens.elementAt(i);
	    if (cid.equals(gen.cid))
		return gen.file;
	}

	return null;
    }


   /**
    * Renumbers an allele in a given marker.
    *
    * @param markerId The marker to look for allele in.
    * @param alleleNr The allele number.
    * @return The new number of the allele.
    */
   public String renumberAllele(int markerId, String alleleNr)
   {
      if (alleleNr == null)
      {
         return null;
      }

      // Get the alleles of the current marker
      Vector alleles = ((MidDataRec) mids.elementAt(markerId)).alleles;


      

      // Loop all alleles and look for the one with the given number. When
      // found, calculate the new number and return it.
      for (int i = 0; i < alleles.size(); i++)
      {

         AllelesRec ar = (AllelesRec) alleles.elementAt(i);
         if (alleleNr.equals(ar.name))
            return "" + ar.n;


      /*
         if (alleleNr.equals( ( (AllelesRec) alleles.elementAt(i)).name))
         {
            return "" + (i+1);
         }

         */
      }

      return null;
   }

    //-------------------------------------------------------------------------------
    public String replaceIfMaleX(IndDataRec ind, MidDataRec mid, String allele) {
	if (ind.sex.equals("M") && 
	    (mid.cname.equals("x") || mid.cname.equals("X")))
	    return "" + (mid.alleles.size()+10);
	else
	    return allele;
    }

    //-------------------------------------------------------------------------------
    private void writeGenotypes(int indnr)
	throws SQLException, IOException {
	ResultSet rset = null;
	IndDataRec ind = (IndDataRec) inds.elementAt(indnr);
	MidDataRec mid = null;
	String a1name = null;
	String a2name = null;
	FileWriter fw = null;
	int i = 0;
	try {
	    mstmt.clearParameters();
	    mstmt.setInt(1, Integer.parseInt(ind.iid));
	    rset = mstmt.executeQuery();
	    while (rset.next()) {
		String dbmid = rset.getString("MID");
		while (! dbmid.equals(((MidDataRec) mids.elementAt(i)).mid)) {
		    mid = (MidDataRec) mids.elementAt(i);
		    fw = getFile(mid.cid);
		    fw.write("0 " + replaceIfMaleX(ind, mid, "0") + " ");
		    i++;
		}
		mid = (MidDataRec) mids.elementAt(i);
		fw = getFile(mid.cid);

		a1name = rset.getString("A1NAME");
		a2name = rset.getString("A2NAME");

		a1name = renumberAllele(i, a1name);
		a2name = renumberAllele(i, a2name);
		a2name = replaceIfMaleX(ind, mid, a2name);
		fw.write(replaceIfNull(a1name) + " " +
			 replaceIfNull(a2name) + " ");
		
		i++;
	    }
	    while (i < mids.size()) {
		mid = (MidDataRec) mids.elementAt(i);
		fw = getFile(mid.cid);
		fw.write("0 " + replaceIfMaleX(ind, mid, "0") + " ");
		i++;
	    }
	    for (i=0; i<gens.size(); i++) {
		GenFileRec gen = (GenFileRec) gens.elementAt(i);
		gen.file.write("\n");
	    }
			
	} catch (SQLException sqle) {
	    writeLogFileError("writeGenotypes: Fatal SQL Error (iid=" + ind.iid + ")");
	    throw sqle;
	} catch (IOException ioe) {
	    writeLogFileError("writeGenotypes: Fatal IO Error (iid=" + ind.iid + ")");
	    ioe.printStackTrace(System.err);			  
	    throw ioe;
	}
    }

    //-------------------------------------------------------------------------------
    private void writeUGenotypes(int indnr)
	throws SQLException, IOException {
	ResultSet rset = null;
	IndDataRec ind = (IndDataRec) inds.elementAt(indnr);
	MidDataRec mid = null;
	String a1name = null;
	String a2name = null;
	RelDataRec rel = (RelDataRec) rels.elementAt(ind.su);
	FileWriter fw = null;
	int i = 0;
	try {
	    umstmt.clearParameters();
	    umstmt.setInt(1, Integer.parseInt(rel.suid));
	    umstmt.setInt(2, Integer.parseInt(ind.iid));
	    rset = umstmt.executeQuery();
	    while (rset.next()) {
		String dbmid = rset.getString("UMID");
		while (! dbmid.equals(((MidDataRec) mids.elementAt(i)).mid)) {
		    mid = (MidDataRec) mids.elementAt(i);
		    fw = getFile(mid.cid);
		    fw.write("0 " + replaceIfMaleX(ind, mid, "0") + " ");
		    i++;
		}
		mid = (MidDataRec) mids.elementAt(i);
		fw = getFile(mid.cid);

		a1name = translateAllele(i, rset.getString("AID1"), ind.identity);
		a2name = translateAllele(i, rset.getString("AID2"), ind.identity);

		a1name = renumberAllele(i, a1name);
		a2name = renumberAllele(i, a2name);
		a2name = replaceIfMaleX(ind, mid, a2name);
		fw.write(replaceIfNull(a1name) + " " +
			 replaceIfNull(a2name) + " ");
		
		i++;
	    }
	    while (i < mids.size()) {
		mid = (MidDataRec) mids.elementAt(i);
		fw = getFile(mid.cid);
		fw.write("0 " + replaceIfMaleX(ind, mid, "0") + " ");
		i++;
	    }
	    for (i=0; i<gens.size(); i++) {
		GenFileRec gen = (GenFileRec) gens.elementAt(i);
		gen.file.write("\n");
	    }
			
	} catch (SQLException sqle) {
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
    private void writeLogFileHeader()
	throws IOException {
	try {
	    log_file.write("#\n");
	    log_file.write("# File generation log file\n");
	    log_file.write("#\n");
	    log_file.write("Project:    " + pname + "\n");
	    log_file.write("User:       " + uname + "\n");
	    log_file.write("Generation: " + fgname + "\n");
	    log_file.write("Format:     CRI-MAP\n");
	    if (mode.equals("S")) {
		log_file.write("Mode:       Single\n");
		log_file.write("Marker Set:   " + msname + "\n");
	    }
	    else {
		log_file.write("Mode:       Multi\n");
		log_file.write("Unified Marker Set:   " + msname + "\n");
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
    private void writeMapFile()
	throws IOException {
	int i = 0;
	int j = 0;
	int max = 0;

	for (i=0; i<mids.size(); i++)
	    if (((MidDataRec) mids.elementAt(i)).alleles.size() > max)
		max = ((MidDataRec) mids.elementAt(i)).alleles.size();

	map_file.write("#\n");
	map_file.write("# File generation mapping file\n");
	map_file.write("#\n");
	map_file.write("\n");
	map_file.write("Remapped Individuals :\n");
	map_file.write("====================\n");
	if (mode.equals("S")) {
	    map_file.write("Identity\tNumber\n");
	    map_file.write("--------\t------\n");
	    for (i=0; i<inds.size(); i++) {
		IndDataRec ind = (IndDataRec) inds.elementAt(i);
        if(!(ind.identity.equals(ind.n)))
        {
		    map_file.write(ind.identity + "\t" + ind.n + "\n");
        }
	    }
	}
	else {
	    map_file.write("Sampling Unit\tIdentity\tNumber\n");
	    map_file.write("-------------\t--------\t------\n");
	    for (i=0; i<inds.size(); i++) {
		IndDataRec ind = (IndDataRec) inds.elementAt(i);
		RelDataRec rel = (RelDataRec) rels.elementAt(ind.su);
        if(!(ind.identity.equals(ind.n)))
        {
		      map_file.write(rel.suname + "\t" + ind.identity + "\t" + ind.n + "\n");
        }
	    }
	}
	map_file.write("\n");
	map_file.write("Synthesized Inividuals: ");
	if (dummys.size() > 0) {
	    map_file.write(((IndDataRec) dummys.elementAt(0)).n);
	    if (dummys.size() > 1) {
		map_file.write("..");
		map_file.write(((IndDataRec) dummys.elementAt(dummys.size()-1)).n);
	    }
	    map_file.write("\n");
	}
	else {
	    map_file.write("-\n");
	}

	map_file.write("\n");
	map_file.write("Allele mappings:\n");
	map_file.write("================\n");
	map_file.write("Allele:");
	for (i=0; i<max; i++)
	    map_file.write("\t" + (i+1));
	map_file.write("\n");

	map_file.write("------");
	for (i=0; i<max; i++)
	    map_file.write("\t---");
	map_file.write("\n");

	for (i=0; i<mids.size(); i++) {
	    MidDataRec mid = (MidDataRec) mids.elementAt(i);
	    map_file.write(mid.mname);
	    for (j=0; j<mid.alleles.size(); j++)
                map_file.write("\t" + ((AllelesRec) mid.alleles.elementAt(j)).name);
	    map_file.write("\n");
	}
    }

    //-------------------------------------------------------------------------------
    private void prepareSQL()
	throws SQLException {

	mstmt = conn.prepareStatement("SELECT P.MID, G.A1NAME, G.A2NAME, G.RAW1, G.RAW2"
				      + " FROM V_POSITIONS_2 P, V_GENOTYPES_3 G"
				      + " WHERE P.MSID=" + msid
				      + "   AND P.MID=G.MID"
				      + "   AND G.IID=?"
				      + " ORDER BY P.CNAME, P.POSITION, P.MNAME");

	umstmt = conn.prepareStatement("SELECT P.UMID, G.AID1, G.AID2, G.A1NAME, G.A2NAME, G.RAW1, G.RAW2"
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

	astmt = conn.prepareStatement("SELECT ABORT_"
				      + " FROM V_FILE_GENERATIONS_1"
				      + " WHERE FGID = " + fgid);

	//pstmt = conn.prepareCall("{CALL GDBP.SET_DATA_FILE_STATUS(?,?,?)}");
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
	    return "0";
	}

}
