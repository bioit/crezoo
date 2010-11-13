package org.tgdb.export;
import org.tgdb.frame.PageManager;
import org.tgdb.TgDbCaller;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.export.filter.GQLFilterRemote;
import org.tgdb.export.filter.GQLFilterRemoteHome;
import org.tgdb.project.AbstractTgDbBean;
import org.tgdb.project.ParamDataObject;
import org.tgdb.servicelocator.ServiceLocator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This is the bean class for the ExportManagerBean enterprise bean.
 * Created Nov 16, 2005 12:58:09 PM
 * @author lami
 */
public class ExportManagerBean extends AbstractTgDbBean implements javax.ejb.SessionBean, org.tgdb.export.ExportManagerRemoteBusiness {
    private javax.ejb.SessionContext context;
    private GQLFilterRemoteHome frh;
    
    // <editor-fold defaultstate="collapsed" desc="EJB infrastructure methods. Click the + sign on the left to edit the code.">
    // TODO Add code to acquire and use other enterprise resources (DataSource, JMS, enterprise bean, Web services)
    // TODO Add business methods or web service operations
    /**
     * Sets the session context
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     * @param aContext The session context
     */
    public void setSessionContext(javax.ejb.SessionContext aContext) {
        context = aContext;
    }
    
    /**
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() {
        
    }
    
    /**
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    public void ejbPassivate() {
        
    }
    
    /**
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    public void ejbRemove() {
        
    }
    // </editor-fold>
    
    /**
     * See section 7.10.3 of the EJB 2.0 specification
     * See section 7.11.3 of the EJB 2.1 specification
     */
    public void ejbCreate() {
        // TODO implement ejbCreate if necessary, acquire resources
        // This method has access to the JNDI context so resource aquisition
        // spanning all methods can be performed here such as home interfaces
        // and data sources.
        frh = (GQLFilterRemoteHome)locator.getHome(ServiceLocator.Services.GQLFILTER);        
    }
    
    
    
    // Add business logic below. (Right-click in editor and choose
    // "EJB Methods > Add Business Method" or "Web Service > Add Operation")

    /**
     * Returns the number of filters in the database
     * @param pdo The paramdataobject containing query specific details
     * @throws org.tgdb.exceptions.ApplicationException If the number of filters could not be retrieved
     * @return The number of filters in the database
     */
    public int getNumberOfFilters(ParamDataObject pdo) throws ApplicationException {
        try {                       
            return frh.getNumberOfFilters(pdo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not count filters.");
        }
    }

    /**
     * Returns a collection of filters.
     * @param pdo The ParamDataObject with information regarding how the query should be restricted
     * @param caller The current caller object
     * @param pageManager The PageManager object that holds info regarding how many objects that should be shown per listing
     * @throws org.tgdb.exceptions.ApplicationException If the filters could not be retrieved
     * @return A collection of filters
     */
    public Collection getFilters(org.tgdb.project.ParamDataObject pdo, TgDbCaller caller, PageManager pageManager) throws ApplicationException {
        validate("FLT_R", caller);
        Collection data = new ArrayList();
        try {
            int index = 0;
            
            // Get ALL filters using the pdo restrictions
            Collection filters = frh.findByQuery(pdo, pageManager);
            
            Iterator itr = filters.iterator();
            
            int start = pageManager.getStart();
            int stop = pageManager.getStop();
            
            while (itr.hasNext()) {
                index++;
                
                // Add the filter as long as we dont have exceeded the 
                // amount to fetch (number of rows to display per page)
                if (index>=start && index<=stop) {
                    GQLFilterRemote f = (GQLFilterRemote)itr.next();
                    data.add(new GQLFilterDTO(f));
                } else {          
                    // Skip this object. This is outside the interval
                    itr.next();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not get filters");
        }
        
        return data;
    }            

    /**
     * Returns the filter with id = fid
     * @param fid The filter id
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If the filter could not be retrieved
     * @return A filter
     */
    public GQLFilterDTO getFilter(int fid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("FLT_R", caller);
        try {                       
            // Build a new data transfer object and return it
            return new GQLFilterDTO(frh.findByPrimaryKey(new Integer(fid)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not get filter.");
        }
    }

    /**
     * Updates the filter with fid
     * @param fid The id of the filter to update
     * @param sid The species id
     * @param name The name of the filter
     * @param comm The comment for the filter
     * @param expression The expression for the filter
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If the filter could not be updated
     */
    public void updateFilter(int fid, int sid, java.lang.String name, java.lang.String comm, java.lang.String expression, TgDbCaller caller) throws ApplicationException {
        validate("FLT_W", caller);
        validate("Name", name, 20);
        validate("Comment", comm, 256);      
        validate("Expression", expression, 2000); 
        try {                       
            // Find the filter...set the caller and update the fetched filter
            GQLFilterRemote f = frh.findByPrimaryKey(new Integer(fid));            
            f.setCaller(caller);
            f.addHistory();
            
            f.setSid(sid);
            f.setName(name);
            f.setComm(comm);
            f.setExpression(expression);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not update filter.");
        }
    }

    /**
     * Creates a new filter
     * @param name The name of the filter
     * @param comm The comment for the filter
     * @param expression The expression for the filter
     * @param sid The species id for the filter
     * @param pid The project id for the filter
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If the filter could not be created
     * @return The id for the new filter
     */
    public int createFilter(java.lang.String name, java.lang.String comm, java.lang.String expression, int sid, int pid, org.tgdb.TgDbCaller caller) throws ApplicationException {
        validate("FLT_W", caller);
        validate("Name", name, 20);
        validate("Comment", comm, 256);        
        validate("Expression", expression, 2000);        
        int fid = 0;
        try {
            // Connect to DB, request a new ID and the create the filter
            makeConnection();
            fid = getIIdGenerator().getNextId(conn, "filters_seq");
                    
            GQLFilterRemote filter = frh.create(fid, name, comm, expression, sid, pid, caller);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not create a new filter.");
        } finally {
            releaseConnection();
        }
        
        return fid;
    }

    /**
     * Removes thee filter from the database
     * @param fid The id of the filter to remove
     * @param caller The current caller object
     * @throws org.tgdb.exceptions.ApplicationException If the filter could not be removed
     */
    public void removeFilter(int fid, TgDbCaller caller) throws ApplicationException {
        validate("FLT_W", caller);
        try{
            // Fetch the filter and call its remove()
            frh.findByPrimaryKey(new Integer(fid)).remove();
            
        }catch(Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Could not remove filter.");
        }
    }
    
    
}
