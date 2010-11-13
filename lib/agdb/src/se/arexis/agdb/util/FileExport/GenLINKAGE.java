/*
  $Log$
  Revision 1.1  2005/03/22 12:50:17  heto
  Working with moving all export files to a new package: se.arexis.agdb.util.FileExport

  Revision 1.4  2005/01/31 16:16:41  heto
  Changing database to PostgreSQL. Problems with counts and selection buttons...

  Revision 1.3  2003/12/09 09:19:36  wali
  fgid added to the output filename.

  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.11  2001/09/06 13:01:04  roca
  Major changes to Genotype import handling.
  modified Linkage output format for Post makeped and allele numbering.
  Bug when deleting markersets fixed

  Revision 1.10  2001/06/21 12:31:00  roca
  *** empty log message ***

  Revision 1.6  2001/06/07 13:19:47  roca
  added function TO_POSITIVE_NUMBER_ELSE_NULL in api_misc
  changed buttons to Finish/cancel on som generation pages
  Fixed bug (when alelename=0) in Genlinkage
  Tried to implement Linkage without locus

  Revision 1.5  2001/05/21 09:37:36  frob
  Indented the file.

  Revision 1.4  2001/05/17 12:54:26  frob
  Bugfix in writeDummys.

*/
//
// LINKAGE file generation
//
// Important notes!!!
//
// This version only implements the MLINK-format with numbered alleles.
//

package se.arexis.agdb.util.FileExport;
import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.DecimalFormat;
import se.arexis.agdb.util.*;

public class GenLINKAGE extends Thread
{

   class IndDataRec {
      public String n;
      public int su;
      public String family;
      public int family_seq;
      public String iid;
      public String identity;
      public String alias;
      public String father;
      public String mother;
      public String sex;
      public String birth_date;

      public IndDataRec () {
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
      public int freq;

      public AllelesRec () {
         this.name = null;
         this.n = 0;
         this.freq = 0;
      }
   }

   class MidDataRec {
      public String cid;
      public String cname;
      public double position;
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

      public void incAllelesOccur(String a1name, String a2name) {
         AllelesRec ar;
         for (int i = 0; i < alleles.size(); i++) {
            ar = (AllelesRec) alleles.elementAt(i);
            // check both alleles and count them if matching
            if (ar.name.equals(a1name))
               ar.freq++;
            if (ar.name.equals(a2name))
               ar.freq++;
         }
      }

      public double getAlleleOccur(int index) {
         if (index < 0 || index >= alleles.size())
            return 0.0000;

         AllelesRec ar = (AllelesRec) alleles.elementAt(index);
         return ar.freq ;
      }
   }

   // Constructor parameters
   private int fgid = 0;
   private String directory = null;
   private int vid = 0;
   private String vname = null;
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
   private String pheno_disease = null;
   private String pheno_no_disease = null;
   private float geneFreq;

   // Counters
   private int fcounter = 0;
   private int icounter = 0;
   private int dcounter = 0;

   // Vectors
   private Vector rels = null;
   private Vector mids = null;
   private Vector inds = null;
   private Vector dummys = null;

   // Files

   private int ped_dfid = 0;
   private FileWriter ped_file = null;
   //private String ped_name = "pedigree.txt";
   private String ped_name = "";
   
   private int data_dfid = 0;
   private FileWriter data_file = null;
   //private String data_name = "data.txt";
   private String data_name = "";
   
   private int log_dfid = 0;
   private FileWriter log_file = null;
   //private String log_name = "log.txt";
   private String log_name = "";
   
   private int map_dfid = 0;
   private FileWriter map_file = null;
   //private String map_name = "mapping.txt";
   private String map_name = "";
   
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
   protected static final int UPDATE_INTERVAL = 10;
   protected static final int PROGRESS_ERROR = -1;
   protected static final int ISTART = 0; // Individual sequence start value
   protected static final String FEMALE = "2";
   protected static final String MALE = "1";
   protected static final String UNKNOWN_SEX = "3";
   protected static final String DISEASE_PRESENT = "2";
   protected static final String DISEASE_ABSENT = "1";
   protected static final String DISEASE_UNKNOWN = "0";


