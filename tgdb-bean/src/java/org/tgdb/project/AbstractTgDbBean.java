package org.tgdb.project;

import java.io.PrintWriter;
import org.tgdb.exceptions.ApplicationException;
import org.tgdb.exceptions.PermissionDeniedException;
import org.tgdb.TgDbCaller;
import org.tgdb.id.IdGenerator;
import org.tgdb.id.PostgresId;
import org.tgdb.project.project.ProjectRemote;
import org.tgdb.project.user.UserRemoteHome;
import org.tgdb.servicelocator.ServiceLocator;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;

public abstract class AbstractTgDbBean implements Serializable {
    
    protected static Logger logger = Logger.getLogger(AbstractTgDbBean.class);

    protected TgDbCaller caller;
    
    protected ServiceLocator locator;

    /** This is used in most entity beans */
    protected static UserRemoteHome userHome;
    
    protected static int countMakeConnection;
    protected static int countReleaseConnection;

    public AbstractTgDbBean() {
              
        locator = ServiceLocator.getInstance();
        
        if (userHome==null) userHome = (UserRemoteHome)locator.getHome(ServiceLocator.Services.USER);
    }
    
    
    
    /**
     * The database connection. This can be used after a call to
     * <CODE>makeConnection()</CODE> and before a <CODE>releaseConnection()</CODE>
     */
    protected Connection conn;
    
    /**
     * The id object gets the Id class for the database in use. Use the method
     * getIdGenerator.
     */
    private IdGenerator id;
    
    /**
     * Get the Id generator object
     *
     * Example: to get a rid for a new role the following code should be called:
     * <CODE>rid = getIIdGenerator().getNextId(conn, "roles_seq");</CODE>
     * @return a IdGenerator that should be used.
     */
    protected IdGenerator getIIdGenerator() {
        if (id==null) {
            // create new
            id = new PostgresId();
        }
        return id;
    }
    
    public void setCaller(TgDbCaller caller) {
        this.caller = caller;
    }

//    public TgDbCaller getCaller() {
//        return caller;
//    }
    
    protected synchronized void makeConnection() {
        try {
            countMakeConnection++; 
            
            javax.sql.DataSource ds = null;
            
            javax.naming.Context c = new javax.naming.InitialContext();
            ds = (javax.sql.DataSource)c.lookup("jdbc/crezoo");
            conn = ds.getConnection();
            
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("---------------------------------------->AbstractTgDbBean#makeConnection: Failed to connect to database");
            throw new EJBException("AbstractTgDbBean#makeConnection\n"+e.getMessage());
        }
    }
    
    /**
     * Release an active connection to the database from the connection pool.
     * This must be called after database operations are done. Failure to do
     * so results in a full connection pool.
     */
    protected synchronized void releaseConnection() {
        try {
            countReleaseConnection++;
            
            //logger.debug("---------------------------------------->AbstractTgDbBean#releaseConnection: Released connection");
            
            conn.close();
            conn = null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new EJBException("Unable to close database: "+e.getMessage());
        }
    }
    
    /**
     * Validates if the string passed along as argument is a number.
     * @param field The name of the input field to be tested
     * @param asDouble Sets if the number should be tested as a decimal number
     * @param number The string to validate
     * @throws org.tgdb.exceptions.ApplicationException If the string is not a number
     */
    protected void validate(String field, String number, boolean asDouble) throws ApplicationException {
        String mess = "is not an integer";
        try {
            
            if(asDouble) {
                Double.parseDouble(number);
                mess = "is not a decimal";
            }
            else {
                Integer.parseInt(number);
            }
        } catch(NumberFormatException e){
            throw new ApplicationException(field+" "+mess);
        }
    }
    
    /**
     * Validates the length of a string
     * @param field The name of the data field
     * @param data The data string
     * @param maxLength The maximum allowed length of the string
     * @throws ApplicationException if the length of the data exceeds the max length
     */
    protected void validate(String field, String data, int maxLength) throws ApplicationException {
        if(data == null)
            data = "";
        if(data.length() > maxLength)
            throw new ApplicationException("Input value for '"+field+"' has too many characters.");
    }
    
    /**
     * Validates if the user has the required privilege
     * @param caller The caller to validate
     * @param priv The privilege to validate
     * @throws org.tgdb.exceptions.PermissionDeniedException If the user did not have the privilege
     */
    protected void validate(String priv, TgDbCaller caller) throws PermissionDeniedException {
        if (!caller.hasPrivilege(priv) && !caller.isAdmin())
        {
            PermissionDeniedException pd = new PermissionDeniedException("No privilege: "+priv+" is required for user "+caller.getName()+"["+caller.getId()+"]");
            pd.fillInStackTrace();
            
            throw pd;
        }
    }
    
