package org.tgdb;

import org.tgdb.frame.Caller;
import org.tgdb.project.privilege.PrivilegeRemote;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.securityprinciple.SecurityPrincipleRemote;
import org.tgdb.project.user.UserRemote;
import org.tgdb.project.user.UserRemoteHome;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemote;
import org.tgdb.samplingunit.samplingunit.SamplingUnitRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


public class TgDbCaller extends Caller implements Serializable {
    
    private int sid;

    private int pid;
    
    private int suid;
    
    private String suidName;
    
    private HashMap projectsHashMap;
    
    public TgDbCaller() {}
    
    public TgDbCaller(UserRemote user) {
        try {
            this.id = user.getId();
            setUsr(user.getUsr());
            setName(user.getName());
            updatePrivileges();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("---------------------------------------->TgDbCaller#TgDbCaller: Failed to initialize ",e);
        }
    }
    
    public int getSuid(){
        return suid;       
    }

    public void setSuid(int suid){
        this.suid = suid;  
        this.setAttribute("suid", suid);
    }
      
    public void setSuidName(String suidName){
        this.suidName = suidName;
        this.setAttribute("suidName", suidName);
    }    
    
    public String getSuidName() {
        return suidName;
    }

 
    public boolean hasPrivilege(String privilegeName) {
        return hasPrivilege(privilegeName, pid);
    }

    public boolean hasPrivilege(String privilegeName, int pid) {
        try {            
            long t1 = System.currentTimeMillis();
            
            if (getPrivHashMap(pid)!=null && getPrivHashMap(pid).containsKey(privilegeName)) {
                long t2 = System.currentTimeMillis();
                //logger.debug("---------------------------------------->TgDbCaller#hasPrivilege: Fetched privilege '"+privilegeName+"' in "+(t2-t1)+" ms");
                return true;
            } else {
                //logger.debug("---------------------------------------->TgDbCaller#hasPrivilege: Privilege '"+privilegeName+"' not granted");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean hasPrivilegeSU(String privilegeName, int suid) {
        long t1 = System.currentTimeMillis();
        try {   
            SamplingUnitRemoteHome suHome = (SamplingUnitRemoteHome)ServiceLocator.getInstance().getHome(ServiceLocator.Services.SAMPLINGUNIT);
            SamplingUnitRemote su = suHome.findByPrimaryKey(new Integer(suid));
            
            Collection projects = su.getProjects();
            Iterator i = projects.iterator();
            ProjectRemote prj = null;
            
            while (i.hasNext()) {
                prj = (ProjectRemote)i.next();
                //logger.debug("---------------------------------------->TgDbCaller#hasPrivilegeSU: Fetched privilege '"+privilegeName+"' for suid = "+suid+" in  pid = "+prj.getPid());
                if (getPrivHashMap(prj.getPid())!=null && getPrivHashMap(prj.getPid()).containsKey(privilegeName)) {
                    long t2 = System.currentTimeMillis();
                    //logger.debug("---------------------------------------->TgDbCaller#hasPrivilegeSU: Fetched privilege '"+privilegeName+"' in "+(t2-t1)+" ms");
                    return true;
                }                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
 
    public int getSid() {
        return sid;
    }
    
    public void setSid(int sid) {
        this.sid = sid;
        this.setAttribute("sid",sid);
    }
    
    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
        this.setAttribute("pid", pid);
    }
    
    public void updatePrivileges() {
        try {
            long t1 = System.currentTimeMillis();
            
            projectsHashMap = new HashMap();
            
            ServiceLocator locator = ServiceLocator.getInstance();
            UserRemoteHome userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
            UserRemote user = userHome.findByPrimaryKey(new Integer(id));
            
            isAdmin = user.isAdmin();
            
            Collection securityPrinciples = user.getSecurityPrinciples();
            SecurityPrincipleRemote sp = null;
            Iterator iPrivs = null;
            Collection arrPrivs = null;
            HashMap tempPrivsHash = null;
            Privilege p = null;
            Iterator i = securityPrinciples.iterator();
            
            while (i.hasNext()) {
                tempPrivsHash = new HashMap();
                
                sp = (SecurityPrincipleRemote)i.next();
                
                // Project object
                Project prj = new Project(sp.getProject());
                
                // Get privileges for project
                arrPrivs = sp.getRole().getPrivileges();
                iPrivs = arrPrivs.iterator();
                while (iPrivs.hasNext()) {
                    p = new Privilege((PrivilegeRemote)iPrivs.next());
                    tempPrivsHash.put(p.getName(), p);
                }
                
                // Set Privileges to project object
                prj.setPrivs(tempPrivsHash);
                
                // Set project object to hashmap
                projectsHashMap.put(new Integer(prj.getPid()), prj);
                        
            }
            long t2 = System.currentTimeMillis();
            logger.debug("---------------------------------------->TgDbCaller#updatePrivileges: Updated privileges in "+(t2-t1)+" ms");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public HashMap getPrivHashMap(int pid) {
        Project prj = (Project)projectsHashMap.get(new Integer(pid));
        
        if (prj!=null)
            return prj.getPrivs();
        else
            return null;
        
    }
}
