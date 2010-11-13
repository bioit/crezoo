package se.arexis.agdb.util;
import java.util.*;
import java.sql.*;

public class GqlTranslator {
    private StringBuffer m_from = null;
    private StringBuffer m_where1 = null;
    private StringBuffer m_where2 = null;
    private String m_gqlstring = null;
    private GqlScanner m_gqlsc = null;
    private Hashtable m_vids = null;
    private Hashtable m_vidTables = null;
    private Hashtable m_mids = null;
    private Hashtable m_midTables = null;
    private String m_pid = null;
    private String m_suid = null;
    private String m_gsid = null;
    private Vector m_comments = null; // Contains errors and warnings
    private Vector m_code_snippet = null; // Contains the corresponding code
    private Connection m_conn = null;
    
    // Returns all warnings and error that has been encounterd during translation
    public Vector getComments() {
        return (Vector) m_comments.clone();
    }
    // Returns all the corresponding code snippets to the warnings and errors
    public Vector getCode() {
        return (Vector) m_code_snippet.clone();
    }
    
    public GqlTranslator(String pid, String suid, String gqlstring, Connection conn) {
        m_gqlstring = gqlstring.replace("\"","'");
        m_pid = pid;
        m_suid = suid;
        m_gsid = null;
        m_conn = conn;
        m_gqlsc = new GqlScanner(m_gqlstring);
        m_vidTables = new Hashtable();
        m_vids = new Hashtable();
        m_midTables = new Hashtable();
        m_mids = new Hashtable();
        m_from = new StringBuffer(512);
        m_where1 = new StringBuffer(512);
        m_where2 = new StringBuffer(512);
        m_comments = new Vector();
        m_code_snippet = new Vector();
    }
    
    public GqlTranslator(String pid, String suid, String gsid, String gqlstring, Connection conn) {
        m_gqlstring = gqlstring.replace("\"","'");
        m_pid = pid;
        m_suid = suid;
        m_gsid = gsid;
        m_conn = conn;
        m_gqlsc = new GqlScanner(m_gqlstring);
        m_vidTables = new Hashtable();
        m_vids = new Hashtable();
        m_midTables = new Hashtable();
        m_mids = new Hashtable();
        m_from = new StringBuffer(512);
        m_where1 = new StringBuffer(512);
        m_where2 = new StringBuffer(512);
        m_comments = new Vector();
        m_code_snippet = new Vector();
    }
    
    public void translate() {
        m_comments.removeAllElements();
        m_code_snippet.removeAllElements();
        String token = null;
        // First we add the constraint that individuals must be part of this sampling unit
        m_from.append("V_INDIVIDUALS_GQL ind ");
        m_where1.append("ind.pid = " + m_pid + " AND ind.suid = " + m_suid + " ");
        if (m_gsid != null) {
            m_from.append(", V_R_IND_GRP_2 grp ");
            m_where1.append("AND ind.iid = grp.iid AND grp.gsid = " + m_gsid + " ");
        }
        while (m_gqlsc.hasMoreTokens()) {
            token = m_gqlsc.getNextToken();
            dispatchToken(token);
        }
    }
    
    public String getFilter() {
        StringBuffer output = new StringBuffer(2000);
        output.append("FROM\n")
        .append(m_from.toString())
        .append("\nWHERE\n")
        .append(m_where1.toString())
        .append(" AND\n(\n")
        .append(m_where2.toString())
        .append(")");
        return output.toString();
    }
    
    private void dispatchToken(String token) {
        // I suppose it is enough if we recognise the standard tokens S., I., P. and G.
        if (token.regionMatches(0, "S.", 0, 2)) { // Set command
            dispatchSet(token.substring(2));
        } else if (token.regionMatches(0, "I.", 0, 2)) { // Individual constraints
            dispatchInd(token.substring(2));
        } else if (token.regionMatches(0, "P.", 0, 2)) { // Phenotype constraints
            dispatchPhe(token.substring(2));
        } else if (token.regionMatches(0, "G.", 0, 2)) { // Genotype constraints
            dispatchGen(token.substring(2));
        } else {
            m_where2.append(token + " ");
        }
        
    }
    
