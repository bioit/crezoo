/*
 * SpeciesDTO.java
 *
 * Created on January 11, 2006, 11:20 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.adminmanager;

import org.tgdb.species.species.SpeciesRemote;
import java.io.Serializable;

/**
 *
 * @author heto
 */
public class SpeciesDTO implements Serializable, Comparable {
    
    private int sid;

    private String name;

    private String comm;
    
    /** Creates a new instance of SpeciesDTO */
    public SpeciesDTO(SpeciesRemote spc) {
        try
        {
            sid = spc.getSid();
            name = spc.getName();
            comm = spc.getComm();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public int getSid() {
        return sid;
    }

    public String getName() {
        return name;
    }

    public String getComm() {
        return comm;
    }
    
    public int compareTo(Object anotherObj) throws ClassCastException {
        if(!(anotherObj instanceof SpeciesDTO))
            throw new ClassCastException("Object is of wrong class. SpeciesDTO object expected but not found.");
        return sid - ((SpeciesDTO)anotherObj).getSid();
    }    
    
    public boolean equals(Object o)
    {
        if(!(o instanceof SpeciesDTO))
            throw new ClassCastException("Object is of wrong class. SpeciesDTO object expected but not found.");
        if (sid==((SpeciesDTO)o).getSid())
            return true;
        return false;
    }
}
