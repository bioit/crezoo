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
 * An abstract class that provides session control for the Adminitration servlets
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
public abstract class AdminArexisServlet extends ArexisServlet {

  /**
   * Constructor
   */

  public AdminArexisServlet() {
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
    throws ServletException, IOException {


    if(checkMissingData()  == true)
    {
      res.sendError(res.SC_SERVICE_UNAVAILABLE);
    }
    String scheme =req.getScheme();
    if (!scheme.equalsIgnoreCase("https")){
        	  redirect(res); // should redirect to warning page?
    }


	  HttpSession session = req.getSession(true);

		String superuser = (String) session.getValue("superuser");
		// Make sure this is a super user
    	if (superuser == null || superuser.trim().equals("") ){
			  redirect(res);
	   } else {
      super.service(req, res);
    }
  }


}
