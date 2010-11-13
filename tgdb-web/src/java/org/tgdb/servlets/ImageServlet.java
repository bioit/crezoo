package org.tgdb.servlets;

import org.tgdb.resource.file.FileRemoteHome;
import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;
import org.tgdb.servicelocator.ServiceLocator;
import org.tgdb.model.modelmanager.ModelManagerRemote;
import org.tgdb.resource.file.FileRemote;

public class ImageServlet extends HttpServlet {

    protected ServiceLocator locator;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        try{
            if(request.getParameter("fileid")!=null && request.getParameter("fileid").length()>0){
                Integer fileid = new Integer(request.getParameter("fileid"));
                locator = ServiceLocator.getInstance();
            
                FileRemoteHome fh = (FileRemoteHome)locator.getHome(ServiceLocator.Services.FILE);
                FileRemote file = fh.findByPrimaryKey(fileid);
                
                byte[] data = file.getData();
                String mimeType = file.getMimeType();
                
                response.setContentType(mimeType);
                String fileName = file.getName();

                response.setHeader("Content-Disposition", "inline; filename=" + fileName);
                ServletOutputStream sos = response.getOutputStream();
                sos.write(data);
                sos.close();
            }
        } catch (Exception e) {}
    }
    
    // <editor-fold defaultstate="collapsed">
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