    private void dispatchSet(String token) {
        String grouping = null;
        String group = null;
        int suid = 0;
        int gsid = 0;
        int gid = 0;
        int index = 0;
        index = token.indexOf(".");
        if (index > 0) {
            grouping = token.substring(0, index);
            token = token.substring(index + 1);
            if (token.length() > 0) {
                group = token.toString();
            }
        } else { // There are no group specified, that is token only contains tha grouping name
            grouping = token;
        }
        
        // Now, let's find out the gsid and the gid;
        Statement stmt = null;
        ResultSet rset = null;
        try {
            if (grouping != null) {
                stmt = m_conn.createStatement();
                rset = stmt.executeQuery("SELECT GSID FROM V_GROUPINGS_1 WHERE NAME='" + grouping + "' AND suid=" + m_suid);
                if (rset.next()) {
                    gsid = rset.getInt("GSID");
                } else {
                    m_comments.addElement("Warning: Grouping doesn't exist for this sampling unit.");
                    m_code_snippet.addElement("S." + grouping);
                    gsid = -1;
                }
                rset.close();
                stmt.close();
                
                if (group != null) {
                    stmt = m_conn.createStatement();
                    rset = stmt.executeQuery("SELECT GID FROM V_GROUPS_1 WHERE NAME='" + group + "' AND gsid=" + gsid);
                    if (rset.next()) {
                        gid = rset.getInt("GID");
                    } else {
                        m_comments.addElement("Warning: Group doesn't exist");
                        m_code_snippet.addElement("S." + grouping + "." + group);
                        gid = -1;
                    }
                }
            }
        } catch (SQLException e) {
            // write something smart
            m_comments.addElement("Error: Syntax error.");
            m_code_snippet.addElement("S." + token);
        } finally {
            try {
                if (rset != null) rset.close();
                if (stmt != null) stmt.close();
            } catch (SQLException ignored) {}
        }
        m_where2.append("\n\t(ind.iid IN (SELECT DISTINCT iid FROM V_SETS_GQL WHERE suid=" + m_suid + " ");
        if (grouping != null)
            m_where2.append("AND GSID=" + gsid + " ");
        if (group != null)
            m_where2.append("AND GID=" + gid + " ");
        
        m_where2.append(") )\n");
        
    }
    
