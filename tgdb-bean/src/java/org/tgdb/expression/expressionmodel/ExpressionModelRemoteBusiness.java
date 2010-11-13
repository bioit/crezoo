
package org.tgdb.expression.expressionmodel;

import java.util.Collection;
import org.tgdb.exceptions.ApplicationException;

public interface ExpressionModelRemoteBusiness {
    
    int getExid() throws java.rmi.RemoteException;

    java.lang.String getExanatomy() throws java.rmi.RemoteException;

    void setExanatomy(String exanatomy) throws java.rmi.RemoteException;

    java.lang.String getExcomm() throws java.rmi.RemoteException;

    void setExcomm(String excomm) throws java.rmi.RemoteException;

    java.util.Collection getFiles() throws java.rmi.RemoteException;

    void addFile(int fileid) throws ApplicationException, java.rmi.RemoteException;

    void addOntology(String oid, String namespace) throws ApplicationException, java.rmi.RemoteException;

    void deleteOntology(String oid, String namespace) throws ApplicationException, java.rmi.RemoteException;

    Collection getOntologyTerms(String namespace) throws ApplicationException, java.rmi.RemoteException;
    
}
