/*

 $Log$
 Revision 1.16  2005/02/08 16:03:21  heto
 DbIndividual is now complete. Some bug tests are done.
 DbSamplingunit is converted. No bugtest.
 All transactions should now be handled in the GUI (yuck..)

 Revision 1.15  2005/02/07 15:54:01  heto
 Converted DbIndividual to PostgreSQL
 Now some transaction problem occures with Groupings (update)

 Revision 1.14  2004/03/25 17:04:43  heto
 Fixing debug messages.
 Removed dead code

 Revision 1.13  2004/03/15 13:56:20  heto
 Removed border on statistics page

 Revision 1.12  2004/03/02 08:44:54  wali
 Improved writeIndividual to include different date formats. createResults fgid handling improved

 Revision 1.11  2004/03/01 08:26:34  wali
 Renamed retrieveString to retrieveSymbol and retrieveApostrophe to retrieveSym. Added some symbols.

 Revision 1.10  2004/02/13 13:23:31  wali
 Extended with retrieveString. Changes ¤ to '.

 Revision 1.9  2003/05/02 07:58:45  heto
 Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
 Modified configuration and source files according to package change.

 Revision 1.8  2003/04/25 12:14:45  heto
 Changed all references to axDefault.css
 Source layout fixes.

 Revision 1.7  2003/04/25 08:58:21  heto
 Added a function to print the default stylesheet.

 Revision 1.6  2002/12/20 09:19:21  heto
 Added a simple error page method.

 Revision 1.5  2002/12/13 15:00:57  heto
 Comments added

 Revision 1.4  2002/11/21 10:49:40  heto
 Changed to specification 1.3 or 1.4.
 session attributes has changed.
 Moved functions from other parts

 Revision 1.3  2002/10/22 06:08:07  heto
 rebuilt the "back-buttons".
 Dont save the request object, save the URL instead.
 New function.

 Revision 1.2  2002/10/18 14:32:24  heto
 No particular change.

 Revision 1.1.1.1  2002/10/16 18:14:04  heto
 Import of aGDB 1.5 L3 from Prevas CVS-tree.
 This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


 Revision 1.26  2001/06/18 09:07:39  frob
 Several new, common methods added.

 Revision 1.25  2001/06/13 06:06:22  frob
 Changed the structure of the header table produced in HTMLWriter. From now, the table
 has only two rows. Any other stuff has to be placed within a content table (also
 produced byt the HTMLWriter). This modification caused updates in several servlets.

 Revision 1.24  2001/05/30 13:45:06  frob
 Moved writeStatisticsPage to ArexisServlet.

 Revision 1.23  2001/05/28 13:50:12  frob
 Removed logError method, use Errors.logError instead.

 Revision 1.22  2001/05/28 06:34:07  frob
 Adoption to changes in HTMLWriter.

 Revision 1.21  2001/05/23 12:32:32  roca
 fixed choice of chromosome for markerset membership
 changed order of links in adminProj. "Order by name" added it adm species & SU's

 Revision 1.20  2001/05/23 11:52:38  frob
 New method: logError

 Revision 1.19  2001/05/22 12:17:56  frob
 Minor fix in commitOrRollback.

 Revision 1.18  2001/05/22 06:16:49  roca
 Backfuncktionality fixed for all (?) servlets/subpages

 Revision 1.17  2001/05/21 08:17:06  roca
 Roca fixed nullreplacement for files, privs not displayed and counter in phenotypes

 Revision 1.16  2001/05/21 06:50:42  frob
 Minor update caused by update in HTMLWriter.

 Revision 1.15  2001/05/18 06:16:50  frob
 Restructured the file. Added code for getting/setting the application version.

 Revision 1.14  2001/05/16 08:55:26  frob
 Minor fix.

 Revision 1.13  2001/05/15 12:13:13  frob
 Added call to defaultCSS() in writeErrorPage() to as the css part is moved to a
 separate method.

 Revision 1.12  2001/05/14 12:32:05  frob
 Bugfix in writeErrorPage(), back button path were incorrect created.

 Revision 1.11  2001/05/09 12:51:36  frob
 Added new method: commitOrRollback()

 Revision 1.10  2001/05/04 11:14:45  frob
 Modified to use Errors class to find text to display on page. Interface changed.

 Revision 1.9  2001/05/03 13:18:09  frob
 New method: errorQueryString, returns query string to use when going back from error page.

 Revision 1.8  2001/05/03 07:55:10  frob
 Minor fix in removeQSParameter.
 Added methods for removing common parameters.

 */

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;


import se.arexis.agdb.util.*;

