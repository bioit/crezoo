package org.tgdb.model.modelmanager;

public class PromoterLinkDTO {
    private int id, pid;
    private String repository, externalid, strainurl;
    
    
    public PromoterLinkDTO(int id, int pid, String repository, String externalid, String strainurl) {
        this.id = id;
        this.pid = pid;
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
    public int getPid() {
        return pid;
    }

    /**
     * @param strainid the strainid to set
     */
    public void setPid(int pid) {
        this.pid = pid;
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
