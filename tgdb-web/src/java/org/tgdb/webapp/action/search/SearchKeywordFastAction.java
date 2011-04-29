package org.tgdb.webapp.action.search;

import org.tgdb.frame.Action;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.model.modelmanager.ModelManagerRemote;
import org.tgdb.servicelocator.ServiceLocator;
import org.tgdb.webapp.action.TgDbAction;
import java.util.Collection;
import java.util.TreeSet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.tgdb.form.FormDataManager;
import org.tgdb.TgDbFormDataManagerFactory;

public class SearchKeywordFastAction extends TgDbAction {
    
    public String getName() {
        return "SearchKeywordFastAction";
    }
    
    public boolean performAction(HttpServletRequest request, ServletContext context) throws ApplicationException {
        try {
            TgDbCaller _caller = (TgDbCaller)request.getSession().getAttribute("caller");
            ServiceLocator _locator = ServiceLocator.getInstance();
            ModelManagerRemote _modelManager = (ModelManagerRemote)_locator.getManager(ServiceLocator.Services.MODELMANAGER);
            
            collectFormData(TgDbFormDataManagerFactory.FAST_SEARCH, TgDbFormDataManagerFactory.WEB_FORM, request);
            
            FormDataManager formDataManager = getFormDataManager(TgDbFormDataManagerFactory.FAST_SEARCH, TgDbFormDataManagerFactory.WEB_FORM, request);
            
            String tmp = formDataManager.getValue("fast_search_key");
            
            Collection results = _modelManager.searchByKeyword(tmp, _caller);
            request.setAttribute("results", results);
            request.setAttribute("hits", new Integer(results.size()).toString());
            
            
            return true;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationException("SearchKeywordFastAction Failed to perform action", e);
        }
    }
}
