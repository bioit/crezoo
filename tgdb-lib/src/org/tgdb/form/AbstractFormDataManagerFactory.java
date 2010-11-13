package org.tgdb.form;

import java.util.ArrayList;

public abstract class AbstractFormDataManagerFactory {
    
    public final static int WEB_FORM = 0;
    public final static int SWING_FORM = 1;
    
    protected static ArrayList names;
    
    public AbstractFormDataManagerFactory() {}
    
    public static String getInstanceName(int id) {
        if(names == null || id > names.size())
            return "unknownName";
        
        return (String)names.get(id);
    }
    
    public abstract FormDataManager createInstance(int name, int type) throws FormDataException;
}
