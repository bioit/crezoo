package se.arexis.agdb.util;

public class GenoDiff {
  private String m_marker;
  private String m_identity;
  private int m_suid;

  private String m_oldAllele1;
  private String m_oldAllele2;
  private String m_oldRaw1;
  private String m_oldRaw2;
  private int m_oldLevel;
  private String m_oldRef;
  private String m_oldComm;
  private String m_oldUsr;

  private String m_newAllele1;
  private String m_newAllele2;
  private String m_newRaw1;
  private String m_newRaw2;
  private int m_newLevel;
  private String m_newRef;
  private String m_newComm;
  private int m_newId;


  public String getIdentity() {
    return m_identity;
  }

  public void setIdentity(String newIdentity) {
    m_identity = newIdentity;
  }

  public String getMarker() {
    return m_marker;
  }

  public void setMarker(String newMarker) {
    m_marker = newMarker;
  }

  public String getNewAllele1() {
    return m_newAllele1;
  }

  public void setNewAllele1(String newAllele1) {
    m_newAllele1 = newAllele1;
  }

  public String getNewAllele2() {
    return m_newAllele2;
  }

  public void setNewAllele2(String newAllele2) {
    m_newAllele2 = newAllele2;
  }

  public String getNewComm() {
    return m_newComm;
  }

  public void setNewComm(String newComm) {
    m_newComm = newComm;
  }

  public int getNewId() {
    return m_newId;
  }

  public void setNewId(int newM_newId) {
    m_newId = newM_newId;
  }

  public int getNewLevel() {
    return m_newLevel;
  }

  public void setNewLevel(int newM_newLevel) {
    m_newLevel = newM_newLevel;
  }

  public String getNewRaw1() {
    return m_newRaw1;
  }

  public void setNewRaw1(String newM_newRaw1) {
    m_newRaw1 = newM_newRaw1;
  }

  public String getNewRaw2() {
    return m_newRaw2;
  }

  public void setNewRaw2(String newM_newRaw2) {
    m_newRaw2 = newM_newRaw2;
  }

  public String getNewRef() {
    return m_newRef;
  }

  public void setNewRef(String newM_newRef) {
    m_newRef = newM_newRef;
  }

  public String getOldAllele1() {
    return m_oldAllele1;
  }

  public void setOldAllele1(String newAllele1) {
    m_oldAllele1 = newAllele1;
  }

  public String getOldAllele2() {
    return m_oldAllele2;
  }

  public void setOldAllele2(String newAllele2) {
    m_oldAllele2 = newAllele2;
  }

  public String getOldComm() {
    return m_oldComm;
  }

  public void setOldComm(String newM_oldComm) {
    m_oldComm = newM_oldComm;
  }

  public String getOldUsr() {
    return m_oldUsr;
  }

  public void setOldUsr(String newUsr) {
      m_oldUsr = newUsr;
  }

  public int getOldLevel() {
    return m_oldLevel;
  }

  public void setOldLevel(int newM_oldLevel) {
    m_oldLevel = newM_oldLevel;
  }

  public String getOldRaw1() {
    return m_oldRaw1;
  }

  public void setOldRaw1(String newM_oldRaw1) {
    m_oldRaw1 = newM_oldRaw1;
  }

  public String getOldRaw2() {
    return m_oldRaw2;
  }

  public void setOldRaw2(String newM_oldRaw2) {
    m_oldRaw2 = newM_oldRaw2;
  }

  public String getOldRef() {
    return m_oldRef;
  }

  public void setOldRef(String newM_oldRef) {
    m_oldRef = newM_oldRef;
  }

  public int getSuid() {
    return m_suid;
  }

  public void setSuid(int newM_suid) {
    m_suid = newM_suid;
  }
}
