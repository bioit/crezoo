package org.tgdb.model.modelmanager;

import org.tgdb.species.gene.GeneRemote;
import org.tgdb.project.user.UserRemote;
import java.io.Serializable;
import org.tgdb.frame.DTO;

public class GeneDTO extends DTO implements Serializable {
    private String name, name_ss, comm, ts, userName, userMail, userFullName, mgiid, genesymbol, genesymbol_ss, geneexpress, idgene, idensembl;
    private String mgiurl, ensemblurl, entrezurl, driver_note, molecular_note, molecular_note_link, common_name, distinguish;
    private int gaid, pid, userId, models_num;
    
    private String chromosomename;
    private int cid;
    
    private String speciesName;
    
    public GeneDTO(GeneRemote gene) {
        try {
            UserRemote user = gene.getUser();
            userName = user.getUsr();
            userId = user.getId();
            userMail = user.getEmail();
            userFullName = user.getName();

            name = gene.getName();
            name_ss = gene.getName().replaceAll("<","&lt;").replaceAll(">","&gt;");
            name_ss = name_ss.replaceAll("&lt;","<sup>").replaceAll("&gt;","</sup>");


            if(exists(gene.getComm())) comm = gene.getComm().replaceAll("<","&lt;").replaceAll(">","&gt;");
            
            ts = gene.getTs().toString();
            gaid = gene.getGaid();
            pid = gene.getProject().getPid();
            
            mgiid = gene.getMgiid();
            
            genesymbol = gene.getGenesymbol();
            genesymbol_ss = gene.getGenesymbol();
            if (exists(genesymbol_ss)) {
                genesymbol_ss = genesymbol_ss.replaceAll("<","&lt;").replaceAll(">","&gt;");
                genesymbol_ss = genesymbol_ss.replaceAll("&lt;","<sup>").replaceAll("&gt;","</sup>");
            }
            
            geneexpress = gene.getGeneexpress();
            if (exists(geneexpress)) geneexpress = geneexpress.replaceAll("<","&lt;").replaceAll(">","&gt;");
            
            idgene = gene.getIdgene();
            idensembl = gene.getIdensembl();
            
            mgiurl = processURl(getMgiurl());
            entrezurl = processURl(getEntrezurl());
            ensemblurl = processURl(getEnsemblurl());
            
            chromosomename = gene.getChromosome().getName();
            cid = gene.getChromosome().getCid();
            
            speciesName = gene.getChromosome().getSpecies().getName();
            
            models_num = gene.getModelsNum();

            driver_note = gene.getDriver_note();
            molecular_note = gene.getMolecular_note();
            molecular_note_link = gene.getMolecular_note_link();
            common_name = gene.getCommon_name();
            distinguish = gene.getDistinguish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getName() {
        if (name == null || name.length()== 0 || name.equalsIgnoreCase("null")){
            return "n/a";
        }
        return name;
    }

    public String getDriver_note() {
        return driver_note;
    }

    public String getMolecular_note() {
        return molecular_note;
    }

    public String getMolecular_note_link() {
        return molecular_note_link;
    }

    public String getCommon_name() {
        return common_name;
    }

    public String getDistinguish() {
        return distinguish;
    }

    public String getComm() {
        if (comm == null || comm.length()== 0 || comm.equalsIgnoreCase("null")){
            return "n/a";
        }
        return comm;
    }
    
    public int getModels_num() {
        return models_num;
    }

    public String getTs() {
        return ts;
    }

    public String getUserName() {
        return userName;
    }
    
    public String getUserMail() {
        return userMail;
    }
    
    public String getUserFullName() {
        return userFullName;
    }

    public int getGaid() {
        return gaid;
    }

    public int getUserId() {
        return userId;
    }
    
    public int getPid() {
        return pid;
    }

    public String getMgiid()  {
        if (mgiid == null || mgiid.length()== 0 || mgiid.equalsIgnoreCase("null")){
            return "0";
        }
        return mgiid;
    }
    
    public String getMgiurl(){
        if(mgiid != null && mgiid.length() != 0 && mgiid.compareTo("0") != 0){
            mgiurl = "<a title=\"MGI Lookup\" href=\"http://www.informatics.jax.org/searches/accession_report.cgi?id=MGI:"+mgiid+"\" target=\"_blank\">MGI:"+mgiid+"</a>";
        } else {
            mgiurl = "";
        } 
        return processURl(mgiurl);
    }
    
    public String getIdgene() {
        if (idgene == null || idgene.length()== 0 || idgene.equalsIgnoreCase("null")){
            return "0";
        }
        return idgene;
    }
    
    public String getEntrezurl(){
        if(idgene != null && idgene.length() != 0 && idgene.compareTo("0") != 0){
            entrezurl = "<a title=\"ENTREZ Lookup\" href=\"http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=full_report&list_uids="+idgene+"\" target=\"_blank\">"+idgene+"</a>";
        } else {
            entrezurl = "";
        }
        return processURl(entrezurl);
    }
    
    public String getIdensembl() {
        if (idensembl == null || idensembl.length()== 0 || idensembl.equalsIgnoreCase("null")){
            return "0";
        }
        return idensembl;
    }
    
    public String getEnsemblurl(){
        if(idensembl != null && idensembl.length() != 0 && idensembl.compareTo("0") != 0){
            if(idensembl.trim().startsWith("ENSMUSG")){
                ensemblurl = "<a title=\"ENSEMBL Lookup\" href=\"http://www.ensembl.org/Mus_musculus/geneview?gene="+idensembl+"\" target=\"_blank\">"+idensembl+"</a>";
            } else {
                ensemblurl = "<a title=\"ENSEMBL Lookup\" href=\"http://www.ensembl.org/Homo_sapiens/geneview?gene="+idensembl+"\" target=\"_blank\">"+idensembl+"</a>";
            }
        } else {
            ensemblurl = "";
        }
        return processURl(ensemblurl);
    }
    
    public String getGenesymbol() {
        if (genesymbol == null || genesymbol.length()== 0 || genesymbol.equalsIgnoreCase("null")){
            return "n/a";
        }
        return genesymbol;
    }
    
    public String getGeneexpress() {
        if (geneexpress == null || geneexpress.length()== 0 || geneexpress.equalsIgnoreCase("null")){
            return "n/a";
        }
        return geneexpress;
    }
    
    public String getChromoName() {
        return chromosomename;
    }
    
    public int getCid() {
        return cid;
    }
    
    public String getSpeciesName() {
        return speciesName;
    }

    public String getName_ss() {
        return name_ss;
    }
    
    public String getGenesymbol_ss() {
        return genesymbol_ss;
    }
}
