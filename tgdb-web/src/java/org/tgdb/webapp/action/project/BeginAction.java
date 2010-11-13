package org.tgdb.webapp.action.project;

import org.tgdb.frame.Navigator;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.SamplingUnitNotFoundException;
import org.tgdb.project.projectmanager.ProjectDTO;
import org.tgdb.samplingunit.samplingunitmanager.SamplingUnitDTO;
import org.tgdb.webapp.action.TgDbAction;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class BeginAction extends TgDbAction {
    
    private static final int PUBLIC_USER_ID = 1029;
    private static final int DEFAULT_ROWS_PER_PAGE = 20;
    
    public String getName() {
        return "BeginAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            
            logger.debug("---------------------------------------->BeginAction#performAction: Started");
            
            HttpSession session = request.getSession();
            Navigator nav = (Navigator)session.getAttribute("navigator");
            TgDbCaller _caller = (TgDbCaller)session.getAttribute("caller");
            
            /*
             * if the caller is null load the default user
             */
            if(_caller == null) {
                logger.debug("---------------------------------------->BeginAction#performAction: Caller is null; will load public user");
                _caller = new TgDbCaller(projectManager.getPublicUser(PUBLIC_USER_ID));
            }
            
            if(nav == null) {
                nav = new Navigator();
            }
            
            ProjectDTO prj = projectManager.getDefaultProject(_caller);
            
            //store the project in the session
            session.setAttribute("project.projectdto", prj);
            
            _caller.setPid(prj.getPid());
            //caller.updatePrivileges();

            /** Set sampling unit */
            SamplingUnitDTO su = samplingUnitManager.getDefaultSamplingUnit(_caller);
            
            if (su==null) {
                logger.error("---------------------------------------->BeginAction#performAction: SamplingUnit is null");
                throw new SamplingUnitNotFoundException("Sampling unit is null. Default could not be found. Pid="+_caller.getPid());
            }
            _caller.setSuidName(su.getName());
            _caller.setSid(su.getSid());
            _caller.setSuid(su.getSuid());
            
            //store the caller in the session
            session.setAttribute("caller", _caller);
           
            logger.debug("---------------------------------------->BeginAction#performAction: sid = "+_caller.getSid()+", suid = "+_caller.getSuid()+", pid = "+_caller.getPid());
            
            nav.getPageManager().setDelta(new Integer(DEFAULT_ROWS_PER_PAGE).intValue());
            
            //store the navigator in the session
            session.setAttribute("navigator", nav);
            
            logger.debug("---------------------------------------->BeginAction#performAction: Ended");
            
            
            return true;
        } catch (ApplicationException e) {
            logger.error("---------------------------------------->BeginAction#performAction: Failed", e);
            //throw e;
        } catch (Exception e) {
            logger.error("---------------------------------------->BeginAction#performAction: Failed", e);
            //throw new ApplicationException("Failed to init settings", e);
        }
        return true;
    }
}
