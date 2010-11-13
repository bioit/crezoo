package org.tgdb.webapp.action;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


public class LanguageAction extends TgDbAction {
    
    public LanguageAction() {}
    
    public String getName() {
        return "LanguageAction";
    }
    
    public boolean performAction(HttpServletRequest req, ServletContext context) {
        return true;
    }
    
}
