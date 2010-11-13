
package org.tgdb.expression.expressionmodel;

import java.rmi.RemoteException;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;


/**
 * This is the home interface for ExpressionModel enterprise bean.
 */
public interface ExpressionModelRemoteHome extends EJBHome {
    
    org.tgdb.expression.expressionmodel.ExpressionModelRemote findByPrimaryKey(Integer key)  throws FinderException, RemoteException;
    
    org.tgdb.expression.expressionmodel.ExpressionModelRemote create(int exid, java.lang.String exanatomy, java.lang.String excomm) throws javax.ejb.CreateException, java.rmi.RemoteException;
    
    java.util.Collection findByModel(int eid) throws javax.ejb.FinderException, java.rmi.RemoteException;

}
