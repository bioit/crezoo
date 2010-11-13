package org.tgdb.model.modelmanager;

public class StrainLinkDTO {
    private int id, strainid;
    private String repository, externalid, strainurl;
    
    
    public StrainLinkDTO(int id, int strainid, String repository, String externalid, String strainurl) {
        this.id = id;
        this.strainid = strainid;
        this.repository = repository;
        this.externalid = externalid;
        this.strainurl = strainurl;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the strainid
     */
    public int getStrainid() {
        return strainid;
    }

    /**
     * @param strainid the strainid to set
     */
    public void setStrainid(int strainid) {
        this.strainid = strainid;
    }

    /**
     * @return the repository
     */
    public String getRepository() {
        return repository;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(String repository) {
        this.repository = repository;
    }

    /**
     * @return the externalid
     */
    public String getExternalid() {
        return externalid;
    }

    /**
     * @param externalid the externalid to set
     */
    public void setExternalid(String externalid) {
        this.externalid = externalid;
    }

    /**
     * @return the strainurl
     */
    public String getStrainurl() {
        return strainurl;
    }

    /**
     * @param strainurl the strainurl to set
     */
    public void setStrainurl(String strainurl) {
        this.strainurl = strainurl;
    }

    
}
