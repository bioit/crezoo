package org.tgdb.webapp.action.directview;

import org.tgdb.frame.ArxFrameException;
import org.tgdb.frame.ArxLoginForward;
import org.tgdb.frame.advanced.AdvancedWorkflowManager;
import org.tgdb.TgDbCaller;
import org.tgdb.model.expmodel.ExpModelRemoteHome;
import org.tgdb.species.gene.GeneRemoteHome;
import org.tgdb.model.modelmanager.ModelManagerRemote;
import org.tgdb.model.reference.ReferenceRemoteHome;
import org.tgdb.model.researchapplication.ResearchApplicationRemoteHome;
import org.tgdb.resource.file.FileRemoteHome;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import org.tgdb.species.species.SpeciesRemoteHome;
import java.io.*;
import org.tgdb.util.Timer;

import javax.servlet.*;
import javax.servlet.http.*;

public class DirectView extends HttpServlet {
    
    
    ServiceLocator locator;
    
    SamplingUnitRemoteHome suHome;
    ExpModelRemoteHome modelHome;
    ResearchApplicationRemoteHome raHome;
    FileRemoteHome fileHome;
    ModelManagerRemote modelManager;
    GeneRemoteHome gaHome; 
    ReferenceRemoteHome referenceHome;
    SpeciesRemoteHome speciesHome;
    
    TgDbCaller caller;
    
    public void lookup()
    {
        locator = ServiceLocator.getInstance();
        modelManager = (ModelManagerRemote)locator.getManager(ServiceLocator.Services.MODELMANAGER);
        modelHome = (ExpModelRemoteHome)locator.getHome(ServiceLocator.Services.EXPMODEL);
        suHome = (SamplingUnitRemoteHome)locator.getHome(ServiceLocator.Services.SAMPLINGUNIT);
        gaHome = (GeneRemoteHome)locator.getHome(ServiceLocator.Services.GENE);
        fileHome = (FileRemoteHome)locator.getHome(ServiceLocator.Services.FILE);
        referenceHome = (ReferenceRemoteHome)locator.getHome(ServiceLocator.Services.REFERENCE);
        speciesHome = (SpeciesRemoteHome)locator.getHome(ServiceLocator.Services.SPECIES);
    }
    
    public void init() throws ServletException
    {
        lookup();
        super.init();
    }
    
    public TgDbCaller getCaller()
    {
        if (caller==null)
        {
            caller = new TgDbCaller();
            caller.setId(1029);
            caller.setPid(99);
            caller.setName("Public");
            caller.setUsr("public");
            caller.setPwd("notknown");
            caller.setSuid(1003);
            caller.updatePrivileges();
            caller.setSid(99);
        }
        return caller;
    }
    
    public void logException(Exception e, PrintWriter out)
    {
        out.println("<pre>");
        e.printStackTrace(out);
        out.println("</pre>");
    }
    
    
    
   
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        AdvancedWorkflowManager wfm = new AdvancedWorkflowManager();
        try
        {
            /* Set headers */
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Content-Type", "text/html; charset=utf-8");
            request.setCharacterEncoding("UTF-8");
            
            System.out.println("--- START REQUEST -------------------------------");
            Timer t = new Timer();
            
            wfm.setCaller(getCaller());
            
            HttpSession session = request.getSession();
            
            String workflowController = request.getParameter("workflow");
            
        if((workflowController.compareTo("ViewModelDirect")==0) || (workflowController.compareTo("ViewGeneDirect")==0)) {

            session.setAttribute("caller", getCaller());
            
            ServletContext ctx = getServletContext();
            wfm.setContext(ctx);
            
            String nextPage = wfm.getNextPage(request, ctx);
            System.out.println("NextPage="+nextPage);
               
            /**
             * Forward to view
             */
            RequestDispatcher forwarder = request.getRequestDispatcher("/"+nextPage);
            forwarder.forward(request, response);

            // Stop timer and display time, End request.
            t.stop();
            System.out.println("DirectView#ProcessRequest(...): "+t);
            System.out.println("--- END REQUEST -------------------------------");
        }else{throw new ArxFrameException("Invalid Workflow!");}
            
            
        }
        catch (ArxLoginForward alf)
        {
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            RequestDispatcher forwarder = request.getRequestDispatcher(alf.getUrl());
            forwarder.forward(request, response);
        }
        catch (ArxFrameException afe)
        {
                
            request.setAttribute("exception", afe);
            //log(request,response);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            RequestDispatcher forwarder = request.getRequestDispatcher("error/GeneralError.jsp");
            //RequestDispatcher forwarder = request.getRequestDispatcher("error/NoAdmission.html");
            forwarder.forward(request, response);
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