/**
 * An abstract class that provides session control for the servlets
 * in the Arexis genetic database project. It extends the
 * <code>HttpServlet</code> which simplifies writing HTTP servlets.
 * This class overrides the <code>service</code> method and verifies
 * that the requesting client has the necessary information stored in
 * her/his session object. If not, the client is being redirected to
 * another location (redirectClass)
 * Because it is an abstract class, servlet writers must subclass it
 * and override at least one method.
 * The methods normally overridden are:
 *
 * <ul>
 *      <li> <code>doGet</code>, if HTTP GET requests are supported.
 *	Overriding the <code>doGet</code> method automatically also
 *	provides support for the HEAD and conditional GET operations.
 *	Where practical, the <code>getLastModified</code> method should
 *	also be overridden, to facilitate caching the HTTP response
 *	data.  This improves performance by enabling smarter
 *	conditional GET support.
 *
 *	<li> <code>doPost</code>, if HTTP POST requests are supported.
 *      <li> <code>doPut</code>, if HTTP PUT requests are supported.
 *      <li> <code>doDelete</code>, if HTTP DELETE requests are supported.
 *
 *	<li> The lifecycle methods <code>init</code> and
 *	<code>destroy</code>, if the servlet writer needs to manage
 *	resources that are held for the lifetime of the servlet.
 *	Servlets that do not manage resources do not need to specialize
 *	these methods.
 *
 *	<li> <code>getServletInfo</code>, to provide descriptive
 *	information through a service's administrative interfaces.
 *      </ul>
 *
 * <P>Notice that the <code>service</code> method is not typically
 * overridden.  The <code>service</code> method, as provided, supports
 * standard HTTP requests by dispatching them to appropriate methods,
 * such as the methods listed above that have the prefix "do". That is,
 * if the user has the necessary session data. Otherwise the servlet will
 * respond with the redirect HTTP-header.
 * In addition, the service method also supports the HTTP 1.1 protocol's
 * TRACE and OPTIONS methods by dispatching to the <code>doTrace</code>
 * and <code>doOptions</code> methods.  The <code>doTrace</code> and
 * <code>doOptions</code> methods are not typically overridden.
 *
 * <P>Servlets typically run inside multi-threaded servers; servlets
 * must be written to handle multiple service requests simultaneously.
 * It is the servlet writer's responsibility to synchronize access to
 * any shared resources.  Such resources include in-memory data such as
 * instance or class variables of the servlet, as well as external
 * components such as files, database and network connections.
 * Information on multithreaded programming in Java can be found in the
 * <a
 * href="http://java.sun.com/Series/Tutorial/java/threads/multithreaded.html">
 * Java Tutorial on Multithreaded Programming</a>.
 *
 * @version 1.0, 2000-10-05
 */
public abstract class ArexisServlet extends HttpServlet
{
   private String m_redirectPath;
   private boolean m_missingData;
   private String m_zone;
   private String m_rootPath;
   private String m_upFilePath;
   private String m_fileGeneratePath;
   private String m_nullReplacement;
   private int m_maxRows;
   private int m_maxDeviations;
   private String m_dateFormat;

   /** The version of the application */
   private String mApplicationVersion = null;
   
   
   //////////////////////////////////////////////////////////////////////
   //
   // Constructors
   //
   //////////////////////////////////////////////////////////////////////
   
   public ArexisServlet()
   {
   }

   //////////////////////////////////////////////////////////////////////
   //
   // Public section 
   //
   //////////////////////////////////////////////////////////////////////
   
   /** Reads specific init-parameters using the ServletConfig object. If
    * init-parameters are missing a flag is set for later use in the
    * service method. Finally the inherited version of the method is
    * called.
    *
    * @throws ServletException  */
   public void init() throws ServletException
   {
      m_missingData = false;
      
      ServletContext config = this.getServletContext();

      // Get init parameters
      m_nullReplacement = config.getInitParameter("nullReplacement");
      m_redirectPath = config.getInitParameter("redirectPath");
      m_zone = config.getInitParameter("zone");
      m_rootPath = config.getInitParameter("rootPath");
      m_upFilePath = config.getInitParameter("upFilePath");
      m_fileGeneratePath = config.getInitParameter("fileGeneratePath");
      String maxRows = config.getInitParameter("maxRows");
      m_dateFormat = config.getInitParameter("dateFormat");
      String maxDev = config.getInitParameter("maxDeviations");
      applicationVersion(config.getInitParameter("version"));
      
      
      
      // If any parameter is missing, set missing flag
      if (m_redirectPath == null || m_zone == null ||
          m_rootPath == null || m_nullReplacement == null ||
          m_upFilePath == null || m_fileGeneratePath == null ||
          maxRows == null || m_dateFormat == null || maxDev == null ||
          applicationVersion() == null)
      {
         m_missingData = true;
      }

      // if all parameters found, parse the integer values
      else
      {
         m_maxRows = Integer.parseInt(maxRows);
         m_maxDeviations = Integer.parseInt(maxDev);
      }

      //super.init(config);
   }
   
   public void destroy()
   {
       Errors.logInfo("ArexisServlet.destroy()");
       super.destroy();
   }
   


   /** A simple method that returns the value of the private variable zonePath
    * (i.e where the servlets should reside).
    * @param servletName
    * @return Returns a string of the path for the zone
    *
    */
   public String getServletPath(String servletName)
   {
      return m_rootPath + m_zone + servletName ;
   }


   /**
    * A simple method that returns the value of the private variable upFilePath
    *(i.e where the uploaded files should be stored).
    *
    * @return Returns a string of the path to the upload directory. This value 
    * is initiated in the configuration file.
    */
   public String getUpFilePath()
   {
      return m_upFilePath ;
   }


   /**
    * A simple method that returns the value of the private variable FileGeneratePath
    *(i.e where the generated analyses files should be stored).
    */
   public String getFileGeneratePath()
   {
      return m_fileGeneratePath;
   }


   /**
    * A simple method that returns the value of the private variable zonePath
    *(i.e where the servlets should reside).
    */
   public boolean checkMissingData()
   {
      return m_missingData;
   }


   /**
    * A simple method that returns a complete URL consisting of the rootPath
    * given by inparameters and the desired extension given by "target"
    */
   public String getURL(String target)
   {
      return m_rootPath + target;
   }


