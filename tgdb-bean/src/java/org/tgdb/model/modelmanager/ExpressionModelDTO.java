package org.tgdb.model.modelmanager;

import org.tgdb.expression.expressionmodel.ExpressionModelRemote;
import org.tgdb.resource.file.FileRemote;
import java.util.Collection;
import java.util.Iterator;
import java.io.Serializable;

public class ExpressionModelDTO implements Serializable {
    
    private int exid;
    private String exanatomy, excomm;
    private Collection exfiles;
    private String exfilestable;
    
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
                exfilestable = "<table><tr>";
                //iterate through the collection of expression files
                Iterator tmpIter = exfiles.iterator();
                while(tmpIter.hasNext()){
                    FileRemote file = (FileRemote)tmpIter.next();
                    //exfilestable = exfilestable+"<td><img src=Controller?workflow=ViewFile&fileid="+file.getFileId()+"></td>";
                    exfilestable = exfilestable+"<td><img src=\"ImageServlet?fileid="+file.getFileId()+"\" alt=\"\" /></td>";
                }
                exfilestable = exfilestable+"</tr></table>";
                
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
    
    public String getExfiletable(){
        return exfilestable;
    }
}
