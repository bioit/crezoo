package org.tgdb.dtos;

public class TgDbGeneDTO implements java.io.Serializable {
    
    private int gid;
    private String name, symbol, chromosome, mgiid;
    
    public TgDbGeneDTO(){}
    
    public int getGid(){
        return gid;
    }
    
    public void setGid(int gid){
        this.gid = gid;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getSymbol(){
        return symbol;
    }
    
    public void setSymbol(String symbol){
        this.symbol = symbol;
    }
    
    public String getChromosome(){
        return chromosome;
    }
    
    public void setChromosome(String chromosome){
        this.chromosome = chromosome;
    }
    
    public String getMgiid(){
        return mgiid;
    }
    
    public void setMgiid(String mgiid){
        this.mgiid = mgiid;
    }
    
}
