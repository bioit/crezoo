package org.tgdb.resource.resourcemanager;

import org.tgdb.TgDbCaller;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import javax.naming.directory.BasicAttributes;
import org.apache.log4j.Logger;

public class MultipartHelper {
    
    private static Logger logger = Logger.getLogger(MultipartHelper.class);
    
    private MultipartParser mp;
    
    private BasicAttributes bas;
    
    private ResourceManagerRemote resourceManager;
    
    private ArrayList fileIds;
    
    private TgDbCaller caller;
    
    /**
     * Creates a new instance of MultipartHelper 
     */
    public MultipartHelper(MultipartParser mp, ResourceManagerRemote resourceManager, TgDbCaller caller) {
        this.mp = mp;
        this.resourceManager = resourceManager;
        fileIds = new ArrayList();
        bas = new BasicAttributes();
        this.caller = caller;
    }
    
    
//    public String getParam(Part p)
//    {
//        try
//        {
//            if (p.isParam())
//            {
//                ParamPart pp = (ParamPart)p;
//
//                return pp.getStringValue();
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        return null;
//    }
    
    /**
     * Parse parameters and store files in this request.
     * 
     * Parameters can be collected with getAttributes.
     * FileIds can be collected with getFileIds
     *
     */
    public void parse()
    {
       logger.debug("---------------------------------------->MultipartHelper#parse: Parsing file");
       try {
        
            Part p = null;
            while ((p = mp.readNextPart()) !=null)
            {
                if (p.isParam())
                {
                    ParamPart pp = (ParamPart)p;
                    String id = pp.getName();
                    String val = pp.getStringValue();

                    bas.put(id, val);
                } else if (p.isFile()) {
                    
                    logger.debug("---------------------------------------->MultipartHelper#parse: It's a file");
                    
                    FilePart fp1 = (FilePart)p;
                    InputStream is = fp1.getInputStream();

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int c;
                    while ((c = is.read())!= -1)
                    {
                        out.write(c);
                    }
                    System.out.flush();
                    out.flush();
                    out.close();

                    byte[] data = out.toByteArray();

                    logger.debug("---------------------------------------->MultipartHelper#parse: Data "+data.length+" bytes");
                    logger.debug("---------------------------------------->MultipartHelper#parse: Content "+fp1.getContentType());


                    int fileid = resourceManager.saveFile(fp1.getFileName(), fp1.getContentType(), data, caller);
                    fileIds.add(new Integer(fileid));

                    // GC data.
                    data = null;
                } else {
                    logger.warn("---------------------------------------->MultipartHelper#parse: No parameter and no file");
                }
            }
       } catch (Exception e) {
           logger.error("---------------------------------------->MultipartHelper#parse: Error while parsing files and attributes", e);
       }
    }
    
    /**
     * Get the file ids for the files stored in database for this operation.
     */
    public ArrayList getFileIds()
    {
        return fileIds;
    }
    
    /**
     * Get all the attributes for the operation
     */
    public BasicAttributes getAttributes()
    {
        return bas;
    }
    
    
    
//    public void saveFile(Part p, FileManagerRemote fileManager) throws ApplicationException
//    {
//        try {
//            if (p.isFile())
//            {
//                FilePart fp1 = (FilePart)p;
//                InputStream is = fp1.getInputStream();
//                
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                int c;
//                while ((c = is.read())!= -1)
//                {
//                    out.write(c);
//                }
//                System.out.flush();
//                out.flush();
//                out.close();
//                
//                byte[] data = out.toByteArray();
//                
//                System.out.println("Data file size="+data.length);
//                //System.out.println(new String(data));
//                //System.out.println(data);
//                
//                
//                
//                System.out.println("Content type="+fp1.getContentType());
//                
//                
//                fileManager.saveFile(fp1.getFileName(), fp1.getContentType(), data);
//                
//                // GC data.
//                data = null;
//            }
//        } catch (ApplicationException e) {
//            throw e;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ApplicationException("Failed to sage file");
//        }
//    }
}
