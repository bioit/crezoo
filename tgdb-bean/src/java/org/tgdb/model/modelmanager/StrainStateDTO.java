/*
 * StrainTypeDTO.java
 *
 * Created on July 12, 2006, 10:40 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tgdb.model.modelmanager;

import org.tgdb.model.strain.state.StrainStateRemote;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 *
 * @author se22519
 */
public class StrainStateDTO implements Serializable, Comparable
{
    private int id;
    private String name;
    private String abbreviation;
    
    /** Creates a new instance of StrainTypeDTO */
    public StrainStateDTO(StrainStateRemote state) {
        try
        {
            id = state.getId();
            name = state.getName();
            abbreviation = state.getAbbreviation();
        } 
        catch (RemoteException ex)
        {
            ex.printStackTrace();
        }
    }

    public int compareTo(Object anotherObj)
    {
        if(!(anotherObj instanceof StrainStateDTO))
            throw new ClassCastException("Object is of wrong class. StrainStateDTO object expected but not found.");
        return getName().compareTo(((StrainStateDTO)anotherObj).getName());
    }
    
    public boolean equals(Object obj)
    {
        if (((StrainStateDTO)obj).id == this.id)
            return true;
        else 
            return false;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getAbbreviation()
    {
        return abbreviation;
    }
}
