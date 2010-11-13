package org.tgdb.dtos;

public class TgDbModelDTO implements java.io.Serializable {
    
    private int eid;
    private String acc, line, designation, mutations;
    
    public TgDbModelDTO(){}
    
    public int getEid(){
        return eid;
    }
    
    public void setEid(int eid){
        this.eid = eid;
    }
    
    public String getAcc() {
        return acc;
    }
    
    public void setAcc(String acc){
        this.acc = acc;
    }
      
    public String getLine() {
        return line;
    }
    
    public void setLine(String line){
        this.line = line;
    }
    
    public String getDesignation(){
        return designation;
    }
    
    public void setDesignation(String designation){
        this.designation = designation;
    }
    
    public String getMutations(){
        return mutations;
    }
    
    public void setMutations(String mutations){
        this.mutations = mutations;
    }
    
}
