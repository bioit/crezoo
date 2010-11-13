package org.tgdb.frame;

import java.io.Serializable;
import java.util.HashMap;
import org.apache.log4j.Logger;

public class Caller implements Serializable {
    
    protected static Logger logger = Logger.getLogger(Caller.class);
    
    protected int id;
    
    private String name;
    
    private String usr;
    
    private String pwd;
    
    private String email;
    
    protected boolean isAdmin;
    
    protected HashMap privs;
    
    protected HashMap properties;
    
    public Caller() {
        properties = new HashMap();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getUsr() {
        return usr;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean hasPrivilege(String privilegeName) {
        try {            
            long t1 = System.currentTimeMillis();
            
            if (privs.containsKey(privilegeName)) {
                long t2 = System.currentTimeMillis();
                logger.debug("---------------------------------------->Caller#hasPrivilege: Fetched privilege '"+privilegeName+"' in "+(t2-t1)+" ms");
                return true;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void updatePrivileges() {
        logger.warn("---------------------------------------->Caller#hasPrivilege: Override method");
    }
    
    public void setAttribute(String key, String value) {
        properties.put(key, value);
    }
    
    public String getAttribute(String key) {
        return (String)properties.get(key);
    }
    
    public void setAttribute(String key, int value) {
        properties.put(key, new Integer(value));
    }
    
    public int getAttributeInt(String key) {
        return ((Integer)properties.get(key)).intValue();
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }
}