   /**
    * A simple method that returns the value to be used to replace NULL in HTML
    * output
    */
   public String getNullReplacement(HttpSession session)
   {
      String nullR = null;
      if (session != null)
         nullR = (String) session.getAttribute("nullReplacement");
      if (nullR == null)
         nullR = m_nullReplacement;
      return nullR;
   }

   /**
    * A simple method that returns the value to be used to replace NULL in FILE
    * output
    */
   public String getFileNullReplacement(HttpSession session)
   {
      String nullR = null;
      if (session != null)
         nullR = (String) session.getAttribute("nullReplacement");
      if (nullR == null)
         nullR = m_nullReplacement;
      if (nullR.equalsIgnoreCase("&nbsp;"))
         nullR = " ";

      return nullR;
   }


   /**
    * Sets the value that will be returned from the method <code>
    * getNullReplacement </code>
    */
   public void setNullReplacement(HttpSession session, String newNR) 
   {
      if (session != null )
         session.putValue("nullReplacement", newNR);
   }


   /**
    * @param session
    * @return  */   
   public String getDateFormat(HttpSession session)
   {
      String datef = null;
      if (session != null)
         datef = (String) session.getAttribute("dateFormat");
      if (datef == null)
         datef = m_dateFormat;
      return datef;
   }


   /**
    * @param session
    * @param newDF  */   
   public void setDateFormat(HttpSession session, String newDF)
   {
      if (session != null)
         session.putValue("dateFormat", newDF);
   }


   /**
    * @param session
    * @return  */   
   public int getMaxRows(HttpSession session)
   {
      String maxRows = null;
      if (session != null)
         maxRows = (String) session.getAttribute("maxRows");
      if (maxRows == null)
         maxRows = Integer.toString(m_maxRows);
      return Integer.parseInt(maxRows);
   }


   /**
    * @param session
    * @param newMR  */   
   public void setMaxRows(HttpSession session, int newMR)
   {
      if (session != null)
         session.putValue("maxRows", Integer.toString(newMR) );
   }


   /**
    * @return  */   
   public int getMaxDeviations()
   {
      return m_maxDeviations;
   }


   /**
    * Returns the application version.
    *
    * @return The application version.
    */
   public String applicationVersion()
   {
      return mApplicationVersion;
   }
   

   //////////////////////////////////////////////////////////////////////
   //
   // Protected section
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Writes the error page.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param rootKey The name of the root key to use when retrieving the
    *                text to display on the page.
    * @param errorMessage The message from the database object.
    * @param servletName The name of the servled where error ocurred.
    * @exception ServletException If no PrintWriter could be created.
    * @exception IOException If no PrintWriter could be created.
    */
   protected void writeErrorPage(HttpServletRequest request,
                                 HttpServletResponse response,
                                 String rootKey,
                                 String errorMessage,
                                 String servletName)
      throws ServletException, IOException
   {
      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");
      PrintWriter out = response.getWriter();
      try
      {
         HTMLWriter.doctype(out);
         HTMLWriter.openHTML(out);
         HTMLWriter.openHEAD(out, "");
         HTMLWriter.defaultCSS(out);
         HTMLWriter.closeHEAD(out);

         HTMLWriter.openBODY(out, "");
         HTMLWriter.headerTable(out, 0, Errors.keyValue(rootKey +
                                                        ".Error"));

         // Start the content table and write the text to the page
         HTMLWriter.contentTableStart(out, 0);
         out.println("<TABLE border=0 cellspacing=0 cellpadding=0>\n"+
                     "  <TR>\n" + 
                     "    <TD>" +
                     Errors.keyValue(rootKey + ".Error.Msg") +
                     "</TD>\n" +
                     "  </TR>\n" +
                     "  <TR>\n" + 
                     "    <TD>&nbsp;</TD>\n" +
                     "  </TR>\n" +
                     "  <TR>\n" +
                     "    <TD>Error message:</TD>\n" +
                     "  </TR>\n" +
                     "  <TR>\n" + 
                     "    <TD>&nbsp;</TD>\n" +
                     "  </TR>\n" +
                     "  <TR>\n" +
                     "    <TD>" + errorMessage + "</TD>\n" +
                     "  </TR>\n" +
                     "  <TR>\n" + 
                     "    <TD>&nbsp;</TD>\n" +
                     "  </TR>\n" +
                     "  <TR>\n" +
                     "    <TD>\n" + 
                     "      <FORM action=\"\">\n" +
                     HTMLWriter.backButton("JavaScript:location.href=\"" +
                                           getServletPath(servletName) +
                                           errorQueryString(request) + 
                                           "\";") +
                     "\n      </FORM>\n" +
                     "    </TD>\n" +
                     "  </TR>\n" + 
                     "</TABLE>");
         HTMLWriter.contentTableEnd(out);
         HTMLWriter.closeBODY(out);
         HTMLWriter.closeHTML(out);
      }
      catch (Exception e)
      {
         e.printStackTrace(System.err);
      }
   }

   
   /**
    * Writes the error page. This is a simpler variant with no predefined messages.
    *
    * @param request The request object to use.
    * @param response The response object to use.
    * @param rootKey The name of the root key to use when retrieving the
    *                text to display on the page.
    * @param errorMessage The message from the database object.
    * @param servletName The name of the servled where error ocurred.
    * @exception ServletException If no PrintWriter could be created.
    * @exception IOException If no PrintWriter could be created.
    */
   protected void writeErrorPage(HttpServletRequest request,
                                 HttpServletResponse response,
                                 String heading,
                                 String errorMessage)
      throws ServletException, IOException
   {
      response.setContentType("text/html");
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");
      PrintWriter out = response.getWriter();
      try
      {
         HTMLWriter.doctype(out);
         HTMLWriter.openHTML(out);
         HTMLWriter.openHEAD(out, "");
         HTMLWriter.defaultCSS(out);
         HTMLWriter.closeHEAD(out);

         HTMLWriter.openBODY(out, "");
         HTMLWriter.headerTable(out, 0, heading);

         // Start the content table and write the text to the page
         HTMLWriter.contentTableStart(out, 0);
         out.println("<TABLE border=0 cellspacing=0 cellpadding=0>\n"+
                     "  <TR>\n" + 
                     "    <TD>" +
                     //heading +
                     "</TD>\n" +
                     "  </TR>\n" +
                     "  <TR>\n" + 
                     "    <TD>&nbsp;</TD>\n" +
                     "  </TR>\n" +
                     "  <TR>\n" +
                     "    <TD>Error message:</TD>\n" +
                     "  </TR>\n" +
                     "  <TR>\n" + 
                     "    <TD>&nbsp;</TD>\n" +
                     "  </TR>\n" +
                     "  <TR>\n" +
                     "    <TD>" + errorMessage + "</TD>\n" +
                     "  </TR>\n" +
                     "  <TR>\n" + 
                     "    <TD>&nbsp;</TD>\n" +
                     "  </TR>\n" +
                     "  <TR>\n" +
                     "    <TD>\n" + 
                     "      <FORM action=\"\">\n" +
                     "\n      </FORM>\n" +
                     "    </TD>\n" +
                     "  </TR>\n" + 
                     "</TABLE>");
         HTMLWriter.contentTableEnd(out);
         HTMLWriter.closeBODY(out);
         HTMLWriter.closeHTML(out);
      }
      catch (Exception e)
      {
         e.printStackTrace(System.err);
      }
   }

