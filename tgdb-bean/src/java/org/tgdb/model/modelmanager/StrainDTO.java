package org.tgdb.model.modelmanager;

import org.tgdb.model.strain.state.StrainStateRemote;
import org.tgdb.model.strain.strain.StrainRemote;
import org.tgdb.model.strain.type.StrainTypeRemote;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public class StrainDTO implements Serializable, Comparable {
    
    private int strainid, models;
    
    private String designation, designation_ss, strain_links_string, strainTypeName, strainStateNames;
    
    public StrainDTO(StrainRemote strain) {
        try
        {
            strainid = strain.getStrainid();

            designation = strain.getDesignation();
            
            designation_ss = strain.getDesignation().replaceAll("<","&lt;").replaceAll(">","&gt;");
            designation_ss = designation_ss.replaceAll("&lt;","<sup>").replaceAll("&gt;","</sup>");

            strain_links_string = strain.getStrain_links_string();

            models = strain.getModels();
            
            Collection types = strain.getTypes();
            
            int j=0;
            StrainTypeRemote type;
            Iterator i = types.iterator();
            while (i.hasNext())
            {
                type = (StrainTypeRemote)i.next();
                if (j!=0)
                    strainTypeName += ", ";
                strainTypeName += type.getName();
                j++;
            }
            
            Collection states = strain.getStates();
            j=0;
            StrainStateRemote state;
            i = states.iterator();
            while (i.hasNext())
            {
                state = (StrainStateRemote)i.next();
                if (j!=0)
                    strainStateNames += ", ";
                strainStateNames += state.getName();
                j++;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        
    }
    
    public int compareTo(Object anotherObj) {
        if(!(anotherObj instanceof StrainDTO))
            throw new ClassCastException("Object is of wrong class. StrainDTO object expected but not found.");
        return getDesignation().compareTo(((StrainDTO)anotherObj).getDesignation());
    }
    
    @Override
    public boolean equals(Object obj){
        if (obj instanceof StrainDTO && ((StrainDTO)obj).strainid == this.strainid)
            return true;
        else 
            return false;
    }

    public int getStrainId() {
        return strainid;
    }

    public String getDesignation() {
        return designation;
    }

    public String getDesignation_ss() {
        return designation_ss;
    }

    public int getModels() {
        return models;
    }
    
    public String getStrainTypeNames(){
        return strainTypeName;
    }
    
    public String getStrainStateNames()
    {
        return strainStateNames;
    }
    
//    public String getMgiId()
//    {
//        return mgiid;
//    }

    public String getStrain_links_string() {
        return this.strain_links_string;
    }
    
}
