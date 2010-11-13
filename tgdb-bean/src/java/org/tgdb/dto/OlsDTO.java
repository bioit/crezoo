package org.tgdb.dto;

import java.io.Serializable;

public class OlsDTO implements Serializable {
    
    private String oid, name, namespace;
    
    public OlsDTO() {}
    
    public String getOid() {
        return oid;
    }
    
    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
}
