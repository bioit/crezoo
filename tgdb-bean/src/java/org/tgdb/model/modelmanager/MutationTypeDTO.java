package org.tgdb.model.modelmanager;

import org.tgdb.model.strain.mutationtype.MutationTypeRemote;
import java.io.Serializable;

public class MutationTypeDTO implements Serializable {
    private int mutantid;
    private String abbreviation;
    private String name;
    
    public MutationTypeDTO(MutationTypeRemote mut) {
        try {
            mutantid = mut.getMutantid();
            abbreviation = mut.getAbbreviation();
            name = mut.getName();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @deprecated use getId() instead
     */
    public int getMutantid()
    {
        return mutantid;
    }
    
    public int getId()
    {
        return mutantid;
    }

    public String getAbbreviation()
    {
        return abbreviation;
    }

    public String getName()
    {
        return name;
    }
    
}
