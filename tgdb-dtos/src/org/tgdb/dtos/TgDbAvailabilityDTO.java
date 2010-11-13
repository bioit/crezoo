package org.tgdb.dtos;

public class TgDbAvailabilityDTO implements java.io.Serializable {
    
    private String repository, repositorylink, background, state, type;
    
    public TgDbAvailabilityDTO(){}
    
    public String getRepository(){
        return repository;
    }
    
    public void setRepository(String repository){
        this.repository = repository;
    }
    
    public String getRepositorylink(){
        return repositorylink;
    }
    
    public void setRepositorylink(String repositorylink){
        this.repositorylink = repositorylink;
    }
    
    public String getBackground(){
        return background;
    }
    
    public void setBackground(String background){
        this.background = background;
    }
    
    public String getState(){
        return state;
    }
    
    public void setState(String state){
        this.state = state;
    }
    
    public String getType(){
        return type;
    }
    
    public void setType(String type){
        this.type = type;
    }
}
