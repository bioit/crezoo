
package org.tgdb.export;

import org.tgdb.frame.PageManager;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.project.ParamDataObject;
import java.util.Collection;


/**
 * This is the business interface for ExportManager enterprise bean.
 */
public interface ExportManagerRemoteBusiness {
    int getNumberOfFilters(ParamDataObject pdo) throws java.rmi.RemoteException, ApplicationException;

    Collection getFilters(ParamDataObject pdo, TgDbCaller caller, PageManager pageManager) throws java.rmi.RemoteException, ApplicationException;

    GQLFilterDTO getFilter(int fid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void updateFilter(int fid, int sid, java.lang.String name, java.lang.String comm, java.lang.String expression, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    int createFilter(java.lang.String name, java.lang.String comm, java.lang.String expression, int sid, int pid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;

    void removeFilter(int fid, TgDbCaller caller) throws org.tgdb.exceptions.ApplicationException, java.rmi.RemoteException;
    
}
