/*
 * TestServlet.java
 *
 * Created on May 18, 2005, 11:10 AM
 */

package test;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.*;
import javax.servlet.http.*;
import org.tgdb.project.user.UserRemote;
import org.tgdb.project.privilege.PrivilegeRemote;
import org.tgdb.project.privilege.PrivilegeRemoteHome;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.role.RoleRemote;
import org.tgdb.project.securityprinciple.SecurityPrinciplePk;
import org.tgdb.project.securityprinciple.SecurityPrincipleRemote;
import org.tgdb.project.securityprinciple.SecurityPrincipleRemoteHome;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemote;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteHome;
import org.tgdb.species.species.SpeciesRemote;
import org.tgdb.species.species.SpeciesRemoteHome;

/**
 *
 * @author heto
 * @version
 */
public class TestServlet extends HttpServlet {
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet TestServlet</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Servlet TestServlet at " + request.getContextPath () + "</h1>");
        out.println("</body>");
        out.println("</html>");
        
        try
        {
            
            org.tgdb.project.user.UserRemoteHome userHome;
            userHome = lookupUserBean();
            
            out.println("Number of users= "+userHome.getNumberOfUsers());
            
            /*
            String id = (String)request.getParameter("id");
            
            //UserRemote usr = home.findByPrimaryKey(Integer.valueOf(id));            
            UserRemote usr = home.findByUsr("heto");
            //UserRemote usr = home.create(new Integer(3), "heto22", "1233", "Tobias Hermansson", "E");
            //usr.setName("Kalle Anka");
            out.println("Name="+usr.getName());
            //user.create(new Integer(1), "heto", "123", "Tobias Hermansson", "E");
            
            */
            
            
            UserRemote usr = userHome.findByPrimaryKey(new Integer(1));
            
            
            
            usr.setName("kalle");
            
            
            
            
            
            org.tgdb.project.project.ProjectRemoteHome prjHome = null;
            prjHome = lookupProjectBean();
            
            ProjectRemote prj = prjHome.findByPrimaryKey(new Integer(1),null);
            
            //prj.addUser(usr);
            
            //prjHome.create(1, "prj1", "", "E");
            //prjHome.create(2, "prj2", "", "E");
            //prjHome.create(3, "prj3", "", "E");
            //prjHome.create(4, "prj4", "", "E");
            
            //ProjectRemote prj = null;
            
            
            
            
            /*
            ProjectRemote prj = prjHome.findByPrimaryKey("1");
            out.println("PrjName="+prj.getName());
            */
            
            /*
            
            prj.setComm("This is a comment");
            prj.enable();
            prj.remove();
            */
            
            /*
            Collection projects = prjHome.findByAll();
            Iterator prjItr = projects.iterator();
            
            
            
            while (prjItr.hasNext())
            {
                ProjectRemote prj = (ProjectRemote)prjItr.next();
                
                out.println("Name="+prj.getName()+"\n");
            }
             */
            
            org.tgdb.project.role.RoleRemoteHome roleHome = null;
            roleHome = lookupRoleBean();
            
            //RoleRemote role = roleHome.create(1, prj, "Testrole", "");
            RoleRemote role = roleHome.findByPrimaryKey(new Integer(1));
            out.println("roleName="+role.getName());
            
            
            
            org.tgdb.project.projectmanager.ProjectManagerRemote prjMgr = null;
            prjMgr = lookupProjectManagerBean();
            
            //prjMgr.addRole(prj, usr, role);
            //prjMgr.removeRole(prj, usr, role);
            
            //role.remove();
            
            
            
             PrivilegeRemoteHome priHome = lookupPrivilegeBean();
             //priHome.create(2, "User", "");
             //priHome.create(3, "ReadOnly", "");
             
             
             PrivilegeRemote pri = priHome.findByPrimaryKey(new Integer(3));
             
             //role.addPrivilege(pri);
             Collection privs = role.getPrivileges();
             Iterator itr = privs.iterator();
             
             while (itr.hasNext())
             {
                 PrivilegeRemote priv = (PrivilegeRemote)itr.next();
                 out.println("PrivilegeName="+priv.getName());
             }
             
             
             
             SecurityPrincipleRemoteHome secPrinHome = lookupSecurityPrincipleBean();
             SecurityPrincipleRemote secPrin = secPrinHome.findByPrimaryKey(new SecurityPrinciplePk(1,1,1));
             //SecurityPrincipleRemote secPrin = secPrinHome.create(prj, usr, role);
             
             out.println("<pre>"+secPrin.getId()+"</pre>");
             
             Collection secPrins = secPrinHome.findByProject(prj.getPid());
             itr = secPrins.iterator();
             
             
             while (itr.hasNext())
             {
                secPrin = (SecurityPrincipleRemote)itr.next();
                out.println("<p>"+secPrin.getId()+":"+secPrin.getPid()+":"+secPrin.getRid()+"</p>");
             }
             
             SpeciesRemoteHome specHome = lookupSpeciesBean();
             //SpeciesRemote spec = specHome.create(1, "human", "");
             SpeciesRemote spec = specHome.findByPrimaryKey(new Integer(1));
             spec.setComm("hejsan!");
             
             
             SamplingUnitRemoteHome sHome = lookupSamplingUnitBean();
             SamplingUnitRemote s = sHome.create(new Integer(1), "samp1", "", null,null);
             //SamplingUnitRemote s = sHome.findByPrimaryKey(Integer.valueOf(1));
             
             out.println("<p>SamplingUnitName:</p><p>"+s.getName()+"</p>");
             
        }
        catch (Exception e)
        {
            out.println("error: "+e.getMessage());
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
        
        out.close();
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

   

   

    private org.tgdb.project.user.UserRemoteHome lookupUserBean() {
        try {
            javax.naming.Context c = new javax.naming.InitialContext();
            Object remote = c.lookup("java:comp/env/ejb/UserBean");
            org.tgdb.project.user.UserRemoteHome rv = (org.tgdb.project.user.UserRemoteHome) javax.rmi.PortableRemoteObject.narrow(remote, org.tgdb.project.user.UserRemoteHome.class);
            return rv;
        }
        catch(javax.naming.NamingException ne) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE,"exception caught" ,ne);
            throw new RuntimeException(ne);
        }
    }

    private org.tgdb.project.project.ProjectRemoteHome lookupProjectBean() {
        try {
            javax.naming.Context c = new javax.naming.InitialContext();
            Object remote = c.lookup("java:comp/env/ejb/ProjectBean");
            org.tgdb.project.project.ProjectRemoteHome rv = (org.tgdb.project.project.ProjectRemoteHome) javax.rmi.PortableRemoteObject.narrow(remote, org.tgdb.project.project.ProjectRemoteHome.class);
            return rv;
        }
        catch(javax.naming.NamingException ne) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE,"exception caught" ,ne);
            throw new RuntimeException(ne);
        }
    }

    private org.tgdb.project.role.RoleRemoteHome lookupRoleBean() {
        try {
            javax.naming.Context c = new javax.naming.InitialContext();
            Object remote = c.lookup("java:comp/env/ejb/RoleBean");
            org.tgdb.project.role.RoleRemoteHome rv = (org.tgdb.project.role.RoleRemoteHome) javax.rmi.PortableRemoteObject.narrow(remote, org.tgdb.project.role.RoleRemoteHome.class);
            return rv;
        }
        catch(javax.naming.NamingException ne) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE,"exception caught" ,ne);
            throw new RuntimeException(ne);
        }
    }

    private org.tgdb.project.projectmanager.ProjectManagerRemote lookupProjectManagerBean() {
        try {
            javax.naming.Context c = new javax.naming.InitialContext();
            Object remote = c.lookup("java:comp/env/ejb/ProjectManagerBean");
            org.tgdb.project.projectmanager.ProjectManagerRemoteHome rv = (org.tgdb.project.projectmanager.ProjectManagerRemoteHome) javax.rmi.PortableRemoteObject.narrow(remote, org.tgdb.project.projectmanager.ProjectManagerRemoteHome.class);
            return rv.create();
        }
        catch(javax.naming.NamingException ne) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE,"exception caught" ,ne);
            throw new RuntimeException(ne);
        }
        catch(javax.ejb.CreateException ce) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE,"exception caught" ,ce);
            throw new RuntimeException(ce);
        }
        catch(java.rmi.RemoteException re) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE,"exception caught" ,re);
            throw new RuntimeException(re);
        }
    }

    private org.tgdb.project.privilege.PrivilegeRemoteHome lookupPrivilegeBean() {
        try {
            javax.naming.Context c = new javax.naming.InitialContext();
            Object remote = c.lookup("java:comp/env/ejb/PrivilegeBean");
            org.tgdb.project.privilege.PrivilegeRemoteHome rv = (org.tgdb.project.privilege.PrivilegeRemoteHome) javax.rmi.PortableRemoteObject.narrow(remote, org.tgdb.project.privilege.PrivilegeRemoteHome.class);
            return rv;
        }
        catch(javax.naming.NamingException ne) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE,"exception caught" ,ne);
            throw new RuntimeException(ne);
        }
    }

    private org.tgdb.project.securityprinciple.SecurityPrincipleRemoteHome lookupSecurityPrincipleBean() {
        try {
            javax.naming.Context c = new javax.naming.InitialContext();
            Object remote = c.lookup("java:comp/env/ejb/SecurityPrincipleBean");
            org.tgdb.project.securityprinciple.SecurityPrincipleRemoteHome rv = (org.tgdb.project.securityprinciple.SecurityPrincipleRemoteHome) javax.rmi.PortableRemoteObject.narrow(remote, org.tgdb.project.securityprinciple.SecurityPrincipleRemoteHome.class);
            return rv;
        }
        catch(javax.naming.NamingException ne) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE,"exception caught" ,ne);
            throw new RuntimeException(ne);
        }
    }

    private org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteHome lookupSamplingUnitBean() {
        try {
            javax.naming.Context c = new javax.naming.InitialContext();
            Object remote = c.lookup("java:comp/env/ejb/SamplingUnitBean");
            org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteHome rv = (org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteHome) javax.rmi.PortableRemoteObject.narrow(remote, org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteHome.class);
            return rv;
        }
        catch(javax.naming.NamingException ne) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE,"exception caught" ,ne);
            throw new RuntimeException(ne);
        }
    }

    private org.tgdb.species.species.SpeciesRemoteHome lookupSpeciesBean() {
        try {
            javax.naming.Context c = new javax.naming.InitialContext();
            Object remote = c.lookup("java:comp/env/ejb/SpeciesBean");
            org.tgdb.species.species.SpeciesRemoteHome rv = (org.tgdb.species.species.SpeciesRemoteHome) javax.rmi.PortableRemoteObject.narrow(remote, org.tgdb.species.species.SpeciesRemoteHome.class);
            return rv;
        }
        catch(javax.naming.NamingException ne) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE,"exception caught" ,ne);
            throw new RuntimeException(ne);
        }
    }

   

   

   
}