   /**
    * Removes a parameter from a query string.
    *
    * @param originalQS The query string to remove the paramter from.
    * @param parameterName The name of the parameter to remove.
    * @return The query string with the parameter removed.
    */
   protected String removeQSParameter(String originalQS,
                                      String parameterName)
   {
      String returnQS;

      parameterName = parameterName + "=";
      
      // Find the start position of the parameter in the query string
      int startPos = originalQS.indexOf(parameterName);

      // If parameter not found, return the original string 
      if (startPos < 0)
      {
         returnQS = originalQS;
      }

      // if found, remove parameter
      else
      {
         // Find the end position of the parameter by finding the
         // position of the following parameter, indicated by a '&'
         int endPos = originalQS.indexOf("&", startPos +
                                         parameterName.length()); 
         
         // No additional parameter was found, means that the given
         // parameter is the last one. Return the part of the query string
         // that precedes the given parameter.
         if (endPos < 0)
         {
            returnQS = originalQS.substring(0, startPos);
         }

         // An additional parameter was found. Return the part of the query
         // string that precedes the given parameter as well as the part
         // that follows it
         else
         {
            returnQS = originalQS.substring(0, startPos) +
               originalQS.substring(endPos + 1); 
         }
      }
      return returnQS;
   }


   /**
    * Removes the "oper" parameter from the query string
    *
    * @param originalQS The query string to remove the parameter from. 
    * @return The query string with the parameter removed.
    */
   protected String removeQSParameterOper(String originalQS)
   {
      return removeQSParameter(originalQS, "oper");
   }
   
   
   /**
    * Removes the "sid" parameter from the query string.
    *
    * @param originalQS The query string to remove the parameter from. 
    * @return The query string with the parameter removed.
    */
   protected String removeQSParameterSid(String originalQS)
   {
      return removeQSParameter(originalQS, "sid");
   }
   

   /**
    * Removes the "cid" parameter from the query string.
    *
    * @param originalQS The query string to remove the parameter from. 
    * @return The query string with the parameter removed.
    */
   protected String removeQSParameterCid(String originalQS)
   {
      return removeQSParameter(originalQS, "cid");
   }


   /**
    * Removes the "pid" parameter from the query string.
    *
    * @param originalQS The query string to remove the parameter from. 
    * @return The query string with the parameter removed.
    */
   protected String removeQSParameterPid(String originalQS)
   {
      return removeQSParameter(originalQS, "pid");
   }


   /**
    * Removes the "id" parameter from the query string.
    *
    * @param originalQS The query string to remove the parameter from. 
    * @return The query string with the parameter removed.
    */
   protected String removeQSParameterId(String originalQS)
   {
      return removeQSParameter(originalQS, "id");
   }


   /**
    * Removes the "mid" parameter from the query string.
    *
    * @param originalQS The query string to remove the parameter from. 
    * @return The query string with the parameter removed.
    */
   protected String removeQSParameterMid(String originalQS)
   {
      return removeQSParameter(originalQS, "mid");
   }


   /**
    * Removes the "iid" parameter from the query string.
    *
    * @param originalQS The query string to remove the parameter from. 
    * @return The query string with the parameter removed.
    */
   protected String removeQSParameterIid(String originalQS)
   {
      return removeQSParameter(originalQS, "iid");
   }


   /**
    * Removes the "suid" parameter from the query string.
    *
    * @param originalQS The query string to remove the parameter from. 
    * @return The query string with the parameter removed.
    */
   protected String removeQSParameterSuid(String originalQS)
   {
      return removeQSParameter(originalQS, "suid");
   }


   /**
    * Returns the query string that should be used when going back from the
    * error page. Default an empty string is returned.
    *
    * @param request The request object to use when building the string.
    * @return The an empty string.
    */
   protected String errorQueryString(HttpServletRequest request)
   {
      return "";
   }
   

