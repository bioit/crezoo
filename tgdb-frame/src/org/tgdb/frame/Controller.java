package org.tgdb.frame;

import org.tgdb.util.Timer;
import java.io.*;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Controller extends HttpServlet 
{   
    private static Logger logger = Logger.getLogger(Controller.class);
    
    private static final String CONTROLLER_TAG = "controller";
    private static final String WORKFLOWMANAGERS_TAG = "workflow-managers";
    private static final String WORKFLOWMANAGER_TAG = "workflow-manager";

    private static final String CLASS_ATTR = "class";
    private static final String ACTIONPREFIX_ATTR = "actionPrefix";
    
    private static final String WORKFLOW_MANAGER = "WorkflowManager";
    
    private String workflowManagerClassName;
    private String loginClassName;
    

    public void init() throws ServletException {           
        try
        {
            parseXML();
            navAction = new NavigatorAction();
            requestLogAction = new RequestLogAction();
        }
        catch (Exception e)
        {
            throw new ServletException("Failed to read controller.xml");
        }
    }
    
    private void debugSession(HttpSession session) {
        Enumeration names = session.getAttributeNames();
        String out = "------------------------------>Controller#debugSession: \n";
        while (names.hasMoreElements())
        {
            out += names.nextElement()+"\n";
        }
        logger.debug(out);
    }
    
    private NavigatorAction navAction;
    private RequestLogAction requestLogAction;
    
    private Caller checkLogin(HttpServletRequest request, HttpServletResponse response) throws ArxFrameException {
        try
        {
            HttpSession session = request.getSession();
            Caller caller = (Caller)session.getAttribute("caller");
            if (caller == null){
                
                logger.debug("------------------------------>Controller#checkLogin: Caller is null");
                Class c = Class.forName(loginClassName);
                ILogin login = (ILogin)c.newInstance();
                caller = login.doLogin(request, response);
                session.setAttribute("caller", caller);
                return caller;
                
            }else{
                logger.debug("------------------------------>Controller#checkLogin: Caller not null ("+caller.getName()+")");
                Class c = Class.forName(loginClassName);
                ILogin login = (ILogin)c.newInstance();
                Caller newcaller = login.doLogin(request, response);
                
                if(newcaller.getUsr().compareTo(caller.getUsr())!=0){
                    logger.debug("------------------------------>Controller#checkLogin: Caller not null. User '"+caller.getUsr()+"' logs out & user '"+newcaller.getUsr()+"' logs in.");
                    caller = null;
                    session.invalidate();
                    Navigator nav = new Navigator();
                    request.getSession().setAttribute("caller", newcaller);
                    request.getSession().setAttribute("navigator", nav);
                    System.gc();
                    return newcaller;
                }else{
                    newcaller = null;
                    System.gc();
                    return caller;
                }   
            }
        }
        catch (ArxLoginForward alf)
        {
            throw alf;
        }
        catch (Exception e)
        {
            logger.error("------------------------------>Controller#checkLogin: Could not login", e);
            throw new ArxFrameException("Could not login", e);
        }
}
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try
        {
            response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Content-Type", "text/html; charset=utf-8");
            request.setCharacterEncoding("UTF-8");
            
            logger.debug("");
            logger.debug("-------------------->Controller#processRequest: Started");
            
            long t1 = System.currentTimeMillis();
            
            HttpSession session = request.getSession();
            ServletContext context = getServletContext();
            
            // Log QS
            requestLogAction.performAction(request, context);

            //debugSession(session);

            Caller caller = checkLogin(request, response);

            /** Handle Navigation actions */
            navAction.performAction(request, response, context);

            /**
             * The user have a workflow manager to handle workflows in the appliction.
             */
            IWorkFlowManager workflowManager = getWorkflowManager(request,context);
            workflowManager.setCaller(caller);
           

            /**
             * Get the next page from workflow manager
             */
            String nextPage = workflowManager.getNextPage(request, context);
            
            /**
             * Forward to view
             */
            RequestDispatcher forwarder = request.getRequestDispatcher("/"+nextPage);
            forwarder.forward(request, response);

            long t2 = System.currentTimeMillis();
            logger.debug("-------------------->Controller#processRequest: Completed in "+(t2-t1)+" ms");
            logger.debug("");
        }
        catch (ArxLoginForward alf)
        {
            response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Pragma", "no-cache");
            RequestDispatcher forwarder = request.getRequestDispatcher(alf.getUrl());
            forwarder.forward(request, response);
        }
        catch (ArxFrameException afe)
        {
            logger.error("ArxFrameException.", afe);
            response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Pragma", "no-cache");
            RequestDispatcher forwarder = request.getRequestDispatcher("error/GeneralError.jsp");
            forwarder.forward(request, response);
        }
    }
    
    private IWorkFlowManager getWorkflowManager(HttpServletRequest request, ServletContext context) throws ArxFrameException {
        IWorkFlowManager mgr = null;
        try
        {
            mgr = (IWorkFlowManager)request.getSession().getAttribute(WORKFLOW_MANAGER);
            if (mgr == null)
            {
                Class c = Class.forName(workflowManagerClassName);
                mgr = (IWorkFlowManager)c.newInstance();
                mgr.setContext(context);
                request.getSession().setAttribute(WORKFLOW_MANAGER, mgr);
            }
        }
        catch (Exception e)
        {
            logger.error("------------------------------>Controller#getWorkflowManager: Failed to get workflow manager", e);
            throw new ArxFrameException("Failed to get workflowManager.", e);
        }
        return mgr;
    }
    
    private void parseXML() throws Exception {
        Timer t = new Timer();
        try
        {
            ServletContext context = this.getServletContext();
            InputStream is = context.getResourceAsStream("/controller.xml");
            
            //wrkFlwMgrs = new ArrayList();
        
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            
            // Get login class name
            NodeList nl = doc.getElementsByTagName("login");
            Element e = (Element)nl.item(0);
            loginClassName = e.getAttribute(CLASS_ATTR);

            NodeList nl2 = doc.getElementsByTagName(WORKFLOWMANAGER_TAG);
            Element wflmgr = (Element)nl2.item(0);
            workflowManagerClassName = wflmgr.getAttribute(CLASS_ATTR);
        }
        catch (Exception e)
        {
            logger.error("------------------------------>Controller#parseXML: Failed to read xml data for Controller", e);
            throw new Exception("Could read configuration", e);
        }
        t.stop();
    }
    
    
    // <editor-fold defaultstate="collapsed">
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
