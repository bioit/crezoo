/*
 * @(#)ArexisServlet.java	1.0 2000-10-05
 *
 * Copyright (c) Prevas AB. All Rights Reserved.
 *
 * CopyrightVersion 1.0
 */

package se.arexis.agdb.servlet;

import javax.servlet.http.*;
import java.io.*;
import javax.servlet.*;

/**
 * An abstract class that provides session control for the non administrative servlets
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

//2003-11-26, liwa, added RES_R, RES_W, CTG_R, CTG_W, RTYPE_R, RTYPE_W.
public abstract class SecureArexisServlet extends ArexisServlet {

  // Defined values used for privilege control.
  final protected int PROJECT_ADM = 1;
  final protected int PROJECT_STA = 2;
  /*
  * NOTE:
  * The privileges GENO_W0 to GENO_W9 must be
  * defined as in a serie, where GENO_W0 has
  * the lowest, GENO_W9 the highest and there are
  * no holes.
  */
  final protected int SU_W = 3;
  final protected int SU_R = 4;
  final protected int GRP_W = 5;
  final protected int GRP_R = 6;
  final protected int IND_W = 7;
  final protected int IND_R = 8;
  final protected int VAR_W = 9;
  final protected int VAR_R = 10;
  final protected int VARS_W = 11;
  final protected int VARS_R = 12;
  final protected int UVAR_W = 13;
  final protected int UVAR_R = 14;
  final protected int UVARS_W = 15;
  final protected int UVARS_R =16;
  final protected int PHENO_W = 17;
  final protected int PHENO_R = 18;
  final protected int MRK_W = 19;
  final protected int MRK_R = 20;
  final protected int LMRK_R = 21;
  final protected int MRKS_W = 22;
  final protected int MRKS_R = 23;
  final protected int UMRK_W = 24;
  final protected int UMRK_R = 25;
  final protected int UMRKS_W = 26;
  final protected int UMRKS_R = 27;
  final protected int GENO_W0 = 28;
  final protected int GENO_W1 = 29;
  final protected int GENO_W2 = 30;
  final protected int GENO_W3 = 31;
  final protected int GENO_W4 = 32;
  final protected int GENO_W5 = 33;
  final protected int GENO_W6 = 34;
  final protected int GENO_W7 = 35;
  final protected int GENO_W8 = 36;
  final protected int GENO_W9 = 37;
  final protected int GENO_R = 38;
  final protected int FLT_W = 39;
  final protected int FLT_R = 40;
  final protected int ANA_W = 41;
  final protected int ANA_R = 42;
  final protected int RES_R = 43;
  final protected int RES_W = 44;
  final protected int CTG_R = 45;
  final protected int CTG_W = 46;
  final protected int RTYPE_R = 47;
  final protected int RTYPE_W = 48;
  

/*final protected int ;
  final protected int ;
  final protected int ;
  final protected int ;
  final protected int ;
  final protected int ;
  final protected int ;
  final protected int ;
  final protected int ;
*/

  /**
   * Constructor
   */

  public SecureArexisServlet() {
  }
  /**
   * This is the ArexisServlet-specific version of the
   * <code>HttpServlet.service</code> method, which verifies that the request
   * comes from a client with the necessary session data. If the client
   * passes the test the super class (i.e. HttpServlet) service method is
   * called. Otherwise the <code>service</code> method response is a
   * redirection to "../redierctClass
   *
   * @param req HttpServletRequest that encapsulates the request to
   * the servlet
   * @param resp HttpServletResponse that encapsulates the response
   * from the servlet
   * @exception IOException if detected when handling the request
   * @exception ServletException if the request could not be handled
   *
   * @see javax.servlet.Servlet#service
   */
  protected void service (HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException 
  {
    HttpSession session = req.getSession(true);
    Boolean loginOk = (Boolean) session.getValue("LoginOk");

    if(checkMissingData()  == true)
    {
      res.sendError(res.SC_SERVICE_UNAVAILABLE);
    }
    String scheme = req.getScheme();
  //  System.err.println("Secure Arexis Servlet: scheme="+scheme);

    if (!scheme.equalsIgnoreCase("https")){
      redirect(res); // should redirect to warning page?
    }

    // check if user has logged in properly
   // System.err.println("Secure Arexis Servlet:login="+loginOk.booleanValue());
    if(loginOk == null || loginOk.booleanValue() != true )
    {
           redirect(res);
    }
    else {
      super.service(req, res);
    }
  }


  /**
  * A function that returns one string (out of two possible) to be written to the HTML-page
  * which string to retur is decided by a comparison of the users privileges to
  * the privileges required.
  */

  protected String privDependentString(int owned[], int required, String ifTrue, String ifFalse)
  {
      for(int i=0; i<owned.length; i++)
      {
        if (owned[i]== required)
        {
          return ifTrue;
        }
      }
      return ifFalse;
  }
}