   /**
    * Commits or rollback a database operations. If operation is
    * rollbacked, an error page is written.
    *
    * @param connection The connection object to use.
    * @param request The request object to use.
    * @param response The response object to use.
    * @param rootKey The root key to use when finding error strings in
    *                Errors.properties. 
    * @param dbObjectMessage The error message from the database object. 
    * @param servletPath The path to use on the back button on the error
    *                    page. 
    * @param dbOperationOk Tells if the database operation was ok or not. 
    * @return True if everyting is ok.
    *         False if anything goes wrong.
    */
   protected boolean commitOrRollback(Connection connection,
                                      HttpServletRequest request,
                                      HttpServletResponse response,
                                      String rootKey,
                                      String dbObjectMessage,
                                      String servletPath,
                                      boolean dbOperationOk)
   {
      boolean isOk = true;
      try
      {
         // If everything is OK, commit the transaction
         if (dbOperationOk)
         {
            connection.commit();
            Errors.logDebug("commitOrRollback: commit");
         }

         // Something is wrong, rollback and write error page
         else
         {
            // Ensure connection is initialized before use
            if (connection != null)
            {
               connection.rollback();
            }
            Errors.logDebug("commitOrRollback: rollback");
            // Write the error page
            writeErrorPage(request, response, rootKey, dbObjectMessage,
                           servletPath + "?");
         }
      }
      catch (Exception e)
      {
         isOk = false;
         e.printStackTrace(System.err);
      }
      return isOk;
   }


   /**
    * Writes the statistical page. If a backstring is given, a back button
    * is written on the page.
    *
    * @param out The PrintWriter to write to.
    * @param generalData The general data.
    * @param statisticData The statistical data.
    * @param backString The string to use as event for the back button. 
    */
   protected static void writeStatisticsPage(PrintWriter out,
                                          String[][] generalData,
                                          String[][] statisticData,
                                          String backString)
   {
      // Write the start of the page
      HTMLWriter.doctype(out);
      HTMLWriter.openHEAD(out, "");
      HTMLWriter.defaultCSS(out);
      HTMLWriter.closeHEAD(out);
      
      // Open body, write header table and start content table
      HTMLWriter.openBODY(out, "");
      HTMLWriter.headerTable(out, 0,
                             Errors.keyValue("Projects.Statistics"));
      HTMLWriter.contentTableStart(out, 0);

      // Start general table and write the general data.
      out.println("<H3>General data</H3>");
      out.println("<TABLE border=0 width=400>");
      for (int row = 0; row < generalData.length; row++)
      {
         out.println("<TR>\n" +
                     "  <TD width=125>" + generalData[row][0] + ":</TD>\n" +
                     "  <TD>" + generalData[row][1] + "</TD>\n" +
                     "</TR>");
      }
      out.println("</TABLE>");
      out.println("\n<HR>\n");

      // Start statistical table and write statistics data.
      out.println("<H3>Statistics</H3>");
      out.println("<TABLE border=0>");
      for (int row = 0; row < statisticData.length; row++)
      {
         out.println("<TR>\n" +
                     "  <TD width=125>" + statisticData[row][0] + ":</TD>\n" +
                     "  <TD>" + statisticData[row][1] + "</TD>\n" +
                     "</TR>");
      }
      out.println("</TABLE>");

      // If back string is given, write a back button
      if (backString != null && backString != "")
      {
         out.println("<FORM action=\"\">\n  " +
                     HTMLWriter.backButton(backString) + "\n" +
                     "</FORM>");
      }
      
      // Finish the page.
      HTMLWriter.contentTableEnd(out);
      HTMLWriter.closeBODY(out);
      HTMLWriter.closeHTML(out);
   }

   
   /**
    * A simple method used to send a redirect-response to client.
    * The redirect will include the name of the servlet to redirect to ("redirectClass")
    * and the path to that servlet given by the init-argumet "redirectPath."
    *
    * @param res HttpServletResponse that encapsulates the response
    * from the servlet
    *
    * @exception IOException if detected when handling the request
    * @exception ServletException if the request could not be handled
    *
    * @see javax.servlet.http#HttpServletResponse
    */
   protected void redirect(HttpServletResponse res)
      throws ServletException, IOException
   {

      res.sendRedirect(m_redirectPath+"redirectClass");
   }


   /**
    * @param session
    * @param inputString
    * @param maxLength
    * @return  */   
   protected String formatOutput(HttpSession session, String inputString,
                                 int maxLength)
   {
      String output = null;

      if (inputString == null || inputString.trim().equalsIgnoreCase(""))
      {
         output=getNullReplacement(session);
         if (output==null || output.trim().equalsIgnoreCase(""))
         {
            output = "&nbsp;&nbsp;";
         }
      }
      else
      {
         if (inputString.length() <= maxLength)
         {
            output = inputString;
         }
         else
         {
            output = inputString.substring(0,maxLength-2) +"..";
         }
      }
      return output;
   }


   /**
    * @param in
    * @param nullSubst
    * @return  */   
   protected String replaceNull(String in, String nullSubst)
   {
      return (in != null ? in : nullSubst);
   }


