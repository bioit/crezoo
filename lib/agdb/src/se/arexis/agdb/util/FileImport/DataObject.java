/*
 * DataObject.java
 *
 * Created on May 5, 2003, 2:43 PM
 */
package se.arexis.agdb.util.FileImport;

import se.arexis.agdb.util.Errors;

/** DataObject is an object to provide a "temporary database". This object is later
 * used to check if importing of data is going to succed.
 * @author heto
 */
public class DataObject
{
    
    /** Store information about individual data. */    
    private IndividualDataObject ind = new IndividualDataObject();
    
    /** Store information about phenotype data */    
    private PhenotypeDataObject phen = new PhenotypeDataObject();
    
    /** Store information about allele data */    
    private AlleleDataObject allele = new AlleleDataObject();
    
    /** Store information about genotype data */    
    private GenotypeDataObject gen = new GenotypeDataObject();
    
    /** Store information about marker data */    
    private MarkerDataObject mark = new MarkerDataObject();
    
    /** Store information about variable data */
    private VariableDataObject var = new VariableDataObject();
    
    /** Store information about grouping data */
    private GroupingDataObject grp = new GroupingDataObject();
    
    /** Store information about sample data */
    private SampleDataObject smp = new SampleDataObject();

    
    /** Set the identity to the database. Store the textual representation.
     * @param identity The Individuals name (identity). Not IID!
     */    
    public void setIndividual(String identity)
    {
        int pos = ind.indexOf(identity);
        
        if (pos < 0)
            ind.add(identity,""); 
    }
    
    /** Set the identity to the database. Store the name and alias.
     * @param identity The name of the individual
     * @param alias An alias for the individual
     */    
    public void setIndividual(String identity, String alias)
    {
        //System.err.println("add "+identity);
        int pos = ind.indexOf(identity);
        
        if (pos < 0)
            ind.add(identity,alias); 
    }
    
    /** Check if given identity of an individual is unique in the database object.
     * @param identity The name of the individual to check for.
     * @return If unique - True & Else - False
     */    
    public boolean isIndividualUnique(String identity)
    {
        return ind.isUnique(identity);
    }
    
    /** Get the individuals name given an alias.
     * @param alias An individuals alias
     * @return Return the name of an individual
     */    
    public String getIdentity(String alias)
    {
        return ind.aliasToIdentity(alias);
    }
    
    
    /*
    public void setMarker(String marker)
    {
        allele.add(marker,"");    
    }
     */
    
    /**
     * @param marker
     * @param allele
     */    
    public void setAllele(String marker, String allele)
    {
        //System.err.println(marker+";"+allele);
        this.allele.add(marker,allele);
    }
    
    
    
    /**
     * @param identity
     * @param marker
     * @param a1
     * @param a2
     */    
    public void setGenotype(String identity, String marker, String a1, String a2)
    {
        gen.add(identity,marker, a1,a2);
    }
    
         
    /**
     * @param marker
     * @param chromosome
     */    
    public void setMarker(String marker, String chromosome)
    {
        //Errors.logDebug("M="+marker+", C="+chromosome);
        mark.add(marker,chromosome);
    }
    
     public void setMarker(String marker)
    {
        //Errors.logDebug("M="+marker+", C="+chromosome);
        mark.add(marker);
    }
    
    /**
     * @param var
     * @param identity
     */    
    public void setPhenotype(String var, String identity)
    {
        phen.add(var, identity);
    }
    
    public void setVariable(String variable)
    {
        var.add(variable);
    }
    
    public void setGrouping(String identity, String group, String grouping)
    {
        Errors.logInfo("DataObject.setGrouping("+identity+","+group+","+grouping+")");
        grp.add(identity, group, grouping);
    }
    
    
    public void setSample(String identity, String sample)
    {
        smp.add(identity, sample);
    }
    

    /**
     * @param marker
     * @return
     */    
    public boolean isMarkerUnique(String marker)
    {
        return mark.isMarkerUnique(marker);
    }
    
    /**
     * @param marker
     * @param chromosome
     * @return
     */    
    public boolean isMarkerUnique(String marker, String chromosome)
    {
        return mark.isMarkerUnique(marker,chromosome);
    }
    
    /**
     * @param allele
     * @param marker
     * @return
     */    
    public boolean isAlleleUnique(String allele, String marker)
    {
        boolean res = false;
        int pos = this.allele.indexOf(allele,marker);
        
        if (pos >= 0)
            res = false;
        else
            res = true;
        
        return res;
    }
    
    /**
     * @param marker
     * @param identity
     * @return
     */    
    public boolean isGenotypeUnique(String marker, String identity)
    {
        boolean res = true;
        
        int pos = gen.getIndexOf(identity,marker);
        
        //System.out.println("Pos="+pos);
        
        if (pos >=0 )
            res = false;
        else
            res = true;
        return res;
    }
    
    public boolean isVariableUnique(String variable)
    {
        return var.isVariableUnique(variable);
    }
    
    public boolean isPhenotypeUnique(String variable, String identity)
    {
        return phen.isPhenotypeUnique(variable, identity);
    }
    
    public boolean isGroupingUnique(String identity, String group, String grouping)
    {
        boolean res = true;
        
        int pos = grp.getIndexOf(identity,group,grouping);        
        if (pos >=0 )
            res = false;
        else
            res = true;
        return res;
    }
    
    
    public boolean isSampleUnique(String identity, String sample)
    {
        return smp.isUnique(identity, sample);
    }
        
    /**
     * @param identity
     * @param marker
     * @return
     */    
    public int indexOfGenotype(String identity,String marker)
    {
        return gen.getIndexOf(identity,marker);
    }
    
    public int indexOfPhenotype(String variable, String identity)
    {
        return phen.getIndexOf(variable, identity);
    }
    
    /**
     * @param index
     * @return
     */    
    public String getGenotypeA1(int index)
    {
        return gen.getA1(index);
    }
    /**
     * @param index
     * @return
     */    
    public String getGenotypeA2(int index)
    {
        return gen.getA2(index);
    }
    
    /** Creates a new instance of DataObject */
    public DataObject() 
    {
    }
    
    /**
     * @return
     */    
    public String getDebug()
    {
        String out = "Markers="+mark.numOfRows();
        
        
        return out;
    }
    
    
    
}
