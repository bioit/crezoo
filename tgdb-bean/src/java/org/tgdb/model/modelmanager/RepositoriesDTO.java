package org.tgdb.model.modelmanager;

import org.tgdb.model.repositories.RepositoriesRemote;
import java.io.Serializable;

public class RepositoriesDTO implements Serializable {
    private int rid, pid, hasdb;
    private String reponame, mouseurl, repourl;
    
    
    /** Creates a new instance of RepositoriesDTO */
public RepositoriesDTO(RepositoriesRemote repository) {
        try{
            rid = repository.getRid();
            reponame = repository.getReponame();
            pid = repository.getPid();
            hasdb = repository.getHasdb();
            mouseurl = repository.getMouseurl();
            repourl = repository.getRepourl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRid(){
        return rid;
    }
    
    public java.lang.String getReponame(){
        return reponame;
    }
    
    public int getPid(){
        return pid;
    }
    
    public int getHasdb(){
        return hasdb;
    }
    
    public java.lang.String getMouseurl(){
        return mouseurl;
    }
    
    public java.lang.String getRepourl(){
        return repourl;
    }
}
