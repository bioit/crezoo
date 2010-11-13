/*
 * ImportIndPreAction.java
 *
 * Created on May 3, 2006, 4:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tgdb.webapp.action.fileimport;

import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author heto
 */
public class ImportIndPreAction extends TgDbAction {
    
    /** Creates a new instance of ImportIndPreAction */
    public ImportIndPreAction() {
    }
    
    public String getName() {
        return "ImportIndPreAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            Collection samplingunits = this.samplingUnitManager.getSamplingUnits(((TgDbCaller)caller).getPid(), (TgDbCaller)caller);
            request.setAttribute("samplingunits",samplingunits);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }
    
}
