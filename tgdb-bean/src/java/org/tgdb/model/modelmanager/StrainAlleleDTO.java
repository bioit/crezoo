package org.tgdb.model.modelmanager;

import org.tgdb.model.strain.allele.StrainAlleleRemote;

public class StrainAlleleDTO {
    
    private int id;
    private String mgi_id, symbol, symbol_ss, name, name_ss, attributes, made_by, origin_strain, mgi_url;
    
//    private int IsStrainAlleleTransgenic;
    
    private String mutations, mutationabbrs, mgilink;
   
    public StrainAlleleDTO(StrainAlleleRemote sa) {
        try
        {
            id = sa.getId();
            mgi_id = sa.getMgiId();
            
            mgilink = getMgilink();
            
            symbol = sa.getSymbol();
            
            symbol_ss = sa.getSymbol().replaceAll("<","&lt;").replaceAll(">","&gt;");
            symbol_ss = symbol_ss.replaceAll("&lt;","<sup>").replaceAll("&gt;","</sup>");
            
            name = sa.getName();
            
            name_ss = sa.getName().replaceAll("<","&lt;").replaceAll(">","&gt;");
            name_ss = name_ss.replaceAll("&lt;","<sup>").replaceAll("&gt;","</sup>");

            made_by = sa.getMade_by();
            origin_strain = sa.getOrigin_strain();
            mgi_url = sa.getMgi_url();
            
                  
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * @return the symbol_ss
     */
    public String getSymbol_ss() {
        return symbol_ss;
    }

    /**
     * @return the name_ss
     */
    public String getName_ss() {
        return name_ss;
    }

    public String getMgi_id() {
        return mgi_id;
    }
    
    public String getMgilink(){
        if(mgi_id!=null && mgi_id.length()!=0 && mgi_id.compareTo("0")!=0){
            mgilink = "<a href=\"http://www.informatics.jax.org/searches/accession_report.cgi?id=MGI:"+mgi_id+"\" target=\"_blank\" title=\"MGI Allele Lookup\">MGI:"+mgi_id+"</a>";
        } else {
            mgilink = "n/a";
        }
        return mgilink;
    }

    public String getMgi_url() {
        if(mgi_url!=null && mgi_url.trim().length()!=0 && !mgi_url.equalsIgnoreCase("null")){
            //do nothing
        }
        else {
            mgi_url = "#";
        }

        return mgi_url;
    }

    public String getSymbol() {
        return symbol;
    }

//    public String getGeneName() {
//        return geneName;
//    }
//
//    public int getGeneId() {
//        return geneId;
//    }

    public String getMutations() {
        return mutations;
    }

    public void setMutations(String mutations) {
        this.mutations = mutations;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
       this.attributes = attributes;
    }

    public String getMutationabbrs() {
        return mutationabbrs;
    }

    public void setMutationabbrs(String mutationabbrs) {
        this.mutationabbrs = mutationabbrs;
    }

//    public int getIsStrainAlleleTransgenic(){
//        return IsStrainAlleleTransgenic;
//    }
//
//    public void setIsStrainAlleleTransgenic(int IsStrainAlleleTransgenic){
//        this.IsStrainAlleleTransgenic = IsStrainAlleleTransgenic;
//    }

    public String getMade_by() {
        return made_by;
    }

    public String getOrigin_strain() {
        return origin_strain;
    }
 }