    private void dispatchInd(String token) {
        String oper = null;
        if ("IDENTITY".equalsIgnoreCase(token))
            oper = "IDENTITY";
        else if ("IID".equalsIgnoreCase(token))
            oper = "IID";
        else if ("ALIAS".equalsIgnoreCase(token))
            oper = "ALIAS";
        else if ("BIRTH_DATE".equalsIgnoreCase(token))
            oper = "BIRTH_DATE";
        else if ("MOTHER".equalsIgnoreCase(token))
            oper= "MIDENTITY";
        else if ("FATHER".equalsIgnoreCase(token))
            oper = "FIDENTITY";
        else if ("SEX".equalsIgnoreCase(token))
            oper = "SEX";
        else if (token.length() == 0)
            oper = "ALIAS";
        else
            oper = "";
        
        m_where2.append("ind." + oper + " ");
    }
    private void dispatchPhe(String token) {
        String vname = null;
        String oper = null;
        String tableName = null;
        int vid = 0;
        int index = 0;
        index = token.indexOf(".");
        if (index > 0) {
            vname = token.substring(0, index);
            oper = token.substring(index + 1);
            if ("VALUE".equalsIgnoreCase(oper))
                oper = "VALUE";
            else if ("DATE".equalsIgnoreCase(oper))
                oper = "DATE_";
            else {
                m_comments.addElement("Error: Unknown suffix '" + oper + "'.");
                m_code_snippet.addElement("P." + token);
                oper = "VALUE"; // Why not?
            }
            
        } else { //
            vname = token.toString();
            oper = "VALUE";
        }
        
        // Check if this variable already has been used
        if (m_vids.containsKey(vname)) {
            vid = Integer.parseInt( (String) m_vids.get(vname));
            tableName = (String) m_vidTables.get(Integer.toString(vid));
            // This table has alredy been added to the from-String-Buffer and the join with
            // individuals has also been done.
        } else {
            // This is the first occurence of this variable.
            Statement stmt = null;
            ResultSet rset = null;
            try {
                stmt = m_conn.createStatement();
                rset = stmt.executeQuery("SELECT VID FROM V_VARIABLES_1 WHERE SUID=" + m_suid + " AND NAME='" + vname + "'");
                if (rset.next()) {
                    vid = rset.getInt("VID");
                } else {
                    m_comments.addElement("Warning: Variable doesn't exist for this sampling unit.");
                    m_code_snippet.addElement("P." + vname);
                    vid = -1;
                }
                
            } catch (SQLException e) {
                m_comments.addElement("Error: Syntax error.");
                m_code_snippet.addElement("P." + token);
            } finally {
                try {
                    if (rset != null) rset.close();
                    if (stmt != null) stmt.close();
                } catch (SQLException ignored) {}
            }
            tableName = "P" + (m_vids.size() + 1);
            m_vids.put(vname, Integer.toString(vid));
            m_vidTables.put(Integer.toString(vid), tableName);
            m_from.append(", V_PHENOTYPES_GQL " + tableName + " ");
            m_where1.append("AND\n")
            .append(tableName + ".iid=ind.iid AND\n")
            .append(tableName + ".vid=" + vid + " ");
        }
        m_where2.append(tableName + "." + oper + " ");
    }
    private void dispatchGen(String token) {
        String mname = null;
        String oper = null;
        String tableName = null;
        int mid = 0;
        int index = 0;
        index = token.indexOf(".");
        if (index > 0) {
            mname = token.substring(0, index);
            oper = token.substring(index + 1);
            if ("A1".equalsIgnoreCase(oper))
                oper = "A1NAME";
            else if ("R1".equalsIgnoreCase(oper))
                oper = "RAW1";
            else if ("A2".equalsIgnoreCase(oper))
                oper = "A2NAME";
            else if ("R2".equalsIgnoreCase(oper))
                oper = "RAW2";
            else {
                m_comments.addElement("Error: Unknown suffix '" + oper + "'.");
                m_code_snippet.addElement("G." + token);
                oper = "A1NAME"; // Why not?
            }
            
        } else { // Error!!! marker name must be followed by any of the following A1, A2, R1 or R2
            // Do something damn it
            m_comments.addElement("Error: Marker not specified.");
            m_code_snippet.addElement("G." + token);
            return;
        }
        
        // Check if this marker already has been used
        if (m_mids.containsKey(mname)) {
            // Alrighty than
            mid = Integer.parseInt( (String) m_mids.get(mname));
            tableName = (String) m_midTables.get(Integer.toString(mid));
            // This table has alredy been added to the from-String-Buffer and the join with
            // individuals has also been done.
        } else {
            // This is the first occurence of this marker.
            Statement stmt = null;
            ResultSet rset = null;
            try {
                stmt = m_conn.createStatement();
                rset = stmt.executeQuery("SELECT MID FROM V_MARKERS_1 WHERE SUID=" + m_suid + " AND NAME='" + mname + "'");
                if (rset.next()) {
                    mid = rset.getInt("MID");
                } else {
                    m_comments.addElement("Warning: Marker doesn't exist for this sampling unit.");
                    m_code_snippet.addElement("G." + mname);
                    mid = -1;
                }
                
            } catch (SQLException e) {
                m_comments.addElement("Error: Syntax error.");
                m_code_snippet.addElement("G." + token);
            } finally {
                try {
                    if (rset != null) rset.close();
                    if (stmt != null) stmt.close();
                } catch (SQLException ignored) {}
            }
            tableName = "G" + (m_mids.size() + 1);
            m_mids.put(mname, Integer.toString(mid));
            m_midTables.put(Integer.toString(mid), tableName);
            m_from.append(", V_GENOTYPES_GQL " + tableName + " ");
            m_where1.append("AND\n")
            .append(tableName + ".iid=ind.iid AND\n")
            .append(tableName + ".mid=" + mid + " ");
        }
        m_where2.append(tableName + "." + oper + " ");
    }
}