   /**
    * @return  */   
   protected String getDateValidationScript()
   {
      String output = "<SCRIPT language=\"JavaScript\">\n" +
         "<!--\n" +
         "var valid = '0123456789';\n" +
         "\n" +
         "function isValid(string,allowed) {\n" +
         "  for (var i=0; i< string.length; i++) {\n" +
         "    if (allowed.indexOf(string.charAt(i)) == -1)\n" +
         "      return false;\n" +
         "  }\n" +
         "  return true;\n" +
         "}\n" +
         "\n" +
         "//-->\n" +
         "</SCRIPT>\n" +
         "\n" +
         "\n" +
         "\n" +
         "<script language=\"javaScript\">\n" +
         "function valDate(control, allowEmpty) {\n" +
         "  var value = trim(control.value);\n" +
         "  var length;\n" +
         "  var year;\n" +
         "  var month;\n" +
         "  var day;\n" +
         "  var hour;\n" +
         "  var minutes;\n" +
         "  control.value = trim(control.value);\n" +
         "  length = control.value.length;\n" +
         "  if (allowEmpty && length == 0)\n" +
         "    return true;\n" +
         "  // The date must be in the format YYYY-MM-DD[ HH:MI]\n" +
         "  if (length != 10 && length != 16) {\n" +
         "    displayError('Date must be specified as \\'YYYY-MM-DD\\' or as\\n' +\n" +
         "          '\\'YYYY-MM-DD HH:MI\\', where HH is the hour given as\\n' +\n" +
         "          'a number between 0 and 23 and MI is the minutes.');\n" +
         "    return false;\n" +
         "  }\n" +
         "  if ((control.value.charAt(4) != '-' || control.value.charAt(7) != '-') ||\n" +
         "      (length == 16 && control.value.charAt(13) != ':') ) {\n" +
         "    displayError('Date must be specified as \\'YYYY-MM-DD\\' or as\\n' +\n" +
         "          '\\'YYYY-MM-DD HH:MI\\', where HH is the hour given as\\n' +\n" +
         "          'a number between 0 and 23 and MI is the minutes.');\n" +
         "    return false;\n" +
         "  }\n" +
         "\n" +
         "  year = control.value.substring(0, 4);\n" +
         "  if (!checkDigits(year, 1900, 2102) ) {\n" +
         "    displayError('Year must be in the range 1900 to 2102.');\n" +
         "    return false;\n" +
         "  }\n" +
         "  month = control.value.substring(5, 7);\n" +
         "  if (!checkDigits(month, 1, 12) ) {\n" +
         "    displayError('Month must be in the range 01 to 12.');\n" +
         "    return false;\n" +
         "  }\n" +
         "  day = control.value.substring(8, 10);\n" +
         "  if (!checkDigits(day, 1, 31) ) {\n" +
         "    displayError('Day must be in the range 01 to 31.');\n" +
         "    return false;\n" +
         "  }\n" +
         "\n" +
         "  if (length == 16) {\n" +
         "    hour = control.value.substring(11, 13);\n" +
         "    if (!checkDigits(hour, 00, 23) ) {\n" +
         "      displayError('Hour must be in the range 00 to 23.');\n" +
         "      return false;\n" +
         "    }\n" +
         "    minutes = control.value.substring(14, 16);\n" +
         "    if (!checkDigits(minutes, 00, 59)) {\n" +
         "      displayError('Minutes must be in the range 00 to 59.');\n" +
         "      return false;\n" +
         "    }\n" +
         "  }\n" +
         "\n" +
         "  return true;\n" +
         "}\n" +
         "function checkDigits(num, min, max) {\n" +
         "  var ret = true;\n" +
         "  // First we make sure that the variable num only contains digits\n" +
         "  if (!isValid(num, valid) ) {\n" +
         "    ret = false;\n" +
         "  }\n" +
         "  if (max < num || num < min)\n" +
         "    ret = false;\n" +
         "  return ret;\n" +
         "}\n" +
         "\n" +
         "function displayError(str) {\n" +
         "   alert(str);\n" +
         "}\n" +
         "</script>\n" +
         "<SCRIPT LANGUAGE=\"JavaScript\">\n" +
         "<!--\n" +
         "function trim(str) {\n" +
         "  while (str.substring(0,1) == ' ') str = str.substring(1);\n" +
         "  while (str.substring(str.length-1,str.length) == ' ') str = str.substring(0,str.length-1);\n" +
         "  return str;\n" +
         "}\n" +
         "//-->\n" +
         "</SCRIPT>\n" +
         "\n";
      return output;

   }
   
   /** This method saves the URL for later use the pressing
    * "back" buttons. Method getPrevURL gets the saved value for use in
    * links and buttons.
    *
    * This also have a migration function from the old way of adding attribute
    * "RETURNING=YES" to the QS. If attribute is set, redirect to the last
    * known URL. A faster way is to use the method "getPrevURL" directly on
    * the "back" button.
    * @param req The request object
    *
    * @param res The response object.
    *
    * @throws IOException Throws IOException
    *
    * @return Returns a servlet response object to use for redirection.
    */
   protected HttpServletResponse checkRedirectStatus(HttpServletRequest req,HttpServletResponse res)
        throws IOException
   {
       String tmp = null;
       tmp = req.getParameter("RETURNING");
       
       if (tmp == null)
           tmp = "NO";
       
       if (tmp.equals("YES"))
       {
           //Redirect the user to the last known URL.
           Errors.logInfo("Redirecting="+req.getSession().getAttribute("prevRequest"));
           res.sendRedirect((String)req.getSession().getAttribute("prevRequest"));
       }
       else
       {
           // Save the url for possible redirection later.
           Errors.logInfo("Saving url="+req.getRequestURI()+"?"+req.getQueryString());
           req.getSession().setAttribute("prevRequest",req.getRequestURI()+"?"+req.getQueryString());
       }
       
       return res;
   }
   
