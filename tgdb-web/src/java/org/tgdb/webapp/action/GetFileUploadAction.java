package org.tgdb.webapp.action;

import org.tgdb.exceptions.ApplicationException;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.Part;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class GetFileUploadAction extends TgDbAction {
    
    public GetFileUploadAction() {}
    
    public String getName() {
        return "GetFileUploadAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            MultipartParser mp = new MultipartParser(request, 100000000);
            Part p = mp.readNextPart();
            
            if (p.isFile()) {
                FilePart fp1 = (FilePart)p;
                InputStream is = fp1.getInputStream();
                
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int c;
                while ((c = is.read())!= -1) {
                    out.write(c);
                }
                
                out.flush();
                out.close();
                
                byte[] data = out.toByteArray();
                
                logger.debug("---------------------------------------->GetFileUploadAction#performAction: File size = "+data.length);
                
                logger.debug("---------------------------------------->GetFileUploadAction#performAction: Content = "+fp1.getContentType());
                
                resourceManager.saveFile(fp1.getFileName(), fp1.getContentType(), data, null);
                
                data = null;
            }
            
            
            return true;
        } catch (ApplicationException e) {
            logger.error("---------------------------------------->GetFileUploadAction#performAction: Failed");
            throw e;
        } catch (Exception e) {
            logger.error("---------------------------------------->GetFileUploadAction#performAction: Failed");
            throw new ApplicationException("GetFileUploadAction", e);
        }
    }
}
