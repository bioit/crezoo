package org.tgdb.webapp.action.expression;

import org.tgdb.frame.io.FileDataObject;
import org.tgdb.frame.io.WebFileUpload;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class SaveExpressionModelAction extends TgDbAction {
    
    public String getName() {
        return "SaveExpressionModelAction";
    }
    
    public int checkMIME (FileDataObject file){
        
        if(file.getMimeType().regionMatches(true, 0, "image/", 0, 6)) {
            logger.debug("---------------------------------------->SaveExpressionModelAction#checkMIME: Request for "+file.getFileName()+" image upload");
            return 1;
        } else {
            logger.debug("---------------------------------------->SaveExpressionModelAction#checkMIME: Abnormal file upload request. File type "+file.getMimeType()+" is not an image");
            return 0;
        }
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
//            HttpSession session = request.getSession();
            WebFileUpload webFile = new WebFileUpload(request, 100000000);
            
            String upload = webFile.getFormParameter("upload");
            String exid_string = webFile.getFormParameter("exid");
            
            if(exists(upload)) {
                TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");

                if (exists(exid_string)) {
                    int exid = new Integer(exid_string).intValue();
                    String exanatomy = webFile.getFormParameter("exanatomy");
                    String excomm = webFile.getFormParameter("excomm");
                    FileDataObject exfile = webFile.getFile("exfile");
                    String exfilecomm = webFile.getFormParameter("exfilecomm");

                    if(checkMIME(exfile)!=0){
                        int fileid = modelManager.addFile(exfile, exfilecomm, _caller);
                        modelManager.addFileToExpressionModel(exid, fileid, _caller);
                    }

                    modelManager.updateExpressionModel(exid, exanatomy, excomm, _caller);
                } else {
                    String exanatomy = webFile.getFormParameter("exanatomy");
                    String excomm = webFile.getFormParameter("excomm");
                    FileDataObject exfile = webFile.getFile("exfile");
                    String exfilecomm = webFile.getFormParameter("exfilecomm");
                    String eid = webFile.getFormParameter("eid");

                    int exid = modelManager.createExpressionModel(exanatomy, excomm);

                    if(checkMIME(exfile)!=0){
                        int fileid = modelManager.addFile(exfile, exfilecomm, _caller);
                        modelManager.addFileToExpressionModel(exid, fileid, _caller);
                    }

                    modelManager.addExpressionModelToModel(new Integer(eid).intValue(), exid, _caller);

                    //ontologies
                    String ma_id = webFile.getFormParameter("ma_id");
                    if(exists(ma_id)){
                        modelManager.addOntologyToExpressionModel(exid, ma_id, "MA", _caller);
                    }

                    String emap_id = webFile.getFormParameter("emap_id");
                    if(exists(emap_id)){
                        modelManager.addOntologyToExpressionModel(exid, emap_id, "EMAP", _caller);
                    }
                }
            
            }
            
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("SaveExpressionModelAction Failed to perform action", e);
        }
    }
}
