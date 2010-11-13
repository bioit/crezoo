/*
 * WebFileUpload.java
 *
 * Created on December 6, 2005, 1:45 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.frame.io;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author lami
 */
public class WebFileUpload {
    private HttpServletRequest req;
    private MultipartParser mp;
    private HashMap map;

    /**
     * Creates a new instance of WebFileUpload 
     */
    public WebFileUpload(HttpServletRequest req, int maxSize) {
        this.req = req;
        try {
            mp = new MultipartParser(req, maxSize);
            map = new HashMap();
            fixParameters();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private void fixParameters() {
        try {                        
            Part p = mp.readNextPart();
            while(p != null) {
                if(p.isParam()) {
                    ParamPart param = (ParamPart)p;
                    map.put(p.getName(), param.getStringValue());
                }
                else if (p.isFile()) {
                    FilePart fp = (FilePart)p;
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    long bytes = fp.writeTo(out);

                    byte[] data = out.toByteArray();   
                    FileDataObject fileData = new FileDataObject(data, fp.getFileName(), fp.getContentType());
                    // GC data.
                    data = null;                    
                    map.put(fp.getName(), fileData);
                }
                
                p = mp.readNextPart();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }          
    }
    
    public String getFormParameter(String key) {       
        return (String)map.get(key);
    }
    
    public FilePart getFileParameter(String key) {
        try {            
            Part p = mp.readNextPart();

            while(p != null) {
                if(p.isFile() && p.getName().equalsIgnoreCase(key)) {
                    return (FilePart)p;
                }
                
                p = mp.readNextPart();
            }
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }  
        
        return null;
    }    
    
    public FileDataObject getFile(String key) {
        return (FileDataObject)map.get(key);
    }    
}