   /** Return the last known URL. Use this method for buttons and links for
    * abort/back purposes.
    * @return Returns a String value for the latest known URL
    * @param req The HttpServletRequest object as input value.
    */
   protected String getPrevURL(HttpServletRequest req)
   {
       return (String)req.getSession().getAttribute("prevRequest");
   }

   /**
    * Sets the sampling unit id of the session object and returns a value
    * indicating whether the sampling unit id was changed or not. If the
    * query string of the given request contains a suid, the session object
    * is assigned this value. If no suid is found in the query string, the
    * suid in the session object is reused. If no suid found in neihter the
    * session or the request object, a default value is used.
    *
    * @param request a HttpServletRequest value
    * @return True if suid has changed
    *         False if we are reusing a suid from session object.
    */
   protected boolean setSessionSuid(HttpServletRequest request)
   {
      HttpSession session = request.getSession(true);

      // Get suids from session and request
      String sessionSuid = (String) session.getAttribute("SUID");
      String requestSuid = request.getParameter("suid");

      // Indicates if the suid is changed. 
      boolean suidHasChanged = true;
      
      // The suid value to use. Initialize with default value.
      String newSuid = Defaults.DEFAULT_SUID;
      
      // If there is a suid in the request, use it
      if (Utils.assigned(requestSuid) && !Utils.blank(requestSuid))
      {
         newSuid = requestSuid;

         // If the request suid is the same as in the session, the suid has
         // not really changed
         if (requestSuid.equals(sessionSuid))
         {
            suidHasChanged = false;
         }
       }

      // if there is a suid in the session, use it. As we are reusing the
      // suid, the suid has not changed
      else if (Utils.assigned(sessionSuid) && !Utils.blank(sessionSuid))
      {
         newSuid = sessionSuid;
         suidHasChanged = false;
      }

      // Set the suid of the session and exit
      session.putValue("SUID", newSuid);
      return suidHasChanged;
   }


   /**
    * Returns a value for the action parameter based on the given request
    * and given su state. If the sampling unit did changed, a default value
    * is used for the action parameter. If the suid did not change and
    * there is a action parameter in the request, reuse that value.
    *
    * @param request The request from client.
    * @param suidHasChanged Indicates wheter the suid has changed or not. 
    * @return A valid value for the action parameter
    */
   protected String getAction(HttpServletRequest request,
                              boolean suidHasChanged)
   {
      // The current action. Initialize to default value.
      String action = Defaults.DEFAULT_ACTION;
            
      // If the suid has not changed, try to read the old action parameter
      // from the request
      if (!suidHasChanged)
      {
         String oldAction = request.getParameter("ACTION");

         // If value found and it is not blank, us this value as return
         // value 
         if (Utils.assigned(oldAction) && !Utils.blank(oldAction))  
         {
            action = oldAction;
         }
      }

      return action;
   }


   /**
    * Returns a valid value for the orderby parameter based on the given
    * request object and the given suid state. If the suid has changed, a
    * default value is returned. If the suid has not changed, and there is
    * a value in the request object, that value is returned.
    *
    * @param request The request from client.
    * @param suidHasChanged Indicates whether the suid did change or not. 
    * @return A valid orderby value
    */
   protected String getOrderBy(HttpServletRequest request,
                               boolean suidHasChanged)
   {
      // Get the default value
      String orderBy = Defaults.DEFAULT_ORDERBY;

      // If suid has not changed, try to read the old orderBy value 
      if (!suidHasChanged)
      {
         String oldOrderBy = request.getParameter("ORDERBY");

         // If value found and it is not blank, use this value as return
         // value 
         if (Utils.assigned(oldOrderBy) && !Utils.blank(oldOrderBy))
         {
            orderBy = oldOrderBy;
         }
      }
      return orderBy;
   }
   

