/*
 * @(#)loginAction.java	1.0 2000-10-09
 *
 * Copyright (c) Prevas AB. All Rights Reserved.
 *
 * CopyrightVersion 1.0
 */

package se.arexis.agdb.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

import se.arexis.agdb.util.Errors;

//import oracle.jdbc.driver.*;
/**
 *
 * Servlet to administrate login to the Arexis zone.
 *
 * @version 1.0, 2000-10-09
 */
public class loginAction extends HttpServlet 
{
    String dburl;
    String uid;
    String pwd;
    String superuid;
    String superpwd;
    String redirectPath;
    
    // Temporary error code for debugging
    // TH
    String errCode = "notdefined";



    /**
     * Recieves request sent by the login.html-page.
     * Certain init-parameters are used to establish a database connection.
     * An attempt is made to match the username and password provided
     * by the request object to a stored username and password in the database-view
     * "V_USERS_1".
     * If a match is found, a redirect to the servlet implementing mainPage is made.
     * (unless the match also matches an adminitrator role, in which case the
     * redirect goes to the "adminMain" servlet)
     * If no match is found, a redirect response to the "redirectClass" is made.
     *
     * Overrides <code>HttpServlet.doGet</code> method.
     *
     * @param req HttpServletRequest that encapsulates the request to
     * the servlet
     * @param resp HttpServletResponse that encapsulates the response
     * from the servlet
     * @exception IOException if detected when handling the request
     * @exception ServletException if the request could not be handled
     *
     * @see javax.servlet.http.HttpServlet#doGet
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
						throws ServletException, IOException 
    {
        Errors.logInfo("loginAction.doPost(req,res) started");
        String strUser;
        String strPasswd;
        String strDriver;
        String pid = null;
        String id = null;
        int privileges[] = null;
        int index;
        boolean noUSRfound=false;
            
        // First we retrive information from our init args
        ServletContext conf = this.getServletContext();

        this.dburl = conf.getInitParameter("dburl");
        this.uid = conf.getInitParameter("uid");
        this.pwd = conf.getInitParameter("pwd");
        this.superuid = conf.getInitParameter("superuid");
        this.superpwd = conf.getInitParameter("superpwd");
        this.redirectPath = conf.getInitParameter("redirectPath");
        strDriver = conf.getInitParameter("driver");
        Errors.setLogFile(conf.getInitParameter("logfile"));

        if (dburl == null || uid == null ||
            pwd == null || superuid == null ||
            superpwd == null || redirectPath == null) 
        {
            // Missing information
            Errors.logFatal("loginAction.doPost(...): Missing data in init file.");
            res.sendError(res.SC_SERVICE_UNAVAILABLE);
            return;
        }

	Connection conn = null;
	Statement stmt = null;
	ResultSet rset = null;
	strUser = req.getParameter("uid");
	strPasswd = req.getParameter("pwd");
        
        //System.err.println("loginaction:params in="+strUser+","+strPasswd);
	try 
        {
            HttpSession session = req.getSession(true);
            //DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            
            //DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            
            Class.forName(strDriver);
            
            conn = DriverManager.getConnection(this.dburl, this.uid, this.pwd);
            //System.err.println("Conn="+ conn);
            
            if (conn == null) 
                errCode = "connNull";
            else
                errCode = "connNotNull";

            // Check if this is the super user
            if (strUser.equalsIgnoreCase(this.superuid) &&
                strPasswd.equals(this.superpwd)) 
            {
                session.setAttribute("conn", conn);
                session.setAttribute("superuser", "superuser");
		res.sendRedirect("adminMain");
		return;
            }

            stmt = conn.createStatement();
            String strSQL;
            //strSQL = "SELECT ID, NAME FROM gdbadm.V_USERS_1 WHERE upper(USR)='" + strUser.toUpperCase() + "' AND PWD='" + strPasswd + "'";
            strSQL = "SELECT ID, UNAME, RNAME, PNAME, PID FROM V_ENABLED_USERS_2 WHERE upper(USR)='" +
            strUser.toUpperCase() + "' AND PWD='" +
            strPasswd + "' ORDER BY PNAME";
            rset = stmt.executeQuery(strSQL);

            if (rset.next()) 
            {
                pid = rset.getString("PID");
                id = rset.getString("ID");
		session.setAttribute("UserID", id);
		session.setAttribute("UserName", rset.getString("UNAME"));
                session.setAttribute("UserSign", strUser);
                session.setAttribute("PID", pid);
                session.setAttribute("PNAME", rset.getString("PNAME"));
                session.setAttribute("ROLE", rset.getString("RNAME"));
		Boolean bLoginOk = new Boolean(true);
		session.setAttribute("LoginOk", bLoginOk);
                // Some servlets also check this session object
                session.setAttribute("projSet", bLoginOk);
                
                // Log 
                
                Errors.log("User ["+strUser+"] logged in.");

                // read and store the privileges associated with this role in the
                // session object privileges.
                rset.close();
                stmt.close();
                stmt = conn.createStatement();
                rset = stmt.executeQuery("SELECT COUNT(*) FROM V_USER_PRIV " +
                    "WHERE PID=" +pid + " AND ID=" +id);
                if(rset.next())
                    privileges = new int[rset.getInt(1)];
                else
                    throw new Exception("");

		rset.close();
		stmt.close();

                stmt = conn.createStatement();
                rset = stmt.executeQuery("SELECT RNAME, PRID FROM V_USER_PRIV " +
                    "WHERE PID=" +pid+" AND ID=" +id);
                index = 0;
                while (rset.next())
                {
                    privileges[index]=rset.getInt("PRID");
                    index++;
                }
                session.setAttribute("PRIVILEGES",privileges);
		rset.close();
		stmt.close();

                //session.setAttribute("MaxRows", "50"); // Maximum number of rows to display
		session.setAttribute("conn", conn);
		res.sendRedirect("mainPage?PAGE=SESSION");

            } 
            else 
            {
                Boolean bLoginOk = new Boolean(false);
                session.setAttribute("LoginOk", bLoginOk);
                noUSRfound=true;
                throw new Exception("");
                //res.sendRedirect(redirectPath+"redirectClass");
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
            // parametrar
            // om conn
            // om felaktiga credentials

            //res.sendRedirect(redirectPath+"redirectClass");

            // no connection?
            if (conn==null)
            {
                res.sendRedirect(redirectPath+"loginError?error=dbase&mess="+e.toString());
            }
            // wrong credentials
            else if (noUSRfound==true)
            {
                res.sendRedirect(redirectPath+"loginError?error=noUSR");
            }
            else
            {
                res.sendRedirect(redirectPath+"loginError?error=undef");
            }
        } 
        finally 
        {
            try 
            {
                if (rset != null) rset.close();
                if (stmt != null) stmt.close();
            } 
            catch (SQLException ignored) {}
        }
        Errors.logInfo("loginAction.doPost(req,res) ended");
    }
}