   //-------------------------------------------------------------------------------
   public GenLINKAGE(int fgid,
                     float geneFreq, int vid, String disease_present, String disease_absent,
                     String directory, String dburl, String uid, String pwd)
      throws SQLException	{

      this.fgid = fgid;
      this.directory = directory;
      this.DB_URL = dburl;
      this.DB_UID = uid;
      this.DB_PWD = pwd;

      // The output file names are extended with the fgid for result import.
      this.ped_name = "pedigree_" + fgid + ".txt";
      this.data_name = "data_" + fgid + ".txt";
      this.log_name = "log_" + fgid + ".txt";
      this.map_name = "mapping_" + fgid + ".txt";
      
      this.pid = null;
      this.pname = null;
      this.id = null;
      this.uname = null;
      this.mode = null;
      this.msid = null;
      this.msname = null;
      this.pheno_disease = disease_present;
      this.pheno_no_disease = disease_absent;

      this.vid = vid;
      this.geneFreq = geneFreq;

      this.rels = new Vector(1);
      this.mids = new Vector(100);
      this.inds = new Vector(1000);
      this.dummys = new Vector(100);

      this.fcounter = 0;
      this.icounter = 0;
      this.dcounter = 0;

      //DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
      try
      {
          Class.forName("oracle.jdbc.driver.OracleDriver");
          this.conn = DriverManager.getConnection(DB_URL, DB_UID, DB_PWD);
          this.conn.setAutoCommit(false);
      }
      catch (Exception e)
      {}
   }

   //-------------------------------------------------------------------------------
   public void run() {
      int i = 0;
      try {
         readFileGenerationParameters();
         readRelationParameters();
         readDiseaseName();

         log_file = createPhysicalFile(directory, log_name);
         log_dfid = createDataFile(fgid, log_name, id);
         map_file = createPhysicalFile(directory, map_name);
         map_dfid = createDataFile(fgid, map_name, id);
         data_file = createPhysicalFile(directory, data_name);
         data_dfid = createDataFile(fgid, data_name, id);
         ped_file = createPhysicalFile(directory, ped_name);
         ped_dfid = createDataFile(fgid, ped_name, id);
         prepareSQL();

         if (mode.equals("S"))
            singleMode();
         else
            multiMode();

      } catch (Exception e) {
         e.printStackTrace(System.err);
      } finally {
         closePhysicalFile(log_file);
         closePhysicalFile(map_file);
         closePhysicalFile(data_file);
         closePhysicalFile(ped_file);
      }
   }