    protected void validatePid(String priv, TgDbCaller caller, int pid) throws PermissionDeniedException
    {
        if (!caller.hasPrivilege(priv, pid) && !caller.isAdmin())
        {
            PermissionDeniedException pd = new PermissionDeniedException("No privilege: "+priv+" is required for user "+caller.getName()+"["+caller.getId()+"]");
            pd.fillInStackTrace();
            
            throw pd;
        }
    }
    
    protected void validateSU(String priv, TgDbCaller caller, int suid) throws PermissionDeniedException
    {
        if (!caller.hasPrivilegeSU(priv, suid) && !caller.isAdmin())
        {
            PermissionDeniedException pd = new PermissionDeniedException("No privilege: "+priv+" is required for user "+caller.getName()+"["+caller.getId()+"]");
            pd.fillInStackTrace();
            
            throw pd;
        }
    }
    
    /**
     * Validates if the user has the required privilege
     * @param projects a collection of ProjectRemote objects
     * @param caller The caller to validate
     * @param priv The privilege to validate
     * @throws org.tgdb.exceptions.PermissionDeniedException If the user did not have the privilege
     */
    protected void validate(String priv, TgDbCaller caller, Collection projects) throws PermissionDeniedException {
        
        // If admin, dont bother to check more, privilege granted.
        if (caller.isAdmin())
            return;
                
        boolean projectMatch = false;
        try {
            Iterator i = projects.iterator();
            while (i.hasNext()) {
                ProjectRemote prj = (ProjectRemote)i.next();
                logger.debug("---------------------------------------->AbstractTgDbBean#validate: Project '"+prj.getName()+"'");
                if (prj.getPid() == caller.getPid())
                    projectMatch = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (!projectMatch) {
            logger.error("---------------------------------------->AbstractTgDbBean#validate: User not assigned to project");
            throw new PermissionDeniedException("User not assigned to project");
        }
        
        //logger.debug("---------------------------------------->AbstractTgDbBean#validate: User qualification '"+projectMatch+"'");
        
        if (!caller.hasPrivilege(priv)) {
            logger.error("---------------------------------------->AbstractTgDbBean#validate: No '"+priv+"' privilege for user '"+caller.getName()+"'");
            PermissionDeniedException pd = new PermissionDeniedException("No privilege: "+priv+" is required for user "+caller.getName()+"["+caller.getId()+"]");
            //pd.fillInStackTrace();
            throw pd;
        } 
    }
    
    /**
     * Validates if the user has the required privilege
     * @param caller The caller to validate
     * @param priv The privilege to validate
     * @throws org.tgdb.exceptions.PermissionDeniedException If the user did not have the privilege
     */
    protected void validate(String priv, String message, TgDbCaller caller) throws PermissionDeniedException {
        if (!caller.hasPrivilege(priv))
            throw new PermissionDeniedException("No privilege: "+message);
    }
    
    /**
     * Validates a date
     * @param field The name of the field to validate
     * @param date The date to validate
     * @param format The format to validate
     * @throws org.tgdb.exceptions.ApplicationException If the date was in wrong format
     */
    protected void validate(String field, String date, SimpleDateFormat format) throws ApplicationException {
        if(date == null)
            date = "";        
        try {
            SimpleDateFormat sdf = format;
            sdf.setLenient(false);
            Date dt2 = sdf.parse(date);
        } catch (ParseException e) {
            throw new ApplicationException("Input value for field '"+field+"' is on wrong format for date. Expected format is: "+format.toPattern());
        } catch (IllegalArgumentException e) {
            throw new ApplicationException("Input value for field '"+field+"' is on wrong format for date. Expected format is: "+format.toPattern());
        }
    }

    public boolean exists(String value) {

        if(value != null && value.length() > 0)
            return true;
        else
            return false;
    }
    
    protected String buildQueryConditions(ParamDataObject qdo) {
        String sql = "";      

        if(qdo != null && qdo.hasValues()) {
            ArrayList list = qdo.getKeys();
            String column = "";
            String data = "";

            for(int i=0;i<list.size();i++) {
                column = (String)list.get(i);
                data = qdo.getValue(column);
                if(data.length() > 0)
                    sql += " AND "+column+"='"+data+"'";
            }
        }  
        
        return sql;
    }

    /*
     * Returns the stack trace as string
     */
    public String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }
}
