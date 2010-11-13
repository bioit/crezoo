/*
  $Log$
  Revision 1.7  2004/03/26 14:57:53  heto
  Commented file.
  Removed dead code

  Revision 1.6  2004/03/25 16:18:28  heto
  Added methods for loggin

  Revision 1.5  2004/03/19 10:34:56  heto
  Added date to messages

  Revision 1.4  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.3  2003/04/25 09:10:40  heto
  Code layout changes

  Revision 1.2  2002/12/20 09:09:42  heto
  Added a new logfile. Logging to a file will enable the possibility to have a log viewer on the webpage.

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.3  2001/05/28 14:36:18  frob
  Renamed the Debug.Level key.

  Revision 1.2  2001/05/28 11:04:50  frob
  Added logDebug, logWarning and logDebug.

  Revision 1.1  2001/05/04 10:59:54  frob
  Initial checkin.

*/

package se.arexis.agdb.util;

import java.util.*;
import java.io.*;

/**
 * Errors is a class that provides a easy-to-use interface to the error
 * resource bundle. To retrieve the value of a certain key, just pass the
 * key to the static method keyValue() and the related value is returned.
 *
 * <P>The Errors class also includes methods for writing messages to the log.
 *
 * @author frob
 * @see Object
 */
public class Errors extends Object 
{
   /** Definition of the path to the property file to use */
   private final static String BUNDLE = "se.arexis.agdb.util.Errors";

   /**
    * Holds the current debug level. Value is read from the property
    * file.
    */
   private final static int DEBUG_LEVEL =
      Integer.parseInt(keyValue("Debug.Level"));
   

   /** The filename for the logs */
   private static String logFile;
   
   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Returns the value related to the given key.
    *
    * @param keyName The name of the key to lookup.
    * @return The value related to the given key.
    */
   public static String keyValue(String keyName)
   {
      return ResourceBundle.getBundle(BUNDLE).getString(keyName);
   }
   
   /**
    * Set the file name to log to.
    * This setting is comming from the configuration file in tomcat
    * and is set in the init code in ArexisServlet
    */
   public static void setLogFile(String fname)
   {
       logFile = fname;
   }
   
   /** Write log information to tomcat server
    *
    * Level = Info (50)
    * Info indicates method entry/exit and messages on successful operations.
    *
    * Messages should be formated:
    *    <I>Servlet.Method(...) PROCESSID Debug text</I>
    *
    * PROCESSID is important if execution is made in more than one thread at a time.
    *
    * Example:
    * <CODE>
    * public void debug()
    * {
    *    Errors.logInfo("ImportProcess.debug() started");
    *    ...
    *    ...
    *    Errors.logInfo("ImportProcess.debug() ended");
    *    return;
    * }
    * </CODE>
    * @param txt The debug text to print to stderr
    */
   public static void logInfo(String txt)
   {
       if (DEBUG_LEVEL >= 50)
       {
           log("INFO",txt);
       }
   }
   
   /** Write log information to tomcat server
    *
    * Level = Debug (40)
    * Debug should be used to display values of variables at certain points.
    *
    * Messages should be formated:
    *    <I>Servlet.Method(...) PROCESSID variable=565</I>
    *
    * PROCESSID is important if execution is made in more than one thread at a time.
    *
    * Example:
    * <CODE>
    * public void debug()
    * {
    *    Errors.logInfo("ImportProcess.debug() started");
    *    ...
    *    Errors.logDebug("ImportProcess.debug() isid="+isid);
    *    ...
    *    Errors.logInfo("ImportProcess.debug() ended");
    *    return;
    * }
    * </CODE>
    * @param txt The debug text to print to stderr
    */
   public static void logDebug(String txt)
   {
       if (DEBUG_LEVEL >= 40)
       {
           log("DEBUG",txt);
       }
   }
   
   /** Write log information to tomcat server
    *
    * Level = Warn (30)
    * Warn should be used to indicate situations where something retryable has gone
    * wrong. An example is a catch declaration to indicate a rollback.
    *
    * Messages should be formated:
    *    <I>Servlet.Method(...) THREADID debug text</I>
    *
    *
    * PROCESSID is important if execution is made in more than one thread at a time.
    *
    * Example:
    * <CODE>
    * ...
    * catch (Exception e)
    * {
    *    Errors.logWarn("ImportProcess.lock() ISID=4 Failed to lock for import: "+e.getMessage());
    *    out = false;
    *    haveLock = false;
    *
    *    try
    *    {
    *        conn.rollback();
    *        Errors.logWarn("ImportProcess.lock() ISID=4 Rollback");
    *    }
    *    catch (Exception e2)
    *    {
    *    }
    * }
    * </CODE>
    * @param txt The debug text to print to stderr
    */
   public static void logWarn(String txt)
   {
       if (DEBUG_LEVEL >= 30)
       {
           log("WARN",txt);
       }
   }
   
   /** Write log information to tomcat server
    *
    * Level = Error (20)
    * Error should be used to indicate situations where something has gone
    * wrong.
    *
    * Messages should be formated:
    *    <I>Servlet.Method(...) THREADID error message</I>
    *
    * PROCESSID is important if execution is made in more than one thread at a time.
    *
    * Example:
    * <CODE>
    * ...
    * catch (Exception e)
    * {
    *    Errors.logError("ImportProcess.lock() ISID=4 Unexpected error: "+e.getMessage());
    * }
    * </CODE>
    * @param txt The debug text to print to stderr
    */
   public static void logError(String txt)
   {
       if (DEBUG_LEVEL >= 20)
       {
           log("ERROR",txt);
       }
   }
   
   /** Write log information to tomcat server
    *
    * Level = Fatal (10)
    * Fatal should be used to indicate situations where something has gone
    * wrong and the application must shut down immediately.
    *
    * Messages should be formated:
    *    <I>Servlet.Method(...) THREADID error message</I>
    *
    * THREADID is important if execution is made in more than one thread at a time.
    *
    * Example:
    * <CODE>
    * ...
    * catch (Exception e)
    * {
    *    Errors.logError("ImportProcess.lock() ISID=4 Database unavailable. Exiting: "+e.getMessage());
    * }
    * </CODE>
    * @param txt The debug text to print to stderr
    */
   public static void logFatal(String txt)
   {
       if (DEBUG_LEVEL >= 10)
       {
           log("FATAL",txt);
       }
   }
   
   
   /** Writes a warning to System.err.
    * @param logString The text to write.
    * @deprecated logWarn should be used instead
    */
   public static void logWarning(String logString)
   {
       logWarn(logString);
   }

   
   /** Log to a standalone file
    * @param logString The text to write to the log
    * @deprecated Use logInfo, logDebug, logWarn, logError, logFatal instead!
    */
   public static void log(String logString)
   {
       try
       {
           File outputFile = new File(logFile);
           // Append to file
           FileWriter out = new FileWriter(outputFile,true);

           out.write(new Date() + " " + logString+"\n");
           
           out.close();
       }
       catch (Exception e)
       {
           Errors.logError("Unable to write to logfile: "+logFile);
       }
       
   }
   
   
   
   
   
   
   
   //////////////////////////////////////////////////////////////////////
   //
   // Protected section
   //
   //////////////////////////////////////////////////////////////////////


   //////////////////////////////////////////////////////////////////////
   //
   // Private section
   //
   //////////////////////////////////////////////////////////////////////
   
   /** Log to a standalone file
    * @param level The text to write as the level
    * @param logString The text to write to the log
    */
   private static void log(String level, String logString)
   {
       System.err.println(new Date() + " " + level + " " + logString);
   }
}
