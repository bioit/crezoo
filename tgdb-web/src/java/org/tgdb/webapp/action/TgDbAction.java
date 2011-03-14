package org.tgdb.webapp.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.tgdb.frame.Action;
import org.tgdb.frame.ActionException;
import org.tgdb.frame.Navigator;
import org.tgdb.form.FormDataManager;
import org.tgdb.frame.WebFormDataManager;
import org.tgdb.form.AbstractFormDataManagerFactory;
import org.tgdb.form.FormDataException;
import org.tgdb.TgDbCaller;
import org.tgdb.TgDbFormDataManagerFactory;
import org.tgdb.adminmanager.AdminManagerRemote;
import org.tgdb.export.ExportManagerRemote;
import org.tgdb.model.modelmanager.ModelManagerRemote;
import org.tgdb.project.projectmanager.ProjectManagerRemote;
import org.tgdb.resource.resourcemanager.ResourceManagerRemote;
import org.tgdb.samplingunit.samplingunitmanager.SamplingUnitManagerRemote;
import org.tgdb.servicelocator.ServiceLocator;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public abstract class TgDbAction extends Action {
    
    protected static Logger logger = Logger.getLogger(TgDbAction.class);
    
    protected ServiceLocator locator;
    
    protected static SamplingUnitManagerRemote samplingUnitManager;
    protected static ProjectManagerRemote projectManager;
    protected static ExportManagerRemote exportManager;
    protected static ResourceManagerRemote resourceManager;
    protected static ModelManagerRemote modelManager;
    protected static AdminManagerRemote adminManager;
    
    public TgDbAction() {
        locator = ServiceLocator.getInstance();
        
        if (samplingUnitManager==null)
            samplingUnitManager =
                    (SamplingUnitManagerRemote)locator.getManager(ServiceLocator.Services.SAMPLINGUNITMANAGER);
        if (projectManager==null)
            projectManager =
                    (ProjectManagerRemote)locator.getManager(ServiceLocator.Services.PROJECTMANAGER);
        if (exportManager==null)
            exportManager =
                    (ExportManagerRemote)locator.getManager(ServiceLocator.Services.EXPORTMANAGER);
        if (resourceManager==null)
            resourceManager =
                    (ResourceManagerRemote)locator.getManager(ServiceLocator.Services.RESOURCEMANAGER);        
        if (modelManager==null)
            modelManager =
                    (ModelManagerRemote)locator.getManager(ServiceLocator.Services.MODELMANAGER);        
        if (adminManager==null)
            adminManager =
                    (AdminManagerRemote)locator.getManager(ServiceLocator.Services.ADMINMANAGER);
    }

    /*
     * Returns the stack trace as string
     */
    public String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }
    
    public FormDataManager getFormDataManager(int name, int type, HttpServletRequest req) throws ActionException {
        // Get the form data manager from the http session...
        WebFormDataManager formDataManager = (WebFormDataManager)req.getSession().getAttribute(AbstractFormDataManagerFactory.getInstanceName(name));        
        // If not created yet...use the factory to build a new one
        // with correct default values
        if(formDataManager == null) {
            try {
                TgDbFormDataManagerFactory formFactory = new TgDbFormDataManagerFactory((TgDbCaller)req.getSession().getAttribute("caller"));
                formDataManager = (WebFormDataManager)formFactory.createInstance(name, type);
            } catch(FormDataException e) {
                throw new ActionException(e.getMessage());
            }
        }
        
        return formDataManager;
    } 
    
    public void collectFormData(int name, int type, HttpServletRequest req) throws ActionException {
        HttpSession se = req.getSession();            
        Navigator nav = (Navigator)se.getAttribute("navigator");        
        WebFormDataManager formDataManager = (WebFormDataManager)getFormDataManager(name, type, req);
        formDataManager.collectParams((Object)req);
        se.setAttribute(TgDbFormDataManagerFactory.getInstanceName(name), formDataManager);
    }
    
    public void resetFormData(int name, HttpServletRequest req) throws ActionException {
        FormDataManager formDataManager = getFormDataManager(name, TgDbFormDataManagerFactory.WEB_FORM, req);
        formDataManager.reset();
    }
    
    public boolean exists(String value) {
        
        if(value != null && value.length() > 0 && !value.equalsIgnoreCase("null"))
            return true;
        else
            return false;
    }
    
    public boolean exists_without_value(String value) {
        
        if(value != null)
            return true;
        else
            return false;
    }
    
    public boolean isSubmit(HttpServletRequest req, String name) {
        
        if (req.getParameter(name)!=null)
            return true;
        else if (req.getParameter(name+".x")!=null)
            return true;
        return false;
    }
    
    public void debugParameters(HttpServletRequest req) {
        String out = "---------------------------------------->TgDbAction#debugParameters: \n";
        Enumeration num = req.getParameterNames();
        while (num.hasMoreElements())
        {
            String e = (String)num.nextElement();
            out += e+"="+req.getParameter(e)+"\n";
        }
        
        logger.debug(out);
    }
    
}