   //-------------------------------------------------------------------------------
   public void singleMode()
      throws Exception {

      int i = 0;
      int j = 0;
      long t0 = System.currentTimeMillis();
      long t1 = 0;

      try {

         writeLogFileHeader();
         setProgress(log_dfid, 0, 1, 50, 100);
         readMids();
         readInds(0);
         writeLogFileSU(0);

         for (i=0; i < inds.size(); i++)
            checkForMissingParents(i);

         writeMapFile();
         setProgress(map_dfid, 0, 1, 100, 100);

         for (i=0; i < inds.size(); i++) {
            writeIndData(i);
            if(vid != -1) // no loci
            writeDiagnos(i);

            writeGenotypes(i);
            if (lastInFamily(i))
               writeDummys(i);
            if ((i % UPDATE_INTERVAL) == 0) {
               setProgress(ped_dfid, 0, 1, i, inds.size()) ;

               if (aborted()) {
                  writeLogFileError("Generation aborted by user");
                  break;
               }
            }
            yield(); // Give other threads a chance to run
         }

         setProgress(ped_dfid, 0, 1, 100, 100);
         writeDataFile();
         setProgress(data_dfid, 0, 1, 100, 100);

      } catch (Exception e) {
         e.printStackTrace(System.err);
         setProgress(ped_dfid, 0, 1, PROGRESS_ERROR, 100);
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

         for (su=0; su < rels.size(); su++)
            readInds(su);

         for (su=0; su < rels.size(); su++) {
            writeLogFileSU(su);
            checkForMissingMarkerMappings(su);
            for (i=j; i < inds.size(); i++)
               checkForMissingParents(i);
            j = inds.size();
         }

         writeMapFile();
         setProgress(map_dfid, 0, 1, 100, 100);

         for (i=0; i < inds.size(); i++) {
            writeIndData(i);
            writeUDiagnos(i);
            writeUGenotypes(i);
            if (lastInFamily(i))
               writeDummys(i);
            if ((i % UPDATE_INTERVAL) == 0) { 
               setProgress(ped_dfid, 0, 1, i, inds.size());
               if (aborted()) {
                  writeLogFileError("Generation aborted by user");
                  break;
               }
            }
            yield(); // Give other threads a chance to run
         }
	    
         setProgress(ped_dfid, 0, 1, 100, 100);
         writeDataFile();
         setProgress(data_dfid, 0, 1, 100, 100);
	
      } catch (Exception e) {
         e.printStackTrace(System.err);
         setProgress(ped_dfid, 0, 1, PROGRESS_ERROR, 100);
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
   private int createDataFile(int fgid, String name, String id)
      throws SQLException, IOException {
      CallableStatement s = null;
      String message = null;
      int dfid = 0;

      try {
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
   private void readDiseaseName()
      throws SQLException, IOException {
      Statement s = null;
      ResultSet r = null;
      String q = null;

      if(this.vid == -1) //no loci (no variable)
      {
        vname = "None";
        return;
      }
      try {
         if (mode.equals("S"))
            q = "SELECT NAME FROM V_VARIABLES_1"
               + " WHERE VID = " + vid;
         else
            q = "SELECT NAME FROM V_U_VARIABLES_1"
               + " WHERE UVID = " + vid;

         s = conn.createStatement();
         r = s.executeQuery(q);

         if (r.next()) {
            vname = r.getString("NAME");
         } else
            throw new SQLException("No (unified) variable found with (u)vid = " + vid);

      } catch (SQLException sqle) {
         writeLogFileError("SQL ERROR [" + q + "]");
         throw sqle;
      } finally {
         if (r != null) r.close();
         if (s != null) s.close();
      }
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
         q = "SELECT R.SID, R.SUID, R.NAME SUNAME, R.FID, F.NAME FNAME, R.EXPRESSION, R.GSID"
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
            ind.n = "" + (ISTART + icounter);
            ind.su = su;
            ind.family = r.getString("GNAME");
            ind.family_seq = fcounter;
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
	    
      } catch (SQLException sqle) {
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
   public boolean indInFamily(String identity, String fam) {
      int i = 0;

      while (i < inds.size()) {
         IndDataRec ind = (IndDataRec) inds.elementAt(i);
         if (identity.equals(ind.identity))
            return fam.equals(ind.family);
         i++;
      }

      return false;
   }

   //-------------------------------------------------------------------------------
   public String getN(String identity) {
      int i = 0;

      if (identity == null)
         return "0";

      for (i=0; i<inds.size(); i++) {
         IndDataRec ind = (IndDataRec) inds.elementAt(i);
         if (identity.equals(ind.identity))
            return ind.n;
      }
	
      for (i=0; i<dummys.size(); i++) {
         IndDataRec ind = (IndDataRec) dummys.elementAt(i);
         if (identity.equals(ind.identity))
            return ind.n;
      }
	
      return "0";
   }

   //-------------------------------------------------------------------------------
   public String createDummy(String fam, String sex) {
      IndDataRec dummy = null;

      dcounter++;
      dummy = new IndDataRec();
      dummy.n = "" + (ISTART + icounter + dcounter);
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
         if (!indExist(ind.father)) {
            writeLogFileWarning("Father (" + ind.father + ") for "
                                + "individual ("+ ind.identity + ")"
                                + " is not selected by filter and family grouping");
            ind.father = null;
         } else if (!indInFamily(ind.father, ind.family)) {
            writeLogFileWarning("Father (" + ind.father + ") for "
                                + "individual ("+ ind.identity + ")"
                                + " is not a member of the family");
            ind.father = null;
         }
      }

      if (ind.mother != null) {
         if (!indExist(ind.mother)) {
            writeLogFileWarning("Mother (" + ind.mother + ") for "
                                + "individual ("+ ind.identity + ")"
                                + " is not selected by filter and family grouping");
            ind.mother = null;
         } else if (!indInFamily(ind.mother, ind.family)) {
            writeLogFileWarning("Mother (" + ind.mother + ") for "
                                + "individual ("+ ind.identity + ")"
                                + " is not a member of the family");
            ind.mother = null;
         }
      }

      if (ind.father == null && ind.mother != null)
         ind.father = createDummy(ind.family, "M");

      if (ind.father != null && ind.mother == null)
         ind.mother = createDummy(ind.family, "F");
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
   public boolean firstInFamily(int i) {
      if (i == 0)
         return true;
      else {
         IndDataRec curr = (IndDataRec) inds.elementAt(i);
         IndDataRec prev = (IndDataRec) inds.elementAt(i-1);
         return !curr.family.equals(prev.family);
      }
   }

   //-------------------------------------------------------------------------------
   public boolean lastInFamily(int i) {
      if (i == inds.size()-1)
         return true;
      else {
         IndDataRec curr = (IndDataRec) inds.elementAt(i);
         IndDataRec next = (IndDataRec) inds.elementAt(i+1);
         return !curr.family.equals(next.family);
      }
   }

   //-------------------------------------------------------------------------------
   public Vector renumberAlleles(Vector a) {
      int i = 0;
      int imax = 0;


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
      //Encode non positive integers as positive integers...
      for (i=0; i<a.size(); i++) {
         AllelesRec ar = (AllelesRec) a.elementAt(i);
         if (ar.n <= 0)
            ar.n = ++imax;
      }

      //Fill out the holes...
      for (i=0; i<a.size(); i++) {
         AllelesRec ar = (AllelesRec) a.elementAt(i);
         if (ar.n != i + 1) {

            AllelesRec nar = new AllelesRec();
            nar.n = i + 1;
            nar.name = "";
            nar.freq = 0;
            a.insertElementAt(nar, i);
         }
      }
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
            ar = new AllelesRec();
            ar.name = r.getString("NAME");
            ar.freq = 0;
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
            + " ORDER BY TO_NUMBER_ELSE_NULL(NAME), NAME";

         s = conn.createStatement();
         r = s.executeQuery(q);
         while (r.next()) {
            ar = new AllelesRec();
            ar.name = r.getString("NAME");
            ar.freq = 0;
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
            q = "SELECT CID, CNAME, MID, MNAME, POSITION"
               + " FROM V_POSITIONS_2"
               + " WHERE MSID = " + msid
               + " ORDER BY TO_NUMBER_ELSE_NULL(CNAME), POSITION, MNAME";

            s = conn.createStatement();
            r = s.executeQuery(q);
            while (r.next()) {
               mid = new MidDataRec();
               mid.cid = r.getString("CID");
               mid.cname = r.getString("CNAME");
               mid.mid = r.getString("MID");
               mid.mname = r.getString("MNAME");
               mid.alleles = readAlleles(mid.mid, mid.mname);
               if (r.getString("POSITION") == null) {
                  mid.position = -1;
                  writeLogFileWarning("Unknown position for marker [" + mid.mname + "]");
               } else
                  mid.position = r.getDouble("POSITION");
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
            q = "SELECT CID, CNAME, UMID, UMNAME, POSITION"
               + " FROM V_U_POSITIONS_2"
               + " WHERE UMSID = " + msid
               + " ORDER BY TO_NUMBER_ELSE_NULL(CNAME), POSITION, UMNAME";

            s = conn.createStatement();
            r = s.executeQuery(q);
            while (r.next()) {
               mid = new MidDataRec();
               mid.cid = r.getString("CID");
               mid.cname = r.getString("CNAME");
               mid.mid = r.getString("UMID");
               mid.mname = r.getString("UMNAME");
               mid.alleles = readUAlleles(mid.mid, mid.mname);
               if (r.getString("POSITION") == null) {
                  mid.position = -1;
                  writeLogFileWarning("Unknown position for marker [" + mid.mname + "]");
               } else
                  mid.position = r.getDouble("POSITION");
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
   public int countMarkers(String cid) {
      int i = 0;
      int counter = 0;

      for (i=0; i < mids.size(); i++)
         if (cid.equals(((MidDataRec) mids.elementAt(i)).cid))
            counter++;

      return counter;
   }

   //-------------------------------------------------------------------------------
   public int countFamilyMembers(String fam) {
      int i = 0;
      int counter = 0;

      for (i=0; i<inds.size(); i++) {
         IndDataRec ind = (IndDataRec) inds.elementAt(i);
         if (fam.equals(ind.family))
            counter++;
      }

      for (i=0; i<dummys.size(); i++) {
         IndDataRec ind = (IndDataRec) dummys.elementAt(i);
         if (fam.equals(ind.family))
            counter++;
      }
	
      return counter;
   }

   //-------------------------------------------------------------------------------
   private void writeIndData(int i)
      throws Exception {
      IndDataRec ind = (IndDataRec) inds.elementAt(i);
      int j = 0;
      ped_file.write(ind.family_seq + " " +
                     ind.n + " " +
                     getN(ind.father) + " " +
                     getN(ind.mother) + " ");
      if (ind.sex.equals("F"))
         ped_file.write(FEMALE);
      else if (ind.sex.equals("M"))
         ped_file.write(MALE);
      else
         ped_file.write(UNKNOWN_SEX);
      ped_file.write(" ");
   }

   //-------------------------------------------------------------------------------
   private void writeDummys(int indnr)
      throws Exception {
      String fam = ((IndDataRec) inds.elementAt(indnr)).family;
      int fam_seq = ((IndDataRec) inds.elementAt(indnr)).family_seq;
      IndDataRec ind = null;;
      int i = 0;
      int j = 0;
      int k = 0;

      for (i=0; i< dummys.size(); i++) {
         ind = (IndDataRec) dummys.elementAt(i);
         if (fam.equals(ind.family)) {
            ped_file.write(fam_seq + " "+ ind.n + " 0 0 "); // Id, father, mother
            if (ind.sex.equals("F"))
               ped_file.write(FEMALE);
            else if (ind.sex.equals("M"))
               ped_file.write(MALE);
            else
               ped_file.write(UNKNOWN_SEX);
            ped_file.write(" ");


            // diagnose
            if(this.vid != -1) // no loci
            ped_file.write(DISEASE_UNKNOWN);

            // alleleer anatal mark *2 0:or
            for (j = 0; j < mids.size(); j++)
            {
               ped_file.write(" 0 0");
            }

            // radbrytning
            ped_file.write("\n");
         }
      }
   }

   //-------------------------------------------------------------------------------
   public String renumberAllele(int midnr, String allele) {
      int i = 0;
      Vector alleles = (Vector) ((MidDataRec) mids.elementAt(midnr)).alleles;

      if (allele == null)
         return null;
	
      for (i=0; i<alleles.size(); i++) {
         AllelesRec ar = (AllelesRec) alleles.elementAt(i);
         if (allele.equals(ar.name))
            return "" + ar.n;
      }
	
      return null;
   }

   //-------------------------------------------------------------------------------
   private void writeDiagnos(int indnr)
      throws SQLException, IOException {
      ResultSet rset = null;
      IndDataRec ind = (IndDataRec) inds.elementAt(indnr);
      String diagnos = null;
      int i = 0;

      if(this.vid == -1) //no loci (no variable)
      {
        diagnos = "No loci";
        return;
      }


      try {
         vstmt.clearParameters();
         vstmt.setInt(1, Integer.parseInt(ind.iid));
         rset = vstmt.executeQuery();

         if (rset.next()) {
            diagnos = rset.getString("VALUE");
            if (diagnos == null)
               diagnos = DISEASE_UNKNOWN;
            else if (diagnos.equals(pheno_disease))
               diagnos = DISEASE_PRESENT;
            else if (diagnos.equals(pheno_no_disease))
               diagnos = DISEASE_ABSENT;
            else
               diagnos = DISEASE_UNKNOWN;
         } else {
            diagnos = DISEASE_UNKNOWN;
         }

         ped_file.write(diagnos + " ");
      } catch (SQLException sqle) {
         writeLogFileError("writeDiagnos: Fatal SQL Error (iid=" + ind.iid + ")");
         throw sqle;
      } catch (IOException ioe) {
         writeLogFileError("writeDiagnos: Fatal IO Error (iid=" + ind.iid + ")");
         ioe.printStackTrace(System.err);
         throw ioe;
      }
   }

   //-------------------------------------------------------------------------------
   private void writeGenotypes(int indnr)
      throws SQLException, IOException {
      ResultSet rset = null;
      IndDataRec ind = (IndDataRec) inds.elementAt(indnr);
      String a1name = null;
      String a2name = null;
      int i = 0;

      try {
         mstmt.clearParameters();
         mstmt.setInt(1, Integer.parseInt(ind.iid));
         rset = mstmt.executeQuery();
         while (rset.next()) {
            String mid = rset.getString("MID");
            while (! mid.equals(((MidDataRec) mids.elementAt(i)).mid)) {
               ped_file.write("0 0 ");
               i++;
            }
            a1name = rset.getString("A1NAME");
            a2name = rset.getString("A2NAME");

            updateAlleleFreq(ind.sex, (MidDataRec) mids.elementAt(i), a1name, a2name);
		
            a1name = renumberAllele(i, a1name);
            a2name = renumberAllele(i, a2name);
		
            ped_file.write(replaceIfNull(a1name) + " " +
                           replaceIfNull(a2name) + " ");
            i++;
         }

         while (i < mids.size()) {
            ped_file.write("0 0 ");
            i++;
         }
         ped_file.write("\n");

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
   private void writeUDiagnos(int indnr)
      throws SQLException, IOException {
      ResultSet rset = null;
      IndDataRec ind = (IndDataRec) inds.elementAt(indnr);
      RelDataRec rel = (RelDataRec) rels.elementAt(ind.su);
      String diagnos = null;
      int i = 0;

      try {
         uvstmt.clearParameters();
         uvstmt.setInt(1, Integer.parseInt(rel.suid));
         uvstmt.setInt(2, Integer.parseInt(ind.iid));
         rset = uvstmt.executeQuery();

         if (rset.next()) {
            diagnos = rset.getString("VALUE");
            if (diagnos == null)
               diagnos = DISEASE_UNKNOWN;
            else if (diagnos.equals(pheno_disease))
               diagnos = DISEASE_PRESENT;
            else if (diagnos.equals(pheno_no_disease))
               diagnos = DISEASE_ABSENT;
            else
               diagnos = DISEASE_UNKNOWN;
         } else {
            diagnos = DISEASE_UNKNOWN;
         }

         ped_file.write(diagnos + " ");
      } catch (SQLException sqle) {
         writeLogFileError("writeDiagnos: Fatal SQL Error (iid=" + ind.iid + ")");
         throw sqle;
      } catch (IOException ioe) {
         writeLogFileError("writeDiagnos: Fatal IO Error (iid=" + ind.iid + ")");
         ioe.printStackTrace(System.err);
         throw ioe;
      }
   }

   //-------------------------------------------------------------------------------
   private void writeUGenotypes(int indnr)
      throws SQLException, IOException {
      ResultSet rset = null;
      IndDataRec ind = (IndDataRec) inds.elementAt(indnr);
      RelDataRec rel = (RelDataRec) rels.elementAt(ind.su);
      String a1name = null;
      String a2name = null;
      int i = 0;
      try {
         umstmt.clearParameters();
         umstmt.setInt(1, Integer.parseInt(rel.suid));
         umstmt.setInt(2, Integer.parseInt(ind.iid));
         rset = umstmt.executeQuery();
         while (rset.next()) {
            String umid = rset.getString("UMID");
            while (! umid.equals(((MidDataRec) mids.elementAt(i)).mid)) {
               ped_file.write("0 0 ");
               i++;
            }
            a1name = translateAllele(i, rset.getString("AID1"), ind.identity);
            a2name = translateAllele(i, rset.getString("AID2"), ind.identity);

            updateAlleleFreq(ind.sex, (MidDataRec) mids.elementAt(i), a1name, a2name);
		
            a1name = renumberAllele(i, a1name);
            a2name = renumberAllele(i, a2name);
		
            ped_file.write(replaceIfNull(a1name) + " " +
                           replaceIfNull(a2name) + " ");
		
            i++;
         }

         while (i < mids.size()) {
            ped_file.write("0 0 ");
            i++;
         }
         ped_file.write("\n");

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
         log_file.write("Format:     LINKAGE, program code 5 (MLINK)\n");
         if (mode.equals("S")) {
            log_file.write("Mode:       Single\n");
            log_file.write("Disease Variable: " + vname + "\n");
            log_file.write("Marker Set:   " + msname + "\n");
         }
         else {
            log_file.write("Mode:       Multi\n");
            log_file.write("Unified Disease Variable: " + vname + "\n");
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
         System.err.println("message='" + message + "'");
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
      map_file.write("Individual mappings:\n");
      map_file.write("====================\n");
      if (mode.equals("S")) {
         map_file.write("Identity\tNumber\n");
         map_file.write("--------\t------\n");
         for (i=0; i<inds.size(); i++) {
            IndDataRec ind = (IndDataRec) inds.elementAt(i);
            map_file.write(ind.identity + "\t" + ind.n + "\n");
         }
      }
      else {
         map_file.write("Sampling Unit\tIdentity\tNumber\n");
         map_file.write("-------------\t--------\t------\n");
         for (i=0; i<inds.size(); i++) {
            IndDataRec ind = (IndDataRec) inds.elementAt(i);
            RelDataRec rel = (RelDataRec) rels.elementAt(ind.su);

         }
      }
      map_file.write("\n");
      map_file.write("Synthesized Inividuals:\n");
      map_file.write("=======================\n");
      map_file.write("Number\n");
      map_file.write("------\n");
      for (i=0; i<dummys.size(); i++) {
         IndDataRec ind = (IndDataRec) dummys.elementAt(i);
         map_file.write(ind.n + "\n");
      }
      map_file.write("\n");
      map_file.write("Allele mappings:\n");
      map_file.write("================\n");
      map_file.write("Marker");
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
   private void writeDataFile()
      throws IOException {
      DecimalFormat df;
      // The first row is of the form:
      // no loci, risk locus, sexlinked (if 1), program code
     if (this.vid != -1)//no loci
     {
      data_file.write( (mids.size() + 1) + " 0 0 5 " +
                      "<< no loci, risk locus, sexlinked (if 1), program code (5=MLINK)\n");
     }
     else
     {
      data_file.write( (mids.size()) + " 0 0 5 " +
                      "<< no loci, risk locus, sexlinked (if 1), program code (5=MLINK)\n");
     }
      // mutsys, mut male, mut female, disequilibrium
      data_file.write("0 0.0 0.0 0 " +
                      "<< mutsys, mut male, mut female, disequilibrium\n");
      int i;
     if (this.vid != -1)//no loci
     {
      for (i = 1; i <= mids.size(); i++)
         data_file.write(i + " ");
      data_file.write(i + "\n");
      }
      else
      {
          for (i = 1; i <= mids.size()-1; i++)
         data_file.write(i + " ");
          data_file.write(i + "\n");

      }
      if(this.vid != -1) // no loci
      {
      // - - - - - - - - - - - -  affection, illness loci - - - - - - - - -
      data_file.write("1 2 << affection, no of alleles\n");
      // Gene frequency, disease
      df = new DecimalFormat("0.##");
      // For some ridiculous reason, java formats number in according to your
      // locale-settings. It's easier to just replace a comma with the dot, instead
      // of changing to a different country setting.
      data_file.write(geneFreq + " " + df.format(1-geneFreq).replace(',', '.') +
                      " << gene freq, disease\n");
      // Liablity classes
      data_file.write("1 << no of liability classes\n");
      data_file.write("0 0 1.00 << penetrance\n");
      }
      // - - - - - - - - - - - - Marker data - - - - - - - - - - - - - - -
      MidDataRec mr;
      df = new DecimalFormat("0.#####");
      for (i = 0; i < mids.size(); i++) {
         mr = (MidDataRec) mids.elementAt(i);
         // The numeric code for numbered alleles is 3, hence the hard coded 3 on the next line
         data_file.write("3 " + mr.alleles.size() + " # " + mr.mname + "\n");
         Vector alleleFreqs = getAlleleFreqs(mr);
         int j = 0;
         for (j = 0; j < alleleFreqs.size(); j++) {
            data_file.write(df.format( ((Double) alleleFreqs.elementAt(j)).doubleValue()).replace(',', '.') + " ");
         }
         data_file.write("\n");
      }

      //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
      // Sex differance, interfernce
      data_file.write("0 0 << sex difference, interfernce (if 1 or 2)\n");
      // Recomb values
      double dist;
      MidDataRec mr1, mr2;

      // The first distance is the distance between the disease locus and the first marker.
      // For some unknown reason (according to Ingrid Kockum) it's alright to set this distance
      // to 10cM.
       if(this.vid != -1) // no loci
      {
        data_file.write("10 ");
      }
      for (i = 0; i < mids.size() - 1; i++) {
         mr1 = (MidDataRec) mids.elementAt(i);
         mr2 = (MidDataRec) mids.elementAt(i+1);

         if (mr1.position < 0 || mr2.position < 0 ||
             !mr1.cid.equals(mr2.cid))
            dist = 50;
         else
            dist = mr2.position - mr1.position;
         data_file.write(df.format(dist).replace(',', '.') + " ");
      }
      data_file.write(" << recomb values\n");
      // Something at the end of the file
      if(this.vid != -1) // no loci
      {
        data_file.write("1 0.1 0.45\n");
      }
      else
      {
        data_file.write("0.1 0.45\n");
      }
   }

   //-------------------------------------------------------------------------------
   private void prepareSQL()
      throws SQLException {

      vstmt = conn.prepareStatement("SELECT VID, VALUE FROM V_PHENOTYPES_1"
                                    + " WHERE VID=" + vid
                                    + "   AND IID=?");

      uvstmt = conn.prepareStatement("SELECT P.VID, P.VALUE"
                                     + " FROM V_R_UVID_VID_1 R, V_PHENOTYPES_1 P"
                                     + " WHERE R.UVID=" + vid
                                     + "   AND R.SUID=?"
                                     + "   AND R.VID=P.VID"
                                     + "   AND P.IID=?");

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

      pstmt = conn.prepareCall("{CALL GDBP.SET_DATA_FILE_STATUS(?,?,?)}");
   }

   //-------------------------------------------------------------------------------
   private void setProgress(int dfid, int su, int sut, int n, int t) 
      throws SQLException, IOException {
      String message = null;
      String progress = null;

      if (n == PROGRESS_ERROR)
         progress = "ERROR";
      else if (su+1 == sut && n == t)
         progress = "DONE";
      else 
         progress = (su*100/sut) + (n*100/sut/t) + " %";

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

   //-------------------------------------------------------------------------------
   private String replaceIfNull(String val) {
      if (val != null)
         return val;
      else
         return "0";
   }

   //-------------------------------------------------------------------------------
   private void updateAlleleFreq(String sex, MidDataRec mid,
                                 String a1name, String a2name) {
      mid.incAllelesOccur(a1name, a2name);
   }

   //-------------------------------------------------------------------------------
   private Vector getAlleleFreqs(MidDataRec mid) {
      Vector ret = new Vector();
      Vector alleles = mid.alleles;
      int alleleOccur = 0;
      int alleleSum = 0;
      Double alleleFreq;

      //count the sum of all allele occurences
      for (int i = 0; i < alleles.size(); i++)
         alleleSum += ((AllelesRec) alleles.elementAt(i)).freq;

      //calculate each allele frequence
      for (int i = 0; i < alleles.size(); i++) {
         alleleOccur = ((AllelesRec) alleles.elementAt(i)).freq;
         if (alleleSum > 0)
            alleleFreq = new Double(alleleOccur/((double) alleleSum));
         else
            alleleFreq = new Double(1/((double) alleles.size()));
         ret.addElement(alleleFreq);
      }

      return ret;
   }

}
