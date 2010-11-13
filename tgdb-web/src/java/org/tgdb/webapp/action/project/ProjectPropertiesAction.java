package org.tgdb.webapp.action.project;

import org.tgdb.frame.Navigator;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.TgDbCaller;
import org.tgdb.project.projectmanager.ProjectDTO;
import org.tgdb.samplingunit.samplingunitmanager.SamplingUnitDTO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.tgdb.webapp.action.*;

public class ProjectPropertiesAction extends TgDbAction {
    
    public ProjectPropertiesAction() {}
    
    public String getName() {
        return "ProjectPropertiesAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException{
        try {
            HttpSession session = request.getSession();
            Navigator nav = (Navigator)session.getAttribute("navigator");
            TgDbCaller _caller = (TgDbCaller)session.getAttribute("caller");            
            
            logger.debug("---------------------------------------->ProjectPropertiesAction#performAction: Setting project");
            
            String pid = request.getParameter("project");
            _caller.setPid(new Integer(pid).intValue());
            
            //caller.updatePrivileges();
            ProjectDTO prj = projectManager.getProject(new Integer(pid).intValue(), _caller);
            session.setAttribute("project.projectdto", prj);

            logger.debug("---------------------------------------->ProjectPropertiesAction#performAction: Setting sampling unit");
            
            SamplingUnitDTO su = samplingUnitManager.getDefaultSamplingUnit(_caller);
            
            _caller.setSuidName(su.getName());
            _caller.setSid(su.getSid());
            _caller.setSuid(su.getSuid());
            
            logger.debug("---------------------------------------->ProjectPropertiesAction#performAction: Setting rows");
            
            String tmpRows = request.getParameter("rows");
            int rows = 20;
            
            try{
                rows = Integer.parseInt(tmpRows);
            } catch (NumberFormatException nfe) {
                logger.error("---------------------------------------->ProjectPropertiesAction#performAction: Number of rows is not an integer");
                throw new ApplicationException("ProjectPropertiesAction" + nfe);
            }
            
            nav.getPageManager().setDelta(new Integer(rows).intValue());
            
            
            return true;
        } catch (ApplicationException ae) {
            ae.printStackTrace();
            throw ae;
        } catch (Exception e) {
            logger.error("---------------------------------------->ProjectPropertiesAction#performAction: Failed");
            throw new ApplicationException("ProjectPropertiesAction" +e.getMessage(),e);
        }
    }
}
