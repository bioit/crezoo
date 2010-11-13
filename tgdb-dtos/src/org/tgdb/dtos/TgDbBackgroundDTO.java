package org.tgdb.dtos;

public class TgDbBackgroundDTO implements java.io.Serializable {
    
    private String dna, target, host, backcross, backcrosses;
    
    public TgDbBackgroundDTO(){}

    public String getDna() {
        return dna;
    }

    public void setDna(String dna) {
        this.dna = dna;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getBackcross() {
        return backcross;
    }

    public void setBackcross(String backcross) {
        this.backcross = backcross;
    }

    public String getBackcrosses() {
        return backcrosses;
    }

    public void setBackcrosses(String backcrosses) {
        this.backcrosses = backcrosses;
    }
}