   /**
    * Returns a valid startindex parameter based on the state of the suid,
    * gsid, the request, the action and the number of elements. If any of
    * suid or gsid has changed, a default value is returned. If none is
    * changed, the startIndex is read from the request. Then the startIndex
    * value is recalculated depending on the value of the action
    * parameter. If startIndex is found but action is blank, the found 
    * startIndex value is reused. If no startIndex value is found, a
    * default value will be used.
    *
    * @param request The request from the client.
    * @param elements Total number of elements to display.
    * @param suidHasChanged Indicates whethere the suid has changed or not.
    * @param gsidHasChanged Indicates whethere the gsid has changed or not.
    * @param action The current action.
    * @return A valid value for the action parameter.
    */
   protected String getStartIndex(HttpServletRequest request,
                                  int elements, 
                                  boolean suidHasChanged,
                                  boolean gsidHasChanged,
                                  String action)
   {
      HttpSession session = request.getSession(true);

      // The startIndex to use. Initialize with default value.
      String newStartIndex = Defaults.DEFAULT_STARTINDEX; 

      // If suid  and gsid has not changed, we might modify the start
      // index. If any id did change, ignore this
      if (!suidHasChanged && !gsidHasChanged)
      {
         
         // Get the startIndex parameter from the request. 
         String requestStartIndex = request.getParameter("STARTINDEX");
         if (Utils.assigned(requestStartIndex) &&
             !Utils.blank(requestStartIndex)) 
         {
            // startIndex was found. Convert it to integer to make it easier
            // to handle.
            int startIndex = Integer.parseInt(requestStartIndex);
         
            // Ensure the action parameter is given
            if (Utils.assigned(action) && !Utils.blank(action))
            {
               // Get the preferred number of rows per page
               int maxRows = getMaxRows(session); 

               /*
                * It might be necessary to adjust the startIndex depending on
                * the action. startIndex is adjusted when action is
                * Servlet.Action.First
                * Servlet.Action.Prev
                * Servlet.Action.Next
                * Servlet.Action.Last
                * Servlet.Action.Display
                */
            
               // If action is "first", set startIndex to 1
               if (action.equals(Defaults.ACTION_FIRST))
               {
                  startIndex = 1;
               }
            
               // If action is "previous", decrease current startIndex with the
               // number of rows displayed per page. If startIndex gets too low
               // there are too few items to display so start on item 1 instead.
               else if (action.equals(Defaults.ACTION_PREV))
               {
                  startIndex -= maxRows;
                  if (startIndex < 1)
                  {
                     startIndex = 1;
                  }
               }
            
               // If action is "next", increase current startIndex with the number
               // of rows displayed per page. If startIndex gets higher than
               // there are items to display, use old startIndex.
               else if (action.equals(Defaults.ACTION_NEXT))
               {
                  startIndex += maxRows;
                  if (startIndex > elements)
                  {
                     startIndex -= maxRows;
                  }
               }
            
               // If action is "last", calculate the startIndex.
               else if (action.equals(Defaults.ACTION_LAST))
               {
                  // First calculate number of required pages and any rest
                  int pages = elements / maxRows;
                  int rest = elements % maxRows;

                  // If at least one page and no rest
                  if (pages > 0 && rest == 0)
                  {
                     startIndex = ((pages - 1) * maxRows) + 1;
                  }

                  // If at least one page and some rest
                  else if (pages > 0 && rest != 0)
                  {
                     startIndex = (pages * maxRows) + 1;
                  }
               }

               // If action is "display", always use start index 1
               else if(action.equals(Defaults.ACTION_DISPLAY))
               {
                  startIndex = 1;
               }
               newStartIndex = String.valueOf(startIndex);
            }
         }
      }
            
      return newStartIndex;
   }


   
 
   
   /**
    * Returns a string with single "'". Cannot handle "null" strings
    */
   protected String retrieveSymbol(String str)
   {
       str.trim();         
       return retrieveSym(str);
   }


   //////////////////////////////////////////////////////////////////////
   //
   // Private section
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Sets the version of the application.
    *
    * @param version The version of the application.
    */
   private void applicationVersion(String version)
   {
      mApplicationVersion = version;
   }
   
   
    private String retrieveSym(String searchString)
   {
      int index = 0; 
      int sym = 0;
      String StrOut = "";
      
      String symbol[] = {"#","'", "&"};
      String replace[] = {"¤!", "¤%","¤£"};
      int no = symbol.length; //number of symbols
      
       //account for the symbols in the parameter 
      for (sym=0; sym < no; sym++){
        for (index = searchString.indexOf(replace[sym]); index != -1; index = searchString.indexOf(replace[sym])) {           
      
            // Copy up to the symbol 
                StrOut += searchString.substring(0, index);
                StrOut += symbol[sym];        
                searchString = searchString.substring(index + 2); 
            } 
            StrOut += searchString; 
            searchString = "";
            searchString = StrOut;
            StrOut = "";
            index=0;
      }
       return searchString; 
   }
   
   
   
   /**
    * Deletes the file system object pointed out by the given string. 
    *
    * @param deletePath The path to the object to remove.
    * @return True if object was removed.
    *         False if object was not removed.
    */
   protected boolean deleteFileObject(String deletePath)
   {
      try
      {
         // Ensure that the path to object to delete contains the path to
         // the directory with generated files. If not, we are trying to
         // delete something which is not a generated file which is an
         // error.
         if  ((deletePath.indexOf(getFileGeneratePath()) < 0)
	 	&& (deletePath.indexOf(getUpFilePath()) < 0))
         {
            throw new Exception("Delete path (" + deletePath +
                                ") does not contain the path to the " +
                                "directory with generated files (" +
                                getFileGeneratePath() + ").");
         }

         // Build a file object based on the delete path and check its
         // existence. If it does not exist, exit with true to indicate the
         // item is deleted.
         File fileSystemObject = new File(deletePath);
         if (!fileSystemObject.exists())
         {
            return true;
         }
         
         // If file object is a directory
         if (fileSystemObject.isDirectory())
         {
            // Get the contents of the directory and call deleteFileObject
            // recursively for each item in the directory. If any item can
            // not be deleted, we will bail out.
            String[] directoryItems = fileSystemObject.list();
            for (int i = 0; i < directoryItems.length; i++) 
            {
               Assertion.assertMsg(deleteFileObject(deletePath + "/" +
                                          directoryItems[i]),
                                "Failed to delete directory item " +
                                deletePath + "/" + directoryItems[i]);
            }
         }
         
         // Try to delete the file system object itself
         Assertion.assertMsg(fileSystemObject.delete(),
                          "Failed to delete the file system object " +
                          fileSystemObject.getPath());
      }
      catch (Exception e)
      {
         e.printStackTrace(System.err);
         return false;
      }
      return true;
   }
   
    /** Create a directory if it does not exist
     * @param path The path (directory) to create
     * @return Return true or false if the operation succeded.
     *
     */
    protected boolean createPath(String path)
    {
        File file = null;
        boolean ret = true;
        try 
        {
            file = new File(path);
            if (!file.isDirectory()) 
            {
                if (!file.mkdirs())
                    ret = false;
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            ret = false;
        }
        return ret;
    }
}
