package org.tgdb.model.modelmanager;

import org.tgdb.expression.expressionmodel.ExpressionModelRemote;
import org.tgdb.resource.file.FileRemote;
import java.util.Collection;
import java.util.Iterator;
import java.io.Serializable;
import java.util.ArrayList;
import org.tgdb.model.reference.ReferenceRemote;

public class ExpressionModelDTO implements Serializable {
    
    private int exid;
    private String exanatomy, excomm;
    private Collection exfiles, references, references_dtos;
    private String exfilestable, emap_terms, ma_terms, references_line;
    
    public ExpressionModelDTO(ExpressionModelRemote expression) {
        try {
            exid = expression.getExid();
            exanatomy = expression.getExanatomy();
            excomm = expression.getExcomm();
            exfiles = expression.getFiles();
            
            //give me the length of the collection.
            int tmp = exfiles.size();
            
            //check if have any element in the collection
            if (tmp!=0){
                exfilestable = "<table>";
                //iterate through the collection of expression files
                Iterator tmpIter = exfiles.iterator();
                while(tmpIter.hasNext()){
                    FileRemote file = (FileRemote)tmpIter.next();
                    //exfilestable = exfilestable+"<td><img src=Controller?workflow=ViewFile&fileid="+file.getFileId()+"></td>";
                    exfilestable = exfilestable+"<tr><td><img src=\"ImageServlet?fileid="+file.getFileId()+"\" alt=\"\" /></td></tr>";
                }
                exfilestable = exfilestable+"</table>";
                
            }//if

            references = expression.getReferences();
            references_line = "";
            references_dtos = new ArrayList();
            Iterator j = references.iterator();
            while(j.hasNext()) {
                ReferenceRemote ref = (ReferenceRemote)j.next();
                references_dtos.add(new ReferenceDTO(ref));
                references_line += "[PubMed: " + ref.getPubmed() + "] ";
                if(ref.getLink() != null) {
                    references_line += "<a href='" + ref.getLink().getUrl() +"' target='_blank' class='data_link'>" + ref.getName() + "</a>";
                }
                else if(ref.getFile() != null) {
                    references_line += "<a href='Controller?workflow=ViewFile&fileid=" + ref.getFile().getFileId() +"' target='_blank' class='data_link'>" + ref.getName() + "</a>";
                }

                references_line += " &bull; ";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int getExid()
    {
        return exid;
    }
    
    public String getExanatomy()
    {
        return exanatomy;
    }
    
    public String getExcomm(){
        return excomm;
    }
    
    public Collection getExfiles(){
        return exfiles;
    }

    /**
     * @return the emap_terms
     */
    public String getEmap_terms() {
        return emap_terms;
    }

    /**
     * @param emap_terms the emap_terms to set
     */
    public void setEmap_terms(String emap_terms) {
        this.emap_terms = emap_terms;
    }

    /**
     * @return the ma_terms
     */
    public String getMa_terms() {
        return ma_terms;
    }

    /**
     * @param ma_terms the ma_terms to set
     */
    public void setMa_terms(String ma_terms) {
        this.ma_terms = ma_terms;
    }
    
    public String getExfiletable(){
        return exfilestable;
    }

    public Collection getReferences() {
        return references;
    }

    public String getReferences_line() {
        return references_line;
    }

    public Collection getReferences_dtos() {
        return references_dtos;
    }
}
