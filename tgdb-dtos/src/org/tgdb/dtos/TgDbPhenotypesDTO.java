package org.tgdb.dtos;

public class TgDbPhenotypesDTO implements java.io.Serializable {
    
    private String phenotype;
    
    public TgDbPhenotypesDTO(){}
    
    public String getPhenotype(){
        return phenotype;
    }
    
    public void setPhenotype(String phenotype){
        this.phenotype = phenotype;
    }
}
